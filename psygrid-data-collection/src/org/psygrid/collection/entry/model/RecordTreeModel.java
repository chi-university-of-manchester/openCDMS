/*
Copyright (c) 2006-2008, The University of Manchester, UK.

This file is part of PsyGrid.

PsyGrid is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as 
published by the Free Software Foundation, either version 3 of 
the License, or (at your option) any later version.

PsyGrid is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public 
License along with PsyGrid.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.psygrid.collection.entry.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.psygrid.collection.entry.DocumentStatus;
import org.psygrid.collection.entry.ExceptionsHelper;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.collection.entry.remote.RemoteManager;
import org.psygrid.data.model.hibernate.*;


/**
 * Implements an TreeModel for IRecord with support for filtering by document
 * status. Aside from {@code statusFilter} this model is immutable. This model
 * represents the IRecord as a hierarchy with 3 levels. The root is the record,
 * the children are document groups and the grandchildren are document occurrences/
 * document instances.
 * 
 * @see RecordView
 * @see #statusFilter
 */
public class RecordTreeModel implements TreeModel {

    private final Record record;
    private final List<TreeModelListener> listeners = new CopyOnWriteArrayList<TreeModelListener>();
    
    /**
     * Only documents with this status are displayed. If {@code null}, all
     * documents are shown.
     */
    private DocumentStatus statusFilter;
    
    public RecordTreeModel(Record record) {
        this.record = record;
    }
    
    public void addTreeModelListener(TreeModelListener l) {
        listeners.add(l);
    }
    
    public void removeTreeModelListener(TreeModelListener l) {
        listeners.remove(l);
    }
    
    /**
     * Fires a structure changed event with root as the path. 
     */
    private void fireTreeModelEvent() {
        for (TreeModelListener l : listeners)
            l.treeStructureChanged(new TreeModelEvent(this, new TreePath(getRoot())));
    }
    
    private Object getParent(Object child) {
        if (child instanceof DocumentOccurrence)
            return ((DocumentOccurrence) child).getDocumentGroup();
        else if (child instanceof DocumentGroup)
            return record;
        else
            throw unknownObjectType(child);
    }
    
    public TreePath getPathToRoot(Object child) {
        List<Object> pathElements = new ArrayList<Object>();
        Object node = child;
        while (!node.equals(getRoot())) {
            pathElements.add(node);
            node = getParent(node);
        }
        pathElements.add(getRoot());
        Collections.reverse(pathElements);
        return new TreePath(pathElements.toArray());
    }

    public Object getChild(Object parent, int index) {
        if (parent instanceof Record)
            return ((Record) parent).getDataSet().getDocumentGroup(index);
        if (parent instanceof DocumentGroup)
            return getDocumentOccurrence((DocumentGroup) parent, index);
        throw unknownObjectType(parent);
    }

    private AssertionError unknownObjectType(Object parent) {
        return new AssertionError("Unknown object type: " + parent.getClass()); //$NON-NLS-1$
    }

    private DocumentOccurrence getDocumentOccurrence(DocumentGroup parent, int index) {
        return getDocOccurrences(parent).get(index);
    }

    public final List<DocumentOccurrence> getDocOccurrences(DocumentGroup parent) {
        List<DocumentOccurrence> docOccurrences = new ArrayList<DocumentOccurrence>();
        DataSet ds = null;
        
        //in preview mode, get the dataset directly from the record
        if (RemoteManager.getInstance().isTestDataset()) {
        	ds = record.getDataSet();
        } else {
            try {
    			ds = PersistenceManager.getInstance().getData().getDataSetSummary(record.getIdentifier().getProjectPrefix()).getCompleteDataSet();
    		}
    		catch (IOException ioe) {
    			ExceptionsHelper.handleIOException(null, ioe, false);
    		}
        }
        
		if (ds == null) {
			ExceptionsHelper.handleException(null, "No project found", null, "No project found for the record.", false);
		}
        for (int i = 0, c = ds.numDocuments(); i < c; ++i) {
            Document document = ds.getDocument(i);
            for (int j = 0, d = document.numOccurrences(); j < d; ++j) {
                DocumentOccurrence docOcc = document.getOccurrence(j);
                if (docOcc.getDocumentGroup().equals(parent) && matchesFilter(docOcc))
                    docOccurrences.add(docOcc);
            }
        }
        return docOccurrences;
    }

    private boolean matchesFilter(DocumentOccurrence docOcc) {
        if (statusFilter == null)
            return true;
        DocumentStatus status = getStatus(docOcc);
        return status.equals(statusFilter);
    }

    private DocumentStatus getStatus(DocumentOccurrence docOcc) {
        synchronized (PersistenceManager.getInstance()) {
            return PersistenceManager.getInstance().getRecordStatusMap()
                    .getDocumentStatus(record, docOcc);
        }
    }

    public int getChildCount(Object parent) {
        if (parent instanceof Record)
            return ((Record) parent).getDataSet().numDocumentGroups();
        if (parent instanceof DocumentGroup)
            return getDocOccurrences((DocumentGroup) parent).size();
        if (parent instanceof DocumentOccurrence)
            return 0;
        throw unknownObjectType(parent);
    }
    
    private int getIndex(DocumentGroup docGroup) {
        DataSet dataSet = record.getDataSet();
        for (int i = 0; i < dataSet.numDocumentGroups(); i++) {
            if (dataSet.getDocumentGroup(i).equals(docGroup))
                return i;
        }
        return -1;
    }
    
    public int getIndexOfChild(Object parent, Object child) {
        if (parent == null || child == null)
            return -1;
        
        if (child instanceof DocumentGroup)
            return getIndex((DocumentGroup) child);
        if (child instanceof DocumentOccurrence)
            return getDocOccurrences((DocumentGroup) parent).indexOf(child);
        throw unknownObjectType(parent);
    }

    public Record getRoot() {
        return record;
    }

    public boolean isLeaf(Object node) {
        return node instanceof DocumentOccurrence ||
            (node instanceof DocumentGroup && getChildCount(node) == 0);
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
        throw new UnsupportedOperationException("This TreeModel does not support mutation"); //$NON-NLS-1$
    }

    public void setStatusFilter(DocumentStatus status) {
        if (statusFilter == status)
            return;
        this.statusFilter = status;
        fireTreeModelEvent();
    }
}

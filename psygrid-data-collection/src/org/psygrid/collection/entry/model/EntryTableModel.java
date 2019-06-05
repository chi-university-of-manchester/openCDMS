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

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.EventListenerList;

import org.psygrid.collection.entry.Icons;
import org.psygrid.collection.entry.event.ApplyStdCodeToRowEvent;
import org.psygrid.collection.entry.event.ApplyStdCodeToRowListener;
import org.psygrid.collection.entry.event.EntryStatusEvent;
import org.psygrid.collection.entry.event.EntryStatusListener;
import org.psygrid.collection.entry.event.EntryTableModelEvent;
import org.psygrid.collection.entry.event.EntryTableModelListener;
import org.psygrid.collection.entry.renderer.RendererData.EditableStatus;
import org.psygrid.data.model.hibernate.*;

import com.jgoodies.binding.beans.PropertyAdapter;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.util.DefaultValidationResultModel;

public abstract class EntryTableModel implements CompositePresModel  {
        
    protected EventListenerList listenerList = new EventListenerList();

    protected CompositeEntry entry;

    protected CompositeResponse response;
    
    protected int numColumns;
    
    protected int numRows;
    
    private ValueModel entryStatusModel;
    
    private SectionPresModel sectionOccPresModel;
    
    private ValidationResultModel validationModel;
    
    /**
     * Each list inside the main list corresponds to a column in the composite.
     */
    protected List<List<BasicPresModel>> childPresModels;
    
    protected Map<Entry, List<BasicPresModel>> childPresModelsMap;
    
    private boolean copy;

    private final DocumentInstance docInstance;
    
    private final ValueModel responseStatusModel;
    
    private final List<ApplyStdCodeToRowAction> applyStdCodeToRowActions;

    private final List<StandardCode> standardCodes;

    public EntryTableModel(CompositeEntry entry, CompositeResponse response,
            SectionPresModel sectionOccPresModel, boolean copy, 
            DocumentInstance docInstance, List<StandardCode> standardCodes) {
        this.docInstance = docInstance;
        childPresModels = new ArrayList<List<BasicPresModel>>();
        childPresModelsMap = new HashMap<Entry, List<BasicPresModel>>();
        this.entry = entry;
        this.response = response;
        this.sectionOccPresModel = sectionOccPresModel;
        this.copy = copy;
        this.validationModel = new DefaultValidationResultModel();
        this.responseStatusModel = new PropertyAdapter(response, "status", true); //$NON-NLS-1$
        entryStatusModel = new ValueHolder(entry.getEntryStatus(), true);
        numRows = 0;
        numColumns = entry.numEntries();
        this.standardCodes = standardCodes;
        applyStdCodeToRowActions = new ArrayList<ApplyStdCodeToRowAction>();
        initEventHandling();
        
        /*
         * Fix for Bug #852 - Dependent composite not being repopulated.
         * 
         * This sets the status of all entries belonging to a composite 
         * to be the same as the composite. 
         */
        for (int i =0; i< entry.numEntries();i++) {
        	entry.getEntry(i).setEntryStatus(entry.getEntryStatus());
        }
    }
    
    private void initEventHandling() {
        PropertyChangeListener handler = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                performValidation(false);
            }
        };
        
        getResponseStatusModel().addValueChangeListener(handler);
    }
    
    public final List<StandardCode> getStandardCodes() {
        return standardCodes;
    }

    public void performValidation(boolean partial) {
        ValidationResult result = validate(partial);
        getValidationModel().setResult(result);
    }

    public final ValidationResultModel getValidationModel() {
        return validationModel;
    }
    
    public final ValueModel getResponseStatusModel() {
        return responseStatusModel;
    }
    
    public final DocumentInstance getDocInstance() {
        return docInstance;
    }
    
    public boolean isCopy() {
        return copy;
    }
    
    public SectionPresModel getSectionOccPresModel() {
        return sectionOccPresModel;
    }
    
    public void addEntryStatusListener(EntryStatusListener listener) {
        listenerList.add(EntryStatusListener.class, listener);
    }
    
    public void removeEntryStatusListener(EntryStatusListener listener) {
        listenerList.remove(EntryStatusListener.class, listener);
    }
    
    public void processEntryStatusChange(PropertyChangeEvent evt) {
        EntryStatus oldValue = (EntryStatus) evt.getOldValue();
        EntryStatus newValue = (EntryStatus) evt.getNewValue();

        setChildrenEntryStatus(newValue);
        fireEntryStatusEvent(new EntryStatusEvent(this, oldValue, newValue, 
                sectionOccPresModel));
    }
    
    public EntryStatusListener[] getEntryStatusListeners() {
        return listenerList.getListeners(EntryStatusListener.class);
    }
    
    protected void fireEntryStatusEvent(EntryStatusEvent event) {
        Object[] listeners = listenerList.getListenerList();

        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == EntryStatusListener.class) {
                ((EntryStatusListener) listeners[i + 1]).statusChanged(event);
            }
        }
    }
    
    private void setChildrenEntryStatus(EntryStatus entryStatus) {
        for (List<BasicPresModel> presModels : childPresModels) {
            for (BasicPresModel presModel : presModels) {
                presModel.getEntryStatusModel().setValue(entryStatus);
            }
        }
    }
    
    public void init() {
        getEntryStatusModel().addValueChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                processEntryStatusChange(evt);
            }
        });
        for (int i = 0; i < numColumns; ++i) {
            List<BasicPresModel> initialList = new ArrayList<BasicPresModel>();
            childPresModels.add(initialList);
            childPresModelsMap.put(entry.getEntry(i), initialList);
        }
    }

    public Map<String, String> getHeadings() {
        int numEntries = entry.numEntries();
        Map<String, String> headings = new LinkedHashMap<String, String>(numEntries);
        for (int i = 0; i < numEntries; ++i) {
            String displayText = entry.getEntry(i).getDisplayText();
            String description = entry.getEntry(i).getDescription();
            if (displayText == null) {
                displayText = ""; //$NON-NLS-1$
            }
            /* 
             * This became a requirement since the this method was changed
             * to return a Map instead of a List. Fail-fast instead of throwing
             * a cryptic ArrayIndexOutOfBoundsException.
             */
            if (headings.containsKey(displayText)) {
                throw new IllegalStateException("Each table header must have a " + //$NON-NLS-1$
                        "unique name"); //$NON-NLS-1$
            }
            headings.put(displayText, description);
        }
        return headings;
    }
    
    public void addEntryTableModelListener(EntryTableModelListener listener) {
        listenerList.add(EntryTableModelListener.class, listener);
    }
    
    public void removeEntryTableListener(EntryTableModelListener listener) {
        listenerList.remove(EntryTableModelListener.class, listener);
    }
    
    protected void fireRowAddedEvent(int rowIndex, String comment, 
            EditableStatus editable) {
        EntryTableModelEvent event = new EntryTableModelEvent(this,
                EntryTableModelEvent.Type.INSERT, rowIndex, comment, editable);
        fireRowChangedEvent(event);
    }
    
    protected void fireRowChangedEvent(EntryTableModelEvent event) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == EntryTableModelListener.class) {
                ((EntryTableModelListener) listeners[i + 1]).tableChanged(event);
            }
        }
    }
    
    protected void fireRowRemovedEvent(int rowIndex, String comment) {
        EntryTableModelEvent event = new EntryTableModelEvent(this,
                    EntryTableModelEvent.Type.DELETE, rowIndex, comment);
        fireRowChangedEvent(event);
    }
    
    public void addRow(int rowIndex, String comment, EditableStatus editable) {      
        applyStdCodeToRowActions.add(new ApplyStdCodeToRowAction(rowIndex));
       ++numRows;
        fireRowAddedEvent(rowIndex, comment, editable);
        processApplyStdCodeActionStatus();
    }
    
    public void addRow(String comment, EditableStatus editable) {
        addRow(numRows, comment, editable);
    }
    
    public void clear()   {
        for (int i = numRows - 1; i >= 0; --i) {
            removeRow(i);
        }
    }
    
    public void removeRow(int rowIndex) {
        removeRow(rowIndex, null);
    }
    
    public void removeRow(int rowIndex, String comment) {
        --numRows;
        applyStdCodeToRowActions.remove(rowIndex);
        updateApplyStdCodeToRowActionsIndices(rowIndex);
        processApplyStdCodeActionStatus();
        List<BasicPresModel> modelsRemoved = new ArrayList<BasicPresModel>(
                numColumns);
        for (int i = 0; i < numColumns; ++i) {
            modelsRemoved.add(childPresModels.get(i).remove(rowIndex));
        }
        
        if (comment == null) {
            response.removeCompositeRow(rowIndex);
        }
        else {
            response.removeCompositeRow(rowIndex, comment);
        }
        
        fireRowRemovedEvent(rowIndex, comment);
        fireChildModelEvent(new ChildModelEvent(this, 
                ChildModelEvent.Type.REMOVE, modelsRemoved, rowIndex));
    }

    private void updateApplyStdCodeToRowActionsIndices(int rowIndex) { 
        for (int i = rowIndex, c = applyStdCodeToRowActions.size(); i < c; ++i) {
            ApplyStdCodeToRowAction rowAction = applyStdCodeToRowActions.get(i);
            rowAction.setRowIndex(i);
        }
    }
    
    public final CompositeResponse getResponse() {
        return response;
    }

    public final CompositeEntry getEntry() {
        return entry;
    }
    
    /* (non-Javadoc)
     * @see org.psygrid.collection.entry.model.IPresModel#getEntryStatusModel()
     */
    public final ValueModel getEntryStatusModel() {
        return entryStatusModel;
    }
    
    public boolean containsEntry(BasicEntry childEntry) {
        return childPresModelsMap.containsKey(childEntry);
    }
    
    public void markAsEdited(String message) {
        ResponseStatus responseStatus = 
            (ResponseStatus) responseStatusModel.getValue();
        
        if (responseStatus != ResponseStatus.FLAGGED_EDITED) {
        	//Bug #1147: if a table is selected mark its entries as invalid, but not the table itself.
        	for (List<BasicPresModel> childModelList: this.childPresModels) {
        		for (BasicPresModel childModel: childModelList) {
        			childModel.getResponse().setAnnotation(message);
        			childModel.getResponseStatusModel().setValue(ResponseStatus.FLAGGED_EDITED);
        		}
        	}
        }
    }
    
    public List<BasicPresModel> getPresModelsForEntry(BasicEntry basicEntry) {
        List<BasicPresModel> presModels = childPresModelsMap.get(basicEntry);

        if (presModels != null) {
            return Collections.unmodifiableList(presModels);
        }

        return new ArrayList<BasicPresModel>(0);
    }
    
    public final void addPresModelsRow(List<BasicPresModel> childModels) {
        for (int i = 0; i < numColumns; ++i) {
            BasicPresModel childModel = childModels.get(i);
            childModel.getEntryStatusModel().setValue(getEntryStatusModel().getValue());
            childPresModels.get(i).add(childModel);
        }
        fireChildModelEvent(new ChildModelEvent(this, ChildModelEvent.Type.ADD, 
                childModels, numRows - 1));
    }
    
    public final void addChildModelListener(ChildModelListener listener) {
        listenerList.add(ChildModelListener.class, listener);
    }
    
    public final void removeChildModelListener(ChildModelListener listener) {
        listenerList.remove(ChildModelListener.class, listener);
    }
    
    protected void fireChildModelEvent(ChildModelEvent event) {
        Object[] listeners = listenerList.getListenerList();

        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ChildModelListener.class) {
                ((ChildModelListener) listeners[i + 1]).childModelsChanged(event);
            }
        }
    }
    
    /**
     * Because of  bug #1147 and bug #1220, a composite entry
     * no longer has its own validation (possibility to be edited/rejected)
     * Instead individual entries within the composite are
     * rejected/edited
     */
    public ValidationResult validate(boolean partial) {
        return new ValidationResult();
    }
    
    public ValidationResult validate() {
        return validate(false);
    }
    
    public int numColumns() {
        return numColumns;
    }
    
    public int numRows() {
        return numRows;
    }

    public ApplyStdCodeToRowAction getApplyStdCodeToRowAction(int rowIndex) {
        return applyStdCodeToRowActions.get(rowIndex);
    }
    
    public List<ApplyStdCodeToRowAction> getApplyStdCodeToRowActions(){
        return Collections.unmodifiableList(applyStdCodeToRowActions);
    }
    
    public void addApplyStdCodeToRowListener(ApplyStdCodeToRowListener listener) {
        listenerList.add(ApplyStdCodeToRowListener.class, listener);
    }
    
    public void removeApplyStdCodeToRowListener(ApplyStdCodeToRowListener listener) {
        listenerList.remove(ApplyStdCodeToRowListener.class, listener);
    }

    protected void fireApplyStdCodeToRowEvent(ApplyStdCodeToRowEvent event) {
        Object[] listeners = listenerList.getListenerList();

        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ApplyStdCodeToRowListener.class) {
                ((ApplyStdCodeToRowListener) listeners[i + 1]).applyStdCode(event);
            }
        }
    }
    
    public class ApplyStdCodeToRowAction extends AbstractAction {
        
        private static final long serialVersionUID = 3727010363728941396L;

        private int rowIndex;

        public ApplyStdCodeToRowAction(int rowIndex) {
            this.rowIndex = rowIndex;
            putValue(Action.SMALL_ICON, Icons.getInstance().getIcon("applyrowstdcode")); //$NON-NLS-1$
            putValue(Action.SHORT_DESCRIPTION, 
                    Messages.getString("EntryTableModel.applyStdCodeRowAction")); //$NON-NLS-1$
        }

        public void setRowIndex(int rowIndex) {
            this.rowIndex = rowIndex;
        }

        public void actionPerformed(ActionEvent e) {
            fireApplyStdCodeToRowEvent(new ApplyStdCodeToRowEvent(this, rowIndex));
        }
    }
    
    public void applyStdCodeToRow(int rowIndex, StandardCode code) {
    	for (int i = getStartColumn(); i < numColumns; ++i) {
            StandardPresModel spm = (StandardPresModel)childPresModels.get(i).get(rowIndex);
            //ignore derived entries
            Entry e = spm.getEntry();
            if ( e instanceof DerivedEntry || e instanceof BooleanEntry || e instanceof ExternalDerivedEntry){
                continue;
            }
            EditAction editable = e.getEditingPermitted();
            if (EditAction.DENY.equals(editable) || EditAction.READONLY.equals(editable)) {
            	continue;
            }
            spm.setStandardCode(code);
        }
    }

    protected abstract int getStartColumn();
    
    public void processApplyStdCodeActionStatus() {
        boolean disableAction = true;
        for ( int i = getStartColumn(); i < numColumns; ++i) {
            Entry e = childPresModels.get(i).get(0).getEntry();
            disableAction &= (e instanceof DerivedEntry || e instanceof BooleanEntry || e instanceof ExternalDerivedEntry);
            EditAction editable = e.getEditingPermitted();
            disableAction &= (EditAction.DENY.equals(editable) || EditAction.READONLY.equals(editable));
        }
        
        //override above setting if any of the basic entries have disable std codes set
        //if a child has std. codes disabled, disable std code filler option (must be done individually
        for ( int j = getStartColumn(); j < numColumns; ++j) {
            Entry e = childPresModels.get(j).get(0).getEntry();
           	if (e instanceof BasicEntry) {
        		if (((BasicEntry)e).isDisableStandardCodes()) {
        			disableAction = true;
        		}
        	}
        }
        
        //disable the action
        for ( ApplyStdCodeToRowAction action: applyStdCodeToRowActions ){
        	action.setEnabled( !disableAction );
        }
    }

}

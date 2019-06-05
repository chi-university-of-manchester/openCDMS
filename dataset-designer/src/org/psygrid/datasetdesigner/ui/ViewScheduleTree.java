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
package org.psygrid.datasetdesigner.ui;

import java.awt.Color;

import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import org.psygrid.data.model.hibernate.DocumentOccurrence;
import org.psygrid.datasetdesigner.model.StudyDataSet;
import org.psygrid.datasetdesigner.controllers.DatasetController;
import org.psygrid.datasetdesigner.renderer.MainTreeCellRenderer;

/**
 * 
 * Schedule Panel - to display all datasets 
 * in tree-like timeline format
 * @author pwhelan
 */
public class ViewScheduleTree extends JTree {

	/**
	 * Constructor - listen to the tree model
	 * @param treeModel the model for this tree
	 */
	public ViewScheduleTree(TreeModel treeModel){
		setBackground(Color.white);
		treeModel.addTreeModelListener(new StructureChangedListener());
		buildTreePanel();
		ToolTipManager.sharedInstance().registerComponent(this);
	}
	
	/**
	 * Fill the tree with datasets, document groups etc.
	 */
	public void  buildTreePanel() {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("");
		DefaultTreeModel buildTreeModel = new DefaultTreeModel(root);
	
		StudyDataSet dSet = DatasetController.getInstance().getActiveDs();
		
		if (dSet != null) {
			dSet.cleanAndCheckDataset();
			DefaultMutableTreeNode dSetNode = new DefaultMutableTreeNode(dSet);
			root.add(dSetNode);
			for (int i=0; i<dSet.getDs().numDocumentGroups(); i++) {
				DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(dSet.getDs().getDocumentGroup(i));
				buildTreeModel.insertNodeInto(childNode, dSetNode, dSetNode.getChildCount());
				for (int j=0; j<dSet.getDs().numDocuments(); j++) {
					for (int y=0; y<dSet.getDs().getDocument(j).numOccurrences(); y++) {
						DocumentOccurrence docOcc = dSet.getDs().getDocument(j).getOccurrence(y);
						if (docOcc.getDocumentGroup().equals(dSet.getDs().getDocumentGroup(i))) {
							buildTreeModel.insertNodeInto(new DefaultMutableTreeNode(docOcc.getDocument()), childNode, childNode.getChildCount());
						}
					}
				}
			}
		} 

		setRootVisible(false);
		setCellRenderer(new MainTreeCellRenderer(getCellRenderer()));
		setModel(buildTreeModel);
	}
	
	/**
	 * If any changes are made to the main tree,
	 * this needs to be updated too
	 * @author pwhelan
	 *
	 */
	private class StructureChangedListener implements TreeModelListener {

		public void treeNodesChanged(TreeModelEvent e) {
			treeStructureChanged(e);
		}

		public void treeNodesInserted(TreeModelEvent e) {
			treeStructureChanged(e);
		}

		public void treeNodesRemoved(TreeModelEvent e) {
			treeStructureChanged(e);
		}

		public void treeStructureChanged(TreeModelEvent e) {
			//if event has been fired and the ds is now null it was triggered
			//by a close event so clear the schedule view
			if (DatasetController.getInstance().getActiveDs() == null) {
				buildTreePanel();
			}
			
			Object [] path = e.getPath();
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)path[path.length-1];
			
			if (node != null) {
				if (node.getUserObject() instanceof StudyDataSet) {
					buildTreePanel();
				} 
			}
		}
	}
}

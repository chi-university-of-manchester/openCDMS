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
package org.psygrid.datasetdesigner.ui.configurationdialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.datasetdesigner.model.DSDocumentOccurrence;
import org.psygrid.datasetdesigner.model.DocTreeModel;

import org.psygrid.datasetdesigner.renderer.MainTreeCellRenderer;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.psygrid.datasetdesigner.utils.PropertiesHelper;

public class PreviewDocumentOccurrenceDialog extends JDialog implements ActionListener {

	private JButton okButton;
	private JComboBox datasetBox;
	private JList docOccList;
	
	public PreviewDocumentOccurrenceDialog(JDialog parentDialog, JComboBox datasetBox, JList docOccList) {
		super(parentDialog, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.previewdococcs"));
		this.datasetBox = datasetBox;
		this.docOccList = docOccList;
		setModal(true);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(buildMainPanel(), BorderLayout.CENTER);
		getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
		pack();
		setLocationRelativeTo(null);  
		setVisible(true);

	}

	public JPanel buildMainPanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		DefaultTreeModel treeModel = new DefaultTreeModel(new DefaultMutableTreeNode());
		
		String datasetName = (String)datasetBox.getSelectedItem();
		DataSet dataset = null;
		
		for (int i=0; i<DocTreeModel.getInstance().getAllDatasets().size(); i++) {
			if (DocTreeModel.getInstance().getAllDatasets().get(i).getName().equals(datasetName)){
				dataset = DocTreeModel.getInstance().getAllDatasets().get(i); 
			}
		}
		
		for (int i=0; i<dataset.numDocumentGroups(); i++) {
			DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(dataset.getDocumentGroup(i));
			treeModel.insertNodeInto(childNode, (DefaultMutableTreeNode)treeModel.getRoot(), ((DefaultMutableTreeNode)treeModel.getRoot()).getChildCount());
			for (int j=0; j<docOccList.getModel().getSize(); j++) {
				DSDocumentOccurrence docOcc = (DSDocumentOccurrence)docOccList.getModel().getElementAt(j);
				if (docOcc.getDocOccurrence().getDocumentGroup().equals(dataset.getDocumentGroup(i))) {
					treeModel.insertNodeInto(new DefaultMutableTreeNode(docOcc), childNode, childNode.getChildCount());
				}
			}
		}

		JTree docOccTree = new JTree();
		docOccTree.setCellRenderer(new MainTreeCellRenderer(docOccTree.getCellRenderer()));
		docOccTree.setRootVisible(false);
		docOccTree.setModel(treeModel);
		JScrollPane docTreeScroll = new JScrollPane(docOccTree);
		docTreeScroll.setPreferredSize(new Dimension(300, 300));
		docTreeScroll.setMinimumSize(new Dimension(300, 300));
		mainPanel.add(docTreeScroll, BorderLayout.CENTER);
		return mainPanel;
	}
	
	public JPanel buildButtonPanel() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		okButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.ok"));
		okButton.addActionListener(this);
		buttonPanel.add(okButton);
		return buttonPanel;
	}
	
	public void actionPerformed(ActionEvent aet) {
		if (aet.getSource() == okButton) {
			this.dispose();
		}
	}

}



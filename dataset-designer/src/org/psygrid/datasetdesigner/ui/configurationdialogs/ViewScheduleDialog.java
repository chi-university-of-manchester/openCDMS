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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.SwingConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.DocumentGroup;
import org.psygrid.data.model.hibernate.DocumentOccurrence;
import org.psygrid.datasetdesigner.custom.DSDRendererTable;
import org.psygrid.datasetdesigner.model.StudyDataSet;
import org.psygrid.datasetdesigner.model.DSDocumentOccurrence;
import org.psygrid.datasetdesigner.model.DocTreeModel;
import org.psygrid.datasetdesigner.renderer.MainTreeCellRenderer;
import org.psygrid.datasetdesigner.ui.MainTabbedPane;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.ElementUtility;
import org.psygrid.datasetdesigner.controllers.DatasetController;

/**
 * Dialog to show an editable schedule view of the study
 * Contains an editable table view and a non-editable tree view 
 * 
 * @author pwhelan
 */
public class ViewScheduleDialog extends JDialog implements ActionListener, ChangeListener{

	/**
	 * The logger for this class
	 */
	private static final Log LOG = LogFactory.getLog(ViewScheduleDialog.class);
	
	private StudyDataSet dSet;
	private JButton okButton;
	private JButton cancelButton;
	private JTable scheduleTable;
	private JTree docOccTree;
	
	private MainTabbedPane docPane;
	
	private boolean readOnly = false;
	
	public ViewScheduleDialog(JFrame parent, MainTabbedPane docPane) {
		super(parent, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.viewschedule"));
		this.dSet = DatasetController.getInstance().getActiveDs();
		this.docPane = docPane;
		setLayout(new BorderLayout());
		JTabbedPane mainPane = new JTabbedPane();
		mainPane.addChangeListener(this);
		mainPane.add(buildMainPanel(), "Schedule Table View");
		mainPane.add(buildTreePanel(), "Schedule Tree View");
		add(mainPane, BorderLayout.CENTER);
		add(buildButtonPanel(), BorderLayout.SOUTH);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public ViewScheduleDialog(JFrame parent, MainTabbedPane docPane, boolean readOnly) {
		super(parent, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.viewschedule"));
		this.dSet = DatasetController.getInstance().getActiveDs();
		this.docPane = docPane;
		this.readOnly = readOnly;
		setLayout(new BorderLayout());
		JTabbedPane mainPane = new JTabbedPane();
		mainPane.addChangeListener(this);
		mainPane.add(buildMainPanel(), "Schedule Table View");
		mainPane.add(buildTreePanel(), "Schedule Tree View");
		add(mainPane, BorderLayout.CENTER);
		add(buildButtonPanel(), BorderLayout.SOUTH);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	
	private JPanel buildMainPanel() {
		JPanel holderPanel = new JPanel();
		holderPanel.setLayout(new BorderLayout());
		scheduleTable = new DSDRendererTable();
		scheduleTable.setModel(new CustomTableModel());
		scheduleTable.getColumnModel().getColumn(0).setHeaderValue(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.documents"));
		//substance 4.0 defaults table headers to the left
		((DefaultTableCellRenderer)scheduleTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
		for (int i=0; i<dSet.getDs().numDocumentGroups(); i++) {
			scheduleTable.getColumnModel().getColumn(i+1).setHeaderValue(dSet.getDs().getDocumentGroup(i).getName());
		}
		
		holderPanel.add(new JScrollPane(scheduleTable), BorderLayout.CENTER);
		return holderPanel;
	}
	
	private JPanel buildTreePanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		DefaultTreeModel treeModel = new DefaultTreeModel(new DefaultMutableTreeNode(dSet));
		
		dSet.cleanAndCheckDataset();
		
		for (int i=0; i<dSet.getDs().numDocumentGroups(); i++) {
			DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(dSet.getDs().getDocumentGroup(i));
			treeModel.insertNodeInto(childNode, (DefaultMutableTreeNode)treeModel.getRoot(), ((DefaultMutableTreeNode)treeModel.getRoot()).getChildCount());
			for (int j=0; j<dSet.getDs().numDocuments(); j++) {
				for (int z=0; z<dSet.getDs().getDocument(j).numOccurrences(); z++) {
					DocumentOccurrence docOcc = dSet.getDs().getDocument(j).getOccurrence(z);
					if (docOcc.getDocumentGroup().equals(dSet.getDs().getDocumentGroup(i))) {
							treeModel.insertNodeInto(new DefaultMutableTreeNode(docOcc.getDocument()), childNode, childNode.getChildCount());
						}
					}
				}
			}

		docOccTree = new JTree();
		docOccTree.addMouseListener(new DoubleClickAdapter());
		docOccTree.setCellRenderer(new MainTreeCellRenderer(docOccTree.getCellRenderer()));
		docOccTree.setModel(treeModel);
		JScrollPane docTreeScroll = new JScrollPane(docOccTree);
		docTreeScroll.setPreferredSize(new Dimension(300, 300));
		docTreeScroll.setMinimumSize(new Dimension(300, 300));
		mainPanel.add(docTreeScroll, BorderLayout.CENTER);
		return mainPanel;

	}
	
	private JPanel buildButtonPanel(){
		okButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ok"));
		okButton.addActionListener(this);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(okButton);
		
		return buttonPanel;
	}
	
	public void actionPerformed(ActionEvent aet) {
		if (aet.getSource() == okButton) {
			if (DatasetController.getInstance().getActiveDs() != null) {
				DatasetController.getInstance().getActiveDs().setDirty(true);
			}

			DocTreeModel.getInstance().refreshDataset(DatasetController.getInstance().getActiveDs());
			
            //if published then show the provenance dialog
			if (DatasetController.getInstance().getActiveDs().getDs().isPublished()) {
				new ProvenanceDialog(this, DatasetController.getInstance().getActiveDs().getDs());
				//don't dispose of dialog
				return;
			} 
			
		}
		this.dispose();
	}
	
	private class CustomTableModel extends DefaultTableModel {
		
		public CustomTableModel() {
		}

		public int getRowCount() {
			if (dSet == null) {
				return 0;
			}
			return dSet.getDs().numDocuments();
		}

		public int getColumnCount() {
			if (dSet == null) {
				return 0;
			}
			return dSet.getDs().numDocumentGroups() + 1;
		}
		
		@Override
		public boolean isCellEditable(int row, int column) {
			//can't edit anything if in read-only mode
			if (readOnly) {
				return false;
			}
			
			if (column == 0) {
				return false;
			}
			return true;
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			if (columnIndex > 0) {
				return Boolean.class;
			}
			
			return super.getColumnClass(columnIndex);
		}

		@Override
		public Object getValueAt(int row, int column) {
			if (column == 0) {
				return dSet.getDs().getDocument(row);
			}
			
			Document doc = dSet.getDs().getDocument(row);
			DocumentGroup docGroup = dSet.getDs().getDocumentGroup(column - 1);
			
			DocTreeModel.getInstance().getDSDataset(dSet.getDs().getName()).cleanAndCheckDataset();
			
			for (int i=0; i<doc.numOccurrences(); i++) {
				if (doc.getOccurrence(i).getDocumentGroup().equals(docGroup) 
						&& !doc.getOccurrence(i).isLocked()) {
					return new Boolean(true);
				}
			}
		
			return new Boolean(false);
		}
		
		public void setValueAt(Object value, int row, int column) {
			DocTreeModel.getInstance().getDSDataset(dSet.getDs().getName()).cleanAndCheckDataset();

			
			Document doc = dSet.getDs().getDocument(row);
			DocumentGroup docGroup = dSet.getDs().getDocumentGroup(column - 1);
			
			if (((Boolean)value).booleanValue()) {
				boolean unlocked = false;
				
				//if it exists, then unlock it
				for (int i=0; i<doc.numOccurrences(); i++) {
					if (doc.getOccurrence(i).getDocumentGroup().equals(docGroup) 
							&& doc.getOccurrence(i).isLocked()) {
						doc.getOccurrence(i).setLocked(false);
						unlocked = true;
					}
				}
				
				//if it wasn't unlocked then create a new one and add it.
				if (!unlocked) {
					DSDocumentOccurrence docOcc = ElementUtility.createIDocumentOccurrence(doc, 
							docGroup, 
							docGroup.getName()+ "-" +doc.getName(), 
							docGroup.getName()+ "-" +doc.getName(), 
							docGroup.getName()+ "-" +doc.getName(), "", 
							false, false);
					doc.addOccurrence(docOcc.getDocOccurrence());
				}
			} else {
				for (int i=0; i<doc.numOccurrences(); i++) {
					if (doc.getOccurrence(i).getDocumentGroup().equals(docGroup)) {
						if (doc.getOccurrence(i).getId() != null && doc.getDataSet().isPublished()) {
							doc.getOccurrence(i).setLocked(true);
						} else {
							doc.removeOccurrence(i);
						}
					}
				}
			}
		}
		
	}


	public void stateChanged(ChangeEvent e) {
		
		if (docOccTree == null) {
			return;
		}
		
		dSet.cleanAndCheckDataset();
		
		DefaultTreeModel treeModel = new DefaultTreeModel(new DefaultMutableTreeNode(dSet));
		
		for (int i=0; i<dSet.getDs().numDocumentGroups(); i++) {
			DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(dSet.getDs().getDocumentGroup(i));
			treeModel.insertNodeInto(childNode, (DefaultMutableTreeNode)treeModel.getRoot(), ((DefaultMutableTreeNode)treeModel.getRoot()).getChildCount());
			for (int j=0; j<dSet.getDs().numDocuments(); j++) {
				for (int z=0; z<dSet.getDs().getDocument(j).numOccurrences(); z++) {
					DocumentOccurrence docOcc = dSet.getDs().getDocument(j).getOccurrence(z);
					if (docOcc.getDocumentGroup().equals(dSet.getDs().getDocumentGroup(i))) {
							treeModel.insertNodeInto(new DefaultMutableTreeNode(docOcc.getDocument()), childNode, childNode.getChildCount());
						}
					}
				}
			}

		docOccTree.setModel(treeModel);

	}
	
	private class DoubleClickAdapter extends MouseAdapter {
		
		public void mouseClicked(MouseEvent e) {
			if (SwingUtilities.isLeftMouseButton(e)) {
				if (e.getClickCount() == 2) {
					int x = e.getX();
					int y = e.getY();
					
					//show to user which is selected
					int row = docOccTree.getRowForLocation(x, y);
					docOccTree.setSelectionRow(row);
					
					try
					{
						Object[] path = docOccTree.getPathForLocation(e.getX(), e.getY()).getPath();
						DefaultMutableTreeNode node = (DefaultMutableTreeNode)path[path.length-1];
						if (node.getUserObject() instanceof Document) {
							
							//dataset name and document name should compose name in tab
							docPane.openTab(((Document)node.getUserObject()));
						} else if (node.getUserObject() instanceof DataSet) {
						}
					} catch (Exception ex)
					{
						LOG.error("View schedule dialog error on double-click", ex);
					}
				}
			}
		}
	}
	
	
}

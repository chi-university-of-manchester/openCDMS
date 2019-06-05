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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.datasetdesigner.model.StudyDataSet;
import org.psygrid.datasetdesigner.model.DocTreeModel;
import org.psygrid.datasetdesigner.model.ESLEmailModel;
import org.psygrid.datasetdesigner.renderer.EntryTableCellRenderer;
import org.psygrid.datasetdesigner.ui.MainFrame;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.esl.model.IRole;
import org.psygrid.esl.model.hibernate.HibernateFactory;
import org.psygrid.www.xml.security.core.types.RoleType;

public class ConfigureESLDialog extends JDialog implements ActionListener {

	private JComboBox datasetBox;

	private JButton okButton;
	private JButton cancelButton;

	private JTable eslTable;

	private int numESLDatasets = 0;

	private HashMap<String, ESLEmailModel> eslMap = new HashMap<String, ESLEmailModel>();

	public ConfigureESLDialog(MainFrame frame) {
		super(frame, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.configureesl"));
		getContentPane().setLayout(new BorderLayout());

		DataSet delDS = null;
		if (DocTreeModel.getInstance().getDELDataset() != null) {
			delDS = DocTreeModel.getInstance().getDELDataset().getDs();
		}
		ArrayList<DataSet> datasets = DocTreeModel.getInstance().getAllDatasets();
		for (int i=0; i<datasets.size(); i++) {
			if (!datasets.get(i).equals(delDS)) {
				if (((DataSet)datasets.get(i)).isEslUsed()) {
					numESLDatasets++;
				}
			}
		}

		if (numESLDatasets > 0) {
			getContentPane().add(buildComboPanel(), BorderLayout.NORTH);
			getContentPane().add(buildMainPanel(), BorderLayout.CENTER);
			getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
		} else {
			getContentPane().add(buildNoESLPanel(), BorderLayout.CENTER);
			getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);

		}

		init();

		pack();
		setLocationRelativeTo(null);  
		
		if (frame.getTree().getSelectedDataSet()!= null) {
			if (datasetBox != null) {
				datasetBox.setSelectedItem(frame.getTree().getSelectedDataSet());
			}
		}

		setVisible(true);
	}

	private JPanel buildNoESLPanel() {
		JPanel noESLPanel = new JPanel();
		noESLPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.noesldatasets")));
		return noESLPanel;
	}

	private JPanel buildComboPanel() {
		JPanel comboPanel = new JPanel();
		comboPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		datasetBox = new JComboBox();
		DefaultComboBoxModel comboModel = new DefaultComboBoxModel();
		DataSet delDS = null;
		if (DocTreeModel.getInstance().getDELDataset() != null) {
			delDS = DocTreeModel.getInstance().getDELDataset().getDs();
		}
		ArrayList<DataSet> datasets = DocTreeModel.getInstance().getAllDatasets();
		for (int i=0; i<datasets.size(); i++) {
			if (!datasets.get(i).equals(delDS)) {
				if (((DataSet)datasets.get(i)).isEslUsed()) {
					comboModel.addElement(datasets.get(i).getName());
				}
			}
		}
		datasetBox.setModel(comboModel);
		datasetBox.addItemListener(new DatasetChangedListener());
		comboPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.selectdataset")));
		comboPanel.add(datasetBox);
		return comboPanel;
	}

	private void initESLModel() {
		if (datasetBox != null && datasetBox.getSelectedItem() != null) {
			String datasetName = (String)datasetBox.getSelectedItem();
			StudyDataSet dsSet = DocTreeModel.getInstance().getDSDataset(datasetName);
			ESLEmailModel eslModel = dsSet.getEslModel();

			CustomTableModel tableModel = new CustomTableModel();

			for (int i=0; i<eslModel.getRoles().size(); i++) {
				IRole role = eslModel.getRoles().get(i);
				Vector rowVector = new Vector();
				rowVector.add(role);
				rowVector.add(role.isNotifyOfRSInvocation());
				rowVector.add(role.isNotifyOfRSDecision());
				rowVector.add(role.isNotifyOfRSTreatment());
				tableModel.addRow(rowVector);
			}

			eslTable.setModel(tableModel);

		}
	}

	private void init() {
		DataSet delDS = null;
		if (DocTreeModel.getInstance().getDELDataset() != null) {
			delDS = DocTreeModel.getInstance().getDELDataset().getDs();
		}
		ArrayList<DataSet> allDatasets = DocTreeModel.getInstance().getAllDatasets();
		for (int j=0; j<allDatasets.size(); j++) {
			if (!allDatasets.get(j).equals(delDS)) {
				String datasetName = allDatasets.get(j).getName();
				StudyDataSet dsSet = DocTreeModel.getInstance().getDSDataset(datasetName);
				ESLEmailModel emailModel = dsSet.getEslModel();
				eslMap.put(datasetName, emailModel);
			}
		}
		//configure initial setting
		if (datasetBox != null && datasetBox.getSelectedItem() != null) {
			String datasetName = (String)datasetBox.getSelectedItem();

			StudyDataSet dsSet = DocTreeModel.getInstance().getDSDataset(datasetName);
			ArrayList<RoleType> roles = dsSet.getRoles();
			ArrayList<IRole> eslRoles = new ArrayList<IRole>();

			for (int j=0; j<roles.size(); j++) {
				HibernateFactory factory = new org.psygrid.esl.model.hibernate.HibernateFactory();
				eslRoles.add(factory.createRole(roles.get(j).getName()));
			}

			ESLEmailModel eslModel = dsSet.getEslModel();
			initESLModel();
			TableColumn zeroColumn = eslTable.getColumnModel().getColumn(0);
			zeroColumn.setCellRenderer(new EntryTableCellRenderer());
			eslTable.getColumnModel().getColumn(0).setHeaderValue(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.rolename"));
			eslTable.getColumnModel().getColumn(1).setHeaderValue(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.nofifyrsinvocation"));
			eslTable.getColumnModel().getColumn(2).setHeaderValue(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.notifyrsdecision"));
			eslTable.getColumnModel().getColumn(3).setHeaderValue(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.notifyrstreatment"));
		}
	}

	private JPanel buildMainPanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setLayout(new BorderLayout());
		eslTable = new JTable(new CustomTableModel());
		eslTable.getColumnModel().getColumn(0).setHeaderValue(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.rolename"));
		eslTable.getColumnModel().getColumn(1).setHeaderValue(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.nofifyrsinvocation"));
		eslTable.getColumnModel().getColumn(2).setHeaderValue(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.notifyrsdecision"));
		eslTable.getColumnModel().getColumn(3).setHeaderValue(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.notifyrstreatment"));
		mainPanel.add(new JScrollPane(eslTable), BorderLayout.CENTER);
		return mainPanel;
	}

	private JPanel buildButtonPanel(){
		okButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ok"));
		okButton.addActionListener(this);
		cancelButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.cancel"));
		cancelButton.addActionListener(this);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		return buttonPanel;
	}

	public void saveCurrent() {
		ESLEmailModel eslModel = new ESLEmailModel();
		ArrayList<IRole> allRoles = new ArrayList<IRole>();

		for (int i=0; i<eslTable.getModel().getRowCount(); i++) {
			IRole role = (IRole)eslTable.getModel().getValueAt(i, 0);
			role.setNotifyOfRSInvocation(((Boolean)eslTable.getModel().getValueAt(i, 1)).booleanValue());
			role.setNotifyOfRSDecision(((Boolean)eslTable.getModel().getValueAt(i, 2)).booleanValue());
			role.setNotifyOfRSTreatment(((Boolean)eslTable.getModel().getValueAt(i, 3)).booleanValue());
			allRoles.add(role);
		}

		eslModel.setRoles(allRoles);
		eslMap.put((String)datasetBox.getSelectedItem(), eslModel);
	}

	public void actionPerformed(ActionEvent aet) {
		if (aet.getSource() == okButton) {
			DataSet delDS = null;
			if (DocTreeModel.getInstance().getDELDataset() != null) {
				delDS = DocTreeModel.getInstance().getDELDataset().getDs();
			}
			ArrayList allNames = DocTreeModel.getInstance().getAllDatasets();
			for (int i=0; i<allNames.size(); i++) {
				if (!allNames.get(i).equals(delDS)) {
					saveCurrent();
					String datasetName = ((DataSet)allNames.get(i)).getName();
					StudyDataSet dsSet = DocTreeModel.getInstance().getDSDataset(datasetName);
					dsSet.setEslModel((ESLEmailModel)eslMap.get(datasetName));
				}
			}
			this.dispose();
		} else if (aet.getSource() == cancelButton) {
			this.dispose();
		}
	}

	private class DatasetChangedListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			saveCurrent();
			initESLModel();
		}
	}

	private class CustomTableModel extends DefaultTableModel {

		private Vector rows;

		public CustomTableModel() {
			rows = new Vector();
		}

		@Override
		public void addRow(Vector rowData) {
			rows.add(rowData);
			fireTableDataChanged();
		}

		public void removeRow(int row) {
			rows.remove(row);
			fireTableDataChanged();
		}

		public int getRowCount() {
			if (rows != null) {
				return rows.size();
			}
			return 0;
		}

		public int getColumnCount() {
			return 4;
		}

		public Class getColumnClass(int columnIndex) {
			if (columnIndex != 0) {
				return Boolean.class;
			}

			return String.class;
		}


		@Override
		public boolean isCellEditable(int row, int column) {
			if (column == 0) {
				return false;
			}

			return true;
		}

		@Override
		public Object getValueAt(int row, int column) {
			Vector rowData = (Vector)rows.get(row);
			return rowData.get(column);
		}

		public void setValueAt(Object value, int row, int column) {
			((Vector)rows.get(row)).setElementAt(value, column);
		}

	}




}
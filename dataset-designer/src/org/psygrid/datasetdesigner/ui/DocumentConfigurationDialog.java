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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SpringLayout;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.datasetdesigner.controllers.DatasetController;
import org.psygrid.datasetdesigner.custom.TextFieldWithStatus;
import org.psygrid.datasetdesigner.model.DSDocumentOccurrence;
import org.psygrid.datasetdesigner.model.DocTreeModel;
import org.psygrid.datasetdesigner.ui.configurationdialogs.ConfigureDocPermissionsDialog;
import org.psygrid.datasetdesigner.utils.ElementUtility;
import org.psygrid.datasetdesigner.utils.HelpHelper;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.SpringUtilities;
import org.psygrid.datasetdesigner.model.StudyDataSet;
import org.psygrid.security.RBACAction;


/**
 * Dialog to set the properties of a document - name, description etc.
 * 
 * @author pwhelan
 */
public class DocumentConfigurationDialog extends JDialog implements ActionListener{

	/**
	 * The logger for this class
	 */
	private static final Log LOG = LogFactory.getLog(DocumentConfigurationDialog.class);


	private JButton okButton;
	private JButton cancelButton;

	private TextFieldWithStatus documentNameField;
	private TextFieldWithStatus descriptionField;
	private TextFieldWithStatus permissionsField;

	private JLabel datasetLabel;

	private JButton editPermissionsButton;

//	private JComboBox consentFormGroupBox;

	private DataSet dataset;

	private Document document = null;

	/**
	 * Specifies whether the document is a part of the
	 * data element library view.
	 */
	private boolean isDEL = false;

	private boolean edit;

	private JTable scheduleTable;

	private JTable cfgsTable;

	public DocumentConfigurationDialog(JFrame frame, boolean isDEL, boolean edit) {
		super(frame, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.documentconfig"));
		if (DatasetController.getInstance().getActiveDs() != null) {
			this.dataset = DatasetController.getInstance().getActiveDs().getDs();
		}
		this.isDEL = isDEL;
		this.edit = edit;
		buildLayout();
	}

	public DocumentConfigurationDialog(JFrame frame, Document document, boolean isDEL, boolean edit) {
		super(frame, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.documentconfig"));
		this.document = document;
		this.dataset = document.getDataSet();
		this.isDEL = isDEL;
		this.edit = edit;
		buildLayout();
	}

	public void buildLayout() {
		setModal(true);
		setLayout(new BorderLayout());

		if (document != null && ((Document)document).getLatestMetaData() != null) {
			JPanel mainPanel = buildMainPanel();
			DELInfoPanel delPanel = new DELInfoPanel(new DataElementContainer((Document)document));
			JTabbedPane pane = new JTabbedPane();
			pane.add(mainPanel, "Basic");
			pane.add(delPanel, "Library Info");
			getContentPane().add(pane, BorderLayout.CENTER);
		}
		else {
			getContentPane().add(buildMainPanel(), BorderLayout.CENTER);
		}
		getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
		setPreferredSize(new Dimension(500, 360));
		init();
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public void init() {
		if (document != null) {
			documentNameField.setText(document.getName());
			descriptionField.setText(document.getDescription());
			datasetLabel.setText(document.getDataSet().getName());
			documentNameField.setEditable(edit);	
			descriptionField.setEditable(edit);	
		}

		if (DatasetController.getInstance().getActiveDs() != null)
		{
			if (DatasetController.getInstance().getActiveDs().isReadOnly()) {
				documentNameField.setEnabled(false);
				descriptionField.setEnabled(false);
			} 
			
			//cannot change the name of the document after publishing
			if (DatasetController.getInstance().getActiveDs().getDs().isPublished()
					&& document != null) {
				documentNameField.setEditable(false);
			}
			datasetLabel.setText(DatasetController.getInstance().getActiveDs().getDs().getName());
		}
	}


	private JPanel buildMainPanel() {
		JPanel holderPanel = new JPanel();
		holderPanel.setLayout(new BorderLayout());

		JPanel mainPanel = new JPanel();
		documentNameField = new TextFieldWithStatus(20, true);

		descriptionField = new TextFieldWithStatus(20, false);
		permissionsField = new TextFieldWithStatus(18, false);
		permissionsField.setEditable(false);
		datasetLabel = new JLabel();
//		consentFormGroupBox = new JComboBox();
//		consentFormGroupBox.setRenderer(new OptionListCellRenderer());

//		if (isDEL) {
//		combModel.addElement(delDS.getName());
//		}
//		else {
//		for (int i=0; i<sets.size(); i++) {
//		if ((sets.get(i)) != delDS) {
//		combModel.addElement((sets.get(i)).getName());
//		}
//		}
//		}

//		datasetField.setModel(combModel);
//		datasetField.addItemListener(new DatasetChangedListener());

//		if (dataset != null) {
//		datasetField.setSelectedItem(dataset.getName());
//		}

//		DefaultComboBoxModel consentFormGroupBoxModel = new DefaultComboBoxModel();

//		if (dataset != null) {
//		for (int j=0; j<dataset.numAllConsentFormGroups(); j++) {
//		consentFormGroupBoxModel.addElement(dataset.getAllConsentFormGroup(j));
//		}
//		}

//		//null empty entry since cfg is not compulsory
//		consentFormGroupBoxModel.addElement(new String(" "));

//		consentFormGroupBox.setModel(consentFormGroupBoxModel);

		mainPanel.add(new JLabel());
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.dataset")));
		mainPanel.add(datasetLabel);

		mainPanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsddocconfigname"));
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.documentname")));
		mainPanel.add(documentNameField);

		mainPanel.setLayout(new SpringLayout());

		int rows = 2;

		if (document != null) {
			mainPanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsddocconfigdescription"));
			mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.docdescription")));
			mainPanel.add(descriptionField);
			rows++;

			if (!isDEL) {
				mainPanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsddocumentconfigpermissions"));
				mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.docpermissions")));
				JPanel permissionsPanel = new JPanel(new BorderLayout());
				permissionsPanel.add(permissionsField, BorderLayout.WEST);
				if ((document.getAction() == null || RBACAction.ACTION_DR_DOC_STANDARD.toString().equals(document.getAction()))
						&& (document.getEditableAction() == null || RBACAction.ACTION_DR_DOC_STANDARD.toString().equals(document.getEditableAction()))) {
					permissionsField.setText(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.docpermissionsdefault"));
				}
				else {
					permissionsField.setText(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.docpermissionscustom"));
				}

				if (edit) {
					editPermissionsButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.viewedit"));
				}
				else {
					editPermissionsButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.view"));
				}
				permissionsPanel.add(Box.createRigidArea(new Dimension(0,5)), BorderLayout.CENTER);
				permissionsPanel.add(editPermissionsButton, BorderLayout.EAST);
				editPermissionsButton.addActionListener(this);
				mainPanel.add(permissionsPanel);
				rows++;
			}
		}



		if (isDEL) {
			if (document != null && ((Document)document).getLatestMetaData() != null) {
				//Add some spacer rows to improve layout when the Library Info tab is present
				mainPanel.add(new JLabel(" "));
				mainPanel.add(new JLabel(" "));
				mainPanel.add(new JLabel(" "));
				rows++;
				mainPanel.add(new JLabel(" "));
				mainPanel.add(new JLabel(" "));
				mainPanel.add(new JLabel(" "));
				rows++;
			}
		}

		SpringUtilities.makeCompactGrid(mainPanel,
				rows, 3, 			//rows, cols
				6, 6,        //initX, initY
				6, 6);       //xPad, yPad

		holderPanel.add(mainPanel, BorderLayout.PAGE_START);

		if (!isDEL) {
			holderPanel.add(buildSchedulePanel(), BorderLayout.CENTER);
			holderPanel.add(buildConsentFormGroupPanel(), BorderLayout.PAGE_END);
		}

		return holderPanel;
	}

	private JPanel buildSchedulePanel() {
		JPanel holderPanel = new JPanel();

		JPanel selectStagesPanel = new JPanel();
		selectStagesPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		selectStagesPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.selectstagesfordocument")));

		holderPanel.setLayout(new BorderLayout());
		scheduleTable = new JTable();
		scheduleTable.setModel(new StagesTableModel());
		for (int i=0; i<dataset.numDocumentGroups(); i++) {
			scheduleTable.getColumnModel().getColumn(i).setHeaderValue(dataset.getDocumentGroup(i).getName());
		}

		holderPanel.add(selectStagesPanel, BorderLayout.NORTH);
		JScrollPane schedulePane = new JScrollPane(scheduleTable);
		schedulePane.setPreferredSize(new Dimension(this.getSize().width, 60));
		schedulePane.setMinimumSize(new Dimension(this.getMinimumSize().width, 60));

		holderPanel.add(schedulePane, BorderLayout.CENTER);
		return holderPanel;
	}

	private JPanel buildConsentFormGroupPanel() {
		JPanel holderPanel = new JPanel();

		JPanel selectCfgsPanel = new JPanel();
		selectCfgsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		selectCfgsPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.selectcfgsfordocument")));

		holderPanel.setLayout(new BorderLayout());
		cfgsTable = new JTable();
		cfgsTable.setModel(new CfgsTableModel());
		cfgsTable.getColumnModel().getColumn(0).setHeaderValue(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.documentconsentformgroup"));
		cfgsTable.getColumnModel().getColumn(1).setHeaderValue(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.consentrequired"));

		holderPanel.add(selectCfgsPanel, BorderLayout.NORTH);
		JScrollPane cfgsPane = new JScrollPane(cfgsTable);
		cfgsPane.setPreferredSize(new Dimension(this.getSize().width, 60));

		holderPanel.add(cfgsPane, BorderLayout.CENTER);
		return holderPanel;
	}

	private JPanel buildButtonPanel() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		okButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ok"));
		okButton.addActionListener(this);
		buttonPanel.add(okButton);

		//only show cancel button if creating new document; otherwise, changes 
		//to occurrences will persist
		if (document == null) {
			cancelButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.cancel"));
			cancelButton.addActionListener(this);
			buttonPanel.add(cancelButton);
		}
		return buttonPanel;
	}

	public boolean validateEntries() {

		if (documentNameField.getText() == null || documentNameField.getText().equals("")) {
			JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.documentnamenonempty"));
			return false;
		}

//		IDataSet dataset = DocTreeModel.getInstance().getDataset((String)datasetField.getSelectedItem()); 
		DataSet dataset = DatasetController.getInstance().getActiveDs().getDs();
		for (int i=0; i<dataset.numDocuments(); i++) {
			Document doc = dataset.getDocument(i);
			if (!doc.equals(document)) {
				if (doc.getName().equalsIgnoreCase(documentNameField.getText())) {
					JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.documentnameexists"));
					return false;
				}
			}
		}

		return true;
	}


	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == okButton) {
			if (validateEntries()) {
				ConsentFormGroup icfg = null;

				if (document == null) {
					Document document = ElementUtility.createIDocument(documentNameField.getText(),
                            documentNameField.getText(),
                            documentNameField.getText(),
                            datasetLabel.getText(),icfg);

					if (scheduleTable != null) {
						for (int i=0; i<scheduleTable.getColumnCount(); i++) {
							DocumentGroup docGroup = dataset.getDocumentGroup(i);
							if (scheduleTable.getValueAt(0,i) != null 
									&& ((Boolean)scheduleTable.getValueAt(0, i))) {
								DSDocumentOccurrence docOcc = ElementUtility.createIDocumentOccurrence(document, 
										docGroup, 
										docGroup.getName()+ "-" +document.getName(), 
										docGroup.getName()+ "-" +document.getName(), 
										docGroup.getName()+ "-" +document.getName(), "", 
										false, false);
								document.addOccurrence(docOcc.getDocOccurrence());
								DocTreeModel.getInstance().fireTreeModelChanged(document);
							}
						} 
					}
					if ( cfgsTable != null ){
						for (int i=0, c=cfgsTable.getRowCount(); i<c; i++) {
							ConsentFormGroup cfg = dataset.getAllConsentFormGroup(i);
							if (cfgsTable.getValueAt(i,1) != null 
									&& ((Boolean)cfgsTable.getValueAt(i,1))) {
								document.addConsentFormGroup(cfg);
							}
						} 
					}
				} else {
					if (!document.getName().equals(documentNameField.getText())) {
						//mark has having been changed..
						((Document)document).setIsRevisionCandidate(true);
					}
					document.setDisplayText(documentNameField.getText());
					document.setDescription(descriptionField.getText());
					document.setName(documentNameField.getText());

					if (icfg != null) {
						List<ConsentFormGroup> cfgs = new ArrayList<ConsentFormGroup>();
						cfgs.add((ConsentFormGroup)icfg);
						((Document)document).setConFrmGrps(cfgs);
					}
				}

				this.dispose();
			}
		} else if (event.getSource() == cancelButton) {
			this.dispose();
		} else if (event.getSource() == editPermissionsButton) {
			new ConfigureDocPermissionsDialog(this, document, !edit);
		}
	}

	private class StagesTableModel extends DefaultTableModel {

		public int getRowCount() {
			return 1;
		}

		public int getColumnCount() {
			return dataset.numDocumentGroups();
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			if (DatasetController.getInstance().getActiveDs() != null &&
					DatasetController.getInstance().getActiveDs().isReadOnly()) 
			{
				return false;
			}

			return true;
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return Boolean.class;
		}

		public void setValueAt(Object value, int row, int column) {
			
			if (document == null) {
				super.setValueAt(value, row, column);
				return;
			}
			
			StudyDataSet dSet = DatasetController.getInstance().getActiveDs();
			DocTreeModel.getInstance().getDSDataset(dSet.getDs().getName()).cleanAndCheckDataset();
			
			DocumentGroup docGroup = dSet.getDs().getDocumentGroup(column);

			if (((Boolean)value).booleanValue()) {
				boolean unlocked = false;
				
				//if it exists, then unlock it
				for (int i=0; i<document.numOccurrences(); i++) {
					if (document.getOccurrence(i).getDocumentGroup().equals(docGroup) 
							&& document.getOccurrence(i).isLocked()) {
						document.getOccurrence(i).setLocked(false);
						unlocked = true;
					}
				}
				
				//if it wasn't unlocked then create a new one and add it.
				if (!unlocked) {
					DSDocumentOccurrence docOcc = ElementUtility.createIDocumentOccurrence(document, 
							docGroup, 
							docGroup.getName()+ "-" +document.getName(), 
							docGroup.getName()+ "-" +document.getName(), 
							docGroup.getName()+ "-" +document.getName(), "", 
							false, false);
					document.addOccurrence(docOcc.getDocOccurrence());
				}
			} else {
				for (int i=0; i<document.numOccurrences(); i++) {
					if (document.getOccurrence(i).getDocumentGroup().equals(docGroup)) {
						if (document.getOccurrence(i).getId() != null && document.getDataSet().isPublished()) {
							document.getOccurrence(i).setLocked(true);
						} else {
							document.removeOccurrence(i);
						}
					}
				}
			}
		}
		
		@Override
		public Object getValueAt(int row, int column) {
			if (document == null) {
				return super.getValueAt(row, column);
			}

			DocumentGroup docGroup = dataset.getDocumentGroup(column);

			DocTreeModel.getInstance().getDSDataset(dataset.getName()).cleanAndCheckDataset();

			for (int i=0; i<document.numOccurrences(); i++) {
				if (document.getOccurrence(i).getDocumentGroup().equals(docGroup)
						&& !document.getOccurrence(i).isLocked()) {
					return new Boolean(true);
				}
			}
			return new Boolean(false);
		}
	}

	private class CfgsTableModel extends DefaultTableModel {

		public int getRowCount() {
			return dataset.numAllConsentFormGroups();
		}

		public int getColumnCount() {
			return 2;
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			if (DatasetController.getInstance().getActiveDs().getDs().isPublished()) {
				return false;
			}
			
			if (DatasetController.getInstance().getActiveDs() != null &&
					DatasetController.getInstance().getActiveDs().isReadOnly()) {
				return false;
			}
			if ( 0 == column ){
				return false;
			}
			return true;
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex){
			case 0:
				return String.class;
			case 1:
				return Boolean.class;
			}
			throw new RuntimeException("Unexpected column in consent form groups table");
		}

		public void setValueAt(Object value, int row, int column) {
			if (document == null) {
				super.setValueAt(value, row, column);
				return;
			}

			ConsentFormGroup cfg = dataset.getAllConsentFormGroup(row);
			if ( 1 == column ){
				if (((Boolean)value)) {
					document.addConsentFormGroup(cfg);
					DocTreeModel.getInstance().fireTreeModelChanged(document);
				} else {
					for (int i=0, c=document.numConsentFormGroups(); i<c;  i++) {
						if (document.getConsentFormGroup(i).equals(cfg)) {
							document.removeConsentFormGroup(i);
							DocTreeModel.getInstance().fireTreeModelChanged(document);
						}
					}
				}
			}
		}
		@Override
		public Object getValueAt(int row, int column) {
			ConsentFormGroup cfg = dataset.getAllConsentFormGroup(row);
			if ( 0 == column ){
				return cfg.getDescription();
			}
			else{
				if (document == null) {
					return super.getValueAt(row, column);
				}
				else{
					for (int i=0, c=document.numConsentFormGroups(); i<c;  i++) {
						if (document.getConsentFormGroup(i).equals(cfg)) {
							return new Boolean(true);
						}
					}
					return new Boolean(false);
				}
			}
		}
	}
}

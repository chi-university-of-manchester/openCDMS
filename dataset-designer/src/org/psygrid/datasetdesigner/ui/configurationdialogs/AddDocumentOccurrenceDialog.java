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

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.DocumentGroup;
import org.psygrid.datasetdesigner.model.DocTreeModel;
import org.psygrid.datasetdesigner.model.DSDocumentOccurrence;

import org.psygrid.datasetdesigner.renderer.OptionListCellRenderer;

import org.psygrid.datasetdesigner.utils.SpringUtilities;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.ElementUtility;

public class AddDocumentOccurrenceDialog extends JDialog implements ActionListener {

	private JComboBox datasetBox;
	private JButton okButton;
	private JButton cancelButton;
	
	private JList docOccList;
	
	private HashMap docOccMap;
	
	private JTextField docOccurrenceNameField;
	private JTextField docOccurrenceDisplayTextField;
	private JTextField docOccurrenceLabelField;
	private JTextField docOccurrenceDescriptionField;
	
	private JComboBox docGroupBox;
	private JComboBox documentBox;
	
	private JCheckBox randomisationTriggerBox;
	private JCheckBox lockedBox;
	
	private DSDocumentOccurrence docOcc = null;
	
	public AddDocumentOccurrenceDialog(JDialog parentDialog, JComboBox datasetBox, JList docOccList, HashMap docOccMap) {
		super(parentDialog, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.adddococcurrence"));
		this.datasetBox = datasetBox;
		this.docOccMap = docOccMap;
		this.docOccList = docOccList;
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(buildSettingsPanel(), BorderLayout.CENTER);
		getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
		init();
		pack();
		setLocationRelativeTo(null);
		}
	
	public AddDocumentOccurrenceDialog(JDialog parentDialog, JComboBox datasetBox,
									   JList docOccList,
									   HashMap docOccMap,
									   DSDocumentOccurrence docOcc) {
		super(parentDialog, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.editdococcurrence"));
		this.docOcc = docOcc;
		this.datasetBox = datasetBox;
		this.docOccMap = docOccMap;
		this.docOccList = docOccList;
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(buildSettingsPanel(), BorderLayout.CENTER);
		getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
		init();
		init(docOcc);
		pack();
		setLocationRelativeTo(null);
	}
	
	
	private void init(DSDocumentOccurrence docOcc) {
		docOccurrenceNameField.setText(docOcc.getDocOccurrence().getName());
		docOccurrenceLabelField.setText(docOcc.getDocOccurrence().getLabel());
		docOccurrenceDescriptionField.setText(docOcc.getDocOccurrence().getDescription());
		docOccurrenceDisplayTextField.setText(docOcc.getDocOccurrence().getDisplayText());
		docGroupBox.setSelectedItem(docOcc.getDocOccurrence().getDocumentGroup());
		documentBox.setSelectedItem(docOcc.getDocument());
		randomisationTriggerBox.setSelected(docOcc.getDocOccurrence().isRandomizationTrigger());
	}
	
	private void init() {
		DefaultComboBoxModel docGroupModel = new DefaultComboBoxModel();
		DefaultComboBoxModel docModel = new DefaultComboBoxModel(); 
		
		ArrayList<DataSet> allDatasets = DocTreeModel.getInstance().getAllDatasets();
		for (int j=0; j<allDatasets.size(); j++) {
			String datasetName = allDatasets.get(j).getName();
			if (datasetName.equals(datasetBox.getSelectedItem())) {
				DataSet dataSet = allDatasets.get(j);
				int numDocGroups = dataSet.numDocumentGroups();
				for (int z=0; z<numDocGroups; z++) {
					DocumentGroup group = dataSet.getDocumentGroup(z);
					docGroupModel.addElement(group);
				}
				int numDocs = dataSet.numDocuments();
				for (int y=0; y<numDocs; y++) {
					Document document = dataSet.getDocument(y);
					docModel.addElement(document);
				}
			}
		}
		
		docGroupBox.setModel(docGroupModel);
		documentBox.setModel(docModel);
	}
	
	private JPanel buildSettingsPanel() {
		JPanel mainPanel = new JPanel(new SpringLayout());
		docOccurrenceNameField = new JTextField(40);
		docOccurrenceDisplayTextField = new JTextField(40);
		docOccurrenceDescriptionField = new JTextField(40);
		docOccurrenceLabelField = new JTextField(40);
		randomisationTriggerBox = new JCheckBox();
		lockedBox = new JCheckBox();
		documentBox = new JComboBox();
		documentBox.setRenderer(new OptionListCellRenderer());
		docGroupBox = new JComboBox();
		docGroupBox.setRenderer(new OptionListCellRenderer());
		
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.dococcname")));
		mainPanel.add(docOccurrenceNameField);
		
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.dococcdisplaytext")));
		mainPanel.add(docOccurrenceDisplayTextField);
		
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.dococclabel")));
		mainPanel.add(docOccurrenceLabelField);
		
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.dococcdescription")));
		mainPanel.add(docOccurrenceDescriptionField);
		
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.document")));
		mainPanel.add(documentBox);
		
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.documentgroup")));
		mainPanel.add(docGroupBox);
		
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.randomisationTrigger")));
		mainPanel.add(randomisationTriggerBox);
		
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.locked")));
		mainPanel.add(lockedBox);
		
		SpringUtilities.makeCompactGrid(mainPanel,
                8, 2, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad

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
	
	public boolean validateEntries() {
		String docOccName = docOccurrenceNameField.getText();
		String docOccDisplayText = docOccurrenceDisplayTextField.getText();
		
		if (docOccName == null || docOccName.equals("")) {
			JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.dococcnamenotempty."));
			return false;
		}
		
		if (docOccDisplayText == null || docOccDisplayText.equals("")) {
			JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.dococcdisplaytextnotempty"));
			return false;
		}
		
		if (documentBox.getSelectedItem() == null) {
			JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.docfieldnonempty"));
			return false;
		}
		
		if (docGroupBox.getSelectedItem() == null) {
			JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.docgroupnonempty"));
			return false;
		}
		
		return true;
	}
	
	public void actionPerformed(ActionEvent aet) {
		if (aet.getSource() == okButton) {
			
			if (validateEntries()) {
				try {
					//if the doc occ is null, we're not editing
					docOcc.getDocOccurrence().getName();
					docOcc.getDocOccurrence().setName(docOccurrenceNameField.getText());
					docOcc.getDocOccurrence().setDisplayText(docOccurrenceDisplayTextField.getText());
					docOcc.getDocOccurrence().setDescription(docOccurrenceDescriptionField.getText());
					docOcc.getDocOccurrence().setLabel(docOccurrenceLabelField.getText());
					docOcc.getDocOccurrence().setRandomizationTrigger(randomisationTriggerBox.isSelected());
					docOcc.getDocOccurrence().setLocked(lockedBox.isSelected());
					docOcc.setDocument((Document)documentBox.getSelectedItem());
					if (docGroupBox.getSelectedItem()!=null && !(docGroupBox.getSelectedItem().equals(""))){
						docOcc.getDocOccurrence().setDocumentGroup((DocumentGroup)docGroupBox.getSelectedItem());
					} else {
						docOcc.getDocOccurrence().setDocumentGroup(null);
					}
				}  catch (NullPointerException ex) {
					Document document = (Document)documentBox.getSelectedItem();
					DSDocumentOccurrence docOcc = ElementUtility.createIDocumentOccurrence((Document)documentBox.getSelectedItem(),
							(DocumentGroup)docGroupBox.getSelectedItem(),
							docOccurrenceNameField.getText(), 
							docOccurrenceDisplayTextField.getText(), 
							docOccurrenceDescriptionField.getText(), 
							docOccurrenceLabelField.getText(), 
							lockedBox.isSelected(), 
							randomisationTriggerBox.isSelected());
					docOcc.setDocument(document);
					((DefaultListModel)docOccList.getModel()).addElement(docOcc);
					docOccMap.put(datasetBox.getSelectedItem(), docOccList.getModel());
				}
				
				this.dispose();
			}
		} else if (aet.getSource() == cancelButton) {
			this.dispose();
		}
	}
		
}


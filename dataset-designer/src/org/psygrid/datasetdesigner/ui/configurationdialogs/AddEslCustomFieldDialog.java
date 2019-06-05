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
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.psygrid.data.model.hibernate.EslCustomField;
import org.psygrid.datasetdesigner.custom.TextFieldWithStatus;
import org.psygrid.datasetdesigner.model.CustomFieldValueModel;
import org.psygrid.datasetdesigner.renderer.OptionListCellRenderer;
import org.psygrid.datasetdesigner.utils.ElementUtility;
import org.psygrid.datasetdesigner.utils.ListModelUtility;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.SpringUtilities;

/**
 * @author Rob Harper
 *
 */
public class AddEslCustomFieldDialog extends JDialog implements ActionListener {
		
	private JButton okButton;
	private JButton cancelButton;
	
	private TextFieldWithStatus fieldNameField;

	private JButton addValueButton;
	private JButton editValueButton;
	private JButton removeValueButton;
	
	private JList customFieldList;
	private EslCustomField customField;
	private JList valuesList;

	
	/**
	 * Constructor : used for adding
	 * @param parentDialog the owner
	 * @param groupList the list to populate the dialog with
	 */
	public AddEslCustomFieldDialog(JDialog parentDialog, JList customFieldList) {
		this(parentDialog, customFieldList, null);
		setTitle(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.addfield"));
	}

	/**
	 * Constructor : used for editing 
	 * @param parentDialog the owner
	 * @param groupList the list 
	 * @param groupModel the model of groups
	 */
	public AddEslCustomFieldDialog(JDialog parentDialog,					  
						  		   JList customFieldList, 
						  		   EslCustomField customField) {
		super(parentDialog, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.editfield"));
		this.customFieldList = customFieldList;
		this.customField = customField;
		setModal(true);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(buildMainPanel(), BorderLayout.CENTER);
		getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
		pack();
		init(customField);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	/**
	 * Show the details and site panel
	 * @return the configured main panel
	 */
	private JPanel buildMainPanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(buildDetailsPanel());
		mainPanel.add(buildValuesPanel());
		return mainPanel;
	}
	
	/**
	 * Build the details panel to take name
	 * and id 
	 * @return the details panel
	 */
	private JPanel buildDetailsPanel() {
		JPanel mainPanel = new JPanel(new SpringLayout());
		fieldNameField = new TextFieldWithStatus(40, true);
		
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.fieldname")));
		mainPanel.add(fieldNameField);
		
		SpringUtilities.makeCompactGrid(mainPanel,
                1, 2, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad

		return mainPanel;
	}
	
	/**
	 * Build the sites panel
	 * @return the configured sites panel
	 */
	private JPanel buildValuesPanel() {
		JPanel valuesPanel = new JPanel();
		valuesPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		valuesPanel.setLayout(new BorderLayout());
		valuesList = new JList();
		valuesList.setCellRenderer(new OptionListCellRenderer());
		valuesList.setModel(new DefaultListModel());
		JScrollPane valuesPane = new JScrollPane(valuesList);
		valuesPane.setMinimumSize(new Dimension(200, 200));
		valuesPane.setPreferredSize(new Dimension(200, 200));
		valuesPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.fieldvalues")), BorderLayout.NORTH);
		valuesPanel.add(valuesPane, BorderLayout.CENTER);
		addValueButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.addfieldvalue"));
		addValueButton.addActionListener(this);
		editValueButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.editfieldvalue"));
		editValueButton.addActionListener(this);
		editValueButton.setEnabled(false);
		removeValueButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.removefieldvalue"));
		removeValueButton.addActionListener(this);
		removeValueButton.setEnabled(false);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.add(addValueButton);
		buttonPanel.add(editValueButton);
		buttonPanel.add(removeValueButton);

		valuesPanel.add(buttonPanel, BorderLayout.SOUTH);

        //Only enable the buttons if an item is selected
		valuesList.addListSelectionListener(new ListSelectionListener() {
               	public void valueChanged(ListSelectionEvent event) {
                    boolean enabled = true;

                    if ((valuesList == null) || (valuesList.getSelectedValue() == null)) {
                        enabled = false;
                    }

                    if (removeValueButton != null) {
                    	removeValueButton.setEnabled(enabled);
                    }

                    if (editValueButton != null) {
                    	editValueButton.setEnabled(enabled);
                    }
                }
            });


		return valuesPanel;
	}
	
	/** 
	 * Build the ok/ cancel button panel
	 */
	public JPanel buildButtonPanel(){
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
	
	private void init(EslCustomField customField) {
		if (customField != null) {
			fieldNameField.setText(customField.getName());
			
			//set site model
			DefaultListModel valueModel = new DefaultListModel();
			for (int i=0; i<customField.getValueCount(); i++) {
				valueModel.addElement(new CustomFieldValueModel(customField.getValue(i)));
			}
			valuesList.setModel(valueModel);			
		}
	}
	
	/**
	 * Handle ok, cancel etc.
	 * @param aet the calling event
	 */
	public void actionPerformed(ActionEvent aet) {
		if (aet.getSource() == okButton) {
			if (validateFields()) {
				if (customField == null) {
					EslCustomField field = ElementUtility.createEslCustomField(fieldNameField.getText(),
                            ListModelUtility.convertListModelToCustomFieldValueList((DefaultListModel) valuesList.getModel()));
				    ((DefaultListModel)customFieldList.getModel()).addElement(field);
				} else {
					customField.setName(fieldNameField.getText());
					((EslCustomField)customField).setValues(ListModelUtility.convertListModelToCustomFieldValueList((DefaultListModel)valuesList.getModel()));
				} 
				this.dispose();
			}
	} else if (aet.getSource() == cancelButton) {
			this.dispose();
	} else if (aet.getSource() == addValueButton ) {
			new AddFieldValueDialog(this, valuesList);
	} else if (aet.getSource() == editValueButton ) {
		if (valuesList.getSelectedIndex() != -1) {
				new AddFieldValueDialog(this, valuesList, (CustomFieldValueModel)valuesList.getSelectedValue());
		}
	} else if (aet.getSource() == removeValueButton ) {
			((DefaultListModel)valuesList.getModel()).removeElement(valuesList.getSelectedValue());
		}
	}

	/**
	 * Validate the entries for groups
	 * @return
	 */
	public boolean validateFields() {
		String fieldName = fieldNameField.getText();

		if (fieldName == null || fieldName.equals("")) {
			JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.fieldnamenotempty"));
			return false;
		}

		if (customFieldList != null) {
			ArrayList<EslCustomField> fields = ListModelUtility.convertListModelToEslCustomFieldList((DefaultListModel)customFieldList.getModel());
			for (EslCustomField field: fields) {
				if ( !field.equals(customField) && field.getName().equals(fieldName)) {
					JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.fieldnamexists"));
					return false;
				}
			}
		}
		
		if ( 0 == valuesList.getModel().getSize() ){
			JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.atleastonevalue"));
			return false;
		}
		
		return true;
	}
	

}

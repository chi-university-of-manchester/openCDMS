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

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import javax.swing.DefaultListModel;

import org.psygrid.data.model.hibernate.Transformer;
import org.psygrid.datasetdesigner.utils.ElementUtility;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.SpringUtilities;
import org.psygrid.datasetdesigner.utils.HelpHelper;

import org.psygrid.datasetdesigner.custom.TextFieldWithStatus;

/**
 * Add or edit a transformer dialog 
 * @author pwhelan
 */
public class AddTransformerDialog extends JDialog implements ActionListener {

	private final static String DATE_TRANSFORMER = "Date Transformer";
	private final static String POSTCODE_TRANSFORMER = "Postcode Transformer";
	private final static String OPCRIT_TRANSFORMER = "Opcrit Transformer";
	private final static String TYRERCUZICK_TRANSFORMER = "TyrerCuzick Transformer";
	private final static String DEFINE_NEW_TRANSFORMER = "Define a new transformer";
	
	//action buttons
	private JButton okButton;
	private JButton cancelButton;
	
	private JComboBox transformerType;

	//field for name, operation entry etc.
	private TextFieldWithStatus wsnamespaceField;
	private TextFieldWithStatus operationField;
	private TextFieldWithStatus urlField;
	private TextFieldWithStatus resultClassField;
	private JCheckBox viewableOutputBox;

	//list of existing transformers
	private JList transformerList;
	
	//the transformer to edit
	private Transformer transformer;

	private boolean viewOnly;

	/**
	 * Constructor 
	 * @param parentDialog the owning dialog
	 * @param datasetBox the dataset combobox
	 * @param transformerList the list of existing transformers
	 * @param transformerMap a map of existing transformers for a dataset
	 */
	public AddTransformerDialog(JDialog parentDialog, JList transformerList) {
		super(parentDialog, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.addtransformer"));
		this.transformerList = transformerList;
		this.viewOnly = false;
		init(null);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	/**
	 * Constructor 
	 * @param parentDialog the owning dialog
	 * @param datasetBox the dataset combobox
	 * @param transformerList the list of existing transformers
	 * @param transformerMap a map of existing transformers for a dataset
	 * @param transformer  the transformer to edit
	 */
	public AddTransformerDialog(JDialog parentDialog, JList transformerList, Transformer transformer) {
		super(parentDialog, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.edittransformer"));
		this.transformer = transformer;
		this.transformerList = transformerList;
		this.viewOnly = false;
		init(transformer);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public AddTransformerDialog(JDialog parentDialog, JList transformerList, Transformer transformer, boolean viewOnly) {
		super(parentDialog, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.viewtransformer"));
		this.transformer = transformer;
		this.transformerList = transformerList;
		this.viewOnly = viewOnly;
		init(transformer);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	/**
	 * Setup the fields with the transformer to edit
	 * @param transformer the transformers to edit
	 */
	private void init(Transformer transformer) {
		setModal(true);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(buildMainPanel(), BorderLayout.CENTER);
		getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
		
		DefaultComboBoxModel transformerTypeModel = new DefaultComboBoxModel();
		transformerTypeModel.addElement(DATE_TRANSFORMER);
		transformerTypeModel.addElement(POSTCODE_TRANSFORMER);
		transformerTypeModel.addElement(OPCRIT_TRANSFORMER);
		transformerTypeModel.addElement(TYRERCUZICK_TRANSFORMER);
		transformerTypeModel.addElement(DEFINE_NEW_TRANSFORMER);
		
		
		transformerType.setModel(transformerTypeModel);
		transformerType.addActionListener(new TypeSelectionChangedListener());
		
		transformerType.setSelectedItem(DATE_TRANSFORMER);
		
		if (transformer != null) {
			String wsoperation = transformer.getWsOperation();
			if (wsoperation.contains("getMonthAndYear")) {
				transformerType.setSelectedItem(DATE_TRANSFORMER);
			} else if (wsoperation.contains("getSOA")) {
				transformerType.setSelectedItem(POSTCODE_TRANSFORMER);
			} else if (wsoperation.contains("opcrit")) {
				transformerType.setSelectedItem(OPCRIT_TRANSFORMER);
			} else if (wsoperation.contains("tyrercuzick")) {
				transformerType.setSelectedItem(TYRERCUZICK_TRANSFORMER);
			} else {
				transformerType.setSelectedItem(DEFINE_NEW_TRANSFORMER);
			}
			
			wsnamespaceField.setText(transformer.getWsNamespace());
			wsnamespaceField.setEditable(!viewOnly);
			operationField.setText(transformer.getWsOperation());
			operationField.setEditable(!viewOnly);
			urlField.setText(transformer.getWsUrl());
			urlField.setEditable(!viewOnly);
			resultClassField.setText(transformer.getResultClass());
			resultClassField.setEditable(!viewOnly);
			viewableOutputBox.setSelected(transformer.isViewableOutput());
			viewableOutputBox.setEnabled(!viewOnly);
		}
	}

	/**
	 * Main panel containing the fields of entry
	 * @return the configured panel with entry fields
	 */
	private JPanel buildMainPanel() {
		JPanel mainPanel = new JPanel(new SpringLayout());
		
		transformerType = new JComboBox();

		wsnamespaceField = new TextFieldWithStatus(45, true);
		operationField = new TextFieldWithStatus(45, true);
		urlField = new TextFieldWithStatus(45, true);
		resultClassField = new TextFieldWithStatus(45, true);
		viewableOutputBox = new JCheckBox();

		mainPanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsdtransformertype"));
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.transformerType")));
		mainPanel.add(transformerType);
		
		mainPanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsdtransnamespace"));
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.webservicenamespace")));
		mainPanel.add(wsnamespaceField);
		
		mainPanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsdtransoperation"));
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.webserviceoperation")));
		mainPanel.add(operationField);
		
		mainPanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsdtransurl"));
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.webserviceurl")));
		mainPanel.add(urlField);
		
		mainPanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsdtransresultclass"));
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.transformerresultclass")));
		mainPanel.add(resultClassField);
		
		mainPanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsdtransviewableoutput"));
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.viewableoutput")));
		mainPanel.add(viewableOutputBox);

		SpringUtilities.makeCompactGrid(mainPanel,
                6, 3, //rows, cols
				6, 6,        //initX, initY
				6, 6);       //xPad, yPad

		return mainPanel;
	}

	/**
	 * The button panel containing 
	 * ok and cancel buttons
	 * @return the configured action button panel
	 */
	public JPanel buildButtonPanel(){
		if (viewOnly) {
			cancelButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.close"));
			cancelButton.addActionListener(this);
		}
		else {
			okButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ok"));
			okButton.addActionListener(this);
			cancelButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.cancel"));
			cancelButton.addActionListener(this);
		}
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		if (!viewOnly) {
			buttonPanel.add(okButton);
		}
		buttonPanel.add(cancelButton);
		return buttonPanel;
	}

	/**
	 * Action event handling
	 * @param aet the calling action event
	 */
	public void actionPerformed(ActionEvent aet) {
		if (aet.getSource() == okButton) {
			if (transformer != null) {
				//this will throw an excption if it's not being edited!
				transformer.getWsNamespace();
				transformer.setResultClass(resultClassField.getText());
				transformer.setViewableOutput(viewableOutputBox.isSelected());
				transformer.setWsOperation(operationField.getText());
				transformer.setWsUrl(urlField.getText());
				transformer.setWsNamespace(wsnamespaceField.getText());
			} else {
				Transformer transformer = ElementUtility.createITransformer(wsnamespaceField.getText(),
                        operationField.getText(),
                        urlField.getText(),
                        resultClassField.getText(),
                        viewableOutputBox.isSelected());
				((DefaultListModel)transformerList.getModel()).addElement(transformer);
			}
			this.dispose();
		} else if (aet.getSource() == cancelButton) {
			this.dispose();
		}
	}
	
	private void setFieldsEnabled(boolean enabled) {
		wsnamespaceField.setEnabled(enabled && !viewOnly);
		resultClassField.setEnabled(enabled && !viewOnly);
		operationField.setEnabled(enabled && !viewOnly);
		urlField.setEnabled(enabled && !viewOnly) ;
		viewableOutputBox.setEnabled(enabled && !viewOnly);
	}

	private void resetFields() {
		wsnamespaceField.setText("");
		resultClassField.setText("");
		operationField.setText("");
		urlField.setText("");
		viewableOutputBox.setSelected(false);
	}
	
	private class TypeSelectionChangedListener implements ActionListener{
		
		public void actionPerformed(ActionEvent e) {
			if (transformerType.getSelectedItem().equals(DATE_TRANSFORMER)) {
				wsnamespaceField.setText("urn:transformers.psygrid.org");
				operationField.setText("getMonthAndYear");
				urlField.setText(PropertiesHelper.getTransformersLocation() + "/transformers/services/datetransformer");
				resultClassField.setText("org.psygrid.data.model.hibernate.DateValue");
				viewableOutputBox.setSelected(true);
				setFieldsEnabled(false);
			} else if (transformerType.getSelectedItem().equals(POSTCODE_TRANSFORMER)) {
				wsnamespaceField.setText("urn:transformers.psygrid.org");
				operationField.setText("getSOA");
				urlField.setText(PropertiesHelper.getTransformersLocation()+ "/transformers/services/postcodetransformer");
				resultClassField.setText("org.psygrid.data.model.hibernate.TextValue");
				setFieldsEnabled(false);
				viewableOutputBox.setSelected(true);
			} else if (transformerType.getSelectedItem().equals(OPCRIT_TRANSFORMER)) {
				wsnamespaceField.setText("urn:transformers.psygrid.org");
				operationField.setText("opcrit");
				urlField.setText(PropertiesHelper.getTransformersLocation() + "/transformers/services/externaltransformer");
				resultClassField.setText("java.lang.String");
				setFieldsEnabled(false);
				viewableOutputBox.setSelected(true);
			} else if (transformerType.getSelectedItem().equals(TYRERCUZICK_TRANSFORMER)) {
				wsnamespaceField.setText("urn:transformers.psygrid.org");
				operationField.setText("tyrercuzick");
				urlField.setText("http://psygridwin.opencdms.org/transformers/services/externaltransformer");
				resultClassField.setText("java.lang.String");
				setFieldsEnabled(false);
				viewableOutputBox.setSelected(true);
			} else {
				resetFields();
				setFieldsEnabled(true);
			}
		}
	}
	
}
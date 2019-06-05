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

import javax.swing.DefaultListModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.psygrid.data.model.hibernate.Unit;
import org.psygrid.datasetdesigner.controllers.DatasetController;
import org.psygrid.datasetdesigner.renderer.OptionListCellRenderer;
import org.psygrid.datasetdesigner.utils.HelpHelper;
import org.psygrid.datasetdesigner.utils.ListModelUtility;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.SpringUtilities;
import org.psygrid.datasetdesigner.utils.ElementUtility;
import org.psygrid.datasetdesigner.custom.TextFieldWithStatus;

public class AddUnitDialog extends JDialog implements ActionListener {

	private JButton okButton;
	private JButton cancelButton;

	private TextFieldWithStatus abbreviationField;
	private TextFieldWithStatus descriptionField;
	private TextFieldWithStatus factorField;

	private JList list;
	
	private Unit unit;

	private JComboBox baseUnitComboBox;
	private DefaultComboBoxModel baseUnitComboBoxModel;

	private boolean viewOnly;

	public AddUnitDialog(JDialog parentDialog, JList list) {
		this(parentDialog, list, null);
	}

	public AddUnitDialog(JDialog parentDialog, JList list, boolean viewOnly) {
		this(parentDialog, list, null, viewOnly);
	}
	
	public AddUnitDialog(JDialog parentDialog, JList list, Unit unit) {
		this(parentDialog, list, unit, false);
	}
	
	public AddUnitDialog(JDialog parentDialog, JList list, Unit unit, boolean viewOnly) {
		super(parentDialog);
		if (unit == null) {
			setTitle(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.addunit"));
		}else {
			setTitle(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.editunit"));
		}
		this.list = list;
		this.unit = unit;
		this.viewOnly = viewOnly;
		setModal(true);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(buildMainPanel(), BorderLayout.CENTER);
		getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
		pack();
		init();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void init() {
		baseUnitComboBoxModel = ListModelUtility.convertListToComboModel(DatasetController.getInstance().getActiveDs().getUnits());
		baseUnitComboBoxModel.addElement(" ");
		baseUnitComboBoxModel.removeElement(unit);
		baseUnitComboBox.setModel(baseUnitComboBoxModel);
		baseUnitComboBox.setEnabled(!viewOnly);
		
		if (unit != null) {
			descriptionField.setText(unit.getDescription());
			abbreviationField.setText(unit.getAbbreviation());

			if (unit.getFactor() != null) {
				factorField.setText(unit.getFactor().toString());
			}

			if (unit.getBaseUnit() == null) {
				baseUnitComboBoxModel.setSelectedItem(" ");
			} else  {
				baseUnitComboBoxModel.setSelectedItem(unit.getBaseUnit());
			}
		}
	}

	private JPanel buildMainPanel() {
		JPanel mainPanel = new JPanel(new SpringLayout());

		abbreviationField = new TextFieldWithStatus(20, true);
		abbreviationField.setEditable(!viewOnly);
		descriptionField = new TextFieldWithStatus(20, false);
		descriptionField.setEditable(!viewOnly);

		factorField = new TextFieldWithStatus(20, false);
		factorField.setEditable(!viewOnly);
		baseUnitComboBox = new JComboBox();
		baseUnitComboBox.setEditable(false);
		baseUnitComboBox.setRenderer(new OptionListCellRenderer());

		mainPanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsdunitsabbr"));
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.abbreviation")));
		mainPanel.add(abbreviationField);
		
		mainPanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsdunitsdescription"));
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.description")));
		mainPanel.add(descriptionField);

		mainPanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsdunitsbaseunit"));
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.baseunit")));
		mainPanel.add(baseUnitComboBox);

		mainPanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsdunitsfactor"));
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.factor")));
		mainPanel.add(factorField);

		SpringUtilities.makeCompactGrid(mainPanel,
                4, 3, //rows, cols
				6, 6,        //initX, initY
				6, 6);       //xPad, yPad

		return mainPanel;
	}

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

	public boolean validateEntries() {
		String abbreviation = abbreviationField.getText();

		if (abbreviation == null || abbreviation.equals("")) {
			JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.abbreviationnonempty"));
			return false;
		}

		try {
			if ((factorField.getText() != null) && !(factorField.getText().equals(""))){
				new Double(factorField.getText());
			}
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.factorfloatingpoint"));
			return false;
		}

		return true;
	}

	public void actionPerformed(ActionEvent aet) {
		if (aet.getSource() == okButton) {
			if (validateEntries()) {
				Unit baseUnit;
				if (baseUnitComboBox.getSelectedItem() == null || baseUnitComboBox.getSelectedItem().equals(" ") ) {
					baseUnit = null;
				} else {
					baseUnit = (Unit)baseUnitComboBox.getSelectedItem();
				}
				
				if (unit == null) {
					Unit newUnit = ElementUtility.createIUnit(abbreviationField.getText(),
                            descriptionField.getText(),
                            baseUnit,
                            factorField.getText());
					((DefaultListModel)list.getModel()).addElement(newUnit);
				} else {
					//if not editing, this will throw an exception;
					unit.getAbbreviation();
					unit.setAbbreviation(abbreviationField.getText());
					unit.setDescription(descriptionField.getText());
					if (factorField.getText() == null || factorField.getText().equals("")) {
						unit.setFactor(null);
					} else {
						unit.setFactor(new Double(factorField.getText()));
					}
					unit.setBaseUnit(baseUnit);
				}
				this.dispose();
			}

		} else if (aet.getSource() == cancelButton) {
			this.dispose();
		}
	}

}
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
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.psygrid.data.model.hibernate.HibernateFactory;
import org.psygrid.data.model.hibernate.Option;import org.psygrid.datasetdesigner.ui.editdialogs.OptionEditDialog;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.SpringUtilities;
import org.psygrid.datasetdesigner.custom.TextFieldWithStatus;

public class AddOptionDialog extends JDialog implements ActionListener {

	private JButton okButton;
	private JButton cancelButton;

	private JList optionList;
	private Option option = null;

	private TextFieldWithStatus optionNameField;
	private TextFieldWithStatus optionDisplayField;
	private TextFieldWithStatus optionCodeField;
	private JCheckBox textEntryAllowed;

	private JDialog parentDialog;

	public AddOptionDialog(JDialog parentDialog, 
			JList optionList) {
		super(parentDialog, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.addoption"));
		this.parentDialog = parentDialog;
		this.optionList = optionList;
		setModal(true);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(buildMainPanel(), BorderLayout.CENTER);
		getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public AddOptionDialog(JDialog parentDialog, 
			JList optionList, 
			Option option) {
		super(parentDialog, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.editoption"));
		this.parentDialog = parentDialog;
		this.optionList = optionList;
		this.option = option;
		setModal(true);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(buildMainPanel(), BorderLayout.CENTER);
		getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
		pack();
		init(option);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void init(Option option) {
		if (option.getName() != null) {
			optionNameField.setText(option.getName());
		}

		if (option.getDisplayText() != null) {
			optionDisplayField.setText(option.getDisplayText());
		}

		if (option.getCode() != null) {
			optionCodeField.setText(option.getCode().toString());
		}

		textEntryAllowed.setSelected(option.isTextEntryAllowed());
		
	}

	private JPanel buildMainPanel() {
		JPanel adderPanel = new JPanel();
		adderPanel.setLayout(new SpringLayout());
		optionNameField = new TextFieldWithStatus(80, true);
		optionDisplayField = new TextFieldWithStatus(80, true);
		optionCodeField = new TextFieldWithStatus(80, false);
		textEntryAllowed = new JCheckBox();
		adderPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.optionname")));
		adderPanel.add(optionNameField);
		adderPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.optiondisplaytext")));
		adderPanel.add(optionDisplayField);
		adderPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.optioncode")));
		adderPanel.add(optionCodeField);
		adderPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.textentryallowed")));
		adderPanel.add(textEntryAllowed);
		SpringUtilities.makeCompactGrid(adderPanel,
				4, 2, //rows, cols
				6, 6,        //initX, initY
				6, 6);       //xPad, yPad


		return adderPanel;
	}

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

	public boolean validateOptionCode() {
		for (int i=0; i<optionList.getModel().getSize(); i++) {
			Option curOption = ((Option)optionList.getModel().getElementAt(i));
			if (curOption.getCode() != null 
					&& !(curOption.getCode().equals("")) 
					&& (curOption.getCode().toString().equals(optionCodeField.getText()))
					&& (curOption != option)) {
				int response = JOptionPane.showConfirmDialog(this,
						PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.optioncodeexists"), 
						PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.optioncodeexiststitle"), 
						JOptionPane.YES_NO_OPTION);
				if (response == JOptionPane.NO_OPTION) {
					 return false;
				}
			}
		}
		
		return true;
	}

	
	public boolean validateEntries() {
		if (optionNameField.getText() == null || optionNameField.getText().equals("")) {
			JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.nonemptyoption"));
			return false;
		}

		if (optionDisplayField.getText() == null || optionDisplayField.getText().equals("")) {
			JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.nonemptyoptiondisplaytext"));
			return false;
		}

		if (optionCodeField.getText() == null || optionCodeField.getText().equals("")) {
			JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.nonemptyoptioncodenonempty"));
			return false;
		}
		
		if (optionCodeField.getText() != null && !optionCodeField.getText().equals("")) {
			try {
				new Integer(optionCodeField.getText());
			} catch (NumberFormatException nex) {
				JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.optioncodetypeinteger"));
				return false;
			}
		}

		for (int i=0; i<optionList.getModel().getSize(); i++) {
			Option curOption = (Option)optionList.getModel().getElementAt(i);
			if (!curOption.equals(option)) {
				if (curOption.getName() != null && curOption.getName().equals(optionNameField.getText())) {
					JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.optionalreadyexists"));
					return false;
				}
				if (curOption.getDisplayText() != null && curOption.getDisplayText().equals(optionDisplayField.getText())) {
					JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.optiondisplaytextexists"));
					return false;
				}
			}
		}

		return true;
	}

	public void actionPerformed(ActionEvent aet) {
		if (aet.getSource() == okButton) {
			if (validateEntries() && validateOptionCode()) {
				OptionEditDialog dialog = (OptionEditDialog)parentDialog;
				try {
					//TODO check fields
					if (dialog.fieldChanged(option.getName(),optionNameField.getText())) {
						dialog.setChanged(true);
						option.setName(optionNameField.getText());
					}
					if (dialog.fieldChanged(option.getDisplayText(),optionDisplayField.getText())) {
						dialog.setChanged(true);
						option.setDisplayText(optionDisplayField.getText());
					}

					if (option.isTextEntryAllowed() != textEntryAllowed.isSelected()) {
						dialog.setChanged(true);
						option.setTextEntryAllowed(textEntryAllowed.isSelected());
					}

					if (dialog.fieldChanged(option.getCode(), new Integer(optionCodeField.getText()))) {
						dialog.setChanged(true);
						try {
							option.setCode(new Integer(optionCodeField.getText()));
						} catch (NumberFormatException ex) {
							option.setCode(null);
						}
					}
				} catch (Exception ex) {
					//Create a new option
					HibernateFactory factory = new HibernateFactory();
					Option option = factory.createOption(optionNameField.getText(),
							optionDisplayField.getText());
					try {
						option.setCode(new Integer(optionCodeField.getText()));
					} catch (NumberFormatException nfe) {
						option.setCode(null);
					}

					option.setTextEntryAllowed(textEntryAllowed.isSelected());
					((DefaultListModel)optionList.getModel()).addElement(option);
					
					dialog.setChanged(true);
				}
				this.dispose();
			}
		} else if (aet.getSource() == cancelButton) {
			this.dispose();
		}
	}

}
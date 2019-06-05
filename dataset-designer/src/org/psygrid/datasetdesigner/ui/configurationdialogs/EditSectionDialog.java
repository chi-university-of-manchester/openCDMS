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

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.Section;
import org.psygrid.data.model.hibernate.SectionOccurrence;
import org.psygrid.datasetdesigner.ui.DocumentPanel;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.SpringUtilities;
import org.psygrid.datasetdesigner.custom.TextFieldWithStatus;

/**
 * Dialog to edit sections
 * 
 * @author pwhelan
 */
public class EditSectionDialog extends JDialog implements ActionListener {
	
	private Document document;
	
	//actions buttons
	private JButton okButton;
	private JButton cancelButton;
	
	//name, display text and description fields 
	private TextFieldWithStatus nameField;
	private TextFieldWithStatus displayTextField;
	private TextFieldWithStatus descriptionField;
	
	//list of configured sections
	private JList sectionList;
	
	private JCheckBox multipleSectionsAllowedBox;
	
	/**
	 * Constructor
	 * @param frame the main frame of the application
	 * @param document the document currently open
	 * @param docPanel the document panel open
	 * @param sectionList the list of existing sections
	 */
	public EditSectionDialog(JFrame frame, Document document, DocumentPanel docPanel, JList sectionList) {
		super(frame);
		setTitle(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.editSection"));
		this.sectionList = sectionList;
		this.document = document;
		setModal(true);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(buildMainPanel(), BorderLayout.CENTER);
		getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
		pack();
		init();
		setLocationRelativeTo(null);  
		setVisible(true);
	}
	
	/**
	 * Set the selected section fields
	 */
	private void init() {
		Section section = (Section)sectionList.getSelectedValue();
		nameField.setText(section.getName());
		displayTextField.setText(section.getDisplayText());
		descriptionField.setText(section.getDescription());
		
		//at this point, there should only ever be one section occurence
		for (int i=0; i<section.numOccurrences(); i++) {
			SectionOccurrence secOcc = section.getOccurrence(i);
			multipleSectionsAllowedBox.setSelected(secOcc.isMultipleAllowed());
		}
	}
	
	/**
	 * Create the main panel with name, 
	 * display text fields etc.
	 * @return the configured main panel
	 */
	private JPanel buildMainPanel() {
		JPanel mainPanel = new JPanel(new SpringLayout());
		mainPanel.setLayout(new SpringLayout());
		
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.sectionName")));
		nameField = new TextFieldWithStatus(15, true);
		mainPanel.add(nameField);
		
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.sectiondisplaytext")));
		displayTextField = new TextFieldWithStatus(15, true);
		mainPanel.add(displayTextField);
		
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.sectiondescription")));
		descriptionField = new TextFieldWithStatus(40, false);
		mainPanel.add(descriptionField);
		
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.multiplesectionsallowed")));
		multipleSectionsAllowedBox = new JCheckBox();
		mainPanel.add(multipleSectionsAllowedBox);
		
		SpringUtilities.makeCompactGrid(mainPanel,
                4, 2, 			//rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad

		return mainPanel;
	}
	
	/**
	 * Build the button panel with ok and 
	 * cancel buttons
	 * @return the configured button panel
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
	
	/**
	 * Set the name and display text of the buttons
	 * @param aet the calling action event
	 */
	public void actionPerformed(ActionEvent aet) {
		if (aet.getSource() == okButton) {
			boolean changed = false;
			Section section = ((Section)sectionList.getSelectedValue());
			if (!section.getName().equals(nameField.getText())) {
				changed = true;
				section.setName(nameField.getText());	
			}
			if (!section.getDisplayText().equals(displayTextField.getText())) {
				changed = true;
				section.setDisplayText(displayTextField.getText());	
			}
			if (!section.getDescription().equals(descriptionField.getText())) {
				changed = true;
				section.setDescription(descriptionField.getText());	
			}
			
			if (section.getOccurrence(0).isMultipleAllowed() != multipleSectionsAllowedBox.isSelected()) {
				changed = true;
				section.getOccurrence(0).setMultipleAllowed(multipleSectionsAllowedBox.isSelected());	
			}
			if (changed) {
				((Document)document).setIsRevisionCandidate(true);
			}
			this.dispose();
		} else if (aet.getSource() == cancelButton) {
			this.dispose();
		}
	}
	
}

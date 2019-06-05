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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.Section;
import org.psygrid.datasetdesigner.ui.DocumentPanel;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.ElementUtility;
import org.psygrid.datasetdesigner.utils.SpringUtilities;
import org.psygrid.datasetdesigner.custom.TextFieldWithStatus;

public class AddSectionDialog extends JDialog implements ActionListener {
		
	private Document document;
	
	private JButton okButton;
	private JButton cancelButton;
	
	private TextFieldWithStatus nameField;
	private TextFieldWithStatus displayTextField;
	private TextFieldWithStatus descriptionField;
	
	private JList sectionList;
	
	private JCheckBox multipleSectionsAllowedBox;
	
	public AddSectionDialog(JFrame frame, Document document, DocumentPanel docPanel, JList sectionList) {
		super(frame);
		setTitle(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.addsection"));
		this.sectionList = sectionList;
		this.document = document;
		setModal(true);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(buildMainPanel(), BorderLayout.CENTER);
		getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	private JPanel buildMainPanel() {
		JPanel mainPanel = new JPanel(new SpringLayout());
		mainPanel.setLayout(new SpringLayout());
		
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.sectionname")));
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
	
	public boolean validateEntries() {
		if (nameField.getText() == null || nameField.getText().equals("")) {
			JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.sectionnamenonempty"));
			return false;
		}

		if (displayTextField.getText() == null || displayTextField.getText().equals("")) {
			JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.sectiondisplaytextnonempty"));
			return false;
		}
		
		for (int i=0; i<sectionList.getModel().getSize(); i++) {
			if(((Section)sectionList.getModel().getElementAt(i)).getName().equalsIgnoreCase(nameField.getText())) {
				JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.sectionamealreadyexists"));
				return false;
			} else if (((Section)sectionList.getModel().getElementAt(i)).getDisplayText().equalsIgnoreCase(displayTextField.getText())) {
				JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.sectiondisplaytextalreadyexists"));
				return false;
			}
		}
		
		return true;
	}
	
	public void actionPerformed(ActionEvent aet) {
		if (aet.getSource() == okButton) {
			if (validateEntries()) {
				((DefaultListModel)sectionList.getModel()).addElement(ElementUtility.createISection(nameField.getText(), 
					      displayTextField.getText(), descriptionField.getText(), multipleSectionsAllowedBox.isSelected()));
				((Document)document).setIsRevisionCandidate(true);	//Mark as revised because section was added
				this.dispose();
			}
		} else if (aet.getSource() == cancelButton) {
			this.dispose();
		}
	}
	
}
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
package org.psygrid.datasetdesigner.ui.editdialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import java.util.Vector;

import org.psygrid.datasetdesigner.utils.SpringUtilities;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.custom.TextFieldWithStatus;

public class SectionEditDialog extends JDialog implements ActionListener {

	private TextFieldWithStatus nameField;
	private TextFieldWithStatus displayTextField;
	private TextFieldWithStatus sectionDescriptionField;
	private JCheckBox multipleSectionsAllowed;
	
	private JButton okButton;
	private JButton cancelButton;
	
	public Vector okListeners = new Vector();
	
	public SectionEditDialog(JFrame frame) {
		super(frame);
		System.out.println("SED");
		setTitle(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.sectioneditdialog"));
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(buildMainPanel(), BorderLayout.CENTER);
		getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
		pack();
		setLocationRelativeTo(null);  
	}
	
	private JPanel buildMainPanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new SpringLayout());
		
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.sectionName")));
		nameField = new TextFieldWithStatus(15, true);
		mainPanel.add(nameField);
		
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.sectiondisplaytext")));
		displayTextField = new TextFieldWithStatus(15, true);
		mainPanel.add(displayTextField);

		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.sectiondescription")));
		sectionDescriptionField = new TextFieldWithStatus(15, false);
		mainPanel.add(sectionDescriptionField);
		
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.multiplesectionsallowed")));
		multipleSectionsAllowed = new JCheckBox();
		mainPanel.add(multipleSectionsAllowed);
		
		SpringUtilities.makeCompactGrid(mainPanel,
                4, 2, 			//rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad

		return mainPanel;
	}
	
	public void addOkListener(ActionListener okl) {
		okListeners.add(okl);
	}
	
	public void removeOkListener(ActionListener okl) {
		okListeners.remove(okl);
	}
	
	public String getName() {
		return nameField.getText();
	}
	
	public String getDisplayText() {
		return displayTextField.getText();
	}
	
	public String getDescriptionText()  {
		return sectionDescriptionField.getText();
	}
	
	public boolean isMulitpleSectionsAllowed() {
		return multipleSectionsAllowed.isSelected();
	}
	
	private JPanel buildButtonPanel() {
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

	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == okButton) {
			for (int i=0; i<okListeners.size(); i++) {
				ActionListener okListener = (ActionListener)okListeners.get(i);
				okListener.actionPerformed(e);
			}
		} else if (e.getSource () == cancelButton) {
			this.dispose();
		}
		
	}
	
}

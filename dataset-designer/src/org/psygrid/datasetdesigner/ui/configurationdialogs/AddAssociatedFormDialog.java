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

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.psygrid.data.model.hibernate.AssociatedConsentForm;
import org.psygrid.datasetdesigner.custom.SizeConstrainedTextArea;

import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.ElementUtility;

public class AddAssociatedFormDialog extends JDialog implements ActionListener {

	private JButton okButton;
	private JButton cancelButton;
	
	private SizeConstrainedTextArea consentArea;
	
	private JTextField referenceField;
	
	private JList associatedList;
	
	public AddAssociatedFormDialog(JDialog parentDialog, JComboBox datasetBox, JList associatedList) {
		super(parentDialog, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.addassociate"));
		this.associatedList = associatedList;
		setModal(true);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(buildMainPanel(), BorderLayout.CENTER);
		getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private JPanel buildMainPanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createTitledBorder(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.associatedconsent")));
		consentArea = new SizeConstrainedTextArea(512);
		consentArea.setWrapStyleWord(true);
		consentArea.setPreferredSize(new Dimension(100, 100));
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.associatedconsentform")), BorderLayout.NORTH);
		mainPanel.add(consentArea, BorderLayout.CENTER);
		JPanel refPanel = new JPanel();
		refPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		refPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.enterrefnumber")));
		referenceField = new JTextField(40);
		refPanel.add(referenceField);
		mainPanel.add(refPanel, BorderLayout.SOUTH);
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
	
	public void actionPerformed(ActionEvent aet) {
		if (aet.getSource() == okButton) {
			AssociatedConsentForm associatedForm = ElementUtility.createIAssociatedConsentForm(
                    consentArea.getText(), referenceField.getText());
			((DefaultListModel)associatedList.getModel()).addElement(associatedForm);
			this.dispose();
		} else if (aet.getSource() == cancelButton) {
			this.dispose();
		}
	}
	
}
	
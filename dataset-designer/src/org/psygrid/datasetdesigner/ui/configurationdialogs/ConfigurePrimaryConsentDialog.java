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
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.psygrid.data.model.hibernate.ConsentFormGroup;
import org.psygrid.data.model.hibernate.AssociatedConsentForm;
import org.psygrid.data.model.hibernate.PrimaryConsentForm;
import org.psygrid.datasetdesigner.actions.AddConsentAction;
import org.psygrid.datasetdesigner.actions.RemoveConsentAction;
import org.psygrid.datasetdesigner.renderer.OptionListCellRenderer;
import org.psygrid.datasetdesigner.utils.ListModelUtility;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.SpringUtilities;
import org.psygrid.datasetdesigner.utils.ElementUtility;
import org.psygrid.datasetdesigner.custom.TextFieldWithStatus;

public class ConfigurePrimaryConsentDialog extends JDialog implements ActionListener {

	private JButton okButton;
	private JButton cancelButton;
	
	private JButton editButton;
	private JButton removeButton;
	
	private JCheckBox eslCheckBox;
	
	private JList consentList;
	
	private JList consentGroupList;
	
	private TextFieldWithStatus descriptionField;
	
	private boolean edit = false;
	
	public ConfigurePrimaryConsentDialog(JDialog parentDialog,
										  JList consentGroupList										  ) {
		super(parentDialog, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.configureprimaryconsent"));
		this.consentGroupList = consentGroupList;
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(buildHeaderPanel(), BorderLayout.NORTH);
		getContentPane().add(buildUnitsPanel(), BorderLayout.CENTER);
		getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
		setModal(true);
		init();
		pack();
		setLocationRelativeTo(null);  
		setVisible(true);
	}

	public ConfigurePrimaryConsentDialog(JDialog parentDialog,
									   JList consentGroupList,
									   boolean edit) {
		super(parentDialog, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.configureprimaryconsent"));
		this.consentGroupList = consentGroupList;
		this.edit = edit;
		setModal(true);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(buildHeaderPanel(), BorderLayout.NORTH);
		getContentPane().add(buildUnitsPanel(), BorderLayout.CENTER);
		getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
		init();
		pack();
		setLocationRelativeTo(null);  
		setVisible(true);
	}
	
	private void init() {
		if (edit) {
			ConsentFormGroup icfg = (ConsentFormGroup)consentGroupList.getSelectedValue();
			descriptionField.setText(icfg.getDescription());
			DefaultListModel consentModel = new DefaultListModel();
			for (int i=0; i<icfg.numConsentForms(); i++) {
				PrimaryConsentForm pcf = (PrimaryConsentForm)icfg.getConsentForm(i);
				consentModel.addElement(pcf);
				for (AssociatedConsentForm acf: pcf.getAssociatedConsentForms()) {
					consentModel.addElement(acf);
				}
			}
			consentList.setModel(consentModel);
			eslCheckBox.setSelected(icfg.isEslTrigger());
		}
	
	}
	
	private JPanel buildHeaderPanel() {
		JPanel headerPanel = new JPanel();
		headerPanel.setLayout(new SpringLayout());
		
		headerPanel.setBorder(BorderFactory.createTitledBorder(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.consentformproperties")));
		
		headerPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.consentformgroupdescription")));
		descriptionField = new TextFieldWithStatus(20, false);
		headerPanel.add(descriptionField);
		
		headerPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.esltrigger")));
		eslCheckBox = new JCheckBox();
		headerPanel.add(eslCheckBox);
		
		SpringUtilities.makeCompactGrid(headerPanel,
                2, 2, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad

		
		return headerPanel;
	}
	
	private JPanel buildUnitsPanel() {
		JPanel unitsPanel = new JPanel();
		unitsPanel.setBorder(BorderFactory.createTitledBorder(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.primaryconsentformmgmt")));
		unitsPanel.setLayout(new BorderLayout());
		consentList = new JList();
		consentList.setModel(new DefaultListModel());
		consentList.setCellRenderer(new OptionListCellRenderer());
		JScrollPane consentPane = new JScrollPane(consentList);
		consentPane.setMinimumSize(new Dimension(300, 300));
		consentPane.setPreferredSize(new Dimension(300, 300));
		consentPane.setMaximumSize(new Dimension(300, 300));
		
		unitsPanel.add(buildAddRemovePanel(), BorderLayout.NORTH);
		unitsPanel.add(consentPane, BorderLayout.CENTER);
		return unitsPanel;
	}
	
	private JPanel buildAddRemovePanel() {
		JPanel addRemovePanel = new JPanel();
		addRemovePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		addRemovePanel.add(new JButton(new AddConsentAction(this, consentList)));
		editButton = new JButton(new AddConsentAction(this, consentList, true));
		editButton.setEnabled(false);
		addRemovePanel.add(editButton);
		removeButton = new JButton(new RemoveConsentAction(this, consentList));
		removeButton.setEnabled(false);
		addRemovePanel.add(removeButton);
		
		//Only enable the buttons if a consent is selected
		consentList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				boolean enabled = true;
				if (consentList == null || consentList.getSelectedValue() == null) {
					enabled = false;
				}
				if (removeButton != null) { removeButton.setEnabled(enabled); }
				if (editButton   != null) { editButton.setEnabled(enabled);   }
			}
		});
		return addRemovePanel;
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
		if (descriptionField.getText() == null || descriptionField.getText().equals("")) {
			JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.consentformdescription"));
			return false;
		}
		
		return true;
	}
	
	public void actionPerformed(ActionEvent aet) {
		if (aet.getSource() == okButton) {
			if (validateEntries()) {
				ArrayList<PrimaryConsentForm> consents = ListModelUtility.convertListModelToPrimaryConsentFormList((DefaultListModel)consentList.getModel());
				ConsentFormGroup icfg;
				
				if (edit) {
					icfg = (ConsentFormGroup)consentGroupList.getSelectedValue();
				} else {
					icfg = ElementUtility.createIConsentFormGroup();
				}
				
				icfg.setDescription(descriptionField.getText());
				icfg.setEslTrigger(eslCheckBox.isSelected());
				icfg.setConsentForms(consents);

				if (!edit) {
					((DefaultListModel)consentGroupList.getModel()).addElement(icfg);
				}
				this.dispose();
			}
		} else if (aet.getSource() == cancelButton) {
			this.dispose();
		}
	}
	
}

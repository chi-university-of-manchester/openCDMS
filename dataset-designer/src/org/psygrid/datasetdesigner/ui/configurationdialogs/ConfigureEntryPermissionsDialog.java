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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.datasetdesigner.controllers.DatasetController;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.Utils;
import org.psygrid.security.RBACAction;


/**
 * Configure the permissions to be given to a particular entry
 *
 * @author Lucy Bridges
 */
public class ConfigureEntryPermissionsDialog extends AbstractConfigurePermissionsDialog implements ActionListener {

	private static final long serialVersionUID = 5048077423507691223L;

	/**
	 * The entry
	 */
	private Entry entry;

	/**
	 * The access action of the entry before editing so that changes can be detected.
	 */
	private String originalAccessAction;

	/**
	 * The access action of the entry's response before editing so that changes can be detected.
	 */
	private String originalRespAccessAction;

	/**
	 * The edit action of the entry before editing so that changes can be detected.
	 */
	private String originalEditAction;

	/**
	 * The edit action of the entry's response before editing so that changes can be detected.
	 */
	private String originalRespEditAction;

	/**
	 * Creates a new ConfigureEntryPermissionsDialog object.
	 *
	 * @param parent
	 * @param entry
	 */
	public ConfigureEntryPermissionsDialog(JDialog parent, Entry entry) {
		this(parent, entry, true);
	}

	/**
	 * Creates a new ConfigureReportsDialog object.
	 *
	 * @param parent
	 * @param entry
	 * @param readOnly
	 */
	public ConfigureEntryPermissionsDialog(JDialog parent, Entry entry, boolean readOnly) {
		super(parent, readOnly);
		if (readOnly) {
			setTitle(PropertiesHelper.getStringFor(
			"org.psygrid.datasetdesigner.ui.viewentrypermissions"));
		} else {
			setTitle(PropertiesHelper.getStringFor(
			"org.psygrid.datasetdesigner.ui.configureentrypermissions"));
		}
		this.entry = entry;
		init();
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(Utils.buildDsHeaderPanel(), BorderLayout.NORTH);
		getContentPane().add(buildPanels(), BorderLayout.CENTER);
		getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
		pack();
		setLocationRelativeTo(parent);
		setVisible(true);
	}

	/**
	 * Bulid the panels
	 *
	 * @return 
	 */
	private JPanel buildPanels() {
		JPanel mainPanel = new JPanel(new BorderLayout());

		JPanel viewActionPanel = createActionPanel(VIEW);
		JPanel editActionPanel = createActionPanel(EDIT);
		mainPanel.add(viewActionPanel, BorderLayout.NORTH);
		mainPanel.add(editActionPanel, BorderLayout.CENTER);
		mainPanel.setBorder(BorderFactory.createTitledBorder(
				PropertiesHelper.getStringFor(
				"org.psygrid.datasetdesigner.ui.configureentrypermissions")));

		return mainPanel;
	}

	private JPanel createActionPanel(final String actionType) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		//Create the radio buttons.
		final JRadioButton defaultOption = new JRadioButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.configuredefaultpermissions"));
		defaultOption.setSelected(true);
		final JRadioButton customOption = new JRadioButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.configurecustompermissions"));
		//Group the radio buttons.
		ButtonGroup group = new ButtonGroup();
		group.add(defaultOption);
		group.add(customOption);

		final JRadioButton blindCustomOption = new JRadioButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.configureblindpermissions"));
		blindCustomOption.setEnabled(false);
		final JRadioButton otherCustomOption = new JRadioButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.configureallpermissions"));
		otherCustomOption.setEnabled(false);
		ButtonGroup customGroup = new ButtonGroup();
		customGroup.add(blindCustomOption);
		customGroup.add(otherCustomOption);

		if (VIEW.equals(actionType)) {
			if (entry.getAccessAction() == null || RBACAction.ACTION_DR_DOC_STANDARD.toString().equals(entry.getAccessAction())) {
				defaultOption.setSelected(true);	
				defaultOption.setEnabled(true);	
			}
			else if (RBACAction.ACTION_DR_DOC_BLIND.toString().equals(entry.getAccessAction())) {
				blindCustomOption.setSelected(true);
				blindCustomOption.setEnabled(true);
				customOption.setSelected(true);
				customOption.setEnabled(true);
				otherCustomOption.setEnabled(true);
			}
			else {
				otherCustomOption.setSelected(true);
				otherCustomOption.setEnabled(true);
				customOption.setSelected(true);
				customOption.setEnabled(true);
				blindCustomOption.setEnabled(true);
			}
		}
		else if (EDIT.equals(actionType)) {
			if (entry.getEditableAction() == null || RBACAction.ACTION_DR_EDIT_ENTRY.toString().equals(entry.getEditableAction())) {
				defaultOption.setSelected(true);	
				defaultOption.setEnabled(true);		
			}
			else if (RBACAction.ACTION_DR_DOC_BLIND.toString().equals(entry.getEditableAction())) {
				blindCustomOption.setSelected(true);
				blindCustomOption.setEnabled(true);
				customOption.setSelected(true);
				customOption.setEnabled(true);
				otherCustomOption.setEnabled(true);
			}
			else {
				otherCustomOption.setSelected(true);
				otherCustomOption.setEnabled(true);
				customOption.setSelected(true);
				customOption.setEnabled(true);
				blindCustomOption.setEnabled(true);
			}
		}

		String title = "";
		if (VIEW.equals(actionType)) {
			title = PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.configureentrypermissionsview");
		}
		else {
			title = PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.configureentrypermissionsedit");
		}
		panel.setBorder(BorderFactory.createTitledBorder(title));

		panel.add(defaultOption);
		panel.add(new JLabel("     Use the same permissions as the parent document."));

		panel.add(Box.createRigidArea(new Dimension(0,5)));
		panel.add(customOption);

		JPanel minipanel = new JPanel();
		minipanel.setLayout(new BoxLayout(minipanel, BoxLayout.Y_AXIS));
		minipanel.setBorder(BorderFactory.createEmptyBorder(5, 25, 5, 10));
		minipanel.add(blindCustomOption);
		if (VIEW.equals(actionType)) {
			minipanel.add(new JLabel("     This will 'hide' responses to the entry from anyone who is NOT a TA or researcher."));
		}
		else if (EDIT.equals(actionType)) {
			minipanel.add(new JLabel("     This will stop the entry from being edited by anyone who is NOT a TA."));
		}
		if (VIEW.equals(actionType)) {
			minipanel.add(otherCustomOption);
			minipanel.add(new JLabel("     This will allow CROs, CPMs, TAs and researchers to view the entry."));
		}
		else if (EDIT.equals(actionType)) {
			minipanel.add(otherCustomOption);
			minipanel.add(new JLabel("     This will allow CROs and TAs to edit the entry."));
		}
		panel.add(minipanel);

		defaultOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				otherCustomOption.setEnabled(false);
				blindCustomOption.setEnabled(false);
				if (VIEW.equals(actionType)) {
					entry.setAccessAction(RBACAction.ACTION_DR_DOC_STANDARD);
					entry.setResponseAccessAction(RBACAction.ACTION_DR_DOC_STANDARD_INST);
				}
				else if (EDIT.equals(actionType)) {
					entry.setEditableAction(RBACAction.ACTION_DR_EDIT_ENTRY);
					entry.setResponseEditableAction(RBACAction.ACTION_DR_EDIT_ENTRY_RESPONSE);
				}
				ConfigureEntryPermissionsDialog.this.okButton.setEnabled(true);
			}
		});
		customOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				otherCustomOption.setEnabled(true);
				blindCustomOption.setEnabled(true);
				ConfigureEntryPermissionsDialog.this.okButton.setEnabled(false);
			}
		});
		blindCustomOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (VIEW.equals(actionType)) {
					entry.setAccessAction(RBACAction.ACTION_DR_DOC_BLIND);
					entry.setResponseAccessAction(RBACAction.ACTION_DR_DOC_BLIND_INST);
				}
				else if (EDIT.equals(actionType)) {
					entry.setEditableAction(RBACAction.ACTION_DR_DOC_BLIND);
					entry.setResponseEditableAction(RBACAction.ACTION_DR_DOC_BLIND_INST);
				}
				ConfigureEntryPermissionsDialog.this.okButton.setEnabled(true);
			}
		});
		otherCustomOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (VIEW.equals(actionType)) {
					entry.setAccessAction(RBACAction.ACTION_DR_VIEW_TREATMENT);
					entry.setResponseAccessAction(RBACAction.ACTION_DR_VIEW_TREATMENT_INST);
				}
				else if (EDIT.equals(actionType)) {
					entry.setEditableAction(RBACAction.ACTION_DR_EDIT_TREATMENT);
					entry.setResponseEditableAction(RBACAction.ACTION_DR_EDIT_TREATMENT_INST);
				}
				ConfigureEntryPermissionsDialog.this.okButton.setEnabled(true);
			}
		});
		return panel;
	}

	/**
	 * Handle ok and cancel
	 * @param aet the trigger event
	 */
	public void actionPerformed(ActionEvent aet) {
		if (aet.getSource() == okButton) {
			if (!readOnly) {
				//Check whether the entry has changed.
				if ((originalAccessAction == null && entry.getAccessAction() != null)
						|| (entry.getAccessAction()!= null && !entry.getAccessAction().equals(originalAccessAction))
						|| (originalEditAction == null && entry.getEditableAction() != null)
						|| (entry.getEditableAction()!= null && !originalEditAction.equals(entry.getEditableAction()))) {

					//Mark a entry and its document as having been edited
					((Entry)entry).setIsRevisionCandidate(true);
					((Document)DatasetController.getInstance().getActiveDocument()).setIsRevisionCandidate(true);

					//Set the dataset to dirty
					DatasetController.getInstance().getActiveDs().setDirty(true);
				}
			}
			this.dispose();
		} else if (aet.getSource() == cancelButton) {
			if (this.originalAccessAction == null) {
				entry.setAccessAction((String)null);
			}
			else {
				entry.setAccessAction(RBACAction.valueOf(this.originalAccessAction));
			}
			if (this.originalRespAccessAction == null) {
				entry.setResponseAccessAction((String)null);
			}
			else {
				entry.setResponseAccessAction(RBACAction.valueOf(this.originalRespAccessAction));
			}
			if (this.originalEditAction == null) {
				entry.setEditableAction((String)null);
			}
			else {
				entry.setEditableAction(RBACAction.valueOf(this.originalEditAction));
			}
			if (this.originalRespEditAction == null) {
				entry.setResponseEditableAction((String)null);
			}
			else {
				entry.setResponseEditableAction(RBACAction.valueOf(this.originalRespEditAction));
			}
			this.dispose();
		}
	}
}

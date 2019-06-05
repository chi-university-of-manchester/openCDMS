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
import org.psygrid.datasetdesigner.controllers.DatasetController;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.Utils;
import org.psygrid.security.RBACAction;


/**
 * Configure the permissions to be given to a particular document
 *
 * @author Lucy Bridges
 */
public class ConfigureDocPermissionsDialog extends AbstractConfigurePermissionsDialog implements ActionListener {

	private static final long serialVersionUID = -7184234096247415307L;

	/**
	 * The document
	 */
	private Document document;

	/**
	 * The access action of the document, before editing so that changes can be detected.
	 */
	private String originalAccessAction;


	/**
	 * The access action of the document instance, before editing so that changes can be detected.
	 */
	private String originalInstAccessAction;

	/**
	 * The edit action of the document, before editing so that changes can be detected.
	 */
	private String originalEditAction;

	/**
	 * The edit action of the document instance, before editing so that changes can be detected.
	 */
	private String originalInstEditAction;


	/**
	 * Creates a new ConfigureReportsDialog object.
	 *
	 * @param frame
	 */
	public ConfigureDocPermissionsDialog(JDialog parent, Document document) {
		this(parent, document, true);
	}

	/**
	 * Creates a new ConfigureReportsDialog object.
	 *
	 * @param frame
	 */
	public ConfigureDocPermissionsDialog(JDialog parent, Document document, boolean readOnly) {
		super(parent, readOnly);
		if (readOnly) {
			setTitle(PropertiesHelper.getStringFor(
			"org.psygrid.datasetdesigner.ui.viewdocpermissions"));
		} else {
			setTitle(PropertiesHelper.getStringFor(
			"org.psygrid.datasetdesigner.ui.configuredocpermissions"));
		}
		this.document = document;
		originalAccessAction = document.getAction();
		originalEditAction = document.getEditableAction();
		originalInstAccessAction = document.getInstanceAction();
		originalInstEditAction = document.getInstanceEditableAction();
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
				"org.psygrid.datasetdesigner.ui.configuredocpermissions")));

		return mainPanel;
	}

	private JPanel createActionPanel(final String actionType) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		//Create the radio buttons.
		final JRadioButton defaultOption = new JRadioButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.configuredefaultpermissions"));
		defaultOption.setSelected(false);
		final JRadioButton customOption = new JRadioButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.configurecustompermissions"));
		//Group the radio buttons.
		ButtonGroup group = new ButtonGroup();
		group.add(defaultOption);
		group.add(customOption);

		final JRadioButton blindCustomOption = new JRadioButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.configureblindpermissions"));
		blindCustomOption.setEnabled(false);
		final JRadioButton otherCustomOption = new JRadioButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.configureallpermissions"));
		otherCustomOption.setEnabled(false);
		final JRadioButton identifyingDocumentCustomOption = new JRadioButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.configureidentifyingpermissions"));
		identifyingDocumentCustomOption.setEnabled(false);
		ButtonGroup customGroup = new ButtonGroup();
		customGroup.add(blindCustomOption);
		customGroup.add(otherCustomOption);
		customGroup.add(identifyingDocumentCustomOption);

		if (VIEW.equals(actionType)) {
			if (document.getAction() == null || RBACAction.ACTION_DR_DOC_STANDARD.toString().equals(document.getAction())) {
				defaultOption.setSelected(true);	
				defaultOption.setEnabled(true);
			}
			else if (RBACAction.ACTION_DR_DOC_BLIND.toString().equals(document.getAction())) {
				blindCustomOption.setSelected(true);
				customOption.setSelected(true);
				blindCustomOption.setEnabled(true);
				otherCustomOption.setEnabled(true);
				customOption.setEnabled(true);
			}
			else if (RBACAction.ACTION_DR_DOC_VIEW_IDENTITY.toString().equals(document.getAction())) {
				identifyingDocumentCustomOption.setSelected(true);
				customOption.setSelected(true);
				identifyingDocumentCustomOption.setEnabled(true);
				otherCustomOption.setEnabled(true);
				customOption.setEnabled(true);
			}
			else {
				otherCustomOption.setSelected(true);
				customOption.setSelected(true);
				otherCustomOption.setEnabled(true);
				blindCustomOption.setEnabled(true);
				customOption.setEnabled(true);
			}
		}
		else if (EDIT.equals(actionType)) {
			if (document.getEditableAction() == null || RBACAction.ACTION_DR_EDIT_DOC.toString().equals(document.getEditableAction())) {
				defaultOption.setSelected(true);	
				defaultOption.setEnabled(true);
			}
			else if (RBACAction.ACTION_DR_DOC_BLIND.toString().equals(document.getEditableAction())) {
				blindCustomOption.setSelected(true);
				customOption.setSelected(true);
				blindCustomOption.setEnabled(true);
				otherCustomOption.setEnabled(true);
				customOption.setEnabled(true);
			}
			else {
				otherCustomOption.setSelected(true);
				customOption.setSelected(true);
				otherCustomOption.setEnabled(true);
				blindCustomOption.setEnabled(true);
				customOption.setEnabled(true);
			}
		}

		String title = "";
		if (VIEW.equals(actionType)) {
			title = PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.configuredocpermissionsview");
		}
		else {
			title = PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.configuredocpermissionsedit");
		}
		panel.setBorder(BorderFactory.createTitledBorder(title));

		panel.add(defaultOption);
		if (VIEW.equals(actionType)) {
			panel.add(new JLabel("     This will allow CROs, CPMs and researchers to view this document."));
		}
		else if (EDIT.equals(actionType)) {
			panel.add(new JLabel("     This will allow CROs to edit this document."));
		}
		panel.add(Box.createRigidArea(new Dimension(0,5)));
		panel.add(customOption);

		JPanel minipanel = new JPanel();
		minipanel.setLayout(new BoxLayout(minipanel, BoxLayout.Y_AXIS));
		minipanel.setBorder(BorderFactory.createEmptyBorder(5, 25, 5, 10));
		minipanel.add(blindCustomOption);
		if (VIEW.equals(actionType)) {
			minipanel.add(new JLabel("     This will 'hide' a treatment document from anyone who is NOT a TA or researcher."));
		}
		else if (EDIT.equals(actionType)) {
			minipanel.add(new JLabel("     This will stop a treatment document from being edited by anyone who is NOT a TA."));
		}

		if (VIEW.equals(actionType)) {
			minipanel.add(otherCustomOption);
			minipanel.add(new JLabel("     This will allow CROs, CPMs, TAs and researchers to view the document."));
		}
		else if (EDIT.equals(actionType)) {
			minipanel.add(otherCustomOption);
			minipanel.add(new JLabel("     This will allow CROs and TAs to edit the document."));
		}

		if (VIEW.equals(actionType)) {
			minipanel.add(identifyingDocumentCustomOption);
			minipanel.add(new JLabel("     This will allow CROs, CPMs and users with the ViewIdentity role to view the document."));
		}
		else if (EDIT.equals(actionType)) {
			// No special edit permissions for identifying data
		}
		panel.add(minipanel);

		/*
		 * Set the a pre-defined policy, represented by the RBACAction, in the document. 
		 * 
		 * These actions are used by ConfigureEntryPermissions to display the document's permissions
		 * when no entry permission has been given.
		 */
		defaultOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				otherCustomOption.setEnabled(false);
				otherCustomOption.setSelected(false);
				blindCustomOption.setEnabled(false);
				blindCustomOption.setSelected(false);
				identifyingDocumentCustomOption.setEnabled(false);
				identifyingDocumentCustomOption.setSelected(false);
				if (VIEW.equals(actionType)) {
					document.setAction(RBACAction.ACTION_DR_DOC_STANDARD);
					document.setInstanceAction(RBACAction.ACTION_DR_DOC_STANDARD_INST);
				}
				else if (EDIT.equals(actionType)) {
					document.setEditableAction(RBACAction.ACTION_DR_EDIT_DOC); 
					document.setInstanceEditableAction(RBACAction.ACTION_DR_EDIT_DOC_INST);
				}
				ConfigureDocPermissionsDialog.this.okButton.setEnabled(true);
			}
		});
		customOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				otherCustomOption.setEnabled(true);
				blindCustomOption.setEnabled(true);
				identifyingDocumentCustomOption.setEnabled(true);
				ConfigureDocPermissionsDialog.this.okButton.setEnabled(false);
			}
		});
		blindCustomOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (VIEW.equals(actionType)) {
					document.setAction(RBACAction.ACTION_DR_DOC_BLIND);
					document.setInstanceAction(RBACAction.ACTION_DR_DOC_BLIND_INST);
				}
				else if (EDIT.equals(actionType)) {
					document.setEditableAction(RBACAction.ACTION_DR_DOC_BLIND); 
					document.setInstanceEditableAction(RBACAction.ACTION_DR_DOC_BLIND_INST);
				}
				ConfigureDocPermissionsDialog.this.okButton.setEnabled(true);
			}
		});
		otherCustomOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (VIEW.equals(actionType)) {
					document.setAction(RBACAction.ACTION_DR_VIEW_TREATMENT);
					document.setInstanceAction(RBACAction.ACTION_DR_VIEW_TREATMENT_INST);
				}
				else if (EDIT.equals(actionType)) {
					document.setEditableAction(RBACAction.ACTION_DR_EDIT_TREATMENT); 
					document.setInstanceEditableAction(RBACAction.ACTION_DR_EDIT_TREATMENT_INST);
				}
				ConfigureDocPermissionsDialog.this.okButton.setEnabled(true);
			}
		});
		identifyingDocumentCustomOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (VIEW.equals(actionType)) {
					document.setAction(RBACAction.ACTION_DR_DOC_VIEW_IDENTITY);
					document.setInstanceAction(RBACAction.ACTION_DR_DOC_VIEW_IDENTITY_INST);
				}
				else if (EDIT.equals(actionType)) {
					document.setEditableAction(RBACAction.ACTION_DR_EDIT_DOC); 
					document.setInstanceEditableAction(RBACAction.ACTION_DR_EDIT_DOC_INST);
				}
				ConfigureDocPermissionsDialog.this.okButton.setEnabled(true);
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
				//Check whether document has changed.
				if ((originalAccessAction == null && document.getAction() != null)
						|| (document.getAction() != null && !originalAccessAction.equals(document.getAction()))
						|| (originalEditAction == null && document.getEditableAction() != null)
						|| (document.getEditableAction() != null && !originalEditAction.equals(document.getEditableAction()))) {
					//Mark a document as having been edited
					((Document)document).setIsRevisionCandidate(true);

					//Set the dataset to dirty
					DatasetController.getInstance().getActiveDs().setDirty(true);
				}
			}
			this.dispose();
		} else if (aet.getSource() == cancelButton) {
			//Reset the document's actions
			if (this.originalAccessAction == null) {
				document.setAction((String)null);
			}
			else {
				document.setAction(RBACAction.valueOf(this.originalAccessAction));
			}
			if (this.originalInstAccessAction == null) {
				document.setInstanceAction((String)null);
			}
			else {
				document.setInstanceAction(RBACAction.valueOf(this.originalInstAccessAction));	
			}
			if (this.originalEditAction == null) {
				document.setEditableAction((String)null);
			}
			else {
				document.setEditableAction(RBACAction.valueOf(this.originalEditAction));
			}
			if (this.originalInstEditAction == null) {
				document.setInstanceEditableAction((String)null);
			}
			else {
				document.setInstanceEditableAction(RBACAction.valueOf(this.originalInstEditAction));	
			}
			this.dispose();
		}
	}
}

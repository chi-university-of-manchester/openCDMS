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
import java.awt.Window;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;

import org.psygrid.data.model.hibernate.AuditableChange;
import org.psygrid.data.model.hibernate.Element;
import org.psygrid.datasetdesigner.custom.TextFieldWithStatus;
import org.psygrid.datasetdesigner.custom.SizeConstrainedTextArea;

import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.SpringUtilities;
import org.psygrid.datasetdesigner.utils.ElementUtility;

import org.psygrid.collection.entry.security.SecurityManager;

import org.psygrid.data.model.hibernate.AuditLog;


/**
 * Provenance dialog - dialog where changes can be documented by the user
 * 
 * @author pwhelan
 */
public class ProvenanceDialog extends JDialog implements ActionListener {
	
	/**
	 * Prefix for referencing in the properties file
	 */
	private final static String STRING_PREFIX = "org.psygrid.datasetdesigner.ui.configurationdialogs.";
	
	/**
	 * Ok
	 */
	private JButton okButton;
	
	/**
	 * Name of the author of the change
	 */
	private TextFieldWithStatus authorNameField;
	
	/**
	 * Text area where comments can be entered
	 */
	private SizeConstrainedTextArea commentArea;

	/**
	 * The element to be changed 
	 */
	private Element element;
	
	/**
	 * The action being performed: edit, change, delete
	 */
	private String action;
	
	/**
	 * The parent window
	 */
	private Window parentWindow;
	
	private boolean disposeParent = true;
	
	/**
	 * Constrcutor 
	 * @param parentFrame the owner frame
	 * @param element the element that is being changed
	 */
	public ProvenanceDialog(JFrame parentFrame, Element element) {
		this(parentFrame, element, AuditableChange.ACTION_EDIT, true);
	}
	
	/**
	 * Constrcutor 
	 * @param parentFrame the owner frame
	 * @param element the element that is being changed
	 */
	public ProvenanceDialog(JFrame parentFrame, Element element, boolean disposeParent) {
		this(parentFrame, element, AuditableChange.ACTION_EDIT, disposeParent);
	}
	
	/**
	 * Constructor
	 * @param parentFrame the owner frame
	 * @param element the element that is being changed 
	 * @param action the action that the change represents (edit, delete etc)
	 */
	public ProvenanceDialog(JFrame parentFrame, Element element, String action) {
		super(parentFrame);
		init(parentFrame, element, action, true);
	}
	
	/**
	 * Constructor
	 * @param parentFrame the owner frame
	 * @param element the element that is being changed 
	 * @param action the action that the change represents (edit, delete etc)
	 */
	public ProvenanceDialog(JFrame parentFrame, Element element, String action, boolean disposeParent) {
		super(parentFrame);
		init(parentFrame, element, action, disposeParent);
	}
	
	/**
	 * Constrcutor 
	 * @param parentDialog the owner dialog
	 * @param element the element that is being changed
	 */
	public ProvenanceDialog(JDialog parentDialog, Element element) {
		this(parentDialog, element, AuditableChange.ACTION_EDIT, true);
	}
	
	/**
	 * Constrcutor 
	 * @param parentDialog the owner dialog
	 * @param element the element that is being changed
	 */
	public ProvenanceDialog(JDialog parentDialog, Element element, boolean disposeParent) {
		this(parentDialog, element, AuditableChange.ACTION_EDIT, disposeParent);
	}
	
	/**
	 * Constrcutor 
	 * @param parentDialog the owner dialog
	 * @param element the element that is being changed
	 * @param action the type of action taking place - editing, deleting etc.
	 */
	public ProvenanceDialog(JDialog parentDialog, Element element, String action, boolean disposeParent) {
		super(parentDialog);
		init(parentDialog, element, action, disposeParent);
	}
	
	/**
	 * Initialise the window; lay out the components etc
	 * @param parentWindow the owner window
	 * @param element the element that is being changed
	 * @param action the type of action taking place: edit, delete etc
	 */
	private void init(Window parentWindow, Element element, String action, boolean disposeParent) {
		setTitle(PropertiesHelper.getStringFor(STRING_PREFIX + "provenancedialog"));
		this.parentWindow = parentWindow;
		this.element = element;
		this.action = action;
		this.disposeParent = disposeParent;
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(buildMainPanel(), BorderLayout.CENTER);
		getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
		initFields();
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	}
	
	/**
	 * Lay out the panel containing the author's name
	 * 
	 * @return the configured panel
	 */
	private JPanel buildMainPanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		
		JPanel formPanel = new JPanel();
		formPanel.setLayout(new SpringLayout());
		
		authorNameField = new TextFieldWithStatus(20, false);
		
		formPanel.add(new JLabel(PropertiesHelper.getStringFor(STRING_PREFIX + "changeAuthor")));
		formPanel.add(authorNameField);
		
		SpringUtilities.makeCompactGrid(formPanel,
                1, 2, //rows, cols
				6, 6,        //initX, initY
				6, 6);       //xPad, yPad
		
		JPanel commentPanel = new JPanel();
		commentPanel.setBorder(BorderFactory.createEtchedBorder());
		commentArea = new SizeConstrainedTextArea(2000);
		commentArea.setPreferredSize(new Dimension(200, 200));
		commentArea.setLineWrap(true);
		commentPanel.setLayout(new BorderLayout());
		commentPanel.add(new JLabel(PropertiesHelper.getStringFor(STRING_PREFIX + "changeComment")), BorderLayout.NORTH);
		commentPanel.add(new JScrollPane(commentArea), BorderLayout.CENTER);
		
		mainPanel.add(formPanel, BorderLayout.NORTH);
		mainPanel.add(commentPanel, BorderLayout.CENTER);
		
		return mainPanel;
	}
	
	/**
	 * Lay out the button panel containing
	 * the Ok and Cancel buttons
	 * 
	 * @return the configured button panel
	 */
	private JPanel buildButtonPanel() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		okButton = new JButton("OK");
		okButton.addActionListener(this);
		buttonPanel.add(okButton);
		return buttonPanel;
	}
	
	/**
	 * Initialise the author field
	 */
	private void initFields() {
		authorNameField.setText(SecurityManager.getInstance().getUserName());
		authorNameField.setEnabled(false);
	}

	/**
	 * Validate the dialog before proceeding to save it
	 * Ensure there is something entered in the commment area
	 * @return true if the dialog validates; false if not
	 */
	private boolean validateEntries() {
		if (commentArea.getText().equals("") || commentArea.getText() == null) {
			JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor(STRING_PREFIX + "commentareanotnull"));
			return false;
		}
		
		return true;
	}
	
	/**
	 * Handle ok; save the change to the audit log of the element
	 * @param aet the calling action event
	 */
	public void actionPerformed(ActionEvent aet) {
		if (aet.getSource() == okButton) {
			if (validateEntries()) {
				AuditableChange auditableChange = ElementUtility.createAuditableChange(action,
                        commentArea.getText(),
                        SecurityManager.getInstance().getUserName());
				AuditLog auditLog = element.getAuditLog();
				if (auditLog == null) {
					auditLog = ElementUtility.createAuditLog();
				}
				auditLog.addAuditableChange(auditableChange);
				//update the audit log
				element.setAuditLog(auditLog);
				this.dispose();

				//dismiss a dialog (this will be from an entry or other configurattion
				// window but don't dismiss the main window (which is a JFrame)
				if ( (!(parentWindow instanceof JFrame)) && disposeParent) {
					parentWindow.dispose();
				}
				
				//set the element changed flag to true
				element.setChanged(true);
			}
		}
	}
 	
}

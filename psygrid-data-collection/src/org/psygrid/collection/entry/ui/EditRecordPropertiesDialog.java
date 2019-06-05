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


package org.psygrid.collection.entry.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jdesktop.swingx.util.WindowUtils;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.EntryMessages;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;

/**
 * @author Lucy Bridges
 *
 */
public class EditRecordPropertiesDialog extends ApplicationDialog {

	private static final long serialVersionUID = 1432049971755529463L;

	private JLabel componentTitle;
	private JComponent component;
	private JComponent component2;
	private JTextField reason;

	private JButton editSave;
	private JButton editCancel;

	private boolean hasReason;

	private boolean updated;

	public EditRecordPropertiesDialog(Application parent, String title, JLabel componentTitle, JComponent component, boolean hasReason)   {
		super(parent, title, true);

		this.componentTitle = componentTitle;
		this.component = component;
		this.component2 = null;
		updated = false;
		this.hasReason = hasReason;
		init();
		initEventHandling();
		pack();
		setLocation(WindowUtils.getPointForCentering(this));
		setVisible(true);
	}

	public EditRecordPropertiesDialog(Application parent, String title, JLabel componentTitle, JComponent component1, JComponent component2, boolean hasReason)   {
		super(parent, title, true);
		this.componentTitle = componentTitle;
		this.component = component1;
		this.component2 = component2;
		updated = false;
		this.hasReason = hasReason;
		init();
		initEventHandling();
		pack();
		setLocation(WindowUtils.getPointForCentering(this));
		setVisible(true);
	}
	
	public EditRecordPropertiesDialog(ApplicationDialog parentDialog, Application parent, String title, JLabel componentTitle, JComponent component, boolean hasReason)   {
		super(parentDialog, parent, title, true);

		this.componentTitle = componentTitle;
		this.component = component;
		this.component2 = null;
		updated = false;
		this.hasReason = hasReason;
		init();
		initEventHandling();
		pack();
		setLocation(WindowUtils.getPointForCentering(this));
		setVisible(true);
	}

	private void init() {
		JLabel reasonLabel = new JLabel(Messages.getString("EditRecordPropertiesDialog.reasonForChangeLabel"));
		reason = new JTextField();

		JPanel panel = new JPanel();
		DefaultFormBuilder editBuilder = new DefaultFormBuilder(new FormLayout("150dlu, 10dlu"),  //$NON-NLS-1$
				panel);
		editBuilder.setDefaultDialogBorder();
		editBuilder.append(componentTitle);
		editBuilder.append(component);

		if (component2 != null) {
			editBuilder.append(component2);
		}
		
		if (hasReason) {
			editBuilder.appendGlueRow();
			editBuilder.nextLine(2);
			editBuilder.append(reasonLabel);
			editBuilder.append(reason);
		}

		editSave = new JButton(EntryMessages.getString("Entry.save"));
		editCancel = new JButton(EntryMessages.getString("Entry.cancel"));
		JPanel buttonsPanel = ButtonBarFactory.buildOKCancelBar(editSave, editCancel);
		editBuilder.append(buttonsPanel, editBuilder.getColumnCount());
		getContentPane().add(panel);

	}

	private EditRecordPropertiesDialog getObject() {
		return this;
	}

	public boolean hasUpdated() {
		return updated;
	}

	private void initEventHandling() {
		editSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if ( hasReason && (null == reason.getText() || 0 == reason.getText().length()) ){
					JOptionPane.showMessageDialog(getObject(), Messages.getString("EditRecordPropertiesDialog.reasonRequiredMessage"), Messages.getString("EditRecordPropertiesDialog.reasonRequiredTitle"), JOptionPane.ERROR_MESSAGE);
					return;
				}
				else {
					dispose();
					updated = true;
				}
			}
		});
		editCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
	}

	public String getReason() {
		return reason.getText();
	}

}

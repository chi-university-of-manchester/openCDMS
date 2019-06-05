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

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.util.WindowUtils;
import org.psygrid.collection.entry.EntryMessages;
import org.psygrid.collection.entry.persistence.PersistenceManager;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Allow the user to specify a different directory to be used instead of
 * the home directory when calculating the path for the .psygrid base 
 * directory. 
 * 
 * @author Lucy Bridges
 *
 */
public class ChangeHomeDirDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private DefaultFormBuilder builder;

	private JButton cancelButton;

	private JButton saveButton;

	private JLabel locationLabel;
	private JTextField locationField;
	private JButton locationButton;

	private static final Log LOG = LogFactory
	.getLog(ChangeHomeDirDialog.class);

	public ChangeHomeDirDialog(JFrame parent) {
		super(parent, Messages.getString("ChangeHomeDirDialog.title"), true);
		initBuilder();
		initComponents();
		initEventHandling();
		build();
		pack();
		setLocation(WindowUtils.getPointForCentering(this));
	}

	private void initComponents() {
		locationLabel = new JLabel(Messages.getString("ChangeHomeDirDialog.locationLabel"));
		locationField = new JTextField();
		locationField.setColumns(25);
		
		//Set the current .psygrid location
		File psygridDir = getDefaultHomeDir();
		if (psygridDir != null) {
			locationField.setText(psygridDir.getAbsolutePath());
		}
		locationButton = new JButton(EntryMessages.getString("Entry.open"));

		cancelButton  = new JButton(EntryMessages.getString("Entry.cancel")); //$NON-NLS-1$
		saveButton    = new JButton(EntryMessages.getString("Entry.save")); //$NON-NLS-1$

	}

	private void initEventHandling() {

		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handleSave(locationField.getText());
				dispose();
			}
		});

		locationButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				File psygridDir = getDefaultHomeDir();
				if (psygridDir != null) {
					chooser.setCurrentDirectory(psygridDir);
				}
				
				// Show the dialog; will wait until dialog is closed
				int result = chooser.showOpenDialog(getParent());

				switch (result) {
				case JFileChooser.APPROVE_OPTION:
					// Approve (Open or Save) was clicked
					File file = chooser.getSelectedFile();
					locationField.setText(file.getAbsolutePath());
					break;
				case JFileChooser.CANCEL_OPTION:
					// Cancel or the close-dialog icon was clicked
					break;
				case JFileChooser.ERROR_OPTION:
					// The selection process did not complete successfully
					break;
				}

			}
		});

	}

	private void handleSave(String dir) {

		String error = null;
		try {
			PersistenceManager.getInstance().updateHomeDirLocation(dir);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem updating location of the PsyGrid data folder to "+dir, e);
			error = e.getMessage();
		}
		
		ChangeHomeDirDialog.this.setCursor(Cursor.getDefaultCursor());
		String title;
		String message;
		if (error != null) {
			title   = Messages.getString("ChangeHomeDirDialog.errorTitle");
			message = Messages.getString("ChangeHomeDirDialog.errorMessage") + error;
		}
		else {
			title   = Messages.getString("ChangeHomeDirDialog.successTitle");
			message = Messages.getString("ChangeHomeDirDialog.successMessage");
		}
		JOptionPane.showMessageDialog(getParent(), message, title,
				JOptionPane.INFORMATION_MESSAGE);
	}

	private void initBuilder() {
		builder = new DefaultFormBuilder(new FormLayout("right:pref, 6dlu, 48dlu", // columns 
		""), // add rows dynamically
			new JPanel());
		builder.setDefaultDialogBorder();
	}

	private void build() {
		builder.append(locationLabel);
		builder.appendRelatedComponentsGapRow();
		builder.nextLine(2);
		builder.append(locationField); 
		builder.append(locationButton);

		builder.appendRelatedComponentsGapRow();
		builder.nextLine();
		builder.appendUnrelatedComponentsGapRow();
		builder.nextLine(2);
		builder.appendRelatedComponentsGapRow();
		builder.nextLine();

		JPanel buttonPanel = ButtonBarFactory.buildOKCancelBar(saveButton, cancelButton);
		builder.append(buttonPanel, builder.getColumnCount());
		getContentPane().add(builder.getPanel());
	}

	private File getDefaultHomeDir() {
		String defaultLocation = PersistenceManager.getInstance().getUserHome();
		if (defaultLocation != null && !defaultLocation.equals("")) {
			File entryDir = new File(defaultLocation);
			return entryDir;
		}
		return null;
	}
}

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


package org.psygrid.collection.entry.sampletracking;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jdesktop.swingx.util.WindowUtils;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.EntryMessages;
import org.psygrid.collection.entry.ui.ApplicationDialog;
import org.psygrid.data.sampletracking.ConfigInfo;
import org.psygrid.data.sampletracking.ParticipantInfo;
import org.psygrid.data.sampletracking.SampleInfo;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;

/**
 * 
 * Allows editing of participant sample identifier.
 * 
 * @author Terry Child
 *
 */
class ParticipantDialog extends ApplicationDialog {
	
	private boolean result = false;

	private ConfigInfo config;
	private ParticipantInfo participant;
	
	private DefaultFormBuilder builder;

	private JTextField participantIdentifier1;
	private JTextField participantIdentifier2;

	private JButton okButton;
	private JButton cancelButton;

	public ParticipantDialog(ConfigInfo config,ParticipantInfo participant,Application application)   {
		super(application, Messages.getString("ParticipantDialog.dialogTitle"), true);
		this.config=config;
		this.participant=participant;
		init();
		pack();
		setLocation(WindowUtils.getPointForCentering(this));
	}

	private void init(){
		builder = new DefaultFormBuilder(new FormLayout("pref, 6dlu, 150dlu:grow"),new JPanel());
		builder.setDefaultDialogBorder();
				
		participantIdentifier1 = new JTextField();
		participantIdentifier2 = new JTextField();
		
		okButton = new JButton(EntryMessages.getString("Entry.ok"));
		cancelButton = new JButton(EntryMessages.getString("Entry.cancel"));
		
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		
		// Build the form
		builder.append(Messages.getString("ParticipantDialog.participantIdentifier1Label"),participantIdentifier1);
		builder.nextLine();
		
		if(!config.isAutoParticipantID()){
			builder.append(Messages.getString("ParticipantDialog.participantIdentifier2Label"),participantIdentifier2);
			builder.nextLine();
		}
						
		JPanel buttonsPanel = ButtonBarFactory.buildRightAlignedBar(okButton,cancelButton);
		builder.append(buttonsPanel, builder.getColumnCount());

		getContentPane().add(builder.getPanel());
	}

	public boolean doModal(){
		participantIdentifier1.setText(participant.getIdentifier());
		participantIdentifier2.setText(participant.getIdentifier());
		setVisible(true);
		return result;
	}

	private void save() {
		if(!participantIdentifier1.getText().equals(participantIdentifier2.getText())){
			String title = Messages.getString("ParticipantDialog.participantIdentifierMismatchTitle");
			String message = Messages.getString("ParticipantDialog.participantIdentifierMismatch");
			JOptionPane.showMessageDialog(getParent(), message, title,JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		// Check the label against a regex
		String regex = config.getParticipantRegex();
		if(regex!=null && regex.length()>0 && !participantIdentifier1.getText().matches(regex)){
			String title = Messages.getString("ParticipantDialog.participantRegexTitle");
			String message = null;
			if(config.getParticipantRegexDescription()!=null && config.getParticipantRegexDescription().length()>0){
				message = config.getParticipantRegexDescription();
			}
			else {
				message = Messages.getString("ParticipantDialog.participantRegexMessage")+"'"+regex+"'";
			}
			JOptionPane.showMessageDialog(getParent(), message, title,JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		participant.setIdentifier(participantIdentifier1.getText());
		result = true;
		dispose();		
	}

	private void close() {
		result = false;
		dispose();
	}
}


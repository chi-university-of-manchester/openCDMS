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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.util.WindowUtils;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.EntryMessages;
import org.psygrid.collection.entry.ExceptionsHelper;
import org.psygrid.collection.entry.SwingWorkerExecutor;
import org.psygrid.collection.entry.persistence.ExternalIdGetter;
import org.psygrid.collection.entry.persistence.NoExternalIdMappingException;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.collection.entry.remote.RemoteManager;
import org.psygrid.collection.entry.remote.RemoteServiceFault;
import org.psygrid.collection.entry.security.EntrySAMLException;
import org.psygrid.collection.entry.security.SecurityManager;
import org.psygrid.common.ui.WrappedJOptionPane;
import org.psygrid.data.model.hibernate.ChangeHistory;
import org.psygrid.data.model.hibernate.GenericState;
import org.psygrid.data.model.hibernate.Record;
import org.psygrid.data.model.hibernate.RecordData;
import org.psygrid.data.model.hibernate.Status;
import org.psygrid.data.utils.security.NotAuthorisedFault;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Class to create a dialog window to view the properties for a 
 * given Record, provides buttons to edit certain properties.
 * 
 * @author Lucy Bridges
 *
 */
public class ViewRecordPropertiesDialog extends ApplicationDialog {

	private static final long serialVersionUID = 1432049971755529462L;

	private static final Log LOG = LogFactory.getLog(ViewRecordPropertiesDialog.class);

	private static SimpleDateFormat hhMmSsddMmmYyyy = new SimpleDateFormat("HH:mm:ss dd-MMM-yyyy");

	private Record record;

	private DefaultFormBuilder builder;

	private JPanel contentPanel;

	private JButton closeButton;

	private JLabel identifierLabel;

	private JLabel linkedIdentifierLabel = null;
	private JLabel linkedIdentifierValue = null;

	private JLabel groupLabel;

	private JLabel groupValue;

	private JLabel siteLabel;

	private JLabel siteValue;

	private JLabel consultantLabel;
	private JLabel consultantValue;

	private JLabel historyLabel;
	private JTable historyTable;
	private JScrollPane historyScrollpane;

	private JLabel statusLabel;

	private JLabel statusValue;

	private JButton statusEditButton;

	private JLabel consentLabel;

	private JLabel consentValue;

	private JButton consentEditButton;

	private JLabel studyEntryLabel;

	private JLabel studyEntryDateVal;

	private JButton studyEntryEditButton = null;

	private JLabel schStartDateLabel;

	private JLabel schStartDateVal;

	private JButton schStartDateEditButton = null;

	private JLabel notesLabel;

	private JTextArea notes;

	private JButton notesEditButton = null;

	private static final String CONSENT_PRESENT = Messages.getString("ViewRecordPropertiesDialog.consentPresentText");
	private static final String CONSENT_NOT_PRESENT = Messages.getString("ViewRecordPropertiesDialog.noConsentPresentText");

	/**
	 * States whether the current user has the privileges necessary to be
	 * able to change the study entry date, scheduled start date and notes
	 * of the record.
	 */
	private boolean canChangeMetadata;

	private Application application;

	public ViewRecordPropertiesDialog(Application parent, Record record)   {
		super(parent, Messages.getString("ViewRecordPropertiesDialog.dialogTitle"), true);
		this.record = record;
		this.application = parent;
		try {
			this.canChangeMetadata = RemoteManager.getInstance().canUpdateRecordMetadata(record);
		}
		catch (Exception ex) {
			LOG.error("Problem occurred when trying to establish whether user can edit the record metadata", ex);
			this.canChangeMetadata = false;
		}

		if (record == null) {
			//An error must have occurred retrieving the record
			return;
		}
		if (!checkUnfinishedDocument()) {
			dispose();
			return;
		}

		initBuilder();
		if(!initComponents()){
			dispose();
			return;
		}
		initEventHandling();
		build();
		pack();
		setLocation(WindowUtils.getPointForCentering(this));
		setVisible(true);
	}

	private void initBuilder(){
		contentPanel = new JPanel(); 
		builder = new DefaultFormBuilder(new FormLayout("116dlu, 6dlu, 110dlu, 2dlu, 50dlu"),  //$NON-NLS-1$
				contentPanel);
		builder.setDefaultDialogBorder();
	}

	private boolean initComponents(){

		boolean returnVal = true;
		
		DateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy");

		String displayIdentifier = null;
		
		if(record.getDataSet().getUseExternalIdAsPrimary() == true){
			try {
				displayIdentifier = ExternalIdGetter.get(record.getIdentifier().getIdentifier());
			} catch (NoExternalIdMappingException e) {
				ExceptionsHelper.handleException(this, "Problem Occurred", e, "Cannot Open Record Properties Dialog" , false);
				return false;
			}
		}else{
			displayIdentifier = record.getIdentifier().getIdentifier();
		}
		
		identifierLabel = new JLabel(displayIdentifier);
		identifierLabel.setFont(new Font("Dialog", Font.BOLD, 13));

		if (record.getSecondaryIdentifier() != null) {
			linkedIdentifierLabel = new JLabel(Messages.getString("ViewRecordPropertiesDialog.linkedRecordLabel"));
			linkedIdentifierValue = new JLabel(record.getSecondaryIdentifier());
		}

		if (record.getPrimaryIdentifier() != null) {
			linkedIdentifierLabel = new JLabel(Messages.getString("ViewRecordPropertiesDialog.linkedRecordLabel"));
			linkedIdentifierValue = new JLabel(record.getPrimaryIdentifier());
		}

		groupLabel = new JLabel(Messages.getString("ViewRecordPropertiesDialog.groupLabel"));
		String groupCode = record.getIdentifier().getGroupPrefix();
		groupValue = new JLabel(groupCode);
		for (int i=0; i< record.getDataSet().numGroups(); i++) {
			if (groupCode.equals(record.getDataSet().getGroup(i).getName())) {
				groupValue = new JLabel(record.getDataSet().getGroup(i).getLongName());
			}
		}

		siteLabel = new JLabel(Messages.getString("ViewRecordPropertiesDialog.siteLabel"));
		siteValue = new JLabel(record.getSite().getSiteName());

		consultantLabel = new JLabel(Messages.getString("ViewRecordPropertiesDialog.consultantLabel"));
		consultantValue = new JLabel(record.getConsultant());

		historyLabel = new JLabel("History");
		historyTable = new JTable(buildHistoryTableModel());
		historyTable.setPreferredScrollableViewportSize(new Dimension(450, 100));
		historyScrollpane = new JScrollPane(historyTable);


		statusLabel = new JLabel(Messages.getString("ViewRecordPropertiesDialog.recordStatusLabel"));
		statusValue = new JLabel(record.getStatus().getLongName());
		statusEditButton = new JButton(EntryMessages.getString("Entry.edit"));

		consentLabel = new JLabel(Messages.getString("ViewRecordPropertiesDialog.consentLabel"));
		if (record.getAllConsents() != null && record.getAllConsents().size() > 0) {
			consentValue = new JLabel(CONSENT_PRESENT);	
		}
		else {
			consentValue = new JLabel(CONSENT_NOT_PRESENT);
		}

		if (canChangeMetadata) {
			consentEditButton = new JButton(Messages.getString("ViewRecordPropertiesDialog.consentEditButtonText"));
		}

		studyEntryLabel = new JLabel(Messages.getString("ViewRecordPropertiesDialog.studyEntryLabel"));
		studyEntryDateVal = new JLabel();
		if ( null != record.getStudyEntryDate() ){
			studyEntryDateVal.setText(dateFormat.format(record.getStudyEntryDate()));
		}

		if (canChangeMetadata) {
			studyEntryEditButton = new JButton(EntryMessages.getString("Entry.edit"));
		}

		String question = record.getDataSet().getScheduleStartQuestion();
		if ( null != question ){	
			schStartDateLabel = new JLabel(formatQuestion(question));
			schStartDateVal = new JLabel();
			if ( null != record.getScheduleStartDate() ){
				schStartDateVal.setText(dateFormat.format(record.getScheduleStartDate()));
			}
		}
		if (canChangeMetadata) {
			schStartDateEditButton = new JButton(EntryMessages.getString("Entry.edit"));
		}

		notesLabel = new JLabel(Messages.getString("ViewRecordPropertiesDialog.notesLabel"));
		notes = new JTextArea(record.getNotes());
		notes.setRows(8);
		notes.setEditable(false);
		notes.setWrapStyleWord(true);
		notes.setLineWrap(true);
		notes.setBackground(notesLabel.getBackground());
		if (canChangeMetadata) {
			notesEditButton = new JButton(EntryMessages.getString("Entry.edit"));
		}

		closeButton = new JButton(EntryMessages.getString("Entry.close")); //$NON-NLS-1$
		
		return true;
	}

	private void initEventHandling(){
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				record = null;			
				application.refreshRecordView();
				dispose();
			}
		});

		if (notesEditButton != null) {
			notesEditButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					handleNotesEdit();
				}
			});
		}
		if (consentEditButton != null) {
			consentEditButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					handleConsentEdit();
				}
			});
		}
		if (schStartDateEditButton != null) {
			schStartDateEditButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					handleScheduleStartDateEdit();
				}
			});
		}
		if (studyEntryEditButton != null) {
			studyEntryEditButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					handleStudyEntryDateEdit();
				}
			});
		}
		statusEditButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handleStatusEdit();
			}
		});
	}

	private void build(){
		builder.appendSeparator(identifierLabel.getText());
		if (linkedIdentifierValue != null) {
			builder.append(linkedIdentifierLabel);
			builder.append(linkedIdentifierValue,3);	
		}

		builder.appendUnrelatedComponentsGapRow();
		builder.nextLine(2);

		builder.append(groupLabel);
		builder.append(groupValue,3);
		builder.appendGlueRow();
		builder.nextLine(2);
		builder.append(siteLabel);
		builder.append(siteValue,3);
		builder.appendGlueRow();
		builder.nextLine(2);

		builder.append(consultantLabel);
		builder.append(consultantValue);
		builder.appendGlueRow();
		builder.nextLine(2);
		builder.appendSeparator();
		builder.nextLine();

		builder.append(historyLabel);
		builder.appendGlueRow();
		builder.nextLine(2);

		builder.append(historyScrollpane, builder.getColumnCount());
		builder.appendGlueRow();
		builder.nextLine(2);
		builder.appendSeparator();
		builder.nextLine();

		builder.append(statusLabel);
		builder.append(statusValue);
		JPanel statusEditButtonPanel = ButtonBarFactory.buildRightAlignedBar(statusEditButton);
		builder.append(statusEditButtonPanel);
		builder.appendGlueRow();
		builder.nextLine(2);
		builder.append(consentLabel);
		builder.append(consentValue);
		if (consentEditButton == null) {
			builder.append(new JLabel());
		}
		else {
			JPanel consentEditButtonPanel = ButtonBarFactory.buildRightAlignedBar(consentEditButton);
			builder.append(consentEditButtonPanel);
		}

		builder.appendGlueRow();
		builder.nextLine(2);
		builder.appendSeparator();
		builder.appendUnrelatedComponentsGapRow();
		builder.nextLine(2);

		builder.append(studyEntryLabel);
		builder.append(studyEntryDateVal);
		if (studyEntryEditButton == null) {
			builder.append(new JLabel());
		}
		else {
			JPanel studyEntryButtonPanel = ButtonBarFactory.buildRightAlignedBar(studyEntryEditButton);
			builder.append(studyEntryButtonPanel);
		}
		builder.appendGlueRow();
		builder.nextLine(2);

		if (schStartDateLabel != null) {
			builder.appendGlueRow();
			builder.nextLine();
			builder.appendSeparator();
			builder.appendGlueRow();
			builder.nextLine(2);
			builder.append(schStartDateLabel);
			builder.append(schStartDateVal);
			if (schStartDateEditButton == null) {
				builder.append(new JLabel());
			}
			else {
				JPanel schStartDateButtonPanel = ButtonBarFactory.buildRightAlignedBar(schStartDateEditButton);
				builder.append(schStartDateButtonPanel);
			}
			builder.appendSeparator();
			builder.appendUnrelatedComponentsGapRow();
			builder.nextLine(2);
		}

		builder.append(notesLabel, builder.getColumnCount());
		JScrollPane notesPane = new JScrollPane(notes);
		notesPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		notesPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		builder.append(notesPane, builder.getColumnCount());
		if (notesEditButton != null) {
			JPanel notesButtonPanel = ButtonBarFactory.buildLeftAlignedBar(notesEditButton);
			builder.append(notesButtonPanel, builder.getColumnCount());
		}

		builder.appendUnrelatedComponentsGapRow();
		builder.nextLine(2);
		JPanel buttonsPanel = ButtonBarFactory.buildRightAlignedBar(closeButton);
		builder.append(buttonsPanel, builder.getColumnCount());

        JScrollPane scroller = new JScrollPane(contentPanel);
		getContentPane().add(scroller);
	}

	private void handleNotesEdit() {

		JLabel newNotesLabel = new JLabel(Messages.getString("ViewRecordPropertiesDialog.editNotesLabel"));
		JTextArea newNotes = new JTextArea(record.getNotes());
		newNotes.setRows(8);
		newNotes.setEditable(true);
		newNotes.setWrapStyleWord(true);
		newNotes.setLineWrap(true);
		JScrollPane scrollableNotes = new JScrollPane(newNotes);

		EditRecordPropertiesDialog dialog = new EditRecordPropertiesDialog(this.getParent(), Messages.getString("ViewRecordPropertiesDialog.editNotesTitle"), newNotesLabel, scrollableNotes, true);
		pack();
		final String reason = dialog.getReason();
		if (!dialog.hasUpdated()) {
			return;
		}
		final RecordData newData = record.generateRecordData();
		final String newNotesText = newNotes.getText();
		newData.setNotes(newNotesText);
		newData.setScheduleStartDate(record.getScheduleStartDate());
		newData.setStudyEntryDate(record.getStudyEntryDate());

		SwingWorker<Object, Object> worker = new SwingWorker<Object, Object>() {
			@Override
			protected Object doInBackground() throws ConnectException, SocketTimeoutException,
			NotAuthorisedFault, IOException, RemoteServiceFault,
			EntrySAMLException {
				//Record must exist in the repository (and therefore have an id) to be updated
				RemoteManager.getInstance().updateRecordMetadata(record, newData, reason);
				return null;
			}

			@Override
			protected void done() {
				try {
					get();
					setWait(false);
					String title = Messages.getString("ViewRecordPropertiesDialog.updateSuccessfulTitle");
					String message = Messages.getString("ViewRecordPropertiesDialog.updateNotesSuccessfulMessage");
					JOptionPane.showMessageDialog(getParent(), message, title,
							JOptionPane.INFORMATION_MESSAGE);

					notes.setText(newNotesText);
					pack();
				} catch (InterruptedException e) {
					setWait(false);
					ExceptionsHelper.handleInterruptedException(e);
				} catch (ExecutionException e) {
					setWait(false);
					Throwable cause = e.getCause();
					if (cause instanceof ConnectException) {
						ExceptionsHelper.handleConnectException(
								ViewRecordPropertiesDialog.this,
								(ConnectException) cause);
					} else if (cause instanceof SocketTimeoutException) {
						ExceptionsHelper.handleSocketTimeoutException(
								ViewRecordPropertiesDialog.this, 
								(SocketTimeoutException) cause);
					} else if (cause instanceof IOException) {
						ExceptionsHelper.handleIOException(
								ViewRecordPropertiesDialog.this,
								(IOException) cause, false);
					} else if (cause instanceof NotAuthorisedFault) {
						ExceptionsHelper.handleNotAuthorisedFault(
								ViewRecordPropertiesDialog.this,
								(NotAuthorisedFault) cause);
					} else if (cause instanceof RemoteServiceFault) {
						ExceptionsHelper.handleRemoteServiceFault(
								ViewRecordPropertiesDialog.this,
								(RemoteServiceFault) cause);
					} else if (cause instanceof EntrySAMLException) {
						ExceptionsHelper.handleEntrySAMLException(
								ViewRecordPropertiesDialog.this,
								(EntrySAMLException) cause);
					} else {
						ExceptionsHelper.handleFatalException(cause);
					}
				}
			}
		};
		setWait(true);
		SwingWorkerExecutor.getInstance().execute(worker);
	}

	private void handleScheduleStartDateEdit() {

		JLabel newSchStartDateLabel = new JLabel(record.getDataSet().getScheduleStartQuestion());
		JLabel newSchStartDateVal = new JLabel();
		BasicDatePicker newSchStartDatePicker = new BasicDatePicker(System.currentTimeMillis());
		if ( null != record.getScheduleStartDate() ){
			newSchStartDatePicker.setDate(record.getScheduleStartDate());
		}

		EditRecordPropertiesDialog dialog = new EditRecordPropertiesDialog(this.getParent(), Messages.getString("ViewRecordPropertiesDialog.editRecordPropertiesDialogTitle"), newSchStartDateLabel, newSchStartDateVal, newSchStartDatePicker, true);
		pack();
		final String reason = dialog.getReason();
		if (!dialog.hasUpdated()) {
			return;
		}

		if ( null != newSchStartDatePicker ){
			if ( !DateValidationHelper.validateDate(newSchStartDatePicker.getDate(), newSchStartDateVal, contentPanel) ){
				String title = Messages.getString("ViewRecordPropertiesDialog.invalidDateTitle");
				String message = Messages.getString("ViewRecordPropertiesDialog.invalidScheduleStartDateMessage");
				JOptionPane.showMessageDialog(getParent(), message, title,
						JOptionPane.ERROR_MESSAGE);
				return;
			}
		}

		final RecordData newData = record.generateRecordData();
		newData.setNotes(record.getNotes());
		newData.setScheduleStartDate(newSchStartDatePicker.getDate());
		newData.setStudyEntryDate(record.getStudyEntryDate());
		record.setScheduleStartDate(newSchStartDatePicker.getDate());

		DateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy");
		final String newDate = dateFormat.format(newSchStartDatePicker.getDate());

		SwingWorker<Object, Object> worker = new SwingWorker<Object, Object>() {
			@Override
			protected Object doInBackground() throws ConnectException, SocketTimeoutException,
			NotAuthorisedFault, IOException, RemoteServiceFault,
			EntrySAMLException {
				//Record must exist in the repository (and therefore have an id) to be updated
				RemoteManager.getInstance().updateRecordMetadata(record, newData, reason);
				return null;
			}

			@Override
			protected void done() {
				try {
					get();
					setWait(false);
					String title = Messages.getString("ViewRecordPropertiesDialog.updateSuccessfulTitle");
					String message = Messages.getString("ViewRecordPropertiesDialog.updateSuccessfulMessage");
					JOptionPane.showMessageDialog(getParent(), message, title,
							JOptionPane.INFORMATION_MESSAGE);

					schStartDateVal.setText(newDate);
					pack();
				} catch (InterruptedException e) {
					setWait(false);
					ExceptionsHelper.handleInterruptedException(e);
				} catch (ExecutionException e) {
					setWait(false);
					Throwable cause = e.getCause();
					if (cause instanceof ConnectException) {
						ExceptionsHelper.handleConnectException(
								ViewRecordPropertiesDialog.this,
								(ConnectException) cause);
					} else if (cause instanceof SocketTimeoutException) {
						ExceptionsHelper.handleSocketTimeoutException(
								ViewRecordPropertiesDialog.this, 
								(SocketTimeoutException) cause);
					} else if (cause instanceof IOException) {
						ExceptionsHelper.handleIOException(
								ViewRecordPropertiesDialog.this,
								(IOException) cause, false);
					} else if (cause instanceof NotAuthorisedFault) {
						ExceptionsHelper.handleNotAuthorisedFault(
								ViewRecordPropertiesDialog.this,
								(NotAuthorisedFault) cause);
					} else if (cause instanceof RemoteServiceFault) {
						ExceptionsHelper.handleRemoteServiceFault(
								ViewRecordPropertiesDialog.this,
								(RemoteServiceFault) cause);
					} else if (cause instanceof EntrySAMLException) {
						ExceptionsHelper.handleEntrySAMLException(
								ViewRecordPropertiesDialog.this,
								(EntrySAMLException) cause);
					} else {
						ExceptionsHelper.handleFatalException(cause);
					}
				}
			}
		};
		setWait(true);
		SwingWorkerExecutor.getInstance().execute(worker);
	}


	private void handleStudyEntryDateEdit() {

		JLabel newStudyEntryDateLabel = new JLabel(studyEntryLabel.getText());
		JLabel newStudyEntryDateVal = new JLabel();
		BasicDatePicker newStudyEntryDatePicker = new BasicDatePicker(System.currentTimeMillis());
		if ( null != record.getStudyEntryDate() ){
			newStudyEntryDatePicker.setDate(record.getStudyEntryDate());
		}

		EditRecordPropertiesDialog dialog = new EditRecordPropertiesDialog(this.getParent(), studyEntryLabel.getText(), newStudyEntryDateLabel, newStudyEntryDateVal, newStudyEntryDatePicker, true);
		pack();
		final String reason = dialog.getReason();
		if (!dialog.hasUpdated()) {
			return;
		}

		if ( null != newStudyEntryDatePicker ){
			if ( !DateValidationHelper.validateDate(newStudyEntryDatePicker.getDate(), newStudyEntryDateVal, contentPanel) ){
				String title = Messages.getString("ViewRecordPropertiesDialog.invalidDateTitle");
				String message = Messages.getString("ViewRecordPropertiesDialog.invalidStudyEntryDateMessage");
				JOptionPane.showMessageDialog(getParent(), message, title,
						JOptionPane.ERROR_MESSAGE);
				return;
			}
		}

		final RecordData newData = record.generateRecordData();
		newData.setNotes(record.getNotes());
		newData.setStudyEntryDate(newStudyEntryDatePicker.getDate());
		newData.setScheduleStartDate(record.getScheduleStartDate());
		record.setStudyEntryDate(newStudyEntryDatePicker.getDate());

		DateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy");
		final String newDate = dateFormat.format(newStudyEntryDatePicker.getDate());

		SwingWorker<Object, Object> worker = new SwingWorker<Object, Object>() {
			@Override
			protected Object doInBackground() throws ConnectException, SocketTimeoutException,
			NotAuthorisedFault, IOException, RemoteServiceFault,
			EntrySAMLException {
				//Record must exist in the repository (and therefore have an id) to be updated remotely
				RemoteManager.getInstance().updateRecordMetadata(record, newData, reason);
				return null;
			}

			@Override
			protected void done() {
				try {
					get();
					setWait(false);
					String title = Messages.getString("ViewRecordPropertiesDialog.propertiesUpdateSuccessfulTitle");
					String message = Messages.getString("ViewRecordPropertiesDialog.propertiosUpdateSuccessMessage");
					JOptionPane.showMessageDialog(getParent(), message, title,
							JOptionPane.INFORMATION_MESSAGE);

					studyEntryDateVal.setText(newDate);
					pack();
				} catch (InterruptedException e) {
					setWait(false);
					ExceptionsHelper.handleInterruptedException(e);
				} catch (ExecutionException e) {
					setWait(false);
					Throwable cause = e.getCause();
					if (cause instanceof ConnectException) {
						ExceptionsHelper.handleConnectException(
								ViewRecordPropertiesDialog.this,
								(ConnectException) cause);
					} else if (cause instanceof SocketTimeoutException) {
						ExceptionsHelper.handleSocketTimeoutException(
								ViewRecordPropertiesDialog.this, 
								(SocketTimeoutException) cause);
					} else if (cause instanceof IOException) {
						ExceptionsHelper.handleIOException(
								ViewRecordPropertiesDialog.this,
								(IOException) cause, false);
					} else if (cause instanceof NotAuthorisedFault) {
						ExceptionsHelper.handleNotAuthorisedFault(
								ViewRecordPropertiesDialog.this,
								(NotAuthorisedFault) cause);
					} else if (cause instanceof RemoteServiceFault) {
						ExceptionsHelper.handleRemoteServiceFault(
								ViewRecordPropertiesDialog.this,
								(RemoteServiceFault) cause);
					} else if (cause instanceof EntrySAMLException) {
						ExceptionsHelper.handleEntrySAMLException(
								ViewRecordPropertiesDialog.this,
								(EntrySAMLException) cause);
					} else {
						ExceptionsHelper.handleFatalException(cause);
					}
				}
			}
		};
		setWait(true);
		SwingWorkerExecutor.getInstance().execute(worker);
	}

	private void handleStatusEdit() {

		JLabel newStatusLabel = new JLabel(Messages.getString("ViewRecordPropertiesDialog.newRecordStatusLabel"));
		JComboBox newStatusCBox = new JComboBox();
		int selectedIndex = -1;

		//decide whether only valid status transitions are to be displayed,
		//or if all status transitions will be possible
		//TODO note that we are re-using the canLoadPendingDocuments method here, which
		//is probably not a great idea. We use it under the assumption that users who have the privileges to reject
		//documents (i.e. CPMs) also have the privileges to change a record's status to anything, rather than one of
		//the permitted status transitions defined for the current status of the object.
		SecurityManager sManager = SecurityManager.getInstance();
		boolean showAllStatuses = false;
		synchronized (sManager) {
			try{
				showAllStatuses = sManager.canLoadPendingDocuments(
						record.getIdentifier().getProjectPrefix(),
						record.getIdentifier().getGroupPrefix());
			}
			catch (Exception ex){
				//do nothing - if we can't contact the security system then we just go with the
				//default behaviour i.e. only defined status transitions are allowed.
			}
		}

		//Calculate the list of eligable statuses
		List<Status> statuses = new ArrayList<Status>();
		if ( showAllStatuses ){
			for ( int i=0, c=record.getDataSet().numStatus(); i<c; i++ ){
				Status  s = record.getDataSet().getStatus(i);
				if ( !s.equals(record.getStatus()) ){
					statuses.add(s);
					newStatusCBox.addItem(s.getLongName());
				}
				/*
				 Removal of this code is a FIX for #1285 - Can't change record status to 'Left' in Edie
				else {
					//There is a bug here. If the current record is the last one in the list,
					//then it won't be added to the status box. Even so, the selected index is set
					//to where the status WOULD have been added - which is a place that doesn't exist.
					
					//I actuall don't see the point of setting the selection index in the 1st place.
					//All this does under all other circumstances is to set the selection index to the 
					//Status that comes after the currently selected status in the list. I don't see how
					//that is useful.
					
					//So basically, just don't set the selection index. Leave it at -1.
					
					//So I am going to comment out this entire clause.
					selectedIndex = i;
				}
				*/
			}
		}
		else{
			for (int i = 0, c = record.getStatus().numStatusTransitions(); i < c; ++i) {
				Status  status = record.getStatus().getStatusTransition(i);
				statuses.add(status);
				newStatusCBox.addItem(status.getLongName());
				if (status.equals(record.getStatus())) {
					//TODO - reevaluate this clause.
					//This clause makes no sense. What this is saying is "If the record's current status can transition
					//to itself, then set itself as the currently selected status. Why would a status ever be able to transition to itself, and even if so,
					//what is the point of setting it as the selected item?
					selectedIndex = i;
				}
			}
		}
		newStatusCBox.setSelectedIndex(selectedIndex);

		//Create dialog box
		EditRecordPropertiesDialog dialog = new EditRecordPropertiesDialog(this.getParent(), Messages.getString("ViewRecordPropertiesDialog.recordPropertiesDlgTitle"), newStatusLabel, newStatusCBox, false);
		pack();
		if (!dialog.hasUpdated()) {
			return;
		}

		//Get the new status
		int index = newStatusCBox.getSelectedIndex();
		final  Status  selectedStatus = statuses.get(index);
		if (selectedStatus.equals(record.getStatus())) {
			//This should never happen?
			String title = Messages.getString("ViewRecordPropertiesDialog.sameStatusTitle");
			String message = Messages.getString("ViewRecordPropertiesDialog.sameStatusMessage");
			JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (!application.isOnline()) {
			//Will only happen the connection is dropped after the dialog is opened
			ExceptionsHelper.handleConnectException(
					ViewRecordPropertiesDialog.this,
					null);
			return;
		}

		if ((record.getStatus().getGenericState().equals(GenericState.ACTIVE)
				|| record.getStatus().getGenericState().equals(GenericState.LEFT))
				&& selectedStatus.getGenericState().equals(GenericState.INACTIVE)) {
			//Status change indicates that the record had consent but is now moving to a state without consent
			String title = Messages.getString("ViewRecordPropertiesDialog.removeConsentTitle");
			String message = Messages.getString("ViewRecordPropertiesDialog.removeConsentMessage");
			JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
			//Ensure the user removes all consent prior to changing to the new status
			handleConsentEdit();
			if (! consentValue.getText().equals(CONSENT_NOT_PRESENT)) {
				String title2 = Messages.getString("ViewRecordPropertiesDialog.removeStatusNotUpdatedTitle");
				String message2 = Messages.getString("ViewRecordPropertiesDialog.removeStatusNotUpdatedMessage");
				JOptionPane.showMessageDialog(this, message2, title2, JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		
		if ((record.getStatus().getGenericState().equals(GenericState.INACTIVE))
				&& (selectedStatus.getGenericState().equals(GenericState.ACTIVE)
						|| selectedStatus.getGenericState().equals(GenericState.LEFT))) {
			//Status change indicates that the record did NOT have consent but is now moving to a state WITH consent.
			String title = Messages.getString("ViewRecordPropertiesDialog.addConsentTitle");
			String message = Messages.getString("ViewRecordPropertiesDialog.addConsentMessage");
			JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
			//Ensure the user adds consent prior to changing to the new status
			handleConsentEdit();
			if (consentValue.getText().equals(CONSENT_NOT_PRESENT)) {
				String title2 = Messages.getString("ViewRecordPropertiesDialog.addStatusNotUpdatedTitle");
				String message2 = Messages.getString("ViewRecordPropertiesDialog.addStatusNotUpdatedMessage");
				JOptionPane.showMessageDialog(this, message2, title2, JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		
		((Record)record).setStatus(selectedStatus);
		if ( RemoteManager.getInstance().isTestDataset() ){
			//Test/preview mode - nothing more to be done
			return;
		}
		
		SwingWorker<Object, Object> worker = new SwingWorker<Object, Object>() {
			@Override
			protected Object doInBackground() throws ConnectException, SocketTimeoutException,
			NotAuthorisedFault, IOException, RemoteServiceFault,
			EntrySAMLException {
				//Record must exist in the repository (and therefore have an id) to be updated
				//otherwise it is just updated locally
				RemoteManager.getInstance().changeRecordStatus(record, selectedStatus);
				if ( selectedStatus.getGenericState().equals(GenericState.INVALID)){
					//remove record from local caches
					PersistenceManager.getInstance().removeRecordFromLocalCaches(record.getIdentifier().getIdentifier());
				}
				return null;
			}

			@Override
			protected void done() {
				try {
					get();
					setWait(false);
					String title = Messages.getString("ViewRecordPropertiesDialog.propertiesUpdateSuccessfulTitle");
					String message = Messages.getString("ViewRecordPropertiesDialog.recordStatusUpdateSuccessMessage");
					JOptionPane.showMessageDialog(getParent(), message, title,
							JOptionPane.INFORMATION_MESSAGE);

					statusValue.setText(selectedStatus.getLongName());
					pack();
				} catch (InterruptedException e) {
					setWait(false);
					ExceptionsHelper.handleInterruptedException(e);
				} 
				catch (ExecutionException e) {
					setWait(false);
					Throwable cause = e.getCause();
					if (cause instanceof ConnectException) {
						ExceptionsHelper.handleConnectException(
								ViewRecordPropertiesDialog.this,
								(ConnectException) cause);
					} else if (cause instanceof SocketTimeoutException) {
						ExceptionsHelper.handleSocketTimeoutException(
								ViewRecordPropertiesDialog.this, 
								(SocketTimeoutException) cause);
					} else if (cause instanceof IOException) {
						ExceptionsHelper.handleIOException(
								ViewRecordPropertiesDialog.this,
								(IOException) cause, false);
					} else if (cause instanceof NotAuthorisedFault) {
						ExceptionsHelper.handleNotAuthorisedFault(
								ViewRecordPropertiesDialog.this,
								(NotAuthorisedFault) cause);
					} else if (cause instanceof RemoteServiceFault) {
						ExceptionsHelper.handleRemoteServiceFault(
								ViewRecordPropertiesDialog.this,
								(RemoteServiceFault) cause);
					} else if (cause instanceof EntrySAMLException) {
						ExceptionsHelper.handleEntrySAMLException(
								ViewRecordPropertiesDialog.this,
								(EntrySAMLException) cause);
					} else {
						ExceptionsHelper.handleFatalException(cause);
					}
				}
			}
		};
		setWait(true);
		SwingWorkerExecutor.getInstance().execute(worker);
	}

	private boolean checkUnfinishedDocument() {
		if (application.getModel().getCurrentDocOccurrenceInstance() != null) {
			String title = "Open Document";
			String message = "Please close the open document before attempting to edit the record properties.";
			WrappedJOptionPane.showWrappedMessageDialog(this.getParent(), message, title, WrappedJOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		return true;
	}

	private void handleConsentEdit() {
		try {
			EditConsentDialog dialog = new EditConsentDialog(this, this.getParent(), record);
			pack();
			if (!dialog.hasUpdated()) {
				return;
			}
			//Refresh the currently open properties dialog
			if (record.getAllConsents() != null && record.getAllConsents().size() > 0) {
				consentValue.setText(CONSENT_PRESENT);	
			}
			else {
				consentValue.setText(CONSENT_NOT_PRESENT);
			}
			statusValue.setText(record.getStatus().getLongName());

			pack();
		} catch (IOException e) {
			setWait(false);
			ExceptionsHelper.handleIOException(getParent(), e, false);
		}
	}

	/**
	 * Used to provide line-wrapping for long text
	 * when using a JLabel. Used specifically for 
	 * the schedule start date as can vary in length
	 * and be quite long.
	 * 
	 * @param question
	 * @return question
	 */
	private String formatQuestion(String question) {
		int chomp = 35;
		if (question.length() < chomp) {
			return question;
		}
		int bits = question.length() / chomp;
		Pattern pattern = Pattern.compile("\\S$");	//Match all non-whitespace
		Pattern pattern2 = Pattern.compile("^\\S");
		String[] pieces = new String[bits+1];
		pieces[0] = question.substring(0, chomp);
		for (int i=1; i<bits+1; i++) {
			//not until end
			int length = chomp*(i+1);
			if (length > question.length()) {
				length = question.length();
			}
			pieces[i] = question.substring(chomp*i, length);
			//If splitting on a word then add a - to the end of the previous line
			Matcher match = pattern.matcher(pieces[i-1]);
			Matcher match2 = pattern2.matcher(pieces[i]);
			boolean wordBreak = false;	//string has split in the middle of a word (ie. no whitespace found)
			if (match.find() && match2.find()) {
				wordBreak = true;
			}

			if (wordBreak && !(i == bits)) {	//ignore the last line
				pieces[i] += "-";
			}
		}
		StringBuilder build = new StringBuilder();
		boolean first = true;
		build.append("<html>");
		for (String piece: pieces) {
			if (first) {
				build.append(piece);
				first = false;
			}
			else {
				build.append("<br>").append(piece);
			}
		}
		build.append("</html>");
		return build.toString();
	}

	/**
	 * Extract a name from the given DN string.
	 * e.g CN=CRO One, OU=users, O=psygrid, C=uk
	 * would return CRO One.
	 * 
	 * @param cnName
	 * @return name
	 */
	private String formatUserName(String dnName) {

		if (dnName == null || dnName.equals("")) {
			return dnName;
		}
		try {
			String[] a = dnName.split(",", 2);
			String[] b = a[0].split("=", 2);
			return b[1];
		}
		catch (Exception e) {
			return dnName;
		}
	}

	private DefaultTableModel buildHistoryTableModel(){

		String[] columnNames = new String[]{"When", "User", "Action"};

		Object[][] data = new Object[record.getHistoryCount()][4];
		for ( int i=0, c=record.getHistoryCount(); i<c; i++ ){
			ChangeHistory history = record.getHistory(i);
			String when = null;
			if ( null != history.getWhenSystem() ){
				when = hhMmSsddMmmYyyy.format(history.getWhenSystem());
			}
			else if ( null != history.getWhen() ){
				when = hhMmSsddMmmYyyy.format(history.getWhen());
			}
			String user = formatUserName(history.getUser());
			data[i] = new String[]{when, user, history.getAction()};
		}

		return new DefaultTableModel(data, columnNames){

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				//Make table read-only
				return false;
			}

		};
	}
}

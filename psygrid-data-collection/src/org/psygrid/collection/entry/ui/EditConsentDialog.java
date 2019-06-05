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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.util.WindowUtils;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.EntryMessages;
import org.psygrid.collection.entry.ExceptionsHelper;
import org.psygrid.collection.entry.SwingWorkerExecutor;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.collection.entry.persistence.RecordStatusMap2;
import org.psygrid.collection.entry.persistence.RecordsList;
import org.psygrid.collection.entry.remote.ESLSubjectNotFoundFault;
import org.psygrid.collection.entry.remote.RemoteManager;
import org.psygrid.collection.entry.remote.RemoteServiceFault;
import org.psygrid.collection.entry.security.DecryptionException;
import org.psygrid.collection.entry.security.EntrySAMLException;
import org.psygrid.common.ui.WrappedJOptionPane;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.utils.security.NotAuthorisedFault;
import org.psygrid.esl.model.ISubject;

import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Dialog window to allow consent for a specified record to be viewed
 * and edited. Launched from the ViewRecordPropertiesDialog.
 * 
 * @author Lucy Bridges
 *
 */
public class EditConsentDialog extends ApplicationDialog {

	private static final long serialVersionUID = 1L;

	/**
	 * The complete record from the repository
	 */
	private Record record;

	private DataSet dataSet;

	private DefaultFormBuilder builder;

	private JButton okButton;

	private JButton cancelButton;

	/**
	 * Current list of consents held by the record.
	 * Holds consentform -> hasConsent
	 */
	private Map<ConsentForm, Boolean> consentMap;

	/**
	 * Current list of consents that have been changed.
	 */
	private Map<ConsentForm, Boolean> consentChangedMap;

	/**
	 * Current list of reasons for withdrawing consent for
	 * consent forms that have been removed. 
	 */
	private Map<ConsentForm, String> consentReasonMap;

	/**
	 * Records whether the Record needs to be entered into the 
	 * ESL after consent is added.
	 */
	private boolean initialEslConsent = false;

	/**
	 * Whether updates have been made to the consent for the
	 * record (i.e whether the 'ok' button has been selected).
	 */
	private boolean updated;

	public EditConsentDialog(ApplicationDialog parentDialog, Application parent, Record record) throws IOException {
		super(parentDialog, parent, Messages.getString("EditConsentDialog.dialogTitle"), true);
		this.record = record;
		initBuilder();
		initComponents();
		pack();
		Dimension size = getSize();
		if (size.width < 600) {
			setSize(600, size.height);   
		}

		setLocation(WindowUtils.getPointForCentering(this));
		initialEslConsent = record.checkConsentForEsl();
		updated = false;
		//See Bug #866
		double height = size.height*1.12;
		setSize(getSize().width, (int)height);

		setVisible(true);
	}

	private void initBuilder() {
		JPanel panel = new JPanel(new BorderLayout());
		builder = new DefaultFormBuilder(new FormLayout("default, 1dlu, 100dlu, default:grow"),  //$NON-NLS-1$
				panel);
		builder.setDefaultDialogBorder();


		//Scroll bar added for long consent forms. See Bug #866
		panel.setAutoscrolls(true);
		JScrollPane scroll = new JScrollPane(panel);
		scroll.setBorder(BorderFactory.createEmptyBorder(5,5,10,5));
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		getContentPane().add(scroll, BorderLayout.CENTER);
	}

	private void initComponents() throws IOException {
		consentMap = new HashMap<ConsentForm, Boolean>();
		consentChangedMap = new HashMap<ConsentForm, Boolean>();
		consentReasonMap = new HashMap<ConsentForm, String>();
		dataSet = getCompleteDataSet();

		for ( int i=0; i<dataSet.numAllConsentFormGroups(); i++ ){
			if ( i>0 ){
				builder.appendUnrelatedComponentsGapRow();
				builder.nextLine(2);                
			}
			ConsentFormGroup cfg = dataSet.getAllConsentFormGroup(i);
			JLabel cfgLabel = new JLabel(cfg.getDescription());
			Font f = cfgLabel.getFont();
			cfgLabel.setFont(new Font(f.getName(), Font.BOLD, f.getSize()));
			builder.append(cfgLabel, builder.getColumnCount());
			builder.appendRow(FormFactory.DEFAULT_ROWSPEC);
			builder.nextLine();
			for ( int j=0; j<cfg.numConsentForms(); j++ ){
				final PrimaryConsentForm pcf = cfg.getConsentForm(j);
				consentMap.put(pcf, false); //No consent present
				consentChangedMap.put(pcf, false); //Consent has not (yet) been changed
				if ( j>0 ){
					JLabel orLabel = new JLabel(Messages.getString("ConsentPanel.orLabel"));
					builder.append(orLabel, builder.getColumnCount());
					builder.appendRow(FormFactory.DEFAULT_ROWSPEC);
					builder.nextLine();
				}
				final JCheckBox pcfCheckBox = new JCheckBox();
				Consent pc = record.getConsent(pcf);
				if ( null != pc && pc.isConsentGiven() ){
					pcfCheckBox.setSelected(true);
					consentMap.put(pcf, true);
				}
				pcfCheckBox.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e){
						boolean change = changeConsent(pcf, ((JCheckBox)e.getSource()).isSelected());
						pcfCheckBox.setSelected(change);
					}
				});

				EntryLabel pcfLabel = new EntryLabel(pcf.getQuestion());
				builder.append(pcfCheckBox);
				builder.append(pcfLabel, 2);
				builder.appendRow(FormFactory.DEFAULT_ROWSPEC);
				builder.nextLine();
				for ( int k=0; k<pcf.numAssociatedConsentForms(); k++ ){
					final AssociatedConsentForm acf = pcf.getAssociatedConsentForm(k);
					consentMap.put(acf, false); //No consent present
					consentChangedMap.put(acf, false); //Consent has not (yet) been changed
					final JCheckBox acfCheckBox = new JCheckBox();
					Consent ac = record.getConsent(acf);
					if ( null != ac && ac.isConsentGiven() ){
						acfCheckBox.setSelected(true);
						consentMap.put(acf, true);
					}
					acfCheckBox.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e){
							boolean change = changeConsent(acf, ((JCheckBox)e.getSource()).isSelected());
							acfCheckBox.setSelected(change);
						}
					});
					EntryLabel acfLabel = new EntryLabel(acf.getQuestion());                    
					builder.append(acfCheckBox);
					builder.append(acfLabel, 2);
					builder.appendRow(FormFactory.DEFAULT_ROWSPEC);
					builder.nextLine();
				}
			}
		}

		okButton = new JButton(EntryMessages.getString("Entry.ok")); //$NON-NLS-1$
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updated = true;
				doDryRun();
			}
		});

		cancelButton = new JButton(EntryMessages.getString("Entry.cancel")); //$NON-NLS-1$
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		//The same as ButtonBarFactory.buildOKCancelBar(okButton, cancelButton)
		//but with increased padding to the side and bottom.
		ButtonBarBuilder builder = new ButtonBarBuilder();
		builder.addGlue();
		builder.addGriddedButtons(new JButton[]{okButton, cancelButton});
		builder.appendRelatedComponentsGapColumn();
		builder.appendUnrelatedComponentsGapRow();
		JPanel buttonsPanel = builder.getPanel();

		getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
	}

	
	/**
	 * Handle change in state of a consent checkbox.
	 * 
	 * @param consentForm The consent form the consent relates to.
	 * @param consentGiven The new consent value
	 * @return 
	 */
	private boolean changeConsent(ConsentForm consentForm, boolean consentGiven) {

		if (!consentGiven) {
			//Show dialog to get reason why consent withdrawn
			WithdrawConsentReasonDialog dlg = new WithdrawConsentReasonDialog(this);
			dlg.setVisible(true);
			String reason = dlg.getReason();
			if ( null == reason ){
				//Assume that Cancel was clicked - revert the change
				consentGiven = true;
				consentMap.put(consentForm, consentGiven);
				consentChangedMap.put(consentForm, false);	//Consent has not been updated
				return consentGiven;
			}
			consentReasonMap.put(consentForm, dlg.getReason());
		}
		else{
			consentReasonMap.put(consentForm, null);
		}

		consentMap.put(consentForm, consentGiven);
		consentChangedMap.put(consentForm, true);	//Consent has been changed

		return consentGiven;
	}


	private DataSet getCompleteDataSet() throws IOException {
		PersistenceManager pManager = PersistenceManager.getInstance();
		synchronized (pManager) {
			return pManager.getData().getCompleteDataSet(
					record.getIdentifier().getProjectPrefix());
		}
	}

	/**
	 * Returns whether the consent has been updated for a Record.
	 * 
	 * i.e whether the 'ok' button was clicked on the dialog.
	 * 
	 * @return updated
	 */
	public boolean hasUpdated() {
		return updated;
	}

	/**
	 * Do a "dry run" of the proposed consent changes. We look to see what
	 * data must be removed if the consent changes are performed.
	 * <p>
	 * If no documents will be removed we go straight on to actually
	 * performing the consent changes. Otherwise a confirmation dialog will
	 * be shown to the user letting them know what will be lost and whether 
	 * they wish to continue.
	 * 
	 */
	private void doDryRun(){
		
		SwingWorker<List<String>, Object> worker = new SwingWorker<List<String>, Object>() {

			@Override
			protected List<String> doInBackground() throws ConnectException, SocketTimeoutException,
			NotAuthorisedFault, IOException, RemoteServiceFault, EntrySAMLException {
				List<String> documentsAtRisk = new ArrayList<String>();
				for ( ConsentForm entry: consentMap.keySet() ){
					//If consent is not present and this has been changed then update the repository
					if ( !consentMap.get(entry) && consentChangedMap.get(entry)) {
						documentsAtRisk.addAll(
								RemoteManager.getInstance().withdrawConsentDryRun(
										record, entry, consentReasonMap.get(entry) ) );
					}
				}
				//we also check for local documents that will no longer have consent:
				documentsAtRisk.addAll(checkLocalDocumentsForConsent());
				
				return documentsAtRisk;
			}

			@Override
			protected void done() {
				try {
					setWait(false);
					List<String> result = get();
					if ( result.isEmpty() ){
						//no remote documents will be deleted as a result of the
						//consent changes so go ahead
						doConsentChange();
					}
					else{
						//show a confirmation dialog
						StringBuilder messageBuilder = new StringBuilder();
						messageBuilder.append(Messages.getString("EditConsentDialog.confirmMessage"));
						messageBuilder.append("\n\n");
						for ( String docName: result ){
							messageBuilder.append(docName);
							messageBuilder.append("\n");
						}
						int click = 
							WrappedJOptionPane.showWrappedConfirmDialog(
								EditConsentDialog.this, 
								messageBuilder.toString(), 
								Messages.getString("EditConsentDialog.confirmTitle"), 
								WrappedJOptionPane.OK_CANCEL_OPTION, 
								WrappedJOptionPane.INFORMATION_MESSAGE);
						if ( WrappedJOptionPane.OK_OPTION == click ){
							//continue with consent changes
							doConsentChange();
						}
						
					}
				} catch (InterruptedException e) {
					setWait(false);
					ExceptionsHelper.handleInterruptedException(e);
				} catch (ExecutionException e) {
					setWait(false);
					Throwable cause = e.getCause();
					if (cause instanceof ConnectException) {
						ExceptionsHelper.handleConnectException(
								EditConsentDialog.this,
								(ConnectException) cause);
					} else if (cause instanceof SocketTimeoutException) {
						ExceptionsHelper.handleSocketTimeoutException(
								EditConsentDialog.this, 
								(SocketTimeoutException) cause);
					} else if (cause instanceof IOException) {
						ExceptionsHelper.handleIOException(
								EditConsentDialog.this,
								(IOException) cause, false);
					} else if (cause instanceof NotAuthorisedFault) {
						ExceptionsHelper.handleNotAuthorisedFault(
								EditConsentDialog.this,
								(NotAuthorisedFault) cause);
					} else if (cause instanceof RemoteServiceFault) {
						ExceptionsHelper.handleRemoteServiceFault(
								EditConsentDialog.this,
								(RemoteServiceFault) cause);
					} else if (cause instanceof EntrySAMLException) {
						ExceptionsHelper.handleEntrySAMLException(
								EditConsentDialog.this,
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
	
	/**
	 * Perform the consent changes, both on the database and in local data
	 * stores.
	 */
	private void doConsentChange(){
		
		SwingWorker<Boolean, Object> worker = new SwingWorker<Boolean, Object>() {
			@Override
			protected Boolean doInBackground() throws ConnectException, SocketTimeoutException,
			NotAuthorisedFault, IOException, RemoteServiceFault,
			EntrySAMLException, DecryptionException {
			
				boolean hasConsent = false;
				for ( ConsentForm entry: consentMap.keySet() ){
					//If consent is present and has changed then update the repository
					if ( consentMap.get(entry) && consentChangedMap.get(entry)){
						//Update local and remote records
						PersistenceManager.getInstance().addLocalConsent(record, entry);
						RemoteManager.getInstance().addConsent(record, entry, consentReasonMap.get(entry));
					}
					//If consent is not present and this has been changed then update the repository
					else if ( !consentMap.get(entry) && consentChangedMap.get(entry)) {
						PersistenceManager.getInstance().removeLocalConsent(record, entry, consentReasonMap.get(entry));
						RemoteManager.getInstance().withdrawConsent(record, entry, consentReasonMap.get(entry));
					}

					if (consentMap.get(entry)) {
						//Consent is still present for at least one consent form
						hasConsent = true;
					}
				}
				RemoteManager.getInstance().updateConsentOnly(dataSet, record);

				//manage any local data affected by the consent changes
				updateLocalRecordsForConsentChanges();
				
				return hasConsent;
				
			}

			@Override
			protected void done() {
				try {
					setWait(false);
					Boolean result = get();
					consentUpdated(result);
				} catch (InterruptedException e) {
					setWait(false);
					ExceptionsHelper.handleInterruptedException(e);
				} catch (ExecutionException e) {
					setWait(false);
					Throwable cause = e.getCause();
					if (cause instanceof ConnectException) {
						ExceptionsHelper.handleConnectException(
								EditConsentDialog.this,
								(ConnectException) cause);
					} else if (cause instanceof SocketTimeoutException) {
						ExceptionsHelper.handleSocketTimeoutException(
								EditConsentDialog.this, 
								(SocketTimeoutException) cause);
					} else if (cause instanceof IOException) {
						ExceptionsHelper.handleIOException(
								EditConsentDialog.this,
								(IOException) cause, false);
					} else if (cause instanceof NotAuthorisedFault) {
						ExceptionsHelper.handleNotAuthorisedFault(
								EditConsentDialog.this,
								(NotAuthorisedFault) cause);
					} else if (cause instanceof RemoteServiceFault) {
						ExceptionsHelper.handleRemoteServiceFault(
								EditConsentDialog.this,
								(RemoteServiceFault) cause);
					} else if (cause instanceof EntrySAMLException) {
						ExceptionsHelper.handleEntrySAMLException(
								EditConsentDialog.this,
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
	
	/**
	 * Get a list of document names for documents that will be removed
	 * if the consent changes requested are applied.
	 * 
	 * @return List of document names.
	 */
	private List<String> checkLocalDocumentsForConsent(){
		PersistenceManager pManager = PersistenceManager.getInstance();
		List<String> noConsentDocNames = new ArrayList<String>();
		if ( !pManager.getData().isAlwaysOnlineMode() ){
			//look for local complete data for the record
			List<DocumentInstance> noConsent = new ArrayList<DocumentInstance>();
			noConsent.addAll(checkLocalDocumentsForConsent(pManager, true));
			noConsent.addAll(checkLocalDocumentsForConsent(pManager, false));
			
			for ( DocumentInstance di: noConsent ){
				noConsentDocNames.add(di.getOccurrence().getCombinedDisplayText());
			}
			
		}
		
		return noConsentDocNames;
	}
	
	/**
	 * Find the document instances stored locally for which there will 
	 * no longer be positive consent if the requested consent changes are
	 * made, for either the complete or incomplete portion of the record.
	 * 
	 * @param pManager Persistence manager.
	 * @param complete If True then look in the complete portion of the record;
	 * if False look in the incomplete portion.
	 * @return The list of document instances that would be removed.
	 */
	private List<DocumentInstance> checkLocalDocumentsForConsent(PersistenceManager pManager, boolean complete){
		List<DocumentInstance> docs = new ArrayList<DocumentInstance>();
		Record r = null;
		try{
			r = pManager.loadRecord(record.getIdentifier(), complete);
		}
		catch(Exception ex){
			//no data - do nothing. Handled as null r below
		}
		if ( null != r ){
			//modify consent on the local record
			for ( ConsentForm cf: consentMap.keySet() ){
				if ( consentChangedMap.get(cf).booleanValue() ){
					Consent c = r.getConsent(cf);
					if ( null == c ){
						if ( consentMap.get(cf).booleanValue() ){
							c = cf.generateConsent();
							c.setConsentGiven(consentMap.get(cf).booleanValue());
							r.addConsent(c);
						}
					}
					else{
						c.setConsentGiven(consentMap.get(cf).booleanValue());
					}
				}
			}
			//see if any document instances inthe record now hae no consent:
			docs = r.findDocInstsWithoutConsent();
		}
		return docs;
	}
	
	
	/**
	 * Manage any data removal of locally stored record.
	 * <p>
	 * Does nothing if user is in always-online mode.
	 * 
	 * @throws DecryptionException
	 * @throws IOException
	 */
	private void updateLocalRecordsForConsentChanges() throws DecryptionException, IOException {
		PersistenceManager pManager = PersistenceManager.getInstance();
		if ( !pManager.getData().isAlwaysOnlineMode() ){
			updateLocalRecordsForConsentChanges(pManager, true);
			updateLocalRecordsForConsentChanges(pManager, false);
		}
	}
	
	
	/**
	 * Manage the data removal for either the complete or incomplete
	 * portion of the record (as specified by the complete argument).
	 * <p>
	 * If an equivalent local record is found it is opened, the consent
	 * is updated then a list of document instances found for which
	 * there is no longer enough consent. These are then deleted
	 * before the record is saved and the record status map updated.
	 * 
	 * @param pManager Persistence manager.
	 * @param complete if True, us the complete portion of the record; 
	 * if False use the incomplete portion
	 * @throws DecryptionException
	 * @throws IOException
	 */
	private void updateLocalRecordsForConsentChanges(PersistenceManager pManager, boolean complete) throws DecryptionException, IOException {
		Record r = null;
		try{
			r = pManager.loadRecord(record.getIdentifier(), complete);
		}
		catch(FileNotFoundException ex){
			//no data - do nothing
		}
		if ( null != r ){
			//modify consent on the local record
			for ( ConsentForm cf: consentMap.keySet() ){
				if ( consentChangedMap.get(cf).booleanValue() ){
					Consent c = r.getConsent(cf);
					if ( null == c ){
						if ( consentMap.get(cf).booleanValue() ){
							c = cf.generateConsent();
							c.setConsentGiven(consentMap.get(cf).booleanValue());
							r.addConsent(c);
						}
					}
					else{
						c.setConsentGiven(consentMap.get(cf).booleanValue());
					}
				}
			}
			//see if any document instances inthe record now have no consent:
			List<DocumentInstance> toRemove = r.findDocInstsWithoutConsent();
			for ( DocumentInstance di: toRemove){
				r.removeDocumentInstance(di);
			}
			if ( 0 == r.numDocumentInstances() ){
				//all document instances removed so the whole record item should
				//be deleted
				RecordsList.Item item = pManager.getRecordsList().getItem(record.getIdentifier(), complete);
				pManager.deleteRecord(item);
				pManager.getRecordsList().removeItem(item);
				pManager.saveRecordsList();
			}
			else{
				pManager.saveRecord(r, complete);
			}
			RecordStatusMap2 rsm = pManager.getRecordStatusMap();
			for ( DocumentInstance di: toRemove ){
				rsm.removeDocStatus(record.getIdentifier().getIdentifier(), di.getOccurrence());
			}
			pManager.saveRecordStatusMap();
		}
	}
	
	
	/**
	 * Show a confirmation to the user when the consent has been successfully
	 * updated then proceed to see if the record status needs to be changed.
	 * 
	 * @param result If False then no consent is now present for the record; if 
	 * True some consent exists.
	 */
	private void consentUpdated(boolean result){
		WrappedJOptionPane.showWrappedMessageDialog(
				EditConsentDialog.this, 
				Messages.getString("EditConsentDialog.updateSuccessfulMessage"), 
				Messages.getString("EditConsentDialog.updateSuccessfulTitle"),
				WrappedJOptionPane.INFORMATION_MESSAGE);

		changeRecordStatus(result);
	}
	
	
	/**
	 * Change the status of the record (if necessary).
	 * <p>
	 * This needs to be done if either (a) all consent has been removed,
	 * so the record needs to be set to an INACTIVE state, or (b) consent 
	 * has been added and the record is currently in a REFERRED state, in 
	 * which case the record needs to be set to an ACTIVE state.
	 * <p>
	 * Otherwise we just proceed to the next step (seeing if the participant
	 * needs to be added to the ESL).
	 */
	private void changeRecordStatus(boolean hasConsent){
		
		Status currentStatus = record.getStatus();
		Status newStatus = null;
		if ( hasConsent ){
			//see if we need to change the record from a REFERRED status
			//to an ACTIVE status
			if ( GenericState.REFERRED.equals(currentStatus.getGenericState()) ){
				//record is currently in Referred state and has consent - need to move to
				//an active state i.e. Consented
				for (int i=0, c=currentStatus.numStatusTransitions(); i<c; i++) {
					Status s = currentStatus.getStatusTransition(i);
					if ( GenericState.ACTIVE.equals(s.getGenericState()) ) {
						newStatus = s;
						break;
					}
				}
			}
			
		}
		else{
			//change the record to an INACTIVE state
			
			List<Status> inactiveStatuses = new ArrayList<Status>();
			for (int i = 0, c=currentStatus.numStatusTransitions(); i<c; i++) {
				if ( currentStatus.getStatusTransition(i).getGenericState().equals(GenericState.INACTIVE) ) {
					inactiveStatuses.add(currentStatus.getStatusTransition(i));
				}
			}
			//Update the Record to an inactive state. Popup a dialog box 
			//with the selection of inactive states if more than one.
			if (inactiveStatuses.size() == 1) {
				newStatus = inactiveStatuses.get(0);
			}
			else if (inactiveStatuses.size() >= 1) {
				JLabel newStatusLabel = new JLabel(Messages.getString("ViewRecordPropertiesDialog.newRecordStatusLabel"));
				JComboBox newStatusCBox = new JComboBox();
				for ( Status s: inactiveStatuses ){
					newStatusCBox.addItem(s.getLongName());
				}
				new EditRecordPropertiesDialog(
						getParentDialog(), getParent(), Messages.getString("ViewRecordPropertiesDialog.recordPropertiesDlgTitle"), 
						newStatusLabel, newStatusCBox, false);
				int index = newStatusCBox.getSelectedIndex();
				if ( index >= 0 ){
					newStatus = inactiveStatuses.get(index);
				}
			}
		}
		
		if ( null == newStatus ){
			//no state change to do so onto the next step
			handleEsl();
		}
		else{
			//save the status change
			((Record)record).setStatus(newStatus);
	
			SwingWorker<Object, Object> worker = new SwingWorker<Object, Object>(){
	
				@Override
				protected Object doInBackground() throws ConnectException, IOException, EntrySAMLException,
				RemoteServiceFault, NotAuthorisedFault{
					PersistenceManager.getInstance().updateRecordStatus(record,  record.getStatus());
					RemoteManager.getInstance().changeRecordStatus(record, record.getStatus());
					return null;
				}
	
				@Override
				protected void done() {
					try{
						setWait(false);
						get();
						WrappedJOptionPane.showWrappedMessageDialog(
								EditConsentDialog.this, 
								Messages.getString("EditConsentDialog.statusUpdateSuccessfulMessage"), 
								Messages.getString("EditConsentDialog.statusUpdateSuccessfulTitle"),
								WrappedJOptionPane.INFORMATION_MESSAGE);
						handleEsl();
						
					} catch (InterruptedException e) {
						ExceptionsHelper.handleInterruptedException(e);
					} catch (ExecutionException e) {
						Throwable cause = e.getCause();
						if (cause instanceof ConnectException) {
							ExceptionsHelper.handleConnectException(
									EditConsentDialog.this,
									(ConnectException) cause);
						} else if (cause instanceof SocketTimeoutException) {
							ExceptionsHelper.handleSocketTimeoutException(
									EditConsentDialog.this, 
									(SocketTimeoutException) cause);
						} else if (cause instanceof IOException) {
							ExceptionsHelper.handleIOException(
									EditConsentDialog.this,
									(IOException) cause, false);
						} else if (cause instanceof NotAuthorisedFault) {
							ExceptionsHelper.handleNotAuthorisedFault(
									EditConsentDialog.this,
									(NotAuthorisedFault) cause);
						} else if (cause instanceof RemoteServiceFault) {
							ExceptionsHelper.handleRemoteServiceFault(
									EditConsentDialog.this,
									(RemoteServiceFault) cause);
						} else if (cause instanceof EntrySAMLException) {
							ExceptionsHelper.handleEntrySAMLException(
									EditConsentDialog.this,
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
	}
	
	/**
	 * Check to see if the participant needs to be added to the ESL;
	 * if there previously wasn't sufficient consent to store their
	 * details but now there is, and they aren't already in	the ESL.
	 */
	private void handleEsl(){
		if ( record.getDataSet().isEslUsed() && !initialEslConsent && record.checkConsentForEsl()){
			SwingWorker<ISubject, Object> worker = new SwingWorker<ISubject, Object>(){
				protected ISubject doInBackground() throws IOException, ESLSubjectNotFoundFault, 
				RemoteServiceFault, EntrySAMLException, NotAuthorisedFault,
				ConnectException, SocketTimeoutException {
					return RemoteManager.getInstance().eslRetrieveSubject(record);
				}
				protected void done() {
					try{
						setWait(false);
						get();
						//subject exists - nothing to do
						//only need to add subject to the ESL if they are not already
						//in there, handled by the ESLSubjectNotFoundFault catch below
						finish();
					} catch (InterruptedException e) {
						setWait(false);
						ExceptionsHelper.handleInterruptedException(e);
					} catch (ExecutionException e) {
						Throwable cause = e.getCause();
						if (cause instanceof ConnectException) {
							ExceptionsHelper.handleConnectException(
									EditConsentDialog.this,
									(ConnectException) cause);
						} else if (cause instanceof SocketTimeoutException) {
							ExceptionsHelper.handleSocketTimeoutException(
									EditConsentDialog.this, 
									(SocketTimeoutException) cause);
						} else if (cause instanceof IOException) {
							ExceptionsHelper.handleIOException(
									EditConsentDialog.this,
									(IOException) cause, false);
						} else if (cause instanceof NotAuthorisedFault) {
							ExceptionsHelper.handleNotAuthorisedFault(
									EditConsentDialog.this,
									(NotAuthorisedFault) cause);
						} else if (cause instanceof ESLSubjectNotFoundFault) {
							//subject not found - needs to be added to the ESL
							addSubjectToEsl();
							
						} else if (cause instanceof RemoteServiceFault) {
							ExceptionsHelper.handleRemoteServiceFault(
									EditConsentDialog.this,
									(RemoteServiceFault) cause);
						} else if (cause instanceof EntrySAMLException) {
							ExceptionsHelper.handleEntrySAMLException(
									EditConsentDialog.this,
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
		else{
			finish();
		}

	}
	
	/**
	 * Launch the ESL dialog so the subject can be added to the ESL.
	 * <p>
	 * Only used when a participant was first added without sufficient 
	 * consent to store their details in the ESL, but this consent is 
	 * subsequently added.
	 */
	private void addSubjectToEsl(){
		EslNewSubjectDialog eslDialog = new EslNewSubjectDialog(getParent(), record);
		eslDialog.setVisible(true);
		finish();
	}
	
	private void finish(){
		dispose();
	}
	
}

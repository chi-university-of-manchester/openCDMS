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

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.collection.entry.EntryMessages;
import org.psygrid.collection.entry.ExceptionsHelper;
import org.psygrid.collection.entry.event.IdentifierEvent;
import org.psygrid.collection.entry.event.IdentifierListener;
import org.psygrid.collection.entry.persistence.DatedProjectType;
import org.psygrid.collection.entry.persistence.IdentifierData;
import org.psygrid.collection.entry.persistence.IdentifiersList;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.collection.entry.remote.RemoteManager;
import org.psygrid.collection.entry.remote.RemoteServiceFault;
import org.psygrid.collection.entry.security.EntrySAMLException;
import org.psygrid.common.ui.WrappedJOptionPane;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.utils.security.NotAuthorisedFault;
import org.psygrid.www.xml.security.core.types.GroupType;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Panel for the {@link NewIdentifierDialog}.
 * <p>
 * Provides controls and logic for selecting the centre, site and 
 * consultant
 * 
 * @author Rob Harper
 *
 */
public class NewIdentifierPanel extends JPanel {
	
	public class JTextFieldLimit extends PlainDocument {
		  private int limit;
		  // optional uppercase conversion
		  private boolean toUppercase = false;
		  
		  JTextFieldLimit(int limit) {
		   super();
		   this.limit = limit;
		   }
		   
		  JTextFieldLimit(int limit, boolean upper) {
		   super();
		   this.limit = limit;
		   toUppercase = upper;
		   }
		 
		  public void insertString
		    (int offset, String  str, AttributeSet attr)
		      throws BadLocationException {
		   if (str == null) return;

		   if ((getLength() + str.length()) <= limit) {
		     if (toUppercase) str = str.toUpperCase();
		     super.insertString(offset, str, attr);
		     }
		   }
		}

	
	private enum StateOfUserInput{
		notStarted,
		participantIdSpecifiedOrNotNeeded,
		groupSpecified,
		siteSpecified
	};

	private class UserInputState{
		
		private StateOfUserInput inputState = StateOfUserInput.notStarted;
		
		public void setInputState(StateOfUserInput inputState){
			this.inputState = inputState;
		
			//consultantCBox centresCBox sitesCBox
			
			switch(inputState){
			case notStarted:
				{
				consultantCBox.setEnabled(false);
				centresCBox.setEnabled(false);
				sitesCBox.setEnabled(false);
					if(patientInitialsText != null){
						patientInitialsText.setEnabled(true);
					}
				}
				break;
			case participantIdSpecifiedOrNotNeeded:
				{
				centresCBox.setEnabled(true);
				}
				break;
			case groupSpecified:
				{
				sitesCBox.setEnabled(true);
				}
				break;
			case siteSpecified:
				{
				consultantCBox.setEnabled(true);
				}
				break;
			}
			
		}
		
		public StateOfUserInput getInputState(){
			return inputState;
		}
		
	};
	
	private static final long serialVersionUID = -6541120380315251822L;

	private static final Log LOG = LogFactory.getLog(NewIdentifierPanel.class);
	
	protected final JDialog dialog;
	
	protected DefaultFormBuilder builder;

	protected JComboBox centresCBox;

	protected JLabel selectGroupLabel;

	protected JComboBox sitesCBox;

	protected JLabel selectSiteLabel;

	protected JComboBox consultantCBox;

	protected JLabel selectConsultantLabel;

	protected JLabel externalIdentifierLabel;
	
	protected JTextField externalIdentifierText;
	
	protected JLabel patientInitialsLabel;
	
	protected JTextField patientInitialsText;

	protected JLabel screenNumAssignedLabel;

	protected EntryLabel screenNumLabel;
	
	protected JButton okButton;

	protected JButton cancelButton;
	

	protected Font boldFont = null;

	
	protected IdentifierData identifierData;

	protected DataSet dataSet;
	
	protected Group selectedGroup;
	
	protected Site selectedSite;
	
	protected String selectedConsultant;
	
	
	protected ItemListener groupListener = null;

	protected ItemListener siteListener = null;

	protected ItemListener consultantListener = null;
	
	private UserInputState userInputSate = new UserInputState();


	protected NewIdentifierPanel(JDialog dialog){
		this.dialog = dialog;
	}
	
	public NewIdentifierPanel(DataSet dataSet, JDialog dialog) throws IdentifierPanelException {
		this.dataSet = dataSet;
		this.dialog = dialog;
		init();
	}

	protected void init() throws IdentifierPanelException {
		initBuilder();
		initComponents();
		initEventHandling();
		build();
		buildButtonBar();
	}

	protected void initBuilder() {
		builder = new DefaultFormBuilder(new FormLayout(
				"50dlu, 3dlu, 125dlu:grow, 3dlu, 125dlu"), this); //$NON-NLS-1$
		builder.setDefaultDialogBorder();
	}

	
	
	protected void initComponents() throws IdentifierPanelException {
		
		
		selectGroupLabel = 
			new JLabel(Messages.getString("IdentifierPanel.selectGroup")); //$NON-NLS-1$
		centresCBox = new JComboBox();

		selectSiteLabel = 
			new JLabel(Messages.getString("IdentifierPanel.selectSite")); //$NON-NLS-1$
		sitesCBox = new JComboBox();
		sitesCBox.setEnabled(false);

		selectConsultantLabel = 
			new JLabel(Messages.getString("IdentifierPanel.selectConsultant")); //$NON-NLS-1$
		consultantCBox = new JComboBox();
		consultantCBox.setEnabled(false);
		
		if(dataSet.isExternalIdUsed()){
			if (!dataSet.getUseExternalIdAsPrimary()){
				this.userInputSate.setInputState(StateOfUserInput.participantIdSpecifiedOrNotNeeded);
				externalIdentifierLabel = new JLabel(Messages.getString("AbstractIdentifierPanel.externalIdentifierLabel"));
				externalIdentifierText = new JTextField();
			}else{
				
				this.userInputSate.setInputState(StateOfUserInput.notStarted);
				//Don't present! Because the user still has to pick the group and all.
				//We DO need to present a text field so that the user can add the participant's initials...
				
				patientInitialsLabel = new JLabel("Participant initials");
				patientInitialsText = new JTextField();
				patientInitialsText.setDocument
		        (new JTextFieldLimit(2));
				 
				 patientInitialsText.getDocument().addDocumentListener(new DocumentListener(){

					public void changedUpdate(DocumentEvent arg0) {
						if(patientInitialsText.getText().length() == 2){
				    		participantInitialsEntered();
				    	}
					}

					
					public void insertUpdate(DocumentEvent arg0) {
						if(patientInitialsText.getText().length() == 2){
				    		participantInitialsEntered();
				    	}
						
					}

					public void removeUpdate(DocumentEvent arg0) {
						if(patientInitialsText.getText().length() == 2){
				    		participantInitialsEntered();
				    	}
						
					}
					 
				 });
				
			}
		
		}else{
			this.userInputSate.setInputState(StateOfUserInput.participantIdSpecifiedOrNotNeeded);
		}
		
		screenNumAssignedLabel = new JLabel(Messages.getString("IdentifierPanel.ScreeningNumberAssigned"));
		
		screenNumLabel = new EntryLabel();
    	Font f = screenNumLabel.getFont();
    	boldFont = f.deriveFont(f.getStyle() ^ Font.BOLD);
        screenNumLabel.setFont(boldFont);
		
		okButton = new JButton(EntryMessages.getString("Entry.ok")); //$NON-NLS-1$
		okButton.setEnabled(false);

		cancelButton = new JButton(EntryMessages.getString("Entry.cancel")); //$NON-NLS-1$
		cancelButton.setEnabled(true);
		
		groupListener = new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					displaySites((Centre)e.getItem());
				}
			}
		};
		
		siteListener = new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					displayConsultants((SiteWrapper)e.getItem());
				}
			}
		};

		consultantListener = new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					displayIdentifier((String) e.getItem());
				}
			}
		};
		
		for (Centre centre : getCentres()) {
			centresCBox.addItem(centre);
		}
		if ( centresCBox.getItemCount() > 1 ){
			centresCBox.setSelectedIndex(-1);
		}
		else{
			centresCBox.setSelectedIndex(0);
			displaySites((Centre)centresCBox.getSelectedItem());
		}
		
		centresCBox.addItemListener(groupListener);

		
	}
	
	public void participantInitialsEntered(){
		if(this.userInputSate.getInputState() == StateOfUserInput.notStarted){
			this.userInputSate.setInputState(StateOfUserInput.participantIdSpecifiedOrNotNeeded);
		}else if(this.userInputSate.getInputState() == StateOfUserInput.siteSpecified){
			this.displayIdentifier(null);
		}
	}
	
	
	/**
	 * Initialize the event handling. Note that the siteListener and
	 * consultantListener are not attached to their respective controls 
	 * at this point; they are only attached when the control becomes active.
	 */
	protected void initEventHandling(){
		
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(dataSet.getUseExternalIdAsPrimary()){
					if(patientInitialsText.getText().length() == 2){
						setIdentifier(e);
					}
				}else{
					setIdentifier(e);
				}
			}
		});

		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancel();
			}
		});

	}
	
	protected void buildButtonBar(){
		JPanel okButtonPanel = ButtonBarFactory.buildOKCancelBar(okButton,
				cancelButton);
		builder.append(okButtonPanel, builder.getColumnCount());
	}
	
	protected void build(){

		if(null != this.patientInitialsLabel){
			builder.append(patientInitialsLabel);
			builder.append(patientInitialsText, 3);
		}
		
		builder.append(selectGroupLabel);
		builder.append(centresCBox, 3);

		builder.append(selectSiteLabel);
		builder.append(sitesCBox, 3);

		builder.append(selectConsultantLabel);
		builder.append(consultantCBox, 3);

		if ( null != externalIdentifierLabel ){
			builder.append(externalIdentifierLabel);
			builder.append(externalIdentifierText, 3);
		}
		


		builder.appendUnrelatedComponentsGapRow();
		builder.nextLine(2);
		builder.append(screenNumAssignedLabel, 3);
		builder.append(screenNumLabel);
		
		builder.appendUnrelatedComponentsGapRow();
		builder.nextLine(2);

	}
	
	/**
	 * Cancel clicked - fire "empty" event.
	 */
	protected void cancel() {
		fireIdentifierEvent(new IdentifierEvent(this, null));
	}

	public void addIdentifierListener(IdentifierListener listener) {
		listenerList.add(IdentifierListener.class, listener);
	}

	public void removeIdentifierListener(IdentifierListener listener) {
		listenerList.remove(IdentifierListener.class, listener);
	}

	protected void fireIdentifierEvent(IdentifierEvent event) {
		Object[] listeners = listenerList.getListenerList();

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == IdentifierListener.class) {
				((IdentifierListener) listeners[i + 1]).identifierChosen(event);
			}
		}
	}
	
	/**
	 * Get the list of centres that the user may select from.
	 * <p>
	 * The list starts as all centres the user has access to,
	 * is then optionally filtered (for special situations), 
	 * and then is sorted.
	 * 
	 * @return List of centres
	 * @throws IdentifierPanelException if no groups are available
	 */
	protected List<Centre> getCentres() throws IdentifierPanelException {
		PersistenceManager pManager = PersistenceManager.getInstance();
		
		//Get the initial list of centre codes
		List<String> groupCodes = new ArrayList<String>();
		if ( pManager.getData().isAlwaysOnlineMode() ){
			//get group list from the repository
		    DatedProjectType project = pManager.getData().getProject(dataSet.getProjectCode());
            try {
                for (GroupType group : RemoteManager.getInstance().getUsersGroupsInProject(project)){
                    groupCodes.add(group.getIdCode());
                }
            } catch (ConnectException e) {
                ExceptionsHelper.handleConnectException(getParent(), e);
            } catch (EntrySAMLException e) {
                ExceptionsHelper.handleEntrySAMLException(getParent(), e);
            } catch (RemoteServiceFault e) {
                ExceptionsHelper.handleRemoteServiceFault(getParent(), e);
            } catch (NotAuthorisedFault e) {
                ExceptionsHelper.handleNotAuthorisedFault(getParent(), e);
            }
		}
		else{
			//get group list from the locally persisted identifiers
			groupCodes = getIdentifiers().getGroups(dataSet.getProjectCode());
		}
				
		//filter the list of centre codes
		groupCodes = filterGroups(groupCodes);

		//handle empty list
		if ( groupCodes.isEmpty() ) {
			throw new IdentifierPanelException("You must belong to at least one group to be able to create new records.", "No Groups");
		}
		
		//Sort the list of centre codes
		Collections.sort(groupCodes);

		//Convert the list of centres codes into a lit of centres that can
		//be displayed in a user-friendly way
		List<Centre> centres = new ArrayList<Centre>();
		for (int j=0; j<groupCodes.size(); j++) {
			String groupCode = groupCodes.get(j);

			for (int i=0; i<dataSet.numGroups(); i++) {
				Group g = dataSet.getGroup(i);
				if (groupCode.equals(g.getName())) {
					centres.add(new Centre(g));
				}
			}
		}
		
		return centres;
	}
	
	/**
	 * Retrieve list of identifiers stored locally for the user.
	 * <p>
	 * Only necessary for online+offline mode where we use it as the
	 * definition of what centres (groups) the user is a member of.
	 * 
	 * @return List of identifiers.
	 */
	protected IdentifiersList getIdentifiers() {
		IdentifiersList identifiers = null;
		if ( RemoteManager.getInstance().isTestDataset() ){
			//Running in test/preview mode.
			//Create some dummy identifiers (one for each group in the dataset)
			identifiers = new IdentifiersList();
			for ( int i=0, c=dataSet.numGroups(); i<c; i++ ){
				Group g = dataSet.getGroup(i);
				Identifier id = new Identifier();
				id.initialize(dataSet.getProjectCode(), g.getName(), 1, 0);
				IdentifierData idd = new IdentifierData(id, -1);
				idd.setUsed(false);
				identifiers.add(idd);
			}
		}
		else{
			//Running in normal mode
			PersistenceManager pManager = PersistenceManager.getInstance();
			synchronized (pManager) {
				identifiers = pManager.getIdentifiers();
			}
		}

		return identifiers;
	}

	/**
	 * Used to filter the initial list of centres (groups).
	 * <p>
	 * Default implementation is empty, subclasses can override
	 * for specific functionality.
	 * 
	 * @param groups All groups.
	 * @return Filtered groups
	 */
	protected List<String> filterGroups(List<String> groups){
		//default implementation is no filtering so just return
		//the input groups unchanged
		return groups;
	}

	/**
	 * Display the list of sites for the selected centre.
	 *  
	 * @param centre The selected centre
	 */
	protected void displaySites(final Centre centre) {
	
		this.userInputSate.setInputState(StateOfUserInput.groupSpecified);
		
		//roll back from previous selections
		okButton.setEnabled(false);
		screenNumLabel.setText("");		
		consultantCBox.removeItemListener(consultantListener); //item listener removed to prevent phantom events
		consultantCBox.removeAllItems();
		consultantCBox.setEnabled(false);		
		sitesCBox.removeItemListener(siteListener); //item listener removed to prevent phantom events
		selectedSite = null;
		selectedConsultant = null;
		
		selectedGroup = centre.getGroup();

		List<SiteWrapper> sites = new ArrayList<SiteWrapper>();
		for (int j=0, d=selectedGroup.numSites(); j<d; j++) {
			Site site = selectedGroup.getSite(j);
			sites.add(new SiteWrapper(site));
		}
		
		if ( sites.isEmpty() ) {
			throw new RuntimeException("No 'sites' configured for this dataset");
		}

		Collections.sort(sites);
		sitesCBox.removeAllItems();
		for ( SiteWrapper site: sites ){
			sitesCBox.addItem(site);
		}
		
		sitesCBox.setEnabled(true);
		if ( 1 == sitesCBox.getItemCount() ){
			//only one site for this centre, so select it automatically
			sitesCBox.setSelectedIndex(0);
			displayConsultants((SiteWrapper)sitesCBox.getSelectedItem());
		}
		else{
			sitesCBox.setSelectedIndex(-1);
		}
		
		//add the listener now that everything is set up
		sitesCBox.addItemListener(siteListener);
	}

	/**
	 * Display the consultants for the selected site
	 * 
	 * @param site The selected site
	 */
	protected void displayConsultants(final SiteWrapper site) {

		this.userInputSate.setInputState(StateOfUserInput.siteSpecified);
		
		//roll back from previous selections
		okButton.setEnabled(false);
		consultantCBox.removeItemListener(consultantListener);
		selectedConsultant = null;
		
		selectedSite = site.getSite();

		List<String> consultants = selectedSite.getConsultants();
		consultantCBox.removeAllItems();
		for (String c : consultants) {
			consultantCBox.addItem(c);
		}	
		
		consultantCBox.setEnabled(true);		
		if ( 0 == consultantCBox.getItemCount() ){
			//No consultants defined - this is OK
			consultantCBox.setEnabled(false);
			displayIdentifier(null);
		}
		else if ( 1 == consultantCBox.getItemCount() ){
			//only one consultant for this site so select it automatically
			consultantCBox.setSelectedIndex(0);
			displayIdentifier((String)consultantCBox.getSelectedItem());
		}
		else{
			consultantCBox.setSelectedIndex(-1);
		}
		
		//add the listener now that everything is set up
		consultantCBox.addItemListener(consultantListener);
	}

	/**
	 * On selection of the consultant find the next identifier for a
	 * participant in the selected centre (group) and display this
	 * to the user.
	 * 
	 * @param consultant The selected consultant
	 */
	protected void displayIdentifier(String consultant) {
		
		selectedConsultant = consultant;
		
		final Long dataSetId;
		if ( RemoteManager.getInstance().isTestDataset() ){
			//Running in test/preview mode
			//The dataset has not been saved so doesn't have an ID yet,
			//so we use -1 instead.
			dataSetId = Long.valueOf(-1);
		}
		else{
			//Running in normal mode
			dataSetId = dataSet.getId();			
		}
		
		//find the next unused identifier stored locally for the selected centre (group)
		identifierData = getIdentifiers().getUnused(dataSetId, selectedGroup.getName());
		
		if ( null == identifierData ) {
			//No unused identifiers found locally so we need to call to the repository
			//to allocate one/some more. 
			//TODO This should be done in a background thread as it might take
			//but I could not get it to sequence properly when the "no identifiers" dialog
			//has to be shown in the case that everything is done before the dialog is 
			//visible i.e. one centre, one site, one consultant
			IdentifiersList identifiers = getNewIdentifiers(selectedGroup.getName());
			if ( null == identifiers ){
				WrappedJOptionPane.showWrappedMessageDialog(
						dialog, 
						Messages.getString("IdentifierPanel.NoScreeningNumbersMessage"), 
						Messages.getString("IdentifierPanel.NoScreeningNumbersTitle"), 
						WrappedJOptionPane.ERROR_MESSAGE);
				return;					
			}
			identifierData = identifiers.getUnused(dataSetId, selectedGroup.getName());
			if (identifierData == null) {
				throw new IllegalStateException("identifierData is null " + //$NON-NLS-1$
						"after updating identifiers from the repository." + //$NON-NLS-1$
				"This usually happens if a stale dataset is being used."); //$NON-NLS-1$
			}	
		}

		String screeningNumber = identifierData.getIdentifier().getIdentifier();
		
		if(this.patientInitialsText != null){
			
			screeningNumber = this.constructExternalIdentifier(identifierData.getIdentifier());
			
		}
		
		
		
        screenNumAssignedLabel.setText(Messages.getString("IdentifierPanel.ScreeningNumberAssigned"));
        screenNumLabel.setText(screeningNumber);
        okButton.setEnabled(true);

	}

	/**
	 * Retrieve new identifier(s) for the specified centre (group) from the repository.
	 * 
	 * @param group The centre (group)
	 * @return List of new identifiers. if <code>null</code> then there was a problem
	 */
	protected IdentifiersList getNewIdentifiers(String group) {
		// There are no unused identifiers
		// Need to go retrieve some more from the repository -
		// if we cannot connect, then issue warning to the user
		if (LOG.isInfoEnabled()) {
			LOG.info("No unused identifiers available for dataset id: " + dataSet.getId() //$NON-NLS-1$
					+ ". Trying to retrieve from the repository."); //$NON-NLS-1$
		}
		IdentifiersList identifiers = null;
		try {
			RemoteManager.getInstance().updateIdentifiersOnly(dataSet, group);
			PersistenceManager pManager = PersistenceManager.getInstance();
			synchronized (pManager) {
				identifiers = pManager.getIdentifiers();
			}
		} catch (Exception e) {
			//do nothing - not being able to retrieve new identifiers will be handled 
			//by the caller due to the returned IdentifiersList being null
		}
		
		return identifiers;
	}
	
	private String constructExternalIdentifier(Identifier id){
		
		String participantInitials = this.patientInitialsText.getText();
		//Get the group.
		String group = id.getGroupPrefix();
		String uniqueId = Integer.toString(id.getSuffix()); 
		
		//The unique id needs to be at least 3 characters long. If it's less than 3,
		//then prepend with leading zeroes enough to make it 3 characters long.
		
		int uniqueIdLength = uniqueId.length();
		int numZeroesToPrepend = 0; 
		if(uniqueIdLength < 3){
			numZeroesToPrepend = 3 - uniqueIdLength;
		}
		
		for(int count = 0; count < numZeroesToPrepend; count++){
			uniqueId = "0" + uniqueId;
		}
		
		StringBuffer theExternalId = new StringBuffer();
		theExternalId.append(group);
		theExternalId.append(uniqueId);
		theExternalId.append(participantInitials);
		
		return theExternalId.toString();
		
	}

	/**
	 * OK clicked. Construct a new Record with the chosen identifier, site and
	 * consultant the fire an event containing this to be received by the caller.
	 * 
	 * @param event
	 */
    protected void setIdentifier(ActionEvent event) {

		final Record record = dataSet.generateInstance();
		if(dataSet.getUseExternalIdAsPrimary() == true){
			((Record)record).setUseExternalIdAsPrimary(true);
		}
		Identifier id = identifierData.getIdentifier();
		record.setIdentifier(id);
		record.setSite(selectedSite);
		record.setConsultant(selectedConsultant);
		if (!setLastUsed(identifierData.getIdentifier(), identifierData.getDataSetId())) {
			return;
		}
		if ( dataSet.isExternalIdUsed() ){
			
			if(!dataSet.getUseExternalIdAsPrimary()){
				String externalId = externalIdentifierText.getText();
				if ( null == externalId || 0 == externalId.length() ){
					JOptionPane.showMessageDialog(this, Messages.getString("AbstractChoiceIdentifierPanel.externalIdentifierRequiredMessage"), Messages.getString("AbstractChoiceIdentifierPanel.externalIdentifierRequiredTitle"), JOptionPane.ERROR_MESSAGE);
					return;
				}
				record.setExternalIdentifier(externalId);
			}else{
				//Need to put the external identifier together now.
				String externalId = this.constructExternalIdentifier(id);
				record.setExternalIdentifier(externalId);
				
			}
		}
		fireIdentifierEvent(new IdentifierEvent(this, record));
	}

    /**
     * Make the selected identifier the last used in the list.
     * <p>
     * TODO Not sure if this is actually required now?
     * 
     * @param identifier The identifier
     * @param dataSetId The ID of the dataset
     * @return True if successful, False otherwise.
     */
	protected boolean setLastUsed(Identifier identifier, long dataSetId) {
		PersistenceManager pManager = PersistenceManager.getInstance();
		synchronized (pManager) {
			pManager.getIdentifiers().setLastUsed(identifier, dataSetId);
			try {
				pManager.saveIdentifiers();
				return true;
			} catch (IOException e) {
				ExceptionsHelper.handleIOException(getParent(), e, false);
				return false;
			}
		}
	}

	/**
	 * Simple class to encapsulate a centre (group) to allow for
	 * formatted display in the dropdown. 
	 * 
	 * @author Rob Harper
	 *
	 */
	public class Centre {
		final Group group;
		public Centre(Group group){
			this.group = group;
		}
		public Group getGroup(){
			return group;
		}
		@Override
		public String toString() {
			String longName = group.getLongName();
			if (longName != null && longName.length()>0 ) {
				return longName+" ("+group.getName()+")";
			}
			else{
				return group.getName();
			}
		}
	}
	
	/**
	 * Simple class to encapsulate a site to allow for sorting
	 * and display in the dropdown.
	 * 
	 * @author Rob Harper
	 *
	 */
	private class SiteWrapper implements Comparable<SiteWrapper> {
		final Site site;
		public SiteWrapper(Site site){
			this.site = site;
		}
		public Site getSite(){
			return site;
		}
		@Override
		public String toString() {
			return site.getSiteName();
		}
		public int compareTo(SiteWrapper o) {
			return this.toString().compareTo(o.toString());
		}
	}
}

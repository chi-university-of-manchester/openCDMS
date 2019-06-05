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


package org.psygrid.collection.entry;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;

import javax.help.CSH;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.Timer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.jdic.desktop.Desktop;
import org.jdesktop.jdic.desktop.DesktopException;
import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXStatusBar;
import org.jdesktop.swingx.util.WindowUtils;
import org.psygrid.collection.entry.action.ChangePasswordAction;
import org.psygrid.collection.entry.action.ExitAction;
import org.psygrid.collection.entry.action.GenerateReportAction;
import org.psygrid.collection.entry.action.HelpAboutAction;
import org.psygrid.collection.entry.action.LinkRecordsAction;
import org.psygrid.collection.entry.action.LoadDocumentAction;
import org.psygrid.collection.entry.action.NewIdentifierChooserAction;
import org.psygrid.collection.entry.action.PrintDocumentAction;
import org.psygrid.collection.entry.action.PrintRecordAction;
import org.psygrid.collection.entry.action.PrintTemplateDocumentAction;
import org.psygrid.collection.entry.action.RebuildLocalCachesAction;
import org.psygrid.collection.entry.action.SaveIncompleteDocumentAction;
import org.psygrid.collection.entry.action.SendLogsByEmailAction;
import org.psygrid.collection.entry.action.SettingsAction;
import org.psygrid.collection.entry.action.SyncLinkedRecordsAction;
import org.psygrid.collection.entry.chooser.ChoosableException;
import org.psygrid.collection.entry.chooser.ChooserHelper;
import org.psygrid.collection.entry.chooser.FileTypeNotRecognizedException;
import org.psygrid.collection.entry.chooser.ImportFileChooser;
import org.psygrid.collection.entry.chooser.ImportFileTypeValidator;
import org.psygrid.collection.entry.chooser.Messages;
import org.psygrid.collection.entry.chooser.NoFileSelectedException;
import org.psygrid.collection.entry.event.ApplyImportEnabledListener;
import org.psygrid.collection.entry.event.ApplyImportEvent;
import org.psygrid.collection.entry.event.ApplyStdCodeEvent;
import org.psygrid.collection.entry.event.ApplyStdCodeListener;
import org.psygrid.collection.entry.event.AutoSaveListener;
import org.psygrid.collection.entry.event.CloseDocumentEvent;
import org.psygrid.collection.entry.event.CloseDocumentListener;
import org.psygrid.collection.entry.event.ConnectionAvailableListener;
import org.psygrid.collection.entry.event.DocOccurrenceCompletedEvent;
import org.psygrid.collection.entry.event.DocOccurrenceCompletedListener;
import org.psygrid.collection.entry.event.ExitTimeoutListener;
import org.psygrid.collection.entry.event.IdentifierEvent;
import org.psygrid.collection.entry.event.IdentifierListener;
import org.psygrid.collection.entry.event.InsertAfterSecOccInstEvent;
import org.psygrid.collection.entry.event.InsertAfterSecOccInstListener;
import org.psygrid.collection.entry.event.InsertBeforeSecOccInstEvent;
import org.psygrid.collection.entry.event.InsertBeforeSecOccInstListener;
import org.psygrid.collection.entry.event.LoginTimeoutListener;
import org.psygrid.collection.entry.event.MultipleSectionOccEvent;
import org.psygrid.collection.entry.event.MultipleSectionOccListener;
import org.psygrid.collection.entry.event.PrintDocumentEvent;
import org.psygrid.collection.entry.event.PrintDocumentListener;
import org.psygrid.collection.entry.event.RemoveSecOccInstEvent;
import org.psygrid.collection.entry.event.RemoveSecOccInstListener;
import org.psygrid.collection.entry.event.SectionAdapter;
import org.psygrid.collection.entry.event.SectionChangedEvent;
import org.psygrid.collection.entry.event.SectionListener;
import org.psygrid.collection.entry.event.ValidationEvent;
import org.psygrid.collection.entry.event.ValidationListener;
import org.psygrid.collection.entry.externaldocparser.AbstractExternalDocumentParser;
import org.psygrid.collection.entry.externaldocparser.ExternalDocumentParserFactory;
import org.psygrid.collection.entry.externaldocparser.ParserException;
import org.psygrid.collection.entry.externaldocparser.RecognizedFileType;
import org.psygrid.collection.entry.externaldocparser.SelectedFileInfo;
import org.psygrid.collection.entry.externaldocparser.AbstractExternalDocumentParser.ParseResults;
import org.psygrid.collection.entry.model.SectionPresModel;
import org.psygrid.collection.entry.persistence.IdentifierData;
import org.psygrid.collection.entry.persistence.IdentifiersList;
import org.psygrid.collection.entry.persistence.PersistenceData;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.collection.entry.persistence.SecondaryIdentifierMap;
import org.psygrid.collection.entry.persistence.UnfinishedDocInstance;
import org.psygrid.collection.entry.print.PrintPdfRenderer;
import org.psygrid.collection.entry.remote.RemoteCommitAction;
import org.psygrid.collection.entry.remote.RemoteManager;
import org.psygrid.collection.entry.remote.RemoteServiceFault;
import org.psygrid.collection.entry.remote.RemoteUpdateAction;
import org.psygrid.collection.entry.renderer.RendererHelper;
import org.psygrid.collection.entry.security.DecryptionException;
import org.psygrid.collection.entry.security.EntrySAMLException;
import org.psygrid.collection.entry.ui.AnnotationDialog;
import org.psygrid.collection.entry.ui.ChooseStdCodeDialog;
import org.psygrid.collection.entry.ui.ConsentDialog;
import org.psygrid.collection.entry.ui.DdeIdentifierDialog;
import org.psygrid.collection.entry.ui.ERCConsentDialog;
import org.psygrid.collection.entry.ui.EslNewSubjectDialog;
import org.psygrid.collection.entry.ui.IdentifierPanelException;
import org.psygrid.collection.entry.ui.InfiniteProgressPanel;
import org.psygrid.collection.entry.ui.NewERCIdentifierDialog;
import org.psygrid.collection.entry.ui.NewIdentifierDialog;
import org.psygrid.collection.entry.ui.ProgressWindow;
import org.psygrid.collection.entry.ui.TextFieldWrapper;
import org.psygrid.collection.entry.util.HelpHelper;
import org.psygrid.collection.entry.util.ResetWaitRunnable;
import org.psygrid.collection.entry.util.WaitRunnable;
import org.psygrid.common.security.LoginInterfaceFrame;
import org.psygrid.common.ui.WrappedJOptionPane;
import org.psygrid.data.model.IProvenanceable;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.reporting.Report;
import org.psygrid.data.reporting.renderer.PdfRenderer;
import org.psygrid.data.reporting.renderer.RendererException;
import org.psygrid.data.repository.DuplicateDocumentsFault;
import org.psygrid.data.repository.RepositoryInvalidIdentifierFault;
import org.psygrid.data.repository.RepositoryNoConsentFault;
import org.psygrid.data.repository.RepositoryOutOfDateFault;
import org.psygrid.data.repository.transformer.TransformerFault;
import org.psygrid.data.utils.security.NotAuthorisedFault;

/**
 * It is the main frame of the application and it aggregates the various panels
 * into one user interface. It provides methods to exit the application,
 * display a document instance, create menus and move from one section to another.
 * In conjunction with its model, <code>Application</code> also controls the 
 * workflow of the application.
 * 
 * @author Ismael Juma (ismael@juma.me.uk)
 * @see ApplicationModel
 * @see FormView
 * @see NavigationPanel
 * @see SectionOccurrencesView
 */
public class Application extends LoginInterfaceFrame implements ProgressWindow   {

	private static final long serialVersionUID = 1L;

	private static final Random rng = new Random();
	
	private ApplicationModel model;

	private InformationView informationView;

	private NavigationPanel navigationPanel;

	private RecordView recordView;

	private JScrollPane formScrollPane;

	private FormView formView;

	private JPanel formPanel;

	private JSplitPane mainPanel;

	private final static Log LOG = LogFactory.getLog(Application.class);

	private JXStatusBar statusBar;

	private SectionListener sectionChangedListener;

	private ValidationListener validationListener;

	private DocOccurrenceCompletedListener docOccurrenceCompletedListener;

	private MultipleSectionOccListener multipleSectionOccListener;

	private CloseDocumentListener closeDocumentListener;

	private PrintDocumentListener printDocumentListener;

	private ApplyStdCodeListener applyStdCodeListener;

	private ApplyImportEnabledListener applyImportEnabledListener;

	private InsertBeforeSecOccInstListener insertBeforeSecOccInstListener;

	private InsertAfterSecOccInstListener insertAfterSecOccInstListener;

	private RemoveSecOccInstListener removeSecOccInstListener;

	private JMenuBar menuBar;

	private JMenu fileMenu;

	private JMenu repositoryMenu;

	private JMenu helpMenu;

	private JMenu optionsMenu;

	/* This should probably be moved to ApplicationModel */
	private Action saveIncompleteDocumentAction = new SaveIncompleteDocumentAction(this);

	private JMenuItem emailLogsMenuItem;

	private Boolean loadPendingDocumentsMItemEnabled;

	//private boolean doProxyAuth = false;

	private JLabel indicatorLabel = new JLabel(Icons.getInstance().getIcon("indicator-grey"));

	private boolean online = false;

	private boolean firstStatusUpdate = true;

	/**
	 * Inactivity timeout value to force login
	 */
	private int loginTimeout;
	
	/**
	 *  Default login timeout if the value can't be read from the properties file
	 */
	private static final int DEFAULT_LOGIN_TIMEOUT = 300000;
	
	//The frequency at which to poll for a login timeout
	//If this is large, timeout accuracy will be poor. If it is small, it will load the system
	private static final int POLL_INTERVAL = 60000;

	//Inactivity timeout value to force exit - 5 mins.
	private static final int EXIT_TIMEOUT = 1800000;

	private String frameTitle = null;

	private boolean appTimersStarted = false;

	private JMenu printMenu;

	private JMenu advancedMenu;

	private final List<ConnectionAvailableListener> connectionAvailableListeners = new CopyOnWriteArrayList<ConnectionAvailableListener>();
	
	public Application() {
		init();
		try {
			loginTimeout = Integer.parseInt(Launcher.getClientProperties().getProperty("client.loginTimeout"));
		} catch (NumberFormatException e) {
			loginTimeout = DEFAULT_LOGIN_TIMEOUT;
			LOG.warn("Problem reading loginTimeout from properties file");
		} catch (IOException e) {
			loginTimeout = DEFAULT_LOGIN_TIMEOUT;
			LOG.warn("Not possible to read loginTimeout value from properties file - using default");
		}
	}

	public Runnable getExitWithoutConfirmationRunnable(final boolean saveState) {
		return new Runnable() {
			public void run() {
				exitWithoutConfirmation(saveState);
			}
		};
	}

	public JScrollPane getFormScrollPane() {
		return formScrollPane;
	}

	private String getFrameTitle(String dataSetDisplayText) {
		if(frameTitle==null){
			String appName = EntryMessages.getString("PsyGridDataCollection"); //$NON-NLS-1$
			frameTitle = appName;
			
			String clientSystem = "";
			try {
				clientSystem = Launcher.getClientProperties().getProperty("system.shortname");
			}
			catch (IOException ioe) {
				LOG.warn("Unable to retrieve client system name.", ioe);
			}
			frameTitle += " - "+clientSystem;
		}
		
		if (dataSetDisplayText == null) {
			return frameTitle;
		}
		StringBuilder frameTitle = new StringBuilder(this.frameTitle.length()
				+ dataSetDisplayText.length() + 2);
		frameTitle.append(dataSetDisplayText).append(" - ").append(this.frameTitle); //$NON-NLS-1$
		return frameTitle.toString();
	}

	/**
	 * Clears the model, removes elements from the content pane and refreshes it.
	 * The actual extent of this change depends on the {@code full} parameter.
	 * If it's {@code true}, the model is fully reset as well as the content pane.
	 * Most often, however, it's desirable to clear the form view while while
	 * keeping a given record selected. Passing {@code false} achieves this.
	 */
	public void clear(boolean full) {
		model.clear(full);
		removeElements(full);
		refreshContentPane();
	}

	/**
	 * Allow the user to create a new identifier for the dataset selected.
	 * 
	 * @param dataSet the dataset selected
	 */
	public void setSelectedDataSet(DataSet dataSet) {
		try{
			
			boolean forceRecordCreation = dataSet.getForceRecordCreation();
			boolean isAlwaysOnlineMode = PersistenceManager.getInstance().getData().isAlwaysOnlineMode();
			
			
			String offlineTitle = EntryMessages.getString("Application.noOfflineModeTitle");
			String offlineMessage = EntryMessages.getString("Application.noOfflineModeMessage");
			if(forceRecordCreation && !isAlwaysOnlineMode){
				WrappedJOptionPane.showWrappedMessageDialog(this, offlineMessage, offlineTitle, JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			
			if(forceRecordCreation){
				String confirmMessage = EntryMessages.getString("Application.enforceRecordCreationWarningMessage");
				String confirmTitle = EntryMessages.getString("Application.enforceRecordCreationTitle");
				int result = WrappedJOptionPane.showWrappedConfirmDialog(this, confirmMessage,
						confirmTitle, JOptionPane.YES_NO_OPTION, WrappedJOptionPane.QUESTION_MESSAGE);
		
				if (result == JOptionPane.NO_OPTION) {
					return;
				}
			}
			
			//Allow user to create a new identifier
			final NewIdentifierDialog identifierDialog = (forceRecordCreation ? new NewERCIdentifierDialog(this, dataSet) : new NewIdentifierDialog(this, dataSet));
			
			identifierDialog.getContentPanel().addIdentifierListener(new IdentifierListener() {
				public void identifierChosen(IdentifierEvent event) {
					closeIdentifierDialog(identifierDialog, event.getRecord());
				}
			});
			identifierDialog.setVisible(true);
		}
		catch(IdentifierPanelException ex){
			WrappedJOptionPane.showWrappedMessageDialog(this, ex.getMessage(), ex.getTitle(), WrappedJOptionPane.ERROR_MESSAGE);
		}

	}

	/**
	 * 
	 */
	public Record getRecord() {
		return model.getCurrentRecord();
	}

	/**
	 * Set the record as the one in use and open it in the main application window.
	 *
	 * @param record
	 */
	public void setSelectedRecord(Record record) {
		LOG.info("Record Selected is "+record.getIdentifier().getIdentifier());

		boolean canRandomize = false;
		if ( isOnline() && record.getDataSet().isRandomizationRequired() ){
			try{
				canRandomize = RemoteManager.getInstance().canRecordBeRandomized(record);
			}
			catch(Exception ex){
				LOG.error(ex);
			}
		}
		
		model.clear(true);
		model.setCanRandomize(canRandomize);
		model.setCurrentRecord(record);
		removeElements(true);
		buildMainPanel(createRecordScrollPane(), new JPanel());
		refreshContentPane();
	}

	//TODO TABS
	public void setSelectedDocOccurrenceInstance(
			DocumentInstance docOccurrenceInstance,
			int sectionOccurrenceIndex) {
		setSelectedDocOccurrenceInstance(docOccurrenceInstance, 
				sectionOccurrenceIndex, DocumentStatus.NOT_STARTED);
	}

	//TODO TABS
	/**
	 * Set an existing document instance for an occurrence choosen
	 * from a (Generic)DocChooserLoader.
	 * 
	 * @param docOccurrenceInstance
	 * @param sectionOccurrenceIndex
	 * @param docStatus
	 */
	public void setSelectedDocOccurrenceInstance(
			DocumentInstance docOccurrenceInstance,
			int sectionOccurrenceIndex, DocumentStatus docStatus) {
		LOG.info("In setSelectedDocOccurrenceInstance ");
		if (sectionOccurrenceIndex < 0) {
			throw new IllegalArgumentException("sectionOccurrenceIndex cannot " + //$NON-NLS-1$
			"be smaller than 0"); //$NON-NLS-1$
		}

		docOccurrenceInstance.detachFromRecord();
		model.getCurrentRecord().attach(docOccurrenceInstance);

		if ( DocumentStatus.VIEW_ONLY == docStatus ){
			//if using VIEW_ONLY mode we need to set the status of the
			//document instance to a dummy "View Only" status object.
			//And it has to be done here otherwise the status is lost 
			//during the above detach and attach
			Status status = new Status();
			status.setShortName(docStatus.toStatusLongName());
			status.setLongName(docStatus.toStatusLongName());
			((DocumentInstance)docOccurrenceInstance).setStatus(status);
		}
		
		model.setSelectedDocOccurrenceInstance(docOccurrenceInstance, docStatus);
		
		if (recordView == null) {
			buildMainPanel(createRecordScrollPane(), new JPanel());
		}
		recordView.makeSelectedDocVisible();
		removeElements(false);
		if (!init(docOccurrenceInstance.getOccurrence()))
			return;

		//TODO If a validation error occurs, stop trying to move forward
		while (model.getCurrentSectionIndex() < sectionOccurrenceIndex)
			model.nextSection();

		refreshContentPane();
		setScrollBarToMinimum();
		
		//At this point, the file has been successfully loaded into memory, and it is safe to delete.
		//Or should we delete it upon commit?
	}
	
	/**
	 * Clears any unsavable values (such as NaN or Infinity) from the document instance's provenance history.
	 * Introduces this method as part of solution for #1328.
	 * @param doc
	 */
	private void clearFaultyProvenance(DocumentInstance doc){
		
		//Get all the derived entries in this document instance.
		List<Entry> vulnerableEntries = new ArrayList<Entry>();
		Document theDoc = doc.getOccurrence().getDocument();
		
		int numEntries = theDoc.numEntries();
		for(int i = 0; i < numEntries; i++){
			Entry e = theDoc.getEntry(i);
			if(e instanceof DerivedEntry){
				
				vulnerableEntries.add(e);
				
			}
		}
		
		DocumentInstance docInst = (DocumentInstance)doc;
		for(Entry e : vulnerableEntries){
			List<Response> responses = docInst.getResponses(e);
			for(Response r: responses){
				
				//If the value of the response is currently NaN or Infinity, then change it to a std code.
				//Otherwise the NaN or Infinity will be placed in provenance as soon as the UI event
				//notification stuff kicks in (which would negate this method entirely).
				
				
				Response resp = (Response)r;
				
				Double respValue = ((NumericValue)((BasicResponse)r).getValue()).getValue();
				
				List<StandardCode> stdCodes = this.model.getStandardCodes();
				
				StandardCode dECode = null;
				
				for(StandardCode code : stdCodes){
					if(code.isUsedForDerivedEntry()){
						dECode = code;
						break;
					}
				}
				
				if(respValue != null && (respValue.isInfinite() || respValue.isNaN())){
					BasicResponse bResp = (BasicResponse)resp;
					NumericValue nVal = (NumericValue)bResp.getTheValue();
					nVal.setValue(null);
					nVal.setStandardCode(dECode);
				}
				
				
				List<Integer> indexOfItemsToRemoveFromP = new ArrayList<Integer>();
				List<Integer> indexOfItemsToRemoveFromOV = new ArrayList<Integer>();
				
				List<Provenance> pList = r.getProvenance();
				for(Provenance p: pList){
					Provenance prov = (Provenance)p;
					IProvenanceable cur = prov.getTheCurrentValue();
					IProvenanceable prev = p.getPrevValue();
					
					Double curVal = null, prevVal = null;
					
					if(cur != null){
						try{
							curVal = Double.valueOf(cur.getValueAsString());
						} catch(NumberFormatException ex){
							curVal = null;
						}
					}
					if(prev != null){
						try{
							prevVal = Double.valueOf(prev.getValueAsString());
						} catch(NumberFormatException ex){
							prevVal = null;
						}
					}
					
					
					int index = pList.indexOf(p);
					
					if(curVal != null && (curVal.isInfinite() || curVal.isNaN()) || prevVal != null && (prevVal.isInfinite() || prevVal.isNaN())){
						indexOfItemsToRemoveFromP.add(index);
						
					}
				}
				
				List<Value> oldValues = null;
				BasicResponse bResp = null;
				if(resp instanceof BasicResponse){
					bResp = (BasicResponse)resp;
					oldValues = bResp.getOldValues();
					
					for(Value v : oldValues){
						int index = oldValues.indexOf(v);
						Double dV = v.getValueForStats();
						
						if(dV != null && (dV.isInfinite() || dV.isNaN())){
							indexOfItemsToRemoveFromOV.add(index);
						}
						
					}
					
				}
				
				
				int numItemsToRemove = indexOfItemsToRemoveFromP.size();
				
				//Go in reverse order, as the indices will run lowest to highest.
				//It is important to remove the high indices (at the end of pList) first,
				//In order for the other indices to maintain integrity.
				for(int i = numItemsToRemove-1; i >= 0; i--){
					int removalIndex = indexOfItemsToRemoveFromP.get(i);
					bResp.removeProvenanceItemByIndex(removalIndex);
				}
				
				//We still need to clear the array of oldValues - ??
				numItemsToRemove = indexOfItemsToRemoveFromOV.size();
				
				//Go in reverse order, as the indices will run lowest to highest.
				//It is important to remove the high indices (at the end of pList) first,
				//In order for the other indices to maintain integrity.
				for(int i = numItemsToRemove-1; i >= 0; i--){
					int removalIndex = indexOfItemsToRemoveFromOV.get(i);
					bResp.removeOldValueAtIndex(removalIndex);
				}
				
			}
		}
	}

	/*
	 * Called when a document occurrence is selected from the DocChooserLoader
	 */
	public void setSelectedDocOccurrence(
			DocumentOccurrence docOccurrence, DocumentStatus docStatus) {

		//Get the instance for this document occurrence, or create a new one if it doesn't exist
		DocumentInstance instance = null; 

		Record currentRecord = model.getCurrentRecord();
		try {
			if ( RemoteManager.getInstance().isTestDataset() ){
				//Running in test/preview mode
				//Try to find existing instance for this occurrence in the current record
				instance = (DocumentInstance)currentRecord.getDocumentInstance(docOccurrence);
			}
			else{
				//Running in normal mode
				
				
				if (docStatus != null && DocumentStatus.NOT_STARTED != docStatus){
					if ( DocumentStatus.LOCALLY_INCOMPLETE == docStatus
							|| DocumentStatus.READY_TO_SUBMIT == docStatus)  {

						//Document instance is stored locally on disk
						instance = (DocumentInstance)PersistenceManager.getInstance().getDocumentInstance(currentRecord, docOccurrence);
						if (instance == null) {
							//This will only happen if the document status map is out of date.
							JOptionPane.showMessageDialog(this, 
									"There was a problem opening this document, it cannot be found.\n",
									"Error opening document", //$NON-NLS-1$
									JOptionPane.ERROR_MESSAGE);
							LOG.error("No document instance exists for "+currentRecord.getIdentifier());
							return;
						}
					}
					else if(docStatus != null && docStatus == DocumentStatus.COMMIT_FAILED){
						//Need to load the record from local file.
						try{
							UnfinishedDocInstance instanceWrapper = PersistenceManager.getInstance().loadUncommitableDocInstance(currentRecord, docOccurrence);
							instance = (DocumentInstance)instanceWrapper.getDocOccurrenceInstance();
							instance.detachFromRecord();
							currentRecord.attach(instance);
							
							this.clearFaultyProvenance(instance);
							//delete from disk and update record status map
							try{
								PersistenceManager.getInstance().deleteCommitFailedDocFile(currentRecord, docOccurrence);
								PersistenceManager.getInstance().updateRecord(currentRecord, instance, instanceWrapper.getDocStatus());
							}
							catch(Exception ex){
								//nothing we can do
							}
						}
						catch(Exception e){
							String title = EntryMessages.getString("ApplicationModel.fileLoadProblemTitle");
							String message = EntryMessages.getString("ApplicationModel.fileLoadProblemMessage");
							WrappedJOptionPane.showWrappedMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
							if(LOG.isErrorEnabled()){
								LOG.error("Uncommitable document: " + currentRecord.getIdentifier().getIdentifier() + 
										 " - " + docOccurrence.getName() + " could not be loaded: ", e);
							}
							//delete the file
							try{
								PersistenceManager.getInstance().deleteCommitFailedDocFile(currentRecord, docOccurrence);
								PersistenceManager.getInstance().updateRecord(currentRecord, instance, DocumentStatus.NOT_STARTED);
							}
							catch(Exception ex){
								//nothing we can do
							}
							
						}
						
					}
					else{

						//Document instance is remote
						Record record = null;
						try {
							record = RemoteManager.getInstance().getRecordSingleDocumentFromOccurrence(getRecord(), docOccurrence);

							//DATA REPLICATION
							if ( docStatus != DocumentStatus.INCOMPLETE &&
									null != record.getSecondaryIdentifier() && 
									null != docOccurrence.getSecondaryOccIndex() &&
									null != docOccurrence.getDocument().getSecondaryDocIndex() ){

								//record has a secondary for this instance
								Record secSum = null;
								Document secDoc = null;
								DocumentOccurrence secOcc = null;
								DocumentInstance secInst = null;
								boolean locked = false;
								try{
									secSum = RemoteManager.getInstance().getRecordSummary(record.getSecondaryIdentifier());
									DataSet secDs = secSum.getDataSet();
									secDoc = secDs.getDocument(docOccurrence.getDocument().getSecondaryDocIndex().intValue());
									secOcc = secDoc.getOccurrence(docOccurrence.getSecondaryOccIndex().intValue());
									if ( secOcc.isLocked() ){
										locked = true;
									}
									else{
										secInst = secSum.getDocumentInstance(secOcc);
									}
								}
								catch (Exception ex){
									//do nothing - any exception caused when trying to retrieve the secondary 
									//record will manifest as a null secInst, handled below.
								}
								if ( !locked ){
									if ( null == secInst ){
										//check if there is no secondary document instance because the
										//secondary record has insufficient consent
										boolean hasConsent = true;
										if ( null != secSum && null != secDoc && null != secOcc ){
											DocumentInstance testInst = secDoc.generateInstance(secOcc);
											hasConsent = secSum.checkConsent(testInst);
										}
										if ( hasConsent ){
											//there is consent for the missing secondary document instance - warn
											//the user and recommend that they synchronize
											WrappedJOptionPane.showWrappedMessageDialog(
													this, 
													Messages.getString("DocInstanceChooserDialog.noSecondaryDocInst"), 
													Messages.getString("DocInstanceChooserDialog.error"), 
													WrappedJOptionPane.ERROR_MESSAGE);
											return;
										}
									}
									else{
										Record sec = RemoteManager.getInstance().getRecordSingleDocument(secInst);
										currentRecord.setSecondaryRecord(sec);
									}
								}
							}

						}
						catch (Exception e) {	
							//Have recieved both ConnectExceptions and NPEs when a connection to the repository is not available so am catching a general exception here
							JOptionPane.showMessageDialog(this, 
									"There was a problem retrieving this document from the Repository.\n" +
									"You must be online to view this document.",
									"Error opening document", //$NON-NLS-1$
									JOptionPane.ERROR_MESSAGE);
							LOG.error("Unable to retrieve the document for "+currentRecord.getIdentifier().getIdentifier(), e);
							return;
						}
						if (record != null) {
							instance = (DocumentInstance)record.getDocumentInstance(docOccurrence);
							if (!instance.isEditingPermitted()) {
								//Document cannot be edited
								docStatus = DocumentStatus.VIEW_ONLY;
							}
						}

					}
				}
			}

			if (instance != null) {
				setSelectedDocOccurrenceInstance(instance, 0, docStatus);
				return;
			}
		}
		catch (IOException e) {
			ExceptionsHelper.handleIOException(getParent(), e, false);
		}
		catch (ChoosableException e) {
			ExceptionsHelper.handleException(getParent(), EntryMessages.getString("Application.choosableExceptionTitle"), e, e.getMessage(), false);
		}
		catch (DecryptionException e) {
			ExceptionsHelper.handleFatalException(e);
		}
		catch (Exception e) {
			ExceptionsHelper.handleFatalException(e);
		}

		
		if (!docOccurrence.getDocument().isEditingPermitted()) {
			//show dialog informing the user that they do not have permission to
			//create an instance of this document
			JOptionPane.showMessageDialog(this, 
					EntryMessages.getString("Application.noPermissionToEditDocMessage_p1") +
					ChooserHelper.getDocumentDisplayText(docOccurrence)+
					EntryMessages.getString("Application.noPermissionToEditDocMessage_p2"),
					EntryMessages.getString("Application.noPermissionToEditDocTitle"),
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		instance = (DocumentInstance)docOccurrence.getDocument().generateInstance(docOccurrence);
		currentRecord.addDocumentInstance(instance);
		if ( model.checkDocInstanceConsent(instance) ){
			//there is sufficient consent to be able to create this
			//document instance
			model.setSelectedDocOccurrenceInstance(instance, DocumentStatus.NOT_STARTED);
			model.checkCurrentDocOccurrenceInstance();
			removeElements(false);
			if (!init(model.getCurrentDocOccurrence())) {
				return;
			}
			refreshContentPane();
			setScrollBarToMinimum();
			showLostConnectivityWarning();
		}
		else{
			//remove instance from record
			currentRecord.removeDocumentInstance(instance);
			//show dialog informing the user that there is not sufficient
			//consent to create the document instance
			JOptionPane.showMessageDialog(this, 
					EntryMessages.getString("Application.noConsentMessage1") + //$NON-NLS-1$
					ChooserHelper.getDocumentDisplayText(instance.getOccurrence())+
					EntryMessages.getString("Application.noConsentMessage2") + //$NON-NLS-1$
					instance.getRecord().getIdentifier().getIdentifier(),
					EntryMessages.getString("Application.noConsentTitle"), //$NON-NLS-1$
					JOptionPane.ERROR_MESSAGE);
		}

	}

	public void addConnectionAvailableListener(ConnectionAvailableListener listener) {
		connectionAvailableListeners .add(listener);
	}

	public void removeConnectionAvailableListener(ConnectionAvailableListener listener) {
		connectionAvailableListeners.remove(listener);
	}

	private void fireConnectionAvailableEvent(boolean available) {
		for (ConnectionAvailableListener l : connectionAvailableListeners)
			l.statusChanged(available);
	}
	
	private void updateMenuStatus() {
		if ( RemoteManager.getInstance().isTestDataset() ){
			//Running in test/preview mode
			//Disable all menu items that will result in communication
			//with the repository and/or security system, and also the 
			//save as incomplete option (there is no need for it and it may
			//complicate things)
			saveIncompleteDocumentAction.setEnabled(false);
			emailLogsMenuItem.setEnabled(false);
			for ( int i=0, c=repositoryMenu.getItemCount(); i<c; i++ ){
				JMenuItem item = repositoryMenu.getItem(i);
				if ( null != item ){
					item.setEnabled(false);
				}
			}
			for ( int i=0, c=optionsMenu.getItemCount(); i<c; i++ ){
				JMenuItem item = optionsMenu.getItem(i);
				if ( null != item ){
					item.setEnabled(false);
				}
			}
		}
		else{
			//Running in normal mode
			DocumentInstance docInstance = model.getCurrentDocOccurrenceInstance();
			boolean enabled;
			if (docInstance == null) {
				enabled = false;
			}
			else {
				Status status = model.getCurrentDocOccurrenceInstance().getStatus();
				if (status == null) {
					//it's likely a document has been re-opened when CoCoA
					//started and that the previous status was VIEW_ONLY
					enabled = false;
				}
				else {
					DocumentStatus docStatus = DocumentStatus.valueOf(status);
					
					
					if (docStatus != DocumentStatus.INCOMPLETE) {
						
						enabled = false;
						
						//Check to see if the document status might be commit_failed.
						DocumentStatus docOccStatus = PersistenceManager.getInstance().getRecordStatusMap().getDocumentStatus(model.getCurrentRecord(), model.getCurrentDocOccurrence());
						if(docOccStatus == DocumentStatus.COMMIT_FAILED){
							enabled = true;
						}
					}
					else {
						enabled = true;
					}
				}
			}
			saveIncompleteDocumentAction.setEnabled(enabled);
		}
	}

	/**
	 * Refreshes the record view. This method should be called if some operation
	 * modifies the current record or its documents.
	 */
	public void refreshRecordView() {
		if ( null != recordView ){
			recordView.revalidate();
		}
	}

	@Override
	public void refreshContentPane() {
		if (getContentPane() instanceof JPanel) {
			JPanel contentPane = (JPanel) getContentPane();
			contentPane.revalidate();
			contentPane.repaint();
		}
	}

	/**
	 * Remove all listeners and either mainPanel or just its right component.
	 * This is required because we add the listeners again in
	 * {@link #init(org.psygrid.data.model.hibernate.DocumentOccurrence)} and we don't want to receive every
	 * event multiple times.
	 * 
	 * @param removeMainPanel
	 *            if {@code true}, mainPanel is removed. Otherwise, the right
	 *            component is removed from mainPanel, but the left one is
	 *            retained.
	 */
	public void removeElements(boolean removeMainPanel) {
		if (mainPanel != null) {
			if (removeMainPanel) {
				if (recordView != null) {
					recordView.dispose();
					recordView = null;
				}
				getContentPane().remove(mainPanel);
				mainPanel = null;
			}
			else {
				if (mainPanel.getRightComponent() != null)
					/* We use a dummy component to keep sizes correct */
					setMainPanelRightComponent(new JPanel());
			}
		}

		if (informationView != null)
			informationView.dispose();

		if (closeDocumentListener != null) {
			model.removeCloseDocumentListener(closeDocumentListener);
			closeDocumentListener = null;
		}
		if (sectionChangedListener != null) {
			model.removeSectionListener(sectionChangedListener);
			sectionChangedListener = null;
		}

		if (validationListener != null) {
			model.removeValidationListener(validationListener);
			validationListener = null;
		}

		if (docOccurrenceCompletedListener != null) {
			model.removeDocOccurrenceCompletedListener(
					docOccurrenceCompletedListener);
			docOccurrenceCompletedListener = null;
		}

		if (multipleSectionOccListener != null) {
			model.removeMultipleSectionOccListener(multipleSectionOccListener);
			multipleSectionOccListener = null;
		}

		if (printDocumentListener != null) {
			model.removePrintDocumentListener(printDocumentListener);
			printDocumentListener = null;
		}

		if (applyStdCodeListener != null) {
			model.removeApplyStdCodeListener(applyStdCodeListener);
			applyStdCodeListener = null;
		}

		if (applyImportEnabledListener != null) {
			model.removeApplyImportEnabledListener(applyImportEnabledListener);
			applyImportEnabledListener = null;
		}

		if (insertBeforeSecOccInstListener != null) {
			model.removeInsertBeforeSecOccInstListener(insertBeforeSecOccInstListener);
			insertBeforeSecOccInstListener = null;
		}

		if (insertAfterSecOccInstListener != null) {
			model.removeInsertAfterSecOccInstListener(insertAfterSecOccInstListener);
			insertAfterSecOccInstListener = null;
		}

		if (removeSecOccInstListener != null) {
			model.removeRemoveSecOccInstListener(removeSecOccInstListener);
			removeSecOccInstListener = null;
		}

		updateMenuStatus();
	}

	private void closeIdentifierDialog(NewIdentifierDialog identifierDialog, Record record){
		identifierDialog.dispose();
		if ( null != record ){
			setRecordFromIdentifierDialog(record);
		}
	}

	/**
	 * The identifier for a new participant record has been selected.
	 * <p>
	 * Next step is to gather consent and (if applicable) participant 
	 * register data.
	 * 
	 * @param record The new participant record.
	 */
	private void setRecordFromIdentifierDialog(final Record record) {

		//if there is consent to gather, or a schedule start question to answer,
		//then we need to show the consent dialog
		
		boolean enforceRecordCreation = record.getDataSet().getForceRecordCreation();
		
		DataSet ds = record.getDataSet();
		if ( ds.numAllConsentFormGroups() > 0 || null != ds.getScheduleStartQuestion() ){
			ConsentDialog dialog = (enforceRecordCreation ? new ERCConsentDialog(this,record) : new ConsentDialog(this, record));
			dialog.setVisible(true);
			if ( !dialog.isOkClicked() ){
				return;
			}

			//If the record's project uses the participant register, and there
			//is the necessary consent, show the Participant Register dialog
			//to allow the participant's identifiable data to be stored
			if ( ds.isEslUsed() ){
				if ( record.checkConsentForEsl() ){
					//launch ESL dialog
					EslNewSubjectDialog eslDialog = new EslNewSubjectDialog(this, record);
					eslDialog.setVisible(true);
					if ( !eslDialog.isEslSaveSuccessful() ){
						return;
					}
				}
				else{
					WrappedJOptionPane.showWrappedMessageDialog(
							this, 
							EntryMessages.getString("Application.eslProblemMessage"),
							EntryMessages.getString("Application.eslProblemTitle"), 
							WrappedJOptionPane.INFORMATION_MESSAGE);
				}
			}

		}

		//initiate dual data entry procedures if appropriate
		final Record secRecord = handleDualDataEntry(record.getDataSet(), record);
		
		//Save the record then open it in the main window
        SwingWorker<Object, Object> worker = new SwingWorker<Object, Object>() {

			@Override
			protected Object doInBackground() throws DuplicateDocumentsFault, EntrySAMLException,
			NotAuthorisedFault, RepositoryOutOfDateFault, RemoteServiceFault, RepositoryInvalidIdentifierFault,
			TransformerFault, RepositoryNoConsentFault, IOException, DecryptionException
			{
				updateRecordStatus(record);
				if ( !RemoteManager.getInstance().isTestDataset() ){
					//Running in normal mode
					//Save the "empty" record so that it is not possible for
					//the identifier to be lost if the user abandons completing
					//the first document instance
					//We don't save anything if running in test/preview mode
					PersistenceManager pManager = PersistenceManager.getInstance();
					synchronized (pManager) {
						if ( pManager.getData().isAlwaysOnlineMode() ){
							RemoteManager.getInstance().commit(record);
							if ( null != secRecord ){
								RemoteManager.getInstance().commit(secRecord);
								RemoteManager.getInstance().updateSecondaryIdentifier(record, secRecord.getIdentifier().getIdentifier());
							}
						}
						else{
							pManager.saveRecord(record, true);
							if ( null != secRecord ){
								pManager.saveRecord(secRecord, true);
							}
						}
						IdentifiersList idsList = pManager.getIdentifiers();
						IdentifierData idData = idsList.get(record.getIdentifier());
						if (idData != null && (!idData.isUsed())) {
							idData.setUsed(true);
							pManager.saveIdentifiers();
						}
						
						if ( null != secRecord ){
							SecondaryIdentifierMap secondidmap = pManager.getSecondaryIdentifierMap();
							secondidmap.add(record.getIdentifier().getIdentifier(), secRecord.getIdentifier().getIdentifier());
							pManager.saveSecondaryIdentifierMap();

							idsList = pManager.getIdentifiers();
							idData = idsList.get(secRecord.getIdentifier());
							if (idData != null && (!idData.isUsed())) {
								idData.setUsed(true);
								pManager.saveIdentifiers();
							}
						}
					}
				}

				return null;
			}

			@Override
			protected void done() {
				try{
					get();
					new ResetWaitRunnable(Application.this).run();
					setSelectedRecord(record);
				}
				catch (InterruptedException e) {
					new ResetWaitRunnable(Application.this).run();
                    ExceptionsHelper.handleInterruptedException(e);
                } catch (ExecutionException e) {
                	new ResetWaitRunnable(Application.this).run();
                    Throwable cause = e.getCause();
					if ( cause instanceof DuplicateDocumentsFault ){
						ExceptionsHelper.handleDuplicateDocumentsFault(getParent(), (DuplicateDocumentsFault)cause);
					}
					if ( cause instanceof EntrySAMLException){
						ExceptionsHelper.handleEntrySAMLException(getParent(), (EntrySAMLException)cause);
					}
					if ( cause instanceof NotAuthorisedFault){
						ExceptionsHelper.handleNotAuthorisedFault(getParent(), (NotAuthorisedFault)cause);
					}
					if ( cause instanceof RepositoryOutOfDateFault){
						ExceptionsHelper.handleRepositoryOutOfDateFault(
								getParent(), (RepositoryOutOfDateFault)cause, record.getIdentifier().getIdentifier(), true);
					}
					if ( cause instanceof RemoteServiceFault){
						ExceptionsHelper.handleRemoteServiceFault(getParent(), (RemoteServiceFault)cause);
					}
					if ( cause instanceof RepositoryInvalidIdentifierFault){
						ExceptionsHelper.handleRepositoryInvalidIdentifierFault(
								getParent(), (RepositoryInvalidIdentifierFault)cause, record.getIdentifier().getIdentifier(), true);
					}
					if ( cause instanceof TransformerFault){
						ExceptionsHelper.handleTransformerFault(getParent(), (TransformerFault)cause, true);
					}
					if ( cause instanceof RepositoryNoConsentFault){
						ExceptionsHelper.handleRepositoryNoConsentFault(
								getParent(), (RepositoryNoConsentFault)cause, record.getIdentifier().getIdentifier(), true);
					}
					if ( cause instanceof IOException){
						ExceptionsHelper.handleIOException(getParent(), (IOException)cause, false);
					}
					if ( cause instanceof DecryptionException){
						ExceptionsHelper.handleFatalException(cause);
					}

				}
			}
        	
        };
        
        new WaitRunnable(this).run();
        SwingWorkerExecutor.getInstance().execute(worker);

	}

	/**
	 * Handle dual-data entry/data replication when a new record is created.
	 * <p>
	 * If the new record is for a dataset that is the primary in a data
	 * replication relationship then ask the user is they wish to link
	 * the new record to a secondary, and if so show the necessary dialogs
	 * to facilitate this.
	 * 
	 * @param ds The dataset of the primary record
	 * @param primRecord The primary record
	 * @return The secondary record; <code>null</code> if no data replication
	 * is being done.
	 */
	private Record handleDualDataEntry(DataSet ds, final Record primRecord){
		if ( null != ds.getSecondaryProjectCode() ){
			//This dataset has a secondary partner involved in dual data entry.
			//Check whether the group for the primary record has secondary groups
			String primGroup = primRecord.getIdentifier().getGroupPrefix();
			List<String> secGroups = null;
			for ( int i=0, c=ds.numGroups(); i<c; i++ ){
				if ( ds.getGroup(i).getName().equals(primGroup) ){
					secGroups = ds.getGroup(i).getSecondaryGroups();
					break;
				}
			}
			if ( null != secGroups && secGroups.size() > 0 ){
				//See if the user wants to link the record to a partner record in 
				//the secondary project
				if ( JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, 
						EntryMessages.getString("Application.addSubjectToLinkedProjMessage_p1") +ds.getSecondaryProjectCode()+
						EntryMessages.getString("Application.addSubjectToLinkedProjMessage_p2"), 
						EntryMessages.getString("Application.addSubjectToLinkedProjTitle"), 
						JOptionPane.YES_NO_OPTION) ){

					PersistenceManager pManager = PersistenceManager.getInstance();
					DataSet secDs = null;
					synchronized (pManager){
						try{
							secDs = pManager.getData().getCompleteDataSet(ds.getSecondaryProjectCode());
						}
						catch(IOException ex){
							ExceptionsHelper.handleIOException(getParent(), ex, false);
						}
					}
					if ( null == secDs ){
						WrappedJOptionPane.showWrappedMessageDialog(
								getParent(), 
								EntryMessages.getString("Application.recordLinkErrorMessage_p1")+ds.getSecondaryProjectCode()+
								EntryMessages.getString("Application.recordLinkErrorMessage_p2")+ds.getSecondaryProjectCode()+
								EntryMessages.getString("Application.recordLinkErrorMessage_p3"), 
								EntryMessages.getString("Application.recordLinkErrorTitle"), 
								WrappedJOptionPane.ERROR_MESSAGE);
						return null;
					}
					Record secRecord = null;
					try{
						final DdeIdentifierDialog ddeIdDlg = new DdeIdentifierDialog(this, secDs, secGroups);
						ddeIdDlg.setVisible(true);
						secRecord = ddeIdDlg.getSecRecord();
						processSecondaryRecord(primRecord, secRecord);
						return secRecord;
					}
					catch(IdentifierPanelException ex){
						WrappedJOptionPane.showWrappedMessageDialog(this, ex.getMessage(), ex.getTitle(), WrappedJOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}
		return null;
	}

	/**
	 * Continue processing the new secondary record in a data replication 
	 * relationship after it's identifier has been allocated.
	 * 
	 * @param primRecord The primary record
	 * @param secRecord The secondary record
	 */
	private void processSecondaryRecord(Record primRecord, Record secRecord){
		if ( null != secRecord ){
			//if there is consent to gather, or a schedule start question to answer,
			//then we need to show the consent dialog
			DataSet ds = secRecord.getDataSet();
			if ( ds.numAllConsentFormGroups() > 0 || null != ds.getScheduleStartQuestion() ){
				ConsentDialog dialog = new ConsentDialog(this, secRecord);
				dialog.setVisible(true);
				if ( !dialog.isOkClicked() ){
					return;
				}
				
				if ( ds.isEslUsed() ){
					//launch ESL dialog
					EslNewSubjectDialog eslDialog = new EslNewSubjectDialog(this, secRecord);
					eslDialog.setVisible(true);
					if ( !eslDialog.isEslSaveSuccessful() ){
						return;
					}
				}

			}

			updateRecordStatus(secRecord);
			
			//link the primary and secondary records
			primRecord.setSecondaryIdentifier(secRecord.getIdentifier().getIdentifier());
			secRecord.setPrimaryIdentifier(primRecord.getIdentifier().getIdentifier());

		}

	}

	private void setScrollBarToMinimum() {
		JScrollBar scrollBar = formScrollPane.getVerticalScrollBar();
		//We want this to run after pending events
		EventQueue.invokeLater(new ScrollBarValueSetter(scrollBar, scrollBar.getMinimum()));
	}

	@Override
	public InfiniteProgressPanel getGlassPane() {
		return (InfiniteProgressPanel) super.getGlassPane();
	}

	private void showLostConnectivityWarning(){
		
		if(!isOnline() && PersistenceManager.getInstance().getData().isAlwaysOnlineMode() && model.getCurrentDocOccurrenceInstance() != null){
			String title = EntryMessages.getString("Application.lostConnectivityTitle");
			String message = EntryMessages.getString("Application.lostConnectivityMessage");
			WrappedJOptionPane.showWrappedMessageDialog(this, message, title,
					JOptionPane.INFORMATION_MESSAGE);
		}

	}
	
	private void init() {

		setSize(800, 600);
		setLocation(WindowUtils.getPointForCentering(this));
		setTitle(getFrameTitle(null));
		setIconImage(getIcon());
		setGlassPane(RendererHelper.getInstance().createInfiniteProgressPanel());
		createStatusBar();
		model = new ApplicationModel();
		
		RendererHelper.initializeRendererHelper(model);
		
		ConnectionAvailableListener connectionAvailableListener = new ConnectionAvailableListener() {
            public void statusChanged(boolean available) {
                if (!available){               	
                	showLostConnectivityWarning();
                }
                   
            }
        };
        
        this.addConnectionAvailableListener(connectionAvailableListener);
		
		createMenuBar();
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		initFocusChangeHandlers();

		// Safe not to remove
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				exit();
			}
		});


	}

	public void startAppTimers() {
		if(appTimersStarted)
			return;

		//listen for inactivity
		//...unless running in preview mode
		if ( !RemoteManager.getInstance().isTestDataset() ){
			//listen for inactivity
			//but only if running in normal mode 
			//(do nothing if in test/preview mode)
			Long eventMask = AWTEvent.KEY_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.MOUSE_WHEEL_EVENT_MASK;
			
			LoginTimeoutListener timeoutListener = new LoginTimeoutListener(this, loginTimeout);
			Toolkit.getDefaultToolkit().addAWTEventListener(timeoutListener, eventMask);

			Timer timer = new Timer(POLL_INTERVAL, timeoutListener);
			timer.setRepeats(true);
			timer.start();

			//listen for inactivity
			//but only if running in normal mode 
			//(do nothing if in test/preview mode)
			ExitTimeoutListener exitTimeoutListener = new ExitTimeoutListener(this, EXIT_TIMEOUT);
			Toolkit.getDefaultToolkit().addAWTEventListener(exitTimeoutListener, eventMask);

			// We poll at half the timeout interval to reduce the fudge factor
			// introduced by events delivered at just under the timeout value.
			Timer exitTimer = new Timer(EXIT_TIMEOUT / 2, exitTimeoutListener);
			exitTimer.setRepeats(true);
			exitTimer.start();

			// Timer autosaves current document every minute (60000ms)
			Timer autoSaveTimer = new Timer(60000, new AutoSaveListener(this));
			autoSaveTimer.setRepeats(true);
			autoSaveTimer.start();

			appTimersStarted = true;
		}
	}

	private void initFocusChangeHandlers() {
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
		.addPropertyChangeListener(new PropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent evt) {
				moveScrollBarToShowFocusedComponent(evt);
			}
		});

	}



	/**
	 * If the permanent focus owner is outside the viewport, moves the scrollbar
	 * to bring it back into the visible area.
	 * 
	 * @param evt A <code>PropertyChangeEvent</code> fired by the current
	 * keyboard focus manager.
	 */
	private void moveScrollBarToShowFocusedComponent(PropertyChangeEvent evt) {
		String propertyName = evt.getPropertyName();
		if (!"permanentFocusOwner".equals(propertyName)) //$NON-NLS-1$
			return;

		KeyboardFocusManager focusManager = 
			KeyboardFocusManager.getCurrentKeyboardFocusManager();

		Component focusOwner = focusManager.getFocusOwner();
		if (formScrollPane == null || (!(focusOwner instanceof JComponent))) {
			return;
		}

		JComponent jFocusOwner = (JComponent) focusOwner;

		/* 
		 * JTextField overrides scrollRectToVisible(Rectangle) and does not
		 * call the superclass method. This means that the JViewport never
		 * gets the request to move the scroll bar. We create a wrapper that
		 * calls the superclass in this case. See TextFieldWrapper for more
		 * information.
		 */
		if (jFocusOwner instanceof JTextField) {
			jFocusOwner = new TextFieldWrapper((JTextField) jFocusOwner);
		}
		/* 
		 * For some reason, getVisibleRect() always returns a Rectangle with x=0 
		 * and y=0. This is not what we want, so we change those values with
		 * the correct values by getting them through getSize() 
		 */ 
		Rectangle r = jFocusOwner.getVisibleRect();
		Dimension size = jFocusOwner.getSize();
		r.height = size.height;
		r.width = size.width;
		jFocusOwner.scrollRectToVisible(r);
	}



	private Image getIcon() {
		return Icons.getInstance().getIcon("psygrid").getImage(); //$NON-NLS-1$
	}

	private void createStatusBar() {
		statusBar = new JXStatusBar();
		JPanel southPanel = new JPanel();
		southPanel.setLayout(new BorderLayout());
		southPanel.add(statusBar, BorderLayout.CENTER);
		southPanel.add(indicatorLabel, BorderLayout.EAST);
		getContentPane().add(southPanel, BorderLayout.SOUTH);
	}

	public JXStatusBar getStatusBar() {
		return statusBar;
	}

	public void exitWithoutConfirmation(final boolean saveState) {
		if ( RemoteManager.getInstance().isTestDataset() ){
			//Running in test/preview mode
			//Just dispose of the window then shut down - no need to try to
			//save anything
			dispose();
			return;
		}
		removeElements(true);
		SwingWorker<Object, Object> worker = new SwingWorker<Object, Object>() {
			@Override
			protected Object doInBackground() throws IOException {
				new WaitRunnable(Application.this).run();
				model.tearDown(saveState);
				return null;
			}

			@Override
			protected void done() {
				try {
					get();
					dispose();
					// Due to webstart, we need to do this. See bug #356.
					System.exit(0);
				} catch (InterruptedException e) {
					if (LOG.isInfoEnabled()) {
						LOG.info(e);
					}
				} catch (ExecutionException e) {
					Throwable cause = e.getCause();
					if (cause instanceof IOException) {
						if (LOG.isErrorEnabled()) {
							LOG.error(cause.getMessage(), cause);
						}
						ExceptionsHelper.handleIOException(Application.this,
								(IOException) cause, true);
					}
					else {
						ExceptionsHelper.handleFatalException(cause);
					}
				}
			}
		};

		// Don't use SwingWorkerExecutor because we dispose it in this call
		worker.execute();

	}

	public void exit() {
		Runnable r = EntryHelper.exit(this, this);
		if (r == null) {
			return;
		}
		r.run();
	}

	private boolean createFormScrollPane() {

		formPanel = new JPanel();
		formPanel.setLayout(new BorderLayout());
		formView = new FormView(this);
		JComponent formViewPanel = formView.createPanel();
		if (formViewPanel == null) {
			return false;
		}
		formPanel.add(formViewPanel, BorderLayout.CENTER);
		navigationPanel = new NavigationPanel(model.getBackAction(), model
				.getForwardAction());
		formPanel.add(navigationPanel, BorderLayout.SOUTH);

		formPanel.setOpaque(false);
		formScrollPane = createScrollPane(formPanel);

		return true;
	}

	private JScrollPane createScrollPane(JPanel panel) {
		JScrollPane scrollPane = new JScrollPane(panel);
		scrollPane.getVerticalScrollBar().setUnitIncrement(10);
		return scrollPane;
	}

	/**
	 * @param selectedDocumentOcurrence
	 * @return {@code true} if the form view was created successfully.
	 */
	private boolean init(DocumentOccurrence selectedDocumentOcurrence) {
		updateMenuStatus();

		String dataSetDisplayText = 
			selectedDocumentOcurrence.getDocument().getDataSet().getDisplayText();
		setTitle(getFrameTitle(dataSetDisplayText));

		if (!createFormScrollPane())
			return false;

		addSectionChangedListener();

		addMultipleSectionOccListener();

		addCloseDocumentListener();

		addPrintDocumentListener();

		addApplyStdCodeListener();

		addApplyImportEnabledListener();
		
		insertBeforeSecOccInstListener = new InsertBeforeSecOccInstListener(){
			public void insertBeforeSecOccInst(InsertBeforeSecOccInstEvent event) {
				Application.this.insertBeforeSecOccInst();
			}			
		};
		model.addInsertBeforeSecOccInstListener(insertBeforeSecOccInstListener);
		
		insertAfterSecOccInstListener = new InsertAfterSecOccInstListener(){
			public void insertAfterSecOccInst(InsertAfterSecOccInstEvent event) {
				Application.this.insertAfterSecOccInst();
			}			
		};
		model.addInsertAfterSecOccInstListener(insertAfterSecOccInstListener);
		
		removeSecOccInstListener = new RemoveSecOccInstListener(){
			public void removeSecOccInst(RemoveSecOccInstEvent event) {
				Application.this.removeSecOccInst();
			}			
		};
		model.addRemoveSecOccInstListener(removeSecOccInstListener);
		
		docOccurrenceCompletedListener = createDocOccurrenceCompletedListener();
		model.addDocOccurrenceCompletedListener(docOccurrenceCompletedListener);

		addValidationListener();

		DocumentOccurrence docOccurrence = model.getCurrentDocOccurrence();
		DocumentInstance docInstance = model.getCurrentDocOccurrenceInstance();

		String primaryIdentifier = null;
		if ( null != model.getCurrentRecord().getPrimaryIdentifier() && 
				null != docOccurrence.getPrimaryOccIndex() ){
			primaryIdentifier = model.getCurrentRecord().getPrimaryIdentifier();
		}
		
		informationView = new InformationView(
				docOccurrence.getCombinedDisplayText(),  //$NON-NLS-1$
				docInstance.getStatus(),
				this,
				docInstance.getLatestHistoryFormatted(),
				primaryIdentifier);


		setMainPanelRightComponent(createCenterPanel());
		return true;
	}

	private void insertBeforeSecOccInst(){
		String reason = null;
		if ( model.getDocInstanceStatus().equals(DocumentStatus.REJECTED) ||
			 model.getDocInstanceStatus().equals(DocumentStatus.CONTROLLED) ){
			AnnotationDialog dialog = new AnnotationDialog(
					this,
					EntryMessages.getString("Application.reviewAndApproveReasonTitle"),
					EntryMessages.getString("Application.reviewAndApproveReasonLabel"));
			dialog.setVisible(true);
			if ( !dialog.isOkSelected() ){
				return;
			}
			reason = dialog.getAnnotation();
		}
		model.insertBeforeSecOccInst(reason);
	}
	
	private void insertAfterSecOccInst(){
		String reason = null;
		if ( model.getDocInstanceStatus().equals(DocumentStatus.REJECTED) ||
			 model.getDocInstanceStatus().equals(DocumentStatus.CONTROLLED) ){
			AnnotationDialog dialog = new AnnotationDialog(
					this,
					EntryMessages.getString("Application.reviewAndApproveReasonTitle"),
					EntryMessages.getString("Application.reviewAndApproveReasonLabel"));
			dialog.setVisible(true);
			if ( !dialog.isOkSelected() ){
				return;
			}
			reason = dialog.getAnnotation();
		}
		model.insertAfterSecOccInst(reason);
	}
	
	private void removeSecOccInst(){
		String reason = null;
		if ( model.getDocInstanceStatus().equals(DocumentStatus.REJECTED) ||
			 model.getDocInstanceStatus().equals(DocumentStatus.CONTROLLED) ){
			AnnotationDialog dialog = new AnnotationDialog(
					this,
					EntryMessages.getString("Application.reviewAndApproveReasonTitle"),
					EntryMessages.getString("Application.reviewAndApproveReasonLabel"));
			dialog.setVisible(true);
			if ( !dialog.isOkSelected() ){
				return;
			}
			reason = dialog.getAnnotation();
		}
		model.removeCurrentSecOccInstance(reason);
	}
	
	private void setMainPanelRightComponent(JComponent rightComponent) {
		/* 
		 * The desired behaviour is for the divider to remain the same after
		 * a document is loaded in the form view. For some reason, Swing
		 * resizes the divider when the right component is set. We therefore
		 * have to restore the divider location.
		 */
		int location = mainPanel.getDividerLocation();
		mainPanel.setRightComponent(rightComponent);
		mainPanel.setDividerLocation(location);
	}

	private JPanel createCenterPanel() {
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout());
		centerPanel.add(informationView.getPane(), BorderLayout.NORTH);
		centerPanel.add(formScrollPane, BorderLayout.CENTER);
		return centerPanel;
	}

	private void buildMainPanel(JScrollPane recordScrollPane, JPanel centerPanel) {
		mainPanel  = createMainPanel(recordScrollPane, centerPanel);
		getContentPane().add(mainPanel, BorderLayout.CENTER);
	}

	private JScrollPane createRecordScrollPane() {
		recordView = new RecordView(this);
		recordView.update();

		JScrollPane recordScrollPane = createScrollPane(recordView.getPanel());
		recordScrollPane.setBorder(null);
		return recordScrollPane;
	}

	private JSplitPane createMainPanel(JComponent westComponent, JComponent centerComponent) {
		final JSplitPane panel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, westComponent, centerComponent);
		/* 
		 * 200 was chosen as a decent initial size, but this could be improved by
		 * relying on the preferred size of the record view.
		 */
		panel.setDividerLocation(250);
		panel.setOpaque(false);
		return panel;
	}

	private void addMultipleSectionOccListener() {
		multipleSectionOccListener = new MultipleSectionOccListener() {
			public void multipleSectioOccEvent(MultipleSectionOccEvent event) {
				showNewSectionDialog(event.getIndex(), event.getSectionOccurrence());
			}
		};
		model.addMultipleSectionOccListener(multipleSectionOccListener);
	}

	private void showNewSectionDialog(int index, SectionOccurrence sectionOcc) {
		String title = EntryMessages.getString("Application.newSectionTitle"); //$NON-NLS-1$
		String message = EntryMessages.getString("Application.newSectionMessage"); //$NON-NLS-1$
		int result = JOptionPane.showConfirmDialog(this, message, title, 
				JOptionPane.YES_NO_OPTION);

		if (result == JOptionPane.YES_OPTION) {
			model.addSection(index, sectionOcc, null);
		}
		model.nextSection(true);
	}

	private void addSectionChangedListener() {
		sectionChangedListener = new SectionAdapter() {
			@Override
			public void sectionChanged(SectionChangedEvent event) {
				formPanel.remove(0);
				formPanel.add(formView.createPanel(), 0);
				reportValidation(null, null, false);
				formPanel.revalidate();
				formPanel.repaint();
				setScrollBarToMinimum();
			}
		};

		model.addSectionListener(sectionChangedListener);
	}

	private void addValidationListener() {
		validationListener = new ValidationListener() {
			public void validationEvent(ValidationEvent event) {
				String message = model.getMessage(event.getValidationTypes());
				ImageIcon icon = model.getIcon(event.getValidationTypes());
				reportValidation(icon, message, event.isNextSection());
			}
		};
		model.addValidationListener(validationListener);
	}

	private void reportValidation(ImageIcon icon, String message,
			boolean nextSection) {
		informationView.setValidationMessage(icon, message);
		if (message != null && nextSection) {
			showValidationFailedDialog(message);
		}
	}

	private DocOccurrenceCompletedListener createDocOccurrenceCompletedListener() {
		return new DocOccurrenceCompletedListener() {
			public void docOccurrenceCompleted(DocOccurrenceCompletedEvent event) {
				Status status = event.getDocOccurrenceInstance().getStatus();

				DocumentStatus docStatus = null;
				if (status == null) {
					//Status can be null if a view_only document was opened automatically when CoCoA was opened
					docStatus = DocumentStatus.VIEW_ONLY;	
				}
				else {
					docStatus = DocumentStatus.valueOf(status);
				}

				Record currentRecord = model.getCurrentRecord();
				switch (docStatus) {
				case INCOMPLETE:					
					boolean saveAsIncomplete = false;
					if ( model.getCurrentDocOccurrence().getDocument().isLongRunning() ){
						//Recommend that the document instance be saved as incomplete
						if ( WrappedJOptionPane.YES_OPTION ==
							WrappedJOptionPane.showWrappedConfirmDialog(
									Application.this,
									EntryMessages.getString("Application.longRunningDocMessage"),
									EntryMessages.getString("Application.longRunningDocTitle"), 
									WrappedJOptionPane.YES_NO_OPTION,
									WrappedJOptionPane.INFORMATION_MESSAGE) ){
							saveAsIncomplete = true;
						}
					}

					if ( saveAsIncomplete ){
						model.saveIncompleteDocument(Application.this, true);
						break;
					}

					new SubmitLocalStrategy(Application.this).submit(currentRecord);
					break;
				case COMPLETE:
					new SubmitLocalStrategy(Application.this).submit(currentRecord);
					break;					
				case APPROVED:
					new SubmitApprovedStrategy(Application.this).submit(currentRecord);
					break;
				case PENDING:
					new SubmitPendingStrategy(Application.this).submit(currentRecord);
					break;
				case REJECTED:
					new SubmitRejectedStrategy(Application.this).submit(currentRecord);
					break;
				case CONTROLLED:
					new SubmitControlledStrategy(Application.this).submit(currentRecord);
					break;
				case VIEW_ONLY:
					//Document has been opened read only for viewing and so no action needs to be taken
					//other than closing the document
			        clear(false);
			        refreshRecordView();
					break;
				}
			}
		};
		
	}

	private void showValidationFailedDialog(String message) {
		String title = EntryMessages.getString("Application.validationErrorsTitle"); //$NON-NLS-1$
		JOptionPane.showMessageDialog(this, message, title,
				JOptionPane.ERROR_MESSAGE);
	}

	private void createMenuBar() {
		menuBar = new JMenuBar();

		fileMenu = new JMenu(EntryMessages.getString("Application.menuFile")); //$NON-NLS-1$
		menuBar.add(fileMenu);

		repositoryMenu = new JMenu(EntryMessages.getString("Application.menuRepository")); //$NON-NLS-1$
		menuBar.add(repositoryMenu);

		printMenu = new JMenu(EntryMessages.getString("Application.menuPrint")); //$NON-NLS-1$
		menuBar.add(printMenu);

		advancedMenu = new JMenu(EntryMessages.getString("Application.menuAdvanced")); //$NON-NLS-1$
		menuBar.add(advancedMenu);

		optionsMenu = new JMenu(EntryMessages.getString("Application.menuOptions")); //$NON-NLS-1$
		menuBar.add(optionsMenu);

		helpMenu = new JMenu(EntryMessages.getString("Application.menuHelp")); //$NON-NLS-1$
		menuBar.add(helpMenu);

		setJMenuBar(menuBar);
	}

	public void populateMenuItems() {
		populateFileMenu();
		populateRepositoryMenu();
		populatePrintMenu();
		populateHelpMenu();
		populateAdvancedMenu();
		populateOptionsMenu();
		updateMenuStatus();
	}

	private void populateAdvancedMenu() {
		advancedMenu.setMnemonic(KeyEvent.VK_A);	//Assign the keyboard shortcut 'A'

		JMenuItem syncLocalCaches = new JMenuItem(new RebuildLocalCachesAction(this));
		syncLocalCaches.setMnemonic(KeyEvent.VK_R);
		advancedMenu.add(syncLocalCaches);
		
		JMenuItem linkRecordsMenuItem = new JMenuItem(new LinkRecordsAction(this));
		linkRecordsMenuItem.setMnemonic(KeyEvent.VK_L); //Assign the keyboard shortcut 'L'

		JMenuItem syncLinkedRecordsMenuItem = new JMenuItem(new SyncLinkedRecordsAction(this));
		syncLinkedRecordsMenuItem.setMnemonic(KeyEvent.VK_S); //Assign the keyboard shortcut 'S'

		PersistenceManager pManager = PersistenceManager.getInstance();
		synchronized (pManager) {
			addLinkedStudiesListener(pManager,
					linkRecordsMenuItem, syncLinkedRecordsMenuItem);
			if (pManager.getData().isLinkedStudies()) {
				advancedMenu.addSeparator();
				advancedMenu.add(linkRecordsMenuItem);
				advancedMenu.add(syncLinkedRecordsMenuItem);
			}
		}


		
	}

	private void populatePrintMenu() {
		printMenu.setMnemonic(KeyEvent.VK_P);	//Assign the keyboard shortcut 'F'

		JMenuItem printTemplateMenuItem = new JMenuItem(new PrintTemplateDocumentAction(this));
		printTemplateMenuItem.setMnemonic(KeyEvent.VK_P);   //Assign the keyboard shortcut 'P'
		printMenu.add(printTemplateMenuItem);

		JMenuItem reportMenuItem = new JMenuItem(new GenerateReportAction(this));
		reportMenuItem.setMnemonic(KeyEvent.VK_R); //Assign the keyboard shortcut 'R'
		printMenu.add(reportMenuItem);

		JMenuItem printDocMenuItem = new JMenuItem(new PrintDocumentAction(this));
		printDocMenuItem.setMnemonic(KeyEvent.VK_D); //Assign the keyboard shortcut 'D'
		printMenu.add(printDocMenuItem);

		JMenuItem printRecordMenuItem = new JMenuItem(new PrintRecordAction(this));
		printRecordMenuItem.setMnemonic(KeyEvent.VK_E); //Assign the keyboard shortcut 'E'
		printMenu.add(printRecordMenuItem);
	}

	private void populateFileMenu() {
		fileMenu.setMnemonic(KeyEvent.VK_F);	//Assign the keyboard shortcut 'F'

		JMenuItem newChooserMenuItem = 
			new JMenuItem(new NewIdentifierChooserAction(this));
		newChooserMenuItem.setMnemonic(KeyEvent.VK_N);	//Assign the keyboard shortcut 'N'
		fileMenu.add(newChooserMenuItem);

		JMenuItem loadDocumentsMenuItem = new JMenuItem(new LoadDocumentAction(this));
		loadDocumentsMenuItem.setMnemonic(KeyEvent.VK_L);
		fileMenu.add(loadDocumentsMenuItem);

		fileMenu.addSeparator();
		JMenuItem exitMenuItem = new JMenuItem(
				new ExitAction(this));
		exitMenuItem.setMnemonic(KeyEvent.VK_E);	//Assign the keyboard shortcut 'E'
		fileMenu.add(exitMenuItem);
	}

	private void populateRepositoryMenu() {
		repositoryMenu.setMnemonic(KeyEvent.VK_R);	//Assign the keyboard shortcut 'R'

		JMenuItem remoteUpdateMenuItem = 
			new JMenuItem(new RemoteUpdateAction(this));
		remoteUpdateMenuItem.setMnemonic(KeyEvent.VK_U); //Assign the keyboard shortcut 'U'
		repositoryMenu.add(remoteUpdateMenuItem);

		synchronized (PersistenceManager.getInstance()) {
			PersistenceData pData = PersistenceManager.getInstance().getData();
			final JMenuItem commitMenuItem = new JMenuItem(new RemoteCommitAction(this));
			commitMenuItem.setMnemonic(KeyEvent.VK_C); //Assign the keyboard shortcut 'C'
			if (!pData.isAlwaysOnlineMode())
				repositoryMenu.add(commitMenuItem);
			pData.addPropertyChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					if (evt.getPropertyName().equals(PersistenceData.ALWAYS_ONLINE_MODE_PROPERTY)) {
						if (evt.getNewValue() == Boolean.FALSE) {
							repositoryMenu.add(commitMenuItem);
							return;
						}
						repositoryMenu.remove(commitMenuItem);
					}
				}
			});
		}		
	}

	private void populateHelpMenu() {

		helpMenu.setMnemonic(KeyEvent.VK_H); //Assign the keyboard shortcut 'H'

		JMenuItem helpMenuItem = new JMenuItem("Help...");
		helpMenuItem.setMnemonic(KeyEvent.VK_H); //Assign the keyboard shortcut 'H'
		helpMenuItem.addActionListener(
				new CSH.DisplayHelpFromSource(HelpHelper.getInstance().getHelpBroker()) );
		helpMenu.add(helpMenuItem);

		emailLogsMenuItem = 
			new JMenuItem(new SendLogsByEmailAction(this));
		emailLogsMenuItem.setMnemonic(KeyEvent.VK_E); //Assign the keyboard shortcut 'E'
		helpMenu.add(emailLogsMenuItem);
		helpMenu.addSeparator();
		JMenuItem aboutMenuItem = 
			new JMenuItem(new HelpAboutAction(this));
		aboutMenuItem.setMnemonic(KeyEvent.VK_A); //Assign the keyboard shortcut 'A'
		helpMenu.add(aboutMenuItem);

	}

	private void populateOptionsMenu() {
		optionsMenu.setMnemonic(KeyEvent.VK_O); //Assign the keyboard shortcut 'O'

		JMenuItem changePasswordMenuItem = 
			new JMenuItem(new ChangePasswordAction(this));
		changePasswordMenuItem.setMnemonic(KeyEvent.VK_C); //Assign the keyboard shortcut 'C'
		optionsMenu.add(changePasswordMenuItem);
		JMenuItem settingsMenuItem = 
			new JMenuItem(new SettingsAction(this));
		settingsMenuItem.setMnemonic(KeyEvent.VK_S); //Assign the keyboard shortcut 'S'
		optionsMenu.add(settingsMenuItem);
	}

	private void addLinkedStudiesListener(
			PersistenceManager pManager, final JMenuItem linkRecordsMenuItem, final JMenuItem syncLinkedRecordsMenuItem) {
		pManager.getData().addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(PersistenceData.LINKED_STUDIES_PROPERTY)) {
					if (evt.getNewValue() == Boolean.TRUE) {
						advancedMenu.addSeparator();
						advancedMenu.add(linkRecordsMenuItem);
						advancedMenu.add(syncLinkedRecordsMenuItem);
					}
					else{
						advancedMenu.remove(linkRecordsMenuItem);
						advancedMenu.remove(syncLinkedRecordsMenuItem);
						//ideally the separator would be removed here too
						//but the API does not seem to allow it!
					}
				}
			}
		});
	}

	public ApplicationModel getModel() {
		return model;
	}

	InformationView getInformationPanel() {
		return informationView;
	}

	private static final class ScrollBarValueSetter implements Runnable  {

		private final int value;
		private final JScrollBar scrollBar;
		ScrollBarValueSetter(JScrollBar scrollBar, int value){
			this.value = value;
			this.scrollBar = scrollBar;
		}

		public final void run() {
			scrollBar.setValue(value);
		}
	}

	public final Boolean isLoadPendingDocumentsMItemEnabled() {
		return loadPendingDocumentsMItemEnabled;
	}

	public final void setLoadPendingDocumentsMItemEnabled(
			Boolean loadPendingDocumentsMItemEnabled) {
		this.loadPendingDocumentsMItemEnabled = loadPendingDocumentsMItemEnabled;
	}

	public final void renderReport(final Report report) {
		SwingWorker<File, Object> worker = new SwingWorker<File, Object>(){
			@Override
			protected File doInBackground() throws IOException, RendererException {
				new WaitRunnable(Application.this).run();
				BufferedOutputStream bos = null;
				try{
					PdfRenderer renderer = new PdfRenderer();
					File file = File.createTempFile(getRandomString(10), ".pdf"); //$NON-NLS-1$ //$NON-NLS-2$
					bos = new BufferedOutputStream(new FileOutputStream(file));
					renderer.render(report, bos);
					bos.flush();
					return file;
				}
				catch(IOException ioe){
					throw ioe;
				}
				catch(RendererException re){
					throw re;
				}
				catch(RuntimeException re){
					throw re;
				}
				finally{
					closeStream(bos);
				}
			}
			@Override
			protected void done() {
				try {
					File file = get();
					Desktop.open(file);
				} catch (InterruptedException e) {
					new ResetWaitRunnable(Application.this).run();
					ExceptionsHelper.handleInterruptedException(e);
				} catch (DesktopException e) {
					new ResetWaitRunnable(Application.this).run();
					ExceptionsHelper.handleCannotOpenPdfException(getParent(), e);
				} catch (ExecutionException e) {
					new ResetWaitRunnable(Application.this).run();
					Throwable cause = e.getCause();
					if (cause instanceof IOException) {
						ExceptionsHelper.handleIOException(getParent(), 
								(IOException) cause, false);
					}
					else if (cause instanceof RendererException) {
						handleRenderException(cause);
						// Prevent a bug in the PdfRenderer from causing the application
						// to have to shutdown, especially given that it doesn't affect
						// the state of the application
					}
					else if (cause instanceof RuntimeException) {
						handleRenderException(cause);
					}
					else {
						ExceptionsHelper.handleFatalException(cause);
					}
				}
				finally{
					new ResetWaitRunnable(Application.this).run();
				}
			}
		};
		SwingWorkerExecutor.getInstance().execute(worker);
	}

	private void handleRenderException(Throwable t) {
		String title = EntryMessages.getString("Application.errorReportTitle"); //$NON-NLS-1$
		String message = EntryMessages.getString("Application.errorReportMessage"); //$NON-NLS-1$
		ExceptionsHelper.handleException(this, title, t, message, false);
	}

	private void closeStream(BufferedOutputStream bos) {
		if (bos == null) {
			return;
		}
		try {
			bos.close();
		} catch (IOException e) {
			LOG.warn("Failed to close stream.", e); //$NON-NLS-1$
		}
	}

	private void addPrintDocumentListener(){
		printDocumentListener = new PrintDocumentListener() {
			public void printDocument(PrintDocumentEvent event) {
				printSingleDocument(model.getCurrentDocOccurrenceInstance());
			}
		};
		model.addPrintDocumentListener(printDocumentListener);
	}

	public void printSingleDocument(final DocumentInstance instance){
		SwingWorker<File, Object> worker = new SwingWorker<File, Object>(){
			@Override
			protected File doInBackground() throws IOException, RendererException {
				new WaitRunnable(Application.this).run();
				BufferedOutputStream bos = null;
				try{
					PrintPdfRenderer renderer = new PrintPdfRenderer();
					File file = File.createTempFile(getRandomString(10), ".pdf"); //$NON-NLS-1$ //$NON-NLS-2$
					bos = new BufferedOutputStream(new FileOutputStream(file));
					renderer.renderSingleDocumentInstance(instance, bos);
					bos.flush();
					return file;
				}
				catch(IOException ioe){
					throw ioe;
				}
				catch(RendererException re){
					throw re;
				}
				catch(RuntimeException re){
					throw re;
				}
				finally{
					closeStream(bos);
				}
			}
			@Override
			protected void done() {
				try {
					File file = get();
					Desktop.open(file);
				} catch (InterruptedException e) {
					new ResetWaitRunnable(Application.this).run();
					ExceptionsHelper.handleInterruptedException(e);
				} catch (DesktopException e) {
					new ResetWaitRunnable(Application.this).run();
					ExceptionsHelper.handleCannotOpenPdfException(getParent(), e);
				} catch (ExecutionException e) {
					new ResetWaitRunnable(Application.this).run();
					Throwable cause = e.getCause();
					if (cause instanceof IOException) {
						ExceptionsHelper.handleIOException(getParent(), 
								(IOException) cause, false);
					}
					else if (cause instanceof RendererException) {
						handleRenderException(cause);
						// Prevent a bug in the PdfRenderer from causing the application
						// to have to shutdown, especially given that it doesn't affect
						// the state of the application
					}
					else if (cause instanceof RuntimeException) {
						handleRenderException(cause);
					}
					else {
						ExceptionsHelper.handleFatalException(cause);
					}
				}
				finally{
					new ResetWaitRunnable(Application.this).run();
				}
			}
		};
		SwingWorkerExecutor.getInstance().execute(worker);
	}

	public void printRecord(final Record record){
		if (record.numDocumentInstances() == 0) {
			WrappedJOptionPane.showMessageDialog(getParent(), EntryMessages.getString("Application.recordHasNoDocsToPrintMessage"));
			return;
		}
		SwingWorker<File, Object> worker = new SwingWorker<File, Object>(){
			@Override
			protected File doInBackground() throws IOException, RendererException {
				new WaitRunnable(Application.this).run();
				BufferedOutputStream bos = null;
				try{
					PrintPdfRenderer renderer = new PrintPdfRenderer();
					File file = File.createTempFile(getRandomString(10), ".pdf"); //$NON-NLS-1$ //$NON-NLS-2$
					bos = new BufferedOutputStream(new FileOutputStream(file));
					renderer.renderRecord(record, bos);
					bos.flush();
					return file;
				}
				catch(IOException ioe){
					throw ioe;
				}
				catch(RendererException re){
					throw re;
				}
				catch(RuntimeException re){
					throw re;
				}
				finally{
					closeStream(bos);
				}
			}
			@Override
			protected void done() {
				try {
					File file = get();
					Desktop.open(file);
				} catch (InterruptedException e) {
					new ResetWaitRunnable(Application.this).run();
					ExceptionsHelper.handleInterruptedException(e);
				} catch (DesktopException e) {
					new ResetWaitRunnable(Application.this).run();
					ExceptionsHelper.handleCannotOpenPdfException(getParent(), e);
				} catch (ExecutionException e) {
					new ResetWaitRunnable(Application.this).run();
					Throwable cause = e.getCause();
					if (cause instanceof IOException) {
						ExceptionsHelper.handleIOException(getParent(), 
								(IOException) cause, false);
					}
					else if (cause instanceof RendererException) {
						handleRenderException(cause);
						// Prevent a bug in the PdfRenderer from causing the application
						// to have to shutdown, especially given that it doesn't affect
						// the state of the application
					}
					else if (cause instanceof RuntimeException) {
						handleRenderException(cause);
					}
					else {
						ExceptionsHelper.handleFatalException(cause);
					}
				}
				finally{
					new ResetWaitRunnable(Application.this).run();
				}
			}
		};
		SwingWorkerExecutor.getInstance().execute(worker);
	}

	public void printTemplateDocument(final DocumentOccurrence occurrence){
		SwingWorker<File, Object> worker = new SwingWorker<File, Object>(){
			@Override
			protected File doInBackground() throws IOException, RendererException {
				new WaitRunnable(Application.this).run();
				BufferedOutputStream bos = null;
				try{
					PrintPdfRenderer renderer = new PrintPdfRenderer();
					File file = File.createTempFile(getRandomString(10), ".pdf"); //$NON-NLS-1$ //$NON-NLS-2$
					bos = new BufferedOutputStream(new FileOutputStream(file));
					renderer.renderBlankDocumentOccurrence(occurrence, bos);
					bos.flush();
					return file;
				}
				catch(IOException ioe){
					throw ioe;
				}
				catch(RendererException re){
					throw re;
				}
				catch(RuntimeException re){
					throw re;
				}
				finally{
					closeStream(bos);
				}
			}
			@Override
			protected void done() {
				try {
					File file = get();
					Desktop.open(file);
				} catch (InterruptedException e) {
					new ResetWaitRunnable(Application.this).run();
					ExceptionsHelper.handleInterruptedException(e);
				} catch (DesktopException e) {
					new ResetWaitRunnable(Application.this).run();
					ExceptionsHelper.handleCannotOpenPdfException(getParent(), e);
				} catch (ExecutionException e) {
					new ResetWaitRunnable(Application.this).run();
					Throwable cause = e.getCause();
					if (cause instanceof IOException) {
						ExceptionsHelper.handleIOException(getParent(), 
								(IOException) cause, false);
					}
					else if (cause instanceof RendererException) {
						handleRenderException(cause);
						// Prevent a bug in the PdfRenderer from causing the application
						// to have to shutdown, especially given that it doesn't affect
						// the state of the application
					}
					else if (cause instanceof RuntimeException) {
						handleRenderException(cause);
					}
					else {
						ExceptionsHelper.handleFatalException(cause);
					}
				}
				finally{
					new ResetWaitRunnable(Application.this).run();
				}
			}
		};
		SwingWorkerExecutor.getInstance().execute(worker);
	}

	private void addApplyImportEnabledListener(){
		applyImportEnabledListener = new ApplyImportEnabledListener() {
			public void doImport(ApplyImportEvent event) {
				applyImportToDocInstance(
						model.getCurrentDocOccurrenceInstance(),
						model.getCurrentEntries(),
						model.getCurrentSectionOccPresModel());
			}
		};
		model.addApplyImportEnabledListener(applyImportEnabledListener);
	}

	private void addApplyStdCodeListener(){
		applyStdCodeListener = new ApplyStdCodeListener() {
			public void applyStdCode(ApplyStdCodeEvent event) {
				applyStdCodeToSection(
						model.getCurrentDocOccurrenceInstance(), 
						model.getCurrentEntries(), 
						model.getCurrentSectionOccPresModel());
			}
		};
		model.addApplyStdCodeListener(applyStdCodeListener);
	}


	public void applyImportToDocInstance(DocumentInstance instance, List<Entry> entries, SectionPresModel secPresModel){

		ImportFileChooser importFileChooser = new ImportFileChooser(this);
		try{
			importFileChooser.queryForImportFile();
		}catch (NoFileSelectedException e){
			//The user has cancelled out of specifying an import file. Nothing else needs to be done.
			return;
		}

		org.psygrid.collection.entry.externaldocparser.SelectedFileInfo fileInfo = importFileChooser.getSelectedFileInfo();
		String stringVersionOfFile = null;

		if(fileInfo == SelectedFileInfo.local){
			stringVersionOfFile = importFileChooser.getSelectedFile().getAbsolutePath();
		}
		else if(fileInfo == SelectedFileInfo.remote){
			stringVersionOfFile = importFileChooser.getSelectedURL().toString();
		}

		ImportFileTypeValidator validator = new ImportFileTypeValidator();
		RecognizedFileType fileType = RecognizedFileType.unknown;

		try{
			fileType = validator.validateFile(stringVersionOfFile);
		}catch (FileTypeNotRecognizedException e){
			String message = new String(EntryMessages.getString("Application.unrecognizedImportFileTypeMessage"));
			JOptionPane.showMessageDialog(null, message);
			return;
		}

		ExternalDocumentParserFactory factory = new ExternalDocumentParserFactory(fileType, fileInfo, stringVersionOfFile, this.model.getCurrentDocOccurrenceInstance());
		AbstractExternalDocumentParser theParser;
		try {
			theParser = factory.getParser();
		} catch (MalformedURLException e) { //This should never happen because the URL has already been validated. This handler is present for completeness only.
			String message = new String(EntryMessages.getString("Application.malformedURLMessage"));
			JOptionPane.showMessageDialog(null, message);
			return;
		}

		try{
			theParser.verifyDocumentFromSource();
			theParser.initializeDocumentMapping();
			ParseResults results = theParser.Parse();
			if(results.getNumImportExceptions() > 0){
				String message = new String(EntryMessages.getString("Application.importEntryErrorMessage"));
				JOptionPane.showMessageDialog(null, message);
			}

			model.fireSectionChangedEvent(new SectionChangedEvent(model, secPresModel, secPresModel));
		}catch (ParserException e){
			String message = new String(e.getMessage() + EntryMessages.getString("Application.parseAbortedMessage"));
			JOptionPane.showMessageDialog(null, message);
		}
	}

	public void applyStdCodeToSection(DocumentInstance instance, List<Entry> entries, SectionPresModel secPresModel){
		ChooseStdCodeDialog dlg = new ChooseStdCodeDialog(
				this,
				EntryMessages.getString("Application.selectStandardCodesMessage"),
				model.getStandardCodes(),
				true);
		dlg.setVisible(true);
		StandardCode stdCode = dlg.getStdCode();
		if (null == stdCode) {
			return;
		}
		boolean allSections = dlg.isAllSections();

		if(allSections && model.currentDocInstanceHasMultipleSections()){ 

			String confirmMessage = EntryMessages.getString("Application.confirmApplyStandardCodesMessage");
			String confirmTitle = EntryMessages.getString("Application.confirmApplyStandardCodesTitle");
			int result = JOptionPane.showConfirmDialog(this, confirmMessage,
					confirmTitle, JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.NO_OPTION) {
				return;
			}
		}

		model.applyStdCodeToSection(stdCode, allSections);
	}

	private void addCloseDocumentListener(){
		closeDocumentListener = new CloseDocumentListener() {
			public void closeDocument(CloseDocumentEvent event) {
				closeCurrentDocument();
			}
		};
		model.addCloseDocumentListener(closeDocumentListener);
	}

	private void closeCurrentDocument(){
		if (getModel().getCurrentDocOccurrence() != null && 
			getModel().getDocInstanceStatus() != DocumentStatus.VIEW_ONLY ) {
			if (!EntryHelper.showDocumentWillBeLostDialog(this)) {
				return;
			}
		}
		clear(false);
	}

	/**
	 * Method to check for connection; if different to previous state, 
	 * update the indicator.
	 */
	public void updateStatus()
	{
		try
		{
			boolean oldOnline = online;
			online = RemoteManager.getInstance().isConnectionAvailable(false);
			//avoid needless refreshing, only update if the status has changed
			if ( firstStatusUpdate ){
				updateIndicatorLabel(online);
				fireConnectionAvailableEvent(online);
				firstStatusUpdate = false;
			}
			else if ( oldOnline != online )
			{
				updateIndicatorLabel(online);
				fireConnectionAvailableEvent(online);
			}
		} 
		catch (Exception ex)
		{
			//assuming exception thrown here means no connection
			online = false;
			updateIndicatorLabel(online);
			fireConnectionAvailableEvent(online);
		}
	}

	public boolean isOnline() {
		return online;
	}

	/**
	 * Method to update network connectivity icon in the status bar
	 * @param connected Whether connection is available or not
	 */
	public void updateIndicatorLabel(boolean connected)
	{

		if (connected) {
			if ( LOG.isInfoEnabled() ){
				LOG.info("Application has gone online.");
			}
			indicatorLabel.setIcon(Icons.getInstance().getIcon("indicator-bright-green"));
			indicatorLabel.setToolTipText(EntryMessages.getString("Application.onlineToolTipText"));
		} else {
			if ( LOG.isInfoEnabled() ){
				LOG.info("Application has gone offline.");
			}
			indicatorLabel.setIcon(Icons.getInstance().getIcon("indicator-red"));
			indicatorLabel.setToolTipText(EntryMessages.getString("Application.offlineToolTipText"));
		}
		indicatorLabel.revalidate();
		indicatorLabel.repaint();
	}

	/**
	 * Invokes thread to update status bar indicator.
	 */
	public void updateStatusBar()
	{
		UpdateStatus updateStatus = new UpdateStatus();
		updateStatus.start();
	}


	/**
	 * Thread to update the status indicator at bottom right of status bar.
	 * Polls for connection every 10 seconds
	 */
	private class UpdateStatus extends Thread
	{
		@Override
		public void run()
		{
			try
			{
				for (;;)
				{
					Thread.sleep(10000);
					updateStatus();
				}
			} catch (Exception ex)
			{
				//on exception do nothing
				if (LOG.isErrorEnabled()) {
					LOG.error(ex);
				}
			}
		}
	}
	@Override
	public void setUserName(String uid){
		setTitle(frameTitle=getFrameTitle(null)+" - "+uid);
	}

	public Action getSaveIncompleteDocumentAction() {
		return saveIncompleteDocumentAction;
	}
	
	/**
	 * For a new record see if its status needs to be updated from a
	 * REFERRED one to an ACTIVE one if there is some consent.
	 * 
	 * @param record The record.
	 */
	private void updateRecordStatus(Record record){
		Status currentStatus = record.getStatus();
		if ( GenericState.REFERRED.equals(currentStatus.getGenericState()) &&
				!record.getAllConsents().isEmpty() ){
			//record is currently in Referred state and has consent - need to move to
			//an active state i.e. Consented
			Status newStatus = null;
			for (int i=0, c=currentStatus.numStatusTransitions(); i<c; i++) {
				Status s = currentStatus.getStatusTransition(i);
				if ( GenericState.ACTIVE.equals(s.getGenericState()) ) {
					newStatus = s;
					break;
				}
			}

			if ( null != newStatus ){
				((Record)record).changeStatus(newStatus, true);
			}
		}
	}
	
	/**
	 * Generate a random string with the specified number of characters.
	 * <p>
	 * Only lower case a-z characters will be used to make up the string.
	 * 
	 * @param nChars Number of characters
	 * @return Random string
	 */
	private String getRandomString(int nChars){
		int start = 'a';
		int end = 'z';
		int range = end-start;
		
		StringBuilder builder = new StringBuilder();
		int count = 0;
		while ( count < nChars ){
			char c = (char)(start+rng.nextInt(range));
			builder.append(c);
			count++;
		}
		return builder.toString();
	}
}

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

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.event.EventListenerList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingworker.SwingWorker;
import org.psygrid.collection.entry.builder.BuilderHandler;
import org.psygrid.collection.entry.event.ApplyImportEnabledListener;
import org.psygrid.collection.entry.event.ApplyImportEvent;
import org.psygrid.collection.entry.event.ApplyStdCodeEvent;
import org.psygrid.collection.entry.event.ApplyStdCodeListener;
import org.psygrid.collection.entry.event.CloseDocumentEvent;
import org.psygrid.collection.entry.event.CloseDocumentListener;
import org.psygrid.collection.entry.event.DocOccurrenceCompletedEvent;
import org.psygrid.collection.entry.event.DocOccurrenceCompletedListener;
import org.psygrid.collection.entry.event.InsertAfterSecOccInstEvent;
import org.psygrid.collection.entry.event.InsertAfterSecOccInstListener;
import org.psygrid.collection.entry.event.InsertBeforeSecOccInstEvent;
import org.psygrid.collection.entry.event.InsertBeforeSecOccInstListener;
import org.psygrid.collection.entry.event.MultipleSectionOccEvent;
import org.psygrid.collection.entry.event.MultipleSectionOccListener;
import org.psygrid.collection.entry.event.PrintDocumentEvent;
import org.psygrid.collection.entry.event.PrintDocumentListener;
import org.psygrid.collection.entry.event.RemoveSecOccInstEvent;
import org.psygrid.collection.entry.event.RemoveSecOccInstListener;
import org.psygrid.collection.entry.event.SectionChangedEvent;
import org.psygrid.collection.entry.event.SectionEvent;
import org.psygrid.collection.entry.event.SectionListener;
import org.psygrid.collection.entry.event.StandardCodesEvent;
import org.psygrid.collection.entry.event.StandardCodesListener;
import org.psygrid.collection.entry.event.ValidationEvent;
import org.psygrid.collection.entry.event.ValidationListener;
import org.psygrid.collection.entry.model.EntryPresModel;
import org.psygrid.collection.entry.model.ResponsePresModel;
import org.psygrid.collection.entry.model.SectionPresModel;
import org.psygrid.collection.entry.model.StandardPresModel;
import org.psygrid.collection.entry.persistence.ConsentMap2;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.collection.entry.persistence.SaveIncompleteRecordWorker;
import org.psygrid.collection.entry.persistence.UnfinishedDocInstance;
import org.psygrid.collection.entry.remote.CommitDocumentWorker;
import org.psygrid.collection.entry.remote.RemoteManager;
import org.psygrid.collection.entry.renderer.PresModelRenderer;
import org.psygrid.collection.entry.renderer.Renderer;
import org.psygrid.collection.entry.renderer.RendererHandler;
import org.psygrid.collection.entry.renderer.RendererHelper;
import org.psygrid.collection.entry.util.ResetWaitRunnable;
import org.psygrid.collection.entry.util.StandardCodesGetter;
import org.psygrid.collection.entry.util.WaitRunnable;
import org.psygrid.collection.entry.validation.TransformedMessage;
import org.psygrid.collection.entry.validation.ValidationType;
import org.psygrid.common.ui.WrappedJOptionPane;
import org.psygrid.data.model.hibernate.*;

import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.view.ValidationResultViewFactory;


public class ApplicationModel {

	private static final Log LOG = LogFactory.getLog(ApplicationModel.class);

	private Action backAction;

	private ForwardAction forwardAction;

	private Action closeAction;

	private Action printAction;

	private Action applyStdCodeAction;

	private Action applyImportAction;

	/**
	 * The index of the section occurrence presentation model without taking 
	 * into account the sections. In other words, if all section occurrences 
	 * were flattened into one list, this index would identify a position in 
	 * that list.
	 */
	private int sectionPresModelIndex;

	private BuilderHandler builderHandler;

	private List<StandardCode> standardCodes;

	private Record currentRecord;

	private DocumentOccurrence currentDocOccurrence;

	private DocumentInstance currentDocOccurrenceInstance;

	private final List<SectionPresModel> sectionPresModels = new ArrayList<SectionPresModel>();


	/**
	 * Contains sections that have been rendered at least once and have
	 * validation errors. The current section is never included. Since
	 * previous sections cannot have validation errors, this Set
	 * can only include sections after the current section.
	 */
	private final Set<SectionPresModel> invalidSectionPresModels = new HashSet<SectionPresModel>();

	private final Map<SectionPresModel, List<Entry>> entriesMap = new HashMap<SectionPresModel, List<Entry>>();

	private EventListenerList listenerList = new EventListenerList();

	private StandardCodesGetter standardCodesGetter;

	private DocumentStatus docInstanceStatus;

	private boolean canRandomize;


	private List<Entry> editedEntries = new ArrayList<Entry>();
	
	public List<Entry> getEditedEntries() {
		return editedEntries;
	}

	/**
	 * Actions for manipulating section occurrence instances
	 */
	private Action insertBeforeSecOccInstAction;
	
	private Action insertAfterSecOccInstAction;
	
	private Action removeSecOccInstAction;
	
	/**
	 * Detects whether there are multiple sections in the current document occurrence instance.
	 * This includes the case where there is only one section
	 * with more than one section occurrence instance.
	 * @return boolean
	 */
	public boolean currentDocInstanceHasMultipleSections(){
		return sectionPresModels != null && sectionPresModels.size() > 1;
	}

	private final PropertyChangeListener validationListener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			if (!hasNextSection())
				setForwardActionOnPending(docInstanceStatus);

			EnumSet<ValidationType> currentSectionValidationTypes = getCurrentSectionValidationTypes();
			fireValidationEvent(new ValidationEvent(ApplicationModel.this,
					currentSectionValidationTypes, false));
		}
	};

	public ApplicationModel() {
		createNavigationActions();
		createSecOccInstActions();
		initStandardCodes();
		RendererHelper.initializeRendererHelper(this);
	}

	private void initStandardCodes() {
		RemoteManager.getInstance().addStandardCodesListener(
				new StandardCodesListener() {
					public void standardCodesUpdated(StandardCodesEvent event) {
						standardCodes = event.getStandardCodes();
					}
				});
	}

	public void setSelectedDocOccurrenceInstance(
			DocumentInstance docOccurrenceInstance, DocumentStatus docStatus) {
		checkDocOccurrenceInstance(docOccurrenceInstance);
		if (LOG.isInfoEnabled()) {
			DocumentOccurrence docOcc = docOccurrenceInstance.getOccurrence();
			logDocOccurrenceSelected(docOcc);
		}
		if (standardCodes == null) {
			standardCodes = createStandardCodes();
		}
		this.docInstanceStatus  = docStatus;
		this.currentDocOccurrence = docOccurrenceInstance.getOccurrence();
		this.currentDocOccurrenceInstance = docOccurrenceInstance;
		currentDocOccurrenceInstance.recordCurrentState();
		initCurrentSection();
		createAllHandlers();
	}

	private void logDocOccurrenceSelected(DocumentOccurrence docOcc) {
		LOG.info("Document instance was selected [name, id]: " +  //$NON-NLS-1$
				docOcc.getDocument().getName() + " - " + docOcc.getName() + ", " + //$NON-NLS-1$ //$NON-NLS-2$
				docOcc.getId());
	}

	void setStandardCodesGetter(StandardCodesGetter standardCodesGetter) {
		this.standardCodesGetter = standardCodesGetter;
	}

	StandardCodesGetter getStandardCodesGetter() {
		return standardCodesGetter;
	}

	public DocumentStatus getDocInstanceStatus() {
		return docInstanceStatus;
	}

	public void setCurrentRecord(Record record) {
		this.currentRecord = record;
	}

	public void setSelectedDocOccurrence(DocumentOccurrence docOccurrence) {
		if (LOG.isInfoEnabled()) {
			logDocOccurrenceSelected(docOccurrence);
		}
		if (standardCodes == null) {
			standardCodes = createStandardCodes();
		}
		this.currentDocOccurrence = docOccurrence;
		this.currentDocOccurrenceInstance = 
			docOccurrence.getDocument().generateInstance(docOccurrence);
		if (currentDocOccurrenceInstance.getStatus() != null) {
			docInstanceStatus = DocumentStatus.valueOf(currentDocOccurrenceInstance.getStatus());
		}
		initCurrentSection();
		createHandlers();
	}

	private void initCurrentSection() {
		sectionPresModelIndex = 0;
		initSections();
		setActionsStatus();
	}

	public void checkCurrentDocOccurrenceInstance() {
		checkDocOccurrenceInstance(getCurrentDocOccurrenceInstance());
	}

	private void checkDocOccurrenceInstance(DocumentInstance instance) {
		Record record = instance.getRecord();
		if (record == null)
			throw new IllegalStateException("The document instance must have a " + //$NON-NLS-1$
			"record associated with it."); //$NON-NLS-1$
		if (record.getIdentifier() == null)
			throw new IllegalStateException("The record must have an identifier " + //$NON-NLS-1$
			"associated with it."); //$NON-NLS-1$
		if (currentRecord != record)
			throw new IllegalStateException("currentRecord should be the same as record"); //$NON-NLS-1$
	}

	public boolean checkDocInstanceConsent(DocumentInstance instance) {
		ConsentMap2 consentMap = PersistenceManager.getInstance().getConsentMap();
		String identifier = instance.getRecord().getIdentifier().getIdentifier();

		boolean docConsent = true;
		if ( consentMap.consentExists(identifier) ){
			//Consent info exists in the consent map for this identifier
			Document d = instance.getOccurrence().getDocument();
			for (int i=0; i<d.numConsentFormGroups(); i++ ){
				ConsentFormGroup cfg = d.getConsentFormGroup(i);
				boolean grpConsent = false;
				for (int j=0; j<cfg.numConsentForms(); j++){
					PrimaryConsentForm pcf = cfg.getConsentForm(j);
					boolean pcfConsent = consentMap.checkConsent(identifier, pcf);
					if ( pcfConsent ){
						//check associated consent forms
						for (int k=0; k<pcf.numAssociatedConsentForms(); k++){
							AssociatedConsentForm acf = pcf.getAssociatedConsentForm(k);
							pcfConsent &=  consentMap.checkConsent(identifier, acf);
						}
					}
					//consent must be obtained for one of the primary consent forms
					//in the consent form group
					grpConsent |= pcfConsent;
				}
				//consent must be obtained for all of the consent form groups associated
				//with the document
				docConsent &= grpConsent;
			}
		}
		else{
			//No consent in the map for this identifier, so we check in the record instead
			docConsent = instance.getRecord().checkConsent(instance);
		}

		return docConsent;

	}

	private RendererHandler createRendererHandler(
			SectionPresModel sectionPresModel) {

		int entriesCount = entriesMap.get(sectionPresModel).size();

		// Some extra space for the renderers inside composite entries and to 
		// avoid reaching the size where a rehash is required
		int mapCapacity = entriesCount + ((int) (entriesCount * 0.35));
		return new RendererHandler(sectionPresModel, mapCapacity);
	}

	private void createHandlers() {
		RendererHandler rendererHandler = createRendererHandler(
				getCurrentSectionOccPresModel());
		builderHandler = new BuilderHandler(getCurrentDocOccurrenceInstance(), 
				standardCodes, 
				new ArrayList<SectionPresModel>(sectionPresModels));
		builderHandler.addRendererHandler(rendererHandler);
	}

	private void createAllHandlers() {
		builderHandler = new BuilderHandler(getCurrentDocOccurrenceInstance(), 
				standardCodes, 
				new ArrayList<SectionPresModel>(sectionPresModels));
		for ( SectionPresModel spm: sectionPresModels){
			RendererHandler rendererHandler = createRendererHandler(spm);
			builderHandler.addRendererHandler(rendererHandler);
		}
		builderHandler.rewind();
	}

	public SectionPresModel getCurrentSectionOccPresModel() {
		return sectionPresModels.get(sectionPresModelIndex);
	}

	List<StandardCode> getStandardCodes(){
		return standardCodes;
	}

	private List<StandardCode> createStandardCodes() {
		if (standardCodesGetter != null) {
			return standardCodesGetter.getStandardCodes();
		}
		List<StandardCode> codes;
		try {
			codes = PersistenceManager.getInstance().loadStandardCodes();
		} catch (IOException e) {
			if (LOG.isErrorEnabled()) {
				LOG.error("Error reading standard codes from disk.", e); //$NON-NLS-1$
			}
			codes = new ArrayList<StandardCode>(0);
			//caused by calling from the dataset designer
		} catch (NullPointerException ex) {
			codes = new ArrayList<StandardCode>(0);
		}

		return codes;
	}

	public List<SectionPresModel> getDocSectionOccPresModels()    {
		return Collections.unmodifiableList(sectionPresModels);
	}

	private List<Entry> getSectionEntries(Section section)    {
		List<Entry> sectionEntries = new ArrayList<Entry>();
		Document doc = getCurrentDocument();
		for (int i = 0, c = doc.numEntries(); i < c; ++i) {
			Entry entry = doc.getEntry(i);
			if (section.equals(entry.getSection())) {
				sectionEntries.add(entry);
			}
		}
		return sectionEntries;
	}

	/**
	 * Initialises sectionOccurrences and sections.
	 */
	private void initSections() {
		Document document = getCurrentDocument();
		sectionPresModels.clear();
		entriesMap.clear();
		invalidSectionPresModels.clear();

		if (document.numSections() == 0) {
			throw new IllegalArgumentException("A document must have at least " + //$NON-NLS-1$
					"one section. Document name: " + document.getName()); //$NON-NLS-1$
		}
		for (int i = 0, c = document.numSections(); i < c; ++i) {
			Section section = document.getSection(i);

			if (section.numOccurrences() == 0) {
				throw new IllegalArgumentException("A section must have at least " + //$NON-NLS-1$
						"one section occurrence. Section name: " +  //$NON-NLS-1$
						section.getName());
			}
			List<Entry> sectionEntries = getSectionEntries(section);

			// Add the section occurrences for this section
			for (int j = 0, d = section.numOccurrences(); j < d; ++j) {
				SectionOccurrence sectionOcc = section.getOccurrence(j);


				if (sectionOcc.isMultipleAllowed()) {
					List<SecOccInstance> secOccInstances =
						currentDocOccurrenceInstance.getSecOccInstances(sectionOcc);
					initSectionInstances(sectionEntries, sectionOcc, secOccInstances);
				}
				else {
					SectionPresModel presModel = new SectionPresModel(sectionOcc);
					entriesMap.put(presModel, sectionEntries);
					sectionPresModels.add(presModel);
				}
			}
		}
	}

	private void initSectionInstances(List<Entry> sectionEntries,
			SectionOccurrence sectionOcc, List<SecOccInstance> secOccInstances) {
		if (secOccInstances.size() < 1) {
			SecOccInstance secOccInstance = sectionOcc.generateInstance();
			currentDocOccurrenceInstance.addSecOccInstance(secOccInstance);
			SectionPresModel presModel = new SectionPresModel(secOccInstance);
			entriesMap.put(presModel, sectionEntries);
			sectionPresModels.add(presModel);
			return;
		}

		for (SecOccInstance secOccInstance : secOccInstances) {
			SectionPresModel presModel = new SectionPresModel(secOccInstance);
			entriesMap.put(presModel, sectionEntries);
			sectionPresModels.add(presModel);
		}
	}

	public boolean hasWarnings() {
		for (ResponsePresModel presModel : getPresModels(ResponsePresModel.class)) {
			ValidationResult result = presModel.getValidationModel()
			.getResult();
			if (result.hasWarnings()) {
				return true;
			}
		}
		return false;
	}

	public void unsetDisabledPresModelsFromDocInstance() {
		for (ResponsePresModel presModel : getPresModels(ResponsePresModel.class)) {
			EntryStatus status = RendererHelper.getInstance().getEntryStatus(presModel);
			if (status == EntryStatus.DISABLED) {
				//NOTE that clear() now only acts on EntryTableModels so
				//that empty rows are removed from tables before a document
				//occurrence is saved. For BasicPresModel and subclasses 
				//reset() is now used to nullify disabled entries in real time.
				presModel.clear();
			}
		}
	}

	/**
	 * Returns presModels that are instances of {@code presModelClass} in the
	 * current RendererHandler.
	 * @param <E>
	 * @param presModelClass
	 */
	private <E extends EntryPresModel> List<E> getCurrentPresModels(Class<E> presModelClass)    {
		return getPresModels(presModelClass, builderHandler.getCurrentRendererHandler());
	}

	/**
	 * Returns all the presModels in {@code renderersMap} that are an instance 
	 * of {@code presModelClass} in the provided rendererHandler.
	 * @param <E>
	 * @param presModelClass
	 */
	private static <E> List<E> getPresModels(Class<E> presModelClass,
			RendererHandler rendererHandler) {
		List<E> presModels = new ArrayList<E>();
		for (List<Renderer> renderers : rendererHandler.getRenderersMap().values()) {
			for (Renderer renderer : renderers) {

				if (!(renderer instanceof PresModelRenderer)) {
					continue;
				}
				EntryPresModel presModel = PresModelRenderer.class.cast(renderer)
				.getPresModel();
				if (presModelClass.isInstance(presModel)) {
					presModels.add(presModelClass.cast(presModel));
				}
			}
		}
		return presModels;
	}

	/**
	 * Returns presModels that are instances of {@code presModelClass} in all
	 * RendererHandlers.
	 * @param <E>
	 * @param presModelClass
	 */
	private <E extends EntryPresModel> List<E> getPresModels(Class<E> presModelClass)    {
		List<E> presModels = new ArrayList<E>();
		if (builderHandler == null)
			return presModels;
		for (RendererHandler rendererHandler : builderHandler.getRendererHandlers()) {
			presModels.addAll(getPresModels(presModelClass, rendererHandler));
		}
		return presModels;
	}

	public EnumSet<ValidationType> validateSection(boolean partial) {
		return validateSection(partial, true);
	}

	private EnumSet<ValidationType> validateSection(boolean partial,
			boolean setResult) {
		EnumSet<ValidationType> valTypes = EnumSet.noneOf(ValidationType.class);

		DocumentStatus docStatus = null;
		if (getCurrentDocOccurrenceInstance().getStatus() != null) {
			docStatus = DocumentStatus.valueOf(getCurrentDocOccurrenceInstance().getStatus());
		}

		for (ResponsePresModel presModel : getCurrentPresModels(ResponsePresModel.class)) {
			ValidationResult result = presModel.validate(partial);
			if (result.hasMessages()) {
				fillValidationTypes(valTypes, docStatus, result);
				if (setResult) {
					presModel.getValidationModel().setResult(result);
				}
			}
		}
		return valTypes;
	}

	public EnumSet<ValidationType> getSectionValidationTypes(
			SectionPresModel sectionPresModel) {
		EnumSet<ValidationType> valTypes = EnumSet.noneOf(ValidationType.class);
		RendererHandler rendererHandler = getBuilderHandler()
		.getRendererHandler(sectionPresModel);
		if (rendererHandler == null) {
			return valTypes;
		}
		DocumentStatus docStatus = null;
		if (getCurrentDocOccurrenceInstance().getStatus() != null) {
			docStatus = DocumentStatus.valueOf(getCurrentDocOccurrenceInstance().getStatus());
		}
		for (ResponsePresModel presModel : getPresModels(
				ResponsePresModel.class, rendererHandler)) {
			ValidationResult result = presModel.getValidationModel()
			.getResult();
			fillValidationTypes(valTypes, docStatus, result);
		}
		return valTypes;
	}

	public EnumSet<ValidationType> getCurrentSectionValidationTypes() {
		return getSectionValidationTypes(getCurrentSectionOccPresModel());
	}

	private void fillValidationTypes(EnumSet<ValidationType> validationTypes,
			DocumentStatus docStatus, ValidationResult result) {
		if (result.hasMessages()) {
			if (result.hasErrors()) {
				validationTypes.add(ValidationType.ERROR);
			} else if (docStatus == DocumentStatus.REJECTED
					&& (result.hasWarnings())) {
				validationTypes.add(ValidationType.WARNING);
			} else if (docStatus == DocumentStatus.INCOMPLETE
					&& result.hasWarnings()) {
				validationTypes.add(ValidationType.WARNING);
			} else if ( (docStatus == DocumentStatus.APPROVED || 
					docStatus == DocumentStatus.CONTROLLED)
					&& result.hasWarnings()) {
				validationTypes.add(ValidationType.WARNING);
			} else if ((docStatus == DocumentStatus.PENDING)
					&& result.hasMessages() && (!result.hasWarnings())) {
				Object message = result.getMessages().get(0);
				if (!(message instanceof TransformedMessage)) {
					validationTypes.add(ValidationType.INFORMATION);
				}
			}
		}
	}

	private boolean processSectionData() {
		DocumentStatus docStatus = null;
		if (getCurrentDocOccurrenceInstance().getStatus() != null) {
			docStatus = DocumentStatus.valueOf(getCurrentDocOccurrenceInstance().getStatus());
		}
		else {
			docStatus = DocumentStatus.VIEW_ONLY;
		}
		//no validation for dataset designer documents!
		if (docStatus == DocumentStatus.DATASET_DESIGNER || docStatus == DocumentStatus.VIEW_ONLY){
			return true;
		}

		EnumSet<ValidationType> valTypes = validateSection(false);
		fireValidationEvent(new ValidationEvent(this, valTypes, true));
		if (valTypes.isEmpty()) {
			return true;
		}
		return false;
	}

	public String getMessage(EnumSet<ValidationType> valTypes) {
		if (!valTypes.isEmpty()) {
			String message;
			if (valTypes.size() > 1) {
				message = EntryMessages.getString("ApplicationModel.multipleValidationTypes"); //$NON-NLS-1$
			}
			if (valTypes.contains(ValidationType.ERROR))    {
				message = EntryMessages.getString("ApplicationModel.validationErrors"); //$NON-NLS-1$;
			}
			else if (valTypes.contains(ValidationType.WARNING)) {
				message = EntryMessages.getString("ApplicationModel.validationWarnings"); //$NON-NLS-1$
			}
			else {
				message = EntryMessages.getString("ApplicationModel.validationMessages"); //$NON-NLS-1$;
			}
			return message;
		}
		return null;
	}

	public ImageIcon getIcon(EnumSet<ValidationType> valTypes) {
		if (!valTypes.isEmpty()) {
			ImageIcon icon;
			if (valTypes.contains(ValidationType.ERROR))    {
				icon = ValidationResultViewFactory.getErrorIcon();
			}
			else if (valTypes.contains(ValidationType.WARNING)) {
				icon = ValidationResultViewFactory.getWarningIcon();
			}
			else {
				icon = ValidationResultViewFactory.getInfoIcon();
			}
			return icon;
		}
		return null;
	}

	public void addDocOccurrenceCompletedListener(
			DocOccurrenceCompletedListener listener) {
		listenerList.add(DocOccurrenceCompletedListener.class, listener);
	}

	public void removeDocOccurrenceCompletedListener(
			DocOccurrenceCompletedListener listener) {
		listenerList.remove(DocOccurrenceCompletedListener.class, listener);
	}

	public void addValidationListener(ValidationListener listener) {
		listenerList.add(ValidationListener.class, listener);
	}

	public void removeValidationListener(ValidationListener listener) {
		listenerList.remove(ValidationListener.class, listener);
	}

	protected void fireValidationEvent(ValidationEvent event) {
		Object[] listeners = listenerList.getListenerList();

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ValidationListener.class) {
				((ValidationListener) listeners[i + 1]).
				validationEvent(event);
			}
		}
	}

	protected void fireDocOccurrenceCompletedEvent(DocOccurrenceCompletedEvent 
			event) {
		Object[] listeners = listenerList.getListenerList();

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == DocOccurrenceCompletedListener.class) {
				((DocOccurrenceCompletedListener) listeners[i + 1]).
				docOccurrenceCompleted(event);
			}
		}
	}

	/**
	 * @return the entries for the current section occurrence.
	 */
	public List<Entry> getCurrentEntries(){
		return entriesMap.get(getCurrentSectionOccPresModel());
	}

	public void addMultipleSectionOccListener(MultipleSectionOccListener listener) {
		listenerList.add(MultipleSectionOccListener.class, listener);
	}

	public void removeMultipleSectionOccListener(MultipleSectionOccListener listener) {
		listenerList.remove(MultipleSectionOccListener.class, listener);
	}

	protected void fireMultipleSectionOccEvent(MultipleSectionOccEvent event) {
		Object[] listeners = listenerList.getListenerList();

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == MultipleSectionOccListener.class) {
				((MultipleSectionOccListener) listeners[i + 1]).
				multipleSectioOccEvent(event);
			}
		}
	}

	public boolean nextSection() {
		return nextSection(false);
	}

	public boolean nextSection(boolean ignoreMultipleAllowed) {
		if (!hasNextSection()) {
			//disable the action so it can't receive any more events
			forwardAction.setEnabled(false);
		}

		if (!processSectionData()) {
			forwardAction.setEnabled(true);
			return false;
		}
		SectionOccurrence currentSectionOcc = sectionPresModels.get(
				sectionPresModelIndex).getSectionOccurrence();

		if (isMultipleSectionOcc(ignoreMultipleAllowed)) {
			fireMultipleSectionOccEvent(new MultipleSectionOccEvent(this,
					sectionPresModelIndex + 1,
					currentSectionOcc));
			//TODO Consider a tri-state return value instead of boolean since
			//this is different from the other returns.
			return false; 
		}

		if (!hasNextSection()) {
			fireDocOccurrenceCompletedEvent(new DocOccurrenceCompletedEvent(this,
					currentDocOccurrenceInstance));
			return false;
		}

		moveToNextSection();
		return true;
	}

	private boolean hasNextSection() {
		return sectionPresModelIndex < (sectionPresModels.size() - 1);
	}

	private boolean isMultipleSectionOcc(boolean ignoreMultipleAllowed) {
		SectionPresModel secPresModel = sectionPresModels.get(
				sectionPresModelIndex);

		Status currentStatus = currentDocOccurrenceInstance.getStatus();
		if (currentStatus != null &&
				( DocumentStatus.PENDING.toStatusLongName().equals(currentStatus.getLongName())
						|| DocumentStatus.APPROVED.toStatusLongName().equals(currentStatus.getLongName())
						|| DocumentStatus.VIEW_ONLY.toStatusLongName().equals(currentStatus.getLongName())
						|| DocumentStatus.DATASET_DESIGNER.toStatusLongName().equals(currentStatus.getLongName()) ) ) {
			//Document cannot have a new section unless it is incomplete, has not yet been
			//committed, or has been rejected
			return false;
		}

		SectionOccurrence currentSectionOcc = secPresModel.getSectionOccurrence();
		if (ignoreMultipleAllowed || (!currentSectionOcc.isMultipleAllowed())) {
			return false;
		}

		if (!hasNextSection()) {
			return true;
		}

		SectionPresModel nextSecPresModel = sectionPresModels.get(sectionPresModelIndex + 1);
		if (nextSecPresModel.getSecOccInstance() != null && 
				nextSecPresModel.getSectionOccurrence().equals(secPresModel.getSectionOccurrence())) {
			return false;
		}
		return true;

	}

	private void moveToNextSection() {
		int lastIndex = sectionPresModels.size() - 1;
		if (sectionPresModelIndex >= lastIndex) {
			throw new IllegalArgumentException("sectionPresModelIndex is: "  //$NON-NLS-1$
					+ sectionPresModelIndex + ". Maximum allowed: " + lastIndex); 
		}
		SectionPresModel oldSectionOcc = 
			sectionPresModels.get(sectionPresModelIndex);
		SectionPresModel currentSectionOcc = 
			sectionPresModels.get(++sectionPresModelIndex);

		if (builderHandler.hasNextRendererHandler()) {
			builderHandler.nextRendererHandler();
			RendererHandler nextRendererHandler = builderHandler.getCurrentRendererHandler();
			if (!nextRendererHandler.getSectionPresModel().equals(currentSectionOcc)) {
				builderHandler.previousRendererHandler();
				builderHandler.addRendererHandler(sectionPresModelIndex, 
						createRendererHandler(currentSectionOcc));
			}
		}
		else {
			builderHandler.addRendererHandler(
					createRendererHandler(currentSectionOcc));
		}
		invalidSectionPresModels.remove(getCurrentSectionOccPresModel());
		setActionsStatus();
		fireSectionChangedEvent(new SectionChangedEvent(this, oldSectionOcc,
				currentSectionOcc));

		return;
	}

	public void addSection(int index, SectionOccurrence sectionOcc, String reason) {
		SecOccInstance newSectionInstance = sectionOcc.generateInstance();
		//the index in the document instance's list is different from that in the
		//section pres models list, because the document instance's list only
		//contains the section occurrence instances for section occurrences
		//allowing multiple run-time instances. So, we loop through the section
		//pres models up until where we want to insert the new one and just count
		//the number that relate to section occurrence instances
		int counter = 0;
		for ( int i=0; i<index; i++ ){
			if ( null != sectionPresModels.get(i).getSecOccInstance()){
				counter++;
			}
		}
		currentDocOccurrenceInstance.addSecOccInstance(newSectionInstance, counter, reason);
		SectionPresModel secPresModel = new SectionPresModel(newSectionInstance);
		List<Entry> sectionEntries = getSectionEntries(sectionOcc.getSection());
		sectionPresModels.add(index, secPresModel);
		entriesMap.put(secPresModel, sectionEntries);
		builderHandler.addSectionPresModel(index, secPresModel);
		if ( null != currentRecord.getSecondaryRecord() ){
			//handle data replication
			DataSet secDs = currentRecord.getSecondaryRecord().getDataSet();
			Document secDoc = secDs.getDocument(currentDocOccurrence.getDocument().getSecondaryDocIndex().intValue());
			DocumentOccurrence secDocOcc = secDoc.getOccurrence(currentDocOccurrence.getSecondaryOccIndex().intValue());
			DocumentInstance secInst = currentRecord.getSecondaryRecord().getDocumentInstance(secDocOcc);
			int secIndex = 0;
			int secOccIndex = 0;
			exit:
			for ( int i=0, c=currentDocOccurrence.getDocument().numSections(); i<c ; i++ ){
				Section sec = currentDocOccurrence.getDocument().getSection(i);
				for ( int j=0, d=sec.numOccurrences(); j<d; j++){
					SectionOccurrence sOcc = sec.getOccurrence(j);
					if ( sOcc.equals(sectionOcc) ){
						secIndex = i;
						secOccIndex = j;
						break exit;
					}
				}
			}
			SectionOccurrence secSectionOcc = secDoc.getSection(secIndex).getOccurrence(secOccIndex);
			SecOccInstance secSecInst = secSectionOcc.generateInstance();
			secInst.addSecOccInstance(secSecInst);
		}
		fireSectionAddedEvent(new SectionEvent(this, secPresModel, index));
	}

	public boolean previousSection() {
		if (sectionPresModelIndex < 1) {
			return false;
		}
		EnumSet<ValidationType> sectionValidationTypes = 
			getSectionValidationTypes(getCurrentSectionOccPresModel());

		if (!sectionValidationTypes.isEmpty()) {
			invalidSectionPresModels.add(getCurrentSectionOccPresModel());
		}

		SectionPresModel oldSectionOcc = 
			sectionPresModels.get(sectionPresModelIndex);
		SectionPresModel currentSectionOcc = 
			sectionPresModels.get(--sectionPresModelIndex);
		builderHandler.previousRendererHandler();

		setActionsStatus();
		fireSectionChangedEvent(new SectionChangedEvent(this, oldSectionOcc,
				currentSectionOcc));
		return true;
	}

	protected void fireSectionChangedEvent(SectionChangedEvent event) {
		Object[] listeners = listenerList.getListenerList();

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == SectionListener.class) {
				((SectionListener) listeners[i + 1]).sectionChanged(event);
			}
		}
	}

	protected void fireSectionAddedEvent(SectionEvent event) {
		Object[] listeners = listenerList.getListenerList();

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == SectionListener.class) {
				((SectionListener) listeners[i + 1]).sectionAdded(event);
			}
		}
	}

	protected void fireSectionRemovedEvent(SectionEvent event) {
		Object[] listeners = listenerList.getListenerList();

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == SectionListener.class) {
				((SectionListener) listeners[i + 1]).sectionRemoved(event);
			}
		}
	}

	protected void fireCloseDocumentEvent(CloseDocumentEvent event) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == CloseDocumentListener.class) {
				((CloseDocumentListener) listeners[i + 1]).closeDocument(event);
			}
		}    	
	}

	protected void firePrintDocumentEvent(PrintDocumentEvent event) {
		Object[] listeners = listenerList.getListenerList();

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == PrintDocumentListener.class) {
				((PrintDocumentListener) listeners[i + 1]).printDocument(event);
			}
		}
	}

	protected void fireApplyStdCodeEvent(ApplyStdCodeEvent event) {
		Object[] listeners = listenerList.getListenerList();

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ApplyStdCodeListener.class) {
				((ApplyStdCodeListener) listeners[i + 1]).applyStdCode(event);
			}
		}
	}

	protected void fireApplyImportEvent(ApplyImportEvent event) {
		Object[] listeners = listenerList.getListenerList();

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ApplyImportEnabledListener.class) {
				((ApplyImportEnabledListener) listeners[i + 1]).doImport(event);
			}
		}
	}

	protected void fireInsertBeforeSecOccInstEvent(InsertBeforeSecOccInstEvent event) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == InsertBeforeSecOccInstListener.class) {
				((InsertBeforeSecOccInstListener) listeners[i + 1]).insertBeforeSecOccInst(event);
			}
		}    	
	}

	protected void fireInsertAfterSecOccInstEvent(InsertAfterSecOccInstEvent event) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == InsertAfterSecOccInstListener.class) {
				((InsertAfterSecOccInstListener) listeners[i + 1]).insertAfterSecOccInst(event);
			}
		}    	
	}

	protected void fireRemoveSecOccInstEvent(RemoveSecOccInstEvent event) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == RemoveSecOccInstListener.class) {
				((RemoveSecOccInstListener) listeners[i + 1]).removeSecOccInst(event);
			}
		}    	
	}

	private void createNavigationActions() {
		backAction = new BackAction();
		forwardAction = new ForwardAction();
		closeAction = new CloseAction();
		printAction = new PrintAction();
		applyStdCodeAction = new ApplyStdCodeAction();
		applyImportAction = new ApplyImportAction();
		setActionsStatus();
	}
	
	private void createSecOccInstActions(){
		insertBeforeSecOccInstAction = new InsertBeforeSecOccInstAction();
		insertAfterSecOccInstAction = new InsertAfterSecOccInstAction();
		removeSecOccInstAction = new RemoveSecOccInstAction();
	}

	public Action getInsertBeforeSecOccInstAction() {
		return insertBeforeSecOccInstAction;
	}

	public Action getInsertAfterSecOccInstAction() {
		return insertAfterSecOccInstAction;
	}

	public Action getRemoveSecOccInstAction() {
		return removeSecOccInstAction;
	}

	public void setActionsStatus() {
		if (sectionPresModelIndex < 1) {
			backAction.setEnabled(false);
		}
		else if (sectionPresModelIndex > 0) {
			backAction.setEnabled(true);
		}

		if (currentDocOccurrence == null) {
			closeAction.setEnabled(false);
			forwardAction.setEnabled(false);
			forwardAction.setForward();
			printAction.setEnabled(false);
			applyStdCodeAction.setEnabled(false);
			applyImportAction.setEnabled(false);
		}
		else {
			closeAction.setEnabled(true);
			forwardAction.setEnabled(true);
			if (!hasNextSection()) {
				if (!setForwardActionOnPending(docInstanceStatus))
					forwardAction.setSave();
			}
			else
				forwardAction.setForward();

			printAction.setEnabled(true);
			applyStdCodeAction.setEnabled(true);

			Status currentStatus = currentDocOccurrenceInstance.getStatus();
			if (currentStatus != null &&
					(  DocumentStatus.PENDING.toStatusLongName().equals(currentStatus.getLongName())
							|| DocumentStatus.APPROVED.toStatusLongName().equals(currentStatus.getLongName())
							|| DocumentStatus.REJECTED.toStatusLongName().equals(currentStatus.getLongName())
							|| DocumentStatus.CONTROLLED.toStatusLongName().equals(currentStatus.getLongName())
							|| DocumentStatus.VIEW_ONLY.toStatusLongName().equals(currentStatus.getLongName())
							|| DocumentStatus.DATASET_DESIGNER.toStatusLongName().equals(currentStatus.getLongName()) ) ) {
				//Document is not editable, so cannot have a standard code applied
				applyStdCodeAction.setEnabled(false);
			}
			
			if (currentStatus != null &&
					(  DocumentStatus.PENDING.toStatusLongName().equals(currentStatus.getLongName())
							|| DocumentStatus.APPROVED.toStatusLongName().equals(currentStatus.getLongName())
							|| DocumentStatus.VIEW_ONLY.toStatusLongName().equals(currentStatus.getLongName())
							|| DocumentStatus.DATASET_DESIGNER.toStatusLongName().equals(currentStatus.getLongName()) ) ) {
				//Document is not editable, so cannot use the section occurrence instance buttons
				insertAfterSecOccInstAction.setEnabled(false);
				insertBeforeSecOccInstAction.setEnabled(false);
				removeSecOccInstAction.setEnabled(false);
			}
			else{
				insertAfterSecOccInstAction.setEnabled(true);
				insertBeforeSecOccInstAction.setEnabled(true);
				removeSecOccInstAction.setEnabled(true);
			}
			
			Document doc = this.getCurrentDocument();
			if(doc == null){
				this.applyImportAction.setEnabled(false);
			}
			else{
				boolean getImportEnabled = doc.getIsImportEnabled();
				this.applyImportAction.setEnabled(getImportEnabled);
			}
		}
	}

	public boolean isSecOccInstActionsActive(){
		Status currentStatus = currentDocOccurrenceInstance.getStatus();
		return !(currentStatus != null &&
				(  DocumentStatus.PENDING.toStatusLongName().equals(currentStatus.getLongName())
				|| DocumentStatus.APPROVED.toStatusLongName().equals(currentStatus.getLongName())
				|| DocumentStatus.VIEW_ONLY.toStatusLongName().equals(currentStatus.getLongName())
				|| DocumentStatus.DATASET_DESIGNER.toStatusLongName().equals(currentStatus.getLongName())) );
	}
	
	/**
	 * Changes the forwardAction name to Reject or Approve if the DocumentStatus
	 * is PENDING.
	 */
	private boolean setForwardActionOnPending(DocumentStatus status) {
		if (status != null && status == DocumentStatus.PENDING) {
			if (hasWarnings())
				forwardAction.setRejectName();
			else
				forwardAction.setApprove();
			return true;
		}
		if (status != null && ( 
				status == DocumentStatus.APPROVED || 
				status == DocumentStatus.VIEW_ONLY ) ) {
			forwardAction.setClose();
			return true;
		}
		return false;
	}

	public Action getBackAction() {
		return backAction;
	}

	public Action getForwardAction() {
		return forwardAction;
	}

	public Action getCloseAction() {
		return closeAction;
	}

	public Action getPrintAction() {
		return printAction;
	}

	public Action getApplyStdCodeAction() {
		return applyStdCodeAction;
	}

	public Action getApplyImportAction() {
		return applyImportAction;
	}

	/**
	 * Convenience method that retrieves the RendererHandler for the section
	 * being displayed from the builderHandler property.
	 * @return RendererHandler for the section being displayed.
	 */
	public RendererHandler getCurrentRendererHandler() {
		return builderHandler.getCurrentRendererHandler();
	}

	public BuilderHandler getBuilderHandler() {
		return builderHandler;
	}

	public DataSet getDataSet() {
		return currentDocOccurrence.getDocument().getDataSet();
	}

	public void addSectionListener(SectionListener listener) {
		listenerList.add(SectionListener.class, listener);
	}

	public void removeSectionListener(SectionListener listener) {
		listenerList.remove(SectionListener.class, listener);
	}

	public Document getCurrentDocument() {
		return currentDocOccurrence.getDocument();
	}

	public DataSet getCurrentDataSet() {
		return currentRecord.getDataSet();
	}

	public Record getCurrentRecord() {
		return currentRecord;
	}

	public DocumentOccurrence getCurrentDocOccurrence() {
		return currentDocOccurrence;
	}

	public DocumentInstance getCurrentDocOccurrenceInstance() {
		return currentDocOccurrenceInstance;
	}

	public void registerValidationListener() {
		for (ResponsePresModel presModel : getCurrentPresModels(ResponsePresModel.class)) {
			String resultProperty = ValidationResultModel.PROPERTYNAME_RESULT;
			ValidationResultModel validationModel = presModel.getValidationModel();
			for (PropertyChangeListener listener : validationModel.getPropertyChangeListeners(resultProperty)) {
				if (listener.equals(validationListener)) {
					return;
				}
			}
			validationModel.addPropertyChangeListener(resultProperty,
					validationListener);
		}
	}

	public void tearDown(boolean saveState) throws IOException  {      
		if (saveState && currentDocOccurrenceInstance != null ) {
			UnfinishedDocInstance unfDocInstance = 
				new UnfinishedDocInstance(currentDocOccurrenceInstance, 
						sectionPresModelIndex, docInstanceStatus);
			PersistenceManager.getInstance().saveUnfinishedDocumentInstance(
					unfDocInstance);
		}
		//on clean shutdown we delete the auto saved document if it exists
		PersistenceManager.getInstance().deleteAutoSaveDocumentInstance();

		PersistenceManager.getInstance().dispose();
		SwingWorkerExecutor.getInstance().dispose();
	}

	public void autoSave(){
		if ( currentDocOccurrenceInstance != null ) {
			UnfinishedDocInstance unfDocInstance = null;
			try{
				unfDocInstance = 
					new UnfinishedDocInstance(currentDocOccurrenceInstance, 
							sectionPresModelIndex, docInstanceStatus);
				PersistenceManager.getInstance().saveAutoSaveDocumentInstance(
						unfDocInstance);
			}
			catch(IOException ex){
				LOG.error("Unable to auto save document", ex);
			}
			finally{
				unfDocInstance = null;
			}
		}
	}

	public void clear(boolean clearCurrentRecord) {
		if ( null != currentRecord && null != currentDocOccurrenceInstance ){
			currentDocOccurrenceInstance.detachFromRecord();
			LOG.info("currentRecord contains "+currentRecord.numDocumentInstances()+" doc instances");
		}
		if ( null != currentRecord ){
			currentRecord.setSecondaryRecord(null);
		}
		if (clearCurrentRecord)
			currentRecord = null;
		currentDocOccurrence = null;
		currentDocOccurrenceInstance = null;
		builderHandler = null;
		sectionPresModelIndex = 0;
		sectionPresModels.clear();
		entriesMap.clear();
		setActionsStatus();
		canRandomize = false;
		RendererHelper.getInstance().getAppModel().getEditedEntries().clear();
	}

	public int getCurrentSectionIndex() {
		return sectionPresModelIndex;
	}

	class BackAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public BackAction() {
			super(EntryMessages.getString("ApplicationModel.back"), //$NON-NLS-1$
					Icons.getInstance().getIcon("back")); //$NON-NLS-1$
			putValue(Action.SHORT_DESCRIPTION, "Move to previous section");
		}

		public void actionPerformed(ActionEvent e) {
			previousSection();   
		}
	}

	class ForwardAction extends AbstractAction  {
		private static final long serialVersionUID = 1L;

		public ForwardAction() {
			setNormalIcon();
			setForward();
		}

		private void setNormalIcon() {
			putValue(Action.SMALL_ICON, Icons.getInstance().getIcon("forward")); //$NON-NLS-1$
		}

		final void setRejectName() {
			setRedIcon();
			putValue(Action.NAME, EntryMessages.getString("ApplicationModel.reject")); //$NON-NLS-1$
			putValue(Action.SHORT_DESCRIPTION, "Reject Document");
		}

		private void setRedIcon() {
			putValue(Action.SMALL_ICON, Icons.getInstance().getIcon("redforward")); //$NON-NLS-1$
		}

		final void setApprove() {
			setRedIcon();
			putValue(Action.NAME, EntryMessages.getString("ApplicationModel.approve")); //$NON-NLS-1$
			putValue(Action.SHORT_DESCRIPTION, "Approve Document");
		}

		final void setClose() {
			setRedIcon();
			putValue(Action.NAME, EntryMessages.getString("ApplicationModel.close")); //$NON-NLS-1$
			putValue(Action.SHORT_DESCRIPTION, "Close Document");
		}

		final void setForward() {
			setNormalIcon();
			putValue(Action.NAME, EntryMessages.getString("ApplicationModel.forward")); //$NON-NLS-1$
			putValue(Action.SHORT_DESCRIPTION, "Move to next section");
		}

		final void setSave() {
			setRedIcon();
			putValue(Action.NAME, EntryMessages.getString("ApplicationModel.save")); //$NON-NLS-1$
			putValue(Action.SHORT_DESCRIPTION, "Save Document");
		}

		public void actionPerformed(ActionEvent e) {
			nextSection();   
		}
	}

	class CloseAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public CloseAction() {
			super(EntryMessages.getString("ApplicationModel.close"), //$NON-NLS-1$
					Icons.getInstance().getIcon("fileclose")); //$NON-NLS-1$
			putValue(Action.SHORT_DESCRIPTION, "Close Document");
		}

		public void actionPerformed(ActionEvent e) {
			closeCurrentDocument();
		}
	}

	class PrintAction extends AbstractAction  {
		private static final long serialVersionUID = 1L;

		public PrintAction() {
			super(EntryMessages.getString("ApplicationModel.print"), //$NON-NLS-1$
					Icons.getInstance().getIcon("printer")); //$NON-NLS-1$
			putValue(Action.SHORT_DESCRIPTION, "Print Document");
		}

		public void actionPerformed(ActionEvent e) {
			printCurrentDocument();   
		}
	}

	class ApplyStdCodeAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public ApplyStdCodeAction() {
			super(EntryMessages.getString("ApplicationModel.applystdcode"), //$NON-NLS-1$
					Icons.getInstance().getIcon("applystdcode")); //$NON-NLS-1$
			putValue(Action.SHORT_DESCRIPTION, "Apply Standard Code");
		}

		public void actionPerformed(ActionEvent e) {
			fireApplyStdCodeEvent(new ApplyStdCodeEvent(this));
		}

	}

	class ApplyImportAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public ApplyImportAction() {
			super(EntryMessages.getString("ApplicationModel.applyimport"), //$NON-NLS-1$
					Icons.getInstance().getIcon("downarrow")); //$NON-NLS-1$
			putValue(Action.SHORT_DESCRIPTION, "Import Document");
		}

		public void actionPerformed(ActionEvent e) {
			fireApplyImportEvent(new ApplyImportEvent(this));
		}

	}

	public void addApplyStdCodeListener(ApplyStdCodeListener listener) {
		listenerList.add(ApplyStdCodeListener.class, listener);
	}

	public void removeApplyStdCodeListener(ApplyStdCodeListener listener) {
		listenerList.remove(ApplyStdCodeListener.class, listener);
	}

	public void addApplyImportEnabledListener(ApplyImportEnabledListener applyImportEnabledListener) {
		listenerList.add(ApplyImportEnabledListener.class, applyImportEnabledListener);
	}

	public void removeApplyImportEnabledListener(ApplyImportEnabledListener applyImportEnabledListener) {
		listenerList.remove(ApplyImportEnabledListener.class, applyImportEnabledListener);
	}

	public void addInsertBeforeSecOccInstListener(InsertBeforeSecOccInstListener listener) {
		listenerList.add(InsertBeforeSecOccInstListener.class, listener);
	}

	public void removeInsertBeforeSecOccInstListener(InsertBeforeSecOccInstListener listener) {
		listenerList.remove(InsertBeforeSecOccInstListener.class, listener);
	}

	public void addInsertAfterSecOccInstListener(InsertAfterSecOccInstListener listener) {
		listenerList.add(InsertAfterSecOccInstListener.class, listener);
	}

	public void removeInsertAfterSecOccInstListener(InsertAfterSecOccInstListener listener) {
		listenerList.remove(InsertAfterSecOccInstListener.class, listener);
	}

	public void addRemoveSecOccInstListener(RemoveSecOccInstListener listener) {
		listenerList.add(RemoveSecOccInstListener.class, listener);
	}

	public void removeRemoveSecOccInstListener(RemoveSecOccInstListener listener) {
		listenerList.remove(RemoveSecOccInstListener.class, listener);
	}

	public void applyStdCodeToSection(StandardCode stdCode, boolean allSections) {
		SectionPresModel currentSpm = getCurrentSectionOccPresModel();
		for ( SectionPresModel spm: getDocSectionOccPresModels() ){
			if ( spm == currentSpm || allSections ){
				RendererHandler rh = builderHandler.getRendererHandler(spm);

				//Make a note of entries that belong to a composite
				Map<Entry, CompositeEntry> compositeMap = new HashMap<Entry, CompositeEntry>();
				for ( Entry e: entriesMap.get(spm) ){
					if (e instanceof CompositeEntry) {
						CompositeEntry comp = (CompositeEntry)e;
						for (int i=0; i < comp.numEntries(); i++) {
							compositeMap.put(comp.getEntry(i), comp);
						}
					}
				}

				if ( null == rh || !rh.isRendered() ){
					//RendererHandler for this section has not been created yet so
					//act directly on the repository model
					SectionOccurrence so = spm.getSectionOccurrence();
					SecOccInstance soi = spm.getSecOccInstance();

					for ( Entry e: entriesMap.get(spm) ){
						EditAction editable = e.getEditingPermitted();
						if (EditAction.DENY.equals(editable)
								|| EditAction.READONLY.equals(editable)) {
							//Entry is not editable so don't apply std code
							continue;
						}
						if (compositeMap.containsKey(e)) {
							editable = compositeMap.get(e).getEditingPermitted();
							if (EditAction.DENY.equals(editable)
									|| EditAction.READONLY.equals(editable)) {
								//The composite table that the entry is part of is not 
								//editable so don't apply std code
								continue;
							}
						}
						e.applyStandardCode(currentDocOccurrenceInstance, so, soi, stdCode);
					}
				}
				else{				
					//There is a RendererHandler for this section, and the section has 
					//already been rendered
					for (StandardPresModel presModel : getPresModels(StandardPresModel.class, rh)) {
						Entry entry = presModel.getEntry();
						// ignore boolean entries and derived entries, and entries that are disabled
						if (entry instanceof BooleanEntry
								|| entry instanceof DerivedEntry
								|| entry instanceof ExternalDerivedEntry
								|| presModel.getEntryStatusModel().getValue() == EntryStatus.DISABLED ) {
							continue;
						}
						EditAction editable = entry.getEditingPermitted();
						if (EditAction.DENY.equals(editable)
								|| EditAction.READONLY.equals(editable)) {
							//Entry is not editable so don't apply std code
							continue;
						}
						if (compositeMap.containsKey(entry)) {
							editable = compositeMap.get(entry).getEditingPermitted();
							if (EditAction.DENY.equals(editable)
									|| EditAction.READONLY.equals(editable)) {
								//The composite table that the entry is part of is not 
								//editable so don't apply std code
								continue;
							}
						}
						presModel.setStandardCode(stdCode);
					}
				}
			}
		}
	}

	public void addPrintDocumentListener(PrintDocumentListener listener) {
		listenerList.add(PrintDocumentListener.class, listener);
	}

	public void removePrintDocumentListener(PrintDocumentListener listener) {
		listenerList.remove(PrintDocumentListener.class, listener);
	}

	public void printCurrentDocument(){
		firePrintDocumentEvent(new PrintDocumentEvent(this));
	}

	public void addCloseDocumentListener(CloseDocumentListener listener) {
		listenerList.add(CloseDocumentListener.class, listener);
	}

	public void removeCloseDocumentListener(CloseDocumentListener listener) {
		listenerList.remove(CloseDocumentListener.class, listener);
	}

	public void closeCurrentDocument(){
		fireCloseDocumentEvent(new CloseDocumentEvent(this));
	}

	/**
	 * @return the indices of the sections that the application can transition
	 * to. 
	 */
	public List<Integer> getSectionTransitionsIndices() {
		List<Integer> sectionIndices = new ArrayList<Integer>();
		int numRendererHandlers = getBuilderHandler().getRendererHandlers().size();

		DocumentStatus docStatus = null;
		if (getCurrentDocOccurrenceInstance().getStatus() != null) {
			//docStatus may be null if a VIEW_ONLY document is automatically
			//opened when CoCoA is restarted.
			docStatus = DocumentStatus.valueOf(getCurrentDocOccurrenceInstance().getStatus());
		}

		//no validation for dataset designer documents!
		if (docStatus != DocumentStatus.DATASET_DESIGNER ){
			boolean currentSectionValid = validateSection(false, false).isEmpty();
			for (int index = 0; index < numRendererHandlers; ++index) {
				/* All the previous section indices are added */
				if (index < sectionPresModelIndex) {
					sectionIndices.add(Integer.valueOf(index));
				}
				/* Various checks are performed for following sections */
				else if (currentSectionValid && index > sectionPresModelIndex) {
					sectionIndices.add(Integer.valueOf(index));
					/* First invalid section is the last valid transition */
					if (invalidSectionPresModels.contains(sectionPresModels.get(index)))   {
						break;
					}
				}
			}
		}
		return sectionIndices;
	}

	public void setSection(int sectionIndex) {
		if (sectionPresModelIndex < sectionIndex) {
			while (nextSection(true) && (sectionPresModelIndex < sectionIndex)) {
				// Do nothing
			}
		}
		else if (sectionPresModelIndex > sectionIndex) {
			while (previousSection() && (sectionPresModelIndex > sectionIndex)) {
				// Do nothing;
			}
		}
	}

	public void saveUncommitableDocument(final Application app){

		SwingWorker<Object, Object> worker = new SwingWorker<Object, Object>(){
			@Override
			protected Object doInBackground() throws Exception {
				UnfinishedDocInstance docInstance = new UnfinishedDocInstance(getCurrentDocOccurrenceInstance(), 0, docInstanceStatus);
				PersistenceManager.getInstance().saveUncommitableDocumentInstance(docInstance);
				PersistenceManager.getInstance().updateRecord(currentRecord, currentDocOccurrenceInstance, DocumentStatus.COMMIT_FAILED);
				app.clear(false);
				return null;
			}

			@Override
			protected void done() {
				try{
					new ResetWaitRunnable(app).run();
					get();
				}
				catch(InterruptedException ie){
		            ExceptionsHelper.handleInterruptedException(ie);
				}
				catch(ExecutionException ee){
					String messageTitle = EntryMessages.getString("ApplicationModel.fileSaveProblemTitle");
					String message = EntryMessages.getString("ApplicationModel.fileSaveProblemMessage");
					WrappedJOptionPane.showWrappedMessageDialog(app, message, messageTitle, JOptionPane.ERROR_MESSAGE);

					if (LOG.isErrorEnabled()) {
						LOG.error("Uncommitable docment: " + getCurrentRecord().getIdentifier().getIdentifier() + 
								" - " + getCurrentDocOccurrence().getName() + " could not be saved: ", ee.getCause());
					}
				}
			}
		};
		
		new WaitRunnable(app).run();
		SwingWorkerExecutor.getInstance().execute(worker);
		
	}

	public void saveIncompleteDocument(final Application app, final boolean showMessage){
		if ( PersistenceManager.getInstance().getData().isAlwaysOnlineMode() ){
			SwingWorkerExecutor.getInstance().execute(
					new CommitDocumentWorker(app, getCurrentDocOccurrenceInstance(), false) {
						@Override
						protected void success() {
							ApplicationModel.this.saveIncompleteSuccess(app, showMessage);
						}

					});
		}
		else{
			SwingWorkerExecutor.getInstance().execute(
					new SaveIncompleteRecordWorker(app, getCurrentDocOccurrenceInstance().getRecord(), getCurrentDocOccurrenceInstance()){
						@Override
						protected void success() {
							ApplicationModel.this.saveIncompleteSuccess(app, showMessage);
						}
					});
		}
	}

	private void saveIncompleteSuccess(Application application, boolean showMessage) {		
		application.clear(false);
	}

	class InsertBeforeSecOccInstAction extends AbstractAction{

		private static final long serialVersionUID = 1L;

		public InsertBeforeSecOccInstAction() {
			super(EntryMessages.getString("ApplicationModel.insertBeforeSecOccInst"), //$NON-NLS-1$
					Icons.getInstance().getIcon("insertBeforeSecOccInst")); //$NON-NLS-1$
			putValue(Action.SHORT_DESCRIPTION, "Insert a new section before the current one");
		}
		
		public void actionPerformed(ActionEvent e) {
			fireInsertBeforeSecOccInstEvent(new InsertBeforeSecOccInstEvent(this));
		}
		
	}

	class InsertAfterSecOccInstAction extends AbstractAction{

		private static final long serialVersionUID = 1L;

		public InsertAfterSecOccInstAction() {
			super(EntryMessages.getString("ApplicationModel.insertAfterSecOccInst"), //$NON-NLS-1$
					Icons.getInstance().getIcon("insertAfterSecOccInst")); //$NON-NLS-1$
			putValue(Action.SHORT_DESCRIPTION, "Insert a new section after the current one");
		}
		
		public void actionPerformed(ActionEvent e) {
			fireInsertAfterSecOccInstEvent(new InsertAfterSecOccInstEvent(this));
		}
		
	}

	class RemoveSecOccInstAction extends AbstractAction{

		private static final long serialVersionUID = 1L;

		public RemoveSecOccInstAction() {
			super(EntryMessages.getString("ApplicationModel.removeSecOccInst"), //$NON-NLS-1$
					Icons.getInstance().getIcon("removeSecOccInst")); //$NON-NLS-1$
			putValue(Action.SHORT_DESCRIPTION, "Remove the current section");
		}
		
		public void actionPerformed(ActionEvent e) {
			fireRemoveSecOccInstEvent(new RemoveSecOccInstEvent(this));
		}
		
	}

	public void removeCurrentSecOccInstance(String reason){
		SectionPresModel spm = sectionPresModels.remove(sectionPresModelIndex);
		builderHandler.removeSectionPresModel(spm);
		entriesMap.remove(spm);
		currentDocOccurrenceInstance.removeSecOccInstance(spm.getSecOccInstance(), reason);
		
		if ( null != currentRecord.getSecondaryRecord() ){
			//handle data replication
			DataSet secDs = currentRecord.getSecondaryRecord().getDataSet();
			Document secDoc = secDs.getDocument(currentDocOccurrence.getDocument().getSecondaryDocIndex().intValue());
			DocumentOccurrence secDocOcc = secDoc.getOccurrence(currentDocOccurrence.getSecondaryOccIndex().intValue());
			DocumentInstance secInst = currentRecord.getSecondaryRecord().getDocumentInstance(secDocOcc);
			int secIndex = 0;
			int secOccIndex = 0;
			exit:
			for ( int i=0, c=currentDocOccurrence.getDocument().numSections(); i<c ; i++ ){
				Section sec = currentDocOccurrence.getDocument().getSection(i);
				for ( int j=0, d=sec.numOccurrences(); j<d; j++){
					SectionOccurrence sOcc = sec.getOccurrence(j);
					if ( sOcc.equals(spm.getSectionOccurrence()) ){
						secIndex = i;
						secOccIndex = j;
						break exit;
					}
				}
			}
			SectionOccurrence secSectionOcc = secDoc.getSection(secIndex).getOccurrence(secOccIndex);
			SecOccInstance secSecInst = secSectionOcc.generateInstance();
			secInst.removeSecOccInstance(secSecInst, reason);
		}
		
		if ( sectionPresModelIndex == sectionPresModels.size() ){
			sectionPresModelIndex--;
		}

		fireSectionRemovedEvent(new SectionEvent(this, spm, sectionPresModelIndex));
		SectionPresModel newSpm = sectionPresModels.get(sectionPresModelIndex);
		fireSectionChangedEvent(new SectionChangedEvent(this, null, newSpm));
	}

	/**
	 * 
	 * @return
	 */
	public int getCurrentSecOccInstCount(){
		SectionOccurrence secOcc = sectionPresModels.get(sectionPresModelIndex).getSectionOccurrence();
		int counter = 0;
		for ( SectionPresModel spm: sectionPresModels ){
			if ( spm.getSectionOccurrence().equals(secOcc) ){
				counter++;
			}
		}
		return counter;
	}
	
	public void insertBeforeSecOccInst(String reason){
		addSection(sectionPresModelIndex, sectionPresModels.get(sectionPresModelIndex).getSectionOccurrence(), reason);
		//increment the current index, since we have added a pres model
		//in front of the current one
		sectionPresModelIndex++;
		
		SectionPresModel oldSectionOcc = 
			sectionPresModels.get(sectionPresModelIndex);
		SectionPresModel currentSectionOcc = 
			sectionPresModels.get(--sectionPresModelIndex);

		builderHandler.addRendererHandler(sectionPresModelIndex,
				createRendererHandler(currentSectionOcc));
		builderHandler.previousRendererHandler();	
		
		setActionsStatus();
		fireSectionChangedEvent(new SectionChangedEvent(this, oldSectionOcc, currentSectionOcc));

	}
	
	public void insertAfterSecOccInst(String reason){
		addSection(sectionPresModelIndex+1, sectionPresModels.get(sectionPresModelIndex).getSectionOccurrence(), reason);
		
		SectionPresModel oldSectionOcc = 
			sectionPresModels.get(sectionPresModelIndex);
		SectionPresModel currentSectionOcc = 
			sectionPresModels.get(++sectionPresModelIndex);

		builderHandler.addRendererHandler(sectionPresModelIndex,
				createRendererHandler(currentSectionOcc));
		builderHandler.nextRendererHandler();	
		
		setActionsStatus();
		fireSectionChangedEvent(new SectionChangedEvent(this, oldSectionOcc, currentSectionOcc));

	}

	public boolean isCanRandomize() {
		return canRandomize;
	}

	public void setCanRandomize(boolean canRandomize) {
		this.canRandomize = canRandomize;
	}
}
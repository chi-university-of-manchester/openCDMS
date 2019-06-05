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

package org.psygrid.datasetdesigner.utils;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.xml.rpc.ServiceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.data.DELServiceFault;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.model.hibernate.Unit.UnitChainLink;
import org.psygrid.data.repository.RepositoryServiceFault;
import org.psygrid.data.utils.security.NotAuthorisedFault;
import org.psygrid.datasetdesigner.controllers.DatasetController;
import org.psygrid.datasetdesigner.listeners.RefreshListener;
import org.psygrid.datasetdesigner.model.DocTreeModel;
import org.psygrid.datasetdesigner.model.DummyDocument;
import org.psygrid.datasetdesigner.model.StudyDataSet;
import org.psygrid.datasetdesigner.ui.DocumentPanel;
import org.psygrid.datasetdesigner.ui.MainFrame;
import org.psygrid.datasetdesigner.ui.MainTabbedPane;
import org.psygrid.datasetdesigner.ui.editdialogs.AbstractEditDialog;
import org.psygrid.datasetdesigner.ui.editdialogs.BooleanEditDialog;
import org.psygrid.datasetdesigner.ui.editdialogs.CompositeEditDialog;
import org.psygrid.datasetdesigner.ui.editdialogs.DateEditDialog;
import org.psygrid.datasetdesigner.ui.editdialogs.DerivedEditDialog;
import org.psygrid.datasetdesigner.ui.editdialogs.ExternalDerivedEditDialog;
import org.psygrid.datasetdesigner.ui.editdialogs.IntegerEditDialog;
import org.psygrid.datasetdesigner.ui.editdialogs.LongTextEditDialog;
import org.psygrid.datasetdesigner.ui.editdialogs.NarrativeEditDialog;
import org.psygrid.datasetdesigner.ui.editdialogs.NumericEditDialog;
import org.psygrid.datasetdesigner.ui.editdialogs.OptionEditDialog;
import org.psygrid.datasetdesigner.ui.editdialogs.TextEditDialog;

public class Utils 
{

	private static final Log LOG = LogFactory.getLog(Utils.class);

	/**
	 * @param labelString
	 * @param button
	 * @param list
	 * @return
	 */
	public static JComponent createSubPanel(String labelString, JComponent list)
	{
		JPanel subPanel = new JPanel();
		subPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		subPanel.setLayout(new BorderLayout());
		subPanel.add(createLabelPanel(labelString), BorderLayout.NORTH);
		JScrollPane scroller = new JScrollPane(list);
		scroller.setPreferredSize(new Dimension(250, 200));
		subPanel.add(scroller, BorderLayout.CENTER);
		return subPanel;
	}

	/**
	 * Create the panel containing the arrows for assigning/removing options
	 * @param rightButton
	 * @param leftButton
	 * @return
	 */
	public static JPanel createArrowPanel(JButton rightButton, JButton leftButton)
	{
		JPanel arrowPanel = new JPanel();
		arrowPanel.setLayout(new BoxLayout(arrowPanel, BoxLayout.Y_AXIS));
		arrowPanel.add(leftButton);
		arrowPanel.add(Box.createVerticalStrut(6));
		arrowPanel.add(rightButton);
		return arrowPanel;
	}
	


	/**
	 * Creates the header panel for the listbox seen in multiple wizard components.
	 * @param labelString
	 * @param list
	 * @param assignButton
	 * @return the correctly layed out JPanel
	 */
	public static JPanel createLabelPanel(String labelString)
	{
		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new BorderLayout());
		labelPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		labelPanel.add(new JLabel(labelString), BorderLayout.WEST);
		return labelPanel;
	}

	/**
	 * Checks to see if the document has at least one new entry that is a submission 
	 * candidate to the data element library.
	 * Note - this method does not address imported elements that have been amended.
	 * @param doc
	 * @return - true if there's at least one, false otherwise
	 */
	public static boolean docHasSubmittableEntries(Document doc){
		boolean hasNewEntries = false;
		List<Entry> entries = doc.getEntries();

		//If just one entry does not already have an lsid, it means that it is new
		//(not imported from the element library), and is a submission candidate
		loop1 : for(Entry e: entries){

			if(e instanceof CompositeEntry){
				for(Entry e1: ((CompositeEntry)e).getEntries()){
					if (e1.getLSID() == null || (e1.getLSID()!= null && e1.getIsRevisionCandidate() == true)){
						hasNewEntries = true;
						break loop1;
					}
				}
			}

			if (e.getLSID() == null || (e.getLSID()!= null && e.getIsRevisionCandidate() == true)){
				hasNewEntries = true;
				break;
			}
		}

		return hasNewEntries;
	}

	public static void createImportPreviewContextForEntry(Entry entry){
		HibernateFactory factory = new HibernateFactory();
		DataSet dSet = factory.createDataset("Sample", "Sample");
		dSet.setRandomizationRequired(false);
		dSet.setProjectCode("SAMPLE");
		dSet.setEslUsed(false);
		dSet.setScheduleStartQuestion("What is your quest?");
		dSet.setExportSecurityActive(false);

		Document tempDoc = factory.createDocument("TEMP", "TEMP");
		Section tempSection = factory.createSection("TEMP", "TEMP");
		tempDoc.addSection(tempSection);

		SectionOccurrence secOcc = factory.createSectionOccurrence("TEMP");
		tempSection.addOccurrence(secOcc);

		Status status = factory.createStatus("TEMP", "TEMP", 1);
		tempDoc.addStatus(status);

		tempDoc.addEntry(entry);
		dSet.addDocument(tempDoc);
		entry.setMyDataSet((DataSet)dSet);
		entry.setSection(tempSection);
	}

	public static void createImportPreviewContextForDocument(Document doc) {
		HibernateFactory factory = new HibernateFactory();
		DataSet dSet = factory.createDataset("Sample", "Sample");
		dSet.setRandomizationRequired(false);
		dSet.setProjectCode("SAMPLE");
		dSet.setEslUsed(false);
		dSet.setScheduleStartQuestion("What is your quest?");
		dSet.setExportSecurityActive(false);

		dSet.addDocument(doc);
	}

	/**
	 * Checks to see if the dataset has at least one submittable document.
	 * This is defined as a new document, without an lsid, with at least one section.
	 * @param ds
	 * @return
	 */
	public static boolean dataSetHasSubmittableDocuments(DataSet ds){
		boolean hasNewEntries = false;
		List<Document> documents = ds.getDocuments();

		//If just one entry does not already have an lsid, it means that it is new
		//(not imported from the element library), and is a submission candidate
		for(Document d: documents){
			if (d.getLSID() == null && d.getSections().size() > 0 || d.getIsRevisionCandidate()){
				//If this document has been restricted then pass it by.
				StudyDataSet dsDS = DocTreeModel.getInstance().getDSDataset(d.getMyDataSet().getName());
				if(!dsDS.isDelRestricted(d.getName())){
					hasNewEntries = true;
					break;
				}
			}
		}

		return hasNewEntries;
	}

	/**
	 * Check to see whether any elements that have been checked out into the
	 * Data Element Library view are submittable. i.e whether any elements
	 * are new or have been edited.
	 * 
	 * @return boolean
	 */
	public static boolean hasSubmittableElements(){
		DataSet ds = null;
		StudyDataSet dsDS = DocTreeModel.getInstance().getDELDataset();
		if (dsDS != null) {
			ds = (DataSet)dsDS.getDs();		
		}

		if (ds == null) {
			return false;
		}

		List<Document> documents = ds.getDocuments();

		//If just one entry does not already have an lsid, it means that it is new
		//(not imported from the element library), and is a submission candidate
		for(Document d: documents){
			if (d.getLSID() == null && d.getSections().size() > 0 || d.getIsRevisionCandidate()){
				//If this document has been restricted then pass it by.
				if(!dsDS.isDelRestricted(d.getName())){
					return true;
				}
			}

			List<Entry> entries = d.getEntries();
			//If just one entry does not already have an lsid, it means that it is new
			//(not imported from the element library), and is a submission candidate
			for(Entry e: entries){
				if(e instanceof CompositeEntry){
					for(Entry e1: ((CompositeEntry)e).getEntries()){
						if (e1.getLSID() == null || (e1.getLSID()!= null && e1.getIsRevisionCandidate() == true)){
							return true;
						}
					}
				}

				if (e.getLSID() == null || (e.getLSID()!= null && e.getIsRevisionCandidate() == true)){
					return true;
				}
			}
		}

		return false;
	}

	public static boolean hasSubmittableValidationRules() {
		DataSet ds = null;
		StudyDataSet dsDS = DocTreeModel.getInstance().getDELDataset();
		if (dsDS != null) {
			ds = (DataSet)dsDS.getDs();		
		}

		if (ds == null) {
			return false;
		}

		for (ValidationRule rule: ds.getValidationRules()) {
			if (rule.getLSID() == null || rule.getIsRevisionCandidate()){
				return true;
			}
		}
		return false;
	}

	/**
	 * Check to see whether any elements that have been checked out into the
	 * Data Element Library view are ready for approval
	 * 
	 * @return boolean
	 */
	public static boolean hasApprovableDocuments(){
		DataSet ds = null;
		StudyDataSet dsDS = DocTreeModel.getInstance().getDELDataset();
		if (dsDS != null) {
			ds = (DataSet)dsDS.getDs();		
		}

		if (ds == null) {
			return false;
		}

		List<Document> documents = ds.getDocuments();

		//If just one entry does not already have an lsid, it means that it is new
		//(not imported from the element library), and is a submission candidate
		for(Document d: documents){
			if (d.getStatus() != null && d.getStatus().equals(DataElementStatus.PENDING)
					&& !d.getIsRevisionCandidate()) {
				//Document is pending and has not been edited
				return true;
			}
		}

		return false;
	}

	public static boolean hasApprovableValidationRules() {
		DataSet ds = null;
		StudyDataSet dsDS = DocTreeModel.getInstance().getDELDataset();
		if (dsDS != null) {
			ds = (DataSet)dsDS.getDs();		
		}

		if (ds == null) {
			return false;
		}

		for (ValidationRule rule: ds.getValidationRules()) {
			if (rule.getStatus() != null
					&& rule.getStatus().equals(DataElementStatus.PENDING)
					&& !rule.getIsRevisionCandidate()){
				return true;
			}
		}
		return false;
	}

	public static void addElementPropertiesToDataset(DataSet ds, DataElementContainer element){
		if(element.getElement() instanceof CompositeEntry){
			CompositeEntry compEnt = (CompositeEntry) element.getElement();
			for(BasicEntry bE: compEnt.getEntries()){
				addTransformersToDataSet(ds, bE);
				addUnitsToDataSet(ds, bE);
				addValidationRulesToDataSet(ds, bE);
			}
		}

		if (!(element.getElement() instanceof ValidationRule)) {
			addTransformersToDataSet(ds, element.getElement());
			addUnitsToDataSet(ds, element.getElement());
			addValidationRulesToDataSet(ds, element.getElement());
		}

		if (element.getElement() instanceof ExternalDerivedEntry) {
			addExternalTransformerToDataSet(ds, element.getElement());
		}
	}

	private static void addUnitToDataSet(Unit unit, DataSet ds){

		List<UnitChainLink> unitChainInfo = unit.buildUnitChainInfo();
		for(int i = unitChainInfo.size()-1; i >= 0; i--){ //Iterate backwards, because END link is at end of list.
			UnitChainLink link = unitChainInfo.get(i);

			ds.addUnit(link.getUnit());

		} //END backwards iteration through unit chain info list.

	}

	private static void addUnitsToDataSet(DataSet ds, Object element) {
		if(element instanceof BasicEntry) {
			BasicEntry bE = (BasicEntry)element;
			int unitCount = bE.numUnits();
			for(int i = 0; i < unitCount; i++) {
				boolean matchFound = false;
				for(Unit dsUnit: ds.getUnits()) {
					if(dsUnit.isEquivalentTo((Unit)bE.getUnit(i))) {
						//Replace the element's unit with the one already stored within the dataset.
						bE.getUnits().set(i, dsUnit);
						matchFound = true;
						break;
					}
				}
				if(!matchFound) {
					addUnitToDataSet((Unit)bE.getUnit(i), ds);
				}
			}
		}
	}

	private static void addValidationRulesToDataSet(DataSet ds, Object element){
		if(element instanceof BasicEntry){
			BasicEntry bE = (BasicEntry)element;
			int validationRuleCount = bE.numValidationRules();
			for(int i = 0; i < validationRuleCount; i++){
				boolean matchFound = false;
				for(ValidationRule dsRule: ds.getValidationRules()){
					if(dsRule.isEquivalentTo((ValidationRule)bE.getValidationRule(i))){
						bE.getValidationRules().set(i, dsRule);
						matchFound = true;
						break;
					}
				}

				if(!matchFound){
					ds.addValidationRule(bE.getValidationRule(i));
				}
			}

		}
	}

	public static boolean elementRequiresPreview(Object element){
		if(element instanceof OptionEntry){
			for(Option op:((OptionEntry)element).getOptions()){
				List<OptionDependent> opDeps = op.getOptionDependents();
				if(opDeps != null && opDeps.size() > 0){
					return true;
				}
			}
		}else if(element instanceof DerivedEntry ||
				element instanceof ExternalDerivedEntry){
			return false;
		}	

		return false;
	}

	public enum PreparationType{
		Preview,
		Import
	}


	public static void prepareElement(Object importElement, DocumentPanel panel){

		Document tempDoc = (Document)panel.getDocument();

		((Entry)importElement).setEntryStatus(EntryStatus.MANDATORY); 

		if(importElement instanceof Entry){

			//If this is a derived entry, option entry, or external derived entry, we need to also
			//Import the related entries.
			if(importElement instanceof DerivedEntry){
				DerivedEntry derivedEntry = (DerivedEntry)importElement;

				Set<String> keySet = derivedEntry.getVariableNames();
				for(String str: keySet){
					Entry depEntry = (Entry)derivedEntry.getVariable(str);
					if(depEntry.getEntryStatus() == null){
						if(depEntry instanceof CompositeEntry ||
								depEntry instanceof DerivedEntry ||
								depEntry instanceof ExternalDerivedEntry){
							depEntry.setMyDataSet(tempDoc.getDataSet());
							prepareElement(depEntry, panel);
						}
						depEntry.setEntryStatus(EntryStatus.MANDATORY); 	
						depEntry.setSection(tempDoc.getSection(0));
					}

					panel.importEntryNoRefresh(depEntry);
				}

			}else if(importElement instanceof ExternalDerivedEntry){
				ExternalDerivedEntry derivedEntry = (ExternalDerivedEntry)importElement;

				Set<String> keySet = derivedEntry.getVariableNames();
				for(String str: keySet){
					Entry depEntry = (Entry)derivedEntry.getVariable(str);
					if(depEntry.getEntryStatus() == null){
						//If this is a composite entry or a derived entry,
						//we need to check make sure their statuses also get set.

						if(depEntry instanceof CompositeEntry ||
								depEntry instanceof DerivedEntry ||
								depEntry instanceof ExternalDerivedEntry){
							depEntry.setMyDataSet(tempDoc.getDataSet());
							prepareElement(depEntry, panel);
						}
						depEntry.setEntryStatus(EntryStatus.MANDATORY); 	
						depEntry.setSection(tempDoc.getSection(0)); //TODO:DEL - this only adds to the 1st section?
					}

					panel.importEntryNoRefresh(depEntry);
				}

			}else if (importElement instanceof OptionEntry){
				//Associate the any dependent entries with the document
				OptionEntry oE = (OptionEntry)importElement;
				for(Option option: oE.getOptions()) {
					for(OptionDependent opDep: option.getOptionDependents()) {
						if(opDep.getMyDependentEntry() instanceof CompositeEntry ||
								opDep.getMyDependentEntry() instanceof ExternalDerivedEntry ||
								opDep.getMyDependentEntry() instanceof DerivedEntry){
							opDep.getMyDependentEntry().setMyDataSet(tempDoc.getDataSet());
							prepareElement(opDep.getMyDependentEntry(), panel);
						}
						opDep.getMyDependentEntry().setEntryStatus(EntryStatus.MANDATORY);
						opDep.getMyDependentEntry().setSection(tempDoc.getSection(0));

						panel.importEntryNoRefresh(opDep.getMyDependentEntry());
					}
				}

			}else if (importElement instanceof CompositeEntry){
				panel.prepareCompositeEntry((CompositeEntry)importElement);
			}

		}
	}

	/**
	 * Retrieve the complete element from the DEL
	 * 
	 * @param mainPane
	 * @param lsid
	 * @return dataElementContainer
	 * @throws ServiceException 
	 * @throws DELServiceFault 
	 * @throws RepositoryServiceFault 
	 * @throws IllegalArgumentException 
	 * @throws NotAuthorisedFault 
	 * @throws Exception
	 */
	public static DataElementContainer getCompleteBrowseElement(MainTabbedPane mainPane, String lsid) throws RemoteException, NotAuthorisedFault, IllegalArgumentException, RepositoryServiceFault, DELServiceFault, ServiceException {

		DataElementContainer importElement = null;
		String saml = mainPane.getDelInitializer().getSaml();
		importElement = mainPane.getDelInitializer().getClient().getCompleteElement(lsid, saml, true);
		return importElement;	
	}

	/**
	 * Retrieve the latest version of the element from the DEL.
	 * 
	 * This method returns the most recent complete element that the user
	 * has access to, i.e a viewer will get the most recent approved element
	 * and an author/curator will get the current 'head' revision.
	 * 
	 * @param mainPane
	 * @param lsid
	 * @return dataElementContainer
	 * @throws ServiceException 
	 * @throws DELServiceFault 
	 * @throws RepositoryServiceFault 
	 * @throws RemoteException 
	 * @throws IllegalArgumentException 
	 * @throws NotAuthorisedFault 
	 * @throws Exception
	 */
	public static DataElementContainer getUpdatedCompleteElement(MainTabbedPane mainPane, String lsid) throws RemoteException, RepositoryServiceFault, DELServiceFault, ServiceException {
		DataElementContainer importElement = null;
		String saml = mainPane.getDelInitializer().getSaml();
		importElement = mainPane.getDelInitializer().getClient().getCompleteElement(lsid, saml, true);
		return importElement;	
	}

	/**
	 * The entry's subordinate elements is returned.
	 * @param doc
	 * @param entry
	 * @return
	 */
	public static List<DataElementContainer> getSubordinateElements(final DataElementContainer element){
		List<DataElementContainer> subordinateElements = new ArrayList<DataElementContainer>();

		if(element.getElement() instanceof OptionEntry){
			OptionEntry opEntry = (OptionEntry)element.getElement();

			//Go through the options
			int optionCount = opEntry.getOptions().size();
			for(int i = 0; i < optionCount; i++){
				Option opt = opEntry.getOptions().get(i);
				int optDepCount = opt.getOptionDependents().size();
				for(int j = 0; j < optDepCount; j++){
					subordinateElements.add(new DataElementContainer((Entry)opt.getOptionDependents().get(j).getDependentEntry()));
				}
			}
		}else if(element.getElement() instanceof DerivedEntry){
			DerivedEntry devEntry = (DerivedEntry)element.getElement();
			Iterator it = devEntry.getVariables().entrySet().iterator();
			while ( it.hasNext() ){
				Map.Entry e = (Map.Entry)it.next();
				subordinateElements.add(new DataElementContainer((Entry) e.getValue()));
			}
		}else if(element.getElement() instanceof ExternalDerivedEntry){
			ExternalDerivedEntry exDevEntry = (ExternalDerivedEntry)element.getElement();
			Iterator it = exDevEntry.getVariables().entrySet().iterator();
			while ( it.hasNext() ){
				Map.Entry e = (Map.Entry)it.next();
				subordinateElements.add(new DataElementContainer((Entry) e.getValue()));
			}
		} else if(element.getElement() instanceof CompositeEntry){
			CompositeEntry compEntry = (CompositeEntry)element.getElement();
			Iterator it = compEntry.getEntries().iterator();
			while ( it.hasNext() ){
				subordinateElements.add(new DataElementContainer((Entry) it.next()));
			}
		}
		return subordinateElements;
	}

	/**
	 * returns whether the element is the child of a secondary relationship
	 * (i.e. option dependent, derived entry variable, external derived entry variable)
	 * @param elem
	 * @param doc
	 * @return
	 */
	public static boolean elementIsASubordinate(DataElementContainer elem, Document doc){

		//clean up references first for Derived Entry
		for (int j=0; j<doc.numEntries(); j++) {
			Entry curEntry = doc.getEntry(j);
			if (curEntry instanceof DerivedEntry) {
				ArrayList<String> varNames = new ArrayList<String>(((DerivedEntry)curEntry).getVariableNames());
				for (int z=varNames.size()-1; z>=0; z--) {
					if ((((DerivedEntry)curEntry).getVariable(varNames.get(z))) == elem.getElement()) {
						return true;
					}
				}
			}
		}

		//clean up references first for External Derived Entry
		for (int j=0; j<doc.numEntries(); j++) {
			Entry curEntry = doc.getEntry(j);
			if (curEntry instanceof ExternalDerivedEntry) {
				ArrayList<String> varNames = new ArrayList<String>(((ExternalDerivedEntry)curEntry).getVariableNames());
				for (int z=varNames.size()-1; z>=0; z--) {
					if ((((ExternalDerivedEntry)curEntry).getVariable(varNames.get(z))) == elem.getElement()) {
						JOptionPane.showMessageDialog(new JFrame(), "This entry is used by an external derived entry.  You must delete or edit the external derived entry first.");
						return true;
					}
				}
			}
		}

		//clean up option dependencies
		ArrayList<Entry> origEntries = new ArrayList<Entry>();
		for (int j=0; j<doc.numEntries(); j++) {
			if(doc.getEntry(j) != elem.getElement())
				origEntries.add(doc.getEntry(j));
		}

		for (int z=0; z<doc.numEntries(); z++) {
			Entry curEntry = doc.getEntry(z);
			if (curEntry instanceof OptionEntry) {
				int numOptions = ((OptionEntry)curEntry).numOptions();
				for (int y=0; y<numOptions; y++) {
					Option option = ((OptionEntry)curEntry).getOption(y);
					for (int h=0; h<option.numOptionDependents(); h++) {
						OptionDependent dep = option.getOptionDependent(h);
						if (elem.getElement()  == dep.getDependentEntry()) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	/**
	 * Determines whether the entry has either primary or secondary data element relationships
	 * with other elements. (If so, then extra work is required in order to populate the lsids of the other
	 * elements, in the event that the collection has been added to the element library, or modified).
	 * @param entry
	 * @return - true of it does have other related elements, false otherwise
	 */
	public static boolean  submittedElementHasSubordinates(DataElementContainer element){

		boolean hasSubordinates = false;

		if(element.getElement() instanceof Document){
			Document doc = (Document) element.getElement();
			if(doc.getEntries().size() > 0){
				hasSubordinates = true;
			}
		}

		if(element.getElement() instanceof CompositeEntry){
			//Find the entry by binary comparison within the document.
			//Once it is found, populate the object with the new entry's

			CompositeEntry compositeEntry = (CompositeEntry) element.getElement();
			int subordinateEntryCount = compositeEntry.getEntries().size();
			if(subordinateEntryCount > 0){
				hasSubordinates = true;
			}
		}

		if(element.getElement() instanceof OptionEntry){
			OptionEntry optionEntry = (OptionEntry) element.getElement();

			//Go through the options
			int optionCount = optionEntry.getOptions().size();
			for(int i = 0; i < optionCount; i++){
				Option opt = optionEntry.getOptions().get(i);
				int optDepCount = opt.getOptionDependents().size();
				if(optDepCount > 0){
					hasSubordinates = true;
					break;
				}
			}
		}

		if(element.getElement() instanceof DerivedEntry){
			DerivedEntry derivedEntry = (DerivedEntry)element.getElement();
			if(derivedEntry.getVariables().size() > 0){
				hasSubordinates = true;
			}
		}

		if(element.getElement() instanceof ExternalDerivedEntry){
			ExternalDerivedEntry extDerivedEntry = (ExternalDerivedEntry) element.getElement();
			if(extDerivedEntry.getVariables().size() > 0){
				hasSubordinates = true;
			}
		}

		return hasSubordinates;
	}

	/**
	 * Get the main entry that is the focus of the document. 
	 * 
	 * i.e the entry that was imported from the DEL. Entries, such
	 * as derived entries will have more than one entry imported 
	 * into the document document.
	 * 
	 * @param document
	 * @return entry
	 */
	public static Entry getMainEntry(DummyDocument document) {
		if (document.numEntries() == 0) {
			return null;
		}
		Entry singleEntry = document.getEntry(0);
		for (Object entry: document.getEntries()) {
			if (entry instanceof ExternalDerivedEntry) {
				singleEntry = (Entry)entry;
			}
			else if (entry instanceof DerivedEntry) {
				singleEntry = (Entry)entry;
			}
			else if (entry instanceof OptionEntry) {
				singleEntry = (Entry)entry;
			}
		}
		return singleEntry;
	}

	private static void addTransformersToDataSet(DataSet ds, Object element) {
		if(element instanceof BasicEntry) {
			BasicEntry bE = (BasicEntry)element;
			int transformerCount = bE.numTransformers();
			for(int i = 0; i < transformerCount; i++) {
				boolean matchFound = false;
				for(Transformer dsTransformer: ds.getTransformers()) {
					if(dsTransformer.isEquivalentTo((Transformer)bE.getTransformer(i))) {
						//Replace the element's transformer with the one already stored within the dataset.
						bE.getTransformers().set(i, dsTransformer);
						matchFound = true;
						break;
					}
				}	
				if(!matchFound) {
					ds.addTransformer(bE.getTransformer(i));
				}
			}

			int outputTransformerCount = bE.numOutputTransformers();
			for(int i = 0; i < outputTransformerCount; i++) {
				boolean matchFound = false;
				for(Transformer dsTransformer: ds.getTransformers()) {
					if(dsTransformer.isEquivalentTo((Transformer)bE.getOutputTransformer(i))) {
						//Replace the element's output transformer with the one already stored within the dataset.
						bE.getOutputTransformers().set(i, dsTransformer);
						matchFound = true;
						break;
					}
				}	
				if(!matchFound) {
					ds.addTransformer(bE.getOutputTransformer(i));
				}
			}
		}else if(element instanceof ExternalDerivedEntry) {
			ExternalDerivedEntry extDE = (ExternalDerivedEntry)element;
			int transformerCount = extDE.numTransformers();
			for(int i = 0; i < transformerCount; i++) {
				boolean matchFound = false;
				for(Transformer dsTransformer: ds.getTransformers()) {
					if(dsTransformer.isEquivalentTo((Transformer)extDE.getTransformer(i))) {
						//Replace the element's transformer with the one already stored within the dataset.
						extDE.getTransformers().set(i, dsTransformer);
						matchFound = true;
						break;
					}
				}	
				if(!matchFound) {
					ds.addTransformer(extDE.getTransformer(i));
				}
			}

		}
	}

	private static void addExternalTransformerToDataSet(DataSet ds, Object element) {
		if(element instanceof ExternalDerivedEntry) {
			ExternalDerivedEntry extDE = (ExternalDerivedEntry)element;
			ds.addTransformer(extDE.getExternalTransformer());
		}
	}
	
	/**
	 * Display the entry configuration dialog for entry and
	 * add a refresh listener to the <code>DocumentPanel</code>
	 * to ensure that it is refreshed when the dialog is dismissed.
	 * 
	 * @param entry the entry to configure
	 * @param isDEL flag to indicate if the entry belongs to the DEL
	 * @param canEdit flag to indicate if the entry can be edited
	 * @param docPanel the document panel to which this entry belongs
	 * @param frame the main frame of the application (to act as parent to the dialog)
	 */
	public static void showEntryDialog(Entry entry, boolean isDEL, boolean canEdit,
										DocumentPanel docPanel, MainFrame frame) {
		
		JDialog editDialog = null;
		
		if (entry instanceof TextEntry) {
			editDialog = new TextEditDialog(frame, (TextEntry)entry, isDEL, canEdit);
			((AbstractEditDialog)editDialog).addOKListener(new RefreshListener(docPanel, entry));
			editDialog.setVisible(true);
		} else if (entry instanceof OptionEntry) {
			editDialog = new OptionEditDialog(frame, (OptionEntry)entry, isDEL, canEdit);
			((AbstractEditDialog)editDialog).addOKListener(new RefreshListener(docPanel, entry));
			editDialog.setVisible(true);
		} else if (entry instanceof IntegerEntry) {
			editDialog = new IntegerEditDialog(frame, (IntegerEntry)entry, isDEL, canEdit);
			((AbstractEditDialog)editDialog).addOKListener(new RefreshListener(docPanel, entry));
			editDialog.setVisible(true);
		} else if (entry instanceof NumericEntry) {
			editDialog = new NumericEditDialog(frame, (NumericEntry)entry, isDEL, canEdit);
			((AbstractEditDialog)editDialog).addOKListener(new RefreshListener(docPanel, entry));
			editDialog.setVisible(true);
		} else if (entry instanceof BooleanEntry) {
			editDialog = new BooleanEditDialog(frame, (BooleanEntry)entry, isDEL, canEdit);
			((AbstractEditDialog)editDialog).addOKListener(new RefreshListener(docPanel, entry));
			editDialog.setVisible(true);
		} else if (entry instanceof LongTextEntry) {
			editDialog = new LongTextEditDialog(frame, (LongTextEntry)entry, isDEL, canEdit);
			((AbstractEditDialog)editDialog).addOKListener(new RefreshListener(docPanel, entry));
			editDialog.setVisible(true);
		} else if (entry instanceof NarrativeEntry) {
			editDialog = new NarrativeEditDialog(frame, (NarrativeEntry)entry, isDEL, canEdit);
			((AbstractEditDialog)editDialog).addOKListener(new RefreshListener(docPanel, entry));
			editDialog.setVisible(true);
		} else if (entry instanceof DateEntry) {
			 editDialog = new DateEditDialog(frame, (DateEntry)entry, isDEL, canEdit);
				((AbstractEditDialog)editDialog).addOKListener(new RefreshListener(docPanel, entry));
			 editDialog.setVisible(true);
		} else if (entry instanceof CompositeEntry) {
			editDialog = new CompositeEditDialog(frame, entry, isDEL, canEdit);
			((AbstractEditDialog)editDialog).addOKListener(new RefreshListener(docPanel, entry));
			editDialog.setVisible(true);
		} else if (entry instanceof DerivedEntry) {
			editDialog = new DerivedEditDialog(frame, (DerivedEntry)entry, isDEL, canEdit);
			((AbstractEditDialog)editDialog).addOKListener(new RefreshListener(docPanel, entry));
			editDialog.setVisible(true);
		} else if (entry instanceof ExternalDerivedEntry) {
			editDialog = new ExternalDerivedEditDialog(frame, (ExternalDerivedEntry)entry, isDEL, canEdit);
			((AbstractEditDialog)editDialog).addOKListener(new RefreshListener(docPanel, entry));
			editDialog.setVisible(true);
		}
	}

	/**
	 * Find the section of the document to which this entry belongs
	 * @param parentDoc the document to which this entry belongs
	 * @param entry the entry to search for
	 * @return the section index to which the entry belongs
	 */
	public static int getSectionForEntry(Document parentDoc, Entry entry) {
		ArrayList<Section> docSections = new ArrayList(((Document)(parentDoc)).getSections());
		
		int sectionIndex = -1;
		
		for (int i=0; i<docSections.size(); i++) {
			if (entry.getSection() == docSections.get(i)) {
				sectionIndex = i;
			}
		}
		
		return sectionIndex;
	}
	
	/**
	 * Constrain the size of the component to the given dimension
	 * Sets preferred, maximum and minimum size to this size
	 * @param comp the component to size
	 * @param size the size to set the the component to
	 */
	public static void sizeComponent(JComponent comp, Dimension size) {
		comp.setPreferredSize(size);
		comp.setMinimumSize(size);
		comp.setMaximumSize(size);
	}
	
    /**
     * Build the header panel containing the name of the current dataset
     *
     * @return the configured panel containing hte name of the dataset
     */
    public static JPanel buildDsHeaderPanel() {
        JPanel comboPanel = new JPanel();
        comboPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        comboPanel.add(new JLabel(PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.ui.selectdataset")));

        StudyDataSet activeDs = DatasetController.getInstance().getActiveDs();
        if (activeDs != null) {
            comboPanel.add(new JLabel(activeDs.getDs().getName()));
        }
        return comboPanel;
    }
    
    public static void createReviewAndApproveStatuses(Factory factory, Document document){
        Status incomplete = factory.createStatus(Status.DOC_STATUS_INCOMPLETE, "Incomplete", 0);
        Status pending = factory.createStatus(Status.DOC_STATUS_PENDING, "Pending Approval", 1);
        Status rejected = factory.createStatus(Status.DOC_STATUS_REJECTED, "Rejected", 2);
        Status approved = factory.createStatus(Status.DOC_STATUS_APPROVED, "Approved", 3);
        Status complete = factory.createStatus(Status.DOC_STATUS_COMPLETE, "Complete", 4);
        
        incomplete.addStatusTransition(pending);
        incomplete.addStatusTransition(complete);
        complete.addStatusTransition(incomplete);
        complete.addStatusTransition(pending);
        pending.addStatusTransition(incomplete);
        pending.addStatusTransition(rejected);
        pending.addStatusTransition(approved);
        rejected.addStatusTransition(pending);
        approved.addStatusTransition(pending);
        
        //remove all the old ones
        for (int i=document.numStatus()-1; i>=0; i--) {
            document.removeStatus(i);
        }
        
        //and all the new ones
        document.addStatus(incomplete);
        document.addStatus(pending);
        document.addStatus(rejected);
        document.addStatus(approved);
        document.addStatus(complete);

    }
    
    public static void createNoReviewAndApproveStatuses(Factory factory, Document document){
        Status incomplete = factory.createStatus(Status.DOC_STATUS_INCOMPLETE, "Incomplete", 0);
        Status complete = factory.createStatus(Status.DOC_STATUS_COMPLETE, "Complete", 1);
        Status controlled = factory.createStatus(Status.DOC_STATUS_CONTROLLED, "Controlled", 2);
        
        incomplete.addStatusTransition(complete);
        complete.addStatusTransition(incomplete);
        complete.addStatusTransition(controlled);

        //remove all the old ones
        for (int i=document.numStatus()-1; i>=0; i--) {
            document.removeStatus(i);
        }
        
        document.addStatus(incomplete);
        document.addStatus(complete);
        document.addStatus(controlled);
        
    }
    
    public static String getFormattedNow() {
		DateFormat formatter = new SimpleDateFormat("HH:mm");
		return formatter.format(Calendar.getInstance().getTime());
    }
    
    public static String getFormattedNowWithSeconds() {
		DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		return formatter.format(Calendar.getInstance().getTime());
    }


}
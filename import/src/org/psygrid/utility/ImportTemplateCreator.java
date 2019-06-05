package org.psygrid.utility;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.dataimport.jaxb.doc.Basicentrytype;
import org.psygrid.dataimport.jaxb.doc.Compositeentrytype;
import org.psygrid.dataimport.jaxb.doc.Documenttype;
import org.psygrid.dataimport.jaxb.doc.Importdoc;
import org.psygrid.dataimport.jaxb.doc.Occurrencetype;
import org.psygrid.dataimport.jaxb.doc.Sectionoccurrencetype;
import org.psygrid.dataimport.jaxb.doc.Sectiontype;
import org.psygrid.dataimport.jaxb.imp.Consentformgroupstype;
import org.psygrid.dataimport.jaxb.imp.Consentformgrouptype;
import org.psygrid.dataimport.jaxb.imp.Consentformtype;
import org.psygrid.dataimport.jaxb.imp.Documentstype;
import org.psygrid.dataimport.jaxb.imp.Import;
import org.psygrid.dataimport.jaxb.imp.Primaryconsentformtype;

public class ImportTemplateCreator {

	public static org.psygrid.dataimport.jaxb.imp.Import createImportTemplateFromDataset(DataSet ds){
	
		Import imp = new Import();
		imp.setProject(ds.getProjectCode());
				
		//Address consent forms.
		addConsentFormGroups(imp, ds);
				
		List<Consentformgrouptype> consentFormGroups = imp.getConsentformgroups().getConsentformgroup();
		populateConsentFormGroups(consentFormGroups, ds);
		
		//Address documents.
		imp.setDocuments(new Documentstype());
		addDocumentsNames(imp.getDocuments(), ds);
		

		return imp;
	}
	
	/**
	 * Creates a document template file based on the dataset contents.
	 * @param docFileName - the name of the template .xml file that controls the import for this document.
	 * The filename must be of the format 'doc_name.xml' where doc_name is the name of the document in the dataset.
	 * @param ds
	 * @return
	 */
	public static org.psygrid.dataimport.jaxb.doc.Importdoc createDocumentTemplate(String docFileName, DataSet ds){
		
		int fileSuffixLocation = docFileName.indexOf(".xml");
		String documentName = docFileName.substring(0, fileSuffixLocation);
		
		int numDSDocs = ds.numDocuments();
		
		int matchingDocIndex = -1;
		for(int i = 0; i < numDSDocs; i++){
			
			Document dsDoc = ds.getDocument(i);
			if(dsDoc.getName().equals(documentName)){
				matchingDocIndex = i;
				break;
			}
		}
		
		if(matchingDocIndex == -1){
			throw new RuntimeException("In createDocumentTemplate - could not find document in dataset with the name: '" + documentName + "'.");
		}
		
		Importdoc docWrapper = new Importdoc();
		Documenttype importDoc = new Documenttype();
		docWrapper.getDocument().add(importDoc);
		
		Document dsDoc = ds.getDocument(matchingDocIndex);
		
		importDoc.setDescription(dsDoc.getDisplayText());
		importDoc.setIndex(BigInteger.valueOf(matchingDocIndex));
		
		int numOccurrences = dsDoc.numOccurrences();
		
		for(int i = 0; i < numOccurrences; i++){
			
			DocumentOccurrence docOcc = dsDoc.getOccurrence(i);
			
			/*
			 * &lt;complexType name="occurrencetype">
			 *   &lt;complexContent>
			 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
			 *       &lt;sequence>
			 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
			 *         &lt;element name="emptydoc" type="{}emptytype" minOccurs="0"/>
			 *         &lt;element name="interviewer" type="{}mappingtype" minOccurs="0"/>
			 *         &lt;element name="interviewdate" type="{}mappingtype" minOccurs="0"/>
			 *         &lt;element name="section" type="{}sectiontype" maxOccurs="unbounded"/>
			 *       &lt;/sequence>
			 *       &lt;attribute name="index" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
			 *     &lt;/restriction>
			 *   &lt;/complexContent>
			 * &lt;/complexType>
			 */
			
			Occurrencetype occurrence = new Occurrencetype();
			System.out.println("Section occurrence display text is: " + docOcc.getDisplayText());
			System.out.println("Section occurrence name is: " + docOcc.getName());
			System.out.println("Section occurrence combined display text is: " + docOcc.getCombinedDisplayText());
			occurrence.setDescription(docOcc.getDisplayText()); //description
																//don't set the empty doc
																//don't set the interviewer (why would this be set here?)
																//don't set the interview date (why would this be set here?)
			
			occurrence.setIndex(BigInteger.valueOf(i));
			importDoc.getOccurrence().add(occurrence);
		}
		
		//Set the sections in each occurrence.
		for(Occurrencetype ot : importDoc.getOccurrence()){
			int numSections = dsDoc.numSections();
			for(int secNum = 0; secNum < numSections; secNum++){
				Section section = dsDoc.getSection(secNum);
				
				Sectiontype importSection = new Sectiontype();
				importSection.setIndex(BigInteger.valueOf(secNum));
				importSection.setDescription(section.getDisplayText());
				
				ot.getSection().add(importSection);
				
			}
		}
		
		
		for(Occurrencetype oct : importDoc.getOccurrence()){
			
			List<Sectiontype> sections = oct.getSection();
			
			for(Sectiontype section : sections){
								
				Section dsSection = dsDoc.getSection(section.getIndex().intValue());
				
				int numDSSectionOccurrences = dsSection.numOccurrences();
				
				Sectionoccurrencetype sectionOccurrenceTemplate = null;
				
				for(int secOccCount = 0; secOccCount < numDSSectionOccurrences; secOccCount++){
					
					SectionOccurrence dsSecOcc = dsSection.getOccurrence(secOccCount);
					Sectionoccurrencetype impSecOcc = new Sectionoccurrencetype();
					impSecOcc.setDescription(dsSecOcc.getCombinedDisplayText()); 
					impSecOcc.setIndex(BigInteger.valueOf(secOccCount));
					
					section.getSectionoccurrence().add(impSecOcc);
					
					//Each section occurrence type then contains the entries - so only one template needs to be created for each
					//section, and this can be applied over and again.
					
					//Section occurrence.
					
					//We will now have to find the entries that are in the section, and add these to the impSecOcc.
					List<Entry> sectionEntries = ImportTemplateCreator.getEntriesForSection(section.getIndex().intValue(), dsDoc);
					
					ImportTemplateCreator.addEntriesForTemplate(sectionEntries, impSecOcc, dsDoc);
				}
			}	
		}
		
		
		
		return docWrapper;
	}
	
	
	/**
	 * Add the entries to the importOcc
	 * @param entriesToAdd
	 * @param importOcc
	 * @param dsDoc
	 */
	private static void addEntriesForTemplate(List<Entry> entriesToAdd, Sectionoccurrencetype importOcc, Document dsDoc){
		
		for(Entry entryToAdd : entriesToAdd){
			
			if (entryToAdd instanceof BasicEntry){
				Basicentrytype impBE = new Basicentrytype();
				
				impBE.setDescription(entryToAdd.getDisplayText());
				
				int entryIndex = dsDoc.getIndexOfEntry(entryToAdd);
				impBE.setIndex(BigInteger.valueOf(entryIndex));
				
				importOcc.getBasicentry().add(impBE);
				
							
			}else if(entryToAdd instanceof CompositeEntry){
				
				Compositeentrytype impCE = new Compositeentrytype();
				impCE.setDescription(entryToAdd.getDisplayText());
				
				int entryIndex = dsDoc.getIndexOfEntry(entryToAdd);
				impCE.setIndex(BigInteger.valueOf(entryIndex));
				
				List<Basicentrytype> impBEs = impCE.getBasicentry();
				
				CompositeEntry compositeEntry = (CompositeEntry)entryToAdd;
				
				importOcc.getCompositeentry().add(impCE);
				
				int numSubEntries = compositeEntry.numEntries();
				
				for(int i = 0; i < numSubEntries; i++){
					
					BasicEntry subEntry = compositeEntry.getEntry(i);
					
					Basicentrytype impSubEntry = new Basicentrytype();
					
					impSubEntry.setIndex(BigInteger.valueOf(i));
					impSubEntry.setDescription(subEntry.getDisplayText());
					
					impBEs.add(impSubEntry);
				}
			}
		}
		
		
		
		
	}
	
	/**
	 * Get the a list of entries contained by the section defined at the given index.
	 * Entries are returned in ascending index order.
	 * @param sectionIndex
	 * @param dsDoc
	 * @return
	 */
	private static List<Entry> getEntriesForSection(int sectionIndex, Document dsDoc){
		
		List<Entry> returnList = new ArrayList<Entry>();
		
		Section section = dsDoc.getSection(sectionIndex);
		
		int numEntries = dsDoc.numEntries();
		
		for(int entryCount = 0; entryCount < numEntries; entryCount++){
			
			Entry entry = dsDoc.getEntry(entryCount);
			
			if(entry.getSection() == section){
				returnList.add(entry);
			}
		}
		
		return returnList;
	}
	

	
		
		
	/**
	 * Creates document import templates for each of the documents in the dataset.
	 *
	 */
	protected static void addDocumentsNames(Documentstype docs, DataSet ds){
		
		final String xmlSuffix = ".xml";
		
		List<String> docsList = docs.getDocument();
		
		for(int i = 0; i < ds.numDocuments(); i++){
			Document dsDoc = ds.getDocument(i);
			docsList.add(dsDoc.getName() + xmlSuffix);
		}
		
	}
	
	/**
	 * Populates the primary and associated consent form for the consent form groups in the list passed in.
	 * Relies on the consent form groups having been assigned an index that associates them with the index value
	 * of the consent form group in the dataset.
	 * @param consentFormGroups
	 * @param ds
	 */
	protected static void populateConsentFormGroups(List<Consentformgrouptype> consentFormGroups, DataSet ds){
		
		for(Consentformgrouptype cfg: consentFormGroups){
			
			//First make sure that it has an index, and that the index translates to a cfg in the DataSet.
			//If not, throw a runtime exception.
			boolean setUpCriteriaMet = true;
			if(cfg.getIndex() == null){
				setUpCriteriaMet = false;
			}else{
				
				if(cfg.getIndex().intValue() < 0 || cfg.getIndex().intValue() >= ds.numAllConsentFormGroups()){
					setUpCriteriaMet = false;
				}
			}
			
			if(!setUpCriteriaMet){
				throw new RuntimeException("ConsentFormGroup " + cfg.getDescription() + " not configured properly.");
			}
			
			ConsentFormGroup dsCFG = ds.getAllConsentFormGroup(cfg.getIndex().intValue());
			
			populateConsentFormGroup(cfg, dsCFG);
		}
		
		
	}
	
	/**
	 * Populates the consent form group passed in, according to the structure of the ds consent  form group.
	 * @param importCFG
	 * @param dsCFG
	 */
	protected static void populateConsentFormGroup(Consentformgrouptype importCFG, ConsentFormGroup dsCFG){
		
		int numDSConsentForms = dsCFG.numConsentForms();
		
		for(int i = 0; i < numDSConsentForms; i++){
			
			PrimaryConsentForm dsConsentForm = dsCFG.getConsentForm(i);
			
			//Create the import consent form, and populate its properties.
			Primaryconsentformtype importConsentForm = new Primaryconsentformtype();
			importConsentForm.setDescription(dsConsentForm.getQuestion());
			importConsentForm.setIndex(BigInteger.valueOf(i));
			
			importCFG.getPrimaryconsentform().add(importConsentForm);
			
			
			//Deal with any associated consent forms now.
			for(int j = 0; j < dsConsentForm.numAssociatedConsentForms(); j++){
				
				AssociatedConsentForm dsAssociatedCF = dsConsentForm.getAssociatedConsentForm(j);
				
				//Create the import associated consent form, and populate its properties.
				Consentformtype impAssociatedCF = new Consentformtype();
				impAssociatedCF.setDescription(dsAssociatedCF.getQuestion());
				impAssociatedCF.setIndex(BigInteger.valueOf(j));
				
				importConsentForm.getAssociatedconsentform().add(impAssociatedCF);
			}
		}
		
	}
	
	/**
	 * Populates the consent form groups in the Import object passed in
	 * Will throw a runtime exception if the the 
	 * @param imp
	 * @param ds
	 */
	protected static void addConsentFormGroups(Import imp, DataSet ds){
		Consentformgroupstype cfgstype = new Consentformgroupstype();
		cfgstype.setDescription("Holds all consent form groups");
		imp.setConsentformgroups(cfgstype);
		
		int numConsentFormGroups = ds.numAllConsentFormGroups();
		
		for(int i = 0; i < numConsentFormGroups; i++){
			ConsentFormGroup cfg = ds.getAllConsentFormGroup(i);
			
			Consentformgrouptype cfgtype = new Consentformgrouptype();
			cfgtype.setDescription(cfg.getDescription());
			cfgtype.setIndex(BigInteger.valueOf(i));
			
			cfgstype.getConsentformgroup().add(cfgtype);			
		}
	}
	
	
}

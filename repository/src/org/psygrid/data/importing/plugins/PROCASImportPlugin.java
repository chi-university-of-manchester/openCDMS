/*
Copyright (c) 2006-2009, The University of Manchester, UK.

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

package org.psygrid.data.importing.plugins;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.common.identifier.IdentifierHelper;
import org.psygrid.data.model.IDateValue;
import org.psygrid.data.model.IIntegerValue;
import org.psygrid.data.model.ILongTextValue;
import org.psygrid.data.model.INumericValue;
import org.psygrid.data.model.IOptionValue;
import org.psygrid.data.model.ITextValue;
import org.psygrid.data.model.IValue;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.repository.client.RepositoryClient;
import org.psygrid.data.utils.wrappers.AAQCWrapper;
import org.psygrid.esl.model.IAddress;
import org.psygrid.esl.model.IFactory;
import org.psygrid.esl.model.IProject;
import org.psygrid.esl.model.ISubject;
import org.psygrid.esl.model.hibernate.HibernateFactory;
import org.psygrid.esl.services.client.EslClient;

import au.com.bytecode.opencsv.CSVReader;

/**
 * PROCAS ImportPlugin.
 * 
 * NB - this code was originally an attempt to implement a generic import class.
 * This was abandoned as the requirement for PROCAS meant that imports could not
 * be implemented in a generic way.
 * 
 * Imports data from CSV files into Document Occurrences and the Electronic
 * Screening Log (Participant Register).
 * 
 * This class makes a number of assumptions about the target dataset and the
 * imported data:
 * 
 * 1. All imported data is in CSV file format.
 * 
 * 2. Each CSV file contains the data for a single document in the dataset
 * 
 * 3. The first line of each CSV file contains labels matching the names of a
 * fields in the dataset document.
 * 
 * 4. The CSV file may contain a subset of the fields defined in the dataset
 * document.
 * 
 * 5. The data for option fields matches the value of one of the options in the
 * corresponding field in the dataset document
 * 
 * 6. Importing a CSV file for the first document occurrence in the study
 * creates dataset records and ESL entries
 * 
 * 7. Each CSV file contains an external id column which is stored in the
 * external id field when a record is first created.
 * 
 * The external id value is used to look up the corresponding records for
 * subsequent imports.
 * 
 * 8. New records are added to the first site in their group.
 * 
 * @author Terry Child
 * 
 */
public class PROCASImportPlugin implements ImportPlugin {

	private static final Log logger = LogFactory.getLog(PROCASImportPlugin.class);

	private static String REGISTRATION = "Registration";
	private static String CONSENTED_TO_DNA = "Consented to DNA";
	private static String QUESTIONNAIRE = "Questionnaire";

	private static String INITIAL_VAS_RESULTS = "Intial Risk - VAS Results";
	private static String INITIAL_CUMULUS_RESULTS = "Intial Risk - Cumulus Results";
	private static String INITIAL_QUANTRA_RESULTS = "Intial Risk - Quantra Results";
	private static String INITIAL_STEPWEDGE_RESULTS = "Intial Risk - Step Wedge Results";
	private static String INITIAL_VOLPARA_RESULTS = "Intial Risk - Volpara Results";
	
	private static String FINAL_SCREENING_APPOINTMENT = "Final Risk - Screening Appointment";
	private static String FINAL_DNA_RESULTS = "Final Risk - DNA Results";

	private static String[] SOURCE_TYPES = {REGISTRATION,CONSENTED_TO_DNA,QUESTIONNAIRE,INITIAL_VAS_RESULTS,INITIAL_CUMULUS_RESULTS,
		INITIAL_QUANTRA_RESULTS,INITIAL_STEPWEDGE_RESULTS,INITIAL_VOLPARA_RESULTS,
		FINAL_SCREENING_APPOINTMENT,FINAL_DNA_RESULTS};

	private static String[][] COLUMN_HEADINGS = {
		{REGISTRATION,"SxNumber,NhsNumber,DateOfBirth,StudyEntryDate,DateOfFirstOfferedAppointment,DateOfMammogram,FullName,AddressOneLine,GpFullName,PracticeName,PracticeAddressLine1,PracticeAddressLine2,PracticeAddressLine3,PracticeAddressLine4,PracticePostcode,Location,Van no,Consented to DNA,To receive risk letter,Consent complete,ConsentComments,ConsentIssues"},
		{CONSENTED_TO_DNA,"NhsNumber"},
		{INITIAL_VOLPARA_RESULTS,"VolparaVersion,PatientID,BreastSide,MammoView,BreastVolumeCm3,HintVolumeCm3,VolumetricBreastDensity"},
		{INITIAL_VAS_RESULTS,"NHS Number,D.O.B.,RCC,RMLO,LCC,LMLO,Initials,Warnings"},
		{INITIAL_QUANTRA_RESULTS,"PatientName,PatientId,StudyDate,Quantra Date,Right Glandular Volume,Right Breast Volume,Right Breast Density,Right Confidence,Left Glandular Volume,Left Breast Volume,Left Breast Density,Left Confidence,Comments"},
		{FINAL_SCREENING_APPOINTMENT,"NhsNumber,DateOfSecondOfferedAppointment,SecondVanNo,SecondLocation"},
		{FINAL_DNA_RESULTS,"96 well,384,NHS Number,Patient no Customer ID,rs614367,rs614367 score,rs704010,rs704010 score,rs713588,rs713588 score,rs889312,rs889312 score,rs909116,rs909116 score,rs1011970,rs1011970 score,rs1562430,rs1562430 score,rs2981579,rs2981579 score,rs3757318,rs3757318 score,rs3803662,rs3803662 score,rs4973768,rs4973768 score,rs1156287,rs1156287 score,rs8009944,rs8009944 score,rs9790879,rs9790879 score,rs10941679,rs10941679 score,rs10995190,rs10995190 score,rs11249433,rs11249433 score,rs13387042,rs13387042 score,rs10931936,rs10931936 score,SumScore"},
	};
	
	
	
	/**
	 * The indexes of the documents within the dataset.
	 */
	private static final int REGISTRATION_DOC_INDEX = 0;
	private static final int QUESTIONNAIRE_DOC_INDEX = 1;
	private static final int SCREENING_APPOINTMENT_DOC_INDEX = 3;
	private static final int CANCER_DIAGNOSIS_DOC_INDEX = 4;
	private static final int DNA_RESULTS_DOC_INDEX = 14;
	
	
	/**
	 * The indexes of the document occurrences.
	 * These DO NOT necessarily correspond to their order in the study schedule.
	 */
	private static final int REGISTRATION_DOC_OCC_INDEX = 0;
	private static final int QUESTIONNAIRE_DOC_OCC_INDEX = 0;
	private static final int CANCER_DIAGNOSIS_DOC_OCC_INDEX = 0;
	private static final int INITIAL_VAS_RESULTS_DOC_OCC_INDEX = 1;
	private static final int INITIAL_CUMULUS_RESULTS_DOC_OCC_INDEX = 0;
	private static final int INITIAL_STEPWEDGE_RESULTS_DOC_OCC_INDEX = 0;
	private static final int INITIAL_QUANTRA_RESULTS_DOC_OCC_INDEX = 0;
	private static final int INITIAL_VOLPARA_RESULTS_DOC_OCC_INDEX = 0;
	private static final int FINAL_SCREENING_APPOINTMENT_DOC_OCC_INDEX = 0;
	private static final int FINAL_DNA_RESULTS_DOC_OCC_INDEX = 0;
	
	
	

	private static final int TABLE_MAX_ROW = 100;
	private static final int NO_COLUMN = -1;

	private static Pattern monthYearPattern = Pattern
			.compile(" */?([0-9]{1,2})/([0-9]{4})");
	private static Pattern yearPattern = Pattern.compile(" */? */?([0-9]{4})");
	
	// Don't use static dateFormatters - they are not thread safe.
	private final DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
	private final DateFormat dateFormatter2 = new SimpleDateFormat("yyyyMMdd");

	private static String UNKNOWN = "Unknown";

	//
	
	private static final class MSG {
		static final String EXTERNALID_MISSING = "Row number %d of the imported data does not have a valid %s. This is needed as an external identifier.";
		static final String READING_ROW = "Reading row number %d of the imported data with %s='%s'.";
		static final String INVALID_ROW = "Ignoring row number %d of the imported data with %s='%s' - it contains invalid data.";
		static final String INVALID_VALUE = "Value '%s' of field '%s' in invalid.";
		static final String RECORD_EXISTS = "Ignoring row number %d of the imported data with %s='%s'. It already has an existing record with identifier '%s'.";
		static final String RECORD_MISSING = "Ignoring row number %d of the imported data with %s='%s'. Cannot find an existing record with this external identifier.";
		static final String RECORD_SAVED = "Record '%s' saved";
		static final String RECORD_VALID = "Record '%s' saved";
		static final String RECORD_NO_CHANGE = "No changes for record '%s'.";
		static final String DOCUMENT_INSTANCE_EXISTS = "Ignoring row number %d of the imported data with %s='%s'. It already has an existing document within the record with identifier '%s'.";
		static final String DOCUMENT_INSTANCE_MISSING = "Ignoring row number %d of the imported data with %s='%s'. No existing document within the record with identifier '%s'.";
		static final String NONMATCHING_VAS_RESULTS = "Only one set of VAS results at row %d for %s='%s'.";
		static final String VAS_READINGS_EXIST = "Ignoring row number %d of the imported data with %s='%s'. It already has existing VAS values within the record with identifier '%s'.";
		static final String CUMULUS_READINGS_EXIST = "Ignoring row number %d of the imported data with %s='%s'. It already has existing Cumulus values within the record with identifier '%s'.";
		static final String STEPWEDGE_READINGS_EXIST = "Ignoring row number %d of the imported data with %s='%s'. It already has existing Step Wedge values within the record with identifier '%s'.";
		static final String VOLPARA_READINGS_EXIST = "Ignoring row number %d of the imported data with %s='%s'. It already has existing values for this Volpara version within the record with identifier '%s'.";
		static final String COLUMN_MISSING = "The imported data file is missing a column named: '%s'.";
	}

	
	private DataSet dataset = null;
	private String sourceType = null;
	private File csvFile = null;
	private String user = null;

	// Set to true by any code that adds or changes a value in the current record.
	public boolean saveCurrentRecord  = false;

	/**
	 * Attribute authority query client
	 */
	private AAQCWrapper aaqc;

	// The last time the saml was updated
	private Date samlTime = null;

	private String saml = null;
	private RepositoryClient repClient = null;
	private EslClient eslClient = null;
	private IFactory eslFactory = null;
	private IProject eslProject = null;

	private List<StandardCode> standardCodes = null;
	private StandardCode defaultStdCode = null;
	private String[] columnNames = null;
	private List<String[]> data = null;
	private Map<String, Integer> colMap = null;
	//private List<String> log = new ArrayList<String>();
	
	PrintStream log = null;

	// Map entry names to IEntry objects
	Map<String, Entry> entryMap = null;

	/*
	 * Map the name of an option entry to a map containing alternatives that should map to a given
	 * value e.g. for the breast_cancer_1 field, option "aunt" should be mapped to option "maternal aunt" .
	 */
	private static Map<String,Map<String,String>> optionMaps = null;
	
	public String[] getImportTypes() {
		return SOURCE_TYPES;
	}
	
	public void run(String projectCode, String sourceType, String filePath,String user, AAQCWrapper aaqc,PrintStream log) throws Exception {

		this.sourceType=sourceType;
		this.csvFile = new File(filePath);
		this.user=user;
		this.aaqc=aaqc;
		this.saml = aaqc.getSAMLAssertion(user);
		this.samlTime=new Date();
		this.log = log;
		repClient = new RepositoryClient();
		eslClient = new EslClient();
		eslFactory = new HibernateFactory();
		entryMap = new HashMap<String, Entry>();
		optionMaps = new HashMap<String,Map<String,String>>();
		
		
		logger.info("Importing into dataset:"+projectCode+" source data type:"+sourceType);
		
		buildOptionMaps();

		try {
			DataSet dataset = repClient.getDataSetSummary(projectCode, new Date(0), saml);
			this.dataset = repClient.getDataSet(dataset.getId(), saml);		
			doImport();
		} catch (Exception e) {
			log.println("Problem importing PROCAS data - please contact OpenCDMS support:"+e.getMessage());
			logger.error("Problem importing PROCAS data", e);
			throw e;
		}
	}

	private void doImport() throws Exception {

		getStandardCodes();

		String projectCode = dataset.getProjectCode();

		eslProject = eslClient.retrieveProjectByCode(projectCode, saml);

		readCSVFile(csvFile);
		
		if(!checkColumnNames(sourceType)) return;
		
		// The VAS results csv has multiple rows per record so has to be handled as a special case
		if (sourceType.equals(INITIAL_VAS_RESULTS)) {
			importVASData(INITIAL_VAS_RESULTS_DOC_OCC_INDEX);
		}			
		// The Cumulus results csv data has multiple rows per record so has to be handled as a special case
		else if (sourceType.equals(INITIAL_CUMULUS_RESULTS)) {
			importCumulusData(INITIAL_CUMULUS_RESULTS_DOC_OCC_INDEX);
		}			
		// The Step Wedge results csv data has multiple rows per record so has to be handled as a special case
		else if (sourceType.equals(INITIAL_STEPWEDGE_RESULTS)) {
			importStepWedgeData(INITIAL_STEPWEDGE_RESULTS_DOC_OCC_INDEX);
		}			
		// The Volpara results csv data has multiple rows per record so has to be handled as a special case
		else if (sourceType.equals(INITIAL_VOLPARA_RESULTS)) {
			importVolparaData(INITIAL_VOLPARA_RESULTS_DOC_OCC_INDEX);
		}			
		else {
			for (int i = 0; i < data.size(); i++) {
				refreshSAMLIfNeeded();
				String[] row = data.get(i);
				if (sourceType.equals(REGISTRATION)) {
					importRegistrationData(i,row);
				} 
				else if (sourceType.equals(CONSENTED_TO_DNA)) {
					importConsentedToDNA(i,row);
				} 
				else if (sourceType.equals(QUESTIONNAIRE)) {
					importQuestionnaireData(i,row);
				} 
				else if (sourceType.equals(INITIAL_QUANTRA_RESULTS)) {
					importQuantraData(INITIAL_QUANTRA_RESULTS_DOC_OCC_INDEX,i,row);
				}			
				else if (sourceType.equals(FINAL_SCREENING_APPOINTMENT)) {
					importFinalScreeningAppointment(FINAL_SCREENING_APPOINTMENT_DOC_OCC_INDEX,i,row);
				}			
				else if (sourceType.equals(FINAL_DNA_RESULTS)) {
					importFinalDNAResults(FINAL_DNA_RESULTS_DOC_OCC_INDEX,i,row);
				}			
			}
		}
	}
	
	// Refresh the SAML every minute if needed
	private void refreshSAMLIfNeeded() throws Exception{
		Date now = new Date();
		if((now.getTime()-samlTime.getTime())>60*1000){
			this.saml = aaqc.getSAMLAssertion(user);
			samlTime=now;
		}
	}

	private void importRegistrationData(int rowNum, String[] row) throws Exception {
		
		int fileRow = rowNum+1;
		String externalIDCol="NhsNumber";
		
		String externalID = row[colMap(externalIDCol)];
		
		if (externalID == null || externalID.length() == 0) {
			log(MSG.EXTERNALID_MISSING, fileRow, externalIDCol);
			return;
		}
		
		log(MSG.READING_ROW, fileRow, externalIDCol, externalID);
	
		Record rec = repClient.getRecordByExternalID(dataset, externalID, saml);
	
		if (rec != null) {
				log(MSG.RECORD_EXISTS, fileRow, externalIDCol,externalID, rec.getIdentifier().getIdentifier());
				return;
		}
		String site = row[colMap("Location")].trim();

		rec = createRecord(site);
		rec.setExternalIdentifier(externalID);

		Document doc = dataset.getDocument(REGISTRATION_DOC_INDEX);
		buildEntryMap(doc);
		DocumentOccurrence occurrence = doc.getOccurrence(REGISTRATION_DOC_OCC_INDEX);
		
		DocumentInstance docinst = rec.getDocumentInstance(occurrence);
		if (docinst != null) {
			log(MSG.DOCUMENT_INSTANCE_EXISTS, fileRow, externalIDCol,externalID, rec.getIdentifier().getIdentifier());
			return;
		}
		try {
			docinst = doc.generateInstance(occurrence);
			// Loop through all the entries and add a response to each one.
			// This works because the document contains only imported data.
			int numEntries = doc.numEntries();
			for(int i=0;i<numEntries;i++){
				Entry entry = doc.getEntry(i);
				String colName = entry.getName();
				addOrUpdateResponse(docinst,row,colName);
			}
		} catch (ParseException e) {
			log(MSG.INVALID_ROW, fileRow, externalIDCol, externalID);
			return;
		}
				
		setDocumentComplete(docinst);
		
		ChangeHistory change = docinst.addToHistory(user);
	    docinst.checkForChanges(change);     
		rec.addDocumentInstance(docinst);		
		
		// Set the schedule start date (the zero point for scheduling) for the record
		// using the value imported into the 'DateOfFirstOfferedAppointment' field.
		Entry dateentry = findEntryByName(occurrence.getDocument(), "DateOfFirstOfferedAppointment");
		BasicResponse res = (BasicResponse) docinst.getResponses(dateentry).get(0);
		IDateValue resval = (IDateValue) res.getValue();
		Date date = resval.getValue();
		
		// Schedule start date is not important for PROCAS - set it to DateOfFirstOfferedAppointment
		RecordData rd = rec.generateRecordData();
		rd.setScheduleStartDate(date);
		
		// Expect the StudyEntryDate column to be formated as 'dd/MM/yyyy'
		String studyEntryDateVal = row[colMap("StudyEntryDate")].trim();		
		Date studyEntryDate = dateFormatter.parse(studyEntryDateVal);
		rd.setStudyEntryDate(studyEntryDate);
		
		rec.setRecordData(rd, null);
		
		String studyNumber = rec.getIdentifier().getIdentifier();
		ISubject subject = createESLSubject(row,studyNumber);
		saveESLSubject(subject);
		repClient.saveRecord(rec, true, saml);
		log(MSG.RECORD_SAVED, rec.getIdentifier().getIdentifier());	
	}

	/**
	 * 
	 * Load records that match the given NhsNumbers and set their 'Consented to DNA' entries to 'Yes'.
	 * 
	 */
	private void importConsentedToDNA(int rowNum, String[] row) throws Exception {
		
		int fileRow = rowNum+1;
		String externalIDCol="NhsNumber";
		
		String externalID = row[colMap(externalIDCol)];
		
		if (externalID == null || externalID.length() == 0) {
			log(MSG.EXTERNALID_MISSING, fileRow, externalIDCol);
			return;
		}
		
		log(MSG.READING_ROW, fileRow, externalIDCol, externalID);
	
		Record rec = repClient.getRecordByExternalID(dataset, externalID, saml);

		if (rec == null) {
			log(MSG.RECORD_MISSING, fileRow, externalIDCol,externalID);
			return;
		}

		// Get the first and only occurrence of the registration document.
		int REGISTRATION_INDEX = 0;
		Document doc = dataset.getDocument(REGISTRATION_INDEX);
		DocumentOccurrence occurrence = doc.getOccurrence(0);
		buildEntryMap(doc);
	
		DocumentInstance docinst = rec.getDocumentInstance(occurrence);
		if (docinst == null) {
			log(MSG.DOCUMENT_INSTANCE_MISSING, fileRow, externalIDCol,externalID, rec.getIdentifier().getIdentifier());
			return;
		}
		try {
			// Create a new 'Yes' value for this option entry and add it to the existing response.
			OptionEntry entry = (OptionEntry)findEntryByName(doc, "Consented to DNA");
			IOptionValue val = (IOptionValue)entry.generateValue();
			// Need to get the option with the same name as the imported value.
			Option option = mapOption("Yes", entry);
			val.setValue(option);
			
			// Add the new value to the existing response
			List<Response> responses = docinst.getResponses(entry);
			if(responses.size()>0){
				BasicResponse existingResponse = (BasicResponse)responses.get(0);
				existingResponse.setValue(val);
			}
		} catch (Exception e) {
			log(MSG.INVALID_ROW, fileRow, externalIDCol, externalID);
			return;
		}
	
		ChangeHistory change = docinst.addToHistory(user);
	    docinst.checkForChanges(change);     

		repClient.saveRecord(rec, true, saml);
		log(MSG.RECORD_SAVED, rec.getIdentifier().getIdentifier());	
	}	

	private void importQuestionnaireData(int rowNum, String[] row) throws Exception {
		
		int fileRow = rowNum+1;
		String externalIDCol="NHS_no";
		
		String externalID = row[colMap(externalIDCol)];
		
		if (externalID == null || externalID.length() == 0) {
			log(MSG.EXTERNALID_MISSING, fileRow, externalIDCol);
			return;
		}
		
		log(MSG.READING_ROW, fileRow, externalIDCol, externalID);
	
		Record rec = repClient.getRecordByExternalID(dataset, externalID, saml);

		if (rec == null) {
			log(MSG.RECORD_MISSING, fileRow, externalIDCol,externalID);
			return;
		}

		Document doc = dataset.getDocument(QUESTIONNAIRE_DOC_INDEX);
		DocumentOccurrence occurrence = doc.getOccurrence(QUESTIONNAIRE_DOC_OCC_INDEX);
		buildEntryMap(doc);
	
		DocumentInstance docinst = rec.getDocumentInstance(occurrence);
		if (docinst != null) {
			log(MSG.DOCUMENT_INSTANCE_EXISTS, fileRow, externalIDCol,externalID, rec.getIdentifier().getIdentifier());
			return;
		}
		try {
			docinst = doc.generateInstance(occurrence);
			// Loop through all the entries and add a response to each one.
			// This works because the document contains only imported data.
			int numEntries = doc.numEntries();
			for(int i=0;i<numEntries;i++){
				Entry entry = doc.getEntry(i);
				String colName = entry.getName();
				addOrUpdateResponse(docinst,row,colName);
			}
		} catch (ParseException e) {
			log(MSG.INVALID_ROW, fileRow, externalIDCol, externalID);
			return;
		}
	
		ChangeHistory change = docinst.addToHistory(user);
	    docinst.checkForChanges(change);     
		setDocumentComplete(docinst);

	    rec.addDocumentInstance(docinst);

	    // We want to import a field named 'previously_diagnosed_with_cancer' into the Cancer Diagnosis document.
	    // Add the Cancer Diagnosis if it doesn't exist.
		doc = dataset.getDocument(CANCER_DIAGNOSIS_DOC_INDEX);
		occurrence = doc.getOccurrence(CANCER_DIAGNOSIS_DOC_OCC_INDEX);
		buildEntryMap(doc);
		docinst = rec.getDocumentInstance(occurrence);
		if (docinst == null) {
			docinst = doc.generateInstance(occurrence);
			addOrUpdateResponse(docinst,row,"previously_diagnosed_with_cancer","previously_diagnosed_with_cancer");
			change = docinst.addToHistory(user);
		    docinst.checkForChanges(change);     
			setDocumentComplete(docinst);
		    rec.addDocumentInstance(docinst);
		}
	    
	    repClient.saveRecord(rec, true, saml);
		log(MSG.RECORD_SAVED, rec.getIdentifier().getIdentifier());	
	}

	/**
	 * Import the data from a VAS csv file.
	 * 
	 * The VAS files contain up to two lines per participant - these are used to populate the VAS Results document
	 * 
	 * NB: The index of the document occurences DO Not necessarily correspond to their order in the study schedule.
	 *  
	 * @param docOccIndex the the index of the document occurrence to populate.
	 * @throws Exception
	 */
	private void importVASData(int docOccIndex) throws Exception {

		String externalIDCol="NHS Number";
		
		// There are two sets of readings for each participant in the VAS document.
		// Both sets of readings will be in a given imported CSV file.
		// The readings are distinguished by the values in the 'Initials' column.
		// This set is used to monitor whether we have already seen a set of
		// readings for this NHS number in the current import file.
		// This is used to decide whether to put the values into the first
		// or second set of readings in the document instance.
		Set<String> readingsSeen = new HashSet<String>(); 
		
		for (int i = 0; i < data.size(); i++) {

			refreshSAMLIfNeeded();

			int fileRow = i+1;
	
			String[] row = data.get(i);
			
			String externalID = row[colMap(externalIDCol)];
			
			if (externalID == null || externalID.length() == 0) {
				log(MSG.EXTERNALID_MISSING, fileRow, externalIDCol);
				continue;
			}
			
			log(MSG.READING_ROW, fileRow, externalIDCol, externalID);
			
			Record rec = repClient.getRecordByExternalID(dataset, externalID, saml);
	
			if (rec == null) {
				log(MSG.RECORD_MISSING, fileRow, externalIDCol,externalID);
				continue;
			}
	
			int VAS_RESULTS_INDEX = 6;
			Document doc = dataset.getDocument(VAS_RESULTS_INDEX);
	
			DocumentOccurrence occurrence = doc.getOccurrence(docOccIndex);
	
			buildEntryMap(doc);
			
			// There are two sets of readings for each VAS Document so we may be adding readings to an existing document.
			boolean newDocInst = false;
			
			DocumentInstance docinst = rec.getDocumentInstance(occurrence);
			if (docinst == null) {
				docinst = doc.generateInstance(occurrence);
				newDocInst = true;
			}

			// This is a 'global' variable to check whether the current record needs to be saved.
			saveCurrentRecord = false;
			
			try {
				// If we have not seen this external ID previously in this import file
				// then import these readings into the first set of readings,
				// else import into the second set.
				// In both cases update the combined average derived response.
				if(!readingsSeen.contains(externalID)){
					addOrUpdateResponse(docinst,row,"D.O.B.","D.O.B.");
					addOrUpdateResponse(docinst,row,"RCC","RCC");
					addOrUpdateResponse(docinst,row,"RMLO","RMLO");
					addOrUpdateResponse(docinst,row,"LCC","LCC");
					addOrUpdateResponse(docinst,row,"LMLO","LMLO");
					addOrUpdateResponse(docinst,row,"Initials","Initials");
					addOrUpdateResponse(docinst,row,"Warnings","Warnings");
					addOrUpdateResponse(docinst,row,"L average");
					addOrUpdateResponse(docinst,row,"R average");
					addOrUpdateResponse(docinst,row,"CC average");
					addOrUpdateResponse(docinst,row,"MLO average");
					addOrUpdateResponse(docinst,row,"Average density");
					addOrUpdateResponse(docinst,row,"Combined Average Density - Both Readers");
				}
				else {	
					addOrUpdateResponse(docinst,row,"D.O.B_2","D.O.B.");
					addOrUpdateResponse(docinst,row,"RCC_2","RCC");
					addOrUpdateResponse(docinst,row,"RMLO_2","RMLO");
					addOrUpdateResponse(docinst,row,"LCC_2","LCC");
					addOrUpdateResponse(docinst,row,"LMLO_2","LMLO");
					addOrUpdateResponse(docinst,row,"Initials_2","Initials");
					addOrUpdateResponse(docinst,row,"Warnings_2","Warnings");
					addOrUpdateResponse(docinst,row,"L average (2)");
					addOrUpdateResponse(docinst,row,"R average (2)");
					addOrUpdateResponse(docinst,row,"CC average (2)");
					addOrUpdateResponse(docinst,row,"MLO Average (2)");
					addOrUpdateResponse(docinst,row,"Average density (2)");
					addOrUpdateResponse(docinst,row,"Combined Average Density - Both Readers");
				}
				readingsSeen.add(externalID);				
			} catch (ParseException e) {
				log(MSG.INVALID_ROW, fileRow, externalIDCol, externalID);
				return;
			}
		
			ChangeHistory change = docinst.addToHistory(user);
		    docinst.checkForChanges(change);     

			if(newDocInst){
				rec.addDocumentInstance(docinst);
			} else {
				setDocumentComplete(docinst);
			}

			if(saveCurrentRecord){
				repClient.saveRecord(rec, true, saml);
				log(MSG.RECORD_SAVED, rec.getIdentifier().getIdentifier());	
			}
			else {
				log(MSG.RECORD_NO_CHANGE, rec.getIdentifier().getIdentifier());					
			}
		}
	}

	/**
	 * Import the data from a Cumulus csv file.
	 * 
	 * The Cumulus files contain multiple lines per participant - these are used to populate the Cumulus Results document
	 * 
	 * NB: The index of the document occurrences DO Not necessarily correspond to their order in the study schedule.
	 *  
	 * @param docOccIndex the the index of the document occurrence to populate.
	 * @throws Exception
	 */
	private void importCumulusData(int docOccIndex) throws Exception {

		String fileNameCol="file_name";
		
		for (int i = 0; i < data.size(); i++) {

			refreshSAMLIfNeeded();

			int fileRow = i+1;
	
			String[] row = data.get(i);

			// the file_name field is formatted as '2424242424-lcc.bmp'
			String fileName = row[colMap(fileNameCol)];
			
			if (fileName == null || fileName.length() == 0) {
				log(MSG.EXTERNALID_MISSING, fileRow, fileNameCol);
				continue;
			}
		
			log(MSG.READING_ROW, fileRow, fileNameCol, fileName);
			
			String externalID = fileName.substring(0, fileName.indexOf('-'));
			
			String readingType = fileName.substring(fileName.indexOf('-')+1,fileName.indexOf('.')).toUpperCase()+"_";
						
			Record rec = repClient.getRecordByExternalID(dataset, externalID, saml);
	
			if (rec == null) {
				log(MSG.RECORD_MISSING, fileRow, fileNameCol,fileName);
				continue;
			}
	
			int CUMULUS_RESULTS_INDEX = 7;
			Document doc = dataset.getDocument(CUMULUS_RESULTS_INDEX);
	
			DocumentOccurrence occurrence = doc.getOccurrence(docOccIndex);
	
			buildEntryMap(doc);

			// There are multiple readings for each Cumulus Document so we may be adding readings to an existing document.
			boolean newDocInst = true;
			
			DocumentInstance docinst = rec.getDocumentInstance(occurrence);
			if (docinst != null) {
				newDocInst = false;
				Entry fileNameEntry = entryMap.get((readingType + fileNameCol).toLowerCase());
				// Reject if there is already a set of readings for this file_name column 
				List<Response> fileNameResponses = docinst.getResponses(fileNameEntry);
				if(fileNameResponses.size()>0){
					log(MSG.CUMULUS_READINGS_EXIST, fileRow, fileNameCol,fileName, rec.getIdentifier().getIdentifier());
					continue;
				}
			}
	
			try {
				if(newDocInst){
					docinst = doc.generateInstance(occurrence);
				}
				addOrUpdateResponse(docinst,row,readingType+"file_name","file_name");
				addOrUpdateResponse(docinst,row,readingType+"EdgeThreshold","EdgeThreshold");
				addOrUpdateResponse(docinst,row,readingType+"DensityThreshold","DensityThreshold");
				addOrUpdateResponse(docinst,row,readingType+"BreastArea_raster","BreastArea_raster");
				addOrUpdateResponse(docinst,row,readingType+"DenseArea_raster","DenseArea_raster");
				addOrUpdateResponse(docinst,row,readingType+"Reader initials","Reader initials");
				addOrUpdateResponse(docinst,row,readingType+"comments","comments");
				addOrUpdateResponse(docinst,row,readingType+"Density");
				
				// Calculate derived values only when their basic entries have been filled in.
				
				if(hasResponse(docinst,"LCC_Density") && hasResponse(docinst,"LMLO_Density")
						&& !hasResponse(docinst,"L_Average_Density"))
					addOrUpdateResponse(docinst,row,"L_Average_Density");
				
				if(hasResponse(docinst,"RCC_Density") && hasResponse(docinst,"RMLO_Density")
						&& !hasResponse(docinst,"R_Average_Density"))
					addOrUpdateResponse(docinst,row,"R_Average_Density");

				if(hasResponse(docinst,"RCC_Density") && hasResponse(docinst,"LCC_Density")
						&& !hasResponse(docinst,"CC_Average_Density"))
					addOrUpdateResponse(docinst,row,"CC_Average_Density");

				if(hasResponse(docinst,"RMLO_Density") && hasResponse(docinst,"LMLO_Density")
						&& !hasResponse(docinst,"MLO_Average_Density"))
					addOrUpdateResponse(docinst,row,"MLO_Average_Density");
				
				if(hasResponse(docinst,"RCC_Density") && hasResponse(docinst,"LCC_Density") &&
					hasResponse(docinst,"RMLO_Density") && hasResponse(docinst,"LMLO_Density")
					&& !hasResponse(docinst,"Average_Density"))
					addOrUpdateResponse(docinst,row,"Average_Density");
				
			} catch (ParseException e) {
				log(MSG.INVALID_ROW, fileRow, fileNameCol, fileName);
				return;
			}
		
			ChangeHistory change = docinst.addToHistory(user);
		    docinst.checkForChanges(change);     

			if(newDocInst){
				rec.addDocumentInstance(docinst);
			} else {
				setDocumentComplete(docinst);
			}
			
			repClient.saveRecord(rec, true, saml);
			log(MSG.RECORD_SAVED, rec.getIdentifier().getIdentifier());	
		}
	}

	
	/**
	 * Import the data from a Quantra csv file.
	 * 
	 * NB: The index of the document occurrences DO Not necessarily correspond to their order in the study schedule.
	 *  
	 * @param docOccIndex the the index of the document occurrence to populate.
	 * @throws Exception
	 */
	private void importQuantraData(int docOccIndex,int rowNum, String[] row) throws Exception {
		
		int fileRow = rowNum+1;
		String externalIDCol="PatientId";
		
		String externalID = row[colMap(externalIDCol)];
		
		if (externalID == null || externalID.length() == 0) {
			log(MSG.EXTERNALID_MISSING, fileRow, externalIDCol);
			return;
		}
		
		log(MSG.READING_ROW, fileRow, externalIDCol, externalID);
	
		Record rec = repClient.getRecordByExternalID(dataset, externalID, saml);

		if (rec == null) {
			log(MSG.RECORD_MISSING, fileRow, externalIDCol,externalID);
			return;
		}

		// Had to find this in the debugger!
		int QUANTRA_RESULTS_INDEX = 11;
		Document doc = dataset.getDocument(QUANTRA_RESULTS_INDEX);
		DocumentOccurrence occurrence = doc.getOccurrence(docOccIndex);
		buildEntryMap(doc);
	
		boolean newDocInst = false;
		
		DocumentInstance docinst = rec.getDocumentInstance(occurrence);
		if (docinst == null) {
			docinst = doc.generateInstance(occurrence);
			newDocInst = true;
		}

		saveCurrentRecord = false;
		
		try {
			// Loop through all the entries and add a response to each one.
			// This works because the document contains only imported data.
			int numEntries = doc.numEntries();
			for(int i=0;i<numEntries;i++){
				Entry entry = doc.getEntry(i);
				String colName = entry.getName();
				addOrUpdateResponse(docinst,row,colName);
			}
		} catch (ParseException e) {
			log(MSG.INVALID_ROW, fileRow, externalIDCol, externalID);
			return;
		}

		ChangeHistory change = docinst.addToHistory(user);
	    docinst.checkForChanges(change);     

		if(newDocInst){
			rec.addDocumentInstance(docinst);
			setDocumentComplete(docinst);
		} 

		if(saveCurrentRecord){
			repClient.saveRecord(rec, true, saml);
			log(MSG.RECORD_SAVED, rec.getIdentifier().getIdentifier());	
		}
		else {
			log(MSG.RECORD_NO_CHANGE, rec.getIdentifier().getIdentifier());					
		}
	
	}	

	/**
	 * Import the data from a Step Wedge csv file.
	 * 
	 * The Step Wedge files contain multiple lines per participant - these are used to populate the Step Wedge Results document
	 * 
	 * NB: The index of the document occurrences DO Not necessarily correspond to their order in the study schedule.
	 *  
	 * @param docOccIndex the the index of the document occurrence to populate.
	 * @throws Exception
	 */
	private void importStepWedgeData(int docOccIndex) throws Exception {

		// Name,Confidence level,Volume breast,Volume gland,Volume gland (%),Comment

		String fileNameCol="Name";
		
		for (int i = 0; i < data.size(); i++) {

			refreshSAMLIfNeeded();

			int fileRow = i+1;
	
			String[] row = data.get(i);

			// the file_name field is formatted as '2424242424_lcc.bmp' etc.
			String fileName = row[colMap(fileNameCol)];
			
			if (fileName == null || fileName.length() == 0) {
				log(MSG.EXTERNALID_MISSING, fileRow, fileNameCol);
				continue;
			}
		
			log(MSG.READING_ROW, fileRow, fileNameCol, fileName);
			
			String externalID = fileName.substring(0, fileName.indexOf('_'));
			
			String readingType = fileName.substring(fileName.indexOf('_')+1).toUpperCase()+"_";
						
			Record rec = repClient.getRecordByExternalID(dataset, externalID, saml);
	
			if (rec == null) {
				log(MSG.RECORD_MISSING, fileRow, fileNameCol,fileName);
				continue;
			}
	
			int STEPWEDGE_RESULTS_INDEX = 12;
			Document doc = dataset.getDocument(STEPWEDGE_RESULTS_INDEX);
	
			DocumentOccurrence occurrence = doc.getOccurrence(docOccIndex);
	
			buildEntryMap(doc);

			// There are multiple readings for each document so we may be adding readings to an existing document.
			boolean newDocInst = true;
			
			DocumentInstance docinst = rec.getDocumentInstance(occurrence);
			if (docinst != null) {
				newDocInst = false;
				Entry volumeBreastEntry = entryMap.get((readingType + "Volume breast").toLowerCase());
				// Reject if there is already a set of readings for this file_name column 
				List<Response> volumeBreastResponses = docinst.getResponses(volumeBreastEntry);
				if(volumeBreastResponses.size()>0){
					log(MSG.STEPWEDGE_READINGS_EXIST, fileRow, fileNameCol,fileName, rec.getIdentifier().getIdentifier());
					continue;
				}
			}
	
			try {
				if(newDocInst){
					docinst = doc.generateInstance(occurrence);
				}
				addOrUpdateResponse(docinst,row,readingType+"Confidence level","Confidence level");
				addOrUpdateResponse(docinst,row,readingType+"Volume breast","Volume breast");
				addOrUpdateResponse(docinst,row,readingType+"Volume gland","Volume gland");
				addOrUpdateResponse(docinst,row,readingType+"Volume gland (%)","Volume gland (%)");
				addOrUpdateResponse(docinst,row,readingType+"Comment","Comment");
				
				// Calculate derived values that don't have a response.
				// We have to do this because individual responses are being set for imported data above.
				// Unlike other documents which have all their data in a single csv file line and
				// can loop through all entries (including derived) and set responses.
				// If a value cannot be calculated the response is not set.
				int numEntries = doc.numEntries();
				for(int d=0;d<numEntries;d++){
					Entry entry = doc.getEntry(d);
					if(entry instanceof DerivedEntry){
						List<Response> responses = docinst.getResponses(entry);
						if(responses.size()==0){
							String colName = entry.getName();
							addOrUpdateResponse(docinst,row,colName);
						}
					}
				}
				
			} catch (ParseException e) {
				log(MSG.INVALID_ROW, fileRow, fileNameCol, fileName);
				return;
			}
		
			ChangeHistory change = docinst.addToHistory(user);
		    docinst.checkForChanges(change);     

			if(newDocInst){
				rec.addDocumentInstance(docinst);
			} else {
				setDocumentComplete(docinst);
			}
			
			repClient.saveRecord(rec, true, saml);
			log(MSG.RECORD_SAVED, rec.getIdentifier().getIdentifier());	
		}
	}

	
	/**
	 * Import the data from the Volpara csv file.
	 * 
	 * The Volpara files contain multiple lines per participant - these are used to populate the Volpara Results document
	 * 
	 * NB: The index of the document occurrences DO NOT necessarily correspond to their order in the study schedule.
	 * 
	 * The code below will reset ALL values in a document instance if the VolparaVersion in the incoming data is
	 * different to the value currently stored in the document instance.
	 *  
	 * @param docOccIndex the the index of the document occurrence to populate.
	 * @throws Exception
	 */
	private void importVolparaData(int docOccIndex) throws Exception {

		// The Volpara csv file contains lots of columns - but we need only the following:
		//
		//	VolparaVersion,PatientID,BreastSide,MammoView,BreastVolumeCm3,HintVolumeCm3,VolumetricBreastDensity,
		
		for (int i = 0; i < data.size(); i++) {

			refreshSAMLIfNeeded();

			int fileRow = i+1;
	
			String[] row = data.get(i);
			
			// The PatientID column contains the NHS number			
			String externalID = row[colMap("PatientID")];
			
			if (externalID == null || externalID.length() == 0) {
				log(MSG.EXTERNALID_MISSING, fileRow, "PatientID");
				continue;
			}
		
			log(MSG.READING_ROW, fileRow, "PatientID", externalID);
			
			// The BreastSide is 'Left' or 'Right'.
			String breastSide = row[colMap("BreastSide")];
			
			// The MammoView column is 'CC' or 'MLO'.
			String mammoView = row[colMap("MammoView")];

			// The readingType will be one of: LCC_,RCC_,LMLO_,RMLO_
			String readingType = breastSide.charAt(0)+mammoView+"_";
						
			Record rec = repClient.getRecordByExternalID(dataset, externalID, saml);

			// This is a 'global' variable to check whether the current record needs to be saved.
			saveCurrentRecord = false;
	
			if (rec == null) {
				log(MSG.RECORD_MISSING, fileRow, "PatientID",externalID);
				continue;
			}
	
			// This is the index of the VOLPARA document in the dataset - I had to find this using a debugger.
			// We need a method to find documents by name.
			int VOLPARA_RESULTS_INDEX = 13;
			
			Document doc = dataset.getDocument(VOLPARA_RESULTS_INDEX);
	
			DocumentOccurrence occurrence = doc.getOccurrence(docOccIndex);
	
			buildEntryMap(doc);

			// There are multiple CSV lines for each document so we may be adding readings to an existing document.
			boolean newDocInst = true;
			
			DocumentInstance docinst = rec.getDocumentInstance(occurrence);
			
			if (docinst != null) {
				newDocInst = false;
				
				// Check if VolparaVersions match - if not, clear all entries with a value.
				
				String importedVolparaVersion = row[colMap("VolparaVersion")];
				
				Entry volparaVersionEntry = findEntryByName(doc, "VolparaVersion");
				
				// There can be only one response in the document instance for the VolparaVersion entry
				BasicResponse res = (BasicResponse) docinst.getResponses(volparaVersionEntry).get(0);

				// Get the value of the response
				TextValue resval = (TextValue) res.getValue();
				String existingVolparaVersion = resval.getValue();
				
				if(!importedVolparaVersion.equals(existingVolparaVersion)){
					// The VolparaVersion has changed,
					// so clear all existing values in this document instance.
					clearAllResponses(doc, docinst);
					
					// Save the new Volpara version
					addOrUpdateResponse(docinst,row,"VolparaVersion","VolparaVersion");					
				}
			}
	
			try {
				if(newDocInst){
					docinst = doc.generateInstance(occurrence);
					addOrUpdateResponse(docinst,row,"VolparaVersion","VolparaVersion");					
				}
				addOrUpdateResponse(docinst,row,readingType+"BreastVolumeCm3","BreastVolumeCm3");
				addOrUpdateResponse(docinst,row,readingType+"HintVolumeCm3","HintVolumeCm3");
				addOrUpdateResponse(docinst,row,readingType+"VolumetricBreastDensity","VolumetricBreastDensity");
				
				// Calculate derived values.
				// We have to do this because individual responses are being set for imported data above.
				// Unlike other documents which have all their data in a single csv file line and
				// can loop through all entries (including derived) and set responses.
				// If a value cannot be calculated the response is not set.
				int numEntries = doc.numEntries();
				for(int d=0;d<numEntries;d++){
					Entry entry = doc.getEntry(d);
					if(entry instanceof DerivedEntry){
						String colName = entry.getName();
						addOrUpdateResponse(docinst,row,colName);
					}
				}
				
			} catch (ParseException e) {
				log(MSG.INVALID_ROW, fileRow, "PatientID", externalID);
				return;
			}
		
			ChangeHistory change = docinst.addToHistory(user);
		    docinst.checkForChanges(change);     

			if(newDocInst){
				rec.addDocumentInstance(docinst);
			} else {
				setDocumentComplete(docinst);
			}
			
			if(saveCurrentRecord){
				repClient.saveRecord(rec, true, saml);
				log(MSG.RECORD_SAVED, rec.getIdentifier().getIdentifier());	
			}
			else {
				log(MSG.RECORD_NO_CHANGE, rec.getIdentifier().getIdentifier());					
			}
		}
	}
	
	
	/**
	 * Import the Final Risk Assessment-> Screening Appointment csv data.
	 * 
	 * NB: The index of the document occurrences DO Not necessarily correspond to their order in the study schedule.
	 *  
	 * @param docOccIndex the the index of the document occurrence to populate.
	 * @throws Exception
	 */
	private void importFinalScreeningAppointment(int docOccIndex,int rowNum, String[] row) throws Exception {

		int fileRow = rowNum+1;
		String externalIDCol="NhsNumber";
		
		String externalID = row[colMap(externalIDCol)];
		
		if (externalID == null || externalID.length() == 0) {
			log(MSG.EXTERNALID_MISSING, fileRow, externalIDCol);
			return;
		}
		
		log(MSG.READING_ROW, fileRow, externalIDCol, externalID);
	
		Record rec = repClient.getRecordByExternalID(dataset, externalID, saml);

		if (rec == null) {
			log(MSG.RECORD_MISSING, fileRow, externalIDCol,externalID);
			return;
		}

		Document doc = dataset.getDocument(SCREENING_APPOINTMENT_DOC_INDEX);
		DocumentOccurrence occurrence = doc.getOccurrence(docOccIndex);
		buildEntryMap(doc);
			
		DocumentInstance docinst = rec.getDocumentInstance(occurrence);
		if (docinst != null) {
			log(MSG.DOCUMENT_INSTANCE_EXISTS, fileRow, externalIDCol,externalID, rec.getIdentifier().getIdentifier());
			return;
		}
		try {
			docinst = doc.generateInstance(occurrence);
			// Loop through all the entries and add a response to each one.
			// This works because the document contains only imported data.
			int numEntries = doc.numEntries();
			for(int i=0;i<numEntries;i++){
				Entry entry = doc.getEntry(i);
				String colName = entry.getName();
				addOrUpdateResponse(docinst,row,colName);
			}
		} catch (ParseException e) {
			log(MSG.INVALID_ROW, fileRow, externalIDCol, externalID);
			return;
		}
				
		setDocumentComplete(docinst);
		
		ChangeHistory change = docinst.addToHistory(user);
	    docinst.checkForChanges(change);     
		rec.addDocumentInstance(docinst);		
				
		repClient.saveRecord(rec, true, saml);
		log(MSG.RECORD_SAVED, rec.getIdentifier().getIdentifier());		
	}	
	
	/**
	 * Import the data from a DNA Results csv file.
	 * 
	 * NB: The index of the document occurrences DO Not necessarily correspond to their order in the study schedule.
	 *  
	 * @param docOccIndex the the index of the document occurrence to populate.
	 * @throws Exception
	 */
	private void importFinalDNAResults(int docOccIndex,int rowNum, String[] row) throws Exception {
		
		int fileRow = rowNum+1;
		String externalIDCol="NHS Number";
		
		String externalID = row[colMap(externalIDCol)];
		
		if (externalID == null || externalID.length() == 0) {
			log(MSG.EXTERNALID_MISSING, fileRow, externalIDCol);
			return;
		}
		
		log(MSG.READING_ROW, fileRow, externalIDCol, externalID);
	
		Record rec = repClient.getRecordByExternalID(dataset, externalID, saml);

		if (rec == null) {
			log(MSG.RECORD_MISSING, fileRow, externalIDCol,externalID);
			return;
		}

		Document doc = dataset.getDocument(DNA_RESULTS_DOC_INDEX);
		DocumentOccurrence occurrence = doc.getOccurrence(docOccIndex);
		buildEntryMap(doc);
	
		boolean newDocInst = false;
		
		DocumentInstance docinst = rec.getDocumentInstance(occurrence);
		if (docinst == null) {
			docinst = doc.generateInstance(occurrence);
			newDocInst = true;
		}

		saveCurrentRecord = false;
		
		try {
			// Loop through all the entries and add a response to each one.
			// This works because the document contains only imported data.
			int numEntries = doc.numEntries();
			for(int i=0;i<numEntries;i++){
				Entry entry = doc.getEntry(i);
				String colName = entry.getName();
				addOrUpdateResponse(docinst,row,colName);
			}
		} catch (ParseException e) {
			log(MSG.INVALID_ROW, fileRow, externalIDCol, externalID);
			return;
		}

		ChangeHistory change = docinst.addToHistory(user);
	    docinst.checkForChanges(change);     

		if(newDocInst){
			rec.addDocumentInstance(docinst);
			setDocumentComplete(docinst);
		} 

		if(saveCurrentRecord){
			repClient.saveRecord(rec, true, saml);
			log(MSG.RECORD_SAVED, rec.getIdentifier().getIdentifier());	
		}
		else {
			log(MSG.RECORD_NO_CHANGE, rec.getIdentifier().getIdentifier());					
		}
	
	}	
	

	/**
	 * Set all the values of existing basic responses in a document instance to the standard missing code.
	 * 
	 * Note: A DerivedEntry is a BasicEntry - so this code will also reset derived entries too. 
	 * 
	 * @param doc the document
	 * @param docinst the document instance
	 */
	private void clearAllResponses(Document doc, DocumentInstance docinst) {
		int numEntries = doc.numEntries();
		for(int d=0;d<numEntries;d++){
			Entry entry = doc.getEntry(d);
			if(entry instanceof BasicEntry){
				List<Response> responses = docinst.getResponses(entry);
				// Only set the value if the response exists
				if(responses.size()>=0){
					BasicResponse br = (BasicResponse) docinst.getResponses(entry).get(0);
					// Only set the value if it is not already the default standard code.
					if(br.getValue().getStandardCode()==null || !br.getValue().getStandardCode().equals(defaultStdCode)){
						IValue val = ((BasicEntry)entry).generateValue();
						val.setStandardCode(defaultStdCode);
						br.setValue(val);
					}
				}
			}
		}
	}

	/**
	 * Check that the required column names are in the imported CSV file.
	 * 
	 * @param sourceType - the type of import
	 * @return true if all the columns are present in the csv file OR there are no required columns
	 */	
	boolean checkColumnNames(String sourceType){
		boolean result = true;
		for(String[] headings: COLUMN_HEADINGS){
			if(headings[0].equals(sourceType)){
				for(String colName:headings[1].split(",")){
					if( colMap(colName)==NO_COLUMN){
						log(MSG.COLUMN_MISSING, colName);
						result = false;
					}
				}
				break;
			}
		}
		return result;
	}
	
	/**
	 * Returns true if the entry with the given name has a response in the given document instance.
	 * 
	 * @param docinst the document instance to check
	 * @param entryName the name of an entry in the associated document
	 * @return true if a response exists
	 */
	boolean hasResponse(DocumentInstance docinst,String entryName){
		Entry entry = entryMap.get(entryName.toLowerCase());
		List<Response> responses = docinst.getResponses(entry);
		return responses.size()>0;
	}

	/**
	 * Use the supplied log message and args to write a message to the log.
	 * 
	 * The method writes to the log for this import and the system log.
	 * 
	 * @param message a string in printf format.
	 * @param args the arguments for the message string.
	 */
	private void log(String message, Object... args) {
		String s = String.format(message, args);
		log.println(s);
		logger.info(s);
	}

	/**
	 * Build a map of maps - used to map imported values to option names for a given entry.
	 */
	private void buildOptionMaps(){
		// Make sure option keys are lower case.
		Map<String,String> optionMap = new HashMap<String,String>();
		optionMap.put("aunt", "Maternal aunt");
		optionMap.put("gran", "Maternal gran");
//		optionMap.put("cousin", "Maternal cousin");
		optionMaps.put("breast_cancer_1", optionMap);
	}

	/**
	 * Map a column name from the CSV file to a column index.
	 * 
	 * @param columnName the column name
	 * @return the column index
	 */
	private int colMap(String columnName) {
		int result = NO_COLUMN;
		columnName = columnName.toLowerCase();
		if (colMap.containsKey(columnName)) {
			result = colMap.get(columnName);
		}
		return result;
	}

	/**
	 * Read in a CSV file and store the data, plus a map of column names to column indexes.
	 * 
	 * @param csvFile the CSV file path
	 * @throws IOException
	 */
	private void readCSVFile(File csvFile) throws IOException {
		CSVReader reader = new CSVReader(new BufferedReader(new FileReader(
				csvFile)));
		List<String[]> lines = (List<String[]>) reader.readAll();
		colMap = new HashMap<String, Integer>();
		columnNames = lines.get(0);
		int numCols = columnNames.length;
		for (int i = 0; i < numCols; i++) {
			String colName = columnNames[i].toLowerCase().trim();
			if(colName.length()>0)colMap.put(colName, i);
		}
		data = lines.subList(1, lines.size());
		reader.close();
	}

	/**
	 * Build a map of lower case entry names to entries for a given document.
	 * @param doc
	 */
	private void buildEntryMap(Document doc){
		entryMap.clear();
		for (int i = 0; i < doc.numEntries(); i++) {
			Entry entry = doc.getEntry(i);
			entryMap.put(entry.getName().toLowerCase(), entry);
		}
	}

	/**
	 * Find the entry in the given document with the given name.
	 * 
	 * @param doc the document to search
	 * @param entryName the name of an entry
	 * 
	 * @return the entry of null if no entry with the given name exists.
	 */
	private Entry findEntryByName(Document doc, String entryName) {
		Entry result = null;
		for (int i = 0; i < doc.numEntries(); i++) {
			Entry ent = doc.getEntry(i);
			if (ent.getName().equals(entryName)) {
				result = ent;
				break;
			}
		}
		return result;
	}
	
	/**
	 * Grabs the system-wide standard code from the server.
	 * 
	 * Also sets the default standard code to the one used for derived entries.
	 * 
	 * @throws ConnectException
	 * @throws SocketTimeoutException
	 */
	private void getStandardCodes() throws ConnectException,
			SocketTimeoutException {
		standardCodes = repClient.getStandardCodes(saml);
		for (StandardCode code : standardCodes) {
			if (code.isUsedForDerivedEntry()) {
				defaultStdCode = code;
				break;
			}
		}
	}

	/**
	 * Creates a new record - PROCAS specific.
	 * 
	 * @param siteName the site name.
	 * @return the record
	 * @throws Exception - not nice but pragmatic.
	 */
	private Record createRecord(String siteName) throws Exception {

		// create a new record
		Record rec = dataset.generateInstance();

		// set the consent: assume that consent exists for all new records
		ConsentFormGroup cfg = dataset.getAllConsentFormGroup(0);
		PrimaryConsentForm pcf = cfg.getConsentForm(0);
		boolean consent = true;
		if (consent) {
			Consent c = pcf.generateConsent();
			c.setConsentGiven(true);
			rec.addConsent(c);
		}

		// If there is a status called "active" in the study, set the study
		// status to 'active'.
		int numStatuses = dataset.numStatus();
		Status status = null;
		for (int i = 0; i < numStatuses; i++) {
			Status s = dataset.getStatus(i);
			if (s.getGenericState() == org.psygrid.data.model.hibernate.GenericState.ACTIVE) {
				if (s.getLongName().equalsIgnoreCase("active")
						|| s.getShortName().equalsIgnoreCase("active")) {
					status = s;
					break;
				}
			}
		}
		if (status != null) {
			((org.psygrid.data.model.hibernate.Record) rec).setStatus(status);
		}

		// reserve identifier for the record
		String defaultGroupCode = "GMR";
		List<Identifier> ids = repClient.generateIdentifiers(dataset.getId(), defaultGroupCode, 1, saml);
		Identifier psygridIdentifier = ids.get(0);
		rec.setIdentifier(psygridIdentifier);

		// Everything is in one group - with several sites - same consultant at each site
		Group gp = dataset.getGroup(0);
		for(int i=0;i<gp.numSites();i++){
			Site site = gp.getSite(i);
			if(site.getSiteName().equalsIgnoreCase(siteName)){
				rec.setSite(site);
				rec.setConsultant(site.getConsultants().get(0));
				break;
			}
		}

		return rec;
	}


	/**
	 * Add or update a response to an entry in a document instance and set its value to the value of the 
	 * matching column in the supplied row of CSV data.
	 * 
	 * If the imported value is the same as the current value then no changes are made.
	 * 
	 * @param di the document instance containing the entry
	 * @param row the row of data from the CSV file
	 * @param entryName the name of the entry in the document
	 * @throws ParseException if unable to parse the imported string into an appropriate value
	 */
	private void addOrUpdateResponse(DocumentInstance di,String[] row,String entryName) throws ParseException{
		addOrUpdateResponse(di,row,entryName,null);
	}

	/**
	 * Add or update a response to an entry in a document instance and set its value to the value of the 
	 * matching column in the supplied row of CSV data.
	 * 
	 * If the imported value is the same as the current value then no changes are made.
	 * 
	 * Note - we cannot update composite responses.
	 * @param di the document instance containing the entry
	 * @param row the row of data from the CSV file
	 * @param entryName the name of the entry in the document
	 * @param columnName the name of the column in the CSV file
	 * @throws ParseException if unable to parse the imported string into an appropriate value
	 */
	private void addOrUpdateResponse(DocumentInstance di,String[] row,String entryName,String columnName) throws ParseException{

		Entry entry = entryMap.get(entryName.toLowerCase());

		// Check to see if this entry in this document instance already has a response.
		if(hasResponse(di,entryName)) {
			// There is already a response for this entry in this document instance.
			// So we need to update the value of the existing response.
			if(entry instanceof DerivedEntry){
				updateDerivedResponse((DerivedEntry)entry,di);
			}
			else if (entry instanceof BasicEntry) {
				updateBasicResponse(di,(BasicEntry)entry,row,columnName);
			} 
		}
		else {
			// No response exists for this entry. 
			// So we need to add a response.		
			Response response = null;
			if(entry instanceof DerivedEntry){
				response = createDerivedResponse((DerivedEntry)entry,di);
			}
			else if (entry instanceof BasicEntry) {
				response = createBasicResponse((BasicEntry)entry,row,"",columnName);
			} else if (entry instanceof CompositeEntry) {
				response = createCompositeResponse((CompositeEntry) entry, row);
			}
			if (response != null) {
				di.addResponse(response);
				saveCurrentRecord = true;
			}
		}
	}	

	// storing derived values in the database is stupid!
	private BasicResponse createDerivedResponse(DerivedEntry entry, DocumentInstance di) {
		Section sec = entry.getSection();
		SectionOccurrence secOcc = sec.getOccurrence(0);
	
		BasicResponse br = null;
		DerivedEntryHelper helper = new DerivedEntryHelper(entry, di, secOcc, defaultStdCode);
		try {
			IValue value = helper.calculateValue();
			br = entry.generateInstance(secOcc);
			br.setValue(value);
		} catch (Exception e) {
			// The DerivedEntryHelper can throw exceptions if unable to calculate values - ignore for now
			// and return a null basic response.
			logger.debug("Unable to calculate a derived value for entry '"+entry.getName()+"' at the moment.");
		} 
		return br;
	}

	private void updateDerivedResponse(DerivedEntry entry, DocumentInstance di) {
		Section sec = entry.getSection();
		SectionOccurrence secOcc = sec.getOccurrence(0);
	
		DerivedEntryHelper helper = new DerivedEntryHelper(entry, di, secOcc, defaultStdCode);
		try {
			IValue value = helper.calculateValue();

			// Update the value only if is has changed.
			BasicResponse br = (BasicResponse) di.getResponses(entry).get(0);

			IValue existingValue = br.getValue();

			if(!((Value)value).valueEquals((Value)existingValue)){
				br.setValue(value);
				saveCurrentRecord=true;
			}
		} catch (Exception e) {
			// The DerivedEntryHelper can throw exceptions if unable to calculate values - ignore for now
			logger.debug("Unable to calculate a derived value for entry '"+entry.getName()+"' at the moment.");
		} 
	}
	
	private BasicResponse createBasicResponse(BasicEntry entry, String[] row,
                                              String prefix, String columnName) throws ParseException {

		Section sec = entry.getSection();

		// Assume one section occurrence for now
		SectionOccurrence secOcc = sec.getOccurrence(0);
		BasicResponse br = entry.generateInstance(secOcc);

		String entryName = (columnName==null)?(prefix + entry.getName()):columnName;

		IValue val = generateValue(entry, row, entryName);

		br.setValue(val);
		return br;
	}

	private void updateBasicResponse(DocumentInstance di,BasicEntry entry, String[] row, String columnName) throws ParseException {

		BasicResponse br = (BasicResponse) di.getResponses(entry).get(0);

		String entryName = (columnName == null) ? (entry.getName()) : columnName;
		
		IValue existingVal = br.getValue();

		IValue val = generateValue(entry, row, entryName);

		// Update the value only if is has changed.
		if(!((Value)val).valueEquals((Value)existingVal)){
			br.setValue(val);
			saveCurrentRecord=true;
		}
	}
	
	private IValue generateValue(BasicEntry entry, String[] row,String entryName)
			throws ParseException {

		IValue val = entry.generateValue();

		EntryStatus entryStatus = entry.getEntryStatus();

		String dataVal = null;

		int column = colMap(entryName);
		if (column >= 0 && column<row.length) {
			dataVal = row[column];
			dataVal = dataVal.trim();
		}

		try {
			if (dataVal == null) {
				if (entryStatus==EntryStatus.MANDATORY) {
					// The imported file does not contain a column for the current entry so set value to unknown
					val.setStandardCode(standardCodes.get(0));
				}				
			} else {
				// Set mandatory fields to unknown if they have no value
				if (dataVal.equalsIgnoreCase(UNKNOWN) || dataVal.equalsIgnoreCase("Not Stated")
						|| (entryStatus==EntryStatus.MANDATORY && dataVal.length()==0)) {
					val.setStandardCode(standardCodes.get(0));
				} else if (val instanceof ITextValue) {
					ITextValue theVal = (ITextValue) val;
					theVal.setValue(dataVal);
				} else if (val instanceof ILongTextValue) {
					ILongTextValue theVal = (ILongTextValue) val;
					theVal.setValue(dataVal);
				} else if (val instanceof IIntegerValue) {
					IIntegerValue theVal = (IIntegerValue) val;
					theVal.setValue(Integer.parseInt(dataVal));
				} else if (val instanceof INumericValue) {
					INumericValue theVal = (INumericValue) val;
					theVal.setValue(Double.parseDouble(dataVal));
				} else if (val instanceof IDateValue) {
					IDateValue theVal = (IDateValue) val;
					// TODO: Add config for unknown dates
					if (!dataVal.equals("/  /") && dataVal.trim().length()!=0) {
						parseDate(theVal, dataVal);
					}
				} else if (val instanceof BooleanValue) {
					BooleanValue theVal = (BooleanValue) val;
					theVal.setValue(Boolean.parseBoolean(dataVal));
				} else if (val instanceof IOptionValue) {
					IOptionValue theVal = (IOptionValue) val;
					OptionEntry oe = (OptionEntry) entry;
					// Need to get the option with the same name as the imported value.
					Option option = mapOption(dataVal, oe);
					if (option != null) {
						theVal.setValue(option);
					} else if (dataVal.length() != 0) {
						throw new ParseException("Invalid value", 0);
					}
				}
			}
		} catch (ParseException ex) {
			log(MSG.INVALID_VALUE, dataVal, entry.getDisplayText());
			throw ex;
		} catch (NumberFormatException ex) {
			if (dataVal.length() > 0) {
				log(MSG.INVALID_VALUE, dataVal, entry.getDisplayText());
				throw new ParseException("Invalid value", 0);
			}
		}
		
		return val;
	}

    	
	private Option mapOption(String dataVal, OptionEntry oe) {
		Option option = null;
		String entryName = oe.getName();
		dataVal=dataVal.toLowerCase();
		if(optionMaps.containsKey(entryName) && optionMaps.get(entryName).containsKey(dataVal)){
			dataVal = optionMaps.get(entryName).get(dataVal);
		}
		int numberOfOptions = oe.numOptions();
		for (int i = 0; i < numberOfOptions; i++) {
			Option op = oe.getOption(i);
			if (op.getName().equalsIgnoreCase(dataVal)) {
				option = op;
				break;
			}
		}
		return option;
	}

	private void parseDate(IDateValue value, String data) throws ParseException  {
		// make sure all properties are null
		value.setValue(null);
		value.setMonth(null);
		value.setYear(null);

		if (data == null) {
			return;
		}

		Matcher m = monthYearPattern.matcher(data);
		Matcher m2 = yearPattern.matcher(data);
		if (m.matches()) {
			// the string value represents a month and year only
			int month = Integer.parseInt(m.group(1));
			if (month > 12 || month < 1) {
				throw new ParseException(
						"'" + month + "' is not a valid month", 0);
			}
			// decrement month, as internally month stored as 0=Jan,...,11=Dec
			value.setMonth(new Integer(--month));
			value.setYear(Integer.parseInt(m.group(2)));
		} else if (m2.matches()) {
			// the string value represents a year only
			value.setYear(Integer.parseInt(m2.group(1)));
		} else {
			// the string value represents a complete date
			// try both formats
			try {
				value.setValue(dateFormatter.parse(data));
			}  catch (ParseException e) {
				value.setValue(dateFormatter2.parse(data));
			}
		}
	}

	private CompositeResponse createCompositeResponse(
            CompositeEntry composite, String[] row) throws ParseException {

		Section sec = composite.getSection();

		// Assume one occurrence for now
		SectionOccurrence secOcc = sec.getOccurrence(0);
		CompositeResponse cr = composite.generateInstance(secOcc);

		String tableName = composite.getName();
		int numEntries = composite.numEntries();

		for (int i = 0; i < TABLE_MAX_ROW; i++) {
			String prefix = tableName + (i + 1) + "_";
			// If there are no column headings in the imported data for this row
			// then stop adding rows.
			boolean foundData = false;
			for (int j = 0; j < numEntries; j++) {
				BasicEntry be = composite.getEntry(j);
				String entryName = be.getName();
				String colName = prefix + entryName;
				int colNum = colMap(colName);
				if (colNum != NO_COLUMN && row[colNum].trim().length() != 0) {
					foundData = true;
					break;
				}
			}
			if (!foundData)
				break;
			CompositeRow comprow = cr.createCompositeRow();
			for (int j = 0; j < numEntries; j++) {
				BasicEntry be = composite.getEntry(j);
				BasicResponse br = createBasicResponse(be, row, prefix, null);
				comprow.addResponse(br);
			}
		}
		return cr;
	}
	
	private ISubject createESLSubject(String row[],String studyNumber) {

		String nhsCol = "NhsNumber";
		String lastnameCol = "FullName";
		String address1Col = "AddressOneLine";

		String nhsNumber = row[colMap(nhsCol)];
		String lastname = row[colMap(lastnameCol)];
		String address1 = row[colMap(address1Col)];

		ISubject eslSubject = eslFactory.createSubject();
		eslSubject.setStudyNumber(studyNumber);
		eslSubject.setLastName(lastname);
		eslSubject.setNhsNumber(nhsNumber);
		eslSubject.setFirstName("NA");
		eslSubject.setSex("Female");
		
		String dobVal = row[colMap("DateOfBirth")];
		try {
			Date dob = dateFormatter.parse(dobVal);
			eslSubject.setDateOfBirth(dob);
		} catch (ParseException e) {
			// Ignore partial dates for now
		}
		
		/*
		 * eslSubject.setStudyNumber(studyNumber); eslSubject.setTitle(title);
		 * eslSubject.setFirstName(forename); eslSubject.setLastName(surname);
		 * eslSubject.setSex(sex); eslSubject.setDateOfBirth(dob);
		 * eslSubject.setEmailAddress(email);
		 * eslSubject.setWorkPhone(workPhone);
		 * eslSubject.setMobilePhone(mobilePhone);
		 * eslSubject.setNhsNumber(NhsNumber);
		 * eslSubject.setHospitalNumber(hospitalNumber);
		 * eslSubject.setCentreNumber(centreNumber);
		 * eslSubject.setRiskIssues(riskIssues);
		 * //eslSubject.addOrUpdateCustomValue(key,value);
		 * 
		 * eslAddress.setAddress1(address1); eslAddress.setAddress2(address2);
		 * eslAddress.setAddress3(address3); eslAddress.setCity(city);
		 * eslAddress.setRegion(region); eslAddress.setCountry(country);
		 * eslAddress.setPostCode(postcode); eslAddress.setHomePhone(homePhone);
		 * eslSubject.setAddress(eslAddress);
		 */

		IAddress eslAddress = eslFactory.createAddress();
		eslAddress.setAddress1(address1);

		eslSubject.setAddress(eslAddress);
		return eslSubject;
	}

	private void saveESLSubject(ISubject eslSubject) throws Exception {
		String group = IdentifierHelper.getGroupCodeFromIdentifier(eslSubject
				.getStudyNumber());
		for (org.psygrid.esl.model.IGroup eslGroup : eslProject.getGroups()) {
			if (eslGroup.getGroupCode().equals(group)) {
				eslSubject.setGroup(eslGroup);
				break;
			}
		}
		eslClient.saveSubject(eslSubject, saml);
	}

	/**
	 * Set the status of a document to complete.
	 */
	private void setDocumentComplete(DocumentInstance docInst){
	    Document document = docInst.getOccurrence().getDocument();
	    for (int i = 0, c = document.numStatus(); i < c; ++i) {
	        Status status = document.getStatus(i);
	        if (status.getShortName().equals(Status.DOC_STATUS_COMPLETE)) {
	            ((DocumentInstance)docInst).setStatus(status);
	            break;
	        }
	    }
	}

	
	/*
	 * 
	 * if(!testMode){ String dsFileName =
	 * "C:\\Users\\Bill\\openCDMS\\New Studies\\mds\\MDS Import Test4_ds.xml";
	 * Object obj1 = PersistenceManager.getInstance().load(dsFileName); ds =
	 * (IDataSet)obj1; }
	 * 
	 * if (be instanceof IDerivedEntry ){ //handle derived entries without a
	 * mapping i.e. we need to run the //calculation defined in the derived
	 * entry - do this later so add //to a list that we'll deal with at the end
	 * }
	 * 
	 * IDerivedEntry de = (IDerivedEntry)doc.getEntry(0); DerivedEntryHelper
	 * helper = new DerivedEntryHelper(de, di, secOcc, defaultStdCode); br =
	 * de.generateInstance(secOcc); di.addResponse(br);
	 * br.setValue(helper.calculateValue());
	 * 
	 * //TODO naughty cast from interface to implementation
	 * //((DocumentInstance)di).setCreated(schStartDate);
	 * //((DocumentInstance)di).setEdited(schStartDate);
	 * 
	 * IAssociatedConsentForm acf = pcf.getAssociatedConsentForm(0); boolean
	 * assoc_consent = true; if (assoc_consent){ IConsent c =
	 * acf.generateConsent(); c.setConsentGiven(true); rec.addConsent(c); }
	 */

	/*
	 * Utility function for dumping the contents of existing records matching
	 * the external IDs of records in a csv file.
	 * 
	 * Useful for testing an import using a tool such a winmerge.
	 * 
	 * The output is written as another CSV file in the same directory as the
	 * input file.
	 */
//	private void export() throws Exception {
//
//		log.clear();
//
////		loadConfig();
//
//		getStandardCodes();
//
//		String projectCode = dataset.getProjectCode();
//
//		readCSVFile(csvFile);
//
//		String docName = occurrence.getDocument().getName().toLowerCase();
//		String externalIDCol = config(docName + ".externalid", "ID");
//
//		String outFile = csvFile.getAbsolutePath() + ".export";
//		PrintWriter out = new PrintWriter(new FileWriter(outFile));
//
//		for (String heading : columnNames) {
//			out.print("\"" + heading + "\"" + ",");
//		}
//		out.println();
//
//		for (int i = 0; i < data.size(); i++) {
//
//			IRecord rec = null;
//
//			String[] csvrow = data.get(i);
//			String externalID = csvrow[colMap(externalIDCol)];
//
//			if (externalID == null || externalID.length() == 0) {
//				log(MSG.EXTERNALID_MISSING, i, externalIDCol);
//				continue;
//			}
//
//			rec = findRecord(externalID);
//
//			if (rec == null) {
//				log(MSG.RECORD_MISSING, i, externalIDCol, externalID);
//				continue;
//			}
//
//			IDocumentInstance docinst = rec.getDocumentInstance(occurrence);
//			if (docinst == null) {
//				log(MSG.DOCUMENT_INSTANCE_MISSING, i, externalIDCol,
//						externalID, rec.getIdentifier().getIdentifier());
//				continue;
//			}
//
//			String[] row = exportDocumentInstance(docinst);
//			for (String value : row) {
//				if (value != null)
//					out.print(value);
//				out.print(",");
//			}
//			out.println();
//		}
//		out.flush();
//	}
//
//	private String[] exportDocumentInstance(IDocumentInstance instance)
//			throws Exception {
//
//		String[] row = new String[colMap.size()];
//
//		IDocumentOccurrence occurrence = instance.getOccurrence();
//		IDocument doc = occurrence.getDocument();
//
//		int numEntries = doc.numEntries();
//
//		for (int i = 0; i < numEntries; i++) {
//			IEntry entry = doc.getEntry(i);
//			if (entry instanceof IBasicEntry) {
//				exportBasicResponse(instance, (IBasicEntry) entry, row);
//			} else if (entry instanceof ICompositeEntry) {
//				exportCompositeResponse(instance, (ICompositeEntry) entry, row);
//			}
//		}
//		return row;
//	}
//
//	private void exportBasicResponse(IDocumentInstance instance,
//			IBasicEntry entry, String[] row) {
//
//		ISection sec = entry.getSection();
//
//		// Assume one occurrence for now
//		ISectionOccurrence secOcc = sec.getOccurrence(0);
//		IBasicResponse response = (IBasicResponse) instance.getResponse(entry,
//				secOcc);
//		IValue value = response.getValue();
//		String valStr = value.isNull() ? "" : value.exportTextValue(true);
//
//		int column = colMap(entry.getName());
//		if (column != NO_COLUMN) {
//			row[column] = "\"" + valStr + "\"";
//		}
//	}
//
//	private ICompositeResponse exportCompositeResponse(
//			IDocumentInstance instance, ICompositeEntry composite, String[] row)
//			throws Exception {
//
//		ISection sec = composite.getSection();
//
//		// Assume one occurrence for now
//		ISectionOccurrence secOcc = sec.getOccurrence(0);
//		ICompositeResponse cr = (ICompositeResponse) instance.getResponse(
//				composite, secOcc);
//
//		String tableName = composite.getName();
//
//		int comprows = cr.numCompositeRows();
//		for (int i = 0; i < comprows; i++) {
//			String prefix = tableName + (i + 1) + "_";
//			ICompositeRow comprow = cr.getCompositeRow(i);
//			int compcols = composite.numEntries();
//			for (int j = 0; j < compcols; j++) {
//				IBasicEntry be = composite.getEntry(j);
//				IBasicResponse response = comprow.getResponse(be);
//				IValue value = response.getValue();
//				String valStr = value.isNull() ? "" : value
//						.exportTextValue(true);
//
//				int column = colMap(prefix + be.getName());
//				if (column != NO_COLUMN) {
//					row[column] = "\"" + valStr + "\"";
//				}
//			}
//		}
//		return cr;
//	}

}

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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.psygrid.data.model.dto.RecordDTO;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.repository.Repository;
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
 * ADDRESS2 ImportPlugin.
 * 
 * Imports data from CSV files into the following documents for ADDRESS2:
 * 
 * Sample Tracking
 * Blood Analysis Results
 * 
 * The ADDRESS2 workflow is as follows:
 * 
 *  1. The CROs take blood samples and assign an externally generated sample ID to the tubes.
 *  2. The CRO opens the 'Sample Tracking' document and enters the sample ID in the field named bloodSampleId
 *  3. The labelled tubes are sent to the ECACC lab at Porton Down for processing and storage.
 *  4. The people at ECACC will use the openCDMS web site to periodically uploaded a CSV file containing the current status of the samples they hold.
 *  5. openCDMS will process the CSV file and update the 'Sample Tracking' document
 *      using the sample ID in the CSV file to find the matching record via the bloodSampleId entered by the CRO.
 *      Only the fields that have changed will be updated. 
 *  6. The ECACC lab will send a processed blood vial to the University of Bristol for analysis.
 *  7. The people at the University of Bristol will use the openCDMS web site to upload two types of CSV file containing analysis results.
 *  8. openCDMS will process these CSV files and create or update the 'Blood Analysis Results' document
 *      using the sample ID in the CSV files to find the matching record (via the bloodSampleID in the Sample Tracking document entered by the CRO).
 *      Only the fields that have changed will be updated.
 * 
 * @author Terry Child
 * 
 */
public class ADDRESS2ImportPlugin implements ImportPlugin {

	private static final Log logger = LogFactory.getLog(ADDRESS2ImportPlugin.class);

	private static String SAMPLE_TRACKING = "Sample Tracking";
	private static String BLOOD_ANALYSIS_GAD_AND_IA2 = "Blood Analysis GAD and IA2";
	private static String BLOOD_ANALYSIS_ZNT8_AND_INSULIN = "Blood Analysis ZnT8 and Insulin";

	private static String[] SOURCE_TYPES = {SAMPLE_TRACKING, BLOOD_ANALYSIS_GAD_AND_IA2, BLOOD_ANALYSIS_ZNT8_AND_INSULIN};
		
	/**
	 * The names of documents within the dataset.
	 */
	private static final String SAMPLE_TRACKING_DOC_NAME = "Sample Tracking";
	private static final String BLOOD_ANALYSIS_DOC_NAME = "Blood Analysis Results";
		
	private static final int NO_COLUMN = -1;

	private static Pattern monthYearPattern = Pattern
			.compile(" */?([0-9]{1,2})/([0-9]{4})");
	private static Pattern yearPattern = Pattern.compile(" */? */?([0-9]{4})");
	
	private final DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
	private final DateFormat dateFormatter2 = new SimpleDateFormat("yyyyMMdd");

	private static String UNKNOWN = "Unknown";
	
	private static class MSG {
		static final String COLUMN_MISSING = "The imported file does not contain a required column named '%s'.";
		static final String EXTERNALID_MISSING = "Row number %d of the imported data does not have a valid %s.";
		static final String READING_ROW = "Reading row number %d of the imported data with %s='%s'.";
		static final String INVALID_ROW = "Ignoring row number %d of the imported data with %s='%s' - it contains invalid data.";
		static final String INVALID_VALUE = "Value '%s' of column '%s' in invalid.";
		static final String RECORD_SAVED = "Record '%s' saved";
		static final String DOCUMENT_INSTANCE_MISSING = "Ignoring row number %d of the imported data with %s='%s'. No existing record with blood sample ID of '%s'.";
		static final String DOCUMENT_INSTANCE_TOOMANY = "Ignoring row number %d of the imported data with %s='%s'. More that one record matches this sample id: '%s'.";
		static final String READINGS_EXIST = "Ignoring row number %d of the imported data with %s='%s'. It already has existing values within the record with identifier '%s'.";
	}

	/**
	 * To access the repository service
	 */
	private Repository repository;

	private DataSet dataset = null;
	private File csvFile = null;
	private String user = null;
	
	/**
	 * Attribute authority query client
	 */
	private AAQCWrapper aaqc;

	// The last time the saml was updated
	private Date samlTime = null;

	private String saml = null;

	private String[] columnNames = null;
	private List<String[]> data = null;
	private Map<String, Integer> colMap = null;
	
	PrintStream log = null;

	// Map entry names to column names for each type of import
		
	String[][][] entriesMap = 
		{	
		 {
	      {"dateReceived","Date received"},
		  {"QCode","QCode"},
		  {"sampleStatus","Sample Status"},
		  {"bloodVolume","Blood volume"},
		  {"numberOfAmpulesStored","No of amps stored"},
		  {"DNAYieldValue","DNAYield"},
		  {"numberOfSerumVials","No of serum vials"}
		 },
		 {
		  {"shipmentIdentifier","Shipment ID"},
		  {"bloodSampleId","Sample ID"},
		  {"UAS","UAS"},
		  {"dateSampleReceived","Date rec"},
		  {"GADResult","GAD result"},
		  {"IA2Result","IA2 result"},
		  {"dateIA2GADResults","Date Report"}
		 }, 
	     {
		  {"shipmentIdentifier","Shipment ID"},
		  {"bloodSampleId","Sample ID"},
		  {"UAS","UAS"},
		  {"dateSampleReceived","Date rec"},
		  {"CIAAInsulinResult","CIAA result"},
		  {"ZnT8Result","ZnT8 result"},
		  {"dateZnT8InsulinResults","Date Report"}
		 } 
		}; 
	
	
	/**
	 * Set the repository
	 * @param repository
	 */
	public void setRepository(Repository repository) {
		this.repository = repository;
	}	

	/**
	 * Set the user who will have permission to save the records.
	 * This means that the user who requests the import can have lower privileges.
	 * @param user
	 */
	public void setUser(String user){
		this.user=user;
	}
	
	public String[] getImportTypes() {
		return SOURCE_TYPES;
	}
	
	public void run(String projectCode, String sourceType, String filePath,String user, AAQCWrapper aaqc,PrintStream log) throws Exception {

		this.csvFile = new File(filePath);
		this.aaqc=aaqc;
		this.saml = aaqc.getSAMLAssertion(this.user);
		this.samlTime=new Date();
		this.log = log;
		
		logger.info("Importing into dataset:"+projectCode+" source data type:"+sourceType);
		
		try {
			this.dataset = getDataSet(projectCode, saml);

			readCSVFile(csvFile);
			if (sourceType.equals(SAMPLE_TRACKING) && !columnsOK(entriesMap[0]) ||
				sourceType.equals(BLOOD_ANALYSIS_GAD_AND_IA2) && !columnsOK(entriesMap[1]) ||
			    sourceType.equals(BLOOD_ANALYSIS_ZNT8_AND_INSULIN) && !columnsOK(entriesMap[2])) {
				return;
			}			
			
			for (int i = 0; i < data.size(); i++) {
				refreshSAML();
				String[] row = data.get(i);
				if (sourceType.equals(SAMPLE_TRACKING)) {
					importRow(i,row,entriesMap[0],"Ref",SAMPLE_TRACKING_DOC_NAME);
				} 
				else if (sourceType.equals(BLOOD_ANALYSIS_GAD_AND_IA2)) {
					importRow(i,row,entriesMap[1],"Sample ID",BLOOD_ANALYSIS_DOC_NAME);
				} 
				else if (sourceType.equals(BLOOD_ANALYSIS_ZNT8_AND_INSULIN)) {
					importRow(i,row,entriesMap[2],"Sample ID",BLOOD_ANALYSIS_DOC_NAME);
				}			
			}
		} catch (Exception e) {
			log.println("Problem importing ADDRESS2 data - please contact OpenCDMS support on 0161 275 5164 or email support@opencdms.org : "+e.getMessage());
			logger.error("Problem importing ADDRESS2 data", e);
			throw e;
		}
	}
	
	private DataSet getDataSet(String projectCode,String saml) throws Exception {
			DataSet datasetSummary = repository.getDataSetSummaryWithDocs(projectCode, saml).toHibernate();
			return repository.getDataSetComplete(datasetSummary.getId(), saml).toHibernate();
	}


	private boolean columnsOK(String[][] entries){
		boolean ok = true;
		for(String[] entry: entries){
			if(colMap(entry[1])==NO_COLUMN){
				log(MSG.COLUMN_MISSING, entry[1]);
				ok = false;
			}			
		}
		return ok;
	}
	
	private void importRow(int rowNum, String[] row,String[][] entries,String bloodSampleIDColumn,String documentName) throws Exception {
		
		int fileRow = rowNum+1;
		
		
		String bloodSampleId = row[colMap(bloodSampleIDColumn)];
		
		if (bloodSampleId == null || bloodSampleId.length() == 0) {
			log(MSG.EXTERNALID_MISSING, fileRow, bloodSampleIDColumn);
			return;
		}
		
		log(MSG.READING_ROW, fileRow, bloodSampleIDColumn, bloodSampleId);

		Document doc = dataset.getDocument(documentName);
		DocumentOccurrence docOcc = doc.getOccurrence(0);

		// Get identifiers for records with matching bloodSampleId - there should be one
		String[] identifiers = repository.getIdentifiersByResponse(dataset.getProjectCode(),SAMPLE_TRACKING_DOC_NAME,"bloodSampleId",bloodSampleId, saml);		

		if(identifiers.length==0){
			log(MSG.DOCUMENT_INSTANCE_MISSING, fileRow, bloodSampleIDColumn, bloodSampleId,bloodSampleId);
			return;
		}
		else if(identifiers.length>1){		
			log(MSG.DOCUMENT_INSTANCE_TOOMANY, fileRow, bloodSampleIDColumn, bloodSampleId,Arrays.toString(identifiers));
			return;
		}

		org.psygrid.data.model.dto.RecordDTO result = repository.getRecordSingleDocumentForOccurrence(identifiers[0],docOcc.getId(), saml);

		Record rec = result.toHibernate();

		rec.attach(dataset);

		DocumentInstance docinst = rec.getDocumentInstance(docOcc);

		// Do we need a new document instance
		boolean newDocInst = (docinst==null);		

		if (newDocInst) {
			docinst = doc.generateInstance(docOcc);
		}

		// Save a record only if any of the imported values have changed
		boolean save = false;
		
		for(String[] entry: entries){

			String dataVal = null;

			// Grab the imported data value
			int column = colMap(entry[1]);
			if (column >= 0 && column<row.length) {
				dataVal = row[column];
				dataVal = dataVal.trim();
			}

			// Ignore any missing data values
			if (dataVal != null && dataVal.length()>0) {
				try {
					boolean changed = setValue(docinst,entry[0],dataVal);
					if(!save){ save = changed; }
				} catch (ParseException e) {
					log(MSG.INVALID_VALUE, dataVal, entry[1]);
					log(MSG.INVALID_ROW, fileRow, bloodSampleIDColumn, bloodSampleId);
					return;
				}
			}
		}			

		if(save){
			ChangeHistory change = docinst.addToHistory(user);
			docinst.checkForChanges(change);     

			if(newDocInst){
				rec.addDocumentInstance(docinst);		
			}
		
			org.psygrid.data.model.dto.RecordDTO r = rec.toDTO();
			repository.saveRecord(r, true, saml);

			log(MSG.RECORD_SAVED, rec.getIdentifier().getIdentifier());	
		}
	}	
	
	// Refresh the SAML every minute if needed
	private void refreshSAML() throws Exception{
		Date now = new Date();
		if((now.getTime()-samlTime.getTime())>60*1000){
			this.saml = aaqc.getSAMLAssertion(user);
			samlTime=now;
		}
	}
	
	private void log(String message, Object... args) {
		String s = String.format(message, args);
		log.println(s);
		logger.info(s);
	}

	private int colMap(String property) {
		int result = NO_COLUMN;
		property = property.toLowerCase();
		if (colMap.containsKey(property)) {
			result = colMap.get(property);
		}
		return result;
	}

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
	 * Adds a value to the document - creating a response if needed.
	 * Only responses who's values are changed are created or updated are changed.
	 * 
	 * @param di the document instance
	 * @param entryName the name of the entry
	 * @param dataVal the imported value
	 * @return true if the value has been created or updated
	 * @throws ParseException
	 */
	private boolean setValue(DocumentInstance docinst,String entryName,String dataVal) throws ParseException {
		
		boolean changed = false;
		
		BasicEntry entry = (BasicEntry)docinst.getOccurrence().getDocument().getEntry(entryName);

		List<Response> responses = docinst.getResponses(entry);
		
		BasicResponse response = null;
		
		// Create a response if non exists
		if(responses.size()==0){
			Section sec = entry.getSection();
			// Assume one occurrence
			SectionOccurrence secOcc = sec.getOccurrence(0);
			response = entry.generateInstance(secOcc);
			docinst.addResponse(response);
		}
		else {
			response = (BasicResponse)responses.get(0);
		}
		
		Value oldValue = (Value)response.getValue();
		Value newValue = createValue(entry,dataVal);		
		
		// Set the value only if it has changed
		if(!newValue.valueEquals((Value)oldValue)){
			response.setValue(newValue);
			changed = true;
		}
		
		return changed;
	}	

	
	/**
	 * Create a value for a Entry from a String.
	 * 
	 * @param entry the entry
	 * @param dataVal parsed according to the type of entry
	 * @return the new value
	 * @throws ParseException thrown if the value cannot be parsed
	 */
	private Value createValue(BasicEntry entry, String dataVal) throws ParseException {

		Value val = (Value)entry.generateValue();

		try {
			if (val instanceof TextValue) {
				TextValue theVal = (TextValue) val;
				theVal.setValue(dataVal);
			} else if (val instanceof LongTextValue) {
				LongTextValue theVal = (LongTextValue) val;
				theVal.setValue(dataVal);
			} else if (val instanceof IntegerValue) {
				IntegerValue theVal = (IntegerValue) val;
				theVal.setValue(Integer.parseInt(dataVal));
			} else if (val instanceof NumericValue) {
				NumericValue theVal = (NumericValue) val;
				theVal.setValue(Double.parseDouble(dataVal));
			} else if (val instanceof DateValue) {
				DateValue theVal = (DateValue) val;
				parseDate(theVal, dataVal);
			} else if (val instanceof BooleanValue) {
				BooleanValue theVal = (BooleanValue) val;
				theVal.setValue(Boolean.parseBoolean(dataVal));
			} else if (val instanceof OptionValue) {
				OptionValue theVal = (OptionValue) val;
				OptionEntry oe = (OptionEntry) entry;
				// Need to get the option with the same name as the imported value.
				Option option = getOption(oe,dataVal);
				if (option != null) {
					theVal.setValue(option);
				} else {
					throw new ParseException("Invalid value", 0);
				}
			}
		}
		catch (NumberFormatException ex) {
			throw new ParseException("Invalid value", 0);
		}

		return val;
	}

	/**
	 * Get the option whose name matches the supplied string
	 * ignoring case.
	 */
	private Option getOption(OptionEntry oe,String optionName) {
		Option option = null;
		for (Option op:oe.getOptions()) {
			if (op.getName().equalsIgnoreCase(optionName)) {
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
			value.setMonth(--month);
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

}



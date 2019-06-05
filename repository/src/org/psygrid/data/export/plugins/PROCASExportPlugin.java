package org.psygrid.data.export.plugins;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.namespace.QName;

import org.apache.axis.client.Call;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.psygrid.data.export.hibernate.ExportRequest;
import org.psygrid.data.model.IIntegerValue;
import org.psygrid.data.model.INumericValue;
import org.psygrid.data.model.IValue;
import org.psygrid.data.model.hibernate.*;

/**
 * Implementation of the ExportPlugin interface for the PROCAS study.
 * 
 * This converts the whole record to a JSON string format and calls 
 * a web service to calculate the Tyrer-Cuzick value for the record. 
 * 
 * @author "Terry Child"
 *
 */
public class PROCASExportPlugin implements ExportPlugin {

	private static final Log logger = LogFactory.getLog(PROCASExportPlugin.class);

	private static final String WEBSERVICE_URL = "https://psygridwin.opencdms.org:8443/transformers/services/externaltransformer";
//	private static final String WEBSERVICE_URL = "http://localhost/transformers/services/externaltransformer";
	private static final String WEBSERVICE_NAMESPACE = "urn:transformers.psygrid.org";
	private static final String WEBSERVICE_OPERATION = "tyrercuzick";
	private static final String[] COLUMNS = {"Initial Risk - Tyrer-Cuzick","BMI"};
	
	
	public boolean isApplicable(ExportRequest request) {
		return request.getProjectCode().startsWith("PRC");
	}

	public String[] getColumnNames(){
		return COLUMNS;
	}
	
	public Properties getResults(Record record,ExportRequest request) {
		Properties results = new Properties();
		String result = calculateTyrerCuzick(record);
		results.put(COLUMNS[0], result);
		String bmi = calculateBMI(record);
		results.put(COLUMNS[1], bmi);
		return results;
	}

	/**
	 * Gathers responses and uses the external transformer to calculate the result.
	 */
	private String calculateBMI(Record record) {
		String result = "";
		try {
			DataSet dataset = record.getDataSet();
			final int QUESTIONNAIRE_DOC_INDEX = 1;
			final int QUESTIONNAIRE_DOC_OCC_INDEX = 0;
			Document document = dataset.getDocument(QUESTIONNAIRE_DOC_INDEX);
			DocumentOccurrence occurrence = document.getOccurrence(QUESTIONNAIRE_DOC_OCC_INDEX);
			DocumentInstance docinst = record.getDocumentInstance(occurrence);
			
			// Get the height in metres
			Double height_ft = getDouble(docinst,"height_ft");
			Double height_in = getDouble(docinst,"height_in");
			Double height_m = getDouble(docinst,"height_m");
			Double height_cm = getDouble(docinst,"height_cm");
			Double metres = null;
			if(height_ft!=null){
				double inches = height_ft * 12;
				if(height_in!=null){
					inches+=height_in;
				}
				metres=(inches*2.54)/100.0;
			}
			else if(height_m!=null){
				metres = height_m;
				if(height_cm!=null){
					metres+=(height_cm/100.0);
				}
			}

			// Get the weight in Kilos
			Double weight_st = getDouble(docinst,"weight_st");
			Double weight_lb = getDouble(docinst,"weight_lb");
			Double weight_kg = getDouble(docinst,"weight_kg");

			Double kilos = null;

			if(weight_st!=null){
				double pounds = weight_st *14;
				if(weight_lb!=null){
					pounds+=weight_lb;
				}
				kilos = pounds/2.2;
			}
			else kilos = weight_kg;
			
			if(metres!=null && kilos!=null){
				double bmi = kilos/(metres*metres);
				bmi = Math.round(bmi*100)/100.0d;
				result = Double.toString(bmi);
			}
			
		} catch (Exception e) {
			logger.warn("Problem calculating BMI record='"+record.getIdentifier().getIdentifier()+"'",e);
		}
		return result;
	}

	private Double getDouble(DocumentInstance docinst,String entryName){
		Double result = null;
		Entry entry = findEntryByName(docinst.getOccurrence().getDocument(), entryName);
		List<Response> responses = docinst.getResponses(entry);
		if(responses.size()>0){
			BasicResponse response = (BasicResponse)responses.get(0);
			IValue value = response.getValue();
			if(value instanceof INumericValue){
				result = ((INumericValue)value).getValue();
			}
			else if(value instanceof IIntegerValue){
				Integer intVal = ((IIntegerValue)value).getValue();
				if(intVal!=null) result = intVal.doubleValue();
			}
		}
		return result;
	}

	private Entry findEntryByName(Document doc, String entryName){
		Entry result = null;
		for (int i = 0; i < doc.numEntries(); i++) {
			Entry entry = doc.getEntry(i);
			if(entry.getName().equals(entryName)) {
				result = entry;
				break;
			}
		}
		return result;
	}
	
	
	/**
	 * Gathers responses and uses the external transformer to calculate the result.
	 */
	private String calculateTyrerCuzick(Record rec) {
		String result = null;
		try {
			Map<String, Object> recordMap = getRecordMap(rec);
			JSONObject job = new JSONObject(recordMap);
			String recordData = job.toString();
	        Call call = new Call(WEBSERVICE_URL);
	        call.setOperationName(new QName(WEBSERVICE_NAMESPACE, WEBSERVICE_OPERATION));
	        result = (String)call.invoke(new Object[]{recordData});
		} catch (Exception e) {
			logger.warn("Problem calling Tyrer-Cuzick calculation record='"+rec.getIdentifier().getIdentifier()+"'",e);
		}
		return result;
	}

	/**
	 * Returns a set of nested maps holding the responses for all document instances
	 * in the supplied record.
	 */
	private Map<String, Object> getRecordMap(Record record) {

		Map<String, Object> documentInstances = new HashMap<String, Object>();

		DataSet dataset = record.getDataSet();
		
		int numDocs = dataset.numDocuments();
		
		for(int i=0;i<numDocs;i++){
			Document document = dataset.getDocument(i);
			List<DocumentInstance> docInstances = record.getDocumentInstances(document);
			for(DocumentInstance docInst:docInstances){
				Map<String, Object> responses = getResponseMap(docInst);
				documentInstances.put(docInst.getOccurrence().getName(), responses);
			}
		}

		return documentInstances;
	}

	
	/**
	 * Returns a map holding the name-value pairs for all the entries within a document instance.
	 * Composite entries are returned as lists of nested maps.
	 */
	private Map<String, Object> getResponseMap(DocumentInstance docinst) {

		Map<String, Object> responses = new HashMap<String, Object>();

		DocumentOccurrence occurrence = docinst.getOccurrence();
		Document doc = occurrence.getDocument();

		int numEntries = doc.numEntries();

		for (int i = 0; i < numEntries; i++) {
			Entry entry = doc.getEntry(i);
			if (entry instanceof BasicEntry) {
				mapBasicResponse(docinst, (BasicEntry) entry, responses);
			} else if (entry instanceof CompositeEntry) {
				mapCompositeResponse(docinst, (CompositeEntry) entry,
						responses);
			}
		}
		return responses;
	}

	/**
	 * Adds the current value of the response to a basic entry to 
	 * the responses map.
	 * 
	 * @param basic - the basic entry for which we want to retrieve the response
	 * @param responses - the map from entry names to response values
	 */
	private void mapBasicResponse(DocumentInstance docinst, BasicEntry basic,Map<String, Object> responses) {
		if(docinst.getResponses(basic).size()>0){
		BasicResponse res = (BasicResponse) docinst.getResponses(basic).get(0);
		IValue val = res.getValue();
		Object objVal = ((Value) val).getTheValue();
		String value = formatValue(objVal);
		responses.put(basic.getName(), value);
		}
	}

	/**
	 * Adds the current values of the responses to a composite entry to the
	 * responses map. The rows of the composite response are added as a list
	 * with each value in the list being a map of basic entry names to their
	 * response values.
	 * 
	 * @param composite
	 *            the composite entry for which we want to retrieve responses
	 * @param responses
	 *            the map from entry names to response values
	 */
	private void mapCompositeResponse(DocumentInstance docinst,
			CompositeEntry composite, Map<String, Object> responses) {

		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
		CompositeResponse compres = (CompositeResponse) docinst.getResponses(
				composite).get(0);
		int numEntries = composite.numEntries();
		int numrows = compres.numCompositeRows();
		for (int i = 0; i < numrows; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			rows.add(map);
			CompositeRow comprow = compres.getCompositeRow(i);
			for (int j = 0; j < numEntries; j++) {
				BasicEntry basic = composite.getEntry(j);
				BasicResponse res = comprow.getResponse(basic);
				IValue val = res.getValue();
				Object objVal = ((Value) val).getTheValue();
				String value = formatValue(objVal);
				map.put(basic.getName(), value);
			}
		}
		responses.put(composite.getName(), rows);
	}

	/**
	 * Format the value of an entry as a string.
	 * 
	 * @param variableValue
	 *            the value stored in the entry's value model.
	 * @return a string representation of the value
	 */
	private String formatValue(Object variableValue) {
		String value = null;
		if (variableValue == null) {
			value = "";
		} else if (variableValue instanceof String) {
			value = (String) variableValue;
		} else if (variableValue instanceof Double) {
			value = Double.toString((Double) variableValue);
		} else if (variableValue instanceof Option) {
			Integer code = ((Option) variableValue).getCode();
			if (code == null) {
				value = "";
			}
			value = code.toString();
		} else if (variableValue instanceof Integer) {
			value = Integer.toString(((Integer) variableValue).intValue());
		} else if (variableValue instanceof Timestamp) {
			value = variableValue.toString();
		}
		return value;
	}
	
}

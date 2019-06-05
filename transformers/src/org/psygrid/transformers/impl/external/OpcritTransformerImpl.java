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

package org.psygrid.transformers.impl.external;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.psygrid.transformers.TransformerException;


/**
 * Class to convert data to and from the format required by the 
 * Opcrit web service. Used to calculate the ExternalDerivedEntry 
 * field in the 90 questions Opcrit Document.
 * 
 * The URL for the remote Opcrit service is specified in the 
 * applicationContext.xml
 * 
 * @author Lucy Bridges
 *
 */
public class OpcritTransformerImpl implements ExternalServiceTransformer {

	/**
	 * A list of diagnosis codes and names for the 13 different scales returned by Opcrit
	 */
	public static final Map<String, Map<String,String>> diagnosis = populateDiagnosis();

	/**
	 * The url used to connect to the remote data source thing..
	 */
	private URL url = null;

	private String urlLocation;
	
	/**
	 * The default value to be used if a value is missing
	 * or a standard code has been used.
	 */
	private static final String DEFAULT_VALUE = "0";
	
	public OpcritTransformerImpl() {
	}

	/**
	 * Get the location of the remote web service
	 * 
	 * @return the url
	 */
	public URL getUrl() {
		return url;
	}

	/**
	 * Set the location of the remote web service
	 * 
	 * @param the url
	 */
	public void setUrl(URL url) {
		this.url = url;
	}


	public String getUrlLocation() {
		return urlLocation;
	}

	public void setUrlLocation(String urlLocation) {
		this.urlLocation = urlLocation;
	}

	/**
	 * Transform the answers to the ninety Opcrit questions, formatted as a
	 * comma-separated string, using the Opcrit webservice. Will return a 
	 * user-friendly string.  
	 * 
	 * @param inputdata
	 * @return outputdata
	 */
	public String transform (String data) throws TransformerException {	
		//remove standard codes
		data = removeStdCodes(data);
		
		//connect to Opcrit service and import the results
		String result = send(data);
		return importData(result, data);
	}

	/**
	 * Connect to the remote URL and send the formatted data
	 * 
	 * @param data formatted as required by the remote service as a string
	 * @return results
	 * @throws IOException
	 */
	private String send(String data) throws TransformerException {

		try {
			//urlLocation is set in applicationContext.xml
			url = new URL(urlLocation);
		}
		catch (MalformedURLException e) {
			throw new TransformerException("Problem creating URL for Opcrit.", e);
		}
		
		URLConnection connection = null;
		
		//append an id to the data
		data = setId(data);
		
		try {
			connection = url.openConnection();
			((HttpURLConnection)connection).setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setDoInput(true); 
			connection.setUseCaches(false);

			Map<String,String> requestData = new HashMap<String,String>();
			//Set the POST parameters according to http://sgdp.iop.kcl.ac.uk/opcritonline/
			requestData.put("formatin", "csv");
			requestData.put("formatout", "csv");
			requestData.put("opdata", data);
			connection.getOutputStream().write(encodeParameters(requestData));

			//Read in results
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String results = "";
			while (true) {
				String line = reader.readLine();
				if (line != null) {
					results += line;
				}
				else {
					break;		//no more input so stop
				}
			}
			reader.close();
			return results;
		}
		catch (IOException e) {
			throw new TransformerException("An IOException occurred when communicating with the Opcrit service. "+e);
		}
	}


	/**
	 * This method will take in the output of the Opcrit
	 * web service and format it to be read back into
	 * the ExternalDerivedEntry
	 * 
	 * @param results
	 * @param original for comparison with results
	 * @return formatted result
	 */
	/*
	 *  All three available return formats will provide the following data:
	 *
	 * 	 * A status flag, value 1 indicates the algorithm failed, value 2 indicates success.
	 *	 * The subject id submitted.
	 *	 * The original 90 items supplied (to allow you the ability to confirm the correct data was processed).
	 *	 * The 13 scale ratings.
	 *	 * A single field containing any notes, warnings, errors etc generated.
	 *
	 *	Using CSV the String returned will be of the form STATUS,ID,Q1,.....Q90,dsm3,dsm3r,....tsuang,notes.
	 *				(Content-Type:text/plain)
	 */
	private String importData(String results, String original) throws TransformerException {

		String[] output = results.split(",");
		String[] input  = original.split(",");

		int totalQ = 91; //total number of questions + id

		//check output[0] for status - this should be null if everything went well
		if ( !output[0].equals("2") ) {
			String notes = null;
			if (output.length == 92) {
				notes = output[91];		//retrieve the contents of the notes field if present
			}
			throw new TransformerException("Opcrit algorithm failed. Status given was "+output[0]+". The error given was "+notes);
		}

		/*
		 * Confirm that input and output Qs are the same, to ensure there were no errors during processing/transfer,
		 * by comparing the first 91 items, which should be identical (90 questions + id) and throw an 
		 * error if not. 
		 */
		for (int i = 0; i < totalQ-1; i++) {
			if ( !input[i].equals(output[i+2]) ) {
				throw new TransformerException("Input and output don't match! Input was "+input[i]+" and output was "+output[i]);
			}
		}

		//The fields returned by Opcrit
		String[] scales = {"DSM3", "DSM3R", "DSM4", "ICD10", "TAYLOR", "RDC", "FEIGHNER", 
				"CARPENTER", "SCHNEIDER", "FRENCH", "FARMER", "CROW", "TSUANG", "NOTES"};

		//Format the results string, complete with diagnosises
		String ratings = "";
		for (int i = 92; i < output.length-1; i++) {
			ratings = ratings+""+scales[i-totalQ-1]+": "+output[i]+" ("+ diagnosis.get(scales[i-totalQ-1]).get(output[i])+")\n";
		}
		ratings = ratings+ "\n"+ "Notes: "+output[output.length-1];
		
		return ratings;
	}

	/**
	 * Transform a Map of parameters for sending as a HTTP
	 * POST request into a byte[]
	 * 
	 * @param params
	 * @return bytes
	 */
	private byte[] encodeParameters(Map<String,String> params) {
		String opdata = "";
		for (String s: params.keySet()) {
			opdata = addParameter(s, params.get(s), opdata);
		}
		return opdata.getBytes();
	}

	/**
	 * Add the given key and value pair onto an output String in 
	 * the format required for a POST/GET HTTP request.
	 * 
	 * @param key
	 * @param value
	 * @param output
	 * @return output
	 */
	private String addParameter(String key, String value, String output) {
		try {
			URLEncoder.encode(key, "UTF-8");
			URLEncoder.encode(value, "UTF-8");
		}
		catch (Exception e) {
			System.out.println("URLEncode "+ e.getMessage());
		}
		if (output.length() > 0) {
			output = output + "&";
		}
		output = output + key + "=" + value;
		return output;
	}
	
	/**
	 * Opcrit expects an id to be provided along with the question answers
	 * @param data
	 * @return data with id
	 */
	private String setId(String data) {
		//generate unique code
        java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
        return guid +","+ data;
	}
	
	private String removeStdCodes(String data) throws TransformerException {
		String[] params = data.split(",");
		if ( params.length != 90 ){
			throw new TransformerException("transform: input data has the wrong number of elements; expected 90, is "+params.length);
		}

		
		StringBuilder cleanData = new StringBuilder();
		boolean first = true;
		for (String param: params) {
			System.out.println(param);
			//If a std code has been used, replace with a default value
			if (getStdCodes().contains(param)) {
				param = DEFAULT_VALUE;
			}
			if (param == null || param.equals("")) {
				param = DEFAULT_VALUE;
			}
			if (first) {
				first = false;
			}
			else {
				cleanData.append(",");
			}
			cleanData.append(param);
		}
		System.out.println(cleanData.toString());
		return cleanData.toString();
	}
	
	private List<String> getStdCodes() {
		List<String> stdCodes = new ArrayList<String>();
		stdCodes.add("960. Data not known");
		stdCodes.add("970. Not applicable");
		stdCodes.add("980. Refused to answer");
		stdCodes.add("999. Data unable to be captured");
		
		return stdCodes;
	}
	
	/**
	 * Create a list of diagnosis key->description for the various scales.
	 * 
	 * This is used to display human readable descriptions alongside the 
	 * results returned by Opcrit.
	 * 
	 * @return ratings
	 */
	private static Map<String, Map<String,String>> populateDiagnosis() {
		Map<String, Map<String,String>> diagnosis = new HashMap<String,Map<String,String>>();
		
		Map<String,String> carpenter = new HashMap<String,String>();
		carpenter.put("1", "Level 5 schizophrenia");
		carpenter.put("2", "Level 6 schizophrenia");
		
		Map<String,String> DSM3 = new HashMap<String,String>();
		DSM3.put("1", "Major depression");
		DSM3.put("2", "Mania");
		DSM3.put("3", "BP disorder");
		DSM3.put("4", "Mania with psychosis");
		DSM3.put("5", "Depression with psychosis");
		DSM3.put("6", "Bipolar with psychosis");
		DSM3.put("7", "Atypical psychosis");
		DSM3.put("8", "Schizophreniform");
		DSM3.put("9", "Schizophrenia");
		DSM3.put("10", "Paranoid disorder");
		
		Map<String,String> DSM3R = new HashMap<String,String>();
		DSM3R.put("1", "Major depressive disorder");
		DSM3R.put("2", "Major depressive disorder, moderate");
		DSM3R.put("3", "Major depressive disorder, severe");
		DSM3R.put("4", "Major depressive disorder with psychosis");
		DSM3R.put("5", "Hypomanic episode");
		DSM3R.put("6", "Manic episode");
		DSM3R.put("7", "Manic episode with psychosis");
		DSM3R.put("8", "Schizophrenia");
		DSM3R.put("9", "Schizophreniform disorder");
		DSM3R.put("10", "Schizoaffective disorder, depressive type");
		DSM3R.put("11", "Schizoaffective disorder, bipolar type");
		DSM3R.put("12", "Delusional disorder");
		DSM3R.put("13", "Psychotic disorder not otherwise specified");
		DSM3R.put("14", "Bipolar disorder");
		
		Map<String,String> DSM4 = new HashMap<String,String>();
		DSM4.put("1", "Major depressive disorder");
		DSM4.put("2", "Major depressive disorder, moderate");
		DSM4.put("3", "Major depressive disorder, severe");
		DSM4.put("4", "Major depressive disorder with psychosis");
		DSM4.put("5", "Hypomanic episode");
		DSM4.put("6", "Manic episode");
		DSM4.put("7", "Manic episode with psychosis");
		DSM4.put("8", "Schizophrenia");
		DSM4.put("9", "Schizophreniform disorder");
		DSM4.put("10", "Schizoaffective disorder, depressive type");
		DSM4.put("11", "Schizoaffective disorder, bipolar type");
		DSM4.put("12", "Delusional disorder");
		DSM4.put("13", "Psychosis not otherwise specified (atypical psychosis)");
		DSM4.put("14", "Bipolar I disorder");
		DSM4.put("15", "Bipolar II disorder");
		
		Map<String,String> ICD10 = new HashMap<String,String>();
		ICD10.put("1", "Mild depression disorder");
		ICD10.put("2", "Moderate depression disorder");
		ICD10.put("3", "Moderate depression with somatic syndrome");
		ICD10.put("4", "Severe depression disorder");
		ICD10.put("5", "Severe depression with psychotic symptoms");
		ICD10.put("6", "Hypomanic disorder");
		ICD10.put("7", "Manic disorder");
		ICD10.put("8", "Mania with psychosis");
		ICD10.put("9", "Bipolar Affective disorder");
		ICD10.put("10", "No longer used");
		ICD10.put("11", "Schizophrenia");
		ICD10.put("12", "Schizoaffective disorder, manic type");
		ICD10.put("13", "Schizoaffective disorder, depressed type");
		ICD10.put("14", "Schizoaffective disorder, bipolar type");
		ICD10.put("15", "Delusional disorder");
		ICD10.put("16", "Other non-organic psychotic syndrome");
		
		Map<String,String> RDC = new HashMap<String,String>();
		RDC.put("1", "Major depression");
		RDC.put("2", "Mania");
		RDC.put("3", "Bipolar I");
		RDC.put("4", "Schizo-affective / manic");
		RDC.put("5", "Schizo-affective / depressive");
		RDC.put("6", "Schizo-affective / bipolar");
		RDC.put("7", "Broad schizophrenia");
		RDC.put("8", "Narrow schizophrenia");
		RDC.put("9", "Unspecified functional psychosis");
		RDC.put("10", "Hypomania");
		RDC.put("11", "Bipolar II");
		
		Map<String,String> Schneider = new HashMap<String,String>();
		Schneider.put("1", "FRS-Schizophrenia");
		
		Map<String,String> Taylor = new HashMap<String,String>();
		Taylor.put("1", "Depression");
		Taylor.put("2", "Mania");
		Taylor.put("3", "BP disorder");
		Taylor.put("4", "Schizophrenia");
		
		Map<String,String> Tsuang = new HashMap<String,String>();
		Tsuang.put("1", "Paranoid");
		Tsuang.put("2", "Undifferentiated");
		Tsuang.put("3", "Hebephrenic");
		
		Map<String,String> Feighner = new HashMap<String,String>();
		Feighner.put("1", "Depression");
		Feighner.put("2", "Mania");
		Feighner.put("3", "Bipolar");
		Feighner.put("4", "Probable schizophrenia");
		Feighner.put("5", "Definite schizophrenia");
		Feighner.put("6", "Schizophrenia with secondary affective disorder-mania");
		Feighner.put("7", "Schizophrenia with secondary affective disorder-depression");
		Feighner.put("8", "Schizophrenia with secondary affective disorder-bipolar");
		
		Map<String,String> French = new HashMap<String,String>();
		French.put("1", "Interpretive psychosis");
		French.put("2", "Chronic hallucinatory psychosis");
		French.put("3", "Delusional attack");
		French.put("4", "Chronic schizophrenia");
		French.put("5", "Bouffee delirante");
		
		Map<String,String> Crow = new HashMap<String,String>();
		Crow.put("1", "Type I");
		Crow.put("2", "Mixed type");
		Crow.put("3", "Type II");

		Map<String,String> Farmer = new HashMap<String,String>();
		Farmer.put("1", "P type");
		Farmer.put("2", "h type");
		
		diagnosis.put("CARPENTER", carpenter);
		diagnosis.put("DSM3", DSM3);
		diagnosis.put("DSM3R", DSM3R);
		diagnosis.put("DSM4", DSM4);
		diagnosis.put("ICD10", ICD10);
		diagnosis.put("TAYLOR", Taylor);
		diagnosis.put("RDC", RDC);
		diagnosis.put("FEIGHNER", Feighner);
		diagnosis.put("SCHNEIDER", Schneider);
		diagnosis.put("FRENCH", French);
		diagnosis.put("FARMER", Farmer);
		diagnosis.put("CROW", Crow);
		diagnosis.put("TSUANG", Tsuang);
		
		return diagnosis;
	}
	
}

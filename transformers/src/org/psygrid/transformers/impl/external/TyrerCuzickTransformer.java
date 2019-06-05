/*
Copyright (c) 2006-2010, The University of Manchester, UK.

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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.psygrid.transformers.TransformerException;

/**
 * Class to convert data to and from the format required by the TyrerCuzick risk
 * calculator. Used to calculate the ExternalDerivedEntry field in the PROCAS
 * Questionnaire document.
 * 
 * @author Terry Child
 * 
 */
public class TyrerCuzickTransformer {

	private static final Log log = LogFactory.getLog(TyrerCuzickTransformer.class);

	private static final int UNKNOWN = -99;
	
	/**
	 * The windows executable extracted from the transformers.war
	 */
	private static File exe = null;

	/**
	 * Transform the fields on the questionnaire.
	 * 
	 * This code extracts the Tyrer-Cuzick windows exe file from the war containing this class.
	 * 
	 * It then converts the input data into an input file and calls the exe with this file as a
	 * command line parameter.
	 * 
	 * The exe writes an output file which is then read and the calculated risk value is returned.
	 * 
	 * @param data - a JSON formatted string containing all the data in the PROCAS Questionnaire document.
	 * @return the result of the Tyrer-Cuzick calculation.
	 */
	public static String transform(String data) throws TransformerException {

		log.info(data);

		// Lazily extract the executable from the transformers.war file if needed.
		synchronized(TyrerCuzickTransformer.class) {
			if(exe==null){
				try {
					exe = extractExe();
				} catch (IOException e) {
					throw new TransformerException("Unable to extract executable.",e);
				}
			}
		}
		
		// Write the supplied JSON formatted data to a temporary file.
		File input = null;
		try {
			input = writeInputFile(data);
		} catch (IOException e) {
			throw new TransformerException("Unable to build input file.",e);
		} catch (JSONException e) {
			throw new TransformerException("Unable to build input file.",e);
		}
		
		// Call the Tyrer-Cuzick executable - returning the output file.
		File output = null;
		try {
			output = callExe(exe,input);
		} catch (IOException e) {
			throw new TransformerException("Unable to execute risk calculation.",e);
		}

		// Parse the Tyrer-Cuzick output file for the result of the calculation.
		String result = "";
		try {
			String v = readOutputFile(output);
			try {
				double val = Double.parseDouble(v);
				// Convert to percentage and round to 2 decimal places 
				val = ((double)Math.round(val*10000))/100;
				result = Double.toString(val);
			} catch (NumberFormatException e) {
				throw new TransformerException("Unable to read output file.", e);
			}
		} catch (IOException e) {
			throw new TransformerException("Unable to read output file.", e);
		}

		// Delete the temporary input and output files.
//		input.deleteOnExit();
//		output.deleteOnExit();
		if(System.getProperty("tyrercuzick.keepfiles")==null){
			input.delete();
			output.delete();
		}
				
		return result;
	}

	/**
	 * Parse a JSON formatted string containing the contents of the Questionnaire document
	 * from the PROCAS study.
	 * 
	 * The data is written to a temporary file in the format required for the Tyrer-Cuzick windows exe.
	 * 
	 * @param input JSON formatted document data
	 * @return a file containing the Tyrer-Cuzick input data
	 * @throws IOException
	 * @throws JSONException 
	 * @throws JSONException 
	 */
	private static File writeInputFile(String input) throws IOException, JSONException {

		// The translator is responsible for mangling the data into
		// a format suitable for input to the tyrer-cuzick exe.
		// This includes applying some heuristics when the data don't quite fit.
		Translator t = new Translator(input);

		String sxnumber = t.getSXNumber();
		
//		File tempFile = File.createTempFile("tyrercuzickinput", ".txt");
		String tempDir = System.getProperty("java.io.tmpdir");
		File tempFile = new File(tempDir,"tyrercuzickinput-"+sxnumber+".txt");
		if(tempFile.exists()) tempFile.delete();
		PrintStream f = new PrintStream(new FileOutputStream(tempFile));

		// Hardwire Tyrer-Cuzick version info
		f.println("v6"); 
		
		// The number of rows in the input file - there is one row per person.
		// We have only one row as this class calculates the value for a single participant at a time.
		f.println("1");   
				
		// The id is not significant
		String id = "*";
		
		int age = t.getAge();
		int menarche = t.getMenarche();
		int parous = t.getParous();
		int Age_1 = t.getAge_1();
		int Menostat = t.getMenostat();
		int Menopause_age = t.getMenopause_age();
		double Ht = t.getHt();				
		double Wt = t.getWt();
		int Hyper = t.getHyper();	
		int AH = t.getAH(); 
		int LCIS = t.getLCIS();
		int Ovarian_status = t.getOvarian_status(); 
		int Ageoc = t.getAgeoc();
		int Ashkanazi = t.getAshkanazi();
		int Hrt_use = t.getHrt_use();
		int Hrt_type = t.getHrt_type();
		double Hrt_length = t.getHrt_length();
		double Inuselength = t.getInuselength();
		double Hrt_last_use = t.getHrt_last_use();
		int Genetic_test = 0; // Not in document
		int Father_genetic_test = 0; // Not in document
		int Mother_affected_status = t.getMother_affected_status();
		int Mother_bilateral_status = t.getMother_bilateral_status();
		int Mother_ovarian_status = t.getMother_ovarian_status();
		int Mother_age = t.getMother_age();
		int Mother_bilateral_age = t.getMother_bilateral_age();
		int Mother_ovarian_age = t.getMother_ovarian_age();
		int Mother_genetic_test_result = t.getMother_genetic_test_result();

		
		f.print(id+" "+age+" "+menarche+" "+parous+" "+Age_1+" "+Menostat+" "+Menopause_age+" "+Ht+" "+Wt+" ");		
		f.print(Hyper+" "+AH+" "+LCIS+" "+Ovarian_status+" "+Ageoc+" "+Ashkanazi+" "+Hrt_use+" "+Hrt_type+" ");
		f.print(Hrt_length+" "+Inuselength+" "+Hrt_last_use+" "+Genetic_test+" "+Father_genetic_test+" ");
		f.print(Mother_affected_status+" "+Mother_bilateral_status+" "+Mother_ovarian_status+" ");
		f.print(Mother_age+" "+Mother_bilateral_age+" "+Mother_ovarian_age+" "+Mother_genetic_test_result+" ");
		
		processSisters(t,f);
		processOtherRelationship(Translator.PATERNAL_GRAN,0,t,f);
		processOtherRelationship(Translator.MATERNAL_GRAN,0,t,f);
		processPaternalAunts(t,f);
		processMaternalAunts(t,f);
		processMultipleRelationship(Translator.DAUGHTER,t,f);
		
		// Second degree relatives
		processNieces(t,f);
		processMultipleRelationship(Translator.PATERNAL_HALF_SISTER,t,f);
		processMultipleRelationship(Translator.MATERNAL_HALF_SISTER,t,f);

		processPaternalCousins(t,f);
		processMaternalCousins(t,f);

		int N_daughters_of_paternal_uncles=0;
		int N_daughters_of_maternal_uncles=0;

		f.print(N_daughters_of_paternal_uncles+" "+N_daughters_of_maternal_uncles);
		f.println();
		
		close(f);
		return tempFile;
	}

	private static void processSisters(Translator t,PrintStream f){
		int N_sisters = t.getN_sisters();
		f.print(N_sisters+" ");
		for(int i=0;i<N_sisters;i++){
			int sister_affected_status = t.getSister_affected_status(i);
			int sister_bilateral_status = t.getSister_bilateral_status(i);
			int sister_ovarian_status = t.getSister_ovarian_status(i);
			int sister_age = t.getSister_age(i);
			int sister_bilateral_age = t.getSister_bilateral_age(i);
			int sister_ovarian_age = t.getSister_ovarian_age(i);
			int sister_genetic_test_result = t.getSister_genetic_test_result(i);
			f.print(sister_affected_status+" "+sister_bilateral_status+" "+sister_ovarian_status+" ");
			f.print(sister_age+" "+sister_bilateral_age+" "+sister_ovarian_age+" "+sister_genetic_test_result+" ");
		}
	}
	
	private static void processOtherRelationship(int type, int index, Translator t,PrintStream f){
			int affected_status = t.getOtherRelativeAffectedStatus(type,index);
			int bilateral_status = t.getOtherRelativeBilateralStatus(type,index);
			int ovarian_status = t.getOtherRelativeOvarianStatus(type,index);
			int age = t.getOtherRelativeAge(type,index);
			int bilateral_age = t.getOtherRelativeBilateralAge(type,index);
			int ovarian_age = t.getOtherRelativeOvarianAge(type,index);
			int genetic_test_result = t.getOtherRelativeGeneticTestResult(type,index);
			f.print(affected_status+" "+bilateral_status+" "+ovarian_status+" "+age+" ");
			f.print(bilateral_age+" "+ovarian_age+" "+genetic_test_result+" ");
	}

	private static void processMultipleRelationship(int type, Translator t,PrintStream f){
		int num = t.getNumOtherRelatives(type);
		f.print(num+" ");
		for(int i=0;i<num;i++){
			processOtherRelationship(type, i, t, f);
		}
	}

	
	// Assume that all paternal cousins are daughters of paternal aunts.
	// There is a need to add extra paternal aunts if there are more paternal cousins
	// than there are paternal aunts.
	private static void processPaternalAunts(Translator t,PrintStream f){
		int numPaternalAunt = t.getNumOtherRelatives(Translator.PATERNAL_AUNT);
		int numPaternalCousin = t.getNumOtherRelatives(Translator.PATERNAL_COUSIN);
		if(numPaternalCousin>numPaternalAunt){
			numPaternalAunt=numPaternalCousin;
		}
		f.print(numPaternalAunt+" ");
		for(int i=0;i<numPaternalAunt;i++){
			processOtherRelationship(Translator.PATERNAL_AUNT, i, t, f);
		}
	}
	
	// Assume that all maternal cousins are daughters of maternal aunts.
	// There is a need to add extra maternal aunts if there are more maternal cousins
	// than there are maternal aunts.
	// We also need to assume that 'cousin' means maternal cousin.
	private static void processMaternalAunts(Translator t,PrintStream f){
		int numMaternalAunt = t.getNumOtherRelatives(Translator.MATERNAL_AUNT);
		int numMaternalCousin = t.getNumOtherRelatives(Translator.MATERNAL_COUSIN);
		int numCousin = t.getNumOtherRelatives(Translator.COUSIN);
		numMaternalCousin+=numCousin;
		if(numMaternalCousin>numMaternalAunt){
			numMaternalAunt=numMaternalCousin;
		}
		f.print(numMaternalAunt+" ");
		for(int i=0;i<numMaternalAunt;i++){
			processOtherRelationship(Translator.MATERNAL_AUNT, i, t, f);
		}
	}
	
	
	// The relationship information between sisters and nieces is not captured.
	// So share out the nieces equally between the sisters and a brother
	private static void processNieces(Translator t,PrintStream f){
		int numNieces = t.getNumOtherRelatives(Translator.NIECE);
		int numSisters = t.getN_sisters();
		int numSiblings=numSisters;
		if(numNieces>numSisters){
			numSiblings+=1; // Add one for a single brother
		}
		int nieceIndex = 0;
		f.print(numSiblings+" ");
		for(int i=0;i<numSiblings;i++){
			// Work out the number of nieces to put under this sibling
			int nieceCount = numNieces/numSiblings;
			if(i<numNieces%numSiblings) nieceCount++;
			f.print(nieceCount+" ");
			for(int j=0;j<nieceCount;j++){
				processOtherRelationship(Translator.NIECE, nieceIndex, t, f);
				nieceIndex++;
			}
		}
	}

	private static void processPaternalCousins(Translator t,PrintStream f){
			// There is one aunt for each cousin - see above
			int numAunt = t.getNumOtherRelatives(Translator.PATERNAL_COUSIN);
			f.print(numAunt+" ");
			for(int i=0;i<numAunt;i++){
				f.print(1+" "); // one daughter per aunt
				processOtherRelationship(Translator.PATERNAL_COUSIN, i, t, f);
			}
		}
	
	private static void processMaternalCousins(Translator t,PrintStream f){
		// There is one aunt for each maternal cousin or cousin - see above
		int numMaternalCousin = t.getNumOtherRelatives(Translator.MATERNAL_COUSIN);
		int numCousin = t.getNumOtherRelatives(Translator.COUSIN);
		int numAunt = numMaternalCousin+numCousin;
		f.print(numAunt+" ");
		for(int i=0;i<numMaternalCousin;i++){
			f.print(1+" "); // one daughter per aunt
			processOtherRelationship(Translator.MATERNAL_COUSIN, i, t, f);
		}
		for(int i=0;i<numCousin;i++){
			f.print(1+" "); // one daughter per aunt
			processOtherRelationship(Translator.COUSIN, i, t, f);
		}
	}

	
	private static String readOutputFile(File output) throws IOException {
		BufferedReader r = new BufferedReader(new FileReader(output));
		String line = r.readLine();
		close(r);
		// Split on whitespace
		String[] values = line.split("\\s");
		String result="";
		if(values.length>2) result = values[1];
		return result;
	}

	private static File extractExe() throws IOException {

		File tempFile = File.createTempFile("RiskFileCalc", ".exe");
		tempFile.deleteOnExit();

		log.info("Extracting executable to:" + tempFile);

		InputStream inputStream = Thread.currentThread()
				.getContextClassLoader().getResourceAsStream("RiskFileCalc.exe");
		OutputStream fileStream = new FileOutputStream(tempFile);

		try {
			final byte[] buf;
			int i;

			buf = new byte[1024];
			i = 0;

			while ((i = inputStream.read(buf)) != -1) {
				fileStream.write(buf, 0, i);
			}
		} finally {
			close(inputStream);
			close(fileStream);
		}

		return tempFile;
	}

	private static void close(final Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (final IOException ex) {
				log.warn("Unable to close stream", ex);
			}
		}
	}
	
	/**
	 * Call the RiskFileCalc.exe with the input file as a parameter.
	 * 
	 * @param exe the executable file.
	 * @param input the input file.
	 * @return the output file.
	 * @throws IOException if unable to create the output file.
	 */
	private static File callExe(File exe,File input) throws IOException {

		File tempFile = File.createTempFile("tyrercuzickoutput", ".tmp");

		Runtime runtime = Runtime.getRuntime();
		
		String execute = exe.getPath()+" /i "+input.getPath()+" /o "+tempFile.getPath();
		
	    Process proc = runtime.exec(execute);
	
	    try {
	        if (proc.waitFor() != 0) {
	            log.warn("RiskFileCalc.exe exit value = " + proc.exitValue());
	        }
	    }
	    catch (InterruptedException e) {
	        log.warn("RiskFileCalc.exe was interrupted",e);
	    }
	    
	    return tempFile;
	}
	
	public static void main(final String[] args) throws Exception {
		JSONObject job = new JSONObject().put("hello", "world");
		job.put("height", 10);

		List<String> list = new ArrayList<String>();
		list.add("one");
		list.add("two");
		list.add("three");
		job.put("mylist", list);
		
		Map<String,Double> map = new HashMap<String,Double>();
		map.put("drink", 20.0);
		map.put("food", 30.3);
		job.put("mymap", map);

		String in=job.toString();
		System.out.println("result="+in);
		
		JSONObject injob = new JSONObject(in);
		String hello = injob.getString("hello");
		Object obj = injob.get("hello");
		Object obj2 = injob.get("height");
		Object obj3 = injob.getJSONObject("mymap").getInt("food");
		
		System.out.println("injob="+injob);		
		System.out.println("hello="+hello);		
		System.out.println("obj="+obj);		
		System.out.println("obj2="+obj2);		
		System.out.println("obj3="+obj3);		

	}

	/**
	 * Translates data from the PROCAS study Questionnaire document into variables required
	 * for the Tyrer-Cuzick risk calculation program.
	 * 
	 * The javadoc comments on the methods are taken from the 'List of variables and required formats-corrected.doc' file
	 * which is part of the PROCAS specification.
	 */
	static private class Translator {

		// 2009-10-28 00:00:00.0
		private static final DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

		private static final String REGISTRATION_NAME = "Intial Risk Assessment-Registration";
		private static final String QUESTIONNAIRE_NAME = "Intial Risk Assessment-Questionnaire";
		private static final String ADDITIONAL_RISK_FACTORS_NAME = "Intial Risk Assessment-Additional Risk Factors";
		
		private static int PATERNAL_GRAN = 0;
		private static int MATERNAL_GRAN = 1;
		private static int PATERNAL_AUNT = 2;
		private static int MATERNAL_AUNT = 3;
		private static int DAUGHTER = 4;
		private static int NIECE = 5;
		private static int PATERNAL_HALF_SISTER = 6;
		private static int MATERNAL_HALF_SISTER = 7;
		private static int PATERNAL_COUSIN = 8;
		private static int MATERNAL_COUSIN = 9;
		private static int COUSIN = 10;

		/**
		 * Container for the data from the whole record.
		 */
		JSONObject record = null;

		/**
		 * The data from the registration document.
		 */
		JSONObject registration = null;

		/**
		 * The data from the questionnaire.
		 */
		JSONObject questionnaire = null;

		/**
		 * Container for the data from the additional risk factors document.
		 */
		JSONObject additionalRiskFactors = null;
		
		Translator(String input) throws JSONException{
			record = new JSONObject(input);
			registration = getDocument(REGISTRATION_NAME);
			questionnaire = getDocument(QUESTIONNAIRE_NAME);
			additionalRiskFactors = getDocument(ADDITIONAL_RISK_FACTORS_NAME);
		}
		
		private JSONObject getDocument(String documentName){
			JSONObject result = null;
			try{
				result = record.getJSONObject(documentName);
			}
			catch(JSONException ex){
				result = new JSONObject();
			}
			return result;
		}

		String getSXNumber(){
			String result="";
			try {
				result = registration.getString("SxNumber");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			return result;
		}
		
		/**
		 * Current age (of patient)	In years (as an integer). Set to -99 if missing.
		 */
		int getAge(){ 
			int value = UNKNOWN;
			try {
				String dobStr = registration.getString("DateOfBirth");
				Date dob = dateFormatter.parse(dobStr);
				String firstStr = registration.getString("DateOfFirstOfferedAppointment");
				Date first = dateFormatter.parse(firstStr);
				value = calcAge(dob,first);				
			} catch (JSONException e) {
				// Ignore this
				log.debug(e);
			} catch (ParseException e) {
				// Ignore this
				log.debug(e);
			}
			return value;
		}
		
		/**
		 * Calculate the age at the date of first offered appointment
		 */
		private int calcAge(Date born,Date datum) {
			Calendar now = Calendar.getInstance();
			Calendar dob = Calendar.getInstance();
			dob.setTime(born);
			now.setTime(datum);
			int year1 = now.get(Calendar.YEAR);
			int year2 = dob.get(Calendar.YEAR);
			int age = year1 - year2;
			int month1 = now.get(Calendar.MONTH);
			int month2 = dob.get(Calendar.MONTH);
			if (month2 > month1) {
				age--;
			} else if (month1 == month2) {
				int day1 = now.get(Calendar.DAY_OF_MONTH);
				int day2 = dob.get(Calendar.DAY_OF_MONTH);
				if (day2 > day1) {
					age--;
				}
			}
			return age;
		}


		/**
		 * Age at menarche	In years (integer). Set to -99 if missing.
		 */
		int getMenarche(){ return getInt("age_menarche"); }

		/**
		 * Number of live births	0=Nulliparous, 1=Parous, 2= Unknown
		 */
		int getParous(){ 
			int parous = getInt("any_children"); 
			if(parous==UNKNOWN) parous = 2;
			return parous; 
		}

		/**
		 * Age at first birth	Years (integer). Set to -99 if missing.
		 */
		int getAge_1(){ return getInt("how_old_first_pregnancy"); }

		/**
		 * Menopausal status	0=pre-menopausal, 1=peri-menopausal, 2=post-menopausal, 3=Unknown
		 * 
		 * Additional:
		 * If a woman has had a hysterectomy and bilateral oophorectomy (both ovaries removed) then she's post-menopausal from the time of surgery.
		 * If one or both ovaries were retained, then when I analyse I tend to assume such a woman is pre-menopausal if younger than 50 and post menopausal if over 50.
		 * 
		 * Additional:
		 * 
		 * This code now implements the following logic:
		 * 
		 *     if Q_MENOPAUSAL_STATUS is post-menopausal set TC_MENO_STAT = post-menopausal; END;
		 *     if Q_HYST is true and Q_OVARIES_REMOVED is 2 set TC_MENO_STAT = post-menopausal
		 *     if Q_HYST is true and Q_OVARIES_REMOVED is not 2 and Q_AGE < 50  set TC_MENO_STAT = pre-menopausal
		 *     if Q_HYST is true and Q_OVARIES_REMOVED is not 2 and Q_AGE >= 50 set TC_MENO_STAT = post-menopausal
		 *     if Q_HYST is not true set TC_MENO_STAT = Q_MENOPAUSAL_STATUS
		 * 
		 */
		int getMenostat(){ 
			int tc_meno_stat = 3;
			final int q_menopausal_status = getInt("menopausal_status");
			if(q_menopausal_status==2){
				tc_meno_stat = 2; // Postmenopausal
			}
			else {
				final int q_hyst=getInt("hyst");
				final int q_ovaries_removed=getInt("ovaries_removed");
				if(q_hyst==1) {
					if(q_ovaries_removed==2){
						tc_meno_stat = 2; // Postmenopausal
					}
					else {
						final int q_age = getAge();
						if(q_age<50) tc_meno_stat=0; // Premenopausal
						else tc_meno_stat=2; // Postmenopausal
					}
				}
				else {
					tc_meno_stat = q_menopausal_status;
					if (tc_meno_stat==UNKNOWN) tc_meno_stat = 3;
				}
			}
			return tc_meno_stat; 
		}

		/**
		 * Age at menopause	Years (integer)
		 * Set to -99 if pre or peri menopausal or if menopausal status is unknown.
		 * Set to 50 if post-menopausal but age at menopause not known.
		 * 
		 * Additional
		 * If a woman has had a hysterectomy and bilateral oophorectomy (both ovaries removed) then she's post-menopausal from the time of surgery.
		 * If one or both ovaries were retained, then when I analyse I tend to assume such a woman is pre-menopausal if younger than 50 and post menopausal if over 50.
		 * 
		 * Additional:
		 * 
		 * This code now implements the following logic:
		 * 
		 * TC_MENO_AGE:
		 *     if TC_MENO_STAT is pre-menopausal or TC_MENO_STAT is peri-menopausal then set TC_MENO_AGE = Q_AGE_MENOPAUSE                   
		 *     if TC_MENO_STAT is post-menopausal and Q_HYST is true and Q_OVARIES_REMOVED is 2 set TC_MENO_AGE = minimum-of(Q_AGE_HYSTERECTOMY,Q_AGE_MENOPAUSE)
		 *     if TC_MENO_STAT is post-menopausal and not(Q_HYST is true and Q_OVARIES_REMOVED is 2) set TC_MENO_AGE = Q_AGE_MENOPAUSE
		 *     if TC_MENO_AGE is UNKNOWN and TC_MENO_STAT is post-menopausal TC_MENO_AGE = 50; 
		 */
		int getMenopause_age() {
			int tc_meno_age = UNKNOWN;
			final int tc_meno_stat = getMenostat();
			final int q_age_menopause = getInt("age_menopause");
			if(tc_meno_stat == 0 || tc_meno_stat == 1){
				tc_meno_age = q_age_menopause;
			}
			if(tc_meno_stat == 2){
				int q_hyst=getInt("hyst");
				int q_ovaries_removed=getInt("ovaries_removed");
				int q_age_hysterectomy=getInt("age_hysterectomy");
				if(q_hyst==1 && q_ovaries_removed==2){
						tc_meno_age = minKnownValue(q_age_hysterectomy,q_age_menopause);
				}
				else {
					tc_meno_age = q_age_menopause;
				}
				if (tc_meno_age == UNKNOWN) tc_meno_age=50;
			}
			return tc_meno_age;
		}
		
		/**
		 * Calculated the minimum known value of two parameters.
		 * 
		 * If neither value is known then UNKNOWN is returned.
		 * If only one parameter is known - its value is returned.
		 * If both parameters are known then the minimum is returned.
		 * 
		 */
		static final int minKnownValue(int a, int b){
			if(a==UNKNOWN && b==UNKNOWN) return UNKNOWN;
			if(a==UNKNOWN) return b;
			if(b==UNKNOWN) return a;
			return Math.min(a, b);
		}
		
		/**
		 * Height	Height (in metres, to nearest cm i.e. 1.65) Set to -99 if missing.
		 */
		double getHt(){
			int height_ft = getInt("height_ft");
			int height_in = getInt("height_in");
			int height_m = getInt("height_m");
			int height_cm = getInt("height_cm");
			
			double Ht = UNKNOWN;
			if(height_ft!=UNKNOWN) {
				Ht= height_ft*12*2.54;
				if(height_in!=UNKNOWN){
					Ht+=2.54*height_in;
				}
				Ht= Ht/100;
			}
			else {
				if(height_m!=UNKNOWN){
					Ht=(double)height_m;
					if(height_cm!=UNKNOWN){
						Ht+=((double)height_cm)/100;
					}
				} else {				
					if(height_cm!=UNKNOWN){
						Ht=((double)height_cm)/100;
					}
				}
			}
			return Ht;
		}

		/**
		 * Weight	Weight (in kg). Set to -99 if missing.
		 */
		double getWt(){
			int weight_st = getInt("weight_st");
			int weight_lb = getInt("weight_lb");
			int weight_kg = getInt("weight_kg");

			double Wt = UNKNOWN;
			if(weight_st!=UNKNOWN) {
				Wt = weight_st*6.35;
				if(weight_lb!=UNKNOWN){
					Wt+=((double)weight_lb)/2.2;
				}
			}
			else Wt=weight_kg;
			return Wt;
		}

		/**
		 * Ashkanazi Jewish heritage	1 if yes. Set to 0 if missing or unknown.
		 */
		int getAshkanazi(){
			int Ashkanazi = getInt("jewish_ashkanazi");
			if(Ashkanazi==UNKNOWN) Ashkanazi=0;
			return Ashkanazi;
		}

		/**
		 * Hyper	History of hyperplasia
		 * Set to 1 if woman has a history of hyplasia. Set to 0 if missing or unknown.
		 */
		int getHyper(){
			int hyper = getInt(additionalRiskFactors,"hyper");
			if(hyper==UNKNOWN) hyper=0;
			return hyper;
		}

		/**
		 * AH	History of atypical hyperplasia	
		 * Set to 1 if woman has a history of atypical hyplasia. Set to 0 if missing or unknown.		 
		 * */
		int getAH(){
			int AH = getInt(additionalRiskFactors,"AH");
			if(AH==UNKNOWN) AH=0;
			return AH;
		}

		/**
		 * LCIS	History of LCIS	
		 * Set to 1 if woman has a history of LCIS. Set to 0 if missing or unknown.
		 */
		int getLCIS(){
			int LCIS = getInt(additionalRiskFactors,"LCIS");
			if(LCIS==UNKNOWN) LCIS=0;
			return LCIS;
		}

		/**
		 * Ovarian status	History of ovarian cancer	
		 * Set to 1 if woman has had ovarian cancer. Set to 0 if missing or unknown.
		 */
		int getOvarian_status(){
			int Ovarian_status = getInt(additionalRiskFactors,"ovarian_status");
			if(Ovarian_status==UNKNOWN) Ovarian_status=0;
			return Ovarian_status;
		}

		/**
		 * Ageoc	Age at diagnosis of ovarian cancer	Years (integer). 
		 * Set to -99 if missing.
		 */
		int getAgeoc(){
			int Ageoc = getInt(additionalRiskFactors,"ageoc");
			return Ageoc;
		}


		/**
		Hrt_use
		0=Never
		1=Previous user (more than 5 years ago)
		2=Previous user (less than 5 years ago)
		3=Current user
		*/
		int getHrt_use(){
			int HRT_ever = getInt("HRT_ever");
			int still_on_HRT = getInt("still_on_HRT");
			double time_stopped_HRT_yrs = getDouble("time_stopped_HRT_yrs");
//			int time_stopped_HRT_mths = getInt("time_stopped_HRT_mths");
	
			int Hrt_use = 0;
	
			if(HRT_ever==1){
				if(still_on_HRT==1){
					Hrt_use = 3;
				} else {
					if(time_stopped_HRT_yrs >= 5){
						Hrt_use = 1;					
					}
					else {
						Hrt_use = 2;										
					}
				}
			} 
			return Hrt_use;
		}

		/**
		Hrt_type - Type of hrt taken
		0=oestrogen <- Amended from original word document (was 1=oestrogen,2=combined)
		1=combined
		Set to 0 if type is unknown or if hrt_use==0 or 1.
		*/
		int getHrt_type(){
			int Hrt_type = getInt("HRT_type");
			int Hrt_use = getHrt_use();
			if(Hrt_type==UNKNOWN || Hrt_use==0 || Hrt_use==1) Hrt_type=1;
			return Hrt_type;
		}

		/**
		 Hrt_length	- Length of time taking HRT in the past	
		 Number of years (to 1dp if required):
		 i.e. 18 months should be entered as 1.5 years. 
		 Set to 0 if not relevant, missing or unknown.
		 */
		double getHrt_length(){
			double years_HRT = getDouble("years_HRT");
			int months_HRT = getInt("months_HRT");
	
			double Hrt_length = 0;
			if(years_HRT!=UNKNOWN){
				Hrt_length = years_HRT;
			}
			if(months_HRT!=UNKNOWN){
				Hrt_length += ((double)months_HRT)/12;			
			}
			return Hrt_length;
		}

		/**
		Inuselength	- Length of time woman intends to use HRT in the future (if current user)	
		Number of years (to 1dp if required): i.e. 18 months should be entered as 1.5 years. 
		Set to 0 if not relevant, missing or unknown.
		*/
		double getInuselength(){
			double inuselength = getInt("inuselength");
			if(inuselength==UNKNOWN) inuselength = 0;
			return inuselength;
		}

		/**
		Hrt_last_use - Time since HRT last used (if previous HRT user & time since last use < 5 years)	
		Number of years (to 1dp if required): i.e. 18 months should be entered as 1.5 years. 
		Set to 0 if not relevant, missing or unknown.
		*/
		double getHrt_last_use(){
			double Hrt_last_use = 0;
			int HRT_ever = getInt("HRT_ever");
			double Time_stopped_HRT_yrs = getDouble("time_stopped_HRT_yrs");
			int Time_stopped_HRT_mths = getInt("time_stopped_HRT_mths");
			if(HRT_ever==1){
				if(Time_stopped_HRT_yrs!=UNKNOWN){
					Hrt_last_use = Time_stopped_HRT_yrs;
				}
				if(Time_stopped_HRT_mths!=UNKNOWN){
					Hrt_last_use += ((double)Time_stopped_HRT_mths)/12;
				}
				if(Hrt_last_use>=5) Hrt_last_use = 0;
			}
			return Hrt_last_use;
		}

		/**
		Mother_affected_status - Has the mother had breast cancer?	
		0 = no 
		1 = yes
		Set to 0 if missing or unknown.
		 */
		int getMother_affected_status(){
			int affected_status = 0;
			int cancer = getInt("mother_cancer");
			int breast_cancer_1 = getInt("mother_breast_cancer_1");
			int bilateral_status = getInt("mother_bilateral_status");
			int ovarian_cancer = getInt("mother_ovarian_cancer");
			if(cancer==1 && breast_cancer_1 == UNKNOWN && bilateral_status == UNKNOWN && ovarian_cancer == UNKNOWN){
				affected_status=1;
			}
			else if(breast_cancer_1==1 || bilateral_status==1) {
				affected_status=1;					
			}
			return affected_status;					
		}

		/**
		 Mother_age	- Age at which mother developed breast cancer or current age if has not developed bc	
		 Age in years (integer)
		 Set to -99 if not relevant, missing or unknown.
		 */
		int getMother_age(){
			int age = UNKNOWN;
			int cancer = getInt("mother_cancer");
			int breast_cancer_1 = getInt("mother_breast_cancer_1");
			int age_breast_cancer_1 = getInt("mother_age_breast_cancer_1");  
			int bilateral_status = getInt("mother_bilateral_status");
			int bilateral_age = getInt("mother_bilateral_age");  
			int ovarian_cancer = getInt("mother_ovarian_cancer");
			int age_mother = getInt("age_mother");
			
			age=age_mother;

			if(cancer==1 && breast_cancer_1 == UNKNOWN && bilateral_status == UNKNOWN && ovarian_cancer == UNKNOWN){
				age = age_breast_cancer_1;  
			}
			else if(age_breast_cancer_1!=UNKNOWN && bilateral_age!=UNKNOWN) {
				age = Math.min(age_breast_cancer_1, bilateral_age);		
			}
			else if(age_breast_cancer_1!=UNKNOWN) {
				age = age_breast_cancer_1;		
			}
			else if(bilateral_age!=UNKNOWN){
				age = bilateral_age;
			}
			return age;					
		}
		
		/**
		Mother_bilateral_status	- Was the mother’s breast cancer bilateral?	0 = no 1=yes
		Set to 0 if not relevant, missing or unknown.
		 */
		int getMother_bilateral_status(){
			int bilateral_status = 0;
			bilateral_status = getInt("mother_bilateral_status");  
			if(bilateral_status==UNKNOWN) bilateral_status = 0;
			return bilateral_status;					
		}

		/**
		Mother_bilateral_age	
		Age at which mother developed bilateral breast cancer	
		Age in years (integer)
		Set to -99 if not relevant, missing or unknown.
		*/
		int getMother_bilateral_age(){ 
			return getInt("mother_bilateral_age");  
		}

		/**
		 Mother_ovarian_status - Has the mother had ovarian cancer?	0 = no 1=yes
		 Set to 0 if missing or unknown.
		 */
		int getMother_ovarian_status(){
			int Mother_ovarian_status = getInt("mother_ovarian_cancer");
			if(Mother_ovarian_status==UNKNOWN) Mother_ovarian_status = 0;
			return Mother_ovarian_status;
		}

		/**
		 Mother_ovarian_age - Age at which mother developed ovarian cancer	
		 Age in years (integer)
		 Set to -99 if not relevant, missing or unknown.
		 */
		int getMother_ovarian_age(){
			return getInt("mother_age_ovarian_cancer");						
		}

		/**
		Mother_genetic_test_result	Genetic testing of the woman’s mother	
		0 = Never
		1= Test result negative
		2= BRCA1
		3= BRCA2
		Set to 0 if unknown.
		 */
		int getMother_genetic_test_result(){
			return 0; // Not in document
		}

		/**
		N_sisters - Number of sisters	
		Number (integer, max for visual display is 5 but others will be included in the calculation)
		 */
		int getN_sisters(){
			// Should we use the number of actual entries in the Sister composite entry instead of the 'Sisters' entry?
			int sisters = getInt("sisters");
			if(sisters==UNKNOWN) sisters = 0;
			if(sisters>5) sisters = 5;
			return sisters;
		}

		/**
		 * Has the sister had breast cancer?	0 = no 1=yes
		 * Set to 0 if missing or unknown, or number of sisters=0
		 * Get the affected status of the Nth sister
		 */
		int getSister_affected_status(int index){
			int affected_status = 0;
			try { 
				JSONObject sister = questionnaire.getJSONArray("sister").getJSONObject(index);
				int cancer = getInt(sister,"cancer");
				int breast_cancer_1 = getInt(sister,"breast_cancer_1");
				int bilateral_status = getInt(sister,"bilateral_status");
				int ovarian_cancer = getInt(sister,"ovarian_cancer");
				if(cancer==1 && breast_cancer_1 == UNKNOWN && bilateral_status == UNKNOWN && ovarian_cancer == UNKNOWN){
					affected_status=1;
				}
				else if(breast_cancer_1==1 || bilateral_status==1) {
					affected_status=1;					
				}
			}
			catch (JSONException e) {
				// Ignore for now
			}
			return affected_status;					
		}

		/** Was the sister’s breast cancer bilateral?	
		 * 0 = no 1=yes
		 * Set to 0 if not relevant, missing or unknown, or number of sisters=0.
		 */
		int getSister_bilateral_status(int index){
			int bilateral_status = 0;
			try {
				JSONObject sister = questionnaire.getJSONArray("sister").getJSONObject(index);
				bilateral_status = getInt(sister,"bilateral_status");  
				if(bilateral_status==UNKNOWN) bilateral_status = 0;
			}
			catch (JSONException e) {
				// Ignore for now
			}
			return bilateral_status;					
		}

		/**
		 * Has the sister had ovarian cancer?	
		 * 0 = no 1=yes
		 * Set to 0 if missing or unknown, or number of sisters=0.
		 */
		int getSister_ovarian_status(int index){
			int sister_ovarian_status = 0;
			try {
				JSONObject sister = questionnaire.getJSONArray("sister").getJSONObject(index);
				sister_ovarian_status = getInt(sister,"ovarian_cancer");  
				if(sister_ovarian_status==UNKNOWN) sister_ovarian_status = 0;
			}
			catch (JSONException e) {
				// Ignore for now
			}
			return sister_ovarian_status;					
		}
		
		/**
		 * sister_age	Age at which sister developed breast cancer or current age if sister has not has bc
		 * Age in years (integer)
		 * Set to -99 if not relevant, missing or unknown.
		 */
		int getSister_age(int index){
			int age = UNKNOWN;
			try {
				JSONObject sister = questionnaire.getJSONArray("sister").getJSONObject(index);
				int cancer = getInt(sister,"cancer");
				int breast_cancer_1 = getInt(sister,"breast_cancer_1");
				int age_breast_cancer_1 = getInt(sister,"age_breast_cancer_1");  
				int bilateral_status = getInt(sister,"bilateral_status");
				int bilateral_age = getInt(sister,"bilateral_age");  
				int ovarian_cancer = getInt(sister,"ovarian_cancer");
				int sister_age = getInt(sister,"age");
				
				age = sister_age;
				
				if(cancer==1 && breast_cancer_1 == UNKNOWN && bilateral_status == UNKNOWN && ovarian_cancer == UNKNOWN){
					age = age_breast_cancer_1;  
				}
				else if(age_breast_cancer_1!=UNKNOWN && bilateral_age!=UNKNOWN) {
					age = Math.min(age_breast_cancer_1, bilateral_age);		
				}
				else if(age_breast_cancer_1!=UNKNOWN) {
					age = age_breast_cancer_1;		
				}
				else if(bilateral_age!=UNKNOWN){
					age = bilateral_age;
				}
			}
			catch (JSONException e) {
				// Ignore for now
			}
			return age;					
		}
		
		/**
		 * Age at which sister developed bilateral breast cancer	
		 * Age in years (integer)
		 * Set to -99 if not relevant, missing or unknown.
		 */
		int getSister_bilateral_age(int index){
			int bilateral_age = UNKNOWN;
			try {
				JSONObject sister = questionnaire.getJSONArray("sister").getJSONObject(index);
				bilateral_age = getInt(sister,"bilateral_age");  
			}
			catch (JSONException e) {
				// Ignore for now
			}
			return bilateral_age;					
		}
		
		/**
		 * Age at which sister developed ovarian cancer	
		 * Age in years (integer)
		 * Set to -99 if not relevant, missing or unknown.
		 */
		int getSister_ovarian_age(int index){
			int sister_ovarian_age = UNKNOWN;
			try {
				JSONObject sister = questionnaire.getJSONArray("sister").getJSONObject(index);
				sister_ovarian_age = getInt(sister,"age_ovarian_cancer");  
			}
			catch (JSONException e) {
				// Ignore for now
			}
			return sister_ovarian_age;					
			
		}
		
		/**
		 * Genetic testing of the woman’s sister	
		 * 0= Never
		 * 1= Test result negative
		 * 2= BRCA1
		 * 3= BRCA2
		 * Set to 0 if unknown, or number of sisters=0.
		 */
		int getSister_genetic_test_result(int index){
			return 0; // Not in document
		}

		/**
		 * Get the number of other relatives of a given type
		 */
		int getNumOtherRelatives(int type){
			return getRelatives(type).size();
		}
		
		/**
		 * Has the relative had breast cancer?	0 = no 1=yes
		 * Set to 0 if missing or unknown.
		 * @param type the type of the relative
		 * @param index the index of the relative in the list of relatives of the given type
		 */
		int getOtherRelativeAffectedStatus(int type, int index){
			int affected_status = 0;
			List<JSONObject> relatives = getRelatives(type);
			if(relatives.size()>index){
				JSONObject relative = relatives.get(index);
				int _1_breast = getInt(relative,"1_breast");
				int bilateral = getInt(relative,"bilateral");
				int ovarian_cancer = getInt(relative,"ovarian_cancer");
				if(_1_breast == UNKNOWN && bilateral == UNKNOWN && ovarian_cancer == UNKNOWN){
					affected_status=1;
				}
				else if(_1_breast==1 || bilateral==1) {
					affected_status=1;					
				}
			}
			return affected_status;
		}

		/**
		 * Was the other relative's breast cancer bilateral?	
		 * 0 = no 1=yes
		 * Set to 0 if not relevant, missing or unknown.
		 * @param type the type of the relative
		 * @param index the index of the relative in the list of relatives of the given type
		 */
		int getOtherRelativeBilateralStatus(int type, int index){
			int bilateral = 0;
			List<JSONObject> relatives = getRelatives(type);
			if(relatives.size()>index){
				JSONObject relative = relatives.get(index);
				bilateral = getInt(relative,"bilateral");  
				if(bilateral==UNKNOWN) bilateral = 0;
			}
			return bilateral;
		}

		/**
		 * Has the other relative had ovarian cancer?	
		 * 0=no 1=yes
		 * Set to 0 if missing or unknown.
		 * @param type the type of the relative
		 * @param index the index of the relative in the list of relatives of the given type
		 */
		int getOtherRelativeOvarianStatus(int type, int index){
			int ovarian_cancer = 0;
			List<JSONObject> relatives = getRelatives(type);
			if(relatives.size()>index){
				JSONObject relative = relatives.get(index);
				ovarian_cancer = getInt(relative,"ovarian_cancer");  
				if(ovarian_cancer==UNKNOWN) ovarian_cancer = 0;
			}
			return ovarian_cancer;
		}

		/**
		 * Age at which other relative developed breast cancer	
		 * Age in years (integer)
		 * Set to -99 if not relevant, missing or unknown.
		 * @param type the type of the relative
		 * @param index the index of the relative in the list of relatives of the given type
		 */
		int getOtherRelativeAge(int type, int index){
			int age = UNKNOWN;
			List<JSONObject> relatives = getRelatives(type);
			if(relatives.size()>index){
				JSONObject relative = relatives.get(index);
				int _1_breast = getInt(relative,"1_breast");
				int bilateral = getInt(relative,"bilateral");
				int ovarian_cancer = getInt(relative,"ovarian_cancer");
				int age_breast_1 = getInt(relative,"age_breast_1");  
				int bilateral_age = getInt(relative,"bilateral_age");  
				if(_1_breast == UNKNOWN && bilateral == UNKNOWN && ovarian_cancer == UNKNOWN){
					age = age_breast_1;  
				}
				else if(age_breast_1!=UNKNOWN && bilateral_age!=UNKNOWN) {
					age = Math.min(age_breast_1, bilateral_age);		
				}
				else if(age_breast_1!=UNKNOWN) {
					age = age_breast_1;		
				}
				else if(bilateral_age!=UNKNOWN){
					age = bilateral_age;
				}
			}
			return age;					
		}

		/**
		 * Age at which relative developed bilateral breast cancer	
		 * Age in years (integer)
		 * Set to -99 if not relevant, missing or unknown.
		 * @param type the type of the relative
		 * @param index the index of the relative in the list of relatives of the given type
		 */
		int getOtherRelativeBilateralAge(int type, int index){
			int bilateral_age = UNKNOWN;
			List<JSONObject> relatives = getRelatives(type);
			if(relatives.size()>index){
				JSONObject relative = relatives.get(index);
				bilateral_age = getInt(relative,"bilateral_age");  
			}
			return bilateral_age;					
		}

		/**
		 * Age at which other relative developed ovarian cancer	
		 * Age in years (integer)
		 * Set to -99 if not relevant, missing or unknown.
		 * @param type the type of the relative
		 * @param index the index of the relative in the list of relatives of the given type
		 */
		int getOtherRelativeOvarianAge(int type, int index){
			int age_ovarian = UNKNOWN;
			List<JSONObject> relatives = getRelatives(type);
			if(relatives.size()>index){
				JSONObject relative = relatives.get(index);
				age_ovarian = getInt(relative,"age_ovarian");  
			}
			return age_ovarian;					
		}

		/**
		 * Not in document
		 */
		int getOtherRelativeGeneticTestResult(int type, int index){
			return 0; // Not in document
		}		
		
		/**
		 * Gets the array of relatives of a given type.
		 * @param type
		 * @return
		 */
		List<JSONObject> getRelatives(int type){
			List<JSONObject> result = new ArrayList<JSONObject>();
			try {
				JSONArray relationships = questionnaire.getJSONArray("relationship");
				for(int i=0;i<relationships.length();i++){
					JSONObject relative = relationships.getJSONObject(i);  
					if(getInt(relative,"breast_cancer_1")==type){
						result.add(relative);
					}
				}
			}
			catch (JSONException e) {
				// Ignore for now
			}
			return result;
		}

		private double getDouble(String fieldName){
			return getDouble(questionnaire,fieldName);
		}

		private double getDouble(JSONObject job, String fieldName){
			double value = UNKNOWN;
			try {
				value = job.getDouble(fieldName);
			} catch (JSONException e) {
				// Ignore this
				log.debug(e);
			}
			return value;
		}

		
		private int getInt(String fieldName){
			return getInt(questionnaire,fieldName);
		}

		private int getInt(JSONObject job, String fieldName){
			int value = UNKNOWN;
			try {
				double valDouble = job.getDouble(fieldName);
				value = (int)valDouble;
			} catch (JSONException e) {
				// Ignore this
				log.debug(e);
			}
			return value;
		}

	};

}

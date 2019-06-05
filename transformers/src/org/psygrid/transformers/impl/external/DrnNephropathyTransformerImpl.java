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

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.transformers.TransformerException;

/**
 * Transformer implementation to calculate Nephropathy value
 * for the DRN Address project.
 * 
 * @author Rob Harper
 *
 */
public class DrnNephropathyTransformerImpl implements
		ExternalServiceTransformer {

	private static final Log LOG = LogFactory.getLog(DrnNephropathyTransformerImpl.class);
	
	public static final String URINARY_ALBUMIN_OR_PROTEIN_YES = "1";
	public static final String URINARY_ALBUMIN_OR_PROTEIN_NO = "0";
	public static final String GENDER_MALE = "1";
	public static final String GENDER_FEMALE = "2";
	public static final String MAU_DS_TESTED_YES = "1";
	public static final String MAU_DS_TESTED_NO = "0";
	public static final String MAU_DS_SAMPLE_NEGATIVE = "1";
	public static final String MAU_DS_SAMPLE_POSITIVE = "2";
	public static final String PU_DS_TESTED_YES = "1";
	public static final String PU_DS_TESTED_NO = "0";
	public static final String PU_DS_SAMPLE_NEGATIVE = "1";
	public static final String PU_DS_SAMPLE_POSITIVE = "2";
	public static final String ACR_TESTED_YES = "1";
	public static final String ACR_TESTED_NO = "0";
	public static final String INFECTION_YES = "1";
	public static final String INFECTION_NO = "0";
	public static final String PCR_TESTED_YES = "1";
	public static final String PCR_TESTED_NO = "0";
	public static final String COLLECTION_TESTED_YES = "1";
	public static final String COLLECTION_TESTED_NO = "0";
	public static final String TIME_24HOURS = "1";
	public static final String TIME_OVERNIGHT = "2";
	public static final String TIME_OTHER = "3";
	
	public static final String URINARY_ALBUMIN_OR_PROTEIN = "urinary_albumin_or_protein";
	public static final String GENDER = "gender";
	public static final String MAU_DS_TESTED = "mau_ds_tested";
	public static final String MAU_DS_SAMPLE = "mau_ds_sample";
	public static final String PU_DS_TESTED = "pu_ds_tested";
	public static final String PU_DS_SAMPLE = "pu_ds_sample";
	public static final String ACR_TESTED = "ACR_tested";
	public static final String ACR_SAMPLE1 = "ACR_sample1";
	public static final String ACR_FOLLOWUP1 = "ACR_followup1";
	public static final String ACR_FOLLOWUP2 = "ACR_followup2";
	public static final String INFECTION = "infection";
	public static final String PCR_TESTED = "PCR_tested";
	public static final String PCR_SAMPLE1 = "PCR_sample1";
	public static final String PCR_FOLLOWUP1 = "PCR_followup1";
	public static final String PCR_FOLLOWUP2 = "PCR_followup2";
	public static final String COLLECTION_TESTED = "collection_tested";
	public static final String TIME = "time";
	public static final String TIME_MIN = "time_min";
	public static final String A_MASS = "a_mass";
	public static final String A_CONC = "a_conc";
	public static final String P_MASS = "p_mass";
	public static final String P_CONC = "p_conc";

	public static final String NEPHRO = "nephro";
	public static final String MAU_DS_RES = "mau_ds_res";
	public static final String PU_DS_RES = "pu_ds_res";
	public static final String ACR_RES = "acr_res";
	public static final String PCR_RES = "pcr_res";
	public static final String COLLECTION_RES = "collection_res";
	public static final String INFECTION_RES = "infection_res";
	
	public static final String NORMAL = "normal";
	public static final String ABNORMAL = "abnormal";
	public static final String EQUIVOCAL = "equivocal";
	public static final String NOT_INTERPRETED = "not_interpreted";
	public static final String NO_TEST_PERFORMED = "no_test_performed";
	public static final String NOT_VALID = "not_valid";
	
	public static final double ACR_LIMIT_MALE = 2.5;
	public static final double ACR_LIMIT_FEMALE = 3.5;
	public static final double PCR_LIMIT = 30;
	public static final double T = 0.104166667;
	public static final double TIME_CALC_24HOURS = 1440;
	public static final double TIME_CALC_OVERNIGHT = 480;
	public static final double CONC_LIMIT = 10;
	
	public URL getUrl() {
		return null;
	}

	public void setUrl(URL url) {
		//do nothing
	}

	/**
	 * @see org.psygrid.transformers.impl.external.ExternalServiceTransformer#transform(java.lang.String)
	 */
	public String transform(String data) throws TransformerException {
		
		/*
		 * Extract the parameters from the input data string
		 */
		LOG.info("Input string: "+data);
		//have to append an extra CSV variable to the end of the string
		//so that all fields are picked up even if they are empty
		data = data+",end";
		String[] params = data.split(",");
		if ( params.length != 23 ){
			throw new TransformerException("transform: input data has the wrong number of elements; expected 22, is "+params.length);
		}
		Map<String, String> inputParameters = new HashMap<String, String>();
		inputParameters.put(URINARY_ALBUMIN_OR_PROTEIN, params[0]);	//1=Yes, 0=No
		inputParameters.put(GENDER, params[1]);						//1=Male, 2=Female
		inputParameters.put(MAU_DS_TESTED, params[2]);				//1=Yes, 0=No
		inputParameters.put(MAU_DS_SAMPLE, params[3]);				//1=Negative, 2=Positive
		inputParameters.put(PU_DS_TESTED, params[4]);				//1=Yes, 0=No
		inputParameters.put(PU_DS_SAMPLE, params[5]);				//1=Negative, 2=Positive
		inputParameters.put(ACR_TESTED, params[6]);					//1=Yes, 0=No
		inputParameters.put(ACR_SAMPLE1, params[7]);				//Decimal, 0.01 to 500
		inputParameters.put(ACR_FOLLOWUP1, params[8]);				//Decimal, 0.01 to 500
		inputParameters.put(ACR_FOLLOWUP2, params[9]);				//Decimal, 0.01 to 500
		inputParameters.put(INFECTION, params[10]);					//1=Yes, 0=No
		inputParameters.put(PCR_TESTED, params[11]);				//1=Yes, 0=No
		inputParameters.put(PCR_SAMPLE1, params[12]);				//Decimal, 0.01 to 500
		inputParameters.put(PCR_FOLLOWUP1, params[13]);				//Decimal, 0.01 to 500
		inputParameters.put(PCR_FOLLOWUP2, params[14]);				//Decimal, 0.01 to 500
		inputParameters.put(COLLECTION_TESTED, params[15]);			//1=Yes, 0=No
		inputParameters.put(TIME, params[16]);						//1=24 Hours, 2=Overnight, 3=Other
		inputParameters.put(TIME_MIN, params[17]);					//Integer, 1 to 3000
		inputParameters.put(A_MASS, params[18]);					//Decimal, 0.01 to 500
		inputParameters.put(A_CONC, params[19]);					//Decimal, 1 to 1000
		inputParameters.put(P_MASS, params[20]);					//Decimal, 0.01 to 500
		inputParameters.put(P_CONC, params[21]);					//Decimal, 1 to 1000
		
		Map<String, String> intermediateParameters = calculateIntermediateParameters(inputParameters);
		LOG.info("intermediateParameters: MAU_DS_RES="+intermediateParameters.get(MAU_DS_RES)+
				"; PU_DS_RES="+intermediateParameters.get(PU_DS_RES)+
				"; ACR_RES="+intermediateParameters.get(ACR_RES)+
				"; PCR_RES="+intermediateParameters.get(PCR_RES)+
				"; collection_RES="+intermediateParameters.get(COLLECTION_RES)+
				"; infection_RES="+intermediateParameters.get(INFECTION_RES)+
				"; nephro="+intermediateParameters.get(NEPHRO) );
		if ( null != intermediateParameters.get(NEPHRO) ){
			//Nephro result obtained already so return it
			return intermediateParameters.get(NEPHRO);
		}
		
		return lookupNephroResult(intermediateParameters);
	}

	/**
	 * Calculate the intermediate parameters mau_ds_res, pu_ds_res, ACR_res, PCR_res,
	 * collection_res and infection_res from the input parameters.
	 * 
	 * @param inputParameters Map of the input parameters.
	 * @return Map of the intermediate parameters.
	 */
	public static Map<String, String> calculateIntermediateParameters(Map<String, String> inputParameters) {
		Map<String, String> intermediateParameters = new HashMap<String, String>();
		
		/*
		 * If no urinary albumin or protein tests performed then Nephro=no_test_performed
		 */
		if ( !URINARY_ALBUMIN_OR_PROTEIN_YES.equals(inputParameters.get(URINARY_ALBUMIN_OR_PROTEIN)) ){
			intermediateParameters.put(NEPHRO, NO_TEST_PERFORMED);
			return intermediateParameters;
		}
		
		/*
		 * Calculate mau_ds_res 
		 */
		if ( MAU_DS_TESTED_YES.equals(inputParameters.get(MAU_DS_TESTED)) ){
			if ( MAU_DS_SAMPLE_POSITIVE.equals(inputParameters.get(MAU_DS_SAMPLE)) ){
				intermediateParameters.put(MAU_DS_RES, ABNORMAL);
			}
			else{
				intermediateParameters.put(MAU_DS_RES, NORMAL);
			}
		}
		else{
			intermediateParameters.put(MAU_DS_RES, NOT_VALID);
		}
		
		/*
		 * Calculate pu_ds_res
		 */
		if ( PU_DS_TESTED_YES.equals(inputParameters.get(PU_DS_TESTED)) ){
			if ( PU_DS_SAMPLE_POSITIVE.equals(inputParameters.get(PU_DS_SAMPLE)) ){
				intermediateParameters.put(PU_DS_RES, ABNORMAL);
			}
			else{
				intermediateParameters.put(PU_DS_RES, NORMAL);
			}
		}
		else{
			intermediateParameters.put(PU_DS_RES, NOT_VALID);
		}
		
		/*
		 * Calculate acr_res
		 */
		if ( ACR_TESTED_YES.equals(inputParameters.get(ACR_TESTED)) ){
			double acrSample1 = 0;
			try{
				acrSample1 = Double.parseDouble(inputParameters.get(ACR_SAMPLE1));
			}
			catch(NumberFormatException nfe){
				acrSample1 = Double.NaN;
			}
			catch(NullPointerException npe){
				acrSample1 = Double.NaN;
			}
			double acrFollowup1 = 0;
			try{
				acrFollowup1 = Double.parseDouble(inputParameters.get(ACR_FOLLOWUP1));
			}
			catch(NumberFormatException nfe){
				acrFollowup1 = Double.NaN;
			}
			catch(NullPointerException npe){
				acrFollowup1 = Double.NaN;
			}
			double acrFollowup2 = 0;
			try{
				acrFollowup2 = Double.parseDouble(inputParameters.get(ACR_FOLLOWUP2));
			}
			catch(NumberFormatException nfe){
				acrFollowup2 = Double.NaN;
			}
			catch(NullPointerException npe){
				acrFollowup2 = Double.NaN;
			}
			
			if ( Double.isNaN(acrSample1) ){
				intermediateParameters.put(ACR_RES, NOT_VALID);
			}
			else{
				double acrLimit = 0;
				boolean validGender = true;
				if ( GENDER_MALE.equals(inputParameters.get(GENDER))){
					acrLimit = ACR_LIMIT_MALE;
				}
				else if ( GENDER_FEMALE.equals(inputParameters.get(GENDER))){
					acrLimit = ACR_LIMIT_FEMALE;
				}
				else{
					validGender = false;
					intermediateParameters.put(ACR_RES, NOT_VALID);
				}
				
				if ( validGender ){
					if ( acrSample1 >= acrLimit ){
						if ( acrFollowup1 >= acrLimit ){
							intermediateParameters.put(ACR_RES, ABNORMAL);
						}
						else if ( acrFollowup1 < acrLimit ){
							if ( acrFollowup2 >= acrLimit ){
								intermediateParameters.put(ACR_RES, ABNORMAL);
							}
							else if ( acrFollowup2 < acrLimit ){
								intermediateParameters.put(ACR_RES, NORMAL);
							}
							else{
								intermediateParameters.put(ACR_RES, NOT_VALID);
							}
						}
						else{
							if ( acrFollowup2 >= acrLimit ){
								intermediateParameters.put(ACR_RES, ABNORMAL);
							}
							else{
								intermediateParameters.put(ACR_RES, NOT_VALID);
							}
						}
					}
					else {
						intermediateParameters.put(ACR_RES, NORMAL);
					}
				}
				else{
					intermediateParameters.put(ACR_RES, NOT_VALID);
				}
			}
		}
		else{
			intermediateParameters.put(ACR_RES, NOT_VALID);
		}
		
		/*
		 * Calculate pcr_res
		 */
		if ( PCR_TESTED_YES.equals(inputParameters.get(PCR_TESTED))){
			double pcrSample1 = 0;
			try{
				pcrSample1 = Double.parseDouble(inputParameters.get(PCR_SAMPLE1));
			}
			catch(NumberFormatException nfe){
				pcrSample1 = Double.NaN;
			}
			catch(NullPointerException npe){
				pcrSample1 = Double.NaN;
			}
			double pcrFollowup1 = 0;
			try{
				pcrFollowup1 = Double.parseDouble(inputParameters.get(PCR_FOLLOWUP1));
			}
			catch(NumberFormatException nfe){
				pcrFollowup1 = Double.NaN;
			}
			catch(NullPointerException npe){
				pcrFollowup1 = Double.NaN;
			}
			double pcrFollowup2 = 0;
			try{
				pcrFollowup2 = Double.parseDouble(inputParameters.get(PCR_FOLLOWUP2));
			}
			catch(NumberFormatException nfe){
				pcrFollowup2 = Double.NaN;
			}
			catch(NullPointerException npe){
				pcrFollowup2 = Double.NaN;
			}
			
			if ( Double.isNaN(pcrSample1) ) {
				intermediateParameters.put(PCR_RES, NOT_VALID);
			}
			else{
				if ( pcrSample1 >= PCR_LIMIT ){
					if ( pcrFollowup1 >= PCR_LIMIT ){
						intermediateParameters.put(PCR_RES, ABNORMAL);
					}
					else if ( pcrFollowup1 < PCR_LIMIT ){
						if ( pcrFollowup2 >= PCR_LIMIT ){
							intermediateParameters.put(PCR_RES, ABNORMAL);
						}
						else if ( pcrFollowup2 < PCR_LIMIT){
							intermediateParameters.put(PCR_RES, NORMAL);
						}
						else{
							intermediateParameters.put(PCR_RES, NOT_VALID);
						}
					}
					else{
						if ( pcrFollowup2 >= PCR_LIMIT ){
							intermediateParameters.put(PCR_RES, ABNORMAL);
						}
						else{
							intermediateParameters.put(PCR_RES, NOT_VALID);
						}
					}
				}
				else{
					intermediateParameters.put(PCR_RES, NORMAL);
				}
			}
		}
		else{
			intermediateParameters.put(PCR_RES, NOT_VALID);
		}
		
		/*
		 * Calculate collection_res
		 */
		if ( COLLECTION_TESTED_YES.equals(inputParameters.get(COLLECTION_TESTED)) ){
			double r = 0;
			String rate = null;
			if ( TIME_24HOURS.equals(inputParameters.get(TIME))){
				r = T * TIME_CALC_24HOURS;
			}
			else if (TIME_OVERNIGHT.equals(inputParameters.get(TIME))){
				r = T * TIME_CALC_OVERNIGHT;
			}
			else if (TIME_OTHER.equals(inputParameters.get(TIME))){
				int timeMin = 0;
				try{
					timeMin = Integer.parseInt(inputParameters.get(TIME_MIN));
				}
				catch(NumberFormatException nfe){
					rate = NOT_VALID;
				}
				catch(NullPointerException npe){
					rate = NOT_VALID;
				}
				r = T * timeMin; 
			}
			else{
				rate = NOT_VALID;
			}

			double aConc = 0;
			try{
				aConc = Double.parseDouble(inputParameters.get(A_CONC));
			}
			catch(NumberFormatException nfe){
				aConc = Double.NaN;
			}
			catch(NullPointerException npe){
				aConc = Double.NaN;
			}
			double pConc = 0;
			try{
				pConc = Double.parseDouble(inputParameters.get(P_CONC));
			}
			catch(NumberFormatException nfe){
				pConc = Double.NaN;
			}
			catch(NullPointerException npe){
				pConc = Double.NaN;
			}
			double aMass = 0;
			try{
				aMass = Double.parseDouble(inputParameters.get(A_MASS));
			}
			catch(NumberFormatException nfe){
				aMass = Double.NaN;
			}
			catch(NullPointerException npe){
				aMass = Double.NaN;
			}
			double pMass = 0;
			try{
				pMass = Double.parseDouble(inputParameters.get(P_MASS));
			}
			catch(NumberFormatException nfe){
				pMass = Double.NaN;
			}
			catch(NullPointerException npe){
				pMass = Double.NaN;
			}

			if ( NOT_VALID.equals(rate) ){
				if ( aConc < CONC_LIMIT || pConc < CONC_LIMIT ){
					intermediateParameters.put(COLLECTION_RES, NORMAL);
				}
				else if ( aConc >= CONC_LIMIT || pConc >= CONC_LIMIT ){
					intermediateParameters.put(COLLECTION_RES, ABNORMAL);
				}
				else {
					intermediateParameters.put(COLLECTION_RES, NOT_VALID);
				}
			}
			else {
				if ( aMass < r || pMass < r ){
					if ( aConc >= CONC_LIMIT || pConc >= CONC_LIMIT ){
						intermediateParameters.put(COLLECTION_RES, NOT_VALID);
					}
					else{
						intermediateParameters.put(COLLECTION_RES, NORMAL);
					}
				}
				else if ( aMass >= r || pMass >= r ){
					if ( aConc < CONC_LIMIT || pConc < CONC_LIMIT ){
						intermediateParameters.put(COLLECTION_RES, NOT_VALID);
					}
					else{
						intermediateParameters.put(COLLECTION_RES, ABNORMAL);
					}
				}
				else{
					if ( aConc < CONC_LIMIT || pConc < CONC_LIMIT ){
						intermediateParameters.put(COLLECTION_RES, NORMAL);
					}
					else if ( aConc >= CONC_LIMIT || pConc >= CONC_LIMIT ){
						intermediateParameters.put(COLLECTION_RES, ABNORMAL);
					}
					else{
						intermediateParameters.put(COLLECTION_RES, NOT_VALID);
					}
				}
			}
			
		}
		else{
			intermediateParameters.put(COLLECTION_RES, NOT_VALID);
		}
		
		
		/*
		 * Calculate infection_res
		 */
		if ( INFECTION_YES.equals(inputParameters.get(INFECTION)) ){
			intermediateParameters.put(INFECTION_RES, ABNORMAL);
		}
		else if ( INFECTION_NO.equals(inputParameters.get(INFECTION)) ){
			intermediateParameters.put(INFECTION_RES, NORMAL);
		}
		else{
			intermediateParameters.put(INFECTION_RES, NOT_VALID);
		}
		
		return intermediateParameters;
	}
	
	/**
	 * Look up the nephropathy result from the intermediate parameters.
	 * 
	 * @param parameters 
	 * @return
	 * @throws TransformerException
	 */
	public static String lookupNephroResult(Map<String, String> parameters) throws TransformerException {
		
		String mauDsRes = parameters.get(MAU_DS_RES);
		String puDsRes = parameters.get(PU_DS_RES);
		String acrRes = parameters.get(ACR_RES);
		String pcrRes = parameters.get(PCR_RES);
		String collectionRes = parameters.get(COLLECTION_RES);
		String infectionRes = parameters.get(INFECTION_RES);
	
		if ( NOT_VALID.equals(mauDsRes) && NOT_VALID.equals(puDsRes) && NOT_VALID.equals(acrRes) && NOT_VALID.equals(pcrRes) && NOT_VALID.equals(collectionRes)){
			return NOT_INTERPRETED;
		}
		if ( NORMAL.equals(mauDsRes) && NOT_VALID.equals(puDsRes) && NOT_VALID.equals(acrRes) && NOT_VALID.equals(pcrRes) && NOT_VALID.equals(collectionRes)){
			return NORMAL;
		}
		if ( ABNORMAL.equals(mauDsRes) && NOT_VALID.equals(puDsRes) && NOT_VALID.equals(acrRes) && NOT_VALID.equals(pcrRes) && NOT_VALID.equals(collectionRes)){
			return EQUIVOCAL;
		}
		if ( NORMAL.equals(puDsRes) && NOT_VALID.equals(acrRes) && NOT_VALID.equals(pcrRes) && NOT_VALID.equals(collectionRes)){
			return NORMAL;
		}
		if ( ABNORMAL.equals(puDsRes) && NOT_VALID.equals(acrRes) && NOT_VALID.equals(pcrRes) && NOT_VALID.equals(collectionRes)){
			return EQUIVOCAL;
		}
		if ( NOT_VALID.equals(acrRes) && NOT_VALID.equals(pcrRes) && NORMAL.equals(collectionRes)){
			return NORMAL;
		}
		if ( NOT_VALID.equals(acrRes) && NOT_VALID.equals(pcrRes) && ABNORMAL.equals(collectionRes)){
			return NOT_INTERPRETED;
		}
		if ( NOT_VALID.equals(acrRes) && NORMAL.equals(pcrRes) && NOT_VALID.equals(collectionRes)){
			return NORMAL;
		}
		if ( NOT_VALID.equals(acrRes) && NORMAL.equals(pcrRes) && NORMAL.equals(collectionRes)){
			return NORMAL;
		}
		if ( NOT_VALID.equals(acrRes) && NORMAL.equals(pcrRes) && ABNORMAL.equals(collectionRes)){
			return EQUIVOCAL;
		}
		if ( NOT_VALID.equals(acrRes) && ABNORMAL.equals(pcrRes) && NOT_VALID.equals(collectionRes)){
			return ABNORMAL;
		}
		if ( NOT_VALID.equals(acrRes) && ABNORMAL.equals(pcrRes) && NORMAL.equals(collectionRes)){
			return EQUIVOCAL;
		}
		if ( NOT_VALID.equals(acrRes) && ABNORMAL.equals(pcrRes) && ABNORMAL.equals(collectionRes)){
			return ABNORMAL;
		}
		if ( NORMAL.equals(acrRes) && NOT_VALID.equals(pcrRes) && NOT_VALID.equals(collectionRes)){
			return NORMAL;
		}
		if ( NORMAL.equals(acrRes) && NOT_VALID.equals(pcrRes) && NORMAL.equals(collectionRes)){
			return NORMAL;
		}
		if ( NORMAL.equals(acrRes) && NOT_VALID.equals(pcrRes) && ABNORMAL.equals(collectionRes)){
			return EQUIVOCAL;
		}
		if ( NORMAL.equals(acrRes) && NORMAL.equals(pcrRes) && NOT_VALID.equals(collectionRes)){
			return NORMAL;
		}
		if ( NORMAL.equals(acrRes) && NORMAL.equals(pcrRes) && NORMAL.equals(collectionRes)){
			return NORMAL;
		}
		if ( NORMAL.equals(acrRes) && NORMAL.equals(pcrRes) && ABNORMAL.equals(collectionRes)){
			return EQUIVOCAL;
		}
		if ( NORMAL.equals(acrRes) && ABNORMAL.equals(pcrRes) && NOT_VALID.equals(collectionRes)){
			return EQUIVOCAL;
		}
		if ( NORMAL.equals(acrRes) && ABNORMAL.equals(pcrRes) && NORMAL.equals(collectionRes)){
			return EQUIVOCAL;
		}
		if ( NORMAL.equals(acrRes) && ABNORMAL.equals(pcrRes) && ABNORMAL.equals(collectionRes)){
			return EQUIVOCAL;
		}
		if ( ABNORMAL.equals(acrRes) && NOT_VALID.equals(pcrRes) && NOT_VALID.equals(collectionRes) && ABNORMAL.equals(infectionRes)){
			return EQUIVOCAL;
		}
		if ( ABNORMAL.equals(acrRes) && NOT_VALID.equals(pcrRes) && NOT_VALID.equals(collectionRes) && NORMAL.equals(infectionRes)){
			return ABNORMAL;
		}
		if ( ABNORMAL.equals(acrRes) && NOT_VALID.equals(pcrRes) && NOT_VALID.equals(collectionRes) && NOT_VALID.equals(infectionRes)){
			return EQUIVOCAL;
		}
		if ( ABNORMAL.equals(acrRes) && NOT_VALID.equals(pcrRes) && NORMAL.equals(collectionRes) ){
			return EQUIVOCAL;
		}
		if ( ABNORMAL.equals(acrRes) && NOT_VALID.equals(pcrRes) && ABNORMAL.equals(collectionRes) && ABNORMAL.equals(infectionRes)){
			return EQUIVOCAL;
		}
		if ( ABNORMAL.equals(acrRes) && NOT_VALID.equals(pcrRes) && ABNORMAL.equals(collectionRes) && NORMAL.equals(infectionRes)){
			return ABNORMAL;
		}
		if ( ABNORMAL.equals(acrRes) && NOT_VALID.equals(pcrRes) && ABNORMAL.equals(collectionRes) && NOT_VALID.equals(infectionRes)){
			return EQUIVOCAL;
		}
		if ( ABNORMAL.equals(acrRes) && NORMAL.equals(pcrRes) && NOT_VALID.equals(collectionRes) && ABNORMAL.equals(infectionRes)){
			return EQUIVOCAL;
		}
		if ( ABNORMAL.equals(acrRes) && NORMAL.equals(pcrRes) && NOT_VALID.equals(collectionRes) && NORMAL.equals(infectionRes)){
			return ABNORMAL;
		}
		if ( ABNORMAL.equals(acrRes) && NORMAL.equals(pcrRes) && NOT_VALID.equals(collectionRes) && NOT_VALID.equals(infectionRes)){
			return EQUIVOCAL;
		}
		if ( ABNORMAL.equals(acrRes) && NORMAL.equals(pcrRes) && NORMAL.equals(collectionRes) ){
			return EQUIVOCAL;
		}
		if ( ABNORMAL.equals(acrRes) && NORMAL.equals(pcrRes) && ABNORMAL.equals(collectionRes) && ABNORMAL.equals(infectionRes)){
			return EQUIVOCAL;
		}
		if ( ABNORMAL.equals(acrRes) && NORMAL.equals(pcrRes) && ABNORMAL.equals(collectionRes) && NORMAL.equals(infectionRes)){
			return ABNORMAL;
		}
		if ( ABNORMAL.equals(acrRes) && NORMAL.equals(pcrRes) && ABNORMAL.equals(collectionRes) && NOT_VALID.equals(infectionRes)){
			return EQUIVOCAL;
		}
		if ( ABNORMAL.equals(acrRes) && ABNORMAL.equals(pcrRes) && NOT_VALID.equals(collectionRes) ){
			return ABNORMAL;
		}
		if ( ABNORMAL.equals(acrRes) && ABNORMAL.equals(pcrRes) && NORMAL.equals(collectionRes) ){
			return EQUIVOCAL;
		}
		if ( ABNORMAL.equals(acrRes) && ABNORMAL.equals(pcrRes) && ABNORMAL.equals(collectionRes) ){
			return ABNORMAL;
		}

		throw new TransformerException("No nephro result found for the parameters: "+MAU_DS_RES+"="+mauDsRes+"; "
				+PU_DS_RES+"="+puDsRes+"; "+ACR_RES+"="+acrRes+"; "+PCR_RES+"="+pcrRes+"; "+MAU_DS_RES+"="+mauDsRes+"; "
				+COLLECTION_RES+"="+collectionRes+"; "+INFECTION_RES+"="+infectionRes);
		
	}
	
}

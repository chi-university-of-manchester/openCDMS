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

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

/**
 * @author Rob Harper
 *
 */
public class DrnNephropathyTransformerImplTest extends TestCase {

	public void testCalculateIntermediateParameters(){
		try{
			
			Map<String, String> inputParams = new HashMap<String, String>();
			
			//1a. IF urinary_albumin_or_protein_tested = NO THEN Nephro = no_test_performed
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.URINARY_ALBUMIN_OR_PROTEIN, DrnNephropathyTransformerImpl.URINARY_ALBUMIN_OR_PROTEIN_NO);
			Map<String, String> result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 1. Nephro value incorrect",DrnNephropathyTransformerImpl.NO_TEST_PERFORMED, result.get(DrnNephropathyTransformerImpl.NEPHRO));
			
			//1a. IF urinary_albumin_or_protein_tested != NO AND != YES THEN Nephro = no_test_performed
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.URINARY_ALBUMIN_OR_PROTEIN, "not-no-or-yes");
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 1. Nephro value incorrect",DrnNephropathyTransformerImpl.NO_TEST_PERFORMED, result.get(DrnNephropathyTransformerImpl.NEPHRO));
			
			//2. MAU_DS tests
			//2a. IF mau_ds_tested = NO THEN mau_ds_res = not_valid
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.MAU_DS_TESTED, DrnNephropathyTransformerImpl.MAU_DS_TESTED_NO);
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 2a. mau_ds_res value incorrect",DrnNephropathyTransformerImpl.NOT_VALID, result.get(DrnNephropathyTransformerImpl.MAU_DS_RES));
			
			//2b. IF mau_ds_tested != NO AND != YES THEN mau_ds_res = not_valid
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.MAU_DS_TESTED, "not-no-or-yes");
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 2b. mau_ds_res value incorrect",DrnNephropathyTransformerImpl.NOT_VALID, result.get(DrnNephropathyTransformerImpl.MAU_DS_RES));
			
			//2c. IF mau_ds_tested = YES AND IF mau_ds_sample = positive THEN mau_ds_res = abnormal
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.MAU_DS_TESTED, DrnNephropathyTransformerImpl.MAU_DS_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.MAU_DS_SAMPLE, DrnNephropathyTransformerImpl.MAU_DS_SAMPLE_POSITIVE);
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 2c. mau_ds_res value incorrect",DrnNephropathyTransformerImpl.ABNORMAL, result.get(DrnNephropathyTransformerImpl.MAU_DS_RES));
			
			//2d. IF mau_ds_tested = YES AND IF mau_ds_sample = negative THEN mau_ds_res = normal
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.MAU_DS_TESTED, DrnNephropathyTransformerImpl.MAU_DS_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.MAU_DS_SAMPLE, DrnNephropathyTransformerImpl.MAU_DS_SAMPLE_NEGATIVE);
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 2d. mau_ds_res value incorrect",DrnNephropathyTransformerImpl.NORMAL, result.get(DrnNephropathyTransformerImpl.MAU_DS_RES));

			//3. PU_DS tests
			//3a. IF pu_ds_tested = NO THEN pu_ds_res = not_valid
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.PU_DS_TESTED, DrnNephropathyTransformerImpl.PU_DS_TESTED_NO);
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 3a. pu_ds_res value incorrect",DrnNephropathyTransformerImpl.NOT_VALID, result.get(DrnNephropathyTransformerImpl.PU_DS_RES));
			
			//3a1. IF pu_ds_tested != NO AND != YES THEN pu_ds_res = not_valid
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.PU_DS_TESTED, "not-no-or-yes");
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 3a1. pu_ds_res value incorrect",DrnNephropathyTransformerImpl.NOT_VALID, result.get(DrnNephropathyTransformerImpl.PU_DS_RES));

			//3b. IF pu_ds_tested = YES AND IF pu_ds_sample = positive THEN pu_ds_res = abnormal
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.PU_DS_TESTED, DrnNephropathyTransformerImpl.PU_DS_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.PU_DS_SAMPLE, DrnNephropathyTransformerImpl.PU_DS_SAMPLE_POSITIVE);
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 3b. pu_ds_res value incorrect",DrnNephropathyTransformerImpl.ABNORMAL, result.get(DrnNephropathyTransformerImpl.PU_DS_RES));
			
			//3c. IF pu_ds_tested = YES AND IF pu_ds_sample = negative THEN pu_ds_res = normal
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.PU_DS_TESTED, DrnNephropathyTransformerImpl.PU_DS_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.PU_DS_SAMPLE, DrnNephropathyTransformerImpl.PU_DS_SAMPLE_NEGATIVE);
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 3c. pu_ds_res value incorrect",DrnNephropathyTransformerImpl.NORMAL, result.get(DrnNephropathyTransformerImpl.PU_DS_RES));

			//4. ACR tests
			//4a. IF ACR_tested = no THEN ACR_res = not_valid
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.ACR_TESTED, DrnNephropathyTransformerImpl.ACR_TESTED_NO);
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 4a. ACR_res value incorrect",DrnNephropathyTransformerImpl.NOT_VALID, result.get(DrnNephropathyTransformerImpl.ACR_RES));

			//4a1. IF ACR_tested != no AND != yes THEN ACR_res = not_valid
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.ACR_TESTED, "not-no-or-yes");
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 4a1. ACR_res value incorrect",DrnNephropathyTransformerImpl.NOT_VALID, result.get(DrnNephropathyTransformerImpl.ACR_RES));

			//4b. IF ACR_tested = yes AND ACR_sample1 = NaN THEN ACR_res = not_valid
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.ACR_TESTED, DrnNephropathyTransformerImpl.ACR_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.ACR_SAMPLE1, "not-a-number");
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 4b. ACR_res value incorrect",DrnNephropathyTransformerImpl.NOT_VALID, result.get(DrnNephropathyTransformerImpl.ACR_RES));
			
			//4c. IF ACR_tested = yes AND gender = male AND IF ACR_sample1 >= 2.5 AND ACR_followup1 >= 2.5 THEN ACR_res = abnormal
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.ACR_TESTED, DrnNephropathyTransformerImpl.ACR_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.GENDER, DrnNephropathyTransformerImpl.GENDER_MALE);
			inputParams.put(DrnNephropathyTransformerImpl.ACR_SAMPLE1, Double.toString(2.51));
			inputParams.put(DrnNephropathyTransformerImpl.ACR_FOLLOWUP1, Double.toString(2.51));
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 4c. ACR_res value incorrect",DrnNephropathyTransformerImpl.ABNORMAL, result.get(DrnNephropathyTransformerImpl.ACR_RES));

			//4d. IF ACR_tested = yes AND gender = male AND IF ACR_sample1 >= 2.5 AND ACR_followup1 < 2.5 AND ACR_followup2 >= 2.5 THEN ACR_res = abnormal
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.ACR_TESTED, DrnNephropathyTransformerImpl.ACR_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.GENDER, DrnNephropathyTransformerImpl.GENDER_MALE);
			inputParams.put(DrnNephropathyTransformerImpl.ACR_SAMPLE1, Double.toString(2.51));
			inputParams.put(DrnNephropathyTransformerImpl.ACR_FOLLOWUP1, Double.toString(2.49));
			inputParams.put(DrnNephropathyTransformerImpl.ACR_FOLLOWUP2, Double.toString(2.51));
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 4d. ACR_res value incorrect",DrnNephropathyTransformerImpl.ABNORMAL, result.get(DrnNephropathyTransformerImpl.ACR_RES));

			//4e. IF ACR_tested = yes AND gender = male AND IF ACR_sample1 >= 2.5 AND ACR_followup1 < 2.5 AND ACR_followup2 < 2.5 THEN ACR_res = normal
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.ACR_TESTED, DrnNephropathyTransformerImpl.ACR_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.GENDER, DrnNephropathyTransformerImpl.GENDER_MALE);
			inputParams.put(DrnNephropathyTransformerImpl.ACR_SAMPLE1, Double.toString(2.51));
			inputParams.put(DrnNephropathyTransformerImpl.ACR_FOLLOWUP1, Double.toString(2.49));
			inputParams.put(DrnNephropathyTransformerImpl.ACR_FOLLOWUP2, Double.toString(2.49));
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 4e. ACR_res value incorrect",DrnNephropathyTransformerImpl.NORMAL, result.get(DrnNephropathyTransformerImpl.ACR_RES));

			//4f. IF ACR_tested = yes AND gender = male AND IF ACR_sample1 >= 2.5 AND ACR_followup1 < 2.5 AND ACR_followup2 = NaN THEN ACR_res = not_valid
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.ACR_TESTED, DrnNephropathyTransformerImpl.ACR_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.GENDER, DrnNephropathyTransformerImpl.GENDER_MALE);
			inputParams.put(DrnNephropathyTransformerImpl.ACR_SAMPLE1, Double.toString(2.51));
			inputParams.put(DrnNephropathyTransformerImpl.ACR_FOLLOWUP1, Double.toString(2.49));
			inputParams.put(DrnNephropathyTransformerImpl.ACR_FOLLOWUP2, "not-a-number");
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 4f. ACR_res value incorrect",DrnNephropathyTransformerImpl.NOT_VALID, result.get(DrnNephropathyTransformerImpl.ACR_RES));

			//4g. IF ACR_tested = yes AND gender = male AND IF ACR_sample1 >= 2.5 AND ACR_followup1 = NaN AND ACR_followup2 >= 2.5 THEN ACR_res = abnormal
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.ACR_TESTED, DrnNephropathyTransformerImpl.ACR_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.GENDER, DrnNephropathyTransformerImpl.GENDER_MALE);
			inputParams.put(DrnNephropathyTransformerImpl.ACR_SAMPLE1, Double.toString(2.51));
			inputParams.put(DrnNephropathyTransformerImpl.ACR_FOLLOWUP1, "not-a-number");
			inputParams.put(DrnNephropathyTransformerImpl.ACR_FOLLOWUP2, Double.toString(2.51));
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 4g. ACR_res value incorrect",DrnNephropathyTransformerImpl.ABNORMAL, result.get(DrnNephropathyTransformerImpl.ACR_RES));

			//4h. IF ACR_tested = yes AND gender = male AND IF ACR_sample1 >= 2.5 AND ACR_followup1 = NaN AND ACR_followup2 < 2.5 THEN ACR_res = not_valid
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.ACR_TESTED, DrnNephropathyTransformerImpl.ACR_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.GENDER, DrnNephropathyTransformerImpl.GENDER_MALE);
			inputParams.put(DrnNephropathyTransformerImpl.ACR_SAMPLE1, Double.toString(2.51));
			inputParams.put(DrnNephropathyTransformerImpl.ACR_FOLLOWUP1, "not-a-number");
			inputParams.put(DrnNephropathyTransformerImpl.ACR_FOLLOWUP2, Double.toString(2.49));
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 4h. ACR_res value incorrect",DrnNephropathyTransformerImpl.NOT_VALID, result.get(DrnNephropathyTransformerImpl.ACR_RES));
			
			//4i. IF ACR_tested = yes AND gender = male AND IF ACR_sample1 >= 2.5 AND ACR_followup1 = NaN AND ACR_followup2 = NaN THEN ACR_res = not_valid
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.ACR_TESTED, DrnNephropathyTransformerImpl.ACR_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.GENDER, DrnNephropathyTransformerImpl.GENDER_MALE);
			inputParams.put(DrnNephropathyTransformerImpl.ACR_SAMPLE1, Double.toString(2.51));
			inputParams.put(DrnNephropathyTransformerImpl.ACR_FOLLOWUP1, "not-a-number");
			inputParams.put(DrnNephropathyTransformerImpl.ACR_FOLLOWUP2, "not-a-number");
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 4i. ACR_res value incorrect",DrnNephropathyTransformerImpl.NOT_VALID, result.get(DrnNephropathyTransformerImpl.ACR_RES));
			
			//4j. IF ACR_tested = yes AND gender = male AND IF ACR_sample1 < 2.5 THEN ACR_res = normal
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.ACR_TESTED, DrnNephropathyTransformerImpl.ACR_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.GENDER, DrnNephropathyTransformerImpl.GENDER_MALE);
			inputParams.put(DrnNephropathyTransformerImpl.ACR_SAMPLE1, Double.toString(2.49));
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 4j. ACR_res value incorrect",DrnNephropathyTransformerImpl.NORMAL, result.get(DrnNephropathyTransformerImpl.ACR_RES));
			
			//4k. IF ACR_tested = yes AND gender = female AND IF ACR_sample1 >= 3.5 AND ACR_followup1 >= 3.5 THEN ACR_res = abnormal
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.ACR_TESTED, DrnNephropathyTransformerImpl.ACR_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.GENDER, DrnNephropathyTransformerImpl.GENDER_FEMALE);
			inputParams.put(DrnNephropathyTransformerImpl.ACR_SAMPLE1, Double.toString(3.51));
			inputParams.put(DrnNephropathyTransformerImpl.ACR_FOLLOWUP1, Double.toString(3.51));
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 4k. ACR_res value incorrect",DrnNephropathyTransformerImpl.ABNORMAL, result.get(DrnNephropathyTransformerImpl.ACR_RES));

			//4l. IF ACR_tested = yes AND gender = female AND IF ACR_sample1 >= 3.5 AND ACR_followup1 < 3.5 AND ACR_followup2 >= 3.5 THEN ACR_res = abnormal
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.ACR_TESTED, DrnNephropathyTransformerImpl.ACR_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.GENDER, DrnNephropathyTransformerImpl.GENDER_FEMALE);
			inputParams.put(DrnNephropathyTransformerImpl.ACR_SAMPLE1, Double.toString(3.51));
			inputParams.put(DrnNephropathyTransformerImpl.ACR_FOLLOWUP1, Double.toString(3.49));
			inputParams.put(DrnNephropathyTransformerImpl.ACR_FOLLOWUP2, Double.toString(3.51));
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 4l. ACR_res value incorrect",DrnNephropathyTransformerImpl.ABNORMAL, result.get(DrnNephropathyTransformerImpl.ACR_RES));

			//4m. IF ACR_tested = yes AND gender = female AND IF ACR_sample1 >= 3.5 AND ACR_followup1 < 3.5 AND ACR_followup2 < 3.5 THEN ACR_res = normal
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.ACR_TESTED, DrnNephropathyTransformerImpl.ACR_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.GENDER, DrnNephropathyTransformerImpl.GENDER_FEMALE);
			inputParams.put(DrnNephropathyTransformerImpl.ACR_SAMPLE1, Double.toString(3.51));
			inputParams.put(DrnNephropathyTransformerImpl.ACR_FOLLOWUP1, Double.toString(3.49));
			inputParams.put(DrnNephropathyTransformerImpl.ACR_FOLLOWUP2, Double.toString(3.49));
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 4m. ACR_res value incorrect",DrnNephropathyTransformerImpl.NORMAL, result.get(DrnNephropathyTransformerImpl.ACR_RES));

			//4n. IF ACR_tested = yes AND gender = female AND IF ACR_sample1 >= 3.5 AND ACR_followup1 < 3.5 AND ACR_followup2 = NaN THEN ACR_res = not_valid
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.ACR_TESTED, DrnNephropathyTransformerImpl.ACR_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.GENDER, DrnNephropathyTransformerImpl.GENDER_FEMALE);
			inputParams.put(DrnNephropathyTransformerImpl.ACR_SAMPLE1, Double.toString(3.51));
			inputParams.put(DrnNephropathyTransformerImpl.ACR_FOLLOWUP1, Double.toString(3.49));
			inputParams.put(DrnNephropathyTransformerImpl.ACR_FOLLOWUP2, "not-a-number");
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 4n. ACR_res value incorrect",DrnNephropathyTransformerImpl.NOT_VALID, result.get(DrnNephropathyTransformerImpl.ACR_RES));

			//4o. IF ACR_tested = yes AND gender = female AND IF ACR_sample1 >= 3.5 AND ACR_followup1 = NaN AND ACR_followup2 >= 3.5 THEN ACR_res = abnormal
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.ACR_TESTED, DrnNephropathyTransformerImpl.ACR_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.GENDER, DrnNephropathyTransformerImpl.GENDER_FEMALE);
			inputParams.put(DrnNephropathyTransformerImpl.ACR_SAMPLE1, Double.toString(3.51));
			inputParams.put(DrnNephropathyTransformerImpl.ACR_FOLLOWUP1, "not-a-number");
			inputParams.put(DrnNephropathyTransformerImpl.ACR_FOLLOWUP2, Double.toString(3.51));
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 4o. ACR_res value incorrect",DrnNephropathyTransformerImpl.ABNORMAL, result.get(DrnNephropathyTransformerImpl.ACR_RES));

			//4p. IF ACR_tested = yes AND gender = female AND IF ACR_sample1 >= 3.5 AND ACR_followup1 = NaN AND ACR_followup2 < 3.5 THEN ACR_res = not_valid
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.ACR_TESTED, DrnNephropathyTransformerImpl.ACR_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.GENDER, DrnNephropathyTransformerImpl.GENDER_FEMALE);
			inputParams.put(DrnNephropathyTransformerImpl.ACR_SAMPLE1, Double.toString(3.51));
			inputParams.put(DrnNephropathyTransformerImpl.ACR_FOLLOWUP1, "not-a-number");
			inputParams.put(DrnNephropathyTransformerImpl.ACR_FOLLOWUP2, Double.toString(3.49));
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 4p. ACR_res value incorrect",DrnNephropathyTransformerImpl.NOT_VALID, result.get(DrnNephropathyTransformerImpl.ACR_RES));
			
			//4q. IF ACR_tested = yes AND gender = female AND IF ACR_sample1 >= 3.5 AND ACR_followup1 = NaN AND ACR_followup2 = NaN THEN ACR_res = not_valid
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.ACR_TESTED, DrnNephropathyTransformerImpl.ACR_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.GENDER, DrnNephropathyTransformerImpl.GENDER_FEMALE);
			inputParams.put(DrnNephropathyTransformerImpl.ACR_SAMPLE1, Double.toString(3.51));
			inputParams.put(DrnNephropathyTransformerImpl.ACR_FOLLOWUP1, "not-a-number");
			inputParams.put(DrnNephropathyTransformerImpl.ACR_FOLLOWUP2, "not-a-number");
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 4q. ACR_res value incorrect",DrnNephropathyTransformerImpl.NOT_VALID, result.get(DrnNephropathyTransformerImpl.ACR_RES));
			
			//4r. IF ACR_tested = yes AND gender = female AND IF ACR_sample1 < 3.5 THEN ACR_res = normal
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.ACR_TESTED, DrnNephropathyTransformerImpl.ACR_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.GENDER, DrnNephropathyTransformerImpl.GENDER_FEMALE);
			inputParams.put(DrnNephropathyTransformerImpl.ACR_SAMPLE1, Double.toString(3.49));
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 4r. ACR_res value incorrect",DrnNephropathyTransformerImpl.NORMAL, result.get(DrnNephropathyTransformerImpl.ACR_RES));

			//4s. IF ACR_tested = yes AND gender != female AND gender != male THEN ACR_res = not_valid
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.ACR_TESTED, DrnNephropathyTransformerImpl.ACR_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.GENDER, "incorrect gender");
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 4s. ACR_res value incorrect",DrnNephropathyTransformerImpl.NOT_VALID, result.get(DrnNephropathyTransformerImpl.ACR_RES));
			
			//5. PCR tests
			//5a. IF PCR_tested = no THEN PCR_res = not_valid
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.PCR_TESTED, DrnNephropathyTransformerImpl.PCR_TESTED_NO);
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 5a. PCR_res value incorrect",DrnNephropathyTransformerImpl.NOT_VALID, result.get(DrnNephropathyTransformerImpl.PCR_RES));

			//5a1. IF PCR_tested != no AND != yes THEN PCR_res = not_valid
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.PCR_TESTED, "not-no-or-yes");
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 5a1. PCR_res value incorrect",DrnNephropathyTransformerImpl.NOT_VALID, result.get(DrnNephropathyTransformerImpl.PCR_RES));

			//5b. IF PCR_tested = yes AND PCR_sample1 = NaN THEN PCR_res = not_valid
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.PCR_TESTED, DrnNephropathyTransformerImpl.PCR_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.PCR_SAMPLE1, "not-a-number");
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 5b. PCR_res value incorrect",DrnNephropathyTransformerImpl.NOT_VALID, result.get(DrnNephropathyTransformerImpl.PCR_RES));

			//5c. IF PCR_tested = yes AND PCR_sample1 >= 30 AND PCR_followup1 >= 30 THEN PCR_res = abnormal
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.PCR_TESTED, DrnNephropathyTransformerImpl.PCR_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.PCR_SAMPLE1, Double.toString(30));
			inputParams.put(DrnNephropathyTransformerImpl.PCR_FOLLOWUP1, Double.toString(30));
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 5c. PCR_res value incorrect",DrnNephropathyTransformerImpl.ABNORMAL, result.get(DrnNephropathyTransformerImpl.PCR_RES));

			//5d. IF PCR_tested = yes AND PCR_sample1 >= 30 AND PCR_followup1 < 30 AND PCR_followup2 >= 30 THEN PCR_res = abnormal
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.PCR_TESTED, DrnNephropathyTransformerImpl.PCR_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.PCR_SAMPLE1, Double.toString(30));
			inputParams.put(DrnNephropathyTransformerImpl.PCR_FOLLOWUP1, Double.toString(29.9));
			inputParams.put(DrnNephropathyTransformerImpl.PCR_FOLLOWUP2, Double.toString(30));
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 5d. PCR_res value incorrect",DrnNephropathyTransformerImpl.ABNORMAL, result.get(DrnNephropathyTransformerImpl.PCR_RES));

			//5e. IF PCR_tested = yes AND PCR_sample1 >= 30 AND PCR_followup1 < 30 AND PCR_followup2 < 30 THEN PCR_res = normal
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.PCR_TESTED, DrnNephropathyTransformerImpl.PCR_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.PCR_SAMPLE1, Double.toString(30));
			inputParams.put(DrnNephropathyTransformerImpl.PCR_FOLLOWUP1, Double.toString(29.9));
			inputParams.put(DrnNephropathyTransformerImpl.PCR_FOLLOWUP2, Double.toString(29.9));
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 5e. PCR_res value incorrect",DrnNephropathyTransformerImpl.NORMAL, result.get(DrnNephropathyTransformerImpl.PCR_RES));

			//5f. IF PCR_tested = yes AND PCR_sample1 >= 30 AND PCR_followup1 < 30 AND PCR_followup2 = NaN THEN PCR_res = not_valid
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.PCR_TESTED, DrnNephropathyTransformerImpl.PCR_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.PCR_SAMPLE1, Double.toString(30));
			inputParams.put(DrnNephropathyTransformerImpl.PCR_FOLLOWUP1, Double.toString(29.9));
			inputParams.put(DrnNephropathyTransformerImpl.PCR_FOLLOWUP2, "not-a-number");
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 5f. PCR_res value incorrect",DrnNephropathyTransformerImpl.NOT_VALID, result.get(DrnNephropathyTransformerImpl.PCR_RES));

			//5g. IF PCR_tested = yes AND PCR_sample1 >= 30 AND PCR_followup1 = NaN AND PCR_followup2 >= 30 THEN PCR_res = abnormal
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.PCR_TESTED, DrnNephropathyTransformerImpl.PCR_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.PCR_SAMPLE1, Double.toString(30));
			inputParams.put(DrnNephropathyTransformerImpl.PCR_FOLLOWUP1, "not-a-number");
			inputParams.put(DrnNephropathyTransformerImpl.PCR_FOLLOWUP2, Double.toString(30));
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 5g. PCR_res value incorrect",DrnNephropathyTransformerImpl.ABNORMAL, result.get(DrnNephropathyTransformerImpl.PCR_RES));

			//5h. IF PCR_tested = yes AND PCR_sample1 >= 30 AND PCR_followup1 = NaN AND PCR_followup2 < 30 THEN PCR_res = not_valid
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.PCR_TESTED, DrnNephropathyTransformerImpl.PCR_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.PCR_SAMPLE1, Double.toString(30));
			inputParams.put(DrnNephropathyTransformerImpl.PCR_FOLLOWUP1, "not-a-number");
			inputParams.put(DrnNephropathyTransformerImpl.PCR_FOLLOWUP2, Double.toString(29.9));
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 5h. PCR_res value incorrect",DrnNephropathyTransformerImpl.NOT_VALID, result.get(DrnNephropathyTransformerImpl.PCR_RES));

			//5i. IF PCR_tested = yes AND PCR_sample1 >= 30 AND PCR_followup1 = NaN AND PCR_followup2 = NaN THEN PCR_res = not_valid
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.PCR_TESTED, DrnNephropathyTransformerImpl.PCR_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.PCR_SAMPLE1, Double.toString(30));
			inputParams.put(DrnNephropathyTransformerImpl.PCR_FOLLOWUP1, "not-a-number");
			inputParams.put(DrnNephropathyTransformerImpl.PCR_FOLLOWUP2, "not-a-number");
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 5i. PCR_res value incorrect",DrnNephropathyTransformerImpl.NOT_VALID, result.get(DrnNephropathyTransformerImpl.PCR_RES));

			//6. Collection tests
			//6a. IF collection_tested = no THEN collection_res = not_valid
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.COLLECTION_TESTED, DrnNephropathyTransformerImpl.PCR_TESTED_NO);
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 6a. collection_res value incorrect",DrnNephropathyTransformerImpl.NOT_VALID, result.get(DrnNephropathyTransformerImpl.COLLECTION_RES));

			//6b. IF collection_tested != no AND != yes THEN collection_res = not_valid
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.COLLECTION_TESTED, "not-yes-or-no");
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 6b. collection_res value incorrect",DrnNephropathyTransformerImpl.NOT_VALID, result.get(DrnNephropathyTransformerImpl.COLLECTION_RES));

			double r24Hours = 0.104166667*1440;

			//6c. IF collection_tested = yes AND time = 24 hours AND (a_mass < r OR p_mass < r) AND (a_conc >= 10 OR p_conc >= 10) THEN collection_res = not_valid
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.COLLECTION_TESTED, DrnNephropathyTransformerImpl.COLLECTION_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.TIME, DrnNephropathyTransformerImpl.TIME_24HOURS);
			inputParams.put(DrnNephropathyTransformerImpl.A_MASS, Double.toString(r24Hours-0.1));
			inputParams.put(DrnNephropathyTransformerImpl.P_MASS, Double.toString(r24Hours-0.1));
			inputParams.put(DrnNephropathyTransformerImpl.A_CONC, Double.toString(10));
			inputParams.put(DrnNephropathyTransformerImpl.P_CONC, Double.toString(10));
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 6c. collection_res value incorrect",DrnNephropathyTransformerImpl.NOT_VALID, result.get(DrnNephropathyTransformerImpl.COLLECTION_RES));

			//6d. IF collection_tested = yes AND time = 24 hours AND (a_mass < r OR p_mass < r) AND !(a_conc >= 10 OR p_conc >= 10) THEN collection_res = normal
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.COLLECTION_TESTED, DrnNephropathyTransformerImpl.COLLECTION_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.TIME, DrnNephropathyTransformerImpl.TIME_24HOURS);
			inputParams.put(DrnNephropathyTransformerImpl.A_MASS, Double.toString(r24Hours-0.1));
			inputParams.put(DrnNephropathyTransformerImpl.P_MASS, Double.toString(r24Hours-0.1));
			inputParams.put(DrnNephropathyTransformerImpl.A_CONC, Double.toString(9.9));
			inputParams.put(DrnNephropathyTransformerImpl.P_CONC, Double.toString(9.9));
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 6d. collection_res value incorrect",DrnNephropathyTransformerImpl.NORMAL, result.get(DrnNephropathyTransformerImpl.COLLECTION_RES));

			//6e. IF collection_tested = yes AND time = 24 hours AND (a_mass < r OR p_mass < r) AND a_conc = NaN AND a_conc = NaN THEN collection_res = normal
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.COLLECTION_TESTED, DrnNephropathyTransformerImpl.COLLECTION_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.TIME, DrnNephropathyTransformerImpl.TIME_24HOURS);
			inputParams.put(DrnNephropathyTransformerImpl.A_MASS, Double.toString(r24Hours-0.1));
			inputParams.put(DrnNephropathyTransformerImpl.P_MASS, Double.toString(r24Hours-0.1));
			inputParams.put(DrnNephropathyTransformerImpl.A_CONC, "not-a-number");
			inputParams.put(DrnNephropathyTransformerImpl.P_CONC, "not-a-number");
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 6e. collection_res value incorrect",DrnNephropathyTransformerImpl.NORMAL, result.get(DrnNephropathyTransformerImpl.COLLECTION_RES));

			//6f. IF collection_tested = yes AND time = 24 hours AND (a_mass >= r OR p_mass >= r) AND (a_conc < 10 OR p_conc < 10) THEN collection_res = not_valid
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.COLLECTION_TESTED, DrnNephropathyTransformerImpl.COLLECTION_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.TIME, DrnNephropathyTransformerImpl.TIME_24HOURS);
			inputParams.put(DrnNephropathyTransformerImpl.A_MASS, Double.toString(r24Hours));
			inputParams.put(DrnNephropathyTransformerImpl.P_MASS, Double.toString(r24Hours));
			inputParams.put(DrnNephropathyTransformerImpl.A_CONC, Double.toString(9.9));
			inputParams.put(DrnNephropathyTransformerImpl.P_CONC, Double.toString(9.9));
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 6f. collection_res value incorrect",DrnNephropathyTransformerImpl.NOT_VALID, result.get(DrnNephropathyTransformerImpl.COLLECTION_RES));

			//6g. IF collection_tested = yes AND time = 24 hours AND (a_mass >= r OR p_mass >= r) AND !(a_conc < 10 OR p_conc < 10) THEN collection_res = abnormal
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.COLLECTION_TESTED, DrnNephropathyTransformerImpl.COLLECTION_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.TIME, DrnNephropathyTransformerImpl.TIME_24HOURS);
			inputParams.put(DrnNephropathyTransformerImpl.A_MASS, Double.toString(r24Hours));
			inputParams.put(DrnNephropathyTransformerImpl.P_MASS, Double.toString(r24Hours));
			inputParams.put(DrnNephropathyTransformerImpl.A_CONC, Double.toString(10));
			inputParams.put(DrnNephropathyTransformerImpl.P_CONC, Double.toString(10));
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 6g. collection_res value incorrect",DrnNephropathyTransformerImpl.ABNORMAL, result.get(DrnNephropathyTransformerImpl.COLLECTION_RES));

			//6h. IF collection_tested = yes AND time = 24 hours AND (a_mass >= r OR p_mass >= r) AND a_conc = NaN AND a_conc = NaN THEN collection_res = abnormal
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.COLLECTION_TESTED, DrnNephropathyTransformerImpl.COLLECTION_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.TIME, DrnNephropathyTransformerImpl.TIME_24HOURS);
			inputParams.put(DrnNephropathyTransformerImpl.A_MASS, Double.toString(r24Hours));
			inputParams.put(DrnNephropathyTransformerImpl.P_MASS, Double.toString(r24Hours));
			inputParams.put(DrnNephropathyTransformerImpl.A_CONC, "not-a-number");
			inputParams.put(DrnNephropathyTransformerImpl.P_CONC, "not-a-number");
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 6h. collection_res value incorrect",DrnNephropathyTransformerImpl.ABNORMAL, result.get(DrnNephropathyTransformerImpl.COLLECTION_RES));

			double rOvernight = 0.104166667*480;

			//6i. IF collection_tested = yes AND time = overnight AND (a_mass < r OR p_mass < r) AND (a_conc >= 10 OR p_conc >= 10) THEN collection_res = not_valid
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.COLLECTION_TESTED, DrnNephropathyTransformerImpl.COLLECTION_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.TIME, DrnNephropathyTransformerImpl.TIME_OVERNIGHT);
			inputParams.put(DrnNephropathyTransformerImpl.A_MASS, Double.toString(rOvernight-0.1));
			inputParams.put(DrnNephropathyTransformerImpl.P_MASS, Double.toString(rOvernight-0.1));
			inputParams.put(DrnNephropathyTransformerImpl.A_CONC, Double.toString(10));
			inputParams.put(DrnNephropathyTransformerImpl.P_CONC, Double.toString(10));
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 6i. collection_res value incorrect",DrnNephropathyTransformerImpl.NOT_VALID, result.get(DrnNephropathyTransformerImpl.COLLECTION_RES));

			//6j. IF collection_tested = yes AND time = overnight AND (a_mass < r OR p_mass < r) AND !(a_conc >= 10 OR p_conc >= 10) THEN collection_res = normal
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.COLLECTION_TESTED, DrnNephropathyTransformerImpl.COLLECTION_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.TIME, DrnNephropathyTransformerImpl.TIME_OVERNIGHT);
			inputParams.put(DrnNephropathyTransformerImpl.A_MASS, Double.toString(rOvernight-0.1));
			inputParams.put(DrnNephropathyTransformerImpl.P_MASS, Double.toString(rOvernight-0.1));
			inputParams.put(DrnNephropathyTransformerImpl.A_CONC, Double.toString(9.9));
			inputParams.put(DrnNephropathyTransformerImpl.P_CONC, Double.toString(9.9));
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 6j. collection_res value incorrect",DrnNephropathyTransformerImpl.NORMAL, result.get(DrnNephropathyTransformerImpl.COLLECTION_RES));

			//6k. IF collection_tested = yes AND time = overnight AND (a_mass < r OR p_mass < r) AND a_conc = NaN AND p_conc = NaN THEN collection_res = normal
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.COLLECTION_TESTED, DrnNephropathyTransformerImpl.COLLECTION_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.TIME, DrnNephropathyTransformerImpl.TIME_OVERNIGHT);
			inputParams.put(DrnNephropathyTransformerImpl.A_MASS, Double.toString(rOvernight-0.1));
			inputParams.put(DrnNephropathyTransformerImpl.P_MASS, Double.toString(rOvernight-0.1));
			inputParams.put(DrnNephropathyTransformerImpl.A_CONC, "not-a-number");
			inputParams.put(DrnNephropathyTransformerImpl.P_CONC, "not-a-number");
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 6k. collection_res value incorrect",DrnNephropathyTransformerImpl.NORMAL, result.get(DrnNephropathyTransformerImpl.COLLECTION_RES));

			//6l. IF collection_tested = yes AND time = overnight AND (a_mass >= r OR p_mass >= r) AND (a_conc < 10 OR p_conc < 10) THEN collection_res = not_valid
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.COLLECTION_TESTED, DrnNephropathyTransformerImpl.COLLECTION_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.TIME, DrnNephropathyTransformerImpl.TIME_OVERNIGHT);
			inputParams.put(DrnNephropathyTransformerImpl.A_MASS, Double.toString(rOvernight));
			inputParams.put(DrnNephropathyTransformerImpl.P_MASS, Double.toString(rOvernight));
			inputParams.put(DrnNephropathyTransformerImpl.A_CONC, Double.toString(9.9));
			inputParams.put(DrnNephropathyTransformerImpl.P_CONC, Double.toString(9.9));
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 6l. collection_res value incorrect",DrnNephropathyTransformerImpl.NOT_VALID, result.get(DrnNephropathyTransformerImpl.COLLECTION_RES));

			//6m. IF collection_tested = yes AND time = overnight AND (a_mass >= r OR p_mass >= r) AND !(a_conc < 10 OR p_conc < 10) THEN collection_res = abnormal
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.COLLECTION_TESTED, DrnNephropathyTransformerImpl.COLLECTION_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.TIME, DrnNephropathyTransformerImpl.TIME_OVERNIGHT);
			inputParams.put(DrnNephropathyTransformerImpl.A_MASS, Double.toString(rOvernight));
			inputParams.put(DrnNephropathyTransformerImpl.P_MASS, Double.toString(rOvernight));
			inputParams.put(DrnNephropathyTransformerImpl.A_CONC, Double.toString(10));
			inputParams.put(DrnNephropathyTransformerImpl.P_CONC, Double.toString(10));
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 6m. collection_res value incorrect",DrnNephropathyTransformerImpl.ABNORMAL, result.get(DrnNephropathyTransformerImpl.COLLECTION_RES));

			//6n. IF collection_tested = yes AND time = overnight AND (a_mass >= r OR p_mass >= r) AND a_conc = NaN AND p_conc = NaN THEN collection_res = abnormal
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.COLLECTION_TESTED, DrnNephropathyTransformerImpl.COLLECTION_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.TIME, DrnNephropathyTransformerImpl.TIME_OVERNIGHT);
			inputParams.put(DrnNephropathyTransformerImpl.A_MASS, Double.toString(rOvernight));
			inputParams.put(DrnNephropathyTransformerImpl.P_MASS, Double.toString(rOvernight));
			inputParams.put(DrnNephropathyTransformerImpl.A_CONC, "not-a-number");
			inputParams.put(DrnNephropathyTransformerImpl.P_CONC, "not-a-number");
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 6n. collection_res value incorrect",DrnNephropathyTransformerImpl.ABNORMAL, result.get(DrnNephropathyTransformerImpl.COLLECTION_RES));
			
			int tOther = 100;
			double rOther = 0.104166667*tOther;

			//6o. IF collection_tested = yes AND time = other AND (a_mass < r OR p_mass < r) AND (a_conc >= 10 OR p_conc >= 10) THEN collection_res = not_valid
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.COLLECTION_TESTED, DrnNephropathyTransformerImpl.COLLECTION_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.TIME, DrnNephropathyTransformerImpl.TIME_OTHER);
			inputParams.put(DrnNephropathyTransformerImpl.TIME_MIN, Integer.toString(tOther));
			inputParams.put(DrnNephropathyTransformerImpl.A_MASS, Double.toString(rOther-0.1));
			inputParams.put(DrnNephropathyTransformerImpl.P_MASS, Double.toString(rOther-0.1));
			inputParams.put(DrnNephropathyTransformerImpl.A_CONC, Double.toString(10));
			inputParams.put(DrnNephropathyTransformerImpl.P_CONC, Double.toString(10));
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 6o. collection_res value incorrect",DrnNephropathyTransformerImpl.NOT_VALID, result.get(DrnNephropathyTransformerImpl.COLLECTION_RES));

			//6p. IF collection_tested = yes AND time = other AND (a_mass < r OR p_mass < r) AND !(a_conc >= 10 OR p_conc >= 10) THEN collection_res = normal
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.COLLECTION_TESTED, DrnNephropathyTransformerImpl.COLLECTION_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.TIME, DrnNephropathyTransformerImpl.TIME_OTHER);
			inputParams.put(DrnNephropathyTransformerImpl.TIME_MIN, Integer.toString(tOther));
			inputParams.put(DrnNephropathyTransformerImpl.A_MASS, Double.toString(rOther-0.1));
			inputParams.put(DrnNephropathyTransformerImpl.P_MASS, Double.toString(rOther-0.1));
			inputParams.put(DrnNephropathyTransformerImpl.A_CONC, Double.toString(9.9));
			inputParams.put(DrnNephropathyTransformerImpl.P_CONC, Double.toString(9.9));
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 6p. collection_res value incorrect",DrnNephropathyTransformerImpl.NORMAL, result.get(DrnNephropathyTransformerImpl.COLLECTION_RES));

			//6q. IF collection_tested = yes AND time = other AND (a_mass < r OR p_mass < r) AND a_conc = NaN AND p_conc = NaN THEN collection_res = normal
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.COLLECTION_TESTED, DrnNephropathyTransformerImpl.COLLECTION_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.TIME, DrnNephropathyTransformerImpl.TIME_OTHER);
			inputParams.put(DrnNephropathyTransformerImpl.TIME_MIN, Integer.toString(tOther));
			inputParams.put(DrnNephropathyTransformerImpl.A_MASS, Double.toString(rOther-0.1));
			inputParams.put(DrnNephropathyTransformerImpl.P_MASS, Double.toString(rOther-0.1));
			inputParams.put(DrnNephropathyTransformerImpl.A_CONC, "not-a-number");
			inputParams.put(DrnNephropathyTransformerImpl.P_CONC, "not-a-number");
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 6q. collection_res value incorrect",DrnNephropathyTransformerImpl.NORMAL, result.get(DrnNephropathyTransformerImpl.COLLECTION_RES));

			//6r. IF collection_tested = yes AND time = other AND (a_mass >= r OR p_mass >= r) AND (a_conc < 10 OR p_conc < 10) THEN collection_res = not_valid
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.COLLECTION_TESTED, DrnNephropathyTransformerImpl.COLLECTION_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.TIME, DrnNephropathyTransformerImpl.TIME_OTHER);
			inputParams.put(DrnNephropathyTransformerImpl.TIME_MIN, Integer.toString(tOther));
			inputParams.put(DrnNephropathyTransformerImpl.A_MASS, Double.toString(rOther));
			inputParams.put(DrnNephropathyTransformerImpl.P_MASS, Double.toString(rOther));
			inputParams.put(DrnNephropathyTransformerImpl.A_CONC, Double.toString(9.9));
			inputParams.put(DrnNephropathyTransformerImpl.P_CONC, Double.toString(9.9));
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 6r. collection_res value incorrect",DrnNephropathyTransformerImpl.NOT_VALID, result.get(DrnNephropathyTransformerImpl.COLLECTION_RES));

			//6s. IF collection_tested = yes AND time = other AND (a_mass >= r OR p_mass >= r) AND !(a_conc < 10 OR p_conc < 10) THEN collection_res = abnormal
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.COLLECTION_TESTED, DrnNephropathyTransformerImpl.COLLECTION_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.TIME, DrnNephropathyTransformerImpl.TIME_OTHER);
			inputParams.put(DrnNephropathyTransformerImpl.TIME_MIN, Integer.toString(tOther));
			inputParams.put(DrnNephropathyTransformerImpl.A_MASS, Double.toString(rOther));
			inputParams.put(DrnNephropathyTransformerImpl.P_MASS, Double.toString(rOther));
			inputParams.put(DrnNephropathyTransformerImpl.A_CONC, Double.toString(10));
			inputParams.put(DrnNephropathyTransformerImpl.P_CONC, Double.toString(10));
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 6s. collection_res value incorrect",DrnNephropathyTransformerImpl.ABNORMAL, result.get(DrnNephropathyTransformerImpl.COLLECTION_RES));

			//6t. IF collection_tested = yes AND time = other AND (a_mass >= r OR p_mass >= r) AND a_conc = NaN AND p_conc = NaN THEN collection_res = abnormal
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.COLLECTION_TESTED, DrnNephropathyTransformerImpl.COLLECTION_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.TIME, DrnNephropathyTransformerImpl.TIME_OTHER);
			inputParams.put(DrnNephropathyTransformerImpl.TIME_MIN, Integer.toString(tOther));
			inputParams.put(DrnNephropathyTransformerImpl.A_MASS, Double.toString(rOther));
			inputParams.put(DrnNephropathyTransformerImpl.P_MASS, Double.toString(rOther));
			inputParams.put(DrnNephropathyTransformerImpl.A_CONC, "not-a-number");
			inputParams.put(DrnNephropathyTransformerImpl.P_CONC, "not-a-number");
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 6t. collection_res value incorrect",DrnNephropathyTransformerImpl.ABNORMAL, result.get(DrnNephropathyTransformerImpl.COLLECTION_RES));
			
			//6u. IF collection_tested = yes AND time = other AND TIME_MIN = NaN AND (a_conc < 10 OR p_conc < 10) THEN collection_res = normal
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.COLLECTION_TESTED, DrnNephropathyTransformerImpl.COLLECTION_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.TIME, DrnNephropathyTransformerImpl.TIME_OTHER);
			inputParams.put(DrnNephropathyTransformerImpl.TIME_MIN, "not-a-number");
			inputParams.put(DrnNephropathyTransformerImpl.A_CONC, Double.toString(9.9));
			inputParams.put(DrnNephropathyTransformerImpl.P_CONC, Double.toString(9.9));
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 6u. collection_res value incorrect",DrnNephropathyTransformerImpl.NORMAL, result.get(DrnNephropathyTransformerImpl.COLLECTION_RES));
			
			//6v. IF collection_tested = yes AND time = other AND TIME_MIN = NaN AND (a_conc >= 10 OR p_conc >= 10) THEN collection_res = abnormal
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.COLLECTION_TESTED, DrnNephropathyTransformerImpl.COLLECTION_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.TIME, DrnNephropathyTransformerImpl.TIME_OTHER);
			inputParams.put(DrnNephropathyTransformerImpl.TIME_MIN, "not-a-number");
			inputParams.put(DrnNephropathyTransformerImpl.A_CONC, Double.toString(10));
			inputParams.put(DrnNephropathyTransformerImpl.P_CONC, Double.toString(10));
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 6v. collection_res value incorrect",DrnNephropathyTransformerImpl.ABNORMAL, result.get(DrnNephropathyTransformerImpl.COLLECTION_RES));
			
			//6w. IF collection_tested = yes AND time = other AND TIME_MIN = NaN AND a_conc = NaN AND p_conc = NaN THEN collection_res = not_valid
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.COLLECTION_TESTED, DrnNephropathyTransformerImpl.COLLECTION_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.TIME, DrnNephropathyTransformerImpl.TIME_OTHER);
			inputParams.put(DrnNephropathyTransformerImpl.TIME_MIN, "not-a-number");
			inputParams.put(DrnNephropathyTransformerImpl.A_CONC, "not-a-number");
			inputParams.put(DrnNephropathyTransformerImpl.P_CONC, "not-a-number");
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 6w. collection_res value incorrect",DrnNephropathyTransformerImpl.NOT_VALID, result.get(DrnNephropathyTransformerImpl.COLLECTION_RES));
			
			//6x. IF collection_tested = yes AND a_mass = NaN AND p_mass = NaN THEN collection_res = not_valid
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.COLLECTION_TESTED, DrnNephropathyTransformerImpl.COLLECTION_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.TIME, DrnNephropathyTransformerImpl.TIME_24HOURS);
			inputParams.put(DrnNephropathyTransformerImpl.A_MASS, "not-a-number");
			inputParams.put(DrnNephropathyTransformerImpl.P_MASS, "not-a-number");
			inputParams.put(DrnNephropathyTransformerImpl.A_CONC, "not-a-number");
			inputParams.put(DrnNephropathyTransformerImpl.P_CONC, "not-a-number");
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 6x. collection_res value incorrect",DrnNephropathyTransformerImpl.NOT_VALID, result.get(DrnNephropathyTransformerImpl.COLLECTION_RES));
			
			//6y. IF collection_tested = yes AND time is invalid THEN collection_res = not_valid
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.COLLECTION_TESTED, DrnNephropathyTransformerImpl.COLLECTION_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.TIME, DrnNephropathyTransformerImpl.TIME_24HOURS);
			inputParams.put(DrnNephropathyTransformerImpl.A_MASS, "not-a-number");
			inputParams.put(DrnNephropathyTransformerImpl.P_MASS, "not-a-number");
			inputParams.put(DrnNephropathyTransformerImpl.A_CONC, "not-a-number");
			inputParams.put(DrnNephropathyTransformerImpl.P_CONC, "not-a-number");
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 6y. collection_res value incorrect",DrnNephropathyTransformerImpl.NOT_VALID, result.get(DrnNephropathyTransformerImpl.COLLECTION_RES));
			
			//6z. IF collection_tested = yes AND time = not_valid AND (a_conc < 10 OR p_conc < 10) THEN collection_res = normal
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.COLLECTION_TESTED, DrnNephropathyTransformerImpl.COLLECTION_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.TIME, "invalid-time");
			inputParams.put(DrnNephropathyTransformerImpl.A_CONC, Double.toString(9.9));
			inputParams.put(DrnNephropathyTransformerImpl.P_CONC, Double.toString(9.9));
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 6z. collection_res value incorrect",DrnNephropathyTransformerImpl.NORMAL, result.get(DrnNephropathyTransformerImpl.COLLECTION_RES));
			
			//6aa. IF collection_tested = yes AND time = not_valid AND (a_conc >= 10 OR p_conc >= 10) THEN collection_res = abnormal
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.COLLECTION_TESTED, DrnNephropathyTransformerImpl.COLLECTION_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.TIME, "invalid-time");
			inputParams.put(DrnNephropathyTransformerImpl.A_CONC, Double.toString(10));
			inputParams.put(DrnNephropathyTransformerImpl.P_CONC, Double.toString(10));
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 6aa. collection_res value incorrect",DrnNephropathyTransformerImpl.ABNORMAL, result.get(DrnNephropathyTransformerImpl.COLLECTION_RES));
			
			//6ab. IF collection_tested = yes AND time = not_valid AND a_conc = NaN AND p_conc = NaN THEN collection_res = not_valid
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.COLLECTION_TESTED, DrnNephropathyTransformerImpl.COLLECTION_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.TIME, "invalid-time");
			inputParams.put(DrnNephropathyTransformerImpl.A_CONC, "not-a-number");
			inputParams.put(DrnNephropathyTransformerImpl.P_CONC, "not-a-number");
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 6ab. collection_res value incorrect",DrnNephropathyTransformerImpl.NOT_VALID, result.get(DrnNephropathyTransformerImpl.COLLECTION_RES));
			
			//6ac IF collection_tested = yes AND r is valid and a_mass = NaN and p_mass = NaN and (a_conc < 10 or p_conc < 10 ) then collection_res = normal  
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.COLLECTION_TESTED, DrnNephropathyTransformerImpl.COLLECTION_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.TIME, DrnNephropathyTransformerImpl.TIME_24HOURS);
			inputParams.put(DrnNephropathyTransformerImpl.A_MASS, "not-a-number");
			inputParams.put(DrnNephropathyTransformerImpl.P_MASS, "not-a-number");
			inputParams.put(DrnNephropathyTransformerImpl.A_CONC, Double.toString(9.9));
			inputParams.put(DrnNephropathyTransformerImpl.P_CONC, Double.toString(9.9));
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 6ac. collection_res value incorrect",DrnNephropathyTransformerImpl.NORMAL, result.get(DrnNephropathyTransformerImpl.COLLECTION_RES));
			
			//6ad IF collection_tested = yes AND r is valid and a_mass = NaN and p_mass = NaN and (a_conc >= 10 or p_conc >= 10 ) then collection_res = abnormal  
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.COLLECTION_TESTED, DrnNephropathyTransformerImpl.COLLECTION_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.TIME, DrnNephropathyTransformerImpl.TIME_24HOURS);
			inputParams.put(DrnNephropathyTransformerImpl.A_MASS, "not-a-number");
			inputParams.put(DrnNephropathyTransformerImpl.P_MASS, "not-a-number");
			inputParams.put(DrnNephropathyTransformerImpl.A_CONC, Double.toString(10));
			inputParams.put(DrnNephropathyTransformerImpl.P_CONC, Double.toString(10));
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 6ad. collection_res value incorrect",DrnNephropathyTransformerImpl.ABNORMAL, result.get(DrnNephropathyTransformerImpl.COLLECTION_RES));
			
			//6ae IF collection_tested = yes AND r is valid and a_mass = NaN and p_mass = NaN and (a_conc = NaN and p_conc = NaN ) then collection_res = not_valid  
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.COLLECTION_TESTED, DrnNephropathyTransformerImpl.COLLECTION_TESTED_YES);
			inputParams.put(DrnNephropathyTransformerImpl.TIME, DrnNephropathyTransformerImpl.TIME_24HOURS);
			inputParams.put(DrnNephropathyTransformerImpl.A_MASS, "not-a-number");
			inputParams.put(DrnNephropathyTransformerImpl.P_MASS, "not-a-number");
			inputParams.put(DrnNephropathyTransformerImpl.A_CONC, "not-a-number");
			inputParams.put(DrnNephropathyTransformerImpl.P_CONC, "not-a-number");
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 6ae. collection_res value incorrect",DrnNephropathyTransformerImpl.NOT_VALID, result.get(DrnNephropathyTransformerImpl.COLLECTION_RES));
			
			//7. Infection tests
			//7a. Infection = Yes
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.INFECTION, DrnNephropathyTransformerImpl.INFECTION_YES);
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 7a. collection_res value incorrect",DrnNephropathyTransformerImpl.ABNORMAL, result.get(DrnNephropathyTransformerImpl.INFECTION_RES));
			
			//7b. Infection = No
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.INFECTION, DrnNephropathyTransformerImpl.INFECTION_NO);
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 7b. collection_res value incorrect",DrnNephropathyTransformerImpl.NORMAL, result.get(DrnNephropathyTransformerImpl.INFECTION_RES));
			
			//7c. Infection != Yes and != No
			initInputMap(inputParams);
			inputParams.put(DrnNephropathyTransformerImpl.INFECTION, "not-yes-or-no");
			result = DrnNephropathyTransformerImpl.calculateIntermediateParameters(inputParams);
			assertEquals("Test 7c. collection_res value incorrect",DrnNephropathyTransformerImpl.NOT_VALID, result.get(DrnNephropathyTransformerImpl.INFECTION_RES));
			
		}
		catch(Exception ex){
			ex.printStackTrace();
			fail(ex.getMessage());
		}
	}
	
	public void testLookupNephroResult(){
		try{
			//Test 1
			String result = DrnNephropathyTransformerImpl.lookupNephroResult(
					initIntermediateMap(
							DrnNephropathyTransformerImpl.NOT_VALID, 
							DrnNephropathyTransformerImpl.NOT_VALID, 
							DrnNephropathyTransformerImpl.NOT_VALID, 
							DrnNephropathyTransformerImpl.NOT_VALID, 
							DrnNephropathyTransformerImpl.NOT_VALID, 
							"anything"));
			assertEquals("Test 1. nephro value incorrect", DrnNephropathyTransformerImpl.NOT_INTERPRETED, result);
			
			//Test 2
			result = DrnNephropathyTransformerImpl.lookupNephroResult(
					initIntermediateMap(
							DrnNephropathyTransformerImpl.NORMAL, 
							DrnNephropathyTransformerImpl.NOT_VALID, 
							DrnNephropathyTransformerImpl.NOT_VALID, 
							DrnNephropathyTransformerImpl.NOT_VALID, 
							DrnNephropathyTransformerImpl.NOT_VALID, 
							"anything"));
			assertEquals("Test 2. nephro value incorrect", DrnNephropathyTransformerImpl.NORMAL, result);
			
			//Test 3
			result = DrnNephropathyTransformerImpl.lookupNephroResult(
					initIntermediateMap(
							DrnNephropathyTransformerImpl.ABNORMAL, 
							DrnNephropathyTransformerImpl.NOT_VALID, 
							DrnNephropathyTransformerImpl.NOT_VALID, 
							DrnNephropathyTransformerImpl.NOT_VALID, 
							DrnNephropathyTransformerImpl.NOT_VALID, 
							"anything"));
			assertEquals("Test 3. nephro value incorrect", DrnNephropathyTransformerImpl.EQUIVOCAL, result);
			
			//Test 4
			result = DrnNephropathyTransformerImpl.lookupNephroResult(
					initIntermediateMap(
							"anything", 
							DrnNephropathyTransformerImpl.NORMAL, 
							DrnNephropathyTransformerImpl.NOT_VALID, 
							DrnNephropathyTransformerImpl.NOT_VALID, 
							DrnNephropathyTransformerImpl.NOT_VALID, 
							"anything"));
			assertEquals("Test 4. nephro value incorrect", DrnNephropathyTransformerImpl.NORMAL, result);
			
			//Test 5
			result = DrnNephropathyTransformerImpl.lookupNephroResult(
					initIntermediateMap(
							"anything", 
							DrnNephropathyTransformerImpl.ABNORMAL, 
							DrnNephropathyTransformerImpl.NOT_VALID, 
							DrnNephropathyTransformerImpl.NOT_VALID, 
							DrnNephropathyTransformerImpl.NOT_VALID, 
							"anything"));
			assertEquals("Test 5. nephro value incorrect", DrnNephropathyTransformerImpl.EQUIVOCAL, result);
			
			//Test 6
			result = DrnNephropathyTransformerImpl.lookupNephroResult(
					initIntermediateMap(
							"anything", 
							"anything", 
							DrnNephropathyTransformerImpl.NOT_VALID, 
							DrnNephropathyTransformerImpl.NOT_VALID, 
							DrnNephropathyTransformerImpl.NORMAL, 
							"anything"));
			assertEquals("Test 6. nephro value incorrect", DrnNephropathyTransformerImpl.NORMAL, result);
			
			//Test 7
			result = DrnNephropathyTransformerImpl.lookupNephroResult(
					initIntermediateMap(
							"anything", 
							"anything", 
							DrnNephropathyTransformerImpl.NOT_VALID, 
							DrnNephropathyTransformerImpl.NOT_VALID, 
							DrnNephropathyTransformerImpl.ABNORMAL, 
							"anything"));
			assertEquals("Test 7. nephro value incorrect", DrnNephropathyTransformerImpl.NOT_INTERPRETED, result);
			
			//Test 8
			result = DrnNephropathyTransformerImpl.lookupNephroResult(
					initIntermediateMap(
							"anything", 
							"anything", 
							DrnNephropathyTransformerImpl.NOT_VALID, 
							DrnNephropathyTransformerImpl.NORMAL, 
							DrnNephropathyTransformerImpl.NOT_VALID, 
							"anything"));
			assertEquals("Test 8. nephro value incorrect", DrnNephropathyTransformerImpl.NORMAL, result);
			
			//Test 9
			result = DrnNephropathyTransformerImpl.lookupNephroResult(
					initIntermediateMap(
							"anything", 
							"anything", 
							DrnNephropathyTransformerImpl.NOT_VALID, 
							DrnNephropathyTransformerImpl.NORMAL, 
							DrnNephropathyTransformerImpl.NORMAL, 
							"anything"));
			assertEquals("Test 9. nephro value incorrect", DrnNephropathyTransformerImpl.NORMAL, result);
			
			//Test 10
			result = DrnNephropathyTransformerImpl.lookupNephroResult(
					initIntermediateMap(
							"anything", 
							"anything", 
							DrnNephropathyTransformerImpl.NOT_VALID, 
							DrnNephropathyTransformerImpl.NORMAL, 
							DrnNephropathyTransformerImpl.ABNORMAL, 
							"anything"));
			assertEquals("Test 10. nephro value incorrect", DrnNephropathyTransformerImpl.EQUIVOCAL, result);
			
			//Test 11
			result = DrnNephropathyTransformerImpl.lookupNephroResult(
					initIntermediateMap(
							"anything", 
							"anything", 
							DrnNephropathyTransformerImpl.NOT_VALID, 
							DrnNephropathyTransformerImpl.ABNORMAL, 
							DrnNephropathyTransformerImpl.NOT_VALID, 
							"anything"));
			assertEquals("Test 11. nephro value incorrect", DrnNephropathyTransformerImpl.ABNORMAL, result);
			
			//Test 12
			result = DrnNephropathyTransformerImpl.lookupNephroResult(
					initIntermediateMap(
							"anything", 
							"anything", 
							DrnNephropathyTransformerImpl.NOT_VALID, 
							DrnNephropathyTransformerImpl.ABNORMAL, 
							DrnNephropathyTransformerImpl.NORMAL, 
							"anything"));
			assertEquals("Test 12. nephro value incorrect", DrnNephropathyTransformerImpl.EQUIVOCAL, result);
			
			//Test 13
			result = DrnNephropathyTransformerImpl.lookupNephroResult(
					initIntermediateMap(
							"anything", 
							"anything", 
							DrnNephropathyTransformerImpl.NOT_VALID, 
							DrnNephropathyTransformerImpl.ABNORMAL, 
							DrnNephropathyTransformerImpl.ABNORMAL, 
							"anything"));
			assertEquals("Test 13. nephro value incorrect", DrnNephropathyTransformerImpl.ABNORMAL, result);
			
			//Test 14
			result = DrnNephropathyTransformerImpl.lookupNephroResult(
					initIntermediateMap(
							"anything", 
							"anything", 
							DrnNephropathyTransformerImpl.NORMAL, 
							DrnNephropathyTransformerImpl.NOT_VALID, 
							DrnNephropathyTransformerImpl.NOT_VALID, 
							"anything"));
			assertEquals("Test 14. nephro value incorrect", DrnNephropathyTransformerImpl.NORMAL, result);
			
			//Test 15
			result = DrnNephropathyTransformerImpl.lookupNephroResult(
					initIntermediateMap(
							"anything", 
							"anything", 
							DrnNephropathyTransformerImpl.NORMAL, 
							DrnNephropathyTransformerImpl.NOT_VALID, 
							DrnNephropathyTransformerImpl.NORMAL, 
							"anything"));
			assertEquals("Test 15. nephro value incorrect", DrnNephropathyTransformerImpl.NORMAL, result);
			
			//Test 16
			result = DrnNephropathyTransformerImpl.lookupNephroResult(
					initIntermediateMap(
							"anything", 
							"anything", 
							DrnNephropathyTransformerImpl.NORMAL, 
							DrnNephropathyTransformerImpl.NOT_VALID, 
							DrnNephropathyTransformerImpl.ABNORMAL, 
							"anything"));
			assertEquals("Test 16. nephro value incorrect", DrnNephropathyTransformerImpl.EQUIVOCAL, result);
			
			//Test 17
			result = DrnNephropathyTransformerImpl.lookupNephroResult(
					initIntermediateMap(
							"anything", 
							"anything", 
							DrnNephropathyTransformerImpl.NORMAL, 
							DrnNephropathyTransformerImpl.NORMAL, 
							DrnNephropathyTransformerImpl.NOT_VALID, 
							"anything"));
			assertEquals("Test 17. nephro value incorrect", DrnNephropathyTransformerImpl.NORMAL, result);
			
			//Test 18
			result = DrnNephropathyTransformerImpl.lookupNephroResult(
					initIntermediateMap(
							"anything", 
							"anything", 
							DrnNephropathyTransformerImpl.NORMAL, 
							DrnNephropathyTransformerImpl.NORMAL, 
							DrnNephropathyTransformerImpl.NORMAL, 
							"anything"));
			assertEquals("Test 18. nephro value incorrect", DrnNephropathyTransformerImpl.NORMAL, result);
			
			//Test 19
			result = DrnNephropathyTransformerImpl.lookupNephroResult(
					initIntermediateMap(
							"anything", 
							"anything", 
							DrnNephropathyTransformerImpl.NORMAL, 
							DrnNephropathyTransformerImpl.NORMAL, 
							DrnNephropathyTransformerImpl.ABNORMAL, 
							"anything"));
			assertEquals("Test 19. nephro value incorrect", DrnNephropathyTransformerImpl.EQUIVOCAL, result);
			
			//Test 20
			result = DrnNephropathyTransformerImpl.lookupNephroResult(
					initIntermediateMap(
							"anything", 
							"anything", 
							DrnNephropathyTransformerImpl.NORMAL, 
							DrnNephropathyTransformerImpl.ABNORMAL, 
							DrnNephropathyTransformerImpl.NOT_VALID, 
							"anything"));
			assertEquals("Test 20. nephro value incorrect", DrnNephropathyTransformerImpl.EQUIVOCAL, result);
			
			//Test 21
			result = DrnNephropathyTransformerImpl.lookupNephroResult(
					initIntermediateMap(
							"anything", 
							"anything", 
							DrnNephropathyTransformerImpl.NORMAL, 
							DrnNephropathyTransformerImpl.ABNORMAL, 
							DrnNephropathyTransformerImpl.NORMAL, 
							"anything"));
			assertEquals("Test 21. nephro value incorrect", DrnNephropathyTransformerImpl.EQUIVOCAL, result);
			
			//Test 22
			result = DrnNephropathyTransformerImpl.lookupNephroResult(
					initIntermediateMap(
							"anything", 
							"anything", 
							DrnNephropathyTransformerImpl.NORMAL, 
							DrnNephropathyTransformerImpl.ABNORMAL, 
							DrnNephropathyTransformerImpl.ABNORMAL, 
							"anything"));
			assertEquals("Test 22. nephro value incorrect", DrnNephropathyTransformerImpl.EQUIVOCAL, result);
			
			//Test 23
			result = DrnNephropathyTransformerImpl.lookupNephroResult(
					initIntermediateMap(
							"anything", 
							"anything", 
							DrnNephropathyTransformerImpl.ABNORMAL, 
							DrnNephropathyTransformerImpl.NOT_VALID, 
							DrnNephropathyTransformerImpl.NOT_VALID, 
							DrnNephropathyTransformerImpl.ABNORMAL));
			assertEquals("Test 23. nephro value incorrect", DrnNephropathyTransformerImpl.EQUIVOCAL, result);
			
			//Test 24
			result = DrnNephropathyTransformerImpl.lookupNephroResult(
					initIntermediateMap(
							"anything", 
							"anything", 
							DrnNephropathyTransformerImpl.ABNORMAL, 
							DrnNephropathyTransformerImpl.NOT_VALID, 
							DrnNephropathyTransformerImpl.NOT_VALID, 
							DrnNephropathyTransformerImpl.NORMAL));
			assertEquals("Test 24. nephro value incorrect", DrnNephropathyTransformerImpl.ABNORMAL, result);
			
			//Test 25
			result = DrnNephropathyTransformerImpl.lookupNephroResult(
					initIntermediateMap(
							"anything", 
							"anything", 
							DrnNephropathyTransformerImpl.ABNORMAL, 
							DrnNephropathyTransformerImpl.NOT_VALID, 
							DrnNephropathyTransformerImpl.NOT_VALID, 
							DrnNephropathyTransformerImpl.NOT_VALID));
			assertEquals("Test 25. nephro value incorrect", DrnNephropathyTransformerImpl.EQUIVOCAL, result);
			
			//Test 26
			result = DrnNephropathyTransformerImpl.lookupNephroResult(
					initIntermediateMap(
							"anything", 
							"anything", 
							DrnNephropathyTransformerImpl.ABNORMAL, 
							DrnNephropathyTransformerImpl.NOT_VALID, 
							DrnNephropathyTransformerImpl.NORMAL, 
							"anything"));
			assertEquals("Test 26. nephro value incorrect", DrnNephropathyTransformerImpl.EQUIVOCAL, result);
			
			//Test 27
			result = DrnNephropathyTransformerImpl.lookupNephroResult(
					initIntermediateMap(
							"anything", 
							"anything", 
							DrnNephropathyTransformerImpl.ABNORMAL, 
							DrnNephropathyTransformerImpl.NOT_VALID, 
							DrnNephropathyTransformerImpl.ABNORMAL, 
							DrnNephropathyTransformerImpl.ABNORMAL));
			assertEquals("Test 27. nephro value incorrect", DrnNephropathyTransformerImpl.EQUIVOCAL, result);
			
			//Test 28
			result = DrnNephropathyTransformerImpl.lookupNephroResult(
					initIntermediateMap(
							"anything", 
							"anything", 
							DrnNephropathyTransformerImpl.ABNORMAL, 
							DrnNephropathyTransformerImpl.NOT_VALID, 
							DrnNephropathyTransformerImpl.ABNORMAL, 
							DrnNephropathyTransformerImpl.NORMAL));
			assertEquals("Test 28. nephro value incorrect", DrnNephropathyTransformerImpl.ABNORMAL, result);
			
			//Test 29
			result = DrnNephropathyTransformerImpl.lookupNephroResult(
					initIntermediateMap(
							"anything", 
							"anything", 
							DrnNephropathyTransformerImpl.ABNORMAL, 
							DrnNephropathyTransformerImpl.NOT_VALID, 
							DrnNephropathyTransformerImpl.ABNORMAL, 
							DrnNephropathyTransformerImpl.NOT_VALID));
			assertEquals("Test 29. nephro value incorrect", DrnNephropathyTransformerImpl.EQUIVOCAL, result);
			
			//Test 30
			result = DrnNephropathyTransformerImpl.lookupNephroResult(
					initIntermediateMap(
							"anything", 
							"anything", 
							DrnNephropathyTransformerImpl.ABNORMAL, 
							DrnNephropathyTransformerImpl.NORMAL, 
							DrnNephropathyTransformerImpl.NOT_VALID, 
							DrnNephropathyTransformerImpl.ABNORMAL));
			assertEquals("Test 30. nephro value incorrect", DrnNephropathyTransformerImpl.EQUIVOCAL, result);
			
			//Test 31
			result = DrnNephropathyTransformerImpl.lookupNephroResult(
					initIntermediateMap(
							"anything", 
							"anything", 
							DrnNephropathyTransformerImpl.ABNORMAL, 
							DrnNephropathyTransformerImpl.NORMAL, 
							DrnNephropathyTransformerImpl.NOT_VALID, 
							DrnNephropathyTransformerImpl.NORMAL));
			assertEquals("Test 31. nephro value incorrect", DrnNephropathyTransformerImpl.ABNORMAL, result);
			
			//Test 32
			result = DrnNephropathyTransformerImpl.lookupNephroResult(
					initIntermediateMap(
							"anything", 
							"anything", 
							DrnNephropathyTransformerImpl.ABNORMAL, 
							DrnNephropathyTransformerImpl.NORMAL, 
							DrnNephropathyTransformerImpl.NOT_VALID, 
							DrnNephropathyTransformerImpl.NOT_VALID));
			assertEquals("Test 32. nephro value incorrect", DrnNephropathyTransformerImpl.EQUIVOCAL, result);
			
			//Test 33
			result = DrnNephropathyTransformerImpl.lookupNephroResult(
					initIntermediateMap(
							"anything", 
							"anything", 
							DrnNephropathyTransformerImpl.ABNORMAL, 
							DrnNephropathyTransformerImpl.NORMAL, 
							DrnNephropathyTransformerImpl.NORMAL, 
							"anything"));
			assertEquals("Test 33. nephro value incorrect", DrnNephropathyTransformerImpl.EQUIVOCAL, result);
			
			//Test 34
			result = DrnNephropathyTransformerImpl.lookupNephroResult(
					initIntermediateMap(
							"anything", 
							"anything", 
							DrnNephropathyTransformerImpl.ABNORMAL, 
							DrnNephropathyTransformerImpl.NORMAL, 
							DrnNephropathyTransformerImpl.ABNORMAL, 
							DrnNephropathyTransformerImpl.ABNORMAL));
			assertEquals("Test 34. nephro value incorrect", DrnNephropathyTransformerImpl.EQUIVOCAL, result);
			
			//Test 35
			result = DrnNephropathyTransformerImpl.lookupNephroResult(
					initIntermediateMap(
							"anything", 
							"anything", 
							DrnNephropathyTransformerImpl.ABNORMAL, 
							DrnNephropathyTransformerImpl.NORMAL, 
							DrnNephropathyTransformerImpl.ABNORMAL, 
							DrnNephropathyTransformerImpl.NORMAL));
			assertEquals("Test 35. nephro value incorrect", DrnNephropathyTransformerImpl.ABNORMAL, result);
			
			//Test 36
			result = DrnNephropathyTransformerImpl.lookupNephroResult(
					initIntermediateMap(
							"anything", 
							"anything", 
							DrnNephropathyTransformerImpl.ABNORMAL, 
							DrnNephropathyTransformerImpl.NORMAL, 
							DrnNephropathyTransformerImpl.ABNORMAL, 
							DrnNephropathyTransformerImpl.NOT_VALID));
			assertEquals("Test 36. nephro value incorrect", DrnNephropathyTransformerImpl.EQUIVOCAL, result);
			
			//Test 37
			result = DrnNephropathyTransformerImpl.lookupNephroResult(
					initIntermediateMap(
							"anything", 
							"anything", 
							DrnNephropathyTransformerImpl.ABNORMAL, 
							DrnNephropathyTransformerImpl.ABNORMAL, 
							DrnNephropathyTransformerImpl.NOT_VALID, 
							"anything"));
			assertEquals("Test 37. nephro value incorrect", DrnNephropathyTransformerImpl.ABNORMAL, result);
			
			//Test 38
			result = DrnNephropathyTransformerImpl.lookupNephroResult(
					initIntermediateMap(
							"anything", 
							"anything", 
							DrnNephropathyTransformerImpl.ABNORMAL, 
							DrnNephropathyTransformerImpl.ABNORMAL, 
							DrnNephropathyTransformerImpl.NORMAL, 
							"anything"));
			assertEquals("Test 38. nephro value incorrect", DrnNephropathyTransformerImpl.EQUIVOCAL, result);
			
			//Test 39
			result = DrnNephropathyTransformerImpl.lookupNephroResult(
					initIntermediateMap(
							"anything", 
							"anything", 
							DrnNephropathyTransformerImpl.ABNORMAL, 
							DrnNephropathyTransformerImpl.ABNORMAL, 
							DrnNephropathyTransformerImpl.ABNORMAL, 
							"anything"));
			assertEquals("Test 38. nephro value incorrect", DrnNephropathyTransformerImpl.ABNORMAL, result);
			
		}
		catch(Exception ex){
			ex.printStackTrace();
			fail(ex.getMessage());
		}
	}

	private void initInputMap(Map<String, String> map){
		map.clear();
		map.put(DrnNephropathyTransformerImpl.URINARY_ALBUMIN_OR_PROTEIN, DrnNephropathyTransformerImpl.URINARY_ALBUMIN_OR_PROTEIN_YES);
		map.put(DrnNephropathyTransformerImpl.MAU_DS_TESTED, DrnNephropathyTransformerImpl.MAU_DS_TESTED_NO);
		map.put(DrnNephropathyTransformerImpl.PU_DS_TESTED, DrnNephropathyTransformerImpl.PU_DS_TESTED_NO);
		map.put(DrnNephropathyTransformerImpl.ACR_TESTED, DrnNephropathyTransformerImpl.ACR_TESTED_NO);
		map.put(DrnNephropathyTransformerImpl.PCR_TESTED, DrnNephropathyTransformerImpl.PCR_TESTED_NO);
		map.put(DrnNephropathyTransformerImpl.COLLECTION_TESTED, DrnNephropathyTransformerImpl.COLLECTION_TESTED_NO);
		map.put(DrnNephropathyTransformerImpl.INFECTION, DrnNephropathyTransformerImpl.INFECTION_NO);
	}

	private Map<String, String> initIntermediateMap(String mauDsRes, String puDsRes, String acrRes, String pcrRes, String collectionRes, String infectionRes){
		Map<String, String> map = new HashMap<String, String>();
		map.put(DrnNephropathyTransformerImpl.MAU_DS_RES, mauDsRes);
		map.put(DrnNephropathyTransformerImpl.PU_DS_RES, puDsRes);
		map.put(DrnNephropathyTransformerImpl.ACR_RES, acrRes);
		map.put(DrnNephropathyTransformerImpl.PCR_RES, pcrRes);
		map.put(DrnNephropathyTransformerImpl.COLLECTION_RES, collectionRes);
		map.put(DrnNephropathyTransformerImpl.INFECTION_RES, infectionRes);
		return map;
	}
	
}

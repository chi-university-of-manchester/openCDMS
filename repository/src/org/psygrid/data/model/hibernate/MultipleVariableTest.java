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
package org.psygrid.data.model.hibernate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;

import org.psygrid.collection.entry.jeval.If;
import org.psygrid.collection.entry.jeval.JEvalHelper;

/**
 * This class encapsulates a collection of individual test cases
 * that are to be run collectively against a derived entry or an external derived
 * entry.
 * 
 * @author Bill Vance
 * 
 @DEL_REP_MULTIVARTEST_TABLE_TAG@
 *
 */
public class MultipleVariableTest extends Element {
	
    private static BigDecimal NUM_MS_IN_DAY = BigDecimal
    .valueOf(1000 * 60 * 60 * 24);
	
	private List<MultipleVariableTestCase> testCases = new ArrayList<MultipleVariableTestCase>();
	private List<MultipleVariableTestCase> failedTests = new ArrayList<MultipleVariableTestCase>();

	public MultipleVariableTest() {
	}
	
	public MultipleVariableTest(String name) {
		this.setName(name);
	}
	
	public MultipleVariableTest(String name, String description) {
		this.setName(name);
		this.setDescription(description);
	}
	
	/**
	 * Adds a test case to this test.
	 * @param testCase
	 */
	public void addTestCase(MultipleVariableTestCase testCase) {
		testCases.add(testCase);
	}
	
	/**
	 * Removes a test case from this test. It does so by binary
	 * comparison within its current list of tests. If it cannot find a match
	 * then nothing happens.
	 * 
	 * @param testCase
	 */
	public void removeTestCase(MultipleVariableTestCase testCase){
		testCases.remove(testCase);
	}
	
	/**
	* Returns the list of test cases that make up this test.
	* 
	* @DEL_REP_MULTIVARTEST_TESTCASES_TAG@
	*/
	public List<MultipleVariableTestCase> getTestCases() {
		return testCases;
	}
	
	/**
	 * Returns the list of failed test cases for the last test that was run.
	 * If successful then it returns a zero-length list.
	 * The individual test cases in the list can be queried to determine what output they
	 * actually generated to help failure analysis.
	 * 
	 * @return
	 */
	public List<MultipleVariableTestCase> getFailedTestCases(){
		return failedTests;
	}
	
	public void setTestCases(List<MultipleVariableTestCase> testCases) {
		this.testCases = testCases;
	}
	
	/**
	 * Runs the test.
	 * @param testSubject - the Derived Entry that 'owns' this test.
	 * @return
	 * @throws EvaluationException 
	 */
	public boolean test(DerivedEntry testSubject) throws EvaluationException {
		
		Evaluator evaluator = null;
     	
    	boolean completeSuccess = true;
    	
    	List<MultipleVariableTestCase> testCases = getTestCases();
    	for(MultipleVariableTestCase tc : testCases){
    		
    		evaluator = new Evaluator();
    		evaluator.putFunction(new If());
    		
    		//Convert all values to doubles.
    		//Put all values, along with corresponding variable names
    		//into the evaluator.
    		
    		Map<String, Value> variableMap = tc.getTestMap();
    		Set<String> keySet = variableMap.keySet();
    		List<String> keySetList = new ArrayList<String>();
    		
    		for(String varName : keySet){
    			keySetList.add(varName);
    			Value testValue = variableMap.get(varName);
    			Double convertedTestValue = this.getValueAsDouble(testValue);
    			evaluator.putVariable(varName, convertedTestValue.toString());
    		}
    		
	    	String jevalFormula = JEvalHelper.escapeVariablesInFormula(testSubject.getFormula(), keySetList);
	    	Double testResult = new Double(evaluator.getNumberResult(jevalFormula));
	    	
	    	if(!testResult.equals(tc.getResponse())){
	    		completeSuccess = false;
	    		tc.setResponseToLastTest(testResult);
	    		failedTests.add(tc);
	    	}
    	
    	}
		return completeSuccess;
	}
	
	/**
	 * Removes all 'failed' test cases and removes all of the last test results.
	 *
	 */
	public void resetTest(){
		failedTests = new ArrayList<MultipleVariableTestCase>();
		for(MultipleVariableTestCase tc: this.testCases){
			tc.setResponseToLastTest(null);
		}
		
	}
	
	
	
   private Double getValueAsDouble(Value theValue) {

        if (theValue instanceof DateValue) {
        	Date theDate = ((DateValue)theValue).getValue();
            return getDateAsNumberOfDays(theDate);
        }

        Object variableValue = theValue.getTheValue();
        if (variableValue == null) {
            return null;
        }
        if (variableValue instanceof Double) {
            return (Double) variableValue;
        }
        if (variableValue instanceof Option) {
            Integer code = ((Option) variableValue).getCode();
            if (code == null) {
                return Double.valueOf(0);
            }
            return Double.valueOf(code.intValue());
            
        }
        if (variableValue instanceof Integer) {
            return Double.valueOf(((Integer) variableValue).intValue());
        }

        throw new IllegalStateException("Variable of wrong value type stored: " //$NON-NLS-1$
                + variableValue.getClass());
    }

    private Double getDateAsNumberOfDays(Date date) {

        BigDecimal dateValue = BigDecimal.valueOf(date.getTime());
        BigDecimal numOfDays = dateValue.divide(NUM_MS_IN_DAY, 1,
                RoundingMode.HALF_DOWN);
        return Double.valueOf(numOfDays.doubleValue());
    }
	
	/**
	 * Runs the test.
	 * @param testSubject the External derived entry that 'owns' this test.
	 * @return
	 */
	public boolean test(ExternalDerivedEntry testSubject){
		return isEditable;
		
	}
	
	@Override
	protected void addChildTasks(DataSet ds) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public org.psygrid.data.model.dto.ElementDTO instantiateDTO() {
		return new org.psygrid.data.model.dto.MultipleVariableTestDTO();
	}

	@Override
	public org.psygrid.data.model.dto.ElementDTO toDTO() {
		 Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
         org.psygrid.data.model.dto.ElementDTO dtoR = toDTO(dtoRefs, RetrieveDepth.DS_COMPLETE);
         return dtoR;
	}
	
	public org.psygrid.data.model.dto.MultipleVariableTestDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
		
        //check for an already existing instance of a dto object for this 
        //derived entry in the map of references
        org.psygrid.data.model.dto.MultipleVariableTestDTO dtoMVT = null;
        if ( dtoRefs.containsKey(this)){
            dtoMVT = (org.psygrid.data.model.dto.MultipleVariableTestDTO)dtoRefs.get(this);
        }
        if ( null == dtoMVT ){
            //an instance of the derived entry has not already
            //been created, so create it, and add it to the 
            //map of references
            dtoMVT = new org.psygrid.data.model.dto.MultipleVariableTestDTO();
            dtoRefs.put(this, dtoMVT);
            toDTO(dtoMVT, dtoRefs, depth);
        }
        
        return dtoMVT;
		
	}
	
    public void toDTO(org.psygrid.data.model.dto.MultipleVariableTestDTO dtoMVT, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
		super.toDTO(dtoMVT, dtoRefs, depth);
		
		org.psygrid.data.model.dto.MultipleVariableTestCaseDTO[] testCases = new org.psygrid.data.model.dto.MultipleVariableTestCaseDTO[this.testCases.size()];
		int counter = 0;
		for(MultipleVariableTestCase mVTC: this.testCases){
			testCases[counter] =  mVTC.toDTO(dtoRefs, depth);
			counter++;
		}
		
		dtoMVT.setTestCases(testCases);
		
		
		
		org.psygrid.data.model.dto.MultipleVariableTestCaseDTO[] failedTestCases = new org.psygrid.data.model.dto.MultipleVariableTestCaseDTO[this.testCases.size()];
		counter = 0;
		for(MultipleVariableTestCase mVTC: this.failedTests){
			testCases[counter] =  mVTC.toDTO(dtoRefs, depth);
			counter++;
		}
		
		dtoMVT.setFailedTests(failedTestCases);
    }

	public List<MultipleVariableTestCase> getFailedTests() {
		return failedTests;
	}

	public void setFailedTests(List<MultipleVariableTestCase> failedTests) {
		this.failedTests = failedTests;
	}

}

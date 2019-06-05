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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class encapsulates a number of single-variable test cases,
 * to be used for validating a validation rule.
 * 
 * @author Bill Vance
 * 
 * @DEL_REP_SINGLEVARTEST_TABLE_TAG@
 *
 */
public class SingleVariableTest extends Element {

	private List<SingleVariableTestCase> testCases = new ArrayList<SingleVariableTestCase>();
	private List<SingleVariableTestCase> failedTests = new ArrayList<SingleVariableTestCase>();


	public SingleVariableTest(){

	}

	public SingleVariableTest(String name) {
		this.setName(name);
	}

	public SingleVariableTest(String name, String description) {
		this.setName(name);
		this.setDescription(description);
	}

	/**
	 * Add a test case to this test.
	 * @param testCase
	 */
	public void addTest(SingleVariableTestCase testCase) {
		testCases.add(testCase);
	}

	/**
	 * Removes a test case from this test via a binary comparison of its
	 * current list contents.
	 * 
	 * If it can't find a match, nothing happens.
	 * 
	 * @param testCase
	 */
	public void removeTest(SingleVariableTestCase testCase) {
		testCases.remove(testCase);
	}

	/**
	 * Returns the list of test cases that comprise this test.
	 * @DEL_REP_SINGLEVARTEST_TESTCASES_TAG@
	 */
	public List<SingleVariableTestCase> getTestCases() {
		return testCases;
	}

	public void setTestCases(List<SingleVariableTestCase> testCases) {
		this.testCases = testCases;
	}

	/**
	 * Runs the test.
	 * @param testSubject - the Validation Rule that 'owns' this test.
	 * @return
	 */
	public boolean test(ValidationRule testSubject) {

		boolean success = true;

		for(SingleVariableTestCase tC: testCases){
			Value input = tC.getTestInput();
			boolean output = tC.getTestOutput();

			List<String> results = testSubject.validate(input.getTheValue());
			if(results.size() > 0){ //failed
				if(output == true){
					//Test was supposed to pass, but failed.
					success = false;
					tC.setFailureDetails(results);
					failedTests.add(tC);
				}else{ 
					//Test was supposed to fail, so it passed.
				}
			}else{ //test passed
				if(output == true){
					//Test was supposed to succeed, so it passed.
				}else{
					//Test was supposed to not succeed but it did, so this is a failure
					success = false;
					tC.setFailureDetails(results);
					failedTests.add(tC);
				}
			}
		}
		return success;
	}

	/**
	 * Returns the list of test cases that failed from the last test run.
	 * Returns a zero-length list if it was successful.
	 * @return
	 */
	public List<SingleVariableTestCase> getFailedTestCases(){
		return failedTests;
	}

	public void setFailedTestCases(List<SingleVariableTestCase> failedTestCases){
		this.failedTests = failedTestCases;
	}

	public void resetTest(){
		failedTests = new ArrayList<SingleVariableTestCase>();
		if (testCases != null) {
			//Remove the previous failure details
			for (SingleVariableTestCase testCase: testCases) {
				testCase.setFailureDetails(new ArrayList<String>());
			}
		}
	}

	@Override
	public org.psygrid.data.model.dto.ElementDTO instantiateDTO() {
		return new org.psygrid.data.model.dto.SingleVariableTestDTO();
	}

	@Override
	public org.psygrid.data.model.dto.ElementDTO toDTO() {

		Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
		org.psygrid.data.model.dto.ElementDTO dtoR = toDTO(dtoRefs, RetrieveDepth.DS_COMPLETE);
		return dtoR;

	}

	public org.psygrid.data.model.dto.SingleVariableTestDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {

		//check for an already existing instance of a dto object for this 
		//derived entry in the map of references
		org.psygrid.data.model.dto.SingleVariableTestDTO dtoSVT = null;
		if ( dtoRefs.containsKey(this)){
			dtoSVT = (org.psygrid.data.model.dto.SingleVariableTestDTO)dtoRefs.get(this);
		}
		if ( null == dtoSVT ){
			//an instance of the derived entry has not already
			//been created, so create it, and add it to the 
			//map of references
			dtoSVT = new org.psygrid.data.model.dto.SingleVariableTestDTO();
			dtoRefs.put(this, dtoSVT);
			toDTO(dtoSVT, dtoRefs, depth);
		}

		return dtoSVT;

	}

	public void toDTO(org.psygrid.data.model.dto.SingleVariableTestDTO dtoSVT, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
		super.toDTO(dtoSVT, dtoRefs, depth);

		org.psygrid.data.model.dto.SingleVariableTestCaseDTO[] dtoTestCases = new org.psygrid.data.model.dto.SingleVariableTestCaseDTO[testCases.size()];
		int counter = 0;
		for(SingleVariableTestCase testCase: this.testCases){
			dtoTestCases[counter] = testCase.toDTO(dtoRefs, depth);
			counter++;
		}

		dtoSVT.setTestCases(dtoTestCases);

		org.psygrid.data.model.dto.SingleVariableTestCaseDTO[] failedDTOTestCases = new org.psygrid.data.model.dto.SingleVariableTestCaseDTO[failedTests.size()];
		counter = 0;
		for(SingleVariableTestCase testCase: this.failedTests){
			failedDTOTestCases[counter] = testCase.toDTO(dtoRefs, depth);
			counter++;
		}

		dtoSVT.setFailedTests(failedDTOTestCases);
	}


	@Override
	protected void addChildTasks(DataSet ds) {
		// TODO Auto-generated method stub

	}

}

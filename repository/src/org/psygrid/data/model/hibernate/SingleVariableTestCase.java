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
import java.util.List;
import java.util.Map;


/**
 * This class encapsulates a single test case for validating a validation rule.
 * Each 'case' consists of a test input value, and the expected boolean output that
 * the validation rule should produce.
 * 
 * @author Bill
 * 
 * @DEL_REP_SINGLEVARTESTCASE_TABLE_TAG@
 *
 */
public class SingleVariableTestCase extends Persistent {

	private Value testInput;
	private boolean testOutput;
	private List<String> failureDetails = new ArrayList<String>();
	
	
	public SingleVariableTestCase(){
		
	}
	
	public SingleVariableTestCase(Value input, boolean output) {
		testInput = input;
		testOutput = output;
	}
	
    /**
     * Returns the test input.
     * 
     * @DEL_REP_SINGLEVARTESTCASE_TESTINPUT_TAG@
     * 
     */
	public Value getTestInput() {
		return testInput;
	}


	public void setTestInput(Value testInput) {
		this.testInput = testInput;
	}


	/**
	 * Returns the expected (correct) output for this test.
	 * 
	 * @DEL_REP_SINGLEVARTESTCASE_CORRECTOUTPUT_TAG@
	 */
	public boolean getTestOutput() {
		return testOutput;
	}


	public void setTestOutput(boolean testOutput) {
		this.testOutput = testOutput;
	}
	
	

	/**
	 * Get the failuire details of a failed test case.
	 * List will be null if there are no details.
	 * @return
	 */
	public List<String> getFailureDetails() {
		return failureDetails;
	}

	public void setFailureDetails(List<String> failureDetails) {
		this.failureDetails = failureDetails;
	}
	
	public org.psygrid.data.model.dto.SingleVariableTestCaseDTO toDTO(
				Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs,
				RetrieveDepth depth) {
		
	    //check for an already existing instance of a dto object for this 
	    //derived entry in the map of references
	    org.psygrid.data.model.dto.SingleVariableTestCaseDTO dtoSVTC = null;
	    if ( dtoRefs.containsKey(this)){
	        dtoSVTC = (org.psygrid.data.model.dto.SingleVariableTestCaseDTO)dtoRefs.get(this);
	    }
	    if ( null == dtoSVTC ){
	        //an instance of the derived entry has not already
	        //been created, so create it, and add it to the 
	        //map of references
	        dtoSVTC = new org.psygrid.data.model.dto.SingleVariableTestCaseDTO();
	        dtoRefs.put(this, dtoSVTC);
	        toDTO(dtoSVTC, dtoRefs, depth);
	    }
	    
	    return dtoSVTC;
		
	}
	
	public void toDTO(org.psygrid.data.model.dto.SingleVariableTestCaseDTO dtoSVTC, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
		super.toDTO(dtoSVTC, dtoRefs, depth);
		
		dtoSVTC.setTestOutput(testOutput);
		if (testInput != null) {
			dtoSVTC.setTestInput(testInput.toDTO(dtoRefs, depth));
		}
		
		if (failureDetails != null) {
			String[] dtoFailureDetails = new String[failureDetails.size()];
			for (int i=0; i < failureDetails.size(); i++) {
				dtoFailureDetails[i] = failureDetails.get(i);
			}
			dtoSVTC.setFailureDetails(dtoFailureDetails);
		}
		
	}

}

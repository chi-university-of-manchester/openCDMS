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

import java.util.HashMap;
import java.util.Map;

import org.psygrid.data.model.hibernate.Persistent;

/**
 * This class encapsulates a single test case for validating a derived entry.
 * 
 * @author Bill Vance
 * 
 * @DEL_REP_MULTIVARTESTCASE_TABLE_TAG@
 * 
 *
 */
public class MultipleVariableTestCase extends Persistent {
	
	Map<String, Value> testMap = new HashMap<String, Value>();
	Double response; //valid response
	Double responseToLastTest; //response obtained from the last test.
	
	public MultipleVariableTestCase(){
		
	}
	
	public MultipleVariableTestCase(Map<String, Value> inputs, Double response) {
		this.testMap = inputs;
		this.response = response;
	}


	/**
	 * 
	 * @return
	 * @DEL_REP_MULTIVARTESTCASE_RESPONSE_TAG@
	 */
	public Double getResponse() {
		return response;
	}

	public void setResponse(Double response) {
		this.response = response;
	}

    /**
     * Get the map of input variables.
     * 
     * @return The map of input variables.
     * @DEL_REP_MULTIVARTESTCASE_INPUTMAP_TAG@
     */
	public Map<String, Value> getTestMap() {
		return testMap;
	}

	public void setTestMap(Map<String, Value> testMap) {
		this.testMap = testMap;
	}

	/**
	 * Returns the response actually generated from the last test run.
	 * @return
	 */
	public Double getResponseToLastTest() {
		return responseToLastTest;
	}

	/**
	 * Sets the response that was generated from this test case from the last test run.
	 * @param responseToLastTest
	 */
	public void setResponseToLastTest(Double responseToLastTest) {
		this.responseToLastTest = responseToLastTest;
	}
	
	public org.psygrid.data.model.dto.MultipleVariableTestCaseDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
		
        //check for an already existing instance of a dto object for this 
        //derived entry in the map of references
        org.psygrid.data.model.dto.MultipleVariableTestCaseDTO dtoMVTC = null;
        if ( dtoRefs.containsKey(this)){
            dtoMVTC = (org.psygrid.data.model.dto.MultipleVariableTestCaseDTO)dtoRefs.get(this);
        }
        if ( null == dtoMVTC ){
            //an instance of the derived entry has not already
            //been created, so create it, and add it to the 
            //map of references
            dtoMVTC = new org.psygrid.data.model.dto.MultipleVariableTestCaseDTO();
            dtoRefs.put(this, dtoMVTC);
            toDTO(dtoMVTC, dtoRefs, depth);
        }
        
        return dtoMVTC;
		
	}
	
    public void toDTO(org.psygrid.data.model.dto.MultipleVariableTestCaseDTO dtoMVTC, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
		super.toDTO(dtoMVTC, dtoRefs, depth);
		
		//test map!
        org.psygrid.data.model.dto.ValueDTO[] dtoVars = new org.psygrid.data.model.dto.ValueDTO[this.testMap.size()];
        String[] dtoKeys = new String[this.testMap.size()];
        int counter = 0;

        for (String entry: testMap.keySet()) {
            dtoKeys[counter] = entry;
            dtoVars[counter] = ((Value)testMap.get(entry)).toDTO(dtoRefs, depth);
            counter++;
        }
        
        dtoMVTC.setTestKeys(dtoKeys);
        dtoMVTC.setTestVariables(dtoVars);
		
		//response
		dtoMVTC.setResponse(this.response);
		
		//response of failed test...
		dtoMVTC.setResponseToLastTest(this.responseToLastTest);
    }

}

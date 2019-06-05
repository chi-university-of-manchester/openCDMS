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
package org.psygrid.data.model.dto;

import java.util.Map;

import org.psygrid.data.model.dto.PersistentDTO;

public class MultipleVariableTestCaseDTO extends PersistentDTO {

	
	private String[] testKeys = new String[0];
	private ValueDTO[] testVariables = new ValueDTO[0];
	private Double response; //valid response
	private Double responseToLastTest; //response obtained from the last test.

	public MultipleVariableTestCaseDTO(){}

    public org.psygrid.data.model.hibernate.MultipleVariableTestCase toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        //check for an already existing instance of a hibernate object for this 
        //derived entry in the map of references
        org.psygrid.data.model.hibernate.MultipleVariableTestCase hMVTC = null;
        if ( hRefs.containsKey(this)){
            hMVTC = (org.psygrid.data.model.hibernate.MultipleVariableTestCase)hRefs.get(this);
        }
        if ( null == hMVTC ){
            //an instance of the derived entry has not already
            //been created, so create it, and add it to the 
            //map of references
            hMVTC = new org.psygrid.data.model.hibernate.MultipleVariableTestCase();
            hRefs.put(this, hMVTC);
            toHibernate(hMVTC, hRefs);
        }
        
        return hMVTC;
    }
    
    public void toHibernate(org.psygrid.data.model.hibernate.MultipleVariableTestCase hMVTC, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
    	super.toHibernate(hMVTC, hRefs);
    	
    	Map variablesMap = hMVTC.getTestMap();
        for ( int i=0; i<this.testKeys.length; i++){
            String key = this.testKeys[i];
            ValueDTO value = this.testVariables[i];
            if ( null != key && null != value ){
                variablesMap.put(key, value.toHibernate(hRefs));
            }
        }
        
        hMVTC.setResponse(this.response);
        hMVTC.setResponseToLastTest(responseToLastTest);
    }


	public Double getResponse() {
		return response;
	}


	public void setResponse(Double response) {
		this.response = response;
	}


	public Double getResponseToLastTest() {
		return responseToLastTest;
	}


	public void setResponseToLastTest(Double responseToLastTest) {
		this.responseToLastTest = responseToLastTest;
	}


	public String[] getTestKeys() {
		return testKeys;
	}


	public void setTestKeys(String[] testKeys) {
		this.testKeys = testKeys;
	}


	public ValueDTO[] getTestVariables() {
		return testVariables;
	}


	public void setTestVariables(ValueDTO[] testVariables) {
		this.testVariables = testVariables;
	}
		
	
}
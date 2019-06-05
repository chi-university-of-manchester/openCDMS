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


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.psygrid.data.model.dto.MultipleVariableTestCaseDTO;

public class MultipleVariableTestDTO extends ElementDTO {
	
	private MultipleVariableTestCaseDTO[] testCases = new MultipleVariableTestCaseDTO[0];
	private MultipleVariableTestCaseDTO[] failedTests = new MultipleVariableTestCaseDTO[0];
	
	public MultipleVariableTestDTO(){}
	
    public org.psygrid.data.model.hibernate.MultipleVariableTest toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        //check for an already existing instance of a hibernate object for this 
        //derived entry in the map of references
        org.psygrid.data.model.hibernate.MultipleVariableTest hMVT = null;
        if ( hRefs.containsKey(this)){
            hMVT = (org.psygrid.data.model.hibernate.MultipleVariableTest)hRefs.get(this);
        }
        if ( null == hMVT ){
            //an instance of the derived entry has not already
            //been created, so create it, and add it to the 
            //map of references
            hMVT = new org.psygrid.data.model.hibernate.MultipleVariableTest();
            hRefs.put(this, hMVT);
            toHibernate(hMVT, hRefs);
        }
        
        return hMVT;
    }
    
    public void toHibernate(org.psygrid.data.model.hibernate.MultipleVariableTest hMVT, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
    	super.toHibernate(hMVT, hRefs);
    	
    	List<org.psygrid.data.model.hibernate.MultipleVariableTestCase> hibTestCases = new ArrayList<org.psygrid.data.model.hibernate.MultipleVariableTestCase>();
    	
    	int numTestCases = this.testCases.length;
    	for(int i = 0; i < numTestCases; i++){
    		hibTestCases.add(testCases[i].toHibernate(hRefs));
    	}
    	
    	hMVT.setTestCases(hibTestCases);
    	
    	
    	List<org.psygrid.data.model.hibernate.MultipleVariableTestCase> hibFailedTestCases = new ArrayList<org.psygrid.data.model.hibernate.MultipleVariableTestCase>();
    	
    	int numFailedTestCases = this.failedTests.length;
    	for(int i = 0; i < numFailedTestCases; i++){
    		if(failedTests[i] != null){
    	  		hibFailedTestCases.add(failedTests[i].toHibernate(hRefs));
    		}
    	}
    	
    	hMVT.setFailedTests(hibFailedTestCases);
    }

	public MultipleVariableTestCaseDTO[] getFailedTests() {
		return failedTests;
	}

	public void setFailedTests(MultipleVariableTestCaseDTO[] failedTests) {
		this.failedTests = failedTests;
	}

	public MultipleVariableTestCaseDTO[] getTestCases() {
		return testCases;
	}

	public void setTestCases(MultipleVariableTestCaseDTO[] testCases) {
		this.testCases = testCases;
	}


}

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


public class SingleVariableTestDTO extends ElementDTO {
	
	
	private SingleVariableTestCaseDTO[] testCases;
	private SingleVariableTestCaseDTO[] failedTests;


	public SingleVariableTestCaseDTO[] getFailedTests() {
		return failedTests;
	}


	public void setFailedTests(SingleVariableTestCaseDTO[] failedTests) {
		this.failedTests = failedTests;
	}


	public SingleVariableTestCaseDTO[] getTestCases() {
		return testCases;
	}


	public void setTestCases(SingleVariableTestCaseDTO[] testCases) {
		this.testCases = testCases;
	}

    public org.psygrid.data.model.hibernate.SingleVariableTest toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        //check for an already existing instance of a hibernate object for this 
        //derived entry in the map of references
        org.psygrid.data.model.hibernate.SingleVariableTest hSVT = null;
        if ( hRefs.containsKey(this)){
            hSVT = (org.psygrid.data.model.hibernate.SingleVariableTest)hRefs.get(this);
        }
        if ( null == hSVT ){
            //an instance of the derived entry has not already
            //been created, so create it, and add it to the 
            //map of references
            hSVT = new org.psygrid.data.model.hibernate.SingleVariableTest();
            hRefs.put(this, hSVT);
            toHibernate(hSVT, hRefs);
        }
        
        return hSVT;
    }
    
    
    public void toHibernate(org.psygrid.data.model.hibernate.SingleVariableTest hSVT, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
		super.toHibernate(hSVT, hRefs);
		
		List<org.psygrid.data.model.hibernate.SingleVariableTestCase> hibTestCases = new ArrayList<org.psygrid.data.model.hibernate.SingleVariableTestCase>();
		for(SingleVariableTestCaseDTO test : testCases){
			hibTestCases.add(test.toHibernate(hRefs));
		}
		
		hSVT.setTestCases(hibTestCases);
		
		List<org.psygrid.data.model.hibernate.SingleVariableTestCase> failedHibTestCases = new ArrayList<org.psygrid.data.model.hibernate.SingleVariableTestCase>();
		for(SingleVariableTestCaseDTO test : failedTests){
			failedHibTestCases.add(test.toHibernate(hRefs));
		}
		
		hSVT.setFailedTestCases(failedHibTestCases);
    }
}

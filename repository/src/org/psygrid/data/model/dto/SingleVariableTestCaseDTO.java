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

public class SingleVariableTestCaseDTO extends PersistentDTO {
	
	private ValueDTO testInput;
	private boolean testOutput;
	private String[] failureDetails = null;
	
	public SingleVariableTestCaseDTO(){}
	

	public boolean getTestOutput() {
		return testOutput;
	}


	public void setTestOutput(boolean testOutput) {
		this.testOutput = testOutput;
	}


	public ValueDTO getTestInput() {
		return testInput;
	}

	public void setTestInput(ValueDTO testInput) {
		this.testInput = testInput;
	}
	
	
    public String[] getFailureDetails() {
		return failureDetails;
	}


	public void setFailureDetails(String[] failureDetails) {
		this.failureDetails = failureDetails;
	}


	public org.psygrid.data.model.hibernate.SingleVariableTestCase toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        //check for an already existing instance of a hibernate object for this 
        //derived entry in the map of references
        org.psygrid.data.model.hibernate.SingleVariableTestCase hSVTC = null;
        if ( hRefs.containsKey(this)){
            hSVTC = (org.psygrid.data.model.hibernate.SingleVariableTestCase)hRefs.get(this);
        }
        if ( null == hSVTC ){
            //an instance of the derived entry has not already
            //been created, so create it, and add it to the 
            //map of references
            hSVTC = new org.psygrid.data.model.hibernate.SingleVariableTestCase();
            hRefs.put(this, hSVTC);
            toHibernate(hSVTC, hRefs);
        }
        
        return hSVTC;
    }
    
    public void toHibernate(org.psygrid.data.model.hibernate.SingleVariableTestCase hSVTC, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
		super.toHibernate(hSVTC, hRefs);
		hSVTC.setTestInput(testInput.toHibernate(hRefs));
		hSVTC.setTestOutput(testOutput);
		
		if (failureDetails != null) {
			List<String> hFailureDetails = new ArrayList<String>();
			for (String failure: failureDetails) {
				hFailureDetails.add(failure);
			}
			hSVTC.setFailureDetails(hFailureDetails);
		}
    }
	

}

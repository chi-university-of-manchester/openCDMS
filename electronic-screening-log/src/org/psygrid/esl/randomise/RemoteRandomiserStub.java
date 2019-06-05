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

package org.psygrid.esl.randomise;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Calendar;

import org.psygrid.esl.model.ICustomEmailInfo;
import org.psygrid.esl.model.IProject;
import org.psygrid.esl.model.IRandomisation;
import org.psygrid.esl.model.ISite;
import org.psygrid.esl.model.ISubject;
import org.psygrid.esl.model.StrataAllocationFault;
import org.psygrid.esl.model.hibernate.CustomEmailInfo;
import org.psygrid.esl.services.NotAuthorisedFault;
import org.psygrid.randomization.Parameter;


/**
 * Test class for the RemoteRandomiser, which doesn't require a remote 
 * randomisation service.
 * 
 * @author Lucy Bridges
 *
 */
public class RemoteRandomiserStub implements IRemoteRandomiser {
	
	public String allocateTreatment(IRandomisation rand, ISubject subject, CustomEmailInfo customEmailInfo,
			String saml) throws RandomisationException, StrataAllocationFault {
		System.out.println("allocateTreatmentStub");
		return null;
	}
	
	public String getAllocation(String rdmzrName, ISubject subject, String saml)
			throws RandomisationException {
		System.out.println("getAllocationStub");
		return null;
	}

	public String[][] getAllAllocations(IProject project, String saml)
		throws RandomisationException {
		System.out.println("getAllAllocationsStub");
		return null;
	}
	
	public void saveRandomisation(IRandomisation rand, String saml)
			throws RandomisationException {	
		System.out.println("saveRandomisationStub");
	}

	public Calendar[] getSubjectRandomisationEvents(String rdmzrName, String subjectCode, String saml) 
	throws RandomisationException {
		System.out.println("getSubjectRandomisationEventsStub");
		return null;
	}
	
    public String[][] getRandomizerStatistics(IProject project, String saml) throws RandomisationException, NotAuthorisedFault {
        System.out.println("getRandomizerStatistics(IProject, String)");
        return null;
    }

    public String[][] getRandomizerStatistics(IProject project, Parameter[] parameters, String saml) throws RandomisationException, NotAuthorisedFault {
        System.out.println("getRandomizerStatistics(IProject, Parameter[], String)");
        return null;
    }

    public String[] getRandomisationResultForDate(String rdmzrName, String subjectCode, Calendar date, String saml) 
    throws RandomisationException, NotAuthorisedFault {
    	System.out.println("getRandomisationResultForDateStub");
        return null;
    }


}
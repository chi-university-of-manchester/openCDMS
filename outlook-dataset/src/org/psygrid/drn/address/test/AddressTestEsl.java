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


package org.psygrid.drn.address.test;

import org.psygrid.drn.address.AddressEsl;
import org.psygrid.esl.model.IFactory;
import org.psygrid.esl.model.IProject;

/**
 * @author Rob Harper
 *
 */
public class AddressTestEsl extends AddressEsl {

    public static void main(String[] args) {
        try{
        	AddressTestEsl addTestEsl = new AddressTestEsl();
            addTestEsl.createAddressEsl(null);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

	public IProject createProject(IFactory factory) {
		IProject project = factory.createProject("ADT");
		project.setProjectCode("ADT");
		project.setProjectName("Address TEST");
		return project;
	}

	public IProject createGroups(IProject project, IFactory factory) {

		project.setGroup(factory.createGroup("TEST", "001001"));

		return project;
	}

}

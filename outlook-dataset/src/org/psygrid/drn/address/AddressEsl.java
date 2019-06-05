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


package org.psygrid.drn.address;

import org.psygrid.esl.model.IFactory;
import org.psygrid.esl.model.IProject;
import org.psygrid.esl.model.hibernate.HibernateFactory;
import org.psygrid.esl.services.client.EslClient;
import org.psygrid.www.xml.security.core.types.GroupType;

/**
 * @author Rob Harper
 *
 */
public class AddressEsl {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try{
        	AddressEsl addEsl = new AddressEsl();
            addEsl.createAddressEsl(null);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public AddressEsl(){

    }

    public void insert(String saml) throws Exception {
        createAddressEsl(saml);
    }

    public void createAddressEsl(String saml) throws Exception {

        EslClient client = new EslClient();
        IFactory factory = new HibernateFactory();

    	IProject project = createProject(factory);
    	client.saveProject(project, saml);
    	System.out.println(project.getProjectCode()+" project has been setup");
    	project = client.retrieveProjectByCode(project.getProjectCode(), saml);
    	project = createGroups(project, factory);
    	client.saveProject(project, saml);
    	System.out.println("Groups have been setup");

    }

	public IProject createProject(IFactory factory) {
		IProject project = factory.createProject("ADD");
		project.setProjectCode("ADD");
		project.setProjectName("ADDRESS");
		return project;
	}

	public IProject createGroups(IProject project, IFactory factory) {

		GroupType[] groups = ADDGroups.allGroups();
		for ( GroupType g: groups ){
			project.setGroup(factory.createGroup(g.getName(), g.getIdCode()));
		}

		return project;
	}

}

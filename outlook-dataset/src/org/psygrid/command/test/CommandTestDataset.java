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

package org.psygrid.command.test;

import java.net.MalformedURLException;
import java.net.URL;

import org.psygrid.command.CommandDataset;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Factory;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.data.model.hibernate.Site;
import org.psygrid.data.repository.client.RepositoryClient;

/**
 * @author Rob Harper
 *
 */
public class CommandTestDataset extends CommandDataset {

	public static void main(String[] args){
		try{
			RepositoryClient client = null;
			if (1==args.length){
				//use the argument as the location of the repository web-service
				System.out.println(args[0]);
				client = new RepositoryClient(new URL(args[0]));
			}
			else{
				client = new RepositoryClient();
			}

			CommandTestDataset command = new CommandTestDataset();
			DataSet ds = command.createDataset();

			Long id = client.saveDataSet(ds, null);
			client.publishDataSet(id, null);
			System.out.println("DataSet successfully saved to the repository and assigned id="+id);

			ds = client.getDataSet(id, null);
			createReports(ds, null);
			System.out.println("Successfully saved reports");

		}
		catch(MalformedURLException ex){
			System.out.println("URL '"+args[0]+"' specified as the argument is not valid");
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public CommandTestDataset(){

	}

	@Override
	protected void configureGroups(Factory factory, DataSet dataSet) {
		Group grp1 = (Group)factory.createGroup("001001");
		grp1.setLongName("TEST");
		Site site1a = new Site("TEST", "TEST", "TEST", grp1);
		site1a.addConsultant("TEST");
		grp1.addSite(site1a);
		dataSet.addGroup(grp1);
	}

	protected String getName(){
		return "COMMAND TEST";
	}

	@Override
	protected String getDisplayText() {
		return "COMMAND TEST";
	}

	protected String getCode(){
		return "CMT";
	}

}

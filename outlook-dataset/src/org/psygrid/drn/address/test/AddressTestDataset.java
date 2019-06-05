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

import java.net.MalformedURLException;
import java.net.URL;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Factory;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.data.model.hibernate.Site;
import org.psygrid.data.repository.client.RepositoryClient;
import org.psygrid.drn.address.AddressDataset;

/**
 * @author Rob Harper
 *
 */
public class AddressTestDataset extends AddressDataset {

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

			AddressTestDataset addTestDs = new AddressTestDataset();
			DataSet ds = addTestDs.createDataset();
			Long id = client.saveDataSet(ds, null);
			client.publishDataSet(id, null);
			System.out.println("DataSet successfully saved to the repository and assigned id="+id);
		}
		catch(MalformedURLException ex){
			System.out.println("URL '"+args[0]+"' specified as the argument is not valid");
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}

	@Override
	protected void configureGroups(DataSet dataSet, Factory factory) {
		Group grp1 = (Group)factory.createGroup("001001");
		grp1.setLongName("TEST");
		Site site1a = new Site("TEST", "TEST", "TEST", grp1);
		site1a.addConsultant("TEST");
		grp1.addSite(site1a);
		dataSet.addGroup(grp1);
	}

	protected String getName(){
		return "Address TEST";
	}

	@Override
	protected String getDisplayText() {
		return "Address TEST";
	}

	protected String getCode(){
		return "ADT";
	}
}

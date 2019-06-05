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


package org.psygrid.data.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import org.psygrid.data.repository.client.RepositoryClient;

/**
 * @author Rob Harper
 *
 */
public class DeleteRecord {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try{
	        deleteRecord(null, null);
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public static void deleteRecord(String serviceUrl, String saml) throws Exception {
        System.out.println("Please enter the identifier of the record to delete: ");
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String identifier1 = in.readLine();
        System.out.println("Please re-enter the identifier of the record to delete: ");
        String identifier2 = in.readLine();
        if ( !identifier1.equals(identifier2) ){
        	System.out.println("Identifiers do not match. Exiting.");
        	return;
        }
        
        System.out.println("Please confirm that you wish to delete record '"+identifier1+"' (type 'yes' to confirm): ");
        String confirm = in.readLine();
        if ( !"yes".equals(confirm) ){
        	System.out.println("Exiting.");
        	return;
        }
        
        RepositoryClient client = null;
        if ( null == serviceUrl ){
        	client = new RepositoryClient();
        }
        else{
        	client = new RepositoryClient(new URL(serviceUrl));
        }
        client.deleteRecord(identifier1, null);
        System.out.println("Record '"+identifier1+"' has been deleted.");
	}
	
}

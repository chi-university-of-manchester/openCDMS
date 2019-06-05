/*
Copyright (c) 2006-2010, The University of Manchester, UK.

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
package org.psygrid.data.importing;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import org.psygrid.data.repository.RepositoryServiceFault;

/**
 * A service to request imports
 * 
 * @author Terry Child
 * 
 */
public interface ImportService extends Remote {
		
	/**
	 * Get a list of import types supported by a given dataset.
	 * @param projectCode the dataset code
	 * @param saml the saml 
	 * @return the list of user readable import types
	 * @throws RemoteException
	 * @throws RepositoryServiceFault
	 */
	String[] getImportTypes(String projectCode, String saml) throws RemoteException, RepositoryServiceFault;

	/**
	 * Requests a scheduled import for a project.
	 * @param data the import data
	 * @param saml the saml
	 * @throws RemoteException
	 * @throws RepositoryServiceFault
	 */
	void requestImport(ImportData data, String saml) throws RemoteException, RepositoryServiceFault;
	
	/**
	 * Get the status of any scheduled imports for a dataset
	 * @param projectCode the dataset code
	 * @param saml the saml
	 * @return the list of status information for all the imports associated with a project.
	 * @throws RemoteException
	 * @throws RepositoryServiceFault
	 */
	ImportStatus[] getImportStatuses(String projectCode, String saml) throws RemoteException, RepositoryServiceFault;
			
}

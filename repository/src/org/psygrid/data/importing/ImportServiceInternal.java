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

import java.rmi.RemoteException;
import java.util.List;

import org.psygrid.data.importing.model.ImportRequest;
import org.psygrid.data.repository.RepositoryServiceFault;

/**
 * Internal interface of the ImportService.
 * 
 * This interface extends the web service interface and adds transactional methods
 * that are used internally by the server but which are not exposed as web service methods.
 * 
 * The need for this interface will disappear when we update our web service library and can choose
 * which individual methods to expose as web services using annotations.
 * When this happens we can merge this interface into its parent.
 * 
 * @author Terry Child
 *
 */
public interface ImportServiceInternal extends ImportService {
		
	/**
	 * Returns a the error log for a given import.
	 * @param id the hibernate id
	 * @return a the import log file as a string
	 */
	String getImportLog(long id);

	/**
	 * Gets the next 'Pending' import request and changes its status to 'Processing'.
	 * 
	 * If a request is already 'Processing' this method returns null.
	 * 
	 * This method is not named 'getNextImportRequest' because getters are read only.
	 * 
	 */	
	public ImportRequest nextImportRequest();
	
	/**
	 * This method is non-transactional so that partial imports may run.
	 * @param request
	 */
	public void runImport(ImportRequest request);
	
	/**
	 * Updates the database with the import request and sends any emails.
	 * 
	 * @param request
	 */
	public void updateImportRequest(ImportRequest request);

}

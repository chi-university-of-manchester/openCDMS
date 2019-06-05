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

package org.psygrid.data.export;

import java.io.IOException;
import java.net.ConnectException;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;

import org.psygrid.data.export.dto.ExportRequest;
import org.psygrid.data.export.hibernate.ExternalQuery;
import org.psygrid.data.repository.RepositoryNoSuchDatasetFault;
import org.psygrid.data.repository.RepositoryServiceFault;
import org.psygrid.data.repository.dao.DAOException;
import org.psygrid.data.utils.security.NotAuthorisedFault;

/**
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
public interface ExportServiceInternal extends ExportService {


	/**
	 * Retreve the details of the next pending export request
	 * that needs to be processed. Depending upon the value of
	 * the immediate argument the next request will be taken from those
	 * marked as being for immediate execution, of from those for normal
	 * execution.
	 * <p>
	 * If <code>null</code> then either there is no outstanding export
	 * request to process, or an export is currently being processed
	 * by another thread.
	 * 
	 * @param immediate If the next pending request should be taken from those
	 * marked as for immediate execution, or not.
	 * 
	 * @return The next pending export request.
	 */
	public org.psygrid.data.export.dto.ExportRequest getNextPendingRequest(boolean immediate) throws RemoteException, ConnectException;
	
	/**
	 * Update the status of an export request, with the request to update
	 * being identified by its unique identifier.
	 * 
	 * @param requestId The id of the request to update.
	 * @param newStatus The new status to apply to the request.
	 * @throws DAOException if no export request exists for the given id.
	 */
	public void updateRequestStatus(Long requestId, String newStatus) throws RemoteException, ConnectException;

	/**
	 * Generate the MD5 and SHA1 hash for the given zipped export file. The outputs 
	 * are stored as text files, specified by the outputPath (typically the same 
	 * location as the zip file).
	 * 
	 * @param filePath
	 * @param outputPath
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	public void generateHashes(String filePath, String md5Path, String shaPath) throws IOException, NoSuchAlgorithmException;

	/**
	 * Update an export request to set it as complete. The status of the
	 * request will be set to complete, the completed date set to the current
	 * date and the path of the exported data zip file set to that provided in
	 * the argument.
	 * 
	 * @param requestId The id of the request to set as complete.
	 * @param path The path of the exported data zip file on the local file
	 * system.
	 * @param md5Path The path of the file containing the MD5 hash of the
	 * exported zip file.
	 * @param shaPath The path of the file containing the SHA1 hash of the 
	 * exported zip file.
	 * @throws DAOException if no export request exists for the given id.
	 */
	public void updateRequestSetComplete(Long requestId, String path, final String md5Path, final String shaPath) throws DAOException;

	/**
	 * Get the external queries for a given dataset.
	 */
	public ExternalQuery[] getExternalQueries(String projectCode);
        
}
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
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.psygrid.data.export.hibernate.ExportRequest;
import org.psygrid.data.export.hibernate.ExternalQuery;
import org.psygrid.data.repository.dao.DAOException;

/**
 * Interface to define the DAO operations relevant to
 * exporting data from the PsyGrid data repository.
 * 
 * @author Rob Harper
 *
 */
public interface ExportDAO {

	/**
	 * Request an export. Creates a new export request ready for
	 * servicing.
	 * 
	 * @param exportRequest The ExportRequest
	 * @param applyExportSecurity Whether security is to be applied on this export 
	 * processed immediately. Otherwise it will be queued for scheduled execution.
	 */
	public void requestExport(ExportRequest request, boolean applyExportSecurity);
	
	/**
	 * Get the list of export requests for the given projects belonging to 
	 * the given user.
	 * 
	 * @param projects List of projects to get export requests for.
	 * @param user The DN of the user.
	 * @return The list of requests.
	 */
	public org.psygrid.data.export.dto.ExportRequest[] getRequestsForUser(List<String> projects, String user);
	
	/**
	 * Download the data for a completed export.
	 * 
	 * @param user The DN of the user requesting the download.
	 * @param id The id of the request.
	 * @return Byte array of zip data.
	 * @throws NoSuchExportException if no completed export exists
	 * for the supplied user and id.
	 */
	public byte[] getCompletedExport(String user, Long id) throws NoSuchExportException;
	
	/**
	 * Download the hash of a completed export. Used for checking an 
	 * export has downloaded correctly.
	 * 
	 * @param user The DN of the user requesting the download.
	 * @param format The hash type, either SHA1 or MD5.
	 * @param id The id of the request.
	 * @return Byte array of zip data.
	 * @throws NoSuchExportException if no completed export exists
	 * for the supplied user and id.
	 */
	public byte[] getCompletedExportHash(String user, String format, Long id) throws NoSuchExportException;
	
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
	public org.psygrid.data.export.dto.ExportRequest getNextPendingRequest(boolean immediate);
	
	/**
	 * Update the details of an export request.
	 * 
	 * @param req The export request.
	 * @return The updated export request.
	 */
	public org.psygrid.data.export.dto.ExportRequest updateExportRequest(org.psygrid.data.export.dto.ExportRequest req);
	
	/**
	 * Update the status of an export request, with the request to update
	 * being identified by its unique identifier.
	 * 
	 * @param requestId The id of the request to update.
	 * @param newStatus The new status to apply to the request.
	 * @throws DAOException if no export request exists for the given id.
	 */
	public void updateRequestStatus(Long requestId, String newStatus) throws DAOException;
	
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
	 * Retrieve the project code of an export request.
	 * 
	 * @param requestId The unique identifier of the export request.
	 * @return The project code.
	 * @throws DAOException if no export request exists for the given id.
	 */
	public String getProjectForExportRequest(Long requestId) throws DAOException;
	
	/**
	 * Delete an export request.
	 * 
	 * @param requestId The id of the export request to delete
	 * @param user The DN of the user trying to delete the export request.
	 * @throws UnableToCancelExportException if the export request cannot be cancelled
	 */
	public void deleteExportRequest(String user, Long requestId) throws UnableToCancelExportException;
	
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
	 * Get the external queries for a given dataset.
	 */
	public ExternalQuery[] getExternalQueries(String projectCode);

}

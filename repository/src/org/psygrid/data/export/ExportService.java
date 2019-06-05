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

import java.rmi.RemoteException;

import org.psygrid.data.export.dto.ExportRequest;
import org.psygrid.data.repository.RepositoryNoSuchDatasetFault;
import org.psygrid.data.repository.RepositoryServiceFault;
import org.psygrid.data.utils.security.NotAuthorisedFault;

/**
 * Export service specification.
 *
 * @author Terry Child
 *
 */
public interface ExportService extends java.rmi.Remote {

	
    /**
     * Request an export of data from the PsyGrid data repository.
     * <p>
     * The request will then be serviced at a later date.
     *
     * @param projectCode The project code of the project to export
     * data from.
     * @param groups The list of groups within the project to export data
     * for.
     * @param docOccs The list of document occurrences within the project
     * to export data for.
     * @param format The format the data should be exported into.
     * @param saml SAML assertion for the security system.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
    public void requestExport(ExportRequest exportRequest, String saml)
		throws RemoteException, RepositoryServiceFault, NotAuthorisedFault, RepositoryNoSuchDatasetFault;

    /**
     * Request an export of data from the PsyGrid data repository for immediate execution.
     *
     * @param projectCode The project code of the project to export
     * data from.
     * @param groups The list of groups within the project to export data
     * for.
     * @param docOccs The list of document occurrences within the project
     * to export data for.
     * @param format The format the data should be exported into.
     * @param saml SAML assertion for the security system.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
    public void requestImmediateExport(ExportRequest exportRequest, String saml)
		throws RemoteException, RepositoryServiceFault, NotAuthorisedFault, RepositoryNoSuchDatasetFault;
	
    /**
     * Get the details of all export requests for the given projects submitted
     * by the calling user.
     *
     * @param projects The list of projects.
     * @param saml SAML assertion for the security system.
     * @return Array of export requests.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
    public org.psygrid.data.export.dto.ExportRequest[] getMyExportRequests(String projects[], String saml)
    	throws RemoteException, RepositoryServiceFault, NotAuthorisedFault;

    /**
     * Download the data for a completed export request.
     *
     * @param exportRequestId The unique id of the export request.
     * @param saml SAML assertion for the security system.
     * @return The exported data.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     * @throws NoSuchExportFault
     */
    public byte[] downloadExport(long exportRequestId, String saml)
    	throws RemoteException, RepositoryServiceFault, NotAuthorisedFault, NoSuchExportFault;

    /**
     * Download the hash file for the export file in the given format.
     *
     * @param exportRequestId
     * @param format
     * @param saml
     * @return hashfile
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     * @throws NoSuchExportFault
     */
    public byte[] downloadExportHash(long exportRequestId, String format, String saml)
        throws RemoteException, RepositoryServiceFault, NotAuthorisedFault, NoSuchExportFault;

    /**
     * Cancel an export request.
     *
     * @param exportRequestId The unique id of the export request.
     * @param saml SAML assertion for the security system.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     * @throws UnableToCancelExportFault
     */
    public void cancelExport(long exportRequestId, String saml)
    	throws RemoteException, RepositoryServiceFault, NotAuthorisedFault, UnableToCancelExportFault;
    
        
}
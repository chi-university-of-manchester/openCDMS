/*
Copyright (c) 2006-2009, The University of Manchester, UK.

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

package org.psygrid.data.query;

import java.rmi.RemoteException;

import org.psygrid.data.query.dto.Query;
import org.psygrid.data.repository.RepositoryServiceFault;

/**
 * @author Rob Harper
 *
 */
public interface QueryService extends java.rmi.Remote {

	/**
	 * Save or update a query.
	 * 
	 * @param query The query
	 */
	public void saveQuery(Query query, String saml) throws RemoteException, RepositoryServiceFault;
	
	public Query[] getMyQueries(String project, String saml) throws RemoteException, RepositoryServiceFault;

	public Query getQuery(long queryId, String saml) throws RemoteException, RepositoryServiceFault;

    /**
     * Execute a saved query to return a list of the identifiers of matching
     * participants.
     *
     * @param queryId The id of the query.
     * @param saml SAML assertion for the security system.
     * @return List of matching participant identifiers.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
    public String[] executeQueryForIdentifiers(long queryId, String saml)
    	throws RemoteException, RepositoryServiceFault;

    /**
     * Execute a saved query to return a list of the external identifiers of matching
     * participants.
     *
     * @param queryId The id of the query.
     * @param saml SAML assertion for the security system.
     * @return List of matching participant external identifiers.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
    public String[] executeQueryForExternalIdentifiers(long queryId, String saml)
    	throws RemoteException, RepositoryServiceFault;

    
    /**
     * Execute a saved query to return the number of matching participants.
     *
     * @param queryId The id of the query.
     * @param saml SAML assertion for the security system.
     * @return Number of matching participants.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
    public long executeQueryForCount(long queryId, String saml)
    	throws RemoteException, RepositoryServiceFault;

}

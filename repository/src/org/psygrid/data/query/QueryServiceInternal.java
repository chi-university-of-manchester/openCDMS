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
import org.psygrid.data.repository.dao.DAOException;

/**
 * This interface extends the web service interface and adds transactional methods
 * that are used internally by the server but which are not exposed as web service methods.
 * 
 * The need for this interface will disappear when we update our web service library and can choose
 * which individual methods to expose as web services using annotations.
 * When this happens we can merge this interface into its parent.
 * 
 * @author Terry Child
 */
public interface QueryServiceInternal extends QueryService {


    /**
     * Execute a saved query to return a list of participant identifiers
     * for the matching records.
     * 
     * @param queryId The id of the query.
     * @return List of matching identifiers.
     * @throws DAOException if no query is found for the given id.
     */
    public String[] executeQueryForIdentifiers(final Long queryId) throws DAOException;

}

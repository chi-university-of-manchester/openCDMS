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

import org.psygrid.data.repository.dao.DAOException;

/**
 * @author Rob Harper
 *
 */
public interface QueryDAO {

	public Long saveQuery(org.psygrid.data.query.dto.Query query, String userName);
	
	public org.psygrid.data.query.dto.Query[] getMyQueries(String project, String userName);

	public org.psygrid.data.query.dto.Query getQuery(final Long queryId) throws DAOException;
	
	public String getProjectCodeForQuery(Long queryId) throws DAOException;
	
	public String[] getGroupsForQuery(Long queryId) throws DAOException;

    /**
     * Execute a saved query to return a list of participant identifiers
     * for the matching records.
     * 
     * @param queryId The id of the query.
     * @return List of matching identifiers.
     * @throws DAOException if no query is found for the given id.
     */
    public String[] executeQueryForIdentifiers(final Long queryId) throws DAOException;

    /**
     * Execute a saved query to return a list of participant external identifiers
     * for the matching records.
     * 
     * @param queryId The id of the query.
     * @return List of matching external identifiers in order.
     * @throws DAOException if no query is found for the given id.
     */
    public String[] executeQueryForExternalIdentifiers(final Long queryId) throws DAOException;

    /**
     * Execute a saved query to return the number of matching participants.
     * 
     * @param queryId The id of the query
     * @return The number of matching participants
     * @throws DAOException if no query is found for the given id.
     */
	public Long executeQueryForCount(final Long queryId) throws DAOException;

}

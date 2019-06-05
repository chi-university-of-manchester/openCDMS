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

package org.psygrid.data.repository.dao;

public interface JdbcDAO {

    /**
     * Reserve space for a given number of identifiers, for the given
     * project and dataset.
     * 
     * @param dataSetId The database id of the dataset.
     * @param group The name of the group.
     * @param nIdentifiers The number of identifiers to reserve.
     * @return The new maximum suffix size for the group after the
     * identifier space has been reserved.
     * @throws DAOException
     */
    public Integer reserveIdentifierSpace(Long dataSetId, String group, int nIdentifiers)
        throws DAOException;


    /**
     * Reserve space for a single identifier (if necessary), for the
     * given project and dataset.
     * <p>
     * If the suffix for the identifier is greater than the current
     * maximum suffix size then the maximum suffix size is updated
     * to be equal to the suffix. Otherwise, nothing is done.
     * 
     * @param dataSetId The database id of the dataset.
     * @param group The name of the group.
     * @param suffix The suffix of the identifier for which space is 
     * being reserved.
     * @throws DAOException
     */
    public void reserveIdentifier (Long dataSetId, String group, int suffix) 
        throws DAOException;
}

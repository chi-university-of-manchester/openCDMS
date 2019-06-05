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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.psygrid.data.model.dto.IdentifierDTO;
import org.psygrid.data.model.hibernate.ChangeHistory;
import org.psygrid.data.model.hibernate.Record;

/**
 * Interface to represent "special" actions to be performed on the
 * repository, generally one-off data manipulations to fix problems.
 * 
 * @author Rob Harper
 *
 */
public interface SpecialDAO {

    /**
     * A bug in the integration between CoCoA and the repository
     * caused the unit of a value not to be set unless the user
     * explicitly changed it. So if the default unit was used the
     * value will have null unit.
     * <p>
     * This DAO method fixes this problem, by setting the unit of
     * all values with null unit where the values response references
     * an entry with one or more units. The unit set is the zeroth
     * in the entry's list of units.
     * 
     * @throws DAOException
     */
    public void fixNullUnitsInOutlookData() throws DAOException;
    
    /**
     * Due to performance issues with the query used in
     * RepositoryDAOHibernate#getRecordsByGroupsAndDocStatus a set
     * of count properties were added to the Record object to record
     * the number of incomplete, pending, rejected and approved
     * document instances in the record.
     * <p>
     * This DAO method sets these counts for existing records.
     * 
     * @throws DAOException
     */
    public void setRecordDocumentNumbers() throws DAOException;
    
    /**
     * During the National Eden import the Record object itself
     * was initially saved with no document instances, and
     * was saved by the user who was used to get the SAML to
     * do the import (probably jda).
     * <p>
     * This DAO method corrects this, and sets the created by user
     * of imported National Eden records to the same user as created
     * the Personal Details form.
     * 
     * @throws DAOException
     */
    public void fixNEdenRecordCreatedBy() throws DAOException;
    
    /**
     * Find the number of records in the repository for a given group
     * of a given project.
     * 
     * @param project The project code.
     * @param group The group code.
     * @return The number of records.
     * @throws DAOException
     */
    public int getNumberOfRecordsInGroup(String project, String group) throws DAOException;
    
    /**
     * Update the identifier of all Records in a given project and group to
     * a new one from the provided list of identifiers.
     * 
     * @param project The project code
     * @param group The group code
     * @param ids The list of new identifiers
     * @return Map of old identifiers to new identifiers
     * @throws DAOException
     */
    public Map<String, String> updateIdentifiersforProjectAndGroup(final String project, final String group, final IdentifierDTO[] ids) throws DAOException;

    /**
     * Update the identifier from the old to the new
     * 
     * @param oldIdentifier The old identifier
     * @param newIdentifer The new identifier
     * @return Map of old identifier to new identifier
     * @throws DAOException
     */
    public Map<String, String> updateIdentifier(final String oldIdentifier, final IdentifierDTO newIdentifier) throws DAOException;
    
    /**
     * Fix ED2 records for Bug 823.
     * <p>
     * For all entries that are by default disabled and have
     * a default value add responses with the default value.
     *
     * @param username
     * @throws DAOException
     */
    public void fixED2RecordsForBug823(String username) throws DAOException;
    
    /**
     * Create ChangeHistory items for each record to represent the
     * record being created - using the now deprecated created and
     * createdBy properties.
     * <p>
     * These are returned in a map with the record id as the key.
     * 
     * @return Map of record ids to ChangeHistory items.
     */
	public Map<Long, ChangeHistory> createRecordCreatedChangeHistoryItems();
	
	/**
	 * Updated a Record to add a ChangeHistory item to it.
	 * 
	 * @param recordId The id of the record.
	 * @param history The ChangeHistory item.
	 */
	public void addRecordCreatedChangeHistory(Long recordId, ChangeHistory history);

	/**
	 * Add the complete status to all documents in all datasets.
	 */
	public void addCompleteStatusToAllDatasets();
	
	/**
	 * Change the Address project from the "review and approve" workflow
	 * to the "controlled" workflow.
	 */
	public void changeAddressToControlledWorkflow();
	
}

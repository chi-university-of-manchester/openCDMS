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

package org.psygrid.data.repository;

import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.xml.stream.XMLStreamException;

import org.psygrid.data.export.hibernate.ExportRequest;
import org.psygrid.data.export.hibernate.ExportSecurityActionMap;
import org.psygrid.data.model.dto.GroupDTO;
import org.psygrid.data.model.dto.extra.GroupSummary;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.data.repository.dao.DAOException;
import org.psygrid.data.repository.dao.NoDatasetException;
import org.psygrid.data.repository.transformer.TransformerException;
import org.springframework.mail.SimpleMailMessage;

/**
 * Internal interface of the Repository.
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
public interface RepositoryServiceInternal extends Repository {
        
    /**
     * Retrieve a list of monthly summary emails that need to be sent
     * to users of the system on the given day, for all datasets.
     * <p>
     * Each monthly summary email will contain details of the documents
     * that need to be completed in the next calendar month for a record.
     * 
     * @param now The date for which the monthly summary is being created.
     * @return The list of monthly summary emails.
     * @throws DAOException if an error occurs whilst trying to construct 
     * monthly summary emails.
     */
    public List<SimpleMailMessage> getAllMonthlySummaries(Date now) throws DAOException;
    
    /**
     * Retrieve a list of scheduling reminders that need to be sent
     * to users of the system on the given day, for all datasets.
     * 
     * @param now The date for which to check for scheduled reminders.
     * @return The list of scheduling reminders.
     * @throws DAOException if an error occurs whilst trying to construct 
     * email reminders.
     */
    public List<SimpleMailMessage> getAllScheduledReminders(Date now) throws DAOException;
    
    /**
     * Export data from the database into an intermediate XML format. The
     * XML is written directly to an output stream.
     * <p>
     * The data exported is defined by the study code, the list of group codes 
     * and the list of document occurrences.
     * 
     * @param request The ExportRequest object
     * @param group The group that the ExportRequest is to be applied to
     * @param actionMap The mapping between security tag values to the export action that must be taken
     * @param context This is used to facilitate transforms. 
     * @param applyExportSecurity Specifies whether export security will be applied. 
     * @param requestor The user who requested the export.
     * @param out The output stream to write the exported data to.
     * @param meta Metadata about the exported data.
     * @throws DAOException
     * @throws TransformerException 
     * @throws NoDatasetException 
     * @throws RemoteException 
     * @throws XMLStreamException
     */
    public void exportToXml(ExportRequest request, String group, List<ExportSecurityActionMap> actionMap, OutputStream out, org.psygrid.data.export.metadata.DataSetMetaData meta) 
    		throws DAOException, RemoteException, NoDatasetException, TransformerException, XMLStreamException;

    public void exportToXml(ExportRequest request, List<String> identifiers,
			List<ExportSecurityActionMap> actionMap, 
			OutputStream out, org.psygrid.data.export.metadata.DataSetMetaData meta) 
	throws DAOException, RemoteException, NoDatasetException, TransformerException, XMLStreamException;


	/**
	 * Retrieve the group summaries for a list of projects.
	 * 
     * @param projectCodes the codes of the projects containing the groups
     * @return the list of groups
     */
	public List<GroupSummary> getGroupSummary(List<String> projectCodes);

	/**
	 * Returns true if a project uses randomisation.
	 * 
	 * @param projectCode a existing project code.
	 * @return true if randomised
	 */
	public boolean isProjectRandomized(String projectCode);

	/**
	 * Retrieve a single group with its sites and their consultants.
	 * 
	 * @param groupID the group id
	 * @return the group
	 */
	public Group getGroup(Long groupID);

	/**
	 * Add a new group to a dataset.
	 * 
	 * @param projectCode the dataset projectCode
	 * @param group the group to save
	 * @throws RepositoryServiceFault - because the current ServiceInterceptor wraps DataAccessExceptions in RepositoryServiceFaults.
	 * These non-webservice methods should probably be in their own service.
	 */
	public void addGroup(String projectCode, Group group) throws RepositoryServiceFault;

	/**
	 * Update an existing group.
	 * 
	 * @param group the group to save
	 * @throws RepositoryServiceFault - because the current ServiceInterceptor wraps DataAccessExceptions in RepositoryServiceFaults.
	 */
	public void updateGroup(Group group) throws RepositoryServiceFault;

	/**
	 * Delete a group.
	 * 
	 * @param groupID an existing group id
	 * @throws RepositoryServiceFault - because the current ServiceInterceptor wraps DataAccessExceptions in RepositoryServiceFaults.
	 */
	public void deleteGroup(Long groupID) throws RepositoryServiceFault;
	
}










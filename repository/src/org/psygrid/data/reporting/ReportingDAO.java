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

package org.psygrid.data.reporting;

import java.net.ConnectException;
import java.util.Date;
import java.util.List;

import org.opensaml.SAMLAssertion;
import org.psygrid.data.model.dto.extra.MinimalEntry;
import org.psygrid.data.reporting.definition.IReport;
import org.psygrid.data.reporting.definition.ReportException;
import org.psygrid.data.repository.dao.DAOException;
import org.psygrid.data.utils.security.NotAuthorisedFault;

/**
 * Interface to represent access to generation of reports on the
 * data held in the repository.
 * 
 * @author Rob Harper
 *
 */
public interface ReportingDAO {

    /**
     * Generate a report giving details of the number of records
     * in the repository for each defined status, for a given dataset.
     * 
     * @param dataSetId The unique identifier of the dataset.
     * @return The generated report.
     * @throws DAOException if no dataset exists for the given id, or
     * if the dataset has no defined statuses.
     */
    public org.psygrid.data.reporting.old.Report reportByStatus(Long dataSetId) throws DAOException;
    
    /**
     * Save a single report definition to the database.
     * 
     * @param report The report definition to save.
     * @return The unique identifier in the database of the saved report.
     */
    public Long saveReport(org.psygrid.data.reporting.definition.dto.Report report);
    
    /**
     * Generate a report from the given report definition for the
     * given record.
     * 
     * @param reportId The unique identifier of the report definition.
     * @param recordId The unique identifier of the record.
     * @param user The name of the user generating the report.
     * @param saml SAML assertion
     * @return The report.
     * @throws DAOException if the report cannot be generated.
     */
    public org.psygrid.data.reporting.RecordReport generateReport(Long reportId, Long recordId, String user, String saml) throws DAOException;
    public org.psygrid.data.reporting.Report generateReport(IReport newreport, List<String> groups, String user, String saml) 
	throws DAOException, GroupsNotAllowedException;
    
    /**
     * Retrieve a summary list of all record report definitions available for 
     * the given dataset.
     * 
     * @param dataSetId The unique identifier of the dataset.
     * @return Array of report summaries.
     */
    public org.psygrid.data.reporting.definition.dto.Report[] getReportsByDataSet(Long dataSetId);
    
    /**
     * Retrieve a summary list of all report definitions available for 
     * the given dataset.
     * 
     * @param dataSetId The unique identifier of the dataset.
     * @return Array of report summaries.
     */
    public org.psygrid.data.reporting.definition.dto.Report[] getAllReportsByDataSet(Long dataSetId);
    
    /**
     * Generate a report that contains the number of records having
     * each status for a given project.
     * 
     * @param projectCode The code of the project to generate the 
     * report for.
     * @return The report
     * @throws DAOException if no project exists for the given code.
     */
    public org.psygrid.data.reporting.Report numbersByStatus(String projectCode) throws DAOException;

    /**
     * Generate a report that contains the number of records having
     * each status for a given project and list of groups within
     * the project.
     * 
     * @param projectCode The code of the project to generate the 
     * report for.
     * @param groups The list of groups.
     * @return The report.
     * @throws DAOException if no project exists for the given code.
     */
    public org.psygrid.data.reporting.Report numbersByStatusForGroups(String projectCode, String[] groups) throws DAOException;
    
    /**
     * Generate management reports for all datasets in the repository.
     * 
     * @param user used to retrieve the saml certificate, required for some reports 
     * @param date The date the report is being generated.
     * @return The management reports.
     * @throws ConnectException
     */
    public List<org.psygrid.data.reporting.ManagementReport> generateAllMgmtReports(String user, Date date) 
    throws ConnectException;
    
    /**
     * Generate management reports for a single dataset in the repository,
     * specified by it's project code.
     * 
     * @param user used to retrieve the saml certificate, required for some reports 
     * @param date The date the report is being generated.
     * @param project The project code.
     * @return The management reports.
     * @throws ConnectException
     */
	public List<org.psygrid.data.reporting.ManagementReport> generateMgmtReportsForProject(String user, Date date, String project) 
	throws ConnectException;
	
	/**
     * Generate management reports for a given dataset.
     * 
     * @param dataSetId The unique identifier of the dataset.
     * @param date The date the report is being generated.
     * @saml saml certificate required to check access to reports and retrieve randomisation data for UKCRN reports
     * @return The management reports.
     * @throws ConnectException
     */
    public List<org.psygrid.data.reporting.ManagementReport> generateMgmtReportsForDataSet(Long dataSetId, Date date, String user, SAMLAssertion saml) 
    throws ConnectException;

    /**
     * Retrieve a summary of all reports, of the type specified, defined 
     * for a given dataset.
     * 
     * @param dataSetCode
     * @param type
     * @return reports
     * @throws NoSuchReportException
     */
    public org.psygrid.data.reporting.definition.dto.Report[] getReportsOfType(final String dataSetCode, final String type)
	throws NoSuchReportException;

    
    /**
     * Delete all reports for a given dataset.
     * 
     * @param dataSetId The unique identifier of the dataset.
     * @param projectCode The project code for the dataset.
     * @throws DAOException if no dataset exists for the given id; if 
     *  dataset identifier and project code do not match.
     */
    public void removeReports(Long dataSetId, String projectCode) throws DAOException;
    
    /**
     * Delete all reports for a given dataset.
     * 
     * @param dataSetId The unique identifier of the dataset.
     * @param projectCode The project code for the dataset.
     * @throws DAOException if no dataset exists for the given id; if 
     *  dataset identifier and project code do not match.
     */
    public void deleteReport(Long dataSetId, Long reportId) throws DAOException;
    
    /**
     * Generate a trends report for records in the given dataset.
     * 
     * @param reportId
     * @param datasetId
     * @param groups the list of groups the user is restricted to (if empty all groups are assumed accessible)
     * @return report
     * @throws DAOException
     * @throws GroupsNotAllowedException groups listed in reports are not allowed for the user
     * @throws NotAuthorisedFault
     */
    public org.psygrid.data.reporting.Report generateTrendReport(final Long reportId, final Long datasetId, final List<String> groups) 
    throws DAOException, GroupsNotAllowedException, NotAuthorisedFault;
    
    /**
     * Generate the supplied report dynamically in the given format.
     * 
     * @param report
     * @param formatType currently 'pdf' or 'xls'
     * @param groups the list of groups the user is restricted to (if empty all groups are assumed accessible)
     * @param saml the saml assertion required by UKCRN reports
     * @param user
     * @return output byte array
     * @throws DAOException
     * @throws ReportException
     * @throws GroupsNotAllowedException groups listed in reports are not allowed for the user
     * @throws NotAuthorisedFault
     */
    public byte[] generateDynamicReport(final org.psygrid.data.reporting.definition.dto.Report report, final String formatType, final List<String> groups, final String saml, final String user) 
    throws DAOException, ReportRenderingException, GroupsNotAllowedException, NotAuthorisedFault;

    /**
     * Generate the supplied record report dynamically, for the specified record, in the given format.
     * 
     * @param newreport
     * @param identifier
     * @param formatType
     * @param groups
     * @param saml
     * @param user
     * @return output byte array
     * @throws DAOException
     * @throws ReportRenderingException
     * @throws GroupsNotAllowedException groups listed in reports are not allowed for the user
     * @throws NotAuthorisedFault
     */
	public byte[] generateDynamicRecordReport(final org.psygrid.data.reporting.definition.dto.RecordReport newreport, final String identifier, final String formatType, final List<String> groups, final String saml, final String user) 
	throws DAOException, ReportRenderingException, GroupsNotAllowedException, NotAuthorisedFault;
    
    /**
     * Retrieve the report for the given report id.
     * 
     * @param reportId
     * @return report
     */
    public org.psygrid.data.reporting.definition.dto.Report getReport(final Long reportId);
    public org.psygrid.data.reporting.definition.dto.RecordReport getRecordReport(final Long reportId);
    
    /**
     * Get a list of groups for the given group codes.
     * 
     * Used when specifying the groups for a report that is to be generated
     * dynamically.
     * 
     * @param groupCodes
     * @return groups
     * @throws DAOException
     */
	public List<org.psygrid.data.model.dto.GroupDTO> getGroupsForCodes(long dsId, List<String> groupCodes) throws DAOException;

	/**
	 * Get a list of entries that may be selected from to generate a basic
	 * statistics chart.
	 * 
	 * @param documentId The unique id of the document selected by the user.
	 * @return List of entries, in a minimal format.
	 * @throws DAOException if no document exists for the given id.
	 */
	public MinimalEntry[] getEntriesForBasicStatsChart(final long documentId) throws DAOException;
	
	/**
	 * Get the list of recipients for each management report of the given
	 * project.
	 * <p>
	 * Note that the ManagementReport objects returned in the List do not
	 * contain any data - they just have the report title and the recipients.
	 * 
     * @param user used to retrieve the saml certificate, required for some reports 
     * @param date The date the report is being generated.
     * @param project The project code.
     * @return The management reports.
	 * @throws ConnectException
	 */
	public List<org.psygrid.data.reporting.ManagementReport> getReportRecipientsForProject(String user, Date date, String project) 
	throws ConnectException;

	/**
	 * Get the list of recipients for a single management report, specified
	 * by it's database id.
	 * <p>
	 * Note that the ManagementReport object returned does not
	 * contain any data - just the report title and the recipients.
	 * 
	 * @param user
	 * @param date
	 * @param project
	 * @param reportId
	 * @return
	 * @throws ConnectException
	 */
	public org.psygrid.data.reporting.ManagementReport getRecipientsForReport(String user, Date date, long reportId) 
	throws ConnectException;

	/**
	 * Generate a single management report by ID.
	 * 
	 * @param id ID of the report.
	 * @param user DN of user
	 * @return The report
	 */
	public org.psygrid.data.reporting.ManagementReport generateMgmtReportById(final long id, final String user);

}

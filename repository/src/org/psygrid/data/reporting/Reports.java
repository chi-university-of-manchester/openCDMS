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
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.psygrid.data.model.dto.extra.MinimalEntry;
import org.psygrid.data.reporting.definition.ReportException;
import org.psygrid.data.reporting.definition.dto.TrendsReport;
import org.psygrid.data.repository.RepositoryServiceFault;

/**
 * Web-service interface to reporting functionality of the data 
 * repository.
 * 
 * @author Rob Harper
 *
 */
public interface Reports extends java.rmi.Remote {

    /**
     * Retrieve a summary of all record reports defined for a given
     * dataset.
     * 
     * @param dataSetId The unique identifier of the DataSet
     * @param saml SAML assertion for the security system.
     * @return Array of report summaries.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     * @throws ConnectException
     */
    public org.psygrid.data.reporting.definition.dto.Report[] getReportsByDataSet(long dataSetId, String saml)
        throws RemoteException, RepositoryServiceFault, ConnectException;
    
    /**
     * Retrieve a summary of all reports of the type specified 
     * defined for a given dataset.
     * 
     * @param dataSetCode The unique code identifying a DataSet
     * @param type The type of report to retrieve. i.e "record", "trends" or "management"
     * @param saml SAML assertion for the security system.
     * @return Array of report summaries.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     * @throws ConnectException
     */
    public org.psygrid.data.reporting.definition.dto.Report[] getReportsOfType(String dataSetCode, String type, String saml)
        throws RemoteException, RepositoryServiceFault, ConnectException;
    
    /**
     * Retrieve a summary of all reports defined for a given
     * dataset.
     * 
     * @param dataSetId The unique identifier of the DataSet
     * @param saml SAML assertion for the security system.
     * @return Array of report summaries.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     * @throws ConnectException
     */
    public org.psygrid.data.reporting.definition.dto.Report[] getAllReportsByDataSet(long dataSetId, String saml)
        throws RemoteException, RepositoryServiceFault, ConnectException;
    
    /**
     * Generate a report from the given report definition 
     * for the given record.
     * 
     * @param reportDefId The unique identifier of the report 
     * definition.
     * @param recordId The unique identifier of the record.
     * @param saml SAML assertion for the security system.
     * @return The generated report.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
    public org.psygrid.data.reporting.RecordReport generateReport(long reportDefId, long recordId, String saml)
        throws RemoteException, RepositoryServiceFault, GroupsNotAllowedException;

    /**
     * Generate a report showing trends across particular documents 
     * in the given dataset
     *  
     * @param reportId
     * @param datasetId
     * @param saml
     * @return The trend report
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
  //  public org.psygrid.data.reporting.Report generateTrendReport(long reportId, long datasetId, String saml)
    //	throws RemoteException, RepositoryServiceFault, GroupsNotAllowedException;
    
    /**
     * Generate the supplied report dynamically in the given format.
     *  
     * @param report
     * @param formatType
     * @param saml
     * @return The output array
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     * @throws ReportException
     * @throws ConnectException
     */
    public byte[] generateDynamicManagementReport(org.psygrid.data.reporting.definition.dto.ManagementReport report, String formatType, String saml) 
		throws RemoteException, RepositoryServiceFault, ReportRenderingException, GroupsNotAllowedException, ConnectException;
    
    /**
     * Generate the supplied record report dynamically, for the specified record
     * and in the given format.
     * 
     * @param report
     * @param identifier
     * @param formatType
     * @param saml
     * @return output byte array
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     * @throws ReportRenderingException
     * @throws ConnectException
     */
    public byte[] generateDynamicRecordReport(org.psygrid.data.reporting.definition.dto.RecordReport report, String identifier, String formatType, String saml) 
	throws RemoteException, RepositoryServiceFault, ReportRenderingException, GroupsNotAllowedException, ConnectException;
    
    /**
     * Generate the supplied trends report dynamically in the given format.
     * 
     * @param report
     * @param formatType
     * @param saml
     * @return
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     * @throws ReportRenderingException
     * @throws GroupsNotAllowedException
     * @throws ConnectException
     */
    public byte[] generateDynamicTrendsReport(TrendsReport report, String formatType, String saml) 
	throws RemoteException, RepositoryServiceFault, ReportRenderingException, GroupsNotAllowedException, ConnectException;
    
    /**
     * Retrieve the full report for the report id specified. 
     * 
     * @param reportId
     * @param saml
     * @return report
     * @throws RemoteException
     * @throws NotAuthorisedFault
     * @throws RepositoryServiceFault
     * @throws ConnectException
     */
    public org.psygrid.data.reporting.definition.dto.Report getReport(long reportId, String saml)
		throws RemoteException, RepositoryServiceFault, ConnectException;

    
    /**
     * Save a report definition.
     * 
     * @param report The report definition to save.
     * @param saml SAML assertion for the security system.
     * @return The unique identifier of the saved report definiiton.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
    public long saveReport(org.psygrid.data.reporting.definition.dto.Report report, String saml)
        throws RemoteException, RepositoryServiceFault;
    
    /**
     * Delete a report definition.
     * 
     * @param reportId The unique identifier of the report 
     * definition.
     * @param saml SAML assertion for the security system.
     * @return The unique identifier of the saved report definiiton.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
    public void deleteReport(long dataSetId, long reportId, String saml)
        throws RemoteException, RepositoryServiceFault;

    /**
     * Retrieve the list of group objects for the given group codes.
     * 
     * Used when specifying the groups for a report that is to be generated
     * dynamically.
     * 
     * @param groupCodes
     * @param saml
     * @return groups
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
    public org.psygrid.data.model.dto.GroupDTO[] getGroupsForCodes(long dsId, String[] groupCodes, String saml) throws RemoteException, RepositoryServiceFault;
    
    /**
     * Retrieve a list of documents for a given dataset.
     * 
     * Used to specify the documents to be used for a given report.
     * 
     * @param projectCode
     * @param saml
     * @return documents
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     * @throws ConnectException
     */
    public org.psygrid.data.model.dto.DocumentDTO[] getDocuments(String projectCode, String saml) 
    throws RemoteException, RepositoryServiceFault, ConnectException;

    /**
     * Retrieve a list of entries for a given document to present as options
     * for generating a basic statistics chart.
     * <p>
     * Note that only entries for which it is sensible to calculate
     * statistics for are returned e.g. numeric entries, derived entries, option
     * entries with codes.
     * 
     * @param projectCode
     * @param documentId
     * @param saml
     * @return
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
    public MinimalEntry[] getEntries(String projectCode, long documentId, String saml) throws RemoteException, RepositoryServiceFault;

    /**
     * Generate a single report.
     *
     * @param id ID of the report
     * @param saml
     * @return Report rendered as a PDF.
     * @throws RemoteException
     * @throws NotAuthorisedFault
     * @throws RepositoryServiceFault
     */
    public byte[] generateMgmtReportById(String projectCode, long id, String saml)
		throws RemoteException, RepositoryServiceFault;

    /**
     * Generate management reports for all datasets in the repository.
     * 
     * @param user used to retrieve the saml certificate, required for some reports 
     * @param date The date the report is being generated.
     * @return The management reports.
     * @throws ConnectException
     */
    public org.psygrid.data.reporting.ManagementReport[] generateAllMgmtReports(String user, Calendar date) throws RemoteException, ConnectException;
    
}

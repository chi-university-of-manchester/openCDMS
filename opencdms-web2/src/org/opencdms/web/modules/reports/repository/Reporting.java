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

package org.opencdms.web.modules.reports.repository;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import org.opencdms.web.modules.reports.models.MgmtReportModel;
import org.opencdms.web.modules.reports.models.RecordReportModel;
import org.opencdms.web.modules.reports.models.TrendReportModel;
import org.psygrid.data.reporting.GroupsNotAllowedException;
import org.psygrid.data.reporting.ReportRenderingException;
import org.psygrid.data.reporting.definition.ReportException;
import org.psygrid.data.repository.RepositoryNoSuchDatasetFault;
import org.psygrid.data.repository.RepositoryServiceFault;
import org.psygrid.data.utils.security.NotAuthorisedFault;

/**
 * Provides a contract to communicate with the Repository's
 * Reporting interface. Can be used to generate reports, to 
 * retrieve individual reports and report summaries. 
 * 
 * @author Lucy Bridges
 *
 */
public interface Reporting {
	
	/**
	 * Retrieve a list of reports for a given type.
	 * 
	 * @param datasetCode
	 * @param type
	 * @param saml
	 * @return reports
	 * @throws RepositoryFault
	 */
	public Map<Long,String> getReports(String datasetCode, String type, String saml) 
	throws ConnectException, SocketTimeoutException, NotAuthorisedFault, RepositoryServiceFault;
	
	/**
	 * Retrieve a list of compatable summary types for a trends report.
	 * 
	 * @param id
	 * @param saml
	 * @return summary types
	 * @throws RepositoryFault
	 */
	public List<String> getSummaryTypesForReport(long id, String saml) 
	throws ConnectException, SocketTimeoutException, NotAuthorisedFault, RepositoryServiceFault, ReportException; 
	
	/**
	 * Retrieve a list of identifiers from the repository for a given
	 * project.
	 * 
	 * @param projectCode
	 * @param saml
	 * @return identifiers
	 * @throws RepositoryFault
	 */
	public List<String> getIdentifiers(String projectCode, String saml) 
	throws RepositoryNoSuchDatasetFault, ConnectException, SocketTimeoutException, 
	RepositoryServiceFault, NotAuthorisedFault, RemoteException;
	
	/**
	 * Will return whether a report is a recruitment or UKCRN summary
	 * report. Returns null if not.
	 * 
	 * @param id
	 * @param saml
	 * @return type
	 * @throws RepositoryFault
	 */
	public String getReportType(long id, String saml) throws RepositoryFault;
	
	/**
	 * Generate a trends report.
	 * 
	 * Returns a pdf/xls as a byte array 
	 * 
	 * @param trendsreport
	 * @param saml
	 * @return byte array
	 * @throws RepositoryFault
	 */
	public byte[] generateReport(TrendReportModel trendsreport, String saml) 
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, ReportException, 
	ReportRenderingException, GroupsNotAllowedException, NotAuthorisedFault;
	
	/**
	 * Generate a record report.
	 * 
	 * Returns a pdf/xls as a byte array 
	 * 
	 * @param recordreport
	 * @param saml
	 * @return byte array
	 * @throws RepositoryFault
	 */
	public byte[] generateReport(RecordReportModel recordreport, String saml) 
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, 
	ReportRenderingException, GroupsNotAllowedException, NotAuthorisedFault;
	
	/**
	 * Generate a management report.
	 * 
	 * Returns a pdf/xls as a byte array 
	 * 
	 * @param managementreport
	 * @param saml
	 * @return byte array
	 * @throws RepositoryFault
	 */
	public byte[] generateReport(MgmtReportModel managementreport, String saml)
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, ReportException, ReportRenderingException, GroupsNotAllowedException, NotAuthorisedFault;

	
	/**
	 * Get a list of documents to be included in particular mangement reports.
	 * 
	 * @param projectCode
	 * @param saml
	 * @return documentBeans
	 * @throws RepositoryFault
	 */
	//public List<DocumentBean> getDocuments(String projectCode, String saml) 
	//throws RepositoryFault;
}


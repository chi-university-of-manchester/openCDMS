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

import javax.xml.rpc.ServiceException;

import org.psygrid.data.model.dto.extra.MinimalEntry;
import org.psygrid.data.reporting.definition.dto.ManagementReport;
import org.psygrid.data.reporting.definition.dto.RecordReport;
import org.psygrid.data.reporting.definition.dto.Report;
import org.psygrid.data.reporting.definition.dto.TrendsReport;
import org.psygrid.data.repository.RepositoryServiceFault;
import org.psygrid.data.utils.security.NotAuthorisedFault;
import org.psygrid.services.SecureSoapBindingImpl;
import org.springframework.context.ApplicationContext;

/**
 * Implementation of the Repository's Reports web-service
 * 
 * @author Rob Harper
 *
 */
public class ReportsSoapBindingImpl extends SecureSoapBindingImpl implements Reports {

	private Reports service = null;

	@Override
	protected void onInit() throws ServiceException {
		super.onInit();
		ApplicationContext ctx = getWebApplicationContext();
		service = (Reports)ctx.getBean("reportsService");
	}
	
	public org.psygrid.data.reporting.RecordReport generateReport(long reportDefId, long recordId, String saml) 
	throws RemoteException, RepositoryServiceFault, NotAuthorisedFault, GroupsNotAllowedException {
		return service.generateReport(reportDefId, recordId, saml);
	}

	public byte[] generateDynamicManagementReport(ManagementReport report, String formatType, String saml) 
	throws RemoteException, RepositoryServiceFault, NotAuthorisedFault, ReportRenderingException, GroupsNotAllowedException, ConnectException {
		return service.generateDynamicManagementReport(report, formatType, saml);
	}

	public byte[] generateDynamicRecordReport(RecordReport report, String identifier, String formatType, String saml) 
	throws RemoteException, RepositoryServiceFault, NotAuthorisedFault, ReportRenderingException, GroupsNotAllowedException, ConnectException {
		return service.generateDynamicRecordReport(report, identifier, formatType, saml);
	}

	public byte[] generateDynamicTrendsReport(TrendsReport report, String formatType, String saml) 
	throws RemoteException, RepositoryServiceFault, NotAuthorisedFault, ReportRenderingException, GroupsNotAllowedException, ConnectException {
		return service.generateDynamicTrendsReport(report, formatType, saml);
	}

	public org.psygrid.data.reporting.definition.dto.Report[] getReportsByDataSet(long dataSetId, String saml) 
	throws RemoteException, RepositoryServiceFault, NotAuthorisedFault, ConnectException {
		return service.getReportsByDataSet(dataSetId, saml);
	}

	public org.psygrid.data.reporting.definition.dto.Report[] getAllReportsByDataSet(long dataSetId, String saml) 
	throws RemoteException, RepositoryServiceFault, NotAuthorisedFault, ConnectException {
		return service.getAllReportsByDataSet(dataSetId, saml);
	}


	public org.psygrid.data.reporting.definition.dto.Report[] getReportsOfType(String dataSetCode, String type, String saml)
	throws RemoteException, RepositoryServiceFault, NotAuthorisedFault, ConnectException {
		return service.getReportsOfType(dataSetCode, type, saml);
	}

	public org.psygrid.data.reporting.definition.dto.Report getReport(long reportId, String saml)
	throws RemoteException, NotAuthorisedFault, RepositoryServiceFault, ConnectException {
		return service.getReport(reportId, saml);
	}

	public long saveReport(Report report, String saml) throws RemoteException, RepositoryServiceFault, NotAuthorisedFault {
		return service.saveReport(report, saml);
	}

	public void deleteReport(long dataSetId, long reportId, String saml) throws RemoteException, RepositoryServiceFault, NotAuthorisedFault {
		service.deleteReport(dataSetId, reportId, saml);
	}

	public org.psygrid.data.model.dto.GroupDTO[] getGroupsForCodes(long dsId, String[] groupCodes, String saml) throws RemoteException, RepositoryServiceFault, NotAuthorisedFault {
		return service.getGroupsForCodes(dsId, groupCodes, saml);
	}

	public org.psygrid.data.model.dto.DocumentDTO[] getDocuments(String projectCode, String saml) 
	throws RemoteException, RepositoryServiceFault, NotAuthorisedFault, ConnectException {
		return service.getDocuments(projectCode, saml);
	}

	public MinimalEntry[] getEntries(String projectCode, long documentId, String saml) throws RemoteException, RepositoryServiceFault, NotAuthorisedFault {
		return service.getEntries(projectCode, documentId, saml);
	}
	
    public byte[] generateMgmtReportById(String projectCode, long id, String saml)
	throws RemoteException, NotAuthorisedFault, RepositoryServiceFault {
    	return service.generateMgmtReportById(projectCode, id, saml);
    }

    /**
     * Both the web service and the internal service layer implement the Reports interface. 
     * This method is only needed by the ReportsJob which calls the method on the internal service layer.
     * This will be fixed when we switch to defining web services using annotations.
     */
    public org.psygrid.data.reporting.ManagementReport[] generateAllMgmtReports(String user, Calendar date) throws RemoteException {
		return null;
	}

}


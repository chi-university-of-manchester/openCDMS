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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opensaml.SAMLAssertion;
import org.psygrid.data.model.dto.DocumentDTO;
import org.psygrid.data.model.dto.DocumentOccurrenceDTO;
import org.psygrid.data.model.dto.EntryDTO;
import org.psygrid.data.model.dto.RecordDTO;
import org.psygrid.data.model.dto.extra.MinimalEntry;
import org.psygrid.data.model.hibernate.EditAction;
import org.psygrid.data.model.hibernate.RetrieveDepth;
import org.psygrid.data.reporting.definition.dto.AbstractChartItem;
import org.psygrid.data.reporting.definition.dto.ManagementReport;
import org.psygrid.data.reporting.definition.dto.RecordChart;
import org.psygrid.data.reporting.definition.dto.RecordReport;
import org.psygrid.data.reporting.definition.dto.Report;
import org.psygrid.data.reporting.definition.dto.SimpleChartItem;
import org.psygrid.data.reporting.definition.dto.SimpleChartRow;
import org.psygrid.data.reporting.definition.dto.TrendsChart;
import org.psygrid.data.reporting.definition.dto.TrendsChartRow;
import org.psygrid.data.reporting.definition.dto.TrendsReport;
import org.psygrid.data.reporting.renderer.PdfRenderer;
import org.psygrid.data.reporting.renderer.RendererException;
import org.psygrid.data.repository.RepositoryServiceFault;
import org.psygrid.data.repository.dao.DAOException;
import org.psygrid.data.repository.dao.NoDatasetException;
import org.psygrid.data.repository.dao.RepositoryDAO;
import org.psygrid.data.utils.security.DocumentSecurityHelper;
import org.psygrid.data.utils.security.NotAuthorisedFault;
import org.psygrid.data.utils.service.AbstractServiceImpl;
import org.psygrid.logging.AuditLogger;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.PGSecurityInvalidSAMLException;
import org.psygrid.security.PGSecuritySAMLVerificationException;
import org.psygrid.security.RBACAction;
import org.psygrid.security.accesscontrol.AEFGroup;
import org.psygrid.security.accesscontrol.AEFProject;
import org.psygrid.security.accesscontrol.IAccessEnforcementFunction;
import org.psygrid.security.utils.SAMLUtilities;
import org.psygrid.www.xml.security.core.types.PrivilegeType;
import org.psygrid.www.xml.security.core.types.ProjectType;

/**
 * Implementation of the Repository's Reports web-service
 * 
 * @author Rob Harper
 *
 */
public class ReportsServiceImpl extends AbstractServiceImpl implements Reports {

	/**
	 * Name of the component, used for audit logging
	 */
	private static final String COMPONENT_NAME = "Reports";

	/**
	 * General purpose logger
	 */
	private static Log sLog = LogFactory.getLog(ReportsServiceImpl.class);

	/**
	 * Audit logger
	 */
	private static AuditLogger logHelper = new AuditLogger(ReportsServiceImpl.class);

	private ReportingDAO reportingDAO = null;

	protected String getComponentName() {
		return COMPONENT_NAME;
	}
	
	public ReportingDAO getReportingDAO() {
		return reportingDAO;
	}

	public void setReportingDAO(ReportingDAO reportingDAO) {
		this.reportingDAO = reportingDAO;
	}

	public org.psygrid.data.reporting.RecordReport generateReport(long reportDefId, long recordId, String saml) throws GroupsNotAllowedException {

		String[] codes = repositoryDAO.getProjectAndGroupForInstance(recordId);
		checkPermissionsByGroup(saml, "generateReport", RBACAction.ACTION_DR_GENERATE_REPORT, codes[0], codes[1]);			

		RecordReport r = reportingDAO.getRecordReport(reportDefId);
		RecordDTO record = repositoryDAO.getRecord(recordId, RetrieveDepth.RS_SUMMARY);
		r.setRecord(record);

		/*
		 * Retrieve the list of groups that this user is allowed to access. This is used 
		 * below to check that the user is allowed to view the group specified in the
		 * report.
		 * 
		 * Unfortunately, the way groups are stored is not standard across different
		 * reports and is quite often specified in charts or not at all (in which case
		 * all groups are selected).
		 */
		List<String> restrictBy = getGroups(codes[0], saml);

		/*
		 * Check that the user is allowed to access the document specified in the report.
		 * Some documents, such as ones dependent on a particular treatment being assigned
		 * during randomization are restricted to treatment administrators only.
		 */
		String userName = findUserName(saml);
		Report[] reports = new Report[1];
		reports[0] = r;
		Report[] allowedReports = getAllowedReports(reports, codes[0], userName, saml);
		if (allowedReports == null || allowedReports.length == 0) {
			throw new NotAuthorisedFault("Unable to view the requested report");
		}
		r = (RecordReport)allowedReports[0];

		/*
		 * Generate the report, with a given list of 'allowed' groups - if the report contains
		 * any groups not in this list then an authorisation error will be thrown.
		 */
		org.psygrid.data.reporting.RecordReport report = (org.psygrid.data.reporting.RecordReport)reportingDAO.generateReport(r.toHibernate(), restrictBy, getPrettyName(userName), saml);
		return report;
	}

	public byte[] generateDynamicManagementReport(ManagementReport report, String formatType, String saml) 
	   throws ReportRenderingException, GroupsNotAllowedException {

		Long dataSetId = report.getDataSet().getId();
		String projectCode = repositoryDAO.getProjectCodeForDataset(dataSetId);

		checkPermissionsByProject(saml,"generateDynamicManagementReport",RBACAction.ACTION_DR_GENERATE_DYNAMIC_REPORT,projectCode);		

		/*
		 * Check that the user is allowed to access the particular management report
		 * 
		 * Some reports, such as the Document and Record status reports are accessible
		 * by a wider range of users.
		 * 
		 * If no RBACAction is specified in the report then it will be allowed anyway.
		 */
		String userName = findUserName(saml);
		checkReportAccess(report, projectCode, userName, saml);

		/*
		 * Retrieve the list of groups that this user is allowed to access. This is used 
		 * below to check that the user is allowed to view all groups specified in the
		 * report.
		 * 
		 * Unfortunately, the way groups are stored is not standard across different
		 * reports and is quite often specified in charts or not at all (in which case
		 * all groups are selected).
		 */
		List<String> restrictBy = getGroups(projectCode, saml);

		/*
		 * Check that the user is allowed to access all documents specified in the report.
		 * Some documents, such as ones dependent on a particular treatment being assigned
		 * during randomization are restricted to treatment administrators only.
		 */
		Report[] reports = new Report[1];
		reports[0] = report;
		Report[] allowedReports = getAllowedReports(reports, projectCode, userName, saml);
		if (allowedReports == null || allowedReports.length == 0) {
			throw new NotAuthorisedFault("Unable to view the requested report");
		}

		/*
		 * Generate the report, with a given list of 'allowed' groups - if the report contains
		 * any groups not in this list then an authorisation error will be thrown.
		 */
		byte[] reportStream = reportingDAO.generateDynamicReport(report, formatType, restrictBy, saml, getPrettyName(userName));

		return reportStream;
	}

	public byte[] generateDynamicRecordReport(RecordReport report, String identifier, String formatType, String saml) 
	throws ReportRenderingException, GroupsNotAllowedException {

			/*
			 * Check that the user is allowed to access reports through the web interface
			 */
			Long dataSetId = report.getDataSet().getId();
			String projectCode = repositoryDAO.getProjectCodeForDataset(dataSetId);

			checkPermissionsByProject(saml,"generateDynamicManagementReport",RBACAction.ACTION_DR_GENERATE_DYNAMIC_RECORD_REPORT,projectCode);		
			
			/*
			 * Check that the user is allowed to access all documents specified in the report.
			 * Some documents, such as ones dependent on a particular treatment being assigned
			 * during randomization, are restricted to treatment administrators only.
			 */
			String userName = findUserName(saml);
			Report[] reports = new Report[1];
			reports[0] = report;
			Report[] allowedReports = getAllowedReports(reports, projectCode, userName, saml);
			if (allowedReports == null || allowedReports.length == 0) {
				throw new NotAuthorisedFault("Unable to view the requested report");
			}

			/*
			 * Retrieve the list of groups that this user is allowed to access. This is used 
			 * below to check that the user is allowed to view all groups specified in the
			 * report.
			 * 
			 * Unfortunately, the way groups are stored is not standard across different
			 * reports and is quite often specified in charts or not at all (in which case
			 * all groups are selected).
			 */
			List<String> restrictBy = getGroups(projectCode, saml);

			/*
			 * Generate the report, with a given list of 'allowed' groups - if the report contains
			 * any groups not in this list then an authorisation error will be thrown.
			 */
			byte[] reportStream = reportingDAO.generateDynamicRecordReport(report, identifier, formatType, restrictBy, saml, getPrettyName(userName));

			return reportStream;
	}

	public byte[] generateDynamicTrendsReport(TrendsReport report, String formatType, String saml) 
	  throws ReportRenderingException, GroupsNotAllowedException {

		/*
		 * Check that the user is allowed to access reports through the web interface
		 */
		Long dataSetId = report.getDataSet().getId();
		String projectCode = repositoryDAO.getProjectCodeForDataset(dataSetId);
		checkPermissionsByProject(saml,"generateDynamicTrendsReport",RBACAction.ACTION_DR_GENERATE_DYNAMIC_TRENDS_REPORT,projectCode);		

		/*
		 * Check that the user is allowed to access all documents specified in the report.
		 * Some documents, such as ones dependent on a particular treatment being assigned
		 * during randomization, are restricted to treatment administrators only.
		 */
		String userName = findUserName(saml);
		Report[] reports = new Report[1];
		reports[0] = report;
		Report[] allowedReports = getAllowedReports(reports, projectCode, userName, saml);
		if (allowedReports == null || allowedReports.length == 0) {
			throw new NotAuthorisedFault("Unable to view the requested report");
		}

		/*
		 * Retrieve the list of groups that this user is allowed to access. This is used 
		 * below to check that the user is allowed to view all groups specified in the
		 * report.
		 * 
		 * Unfortunately, the way groups are stored is not standard across different
		 * reports and is quite often specified in charts or not at all (in which case
		 * all groups are selected).
		 */
		List<String> restrictBy = getGroups(projectCode, saml);

		/*
		 * Generate the report, with a given list of 'allowed' groups - if the report contains
		 * any groups not in this list then an authorisation error will be thrown.
		 */
		byte[] reportStream = reportingDAO.generateDynamicReport(report, formatType, restrictBy, saml, getPrettyName(userName));
		return reportStream;
	}

	public org.psygrid.data.reporting.definition.dto.Report[] getReportsByDataSet(long dataSetId, String saml)  {

			String projectCode = repositoryDAO.getProjectCodeForDataset(dataSetId);
			checkPermissionsByProject(saml,"getReportsByDataSet",RBACAction.ACTION_DR_GET_REPORTS_BY_DATASET,projectCode);		

			Report[] dsReports = reportingDAO.getReportsByDataSet(dataSetId);
			if (dsReports == null) {
				return null;
			}
			//check the documents are allowed.. this will ignore a report completely if it contains any element of a denied document.
			String userName = findUserName(saml);
			return getAllowedReports(dsReports, projectCode, userName, saml);
	}


	public org.psygrid.data.reporting.definition.dto.Report[] getAllReportsByDataSet(long dataSetId, String saml) {

			String projectCode = repositoryDAO.getProjectCodeForDataset(dataSetId);
			checkPermissionsByProject(saml,"getAllReportsByDataSet",RBACAction.ACTION_DR_GET_ALL_REPORTS_BY_DATASET,projectCode);		

			Report[] dsReports = reportingDAO.getAllReportsByDataSet(dataSetId);
			if (dsReports == null) {
				return null;
			}
			String userName = findUserName(saml);
			return getAllowedReports(dsReports, projectCode, userName, saml);
	}


	public org.psygrid.data.reporting.definition.dto.Report[] getReportsOfType(String dataSetCode, String type, String saml) {

		try{
			String projectCode = dataSetCode;
			checkPermissionsByProject(saml,"getReportsOfType",RBACAction.ACTION_DR_GET_REPORTS_OF_TYPE,projectCode);		
			Report[] dsReports = reportingDAO.getReportsOfType(dataSetCode, type);
			if (dsReports == null) {
				return null;
			}
			String userName = findUserName(saml);
			return getAllowedReports(dsReports, projectCode, userName, saml);
		}
		catch(NoSuchReportException ex){
			throw new IllegalArgumentException("Invalid report type requested", ex);
		}
	}

	public org.psygrid.data.reporting.definition.dto.Report getReport(long reportId, String saml) {

		org.psygrid.data.reporting.definition.dto.Report report = reportingDAO.getReport(reportId);
		String projectCode = repositoryDAO.getProjectCodeForDataset(report.getDataSet().getId());  
		checkPermissionsByProject(saml,"getReport",RBACAction.ACTION_DR_GET_REPORT,projectCode);		

		String userName = findUserName(saml);
		Report[] reports = new Report[1];
		reports[0] = report;
		Report[] allowedReports = getAllowedReports(reports, projectCode, userName, saml);
		if (allowedReports == null || allowedReports.length == 0) {
			throw new NotAuthorisedFault("Unable to view the requested report");
		}

		return report;
	}

	public long saveReport(Report report, String saml) {
			String projectCode = repositoryDAO.getProjectCodeForDataset(report.getDataSet().getId());
			checkPermissionsByProject(saml,"saveReport",RBACAction.ACTION_DR_SAVE_REPORT,projectCode);		
			return reportingDAO.saveReport(report);
	}

	public void deleteReport(long dataSetId, long reportId, String saml) {
			String projectCode = repositoryDAO.getProjectCodeForDataset(dataSetId);
			checkPermissionsByProject(saml,"deleteReport",RBACAction.ACTION_DR_DELETE_REPORT,projectCode);		
			reportingDAO.deleteReport(Long.valueOf(dataSetId),Long.valueOf(reportId));
	}

	public org.psygrid.data.model.dto.GroupDTO[] getGroupsForCodes(long dsId, String[] groupCodes, String saml) {

		String projectCode = repositoryDAO.getProjectCodeForDataset(dsId);
		checkPermissionsByProject(saml,"getGroupsForCodes",RBACAction.ACTION_DR_GET_GROUPS_FOR_CODES,projectCode);		

		List<String> codes = new ArrayList<String>();
		for (String c: groupCodes) {
			codes.add(c);
		}

		List<org.psygrid.data.model.dto.GroupDTO> listgroups = reportingDAO.getGroupsForCodes(dsId, codes);
		org.psygrid.data.model.dto.GroupDTO[] groups = new org.psygrid.data.model.dto.GroupDTO[listgroups.size()];

		for (int i = 0; i < listgroups.size(); i++) {
			groups[i] = listgroups.get(i);
		}

		return groups;
	}

	public org.psygrid.data.model.dto.DocumentDTO[] getDocuments(String projectCode, String saml) 
	throws RepositoryServiceFault {

		try{
			checkPermissionsByProject(saml,"getDocuments",RBACAction.ACTION_DR_GET_DOCUMENTS,projectCode);		

			org.psygrid.data.model.dto.DataSetDTO ds = repositoryDAO.getSummaryForProjectCode(projectCode, RetrieveDepth.DS_WITH_DOCS);

			String userName = findUserName(saml);

			return this.getAllowedDocuments(ds.getDocuments(), projectCode, userName, saml);
		}
		catch(NoDatasetException ex){
			throw new RepositoryServiceFault(ex);
		}
	}

	public MinimalEntry[] getEntries(String projectCode, long documentId, String saml) throws RemoteException, RepositoryServiceFault, NotAuthorisedFault {

		final String METHOD_NAME = RBACAction.ACTION_DR_GET_ENTRIES.toString();

		try{
			String userName = findUserName(saml);
			String callerIdentity = accessControl.getCallersIdentity();
			logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);

			checkPermissionsByProject(saml,"getEntries",RBACAction.ACTION_DR_GET_ENTRIES,projectCode);		
			MinimalEntry[] entries = reportingDAO.getEntriesForBasicStatsChart(documentId);
			//Remove any entries the user isn't allowed to view
			List<MinimalEntry> allowedEntries = new ArrayList<MinimalEntry>(entries.length);
			for (MinimalEntry entry: entries) {
				String action = entry.getAccessAction();
				if (action == null) {
					allowedEntries.add(entry);
				}
				else {
					RBACAction rbac = RBACAction.valueOf(action);
					if (accessControl.authoriseUser(saml, new AEFGroup(), rbac.toAEFAction(), new AEFProject(null, projectCode, false))) {
						allowedEntries.add(entry);
					}
				}
			}
			MinimalEntry[] finalEntries = allowedEntries.toArray(new MinimalEntry[allowedEntries.size()]);
			return finalEntries;
		}
		catch(PGSecurityInvalidSAMLException ex){
			throw new NotAuthorisedFault("The supplied SAML assertion has expired", ex);
		}
		catch(PGSecuritySAMLVerificationException ex){
			throw new NotAuthorisedFault("The supplied SAML assertion does not come from a trusted issuer", ex);
		}
		catch(PGSecurityException ex){
			throw new NotAuthorisedFault("An error occurred during authorisation", ex);
		}
	}

	public byte[] generateMgmtReportById(String projectCode, long id,
			String saml) throws RemoteException, NotAuthorisedFault,
			RepositoryServiceFault {

		try {
			checkPermissionsByProject(saml, "generateMgmtReportById",
					RBACAction.ACTION_DR_GENERATE_MGMT_REPORT_BY_ID,
					projectCode);

			String userName = findUserName(saml);
			org.psygrid.data.reporting.ManagementReport report = reportingDAO
					.generateMgmtReportById(id, userName);

			PdfRenderer pdfRenderer = new PdfRenderer();
			ByteArrayOutputStream pdfOs = new ByteArrayOutputStream();
			pdfRenderer.render(report, pdfOs);

			return pdfOs.toByteArray();
		} catch (RendererException ex) {
			sLog.error(ex.getMessage(), ex);
			throw new RepositoryServiceFault(
					"An error occurred during report rendering", ex);
		} catch (IOException ex) {
			sLog.error(ex.getMessage(), ex);
			throw new RepositoryServiceFault(
					"An error occurred during report rendering", ex);
		}
	}
	
	/**
	 * Retrieve the list of groups the requester has permissions to access, based
	 * on their SAML assertion.
	 * 
	 * Unfortunately, the way groups are stored is not standard across different
	 * reports and is quite often specified in charts or not at all (in which case
	 * all groups are selected). Retrieving the groups specified in the report itself
	 * has therefore been left to the repositoryDAO object.
	 * 
	 * @param ds
	 * @param saml
	 * @return list of authorised group codes
	 * @throws NotAuthorisedFault
	 */
	private List<String> getGroups(String projectCode, String saml) throws NotAuthorisedFault {

		SAMLAssertion sa = null;
		List<String> groupCode = new ArrayList<String>();
		try {
			sa = SAMLUtilities.retrieveSAMLAssertion(saml);
		}
		catch(PGSecurityException ex){
			sLog.error("getGroups"+": "+ex.getClass().getSimpleName(),ex);
			throw new NotAuthorisedFault("An error occurred during authorisation", ex);
		}

		try {
			ProjectType projectType = new ProjectType(null, projectCode, null, null, false); 
			PrivilegeType[] privilegeType = SAMLUtilities.getUsersPrivilegesInProjectFromST(sa, projectType);

			for (PrivilegeType p: privilegeType) {
				if (p.getGroup() != null) {
					if (p.getGroup().getIdCode() != null) {
						groupCode.add(p.getGroup().getIdCode());
					}
				}
			}

			return groupCode;
		}
		catch(NullPointerException npe) {
			return null;
		}
	}

	/**
	 * Extract a name for the given CN string from
	 * a saml assertion
	 * @param cnName
	 * @return name
	 */
	private String getPrettyName(String cnName) {

		if (cnName == null || cnName.equals("")) {
			return cnName;
		}
		//e.g CN=CRO One, OU=users, O=psygrid, C=uk
		try {
			String[] a = cnName.split(",", 2);
			String[] b = a[0].split("=", 2);
			return b[1];
		}
		catch (Exception e) {
			return cnName;
		}
	}

	/**
	 * Check that the user is allowed to view the given management report in project specified.
	 * 
	 * Note: if no RBACAction is found in the report the user is assumed to be able to access it.
	 * 
	 * Throws NotAuthorisedFault if the user is not allowed to access the report.
	 *  
	 * @param report
	 * @param projectCode
	 * @param userName
	 * @param saml
	 * @throws PGSecurityInvalidSAMLException
	 * @throws PGSecurityException
	 * @throws ConnectException
	 * @throws PGSecuritySAMLVerificationException
	 * @throws RepositoryServiceFault
	 * @throws RemoteException
	 * @throws NotAuthorisedFault
	 */
	private void checkReportAccess(ManagementReport report, String projectCode, String userName, String saml) {
		String action = report.getViewAction();
		if (action == null) {
			throw new NotAuthorisedFault("User "+userName+" is not authorised to view the report "+report.getTitle()+" for the dataset "+projectCode+" because no RBACAction has been specified.");
		}
		RBACAction rbac = RBACAction.valueOf(action);			
		try {
			if (accessControl.authoriseUser(saml, new AEFGroup(), rbac.toAEFAction(), new AEFProject(null, projectCode, false)) ){
				return;
			}
			else {
				//Should be thrown by the AccessControl method above)
				throw new NotAuthorisedFault("User "+userName+" is not authorised to view the report "+report.getTitle()+" for the dataset "+projectCode);
			}
		} 
		catch(PGSecurityInvalidSAMLException ex){
			throw new NotAuthorisedFault("The supplied SAML assertion has expired", ex);
		}
		catch(PGSecuritySAMLVerificationException ex){
			throw new NotAuthorisedFault("The supplied SAML assertion does not come from a trusted issuer", ex);
		}
		catch(PGSecurityException ex){
			throw new NotAuthorisedFault("An error occurred during authorisation", ex);
		}
	}

	/**
	 * Return only the reports the user has permission to access from the set of all reports in the dataset, based on 
	 * whether they have access to all documents used each report. 
	 * 
	 * @param datasetReports
	 * @param projectCode
	 * @param userName
	 * @param saml
	 * @return allowedReports
	 * @throws PGSecurityInvalidSAMLException
	 * @throws PGSecurityException
	 * @throws ConnectException
	 * @throws PGSecuritySAMLVerificationException
	 * @throws RepositoryServiceFault
	 * @throws RemoteException
	 * @throws NotAuthorisedFault
	 */
	private Report[] getAllowedReports(Report[] datasetReports, String projectCode, String userName, String saml) {

		org.psygrid.data.model.dto.DataSetDTO ds = null;
		try {
			//Retrieve documents for the dataset
			//TODO check the entries in each document (the RetrieveDepth needs to be DS_COMPLETE but this is too slow)
			ds = repositoryDAO.getSummaryForProjectCode(projectCode, RetrieveDepth.DS_WITH_DOCS);
		}
		catch (NoDatasetException e) {
			//This should never happen
			sLog.error("No dataset for the project code "+projectCode);
			return null;
		}

		DocumentDTO[] allowedDocuments = getAllowedDocuments(ds.getDocuments(), projectCode, userName, saml);

		List<org.psygrid.data.reporting.definition.dto.Report> reports = new ArrayList<org.psygrid.data.reporting.definition.dto.Report>(); 
		nextReport: for (org.psygrid.data.reporting.definition.dto.Report report: datasetReports) {
			if (report instanceof RecordReport) {
				RecordReport r = (RecordReport)report;

				//check that every document for each item in each chart is allowed
				for (RecordChart chart: r.getCharts()) {
					for (SimpleChartRow row: chart.getRows()) {
						for (AbstractChartItem series: row.getSeries()) {
							SimpleChartItem item = (SimpleChartItem)series;
							boolean hasPermission = false;
							//Check that the document is allowed by comparing the document occ with all docOccs of all allowed documents
							if (allowedDocuments != null) {
								for (DocumentDTO allowed: allowedDocuments) {
									for (DocumentOccurrenceDTO occ: allowed.getOccurrences()) {
										if (occ.getId().equals(item.getDocOccurrence().getId())) {
											//if the document is in the list of allowed documents..
											hasPermission = true;
											break;
										}
									}
								}
							}
							if (!hasPermission) {
								//Unable to find item's document in list of allowed documents
								continue nextReport;
							}
						}
					}
				}
				//Report must be allowed if we get this far
				reports.add(r);
			}
			else if (report instanceof TrendsReport) {
				TrendsReport r = (TrendsReport)report;

				for (TrendsChart chart: r.getCharts()) {
					for (TrendsChartRow row: chart.getRows()) {
						for (AbstractChartItem series: row.getSeries()) {
							SimpleChartItem item = (SimpleChartItem)series;

							boolean hasPermission = false;
							//Check that the document is allowed by comparing the document occ with all docOccs of all allowed documents
							for (DocumentDTO allowed: allowedDocuments) {
								for (DocumentOccurrenceDTO occ: allowed.getOccurrences()) {
									if (occ.getId().equals(item.getDocOccurrence().getId())) {
										//if the document is in the list of allowed documents..
										hasPermission = true;
										break;
									}
								}
							}

							if (!hasPermission) {
								//Unable to find item's document in list of allowed documents
								continue nextReport;
							}
						}
					}
				}

				//Report must be allowed if we get this far
				reports.add(r);
			}
			else if (report instanceof ManagementReport) {
				ManagementReport r = (ManagementReport)report;
				try {
					checkReportAccess(r, projectCode, userName, saml);
					reports.add(report);
				}
				catch (NotAuthorisedFault f) {
					//Report is not added if this is thrown
				}
			}
		}

		org.psygrid.data.reporting.definition.dto.Report[] allowedReports = new org.psygrid.data.reporting.definition.dto.Report[reports.size()];
		for (int i=0; i< reports.size(); i++) {
			allowedReports[i] = reports.get(i);
		}
		return allowedReports;
	}

	/**
	 * Examine the documents provided and return the ones allowed by the user
	 *  
	 * @param documents
	 * @param projectCode
	 * @param user
	 * @param saml
	 * @return allowedDocuments
	 * @throws PGSecurityInvalidSAMLException
	 * @throws PGSecurityException
	 * @throws ConnectException
	 * @throws PGSecuritySAMLVerificationException
	 */
	private DocumentDTO[] getAllowedDocuments(DocumentDTO[] documents, String projectCode, String user, String saml) {
		if (documents == null) {
			return null;
		}
		List<DocumentDTO> allowed = new ArrayList<DocumentDTO>();
		for (DocumentDTO document: documents) {
			String action = document.getAction();
			document.setEditingPermitted(false);	//No need to check authorisation as document instances should not be created/edited
			docHelper.setSaml(saml);
			if (action == null) {
				DocumentDTO allowedDoc = docHelper.checkEntries(document, projectCode, user);
				//if the response to an entry is not viewable remove the whole document
				boolean viewable = true;
				for (EntryDTO e: allowedDoc.getEntries()) {
					if (EditAction.DENY.toString().equals(e.getEditingPermitted())) {
						viewable = false;
						break;
					}
				}
				if (viewable) {
					allowed.add(document);
				}
				continue;
			}
			RBACAction rbac = RBACAction.valueOf(action);			
			try {
				if (accessControl.authoriseUser(saml, new AEFGroup(), rbac.toAEFAction(), new AEFProject(null, projectCode, false)) ){
					DocumentDTO allowedDoc = docHelper.checkEntries(document, projectCode, user);
					//if the response to an entry is not viewable remove the whole document
					boolean viewable = true;
					for (EntryDTO e: allowedDoc.getEntries()) {
						if (EditAction.DENY.toString().equals(e.getEditingPermitted())) {
							viewable = false;
							break;
						}
					}
					if (viewable) {
						allowed.add(document);
					}
				}
			} 
			catch(PGSecurityInvalidSAMLException ex){
				throw new NotAuthorisedFault("The supplied SAML assertion has expired", ex);
			}
			catch(PGSecuritySAMLVerificationException ex){
				throw new NotAuthorisedFault("The supplied SAML assertion does not come from a trusted issuer", ex);
			}
			catch(PGSecurityException ex){
				throw new NotAuthorisedFault("An error occurred during authorisation", ex);
			}
		}
		DocumentDTO[] allowedDocs = new DocumentDTO[allowed.size()];
		for (int i=0; i<allowed.size(); i++) {
			allowedDocs[i] = allowed.get(i);
		}
		return allowedDocs;
	}

	public org.psygrid.data.reporting.ManagementReport[] generateAllMgmtReports(String user, Calendar date) throws ConnectException {
		List<org.psygrid.data.reporting.ManagementReport> reports = reportingDAO.generateAllMgmtReports(user, date.getTime());
		return reports.toArray(new org.psygrid.data.reporting.ManagementReport[]{});
	}

}

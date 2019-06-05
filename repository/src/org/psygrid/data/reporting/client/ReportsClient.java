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

package org.psygrid.data.reporting.client;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.rpc.ServiceException;

import org.apache.axis.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.data.model.dto.extra.MinimalEntry;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.data.model.hibernate.Persistent;
import org.psygrid.data.reporting.GroupsNotAllowedException;
import org.psygrid.data.reporting.ReportRenderingException;
import org.psygrid.data.reporting.Reports;
import org.psygrid.data.reporting.ReportsServiceLocator;
import org.psygrid.data.reporting.definition.IManagementReport;
import org.psygrid.data.reporting.definition.IRecordReport;
import org.psygrid.data.reporting.definition.IReport;
import org.psygrid.data.reporting.definition.ITrendsReport;
import org.psygrid.data.repository.RepositoryServiceFault;
import org.psygrid.data.utils.security.NotAuthorisedFault;

/**
 * Class to act as a layer of abstraction between a Java client
 * and the web services exposed by the data repository for
 * reporting.
 * 
 * @author Rob Harper
 *
 */
public class ReportsClient extends org.psygrid.common.AbstractClient {

	private static final Log LOG = LogFactory.getLog(ReportsClient.class);
	
	/**
	 * Default no-arg constructor
	 */
	public ReportsClient(){}

	/**
	 * Constructor that accepts a value for the url where the web
	 * service is located.
	 * 
	 * @param url
	 */
	public ReportsClient(URL url){
		super(url);
	}

	public List<IReport> getReportsByDataSet(Long dataSetId, String saml)
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, NotAuthorisedFault{
		try{
			Reports service = getService();
			org.psygrid.data.reporting.definition.dto.Report[] dtoReps = service.getReportsByDataSet(dataSetId, saml);
			List<IReport> reports = new ArrayList<IReport>();
			for ( int i=0; i<dtoReps.length; i++){
				reports.add(dtoReps[i].toHibernate());
			}
			return reports;
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(ServiceException ex){
			throw new RuntimeException(ex);
		}
		catch(RemoteException ex){
			throw new RuntimeException(ex);
		}

	}

	public List<IReport> getReportsOfType(String dataSetCode, String type, String saml)
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, NotAuthorisedFault{
		try{
			Reports service = getService();
			org.psygrid.data.reporting.definition.dto.Report[] dtoReps = service.getReportsOfType(dataSetCode, type, saml);
			List<IReport> reports = new ArrayList<IReport>();
			for ( int i=0; i<dtoReps.length; i++){
				reports.add(dtoReps[i].toHibernate());
			}
			return reports;
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(ServiceException ex){
			throw new RuntimeException(ex);
		}
		catch(RemoteException ex){
			throw new RuntimeException(ex);
		}

	}
	
	public List<IReport> getAllReportsByDataSet(Long dataSetId, String saml)
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, NotAuthorisedFault {
		try {
			Reports service = getService();
			org.psygrid.data.reporting.definition.dto.Report[] dtoReps = service
			.getAllReportsByDataSet(dataSetId.longValue(), saml);
			List<IReport> reports = new ArrayList<IReport>();
			for (int i = 0; i < dtoReps.length; i++) {
				reports.add(dtoReps[i].toHibernate());
			}
			return reports;
		} catch (AxisFault fault) {
			handleAxisFault(fault, LOG);
			return null;
		} catch (ServiceException ex) {
			throw new RuntimeException(ex);
		} catch (RemoteException ex) {
			throw new RuntimeException(ex);
		}

	}

	public org.psygrid.data.reporting.RecordReport generateReport(Long reportId, Long recordId, String saml)
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, NotAuthorisedFault, GroupsNotAllowedException{
		try{
			Reports service = getService();
			return service.generateReport(reportId, recordId, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(ServiceException ex){
			throw new RuntimeException(ex);
		}
		catch(RemoteException ex){
			throw new RuntimeException(ex);
		}

	}

/*	public org.psygrid.data.reporting.Report generateTrendReport(Long reportId, Long datasetId, String saml)
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, NotAuthorisedFault, GroupsNotAllowedException {
		try{
			Reports service = getService();
			return service.generateTrendReport(reportId, datasetId, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(ServiceException ex){
			throw new RuntimeException(ex);
		}
		catch(RemoteException ex){
			throw new RuntimeException(ex);
		}

	}
	*/
	public byte[] generateDynamicManagementReport(IManagementReport report, String formatType, String saml)
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, NotAuthorisedFault, ReportRenderingException, GroupsNotAllowedException {
		try{
			Reports service = getService();
			return service.generateDynamicManagementReport(report.toDTO(), formatType, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(ServiceException ex){
			throw new RuntimeException(ex);
		}
		catch(RemoteException ex){
			throw new RuntimeException(ex);
		}
	}
	
	public byte[] generateDynamicTrendsReport(ITrendsReport report, String formatType, String saml)
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, NotAuthorisedFault, ReportRenderingException, GroupsNotAllowedException {
		try{
			Reports service = getService();
			return service.generateDynamicTrendsReport(report.toDTO(), formatType, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(ServiceException ex){
			throw new RuntimeException(ex);
		}
		catch(RemoteException ex){
			throw new RuntimeException(ex);
		}
	}
	
	public byte[] generateDynamicRecordReport(IRecordReport report, String identifier, String formatType, String saml)
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, NotAuthorisedFault, ReportRenderingException, GroupsNotAllowedException {
		try{
			Reports service = getService();
			org.psygrid.data.reporting.definition.dto.RecordReport dtoReport = (org.psygrid.data.reporting.definition.dto.RecordReport)report.toDTO();
			return service.generateDynamicRecordReport(dtoReport, identifier, formatType, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(ServiceException ex){
			throw new RuntimeException(ex);
		}
		catch(RemoteException ex){
			throw new RuntimeException(ex);
		}

	}
	
	public IReport getReport(long reportId, String saml)
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, NotAuthorisedFault{
		try{
			Reports service = getService();
			org.psygrid.data.reporting.definition.dto.Report dtoReport = service.getReport(reportId, saml);
			
			return dtoReport.toHibernate();
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(ServiceException ex){
			throw new RuntimeException(ex);
		}
		catch(RemoteException ex){
			throw new RuntimeException(ex);
		}

	}
	
	public void deleteReport(Long dataSetId,
			Long reportId, String saml) throws ConnectException, SocketTimeoutException,
			RepositoryServiceFault, NotAuthorisedFault {
		try {
			Reports service = getService();
			service.deleteReport(dataSetId, reportId, saml);
		} catch (AxisFault fault) {
			handleAxisFault(fault, LOG);
		} catch (ServiceException ex) {
			throw new RuntimeException(ex);
		} catch (RemoteException ex) {
			throw new RuntimeException(ex);
		}

	}

	public Long saveReport(IReport report, String saml)
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, NotAuthorisedFault{
		try{
			Reports service = getService();
			return service.saveReport(report.toDTO(), saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(ServiceException ex){
			throw new RuntimeException(ex);
		}
		catch(RemoteException ex){
			throw new RuntimeException(ex);
		}

	}
    	
	public List<Group> getGroupsForCodes(Long dsId, List<String> groupCodes, String saml)
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, NotAuthorisedFault{
		try{
			Reports service = getService();
			
			String[] codes = new String[groupCodes.size()];
			for (int i = 0; i <groupCodes.size(); i++) {
				codes[i] = groupCodes.get(i);
			}
			
			org.psygrid.data.model.dto.GroupDTO[] dtoGroups = service.getGroupsForCodes(dsId, codes, saml);
			List<Group> groups = new ArrayList<Group>();
			Map<org.psygrid.data.model.dto.PersistentDTO, Persistent> hRefs = new HashMap<org.psygrid.data.model.dto.PersistentDTO, Persistent>();
			for (org.psygrid.data.model.dto.GroupDTO group: dtoGroups) {
				groups.add(group.toHibernate(hRefs));
			}
			
			return groups;
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(ServiceException ex){
			throw new RuntimeException(ex);
		}
		catch(RemoteException ex){
			throw new RuntimeException(ex);
		}

	}
	
	public List<Document> getDocuments(String projectCode, String saml)
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, NotAuthorisedFault{
		try{
			Reports service = getService();
			
			org.psygrid.data.model.dto.DocumentDTO[] dtoDocuments = service.getDocuments(projectCode, saml);
			List<Document> documents = new ArrayList<Document>();
			Map<org.psygrid.data.model.dto.PersistentDTO, Persistent> hRefs = new HashMap<org.psygrid.data.model.dto.PersistentDTO, Persistent>();
			for (org.psygrid.data.model.dto.DocumentDTO document: dtoDocuments) {
				documents.add(document.toHibernate(hRefs));
			}
			
			return documents;
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(ServiceException ex){
			throw new RuntimeException(ex);
		}
		catch(RemoteException ex){
			throw new RuntimeException(ex);
		}

	}
	
	public MinimalEntry[] getEntries(String projectCode, long documentId, String saml)
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, NotAuthorisedFault{
		try{
			Reports service = getService();			
			return service.getEntries(projectCode, documentId, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(ServiceException ex){
			throw new RuntimeException(ex);
		}
		catch(RemoteException ex){
			throw new RuntimeException(ex);
		}

	}
	
	public byte[] generateMgmtReportById(String projectCode, long id, String saml)
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, NotAuthorisedFault {

		try{
			Reports service = getService();			
			return service.generateMgmtReportById(projectCode, id, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(ServiceException ex){
			throw new RuntimeException(ex);
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}           


	}
	
	private Reports getService() throws ServiceException{
		ReportsServiceLocator locator = new ReportsServiceLocator();
		Reports service = null;
		if ( null == this.url ){
			service = locator.getreports();
		}
		else{
			service = locator.getreports(url);
		}
		return service;
	}
}

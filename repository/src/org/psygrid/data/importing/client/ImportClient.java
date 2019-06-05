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

package org.psygrid.data.importing.client;

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
import org.psygrid.data.importing.ImportData;
import org.psygrid.data.importing.ImportService;
import org.psygrid.data.importing.ImportServiceServiceLocator;
import org.psygrid.data.importing.ImportStatus;
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
import org.psygrid.data.repository.Repository;
import org.psygrid.data.repository.RepositoryServiceFault;
import org.psygrid.data.utils.security.NotAuthorisedFault;

/**
 * Class to act as a layer of abstraction between a Java client
 * and the web services exposed by the import service.
 * 
 * @author Terry Child
 *
 */
public class ImportClient extends org.psygrid.common.AbstractClient {

	private static final Log LOG = LogFactory.getLog(ImportClient.class);
	
	private final ImportService service;

	/**
	 * Default no-arg constructor
	 */
	public ImportClient(){
		service = getService();
	}

	/**
	 * Constructor that accepts a value for the url where the web
	 * service is located.
	 * 
	 * @param url
	 */
	public ImportClient(URL url){
		super(url);
		service = getService();
	}

	public String[] getImportTypes(String projectCode, String saml) throws ConnectException, SocketTimeoutException,RepositoryServiceFault, NotAuthorisedFault {
		try {
			return service.getImportTypes(projectCode, saml);
		} catch (AxisFault fault) {
			handleAxisFault(fault, LOG);
			return null; // Should never be reached
		} catch (RemoteException ex) {
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}		
	}

	public void requestImport(ImportData data, String saml) throws ConnectException, SocketTimeoutException,RemoteException, RepositoryServiceFault, NotAuthorisedFault {
		try {
			service.requestImport(data, saml);
		} catch (AxisFault fault) {
			handleAxisFault(fault, LOG);
		} catch (RemoteException ex) {
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}				
	}
	
	public ImportStatus[] getImportStatuses(String projectCode, String saml) throws ConnectException, SocketTimeoutException,RemoteException, RepositoryServiceFault, NotAuthorisedFault {
		try {
			ImportStatus[] statuses = service.getImportStatuses(projectCode, saml);
			return statuses;
		} catch (AxisFault fault) {
			handleAxisFault(fault, LOG);
			return null; // Should never be reached
		} catch (RemoteException ex) {
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}		
	}
		
	private ImportService getService() {
		ImportServiceServiceLocator locator = new ImportServiceServiceLocator();
		ImportService service = null;
		try{
			if ( this.url == null ){
				service = locator.getimportservice();
			}
			else{
				service = locator.getimportservice(url);
			}
		}
		catch(ServiceException ex){
			//this can only happen if the repository was built with
			//an incorrect URL
			throw new RuntimeException("Repository URL is invalid!", ex);
		}
		return service;
	}
}

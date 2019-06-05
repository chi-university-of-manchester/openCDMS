/*
Copyright (c) 2006-2009, The University of Manchester, UK.

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

package org.psygrid.data.query.client;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.rpc.ServiceException;

import org.apache.axis.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.common.AbstractClient;
import org.psygrid.data.repository.RepositorySoapBindingStub;
import org.psygrid.data.query.IQuery;
import org.psygrid.data.query.QueryService;
import org.psygrid.data.query.QueryServiceServiceLocator;
import org.psygrid.data.repository.RepositoryServiceFault;
import org.psygrid.data.utils.security.NotAuthorisedFault;

/**
 * @author Rob Harper
 *
 */
public class QueryServiceClient extends AbstractClient {

	private static final Log LOG = LogFactory.getLog(QueryServiceClient.class);
	
	private final QueryService service;

	/**
	 * Default no-arg constructor
	 */
	public QueryServiceClient(){
		service = getService();
	}

	public QueryServiceClient(URL url){
		super(url);
		service = getService();
	}

	/**
	 * Constructor that accepts a value for the url where the web
	 * service is located and the timeout for the web service.
	 * 
	 * @param url
	 * @param timeout
	 */
	public QueryServiceClient(URL url, int timeout){
		super(url, timeout);
		service = getService();
	}

	/**
	 * Constructor that accepts a timeout for the web service.
	 * 
	 * @param timeout
	 */
	public QueryServiceClient(int timeout){
		super(timeout);
		service = getService();
	}

	public void saveQuery(IQuery query, String saml) throws ConnectException, SocketTimeoutException, 
	RepositoryServiceFault, NotAuthorisedFault {
		try{
			service.saveQuery(query.toDTO(), saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}           

	}
	
	public List<IQuery> getMyQueries(String project, String saml) throws ConnectException, SocketTimeoutException, 
	RepositoryServiceFault, NotAuthorisedFault {
		try{
			org.psygrid.data.query.dto.Query[] dtoQs = service.getMyQueries(project, saml);
			List<IQuery> queries = new ArrayList<IQuery>();
			for ( int i=0, c=dtoQs.length; i<c; i++ ){
				queries.add(dtoQs[i].toHibernate());
			}
			return queries;
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}           

	}
	
	public IQuery getQuery(Long queryId, String saml) throws ConnectException, SocketTimeoutException, 
	RepositoryServiceFault, NotAuthorisedFault {
		try{
			return service.getQuery(queryId, saml).toHibernate();
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}           

	}
	
	private QueryService getService() {
		QueryServiceServiceLocator locator = new QueryServiceServiceLocator();
		QueryService service = null;
		try{
			if ( null == this.url ){
				service = locator.getqueryservice();
			}
			else{
				service = locator.getqueryservice(url);
			}
		}
		catch(ServiceException ex){
			//this can only happen if the repository was built with
			//an incorrect URL
			throw new RuntimeException("Repository URL is invalid!", ex);
		}
		if ( this.timeout >= 0 ){
			RepositorySoapBindingStub stub  = (RepositorySoapBindingStub)service;
			stub.setTimeout(this.timeout);
		}
		return service;
	}
	
	public Long executeQueryForCount(long queryId, String saml)
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, NotAuthorisedFault {
		try{
			return service.executeQueryForCount(queryId, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}           
	}
	
	public List<String> executeQueryForIdentifiers(long queryId, String saml)
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, NotAuthorisedFault {
		try{
			String[] result = service.executeQueryForIdentifiers(queryId, saml);
			return Arrays.asList(result);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}           
	}

	public List<String> executeQueryForExternalIdentifiers(long queryId, String saml)
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, NotAuthorisedFault {
		try{
			String[] result = service.executeQueryForExternalIdentifiers(queryId, saml);
			return Arrays.asList(result);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}           
	}


}

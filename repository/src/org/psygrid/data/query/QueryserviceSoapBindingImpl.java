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

package org.psygrid.data.query;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.data.query.dto.Query;
import org.psygrid.data.repository.RepositoryServiceFault;
import org.psygrid.data.repository.dao.DAOException;
import org.psygrid.data.repository.dao.RepositoryDAO;
import org.psygrid.data.utils.security.NotAuthorisedFault;
import org.psygrid.logging.AuditLogger;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.PGSecurityInvalidSAMLException;
import org.psygrid.security.PGSecuritySAMLVerificationException;
import org.psygrid.security.RBACAction;
import org.psygrid.security.accesscontrol.AEFGroup;
import org.psygrid.security.accesscontrol.AEFProject;
import org.psygrid.services.SecureSoapBindingImpl;
import org.springframework.context.ApplicationContext;

/**
 * @author Rob Harper
 *
 */
public class QueryserviceSoapBindingImpl extends SecureSoapBindingImpl implements QueryService {

	private QueryService service = null;

	@Override
	protected void onInit() throws ServiceException {
		super.onInit();
		ApplicationContext ctx = getWebApplicationContext();
		service = (QueryService)ctx.getBean("queryService");
	}

	public void saveQuery(Query query, String saml) throws RemoteException, NotAuthorisedFault, RepositoryServiceFault {
		service.saveQuery(query, saml);
	}

	public Query[] getMyQueries(String project, String saml) throws RemoteException, NotAuthorisedFault, RepositoryServiceFault {
		return service.getMyQueries(project, saml);
	}

	public Query getQuery(long queryId, String saml) throws RemoteException,
			NotAuthorisedFault, RepositoryServiceFault {
		return service.getQuery(queryId, saml);
	}

	public long executeQueryForCount(long queryId, String saml)
			throws RemoteException, RepositoryServiceFault, NotAuthorisedFault {
		return service.executeQueryForCount(queryId, saml);
	}

	public String[] executeQueryForIdentifiers(long queryId, String saml)
			throws RemoteException, RepositoryServiceFault, NotAuthorisedFault {
		return service.executeQueryForIdentifiers(queryId, saml);
	}

	public String[] executeQueryForExternalIdentifiers(long queryId, String saml)
			throws RemoteException, RepositoryServiceFault, NotAuthorisedFault {
		return service.executeQueryForExternalIdentifiers(queryId, saml);
	}

}

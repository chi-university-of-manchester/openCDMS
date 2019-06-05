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

package org.psygrid.data.importing;

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
 * Implementation of the ImportService web service
 * 
 * @author Terry Child
 *
 */
public class ImportserviceSoapBindingImpl extends SecureSoapBindingImpl implements ImportService {

	private ImportService service = null;

	@Override
	protected void onInit() throws ServiceException {
		super.onInit();
		ApplicationContext ctx = getWebApplicationContext();
		service = (ImportService)ctx.getBean("importService");
	}

	public String[] getImportTypes(String projectCode, String saml)
			throws RemoteException, RepositoryServiceFault {
		return service.getImportTypes(projectCode, saml);
	}

	public void requestImport(ImportData data, String saml)
			throws RemoteException, RepositoryServiceFault {
		service.requestImport(data, saml);
	}

	public ImportStatus[] getImportStatuses(String projectCode, String saml)
			throws RemoteException, RepositoryServiceFault {
		return service.getImportStatuses(projectCode, saml);
	}
	

}


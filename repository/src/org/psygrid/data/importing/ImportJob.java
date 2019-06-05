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

import javax.servlet.ServletContext;

import org.psygrid.data.importing.model.ImportRequest;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * Job to process import requests.
 * 
 * @author Terry Child
 *
 */
public class ImportJob extends QuartzJobBean {

	private ImportServiceInternal importService = null;

	public void setImportService(ImportServiceInternal importService){
		this.importService=importService;
	}
	
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		// The import is split into three calls so the import request can be set to
		// a status of 'Processing' before the import is run.
		ImportRequest request = importService.nextImportRequest();
		if(request!=null){
			importService.runImport(request);
			importService.updateImportRequest(request);
		}
	}		
	
}

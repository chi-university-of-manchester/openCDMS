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

package org.opencdms.web.modules.export.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.opencdms.web.core.models.ProjectAndGroupsModel;
import org.psygrid.data.export.hibernate.ExportRequest;
import org.psygrid.www.xml.security.core.types.GroupType;


/**
 * @author Rob Harper
 *
 */
public class ExportRequestModel extends ProjectAndGroupsModel implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private ExportDetailsModel details = new ExportDetailsModel();

	public ExportDetailsModel getDetails() {
		return details;
	}

	public void setDetails(ExportDetailsModel details) {
		this.details = details;
	}
	
	public void populateExportRequest(ExportRequest export){
		
		export.setProjectCode(getStudy().getIdCode());
		
		List<String> groups = new ArrayList<String>();
		for ( GroupType gt: getCentres()){
			groups.add(gt.getIdCode());
		}
		export.setGroups(groups);
		
		details.populateExportRequest(export);
	}
}

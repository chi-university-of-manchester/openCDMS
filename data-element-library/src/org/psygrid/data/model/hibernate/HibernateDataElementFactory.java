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
package org.psygrid.data.model.hibernate;

import org.psygrid.data.model.ElementFactory;
import org.psygrid.data.model.IAdminInfo;
import org.psygrid.data.model.IDELQueryObject;
import org.psygrid.data.model.hibernate.AdminInfo;

public class HibernateDataElementFactory implements ElementFactory {

	public IAdminInfo createAdminInfo(DataElementAction action, String description, String registrar,
			String terminologicalReference, boolean elementActive) {
		return new AdminInfo(action, description, elementActive, registrar, terminologicalReference);
	}
	
	public IDELQueryObject createDELQueryManager(String elementType, String searchText, String searchTextCriteria){
		DELQueryObject qObj = new DELQueryObject(elementType, searchText, searchTextCriteria);
		qObj.setNewQuery(true);
		return qObj;
	}
	
	public ElementStatusContainer createElementStatusContainer(DataElementContainer container){
	
		LSID lsid = container.getElementLSIDObject();
		boolean isHeadRevision = container.getHeadRevision();
		DataElementStatus status = container.getStatus();
	
		ElementStatusContainer statusContainer = new ElementStatusContainer(lsid, isHeadRevision, status);
		
		return statusContainer;
	}
	
}

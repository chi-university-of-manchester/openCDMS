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
package org.psygrid.data.model.dto;

import org.psygrid.data.model.hibernate.DataElementStatus;
import org.psygrid.data.model.hibernate.LSIDException;

public class ElementStatusContainer {
	
	private String lsid;
	private String dataElementStatus;
	private boolean isHeadRevision;
	private String headRevisionLSID;
	private String headRevisionStatus;
	
	public ElementStatusContainer(){}

	public String getDataElementStatus() {
		return dataElementStatus;
	}

	public void setDataElementStatus(String dataElementStatus) {
		this.dataElementStatus = dataElementStatus;
	}

	public String getLsid() {
		return lsid;
	}

	public void setLsid(String lsid) {
		this.lsid = lsid;
	}

	
	public boolean getIsHeadRevision(){
		return this.isHeadRevision;
	}
	
	public void setIsHeadRevision(boolean isHeadRevision){
		this.isHeadRevision = isHeadRevision;
	}
	
	public org.psygrid.data.model.hibernate.ElementStatusContainer toHibernate(){
		
		org.psygrid.data.model.hibernate.LSID lsidObj = null;
		org.psygrid.data.model.hibernate.LSID headRevLsidObj = null;
		
		try {
			lsidObj = org.psygrid.data.model.hibernate.LSID.valueOf(lsid);
			headRevLsidObj = org.psygrid.data.model.hibernate.LSID.valueOf(headRevisionLSID);
		} catch (LSIDException e) {
			//Won't happen.
		}
		
		org.psygrid.data.model.hibernate.ElementStatusContainer hibContainer = 
			new org.psygrid.data.model.hibernate.ElementStatusContainer(lsidObj,
					isHeadRevision, DataElementStatus.valueOf(dataElementStatus));
		
		if(!isHeadRevision){
			hibContainer.populateHeadRevisionInfo(headRevLsidObj, DataElementStatus.valueOf(headRevisionStatus));
		}
	
		return hibContainer;
	}

	public String getHeadRevisionLSID() {
		return headRevisionLSID;
	}

	public void setHeadRevisionLSID(String headRevisionLSID) {
		this.headRevisionLSID = headRevisionLSID;
	}

	public String getHeadRevisionStatus() {
		return headRevisionStatus;
	}

	public void setHeadRevisionStatus(String headRevisionStatus) {
		this.headRevisionStatus = headRevisionStatus;
	}

}

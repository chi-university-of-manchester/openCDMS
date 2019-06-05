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

import org.psygrid.data.model.hibernate.LSID;


/**
 * This class contains state information about a particular element.
 * 
 * @author williamvance
 *
 */
public class ElementStatusContainer {
	
	private LSID lsid;
	private DataElementStatus elemStatus;
	private boolean isHeadRevision;
	private LSID headRevisionLSID;
	private DataElementStatus headRevisionStatus;

	
	public LSID getHeadRevisionLSID() {
		return headRevisionLSID;
	}

	public DataElementStatus getHeadRevisionStatus() {
		return headRevisionStatus;
	}
	
	public void populateHeadRevisionInfo(LSID headRevisionLSID, DataElementStatus headRevisionStatus){
		this.headRevisionLSID = headRevisionLSID;
		this.headRevisionStatus = headRevisionStatus;
	}


	public ElementStatusContainer(LSID lsid, boolean isHeadRevision, DataElementStatus elemStatus){
		this.lsid = lsid;
		this.isHeadRevision = isHeadRevision;
		this.elemStatus = elemStatus;
		
		if(this.isHeadRevision){
			headRevisionLSID = lsid;
			headRevisionStatus = elemStatus;
		}
		
	}

	/**
	 * Return the element's status.
	 * @return
	 */
	public DataElementStatus getElemStatus() {
		return elemStatus;
	}

	/**
	 * Get the element's LSID object.
	 * @return
	 */
	public LSID getLsid() {
		return lsid;
	}


	/**
	 * Get the element's revision number.
	 * @return
	 */
	public int getRevisionNumber() {
		return Integer.valueOf(lsid.getRevisionId());
	}

	/**
	 * Return whether the element is at head revision.
	 * @return
	 */
	public boolean getIsHeadRevision() {
		return isHeadRevision;
	}

	
	public org.psygrid.data.model.dto.ElementStatusContainer toDTO(){
		
		org.psygrid.data.model.dto.ElementStatusContainer dtoObj = new org.psygrid.data.model.dto.ElementStatusContainer();
		dtoObj.setDataElementStatus(elemStatus.toString());
		dtoObj.setLsid(getLsid().toString());
		dtoObj.setIsHeadRevision(isHeadRevision);
		dtoObj.setHeadRevisionStatus(headRevisionStatus.toString());
		dtoObj.setHeadRevisionLSID(headRevisionLSID.toString());
		
		return dtoObj;
	}
	
	public boolean isEqual(ElementStatusContainer comparisonContainer){
		
		boolean answer = this.lsid.toString().equals(comparisonContainer.getLsid().toString()) && 
			this.elemStatus == comparisonContainer.getElemStatus();
		
		return answer;
	}
	
}

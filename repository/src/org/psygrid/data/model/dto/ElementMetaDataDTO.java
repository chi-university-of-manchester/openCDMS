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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ElementMetaDataDTO extends org.psygrid.data.model.dto.PersistentDTO {
	
	private Date elementDate; //Saved - Either submission date or revision date - depends on context.
	private String who; //Saved - Either the subitter or revisor - depends on context.
	private String registrar; //organisation that owns the element. Saved off for revision zero only.
	private String elementLSID; //Saved
	private String activityDescription; //Saved - either describes why the item was added, or revision details. Depends on context.
	private String displayName; //Generated, NOT persisted.
	private String description; //Generated, NOT persisted.
	private String elementStatus; //Saved
	private String replacedBy; //Saved, optional
	private String terminologicalRef; //Saved, optional
	private String elementAction; //Saved, mandatory
	private ElementHistoryItemDTO[] historyList; 

	public ElementMetaDataDTO(){}
	
	public String getActivityDescription() {
		return activityDescription;
	}

	public void setActivityDescription(String activityDescription) {
		this.activityDescription = activityDescription;
	}


	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Date getElementDate() {
		return elementDate;
	}

	public void setElementDate(Date elementDate) {
		this.elementDate = elementDate;
	}

	public String getElementLSID() {
		return elementLSID;
	}

	public void setElementLSID(String elementLSID) {
		this.elementLSID = elementLSID;
	}

	public String getEnumElementStatus() {
		return elementStatus;
	}

	public void setEnumElementStatus(String elementStatus) {
		this.elementStatus = elementStatus;
	}

	public ElementHistoryItemDTO[] getHistoryList() {
		return historyList;
	}

	public void setHistoryList(ElementHistoryItemDTO[] historyList) {
		this.historyList = historyList;
	}

	public String getRegistrar() {
		return registrar;
	}

	public void setRegistrar(String registrar) {
		this.registrar = registrar;
	}

	public String getReplacedBy() {
		return replacedBy;
	}

	public void setReplacedBy(String replacedBy) {
		this.replacedBy = replacedBy;
	}

	public String getTerminologicalRef() {
		return terminologicalRef;
	}

	public void setTerminologicalRef(String terminologicalRef) {
		this.terminologicalRef = terminologicalRef;
	}

	public String getWho() {
		return who;
	}

	public void setWho(String who) {
		this.who = who;
	}

	
	public void toHibernate(org.psygrid.data.model.hibernate.ElementMetaData elemMetaData, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs) {
		super.toHibernate(elemMetaData, hRefs);
		
		elemMetaData.setActivityDescription(this.getActivityDescription());
		elemMetaData.setDescription(this.getDescription());
		elemMetaData.setDisplayName(this.getDisplayName());
		elemMetaData.setElementDate(this.getElementDate());
		elemMetaData.setElementLSID(this.getElementLSID());
		elemMetaData.setEnumElementStatus(this.getEnumElementStatus());
		
		//Set the history list.
		List<org.psygrid.data.model.hibernate.ElementHistoryItem> historyItems = new ArrayList<org.psygrid.data.model.hibernate.ElementHistoryItem>();
		final int numHistoryItems = this.historyList.length;
		for(int count = 0; count < numHistoryItems; count ++){
			historyItems.add((org.psygrid.data.model.hibernate.ElementHistoryItem)this.getHistoryList()[count].toHibernate(hRefs));
		}
		elemMetaData.setHistoryList(historyItems);
		
		elemMetaData.setRegistrar(this.getRegistrar());
		elemMetaData.setRegistrar(this.getReplacedBy());
		elemMetaData.setWho(this.getWho());
		elemMetaData.setActionEnum(this.elementAction);
	}

	@Override
	public org.psygrid.data.model.hibernate.Persistent toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs) {
		org.psygrid.data.model.hibernate.ElementMetaData elemMetaData = null;
		if(hRefs.containsKey(this)){
			elemMetaData = (org.psygrid.data.model.hibernate.ElementMetaData)hRefs.get(this);
			return elemMetaData;
		}
		
		if(null == elemMetaData){
			elemMetaData = new org.psygrid.data.model.hibernate.ElementMetaData();
			hRefs.put(this, elemMetaData);
			toHibernate(elemMetaData, hRefs);	
		}
		
		return elemMetaData;
	}

	public String getElementAction() {
		return elementAction;
	}

	public void setElementAction(String elementAction) {
		this.elementAction = elementAction;
	}


}

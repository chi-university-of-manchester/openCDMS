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

import java.util.Date;
import java.util.Map;

import org.psygrid.data.model.hibernate.Persistent;

public class ElementHistoryItemDTO extends org.psygrid.data.model.dto.PersistentDTO{
	
	private String actionTaken;
	private String lsid; //identifier of the item
	private Date historyEventDate;
	private String who; //who created the history event.
	private String description; //description of the history event.
	
	public ElementHistoryItemDTO(){}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getHistoryEventDate() {
		return historyEventDate;
	}
	public void setHistoryEventDate(Date historyEventDate) {
		this.historyEventDate = historyEventDate;
	}

	public String getActionTaken(){
		return this.actionTaken;
	}
	
	public void setActionTaken(String action){
		this.actionTaken = action;
	}
	
	public String getLsid() {
		return lsid;
	}
	public void setLsid(String lsid) {
		this.lsid = lsid;
	}
	public String getWho() {
		return who;
	}
	public void setWho(String who) {
		this.who = who;
	}
	
	
	public void toHibernate(org.psygrid.data.model.hibernate.ElementHistoryItem hibElemHistoryItem, Map<org.psygrid.data.model.dto.PersistentDTO, Persistent> hRefs) {
		super.toHibernate(hibElemHistoryItem, hRefs);
		hibElemHistoryItem.setDescription(this.getDescription());		
		hibElemHistoryItem.setEnumActionTaken(this.getActionTaken());
		hibElemHistoryItem.setHistoryEventDate(this.getHistoryEventDate());
		hibElemHistoryItem.setLsid(this.getLsid());
		hibElemHistoryItem.setWho(this.getWho());
	}
	
	@Override
	public Persistent toHibernate(Map<org.psygrid.data.model.dto.PersistentDTO, Persistent> hRefs) {
		org.psygrid.data.model.hibernate.ElementHistoryItem elemHistoryItem = null;
		if(hRefs.containsKey(this)){
			elemHistoryItem = (org.psygrid.data.model.hibernate.ElementHistoryItem)hRefs.get(this);
			return elemHistoryItem;
		}
		
		if(null == elemHistoryItem){
			elemHistoryItem = new org.psygrid.data.model.hibernate.ElementHistoryItem();
			hRefs.put(this, elemHistoryItem);
			toHibernate(elemHistoryItem, hRefs);	
		}
		
		return elemHistoryItem;
	}

}

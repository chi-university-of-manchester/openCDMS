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

import java.util.Date;
import java.util.Map;

import org.psygrid.data.model.dto.PersistentDTO;

public class ElementHistoryItem extends org.psygrid.data.model.hibernate.Persistent{
		
	private DataElementAction actionTaken;
	private String lsid; //identifier of the item
	private Date historyEventDate;
	private String who; //who created the history event.
	private String description; //description of the history event.
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
	
	public DataElementAction getActionTaken(){
		return actionTaken;
	}
	
	public void setActiontaken(DataElementAction action){
		this.actionTaken = action;
	}
	
	public String getEnumActionTaken(){
		if(actionTaken == null){
			return null;
		}else{
			return actionTaken.toString();
		}
	}
	
	public void setEnumActionTaken(String action){
		if(action == null){
			this.actionTaken = null;
		}else{
			this.actionTaken = DataElementAction.valueOf(action);
		}
	}
	
	public void toDTO(org.psygrid.data.model.dto.ElementHistoryItemDTO dtoElemHistItem, Map<org.psygrid.data.model.hibernate.Persistent, PersistentDTO> dtoRefs, RetrieveDepth depth) {
		super.toDTO(dtoElemHistItem, dtoRefs, depth);
		dtoElemHistItem.setDescription(this.getDescription());
		dtoElemHistItem.setHistoryEventDate(this.getHistoryEventDate());
		dtoElemHistItem.setLsid(this.getLsid());
		dtoElemHistItem.setWho(this.getWho());
		dtoElemHistItem.setActionTaken(this.getEnumActionTaken());
	}
	
	@Override
	public PersistentDTO toDTO(Map<org.psygrid.data.model.hibernate.Persistent, PersistentDTO> dtoRefs, RetrieveDepth depth) {
		org.psygrid.data.model.dto.ElementHistoryItemDTO elemHistoryItem = null;
		//check to see if we're already in the map.
	       if ( dtoRefs.containsKey(this)){
	    	   elemHistoryItem = (org.psygrid.data.model.dto.ElementHistoryItemDTO)dtoRefs.get(this);
	       }
	       if( null == elemHistoryItem){
	    	   elemHistoryItem = new org.psygrid.data.model.dto.ElementHistoryItemDTO();
	    	   dtoRefs.put(this, elemHistoryItem);
	    	   toDTO(elemHistoryItem, dtoRefs, depth);
	       }
			 
			return elemHistoryItem;
	}

}

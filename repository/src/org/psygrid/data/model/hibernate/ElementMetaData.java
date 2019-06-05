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


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.psygrid.data.model.dto.PersistentDTO;

/**
 @DEL_REP_METADATA_TABLE_TAG@
 */
public class ElementMetaData extends org.psygrid.data.model.hibernate.Persistent {
	
	public enum Status{
		activated,
		deactivated
	}
	
	private Date elementDate; //Saved - Either submission date or revision date - depends on context.
	private String who; //Saved - Either the subitter or revisor - depends on context.
	private String registrar; //organisation that owns the element. Saved off for revision zero only.
	private String elementLSID; //Saved
	private String activityDescription; //Saved - either describes why the item was added, or revision details. Depends on context.
	private String displayName; //Generated, NOT persisted.
	private String description; //Generated, NOT persisted.
	private Status elementStatus; //Saved
	private String replacedBy; //Saved, optional
	private String terminologicalRef; //Saved, optional
	private DataElementAction action; //Saved, mandatory.
	private List<ElementHistoryItem> historyList = new ArrayList<ElementHistoryItem>();
	
	//DateElement added and revsionDate could almost be the same field, and the meaning could be determined
	//according to whether the element is revision zero or not. The field could then simply be called 'date'.
	
	//Element submitter and elementModifier are similar to the situation above. There needs to be only one field,
	//and the exact meaning can be known according to whether the current elment is revision zero or not.
	
	//Display name and description should be obtainable from the element itself, rather than storing redundant
	//information. Therefore, these items will not be persisted to the database.

	/**
	 * @DEL_REP_METADATA_ACTIVITY_DESCRIPTION_TAG@
	 */
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

	/**
	 * @DEL_REP_METADATA_DATE_TAG@
	 */
	public Date getElementDate() {
		return elementDate;
	}

	public void setElementDate(Date elementDate) {
		this.elementDate = elementDate;
	}

	/**
	 * @DEL_REP_METADATA_LSID_TAG@
	 */
	public String getElementLSID() {
		return elementLSID;
	}

	public void setElementLSID(String elementLSID) {
		this.elementLSID = elementLSID;
	}

	public Status getElementStatus() {
		return elementStatus;
	}

	public void setElementStatus(Status elementStatus) {
		this.elementStatus = elementStatus;
	}
	
	/**
	 * @DEL_REP_METADATA_STATUS_TAG@
	 */
	public String getEnumElementStatus(){
		if(elementStatus == null){
			return null;
		}else{
			return elementStatus.toString();
		}
		
	}
	
	public void setEnumElementStatus(String elementStatus){
		if(elementStatus == null){
			this.elementStatus = null;
		}else{
			this.elementStatus = Status.valueOf(elementStatus);
		}
	}

	public List<ElementHistoryItem> getHistoryList() {
		return historyList;
	}

	public void setHistoryList(List<ElementHistoryItem> historyList) {
		this.historyList = historyList;
	}

	/**
	 * @DEL_REP_METADATA_REGISTRAR_TAG@
	 */
	public String getRegistrar() {
		return registrar;
	}

	public void setRegistrar(String registrar) {
		this.registrar = registrar;
	}

	/**
	 * @DEL_REP_METADATA_REPLACEDBY_TAG@
	 */
	public String getReplacedBy() {
		return replacedBy;
	}

	public void setReplacedBy(String replacedBy) {
		this.replacedBy = replacedBy;
	}

	/**
	 * @DEL_REP_METADATA_TERMINOLOGYREF_TAG@
	 */
	public String getTerminologicalRef() {
		return terminologicalRef;
	}

	public void setTerminologicalRef(String terminologicalRef) {
		this.terminologicalRef = terminologicalRef;
	}

	/**
	 * @DEL_REP_METADATA_WHO_TAG@
	 */
	public String getWho() {
		return who;
	}

	public void setWho(String who) {
		this.who = who;
	}
	
	public org.psygrid.data.model.dto.ElementMetaDataDTO toDTO(){
		 Map<org.psygrid.data.model.hibernate.Persistent, PersistentDTO> dtoRefs = new HashMap<org.psygrid.data.model.hibernate.Persistent, PersistentDTO>();
		    return (org.psygrid.data.model.dto.ElementMetaDataDTO)toDTO(dtoRefs, RetrieveDepth.DS_COMPLETE);
	}
	
	@Override
	public PersistentDTO toDTO(Map<org.psygrid.data.model.hibernate.Persistent, PersistentDTO> dtoRefs, RetrieveDepth depth) {
		org.psygrid.data.model.dto.ElementMetaDataDTO elemMetaData = null;
		//check to see if we're already in the map.
	       if ( dtoRefs.containsKey(this)){
	    	   elemMetaData = (org.psygrid.data.model.dto.ElementMetaDataDTO)dtoRefs.get(this);
	       }
	       if( null == elemMetaData){
	    	   elemMetaData = new org.psygrid.data.model.dto.ElementMetaDataDTO();
	    	   dtoRefs.put(this, elemMetaData);
	    	   toDTO(elemMetaData, dtoRefs, depth);
	       }
			 
			return elemMetaData;
	}

	public void toDTO(org.psygrid.data.model.dto.ElementMetaDataDTO elemMetaData, Map<org.psygrid.data.model.hibernate.Persistent, PersistentDTO> dtoRefs, RetrieveDepth depth) {
		super.toDTO(elemMetaData, dtoRefs, depth);
		elemMetaData.setActivityDescription(this.getActivityDescription());
		elemMetaData.setDescription(this.getDescription());
		elemMetaData.setDisplayName(this.getDisplayName());
		elemMetaData.setElementDate(this.getElementDate());
		elemMetaData.setElementLSID(this.getElementLSID());
		elemMetaData.setEnumElementStatus(this.getEnumElementStatus());
		
		List<org.psygrid.data.model.hibernate.ElementHistoryItem> historyItems = this.getHistoryList();
		org.psygrid.data.model.dto.ElementHistoryItemDTO[] dtoHistoryItems = new org.psygrid.data.model.dto.ElementHistoryItemDTO[historyItems.size()];
		for(int count = 0; count < historyItems.size(); count++){
			org.psygrid.data.model.hibernate.ElementHistoryItem hibItem = historyItems.get(count);
			dtoHistoryItems[count] = (org.psygrid.data.model.dto.ElementHistoryItemDTO)hibItem.toDTO(dtoRefs, depth);
		}
		
		elemMetaData.setHistoryList(dtoHistoryItems);
		
		elemMetaData.setRegistrar(this.getRegistrar());
		elemMetaData.setTerminologicalRef(this.getTerminologicalRef());
		elemMetaData.setWho(this.getWho());
		
		if(action != null){
			elemMetaData.setElementAction(action.toString());
		}
	}

	public DataElementAction getAction() {
		return action;
	}

	public void setAction(DataElementAction action) {
		this.action = action;
	}

	public void setActionEnum(String elementAction) {
		
		if(elementAction == null){
			this.action = null;
		}else{
			this.action = DataElementAction.valueOf(elementAction);
		}
		
	}

}

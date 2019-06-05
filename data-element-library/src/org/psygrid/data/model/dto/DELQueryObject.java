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
import java.util.List;

import org.psygrid.data.model.hibernate.DataElementStatus;

public class DELQueryObject extends Object{
	
	private int granularity;
	private int totalNumResults;
	private boolean isNewQuery;
	private int returnedElementCount;
	
	//BEGIN Search criteria properties.
	private String elementType;
	private String searchCriteria;
	private String searchType;
	
	private List<String> docFilterLSIDs; //Only look for elements contained in THESE docs.
	private List<String> authorityFilterLSIDs; //Only look for elements belonging to THESE authorities;
	private List<String> statusExclusions;
	
	private boolean searchLatestRevisionOnly; //search only latest revision? True is default.
	
	private boolean searchPendingElements; //get pending elements info? Default is false. Only a possiblity for authors, curators.
	
	//END Search criteria properties.
	
	private List<String> matchingLSIDs = null;
	private List<DataElementContainerDTO> returnedElements = null;
	
	/**
	 * This public, no-arg constructor is required for wsdl-to-java. It should not be used otherwise.
	 *
	 */
	public DELQueryObject(){
		
	}
	
	public DELQueryObject(String elementType, String searchCriteria, String searchType){
		matchingLSIDs = new ArrayList<String>();
		returnedElements = new ArrayList<DataElementContainerDTO>();
		this.elementType = elementType;
		this.searchCriteria = searchCriteria;
		this.searchType = searchType;
		this.authorityFilterLSIDs = new ArrayList<String>();
		this.statusExclusions = new ArrayList<String>();
		this.docFilterLSIDs = new ArrayList<String>();
		this.searchLatestRevisionOnly = true;
		this.searchPendingElements = false;
	}

	public int getGranularity() {
		return granularity;
	}

	public void setGranularity(int granularity) {
		this.granularity = granularity;
	}
	
	public String fetchusImmediatusThyneMatchingLSIDAndRemove(){
		
		if(matchingLSIDs != null && matchingLSIDs.size() > 0){
			String lsid = matchingLSIDs.get(0);
			matchingLSIDs.remove(0);
			return lsid;
		}else{
			return null;
		}
	}

	public List<String> getMatchingLSIDs() {
		return matchingLSIDs;
	}

	public void setMatchingLSIDs(List<String> matchingLSIDs) {
		this.matchingLSIDs = matchingLSIDs;
	}

	public int getTotalNumResults() {
		return totalNumResults;
	}

	public void setTotalNumResults(int totalNumResults) {
		this.totalNumResults = totalNumResults;
	}

	public List<DataElementContainerDTO> getReturnedElements() {
		return returnedElements;
	}

	public void setReturnedElements(List<DataElementContainerDTO> returnedElements) {
		this.returnedElements = returnedElements;
		this.returnedElementCount = returnedElements.size();
	}
	
	public boolean isNewQuery() {
		return isNewQuery;
	}

	public void setNewQuery(boolean isNewQuery) {
		this.isNewQuery = isNewQuery;
	}
	
	public String getElementType() {
		return elementType;
	}

	public String getSearchCriteria() {
		return searchCriteria;
	}

	public String getSearchType() {
		return searchType;
	}
	
	/**
	 * This method adds an element to the returnedElements array.
	 * It ensures that the element's lsid is then removed from the matchingLSIDs array.
	 * @param elem
	 */
	public void addElement(DataElementContainerDTO elem, boolean removeMatchingLSIDs){
		returnedElements.add(elem);
		
		if(removeMatchingLSIDs){
			this.returnedElementCount = returnedElements.size();
			String lsid = elem.toHibernate().getElementLSID();
			
			int matchingLSIDCount = matchingLSIDs.size();
			for(int i = 0; i < matchingLSIDCount; i++){
				if(matchingLSIDs.get(i).equals(lsid)){
					matchingLSIDs.remove(i);
					break;
				}
			}
		}
	}
	
	public org.psygrid.data.model.hibernate.DELQueryObject toHibernate(){
		org.psygrid.data.model.hibernate.DELQueryObject hibernateObject = new org.psygrid.data.model.hibernate.DELQueryObject(elementType, searchCriteria, searchType);
		hibernateObject.setGranularity(granularity);
		hibernateObject.setTotalNumResults(totalNumResults);
		hibernateObject.setMatchingLSIDs(matchingLSIDs); 
		hibernateObject.setNewQuery(isNewQuery);
		hibernateObject.setReturnedElementCount(returnedElementCount);
		
		int numReturnedElements = this.returnedElements.size();
		List<org.psygrid.data.model.hibernate.DataElementContainer> returnedDTOElements = new ArrayList<org.psygrid.data.model.hibernate.DataElementContainer>();
		
		for(int i = 0; i < numReturnedElements; i++){
			returnedDTOElements.add(returnedElements.get(i).toHibernate());
		}	
		
		hibernateObject.setReturnedElements(returnedDTOElements);
		
		List<DataElementStatus> statusExclusionsList = new ArrayList<DataElementStatus>();
		if(statusExclusions != null && statusExclusions.size() > 0){
			for(String str : statusExclusions){
				statusExclusionsList.add(DataElementStatus.valueOf(str));
			}
		}
		
		hibernateObject.populateAdvancedSearchCriteria(docFilterLSIDs, authorityFilterLSIDs, statusExclusionsList, searchLatestRevisionOnly);
		
		return hibernateObject;
	}

	public void setElementType(String elementType) {
		this.elementType = elementType;
	}

	public void setSearchCriteria(String searchCriteria) {
		this.searchCriteria = searchCriteria;
	}

	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	/**
	 * Returns the number of elements returned from the last server request.
	 */
	public int getReturnedElementCount() {
		return returnedElementCount;
	}

	/**
	 * Sets the number of elements returned from the last server request.
	 * @param returnedElementCount
	 */
	public void setReturnedElementCount(int returnedElementCount) {
		this.returnedElementCount = returnedElementCount;
	}

	public List<String> getAuthorityFilterLSIDs() {
		return authorityFilterLSIDs;
	}

	public void setAuthorityFilterLSIDs(List<String> authorityFilterLSIDs) {
		this.authorityFilterLSIDs = authorityFilterLSIDs;
	}

	public List<String> getDocFilterLSIDs() {
		return docFilterLSIDs;
	}

	public void setDocFilterLSIDs(List<String> docFilterLSIDs) {
		this.docFilterLSIDs = docFilterLSIDs;
	}

	public boolean getSearchLatestRevisionOnly() {
		return searchLatestRevisionOnly;
	}

	public void setSearchLatestRevisionOnly(boolean searchLatestRevisionOnly) {
		this.searchLatestRevisionOnly = searchLatestRevisionOnly;
	}

	public boolean getSearchPendingElements() {
		return searchPendingElements;
	}

	public void setSearchPendingElements(boolean searchPendingElements) {
		this.searchPendingElements = searchPendingElements;
	}

	public List<String> getStatusExclusions() {
		return statusExclusions;
	}

	public void setStatusExclusions(List<String> statusExclusions) {
		this.statusExclusions = statusExclusions;
	}
}

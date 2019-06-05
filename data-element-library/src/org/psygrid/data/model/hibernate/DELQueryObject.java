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
import java.util.List;

import org.psygrid.data.model.IDELQueryObject;

/**
 * This class manages large queries with results greater than 50 items.
 * It contains up to 50 actual result objects, and the remainder of items are identified
 * in an array of LSIDs only.
 * 
 * The client SHOULD clear the result object from the array before re-submitting the
 * management object to the server to return the next 50 results.
 * 
 * The server will then get the next 50 object identified by the lsid array, and return the 
 * results. By doing this iteratively, the client can retrieve all the results without any
 * danger of running out of memory.
 * 
 * @author williamvance
 *
 */
public class DELQueryObject implements IDELQueryObject{

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
	private List<DataElementStatus> statusExclusions; //Element statuses to exclude in the returned search
	
	private boolean searchLatestRevisionOnly; //search only latest revision? True is default.
	//END Search criteria properties.
	
	private List<String> matchingLSIDs = null;
	private List<DataElementContainer> returnedElements = null;
		
	/**
	 * Always constructed by the del server, never the client.
	 * @param elementType - the type of element (e.g. Document, NumericEntry, DerivedEntry) to search for.
	 * @param searchCriteria - text-based criteria, to match element name and/or description
	 * @param searchType - e.g. starts with, ends with, contains
	 */
	public DELQueryObject(String elementType, String searchCriteria, String searchType){
		granularity = 50;
		totalNumResults = 0;
		matchingLSIDs = new ArrayList<String>();
		returnedElements = new ArrayList<DataElementContainer>();
		this.elementType = elementType; //The type of element (e.g. BasicEntry, DerivedEntry, etc).
		this.searchCriteria = searchCriteria; //The string to match on name and/or description
		this.searchType = searchType; //e.g. 'contains', 'begins with', 'ends with', etc.
		searchLatestRevisionOnly = true;
		docFilterLSIDs = new ArrayList<String>();
		authorityFilterLSIDs = new ArrayList<String>();
		statusExclusions = new ArrayList<DataElementStatus>();
	}
	
	/**
	 * This is meant to be called immediately after the constructor in the event that the client
	 * has specified advanced search filters. The first two parameters can either be null or zero-length
	 * lists, if not part of the advanced filter.
	 * 
	 * @param docFilterLSIDs
	 * @param authorityFilterLSIDs
	 * @param searchLatestRevisionOnly
	 */
	public void populateAdvancedSearchCriteria(List<String> docFilterLSIDs, List<String> authorityFilterLSIDs, List<DataElementStatus> statusExclusions, boolean searchLatestRevisionOnly){
		this.docFilterLSIDs = docFilterLSIDs;
		this.authorityFilterLSIDs = authorityFilterLSIDs;
		this.searchLatestRevisionOnly = searchLatestRevisionOnly;
		this.statusExclusions = statusExclusions;
	}
	
	/**
	 * This is meant be called immediately after the constructor in the event the client has specified
	 * that there are certain element status about which they don't want info.
	 * 
	 * If nothing is specified here, then the search will return all of the statuses that the user's
	 * role(s) allows them to see anyway. This list only helps to narrow down a search for users.
	 * 
	 * @param status
	 */
	public void addElementStatusExclusion(DataElementStatus status){
		boolean statusAlreadyInList = false;
		
		for(DataElementStatus stat:statusExclusions){
			if(status == stat){
				statusAlreadyInList = true;
				break;
			}
		}
		
		if(!statusAlreadyInList){
			statusExclusions.add(status);
		}
	}
	
	public List<DataElementStatus> getElementStatusExclusions(){
		return this.statusExclusions;
	}

	/**
	 * Returns the number of total results of the query. This is not settable through the client interface.
	 * @return
	 */
	public int getTotalResults() {
		return totalNumResults;
	}

	/**
	 * Used to set the number of total results yielded by the query.
	 * @param totalResults
	 */
	public void setTotalResults(int totalResults) {
		this.totalNumResults = totalResults;
	}
	
	public void setReturnedElements(List<DataElementContainer> returnedElements) {
		this.returnedElements = returnedElements;
		this.returnedElementCount = returnedElements.size();
	}


	/**
	 * This returns a collection of elements. 
	 * IMPORTANT: This can be called once and only once because the returnedElements array gets cleared.
	 */
	public List<DataElementContainer> getReturnedElements() {
		List<DataElementContainer> elemList = new ArrayList<DataElementContainer>(returnedElements);
		returnedElements.clear();
		return elemList;
	}
	
	
	/**
	 * This method adds an element to the returnedElements array.
	 * It ensures that the element's lsid is then removed from the matchingLSIDs array.
	 * @param elem
	 */
	public void addElement(DataElementContainer elem){
		returnedElements.add(elem);
		String lsid = elem.getElementLSID();
		this.returnedElementCount = returnedElements.size();
		
		int matchingLSIDCount = matchingLSIDs.size();
		for(int i = 0; i < matchingLSIDCount; i++){
			if(matchingLSIDs.get(i).equals(lsid)){
				matchingLSIDs.remove(i);
				break;
			}
		}
	}

	/**
	 * Returns the granularity (or number) of elements returned each time from the server.
	 * @return
	 */
	public int getGranularity() {
		return granularity;
	}

	/**
	 * Sets the granularity (or number) of elements that should be returned each time from the server.
	 * A default value is set in the class constructor. 
	 * @param granularity
	 */
	public void setGranularity(int granularity) {
		this.granularity = granularity;
	}

	/**
	 * Returns the number of Results that have yet to be retrieved from the server.
	 * @return
	 */
	public int getRemainingResults() {
		return matchingLSIDs.size();
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
	
	/**
	 * Returns whether this object is a new query. This is used by the server to determine
	 * whether to execute the query contained in the object, or to load up the next bunch
	 * of elements specified in the lsid array.
	 * 
	 * @return - whether or not this is a new query.
	 */
	public boolean isNewQuery() {
		return isNewQuery;
	}

	/**
	 * Sets whether the query object represents a new query. 
	 * @param isNewQuery
	 */
	public void setNewQuery(boolean isNewQuery) {
		this.isNewQuery = isNewQuery;
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

	public org.psygrid.data.model.dto.DELQueryObject toDTO(){
		
		org.psygrid.data.model.dto.DELQueryObject dtoObj = new org.psygrid.data.model.dto.DELQueryObject(elementType, searchCriteria, searchType);
		dtoObj.setGranularity(granularity);
		dtoObj.setTotalNumResults(totalNumResults);
		dtoObj.setMatchingLSIDs(matchingLSIDs);
		dtoObj.setNewQuery(isNewQuery);
		dtoObj.setReturnedElementCount(returnedElementCount);
		
		int numReturnedElements = this.returnedElements.size();
		List<org.psygrid.data.model.dto.DataElementContainerDTO> returnedDTOElements = new ArrayList<org.psygrid.data.model.dto.DataElementContainerDTO>();
		
		for(int i = 0; i < numReturnedElements; i++){
			org.psygrid.data.model.dto.DataElementContainerDTO element = returnedElements.get(i).toDTO();
			returnedDTOElements.add(element);
		}
		
		dtoObj.setReturnedElements(returnedDTOElements);
		
		if(this.authorityFilterLSIDs!=null && this.authorityFilterLSIDs.size() > 0){
			dtoObj.setAuthorityFilterLSIDs(authorityFilterLSIDs);
		}
		
		if(this.docFilterLSIDs != null && this.docFilterLSIDs.size() > 0){
			dtoObj.setDocFilterLSIDs(docFilterLSIDs);
		}
		
		if(this.statusExclusions != null && this.statusExclusions.size() > 0){
			List<String> statusExclusionStrings = new ArrayList<String>();
			for(DataElementStatus status : statusExclusions){
				statusExclusionStrings.add(status.toString());
			}
			dtoObj.setStatusExclusions(statusExclusionStrings);
		}
		
		dtoObj.setSearchLatestRevisionOnly(searchLatestRevisionOnly);
		
		return dtoObj;
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
		
}

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

package org.psygrid.web.forms;

import org.psygrid.data.model.dto.extra.DocInstChangeHistoryResult;
import org.psygrid.data.model.dto.extra.RecordChangeHistoryResult;
import org.psygrid.data.model.dto.extra.SearchRecordChangeHistoryResult;


/**
 * @author Rob Harper
 *
 */
public class Audit {

	private String project;
	
	private String projectText;
	
	private String startDate;
	
	private String endDate;
	
	private String user;
	
	private String identifier;
	
	private Long recordHistoryId;
	
	private Long documentHistoryId;
	
	private SearchRecordChangeHistoryResult searchRecordChangeHistoryResult;
	
	private RecordChangeHistoryResult recordChangeHistoryItem;
	
	private DocInstChangeHistoryResult[] searchDocInstChangeHistoryResults;
	
	private DocInstChangeHistoryResult docInstChangeHistoryItem;
		
	private int startIndex;
	
	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getProjectText() {
		return projectText;
	}

	public void setProjectText(String projectText) {
		this.projectText = projectText;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public Long getRecordHistoryId() {
		return recordHistoryId;
	}

	public void setRecordHistoryId(Long recordHistoryId) {
		this.recordHistoryId = recordHistoryId;
	}

	public SearchRecordChangeHistoryResult getSearchRecordChangeHistoryResult() {
		return searchRecordChangeHistoryResult;
	}

	public void setSearchRecordChangeHistoryResult(
			SearchRecordChangeHistoryResult searchRecordChangeHistoryResult) {
		this.searchRecordChangeHistoryResult = searchRecordChangeHistoryResult;
	}

	public RecordChangeHistoryResult getRecordChangeHistoryItem() {
		return recordChangeHistoryItem;
	}

	public void setRecordChangeHistoryItem(
			RecordChangeHistoryResult recordChangeHistoryItem) {
		this.recordChangeHistoryItem = recordChangeHistoryItem;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public Long getDocumentHistoryId() {
		return documentHistoryId;
	}

	public void setDocumentHistoryId(Long documentHistoryId) {
		this.documentHistoryId = documentHistoryId;
	}

	public DocInstChangeHistoryResult[] getSearchDocInstChangeHistoryResults() {
		return searchDocInstChangeHistoryResults;
	}

	public void setSearchDocInstChangeHistoryResults(
			DocInstChangeHistoryResult[] searchDocInstChangeHistoryResults) {
		this.searchDocInstChangeHistoryResults = searchDocInstChangeHistoryResults;
	}

	public DocInstChangeHistoryResult getDocInstChangeHistoryItem() {
		return docInstChangeHistoryItem;
	}

	public void setDocInstChangeHistoryItem(
			DocInstChangeHistoryResult docInstChangeHistoryItem) {
		this.docInstChangeHistoryItem = docInstChangeHistoryItem;
	}	
	
}

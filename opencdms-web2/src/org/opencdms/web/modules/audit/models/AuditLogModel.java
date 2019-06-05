/*
Copyright (c) 2006-2009, The University of Manchester, UK.

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

package org.opencdms.web.modules.audit.models;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import org.psygrid.data.model.dto.extra.DocInstChangeHistoryResult;
import org.psygrid.data.model.dto.extra.ProvenanceForChangeResult;
import org.psygrid.data.model.dto.extra.RecordChangeHistoryResult;
import org.psygrid.data.model.dto.extra.SearchRecordChangeHistoryResult;
import org.psygrid.www.xml.security.core.types.ProjectType;

/**
 * @author Rob Harper
 *
 */
public class AuditLogModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private ProjectType study;
	private Date startDate;
	private Date endDate;
	private User user;
	private String participant;
	private int startIndex = 0;
	
	private SearchRecordChangeHistoryResult searchResult;
	
	private RecordChangeHistoryResult rchResult;
	
	private DocInstChangeHistoryResult[] docResult;
	
	private DocInstChangeHistoryResult selectedDocResult;
	
	private ProvenanceForChangeResult[] provResult;

	// Maps openCDMS identifiers to display identifiers e.g. external identifiers.
	private Map<String,String> identifierMap;
	
	public ProjectType getStudy() {
		return study;
	}
	public void setStudy(ProjectType study) {
		this.study = study;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getParticipant() {
		return participant;
	}
	public void setParticipant(String participant) {
		this.participant = participant;
	}
	
	public int getStartIndex() {
		return startIndex;
	}
	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}
	public SearchRecordChangeHistoryResult getSearchResult() {
		return searchResult;
	}
	public void setSearchResult(SearchRecordChangeHistoryResult searchResult) {
		this.searchResult = searchResult;
	}
	
	public static class User implements Serializable {
		
		private static final long serialVersionUID = 1L;

		private String dn;
		private String name;
		
		public User(){
			super();
		}
		
		public User(String dn){
			this.dn = dn;
			this.name = dn.substring(dn.indexOf("=")+1, dn.indexOf(","));
		}
		
		public String getDn() {
			return dn;
		}
		public void setDn(String dn) {
			this.dn = dn;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
	}

	public RecordChangeHistoryResult getRchResult() {
		return rchResult;
	}
	public void setRchResult(RecordChangeHistoryResult rchResult) {
		this.rchResult = rchResult;
	}
	public DocInstChangeHistoryResult[] getDocResult() {
		return docResult;
	}
	public void setDocResult(DocInstChangeHistoryResult[] docResult) {
		this.docResult = docResult;
	}
	public DocInstChangeHistoryResult getSelectedDocResult() {
		return selectedDocResult;
	}
	public void setSelectedDocResult(DocInstChangeHistoryResult selectedDocResult) {
		this.selectedDocResult = selectedDocResult;
	}
	public ProvenanceForChangeResult[] getProvResult() {
		return provResult;
	}
	public void setProvResult(ProvenanceForChangeResult[] provResult) {
		this.provResult = provResult;
	}
	public void setIdentifierMap(Map<String,String> identifierMap) {
		this.identifierMap = identifierMap;
	}
	public Map<String,String> getIdentifierMap() {
		return identifierMap;
	}

}

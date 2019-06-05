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

package org.psygrid.data.model.dto.extra;

import java.io.Serializable;
import java.util.Date;

/**
 * Class to hold a single result from a call to {@link RepositoryDAO#searchRecordChangeHistory}
 * 
 * @author Rob Harper
 *
 */
public class RecordChangeHistoryResult implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long recordId;
	
	private String identifier;
	
	private Long historyId;
	
	private String user;
	
	private Date when;
	
	private Date whenSystem;
	
	private String action;

	public RecordChangeHistoryResult(){}
	
	public RecordChangeHistoryResult(Long recordId, String identifier, Long historyId, String user, Date when, Date whenSystem, String action){
		this.recordId = recordId;
		this.identifier = identifier;
		this.historyId = historyId;
		this.user = user;
		this.when = when;
		this.whenSystem = whenSystem;
		this.action = action;
	}

	public Long getRecordId() {
		return recordId;
	}

	public void setRecordId(Long recordId) {
		this.recordId = recordId;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public Long getHistoryId() {
		return historyId;
	}

	public void setHistoryId(Long historyId) {
		this.historyId = historyId;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Date getWhen() {
		return when;
	}

	public void setWhen(Date when) {
		this.when = when;
	}

	public Date getWhenSystem() {
		return whenSystem;
	}

	public void setWhenSystem(Date whenSystem) {
		this.whenSystem = whenSystem;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
	
}

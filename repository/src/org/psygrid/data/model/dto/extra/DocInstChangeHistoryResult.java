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
 * Class to hold a single result from a call to {@link RepositoryDAO#searchDocInstChangeHistory}
 * 
 * @author Rob Harper
 *
 */
public class DocInstChangeHistoryResult implements Serializable  {

	private static final long serialVersionUID = 1L;

	private Long docInstId;
	
	private String displayText;
	
	private Long historyId;
	
	private String user;
	
	private Date when;
	
	private Date whenSystem;
	
	private String action;

	public DocInstChangeHistoryResult() {
		super();
	}

	public DocInstChangeHistoryResult(Long docInstId, String displayText,
			Long historyId, String user, Date when, Date whenSystem, String action) {
		super();
		this.docInstId = docInstId;
		this.displayText = displayText;
		this.historyId = historyId;
		this.user = user;
		this.when = when;
		this.whenSystem = whenSystem;
		this.action = action;
	}

	public Long getDocInstId() {
		return docInstId;
	}

	public void setDocInstId(Long docInstId) {
		this.docInstId = docInstId;
	}

	public String getDisplayText() {
		return displayText;
	}

	public void setDisplayText(String displayText) {
		this.displayText = displayText;
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

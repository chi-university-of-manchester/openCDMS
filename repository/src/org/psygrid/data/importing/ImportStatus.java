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


package org.psygrid.data.importing;

import java.util.Date;

/**
 * Status information for an import request.
 * 
 * @author Terry Child
 */
public final class ImportStatus {
	
    /**
	 * Unique request identifier.
	 */
	private Long id;
	
	/**
	 * The project code of the project.
	 */
	private String projectCode;

	/**
	 * The DN of the requester.
	 */
	private String user;
	
	/**
	 * The date/time when the request was made.
	 */
	private Date requestDate;
	
	/**
	 * remoteFilePath
	 */
	private String remoteFilePath;

	/**
	 * If True then the request is required to be processed 
	 * immediately. Otherwise it will be queued for scheduled execution.
	 */
	private boolean immediate;
	
	/**
	 * The status of the request.
	 */
	private String status;

	/**
	 * The line currently being processed. 
	 */
	private int currentLine;

	/**
	 * The date/time when the request was completed.
	 */
	private Date completedDate;
						
	/**
	 * Default no-arg constructor.
	 */
	public ImportStatus(){}

	/**
	 * @param id
	 * @param projectCode
	 * @param user
	 * @param requestDate
	 * @param remoteFilePath
	 * @param filePath
	 * @param md5Hash
	 * @param immediate
	 * @param status
	 * @param currentLine
	 * @param completedDate
	 */
	public ImportStatus(Long id, String projectCode, String user,
			Date requestDate, String remoteFilePath,
			boolean immediate, String status, int currentLine,
			Date completedDate) {
		super();
		this.id = id;
		this.projectCode = projectCode;
		this.user = user;
		this.requestDate = requestDate;
		this.remoteFilePath = remoteFilePath;
		this.immediate = immediate;
		this.status = status;
		this.currentLine = currentLine;
		this.completedDate = completedDate;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the projectCode
	 */
	public String getProjectCode() {
		return projectCode;
	}

	/**
	 * @param projectCode the projectCode to set
	 */
	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * @return the requestDate
	 */
	public Date getRequestDate() {
		return requestDate;
	}

	/**
	 * @param requestDate the requestDate to set
	 */
	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

	/**
	 * @return the remoteFilePath
	 */
	public String getRemoteFilePath() {
		return remoteFilePath;
	}

	/**
	 * @param remoteFilePath the remoteFilePath to set
	 */
	public void setRemoteFilePath(String remoteFilePath) {
		this.remoteFilePath = remoteFilePath;
	}

	/**
	 * @return the immediate
	 */
	public boolean isImmediate() {
		return immediate;
	}

	/**
	 * @param immediate the immediate to set
	 */
	public void setImmediate(boolean immediate) {
		this.immediate = immediate;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the currentLine
	 */
	public int getCurrentLine() {
		return currentLine;
	}

	/**
	 * @param currentLine the currentLine to set
	 */
	public void setCurrentLine(int currentLine) {
		this.currentLine = currentLine;
	}

	/**
	 * @return the completedDate
	 */
	public Date getCompletedDate() {
		return completedDate;
	}

	/**
	 * @param completedDate the completedDate to set
	 */
	public void setCompletedDate(Date completedDate) {
		this.completedDate = completedDate;
	}

	
}


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

/**
 * Bean to return the result of a query about updated record/document status
 * for a single record.
 * 
 * @author Rob Harper
 *
 */
public class StatusResult {

	private String identifier;
	
	private Long recStatusId;
	
	private Long instanceId;
	
	private Long occurrenceId;
	
	private Long docStatusId;
	
	public StatusResult(){}
	
	public StatusResult(String identifier, Long recStatusId, Long instanceId, Long occurrenceId, Long docStatusId){
		this.identifier = identifier;
		this.recStatusId = recStatusId;
		this.instanceId = instanceId;
		this.occurrenceId = occurrenceId;
		this.docStatusId = docStatusId;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public Long getRecStatusId() {
		return recStatusId;
	}

	public void setRecStatusId(Long recStatusId) {
		this.recStatusId = recStatusId;
	}

	public Long getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(Long instanceId) {
		this.instanceId = instanceId;
	}

	public Long getOccurrenceId() {
		return occurrenceId;
	}

	public void setOccurrenceId(Long occurrenceId) {
		this.occurrenceId = occurrenceId;
	}

	public Long getDocStatusId() {
		return docStatusId;
	}

	public void setDocStatusId(Long statusId) {
		this.docStatusId = statusId;
	}
	
}

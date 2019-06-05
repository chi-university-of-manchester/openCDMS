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

import java.util.ArrayList;
import java.util.List;

/**
 * Bean to store the data as the user moved through the Request Export
 * wizard.
 * 
 * @author Rob Harper
 *
 */
public class ExportRequest {

	private String project;
	
	private String projectText;
	
	private List<String> groups = new ArrayList<String>();
	
	private String format;
	
	//TODO this should really be a List<Long> but I couldn't
	//get the binding working properly
	private List<String> documents = new ArrayList<String>();
	
	/**
	 * Format is: docId_entryId
	 */
	private List<String> entries = new ArrayList<String>();
	
	private String immediate;
	
	private List<String> docStatuses = new ArrayList<String>();
	
	private String codeValue;
	
	public List<String> getGroups() {
		return groups;
	}

	public void setGroups(List<String> groups) {
		this.groups = groups;
	}

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

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public List<String> getDocuments() {
		return documents;
	}

	public void setDocuments(List<String> documents) {
		this.documents = documents;
	}

	public List<String> getEntries() {
		return entries;
	}

	public void setEntries(List<String> entries) {
		this.entries = entries;
	}

	public String getImmediate() {
		return immediate;
	}

	public void setImmediate(String immediate) {
		this.immediate = immediate;
	}

	public List<String> getDocStatuses() {
		return docStatuses;
	}

	public void setDocStatuses(List<String> docStatuses) {
		this.docStatuses = docStatuses;
	}

	/**
	 * Get whether codes and/or values are to be displayed for responses.
	 * 
	 * Applicable for CSV and Excel exports only.
	 * 
	 * @return codeValue String
	 */
	public String getCodeValue() {
		return codeValue;
	}

	/**
	 * Set whether codes and/or values are to be displayed for responses.
	 * 
	 * Applicable for CSV and Excel exports only.
	 * 
	 * @param codeValue
	 */
	public void setCodeValue(String codeValue) {
		this.codeValue = codeValue;
	}	
	
}

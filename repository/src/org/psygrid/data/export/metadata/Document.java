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

package org.psygrid.data.export.metadata;

import java.util.ArrayList;
import java.util.List;

/**
 * Metadata about a document included in an export.
 * 
 * @author Rob Harper
 *
 */
public class Document {

	/**
	 * The database id of the document (more precisely the 
	 * document occurrence)
	 */
	private final Long id;
	
	/**
	 * The full name of the document occurrence
	 */
	private String name;
	
	/**
	 * The isolated name of the document.
	 */
	private String docName;
	
	/**
	 * Name of the study stage that the document occurrence is in.
	 */
	private String studyStage;
	
	/**
	 * The list of sections in the document
	 */
	private List<Section> sections = new ArrayList<Section>();

	public Document(Long id){
		this.id = id;
	}
	
	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setDocName(String name) {
		this.docName = name;
	}
	
	public String getDocName(){
		return docName;
	}
	
	public void setStudyStage(String studyStage){
		this.studyStage = studyStage;
	}
	
	public String getStudyStage(){
		return studyStage;
	}

	public List<Section> getSections() {
		return sections;
	}

	public void setSections(List<Section> sections) {
		this.sections = sections;
	}
	
	/**
	 * Return the number of columns that will be used
	 * when converting the exported data for this document
	 * to a tabular format.
	 * 
	 * @return The number of columns.
	 */
	public int columnCount(boolean showCode, boolean showValue){
		int count = 0;
		for ( Section s: sections ){
			count += s.columnCount(showCode, showValue);
		}
		//add 1 for the doc status!
		count++;
		return count;
	}
	
	/**
	 * Locate a section in the list of sections by its database id.
	 * 
	 * @param id The database id.
	 * @return The section or null if no section found with the 
	 * given id
	 */
	public Section findSectionById(Long id){
		for ( Section s: sections ){
			if ( s.getId().equals(id) ){
				return s;
			}
		}
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if ( null != obj && obj instanceof Document ){
			return ((Document)obj).getId().equals(this.id);
		}
		return false;
	}
	
	
}

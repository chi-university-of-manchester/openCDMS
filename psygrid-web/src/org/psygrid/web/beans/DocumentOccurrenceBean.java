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


package org.psygrid.web.beans;

import java.util.ArrayList;
import java.util.List;

import org.psygrid.data.model.hibernate.DocumentOccurrence;

/**
 * @author Rob Harper
 *
 */
public class DocumentOccurrenceBean {

	private long id;
	
	private String name;
	
	private List<DocumentSectionBean> sections = new ArrayList<DocumentSectionBean>();
	
	private long documentid;
	
	public DocumentOccurrenceBean(long id, String name){
		this.id = id;
		this.name = name;
	}
	
	public DocumentOccurrenceBean(DocumentOccurrence docOcc){
		this.id = docOcc.getId().longValue();
		this.name = docOcc.getDisplayText();
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<DocumentSectionBean> getSections() {
		return sections;
	}

	public void setSections(List<DocumentSectionBean> sections) {
		this.sections = sections;
	}
	
	public void addSection(DocumentSectionBean section) {
		this.sections.add(section);
	}
	
	public void addSection(long id, String displayText) {
		this.sections.add(new DocumentSectionBean(id, displayText));
	}

	public long getDocumentid() {
		return documentid;
	}

	public void setDocumentid(long documentid) {
		this.documentid = documentid;
	}
}

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

/**
 * @author Lucy Bridges
 *
 */
public class DocumentBean {

	private long id;
	
	private String name;

	private List<DocumentOccurrenceBean> docOccs = new ArrayList<DocumentOccurrenceBean>();
	
	public DocumentBean(long id, String name){
		this.id = id;
		this.name = name;
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

	public List<DocumentOccurrenceBean> getDocOccs() {
		return docOccs;
	}

	public void addDocOcc(DocumentOccurrenceBean docOcc) {
		docOccs.add(docOcc);
	}
	
	public void setDocOccs(List<DocumentOccurrenceBean> docOccs) {
		this.docOccs = docOccs;
	}
	
}

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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to hold the metadata about the export of records from
 * a dataset.
 * 
 * @author Rob Harper
 *
 */
public class DataSetMetaData {

	/**
	 * The name of the dataset
	 */
	private String name;
	
	/**
	 * The code of the dataset
	 */
	private String code;
	
	/**
	 * The person who requested the export
	 */
	private String requestor;
	
	/**
	 * The date of the export request
	 */
	private String exportDate;
	
	/**
	 * List of the metadata fields included in the export (record 
	 * status etc)
	 */
	private List<String> metaFields = new ArrayList<String>();
	
	/**
	 * List of documents in the export.
	 */
	private List<Document> documents = new ArrayList<Document>();
	
	/**
	 * 
	 */
	private Map<String,String> missingValues = new LinkedHashMap<String,String>();
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getExportDate() {
		return exportDate;
	}

	public void setExportDate(String exportDate) {
		this.exportDate = exportDate;
	}

	public String getRequestor() {
		return requestor;
	}

	public void setRequestor(String requestor) {
		this.requestor = requestor;
	}

	public List<String> getMetaFields() {
		return metaFields;
	}

	public void setMetaFields(List<String> metaFields) {
		this.metaFields = metaFields;
	}

	public List<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}

	/**
	 * Find a document in the list of documents by its database
	 * id.
	 * 
	 * @param id The database id of the document to find.
	 * @return The document, or null if no document found with that id.
	 */
	public Map<String, String> getMissingValues() {
		return missingValues;
	}

	public void setMissingValues(Map<String, String> missingValues) {
		this.missingValues = missingValues;
	}
	
	public void addMissingValue(String code, String label) {
		this.missingValues.put(code, label);
	}
	
	/**
	 * Find a document in the list of documents by its database
	 * id.
	 * 
	 * @param id The database id of the document to find.
	 * @return The document, or null if no document found with that id.
	 */
	public Document findDocumentById(Long id){
		for ( Document d: documents ){
			if ( d.getId().equals(id) ){
				return d;
			}
		}
		return null;
	}
}

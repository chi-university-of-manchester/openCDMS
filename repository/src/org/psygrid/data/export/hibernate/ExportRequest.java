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


package org.psygrid.data.export.hibernate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class to represent a request to export data from the PsyGrid
 * data repository.
 * 
 * @author Rob Harper
 * 
 * @hibernate.class table="t_export_requests"
 */
public class ExportRequest {

	public static final String STATUS_PENDING = "Pending";
	public static final String STATUS_PROCESSING = "Processing";
	public static final String STATUS_COMPLETE = "Complete";
	public static final String STATUS_ERROR = "Error";
	public static final String STATUS_NO_DATA = "No Data";
	
	
    /**
	 * Unique identifier, generated by the Hibernate framework
	 */
	private Long id;
	
	/**
	 * Version, generated by the Hibernate framework
	 */
	private int version;
	
	/**
	 * The DN of the requestor of the export.
	 */
	private String requestor;
	
	/**
	 * The status of the request.
	 */
	private String status;
	
	/**
	 * The project code of the project to export.
	 */
	private String projectCode;
	
	/**
	 * The id of the query that will be run to generate the list of records to
	 * be exported. Will be <code>null</code> if the export isn't related to a query.
	 */
	private Long queryId;
	
	/**
	 * The list of groups within the project to export data
     * for.
	 */
	private List<String> groups = new ArrayList<String>();
	
	
	/**
	 * The mapping of actions export actions to various security tags that are 
	 * associated with certain exportable data elements.
	 */
	private List<ExportSecurityActionMap>  exportActionsMap = new ArrayList<ExportSecurityActionMap>();
	

	/**
	 * The list of document occurrences (and entries) within the project to
	 * export data for.
	 */
	private List<ExportDocument> docOccs = new ArrayList<ExportDocument>();
	
	/**
	 * The date/time when the export request was made.
	 */
	private Date requestDate;
	
	/**
	 * The date/time when the export request was completed.
	 */
	private Date completedDate;
	
	/**
	 * The filename of the zip file containing the exported 
	 * data.
	 */
	private String path;
	
	/**
	 * The filename of the text file containing the SHA-1 hash of the zip file.
	 */
	private String sha1Path;
	
	/**
	 * The filename of the text file containing the MD5 hash of the zip file.
	 */
	private String md5Path;
	
	/**
	 * Format to export the data to.
	 */
	private String format;

	/**
	 * If True then the export request is required to be processed 
	 * immediately. Otherwise it will be queued for scheduled execution.
	 */
	private boolean immediate;
	
	/**
	 * Only document instances with one of these statuses will be exported.
	 */
	private List<String> documentStatuses = new ArrayList<String>();
	
	/**
	 * Whether to show the codes for responses.
	 * 
	 * Applicable for CSV and Excel exports only.
	 */
	private boolean showCodes = true;
	
	/**
	 * Whether to show the codes for responses.
	 * 
	 * Applicable for CSV and Excel exports only.
	 */
	private boolean showValues = true;
	
	/**
	 * Whether to export the participant register
	 */
	private boolean participantRegister;
	
	/**
	 * Default no-arg constructor.
	 */
	public ExportRequest(){}
	
	/**
	 * Constructor that accepts the requestor and project code as
	 * arguments.
	 * 
	 * @param requestor The requestor.
	 * @param projectCode The project code.
	 */
	public ExportRequest(String requestor, String projectCode, List<String> groups, String format, boolean immediate){
		this.requestor = requestor;
		this.projectCode = projectCode;
		this.groups = groups;
		this.requestDate = new Date();
		this.status = STATUS_PENDING;
		this.format = format;
		this.immediate = immediate;
	}
	
	/**
	 * Get the unique identifier, generated by the Hibernate framework
	 * 
	 * @return The unique identifier
	 * 
	 * @hibernate.id column = "c_id" 
	 * 			     generator-class="native"
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Set the unique identifier, generated by the Hibernate framework
	 * 
	 * @param id The unique identifier
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Get the version, generated by the Hibernate framework
	 * 
	 * @return The version
	 * 
	 * @hibernate.version column = "c_version"
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * Set the version, generated by the Hibernate framework
	 * 
	 * @param version The version
	 */
	public void setVersion(int version) {
		this.version = version;
	}

	/**
	 * Get the date/time when the export request was completed.
	 * 
	 * @return The date/time completed.
	 * 
	 * @hibernate.property column="c_completed"
	 */
	public Date getCompletedDate() {
		return completedDate;
	}

	/**
	 * Set the date/time when the export request was completed.
	 * 
	 * @param completedDate The date/time completed.
	 */
	public void setCompletedDate(Date completedDate) {
		this.completedDate = completedDate;
	}

	/**
	 * Get the filename of the zip file containing the exported 
	 * data.
	 * 
	 * @return The filename.
	 * 
	 * @hibernate.property column="c_path"
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Set the filename of the zip file containing the exported 
	 * data.
	 * 
	 * @param path The filename.
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * Get the filename of the text file containing the MD5 
	 * hash of the export
	 * 
	 * @return md5Path
	 * 
	 * @hibernate.property column="c_md5_path"
	 */
	public String getMd5Path() {
		return md5Path;
	}

	/**
	 * Set the filename of the text file containing the MD5 
	 * hash of the export
	 * 
	 * @param md5Path
	 */
	public void setMd5Path(String md5Path) {
		this.md5Path = md5Path;
	}

	/**
	 * Get the filename of the text file containing the SHA-1
	 * hash of the export
	 * 
	 * @return sha1Path
	 * 
	 * @hibernate.property column="c_sha1_path"
	 */
	public String getSha1Path() {
		return sha1Path;
	}

	/**
	 * Set the filename of the text file containing the SHA-1
	 * hash of the export.
	 * 
	 * @param sha1Path
	 */
	public void setSha1Path(String sha1Path) {
		this.sha1Path = sha1Path;
	}

	/**
	 * Get the project code of the project to export.
	 * 
	 * @return The project code.
	 * 
	 * @hibernate.property column="c_project_code"
	 */
	public String getProjectCode() {
		return projectCode;
	}

	/**
	 * Set the project code of the project to export.
	 * 
	 * @param projectCode The project code.
	 */
	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}
	
	/**
	 * Get the id of the query that will be run to generate the list of records to
	 * be exported. Will be <code>null</code> if the export isn't related to a query.
	 * 
	 * @return The query id.
	 * 
	 * @hibernate.property column="c_query_id"
	 */
	public Long getQueryId() {
		return queryId;
	}

	/**
	 * Set the id of the query that will be run to generate the list of records to
	 * be exported. Will be <code>null</code> if the export isn't related to a query.
	 * 
	 * @param queryId The query id.
	 */
	public void setQueryId(Long queryId) {
		this.queryId = queryId;
	}

	/**
     * Get the collection of sections that are associated with the document.
     * 
     * Sections are intended to logically divide up the entries in a document
     * so as to give it structure. Each entry in a document's collection of
     * entries must be associated with one of the document's sections.
     * 
     * @return The collection of sections.
     * 
     * @hibernate.list cascade="all"
     * @hibernate.one-to-many class="org.psygrid.data.export.hibernate.ExportSecurityActionMap"
     * @hibernate.key column="c_exportreq_id" not-null="false"
     * @hibernate.list-index column="c_index"
     */
	public List<ExportSecurityActionMap> getActionsMap() {
		return exportActionsMap;
	}
	
	public void setActionsMap(List<ExportSecurityActionMap> map){
		this.exportActionsMap = map;
	}
	

	/**
	 * Get the list of groups within the project to export data
     * for.
     * 
	 * @return The list of groups.
	 * 
     * @hibernate.list table="t_ex_req_groups"
     *                 cascade="all"
     * @hibernate.key column="c_req_id"
     * @hibernate.list-index column="c_index"
     * @hibernate.element type="string"
     *                    column="c_group"
     *                    not-null="true"
	 */
	public List<String> getGroups() {
		return groups;
	}

	/**
	 * Set the list of groups within the project to export data
     * for.
     * 
	 * @param groups The list of groups.
	 */
	public void setGroups(List<String> groups) {
		this.groups = groups;
	}

	/**
	 * Get the date/time when the export request was made.
	 * 
	 * @return The request date.
	 * 
	 * @hibernate.property column="c_request_date"
	 */
	public Date getRequestDate() {
		return requestDate;
	}

	/**
	 * Set the date/time when the export request was made.
	 * 
	 * @param requestDate The request date.
	 */
	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

	/**
	 * Get the DN of the requestor of the export.
	 * 
	 * @return The DN of the requestor.
	 * 
	 * @hibernate.property column="c_requestor"
	 */
	public String getRequestor() {
		return requestor;
	}

	/**
	 * Set the DN of the requestor of the export.
	 * 
	 * @param requestor The DN of the requestor.
	 */
	public void setRequestor(String requestor) {
		this.requestor = requestor;
	}

	/**
	 * Get the status of the request.
	 * 
	 * @return The status.
	 * 
	 * @hibernate.property column="c_status"
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Set the status of the request.
	 * 
	 * @param status The status.
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	
	/**
	 * Get the list of document occurrences within the project to
	 * export data for.
	 * 
	 * @return docOccs The list of document occurrences;
	 * 
	 * @hibernate.list cascade="all"
     * @hibernate.one-to-many class="org.psygrid.data.export.hibernate.ExportDocument"
     * @hibernate.key column="c_exportreq_id" not-null="false"
     * @hibernate.list-index column="c_index"
	 */
	public List<ExportDocument> getDocOccs() {
		return docOccs;
	}

	/**
	 * Set the list of document occurrences within the project to
	 * export data for.
	 * 
	 * @param docOccs The list of document occurrences.
	 */
	public void setDocOccs(List<ExportDocument> docOccs) {
		this.docOccs = docOccs;
	}

	/**
	 * Get the format the data should be exported to.
	 * 
	 * @return The export format.
	 * 
	 * @hibernate.property column="c_format"
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * Set the format the data should be exported to.
	 * 
	 * @param format The export format.
	 */
	public void setFormat(String format) {
		this.format = format;
	}
	
	/**
	 * Get whether the export request is to processed immediately
	 * or not.
	 * 
	 * @return Boolean
	 * 
	 * @hibernate.property column="c_immediate"
	 */
	public boolean isImmediate() {
		return immediate;
	}

	public void setImmediate(boolean immediate) {
		this.immediate = immediate;
	}

	/**
	 * Get a list of document statuses that will determine what document
	 * instances will be exported.
	 * 
	 * For example, this can be used to export only approved documents or
	 * everything except incomplete documents.
	 * 
	 * @return documentStatuses
	 * 
	 * @hibernate.list table="t_ex_req_doc_statuses" cascade="all"
     * @hibernate.key column="c_req_id"
     * @hibernate.list-index column="c_index"
     * @hibernate.element type="string"
     *                    column="c_docstatus"
     *                    not-null="true"
	 */
	public List<String> getDocumentStatuses() {
		return documentStatuses;
	}

	/**
	 * Set the list of document statuses that will determine what document
	 * instances will be exported. 
	 * 
	 * For example, this can be used to export only approved documents or
	 * everything except incomplete documents.
	 * 
	 * @param documentStatuses
	 */
	public void setDocumentStatuses(List<String> documentStatuses) {
		this.documentStatuses = documentStatuses;
	}

	/**
	 * Whether to show the codes for responses.
	 * 
	 * Applicable for CSV and Excel exports only.
	 * 
	 * @return showCodes
	 * 
	 * @hibernate.property column="c_is_show_codes"
	 */
	public boolean isShowCodes() {
		return showCodes;
	}

	/**
	 * Whether to show the codes for responses.
	 * 
	 * Applicable for CSV and Excel exports only.
	 * 
	 * @param showCodes
	 */
	public void setShowCodes(boolean showCodes) {
		this.showCodes = showCodes;
	}

	/**
	 * Whether to show the values for responses. If false showCodes should be 
	 * set to true, so that just the codes are displayed.
	 * 
	 * Applicable for CSV and Excel exports only.
	 * 
	 * @return showValues
	 * 
	 * @hibernate.property column="c_is_show_values"
	 */
	public boolean isShowValues() {
		return showValues;
	}

	/**
	 * Whether to show the values for responses. If false showCodes should be 
	 * set to true, so that just the codes are displayed.
	 * 
	 * Applicable for CSV and Excel exports only.
	 * 
	 * @param showValues
	 */
	public void setShowValues(boolean showValues) {
		this.showValues = showValues;
	}
	
	/**
	 * Whether the participant register should be exported
	 * 
	 * @return
	 * 
	 * @hibernate.property column="c_is_participant_register"
	 */
	public boolean isParticipantRegister() {
		return participantRegister;
	}

	/**
	 * Whether the participant register should be exported
	 * 
	 * @param participantRegister
	 */
	public void setParticipantRegister(boolean participantRegister) {
		this.participantRegister = participantRegister;
	}

	public void init(){
		this.requestDate = new Date();
		this.status = STATUS_PENDING;
	}
	
	public org.psygrid.data.export.dto.ExportRequest toDTO(){
		org.psygrid.data.export.dto.ExportRequest dtoER = new org.psygrid.data.export.dto.ExportRequest();
		dtoER.setCompletedDate(completedDate);
		dtoER.setId(id);
		dtoER.setPath(path);
		dtoER.setSha1Path(sha1Path);
		dtoER.setMd5Path(md5Path);
		dtoER.setProjectCode(projectCode);
		dtoER.setQueryId(queryId);
		dtoER.setRequestDate(requestDate);
		dtoER.setRequestor(requestor);
		dtoER.setStatus(status);
		dtoER.setVersion(version);
		dtoER.setFormat(format);
		dtoER.setImmediate(immediate);
		dtoER.setShowCodes(showCodes);
		dtoER.setShowValues(showValues);
		dtoER.setParticipantRegister(participantRegister);
		String[] dtoGroups = new String[groups.size()];
		for ( int i=0; i<groups.size(); i++ ){
			dtoGroups[i] = groups.get(i);
		}
		dtoER.setGroups(dtoGroups);
		
		org.psygrid.data.export.dto.ExportSecurityActionMap[] actionMapArray = new org.psygrid.data.export.dto.ExportSecurityActionMap[this.exportActionsMap.size()];
		for (int i=0; i< exportActionsMap.size(); i++) {
			actionMapArray[i] = exportActionsMap.get(i).toDTO();
		}
		dtoER.setExportSecurityActionMaps(actionMapArray);
		
		org.psygrid.data.export.dto.ExportDocument[] dtoDocOccs = new org.psygrid.data.export.dto.ExportDocument[this.docOccs.size()];
		for ( int i=0; i<docOccs.size(); i++ ){
			dtoDocOccs[i] = docOccs.get(i).toDTO();
		}
		dtoER.setDocOccs(dtoDocOccs);

		String[] dtoDocStatuses = new String[documentStatuses.size()];
		for (int i=0; i < documentStatuses.size(); i++) {
			dtoDocStatuses[i] = documentStatuses.get(i);
		}
		dtoER.setDocumentStatuses(dtoDocStatuses);
		
		return dtoER;
	}

	/**
	 * Returns true if the document occurrence with the given id should be exported for this request.
	 * 
	 * @param docOccId the document occurrence id
	 * @return export status
	 */
	public boolean exportDocOcc(Long docOccId){
		//export this occurrence if either (a) the docOccs list is null, implying
		//all occurrences are being exported, or (b) the id of this occurrence is
		//in the list of ids to export.
		boolean export = false;
		if (docOccs == null) {
			export = true;
		}
		else {
			for (ExportDocument exDoc: docOccs) {
				if (exDoc.getDocOccId().equals(docOccId)) {
					export = true;
					break;
				}
			}
		}
		return export;
	}

	/**
	 * Returns true if the given entry within the given document occurrence should be exported for this request.
	 * 
	 * @param docOccId the document occurrence id
	 * @param entryId the entry id
	 * @return export status
	 */
	public boolean exportEntry(Long docOccId,Long entryId){

		boolean export = false;

		for(ExportDocument exDoc: docOccs){
			if (exDoc.getDocOccId().equals(docOccId) && exDoc.getEntryIds().contains(entryId)){
				export = true;
				break;
			}
		}
		return export;
	}

}

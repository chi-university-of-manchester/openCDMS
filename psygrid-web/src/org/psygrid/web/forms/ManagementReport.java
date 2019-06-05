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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.psygrid.web.beans.DocumentBean;
import org.psygrid.web.beans.DocumentOccurrenceBean;
import org.psygrid.web.beans.EntryBean;

/**
 * Used to store the results of the 'generate report' web form wizard
 * 
 * @author Lucy Bridges
 *
 */
public class ManagementReport extends Report {

	private static final SimpleDateFormat formatter = new SimpleDateFormat("MMMM yyyy");
	
	public static final String RECRUITMENT  = "recruitment";
	public static final String UKCRNSUMMARY = "ukcrnsummary";
	public static final String RECEIVINGTREATMENT  = "receivingtreatment";
	public static final String STATUSREPORT  = "statusreport";
	public static final String DATEREPORT  = "datereport";
	public static final String DOCUMENTREPORT = "documentreport";
	public static final String BASICSTATSREPORT = "basicstatsreport";
	
	private String managementType;
	private List<String> groups = new ArrayList<String>();
	private Calendar startDate;
	private Calendar endDate;
	private Map<String,String> targets = new LinkedHashMap<String,String>();
	private DocumentBean document = null;
	private String documentid = null;
	private DocumentOccurrenceBean docOcc = null;
	private String dococcid = null;
	private List<String> entryIds = new ArrayList<String>();
	private List<EntryBean> entries = new ArrayList<EntryBean>();
	private List<String> statistics = new ArrayList<String>();
	
	/**
	 * A list of documents in the relevant dataset that could be 
	 * used in this report.
	 */
	private List<DocumentBean> potentialDocuments = new ArrayList<DocumentBean>();
	
	private List<EntryBean> potentialEntries = new ArrayList<EntryBean>();
	
	/**
	 * Get the list of group codes this report is to
	 * be generated for.
	 * 
	 * @return group codes
	 */
	public List<String> getGroups() {
		return groups;
	}

	public void addGroup(String g) {
		groups.add(g);
	}
	
	public void setGroups(List<String> groups) {
		this.groups = groups;
	}

	public Calendar getEndDate() {
		return endDate;
	}
	
	public void setEndDate(Calendar endDate) {
		this.endDate = endDate;
	}

	public String getFormattedEndDate(){
		return formatter.format(endDate.getTime());
	}

	public Calendar getStartDate() {
		return startDate;
	}
	
	public void setStartDate(Calendar startDate) {
		this.startDate = startDate;
	}
	
	public String getFormattedStartDate(){
		return formatter.format(startDate.getTime());
	}

	public Map<String, String> getTargets() {
		return targets;
	}
	
	public void addTarget(String date, String value) {
		this.targets.put(date, value);
	}
	
	public void setTargets(Map<String, String> targets) {
		this.targets = targets;
	}

	/**
	 * The management report type should be left null, unless it is a
	 * report described by one of the static report name strings defined
	 * in this class
	 */
	public String getManagementType() {
		return managementType;
	}

	/**
	 * The management report type should be left null, unless it is a
	 * report described by one of the static report name strings defined
	 * in this class
	 */
	public void setManagementType(String type) {
		this.managementType = type;
	}

	public DocumentBean getDocument() {
		return document;
	}

	public void setDocument(DocumentBean document) {
		this.document = document;
	}

	public String getDocumentid() {
		return documentid;
	}

	public void setDocumentid(String documentid) {
		this.documentid = documentid;
	}

	public List<DocumentBean> getPotentialDocuments() {
		return potentialDocuments;
	}

	public void addPotentialDocument(DocumentBean document) {
		potentialDocuments.add(document);
	}
	
	public void setPotentialDocuments(List<DocumentBean> potentialDocuments) {
		this.potentialDocuments = potentialDocuments;
	}

	public DocumentOccurrenceBean getDocOcc() {
		return docOcc;
	}

	public void setDocOcc(DocumentOccurrenceBean docOcc) {
		this.docOcc = docOcc;
	}

	public String getDococcid() {
		return dococcid;
	}

	public void setDococcid(String dococcid) {
		this.dococcid = dococcid;
	}

	public List<String> getEntryIds() {
		return entryIds;
	}

	public void setEntryIds(List<String> entryIds) {
		this.entryIds = entryIds;
	}

	public List<EntryBean> getEntries() {
		return entries;
	}

	public void setEntries(List<EntryBean> entries) {
		this.entries = entries;
	}

	public void addEntry(EntryBean entry){
		this.entries.add(entry);
	}
	
	public void clearEntries(){
		this.entries.clear();
	}
	
	public List<EntryBean> getPotentialEntries() {
		return potentialEntries;
	}

	public void setPotentialEntries(List<EntryBean> potentialEntries) {
		this.potentialEntries = potentialEntries;
	}

	public List<DocumentOccurrenceBean> getPotentialOccurrences(){
		return document.getDocOccs();
	}

	public List<String> getStatistics() {
		return statistics;
	}

	public void setStatistics(List<String> statistics) {
		this.statistics = statistics;
	}
	
	/**
	 * Set all fields belonging to this management report
	 * back to their original value (i.e null or an empty
	 * list). This is useful to reset the report object. 
	 */
	public void clearManagementFields() {
		managementType = null;
		groups = new ArrayList<String>();
		startDate = null;
		endDate = null;
		targets = new LinkedHashMap<String,String>();
		document = null;
		documentid = null;
		docOcc = null;
		dococcid = null;
		entryIds = new ArrayList<String>();
		entries = new ArrayList<EntryBean>();
		statistics = new ArrayList<String>();
		potentialDocuments = new ArrayList<DocumentBean>();
		potentialEntries = new ArrayList<EntryBean>();
	}
	
}

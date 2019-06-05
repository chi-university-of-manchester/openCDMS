/*
Copyright (c) 2006-2009, The University of Manchester, UK.

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

package org.opencdms.web.modules.export.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.psygrid.data.export.ExportFormat;
import org.psygrid.data.export.hibernate.ExportDocument;
import org.psygrid.data.export.hibernate.ExportRequest;
import org.psygrid.data.model.hibernate.DocumentOccurrence;
import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.data.model.hibernate.SectionOccurrence;
import org.psygrid.data.model.hibernate.Status;

/**
 * @author Rob Harper
 *
 */
public class ExportDetailsModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2412925311436036844L;
	
	private ExportFormat format;
	private List<ExportEntry> entries = new ArrayList<ExportEntry>();
	private List<Status> docStatuses = new ArrayList<Status>();
	private String codesOrValues;
	private Boolean participantRegister = false;
	
	public ExportFormat getFormat() {
		return format;
	}
	public void setFormat(ExportFormat format) {
		this.format = format;
	}
	public List<ExportEntry> getEntries() {
		return entries;
	}
	public void setEntries(List<ExportEntry> entries) {
		this.entries = entries;
	}
	public List<Status> getDocStatuses() {
		return docStatuses;
	}
	public void setDocStatuses(List<Status> docStatuses) {
		this.docStatuses = docStatuses;
	}
	public String getCodesOrValues() {
		return codesOrValues;
	}
	public void setCodesOrValues(String codesOrValues) {
		this.codesOrValues = codesOrValues;
	}
	
	public Boolean getParticipantRegister() {
		return participantRegister;
	}
	
	public void setParticipantRegister(Boolean participantRegister) {
		this.participantRegister = participantRegister;
	}

	public void populateExportRequest(ExportRequest export){
		
		export.setFormat(format.toStringForRequest());
		
		boolean showCodes  = true;
		boolean showValues = true;
		if ("Codes Only".equals(codesOrValues)) {
			showValues = false;
		}
		else if ("Values Only".equals(codesOrValues)) {
			showCodes = false;
		}
		export.setShowCodes(showCodes);
		export.setShowValues(showValues);
		
		export.setParticipantRegister(participantRegister);

		//TODO section occurrences!
		Map<Long, ExportDocument> map = new HashMap<Long, ExportDocument>();
		for ( ExportEntry ee: entries ){
			ExportDocument doc = map.get(ee.getDocOccId());
			if ( null == doc ){
				doc = new ExportDocument();
				doc.setDocOccId(ee.getDocOccId());
				map.put(ee.getDocOccId(), doc);
			}
			doc.getEntryIds().add(ee.getEntryId());
		}
		export.setDocOccs(new ArrayList<ExportDocument>(map.values()));

		List<String> statuses = new ArrayList<String>();
		for ( Status s: docStatuses ){
			statuses.add(s.getLongName());
		}
		export.setDocumentStatuses(statuses);
		
	}
	
	public static class ExportEntry implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private long docOccId;
		private long secOccId;
		private long entryId;
		private String text;
		public ExportEntry(DocumentOccurrence docOcc, SectionOccurrence secOcc, Entry entry){
			docOccId = docOcc.getId();
			secOccId = secOcc.getId();
			entryId = entry.getId();
			if ( null == entry.getDisplayText() ){
				text = entry.getName();
			}
			else{
				text = entry.getDisplayText();
			}
		}
		public long getDocOccId() {
			return docOccId;
		}
		public void setDocOccId(long docOccId) {
			this.docOccId = docOccId;
		}
		public long getSecOccId() {
			return secOccId;
		}
		public void setSecOccId(long secOccId) {
			this.secOccId = secOccId;
		}
		public long getEntryId() {
			return entryId;
		}
		public void setEntryId(long entryId) {
			this.entryId = entryId;
		}
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
		@Override
		public String toString() {
			return text;
		}
	}
}

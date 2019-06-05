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

package org.psygrid.data.export;

import java.io.OutputStream;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.psygrid.data.export.hibernate.ExportDocument;
import org.psygrid.data.export.hibernate.ExportRequest;
import org.psygrid.data.export.hibernate.ExportSecurityActionMap;
import org.psygrid.data.export.metadata.BasicEntry;
import org.psygrid.data.export.metadata.DataSetMetaData;
import org.psygrid.data.export.plugins.ExportPlugin;
import org.psygrid.data.export.plugins.ParticipantRegisterPlugin;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.repository.dao.DAOException;
import org.psygrid.data.repository.transformer.TransformerException;
import org.psygrid.data.utils.esl.EslException;
import org.psygrid.data.utils.esl.IRemoteClient;
import org.psygrid.data.utils.esl.RemoteClient;

/**
 * Class used to export the data from a list of records into an
 * intermediate XML format, with accompanying metadata.
 * <p>
 * Use of this class assumes that the Hibernate session used to
 * retrieve the record is still open (so that lazy loading may 
 * occur).
 * <p>
 * This is a base implementation that does not apply any export
 * security to the export.
 * 
 * @author Rob Harper
 *
 */
public class XMLExporter {

	private static final Log LOG = LogFactory.getLog(XMLExporter.class);

	public static final String XML_DATASET = "dataset";
	public static final String XML_PROJECT = "project";
	public static final String XML_PARTICIPANT = "participant";
	public static final String XML_IDENTIFIER = "identifier";
	public static final String XML_DOCUMENT = "document";
	public static final String XML_SECTION = "section";
	public static final String XML_INSTANCE = "instance";
	public static final String XML_STATUS = "status";
	public static final String XML_NAME = "name";
	public static final String XML_COMPOSITE = "composite";
	public static final String XML_ROW = "row";
	public static final String XML_ROWS = "rows";
	public static final String XML_NUMBER = "number";
	public static final String XML_ENTRY = "entry";
	public static final String XML_VALUE = "value";
	public static final String XML_CODE = "code";
	public static final String XML_UNIT = "unit";
	public static final String XML_IS_MISSING = "isMissing";
	public static final String XML_ID = "id";
	public static final String XML_COUNT = "count";
	public static final String XML_INDEX = "index";
	public static final String XML_METADATA = "metadata";
	public static final String XML_EXTRA = "extra";
	public static final String XML_USERDATA = "requestdata";

	public static final String NAME_STATUS = "Status";
	public static final String NAME_SITE = "Site";
	public static final String NAME_CONSULTANT = "Consultant";
	public static final String NAME_STUDY_ENTRY_DATE = "StudyEntryDate";
	public static final String NAME_SCHEDULE_START_DATE = "ScheduleStartDate";
	public static final String NAME_RANDOMISATION_DATE = "RanomisationDate";
	public static final String NAME_PRIMARY_IDENTIFIER = "PrimaryIdentifier";
	public static final String NAME_SECONDARY_IDENTIFIER = "SecondaryIdentifier";
	public static final String NAME_NOTES = "Notes";

	public static final String NOT_RANDOMISED = "Not Randomised";
	
	/**
	 * A list of export plugins.
	 * Export plugins can provide extra information for a record during an export.
	 */
	private List<ExportPlugin> plugins;

	
	/**
	 * Wired in the application context.
	 * @param plugins the plugins to set
	 */
	public void setPlugins(List<ExportPlugin> plugins) {
		this.plugins = plugins;
	}


	/**
	 * Export the data from the records, writing it as XML directly to
	 * the output stream.
	 * <p>
	 * The XML written for each record is as minimal as possible, with 
	 * essentially no information stored other than the values of responses
	 * and which entry they relate to. Therefore to further transform the
	 * exported XML into something usable it must be combined with the export
	 * metadata generated at the same time.
	 * 
	 * The hibernate session is passed in so we can evict the records from the hibernate session
	 * as they are processed, otherwise large numbers of records cause out of memory exceptions.
	 * 
	 * @param output the output stream for the exported xml
	 * @param session the current hibernate session
	 * @param c the criteria query which returns the records for this export
	 * @param meta the export metadata
	 * @param actionMap
	 * @param request the exports request
	 * 
	 * @throws XMLStreamException
	 * @throws DAOException
	 * @throws TransformerException
	 * @throws RemoteException
	 */	
	public void export(OutputStream output, Session session, Criteria c, 
			org.psygrid.data.export.metadata.DataSetMetaData meta, 
			List<ExportSecurityActionMap> actionMap,
			ExportRequest request) throws XMLStreamException, DAOException, TransformerException, RemoteException {

		List<ExportPlugin> pluginList = getApplicablePlugins(request);
		
		//Map to store the maximum number of rows for each composite entry across
		//all the records being exported
		Map<Long, Integer> compositeMap = new HashMap<Long, Integer>();

		//Map to store section info for each section that permits multiple
		//instances being created at runtime. Stored the maximum number of
		//instances, and for each instance the maximum number of rows for
		//each composite, across all the records being exported.
		Map<Long, SectionMap> sectionMap = new HashMap<Long, SectionMap>();

		// Create an output factory
		XMLOutputFactory xmlof = XMLOutputFactory.newInstance();

		// Create an XML stream writer
		XMLStreamWriter xmlw = xmlof.createXMLStreamWriter(output);

		// Add the dataset missing values to the metadata
		addMissingValues(meta);
		
		writeXmlHeader(xmlw);
		
		// We use join fetches when retrieving the dataset - this is so that hibernate proxies are NOT used for entries.
		// This is necessary because the code in this class uses instanceof(!!!) on a document's entries.
		// Instanceof does NOT work correctly with hibernate proxies in polymorphic collections.
		// See - https://community.jboss.org/wiki/ProxyVisitorPattern
		DataSet ds = (DataSet)session.createQuery("from DataSet d left join fetch d.documents doc " +
				"left join fetch doc.entries e where d.projectCode=:projectCode ")
				.setString("projectCode", request.getProjectCode()).uniqueResult();

		// This is better for batch processing.
		ScrollableResults records = c.scroll();
		
		while (records.next()){
			
			// Grab the next record from the scrollable results
			Record r = (Record)records.get(0);
			
			LOG.info("Export "+request.getId()+" exporting record '"+r.getIdentifier().getIdentifier()+"'");
						
			String groupCode = r.getIdentifier().getGroupPrefix();

			// Build a list of document occurrences to export for the current record.
			List<DocumentOccurrence> docsOccsToExport = new ArrayList<DocumentOccurrence>();

			for (Document doc: ds.getDocuments()){
				
				for ( DocumentOccurrence docOcc: doc.getOccurrences()){
				
					if (request.exportDocOcc(docOcc.getId())) {
						DocumentInstance docInst = r.getDocumentInstance(docOcc);
						if (docInst != null && docInst.getStatus() != null &&
								request.getDocumentStatuses().contains(docInst.getStatus().getLongName())) {
							docsOccsToExport.add(docOcc);
						}
					}
				}
			}

			if (docsOccsToExport.size() > 0) {
				writeRecordStart(ds,r, xmlw, pluginList, request);
				for (DocumentOccurrence docOcc: docsOccsToExport) {
					writeDocument(ds, r, docOcc, xmlw, compositeMap, sectionMap,ds.getProjectCode(), groupCode, request, meta, actionMap);
				}
				writeRecordEnd(xmlw);	
			}
		
			// Evict the record from the hibernate session after we have processed it.
			session.evict(r);
		}
		
		records.close();
		
		writeXmlFooter(xmlw);
		xmlw.close();
		
		//generate the export metadata
		generateMetadata(ds, meta, request, compositeMap, sectionMap, pluginList);
	}


	/**
	 * Returns a list of applicable export plugins.
	 */
	public List<ExportPlugin> getApplicablePlugins(ExportRequest request) {

		List<ExportPlugin> pluginList = new ArrayList<ExportPlugin>();

		for(ExportPlugin plugin: plugins){
			if(plugin.isApplicable(request)){
				LOG.info("Using export plugin:'"+plugin+"' for dataset '"+request.getProjectCode()+"'");
				pluginList.add(plugin);
				break; // Choose only the first applicable plugin for now.
			}
		}
		
		return pluginList;
	}
		
	protected void addMissingValues(DataSetMetaData meta) {
		//TODO get missing values from dataset rather than hardcode
		meta.addMissingValue("960", "Data not known");
		meta.addMissingValue("970", "Not applicable");
		meta.addMissingValue("980", "Refused to answer");
		meta.addMissingValue("999", "Data unable to be captured");
	}
	
	/**
	 * Write the opening tag of the XML
	 * 
	 * @param xmlw
	 * @throws XMLStreamException
	 */
	protected void writeXmlHeader(XMLStreamWriter xmlw) throws XMLStreamException {
		xmlw.writeStartDocument();
		xmlw.writeStartElement(XML_DATASET);
	}

	/**
	 * Write the closing tag of the XML
	 * 
	 * @param xmlw
	 * @throws XMLStreamException
	 */
	protected void writeXmlFooter(XMLStreamWriter xmlw) throws XMLStreamException {
		xmlw.writeEndDocument();
	}

	/**
	 * Write the start of a participant as XML
	 * 
	 * @param r
	 * @param xmlw
	 * @throws XMLStreamException
	 */
	protected void writeRecordStart(org.psygrid.data.model.hibernate.DataSet ds, Record r, XMLStreamWriter xmlw, List<ExportPlugin> pluginList, ExportRequest request) throws XMLStreamException {
		xmlw.writeStartElement(XML_PARTICIPANT);
		//identifier
		xmlw.writeStartElement(XML_IDENTIFIER);
		
		// Conditionally report the external identifier
		String identifier = r.getIdentifier().getIdentifier();
		String externalID = r.getExternalIdentifier();
		boolean useExternalID = r.getUseExternalIdAsPrimary();
		String exportID = useExternalID?externalID:identifier;

		xmlw.writeCharacters(exportID);
		xmlw.writeEndElement();
		//record metadata
		xmlw.writeStartElement(XML_METADATA);
		writeSimpleEntry(NAME_STATUS, r.getStatus().getLongName(), xmlw);
		writeSimpleEntry(NAME_SITE, r.getSite().getSiteName(), xmlw);
		writeSimpleEntry(NAME_CONSULTANT, r.getConsultant(), xmlw);
		writeSimpleEntry(NAME_STUDY_ENTRY_DATE, r.getStudyEntryDate(), xmlw);
		writeSimpleEntry(NAME_SCHEDULE_START_DATE, r.getScheduleStartDate(), xmlw);
		if (ds.isRandomizationRequired()) {
			writeSimpleEntry(NAME_RANDOMISATION_DATE, getRandomisationDate(ds,r,request.getRequestor()), xmlw);
		}
		writeSimpleEntry(NAME_PRIMARY_IDENTIFIER, r.getPrimaryIdentifier(), xmlw);
		writeSimpleEntry(NAME_SECONDARY_IDENTIFIER, r.getSecondaryIdentifier(), xmlw);
		writeSimpleEntry(NAME_NOTES, r.getNotes(), xmlw);
		
		// Write any data generated by export plugins
		for(ExportPlugin plugin : pluginList) {
			Properties pluginResults = plugin.getResults(r,request);
			for(String column:plugin.getColumnNames()){
				String result = pluginResults.get(column)!=null ? pluginResults.get(column).toString() : "";
				writeSimpleEntry(column, result , xmlw);
			}
		}

		xmlw.writeEndElement();
	}

	
	/**
	 * Write the end of a participant as XML
	 * 
	 * @param xmlw
	 * @throws XMLStreamException
	 */
	protected void writeRecordEnd(XMLStreamWriter xmlw) throws XMLStreamException{
		xmlw.writeEndElement();
	}

	/**
	 * Write a document for a participant as XML
	 * 
	 * @param r The record for the participant
	 * @param docOcc The document occurrence being written
	 * @param xmlw XML writer
	 * @param compositeMap Map of composite entry metadata
	 * @param sectionMap Map of multiple-runtime-instance sections
	 * @param groupCode Group/centre code of the current record
	 * @throws XMLStreamException
	 * @throws DAOException
	 * @throws TransformerException
	 * @throws RemoteException
	 */
	protected void writeDocument(org.psygrid.data.model.hibernate.DataSet ds,
			Record r, DocumentOccurrence docOcc, XMLStreamWriter xmlw, Map<Long, Integer> compositeMap,
			Map<Long, SectionMap> sectionMap, String projectCode, String groupCode, ExportRequest request, DataSetMetaData meta, List<ExportSecurityActionMap> actionMap) 
	throws XMLStreamException, DAOException, TransformerException, RemoteException {
		
		String requestor = request.getRequestor();
		
		Document doc = docOcc.getDocument();
		DocumentInstance docInst = r.getDocumentInstance(docOcc);

		xmlw.writeStartElement(XML_DOCUMENT);
		writeTag(XML_ID, docOcc.getId().toString(), xmlw);

		//TODO - I really want to have this check higher up, so if they don't
		//have authorization they don't see the document at all in the export
		// - but I need to talk to Lucy about this...
		if ( checkAuthorisation(projectCode, groupCode, docInst.getAction(), requestor) ) {	
			xmlw.writeStartElement(XML_STATUS);
			xmlw.writeCharacters(docInst.getStatus().getLongName());
			xmlw.writeEndElement();

			for ( int i=0, c=doc.numSections(); i<c; i++ ){
				Section section = doc.getSection(i);
				for ( int j=0, d=section.numOccurrences(); j<d; j++ ){
					SectionOccurrence secOcc = section.getOccurrence(j);
					xmlw.writeStartElement(XML_SECTION);
					writeTag(XML_ID, secOcc.getId().toString(), xmlw);
					if ( secOcc.isMultipleAllowed() ){
						if (!sectionMap.containsKey(secOcc.getId())) {
							sectionMap.put(secOcc.getId(), new SectionMap());
						}
						SectionMap map = sectionMap.get(secOcc.getId());
						int counter = 0;
						for ( int k=0, e=docInst.numSecOccInstances(); k<e; k++ ){
							SecOccInstance secOccInst = docInst.getSecOccInstance(k);
							if ( secOccInst.getSectionOccurrence().equals(secOcc) ){
								Map<Long, Integer> compMap = map.getCompositeMap(counter);
								xmlw.writeStartElement(XML_INSTANCE);
								writeTag(XML_INDEX, Integer.toString(counter), xmlw);
								counter++;
								for ( int l=0, f=doc.numEntries(); l<f; l++ ){
									Entry entry = doc.getEntry(l);
									if ( request.exportEntry(docOcc.getId(),entry.getId()) && entry.getSection().equals(section) ){
										setRequiredAction(ds,entry, doc, actionMap);
										Response resp = docInst.getResponse(entry, secOccInst);
										takeRequiredSecurityAction(ds,resp);
										boolean authorized = checkAuthForResponse(resp, projectCode, groupCode, requestor);
										writeResponse(entry, resp, xmlw, compMap, authorized, meta);
									}
								}								
								xmlw.writeEndElement();
							}
						}
					}
					else{
						xmlw.writeStartElement(XML_INSTANCE);
						writeTag(XML_INDEX, Integer.toString(0), xmlw);
						for ( int k=0, e=doc.numEntries(); k<e; k++ ){
							Entry entry = doc.getEntry(k);
							if ( request.exportEntry(docOcc.getId(),entry.getId()) && entry.getSection().equals(section) ){
								setRequiredAction(ds,entry, doc, actionMap);
								Response resp = docInst.getResponse(entry, secOcc);
								takeRequiredSecurityAction(ds,resp);
								boolean authorized = checkAuthForResponse(resp, projectCode, groupCode, requestor);
								writeResponse(entry, resp, xmlw, compositeMap, authorized, meta);
							}
						}
						xmlw.writeEndElement();
					}
					xmlw.writeEndElement();
				}
			}
		}

		xmlw.writeEndElement();
	}

	/**
	 * Write the contents of a single response to XML
	 * 
	 * @param entry The entry the response is for
	 * @param resp The response
	 * @param xmlw XML writer
	 * @param compositeMap Map of composite entry metadata
	 * @param authorized True if the export requestor is authorized to export this response
	 * @throws XMLStreamException
	 */
	private static void writeResponse(final Entry entry, final Response resp, final XMLStreamWriter xmlw, Map<Long, Integer> compositeMap, boolean authorized, DataSetMetaData meta) throws XMLStreamException {
		if ( entry instanceof org.psygrid.data.model.hibernate.BasicEntry){
			writeBasicResponse((org.psygrid.data.model.hibernate.BasicEntry)entry, (BasicResponse)resp, xmlw, authorized, meta);
		}
		else if ( entry instanceof CompositeEntry){
			writeCompositeResponse((CompositeEntry)entry, (CompositeResponse)resp, xmlw, compositeMap, authorized, meta);
		}
	}

	/**
	 * Write the contents of a single basic response to XML
	 * 
	 * @param entry The basic entry the basic response is for.
	 * @param resp The basic response
	 * @param xmlw XML writer
	 * @param authorized True if the export requestor is authorized to export this response
	 * @throws XMLStreamException
	 */
	private static void writeBasicResponse(final org.psygrid.data.model.hibernate.BasicEntry entry, final BasicResponse resp, final XMLStreamWriter xmlw, boolean authorized, DataSetMetaData meta) throws XMLStreamException {
		xmlw.writeStartElement(XML_ENTRY);
		writeTag(XML_ID, entry.getId().toString(), xmlw);
		if ( null == resp ){
			writeTag(XML_IS_MISSING, null, xmlw);
			writeTag(XML_VALUE, null, xmlw);
			writeTag(XML_CODE, null, xmlw);
			writeTag(XML_UNIT, null, xmlw);
			writeTag(XML_EXTRA, null, xmlw);
		}
		else{
			String value = resp.exportTextValue(authorized);
			
			//Strip the value of any carriage returns or line breaks as these are problematic for many applications, such as SPSS.
			String cleanedValue = StringEditor.doReplacement(StringEditor.SpecialCharacter.all,' ', value);
			
			
			
			String code = resp.exportCodeValue(authorized);
			boolean isMissing = false;
			if (value != null && code != null) {
				for (String c: meta.getMissingValues().keySet()) {
					if (c.equals(code)
							&& value.equals(meta.getMissingValues().get(c))) {
						isMissing = true;	
					}
				}
			}
			writeTag(XML_IS_MISSING, Boolean.toString(isMissing), xmlw);
			writeTag(XML_VALUE, cleanedValue, xmlw);
			writeTag(XML_CODE, code, xmlw);
			writeTag(XML_UNIT, resp.exportUnitValue(authorized), xmlw);
			writeTag(XML_EXTRA, resp.exportExtraValue(authorized), xmlw);
		}
		xmlw.writeEndElement();
	}

	/**
	 * Write the contents of a single composite response to XML
	 * 
	 * @param entry The composite entry the composite response is for.
	 * @param resp The composite response.
	 * @param xmlw XML writer
	 * @param compositeMap Map of composite entry metadata
	 * @param authorized True if the export requestor is authorized to export this response
	 * @throws XMLStreamException
	 */
	private static void writeCompositeResponse(final CompositeEntry entry, final CompositeResponse resp, final XMLStreamWriter xmlw, Map<Long, Integer> compositeMap, boolean authorized, DataSetMetaData meta) throws XMLStreamException {
		xmlw.writeStartElement(XML_COMPOSITE);
		writeTag(XML_ID, entry.getId().toString(), xmlw);
		if ( null != resp ){
			Integer rowCount = compositeMap.get(entry.getId());
			if ( null == rowCount ){
				compositeMap.put(Long.valueOf(entry.getId().longValue()), Integer.valueOf(resp.numCompositeRows()));
			}
			else{
				if ( resp.numCompositeRows() > rowCount.intValue() ){
					compositeMap.put(Long.valueOf(entry.getId().longValue()), Integer.valueOf(resp.numCompositeRows()));
				}
			}
			for ( int i=0, c=resp.numCompositeRows(); i<c; i++ ){
				CompositeRow row = resp.getCompositeRow(i);
				xmlw.writeStartElement(XML_ROW);
				writeTag(XML_INDEX, Integer.toString(i), xmlw);
				for ( int j=0, d=entry.numEntries(); j<d; j++ ){
					org.psygrid.data.model.hibernate.BasicEntry be = entry.getEntry(j);
					BasicResponse br = row.getResponse(be);
					writeBasicResponse(be, br, xmlw, authorized, meta);
				}
				xmlw.writeEndElement();
			}
		}
		xmlw.writeEndElement();
	}

	/**
	 * Write a "simple" entry to the XML. A simple entry has just a name and a value.
	 * 
	 * @param name The name
	 * @param value The value
	 * @param xmlw XML writer
	 * @throws XMLStreamException
	 */
	private static void writeSimpleEntry(final String name, final String value, final XMLStreamWriter xmlw) throws XMLStreamException {
		xmlw.writeStartElement(XML_ENTRY);
		xmlw.writeStartElement(XML_NAME);
		xmlw.writeCharacters(name);
		xmlw.writeEndElement();
		xmlw.writeStartElement(XML_VALUE);
		if ( null != value ){
			xmlw.writeCharacters(value);
		}
		xmlw.writeEndElement();
		xmlw.writeEndElement();
	}

	/**
	 * Write a "simple" entry to the XML where the value is a date.
	 * 
	 * @param name The name
	 * @param date The value (date)
	 * @param xmlw XML writer
	 * @throws XMLStreamException
	 */
	private static void writeSimpleEntry(final String name, final Date date, final XMLStreamWriter xmlw) throws XMLStreamException {
		String value = null;
		if ( null != date ){
			SimpleDateFormat ddMmmYyyyFormatter = new SimpleDateFormat("dd-MMM-yyyy");
			value = ddMmmYyyyFormatter.format(date);
		}
		writeSimpleEntry(name, value, xmlw);
	}

	/**
	 * Write a tag to the XML.
	 * <p>
	 * This is a general purpose method to write any tag to the XML, consisting
	 * of an start tag with the supplied name, a value and a closing tag.
	 * 
	 * @param tag The name of the tag
	 * @param value The contents of the tag
	 * @param xmlw XML writer
	 * @throws XMLStreamException
	 */
	private static void writeTag(final String tag, final String value, final XMLStreamWriter xmlw) throws XMLStreamException{
		xmlw.writeStartElement(tag);
		if ( null != value ){
			xmlw.writeCharacters(value);
		}
		xmlw.writeEndElement();
	}

	/**
	 * Generate the metadata for the records just exported to XML.
	 * <p>
	 * The metadata contains a description of the data exported, for 
	 * example the names of the documents exported, their sections and
	 * entries.
	 * <p>
	 * Crucially it also contains information on parts of the export
	 * that may be a variable size depending upon the specifics of the
	 * data. This includes the maximum number of rows in composite (table)
	 * entrys, and the maximum number of instances of sections that permit
	 * multiple instances to be created at runtime.
	 * 
	 * @param dataSet The dataset the exported records belong to.
	 * @param meta The metadata object
	 * @param docOccs The document occurrences that were exported.
	 * @param compositeMap Map to store the maximum number of rows for each 
	 * composite entry across all the records being exported
	 * @param sectionMap Map to store section info for each section that permits 
	 * multiple instances being created at runtime. Stored the maximum number of
	 * instances, and for each instance the maximum number of rows for
	 * each composite, across all the records being exported.
	 * @throws XMLStreamException
	 */
	protected void generateMetadata(final org.psygrid.data.model.hibernate.DataSet dataSet, final org.psygrid.data.export.metadata.DataSetMetaData meta, ExportRequest request, Map<Long, Integer> compositeMap, Map<Long, SectionMap> sectionMap,List<ExportPlugin> pluginList) throws XMLStreamException {
		
		meta.setCode(dataSet.getProjectCode());
		meta.setName(dataSet.getDisplayText());

		meta.setMetaFields(new ArrayList<String>());
		meta.getMetaFields().add(NAME_STATUS);
		meta.getMetaFields().add(NAME_SITE);
		meta.getMetaFields().add(NAME_CONSULTANT);
		meta.getMetaFields().add(NAME_STUDY_ENTRY_DATE);
		meta.getMetaFields().add(NAME_SCHEDULE_START_DATE);
		if (dataSet.isRandomizationRequired()) {
			meta.getMetaFields().add(NAME_RANDOMISATION_DATE);
		}
		meta.getMetaFields().add(NAME_PRIMARY_IDENTIFIER);
		meta.getMetaFields().add(NAME_SECONDARY_IDENTIFIER);
		meta.getMetaFields().add(NAME_NOTES);
		
		// Write the columns generated by the export plugins to the metadata
		for(ExportPlugin plugin : pluginList){
			String[] columns = plugin.getColumnNames();
			for(String name:columns){
				meta.getMetaFields().add(name);				
			}
		}

		meta.setRequestor(request.getRequestor());
		Date date = new Date();
		SimpleDateFormat ddMmmYyyyFormatter = new SimpleDateFormat("dd-MMM-yyyy");

		meta.setExportDate(ddMmmYyyyFormatter.format(date));

		for ( int i=0, c=dataSet.numDocuments(); i<c; i++ ){
			Document doc = dataSet.getDocument(i);
			for ( int j=0, d=doc.numOccurrences(); j<d; j++){
				DocumentOccurrence  docOcc = doc.getOccurrence(j);
					boolean foundDoc = false;
					if ( null == request.getDocOccs()) {
						foundDoc = true;
					}
					else {
						for (ExportDocument exDoc: request.getDocOccs()) {
							if (exDoc.getDocOccId().equals(docOcc.getId())) {
								foundDoc = true;
								break;
							}
						}
					}	

					if (foundDoc) {
						org.psygrid.data.export.metadata.Document metaDoc = new org.psygrid.data.export.metadata.Document(docOcc.getId());

						boolean foundExisting = false;
						for (org.psygrid.data.export.metadata.Document existing: meta.getDocuments()) {
							if (existing.equals(metaDoc)) {
								metaDoc = existing;
								foundExisting = true;
								break;
							}
						}
						if (!foundExisting) {
							
							String documentName, studyStage = null;
							Document repDoc = docOcc.getDocument();
							if(repDoc != null){
								documentName = repDoc.getName();
								if (documentName == null){
									documentName = "Unknown";
								}else if (documentName.equals("")){
									documentName = "Unknown";
								}
							}else{
								documentName = "Unknown";
							}
							
							DocumentGroup docGroup = docOcc.getDocumentGroup();
							if(docGroup != null){
								studyStage = docGroup.getName();
								if(studyStage == null){
									studyStage = "Unknown";
								}else if (studyStage.equals("")){
									studyStage = "Unknown";
								}
							}else{
								studyStage = "Unknown";
							}
							
							
							metaDoc.setDocName(documentName);
							metaDoc.setStudyStage(studyStage);
							metaDoc.setName(docOcc.getCombinedName());						
							meta.getDocuments().add(metaDoc);
						}

						for ( int k=0, e=doc.numSections(); k<e; k++ ){
							Section section = doc.getSection(k);
							for ( int l=0, f=section.numOccurrences(); l<f; l++ ){
								SectionOccurrence secOcc = section.getOccurrence(l);
								org.psygrid.data.export.metadata.Section metaSec = new org.psygrid.data.export.metadata.Section(secOcc.getId());
								int secIndex = metaDoc.getSections().indexOf(metaSec);
								if ( secIndex >= 0 ){
									metaSec = metaDoc.getSections().get(secIndex);
								}
								else{
									metaDoc.getSections().add(metaSec);
									String secName = secOcc.getCombinedName();
									if (secName == null || secName.equals("")) {
										secName = secOcc.getCombinedDisplayText();
									}
									metaSec.setName(secName);
								}

								SectionMap sMap = null;
								if ( secOcc.isMultipleAllowed() ){
									sMap = sectionMap.get(secOcc.getId());
									int count = 0;
									if ( null != sMap ){
										count = sMap.getCount();
									}
									metaSec.setInstanceCount(count);
								}
								else{
									metaSec.setInstanceCount(1);
								}
								for ( int m=0, g=doc.numEntries(); m<g; m++ ){
									Entry entry = doc.getEntry(m);
									if (request.exportEntry(docOcc.getId(),entry.getId()) && entry.getSection().equals(section) ){
										writeMetaEntry(entry, sMap, compositeMap, metaSec);
									}
								}

							}
						}


						for (int s=0; s < metaDoc.getSections().size(); s++) {
							if (metaDoc.getSections().get(s).getEntries().size() == 0) {
								metaDoc.getSections().remove(metaDoc.getSections().get(s));
							}
						}

					}
				}
			}
		}


	/**
	 * Generate the metadata for a single entry.
	 * 
	 * @param entry The entry
	 * @param sMap Map to store section info for each section that permits 
	 * multiple instances being created at runtime. Stored the maximum number of
	 * instances, and for each instance the maximum number of rows for
	 * each composite, across all the records being exported.
	 * @param compositeMap Map to store the maximum number of rows for each 
	 * composite entry across all the records being exported
	 * @param metaSec Parent section in the metadata
	 * @throws XMLStreamException
	 */
	private static void writeMetaEntry(Entry entry, SectionMap sMap, Map<Long, Integer> compositeMap, org.psygrid.data.export.metadata.Section metaSec) throws XMLStreamException {
		if ( entry instanceof org.psygrid.data.model.hibernate.BasicEntry){
			writeMetaBasicEntry((org.psygrid.data.model.hibernate.BasicEntry)entry, metaSec);
		}
		if ( entry instanceof CompositeEntry){
			writeMetaCompositeEntry((CompositeEntry)entry, sMap, compositeMap, metaSec);
		}
	}

	/**
	 * Generate the metadata for a single basic entry.
	 * 
	 * @param entry The basic entry 
	 * @param metaSec Parent section in the metadata
	 * @throws XMLStreamException
	 */
	private static void writeMetaBasicEntry(org.psygrid.data.model.hibernate.BasicEntry entry, org.psygrid.data.export.metadata.Section metaSec) throws XMLStreamException {
		org.psygrid.data.export.metadata.BasicEntry metaEntry = new org.psygrid.data.export.metadata.BasicEntry(entry.getId());
		int entryIndex = metaSec.getEntries().indexOf(metaEntry);
		if ( entryIndex >= 0 ){
			metaEntry = (org.psygrid.data.export.metadata.BasicEntry)metaSec.getEntries().get(entryIndex);
		}
		else{
			String name = entry.getName();
			if (name == null || name.equals("")) {
				name = entry.getDisplayText();
			}
			metaEntry.setName(name);
			metaSec.getEntries().add(metaEntry);

			if ( entry.numUnits() > 0 ){
				metaEntry.setUnitPresent(true);
			}

			if ( entry instanceof OptionEntry){
				OptionEntry oe = (OptionEntry)entry;
				metaEntry.setEntryType(BasicEntry.TYPE.Option);
				for ( int i=0, c=oe.numOptions(); i<c; i++ ){
					if ( oe.getOption(i).isTextEntryAllowed() ){
						metaEntry.setExtraValuePresent(true); 
						break;
					}
				}
				for (int option = 0; option< oe.numOptions(); option++) {
					Integer code = oe.getOption(option).getCode();

					String codeStr = "";
					if (code != null) {
						codeStr = Integer.toString(code);
					}
					if (codeStr.equals("")) {
						LOG.warn("Adding an empty code for the entry '"+metaSec.getName()+" - "+entry.getDisplayText()+" : "+oe.getOption(option).getDisplayText()+"'");
					}
					String optStr = oe.getOption(option).getDisplayText();
					metaEntry.addValueLabel(codeStr, optStr);
				}
			}
			else if ( entry instanceof TextEntry){
				metaEntry.setEntryType(BasicEntry.TYPE.Text);
			}
			else if ( entry instanceof LongTextEntry){
				metaEntry.setEntryType(BasicEntry.TYPE.LongText);
			}
// TMC - Commented this out as a BasicEntry cannot be a NarrativeEntry
//			else if ( entry instanceof NarrativeEntry){
//				metaEntry.setEntryType(BasicEntry.TYPE.Narrative);
//			}
			else if ( entry instanceof NumericEntry){
				metaEntry.setEntryType(BasicEntry.TYPE.Numeric);
			}
			else if ( entry instanceof IntegerEntry){
				metaEntry.setEntryType(BasicEntry.TYPE.Integer);
			}
			else if ( entry instanceof BooleanEntry){
				metaEntry.setEntryType(BasicEntry.TYPE.Boolean);
			}
			else if ( entry instanceof DateEntry){
				metaEntry.setEntryType(BasicEntry.TYPE.Date);
			}
			else if (entry instanceof DerivedEntry){
				metaEntry.setEntryType(BasicEntry.TYPE.Numeric);
			}
			else {
				metaEntry.setEntryType(BasicEntry.TYPE.LongText);
			}

			metaEntry.setUsesMissingCodes(!entry.isDisableStandardCodes());
		}

	}

	/**
	 * Generate the metadata for a single composite (table) entry.
	 * 
	 * @param entry The composite (table) entry
	 * @param sMap Map to store section info for each section that permits 
	 * multiple instances being created at runtime. Stored the maximum number of
	 * instances, and for each instance the maximum number of rows for
	 * each composite, across all the records being exported.
	 * @param compositeMap Map to store the maximum number of rows for each 
	 * composite entry across all the records being exported
	 * @param metaSec Parent section in the metadata
	 * @throws XMLStreamException
	 */
	private static void writeMetaCompositeEntry(CompositeEntry entry, SectionMap sMap, Map<Long, Integer> compositeMap, org.psygrid.data.export.metadata.Section metaSec) throws XMLStreamException {
		org.psygrid.data.export.metadata.CompositeEntry metaComp = new org.psygrid.data.export.metadata.CompositeEntry(entry.getId());
		int entryIndex = metaSec.getEntries().indexOf(metaComp);
		if ( entryIndex >= 0 ){
			metaComp = (org.psygrid.data.export.metadata.CompositeEntry)metaSec.getEntries().get(entryIndex);
		}
		else{
			metaComp.setName(entry.getName());
			metaSec.getEntries().add(metaComp);
		}

		if ( null == sMap ){
			Integer count = compositeMap.get(entry.getId());
			if ( null == count ){
				count = Integer.valueOf(0);
			}
			metaComp.addRow(Integer.valueOf(0), Integer.valueOf(count));
		}
		else{
			for ( int i=0, c=sMap.getCount(); i<c; i++ ){
				metaComp.addRow(Integer.valueOf(i), sMap.getCompositeMap(i).get(entry.getId()));
			}
		}
		for ( int i=0, c=entry.numEntries(); i<c; i++ ){
			org.psygrid.data.model.hibernate.BasicEntry be = entry.getEntry(i);
			org.psygrid.data.export.metadata.BasicEntry metaEntry = new org.psygrid.data.export.metadata.BasicEntry(be.getId());
			int beIndex = metaComp.getEntries().indexOf(metaEntry);
			if ( beIndex >= 0 ){
				metaEntry = (org.psygrid.data.export.metadata.BasicEntry)metaComp.getEntries().get(beIndex);
			}
			else{
				metaComp.getEntries().add(metaEntry);
				metaEntry.setName(be.getName());
				if ( be.numUnits() > 0 ){
					metaEntry.setUnitPresent(true);
				}

				if ( be instanceof OptionEntry){
					OptionEntry oe = (OptionEntry)be;
					metaEntry.setEntryType(BasicEntry.TYPE.Option);
					for ( int j=0, d=oe.numOptions(); j<d; j++ ){
						if ( oe.getOption(j).isTextEntryAllowed() ){
							metaEntry.setExtraValuePresent(true); 
							break;
						}
					}
					for (int option = 0; option< oe.numOptions(); option++) {
						Integer code = oe.getOption(option).getCode();
						String codeStr = "";
						if (code != null) {
							codeStr = Integer.toString(code);
						}
						if (codeStr.equals("")) {
							LOG.warn("Adding an empty code for the entry '"+metaSec.getName()+" - "+entry.getDisplayText()+" - "+be.getDisplayText()+" : "+oe.getOption(option).getDisplayText()+"'");
						}
						metaEntry.addValueLabel(codeStr, oe.getOption(option).getDisplayText());
					}
				}
				else if ( be instanceof TextEntry){
					metaEntry.setEntryType(BasicEntry.TYPE.Text);
				}
				else if ( be instanceof LongTextEntry){
					metaEntry.setEntryType(BasicEntry.TYPE.LongText);
				}
// TMC - a basic entry cannot be a narrative entry
//				else if ( be instanceof NarrativeEntry){
//					metaEntry.setEntryType(BasicEntry.TYPE.Narrative);
//				}
				else if ( be instanceof NumericEntry){
					metaEntry.setEntryType(BasicEntry.TYPE.Numeric);
				}
				else if ( be instanceof IntegerEntry){
					metaEntry.setEntryType(BasicEntry.TYPE.Integer);
				}
				else if ( be instanceof BooleanEntry){
					metaEntry.setEntryType(BasicEntry.TYPE.Boolean);
				}
				else if ( be instanceof DateEntry){
					metaEntry.setEntryType(BasicEntry.TYPE.Date);
				}
				else {
					metaEntry.setEntryType(BasicEntry.TYPE.LongText);
				}

				metaEntry.setUsesMissingCodes(!be.isDisableStandardCodes());
			}
		}
	}


	/**
	 * Get the date of the last randomisation for the record.
	 *  
	 * @param record
	 * @return
	 */
	protected String getRandomisationDate(org.psygrid.data.model.hibernate.DataSet ds, Record record, String requestor) {
		try {
			IRemoteClient client = new RemoteClient();

			Date[] randomisations = client.getSubjectRandomisationEvents(ds.getProjectCode(), record.getIdentifier().getIdentifier(), null);
			if (randomisations.length > 0) {
				Date date = randomisations[randomisations.length-1];
				SimpleDateFormat ddMmmYyyyFormatter = new SimpleDateFormat("dd-MMM-yyyy");
				return ddMmmYyyyFormatter.format(date);
			}
		}
		catch (EslException e) {
			LOG.error("Problem occurred when calling 'getSubjectRandomisationEvents' ", e);
			return null;
		}

		return NOT_RANDOMISED;
	}

	/**
	 * Class to store information about the maximum number of rows contained
	 * in responses to each composte entry, for each instance of a section 
	 * that allows multiple runtime instances.
	 * 
	 * @author Rob Harper
	 *
	 */
	public class SectionMap {
		/**
		 * Each item in the list represents the instance of the section with
		 * the corresponding index; the map maps the id of a composite entry
		 * to the maximum number of rows contained in the responses to it.
		 */
		private List< Map<Long, Integer> > compositeMaps = new ArrayList<Map<Long, Integer>>();

		public Map<Long, Integer> getCompositeMap(int index){
			if ( index < compositeMaps.size() ){
				return compositeMaps.get(index);
			}
			Map<Long, Integer> compositeMap = new HashMap<Long, Integer>();
			compositeMaps.add(compositeMap);
			return compositeMap;
		}

		public int getCount(){
			return compositeMaps.size();
		}
	}

	/**
	 * Check that the user is authorised to view the document. If not, the document should be
	 * skipped.
	 * 
	 * @param groupCode 
	 * @param action 
	 * @return
	 */
	protected boolean checkAuthorisation(String projectCode, String groupCode, String action, String requestor) {
		return true;
	}

	/**
	 * Check that the user is authorised to view the response. If not, the response should be
	 * obscured.
	 * 
	 * @param response
	 * @param groupCode 
	 * @return
	 */
	protected boolean checkAuthForResponse(Response response, String projectCode, String groupCode, String requestor){
		return true;
	}

	/**
	 * This method sets the entry's required export security action. This is done by inspecting its security tag
	 * and finding the corresponding action for this action in the exportActionslist.
	 * 
	 * If the the entry has no tag (it is null), then the data is exported as-is in the database.
	 * If the exportActionsList is null or incomplete, the data will be exported restricted (most secure).
	 * 
	 * @param entry - the entry whose security tag is to be inspected.
	 * @param document - passed in so that the document's export security tag can be checked and the appropriate action taken
	 *
	 */
	protected void setRequiredAction(org.psygrid.data.model.hibernate.DataSet ds, Entry entry, Document document,List<ExportSecurityActionMap> actionMap) {
		//default implementation - do nothing
	}

	/**
	 * Modify the value of a response as appropriate depending upon the
	 * export action defined for it.
	 * 
	 * @param resp The response.
	 * @throws DAOException
	 * @throws RemoteException
	 * @throws TransformerException
	 */
	protected void takeRequiredSecurityAction(org.psygrid.data.model.hibernate.DataSet ds,Response resp) throws DAOException, RemoteException, TransformerException {
		//default implementation - do nothing
	}
	
	private boolean hasDataForColumn(Properties pluginResults, String column) {
		return pluginResults.get(column)!=null;
	}

}

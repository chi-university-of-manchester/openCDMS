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

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.data.export.metadata.BasicEntry;
import org.psygrid.data.export.metadata.CompositeEntry;
import org.psygrid.data.export.metadata.DataSetMetaData;
import org.psygrid.data.export.metadata.Document;
import org.psygrid.data.export.metadata.Entry;
import org.psygrid.data.export.metadata.Section;
import org.psygrid.data.export.scheduling.ExportJob;

import com.csvreader.CsvWriter;

/**
 * Methods to convert the intermediate XML files 
 * produced in the first stage of a data export into the final
 * output file(s) for the user.
 * 
 * @author Rob Harper
 *
 */
public class CsvExportFormatter {
	
	private static final Log LOG = LogFactory.getLog(CsvExportFormatter.class);

	public static final char CSV_DELIMITER = ',';
	public static final String CSV_SUFFIX = ".csv";
	public static final String METADATA_FILE = "_metadata";

	private static final Long METADATA_ID = Long.valueOf(-1);
	
	/**
	 * Transform the intermediate XML export files into a single CSV
	 * file.
	 * 
	 * @param inputPaths The paths of the XML files
	 * @param metaData The export metadata.
	 * @param outputPath The path of the output CSV file
	 * @return List of the paths of the output CSV files
	 * @throws XMLStreamException
	 * @throws IOException
	 */
	public static List<File> toSingleCSV(File[] inputPaths, DataSetMetaData metaData, File outputPath, 
			boolean showHeaders, boolean showCodes, boolean showValues) throws XMLStreamException, IOException {
		return toSingleCSV(inputPaths, metaData, outputPath, null, showHeaders, showCodes, showValues);
	}
	
	/**
	 * Transform the intermediate XML export files into a single CSV
	 * file.
	 * 
	 * @param inputPaths The paths of the XML files
	 * @param metaData The export metadata.
	 * @param outputPath The path of the output CSV file
	 * @param sysMissing The system missing value to substitute dataset missing values for
	 * @return List of the paths of the output CSV files
	 * @throws XMLStreamException
	 * @throws IOException
	 */
	public static List<File> toSingleCSV(File[] inputPaths, DataSetMetaData metaData, File outputPath, String sysMissing, 
			boolean showHeaders, boolean showCodes, boolean showValues) throws XMLStreamException, IOException {
		CsvWriter output = null;
		List<File> outFiles = new ArrayList<File>();
		outFiles.add(outputPath);
		try{
			XMLInputFactory factory = XMLInputFactory.newInstance();
			factory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
			output = new CustomCsvWriter(new FileOutputStream(outputPath), CSV_DELIMITER, Charset.defaultCharset());
			
			if (showHeaders) {
				writeUserData(metaData, output);
				writeHeaders(metaData, output, showCodes,showValues);
			}
			for ( File inputPath: inputPaths ){
				FileReader inFile = null;
				XMLStreamReader input = null;
				try{
					inFile = new FileReader(inputPath);

					long filesize = inputPath.length();
					if (filesize == 0) {
						continue;
					}
					
					input = factory.createXMLStreamReader(inFile);
					while ( true ){
						int event = input.next();
						if ( XMLStreamConstants.END_DOCUMENT ==  event ){
							break;
						}

						if ( XMLStreamConstants.START_ELEMENT == event ){
							if ( XMLExporter.XML_PARTICIPANT.equals(input.getLocalName()) ){
								processParticipant(input, metaData, output, sysMissing,showCodes,showValues);
							}
						}

					}
				}
				finally{
					if ( null != input ){
						input.close();
					}
					if ( null != inFile ){
						inFile.close();
					}
				}
			}
		}
		finally{
			if ( null != output ){
				output.close();
			}
		}
		return outFiles;
	}

	/**
	 * Transform the intermediate XML export files into a collection
	 * of CSV files, one for each document occurrence that was requested
	 * in the export.
	 * 
	 * @param inputPaths The paths of the XML files
	 * @param metaData The export metadata.
	 * @param outputPathStart Prefix for the output CSV files
	 * @return List of the paths of the output CSV files
	 * @throws XMLStreamException
	 * @throws IOException
	 */
	public static List<File> toMultipleCsv(File[] inputPaths, DataSetMetaData metaData, String outputPathStart, 
			boolean showHeaders, boolean showCodes, boolean showValues) throws XMLStreamException, IOException {
		Map<Long, CsvWriter> writers = new HashMap<Long, CsvWriter>();
		List<File> outFiles = new ArrayList<File>();
		try{

			XMLInputFactory factory = XMLInputFactory.newInstance();

			File mdFileName = new File(outputPathStart+METADATA_FILE+CSV_SUFFIX);
			outFiles.add(mdFileName);
			writers.put(METADATA_ID, new CustomCsvWriter(new FileOutputStream(mdFileName), CSV_DELIMITER, Charset.defaultCharset()));
			for ( Document metaDoc : metaData.getDocuments() ){
				File filename = new File(outputPathStart + "_" + createCsvFileName(metaDoc.getName()) + CSV_SUFFIX);
				outFiles.add(filename);
				writers.put(metaDoc.getId(), new CustomCsvWriter(new FileOutputStream(filename), CSV_DELIMITER, Charset.defaultCharset()));
			}

			if (showHeaders) {
				writeUserData(metaData, writers);
				writeHeaders(metaData, writers,showCodes,showValues);
			}
			for ( File inputPath: inputPaths ){

				FileReader inFile = null;
				XMLStreamReader input = null;
				try{
					inFile = new FileReader(inputPath);
					input = factory.createXMLStreamReader(inFile);

					while ( true ){
						int event = input.next();
						if ( XMLStreamConstants.END_DOCUMENT ==  event ){
							break;
						}

						if ( XMLStreamConstants.START_ELEMENT == event ){
							if ( XMLExporter.XML_PARTICIPANT.equals(input.getLocalName()) ){
								processParticipant(input, metaData, writers,showCodes,showValues);
							}
						}

					}
				}
				finally{
					if ( null != input ){
						input.close();
					}
					if ( null != inFile ){
						inFile.close();
					}
				}
			}

		}
		finally{
			for ( Map.Entry<Long, CsvWriter> e: writers.entrySet() ){
				if ( null != e.getValue() ){
					e.getValue().close();
				}
			}
		}
		return outFiles;
	}


	private static void writeUserData(final DataSetMetaData metaData, final CsvWriter output) throws IOException {
		output.write("Requestor");
		output.write(metaData.getRequestor());
		output.endRecord();

		output.write("Exported Date");
		output.write(metaData.getExportDate());
		output.endRecord();
	}

	private static void writeHeaders(final DataSetMetaData metaData, final CsvWriter output, boolean showCodes, boolean showValues) throws IOException {
		//3 rows of headers - documents, sections, entries
		//Pass 1 - document line
		output.write(null); //spacer for the identifier
		for ( String name: metaData.getMetaFields()){
			output.write(null);
		}
		for ( Document doc: metaData.getDocuments() ){
			String name = doc.getName();
			for ( int i=0, c=doc.columnCount(showCodes, showValues); i<c; i++ ){
				output.write(name);
			}
		}
		output.endRecord();
		//Pass 2 - section line
		output.write(null); //spacer for the identifier
		for ( String name: metaData.getMetaFields()){
			output.write(null);
		}
		for ( Document doc: metaData.getDocuments() ){
			output.write(null); //spacer for the doc status
			for ( Section sec: doc.getSections() ){
				for ( int i=0, c=sec.getInstanceCount(); i<c; i++ ){
					String secSuffix = "";
					if ( c > 1 ){
						secSuffix = " - " + Integer.toString(i+1);
					}
					String name = sec.getName() + secSuffix;
					for ( int j=0, d=sec.columnCount(i, showCodes, showValues); j<d; j++ ){
						output.write(name);
					}
				}
			}
		}
		output.endRecord();
		//Pass 3 - entry line
		output.write("Identifier");
		for ( String name: metaData.getMetaFields()){
			output.write(name);
		}
		for ( Document doc: metaData.getDocuments() ){
			output.write("Status");
			for ( Section sec: doc.getSections() ){
				for ( int i=0, c=sec.getInstanceCount(); i<c; i++ ){
					for ( Entry e: sec.getEntries() ){
						e.writeHeader(i, output, showCodes, showValues);
					}
				}
			}
		}
		output.endRecord();
	}

	private static void writeUserData(final DataSetMetaData metaData, final Map<Long, CsvWriter> writers) throws IOException {
		CsvWriter output = writers.get(METADATA_ID);
		output.write("Requestor");
		output.write(metaData.getRequestor());
		output.endRecord();

		output.write("Exported Date");
		output.write(metaData.getExportDate());
		output.endRecord();
	}

	private static void writeHeaders(final DataSetMetaData metaData, final Map<Long, CsvWriter> writers,boolean showCodes, boolean showValues) throws IOException {

		//metadata
		CsvWriter metaWriter = writers.get(METADATA_ID);
		metaWriter.write(null);
		for ( String name: metaData.getMetaFields()){
			metaWriter.write(null);
		}
		metaWriter.endRecord();
		metaWriter.write("Identifier");
		for ( String name: metaData.getMetaFields()){
			metaWriter.write(name);
		}
		metaWriter.endRecord();

		//documents
		for ( Document metaDoc: metaData.getDocuments() ){
			CsvWriter writer = writers.get(metaDoc.getId());
			//Pass 1 - section line
			writer.write(null); //spacer for the identifier
			writer.write(null); //spacer for the doc status
			for ( Section sec: metaDoc.getSections() ){
				for ( int i=0, c=sec.getInstanceCount(); i<c; i++ ){
					String secSuffix = "";
					if ( c > 1 ){
						secSuffix = " - " + Integer.toString(i+1);
					}
					String name = sec.getName() + secSuffix;
					for ( int j=0, d=sec.columnCount(i, showCodes, showValues); j<d; j++ ){
						writer.write(name);
					}
				}
			}
			writer.endRecord();

			//Pass 2 - entry line
			writer.write("Identifier");
			writer.write("Status");
			for ( Section sec: metaDoc.getSections() ){
				for ( int i=0, c=sec.getInstanceCount(); i<c; i++ ){
					for ( Entry e: sec.getEntries() ){
						e.writeHeader(i, writer, showCodes, showValues);
					}
				}
			}
			writer.endRecord();

		}


	}

	private static void processParticipant(final XMLStreamReader input, final DataSetMetaData metaData, final CsvWriter output, final String sysMissing,
			boolean showCodes, boolean showValues) throws XMLStreamException, IOException {
		int documentCount = 0;
		while ( true ){
			int event = input.next();

			if ( XMLStreamConstants.END_DOCUMENT ==  event || 
					XMLStreamConstants.END_ELEMENT ==  event && XMLExporter.XML_PARTICIPANT.equals(input.getLocalName())){
				break;
			}

			if ( XMLStreamConstants.START_ELEMENT == event ){
				if ( XMLExporter.XML_IDENTIFIER.equals(input.getLocalName()) ){
					String identifier = getContents(input);
					
					LOG.info("Processing participant: " + identifier);
					
					output.write(identifier);
					documentCount = 0;
				}
				if ( XMLExporter.XML_METADATA.equals(input.getLocalName()) ){
					processMetadata(input, output);
				}
				if ( XMLExporter.XML_DOCUMENT.equals(input.getLocalName()) ){
					documentCount = processDocument(input, metaData, output, documentCount, sysMissing, showCodes, showValues);
				}
			}

		}

		while (documentCount < metaData.getDocuments().size()) {
			//Add padding so that each line in the CSV file is the same length (required for STATA)
			pad(metaData.getDocuments().get(documentCount).columnCount(showCodes, showValues), output);
			documentCount++;
		}
		
		//move to next line
		output.endRecord();

	}

	private static void processParticipant(final XMLStreamReader input, final DataSetMetaData metaData, final Map<Long, CsvWriter> writers,
			boolean showCodes, boolean showValues) throws XMLStreamException, IOException {
		while ( true ){
			int event = input.next();

			if ( XMLStreamConstants.END_DOCUMENT ==  event || 
					XMLStreamConstants.END_ELEMENT ==  event && XMLExporter.XML_PARTICIPANT.equals(input.getLocalName())){
				break;
			}

			if ( XMLStreamConstants.START_ELEMENT == event ){
				if ( XMLExporter.XML_IDENTIFIER.equals(input.getLocalName()) ){
					String identifier = getContents(input);
					
					LOG.info("Processing participant: " + identifier);
					
					for ( Map.Entry<Long, CsvWriter> e: writers.entrySet() ){
						e.getValue().write(identifier);
					}
				}
				if ( XMLExporter.XML_METADATA.equals(input.getLocalName()) ){
					processMetadata(input, writers.get(METADATA_ID));
				}
				if ( XMLExporter.XML_DOCUMENT.equals(input.getLocalName()) ){
					processDocument(input, metaData, writers,showCodes,showValues);
				}
			}

		}
		
		//move to next line
		for ( Map.Entry<Long, CsvWriter> e: writers.entrySet() ){
			e.getValue().endRecord();
		}

	}

	private static void processMetadata(final XMLStreamReader input, final CsvWriter output) throws XMLStreamException, IOException {
		while ( true ){
			int event = input.next();

			if ( XMLStreamConstants.END_DOCUMENT ==  event || 
					XMLStreamConstants.END_ELEMENT ==  event && XMLExporter.XML_METADATA.equals(input.getLocalName()) ){
				break;
			}

			if ( XMLStreamConstants.START_ELEMENT == event ){
				if ( XMLExporter.XML_VALUE.equals(input.getLocalName()) ){
					output.write(getContents(input));
				}
			}
		}		
	}

	private static int processDocument(final XMLStreamReader input, final DataSetMetaData metaData, final CsvWriter output, int documentCount, final String sysMissing,
			boolean showCodes, boolean showValues) throws XMLStreamException, IOException {
		Document metaDoc = null;
		boolean documentMissing = true;
		while ( true ){
			int event = input.next();

			if ( XMLStreamConstants.END_DOCUMENT ==  event || 
					XMLStreamConstants.END_ELEMENT ==  event && XMLExporter.XML_DOCUMENT.equals(input.getLocalName())){
				break;
			}

			if ( XMLStreamConstants.START_ELEMENT == event ){
				if ( XMLExporter.XML_ID.equals(input.getLocalName()) ){
					String id = getContents(input);
					metaDoc = metaData.findDocumentById(Long.valueOf(id));
					
					if(metaDoc != null)
						LOG.info("Processing document: " +  metaDoc.getDocName());
				
					while (!metaDoc.equals(metaData.getDocuments().get(documentCount))) {
						//Documents may be missing so add padding until the next document is found..
						//This relies on the documents in the XMLExporter being in the same order as the MetaData documents
						pad(metaData.getDocuments().get(documentCount).columnCount(showCodes, showValues), output);
						documentCount++;
					}
					documentCount++;
				}

				if ( XMLExporter.XML_STATUS.equals(input.getLocalName()) ){
					output.write(getContents(input));
					documentMissing = false;
				}

				if ( XMLExporter.XML_SECTION.equals(input.getLocalName()) ){
					processSection(input, metaDoc, output, sysMissing,showCodes,showValues);
				}
			}
		}		

		if ( documentMissing ){
			//pad out the csv file
			pad(metaDoc.columnCount(showCodes, showValues), output);
		}

		return documentCount;
	}

	private static void processDocument(final XMLStreamReader input, final DataSetMetaData metaData, final Map<Long, CsvWriter> writers,
			boolean showCodes, boolean showValues) throws XMLStreamException, IOException {

		Document metaDoc = null;
		CsvWriter output = null;
		boolean documentMissing = true;
		while ( true ){
			int event = input.next();

			if ( XMLStreamConstants.END_DOCUMENT ==  event || 
					XMLStreamConstants.END_ELEMENT ==  event && XMLExporter.XML_DOCUMENT.equals(input.getLocalName())){
				break;
			}

			if ( XMLStreamConstants.START_ELEMENT == event ){
				if ( XMLExporter.XML_ID.equals(input.getLocalName()) ){
					String id = getContents(input);
					output = writers.get(Long.valueOf(id));
					metaDoc = metaData.findDocumentById(Long.valueOf(id));
					
					if(metaDoc != null)
						LOG.info("Processing document: " +  metaDoc.getDocName());
					
				}

				if ( XMLExporter.XML_STATUS.equals(input.getLocalName()) ){
					output.write(getContents(input));
					documentMissing = false;
				}

				if ( XMLExporter.XML_SECTION.equals(input.getLocalName()) ){
					processSection(input, metaDoc, output, null,showCodes,showValues);
				}
			}
		}		

		if ( documentMissing ){
			//pad out the csv file
			pad(metaDoc.columnCount(showCodes, showValues), output);
		}
	}

	private static void processSection(final XMLStreamReader input, final Document metaDoc, final CsvWriter output, final String sysMissing,
			boolean showCodes, boolean showValues) throws XMLStreamException, IOException {
		Section metaSec = null;
		int instanceCount = 0;
		while ( true ){
			int event = input.next();

			if ( XMLStreamConstants.END_DOCUMENT ==  event || 
					XMLStreamConstants.END_ELEMENT ==  event && XMLExporter.XML_SECTION.equals(input.getLocalName())){
				break;
			}

			if ( XMLStreamConstants.START_ELEMENT == event ){
				if ( XMLExporter.XML_ID.equals(input.getLocalName()) ){
					String id = getContents(input);
					metaSec = metaDoc.findSectionById(Long.valueOf(id));
					
					if(metaSec == null)
						LOG.info("Did not find metaSec for id: "  + id);
					
				}

				if ( XMLExporter.XML_INSTANCE.equals(input.getLocalName()) ){
					processSectionInstance(input, metaSec, output, sysMissing, showCodes, showValues);
					instanceCount++;
				}
			}
		}

		//pad for missing section instances
		if(metaSec != null){
			//checking for null is a fix for bug #1380.
			//If it is null, it is ASSUMED that this is so because the section contains no exportable data
			//(e.g. it contains only a narrative entry)
			
			for ( int i=instanceCount; i<metaSec.getInstanceCount(); i++ ){
				pad(metaSec.columnCount(i, showCodes, showValues), output);
			}
		}
		
	}

	private static void processSectionInstance(final XMLStreamReader input, final Section metaSec, final CsvWriter output, final String sysMissing,
			boolean showCodes, boolean showValues) throws XMLStreamException, IOException {
		int index = -1;
		while ( true ){
			int event = input.next();

			if ( XMLStreamConstants.END_DOCUMENT ==  event || 
					XMLStreamConstants.END_ELEMENT ==  event && XMLExporter.XML_INSTANCE.equals(input.getLocalName())){
				break;
			}

			if ( XMLStreamConstants.START_ELEMENT == event ){
				if ( XMLExporter.XML_INDEX.equals(input.getLocalName()) ){
					index = Integer.parseInt(getContents(input));
				}

				if ( XMLExporter.XML_ENTRY.equals(input.getLocalName()) ){
					processEntry(input, metaSec, output, sysMissing,showCodes,showValues);
				}

				if ( XMLExporter.XML_COMPOSITE.equals(input.getLocalName()) ){
					processComposite(input, metaSec, index, output, sysMissing, showCodes, showValues);
				}

			}
		}
	}	

	private static void processEntry(final XMLStreamReader input, final Section metaSec, final CsvWriter output, final String sysMissing,
			boolean showCodes, boolean showValues) throws XMLStreamException, IOException {
		BasicEntry metaEntry = null;
		String value = null;
		boolean isMissing = false;
		while ( true ){
			int event = input.next();

			if ( XMLStreamConstants.END_DOCUMENT ==  event || 
					XMLStreamConstants.END_ELEMENT ==  event && XMLExporter.XML_ENTRY.equals(input.getLocalName())){
				break;
			}

			if ( XMLStreamConstants.START_ELEMENT == event ){
				if ( XMLExporter.XML_ID.equals(input.getLocalName()) ){
					String id = getContents(input);
					metaEntry = (BasicEntry)metaSec.findEntryById(Long.valueOf(id));
				}
				
				if ( XMLExporter.XML_IS_MISSING.equals(input.getLocalName())){
					String missing = getContents(input);
					if (missing != null && !missing.equals("")) {
						isMissing = Boolean.parseBoolean(missing);
					}
				}
				
				if ( XMLExporter.XML_VALUE.equals(input.getLocalName())){
					value = getContents(input);
					if (showValues) {
						if (isMissing && sysMissing != null) {
							value = sysMissing;	//Replace with the system missing value
						}
						output.write(value);
					}
				}

				if ( XMLExporter.XML_CODE.equals(input.getLocalName())){
					String code = getContents(input);
					if (showCodes) {
						if (isMissing && sysMissing != null) {
							code = sysMissing;	//Replace with the system missing value
						}
						else if (!showValues && (code == null)) {
								code = value;	//Where a value doesn't have an associated code, the value should still be shown.
						}
						output.write(code);
					}
				}

				if ( XMLExporter.XML_UNIT.equals(input.getLocalName()) && metaEntry.isUnitPresent() ){
					output.write(getContents(input));
				}

				if ( XMLExporter.XML_EXTRA.equals(input.getLocalName()) && metaEntry.isExtraValuePresent() ){
					output.write(getContents(input));
				}
			}
		}		
	}

	private static void processComposite(final XMLStreamReader input, final Section metaSec, final int instanceIndex, final CsvWriter output, final String sysMissing,
			boolean showCodes, boolean showValues) throws XMLStreamException, IOException {

		CompositeEntry metaEntry = null;
		int rowCount = 0;
		while ( true ){
			int event = input.next();

			if ( XMLStreamConstants.END_DOCUMENT ==  event || 
					XMLStreamConstants.END_ELEMENT ==  event && XMLExporter.XML_COMPOSITE.equals(input.getLocalName())){
				break;
			}

			if ( XMLStreamConstants.START_ELEMENT == event ){
				if ( XMLExporter.XML_ID.equals(input.getLocalName()) ){
					String id = getContents(input);
					metaEntry = (CompositeEntry)metaSec.findEntryById(Long.valueOf(id));
				}

				if ( XMLExporter.XML_ROW.equals(input.getLocalName()) ){
					processCompositeRow(input, metaEntry, output, sysMissing, showCodes, showValues);
					rowCount++;
				}
			}
		}
		//pad for missing rows
		for ( int i=rowCount; i<metaEntry.getRowCount(instanceIndex); i++ ){
			pad(metaEntry.columnsPerRow(showCodes, showValues), output);
		}
	}

	private static void processCompositeRow(final XMLStreamReader input, final CompositeEntry metaComp, final CsvWriter output, final String sysMissing,
			boolean showCodes, boolean showValues) throws XMLStreamException, IOException {
		while ( true ){
			int event = input.next();

			if ( XMLStreamConstants.END_DOCUMENT ==  event || 
					XMLStreamConstants.END_ELEMENT ==  event && XMLExporter.XML_ROW.equals(input.getLocalName())){
				break;
			}

			if ( XMLStreamConstants.START_ELEMENT == event ){
				if ( XMLExporter.XML_ENTRY.equals(input.getLocalName()) ){
					processEntryInComposite(input, metaComp, output, sysMissing, showCodes, showValues);
				}
			}
		}
	}

	private static void processEntryInComposite(final XMLStreamReader input, final CompositeEntry metaComp, final CsvWriter output, final String sysMissing,
			boolean showCodes, boolean showValues) throws XMLStreamException, IOException {
		BasicEntry metaEntry = null;
		String value = null;
		boolean isMissing = false;
		while ( true ){
			int event = input.next();

			if ( XMLStreamConstants.END_DOCUMENT ==  event || 
					XMLStreamConstants.END_ELEMENT ==  event && XMLExporter.XML_ENTRY.equals(input.getLocalName())){
				break;
			}

			if ( XMLStreamConstants.START_ELEMENT == event ){
				if ( XMLExporter.XML_ID.equals(input.getLocalName()) ){
					String id = getContents(input);
					metaEntry = metaComp.findEntryById(Long.valueOf(id));
				}
				
				if ( XMLExporter.XML_IS_MISSING.equals(input.getLocalName())){
					String missing = getContents(input);
					if (missing != null && !missing.equals("")) {
						isMissing = Boolean.parseBoolean(missing);
					}
				}
				
				if ( XMLExporter.XML_VALUE.equals(input.getLocalName())){
					value = getContents(input);
					if (showValues) {
						if (isMissing && sysMissing != null) {
							value = sysMissing;	//Replace with the system missing value
						}
						output.write(value);
					}
				}

				if ( XMLExporter.XML_CODE.equals(input.getLocalName())){
					String code = getContents(input);
					if (showCodes) {
						if (isMissing && sysMissing != null) {
							code = sysMissing;	//Replace with the system missing value
						}
						else if (!showValues && (code == null)) {
							code = value;	//Where a value doesn't have an associated code, the value should still be shown.
						}
						output.write(code);
					}
				}

				if ( XMLExporter.XML_UNIT.equals(input.getLocalName()) && metaEntry.isUnitPresent() ){
					output.write(getContents(input));
				}

				if ( XMLExporter.XML_EXTRA.equals(input.getLocalName()) && metaEntry.isExtraValuePresent() ){
					output.write(getContents(input));
				}
			}
		}		
	}



	private static String getContents(final XMLStreamReader input) throws XMLStreamException {
		String contents = null;
		while ( true ){
			int event = input.next();
			if ( XMLStreamConstants.END_DOCUMENT ==  event || 
					XMLStreamConstants.END_ELEMENT ==  event ){
				break;
			}
			if ( XMLStreamConstants.CHARACTERS == event ){
				contents = input.getText();
			}

		}
		return contents;
	}

	private static void pad(int count, CsvWriter output) throws IOException {
		for ( int i=0; i<count; i++ ){
			output.write(null);
		}
	}

	public static String createCsvFileName(String in){
		String temp = in.replace("/", "_");
		return temp.replace(" ", "_");
	}

}

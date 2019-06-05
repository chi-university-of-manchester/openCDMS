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
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import org.psygrid.data.export.metadata.BasicEntry;
import org.psygrid.data.export.metadata.CompositeEntry;
import org.psygrid.data.export.metadata.DataSetMetaData;
import org.psygrid.data.export.metadata.Document;
import org.psygrid.data.export.metadata.Entry;
import org.psygrid.data.export.metadata.Section;

/**
 * Class containing methods to convert the intermediate XML files 
 * produced in the first stage of a data export into an Excel document.
 * 
 * @author Lucy Bridges
 *
 */
public class ExcelExportFormatter {

	public static final String EXCEL_SUFFIX = ".xls";

	public static final int MAX_COLS = 256;	

	private boolean showCodes   = true;
	private boolean showValues  = true;
	private boolean showHeaders = true;

	private WritableWorkbook workbook = null;

	/**
	 * The worksheet currently being processed 
	 */
	private int documentCount = 0;

	private String currentIdentifier = null;
	private String currentDocStatus = null;

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
	public List<File> toExcelDocument(File[] inputPaths, DataSetMetaData metaData, File outputPath) throws XMLStreamException, IOException, WriteException {

		List<File> outFiles = new ArrayList<File>();
		outFiles.add(outputPath);

		int maxCols = MAX_COLS;//		Max cols in Excel is 256, minus the 2 data columns at the start of the sheet

		try{
			XMLInputFactory factory = XMLInputFactory.newInstance();
			factory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
			workbook = Workbook.createWorkbook(new FileOutputStream(outputPath));

			//create the general sheet, with the exporter's details
			WritableSheet generalSheet = workbook.createSheet("General", workbook.getNumberOfSheets());
			int subjectCounter = 0;
			if (showHeaders) {		
				subjectCounter = writeUserData(metaData, generalSheet);
			}
			List<WritableSheet> sheets = new ArrayList<WritableSheet>();
			List<Integer> sheetCounters = new ArrayList<Integer>();
			for (Document document: metaData.getDocuments()) {
				//Get total columns, inc identifier and status
				int totalCols = document.columnCount(showCodes, showValues);
				boolean partNames = ((totalCols/maxCols) > 0);

				//Split large documents into several sheets
				for (int i = 0; i <= (totalCols/maxCols); i++) {
					String docName = document.getName();
					if (partNames) {
						docName += " - part "+(i+1);
					}
					WritableSheet sheet = workbook.createSheet(docName, workbook.getNumberOfSheets());
					sheets.add(sheet);

					if (showHeaders) {
						int max = totalCols;
						if (maxCols*(i+1) < totalCols) {
							max = (maxCols*(i+1))-1;
						}
						int row = writeHeaders(document, sheet, maxCols*i, max);
						sheetCounters.add(row);
					}
					else {
						sheetCounters.add(0);
					}
				}
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
								subjectCounter = processParticipant(input, metaData, sheets, sheetCounters, generalSheet, subjectCounter);
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
			if ( null != workbook ){
				workbook.write();
				workbook.close();
			}
		}
		return outFiles;
	}

	public boolean isShowCodes() {
		return showCodes;
	}

	public void setShowCodes(boolean showCodes) {
		this.showCodes = showCodes;
	}

	public boolean isShowValues() {
		return showValues;
	}

	public void setShowValues(boolean showValues) {
		this.showValues = showValues;
	}

	public boolean isShowHeaders() {
		return showHeaders;
	}

	public void setShowHeaders(boolean showHeaders) {
		this.showHeaders = showHeaders;
	}

	private int writeUserData(final DataSetMetaData metaData, final WritableSheet sheet) throws IOException, WriteException {
		int row = 0;
		sheet.addCell(new Label(0, row, "Requestor"));
		sheet.addCell(new Label(1, row, metaData.getRequestor()));

		row++;
		sheet.addCell(new Label(0, row, "Exported Date"));
		sheet.addCell(new Label(1, row, metaData.getExportDate()));

		row += 2;

		int col = 0;
		sheet.addCell(new Label(col, row, "Identifier"));
		col++;
		for ( String name: metaData.getMetaFields()){
			sheet.addCell(new Label(col, row, name));
			col++;
		}
		return ++row;
	}

	private int writeHeaders(final Document document, final WritableSheet sheet, int start, int end) throws IOException, WriteException {
		//2 rows of headers - sections, entries
		int row = 0; 
		int column = 0;
		int counter = 2;
		//Pass 1 - section line
		sheet.addCell(new Label(column++, row, ""));	//spacer for the identifier
		sheet.addCell(new Label(column++, row, "")); //spacer for the doc status
		nextSec : for ( Section sec: document.getSections() ){
			for ( int i=0, c=sec.getInstanceCount(); i<c; i++ ){
				String secName = sec.getName() + Integer.toString(i);
				nextCol: for ( int j=0, d=sec.columnCount(i, showCodes, showValues); j<d; j++ ){
					if (counter < start) {
						counter++;
						continue nextCol;
					}
					if (counter > end) {
						break nextSec;
					}
					sheet.addCell(new Label(column++, row, secName));
					counter++;
				}
			}
		}
		row++;
		column = 0;	//The column on the current sheet
		counter = 2;//Takes into account the columns from previous sheets for this document
		//Pass 2 - entry line
		sheet.addCell(new Label(column++, row, "Identifier"));
		sheet.addCell(new Label(column++, row, "Status"));
		for ( Section sec: document.getSections() ){
			for ( int i=0, c=sec.getInstanceCount(); i < c; i++ ){
				nextEntry: for ( Entry e: sec.getEntries() ){
					int entryStart = 0;
					if (counter < start) {
						int entryColCount = e.columnCount(i, showCodes, showValues);
						if ((counter + entryColCount) < start) {
							//Still not enough, so move to the next entry.
							counter += entryColCount;
							continue nextEntry;
						}
						else {
							//Begin partway through
							entryStart = (start - counter); 
							counter = start;
						}
					}
					else if (counter > end) {
						return ++row;
					}

					int oldColumn = column;
					if (e instanceof BasicEntry) {
						column = writeBasicEntry((BasicEntry)e, column, row, sheet, counter, end, entryStart);
					}
					else if (e instanceof CompositeEntry) {
						column = writeCompositeEntry((CompositeEntry)e, column, row, i, sheet, counter, end, entryStart);
					}
					counter += (column - oldColumn);
				}
			}
		}
		return ++row;
	}

	private int writeBasicEntry(BasicEntry entry, int column, int row, WritableSheet sheet, int currentCol, int maxCol, int startAt) throws WriteException {
		if (showValues) {
			if (currentCol <= maxCol && startAt == 0) {
				sheet.addCell(new Label(column++, row, entry.getName() + Entry.VALUE_COLUMN));
				currentCol++;
			}
		}
		if (showCodes) {
			if (currentCol <= maxCol && startAt < 2) {
				sheet.addCell(new Label(column++, row, entry.getName() + Entry.CODE_COLUMN));
				currentCol++;
			}
		}
		if ( entry.isUnitPresent()){
			if (currentCol <= maxCol && startAt < 3) {
				sheet.addCell(new Label(column++, row, entry.getName() + Entry.UNIT_COLUMN));
				currentCol++;
			}
		}
		if ( entry.isExtraValuePresent()){
			if (currentCol <= maxCol && startAt < 4) {
				sheet.addCell(new Label(column++, row, entry.getName() + Entry.EXTRA_COLUMN));
				currentCol++;
			}
		}
		return column;
	}

	private int writeCompositeEntry(CompositeEntry composite, int column, int row, int count, WritableSheet sheet, int currentCol, int maxCol, int startAt) throws WriteException  {
		int rowCount = composite.getRowCount(count);
		int entryCounter = 0;
		for ( int i=0; i<rowCount; i++ ){
			String rowSuffix = Integer.toString(i+1);
			for ( BasicEntry e: composite.getEntries() ){
				if (showValues) {
					if (currentCol <= maxCol && startAt <= entryCounter) {
						sheet.addCell(new Label(column++, row, composite.getName() + Entry.SEPARATOR + e.getName() + Entry.VALUE_COLUMN + Entry.SEPARATOR + rowSuffix));
						currentCol++;
					}
					entryCounter++;
				}
				if (showCodes) {
					if (currentCol <= maxCol && startAt <= entryCounter) {
						sheet.addCell(new Label(column++, row, composite.getName() + Entry.SEPARATOR + e.getName() + Entry.CODE_COLUMN + Entry.SEPARATOR + rowSuffix));
						currentCol++;
					}
					entryCounter++;
				}
				if ( e.isUnitPresent()){
					if (currentCol <= maxCol && startAt <= entryCounter) {
						sheet.addCell(new Label(column++, row, composite.getName() + Entry.SEPARATOR + e.getName() + Entry.UNIT_COLUMN + Entry.SEPARATOR + rowSuffix));
						currentCol++;
					}
					entryCounter++;
				}
				if ( e.isExtraValuePresent()){
					if (currentCol <= maxCol && startAt <= entryCounter) {
						sheet.addCell(new Label(column++, row, composite.getName() + Entry.SEPARATOR + e.getName() + Entry.EXTRA_COLUMN + Entry.SEPARATOR + rowSuffix));
						currentCol++;
					}
					entryCounter++;
				}
			}
		}
		return column;
	}

	private int processParticipant(final XMLStreamReader input, final DataSetMetaData metaData, final List<WritableSheet> sheets, List<Integer> sheetCounters, final WritableSheet generalSheet, int subjectCounter) 
	throws XMLStreamException, IOException, WriteException {
		documentCount = 0;
		while ( true ){
			int event = input.next();

			if ( XMLStreamConstants.END_DOCUMENT ==  event || 
					XMLStreamConstants.END_ELEMENT ==  event && XMLExporter.XML_PARTICIPANT.equals(input.getLocalName())){
				break;
			}

			if ( XMLStreamConstants.START_ELEMENT == event ){
				if ( XMLExporter.XML_IDENTIFIER.equals(input.getLocalName()) ){
					currentIdentifier = getContents(input);
					writeUser(currentIdentifier, sheets.get(documentCount), sheetCounters.get(documentCount));
				}
				if ( XMLExporter.XML_METADATA.equals(input.getLocalName()) ){
					subjectCounter = processMetadata(currentIdentifier, input, generalSheet, subjectCounter);
				}
				if ( XMLExporter.XML_DOCUMENT.equals(input.getLocalName()) ){
					documentCount = processDocument(input, metaData, sheets, sheetCounters);
				}
			}

		}

		//move to next line in the general sheet
		return subjectCounter;

	}

	private int processMetadata(String identifier, final XMLStreamReader input, final WritableSheet sheet, int counter) throws XMLStreamException, IOException, WriteException {
		int column = 0;
		sheet.addCell(new Label(column, counter, identifier));
		column++;

		while ( true ){
			int event = input.next();

			if ( XMLStreamConstants.END_DOCUMENT ==  event || 
					XMLStreamConstants.END_ELEMENT ==  event && XMLExporter.XML_METADATA.equals(input.getLocalName()) ){
				counter++;
				break;
			}

			if ( XMLStreamConstants.START_ELEMENT == event ){
				if ( XMLExporter.XML_VALUE.equals(input.getLocalName()) ){
					sheet.addCell(new Label(column, counter, getContents(input)));
					column++;
				}
			}
		}		
		return counter;
	}

	private int writeUser(final String identifier, final WritableSheet sheet, int counter) throws IOException, WriteException {
		sheet.addCell(new Label(0, counter, identifier));
		return counter++;
	}

	private int checkDocumentSize(int col, final List<WritableSheet> sheets, List<Integer> sheetCounters) throws IOException, WriteException {
		if (col >= (MAX_COLS)) {
			int newRowCount = sheetCounters.get(documentCount).intValue()+1;	//Add one row for each participant for each document
			sheetCounters.set(documentCount, newRowCount);

			//Move onto the next part of this document
			col = 2;
			documentCount++;
			sheets.get(documentCount).addCell(new Label(0, sheetCounters.get(documentCount), currentIdentifier));
			sheets.get(documentCount).addCell(new Label(1, sheetCounters.get(documentCount), currentDocStatus));
		}

		return col;
	}

	private int processDocument(final XMLStreamReader input, final DataSetMetaData metaData, final List<WritableSheet> sheets, List<Integer> sheetCounters) 
	throws XMLStreamException, IOException, WriteException {
		Document metaDoc = null;
		int column = 0;
		while ( true ){
			int event = input.next();

			if ( XMLStreamConstants.END_DOCUMENT ==  event || 
					XMLStreamConstants.END_ELEMENT ==  event && XMLExporter.XML_DOCUMENT.equals(input.getLocalName())){
				int newRowCount = sheetCounters.get(documentCount).intValue()+1;	//Add one row for each participant for each document
				sheetCounters.set(documentCount, newRowCount);
				documentCount++;
				break;
			}

			if ( XMLStreamConstants.START_ELEMENT == event ){
				if ( XMLExporter.XML_ID.equals(input.getLocalName()) ){
					String id = getContents(input);
					metaDoc = metaData.findDocumentById(Long.valueOf(id));
					while (!metaDoc.equals(metaData.getDocuments().get(documentCount))
							&& documentCount <= metaData.getDocuments().size()) {
						//Documents may be missing so keep skipping until the next document is found..
						//This relies on the documents in the XMLExporter being in the same order as the MetaData documents
						documentCount++;
					}

					sheets.get(documentCount).addCell(new Label(column++, sheetCounters.get(documentCount), currentIdentifier));
				}

				if ( XMLExporter.XML_STATUS.equals(input.getLocalName()) ){
					currentDocStatus = getContents(input);
					sheets.get(documentCount).addCell(new Label(column++, sheetCounters.get(documentCount), currentDocStatus));
				}

				if ( XMLExporter.XML_SECTION.equals(input.getLocalName()) ){
					column = checkDocumentSize(column, sheets, sheetCounters);
					column = processSection(input, metaDoc, sheets, column, sheetCounters);
				}
			}
		}		

		return documentCount;
	}

	private int processSection(final XMLStreamReader input, final Document metaDoc, final List<WritableSheet> sheets, int column, List<Integer> sheetCounters) 
	throws XMLStreamException, IOException, WriteException {
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
				}

				if ( XMLExporter.XML_INSTANCE.equals(input.getLocalName()) ){
					column = checkDocumentSize(column, sheets, sheetCounters);
					column = processSectionInstance(input, metaSec, sheets, column, sheetCounters);
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
				column = pad(metaSec.columnCount(i, showCodes, showValues), sheets, sheetCounters, column);
			}
		}

		return column;
	}

	private int processSectionInstance(final XMLStreamReader input, final Section metaSec, final List<WritableSheet> sheets, int column, List<Integer> sheetCounters) 
	throws XMLStreamException, IOException, WriteException {
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
					column = processEntry(input, metaSec, sheets, column, sheetCounters);
				}

				if ( XMLExporter.XML_COMPOSITE.equals(input.getLocalName()) ){
					column = processComposite(input, metaSec, index, sheets, column, sheetCounters);
				}

			}
		}
		return column;
	}

	private int processEntry(final XMLStreamReader input, final Section metaSec, final List<WritableSheet> sheets, int column, List<Integer> sheetCounters) 
	throws XMLStreamException, IOException, WriteException {
		BasicEntry metaEntry = null;
		String value = null;
		while ( true ){
			int event = input.next();

			//	column = checkDocumentSize(column, sheets, sheetCounters);

			if ( XMLStreamConstants.END_DOCUMENT ==  event || 
					XMLStreamConstants.END_ELEMENT ==  event && XMLExporter.XML_ENTRY.equals(input.getLocalName())){
				break;
			}

			if ( XMLStreamConstants.START_ELEMENT == event ){
				if ( XMLExporter.XML_ID.equals(input.getLocalName()) ){
					String id = getContents(input);
					metaEntry = (BasicEntry)metaSec.findEntryById(Long.valueOf(id));
				}

				if ( XMLExporter.XML_VALUE.equals(input.getLocalName())){
					value = getContents(input);
					if (showValues) {
						sheets.get(documentCount).addCell(new Label(column++, sheetCounters.get(documentCount), value));
						column = checkDocumentSize(column, sheets, sheetCounters);
					}
				}

				if ( XMLExporter.XML_CODE.equals(input.getLocalName())){
					String code = getContents(input);
					if (showCodes) {
						if (!showValues && (code == null)) {
							code = value;	//Where a value doesn't have an associated code, the value should still be shown.
						}
						sheets.get(documentCount).addCell(new Label(column++, sheetCounters.get(documentCount), code));
						column = checkDocumentSize(column, sheets, sheetCounters);
					}
				}

				if ( XMLExporter.XML_UNIT.equals(input.getLocalName()) && metaEntry.isUnitPresent() ){
					sheets.get(documentCount).addCell(new Label(column++, sheetCounters.get(documentCount), getContents(input)));
					column = checkDocumentSize(column, sheets, sheetCounters);
				}

				if ( XMLExporter.XML_EXTRA.equals(input.getLocalName()) && metaEntry.isExtraValuePresent() ){
					sheets.get(documentCount).addCell(new Label(column++, sheetCounters.get(documentCount), getContents(input)));
					column = checkDocumentSize(column, sheets, sheetCounters);
				}
			}
		}	
		return column;
	}

	private int processComposite(final XMLStreamReader input, final Section metaSec, final int instanceIndex, final List<WritableSheet> sheets, int column, List<Integer> sheetCounters) 
	throws XMLStreamException, IOException, WriteException {

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
					column = processCompositeRow(input, metaEntry, sheets, column, sheetCounters);
					rowCount++;
				}
			}
		}
		
		//pad for missing rows
		for ( int i=rowCount; i<metaEntry.getRowCount(instanceIndex); i++ ){
			column = pad(metaEntry.columnsPerRow(showCodes, showValues), sheets, sheetCounters, column);
		}

		return column;
	}

	private int processCompositeRow(final XMLStreamReader input, final CompositeEntry metaComp, final List<WritableSheet> sheets, int column, List<Integer> sheetCounters) 
	throws XMLStreamException, IOException, WriteException {
		while ( true ){
			int event = input.next();

			if ( XMLStreamConstants.END_DOCUMENT ==  event || 
					XMLStreamConstants.END_ELEMENT ==  event && XMLExporter.XML_ROW.equals(input.getLocalName())){
				break;
			}

			if ( XMLStreamConstants.START_ELEMENT == event ){
				if ( XMLExporter.XML_ENTRY.equals(input.getLocalName()) ){
					column = processEntryInComposite(input, metaComp, sheets, column, sheetCounters);
				}
			}
		}
		return column;
	}

	private int processEntryInComposite(final XMLStreamReader input, final CompositeEntry metaComp, final List<WritableSheet> sheets, int column, List<Integer> sheetCounters) 
	throws XMLStreamException, IOException, WriteException {
		BasicEntry metaEntry = null;
		String value = null;
		while ( true ){
			int event = input.next();

			//column = checkDocumentSize(column, sheets, sheetCounters);

			if ( XMLStreamConstants.END_DOCUMENT ==  event || 
					XMLStreamConstants.END_ELEMENT ==  event && XMLExporter.XML_ENTRY.equals(input.getLocalName())){
				break;
			}

			if ( XMLStreamConstants.START_ELEMENT == event ){
				if ( XMLExporter.XML_ID.equals(input.getLocalName()) ){
					String id = getContents(input);
					metaEntry = metaComp.findEntryById(Long.valueOf(id));
				}

				if ( XMLExporter.XML_VALUE.equals(input.getLocalName())){
					value = getContents(input);
					if (showValues) {
						sheets.get(documentCount).addCell(new Label(column++, sheetCounters.get(documentCount), value));
						column = checkDocumentSize(column, sheets, sheetCounters);
					}
				}

				if ( XMLExporter.XML_CODE.equals(input.getLocalName())){
					String code = getContents(input);
					if (showCodes) {
						if (!showValues && (code == null || code.equals(""))) {
							code = value;	//Where a value doesn't have an associated code, the value should still be shown.
						}
						sheets.get(documentCount).addCell(new Label(column++, sheetCounters.get(documentCount), code));
						column = checkDocumentSize(column, sheets, sheetCounters);
					}
				}

				if ( XMLExporter.XML_UNIT.equals(input.getLocalName()) && metaEntry.isUnitPresent() ){
					sheets.get(documentCount).addCell(new Label(column++, sheetCounters.get(documentCount), getContents(input)));
					column = checkDocumentSize(column, sheets, sheetCounters);
				}

				if ( XMLExporter.XML_EXTRA.equals(input.getLocalName()) && metaEntry.isExtraValuePresent() ){
					sheets.get(documentCount).addCell(new Label(column++, sheetCounters.get(documentCount), getContents(input)));
					column = checkDocumentSize(column, sheets, sheetCounters);
				}
			}
		}		
		return column;
	}



	private String getContents(final XMLStreamReader input) throws XMLStreamException {
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

	private int pad(int count, final List<WritableSheet> sheets, List<Integer> sheetCounters, int column) throws IOException, WriteException {
		for ( int i=0; i<count; i++ ){
			sheets.get(documentCount).addCell(new Label(column++, sheetCounters.get(documentCount), ""));
			column = checkDocumentSize(column, sheets, sheetCounters);
		}
		return column;
	}

	public String createCsvFileName(String in){
		String temp = in.replace("/", "_");
		return temp.replace(" ", "_");
	}

}


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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.psygrid.data.export.StringEditor.SpecialCharacter;
import org.psygrid.data.export.metadata.BasicEntry;
import org.psygrid.data.export.metadata.CompositeEntry;
import org.psygrid.data.export.metadata.DataSetMetaData;
import org.psygrid.data.export.metadata.Document;
import org.psygrid.data.export.metadata.Entry;
import org.psygrid.data.export.metadata.Section;

/**
 * Class containing methods to convert the intermediate XML files 
 * produced in the first stage of a data export into the final
 * output file(s) for the user.
 * 
 * @author Lucy Bridges
 *
 */
public class SPSSExportFormatter extends StatsExportFormatter {

	public static final String SPSS_SUFFIX = ".sps";

	private static final String COL = "col";

	/**
	 * Use the intermediate XML export files to generate a spss setup
	 * file.
	 * 
	 * @param metaData The export metadata.
	 * @param outputPath The path for the spss setup file
	 * @return The path of the generated spss setup file
	 * @throws XMLStreamException
	 * @throws IOException
	 */
	public File getSetupFile(String csvPath, DataSetMetaData metaData, String outputPath) throws IOException {
		columns = getHeaders(metaData);

		File output = new File(outputPath+SPSS_SUFFIX);
		BufferedWriter writer = new BufferedWriter(new FileWriter(output));

		try{		
			addTitle(metaData, writer);

			addColumnDefinitions(csvPath, writer);

			addColumnLabels(writer);

			addValueLabels(metaData, writer);

			addMissingValues(metaData, writer);

			addFooter(writer);
		}
		finally{
			writer.close();
		}
		return output;
	}


	private void addColumnDefinitions(String dataFile, BufferedWriter writer) throws IOException {
		writer.append("FILE HANDLE dat1 NAME=\"").append(dataFile).append("\" /mode=character .");
		writer.newLine();
		writer.append("GET DATA type=txt /FILE=dat1  /delimiters=\",\" /qualifier='\"' /arrangement=delimited /variables= ");
		writer.newLine();
		int i = 1;
		for (String col: columns.keySet()) {
			String colID = COL+i;	
//			TODO add entry types, ignoring sysmis?
			//TODO check date format...
			writer.append("\t").append(colID).append(" ").append(columns.get(col));
			writer.newLine();
			i++;
		}
		writer.append(".");
		writer.newLine();
		writer.newLine();
	}

	private void addColumnLabels(BufferedWriter writer) throws IOException {

		writer.append("VARIABLE LABELS");
		writer.newLine();
		int i = 1;
		for (String col: columns.keySet()) {
			//Using the StringEditor below to ensure that the variable labels don't have any 
			//carriage return or line feed characters in them.
			String cleanedCol = StringEditor.doReplacement(StringEditor.SpecialCharacter.all, ' ', col);
			String colID = COL+i;	
			writer.append("\t").append(colID).append(" \"").append(cleanedCol).append("\"");
			writer.newLine();
			i++;
		}
		writer.append(".");
		writer.newLine();
		writer.newLine();
	}

	private void addValueLabels(final DataSetMetaData metaData, BufferedWriter writer) throws IOException {
		writer.append("VALUE LABELS");
		writer.newLine();
		//skip the metadata columns and identifier..
		int colCounter = metaData.getMetaFields().size()+2;

		for ( Document doc: metaData.getDocuments() ){
			colCounter++;	//Skip the document status
			for ( Section sec: doc.getSections() ){
				for ( int i=0, c=sec.getInstanceCount(); i<c; i++ ){
					for ( Entry e: sec.getEntries() ){
						if (e instanceof BasicEntry) {
							BasicEntry be = (BasicEntry)e;
							//This is an option entry or standard code, can it be anything else??
							if (be.getValueLabels().size() > 0 || be.isUsesMissingCodes()) {
								String col = COL+colCounter;
								writer.append("\t").append(col);
								writer.newLine();
								boolean first = true;
								for (String code: be.getValueLabels().keySet()) {
									if (code != null && !code.equals("")) {
										if (!first) {
											writer.newLine();	
										}
										first = false;
										String cleanedValueLabel = StringEditor.doReplacement(SpecialCharacter.all, ' ', be.getValueLabels().get(code));
										writer.append("\t").append(code).append(" \"").append(cleanedValueLabel).append("\"");
									}
								}
								if (metaData.getMissingValues().size() < 4 
										&& be.isUsesMissingCodes()) {
									for (String code: metaData.getMissingValues().keySet()) {
										if (!first) {
											writer.newLine();	
										}
										first = false;
										writer.append("\t").append(code).append(" \"").append(metaData.getMissingValues().get(code)).append("\"");
									}
								}
								writer.append(" /");
								writer.newLine();	
							}
							if (be.isUnitPresent()) {
								colCounter++;
							}
							if (be.isExtraValuePresent()) {
								colCounter++;
							}
							colCounter++;
						}
						else if (e instanceof CompositeEntry) {
							CompositeEntry ce = (CompositeEntry)e;
							int rowCount = ce.getRowCount(i);
							for ( int row=0; row<rowCount; row++ ){
								for ( BasicEntry be: ce.getEntries() ){
									//TODO either option entry or standard code, can it be anything else?
									if (be.getValueLabels().size() > 0 || be.isUsesMissingCodes()) {
										String col = COL+colCounter;
										writer.append("\t").append(col);
										writer.newLine();
										boolean first = true;
										for (String code: be.getValueLabels().keySet()) {
											if (code != null && !code.equals("")) {
												if (!first) {
													writer.newLine();	
												}
												first = false;
												writer.append("\t").append(code).append(" \"").append(be.getValueLabels().get(code)).append("\"");
											}
										}
										if (metaData.getMissingValues().size() < 4
												&& be.isUsesMissingCodes()) {
											for (String code: metaData.getMissingValues().keySet()) {
												if (!first) {
													writer.newLine();	
												}
												first = false;
												writer.append("\t").append(code).append(" \"").append(metaData.getMissingValues().get(code)).append("\"");
											}
										}
										writer.append(" /");
										writer.newLine();	
									}
									if (be.isUnitPresent()) {
										colCounter++;
									}
									if (be.isExtraValuePresent()) {
										colCounter++;
									}
									colCounter++;
								}
							}
						}
						else {
							throw new IOException("Entry type is not recognised");
						}
					}
				}
			}
		}
		writer.append(".");
		writer.newLine();	

	}

	private void addMissingValues(final DataSetMetaData metaData, BufferedWriter writer) throws IOException {
		writer.append("MISSING VALUES");
		writer.newLine();

		//skip the metadata columns and identifier..
		int colCounter = metaData.getMetaFields().size()+2;
		boolean firstCol = true;
		for ( Document doc: metaData.getDocuments() ){
			colCounter++;	//Skip the document status
			for ( Section sec: doc.getSections() ){
				for ( int i=0, c=sec.getInstanceCount(); i<c; i++ ){
					for ( Entry e: sec.getEntries() ){
						if (e instanceof BasicEntry) {
							BasicEntry be = (BasicEntry)e;
							//SPSS will only allow three missing values 
							if (be.isUsesMissingCodes() && metaData.getMissingValues().size() < 4) {
								String col = COL+colCounter;
								if (!firstCol) {
									writer.newLine();
								}
								writer.append("\t");
								if (!firstCol) {
									writer.append("/");
								}
								writer.append(col);

								writer.append(" (");
								List<String> codes = new ArrayList<String>();
								for (String code: metaData.getMissingValues().keySet()) {
									codes.add(code);
								}
								boolean first = true;
								for (int j = 0; j < codes.size(); j++) {
									if (!first) {
										writer.append(", ");
									}
									if (this.getEntryType(be).startsWith("A")) {
										writer.append("'").append(codes.get(j).toString()).append("'");
									}
									else {
										writer.append(codes.get(j).toString());
									}
									first = false;
								}
								writer.append(")");

								firstCol = false;
							}
							if (be.isUnitPresent()) {
								colCounter++;
							}
							if (be.isExtraValuePresent()) {
								colCounter++;
							}
							colCounter++;
						}
						else if (e instanceof CompositeEntry) {
							CompositeEntry ce = (CompositeEntry)e;
							int rowCount = ce.getRowCount(i);
							for ( int row=0; row<rowCount; row++ ){
								for ( BasicEntry be: ce.getEntries() ){
									if (be.isUsesMissingCodes() && metaData.getMissingValues().size() < 4) {
										String col = COL+colCounter;
										if (!firstCol) {
											writer.newLine();
										}
										writer.append("\t");
										if (!firstCol) {
											writer.append("/");
										}
										writer.append(col);

										//SPSS will only allow three missing values
										writer.append(" (");
										List<String> codes = new ArrayList<String>();
										for (String code: metaData.getMissingValues().keySet()) {
											codes.add(code);
										}
										boolean first = true;
										for (int j = 0; j < codes.size(); j++) {
											if (!first) {
												writer.append(", ");
											}
											if (this.getEntryType(be).startsWith("A")) {
												//Text entries need quotes
												writer.append("'").append(codes.get(j).toString()).append("'");
											}
											else {
												writer.append(codes.get(j).toString());
											}
											first = false;
										}
										writer.append(")");

										firstCol = false;
									}
									if (be.isUnitPresent()) {
										colCounter++;
									}
									if (be.isExtraValuePresent()) {
										colCounter++;
									}
									colCounter++;
								}
							}
						}
						else {
							throw new IOException("Entry type is not recognised");
						}
					}
				}
			}
		}

		writer.append(".");
		writer.newLine();	
		writer.newLine();
	}

	private void addTitle(final DataSetMetaData metaData, BufferedWriter writer) throws IOException {
		String study = metaData.getName();
		String requestor = metaData.getRequestor();
		String exportDate = metaData.getExportDate();

		writer.append("*                                                                            .");
		writer.newLine();
		writer.append("*   openCMDS exported data for ").append(study).append("	                    .");
		writer.newLine();
		writer.append("*                                                                            .");
		writer.newLine();
		writer.append("*   Exported by ").append(requestor).append("                                .");
		writer.newLine();
		writer.append("*  ").append(exportDate).append("                                            .");
		writer.newLine();
		writer.append("*                                                                            .");
		writer.newLine();
		writer.append("*  SPSS Data Definition Statements                                           .");
		writer.newLine();
		writer.append("*                                                                            .");
		writer.newLine();
		writer.append("*                                                                            .");
		writer.newLine();
		writer.append("* User Notes:                                                                .");
		writer.newLine();
		writer.append("*                                                                            .");
		writer.newLine();
		writer.append("*    1. You will need to change the path and file name specified by the     .");
		writer.newLine();
		writer.append("*    FILE HANDLE NAME to the full location of the data file on your computer.   .");
		writer.newLine();
		writer.append("*    e.g  NAME=\"C:\\My Documents\\export123.csv\"                           .");
		writer.newLine();
		writer.append("*    2. You may also want to enable the save outfile. To do this remove the 	.");
		writer.newLine();
		writer.append("*	'*' next to the line at the end of this file and update the filename.   .");
		writer.newLine();
		writer.append("*	e.g  SAVE OUTFILE=\"c:\\My Documents\\spss-exportfilename.sav\".		.");
		writer.newLine();
		writer.newLine();
	}

	private void addFooter(BufferedWriter writer) throws IOException {
		//Might not be required
		/********************************************************************
		Section 5: Save Outfile


		  This section saves out a Stata system format file.  There is no reason to
		  modify it if the macros in Section 1 were specified correctly.

		 *********************************************************************/

		//save 'outfile', replace
		writer.append("EXECUTE.");
		writer.newLine();
		writer.append("* Create SPSS system file.");
		writer.newLine();
		writer.append("*SAVE OUTFILE=\"c:\\spss-exportfilename.sav\".");
	}

	protected String getEntryType(BasicEntry entry) {
		String type = null;
		final String text = getTextEntryType();
		final String longText = "A512";	
		switch (entry.getEntryType()) {
		case Boolean:
			type = "A1";
			break;
		case Option:
		case Integer:
			//This is the largest integer that SPSS will allow as far as I could see.
			type = "F40.0";
			break;
		case Numeric:
			//This is as large a numeric type as SPSS will allow as far as I could see.
			type = "F40.16";
			break;
		case Text:
			type = text;
			break;
		case LongText:
			type = longText;
			break;
		case Narrative:
			type = "A512";
			break;
		case Date:
			type = getDateEntryType(); //Alt DATETIME10 or SDATE10		//Datetime values in the form dd-mmm-yy hh:mm
			break;
		default:
			type = text;
		}
		return type;
	}
	protected String getTextEntryType() {
		return "A256";
	}
	protected String getDateEntryType() {
		return "Date";
	}
}

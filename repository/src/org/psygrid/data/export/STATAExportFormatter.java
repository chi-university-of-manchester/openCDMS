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

import javax.xml.stream.XMLStreamException;

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
public class STATAExportFormatter extends StatsExportFormatter {

	public static final String STATA_SUFFIX = ".do";

	private static final String COL = "col";

	/**
	 * Use the intermediate XML export files to generate a stata setup
	 * file.
	 * 
	 * @param csvPath The location of the CSV data file 
	 * @param metaData The export metadata.
	 * @param outputPath The path for the stata setup file
	 * @return The path of the generated stata setup file
	 * @throws XMLStreamException
	 * @throws IOException
	 */
	public File getSetupFile(String csvPath, DataSetMetaData metaData, String outputPath) throws IOException {
		columns = getHeaders(metaData);

		File output = new File(outputPath+STATA_SUFFIX);
		BufferedWriter writer = new BufferedWriter(new FileWriter(output));

		try{		
			addTitle(metaData, writer);

			addColumnDefinitions(csvPath, writer);

			addColumnLabels(writer);

			addValueLabels(metaData, writer);

			//addMissingValues(metaData, writer);	//Not used, not sure of the proper syntax

			addFooter(writer);
		}
		finally{
			writer.close();
		}
		return output;
	}

	private void addColumnDefinitions(String dataFile, BufferedWriter writer) throws IOException {
		writer.append("set maxvar 10000");
		writer.newLine();
		writer.append("#delimit ;");
		writer.newLine();
		writer.newLine();

		writer.append("infile");
		writer.newLine();
		int i = 0;
		for (String col: columns.keySet()) {
			String colID = COL+i;	
			writer.append("\t").append(columns.get(col)).append(" ").append(colID);
			writer.newLine();
			i++;
		}

		writer.append("using "+dataFile+", clear;");
		writer.newLine();
		writer.newLine();
	}

	private void addColumnLabels(BufferedWriter writer) throws IOException {
		int i = 0;
		for (String col: columns.keySet()) {
			String colID = COL+i;	
			writer.append("label variable ").append(colID).append(" \"").append(col).append("\";");
			writer.newLine();
			i++;
		}

		writer.newLine();
	}

	private void addValueLabels(final DataSetMetaData metaData, BufferedWriter writer) throws IOException {	
		//skip the metadata columns and identifier..
		int colCounter = metaData.getMetaFields().size()+1;

		for ( Document doc: metaData.getDocuments() ){
			colCounter++;	//Skip the document status
			for ( Section sec: doc.getSections() ){
				for ( int i=0, c=sec.getInstanceCount(); i<c; i++ ){
					for ( Entry e: sec.getEntries() ){
						if (e instanceof BasicEntry) {
							BasicEntry be = (BasicEntry)e;
							//STATA will not allow strings to have value labels
							if (!getEntryType(be).startsWith("str")) {
								int labelCount = 0;
								for (String code: be.getValueLabels().keySet()) {
									if (code != null && !code.equals("")) {
										labelCount++;
									}
								}
								
								if (labelCount > 0) {
									String col = COL+colCounter;
									writer.append("label define ").append(col).append("F");
									writer.newLine();
									//This can be either option entry or standard code, can it be anything else??
									for (String code: be.getValueLabels().keySet()) {
										if (code != null && !code.equals("")) {
											writer.append("\t").append(code).append(" \"").append(code).append(". ").append(be.getValueLabels().get(code)).append("\"");
											writer.newLine();
										}
									}
									writer.append(";");
									writer.newLine();
									writer.append("label values ").append(col).append(" ").append(col).append("F;");
									writer.newLine();
								}
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
									//STATA will not allow strings to have value labels
									if (!getEntryType(be).startsWith("str")) {
										int labelCount = 0;
										for (String code: be.getValueLabels().keySet()) {
											if (code != null && !code.equals("")) {
												labelCount++;
											}
										}
										if (labelCount > 0) {
											String col = COL+colCounter;
											writer.append("label define ").append(col).append("F");
											writer.newLine();
											//This can be either option entry or standard code, can it be anything else?
											for (String code: be.getValueLabels().keySet()) {
												if (code != null && !code.equals("")) {
													writer.append("\t").append(code).append(" \"").append(code).append(". ").append(be.getValueLabels().get(code)).append("\"");
													writer.newLine();
												}
											}
											writer.append(";");
											writer.newLine();
											writer.append("label values ").append(col).append(" ").append(col).append("F;");
											writer.newLine();
										}
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

		writer.append("#delimit cr");
		writer.newLine();
	}

	private void addTitle(final DataSetMetaData metaData, BufferedWriter writer) throws IOException {
		String study = metaData.getName();
		String requestor = metaData.getRequestor();
		String exportDate = metaData.getExportDate();

		writer.append("/********************************************************");
		writer.newLine();
		writer.append("openCMDS exported data for ").append(study);
		writer.newLine();writer.newLine();
		writer.append("Exported by ").append(requestor);
		writer.newLine();
		writer.append(exportDate);
		writer.newLine();writer.newLine();
		writer.append("STATA Data Definition Statements");
		writer.newLine();writer.newLine();			     
		writer.append("********************************************************/");
		writer.newLine();writer.newLine();
	}

	private void addFooter(BufferedWriter writer) {
		//Might not be required
		/********************************************************************
		Section 5: Save Outfile


		  This section saves out a Stata system format file.  There is no reason to
		  modify it if the macros in Section 1 were specified correctly.

		 *********************************************************************/

		//save 'outfile', replace

	}

	protected String getEntryType(BasicEntry entry) {
		String type = null;
		final String text = getTextEntryType();
		switch (entry.getEntryType()) {
		case Boolean:
			type = "byte";
			break;
		case Option:
		case Integer:
			type = "int";
			break;
		case Numeric:
			type = "double";
			break;
		case Text:
			type = text;
			break;
		case LongText:
			type = "str244"; //This will truncate long text fields but is the longest string stata will do
			break;
		case Narrative:
			type = "str244"; //This will truncate narrative fields but is the longest string stata will do
			break;
		default:
			type = text;
		}
		return type;
	}

	protected String getTextEntryType() {
		return "str244";
	}
	protected String getDateEntryType() {
		return "str244";
	}
}

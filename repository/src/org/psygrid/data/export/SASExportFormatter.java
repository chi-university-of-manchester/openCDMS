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
import org.psygrid.data.export.metadata.BasicEntry.TYPE;

/**
 * Class containing methods to convert the intermediate XML files 
 * produced in the first stage of a data export into the final
 * output file(s) for the user.
 * 
 * @author Lucy Bridges
 *
 */
public class SASExportFormatter extends StatsExportFormatter {

	public static final String SAS_SUFFIX = ".sas";

	private static final String COL = "COL";

	/**
	 * Use the intermediate XML export files to generate a SAS setup
	 * file.
	 * 
	 * @param csvPath The location of the CSV data file 
	 * @param metaData The export metadata.
	 * @param outputPath The path for the SAS setup file
	 * @return The path of the generated SAS setup file
	 * @throws XMLStreamException
	 * @throws IOException
	 */
	public File getSetupFile(String csvPath, DataSetMetaData metaData, String outputPath) throws IOException {
		columns = getHeaders(metaData);

		File output = new File(outputPath+SAS_SUFFIX);
		BufferedWriter writer = new BufferedWriter(new FileWriter(output));

		try{		
			addTitle(metaData, writer);

			addValueLabels(metaData, writer);

			addColumnDefinitions(csvPath, writer);

			addColumnLabels(writer);

			addMissingValues(metaData, writer);

			addFooter(writer);
		}
		finally{
			writer.close();
		}
		return output;
	}

	private void addColumnDefinitions(String dataFile, BufferedWriter writer) throws IOException {
		writer.append("data;");
		writer.newLine();

		writer.append("\tinfile '").append(dataFile).append("' ").append(" delimiter=',' DSD LRECL=12000 MISSOVER;");
		writer.newLine();

		//Speficy the column names and type
		writer.append("\tinput");
		writer.newLine();
		int i = 0;
		for (String col: columns.keySet()) {
			String colID = COL+i;	
			writer.append("\t\t").append(colID).append(" :");
			String type = columns.get(col);
			writer.append(type);
			if (!type.equals(getNumericEntryType())) {
				writer.append(".");
			}
			writer.newLine();
			i++;
		}
		writer.append(";");
		writer.newLine();


		writer.newLine();
	}

	private void addColumnLabels(BufferedWriter writer) throws IOException {
		writer.append("\tlabel");
		writer.newLine();
		int i = 0;
		for (String col: columns.keySet()) {
			String colID = COL+i;	
			writer.append("\t\t").append(colID).append(" = \"").append(col).append("\"");
			writer.newLine();
			i++;
		}
		writer.append(";");
		writer.newLine();

		//Link column names with the variable given to specify the value labels (in PROC FORMAT)
		writer.append("\tformat");
		writer.newLine();
		i = 0;
		for (String col: columns.keySet()) {
			String colID = COL+i;	
			String colFmt = getColumnFormat(col, i);
			writer.append("\t\t").append(colID).append(" ").append(colFmt).append(".");
			writer.newLine();
			i++;
		}
		writer.append(";");

		writer.newLine();
	}

	private String getColumnFormat(String column, int i) {
		String colFmt = COL+i+"_";
		if (!columns.get(column).equals(getNumericEntryType())) {
			colFmt = "$"+colFmt;
		}
		return colFmt;
	}

	private String getColumnFormat(BasicEntry be, int i) {
		String colFmt = COL+i+"_";
		if (!be.getEntryType().equals(TYPE.Numeric)
				&& !be.getEntryType().equals(TYPE.Integer)
		) {
			colFmt = "$"+colFmt;
		}
		return colFmt;
	}

	private void addValueLabels(final DataSetMetaData metaData, BufferedWriter writer) throws IOException {	
		//skip the metadata columns and identifier and record status..
		int colCounter = metaData.getMetaFields().size()+1;
		writer.append("proc format;");
		writer.newLine();

		//Add the metadata otherwise we get an error
		for (int i = 0; i < colCounter; i++) {
			String metacol = "$"+COL+i;
			writer.append("value ").append(metacol).append("_");	//variables cannot end in a number, so using an _ instead.
			writer.newLine();
			writer.append(";");
			writer.newLine();
		}

		for ( Document doc: metaData.getDocuments() ){
			//Add the document status otherwise we get an error
			String statuscol = "$"+COL+colCounter;
			writer.append("value ").append(statuscol).append("_");	//variables cannot end in a number, so using an _ instead.
			writer.newLine();
			writer.append(";");
			writer.newLine();

			colCounter++;	//Skip the document status

			for ( Section sec: doc.getSections() ){
				for ( int i=0, c=sec.getInstanceCount(); i<c; i++ ){
					for ( Entry e: sec.getEntries() ){
						if (e instanceof BasicEntry) {
							BasicEntry be = (BasicEntry)e;
							//Can be either an option entry or standard code, can it be anything else??
							String col = getColumnFormat(be, colCounter);
							writer.append("value ").append(col);	//variables cannot end in a number, so using an _ instead.
							writer.newLine();
							for (String code: be.getValueLabels().keySet()) {
								if (code != null && !code.equals("")) {
									String value = code;
									if (!be.getEntryType().equals(TYPE.Numeric)
											&& !be.getEntryType().equals(TYPE.Integer)
									) {
										value = "'"+code+"'";
									}
									writer.append("\t").append(value).append(" = \"").append(be.getValueLabels().get(code)).append("\"");
									writer.newLine();
								}
							}
							/*if (be.isUsesMissingCodes()) {
								for (String code: metaData.getMissingValues().keySet()) {
									if (code != null && !code.equals("")) {
										String value = code;
										try {
											Integer.parseInt(value);
										}
										catch (NumberFormatException nfe) {
											//Add quotes for non-numbers
											value = "'"+code+"'";
										}
										writer.append("\t").append(value).append(" = \"").append(metaData.getMissingValues().get(code)).append("\"");
										writer.newLine();
									}
								}
							}*/
							writer.append(";");
							writer.newLine();

							if (be.isUnitPresent()) {
								colCounter++;
								String unitcol = "$"+COL+colCounter;
								writer.append("value ").append(unitcol).append("_");	//variables cannot end in a number, so using an _ instead.
								writer.newLine();
								writer.append(";");
								writer.newLine();
							}
							if (be.isExtraValuePresent()) {
								colCounter++;
								String extracol = "$"+COL+colCounter;
								writer.append("value ").append(extracol).append("_");	//variables cannot end in a number, so using an _ instead.
								writer.newLine();
								writer.append(";");
								writer.newLine();
							}
							colCounter++;
						}
						else if (e instanceof CompositeEntry) {
							CompositeEntry ce = (CompositeEntry)e;
							int rowCount = ce.getRowCount(i);
							for ( int row=0; row<rowCount; row++ ){
								for ( BasicEntry be: ce.getEntries() ){
									//Can be either an option entry or standard code, can it be anything else?
									String col = getColumnFormat(be, colCounter);//"COL"+colCounter;
									writer.append("value ").append(col);
									writer.newLine();
									for (String code: be.getValueLabels().keySet()) {
										if (code != null && !code.equals("")) {
											String value = code;
											if (!be.getEntryType().equals(TYPE.Numeric)
													&& !be.getEntryType().equals(TYPE.Integer)
											) {
												value = "'"+code+"'";
											}
											writer.append("\t").append(value).append(" = \"").append(be.getValueLabels().get(code)).append("\"");
											writer.newLine();
										}
									}
									/*if (be.isUsesMissingCodes()) {
										for (String code: metaData.getMissingValues().keySet()) {
											if (code != null && !code.equals("")) {
												String value = code;
												try {
													Integer.parseInt(value);
												}
												catch (NumberFormatException nfe) {
													//Add quotes for non-numbers
													value = "'"+code+"'";
												}
												writer.append("\t").append(value).append(" = \"").append(metaData.getMissingValues().get(code)).append("\"");
												writer.newLine();
											}
										}
									}*/
									writer.append(";");
									writer.newLine();

									if (be.isUnitPresent()) {
										colCounter++;
										String unitcol = "$"+COL+colCounter;
										writer.append("value ").append(unitcol).append("_");	//variables cannot end in a number, so using an _ instead.
										writer.newLine();
										writer.append(";");
										writer.newLine();
									}
									if (be.isExtraValuePresent()) {
										colCounter++;
										String extracol = "$"+COL+colCounter;
										writer.append("value ").append(extracol).append("_");	//variables cannot end in a number, so using an _ instead.
										writer.newLine();
										writer.append(";");
										writer.newLine();
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
		writer.newLine();
	}

	private void addMissingValues(final DataSetMetaData metaData, BufferedWriter writer) throws IOException {
		//This doesn't work because SAS only allows missing values to be 'A'-'Z' or '_'.
		/*writer.append("MISSING ");
		for (String code: metaData.getMissingValues().keySet()) {
			writer.append(code).append(" ");
		}
		writer.append(";");
		writer.newLine();*/
	}

	private void addTitle(final DataSetMetaData metaData, BufferedWriter writer) throws IOException {
		String study = metaData.getName();
		String requestor = metaData.getRequestor();
		String exportDate = metaData.getExportDate();

		writer.append("/*");
		writer.newLine();
		writer.append("openCDMS exported data for ").append(study);
		writer.newLine();
		writer.append("Exported by ").append(requestor);
		writer.newLine();
		writer.append(exportDate);
		writer.newLine();
		writer.append("SAS Data Definition Statements");
		writer.newLine();
		writer.newLine();
		writer.append("You will need to change the path of the csv data file to its location on your system in the 'data; infile' section.");
		writer.newLine();
		writer.append("*/");
		writer.newLine();
	}

	private void addFooter(BufferedWriter writer) throws IOException {
		//save 'outfile', replace
		writer.append("proc print;");
		writer.append("run;");
		writer.newLine();
	}

	protected String getEntryType(BasicEntry entry) {
		String type = null;

		final String text = getTextEntryType();
		switch (entry.getEntryType()) {
		case Boolean:
			type = "1";
			break;
		case Integer:
			//This is the largest integer that sas does?
			type = "8";
			break;
		case Numeric:
			//This is smaller than a double, but is the largest numeric sas does?
			type = getNumericEntryType();
			break;
		case Text:
			type = text;
			break;
		case LongText:
			//I think this is the longest text format allowed..
			type = "$32767";
			break;
		case Narrative:
			type = "$8000";
			break;
		default:
			type = text;
		}
		return type;
	}

	protected String getNumericEntryType() {
		return "8.12";
	}

	protected String getTextEntryType() {
		return "$512";
	}
	protected String getDateEntryType() {
		return "$512";
	}
}

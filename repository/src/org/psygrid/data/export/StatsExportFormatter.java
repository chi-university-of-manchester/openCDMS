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
import java.io.IOException;
import java.util.LinkedHashMap;

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
public abstract class StatsExportFormatter {

	protected LinkedHashMap<String,String> columns = new LinkedHashMap<String,String>();
	
	/**
	 * Use the intermediate XML export files to generate a setup file for
	 * a particular statistical package
	 * 
	 * @param csvPath The location of the CSV data file 
	 * @param metaData The export metadata.
	 * @param outputPath The path for the setup file
	 * @return The path of the generated setup file
	 * @throws XMLStreamException
	 * @throws IOException
	 */
	public abstract File getSetupFile(String csvPath, DataSetMetaData metaData, String outputPath) throws IOException;

	protected LinkedHashMap<String,String> getHeaders(final DataSetMetaData metaData) throws IOException {
		final String separator = " - ";
		final String text = getTextEntryType();
		
		//The headers should match exactly the generated csv file.
		LinkedHashMap<String,String> headers = new LinkedHashMap<String,String>();
		
		headers.put("Identifier", text);
		for ( String name: metaData.getMetaFields()){
			if (name.contains("Date")||name.contains("date")) {
				headers.put(name, getDateEntryType());
			}
			else {
				headers.put(name, text);
			}
		}
		
		for ( Document doc: metaData.getDocuments() ){
			headers.put(doc.getName()+separator+"Status", text);
						
			for ( Section sec: doc.getSections() ){
				for ( int i=0, c=sec.getInstanceCount(); i<c; i++ ){
					for ( Entry e: sec.getEntries() ){
						if (e instanceof BasicEntry) {
							BasicEntry be = (BasicEntry)e;
							String type = getEntryType(be);
							String entryName = e.getName();
							if (e.getName() == null || e.getName().equals("")) {
								entryName = "No Name - "+headers.size();
							}
							String fullEntryName = doc.getDocName() + " (" + doc.getStudyStage() + ")" + separator + sec.getName() + " " + (c-1) + separator + entryName;
							if (headers.containsKey(fullEntryName)) {
								int counter = 0;
								while (headers.containsKey(fullEntryName+" "+counter)) {
									counter++;
								}
								fullEntryName += " "+counter;
							}
							headers.put(fullEntryName, type);
							if (be.isUnitPresent()) {
								headers.put(fullEntryName+Entry.UNIT_COLUMN, text);
							}
							if (be.isExtraValuePresent()) {
								headers.put(fullEntryName+Entry.EXTRA_COLUMN, text);
							}
						}
						else if (e instanceof CompositeEntry) {
							CompositeEntry ce = (CompositeEntry)e;
							int rowCount = ce.getRowCount(i);
							for ( int row=0; row<rowCount; row++ ){
								String rowSuffix = Integer.toString(row+1);
								for ( BasicEntry be: ce.getEntries() ){
									String type = getEntryType(be);
									String entryName = be.getName();
									if (be.getName() == null || be.getName().equals("")) {
										entryName = "No Name -"+headers.size();
									}
									String fullEntryName = doc.getDocName() + " (" + doc.getStudyStage() + ")" + separator + sec.getName() + " " + (c-1) + separator + entryName + separator + rowSuffix;
									if (headers.containsKey(fullEntryName)) {
										int counter = 0;
										while (headers.containsKey(fullEntryName+" "+counter)) {
											counter++;
										}
										fullEntryName += " "+counter;
									}
									headers.put(fullEntryName, type);

									if ( be.isUnitPresent() ){
										headers.put(fullEntryName + Entry.UNIT_COLUMN, text);
									}
									if ( be.isExtraValuePresent() ){
										headers.put(fullEntryName + Entry.EXTRA_COLUMN, text);
									}
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
		return headers;
	}
	
	protected abstract String getEntryType(BasicEntry entry);
	protected abstract String getTextEntryType();
	protected abstract String getDateEntryType();
}

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


package org.psygrid.collection.entry.externaldocparser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.List;


import org.psygrid.collection.jaxb.Mappingentry;

import au.com.bytecode.opencsv.CSVReader;
import org.psygrid.data.model.hibernate.DocumentInstance;

/**
 * @author admin
 *
 */
public class GenericCSVExternalDocumentParser extends
		AbstractExternalDocumentParser {

	private CSVReader reader = null;
	private List<String[]> csvOutputList = null;
	private String[] rowMatchingIdentifier = null;
	
	/**
	 * @param documentPath
	 * @param docInstance
	 */
	public GenericCSVExternalDocumentParser(String documentPath,
			DocumentInstance docInstance) {
		super(documentPath, docInstance);
	}

	/**
	 * @param documentPath
	 * @param docInstance
	 */
	public GenericCSVExternalDocumentParser(URL documentPath,
			DocumentInstance docInstance) {
		super(documentPath, docInstance);
	}

	/* (non-Javadoc)
	 * @see org.psygrid.dataimport.identifier.AbstractExternalDocumentParser#getValue(int, int)
	 */
	@Override
	public Object getValue(int documentEntryNumber, int documentSectionNumber) throws ParserException {
		Mappingentry entry = this.getMappingEntry(documentEntryNumber, documentSectionNumber);
		Object returnObject = null;
		return this.getOutputFromMapping(entry);		
	}

	@Override
	protected void initParser() throws ParserException {

		try {
			if(this.source == fileSource.local){
				File file = new File(this.localDocumentLocation);
				this.reader = new CSVReader(new FileReader(file.getAbsoluteFile()));
			}
			else{
				InputStream stream = this.remoteDocumentLocation.openStream();
				java.io.InputStreamReader reader = new InputStreamReader(stream);
				this.reader = new CSVReader(reader);
			}
		}catch (FileNotFoundException e) {
			throw new ParserException("File not found.");
		} catch (IOException e) {
			throw new ParserException("An I/O exception occurred.");
		}
		
	    try {
			this.csvOutputList = reader.readAll();		
			String identifier = this.docInstance.getRecord().getIdentifier().getIdentifier();
			rowMatchingIdentifier = getRowForIdentifier(identifier);
			
		} catch (IOException e1) {
			throw new ParserException("File could not be parsed.");
		}
	}

	@Override
	protected Object getOutputFromMapping(Mappingentry entry) throws ParserException {
		
		int posInOutputFile = Integer.parseInt(entry.getSourceEntry(),10 /*radix*/);
		
		int importDataSize = Array.getLength(rowMatchingIdentifier);

		if(posInOutputFile > importDataSize){
			throw new ParserException("Index out of bounds");
		}
		
		return rowMatchingIdentifier[posInOutputFile-1];
	}
	
	private String[] getRowForIdentifier(String identifier) throws ParserException {
		
		String[] list = null;
		boolean idMatchFound = false;
		for(int i = 0; i < csvOutputList.size(); i++){
			list = csvOutputList.get(i);
			String id = list[0];
			if(id.equals(identifier)){
				idMatchFound = true;
				break;
			}
		}
		
		if(idMatchFound){
			return list;
		}else{
			throw new ParserException("Identifier " + identifier + "not found in .csv file.");
		}
	}

	/* FOR FUTURE
	@Override
	protected void initSupportedParserFeatures() {
		this.supportedImportParserFeatures = new ArrayList<DocumentImportFeature>();
		//The array is empty at this time because the parser doesn't yet support any complex features.
	}
	*/
}



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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.psygrid.collection.jaxb.Importmapping;
import org.psygrid.collection.jaxb.Mappingentry;
import org.psygrid.data.model.IValue;
import org.psygrid.data.model.hibernate.*;


public abstract class AbstractExternalDocumentParser implements
		IExternalDocumentParser {
	
	public class ParseResults{
		public ParseResults(){}
		private int numUnmappableItems;
		private int numImportExceptions;
		
		public int getNumImportExceptions(){
			return numImportExceptions;
		}
		
		public int getNumUnmappableItems(){
			return numUnmappableItems;
		}
		
	}
	
	enum fileSource{
		local,
		url
	}

// 	FOR FUTURE
//	protected ArrayList<DocumentImportFeature> supportedImportParserFeatures = null;
	protected fileSource source;
	protected String localDocumentLocation;
	protected URL remoteDocumentLocation;
	protected DocumentInstance docInstance;
	private boolean documentOpenedSuccessfully = false;
	private boolean mappingCreatedSuccessfully = false;
//	protected List<Mappingentry> mappingEntries = null;
	protected Importmapping importMap = null;
	
	/*
	 * This constructor is to be used in the event that the file is local (on the user's machine)
	 */
	public AbstractExternalDocumentParser(String documentPath, DocumentInstance docInstance){
		source = fileSource.local;
		localDocumentLocation = documentPath;
		this.docInstance = docInstance;
		//this.initSupportedParserFeatures();
	}
	
	/*
	 * This constructor is to be used in the event that the file's location is specified as a URL.
	 * This is to provide a means of access to remote files.
	 */
	public AbstractExternalDocumentParser(URL documentPath, DocumentInstance docInstance){
		source = fileSource.url;
		remoteDocumentLocation = documentPath;
		this.docInstance = docInstance;
		//this.initSupportedParserFeatures();
	}
	
	/*
	 * openDocumentFromSource will indicate from its return value whether the
	 * document was opened properly, thereby verifying its existence.
	 * If false is returned, it is because the file didn't exist, or couldn't be accessed.
	 */
	public void verifyDocumentFromSource() throws ParserException {
		if(source == fileSource.local)
			verifyLocalDocument();
		else
			verifyRemoteDocument();
		
		initParser();
	} 

	protected abstract void initParser() throws ParserException;
	
	/* FOR FUTURE
	protected abstract void initSupportedParserFeatures();
	*/
	
	/* FOR FUTURE
	public List<DocumentImportFeature> getSupportedDocumentImportFeatures(){
		return supportedImportParserFeatures;
	}
	*/
	
	/*
	 * Returns whether the local document could be opened by the parser.
	 */
	protected void verifyLocalDocument() throws ParserException{
		try {
			new FileReader(localDocumentLocation);
		} catch (FileNotFoundException e) {
			throw new ParserException("Local file not found.");
		}
	}
	
	/*
	 * Returns whether the remote document could be opened by the parser.
	 */
	protected void verifyRemoteDocument() throws ParserException{
        try {
			remoteDocumentLocation.openStream();
		} catch (IOException e) {
			throw new ParserException("Remote document could not be opened.");
		}
	}
	
	public ParseResults Parse(){
		ParseResults results = new ParseResults();
		
		int entryIndex = -1;				//Used for iterating through entries and indexing into the import array
		int indexOfLastEntry;
		int indexOfLastBasicEntry = 0;		//Used in case of composite entries to populate as many of the basic entries as possible
		Vector compositeEntryVector = new Vector(2,2);	//Will contain any composite entries for the current section, if any. A vector is used because it can grow dynamically, unlike an array.
		
		Response genericResponse = null;
		Document document = this.docInstance.getOccurrence().getDocument();
		int numSections = document.numSections();
		Section section = null;
		Entry entry = null;
		int runningEntryIndex = -1;
		
		SectionOccurrence secOccurrence = null;
		for(int j = 0; j < numSections; j++){
		//For now, just grab the first section
			section = document.getSection(j);
			
			//Find out how many entries are in this section...
			List<Entry> sectionEntries = new ArrayList<Entry>();

			for (int i = 0; i < document.numEntries(); i++) {
				entry = document.getEntry(i);
				if (section.equals(entry.getSection())) {
					sectionEntries.add(entry);
				}
			}
				
			int entriesInSection = sectionEntries.size();

			int numberOfSectionOccurrences = section.numOccurrences();
			
			for(int k = 0; k < numberOfSectionOccurrences; k++){
				
				secOccurrence = section.getOccurrence(k);
				
				for(int l = 0; l < entriesInSection; l++){
					entry = sectionEntries.get(l);
					runningEntryIndex++;
					
					if(entry instanceof NarrativeEntry || entry instanceof ExternalDerivedEntry){
						//It won't have a response, and can just skip it.
						continue;
					}
					
					genericResponse = this.docInstance.getResponse(entry, secOccurrence);
					if ( null == genericResponse ){
						//if the entry is in a section that has not been visited yet then
						//there will not be a response yet, so add one
						genericResponse = entry.generateInstance(secOccurrence);
						docInstance.addResponse(genericResponse);
						((BasicResponse)genericResponse).setValue(((BasicEntry)entry).generateValue());
					}
					BasicResponse response = (BasicResponse)genericResponse;
					IValue value = response.getValue();
					
					Object entryValue;
					try {
						entryValue = getValue(runningEntryIndex, 1); //Note: future versions will use section and section occurrence info
					} catch (ParserException e1) {
						results.numImportExceptions++;
						continue;
					}
					
					if(entryValue != null) //Null entry just means that the import document had a null entry at this index.
					{
						try{
						value.importValue(entryValue.toString(), entry);
						value.setStandardCode(null);
						}catch (ModelException e){
							results.numImportExceptions++;
						}
					}
				} //l-loop
			} //k-loop
		} //j-loop
		return results;
	}
	
	/*
	 * Returns whether the document mapping (jaxb) was successfully initialized.
	 */
	public boolean initializeDocumentMapping(){
		boolean success = true;
		String mappingString = null;
		
		mappingString = this.docInstance.getOccurrence().getDocument().getImportMappingString();

        JAXBContext jc = null;
        Unmarshaller u = null;
		try {
			jc = JAXBContext.newInstance( "org.psygrid.collection.jaxb" );
		} catch (JAXBException e) {
			success = false;
		}
		
		if(!success)
			return false;
		
        try {
			u = jc.createUnmarshaller();
		} catch (JAXBException e) {
			success = false;
		}
		
		if(!success)
			return false;
		
	    StringReader stringReader = new StringReader(mappingString);
	    StreamSource streamSource = new StreamSource(stringReader);
	    Importmapping impMapping;
		impMapping = null;
	    try {
	 		impMapping = (Importmapping)u.unmarshal(streamSource);
		} catch (JAXBException e) {
			success = false;
		}
		
		if(!success)
			return false;
	

		importMap = impMapping;
		
		this.mappingCreatedSuccessfully = true;
		
		return true;
	}
	
	/*
	 * Returns a value object, from the external document, for the specificied entry within a specified section.
	 * @see org.psygrid.dataimport.identifier.IExternalDocumentParser#getValue(int, int)
	 */
	public abstract Object getValue(int documentEntryNumber, int documentSectionNumber) throws ParserException; 
	
	protected abstract Object getOutputFromMapping(Mappingentry entry) throws ParserException;
		
	/*
	 * Retrieves a mapping according to the index passed in
	 */
	protected Mappingentry getMappingEntry(int index, int sectionNumber){
		Mappingentry returnEntry = null;
		for(Mappingentry entry : this.importMap.getMapping()){
			if(entry.getDocumentEntry().intValue() == index+1){
				returnEntry = entry;
				break;
			}
		}
		return returnEntry;
	}
}

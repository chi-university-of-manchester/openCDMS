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

import java.net.MalformedURLException;
import java.net.URL;

import org.psygrid.data.model.hibernate.DocumentInstance;

public class ExternalDocumentParserFactory {

	private SelectedFileInfo fileInfo;
	private RecognizedFileType fileType;
	private String fullFilePathAndName;
	private DocumentInstance documentInstance;
	
	public ExternalDocumentParserFactory(RecognizedFileType fileType, SelectedFileInfo fileInfo, String fullFilePathAndName, DocumentInstance docInstance){
		super();
		this.fileType = fileType;
		this.fileInfo = fileInfo;
		this.fullFilePathAndName = fullFilePathAndName;
		this.documentInstance = docInstance;
	}
	
	public AbstractExternalDocumentParser getParser() throws MalformedURLException{
		
		AbstractExternalDocumentParser theParser = null;
		//Now instantiate the parser based on whether the file is xml or csv and whether the file type is local or not
		if(fileInfo == SelectedFileInfo.local && fileType == RecognizedFileType.csv)
			theParser = new GenericCSVExternalDocumentParser(fullFilePathAndName, documentInstance);
		else if(fileInfo == SelectedFileInfo.local && fileType != RecognizedFileType.csv)
			theParser = new GenericXMLExternalDocumentParser(fullFilePathAndName, documentInstance);
		else if(fileInfo != SelectedFileInfo.local){
			URL url = new URL(fullFilePathAndName);
			
			if(fileType == RecognizedFileType.csv)
				theParser = new GenericCSVExternalDocumentParser(url, documentInstance);
			else if(fileType == RecognizedFileType.xml)
				theParser = new GenericXMLExternalDocumentParser(url, documentInstance);
		}
		
		return theParser;	
	}
	
}

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

import java.io.*;

import javax.xml.xpath.*;
import javax.xml.parsers.*;

import org.psygrid.data.model.hibernate.DocumentInstance;
import org.xml.sax.*;
import org.w3c.dom.*;

import java.net.URL;

import org.psygrid.collection.jaxb.Mappingentry;

public class GenericXMLExternalDocumentParser extends
		AbstractExternalDocumentParser {
	
	private XPath path = null;
	private Document inputSource = null;

	public GenericXMLExternalDocumentParser(String documentPath,
			DocumentInstance docInstance) {
		super(documentPath, docInstance);
	}

	public GenericXMLExternalDocumentParser(URL documentPath,
			DocumentInstance docInstance) {
		super(documentPath, docInstance);
	}

	@Override
	public Object getValue(int documentEntryNumber, int documentSectionNumber) throws ParserException {
		Mappingentry entry = this.getMappingEntry(documentEntryNumber, documentSectionNumber);
		return this.getOutputFromMapping(entry);		
	}

	@Override
	protected void initParser() throws ParserException {
		path = XPathFactory.newInstance().newXPath(); 
	
	    // parse the XML as a W3C Document
	    DocumentBuilder builder = null;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new ParserException("XML Parser object could not be created.");
		}
	    try {
	    	if(this.source == fileSource.local)
	    		inputSource = builder.parse(new File(this.localDocumentLocation));
	    	else {
	    		InputStream stream = remoteDocumentLocation.openStream();
	    		inputSource = builder.parse(stream);
	    	}
		} catch (SAXException e) {
			throw new ParserException("Source file could not be parsed.");
		} catch (IOException e) {
			throw new ParserException("Source file could not be opened.");
		}
	}

	@Override
	protected Object getOutputFromMapping(Mappingentry entry) throws ParserException {
		String xpathQuery = entry.getSourceEntry();
	    try {
            String elementAsString = path.evaluate(xpathQuery, inputSource);
	    	return elementAsString;
		} catch (XPathExpressionException e) {
			throw new ParserException("Entry could not be located in the source file.");
		} 
	}
	
	/* FOR FUTURE
	@Override
	protected void initSupportedParserFeatures() {
		this.supportedImportParserFeatures = new ArrayList<DocumentImportFeature>();
		//This parser does not support any complex features at this time, so the array is empty.
	}
	*/
}

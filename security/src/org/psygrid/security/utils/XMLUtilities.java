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


//Created on Nov 1, 2005 by John Ainsworth
package org.psygrid.security.utils;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author jda
 *
 */
public class XMLUtilities {

	private static Log sLog = LogFactory.getLog(XMLUtilities.class);

	public XMLUtilities(){};
	
	/**
	 * Validate the string supplied against the schema(s) loaded into the
	 * the XML parser. 
	 * @param       doc  the xml DOM to be validated
	 * @param	   schemaFile The schema to validate against
	 * @return      DOM document of the XML if valid, otherwise null.
	 * @see         Document
	 **/
	public boolean checkAgainstSchema(Document doc, String schemaFile) {
		sLog.debug("checking... \n" + domToString(doc));
		sLog.debug("Against... " + schemaFile);
		DOMConfiguration config = ((CoreDocumentImpl) doc).getDomConfig();
		config.setParameter("schema-type", "http://www.w3.org/2001/XMLSchema");
		config.setParameter("schema-location", schemaFile);
		config.setParameter("validate", Boolean.TRUE);
		DOMErrorHandlerImpl eh = new DOMErrorHandlerImpl();
		config.setParameter("error-handler", eh);
		doc.normalizeDocument();
		return eh.isParseResult();
	}

	/**
	 * @param doc
	 * @return
	 */
	public static String domToString(Document doc) {
		try {
			OutputFormat format = new OutputFormat(doc);
			StringWriter stringOut = new StringWriter();
			XMLSerializer serial = new XMLSerializer(stringOut, format);
			serial.asDOMSerializer();
			serial.serialize(doc.getDocumentElement());
			String outString = stringOut.toString();
			return outString;
		} catch (IOException ioe) {
			sLog.fatal("failed to convert dom to string");
			ioe.printStackTrace();
			return null;
		}
	}
	
	public  Document toDocument(String xmlStuff, boolean validating,
			String schemaFile) {
		try {
			// Create a builder factory
			DocumentBuilderFactory factory = DocumentBuilderFactory
			.newInstance();
			factory.setValidating(false);
			// Create the builder and parse the file
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setErrorHandler(getErrorHandler());
			Document doc = builder.parse(new InputSource(new StringReader(
					xmlStuff)));
			if (validating) {
				if(checkAgainstSchema(doc, schemaFile)){
					return doc;
				}
			} else {
				return doc;
			}
			
		} catch (SAXException e) {
			sLog.fatal(e.getMessage());
		} catch (ParserConfigurationException e) {
			sLog.fatal(e.getMessage());
		} catch (IOException e) {
			sLog.fatal(e.getMessage());
		}
		return null;
	}	
	
	
	public static String xmlFragmentToString(Element e) {
		try {
			OutputFormat format = new OutputFormat();
			StringWriter stringOut = new StringWriter();
			XMLSerializer serial = new XMLSerializer(stringOut, format);
			serial.asDOMSerializer();
			serial.serialize(e);
			String outString = stringOut.toString();
			return outString;
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return null;
	}

	private static ErrorHandler getErrorHandler() {
		return new ErrorHandler() {
			public void error(SAXParseException error) throws SAXException {

				sLog.fatal("XML Parse Error: severity - error");

				String location = error.getSystemId();
				if (location != null) {
					sLog.fatal("Parse error at URI: " + location);
				}

				sLog.fatal("Details: L=" + error.getLineNumber() + " C="
						+ error.getColumnNumber() + " : " + error.getMessage());

				try {
					throw error.getException();
				} catch (Exception e) {
					throw new SAXException(error.getMessage());
				}

			};

			public void warning(SAXParseException error) {

				sLog.fatal("XML Parse Error: severity - warning");

				String location = error.getSystemId();
				if (location != null) {
					sLog.fatal("Parse error at URI: " + location);
				}

				sLog.fatal("Details: L=" + error.getLineNumber() + " C="
						+ error.getColumnNumber() + " : " + error.getMessage());
			};

			public void fatalError(SAXParseException error) throws SAXException {

				sLog.fatal("XML Parse Error: severity - fatal error");

				String location = error.getSystemId();
				if (location != null) {
					sLog.fatal("Parse error at URI: " + location);
				}

				sLog.fatal("Details: L=" + error.getLineNumber() + " C="
						+ error.getColumnNumber() + " : " + error.getMessage());
				try {
					throw error.getException();
				} catch (Exception e) {
					throw new SAXException(error.getMessage());
				}
			}
		};
	}

}

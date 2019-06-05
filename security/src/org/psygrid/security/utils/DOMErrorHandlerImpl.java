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


//Created on Nov 7, 2005 by John Ainsworth

package org.psygrid.security.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.DOMError;
import org.w3c.dom.DOMErrorHandler;

/**
 * @author jda
 *
 */
public class DOMErrorHandlerImpl implements DOMErrorHandler {
	/** logger */
	private static Log sLog = LogFactory.getLog(DOMErrorHandlerImpl.class);

	private boolean parseResult = true;

	public boolean handleError(DOMError error) {
		String errorType = null;
		switch (error.getSeverity()) {
		case DOMError.SEVERITY_WARNING:
			errorType = "Warning";
			break;
		case DOMError.SEVERITY_ERROR:
			errorType = "Error";
			break;
		case DOMError.SEVERITY_FATAL_ERROR:
			errorType = "Fatal Error";
			break;
		}
		sLog.info("XML Parse Error: severity - " + errorType);

		String location = error.getLocation().getUri();
		if (location != null) {
			sLog.info("Parse error at URI: " + location);
		}

		parseResult = false;

		sLog.info("Details: L=" + error.getLocation().getLineNumber() + " C="
				+ error.getLocation().getColumnNumber() + " : "
				+ error.getMessage());

		return error.getSeverity() != DOMError.SEVERITY_FATAL_ERROR;
	}

	/**
	 * @return Returns the parseResult.
	 */
	protected boolean isParseResult() {
		return this.parseResult;
	}

	/**
	 * @param parseResult The parseResult to set.
	 */
	protected void setParseResult(boolean parseResult) {
		this.parseResult = parseResult;
	}
}
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


package org.psygrid.web.beans;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Bean to represent an error to be displayed by a JSP.
 * 
 * @author Rob Harper
 *
 */
public class ErrorBean {

	private String message;
	
	private String stackTrace;

	private String title;
	
	public ErrorBean(){}
	
	public ErrorBean(Throwable t){
		this.message = t.getMessage();
		final Writer result = new StringWriter();
	    final PrintWriter printWriter = new PrintWriter(result);
	    t.printStackTrace(printWriter);
	    this.stackTrace = result.toString();
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getStackTrace() {
		return stackTrace;
	}

	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
}

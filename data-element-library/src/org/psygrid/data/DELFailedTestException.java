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
package org.psygrid.data;

/**
 * This exception is thown when an element has failed one or more
 * of its tests during saving/updating.
 * 
 * @author Lucy Bridges
 *
 */
public class DELFailedTestException extends Exception {
	
	private static final long serialVersionUID = 485821896417460642L;
	
	private String message;

	public DELFailedTestException() {
		super();
	}

	public DELFailedTestException(String message) {
		super(message);
		this.message = message;
	}

	public DELFailedTestException(Throwable cause) {
		super(cause);
		this.message = cause.getMessage();
	}

	public DELFailedTestException(String message, Throwable cause) {
		super(message, cause);
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

}

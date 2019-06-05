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
package org.psygrid.data.dao;

public class ElementRevisionException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -33967679038157964L;
	
	private String message;

	public ElementRevisionException() {
		super();
	}

	public ElementRevisionException(String message) {
		super(message);
		this.message = message;
	}

	public ElementRevisionException(Throwable cause) {
		super(cause);
		this.message = cause.getMessage();

	}

	public ElementRevisionException(String message, Throwable cause) {
		super(message, cause);
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
	
    public void setMessage(String message) {
        this.message = message;
    }
}
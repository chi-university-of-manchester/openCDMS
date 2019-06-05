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

package org.psygrid.collection.entry.ui;

/**
 * Exception to be thrown when a problems arises during
 * the initialization of an identifier panel e.g. the
 * user has no groups.
 * 
 * @author Rob Harper
 *
 */
public class IdentifierPanelException extends Exception {

	private static final long serialVersionUID = 9121006437015476612L;

	private String title;
	
	/**
	 * 
	 */
	public IdentifierPanelException() {
	}

	/**
	 * @param message
	 */
	public IdentifierPanelException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public IdentifierPanelException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public IdentifierPanelException(String message, Throwable cause) {
		super(message, cause);
	}

	public IdentifierPanelException(String message, String title){
		super(message);
		this.title = title;
	}

	public String getTitle() {
		return title;
	}
}

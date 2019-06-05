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

package org.psygrid.esl.dao;

/**
 * Exception class thrown when an attempt is made to save a 
 * new subject that has the same details as an existing
 * subject.
 * 
 * @author Lucy Bridges
 *
 */
public class SubjectExistsException extends DAOException {

	private static final long serialVersionUID = -7607771563019374398L;

	public SubjectExistsException() {
		super();
	}

	public SubjectExistsException(String message, Throwable cause) {
		super(message, cause);
	}

	public SubjectExistsException(String message) {
		super(message);
	}

	public SubjectExistsException(Throwable cause) {
		super(cause);
	}

}

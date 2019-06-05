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

package org.psygrid.security.policyauthority.dao;

/**
 * Exception class thrown when it is not possible to save (update)
 * an object in the data repository, due to a version conflict.
 * This will occur when the same object has been saved by another
 * session after the object was retrieved from the repository.
 * 
 * @author Rob Harper
 *
 */
public class ObjectOutOfDateException extends Exception{

    static final long serialVersionUID = 2600931525846870806L;
    
    public ObjectOutOfDateException(String message, Throwable cause) {
        super(message, cause);
    }

    public ObjectOutOfDateException(String message) {
        super(message);
    }

    public ObjectOutOfDateException(Throwable cause) {
        super(cause);
    }

    public ObjectOutOfDateException() {
        super();
    }

}

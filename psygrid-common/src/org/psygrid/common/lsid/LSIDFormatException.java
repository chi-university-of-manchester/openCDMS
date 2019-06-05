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

package org.psygrid.common.lsid;

/**
 * Exception class thrown when an LSID object - either a
 * LSID class or a string - does not adhere to the 
 * expected format for LSIDs.
 * 
 *  @author Lucy Bridges
 *
 */
public class LSIDFormatException extends Exception {

    static final long serialVersionUID = 1L;
    
    public LSIDFormatException() {
        super();
    }

    public LSIDFormatException(String message) {
        super(message);
    }

    public LSIDFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public LSIDFormatException(Throwable cause) {
        super(cause);
    }

}

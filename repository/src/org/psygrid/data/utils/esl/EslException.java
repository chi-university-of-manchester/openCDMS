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


package org.psygrid.data.utils.esl;

/**
 * Exception is thrown when problem occurs with the 
 * ESL.
 * 
 * This exception should only be thrown when there is 
 * no way of dealing with the error programmatically.
 * 
 * @author Lucy Bridges
 *
 */
public class EslException extends Exception {

    static final long serialVersionUID = 1272777199690281296L;
    
    public EslException() {
        super();
    }

    public EslException(String message) {
        super(message);
    }

    public EslException(String message, Throwable cause) {
        super(message, cause);
    }

    public EslException(Throwable cause) {
        super(cause);
    }

}

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

package org.psygrid.randomization;

/**
 * Exception thrown by randomization web-service endpoints
 * when an unrecoverable error has occurred.
 * 
 * @author Rob Harper
 *
 */
public class RandomizationFault extends Exception {

    private static final long serialVersionUID = -4187669887517533563L;

    private String message;
    
    public RandomizationFault() {
        super();
    }

    public RandomizationFault(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    public RandomizationFault(String message) {
        super(message);
        this.message = message;
    }

    public RandomizationFault(Throwable cause) {
        super(cause);
        this.message = cause.getMessage();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
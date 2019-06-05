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

package org.opencdms.web.modules.reports.repository;

/**
 * Exception thrown by the ReportingClient when a problem occured
 * talking to the Repository web-service.
 * 
 * @author Lucy Bridges
 *
 */
public class RepositoryFault extends Exception {

    static final long serialVersionUID = -8266582156596951475L;
    
    private String message;
    
    public RepositoryFault() {
        super();
    }

    public RepositoryFault(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    public RepositoryFault(String message) {
        super(message);
        this.message = message;
    }

    public RepositoryFault(Throwable cause) {
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

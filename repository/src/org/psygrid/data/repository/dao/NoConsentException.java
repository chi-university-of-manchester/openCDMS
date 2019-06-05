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

package org.psygrid.data.repository.dao;

/**
 * Exception class thrown by RecordDAO#saveRecord in the case that
 * there is not sufficient consent in the record being saved for 
 * one or more document instances. 
 * 
 * @author Rob Harper
 *
 */
public class NoConsentException extends DAOException {
    
    static final long serialVersionUID = -8853183019097471784L;
    
    public NoConsentException() {
        super();
    }

    public NoConsentException(String message) {
        super(message);
    }

    public NoConsentException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoConsentException(Throwable cause) {
        super(cause);
    }

}

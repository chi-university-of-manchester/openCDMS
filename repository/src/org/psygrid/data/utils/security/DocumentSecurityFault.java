/*
Copyright (c) 2008, The University of Manchester, UK.

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
02110-1301, USA.
 */
package org.psygrid.data.utils.security;

/**
 * Exception thrown by the DocumentSecurityHelper...
 * 
 * @author Lucy Bridges
 *
 */
public class DocumentSecurityFault extends RuntimeException {

    static final long serialVersionUID = -5429456358022609368L;
    
    private String message;
    
    public DocumentSecurityFault() {
        super();
    }

    public DocumentSecurityFault(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    public DocumentSecurityFault(String message) {
        super(message);
        this.message = message;
    }

    public DocumentSecurityFault(Throwable cause) {
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

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

package org.globus.myproxy;

public interface MyProxyConstants {

    public static final String MYPROXY_PROTOCOL_VERSION = "MYPROXYv2";
    public static final int MIN_PASSWORD_LENGTH         = 6;

    static final String VERSION    = "VERSION=" + MYPROXY_PROTOCOL_VERSION;
    static final String COMMAND    = "COMMAND=";
    static final String USERNAME   = "USERNAME=";
    static final String PASSPHRASE = "PASSPHRASE=";
    static final String LIFETIME   = "LIFETIME=";
    static final String CRED_NAME  = "CRED_NAME=";
    static final String RETRIEVER  = "RETRIEVER=";
    static final String RENEWER    = "RENEWER=";
    static final String CRED_DESC  = "CRED_DESC=";
    static final String NEW_PHRASE = "NEW_PHRASE=";
    static final String CRLF       = "\n";

}

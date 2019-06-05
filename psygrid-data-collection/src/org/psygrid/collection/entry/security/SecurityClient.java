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


package org.psygrid.collection.entry.security;

import java.rmi.Remote;

import javax.xml.rpc.Stub;

public abstract class SecurityClient {
    
    protected Remote port;
    
    protected String endPointAddress;
        
    protected SecurityClient(Remote port, String endPointAddress) {
        this.port = port;
        this.endPointAddress = endPointAddress;
    }
    
    protected void init() {
        
        // Set the address of the service endpoint
        ((javax.xml.rpc.Stub) port)._setProperty(Stub.ENDPOINT_ADDRESS_PROPERTY,
                endPointAddress);
    }
    
    protected Remote getPort() {
        return port;
    }
}

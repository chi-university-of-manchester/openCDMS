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

package org.psygrid.common;

import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;

import org.apache.axis.AxisFault;
import org.apache.commons.logging.Log;

public abstract class AbstractClient {

    /**
     * The url where the web-service is located.
     */
    protected URL url = null;
    
    /**
     * Timeout period for web-service connection in milliseconds.
     * <p>
     * A negative value implies that the default is used.
     */
    protected int timeout = -1;
    
    /**
     * Default no-arg constructor
     */
    public AbstractClient(){}
    
    /**
     * Constructor that accepts a value for the url where the web
     * service is located.
     * 
     * @param url
     */
    public AbstractClient(URL url){
        this.url = url;
    }
    
    /**
     * Constructor that accepts a value for the url where the web
     * service is located and the timeout for the web service.
     * 
     * @param url
     * @param timeout
     */
    public AbstractClient(URL url, int timeout){
        this.url = url;
        this.timeout = timeout;
    }
    
    /**
     * Constructor that accepts a timeout for the web service.
     * 
     * @param timeout
     */
    public AbstractClient(int timeout){
        this.timeout = timeout;
    }
    
    public int getTimeout() {
        return timeout;
    }

    public URL getUrl() {
        return url;
    }

    protected void handleAxisFault(AxisFault fault, Log log)
            throws ConnectException, SocketTimeoutException {
        if ( fault.getCause() instanceof ConnectException ){
        	log.info("Handling fault with cause = ConnectException");
            throw (ConnectException)fault.getCause();
        }
        else if ( fault.getCause() instanceof UnknownHostException ||
                fault.getCause() instanceof NoRouteToHostException ){
        	log.info("Handling fault with cause = UnknownHostException/NoRouteToHostException");
            ConnectException cex = new ConnectException(fault.getCause().getMessage());
            cex.initCause(fault.getCause());
            throw cex;
        }
        else if ( fault.getCause() instanceof SocketTimeoutException ){
        	log.info("Handling fault with cause = SocketTimeoutException");
        	throw (SocketTimeoutException)fault.getCause();
        }
        else if ( fault.getCause() instanceof SocketException ){
        	log.info("Handling fault with cause = SocketException");
        	ConnectException cex = new ConnectException(fault.getCause().getMessage());
            cex.initCause(fault.getCause());
            throw cex;
        }
        else if ( null == fault.getCause() && fault.getFaultCode().getLocalPart().equals("HTTP") ){
        	log.info("Handling fault with null cause, code = HTTP");
            SocketTimeoutException ste = new SocketTimeoutException("Connection dropped.");
            ste.initCause(fault);
            throw ste;
        }
        else{
        	if ( null != fault.getCause() ){
        		log.fatal(fault.getCause().getClass().getName());
        	}
        	else{
        		log.fatal("fault.getCause() returned null");
        	}
            log.fatal(fault.getMessage(), fault);
            if ( null != fault.getCause() ){
                throw new RuntimeException(fault.getCause());
            }
            else{
                throw new RuntimeException(fault);
            }
        }
    }
}

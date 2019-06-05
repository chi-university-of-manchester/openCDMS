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

package org.psygrid.logging;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import org.apache.axis.Constants;
import org.apache.axis.MessageContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class to handle logging of method calls for audit purposes.
 * 
 * @author Rob Harper
 *
 */
public class AuditLogger {

    /**
     * Logger specifically for audit logging.
     * <p>
     * Needs to be set up in the log4j configuration.
     */
    private Log log = null;
    
    public AuditLogger(Class c) {
		log = LogFactory.getLog("Audit."+c);
	}
    
    /**
     * The name and IP address of the host machine.
     */
    private static final String hostAddress = findHostAddress();
    
    
    /**
     * Log a method call.
     * 
     * @param componentName The name of the component whose method was called.
     * @param methodName The name of the method that was called.
     * @param userName The username of the user on whose behalf this operation 
     * is being performed. This comes from the SAML assertion
     * @param callerIdentity The username of the user who is actually calling 
     * the operation. This comes from the SSL transport, and is taken from the 
     * end users certificate, when authenticated.
     */
    public void logMethodCall(String componentName, String methodName, String userName, String callerIdentity){
        //find client IP address
        MessageContext msgCtx = MessageContext.getCurrentContext();
        String ipAddr = null;
        if(msgCtx!=null){
        		ipAddr = msgCtx.getStrProp(Constants.MC_REMOTE_ADDR);
        }
        log.info("Method "+methodName+" of component "+componentName+" on "+hostAddress+" called from "+ipAddr+" by "+userName+" identified as "+callerIdentity);
    }

    
    /**
     * Log a method call.
     * 
     * @param componentName The name of the component whose method was called.
     * @param methodName The name of the method that was called.
     * @param userName The username of the user on whose behalf this operation 
     * is being performed. 
     * @param callerIdentity The dn of the user who is actually calling 
     * the operation. 
     * @param ipAddr The ip address of the remote computer
     */
    public void logMethodCall(String componentName, String methodName, String userName, String callerIdentity, String ipAddr){
        log.info("Method "+methodName+" of component "+componentName+" on "+hostAddress+" called from "+ipAddr+" by "+userName+" identified as "+callerIdentity);
    }

    
    /**
     * Log that access has been denied when an attempt has been made to
     * call a method.
     * 
     * @param componentName The name of the component whose method was called.
     * @param methodName The name of the method that was called.
     * @param userName The username of the user on whose behalf this operation 
     * is being performed. This comes from the SAML assertion
     * @param callerIdentity The username of the user who is actually calling 
     * the operation. This comes from the SSL transport, and is taken from the 
     * end users certificate, when authenticated.
     */
    public void logAccessDenied(String componentName, String methodName, String userName, String callerIdentity){
        log.warn("Access denied to "+methodName+" of component "+componentName+" for user "+userName+" identified as "+callerIdentity);
    }
    
    /**
     * Find the host name and IP address.
     * <p>
     * Inspired by an example at http://www.jguru.com/faq/view.jsp?EID=790132
     * 
     * @return Host name(s) and IP address(es)
     */
    public static String findHostAddress(){
        StringBuilder host = new StringBuilder();
        try{
            NetworkInterface iface = null;
            int counter = 0;
            for(Enumeration ifaces = NetworkInterface.getNetworkInterfaces();ifaces.hasMoreElements();){
                iface = (NetworkInterface)ifaces.nextElement();
                InetAddress ia = null;
                for(Enumeration ips = iface.getInetAddresses();ips.hasMoreElements();){
                    ia = (InetAddress)ips.nextElement();
                    //hopefully these options well only get us the useful entries!
                    if ( !ia.isLinkLocalAddress() && !ia.isLoopbackAddress() ){
                        if ( counter > 0 ){
                            host.append("; ");
                        }
                        host.append(ia.getCanonicalHostName())
                            .append(" (")
                            .append(ia.getHostAddress())
                            .append(")");
                        counter++;
                    }
                }
            }
        }
        catch(Exception ex){
            host.append("Unknown");
        }
        return host.toString();
    }
}

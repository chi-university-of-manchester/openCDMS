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

package org.psygrid.esl.services;

import javax.xml.rpc.ServiceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.esl.util.AAQCWrapper;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.PGSecurityInvalidSAMLException;
import org.psygrid.security.PGSecuritySAMLVerificationException;
import org.psygrid.security.accesscontrol.IAccessEnforcementFunction;
import org.springframework.context.ApplicationContext;
import org.springframework.remoting.jaxrpc.ServletEndpointSupport;

/**
 * @author Lucy Bridges
 *
 */
public class SecureSoapImpl extends ServletEndpointSupport {

	    /**
	     * General purpose logger
	     */
	    private static Log sLog = LogFactory.getLog(SecureSoapImpl.class);

	    /**
	     * Access enforcement function
	     */
	    protected IAccessEnforcementFunction accessControl = null;

		protected AAQCWrapper aaqcWrapper = null;
	    
	    
	    protected void onInit() throws ServiceException {
	        super.onInit();
	        ApplicationContext ctx = getWebApplicationContext();
	        accessControl = (IAccessEnforcementFunction) ctx.getBean("accessController");
	        try{
	            accessControl.initialise(this.getServletEndpointContext());
	            aaqcWrapper = (AAQCWrapper) ctx.getBean("aaqcWrapper");
	        }
	        catch(PGSecurityException ex){
	            sLog.error("onInit: "+ex.getClass().getSimpleName(),ex);
	            throw new ServiceException("An error occurred during initialisation of the access enforcement function", ex);
	        }
	    }

	    
	    public void destroy() {
	        super.destroy();
	    }
	    
	    public AAQCWrapper getAaqcWrapper() {
			return aaqcWrapper;
		}
	    
	    protected String findUserName(String saml){
	        
	        //find invoker's username
	        String userName = null;
	        try{
	            userName = accessControl.getUserFromUnverifiedSAML(saml);
	        }
	        catch(PGSecurityException ex){
	            userName = "Unknown";
	        }
	        return userName;
	    }
	    
}
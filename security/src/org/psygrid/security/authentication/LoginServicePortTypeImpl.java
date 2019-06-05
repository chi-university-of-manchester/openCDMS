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


//Created on Feb 9, 2006 by John Ainsworth
package org.psygrid.security.authentication;

import java.net.ConnectException;
import java.util.Date;

import javax.xml.rpc.ServiceException;
import javax.xml.rpc.server.ServiceLifecycle;

import org.apache.axis.Constants;
import org.apache.axis.MessageContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.logging.AuditLogger;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.SecureService;
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.psygrid.security.authentication.service.AccountDormantFaultMessage;
import org.psygrid.security.authentication.service.AccountLockedFaultMessage;
import org.psygrid.security.authentication.service.LoginServicePortType;
import org.psygrid.security.authentication.service.NotAuthorisedFaultMessage;
import org.psygrid.security.authentication.service.ProcessingFaultMessage;

/**
 * @author jda
 * 
 */
public class LoginServicePortTypeImpl extends SecureService implements
		LoginServicePortType, ServiceLifecycle {

	/** logger */
	private static Log sLog = LogFactory.getLog(LoginServicePortTypeImpl.class);
	/**
	 * Audit logger
	 */
	protected static AuditLogger logHelper = new AuditLogger(LoginServicePortTypeImpl.class);

	protected static final String COMPONENT_NAME = "LoginService";
	
	protected AAQueryClient aaqc = null;
	
	public void destroy() {
		super.destroy();
	}

	public void init(Object o) throws ServiceException {
		super.init(o);


		try {
			String propertiesFile = oContext.getServletContext().getInitParameter(
			"authenticationProperties");
			AuthenticationManager.initialise(propertiesFile);
		} catch (PGSecurityException pgse) {
			sLog.fatal("failed to initiliase Authentication Service");
			throw new ServiceException(pgse);
		}
		try{
			String propertiesFile = oContext.getServletContext().getInitParameter("attributeAuthorityProperties");	
			aaqc = new AAQueryClient(propertiesFile);
		} catch (PGSecurityException p){
			throw new ServiceException(
					"Service not available: configuration error: missing attributeAuthorityProperties entry in web.xml");			
		}
	}

	public java.lang.String login(java.lang.String username,
			short[] passwd) throws java.rmi.RemoteException,
			NotAuthorisedFaultMessage, ProcessingFaultMessage, AccountLockedFaultMessage, AccountDormantFaultMessage {

		AuthenticationManager am = null;
		final String METHOD_NAME = "login";
		logHelper
				.logMethodCall(COMPONENT_NAME, METHOD_NAME, username, username);

		
		try {
			am = new AuthenticationManager();
			if(!am.checkUID(username)){
				logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME+": unknown user id",
						username, username);
				throw new NotAuthorisedFaultMessage();
			}
			char[] password = new char[passwd.length];
			for (int i = 0; i < password.length; i++) {
				password[i] = (char) passwd[i];
			}
			String credential = am.login(username, password);
			
	        MessageContext msgCtx = MessageContext.getCurrentContext();
	        String ipAddr = msgCtx.getStrProp(Constants.MC_REMOTE_ADDR);

			if (credential == null) {
				if(aaqc.recordLoginAttempt(username, false, new Date(), ipAddr, null)){
					throw new AccountLockedFaultMessage();
				}
				logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME+": wrong password",
						username, username);
				throw new NotAuthorisedFaultMessage();
			}
			if(aaqc.recordLoginAttempt(username, true, new Date(), ipAddr, credential)){
				if(aaqc.isAccountDormant(username)){
					throw new AccountDormantFaultMessage();
				} else {
					throw new AccountLockedFaultMessage();
				}
			}
			return credential;
		} catch (PGSecurityException pgse) {
			throw new ProcessingFaultMessage(pgse);
		} catch(ConnectException ce){
			throw new ProcessingFaultMessage(ce);			
		}
	}
	
	public java.util.Date getTimeAtServer() throws java.rmi.RemoteException,
			org.psygrid.security.authentication.service.ProcessingFaultMessage {
		return new Date();
	}
	
    public java.lang.String getVersion() throws java.rmi.RemoteException
    {
    		return getServletContext().getInitParameter("version");
    }
}
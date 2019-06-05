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

import java.io.IOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.apache.axis.AxisFault;
import org.apache.axis.encoding.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.collection.entry.remote.RemoteServiceFault;
import org.psygrid.security.authentication.service.AccountDormantFaultMessage;
import org.psygrid.security.authentication.service.AccountLockedFaultMessage;
import org.psygrid.security.authentication.service.Login;
import org.psygrid.security.authentication.service.LoginLocator;
import org.psygrid.security.authentication.service.LoginServicePortType;
import org.psygrid.security.authentication.service.NotAuthorisedFaultMessage;
import org.psygrid.security.authentication.service.ProcessingFaultMessage;
import org.psygrid.security.components.net.PsyGridClientSocketFactory;

public class LoginClient extends SecurityClient  {
	
    private static final Log LOG = LogFactory.getLog(LoginClient.class);
    
    LoginClient(LoginServicePortType port, String endPointAddress)  {
        super(port, endPointAddress);
    }
    
    public static LoginClient getInstance() {
        Login locator = new LoginLocator();
        LoginServicePortType port = null;
        try {
            port = locator.getLoginServicePortTypeSOAPPort();
        } catch (ServiceException e) {
            SecurityHelper.wrapIntoRuntimeExceptionAndThrow(e);
        }
        String endPointAddress = locator.getLoginServicePortTypeSOAPPortAddress();
        LoginClient client = new LoginClient(port, endPointAddress);
        client.init();
        return client;
    }
    
    public void login(String userName, char[] password)
            throws RemoteServiceFault, AuthenticationException, AccountLockedException, AccountDormantException, IOException {
    	
        short[] passwordShorts = new short[password.length];
        for (int i = 0; i < password.length; ++i) {
            passwordShorts[i] = (short) password[i];
        }
        try {
        String credential = getPort().login(userName, passwordShorts);
        for (int i = 0; i < passwordShorts.length; ++i) {
            passwordShorts[i] = 0;
        }

        byte[] keyStore = Base64.decode(credential);
        PersistenceManager.getInstance().saveKeyStore(keyStore);
 
        //Force the new key in over the existing key
        //Bug#487
        
        System.setProperty("javax.net.ssl.keyStorePassword", new String(password)); //$NON-NLS-1$ 
        PsyGridClientSocketFactory.reinit();
 
        } catch (NotAuthorisedFaultMessage nafm) {
            throw new AuthenticationException(nafm);
        } catch (AccountLockedFaultMessage alfm) {
            throw new AccountLockedException(alfm);
        } catch (AccountDormantFaultMessage adfm) {
            throw new AccountDormantException(adfm);
        } catch (ProcessingFaultMessage pfm) {
            throw new RemoteServiceFault(pfm);
        } catch (AxisFault fault) {
            if ( fault.getCause() instanceof ConnectException ){
            	LOG.info("Handling fault with cause = ConnectException");
                throw (ConnectException)fault.getCause();
            }
            else if ( fault.getCause() instanceof UnknownHostException ||
                    fault.getCause() instanceof NoRouteToHostException ){
            	LOG.info("Handling fault with cause = UnknownHostException/NoRouteToHostException");
                ConnectException cex = new ConnectException(fault.getCause().getMessage());
                cex.initCause(fault.getCause());
                throw cex;
            }
            else if ( fault.getCause() instanceof SocketTimeoutException ){
            	LOG.info("Handling fault with cause = SocketTimeoutException");
                throw (SocketTimeoutException)fault.getCause();
            }
            else if ( null == fault.getCause() && fault.getFaultCode().getLocalPart().equals("HTTP") ){
            	LOG.info("Handling fault with null cause, code = HTTP");
                SocketTimeoutException ste = new SocketTimeoutException("Connection dropped.");
                ste.initCause(fault);
                throw ste;
            }
            else if ( fault.getCause() instanceof IOException ){
            	LOG.info("Handling fault with cause = IOException");
                throw (IOException)fault.getCause();
            }
            else{
            	if ( null != fault.getCause() ){
            		LOG.fatal(fault.getCause().getClass().getName());
            	}
            	else{
            		LOG.fatal("fault.getCause() returned null");
            	}
                LOG.fatal(fault.getMessage(), fault);
                if ( null != fault.getCause() ){
                    throw new RuntimeException(fault.getCause());
                }
                else{
                    throw new RuntimeException(fault);
                }
            }
        }
        catch (RemoteException re) {
            throw new RuntimeException(re);
        }
    }
    
    @Override
    protected final LoginServicePortType getPort() {
        return (LoginServicePortType) port;
    }

    public String getVersion() throws ConnectException, RemoteServiceFault {
		try {
			return getPort().getVersion();
		} catch (AxisFault af) {
			Throwable cause = af.getCause();
			if (cause instanceof ConnectException) {
				LOG.info("ConnectException: " + cause.getMessage());
				cause.printStackTrace();
				throw (ConnectException) cause;
			} else if (cause instanceof UnknownHostException) {
				LOG.info("UnknownHostException: " + cause.getMessage());
				ConnectException ce = new ConnectException(cause.getMessage());
				ce.initCause(cause);
				throw ce;
			} else if (cause instanceof NoRouteToHostException) {
				LOG.info("NoRouteToHostException: " + cause.getMessage());
				ConnectException ce = new ConnectException(cause.getMessage());
				ce.initCause(cause);
				throw ce;
			} else {
				LOG.info("RemoteServiceFault: " + cause.getMessage());
				cause.printStackTrace();
				throw new RemoteServiceFault(af);
			}
		} catch (RemoteException re) {
			LOG.info("Remote Exception " + re.getMessage());
			re.printStackTrace();
			throw new RuntimeException(re);
		}
	}
}

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


package org.psygrid.securitymanager.security;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.xml.rpc.ServiceException;

import org.apache.axis.AxisFault;
import org.apache.axis.encoding.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opensaml.SAMLAssertion;
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.psygrid.security.attributeauthority.service.AttributeAuthorityService;
import org.psygrid.security.attributeauthority.service.AttributeAuthorityServiceLocator;
import org.psygrid.security.authentication.service.AccountDormantFaultMessage;
import org.psygrid.security.authentication.service.Login;
import org.psygrid.security.authentication.service.LoginLocator;
import org.psygrid.security.authentication.service.LoginServicePortType;
import org.psygrid.security.authentication.service.AccountLockedFaultMessage;
import org.psygrid.security.authentication.service.NotAuthorisedFaultMessage;
import org.psygrid.security.authentication.service.ProcessingFaultMessage;
import org.psygrid.security.components.net.PsyGridClientSocketFactory;

import org.psygrid.securitymanager.security.AccountLockedException;
import org.psygrid.www.xml.security.core.types.ProjectType;

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
            throws RemoteServiceFault, 
            AuthenticationException,
            AccountLockedException,
            AccountDormantException,
            IOException {

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
			System.setProperty("javax.net.ssl.keyStore",  PersistenceManager.getInstance().getKeyStoreLocation());
			System.setProperty("javax.net.ssl.keyStorePassword", new String(password)); //$NON-NLS-1$ //$NON-NLS-2$
			PsyGridClientSocketFactory.reinit();

        } catch (NotAuthorisedFaultMessage nafm) {
            LOG.error("LoginClient: login " + nafm.getMessage());
        	throw new AuthenticationException(nafm);
        } catch (AccountLockedFaultMessage alfm) {
        	LOG.error("LoginClient: login " + alfm.getMessage());
            throw new AccountLockedException(alfm);
        } catch (AccountDormantFaultMessage alfm) {
        	LOG.error("LoginClient: login " + alfm.getMessage());
            throw new AccountDormantException(alfm);
        } catch (ProcessingFaultMessage pfm) {
        	LOG.error("LoginClient : login " + pfm.getMessage());
        	throw new RemoteServiceFault(pfm);
        } catch (AxisFault af) {
        	LOG.error("LoginClient : login " + af.getMessage());
            Throwable cause = af.getCause();
            if (cause instanceof ConnectException) {
                LOG.error("LoginClient: connection exception");
            	throw (ConnectException) cause;
            }
            else if (cause instanceof UnknownHostException || 
                    cause instanceof NoRouteToHostException) {
                ConnectException ce = new ConnectException(cause.getMessage());
                ce.initCause(cause);
                throw ce;
            }
            else {
//    			JOptionPane.showMessageDialog(
//    					new JDialog(),
//    					"There is a problem connecting to the server.  Please login again.", //$NON-NLS-1$
//    					"Connectivity problem. ", //$NON-NLS-1$
//    					JOptionPane.ERROR_MESSAGE);
            	LOG.error("LoginClient: logging in runtime exception");
//    			System.exit(0);
    			throw new RuntimeException(af);
            }
        }
        catch (RemoteException re) {
        	LOG.error("LoginClient: login " + re.getMessage());
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
				throw (ConnectException) cause;
			} else if (cause instanceof UnknownHostException) {
				LOG.info("UnknownHostException: " + cause.getMessage());
				ConnectException ce = new ConnectException(cause.getMessage());
				throw ce;
			} else if (cause instanceof NoRouteToHostException) {
				LOG.info("NoRouteToHostException: " + cause.getMessage());
				ConnectException ce = new ConnectException(cause.getMessage());
				throw ce;
			} else {
				LOG.info("RemoteServiceFault: " + cause.getMessage());
				throw new RemoteServiceFault(af);
			}
		} catch (RemoteException re) {
			LOG.info("Remote Exception " + re.getMessage());
			throw new RuntimeException(re);
		}
	}
}

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

import java.io.IOException;
import java.net.ConnectException;
import java.rmi.RemoteException;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.auth.LoginService;
import org.psygrid.common.proxy.ProxyAuthenticationDialog;
import org.psygrid.common.security.LoginInterfaceFrame;
import org.psygrid.securitymanager.ExceptionsHelper;
import org.psygrid.securitymanager.utils.PropertiesHelper;

public class EntryLoginService extends LoginService {

    private boolean skipLogin = false;
    
    private static final Log LOG = LogFactory.getLog(SecurityManager.class);
    
    private LoginInterfaceFrame frame;
    
    public EntryLoginService(LoginInterfaceFrame frame) {
    	this.frame = frame;
    }
    
    @Override
    public boolean authenticate(String name, char[] password, String server)
            throws AuthenticationException, IOException, RemoteServiceFault {
		if(frame.isDoProxyAuth()){
			ProxyAuthenticationDialog dlg = new ProxyAuthenticationDialog(frame);
			dlg.setVisible(true);
		}

		
		try
		{
	    	return SecurityManager.getInstance().login(name, password, skipLogin);
		} catch (AccountLockedException ale) {
			String title = PropertiesHelper.getPropertyHelper()
				.getStringFor("SecurityManager.accountLockedTitle"); //$NON-NLS-1$
			String message = PropertiesHelper.getPropertyHelper()
				.getStringFor("SecurityManager.accountLockedMessage"); //$NON-NLS-1$
			JOptionPane.showMessageDialog(frame, message, title,
				JOptionPane.ERROR_MESSAGE);
			System.exit(0);
			return false;
		} catch (AccountDormantException ade) {
			String title = PropertiesHelper.getPropertyHelper()
				.getStringFor("SecurityManager.accountDormantTitle"); //$NON-NLS-1$
			String message = PropertiesHelper.getPropertyHelper()
				.getStringFor("SecurityManager.accountDormantMessage"); //$NON-NLS-1$
			JOptionPane.showMessageDialog(frame, message, title,
				JOptionPane.ERROR_MESSAGE);
			System.exit(0);
			return false;	
		} catch (RemoteException rex)
		{
			ExceptionsHelper.handleRemoteException(frame, rex);
			System.exit(0);
			return false;
		} catch (ConnectException cex)
		{
			ExceptionsHelper.handleConnectException(frame, cex);
			System.exit(0);
			return false;
		}

    }
}

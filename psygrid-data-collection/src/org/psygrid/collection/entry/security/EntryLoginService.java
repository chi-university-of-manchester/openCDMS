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
import java.rmi.RemoteException;

import javax.swing.JOptionPane;

import org.jdesktop.swingx.auth.LoginService;
import org.psygrid.collection.entry.remote.RemoteServiceFault;
import org.psygrid.common.proxy.ProxyAuthenticationDialog;
import org.psygrid.common.security.LoginInterfaceFrame;

public class EntryLoginService extends LoginService {

    private boolean skipLogin = false;
    
    private LoginInterfaceFrame application = null;
    
    private Result result = Result.UNKNOWN;
    
    public EntryLoginService() {
        // Empty constructor
    }
    
    /**
     * This constructor should only be used for testing. Otherwise, use
     * the default constructor or pass <code>skipLogin</code> as false. 
     * If <code>skipLogin</code> is true, the client will not check the accuracy 
     * of the login before proceeding.
     * 
     * Do not use this in production as it will cause errors in the decryption
     * of the stored data and none of the remote operations will work correctly
     * in a secured repository.
     * 
     * @param skipLogin if true, login becomes a no-op. If false, it's 
     * equivalent to the default constructor.
     * 
     * @see #EntryLoginService()
     */
    public EntryLoginService(boolean skipLogin, LoginInterfaceFrame app ) {
        this.skipLogin = skipLogin;
        this.application = app;
    }
    
    @Override
    public boolean authenticate(String name, char[] password, String server)
            throws AuthenticationException, RemoteException, IOException,
            RemoteServiceFault {
		if(application.isDoProxyAuth()  &&	SecurityManager.getInstance().getLoginStatus() == SecurityManager.LoginStatus.PreInitialLogin){
			ProxyAuthenticationDialog dlg = new ProxyAuthenticationDialog(application);
			dlg.setVisible(true);
		}
		try {
			boolean loginResult = SecurityManager.getInstance().login(name, password, skipLogin);
			if ( loginResult ){
				this.result = Result.SUCCESS;
			}
			else{
				this.result = Result.FAILURE;
			}
			return loginResult;
		} catch (AccountLockedException ale) {
			this.result = Result.LOCKED;
			return false;
		} catch (AccountDormantException ade) {
			String title = SecurityMessages
			.getString("SecurityManager.accountDormantTitle"); //$NON-NLS-1$
			String message = SecurityMessages
			.getString("SecurityManager.accountDormantMessage"); //$NON-NLS-1$
			JOptionPane.showMessageDialog(application, message, title,
					JOptionPane.ERROR_MESSAGE);
			System.exit(0);
			return false;
		}
		catch (FirstLoginNoConnectionException ex){
			this.result = Result.FIRST_NO_CONNECT;
			return false;
		}
    }
    
    public enum Result{ UNKNOWN, SUCCESS, FAILURE, LOCKED, FIRST_NO_CONNECT }

	public Result getResult() {
		return result;
	}
}

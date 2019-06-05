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

package org.psygrid.collection.entry.ui;

import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.File;
import java.util.List;

import javax.imageio.ImageIO;

import org.jdesktop.swingx.JXLoginPanel;
import org.jdesktop.swingx.auth.LoginService;
import org.jdesktop.swingx.auth.PasswordStore;
import org.jdesktop.swingx.auth.UserNameStore;

/**
 * Login panel that is housed by PsygridLoginDialog. This class is
 * just required to allow external access to some of the properties
 * of the base class, JXLoginPanel.
 * 
 * @author Rob Harper
 *
 */
public class PsygridLoginPanel2 extends JXLoginPanel {

    private static final long serialVersionUID = -98975032798171437L;

    public PsygridLoginPanel2() {
        super();
    }

    public PsygridLoginPanel2(LoginService arg0, PasswordStore arg1, UserNameStore arg2, List<String> arg3) {
        super(arg0, arg1, arg2, arg3);
    }

    public PsygridLoginPanel2(LoginService arg0, PasswordStore arg1, UserNameStore arg2) {
        super(arg0, arg1, arg2);
    }

    public PsygridLoginPanel2(LoginService arg0) {
        super(arg0);
    }

    /**
     * Set the status of the panel to "Cancelled"
     */
    public void cancel(){
        setStatus(Status.CANCELLED);
    }
    
    /**
     * Cancel the login process.
     * <p>
     * Only required to expose this method as public, as it is protected
     * in the base class.
     */
    @Override
    public void cancelLogin(){
        super.cancelLogin();
    }

	@Override
	protected Image createLoginBanner() {
		Image i = super.createLoginBanner();
		try{
			ImageIO.write((RenderedImage)i, "png", new File("/home/rsh/login_banner.png"));
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		return i;
	}

}

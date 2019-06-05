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

package org.psygrid.securitymanager.ui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.jgoodies.forms.factories.ButtonBarFactory;

import org.psygrid.securitymanager.utils.PropertiesHelper;

public class ForceChangePasswordDialog extends ChangePasswordDialog {

	private static final long serialVersionUID = 1L;

	public ForceChangePasswordDialog(JFrame parent) {
		super(parent);
		//let the windowClosing handler deal with a close event
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		//do not let the window be dismissed without closing the application
		addWindowListener(new CloseWindowAdapter());
	}

	private class CloseWindowAdapter extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			int n = JOptionPane.showConfirmDialog(
	                (JFrame)getOwner(), PropertiesHelper.getPropertyHelper().getStringFor("ForceChangePasswordDialog.appwillexit") +
	                PropertiesHelper.getPropertyHelper().getStringFor("ForceChangePasswordDialog.appwishtoexit"),
	                PropertiesHelper.getPropertyHelper().getStringFor("ForceChangePasswordDialog.confirmexit"),
	                JOptionPane.YES_NO_OPTION);
	        if (n == JOptionPane.YES_OPTION) {
	    		System.exit(0);
	        } else if (n == JOptionPane.NO_OPTION) {
	        	return;
	        } 
		}
	}
	
	@Override
	protected JPanel buildButtonPanel() {
		return ButtonBarFactory.buildOKBar(okButton);
	}
	
}

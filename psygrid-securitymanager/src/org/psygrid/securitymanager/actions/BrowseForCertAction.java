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

package org.psygrid.securitymanager.actions;


import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JDialog;
import javax.swing.JTextField;

import org.psygrid.securitymanager.utils.PropertiesHelper;

import org.psygrid.securitymanager.ui.TextFieldWithStatus;

/**
 * Class that handles the action for browsing for a 
 * certificate to select to login with.
 * 
 * @author pwhelan
 */
public class BrowseForCertAction extends AbstractAction
{

	private JDialog dialog;
	private TextFieldWithStatus keystoreField;
	
	/**
	 * 
	 * @param dialog
	 * @param keystoreField
	 */
	public BrowseForCertAction(JDialog dialog, TextFieldWithStatus keystoreField)
	{
		super(PropertiesHelper.getPropertyHelper().getStringFor("org.psygrid.securitymanager.actions.browseforcert"));
		this.dialog = dialog;
		this.keystoreField = keystoreField;
	}
	
	public void actionPerformed(ActionEvent e) {
		JFileChooser chooser = new JFileChooser();
		  int returnVal = chooser.showOpenDialog(dialog);
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		       keystoreField.setText(chooser.getSelectedFile().getAbsolutePath());
		    }
	}
}
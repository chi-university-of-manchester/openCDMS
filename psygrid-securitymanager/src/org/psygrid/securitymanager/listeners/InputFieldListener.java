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

package org.psygrid.securitymanager.listeners;

import org.psygrid.securitymanager.model.UserModel;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import javax.swing.text.BadLocationException;

public class InputFieldListener implements DocumentListener
{
	public final static int FIRST_NAME = 0;
	public final static int LAST_NAME = 1;
	public final static int EMAIL_ADDRESS = 2;
	public final static int USER_ID = 3;
	
	private int type;
	
	public InputFieldListener(int type) {
		this.type = type;
	}


	public void changedUpdate(DocumentEvent e) {
		String inputText = "";
		try
		{
			inputText = e.getDocument().getText(0, e.getDocument().getLength());
		} catch (BadLocationException docex)
		{
			//hopefully we'll never get here
			//if we do, just use the already assigned ""
		}
		
		switch (type) 
		{
			case FIRST_NAME:
				UserModel.getInstance().setFirstname(inputText);
				break;
			case LAST_NAME:
				UserModel.getInstance().setLastname(inputText);
				break;
			case EMAIL_ADDRESS:
				UserModel.getInstance().setEmailAddress(inputText, false);
				break;
			case USER_ID:
				UserModel.getInstance().setUserID(inputText);
				break;
		}
	}

	public void insertUpdate(DocumentEvent e) {
		changedUpdate(e);
	}

	public void removeUpdate(DocumentEvent e) {
		changedUpdate(e);
	}
	
}
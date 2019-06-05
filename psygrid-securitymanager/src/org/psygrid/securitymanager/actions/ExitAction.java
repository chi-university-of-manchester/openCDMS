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

import java.io.*;

import javax.swing.AbstractAction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.securitymanager.security.PersistenceManager;
import org.psygrid.securitymanager.utils.PropertiesHelper;

/**
 * Action to exit the application
 * 
 * @author pwhelan
 *
 */
public class ExitAction extends AbstractAction
{
	private static final Log LOG = LogFactory.getLog(ExitAction.class);
	
	/**
	 * Basic Exit Action with text.
	 *
	 */
	public ExitAction()
	{
		super(PropertiesHelper.getPropertyHelper().getStringFor("org.psygrid.securitymanager.actions.exit"));
	}
	
	
	public void actionPerformed(ActionEvent e) 
	{
		try
		{
			PersistenceManager.getInstance().dispose();
		} catch (IOException ex)
		{
			LOG.error("Exit Action : error disposing of persistence manager " + ex.getMessage());
		}
		System.exit(0);
	}
	
	
}

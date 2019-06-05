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

/**
 * @author pwhelan
 *
 */

package org.psygrid.securitymanager.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.securitymanager.controller.ModelFetchingController;
import org.psygrid.securitymanager.utils.IconsHelper;
import org.psygrid.securitymanager.utils.PropertiesHelper;
import org.psygrid.securitymanager.security.SecurityManager;

import org.psygrid.securitymanager.Application;


/**
 * Action that refreshes the main tree
 * @author pwhelan
 *
 */
public class RefreshAction extends AbstractAction
{

	private static final Log LOG = LogFactory.getLog(RefreshAction.class);
	
	private Application application;
	
	/**
	 * Refresh the main tree
	 * @param application the main applicaiton  
	 */
	public RefreshAction(Application application)
	{
		super(PropertiesHelper.getPropertyHelper().getStringFor("org.psygrid.securitymanager.actions.refresh"),
				IconsHelper.getInstance().getImageIcon("reload.png"));
		this.application = application;
		
	}

	/**
	 * Refresh the main tree; 
	 * @param application the main applicaiton
	 * @param isMenu; indicates that this is for an item in the file menu  
	 */
	public RefreshAction(Application application, boolean isMenu)
	{
		super(PropertiesHelper.getPropertyHelper().getStringFor("org.psygrid.securitymanager.actions.refresh"));
		this.application = application;
	}
	
	/**
	 * Refresh the main application tree
	 * and reset logged in user's projects and roles etc.
	 */
	public void actionPerformed(ActionEvent e) {
		//refresh the security key before resetting the models
		application.reinit();
		ModelFetchingController.getInstance().reset();
	}
	
}
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

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.psygrid.securitymanager.utils.IconsHelper;
import org.psygrid.securitymanager.utils.PropertiesHelper;

import org.psygrid.securitymanager.wizard.Wizard;
import org.psygrid.securitymanager.wizard.WizardPanelDescriptor;
import org.psygrid.securitymanager.wizarddescriptors.AddGroupsDescriptor;
import org.psygrid.securitymanager.wizarddescriptors.AddProjectsDescriptor;
import org.psygrid.securitymanager.wizarddescriptors.AddRolesDescriptor;
import org.psygrid.securitymanager.wizarddescriptors.AddUserDescriptor;
import org.psygrid.securitymanager.wizarddescriptors.ConfirmSelectionDescriptor;
import org.psygrid.securitymanager.wizarddescriptors.ResultPanelDescriptor;
import org.psygrid.securitymanager.wizarddescriptors.SetPasswordPanelDescriptor;

/**
 * Action used to add a user to the security system.
 * 
 * @author pwhelan
 *
 */
public class AddUserAction extends AbstractAction
{

	/**
	 * An action invoked from the file menu; no icon
	 * @param fileMenu
	 */
	public AddUserAction(boolean fileMenu)
	{
		super(PropertiesHelper.getPropertyHelper().getStringFor("org.psygrid.securitymanager.actions.adduser"));
	}
	
	/**
	 * Generic reusable action with small icon
	 *
	 */
	public AddUserAction()
	{
		super( " " + PropertiesHelper.getPropertyHelper().getStringFor("org.psygrid.securitymanager.actions.adduser" ) + "    "
				,IconsHelper.getInstance().getImageIcon("Plus16.png"));
	}

	/**
	 * Generic resuable action with big icon
	 * @param bigIcons
	 */
	public AddUserAction(int bigIcons)
	{
		super( " " + PropertiesHelper.getPropertyHelper().getStringFor("org.psygrid.securitymanager.actions.adduser" ) + "    "
				,IconsHelper.getInstance().getImageIcon("Plus24.png"));
	}
	
	/**
	 * Create the add user wizard; show it and position on screen. 
	 */
	 public void actionPerformed(ActionEvent e) {
		
		Wizard wizard = new Wizard();
        wizard.getDialog().setTitle(PropertiesHelper.getPropertyHelper().getStringFor("org.psygrid.securitymanager.actions.adduser.wizardtitle"));
    	
        ((java.awt.Frame)wizard.getDialog().getOwner()).setIconImage(IconsHelper.getInstance().getImageIcon("psygrid.jpg").getImage());
        
        WizardPanelDescriptor descriptor1 = new AddUserDescriptor();
        wizard.registerWizardPanel(AddUserDescriptor.IDENTIFIER, descriptor1);
        
        WizardPanelDescriptor descriptor3 = new ConfirmSelectionDescriptor();
        wizard.registerWizardPanel(ConfirmSelectionDescriptor.IDENTIFIER, descriptor3);
        
        WizardPanelDescriptor descriptor4 = new AddProjectsDescriptor(false);
        wizard.registerWizardPanel(AddProjectsDescriptor.IDENTIFIER, descriptor4);

        WizardPanelDescriptor descriptor5 = new AddGroupsDescriptor();
        wizard.registerWizardPanel(AddGroupsDescriptor.IDENTIFIER, descriptor5);

        WizardPanelDescriptor descriptor6 = new AddRolesDescriptor();
        wizard.registerWizardPanel(AddRolesDescriptor.IDENTIFIER, descriptor6);
        
        WizardPanelDescriptor descriptor7 = new SetPasswordPanelDescriptor();
        wizard.registerWizardPanel(SetPasswordPanelDescriptor.IDENTIFIER, descriptor7);
        
        WizardPanelDescriptor descriptor8 = new ResultPanelDescriptor();
        wizard.registerWizardPanel(ResultPanelDescriptor.IDENTIFIER, descriptor8);
        
        wizard.setCurrentPanel(AddUserDescriptor.IDENTIFIER);
        
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension wizardSize = wizard.getDialog().getPreferredSize();
        
        //center the dialog on screen
        wizard.getDialog().setLocation((int)(size.getWidth()/2 - (wizardSize.getWidth()/2)), 
        							(int)(size.getHeight()/2 - (wizardSize.getHeight()/2)));

        wizard.showModalDialog();
	}
	
	
	
	
}

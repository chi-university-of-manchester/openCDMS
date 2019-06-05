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
import org.psygrid.securitymanager.wizarddescriptors.ConfirmDeletionDescriptor;
import org.psygrid.securitymanager.wizarddescriptors.DeleteUserDescriptor;
import org.psygrid.securitymanager.wizarddescriptors.DeleteProjectsDescriptor;
import org.psygrid.securitymanager.wizarddescriptors.ResultPanelDescriptor;
import org.psygrid.securitymanager.wizarddescriptors.MultiUserPanelDescriptor;

/**
 * Action that creates the delete user wizard dialog.
 * @author pwhelan
 *
 */
public class DeleteUserAction extends AbstractAction
{

	/**
	 * Create the wizard from the file menu; no icon
	 * @param fileMenu
	 */
	public DeleteUserAction(boolean fileMenu)
	{
		super(PropertiesHelper.getPropertyHelper().getStringFor("org.psygrid.securitymanager.actions.deleteuser"));
	}
	
	/**
	 * Create wizard; action with big icon
	 * @param bigIcons
	 */
	public DeleteUserAction(int bigIcons)
	{
		super(PropertiesHelper.getPropertyHelper().getStringFor("org.psygrid.securitymanager.actions.deleteuser")
				, IconsHelper.getInstance().getImageIcon("Delete24.png"));
	}

	/**
	 * Create wizard; action with little icon
	 *
	 */
	public DeleteUserAction()
	{
		super(PropertiesHelper.getPropertyHelper().getStringFor("org.psygrid.securitymanager.actions.deleteuser")
				, IconsHelper.getInstance().getImageIcon("Delete16.png"));
	}
	
	/**
	 * Create the wizard; show it and position on screen
	 */
	public void actionPerformed(ActionEvent e) {
    	Wizard wizard = new Wizard();
        wizard.getDialog().setTitle(PropertiesHelper.getPropertyHelper().getStringFor("org.psygrid.securitymanager.actions.deleteuser.wizardtitle"));	
        ((java.awt.Frame)wizard.getDialog().getOwner()).setIconImage(IconsHelper.getInstance().getImageIcon("psygrid.jpg").getImage());
        
        WizardPanelDescriptor descriptor1 = new DeleteUserDescriptor();
        wizard.registerWizardPanel(DeleteUserDescriptor.IDENTIFIER, descriptor1);

        WizardPanelDescriptor descriptor2 = new DeleteProjectsDescriptor();
        wizard.registerWizardPanel(DeleteProjectsDescriptor.IDENTIFIER, descriptor2);
        
        WizardPanelDescriptor descriptor3 = new ConfirmDeletionDescriptor();
        wizard.registerWizardPanel(ConfirmDeletionDescriptor.IDENTIFIER, descriptor3);
        
        WizardPanelDescriptor descriptor7 = new ResultPanelDescriptor();
        wizard.registerWizardPanel(ResultPanelDescriptor.IDENTIFIER, descriptor7);
        
        WizardPanelDescriptor descriptor8 = new MultiUserPanelDescriptor(MultiUserPanelDescriptor.DELETE_USER);
        wizard.registerWizardPanel(MultiUserPanelDescriptor.IDENTIFIER, descriptor8);
        
        wizard.setCurrentPanel(DeleteUserDescriptor.IDENTIFIER);
        
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension wizardSize = wizard.getDialog().getPreferredSize();
        
        //center the dialog on screen
        wizard.getDialog().setLocation((int)(size.getWidth()/2 - (wizardSize.getWidth()/2)), 
        							(int)(size.getHeight()/2 - (wizardSize.getHeight()/2)));

        wizard.showModalDialog();

	}
	
}
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
import org.psygrid.securitymanager.wizarddescriptors.MultiUserPanelDescriptor;
import org.psygrid.securitymanager.wizarddescriptors.ResetPasswordPanelDescriptor;
import org.psygrid.securitymanager.wizarddescriptors.ResetPasswordUserPanelDescriptor;


/**
 * Action that creates the delete user wizard dialog.
 * @author pwhelan
 *
 */
public class ResetPasswordAction extends AbstractAction
{

	/**
	 * Create the wizard from the file menu; no icon
	 * @param fileMenu
	 */
	public ResetPasswordAction(boolean fileMenu)
	{
		super(PropertiesHelper.getPropertyHelper().getStringFor("org.psygrid.securitymanager.actions.resetpassword"));
	}
	
	/**
	 * Create wizard; action with big icon
	 * @param bigIcons
	 */
	public ResetPasswordAction(int bigIcons)
	{
		super(PropertiesHelper.getPropertyHelper().getStringFor("org.psygrid.securitymanager.actions.resetpassword")
				, IconsHelper.getInstance().getImageIcon("Undo24.png"));
	}

	/**
	 * Create wizard; action with little icon
	 *
	 */
	public ResetPasswordAction()
	{
		super(PropertiesHelper.getPropertyHelper().getStringFor("org.psygrid.securitymanager.actions.resetpassword")
				, IconsHelper.getInstance().getImageIcon("Undo16.png"));
	}
	
	/**
	 * Create the wizard; show it and position on screen
	 */
	public void actionPerformed(ActionEvent e) {
		Wizard wizard = new Wizard();
	    wizard.getDialog().setTitle(PropertiesHelper.getPropertyHelper().getStringFor("org.psygrid.securitymanager.actions.resetpassword"));	
	    ((java.awt.Frame)wizard.getDialog().getOwner()).setIconImage(IconsHelper.getInstance().getImageIcon("psygrid.jpg").getImage());
        
        WizardPanelDescriptor descriptor1 = new ResetPasswordUserPanelDescriptor();
        wizard.registerWizardPanel(ResetPasswordUserPanelDescriptor.IDENTIFIER, descriptor1);

        WizardPanelDescriptor descriptor2 = new ResetPasswordPanelDescriptor();
        wizard.registerWizardPanel(ResetPasswordPanelDescriptor.IDENTIFIER, descriptor2);
        
        WizardPanelDescriptor descriptor3 = new MultiUserPanelDescriptor(MultiUserPanelDescriptor.PASSWORD);
        wizard.registerWizardPanel(MultiUserPanelDescriptor.IDENTIFIER, descriptor3);
        
        wizard.setCurrentPanel(ResetPasswordUserPanelDescriptor.IDENTIFIER);
        
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension wizardSize = wizard.getDialog().getPreferredSize();
        
        //center the dialog on screen
        wizard.getDialog().setLocation((int)(size.getWidth()/2 - (wizardSize.getWidth()/2)), 
        							(int)(size.getHeight()/2 - (wizardSize.getHeight()/2)));

        wizard.showModalDialog();
	}
	
}
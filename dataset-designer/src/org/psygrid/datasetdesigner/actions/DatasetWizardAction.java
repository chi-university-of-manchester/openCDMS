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
package org.psygrid.datasetdesigner.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.psygrid.common.ui.WrappedJOptionPane;
import org.psygrid.datasetdesigner.controllers.DatasetController;
import org.psygrid.datasetdesigner.controllers.TempFileController;
import org.psygrid.datasetdesigner.ui.MainFrame;
import org.psygrid.datasetdesigner.utils.DatasetUtility;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

import org.psygrid.datasetdesigner.ui.wizard.Wizard;
import org.psygrid.datasetdesigner.ui.wizard.WizardPanelDescriptor;
import org.psygrid.datasetdesigner.utils.IconsHelper;

import org.psygrid.datasetdesigner.ui.wizard.descriptors.*;

/**
 * Show the new DataSet Wizard
 * 
 * @author pwhelan
 */
public class DatasetWizardAction extends AbstractAction {
	
	/**
	 * The main window of the application
	 */
	private MainFrame mainFrame;
	
	/**
	 * Constructor
	 * Set the title and the parent window
	 * @param frame
	 */
	public DatasetWizardAction(MainFrame mainFrame) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.wizardtitle"));
		this.mainFrame = mainFrame;
	}
	
	/**
	 * Create the panel descriptors and panels
	 * Show the wizard dialog 
	 * @param aet The calling action event
	 */
	public void actionPerformed(ActionEvent aet) {
		Wizard wizard = new Wizard(mainFrame);
		
		mainFrame.getMainMenuBar().setDelContext(false);
		
        wizard.getDialog().setTitle(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.wizardtitle"));
        ((java.awt.Frame)wizard.getDialog().getOwner()).setIconImage(IconsHelper.getInstance().getImageIcon("psygrid.jpg").getImage());
        
        //properties panel
        WizardPanelDescriptor descriptor1 = new BasicPropertiesDescriptor(wizard.getModel());
        wizard.registerWizardPanel(BasicPropertiesDescriptor.IDENTIFIER, descriptor1);
        
        //centre configuration panel for single centre study
        WizardPanelDescriptor descriptor2 = new SingleCentreDescriptor(wizard.getModel());
        wizard.registerWizardPanel(SingleCentreDescriptor.IDENTIFIER, descriptor2);

        //centre configuration panel for multi centre study
        WizardPanelDescriptor descriptor3 = new MultiCentreDescriptor(wizard.getDialog(), wizard.getModel());
        wizard.registerWizardPanel(MultiCentreDescriptor.IDENTIFIER, descriptor3);

        WizardPanelDescriptor descriptor5 = new DocumentGroupsDescriptor(wizard.getDialog(), wizard.getModel());
        wizard.registerWizardPanel(DocumentGroupsDescriptor.IDENTIFIER, descriptor5);

        WizardPanelDescriptor descriptor6 = new DocumentConfigurationDescriptor(wizard.getDialog(), wizard.getModel());
        wizard.registerWizardPanel(DocumentConfigurationDescriptor.IDENTIFIER, descriptor6);

        WizardPanelDescriptor descriptor7 = new ScheduleDescriptor(wizard.getModel());
        wizard.registerWizardPanel(ScheduleDescriptor.IDENTIFIER, descriptor7);
        
        wizard.setCurrentPanel(BasicPropertiesDescriptor.IDENTIFIER);

        wizard.getDialog().pack();
        wizard.getDialog().setLocationRelativeTo(null);
        
        
		if (DatasetController.getInstance().getActiveDs() != null) {
			//only prompt if it's changed
			if (DatasetController.getInstance().getActiveDs().isDirty()) {
				int returnVal = WrappedJOptionPane.showConfirmDialog(mainFrame, 
						"You have unsaved changes to the open study.  Do you want to save the changes? ", "Unsaved changes", JOptionPane.YES_NO_CANCEL_OPTION);
				if (returnVal == JOptionPane.CANCEL_OPTION) {
					//do nothing
				} else if (returnVal == JOptionPane.YES_OPTION) {
					String lastStoredLocation = DatasetController.getInstance().getActiveDs().getLastStoredLocation(); 
					//if last stored location set, save dataset and then close it
					if (lastStoredLocation != null) {
						DatasetUtility.saveDataset(mainFrame);
						DatasetUtility.closeDataset(mainFrame);
						wizard.showModalDialog();
					//if no last stored location set, then save as... dataset and close it
					} else {
						if (DatasetUtility.saveAsDataset(mainFrame) == JFileChooser.APPROVE_OPTION) {
							DatasetUtility.closeDataset(mainFrame);
							TempFileController.getInstance().deleteTempFile();
							wizard.showModalDialog();
						}
					}
				} else if (returnVal == JOptionPane.NO_OPTION){
					wizard.showModalDialog();
				}
			//if it's not dirty, show the wizard directly
			} else {
				wizard.showModalDialog();
			}
		//if none open, just show the wizard
		} else {
			wizard.showModalDialog();
		}
	}
}

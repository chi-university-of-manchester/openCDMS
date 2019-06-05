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
import javax.swing.JOptionPane;
import javax.swing.JFrame;

import org.psygrid.datasetdesigner.model.*;

import org.psygrid.datasetdesigner.model.StudyDataSet; 
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

/**
 * Action that deletes a group from the project for the specified user.
 * @author pwhelan
 */
public class DeleteDatasetLocallyAction extends AbstractAction {

	private StudyDataSet dataset;
	private JFrame frame;
	
	public DeleteDatasetLocallyAction(JFrame frame, StudyDataSet dataset) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.deletedatasetlocally"));
		this.dataset = dataset;
		this.frame = frame;
	}
	
	public void actionPerformed(ActionEvent aet) {
		int n = JOptionPane.showConfirmDialog(
                frame, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.confirmlocaldatasetdelete"),
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
        if (n == JOptionPane.YES_OPTION) {
    		DocTreeModel.getInstance().removeDataset(dataset.getDs());
			JOptionPane.showMessageDialog(frame, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.datasetdeleted"));
        } else if (n == JOptionPane.NO_OPTION) {
          //nothing then  
        } 
	}
	
	
}

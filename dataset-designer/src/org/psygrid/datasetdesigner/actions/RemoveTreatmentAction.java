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

import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.DefaultListModel;

import org.psygrid.datasetdesigner.model.RandomisationHolderModel;
import org.psygrid.datasetdesigner.model.TreatmentHolderModel;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;


public class RemoveTreatmentAction extends AbstractAction {
	
	private JComboBox datasetBox;
	private JList treatmentList;
	private HashMap<String, RandomisationHolderModel> randomisationMap;
	
	public RemoveTreatmentAction(JComboBox datasetBox, 
							JList treatmentList, 
							HashMap<String, RandomisationHolderModel> randomisationMap) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.remove"));
		this.datasetBox = datasetBox;
		this.treatmentList = treatmentList;
		this.randomisationMap = randomisationMap;
	}
	
	public void actionPerformed(ActionEvent aet) {
		if (treatmentList.getSelectedValue() != null) {
			TreatmentHolderModel treatment = (TreatmentHolderModel)treatmentList.getSelectedValue();
			((DefaultListModel)treatmentList.getModel()).removeElement(treatmentList.getSelectedValue());
			RandomisationHolderModel randomModel = randomisationMap.get(datasetBox.getSelectedItem());
			randomModel.getRandomisationTreatments().remove(treatment);
		}
	}
	
}

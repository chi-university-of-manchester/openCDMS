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

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import java.awt.event.ActionEvent;

import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.DefaultListModel;

import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.SpringUtilities;

import org.psygrid.datasetdesigner.ui.configurationdialogs.AddTreatmentDialog;

import org.psygrid.randomization.model.hibernate.Stratum;

import org.psygrid.datasetdesigner.model.TreatmentHolderModel;

public class AddTreatmentAction extends AbstractAction {
	
	private JList treatmentList;
	
	private boolean edit = false;
	
	private JDialog parentDialog;
	
	public AddTreatmentAction(JDialog parentDialog, 
							JList treatmentList) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.add"));
		this.treatmentList = treatmentList;
		this.parentDialog = parentDialog;
	}
	
	public AddTreatmentAction(JDialog parentDialog,
							  JList treatmentList, 
							  boolean edit) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.edit"));
		this.parentDialog = parentDialog;
		this.treatmentList = treatmentList;
		this.edit = edit;
	}
	
	public void actionPerformed(ActionEvent aet) {
		if (!edit) {
			new AddTreatmentDialog(parentDialog, treatmentList);
		} else {
			new AddTreatmentDialog(parentDialog, treatmentList, (TreatmentHolderModel)treatmentList.getSelectedValue());
		}
	}
	
}

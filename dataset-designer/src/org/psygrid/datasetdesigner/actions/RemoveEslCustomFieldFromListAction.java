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

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.JList;
import javax.swing.JOptionPane;

import org.psygrid.data.model.hibernate.EslCustomField;
import org.psygrid.datasetdesigner.model.RandomisationHolderModel;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.randomization.model.hibernate.Stratum;

/**
 * @author Rob Harper
 *
 */
public class RemoveEslCustomFieldFromListAction extends RemoveFromListAction {

	private RandomisationHolderModel rhm;
	private Component parent;
	
	public RemoveEslCustomFieldFromListAction(Component parent, RandomisationHolderModel rhm, JList list) {
		super(list);
		this.parent = parent;
		this.rhm = rhm;
	}

	@Override
	public void actionPerformed(ActionEvent aet) {
		//check that the field being removed has not been selected
		//to be used for stratification
		EslCustomField cf = (EslCustomField)list.getSelectedValue();
		if ( null != cf ){
			if (null != rhm && null != rhm.getRandomisationStrata()) {
				for ( Stratum s: rhm.getRandomisationStrata() ){
					if ( cf.getName().equals(s.getName()) ){
			            JOptionPane.showMessageDialog(parent,
			                    PropertiesHelper.getStringFor(
			                        "org.psygrid.datasetdesigner.ui.cantremoveeslcustomfield"));
						return;
					}
				}
			}
		}
		super.actionPerformed(aet);
	}

}

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

import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;

import org.psygrid.common.ui.WrappedJOptionPane;

import org.psygrid.data.model.hibernate.AssociatedConsentForm;
import org.psygrid.data.model.hibernate.ConsentForm;
import org.psygrid.data.model.hibernate.PrimaryConsentForm;

import org.psygrid.datasetdesigner.utils.PropertiesHelper;

public class RemoveConsentAction extends AbstractAction {
	
	private JList consentList;
	
	private JDialog parentDialog;
	
	
	public RemoveConsentAction(JDialog parentDialog, JList consentList) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.remove"));
		this.consentList = consentList;
		this.parentDialog = parentDialog;
	}

	public void remove() {
		ConsentForm icf = (ConsentForm)consentList.getSelectedValue();

		//if it's a primary form, check for associated forms and remove them as well
		if (icf instanceof PrimaryConsentForm) {
			List<AssociatedConsentForm> listForms = ((PrimaryConsentForm)icf).getAssociatedConsentForms();
			for (AssociatedConsentForm acf: listForms) {
				((DefaultListModel)consentList.getModel()).removeElement(acf);
			}
		//if it's an associated consent form, remove references to it in primary consent forms
		} else if (icf instanceof AssociatedConsentForm) {
			for (Object curForm: ((DefaultListModel)consentList.getModel()).toArray()) {
				if (curForm instanceof PrimaryConsentForm) {
					List<AssociatedConsentForm> listForms = ((PrimaryConsentForm)curForm).getAssociatedConsentForms();
					for (int i=listForms.size()-1; i>=0; i--) {
						AssociatedConsentForm assocForm = listForms.get(i);
						if (assocForm.equals(icf)) {
							((PrimaryConsentForm)curForm).removeAssociatedConsentForm(i);
						}
					}		
				}
			}
		}

		//remove the element itself from the list
		((DefaultListModel)consentList.getModel()).removeElement(icf);
		
	}
	
	public void actionPerformed(ActionEvent aet) {
		ConsentForm icf = (ConsentForm)consentList.getSelectedValue();
		
		if (icf instanceof PrimaryConsentForm) {
			if (((PrimaryConsentForm)icf).getAssociatedConsentForms().size() > 0 ) {
				int returnValue = WrappedJOptionPane.showConfirmDialog(parentDialog, "This consent form is associated with other consent forms.  These will also be removed.  Proceed?");
				if (returnValue == JOptionPane.YES_OPTION) {
					remove();
				}
			} else {
				remove();
			}
		} else {
			remove();
		}
		
	}
	
}
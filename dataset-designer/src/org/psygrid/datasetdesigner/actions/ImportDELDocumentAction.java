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

import org.psygrid.common.ui.WrappedJOptionPane;
import org.psygrid.data.model.hibernate.BasicEntry;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.data.model.hibernate.ValidationRule;
import org.psygrid.datasetdesigner.model.DELStudySet;
import org.psygrid.datasetdesigner.model.StudyDataSet;
import org.psygrid.datasetdesigner.ui.MainTabbedPane;
import org.psygrid.datasetdesigner.ui.dataelementfacilities.DataElementSubmitDocumentDialog;

public class ImportDELDocumentAction extends AbstractAction {

	private static final long serialVersionUID = 1515221139086865953L;

	private Document document;
	private StudyDataSet dataset;
	protected MainTabbedPane docPane;

	public ImportDELDocumentAction(MainTabbedPane docPane, Document document, StudyDataSet dataset) {
		super("Save to Library");
		this.document = document;
		this.dataset = dataset;
		this.docPane = docPane;
	}

	public void actionPerformed(ActionEvent e) {
		//Save document to the DEL, ensuring that the document is sanity checked and cleaned first..
		if (dataset instanceof DELStudySet) {
		}
		else {
			//Check the entries and validation rules for existence within the library and any local edits..
			for (Entry entry: document.getEntries()) {
				if (entry.getLSID() != null && entry.getIsRevisionCandidate()) {
					//Entry is from the DEL and has been edited locally
					WrappedJOptionPane.showMessageDialog(docPane, "This document contains an entry from the library that has been edited locally, so cannot be saved back to the library from this study.");
					return;
				}
				if (entry instanceof BasicEntry) {
					BasicEntry be = (BasicEntry)entry;
					if (be.getValidationRules() != null) {
						for (ValidationRule rule: be.getValidationRules()) {
							if (rule.getLSID() != null && rule.getIsRevisionCandidate()) {
								//Rule is from the DEL and has been edited locally
								WrappedJOptionPane.showMessageDialog(docPane, "This document contains a validation rule from the library that has been edited locally, so cannot be saved back to the library from this study.");
								return;
							}
						}
					}
				}
			}
		}
		
		new DataElementSubmitDocumentDialog(docPane, this.document);
	}
}


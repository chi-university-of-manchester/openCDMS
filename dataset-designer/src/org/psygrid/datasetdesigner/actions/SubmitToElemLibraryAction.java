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

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.datasetdesigner.model.StudyDataSet;
import org.psygrid.datasetdesigner.model.DocTreeModel;
import org.psygrid.datasetdesigner.ui.MainTabbedPane;
import org.psygrid.datasetdesigner.ui.dataelementfacilities.DataElementSubmitPendingDialog;
import org.psygrid.datasetdesigner.ui.dataelementfacilities.AbstractSubmitDialog.SubmissionLevel;
import org.psygrid.datasetdesigner.utils.Utils;


public class SubmitToElemLibraryAction extends AbstractAction {

	private static final long serialVersionUID = -8427901087294423932L;

	public enum SubmissionType{
		Entries,
		Documents,
		ValidationRules,	
	}

	private DataSet ds;
	private Document document;
	private MainTabbedPane docPane;
	private SubmissionType type;

	public SubmitToElemLibraryAction(MainTabbedPane docPane, Document document) {
		super("Save Elements to Library");
		this.document = document;
		this.docPane = docPane;
		type = SubmissionType.Entries;
	}

	public SubmitToElemLibraryAction(MainTabbedPane docPane, DataSet ds){
		super("Save Documents to Library");
		this.ds = ds;
		this.docPane = docPane;
		type = SubmissionType.Documents;
	}

	public SubmitToElemLibraryAction(MainTabbedPane docPane, DataSet ds, SubmissionType submissionType){
		super("Save Validation Rules to Library");
		this.ds = ds;
		this.docPane = docPane;
		type = submissionType;
	}

	public SubmitToElemLibraryAction(MainTabbedPane docPane, SubmissionType submissionType){
		super("Save "+submissionType.toString()+" to Library");
		StudyDataSet dsDS = DocTreeModel.getInstance().getDELDataset();
		if (dsDS != null) {
			this.ds = dsDS.getDs();
		}
		this.docPane = docPane;
		this.type = submissionType;

		switch(type) {
		case Entries:
			if (ds != null) {
				for (Document doc: ((DataSet)ds).getDocuments()) {
					if (Utils.docHasSubmittableEntries(doc)) {
						this.document = doc;
					}
				}
			}
		}
	}

	public void actionPerformed(ActionEvent e) {		
		switch(type) {
		case Entries:
			new DataElementSubmitPendingDialog(docPane, this.document);
			break;
		case Documents:
			new DataElementSubmitPendingDialog(docPane, this.ds);
			break;
		case ValidationRules:
			new DataElementSubmitPendingDialog(docPane, this.ds, SubmissionLevel.ValidationRule);
			break;
		}

	}
}

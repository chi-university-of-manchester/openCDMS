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
import javax.swing.JDialog;

import org.psygrid.data.model.hibernate.ValidationRule;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.datasetdesigner.model.StudyDataSet;
import org.psygrid.datasetdesigner.model.DocTreeModel;
import org.psygrid.datasetdesigner.ui.MainTabbedPane;
import org.psygrid.datasetdesigner.ui.dataelementfacilities.ApproveElementsDialog;
import org.psygrid.datasetdesigner.ui.dataelementfacilities.AbstractSubmitDialog.SubmissionLevel;
import org.psygrid.datasetdesigner.utils.Utils;


public class ApproveElementAction extends AbstractAction {

	private static final long serialVersionUID = -8427901087294423932L;

	public enum SubmissionType{
		Entries,
		Documents,
		ValidationRules,	
	}

	private DataSet ds;
	private Document document;
	private ValidationRule rule;
	private MainTabbedPane docPane;
	private JDialog parentDialog;
	private SubmissionType type;

	public ApproveElementAction(MainTabbedPane docPane, Document document) {
		super("Approve Document");
		this.document = document;
		this.docPane = docPane;
		type = SubmissionType.Entries;
	}

	public ApproveElementAction(MainTabbedPane docPane, DataSet ds){
		super("Approve "+SubmissionType.Documents);
		this.ds = ds;
		this.docPane = docPane;
		type = SubmissionType.Documents;
	}

	public ApproveElementAction(MainTabbedPane docPane, DataSet ds, boolean isValidationRule){
		super("Approve Validation Rules");
		this.docPane = docPane;
		this.ds = ds;
		type = SubmissionType.ValidationRules;
	}
	
	public ApproveElementAction(JDialog parentDialog, MainTabbedPane docPane, ValidationRule rule){
		super("Approve Validation Rule");
		this.docPane = docPane;
		this.rule = rule;
		this.parentDialog = parentDialog;
		type = SubmissionType.ValidationRules;
	}
	
	public ApproveElementAction(MainTabbedPane docPane){
		super("Approve");
		StudyDataSet dsDS = DocTreeModel.getInstance().getDELDataset();
		if (dsDS != null) {
			this.ds = dsDS.getDs();
		}
		this.docPane = docPane;
		
		if (Utils.dataSetHasSubmittableDocuments((DataSet)ds)) {
			type = SubmissionType.Documents;		
		}
		else {
			for (Document doc: ((DataSet)ds).getDocuments()) {
				if (Utils.docHasSubmittableEntries(doc)) {
					type = SubmissionType.Entries;
					this.document = doc;
				}
			}
		}
	}

	public void actionPerformed(ActionEvent e) {		
		switch(type) {
		case Entries:
			new ApproveElementsDialog(docPane, this.document);
			break;
		case Documents:
			new ApproveElementsDialog(docPane, this.ds);
			break;
		case ValidationRules:
			if (rule != null) {
				new ApproveElementsDialog(parentDialog, docPane, this.rule);
			}
			else {
				new ApproveElementsDialog(docPane, this.ds, SubmissionLevel.ValidationRule);
			}
			break;
		}

	}
}

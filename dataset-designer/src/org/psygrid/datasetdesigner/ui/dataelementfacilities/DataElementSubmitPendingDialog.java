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
package org.psygrid.datasetdesigner.ui.dataelementfacilities;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.common.ui.WrappedJOptionPane;
import org.psygrid.data.model.hibernate.DataElementContainer;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.datasetdesigner.ui.MainTabbedPane;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

public class DataElementSubmitPendingDialog extends AbstractSubmitDialog {

	private static final Log LOG = LogFactory.getLog(DataElementSubmitPendingDialog.class);

	static public boolean delConnectionIsInitialised = false;

	public DataElementSubmitPendingDialog(MainTabbedPane docPane, Document document){
		super(docPane, document, "Submit New/Revised Element(s) to Library");
		submissionLevel = SubmissionLevel.Entry;
		setSubmissionQuestionText(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.entercomments"));
		init(docPane, true);

		populateCandidateTable();
	}

	public DataElementSubmitPendingDialog(MainTabbedPane docPane, DataSet ds){
		super(docPane, ds, "Submit New/Revised Element(s) to Library");
		submissionLevel = SubmissionLevel.Document;
		setSubmissionQuestionText(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.entercomments"));
		init(docPane, true);

		populateCandidateTable();
	}

	public DataElementSubmitPendingDialog(MainTabbedPane docPane, DataSet ds, SubmissionLevel submissionLevel){
		super(docPane, ds, submissionLevel, "Submit New/Revised Element(s) to Library");
		setSubmissionQuestionText(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.entercomments"));
		init(docPane, true);

		populateCandidateTable();
	}

	public DataElementSubmitPendingDialog(JDialog parentDialog, MainTabbedPane docPane, DataSet ds, SubmissionLevel submissionLevel){
		super(parentDialog, ds, submissionLevel, "Submit New/Revised Elemnt(s) to Library");
		setSubmissionQuestionText(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.entercomments"));
		init(docPane, true);

		populateCandidateTable();
	}
	protected void populateValidationRulesCandidateTable() {
		super.populateValidationRulesCandidateTable();
		
		//Warn user before saving
		WrappedJOptionPane.showWrappedMessageDialog(this, "Please note that after submitting these rules you will no longer be able to add them to entries until the rules have been approved.", "", WrappedJOptionPane.INFORMATION_MESSAGE);
		
	}
	
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		CandidateTableModel candidateModel = 	(CandidateTableModel)this.candidateEntriesTable.getModel();
		if(obj == selectButton){
			candidateModel.selectAll();
			submitButton.setEnabled(true);
		}else if (obj == deselectButton){
			candidateModel.deselectAll();
			submitButton.setEnabled(false);
		}else if (obj == submitButton) {

			//Check to make sure they have filled in submit text, and selected an authority.
			String submitText = this.submissionText.getText();
			String authority = (String)this.authorityList.getSelectedItem();
			if(submitText.length() == 0 || authority.length() == 0){
				WrappedJOptionPane.showMessageDialog(this, "You must create submit text and select an authority");
				return;
			}


			int candidateCount = candidateModel.getRowCount();
			List<DataElementContainer> selectedElements = new ArrayList<DataElementContainer>();
			for(int i = 0; i < candidateCount; i++) {
				if(candidateModel.getRowIsSelected(i)){
					selectedElements.add(candidateModel.getElement(i));
				}
			}
			doSubmit(selectedElements);

		}else if (obj == cancelButton){
			this.dispose();
		}
	}

}

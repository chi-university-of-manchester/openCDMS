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
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.common.ui.WrappedJOptionPane;
import org.psygrid.data.model.hibernate.DataElementContainer;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.datasetdesigner.ui.MainTabbedPane;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

/**
 * Dialog to allow the submission of one whole document
 * 
 * @author Lucy Bridges
 *
 */
public class DataElementSubmitDocumentDialog extends AbstractSubmitDialog {

	private static final long serialVersionUID = 2332054752623563930L;

	private static final Log LOG = LogFactory.getLog(DataElementSubmitDocumentDialog.class);

	public DataElementSubmitDocumentDialog(MainTabbedPane docPane, Document document){
		super(docPane, document, "Submit New Document to Library");
		submissionLevel = SubmissionLevel.Document;
		setSubmissionQuestionText(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.entercomments"));
		init(docPane, true);

		populateCandidateTable();

		//There should only be one row (one document), so make sure it's selected by default.
		CandidateTableModel candidateModel = (CandidateTableModel)this.candidateEntriesTable.getModel();
		candidateModel.selectRow(true, 0);
		selectButton.setEnabled(false);
		deselectButton.setEnabled(false);
		submitButton.setEnabled(true);
	}

	protected void populateDocumentCandidateTable() {
		DataElementContainer container = new DataElementContainer(doc);
		List<DataElementContainer> candidateDocuments = new ArrayList<DataElementContainer>();
		candidateDocuments.add(container);
		Vector vWrapper = new Vector();
		vWrapper.add(container);
		((CandidateTableModel)candidateEntriesTable.getModel()).addRow(vWrapper);
	}

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		CandidateTableModel candidateModel = (CandidateTableModel)this.candidateEntriesTable.getModel();
		if (obj == submitButton) {

			//Check to make sure they have filled in submit text, and selected an authority.
			String submitText = this.submissionText.getText();
			String authority = (String)this.authorityList.getSelectedItem();
			if(submitText.length() == 0){
				WrappedJOptionPane.showMessageDialog(this, "You must enter some comment text.");
				return;
			}
			if(authority.length() == 0){
				WrappedJOptionPane.showMessageDialog(this, "You must select an authority.");
				return;
			}

			//There should only be only selected element in this case
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

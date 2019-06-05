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

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.common.ui.WrappedJOptionPane;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.datasetdesigner.model.DocTreeModel;
import org.psygrid.datasetdesigner.model.StudyDataSet;
import org.psygrid.datasetdesigner.ui.MainTabbedPane;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.Utils;

public class ApproveElementsDialog extends AbstractSubmitDialog {

	private static final long serialVersionUID = -1300410084936277886L;

	private static final Log LOG = LogFactory.getLog(ApproveElementsDialog.class);

	static public boolean delConnectionIsInitialised = false;

	private Document document;

	public ApproveElementsDialog(MainTabbedPane docPane, Document document){
		super(docPane, document, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.approvedocument"));
		submissionLevel = SubmissionLevel.Document;
		this.document = document;
		init(docPane, false);	//Elements for approval already have the authority set
		populateCandidateTable();
	}

	public ApproveElementsDialog(MainTabbedPane docPane, DataSet ds){
		super(docPane, ds, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.approvedocuments"));
		submissionLevel = SubmissionLevel.Document;
		init(docPane, false);
		populateCandidateTable();
	}

	public ApproveElementsDialog(JDialog parentDialog, MainTabbedPane docPane, ValidationRule rule){
		super(parentDialog, rule, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.approvevalidationrule"));
		submissionLevel = SubmissionLevel.ValidationRule;
		init(docPane, false);
		populateCandidateTable();
	}

	public ApproveElementsDialog(MainTabbedPane docPane, DataSet ds, SubmissionLevel submissionLevel){
		super(docPane, ds, submissionLevel, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.approveelements"));
		init(docPane, false);
		populateCandidateTable();
	}

	protected void populateDocumentCandidateTable() {
		List<DataElementContainer> candidateDocuments = new ArrayList<DataElementContainer>();
		if (document != null) {
			candidateDocuments.add(new DataElementContainer((Document)document));
		}
		else {
			for (Document e: ds.getDocuments()){
				if (DataElementStatus.PENDING.equals(e.getStatus()) && !e.getIsRevisionCandidate()){
					StudyDataSet ds = DocTreeModel.getInstance().getDSDataset(e.getMyDataSet().getName());
					if (!ds.isDelRestricted(e.getName())){
						candidateDocuments.add(new DataElementContainer(e));
					}
				}	
			}
		}

		for(DataElementContainer e: candidateDocuments){
			Vector vWrapper = new Vector();
			vWrapper.add(e);
			((CandidateTableModel)candidateEntriesTable.getModel()).addRow(vWrapper);
		}
	}

	protected void populateElementCandidateTable() {
		List<DataElementContainer> candidateEntries = new ArrayList<DataElementContainer>();

		for (Entry e: doc.getEntries()){
			if (e instanceof CompositeEntry){
				for (Entry entry: ((CompositeEntry)e).getEntries()){
					if (DataElementStatus.PENDING.equals(e.getStatus()) && !e.getIsRevisionCandidate()) {
						candidateEntries.add(new DataElementContainer(entry));
					}
				}
			}

			if (DataElementStatus.PENDING.equals(e.getStatus()) && !e.getIsRevisionCandidate()) {
				candidateEntries.add(new DataElementContainer(e));
			}
		}

		for (DataElementContainer e: candidateEntries){
			Vector vWrapper = new Vector();
			vWrapper.add(e);
			((CandidateTableModel)candidateEntriesTable.getModel()).addRow(vWrapper);
		}		
	}

	protected void populateValidationRulesCandidateTable() {
		List<DataElementContainer> candidateRules = new ArrayList<DataElementContainer>();
		if (rule != null) {
			candidateRules.add(new DataElementContainer(rule));
		}
		else {
			for (ValidationRule rule: ds.getValidationRules()){
				if (DataElementStatus.PENDING.equals(rule.getStatus())
						&& !rule.getIsRevisionCandidate()){
					candidateRules.add(new DataElementContainer(rule));
				}	
			}
		}

		for (DataElementContainer rule: candidateRules){
			Vector vWrapper = new Vector();
			vWrapper.add(rule);
			((CandidateTableModel)candidateEntriesTable.getModel()).addRow(vWrapper);
		}
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
			int candidateCount = candidateModel.getRowCount();
			List<DataElementContainer> selectedElements = new ArrayList<DataElementContainer>();
			for(int i = 0; i < candidateCount; i++) {
				if(candidateModel.getRowIsSelected(i)){
					selectedElements.add((DataElementContainer)candidateModel.getElement(i));
				}
			}
			approve(selectedElements);
		}else if (obj == cancelButton){
			this.dispose();
		}
	}

	private void approve(List<DataElementContainer> selectedElements) {
		CandidateTableModel candidateModel = (CandidateTableModel)this.candidateEntriesTable.getModel();
		int candidateCount = candidateModel.getRowCount();

		//Now it's entirely possible that some of the 'selected' candidates are going to be entered anyway, because
		//they are subordinates of the ones already submitted.
		//Therefore, we need to weed out any of these cases.
		//The logic is similar to the uncheck-override logic.
		List<DataElementContainer> elementsToRemove = new ArrayList<DataElementContainer>();
		List<DataElementContainer> allSubElements = new ArrayList<DataElementContainer>();

		for(int i = 0; i < selectedElements.size(); i++){
			DataElementContainer elem = selectedElements.get(i);
			List<DataElementContainer> subElems = Utils.getSubordinateElements(elem);
			allSubElements.addAll(subElems);
		}

		for(int i = 0; i < selectedElements.size(); i++){
			DataElementContainer elem = selectedElements.get(i);
			for(DataElementContainer possibleMatch: allSubElements){
				if(possibleMatch == elem){
					elementsToRemove.add(elem);
					break;
				}
			}
		}

		selectedElements.removeAll(elementsToRemove);

		//Approve the elements now...
		
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		for(DataElementContainer e1: selectedElements) {
			String operationType = null;
			try {
				String samlAssertion = mainPane.getDelInitializer().getSaml();
				if(e1.getElementLSID() == null){
					//This should not happen
					WrappedJOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.unabletoapprove"));
					LOG.error("Attempted to approve a new element");
					return;
				}else if(e1.getElementLSID() != null && e1.getIsRevisionCandidate()){
					//This should not happen
					WrappedJOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.unabletoapproveedited"));
					LOG.error("Attempted to approve an element with local changes");
					return;

				}else{
					//If this the element was a subordinate, we cannot approve it (the parent element must be approved instead)
					if(submissionLevel == SubmissionLevel.Entry){
						if(Utils.elementIsASubordinate(e1, doc)){
							//Throw error - subordinate elements cannot be approved alone
							WrappedJOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.unabletoapprovesubordinate"));
							LOG.error("Attempted to approve a sobordinate element");
							return;
						}
					}

					operationType = PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.approve");
					mainPane.getDelInitializer().getClient().modifyElementStatus(e1, DataElementAction.APPROVE.toString(), "", samlAssertion);
				}
			} catch (Exception ex){
				LOG.error(operationType + " failed for element with name " + e1.getElementDescription()+ ". The reason is - " + ex.getMessage());
				candidateModel.selectItem(false, e1); //De-select the item. Afterwards, only the selected items will be removed from the list.
				WrappedJOptionPane.showMessageDialog(this, operationType + " failed. Reason - " + ex.getMessage() + ".", operationType + " Failure", JOptionPane.ERROR_MESSAGE);
				continue;
			}

			DataElementContainer fromDEL;
			try {
				fromDEL = (DataElementContainer)Utils.getCompleteBrowseElement(mainPane, e1.getElementLSID().toString());
			} catch (Exception e3) {
				String itemLSID = e1.getElementLSID().toString();
				LOG.error("Could not retrieve LSID " + itemLSID + " following successful " + operationType + ". Local" +
				"entry could not be successfully updated.");
				WrappedJOptionPane.showMessageDialog(this, operationType + " succeded for " + e1.getElementDescription() + " but subsequent client update failed. It may therefore not be possible" +
						"to edit the element further." , "Client Update Failure", JOptionPane.ERROR_MESSAGE);
				continue;
			}
			
			String name = e1.getElementName();
			if (name == null||name.equals("")) {
				name = e1.getElementDescription();
			}
			WrappedJOptionPane.showMessageDialog(this, operationType + " was successful for " + name + ".");

			fromDEL.setIsEditable(e1.getIsEditable());			
			/*
			 * Replace the local element with the newly approved one from the DEL
			 */
			updateElement(e1, fromDEL);

		}


		//Remove the items that were just saved
		List<Integer> itemsToRemove = new ArrayList<Integer>();

		for(int i = candidateCount-1; i >= 0; i--) {
			if(candidateModel.getRowIsSelected(i))
				itemsToRemove.add(i); //End-of-list items added first
		}

		//Do removal.
		for(int i = 0; i < itemsToRemove.size(); i++){
			candidateModel.removeRow(itemsToRemove.get(i));
		}

		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

		saveLocally();  //Update local settings (otherwise the new status of elements displayed locally can be forgotten)
	}

}

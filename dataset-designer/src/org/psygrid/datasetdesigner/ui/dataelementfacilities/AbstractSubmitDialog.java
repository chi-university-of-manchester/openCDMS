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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.VerticalLayout;
import org.psygrid.common.ui.WrappedJOptionPane;
import org.psygrid.data.DELFailedTestException;
import org.psygrid.data.model.IAdminInfo;
import org.psygrid.data.model.hibernate.DataElementAction;
import org.psygrid.data.model.hibernate.ValidationRule;
import org.psygrid.data.model.hibernate.BasicEntry;
import org.psygrid.data.model.hibernate.CompositeEntry;
import org.psygrid.data.model.hibernate.DataElementContainer;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.DerivedEntry;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.data.model.hibernate.ExternalDerivedEntry;
import org.psygrid.data.model.hibernate.HibernateDataElementFactory;
import org.psygrid.data.model.hibernate.LSID;
import org.psygrid.datasetdesigner.controllers.DatasetController;
import org.psygrid.datasetdesigner.model.DocTreeModel;
import org.psygrid.datasetdesigner.model.DummyDocument;
import org.psygrid.datasetdesigner.model.StudyDataSet;
import org.psygrid.datasetdesigner.ui.MainFrame;
import org.psygrid.datasetdesigner.ui.MainTabbedPane;
import org.psygrid.datasetdesigner.utils.LocalFileUtility;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.Utils;

public abstract class AbstractSubmitDialog extends JDialog implements ActionListener, ListSelectionListener{

	private static final Log LOG = LogFactory.getLog(AbstractSubmitDialog.class);

	public enum SubmissionLevel{
		Entry,
		Document,
		ValidationRule
	}

	protected MainTabbedPane mainPane;
	protected Document doc = null;
	protected DataSet ds = null;
	protected ValidationRule rule = null;
	protected JTable candidateEntriesTable;
	protected CandidateTableModel candidateTableModel = new CandidateTableModel();
	protected JButton selectButton;
	protected JButton deselectButton;
	protected JButton submitButton;
	protected JButton cancelButton;
	protected JTextField submissionText;
	protected JComboBox authorityList;
	protected SubmissionLevel submissionLevel; 

	protected String submissionQuestionText = null;

	static public boolean delConnectionIsInitialised = false;

	public AbstractSubmitDialog(MainTabbedPane docPane, Document document, String title){
		super(docPane.getFrame(), title);
		submissionLevel = SubmissionLevel.Entry;
		this.doc = (Document)document;
	}

	public AbstractSubmitDialog(MainTabbedPane docPane, DataSet ds, String title){
		super(docPane.getFrame(), title);
		submissionLevel = SubmissionLevel.Document;
		this.ds = (DataSet) ds;
		if (ds == null
				&& DocTreeModel.getInstance().getDELDataset() != null) {
			ds = DocTreeModel.getInstance().getDELDataset().getDs();
		}
	}

	public AbstractSubmitDialog(JDialog parentDialog, ValidationRule rule, String title){
		super(parentDialog, title);
		submissionLevel = SubmissionLevel.ValidationRule;
		this.rule = (ValidationRule)rule;
	}

	public AbstractSubmitDialog(MainTabbedPane docPane, DataSet ds, SubmissionLevel submissionLevel, String title){
		super(docPane.getFrame(), title);
		this.submissionLevel = submissionLevel;
		this.ds = (DataSet)ds;
		if (ds == null
				&& DocTreeModel.getInstance().getDELDataset() != null) {
			ds = DocTreeModel.getInstance().getDELDataset().getDs();
		}
	}

	public AbstractSubmitDialog(JDialog parentDialog, DataSet ds, SubmissionLevel submissionLevel, String title){
		super(parentDialog, title);
		this.submissionLevel = submissionLevel;
		this.ds = (DataSet)ds;
		if (ds == null
				&& DocTreeModel.getInstance().getDELDataset() != null) {
			ds = DocTreeModel.getInstance().getDELDataset().getDs();
		}
	}

	protected void init(MainTabbedPane docPane, boolean showAuthority) {
		mainPane = docPane;
		getContentPane().setLayout(new BorderLayout());
		if (submissionQuestionText != null) {
			getContentPane().add(buildSubmissionCommentsPanel(),  BorderLayout.NORTH);
		}
		if (showAuthority) {
			getContentPane().add(buildAuthorityEntryPanel(), BorderLayout.CENTER);
		}
		getContentPane().add(buildElementSelectionPanel(), BorderLayout.SOUTH);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	protected void populateCandidateTable() {
		switch (submissionLevel) {
		case Entry:
			populateElementCandidateTable();
			break;
		case Document:
			populateDocumentCandidateTable();
			break;
		case ValidationRule:
			populateValidationRulesCandidateTable();
			break;
		}
	}

	protected void populateDocumentCandidateTable() {
		List<DataElementContainer> candidateDocuments = new ArrayList<DataElementContainer>();

		for (Document e: ds.getDocuments()){
			if (e instanceof DummyDocument) {
				for (Entry entry: e.getEntries()){
					if(entry.getLSID() == null || (entry.getLSID() != null && entry.getIsRevisionCandidate())) {
						candidateDocuments.add(new DataElementContainer(entry));
					}
				}
			}
			else if(e.getLSID() == null || e.getIsRevisionCandidate()){
				StudyDataSet ds = DocTreeModel.getInstance().getDSDataset(e.getMyDataSet().getName());
				if(!ds.isDelRestricted(e.getName())){
					candidateDocuments.add(new DataElementContainer(e));
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
			if(e instanceof CompositeEntry){
				for(Entry entry: ((CompositeEntry)e).getEntries()){
					if(entry.getLSID() == null || (entry.getLSID() != null && entry.getIsRevisionCandidate()))
						candidateEntries.add(new DataElementContainer(entry));
				}
			}

			if(e.getLSID() == null || (e.getLSID() != null && e.getIsRevisionCandidate())) {
				candidateEntries.add(new DataElementContainer(e));
			}
		}

		for(DataElementContainer e: candidateEntries){
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
				if(rule.getLSID() == null || rule.getIsRevisionCandidate()) {
					candidateRules.add(new DataElementContainer(rule));
				}	
			}
		}

		for(DataElementContainer rule: candidateRules){
			Vector vWrapper = new Vector();
			vWrapper.add(rule);
			((CandidateTableModel)candidateEntriesTable.getModel()).addRow(vWrapper);
		}
	}

	private Component buildElementSelectionPanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		JPanel searchCriteriaPanel = new JPanel();
		searchCriteriaPanel.setLayout(new VerticalLayout());

		candidateEntriesTable = new JTable(candidateTableModel);
		//substance 4.0 defaults table headers to the left
		((DefaultTableCellRenderer)candidateEntriesTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
		candidateEntriesTable.getColumnModel().getColumn(0).setHeaderValue(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.selected"));
		candidateEntriesTable.getColumnModel().getColumn(1).setHeaderValue(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.elementname"));
		candidateEntriesTable.getColumnModel().getColumn(2).setHeaderValue(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.submissiontype"));
		candidateEntriesTable.getSelectionModel().addListSelectionListener(this);

		JScrollPane candidateEntriesScrollPane = new javax.swing.JScrollPane(candidateEntriesTable);
		candidateEntriesScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		candidateEntriesScrollPane.setMaximumSize(new java.awt.Dimension(800, 200));
		candidateEntriesScrollPane.setMinimumSize(new java.awt.Dimension(400, 180));
		candidateEntriesScrollPane.setPreferredSize(new java.awt.Dimension(450,200));

		JPanel candidateEntriesLeftAlignmentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		candidateEntriesLeftAlignmentPanel.add(candidateEntriesScrollPane);

		searchCriteriaPanel.add(candidateEntriesLeftAlignmentPanel);

		mainPanel.add(searchCriteriaPanel);

		//Add the Select-All & Deselect-All buttons
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		selectButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.selectall"));
		deselectButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.deselectall"));

		selectButton.addActionListener(this);
		deselectButton.addActionListener(this);

		buttonPanel.add(selectButton);
		buttonPanel.add(deselectButton);

		submitButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.submit"));
		submitButton.setEnabled(false);
		cancelButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.close"));

		submitButton.addActionListener(this);
		cancelButton.addActionListener(this);

		buttonPanel.add(submitButton);
		buttonPanel.add(cancelButton);

		mainPanel.add(buttonPanel);

		mainPanel.setBorder(BorderFactory.createTitledBorder(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.candidateentries")));

		return mainPanel;
	}

	private Component buildSubmissionCommentsPanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		JPanel searchCriteriaPanel = new JPanel();
		searchCriteriaPanel.setLayout(new VerticalLayout());

		submissionText = new JTextField(10);

		searchCriteriaPanel.add(submissionText);

		mainPanel.add(searchCriteriaPanel);
		//was "Enter your submission/revision comments:"
		mainPanel.setBorder(BorderFactory.createTitledBorder(getSubmissionQuestionText()));

		return mainPanel;
	}

	private Component buildAuthorityEntryPanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		JPanel authorityPanel = new JPanel();
		authorityPanel.setLayout(new VerticalLayout());

		String[] lsidAuthorities = mainPane.getDelInitializer().getLSIDAuthorities();//TODO:DEL - handle failure
		//Add a blank!
		List<String> lsidAuthoritiesList = java.util.Arrays.asList(lsidAuthorities);
		List<String> cleanAuthoritiesList = new ArrayList<String>(lsidAuthoritiesList);
		cleanAuthoritiesList.add(0, "");
		lsidAuthorities = cleanAuthoritiesList.toArray(lsidAuthorities);

		authorityList = new JComboBox(lsidAuthorities);
		authorityList.setPreferredSize(new Dimension(50, 20));
		authorityList.setMaximumSize(new Dimension(50,20));
		authorityList.setMinimumSize(new Dimension(50, 20));

		if (lsidAuthorities.length == 2) {
			//Set the only authority found as automatically selected and don't
			//display to the user.
			authorityList.setSelectedIndex(1);
		}
		else {
			authorityPanel.add(authorityList);
			authorityList.addActionListener(this);
			mainPanel.add(authorityPanel);
			mainPanel.setBorder(BorderFactory.createTitledBorder(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.selectorganisation")));
		}


		return mainPanel;
	}

	protected class CandidateTableModel extends DefaultTableModel {

		private static final long serialVersionUID = 1L;
		private List<DataElementContainer> elements = new ArrayList<DataElementContainer>(); 
		private List<Boolean> elementsSelected = new ArrayList<Boolean>();

		public Class getColumnClass(int columnIndex)
		{
			if(columnIndex == 0) {
				return Boolean.class;
			} else {
				return super.getColumnClass(columnIndex);
			}
		}


		@Override
		public Object getValueAt(int row, int column) {

			DataElementContainer elem = elements.get(row);
			Object returnObj = null;
			switch(column){
			case 0:
			{
				returnObj = elementsSelected.get(row);
			}
			break;
			case 1:
			{
				returnObj = elem.getElementName();
				if (returnObj == null || ((String)returnObj).equals("")) {
					returnObj = elem.getElementDescription();	
				}
			}
			break;
			case 2:
			{
				String newSubmission = PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.newsubmission");
				String revision      = PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.revision");
				String approve       = PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.approve");

				LSID lsid = elem.getElementLSIDObject();
				if (lsid == null) {
					returnObj = newSubmission;
				}
				else if (elem.getIsRevisionCandidate()){
					returnObj = revision;
				}
				else {
					//Must be an approval if the element isn't a revision candidate.
					returnObj = approve;
				}
			}
			break;
			default:
				returnObj = elem.getElementName();
			}

			return returnObj;
		}

		@Override
		public void addRow(Vector elem) {
			elements.add((DataElementContainer)elem.get(0));
			elementsSelected.add(false);
			fireTableDataChanged();
		}

		public void selectRow(boolean value, int row){
			elementsSelected.set(row, value);
			fireTableDataChanged();
		}

		public void selectItem(boolean value, DataElementContainer element){
			int elementCount = elements.size();
			for(int i = 0; i < elementCount; i++){
				DataElementContainer elemInModel = elements.get(i);
				if(elemInModel == element){
					elementsSelected.set(i, value);
					fireTableDataChanged();
					break;
				}
			}
		}

		public boolean getRowIsSelected(int row){
			return elementsSelected.get(row);
		}

		public boolean areAnyRowsSelected(){

			//Search to see if any of the contents are 'true'.
			boolean result = false;
			for(Boolean b: elementsSelected){
				if(b){
					result = true;
					break;
				}
			}

			return result;
		}

		@Override
		public void removeRow(int row) {
			elements.remove(row);
			fireTableDataChanged();
		}

		public int getRowCount() {
			if (elements != null) {
				return elements.size();
			}
			return 0;
		}

		public DataElementContainer getElement(int row) {
			return elements.get(row);
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			if(column == 0)
				return true;
			else
				return false;
		}

		public int getColumnCount() {
			return 3;
		}

		public void selectAll(){
			int elementsSelectedSize = elementsSelected.size();

			elementsSelected.clear();

			for(int i = 0; i < elementsSelectedSize; i++){
				elementsSelected.add(true);
			}
			fireTableDataChanged();
		}

		public void deselectAll(){
			int elementsSelectedSize = elementsSelected.size();

			elementsSelected.clear();

			for(int i = 0; i < elementsSelectedSize; i++){
				elementsSelected.add(false);
			}
			fireTableDataChanged();
		}


		@Override
		public void setValueAt(Object aValue, int row, int column) {

			if(column == 0){
				boolean allowElementSelectionChange = true;

				//If the user has checked or unchecked a SUBMISSION item (not revision)
				//AND the item is contains subordinates, then check or uncheck all of the
				//element's subordinates as well.

				//Determine if submission item.
				boolean isSubmissionItem = false;
				DataElementContainer elem = elements.get(row);
				LSID lsid = elem.getElementLSIDObject();
				if(lsid == null){
					isSubmissionItem = true;
				}


				if(isSubmissionItem){
					if (!SubmissionLevel.ValidationRule.equals(submissionLevel)) {
						//Get element subordinates, if it has any.
						List<DataElementContainer> subordinateElements = Utils.getSubordinateElements(elem);

						//Now select or unselect the items in the list.
						for(DataElementContainer el: elements){
							for(DataElementContainer subEl: subordinateElements){
								if(subEl == el){
									//Select or de-select.
									int subElIndex = elements.indexOf(subEl);

									if((Boolean)aValue == false){
										//Don't de-select if this is a subordinate of ANOTHER element
										//Retrieve the subordinates of ALL the other items in the list,
										//apart from self and the current element.
										//If subEl matches the subordinates of ANY of the others, then
										//do NOT de-select.

										boolean applyDeSelect = true;
										for(int i = 0; i < elements.size(); i++){
											if(elements.get(i) == subEl || elements.get(i) == elements.get(row)){
												continue;
											}

											for(DataElementContainer daElem: Utils.getSubordinateElements(elements.get(i))){
												if(daElem == subEl && elementsSelected.get(i)){
													applyDeSelect = false;
													break;
												}
											}
										}

										if(applyDeSelect){
											elementsSelected.set(subElIndex, (Boolean)aValue);
										}

									}else{
										elementsSelected.set(subElIndex, (Boolean)aValue);
									}
									break;
								}
							}
						}
					}
				}	

				if(isSubmissionItem && elements.get(row).getElement() instanceof BasicEntry){
					//If the element is a subordinate of another item in the list, and that (or those) item(s) are already checked,
					//Then un-checking this item is not be allowed.
					//First thing to do is to retrieve the subordinate elements of all the other items

					for(int i = 0; i < elements.size(); i++){

						if(!allowElementSelectionChange){
							break;
						}

						if(i == row){
							continue;
						}

						DataElementContainer p = elements.get(i);
						List<DataElementContainer> subElements = Utils.getSubordinateElements(p);
						for(DataElementContainer sub: subElements){
							if(sub == elements.get(row) && elementsSelected.get(i) == true){
								allowElementSelectionChange = false;
								break;
							}
						}
					}

				}
				if(allowElementSelectionChange == true){
					elementsSelected.set(row, (Boolean)aValue);
				}
			}else{
				super.setValueAt(aValue, row, column);
			}
			fireTableDataChanged();
		}
	}

	public abstract void actionPerformed(ActionEvent e);

	public void valueChanged(ListSelectionEvent e) {
		if(candidateTableModel.areAnyRowsSelected()){
			submitButton.setEnabled(true);
		}
		else{
			submitButton.setEnabled(false);
		}

	}

	public String getSubmissionQuestionText() {
		return submissionQuestionText;
	}

	public void setSubmissionQuestionText(String submissionQuestionText) {
		this.submissionQuestionText = submissionQuestionText;
	};

	protected void saveLocally() {
		try {
			LocalFileUtility.delsave();
			//autosave as well just in case!
			LocalFileUtility.autosave(); 
		} catch (Exception ex) {
			LOG.error("Exception happened saving local del or autosave", ex);
		}
	}

	protected void updateElement(DataElementContainer oldElement, DataElementContainer newElement) {
		DataSet ds = (DataSet)DatasetController.getInstance().getActiveDs().getDs();
		for (int i = 0; i < ds.numDocuments(); i++) {
			Document doc = ds.getDocument(i);

			/*
			 * Check whether the updated element is a document
			 */
			if (doc.equals(oldElement.getElement())) {
				Document newDoc = (Document)newElement.getElement();
				Document oldDoc = (Document)oldElement.getElement();

				ds.removeDocument(i);

				ds.addDocument(newDoc);

				doc.setMyDataSet(ds);
				newDoc.setMyDataSet(ds);

				DatasetController.getInstance().setActiveDocument(newDoc);
				
				//Find and replace any potentially updated validation rules

				/*
				 * Check all the old document's entries for any validation
				 * rules to remove
				 */
				for (int j = 0; j < oldDoc.numEntries(); j++) {
					Entry entry = oldDoc.getEntry(j);
					if (entry instanceof BasicEntry) {
						/*
						 * Remove any validation rules used by the entry 
						 */
						if (((BasicEntry)entry).getValidationRules() != null) {
							for (int m = 0; m < ((BasicEntry)entry).numValidationRules(); m++) {
								ValidationRule rule = ((BasicEntry)entry).getValidationRule(m);
								for (int n = 0; n < ds.numValidationRules(); n++) {
									ValidationRule existingRule = (ValidationRule)ds.getValidationRule(n);
									if (rule.equals(existingRule)) {
										ds.removeValidationRule(n);
									}
								}
							}
						}
						if (entry instanceof ExternalDerivedEntry) {
							if (((ExternalDerivedEntry)entry).getExternalTransformer() != null) {
								for (int m=0; m < ds.numTransformers(); m++) {
									if (((ExternalDerivedEntry)entry).getExternalTransformer().equals(ds.getTransformer(m))) {
										ds.removeTransformer(m);
									}
								}
							}
						}
					}
				}
				/*
				 * Check all the new document's entries for any validation
				 * rules to add
				 */
				for (int j = 0; j < newDoc.numEntries(); j++) {
					Entry entry = newDoc.getEntry(j);
					if (entry instanceof BasicEntry) {
						/*
						 * Add any validation rules used by the entry 
						 */
						if (((BasicEntry)entry).getValidationRules() != null) {
							for (int m = 0; m < ((BasicEntry)entry).numValidationRules(); m++) {
								ValidationRule rule = ((BasicEntry)entry).getValidationRule(m);
								if (!ds.getValidationRules().contains(rule)) {
									ds.addValidationRule(rule);
								}
							}
						}
						if (entry instanceof ExternalDerivedEntry) {
							if (((ExternalDerivedEntry)entry).getExternalTransformer() != null
									&& !ds.getTransformers().contains(((ExternalDerivedEntry)entry).getExternalTransformer())) {
								ds.addTransformer(((ExternalDerivedEntry)entry).getExternalTransformer());
							}
						}
					}
				}

				//Refresh the open document to reflect the new statuses, etc.
				if (mainPane.getPanelForDocument(doc) != null) {
					mainPane.closeTab(doc);
					mainPane.openTab(newDoc);
				}
				DocTreeModel.getInstance().refreshDocument(newDoc);
				break;	//Found element, so stop
			}

			/*
			 * Check whether the updated element is an entry of the current document
			 */
			for (int j = 0; j < doc.numEntries(); j++) {
				Entry entry = doc.getEntry(j);
				if (entry.equals(oldElement.getElement())) {
					DatasetController.getInstance().setActiveDocument(doc);
					Entry newEntry = (Entry)newElement.getElement();

					//Add back variables not returned by the DEL
					newEntry.setEntryStatus(entry.getEntryStatus());
					newEntry.setSection(entry.getSection());
					newEntry.setMyDataSet(ds);

					doc.getEntries().set(j, newEntry);

					updateDependants(entry, newEntry, doc);

					if (entry instanceof BasicEntry) {
						/*
						 * Update any validation rules used by the entry 
						 */
						if (((BasicEntry)entry).getValidationRules() != null) {
							for (int m = 0; m < ((BasicEntry)entry).numValidationRules(); m++) {
								ValidationRule rule = ((BasicEntry)entry).getValidationRule(m);
								for (int k = 0; k < ds.numValidationRules(); k++) {
									ValidationRule existingRule = (ValidationRule)ds.getValidationRule(k);
									if (rule.equals(existingRule)) {
										ds.removeValidationRule(k);
										//This assumes that the list of validation rules in the new and old entries are in the same order
										if (!((DataSet)ds).getValidationRules().contains(((BasicEntry)newEntry).getValidationRule(m))) {
											ds.addValidationRule(((BasicEntry)newEntry).getValidationRule(m));
										}
									}
								}
							}
						}

						if (entry instanceof ExternalDerivedEntry) {
							if (((ExternalDerivedEntry)entry).getExternalTransformer() != null) {
								for (int m=0; m < ds.numTransformers(); m++) {
									if (((ExternalDerivedEntry)entry).getExternalTransformer().equals(ds.getTransformer(m))) {
										ds.removeTransformer(m);
										if (!ds.getTransformers().contains(((ExternalDerivedEntry)entry).getExternalTransformer())) {
											ds.addTransformer(((ExternalDerivedEntry)newEntry).getExternalTransformer());
										}
									}
								}
							}
						}
					}

					//Refresh the open document to reflect the new statuses, etc.
					if (mainPane.getPanelForDocument(doc) != null) {
						mainPane.closeTab(doc);
						mainPane.openTab(doc);
					}
					DocTreeModel.getInstance().refreshDocument(doc);
					break;	//Found element, so stop
				}
			}
		}

		/*
		 * Check whether the updated element is a validation rule
		 */
		for (int k = 0; k < ds.numValidationRules(); k++) {
			ValidationRule rule = (ValidationRule)ds.getValidationRule(k);
			if (rule.equals(oldElement.getElement())) {
				ds.removeValidationRule(k);
				ds.addValidationRule((ValidationRule)newElement.getElement());
			}
		}

		//Update tree to reflect document/entry changes
		if (!this.submissionLevel.equals(SubmissionLevel.ValidationRule)) {
			((MainFrame)this.mainPane.getFrame()).getMainTree().treeNodesChanged(null);
		}
	}

	/**
	 * Update any entries dependant on the entry being updated.
	 * 
	 * This is required for derived, external derived and composite entries when
	 * they are checked out individually, in order to update the dependant entries
	 * that are also checked out.
	 * 
	 * @param oldEntry
	 * @param newEntry
	 * @param doc
	 */
	private void updateDependants(Entry oldEntry, Entry newEntry, Document doc) {

		List<DataElementContainer> oldSubordinates = Utils.getSubordinateElements(new DataElementContainer(oldEntry));
		List<DataElementContainer> newSubordinates = Utils.getSubordinateElements(new DataElementContainer(newEntry));
		for (DataElementContainer oldContainer: oldSubordinates) {

			//Subordinates should only ever be basic entries..
			if (oldContainer.getElement() instanceof BasicEntry) {
				BasicEntry oldBE = (BasicEntry)oldContainer.getElement();

				for (int k = 0; k < doc.numEntries(); k++) {
					Entry docEntry = doc.getEntry(k);
					if (docEntry.equals(oldBE)) {
						String newlsid = mainPane.getDelInitializer().getLatestLSID(oldBE.getLSID().toString());
						for (DataElementContainer newContainer: newSubordinates) {
							BasicEntry newBE = (BasicEntry)newContainer.getElement();
							if (newlsid.equals(newBE.getLSID().toString())) {
								newBE.setEntryStatus(docEntry.getEntryStatus());
								newBE.setSection(docEntry.getSection());
								newBE.setMyDataSet(ds);

								//Dependants for entries checked out individually should not be editable.
								newBE.setIsEditable(false);

								doc.getEntries().set(k, newBE);	
							}
							if (newBE instanceof DerivedEntry
									||newBE instanceof ExternalDerivedEntry) {
								//Recurse through any dependants that are derived entries too..
								this.updateDependants(oldBE, newBE, doc);
							}
						}
					}
					else if (docEntry instanceof CompositeEntry) {
						CompositeEntry docComp = (CompositeEntry)docEntry;

						String newlsid = mainPane.getDelInitializer().getLatestLSID(oldBE.getLSID().toString());
						for (DataElementContainer newContainer: newSubordinates) {
							BasicEntry newBE = (BasicEntry)newContainer.getElement();

							for (BasicEntry docBE: docComp.getEntries()) {
								if (docBE.equals(newBE)) {
									if (newlsid.equals(newBE.getLSID().toString())) {
										newBE.setEntryStatus(docEntry.getEntryStatus());
										newBE.setSection(docEntry.getSection());
										newBE.setMyDataSet(ds);

										//Dependants for entries checked out individually should not be editable.
										newBE.setIsEditable(false);
									}
									if (newBE instanceof DerivedEntry
											||newBE instanceof ExternalDerivedEntry) {
										//Recurse through any dependants that are derived entries too..
										this.updateDependants(oldBE, newBE, doc);
									}
								}
							}
						}
					}
				}
			}
		}

	}
	

	protected void doSubmit(List<DataElementContainer> selectedElements) {
		CandidateTableModel candidateModel = (CandidateTableModel)this.candidateEntriesTable.getModel();
		int candidateCount = candidateModel.getRowCount();
		String authority = (String)this.authorityList.getSelectedItem();

		//Now it's entirely possible that some of the 'selected' candidates are going to be entered anyway, becuase
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
		String submissionNotes = submissionText.getText();

		//Submit the elements now...
		//Need to create
		HibernateDataElementFactory factory = new HibernateDataElementFactory();
		IAdminInfo info = factory.createAdminInfo(DataElementAction.SUBMIT, submissionNotes, null, null, true);

		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		for(DataElementContainer e1: selectedElements) {

			String operationType = null;
			try {
				String samlAssertion = mainPane.getDelInitializer().getSaml();
				if(e1.getElementLSIDObject() == null){
					operationType = "Save";
					mainPane.getDelInitializer().getClient().saveNewElement(e1, info, (String)this.authorityList.getSelectedItem(),samlAssertion);
				}else if(e1.getElementLSIDObject() != null && e1.getIsEditable()){
					operationType = "Revision";
					mainPane.getDelInitializer().getClient().reviseElement(e1, info, authority, samlAssertion);
					e1.setIsRevisionCandidate(false);

					//If this the element was a subordinate, we want to set the document as
					//'unauthorised to revise'
					if(submissionLevel == SubmissionLevel.Entry){
						if(Utils.elementIsASubordinate(e1, doc)){
							//TODO:DEL - display an appropriate messge here?
							StudyDataSet ds = DocTreeModel.getInstance().getDSDataset(doc.getMyDataSet().getName());
							ds.addDELRestrictedDoc(doc.getName());
							LOG.info("Disabling " + doc.getName() + " as a revisable document.");
						}
					}
				}else{
					//This should never happen.
				}

			} 
			catch (DELFailedTestException ex) {
				LOG.info("The element "+e1.getElementDescription()+" has test failures. Unable to "+operationType.toLowerCase()+" element.", ex);
				candidateModel.selectItem(false, e1); //De-select the item. Afterwards, only the selected items will be removed from the list.
				WrappedJOptionPane.showMessageDialog(this, operationType + " failed. The element "+e1.getElementName()+" must pass all attached tests before saving.", operationType + " Failure", JOptionPane.ERROR_MESSAGE);
				continue;
			}
			catch (Exception ex) {
				LOG.error(operationType + " failed for element: " +
						e1.getElementDescription() + ". The reason is - " + ex.getMessage(), ex);
				candidateModel.selectItem(false, e1); //De-select the item. Afterwards, only the selected items will be removed from the list.
				WrappedJOptionPane.showMessageDialog(this, operationType + " failed. Reason - " + ex.getMessage() + ".", operationType + " Failure", JOptionPane.ERROR_MESSAGE);
				continue;
			}

			DataElementContainer fromDEL;
			try {
				fromDEL = (DataElementContainer)Utils.getCompleteBrowseElement(mainPane, e1.getElementLSID());
			} catch (Exception e3) {
				String itemLSID = e1.getElementLSID();
				LOG.error("Could not retrieve LSID " + itemLSID + " following successful " + operationType + ". Local" +
						"entry could not be successfully updated.", e3);
				WrappedJOptionPane.showMessageDialog(this, operationType + " succeded for " + e1.getElementName() + " but subsequent client update failed. It may therefore not be possible" +
						"to edit the element further." , "Client Update Failure", JOptionPane.ERROR_MESSAGE);
				continue;
			}

			String name = e1.getElementName();
			if (name == null||name.equals("")) {
				name = e1.getElementDescription();
			}
			WrappedJOptionPane.showMessageDialog(this, operationType + " was successful for " + name + ".");

			/*
			 * Replace the local element with the newly approved one from the DEL
			 */
			updateElement(e1, fromDEL);
			
			e1 = fromDEL;	//Update the local element with the newly saved one from the DEL

		}

		/*
		 * Mark whether the document has any further remaining
		 * elements to be saved to the DEL.
		 */
		if (doc != null) {
			boolean isRevisionCandidate = false;
			for(DataElementContainer e1: selectedElements) {
				if (e1.getIsRevisionCandidate()) {
					isRevisionCandidate = true;
					break;
				}
			}
			doc.setIsRevisionCandidate(isRevisionCandidate);
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

		//set the dataset to dirty (to be able to save updates to element)
		DatasetController.getInstance().getActiveDs().setDirty(true);
		
		
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

		saveLocally();	//Update local settings (otherwise the new status of elements displayed locally can be forgotten)
	}
}

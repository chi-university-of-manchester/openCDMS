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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.common.ui.WrappedJOptionPane;
import org.psygrid.data.model.hibernate.DataElementStatus;
import org.psygrid.data.model.hibernate.ValidationRule;
import org.psygrid.data.model.hibernate.BasicEntry;
import org.psygrid.data.model.hibernate.DataElementContainer;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.DocumentOccurrence;
import org.psygrid.data.model.hibernate.ElementStatusContainer;
import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.datasetdesigner.model.DocTreeModel;
import org.psygrid.datasetdesigner.model.DummyDocument;
import org.psygrid.datasetdesigner.ui.MainTabbedPane;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.Utils;

/**
 * @author Lucy Bridges
 *
 */
public class UpdateElementsDialog extends JDialog implements ActionListener, ListSelectionListener {
	private static final long serialVersionUID = 6979495440732821044L;

	private static final Log LOG = LogFactory.getLog(UpdateElementsDialog.class);

	private MainTabbedPane mainPane;
	private List<ElementStatusContainer> changedElements;
	private JButton importButton;
	private JButton selectAllButton;
	private JButton closeButton;

	private List<DataElementContainer> elements; 

	/**
	 * Oldlsid:newlsid
	 */
	private Map<String,String> updatedLSIDs = new HashMap<String,String>();	

	private JTable elementsTable;

	public UpdateElementsDialog(MainTabbedPane docPane, List<ElementStatusContainer> changedElements) {
		super(docPane.getFrame(), PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.update.updateelements"));
		mainPane = docPane;
		this.changedElements = changedElements;
		elements = new ArrayList<DataElementContainer>();
		init();
		if (elements.size() == 0) {
			return;
		}
		build();

		//User should not be able to leave without selected one of the options
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	}

	private void init() {
		ArrayList<DataSet> datasets = DocTreeModel.getInstance().getAllDatasets();

		//Retrieve the updated lsids
		//This is so we can retrieve the updated object if needed later
		//It is done now so that pending elements can be ignored by viewers
		//Further checks are done later
		for (int i = changedElements.size()-1; i >= 0 ; i--) {
			ElementStatusContainer element = changedElements.get(i);
			if( element.getHeadRevisionStatus().equals(DataElementStatus.PENDING)
					&& !DELSecurity.getInstance().canViewPending()) { 
				//This may happen if the element has changed but the current user does not have permission to access it.
				changedElements.remove(i);
			}
			else {
				String updatedLSID = mainPane.getDelInitializer().getLatestLSID(element.getLsid().toString());
				updatedLSIDs.put(element.getLsid().toString(),updatedLSID);
			}
		}

		if (changedElements.size() == 0) {
			return;
		}

		for (DataSet dataset: datasets) {
			nextDocument: for (Document document: ((DataSet)dataset).getDocuments()) {
				for (ElementStatusContainer element: changedElements) {

					Document doc = (Document)document;
					if (element.getLsid().equals(doc.getLSID())) {
						//add the document and continue
						DataElementStatus status = doc.getStatus();
						if (DataElementStatus.APPROVED.equals(status)) {
							DataElementContainer docContainer = new DataElementContainer(doc);
							for (Entry entry: doc.getEntries()) {
								//We are adding the document, so remove any entries belonging to this document that have been added already
								DataElementContainer elemContainer = new DataElementContainer(entry);
								if (elements.contains(elemContainer)) {
									elements.remove(elemContainer);
								}
							}

							//Check that new version is not a pending version for viewers
							DataElementStatus newStatus = element.getHeadRevisionStatus();
							if (DataElementStatus.PENDING.equals(newStatus)) {
								if (DELSecurity.getInstance().canViewPending()) {
									//Viewers are not allowed to see pending elements
									elements.add(docContainer);
								}
							}
							else {
								elements.add(docContainer);
							}
						}
						else if (DataElementStatus.PENDING.equals(status)) {
							elements.add(new DataElementContainer(doc));
						}

						continue nextDocument;
					}

					//should only add an entry if the document hasn't already been matched (might currently match an entry then a document after..)
					for (Entry entry: doc.getEntries()) {
						if (element.getLsid().equals(entry.getLSID())) {
							//Add the entry
							DataElementStatus entryStatus = entry.getStatus();
							if (DataElementStatus.APPROVED.equals(entryStatus)) {
								//Check that new version is not a pending version for viewers
								DataElementContainer entryContainer = new DataElementContainer(entry);
								DataElementStatus newStatus = element.getHeadRevisionStatus();
								if (DataElementStatus.PENDING.equals(newStatus)) {
									if (DELSecurity.getInstance().canViewPending()) {
										//Viewers are not allowed to see pending elements
										elements.add(entryContainer);
									}
								}
								else {
									elements.add(entryContainer);
								}
							}
							else if (DataElementStatus.PENDING.equals(entryStatus)) {
								elements.add(new DataElementContainer(entry));
							}

						}	
					}

				}
			}

			for (ValidationRule rule: ((DataSet)dataset).getValidationRules()) {
				for (ElementStatusContainer element: changedElements) {
					if (element.getLsid().equals(rule.getLSID())) {
						DataElementStatus ruleStatus = rule.getStatus();
						if (DataElementStatus.APPROVED.equals(ruleStatus)) {
							//Check that new version is not a pending version for viewers
							DataElementStatus newStatus = element.getHeadRevisionStatus();
							if (DataElementStatus.PENDING.equals(newStatus)) {
								if (DELSecurity.getInstance().canViewPending()) {
									//Viewers are not allowed to see pending elements
									elements.add(new DataElementContainer(rule));
								}
							}
							else {
								elements.add(new DataElementContainer(rule));
							}
						}
						else if (DataElementStatus.PENDING.equals(ruleStatus)) {	
							elements.add(new DataElementContainer(rule));
						}
					}
				}
			}

		}
	}

	private DataElementStatus getUpdatedStatus(DataElementContainer element) {
		for (ElementStatusContainer changed: changedElements) {
			try {
				if (element.getElementLSID().equals(changed.getLsid().toString())) {
					//Found matching element, so return the updated revision status
					return changed.getHeadRevisionStatus();
				}
			}
			catch (Exception e) {
				LOG.error("Unable to parse LSID for a changed Element", e);
			}
		}
		return null;
	}

	private void build() {
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(buildTitlePanel(),  BorderLayout.NORTH);
		getContentPane().add(buildElementsPanel(),  BorderLayout.CENTER);
		getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
		pack();
		setLocationRelativeTo(null);  
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == closeButton){
			String message = PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.update.dontupdate");
			if (DELSecurity.getInstance().canEditElements()) {
				message = PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.update.dontupdatereadonly");
			}
			int answer = WrappedJOptionPane.showConfirmDialog(this, message, "", WrappedJOptionPane.OK_CANCEL_OPTION);

			switch (answer) {
			case WrappedJOptionPane.OK_OPTION:
				setReadOnly(elements);
				dispose();
				break;
			case WrappedJOptionPane.NO_OPTION:
				break;
			}
		}
		else if(e.getSource() == selectAllButton) { 
			if (selectAllButton.getText().equals(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.selectall"))) {
				((CustomTableModel)elementsTable.getModel()).selectAll();
				selectAllButton.setText(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.deselectall"));
			}
			else {
				((CustomTableModel)elementsTable.getModel()).deselectAll();
				selectAllButton.setText(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.selectall"));
			}
		}
		else if(e.getSource() == importButton){

			//Get selected elements
			List<DataElementContainer> selected = new ArrayList<DataElementContainer>();
			for (int i = 0; i < elements.size(); i++) {
				boolean isSelected = ((CustomTableModel)elementsTable.getModel()).getRowIsSelected(i);
				if (isSelected) {
					selected.add(elements.get(i));
				}
			}

			boolean localEdits = false;
			for (DataElementContainer elem: selected) {
				if (elem.getIsRevisionCandidate()) {
					localEdits = true;
					break;
				}
			}

			//Show a warning if elements have been updated locally
			if (localEdits) {
				int answer = WrappedJOptionPane.showConfirmDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.update.confirmupdate"), "", WrappedJOptionPane.OK_CANCEL_OPTION);
				switch (answer) {
				case WrappedJOptionPane.OK_OPTION:
					updateElements(selected);
					break;
				case WrappedJOptionPane.NO_OPTION:
					break;
				}
			}
			else {
				updateElements(selected);
			}
		}
	}

	private JPanel buildTitlePanel() {
		JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		titlePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		JLabel title = new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.update.elementupdates"));
		Font font = title.getFont();
		title.setFont(new Font(font.getFamily(), font.getStyle(), font.getSize()+1));
		titlePanel.add(title);
		return titlePanel;
	}

	private JPanel buildButtonPanel() {

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);

		selectAllButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.selectall"));
		selectAllButton.setEnabled(true);
		selectAllButton.addActionListener(this);
		buttonPanel.add(selectAllButton);

		buttonPanel.add(new JLabel("	"));
		buttonPanel.add(new JLabel("	"));
		buttonPanel.add(new JLabel("	"));
		buttonPanel.add(new JLabel("	"));

		importButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.update"));
		importButton.setEnabled(false);
		importButton.addActionListener(this);
		buttonPanel.add(importButton);

		closeButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.update.ignore"));	
		closeButton.addActionListener(this);
		buttonPanel.add(closeButton);

		return buttonPanel;
	}

	private JPanel buildElementsPanel() {
		JPanel contentPanel = new JPanel();

		elementsTable = new JTable(new CustomTableModel());
		//substance 4.0 defaults table headers to the left
		((DefaultTableCellRenderer)elementsTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
		elementsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		elementsTable.getColumnModel().getColumn(0).setHeaderValue(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.selected"));
		elementsTable.getColumnModel().getColumn(1).setHeaderValue(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.update.name"));
		elementsTable.getColumnModel().getColumn(2).setHeaderValue(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.update.type"));
		elementsTable.getColumnModel().getColumn(3).setHeaderValue(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.update.localrevision"));
		elementsTable.getColumnModel().getColumn(4).setHeaderValue(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.update.newrevision"));
		elementsTable.getColumnModel().getColumn(5).setHeaderValue(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.update.localedits"));
		elementsTable.getSelectionModel().addListSelectionListener(this);

		//Populate the table
		for (int i = 0; i < elements.size(); i++) {
			Vector vWrapper = new Vector();
			vWrapper.add(elements.get(i));
			((CustomTableModel)elementsTable.getModel()).addRow(vWrapper);
		}

		JScrollPane filterColumnScrollPane = new javax.swing.JScrollPane(elementsTable);
		filterColumnScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		filterColumnScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		filterColumnScrollPane.setMaximumSize(new java.awt.Dimension(800, 200));
		filterColumnScrollPane.setMinimumSize(new java.awt.Dimension(400, 180));
		filterColumnScrollPane.setPreferredSize(new java.awt.Dimension(600,200));

		JPanel scrollWrapperLeftAlignmentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		scrollWrapperLeftAlignmentPanel.add(filterColumnScrollPane);

		contentPanel.add(scrollWrapperLeftAlignmentPanel);

		return contentPanel;
	}

	private enum ElementType{
		Document,
		Entry,
		Validation_Rule
	}

	private class CustomTableModel extends DefaultTableModel{ 

		private static final long serialVersionUID = 1L;

		private List<Boolean> elementsSelected = new ArrayList<Boolean>(elements.size());

		public CustomTableModel() {
		}

		@Override
		public void addRow(Vector elem) {	
			elementsSelected.add(false);
			fireTableDataChanged();
		}

		public void	clearContent(){
			elements.clear();
		}

		public DataElementContainer getRowObject(int row){
			return elements.get(row);
		}

		@Override
		public void removeRow(int row) {
			elements.remove(row);
			elementsSelected.remove(row);
			fireTableDataChanged();
		}

		@Override
		public int getRowCount() {
			if (elements != null) {
				return elements.size();
			}
			return 0;
		}

		@Override
		public int getColumnCount() {
			return 6;
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			if (column == 0) {
				return true;
			}
			return false;
		}

		public void selectRow(boolean value, int row){
			elementsSelected.set(row, value);
			fireTableDataChanged();
		}

		public void selectAll(){
			int elementsSelectedSize = elementsSelected.size();
			elementsSelected.clear();

			for(int i = 0; i < elementsSelectedSize; i++){
				elementsSelected.add(true);
			}
			valueChanged(null);	//Update the 'update' button
			fireTableDataChanged();
		}

		public void deselectAll(){
			int elementsSelectedSize = elementsSelected.size();
			elementsSelected.clear();

			for(int i = 0; i < elementsSelectedSize; i++){
				elementsSelected.add(false);
			}
			valueChanged(null);	//Update the 'update' button
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
		public void setValueAt(Object aValue, int row, int column) {
			if (column == 0) {
				elementsSelected.set(row, (Boolean)aValue);
			} 
			else {
				super.setValueAt(aValue, row, column);
			}
			fireTableDataChanged();
		}

		@Override
		public Object getValueAt(int row, int column) {
			DataElementContainer elem = elements.get(row);
			Object returnObj;
			switch(column){
			case 0:
				//Is selected?
				returnObj = elementsSelected.get(row);
				break;
			case 1: 
				//Element name
				returnObj = elem.getElementName();
				if (returnObj == null || ((String)returnObj).equals("")) {
					returnObj = elem.getElementDescription();	
				}
				break;
			case 2: 
				//Element type
				if (elem.getElement() instanceof Document) {
					returnObj = ElementType.Document;
				}
				else if (elem.getElement() instanceof ValidationRule) {
					returnObj = ElementType.Validation_Rule;
				}
				else {
					returnObj = ElementType.Entry;	
				}
				break;
			case 3: 
				//Old status
				returnObj = elem.getStatus().toString();
				break;
			case 4: 
				//New status
				DataElementStatus newStatus = getUpdatedStatus(elem);
				returnObj = "";
				if (newStatus != null) {	//Shouldn't happen..
					returnObj = newStatus.toString();
				}
				break;
			case 5:	
				//Element has local edits
				returnObj = elem.getIsRevisionCandidate();
				break;
			default: 
				returnObj = elem.getElementName();
			}

			return returnObj;
		}

		public Class getColumnClass(int columnIndex) {
			if(columnIndex == 0) {
				return Boolean.class;
			} else {
				return super.getColumnClass(columnIndex);
			}
		}
	}

	public void valueChanged(ListSelectionEvent e) {

		if (((CustomTableModel)elementsTable.getModel()).areAnyRowsSelected()) {
			importButton.setEnabled(true);
		}
		else {
			importButton.setEnabled(false);
		}
	}

	private boolean updateElements(List<DataElementContainer> elements) {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		List<DataElementContainer> failed = new ArrayList<DataElementContainer>();

		nextElement: for (DataElementContainer element: elements) {
			String lsid = updatedLSIDs.get(element.getElementLSID());

			/*
			 * Retrieve the updated element from the data element library
			 */
			DataElementContainer fromDEL;
			try {
				fromDEL = (DataElementContainer)Utils.getUpdatedCompleteElement(mainPane, lsid);
			} catch (Exception e3) {e3.printStackTrace();
			LOG.error("Could not retrieve LSID " + lsid +". Local entry could not be successfully updated.", e3);
			WrappedJOptionPane.showMessageDialog(this, "Update failed for "+element.getElementName() + ". It may therefore not be possible" +
					"to edit the element further." , "Client Update Failure", JOptionPane.ERROR_MESSAGE);
			continue;
			}

			/*
			 * Replace the locally held element
			 */
			List<DataSet> datasets = DocTreeModel.getInstance().getAllDatasets();

			if (element.getElement() instanceof Document) {
				for (DataSet ds: datasets) {
					for (int i = 0; i < ds.numDocuments(); i++) {
						Document doc = (Document)ds.getDocument(i);

						if (doc.equals(element.getElement())) {
							Document newDoc = (Document)fromDEL.getElement();
				
							ds.removeDocument(i);
							ds.addDocument(newDoc);

							doc.setMyDataSet((DataSet)ds);
							newDoc.setMyDataSet((DataSet)ds);

							for (DocumentOccurrence docOcc: doc.getOccurrences()) {
								docOcc.setDocument(newDoc);
							}
							newDoc.setOccurrences(doc.getOccurrences());

							//Find and replace any potentially updated validation rules

							/*
							 * Check all the old document's entries for any validation
							 * rules to remove
							 */
							for (int j = 0; j < doc.numEntries(); j++) {
								Entry entry = doc.getEntry(j);
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
											if (!((DataSet)ds).getValidationRules().contains(rule)) {
												ds.addValidationRule(rule);
											}
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

							continue nextElement;
						}
					}
				}
				failed.add(element);
			}
			else if (element.getElement() instanceof Entry) {
				for (DataSet ds: datasets) {
					for (int i = 0; i < ds.numDocuments(); i++) {
						Document doc = (Document)ds.getDocument(i);
						for (int j = 0; j < doc.numEntries(); j++) {
							Entry entry = doc.getEntry(j);
							if (entry.equals(element.getElement())) {
								Entry newEntry = (Entry)fromDEL.getElement();

								//Add back variables not returned by the DEL
								newEntry.setEntryStatus(entry.getEntryStatus());
								newEntry.setSection(entry.getSection());
								newEntry.setMyDataSet((DataSet)ds);

								doc.getEntries().set(j, newEntry);

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
								}

								if (!(doc instanceof DummyDocument)) {
									doc.setIsRevisionCandidate(true);
								}

								//Refresh the open document to reflect the new statuses, etc.
								if (mainPane.getPanelForDocument(doc) != null) {
									mainPane.closeTab(doc);
									mainPane.openTab(doc);
								}
								DocTreeModel.getInstance().refreshDocument(doc);

								continue nextElement;
							}
						}
					}
				}
				failed.add(element);
			}
			else {
				//Assuming validation rule
				for (DataSet ds: datasets) {
					for (int k = 0; k < ds.numValidationRules(); k++) {
						ValidationRule rule = (ValidationRule)ds.getValidationRule(k);
						if (rule.equals(element.getElement())) {
							ds.removeValidationRule(k);
							ds.addValidationRule((ValidationRule)fromDEL.getElement());
							continue nextElement;
						}
					}
				}
				failed.add(element);
			}

		}

		//update table if some elements remain, otherwise dispose 
		for (int i = 0; i < elements.size(); i++) {
			if (!failed.contains(elements.get(i))) {
				for (int j = 0; j < this.elements.size(); j++) {
					if (elements.get(i).equals(this.elements.get(j))) {
						((CustomTableModel)elementsTable.getModel()).removeRow(j);
					}
				}
			}
		}
		if (((CustomTableModel)elementsTable.getModel()).getRowCount() == 0) {
			dispose();
		}

		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

		if (failed.size() > 0) {
			StringBuilder failures = new StringBuilder();
			failures.append("\n");
			for (DataElementContainer elem: failed) {
				if (elem.getElementName() != null && !elem.getElementName().equals("")) {
					failures.append(elem.getElementName());	
				}
				else {
					failures.append(elem.getElementDescription());	
				}
				failures.append("\n");
			}
			LOG.info("The following elements failed to update (no local match was found for the element(s) to be updated): " +
					failures.toString());
			WrappedJOptionPane.showWrappedMessageDialog(this, "The following elements failed to update: " +
					failures.toString(), "", WrappedJOptionPane.WARNING_MESSAGE);
			return false;	//some elements failed to update
		}
		return true;
	}

	private void setReadOnly(List<DataElementContainer> elements) {
		for (DataElementContainer element: elements) {
			element.setIsEditable(false);
			if (element.getElement() instanceof Document) {
				//This will stop the document from being checked for updates again in future 
				((Document)element.getElement()).setHeadRevision(false);	
				for (Entry entry: ((Document)element.getElement()).getEntries()) {
					entry.setIsEditable(false);
					entry.setHeadRevision(false);
				}
			}
			else if (element.getElement() instanceof Entry) {
				((Entry)element.getElement()).setHeadRevision(false);
			}
			else if (element.getElement() instanceof ValidationRule) {
				((ValidationRule)element.getElement()).setHeadRevision(false);
			}
		}
	}
}

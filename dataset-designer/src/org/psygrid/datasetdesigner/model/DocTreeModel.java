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
package org.psygrid.datasetdesigner.model;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.psygrid.data.model.hibernate.*;

import org.psygrid.datasetdesigner.controllers.DatasetController;
import org.psygrid.datasetdesigner.utils.ElementUtility;

/**
 * The DocTreeModel controls the tree model for 
 * display in the left hand pane
 * 
 *
 * 
 * @author pwhelan
 */
//TODO: rewrite this; splitting into classes for creation factory, tree model and defaultdatasetsettings
public class DocTreeModel extends DefaultTreeModel {
	
	/** A singleton instance of this class*/
	private static DocTreeModel singleton;

	/** Action Listeners */
	private Vector<ActionListener> listeners = new Vector<ActionListener>();

	/**
	 * Constructor - creates and empty tree model
	 */
	public DocTreeModel() {
		super(new DefaultMutableTreeNode());
		listeners = new Vector<ActionListener>();
	}

	/**
	 * Return an instance of the doc tree model
	 * @return retun an instance of this class
	 */
	public static DocTreeModel getInstance() {
		if (singleton == null) {
			singleton = new DocTreeModel();
		}
		return singleton;
	}

	/**
	 * Add a dataset to the tree model
	 * Iterates the tree and add documents and entries 
	 * @param dataset the dataset to add
	 */
	public void addDataset(StudyDataSet dataset) {
		//call this first so that listeners will know what it is!
		DatasetController.getInstance().setActiveDs(dataset);
		
		DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(dataset);
		DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)getRoot();
		//insert it at the end of the other ones
		insertNodeInto(childNode, parentNode, ((DefaultMutableTreeNode)getRoot()).getChildCount());
		for (int i=0; i<dataset.getDs().numDocuments(); i++) {
			Document doc = dataset.getDs().getDocument(i);
			addDocument(doc);
			if (!(doc instanceof DummyDocument)) {
				for (int j=0; j<doc.numEntries(); j++) {
					Entry entry = doc.getEntry(j);
					if (!entry.isLocked()) {
						addEntry(entry, doc);
					}
				}
			}
		}
	}
	
	/**
	 * Add a dataset to the tree model
	 * Iterates the tree and add documents and entries 
	 * @param dataset the dataset to add
	 */
	public void insertDataset(StudyDataSet dataset, int index) {
		DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(dataset);
		DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)getRoot();
		//insert it at the end of the other ones
		insertNodeInto(childNode, parentNode, index);
		for (int i=0; i<dataset.getDs().numDocuments(); i++) {
			Document doc = dataset.getDs().getDocument(i);
			addDocument(doc);
			if (!(doc instanceof DummyDocument)) {
				for (int j=0; j<doc.numEntries(); j++) {
					Entry entry = doc.getEntry(j);
					if (!entry.isLocked()) {
						addEntry(entry, doc);
					}
				}
			}
		}
	}


	/**
	 * Get all the datasets in the tree
	 * @return a list of all the DSDatasets in the tree 
	 */
	public ArrayList<StudyDataSet> getAllDSDatasets() {
		ArrayList<StudyDataSet> dsDatasets = new ArrayList<StudyDataSet>();
		for (int i=0; i<((DefaultMutableTreeNode)getRoot()).getChildCount(); i++) {
			dsDatasets.add(((StudyDataSet)((DefaultMutableTreeNode)((DefaultMutableTreeNode)getRoot()).getChildAt(i)).getUserObject()));
		}
		return dsDatasets;
	}

	/**
	 * Get all the datasets in tree
	 * @return a list of all the IDataSet in the tree
	 */
	public ArrayList<DataSet> getAllDatasets() {
		ArrayList<DataSet> datasets = new ArrayList<DataSet>();

		for (int i=0; i<((DefaultMutableTreeNode)getRoot()).getChildCount(); i++) {
			datasets.add(((StudyDataSet)((DefaultMutableTreeNode)((DefaultMutableTreeNode)getRoot()).getChildAt(i)).getUserObject()).getDs());
		}

		return datasets;
	}
	
	/**
	 * Only one dataset can ever be in the tree; return this here; otherwise
	 * @return get the dataset in the tree; null if no dataset present
	 */
	public StudyDataSet getDataset() {
		if (((DefaultMutableTreeNode)getRoot()).getChildCount() > 0) {
			return ((StudyDataSet)((DefaultMutableTreeNode)((DefaultMutableTreeNode)getRoot()).getChildAt(0)).getUserObject());
		}
		return null;
	}

	/**
	 * Get the dataset with this name
	 * @param datasetName name of the dataset to fetch
	 * @return DSDataSet the DSDataset that matches this one
	 */
	public StudyDataSet getDSDataset(String datasetName) {
		for (int i=0; i<((DefaultMutableTreeNode)getRoot()).getChildCount(); i++) {
			StudyDataSet curDSSet = ((StudyDataSet)((DefaultMutableTreeNode)((DefaultMutableTreeNode)getRoot()).getChildAt(i)).getUserObject());
			DataSet curDataset = curDSSet.getDs();
			if (curDataset.getName().equals(datasetName)) {
				return curDSSet;
			}
		}

		return null;
	}

	/**
	 * Get the dataset with this name
	 * @param datasetName name of the dataset to fetch
	 * @return IDataSet the IDataset that matches this name
	 */
	public DataSet getDataset(String datasetName) {
		for (int i=0; i<((DefaultMutableTreeNode)getRoot()).getChildCount(); i++) {
			DataSet curDataset = ((StudyDataSet)((DefaultMutableTreeNode)((DefaultMutableTreeNode)getRoot()).getChildAt(i)).getUserObject()).getDs();
			if (curDataset.getName().equals(datasetName)) {
				return curDataset;
			}
		}
		return null;
	}

	/**
	 * Retrieve the 'dummy' dataset created for the data element
	 * library view.
	 * 
	 * @param datasetName
	 * @return dataset
	 */
	public StudyDataSet getDELDataset() {
		StudyDataSet ds = null;
		for (StudyDataSet d: DocTreeModel.getInstance().getAllDSDatasets()) {
			if (d instanceof DELStudySet) {
				ds = d;		
				break;
			}
		}
		return ds;
	}


	/**
	 * Get the document that has this document name
	 * @param documentName the name of the document to fetch
	 * @return IDocument the IDocument that matches this document name
	 */
	public Document getDocument(String documentName) {
		for (int i=0; i<((DefaultMutableTreeNode)getRoot()).getChildCount(); i++) {
			StudyDataSet curDS = (StudyDataSet)((DefaultMutableTreeNode)((DefaultMutableTreeNode)getRoot()).getChildAt(i)).getUserObject();
			DataSet curIS = curDS.getDs();
			for (int j=0; j<curIS.numDocuments(); j++) {
				if (curIS.getDocument(j).getName().equals(documentName)) {
					return curIS.getDocument(j);
				}
			}
		}
		return null;
	}
	
	/**
	 * Get the document that has this document name
	 * @param documentName the name of the document to fetch
	 * @return IDocument the IDocument that matches this document name
	 */
	public DefaultMutableTreeNode getDatasetNode(Document document) {
		for (int i=0; i<((DefaultMutableTreeNode)getRoot()).getChildCount(); i++) {
			StudyDataSet curDS = (StudyDataSet)((DefaultMutableTreeNode)((DefaultMutableTreeNode)getRoot()).getChildAt(i)).getUserObject();
			DataSet curIS = curDS.getDs();
			if (curIS.equals(document.getDataSet())){
				return ((DefaultMutableTreeNode)((DefaultMutableTreeNode)getRoot()).getChildAt(i));
			}
		}
		return null;
	}

	/**
	 * Remove this document with this document name
	 * @param documentName the name of the document to remove
	 */
	public void removeDocument(Document document) {
		//if published, set doc occs to locked (rather than removing)
		if (document.getDataSet().isPublished()) {
			for (int i=0; i<document.numOccurrences(); i++) {
				document.getOccurrence(i).setLocked(true);
			}
		} else {
			//remove from dataset
			for (int i=0; i<document.getDataSet().numDocuments(); i++) {
				if (document.getDataSet().getDocument(i).getName().equals(document.getName())) {
					document.getDataSet().removeDocument(i);
					StudyDataSet ds = this.getDSDataset(document.getDataSet().getName());
					ds.removeDelRestrictedDoc(document.getName());
				}
			}

			//remove from tree
			DefaultMutableTreeNode parentNode = new DefaultMutableTreeNode();
			for (int i=0; i<((DefaultMutableTreeNode)getRoot()).getChildCount(); i++) {
				DataSet curDataset = ((StudyDataSet)((DefaultMutableTreeNode)((DefaultMutableTreeNode)getRoot()).getChildAt(i)).getUserObject()).getDs();
				if (curDataset.getName().equals(document.getDataSet().getName())) {
					parentNode = (DefaultMutableTreeNode)((DefaultMutableTreeNode)getRoot()).getChildAt(i);

					for (int j=0; j<parentNode.getChildCount(); j++) {
						Document childDoc = (Document)((DefaultMutableTreeNode)(parentNode.getChildAt(j))).getUserObject();
						if (childDoc.getName().equals(document.getName())){
							removeNodeFromParent(((DefaultMutableTreeNode)(parentNode.getChildAt(j))));
						}
					}
				}
			}
		}
	}

	/**
	 * Refresh the document in the tree
	 * @param document the IDocument to refresh
	 */
	public void refreshDocument(Document document) {
		DefaultMutableTreeNode parentNode = new DefaultMutableTreeNode();
		for (int i=0; i<((DefaultMutableTreeNode)getRoot()).getChildCount(); i++) {
			DataSet curDataset = ((StudyDataSet)((DefaultMutableTreeNode)((DefaultMutableTreeNode)getRoot()).getChildAt(i)).getUserObject()).getDs();
			if (curDataset.getName().equals(document.getDataSet().getName())) {
				parentNode = (DefaultMutableTreeNode)((DefaultMutableTreeNode)getRoot()).getChildAt(i);

				for (int j=0; j<parentNode.getChildCount(); j++) {
					Document childDoc = (Document)((DefaultMutableTreeNode)(parentNode.getChildAt(j))).getUserObject();
					if (childDoc.getName().equals(document.getName())){
						removeNodeFromParent(((DefaultMutableTreeNode)(parentNode.getChildAt(j))));
						insertNodeInto(new DefaultMutableTreeNode(document), parentNode, j);
						if (!(document instanceof DummyDocument)) {
							for (int z=0; z<document.numEntries(); z++) {
								if (!document.getEntry(z).isLocked()){
									addEntry(document.getEntry(z), document);
								}
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Refresh the document in the tree
	 * @param document the IDocument to refresh
	 */
	public void refreshDataset(StudyDataSet dsSet) {
		DefaultMutableTreeNode parentNode = new DefaultMutableTreeNode();
		for (int i=0; i<((DefaultMutableTreeNode)getRoot()).getChildCount(); i++) {
			DataSet curDataset = ((StudyDataSet)((DefaultMutableTreeNode)((DefaultMutableTreeNode)getRoot()).getChildAt(i)).getUserObject()).getDs();
			if (curDataset.getName().equals(dsSet.getDs().getName())) {
				parentNode = (DefaultMutableTreeNode)((DefaultMutableTreeNode)getRoot()).getChildAt(i);
				removeNodeFromParent(parentNode);
				addDataset(dsSet);
			}
		}
	}
	
	

	/**
	 * Remove the dataset from this tree model
	 * @param dataset the dataset to remove
	 */
	public void removeDataset(DataSet dataset) {
		//remove from tree
		for (int i=0; i<((DefaultMutableTreeNode)getRoot()).getChildCount(); i++) {
			DataSet curDataset = ((StudyDataSet)((DefaultMutableTreeNode)((DefaultMutableTreeNode)getRoot()).getChildAt(i)).getUserObject()).getDs();
			if (curDataset.getName().equals(dataset.getName())) {
				removeNodeFromParent((DefaultMutableTreeNode)((DefaultMutableTreeNode)getRoot()).getChildAt(i));
				DatasetController.getInstance().setActiveDs(null);
			}
		}
	}
	
	/**
	 * Remove the dataset from this tree model
	 * @param dataset the dataset to remove
	 */
	public void removeDSDataset(StudyDataSet dataset) {
		//remove from tree
		for (int i=0; i<((DefaultMutableTreeNode)getRoot()).getChildCount(); i++) {
			StudyDataSet curDataset = ((StudyDataSet)((DefaultMutableTreeNode)((DefaultMutableTreeNode)getRoot()).getChildAt(i)).getUserObject());
			if (dataset.equals(curDataset)) {
				//set active ds to null first so that this is known to listeners of
				//structural changes to the tree
				DatasetController.getInstance().setActiveDs(null);
				removeNodeFromParent((DefaultMutableTreeNode)((DefaultMutableTreeNode)getRoot()).getChildAt(i));
			}
		}
	}

	/**
	 * Add the IDocument
	 * @param document
	 */
	public void addDocument(Document document) {
		DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(document);
		DefaultMutableTreeNode parentNode = new DefaultMutableTreeNode();
		for (int i=0; i<((DefaultMutableTreeNode)getRoot()).getChildCount(); i++) {
			DataSet curDataset = ((StudyDataSet)((DefaultMutableTreeNode)((DefaultMutableTreeNode)getRoot()).getChildAt(i)).getUserObject()).getDs();
			if (curDataset.getName().equals(document.getDataSet().getName())) {
				parentNode = (DefaultMutableTreeNode)((DefaultMutableTreeNode)getRoot()).getChildAt(i);
			}
		}
		insertNodeInto(childNode, parentNode, parentNode.getChildCount());
	}

	/**
	 * Add imported document
	 * @param document Add imported document to the 
	 */
	public void addImportedDocument(Document document) {
		DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(document);
		DefaultMutableTreeNode parentNode = new DefaultMutableTreeNode();

		for (int i=0; i<((DefaultMutableTreeNode)getRoot()).getChildCount(); i++) {
			DataSet curDataset = ((StudyDataSet)((DefaultMutableTreeNode)((DefaultMutableTreeNode)getRoot()).getChildAt(i)).getUserObject()).getDs();
			if (curDataset.getName().equals(document.getDataSet().getName())) {
				parentNode = (DefaultMutableTreeNode)((DefaultMutableTreeNode)getRoot()).getChildAt(i);
			}
		}

		insertNodeInto(childNode, parentNode, parentNode.getChildCount());
		if (!(document instanceof DummyDocument)) {
			//Now do the individual entries.
			int entryCount = document.numEntries();
			for(int i = 0; i < entryCount; i++){
				if (!document.getEntry(i).isLocked()) {
					addEntry(document.getEntry(i), document);
				}
			}
		}
	}

	public void addEntry(Entry entry, Document parentDoc) {
		if (!(parentDoc instanceof DummyDocument)) {	//Entries are not displayed for 'dummy' documents
			DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(entry);
			DefaultMutableTreeNode grandParentNode = new DefaultMutableTreeNode();
			DefaultMutableTreeNode parentNode = new DefaultMutableTreeNode();

			int index = 0;

			
			for (int i=0; i<((DefaultMutableTreeNode)getRoot()).getChildCount(); i++) {
				DataSet curDataset = ((StudyDataSet)((DefaultMutableTreeNode)((DefaultMutableTreeNode)getRoot()).getChildAt(i)).getUserObject()).getDs();
				if (curDataset.getName().equals(entry.getDataSet().getName())) {
					grandParentNode = (DefaultMutableTreeNode)((DefaultMutableTreeNode)getRoot()).getChildAt(i);
					
					for (int j=0; j<grandParentNode.getChildCount(); j++) {
						Document document = (Document)((DefaultMutableTreeNode)grandParentNode.getChildAt(j)).getUserObject();
						
						if (document.getName().equals(parentDoc.getName())) {
							int lockedEntryOffset = 0;
							parentNode = (DefaultMutableTreeNode)grandParentNode.getChildAt(j);
							for (int z=0; z<document.numEntries(); z++) {
								Entry childEntry = document.getEntry(z);
								if (childEntry.isLocked()) {
									lockedEntryOffset++;
								}
								if (childEntry.getName().equals(entry.getName())){
									index = z - lockedEntryOffset;
									break;
								}
							}
						}
					}
				}
			}
			insertNodeInto(childNode, parentNode, index);
		}

	}

	/**
	 * Entry is found by name, and is replaced.
	 * @param entry
	 * @param parentDoc
	 * @return
	 */
	public boolean updateEntry(Entry entry, Document parentDoc) {

		DefaultMutableTreeNode grandParentNode = new DefaultMutableTreeNode();
		DefaultMutableTreeNode parentNode = new DefaultMutableTreeNode();

		for (int i=0; i<((DefaultMutableTreeNode)getRoot()).getChildCount(); i++) {
			DataSet curDataset = ((StudyDataSet)((DefaultMutableTreeNode)((DefaultMutableTreeNode)getRoot()).getChildAt(i)).getUserObject()).getDs();
			if (curDataset.getName().equals(entry.getDataSet().getName())) {
				grandParentNode = (DefaultMutableTreeNode)((DefaultMutableTreeNode)getRoot()).getChildAt(i);
				for (int j=0; j<curDataset.numDocuments(); j++) {
					if (curDataset.getDocument(j).getName().equals(parentDoc.getName())) {
						parentNode = (DefaultMutableTreeNode)grandParentNode.getChildAt(j);
						for (int z=0; z<parentNode.getChildCount(); z++) {
							Entry childEntry = (Entry)((DefaultMutableTreeNode)parentNode.getChildAt(z)).getUserObject();
							if (childEntry.getName().equals(entry.getName())){
								((DefaultMutableTreeNode)parentNode.getChildAt(z)).setUserObject(entry);
								return true;
							}
						}
					}
				}
			}
		}

		return false;
	}

	public void deleteEntry(Entry entry, Document parentDoc) {
		DefaultMutableTreeNode grandParentNode = new DefaultMutableTreeNode();
		DefaultMutableTreeNode parentNode = new DefaultMutableTreeNode();

		for (int i=0; i<((DefaultMutableTreeNode)getRoot()).getChildCount(); i++) {
			DataSet curDataset = ((StudyDataSet)((DefaultMutableTreeNode)((DefaultMutableTreeNode)getRoot()).getChildAt(i)).getUserObject()).getDs();
			if (curDataset.getName().equals(entry.getDataSet().getName())) {
				grandParentNode = (DefaultMutableTreeNode)((DefaultMutableTreeNode)getRoot()).getChildAt(i);
				for (int j=0; j<curDataset.numDocuments(); j++) {
					if (curDataset.getDocument(j).getName().equals(parentDoc.getName())) {
						parentNode = (DefaultMutableTreeNode)grandParentNode.getChildAt(j);
						for (int z=0; z<parentNode.getChildCount(); z++) {
							Entry childEntry = (Entry)((DefaultMutableTreeNode)parentNode.getChildAt(z)).getUserObject();
							if (childEntry.getName().equals(entry.getName())){
								removeNodeFromParent((DefaultMutableTreeNode)parentNode.getChildAt(z));
								return;
							}
						}
					}
				}
			}
		}
	}






















	public void importIDocument(Document doc, DataSet ds){
		ds.addDocument(doc);
		addImportedDocument(doc);
	}

	public Entry copyEntry(Document document, Entry entry, boolean usePrefix) {

		String prefix = "";

		if (usePrefix) {
			boolean nameNotExist = true;
			test:
				while(nameNotExist) {
					for (int i=0; i<document.numEntries(); i++) {
						if (document.getEntry(i).getName().equals(prefix+entry.getName())) {
							prefix = new String(prefix + "Copy of ");
							continue test;
						} 
					}
					nameNotExist = false;
				}
		}

		Entry copiedEntry = null;

		if (entry instanceof TextEntry) {
			copiedEntry = ElementUtility.createTextEntry(prefix + entry.getName());
		} else if (entry instanceof LongTextEntry) {
			copiedEntry = ElementUtility.createLongTextEntry(prefix + entry.getName());
		} else if (entry instanceof OptionEntry) {
			copiedEntry = ElementUtility.createOptionEntry(prefix + entry.getName());
			for (int i=0; i<((OptionEntry)entry).numOptions(); i++) {
				Option curOption = ((OptionEntry)entry).getOption(i);
				Option option = ElementUtility.createOption(curOption.getDisplayText());
				option.setName(curOption.getName());
				option.setCode(curOption.getCode());
				option.setDescription(curOption.getDescription());
				((OptionEntry)copiedEntry).addOption(option);
			}
		} else if (entry instanceof NumericEntry) {
			copiedEntry = ElementUtility.createNumericEntry(prefix + entry.getName());
		} else if (entry instanceof BooleanEntry) {
			copiedEntry = ElementUtility.createBooleanEntry(prefix + entry.getName());
		} else if (entry instanceof DerivedEntry) {
			copiedEntry = ElementUtility.createDerivedEntry(prefix + entry.getName());
			ArrayList<String> varNames = new ArrayList<String>(((DerivedEntry)entry).getVariableNames());

			for (int i=0; i<((DerivedEntry)entry).getVariableNames().size(); i++) {
				String varName = varNames.get(i);
				((DerivedEntry)copiedEntry).addVariable(varName, ((DerivedEntry)entry).getVariable(varName));
			}

			((DerivedEntry)copiedEntry).setFormula(((DerivedEntry)entry).getFormula());
		} else if (entry instanceof DateEntry) {
			copiedEntry = ElementUtility.createDateEntry(prefix + entry.getName());
		} else if (entry instanceof IntegerEntry) {
			copiedEntry = ElementUtility.createIntegerEntry(prefix + entry.getName());
		} else if (entry instanceof NarrativeEntry) {
			copiedEntry = ElementUtility.createNarrativeEntry(prefix + entry.getName());
		} else if (entry instanceof CompositeEntry) {
			copiedEntry = ElementUtility.createComposite(prefix + entry.getName());
			copiedEntry.setSection(entry.getSection());

			if (((CompositeEntry)entry).numRowLabels() >0 ) {
				TextEntry text = ElementUtility.createTextEntry("Fixed Label", ((CompositeEntry) entry).getEntry(0).getDisplayText());
				text.setSection(entry.getSection());
				((CompositeEntry)copiedEntry).insertEntry(text, 0);
			}

			for (int i=0; i<((CompositeEntry)entry).numRowLabels(); i++) {
				((CompositeEntry)copiedEntry).addRowLabel(new String(((CompositeEntry)entry).getRowLabel(i)));
			}

			for (int j=0; j<((CompositeEntry)entry).numEntries(); j++) {
				if (!(((CompositeEntry)entry).getEntry(j)).getName().equals("Fixed Label")) {
					//clone the composite entry
					Entry copiedBasic = copyEntry(document, ((CompositeEntry) entry).getEntry(j), false);
					copiedBasic.setSection(entry.getSection());
					((CompositeEntry)copiedEntry).addEntry((BasicEntry)copiedBasic);
				}
			}
		} else if (entry instanceof ExternalDerivedEntry) {
			copiedEntry = ElementUtility.createExternalDerivedEntry(prefix + entry.getName());
//			((IExternalDerivedEntry)copiedEntry).addOutputTransformer(((IExternalDerivedEntry)entry).getExternalTransformer());
		}

		copiedEntry.setDisplayText(prefix + entry.getDisplayText());
		copiedEntry.setDescription(entry.getDescription());
		copiedEntry.setEntryStatus(entry.getEntryStatus());
		copiedEntry.setExportSecurity(entry.getExportSecurity());
		copiedEntry.setLabel(entry.getLabel());
		copiedEntry.setSection(entry.getSection());

		((Entry)entry).setIsEditable(((Entry)entry).getIsEditable());

		//copy units, transformers and validation rules
		if (entry instanceof BasicEntry) {
			copyAttributes((BasicEntry)entry, (BasicEntry)copiedEntry);
		}

		//copy the option dependencies
		for (int i=0; i<document.numEntries(); i++) {
			if (document.getEntry(i) instanceof OptionEntry) {
				OptionEntry optEntry = (OptionEntry)document.getEntry(i);
				for (int j=0; j<optEntry.numOptions(); j++) {
					Option option = optEntry.getOption(j);
					for (int z=0; z<option.numOptionDependents(); z++) {
						OptionDependent dependent = option.getOptionDependent(z);
						if (dependent.getDependentEntry().equals(entry)) {
							OptionDependent optDep = ElementUtility.createOptionDependent();
							optDep.setEntryStatus(dependent.getEntryStatus());
							option.addOptionDependent(optDep);
							optDep.setDependentEntry(copiedEntry);
						}
					}
				}
			}
		}

		return copiedEntry;
	}

	public void copyAttributes(BasicEntry entry, BasicEntry copiedEntry) {
		for (int i=0; i<entry.numUnits(); i++) {
			copiedEntry.addUnit(entry.getUnit(i));
		}
		for (int i=0; i<entry.numTransformers(); i++) {
			copiedEntry.addTransformer(entry.getTransformer(i));
		}
		for (int i=0; i<entry.numValidationRules(); i++) {
			copiedEntry.addValidationRule(entry.getValidationRule(i));
		}
		
		copiedEntry.setDisableStandardCodes(entry.isDisableStandardCodes());
	}


//	public void saveESLModelForDataset(ESLEmailModel eslModel, String datasetName) {
//		getDSDataset(datasetName).setEslModel(eslModel);
//	}
//
//	public void saveRandomisationHolderModelForDataset(RandomisationHolderModel randomHolderModel, String datasetName) {
//		getDSDataset(datasetName).setRandomHolderModel(randomHolderModel);
//	}
//
//	public void saveRandomisationHolderModelForDataset(RandomisationHolderModel randomHolderModel, DSDataSet dsSet) {
//		dsSet.setRandomHolderModel(randomHolderModel);
//	}

//	public RandomisationHolderModel getRandomisationHolderModelForDataset(String datasetName) {
//		DSDataSet dsSet = getDSDataset(datasetName);
//		return dsSet.getRandomHolderModel();
//	}





	/**
	 * Retrieve a list of LSIDs that are held locally and have been checked out
	 * from the Data Element Library.
	 * 
	 * @param ignoreReadOnly ignore those elements that have been opened read only
	 * @param topLevelOnly get top level elements only
	 * 
	 * @return lsids
	 */
	public List<ElementStatusContainer> getCheckedOutLSIDs(boolean ignoreReadOnly, boolean topLevelOnly) {
		ArrayList<DataSet> datasets = DocTreeModel.getInstance().getAllDatasets();

		List<ElementStatusContainer> checkedOut = new ArrayList<ElementStatusContainer>();
		for (DataSet dataset: datasets) {
			for (Document document: ((DataSet)dataset).getDocuments()) {
				Document doc = (Document)document;
				if ((doc.getIsEditable() || doc instanceof DummyDocument) 
						|| ignoreReadOnly) {
					DataElementStatus status = doc.getStatus();
					if (DataElementStatus.APPROVED.equals(status)) {
						checkedOut.add(new ElementStatusContainer(doc.getLSID(), doc.getHeadRevision(), doc.getStatus()));
					}
					else if (DataElementStatus.PENDING.equals(status)) {
						checkedOut.add(new ElementStatusContainer(doc.getLSID(), doc.getHeadRevision(), doc.getStatus()));
					}

					if (topLevelOnly) {
						if (doc instanceof DummyDocument) {
							Entry singleEntry = doc.getEntry(0);
							for (Object entry: doc.getEntries()) {
								if (entry instanceof ExternalDerivedEntry) {
									singleEntry = (Entry)entry;
								}
								else if (entry instanceof DerivedEntry) {
									singleEntry = (Entry)entry;
								}
								else if (entry instanceof OptionEntry) {
									singleEntry = (Entry)entry;
								}
							}
							if (singleEntry.getIsEditable() || ignoreReadOnly) {
								DataElementStatus entryStatus = singleEntry.getStatus();
								if (DataElementStatus.APPROVED.equals(entryStatus)) {
									checkedOut.add(new ElementStatusContainer(singleEntry.getLSID(), singleEntry.getHeadRevision(), singleEntry.getStatus()));
								}
								else if (DataElementStatus.PENDING.equals(entryStatus)) {
									checkedOut.add(new ElementStatusContainer(singleEntry.getLSID(), singleEntry.getHeadRevision(), singleEntry.getStatus()));
								}
							}
						}
					}
					else {
						for (Entry entry: doc.getEntries()) {
							if (entry.getIsEditable() || ignoreReadOnly) {
								DataElementStatus entryStatus = entry.getStatus();
								if (DataElementStatus.APPROVED.equals(entryStatus)) {
									checkedOut.add(new ElementStatusContainer(entry.getLSID(), entry.getHeadRevision(), entry.getStatus()));
								}
								else if (DataElementStatus.PENDING.equals(entryStatus)) {
									checkedOut.add(new ElementStatusContainer(entry.getLSID(), entry.getHeadRevision(), entry.getStatus()));
								}

								if (entry instanceof CompositeEntry) {
									for (BasicEntry be: ((CompositeEntry)entry).getEntries()) {
										if (be.getIsEditable() || ignoreReadOnly) {
											DataElementStatus beStatus = be.getStatus();
											if (DataElementStatus.APPROVED.equals(beStatus)) {
												checkedOut.add(new ElementStatusContainer(be.getLSID(), be.getHeadRevision(), be.getStatus()));
											}
											else if (DataElementStatus.PENDING.equals(beStatus)) {
												checkedOut.add(new ElementStatusContainer(be.getLSID(), be.getHeadRevision(), be.getStatus()));
											}
										}
									}
								}
							}
						}
					}
				}
			}

			//Check validation rules
			for (ValidationRule rule: ((DataSet)dataset).getValidationRules()) {
				if ((rule != null && rule.getIsEditable()) || ignoreReadOnly) {
					DataElementStatus ruleStatus = rule.getStatus();
					if (DataElementStatus.APPROVED.equals(ruleStatus)) {
						checkedOut.add(new ElementStatusContainer(rule.getLSID(), rule.getHeadRevision(), rule.getStatus()));
					}
					else if (DataElementStatus.PENDING.equals(ruleStatus)) {
						checkedOut.add(new ElementStatusContainer(rule.getLSID(), rule.getHeadRevision(), rule.getStatus()));
					}
				}
			}
		}
		return checkedOut;
	}

	/**
	 * Add an action listener - used for listening to model events
	 * @param listener the listener to add to the list
	 */
	public void addActionListener(ActionListener listener) {
		listeners.add(listener);
	}

	/**
	 * Remove an action listener
	 * @param listener the listener to add to the list
	 */
	public void removeActionListener(ActionListener listener) {
		listeners.remove(listener);
	}

	public void fireTreeModelChanged(Document document) {
		fireTreeNodesChanged(this, getPathToRoot(getDatasetNode(document)), null, null);
	}
	
	public void fireTreeModelChanged(StudyDataSet dsSet) {
		for (int i=0; i<((DefaultMutableTreeNode)getRoot()).getChildCount(); i++) {
			StudyDataSet curDS = (StudyDataSet)((DefaultMutableTreeNode)((DefaultMutableTreeNode)getRoot()).getChildAt(i)).getUserObject();
			if (curDS.getDs().getName().equals(dsSet.getDs().getName())) {
				//found the node, fire the event
				fireTreeNodesChanged(this, getPathToRoot(((DefaultMutableTreeNode)getRoot()).getChildAt(i)), null, null);
				return;
			}
		}
	}
	
	
}

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

package org.psygrid.datasetdesigner.ui;

import java.awt.Color;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.data.model.hibernate.DataElementStatus;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.datasetdesigner.actions.ApproveElementAction;
import org.psygrid.datasetdesigner.actions.CloseDocumentAction;
import org.psygrid.datasetdesigner.actions.ConfigureDatasetPropertiesAction;
import org.psygrid.datasetdesigner.actions.ConfigureDocumentPropertiesAction;
import org.psygrid.datasetdesigner.actions.ConfigureImportMappingAction;
import org.psygrid.datasetdesigner.actions.DeleteDocumentAction;
import org.psygrid.datasetdesigner.actions.EditEntryAction;
import org.psygrid.datasetdesigner.actions.ImportDELDocumentAction;
import org.psygrid.datasetdesigner.actions.NewDocumentAction;
import org.psygrid.datasetdesigner.actions.SearchDELEntriesAction;
import org.psygrid.datasetdesigner.actions.ShowAuditLogAction;
import org.psygrid.datasetdesigner.actions.SubmitToElemLibraryAction;
import org.psygrid.datasetdesigner.actions.UpdateLibraryElementsAction;
import org.psygrid.datasetdesigner.controllers.DatasetController;
import org.psygrid.datasetdesigner.model.DELStudySet;
import org.psygrid.datasetdesigner.model.DocTreeModel;
import org.psygrid.datasetdesigner.model.DummyDocument;
import org.psygrid.datasetdesigner.model.StudyDataSet;
import org.psygrid.datasetdesigner.renderer.MainTreeCellRenderer;
import org.psygrid.datasetdesigner.ui.dataelementfacilities.DELSecurity;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.Utils;


/**
 * Main Tree of the application that contains datasets, documents and entries
 * @author pwhelan
 */
public class MainTree extends JTree implements TreeModelListener {

	/**
	 * The logger for this class
	 */
	private static final Log LOG = LogFactory.getLog(MainTree.class);

	/**
	 * The document panel used in the main window
	 */
	private MainTabbedPane docPane;

	/**
	 * The treepath currently being transferred.
	 */
	private TreePath latestPath;

	/**
	 * The supported DataFlavor of this class
	 */
	public static DataFlavor TREE_PATH_FLAVOR = new DataFlavor(TreePath.class,
	"Tree Path");

	/**
	 * Specifies whether the document is part of the data element library view.
	 */
	private boolean isDEL;

	/**
	 * Constructor - initialise with dummy root node and add required listeners
	 * @param docPane the tabbed pane that contains documents
	 */
	public MainTree(MainTabbedPane docPane) {
		super(new DefaultMutableTreeNode("Dataset"));
		this.docPane = docPane;
		setModel(DocTreeModel.getInstance());
		getModel().addTreeModelListener(this);
		setCellRenderer(new MainTreeCellRenderer(getCellRenderer()));
		addMouseListener(new DoubleClickAdapter());
		addMouseListener(new RightClickAdapter());
		addMouseListener(new LeftClickAdapter());
		setRootVisible(false);
		setDragEnabled(true);
		setBackground(Color.white);

		// If we only support move operations...
		new TreeDragSource(this, DnDConstants.ACTION_COPY_OR_MOVE);
		new TreeDropTarget(this);

		isDEL = false;
		if (DatasetController.getInstance().getActiveDs() != null 
				&& DatasetController.getInstance().getActiveDs() instanceof DELStudySet) {
			isDEL = true;
		}
		ToolTipManager.sharedInstance().registerComponent(this);
	}

	/**
	 * Event occurs that a tree has changed so validate and repaint the UI
	 * @param e the calling TreeModelEvent 
	 */
	public void treeNodesChanged(TreeModelEvent e) {
		validate();
		repaint();
	}

	/**
	 * Triggered when new document inserted; select the new document.
	 * @param e the calling TreeModelEvent
	 */
	public void treeNodesInserted(TreeModelEvent e) {
		validate();
		repaint();
		final TreePath epath = e.getTreePath();
		final TreeModelEvent event = e;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				expandPath(epath);
				Object[] childObject = new Object[epath.getPath().length + 1]; 

				for (int i=0; i<epath.getPath().length; i++) {
					childObject[i] = epath.getPath()[i];
				}
				childObject[epath.getPath().length] = event.getChildIndices();
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)event.getPath()[event.getPath().length-1];
				childObject[epath.getPath().length] = (node.getChildAt(event.getChildIndices()[0]));
				setSelectionPath(new TreePath(childObject));
				setSelectionPath(epath);
			}
		});
	}

	/**
	 * Event occurs that a tree has changed so validate and repaint the UI
	 * @param e the calling TreeModelEvent
	 */
	public void treeNodesRemoved(TreeModelEvent e) {
		validate();
		repaint();
	}

	/**
	 * Event occurs that a tree has changed so validate and repaint the UI
	 * @param e the calling TreeModelEvent
	 */
	public void treeStructureChanged(TreeModelEvent e) {
		validate();
		repaint();
	}

	/**
	 * Listens for double-clicks on the tree
	 * A double click on a document opens the document in the document panel
	 * @author pwhelan
	 */
	private class DoubleClickAdapter extends MouseAdapter {

		/**
		 * On a double-click on document, open the document in the DocumentPanel
		 */
		public void mouseClicked(MouseEvent e) {
			if (SwingUtilities.isLeftMouseButton(e)) {
				//should open in read-only mode too
				if (DatasetController.getInstance().getActiveDs() != null ) {
					if (e.getClickCount() == 2) {
						int x = e.getX();
						int y = e.getY();

						//show to user which is selected
						int row = getRowForLocation(x, y);
						setSelectionRow(row);

						try
						{
							Object[] path = getPathForLocation(e.getX(), e.getY()).getPath();
							DefaultMutableTreeNode node = (DefaultMutableTreeNode)path[path.length-1];
							if (node.getUserObject() instanceof Document) {
								DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)node.getParent();
								//Specify whether the document is a part of the DEL view
								setIsDEL(parentNode.getUserObject() instanceof DELStudySet);
								//dataset name and document name should compose name in tab
								docPane.openTab(((Document)node.getUserObject()));
							} else if (node.getUserObject() instanceof Entry) {
								//do not show dialog if ds is read-only
								if (!DatasetController.getInstance().getActiveDs().isReadOnly()) {
									Entry entry = (Entry)node.getUserObject();
									DefaultMutableTreeNode grandParentNode = (DefaultMutableTreeNode)node.getParent().getParent();
									//Specify whether the document is a part of the DEL view
									setIsDEL(grandParentNode.getUserObject() instanceof DELStudySet);
									Document parentDoc = (Document)((DefaultMutableTreeNode)node.getParent()).getUserObject();
									docPane.openTab(parentDoc);
									DocumentPanel docPanel = (DocumentPanel)docPane.getSelectedComponent();
									docPanel.refresh(Utils.getSectionForEntry(parentDoc, entry));
									if (isDEL) {
										boolean isEditable = ((Entry)node.getUserObject()).getIsEditable();
										Utils.showEntryDialog(entry, isDEL, isEditable, (DocumentPanel)docPane.getSelectedComponent(), (MainFrame)docPane.getFrame());
									} else {
										//always allow editing outside of the DEL view
										Utils.showEntryDialog(entry, isDEL, true, (DocumentPanel)docPane.getSelectedComponent(), (MainFrame)docPane.getFrame());
									}
								}
							}
						} catch (NullPointerException nex) {
							//Swing bug, path not always locatable; ignore
						}
					}
				}
			}
		}
	}

	private class LeftClickAdapter extends MouseAdapter {

		public void mouseClicked(MouseEvent e) {
			if (SwingUtilities.isLeftMouseButton(e)) {
				TreePath treePath = getPathForLocation(e.getX(), e.getY());
				setSelectionPath(treePath);

				//Update the main menu bar depending on whether the DEL view is in use..
				if (treePath != null) {
					Object[] path = treePath.getPath();
					DefaultMutableTreeNode node = (DefaultMutableTreeNode)path[path.length-1];

					if (node.getUserObject() instanceof Document) {
						DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)node.getParent();
						//Specify whether the document is a part of the DEL view
						setIsDEL(parentNode.getUserObject() instanceof DELStudySet);
					}
					else if (node.getUserObject() instanceof Entry) {
						Object grandparent = ((DefaultMutableTreeNode)node.getParent().getParent()).getUserObject();
						setIsDEL(grandparent instanceof DELStudySet);
					}
					else {
						//Specify that the DEL view is to be used
						setIsDEL(node.getUserObject() instanceof DELStudySet);
					}
				}
			}
		}
	}

	/**
	 * Listens for right-clicks on the tree and produces the appropriate menu
	 * @author pwhelan
	 *
	 */
	private class RightClickAdapter extends MouseAdapter {

		/**
		 * On a Right-Click produce the appropriate menu
		 * @param e the calling MouseEvent
		 */
		public void mouseClicked(MouseEvent e) {
			if (SwingUtilities.isRightMouseButton(e)) {
				TreePath treePath = getPathForLocation(e.getX(), e.getY());
				setSelectionPath(treePath);

				if (treePath != null) {
					Object[] path = treePath.getPath();

					DefaultMutableTreeNode node = (DefaultMutableTreeNode)path[path.length-1];
					MainFrame frame = (MainFrame)docPane.getFrame();

					if (node.getUserObject() instanceof DummyDocument) {
						JPopupMenu documentMenu = new JPopupMenu();

						Entry singleEntry = Utils.getMainEntry((DummyDocument)node.getUserObject());

						String authority = null;
						if (singleEntry.getLSID() != null) {
							authority = singleEntry.getLSID().getAuthorityId();
						}

						if (isDEL && !DELSecurity.getInstance().canEditElements(authority)) {
							documentMenu.add(new JMenuItem(new CloseDocumentAction(frame, (DummyDocument)node.getUserObject())));
						}
						else {
							documentMenu.add(new JMenuItem(new DeleteDocumentAction(frame, (DummyDocument)node.getUserObject(), PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.tree.deleteentrylocally"))));	
						}

						documentMenu.addSeparator();
						//Only authors can edit document properties in the DEL view
						JMenuItem editEntry = null;
						if (DELSecurity.getInstance().canEditElements(authority)) {
							editEntry = new JMenuItem(new EditEntryAction(frame, singleEntry, docPane.getPanelForDocument((Document)node.getUserObject()), isDEL, true));
							editEntry.setEnabled(false);
						}
						else {
							editEntry = new JMenuItem(new EditEntryAction(frame, singleEntry, docPane.getPanelForDocument((Document)node.getUserObject()), isDEL));
							editEntry.setEnabled(false);
						}
						if (editEntry != null) {
							if (DatasetController.getInstance().getActiveDocument().equals((DummyDocument)node.getUserObject())) {
								editEntry.setEnabled(true);
							}
							documentMenu.add(editEntry);
						}
						Document doc = (Document)node.getUserObject();
						boolean delClientInitialized = frame.getDocPane().getDelInitializer().isDelConnectionIsInitialised();

						boolean enableSubmitEntriesItem = false;
						if(Utils.docHasSubmittableEntries(doc) && delClientInitialized){
							enableSubmitEntriesItem = true;
						}

						if (DataElementStatus.PENDING.toString().equals(doc.getEnumStatus())
								&& DELSecurity.getInstance().canApproveElements(authority)) {
							documentMenu.addSeparator();
							JMenuItem approveElementItem = new JMenuItem(new ApproveElementAction(docPane, doc));
							approveElementItem.setEnabled(!enableSubmitEntriesItem);
							documentMenu.add(approveElementItem);
						}

						documentMenu.show((JComponent)e.getSource(), e.getX(), e.getY());
					}
					else if (node.getUserObject() instanceof Document) {
						JPopupMenu documentMenu = new JPopupMenu();
						String authority = null;
						if (((Document)node.getUserObject()).getLSID() != null) {
							authority = ((Document)node.getUserObject()).getLSID().getAuthorityId();
						}

						DefaultMutableTreeNode parent = (DefaultMutableTreeNode)node.getParent();

						if (isDEL && !DELSecurity.getInstance().canEditElements(authority)) {
							documentMenu.add(new JMenuItem(new CloseDocumentAction(frame, (Document)node.getUserObject())));
						}
						else {
							if (isDEL) {
								documentMenu.add(new JMenuItem(new DeleteDocumentAction(frame, (Document)node.getUserObject(), PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.tree.deletecrflocally"))));
							}
							else {
								//don't show 'new doc' in read-only mode
								if (parent.getUserObject() instanceof StudyDataSet && !((StudyDataSet)parent.getUserObject()).isReadOnly()) {
									documentMenu.add(new JMenuItem(new DeleteDocumentAction(frame, (Document)node.getUserObject())));
								}
							}
						}
						documentMenu.addSeparator();

						//Specify whether the document is a part of the DEL view
						setIsDEL(parent.getUserObject() instanceof DELStudySet);
						if (isDEL) {
						}
						else {
							if (DatasetController.getInstance().getActiveDs() != null
									&& !DatasetController.getInstance().getActiveDs().isReadOnly()) {
								documentMenu.add(new JMenuItem(new ConfigureImportMappingAction(frame, (Document)node.getUserObject())));
								documentMenu.addSeparator();
							}
//							documentMenu.add(new JMenuItem(new ConfigureDocumentOccurrencesAction(frame)));
						}

						//Only authors can edit document properties in the DEL view
						if (isDEL && !DELSecurity.getInstance().canEditElements(authority)) {
							documentMenu.add(new JMenuItem(new ConfigureDocumentPropertiesAction(docPane.getFrame(), (Document)node.getUserObject(), isDEL, false)));
						}
						else {
							documentMenu.add(new JMenuItem(new ConfigureDocumentPropertiesAction(docPane.getFrame(), (Document)node.getUserObject(), isDEL, true)));
						}

						boolean delClientInitialized = frame.getDocPane().getDelInitializer().isDelConnectionIsInitialised();
						boolean enableDELEntriesSearchItem = false;

						if(((Document)node.getUserObject()).getSections().size() != 0 && delClientInitialized
								&& DELSecurity.getInstance().canSearchLibrary(authority)){
							enableDELEntriesSearchItem = true;
						}

						Document doc = (Document)node.getUserObject();
						if (!((Document)node.getUserObject()).equals(DatasetController.getInstance().getActiveDocument())) {
							//Document must be open to be able to import an entry
							enableDELEntriesSearchItem = false;
						}

						//Check whether this document can be approved
						boolean enableApproveElement = false;
						if (isDEL) {
							boolean enableSubmitEntriesItem = false;
							if(Utils.docHasSubmittableEntries(doc) && delClientInitialized){
								enableSubmitEntriesItem = true;
							}
							if (DataElementStatus.PENDING.toString().equals(doc.getEnumStatus())
									&& DELSecurity.getInstance().canApproveElements(authority)
									&& !doc.getIsRevisionCandidate()) {
								//Document should not contain any edited elements (GUI should not allow this anyway)
								enableApproveElement = !enableSubmitEntriesItem;
							}
						}

						if (isDEL && !DELSecurity.getInstance().canEditElements(authority)) {
							//Curators/viewers can't add elements to a document from the DEL
						}
						else {
							if (parent.getUserObject() instanceof StudyDataSet && !((StudyDataSet)parent.getUserObject()).isReadOnly()) {
								documentMenu.addSeparator();
								JMenuItem searchDELEntriesItem = new JMenuItem(new SearchDELEntriesAction(docPane, (Document)node.getUserObject(), SearchDELEntriesAction.SearchType.Entries));						
								documentMenu.add(searchDELEntriesItem);					
								searchDELEntriesItem.setEnabled(enableDELEntriesSearchItem);

								if (!enableApproveElement) {
									boolean enableDELSubmitDoc = enableDELEntriesSearchItem;
									if (!isDEL && ((Document)node.getUserObject()).getLSID() != null) {
										//Currently only able to new Documents from the study view to the library
										enableDELSubmitDoc = false;
									}
									else if (isDEL && !((Document)node.getUserObject()).getIsRevisionCandidate()) {
										//Only enable the option in the DEL view if the document has been edited, otherwise it causes confusion with the save dialog
										enableDELSubmitDoc = false;
									}
									JMenuItem importDELEntriesItem = new JMenuItem(new ImportDELDocumentAction(docPane, (Document)node.getUserObject(), (StudyDataSet)parent.getUserObject()));						
									documentMenu.add(importDELEntriesItem);					
									importDELEntriesItem.setEnabled(enableDELSubmitDoc);
								}
							}
						}

						if (enableApproveElement) {
							JMenuItem approveElementItem = new JMenuItem(new ApproveElementAction(docPane, doc));
							approveElementItem.setEnabled(enableApproveElement);
							documentMenu.add(approveElementItem);
						}

						documentMenu.show((JComponent)e.getSource(), e.getX(), e.getY());
					}
					else if (node.getUserObject() instanceof DELStudySet) {
						//Specify that the DEL view is to be used
						setIsDEL(true);

						JPopupMenu datasetMenu = new JPopupMenu();
						if (DELSecurity.getInstance().canEditElements()) {
							datasetMenu.add(new JMenuItem(new NewDocumentAction(frame, true)));
							datasetMenu.addSeparator();
						}

						//Add DEL functions
						StudyDataSet ds = (StudyDataSet)node.getUserObject();

						boolean enableSearchItem = false;
						boolean enableSubmitItem = false;
						boolean delClientInitialized = frame.getDocPane().getDelInitializer().isDelConnectionIsInitialised();
						if(delClientInitialized){
							if (DELSecurity.getInstance().canSearchLibrary()) {
								enableSearchItem = true;	
								if(Utils.dataSetHasSubmittableDocuments((DataSet)ds.getDs())){
									enableSubmitItem = true;
								}
							}
						}

						JMenuItem searchItem = new JMenuItem(new SearchDELEntriesAction(docPane, ds, SearchDELEntriesAction.SearchType.Documents));
						datasetMenu.add(searchItem);
						searchItem.setEnabled(enableSearchItem);
						JMenuItem searchRulesItem = new JMenuItem(new SearchDELEntriesAction(docPane, ds, SearchDELEntriesAction.SearchType.ValidationRules));
						datasetMenu.add(searchRulesItem);
						searchRulesItem.setEnabled(enableSearchItem);


						if (DELSecurity.getInstance().canApproveElements()) {
							datasetMenu.addSeparator();
							JMenuItem approveElementItem = new JMenuItem(new ApproveElementAction(docPane, ds.getDs()));
							approveElementItem.setEnabled(enableSearchItem && Utils.hasApprovableDocuments());
							datasetMenu.add(approveElementItem);
						}
						if (DELSecurity.getInstance().canSubmitElements()) {
							JMenuItem submitItem = new JMenuItem(new SubmitToElemLibraryAction(docPane, ds.getDs()));
							datasetMenu.add(submitItem);
							submitItem.setEnabled(enableSubmitItem);
						}

						datasetMenu.show((JComponent)e.getSource(), e.getX(), e.getY());
					}
					else if (node.getUserObject() instanceof StudyDataSet) {
						//Specify that the DEL view is NOT to be used
						setIsDEL(false);

						StudyDataSet dSet = ((StudyDataSet)node.getUserObject());
						boolean isPublished = ((StudyDataSet)node.getUserObject()).getDs().isPublished();

						JPopupMenu datasetMenu = new JPopupMenu();

						if (!dSet.isReadOnly()) {
							datasetMenu.add(new JMenuItem(new NewDocumentAction(frame, false)));
						}
						datasetMenu.addSeparator();
						datasetMenu.add(new JMenuItem(new ConfigureDatasetPropertiesAction(frame)));
						DataSet ds = (DataSet)((StudyDataSet)node.getUserObject()).getDs();
						datasetMenu.add(new JMenuItem(new ShowAuditLogAction(frame, ds)));
						datasetMenu.addSeparator();


						boolean enableSearchItem = false;
						boolean delClientInitialized = frame.getDocPane().getDelInitializer().isDelConnectionIsInitialised();
						if(delClientInitialized){
							if (DELSecurity.getInstance().canSearchLibrary()) {
								enableSearchItem = true;
							}
						}

						if (!dSet.isReadOnly()) {
							JMenuItem searchItem = new JMenuItem(new SearchDELEntriesAction(docPane, dSet, SearchDELEntriesAction.SearchType.Documents));
							datasetMenu.add(searchItem);
							searchItem.setEnabled(enableSearchItem);
							JMenuItem searchRulesItem = new JMenuItem(new SearchDELEntriesAction(docPane, dSet, SearchDELEntriesAction.SearchType.ValidationRules));
							datasetMenu.add(searchRulesItem);
							searchRulesItem.setEnabled(enableSearchItem);						
							JMenuItem updateItem = new JMenuItem(new UpdateLibraryElementsAction(frame.getDocPane()));
							datasetMenu.add(updateItem);
							boolean enableUpdateItem = false;
							if (enableSearchItem && DocTreeModel.getInstance().getCheckedOutLSIDs(false, false).size() > 0) {
								enableUpdateItem = true;
							}
							updateItem.setEnabled(enableUpdateItem);
						}

						datasetMenu.show((JComponent)e.getSource(), e.getX(), e.getY());
					}
					if (node.getUserObject() instanceof Entry) {
						Object grandparent = ((DefaultMutableTreeNode)node.getParent().getParent()).getUserObject();
						if (grandparent instanceof DELStudySet) {
							setIsDEL(true);
						}
						else {
							setIsDEL(false);
						}
						if (((DocumentPanel)docPane.getSelectedComponent()) != null && 
								((DocumentPanel)docPane.getSelectedComponent()).getDocument().equals(((DefaultMutableTreeNode)node.getParent()).getUserObject())) {
							JPopupMenu entryMenu = new JPopupMenu();
							if (isDEL) {
								String authority = null; 
								Document doc = (Document)((DocumentPanel)docPane.getSelectedComponent()).getDocument();
								if (doc.getLSID() != null) {
									authority = doc.getLSID().getAuthorityId();
								}

								boolean isEditable = ((Entry)node.getUserObject()).getIsEditable();
								if (isEditable && DELSecurity.getInstance().canEditElements(authority)) {
									entryMenu.add(new JMenuItem(new EditEntryAction(frame, ((Entry)node.getUserObject()), (DocumentPanel)docPane.getSelectedComponent(), isDEL, true)));
								}
								else {
									entryMenu.add(new JMenuItem(new EditEntryAction(frame, ((Entry)node.getUserObject()), (DocumentPanel)docPane.getSelectedComponent(), isDEL)));
								}
							}
							else {


								//only editing when not in read only mode
								if (DatasetController.getInstance().getActiveDs() != null 
										&& !DatasetController.getInstance().getActiveDs().isReadOnly()) {
									entryMenu.add(new JMenuItem(new EditEntryAction(frame, ((Entry)node.getUserObject()), (DocumentPanel)docPane.getSelectedComponent(), isDEL, true)));
								}
							}

							//if there is something to show; then show it
							if (entryMenu.getComponentCount() > 0) {
								entryMenu.show((JComponent)e.getSource(), e.getX(), e.getY());
							}
						}


					}
				}
			}
		}
	}

	/**
	 * Get the currently selected path in the tree
	 * @return the name of the current selected dataset
	 */
	public String getSelectedDataSet() {
		TreePath treepath = getSelectionPath();

		try {
			Object[] path = treepath.getPath();
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)path[path.length-1];
			if (node.getUserObject() instanceof StudyDataSet) {
				return ((StudyDataSet)node.getUserObject()).getDs().getName();
			} else if (node.getUserObject() instanceof Document) {
				return ((StudyDataSet)((DefaultMutableTreeNode)node.getParent()).getUserObject()).getDs().getName();
			} else if (node.getUserObject() instanceof Entry) {
				return ((StudyDataSet)((DefaultMutableTreeNode)node.getParent().getParent()).getUserObject()).getDs().getName();
			}
		} catch (NullPointerException nex) {
			//path can't be found
		}

		return null;
	}

	/**
	 * Get whether the current element is part of the data element library view.
	 * 
	 * @return boolean
	 */
	protected boolean isDEL() {
		return isDEL;
	}

	public void setIsDEL(boolean isDEL) {
		MainFrame main = (MainFrame)docPane.getFrame();
		MainMenuBar menu = main.getMainMenuBar();
		if (menu != null) {
			menu.setDelContext(isDEL);
		}
		this.isDEL = isDEL;
	}

//	public void valueChanged(TreeSelectionEvent e) {
//	TreePath selectedTreePath = e.getNewLeadSelectionPath();
//	if ( selectedTreePath == null ) 
//	{
//	selectedNode = null;
//	return;
//	}

//	selectedNode = 
//	(DefaultMutableTreeNode)selectedTreePath.getLastPathComponent();

//	}

//	TreeDragSource.java
//	A drag source wrapper for a JTree. This class can be used to make
//	a rearrangeable DnD tree with the TransferableTreeNode class as the
//	transfer data type.

	class TreeDragSource implements DragSourceListener, DragGestureListener {

		DragSource source;

		DragGestureRecognizer recognizer;

		TransferableTreeNode transferable;

		DefaultMutableTreeNode oldNode;

		JTree sourceTree;

		public TreeDragSource(JTree tree, int actions) {
			sourceTree = tree;
			source = new DragSource();
			recognizer = source.createDefaultDragGestureRecognizer(sourceTree,
					actions, this);
		}

		/*
		 * Drag Gesture Handler
		 */
		public void dragGestureRecognized(DragGestureEvent dge) {
			System.out.println("MT : drag gesutre recognized");
			TreePath path = sourceTree.getSelectionPath();
			if ((path == null) || (path.getPathCount() <= 1)) {
				// We can't move the root node or an empty selection
				return;
			}
			oldNode = (DefaultMutableTreeNode) path.getLastPathComponent();
			transferable = new TransferableTreeNode(path);
//			source.startDrag(dge, DragSource.DefaultMoveNoDrop, transferable, this);

			// If you support dropping the node anywhere, you should probably
			// start with a valid move cursor:
			source.startDrag(dge, DragSource.DefaultMoveDrop, transferable,
					this);
		}

		/*
		 * Drag Event Handlers
		 */
		public void dragEnter(DragSourceDragEvent dsde) {}

		public void dragExit(DragSourceEvent dse) {}

		public void dragOver(DragSourceDragEvent dsde) {}

		public void dropActionChanged(DragSourceDragEvent dsde) {}

		public void dragDropEnd(DragSourceDropEvent dsde) {}
	}

	class TreeDropTarget implements DropTargetListener {

		DropTarget target;

		JTree targetTree;

		public TreeDropTarget(JTree tree) {
			targetTree = tree;
			target = new DropTarget(targetTree, this);
		}

		public void dragEnter(DropTargetDragEvent dtde) {
			dtde.acceptDrag(dtde.getDropAction());
		}

		public void dragOver(DropTargetDragEvent dtde) {
			dtde.acceptDrag(dtde.getDropAction());
		}

		public void dragExit(DropTargetEvent dte) {}

		public void dropActionChanged(DropTargetDragEvent dtde) {}

		public void drop(DropTargetDropEvent dtde) {
			TreePath parentpath =getClosestPathForLocation(dtde.getLocation().x, dtde.getLocation().y);
			DefaultMutableTreeNode parent = (DefaultMutableTreeNode) parentpath
			.getLastPathComponent();

			try {
				Transferable tr = dtde.getTransferable();
				DataFlavor[] flavors = tr.getTransferDataFlavors();
				for (int i = 0; i < flavors.length; i++) {
					if (tr.isDataFlavorSupported(flavors[i])) {
						dtde.acceptDrop(dtde.getDropAction());
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) latestPath
						.getLastPathComponent();

						if (parent.getUserObject() instanceof Entry &&
								node.getUserObject() instanceof Entry) {
							Entry dragEntry = (Entry)parent.getUserObject();
							Entry dropEntry = (Entry)node.getUserObject();

							Document dragParent = (Document)((DefaultMutableTreeNode)parent.getParent()).getUserObject();
							Document dropParent = (Document)((DefaultMutableTreeNode)node.getParent()).getUserObject();

							if (!((Document)dropParent).getIsEditable()) {
								return;	//We can't change this document
							}
							if (dragParent.equals(dropParent)) {
								int dragIndex = -1;
								int dropIndex = -1;

								for (int n=0; n<dragParent.numEntries(); n++) {
									Entry curEntry = dragParent.getEntry(n);
									if (curEntry.equals(dropEntry)) {
										dropIndex = n; 
									}

									if (curEntry.equals(dragEntry)) {
										dragIndex = n;
									}
								}

								if (dragIndex != -1 && dropIndex != -1) {
									dropEntry.setSection(dragEntry.getSection());
									dragParent.moveEntry(dropIndex, dragIndex);
									docPane.getCurrentPanel().refresh(docPane.getCurrentPanel().getCurrentSection());
									DocTreeModel.getInstance().refreshDocument(dropParent);
									((Document)dropParent).setIsRevisionCandidate(true);
								}
							}
						}
						dtde.dropComplete(true);
						return;
					}
				}
				try {
					dtde.rejectDrop();
				} catch (Exception ex) {
					//try to reject; ok, it not
				}
			} catch (Exception e) {
				e.printStackTrace();
				try {
					dtde.rejectDrop();
				} catch (Exception ex) {
					//try to reject; ok, it not
				}
			}
		}
	}

	/**
	 * Describes how a tree node can be dragged and dropped within the tree
	 * @author pwhelan
	 */
	class TransferableTreeNode implements Transferable {

		/**
		 * The data flavors supported
		 */
		DataFlavor flavors[] = { TREE_PATH_FLAVOR };

		/**
		 * The current path selected
		 */
		TreePath path;

		/**
		 * Constructor, the tree path to transfer
		 * @param tp the tree path to transfer
		 */
		public TransferableTreeNode(TreePath tp) {
			path = tp;
		}

		/**
		 * Get the supported transfer data flavors
		 * @return the data flavors supported
		 */
		public synchronized DataFlavor[] getTransferDataFlavors() {
			return flavors;
		}

		/**
		 * Check if the data flavor is supported
		 * @param to the flavor to check
		 * @return true if the data flavor is supported; false if not
		 */
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return (flavor.getRepresentationClass() == TreePath.class);
		}

		/**
		 * Get the transfer object
		 * @param flavor the data flavor 
		 * @return the object to transfer
		 */
		public Object getTransferData(DataFlavor flavor)
		throws UnsupportedFlavorException, IOException {
			if (isDataFlavorSupported(flavor)) {
				latestPath = path;
				return path;
			}

			throw new UnsupportedFlavorException(flavor);
		}
	}

}	


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

package org.psygrid.collection.entry;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXTitledPanel;
import org.psygrid.collection.entry.chooser.ChooserHelper;
import org.psygrid.collection.entry.displaytreatmentallocation.DisplayTreatmentAllocationAction;
import org.psygrid.collection.entry.editexternalid.EditExternalIdentifierAction;
import org.psygrid.collection.entry.event.ContextualMenuMouseAdapter;
import org.psygrid.collection.entry.model.EntryComboBoxModel;
import org.psygrid.collection.entry.model.RecordTreeModel;
import org.psygrid.collection.entry.persistence.ConsentMap2;
import org.psygrid.collection.entry.persistence.ExternalIdGetter;
import org.psygrid.collection.entry.persistence.NoExternalIdMappingException;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.collection.entry.remote.ChangeDocStatusWorker;
import org.psygrid.collection.entry.remote.GetRegisterSubjectWorker;
import org.psygrid.collection.entry.remote.GetSubjectForRandomizationWorker;
import org.psygrid.collection.entry.remote.RemoteManager;
import org.psygrid.collection.entry.remote.RemoteServiceFault;
import org.psygrid.collection.entry.sampletracking.SampleTrackingAction;
import org.psygrid.collection.entry.security.EntrySAMLException;
import org.psygrid.collection.entry.ui.Messages;
import org.psygrid.collection.entry.ui.ViewRecordPropertiesDialog;
import org.psygrid.collection.entry.util.ResetWaitRunnable;
import org.psygrid.collection.entry.util.WaitRunnable;
import org.psygrid.common.identifier.InvalidIdentifierException;
import org.psygrid.common.ui.WrappedJOptionPane;
import org.psygrid.data.model.hibernate.AssociatedConsentForm;
import org.psygrid.data.model.hibernate.ConsentFormGroup;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.DocumentGroup;
import org.psygrid.data.model.hibernate.DocumentInstance;
import org.psygrid.data.model.hibernate.DocumentOccurrence;
import org.psygrid.data.model.hibernate.PrimaryConsentForm;
import org.psygrid.data.model.hibernate.Record;
import org.psygrid.data.model.hibernate.Status;
import org.psygrid.data.utils.security.NotAuthorisedFault;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

/**
 * <p>A view that shows a IRecord (also known as participant) in a tree where
 * the immediate children are document groups and the grandchildren are
 * document occurrences/document instances.</p>
 * 
 * <p>In addition to this hierarchichal view of the record, this view also
 * allows the user to open documents, view/edit record properties and change
 * the status of documents from complete to controlled/pending.</p>
 * 
 * @see RecordTreeModel
 * @see IRecord
 */
public class RecordView {
	
	private static final Icon RECORD_ICON = Icons.getInstance().getIcon("dataset"); //$NON-NLS-1$

	private static final Icon DOCUMENT_GROUP_ICON = Icons.getInstance().getIcon("document_group"); //$NON-NLS-1$

	private static final Icon DOCUMENT_GROUP_LOCKED_ICON = Icons.getInstance().getIcon("document_group_locked"); //$NON-NLS-1$

	private static final Icon DOCUMENT_OCCURRENCE_ICON = Icons.getInstance().getIcon("document_occurrence"); //$NON-NLS-1$

	private static final Icon DOCUMENT_NOT_STARTED_ICON = Icons.getInstance().getIcon("document_not_started"); //$NON-NLS-1$

	private static final Icon DOCUMENT_LOCALLY_INCOMPLETE_ICON = Icons.getInstance().getIcon("document_locally_incomplete"); //$NON-NLS-1$

	private static final Icon DOCUMENT_INCOMPLETE_ICON = Icons.getInstance().getIcon("document_incomplete"); //$NON-NLS-1$

	private static final Icon DOCUMENT_READY_TO_SUBMIT_ICON = Icons.getInstance().getIcon("document_ready_to_submit"); //$NON-NLS-1$

	private static final Icon DOCUMENT_COMPLETE_ICON = Icons.getInstance().getIcon("document_complete"); //$NON-NLS-1$

	private static final Icon DOCUMENT_PENDING_ICON = Icons.getInstance().getIcon("document_pending"); //$NON-NLS-1$

	private static final Icon DOCUMENT_REJECTED_ICON = Icons.getInstance().getIcon("document_rejected"); //$NON-NLS-1$

	private static final Icon DOCUMENT_APPROVED_ICON = Icons.getInstance().getIcon("document_approved"); //$NON-NLS-1$

	private static final Icon DOCUMENT_CONTROLLED_ICON = Icons.getInstance().getIcon("document_approved"); //$NON-NLS-1$

	private static final Icon DOCUMENT_OCCURRENCE_LOCKED_ICON = Icons.getInstance().getIcon("document_locked"); //$NON-NLS-1$

	private static final Icon DOCUMENT_OCCURRENCE_INACTIVE_ICON = Icons.getInstance().getIcon("document_locked"); //$NON-NLS-1$
	
	private static final Icon FAILED_COMMIT_ICON = Icons.getInstance().getIcon("failed_save");

	private final static String STATUS_FILTER_ALL = "All";

	private static final long serialVersionUID = 1L;

	private ApplicationModel model;

	private JXTitledPanel panel;

	private JTree recordTree;

	private Application application;

	public RecordView(final Application application) {
		this.model = application.getModel();
		this.application = application;
		panel = new JXTitledPanel();
		panel.setScrollableTracksViewportHeight(true);
		panel.setScrollableTracksViewportWidth(true);
		panel.setContentContainer(createContentContainer());
	}

	/**
	 * Revalidates the current tree. This method should be called if the
	 * current record or its documents have been modified.
	 */
	public void revalidate() {
		recordTree.revalidate();
	}

	/**
	 * Updates the title and model of the record tree. This method should be
	 * called if the current record has changed.
	 */
	public void update() {
		recordTree.setModel(new RecordTreeModel(model.getCurrentRecord()));
	}

	public RecordTreeModel getRecordTreeModel() {
		return (RecordTreeModel) recordTree.getModel();
	}

	public void dispose() {
		/* Do nothing for now */
	}

	public JPanel getPanel() {
		return panel;
	}

	private String getPrimarySecondaryText(Record record) {
		String primSecText = null;
		if ( null != record.getPrimaryIdentifier() )
			primSecText = EntryMessages.getString("RecordView.primaryParticipant")+ record.getRecord().getPrimaryIdentifier();
		else if ( null != record.getSecondaryIdentifier() )
			primSecText = EntryMessages.getString("RecordView.secondaryParticipant")+ record.getSecondaryIdentifier();
		return primSecText;
	}

	private String getText(Object value, boolean longText) {
		String startHtml = EntryMessages.getString("RecordView.startHtmlTooltip");
		String endHtml = EntryMessages.getString("RecordView.endHtmlTooltip");
		String startPara = EntryMessages.getString("RecordView.startHtmlPara");
		String endPara = EntryMessages.getString("RecordView.endHtmlPara");
		if (value == null)
			return ""; //$NON-NLS-1$
		if (value instanceof Record) {
			
			String idForDisplay = null;
			Record record = (Record) value;
			String sysIdentifier = record.getIdentifier().getIdentifier();
			
			if(record.getDataSet().getUseExternalIdAsPrimary() == true){
				try {
					idForDisplay = ExternalIdGetter.get(sysIdentifier);
				} catch (NoExternalIdMappingException e) {
					if(record.getStatus().getShortName().equalsIgnoreCase("INVALID")){
						idForDisplay = "?????????";
					}else{			
						ExceptionsHelper.handleException(this.panel, "Problem Occurred", e, "Problem displaying record id", false);
						idForDisplay = sysIdentifier;
					}
				}
			}else{
				idForDisplay = sysIdentifier;
			}
			
			if (longText) {
				String text = startHtml + EntryMessages.getString("RecordView.participant") + 
				idForDisplay + endPara;
				String primSecText = getPrimarySecondaryText(record);
				if (primSecText != null)
					text += startPara + primSecText;
				text += endHtml;
				return text;
			}

			// Get the record status from the record status map and append it to the record identifier.
			Status recordStatus = PersistenceManager.getInstance().getRecordStatusMap().getStatusForRecord(record.getIdentifier().getIdentifier());
			idForDisplay+=" ("+(recordStatus==null?"???":recordStatus.getLongName())+")";
			
			return idForDisplay;
		}
		if (value instanceof DocumentGroup)
			return ((DocumentGroup) value).getDisplayText();
		if (value instanceof DocumentOccurrence) {
			DocumentOccurrence docOcc = (DocumentOccurrence) value;
			if (longText) {
				String statusText = getDocumentStatus(docOcc).toStatusLongName();
				return startHtml +
				docOcc.getCombinedDisplayText() +
				endPara + startPara + EntryMessages.getString("RecordView.status") + 
				statusText + endHtml;
			}
			return docOcc.getCombinedDisplayText();
		}
		return value.toString() == null ? "" : value.toString(); //$NON-NLS-1$
	}

	private JPanel createContentContainer() {
		JPanel contentContainer = new JPanel(new BorderLayout());

		DefaultFormBuilder sectionBuilder = new DefaultFormBuilder(
				new FormLayout("default, 2dlu, default, 2dlu, right:default:grow"), new JPanel()); //$NON-NLS-1$

		sectionBuilder.append(new JLabel(EntryMessages.getString("RecordView.status")));
		ComboBoxModel comboBoxModel = getStatusFilterModel();
		JComboBox statusFilter = new JComboBox(comboBoxModel);
		statusFilter.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if (e.getItem().equals(STATUS_FILTER_ALL))
						getRecordTreeModel().setStatusFilter(null);
					else
						getRecordTreeModel().setStatusFilter(
								DocumentStatus.fromStatusLongName((String) e.getItem()));
				}
			}
		});
		sectionBuilder.append(statusFilter);
		JButton closeButton = new JButton(new AbstractAction() {
			private static final long serialVersionUID = 1L;
			{
				putValue(Action.SMALL_ICON, Icons.getInstance().getIcon("fileclose")); //$NON-NLS-1$
				putValue(Action.SHORT_DESCRIPTION, "Close");
			}
			public void actionPerformed(ActionEvent e) {
				if (application.getModel().getCurrentDocOccurrence() != null) {
					if (!EntryHelper.showDocumentWillBeLostDialog(application)){
						return;
					}
				}
				application.clear(true);
			}
		});
		closeButton.setBorderPainted(false);
		closeButton.putClientProperty("substancelaf.componentFlat", //$NON-NLS-1$
				Boolean.TRUE);
		sectionBuilder.append(closeButton);
		contentContainer.add(sectionBuilder.getPanel(), BorderLayout.NORTH);

		recordTree = new JTree(new RecordTreeModel(model.getCurrentRecord())) {
			private static final long serialVersionUID = 1L;

			@Override
			public String convertValueToText(Object value, boolean selected,
					boolean expanded, boolean leaf, int row, boolean hasFocus) {
				return getText(value, false);
			}
		};
		recordTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		ToolTipManager.sharedInstance().registerComponent(recordTree);
		recordTree.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		/* 
		 * A listener that causes right-clicks to have the same selection change
		 * effect as left-clicks. In other words, the behaviour that Swing should
		 * have had by default...
		 */
		recordTree.addMouseListener(new ContextualMenuMouseAdapter(null) {
			@Override
			protected void showContextualMenu(MouseEvent e) {
				TreePath pathForLocation = recordTree.getPathForLocation(e.getX(), e.getY());
				if (pathForLocation == null) {
					recordTree.setSelectionPath(null);
					return;
				}
				List<TreePath> selectionPaths = recordTree.getSelectionPaths() == null ?
						Collections.<TreePath>emptyList() : Arrays.asList(recordTree.getSelectionPaths());
						if ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0) {
							if (!selectionPaths.contains(pathForLocation))
								recordTree.addSelectionPath(pathForLocation);
							return;
						}
						if ((e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) != 0) {
							int selectionRow = recordTree.getSelectionModel().getLeadSelectionRow();
							int locationRow = recordTree.getRowForPath(pathForLocation);
							recordTree.addSelectionInterval(selectionRow, locationRow);
							return;
						}

						/* 
						 * This check allows one to do the multi-selection using
						 * Ctrl or Shift and right-click without any modifier
						 * without losing the selection as long as the right-click
						 * happens on a currently selected path.
						 */
						if (!selectionPaths.contains(pathForLocation))
							recordTree.setSelectionPath(pathForLocation);
			}
		});
		recordTree.addMouseListener(new ContextualMenuMouseAdapter(null) {
			@Override
			protected void showContextualMenu(MouseEvent e) {
				List<Object> selectedItems = getSelectedItems(e);
				if (selectedItems.isEmpty())
					return;

				/* Operations that only make sense for single selection */
				if (selectedItems.size() == 1) {
					DocumentOccurrence docOcc = getSelectedDocOcc(e);
					if (docOcc != null) {
						showContextualMenuForDoc(e, docOcc);
						return;
					}
					Record record = getSelectedRecord(e);
					if (record != null)
						showContextualMenuForRecord(e, record);
				}

				/* Multi-selection operations */
				List<DocumentOccurrence> docOccs = getSelectedDocOccs(e,
						EnumSet.of(DocumentStatus.COMPLETE));
				List<DocumentGroup> docGroups = getSelectedDocumentGroups(e);

				/* 
				 * We don't handle selections that involve selections with
				 * multiple types.
				 */
				if (!docOccs.isEmpty() && docOccs.size() == selectedItems.size())
					showContextualMenuForDocs(e, docOccs);

				if (!docGroups.isEmpty() && docGroups.size() == selectedItems.size())
					showContextualMenuForDocGroups(e, docGroups);
			}

			private void showContextualMenuForDocGroups(MouseEvent e,
					final List<DocumentGroup> docGroups) {
				JPopupMenu popupMenu = new JPopupMenu();
				final Collection<DocumentOccurrence> completeDocs = getCompleteDocumentOccs(docGroups);
				if (completeDocs.isEmpty())
					return;
				if (model.getCurrentDataSet().isNoReviewAndApprove()) {
					popupMenu.add(new AbstractAction(EntryMessages.getString("RecordView.allCompleteToControlled")) {
						private static final long serialVersionUID = 1L;

						public void actionPerformed(ActionEvent e) {
							doMoveToControlled(completeDocs);
						}
					});
				}
				else {
					popupMenu.add(new AbstractAction(EntryMessages.getString("RecordView.allCompleteToPending")) {
						private static final long serialVersionUID = 1L;

						public void actionPerformed(ActionEvent e) {
							doSubmitForReview(completeDocs);
						}
					});
				}
				popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}


			private Collection<DocumentOccurrence> getCompleteDocumentOccs(
					List<DocumentGroup> docGroups) {
				List<DocumentOccurrence> docOccs = new ArrayList<DocumentOccurrence>();
				for (DocumentGroup docGroup : docGroups) {
					for (DocumentOccurrence docOcc : getRecordTreeModel().getDocOccurrences(docGroup))
						if (getDocumentStatus(docOcc) == DocumentStatus.COMPLETE)
							docOccs.add(docOcc);
				}
				return docOccs;
			}

			private void showContextualMenuForRecord(MouseEvent e,
					final Record record) {
				JPopupMenu popupMenu = new JPopupMenu();

				JMenuItem viewProperties = new JMenuItem(new AbstractAction(EntryMessages.getString("RecordView.viewEditParticipantProperties")) {
					private static final long serialVersionUID = 1L;

					public void actionPerformed(ActionEvent e) {
						if (!application.isOnline()) {
							String title = EntryMessages.getString("RecordView.propertiesofflinetitle");
				    		String message = EntryMessages.getString("RecordView.propertiesofflinemessage");
							WrappedJOptionPane.showWrappedMessageDialog(application, message, title, WrappedJOptionPane.INFORMATION_MESSAGE);	
						}
						else if(PersistenceManager.getInstance().recordReadyToCommit(record.getIdentifier())){
							// Prevent viewing the properties of an uncommitted record when application is online.
							String title = EntryMessages.getString("RecordView.propertiesofflinetitle");
							String message = EntryMessages.getString("RecordView.propertiesuncommittedrecord");
							WrappedJOptionPane.showWrappedMessageDialog(application, message, title, WrappedJOptionPane.INFORMATION_MESSAGE);
						}
						else {
							
							SwingWorker<Record, Object> worker = new SwingWorker<Record, Object>() {

								@Override
								protected Record doInBackground() throws IOException,
								NotAuthorisedFault, RemoteServiceFault, EntrySAMLException, 
								ConnectException, SocketTimeoutException, InvalidIdentifierException
								{
									return RemoteManager.getInstance().getRecordSummary(
											model.getCurrentRecord().getIdentifier().getIdentifier());
								}

								@Override
								protected void done() {
									try{
										new ResetWaitRunnable(application).run();
										Record record = get();
										new ViewRecordPropertiesDialog(
												application, record);

									}
							        catch (InterruptedException e) {
							            ExceptionsHelper.handleInterruptedException(e);
									}
							        catch (ExecutionException e){
							            Throwable cause = e.getCause();
							            if (cause instanceof ConnectException) {
							                ExceptionsHelper.handleConnectException(
							                        application,
							                        (ConnectException) cause);
							            } else if (cause instanceof SocketTimeoutException) {
							                ExceptionsHelper.handleSocketTimeoutException(
							                        application, 
							                        (SocketTimeoutException) cause);
							            } else if (cause instanceof IOException) {
							                ExceptionsHelper.handleIOException(
							                        application,
							                        (IOException) cause, false);
							            } else if (cause instanceof NotAuthorisedFault) {
							                ExceptionsHelper.handleNotAuthorisedFault(
							                        application,
							                        (NotAuthorisedFault) cause);
							            } else if (cause instanceof RemoteServiceFault) {
							    			String title = Messages.getString("ViewRecordPropertiesDialog.noRemoteRecordTitle");
							    			String message = Messages.getString("ViewRecordPropertiesDialog.noRemoteRecordMessage");
							    			WrappedJOptionPane.showWrappedMessageDialog(application, message, title, WrappedJOptionPane.WARNING_MESSAGE);
							            } else if (cause instanceof EntrySAMLException) {
							                ExceptionsHelper.handleEntrySAMLException(
							                        application,
							                        (EntrySAMLException) cause);
							            } else {
							                ExceptionsHelper.handleFatalException(cause);
							            }							        	
							        }
								}
							};
							
							new WaitRunnable(application).run();
							SwingWorkerExecutor.getInstance().execute(worker);
						}
					}
				});
				popupMenu.add(viewProperties); 

				if ( model.getCurrentDataSet().isEslUsed() ){
					//show edit participant register menu item
					JMenuItem viewRegister = new JMenuItem(new AbstractAction(EntryMessages.getString("RecordView.viewEditParticipantRegister")) {

						public void actionPerformed(ActionEvent e) {
							if (!application.isOnline()) {
								String title = EntryMessages.getString("RecordView.propertiesofflinetitle");
					    		String message = EntryMessages.getString("RecordView.propertiesofflinemessage");
								WrappedJOptionPane.showWrappedMessageDialog(application, message, title, WrappedJOptionPane.INFORMATION_MESSAGE);	
							}
							else {
								GetRegisterSubjectWorker worker = new GetRegisterSubjectWorker(model.getCurrentRecord(), application);
								SwingWorkerExecutor.getInstance().execute(worker);
							}
						}
						
					});
					popupMenu.add(viewRegister); 
				}
				
				if(application.isOnline() && model.getCurrentDataSet().isRandomizationRequired() == true && model.getCurrentDataSet().getShowRandomisationTreatment() == true){
					//Provide a menu item that allows the retrieval of the allocated treatment.
					//if isCanRandomize returns false this is most likely because the record has already been randomised - and that's when we want to display this menu item.
					popupMenu.add(new DisplayTreatmentAllocationAction(application, model.getCurrentRecord()));
				}
				
				if(application.isOnline() && model.getCurrentDataSet().getExternalIdEditableSubstringMap() != null && model.getCurrentDataSet().getExternalIdEditableSubstringMap().size() > 0){
					popupMenu.add(new EditExternalIdentifierAction(application, model.getCurrentRecord()));
				}
				
				if ( model.isCanRandomize() && application.isOnline() ){
					JMenuItem randomize = new JMenuItem(new AbstractAction(EntryMessages.getString("RecordView.randomize")){

						public void actionPerformed(ActionEvent e) {
							if (application.isOnline()) {
								GetSubjectForRandomizationWorker worker = new GetSubjectForRandomizationWorker(model.getCurrentRecord(), application);
								SwingWorkerExecutor.getInstance().execute(worker);
							}
							else {
								String title = EntryMessages.getString("RecordView.randomizeofflinetitle");
					    		String message = EntryMessages.getString("RecordView.randomizeofflinemessage");
								WrappedJOptionPane.showWrappedMessageDialog(application, message, title, WrappedJOptionPane.INFORMATION_MESSAGE);	
							}
						}
						
					});
					popupMenu.add(randomize);
				}
				
				// Conditionally add the sample tracking menu item
				if (application.isOnline()){
					if(SampleTrackingAction.isTrackingEnabled(record)) popupMenu.add(new SampleTrackingAction(application));
				}
				
				if(application.isOnline()){
					
				}
						
				final Collection<DocumentOccurrence> completeDocs = getCompleteDocumentOccs(record);
				if (!completeDocs.isEmpty() && model.getCurrentDataSet().isNoReviewAndApprove()) {
					popupMenu.add(new AbstractAction(EntryMessages.getString("RecordView.allCompleteToControlled")) {
						private static final long serialVersionUID = 1L;

						public void actionPerformed(ActionEvent e) {
                            doMoveToControlled(completeDocs);
						}
					});
				}
				else if (!completeDocs.isEmpty()) {
					popupMenu.add(new AbstractAction(EntryMessages.getString("RecordView.allCompleteToPending")) {
						private static final long serialVersionUID = 1L;

						public void actionPerformed(ActionEvent e) {
							doSubmitForReview(completeDocs);
						}
					});
				}
				popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}

			private Collection<DocumentOccurrence> getCompleteDocumentOccs(
					Record record) {
				List<DocumentGroup> docGroups = new ArrayList<DocumentGroup>();
				for (int i = 0, c = record.getDataSet().numDocumentGroups(); i < c; ++i)
					docGroups.add(record.getDataSet().getDocumentGroup(i));
				return getCompleteDocumentOccs(docGroups);
			}

			private void showContextualMenuForDocs(MouseEvent e,
					final Collection<DocumentOccurrence> docOccs) {
				JPopupMenu popupMenu = new JPopupMenu();
				if (addToPopupMenu(popupMenu, docOccs))
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}

			/**
			 * Adds "Move to controlled" or "Submit for review" action to
			 * popupMenu if appropriate.
			 */
			private boolean addToPopupMenu(JPopupMenu popupMenu, final Collection<DocumentOccurrence> docOccs) {
				for (DocumentOccurrence docOcc : docOccs) {
					if (getDocumentStatus(docOcc) != DocumentStatus.COMPLETE)
						return false;
				}
				if (model.getCurrentDataSet().isNoReviewAndApprove()) {
					popupMenu.add(new AbstractAction(EntryMessages.getString("RecordView.moveToControlled")) {
						private static final long serialVersionUID = 1L;

						public void actionPerformed(ActionEvent e) {
							doMoveToControlled(docOccs);
						}
					});
				} else {
					popupMenu.add(new AbstractAction(EntryMessages.getString("RecordView.moveToPending")) {
						private static final long serialVersionUID = 1L;

						public void actionPerformed(ActionEvent e) {
							doSubmitForReview(docOccs);
						}
					});
				}
				return true;
			}

			private void showContextualMenuForDoc(MouseEvent e,
					final DocumentOccurrence docOcc) {
				JPopupMenu popupMenu = new JPopupMenu();
				boolean showPopup = false;
				if (canOpen(docOcc, false)) {
					showPopup = true;
					popupMenu.add(new AbstractAction(EntryMessages.getString("Entry.open")) {
						private static final long serialVersionUID = 1L;
						public void actionPerformed(ActionEvent e) {
							loadDocument(docOcc);
						}
					});
				}
				showPopup |= addToPopupMenu(popupMenu, Collections.singleton(docOcc));
				if (showPopup)
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}

		});
		recordTree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					DocumentOccurrence docOcc = getSelectedDocOcc(e);
					if (canOpen(docOcc, true)) {
						loadDocument(docOcc);
					}
				}

			}
		});
		recordTree.setBackground(Color.WHITE);
		recordTree.setEditable(false);
		recordTree.setExpandsSelectedPaths(true);
		recordTree.setScrollsOnExpand(false);
		setRenderer();
		contentContainer.add(new JScrollPane(recordTree));
		return contentContainer;
	}

	private void doSubmitForReview(Collection<DocumentOccurrence> docOccs) {
		if (!application.isOnline()) {
			String title = EntryMessages.getString("RecordView.submitofflinetitle");
    		String message = EntryMessages.getString("RecordView.submitofflinemessage");
			WrappedJOptionPane.showWrappedMessageDialog(application, message, title, WrappedJOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
        boolean docIsOpen = ((Application)application).getModel().getCurrentDocOccurrenceInstance() == null ?
        		false : true;
        
        if(docIsOpen){
        	String title = EntryMessages.getString("RecordView.documentsStillOpenTitle");
        	String message = EntryMessages.getString("RecordView.documentsStillOpenMessage");
        	JOptionPane.showMessageDialog(application, message, title, 
        			JOptionPane.INFORMATION_MESSAGE);
        	return;
        }
		
		ChangeDocStatusWorker worker = new ChangeDocStatusWorker(
				application, docOccs, DocumentStatus.PENDING, 
				model.getCurrentRecord().getIdentifier().getIdentifier()){

			@Override
			protected void success() {
				super.success();
				recordTree.revalidate();
			}
		};

		new WaitRunnable(application).run();
		SwingWorkerExecutor.getInstance().execute(worker);

	}

	private void doMoveToControlled(Collection<DocumentOccurrence> docOccs) {
		if (!application.isOnline()) {
			String title = EntryMessages.getString("RecordView.controlledofflinetitle");
    		String message = EntryMessages.getString("RecordView.controlledofflinemessage");
			WrappedJOptionPane.showWrappedMessageDialog(application, message, title, WrappedJOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
        boolean docIsOpen = ((Application)application).getModel().getCurrentDocOccurrenceInstance() == null ?
        		false : true;
        
        if(docIsOpen){
        	String title = EntryMessages.getString("RecordView.documentsStillOpenTitle");
        	String message = EntryMessages.getString("RecordView.documentsStillOpenMessage");
        	JOptionPane.showMessageDialog(application, message, title, 
        			JOptionPane.INFORMATION_MESSAGE);
        	return;
        }
		
		ChangeDocStatusWorker worker = new ChangeDocStatusWorker(
				application, docOccs, DocumentStatus.CONTROLLED, 
				model.getCurrentRecord().getIdentifier().getIdentifier()){

			@Override
			protected void success() {
				super.success();
				recordTree.revalidate();
			}
		};

		new WaitRunnable(application).run();
		SwingWorkerExecutor.getInstance().execute(worker);

	}

	private ComboBoxModel getStatusFilterModel() {
		return new EntryComboBoxModel() {
			private final List<String> statuses = new ArrayList<String>();
			private String selectedItem = STATUS_FILTER_ALL;
			{
				for (DocumentStatus status : DocumentStatus.getUserVisible(
						model.getCurrentDataSet().isNoReviewAndApprove(),
						PersistenceManager.getInstance().getData().isAlwaysOnlineMode())){
					statuses.add(status.toStatusLongName());
				}
				statuses.add(STATUS_FILTER_ALL);
				Collections.sort(statuses);
			}
			public Object getElementAt(int index) {
				return statuses.get(index);
			}

			public Object getSelectedItem() {
				return selectedItem;
			}

			public void setSelectedItem(Object anItem) {
				selectedItem = (String) anItem;
			}

			public int getSize() {
				return statuses.size();
			}
		};
	}

	private void setRenderer() {
		/* 
		 * We delegate to the look and feel renderer and customise it instead
		 * of replacing it.
		 */
		final TreeCellRenderer cellRenderer = recordTree.getCellRenderer();
		recordTree.setCellRenderer(new TreeCellRenderer() {
			private Font font;
			public Component getTreeCellRendererComponent(JTree tree,
					Object value, boolean selected, boolean expanded,
					boolean leaf, int row, boolean hasFocus) {
				Component comp = cellRenderer.getTreeCellRendererComponent(tree, value,
						selected, expanded, leaf, row, hasFocus);
				setFont(comp, value);

				/* 
				 * This should always be true, but if not we just don't set the
				 * tooltip text.
				 */
				if (comp instanceof JComponent) {
					JComponent jcomp = (JComponent) comp;
					jcomp.setToolTipText(getText(value, true));
				}

				/*
				 * This should always be true, but in case a look and feel
				 * does not use a JLabel, then we degrade gracefully and do
				 * not customise the icons.
				 */
				if (comp instanceof JLabel) {
					JLabel label = (JLabel) comp;
					setIcon(label, value);
				}
				return comp;
			}

			private void setFont(Component comp, Object value) {
				if (value instanceof DocumentOccurrence) {
					DocumentOccurrence docOcc = (DocumentOccurrence) value;
					if (isSameDocument(docOcc)) {
						storeOriginalFont(comp);
						comp.setFont(font.deriveFont(Font.BOLD));
					}
					else if (font != null && (!comp.getFont().equals(font)))
						comp.setFont(font);

					comp.setEnabled(!isSecondary(docOcc));
				}
				else if (font != null && (!comp.getFont().equals(font))) {
					comp.setFont(font);
					comp.setEnabled(true);
				}
				else
					comp.setEnabled(true);
			}

			private void storeOriginalFont(Component comp) {
				if (font == null)
					font = comp.getFont();
			}

			private void setIcon(JLabel label, Object value) {
				if (value instanceof Record)
					label.setIcon(RECORD_ICON);
				else if (value instanceof DocumentGroup) {
					if (isLocked((DocumentGroup) value))
						label.setIcon(DOCUMENT_GROUP_LOCKED_ICON);
					else
						label.setIcon(DOCUMENT_GROUP_ICON);
				}
				else if (value instanceof DocumentOccurrence) {
					DocumentOccurrence docOcc = (DocumentOccurrence) value;
					DocumentStatus docStatus = getDocumentStatus(docOcc);
					Icon icon = null;

					/* 
					 * We use a single icon for locked/inactive that is not dependent
					 * on status. Ideally, we'd have locked/inactive version
					 * for each status, but that seemed of lesser importance
					 * than other items.
					 */
					if (docOcc.isLocked() || !checkDocConsent(docOcc))
						icon = DOCUMENT_OCCURRENCE_LOCKED_ICON;
					else if (isLocked(docOcc.getDocumentGroup()))
						icon = DOCUMENT_OCCURRENCE_INACTIVE_ICON;
					else {
						switch (docStatus) {
						case NOT_STARTED:
							icon = DOCUMENT_NOT_STARTED_ICON;
							break;
						case INCOMPLETE:
							icon = DOCUMENT_INCOMPLETE_ICON;
							break;
						case LOCALLY_INCOMPLETE:
							icon = DOCUMENT_LOCALLY_INCOMPLETE_ICON;
							break;
						case COMPLETE:
							icon = DOCUMENT_COMPLETE_ICON;
							break;
						case READY_TO_SUBMIT:
							icon = DOCUMENT_READY_TO_SUBMIT_ICON;
							break;
						case APPROVED:
							icon = DOCUMENT_APPROVED_ICON;
							break;
						case CONTROLLED:
							icon = DOCUMENT_CONTROLLED_ICON;
							break;
						case PENDING:
							icon = DOCUMENT_PENDING_ICON;
							break;
						case REJECTED:
							icon = DOCUMENT_REJECTED_ICON;
							break;
						case COMMIT_FAILED:
							icon = FAILED_COMMIT_ICON;
							break;
							/* For internal use, so we don't do anything */
						case DATASET_DESIGNER:
						case VIEW_ONLY:
						default:
						}
					}
					if (icon == null)
						icon = DOCUMENT_OCCURRENCE_ICON;
					label.setIcon(icon);
				}
				else
					label.setIcon(null);
			}
		});
	}

	private boolean canOpen(DocumentOccurrence docOcc, boolean showMessages) {
		if (docOcc == null) {
			return false;
		}
		if (isSameDocument(docOcc)) {
			if (showMessages) {
				EntryHelper.showSelectedDocumentIsAlreadyOpen(application);
			}
			return false;
		}
		else if ((model.getCurrentRecord().getDocumentInstance(docOcc) == null
				&& docOcc.isLocked()) || isLocked(docOcc.getDocumentGroup())) {
			if (showMessages)
				EntryHelper.showSelectedDocumentIsLockedDialog(application);
			return false;
		}
		else if (isSecondary(docOcc) && DocumentStatus.NOT_STARTED == getDocumentStatus(docOcc)) {
			if (showMessages)
				EntryHelper.showSelectedDocumentIsSecondary(application,
						model.getCurrentRecord());
			return false;
		}
		return true;
	}

	private boolean isLocked(DocumentGroup group) {
		return EntryHelper.isLocked(application, group, model.getCurrentRecord());
	}

	private boolean isSecondary(DocumentOccurrence docOcc) {
		return ChooserHelper.checkDocumentInstanceDde(model.getCurrentRecord(), docOcc);
	}

	private void loadDocument(DocumentOccurrence docOcc) {
		if (application.getModel().getCurrentDocOccurrence() != null) {
			if (EntryHelper.showDocumentWillBeLostDialog(application)){
				application.getModel().clear(false);
			}
			else{
				return;
			}
		}
		
		DocumentStatus docStatus = getOpeningStatus(docOcc);
		application.setSelectedDocOccurrence(docOcc, docStatus);
	}

	private DocumentStatus getOpeningStatus(DocumentOccurrence docOcc) {
		PersistenceManager pManager = PersistenceManager.getInstance();
		DocumentStatus ds = getDocumentStatus(docOcc);
		if ( !pManager.getData().canLoadPendingDocuments() &&
				( ds == DocumentStatus.PENDING ||
						ds == DocumentStatus.APPROVED ) ){
			return DocumentStatus.VIEW_ONLY;
		}
		if ( isSecondary(docOcc) ){
			return DocumentStatus.VIEW_ONLY;
		}
		return ds;
	}

	private DocumentStatus getDocumentStatus(DocumentOccurrence docOcc) {
		synchronized (PersistenceManager.getInstance()) {
			return PersistenceManager.getInstance().getRecordStatusMap()
			.getDocumentStatus(model.getCurrentRecord(), docOcc);
		}
	}

	private boolean isSameDocument(DocumentOccurrence docOcc) {
		return docOcc.equals(application.getModel().getCurrentDocOccurrence());
	}

	private Object getSelectedItem(MouseEvent e) {
		TreePath path = recordTree.getSelectionModel().getLeadSelectionPath();
		if (path != null)
			return path.getLastPathComponent();
		return null;
	}

	/**
	 * Returns the selected items in the tree model.
	 */
	private List<Object> getSelectedItems(MouseEvent e) {
		TreePath[] selectionPaths = recordTree.getSelectionModel().getSelectionPaths();
		if (selectionPaths == null)
			return Collections.emptyList();

		List<Object> items = new ArrayList<Object>(selectionPaths.length);
		for (TreePath path : selectionPaths)
			items.add(path.getLastPathComponent());
		return items;
	}

	private Record getSelectedRecord(MouseEvent e) {
		Object selected = getSelectedItem(e);
		if (selected instanceof Record)
			return (Record) selected;
		return null;
	}

	private List<DocumentOccurrence> getSelectedDocOccs(MouseEvent e, Set<DocumentStatus> docStatuses) {
		List<Object> selectedItems = getSelectedItems(e);
		List<DocumentOccurrence> docOccs = new ArrayList<DocumentOccurrence>();
		for (Object selected : selectedItems) {
			if (selected instanceof DocumentOccurrence) {
				DocumentOccurrence docOcc = (DocumentOccurrence) selected;
				if (docStatuses.contains(getDocumentStatus(docOcc)))
					docOccs.add(docOcc);
			}
		}
		return docOccs;
	}

	private List<DocumentGroup> getSelectedDocumentGroups(MouseEvent e) {
		List<Object> selectedItems = getSelectedItems(e);
		List<DocumentGroup> docGroups = new ArrayList<DocumentGroup>();
		for (Object selected : selectedItems) {
			if (selected instanceof DocumentGroup)
				docGroups.add((DocumentGroup) selected);
		}
		return docGroups;
	}

	private DocumentOccurrence getSelectedDocOcc(MouseEvent e) {
		Object selected = getSelectedItem(e);
		if (selected instanceof DocumentOccurrence)
			return (DocumentOccurrence) selected;
		return null;
	}

	private boolean makeSelectedDocVisible(RecordTreeModel treeModel, Object parent,
			DocumentOccurrence docOcc) {
		for (int i = 0, c = treeModel.getChildCount(parent); i < c; i++) {
			Object child = treeModel.getChild(parent, i);
			if (child.equals(docOcc)) {
				recordTree.scrollPathToVisible(treeModel.getPathToRoot(child));
				return true;
			}
			if (makeSelectedDocVisible(treeModel, child, docOcc))
				return true;
		}
		return false;
	}

	public void makeSelectedDocVisible() {
		RecordTreeModel treeModel = (RecordTreeModel) recordTree.getModel();
		DocumentOccurrence docOcc = model.getCurrentDocOccurrence();
		if (docOcc != null)
			makeSelectedDocVisible(treeModel, treeModel.getRoot(), docOcc);
	}
	
    private boolean checkDocConsent(DocumentOccurrence documentOccurrence) {
		ConsentMap2 consentMap = PersistenceManager.getInstance().getConsentMap();
		String identifier = model.getCurrentRecord().getIdentifier().getIdentifier();

		boolean docConsent = true;
		if ( consentMap.consentExists(identifier) ){
			//Consent info exists in the consent map for this identifier
			Document d = documentOccurrence.getDocument();
			for (int i=0; i<d.numConsentFormGroups(); i++ ){
				ConsentFormGroup cfg = d.getConsentFormGroup(i);
				boolean grpConsent = false;
				for (int j=0; j<cfg.numConsentForms(); j++){
					PrimaryConsentForm pcf = cfg.getConsentForm(j);
					boolean pcfConsent = consentMap.checkConsent(identifier, pcf);
					if ( pcfConsent ){
						//check associated consent forms
						for (int k=0; k<pcf.numAssociatedConsentForms(); k++){
							AssociatedConsentForm acf = pcf.getAssociatedConsentForm(k);
							pcfConsent &=  consentMap.checkConsent(identifier, acf);
						}
					}
					//consent must be obtained for one of the primary consent forms
					//in the consent form group
					grpConsent |= pcfConsent;
				}
				//consent must be obtained for all of the consent form groups associated
				//with the document
				docConsent &= grpConsent;
			}
		}
		else{
			//No consent in the map for this identifier, so we check in the record instead
			docConsent = model.getCurrentRecord().checkConsent((DocumentInstance)documentOccurrence.getDocument().generateInstance(documentOccurrence));
		}

		return docConsent;

	}
}

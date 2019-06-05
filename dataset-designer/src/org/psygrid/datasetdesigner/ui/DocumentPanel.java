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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.collection.entry.ApplicationModel;
import org.psygrid.collection.entry.EntryMessages;
import org.psygrid.collection.entry.FormView;
import org.psygrid.collection.entry.NavigationPanel;
import org.psygrid.collection.entry.event.SectionAdapter;
import org.psygrid.collection.entry.event.SectionChangedEvent;
import org.psygrid.collection.entry.event.SectionListener;
import org.psygrid.collection.entry.ui.DividerLabel;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.datasetdesigner.controllers.DatasetController;
import org.psygrid.datasetdesigner.dnd.StringTransferable;
import org.psygrid.datasetdesigner.model.DocTreeModel;
import org.psygrid.datasetdesigner.model.DummyDocument;
import org.psygrid.datasetdesigner.ui.configurationdialogs.ProvenanceDialog;
import org.psygrid.datasetdesigner.ui.dataelementfacilities.DELSecurity;
import org.psygrid.datasetdesigner.ui.editdialogs.AbstractEditDialog;
import org.psygrid.datasetdesigner.ui.editdialogs.BooleanEditDialog;
import org.psygrid.datasetdesigner.ui.editdialogs.CompositeEditDialog;
import org.psygrid.datasetdesigner.ui.editdialogs.DateEditDialog;
import org.psygrid.datasetdesigner.ui.editdialogs.DerivedEditDialog;
import org.psygrid.datasetdesigner.ui.editdialogs.ExternalDerivedEditDialog;
import org.psygrid.datasetdesigner.ui.editdialogs.IntegerEditDialog;
import org.psygrid.datasetdesigner.ui.editdialogs.LongTextEditDialog;
import org.psygrid.datasetdesigner.ui.editdialogs.NarrativeEditDialog;
import org.psygrid.datasetdesigner.ui.editdialogs.NumericEditDialog;
import org.psygrid.datasetdesigner.ui.editdialogs.OptionEditDialog;
import org.psygrid.datasetdesigner.ui.editdialogs.SectionControlDialog;
import org.psygrid.datasetdesigner.ui.editdialogs.SectionEditDialog;
import org.psygrid.datasetdesigner.ui.editdialogs.TextEditDialog;
import org.psygrid.datasetdesigner.utils.ElementUtility;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

/**
 * Document Panel
 * The main panel of the DSD
 * Controls the layout and event handling of documents
 * 
 * @author pwhelan
 */
public class DocumentPanel extends JPanel implements DropTargetListener, ActionListener {
	
	private static final Log LOG = LogFactory.getLog(DocumentPanel.class);

	private transient JPanel formPanel;

	private transient FormView formView;

	private transient int count = 0;

	private transient ApplicationModel model;

	private transient JPanel formViewPanel;

	private SectionEditDialog sectionEditDialog;

	private Entry copiedEntry;

	private JPanel containerPanel;

	private DsdInformationView informationView;

	private JPanel centerPanel;

	private Dimension preferredCenterCompSize;

	private int currentIndex;

	private int currentSectionIndex;

	/**
	 * The MainFrame 
	 */
	private MainFrame frame;

	private JDialog parentDialog;

	boolean isInElementViewMode;

	private Document document;
	
	private JScrollPane formScrollPane;

	/**
	 * Specifies whether this document is part of the
	 * data element library view
	 */
	private boolean isDEL;

	public DocumentPanel(MainFrame frame, Document document, boolean isDEL) {
		super();
		this.frame = frame;
		this.isDEL = isDEL;
		init(false, document);
	}

	public DocumentPanel(JDialog parent, Document docToRender, boolean isDEL) {
		super();
		this.parentDialog = parent;
		this.isDEL = isDEL;
		init(true, docToRender);
	}

	private void init(boolean isInElementViewMode, Document document) {
		this.document = document;
		this.isInElementViewMode = isInElementViewMode;
		// component, ops, listener, accepting
		new DropTarget(this, 
				DnDConstants.ACTION_MOVE,
				this,
				true);
		renderPanel();
	}

	public void showSectionEditDialog() {
		if (document.numSections() == 0) {
			sectionEditDialog = new SectionEditDialog(frame);
			sectionEditDialog.addOkListener(new SectionOkListener());
			sectionEditDialog.setVisible(true);
		}
	}

	private void renderPanel() {
		//clean up existing preview occs
		int numOccs = document.numOccurrences(); 

		for (int y=numOccs-1; y>=0; y--) {
			//preview occs are used for rendering only!
			String occName = document.getOccurrence(y).getName();
			if (occName.startsWith("Preview")) {
				document.removeOccurrence(y);
			}
		}

		removeAll();
		model = new ApplicationModel();
		final Factory factory = new HibernateFactory(); 
		if (document == null || document.numSections() == 0)  {
			add(new JPanel());
			showSectionEditDialog();
		} else {
			final DocumentOccurrence docOcc = factory.createDocumentOccurrence("Preview Doc" + Math.random());
			document.addOccurrence(docOcc);	
			final Record record = new Record();
			record.setIdentifier(new Identifier());
			final DocumentInstance docInstance = (DocumentInstance)document.generateInstance(docOcc);
			model.setCurrentRecord(record);
			docInstance.setRecord(record);
			docInstance.setStatus(factory.createStatus(Status.DOC_STATUS_DATASET_DESIGNER, -7));
			model.setSelectedDocOccurrence(docOcc);
			model.setSelectedDocOccurrenceInstance(docInstance, org.psygrid.collection.entry.DocumentStatus.DATASET_DESIGNER);
			informationView = new DsdInformationView(model.getCurrentDocument()
					.getDisplayText() + EntryMessages.getString("Application.dash") + "Design View", 
					model,
					this);
			centerPanel = createMainPanel(null, createFormScrollPane(model));
			setLayout(new BorderLayout());
			add(informationView.getPane(), BorderLayout.NORTH);
			add(centerPanel, BorderLayout.CENTER);
		}

		if (preferredCenterCompSize != null) {
			updateCenterPanel(preferredCenterCompSize);
		}

		revalidate();
		repaint();
	}


	private JPanel createMainPanel(final JComponent westComponent, final JComponent centerComponent) {
		containerPanel = new JPanel(new BorderLayout());
		containerPanel.setOpaque(false);
		if (westComponent != null) {
			containerPanel.add(westComponent, BorderLayout.WEST);
		}

		containerPanel.add(centerComponent, BorderLayout.CENTER);

		return containerPanel;
	}


	private JScrollPane createFormScrollPane(final ApplicationModel model) {
		formPanel = new JPanel();

		formPanel.setLayout(new BorderLayout());
		formView = new FormView(model);

		formViewPanel = (JPanel)formView.createPanel();
		if (((Document)document).getIsEditable()) {
			formViewPanel.addMouseListener(new RightClickMouseAdapter());
		}
		else {//Show view options only
			if (formViewPanel != null) {
				formViewPanel.addMouseListener(new RightClickMouseAdapter(false, true));
			}
		}

		if (formViewPanel != null) {
			for (int i=0; i<formViewPanel.getComponentCount(); i++) {
				Component comp = formViewPanel.getComponent(i);
				if (((Document)document).getIsEditable()) {
					comp.addMouseListener(new RightClickMouseAdapter(true, this.isInElementViewMode));
				}
				else {
					comp.addMouseListener(new RightClickMouseAdapter(true, true));
				}
				new DropTarget(comp, 
						DnDConstants.ACTION_MOVE,
						this,
						true);
			}

			formPanel.add(formViewPanel, BorderLayout.CENTER);
		}
		NavigationPanel navigationPanel = new NavigationPanel(model.getBackAction(), model
				.getForwardAction());
		
		if (document.numSections() > 1 && 
				model.getCurrentSectionIndex() != (document.numSections()-1))  {
			model.getForwardAction().setEnabled(true);
		} else {
			model.getForwardAction().setEnabled(false);
		}
		
		formPanel.add(navigationPanel, BorderLayout.SOUTH);
		formPanel.setOpaque(false);
		formScrollPane = createScrollPane(formPanel);

		SectionListener sectionChangedListener = new SectionAdapter() {
			@Override
			public void sectionChanged(SectionChangedEvent event) {
				copiedEntry = null;
				formPanel.remove(0);
				formViewPanel = (JPanel)formView.createPanel();
				formPanel.add(formViewPanel, 0);
				formPanel.revalidate();
				formPanel.repaint();
				if (((Document)document).getIsEditable()) {
					formViewPanel.addMouseListener(new RightClickMouseAdapter(true, isInElementViewMode));
				}
				else {
					formViewPanel.addMouseListener(new RightClickMouseAdapter(true, true));
				}
				for (int i=0; i<formViewPanel.getComponentCount(); i++) {
					if (((Document)document).getIsEditable()) {
						formViewPanel.getComponent(i).addMouseListener(new RightClickMouseAdapter(true, isInElementViewMode));	
					}
					else {
						formViewPanel.getComponent(i).addMouseListener(new RightClickMouseAdapter(true, true));
					}

				}
				
				//enabled forward button if there are more than one sections
				if (document.numSections() > 1 && 
						model.getCurrentSectionIndex() != (document.numSections()-1)) {
					model.getForwardAction().setEnabled(true);
				} else {
					model.getForwardAction().setEnabled(false);
				}
			}
		};    
		model.addSectionListener(sectionChangedListener);

		return formScrollPane;
	}

	public void renderPanel(final boolean noSectionDialog) {
		removeAll();
		model = new ApplicationModel();

		final Factory factory = new HibernateFactory(); 

		Document doc = document;
		if (doc == null || doc.numSections() == 0)  {
			add(new JPanel());
		} else {
			DocumentOccurrence docOcc = factory.createDocumentOccurrence("Preview Doc" + Math.random());
			doc.addOccurrence(docOcc);	

			Record record = new Record();
			record.setIdentifier(new Identifier());

			DocumentInstance docInstance = (DocumentInstance)doc.generateInstance(docOcc);
			docInstance.setRecord(record);
			docInstance.setStatus(factory.createStatus(Status.DOC_STATUS_DATASET_DESIGNER, -7));

			model.setCurrentRecord(record);
			model.setSelectedDocOccurrence(docOcc);
			model.setSelectedDocOccurrenceInstance(docInstance, org.psygrid.collection.entry.DocumentStatus.DATASET_DESIGNER);

			informationView = new DsdInformationView(model.getCurrentDocument()
					.getDisplayText() + EntryMessages.getString("Application.dash") + "Design View", 
					model,
					this);

			centerPanel = createMainPanel(null, createFormScrollPane(model));

			setLayout(new BorderLayout());
			add(informationView.getPane(), BorderLayout.NORTH);
			
			add(centerPanel, BorderLayout.CENTER);
		}

		if (preferredCenterCompSize != null) {
			updateCenterPanel(preferredCenterCompSize);
		}

		revalidate();
		repaint();
	}


	public void setPreferredCenterCompSize(final Dimension size) {
		this.preferredCenterCompSize = size;
		refresh(true);
	}


	public void updateCenterPanel(final Dimension size) {
		if (containerPanel != null ){
			if (containerPanel.getComponentCount() == 1){
				containerPanel.getComponent(0).setPreferredSize(size);
			} else if (containerPanel.getComponentCount() == 2){
				containerPanel.getComponent(1).setPreferredSize(size);
			}
			
			containerPanel.revalidate();
		}

		preferredCenterCompSize = size;
	}

	private JScrollPane createScrollPane(JPanel panel) {
		JScrollPane scrollPane = new JScrollPane(panel);
		scrollPane.getVerticalScrollBar().setUnitIncrement(10);
		return scrollPane;
	}

	public void dragEnter(DropTargetDragEvent dtde) {
	}

	public void dragExit(DropTargetEvent dte) {
	}

	public void dragOver(DropTargetDragEvent dtde) {
	}

	public void drop(DropTargetDropEvent e) {
		
		Point dropPoint = SwingUtilities.convertPoint((JComponent)e.getDropTargetContext().getComponent(), e.getLocation(), formViewPanel);
		
		if (DatasetController.getInstance().getActiveDs() != null &&
				!DatasetController.getInstance().getActiveDs().isReadOnly()
			 && ! (ElementUtility.isDocumentLocked(document) && !isDEL)	
			) {
			if (!((Document)this.document).getIsEditable()) {
				return;
			}
			currentSectionIndex = model.getCurrentSectionIndex();
			
			count = getIndexComponentAt((int)dropPoint.getX(), (int)dropPoint.getY());
			DataFlavor chosen = chooseDropFlavor(e);
			
			if (chosen == null) {
				e.rejectDrop();      	
				return;
			}

			Object data=null;
			try {
				/*
				 * the source listener receives this action in dragDropEnd.
				 * if the action is DnDConstants.ACTION_COPY_OR_MOVE then
				 * the source receives MOVE!
				 */
				e.acceptDrop(DnDConstants.ACTION_MOVE);

				data = e.getTransferable().getTransferData(chosen);
				if (data == null) {
					//do nothing
				}
			} catch ( Throwable t ) {
				t.printStackTrace();
				e.dropComplete(false);
				return;
			}

			Entry entry = null;

			//move this somewhere sensible later
			if (data.toString().equals("Text Entry")) {
//				Custom button text
				Object[] options = {PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.textentry.singleline"),
						PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.textentry.multiline"),
						PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.cancel")};
				int n = JOptionPane.showOptionDialog(frame,
						PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.textentry.textentrytype"),
						PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.textentry.textentrytypeheader"),
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null,
						options,
						options[2]);

				if (n == 0) {
					entry = ElementUtility.createTextEntry("");
				} else if (n ==1 ) {
					entry = ElementUtility.createLongTextEntry("");
				} 
			} else if (data.toString().equals("Option Entry")) {
				entry = ElementUtility.createOptionEntry("");
			} else if (data.toString().equals("Numeric Entry")) {
				Object[] options = {PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.numericentry.number"),
						PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.numericentry.integer"),
						PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.cancel")};
				int n = JOptionPane.showOptionDialog(frame,
						PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.textentry.typeofnumericentry"),
						PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.textentry.numericentrytypeheader"),
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null,
						options,
						options[2]);
				if (n == 0) {
					entry = ElementUtility.createNumericEntry("");
				} else if (n ==1 ) {
					entry = ElementUtility.createIntegerEntry("");
				} 

				
			} else if (data.toString().equals("Check Boxes")) {
				entry = ElementUtility.createBooleanEntry("");
			} else if (data.toString().equals("Calculated Entry")) {
				Object[] options = {PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.derivedentry.internal"),
						PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.derivedentry.external"),
						PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.cancel")};
				int n = JOptionPane.showOptionDialog(frame,
						PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.derived.typeofderivedentry"),
						PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.derived.derivedentrytypeheader"),
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null,
						options,
						options[2]);
				if (n == 0) {
					entry = ElementUtility.createDerivedEntry("");
				} else if (n ==1 ) {
					entry = ElementUtility.createExternalDerivedEntry("");
				} 
			} else if (data.toString().equals("Date Entry")) {
				entry = ElementUtility.createDateEntry("");
			} else if (data.toString().equals("Integer Entry")) {
				entry = ElementUtility.createIntegerEntry("");
			} else if (data.toString().equals("Long Text Entry")) {
				entry = ElementUtility.createLongTextEntry("");
			} else if (data.toString().equals(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.narrativeentry"))) {
				entry = ElementUtility.createNarrativeEntry("");
			} else if (data.toString().equals("Table Entry")) {
				entry = ElementUtility.createComposite("");
			} else if (data.toString().equals("External Calculated Entry")) {
				entry = ElementUtility.createExternalDerivedEntry("");
			} else if (data.toString().equals("Section")) {
				sectionEditDialog = new SectionEditDialog(frame);
				sectionEditDialog.addOkListener(new SectionOkListener());
				sectionEditDialog.setVisible(true);
			}

			if (entry != null ) {
				((Entry)entry).setIsEditable(true);
				currentIndex = count;
				shiftComponents(count, entry);
				showEntryEditDialog(entry);
			} 
			//new section is handled elswhere
			if (!data.toString().equals("Section")) {
				refresh(currentSectionIndex);
			}
		}
	}

	public void viewEntry(Entry entry, JDialog parent){
		showEntryEditDialog(entry, true, parent);
	}


	public void importEntry(Entry entry){
		Document doc = document;
		int sectionIndex = model.getCurrentSectionIndex();
		Section section = doc.getSection(sectionIndex);
		//Need to find out how many entries are in the current section.
		int numEntriesInCurrentSection = 0;
		for(int i = 0; i < doc.numEntries(); i++){
			if(doc.getEntry(i).getSection() == section){
				numEntriesInCurrentSection++;
			}
		}

		if (entry != null ) {
			currentIndex = count;
			shiftComponents(numEntriesInCurrentSection, entry);
		} 

		refresh();
	}

	public void prepareCompositeEntry(CompositeEntry compEntry){

		compEntry.setEntryStatus(EntryStatus.MANDATORY);

		if (model != null || document.numSections() != 0) {
			int secIndex = model.getCurrentSectionIndex();

			for(BasicEntry bE: compEntry.getEntries()){
				bE.setEntryStatus(EntryStatus.MANDATORY);
				bE.setSection(document.getSection(secIndex));
			}
		}

	}

	public Document getDocument() {
		return document;
	}

	public void importEntryNoRefresh(Entry entry){
		Document doc = document;
		int sectionIndex = model.getCurrentSectionIndex();
		Section section = doc.getSection(sectionIndex);
		//Need to find out how many entries are in the current section.
		int numEntriesInCurrentSection = 0;
		for(int i = 0; i < doc.numEntries(); i++){
			if(doc.getEntry(i).getSection() == section){
				numEntriesInCurrentSection++;
			}
		}

		if (entry != null ) {
			currentIndex = count;
			shiftComponents(numEntriesInCurrentSection, entry);
		} 

		DocTreeModel.getInstance().addEntry(entry, this.getDocument());
	}

	public void showEntryEditDialog(Entry entry){
		showEntryEditDialog(entry, false, null);
	}

	public void showEntryEditDialog(Entry entry, boolean viewDetachedEntry, JDialog parent) {
		Document doc = document;
		int numEntries = doc.numEntries();
		JDialog editDialog = null;
		boolean canEdit = true;
		if (viewDetachedEntry) {
			//Entries viewed from the DEL search dialog must not be editable
			canEdit = false;
		}
		else if (!((Entry)entry).getIsEditable()){
			//Entry is not editable
			canEdit = false;
		}
		else if (isDEL) {
			String authority = null;
			if (((Entry)entry).getLSID() != null) {
				authority = ((Entry)entry).getLSID().getAuthorityId();
			}
			//Only authors can edit entries in the DEL view
			canEdit = DELSecurity.getInstance().canEditElements(authority);
		}

		if (numEntries > 0 || viewDetachedEntry) {
			if (entry instanceof TextEntry) {
				editDialog = viewDetachedEntry ? new TextEditDialog(parent, (TextEntry)entry, null, isDEL, canEdit):
					new TextEditDialog(frame, (TextEntry)entry, isDEL, canEdit);
				if(!viewDetachedEntry){
					((AbstractEditDialog)editDialog).addOKListener(this);
					((AbstractEditDialog)editDialog).addRemoveListener(new RemoveListener());
				}
				editDialog.setVisible(true);
			} else if (entry instanceof OptionEntry) {
				editDialog = viewDetachedEntry ? new OptionEditDialog(parent, (OptionEntry)entry, null, isDEL, canEdit):
					new OptionEditDialog(frame, (OptionEntry)entry, isDEL, canEdit);
				if(!viewDetachedEntry){
					((AbstractEditDialog)editDialog).addOKListener(this);
					((AbstractEditDialog)editDialog).addRemoveListener(new RemoveListener());
				}
				editDialog.setVisible(true);
			} else if (entry instanceof IntegerEntry) {
				editDialog = viewDetachedEntry ?  new IntegerEditDialog(parent, (IntegerEntry)entry, null, isDEL, canEdit):
					new IntegerEditDialog(frame, (IntegerEntry)entry, isDEL, canEdit);
				if(!viewDetachedEntry){
					((AbstractEditDialog)editDialog).addOKListener(this);
					((AbstractEditDialog)editDialog).addRemoveListener(new RemoveListener());
				}
				editDialog.setVisible(true);
			} else if (entry instanceof NumericEntry) {
				editDialog = viewDetachedEntry ? new NumericEditDialog(parent, (NumericEntry)entry, doc, isDEL, canEdit):
					new NumericEditDialog(frame, (NumericEntry)entry, isDEL, canEdit);
				if(!viewDetachedEntry){
					((AbstractEditDialog)editDialog).addOKListener(this);
					((AbstractEditDialog)editDialog).addRemoveListener(new RemoveListener());
				}
				editDialog.setVisible(true);
			} else if (entry instanceof BooleanEntry) {
				editDialog = viewDetachedEntry ? new BooleanEditDialog(parent, (BooleanEntry)entry, null, isDEL, canEdit):
					new BooleanEditDialog(frame, (BooleanEntry)entry, isDEL, canEdit);
				if(!viewDetachedEntry){
					((AbstractEditDialog)editDialog).addOKListener(this);
					((AbstractEditDialog)editDialog).addRemoveListener(new RemoveListener());
				}
				editDialog.setVisible(true);
			} else if (entry instanceof LongTextEntry) {
				editDialog = viewDetachedEntry ? new LongTextEditDialog(parent, (LongTextEntry)entry, null, isDEL, canEdit):
					new LongTextEditDialog(frame, (LongTextEntry)entry, isDEL, canEdit);
				if(!viewDetachedEntry){
					((AbstractEditDialog)editDialog).addOKListener(this);
					((AbstractEditDialog)editDialog).addRemoveListener(new RemoveListener());
				}
				editDialog.setVisible(true);
			} else if (entry instanceof NarrativeEntry) {
				editDialog = viewDetachedEntry ? new NarrativeEditDialog(parent, (NarrativeEntry)entry, null, isDEL, canEdit):
					new NarrativeEditDialog(frame, (NarrativeEntry)entry, isDEL, canEdit);
				if(!viewDetachedEntry){
					((AbstractEditDialog)editDialog).addOKListener(this);
					((AbstractEditDialog)editDialog).addRemoveListener(new RemoveListener());
				}
				editDialog.setVisible(true);
			} else if (entry instanceof DateEntry) {
				editDialog = viewDetachedEntry ? new DateEditDialog(parent, (DateEntry)entry, null, isDEL, canEdit):
					new DateEditDialog(frame, (DateEntry)entry, isDEL, canEdit);
				if(!viewDetachedEntry){
					((AbstractEditDialog)editDialog).addOKListener(this);
					((AbstractEditDialog)editDialog).addRemoveListener(new RemoveListener());
				}
				editDialog.setVisible(true);
			} else if (entry instanceof CompositeEntry) {
				editDialog = viewDetachedEntry ? new CompositeEditDialog(parent, (CompositeEntry)entry, null, isDEL, canEdit):
					new CompositeEditDialog(frame, entry, isDEL, canEdit);
				if(!viewDetachedEntry){
					((AbstractEditDialog)editDialog).addOKListener(this);
					((AbstractEditDialog)editDialog).addRemoveListener(new RemoveListener());
				}
				editDialog.setVisible(true);
			} else if (entry instanceof DerivedEntry) {
				editDialog = viewDetachedEntry ? new DerivedEditDialog(parent, (DerivedEntry)entry, null, isDEL, canEdit):
					new DerivedEditDialog(frame, (DerivedEntry)entry, isDEL, canEdit);
				if(!viewDetachedEntry){
					((AbstractEditDialog)editDialog).addOKListener(this);
					((AbstractEditDialog)editDialog).addRemoveListener(new RemoveListener());
				}
				editDialog.setVisible(true);
			} else if (entry instanceof ExternalDerivedEntry) {
				editDialog = viewDetachedEntry ? new ExternalDerivedEditDialog(parent, (ExternalDerivedEntry)entry, isDEL, canEdit):
					new ExternalDerivedEditDialog(frame, (ExternalDerivedEntry)entry, isDEL, canEdit);
				if(!viewDetachedEntry){
					((AbstractEditDialog)editDialog).addOKListener(this);
					((AbstractEditDialog)editDialog).addRemoveListener(new RemoveListener());
				}
				editDialog.setVisible(true);
			}
		}

	}

	/**
	 * Called by drop
	 * Checks the flavors and operations
	 * @param e the DropTargetDropEvent object
	 * @return the chosen DataFlavor or null if none match
	 */
	private DataFlavor chooseDropFlavor(DropTargetDropEvent e) {
		if (e.isLocalTransfer() == true &&
				e.isDataFlavorSupported(StringTransferable.localStringFlavor)) {
			return StringTransferable.localStringFlavor;
		}
		DataFlavor chosen = null;      
		return chosen;
	}

	public void dropActionChanged(DropTargetDragEvent dtde) {
	}

	public void shiftComponents(int pos, Entry newComp) {
		if (model != null || document.numSections() != 0) {
			int secIndex = model.getCurrentSectionIndex();

			//if composite, ensure all entries contained within are set
			if (newComp instanceof CompositeEntry) {
				for (int i=0; i<((CompositeEntry)newComp).numEntries(); i++) {
					Entry entry = ((CompositeEntry)newComp).getEntry(i);
					entry.setSection(document.getSection(secIndex));
				}
			}

			newComp.setSection(document.getSection(secIndex));
		} else {
			model = new ApplicationModel();
			if (document.numSections() == 0) {
				//first require the user to create a section
				JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.sectionfirst"));
				return;
			}
		}

		document.insertEntry(newComp, pos);
	}

	public void insertSection(int pos, 
			String sectionName, 
			String sectionDisplayText,
			String sectionDescriptionText,
			boolean isMultipleSectionsAllowed) {
		Section section = ElementUtility.createISection(sectionName,
                sectionDisplayText,
                sectionDescriptionText,  isMultipleSectionsAllowed);
		document.addSection(section);

		if (document.numEntries() > 0 ) {
			if (document.numSections() != 0) {
				Section  currentSection = document.getSection(currentSectionIndex);

				//go back and set as far as this section is used
				for (int i=pos-1; i>=0; i--) {
					Entry entry = document.getEntry(i);
					if(entry.getSection() == currentSection){

						//if composite, ensure all entries contained within are set
						if (entry instanceof CompositeEntry) {
							for (int j=0; j<((CompositeEntry)entry).numEntries(); j++) {
								Entry basicentry = ((CompositeEntry)entry).getEntry(j);
								basicentry.setSection(section);
							}
						}

						entry.setSection(section);
					} else {
						break;
					}
				}
			}
		}

		for (int i=0; i<document.numSections(); i++) {
			if (document.getSection(i).equals(section)) {
				refresh(i);
			}
		}

		model.setSection(currentSectionIndex);
	}

	public boolean isFirstInSection(int entryIndex) {
		Section  entrySection = document.getEntry(entryIndex).getSection();
		if (entryIndex <= 0){
			return true;
		}
		Section  prevSection = document.getEntry(entryIndex-1).getSection();
		if (entrySection.equals(prevSection)) {
			return false;
		}
		return true;
	}

	public boolean isLastInSection(int entryIndex) {
		Section  entrySection = document.getEntry(entryIndex).getSection();
		if (entryIndex >= document.numEntries()-1){
			return true;
		}
		Section  nextSection = document.getEntry(entryIndex+1).getSection();
		if (entrySection.equals(nextSection)) {
			return false;
		}
		return true;
	}

	public int getCurrentSection() {
		return model.getCurrentSectionIndex();
	}

	public void refresh() {
		renderPanel();
	}

	public void refresh(int i) {
		refresh();
		model.setSection(i);
	}

	public void refresh(boolean noSection) {
		renderPanel(noSection);
	}

	public boolean isInElementViewMode() {
		return isInElementViewMode;
	}

	public void showSectionEditor() {
		if(!this.isInElementViewMode) {
			SectionControlDialog sectionControlDialog = new SectionControlDialog(frame, document, this);
			sectionControlDialog.setVisible(true);
		}
	}

	private int getIndexComponentAt(int x, int y) {
		JComponent targetComponent = (JComponent)formViewPanel.getComponentAt(x, y);
		
		//index of the clicked component in the container panel
		int foundIndex = getFoundIndex(targetComponent, y);

		int entryIndex = -1;
		String entryName = "";

		//index in to the current section (add number of preceeding entries)
		Section  curSection = (Section)document.getSection(model.getCurrentSectionIndex());
		
		if (foundIndex != -1) {
			for (int j=foundIndex; j<formViewPanel.getComponentCount(); j++) {
				if (formViewPanel.getComponent(j) instanceof  DividerLabel) {
					DividerLabel divLabel = (DividerLabel)formViewPanel.getComponent(j);
					entryName = divLabel.getText();
					break;
				}
			}
		}
			
		int lastInSection = 0; 

		//if still no entry has been found, set it to be the last in the current section
		//this needs to be done before indexing into the section below
		if (entryIndex == -1) {
			for (int i=0; i<document.numEntries(); i++) {
				if (document.getEntry(i).getSection().equals(curSection)){
					if (!(document.getEntry(i).isLocked())) {
						entryIndex++;
						lastInSection = i;
					}
				}
			}
			//add one extra for the new entry
			entryIndex++;
		}
		
		
		//get the index of the found entry in the document
		//do this by matching divider entry label to 
		//the uniquely named entry in the document
		for (int i=0; i<document.numEntries(); i++) {
			if (document.getEntry(i).getName().equals(entryName)){
				if (lastInSection == i) {
					entryIndex = i+1;
				} else {
					entryIndex = i;
				}
				break;
			}
		}
		

		//index in to the current section (add number of preceeding entries)
		for (int i=0; i<document.numEntries(); i++) {
			if (!document.getEntry(i).getSection().equals(curSection)){
				if (!document.getEntry(i).isLocked()) {
					entryIndex ++;
				}
			//stop when you find the current section
			} else {
				break;
			}
		}
			
		return entryIndex;
	}
	
	private int getFoundIndex(JComponent clickedComponent, int eventY) {
		// if not clicked on component within the specific renderers, find closest match
		if (clickedComponent==formViewPanel) {
			for (int i=0; i<formViewPanel.getComponentCount(); i++) {
				if (formViewPanel.getComponent(i).getY() >= eventY) {
					if (formViewPanel.getComponent(i) != formPanel) {
						clickedComponent = (JComponent)formViewPanel.getComponent(i);
					} 
					break;
				}
			}
		}

		int foundIndex = -1;

		for (int i=0; i<formViewPanel.getComponentCount(); i++) {
			Object formViewComponent = formViewPanel.getComponent(i);

			if (formViewComponent instanceof org.psygrid.collection.entry.ui.TextEntryField) {
				formViewComponent = ((org.psygrid.collection.entry.ui.TextEntryField)formViewComponent).getTextComponent();
			}

			if (formViewComponent.equals(clickedComponent)){
				foundIndex = i;
			}
		}
		
		return foundIndex;
	}

	private class RightClickMouseAdapter extends MouseAdapter {

		private boolean showViewOptionsOnly = false;

		public RightClickMouseAdapter() {
		}

		public RightClickMouseAdapter(boolean isComponent, boolean showViewOptionsOnly) {
			this.showViewOptionsOnly = showViewOptionsOnly;
		}

		public void mouseClicked(MouseEvent evt) {

			//do nothing in read only mode
			if (DatasetController.getInstance().getActiveDs().isReadOnly()
					//if document is locked, can't edit either
					|| (ElementUtility.isDocumentLocked(document) && !isDEL())
			) {
				return;
			}

			if (SwingUtilities.isRightMouseButton(evt))
			{
				int entryIndex = -1;
				int lastEntryInSection = -1;
				int foundIndex = getFoundIndex((JComponent)evt.getSource(), evt.getY());

				String entryName = "";
				if (foundIndex != -1) {
					for (int j=foundIndex; j<formViewPanel.getComponentCount(); j++) {
						if (formViewPanel.getComponent(j) instanceof  DividerLabel) {
							DividerLabel divLabel = (DividerLabel)formViewPanel.getComponent(j);
							entryName = divLabel.getText();
							break;
						}
					}

					//get the index of the found entry
					if (entryIndex == -1) {
						for (int i=0; i<document.numEntries(); i++) {
							if (document.getEntry(i).getName().equals(entryName)){
								entryIndex = i;
								break;
							}
						}
					}
				} else {
					Section  curSection = (Section)document.getSection(model.getCurrentSectionIndex());
					for (int i=0; i<document.numEntries(); i++) {
						if (!(document.getEntry(i).isLocked())) {
							if (document.getEntry(i).getSection().equals(curSection)){
								lastEntryInSection = i;
							}
						}
					}
					//last entry in section is still -1, so no entries in section
					if (lastEntryInSection == -1) {
						lastEntryInSection = 0;
					}
				}

				//if found, an entry exists for this so put at the bottom of the section
				if (foundIndex == -1){
					//only show paste entry if there is something copied
					if (copiedEntry !=null) {
						JPopupMenu editOrDeleteMenu = new JPopupMenu();
						editOrDeleteMenu.add(new JMenuItem(new PasteAction(lastEntryInSection)));
						editOrDeleteMenu.show((Component)evt.getSource(), evt.getX(), evt.getY());
					}
				} else {
					JPopupMenu editOrDeleteMenu = new JPopupMenu();

					Entry e = (Entry)document.getEntry(entryIndex);
					//if(this.showViewOptionsOnly || !e.getIsEditable()) {
					if(e.getIsEditable()) {
						editOrDeleteMenu.add(new JMenuItem(new EditEntryAction(entryIndex)));
					}else {
						editOrDeleteMenu.add(new JMenuItem(new ViewEntryAction(entryIndex)));
					}

					if(!this.showViewOptionsOnly){
						editOrDeleteMenu.add(new JMenuItem(new DeleteEntryAction(entryIndex)));
					}

					if(!this.showViewOptionsOnly) {
						if (!(document.getEntry(entryIndex) instanceof ExternalDerivedEntry)) {
							editOrDeleteMenu.add(new JSeparator());
							editOrDeleteMenu.add(new JMenuItem(new CopyAction(entryIndex)));
						}
					}

					if (copiedEntry !=null && !this.showViewOptionsOnly) {
						editOrDeleteMenu.add(new JMenuItem(new PasteAction(entryIndex)));
					}

					if(!this.showViewOptionsOnly){
						editOrDeleteMenu.add(new JSeparator());
						if (isFirstInSection(entryIndex)) {
							editOrDeleteMenu.add(new JMenuItem(new MoveUpAction(entryIndex))).setEnabled(false);
						} else {
							editOrDeleteMenu.add(new JMenuItem(new MoveUpAction(entryIndex))).setEnabled(true);
						}
						if (isLastInSection(entryIndex)) {
							editOrDeleteMenu.add(new JMenuItem(new MoveDownAction(entryIndex))).setEnabled(false);
						} else {
							editOrDeleteMenu.add(new JMenuItem(new MoveDownAction(entryIndex))).setEnabled(true);
						}
					}

					editOrDeleteMenu.show((Component)evt.getSource(), evt.getX(), evt.getY());
				}
			}
		}

		public void mousePressed(MouseEvent evt) {
		}
	}

	private class SectionOkListener implements ActionListener {

		public void actionPerformed(ActionEvent evt) {

			insertSection(count, sectionEditDialog.getName(), 
					sectionEditDialog.getDisplayText(),
					sectionEditDialog.getDescriptionText(),
					sectionEditDialog.isMulitpleSectionsAllowed());
			((Document)document).setIsRevisionCandidate(true);
			sectionEditDialog.dispose();
		}
	}


	private class CopyAction extends AbstractAction {
		private int index;

		public CopyAction(int index) {
			super("Copy Entry");
			this.index = index;
		}

		public void actionPerformed(ActionEvent evt) {
			Document doc = document;
			copiedEntry = doc.getEntry(index);
		}
	}

	private class PasteAction extends AbstractAction {
		private int index;

		public PasteAction(int index) {
			super("Paste Entry");
			this.index = index;
		}

		public void actionPerformed(ActionEvent evt) {
			Document doc = document;
			Entry pastedEntry = DocTreeModel.getInstance().copyEntry(doc, copiedEntry, true);
			doc.insertEntry(pastedEntry, index+1);
			DocTreeModel.getInstance().addEntry(pastedEntry, doc);
			((Document)document).setIsRevisionCandidate(true);
			refresh(getCurrentSection());
		}
	}

	/**
	 * Delete the entry in the document panel
	 * 
	 * - clean up any references contained in derived entries
	 * - if entry is contained in external derived entries,
	 *  do not delete but warn the user
	 * - clean up any reference to this entry contained within
	 * the option dependents of other entries
	 * 
	 */
	private class DeleteEntryAction extends AbstractAction {

		/** The index of the entry in the document */
		private int index;

		/**
		 * Constructor 
		 * 
		 * @param index the index of the entry in the document to delete
		 */
		public DeleteEntryAction(int index) {
			super("Delete Entry");
			this.index = index;
		}

		/**
		 * Delete the entry and associated references 
		 */
		public void actionPerformed(ActionEvent evt) {
			
			/**
			 * The document to which this entry belongs (open document
			 * in the panel)
			 */
			Document doc = document;
			
			/** 
			 * If index is exceeds max entries in doc, set to delete the last 
			 * entry in the document
			 */
			if (index >= doc.numEntries()) {
				index = doc.numEntries()-1;
			}

			/**
			 * Get the entry itself from the document
			 */
			Entry entry = doc.getEntry(index);

			//clean up references first for Derived Entry
			for (int j=0; j<doc.numEntries(); j++) {
				Entry curEntry = doc.getEntry(j);
				if (curEntry instanceof DerivedEntry) {
					ArrayList<String> varNames = new ArrayList(((DerivedEntry)curEntry).getVariableNames());
					for (int z=varNames.size()-1; z>=0; z--) {
						if ((((DerivedEntry)curEntry).getVariable(varNames.get(z))).equals(entry)) {
							JOptionPane.showMessageDialog(new JFrame(), PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.removingderivedwarn"));
							return;
						}
					}
				}
			}

			//warn the user that document is used by
			// External Derived Entry and do not delete it
			for (int j=0; j<doc.numEntries(); j++) {
				Entry curEntry = doc.getEntry(j);
				if (curEntry instanceof ExternalDerivedEntry) {
					ArrayList<String> varNames = new ArrayList<String>(((ExternalDerivedEntry)curEntry).getVariableNames());
					for (int z=varNames.size()-1; z>=0; z--) {
						if ((((ExternalDerivedEntry)curEntry).getVariable(varNames.get(z))).equals(entry)) {
							JOptionPane.showMessageDialog(new JFrame(), PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.removingextderivedwarn"));
							return;
						}
					}
				}
			}

			//clean up the option dependencies
			for (int z=0; z<doc.numEntries(); z++) {
				Entry curEntry = doc.getEntry(z);
				if (curEntry instanceof OptionEntry) {
					int numOptions = ((OptionEntry)curEntry).numOptions();
					for (int y=0; y<numOptions; y++) {
						Option option = ((OptionEntry)curEntry).getOption(y);
						for (int h=option.numOptionDependents()-1; h>=0; h--) {
							OptionDependent dep = option.getOptionDependent(h);
							if ((entry.equals(dep.getDependentEntry()))) {
								option.removeOptionDependent(h);
							}
						}
					}
				}
			}

			// Delete the entry from the tree model 
			DocTreeModel.getInstance().deleteEntry(entry, document);
			
			if (DatasetController.getInstance().getActiveDs().getDs().isPublished()) {
				entry.setLocked(true);
				new ProvenanceDialog(frame, DatasetController.getInstance().getActiveDs().getDs(), AuditableChange.ACTION_DELETE);
			} else {
				doc.removeEntry(index);
			}
			
			((Document)document).setIsRevisionCandidate(true);
			refresh(getCurrentSection());
		}
	}

	private class EditEntryAction extends AbstractAction {

		private int index;

		public EditEntryAction(int index) {
			super ("Edit Entry");
			this.index = index;
		}

		public void actionPerformed(ActionEvent evt) {

			int currentSectionIndex = model.getCurrentSectionIndex();

			if (evt.getSource() instanceof AbstractEditDialog) {
				refresh(currentSectionIndex);
				return;
			}

			Document doc = (Document)document;
			int numEntries = doc.numEntries();
			JDialog editDialog;

			if (index >= doc.numEntries()) {
				index = doc.numEntries()-1;
			}

			//Only authors can edit entries in the DEL view
			boolean canEdit = true;
			if (isDEL) {
				String authority = null;
				if (document instanceof DummyDocument) {
					Entry onlyEntry = doc.getEntry(0);
					if (onlyEntry.getLSID() != null) {
						authority = onlyEntry.getLSID().getAuthorityId();
					}
				}
				else if (doc.getLSID() != null) {
					//Check that the user is an author in the authority that the document belongs too
					authority = doc.getLSID().getAuthorityId();
				}
				canEdit = DELSecurity.getInstance().canEditElements(authority);
			}

			if (numEntries > 0 ) {
				Entry entry = doc.getEntry(index);
				if (!((Entry)entry).getIsEditable()){
					//Entry is not editable
					canEdit = false;
				}

				if (entry instanceof TextEntry) {
					editDialog = new TextEditDialog(frame, (TextEntry)entry, isDEL, canEdit);
					((AbstractEditDialog)editDialog).addOKListener(this);
					editDialog.setVisible(true);
				} else if (entry instanceof OptionEntry) {
					editDialog = new OptionEditDialog(frame, (OptionEntry)entry, isDEL, canEdit);
					((AbstractEditDialog)editDialog).addOKListener(this);
					editDialog.setVisible(true);
				} else if (entry instanceof NumericEntry) {
					editDialog = new NumericEditDialog(frame, (NumericEntry)entry, isDEL, canEdit);
					((AbstractEditDialog)editDialog).addOKListener(this);
					editDialog.setVisible(true);
				} else if (entry instanceof IntegerEntry) {
					editDialog = new IntegerEditDialog(frame, (IntegerEntry)entry, isDEL, canEdit);
					((AbstractEditDialog)editDialog).addOKListener(this);
					editDialog.setVisible(true);
				} else if (entry instanceof BooleanEntry) {
					editDialog = new BooleanEditDialog(frame, (BooleanEntry)entry, isDEL, canEdit);
					((AbstractEditDialog)editDialog).addOKListener(this);
					editDialog.setVisible(true);
				} else if (entry instanceof LongTextEntry) {
					editDialog = new LongTextEditDialog(frame, (LongTextEntry)entry, isDEL, canEdit);
					((AbstractEditDialog)editDialog).addOKListener(this);
					editDialog.setVisible(true);
				} else if (entry instanceof NarrativeEntry) {
					editDialog = new NarrativeEditDialog(frame, (NarrativeEntry)entry, isDEL, canEdit);
					((AbstractEditDialog)editDialog).addOKListener(this);
					editDialog.setVisible(true);
				} else if (entry instanceof DateEntry) {
					editDialog = new DateEditDialog(frame, (DateEntry)entry, isDEL, canEdit);
					((AbstractEditDialog)editDialog).addOKListener(this);
					editDialog.setVisible(true);
				} else if (entry instanceof CompositeEntry) {
					editDialog = new CompositeEditDialog(frame, (CompositeEntry)entry, isDEL, canEdit);
					((AbstractEditDialog)editDialog).addOKListener(this);
					editDialog.setVisible(true);
				} else if (entry instanceof DerivedEntry) {
					editDialog = new DerivedEditDialog(frame, (DerivedEntry)entry, isDEL, canEdit);
					((AbstractEditDialog)editDialog).addOKListener(this);
					editDialog.setVisible(true);
				} else if (entry instanceof ExternalDerivedEntry) {
					editDialog = new ExternalDerivedEditDialog(frame, (ExternalDerivedEntry)entry, isDEL, canEdit);
					((AbstractEditDialog)editDialog).addOKListener(this);
					editDialog.setVisible(true);
				}
			}
		}
	}

	private class MoveUpAction extends AbstractAction {

		final private int index;

		public MoveUpAction(int index) {
			super("Move Up");
			this.index = index;
		}

		public void actionPerformed(ActionEvent aet) {
			document.moveEntry(index, index -1);
			refresh(getCurrentSection());
			DocTreeModel.getInstance().refreshDocument(document);
			((Document)document).setIsRevisionCandidate(true);
		}


	}

	private class MoveDownAction extends AbstractAction {

		final private int index;

		public MoveDownAction(int index) {
			super("Move Down");
			this.index = index;
		}

		public void actionPerformed(ActionEvent aet) {
			document.moveEntry(index, index+1);
			refresh(getCurrentSection());
			DocTreeModel.getInstance().refreshDocument(document);
			((Document)document).setIsRevisionCandidate(true);
		}
	}


	private class ViewEntryAction extends AbstractAction {

		private int index;

		public ViewEntryAction(int index) {
			super ("View Entry");
			this.index = index;
		}

		public void actionPerformed(ActionEvent evt) {

			int currentSectionIndex = model.getCurrentSectionIndex();

			if (evt.getSource() instanceof AbstractEditDialog) {
				refresh(currentSectionIndex);
				return;
			}

			Document doc = (Document)document;
			int numEntries = document.numEntries();
			JDialog editDialog;

			if (index >= document.numEntries()) {
				index = document.numEntries()-1;
			}

			//Only authors can edit entries in the DEL view
			boolean canEdit = true;

			//Check that the user is an author in the authority that the document belongs too
			if (isDEL) {
				String authority = null;
				if (document instanceof DummyDocument) {
					Entry onlyEntry = doc.getEntry(0);
					if (onlyEntry.getLSID() != null) {
						authority = onlyEntry.getLSID().getAuthorityId();

					}
				}
				else if (doc.getLSID() != null) {
					authority = doc.getLSID().getAuthorityId();
				}
				canEdit = DELSecurity.getInstance().canEditElements(authority);
			}

			if (numEntries > 0 ) {
				Entry entry = document.getEntry(index);
				if (!((Entry)entry).getIsEditable()){
					//Entry is not editable
					canEdit = false;
				}
				if (entry instanceof TextEntry) {
					if (parentDialog != null) {
						editDialog = new TextEditDialog(parentDialog, (TextEntry)entry, document, isDEL, canEdit);
					} else {
						editDialog = new TextEditDialog(frame, (TextEntry)entry, isDEL, canEdit);
					}
					((AbstractEditDialog)editDialog).addOKListener(this);
					editDialog.setVisible(true);
				} else if (entry instanceof OptionEntry) {
					if (parentDialog != null) {
						editDialog = new OptionEditDialog(parentDialog, (OptionEntry)entry, document, isDEL, canEdit);
					} else {
						editDialog = new OptionEditDialog(frame, (OptionEntry)entry, isDEL, canEdit);
					}
					((AbstractEditDialog)editDialog).addOKListener(this);
					editDialog.setVisible(true);
				} else if (entry instanceof NumericEntry) {
					if (parentDialog != null) {
						editDialog = new NumericEditDialog(parentDialog, (NumericEntry)entry, document, isDEL, canEdit);
					} else {
						editDialog = new NumericEditDialog(frame, (NumericEntry)entry, isDEL, canEdit);
					}
					((AbstractEditDialog)editDialog).addOKListener(this);
					editDialog.setVisible(true);
				} else if (entry instanceof IntegerEntry) {
					if (parentDialog != null) {
						editDialog = new IntegerEditDialog(parentDialog, (IntegerEntry)entry, document, isDEL, canEdit);
					} else {
						editDialog = new IntegerEditDialog(frame, (IntegerEntry)entry, isDEL, canEdit);
					}
					((AbstractEditDialog)editDialog).addOKListener(this);
					editDialog.setVisible(true);
				} else if (entry instanceof BooleanEntry) {
					if (parentDialog != null) {
						editDialog = new BooleanEditDialog(parentDialog, (BooleanEntry)entry, document, isDEL, canEdit);
					} else {
						editDialog = new BooleanEditDialog(frame, (BooleanEntry)entry, isDEL, canEdit);
					}
					((AbstractEditDialog)editDialog).addOKListener(this);
					editDialog.setVisible(true);
				} else if (entry instanceof LongTextEntry) {
					if (parentDialog != null) {
						editDialog = new LongTextEditDialog(parentDialog, (LongTextEntry)entry, document, isDEL, canEdit);
					} else {
						editDialog = new LongTextEditDialog(frame, (LongTextEntry)entry, isDEL, canEdit);
					}
					((AbstractEditDialog)editDialog).addOKListener(this);
					editDialog.setVisible(true);
				} else if (entry instanceof NarrativeEntry) {
					if (parentDialog != null) {
						editDialog = new NarrativeEditDialog(parentDialog, (NarrativeEntry)entry, document, isDEL, canEdit);
					} else {
						editDialog = new NarrativeEditDialog(frame, (NarrativeEntry)entry, isDEL, canEdit);
					}
					((AbstractEditDialog)editDialog).addOKListener(this);
					editDialog.setVisible(true);
				} else if (entry instanceof DateEntry) {
					if (parentDialog != null) {
						editDialog = new DateEditDialog(parentDialog, (DateEntry)entry, document, isDEL, canEdit);
					} else {
						editDialog = new DateEditDialog(frame, (DateEntry)entry, isDEL, canEdit);
					}
					((AbstractEditDialog)editDialog).addOKListener(this);
					editDialog.setVisible(true);
				} else if (entry instanceof CompositeEntry) {
					if (parentDialog != null) {
						editDialog = new CompositeEditDialog(parentDialog, (CompositeEntry)entry, document, isDEL, canEdit);
					} else {
						editDialog = new CompositeEditDialog(frame, (CompositeEntry)entry, isDEL, canEdit);
					}
					((AbstractEditDialog)editDialog).addOKListener(this);
					editDialog.setVisible(true);
				} else if (entry instanceof DerivedEntry) {
					if (parentDialog != null) {
						editDialog = new DerivedEditDialog(parentDialog, (DerivedEntry)entry, document, isDEL, canEdit);
					} else {
						editDialog = new DerivedEditDialog(frame, (DerivedEntry)entry, isDEL, canEdit);
					}
					((AbstractEditDialog)editDialog).addOKListener(this);
					editDialog.setVisible(true);
				} else if (entry instanceof ExternalDerivedEntry) {
					if (parentDialog != null) {
						editDialog = new ExternalDerivedEditDialog(parentDialog, (ExternalDerivedEntry)entry, isDEL, canEdit);
					} else {
						editDialog = new ExternalDerivedEditDialog(frame, (ExternalDerivedEntry)entry, isDEL, canEdit);
					}
					((AbstractEditDialog)editDialog).addOKListener(this);
					editDialog.setVisible(true);
				}
			}
		}
	}

	public void actionPerformed(ActionEvent aet) {
		refresh();
	}

	
	/**
	 * Fired when cancel is called after dropping the edit dialog
	 * @author pwhelan
	 */
	private class RemoveListener implements ActionListener {

		public void actionPerformed(ActionEvent aet) {
			if (currentIndex != -1) {
				document.removeEntry(currentIndex);
				currentIndex = -1;
			}
		}
	}

	public boolean isDEL() {
		return isDEL;
	}

}

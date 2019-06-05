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


package org.psygrid.collection.entry.chooser;

import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.table.TableCellRenderer;

import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.DocumentStatus;
import org.psygrid.collection.entry.ExceptionsHelper;
import org.psygrid.collection.entry.persistence.ExternalIdGetter;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.collection.entry.util.ResetWaitRunnable;
import org.psygrid.collection.entry.util.WaitRunnable;

public class DocInstanceChooserPanel extends ChooserPanel {
	private static final long serialVersionUID = 1930755107130839753L;

	protected JPanel statusSelectionPanel;
	protected JComboBox statusList;

	protected final static String STATUS_FILTER_ALL = Messages.getString("DocInstanceChooserPanel.all");

	public DocInstanceChooserPanel(Application application, ChooserDialog dialog) {
		super(application, dialog);
	}

	@Override
	protected void processSelectionAction(int row, Choosable selectedValue) {
		ChoosableType type = selectedValue.getType();
		switch(type) {
		case DATASET:
			super.setDataset(((ChoosableDataSet)selectedValue).getDataSet());
			activateStatusDropdown();
		case DOCUMENT_STATUS:
		case RECORD:
		case DOCUMENT_GROUP:
			loadChoosable(row);
			break;
		default: throw new IllegalArgumentException("Unexpected type: " + type); //$NON-NLS-1$
		}
	}

	@Override
	protected TableCellRenderer getTableCellRenderer() {
		return new DocInstanceChooserTableRenderer();
	}

	@Override
	protected JPanel createStatusSelectionPanel() {
		statusSelectionPanel = new JPanel(new FlowLayout());
		JLabel statusDesc = new JLabel(Messages.getString("DocInstanceChooserPanel.statusfilter"));

		statusList = new JComboBox();
		statusList.addItem(STATUS_FILTER_ALL);
		statusList.setEditable(false);
		statusList.setEnabled(false);
		statusList.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if (e.getItem().equals(STATUS_FILTER_ALL)) {
						filterStatus(null);
					}
					else {
						filterStatus((String)e.getItem());
					}
				}
			}
		});

		statusSelectionPanel.add(statusDesc);
		statusSelectionPanel.add(statusList);
		statusSelectionPanel.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 2));
		return statusSelectionPanel;
	}

	protected void activateStatusDropdown(){
		statusList.removeAllItems();
		List<String> statuses = new ArrayList<String>(); 
		for (DocumentStatus status : DocumentStatus.getUserVisible(
				getDataset().isNoReviewAndApprove(),
				PersistenceManager.getInstance().getData().isAlwaysOnlineMode())) {
			statuses.add(status.toStatusLongName());
		}
		Collections.sort(statuses);
		statuses.add(0, STATUS_FILTER_ALL);
		for ( String status: statuses ){
			statusList.addItem(status);
		}
		statusList.setEnabled(true);
	}

	@Override
	protected void resetStatusSelectionPanel() {
		Choosable p = model.getCurrentTableModel().parent;
		if ( p instanceof ChoosableList ){
			statusList.removeAllItems();
			statusList.addItem(STATUS_FILTER_ALL);
			statusList.setEnabled(false);
		}
	}

	/**
	 * Filter the chooser items by the given status..
	 * @param status
	 */
	public void filterStatus(String status) {
		new WaitRunnable(this.dialog).run();
		Choosable parent = model.getCurrentTableModel().parent;
		DocumentStatus docStatus = DocumentStatus.fromStatusLongName(status);

		if (parent instanceof ChoosableList) {
			//Currently displaying list of datasets
		}
		else if (parent instanceof ChoosableDataSet) {
			//Currently displaying records within a dataset
			//refetch identifiers and filter on status

			//ignores all identifiers with no documents in the given status
			//if the status is null, select all identifiers (with or without documents)
			try {
				List<String> identifiers = ChooserHelper.loadChoosableRecords(application, ((ChoosableDataSet)parent), ((ChoosableDataSet)parent), docStatus, true);	

				((ChoosableDataSet)parent).setChildren(new ArrayList<Choosable>());
				for (String identifier: identifiers) {
					
					String theIdentifier = identifier;
					
					if(dataset.getUseExternalIdAsPrimary() == true){
						
						theIdentifier = ExternalIdGetter.get(identifier);
						
					}
					
					RemoteChoosableRecord record = new RemoteChoosableRecord(theIdentifier, identifier, docStatus, true, (AbstractChoosableWithChildren)parent);
					((ChoosableDataSet)parent).addChild(record);
				}

				model.setParentTableModel();
				if (((ChoosableDataSet)parent).getParent() != null) {
					int counter = 0;
					for (Choosable c: ((ChoosableDataSet)parent).getParent().getChildren()) {
						if (c == parent) {
							model.loadChoosable(counter);
							break;
						}
						counter++;
					}
				}
				model.getCurrentTableModel().fireTableDataChanged();
			}
			catch (Exception e) {
				ExceptionsHelper.handleException(getParent(), "Problem Occurred", null, "Unable to update the table for the participant.", false);
			}
		}
		else if (parent instanceof RemoteChoosableRecord) {
			//Currently displaying document groups for a record
			//TODO update list of document groups and document instances
		}
		else if (parent instanceof ChoosableDocInstanceGroup) {
			//Currently displaying document instances for a record
			ChoosableDocInstanceGroup cdg = (ChoosableDocInstanceGroup)parent;
			try {
				//TODO update model when list of doc instances is changed.
				if (status.equals(STATUS_FILTER_ALL)) {

				}
				else {
					List<ChoosableDocInstance> list = new ArrayList<ChoosableDocInstance>();
					for (ChoosableDocInstance inst: cdg.getChildren()) {
						if (status.equals(inst.getDocInstance().getStatus().getLongName())) {
							list.add(inst);
						}
						//TODO account for locally incomplete and pending approval documents
					}
					cdg.setChildren(list);
				}
				model.getCurrentTableModel().fireTableDataChanged();
			}
			catch (Exception e) {
				ExceptionsHelper.handleException(getParent(), "Problem Occurred", null, "Unable to update the table for the participant.", false);
			}
		}
		new ResetWaitRunnable(this.dialog).run();
	}
}

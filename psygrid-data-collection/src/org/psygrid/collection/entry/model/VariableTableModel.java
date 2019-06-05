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


package org.psygrid.collection.entry.model;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.psygrid.collection.entry.DocumentStatus;
import org.psygrid.collection.entry.Icons;
import org.psygrid.collection.entry.event.TableChangeRequestedEvent;
import org.psygrid.collection.entry.event.TableChangeRequestedListener;
import org.psygrid.collection.entry.renderer.RendererData.EditableStatus;
import org.psygrid.data.model.hibernate.CompositeEntry;
import org.psygrid.data.model.hibernate.CompositeResponse;
import org.psygrid.data.model.hibernate.DocumentInstance;
import org.psygrid.data.model.hibernate.StandardCode;

public class VariableTableModel extends EntryTableModel    {

	private final List<RemoveRowAction> removeRowActions;
	private final Action addRowAction;

	public VariableTableModel(CompositeEntry entry, CompositeResponse response,
			SectionPresModel sectionOccPresModel, boolean copy,
			DocumentInstance docInstance, List<StandardCode> standardCodes) {
		super(entry, response, sectionOccPresModel, copy, docInstance,
				standardCodes);
		if (entry.numRowLabels() > 0) {
			throw new IllegalArgumentException("entry cannot have any row labels"); //$NON-NLS-1$
		}
		addRowAction = new AbstractAction(org.psygrid.collection.entry.ui.Messages.getString("VariableTableModel.newRow")) { //$NON-NLS-1$

			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				DocumentStatus docStatus = DocumentStatus.valueOf(getDocInstance().getStatus());
				if (docStatus == DocumentStatus.REJECTED || docStatus == DocumentStatus.CONTROLLED ){
					fireRowAddedRequestedEvent(numRows, EditableStatus.TRUE);
					return;
				}
				addRow(null, EditableStatus.DEFAULT);
			}
		};
		removeRowActions = new ArrayList<RemoveRowAction>();
	}

	public Action getAddRowAction() {
		return addRowAction;
	}

	@Override
	public void init() {
		int totalRows = response.numCompositeRows();

		super.init();

		for (int i = 0; i < totalRows; ++i) {
			addRow(i, null, EditableStatus.DEFAULT);
		}

		if (totalRows == 0) {
			addRow(null, EditableStatus.DEFAULT);
		}
	}

	public RemoveRowAction getRemoveRowAction(int rowIndex) {
		return removeRowActions.get(rowIndex);
	}

	public List<RemoveRowAction> getRemoveRowActions(){
		return Collections.unmodifiableList(removeRowActions);
	}

	private void updateRemoveRowActionsIndices(int rowIndex) { 
		for (int i = rowIndex, c = removeRowActions.size(); i < c; ++i) {
			RemoveRowAction rowAction = removeRowActions.get(i);
			rowAction.setRowIndex(i);
		}
	}

	public void processRemoveRowActionsStatus() {
		if (removeRowActions.size() == 1) {
			removeRowActions.get(0).setEnabled(false);
		}
		if (removeRowActions.size() == 2) {
			removeRowActions.get(0).setEnabled(true);
		}
	}

	@Override
	public void addRow(int rowIndex, String comment, EditableStatus editable) {
		removeRowActions.add(new RemoveRowAction(rowIndex));

		processRemoveRowActionsStatus();

		super.addRow(rowIndex, comment, editable);
	}

	@Override
	public void removeRow(int rowIndex, String comment) {
		removeRowActions.remove(rowIndex);
		updateRemoveRowActionsIndices(rowIndex);
		processRemoveRowActionsStatus();

		super.removeRow(rowIndex, comment);
	}

	public void addTableChangeRequestedListener(TableChangeRequestedListener listener) {
		listenerList.add(TableChangeRequestedListener.class, listener);
	}

	public void removeTableChangeRequestedListener(TableChangeRequestedListener listener) {
		listenerList.remove(TableChangeRequestedListener.class, listener);
	}

	protected void fireRowAddedRequestedEvent(int rowIndex, EditableStatus status) {
		TableChangeRequestedEvent event = new TableChangeRequestedEvent(this,
				TableChangeRequestedEvent.Type.INSERT, rowIndex, status);
		fireRowChangeRequestedEvent(event);
	}

	protected void fireRowDeleteRequestedEvent(int rowIndex) {
		TableChangeRequestedEvent event = new TableChangeRequestedEvent(this,
				TableChangeRequestedEvent.Type.DELETE, rowIndex);
		fireRowChangeRequestedEvent(event);
	}

	protected void fireRowChangeRequestedEvent(TableChangeRequestedEvent event) {
		Object[] listeners = listenerList.getListenerList();

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TableChangeRequestedListener.class) {
				((TableChangeRequestedListener) listeners[i + 1]).changeRequested(event);
			}
		}
	}

	public class RemoveRowAction extends AbstractAction {

		private static final long serialVersionUID = 1L;
		private int rowIndex;

		public RemoveRowAction(int rowIndex) {
			this.rowIndex = rowIndex;
			putValue(Action.SMALL_ICON, Icons.getInstance().getIcon("trash")); //$NON-NLS-1$
			putValue(Action.SHORT_DESCRIPTION, 
					Messages.getString("EntryTableModel.removeRowAction")); //$NON-NLS-1$
		}

		public void setRowIndex(int rowIndex) {
			this.rowIndex = rowIndex;
		}

		public void actionPerformed(ActionEvent e) {
			DocumentStatus docStatus = DocumentStatus.valueOf(getDocInstance().getStatus());
			if (docStatus == DocumentStatus.REJECTED || docStatus == DocumentStatus.CONTROLLED ){
				fireRowDeleteRequestedEvent(rowIndex);
				return;
			}
			removeRow(rowIndex);
		}
	}

	@Override
	protected int getStartColumn() {
		return 0;
	}

	public void reset() {
		//remove all but the first row, and clear the first row
		for (int i = numRows - 1; i > 0; --i) {
			removeRow(i);
		}
		for ( List<BasicPresModel> presModels: childPresModels){
			for ( BasicPresModel presModel: presModels ){
				presModel.reset();
			}
		}
	}

	public void touch() {
		// TODO Auto-generated method stub
		
	}
}

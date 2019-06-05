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

import javax.swing.table.TableCellRenderer;

import org.psygrid.collection.entry.Application;
import org.psygrid.data.model.hibernate.DataSet;

public class DocChooserPanel extends ChooserPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DocChooserPanel(Application application, ChooserDialog dialog) {
		super(application, dialog);
	}

	@Override
	public void init(ChoosableList choosableList) {
		Object type = choosableList.getType();
		if (type != null && type instanceof ChoosableType == false) {
			throw new IllegalArgumentException("choosableList#getType() must " + //$NON-NLS-1$
					"return an enum of type DocChoosableType, but it returns: " //$NON-NLS-1$
					+ type.getClass());
		}
		super.init(choosableList);
	}

	@Override
	protected void processSelectionAction(int row, Choosable selectedValue) {
		ChoosableType type = selectedValue.getType();
		switch(type) {
		case DATASET:
			DataSet dataSet = ((ChoosableDataSet) selectedValue).getDataSet();
			DataSetSelectedEvent dsEvent = new DataSetSelectedEvent(this, dataSet);
			fireDataSetSelected(dsEvent);
			break;
		case DOCUMENT_GROUP:
			loadChoosable(row);
			break;

		case DOCUMENT_OCCURRENCE:
			DocOccurrenceSelectedEvent event = new DocOccurrenceSelectedEvent(this, (ChoosableDocOccurrence) selectedValue);
			fireDocOccurrenceSelected(event);
			break;
		}
	}

	public void addDocOccurrenceSelectedListener(
			DocOccurrenceSelectedListener listener) {
		listenerList.add(DocOccurrenceSelectedListener.class, listener);
	}

	public void addDataSetSelectedListener(
			DataSetSelectedListener listener) {
		listenerList.add(DataSetSelectedListener.class, listener);
	}

	public void removeDataSetSelectedListener(
			DataSetSelectedListener listener) {
		listenerList.remove(DataSetSelectedListener.class, listener);
	}

	public void removeDocOccurrenceSelectedListener(
			DocOccurrenceSelectedListener listener) {
		listenerList.remove(DocOccurrenceSelectedListener.class, listener);
	}

	protected void fireDocOccurrenceSelected(DocOccurrenceSelectedEvent event) {
		Object[] listeners = listenerList.getListenerList();

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == DocOccurrenceSelectedListener.class) {
				((DocOccurrenceSelectedListener) listeners[i + 1])
				.docSelected(event);
			}
		}
	}

	protected void fireDataSetSelected(DataSetSelectedEvent event) {
		Object[] listeners = listenerList.getListenerList();

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == DataSetSelectedListener.class) {
				((DataSetSelectedListener) listeners[i + 1])
				.docSelected(event);
			}
		}
	}

	@Override
	protected TableCellRenderer getTableCellRenderer() {
		return new DocStatusChooserTableRenderer();
	}
}

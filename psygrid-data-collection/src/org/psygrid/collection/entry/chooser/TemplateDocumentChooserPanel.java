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

/**
 * @author Rob Harper
 *
 */
public class TemplateDocumentChooserPanel extends ChooserPanel {

	private static final long serialVersionUID = -6148087938574417514L;

	public TemplateDocumentChooserPanel(Application application, ChooserDialog dialog) {
		super(application, dialog);
	}
	
	@Override
	protected TableCellRenderer getTableCellRenderer() {
		return new DocStatusChooserTableRenderer();
	}

	@Override
	protected void processSelectionAction(int row, Choosable selectedValue) {
		ChoosableType type = selectedValue.getType();
		switch(type) {
		case DATASET:
			loadChoosable(row);
			break;
		case DOCUMENT_GROUP:
			loadChoosable(row);
			break;

		case DOCUMENT_OCCURRENCE:
			TemplateDocOccurrenceSelectedEvent event = new TemplateDocOccurrenceSelectedEvent(this, (ChoosableDocOccurrence) selectedValue);
			fireDocOccurrenceSelected(event);
			break;
		}
	}

	public void addDocOccurrenceSelectedListener(
			TemplateDocOccurrenceSelectedListener listener) {
		listenerList.add(TemplateDocOccurrenceSelectedListener.class, listener);
	}

	public void removeDocOccurrenceSelectedListener(
			TemplateDocOccurrenceSelectedListener listener) {
		listenerList.remove(TemplateDocOccurrenceSelectedListener.class, listener);
	}

	protected void fireDocOccurrenceSelected(TemplateDocOccurrenceSelectedEvent event) {
		Object[] listeners = listenerList.getListenerList();

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TemplateDocOccurrenceSelectedListener.class) {
				((TemplateDocOccurrenceSelectedListener) listeners[i + 1])
				.docSelected(event);
			}
		}
	}
}

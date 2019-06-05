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


package org.psygrid.collection.entry.renderer;

import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.JLabel;

import org.psygrid.collection.entry.DocumentStatus;
import org.psygrid.collection.entry.Fonts;
import org.psygrid.collection.entry.event.EditEvent;
import org.psygrid.collection.entry.event.EditListener;
import org.psygrid.collection.entry.event.RendererCreatedEvent;
import org.psygrid.collection.entry.model.CompositePresModel;
import org.psygrid.collection.entry.model.EntryTableModel;
import org.psygrid.collection.entry.model.FixedTableModel;
import org.psygrid.collection.entry.model.ResponsePresModel;
import org.psygrid.collection.entry.model.SectionPresModel;
import org.psygrid.collection.entry.model.VariableTableModel;
import org.psygrid.collection.entry.ui.EntryLabel;
import org.psygrid.collection.entry.ui.EntryTable;
import org.psygrid.collection.entry.ui.FixedTable;
import org.psygrid.collection.entry.ui.VariableTable;
import org.psygrid.data.model.hibernate.*;

public class CompositeRendererSPI implements RendererSPI  {

	public boolean canHandle(Entry model, Entry parent) {
		if (model instanceof CompositeEntry) {
			return true;
		}
		return false;
	}

	public Renderer getRenderer(RendererData rendererData) {
		CompositeEntry compositeEntry = (CompositeEntry) rendererData.getModel();

		if (compositeEntry.numEntries() == 0) {
			throw new IllegalArgumentException("A Composite Entry must have 1 or " + //$NON-NLS-1$
			"more basic entries."); //$NON-NLS-1$
		}
		String displayText = 
			RendererHelper.getInstance().concatEntryLabelAndDisplayText(compositeEntry);
		EntryLabel title = new EntryLabel(displayText);
		title.setFont(Fonts.getInstance().getBoldLabelFont());

		SectionPresModel sectionPresModel = rendererData.getSectionOccPresModel();
		DocumentInstance docInstance = rendererData.getDocOccurrenceInstance();

		CompositeResponse compResponse = (CompositeResponse) RendererHelper.getInstance().
		getResponse(docInstance, compositeEntry, sectionPresModel);

		if (compResponse == null) {
			compResponse = (CompositeResponse) RendererHelper.getInstance().generateInstance(
					compositeEntry, sectionPresModel);
			docInstance.addResponse(compResponse);
		}

		JLabel validationLabel = new JLabel();

		EntryTableModel tableModel;
		EntryTable table;
		boolean copy = rendererData.isCopy();
		List<StandardCode> standardCodes = rendererData.getStandardCodes();
		if (compositeEntry.numRowLabels() > 0) {
			tableModel = new FixedTableModel(compositeEntry, compResponse, 
					rendererData.getSectionOccPresModel(), copy, docInstance,
					standardCodes);
			table = new FixedTable(tableModel, 
					rendererData.getRendererHandler(),
					validationLabel);
		}
		else {
			tableModel = new VariableTableModel(compositeEntry, compResponse,
					rendererData.getSectionOccPresModel(), copy, docInstance,
					standardCodes);
			table = new VariableTable((VariableTableModel) tableModel, 
					rendererData.getRendererHandler(),
					validationLabel);
		}
		table.init();
		Status status = rendererData.getDocOccurrenceInstance().getStatus();
		RendererHelper.getInstance().processEntryStatus(title, tableModel, table, copy, 
				status, rendererData.isEditable());
		RendererHelper.getInstance().processDescription(null, compositeEntry, title);
		RendererHelper.getInstance().processValidation(tableModel, validationLabel, null);
		PresModelRenderer<? extends ResponsePresModel> renderer = 
			new CompositeRenderer<CompositePresModel>(validationLabel, title,
					table, tableModel);

		RendererHandler handler = rendererData.getRendererHandler();
		if (status != null) {
			DocumentStatus docStatus = DocumentStatus.valueOf(status);
			if (DocumentStatus.DATASET_DESIGNER != docStatus ) {
				MouseListener changeResponseListener = 
					RendererHelper.getInstance().getChangeResponseStatusListener(renderer, title, docStatus);
				title.addMouseListener(changeResponseListener);
				for (JLabel label : table.getHeaderLabels()) {
					label.addMouseListener(changeResponseListener);
				}
			} else if (DocumentStatus.valueOf(status) == DocumentStatus.REJECTED || 
					   DocumentStatus.valueOf(status) == DocumentStatus.CONTROLLED ) {
				handler.addEditListener(new CompositeEditListener(tableModel));
			}
		}
		int rowIndex = rendererData.getRowIndex();
		if (!copy) {
			handler.putRenderer(compositeEntry, rowIndex, renderer);
		}
		handler.fireRendererCreatedEvent(new RendererCreatedEvent(this, renderer));
		return renderer;

	}

	static final class CompositeEditListener implements EditListener  {

		private final EntryTableModel tableModel;
		public CompositeEditListener(EntryTableModel tableModel) {
			this.tableModel = tableModel;
		}

		public void editOccurred(EditEvent event) {
			BasicEntry entry = event.getCurrentPresModel().getEntry();
			if (!tableModel.containsEntry(entry)) {
				return;
			}
			tableModel.markAsEdited("This table has been edited.");
		}
	}
}

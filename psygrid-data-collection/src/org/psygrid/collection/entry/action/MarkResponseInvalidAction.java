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

package org.psygrid.collection.entry.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JFrame;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingworker.SwingWorker;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.SwingWorkerExecutor;
import org.psygrid.collection.entry.model.BasicPresModel;
import org.psygrid.collection.entry.model.CompositePresModel;
import org.psygrid.collection.entry.model.FixedTableModel;
import org.psygrid.collection.entry.model.ResponsePresModel;
import org.psygrid.collection.entry.remote.MarkResponseAsInvalidWorker;
import org.psygrid.collection.entry.renderer.PresModelRenderer;
import org.psygrid.collection.entry.renderer.RendererHelper;
import org.psygrid.collection.entry.ui.AnnotationDialog;
import org.psygrid.data.model.hibernate.BasicEntry;
import org.psygrid.data.model.hibernate.ResponseStatus;

/**
 * @author Rob Harper
 *
 */
public class MarkResponseInvalidAction extends MarkResponseAction {

	private static final long serialVersionUID = 1L;

	private static final Log LOG = LogFactory.getLog(MarkResponseInvalidAction.class);

	private MarkResponseValidAction validAction;

	public MarkResponseInvalidAction(
			PresModelRenderer<? extends ResponsePresModel> renderer) {
		super(renderer, Messages.getString("ChangeResponseStatusAction.markAsInvalid"));
	}

	public void actionPerformed(ActionEvent e) {
		SwingWorker<Object, Object> markAsInvalidWorker = getMarkAsInvalidWorker();
		
		if (null != markAsInvalidWorker) {
			SwingWorkerExecutor.getInstance().execute(markAsInvalidWorker);
		}
		
	}

	public MarkResponseValidAction getValidAction() {
		return validAction;
	}

	public void setValidAction(MarkResponseValidAction validAction) {
		this.validAction = validAction;
	}

	private SwingWorker<Object, Object> getMarkAsInvalidWorker() {
		try {
			JFrame frame = RendererHelper.getInstance().findJFrameFromRenderer(getRenderer());
			if (frame == null || (!(frame instanceof Application))) {
				if (LOG.isWarnEnabled()) {
					String string = frame == null ? "null" : frame.toString();
					LOG.warn("Frame received is not of type Application, " + string);
				}
			}
			AnnotationDialog dialog = new AnnotationDialog(frame);
			dialog.setVisible(true);
			String annotation = dialog.getAnnotation();
			if (annotation == null) {
				annotation = "";
			}
			if (!dialog.isOkSelected()) {
				return null;
			}
			MarkResponseAsInvalidWorker worker = new MarkResponseAsInvalidWorker(
					getPresModel(), annotation, this);

			return worker;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	public void changeState(String annotation) {
		//Bug #1147: if a table is selected mark its entries as invalid, but not the table itself.
		if (getPresModel() instanceof CompositePresModel) {
			
			int startIndex = 0;
			
			if(getPresModel() instanceof FixedTableModel){
				//Bug#1422 - need to differentiate between a FixedTabelModel and a CompositePresModel.
				//If it's a fixed table model we do NOT want to do anything to the fixed label entry.
				startIndex = 1;
			}
			
			CompositePresModel cpm = (CompositePresModel)getPresModel();
			for (int i = startIndex; i < cpm.getEntry().numEntries(); i++) {
				BasicEntry entry = cpm.getEntry().getEntry(i);
				for (BasicPresModel basicPresModel: cpm.getPresModelsForEntry(entry)) {
					putValue(Action.NAME, Messages.getString("ChangeResponseStatusAction.markAsInvalid"));
					basicPresModel.getResponse().setAnnotation(annotation);
					basicPresModel.getResponseStatusModel().setValue(ResponseStatus.FLAGGED_INVALID);
					this.setEnabled(false);
					validAction.setEnabled(true);
				}
			}
			return;			
		}

		putValue(Action.NAME, Messages.getString("ChangeResponseStatusAction.markAsInvalid"));
		getPresModel().getResponse().setAnnotation(annotation);
		getPresModel().getResponseStatusModel().setValue(ResponseStatus.FLAGGED_INVALID);
		this.setEnabled(false);
		validAction.setEnabled(true);
	}

}

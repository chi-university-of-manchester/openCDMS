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

import org.psygrid.collection.entry.SwingWorkerExecutor;
import org.psygrid.collection.entry.model.ResponsePresModel;
import org.psygrid.collection.entry.remote.MarkResponseAsValidWorker;
import org.psygrid.collection.entry.renderer.PresModelRenderer;
import org.psygrid.data.model.hibernate.ResponseStatus;

/**
 * @author Rob Harper
 *
 */
public class MarkResponseValidAction extends MarkResponseAction {

	private static final long serialVersionUID = 1L;

	private MarkResponseInvalidAction invalidAction;
	
	public MarkResponseValidAction(
            PresModelRenderer<? extends ResponsePresModel> renderer) {
        super(renderer, Messages.getString("ChangeResponseStatusAction.markAsValid"));
    }

	public void actionPerformed(ActionEvent e) {
		SwingWorkerExecutor.getInstance().execute(new MarkResponseAsValidWorker(getPresModel(), this));
	}

    public void changeState() {
        putValue(Action.NAME, Messages.getString("ChangeResponseStatusAction.markAsValid"));
        getPresModel().getResponse().setAnnotation("");
        getPresModel().getResponseStatusModel().setValue(ResponseStatus.NORMAL);
        this.setEnabled(false);
        invalidAction.setEnabled(true);
    }

	public MarkResponseInvalidAction getInvalidAction() {
		return invalidAction;
	}

	public void setInvalidAction(MarkResponseInvalidAction invalidAction) {
		this.invalidAction = invalidAction;
	}
    
}

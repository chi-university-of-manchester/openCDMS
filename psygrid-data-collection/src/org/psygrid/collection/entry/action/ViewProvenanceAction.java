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

import javax.swing.AbstractAction;

import org.psygrid.collection.entry.model.ResponsePresModel;
import org.psygrid.collection.entry.renderer.PresModelRenderer;
import org.psygrid.collection.entry.renderer.RendererHelper;
import org.psygrid.collection.entry.ui.ViewProvenanceDialog;

/**
 * @author Rob Harper
 *
 */
public class ViewProvenanceAction extends AbstractAction {

	private static final long serialVersionUID = 2283976031477623403L;

	private final PresModelRenderer<? extends ResponsePresModel> renderer;
    
    public ViewProvenanceAction(
            PresModelRenderer<? extends ResponsePresModel> renderer) {
        super(Messages.getString("ViewProvenanceAction.name"));
        this.renderer = renderer;
    }

	public void actionPerformed(ActionEvent e) {
		ViewProvenanceDialog dialog = 
			new ViewProvenanceDialog(renderer.getPresModel().getResponse(), 
					RendererHelper.getInstance().findJFrameFromRenderer(renderer));
		dialog.setVisible(true);
	}

}
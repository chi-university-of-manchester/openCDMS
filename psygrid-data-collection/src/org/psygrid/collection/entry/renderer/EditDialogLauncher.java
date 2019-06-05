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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

import org.psygrid.collection.entry.DocumentStatus;
import org.psygrid.collection.entry.event.PostEditDialogBuiltEvent;
import org.psygrid.collection.entry.event.PostEditDialogBuiltListener;
import org.psygrid.collection.entry.ui.EditDialog;

public final class EditDialogLauncher extends MouseAdapter {
    private final RendererData rendererData;

    private final BasicRenderer<?> renderer;

    private final PostEditDialogBuiltListener postBuiltListener;

    private final DocumentStatus docStatus;
    
    public EditDialogLauncher(RendererData rendererData,
            BasicRenderer<?> renderer, PostEditDialogBuiltListener postBuiltListener, 
            DocumentStatus docStatus) {
        this.rendererData = rendererData;
        this.renderer = renderer;
        this.postBuiltListener = postBuiltListener;
        this.docStatus = docStatus;
    }

    @Override
    public final void mouseClicked(MouseEvent e) {
    	if ( MouseEvent.BUTTON3 == e.getButton()){
    		//right click - show history
    		JPopupMenu popupMenu = RendererHelper.getInstance().createRightClickMenu(renderer, docStatus);
    		popupMenu.show(e.getComponent(), e.getX(), e.getY());
    		return;
    	}
    	
        if (e.getClickCount() == 2 ) {
            final EditDialog editDialog = new EditDialog(rendererData, renderer);
            if (postBuiltListener != null) {
                PostEditDialogBuiltListener listener = new PostEditDialogBuiltListener() {
                    public void postBuilt(PostEditDialogBuiltEvent event) {
                        editDialog.removePostEditDialogBuildEvent(this);
                        postBuiltListener.postBuilt(event);
                    }
                };
                editDialog.addPostEditDialogBuildEvent(listener);
            }
            editDialog.build();
            editDialog.setVisible(true);
        }
    }
}
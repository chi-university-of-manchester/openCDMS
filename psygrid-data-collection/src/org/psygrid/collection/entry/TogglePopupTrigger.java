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


package org.psygrid.collection.entry;

import java.awt.Component;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

import org.psygrid.collection.entry.event.ContextualMenuMouseAdapter;

public class TogglePopupTrigger extends ContextualMenuMouseAdapter {
    private final JPopupMenu popup;
    
    /**
     * 
     * @param popup
     * @param triggerCheckComp triggerCheckComp#isEnabled() will be called as one
     * of the factors to determine if the popup will be trigggered. If null, then
     * the source of the event will be used for this.
     */
    public TogglePopupTrigger(final JPopupMenu popup, Component triggerCheckComp) {
        super(triggerCheckComp);
        this.popup = popup;
    }
    
    @Override
    protected void showContextualMenu(MouseEvent event) {
        popup.show(event.getComponent(), event.getX(), event.getY());
    }
}

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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

public class TogglePopupButtonTrigger extends MouseAdapter    {
    
    private JPopupMenu popup;
    
    public TogglePopupButtonTrigger(JPopupMenu popup) {
        this.popup = popup;
    }
    
    @Override
    public void mousePressed(MouseEvent me) {
        if (me.getComponent().isEnabled()) {
            int x = me.getX();
            int y = me.getY();
            popup.show(me.getComponent(), x, y);
        }
    }
    
}

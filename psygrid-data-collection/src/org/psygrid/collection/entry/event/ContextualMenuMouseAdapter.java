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

package org.psygrid.collection.entry.event;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public abstract class ContextualMenuMouseAdapter extends MouseAdapter {
    private boolean forwardToReleased = false;
    
    private final Component triggerCheckComp;
    
    /**
     * @param triggerCheckComp triggerCheckComp#isEnabled() will be called as one
     * of the factors to determine if the popup will be trigggered. If null, then
     * the source of the event will be used for this.
     */
    public ContextualMenuMouseAdapter(Component triggerCheckComp) {
        this.triggerCheckComp  = triggerCheckComp;
    }
    
    private boolean shouldTrigger(MouseEvent me) {
        Component comp = triggerCheckComp;
        if (comp == null)
            comp = me.getComponent();
        
        //TODO This used to be comp.isEnabled && me.isPopupTrigger but was changed
        //as part of fix for bug 621, not exactly sure why
        return me.isPopupTrigger();
    }
    
    protected abstract void showContextualMenu(MouseEvent event);
    
    @Override
    public final void mousePressed(MouseEvent me) {
        if (shouldTrigger(me)) {
            showContextualMenu(me);
            return;
        }
        forwardToReleased = true;
    }
    
    @Override
    public final void mouseReleased(MouseEvent me) {
        if (!forwardToReleased)
            return;
        
        forwardToReleased = false;
        if (shouldTrigger(me))
            showContextualMenu(me);
    }
}

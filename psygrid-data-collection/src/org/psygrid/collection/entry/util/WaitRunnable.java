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


package org.psygrid.collection.entry.util;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Window;

import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.ui.InfiniteProgressPanel;

/**
 * A Runnable that sets the cursor of Application to
 * <code>Cursor.WAIT_CURSOR</code>, adds a progress animation to the
 * GlassPane and blocks input. The #run method is always executed
 * in the event thread, so there's no need to call EventQueue#invokeLater.
 * 
 * <p>Note that it's important for ResetWaitRunnable to be called after
 * the operations is finished in order for the input to be restored.</p>
 * 
 * @author Ismael Juma (ismael@juma.me.uk)
 * @see ResetWaitRunnable
 * @see InfiniteProgressPanel
 * @see Component#setCursor(Cursor)
 * @see Cursor#WAIT_CURSOR
 * @see #run()
 * @see Application
 * 
 */
public final class WaitRunnable implements Runnable    {

    private final Window window;
    
    /**
     * Creates an instance of this object.
     * 
     * @param window The Component to set the cursor to 
     * <code>Cursor#WAIT_CURSOR</code>.
     * the cursor is in the {@code Cursor#WAIT_CURSOR} state or <code>null</code>.
     */
    public WaitRunnable(Window window) {
        if (window == null)
            throw new IllegalArgumentException("container cannot be null"); //$NON-NLS-1$
        this.window = window;
    }
    
    /**
     * See {@link WaitRunnable}.
     */
    public void run() {
        if (EventQueue.isDispatchThread()) {
            internalRun();
        } else {
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    internalRun();
                }
            });
        }
    }

    private void internalRun() {
        Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
        window.setCursor(waitCursor);
//      Disabled due to CPU hogging
//      window.getGlassPane().start();
    }
}

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
 * A Runnable that resets the cursor, removes the progress animation from
 * the GlassPane and restores input. The #run method is always executed
 * in the event thread, so there's no need to call EventQueue#invokeLater.
 * 
 * @author Ismael Juma (ismael@juma.me.uk)
 * 
 * @see WaitRunnable
 * @see InfiniteProgressPanel
 * @see Application
 * @see Component#setCursor(Cursor)
 * @see Cursor#getDefaultCursor()
 * @see #run()
 *
 */
public final class ResetWaitRunnable implements Runnable  {

    private final Window window;
    
    /**
     * Creates an instance of this object.
     * 
     * @param window The Component to set the cursor to the default.
     */
    public ResetWaitRunnable(Window window) {
        this.window = window;
    }
    
    /**
     * See {@link ResetWaitRunnable}.
     */
    public void run() {
        /* 
         * Always call invokeLater so that we eliminate the possibility of
         * running this before a queued WaitRunnable
         */
        EventQueue.invokeLater(new Runnable() {
            public void run() {
//Disabled due to CPU hogging            	
//              window.getGlassPane().stop();
                Cursor normalCursor = Cursor.getDefaultCursor();
                window.setCursor(normalCursor);
            }
        });
    }
}

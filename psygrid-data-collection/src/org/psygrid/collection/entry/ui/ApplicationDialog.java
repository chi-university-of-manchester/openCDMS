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
package org.psygrid.collection.entry.ui;

import javax.swing.JDialog;

import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.renderer.RendererHelper;
import org.psygrid.collection.entry.util.ResetWaitRunnable;
import org.psygrid.collection.entry.util.WaitRunnable;

public class ApplicationDialog extends JDialog implements ProgressWindow    {

    private static final long serialVersionUID = 1L;
    
    private Application parentApplication;
    private ApplicationDialog parentDialog;
    
    public ApplicationDialog(Application parent, boolean modal) {
        this(parent, null, modal);
        this.parentApplication = parent;
    }
    
    public ApplicationDialog(Application parent, String title, boolean modal) {
        super(parent, title, modal);
        this.parentApplication = parent;
        setGlassPane(RendererHelper.getInstance().createInfiniteProgressPanel());
    }
    
    public ApplicationDialog(ApplicationDialog dialog, Application parent, String title, boolean modal) {
        super(dialog, title, modal);
        this.parentApplication = parent;
        this.parentDialog = dialog;
        setGlassPane(RendererHelper.getInstance().createInfiniteProgressPanel());
    }
    
    public final void setWait(boolean b) {
        if (b) {
            new WaitRunnable(this).run();
            if ( null != parentApplication ){
            	new WaitRunnable(parentApplication).run();
            }
            if ( null != parentDialog ){
            	new WaitRunnable(parentDialog).run();
            }
        }
        else {
            new ResetWaitRunnable(this).run();
            if ( null != parentApplication ){
            	new ResetWaitRunnable(parentApplication).run();
            }
            if ( null != parentDialog ){
            	new ResetWaitRunnable(parentDialog).run();
            }
        }
    }
    
    @Override
    public final InfiniteProgressPanel getGlassPane() {
    	return (InfiniteProgressPanel) super.getGlassPane();
    }
    
    @Override
    public final Application getParent() {
        return parentApplication;
    }

    public final ApplicationDialog getParentDialog(){
    	return parentDialog;
    }
}

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

import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.SwingWorkerExecutor;
import org.psygrid.collection.entry.chooser.LoadReportsChooserLoader;
import org.psygrid.common.ui.WrappedJOptionPane;

public final class GenerateReportAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final Application application;
    public GenerateReportAction(Application application) {
        super("Report...");
        this.application = application;
    }
    
    public void actionPerformed(ActionEvent event) {
    	if (!application.isOnline()) {
    		String title = Messages.getString("GenerateReportAction.offlinetitle");
    		String message = Messages.getString("GenerateReportAction.offlinemessage");
			WrappedJOptionPane.showWrappedMessageDialog(application, message, title, WrappedJOptionPane.INFORMATION_MESSAGE);
			return;
		}
        SwingWorkerExecutor.getInstance().execute(
                new LoadReportsChooserLoader(application));
    }
}

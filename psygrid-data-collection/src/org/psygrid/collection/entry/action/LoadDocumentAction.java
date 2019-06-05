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
import org.psygrid.collection.entry.EntryHelper;
import org.psygrid.collection.entry.SwingWorkerExecutor;
import org.psygrid.collection.entry.chooser.LoadDocumentChooserLoader;

/**
 * @author Rob Harper
 *
 */
public class LoadDocumentAction extends AbstractAction {

	private static final long serialVersionUID = -3445211576658530254L;

	private Application application;
	
	public LoadDocumentAction(Application application) {
        super(Messages.getString("LoadDocumentAction.openParticipant")); //$NON-NLS-1$
        this.application = application;
    }

	public void actionPerformed(ActionEvent arg0) {
        if (application.getModel().getCurrentDocOccurrence() != null) {
            if (!EntryHelper.showDocumentWillBeLostDialog(application)) {
                return;
            }
        }
        
        SwingWorkerExecutor.getInstance().execute(
                new LoadDocumentChooserLoader(application));
	}

}

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
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.EntryHelper;
import org.psygrid.collection.entry.EntryMessages;
import org.psygrid.collection.entry.ExceptionsHelper;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.collection.entry.remote.RemoteUpdateAction;
import org.psygrid.collection.entry.util.WaitRunnable;
import org.psygrid.common.ui.WrappedJOptionPane;

/**
 * @author Rob Harper
 *
 */
public class RebuildLocalCachesAction extends AbstractAction {

	private static final long serialVersionUID = -1302630566861366394L;

	private Application application;
	
	public RebuildLocalCachesAction(Application application){
		super("Rebuild Local Caches...");
		this.application = application;
	}
	

	public void actionPerformed(ActionEvent e) {
		if (!application.isOnline()) {
			String title = EntryMessages.getString("RebuildLocalCachesAction.offlinetitle");
    		String message = EntryMessages.getString("RebuildLocalCachesAction.offlinemessage");
			WrappedJOptionPane.showWrappedMessageDialog(application, message, title, WrappedJOptionPane.INFORMATION_MESSAGE);	
			return;
		}
    	EntryHelper.runWhenNoUncommittedRecords(application, "", 
    		new Runnable() {
                public void run() {
                    doRebuild();
                }
        	},
    		null);

	}
	
	private void doRebuild(){
		try{
			new WaitRunnable(application).run();
			PersistenceManager.getInstance().deleteLocalCaches();
			RemoteUpdateAction action = new RemoteUpdateAction( application ) {
				private static final long serialVersionUID = 1L;

				@Override
				protected void showSuccessMessage() {
					successMessage();
				}

			};
			action.actionPerformed(new ActionEvent(application, 
					ActionEvent.ACTION_PERFORMED, "")); //$NON-NLS-1$
		}
		catch(IOException ex){
			ExceptionsHelper.handleIOException(application, ex, true);
		}
	}

	private void successMessage(){
		String title = Messages.getString("RebuildLocalCachesAction.successtitle"); //$NON-NLS-1$
		String message = Messages.getString("RebuildLocalCachesAction.successmessage"); //$NON-NLS-1$
		WrappedJOptionPane.showWrappedMessageDialog(application, message, title, 
				JOptionPane.INFORMATION_MESSAGE);
	}
}

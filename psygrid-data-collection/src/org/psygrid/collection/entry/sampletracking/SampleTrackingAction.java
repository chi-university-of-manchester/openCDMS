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


package org.psygrid.collection.entry.sampletracking;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.remote.RemoteManager;
import org.psygrid.data.model.hibernate.Record;

public class SampleTrackingAction extends AbstractAction {

	private static final long serialVersionUID = 2283976031477623403L;

	private final static Log LOG = LogFactory.getLog(SampleTrackingAction.class);
	
	private Application application;

    public SampleTrackingAction(Application application) {
        super(Messages.getString("SampleTrackingAction.name"));
		this.application = application;
    }

	public void actionPerformed(ActionEvent e) {
		SampleListDialog dialog = new SampleListDialog(application);
		dialog.doModal();
	}
	
	public static boolean isTrackingEnabled(Record record){
		boolean result = false;
		try {
			return RemoteManager.getInstance().getSampleConfig(record.getDataSet()).isTracking();
		} catch (Exception e1) {
			// Not a major problem so just log it for now.
			LOG.warn("Problem accessing sampletracking configuration",e1);
		}
		return result;
	}

}

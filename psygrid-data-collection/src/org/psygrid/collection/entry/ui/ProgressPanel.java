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

import javax.swing.JPanel;

import org.psygrid.collection.entry.event.JobEvent;
import org.psygrid.collection.entry.event.JobListener;

public class ProgressPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public void addJobListener(JobListener listener) {
        listenerList.add(JobListener.class, listener);
    }
    
    protected void fireJobStartedEvent() {
        fireJobEvent(true);
    }
    
    protected void fireJobFinishedEvent() {
        fireJobEvent(false);
    }
    
    private void fireJobEvent(boolean started) {
        JobEvent event = null;
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == JobListener.class) {
                if (event == null) {
                    event = new JobEvent(this);
                }
                JobListener jobListener = (JobListener) listeners[i + 1];
                if (started) {
                    jobListener.jobStarted(event);
                } else {
                    jobListener.jobFinished(event);
                }
            }
        }
    }
    
    public void removeJobListener(JobListener listener) {
        listenerList.add(JobListener.class, listener);
    }
}

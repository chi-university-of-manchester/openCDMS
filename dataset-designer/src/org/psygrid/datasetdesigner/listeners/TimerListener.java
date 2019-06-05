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
package org.psygrid.datasetdesigner.listeners;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.collection.entry.persistence.PersistenceManager;

import org.psygrid.datasetdesigner.model.StudyDataSet;
import org.psygrid.datasetdesigner.utils.LocalFileUtility;

import org.psygrid.datasetdesigner.controllers.DatasetController;

import java.awt.event.*;

import java.io.File;

/**
 * Listener to test for inactivity; closes application after set TIMEOUT period.
 *
 * @author pwhelan
 */
public class TimerListener implements ActionListener {
	
	/**
	 * The logger for this class
	 */
	private static final Log LOG = LogFactory.getLog(TimerListener.class);
	
    /**
     * Lock
     */
    Object lock;


    /**
     * Creates a new TimerListener object.
     */
    public TimerListener() {
        lock = new int[1];
    }

    /**
     * When it has timed out, show a message dialog and exit.
     * @param aet the calling <code>ActionEvent</code>
     */
    public void actionPerformed(ActionEvent e) {
        synchronized (lock) {
        	LocalFileUtility.autosave();
        }
    }
}

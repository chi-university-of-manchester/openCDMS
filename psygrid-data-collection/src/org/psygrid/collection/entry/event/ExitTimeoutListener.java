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

import java.awt.event.*;
import javax.swing.*;

import org.psygrid.collection.entry.Application;

/**
 * Check for inactivity; closes application after defined timeout period.
 * 
 * @author pwhelan
 */
public class ExitTimeoutListener extends TimeoutListener
{
  
  	private Application frame;

  	public ExitTimeoutListener(Application frame, long timeout) {
  		super(timeout);
  		this.frame = frame;
  	}

  	/**
  	 * When it has timed out, show a message dialog and exit.
  	 */
  	public void actionPerformed(ActionEvent e) {
  		synchronized(_lock) {
  			// We really shouldn't exit, but cleanup gracefully instead
  			if(System.currentTimeMillis() - _lastTime >= _timeout) {
  				JOptionPane.setRootFrame(frame);
  				JOptionPane.showMessageDialog(frame, "Your session has timed out.  Please login again.");
  				
  				//save state of any open document
  				frame.exitWithoutConfirmation(true);
  			}
  		}
	}

}


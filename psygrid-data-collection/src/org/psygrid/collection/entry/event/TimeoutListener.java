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

import java.awt.AWTEvent;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * Listener to test for inactivity.
 * Taken from: http://www.devx.com/getHelpOn/10MinuteSolution/20437
 *
 * @author Rob Harper
 *
 */
public abstract class TimeoutListener implements AWTEventListener, ActionListener { 
	Object _lock;
	long _timeout, _lastTime;
  
  	public TimeoutListener(long timeout) {
  		_lock     = new int[1];
  		_timeout = timeout;
  		_lastTime = System.currentTimeMillis();
  	}

  	protected void __recordLastEvent(AWTEvent event) {
  		synchronized(_lock) {
  			_lastTime = System.currentTimeMillis();
  		}
  	}

	public void eventDispatched(AWTEvent event) {
		__recordLastEvent(event);		
	}

}

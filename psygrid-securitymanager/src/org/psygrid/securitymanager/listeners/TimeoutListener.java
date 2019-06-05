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

package org.psygrid.securitymanager.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JOptionPane;

import org.psygrid.securitymanager.Application;

/**
 * Listener to test for inactivity; closes application after set TIMEOUT period.
 * 
 * @author pwhelan
 */
public class TimeoutListener implements MouseListener,
                                 MouseMotionListener,
                                 KeyListener, ActionListener
{
	Object _lock;
	long _timeout, _lastTime;
  
  	private Application application;

  	public TimeoutListener(Application application, long timeout) {
  		this.application = application;
  		_lock     = new int[1];
  		_timeout  = timeout;
  		_lastTime = System.currentTimeMillis();
  	}

  	private void __recordLastEvent(InputEvent event) {
	  synchronized(_lock) {
		  _lastTime = System.currentTimeMillis();
	  }
  	}

  	/**
  	 * When it has timed out, show a message dialog and exit.
  	 */
  	public void actionPerformed(ActionEvent e) {
  		synchronized(_lock) {
  			// We really shouldn't exit, but cleanup gracefully instead
  			if(System.currentTimeMillis() - _lastTime >= _timeout) {
  				JOptionPane.showMessageDialog(application, "Your session has timed out.  Please login again.");
  				System.exit(0);
  			}
  		}
	}

  	/**
  	 * Update on all mouse events.
  	 */
	public void mouseDragged(MouseEvent e) { __recordLastEvent(e); }
	public void mouseMoved(MouseEvent e)   { __recordLastEvent(e); }
	public void mouseClicked(MouseEvent e) { __recordLastEvent(e); }
	public void mouseEntered(MouseEvent e) { __recordLastEvent(e); }
	public void mouseExited(MouseEvent e)  { __recordLastEvent(e); }
	public void mousePressed(MouseEvent e) { __recordLastEvent(e); }
	public void mouseReleased(MouseEvent e){ __recordLastEvent(e); }
	public void keyPressed(KeyEvent e)     { __recordLastEvent(e); }
	public void keyReleased(KeyEvent e)    { __recordLastEvent(e); }
	public void keyTyped(KeyEvent e)       { __recordLastEvent(e); }
}


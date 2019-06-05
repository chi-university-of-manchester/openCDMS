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

package org.psygrid.securitymanager.ui;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Action;
import javax.swing.JButton;

/**
 * Simple button with fixed size and central alignment.
 * @author pwhelan
 */
public class CustomBoxButton extends JButton
{
	
	private final static int BUTTON_WIDTH = 140;
	private final static int BUTTON_HEIGHT = 50;
	
	public CustomBoxButton(Action action)
	{
		super(action);
		setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
		setMaximumSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
		setMinimumSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
		setAlignmentX(Component.CENTER_ALIGNMENT);
	}
	
	
}
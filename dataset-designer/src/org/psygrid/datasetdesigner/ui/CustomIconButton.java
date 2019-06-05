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

package org.psygrid.datasetdesigner.ui;

import java.awt.Dimension;

import javax.swing.Action;
import javax.swing.JButton;

/**
 * Simple icon button with fixed size and tootip.
 * @author pwhelan
 */
public class CustomIconButton extends JButton
{
	/**
	 * Fixed width for the button
	 */
	private final static int BUTTON_WIDTH = 40;

	/**
	 * Fixed height for the button
	 */
	private final static int BUTTON_HEIGHT = 20;
	
	/** 
	 * Custom button with standard size and tooltip.
	 * @param action The action for this button
	 * @param tooltip A string containing the label to be used as button
	 * tooltip
	 */
	public CustomIconButton(Action action, String tooltip)
	{
		super(action);
		setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
		setMaximumSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
		setMinimumSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
		setToolTipText(tooltip);
	}
	
	
}
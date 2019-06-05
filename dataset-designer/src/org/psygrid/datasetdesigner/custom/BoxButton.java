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
package org.psygrid.datasetdesigner.custom;

import java.awt.Dimension;
import javax.swing.JButton;

import org.psygrid.datasetdesigner.utils.IconsHelper;

/**
 * BoxButton to display a button with a fixed size
 * 
 * 
 * @author pwhelan
 */
public class BoxButton extends JButton {

	/**
	 * Value for the width of the button; button is constrained to this width
	 */
	private final static int FIXED_WIDTH = 60;

	/**
	 * Value for the height of the button; button is constrained to this height
	 */
	private final static int FIXED_HEIGHT = 60;

	/**
	 * Constructor; takes the text label and icon name
	 * @param text the text to appear on the button
	 * @param icon the name of the icon
	 */
	public BoxButton(String text, String icon){
		super("", IconsHelper.getInstance().getImageIcon(icon));
		setMinimumSize(new Dimension(FIXED_WIDTH, FIXED_HEIGHT));
		setMaximumSize(new Dimension(FIXED_WIDTH, FIXED_HEIGHT));
		setPreferredSize(new Dimension(FIXED_WIDTH, FIXED_HEIGHT));
	}
	
	
}

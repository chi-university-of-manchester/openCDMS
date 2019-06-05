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

import java.awt.Color;
import java.awt.Font;

import javax.swing.*;

/**
 * A simple JLabel that just displays the text in plain font.
 * 
 * @author pwhelan
 */
public class CustomLabel extends JLabel
{
	
	public CustomLabel(String labelText)
	{
		super(labelText);
		this.setFont(getFont().deriveFont(Font.PLAIN));
	}
	
	public CustomLabel(String labelText, Color color)
	{
		super(labelText);
		this.setFont(getFont().deriveFont(Font.PLAIN));
		this.setForeground(color);
	}
	
}
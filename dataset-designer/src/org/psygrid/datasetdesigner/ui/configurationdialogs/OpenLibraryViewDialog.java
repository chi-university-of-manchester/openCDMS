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
package org.psygrid.datasetdesigner.ui.configurationdialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;

import org.psygrid.datasetdesigner.ui.MainFrame;

public class OpenLibraryViewDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;
	
	private MainFrame frame;
	
	public OpenLibraryViewDialog(MainFrame frame) {
		super(frame);
		this.frame = frame;
	}
	
	public void actionPerformed(ActionEvent aet) {
	
	}
}
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
package org.psygrid.datasetdesigner.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;

import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.ui.MainTabbedPane;

import org.psygrid.datasetdesigner.ui.configurationdialogs.ViewScheduleDialog;

/**
 * @author pwhelan
 *
 */
public class ViewScheduleAction extends AbstractAction {

	private final JFrame parentFrame;
	private MainTabbedPane docPane;
	
	private boolean readOnly = false;
	
	public ViewScheduleAction(JFrame parentFrame, MainTabbedPane docPane) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.manageschedule"));
		this.parentFrame = parentFrame;
		this.docPane = docPane;
	}
	
	public ViewScheduleAction(JFrame parentFrame, MainTabbedPane docPane, boolean readOnly) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.viewschedule"));
		this.parentFrame = parentFrame;
		this.docPane = docPane;
		this.readOnly = readOnly;
	}

	public void actionPerformed(ActionEvent aet) {
		//different title for the dialogs here
		if (readOnly) {
			new ViewScheduleDialog(parentFrame, docPane, readOnly);
		} else {
			new ViewScheduleDialog(parentFrame, docPane);
		}
	}
	
}

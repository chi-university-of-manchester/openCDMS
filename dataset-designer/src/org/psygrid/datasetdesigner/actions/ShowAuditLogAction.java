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

import org.psygrid.data.model.hibernate.Element;
import org.psygrid.datasetdesigner.ui.configurationdialogs.ShowAuditLog;

import org.psygrid.datasetdesigner.utils.PropertiesHelper;

/**
 * Action to show the about dialog
 * @author pwhelan
 */
public class ShowAuditLogAction extends AbstractAction {
	
	/**
	 * The main window of the application
	 */
	private JFrame frame;
	
	
	/**
	 * The element for audit log viewer
	 */
	private Element element;
	
	/**
	 * Constructor
	 * set the label for the action
	 */
	public ShowAuditLogAction(JFrame frame, Element element) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.auditlog"));
		this.frame = frame;
		this.element = element;
	}
	
	/**
	 * Display Help about dialog
	 * @param aet the calling action event
	 */
	public void actionPerformed(ActionEvent aet) {
		new ShowAuditLog(frame, element);
	}
}
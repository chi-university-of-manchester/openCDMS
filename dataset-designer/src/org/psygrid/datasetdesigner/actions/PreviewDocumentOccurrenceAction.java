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

import org.psygrid.data.model.*;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JList;

import org.psygrid.datasetdesigner.ui.DocumentConfigurationDialog;
import org.psygrid.datasetdesigner.ui.configurationdialogs.*;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

/**
 * Action shows the document occurrences in a tree-like structure.
 * @author pwhelan
 */
public class PreviewDocumentOccurrenceAction extends AbstractAction {

	private JDialog parentDialog;
	private JComboBox datasetBox;
	private JList docOccList;
	
	public PreviewDocumentOccurrenceAction(JDialog parentDialog, JComboBox datasetBox, JList docOccList) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.preview"));
		this.datasetBox = datasetBox;
		this.parentDialog = parentDialog;
		this.docOccList = docOccList;
	}
	
	public void actionPerformed(ActionEvent aet) {
		new PreviewDocumentOccurrenceDialog(parentDialog, datasetBox, docOccList);
	}
	
}
	
	
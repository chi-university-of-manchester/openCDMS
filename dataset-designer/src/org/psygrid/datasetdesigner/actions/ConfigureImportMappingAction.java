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
import javax.swing.JOptionPane;

import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.Section;
import org.psygrid.data.model.hibernate.SectionOccurrence;
import org.psygrid.datasetdesigner.ui.configurationdialogs.ConfigureImportMappingDialog;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

public class ConfigureImportMappingAction extends AbstractAction {

	private final static String STRING_PREFIX = "org.psygrid.datasetdesigner.ui.configurationdialogs.configureimportmappingaction.";
	
	private Document document;
	private JFrame frame;
	
	public ConfigureImportMappingAction(JFrame frame, Document document) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.configureimportmapping"));
		this.document = document;
		this.frame = frame;
	}
	
	public void actionPerformed(ActionEvent aet) {
		//check if any sections allow dynamic creation of sections; if so, import mapping is not allowed here
		for (int i=0; i<document.numSections(); i++) {
			Section section = document.getSection(i);
			for (int j=0; j< section.numOccurrences(); j++) {
				SectionOccurrence secOcc = section.getOccurrence(j);
				if (secOcc.isMultipleAllowed()) {
					JOptionPane.showMessageDialog(frame, PropertiesHelper.getStringFor(STRING_PREFIX + "nomultiplesections"));
					return;
				}
			}
		}
		
		new ConfigureImportMappingDialog(frame, document);
	}
	
}

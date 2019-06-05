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

import com.jgoodies.validation.view.ValidationComponentUtils;

import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Text field that sets the background colour 
 * based on the mandatory flag and constrains the 
 * size based on the custom document filter applied here
 * 
 * @author pwhelan
 */
public class TextFieldWithStatus extends JTextField {
	
	/**
	 * A logger for this class
	 */
	private static final Log LOG = LogFactory.getLog(TextFieldWithStatus.class);
	
	/**
	 * Text field that sets the background colour 
	 * based on the mandatory flag and constrains the 
	 * size based on the custom document filter applied here
	 * @param size the size of the textfield
	 * @param mandatory true if the field must be completed by the user for the 
	 * 					document to validate; false if not
	 */
	public TextFieldWithStatus (int size, boolean mandatory) {
		super(size);
		
		//set the mandatory field background colour
		if (mandatory) {
			ValidationComponentUtils.setMandatoryBackground(this);
		}
		
		//constrain the text field size to the max number of bytes allowed in the database
		((AbstractDocument)getDocument()).setDocumentFilter(new TooManyBytesFilter(getDocument()));
	}
		
}

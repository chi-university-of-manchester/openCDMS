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


package org.psygrid.collection.entry.validation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

import javax.swing.JLabel;

import org.psygrid.collection.entry.Icons;

import com.jgoodies.validation.ValidationMessage;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.view.ValidationResultViewFactory;

public class ValidationHandler implements PropertyChangeListener {

	private final JLabel validationLabel;
	private final String validationPrefix;

	private static final String HTML_START = "<html>";     //$NON-NLS-1$
	private static final String HTML_END = "</html>"; //$NON-NLS-1$
	private static final String LINE_BREAK = "<p>"; //$NON-NLS-1$

	public ValidationHandler(JLabel validationLabel, String validationPrefix) {
		this.validationLabel = validationLabel;
		this.validationPrefix = validationPrefix;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		ValidationResult result = (ValidationResult) evt.getNewValue();
		if (result.hasMessages()) {
			StringBuilder builder = new StringBuilder();
			builder.append(HTML_START);

			StringBuilder validationBuilder = new StringBuilder();
			if (validationPrefix != null) {
				validationBuilder.append(validationPrefix);
			}
			Iterator<?> it;
			if (result.hasErrors()) {
				it = result.getErrors().iterator();
			}
			else if (result.hasWarnings()){
				it = result.getWarnings().iterator();
			}
			else {
				it = result.getMessages().iterator();
			}
			int counter = 0;
			while (it.hasNext())    {
				ValidationMessage message = (ValidationMessage)it.next();
				if ( counter > 0 ){
					validationBuilder.append(LINE_BREAK);
				}
				validationBuilder.append(message.formattedText());
				counter++;
			}

			if (validationBuilder.length() > 55) {
				builder.append("<div style='width: 300px; text-justification: justify;'>");
			}
			builder.append(validationBuilder.toString());
			if (validationBuilder.length() > 55) {
				builder.append("</div>");
			}
			builder.append(HTML_END);
			if (result.hasErrors()) {
				validationLabel.setIcon(ValidationResultViewFactory.getErrorIcon());
			}
			else if (result.hasWarnings())  {
				validationLabel.setIcon(ValidationResultViewFactory.getWarningIcon());
			}
			else {
				Object message = result.getMessages().get(0);
				if (message instanceof TransformedMessage) {
					validationLabel.setIcon(Icons.getInstance().getIcon("transformed")); //$NON-NLS-1$
				}
				else {
					validationLabel.setIcon(ValidationResultViewFactory.getInfoIcon());
				}
			}

			validationLabel.setToolTipText(builder.toString());
		} else {
			validationLabel.setIcon(null);
			validationLabel.setToolTipText(null);
		}

	}

}

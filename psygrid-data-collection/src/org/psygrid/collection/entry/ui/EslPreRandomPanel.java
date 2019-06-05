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

package org.psygrid.collection.entry.ui;

import javax.swing.JLabel;

import org.psygrid.collection.entry.EntryMessages;
import org.psygrid.data.model.hibernate.Record;
import org.psygrid.esl.model.ISubject;

/**
 * ESL Panel used for checking the participant's register details just before 
 * randomization.
 * 
 * @author Rob Harper
 *
 */
public class EslPreRandomPanel extends EslViewEditPanel {

	private static final long serialVersionUID = -4841254190014859550L;

	public EslPreRandomPanel(Record record, ISubject subject) {
		super(record, subject);
	}

	@Override
	protected void addInstructions() {
		builder.append(new JLabel(EntryMessages.getString("EslPanel.reviewMessage1")));
		builder.append(new JLabel(EntryMessages.getString("EslPanel.reviewMessage2")));
		builder.append(new JLabel(EntryMessages.getString("EslPanel.reviewMessage3")));
	}

}

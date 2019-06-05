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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.psygrid.collection.entry.EntryMessages;
import org.psygrid.data.model.hibernate.Record;
import org.psygrid.esl.model.ISubject;

import com.jgoodies.forms.factories.ButtonBarFactory;

/**
 * ESL Panel to be used for viewing and editing a participants details
 * 
 * @author Rob Harper
 *
 */
public class EslViewEditPanel extends EslPanel {

	private static final long serialVersionUID = 7429370936811116868L;

	private JButton editButton;
	private boolean editing = false;

	public EslViewEditPanel(Record record, ISubject subject) {
		super(record, subject, true);
	}

	@Override
	protected JButton createOkButton() {
		return new JButton(EntryMessages.getString("Entry.ok")); //$NON-NLS-1$
	}

	@Override
	protected void addEditButton() {
		editButton = new JButton(EntryMessages.getString("Entry.edit")); //$NON-NLS-1$
		if(getSubject().isLocked()) {
			// Subject can't be edited if locked
			editButton.setEnabled(false);
		} 
		editButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editSubject();
			}
		});

		JPanel editButtonPanel = ButtonBarFactory.buildLeftAlignedBar(editButton);
		builder.append(editButtonPanel);
	}

	private void editSubject() {
		editing = true;
		makeEditable();
		editButton.setEnabled(false);
	}

	@Override
	protected boolean isSaveRequired() {
		return editing;
	}
	
}

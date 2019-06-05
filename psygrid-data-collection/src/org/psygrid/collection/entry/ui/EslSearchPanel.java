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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.EntryMessages;
import org.psygrid.collection.entry.chooser.ChooserDialog;
import org.psygrid.collection.entry.util.EslHelper;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Record;
import org.psygrid.esl.model.ISubject;
import org.psygrid.esl.model.hibernate.Address;
import org.psygrid.esl.model.hibernate.Subject;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * A panel used within the IdentifierDialog to allow the user to 
 * provide ESL options rather than an identifier when opening a
 * record.
 * 
 * The panel provides the most pertinent fields from the ESL and
 * allows the user to fill in one or more before searching the 
 * ESL and retrieving a list of matching subjects.  
 * 
 * @author Lucy Bridges
 *
 */
public class EslSearchPanel extends JPanel {

	private static final long serialVersionUID = 6968481216190927864L;

	private DefaultFormBuilder builder;
	private JButton searchButton;
	private JButton cancelButton;
	private JButton moreButton;

	private JTextField firstName;
	private JTextField lastName;
	private BasicDatePicker dateOfBirth;
	private JTextField nhsNumber;
	private JTextField postCode;

	private ChooserDialog parent;

	private Application application;
	private DataSet dataset;
	
	private Record selectedRecord = null;

	public EslSearchPanel(Application application, ChooserDialog parent, DataSet dataset) {
		super();
		this.parent = parent;
		this.application = application;
		this.dataset = dataset;
		init();
	}

	private void init() {

		FormLayout layout = new FormLayout(
				//"50dlu, 12dlu, 85dlu, 25dlu, 50dlu, 4dlu, 85dlu",   
				"50dlu, 12dlu, fill:pref:grow, 25dlu, 50dlu, 4dlu, fill:pref:grow",
		"top:pref, 9dlu, center:pref, 9dlu, center:pref, 9dlu, center:pref, 9dlu, fill:pref:grow, 9dlu, fill:pref:grow, 9dlu, pref:grow"); //$NON-NLS-1$
		builder = new DefaultFormBuilder(layout, this);

		builder.setDefaultDialogBorder();
		builder.appendSeparator();

		firstName = new JTextField();
		JLabel firstNameLabel = new JLabel(EntryMessages.getString("EslSearchPanel.firstname"));
		lastName = new JTextField();
		JLabel lastNameLabel = new JLabel(EntryMessages.getString("EslSearchPanel.lastname"));	

		dateOfBirth = new BasicDatePicker(System.currentTimeMillis()); 
		JLabel dateOfBirthLabel = new JLabel(EntryMessages.getString("EslSearchPanel.dob"));
		nhsNumber = new JTextField();
		JLabel nhsNumberLabel = new JLabel(EntryMessages.getString("EslSearchPanel.nhsnumber"));
		postCode = new JTextField();
		JLabel postCodeLabel = new JLabel(EntryMessages.getString("EslSearchPanel.postcode"));

		CellConstraints cc = new CellConstraints();
		builder.add(new JLabel(EntryMessages.getString("EslSearchPanel.searchfor")),   new CellConstraints(1, 2, 5, 3));

		builder.add(firstNameLabel,  cc.xy(1, 5));
		builder.add(firstName, cc.xy(3, 5));
		builder.add(lastNameLabel, cc.xy(5, 5));
		builder.add(lastName, cc.xy(7, 5));

		builder.add(dateOfBirthLabel, cc.xy(1, 7));
		builder.add(dateOfBirth, cc.xy(3, 7));

		builder.add(nhsNumberLabel, cc.xy(1, 9));
		builder.add(nhsNumber, cc.xy(3, 9));

		builder.add(postCodeLabel, cc.xy(1, 11));
		builder.add(postCode, cc.xy(3, 11));

		moreButton = new JButton(EntryMessages.getString("Entry.more")); //$NON-NLS-1$
		moreButton.setEnabled(true);
		moreButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Show EslDialog with search functionality..
				moreOptions();
			}
		});

		searchButton = new JButton(EntryMessages.getString("EslSearchPanel.search")); //$NON-NLS-1$
		searchButton.setEnabled(false);
		searchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				search();
			}
		});

		cancelButton = new JButton(EntryMessages.getString("Entry.cancel")); //$NON-NLS-1$
		cancelButton.setEnabled(true);
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancel();
			}
		});


		FocusListener itemListener = new FocusListener() {
			public void focusGained(FocusEvent e) {
				searchButton.setEnabled(true);
			}
			public void focusLost(FocusEvent e) {
			}
		};
		firstName.addFocusListener(itemListener);
		firstName.addFocusListener(itemListener);
		lastName.addFocusListener(itemListener);
		dateOfBirth.addFocusListener(itemListener);
		nhsNumber.addFocusListener(itemListener);
		postCode.addFocusListener(itemListener);

		builder.appendUnrelatedComponentsGapRow();
		builder.nextLine(2);

		JPanel moreButtonPanel = ButtonBarFactory.buildLeftAlignedBar(moreButton);
		builder.add(moreButtonPanel, cc.xy(1, 13));
		JPanel okButtonPanel = ButtonBarFactory.buildRightAlignedBar(searchButton, cancelButton);
		builder.add(okButtonPanel, new CellConstraints(1, 13, 7, 1));   //cc.xy(7, 13)
		
		this.setPreferredSize(new Dimension(648, 222));
	}

	private void moreOptions() {
		EslFullSearchSubjectDialog dialog = new EslFullSearchSubjectDialog(application, parent, dataset);
		dialog.setVisible(true);
		selectedRecord = dialog.getSelectedRecord();
		parent.eslRecordSelectedAction(selectedRecord);
	}

	private void search() {
		ISubject subject = new Subject();

		//Populate subject fields..
		if (firstName.getText().length() > 0) {
			subject.setFirstName(firstName.getText());
		}
		if (lastName.getText().length() > 0) {
			subject.setLastName(lastName.getText());
		}
		if (dateOfBirth.getDate() != null && dateOfBirth.getDate().toString().length() > 0) {
			subject.setDateOfBirth(dateOfBirth.getDate());
		}
		if (nhsNumber.getText().length() > 0) {
			subject.setNhsNumber(nhsNumber.getText());
		}
		if (postCode.getText() != null && postCode.getText().length() > 0) {
			subject.setAddress(new Address());
			subject.getAddress().setPostCode(postCode.getText());
		}

		//And do a search..
		List<ISubject> subjects = EslHelper.searchEslSubject(application, parent, subject, dataset);

		//Display the appropriate dialog window.
		selectedRecord = EslHelper.displaySearchResults(application, parent, subjects, dataset);
		parent.eslRecordSelectedAction(selectedRecord);
	}

	private void cancel() {
		parent.dispose();
	}
	
	public Record getSelectedRecord() {
		return selectedRecord;
	}
}

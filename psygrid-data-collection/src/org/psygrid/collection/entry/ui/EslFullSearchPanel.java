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

import javax.swing.JButton;
import javax.swing.JPanel;

import org.psygrid.collection.entry.EntryMessages;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.esl.model.IAddress;
import org.psygrid.esl.model.ISubject;

/**
 * ESL Panel used for searching the participant register by all available fields
 * 
 * @author Rob Harper
 *
 */
public class EslFullSearchPanel extends EslPanel {

	private static final long serialVersionUID = 7908798400175318393L;

	/**
	 * @param dataSet
	 */
	public EslFullSearchPanel(DataSet dataSet) {
		super(dataSet);
	}

	@Override
	protected JButton createOkButton() {
		return new JButton(EntryMessages.getString("EslSearchPanel.search")); //$NON-NLS-1$	
	}

	@Override
	protected void addPanels(ISubject subject, DataSet dataSet) {
		JPanel personalDetailsPanel = null;
		JPanel sexDobPanel = null;
		JPanel addressPanel = null;
		JPanel phoneEmailPanel = null;
		JPanel healthDetailsPanel = null;
		JPanel customPanel = null;

		if ( null == subject ){
			personalDetailsPanel = initPersonalDetailsPanel();
			sexDobPanel = initSexDobPanel();
			addressPanel = initAddressPanel();
			phoneEmailPanel = initPhoneEmailPanel();
			healthDetailsPanel = initHealthDetailsPanel();
			customPanel = initCustomPanel(dataSet);
		}
		else{
			personalDetailsPanel = initPersonalDetailsPanel(
					subject.getTitle(), subject.getFirstName(), subject.getLastName());
			sexDobPanel = initSexDobPanel(
					subject.getSex(), subject.getDateOfBirth());
			IAddress address = subject.getAddress();
			addressPanel = initAddressPanel(
					address.getAddress1(), address.getAddress2(), address.getAddress3(),
					address.getCity(), address.getRegion(), address.getCountry(), address.getPostCode());
			phoneEmailPanel = initPhoneEmailPanel(
					subject.getEmailAddress(), address.getHomePhone(), subject.getWorkPhone(), 
					subject.getMobilePhone());
			healthDetailsPanel = initHealthDetailsPanel(
					subject.getNhsNumber(), subject.getHospitalNumber());
			customPanel = initCustomPanel(dataSet, subject);
		}

		builder.append(personalDetailsPanel);
		builder.append(sexDobPanel);
		builder.append(addressPanel);
		builder.append(phoneEmailPanel);
		builder.append(healthDetailsPanel);
		if ( null != customPanel ){
			builder.append(customPanel);
		}
	}

	@Override
	protected boolean doValidation() {
		return false;
	}

	@Override
	protected boolean isSaveRequired() {
		return false;
	}

	
}

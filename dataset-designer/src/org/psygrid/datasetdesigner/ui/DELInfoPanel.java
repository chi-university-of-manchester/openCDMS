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
package org.psygrid.datasetdesigner.ui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.psygrid.data.model.hibernate.DataElementContainer;
import org.psygrid.datasetdesigner.ui.dataelementfacilities.DELSecurity;
import org.psygrid.datasetdesigner.utils.HelpHelper;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.SpringUtilities;

public class DELInfoPanel extends JPanel {

	DataElementContainer element;


	public DELInfoPanel(DataElementContainer element){

		this.element = element;
		setLayout(new SpringLayout());
		add(HelpHelper.getInstance().getHelpButtonWithID("dsddelconfigdelelementid"));
		add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.delelementid")));

		JTextField lsid, date, authority, who, status, comment;

		lsid = new JTextField(this.element.getElementLSID());
		lsid.setEditable(false);
		lsid.setBackground(this.getBackground());
		add(lsid);

		add(HelpHelper.getInstance().getHelpButtonWithID("dsddelconfigdeldatemodified"));
		add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.deldatemodified")));
		date = new JTextField();
		date.setEditable(false);
		date.setBackground(this.getBackground());
		add(date);

		add(HelpHelper.getInstance().getHelpButtonWithID("dsddelconfigdelauthority"));
		add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.delauthority")));
		authority = new JTextField(this.element.getElementLSIDObject().getAuthorityId());
		authority.setEditable(false);
		authority.setBackground(this.getBackground());
		add(authority);

		add(HelpHelper.getInstance().getHelpButtonWithID("dsddelconfigdelperson"));
		add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.delperson")));
		who = new JTextField();
		who.setEditable(false);
		who.setBackground(this.getBackground());
		add(who);

		add(HelpHelper.getInstance().getHelpButtonWithID("dsddelconfigdelstatus"));
		add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.delstatus")));
		status = new JTextField();
		status.setEditable(false);
		status.setBackground(this.getBackground());
		add(status);

		if (this.element.getLatestMetaData() != null) {
			date.setText(this.element.getLatestMetaData().getElementDate().toString());
			who.setText(this.element.getLatestMetaData().getWho());
		}
		
		if (element.getStatus() != null) {
			status.setText(element.getStatus().toString());
		}

		
		int rows = 5;

		String message = "";
		if (this.element.getIsEditable()) {
			if (this.element.getIsRevisionCandidate()) {
				if (this.element.getStatus() != null) {
					message = "This element has been edited.";
				}
				else {
					message = "This element can be added to the library.";
				}
			}
		}
		else {
			if (!DELSecurity.getInstance().canEditElements(authority.getText())) {
				message = "You do not have permission to edit this element";
			}
			else if (!this.element.getHeadRevision()) {
				message = "This element is out of date and cannot be edited.";
			}
			else {
				message = "This element has been marked as not editable.";
			}
		}
		add(HelpHelper.getInstance().getHelpButtonWithID("dsddelconfigdelcomment"));
		add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.delcomment")));
		comment = new JTextField(message);
		comment.setEditable(false);
		comment.setBorder(null);
		comment.setBackground(this.getBackground());
		add(comment);
		rows++;


		SpringUtilities.makeCompactGrid(this,
				rows, 3, //rows, cols
				6, 6,        //initX, initY
				6, 6);       //xPad, yPad
	}
}

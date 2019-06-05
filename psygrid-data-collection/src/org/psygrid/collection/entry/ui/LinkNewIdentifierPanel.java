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

import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;

import org.psygrid.data.model.hibernate.DataSet;

/**
 * Panel used when creating a new secondary record to be linked
 * to a primary record.
 * <p>
 * Subclass of {@link NewIdentifierPanel} that provides an
 * implementation of filterGroups that restricts the groups shown 
 * to those in the supplied list of group codes.
 * 
 * @author Rob Harper
 *
 */
public class LinkNewIdentifierPanel extends NewIdentifierPanel {

	private static final long serialVersionUID = -6538143875802247220L;

	private List<String> secondaryGroups;

	public LinkNewIdentifierPanel(DataSet dataSet, List<String> groups, JDialog dialog)
			throws IdentifierPanelException {
		super(dialog);
		this.dataSet = dataSet;
		this.secondaryGroups = groups;
		init();
	}

	@Override
	protected List<String> filterGroups(List<String> groups) {
		List<String> displayGroups = new ArrayList<String>();
		for ( String group: groups ){
			if ( secondaryGroups.contains(group) ){
				displayGroups.add(group);
			}
		}
		return displayGroups;
	}

}

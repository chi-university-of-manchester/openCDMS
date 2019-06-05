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

package org.psygrid.outlook.test.patches.v1_1_21;

import java.util.List;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch11 extends AbstractPatch {


	@Override
	public String getName() {
		return "Update Groups to provide friendly long names";
	}

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		DataSet dataSet = (DataSet)ds;

		//Add new Statuses
		updateGroups(dataSet);
	}

	private void updateGroups(DataSet dataSet) {

		List<Group> groups = dataSet.getGroups();

		/*
		 * Get the groups from the dataset and set the long names from the names provided in
		 * the original org.psygrid.www.xml.security.core.types.GroupType objects
		 */
		for (Group group: groups) {
			System.out.println("Found group "+group.getName());

			if ("001001".equals(group.getName())) {
				group.setLongName("Bristol Avon-Avon and Wiltshire Mental Health Partnership");
			}

			if ("002001".equals(group.getName())) {
				group.setLongName("East Anglia-Norfolk and Waveney Mental Health Partnership Trust");
			}

			if ("002002".equals(group.getName())) {
				group.setLongName("East Anglia-Cambridge CAMEO");
			}

			if ("003001".equals(group.getName())) {
				group.setLongName("East Midlands-Nottinghamshire Healthcare NHS Trust");
			}

			if ("003002".equals(group.getName())) {
				group.setLongName("East Midlands-Lincolnshire Partnership Trust");
			}

			if ("004001".equals(group.getName())) {
				group.setLongName("North East-Newcastle, Northumberland and North Tyneside Mental Health Trust");
			}

			if ("005001".equals(group.getName())) {
				group.setLongName("North London-South West London and St. Georges Trust");
			}

			if ("005002".equals(group.getName())) {
				group.setLongName("North London-Central and West London Trust");
			}

			if ("006001".equals(group.getName())) {
				group.setLongName("North West-Manchester Mental Health and Social Care Trust");
			}

			if ("007001".equals(group.getName())) {
				group.setLongName("South London-South London and Maudsely Trust");
			}

			if ("008001".equals(group.getName())) {
				group.setLongName("West Midlands-Birmingham and Solihull Mental Health Trust");
			}

			if ("002003".equals(group.getName())) {
				group.setLongName("East Anglia-Peterbrough NHS Trust");
			}

			if ("006002".equals(group.getName())) {
				group.setLongName("North West-Bolton, Salford and Trafford Mental Health Trust");
			}

			if ("006003".equals(group.getName())) {
				group.setLongName("North West-Lancashire Care Trust");
			}

		}


	}



}

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

package org.psygrid.edie.control.patches.v1_1_24;

import java.util.List;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch6 extends AbstractPatch {


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
				group.setLongName("Manchester");
			}
			if ("002001".equals(group.getName())) {
				group.setLongName("Birmingham");
			}
			if ("003001".equals(group.getName())) {
				group.setLongName("Cambridge");
			}
			if ("004001".equals(group.getName())) {
				group.setLongName("East Anglia");
			}
			if ("005001".equals(group.getName())) {
				group.setLongName("Glasgow");
			}
		}

	}

}

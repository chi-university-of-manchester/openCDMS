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

package org.psygrid.neden.patches.v1_1_21;

import java.util.List;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch36 extends AbstractPatch {


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
				group.setLongName("Heart of Birmingham - West EIS");
			}
			if ("002001".equals(group.getName())) {
				group.setLongName("Heart of Birmingham - East EIS");
			}
			if ("003001".equals(group.getName())) {
				group.setLongName("East PCT Birmingham");
			}
			if ("004001".equals(group.getName())) {
				group.setLongName("Lancashire 001-400");
			}
			if ("004002".equals(group.getName())) {
				group.setLongName("Lancashire 401-80");
			}
			if ("005001".equals(group.getName())) {
				group.setLongName("Norfolk");
			}
			if ("006001".equals(group.getName())) {
				group.setLongName("Cambridge CAMEO");
			}
			if ("007001".equals(group.getName())) {
				group.setLongName("Cornwall 001-500");
			}
			if ("007002".equals(group.getName())) {
				group.setLongName("Cornwall 501-1000");
			}
			if ("008001".equals(group.getName())) {
				group.setLongName("Birmingham South");
			}
			if ("004003".equals(group.getName())) {
				group.setLongName("Lancashire-Blackpool and Morecambe");
			}
			if ("009001".equals(group.getName())) {
				group.setLongName("Kings Lynn");
			}
			if ("010001".equals(group.getName())) {
				group.setLongName("Solihull");
			}
			if ("011001".equals(group.getName())) {
				group.setLongName("Cheshire and Wirral");
			}
			if ("012001".equals(group.getName())) {
				group.setLongName("Huntingdon");
			}



		}


	}



}

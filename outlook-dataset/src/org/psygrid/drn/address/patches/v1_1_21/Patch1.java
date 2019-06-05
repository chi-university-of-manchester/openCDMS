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

package org.psygrid.drn.address.patches.v1_1_21;

import java.util.List;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch1 extends AbstractPatch {


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

			if ("640001".equals(group.getName())) {
				group.setLongName("North Cumbria Acute Hospitals NHS Trust");
			}
			if ("640002".equals(group.getName())) {
				group.setLongName("South Tees Hospital NHS Trust");
			}
			if ("640003".equals(group.getName())) {
				group.setLongName("City Hospitals Sunderland NHS Foundation Trust");
			}
			if ("640004".equals(group.getName())) {
				group.setLongName("Gateshead Hospitals NHS Foundation Trust");
			}
			if ("640005".equals(group.getName())) {
				group.setLongName("Newcastle upon Tyne Hospitals NHS Foundation Trust");
			}
			if ("651001".equals(group.getName())) {
				group.setLongName("Salford Royal Hospitals NHS Foundation Trust");
			}
			if ("650001".equals(group.getName())) {
				group.setLongName("NW London Hospitals NHS Trust");
			}
			if ("650002".equals(group.getName())) {
				group.setLongName("Hillingdon Hospital NHS Trust");
			}
			if ("652001".equals(group.getName())) {
				group.setLongName("Royal Cornwall Healthcare NHS Trust");
			}
			if ("652002".equals(group.getName())) {
				group.setLongName("Plymouth Hospitals NHS Trust");
			}
			if ("652003".equals(group.getName())) {
				group.setLongName("Royal Devon and Exeter NHS Foundation Trust");
			}
			if ("652004".equals(group.getName())) {
				group.setLongName("South Devon Healthcare NHS Foundation Trust");
			}
			if ("649001".equals(group.getName())) {
				group.setLongName("Oxford Radcliffe Hospitals NHS Trust");
			}
			if ("654001".equals(group.getName())) {
				group.setLongName("The Ipswich Hospital NHS Trust");
			}
			if ("653001".equals(group.getName())) {
				group.setLongName("Barts and the London NHS Trust");
			}
			if ("653002".equals(group.getName())) {
				group.setLongName("Whipps Cross University Hospital Trust");
			}
			if ("653004".equals(group.getName())) {
				group.setLongName("Newham University Hospital NHS Trust");
			}
			if ("653003".equals(group.getName())) {
				group.setLongName("Southend University Hospital NHS Foundation");
			}
			if ("655001".equals(group.getName())) {
				group.setLongName("University Hospitals of Leicester NHS Trust");
			}
		}


	}



}

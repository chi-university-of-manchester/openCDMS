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

package org.psygrid.edie.patches.v1_1_13;

import java.util.ArrayList;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.DocumentGroup;
import org.psygrid.data.model.hibernate.GenericState;
import org.psygrid.edie.EDIEDataset;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch30 extends AbstractPatch {

	@Override
	public String getName() {
		return "Update Baseline 0 and Study Termination DocumentGroups to remove restrictions on accessing Documents";
	}

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {

		DataSet dataSet = (DataSet)ds;

		/*
		 * Remove the prerequisite groups for each DocumentGroup, so
		 * that Documents from these groups can be completed immediately.
		 */
		for (DocumentGroup group: dataSet.getDocumentGroups()) {
			if (group.getName().equals("Baseline 0 Group")) {
				group.setPrerequisiteGroups(new ArrayList<DocumentGroup>());
			}
			else if (group.getName().equals("Study termination")) {
				group.setAllowedRecordStatus(EDIEDataset.getStatuses(dataSet, GenericState.ACTIVE));
			}
		}

	}

}

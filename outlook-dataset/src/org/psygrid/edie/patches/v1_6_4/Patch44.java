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

package org.psygrid.edie.patches.v1_6_4;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.DocumentGroup;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch44 extends AbstractPatch {

	public String getName() {
		return "Update ED2 to stop records being automatically set as complete.";
	}

	public void applyPatch(DataSet ds, String saml) throws Exception {
		for (int i = 0; i < ds.numDocumentGroups(); i++) {
			DocumentGroup group = ds.getDocumentGroup(i);
			if (group.getDisplayText().equals("24 months")) {
				group.setUpdateStatus(null);
				break;
			}
		}
	}
}

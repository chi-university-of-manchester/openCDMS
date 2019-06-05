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

package org.psygrid.neden.patches.v1_1_20;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.data.model.hibernate.Site;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch34 extends AbstractPatch {
	@Override
	public String getName() {
		return "Update sites to add consultants.";
	}

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {


		for (int i=0; i < ds.numGroups();i++) {
			Group g = ds.getGroup(i);
			for (int j=0; j < g.numSites(); j++) {
				Site s = g.getSite(j);
				s.addConsultant("Max Birchwood");
			}
		}

	}

}

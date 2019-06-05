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

package org.psygrid.edie.patches.v1_1_12;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch26 extends AbstractPatch {
	@Override
	public String getName() {
		return "Patch to correct the site code for the University of East Anglia.";
	}

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {

		Group group = ds.getGroup(3);
		if ( !group.getName().equals("004001") ) {
			throw new Exception("This is not the group for 004001, it is "+group.getName());
		}

		if ( !group.getSite(0).getSiteName().equals("University of East Anglia") ) {
			throw new Exception("This is not the site for the University of East Anglia, it is "+group.getSite(0).getSiteName());
		}

		System.out.println("Updating the site id..");
		group.getSite(0).setSiteId("N0000974");
	}

}

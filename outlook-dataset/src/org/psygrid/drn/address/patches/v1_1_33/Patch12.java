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

package org.psygrid.drn.address.patches.v1_1_33;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Factory;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.data.model.hibernate.HibernateFactory;
import org.psygrid.data.model.hibernate.Site;
import org.psygrid.outlook.patches.AbstractPatch;

/**
 * @author Rob Harper
 *
 */
public class Patch12 extends AbstractPatch {

	/* (non-Javadoc)
	 * @see org.psygrid.outlook.patches.AbstractPatch#applyPatch(org.psygrid.data.model.IDataSet, java.lang.String)
	 */
	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		Factory factory = new HibernateFactory();
		Group grp30 = (Group)factory.createGroup("651003");
		grp30.setLongName("Royal Blackburn Hospital");
		Site site30a = new Site("Royal Blackburn Hospital", "N0001527", "BB2 3HH", grp30);
		site30a.addConsultant("Claire Smith");
		grp30.addSite(site30a);
		Group grp31 = (Group)factory.createGroup("651004");
		grp31.setLongName("Burnley General Hospital");
		Site site31a = new Site("Burnley General Hospital", "N0000287", "BB10 2PQ", grp31);
		site31a.addConsultant("Miles Riddle");
		grp31.addSite(site31a);
		ds.addGroup(grp30);
		ds.addGroup(grp31);
	}

	/* (non-Javadoc)
	 * @see org.psygrid.outlook.patches.AbstractPatch#getName()
	 */
	@Override
	public String getName() {
		return "Add Blackburn and Burnley groups";
	}

}

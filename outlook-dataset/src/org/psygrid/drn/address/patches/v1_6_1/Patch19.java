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

package org.psygrid.drn.address.patches.v1_6_1;

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
public class Patch19 extends AbstractPatch {

	/* (non-Javadoc)
	 * @see org.psygrid.outlook.patches.AbstractPatch#applyPatch(org.psygrid.data.model.IDataSet, java.lang.String)
	 */
	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		Factory factory = new HibernateFactory();

		Group grp32 = (Group)ds.getGroup(31);
		if ( !"649007".equals(grp32.getName())){
			throw new RuntimeException("This is not the 649007 group, it is "+grp32.getName());
		}
		Site site32a = (Site)grp32.getSite(0);
		site32a.setSiteId("N0000204");

		Group grp33 = (Group)factory.createGroup("654004");
		grp33.setLongName("Norfolk and Norwich University Hospitals NHS Foundation Trust");
		Site site33a = new Site("Norfolk and Norwich Hospital", "N0000036", "NR4 7UY", grp33);
		site33a.addConsultant("Dr Nandu Thalange");
		grp33.addSite(site33a);
		ds.addGroup(grp33);
	}

	/* (non-Javadoc)
	 * @see org.psygrid.outlook.patches.AbstractPatch#getName()
	 */
	@Override
	public String getName() {
		return "Add Norfolk group and fix Warwick UKCRN code";
	}

}

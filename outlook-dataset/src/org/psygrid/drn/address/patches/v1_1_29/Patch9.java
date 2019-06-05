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


package org.psygrid.drn.address.patches.v1_1_29;

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
public class Patch9 extends AbstractPatch {

	/* (non-Javadoc)
	 * @see org.psygrid.outlook.patches.AbstractPatch#applyPatch(org.psygrid.data.model.IDataSet, java.lang.String)
	 */
	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		Factory factory = new HibernateFactory();
		Group grp25 = (Group)factory.createGroup("651002");
		grp25.setLongName("Central Manchester and Manchester Childrens Hospital");
		Site site25a = new Site("Manchester Royal Infirmary", "N0000080", "M13 9WL", grp25);
		site25a.addConsultant("Prof Rob Davies");
		grp25.addSite(site25a);
		Group grp26 = (Group)factory.createGroup("649005");
		grp26.setLongName("The Royal Berkshire NHS foundation trust");
		Site site26a = new Site("Royal Berkshire Hospital", "N0000139", "RG1 5AN", grp26);
		site26a.addConsultant("Dr Hugh Simpson");
		grp26.addSite(site26a);
		Group grp27 = (Group)factory.createGroup("649006");
		grp27.setLongName("Buckinghamshire Hospitals NHS Trust");
		Site site27a = new Site("Wycombe Hospital", "N0000106", "HP11 2TT", grp27);
		site27a.addConsultant("Dr Ian Gallen");
		grp27.addSite(site27a);
		Site site27b = new Site("Amersham Hospital", "N0000280", "HP7 0JD", grp27);
		site27b.addConsultant("Dr Ian Gallen");
		grp27.addSite(site27b);
		Group grp28 = (Group)factory.createGroup("653005");
		grp28.setLongName("Homerton University Hospital NHS Foundation Trust");
		Site site28a = new Site("Homerton Hospital", "N0000292", "E9 6SR", grp28);
		site28a.addConsultant("Dr John Anderson");
		grp28.addSite(site28a);
		Group grp29 = (Group)factory.createGroup("653006");
		grp29.setLongName("Broomfield Hospital");
		Site site29a = new Site("Broomfield Hospital", "N0000013", "CM1 7ET", grp29);
		site29a.addConsultant("Dr Alan Jackson");
		grp29.addSite(site29a);

		ds.addGroup(grp25);
		ds.addGroup(grp26);
		ds.addGroup(grp27);
		ds.addGroup(grp28);
		ds.addGroup(grp29);
	}

	/* (non-Javadoc)
	 * @see org.psygrid.outlook.patches.AbstractPatch#getName()
	 */
	@Override
	public String getName() {
		return "More new groups...";
	}

}

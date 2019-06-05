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


package org.psygrid.drn.address.patches.v1_1_28;

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
public class Patch5 extends AbstractPatch {

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		Factory factory = new HibernateFactory();
		Group grp20 = (Group)factory.createGroup("650003");
		grp20.setLongName("Ealing Hospital");
		Site site20a = new Site("Ealing Hospital", "N0000128", "UB1 3HW", grp20);
		site20a.addConsultant("Dr Kevin Baynes");
		grp20.addSite(site20a);
		Group grp21 = (Group)factory.createGroup("650004");
		grp21.setLongName("Imperial College Healthcare NHS trust");
		Site site21a = new Site("Hammersmith Hospital", "N0000129", "W12 0HS", grp21);
		site21a.addConsultant("Dr Ann Dornhorst");
		grp21.addSite(site21a);
		Site site21b = new Site("St Mary's Hospital", "N0000214", "W2 1NY", grp21);
		site21b.addConsultant("Prof Robert Elkeles");
		grp21.addSite(site21b);
		Group grp22 = (Group)factory.createGroup("650005");
		grp22.setLongName("Chelsea and Westminister Hospital");
		Site site22a = new Site("Chelsea and Westminister Hospital", "N0000016", " SW10 9NH", grp22);
		site22a.addConsultant("Dr Nicola Bridges");
		grp22.addSite(site22a);
		Group grp23 = (Group)factory.createGroup("650006");
		grp23.setLongName("West Middlesex University Hospitals");
		Site site23a = new Site("West Middlesex Hospitals NHS trust", "N0000060", "TW7 6AF", grp23);
		site23a.addConsultant("Dr Rashmi Kaushal");
		grp23.addSite(site23a);
		Group grp24 = (Group)factory.createGroup("650007");
		grp24.setLongName("Barnet and Chase Farm Hospitals");
		Site site24a = new Site("Barnet Hospital", "N0000126", "EN5 3DJ", grp24);
		site24a.addConsultant("Dr Vaseem Hakeem");
		grp24.addSite(site24a);
		Site site24b = new Site("Chase Farm Hospital", "N0000181", "EN2 8JL", grp24);
		site24b.addConsultant("Dr Vaseem Hakeem");
		grp24.addSite(site24b);

		ds.addGroup(grp20);
		ds.addGroup(grp21);
		ds.addGroup(grp22);
		ds.addGroup(grp23);
		ds.addGroup(grp24);
	}

	@Override
	public String getName() {
		return "Add groups to Address";
	}

}

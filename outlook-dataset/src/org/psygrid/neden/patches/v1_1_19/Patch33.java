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


package org.psygrid.neden.patches.v1_1_19;

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
public class Patch33 extends AbstractPatch {

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		Factory factory = new HibernateFactory();

		Group kingsLynn = (Group)factory.createGroup("009001");
		kingsLynn.addSite(new Site("Kings Lynn EIS", "N0002239", "PE30 5PD", kingsLynn));
		ds.addGroup(kingsLynn);

		Group solihull = (Group)factory.createGroup("010001");
		solihull.addSite(new Site("Solihull EIS", "N0002243", "B37 7RW", solihull));
		ds.addGroup(solihull);

		Group cheshireWirral = (Group)factory.createGroup("011001");
		cheshireWirral.addSite(new Site("Wirral EIT", "N0002240", "CH42 0LQ", cheshireWirral));
		cheshireWirral.addSite(new Site("West Cheshire EIT", "N0002241", "CH65 0BY", cheshireWirral));
		cheshireWirral.addSite(new Site("East Cheshire EIT", "N0002242", "CW1 4QJ", cheshireWirral));
		ds.addGroup(cheshireWirral);

		Group huntingdon = (Group)factory.createGroup("012001");
		huntingdon.addSite(new Site("Huntingdon EIS", "N0002244", "PE29 3RJ", huntingdon));
		ds.addGroup(huntingdon);

	}

	@Override
	public String getName() {
		return "Add Solihull, Kings Lynn, Huntingdon and Cheshire/Wirral groups";
	}

}

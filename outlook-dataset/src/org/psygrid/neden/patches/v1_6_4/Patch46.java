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

package org.psygrid.neden.patches.v1_6_4;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Factory;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.data.model.hibernate.HibernateFactory;
import org.psygrid.data.model.hibernate.Site;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch46 extends AbstractPatch {

	public String getName() {
		return "Create a new Centre for Peterborough";
	}

	public void applyPatch(DataSet ds, String saml) throws Exception {
		Factory factory = new HibernateFactory();

		Group group = (Group)factory.createGroup("006002");
		group.setLongName("Peterborough");
		group.addSecondaryGroup("002003");

		Site site = new Site("CAMEO North Early Intervention Service", "UKCRN ID", "PE3 6AN", group);
		site.addConsultant("Peter Jones");
		site.setSiteId("7283");
		group.addSite(site);

		ds.addGroup(group);
	}

}

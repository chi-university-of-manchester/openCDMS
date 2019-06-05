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

package org.psygrid.edie.test.patches.v1_1_20;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.data.model.hibernate.Site;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch12 extends AbstractPatch {
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
				addConsultants(s);
			}
		}

		for (int i=0; i < ds.numGroups();i++) {
			Group g = ds.getGroup(i);
			for (int j=0; j < g.numSites(); j++) {
				Site s = g.getSite(j);
				if (s.getConsultants().size() == 0) {
					//Should be four sites in total
					System.out.println("The site "+s.getSiteName()+" has no consultants");
				}
			}
		}

	}

	private void addConsultants(Site site) {

		if ("Faculty of Medical and Human Sciences".equals(site.getSiteName())) {
			//M13 9PL
			site.addConsultant("Tony Morrison");
		}
		else if ("School of Health Sciences".equals(site.getSiteName())) {
			//B15 2TT
			site.addConsultant("Max Birchwood");
		}
		else if ("School of Clinical Medicine".equals(site.getSiteName())) {
			//CB2 2QQ
			site.addConsultant("Peter Jones");
		}
		else if ("University of East Anglia".equals(site.getSiteName())) {
			//NR4 7TJ
			site.addConsultant("David Fowler");
		}
		else if ("Gartnavel Royal Hospital".equals(site.getSiteName())) {
			//G12 0XH
			site.addConsultant("Andew Gumley");
		}
	}

}

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

package org.psygrid.outlook.patches.v1_1_20;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.data.model.hibernate.Site;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch52 extends AbstractPatch {
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

		if ("Bath NHS House".equals(site.getSiteName())) {
			site.addConsultant("Sarah Sullivan");
		}
		else if ("Hellesdon Hospital".equals(site.getSiteName())) {
			site.addConsultant("Peter Jones");
		}
		else if ("CAMEO Cambridge".equals(site.getSiteName())) {
			site.addConsultant("Peter Jones");
		}
		else if ("Mapperley Hospital".equals(site.getSiteName())) {
			site.addConsultant("Hugh Middleton");
		}
		else if ("Trust HQ, Lincolnshire".equals(site.getSiteName())) {
			site.addConsultant("Hugh Middleton");
		}
		else if ("St Nicholas Hospital".equals(site.getSiteName())) {
			//TODO North-East, Newcastle
		}
		else if ("Springfield University Hospital".equals(site.getSiteName())) {
			site.addConsultant("Tom Barnes");
		}
		else if ("Trust HQ, Central & North West London".equals(site.getSiteName())) {
			site.addConsultant("Tom Barnes");
		}
		else if ("Chorlton House".equals(site.getSiteName())) {
			site.addConsultant("Shôn Lewis");
		}
		else if ("Bethlem Royal Hospital".equals(site.getSiteName())) {
			site.addConsultant("Til Wykes");
		}
		else if ("Trust HQ, Birmingham and Solihull".equals(site.getSiteName())) {
			site.addConsultant("Max Birchwood");
		}
		else if ("Kingfisher House".equals(site.getSiteName())) {
			site.addConsultant("Peter Jones");
		}
		else if ("Prestwich Hospital".equals(site.getSiteName())) {
			site.addConsultant("Shôn Lewis");
		}
		else if ("Blackpool and Morecambe EIS".equals(site.getSiteName())) {
			site.addConsultant("Max Marshall");
		}
		else if ("Preston EIS".equals(site.getSiteName())) {
			site.addConsultant("Max Marshall");
		}
		else if ("Blackburn EIS".equals(site.getSiteName())) {
			site.addConsultant("Max Marshall");
		}

	}

}

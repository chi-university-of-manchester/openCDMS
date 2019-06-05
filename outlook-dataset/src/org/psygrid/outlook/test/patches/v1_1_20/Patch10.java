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

package org.psygrid.outlook.test.patches.v1_1_20;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.data.model.hibernate.Site;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch10 extends AbstractPatch {
	@Override
	public String getName() {
		return "Update sites to add consultants.";
	}

	/*	private enum sites = "Bath NHS House", "Hellesdon Hospital", "CAMEO Cambridge", "Mapperley Hospital",
	"Trust HQ, Lincolnshire", "St Nicholas Hospital", "Springfield University Hospital",
	"Trust HQ, Central & North West London", "Chorlton House", "Bethlem Royal Hospital",
	"Trust HQ, Birmingham and Solihull", "Kingfisher House", "Prestwich Hospital", "Blackpool and Morecambe EIS",
	"Preston EIS", "Blackburn EIS";
	 */
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
			site.addConsultant("Bob");
		}
		else if ("Hellesdon Hospital".equals(site.getSiteName())) {
			site.addConsultant("Test 1");
			site.addConsultant("Test 2");
		}
		else if ("CAMEO Cambridge".equals(site.getSiteName())) {

		}
		else if ("Mapperley Hospital".equals(site.getSiteName())) {
			site.addConsultant("Test3");
		}
		else if ("Trust HQ, Lincolnshire".equals(site.getSiteName())) {
			site.addConsultant("Test4");
		}
		else if ("St Nicholas Hospital".equals(site.getSiteName())) {

		}
		else if ("Springfield University Hospital".equals(site.getSiteName())) {
			site.addConsultant("Test 5");
			site.addConsultant("Test 6");
			site.addConsultant("Test 7");
		}
		else if ("Trust HQ, Central & North West London".equals(site.getSiteName())) {
			site.addConsultant("Test 8");
		}
		else if ("Chorlton House".equals(site.getSiteName())) {
			site.addConsultant("Test 9");
		}
		else if ("Bethlem Royal Hospital".equals(site.getSiteName())) {
			site.addConsultant("Test 10");
		}
		else if ("Trust HQ, Birmingham and Solihull".equals(site.getSiteName())) {
			site.addConsultant("Test 11");
		}
		else if ("Kingfisher House".equals(site.getSiteName())) {
			site.addConsultant("Test 12");
		}
		else if ("Prestwich Hospital".equals(site.getSiteName())) {

		}
		else if ("Blackpool and Morecambe EIS".equals(site.getSiteName())) {
			site.addConsultant("Test 13");
			site.addConsultant("Test 14");
		}
		else if ("Preston EIS".equals(site.getSiteName())) {
			site.addConsultant("Test 15");
		}
		else if ("Blackburn EIS".equals(site.getSiteName())) {

		}


	}

}

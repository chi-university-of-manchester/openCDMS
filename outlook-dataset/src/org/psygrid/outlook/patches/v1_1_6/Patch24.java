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

package org.psygrid.outlook.patches.v1_1_6;

import java.util.ArrayList;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.data.model.hibernate.Site;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch24 extends AbstractPatch {

	public String getName() {
		return "Add sites to the groups";
	}

	//    Bristol Avon-Avon and Wiltshire Mental Health Partnership
	//    001001
	//    East Anglia-Norfolk and Waveney Mental Health Partnership Trust
	//    002001
	//    East Anglia-Cambridge CAMEO
	//    002002
	//    East Anglia-Peterbrough NHS Trust
	//    002003
	//    East Midlands-Nottinghamshire Healthcare NHS Trust
	//    003001
	//    East Midlands-Lincolnshire Partnership Trust
	//    003002
	//    North East-Newcastle, Northumberland and North Tyneside Mental  Health Trust
	//    004001
	//    North London-South West London and St. Georges Trust
	//    005001
	//    North London-Central and West London Trust
	//    005002
	//    North West-Manchester Mental Health and Social Care Trust
	//    006001
	//    Bolton, Salford Trafford
	//    006002
	//    South London-South London and Maudsely Trust
	//    007001
	//    West Midlands-Birmingham and Solihull Mental Health Trust
	//    008001

	public void applyPatch(DataSet ds, String saml) throws Exception {
		int numGroups = ds.numGroups();

		for (int i = 0; i < numGroups; i++) {
			Group g = (Group) ds.getGroup(i);
			if (g.getName().equals("001001")) {
				ArrayList<Site> sl = new ArrayList<Site>();
				sl.add(new Site("Bath NHS House", "N0000688", "BA1 3QE", g));
				g.setSites(sl);
			}
			if (g.getName().equals("002001")) {
				ArrayList<Site> sl = new ArrayList<Site>();
				sl
						.add(new Site("Hellesdon Hospital", "N0000696",
								"NR6 5BE", g));
				g.setSites(sl);
			}
			if (g.getName().equals("002002")) {
				ArrayList<Site> sl = new ArrayList<Site>();
				sl.add(new Site("CAMEO Cambridge", "N0000683", "CB1 5EE", g));
				g.setSites(sl);
			}
			if (g.getName().equals("002003")) {
				ArrayList<Site> sl = new ArrayList<Site>();
				sl.add(new Site("Kingfisher House", "N0000697", "PE29 6FH", g));
				g.setSites(sl);
			}
			if (g.getName().equals("003001")) {
				ArrayList<Site> sl = new ArrayList<Site>();
				sl
						.add(new Site("Mapperley Hospital", "N00000689",
								"NG3 6AA", g));
				g.setSites(sl);
			}
			if (g.getName().equals("003002")) {
				ArrayList<Site> sl = new ArrayList<Site>();
				sl.add(new Site("Trust HQ, Lincolnshire", "N0000690",
						"LN4 2HN", g));
				g.setSites(sl);
			}
			if (g.getName().equals("004001")) {
				ArrayList<Site> sl = new ArrayList<Site>();
				sl.add(new Site("St Nicholas Hospital", "N0000691", "NE3 3XT",
						g));
				g.setSites(sl);
			}
			if (g.getName().equals("005001")) {
				ArrayList<Site> sl = new ArrayList<Site>();
				sl.add(new Site("Springfield University Hospital", "N0000687",
						"SW17 7DJ", g));
				g.setSites(sl);
			}
			if (g.getName().equals("005002")) {
				ArrayList<Site> sl = new ArrayList<Site>();
				sl.add(new Site("Trust HQ, Central & North West London",
						"N0000692", "W2 6LA", g));
				g.setSites(sl);
			}
			if (g.getName().equals("006001")) {
				ArrayList<Site> sl = new ArrayList<Site>();
				sl.add(new Site("Chorlton House", "N0000693", "M21 9UN", g));
				g.setSites(sl);
			}
			if (g.getName().equals("006002")) {
				ArrayList<Site> sl = new ArrayList<Site>();
				sl
						.add(new Site("Prestwich Hospital", "N0000686",
								"M25 3BL", g));
				g.setSites(sl);
			}
			if (g.getName().equals("007001")) {
				ArrayList<Site> sl = new ArrayList<Site>();
				sl.add(new Site("Bethlem Royal Hospital", "N0000694",
						"SE5 8AZ", g));
				g.setSites(sl);
			}
			if (g.getName().equals("008001")) {
				ArrayList<Site> sl = new ArrayList<Site>();
				sl.add(new Site("Trust HQ, Birmingham and Solihull",
						"N0000696", "B1 3RB", g));
				g.setSites(sl);
			}
		}
	}

}

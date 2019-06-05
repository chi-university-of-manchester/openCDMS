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
public class Patch23 extends AbstractPatch {

	/* (non-Javadoc)
	 * @see org.psygrid.outlook.patches.AbstractPatch#applyPatch(org.psygrid.data.model.IDataSet, java.lang.String)
	 */
	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		Factory factory = new HibernateFactory();

		Group grp35 = (Group)factory.createGroup("651006");
		grp35.setLongName("East Lancashire NHS trust");
		Site site35a = new Site("Burnley General Hospital", "N0000287", "?", grp35);
		site35a.addConsultant("Dr Claire Smith");
		grp35.addSite(site35a);
		ds.addGroup(grp35);

		Group grp36 = (Group)factory.createGroup("651007");
		grp36.setLongName("St Helens and Knowsley Hospitals NHS");
		Site site36a = new Site("St Helens and Knowsley Hospitals", "N0000287", "?", grp36);
		site36a.addConsultant("Dr Kevin Hardy");
		grp36.addSite(site36a);
		ds.addGroup(grp36);

		Group grp37 = (Group)factory.createGroup("651008");
		grp37.setLongName("University Hospital of South Manchester NHS Foundation Trust");
		Site site37a = new Site("Wythenshawe Hospital", "N0000172", "?", grp37);
		site37a.addConsultant("Dr Andrew Bradbury");
		grp37.addSite(site37a);
		ds.addGroup(grp37);

		Group grp38 = (Group)factory.createGroup("651009");
		grp38.setLongName("Wirral University Teaching Hospital NHS Foundation Trust");
		Site site38a = new Site("Arrowe Park Hospital", "N0000009", "?", grp38);
		site38a.addConsultant("Dr King Sun Leong");
		grp38.addSite(site38a);
		ds.addGroup(grp38);

		Group grp39 = (Group)factory.createGroup("650008");
		grp39.setLongName("West Hertfordshire hospital NHS trust");
		Site site39a = new Site("Hemel Hempstead General Hospital", "N0000189", "?", grp39);
		site39a.addConsultant("Dr Colin Johnson");
		grp39.addSite(site39a);
		ds.addGroup(grp39);

		Group grp40 = (Group)factory.createGroup("649002");
		grp40.setLongName("Wycombe Hospital");
		Site site40a = new Site("Wycombe Hospital", "N0000106", "?", grp40);
		site40a.addConsultant("Dr Ian Gallen");
		grp40.addSite(site40a);
		ds.addGroup(grp40);

		Group grp41 = (Group)factory.createGroup("649003");
		grp41.setLongName("George Eliot Hospital");
		Site site41a = new Site("George Eliot Hospital", "N0000187", "?", grp41);
		site41a.addConsultant("Dr Vinod Patel");
		grp41.addSite(site41a);
		ds.addGroup(grp41);

		Group grp42 = (Group)factory.createGroup("651010");
		grp42.setLongName("Countess of Chester Hospital NHS Foundation Trust");
		Site site42a = new Site("Countess of Chester Hospital", "N0000083", "?", grp42);
		site42a.addConsultant("Dr Niru Goenka");
		grp42.addSite(site42a);
		ds.addGroup(grp42);

		Group grp43 = (Group)factory.createGroup("654005");
		grp43.setLongName("James Paget University Hospitals NHS Foundation Trust");
		Site site43a = new Site("Northgate hospital", "N0000749", "?", grp43);
		site43a.addConsultant("Dr Sangeeta Garg");
		grp43.addSite(site43a);
		ds.addGroup(grp43);

		Group grp44 = (Group)factory.createGroup("655002");
		grp44.setLongName("United Lincolnshire Hospitals NHS Trust");
		Site site44a = new Site("Lincoln County Hospital", "N0000089", "?", grp44);
		site44a.addConsultant("Dr Keith Sands");
		grp44.addSite(site44a);
		ds.addGroup(grp44);

		Group grp45 = (Group)factory.createGroup("640006");
		grp45.setLongName("Queen Elizabeth Hospital NHS Trust");
		Site site45a = new Site("Queen Elizabeth Hospital Woolwich", "0000255", "?", grp45);
		site45a.addConsultant("Dr Jola Weaver");
		grp45.addSite(site45a);
		ds.addGroup(grp45);

		Group grp46 = (Group)factory.createGroup("640008");
		grp46.setLongName("Newcastle Diabetes Centre");
		Site site46a = new Site("Newcastle General Hospital: Diabetes OPD", "N0000004", "?", grp46);
		site46a.addConsultant("Prof Mark Walker");
		grp46.addSite(site46a);
		ds.addGroup(grp46);

		Group grp47 = (Group)factory.createGroup("640009");
		grp47.setLongName("South Tyneside Healthcare NHS Trust");
		Site site47a = new Site("South Tyneside District General Hospital", "N0000073", "?", grp47);
		site47a.addConsultant("Dr John Parr");
		grp47.addSite(site47a);
		ds.addGroup(grp47);

		Group grp48 = (Group)factory.createGroup("640010");
		grp48.setLongName("Northumbria Healthcare NHS Trust");
		Site site48a = new Site("North Tyneside General Hospital", "N0000279", "?", grp48);
		site48a.addConsultant("Dr Simon Eaton");
		grp48.addSite(site48a);
		ds.addGroup(grp48);

		Group grp49 = (Group)factory.createGroup("640011");
		grp49.setLongName("North Tees and Hartlepool NHS Foundation Trust");
		Site site49a = new Site("North Tees and Hartlepool hospital", "N0000390", "?", grp49);
		site49a.addConsultant("Dr Steve Jones");
		grp49.addSite(site49a);
		ds.addGroup(grp49);

	}

	/* (non-Javadoc)
	 * @see org.psygrid.outlook.patches.AbstractPatch#getName()
	 */
	@Override
	public String getName() {
		return "Add lots of groups";
	}

}

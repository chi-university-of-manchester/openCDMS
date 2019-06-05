/*
Copyright (c) 2006-2009, The University of Manchester, UK.

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

package org.psygrid.drn.address.patches.v1_6_4;

import java.util.List;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.data.model.hibernate.Site;
import org.psygrid.data.reporting.client.ReportsClient;
import org.psygrid.data.reporting.definition.IReport;
import org.psygrid.drn.address.Reports;
import org.psygrid.outlook.patches.AbstractPatch;

/**
 * @author Rob Harper
 *
 */
public class Patch34 extends AbstractPatch {

	/* (non-Javadoc)
	 * @see org.psygrid.outlook.patches.AbstractPatch#applyPatch(org.psygrid.data.model.IDataSet, java.lang.String)
	 */
	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		Group eastLancs = (Group)ds.getGroup(29);
		if ( !"651003".equals(eastLancs.getName())){
			throw new RuntimeException("This is not the 651003 group, it is "+eastLancs.getName());
		}
		eastLancs.setLongName("East Lancashire NHS Trust");
		
		Site blackburn = (Site)eastLancs.getSite(0);
		if ( !"Royal Blackburn Hospital".equals(blackburn.getSiteName())){
			throw new RuntimeException("This is not the Royal Blackburn Hospital site, it is "+blackburn.getSiteName());
		}
		blackburn.addConsultant("Dr Ramatoola");
		
		Site burnley = new Site("Burnley General Hospital", "N0000287", "BB10 2PQ");
		burnley.addConsultant("Miles Riddle");
		eastLancs.addSite(burnley);
		
		Group g651004 = (Group)ds.getGroup(30);
		if ( !"651004".equals(g651004.getName())){
			throw new RuntimeException("This is not the 651004 group, it is "+g651004.getName());
		}
		g651004.setLongName("DEPRECATED");

		Group g651006 = (Group)ds.getGroup(34);
		if ( !"651006".equals(g651006.getName())){
			throw new RuntimeException("This is not the 651006 group, it is "+g651006.getName());
		}
		g651006.setLongName("DEPRECATED");

		
		ReportsClient client = new ReportsClient();

		List<IReport> reports = client.getReportsOfType(ds.getProjectCode(), "management", saml);
		for ( IReport report: reports ){
			if ( report.getTitle().endsWith("Recruitment Progress Report(North West England)") ||
				 report.getTitle().endsWith("Principal Investigator (North West England) Report") ||
				 report.getTitle().endsWith("Recruitment Manager Report") ||
				 report.getTitle().endsWith("Recruitment Progress Report")){
				System.out.println("Deleting "+report.getTitle());
				client.deleteReport(ds.getId(), report.getId(), saml);
			}
		}
		
		client.saveReport(Reports.piNorthWestMgmtReport(ds), saml);
		client.saveReport(Reports.recruitmentInNorthWestReport(ds), saml);
		client.saveReport(Reports.cpmRecruitmentReport(ds), saml);
		client.saveReport(Reports.cpmMgmtReport(ds), saml);
		
	}

	/* (non-Javadoc)
	 * @see org.psygrid.outlook.patches.AbstractPatch#getName()
	 */
	@Override
	public String getName() {
		return "Correct East Lancs group and sort out NW reports";
	}

}

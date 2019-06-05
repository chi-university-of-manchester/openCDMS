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

package org.psygrid.outlook.patches.v1_1_7;

import java.util.ArrayList;
import java.util.List;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.reporting.client.ReportsClient;
import org.psygrid.data.reporting.definition.IReport;
import org.psygrid.outlook.Reports;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch29 extends AbstractPatch {

	@Override
	public String getName() {
		return "Update all reports containing a project management chart and update ukCRN and recruitment reports to show raw data when emailed.";
	}

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {

		ReportsClient client = null;
		client = new ReportsClient();

		/* delete the old reports */
		System.out.println("Removing old mgmt reports");
		List<IReport> reports = client.getAllReportsByDataSet(ds.getId(), saml);

		List<String> titles = new ArrayList<String>();
		titles.add(Reports.ciMgmtReport(ds).getTitle());
		titles.add(Reports.cpmMgmtReport(ds).getTitle());
		titles.add(Reports.piBristolAvonMgmtReport(ds).getTitle());
		titles.add(Reports.piEastAngliaMgmtReport(ds).getTitle());
		titles.add(Reports.piEastMidlandsMgmtReport(ds).getTitle());
		titles.add(Reports.piNorthEastMgmtReport(ds).getTitle());
		titles.add(Reports.piNorthLondonMgmtReport(ds).getTitle());
		titles.add(Reports.piNorthWestMgmtReport(ds).getTitle());
		titles.add(Reports.piSouthLondonMgmtReport(ds).getTitle());
		titles.add(Reports.piWestMidlandsMgmtReport(ds).getTitle());
		titles.add(Reports.ukCRNReport(ds).getTitle());
		titles.add(Reports.recruitmentInBristolAvonReport(ds).getTitle());
		titles.add(Reports.recruitmentInEastAngliaReport(ds).getTitle());
		titles.add(Reports.recruitmentInEastMidlandsReport(ds).getTitle());
		titles.add(Reports.recruitmentInNorthEastReport(ds).getTitle());
		titles.add(Reports.recruitmentInNorthLondonReport(ds).getTitle());
		titles.add(Reports.recruitmentInNorthWestReport(ds).getTitle());
		titles.add(Reports.recruitmentInSouthLondonReport(ds).getTitle());
		titles.add(Reports.recruitmentInWestMidlandsReport(ds).getTitle());
		titles.add(Reports.cpmRecruitmentReport(ds).getTitle());
		titles.add(Reports.ciRecruitmentReport(ds).getTitle());

		for(IReport report : reports){
			for (String title: titles) {
				//Remove the report if it matches one of the above management reports
				//(obviously assumes that the title is not updated)
				if (report.getTitle().equals(title)) {
					client.deleteReport(ds.getId(), report.getId(), saml);
				}
			}

		}

		/* add the new reports */
		System.out.println("Creating new reports");
		createReports(client, ds, saml);

	}

	private void createReports(ReportsClient client, DataSet ds, String saml) {

		try {
			client.saveReport(Reports.ciMgmtReport(ds), saml);
			client.saveReport(Reports.cpmMgmtReport(ds), saml);
			client.saveReport(Reports.piBristolAvonMgmtReport(ds), saml);
			client.saveReport(Reports.piEastAngliaMgmtReport(ds), saml);
			client.saveReport(Reports.piEastMidlandsMgmtReport(ds), saml);
			client.saveReport(Reports.piNorthEastMgmtReport(ds), saml);
			client.saveReport(Reports.piNorthLondonMgmtReport(ds), saml);
			client.saveReport(Reports.piNorthWestMgmtReport(ds), saml);
			client.saveReport(Reports.piSouthLondonMgmtReport(ds), saml);
			client.saveReport(Reports.piWestMidlandsMgmtReport(ds), saml);
			client.saveReport(Reports.ukCRNReport(ds), saml);
			client.saveReport(Reports.recruitmentInBristolAvonReport(ds), saml);
			client.saveReport(Reports.recruitmentInEastAngliaReport(ds), saml);
			client.saveReport(Reports.recruitmentInEastMidlandsReport(ds), saml);
			client.saveReport(Reports.recruitmentInNorthEastReport(ds), saml);
			client.saveReport(Reports.recruitmentInNorthLondonReport(ds), saml);
			client.saveReport(Reports.recruitmentInNorthWestReport(ds), saml);
			client.saveReport(Reports.recruitmentInSouthLondonReport(ds), saml);
			client.saveReport(Reports.recruitmentInWestMidlandsReport(ds), saml);
			client.saveReport(Reports.ciRecruitmentReport(ds), saml);
			client.saveReport(Reports.cpmRecruitmentReport(ds), saml);
		}
		catch (Exception e) {
			System.out.println("Problem saving report: "+e.getMessage());
			e.printStackTrace();
		}
	}


}
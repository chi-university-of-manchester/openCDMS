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

package org.psygrid.neden.patches.v1_1_7;

import java.util.ArrayList;
import java.util.List;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.reporting.client.ReportsClient;
import org.psygrid.data.reporting.definition.IReport;
import org.psygrid.neden.Reports;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch13 extends AbstractPatch {
	@Override
	public String getName() {
		return "Update all reports containing a project management chart and update ukCRN and recruitment reports to show raw data when emailed.";
	}

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {

		ReportsClient client = null;
		client = new ReportsClient();

		/* delete the old reports */
		System.out.println("Removing old reports");
		List<IReport> reports = client.getAllReportsByDataSet(ds.getId(), saml);

		List<String> titles = new ArrayList<String>();
		titles.add(Reports.ciMgmtReport(ds).getTitle());
		titles.add(Reports.cpmMgmtReport(ds).getTitle());
		titles.add(Reports.piCambridgeMgmtReport(ds).getTitle());
		titles.add(Reports.piCornwallMgmtReport(ds).getTitle());
		titles.add(Reports.piHeartBirminghamEastMgmtReport(ds).getTitle());
		titles.add(Reports.piHeartBirminghamWestMgmtReport(ds).getTitle());
		titles.add(Reports.piLancashireMgmtReport(ds).getTitle());
		titles.add(Reports.piNorfolkMgmtReport(ds).getTitle());
		titles.add(Reports.piPCTBirminghamEastMgmtReport(ds).getTitle());
		titles.add(Reports.piPCTBirminghamSouthMgmtReport(ds).getTitle());
		titles.add(Reports.rmMgmtReport(ds).getTitle());
		titles.add(Reports.ukCRNReport(ds).getTitle());
		titles.add(Reports.recruitmentInCambridgeReport(ds).getTitle());
		titles.add(Reports.recruitmentInCornwallReport(ds).getTitle());
		titles.add(Reports.recruitmentInHeartBirminghamEastReport(ds).getTitle());
		titles.add(Reports.recruitmentInHeartBirminghamWestReport(ds).getTitle());
		titles.add(Reports.recruitmentInLancashireReport(ds).getTitle());
		titles.add(Reports.recruitmentInNorfolkReport(ds).getTitle());
		titles.add(Reports.recruitmentInPCTBirminghamEastReport(ds).getTitle());
		titles.add(Reports.recruitmentInPCTBirminghamSouthReport(ds).getTitle());

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
			client.saveReport(Reports.piCambridgeMgmtReport(ds), saml);
			client.saveReport(Reports.piCornwallMgmtReport(ds), saml);
			client.saveReport(Reports.piHeartBirminghamEastMgmtReport(ds), saml);
			client.saveReport(Reports.piHeartBirminghamWestMgmtReport(ds), saml);
			client.saveReport(Reports.piLancashireMgmtReport(ds), saml);
			client.saveReport(Reports.piNorfolkMgmtReport(ds), saml);
			client.saveReport(Reports.piPCTBirminghamEastMgmtReport(ds), saml);
			client.saveReport(Reports.piPCTBirminghamSouthMgmtReport(ds), saml);
			client.saveReport(Reports.rmMgmtReport(ds), saml);
			client.saveReport(Reports.ukCRNReport(ds), saml);
			client.saveReport(Reports.recruitmentInCambridgeReport(ds), saml);
			client.saveReport(Reports.recruitmentInCornwallReport(ds), saml);
			client.saveReport(Reports.recruitmentInHeartBirminghamEastReport(ds), saml);
			client.saveReport(Reports.recruitmentInHeartBirminghamWestReport(ds), saml);
			client.saveReport(Reports.recruitmentInLancashireReport(ds), saml);
			client.saveReport(Reports.recruitmentInNorfolkReport(ds), saml);
			client.saveReport(Reports.recruitmentInPCTBirminghamEastReport(ds), saml);
			client.saveReport(Reports.recruitmentInPCTBirminghamSouthReport(ds), saml);
			client.saveReport(Reports.ciRecruitmentReport(ds), saml);
			client.saveReport(Reports.cpmRecruitmentReport(ds), saml);
		}
		catch (Exception e) {
			System.out.println("Problem saving report: "+e.getMessage());
			e.printStackTrace();
		}
	}

}

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

package org.psygrid.edie.patches.v1_1_10;

import java.util.ArrayList;
import java.util.List;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.reporting.client.ReportsClient;
import org.psygrid.data.reporting.definition.IReport;
import org.psygrid.edie.Reports;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch23 extends AbstractPatch {
	@Override
	public String getName() {
		return "Update recruitment progress reports to add targets.";
	}

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {

		ReportsClient client = null;
		client = new ReportsClient();

		/* delete the old reports */
		System.out.println("Removing old recruitment progress reports");
		List<IReport> reports = client.getAllReportsByDataSet(ds.getId(), saml);

		List<String> titles = new ArrayList<String>();
		titles.add(Reports.ukCRNReport(ds).getTitle());
		titles.add(Reports.recruitmentInCambridgeReport(ds).getTitle());
		titles.add(Reports.recruitmentInEastAngliaReport(ds).getTitle());
		titles.add(Reports.recruitmentInBirminghamReport(ds).getTitle());
		titles.add(Reports.recruitmentInGlasgowReport(ds).getTitle());
		titles.add(Reports.recruitmentInManchesterReport(ds).getTitle());
		titles.add(Reports.ciRecruitmentReport(ds).getTitle());
		titles.add(Reports.cpmRecruitmentReport(ds).getTitle());

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
		System.out.println("Creating new recruitment progress reports");
		createReports(client, ds, saml);

	}

	private void createReports(ReportsClient client, DataSet ds, String saml) {

		try {
			client.saveReport(Reports.ukCRNReport(ds), saml);
			client.saveReport(Reports.recruitmentInCambridgeReport(ds), saml);
			client.saveReport(Reports.recruitmentInCambridgeReport(ds), saml);
			client.saveReport(Reports.recruitmentInEastAngliaReport(ds), saml);
			client.saveReport(Reports.recruitmentInBirminghamReport(ds), saml);
			client.saveReport(Reports.recruitmentInGlasgowReport(ds), saml);
			client.saveReport(Reports.recruitmentInManchesterReport(ds), saml);
			client.saveReport(Reports.ciRecruitmentReport(ds), saml);
			client.saveReport(Reports.cpmRecruitmentReport(ds), saml);
		}
		catch (Exception e) {
			System.out.println("Problem saving report: "+e.getMessage());
			e.printStackTrace();
		}
	}

}
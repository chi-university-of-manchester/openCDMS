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

package org.psygrid.neden.patches.v1_1_8;

import java.util.ArrayList;
import java.util.List;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.reporting.client.ReportsClient;
import org.psygrid.data.reporting.definition.IReport;
import org.psygrid.neden.Reports;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch14 extends AbstractPatch {
	@Override
	public String getName() {
		return "Remove duplicate reports and correct the title of the 'Longest and Shortest Durations of Untreated Psychosis' report." +
				" This corrects report patches 8 and 13.";
	}

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {

		ReportsClient client = null;
		client = new ReportsClient();

		/* delete the old reports */
		System.out.println("Removing old reports");
		List<IReport> reports = client.getAllReportsByDataSet(ds.getId(), saml);

		List<String> titles = new ArrayList<String>();
		titles.add("Duration of Untreated Psychosis (DUP) - Baseline");  //remove the highlow chart - the normal DUP trends chart has a slightly different title
		titles.add(Reports.dupTrendsReportBaseline(ds).getTitle());
		titles.add(Reports.panssReportBaseline(ds).getTitle());
		titles.add(Reports.ciRecruitmentReport(ds).getTitle());
		titles.add(Reports.cpmRecruitmentReport(ds).getTitle());

		for(IReport report : reports){
			for (String title: titles) {
				//Remove the report if it matches one of the above management reports
				//(obviously assumes that the title is not updated)
				if (report.getTitle().equals(title)) {
					client.deleteReport(ds.getId(), report.getId(), saml);
					break;
				}
			}
		}

		/* add the new reports */
		System.out.println("Creating new reports");
		createReports(client, ds, saml);

	}

	private void createReports(ReportsClient client, DataSet ds, String saml) {

		try {
			client.saveReport(Reports.dupHighLowTrendsReport(ds), saml);
			client.saveReport(Reports.dupTrendsReportBaseline(ds), saml);
			client.saveReport(Reports.panssReportBaseline(ds), saml);
			client.saveReport(Reports.ciRecruitmentReport(ds), saml);
			client.saveReport(Reports.cpmRecruitmentReport(ds), saml);
		}
		catch (Exception e) {
			System.out.println("Problem saving report: "+e.getMessage());
			e.printStackTrace();
		}
	}

}

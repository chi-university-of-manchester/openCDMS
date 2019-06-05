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

import java.util.List;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.reporting.client.ReportsClient;
import org.psygrid.data.reporting.definition.IReport;
import org.psygrid.outlook.Reports;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch25 extends AbstractPatch {

	@Override
	public String getName() {
		return "Update all reports for the new reporting framework and add new reports";
	}

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {

		ReportsClient client = null;
		client = new ReportsClient();

		/* delete the old reports */
		System.out.println("Removing reports.. ");
		List<IReport> reports = client.getAllReportsByDataSet(ds.getId(), saml);
		for(IReport report : reports){
			client.deleteReport(ds.getId(), report.getId(), saml);
		}

		/* add the new reports */
		System.out.println("Creating reports..");
		createReports(client, ds, saml);

	}

	private void createReports(ReportsClient client, DataSet ds, String saml) {

		try {
			client.saveReport(Reports.calgaryScaleReport12Months(ds), saml);
			client.saveReport(Reports.calgaryScaleReport6Months(ds), saml);
			client.saveReport(Reports.calgaryScaleReportBaseline(ds), saml);
			client.saveReport(Reports.ciMgmtReport(ds), saml);
			client.saveReport(Reports.ciRecruitmentReport(ds), saml);
			client.saveReport(Reports.cpmMgmtReport(ds), saml);
			client.saveReport(Reports.cpmRecruitmentReport(ds), saml);
			client.saveReport(Reports.drugCheckReport12Months(ds), saml);
			client.saveReport(Reports.drugCheckReportBaseline(ds), saml);
			client.saveReport(Reports.drugSideEffectsReport12Months(ds), saml);
			client.saveReport(Reports.drugSideEffectsReport6Months(ds), saml);
			client.saveReport(Reports.drugSideEffectsReportBaseline(ds), saml);
			client.saveReport(Reports.eq5dReport12Months(ds), saml);
			client.saveReport(Reports.eq5dReportBaseline(ds), saml);
			client.saveReport(Reports.gafReport12Months(ds), saml);
			client.saveReport(Reports.gafReportBaseline(ds), saml);
			client.saveReport(Reports.insightScaleReport12Months(ds), saml);
			client.saveReport(Reports.insightScaleReportBaseline(ds), saml);
			client.saveReport(Reports.panssReport12Months(ds), saml);
			client.saveReport(Reports.panssReport6Months(ds), saml);
			client.saveReport(Reports.panssReportBaseline(ds), saml);
			client.saveReport(Reports.piBristolAvonMgmtReport(ds), saml);
			client.saveReport(Reports.piEastAngliaMgmtReport(ds), saml);
			client.saveReport(Reports.piEastMidlandsMgmtReport(ds), saml);
			client.saveReport(Reports.piNorthEastMgmtReport(ds), saml);
			client.saveReport(Reports.piNorthLondonMgmtReport(ds), saml);
			client.saveReport(Reports.piNorthWestMgmtReport(ds), saml);
			client.saveReport(Reports.piSouthLondonMgmtReport(ds), saml);
			client.saveReport(Reports.piWestMidlandsMgmtReport(ds), saml);
			client.saveReport(Reports.premorbidAdjustmentReport(ds), saml);
			client.saveReport(Reports.recruitmentInBristolAvonReport(ds), saml);
			client.saveReport(Reports.recruitmentInEastAngliaReport(ds), saml);
			client.saveReport(Reports.recruitmentInEastMidlandsReport(ds), saml);
			client.saveReport(Reports.recruitmentInNorthEastReport(ds), saml);
			client.saveReport(Reports.recruitmentInNorthLondonReport(ds), saml);
			client.saveReport(Reports.recruitmentInNorthWestReport(ds), saml);
			client.saveReport(Reports.recruitmentInSouthLondonReport(ds), saml);
			client.saveReport(Reports.recruitmentInWestMidlandsReport(ds), saml);
			client.saveReport(Reports.ukCRNReport(ds), saml);
			client.saveReport(Reports.youngMania12Months(ds), saml);
			client.saveReport(Reports.youngMania6Months(ds), saml);
			client.saveReport(Reports.youngManiaBaseline(ds), saml);

	        client.saveReport(Reports.insightScaleTrendsReportBaseline(ds), saml);
	        client.saveReport(Reports.insightScaleTrendsReport12Months(ds), saml);
	        client.saveReport(Reports.youngManiaTrendsReportBaseline(ds), saml);
	        client.saveReport(Reports.youngManiaTrendsReport6Months(ds), saml);
	        client.saveReport(Reports.youngManiaTrendsReport12Months(ds), saml);
	        client.saveReport(Reports.calgaryScaleTrendsReportBaseline(ds), saml);
	        client.saveReport(Reports.calgaryScaleTrendsReport6Months(ds), saml);
	        client.saveReport(Reports.calgaryScaleTrendsReport12Months(ds), saml);
	        client.saveReport(Reports.eq5dTrendsReportBaseline(ds), saml);
	        client.saveReport(Reports.eq5dTrendsReport12Months(ds), saml);
	        client.saveReport(Reports.gafTrendsReportBaseline(ds), saml);
	        client.saveReport(Reports.gafTrendsReport12Months(ds), saml);
	        client.saveReport(Reports.pathwaysTrendsReport(ds), saml);
	        client.saveReport(Reports.premorbidTrendsReport(ds), saml);
	        client.saveReport(Reports.dupHighLowTrendsReport(ds), saml);
	        client.saveReport(Reports.panssTrendsReportBaseline(ds), saml);
	        client.saveReport(Reports.panssTrendsReport6Months(ds), saml);
	        client.saveReport(Reports.panssTrendsReport12Months(ds), saml);
	        client.saveReport(Reports.dupTrendsReport(ds), saml);


		}
		catch (Exception e) {
			System.out.println("Problem saving report: "+e.getMessage());
			e.printStackTrace();
		}
	}

}

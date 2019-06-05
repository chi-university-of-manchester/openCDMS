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

package org.psygrid.edie.patches.v1_1_6;

import java.util.List;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.reporting.client.ReportsClient;
import org.psygrid.data.reporting.definition.IReport;
import org.psygrid.edie.Reports;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch5 extends AbstractPatch {

    public boolean isolated(){
   	 return true;
   }

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
			client.saveReport(Reports.ciMgmtReport(ds), saml);
			client.saveReport(Reports.cpmMgmtReport(ds), saml);
			client.saveReport(Reports.piBirminghamMgmtReport(ds), saml);
			client.saveReport(Reports.piCambridgeMgmtReport(ds), saml);
			client.saveReport(Reports.piEastAngliaMgmtReport(ds), saml);
			client.saveReport(Reports.piGlasgowMgmtReport(ds), saml);
			client.saveReport(Reports.piManchesterMgmtReport(ds), saml);
			client.saveReport(Reports.ukCRNReport(ds), saml);
			client.saveReport(Reports.recruitmentReport(ds), saml);
			client.saveReport(Reports.cpmRecruitmentReport(ds), saml);
			client.saveReport(Reports.recruitmentInCambridgeReport(ds), saml);
			client.saveReport(Reports.recruitmentInEastAngliaReport(ds), saml);
			client.saveReport(Reports.recruitmentInEastMidlandsReport(ds), saml);
			client.saveReport(Reports.recruitmentInGlasgowReport(ds), saml);
			client.saveReport(Reports.recruitmentInManchesterReport(ds), saml);
		}
		catch (Exception e) {
			System.out.println("Problem saving report: "+e.getMessage());
			e.printStackTrace();
		}
	}
}
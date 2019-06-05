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

package org.psygrid.matisse.patches.v1_1_16;

import java.util.List;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.reporting.client.ReportsClient;
import org.psygrid.data.reporting.definition.IReport;
import org.psygrid.matisse.Reports;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch2 extends AbstractPatch {
	@Override
	public String getName() {
		return "Update management reports to add action types.";
	}

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {

		ReportsClient client = null;
		client = new ReportsClient();

		/* delete the old reports */
		System.out.println("Removing old management reports");
		List<IReport> reports = client.getReportsOfType(ds.getProjectCode(), "management", saml);

		for(IReport report : reports){
			client.deleteReport(ds.getId(), report.getId(), saml);
		}
		System.out.println("Removed "+reports.size()+" reports");
		/* add the new reports */
		System.out.println("Creating new management reports");
		createReports(client, ds, saml);

	}

	private void createReports(ReportsClient client, DataSet ds, String saml) {

		try {
			client.saveReport(Reports.ciRecruitmentReport(ds), saml);
			client.saveReport(Reports.recruitmentInLondonReport(ds), saml);
			client.saveReport(Reports.recruitmentInCamdenReport(ds), saml);
			client.saveReport(Reports.recruitmentInWestEnglandReport(ds), saml);
			client.saveReport(Reports.recruitmentInNorthernIrelandReport(ds), saml);
			client.saveReport(Reports.ukCRNReport(ds), saml);

			System.out.println("Added 6 reports");
		}
		catch (Exception e) {
			System.out.println("Problem saving report: "+e.getMessage());
			e.printStackTrace();
		}
	}

}

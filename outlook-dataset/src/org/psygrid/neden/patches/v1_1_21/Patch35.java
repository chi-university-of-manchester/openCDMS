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


package org.psygrid.neden.patches.v1_1_21;

import java.util.ArrayList;
import java.util.List;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.reporting.client.ReportsClient;
import org.psygrid.data.reporting.definition.IReport;
import org.psygrid.neden.Reports;
import org.psygrid.outlook.patches.AbstractPatch;

/**
 * @author Rob Harper
 *
 */
public class Patch35 extends AbstractPatch {

	/* (non-Javadoc)
	 * @see org.psygrid.outlook.patches.AbstractPatch#applyPatch(org.psygrid.data.model.IDataSet, java.lang.String)
	 */
	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		ReportsClient client = null;
		client = new ReportsClient();

		List<IReport> reports = client.getAllReportsByDataSet(ds.getId(), saml);

		List<String> titles = new ArrayList<String>();
		titles.add(Reports.cpmMgmtReport(ds).getTitle());
		titles.add(Reports.rmMgmtReport(ds).getTitle());
		titles.add(Reports.cpmRecruitmentReport(ds).getTitle());
		titles.add("Cambridge CAMEO");

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
		client.saveReport(Reports.cpmMgmtReport(ds), saml);
		client.saveReport(Reports.rmMgmtReport(ds), saml);
		client.saveReport(Reports.cpmRecruitmentReport(ds), saml);
		client.saveReport(Reports.recruitmentInCambridgeReport(ds), saml);
		client.saveReport(Reports.recruitmentInKingsLynnReport(ds), saml);
		client.saveReport(Reports.recruitmentInSolihullReport(ds), saml);
		client.saveReport(Reports.recruitmentInCheshireWirralReport(ds), saml);

	}

	/* (non-Javadoc)
	 * @see org.psygrid.outlook.patches.AbstractPatch#getName()
	 */
	@Override
	public String getName() {
		return "Add/modify reports for new groups";
	}

}

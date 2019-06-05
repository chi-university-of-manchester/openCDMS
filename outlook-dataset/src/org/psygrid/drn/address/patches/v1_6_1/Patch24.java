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

import java.util.List;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.reporting.client.ReportsClient;
import org.psygrid.data.reporting.definition.IReport;
import org.psygrid.drn.address.Reports;
import org.psygrid.outlook.patches.AbstractPatch;

/**
 * @author Rob Harper
 *
 */
public class Patch24 extends AbstractPatch {

	/* (non-Javadoc)
	 * @see org.psygrid.outlook.patches.AbstractPatch#applyPatch(org.psygrid.data.model.IDataSet, java.lang.String)
	 */
	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		ReportsClient client = new ReportsClient();

		List<IReport> reports = client.getReportsOfType(ds.getProjectCode(), "management", saml);
		for ( IReport report: reports ){
			if ( report.getTitle().endsWith("Recruitment Progress Report(North West England)") ||
				 report.getTitle().endsWith("Principal Investigator (North West England) Report") ||
				 report.getTitle().endsWith("Recruitment Progress Report(North East and Cumbria)") ||
				 report.getTitle().endsWith("Principal Investigator (North East and Cumbria) Report") ||
				 report.getTitle().endsWith("Recruitment Progress Report(North West London)") ||
				 report.getTitle().endsWith("Principal Investigator (North West London) Report") ||
				 report.getTitle().endsWith("Recruitment Progress Report(Thames Valley)") ||
				 report.getTitle().endsWith("Principal Investigator (Thames Valley) Report") ||
				 report.getTitle().endsWith("Recruitment Progress Report(South East Midlands)") ||
				 report.getTitle().endsWith("Principal Investigator (South East Midlands) Report") ||
				 report.getTitle().endsWith("Recruitment Manager Report") ||
				 report.getTitle().endsWith("Recruitment Progress Report")){
				System.out.println("Deleting "+report.getTitle());
				client.deleteReport(ds.getId(), report.getId(), saml);
			}
		}

		client.saveReport(Reports.piNorthWestMgmtReport(ds), saml);
		client.saveReport(Reports.recruitmentInNorthWestReport(ds), saml);
		client.saveReport(Reports.piNorthEastCumbriaMgmtReport(ds), saml);
		client.saveReport(Reports.recruitmentInNorthEastAndCumbriaReport(ds), saml);
		client.saveReport(Reports.piNorthWestLondonMgmtReport(ds), saml);
		client.saveReport(Reports.recruitmentInNorthWestLondonReport(ds), saml);
		client.saveReport(Reports.piThamesValleyMgmtReport(ds), saml);
		client.saveReport(Reports.recruitmentInThamesValleyReport(ds), saml);
		client.saveReport(Reports.piSouthEastMidlandsMgmtReport(ds), saml);
		client.saveReport(Reports.recruitmentInSouthEastMidlandsReport(ds), saml);
		client.saveReport(Reports.cpmRecruitmentReport(ds), saml);
		client.saveReport(Reports.cpmMgmtReport(ds), saml);

	}

	/* (non-Javadoc)
	 * @see org.psygrid.outlook.patches.AbstractPatch#getName()
	 */
	@Override
	public String getName() {
		return "Re-configure reports";
	}

}

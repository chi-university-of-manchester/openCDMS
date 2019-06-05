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


package org.psygrid.drn.address.patches.v1_1_29;

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
public class Patch10 extends AbstractPatch {

	/* (non-Javadoc)
	 * @see org.psygrid.outlook.patches.AbstractPatch#applyPatch(org.psygrid.data.model.IDataSet, java.lang.String)
	 */
	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		ReportsClient client = new ReportsClient();

		List<IReport> reports = client.getReportsOfType(ds.getProjectCode(), "management", saml);
		for ( IReport report: reports ){
			System.out.println("Deleting "+report.getTitle());
			client.deleteReport(ds.getId(), report.getId(), saml);
		}

		IReport cpmReport = Reports.cpmMgmtReport(ds);
		IReport ciReport = Reports.ciMgmtReport(ds);
		IReport ukCRNReport= Reports.ukCRNReport(ds);
		IReport ciRecruitment  = Reports.recruitmentReport(ds);
		IReport cpmRecruitment = Reports.cpmRecruitmentReport(ds);
		IReport recordStatusReport = Reports.recordStatusReport(ds, null, "");
		IReport documentStatusReport = Reports.documentStatusReport(ds, null, "");
		IReport collectionDateReport = Reports.collectionDateReport(ds, null, "");
		IReport basicStatsReport = Reports.basicStatisticsReport(ds);
		IReport piNorthEastCumbriaMgmtReport = Reports.piNorthEastCumbriaMgmtReport(ds);
		IReport piNorthWestMgmtReport = Reports.piNorthWestMgmtReport(ds);
		IReport piNorthWestLondonMgmtReport = Reports.piNorthWestLondonMgmtReport(ds);
		IReport piSouthWestMgmtReport = Reports.piSouthWestMgmtReport(ds);
		IReport piThamesValleyMgmtReport = Reports.piThamesValleyMgmtReport(ds);
		IReport piEasternEnglandMgmtReport = Reports.piEasternEnglandMgmtReport(ds);
		IReport piNorthEastLondonMgmtReport = Reports.piNorthEastLondonMgmtReport(ds);
		IReport recruitmentInNorthEastAndCumbriaReport = Reports.recruitmentInNorthEastAndCumbriaReport(ds);
		IReport recruitmentInNorthWestReport = Reports.recruitmentInNorthWestReport(ds);
		IReport recruitmentInNorthWestLondonReport = Reports.recruitmentInNorthWestLondonReport(ds);
		IReport recruitmentInSouthWestReport = Reports.recruitmentInSouthWestReport(ds);
		IReport recruitmentInThamesValleyReport = Reports.recruitmentInThamesValleyReport(ds);
		IReport recruitmentInEasternEnglandReport = Reports.recruitmentInEasternEnglandReport(ds);
		IReport recruitmentInNorthEastLondonReport = Reports.recruitmentInNorthEastLondonReport(ds);

		client.saveReport(cpmReport, saml);
		client.saveReport(ciReport, saml);
		client.saveReport(ukCRNReport, saml);
		client.saveReport(ciRecruitment, saml);
		client.saveReport(cpmRecruitment, saml);
		client.saveReport(recordStatusReport, saml);
		client.saveReport(documentStatusReport, saml);
		client.saveReport(collectionDateReport, saml);
		client.saveReport(basicStatsReport, saml);
		client.saveReport(piNorthEastCumbriaMgmtReport, saml);
		client.saveReport(piNorthWestMgmtReport, saml);
		client.saveReport(piNorthWestLondonMgmtReport, saml);
		client.saveReport(piSouthWestMgmtReport, saml);
		client.saveReport(piThamesValleyMgmtReport, saml);
		client.saveReport(piEasternEnglandMgmtReport, saml);
		client.saveReport(piNorthEastLondonMgmtReport, saml);
		client.saveReport(recruitmentInNorthEastAndCumbriaReport, saml);
		client.saveReport(recruitmentInNorthWestReport, saml);
		client.saveReport(recruitmentInNorthWestLondonReport, saml);
		client.saveReport(recruitmentInSouthWestReport, saml);
		client.saveReport(recruitmentInThamesValleyReport, saml);
		client.saveReport(recruitmentInEasternEnglandReport, saml);
		client.saveReport(recruitmentInNorthEastLondonReport, saml);

	}

	/* (non-Javadoc)
	 * @see org.psygrid.outlook.patches.AbstractPatch#getName()
	 */
	@Override
	public String getName() {
		return "Reloaded Address management reports";
	}

}

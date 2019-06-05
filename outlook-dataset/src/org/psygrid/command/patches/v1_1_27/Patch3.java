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


package org.psygrid.command.patches.v1_1_27;

import java.util.ArrayList;
import java.util.List;

import org.psygrid.command.COMGroups;
import org.psygrid.command.Reports;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.reporting.client.ReportsClient;
import org.psygrid.data.reporting.definition.IManagementReport;
import org.psygrid.data.reporting.definition.IReport;
import org.psygrid.outlook.patches.AbstractPatch;
import org.psygrid.www.xml.security.core.types.GroupType;

/**
 * @author Rob Harper
 *
 */
public class Patch3 extends AbstractPatch {

	@Override
	public String getName() {
		return "Update the Command management reports";
	}

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		ReportsClient client = new ReportsClient();
		List<IReport> reports = client.getAllReportsByDataSet(ds.getId(), saml);

		for (IReport report: reports) {
			if (report instanceof IManagementReport) {
				client.deleteReport(ds.getId(), report.getId(), saml);
			}
		}

		//Management reports
		IReport cpmReport = Reports.cpmMgmtReport(ds);
		IReport ciReport = Reports.ciMgmtReport(ds);
		List<IReport> piReports = new ArrayList<IReport>();
		for ( GroupType gt: COMGroups.allGroups() ){
			piReports.add(Reports.piMgmtReport(ds, gt));
		}
		IReport ukCRNReport= Reports.ukCRNReport(ds);
		IReport ciRecruitment  = Reports.recruitmentReport(ds);
		IReport cpmRecruitment = Reports.cpmRecruitmentReport(ds);
		List<IReport> rcrtReports = new ArrayList<IReport>();
		for ( GroupType gt: COMGroups.allGroups() ){
			rcrtReports.add(Reports.recruitmentInGroupReport(ds, gt));
		}
		IReport ciReceivingTreatment    = Reports.ciReceivingTreatmentReport(ds);
		IReport cpmReceivingTreatment   = Reports.cpmReceivingTreatmentReport(ds);
		IReport recordStatusReport = Reports.recordStatusReport(ds, null, "");
		IReport documentStatusReport = Reports.documentStatusReport(ds, null, "");
		IReport collectionDateReport = Reports.collectionDateReport(ds, null, "");
		IReport stdCodeStatusReport  = Reports.stdCodeStatusReport(ds);
		IReport basicStatsReport = Reports.basicStatisticsReport(ds);

		//save the reports
		client.saveReport(cpmReport, saml);
		client.saveReport(ciReport, saml);
		for ( IReport r: piReports ){
			client.saveReport(r, saml);
		}
		client.saveReport(ukCRNReport, saml);
		client.saveReport(ciRecruitment, saml);
		client.saveReport(cpmRecruitment, saml);
		for ( IReport r: rcrtReports ){
			client.saveReport(r, saml);
		}
		client.saveReport(ciReceivingTreatment, saml);
		client.saveReport(cpmReceivingTreatment, saml);

		client.saveReport(recordStatusReport, saml);
		client.saveReport(documentStatusReport, saml);
		client.saveReport(collectionDateReport, saml);
		client.saveReport(stdCodeStatusReport, saml);
		client.saveReport(basicStatsReport, saml);
	}

}

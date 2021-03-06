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

package org.psygrid.edie.patches.v1_1_24;

import java.util.List;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.reporting.client.ReportsClient;
import org.psygrid.data.reporting.definition.IManagementReport;
import org.psygrid.data.reporting.definition.IReport;
import org.psygrid.outlook.patches.AbstractPatch;
import org.psygrid.security.RBACAction;

public class Patch38 extends AbstractPatch {


	@Override
	public String getName() {
		return "Update the RBACActions for every report to determine who can view it via psygrid-web";
	}

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		ReportsClient client = new ReportsClient();
		List<IReport> reports = client.getAllReportsByDataSet(ds.getId(), saml);

		for (IReport report: reports) {
			if (report instanceof IManagementReport) {

				IManagementReport mr = (IManagementReport)report;
				if ("EDIE 2 - Record Status Report".equals(report.getTitle())) {
					System.out.println("Updating EDIE 2 - Record Status Report");
					mr.setViewAction(RBACAction.ACTION_DR_STATUS_REPORT);
				}
				else if ("EDIE 2 - Document Status Report".equals(report.getTitle())) {
					System.out.println("Updating EDIE 2 - Document Status Report");
					mr.setViewAction(RBACAction.ACTION_DR_STATUS_REPORT);
				}
				else {
					System.out.println("Updating "+mr.getTitle());
					mr.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
				}
				client.saveReport(mr, saml);
			}
		}
	}


}

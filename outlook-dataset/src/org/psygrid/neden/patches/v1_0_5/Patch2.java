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

package org.psygrid.neden.patches.v1_0_5;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.reporting.client.ReportsClient;
import org.psygrid.data.reporting.definition.Factory;
import org.psygrid.data.reporting.definition.IGroupsSummaryChart;
import org.psygrid.data.reporting.definition.IManagementReport;
import org.psygrid.data.reporting.definition.IProjectSummaryChart;
import org.psygrid.data.reporting.definition.hibernate.HibernateFactory;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch2 extends AbstractPatch {

	public String getName() {
		return "Add report for RecruitmentManager";
	}

	public void applyPatch(DataSet ds, String saml) throws Exception {
		Factory factory = new HibernateFactory();

		ReportsClient client = null;
		client = new ReportsClient();

		IManagementReport report = factory.createManagementReport(ds,
				"National EDEN - Recruitment Manager Report");
		//report.setRole("RecruitmentManager");
		report.setWithRawData(true);

		IProjectSummaryChart chrt1 = factory.createProjectSummaryChart(
				org.psygrid.data.reporting.Chart.CHART_PIE, "Whole Project");
		chrt1.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
		report.addChart(chrt1);

		IGroupsSummaryChart chrt2 = factory.createGroupsSummaryChart(
				org.psygrid.data.reporting.Chart.CHART_PIE,
				"Heart of Birmingham - West EIS");
		chrt2.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
		chrt2.addGroup(ds.getGroup(0));
		report.addChart(chrt2);

		IGroupsSummaryChart chrt3 = factory.createGroupsSummaryChart(
				org.psygrid.data.reporting.Chart.CHART_PIE,
				"Heart of Birmingham - East EIS");
		chrt3.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
		chrt3.addGroup(ds.getGroup(1));
		report.addChart(chrt3);

		IGroupsSummaryChart chrt4 = factory.createGroupsSummaryChart(
				org.psygrid.data.reporting.Chart.CHART_PIE,
				"East PCT Birmingham");
		chrt4.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
		chrt4.addGroup(ds.getGroup(2));
		report.addChart(chrt4);

		IGroupsSummaryChart chrt5 = factory.createGroupsSummaryChart(
				org.psygrid.data.reporting.Chart.CHART_PIE, "Lancashire");
		chrt5.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
		chrt5.addGroup(ds.getGroup(3));
		chrt5.addGroup(ds.getGroup(4));
		report.addChart(chrt5);

		IGroupsSummaryChart chrt6 = factory.createGroupsSummaryChart(
				org.psygrid.data.reporting.Chart.CHART_PIE, "Norfolk");
		chrt6.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
		chrt6.addGroup(ds.getGroup(5));
		report.addChart(chrt6);

		IGroupsSummaryChart chrt7 = factory.createGroupsSummaryChart(
				org.psygrid.data.reporting.Chart.CHART_PIE, "Cambridge CAMEO");
		chrt7.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
		chrt7.addGroup(ds.getGroup(6));
		report.addChart(chrt7);

		IGroupsSummaryChart chrt8 = factory.createGroupsSummaryChart(
				org.psygrid.data.reporting.Chart.CHART_PIE, "Cornwall");
		chrt8.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
		chrt8.addGroup(ds.getGroup(7));
		chrt8.addGroup(ds.getGroup(8));
		report.addChart(chrt8);

		IGroupsSummaryChart chrt9 = factory.createGroupsSummaryChart(
				org.psygrid.data.reporting.Chart.CHART_PIE, "Birmingham South");
		chrt9.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
		chrt9.addGroup(ds.getGroup(9));
		report.addChart(chrt9);

		client.saveReport(report, saml);

	}

}

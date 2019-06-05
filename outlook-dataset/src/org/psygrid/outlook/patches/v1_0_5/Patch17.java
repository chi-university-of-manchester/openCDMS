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

package org.psygrid.outlook.patches.v1_0_5;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.reporting.client.ReportsClient;
import org.psygrid.data.reporting.definition.Factory;
import org.psygrid.data.reporting.definition.IGroupsSummaryChart;
import org.psygrid.data.reporting.definition.IManagementReport;
import org.psygrid.data.reporting.definition.IProjectSummaryChart;
import org.psygrid.data.reporting.definition.hibernate.HibernateFactory;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch17 extends AbstractPatch {


    public boolean isReport(){
        return true;
    }

    public String getName() {
        return "Add group 006002";
    }

    public void applyPatch(DataSet ds, String saml) throws Exception {
        Factory factory = new HibernateFactory();

        ReportsClient client = null;
        client = new ReportsClient();

        IManagementReport report = factory.createManagementReport(ds, "Outlook - Principal Investigator (North West Hub) Report");
        //report.setRole("PrincipalInvestigator");
        report.addGroup(ds.getGroup(8));
        report.addGroup(ds.getGroup(12));
        report.setWithRawData(false);

        IProjectSummaryChart chrt1 = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Whole Project");
        report.addChart(chrt1);

        IGroupsSummaryChart chrt2 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "North West Hub");
        chrt2.addGroup(ds.getGroup(8));
        chrt2.addGroup(ds.getGroup(12));
        report.addChart(chrt2);

        client.saveReport(report, saml);

        IManagementReport mreport = factory.createManagementReport(ds, "Outlook - Clinical Project Manager Report");
        //mreport.setRole("ClinicalResearchManager");
        mreport.setWithRawData(true);

        IProjectSummaryChart chrt1a = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Whole Project");
        chrt1a.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
        mreport.addChart(chrt1a);

        IGroupsSummaryChart chrt2a = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Bristol Avon Hub");
        chrt2a.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
        chrt2a.addGroup(ds.getGroup(0));
        mreport.addChart(chrt2a);

        IGroupsSummaryChart chrt3 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "East Anglia Hub");
        chrt3.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
        chrt3.addGroup(ds.getGroup(1));
        chrt3.addGroup(ds.getGroup(2));
        chrt3.addGroup(ds.getGroup(11));
        mreport.addChart(chrt3);

        IGroupsSummaryChart chrt4 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "East Midlands Hub");
        chrt4.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
        chrt4.addGroup(ds.getGroup(3));
        chrt4.addGroup(ds.getGroup(4));
        mreport.addChart(chrt4);

        IGroupsSummaryChart chrt5 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "North East Hub");
        chrt5.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
        chrt5.addGroup(ds.getGroup(5));
        mreport.addChart(chrt5);

        IGroupsSummaryChart chrt6 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "North London Hub");
        chrt6.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
        chrt6.addGroup(ds.getGroup(6));
        chrt6.addGroup(ds.getGroup(7));
        mreport.addChart(chrt6);

        IGroupsSummaryChart chrt7 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "North West Hub");
        chrt7.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
        chrt7.addGroup(ds.getGroup(8));
        chrt7.addGroup(ds.getGroup(12));
        mreport.addChart(chrt7);

        IGroupsSummaryChart chrt8 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "South London Hub");
        chrt8.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
        chrt8.addGroup(ds.getGroup(9));
        mreport.addChart(chrt8);

        IGroupsSummaryChart chrt9 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "West Midlands Hub");
        chrt9.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
        chrt9.addGroup(ds.getGroup(10));
        mreport.addChart(chrt9);

        client.saveReport(mreport, saml);

    }

}

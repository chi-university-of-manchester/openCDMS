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

package org.psygrid.edie;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.data.reporting.Chart;
import org.psygrid.data.reporting.definition.Factory;
import org.psygrid.data.reporting.definition.IBasicStatisticsChart;
import org.psygrid.data.reporting.definition.ICollectionDateChart;
import org.psygrid.data.reporting.definition.IDocumentStatusChart;
import org.psygrid.data.reporting.definition.IGroupsSummaryChart;
import org.psygrid.data.reporting.definition.IManagementReport;
import org.psygrid.data.reporting.definition.IProjectSummaryChart;
import org.psygrid.data.reporting.definition.IReceivingTreatmentChart;
import org.psygrid.data.reporting.definition.IRecordStatusChart;
import org.psygrid.data.reporting.definition.IRecruitmentProgressChart;
import org.psygrid.data.reporting.definition.IReport;
import org.psygrid.data.reporting.definition.IStdCodeStatusChart;
import org.psygrid.data.reporting.definition.IUKCRNSummaryChart;
import org.psygrid.data.reporting.definition.ReportException;
import org.psygrid.data.reporting.definition.ReportFrequency;
import org.psygrid.data.reporting.definition.hibernate.HibernateFactory;
import org.psygrid.security.RBACAction;

public class Reports {

    public static Factory factory = new HibernateFactory();



    public static IReport cpmMgmtReport(DataSet ds) throws ReportException {

        IManagementReport report = factory.createManagementReport(ds, ds.getName()+" - Recruitment Manager Report");
        report.setEmailAction(RBACAction.ACTION_DR_RECRUITMENT_REPORT);
        report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
        report.setWithRawData(true);
        report.setTemplate(false);

        IProjectSummaryChart total = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_TABLE, "Total Clients");
		total.setShowTotal(true);
		report.addChart(total);
        IProjectSummaryChart chrt1 = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Whole Project");
        chrt1.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
        report.addChart(chrt1);

        IGroupsSummaryChart chrt2 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Manchester");
        chrt2.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
        chrt2.addGroup(ds.getGroup(0));
        report.addChart(chrt2);

        IGroupsSummaryChart chrt3 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Birmingham");
        chrt3.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
        chrt3.addGroup(ds.getGroup(1));
        report.addChart(chrt3);

        IGroupsSummaryChart chrt4 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Cambridge");
        chrt4.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
        chrt4.addGroup(ds.getGroup(2));
        report.addChart(chrt4);

        IGroupsSummaryChart chrt5 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "East Anglia");
        chrt5.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
        chrt5.addGroup(ds.getGroup(3));
        report.addChart(chrt5);

        IGroupsSummaryChart chrt6 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Glasgow");
        chrt6.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
        chrt6.addGroup(ds.getGroup(4));
        report.addChart(chrt6);

        return report;
    }

    public static IReport ciMgmtReport(DataSet ds) throws ReportException {

        IManagementReport report = factory.createManagementReport(ds, ds.getName()+" - Chief Investigator Report");
        report.setEmailAction(RBACAction.ACTION_DR_CHIEF_INVESTIGATOR_REPORT);
        report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
        report.setWithRawData(false);

        IProjectSummaryChart total = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_TABLE, "Total Clients");
		total.setShowTotal(true);
		report.addChart(total);

		IProjectSummaryChart chrt1 = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Whole Project");
		report.addChart(chrt1);
		return report;
    }

    public static IReport piManchesterMgmtReport(DataSet ds) throws ReportException {

        IManagementReport report = factory.createManagementReport(ds, ds.getName()+" - Principal Investigator (Manchester) Report");
        report.setEmailAction(RBACAction.ACTION_DR_INVESTIGATOR_REPORT);
        report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
        report.addGroup(ds.getGroup(0));
        report.setWithRawData(false);
        report.setTemplate(false);

        IProjectSummaryChart total = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_TABLE, "Total Clients");
		total.setShowTotal(true);
		report.addChart(total);
        IProjectSummaryChart chrt1 = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Whole Project");
        report.addChart(chrt1);

        IGroupsSummaryChart chrt2 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Manchester");
        chrt2.addGroup(ds.getGroup(0));
        report.addChart(chrt2);

        return report;
    }

    public static IReport piBirminghamMgmtReport(DataSet ds) throws ReportException {

        IManagementReport report = factory.createManagementReport(ds, ds.getName()+" - Principal Investigator (Birmingham) Report");
        report.setEmailAction(RBACAction.ACTION_DR_INVESTIGATOR_REPORT);
        report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
        report.addGroup(ds.getGroup(1));
        report.setWithRawData(false);
        report.setTemplate(false);

        IProjectSummaryChart total = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_TABLE, "Total Clients");
		total.setShowTotal(true);
		report.addChart(total);
        IProjectSummaryChart chrt1 = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Whole Project");
        report.addChart(chrt1);

        IGroupsSummaryChart chrt2 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Birmingham");
        chrt2.addGroup(ds.getGroup(1));
        report.addChart(chrt2);

        return report;
    }

    public static IReport piCambridgeMgmtReport(DataSet ds) throws ReportException {

        IManagementReport report = factory.createManagementReport(ds, ds.getName()+" - Principal Investigator (Cambridge) Report");
        report.setEmailAction(RBACAction.ACTION_DR_INVESTIGATOR_REPORT);
        report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
        report.addGroup(ds.getGroup(2));
        report.setWithRawData(false);
        report.setTemplate(false);

        IProjectSummaryChart total = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_TABLE, "Total Clients");
		total.setShowTotal(true);
		report.addChart(total);
        IProjectSummaryChart chrt1 = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Whole Project");
        report.addChart(chrt1);

        IGroupsSummaryChart chrt2 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Cambridge");
        chrt2.addGroup(ds.getGroup(2));
        report.addChart(chrt2);

        return report;
    }

    public static IReport piEastAngliaMgmtReport(DataSet ds) throws ReportException {

        IManagementReport report = factory.createManagementReport(ds, ds.getName()+" - Principal Investigator (East Anglia) Report");
        report.setEmailAction(RBACAction.ACTION_DR_INVESTIGATOR_REPORT);
        report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
        report.addGroup(ds.getGroup(3));
        report.setWithRawData(false);
        report.setTemplate(false);

        IProjectSummaryChart total = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_TABLE, "Total Clients");
		total.setShowTotal(true);
		report.addChart(total);
        IProjectSummaryChart chrt1 = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Whole Project");
        report.addChart(chrt1);

        IGroupsSummaryChart chrt2 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "East Anglia");
        chrt2.addGroup(ds.getGroup(3));
        report.addChart(chrt2);


        return report;
    }

    public static IReport piGlasgowMgmtReport(DataSet ds) throws ReportException {

        IManagementReport report = factory.createManagementReport(ds, ds.getName()+" - Principal Investigator (Glasgow) Report");
        report.setEmailAction(RBACAction.ACTION_DR_INVESTIGATOR_REPORT);
        report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
        report.addGroup(ds.getGroup(4));
        report.setWithRawData(false);
        report.setTemplate(false);

        IProjectSummaryChart total = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_TABLE, "Total Clients");
		total.setShowTotal(true);
		report.addChart(total);
        IProjectSummaryChart chrt1 = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Whole Project");
        report.addChart(chrt1);

        IGroupsSummaryChart chrt2 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Glasgow");
        chrt2.addGroup(ds.getGroup(4));
        report.addChart(chrt2);


        return report;
    }

	public static IReport ciRecruitmentReport(DataSet ds) throws ReportException {
		IManagementReport report = recruitmentReport(ds, null, "Overview", "Chief Investigator");
		report.setEmailAction(RBACAction.ACTION_DR_CHIEF_INVESTIGATOR_REPORT);
		report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
		report.setTemplate(true);

		IRecruitmentProgressChart chart = (IRecruitmentProgressChart)((IManagementReport)report).getChart(0);
		chart.setTargets(new LinkedHashMap<Calendar, Integer>());

		/*
		 * Create the total recruitment targets for the whole Edie2 project
		 */
		chart.addTarget(new GregorianCalendar(2006, 10, 0), 0);		//Nov 2006
		chart.addTarget(new GregorianCalendar(2006, 11, 0), 5);
		chart.addTarget(new GregorianCalendar(2007, 0, 0), 8);
		chart.addTarget(new GregorianCalendar(2007, 1, 0), 13);
		chart.addTarget(new GregorianCalendar(2007, 2, 0), 16);
		chart.addTarget(new GregorianCalendar(2007, 3, 0), 24);
		chart.addTarget(new GregorianCalendar(2007, 4, 0), 32);
		chart.addTarget(new GregorianCalendar(2007, 5, 0), 40);
		chart.addTarget(new GregorianCalendar(2007, 6, 0), 48);
		chart.addTarget(new GregorianCalendar(2007, 7, 0), 56);
		chart.addTarget(new GregorianCalendar(2007, 8, 0), 69);
		chart.addTarget(new GregorianCalendar(2007, 9, 0), 80);
		chart.addTarget(new GregorianCalendar(2007, 10, 0), 93);
		chart.addTarget(new GregorianCalendar(2007, 11, 0), 104);
		chart.addTarget(new GregorianCalendar(2008, 0, 0), 117);
		chart.addTarget(new GregorianCalendar(2008, 1, 0), 128);
		chart.addTarget(new GregorianCalendar(2008, 2, 0), 141);
		chart.addTarget(new GregorianCalendar(2008, 3, 0), 152);
		chart.addTarget(new GregorianCalendar(2008, 4, 0), 165);
		chart.addTarget(new GregorianCalendar(2008, 5, 0), 176);
		chart.addTarget(new GregorianCalendar(2008, 6, 0), 189);
		chart.addTarget(new GregorianCalendar(2008, 7, 0), 200);
		chart.addTarget(new GregorianCalendar(2008, 8, 0), 213);
		chart.addTarget(new GregorianCalendar(2008, 9, 0), 224);
		chart.addTarget(new GregorianCalendar(2008, 10, 0), 237);
		chart.addTarget(new GregorianCalendar(2008, 11, 0), 248);
		chart.addTarget(new GregorianCalendar(2009, 0, 0), 261);
		chart.addTarget(new GregorianCalendar(2009, 1, 0), 272);
		chart.addTarget(new GregorianCalendar(2009, 2, 0), 285);
		chart.addTarget(new GregorianCalendar(2009, 3, 0), 296);
		chart.addTarget(new GregorianCalendar(2009, 4, 0), 309);
		chart.addTarget(new GregorianCalendar(2009, 5, 0), 320);

		return report;
	}

	public static IReport cpmRecruitmentReport(DataSet ds) throws ReportException {

		IManagementReport report = factory.createManagementReport(ds, ds.getName()+" - Recruitment Progress Report");
		report.setEmailAction(RBACAction.ACTION_DR_CRM_REPORT);
		report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
		report.setWithRawData(false);
		report.setTemplate(false);

		//add charts from the reports for each of the hubs
		report.addManagementChart(((IManagementReport)recruitmentInCambridgeReport(ds)).getChart(0));
		report.addManagementChart(((IManagementReport)recruitmentInManchesterReport(ds)).getChart(0));
		report.addManagementChart(((IManagementReport)recruitmentInBirminghamReport(ds)).getChart(0));
		report.addManagementChart(((IManagementReport)recruitmentInCambridgeReport(ds)).getChart(0));
		report.addManagementChart(((IManagementReport)recruitmentInEastAngliaReport(ds)).getChart(0));
		report.addManagementChart(((IManagementReport)recruitmentInGlasgowReport(ds)).getChart(0));
		return report;

	}

	public static IReport recruitmentReport(DataSet ds) throws ReportException {
		IManagementReport report = recruitmentReport(ds, null, "Overview", "Chief Investigator");
		report.setEmailAction(RBACAction.ACTION_DR_CHIEF_INVESTIGATOR_REPORT);
		report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
		return report;
	}

	public static IReport recruitmentInEastMidlandsReport(DataSet ds) throws ReportException {
		return recruitmentInBirminghamReport(ds);
	}

	public static IReport recruitmentInManchesterReport(DataSet ds) throws ReportException {
      	List<Group> groups = new ArrayList<Group>();
		groups.add(ds.getGroup(0));

		IReport report = recruitmentReport(ds, groups, "Manchester Hub");
		report.setTemplate(false);

		IRecruitmentProgressChart chart = (IRecruitmentProgressChart)((IManagementReport)report).getChart(0);
		chart.setTargets(new LinkedHashMap<Calendar, Integer>());

		chart.addTarget(new GregorianCalendar(2006, 10, 0), 0);		//Nov 2006
		chart.addTarget(new GregorianCalendar(2006, 11, 0), 1);
		chart.addTarget(new GregorianCalendar(2007, 0, 0), 2);
		chart.addTarget(new GregorianCalendar(2007, 1, 0), 3);
		chart.addTarget(new GregorianCalendar(2007, 2, 0), 4);
		chart.addTarget(new GregorianCalendar(2007, 3, 0), 6);
		chart.addTarget(new GregorianCalendar(2007, 4, 0), 8);
		chart.addTarget(new GregorianCalendar(2007, 5, 0), 10);
		chart.addTarget(new GregorianCalendar(2007, 6, 0), 12);
		chart.addTarget(new GregorianCalendar(2007, 7, 0), 14);
		chart.addTarget(new GregorianCalendar(2007, 8, 0), 17);
		chart.addTarget(new GregorianCalendar(2007, 9, 0), 20);
		chart.addTarget(new GregorianCalendar(2007, 10, 0), 23);
		chart.addTarget(new GregorianCalendar(2007, 11, 0), 26);
		chart.addTarget(new GregorianCalendar(2008, 0, 0), 29);
		chart.addTarget(new GregorianCalendar(2008, 1, 0), 32);
		chart.addTarget(new GregorianCalendar(2008, 2, 0), 35);
		chart.addTarget(new GregorianCalendar(2008, 3, 0), 38);
		chart.addTarget(new GregorianCalendar(2008, 4, 0), 41);
		chart.addTarget(new GregorianCalendar(2008, 5, 0), 44);
		chart.addTarget(new GregorianCalendar(2008, 6, 0), 47);
		chart.addTarget(new GregorianCalendar(2008, 7, 0), 50);
		chart.addTarget(new GregorianCalendar(2008, 8, 0), 53);
		chart.addTarget(new GregorianCalendar(2008, 9, 0), 56);
		chart.addTarget(new GregorianCalendar(2008, 10, 0), 59);
		chart.addTarget(new GregorianCalendar(2008, 11, 0), 62);
		chart.addTarget(new GregorianCalendar(2009, 0, 0), 65);
		chart.addTarget(new GregorianCalendar(2009, 1, 0), 68);
		chart.addTarget(new GregorianCalendar(2009, 2, 0), 71);
		chart.addTarget(new GregorianCalendar(2009, 3, 0), 74);
		chart.addTarget(new GregorianCalendar(2009, 4, 0), 77);
		chart.addTarget(new GregorianCalendar(2009, 5, 0), 80);

		return report;
    }

    public static IReport recruitmentInBirminghamReport(DataSet ds) throws ReportException {
      	List<Group> groups = new ArrayList<Group>();
		groups.add(ds.getGroup(1));

		IReport report = recruitmentReport(ds, groups, "Birmingham Hub");
		report.setTemplate(false);

		IRecruitmentProgressChart chart = (IRecruitmentProgressChart)((IManagementReport)report).getChart(0);
		chart.setTargets(new LinkedHashMap<Calendar, Integer>());

		chart.addTarget(new GregorianCalendar(2006, 10, 0), 0);		//Nov 2006
		chart.addTarget(new GregorianCalendar(2006, 11, 0), 1);
		chart.addTarget(new GregorianCalendar(2007, 0, 0), 2);
		chart.addTarget(new GregorianCalendar(2007, 1, 0), 3);
		chart.addTarget(new GregorianCalendar(2007, 2, 0), 4);
		chart.addTarget(new GregorianCalendar(2007, 3, 0), 6);
		chart.addTarget(new GregorianCalendar(2007, 4, 0), 8);
		chart.addTarget(new GregorianCalendar(2007, 5, 0), 10);
		chart.addTarget(new GregorianCalendar(2007, 6, 0), 12);
		chart.addTarget(new GregorianCalendar(2007, 7, 0), 14);
		chart.addTarget(new GregorianCalendar(2007, 8, 0), 17);
		chart.addTarget(new GregorianCalendar(2007, 9, 0), 20);
		chart.addTarget(new GregorianCalendar(2007, 10, 0), 23);
		chart.addTarget(new GregorianCalendar(2007, 11, 0), 26);
		chart.addTarget(new GregorianCalendar(2008, 0, 0), 29);
		chart.addTarget(new GregorianCalendar(2008, 1, 0), 32);
		chart.addTarget(new GregorianCalendar(2008, 2, 0), 35);
		chart.addTarget(new GregorianCalendar(2008, 3, 0), 38);
		chart.addTarget(new GregorianCalendar(2008, 4, 0), 41);
		chart.addTarget(new GregorianCalendar(2008, 5, 0), 44);
		chart.addTarget(new GregorianCalendar(2008, 6, 0), 47);
		chart.addTarget(new GregorianCalendar(2008, 7, 0), 50);
		chart.addTarget(new GregorianCalendar(2008, 8, 0), 53);
		chart.addTarget(new GregorianCalendar(2008, 9, 0), 56);
		chart.addTarget(new GregorianCalendar(2008, 10, 0), 59);
		chart.addTarget(new GregorianCalendar(2008, 11, 0), 62);
		chart.addTarget(new GregorianCalendar(2009, 0, 0), 65);
		chart.addTarget(new GregorianCalendar(2009, 1, 0), 68);
		chart.addTarget(new GregorianCalendar(2009, 2, 0), 71);
		chart.addTarget(new GregorianCalendar(2009, 3, 0), 74);
		chart.addTarget(new GregorianCalendar(2009, 4, 0), 77);
		chart.addTarget(new GregorianCalendar(2009, 5, 0), 80);

		return report;
    }

    public static IReport recruitmentInCambridgeReport(DataSet ds) throws ReportException {
      	List<Group> groups = new ArrayList<Group>();
		groups.add(ds.getGroup(2));

		IReport report = recruitmentReport(ds, groups, "Cambridge Hub");
		report.setTemplate(false);

		IRecruitmentProgressChart chart = (IRecruitmentProgressChart)((IManagementReport)report).getChart(0);
		chart.setTargets(new LinkedHashMap<Calendar, Integer>());

		chart.addTarget(new GregorianCalendar(2006, 10, 0), 0);		//Nov 2006
		chart.addTarget(new GregorianCalendar(2006, 11, 0), 1);
		chart.addTarget(new GregorianCalendar(2007, 0, 0), 1);
		chart.addTarget(new GregorianCalendar(2007, 1, 0), 2);
		chart.addTarget(new GregorianCalendar(2007, 2, 0), 2);
		chart.addTarget(new GregorianCalendar(2007, 3, 0), 3);
		chart.addTarget(new GregorianCalendar(2007, 4, 0), 4);
		chart.addTarget(new GregorianCalendar(2007, 5, 0), 5);
		chart.addTarget(new GregorianCalendar(2007, 6, 0), 6);
		chart.addTarget(new GregorianCalendar(2007, 7, 0), 7);
		chart.addTarget(new GregorianCalendar(2007, 8, 0), 9);
		chart.addTarget(new GregorianCalendar(2007, 9, 0), 10);
		chart.addTarget(new GregorianCalendar(2007, 10, 0), 12);
		chart.addTarget(new GregorianCalendar(2007, 11, 0), 13);
		chart.addTarget(new GregorianCalendar(2008, 0, 0), 15);
		chart.addTarget(new GregorianCalendar(2008, 1, 0), 16);
		chart.addTarget(new GregorianCalendar(2008, 2, 0), 18);
		chart.addTarget(new GregorianCalendar(2008, 3, 0), 19);
		chart.addTarget(new GregorianCalendar(2008, 4, 0), 21);
		chart.addTarget(new GregorianCalendar(2008, 5, 0), 22);
		chart.addTarget(new GregorianCalendar(2008, 6, 0), 24);
		chart.addTarget(new GregorianCalendar(2008, 7, 0), 25);
		chart.addTarget(new GregorianCalendar(2008, 8, 0), 27);
		chart.addTarget(new GregorianCalendar(2008, 9, 0), 28);
		chart.addTarget(new GregorianCalendar(2008, 10, 0), 30);
		chart.addTarget(new GregorianCalendar(2008, 11, 0), 31);
		chart.addTarget(new GregorianCalendar(2009, 0, 0), 33);
		chart.addTarget(new GregorianCalendar(2009, 1, 0), 34);
		chart.addTarget(new GregorianCalendar(2009, 2, 0), 36);
		chart.addTarget(new GregorianCalendar(2009, 3, 0), 37);
		chart.addTarget(new GregorianCalendar(2009, 4, 0), 39);
		chart.addTarget(new GregorianCalendar(2009, 5, 0), 40);

		return report;
    }

    public static IReport recruitmentInEastAngliaReport(DataSet ds) throws ReportException {
    	List<Group> groups = new ArrayList<Group>();
		groups.add(ds.getGroup(3));

		IReport report = recruitmentReport(ds, groups, "East Anglia Hub");
		report.setTemplate(false);

		IRecruitmentProgressChart chart = (IRecruitmentProgressChart)((IManagementReport)report).getChart(0);
		chart.setTargets(new LinkedHashMap<Calendar, Integer>());

		chart.addTarget(new GregorianCalendar(2006, 10, 0), 0);		//Nov 2006
		chart.addTarget(new GregorianCalendar(2006, 11, 0), 1);
		chart.addTarget(new GregorianCalendar(2007, 0, 0), 1);
		chart.addTarget(new GregorianCalendar(2007, 1, 0), 2);
		chart.addTarget(new GregorianCalendar(2007, 2, 0), 2);
		chart.addTarget(new GregorianCalendar(2007, 3, 0), 3);
		chart.addTarget(new GregorianCalendar(2007, 4, 0), 4);
		chart.addTarget(new GregorianCalendar(2007, 5, 0), 5);
		chart.addTarget(new GregorianCalendar(2007, 6, 0), 6);
		chart.addTarget(new GregorianCalendar(2007, 7, 0), 7);
		chart.addTarget(new GregorianCalendar(2007, 8, 0), 9);
		chart.addTarget(new GregorianCalendar(2007, 9, 0), 10);
		chart.addTarget(new GregorianCalendar(2007, 10, 0), 12);
		chart.addTarget(new GregorianCalendar(2007, 11, 0), 13);
		chart.addTarget(new GregorianCalendar(2008, 0, 0), 15);
		chart.addTarget(new GregorianCalendar(2008, 1, 0), 16);
		chart.addTarget(new GregorianCalendar(2008, 2, 0), 18);
		chart.addTarget(new GregorianCalendar(2008, 3, 0), 19);
		chart.addTarget(new GregorianCalendar(2008, 4, 0), 21);
		chart.addTarget(new GregorianCalendar(2008, 5, 0), 22);
		chart.addTarget(new GregorianCalendar(2008, 6, 0), 24);
		chart.addTarget(new GregorianCalendar(2008, 7, 0), 25);
		chart.addTarget(new GregorianCalendar(2008, 8, 0), 27);
		chart.addTarget(new GregorianCalendar(2008, 9, 0), 28);
		chart.addTarget(new GregorianCalendar(2008, 10, 0), 30);
		chart.addTarget(new GregorianCalendar(2008, 11, 0), 31);
		chart.addTarget(new GregorianCalendar(2009, 0, 0), 33);
		chart.addTarget(new GregorianCalendar(2009, 1, 0), 34);
		chart.addTarget(new GregorianCalendar(2009, 2, 0), 36);
		chart.addTarget(new GregorianCalendar(2009, 3, 0), 37);
		chart.addTarget(new GregorianCalendar(2009, 4, 0), 39);
		chart.addTarget(new GregorianCalendar(2009, 5, 0), 40);

		return report;
    }

    public static IReport recruitmentInGlasgowReport(DataSet ds) throws ReportException {
		List<Group> groups = new ArrayList<Group>();
		groups.add(ds.getGroup(4));

		IReport report = recruitmentReport(ds, groups, "Glasgow Hub");
		report.setTemplate(false);

		IRecruitmentProgressChart chart = (IRecruitmentProgressChart)((IManagementReport)report).getChart(0);
		chart.setTargets(new LinkedHashMap<Calendar, Integer>());

		chart.addTarget(new GregorianCalendar(2006, 10, 0), 0);		//Nov 2006
		chart.addTarget(new GregorianCalendar(2006, 11, 0), 1);
		chart.addTarget(new GregorianCalendar(2007, 0, 0), 2);
		chart.addTarget(new GregorianCalendar(2007, 1, 0), 3);
		chart.addTarget(new GregorianCalendar(2007, 2, 0), 4);
		chart.addTarget(new GregorianCalendar(2007, 3, 0), 6);
		chart.addTarget(new GregorianCalendar(2007, 4, 0), 8);
		chart.addTarget(new GregorianCalendar(2007, 5, 0), 10);
		chart.addTarget(new GregorianCalendar(2007, 6, 0), 12);
		chart.addTarget(new GregorianCalendar(2007, 7, 0), 14);
		chart.addTarget(new GregorianCalendar(2007, 8, 0), 17);
		chart.addTarget(new GregorianCalendar(2007, 9, 0), 20);
		chart.addTarget(new GregorianCalendar(2007, 10, 0), 23);
		chart.addTarget(new GregorianCalendar(2007, 11, 0), 26);
		chart.addTarget(new GregorianCalendar(2008, 0, 0), 29);
		chart.addTarget(new GregorianCalendar(2008, 1, 0), 32);
		chart.addTarget(new GregorianCalendar(2008, 2, 0), 35);
		chart.addTarget(new GregorianCalendar(2008, 3, 0), 38);
		chart.addTarget(new GregorianCalendar(2008, 4, 0), 41);
		chart.addTarget(new GregorianCalendar(2008, 5, 0), 44);
		chart.addTarget(new GregorianCalendar(2008, 6, 0), 47);
		chart.addTarget(new GregorianCalendar(2008, 7, 0), 50);
		chart.addTarget(new GregorianCalendar(2008, 8, 0), 53);
		chart.addTarget(new GregorianCalendar(2008, 9, 0), 56);
		chart.addTarget(new GregorianCalendar(2008, 10, 0), 59);
		chart.addTarget(new GregorianCalendar(2008, 11, 0), 62);
		chart.addTarget(new GregorianCalendar(2009, 0, 0), 65);
		chart.addTarget(new GregorianCalendar(2009, 1, 0), 68);
		chart.addTarget(new GregorianCalendar(2009, 2, 0), 71);
		chart.addTarget(new GregorianCalendar(2009, 3, 0), 74);
		chart.addTarget(new GregorianCalendar(2009, 4, 0), 77);
		chart.addTarget(new GregorianCalendar(2009, 5, 0), 80);

		return report;
    }

	/**
	 * Create a management report comparing the number of new subjects consented
	 * into the trial against targets set for each month, giving a view of the
	 * trial's progress.
	 *
	 * @param ds the dataset
	 * @param groups to create the report for
	 * @param hub name of the NHS hub the report is for
	 * @return report
	 * @throws ReportException
	 */
	private static IReport recruitmentReport(DataSet ds, List<Group> groups, String hub) throws ReportException {

		IManagementReport report = recruitmentReport(ds, groups, hub, "PrincipalInvestigator");
		report.setEmailAction(RBACAction.ACTION_DR_INVESTIGATOR_REPORT);
		report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
		return report;
	}

	private static IManagementReport recruitmentReport(DataSet ds, List<Group> groups, String hub, String role) throws ReportException {

		IManagementReport report = factory.createManagementReport(ds, ds.getName()+" - Recruitment Progress Report");
		report.setWithRawData(true);
		report.setFrequency(ReportFrequency.MONTHLY);

		/*
		 * Create a timeseries chart showing the number of subjects consented
		 * into the trial against the targets set for each month, giving
		 * a view of the trial's progress.
		 */
		IRecruitmentProgressChart chart = factory.createRecruitmentProgressChart(Chart.CHART_TIME_SERIES, ds.getName()+" - Recruitment Progress ("+hub+")");

		//Set this automatically when the chart is generated (will show previous 6 months by default)
		chart.setTimePeriod(null, null);

		chart.setRangeAxisLabel("Number of Clients");	//y-axis label

		//check
		if (groups != null) {
			for (Group g: groups) {
				chart.addGroup(g);
			}
		}
		report.addManagementChart(chart);

		return report;
	}

	public static IReport ukCRNReport(DataSet ds) throws ReportException {

		IManagementReport report = factory.createManagementReport(ds, ds.getName()+" - UKCRN Report");
		report.setEmailAction(RBACAction.ACTION_DR_UKCRN_REPORT);
		report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
		report.setWithRawData(true);
		report.setFrequency(ReportFrequency.MONTHLY);
		report.setShowHeader(false);

		IUKCRNSummaryChart chart = factory.createUKCRNSummaryChart(org.psygrid.data.reporting.Chart.CHART_TABLE, ds.getName());
		report.addChart(chart);

		//This will be set automatically when the report is generated to create
		//a for the current financial year (assuming may-april)
		chart.setTimePeriod(null, null);

		return report;
	}

	public static IReport ciReceivingTreatmentReport(DataSet ds) throws ReportException {
		IManagementReport report = receivingTreatmentReport(ds, null, "Whole Project", "Chief Investigator");
		report.setEmailAction(RBACAction.ACTION_DR_CHIEF_INVESTIGATOR_REPORT);
		report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
		report.setTemplate(true);
		return report;
	}

	public static IReport cpmReceivingTreatmentReport(DataSet ds) throws ReportException {

		IManagementReport report = factory.createManagementReport(ds, ds.getName()+" - Receiving Treatment Report (grouped)");
		report.setEmailAction(RBACAction.ACTION_DR_CRM_REPORT);
		report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
		report.setWithRawData(true);
		report.setTemplate(true);
		report.setFrequency(ReportFrequency.NEVER);

		IManagementReport man = ((IManagementReport)receivingTreatmentInManchesterReport(ds));
		IManagementReport em  = ((IManagementReport)receivingTreatmentInEastMidlandsReport(ds));
		IManagementReport cam = ((IManagementReport)receivingTreatmentInCambridgeReport(ds));
		IManagementReport ea  = ((IManagementReport)receivingTreatmentInEastAngliaReport(ds));
		IManagementReport gl  = ((IManagementReport)receivingTreatmentInGlasgowReport(ds));
		//add charts from the reports for each of the hubs
		for (int i=0; i < man.numCharts(); i++) {
			report.addManagementChart(man.getChart(i));
		}
		for (int i=0; i < em.numCharts(); i++) {
			report.addManagementChart(em.getChart(i));
		}
		for (int i=0; i < cam.numCharts(); i++) {
			report.addManagementChart(cam.getChart(i));
		}
		for (int i=0; i < ea.numCharts(); i++) {
			report.addManagementChart(ea.getChart(i));
		}
		for (int i=0; i < gl.numCharts(); i++) {
			report.addManagementChart(gl.getChart(i));
		}

		return report;
	}

	public static IReport receivingTreatmentInCambridgeReport(DataSet ds) throws ReportException {
		List<Group> groups = new ArrayList<Group>();
		groups.add(ds.getGroup(2));

		IReport report = receivingTreatmentReport(ds, groups, "Cambridge Hub");
		report.setTemplate(false);
		return report;
	}

	public static IReport receivingTreatmentInManchesterReport(DataSet ds) throws ReportException {
		List<Group> groups = new ArrayList<Group>();
		groups.add(ds.getGroup(0));
		IReport report = receivingTreatmentReport(ds, groups, "Manchester Hub");
		report.setTemplate(false);
		return report;
	}

	public static IReport receivingTreatmentInEastMidlandsReport(DataSet ds) throws ReportException {
		List<Group> groups = new ArrayList<Group>();
		groups.add(ds.getGroup(1));

		IReport report = receivingTreatmentReport(ds, groups, "East Midlands Hub");
		report.setTemplate(false);
		return report;
	}

	public static IReport receivingTreatmentInEastAngliaReport(DataSet ds) throws ReportException {
		List<Group> groups = new ArrayList<Group>();
		groups.add(ds.getGroup(3));

		IReport report = receivingTreatmentReport(ds, groups, "East Anglia Hub");
		report.setTemplate(false);
		return report;
	}

	public static IReport receivingTreatmentInGlasgowReport(DataSet ds) throws ReportException {
		List<Group> groups = new ArrayList<Group>();
		groups.add(ds.getGroup(4));

		IReport report = receivingTreatmentReport(ds, groups, "Glasgow Hub");
		report.setTemplate(false);
		return report;
	}

	/**
	 * Create a management report showing the list of subjects in a project
	 * grouped by randomisation treatment type.
	 *
	 * @param ds the dataset
	 * @param groups to create the report for
	 * @param hub name of the NHS hub the report is for
	 * @return report
	 * @throws ReportException
	 */
	private static IReport receivingTreatmentReport(DataSet ds, List<Group> groups, String hub) throws ReportException {
		IManagementReport report = receivingTreatmentReport(ds, groups, hub, "PrincipalInvestigator");
		report.setEmailAction(RBACAction.ACTION_DR_INVESTIGATOR_REPORT);
		report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
		return report;
	}

	private static IManagementReport receivingTreatmentReport(DataSet ds, List<Group> groups, String hub, String role) throws ReportException {

		IManagementReport report = factory.createManagementReport(ds, ds.getName()+" - Receiving Treatment Report");
		//report.setRole(role);
		report.setWithRawData(true);
		report.setFrequency(ReportFrequency.NEVER);

		/*
		 * Create a chart showing a list of subjects for each treatment assigned
		 * by the randomisation service.
		 */
		IReceivingTreatmentChart chart = factory.createReceivingTreatmentChart(Chart.CHART_TABLE, hub);

		//The time period will be set through psygridweb, if not a chart will
		//be generated for the previous six months based on current date.
		chart.setTimePeriod(null, null);

		//check
		if (groups != null) {
			for (Group g: groups) {
				chart.addGroup(g);
			}
		}
		report.addManagementChart(chart);

		return report;
	}

	/**
	 * Create a record completion status report showing the status of each study point
	 * in a dataset for all records in the selected groups.
	 *
	 * Gives an overview of the status of each record and therefore of a trial's progress.
	 *
	 * @param ds the dataset
	 * @param groups to create the report for
	 * @param hub name of the NHS hub the report is for
	 * @return report
	 * @throws ReportException
	 */
	public static IReport recordStatusReport(DataSet ds, List<Group> groups, String hub) throws ReportException {
		IManagementReport report = recordStatusReport(ds, groups, hub, "PrincipalInvestigator");
		report.setEmailAction(RBACAction.ACTION_DR_CRM_REPORT);
		report.setViewAction(RBACAction.ACTION_DR_STATUS_REPORT);
		return report;
	}

	private static IManagementReport recordStatusReport(DataSet ds, List<Group> groups, String hub, String role) throws ReportException {

		IManagementReport report = factory.createManagementReport(ds, ds.getName()+" - Record Status Report");
		//report.setRole(role);
		report.setWithRawData(true);
		report.setTemplate(true);
		report.setFrequency(ReportFrequency.NEVER);

		/*
		 * Create a tabular chart showing the records in the groups provided and their current status
		 */
		IRecordStatusChart chart = factory.createRecordStatusChart(Chart.CHART_TABLE, "Record "+hub);

		chart.setRangeAxisLabel("");	//y-axis label

		//check
		if (groups != null) {
			for (Group g: groups) {
				chart.addGroup(g);
			}
		}
		report.addManagementChart(chart);

		return report;
	}

	/**
	 * Create a document completion status report showing the status of each document instance
	 * in a dataset for all records in the selected groups.
	 *
	 * Gives an overview of the status of each record and therefore of a trial's progress.
	 *
	 * @param ds the dataset
	 * @param groups to create the report for
	 * @param hub name of the NHS hub the report is for
	 * @return report
	 * @throws ReportException
	 */
	public static IReport documentStatusReport(DataSet ds, List<Group> groups, String hub) throws ReportException {
		IManagementReport report = documentStatusReport(ds, groups, hub, "PrincipalInvestigator");
		report.setEmailAction(RBACAction.ACTION_DR_CRM_REPORT);
		report.setViewAction(RBACAction.ACTION_DR_STATUS_REPORT);
		return report;
	}

	private static IManagementReport documentStatusReport(DataSet ds, List<Group> groups, String hub, String role) throws ReportException {

		IManagementReport report = factory.createManagementReport(ds, ds.getName()+" - Document Status Report");
		//report.setRole(role);
		report.setWithRawData(true);
		report.setTemplate(true);
		report.setFrequency(ReportFrequency.NEVER);

		/*
		 * Create a tabular chart showing the records in the groups provided and their current status
		 */
		IDocumentStatusChart chart = factory.createDocumentStatusChart(Chart.CHART_TABLE, "Document "+hub);

		chart.setRangeAxisLabel("");	//y-axis label

		//check
		if (groups != null) {
			for (Group g: groups) {
				chart.addGroup(g);
			}
		}
		report.addManagementChart(chart);

		return report;
	}

	/**
	 * Create a document collection date report showing the date of data collection for each
	 * document instance in a dataset for all records in the selected groups.
	 *
	 * @param ds the dataset
	 * @param groups to create the report for
	 * @param hub name of the NHS hub the report is for
	 * @return report
	 * @throws ReportException
	 */
	public static IReport collectionDateReport(DataSet ds, List<Group> groups, String hub) throws ReportException {

		IManagementReport report = factory.createManagementReport(ds, ds.getName()+" - Document Collection Date Report");
		report.setEmailAction(RBACAction.ACTION_DR_INVESTIGATOR_REPORT);
		report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
		report.setWithRawData(true);
		report.setTemplate(true);
		report.setFrequency(ReportFrequency.NEVER);

		/*
		 * Create a tabular chart showing the records in the groups provided and their current status
		 */
		ICollectionDateChart chart = factory.createCollectionDateChart(Chart.CHART_TABLE, "Documents "+hub);
		chart.setRangeAxisLabel("");	//y-axis label

		//check
		if (groups != null) {
			for (Group g: groups) {
				chart.addGroup(g);
			}
		}

		report.addManagementChart(chart);

		return report;
	}

	public static IReport stdCodeStatusReport(DataSet ds) throws ReportException {

		IManagementReport report = factory.createManagementReport(ds, "Edie 2 - Standard Codes Usage Report");
		report.setEmailAction(RBACAction.ACTION_DR_INVESTIGATOR_REPORT);
		report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
		report.setWithRawData(true);
		report.setTemplate(true);
		report.setFrequency(ReportFrequency.NEVER);

		/*
		 * Create a chart showing percentage occurrence of std codes for entries in the documents specified for the records in the
		 * groups provided
		 */
		IStdCodeStatusChart pansschart2 = factory.createStdCodeStatusChart(Chart.CHART_BAR,
				"Document Percentage Usage");
		pansschart2.setUsePercentages(true);
		pansschart2.setPerDocument(true);
		pansschart2.setRangeAxisLabel("");	//y-axis label

		report.addManagementChart(pansschart2);


		/*
		 * Create a chart showing percentage occurrence of std codes for entries in the documents specified for the records in the
		 * groups provided
		 */
		IStdCodeStatusChart pansschart = factory.createStdCodeStatusChart(Chart.CHART_TABLE,
				"Question Percentage Usage");
		pansschart.setUsePercentages(true);
		pansschart.setPerEntry(true);
		pansschart.setRangeAxisLabel("");	//y-axis label

		report.addManagementChart(pansschart);


		/*
		 * Create a chart showing usage of std codes for entries in the documents specified for the records in the
		 * groups provided
		 */
		IStdCodeStatusChart pansschart1 = factory.createStdCodeStatusChart(Chart.CHART_TABLE,
				"Usage Per Patient");
		pansschart1.setUsePercentages(false);
		pansschart1.setRangeAxisLabel("");	//y-axis label

		report.addManagementChart(pansschart1);



		return report;
	}

	public static IReport basicStatisticsReport(DataSet ds) throws ReportException{

		IManagementReport report = factory.createManagementReport(ds, ds.getName()+" - Basic Statistics Report");
		report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
		report.setWithRawData(false);
		report.setTemplate(true);
		report.setFrequency(ReportFrequency.NEVER);

		/*
		 * Create a chart showing percentage occurrence of std codes for entries in the documents specified for the records in the
		 * groups provided
		 */
		IBasicStatisticsChart basicStatsChart = factory.createBasicStatisticsChart(Chart.CHART_TABLE,
				"Statistics");
		basicStatsChart.setUsePercentages(false);
		basicStatsChart.setRangeAxisLabel("");	//y-axis label

		report.addChart(basicStatsChart);

		return report;
	}


}

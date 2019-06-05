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

package org.psygrid.matisse;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.data.reporting.Chart;
import org.psygrid.data.reporting.definition.Factory;
import org.psygrid.data.reporting.definition.IBasicStatisticsChart;
import org.psygrid.data.reporting.definition.IManagementReport;
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

	public static IReport ciRecruitmentReport(DataSet ds) throws ReportException {
		IManagementReport report = recruitmentReport(ds, null, "Overview", "Chief Investigator");
		report.setEmailAction(RBACAction.ACTION_DR_CHIEF_INVESTIGATOR_REPORT);
		report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
		report.setTemplate(true);
		return report;
	}

	public static IReport recruitmentInLondonReport(DataSet ds) throws ReportException {
		List<Group> groups = new ArrayList<Group>();
		groups.add(ds.getGroup(0));

		IReport report = recruitmentReport(ds, groups, "Central & North West London Hub");
		report.setTemplate(false);
		return report;
	}

	public static IReport recruitmentInCamdenReport(DataSet ds) throws ReportException {
		List<Group> groups = new ArrayList<Group>();
		groups.add(ds.getGroup(1));

		IReport report = recruitmentReport(ds, groups, "Camden & Islington Hub");
		report.setTemplate(false);
		return report;
	}

	public static IReport recruitmentInWestEnglandReport(DataSet ds) throws ReportException {

		List<Group> groups = new ArrayList<Group>();
		groups.add(ds.getGroup(2));

		IReport report = recruitmentReport(ds, groups, "West England Hub");
		report.setTemplate(false);
		return report;
	}

	public static IReport recruitmentInNorthernIrelandReport(DataSet ds) throws ReportException {
		List<Group> groups = new ArrayList<Group>();
		groups.add(ds.getGroup(3));
		IReport report = recruitmentReport(ds, groups, "Northern Ireland Hub");
		report.setTemplate(false);
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

		IManagementReport report = factory.createManagementReport(ds, "Matisse - Recruitment Progress Report");
		//report.setRole(role);
		report.setWithRawData(true);
		report.setFrequency(ReportFrequency.MONTHLY);

		/*
		 * Create a timeseries chart showing the number of subjects consented
		 * into the trial against the targets set for each month, giving
		 * a view of the trial's progress.
		 */
		IRecruitmentProgressChart chart = factory.createRecruitmentProgressChart(Chart.CHART_TIME_SERIES, "Matisse - Recruitment Progress ("+hub+")");

		chart.setRangeAxisLabel("Number of Clients");	//y-axis label


		//generate default targets for the previous six months, based on current date
		Calendar curDate = Calendar.getInstance();
		Calendar startDate = new GregorianCalendar(curDate.get(Calendar.YEAR), curDate.get(Calendar.MONTH) - 6, 0);
		Calendar endDate   = new GregorianCalendar(curDate.get(Calendar.YEAR), curDate.get(Calendar.MONTH), 0);
		startDate.setTimeZone(TimeZone.getTimeZone("GMT"));
		endDate.setTimeZone(TimeZone.getTimeZone("GMT"));

		int target = 10;		//expecting to recruit 10 people per month
		for (int i = 0; i < 6; i++) {
			int month = startDate.get(Calendar.MONTH) + i;		//Calendar class will automatically take into account changes in the year
			int year  = startDate.get(Calendar.YEAR);

			Calendar cal = new GregorianCalendar(year, month+1, 0);	//add one to month to compensate for Calendar's month starting at 0
			cal.setTimeZone(TimeZone.getTimeZone("GMT"));
			chart.addTarget(cal, target);
		}

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

		IManagementReport report = factory.createManagementReport(ds, "Matisse - UKCRN Report");
		report.setEmailAction(RBACAction.ACTION_DR_UKCRN_REPORT);
		report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
		report.setWithRawData(true);
		report.setFrequency(ReportFrequency.MONTHLY);
		report.setShowHeader(false);

		IUKCRNSummaryChart chart = factory.createUKCRNSummaryChart(org.psygrid.data.reporting.Chart.CHART_TABLE, "Matisse");
		report.addChart(chart);

		return report;
	}

	public static IReport stdCodeStatusReport(DataSet ds) throws ReportException {

		IManagementReport report = factory.createManagementReport(ds, "Matisse - Standard Codes Usage Report");
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
		//report.setAction(RBACAction.ACTION_DR_INVESTIGATOR_REPORT);
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

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

package org.psygrid.command;

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
import org.psygrid.www.xml.security.core.types.GroupType;

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

        for ( GroupType gt: COMGroups.allGroups() ){
            IGroupsSummaryChart chrt = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, gt.getName());
            chrt.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
            Group thisGroup = null;
            for ( int i=0, c=ds.numGroups(); i<c; i++ ){
            	Group g = ds.getGroup(i);
            	if ( g.getName().equals(gt.getIdCode()) ){
            		thisGroup = g;
            		chrt.addGroup(thisGroup);
            		break;
            	}
            }
            if ( null == thisGroup ){
            	throw new RuntimeException("No group in the dataset with code = "+gt.getIdCode());
            }
            report.addChart(chrt);
        }

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

    public static IReport piMgmtReport(DataSet ds, GroupType gt) throws ReportException {

    	//find the group
        Group thisGroup = null;
        for ( int i=0, c=ds.numGroups(); i<c; i++ ){
        	Group g = ds.getGroup(i);
        	if ( g.getName().equals(gt.getIdCode()) ){
        		thisGroup = g;
        		break;
        	}
        }
        if ( null == thisGroup ){
        	throw new RuntimeException("No group in the dataset with code = "+gt.getIdCode());
        }

        IManagementReport report = factory.createManagementReport(ds, ds.getName()+" - Principal Investigator ("+gt.getName()+") Report");
        report.setEmailAction(RBACAction.ACTION_DR_INVESTIGATOR_REPORT);
        report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
        report.addGroup(thisGroup);
        report.setWithRawData(false);
        report.setTemplate(false);

        IProjectSummaryChart total = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_TABLE, "Total Clients");
		total.setShowTotal(true);
		report.addChart(total);
        IProjectSummaryChart chrt1 = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Whole Project");
        report.addChart(chrt1);

        IGroupsSummaryChart chrt2 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, gt.getName());
        chrt2.addGroup(thisGroup);
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

		//TODO targets for Command
		chart.addTarget(new GregorianCalendar(2008, 1, 0), 0);
		chart.addTarget(new GregorianCalendar(2008, 2, 0), 0);
		chart.addTarget(new GregorianCalendar(2008, 3, 0), 0);
		chart.addTarget(new GregorianCalendar(2008, 4, 0), 0);
		chart.addTarget(new GregorianCalendar(2008, 5, 0), 0);
		chart.addTarget(new GregorianCalendar(2008, 6, 0), 0);
		chart.addTarget(new GregorianCalendar(2008, 7, 0), 0);
		chart.addTarget(new GregorianCalendar(2008, 8, 0), 0);
		chart.addTarget(new GregorianCalendar(2008, 9, 0), 0);
		chart.addTarget(new GregorianCalendar(2008, 10, 0), 0);
		chart.addTarget(new GregorianCalendar(2008, 11, 0), 0);
		chart.addTarget(new GregorianCalendar(2009, 0, 0), 0);
		chart.addTarget(new GregorianCalendar(2009, 1, 0), 0);
		chart.addTarget(new GregorianCalendar(2009, 2, 0), 0);
		chart.addTarget(new GregorianCalendar(2009, 3, 0), 0);
		chart.addTarget(new GregorianCalendar(2009, 4, 0), 0);
		chart.addTarget(new GregorianCalendar(2009, 5, 0), 0);
		chart.addTarget(new GregorianCalendar(2009, 6, 0), 0);
		chart.addTarget(new GregorianCalendar(2009, 7, 0), 0);
		chart.addTarget(new GregorianCalendar(2009, 8, 0), 0);
		chart.addTarget(new GregorianCalendar(2009, 9, 0), 0);
		chart.addTarget(new GregorianCalendar(2009, 10, 0), 0);
		chart.addTarget(new GregorianCalendar(2009, 11, 0), 0);
		chart.addTarget(new GregorianCalendar(2010, 0, 0), 0);
		chart.addTarget(new GregorianCalendar(2010, 1, 0), 0);
		chart.addTarget(new GregorianCalendar(2010, 2, 0), 0);
		chart.addTarget(new GregorianCalendar(2010, 3, 0), 0);
		chart.addTarget(new GregorianCalendar(2010, 4, 0), 0);
		chart.addTarget(new GregorianCalendar(2010, 5, 0), 0);
		chart.addTarget(new GregorianCalendar(2010, 6, 0), 0);
		chart.addTarget(new GregorianCalendar(2010, 7, 0), 0);
		chart.addTarget(new GregorianCalendar(2010, 8, 0), 0);
		chart.addTarget(new GregorianCalendar(2010, 9, 0), 0);
		chart.addTarget(new GregorianCalendar(2010, 10, 0), 0);
		chart.addTarget(new GregorianCalendar(2010, 11, 0), 0);

		return report;
	}

	public static IReport cpmRecruitmentReport(DataSet ds) throws ReportException {

		IManagementReport report = factory.createManagementReport(ds, ds.getName()+" - Recruitment Progress Report");
		report.setEmailAction(RBACAction.ACTION_DR_CRM_REPORT);
		report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
		report.setWithRawData(false);
		report.setTemplate(false);

		//add charts from the reports for each of the hubs
		for ( GroupType gt: COMGroups.allGroups() ){
			report.addManagementChart(((IManagementReport)recruitmentInGroupReport(ds, gt)).getChart(0));
		}
		return report;

	}

	public static IReport recruitmentReport(DataSet ds) throws ReportException {
		IManagementReport report = recruitmentReport(ds, null, "Overview", "Chief Investigator");
		report.setEmailAction(RBACAction.ACTION_DR_CHIEF_INVESTIGATOR_REPORT);
		report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
		return report;
	}

	public static IReport recruitmentInGroupReport(DataSet ds, GroupType gt) throws ReportException {
      	List<Group> groups = new ArrayList<Group>();
        Group thisGroup = null;
        for ( int i=0, c=ds.numGroups(); i<c; i++ ){
        	Group g = ds.getGroup(i);
        	if ( g.getName().equals(gt.getIdCode()) ){
        		thisGroup = g;
        		break;
        	}
        }
        if ( null == thisGroup ){
        	throw new RuntimeException("No group in the dataset with code = "+gt.getIdCode());
        }
		groups.add(thisGroup);

		IReport report = recruitmentReport(ds, groups, gt.getName()+" Hub");
		report.setTemplate(false);

		IRecruitmentProgressChart chart = (IRecruitmentProgressChart)((IManagementReport)report).getChart(0);
		chart.setTargets(new LinkedHashMap<Calendar, Integer>());

		addTargetsForGroup(chart, gt);

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

		for ( GroupType gt: COMGroups.allGroups() ){
			IManagementReport man = ((IManagementReport)receivingTreatmentReportForGroup(ds, gt));
			for (int i=0; i < man.numCharts(); i++) {
				report.addManagementChart(man.getChart(i));
			}
		}

		return report;
	}

	public static IReport receivingTreatmentReportForGroup(DataSet ds, GroupType gt) throws ReportException {
		List<Group> groups = new ArrayList<Group>();

        Group thisGroup = null;
        for ( int i=0, c=ds.numGroups(); i<c; i++ ){
        	Group g = ds.getGroup(i);
        	if ( g.getName().equals(gt.getIdCode()) ){
        		thisGroup = g;
        		break;
        	}
        }
        if ( null == thisGroup ){
        	throw new RuntimeException("No group in the dataset with code = "+gt.getIdCode());
        }
        groups.add(thisGroup);

		IReport report = receivingTreatmentReport(ds, groups, gt.getName()+" Hub");
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

	private static void addTargetsForGroup(IRecruitmentProgressChart chart, GroupType gt){
		//TODO targets
		if ( "001001".equals(gt.getIdCode())){
			chart.addTarget(new GregorianCalendar(2008, 1, 0), 0);
			chart.addTarget(new GregorianCalendar(2008, 2, 0), 0);
			chart.addTarget(new GregorianCalendar(2008, 3, 0), 0);
			chart.addTarget(new GregorianCalendar(2008, 4, 0), 0);
			chart.addTarget(new GregorianCalendar(2008, 5, 0), 0);
			chart.addTarget(new GregorianCalendar(2008, 6, 0), 0);
			chart.addTarget(new GregorianCalendar(2008, 7, 0), 0);
			chart.addTarget(new GregorianCalendar(2008, 8, 0), 0);
			chart.addTarget(new GregorianCalendar(2008, 9, 0), 0);
			chart.addTarget(new GregorianCalendar(2008, 10, 0), 0);
			chart.addTarget(new GregorianCalendar(2008, 11, 0), 0);
			chart.addTarget(new GregorianCalendar(2009, 0, 0), 0);
			chart.addTarget(new GregorianCalendar(2009, 1, 0), 0);
			chart.addTarget(new GregorianCalendar(2009, 2, 0), 0);
			chart.addTarget(new GregorianCalendar(2009, 3, 0), 0);
			chart.addTarget(new GregorianCalendar(2009, 4, 0), 0);
			chart.addTarget(new GregorianCalendar(2009, 5, 0), 0);
			chart.addTarget(new GregorianCalendar(2009, 6, 0), 0);
			chart.addTarget(new GregorianCalendar(2009, 7, 0), 0);
			chart.addTarget(new GregorianCalendar(2009, 8, 0), 0);
			chart.addTarget(new GregorianCalendar(2009, 9, 0), 0);
			chart.addTarget(new GregorianCalendar(2009, 10, 0), 0);
			chart.addTarget(new GregorianCalendar(2009, 11, 0), 0);
			chart.addTarget(new GregorianCalendar(2010, 0, 0), 0);
			chart.addTarget(new GregorianCalendar(2010, 1, 0), 0);
			chart.addTarget(new GregorianCalendar(2010, 2, 0), 0);
			chart.addTarget(new GregorianCalendar(2010, 3, 0), 0);
			chart.addTarget(new GregorianCalendar(2010, 4, 0), 0);
			chart.addTarget(new GregorianCalendar(2010, 5, 0), 0);
			chart.addTarget(new GregorianCalendar(2010, 6, 0), 0);
			chart.addTarget(new GregorianCalendar(2010, 7, 0), 0);
			chart.addTarget(new GregorianCalendar(2010, 8, 0), 0);
			chart.addTarget(new GregorianCalendar(2010, 9, 0), 0);
			chart.addTarget(new GregorianCalendar(2010, 10, 0), 0);
			chart.addTarget(new GregorianCalendar(2010, 11, 0), 0);
		}
	}

}

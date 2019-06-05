package org.psygrid.champ.patches;

import java.util.ArrayList;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.reporting.Chart;
import org.psygrid.data.reporting.client.ReportsClient;
import org.psygrid.data.reporting.definition.IBasicStatisticsChart;
import org.psygrid.data.reporting.definition.ICollectionDateChart;
import org.psygrid.data.reporting.definition.IDocumentStatusChart;
import org.psygrid.data.reporting.definition.IGroupsSummaryChart;
import org.psygrid.data.reporting.definition.IManagementReport;
import org.psygrid.data.reporting.definition.IProjectSummaryChart;
import org.psygrid.data.reporting.definition.IReceivingTreatmentChart;
import org.psygrid.data.reporting.definition.IRecordStatusChart;
import org.psygrid.data.reporting.definition.IRecruitmentProgressChart;
import org.psygrid.data.reporting.definition.IStdCodeStatusChart;
import org.psygrid.data.reporting.definition.IUKCRNSummaryChart;
import org.psygrid.data.reporting.definition.ReportFrequency;
import org.psygrid.outlook.patches.AbstractPatch;
import org.psygrid.security.RBACAction;

public class Patch2 extends AbstractPatch {

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		ReportsClient client = new ReportsClient();
		org.psygrid.data.reporting.definition.hibernate.HibernateFactory factory = new org.psygrid.data.reporting.definition.hibernate.HibernateFactory();
		ArrayList<IManagementReport> mgmtReports = new ArrayList<IManagementReport>();
		
		//Add record status report.
		IManagementReport recordStatusReport = factory.createManagementReport(ds, ds.getName() + " - Record Status Report");
		recordStatusReport.setDataSet(ds);
		recordStatusReport.setEmailAction(RBACAction.ACTION_DR_CRM_REPORT);
		recordStatusReport.setViewAction(RBACAction.ACTION_DR_STATUS_REPORT);
		recordStatusReport.setWithRawData(true);
		recordStatusReport.setTemplate(true);
		recordStatusReport.setFrequency(ReportFrequency.NEVER);
		/* 
		 * Create a tabular chart showing the records in the groups provided and their current status
		 */
		IRecordStatusChart recordStatusChart = factory.createRecordStatusChart(Chart.CHART_TABLE, "Records ");
		recordStatusChart.setRangeAxisLabel("");	//y-axis label
		recordStatusReport.addManagementChart(recordStatusChart);
		mgmtReports.add(recordStatusReport);
		//End Add record status report.
		
		//Add Document Status Report.
		IManagementReport docStatusReport = factory.createManagementReport(ds, ds.getName() + " - Document Status Report");
		docStatusReport.setDataSet(ds);
		docStatusReport.setEmailAction(RBACAction.ACTION_DR_CRM_REPORT);
		docStatusReport.setViewAction(RBACAction.ACTION_DR_STATUS_REPORT);
		docStatusReport.setWithRawData(true);
		docStatusReport.setTemplate(true);
		docStatusReport.setFrequency(ReportFrequency.NEVER);

		/* 
		 * Create a tabular chart showing the records in the groups provided and their current status
		 */
		IDocumentStatusChart docStatusChart = factory.createDocumentStatusChart(Chart.CHART_TABLE, "Documents ");
		//chart.setRangeAxisLabel("");	//y-axis label

		//check
		for (int j=0; j<ds.numGroups(); j++) {
			docStatusChart.addGroup(ds.getGroup(j));
		}
		docStatusReport.addManagementChart(docStatusChart);
		mgmtReports.add(docStatusReport);
		//End add Document Status Report.
		
		//Add Recruitment Progress Report.
		IManagementReport recruitmentProgressReport = factory.createManagementReport(ds, ds.getName() + " - Recruitment Progress Report");
		recruitmentProgressReport.setDataSet(ds);
		recruitmentProgressReport.setEmailAction(RBACAction.ACTION_DR_INVESTIGATOR_REPORT);
		recruitmentProgressReport.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
		//recruitmentProgressReport.setRole(role);
		recruitmentProgressReport.setWithRawData(true);
		recruitmentProgressReport.setFrequency(ReportFrequency.MONTHLY);

		/* 
		 * Create a timeseries chart showing the number of subjects consented
		 * into the trial against the targets set for each month, giving
		 * a view of the trial's progress. 
		 */
		IRecruitmentProgressChart recruitmentProgressChart = factory.createRecruitmentProgressChart(Chart.CHART_TIME_SERIES, "Recruitment Progress");

		//Set this automatically when the chart is generated (will show previous 6 months by default)
		recruitmentProgressChart.setTimePeriod(null, null);

		recruitmentProgressChart.setRangeAxisLabel("Number of Clients");	//y-axis label

		//check
		for (int j=0; j<ds.numGroups(); j++) {
			recruitmentProgressChart.addGroup(ds.getGroup(j));
		}

		recruitmentProgressReport.addManagementChart(recruitmentProgressChart);
		mgmtReports.add(recruitmentProgressReport);
		//End Add Recruitment Progress Report.
		
		//Add UKCRN Report
		IManagementReport ukcrnReport = factory.createManagementReport(ds, ds.getName() + " - UKCRN Report");
		ukcrnReport.setDataSet(ds);
		ukcrnReport.setEmailAction(RBACAction.ACTION_DR_UKCRN_REPORT);
		ukcrnReport.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
		ukcrnReport.setWithRawData(true);
		ukcrnReport.setFrequency(ReportFrequency.MONTHLY);
		ukcrnReport.setShowHeader(false);

		IUKCRNSummaryChart ukcrnChart = factory.createUKCRNSummaryChart(org.psygrid.data.reporting.Chart.CHART_TABLE, ds.getName());
		ukcrnReport.addChart(ukcrnChart);

		//This will be set automatically when the ukcrnReport is generated to create
		//a for the current financial year (assuming may-april)
		ukcrnChart.setTimePeriod(null, null);
		mgmtReports.add(ukcrnReport);
		//End Add UKCRN Report
		
		//Add Standard Code Status Report
		IManagementReport stdCodeReport = factory.createManagementReport(ds, ds.getName() + " - Standard Code Status Report");
		stdCodeReport.setEmailAction(RBACAction.ACTION_DR_INVESTIGATOR_REPORT);
		stdCodeReport.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
		stdCodeReport.setWithRawData(true);
		stdCodeReport.setTemplate(true);
		stdCodeReport.setFrequency(ReportFrequency.NEVER);

		/* 
		 * Create a chart showing percentage occurrence of std codes for entries in the documents specified for the records in the 
		 * groups provided
		 */	
		IStdCodeStatusChart pansschart2 = factory.createStdCodeStatusChart(Chart.CHART_BAR, 
		"Document Percentage Usage");
		pansschart2.setUsePercentages(true);
		pansschart2.setPerDocument(true);
		pansschart2.setRangeAxisLabel("");	//y-axis label

		stdCodeReport.addManagementChart(pansschart2);

		/* 
		 * Create a chart showing percentage occurrence of std codes for entries in the documents specified for the records in the 
		 * groups provided
		 */	
		IStdCodeStatusChart pansschart = factory.createStdCodeStatusChart(Chart.CHART_TABLE, 
		"Question Percentage Usage");
		pansschart.setUsePercentages(true);
		pansschart.setPerEntry(true);
		pansschart.setRangeAxisLabel("");	//y-axis label

		stdCodeReport.addManagementChart(pansschart);

		/* 
		 * Create a chart showing usage of std codes for entries in the documents specified for the records in the 
		 * groups provided
		 */	
		IStdCodeStatusChart pansschart1 = factory.createStdCodeStatusChart(Chart.CHART_TABLE, 
		"Usage Per Patient");
		pansschart1.setUsePercentages(false);
		pansschart1.setRangeAxisLabel("");	//y-axis label

		stdCodeReport.addManagementChart(pansschart1);
		mgmtReports.add(stdCodeReport);
		//End Add Standard Code Status Report
		
		//Add Basic Statistics Report.
		IManagementReport basicStatsReport = factory.createManagementReport(ds, ds.getName() + " - Basic Statistic Report");
		basicStatsReport.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
		basicStatsReport.setWithRawData(false);
		basicStatsReport.setTemplate(true);
		basicStatsReport.setFrequency(ReportFrequency.NEVER);

		/* 
		 * Create a chart showing percentage occurrence of std codes for entries in the documents specified for the records in the 
		 * groups provided
		 */	
		IBasicStatisticsChart basicStatsChart = factory.createBasicStatisticsChart(Chart.CHART_TABLE, 
		"Statistics");
		basicStatsChart.setUsePercentages(false);
		basicStatsChart.setRangeAxisLabel("");	//y-axis label

		basicStatsReport.addChart(basicStatsChart);
		mgmtReports.add(basicStatsReport);
		//End Add Basic Statistics Report.
		
		//Add Collection Date Report
		IManagementReport collectionDateReport = factory.createManagementReport(ds, ds.getName() + " - Collection Date Report");
		collectionDateReport.setEmailAction(RBACAction.ACTION_DR_INVESTIGATOR_REPORT);
		collectionDateReport.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
		collectionDateReport.setWithRawData(true);
		collectionDateReport.setTemplate(true);
		collectionDateReport.setFrequency(ReportFrequency.NEVER);

		/* 
		 * Create a tabular chart showing the records in the groups provided and their current status
		 */
		ICollectionDateChart collectionDateChart = factory.createCollectionDateChart(Chart.CHART_TABLE, "Documents ");
		collectionDateChart.setRangeAxisLabel("");	//y-axis label

		//check
		for (int j=0; j<ds.numGroups(); j++) {
			collectionDateChart.addGroup(ds.getGroup(j));
		}

		collectionDateReport.addManagementChart(collectionDateChart);
		mgmtReports.add(collectionDateReport);
		// no email action set here
		//End Add Collection Date Report
		
		//Add Project Summary Report
		IManagementReport projectSummaryReport = factory.createManagementReport(ds, ds.getName() + " - Project Summary Report");
		projectSummaryReport.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
		projectSummaryReport.setWithRawData(false);

		IProjectSummaryChart total = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_TABLE, "Total Clients");
		total.setShowTotal(true);
		projectSummaryReport.addChart(total);
		IProjectSummaryChart chrt1 = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Whole Project");
		projectSummaryReport.addChart(chrt1);

		for (int z=0; z<ds.numGroups(); z++) {
			IGroupsSummaryChart chrt = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, 
					ds.getDataSet().getGroup(z).getLongName());
			chrt.addGroup(ds.getGroup(z));
			projectSummaryReport.addChart(chrt);
		}

		mgmtReports.add(projectSummaryReport);
		//End Add Project Summary Report
		
		//Add Group Summary Report
		IManagementReport groupSummaryReport = factory.createManagementReport(ds, ds.getName() + " - Group Summary Report");
		groupSummaryReport.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
		groupSummaryReport.setWithRawData(true);
//				groupSummaryReport.setTemplate(false);

		for (int z=0; z<ds.numGroups(); z++) {
			IGroupsSummaryChart chrt = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, 
					ds.getDataSet().getGroup(z).getLongName());
			chrt.addGroup(ds.getGroup(z));
			groupSummaryReport.addChart(chrt);
		}

		mgmtReports.add(groupSummaryReport);
		//End Add Group Summary Report
		
		//Add Receiving Treatment Report.
		IManagementReport receivingTreatmentReport = factory.createManagementReport(ds, ds.getDataSet().getName()+" - Receiving Treatment Report");
		receivingTreatmentReport.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
		//receivingTreatmentReport.setRole(role);
		receivingTreatmentReport.setWithRawData(true);
		receivingTreatmentReport.setFrequency(ReportFrequency.NEVER);

		/* 
		 * Create a chart showing a list of subjects for each treatment assigned
		 * by the randomisation service. 
		 */
		IReceivingTreatmentChart receivingTreatmentChart = factory.createReceivingTreatmentChart(Chart.CHART_TABLE, ds.getName());

		//The time period will be set through psygridweb, if not a chart will 
		//be generated for the previous six months based on current date.
		receivingTreatmentChart.setTimePeriod(null, null);

		for (int w=0; w<ds.numGroups(); w++) {
			receivingTreatmentChart.addGroup(ds.getGroup(w));
		}

		receivingTreatmentReport.addManagementChart(receivingTreatmentChart);
		mgmtReports.add(receivingTreatmentReport);
		//End Add Receiving Treatment Report.
		
		for (int y=0; y<mgmtReports.size(); y++) {
			client.saveReport(mgmtReports.get(y), saml);
		}
		
	}

	@Override
	public String getName() {
		return "Adds all standard reports to the dataset - as per Create.";
	}

}

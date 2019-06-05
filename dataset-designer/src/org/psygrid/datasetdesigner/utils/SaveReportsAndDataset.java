package org.psygrid.datasetdesigner.utils;


import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.axis.encoding.Base64;
import org.apache.axis.utils.Options;
import org.opensaml.SAMLAssertion;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Persistent;
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
import org.psygrid.data.repository.client.RepositoryClient;
import org.psygrid.datasetdesigner.model.StudyDataSet;
import org.psygrid.security.RBACAction;
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.psygrid.security.authentication.client.LoginClient;
import org.psygrid.security.authentication.service.LoginServicePortType;
import org.psygrid.security.components.net.PsyGridClientSocketFactory;
import org.psygrid.security.utils.PropertyUtilities;

public class SaveReportsAndDataset {

	private static LoginServicePortType aa1 = null;
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		System.setProperty("axis.socketSecureFactory",
		"org.psygrid.security.components.net.PsyGridClientSocketFactory");
		Options opts = new Options(args);
		String[] remainingArgs = opts.getRemainingArgs();
		Properties properties = PropertyUtilities.getProperties("test.properties");
		System.out.println(properties.getProperty("org.psygrid.security.authentication.client.trustStoreLocation"));
		LoginClient tc = null;
		
		try {
			tc = new LoginClient("test.properties");
			aa1 = tc.getPort();
		} catch (Exception e) {
			e.printStackTrace();
		}
		char[] password = opts.getPassword().toCharArray();
		short[] pwd = new short[password.length];
		for (int i = 0; i < pwd.length; i++) {
			pwd[i] = (short) password[i];
		}
		String credential = tc.getPort().login(opts.getUser(), pwd);
		if (credential != null) {
			byte[] ks = Base64.decode(credential);
			FileOutputStream fos = new FileOutputStream(properties
					.getProperty("org.psygrid.security.authentication.client.keyStoreLocation"));
			fos.write(ks);
			fos.flush(); 
			fos.close();
		}
		System.out.println("loggedin");
		System.setProperty("javax.net.ssl.keyStorePassword", new String(password));
		PsyGridClientSocketFactory.reinit();
		AAQueryClient qc = new AAQueryClient("test.properties");
		System.out.println("getAssertion");
		SAMLAssertion sa = qc.getSAMLAssertion();


		String dsFile = remainingArgs[0];
		PersistenceManager.getInstance().setAliases();
		
		
		Object obj1 = PersistenceManager.getInstance().load(dsFile);
		
		StudyDataSet dSet = (StudyDataSet)obj1;
		
		SaveReportsAndDataset.saveToRepository(dSet, sa);
		
		SaveReportsAndDataset.saveReports(dSet, sa);
    	
	}
	
	private static void saveReports(StudyDataSet ds, SAMLAssertion sa) throws Exception{

		ReportsClient client = new ReportsClient();
		org.psygrid.data.reporting.definition.hibernate.HibernateFactory factory = new org.psygrid.data.reporting.definition.hibernate.HibernateFactory();
		ArrayList<IManagementReport> mgmtReports = new ArrayList<IManagementReport>();

		if (ds.getReports() == null) {
			throw new Exception("No reports configured");
		}

		for (int i=0; i<ds.getReports().size(); i++) {	
			String reportName = ds.getReports().get(i);

			if (reportName.equals("Record Status Report")) {
				IManagementReport report = factory.createManagementReport(ds.getDs(), ds.getDs().getName() + " - Record Status Report");
				report.setDataSet(ds.getDs());
				report.setEmailAction(RBACAction.ACTION_DR_CRM_REPORT);
				report.setViewAction(RBACAction.ACTION_DR_STATUS_REPORT);
				report.setWithRawData(true);
				report.setTemplate(true);
				report.setFrequency(ReportFrequency.NEVER);
				/* 
				 * Create a tabular chart showing the records in the groups provided and their current status
				 */
				IRecordStatusChart chart = factory.createRecordStatusChart(Chart.CHART_TABLE, "Records ");
				chart.setRangeAxisLabel("");	//y-axis label
				report.addManagementChart(chart);

				mgmtReports.add(report);
			} else if (reportName.equals("Document Status Report")) {
				IManagementReport report = factory.createManagementReport(ds.getDs(), ds.getDs().getName() + " - Document Status Report");
				report.setDataSet(ds.getDs());
				report.setEmailAction(RBACAction.ACTION_DR_CRM_REPORT);
				report.setViewAction(RBACAction.ACTION_DR_STATUS_REPORT);
				report.setWithRawData(true);
				report.setTemplate(true);
				report.setFrequency(ReportFrequency.NEVER);

				/* 
				 * Create a tabular chart showing the records in the groups provided and their current status
				 */
				IDocumentStatusChart chart = factory.createDocumentStatusChart(Chart.CHART_TABLE, "Documents ");
				//chart.setRangeAxisLabel("");	//y-axis label

				//check
				for (int j=0; j<ds.getDs().numGroups(); j++) {
					chart.addGroup(ds.getDs().getGroup(j));
				}
				report.addManagementChart(chart);
				mgmtReports.add(report);
			} else if (reportName.equals("Recruitment Progress Report")) {
				IManagementReport report = factory.createManagementReport(ds.getDs(), ds.getDs().getName() + " - Recruitment Progress Report");
				report.setDataSet(ds.getDs());
				report.setEmailAction(RBACAction.ACTION_DR_INVESTIGATOR_REPORT);
				report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
				//report.setRole(role);
				report.setWithRawData(true);
				report.setFrequency(ReportFrequency.MONTHLY);

				/* 
				 * Create a timeseries chart showing the number of subjects consented
				 * into the trial against the targets set for each month, giving
				 * a view of the trial's progress. 
				 */
				IRecruitmentProgressChart chart = factory.createRecruitmentProgressChart(Chart.CHART_TIME_SERIES, "Recruitment Progress");

				//Set this automatically when the chart is generated (will show previous 6 months by default)
				chart.setTimePeriod(null, null);

				chart.setRangeAxisLabel("Number of Clients");	//y-axis label

				//check
				for (int j=0; j<ds.getDs().numGroups(); j++) {
					chart.addGroup(ds.getDs().getGroup(j));
				}

				report.addManagementChart(chart);
				mgmtReports.add(report);

			} else if (reportName.equals("UKCRN Report")) {
				IManagementReport report = factory.createManagementReport(ds.getDs(), ds.getDs().getName() + " - UKCRN Report");
				report.setDataSet(ds.getDs());
				report.setEmailAction(RBACAction.ACTION_DR_UKCRN_REPORT);
				report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
				report.setWithRawData(true);
				report.setFrequency(ReportFrequency.MONTHLY);
				report.setShowHeader(false);

				IUKCRNSummaryChart chart = factory.createUKCRNSummaryChart(org.psygrid.data.reporting.Chart.CHART_TABLE, ds.getDs().getName());
				report.addChart(chart);

				//This will be set automatically when the report is generated to create
				//a for the current financial year (assuming may-april)
				chart.setTimePeriod(null, null);
				mgmtReports.add(report);
			} else if (reportName.equals("Standard Code Status Report")) {
				IManagementReport report = factory.createManagementReport(ds.getDs(), ds.getDs().getName() + " - Standard Code Status Report");
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
				mgmtReports.add(report);
			} else if (reportName.equals("Basic Statistic Report")) {
				IManagementReport report = factory.createManagementReport(ds.getDs(), ds.getDs().getName() + " - Basic Statistic Report");
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
				mgmtReports.add(report);
			} else if (reportName.equals("Collection Date Report")) {
				IManagementReport report = factory.createManagementReport(ds.getDs(), ds.getDs().getName() + " - Collection Date Report");
				report.setEmailAction(RBACAction.ACTION_DR_INVESTIGATOR_REPORT);
				report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
				report.setWithRawData(true);
				report.setTemplate(true);
				report.setFrequency(ReportFrequency.NEVER);

				/* 
				 * Create a tabular chart showing the records in the groups provided and their current status
				 */
				ICollectionDateChart chart = factory.createCollectionDateChart(Chart.CHART_TABLE, "Documents ");
				chart.setRangeAxisLabel("");	//y-axis label

				//check
				for (int j=0; j<ds.getDs().numGroups(); j++) {
					chart.addGroup(ds.getDs().getGroup(j));
				}

				report.addManagementChart(chart);
				mgmtReports.add(report);
				// no email action set here
			} else if (reportName.equals("Project Summary Report")) {
				IManagementReport report = factory.createManagementReport(ds.getDs(), ds.getDs().getName() + " - Project Summary Report");
				report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
				report.setWithRawData(false);

				IProjectSummaryChart total = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_TABLE, "Total Clients");
				total.setShowTotal(true);
				report.addChart(total);
				IProjectSummaryChart chrt1 = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Whole Project");
				report.addChart(chrt1);

				for (int z=0; z<ds.getDs().numGroups(); z++) {
					IGroupsSummaryChart chrt = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, 
							ds.getDs().getDataSet().getGroup(z).getLongName());
					chrt.addGroup(ds.getDs().getGroup(z));
					report.addChart(chrt);
				}

				mgmtReports.add(report);

				// no email action set here
			} else if (reportName.equals("Group Summary Report")) {
				IManagementReport report = factory.createManagementReport(ds.getDs(), ds.getDs().getName() + " - Group Summary Report");
				report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
				report.setWithRawData(true);
//				report.setTemplate(false);

				for (int z=0; z<ds.getDs().numGroups(); z++) {
					IGroupsSummaryChart chrt = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, 
							ds.getDs().getDataSet().getGroup(z).getLongName());
					chrt.addGroup(ds.getDs().getGroup(z));
					report.addChart(chrt);
				}

				mgmtReports.add(report);
			} else if (reportName.equals("Receiving Treatment Report")) {
				IManagementReport report = factory.createManagementReport(ds.getDs(), ds.getDs().getDataSet().getName()+" - Receiving Treatment Report");
				report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
				//report.setRole(role);
				report.setWithRawData(true);
				report.setFrequency(ReportFrequency.NEVER);

				/* 
				 * Create a chart showing a list of subjects for each treatment assigned
				 * by the randomisation service. 
				 */
				IReceivingTreatmentChart chart = factory.createReceivingTreatmentChart(Chart.CHART_TABLE, ds.getDs().getName());

				//The time period will be set through psygridweb, if not a chart will 
				//be generated for the previous six months based on current date.
				chart.setTimePeriod(null, null);

				for (int w=0; w<ds.getDs().numGroups(); w++) {
					chart.addGroup(ds.getDs().getGroup(w));
				}

				report.addManagementChart(chart);
				mgmtReports.add(report);
			}
		}
		for (int y=0; y<mgmtReports.size(); y++) {
			client.saveReport(mgmtReports.get(y), sa.toString());
		}
	
	}
	
	static void saveToRepository(StudyDataSet dSet, SAMLAssertion sa) throws Exception{
		
    	try {
    		DataSet newCleanDataSet = null;
    		RepositoryClient repositoryClient = new RepositoryClient();
    
    		//clean up all the old references
    		DataSet dsHibernate = ((DataSet)dSet.getDs());
    		
    		List<Persistent> emptyDeletedObjectsList = new ArrayList<Persistent>();
    		dsHibernate.setDeletedObjects(emptyDeletedObjectsList);
    		
    		//in case of saving across different systems, deleted datasets etc. 
    		//hibernate info is not correct; so check here if the dataset exists
    		//in the repository; if not prepareElementForNewRevision
    		try {
        		repositoryClient.getDataSetSummary(dSet.getDs().getProjectCode(), new Date(0), sa.toString());
        		//if it doesn't exist, prepare el for new revision
        		//if it doesn't exist; clear id's
    		} catch (org.psygrid.data.repository.RepositoryNoSuchDatasetFault nsdf) {
    			dsHibernate.setPrepareElementForNewRevision(true);
    		}
    		
    		newCleanDataSet = dsHibernate.toDTO().toHibernate();
    		newCleanDataSet.setVersionNo("1.0.0");
    		((DataSet)newCleanDataSet).setPublished(false);
    		
    		Long savedId = repositoryClient.saveDataSet(newCleanDataSet, sa.toString());
    		
    		repositoryClient.publishDataSet(savedId, sa.toString());
    		
    		dsHibernate.setPrepareElementForNewRevision(false);
    		
    		dSet.setDirty(false);
    		//refresh copy in memory with latest from repository to keep refs up to date
    		dSet.setDs((DataSet)repositoryClient.getDataSet(savedId, sa.toString()));
    		
    	} catch (org.psygrid.data.repository.RepositoryOutOfDateFault roof) {
    		throw new Exception(roof);
    	} catch (org.psygrid.data.utils.security.NotAuthorisedFault naf) { 
    		throw new Exception(naf);
    	} catch (Exception ex) {
    		throw ex;
    	}
		
	}

}

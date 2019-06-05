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

package org.psygrid.data.reporting;

import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;

import org.apache.axis.utils.Options;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opensaml.SAMLAssertion;
import org.psygrid.data.model.dto.RecordDTO;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.reporting.client.ReportsClient;
import org.psygrid.data.reporting.definition.Factory;
import org.psygrid.data.reporting.definition.IManagementReport;
import org.psygrid.data.reporting.definition.IRecordChart;
import org.psygrid.data.reporting.definition.IRecordReport;
import org.psygrid.data.reporting.definition.IRecruitmentProgressChart;
import org.psygrid.data.reporting.definition.ISimpleChartItem;
import org.psygrid.data.reporting.definition.ISimpleChartRow;
import org.psygrid.data.reporting.definition.ITrendsChart;
import org.psygrid.data.reporting.definition.ITrendsChartRow;
import org.psygrid.data.reporting.definition.IUKCRNSummaryChart;
import org.psygrid.data.repository.dao.RepositoryDAO;
import org.psygrid.data.utils.security.NotAuthorisedFault;
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.psygrid.security.authentication.client.LoginClient;
import org.psygrid.security.authentication.service.LoginServicePortType;
import org.psygrid.security.components.net.PsyGridClientSocketFactory;
import org.psygrid.security.utils.PropertyUtilities;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;


/**
 * Populates the database with an example project to test the web 
 * services available to the CPM
 * 
 * @author Lucy Bridges
 *
 */
public class DynamicReportingCPMTest {

		
	private static RepositoryDAO dao = null;
	private static ReportingDAO reportingDAO = null;
    protected static ApplicationContext ctx = null;
    private static Factory factory = null;	
    
	private static LoginServicePortType aa1 = null;

	private static Log _log = LogFactory.getLog(DynamicReportingCPMTest.class);

	private static ReportsClient client;

	private static String studyNumber = "OLK/002001-1";

	private static boolean sendEmails = false;

	private static int reportCounter = 0;
	
	/**
	 * Sepcify the dataset to be used 
	 */
	private static Long datasetId = new Long(4143);
	//ED2 is  13188 
	//OLK 4143
	
	/**
	 * @param args the username and password required for authentication with the ws
	 */
	public static void main(String[] args) throws Exception {

		 String[] paths = {"applicationContext.xml"};
	     ctx = new ClassPathXmlApplicationContext(paths);
			dao = (RepositoryDAO)ctx.getBean("repositoryDAO");
			reportingDAO = (ReportingDAO)ctx.getBean("reportingDAO");
		factory = (Factory) ctx.getBean("reportFactory");
		
		//SAMLAssertion saml = login(args);

		//client = new ReportsClient();

		//Generate record report?
		//System.out.println("Generating all record reports..");
		//testAllRecordReports(saml.toString());
		
		//Generate trends report
	//	System.out.println("Generating all trends reports..");
	//	testAllTrendsReports(saml.toString());

		//Generate management report
//System.out.println("Generating management reports..");
	//testAllManagementReports(saml.toString());
		
		System.out.println("Testing reports job..");
		testManagementReportJob();
		//Generate UKCRN report
		//System.out.println("Generating UKCRN summary report..");
		//testUKCRNSummaryReport(saml.toString());
	
	}

	private static SAMLAssertion login(String[] args) throws Exception {

		System.setProperty("axis.socketSecureFactory",
		"org.psygrid.security.components.net.PsyGridClientSocketFactory");
		Options opts = new Options(args);
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
		System.out.println("User is: "+opts.getUser());
		System.out.println("Password is: "+opts.getPassword());
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
	System.out.println(qc.getMyProjects().size());
		System.out.println("getAssertion");
		
		SAMLAssertion sa = qc.getSAMLAssertion();

		return sa;
	}

	private static void testAllRecordReports(String saml) {

		Long dsId = datasetId;
		
		ReportsClient client = new ReportsClient();

		//Retrieve all recordReports, generate the PDF and email/save
		try {
			//use the dao to bypass security as most users aren't able to get all reports by dataset
			org.psygrid.data.reporting.definition.dto.Report[] reportList = reportingDAO.getReportsByDataSet(dsId);
			org.psygrid.data.model.dto.DataSetDTO ds = dao.getDataSet(dsId);
			for (org.psygrid.data.reporting.definition.dto.Report r: reportList) {

				try {
					//org.psygrid.data.reporting.definition.dto.RecordReport rreport = (org.psygrid.data.reporting.definition.dto.RecordReport)r;
					org.psygrid.data.reporting.definition.dto.RecordReport rreport = (org.psygrid.data.reporting.definition.dto.RecordReport)reportingDAO.getReport(r.getId());
					org.psygrid.data.reporting.definition.hibernate.RecordReport rr = rreport.toHibernate();
					//dynamically set the record from the saved 'template' report
					RecordDTO record = dao.getRecord(studyNumber, RetrieveDepth.RS_MINIMUM);
					rr.setRecord(record.toHibernate());
//					rr.setDataSet(ds.toHibernate());

					byte[] output = client.generateDynamicRecordReport(rr, "OLK/001001-1", "pdf", saml);
					FileOutputStream fos = new FileOutputStream(rreport.getTitle()+"-DynamicRecordTest.pdf");
					fos.write(output);

					System.out.println("Generated report for "+rreport.getTitle());
					System.out.println("	rows "+rr.getCharts().get(0).numRows());
					System.out.println("	rows "+rreport.getCharts()[0].numRows());
				}
				catch(ClassCastException cce) {
					//ignore anything that's not a trend report
					//cce.printStackTrace();
				}
			}

		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Couldn't fetch report", e);
		}

	}
	
	private static void testAllTrendsReports(String saml) {

		Long dsId = datasetId;
		
		ReportsClient client = new ReportsClient();

		//Retrieve all trendsReports, generate the PDF and email/save
		try {
			org.psygrid.data.model.dto.DataSetDTO ds = dao.getDataSet(dsId);
			
			//use the dao to bypass security as most users aren't able to get all reports by dataset
			org.psygrid.data.reporting.definition.dto.Report[] reportList = reportingDAO.getAllReportsByDataSet(dsId);
			System.out.println(""+reportList.length+" reports found");
			int i = 0;
			for (org.psygrid.data.reporting.definition.dto.Report r: reportList) {

				try {
					
					//retrieve the full report (rather than just a 'summary' of the data
					org.psygrid.data.reporting.definition.dto.TrendsReport rreport = (org.psygrid.data.reporting.definition.dto.TrendsReport)reportingDAO.getReport(r.getId());//(org.psygrid.data.reporting.definition.dto.TrendsReport)r;
					
					org.psygrid.data.reporting.definition.hibernate.TrendsReport rr = (org.psygrid.data.reporting.definition.hibernate.TrendsReport)rreport.toHibernate();
					for (int c = 0; c< rr.numCharts(); c++) {
						ITrendsChart chart = rr.getChart(c);
						for (int row = 0; row < chart.numRows(); row++) {
							if (chart.getRow(row).getSummaryType().equals(ITrendsChartRow.SUMMARY_TYPE_MEAN)) {
								chart.getRow(row).setSummaryType(ITrendsChartRow.SUMMARY_TYPE_MEDIAN);
							}
						}
					}
					
					rreport.setDataSet(ds);
					byte[] output = client.generateDynamicTrendsReport(rr, "pdf", saml);
					
					FileOutputStream fos = new FileOutputStream(rreport.getTitle()+" - "+ i++ +" - DynamicTrendsTest.pdf");
					fos.write(output);

					System.out.println("Generated report for "+rreport.getTitle());
				}
				catch(ClassCastException cce) {
					//ignore anything that's not a trend report
					//cce.printStackTrace();
				}
			}

		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Couldn't fetch report", e);
		}

	}

	public static void testAllManagementReports(String saml) {

		Long dsId = datasetId;

		//Generate the report and email/save the output
		try {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DATE, 1);
			cal.set(Calendar.MONTH, 0);
			while ( cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY ){
				cal.add(Calendar.DATE, 1);
			}

//			use the dao to bypass security as most users aren't able to get all reports by dataset
			org.psygrid.data.reporting.definition.dto.Report[] reportList = reportingDAO.getAllReportsByDataSet(dsId);
			
			for (org.psygrid.data.reporting.definition.dto.Report r: reportList) {
				try {
					org.psygrid.data.reporting.definition.dto.ManagementReport rreport = (org.psygrid.data.reporting.definition.dto.ManagementReport)r;
					org.psygrid.data.model.dto.DataSetDTO ds = dao.getDataSet(dsId);
					
					rreport.setDataSet(ds);
					byte[] output = client.generateDynamicManagementReport(rreport.toHibernate(), "pdf", saml);
					
					 
					//FileOutputStream fos = new FileOutputStream(rreport.getTitle()+"-DynamicManagementTest.pdf");
					//fos.write(output);

					System.out.println("Generated report for "+rreport.getTitle());
					System.out.println("With email action: "+rreport.getEmailAction());
					System.out.println("With view action: "+rreport.getViewAction());
				}
				catch(ClassCastException cce) {
					//ignore anything that's not a management report
					//cce.printStackTrace();
				}
				catch (NotAuthorisedFault ne) {
					//ignore because there will be reports in the list that the user isn't allowed to send
					ne.printStackTrace();
					throw new RuntimeException("Couldn't generate/send report "+ne.getCause(), ne);
				}
			}

		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Couldn't generate/send report ", e);
		}	
	}

	public static void testManagementReportJob() {

//		Long dsId = datasetId;

		//Generate the reports
		try {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DATE, 1);
			cal.set(Calendar.MONTH, 0);
			while ( cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY ){
				cal.add(Calendar.DATE, 1);
			}

//			use the same dao method as the ReportsJob bean
			String user = "CN=John Ainsworth, OU=Informatics, O=PsyGrid, C=UK";
			List<ManagementReport> reportList = reportingDAO.generateAllMgmtReports(user, new Date());			
			for (ManagementReport r: reportList) {
				
					System.out.println("Generated report for "+r.getTitle());
					System.out.println("Recipients are: ");
					for (String email: r.getRecipients()) {
						System.out.print(email+", ");
					}
			}

		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Couldn't generate/send report ", e);
		}	
	}

	
	public static void testUKCRNSummaryReport(String saml) {

		Long dsId = datasetId;

		//Generate the report and email/save the output
		try {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DATE, 1);
			cal.set(Calendar.MONTH, 0);
			while ( cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY ){
				cal.add(Calendar.DATE, 1);
			}

			//use the dao to bypass security as most users aren't able to get all reports by dataset
			org.psygrid.data.reporting.definition.dto.Report[] reportList = reportingDAO.getAllReportsByDataSet(dsId);
			for (org.psygrid.data.reporting.definition.dto.Report r: reportList) {
				try {
					
					if (r.getTitle().endsWith("UKCRN Report")) {
						byte[] output = client.generateDynamicManagementReport((IManagementReport)r.toHibernate(), "xls", saml);
						
						FileOutputStream fos = new FileOutputStream(r.getTitle()+"-DynamicManagementTest.xls");
						fos.write(output);

						System.out.println("Generated report for "+r.getTitle());
					}
				}
				catch(ClassCastException cce) {
					//ignore anything that's not a trend report
					//cce.printStackTrace();
				}
			}

		}
		catch (Exception e) {
			throw new RuntimeException("Couldn't generate/send report ", e);
		}	
	}

	/**
	 * Generate a Record Report for the GAF document,
	 * without saving the report definition. The
	 * report output is saved to the filesystem as 
	 * pdf and xls documents.
	 */
	public static void testDynamicGAFRecordReport(String saml) {
		//creates pdf and xls documents
		Long dsId = new Long(1);
		DataSet ds = null;

		try {
			ds = dao.getDataSet(dsId).toHibernate();
		}
		catch (Exception e) {
			throw new RuntimeException("Unable to retrieve ds", e);
		}

		Document gaf = ds.getDocument(8);
		//check that we have the right document!
		if ( !gaf.getName().equals("GAF Data Entry Sheet") ){
			throw new RuntimeException("This isn't the GAF document!!!");
		}
		DocumentOccurrence docOcc = gaf.getOccurrence(0);

		IRecordReport gafReport = 
			factory.createRecordReport(ds, "Gaf Record Report - "+docOcc.getDisplayText());

		Section mainSec = gaf.getSection(0);
		SectionOccurrence mainSecOcc = mainSec.getOccurrence(0);

		//assign a particular record to the report
		try {
			RecordDTO record = dao.getRecord(studyNumber, RetrieveDepth.RS_MINIMUM);
			gafReport.setRecord(record.toHibernate());
			System.out.println("Record id: "+record.getId()+" and identifier is: "+record.getIdentifier().getIdentifier());
		}
		catch(Exception e) {
			throw new RuntimeException("Problem retrieving record", e);
		}

		//various chart types
		String[] types = new String[7];
		types[0] = org.psygrid.data.reporting.Chart.CHART_TABLE;
		types[1] = org.psygrid.data.reporting.Chart.CHART_BAR;
		types[2] = org.psygrid.data.reporting.Chart.CHART_BAR_HZ;
		types[3] = org.psygrid.data.reporting.Chart.CHART_LINE;
		types[4] = org.psygrid.data.reporting.Chart.CHART_LINE_HZ;
		types[5] = org.psygrid.data.reporting.Chart.CHART_PIE;
		types[6] = org.psygrid.data.reporting.Chart.CHART_STACKED_BAR;

		//create a chart for each possible chart type
		for (String type: types) {

			IRecordChart chart =  
				factory.createSimpleChart(type,
						"Test - "+type);
			try {
				gafReport.addChart(chart);

				chart.setRangeAxisLabel("Score");
				ISimpleChartRow row = factory.createSimpleChartRow();
				chart.addRow(row);
				try {
					ISimpleChartItem s1 = factory.createSimpleChartItem(gaf.getEntry(1), docOcc, mainSecOcc);
					row.addSeries(s1);

					ISimpleChartItem s2 = factory.createSimpleChartItem(gaf.getEntry(2), docOcc, mainSecOcc);
					row.addSeries(s2);

					ISimpleChartItem s3 = factory.createSimpleChartItem(gaf.getEntry(3), docOcc, mainSecOcc);
					row.addSeries(s3);	
				}
				//(obs) if the entry doesn't exist continue anyway
				catch (Exception e) {
					throw new RuntimeException(e);
				}

			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}


		//Finally, generate the report and save to file
		try {
			byte[] output  = null;
			byte[] output2 = null;
			
			output = client.generateDynamicRecordReport(gafReport, "OLK/001001-1", "pdf", saml);
			output2 = client.generateDynamicRecordReport(gafReport, "OLK/001001-1", "xls", saml);

			FileOutputStream fos = new FileOutputStream(gafReport.getTitle()+"-DynamicTest.pdf");
			fos.write(output);
			FileOutputStream fos2 = new FileOutputStream(gafReport.getTitle()+"-DynamicTest.xls");
			fos2.write(output2);

		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Generate a Management Report for recruitment progress, 
	 * without saving the report definition. The report 
	 * output is saved to the filesystem as pdf and xls 
	 * documents.
	 */
	public static void testDynamicRecruitmentProgressReport(String saml) {

		Long dsId = datasetId;
		DataSet ds = null;

		try {
			ds = dao.getDataSet(dsId).toHibernate();
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Unable to retrieve ds", e);
		}

		IManagementReport mReport = factory.createManagementReport(ds, "Recruitment Progress (All) - Management Report");
		//mReport.setRole("PrincipalInvestigator");
		mReport.setWithRawData(false);

		/* 
		 * Create a timeseries chart showing the number of subjects consented
		 * into the trial against the targets set for each month, giving
		 * a view of the trial's progress. 
		 */
		IRecruitmentProgressChart chart = factory.createRecruitmentProgressChart(Chart.CHART_TIME_SERIES, "Recruitment Progress (All) Chart");

		//generate a chart for the previous six months, based on current date
		Calendar curDate = Calendar.getInstance();
		Calendar startDate = new GregorianCalendar(curDate.get(Calendar.YEAR), curDate.get(Calendar.MONTH) - 6, 0);
		Calendar endDate   = new GregorianCalendar(curDate.get(Calendar.YEAR), curDate.get(Calendar.MONTH), 0);

		chart.setTimePeriod(startDate, endDate);

		chart.setRangeAxisLabel("Number of Clients");	//y-axis label

		int target = 10;		//expecting to recruit 10 people per month

		//create default recruitment targets for the six months covered by the chart
		for (int i = 0; i < 6; i++) {
			int month = startDate.get(Calendar.MONTH) + i;		//Calendar class will automatically take into account changes in the year
			int year  = startDate.get(Calendar.YEAR);
			Calendar cal = new GregorianCalendar(year, month+1, 0);	//add one to month to compensate for Calendar's month starting at 0

			chart.addTarget(cal, target);
		}

		try {
			mReport.addManagementChart(chart);
		}
		catch (Exception e) {
			throw new RuntimeException("Unable to add chart", e);
		}

		try {
			byte[] output  = null;
			byte[] output2 = null;
			
				output  = client.generateDynamicManagementReport(mReport, "pdf", saml);
				output2 = client.generateDynamicManagementReport(mReport, "xls", saml);
			
			FileOutputStream fos = new FileOutputStream(mReport.getTitle()+"-DynamicTest.pdf");
			fos.write(output);

			FileOutputStream fos2 = new FileOutputStream(mReport.getTitle()+"-DynamicTest.xls");
			fos2.write(output2);
		}
		catch (Exception e) {
			throw new RuntimeException("Problem generating/saving report", e);
		}
	}
	
	/**
	 * Generate a Management Report for the UKCRN Summary 
	 * without saving the report definition. The report 
	 * output is saved to the filesystem as pdf and xls 
	 * documents.
	 */
	public static void testDynamicUKCRNSummaryReport(String saml) {
		
		Long dsId = datasetId;
		DataSet ds = null;

		try {
			ds = dao.getDataSet(dsId).toHibernate();
		}
		catch (Exception e) {
			throw new RuntimeException();
		}
		
		IManagementReport report = factory.createManagementReport(ds, ds.getProjectCode()+" Test - UKCRN Report");
		//report.setRole("ChiefInvestigator");
		report.setWithRawData(false);
		report.setDataSet(ds);
		IUKCRNSummaryChart chart = factory.createUKCRNSummaryChart(org.psygrid.data.reporting.Chart.CHART_TABLE, ds.getProjectCode());
		
		try {
			report.addChart(chart);
		}
		catch (Exception e) {
			throw new RuntimeException("Unable to add chart", e);
		}

		//generate a chart for the current financial year (assuming may-april)
		Calendar curDate = Calendar.getInstance();
		Calendar startDate;
		Calendar endDate;
		if (curDate.get(Calendar.MONTH) < Calendar.MAY) {
			//+1 to the month's because Calendar's months start at 0
			startDate = new GregorianCalendar(curDate.get(Calendar.YEAR) -1, Calendar.MAY +1, 0);
			endDate   = new GregorianCalendar(curDate.get(Calendar.YEAR), Calendar.APRIL +1, 0);	
		}
		else {
			startDate = new GregorianCalendar(curDate.get(Calendar.YEAR), Calendar.MAY +1, 0);
			endDate   = new GregorianCalendar(curDate.get(Calendar.YEAR) +1, Calendar.APRIL +1, 0);
		}

		chart.setTimePeriod(startDate, endDate);
		
		try {		
			byte[] output  = null;
			byte[] output2 = null;

				output  = client.generateDynamicManagementReport(report, "pdf", saml);
				output2 = client.generateDynamicManagementReport(report, "xls", saml);
				
			FileOutputStream fos = new FileOutputStream(report.getTitle()+"-DynamicTest.pdf");
			fos.write(output);
			FileOutputStream fos2 = new FileOutputStream(report.getTitle()+"-DynamicTest.xls");
			fos2.write(output2);
		}
		catch (Exception e) {
			throw new RuntimeException("Unable to add chart", e);
		}
	}

}


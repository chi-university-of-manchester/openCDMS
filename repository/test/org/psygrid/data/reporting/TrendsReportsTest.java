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

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.mail.internet.MimeMessage;

import org.psygrid.data.dao.DAOTest;
import org.psygrid.data.dao.DAOTestHelper;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.reporting.client.ReportsClient;
import org.psygrid.data.reporting.definition.ISimpleChartItem;
import org.psygrid.data.reporting.definition.ISimpleChartRow;
import org.psygrid.data.reporting.definition.ITrendsChart;
import org.psygrid.data.reporting.definition.ITrendsChartRow;
import org.psygrid.data.reporting.definition.ITrendsReport;
import org.psygrid.data.reporting.renderer.PdfRenderer;
import org.psygrid.data.repository.dao.RepositoryDAO;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 * JUnit test for the TrendsChart class, which 
 * summarises data for reports, used to spot trends 
 * 
 * @author Lucy Bridges
 *
 */
public class TrendsReportsTest extends DAOTest {

	private RepositoryDAO dao = null;

	private ReportingDAO reportingDAO = null;

	private Factory factory = null;

	final static private boolean sendEmails = false;

	private static int reportCounter = 0;
	
	private org.psygrid.data.reporting.definition.Factory reportFactory = null;

	Long dsId = null;

	protected void setUp() throws Exception {
		super.setUp();
		dao = (RepositoryDAO)ctx.getBean("repositoryDAO");
		reportingDAO = (ReportingDAO)ctx.getBean("reportingDAO");
		factory = (Factory) ctx.getBean("factory");
		reportFactory = (org.psygrid.data.reporting.definition.Factory)ctx.getBean("reportFactory");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		dao = null;
		factory = null;
	}

	public void testSaveDUPReport() {

		ITrendsReport report = createDUPReport();

		ReportsClient client = new ReportsClient();

		Long reportId = null;
		try {
			reportId = reportingDAO.saveReport(report.toDTO());
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't save report ");
		}
		assertNotNull(reportId);
		
		ITrendsReport savedReport = null;
		try {
			//retrieve report
			//List<IReport> reports = client.getAllReportsByDataSet(dsId, null);
			org.psygrid.data.reporting.definition.dto.Report[] reports = reportingDAO.getAllReportsByDataSet(dsId);
			for (org.psygrid.data.reporting.definition.dto.Report r: reports) {
				if (r.getTitle().equals(report.getTitle())) {
					savedReport = (ITrendsReport)r.toHibernate();
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't retrieve report ");
		}

		//assertions
		try {
			assertNotNull(savedReport);
			assertNotNull(savedReport.getChart(0));
			ITrendsChart c = (ITrendsChart)savedReport.getChart(0);
			assertNotNull(c.getTitle());
			assertNotNull(c.getType(0));
			assertNotNull(c.getStartDate());
			assertNotNull(c.getEndDate());
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Problem with assertions");
		}
	}


	/**
	 * Create and email a DUP trend report for the
	 * Outlook dataset.
	 */
	public void testGenerateOutlookDUPReport() {

		//Setup and save the report
		ITrendsReport report = existingDUPReport(); 

		ReportsClient client = new ReportsClient();
		Long reportId = null;
		try {
			reportId = reportingDAO.saveReport(report.toDTO());
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't save report ");
		}

		assertNotNull("report id is null", reportId);
		//Generate the report and email the output
		try {
			List<String> allowedGroups = new ArrayList<String>();
			allowedGroups.add("002001");
			allowedGroups.add("002002");
			//Render the report
			Report r = reportingDAO.generateTrendReport(reportId, dsId, allowedGroups);
			System.out.println("Report title is: "+r.getTitle()); 

			if (sendEmails) {
				emailReport(r, r.getTitle());
			}
			else {
				saveReport(r, r.getTitle());
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't generate/send report "+e.getMessage());
		}	
	}

	/**
	 * Create and email a test DUP trend report
	 * 
	 */
	public void testGenerateDUPReport() {

		//Setup and save the report
		ITrendsReport report = createDUPReport(); 

		ReportsClient client = new ReportsClient();
		Long reportId = null;
		try {
			reportId = reportingDAO.saveReport(report.toDTO());
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't save report ");
		}

		assertNotNull("report id is null", reportId);
		//Generate the report and email the output
		try {

			List<String> allowedGroups = new ArrayList<String>();
			allowedGroups.add("002001");
			allowedGroups.add("002002");
			//Render the report
			Report r = reportingDAO.generateTrendReport(reportId, dsId, allowedGroups); 
			System.out.println("Report title is: "+r.getTitle()); 

			if (sendEmails) {
				emailReport(r, r.getTitle());
			}
			else {
				saveReport(r, r.getTitle());
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't generate/send report "+e.getMessage());
		}	
	}
	
	
	public void testAllOutlookTrendsReports() {

		dsId = new Long(1);
		
		ReportsClient client = new ReportsClient();

		//Retrieve all trendsReports, generate the PDF and email 
		try {
			org.psygrid.data.reporting.definition.dto.Report[] reportList = reportingDAO.getAllReportsByDataSet(dsId);
			for (org.psygrid.data.reporting.definition.dto.Report r: reportList) {
				
				try {
					Long reportId = ((org.psygrid.data.reporting.definition.dto.TrendsReport)r).getId();
					
					List<String> allowedGroups = new ArrayList<String>();
					allowedGroups.add("002001");
					allowedGroups.add("002002");
					allowedGroups.add("001001");
					allowedGroups.add("003001");
					allowedGroups.add("003002");
					allowedGroups.add("004001");
					allowedGroups.add("005001");
					allowedGroups.add("005002");
					allowedGroups.add("006001");
					allowedGroups.add("007001");
					allowedGroups.add("008001");
					allowedGroups.add("002003");
					Report report = reportingDAO.generateTrendReport(reportId, dsId, allowedGroups);
					System.out.println("Generated report for "+report.getTitle());
					
					if (sendEmails) {
						emailReport(report, r.getTitle());
					}
					else {
						saveReport(report, r.getTitle());
					}
				}
				catch(ClassCastException cce) {
					//ignore anything that's not a trend report
					//cce.printStackTrace();
				}
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't fetch report");
		}
		
	}
	
	private ITrendsReport createDUPReport() {

//		IDataSet ds = createDataset();

		String name = "testDUPTrendsReport - "+(new Date()).toString();
		DataSet ds = null;
		dsId = new Long(1);

		try {
			ds = dao.getDataSet(dsId).toHibernate();
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't retrieve dataset: "+ e.getMessage());
		}

		
		Document dupDoc = ds.getDocument(9);
		//check that we have the right document!
		if ( !dupDoc.getName().equals("DUP") ){
			fail("This isn't the DUP document!!!");
		}
		DocumentOccurrence dupBaseline = dupDoc.getOccurrence(0);

		ITrendsReport dupReport = reportFactory.createTrendsReport(ds, "DUP - "+dupBaseline.getDisplayText()+" - (JUnit Test) Trends");

		Section mainSec = dupDoc.getSection(0);
		SectionOccurrence mainSecOcc = mainSec.getOccurrence(0);

		//Summary chart
		ITrendsChart summary = reportFactory.createTrendsChart(org.psygrid.data.reporting.Chart.CHART_TABLE,
		"Summary");
		summary.addType(org.psygrid.data.reporting.Chart.CHART_TIME_SERIES);

		try {
			dupReport.addChart(summary);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Unable to add chart");
		}

		ISimpleChartRow dup = reportFactory.createSimpleChartRow(); 
		dup.setLabel("Duration of Untreated Psychosis");
		ISimpleChartRow helpSeeking = reportFactory.createSimpleChartRow(); 
		helpSeeking.setLabel("Help Seeking");
		ISimpleChartRow referral = reportFactory.createSimpleChartRow(); 
		referral.setLabel("Referral");

		//referral date to EIS?  (or entry(3) - 1st referral to MHS?)
		ISimpleChartItem s1 = reportFactory.createSimpleChartItem(dupDoc.getEntry(5), dupBaseline, mainSecOcc);
		try {
			referral.addSeries(s1);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Unable to add series s1");
		}

		//delay in help-seeking?
		ISimpleChartItem s3 = reportFactory.createSimpleChartItem(dupDoc.getEntry(8), dupBaseline, mainSecOcc);
		try {
			helpSeeking.addSeries(s3);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Unable to add series s3");
		}

		//duration of untreated psychosis?
		ISimpleChartItem s7 = reportFactory.createSimpleChartItem(dupDoc.getEntry(12), dupBaseline, mainSecOcc);
		try {
			dup.addSeries(s7);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Unable to add series s7");
		}

		//set the time period for the graph 
		summary = setPreviousSixMonths(summary);
		
		return dupReport;
	}

	/**
	 * Create and email a DUP trend report for the
	 * Outlook dataset.
	 */
	public void testGenerateOutlookPANSSReport() {

		//Setup and save the report
		ITrendsReport report = existingPANSSReport(); 

		ReportsClient client = new ReportsClient();
		Long reportId = null;
		try {
			reportId = reportingDAO.saveReport(report.toDTO());
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't save report ");
		}

		assertNotNull("report id is null", reportId);
		//Generate the report and email the output
		try {

			Calendar date = Calendar.getInstance();
			System.out.println("Date: "+date.get(Calendar.YEAR) +" "+ date.get(Calendar.MONTH));

			//Render the report
			List<String> allowedGroups = new ArrayList<String>();
			allowedGroups.add("002001");
			allowedGroups.add("002002");
			Report r = reportingDAO.generateTrendReport(reportId, dsId, allowedGroups);
			System.out.println("Report title is: "+r.getTitle()); 
			
			if (sendEmails) {
				emailReport(r, r.getTitle());
			}
			else {
				saveReport(r, r.getTitle());
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't generate/send report "+e.getMessage());
		}	
	}

	private DataSet createDataset() {
		String name = "testTrendsReport - "+(new Date()).toString();
		DataSet ds = factory.createDataset(name);
		//generate unique project code
		java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
		String projectCode = DAOTestHelper.checkProjectCode(guid.toString());            
		ds.setProjectCode(projectCode);

		Group g1 = factory.createGroup("G1");
		ds.addGroup(g1);
		Group g2 = factory.createGroup("G2");
		ds.addGroup(g2);
		Group g3 = factory.createGroup("G3");
		ds.addGroup(g3);


		try {
			dsId = dao.saveDataSet(ds.toDTO());
			dao.publishDataSet(dsId);
			ds = dao.getDataSet(dsId).toHibernate();
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't save/retrieve dataset: "+ e.getMessage());
		}

		//populate the dataset
		try {
			//generate identifiers...
			Identifier[] ids = DAOTestHelper.getIdentifiers(dao, projectCode, dsId, 2, "G1");

			//create a couple of records
			Record r1 = ds.generateInstance();
			r1.setIdentifier(ids[0]);
			dao.saveRecord(r1.toDTO(), true, null, "NoUser");
			Record r2 = ds.generateInstance();
			r2.setIdentifier(ids[1]);
			dao.saveRecord(r2.toDTO(), true, null, "NoUser");

			//create another record having a different group
			ids = DAOTestHelper.getIdentifiers(dao, projectCode, dsId, 1, "G3");
			Record r3 = ds.generateInstance();
			r3.setIdentifier(ids[0]);
			dao.saveRecord(r3.toDTO(), true, null, "NoUser");
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't save/retrieve dataset: "+ e.getMessage());
		}

		return ds;
	}

	private ITrendsReport existingDUPReport() {

		String name = "testDUPTrendsReport - "+(new Date()).toString();
		DataSet ds = null;
		dsId = new Long(5133);

		try {
			ds = dao.getDataSet(dsId).toHibernate();
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't retrieve dataset: "+ e.getMessage());
		}

		Document dupDoc = ds.getDocument(9);
		//check that we have the right document!
		if ( !dupDoc.getName().equals("DUP") ){
			fail("This isn't the DUP document!!!");
		}
		DocumentOccurrence dupBaseline = dupDoc.getOccurrence(0);

		Section mainSec = dupDoc.getSection(0);
		SectionOccurrence mainSecOcc = mainSec.getOccurrence(0);


		//create the report
		ITrendsReport report = reportFactory.createTrendsReport(ds, "Test DUP Trend Report 1");

		ITrendsChart chart = reportFactory.createTrendsChart(Chart.CHART_TIME_SERIES, "Test DUP Trend Chart 1");
		chart.addType(Chart.CHART_TABLE);
		
		//display the graph for the previous six months 
		chart = setPreviousSixMonths(chart);
				
		//get the mean average of the values in the summary
		String summaryType = ITrendsChartRow.SUMMARY_TYPE_MEAN;
		
		//set the range axis (y-axis) label
		chart.setRangeAxisLabel("Days");
				
		//display a bar chart with the total cases per month on the same graph
		chart.setShowTotals(true);		
		
		
		ITrendsChartRow dup = reportFactory.createTrendsChartRow(); 
		dup.setLabel("Duration of Untreated Psychosis");
		dup.setSummaryType(summaryType);
		ITrendsChartRow helpSeeking = reportFactory.createTrendsChartRow(); 
		helpSeeking.setLabel("Help Seeking");
		helpSeeking.setSummaryType(summaryType);
		ITrendsChartRow referral = reportFactory.createTrendsChartRow(); 
		referral.setLabel("Referral");
		referral.setSummaryType(summaryType);

		try {
			chart.addRow(dup);
			chart.addRow(helpSeeking);
			chart.addRow(referral);

			//delay in help-seeking?
			ISimpleChartItem s3 = reportFactory.createSimpleChartItem(dupDoc.getEntry(8), dupBaseline, mainSecOcc);
			helpSeeking.addSeries(s3);

			//delay within MHS?
			ISimpleChartItem s5 = reportFactory.createSimpleChartItem(dupDoc.getEntry(10), dupBaseline, mainSecOcc);
			referral.addSeries(s5);		//add to s6?

			//duration of untreated psychosis?
			ISimpleChartItem s7 = reportFactory.createSimpleChartItem(dupDoc.getEntry(12), dupBaseline, mainSecOcc);
			dup.addSeries(s7);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Problem when adding series to a row");
		}
		
		try {
			report.addChart(chart);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Couldn't add a chart to the report ");
		}

		return report;
	}
	
	
	private ITrendsReport existingPANSSReport() {

		String name = "testPANSSTrendsReport - "+(new Date()).toString();
		DataSet ds = null;
		dsId = new Long(1);

		try {
			ds = dao.getDataSet(dsId).toHibernate();
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't retrieve dataset: "+ e.getMessage());
		}

		Document panss = ds.getDocument(6);
		//check that we have the right document!
		if ( !panss.getName().equals("PANSS") ){
			fail("This isn't the PANSS document!!!");
		}

		//Assuming baseline panss document only is to be used 
		DocumentOccurrence docOcc = panss.getOccurrence(0);

		
		Section posSec = panss.getSection(1);
		SectionOccurrence posSecOcc = posSec.getOccurrence(0);
		Section negSec = panss.getSection(2);
		SectionOccurrence negSecOcc = negSec.getOccurrence(0);
		Section genSec = panss.getSection(3);
		SectionOccurrence genSecOcc = genSec.getOccurrence(0);

		//create the report
		ITrendsReport report = reportFactory.createTrendsReport(ds, "Test PANSS Trend Report 1");

		ITrendsChart chart = reportFactory.createTrendsChart(Chart.CHART_TIME_SERIES, "Test PANSS Trend Chart 1");
		chart.addType(Chart.CHART_TABLE);

		Calendar date = Calendar.getInstance();
		//display the graph for the previous six months 
		//+1 to the months because Calendar's months start at 0
		Calendar startDate = new GregorianCalendar(date.get(Calendar.YEAR), date.get(Calendar.MONTH) - 6, 0);
		Calendar endDate   = new GregorianCalendar(date.get(Calendar.YEAR), date.get(Calendar.MONTH), 0);
		chart.setTimePeriod(startDate, endDate);
		chart = setPreviousSixMonths(chart);
		//get the mean average of the values in the summary
		String summaryType = ITrendsChartRow.SUMMARY_TYPE_MEAN;

		//set the range axis (y-axis) label
		chart.setRangeAxisLabel("Total Scores");

		try {

			ITrendsChartRow posRow = reportFactory.createTrendsChartRow(); 
			posRow.setLabel("Positive");
			posRow.setSummaryType(summaryType);
			ITrendsChartRow negRow = reportFactory.createTrendsChartRow(); 
			negRow.setLabel("Negative");
			negRow.setSummaryType(summaryType);
			ITrendsChartRow genRow = reportFactory.createTrendsChartRow(); 
			genRow.setLabel("General");
			genRow.setSummaryType(summaryType);

			chart.addRow(posRow);
			chart.addRow(negRow);
			chart.addRow(genRow);

			ISimpleChartItem posTotal = reportFactory.createSimpleChartItem(panss.getEntry(8), docOcc, posSecOcc);
			posRow.addSeries(posTotal);
			ISimpleChartItem negTotal = reportFactory.createSimpleChartItem(panss.getEntry(16), docOcc, negSecOcc);
			negRow.addSeries(negTotal);
			ISimpleChartItem genTotal = reportFactory.createSimpleChartItem(panss.getEntry(34), docOcc, genSecOcc);
			genRow.addSeries(genTotal);
			//ISimpleChartItem total = factory.createSimpleChartItem(panss.getEntry(35), docOcc, genSecOcc);
			//summary.addItem(total);
		
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Problem when adding series to a row");
		}

		try {
			report.addChart(chart);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Couldn't add chart to the report ");
		}

		return report;
	}
	
	private static void emailReport(Report r, String name) {

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PdfRenderer renderer = new PdfRenderer();

		try { 
			renderer.render(r, os);

			//Email
			JavaMailSenderImpl sender = new JavaMailSenderImpl();
			sender.setHost("echobase.smb.man.ac.uk");

			MimeMessage message = sender.createMimeMessage();

			//use the true flag to indicate you need a multipart message
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setTo("lucy.bridges@manchester.ac.uk");
			helper.setFrom("lucy.bridges@manchester.ac.uk");
			helper.setSubject("Test "+name+" Trends Report");
			helper.setSentDate(new Date());

			//use the true flag to indicate the text included is HTML
			helper.setText(
					"<html><body><p>Test email of "+name+" trends report (hopefully!)</p></body></html>", true);

			InputStreamSource src = new ByteArrayResource(os.toByteArray());
			helper.addAttachment("test.pdf", src);



			if (sendEmails) {
				sender.send(message);
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Couldn't email report");
		}

	}
	
	private static void saveReport(Report r, String name) {
		
		try {
			FileOutputStream fos = new FileOutputStream("Test "+reportCounter+" "+r.getTitle()+".pdf");
			PdfRenderer renderer = new PdfRenderer();
			renderer.render(r, fos);
			reportCounter++;
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Couldn't email report");
		}
	}
	
	/**
	 * Add a time period to a given chart, for the previous six months
	 * based on the current date.
	 *  
	 * @param chart
	 * @return chart
	 */
	private static ITrendsChart setPreviousSixMonths(ITrendsChart chart) {

		//generate the dates for the previous six months, based on current date
		Calendar curDate = Calendar.getInstance();
		//+1 to the months because Calendar's months start at 0
		Calendar startDate = new GregorianCalendar(curDate.get(Calendar.YEAR), curDate.get(Calendar.MONTH) +1 - 6, 0);
		Calendar endDate   = new GregorianCalendar(curDate.get(Calendar.YEAR), curDate.get(Calendar.MONTH) +1, 0);

		chart.setTimePeriod(startDate, endDate);
		return chart;
	}
}

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
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.reporting.client.ReportsClient;
import org.psygrid.data.reporting.definition.IReport;
import org.psygrid.data.reporting.definition.ISimpleChartItem;
import org.psygrid.data.reporting.definition.ITrendsChart;
import org.psygrid.data.reporting.definition.ITrendsChartRow;
import org.psygrid.data.reporting.definition.ITrendsGanttChart;
import org.psygrid.data.reporting.definition.ITrendsReport;
import org.psygrid.data.reporting.definition.ReportException;
import org.psygrid.data.reporting.renderer.PdfRenderer;
import org.psygrid.data.repository.dao.RepositoryDAO;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

public class PanssTrendsReportTest extends DAOTest {

	private RepositoryDAO dao = null;

	private ReportingDAO reportingDAO = null;

	final static private boolean sendEmails = false;

	private org.psygrid.data.reporting.definition.Factory reportFactory = null;

	private static int reportCounter = 0;

	Long dsId = null;

	protected void setUp() throws Exception {
		super.setUp();
		dao = (RepositoryDAO)ctx.getBean("repositoryDAO");
		reportingDAO = (ReportingDAO)ctx.getBean("reportingDAO");
		reportFactory = (org.psygrid.data.reporting.definition.Factory)ctx.getBean("reportFactory");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		dao = null;
	}

	public void testSaveReport() {

		DataSet ds = null;
		dsId = new Long(1);

		try {
			ds = dao.getDataSet(dsId).toHibernate();
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't retrieve dataset: "+ e.getMessage());
		}

		ITrendsReport report = createReport(ds, null, "all");

		ReportsClient client = new ReportsClient();

		Long reportId = null;
		try {
			reportId = client.saveReport(report, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't save report ");
		}
		assertNotNull(reportId);

		List<IReport> reportsList = null;
		try {
			reportsList = client.getAllReportsByDataSet(dsId, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't save report ");
		}

		ITrendsReport savedReport = null;
		for (IReport r: reportsList) {
			if (r.getTitle().equals(report.getTitle())) {
				//assume they are the same report
				savedReport = (ITrendsReport)r;
				break;
			}
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

	public void testGenerateReport() {

		DataSet ds = null;
		dsId = new Long(1);

		try {
			ds = dao.getDataSet(dsId).toHibernate();
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't retrieve dataset: "+ e.getMessage());
		}

		ITrendsReport report = createReport(ds, null, "all");

		ReportsClient client = new ReportsClient();

		Long reportId = null;
		try {
			reportId = client.saveReport(report, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't save report");	
		}

		//Render the report
		try {
			List<String> allowedGroups = new ArrayList<String>();
			allowedGroups.add("002001");
			allowedGroups.add("002002");

			Report r = reportingDAO.generateTrendReport(reportId, dsId, allowedGroups); 
			System.out.println("Report title is: "+r.getTitle());

			if (sendEmails) {
				emailReport(r, "Test All Groups");
			}
			else {
				saveReport(r, "TestAllGroups");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't render the report");
		}


	}

	public void testGenerateReportForWM() {

		DataSet ds = null;
		dsId = new Long(1);

		try {
			ds = dao.getDataSet(dsId).toHibernate();
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't retrieve dataset: "+ e.getMessage());
		}

		ITrendsReport report = null; 

		try {
			report = panssInWestMidlandsReport(ds);
		}
		catch (ReportException re) {
			re.printStackTrace();
			fail("Unable to create report");
		}

		ReportsClient client = new ReportsClient();

		Long reportId = null;
		try {
			reportId = client.saveReport(report, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't save report");
		}
		assertNotNull(reportId);

		//Render the report
		try {
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
			Report r = reportingDAO.generateTrendReport(reportId, dsId, allowedGroups); 
			System.out.println("Report title is: "+r.getTitle());

			if (sendEmails) {
				emailReport(r, "Test West Midlands Groups");
			}
			else {
				saveReport(r, "Test West Midlands Groups");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't render the report");
		}

	}

	public void testGenerateReportForEA() {

		DataSet ds = null;
		dsId = new Long(1);

		try {
			ds = dao.getDataSet(dsId).toHibernate();
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't retrieve dataset: "+ e.getMessage());
		}

		ITrendsReport report = null; 

		try {
			report = panssTrendsInEastAngliaReport(ds);
		}
		catch (ReportException re) {
			re.printStackTrace();
			fail("Unable to create report");
		}

		ReportsClient client = new ReportsClient();

		Long reportId = null;
		try {
			reportId = client.saveReport(report, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't save report");
		}
		assertNotNull(reportId);

		//Render the report
		try {
			List<String> allowedGroups = new ArrayList<String>();
			allowedGroups.add("002001");
			allowedGroups.add("002002");
			allowedGroups.add("002003");
			Report r = reportingDAO.generateTrendReport(reportId, dsId, allowedGroups);
			System.out.println("Report title is: "+r.getTitle());

			if (sendEmails) {
				emailReport(r, "Test East Anglia Groups");
			}
			else {
				saveReport(r, "Test East Anglia Groups");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't render the report");
		}

	}

	public void testGenerateTreatmentsReport() {

		DataSet ds = null;
		dsId = new Long(1);

		try {
			ds = dao.getDataSet(dsId).toHibernate();
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't retrieve dataset: "+ e.getMessage());
		}
		ITrendsReport report = null;
		try {
			report = panssTreatmentsReport(ds, null, "all");
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't create report");	
		}

		ReportsClient client = new ReportsClient();

		Long reportId = null;
		try {
			reportId = client.saveReport(report, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't save report");	
		}

		//Render the report
		try {
			List<String> allowedGroups = new ArrayList<String>();
			allowedGroups.add("002001");
			allowedGroups.add("002002");
			Report r = reportingDAO.generateTrendReport(reportId, dsId, allowedGroups);
			System.out.println("Report title is: "+r.getTitle());

			if (sendEmails) {
				emailReport(r, "Test All Groups");
			}
			else {
				saveReport(r, "TestAllGroups");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't render the report");
		}
	}


	public void testGenerateCombinedReport() {

		DataSet ds = null;
		dsId = new Long(1);

		try {
			ds = dao.getDataSet(dsId).toHibernate();
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't retrieve dataset: "+ e.getMessage());
		}
		ITrendsReport report = null;
		try {
			report = panssTrendsInEastAngliaReport(ds);
			report.setTitle(report.getTitle()+"-Combined");
			ITrendsReport treatments = panssTreatmentsReport(ds, null, "all");

			report.addChart(treatments.getChart(0));	//add the treatments onto the general panss trends report
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't create report");	
		}

		ReportsClient client = new ReportsClient();

		Long reportId = null;
		try {
			reportId = client.saveReport(report, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't save report");	
		}

		//Render the report
		try {
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
			Report r = reportingDAO.generateTrendReport(reportId, dsId, allowedGroups); 
			System.out.println("Report title is: "+r.getTitle());

			if (sendEmails) {
				emailReport(r, "Test Combined");
			}
			else {
				saveReport(r, "TestCombined");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't render the report");
		}
	}


	private ITrendsReport createReport(DataSet ds, List<Group> groups, String hub) {

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

		//Create a new trends report
		ITrendsReport panssReport = 
			reportFactory.createTrendsReport(ds, "PANSS - "+docOcc.getDisplayText() +" Trends");

		//Summary chart
		ITrendsChart summary = 
			reportFactory.createTrendsChart(org.psygrid.data.reporting.Chart.CHART_TIME_SERIES,
					"PANSS Summary ("+hub+")");
		//summary.addType(org.psygrid.data.reporting.Chart.CHART_TABLE);
		summary.setRangeAxisLabel("Total Scores");

		try {
			panssReport.addChart(summary);
		}
		catch (ReportException re) {
			re.printStackTrace();
			fail("Unable to add chart");
		}

		//Set chart to display the previous six months
		summary = setPreviousSixMonths(summary);

		//get the mean average of the values in the summary
		String summaryType = ITrendsChartRow.SUMMARY_TYPE_MEAN;

		try {
			if (groups != null) {
				for (Group g: groups) {
					summary.addGroup(g);
				}
			}
		}
		catch (ReportException re) {
			re.printStackTrace();
			fail("Problem when adding groups to chart");
		}

		ITrendsChartRow posRow = reportFactory.createTrendsChartRow(); 
		posRow.setLabel("Positive");
		posRow.setSummaryType(summaryType);
		ITrendsChartRow negRow = reportFactory.createTrendsChartRow(); 
		negRow.setLabel("Negative");
		negRow.setSummaryType(summaryType);
		ITrendsChartRow genRow = reportFactory.createTrendsChartRow(); 
		genRow.setLabel("General");
		genRow.setSummaryType(summaryType);

		try {
			summary.addRow(posRow);
			summary.addRow(negRow);
			summary.addRow(genRow);

			ISimpleChartItem posTotal = reportFactory.createSimpleChartItem(panss.getEntry(8), docOcc, posSecOcc);
			posRow.addSeries(posTotal);
			ISimpleChartItem negTotal = reportFactory.createSimpleChartItem(panss.getEntry(16), docOcc, negSecOcc);
			negRow.addSeries(negTotal);
			ISimpleChartItem genTotal = reportFactory.createSimpleChartItem(panss.getEntry(34), docOcc, genSecOcc);
			genRow.addSeries(genTotal);
		}
		catch (ReportException re) {
			re.printStackTrace();
			fail("Unable to add rows/series to chart");
		}

		return panssReport;

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
		Calendar startDate = new GregorianCalendar(curDate.get(Calendar.YEAR), curDate.get(Calendar.MONTH) + 1 - 6, 0);
		Calendar endDate   = new GregorianCalendar(curDate.get(Calendar.YEAR), curDate.get(Calendar.MONTH) + 1, 0);

		chart.setTimePeriod(startDate, endDate);
		return chart;
	}


	private ITrendsReport panssTrendsInBristolAvonReport(DataSet ds) throws ReportException {
		List<Group> groups = new ArrayList<Group>();
		groups.add(ds.getGroup(0));

		return createReport(ds, groups, "Bristol Avon Hub");
	}

	private ITrendsReport panssTrendsInEastAngliaReport(DataSet ds) throws ReportException {
		List<Group> groups = new ArrayList<Group>();
		groups.add(ds.getGroup(1));
		groups.add(ds.getGroup(2));
		groups.add(ds.getGroup(11));

		return createReport(ds, groups, "East Anglia Hub");
	}

	private ITrendsReport panssTrendsInEastMidlandsReport(DataSet ds) throws ReportException {
		List<Group> groups = new ArrayList<Group>();
		groups.add(ds.getGroup(3));
		groups.add(ds.getGroup(4));

		return createReport(ds, groups, "East Midlands Hub");
	}

	private ITrendsReport panssTrendsInNorthEastReport(DataSet ds) throws ReportException {

		List<Group> groups = new ArrayList<Group>();
		groups.add(ds.getGroup(5));

		return createReport(ds, groups, "North East Hub");
	}

	private ITrendsReport panssTrendsInNorthLondonReport(DataSet ds) throws ReportException {
		List<Group> groups = new ArrayList<Group>();
		groups.add(ds.getGroup(6));
		groups.add(ds.getGroup(7));

		return createReport(ds, groups, "North London Hub");
	}

	private ITrendsReport panssTrendsInNorthWestReport(DataSet ds) throws ReportException {
		List<Group> groups = new ArrayList<Group>();
		groups.add(ds.getGroup(8));

		return createReport(ds, groups, "North West Hub");
	}

	private ITrendsReport panssTrendsInSouthLondonReport(DataSet ds) throws ReportException {
		List<Group> groups = new ArrayList<Group>();
		groups.add(ds.getGroup(9));

		return createReport(ds, groups, "South London Hub");
	}

	private ITrendsReport panssInWestMidlandsReport(DataSet ds) throws ReportException {
		List<Group> groups = new ArrayList<Group>();
		groups.add(ds.getGroup(10));

		return createReport(ds, groups, "West Midlands Hub");
	}

	/**
	 * Create a treatment chart to sit underneath the panss summary
	 */
	private ITrendsReport panssTreatmentsReport(DataSet ds, List<Group> groups, String title) throws ReportException {


		Document treatmentDoc = ds.getDocument(0);
		//check that we have the right document!
		if ( !treatmentDoc.getName().equals("TreatmentDocumentation") ){
			throw new ReportException("This isn't the treatment documentation document!!!");
		}

		//Assuming first document only is to be used 
		DocumentOccurrence treatmentDocOcc = treatmentDoc.getOccurrence(0);
		Section treatmentSec = treatmentDoc.getSection(1);
		SectionOccurrence treatmentSecOcc = treatmentSec.getOccurrence(0);

		//Create a new report
		ITrendsReport treatmentReport = reportFactory.createTrendsReport(ds, "Treatment types - "+treatmentDocOcc.getDisplayText() +" Report");

		ITrendsGanttChart treatment = 
			reportFactory.createTrendsGanttChart(org.psygrid.data.reporting.Chart.CHART_GANTT,
			"Treatments");

		treatmentReport.addChart(treatment);

		//Set chart to the same time period as the panss summary chart
		//treatment = (ITrendsGanttChart)setPreviousSixMonths(treatment);

		//TEST
		Calendar startDate = new GregorianCalendar(2006, Calendar.JUNE, 0);
		Calendar endDate = new GregorianCalendar(2006, Calendar.DECEMBER, 0);
		treatment.setTimePeriod(startDate, endDate);


		//String summaryType = ITrendsGanttChart.SUMMARY_TYPE_GANTT;	

		if (groups != null) {
			for (Group g: groups) {
				treatment.addGroup(g);
			}
		}

		//the selection of possible types of treatment (in entry 1) 
		List<String> treatmentTypes = new ArrayList<String>();
		OptionEntry optionEntry = (OptionEntry)treatmentDoc.getEntry(1);

		for (int i=0; i < optionEntry.numOptions(); i++) {
			treatmentTypes.add(optionEntry.getOption(i).getDisplayText());
		}

		/*
		 * Each TrendsChartRow must have three SimpleChartItems.
		 * The first specifying the thing to be searched for (which will
		 * form the individual rows of the final chart) and the remaining two 
		 * pointing to the start and end dates for it.
		 * 
		 * Each TrendsChartRow represents a possible answer to the question
		 * specified in the first SimpleChartItem and its label must be
		 * set to this answer. 
		 */
		for (String answer: treatmentTypes) {
			ITrendsChartRow pRow = reportFactory.createTrendsChartRow(); 
			treatment.addRow(pRow);
			pRow.setLabel(answer);

			ISimpleChartItem type = reportFactory.createSimpleChartItem(treatmentDoc.getEntry(1), treatmentDocOcc, treatmentSecOcc);
			pRow.addSeries(type);

			ISimpleChartItem start = reportFactory.createSimpleChartItem(treatmentDoc.getEntry(4), treatmentDocOcc, treatmentSecOcc);
			pRow.addSeries(start);

			ISimpleChartItem end = reportFactory.createSimpleChartItem(treatmentDoc.getEntry(5), treatmentDocOcc, treatmentSecOcc);
			pRow.addSeries(end);		
		}

		return treatmentReport;

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
}

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
import org.psygrid.data.reporting.definition.ITrendsReport;
import org.psygrid.data.reporting.definition.ReportException;
import org.psygrid.data.reporting.renderer.PdfRenderer;
import org.psygrid.data.repository.dao.RepositoryDAO;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

public class PathwaysTrendsReportTest extends DAOTest {

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
				saveReport(r, "Test All Groups");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't render the report");
		}

	}	

	private ITrendsReport createReport(DataSet ds, List<Group> groups, String hub) {
		Document pathways = ds.getDocument(11);
		//check that we have the right document!
		if ( !pathways.getName().equals("PathwaysToCare") ){
			fail("This isn't the pathways to care document!!!");
		}

		//Assuming baseline document only is to be used 
		DocumentOccurrence docOcc = pathways.getOccurrence(0);

		Section posSec = pathways.getSection(1);
		SectionOccurrence posSecOcc = posSec.getOccurrence(0);


		//Create a new trends report
		ITrendsReport pathwaysReport = 
			reportFactory.createTrendsReport(ds, "Collated Pathways to Care Report");
		//factory.createTrendsReport(ds, "Collated Pathways to Care Report ("+docOcc.getDisplayText() +")");

		//Summary chart
		ITrendsChart summary = 
			reportFactory.createTrendsChart(org.psygrid.data.reporting.Chart.CHART_STACKED_BAR,
					"Collated Pathways to Care ("+hub+")");
		summary.addType(org.psygrid.data.reporting.Chart.CHART_TABLE);
		summary.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
		summary.addType(org.psygrid.data.reporting.Chart.CHART_BAR_HZ);
		summary.addType(org.psygrid.data.reporting.Chart.CHART_LINE);
		summary.addType(org.psygrid.data.reporting.Chart.CHART_LINE_HZ);
		summary.addType(org.psygrid.data.reporting.Chart.CHART_PIE);
		summary.addType(org.psygrid.data.reporting.Chart.CHART_TIME_SERIES);
		summary.setRangeAxisLabel("Number of people");
		try {
			pathwaysReport.addChart(summary);
		}
		catch (ReportException re) {
			re.printStackTrace();
			fail("Unable to add chart to report");
		}

		//Set chart to display the previous six months
		summary = setPreviousSixMonths(summary);

		//get the total numbers of people taking each pathway
		String summaryType = ITrendsChartRow.SUMMARY_TYPE_COLLATE;

		try {
			if (groups != null) {
				for (Group g: groups) {
					summary.addGroup(g);
				}
			}
		}
		catch (ReportException re) {
			re.printStackTrace();
			fail("Unable to add chart to report");
		}

		//the selection of possible answers to the question (entry 4) 'Who was contacted?'
		List<String> posAnswers = new ArrayList<String>();
		OptionEntry optionEntry = (OptionEntry)pathways.getEntry(4);

		for (int i=0; i < optionEntry.numOptions(); i++) {
			posAnswers.add(optionEntry.getOption(i).getDisplayText());
		}

		for (String answer: posAnswers) {
			ITrendsChartRow pRow = reportFactory.createTrendsChartRow(); 
			pRow.setLabel(answer);
			pRow.setSummaryType(summaryType);
			ISimpleChartItem pathwayType = reportFactory.createSimpleChartItem(pathways.getEntry(4), docOcc, posSecOcc);

			try {
				pRow.addSeries(pathwayType);
				summary.addRow(pRow);
			}
			catch (ReportException re) {
				re.printStackTrace();
				fail("Unable to add chart to report");
			}
		}

		System.out.println("Entry is: "+pathways.getEntry(4).getDisplayText());

		return pathwaysReport;

	}


	private ITrendsReport pathwaysTrendsInEastAngliaReport(DataSet ds) throws ReportException {
		List<Group> groups = new ArrayList<Group>();
		groups.add(ds.getGroup(1));
		groups.add(ds.getGroup(2));
		groups.add(ds.getGroup(11));

		return createReport(ds, groups, "East Anglia Hub");
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
			FileOutputStream fos = new FileOutputStream("Pathways Trends Test "+reportCounter+" "+r.getTitle()+".pdf");
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
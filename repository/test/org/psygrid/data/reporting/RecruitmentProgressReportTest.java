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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.mail.internet.MimeMessage;

import org.psygrid.data.dao.DAOTest;
import org.psygrid.data.dao.DAOTestHelper;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.reporting.client.ReportsClient;
import org.psygrid.data.reporting.definition.IManagementReport;
import org.psygrid.data.reporting.definition.IRecruitmentProgressChart;
import org.psygrid.data.reporting.definition.IReport;
import org.psygrid.data.reporting.renderer.PdfRenderer;
import org.psygrid.data.repository.dao.RepositoryDAO;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

public class RecruitmentProgressReportTest extends DAOTest {

	private RepositoryDAO dao = null;

	private ReportingDAO reportingDAO = null;

	private Factory factory = null;

	final static private boolean sendEmails = true;
	
	private org.psygrid.data.reporting.definition.Factory reportFactory = null;

	
	
	private static final Long datasetId = new Long(21921); 
	
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

	public void testSaveReport() {

		IManagementReport report = createReport();

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
			reportsList = client.getAllReportsByDataSet(datasetId, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't save report ");
		}

		IManagementReport savedReport = null;
		for (IReport r: reportsList) {
			if (r.getTitle().equals(report.getTitle())) {
				//assume they are the same report
				savedReport = (IManagementReport)r;
				break;
			}
		}
		

		//assertions
		try {
			assertNotNull(savedReport);
			assertNotNull(savedReport.getGroup(0));
			assertNotNull(savedReport.getGroup(1));
			assertNotNull(savedReport.getGroup(2));
			assertNotNull(savedReport.getChart(0));
			IRecruitmentProgressChart c = (IRecruitmentProgressChart)savedReport.getChart(0);
			assertNotNull(c.getTitle());
			assertNotNull(c.getType(0));
			assertNotNull(c.getStartDate());
			assertNotNull(c.getEndDate());
			assertNotNull(c.getTargets());
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Problem with assertions");
		}
	}

	public void testGenerateReport_twogroups() {

		Long dsId = datasetId;
		
		IManagementReport report = createReport_twoGroups();

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
			reportsList = client.getAllReportsByDataSet(datasetId, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't save report ");
		}

		IManagementReport savedReport = null;
		for (IReport r: reportsList) {
			if (r.getTitle().equals(report.getTitle())) {
				//assume they are the same report
				savedReport = (IManagementReport)r;
				break;
			}
		}
		

		//assertion that the report has been saved correctly
		try {
			assertNotNull(savedReport);
			assertNotNull(savedReport.getGroup(0));
			assertNotNull(savedReport.getGroup(1));
			//assertNotNull(savedReport.getGroup(2));
			assertNotNull(savedReport.getChart(0));
			IRecruitmentProgressChart c = (IRecruitmentProgressChart)savedReport.getChart(0);
			assertNotNull(c.getTitle());
			assertNotNull(c.getType(0));
			assertNotNull(c.getStartDate());
			assertNotNull(c.getEndDate());
			assertNotNull(c.getTargets());
			
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Problem with assertions");
		}
		
		try {
	           
            List<ManagementReport> reportList = reportingDAO.generateMgmtReportsForDataSet(dsId, new Date(), "NoUser", null);
            assertEquals("Wrong number of reports found. ", 1, reportList.size());
            //Render the report 
            ManagementReport r = reportList.get(0);
            System.out.println("Report title is: "+r.getTitle());
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            PdfRenderer renderer = new PdfRenderer();
            renderer.render(r, os);
            
            //Email
            JavaMailSenderImpl sender = new JavaMailSenderImpl();
            sender.setHost("echobase.smb.man.ac.uk");
            
            MimeMessage message = sender.createMimeMessage();

            //use the true flag to indicate you need a multipart message
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo("lucy.bridges@manchester.ac.uk");
            helper.setFrom("lucy.bridges@manchester.ac.uk");
            helper.setSubject("Test Recruitment Progress");
            helper.setSentDate(new Date());

            //use the true flag to indicate the text included is HTML
            helper.setText(
                    "<html><body><p>Test email of recruitment progress report (hopefully!)</p></body></html>", true);

            InputStreamSource src = new ByteArrayResource(os.toByteArray());
            helper.addAttachment("test.pdf", src);

            if (sendEmails) {
            	sender.send(message);
            }
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't generate/send report ");
		}
		
	}
	
	public void testGenerateReport() {

		//Setup and save the report
		IManagementReport report = createReport();

		ReportsClient client = new ReportsClient();
		Long reportId = null;
		try {
			reportId = client.saveReport(report, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't save report ");
		}

		assertNotNull("report id is null", reportId);
		//Generate the report and email the output
		try {
           
            List<ManagementReport> reportList = reportingDAO.generateMgmtReportsForDataSet(datasetId, new Date(), "NoUser", null);
            assertEquals("Wrong number of reports found. ", 1, reportList.size());
            //Render the report 
            ManagementReport r = reportList.get(0);
            System.out.println("Report title is: "+r.getTitle());
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            PdfRenderer renderer = new PdfRenderer();
            renderer.render(r, os);
            
            //Email
            JavaMailSenderImpl sender = new JavaMailSenderImpl();
            sender.setHost("echobase.smb.man.ac.uk");
            
            MimeMessage message = sender.createMimeMessage();

            //use the true flag to indicate you need a multipart message
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo("lucy.bridges@manchester.ac.uk");
            helper.setFrom("lucy.bridges@manchester.ac.uk");
            helper.setSubject("Test Recruitment Progress");
            helper.setSentDate(new Date());

            //use the true flag to indicate the text included is HTML
            helper.setText(
                    "<html><body><p>Test email of recruitment progress report (hopefully!)</p></body></html>", true);

            InputStreamSource src = new ByteArrayResource(os.toByteArray());
            helper.addAttachment("test.pdf", src);

            if (sendEmails) {
            	sender.send(message);
            }
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't generate/send report ");
		}
		
		
	}

	public void testGenerateReport_incorrectDates() {

		//Setup and save the report
		IManagementReport report = createReport_incorrectDates();

		ReportsClient client = new ReportsClient();
		Long reportId = null;
		try {
			reportId = client.saveReport(report, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't save report ");
		}

		assertNotNull("report id is null", reportId);
		//Generate the report and email the output
		try {
           
            List<ManagementReport> reportList = reportingDAO.generateMgmtReportsForDataSet(datasetId, new Date(), "NoUser", null);
            assertEquals("Wrong number of reports found. ", 1, reportList.size());
            //Render the report 
            ManagementReport r = reportList.get(0);
            System.out.println("Report title is: "+r.getTitle());
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            PdfRenderer renderer = new PdfRenderer();
            renderer.render(r, os);
            
            //Email
            JavaMailSenderImpl sender = new JavaMailSenderImpl();
            sender.setHost("echobase.smb.man.ac.uk");
            
            MimeMessage message = sender.createMimeMessage();

            //use the true flag to indicate you need a multipart message
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo("lucy.bridges@manchester.ac.uk");
            helper.setFrom("lucy.bridges@manchester.ac.uk");
            helper.setSubject("Test Recruitment Progress");
            helper.setSentDate(new Date());

            //use the true flag to indicate the text included is HTML
            helper.setText(
                    "<html><body><p>Test email of recruitment progress report (hopefully!)</p></body></html>", true);

            InputStreamSource src = new ByteArrayResource(os.toByteArray());
            helper.addAttachment("test.pdf", src);

            if (sendEmails) {
            	sender.send(message);
            }
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't generate/send report ");
		}
		
		
	}
	
	/**
	 * Create and email a recruitment progress report for the
	 * Outlook dataset.
	 */
	public void testGenerateOutlookReport() {

		//Setup and save the report
		IManagementReport report = existingReport(); 

		ReportsClient client = new ReportsClient();
		Long reportId = null;
		try {
			reportId = client.saveReport(report, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't save report ");
		}

		assertNotNull("report id is null", reportId);
		//Generate the report and email the output
		try {
           
            List<ManagementReport> reportList = reportingDAO.generateMgmtReportsForDataSet(datasetId, new Date(), "NoUser", null);

            //Render the report 
            ManagementReport r = reportList.get(reportList.size()-1);
            System.out.println("Report title is: "+r.getTitle());
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            PdfRenderer renderer = new PdfRenderer();
            renderer.render(r, os);
            
            //Email
            JavaMailSenderImpl sender = new JavaMailSenderImpl();
            sender.setHost("echobase.smb.man.ac.uk");
            
            MimeMessage message = sender.createMimeMessage();

            //use the true flag to indicate you need a multipart message
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo("lucy.bridges@manchester.ac.uk");
            helper.setFrom("lucy.bridges@manchester.ac.uk");
            helper.setSubject("Test Recruitment Progress");
            helper.setSentDate(new Date());

            //use the true flag to indicate the text included is HTML
            helper.setText(
                    "<html><body><p>Test email of recruitment progress report (hopefully!)</p></body></html>", true);

            InputStreamSource src = new ByteArrayResource(os.toByteArray());
            helper.addAttachment("test.pdf", src);

            if (sendEmails) {
            	sender.send(message);
            }
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't generate/send report ");
		}	
	}

	private DataSet createDataset() {
		String name = "testRecruitmentProgressReport - "+(new Date()).toString();
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

		Long dsId = null;

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
	
	
	private IManagementReport createReport() {

		DataSet ds = createDataset();

		//create the report
		IManagementReport report = reportFactory.createManagementReport(ds, "Test Recruitment Progress Report");
		
		try {
			report.addGroup(ds.getGroup(0));		//2 results
			report.addGroup(ds.getGroup(1));		//0 results
			report.addGroup(ds.getGroup(2));		//1 result
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't add groups to the report: "+ e.getMessage());
		}
		
		IRecruitmentProgressChart chart = reportFactory.createRecruitmentProgressChart(Chart.CHART_TIME_SERIES, "Test Recruitment Progress Chart");
		//chart.addType(Chart.CHART_TABLE);

		Calendar startDate = new GregorianCalendar(2006, 8, 0);
		Calendar endDate   = new GregorianCalendar(2006, 12, 0);
		
		chart.setTimePeriod(startDate, endDate);
		chart.setRangeAxisLabel("Number of Clients");
		//create recruitment targets for each month
		Calendar a = new GregorianCalendar(2006, 8, 0);
		chart.addTarget(a, new Integer(8));		//expecting to recruit 8 people in August
		Calendar b = new GregorianCalendar(2006, 9, 0);
		chart.addTarget(b, new Integer(9));
		Calendar c = new GregorianCalendar(2006, 10, 0);
		chart.addTarget(c, new Integer(0));
		Calendar d = new GregorianCalendar(2006, 11, 0);
		chart.addTarget(d, new Integer(11));
		Calendar e = new GregorianCalendar(2006, 12, 0);
		chart.addTarget(e, new Integer(12));
		
		
		
		try {
			report.addManagementChart(chart);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Couldn't add chart to the report ");
		}

		return report;
	}
	
	private IManagementReport createReport_twoGroups() {

		DataSet ds = createDataset();

		//create the report
		IManagementReport report = reportFactory.createManagementReport(ds, "Test Recruitment Progress Report");
		
		try {
			//test for restriction by groups, by leaving off the final group
			report.addGroup(ds.getGroup(0));		//2 results
			report.addGroup(ds.getGroup(1));		//0 results
			//report.addGroup(ds.getGroup(2));		//1 result
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't add groups to the report: "+ e.getMessage());
		}
		
		IRecruitmentProgressChart chart = reportFactory.createRecruitmentProgressChart(Chart.CHART_TIME_SERIES, "Test Recruitment Progress Chart");
		//chart.addType(Chart.CHART_TABLE);
		
		Calendar startDate = new GregorianCalendar(2006, 8, 0);
		Calendar endDate   = new GregorianCalendar(2006, 12, 0);
		
		chart.setTimePeriod(startDate, endDate);
		chart.setRangeAxisLabel("Number of Clients");
		//create the recruitment targets for each month
		Calendar a = new GregorianCalendar(2006, 8, 0);
		chart.addTarget(a, new Integer(8));		//expecting to recruit 8 people in August
		Calendar b = new GregorianCalendar(2006, 9, 0);
		chart.addTarget(b, new Integer(9));
		Calendar c = new GregorianCalendar(2006, 10, 0);
		chart.addTarget(c, new Integer(0));
		Calendar d = new GregorianCalendar(2006, 11, 0);
		chart.addTarget(d, new Integer(11));
		Calendar e = new GregorianCalendar(2006, 12, 0);
		chart.addTarget(e, new Integer(12));
		
		try {
			report.addManagementChart(chart);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Couldn't add chart to the report ");
		}

		return report;
	}

	/**
	 * Test that a date range outside of the retrieved values
	 * is displayed correctly
	 * 
	 * @return report
	 */
	private IManagementReport createReport_incorrectDates() {

		DataSet ds = createDataset();

		//create the report
		IManagementReport report = reportFactory.createManagementReport(ds, "Test Recruitment Progress Report");
		
		try {
			report.addGroup(ds.getGroup(0));		//2 results
			report.addGroup(ds.getGroup(1));		//0 results
			report.addGroup(ds.getGroup(2));		//1 result
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't add groups to the report: "+ e.getMessage());
		}
		
		IRecruitmentProgressChart chart = reportFactory.createRecruitmentProgressChart(Chart.CHART_TIME_SERIES, "Test Recruitment Progress Chart");
		//chart.addType(Chart.CHART_TABLE);

		//date range is before any of the created records
		Calendar startDate = new GregorianCalendar(2006, 7, 0);
		Calendar endDate   = new GregorianCalendar(2006, 9, 0);
		
		chart.setTimePeriod(startDate, endDate);
		chart.setRangeAxisLabel("Number of Clients");
		//create recruitment targets for each month
		Calendar a = new GregorianCalendar(2006, 8, 0);
		chart.addTarget(a, new Integer(8));		//expecting to recruit 8 people in August
		Calendar b = new GregorianCalendar(2006, 9, 0);
		chart.addTarget(b, new Integer(9));
		Calendar c = new GregorianCalendar(2006, 10, 0);
		chart.addTarget(c, new Integer(0));
		Calendar d = new GregorianCalendar(2006, 11, 0);
		chart.addTarget(d, new Integer(11));
		Calendar e = new GregorianCalendar(2006, 12, 0);
		chart.addTarget(e, new Integer(12));
		
		try {
			report.addManagementChart(chart);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Couldn't add chart to the report ");
		}

		return report;
	}
	
	private IManagementReport existingReport() {

		//String name = "testRecruitmentProgressReport - "+(new Date()).toString();
		DataSet ds = null;
		Long dsId = datasetId;

		try {
			ds = dao.getDataSet(dsId).toHibernate();
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't retrieve dataset: "+ e.getMessage());
		}



		//create the report
		IManagementReport report = reportFactory.createManagementReport(ds, "Test Recruitment Progress Report 25");
		
		try {
			report.addGroup(ds.getGroup(0));
			report.addGroup(ds.getGroup(1));
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't add groups to the report: "+ e.getMessage());
		}
		
		IRecruitmentProgressChart chart = reportFactory.createRecruitmentProgressChart(Chart.CHART_TIME_SERIES, "Test Recruitment Progress Chart 25");
		chart.addType(Chart.CHART_TABLE);

		Calendar startDate = new GregorianCalendar(2006, 8, 0);
		Calendar endDate   = new GregorianCalendar(2006, 12, 0);
		
		chart.setTimePeriod(startDate, endDate);
		chart.setRangeAxisLabel("Number of Clients");

		//create recruitment targets for each month
		int target = 10;		//expecting to recruit 10 people per month
		for (int i = 1; i < 16; i++) {
			int month = endDate.get(Calendar.MONTH) -1 - i;		//Calendar class will automatically take into account changes in the year
			int year  = endDate.get(Calendar.YEAR);
			
			Calendar cal = new GregorianCalendar(year, month, 0);
			
			chart.addTarget(cal, target);
		}
		
		try {
			report.addManagementChart(chart);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Couldn't add chart to the report ");
		}

		return report;
	}
	
	
	public void testCreateRecords() {

		String name = "testCreateRecords - "+(new Date()).toString();
		DataSet ds = null;
		Long dsId   = new Long(datasetId);
		
		int noOfRecords = 1;
		String group    = "001001";
		
		try {
			ds = dao.getDataSet(dsId).toHibernate();
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't retrieve dataset: "+ e.getMessage());
		}
		
		//populate the dataset
		try {
			//generate identifiers...
			Identifier[] ids = DAOTestHelper.getIdentifiers(dao, ds.getProjectCode(), dsId, noOfRecords, group);
			for (Identifier i: ids) {
				//create a couple of records
				Record r1 = (Record)ds.generateInstance();
				r1.setIdentifier(i);
				//Should be sufficient for neden
				r1.addConsent(ds.getAllConsentFormGroup(0).getConsentForm(0).generateConsent());
				r1.addConsent(ds.getAllConsentFormGroup(0).getConsentForm(1).generateConsent());
				r1.addConsent(ds.getAllConsentFormGroup(0).getConsentForm(1).getAssociatedConsentForm(0).generateConsent());
				r1.setStatus(ds.getStatus(3));
				Document doc = ds.getDocument(0);
				DocumentInstance inst = (DocumentInstance)doc.generateInstance(doc.getOccurrence(0));
				inst.setStatus(new Status("Pending", "Pending Approval", 12));
				
				if (r1.checkConsent(inst) ) {
					System.out.println("There is consent!");
				}
				
				r1.addDocumentInstance(inst);
				dao.saveRecord(r1.toDTO(), true, null, "NoUser");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't save/retrieve dataset: "+ e.getMessage());
		}
	}

}

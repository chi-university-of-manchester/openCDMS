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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.psygrid.data.dao.DAOTest;
import org.psygrid.data.model.dto.RecordDTO;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.reporting.client.ReportsClient;
import org.psygrid.data.reporting.definition.Factory;
import org.psygrid.data.reporting.definition.IGroupsSummaryChart;
import org.psygrid.data.reporting.definition.IManagementReport;
import org.psygrid.data.reporting.definition.IProjectSummaryChart;
import org.psygrid.data.reporting.definition.IRecordChart;
import org.psygrid.data.reporting.definition.IRecordReport;
import org.psygrid.data.reporting.definition.IRecruitmentProgressChart;
import org.psygrid.data.reporting.definition.ISimpleChartItem;
import org.psygrid.data.reporting.definition.ISimpleChartRow;
import org.psygrid.data.reporting.definition.ITrendsChart;
import org.psygrid.data.reporting.definition.ITrendsChartRow;
import org.psygrid.data.reporting.definition.IUKCRNSummaryChart;
import org.psygrid.data.reporting.definition.IUserSummaryChart;
import org.psygrid.data.reporting.definition.hibernate.TrendsReport;
import org.psygrid.data.repository.dao.RepositoryDAO;

public class DynamicGenerationTest extends DAOTest {

	private RepositoryDAO dao = null;

	private ReportingDAO reportingDAO = null;

	private Factory factory = null;

	/**
	 * Select whether to use the ReportsClient web services
	 * and therefore the security or to use the ReportingDAO
	 * to bypass it.
	 */
	private final boolean useSecurity = false;
	
	/**
	 * Specify the groups permitted to be accessed when viewing
	 * reports. This is normally retrieved from a user's SAML
	 * assertion.
	 */
	private List<String> allowedGroups = new ArrayList<String>();
	
	private Long datasetId = new Long(5);
	
	protected void setUp() throws Exception {
		super.setUp();
		dao = (RepositoryDAO)ctx.getBean("repositoryDAO");
		reportingDAO = (ReportingDAO)ctx.getBean("reportingDAO");
		factory = (Factory) ctx.getBean("reportFactory");
		
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
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		dao = null;
		factory = null;
	}

	/**
	 * Generate a Record Report for the GAF document,
	 * without saving the report definition. The
	 * report output is saved to the filesystem as 
	 * pdf and xls documents.
	 */
	public void testGafGenerateRecordReport() {
		//create pdf and xls
		Long dsId = new Long(datasetId);
		DataSet ds = null;

		try {
			ds = dao.getDataSet(dsId).toHibernate();
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		Document gaf = ds.getDocument(8);
		//check that we have the right document!
		if ( !gaf.getName().equals("GAF Data Entry Sheet") ){
			fail("This isn't the GAF document!!!");
		}
		DocumentOccurrence docOcc = gaf.getOccurrence(0);

		IRecordReport gafReport = 
			factory.createRecordReport(ds, "Gaf Record Report - "+docOcc.getDisplayText());

		Section mainSec = gaf.getSection(0);
		SectionOccurrence mainSecOcc = mainSec.getOccurrence(0);

		try {
			RecordDTO record = dao.getRecord("EDT/001001-1", RetrieveDepth.RS_MINIMUM);
			gafReport.setRecord(record.toHibernate());
			System.out.println("Record id: "+record.getId()+" and identifier is: "+record.getIdentifier().getIdentifier());
		}
		catch(Exception e) {
			e.printStackTrace();
			fail("Problem retrieving record");
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
					e.printStackTrace();
					fail();
				}

			}
			catch (Exception e) {
				e.printStackTrace();
				fail("Exception: "+e);
			}
		}


		try {
			byte[] output  = null;
			byte[] output2 = null;
			if (useSecurity) {
				ReportsClient client = new ReportsClient();
				output = client.generateDynamicRecordReport(gafReport, "OLK/001001-1", "pdf", null);
				output2 = client.generateDynamicRecordReport(gafReport, "OLK/001001-1", "xls", null);
			}
			else {
				output = reportingDAO.generateDynamicReport(gafReport.toDTO(), "pdf", allowedGroups, null, "NoUser");
				output2 = reportingDAO.generateDynamicReport(gafReport.toDTO(), "xls", allowedGroups, null, "NoUser");
			}

			FileOutputStream fos = new FileOutputStream(gafReport.getTitle()+"-DynamicTest.pdf");
			fos.write(output);

			FileOutputStream fos2 = new FileOutputStream(gafReport.getTitle()+"-DynamicTest.xls");
			fos2.write(output2);

		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e);
		}
	}

	/**
	 * Generate a Record Report for the Premorbid
	 * document, without saving the report definition. 
	 * The report output is saved to the filesystem as 
	 * pdf and xls documents.
	 *
	 */
	public void testPremorbidGenerateRecordReport() {


		Long dsId = new Long(datasetId);
		DataSet ds = null;

		try {
			ds = dao.getDataSet(dsId).toHibernate();
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		Document pas = ds.getDocument(12);
		DocumentOccurrence docOcc = pas.getOccurrence(0);;

		//create a report detailing the premorbid
		//adjustment scale for an individual
		IRecordReport pasReport = 
			factory.createRecordReport(ds, "Premorbid Record Report - "+docOcc.getDisplayText());


		//check that we have the right document!
		if ( !pas.getName().equals("Premorbid Summary Sheet") ){
			fail("This isn't the Premorbid document!!");
		}

		//retrieve the section containing the adjusted scores
		Section posSec = pas.getSection(2);
		SectionOccurrence adjustedSecOcc = posSec.getOccurrence(0);

		try {
			RecordDTO record = dao.getRecord("OLK/002001-1", RetrieveDepth.RS_MINIMUM);
			pasReport.setRecord(record.toHibernate());
			System.out.println("Record id: "+record.getId()+" and identifier is: "+record.getIdentifier().getIdentifier());
		}
		catch(Exception e) {
			e.printStackTrace();
			fail("Problem retrieving record");
		}

		//test all types of chart
		String[] types = new String[7];
		types[0] = org.psygrid.data.reporting.Chart.CHART_TABLE;
		types[1] = org.psygrid.data.reporting.Chart.CHART_BAR;
		types[2] = org.psygrid.data.reporting.Chart.CHART_BAR_HZ;
		types[3] = org.psygrid.data.reporting.Chart.CHART_LINE;
		types[4] = org.psygrid.data.reporting.Chart.CHART_LINE_HZ;
		types[5] = org.psygrid.data.reporting.Chart.CHART_PIE;
		types[6] = org.psygrid.data.reporting.Chart.CHART_STACKED_BAR;



		//Scores chart

		//groups the following:
		// - childhood
		// - early adolescence
		// - late adolescence
		// - adulthood
		//
		//into the categories of:
		// - adaption to school
		// - peer relationships
		// - scholastic performance
		// - socialbility and withdrawal
		// - social sexual aspects of life

		for (String type: types) {

			IRecordChart chart =  
				factory.createSimpleChart(type,
						"Test - "+type);
			try {
				pasReport.addChart(chart);
				chart.setRangeAxisLabel("axisLabel");

				ISimpleChartRow childhood = new org.psygrid.data.reporting.definition.hibernate.SimpleChartRow(); 
				childhood.setLabel("Childhood");
				ISimpleChartRow earlyadolescence = new org.psygrid.data.reporting.definition.hibernate.SimpleChartRow(); 
				earlyadolescence.setLabel("Early Adolescence");
				ISimpleChartRow lateadolescence = new org.psygrid.data.reporting.definition.hibernate.SimpleChartRow(); 
				lateadolescence.setLabel("Late Adolescence");
				ISimpleChartRow adulthood = new org.psygrid.data.reporting.definition.hibernate.SimpleChartRow(); 
				adulthood.setLabel("Adulthood");

				chart.addRow(childhood);
				chart.addRow(earlyadolescence);
				chart.addRow(lateadolescence);
				chart.addRow(adulthood);

				//entry 0 = childhood
				//entry 5 = early adolescence
				//entry 11= late 
				//entry 17= adult
				//entry 21=general(stop)
				try {
					for (int i = 63; i < 67; i++) {
						ISimpleChartItem item = factory.createSimpleChartItem(
								pas.getEntry(i), docOcc, adjustedSecOcc);
						childhood.addSeries(item);
					}
					for (int i = 68; i < 73; i++) {
						ISimpleChartItem item = factory.createSimpleChartItem(
								pas.getEntry(i), docOcc, adjustedSecOcc);
						earlyadolescence.addSeries(item);
					}
					//get label from entry(64) (i.e late adolesence)
					for (int i = 74; i < 79; i++) {
						ISimpleChartItem item = factory.createSimpleChartItem(
								pas.getEntry(i), docOcc, adjustedSecOcc);
						lateadolescence.addSeries(item);
					}
					for (int i = 80; i < 83; i++) {
						ISimpleChartItem item = factory.createSimpleChartItem(
								pas.getEntry(i), docOcc, adjustedSecOcc);
						adulthood.addSeries(item);
					}
				}
				//(obs) if the entry doesn't exist continue anyway
				catch (Exception e) {
					e.printStackTrace();
					fail();
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				fail("Exception: "+e);
			}
		}

		try {	
			byte[] output  = null;
			byte[] output2 = null;
			if (useSecurity) {
				ReportsClient client = new ReportsClient();
				output = client.generateDynamicRecordReport(pasReport, "OLK/002001-1", "pdf", null);
				output2 = client.generateDynamicRecordReport(pasReport, "OLK/002001-1", "xls", null);
			}
			else {
				output = reportingDAO.generateDynamicReport(pasReport.toDTO(), "pdf", allowedGroups, null, "NoUser");
				output2 = reportingDAO.generateDynamicReport(pasReport.toDTO(), "xls", allowedGroups, null, "NoUser");
			}	

			FileOutputStream fos = new FileOutputStream(pasReport.getTitle()+"-DynamicTest.pdf");
			fos.write(output);

			FileOutputStream fos2 = new FileOutputStream(pasReport.getTitle()+"-DynamicTest.xls");
			fos2.write(output2);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e);
		}
	}

	/**
	 * Generate a Trends Report for the PANSS document, 
	 * without saving the report definition. The report 
	 * output is saved to the filesystem as pdf and xls 
	 * documents.
	 */
	public void testPANSSGenerateTrendReport() {


		Long dsId = new Long(datasetId);
		DataSet ds = null;

		try {
			ds = dao.getDataSet(dsId).toHibernate();
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		TrendsReport panssReport = (TrendsReport)factory.createTrendsReport(ds, "PANSS Trends Report");


		Document panss = ds.getDocument(6);
		//Check that we have the right document!
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

		//panssReport.setDataSet((org.psygrid.data.model.hibernate.DataSet)ds);		

//		Summary chart
		ITrendsChart summary = 
			factory.createTrendsChart(org.psygrid.data.reporting.Chart.CHART_TIME_SERIES,
			"PANSS Summary (all)");
		summary.addType(org.psygrid.data.reporting.Chart.CHART_TABLE);
		summary.setRangeAxisLabel("Total Scores");
		try {
			panssReport.addChart(summary);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Problem adding the summary chart");
		}

		//Set chart to display the previous six months
		summary = setPreviousSixMonths(summary);

		//display a bar chart with the total cases per month on the same graph
		summary.setShowTotals(true);	

		//Get the mean average of the values in the summary
		String summaryType = ITrendsChartRow.SUMMARY_TYPE_MEAN;

		ITrendsChartRow posRow = factory.createTrendsChartRow(); 
		posRow.setLabel("Positive");
		posRow.setSummaryType(summaryType);
		ITrendsChartRow negRow = factory.createTrendsChartRow(); 
		negRow.setLabel("Negative");
		negRow.setSummaryType(summaryType);
		ITrendsChartRow genRow = factory.createTrendsChartRow(); 
		genRow.setLabel("General");
		genRow.setSummaryType(summaryType);

		try {
			summary.addRow(posRow);
			summary.addRow(negRow);
			summary.addRow(genRow);

			ISimpleChartItem posTotal = factory.createSimpleChartItem(panss.getEntry(8), docOcc, posSecOcc);
			posRow.addSeries(posTotal);
			ISimpleChartItem negTotal = factory.createSimpleChartItem(panss.getEntry(16), docOcc, negSecOcc);
			negRow.addSeries(negTotal);
			ISimpleChartItem genTotal = factory.createSimpleChartItem(panss.getEntry(34), docOcc, genSecOcc);
			genRow.addSeries(genTotal);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Problem adding rows or series");
		}

		try {
			byte[] output  = null;
			byte[] output2 = null;
			if (useSecurity) {
				ReportsClient client = new ReportsClient();
				output  = client.generateDynamicTrendsReport(panssReport,"pdf", null);
				output2 = client.generateDynamicTrendsReport(panssReport,"xls", null);
			}
			else {
				output  = reportingDAO.generateDynamicReport(panssReport.toDTO(), "pdf", allowedGroups, null, "NoUser");
				output2 = reportingDAO.generateDynamicReport(panssReport.toDTO(), "xls", allowedGroups, null, "NoUser");
			}	

			FileOutputStream fos = new FileOutputStream(panssReport.getTitle()+"-DynamicTest.pdf");
			fos.write(output);

			FileOutputStream fos2 = new FileOutputStream(panssReport.getTitle()+"-DynamicTest.xls");
			fos2.write(output2);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e);
		}
	}

	/**
	 * Generate a Management Report for recruitment progress, 
	 * without saving the report definition. The report 
	 * output is saved to the filesystem as pdf and xls 
	 * documents.
	 */
	public void testGenerateRecruitmentProgressReport() {

		Long dsId = new Long(datasetId);
		DataSet ds = null;

		try {
			ds = dao.getDataSet(dsId).toHibernate();
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		IManagementReport mReport = factory.createManagementReport(ds, "Recruitment Progress (All) - Management Report");
		//mReport.setRole("PrincipalInvestigator");
		mReport.setWithRawData(false);

		/* 
		 * Create a timeseries chart showing the number of subjects consented
		 * into the trial against the targets set for each month, giving
		 * a view of the trial's progress. 
		 */
		IRecruitmentProgressChart chart = factory.createRecruitmentProgressChart(Chart.CHART_TIME_SERIES, "Outlook - Recruitment Progress (All) Chart");

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
			//chart.setReport(mReport);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Unable to add chart");
		}

		try {
			byte[] output  = null;
			byte[] output2 = null;
			if (useSecurity) {
				ReportsClient client = new ReportsClient();
				output  = client.generateDynamicManagementReport(mReport, "pdf", null);
				output2 = client.generateDynamicManagementReport(mReport, "xls", null);
			}
			else {
				output  = reportingDAO.generateDynamicReport(mReport.toDTO(), "pdf", allowedGroups, null, "NoUser");
				output2 = reportingDAO.generateDynamicReport(mReport.toDTO(), "xls", allowedGroups, null, "NoUser");
			}	

			FileOutputStream fos = new FileOutputStream(mReport.getTitle()+"-DynamicTest.pdf");
			fos.write(output);

			FileOutputStream fos2 = new FileOutputStream(mReport.getTitle()+"-DynamicTest.xls");
			fos2.write(output2);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e);
		}
	}

	/**
	 * Generate a Management Report for recruitment progress
	 * within Bristol and Avon without saving the report 
	 * definition, which by default shouldn't show any results
	 * with the Outlook test dataset. The report output is 
	 * saved to the filesystem as pdf and xls documents.
	 */
	public void testGenerateRecruitmentProgressBristolAvonReport() {

		Long dsId = new Long(datasetId);
		DataSet ds = null;

		try {
			ds = dao.getDataSet(dsId).toHibernate();
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		IManagementReport mReport = factory.createManagementReport(ds, "Recruitment Progress (Bristol Avon) - Management Report");
		//mReport.setRole("PrincipalInvestigator");
		mReport.setWithRawData(false);

		/* 
		 * Create a timeseries chart showing the number of subjects consented
		 * into the trial against the targets set for each month, giving
		 * a view of the trial's progress. 
		 */
		IRecruitmentProgressChart chart = factory.createRecruitmentProgressChart(Chart.CHART_TIME_SERIES, "Outlook - Recruitment Progress (Bristol & Avon) Chart");

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
			chart.addGroup(ds.getGroup(0));
			mReport.addManagementChart(chart);
			//chart.setReport(mReport);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Unable to add chart");
		}

		try {	
			byte[] output  = null;
			byte[] output2 = null;
			if (useSecurity) {
				ReportsClient client = new ReportsClient();
				output  = client.generateDynamicManagementReport(mReport, "pdf", null);
				output2 = client.generateDynamicManagementReport(mReport, "xls", null);
			}
			else {
				output  = reportingDAO.generateDynamicReport(mReport.toDTO(), "pdf", allowedGroups, null, "NoUser");
				output2 = reportingDAO.generateDynamicReport(mReport.toDTO(), "xls", allowedGroups, null, "NoUser");
			}	

			FileOutputStream fos = new FileOutputStream(mReport.getTitle()+"-DynamicTest.pdf");
			fos.write(output);

			FileOutputStream fos2 = new FileOutputStream(mReport.getTitle()+"-DynamicTest.xls");
			fos2.write(output2);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e);
		}
	}

	/**
	 * Generate a Management Report for recruitment progress
	 * within East Anglia without saving the report 
	 * definition, which by default should show the same 
	 * results as the default recruitment progress report.
	 * The report output is saved to the filesystem as pdf 
	 * and xls documents.
	 */
	public void testGenerateRecruitmentProgressEastAngliaReport() {

		Long dsId = new Long(datasetId);
		DataSet ds = null;

		try {
			ds = dao.getDataSet(dsId).toHibernate();
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		IManagementReport mReport = factory.createManagementReport(ds, "Recruitment Progress (East Anglia) - Management Report");
		//mReport.setRole("PrincipalInvestigator");
		mReport.setWithRawData(false);

		/* 
		 * Create a timeseries chart showing the number of subjects consented
		 * into the trial against the targets set for each month, giving
		 * a view of the trial's progress. 
		 */
		IRecruitmentProgressChart chart = factory.createRecruitmentProgressChart(Chart.CHART_TIME_SERIES, "Outlook - Recruitment Progress (East Anglia) Chart");

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
			chart.addGroup(ds.getGroup(1));
			chart.addGroup(ds.getGroup(2));
			chart.addGroup(ds.getGroup(11));
			mReport.addManagementChart(chart);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Unable to add chart");
		}

		try {	
			byte[] output  = null;
			byte[] output2 = null;
			if (useSecurity) {
				ReportsClient client = new ReportsClient();
				output  = client.generateDynamicManagementReport(mReport, "pdf", null);
				output2 = client.generateDynamicManagementReport(mReport, "xls", null);
			}
			else {
				output  = reportingDAO.generateDynamicReport(mReport.toDTO(), "pdf", allowedGroups, null, "NoUser");
				output2 = reportingDAO.generateDynamicReport(mReport.toDTO(), "xls", allowedGroups, null, "NoUser");
			}	

			FileOutputStream fos = new FileOutputStream(mReport.getTitle()+"-DynamicTest.pdf");
			fos.write(output);

			FileOutputStream fos2 = new FileOutputStream(mReport.getTitle()+"-DynamicTest.xls");
			fos2.write(output2);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e);
		}
	}

	/**
	 * Generate a Management Report for a project summary, 
	 * without saving the report definition. The report 
	 * output is saved to the filesystem as pdf and xls 
	 * documents.
	 */
	public void testGenerateManagementReport() {

		Long dsId = new Long(datasetId);
		DataSet ds = null;

		try {
			ds = dao.getDataSet(dsId).toHibernate();
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		IManagementReport report = factory.createManagementReport(ds, "CI Management Report");
		//report.setRole("ChiefInvestigator");
		report.setWithRawData(false);

		IProjectSummaryChart chrt1 = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Whole Project");
		try {
			report.addManagementChart(chrt1);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Unable to add chart");
		}

		try {	
			byte[] output  = null;
			byte[] output2 = null;
			if (useSecurity) {
				ReportsClient client = new ReportsClient();
				output  = client.generateDynamicManagementReport(report, "pdf", null);
				output2 = client.generateDynamicManagementReport(report, "xls", null);
			}
			else {
				output  = reportingDAO.generateDynamicReport(report.toDTO(), "pdf", allowedGroups, null, "NoUser");
				output2 = reportingDAO.generateDynamicReport(report.toDTO(), "xls", allowedGroups, null, "NoUser");
			}	

			FileOutputStream fos = new FileOutputStream(report.getTitle()+"-DynamicTest.pdf");
			fos.write(output);

			FileOutputStream fos2 = new FileOutputStream(report.getTitle()+"-DynamicTest.xls");
			fos2.write(output2);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e);
		}
	}

	/**
	 * Generate a Management Report for the Bristol and Avon, 
	 * hub without saving the report definition. The report 
	 * output is saved to the filesystem as pdf and xls 
	 * documents.
	 * 
	 * This report would generally return no results as the 
	 * group is not normally used during testing.
	 */
	public void testGenerateBristolAvonManagementReport() {

		Long dsId = new Long(datasetId);
		DataSet ds = null;

		try {
			ds = dao.getDataSet(dsId).toHibernate();
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		IManagementReport report = factory.createManagementReport(ds, "PI (Bristol Avon Hub) Management Report");
		//report.setRole("PrincipalInvestigator");

		try {
			//Group 0 should return no results by default for the Outlook dataset
			report.addGroup(ds.getGroup(0));
			report.setWithRawData(false);

			IProjectSummaryChart chrt1 = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Whole Project");
			report.addManagementChart(chrt1);

			IGroupsSummaryChart chrt2 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Bristol Avon Hub");
			chrt2.addGroup(ds.getGroup(0));
			report.addManagementChart(chrt2);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Problem setting up the report");
		}

		try {	
			byte[] output  = null;
			byte[] output2 = null;
			if (useSecurity) {
				ReportsClient client = new ReportsClient();
				output  = client.generateDynamicManagementReport(report, "pdf", null);
				output2 = client.generateDynamicManagementReport(report, "xls", null);
			}
			else {
				output  = reportingDAO.generateDynamicReport(report.toDTO(), "pdf", allowedGroups, null, "NoUser");
				output2 = reportingDAO.generateDynamicReport(report.toDTO(), "xls", allowedGroups, null, "NoUser");
			}	

			FileOutputStream fos = new FileOutputStream(report.getTitle()+"-DynamicTest.pdf");
			fos.write(output);
			FileOutputStream fos2 = new FileOutputStream(report.getTitle()+"-DynamicTest.xls");
			fos2.write(output2);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e);
		}
	}

	/**
	 * Generate a Management Report for the Bristol and Avon, 
	 * hub without saving the report definition. The report 
	 * output is saved to the filesystem as pdf and xls 
	 * documents.
	 * 
	 * This report tests adding multiple charts and would 
	 * generally return the same results as the standard 
	 * management report.
	 */
	public void testGenerateEastAngliaManagementReport() {

		Long dsId = new Long(datasetId);
		DataSet ds = null;

		try {
			ds = dao.getDataSet(dsId).toHibernate();
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		IManagementReport report = factory.createManagementReport(ds, "PI (East Anglia Hub) Management Report");
		//report.setRole("PrincipalInvestigator");

		try {
			report.addGroup(ds.getGroup(1));
			report.addGroup(ds.getGroup(2));
			report.addGroup(ds.getGroup(11));    

			report.setWithRawData(false);

			IProjectSummaryChart chrt1 = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Whole Project");
			report.addManagementChart(chrt1);

			IGroupsSummaryChart chrt2 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "East Anglia Hub");
			report.addGroup(ds.getGroup(1));
			report.addGroup(ds.getGroup(2));
			report.addGroup(ds.getGroup(11));   
			report.addManagementChart(chrt2);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Problem setting up the report");
		}

		try {		
			byte[] output  = null;
			byte[] output2 = null;
			if (useSecurity) {
				ReportsClient client = new ReportsClient();
				output  = client.generateDynamicManagementReport(report, "pdf", null);
				output2 = client.generateDynamicManagementReport(report, "xls", null);
			}
			else {
				output  = reportingDAO.generateDynamicReport(report.toDTO(), "pdf", allowedGroups, null, "NoUser");
				output2 = reportingDAO.generateDynamicReport(report.toDTO(), "xls", allowedGroups, null, "NoUser");
			}	

			FileOutputStream fos = new FileOutputStream(report.getTitle()+"-DynamicTest.pdf");
			fos.write(output);
			FileOutputStream fos2 = new FileOutputStream(report.getTitle()+"-DynamicTest.xls");
			fos2.write(output2);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e);
		}
	}

	/**
	 * Creates a management report containing a 
	 * user summary chart, although these don't appear
	 * to be used a present.
	 */
	public void testUserSummaryChartReport() {

		Long dsId = new Long(datasetId);
		DataSet ds = null;

		try {
			ds = dao.getDataSet(dsId).toHibernate();
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		IManagementReport report = factory.createManagementReport(ds, "User Summary - Management Report");
		IUserSummaryChart chrt = factory.createUserSummaryChart(Chart.CHART_BAR, "Test user summary chart");
		try {
			chrt.addGroup(ds.getGroup(0));
			chrt.addGroup(ds.getGroup(1));
			chrt.addGroup(ds.getGroup(2));

			report.addChart(chrt);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Problem setting up the report");
		}

		try {		
			byte[] output  = null;
			byte[] output2 = null;
			if (useSecurity) {
				ReportsClient client = new ReportsClient();
				output  = client.generateDynamicManagementReport(report, "pdf", null);
				output2 = client.generateDynamicManagementReport(report, "xls", null);
			}
			else {
				output  = reportingDAO.generateDynamicReport(report.toDTO(), "pdf", allowedGroups, null, "NoUser");
				output2 = reportingDAO.generateDynamicReport(report.toDTO(), "xls", allowedGroups, null, "NoUser");
			}	

			FileOutputStream fos = new FileOutputStream(report.getTitle()+"-DynamicTest.pdf");
			fos.write(output);
			FileOutputStream fos2 = new FileOutputStream(report.getTitle()+"-DynamicTest.xls");
			fos2.write(output2);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e);
		}

	}

	public void testUKCRNSummaryReport() {
		
		Long dsId = new Long(datasetId);
		DataSet ds = null;

		try {
			ds = dao.getDataSet(dsId).toHibernate();
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		
		IManagementReport report = factory.createManagementReport(ds, "Outlook - UKCRN Report");
		//report.setRole("ChiefInvestigator");
		report.setWithRawData(false);

		IUKCRNSummaryChart chart = factory.createUKCRNSummaryChart(org.psygrid.data.reporting.Chart.CHART_TABLE, "Outlook");
		
		try {
			report.addChart(chart);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Unable to add chart");
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
			if (useSecurity) {
				ReportsClient client = new ReportsClient();
				output  = client.generateDynamicManagementReport(report, "pdf", null);
				output2 = client.generateDynamicManagementReport(report, "xls", null);
			}
			else {
				output  = reportingDAO.generateDynamicReport(report.toDTO(), "pdf", allowedGroups, null, "NoUser");
				output2 = reportingDAO.generateDynamicReport(report.toDTO(), "xls", allowedGroups, null, "NoUser");
			}	

			FileOutputStream fos = new FileOutputStream(report.getTitle()+"-DynamicTest.pdf");
			fos.write(output);
			FileOutputStream fos2 = new FileOutputStream(report.getTitle()+"-DynamicTest.xls");
			fos2.write(output2);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e);
		}
	}
	
	/**
	 * Add a time period to a given trends chart, for the previous six months
	 * based on the current date.
	 *  
	 * @param chart
	 * @return trendschart
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
}

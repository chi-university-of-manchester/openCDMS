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
import java.util.List;

import org.psygrid.data.dao.DAOTest;
import org.psygrid.data.dao.DAOTestHelper;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Factory;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.data.model.hibernate.Identifier;
import org.psygrid.data.model.hibernate.Record;
import org.psygrid.data.reporting.client.ReportsClient;
import org.psygrid.data.reporting.definition.IManagementChart;
import org.psygrid.data.reporting.definition.IManagementReport;
import org.psygrid.data.reporting.definition.ReportFrequency;
import org.psygrid.data.reporting.renderer.PdfRenderer;
import org.psygrid.data.repository.dao.RepositoryDAO;

public class ManagementTest extends DAOTest {

	private RepositoryDAO dao = null;

	private ReportingDAO reportingDAO = null;

	private Factory factory = null;

	private org.psygrid.data.reporting.definition.Factory reportFactory = null;

	protected void setUp() throws Exception {
		super.setUp();
		dao = (RepositoryDAO)ctx.getBean("repositoryDAOService");
		reportingDAO = (ReportingDAO)ctx.getBean("reportingDAO");
		factory = (Factory) ctx.getBean("factory");
		reportFactory = (org.psygrid.data.reporting.definition.Factory)ctx.getBean("reportFactory");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		dao = null;
		factory = null;
	}

	public void testRenderAndSaveAllMgmtReports() {
		
		String name = "testRenderAndSaveAllMgmtReports - "+(new Date()).toString();
		System.out.println("Running unit test: "+name);
		DataSet ds = null;
		Long dsId = new Long(5);
		
		//retrieve the Outlook dataset
		try {
			ds = dao.getDataSet(dsId).toHibernate();
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't retrieve dataset: "+ e.getMessage());
		}
		assertNotNull("No dataset retrieved", ds);
		//Generate the report and email the output
		try {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DATE, 1);
			cal.set(Calendar.MONTH, 0);
			while ( cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY ){
				cal.add(Calendar.DATE, 1);
			}
			List<ManagementReport> reportList = reportingDAO.generateMgmtReportsForDataSet(dsId, cal.getTime(), "NoUser", null);
			
			//Render all of the reports 
			for (ManagementReport r: reportList) {

				System.out.println("Rendering report: "+r.getTitle());

				FileOutputStream fos = new FileOutputStream(r.getTitle()+".pdf");
				PdfRenderer renderer = new PdfRenderer();	
				renderer.render(r, fos);
			}

		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't generate/send report ");
		}	



	}

	public void testMgmtReports(){
		try{
			//create a dataset
			String name = "testMgmtReports - "+(new Date()).toString();
			DataSet dataSet = factory.createDataset(name);
			java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
			String projectCode = DAOTestHelper.checkProjectCode(guid.toString());            
			dataSet.setProjectCode(projectCode);
			dataSet.addStatus(factory.createStatus("Status 1", 1));
			dataSet.addGroup(factory.createGroup("Group 1"));
			Long dsId = dao.saveDataSet(dataSet.toDTO());
			dao.publishDataSet(dsId);
			dataSet = dao.getDataSet(dsId).toHibernate();

			//generate identifiers...
			Identifier[] ids = DAOTestHelper.getIdentifiers(dao, projectCode, dsId, 2, "Group 1");

			//create a couple of records
			Record r1 = dataSet.generateInstance();
			r1.setIdentifier(ids[0]);
			dao.saveRecord(r1.toDTO(), true, null, "NoUser");
			Record r2 = dataSet.generateInstance();
			r2.setIdentifier(ids[1]);
			dao.saveRecord(r2.toDTO(), true, null, "NoUser");

			//create some management reports
			//Report 1 - null frequency
			IManagementReport report1 = reportFactory.createManagementReport(dataSet, "Report 1");
			//report1.setRole("ClinicalResearchManager");
			reportingDAO.saveReport(report1.toDTO());
			//Report 2 - weekly frequency
			IManagementReport report2 = reportFactory.createManagementReport(dataSet, "Report 2");
			//report2.setRole("ClinicalResearchManager");
			report2.setFrequency(ReportFrequency.WEEKLY);
			reportingDAO.saveReport(report2.toDTO());
			//Report 3 - monthly frequency
			IManagementReport report3 = reportFactory.createManagementReport(dataSet, "Report 3");
			//report3.setRole("ClinicalResearchManager");
			report3.setFrequency(ReportFrequency.MONTHLY);
			reportingDAO.saveReport(report3.toDTO());
			//Report 4 - quarterly frequency
			IManagementReport report4 = reportFactory.createManagementReport(dataSet, "Report 4");
			//report4.setRole("ClinicalResearchManager");
			report4.setFrequency(ReportFrequency.QUARTERLY);
			reportingDAO.saveReport(report4.toDTO());

			//Test which reports are generated
			//1. The first Monday in January - weekly, monthly and quarterly reports
			//should all be generated
			{
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.DATE, 1);
				cal.set(Calendar.MONTH, 0);
				while ( cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY ){
					cal.add(Calendar.DATE, 1);
				}
				List<ManagementReport> reports = reportingDAO.generateMgmtReportsForDataSet(dsId, cal.getTime(), "NoUser", null);
				assertEquals("Generated the wrong number of reports for 1st Monday of January ("+cal.getTime()+")",
						4, reports.size());
			}

			//2. The first Monday in February - weekly and monthly reports should be generated
			{
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.DATE, 1);
				cal.set(Calendar.MONTH, 1);
				while ( cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY ){
					cal.add(Calendar.DATE, 1);
				}
				List<ManagementReport> reports = reportingDAO.generateMgmtReportsForDataSet(dsId, cal.getTime(), "NoUser", null);
				assertEquals("Generated the wrong number of reports for 1st Monday of February ("+cal.getTime()+")",
						3, reports.size());
			}

			//3. The second Monday in March - weekly reports should be generated
			{
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.DATE, 8);
				cal.set(Calendar.MONTH, 2);
				while ( cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY ){
					cal.add(Calendar.DATE, 1);
				}
				List<ManagementReport> reports = reportingDAO.generateMgmtReportsForDataSet(dsId, cal.getTime(), "NoUser", null);
				assertEquals("Generated the wrong number of reports for 2nd Monday of March ("+cal.getTime()+")",
						2, reports.size());
			}

		}
		catch(Exception ex){
			ex.printStackTrace();
			fail("Exception: "+ex);
		}
	}

	public void testSaveUserSummChart(){
		try{
			String name = "testSaveUserSummChart - "+(new Date()).toString();
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

			Long dsId = dao.saveDataSet(ds.toDTO());
			ds = dao.getDataSet(dsId).toHibernate();

			IManagementReport rpt = reportFactory.createManagementReport(ds, "Test mgmt report");
			IManagementChart chrt = reportFactory.createUserSummaryChart(Chart.CHART_BAR, "Test user summary chart");
			rpt.addManagementChart(chrt);

			ReportsClient client = new ReportsClient();
			client.saveReport(rpt, null);

		}
		catch(Exception ex){
			ex.printStackTrace();
			fail("Exception: "+ex);
		}
	}

}

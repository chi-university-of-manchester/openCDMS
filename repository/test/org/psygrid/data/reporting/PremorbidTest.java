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

import org.psygrid.data.dao.DAOTest;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.reporting.definition.Factory;
import org.psygrid.data.reporting.definition.IRecordReport;
import org.psygrid.data.reporting.definition.IRecordChart;
import org.psygrid.data.reporting.definition.ISimpleChartItem;
import org.psygrid.data.reporting.definition.ISimpleChartRow;
import org.psygrid.data.repository.dao.RepositoryDAO;

public class PremorbidTest extends DAOTest {

	private RepositoryDAO dao = null;

	private ReportingDAO reportingDAO = null;

	private Factory factory = null;


	protected void setUp() throws Exception {
		super.setUp();
		dao = (RepositoryDAO)ctx.getBean("repositoryDAO");
		reportingDAO = (ReportingDAO)ctx.getBean("reportingDAO");
		factory = (Factory) ctx.getBean("reportFactory");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		dao = null;
		factory = null;
	}

	/**
	 * Creates a report for the premorbid adjustment scale
	 */
	public void testCreatePremorbidReport() {

		Long dsId = new Long(1);
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
			factory.createRecordReport(ds, "(JUnitTest) Premorbid Adjustment Scale - "+docOcc.getDisplayText());


		//would like to overwrite the report with a new version
		/*try {
			reports = dao.getReportsByDataSet(dsId);

		for (org.psygrid.data.reporting.definition.dto.Report r: reports) {
			if (r.getTitle().equals("Premorbid Adjustment Scale - Baseline")) {
				pasReport = (IRecordReport)r.toHibernate();
				break;
			}
		}
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}*/


		//check that we have the right document!
		if ( !pas.getName().equals("Premorbid Summary Sheet") ){
			fail("This isn't the Premorbid document!!");
			// throw new Exception("This isn't the Premorbid document!!!");
		}

		//retrieve the section containing the adjusted scores
		Section posSec = pas.getSection(2);
		SectionOccurrence adjustedSecOcc = posSec.getOccurrence(0);

		try {
			//Summary chart
			IRecordChart summary = factory.createSimpleChart(org.psygrid.data.reporting.Chart.CHART_TABLE,
			"Summary");

			pasReport.addChart(summary);


			ISimpleChartRow childhood = factory.createSimpleChartRow(); 
			//factory.createComplexChartItem();
			childhood.setLabel("Childhood");
			ISimpleChartRow earlyadolesence = factory.createSimpleChartRow(); 
			earlyadolesence.setLabel("Early Adolesence");
			ISimpleChartRow lateadolesence = factory.createSimpleChartRow(); 
			lateadolesence.setLabel("Late Adolesence");
			ISimpleChartRow adulthood = factory.createSimpleChartRow(); 
			adulthood.setLabel("Adulthood");

			summary.addRow(childhood);
			summary.addRow(earlyadolesence);
			summary.addRow(lateadolesence);
			summary.addRow(adulthood);

			//entry 0 = childhood
			//entry 5 = early adolescence
			//entry 11= late 
			//entry 17= adult
			//entry 21=general(stop)
			try {
//				childhood == 62
				for (int i = 63; i < 67; i++) {
					ISimpleChartItem item = factory.createSimpleChartItem(
							pas.getEntry(i), docOcc, adjustedSecOcc);
					childhood.addSeries(item);
				}
				for (int i = 68; i < 73; i++) {
					ISimpleChartItem item = factory.createSimpleChartItem(
							pas.getEntry(i), docOcc, adjustedSecOcc);
					earlyadolesence.addSeries(item);
				}
				//get label from entry(64) (i.e late adolesence)
				for (int i = 74; i < 79; i++) {
					ISimpleChartItem item = factory.createSimpleChartItem(
							pas.getEntry(i), docOcc, adjustedSecOcc);
					lateadolesence.addSeries(item);
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
			fail();
		}

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
		IRecordChart scores =  
			factory.createSimpleChart(org.psygrid.data.reporting.Chart.CHART_BAR_HZ,
			"Scores");
		try {
			pasReport.addChart(scores);


			ISimpleChartRow childhood = new org.psygrid.data.reporting.definition.hibernate.SimpleChartRow(); 
			childhood.setLabel("Childhood");
			ISimpleChartRow earlyadolesence = new org.psygrid.data.reporting.definition.hibernate.SimpleChartRow(); 
			earlyadolesence.setLabel("Early Adolesence");
			ISimpleChartRow lateadolesence = new org.psygrid.data.reporting.definition.hibernate.SimpleChartRow(); 
			lateadolesence.setLabel("Late Adolesence");
			ISimpleChartRow adulthood = new org.psygrid.data.reporting.definition.hibernate.SimpleChartRow(); 
			adulthood.setLabel("Adulthood");

			scores.addRow(childhood);
			scores.addRow(earlyadolesence);
			scores.addRow(lateadolesence);
			scores.addRow(adulthood);

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
					earlyadolesence.addSeries(item);
				}
				//get label from entry(64) (i.e late adolesence)
				for (int i = 74; i < 79; i++) {
					ISimpleChartItem item = factory.createSimpleChartItem(
							pas.getEntry(i), docOcc, adjustedSecOcc);
					lateadolesence.addSeries(item);
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

		try {
			reportingDAO.saveReport(pasReport.toDTO());
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e);
		}
	}

}

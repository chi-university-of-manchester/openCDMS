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

public class AllChartsTest extends DAOTest {

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
	 * Generates a report containing all RecordChart types, with a 
	 * data series containing a multiple categories.
	 *
	 */
	public void testAllChartsReport() {


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
			factory.createRecordReport(ds, "Unit Test - All Charts - "+docOcc.getDisplayText());


		//check that we have the right document!
		if ( !pas.getName().equals("Premorbid Summary Sheet") ){
			fail("This isn't the Premorbid document!!");
		}

		//retrieve the section containing the adjusted scores
		Section posSec = pas.getSection(2);
		System.out.println(posSec.getName());
		SectionOccurrence adjustedSecOcc = posSec.getOccurrence(0);
		System.out.println(adjustedSecOcc.getName());

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
				ISimpleChartRow earlyadolesence = new org.psygrid.data.reporting.definition.hibernate.SimpleChartRow(); 
				earlyadolesence.setLabel("Early Adolesence");
				ISimpleChartRow lateadolesence = new org.psygrid.data.reporting.definition.hibernate.SimpleChartRow(); 
				lateadolesence.setLabel("Late Adolesence");
				ISimpleChartRow adulthood = new org.psygrid.data.reporting.definition.hibernate.SimpleChartRow(); 
				adulthood.setLabel("Adulthood");

				chart.addRow(childhood);
				chart.addRow(earlyadolesence);
				chart.addRow(lateadolesence);
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

		}


		try {
			reportingDAO.saveReport(pasReport.toDTO());
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e);
		}
	}
	
	/**
	 * Generates a report containing all RecordChart types with a basic 
	 * data series containing a single category.
	 * 
	 * Uses the GAF data entry form.
	 */
	public void testAllSimpleChartsReport() {


		Long dsId = new Long(1);
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
			factory.createRecordReport(ds, "Unit Test - All Charts (Simple) - "+docOcc.getDisplayText());

		
		Section mainSec = gaf.getSection(0);
		SectionOccurrence mainSecOcc = mainSec.getOccurrence(0);


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
				row.setLabel("Row 1");
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
			reportingDAO.saveReport(gafReport.toDTO());
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e);
		}
	}
}

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
import org.psygrid.data.model.hibernate.Factory;
import org.psygrid.data.repository.dao.RepositoryDAO;

public class ReportTest extends DAOTest {

	private static final Long REPORT_ID = new Long(3140); //3140 //3307
	private static final Long RECORD_ID = new Long(4124);

	private RepositoryDAO dao = null;

	private ReportingDAO reportingDAO = null;
	
	private Factory factory = null;

	private org.psygrid.data.reporting.definition.Factory reportFactory = null;

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

	public void testReport(){
		try{
			String requestor = "NoUser";
			org.psygrid.data.reporting.RecordReport report = reportingDAO.generateReport(REPORT_ID, RECORD_ID, requestor, null);

			System.out.println("Report title="+report.getTitle());
			System.out.println("Report requestor="+report.getRequestor());
			System.out.println("Report subject="+report.getSubject());
			System.out.println("Report date="+report.getRequestDate());
			System.out.println("===============================================");
			for (org.psygrid.data.reporting.Chart c: report.getCharts()){
				System.out.println("Chart title="+c.getTitle());
				for ( int i=0; i<c.getTypes().length; i++ ){
					System.out.println("Chart type "+i+"="+c.getTypes()[i]);
				}
				System.out.print("Series labels=");
				for ( ChartRow row: c.getRows() ){
					System.out.print(row.getLabel()+" [ ");
					for (ChartSeries series: row.getSeries()) {
						System.out.print( series.getLabel()+", ");
					}
					System.out.print(" ]");
				}
				System.out.println();
				for ( org.psygrid.data.reporting.ChartRow r: c.getRows() ){
					System.out.println("-----------------------------------------------");
					System.out.println("Row label="+r.getLabel());
					System.out.println("Row label type="+r.getLabelType());
					for ( org.psygrid.data.reporting.ChartSeries s : r.getSeries() ) {
						for ( org.psygrid.data.reporting.ChartPoint p: s.getPoints() ){
							System.out.println("Point value="+p.getValue());
							System.out.println("Point value type="+p.getValueType());
						}
					}
				}                
				System.out.println("===============================================");
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
			fail("Exception: "+ex);
		}
	}

}
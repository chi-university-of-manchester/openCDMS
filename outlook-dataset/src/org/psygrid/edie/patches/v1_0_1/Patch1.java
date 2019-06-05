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

package org.psygrid.edie.patches.v1_0_1;

import java.util.List;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.reporting.client.ReportsClient;
import org.psygrid.data.reporting.definition.IManagementChart;
import org.psygrid.data.reporting.definition.IManagementReport;
import org.psygrid.data.reporting.definition.IReport;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch1 extends AbstractPatch {

    @Override
    public String getName() {
        return "Update Report and Chart Titles";
    }

    @Override
    public void applyPatch(DataSet ds, String saml) throws Exception {

		ReportsClient client = null;
		client = new ReportsClient();

		List<IReport> reports = client.getAllReportsByDataSet(ds.getId(), saml);
		for(IReport report : reports){
			IManagementReport r =(IManagementReport)report;
			if(r.getTitle().equals("EDIE 2 - Recruitment Manager Report")){
				System.out.println("Found "+r.getTitle());
				for (int i = 0; i < 6; i++) {
					IManagementChart chart = r.getChart(i);
					if (chart.getTitle().equals("Bristol Avon Hub")) {
						System.out.println(chart.getTitle());
						chart.setTitle("Manchester");
					} else if (chart.getTitle().equals("East Anglia Hub")) {
						chart.setTitle("Birmingham");
					} else if (chart.getTitle().equals("East Midlands Hub")) {
						chart.setTitle("Cambridge");
					} else if (chart.getTitle().equals("North East Hub")) {
						chart.setTitle("East Anglia");
					} else if (chart.getTitle().equals("North London Hub")) {
						chart.setTitle("Glasgow");
					}
				}
				client.saveReport(r, saml);
			}
			else if(r.getTitle().equals("EDIE 2 - Principal Investigator (Bristol Avon Hub) Report")){
				System.out.println("Found "+r.getTitle());
				r.setTitle("EDIE 2 - Principal Investigator (Manchester) Report");
				for (int i = 0; i < 2; i++) {
					IManagementChart chart = r.getChart(i);
					if (chart.getTitle().equals("Bristol Avon Hub")) {
						System.out.println(chart.getTitle());
						chart.setTitle("Manchester");
					}
				}
				client.saveReport(r, saml);
			}
			else if(r.getTitle().equals("EDIE 2 - Principal Investigator (East Anglia Hub) Report")){
				System.out.println("Found "+r.getTitle());
				r.setTitle("EDIE 2 - Principal Investigator (Birmingham) Report");
				for (int i = 0; i < 2; i++) {
					IManagementChart chart = r.getChart(i);
					if (chart.getTitle().equals("East Anglia Hub")) {
						System.out.println(chart.getTitle());
						chart.setTitle("Birmingham");
					}
				}
				client.saveReport(r, saml);
			}
			else if(r.getTitle().equals("EDIE 2 - Principal Investigator (East Midlands Hub) Report")){
				System.out.println("Found "+r.getTitle());
				r.setTitle("EDIE 2 - Principal Investigator (Cambridge) Report");
				for (int i = 0; i < 2; i++) {
					IManagementChart chart = r.getChart(i);
					if (chart.getTitle().equals("East Midlands Hub")) {
						System.out.println(chart.getTitle());
						chart.setTitle("Cambridge");
					}
				}
				client.saveReport(r, saml);
			}
			else if(r.getTitle().equals("EDIE 2 - Principal Investigator (North East Hub) Report")){
				System.out.println("Found "+r.getTitle());
				r.setTitle("EDIE 2 - Principal Investigator (East Anglia) Report");
				for (int i = 0; i < 2; i++) {
					IManagementChart chart = r.getChart(i);
					if (chart.getTitle().equals("North East Hub")) {
						System.out.println(chart.getTitle());
						chart.setTitle("East Anglia");
					}
				}
				client.saveReport(r, saml);
			}
			else if(r.getTitle().equals("EDIE 2 - Principal Investigator (North London Hub) Report")){
				System.out.println("Found "+r.getTitle());
				r.setTitle("EDIE 2 - Principal Investigator (Glasgow) Report");
				for (int i = 0; i < 2; i++) {
					IManagementChart chart = r.getChart(i);
					if (chart.getTitle().equals("North London Hub")) {
						System.out.println(chart.getTitle());
						chart.setTitle("Glasgow");
					}
				}
				client.saveReport(r, saml);
			}
		}
    }
}

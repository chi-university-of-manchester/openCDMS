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


package org.psygrid.drn.address.patches.v1_1_28;

import java.util.List;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.reporting.client.ReportsClient;
import org.psygrid.data.reporting.definition.IChart;
import org.psygrid.data.reporting.definition.IGroupsSummaryChart;
import org.psygrid.data.reporting.definition.IManagementReport;
import org.psygrid.data.reporting.definition.IRecruitmentProgressChart;
import org.psygrid.data.reporting.definition.IReport;
import org.psygrid.outlook.patches.AbstractPatch;

/**
 * @author Rob Harper
 *
 */
public class Patch6 extends AbstractPatch {

	/* (non-Javadoc)
	 * @see org.psygrid.outlook.patches.AbstractPatch#applyPatch(org.psygrid.data.model.IDataSet, java.lang.String)
	 */
	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		ReportsClient client = new ReportsClient();

		List<IReport> reports = client.getReportsOfType(ds.getProjectCode(), "management", saml);
		for ( IReport report: reports ){
			if ( report.getTitle().equals(ds.getName()+" - Principal Investigator (North West London) Report") ){
				System.out.println("Modifying "+ds.getName()+" - Principal Investigator (North West London) Report");
		        IManagementReport mr = (IManagementReport)report;
		        mr.addGroup(ds.getGroup(19));
		        mr.addGroup(ds.getGroup(20));
		        mr.addGroup(ds.getGroup(21));
		        mr.addGroup(ds.getGroup(22));
		        mr.addGroup(ds.getGroup(23));
		        IChart chart = mr.getChart(2);
		        if ( !"North West London".equals(chart.getTitle()) ){
		        	throw new RuntimeException("This is not the North West London chart - it is "+chart.getTitle());
		        }
		        IGroupsSummaryChart gsc = (IGroupsSummaryChart)chart;
		        gsc.addGroup(ds.getGroup(19));
		        gsc.addGroup(ds.getGroup(20));
		        gsc.addGroup(ds.getGroup(21));
		        gsc.addGroup(ds.getGroup(22));
		        gsc.addGroup(ds.getGroup(23));
		        client.saveReport(mr, saml);
			}
			if ( report.getTitle().equals(ds.getName()+" - Recruitment Progress Report") ){
				IManagementReport mr = (IManagementReport)report;
				if ( mr.getChart(0).getTitle().equals(ds.getName()+" - Recruitment Progress (North West London)")){
					System.out.println("Modifying Recruitment Progress North West London report");
					IRecruitmentProgressChart chart = (IRecruitmentProgressChart)mr.getChart(0);
					chart.addGroup(ds.getGroup(19));
					chart.addGroup(ds.getGroup(20));
					chart.addGroup(ds.getGroup(21));
					chart.addGroup(ds.getGroup(22));
					chart.addGroup(ds.getGroup(23));
			        client.saveReport(mr, saml);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.psygrid.outlook.patches.AbstractPatch#getName()
	 */
	@Override
	public String getName() {
		return "Update reports for new groups";
	}

}

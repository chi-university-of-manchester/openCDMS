/*
Copyright (c) 2006-2009, The University of Manchester, UK.

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

package org.psygrid.drn.address.patches.v1_6_4;

import java.util.List;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.reporting.client.ReportsClient;
import org.psygrid.data.reporting.definition.IReport;
import org.psygrid.drn.address.Reports;
import org.psygrid.outlook.patches.AbstractPatch;

/**
 * @author pwhelan
 *
 */
public class Patch36 extends AbstractPatch {

	/* (non-Javadoc)
	 * @see org.psygrid.outlook.patches.AbstractPatch#applyPatch(org.psygrid.data.model.IDataSet, java.lang.String)
	 */
	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		ReportsClient client = new ReportsClient();

		
		List<IReport> reports = client.getReportsOfType(ds.getProjectCode(), "management", saml);
		for ( IReport report: reports ){
			if ( report.getTitle().endsWith("Recruitment Manager Report") ||
				 report.getTitle().endsWith("Recruitment Progress Report")){
				System.out.println("Deleting "+report.getTitle());
				client.deleteReport(ds.getId(), report.getId(), saml);
			}
		}
		
		client.saveReport(Reports.cpmRecruitmentReport(ds), saml);
		client.saveReport(Reports.cpmMgmtReport(ds), saml);
		System.out.println("Reports saved");
		
	}

	/* (non-Javadoc)
	 * @see org.psygrid.outlook.patches.AbstractPatch#getName()
	 */
	@Override
	public String getName() {
		return "Explicitly set groups to management reports because of DEPRECATED groups";
	}

}

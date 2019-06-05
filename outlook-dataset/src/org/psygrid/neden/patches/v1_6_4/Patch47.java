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

package org.psygrid.neden.patches.v1_6_4;

import java.util.List;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.reporting.client.ReportsClient;
import org.psygrid.data.reporting.definition.IReport;
import org.psygrid.neden.NEDENDataset;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch47 extends AbstractPatch {

	public String getName() {
		return "Readd the reports, after adding the new Peterborough Centre";
	}

	public void applyPatch(DataSet ds, String saml) throws Exception {
		ReportsClient client = null;
		client = new ReportsClient();

		/* delete the old reports */
		System.out.println("Removing reports.. ");
		List<IReport> reports = client.getAllReportsByDataSet(ds.getId(), saml);
		for(IReport report : reports){
			client.deleteReport(ds.getId(), report.getId(), saml);
		}

		/* add the new reports */
		System.out.println("Creating reports..");
		NEDENDataset.createReports(ds, saml);
	}

}

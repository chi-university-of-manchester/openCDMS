package org.psygrid.drn.address.patches.v1_1_33;

import java.util.List;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.reporting.client.ReportsClient;
import org.psygrid.data.reporting.definition.IReport;
import org.psygrid.drn.address.Reports;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch16 extends AbstractPatch {

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		ReportsClient client = new ReportsClient();

		List<IReport> reports = client.getReportsOfType(ds.getProjectCode(), "management", saml);
		for ( IReport report: reports ){
			if ( report.getTitle().endsWith("Recruitment Progress Report(Overview)") ){
				System.out.println("Deleting "+report.getTitle());
				client.deleteReport(ds.getId(), report.getId(), saml);
			}
		}
		
		client.saveReport(Reports.recruitmentReport(ds), saml);
		client.saveReport(Reports.cpmMgmtReport(ds), saml);
	}

	@Override
	public String getName() {
		return "Correct the reports";
	}

}

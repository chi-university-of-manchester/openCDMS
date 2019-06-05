package org.psygrid.drn.address.patches.v1_7_0;

import java.util.List;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.reporting.client.ReportsClient;
import org.psygrid.data.reporting.definition.IReport;
import org.psygrid.drn.address.Reports;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch46 extends AbstractPatch {

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		ReportsClient client = new ReportsClient();

		List<IReport> reports = client.getReportsOfType(ds.getProjectCode(), "management", saml);
		for ( IReport report: reports ){
			if ( report.getTitle().endsWith("Recruitment Progress Report") || //Got it - equates to cpmRecruitmentReport
				 report.getTitle().endsWith("Recruitment Manager Report") || //Got it - eqates to cpmMgmtReport
				 report.getTitle().endsWith("Principal Investigator (North West England) Report")){ //Got it - equates to piNorthWestMgmtReport
				 
				System.out.println("Deleting "+report.getTitle());
				client.deleteReport(ds.getId(), report.getId(), saml);
			}
		}

		client.saveReport(Reports.cpmRecruitmentReport(ds), saml); //changed
		client.saveReport(Reports.cpmMgmtReport(ds), saml); //Changed
		client.saveReport(Reports.piNorthWestMgmtReport(ds), saml); //Changed

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Re-configure reports (adding 'Stockport NHS Trust' and 'Lancashire Teaching Hospitals Trust'";
	}

}

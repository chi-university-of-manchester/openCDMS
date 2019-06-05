package org.psygrid.drn.address.patches.v1_6_8;

import java.net.URL;
import java.util.List;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.outlook.patches.AbstractPatch;
import org.psygrid.data.reporting.client.ReportsClient;
import org.psygrid.data.reporting.definition.IReport;
import org.psygrid.drn.address.Reports;


public class Patch39 extends AbstractPatch {

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		
		String url = "https://nww.psygrid.nhs.uk/repository/services/reports"; 
		ReportsClient client = new ReportsClient(new URL(url));

		List<IReport> reports = client.getReportsOfType(ds.getProjectCode(), "management", saml);
		for ( IReport report: reports ){
			if ( report.getTitle().endsWith("Principal Investigator (Eastern England) Report") ||
				 report.getTitle().endsWith("Principal Investigator (North East and Cumbria) Report") ||
				 report.getTitle().endsWith("Recruitment Manager Report") ||
				 report.getTitle().endsWith("Recruitment Progress Report")){
				System.out.println("Deleting "+report.getTitle());
				client.deleteReport(ds.getId(), report.getId(), saml);
			}
		}
		
			
		client.saveReport(Reports.cpmMgmtReport(ds), saml);
		client.saveReport(Reports.cpmRecruitmentReport(ds), saml);
		client.saveReport(Reports.piEasternEnglandMgmtReport(ds), saml);
		client.saveReport(Reports.piNorthEastCumbriaMgmtReport(ds), saml);
	
	}

	@Override
	public String getName() {
		return "Reconfigure reports after adding two new centres.";
	}

}

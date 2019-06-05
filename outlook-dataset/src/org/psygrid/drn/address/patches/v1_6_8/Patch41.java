package org.psygrid.drn.address.patches.v1_6_8;

import java.net.URL;
import java.util.List;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.reporting.client.ReportsClient;
import org.psygrid.data.reporting.definition.IReport;
import org.psygrid.drn.address.Reports;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch41 extends AbstractPatch {

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		
		String url = "https://nww.psygrid.nhs.uk/repository/services/reports"; 
		ReportsClient client = new ReportsClient(new URL(url));

		
		List<IReport> reports = client.getReportsOfType(ds.getProjectCode(), "management", saml);
		for ( IReport report: reports ){
			if ( report.getTitle().endsWith("Principal Investigator (Thames Valley) Report") ||
				 report.getTitle().endsWith("Principal Investigator (North East London) Report") ||
				 report.getTitle().endsWith("Recruitment Manager Report") ||
				 report.getTitle().endsWith("Recruitment Progress Report") ||
				 report.getTitle().endsWith("Recruitment Progress Report(North East London)") ||
				 report.getTitle().endsWith("Recruitment Progress Report(Thames Valley)")){
				System.out.println("Deleting "+report.getTitle());
				client.deleteReport(ds.getId(), report.getId(), saml);
			}
		}
		
		client.saveReport(Reports.piThamesValleyMgmtReport(ds), saml); //For the 'Principal Investigator (Thames Valley) Report'
		client.saveReport(Reports.piNorthEastLondonMgmtReport(ds), saml); //For the 'Principal Investigator (North East London) Report'
		client.saveReport(Reports.cpmMgmtReport(ds), saml); //For the 'Recruitment Manager Report'
		client.saveReport(Reports.cpmRecruitmentReport(ds), saml); //For the 'Recruitment Progress Report'
		client.saveReport(Reports.recruitmentInThamesValleyReport(ds), saml); //For the 'Recruitment Progress Report(Thames Valley)'
		client.saveReport(Reports.recruitmentInNorthEastLondonReport(ds), saml); //For the 'Recruitment Progress Report(North East London)'
	
	}

	@Override
	public String getName() {
		return "Reconfigure reports after adding two new centres: 'University Hospitals Coventry' and 'Barking, Havering and Redbridge Hospitals NHS Trust'.";
	}

}

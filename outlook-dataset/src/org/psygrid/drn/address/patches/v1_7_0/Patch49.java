package org.psygrid.drn.address.patches.v1_7_0;

import java.util.List;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.reporting.client.ReportsClient;
import org.psygrid.data.reporting.definition.IReport;
import org.psygrid.drn.address.AddressDataset;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch49 extends AbstractPatch {

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		
		ReportsClient client = new ReportsClient();

		List<IReport> reports = client.getReportsOfType(ds.getProjectCode(), "management", saml);
		for ( IReport report: reports ){
				client.deleteReport(ds.getId(), report.getId(), saml);
			}
			
		AddressDataset.createReports(ds, saml);
		
		}
		

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Adding four new 'Northeast and Cumbria centres to reports." ;
	}

}

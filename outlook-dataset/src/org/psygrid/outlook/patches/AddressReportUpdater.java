package org.psygrid.outlook.patches;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.drn.address.AddressDataset;

/**
 * Updates address reports
 * 
 * Example arguments:
 * 
 * -l https://localhost:443/authentication/services/login -u MattMachin -w mypasswd ADD
 * 
 * @author MattMachin
 *
 */
public class AddressReportUpdater extends ReportUpdater {

	public static void main(String[] args) throws Exception {
		AddressReportUpdater reportUpdater = new AddressReportUpdater();
		reportUpdater.updateReports(args);
	}
	
	protected void createReports(DataSet ds, String saml) throws Exception {
		AddressDataset.createReports(ds, saml);
	}
}

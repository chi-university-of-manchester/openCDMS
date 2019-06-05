package org.psygrid.outlook.patches;

import java.util.List;
import java.util.Properties;

import org.apache.axis.utils.Options;
import org.opensaml.SAMLAssertion;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.reporting.client.ReportsClient;
import org.psygrid.data.reporting.definition.IReport;
import org.psygrid.data.repository.client.RepositoryClient;
import org.psygrid.security.authentication.client.LoginClient;
import org.psygrid.security.utils.PropertyUtilities;

/**
 * Base class to update reports for a study. Deletes existing reports and then creates new ones.
 * @author MattMachin
 *
 */
public abstract class ReportUpdater {
	private UpdaterAndPatcherUtils utils = new UpdaterAndPatcherUtils(); 
	
	protected void updateReports(String[] args) throws Exception {
		System.setProperty("axis.socketSecureFactory",
	        "org.psygrid.security.components.net.PsyGridClientSocketFactory");
        Options opts = new Options(args);
        String[] patcherArgs = opts.getRemainingArgs();
        Properties properties = PropertyUtilities.getProperties("test.properties");
        System.out.println(properties.getProperty("org.psygrid.security.authentication.client.trustStoreLocation"));
        LoginClient tc = null;
        
        try {
            tc = new LoginClient("test.properties");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        char[] password = opts.getPassword().toCharArray();
        short[] pwd = utils.getPasswordAsShort(password);
        
        utils.login(properties, tc, opts, pwd);
        
        SAMLAssertion sa = utils.getSAML(properties, password);
 
        String repositoryUrl = properties.getProperty("org.psygrid.data.client.serviceURL");
        RepositoryClient client = utils.getRepositoryClient(repositoryUrl);
        DataSet ds = utils.loadDataset(client, patcherArgs[0], sa.toString());
        
        performUpdate(ds, sa.toString());
	}
	
	/**
	 * Override this method in derived classes to create the reports for a particular data set.
	 */
	protected abstract void createReports(DataSet ds, String saml) throws Exception;
	
	private void performUpdate(DataSet ds, String saml) throws Exception {
		ReportsClient client = new ReportsClient();

		List<IReport> reports = client.getAllReportsByDataSet(ds.getId(), saml);
		System.out.println("Deleting reports...");
		for ( IReport report: reports ){
				client.deleteReport(ds.getId(), report.getId(), saml);
			}
			
		createReports(ds, saml);
		System.out.println("New reports created ... done");
	}
	
		
}

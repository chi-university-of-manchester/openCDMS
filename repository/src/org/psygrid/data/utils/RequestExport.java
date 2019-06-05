package org.psygrid.data.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.axis.encoding.Base64;
import org.apache.axis.utils.Options;
import org.opensaml.SAMLAssertion;
import org.psygrid.data.export.ExportFormat;
import org.psygrid.data.export.hibernate.ExportDocument;
import org.psygrid.data.export.hibernate.ExportRequest;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.DocumentOccurrence;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.data.repository.RepositoryNoSuchDatasetFault;
import org.psygrid.data.repository.RepositoryServiceFault;
import org.psygrid.data.repository.client.RepositoryClient;
import org.psygrid.data.utils.security.NotAuthorisedFault;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.PGSecurityInvalidSAMLException;
import org.psygrid.security.PGSecuritySAMLVerificationException;
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.psygrid.security.authentication.client.LoginClient;
import org.psygrid.security.authentication.service.LoginServicePortType;
import org.psygrid.security.components.net.PsyGridClientSocketFactory;
import org.psygrid.security.utils.PropertyUtilities;
import org.psygrid.security.utils.SAMLUtilities;

public class RequestExport {
	
	/**
	 * logger
	 */
	private static LoginServicePortType aa1 = null;

	/**
	 * This is used to request an export for a studies whose exports cannot be requested
	 * via the web (that is, all studies whose project codes do not comply with the outlook-style
	 * centre format of 1st 3 digits for hub and last 3 digits for trust.
	 * Until that bug is fixed (#1310), this class is the workaround.
	 * @param args
	 * @throws IOException 
	 * @throws PGSecurityException 
	 * @throws PGSecurityInvalidSAMLException 
	 * @throws PGSecuritySAMLVerificationException 
	 * @throws RepositoryNoSuchDatasetFault 
	 * @throws NotAuthorisedFault 
	 * @throws RepositoryServiceFault 
	 */
	public static void main(String[] args) throws IOException, PGSecurityException, PGSecuritySAMLVerificationException, PGSecurityInvalidSAMLException, RepositoryServiceFault, NotAuthorisedFault, RepositoryNoSuchDatasetFault {
		
		System.setProperty("axis.socketSecureFactory",
		"org.psygrid.security.components.net.PsyGridClientSocketFactory");
		Options opts = new Options(args);
		String[] remaining = opts.getRemainingArgs();
		Properties properties = PropertyUtilities.getProperties("test.properties");
		System.out.println(properties.getProperty("org.psygrid.security.authentication.client.trustStoreLocation"));
		LoginClient tc = null;
		
		try {
			tc = new LoginClient("test.properties");
			aa1 = tc.getPort();
		} catch (Exception e) {
			e.printStackTrace();
		}
		char[] password = opts.getPassword().toCharArray();
		short[] pwd = new short[password.length];
		for (int i = 0; i < pwd.length; i++) {
			pwd[i] = (short) password[i];
		}
		String credential = tc.getPort().login(opts.getUser(), pwd);
		if (credential != null) {
			byte[] ks = Base64.decode(credential);
			FileOutputStream fos = new FileOutputStream(properties
					.getProperty("org.psygrid.security.authentication.client.keyStoreLocation"));
			fos.write(ks);
			fos.flush(); 
			fos.close();
		}
		System.out.println("loggedin");
		System.setProperty("javax.net.ssl.keyStorePassword", new String(password));
		PsyGridClientSocketFactory.reinit();
		AAQueryClient qc = new AAQueryClient("test.properties");
		System.out.println("getAssertion");
		SAMLAssertion sa = qc.getSAMLAssertion();
		
		String theUser = SAMLUtilities.getUserFromSAML(sa.toString());

		String repositoryServiceURLStr = properties.getProperty("org.psygrid.data.client.serviceURL");
		URL repositoryServiceURL = new URL(repositoryServiceURLStr);
		RepositoryClient rc = new RepositoryClient(repositoryServiceURL);
		
		DataSet dsSummary = rc.getDataSetSummary(remaining[0], new Date(0), sa.toString());
        DataSet ds = rc.getDataSet(dsSummary.getId(), sa.toString());
        
        int numberOfGroups = ds.numGroups();
        List<String> groups = new ArrayList<String>();
        for(int i = 0; i < numberOfGroups; i++){
        	Group gp = ds.getGroup(i);
        	String gpCode = gp.getName();
        	groups.add(gpCode);
        }
        ExportFormat fmt = ExportFormat.SINGLE_CSV;
        ExportRequest exportRequest = new ExportRequest(theUser, remaining[0], groups, fmt.toString(), true);
        
        List<String> statuses = new ArrayList<String>();
		statuses.add("Incomplete");
		statuses.add("Complete");
		statuses.add("Rejected");
		statuses.add("Approved");
		exportRequest.setDocumentStatuses(statuses);
		
		List<ExportDocument> docOccs = new ArrayList<ExportDocument>();
		
		int numDocuments = ds.numDocuments();
		for(int i = 0; i < numDocuments; i++){
			Document doc = ds.getDocument(i);
			
			int numOccurrences = doc.numOccurrences();
			
			for(int j = 0; j < numOccurrences; j++){
				
				DocumentOccurrence docOcc = doc.getOccurrence(j);
			
				ExportDocument expDoc = new ExportDocument();
				expDoc.setDocOccId(docOcc.getId());
				docOccs.add(expDoc);
				
			}
			
		}
		
		exportRequest.setDocOccs(docOccs);
        
        rc.requestExport(exportRequest, sa.toString());

	}

}

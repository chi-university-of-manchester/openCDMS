package org.psygrid.outlook.patches;

import java.io.FileOutputStream;
import java.net.URL;
import java.util.Date;
import java.util.Properties;

import org.apache.axis.encoding.Base64;
import org.apache.axis.utils.Options;
import org.opensaml.SAMLAssertion;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.repository.client.RepositoryClient;
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.psygrid.security.authentication.client.LoginClient;
import org.psygrid.security.components.net.PsyGridClientSocketFactory;

public class UpdaterAndPatcherUtils {
	public SAMLAssertion getSAML(Properties properties, char[] password) throws Exception {
		System.setProperty("javax.net.ssl.keyStorePassword", new String(password));
		PsyGridClientSocketFactory.reinit();
        AAQueryClient qc = new AAQueryClient("test.properties");
        System.out.println("getAssertion");
        SAMLAssertion sa = qc.getSAMLAssertion();
        System.out.println(sa.toString());
        System.out.println(properties.getProperty("org.psygrid.data.client.serviceURL"));
        
        return sa;
	}
	
	public void login(Properties properties, LoginClient tc, Options opts, short[] pwd) throws Exception {
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
	}
	
	public RepositoryClient getRepositoryClient(String repositoryUrl) throws Exception {
		RepositoryClient client = null;
        if ( null == repositoryUrl ){
            client = new RepositoryClient();
        }
        else{
            client = new RepositoryClient(new URL(repositoryUrl));
        }
        return client;
	}
	
	public DataSet loadDataset(RepositoryClient client, String projectCode, String saml) throws Exception {
		System.out.println("Loading the dataset...");
        DataSet dsSummary = client.getDataSetSummary(projectCode, new Date(0), saml);
        DataSet ds = client.getDataSet(dsSummary.getId(), saml);
        
        return ds;
	}
	
	public short[] getPasswordAsShort(char[] password) {
		short[] pwd = new short[password.length];
        for (int i = 0; i < pwd.length; i++) {
            pwd[i] = (short) password[i];
        }
        return pwd;
	}
}

package org.psygrid.IAT;

import java.io.FileOutputStream;
import java.net.ConnectException;
import java.net.URL;
import java.util.Properties;

import org.apache.axis.encoding.Base64;
import org.apache.axis.utils.Options;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opensaml.SAMLAssertion;
import org.psygrid.champ.InstallChampRandomizer;
import org.psygrid.randomization.client.RandomizationClient;
import org.psygrid.randomization.model.hibernate.StratifiedRandomizer;
import org.psygrid.randomization.model.hibernate.Stratum;
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.psygrid.security.authentication.client.LoginClient;
import org.psygrid.security.authentication.service.LoginServicePortType;
import org.psygrid.security.components.net.PsyGridClientSocketFactory;
import org.psygrid.security.utils.PropertyUtilities;
import org.psygrid.www.xml.security.core.types.ProjectType;

import randomX.randomHotBits;
import randomX.randomX;

/**
 * Patch in the randomizer for the Interact study
 * 
 * @author pwhelan
 */
public class InstallIATRandomizer {
	public static final String DS_NAMESPACE = "http://www.w3.org/2000/09/xmldsig#";

	/**
	 * logger
	 */
	private static LoginServicePortType aa1 = null;

	private static Log _log = LogFactory.getLog(InstallIATRandomizer.class);

	public static void main(String[] args) throws Exception {
		System.setProperty("axis.socketSecureFactory",
				"org.psygrid.security.components.net.PsyGridClientSocketFactory");
		Options opts = new Options(args);
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
		SAMLAssertion sa = qc.getSAMLAssertion(new ProjectType("INTERACT", "INACT", "INTERACT", "5215", false));

		System.out.println("insert");
		System.out.println(sa.toString());
		System.out.println(properties.getProperty("org.psygrid.randomization.client.serviceURL"));
		String randomization = properties.getProperty("org.psygrid.randomization.client.serviceURL");
        try{
            RandomizationClient client = null;
            
            client = new RandomizationClient(new URL(randomization));

            StratifiedRandomizer rnd = new StratifiedRandomizer("INACT");

            Stratum s1 = new Stratum();
            s1.setName("Medication");
            s1.getValues().add("Olanzapine/Clozapine last 6 months");
            s1.getValues().add("No Olanzapine/Clozapine last 6 months");
            rnd.addStratum(s1);
            
            rnd.generateCombinations("org.psygrid.randomization.model.hibernate.IBRpbrblRandomizer", 1, 4);
            int seedlen = (s1.getValues().size());
            long[] seeds= new long[seedlen];
            for(int i=0;i<seedlen;i++){
                seeds[i]=getSeed();
            }
            rnd.createRngs(seeds);
            rnd.addTreatment("Intervention","1");
            rnd.addTreatment("Treatment as usual","2");
            client.saveRandomizer(rnd, sa.toString());

            System.out.println("Randomizer successfully saved to the service");   
        }
        catch(Exception ex){
            ex.printStackTrace();
        }	
    }

	/**
	 * Get the random seed
	 * @return
	 * @throws ConnectException
	 */
	static Long getSeed() throws ConnectException{
		randomX randomizer = new randomHotBits();
		return randomizer.nextLong();
	}

}

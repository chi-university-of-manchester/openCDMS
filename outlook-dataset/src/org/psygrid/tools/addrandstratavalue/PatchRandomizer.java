package org.psygrid.tools.addrandstratavalue;
import java.io.FileOutputStream;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import org.apache.axis.encoding.Base64;
import org.apache.axis.utils.Options;
import org.opensaml.SAMLAssertion;
import org.psygrid.command.CommandRandomizer;
import org.psygrid.randomization.NotAuthorisedFault;
import org.psygrid.randomization.RandomizationFault;
import org.psygrid.randomization.UnknownRandomizerFault;
import org.psygrid.randomization.client.RandomizationClient;
import org.psygrid.randomization.model.hibernate.Randomizer;
import org.psygrid.randomization.model.hibernate.StratifiedRandomizer;
import org.psygrid.randomization.model.hibernate.Stratum;
import org.psygrid.randomization.model.hibernate.StratumCombination;
import org.psygrid.randomization.model.hibernate.Treatment;
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.psygrid.security.authentication.client.LoginClient;
import org.psygrid.security.authentication.service.LoginServicePortType;
import org.psygrid.security.components.net.PsyGridClientSocketFactory;
import org.psygrid.security.utils.PropertyUtilities;
import org.psygrid.www.xml.security.core.types.ProjectType;

import java.util.Properties;


public class PatchRandomizer {
	
	/**
	 * logger
	 */
	private static LoginServicePortType aa1 = null;


	/**
	 * @param args
	 * args[0] username delimiter, -u | REQUIRED
	 * args[1] username | REQUIRED
	 * args[2] password delimiter, -w | REQUIRED
	 * args[3] password | REQUIRED
	 * args[4] randomizer name | REQUIRED
	 * args[5] name of existing stratum to augment | REQUIRED
	 * args[6] new strata value name to add to existing stratum | REQUIRED
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
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
		
		
		System.out.println(properties.getProperty("org.psygrid.randomization.client.serviceURL"));
		String randomization = properties.getProperty("org.psygrid.randomization.client.serviceURL");

		URL url;
		try {
			url = new URL(randomization);
			RandomizationClient client = new RandomizationClient(url);
			Randomizer rand = client.getRandomizer(remaining[0], sa.toString());
			
			StratifiedRandomizer sRand = (StratifiedRandomizer)rand;
			
			List<Stratum> strata = sRand.getStrata();
			Stratum stratumToModify = null;
			int numCombinations = 1;
			for(Stratum stratum: strata){
				if(stratum.getName().equals(remaining[1])){
					stratumToModify = stratum;
				}else{
					numCombinations *= stratum.getValues().size();
				}
			}
			
			if(stratumToModify != null){
				long[] seeds= new long[numCombinations];
		        for(int i=0;i<numCombinations;i++){
		            seeds[i]=CommandRandomizer.getSeed();
		        }
		        
		       //sRand.addNewStrataValue(stratumToModify, remaining[2], "org.psygrid.randomization.model.hibernate.IBRpbrblRandomizer", seeds);
		      
		        
			}else{
				throw new Exception("Could not find the " + stratumToModify + " stratum in the randomizer.");
			}
		
			client.saveRandomizer(sRand, sa.toString());
			
			System.out.println("Randomizer saved successfully.");


		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConnectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SocketTimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownRandomizerFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RandomizationFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotAuthorisedFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
				
	}

}

package org.psygrid.datasetdesigner.utils;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.axis.encoding.Base64;
import org.apache.axis.utils.Options;
import org.opensaml.SAMLAssertion;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.datasetdesigner.model.RandomisationHolderModel;
import org.psygrid.datasetdesigner.model.StudyDataSet;
import org.psygrid.randomization.client.RandomizationClient;
import org.psygrid.randomization.model.hibernate.StratifiedRandomizer;
import org.psygrid.randomization.model.hibernate.Stratum;
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.psygrid.security.authentication.client.LoginClient;
import org.psygrid.security.authentication.service.LoginServicePortType;
import org.psygrid.security.components.net.PsyGridClientSocketFactory;
import org.psygrid.security.utils.PropertyUtilities;


public class SaveRandomizer {
	
	private static LoginServicePortType aa1 = null;

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		System.setProperty("axis.socketSecureFactory",
		"org.psygrid.security.components.net.PsyGridClientSocketFactory");
		Options opts = new Options(args);
		String[] remainingArgs = opts.getRemainingArgs();
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
		
		String seedFile = remainingArgs[0];
		BufferedReader reader = new BufferedReader(new FileReader(seedFile));
		
		String line = reader.readLine();
		List<Long> seedList = new ArrayList<Long>();
		
		do{
			
			try{
			Long seed = Long.parseLong(line);
			seedList.add(seed);
			line = reader.readLine();
			}catch (NumberFormatException e){
				throw new Exception(e);
			}
			
			
		}while(line != null);
		
		String dsName = remainingArgs[1];
		PersistenceManager.getInstance().setAliases();
		
		Object obj1 = PersistenceManager.getInstance().load(dsName);
		
		StudyDataSet dSet = (StudyDataSet)obj1;
		

		String randomiserLocation  = remainingArgs[2];
		
		RandomizationClient client;
		client = new RandomizationClient(new URL(randomiserLocation));
		StratifiedRandomizer rnd = new StratifiedRandomizer(dSet.getDs().getProjectCode());
		RandomisationHolderModel rhm  = dSet.getRandomHolderModel();
		
		if (rhm != null && rhm.getRandomisationStrata() != null &&
				rhm.getRandomisationStrata().size() > 0) {
			int seedlen = 0;
			for (int j=0; j<rhm.getRandomisationStrata().size(); j++) {
				Stratum s = rhm.getRandomisationStrata().get(j);

				GetRandomizerSeeds.configureStrataValues(s, dSet);

				rnd.addStratum(s);

				if (seedlen == 0) {
					seedlen = s.getValues().size();
				} else {
					seedlen *= s.getValues().size();
				}
			}

			int minBlockSize = rhm.getMinimumBlockSize();
			int maxBlockSize = rhm.getMaximumBlockSize();
			minBlockSize /= rhm.getRandomisationTreatments().size();
			maxBlockSize /= rhm.getRandomisationTreatments().size();

			rnd.generateCombinations("org.psygrid.randomization.model.hibernate.IBRpbrblRandomizer", minBlockSize, maxBlockSize);

			int numSeeds = seedList.size();
			long[] seedArray = new long[numSeeds];
			
			for(int count = 0; count < numSeeds; count++){
				seedArray[count] = seedList.get(count).longValue();
			}
			
			rnd.createRngs(seedArray);

			if (rhm.getRandomisationTreatments() != null) {
				for (int i=0; i<rhm.getRandomisationTreatments().size(); i++) {
					rnd.addTreatment(rhm.getRandomisationTreatments().get(i).getTreatmentName(), rhm.getRandomisationTreatments().get(i).getTreatmentCode());
				}
			}
			
			try{
				client.saveRandomizer(rnd, sa.toString());
			}catch (Exception e){
				throw new Exception("Randomizer save didn't work.");
			}
			

		}
	
		

	}

}

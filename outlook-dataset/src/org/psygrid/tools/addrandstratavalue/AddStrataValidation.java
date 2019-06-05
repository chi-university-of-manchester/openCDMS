package org.psygrid.tools.addrandstratavalue;

import java.io.FileOutputStream;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.axis.encoding.Base64;
import org.apache.axis.utils.Options;
import org.opensaml.SAMLAssertion;
import org.psygrid.command.CommandRandomizer;
import org.psygrid.randomization.DuplicateRandomizerFault;
import org.psygrid.randomization.NotAuthorisedFault;
import org.psygrid.randomization.Parameter;
import org.psygrid.randomization.RandomizationFault;
import org.psygrid.randomization.client.RandomizationClient;
import org.psygrid.randomization.model.hibernate.Randomizer;
import org.psygrid.randomization.model.hibernate.StratifiedRandomizer;
import org.psygrid.randomization.model.hibernate.Stratum;
import org.psygrid.randomization.model.hibernate.StratumCombination;
import org.psygrid.randomization.model.hibernate.StratumPointer;
import org.psygrid.randomization.model.hibernate.Treatment;
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.psygrid.security.authentication.client.LoginClient;
import org.psygrid.security.authentication.service.AccountDormantFaultMessage;
import org.psygrid.security.authentication.service.AccountLockedFaultMessage;
import org.psygrid.security.authentication.service.LoginServicePortType;
import org.psygrid.security.authentication.service.NotAuthorisedFaultMessage;
import org.psygrid.security.authentication.service.ProcessingFaultMessage;
import org.psygrid.security.components.net.PsyGridClientSocketFactory;
import org.psygrid.security.utils.PropertyUtilities;


/**
 * This class validates the addition of a new strata value to an study whose randomiser is already running.
 * To use this class:
 * 1) Create a new study via Create on a test server. Ensure that it uses randomisation. Make sure that it is a 
 * 		stratified randomisation. The number of strata, strata values and treatment arms is up to you. You can test whatever
 * 		configuration you like with this class.
 * 2) Publish that study. Don't use it with Collect. Only use this class to make allocations to the randomizer.
 * 3) Run this test class (the required inputs are explained below). Basically, this programme will allocate a specified number of
 * 		the full array of strata combinations to be randomized. So if you specify 10 and you have sex as your only strata, then it will
 * 		allocate 10*2 subjects (10 male and 10 female) to the randomizer. It will then grab the statistics to prove that the allocations
 * 		have been done evenly. Next, the programme adds a value to one of the strata, as specified. It then allocates x sets of strata
 * 		combinations, with the new combinations included, of course. Then it gathers the statistics to show that the allocations for each
 * 		stratum combination are even-handed. This test is good because it (a) test to make sure that each new stratum combination is properly
 * 		created and initialized and that (b) allocation users representing these new users actually works and (c) the statistics show that the
 * 		allocations being made for these new strata are correctly distributed across the treatment arms.
 * 
 * args[0] username delimiter, -u | REQUIRED
 * args[1] username | REQUIRED
 * args[2] password delimiter, -w | REQUIRED
 * args[3] password | REQUIRED
 * args[4] project code/randomiser name
 * args[5] number of strata combination sets to allocate participants to
 * args[6] name of stratum to modify
 * args[7] name of stratum value to add to stratum
 * args[8]

 * @param args
 */

public class AddStrataValidation {

		private static Properties properties = null;
		private SAMLAssertion sa = null; 	
		private Integer currentParticipantId = new Integer(300);
		private StratifiedRandomizer randomizer = null;
		final private Options options;
		final private int numberOfAllocationCycles;
		final private String randomizerName;
		final private String stratumToAugment;
		final private String newStrataValue;
		
		public AddStrataValidation(Options opts, String[] remainingArgs) throws Exception{
			options = opts;
			
			numberOfAllocationCycles = new Integer(remainingArgs[1]).intValue();
			randomizerName = remainingArgs[0];
			stratumToAugment = remainingArgs[2];
			newStrataValue = remainingArgs[3];
			
			login();
		}
		
		/**
		 * Initialises the randomization client.
		 * @throws Exception 
		 */
		private RandomizationClient getRandomizerClient() throws Exception{
			String randomization = properties.getProperty("org.psygrid.randomization.client.serviceURL");

			URL url;
			url = new URL(randomization);
			RandomizationClient client = new RandomizationClient(url);
			return client;
		}
		
		/**
		 * Gets the user's SAML assertion.
		 * @throws Exception
		 */
		private void login() throws Exception{
			
			LoginServicePortType aa1 = null;
			
			Properties properties = PropertyUtilities.getProperties("test.properties");
			System.out.println(properties.getProperty("org.psygrid.security.authentication.client.trustStoreLocation"));
			LoginClient tc = null;
			
			try {
				tc = new LoginClient("test.properties");
				aa1 = tc.getPort();
			} catch (Exception e) {
				e.printStackTrace();
			}
			char[] password = options.getPassword().toCharArray();
			short[] pwd = new short[password.length];
			for (int i = 0; i < pwd.length; i++) {
				pwd[i] = (short) password[i];
			}
			String credential = tc.getPort().login(options.getUser(), pwd);
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
			sa = qc.getSAMLAssertion();
		}
	
		public static void main(String[] args) throws Exception {

			System.setProperty("axis.socketSecureFactory",
			"org.psygrid.security.components.net.PsyGridClientSocketFactory");
			Options opts = new Options(args);
			String[] remaining = opts.getRemainingArgs();
			properties = PropertyUtilities.getProperties("test.properties");
			System.out.println(properties.getProperty("org.psygrid.security.authentication.client.trustStoreLocation"));
			
			AddStrataValidation validator = new AddStrataValidation(opts, remaining);
			
			validator.reportAllocationStatistics();
			return;
			/*
			validator.doAllocations();
			validator.reportAllocationStatistics();
			*/
			
			/*
			validator.amendRandomizer();
			validator.doAllocations();
			validator.reportAllocationStatistics();
			*/
			
		}
		
		/**
		 * Allocates a number of new users to the randomizer.
		 * Retrieves the randomizer, and allocates a new user for each stratum combination.
		 * Does this allocation cycle the number of times specified by 'numberOfAllocationCycles'.
		 * @throws Exception 
		 */
		public void doAllocations() throws Exception{
			randomizer = getRandomizer();
			RandomizationClient client = getRandomizerClient();
			List<StratumCombination> combinations = randomizer.getCombinations();
			
			for(int i = 0; i < numberOfAllocationCycles; i++){
				
				for(StratumCombination combo : combinations){
					
					int paramCount = 0;
					Parameter[] parameters = new Parameter[combo.getPointers().size()];
					
					List<StratumPointer> pointers = combo.getPointers();
					
					for(StratumPointer ptr: pointers){
						
						parameters[paramCount] = new Parameter(ptr.getStratum().getName(), ptr.getValue());
						paramCount++;
					}
					
					String subject = getNextParticipantId();	
					client.allocate(randomizerName, subject, parameters, sa.toString());
				}		
			}
		}
		
		private String getNextParticipantId(){
			String participantId = this.currentParticipantId.toString();
			currentParticipantId++;
			return participantId;
		}
		
		/**
		 * prints out the allocation statistics for each and every stratum combination.
		 * @throws Exception 
		 */
		public void reportAllocationStatistics() throws Exception{
			
			RandomizationClient client = getRandomizerClient();
			
			StratifiedRandomizer randomizer = getRandomizer();
			
			List<StratumCombination> combinations = randomizer.getCombinations();
			
			int stratumComboCount = 0;
			
			for(StratumCombination combo : combinations){
				stratumComboCount++;
				
				int paramCount = 0;
				Parameter[] parameters = new Parameter[combo.getPointers().size()];
				
				List<StratumPointer> pointers = combo.getPointers();
				
				for(StratumPointer ptr: pointers){
					
					parameters[paramCount] = new Parameter(ptr.getStratum().getName(), ptr.getValue());
					paramCount++;
				}
				
				
				String[][] statsArray = client.getRandomizerStatistics(randomizerName, parameters, sa.toString());
				
				int paramSize = parameters.length;
				
				System.out.println("Allocation stats for the following stratum combination: ");
				for(int i = 0; i < paramSize; i++){
					Parameter p = parameters[i];
					System.out.println("Stratum: " + p.getKey() + " and Value: " + p.getValue());
				}
				
				int statsArrayLength = statsArray.length;
				
				for(int i = 0; i < statsArrayLength; i++){
					System.out.println(Long.parseLong(statsArray[i][1]) + " participants allocated to the " + statsArray[i][0] + " treatment arm.");
				}
				
				
			}
			
			System.out.println((new Integer(stratumComboCount)).toString() + " strata combinations present in this randomizer.");
			
		}
	
		/**
		 * Amends the stratum in the randomiser specified by the 'stratumToAugment' variable.
		 * Adds a new strata value to this - adds 'newStrataValue'.
		 * Saves the randomizer, and then reloads and saves the amended one as the randomiser that this object references.
		 * @throws Exception 
		 * @throws NotAuthorisedFault 
		 * @throws RandomizationFault 
		 * @throws DuplicateRandomizerFault 
		 * @throws SocketTimeoutException 
		 * @throws ConnectException 
		 */
		public void amendRandomizer() throws ConnectException, SocketTimeoutException, DuplicateRandomizerFault, RandomizationFault, NotAuthorisedFault, Exception{
			
			
			StratifiedRandomizer sRand = this.getRandomizer();
			
			List<Stratum> strata = sRand.getStrata();
			Stratum stratumToModify = null;
			int numCombinations = 1;
			for(Stratum stratum: strata){
				if(stratum.getName().equals(stratumToAugment)){
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
		        
		       //sRand.addNewStrataValue(stratumToModify, newStrataValue, "org.psygrid.randomization.model.hibernate.IBRpbrblRandomizer", seeds);
		        
			}
	
			this.getRandomizerClient().saveRandomizer(sRand, sa.toString());
		}
		
		/**
		 * Retrieves the randomizer that is specified by the 'randomizerName' variable.
		 * @return
		 */
		private StratifiedRandomizer getRandomizer() throws Exception {
			
			RandomizationClient client = this.getRandomizerClient();
			Randomizer rand = client.getRandomizer(randomizerName, sa.toString());
			return (StratifiedRandomizer)rand;	
		}
		
		
		

}

package org.psygrid.tools.addrandstratavalue;

import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import org.apache.axis.encoding.Base64;
import org.apache.axis.utils.Options;
import org.opensaml.SAMLAssertion;
import org.psygrid.esl.model.IFactory;
import org.psygrid.esl.model.IProject;
import org.psygrid.esl.model.IStrata;
import org.psygrid.esl.model.hibernate.HibernateFactory;
import org.psygrid.esl.services.client.EslClient;
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.psygrid.security.authentication.client.LoginClient;
import org.psygrid.security.authentication.service.LoginServicePortType;
import org.psygrid.security.components.net.PsyGridClientSocketFactory;
import org.psygrid.security.utils.PropertyUtilities;
import org.psygrid.www.xml.security.core.types.ProjectType;


public class AddNewCentreToStudyESL {
	
	private static EslClient client = new EslClient();
	private static IFactory factory = new HibernateFactory();
	private static LoginServicePortType aa1 = null;
	
	
	public static String projectCode = null;
	public static String centreName = null;
	public static String centreCode = null;
	public static String stratumName = null;
	

	/**
	 * Adds a new centre to the esl, and adds this to the randomization stratum, if the
	 * project randomizes by centre.
	 * @param args[0] - username delimiter (-u)
	 * @param args[1] - username
	 * @param args[2] - password delimiter (-w)
	 * @param args[3] - project code : remainingArgs[0]
	 * @param args[4] - centre name : remainingArgs[1]
	 * @param args[5] - centre code : remainingArgs[2]
	 * @param args[6] - stratum name : remainingArgs[3]
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		Properties properties = PropertyUtilities.getProperties("test.properties");
	
		System.out.println(properties.getProperty("org.psygrid.security.authentication.client.trustStoreLocation"));
		
		client = new EslClient(new URL(properties.getProperty("org.psygrid.esl.client.serviceURL")));

		SAMLAssertion sa = null;
		IProject project = null;
        try{
        	sa = login(args);
        	String saml = sa.toString();
        	

        	project = client.retrieveProjectByCode(projectCode, saml);
        	System.out.println("Project ID "+project.getId());
        	project = createGroups(project, centreName, centreCode);
        	
        	//TODO: BV Before running this, make SURE that strata value are represented with the
        	//centre code and not the centre name.
        	project = addGroupAsStrataValue(project, stratumName, centreCode);
        	client.saveProject(project, saml);
        	System.out.println("Groups have been setup");
        }
        catch(Exception ex){
            ex.printStackTrace();
        }

	}
	
	public static SAMLAssertion login(String[] args) throws Exception {

		System.setProperty("axis.socketSecureFactory",
		"org.psygrid.security.components.net.PsyGridClientSocketFactory");
		Options opts = new Options(args);
		String [] remainingArgs = opts.getRemainingArgs();
		projectCode = remainingArgs[0];
		centreName = remainingArgs[1];
		centreCode = remainingArgs[2];
		stratumName = remainingArgs[3];
		
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
		
		//TODO:  Make sure that this works before running on the live system.
		SAMLAssertion sa = qc.getSAMLAssertion(new ProjectType("?", remainingArgs[0], "?", "?", false));

		return sa;
	}
	
	/**
	 * Adds a new centre to the ESL project
	 * @param project
	 * @param centreName name of the centre to add
	 * @param centreCode the added centre's code
	 * @return
	 */
	private static IProject createGroups(IProject project, String centreName, String centreCode) {
		project.setGroup(factory.createGroup(centreName, centreCode));
		return project;
	}
	
	/**
	 * Adds a new strata value to the stratum name passed in.
	 * @param project
	 * @param stratumName - the name of the stratum to augment
	 * @param stratumValue - the stratum value to add
	 * @return
	 */
	private static IProject addGroupAsStrataValue(IProject project, String stratumName, String stratumValue){
		
		List<IStrata> strata = project.getRandomisation().getStrata();
		for(IStrata stratum : strata){
			if(stratum.getName().equals(stratumName)){
				stratum.getValues().add(stratumValue);
			}
		}
		
		return project;
		
	}


}

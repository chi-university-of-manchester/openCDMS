package org.psygrid.tools.addrandstratavalue;

import java.io.FileOutputStream;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import org.apache.axis.encoding.Base64;
import org.apache.axis.utils.Options;
import org.opensaml.SAMLAssertion;
import org.psygrid.esl.model.ICustomField;
import org.psygrid.esl.model.IProject;
import org.psygrid.esl.model.IStrata;
import org.psygrid.esl.services.client.EslClient;
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.psygrid.security.authentication.client.LoginClient;
import org.psygrid.security.authentication.service.LoginServicePortType;
import org.psygrid.security.components.net.PsyGridClientSocketFactory;
import org.psygrid.security.utils.PropertyUtilities;
import org.psygrid.www.xml.security.core.types.ProjectType;

public class AddNewCustomFieldValueToESL {

	private static EslClient client = new EslClient();
	
	private static LoginServicePortType aa1 = null;
	
	public static String customFieldName = null;
	
	public static String customFieldValue = null;
	
	public static String projectCode = null;
	
	public static void main(String[] args) throws Exception {
		
		Properties properties = PropertyUtilities.getProperties("test.properties");
	
		System.out.println(properties.getProperty("org.psygrid.security.authentication.client.trustStoreLocation"));
		
		client = new EslClient(new URL(properties.getProperty("org.psygrid.esl.client.serviceURL")));

		SAMLAssertion saml = login(args);
		String sa = saml.toString();
		
		IProject project = null;
		project = client.retrieveProjectByCode(projectCode, sa);
		
		AddNewCustomFieldValueToESL.addNewCustomFieldValue(customFieldName, customFieldValue, project);
		
		addNewCustomFieldValueAsStrataValue(project, customFieldName, customFieldValue);
		
		client.saveProject(project, sa);
    	System.out.println("ESL Project has been successfully modified");
	}
	
	/**
	 * Adds a new strata value to the stratum name passed in.
	 * @param project
	 * @param stratumName - the name of the stratum to augment
	 * @param stratumValue - the stratum value to add
	 * @return
	 */
	private static IProject addNewCustomFieldValueAsStrataValue(IProject project, String stratumName, String stratumValue){
		
		List<IStrata> strata = project.getRandomisation().getStrata();
		for(IStrata stratum : strata){
			if(stratum.getName().equals(stratumName)){
				stratum.getValues().add(stratumValue);
			}
		}
		
		return project;
		
	}
	
	private static void addNewCustomFieldValue(String customFieldName, String valueToAdd, IProject project) throws Exception{
		
		int numCustomFields = project.getCustomFieldCount();
		for(int i = 0; i < numCustomFields; i++){
			ICustomField cf = project.getCustomField(i);
			if(cf.getName().equals(customFieldName)){
				cf.addValue(valueToAdd);
				return;
			}
		}
		
		throw new Exception("Hey - didn't find the customField I was supposed to add the value to.");
		
	}
	
	public static SAMLAssertion login(String[] args) throws Exception {

		System.setProperty("axis.socketSecureFactory",
		"org.psygrid.security.components.net.PsyGridClientSocketFactory");
		Options opts = new Options(args);
		String [] remainingArgs = opts.getRemainingArgs();
		
		customFieldName = remainingArgs[0];
		customFieldValue = remainingArgs[1];
		projectCode = remainingArgs[2];
		
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
		SAMLAssertion sa = qc.getSAMLAssertion();

		return sa;
	}

	
}

package org.psygrid.security.attributeauthority.test;

import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.util.Properties;

import org.apache.axis.encoding.Base64;
import org.apache.axis.utils.Options;
import org.opensaml.SAMLAssertion;
import org.psygrid.security.attributeauthority.client.AAManagementClient;
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.psygrid.security.authentication.client.LoginClient;
import org.psygrid.security.authentication.service.LoginServicePortType;
import org.psygrid.security.components.net.PsyGridClientSocketFactory;
import org.psygrid.security.utils.PropertyUtilities;
import org.psygrid.www.xml.security.core.types.GroupAttributeType;

public class TestGroupAttributeFeature {

	private static LoginServicePortType aa1 = null;
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		/*
		System.setProperty("axis.socketSecureFactory",
		"org.psygrid.security.components.net.PsyGridClientSocketFactory");
		*/
		Options opts = new Options(args);
		String[] remaining = opts.getRemainingArgs();
		Properties properties = PropertyUtilities.getProperties("test.properties");
		System.out.println(properties.getProperty("org.psygrid.security.authentication.client.trustStoreLocation"));
		LoginClient tc = null;
		
		String projectCode = remaining[0];
		String groupCode = remaining[1];
		String userForename = remaining[2];
		String userSurname = remaining[3];
		
		/*
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
		//SAMLAssertion sa = qc.getSAMLAssertion();
		 * 
		 */

		AAManagementClient mc = new AAManagementClient("test.properties");
		AAQueryClient qc = new AAQueryClient("test.properties");
		GroupAttributeType grpAttrType = new GroupAttributeType("001", "The Pharm", "123 Lonley Road", "Surrey", "ML10 6LL");
		

		//boolean result1 = mc.getPort().addGroupAttributeToGroup(projectCode, groupCode, grpAttrType);
		
		//boolean result2 = mc.getPort().addGroupAttributeToUser(userForename, userSurname, projectCode, groupCode, grpAttrType);
		
		//qc.getPort().getEmailAddressForUserWithPrivileges(null, null, null);
		
		GroupAttributeType[] groupAttributeArrayForGroup = qc.getPort().getGroupAttributesForGroup(projectCode, groupCode);
		
		GroupAttributeType[] groupAttributeArrayForUser = qc.getPort().getGroupAttributesForUserInGroup(projectCode, groupCode, userForename, userSurname);
		
		int debugLine = -1;
		
		

	}

}

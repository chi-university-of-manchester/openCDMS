package org.psygrid.security.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.internet.InternetAddress;

import org.apache.axis.encoding.Base64;
import org.apache.axis.utils.Options;
import org.opensaml.SAMLAssertion;
import org.psygrid.dataimport.jaxb.imp.Grouptype;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.PGSecurityInvalidSAMLException;
import org.psygrid.security.PGSecuritySAMLVerificationException;
import org.psygrid.security.attributeauthority.client.AAManagementClient;
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.psygrid.security.authentication.client.LoginClient;
import org.psygrid.security.authentication.service.LoginServicePortType;
import org.psygrid.security.components.net.PsyGridClientSocketFactory;
import org.psygrid.www.xml.security.core.types.AttributeType;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.RoleType;
import org.psygrid.www.xml.security.core.types.UserPrivilegesType;

public class OutputUserInfoForProject {

	private static LoginServicePortType aa1 = null;
	
	/**
	 * 
	 * This class outputs the users, their roles and groups to the console.
	 * 
	 * Arguments are: -u UserName -w password PROJECT_NAME PROJECT_CODE
	 * 
	 * @param args
	 * @throws PGSecurityInvalidSAMLException 
	 * @throws PGSecuritySAMLVerificationException 
	 * @throws PGSecurityException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException, PGSecurityException, PGSecuritySAMLVerificationException, PGSecurityInvalidSAMLException {
		
		
		System.setProperty("axis.socketSecureFactory",
		"org.psygrid.security.components.net.PsyGridClientSocketFactory");
		
		Options opts = new Options(args);
		String[] remaining = opts.getRemainingArgs();
		
		String saml = doLogin(opts);
		
		AAManagementClient mgmtClient = new AAManagementClient("test.properties");
		AAQueryClient queryClient = new AAQueryClient("test.properties");
		
		String projectName = remaining[0];
		String projectCode = remaining[1];
		ProjectType p = new ProjectType(projectName, projectCode, null, null, false);
		UserPrivilegesType[] usersStuff = mgmtClient.getPort().getUsersAndPrivilegesInProject(p);
		int numUsers = usersStuff.length;
		for(int i = 0; i < numUsers; i++){
			InternetAddress add = queryClient.lookUpEmailAddress(usersStuff[i].getUser().getDistinguishedName());
			outputUserInfo(usersStuff[i], add!=null?add.toString():"unknown");
		}
		

	}
	
	private static void outputUserInfo(UserPrivilegesType user, String emailAddress){
		
		String dn = user.getUser().getDistinguishedName();
		String name = dn.substring(3,dn.indexOf(','));
		System.out.println("User: " + name);
		System.out.println("Email address: " + emailAddress);
		
		AttributeType[] attributes = user.getAttribute();
		
		//the attribute will have either a list of roles, or a list of groups I think.
		int numAttributes = attributes.length;
		
		List<AttributeType> roles = new ArrayList<AttributeType>();
		List<AttributeType> centres = new ArrayList<AttributeType>();
		
		for(int i = 0; i < numAttributes; i++){
			if(attributeIsRole(attributes[i])){
				roles.add(attributes[i]);
			}
			if(attributeIsCentre(attributes[i])){
				centres.add(attributes[i]);
			}
		}
		
		System.out.println("Roles");
		if(roles.size() > 0){
			for(AttributeType a : roles){
				RoleType[] roles2 = a.getRole();
				int numRoles = roles2.length;
				for(int i = 0; i < numRoles; i++){
					RoleType rr = roles2[i];
					System.out.println("   " + rr.getName());
				}
			}
		}else{
			System.out.println("NONE");
		}
		
		System.out.println("Centres");
		if(centres.size() > 0){
			for(AttributeType a : centres){
				GroupType[] groups = a.getGroup();
				int numgroups = groups.length;
				for(int i = 0; i < numgroups; i++){
					GroupType g = groups[i];
					System.out.println("   " + g.getName());
				}
			}
		}else{
			System.out.println("NONE");
		}
		System.out.println();		
	}
	
	private static boolean attributeIsRole(AttributeType a){
		RoleType[] r = a.getRole();
		if(r != null && r.length > 0){
			return true;
		}else{
			return false;
		}
	}
	
	private static boolean attributeIsCentre(AttributeType a){
		GroupType[] g = a.getGroup();
		if(g != null && g.length > 0){
			return true;
		}else{
			return false;
		}
	}
	
	private static String doLogin(Options opts) throws IOException, PGSecurityException, PGSecuritySAMLVerificationException, PGSecurityInvalidSAMLException{
		
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

		if(sa!= null){
			return sa.toString();
		}else{
			return null;
		}
		
	}

}

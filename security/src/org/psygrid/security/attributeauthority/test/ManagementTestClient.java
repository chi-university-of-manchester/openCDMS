/*
Copyright (c) 2006-2008, The University of Manchester, UK.

This file is part of PsyGrid.

PsyGrid is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as 
published by the Free Software Foundation, either version 3 of 
the License, or (at your option) any later version.

PsyGrid is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public 
License along with PsyGrid.  If not, see <http://www.gnu.org/licenses/>.
*/


//Created on Oct 12, 2005 by John Ainsworth



package org.psygrid.security.attributeauthority.test;

import java.net.URL;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.configuration.FileProvider;
import org.apache.axis.utils.Options;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.security.attributeauthority.service.AttributeAuthorityManagementPortType;
import org.psygrid.security.attributeauthority.service.AttributeAuthorityService;
import org.psygrid.security.attributeauthority.service.AttributeAuthorityServiceLocator;
import org.psygrid.www.xml.security.core.types.AttributeType;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectDescriptionType;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.RoleType;
import org.psygrid.www.xml.security.core.types.UserPrivilegesType;
import org.psygrid.www.xml.security.core.types.UserType;

//import org.psygrid.security.attributeauthority.types.GetAttributesForUserInProjectResponse;

public class ManagementTestClient {

	    /**
	    * logger
	    */
	   //private static Logger _log = Logger.getLogger(TestClient.class.getName());
	   private static Log _log = LogFactory.getLog(ManagementTestClient.class);
	   private static String _trustStoreLocation = "trust.jks";
	   private static String _trustStorePassword = "password";
	   private static String _keyStoreLocation = "client.jks";
	   private static String _keyStorePassword = "password";
	   static String uidstring = "unique";
	   static int uid = 0;
	   
	   /**
	    * The singleton instance of this class.
	    */
	   protected static ManagementTestClient _instance = null;

	   /**
	    * The service port for calling methods on the Test web service.
	    */
	   protected AttributeAuthorityManagementPortType _port = null;

	   /**
	    * Protected constructor for gaining access to the Test port.
	    */
	   protected ManagementTestClient(EngineConfiguration config) throws Exception {
	       AttributeAuthorityService xService = new AttributeAuthorityServiceLocator(config);
	       try {
	           _port = xService.getAttributeAuthorityPortTypeManagementSOAPPort();
	       } catch (Exception e) {
	           throw new Exception(
	                   "Error getting AA port from service.", e);
	       }
	   }

	   /**
	    * Get instance of the Test client using the default service endpoint address.
	    *
			 * @param pTrustStoreLocation The complete path to the CA keystore.
			 * @param pTrustStorePassword The password to access to the CA keystore.
			 * @param pKeyStoreLocation The complete path to the user's keystore.
			 * @param pKeyStorePassword The password to access the user's keystore.
	    * @return The single instance of the Test client.
	    */
	   public static ManagementTestClient getInstance(EngineConfiguration config, String pTrustStoreLocation,
	                                                          String pTrustStorePassword, String pKeyStoreLocation,
	                                                          String pKeyStorePassword)
	           throws Exception {

	       _trustStoreLocation = pTrustStoreLocation;
	       _trustStorePassword = pTrustStorePassword;
	       _keyStoreLocation = pKeyStoreLocation;
	       _keyStorePassword = pKeyStorePassword;

	       if (_instance == null) _instance = new ManagementTestClient(config);
	       return _instance;
	   }

	   /**
	    * Get instance of the Test client to access the AA service
	    * at the specified endpoint address.
	    *
	    * @param pServiceEndpointAddress The URL of the service to access (e.g. http://<service address>:<port>/<path>).
	    * @return The single instance of the Test client.
	    */
	   public static ManagementTestClient getInstance(EngineConfiguration config, String pServiceEndpointAddress)
	           throws Exception {
	       return getInstance(config, pServiceEndpointAddress, null, null, null, null);
	   }

	   /**
	    * Get instance of the Test client to access the RUS service
	    * at the specified endpoint address.
	    *
	    * @param pServiceEndpointAddress The URL of the service to access (e.g. http://<service address>:<port>/<path>).
			 * @param pTrustStoreLocation The complete path to the CA keystore.
			 * @param pTrustStorePassword The password to access to the CA keystore.
			 * @param pKeyStoreLocation The complete path to the user's keystore.
			 * @param pKeyStorePassword The password to access the user's keystore.
	    * @return The single instance of the Test client.
	    */
	   public static ManagementTestClient getInstance(EngineConfiguration config, String pServiceEndpointAddress, String pTrustStoreLocation,
	                                                          String pTrustStorePassword, String pKeyStoreLocation,
	                                                          String pKeyStorePassword)
	           throws Exception {

	       _trustStoreLocation = pTrustStoreLocation;
	       _trustStorePassword = pTrustStorePassword;
	       _keyStoreLocation = pKeyStoreLocation;
	       _keyStorePassword = pKeyStorePassword;

	       if (_instance == null) _instance = new ManagementTestClient(config);
	       _instance.setEndpointAddress(pServiceEndpointAddress);
	       return _instance;
	   }

	   /**
	    * Set the endpoint address of the service to access. (Accessed through getInstance).
	    *
	    * @param pAddress Service endpoint address.
	    */
	   private void setEndpointAddress(String pAddress) {
					// Set the address of the service endpoint
	       ((javax.xml.rpc.Stub) _port)._setProperty(
	               javax.xml.rpc.Stub.ENDPOINT_ADDRESS_PROPERTY,
	               pAddress);
					// If service address begins with https then secure connection required
					// so set up the system properties for the user and CA keystores.
	       if (pAddress.startsWith("https")) {
	           if(_trustStoreLocation != null)
	               System.setProperty("javax.net.ssl.trustStore", _trustStoreLocation);
	           if(_trustStorePassword != null)
	               System.setProperty("javax.net.ssl.trustStorePassword",
	                   _trustStorePassword);
	           if(_keyStoreLocation != null){
	               System.setProperty("javax.net.ssl.keyStore", _keyStoreLocation);
	           }
	           if(_keyStorePassword != null){
	               System.setProperty("javax.net.ssl.keyStorePassword",
	                   _keyStorePassword);        
	           }
	           //System.setProperty("javax.net.debug", "ssl,handshake");
	       }
	   }
	   public AttributeAuthorityManagementPortType getPort(){
		 
	   		return _port;
	   }
	

    

    public static void main (String[] args) throws Exception {

        Options opts = new Options(args);
      
        EngineConfiguration config = new FileProvider("../etc/attribute-authority/client-deploy.wsdd");

        AttributeAuthorityService abs = new AttributeAuthorityServiceLocator(config);
        AttributeAuthorityManagementPortType aa1 = null;
    
        opts.setDefaultURL( abs.getAttributeAuthorityPortTypeManagementSOAPPortAddress() );
        System.out.println(opts.getURL());	
        URL serviceURL = new URL(opts.getURL());
  
        ManagementTestClient tc;
        try{
        tc = ManagementTestClient.getInstance(config, serviceURL.toString(), "../etc/keystores/trust.jks", "password", "../etc/keystores/client.jks", "password");
    			aa1 = tc.getPort();
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    		try{
        		for(int i=0;i<1;i++){ 
        			RoleType sa =  new RoleType("SystemAdministrator", null);
        			RoleType ci =  new RoleType("ChiefInvestigator", null);	
        			RoleType[] roles = new RoleType[]{sa, ci};
        			GroupType gt1 = new GroupType("subgroup1", "x25", "parent");
        			GroupType[] groups = new GroupType[]{gt1};
        			ProjectType project = new ProjectType("project"+i, "x1"+i, null, null, false);
        			ProjectDescriptionType pdt = new ProjectDescriptionType(project, groups, roles);
        			ProjectDescriptionType[] pdta = new ProjectDescriptionType[]{pdt};
        			aa1.addProject(pdta);
        		}
    		} catch (Exception e){
    			e.printStackTrace();
    		}
    		try{		
    				RoleType sa =  new RoleType("SystemAdministrator", null);
    				RoleType ci =  new RoleType("ChiefInvestigator", null);	
    				RoleType[] roles = new RoleType[]{sa, ci};
    				GroupType gt1 = new GroupType("subgroup1", "x25", "parent");
    				GroupType[] groups = new GroupType[]{gt1};
    				ProjectType project = new ProjectType("waltons", "wa1", null, null, false);
    				ProjectDescriptionType pdt = new ProjectDescriptionType(project, groups, roles);
    				ProjectDescriptionType[] pdta = new ProjectDescriptionType[]{pdt};
    				aa1.addProject(pdta);	
    		} catch (Exception e){
    			e.printStackTrace();
    		}
    		try{		
				RoleType sa =  new RoleType("DataAnalyst", null);
				RoleType ci =  new RoleType("ScientificResearchManager", null);	
				RoleType[] roles = new RoleType[]{sa, ci};
				GroupType gt1 = new GroupType("simpsub1", "x25", "parent");
				GroupType gt2 = new GroupType("simpsub2", "x25", "parent");
				GroupType[] groups = new GroupType[]{gt1, gt2};
				ProjectType project = new ProjectType("simpsons", "sp1", null, null, false);
				ProjectDescriptionType pdt = new ProjectDescriptionType(project, groups, roles);
				ProjectDescriptionType[] pdta = new ProjectDescriptionType[]{pdt};
				aa1.addProject(pdta);	
		} catch (Exception e){
			e.printStackTrace();
		}
		try{
		for(int i=0;i<10;i++){
    			RoleType sa =  new RoleType("SystemAdministrator", null);
    			RoleType ci =  new RoleType("ChiefInvestigator", null);	
    			RoleType[] roles = new RoleType[]{sa, ci};
    			GroupType gt1 = new GroupType("subgroup1", "x25", "parent");
    			GroupType[] groups = new GroupType[]{gt1};
    			ProjectType project = new ProjectType("waltons", "wa1", null, null, false);
    			AttributeType pdt = new AttributeType(project, groups, roles);
    			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, "john-boy"+i, null , null, null), new AttributeType[]{pdt});
    			boolean r = aa1.addUser(new UserPrivilegesType[]{urgt});
    			System.out.println(r);
    		}
		} catch (Exception e){
			e.printStackTrace();
		}
//   		boolean q = aa1.deleteProject(new DeleteProjectRequest(new String[]{"project0"}));
//		try{
//			RoleType sa =  new RoleType("SystemAdministrator", null);
//			RoleType ci =  new RoleType("ChiefInvestigator", null);	
//			RoleType[] roles = new RoleType[]{sa, ci};
//			GroupType gt1 = new GroupType("subgroup1", "x25", "parent");
//			GroupType[] groups = new GroupType[]{gt1};
//			ProjectType project = new ProjectType("waltons");
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType("john-boy0", new AttributeType[]{pdt});
//			aa1.deleteProjectFromUser(new UserPrivilegesType[]{urgt});
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//
//    		boolean rr = aa1.deleteUser(new DeleteUserRequest(new String[]{"john-boy4"}));
//   		boolean rs = aa1.deleteUser(new DeleteUserRequest(new String[]{"john-boy0"}));
//    		boolean rt = aa1.deleteUser(new DeleteUserRequest(new String[]{"john-boy1"}));
//    		boolean rtt = aa1.deleteUser(new DeleteUserRequest(new String[]{"john-boy7"}));
//    		boolean rttt = aa1.deleteUser(new DeleteUserRequest(new String[]{"john-boy3"}));
//	     	boolean s = aa1.deleteUser(new DeleteUserRequest(new String[]{"nobody"})); 		
//		
//		try{
//			RoleType[] roles = new RoleType[]{};
//			GroupType gt1 = new GroupType("simpsub1", "x25", "parent");
//			GroupType[] groups = new GroupType[]{gt1};
//			ProjectType project = new ProjectType("simpsons");
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType("john-boy1", new AttributeType[]{pdt});   		
//			aa1.addProjectToUser(new UserPrivilegesType[]{urgt});
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{};
//			GroupType gt1 = new GroupType("simpsub2", "x25", "parent");
//			GroupType[] groups = new GroupType[]{gt1};
//			ProjectType project = new ProjectType("simpsons");
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType("john-boy1", new AttributeType[]{pdt});   		
//			aa1.addGroupInProjectToUser(new UserPrivilegesType[]{urgt});
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//      
//      		try{
//      			RoleType[] roles = new RoleType[]{new RoleType("ScientificResearchManager", null)};
//    			ProjectType project = new ProjectType("simpsons");
//    			GroupType gt1 = new GroupType("subgroup1", "x25", "parent");
//    			GroupType[] groups = new GroupType[]{gt1};
//    			AttributeType pdt = new AttributeType(project, groups, roles);
//      			UserPrivilegesType urgt = new UserPrivilegesType("john-boy1", new AttributeType[]{pdt});   		
//      			aa1.addRoleInProjectToUser(new UserPrivilegesType[]{urgt});
//      		}catch (Exception e){
//      			e.printStackTrace();
//      		};
//      		
//      		try{
//      			RoleType[] roles = new RoleType[]{new RoleType("ClinicalResearchOfficer", null)};
//    			ProjectType project = new ProjectType("ainsworths");
//    			GroupType gt1 = new GroupType("subgroup1", "x25", "parent");
//    			GroupType[] groups = new GroupType[]{gt1};
//      			AttributeType pdt = new AttributeType(project, groups, roles);
//      			UserPrivilegesType urgt = new UserPrivilegesType("john-boy9", new AttributeType[]{pdt});   		
//      			aa1.updateUser(new UserPrivilegesType[]{urgt});
//      		}catch (Exception e){
//      			e.printStackTrace();
//      		};
//      		
//      		try{
//      			RoleType[] roles = new RoleType[]{new RoleType("SystemAdministrator", null)};
//    			ProjectType project = new ProjectType("waltons");
//    			GroupType gt1 = new GroupType("subgroup1", "x25", "parent");
//    			GroupType[] groups = new GroupType[]{gt1};
//      			AttributeType pdt = new AttributeType(project, groups, roles);
//      			UserPrivilegesType urgt = new UserPrivilegesType("john-boy2", new AttributeType[]{pdt});   		
//      			aa1.deleteRoleInProjectFromUser(new UserPrivilegesType[]{urgt});
//      		}catch (Exception e){
//      			e.printStackTrace();
//      		}; 		
//      		
//      		try{
//      			RoleType[] roles = new RoleType[]{new RoleType("SystemAdministrator", null)};
//    			ProjectType project = new ProjectType("waltons");
//    			GroupType gt1 = new GroupType("subgroup1", "x25", "parent");
//    			GroupType[] groups = new GroupType[]{gt1};
//      			AttributeType pdt = new AttributeType(project, null, null);
//      			UserPrivilegesType urgt = new UserPrivilegesType("john-boy3", new AttributeType[]{pdt});   		
//      			aa1.deleteProjectFromUser(new UserPrivilegesType[]{urgt});
//      		}catch (Exception e){
//      			e.printStackTrace();
//      		}; 	
//      		boolean t = aa1.deleteUser(new DeleteUserRequest(new String[]{"john-boy9"})); 
//     		System.out.println(aa1.retrieveConfiguration(true));
    }
}


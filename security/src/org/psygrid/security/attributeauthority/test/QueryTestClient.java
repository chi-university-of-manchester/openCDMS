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
import java.security.cert.X509Certificate;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.configuration.FileProvider;
import org.apache.axis.utils.Options;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.signature.XMLSignature;
import org.psygrid.security.attributeauthority.service.AttributeAuthorityQueryPortType;
import org.psygrid.security.attributeauthority.service.AttributeAuthorityService;
import org.psygrid.security.attributeauthority.service.AttributeAuthorityServiceLocator;
import org.psygrid.security.utils.XMLUtilities;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.RoleType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class QueryTestClient {
		public static final String DS_NAMESPACE = "http://www.w3.org/2000/09/xmldsig#";
	    /**
	    * logger
	    */
	   //private static Logger _log = Logger.getLogger(TestClient.class.getName());
	   private static Log _log = LogFactory.getLog(QueryTestClient.class);
	   private static String _trustStoreLocation = "trust.jks";
	   private static String _trustStorePassword = "password";
	   private static String _keyStoreLocation = "client.jks";
	   private static String _keyStorePassword = "password";

	   /**
	    * The singleton instance of this class.
	    */
	   protected static QueryTestClient _instance = null;

	   /**
	    * The service port for calling methods on the Test web service.
	    */
	   protected AttributeAuthorityQueryPortType _port = null;

	   /**
	    * Protected constructor for gaining access to the Test port.
	    */
	   protected QueryTestClient(EngineConfiguration config) throws Exception {
	       AttributeAuthorityService xService = new AttributeAuthorityServiceLocator(config);
	       try {
	           _port = xService.getAttributeAuthorityPortTypeQuerySOAPPort();
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
	   public static QueryTestClient getInstance(EngineConfiguration config, String pTrustStoreLocation,
	                                                          String pTrustStorePassword, String pKeyStoreLocation,
	                                                          String pKeyStorePassword)
	           throws Exception {

	       _trustStoreLocation = pTrustStoreLocation;
	       _trustStorePassword = pTrustStorePassword;
	       _keyStoreLocation = pKeyStoreLocation;
	       _keyStorePassword = pKeyStorePassword;

	       if (_instance == null) _instance = new QueryTestClient(config);
	       return _instance;
	   }

	   /**
	    * Get instance of the Test client to access the AA service
	    * at the specified endpoint address.
	    *
	    * @param pServiceEndpointAddress The URL of the service to access (e.g. http://<service address>:<port>/<path>).
	    * @return The single instance of the Test client.
	    */
	   public static QueryTestClient getInstance(EngineConfiguration config, String pServiceEndpointAddress)
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
	   public static QueryTestClient getInstance(EngineConfiguration config, String pServiceEndpointAddress, String pTrustStoreLocation,
	                                                          String pTrustStorePassword, String pKeyStoreLocation,
	                                                          String pKeyStorePassword)
	           throws Exception {

	       _trustStoreLocation = pTrustStoreLocation;
	       _trustStorePassword = pTrustStorePassword;
	       _keyStoreLocation = pKeyStoreLocation;
	       _keyStorePassword = pKeyStorePassword;

	       if (_instance == null) _instance = new QueryTestClient(config);
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
	   public AttributeAuthorityQueryPortType getPort(){
	   		return _port;
	   }
	

    

    public static void main (String[] args) throws Exception {

        Options opts = new Options(args);
      
        EngineConfiguration config = new FileProvider("../etc/attribute-authority/client-deploy.wsdd");

        AttributeAuthorityService abs = new AttributeAuthorityServiceLocator(config);

        AttributeAuthorityQueryPortType aa1 = null;
    
        opts.setDefaultURL( abs.getAttributeAuthorityPortTypeQuerySOAPPortAddress() );
        System.out.println(opts.getURL());	
        URL serviceURL = new URL(opts.getURL());
  
        QueryTestClient tc;
        try{
        tc = QueryTestClient.getInstance(config, serviceURL.toString(), "trustc.jks", "password", "myProxy.jks", "password");
    			aa1 = tc.getPort();
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    		String x1 = aa1.getMyAttributesInProject(new ProjectType("SYSTEM","-1", null, null, true));
    		System.out.println(x1);
    		aa1.getProjects();
    		aa1.getGroupsInProject(new ProjectType("waltons", "wa1", null, null, false));
    		aa1.getUsersInGroupInProject(new GroupType("subgroup1", "x25", "parent"), new ProjectType("waltons", "wa1", null, null, false));
       	aa1.getUsersInProjectWithRole(new RoleType("ChiefInvestigator", null), new ProjectType("waltons", "wa1", null, null, false));
      	aa1.getUsersInGroupInProjectWithRole(new GroupType("subgroup1", "x25", "parent"),new RoleType("ChiefInvestigator", null), new ProjectType("waltons", "wa1", null, null, false));
      	String r = aa1.getAttributesForUserInProject("john-boy1", new ProjectType("waltons", "wa1", null, null, false));
		System.out.println(r);
		String r1 = aa1.getAttributesForUser("john-boy8");
		
		
		System.out.println("start1");
		r1="<Signature xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\"><SignedInfo><CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/><SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\"/><Reference URI=\"#eb3a4953912e740e773bc34d792cbf17\"><Transforms><Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\"/><Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"><ec:InclusiveNamespaces PrefixList=\"code ds kind rw saml samlp typens #default\" xmlns:ec=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/></Transform></Transforms><DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"/><DigestValue>KPEcqCWiLcwcMI7qtRsDBpK59f8=</DigestValue></Reference></SignedInfo><SignatureValue> JW/N5vkJ8Su8LvcDChLtM0sTrNUj1ZwuKjm48hT80XbErAVDQAcv/EX0S4WYgSOdevM57QOz7DqS 9GRNnT1sdvPnPeNYaFAf0dz8pW0uITHepNRJBhDYnWiLhfTbvgu2qleix2ixf2mTvPUoDWNR90n3 o4myqm8q+MuK/ZO/P90mGC9Ve2VYWwD7ZkUZHA7Aw2SmozXAzw5opfY0Y+iQYyvzoT7h8g0nZYX1 OGZTvaEeW70zA+7mwAlk38X9KIy3GdpAghwZbKhQom2iejYkL++9cbqhp6/lZSeG1Odpyw+XYMsC GCJSNoX8QX3w7BNtOHhDLR3ZYCmlU0rxNVsMBw== </SignatureValue><KeyInfo><X509Data><X509Certificate> MIIDkzCCAnsCCQCYl/JM5R+OgjANBgkqhkiG9w0BAQUFADCBhjELMAkGA1UEBhMCdWsxCzAJBgNV BAgTAmFjMRMwEQYDVQQHEwptYW5jaGVzdGVyMRAwDgYDVQQKEwdwc3lncmlkMQswCQYDVQQLEwJD QTEXMBUGA1UEAxMOY2EucHN5Z3JpZC5vcmcxHTAbBgkqhkiG9w0BCQEWDmNhQHBzeWdyaWQub3Jn MB4XDTA1MTAxNDA4MzUzOFoXDTA4MDcxMDA4MzUzOFowgY8xCzAJBgNVBAYTAnVrMQswCQYDVQQI EwJhYzETMBEGA1UEBxMKbWFuY2hlc3RlcjEQMA4GA1UEChMHcHN5Z3JpZDEOMAwGA1UECxMFc2Vy djIxGjAYBgNVBAMTEXNlcnYyLnBzeWdyaWQub3JnMSAwHgYJKoZIhvcNAQkBFhFzZXJ2MkBwc3ln cmlkLm9yZzCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAL0kjumClk5h6FIVfj5UtjAI Nx1jHVXkRMSkaPF8xoDOErI0O13f8XL6/T+/BGq1kWzLxl1Cpb1FZL+pwMMRF8yaVOnaD5MygE7j 3smpdzHYM+rxStfxcbUar00c1ze5wlbA0cSQpG7GC2UxUkctLGWYlYtCubFMt1aG9Y1cazg9LmXs m01IO+ljccm3iY0OXuzGBT9lHjY0Of4YcVoH7Ta9fnqPeb1E2gqkY+W81IzYCFMSRI6Mfclzke9c p60uoZ/FigYEEieLM0IaAFVKWHxbBfFX9SRUI8OZVl4Tj9tDVZZ8R8zWXdH4XRKkCbAfQSbhjmga pS6H1lZ1brOnSy8CAwEAATANBgkqhkiG9w0BAQUFAAOCAQEAbO/z8sWgCkAAgnl0A8p6YYulL4z3 Yz7t4ywovj9twRkGhNZk8ZFfDZH3UyHwryic6VDfKPIBAADgKBuv9h6IQJjEK9gVeHKnaFQEaXZi KyV41MAsAc0dNhYZXzbUghV7PSsffiD+ZtvqHlJnOKeNLLv49MHlvbecb8AQC9+z0nzNrktqoTIq cDX7E8a0dQgHOYWaPAz752H0k9TDNRkyhy0DMBpfhLERi+l1cQwNO8pGXCLQUR4z9tzvLH7js6Kt Gr50XcEU8BSml582o4DL2zzzcmTGJO1iu7jZ6Qy1cu5sOfjNdpdc3TjfL3aG63wHhPZcEHDjnUvY Atu++6GM6w== </X509Certificate></X509Data></KeyInfo></Signature>";
		Document doc = new XMLUtilities().toDocument(r1, false, null);
		System.out.println("start2");
		Element e = doc.getDocumentElement();
		System.out.println("start3");
		NodeList  nl=e.getElementsByTagName("ds:Signature");
		System.out.println("length = " +nl.getLength());
		if(e==null){
			System.out.println("failed");
		}
		Element ds = (Element)nl.item(0);
		System.out.println(XMLUtilities.xmlFragmentToString(e));
		XMLSignature xmlsig = new XMLSignature(e, null);
		System.out.println("start4");
		KeyInfo info = xmlsig.getKeyInfo();
		System.out.println("start5");
		X509Certificate cert = info.getX509Certificate();
		System.out.println("start6");
		if (xmlsig.checkSignatureValue(cert)) {
			System.out.println("\n\n.......smoking.........\n\n");
		} else {
			System.out.println("\n\n.......damp squib.........\n\n");
		
		}

	}

}


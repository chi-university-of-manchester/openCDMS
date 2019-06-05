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



package org.psygrid.security.components.net;

import java.io.InputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.utils.Options;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.security.attributeauthority.service.AttributeAuthorityQueryPortType;
import org.psygrid.security.attributeauthority.service.AttributeAuthorityService;
import org.psygrid.security.attributeauthority.service.AttributeAuthorityServiceLocator;

public class NetworkDiagnostics {
		public static final String DS_NAMESPACE = "http://www.w3.org/2000/09/xmldsig#";
	    /**
	    * logger
	    */
	   //private static Logger _log = Logger.getLogger(TestClient.class.getName());
	   private static Log _log = LogFactory.getLog(NetworkDiagnostics.class);
	   private static String _trustStoreLocation = "trust.jks";
	   private static String _trustStorePassword = "password";
	   private static String _keyStoreLocation = "client.jks";
	   private static String _keyStorePassword = "password";

	   /**
	    * The singleton instance of this class.
	    */
	   protected static NetworkDiagnostics _instance = null;

	   /**
	    * The service port for calling methods on the Test web service.
	    */
	   protected AttributeAuthorityQueryPortType _port = null;

	   /**
	    * Protected constructor for gaining access to the Test port.
	    */
	   protected NetworkDiagnostics(EngineConfiguration config) throws Exception {
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
	   public static NetworkDiagnostics getInstance(EngineConfiguration config, String pTrustStoreLocation,
	                                                          String pTrustStorePassword, String pKeyStoreLocation,
	                                                          String pKeyStorePassword)
	           throws Exception {

	       _trustStoreLocation = pTrustStoreLocation;
	       _trustStorePassword = pTrustStorePassword;
	       _keyStoreLocation = pKeyStoreLocation;
	       _keyStorePassword = pKeyStorePassword;

	       if (_instance == null) _instance = new NetworkDiagnostics(config);
	       return _instance;
	   }

	   /**
	    * Get instance of the Test client to access the AA service
	    * at the specified endpoint address.
	    *
	    * @param pServiceEndpointAddress The URL of the service to access (e.g. http://<service address>:<port>/<path>).
	    * @return The single instance of the Test client.
	    */
	   public static NetworkDiagnostics getInstance(EngineConfiguration config, String pServiceEndpointAddress)
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
	   public static NetworkDiagnostics getInstance(EngineConfiguration config, String pServiceEndpointAddress, String pTrustStoreLocation,
	                                                          String pTrustStorePassword, String pKeyStoreLocation,
	                                                          String pKeyStorePassword)
	           throws Exception {

	       _trustStoreLocation = pTrustStoreLocation;
	       _trustStorePassword = pTrustStorePassword;
	       _keyStoreLocation = pKeyStoreLocation;
	       _keyStorePassword = pKeyStorePassword;

	       if (_instance == null) _instance = new NetworkDiagnostics(config);
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
      
        // 1. Try to connect without a proxy to http://nww.psygrid.nhs.uk using JRE HTPP
        // 2. Try to connect through a proxy to http://nww.psygrid.nhs.uk using JRE HTPP - authentication method will auto select
        // 3. Try to connect through/without a proxy to http://nww.psygrid.nhs.uk using Apache HttpClient

        
        final String authUser = "user";
        final String authPassword = "password";
        Authenticator.setDefault(
           new Authenticator() {
              public PasswordAuthentication getPasswordAuthentication() {
            	  this.getRequestingHost();
            	  this.getRequestingPort();
            	  this.getRequestingPrompt();
            	  this.getRequestingProtocol();
            	  this.getRequestingScheme();
            	  this.getRequestingSite();
            	  this.getRequestorType();
            	  this.getRequestingURL();
                 return new PasswordAuthentication(
                       authUser, authPassword.toCharArray());
              }
           }
        );
        
        String url = "https://developers.psygrid.org";
        URL server = new URL(url);
        
        // 1. Try to connect without a proxy to http://nww.psygrid.nhs.uk using JRE HTPP
        
        try{
        	HttpURLConnection connection = (
        			HttpURLConnection)server.openConnection();
        	connection.connect();
        	System.out.println("Response from plain http connection is:");
        	System.out.println(connection.getResponseCode());
        	System.out.println(connection.getResponseMessage());
       		System.out.println("Connection reports using proxy status of "+connection.usingProxy());
       		if(connection.getResponseCode()==connection.HTTP_OK){
        		InputStream in = connection.getInputStream();
        		byte[] b = new byte[4096];
        		while(in.read(b)!=-1){};
        		String page = new String(b);
        		connection.disconnect();
        		//System.out.print(page);
        		//System.exit(0);
        	}
        } catch (Exception e){
        	e.printStackTrace();
        	System.out.println(e.getMessage());
        }
        
        // 2. Try to connect through a proxy to http://nww.psygrid.nhs.uk using JRE HTPP - authentication method will auto select            
//      System.setProperty("http.proxyUser", authUser);
//      System.setProperty("http.proxyPassword", authPassword);
      String proxy = "kantara.smb.man.ac.uk";
      int pport = 80;

      SocketAddress addr = new
      InetSocketAddress(proxy, pport);
      Proxy p = new Proxy(Proxy.Type.HTTP, addr);
        try{
        	HttpURLConnection connection = (
        			HttpURLConnection)server.openConnection(p);
        	connection.connect();
        	System.out.println("Response from proxied http connection is:");
        	System.out.println(connection.getResponseCode());
        	System.out.println(connection.getResponseMessage());
        	
        	if(connection.getResponseCode()==connection.HTTP_OK){
        		System.out.println("Connection reports using proxy status of "+connection.usingProxy());
        		InputStream in = connection.getInputStream();
        		byte[] b = new byte[4096];
        		while(in.read(b)!=-1){};
        		connection.disconnect();
        		String page = new String(b);
        		System.out.print(page);
        		System.exit(0);
        	}
        } catch (Exception e){
        	e.printStackTrace();
        	System.out.println(e.getMessage());
        }     
        // 3. Try to connect through/without a proxy to http://nww.psygrid.nhs.uk using Apache HttpClient

}
}

class MyAuthenticator extends Authenticator {

    public PasswordAuthentication getPasswordAuthentication () {

        return new PasswordAuthentication ("user", "pass1".toCharArray());
    }
}

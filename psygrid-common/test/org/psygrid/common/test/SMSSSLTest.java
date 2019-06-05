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

package org.psygrid.common.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 * Test the Kapow! SMS sending service using HTTPS
 * 
 * @author Lucy Bridges
 *
 */
public class SMSSSLTest {
		
	private static final String URL = "https://www.kapow.co.uk/scripts/";

	private static final String SEND = "sendsms.php";
	private static final String CHK_STATUS = "chk_daystatus.php";
	private static final String CHK_CREDIT = "chk_credit.php";
	
	protected static final String _trustStoreLocation = "trust.jks";
	protected static final String _trustStorePassword = "password";
	protected static final String _keyStoreLocation = "client.jks";
	protected static final String _keyStorePassword = "password";

	public static void main(String[] args) {
		
		if(System.getProperty("javax.net.ssl.trustStore")==null){
			if (_trustStoreLocation != null)
				System.setProperty("javax.net.ssl.trustStore",
						_trustStoreLocation);
		} 
		if(System.getProperty("javax.net.ssl.trustStorePassword")==null){
			if (_trustStorePassword != null)
				System.setProperty("javax.net.ssl.trustStorePassword",
						_trustStorePassword);
		} 
		if(System.getProperty("javax.net.ssl.keyStore")==null){
			if (_keyStoreLocation != null) {
				System.setProperty("javax.net.ssl.keyStore", _keyStoreLocation);
			}
		} 
		if(System.getProperty("javax.net.ssl.keyStorePassword")==null){
			if (_keyStorePassword != null) {
				System.setProperty("javax.net.ssl.keyStorePassword",
						_keyStorePassword);
			}
		}
			
		
		
		/*
		 * Debugging info
		 */
		System.setProperty("javax.net.debug", "ssl,handshake");
		System.out.println("SSL Trust store is "+System.getProperty("javax.net.ssl.trustStore"));
		System.out.println("SSL Key store is "+System.getProperty("javax.net.ssl.keyStore"));
		
		
		testCheckLogs();
		testCheckCredit();
	}
	
	public static void testCheckLogs() {
		System.out.println("Checking Logs..");
		try {
			String results = checkAccount();

			if (results == null || results.equals("")) {
				System.out.println("No SMS Log today");
			}
			else {
				System.out.println(results);
			}
		}
		catch (Exception e) {
			System.err.println("Error occurred checking account logs. "+e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void testCheckCredit() {
		System.out.println("Checking Credit..");
		try {			
			int results = checkCredit();

			System.out.println("Kapow has "+results+" credits");

		}catch (Exception e) {
			e.printStackTrace();
			System.out.println("Problem occurred when attempting to check the credits for the SMS account "+e.getMessage());
		}
	}
		
	private static String checkAccount() throws Exception {
		
		String url = URL;
		HttpClient client = new HttpClient();
		//Use A Proxy
		client.getHostConfiguration().setProxy("172.31.39.245", 80);
		
		
		PostMethod method = new PostMethod(url+CHK_STATUS);
		NameValuePair[] data = {
				new NameValuePair("username", "psygrid"),
				new NameValuePair("password", "173913")
		};
		method.setRequestBody(data);


		String response = "";
		try {
			int status = client.executeMethod(method);

			if (status == 200) {
				InputStream is = method.getResponseBodyAsStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
			
				String line = "";
				while ( (line=br.readLine()) != null ) { 
					response += line;
				} 

				response = method.getResponseBodyAsString();
				//checkResponse(response, "checkAccount");
			}
			else {
				throw new Exception("Problem when checking SMS log. Problem was (status "+status+") "+method.getResponseBodyAsString());
			}
		} 
		catch (IOException ioe) {
			ioe.printStackTrace();
			throw new Exception("IOException occurred when checking SMS log", ioe);
		}
		catch (NullPointerException npe) {
			npe.printStackTrace();
		}
		finally {
			// release any connection resources used by the method
			method.releaseConnection();
		}

		return response;
	}

	/**
	 * Check the credit available
	 * 
	 * @return remainingCredit
	 */
	public static int checkCredit() throws Exception {

		String url = URL;


		HttpClient client = new HttpClient();

		//Use A Proxy
		client.getHostConfiguration().setProxy("172.31.39.245", 80);
		
		
		PostMethod creditChk = new PostMethod(url+CHK_CREDIT);
		NameValuePair[] data = {
				new NameValuePair("username", "psygrid"),
				new NameValuePair("password", "173913")
		};
		creditChk.setRequestBody(data);

		/**
		 * Credits remaining
		 */
		int credit = 0;
		try {
			int status = client.executeMethod(creditChk);

			// print the status and response
			//System.out.println(status + "\n" + creditChk.getResponseBodyAsString());

			if (status != 200) {
				throw new Exception("Problem occurred checking credit. HTTP status returned was "+status);
			}

			String result = creditChk.getResponseBodyAsString();
			//checkResponse(result, "checkCredit");

			credit = Integer.parseInt(result);		
		} 
		catch (IOException ioe) {
			throw new Exception("IOException occurred when checking credit.", ioe);
		}
		finally {
			// release any connection resources used by the method
			creditChk.releaseConnection();
		}

		return credit;
	}

}

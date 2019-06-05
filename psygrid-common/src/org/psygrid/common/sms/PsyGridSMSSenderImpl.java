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

package org.psygrid.common.sms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class to send SMS messages and perform admin functions for 
 * the Kapow! SMS service.
 * 
 * @author Lucy Bridges
 *
 */
public class PsyGridSMSSenderImpl {

	private static final Log mailLog = LogFactory.getLog("SMS."+PsyGridSMSSenderImpl.class);

	private static final Log sLog = LogFactory.getLog(PsyGridSMSSenderImpl.class);

	private SMSSettings settings;

	private static final String SEND = "sendsms.php";
	private static final String CHK_STATUS = "chk_daystatus.php";
	private static final String CHK_CREDIT = "chk_credit.php";

	private final String hostName = findHostName();

	/**
	 * Send an SMS message.
	 * 
	 * @param smsMessage
	 * @return creditRemaining
	 * @throws SMSException
	 */
	public int send(SMSMessage smsMessage) throws SMSException {

		int creditRemaining = sender(smsMessage);
		try{
			logSMS(smsMessage.getRecipientName(), smsMessage.getRecipientNumber());
		}
		catch(Exception ex){
			sLog.error(ex);
		}
		return creditRemaining;
	}

	/**
	 * Send multiple SMS messages.
	 * 
	 * @param smsMessages
	 * @return creditRemaining
	 * @throws SMSException
	 */
	public int send(SMSMessage[] smsMessages) throws SMSException {

		int creditRemaining = 0;
		try{
			for ( int j=0; j<smsMessages.length; j++ ){
				SMSMessage message = smsMessages[j];
				creditRemaining = send(message);
			}
		}
		catch(Exception ex){
			sLog.error(ex);            
		}
		return creditRemaining;
	}

	/**
	 * Check the messages sent today using the remote SMS account.
	 * 
	 * Returns the logs for all messages sent on the current day
	 * 
	 * @return results
	 * @throws SMSException
	 */
	public String checkAccount() throws SMSException {
		if (settings == null) {
			throw new SMSException("SMSSettings has not been set");
		}

		System.out.println("SSL Trust store is "+System.getProperty("javax.net.ssl.trustStore"));
		System.out.println("SSL Key store is "+System.getProperty("javax.net.ssl.keyStore"));

		
		//TODO Could also generate a monthly report using generatereport.html?&month=6&year=2007
		String url = settings.getServiceURL();
		HttpClient client = connect();
		PostMethod method = new PostMethod(url+CHK_STATUS);
		NameValuePair[] data = {
				new NameValuePair("username", settings.getServiceUsername()),
				new NameValuePair("password", settings.getServicePassword())
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

				//response = method.getResponseBodyAsString();
				checkResponse(response, "checkAccount");
			}
			else {
				throw new SMSException("Problem when checking SMS log. Problem was (status "+status+") "+method.getResponseBodyAsString());
			}
		} 
		catch (IOException ioe) {
			ioe.printStackTrace();
			throw new SMSException("IOException occurred when checking SMS log", ioe);
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
	 * Sends the given SMS message
	 * 
	 * @param message
	 * @return creditRemaining
	 * @throws SMSException
	 */
	private int sender(SMSMessage message) throws SMSException {
		if (settings == null) {
			throw new SMSException("SMSSettings has not been set");
		}
		if (message == null) {
			throw new SMSException("SMS Message has not been set");
		}

		String url = settings.getServiceURL();
		HttpClient client = connect();

		//Check HTTPClient is set and fields are set
		//Check message fields are set
		PostMethod method = new PostMethod(url+SEND);
		NameValuePair[] data = {
				new NameValuePair("username", settings.getServiceUsername()),
				new NameValuePair("password", settings.getServicePassword()),
				new NameValuePair("mobile", message.getRecipientNumber()),
				new NameValuePair("sms", message.getMessage())
		};
		method.setRequestBody(data);

		//Add a fromId if set
		if (message.getFromId() != null && !message.getFromId().equals("")) {
			NameValuePair[] data1 = {
					new NameValuePair("username", settings.getServiceUsername()),
					new NameValuePair("password", settings.getServicePassword()),
					new NameValuePair("mobile", message.getRecipientNumber()),
					new NameValuePair("sms", message.getMessage()),
					new NameValuePair("fromId", message.getFromId())
			};
			method.setRequestBody(data1);
		}

		//get return value
		String response = null;
		try {
			int status = client.executeMethod(method);

			if (status != 200) {
				throw new SMSException("Problem occurred calling method to send message. HTTP status returned was "+status);
			}

			response = method.getResponseBodyAsString();

			checkResponse(response, "settings");
		} 
		catch (IOException ioe) {
			throw new SMSException("IOException occurred when sending message for "+message.getRecipientName(), ioe);
		}
		finally {
			// release any connection resources used by the method
			method.releaseConnection();
		}

		return checkCredit();	//credits remaining
	}

	/**
	 * Setup a HTTP connection
	 * 
	 * @return httpClient
	 * @throws SMSException
	 */
	private HttpClient connect() throws SMSException {
		if (settings == null) {
			throw new SMSException("SMSSettings has not been set");
		}

		HttpClient client = new HttpClient();

		//connect
		if (settings.getProxy() == null || settings.getProxy().equals("")) {
			//No proxy req'd
		}
		else {
			//Use A Proxy
			int port = Integer.parseInt(settings.getProxyPort());
			client.getHostConfiguration().setProxy(settings.getProxy(), port);
			
			if (settings.getProxyAuthType() == null || settings.getProxyAuthType().equals("")) {
				return client;	//no proxy auth req'd 
			}

			
			Credentials credentials = null;
			AuthScope authScope = new AuthScope(hostName, 443, settings.getProxyRealm(), AuthScope.ANY_SCHEME);	//TODO this is currently untested
			
			if (settings.getProxyAuthType().equalsIgnoreCase("WINDOWS")) {
				credentials = new NTCredentials(settings.getProxyUsername(), settings.getProxyPassword()
						, hostName, settings.getProxyWindowsDomain());
			}
			else if (settings.getProxyAuthType().equalsIgnoreCase("BASIC")) {
				credentials = new UsernamePasswordCredentials(settings.getProxyUsername(), settings.getProxyPassword());
			}
			else if (settings.getProxyAuthType().equalsIgnoreCase("DIGEST")) {
				credentials = new UsernamePasswordCredentials(settings.getProxyUsername(), settings.getProxyUsername());
			}
			else {
				sLog.warn("Unknown proxy authentication scheme specified");
			}

			if (credentials != null) {
				client.getState().setProxyCredentials(authScope, credentials);	 
			}
		}

		return client;
	}

	/**
	 * Check the credit available
	 * 
	 * @return remainingCredit
	 */
	public int checkCredit() throws SMSException {
		if (settings == null) {
			throw new SMSException("SMSSettings has not been set");
		}

		String url = settings.getServiceURL();


		HttpClient client = connect();

		PostMethod creditChk = new PostMethod(url+CHK_CREDIT);
		NameValuePair[] data = {
				new NameValuePair("username", settings.getServiceUsername()),
				new NameValuePair("password", settings.getServicePassword())
		};
		creditChk.setRequestBody(data);

		/**
		 * Credits remaining
		 */
		int credit = 0;
		try {
			int status = client.executeMethod(creditChk);

			// print the status and response
			System.out.println(status + "\n" + creditChk.getResponseBodyAsString());

			if (status != 200) {
				throw new SMSException("Problem occurred checking credit. HTTP status returned was "+status);
			}

			String result = creditChk.getResponseBodyAsString();
			checkResponse(result, "checkCredit");

			credit = Integer.parseInt(result);		
		} 
		catch (IOException ioe) {
			throw new SMSException("IOException occurred when checking credit.", ioe);
		}
		finally {
			// release any connection resources used by the method
			creditChk.releaseConnection();
		}

		return credit;
	}

	private void logSMS(String recipientName, String number) throws Exception {
		StringBuilder msg = new StringBuilder();
		msg.append("SMS sent. To: ");
		msg.append(recipientName);
		msg.append(" ( ");
		msg.append(number);
		msg.append(" )");

		mailLog.info(msg.toString());
	}


	public SMSSettings getSettings() {
		return settings;
	}


	public void setSettings(SMSSettings sender) {
		this.settings = sender;
	}

	/**
	 * Find the host name 
	 * @return Host name
	 */
	private static String findHostName() {
		try {
			InetAddress addr = InetAddress.getLocalHost();
			// Get hostname
			if (sLog.isDebugEnabled()) {
				sLog.debug("hostname = "+addr.getHostName());
			}
			return addr.getHostName();
		} catch (UnknownHostException e) {
			sLog.error(e.getMessage());
		}
		return null;
	}

	/**
	 * Checks the response given by Kapow!
	 * 
	 * Possible are:
	 * 
	 * OK       = message has been accepted and spooled for delivery.
	 * USERPASS = invalid username or password.
	 * NOCREDIT = account has no credits or credit limit has been reached.
	 * ERROR    = any other error has occurred.
	 * 
	 * @param response
	 * @param method
	 * @return boolean
	 * @throws SMSException
	 */
	private boolean checkResponse(String response, String method) throws SMSException {
		Pattern pattern = Pattern.compile("^Error:*", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(response);

		if (response == null) {
			throw new SMSException("Null response returned in method "+method);
		}
		else if (response.equals("USERPASS")) {
			throw new SMSException("Username or password was incorrect");
		}
		else if (response.equals("NOCREDIT")) {
			throw new SMSException("No Credit left");
		}
		else if (response.equals("ERROR")) {
			throw new SMSException("Error occurred in method "+method);
		}
		else if (matcher.find()) {
			String error = response.substring(matcher.end());
			throw new SMSException("Error occurred in method "+method+". Error was "+error);
		}

		return true;	
	}
}

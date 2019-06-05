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


//Created on Feb 9, 2006 by John Ainsworth
package org.psygrid.security.authentication.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Vector;

import org.globus.myproxy.MyProxyException;
import org.psygrid.security.PGSecurityException;

/**
 * @author jda
 * 
 */
public class MPTestClient {

	public static final String MYPROXY_PROTOCOL_VERSION = "MYPROXYv2";

	private static final String VERSION = "VERSION=" + MYPROXY_PROTOCOL_VERSION;

	private static final String RESPONSE = "RESPONSE=";

	private static final String ERROR = "ERROR=";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.setProperty("javax.net.ssl.trustStore", "../trust2.jks");

		System.setProperty("javax.net.ssl.trustStorePassword", "password");

		//System.setProperty("javax.net.ssl.keyStore", "../client2.jks");

		//System.setProperty("javax.net.ssl.keyStorePassword", "password");

		System.setProperty("javax.net.debug", "ssl,handshake");
		int port = 7512;
		String host = "atisha.smb.man.ac.uk";

		try {
			String cmd = "python myproxy_login.py --pshost=atisha.smb.man.ac.uk --psport=7512 --username=dsn --password=173913 -t300";
			Process p = Runtime.getRuntime().exec(cmd);
			if(p.waitFor()!=0){
				throw new PGSecurityException("MyProxy login script error");
			}
			InputStream in = p.getInputStream();
			InputStream err = p.getErrorStream();
			InputStreamReader rin = new InputStreamReader(in);
			BufferedReader bin = new BufferedReader(rin);
			Vector lines = new Vector();
			String line;
			boolean ok = false;
			for(;;){
				if(in.available()!=0){
					ok=true;
					break;
				} else if(err.available()!=0){
					ok=false;
					break;
				}
			}
			if(ok){
			while ((line = bin.readLine()) != null) {
				if(line==null){
					System.out.println("NULL!!!!!!!!!!");
					continue;
				}
				System.out.println("java "+line);
				lines.addElement(line);
			}
			} else {
				p.destroy();
				throw new PGSecurityException("MyProxy login script error");
			}

			
			String myPEM = null;
			for(int i=0;i<lines.size();i++){
				myPEM +=lines.get(i)+"\n";
			}
			System.out.println(myPEM);
			
			
			
			
			
//			Process p = Runtime.getRuntime().exec(cmd);
//			InputStream in = p.getInputStream();
//			InputStreamReader rin = new InputStreamReader(in);
//			BufferedReader bin = new BufferedReader(rin);
//			Vector lines = new Vector();
//			String line;
//			Thread.sleep(10000);
//			while((line = bin.readLine())!=null){
//				System.out.println(line);
//				lines.addElement(line);
//			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

//		 org.globus.myproxy.MyProxy myproxy = new
//		 org.globus.myproxy.MyProxy(host, port);     
//		 GlobusGSSCredentialImpl credential = null;
//		 try
//		 {
//		 credential = (GlobusGSSCredentialImpl) myproxy.get("dsn", "173913",
//		 1234567);
//		 }
//		 catch (MyProxyException mpe)
//		 {
//		 mpe.printStackTrace();
//		 System.out.println("Error retrieving credential from Myproxy repository: " + mpe.getMessage());
//		 }
				
		 try {
//		 SSLContext ctx = SSLContext.getInstance("SSLv3");
//		 TrustManagerFactory tmf =
//		 TrustManagerFactory.getInstance("SunX509", "SunJSSE");
//		 KeyStore ks = null;
//		 tmf.init(ks);
//		 ctx.init(null, tmf.getTrustManagers(), null);
//		 SSLSocketFactory sslFact = ctx.getSocketFactory();
//				
//		 SSLSocket s = (SSLSocket) sslFact.createSocket(host, port);
//		 SSLSocketFactory sslFact = (SSLSocketFactory) SSLSocketFactory
//		 .getDefault();
//		 SSLSocket s = (SSLSocket) sslFact.createSocket(host, port);
//		 s.setEnabledProtocols(new String[]{"SSLv3"});
//
//		 OutputStream out = s.getOutputStream();
//		 InputStream in = s.getInputStream();			
//		 Thread.sleep(10000);
//		 s.startHandshake();
//		 Thread.sleep(10000);
//		 out.write((byte)0);	 
//		 //build message
//		 GetParams gp = new GetParams();
//		 gp.setUserName("dsn");
//		 gp.setPassphrase("173913");
//		 gp.setLifetime(300);
//		 String msg = gp.makeRequest();
//		
//		 //System.out.println(msg.toString());
//			
//		 // send message
//		 out.write(msg.getBytes());
//		 out.flush();
//		 //System.out.println("Req sent:" + msg);
//		 Thread.sleep(5000);
//		 // wait for repsonse
//		 handleReply(in);
//		
//		 for(;;){
//						
//		 }
//		 //out.write(0);
//		 //System.out.println(in.read());
//		
		 }
		
		 catch (Exception e) {
			 e.printStackTrace();
		 }
	}

	private static InputStream handleReply(InputStream in) throws IOException,
			MyProxyException {
		String tmp = null;

		/*
		 * there was something weird here with the received protocol version
		 * sometimes. it contains an extra <32 byte. fixed it by using endsWith.
		 * now i read extra byte at the end of each message.
		 */

		// protocol version
		tmp = readLine(in);
		if (tmp == null) {
			throw new EOFException();
		}
		if (!tmp.endsWith(VERSION)) {
			throw new MyProxyException("Protocol version mismatch: " + tmp);
		}

		// response
		tmp = readLine(in);
		if (tmp == null) {
			throw new EOFException();
		}
		if (!tmp.startsWith(RESPONSE)) {
			throw new MyProxyException("Invalid reply: no response message");
		}

		boolean error = tmp.charAt(RESPONSE.length()) != '0';

		int avail = in.available();
		byte[] b = new byte[avail];
		in.read(b);

		ByteArrayInputStream inn = new ByteArrayInputStream(b);

		if (error) {
			StringBuffer errorStr = new StringBuffer();
			while ((tmp = readLine(inn)) != null) {
				if (tmp.startsWith(ERROR)) {
					if (errorStr.length() > 0)
						errorStr.append(' ');
					errorStr.append(tmp.substring(ERROR.length()));
				}
			}
			throw new MyProxyException(errorStr.toString());
		}

		return inn;
	}

	private static String readLine(InputStream in) throws IOException {
		int c, length = 0;

		c = in.read();
		if (c == -1) {
			return null;
		}

		StringBuffer buf = new StringBuffer();
		buf.append((char) c);

		while (true) {
			c = in.read();

			if (c == -1 || c == '\n' || length > 512) {
				break;
			} else if (c == '\r') {
				in.read();
				break;
			} else {
				buf.append((char) c);
				length++;
			}
		}
		String line = buf.toString();
		// log.debug("Received line: " + line);
		return line;
	}

	private static void close(OutputStream out, InputStream in, Socket sock) {
		try {
			if (out != null)
				out.close();
			if (in != null)
				in.close();
			if (sock != null)
				sock.close();
		} catch (IOException ee) {
		}
	}
}

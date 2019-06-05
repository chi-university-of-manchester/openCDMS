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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

/**
 * @author jda
 * 
 */
public class TestServer {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		System.setProperty("javax.net.ssl.trustStore",
				"trusts.jks");
		
		System.setProperty("javax.net.ssl.trustStorePassword",
				"password");
		
		System.setProperty("javax.net.ssl.keyStore", "server.jks");
		
		System.setProperty("javax.net.ssl.keyStorePassword",
				"Ki55inG8");

		System.setProperty("javax.net.debug", "ssl,handshake");
		int port = 8443;

		SSLServerSocket s;

		
		
		
		try {
			SSLServerSocketFactory sslSrvFact = (SSLServerSocketFactory) SSLServerSocketFactory
					.getDefault();
			s = (SSLServerSocket) sslSrvFact.createServerSocket(port);
			s.setEnabledProtocols(new String[]{"SSLv3"});
			s.setUseClientMode(false);
			s.setNeedClientAuth(true);
			SSLSocket c = (SSLSocket) s.accept();

			OutputStream out = c.getOutputStream();
			InputStream in = c.getInputStream();
			System.out.println("start.......");
			for(;;){
				int b=in.read();
				System.out.println((char)b);
				out.write(b);
			}

		}

		catch (IOException e) {
			e.printStackTrace();
		}
	}
}

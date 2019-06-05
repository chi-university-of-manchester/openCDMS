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

import java.io.InputStream;
import java.io.OutputStream;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * @author jda
 *
 */
public class TestClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.setProperty("javax.net.ssl.trustStore",
		"trustc.jks");

System.setProperty("javax.net.ssl.trustStorePassword",
		"password");

System.setProperty("javax.net.ssl.keyStore", "myProxy.jks");

System.setProperty("javax.net.ssl.keyStorePassword",
		"password");

System.setProperty("javax.net.debug", "ssl,handshake");
		int port = 8443;
		String host = "130.88.246.212";
 
		try {

			SSLSocketFactory sslFact = (SSLSocketFactory) SSLSocketFactory
			.getDefault();
			
			SSLSocket s = (SSLSocket) sslFact.createSocket(host, port);
			s.setEnabledProtocols(new String[]{"SSLv3"});
			//s.startHandshake();
			s.setUseClientMode(true);
			
			OutputStream out = s.getOutputStream();
			InputStream in = s.getInputStream();

			// Send messages to the server through
			// the OutputStream
			// Receive messages from the server
			// through the InputStream
			s.startHandshake();
			out.write(0);
			out.write(78);
			out.write(89);
//			for (;;) {
//				try {
//					//System.out.println("write.......");
//					out.write("abcdefghijklmnopqrstuvwxyz".getBytes());
//					int b = in.read();
//					System.out.println((char) b);
//   				System.out.println("done");
//					Thread.sleep(20);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
		}

		catch (Exception e) {
		}
	}
}

class InThread implements Runnable {
	InputStream in = null;

	InThread(InputStream i) {
		in = i;
	}

	public void run() {
		for (;;) {
			try {
				System.out.println("read.......");
				int b = in.read();
				System.out.println((char) b);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}

class OutThread implements Runnable {
	OutputStream out = null;

	OutThread(OutputStream o) {
		out = o;
	}

	public void run() {
		for (;;) {
			try {
				//System.out.println("write.......");
				out.write("abcdefghijklmnopqrstuvwxyz".getBytes());
		
//System.out.println("done");
Thread.sleep(20);
			} catch (Exception e) {
e.printStackTrace();
			}
		}
	}
}
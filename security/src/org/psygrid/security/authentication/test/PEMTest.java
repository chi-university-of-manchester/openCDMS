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


//Created on Feb 15, 2006 by John Ainsworth

package org.psygrid.security.authentication.test;

import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.cert.Certificate;

import org.bouncycastle.openssl.PEMReader;

/**
 * @author jda
 * 
 */
public class PEMTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		System.out.println(convertPEMToJKS("test.pem"));
	}

	 static String convertPEMToJKS(String cert) {
		Certificate[] chain = new Certificate[10];
		int countCert = 0;
		try {
			java.security.Security.addProvider(new
					org.bouncycastle.jce.provider.BouncyCastleProvider());
			FileReader rd = new FileReader(cert);
			PEMReader pemRd = new PEMReader(rd, null, org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME);

			Object o;
			KeyPair pair = null;
			System.out.println("looking for objects in PEM");
			while ((o = pemRd.readObject()) != null) {
				System.out.println("objectfound");
				if (o instanceof KeyPair) {
					System.out.println("found key pair");
					pair = (KeyPair) o;
				} else if (o instanceof Certificate) {
					System.out.println("found cert");
					Certificate c = (Certificate) o;
					chain[countCert] = c;
					System.out.println(c.getType());
					countCert++;
				}
			}
			
			org.bouncycastle.jce.provider.JDKPKCS12KeyStore bcks = new org.bouncycastle.jce.provider.JDKPKCS12KeyStore(
					org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME);
			
			Certificate[] newChain = new Certificate[countCert];
			for (int i = 0; i < countCert; i++) {
				newChain[i]= chain[i];
				System.out.println("length = "+countCert);
				System.out.println(chain[i].toString());
			}
			
			if (pair != null) {
				KeyStore newKs = KeyStore.getInstance("JKS");
				newKs.load(null, "password".toCharArray());
				//bcks.engineLoad(null, "password".toCharArray());
				newKs.setKeyEntry("my-key", pair.getPrivate(), "password"
						.toCharArray(), newChain);
				Certificate[] c = newKs.getCertificateChain("my-key");
				for (int i = 0; i < c.length; i++) {
					System.out.println("length = "+c.length);
					System.out.println(chain[i].toString());
				}
				
//				KeyStore kspkcs12 = KeyStore.getInstance("PKCS12");
//				kspkcs12.load(new FileInputStream("test.p12"), "password"
//						.toCharArray());
//				Enumeration eAliases = kspkcs12.aliases();
//				int n = 0;
//				while (eAliases.hasMoreElements()) {
//					String strAlias = (String) eAliases.nextElement();
//					System.err.println("Alias " + n++ + ": " + strAlias);
//					if (kspkcs12.isKeyEntry(strAlias)) {
//						System.err.println("Adding key for alias " + strAlias);
//						java.security.Key key = kspkcs12.getKey(strAlias,
//								"password".toCharArray());
//						Certificate chain2[] = kspkcs12
//								.getCertificateChain(strAlias);
//						for (int i = 0; i < chain2.length; i++) {
//							System.out.println("length = "+chain2.length);
//							System.out.println(chain2[i].toString());
//						}
//						newKs.setKeyEntry(strAlias, key, "password".toCharArray(), chain);
//					}
//				}
//				OutputStream out = new FileOutputStream("tout.jks");
//				newKs.store(out, "password".toCharArray());
//				out.close();

				ByteArrayOutputStream ostream = new ByteArrayOutputStream(8192);
				System.out.println(ostream.size());
				newKs.store(ostream, "password".toCharArray());
				ostream.close();
				return ostream.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}

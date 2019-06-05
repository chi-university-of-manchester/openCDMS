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
package org.psygrid.security.authentication.test;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.rmi.RemoteException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;

import org.apache.axis.encoding.Base64;
import org.apache.axis.utils.Options;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.openssl.PEMReader;
import org.opensaml.SAMLAssertion;
import org.psygrid.security.PGSecurityClockSyncException;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.psygrid.security.authentication.client.LoginClient;
import org.psygrid.security.authentication.service.LoginServicePortType;
import org.psygrid.security.authentication.service.ProcessingFaultMessage;
import org.psygrid.www.xml.security.core.types.ProjectType;

public class TestLoginClient {

	private static LoginServicePortType login = null;
	
	/**
	 * logger
	 */
	private static Log _log = LogFactory.getLog(TestLoginClient.class);

	static String convertPEMToJKS(String cert) {
		ArrayList<Certificate> chain = new ArrayList<Certificate>();
		try {
			StringReader rd = new StringReader(cert);
			PEMReader pemRd = new PEMReader(
					rd,
					null,
					org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME);
			Object o;
			KeyPair pair = null;

			while ((o = pemRd.readObject()) != null) {
				if (o instanceof KeyPair) {
					pair = (KeyPair) o;
				} else if (o instanceof Certificate) {
					Certificate c = (Certificate) o;
					chain.add(c);
				}
			}
			if (pair != null) {
				KeyStore newKs = KeyStore.getInstance("JKS");
				newKs.load(null, "secret".toCharArray());
				newKs.setKeyEntry("my-key", pair.getPrivate(), "secret"
						.toCharArray(), chain.toArray(new Certificate[chain
						.size()]));
				ByteArrayOutputStream ostream = new ByteArrayOutputStream();
				newKs.store(ostream, "secret".toCharArray());

				return ostream.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) throws Exception {
		System.setProperty("axis.socketSecureFactory",
		"org.psygrid.security.components.net.PsyGridClientSocketFactory");
		Options opts = new Options(args);

		LoginClient tc = null;
		java.security.Security
				.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		try {
			tc = new  LoginClient("test.properties");
			login = tc.getPort();
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
			FileOutputStream fos = new FileOutputStream("myProxy.jks");
			fos.write(ks);
			fos.flush();
			fos.close();
		}
		//System.out.println(tc.getPort().getTimeAtServer().toString());
		try{
			if(!checkKeyIsValid(60, "myProxy.jks", "my-key", "password".toCharArray())){
				System.out.println("key not valid...refresh");
				Date d = refreshKey("myProxy.jks", "my-key", "password".toCharArray(), opts.getUser(), opts.getPassword().toCharArray());
				System.out.println(d.toString());
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		AAQueryClient qc = new AAQueryClient("test.properties");
		System.out.println("starting.....");
		SAMLAssertion sa = qc.getSAMLAssertion(new ProjectType(null,"OLK", null, null, false));
		System.out.println(sa.toString());
	}

	static boolean checkKeyIsValid(int lifetimeRequired, String file, String alias,
			char[] password) throws PGSecurityClockSyncException,
			PGSecurityException {
		boolean result = true;
		try {
			KeyStore ks = KeyStore.getInstance("JKS");
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(file);
			} catch (FileNotFoundException fnfe) {
				return false;
			}
			ks.load(fis, password);
			Certificate[] certs = ks.getCertificateChain(alias);
			// Only support X.509
			X509Certificate x509 = (X509Certificate) certs[0];
			Date futureTime = new Date();
			futureTime.setTime(futureTime.getTime() + lifetimeRequired);
			try {
				x509.checkValidity(futureTime);
			} catch (CertificateExpiredException cee) {
				result = false;
			} catch (CertificateNotYetValidException cnyve) {
				throw new PGSecurityClockSyncException(
						"Date-time mismatch between client and server.");
			}
			return result;
		} catch (KeyStoreException kse) {
			throw new PGSecurityException(kse.getMessage());
		} catch (CertificateException ce) {
			throw new PGSecurityException(ce.getMessage());
		} catch (IOException ioe) {
			throw new PGSecurityException(ioe.getMessage());
		} catch (NoSuchAlgorithmException nsae) {
			throw new PGSecurityException(nsae.getMessage());
		}
	}

	static Date refreshKey(String file, String alias, char[] keyPassword, String user, char[] userPassword)
			throws PGSecurityException {
		short[] pwd = new short[userPassword.length];
		for (int i = 0; i < pwd.length; i++) {
			pwd[i] = (short) userPassword[i];
		}
		try {
			String credential = login.login(user, pwd);
			byte[] ba = null;
			if (credential != null) {
				byte[] ks = Base64.decode(credential);
				FileOutputStream fos = new FileOutputStream("myProxy.jks");
				fos.write(ks);
				fos.flush();
				fos.close();
			}		
			KeyStore ks = KeyStore.getInstance("JKS");
			FileInputStream fis  = new FileInputStream(file);
			ks.load(fis, keyPassword);
			Certificate[] certs = ks.getCertificateChain(alias);
			// Only support X.509
			X509Certificate x509 = (X509Certificate) certs[0];
			return x509.getNotAfter();
		} catch (ProcessingFaultMessage pfm) {
			throw new PGSecurityException(pfm.getMessage());
		} catch (RemoteException re) {
			throw new PGSecurityException(re.getMessage());
		} catch (IOException ioe) {
			throw new PGSecurityException(ioe.getMessage());
		} catch (KeyStoreException kse) {
			throw new PGSecurityException(kse.getMessage());
		} catch (CertificateException ce) {
			throw new PGSecurityException(ce.getMessage());
		} catch (NoSuchAlgorithmException nsae) {
			throw new PGSecurityException(nsae.getMessage());
		}
	}
}

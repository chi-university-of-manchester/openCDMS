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
// ========================================================================
// Copyright (c) 1999 Jason Gilbert
// ========================================================================


package org.psygrid.security.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import org.opensaml.SAMLException;
import org.psygrid.security.PGSecuritySAMLVerificationException;

/**
 * This class can be used to display the RFC2253 style DN
 *<PRE>
 *    usage: java KeystoreInfo {keystorefile} {inkeypassphrase} 
 *</PRE>
 *
 *
 * @author John Ainsworth
 */
public class KeystoreInfo
{
   public static void main(String[] args) throws Exception
   {
      if (args.length < 2) {
         System.err.println(
               "usage: java KeystoreInfo {keystorefile} {inkeypassphrase} ");
         System.exit(1);
      }

      File fileIn = new File(args[0]);


      if (!fileIn.canRead()) {
         System.err.println(
               "Unable to access input keystore: " + fileIn.getPath());
         System.exit(2);
      }


      KeyStore ksjks = KeyStore.getInstance("jks"); 

      char[] inphrase = args[1].toCharArray();

      ksjks.load(new FileInputStream(fileIn), inphrase);

		try {
			Enumeration<String> aliases = ksjks.aliases();
			while(aliases.hasMoreElements()){
				String alias = aliases.nextElement();
				X509Certificate c = (X509Certificate)ksjks.getCertificate(alias);
				c.checkValidity();
				System.out.println(c.getSubjectX500Principal().getName());
			}
		} catch (KeyStoreException kse){
			System.out.println("Certificate can not be verified " + kse.getMessage());
		}catch (CertificateNotYetValidException nve){
			System.out.println("Certificate can not be verified " + nve.getMessage());		
		}catch (CertificateExpiredException cee){
			System.out.println("Certificate can not be verified " + cee.getMessage());		
		}
   }

}

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
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

/**
 * This class can be used to import a key/certificate pair from a pkcs12 file
 * into a regular JKS format keystore for use with jetty and other java based
 * SSL applications, etc. 
 *<PRE>
 *    usage: java PKCS12Import {pkcs12file} {newjksfile} {inkeypassphrase} {outkeypassphrase}
 *</PRE>
 *
 * <P>
 * After execution you should have a
 * JKS keystore file that contains the private key and certificate that were in
 * the pkcs12
 * <P>
 * You can generate a pkcs12 file from PEM encoded certificate and key files
 * using the following openssl command:
 * <PRE>
 *    openssl pkcs12 -export -out keystore.pkcs12 -in www.crt -inkey www.key
 * </PRE>
 * then run:
 * <PRE>
 *    java PKCS12Import keystore.pkcs12 keytore.jks
 * </PRE>
 *
 * @author Jason Gilbert &lt;jason@doozer.com&gt;
 */
public class PKCS12toJKS
{
   public static void main(String[] args) throws Exception
   {
      if (args.length < 4) {
         System.err.println(
               "usage: java PKCS12Import {pkcs12file} {newjksfile} {inkeypassphrase} {outkeypassphrase}");
         System.exit(1);
      }

      File fileIn = new File(args[0]);
      File fileOut = new File(args[1]);


      if (!fileIn.canRead()) {
         System.err.println(
               "Unable to access input keystore: " + fileIn.getPath());
         System.exit(2);
      }

      if (fileOut.exists() && !fileOut.canWrite()) {
         System.err.println(
               "Output file is not writable: " + fileOut.getPath());
         System.exit(2);
      }

      KeyStore kspkcs12 = KeyStore.getInstance("pkcs12");
      KeyStore ksjks = KeyStore.getInstance("jks"); 

      LineNumberReader in = new LineNumberReader(new InputStreamReader(System.in));

      char[] inphrase = args[2].toCharArray();

      char[] outphrase = args[3].toCharArray();

      kspkcs12.load(new FileInputStream(fileIn), inphrase);

      ksjks.load(
            (fileOut.exists())
            ? new FileInputStream(fileOut) : null, outphrase);

      Enumeration eAliases = kspkcs12.aliases();
      int n = 0;
      while (eAliases.hasMoreElements()) {
         String strAlias = (String)eAliases.nextElement();
         System.err.println("Alias " + n++ + ": " + strAlias);

         if (kspkcs12.isKeyEntry(strAlias)) {
            System.err.println("Adding key for alias " + strAlias);
            Key key = kspkcs12.getKey(strAlias, inphrase);

            Certificate[] chain = kspkcs12.getCertificateChain(strAlias);

            ksjks.setKeyEntry(strAlias, key, outphrase, chain);
         }
      }

      OutputStream out = new FileOutputStream(fileOut);
      ksjks.store(out, outphrase);
      out.close();
   }

   static void dumpChain(Certificate[] chain)
   {
      for (int i = 0; i < chain.length; i++) {
         Certificate cert = chain[i];
         if (cert instanceof X509Certificate) {
            X509Certificate x509 = (X509Certificate)chain[i];
            System.err.println("subject: " + x509.getSubjectDN());
            System.err.println("issuer: " + x509.getIssuerDN());
         }
      }
   }

}

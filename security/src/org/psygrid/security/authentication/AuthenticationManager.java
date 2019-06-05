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


//Created on Feb 10, 2006 by John Ainsworth

package org.psygrid.security.authentication;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.StartTlsRequest;
import javax.naming.ldap.StartTlsResponse;
import javax.net.ssl.SSLSocketFactory;

import org.apache.axis.encoding.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.x509.X509CertificateStructure;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.bouncycastle.jce.PrincipalUtil;
import org.bouncycastle.jce.provider.X509CertificateObject;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.psygrid.security.LDAPPasswordHashScheme;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.utils.PasswordUtilities;
import org.psygrid.security.utils.PropertyUtilities;

/**
 * @author jda
 * 
 */
public class AuthenticationManager {
	
    static X509V3CertificateGenerator  v3CertGen = new X509V3CertificateGenerator();

	/**
	 * logger
	 */
	private static Log log = LogFactory.getLog(AuthenticationManager.class);

	static boolean initialised = false;

	static int lifetime = 300;

	static String _trustStoreLocation = "trust.jks";

	static String _trustStorePassword = "password";
	
	static String _caKeyStoreLocation = "";

	static String _caKeyStorePassword = "";
	
	static String _caKeyStoreAlias = "";
	
	static String clientKeyStoreAlias = "my-key";
	
	static PrivateKey caPrivKey = null;
	
	static X509Certificate caCert = null;
	
	static KeyStore caKeyStore = null;
	
	static String ldapDir = null;
	
	static String ldapBaseDN = null;
	
	static String ldapRoot = null;

	static String ldapRootPassword = null;

	static String ldapPasswordHash = null;

	static boolean ldapUseTLS = false;
	
	static Properties properties = null;
	
	static SSLSocketFactory sslFact = null;
	
	static void initialise(String propsFile) throws PGSecurityException {
		if (propsFile == null) {
			throw new PGSecurityException("properties cannot be null");
		}
		properties = PropertyUtilities.getProperties(propsFile);
		if (properties != null) {
			setSecurityProperties(
					properties
							.getProperty("org.psygrid.security.authentication.trustStoreLocation"),
					properties
							.getProperty("org.psygrid.security.authentication.trustStorePassword"),
					properties
							.getProperty("org.psygrid.security.authentication.caKeyStoreLocation"),
					properties
							.getProperty("org.psygrid.security.authentication.caKeyStorePassword"),					
					properties
							.getProperty("org.psygrid.security.authentication.caKeyStoreAlias")			
							);
			lifetime = Integer
					.parseInt(properties
							.getProperty("org.psygrid.security.authentication.lifetime"));
			ldapDir = properties
					.getProperty("org.psygrid.security.authentication.ldapDirectoryURL");
			ldapBaseDN = properties
					.getProperty("org.psygrid.security.authentication.ldapBaseDN");
			clientKeyStoreAlias = properties
					.getProperty("org.psygrid.security.authentication.clientKeyStoreAlias");
			ldapRoot = properties
					.getProperty("org.psygrid.security.authentication.ldapRoot");
			ldapRootPassword = properties
					.getProperty("org.psygrid.security.authentication.ldapRootPassword");
			ldapPasswordHash = properties
					.getProperty("org.psygrid.security.authentication.ldapPasswordHash");			
			ldapUseTLS = Boolean.parseBoolean(properties
					.getProperty("org.psygrid.security.authentication.ldapUseTLS"));	
		} else {
			throw new PGSecurityException("properties cannot be null");
		}
		
		java.security.Security.addProvider(new
				org.bouncycastle.jce.provider.BouncyCastleProvider());
		try{
			
	        caKeyStore = KeyStore.getInstance("JKS");
	        caKeyStore.load(new FileInputStream(_caKeyStoreLocation), _caKeyStorePassword.toCharArray());
	        
			//Ca certs and keys
	        caPrivKey = (PrivateKey)caKeyStore.getKey(_caKeyStoreAlias, _caKeyStorePassword.toCharArray());
	        if (caPrivKey == null) {
	            throw new RuntimeException("Got null key from keystore!");
	        }
	        // and get the certificate
	        caCert = (X509Certificate) caKeyStore.getCertificate(_caKeyStoreAlias);
	        if (caCert == null) {
	            throw new RuntimeException("Got null cert from keystore!");
	        }
	        caCert.verify(caCert.getPublicKey());
	        
		} catch (IOException ioe) {
			ioe.printStackTrace();
			throw new PGSecurityException(ioe.getMessage());
		} catch (KeyStoreException kse) {
			kse.printStackTrace();
			throw new PGSecurityException(kse.getMessage());
		} catch (CertificateException ce) {
			ce.printStackTrace();
			throw new PGSecurityException(ce.getMessage());
		} catch (NoSuchAlgorithmException nsae) {
			nsae.printStackTrace();
			throw new PGSecurityException(nsae.getMessage());
		} catch (NoSuchProviderException nspe) {
			nspe.printStackTrace();
			throw new PGSecurityException(nspe.getMessage());
		} catch (UnrecoverableKeyException uke) {
			uke.printStackTrace();
			throw new PGSecurityException(uke.getMessage());
		} catch (InvalidKeyException ike) {
			ike.printStackTrace();
			throw new PGSecurityException(ike.getMessage());
		} catch (SignatureException se) {
			se.printStackTrace();
			throw new PGSecurityException(se.getMessage());
		}
		     
		initialised = true;
	}

	AuthenticationManager() throws PGSecurityException {
		 if (!initialised) {
			throw new PGSecurityException("MyProxyLoginClient not initialised");
		}
	}

	/**
	 * Set the local copies of the security properties.
	 * 
	 * @param trustStoreLocation
	 * @param trustStorePassword
	 * @param caKeyStoreLocation
	 * @param caKeyStorePassword
	 * @param caKeyStoreAlias
	 */
	static void setSecurityProperties(String trustStoreLocation,
			String trustStorePassword, String caKeyStoreLocation, String caKeyStorePassword,
			String caKeyStoreAlias) {
		_trustStoreLocation = trustStoreLocation;
		_trustStorePassword = trustStorePassword;
		_caKeyStoreLocation = caKeyStoreLocation;
		_caKeyStorePassword = caKeyStorePassword;
		_caKeyStoreAlias    = caKeyStoreAlias;	
		//System.setProperty("javax.net.debug", "ssl,handshake");
	}


	String convertPEMToJKS(String cert, char[] password) {
		ArrayList<Certificate> chain = new ArrayList<Certificate>();
		try {
			StringReader rd = new StringReader(cert);
			PEMReader pemRd = new PEMReader(rd, null, org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME);
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
				newKs.load(null, password);
				newKs.setKeyEntry(clientKeyStoreAlias, pair.getPrivate(), password, chain.toArray(new Certificate[chain.size()]));
				ByteArrayOutputStream ostream = new ByteArrayOutputStream();
				newKs.store(ostream, password);
				return ostream.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Generates a key pair of given algorithm and strength.
	 * 
	 * @param algorithm
	 *            the algorithm of the key pair.
	 * @param bits
	 *            the strength
	 * @return <code>KeyPair</code> the generated key pair.
	 * @exception GeneralSecurityException
	 *                if something goes wrong.
	 */
	public static KeyPair generateKeyPair(String algorithm, int bits)
			throws GeneralSecurityException {
		KeyPairGenerator generator = null;

		generator = KeyPairGenerator.getInstance(algorithm);

		generator.initialize(bits);
		return generator.generateKeyPair();
	}

	/**
	 * Creates a certificate request from the specified subject DN and a key
	 * pair. The <I>"MD5WithRSAEncryption"</I> is used as the signing algorithm
	 * of the certificate request.
	 * 
	 * @param subject
	 *            the subject of the certificate request
	 * @param keyPair
	 *            the key pair of the certificate request
	 * @return the certificate request.
	 * @exception GeneralSecurityException
	 *                if security error occurs.
	 */
	public byte[] createCertificateRequest(String subject, KeyPair keyPair)
			throws GeneralSecurityException {
		X509Name name = new X509Name(subject);
		return createCertificateRequest(name, "MD5WithRSAEncryption", keyPair);
	}

	/**
	 * Creates a certificate request from the specified subject name, signing
	 * algorithm, and a key pair.
	 * 
	 * @param subjectDN
	 *            the subject name of the certificate request.
	 * @param sigAlgName
	 *            the signing algorithm name.
	 * @param keyPair
	 *            the key pair of the certificate request
	 * @return the certificate request.
	 * @exception GeneralSecurityException
	 *                if security error occurs.
	 */
	public byte[] createCertificateRequest(X509Name subjectDN,
			String sigAlgName, KeyPair keyPair) throws GeneralSecurityException {
		DERSet attrs = null;
		PKCS10CertificationRequest certReq = null;
		certReq = new PKCS10CertificationRequest(sigAlgName, subjectDN, keyPair
				.getPublic(), attrs, keyPair.getPrivate());

		return certReq.getEncoded();
	}

	/**
	 * Loads a X509 certificate from the specified input stream. Input stream
	 * must contain DER-encoded certificate.
	 * 
	 * @param in
	 *            the input stream to read the certificate from.
	 * @return <code>X509Certificate</code> the loaded certificate.
	 * @exception GeneralSecurityException
	 *                if certificate failed to load.
	 */
	public X509Certificate loadCertificate(InputStream in) throws IOException,
			GeneralSecurityException {
		ASN1InputStream derin = new ASN1InputStream(in);
		DERObject certInfo = derin.readObject();
		ASN1Sequence seq = ASN1Sequence.getInstance(certInfo);
		return new X509CertificateObject(new X509CertificateStructure(seq));
	}
	
	String getCallersIdentityByUID(String uid){

		String filter = "(uid="+uid+")";
		SearchControls searchCtrls = new SearchControls();
		searchCtrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		String result = null;
		LdapContext ctx = null;
		try{
			ctx = createLDAPContext(ldapRoot, ldapRootPassword.toCharArray());			
			NamingEnumeration answer = ctx.search(ldapBaseDN, filter, searchCtrls);
			while(answer.hasMoreElements()){
				result = ((SearchResult)answer.next()).getNameInNamespace();
				//there should only be one!
				break;
			}
		} catch (NamingException ne) {
			log.error(ne.getMessage(),ne);
		} catch (IOException io) {
			log.error("Failed TLS negotiation with LDAP server",io);
		} finally {
			closeLDAPContext(ctx);
		}
		return result;
	}
	public boolean checkUID(String uid) {
		boolean result = false;
		LdapContext ctx = null;
		try{
			ctx = createLDAPContext(ldapRoot, ldapRootPassword.toCharArray());

			try {
				String cn = this.getCallersIdentityByUID(uid);
				if (cn != null) {
					String[] attrIDs = { "uid" };
					Attributes attrs = ctx.getAttributes(cn, attrIDs);
					String userId = (String) attrs.get("uid").get();
					if (userId.equals(uid)) {
						result = true;
					}
				}
			} catch (NamingException ne) {
				log.info(ne.getMessage(),ne);
			}

		} catch (NamingException ne) {
			log.info(ne.getMessage(),ne);
		} catch (IOException io) {
			log.error("Failed TLS negotiation with LDAP server",io);
		} finally {
			closeLDAPContext(ctx);
		}
		return result;
	}
	
	public String login(String uid, char [] password) {
		
		String credential = null;
		String dn = this.getCallersIdentityByUID(uid);
		char [] ldapPassword = password;
		if(ldapPasswordHash!=null){
			if(ldapPasswordHash.equals(LDAPPasswordHashScheme.SHA.toString())){
				ldapPassword = PasswordUtilities.hashPassword(password, LDAPPasswordHashScheme.SHA, log);
			}
		}
		LdapContext ctx = null;
		try{
			ctx = createLDAPContext(dn, ldapPassword);
			try {
				String[] attrIDs = { "uid" };
				Attributes attrbs = ctx.getAttributes(dn, attrIDs);
				String userId = (String) attrbs.get("uid").get();
				if (userId.equals(uid)) {
					//generate the temporary X509 credential
					try{					
						//User key pair
						KeyPair keyPair = generateKeyPair("RSA", 1024);
						PublicKey userPubKey = keyPair.getPublic();

				        //
				        // create the certificate - version 3
				        //
				        v3CertGen.reset();

				        v3CertGen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
				        v3CertGen.setIssuerDN(PrincipalUtil.getSubjectX509Principal(caCert));
				        v3CertGen.setNotBefore(new Date(System.currentTimeMillis()));
				        v3CertGen.setNotAfter(new Date(System.currentTimeMillis() + (1000L * lifetime)));
				        v3CertGen.setSubjectDN(new X509Name(true, dn));
				        v3CertGen.setPublicKey(userPubKey);
				        v3CertGen.setSignatureAlgorithm("SHA1WithRSAEncryption");

				        X509Certificate userCert = v3CertGen.generateX509Certificate(caPrivKey);

				        userCert.checkValidity(new Date());

				        userCert.verify(caCert.getPublicKey());

				        X509Certificate[] chain = new X509Certificate[2];
				        // first the client, then the CA certificate
				        chain[0] = userCert;
				        chain[1] = caCert;       
				        
						KeyStore newKs = KeyStore.getInstance("JKS");
						newKs.load(null, password);
						newKs.setKeyEntry(clientKeyStoreAlias, keyPair.getPrivate(), password, chain);
						ByteArrayOutputStream ostream = new ByteArrayOutputStream();		    
						newKs.store(ostream, password);
						credential = Base64.encode(ostream.toByteArray());	
				        return credential;
						
					} catch (GeneralSecurityException gse){
						log.error(gse.getMessage(),gse);
					} catch (IOException ioe){
						log.error(ioe.getMessage(),ioe);
					}
				}
			} catch (NamingException ne) {
				log.error(ne.getMessage(),ne);
			}
		} 
		catch (AuthenticationException ne) {
			// Log incorrect logins as info rather than error.
			log.info(ne.getMessage(),ne);
		}
		catch (NamingException ne) {
			log.error(ne.getMessage(),ne);
		}
		catch (IOException io) {
			log.error("Failed TLS negotiation with LDAP server",io);
		} finally {
			closeLDAPContext(ctx);
		}
		return null;
	}
	protected LdapContext createLDAPContext(String cid, char[] password)
			throws NamingException, IOException {

		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, ldapDir);
		LdapContext ctx = null;

		if (ldapUseTLS) {

			StartTlsResponse tls = null;
			ctx = new InitialLdapContext(env, null);
			tls = (StartTlsResponse) ctx
					.extendedOperation(new StartTlsRequest());
			tls.negotiate();
			ctx.addToEnvironment(Context.SECURITY_AUTHENTICATION, "simple");
			ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, cid);
			ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, password);
			ctx.reconnect(null);

		} else {

			env.put(Context.SECURITY_AUTHENTICATION, "simple");
			env.put(Context.SECURITY_PRINCIPAL, cid);
			env.put(Context.SECURITY_CREDENTIALS, password);
			ctx = new InitialLdapContext(env, null);

		}
		return ctx;
	}

	protected void closeLDAPContext(LdapContext ctx) {
		try {
			if (ctx != null) {
				ctx.close();
			}
		} catch (NamingException ne) {
			log.info("LDAP context close failure");
		}
	}
}

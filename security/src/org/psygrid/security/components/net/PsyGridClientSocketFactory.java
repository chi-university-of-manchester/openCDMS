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

package org.psygrid.security.components.net;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Hashtable;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.X509KeyManager;

import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.components.net.BooleanHolder;
import org.apache.axis.components.net.TransportClientProperties;
import org.apache.axis.components.net.TransportClientPropertiesFactory;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.StringUtils;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.ProxyClient;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.logging.Log;
import org.psygrid.common.proxy.ProxyAuthenticationMethods;

/**
 * Adapted for use with dynamic keystores
 */
public class PsyGridClientSocketFactory extends DefaultJSSESocketFactory {

	/** Field log */
	protected static Log log = LogFactory
			.getLog(PsyGridClientSocketFactory.class.getName());

	/**
	 * This static flag is used to force the SSLContext to be reinitialised.
	 * Ideally, this should be an instance variable, not static, but it
	 * is not possible for the SecurityManager to access the instance.
	 * The side effect of using a static is that all instances will have their
	 * context invalidated and will reinitialise. This has no functional effect
	 * but does mean that reinitialisation may occur when it is not needed.
	 * However, there should only be one insatnce for a client and so normnally
	 * it will not be a problem.
	 * Bug#487
	 */
	private static boolean contextInvalid = true;
	
	private static ProxyAuthenticationMethods authScheme = ProxyAuthenticationMethods.NONE;
	
	private final String hostName = findHostName();
	
	private static String NTdomain = "";

	/**
	 * Constructor PsyGridClientSocketFactory
	 * 
	 * @param attributes
	 */
	public PsyGridClientSocketFactory(Hashtable attributes) {
		super(attributes);
	}

	/**
	 * Method getContext
	 * 
	 * @return
	 * 
	 * @throws Exception
	 */
	protected SSLContext getContext() throws Exception {
		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(new KeyManager[] { new CustomX509KeyManager() }, null,
				new java.security.SecureRandom());
		return sc;
	}

	/**
	 * creates a secure socket
	 * 
	 * @param host
	 * @param port
	 * @param otherHeaders
	 * @param useFullURL
	 * 
	 * @return Socket
	 * @throws Exception
	 */
	public Socket create(String host, int port, StringBuffer otherHeaders,
			BooleanHolder useFullURL) throws Exception {
		try {
			// Make sure we get a new context if it has been invalidated
			// Bug#487
			if (contextInvalid || sslFactory == null) {
				initFactory();
				contextInvalid = false;
			}

			if (port == -1) {
				port = 443;
			}

			TransportClientProperties tcp = TransportClientPropertiesFactory
					.create("https");

			boolean hostInNonProxyList = isHostInNonProxyList(host, tcp
					.getNonProxyHosts());

			Socket sslSocket = null;
			if (tcp.getProxyHost().length() == 0 || hostInNonProxyList) {
				// direct SSL connection
				sslSocket = sslFactory.createSocket(host, port);
			} else {
				// Default proxy port is 80, even for https
				int tunnelPort = (tcp.getProxyPort().length() != 0) ? Integer
						.parseInt(tcp.getProxyPort()) : 80;
				if (tunnelPort < 0)
					tunnelPort = 80;

				// Create the regular socket connection to the proxy
				Socket tunnel = null;
				if (authScheme.equals(ProxyAuthenticationMethods.NONE)) {
					tunnel = new Socket(tcp.getProxyHost(), tunnelPort);
					OutputStream tunnelOutputStream = tunnel.getOutputStream();
					PrintWriter out = new PrintWriter(new BufferedWriter(
							new OutputStreamWriter(tunnelOutputStream)));

					out.print("CONNECT " + host + ":" + port + " HTTP/1.0\r\n"
							+ "User-Agent: AxisClient");
					if (tcp.getProxyUser().length() != 0
							&& tcp.getProxyPassword().length() != 0) {
						// add basic authentication header for the proxy
						String encodedPassword = XMLUtils.base64encode((tcp
								.getProxyUser()
								+ ":" + tcp.getProxyPassword()).getBytes());
						out.print("\nProxy-Authorization: Basic "
								+ encodedPassword);
					}
					out.print("\nContent-Length: 0");
					out.print("\nPragma: no-cache");
					out.print("\r\n\r\n");
					out.flush();
					InputStream tunnelInputStream = tunnel.getInputStream();

					String replyStr = "";
					String replyHeaders = "";
					// Make sure to read all the response from the proxy to
					// prevent SSL negotiation failure
					// Response message terminated by two sequential newlines
					int newlinesSeen = 0;
					boolean headerDone = false; /* Done on first newline */

					while (newlinesSeen < 2) {
						int i = tunnelInputStream.read();

						if (i < 0) {
							throw new IOException("Unexpected EOF from proxy");
						}
						if (i == '\n') {
							headerDone = true;
							++newlinesSeen;
							replyHeaders += String.valueOf((char) i);
						} else if (i != '\r') {
							newlinesSeen = 0;
							if (!headerDone) {
								replyStr += String.valueOf((char) i);
							} else {
								replyHeaders += String.valueOf((char) i);
							}
						}
					}
					if (StringUtils.startsWithIgnoreWhitespaces("HTTP/1.0 200",
							replyStr)
							|| StringUtils.startsWithIgnoreWhitespaces(
									"HTTP/1.1 200", replyStr)) {
						// its all good
					} else {
						throw new IOException(Messages.getMessage(
								"cantTunnel00", new String[] {
										tcp.getProxyHost(), "" + tunnelPort,
										replyStr }));
					}
				} else {
					// proxy authentication needed
					// let the httpclient take care of it for us
					ProxyClient proxyclient = new ProxyClient();

					// set the host the proxy should create a connection to
					proxyclient.getHostConfiguration().setHost(host, port);
					
					// set the proxy host and port
					proxyclient.getHostConfiguration().setProxy(
							tcp.getProxyHost(), tunnelPort);
					Credentials cred = null;
					switch (authScheme) {
					case WINDOWS:
						cred = new NTCredentials(tcp.getProxyUser(), tcp
								.getProxyPassword(), hostName, NTdomain);
						break;
					case BASIC:
						cred = new NTCredentials(tcp.getProxyUser(), tcp
								.getProxyPassword(), hostName, NTdomain);
						break;
					case DIGEST:
						cred = new NTCredentials(tcp.getProxyUser(), tcp
								.getProxyPassword(), null, null);
						break;
					default:
						throw new RuntimeException(
								"Unknown authentication scheme");
					}
					// set the proxy credentials, only necessary for
					// authenticating proxies
					// TODO AuthScope is currently created so the credentials
					// apply to any host
					// It is more secure to restrict these values to the actual
					// target, but lets get it to work first!
					proxyclient.getState().setProxyCredentials(
							new AuthScope(null, -1, null), cred);

					// create the socket
					ProxyClient.ConnectResponse response = proxyclient
							.connect();

					if (response.getSocket() != null) {
						tunnel = response.getSocket();
					} else {
						if (log.isErrorEnabled()) {
							log.error("Setup Tunnel Authentication failed "
									+ tcp.getProxyHost() + ":" + tunnelPort);
						}
						throw new IOException(
								"cantTunnel: Authentication failure "
								+ tcp.getProxyHost() + " " + tunnelPort);
					}
				}
				sslSocket = sslFactory.createSocket(tunnel, host, port, true);
			}

			((SSLSocket) sslSocket).startHandshake();
			// End of Axis code

			SSLSocket sslSock = (SSLSocket) sslSocket;

			SSLSession session = sslSock.getSession();
			String hostname = session.getPeerHost();
			
//			Bug #635
//			try {
//				InetAddress addr = InetAddress.getByName(hostname);
//			} catch (UnknownHostException uhe) {
//				throw new UnknownHostException(
//						"Could not resolve SSL sessions " + "server hostname: "
//								+ hostname);
//			}

			javax.security.cert.X509Certificate[] certs = session
					.getPeerCertificateChain();
			if (certs == null || certs.length == 0)
				throw new SSLPeerUnverifiedException(
						"No server certificates found!");

			// get the servers DN in its string representation
			String dn = certs[0].getSubjectDN().getName();

			// get the common name from the first cert
			String cn = getCN(dn);
			if ((!hostname.equalsIgnoreCase(cn))
					|| (!host.equalsIgnoreCase(cn))) {
				throw new SSLPeerUnverifiedException(
						"HTTPS hostname invalid: expected '" + hostname
								+ "', received '" + cn + "'");
			}
			return sslSock;
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error(e.getClass().getName() + " " + e.getMessage(),e);
			}
			throw e;
		}
	}

	private String getCN(String dn) {
		int i = 0;
		i = dn.indexOf("CN=");
		if (i == -1) {
			return null;
		}
		// get the remaining DN without CN=
		dn = dn.substring(i + 3);
		char[] dncs = dn.toCharArray();
		for (i = 0; i < dncs.length; i++) {
			if (dncs[i] == ',' && i > 0 && dncs[i - 1] != '\\') {
				break;
			}
		}
		return dn.substring(0, i);
	}

    /**
     * Bug #487
     * This method is called by the SecurityManager to force a new context 
     * to be created for all new sockets when the keystore changes.	
     * 
     */
	public static void reinit() {
		//log.info("SSLContext has been invalidated");
		contextInvalid = true;
	}

	class CustomX509KeyManager implements X509KeyManager {
		X509KeyManager sunJSSEX509KeyManager;

		CustomX509KeyManager() throws Exception {
			reinit();
		}

		public String chooseClientAlias(String[] keyType, Principal[] issuers,
				Socket socket) {
			return sunJSSEX509KeyManager.chooseClientAlias(keyType, issuers,
					socket);
		}

		public String chooseServerAlias(String keyType, Principal[] issuers,
				Socket socket) {
			return sunJSSEX509KeyManager.chooseServerAlias(keyType, issuers,
					socket);
		}

		public String[] getServerAliases(String keyType, Principal[] issuers) {
			return sunJSSEX509KeyManager.getServerAliases(keyType, issuers);
		}

		public String[] getClientAliases(String keyType, Principal[] issuers) {
			return sunJSSEX509KeyManager.getClientAliases(keyType, issuers);
		}

		public PrivateKey getPrivateKey(String alias) {
			X509Certificate x509 = sunJSSEX509KeyManager
					.getCertificateChain(alias)[0];
			try {
				x509.checkValidity();
			} catch (CertificateExpiredException cee) {
				try {
					reinit();
				} catch (Exception e) {
					log.error(e);
					return null;
				}
			} catch (CertificateNotYetValidException cnyve) {
				log.warn("Date-time mismatch between client and server.",cnyve);
			}
			return sunJSSEX509KeyManager.getPrivateKey(alias);
		}

		public X509Certificate[] getCertificateChain(String alias) {
			X509Certificate x509 = sunJSSEX509KeyManager
					.getCertificateChain(alias)[0];
			try {
				x509.checkValidity();
			} catch (CertificateExpiredException cee) {
				try {
					reinit();
				} catch (Exception e) {
					log.error(e);
					return null;
				}
			} catch (CertificateNotYetValidException cnyve) {
				log.error("Date-time mismatch between client and server.",cnyve);
			}
			return sunJSSEX509KeyManager.getCertificateChain(alias);
		}

		private void reinit() throws Exception {
			KeyStore ks = KeyStore.getInstance("JKS");
			char[] password = System.getProperty(
					"javax.net.ssl.keyStorePassword").toCharArray();
			ks.load(new FileInputStream(System
					.getProperty("javax.net.ssl.keyStore")), password);
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			kmf.init(ks, password);

			KeyManager kms[] = kmf.getKeyManagers();

			/*
			 * Iterate over the returned keymanagers, look for an instance of
			 * X509KeyManager. If found, use that as our "default" key manager.
			 */
			for (int i = 0; i < kms.length; i++) {
				if (kms[i] instanceof X509KeyManager) {
					sunJSSEX509KeyManager = (X509KeyManager) kms[i];
					return;
				}
			}
		}
	}
	
    /**
     * Find the host name 
     * @return Host name
     */
    public static String findHostName() {
        try {
            InetAddress addr = InetAddress.getLocalHost();
            // Get hostname
			if (log.isDebugEnabled()) {
				log.debug("hostname = "+addr.getHostName());
			}
            return addr.getHostName();
        } catch (UnknownHostException e) {
			log.error(e.getMessage());
		}
		return null;
	}

	/**
	 * @return Returns the nTdomain.
	 */
	public static String getNTdomain() {
		return NTdomain;
	}

	/**
	 * @param tdomain The nTdomain to set.
	 */
	public static void setNTdomain(String tdomain) {
		NTdomain = tdomain;
	}

	/**
	 * @return Returns the authScheme.
	 */
	public static ProxyAuthenticationMethods getAuthScheme() {
		return authScheme;
	}

	/**
	 * @param authScheme The authScheme to set.
	 */
	public static void setAuthScheme(ProxyAuthenticationMethods authScheme) {
		PsyGridClientSocketFactory.authScheme = authScheme;
	}
}

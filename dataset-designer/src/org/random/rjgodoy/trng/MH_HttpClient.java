package org.random.rjgodoy.trng;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.ProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
//import javax.xml.ws.http.HTTPException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/** Mini HTTP-Client for accessing <A href="http://www.random.org">www.random.org</A>.<P>
 *
 * This class accesses the following services:
 * <DL>
 *  <DT>&nbsp;<B>The Quota Service</B>
 *  <DD><A href="http://www.random.org/quota?format=plain">http://www.random.org/quota?format=plain</A>
 *  <DT>&nbsp;<B>The Random Integer Generator</B>
 *  <DD><A href="http://www.random.org/integers/?min=-128&max=127&base=10&col=1000000000&format=plain&num=">/integers/?min=-128&max=127&base=10&col=1000000000&format=plain&num=</A>
 *      <i>number of bytes</i>
 * </DL>
 *
 * The initialization process uses several system properties for configuration (see {@link MH_SecureRandom}).<P>
 *
 * <B>Note</B>: this class has package-private scope because crypto-related helper classes in a provider package should have package-private scope.
 * Besides, instances of this class are NOT thread safe (there is only one instance of this class, and it use is controlled by a lock in {@link MH_SecureRandomSpi}).
 *
 * <P><font color=red><B>TODO:</B> It should be possible to configure which cipher-suite will be used.</font>
 *
 * @author Javier Godoy
 */
class MH_HttpClient {
	private final Log     LOG       = LogFactory.getLog("MH_HttpClient");
	private final String  HOST      = "www.random.org";
	private final String  QUOTA     = "/quota/?format=plain";
	private final String  FETCH     = "/integers/?min=-128&max=127&base=10&col=1000000000&format=plain&num=";
	private final String  JVM       = System.getProperty("java.runtime.version").replaceAll("[()<>@,;:\\\\\"/\\[\\]?={}\\p{Blank}\\p{Cntrl}]","_");
  private final String  VERSION   = "0.0.8";
  private final String  AUTHOR    = "rjgodoy@fich.unl.edu.ar";

	private String  USER;
	private int     SO_TIMEOUT     = 2*3600*1000;
	private boolean SSL_ENABLED;
	private String  SSL_PROTOCOL;
	private String  SSL_PROVIDER;
	private String  CERT_FILE;
	private int     MAX_REDIRECTS  = 99;

	private boolean keepalive   = true;
	private Socket  socket;
	private boolean first_ssl   = true;
	private long    quota       = Long.MIN_VALUE;
	private SSLSocketFactory ssl_factory;


	/**Initializes an instance of <code>MH_HttpClient</code>.
	 * The initialization process uses several system properties for configuration (see {@link MH_SecureRandom}).
	 */
	public MH_HttpClient() {
		String pkg = this.getClass().getPackage().getName()+".";

		String user          = System.getProperty(pkg+"user");
		String so_timeout    = System.getProperty(pkg+"timeout");
		String cert_file     = System.getProperty(pkg+"certfile");
		String ssl_provider  = System.getProperty(pkg+"ssl_provider");
		String ssl_protocol  = System.getProperty(pkg+"ssl_protocol");
		String max_redirects = System.getProperty(pkg+"max_redirects");

		USER = user;
		if (USER==null)
			throw new ProviderException(pkg+"user == null");
		if (!USER.matches("\\w+@(?:\\w+\\.)+\\w+"))
			throw new ProviderException(pkg+"user (email address expected)");

		try {
			if (so_timeout!=null)
				SO_TIMEOUT  = (int) Math.ceil(Double.parseDouble(so_timeout)*3600*1000);
			if (SO_TIMEOUT!=0&&SO_TIMEOUT<3600*1000)
				throw new ProviderException("timeout (illegal value) should be 0 or >= 3600000 ("+SO_TIMEOUT+")");
		} catch (NumberFormatException e) {
			  throw new ProviderException(pkg+"timeout (illegal value)",e);
		}

		if (cert_file!=null) {
			CERT_FILE=cert_file;
			if (!new File(cert_file).exists()) {
				LOG.error(pkg+"certfile (file does not exists: '"+cert_file+"'; will *not* check");
			  CERT_FILE=null;
			}
		}

		SSL_PROTOCOL = ssl_protocol;
		SSL_PROVIDER = ssl_provider;
		SSL_ENABLED  = ssl_protocol!=null;

		if (!SSL_ENABLED&&CERT_FILE!=null) {
			LOG.warn(pkg+"cerfile specified, but SSL disabled; will *not* check");
			CERT_FILE=null;
		}

		if (!SSL_ENABLED&&SSL_PROVIDER!=null) {
			LOG.warn(pkg+"ssl_provider specified, but no ssl_protocol; will *not* use SSL");
			SSL_ENABLED=false;
			SSL_PROVIDER=null;
		}

		if (max_redirects!=null) {
			MAX_REDIRECTS = Integer.parseInt(max_redirects);
			if (MAX_REDIRECTS<0)
				throw new ProviderException(pkg+"max_redirects should be >= 0");
		}

	 }

  /** Returns an estimated quota (avoids requesting the actual quota if it is already known to be positive).
   * If the estimated quota (calculated as the last known quota minus accumulated bit consumption)
   * is positive then the estimated value is immediatly returned, else the
   * {@link MH_HttpClient#checkQuota() actual quota} is requested.
   *
   * As a consequence, it is guarranteed that: <UL>
   * <LI> The estimated quota will be less or equal than the actual quota.
   * <LI> If the estimated quota is negative, the actual quota will be negative too (and equal to the estimated one).
   * </UL>
   *
   * @return The estimated quota, or <code>Long.MIN_VALUE</code> if an error occurs.
   */

	public long estimateQuota() {
		LOG.trace("estimateQuota()");
		if (quota<=0L) checkQuota();
		return quota;
	}

	/** Query and returns the available quota.<P>
	 * If this method fails (because a server or protocol error) returns Long.MIN_VALUE.
	 * If this method was successful, the {@link MH_HttpClient#estimateQuota() estimated quota} is updated and accumulated bit consumtion is reset.
	 *
	 * @return The actual quota, or <code>Long.MIN_VALUE</code> if an error occurs.
	 */
	public long checkQuota() {
		LOG.trace("checkQuota()");
		try {
			String response = get(QUOTA,0);
			if (response==null) return Long.MIN_VALUE;
			return quota=Long.parseLong(response);
		} catch (Exception e) {
			logException(e);
			return Long.MIN_VALUE;
		}
	}

	/** Fetches a block of bytes from www.random.org web-service.<P>
	 * If this method is successful, it should return <code>length</code>.
	 * If this method fails because of a service error (e.g.: the service is unavailable,
	 * the service has changed its interface, etc) it returns 0 (i.e. no bytes were read).
	 * If this method fails because an error when parsing the results (e.g.: no trailng TAB character) it returns the amount of bytes actually written to <code>bytes</code> buffer.
	 *
	 * @param bytes   destination buffer
	 * @param offset  offset at which to start storing bytes
	 * @param length  maximum number of bytes to read.
	 *
	 * @throws ArrayIndexOutOfBoundsException if any <code>offset</code> or <code>length</code> is negative,
	 *                                        if <code>offset</code> is greater than <code>bytes.length</code>
	 *                                     or if <code>length+offset</code> is greater than <code>bytes.length</code>.
	 * @throws NullPointerException if <code>bytes</code> is <code>null</code>.
	 * @return The number of bytes read.
	 */
	public int nextBytes(byte[] bytes,int offset,int length) {
		if (length==0) return 0;
		if (offset<0||offset>=bytes.length||length+offset>bytes.length||length<0)
			throw new ArrayIndexOutOfBoundsException("byte["+bytes.length+"]; offset="+offset+"; length="+length);

		try {
			quota-=length*8;
			LOG.trace("nextBytes(byte["+length+"])");
			String response = get(FETCH+length,0);
			if (response==null) return 0;
			int end=-1,start=0;
			for (int i=0;i<length;i++) {
				start=end+1;
				end=response.indexOf('\t',start);
				try {
					bytes[i+offset]=Byte.parseByte(response.substring(start, end));
				} catch (Exception e) {
					LOG.error(e);
					return i;
				}
			}
			return length;
		} catch (Exception e) {
			logException(e);
			return 0;
		}
	}


	//Prepares and commit an HTTP GET request
	//(re) initializes Socket if necessary
	//Return null if the request failed.
	private String get(String request_uri,int nredirects) throws IOException {
		if (nredirects>MAX_REDIRECTS) {
			LOG.error("Too much redirects!");
			return null;
		}
		LOG.trace("get(\""+request_uri+"\")");
		boolean trace = LOG.isTraceEnabled();

		keepalive=true;
		if (socket==null) {
			try {
				socket = createSocket();
			}
			 catch (NoSuchAlgorithmException  e) { LOG.error(e); ssl_factory=null; return null; }
			 catch (NoSuchProviderException   e) { LOG.error(e); ssl_factory=null; return null; }
			 catch (CertificateException      e) { LOG.error(e); ssl_factory=null; return null; }
			 catch (KeyStoreException         e) { LOG.error(e); ssl_factory=null; return null; }
			 catch (KeyManagementException    e) { LOG.error(e); ssl_factory=null; return null; }
			 catch (UnrecoverableKeyException e) { LOG.error(e); ssl_factory=null; return null; }
			LOG.info(
					 "Connected to "
					 +socket.getRemoteSocketAddress()
					 +",localport="+socket.getLocalPort()
			);
			if (SSL_ENABLED) configSSL((SSLSocket)socket);
			socket.setSoTimeout(SO_TIMEOUT);
			keepalive=false;
		}

		OutputStream out = socket.getOutputStream();

		String request =
			"User-Agent: "+AUTHOR+"/"+VERSION+" ("+USER+") Java/"+JVM+"\r\n"+
			"Accept: text/plain\r\n"+
			"Connection: keep-alive\r\n"+
			"Host: "+HOST+"\r\n\r\n";

		if (keepalive) {
			try {
				return commit(out, request_uri, request, trace, nredirects);
			}
		  	catch (IOException e){
		  		closeSocket();
		  }
		  return get(request_uri,nredirects);
		} else return commit(out, request_uri, request, trace, nredirects);
	}

	//Actually sends an HTTP request.
	//Return null if the request failed.
	private String commit(OutputStream out, String request_uri, String request, boolean trace, int nredirects) throws IOException {
		LOG.trace("commit()");

		request = "GET "+request_uri+" HTTP/1.1\r\n"+request;
		if (trace) for (String line:request.trim().split("\r\n")) LOG.trace("-> "+line);

		out.write(request.getBytes());

		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out.flush();
		String line = in.readLine();
		if (trace) LOG.trace("<- "+line);
		if (line==null) throw new IOException("Closed");
		int pos = line.indexOf(' ');
		if (pos<0||pos>line.length()) {
			LOG.error("Status-Line \""+line+"\"");
			closeSocket();
		}
		line=line.substring(pos+1).trim();
		pos = line.indexOf(' ');
		if (pos<0) pos=line.length();
		int sc;
		try {
			sc = Integer.parseInt(line.substring(0,pos));
		} catch (NumberFormatException nfe) {
			LOG.error("Status-Line \""+line+"\"");
			closeSocket();
			return null;
		}
		if (sc>=300&&sc<400) {
			if (!trace) LOG.warn("Status-Code "+line);
			String locationHeader[] = new String[]{null};
			getBody(in, trace,locationHeader);
			if (locationHeader[0]==null) {
				LOG.error("No location header");
				return null;
			}
			return get(locationHeader[0],nredirects+1);
		}
		if (sc!=200) {
			LOG.error("Status-Code "+line);
			String body=getBody(in, trace);
			if (body!=null) LOG.error(body.trim());
			//throw new HTTPException(0);
			throw new RuntimeException();
		}
		if (!trace) LOG.debug("Status-Code "+line);
		return getBody(in,trace);
	}

  //Returns the text/plain body of an HTTP request
	//Returns null if the request failed.
	private String getBody(BufferedReader in,boolean trace) throws IOException {
		LOG.trace("getBody()");
		String line;
		String content_type=null;
		int content_length=-1;
		boolean close=true;
		boolean chunked=false;
		while (true) {
			line = in.readLine();
			if (line==null) break;
			if (line.startsWith("Connection")) {
				int pos = line.indexOf(':');
				if (pos>0&&pos<line.length()) {
					if (line.substring(pos+1).trim().equalsIgnoreCase("close")) {
						close=true;
					}
					else if (line.substring(pos+1).trim().equalsIgnoreCase("keep-alive")) {
						close=false;
					}
				}
				if (trace) LOG.trace("<- "+line);
				continue;
			}
			if (line.startsWith("Content-Length")) {
				int pos = line.indexOf(':');
				if (pos>0&&pos<line.length()) {
					try {
						content_length=Integer.parseInt(line.substring(pos+1).trim());
					} catch (NumberFormatException nfe) {
						LOG.warn(nfe);
					}
				}
			}
			if (line.startsWith("Transfer-Encoding")) {
				int pos = line.indexOf(':');
				if (pos>0&&pos<line.length()) {
					chunked|=(line.substring(pos+1).trim().toLowerCase().equals("chunked"));
				}
			}
			if (content_type==null&&line.startsWith("Content-Type")) {
				int pos = line.indexOf(':');
				if (pos>0&&pos<line.length()) {
					content_type=line.substring(pos+1).trim();
				} else {
					LOG.warn(line);
				}
			}
			if (line.length()==0) {
				if (content_type==null) {
					LOG.warn("No Content-Type");
				} else {
					if (!content_type.startsWith("text/plain")) {
						LOG.error("Content-Type: "+content_type);
						closeSocket();
						return null;
					}
				}
				if (chunked) {
					StringBuffer sb = new StringBuffer();
					while (true) {
						line = in.readLine();
						if (trace) LOG.trace(line);
						int pos = line.indexOf(';');
						if (pos>0) line = line.substring(0,pos);
						int chunk_size;
						try {
							chunk_size = Integer.parseInt(line,16);
						}
						catch (NumberFormatException e) {
							LOG.error(e);
							closeSocket();
							return null;
						}
						if (chunk_size==0) {
							if (close) closeSocket();
							return sb.toString();
						}
						while (chunk_size>0) {
							line = in.readLine();
							sb.append(line);
							if (trace) LOG.trace(line);
							chunk_size-=line.length();
						}
					}
				}
				if (content_length<0) {
					LOG.warn("No Content-Length");
					line = in.readLine();
					if (trace) LOG.trace(line);
					closeSocket();
					return line;
				} else {
					if (!trace) LOG.debug("Content-Length: "+content_length);
					char[] chars = new char[content_length];
					int len = in.read(chars);
					line = new String(chars,0,len);
					if (trace) LOG.trace(line);
					if (close) closeSocket();
					return line;
				}
			}
			if (trace) LOG.trace("<- "+line);
		}
		LOG.error("No content or non text/plain content");
		closeSocket();
		return null;
	}

	private String getBody(BufferedReader in,boolean trace,String locationHeader[]) throws IOException {
		LOG.trace("getBody()");
		String line;
		String content_type=null;
		int content_length=-1;
		boolean close=false;
		while (true) {
			line = in.readLine();
			if (line==null) break;
			if (line.startsWith("Connection")) {
				int pos = line.indexOf(':');
				if (pos>0&&pos<line.length()) {
					if (line.substring(pos+1).trim().equalsIgnoreCase("close")) {
						LOG.trace(line);
						close=true;
						continue;
					}
				}
			}
			if (locationHeader!=null && line.startsWith("Location")) {
				int pos = line.indexOf(':');
				if (pos>0&&pos<line.length()) {
					if (pos>0&&pos<line.length()) locationHeader[0]=line.substring(pos+1).trim();
					LOG.warn(line);
				}
			}
			if (line.startsWith("Content-Length")) {
				int pos = line.indexOf(':');
				if (pos>0&&pos<line.length()) {
					try {
						content_length=Integer.parseInt(line.substring(pos+1).trim());
					} catch (NumberFormatException nfe) {
						LOG.warn(nfe);
					}
				}
			}
			if (content_type==null&&line.startsWith("Content-Type")) {
				int pos = line.indexOf(':');
				if (pos>0&&pos<line.length()) {
					content_type=line.substring(pos+1).trim();
				} else {
					LOG.warn(line);
				}
			}
			if (line.length()==0) {
				if (content_type==null) {
					LOG.warn("No Content-Type");
				} else {
					if (!content_type.startsWith("text/plain")) {
						LOG.error("Content-Type: "+content_type);
						closeSocket();
						return null;
					}
				}
				if (content_length<0) {
					LOG.warn("No Content-Length");
					line = in.readLine();
					if (trace) LOG.trace(line);
					closeSocket();
					return line;
				} else {
					if (!trace) LOG.debug("Content-Length: "+content_length);
					char[] chars = new char[content_length];
					int len = in.read(chars);
					line = new String(chars,0,len);
					if (trace) LOG.trace(line);
					if (close) closeSocket();;
					return line;
				}
			}
			if (trace) LOG.trace(line);
		}
		if (locationHeader==null) LOG.error("No content or non text/plain content");
		if (close) closeSocket();
		return null;
	}

	private void logException(Exception ex) {
		LOG.error(ex);
		StackTraceElement ste1[] = ex.getStackTrace();
		if (LOG.isDebugEnabled()) {
			for (StackTraceElement e:ste1)
				LOG.trace(e);
		} else {
			StackTraceElement ste2[] = new Throwable().getStackTrace();
			if (ste1!=null&&ste2!=null)
				for (int i=0,n=ste1.length-ste2.length;i<n;i++)
					LOG.error(ste1[i]);
		}
	}

	//Configures SSL enabled Protocols and Cipher-Suites
	//Performs the SSL handshake
	private void configSSL(SSLSocket socket) throws IOException {
		if (first_ssl && LOG.isDebugEnabled()) {
			for (String s:socket.getEnabledProtocols())    LOG.debug("SSL Enabled Protocol "+s);
			for (String s:socket.getEnabledCipherSuites()) LOG.debug("SSL Enabled CipherSuite "+s);
			first_ssl=false;
			LOG.debug("SSL handshake started");
		}
		try {socket.startHandshake();}
		catch (SSLHandshakeException e) {
			e.setStackTrace(new StackTraceElement[0]);
			throw e;
		}
		LOG.info("Using "+socket.getSession().getProtocol()+" "+socket.getSession().getCipherSuite());
	}


	//Creates a plain (TCP) socket or a SSL Socket
	private Socket createSocket()
		throws NoSuchAlgorithmException,
		       NoSuchProviderException,
		       CertificateException,
		       KeyStoreException,
		       KeyManagementException,
		       UnrecoverableKeyException,
		       IOException {

		if (!SSL_ENABLED) return new Socket(HOST,80);

		if (ssl_factory==null) {
			SSLContext ctx = SSL_PROVIDER==null
			               ? SSLContext.getInstance(SSL_PROTOCOL)
					           : SSLContext.getInstance(SSL_PROTOCOL, SSL_PROVIDER );

			LOG.info("SSLContext: ["+ctx.getProtocol()+" "+ctx.getProvider()+"]");
			if (CERT_FILE!=null) {

				KeyManagerFactory kmf = KeyManagerFactory.getInstance( "SunX509" );
				KeyStore ks = KeyStore.getInstance( "JKS") ;
				ks.load(null,null);

				InputStream inStream = new FileInputStream(CERT_FILE);
				X509Certificate cert = null;
				try {
					CertificateFactory cf = CertificateFactory.getInstance("X.509");
					cert = (X509Certificate) cf.generateCertificate(inStream);
				} finally {
					inStream.close();
				}
				LOG.info("(Server Certificate) "+ cert.getSubjectDN().toString());
				ks.setCertificateEntry("", cert);
				kmf.init(ks, null);
				TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
				tmf.init( ks );
				ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
			} else {
				LOG.warn("No server certificate");
				ctx.init(null,null,null);
			}
			ssl_factory = ctx.getSocketFactory();
		}
		return ssl_factory.createSocket(HOST,443);
	}


	/** Closes the socket used by this instance. Subsequent calls to
	 * {@link #checkQuota()} and {@link #nextBytes(byte[], int, int)} will open a new socket.*/
	public void closeSocket() {
		LOG.trace("closeSocket()");
		if (socket!=null) try {
			socket.close();
			LOG.info("Connection closed");
		} catch (IOException e) {;}
		socket=null;
	}


	/** Called by the garbage collector on an object when garbage collection determines that there are no more references to the object.
	 * Closes the socket upon finalization.
	 * @throws Throwable */
	@Override
	protected void finalize() throws Throwable {
		if (socket==null) try {socket.close();} catch (Exception e) {;}
		super.finalize();
	}
}

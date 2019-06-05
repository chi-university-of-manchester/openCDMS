package org.random.rjgodoy.trng;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.SecureRandomSpi;

import javax.net.ssl.SSLContext;


/** A <tt>SecureRandom</tt> implementation accessing <A href="http://www.random.org">www.random.org</A>.
 * Instances of this class do not require {@link RjgodoyProvider} to be registered.<P>
 *
 * Before an instance of this class is constructed (either implicitly by {@link SecureRandom} methods, or explicitly by invoking {@link #MH_SecureRandom() this class constructor}),
 * several System properties must be defined for configuring the shared the HTTP connection. Modifying these properties after an instance of this class has been created has no effect.<P>
 *
 * Other properties (namely <tt>INSTANCE_*</tt>) are instance-specific and apply to the subsequently created instances.<P>
 *
 * The {@link MH_SecureRandom#POOL_DAEMON org.random.rjgodoy.trng.pool_daemon} property is neither a global property nor an instance one:
 * there will be no daemon if all {@link SecureRandomSpi} were instantiated with <tt>org.random.rjgodoy.trng.pool_daemon</tt> equal to <code>false</code>;
 * otherwise, if at least one {@link SecureRandomSpi} was instantiated while this property was <code>true</code> and that {@link SecureRandomSpi} is reachable,
 * then a daemon will be mantained.<P>
 *
 * Initialization based on system properties is synchronized with the MH_SecureRandom class object.
 *
 * <P><font color=red><B>TODO:</B> The reachable thing is not implemented. The daemon will be alive forever.</font>
 *
 * @author Javier Godoy
 */
public class MH_SecureRandom extends SecureRandom {

	/**Initializes a new instance of <code>SecureRandom</code> using the {@link MH_SecureRandomSpi} implementation.<P>
	 * This constructor is only intended for using <tt>MH_SecureRandom</tt> as a stand-alone class. The preferred mechanism is registering the provider {@link RjgodoyProvider}
	 * and creating an instance by {@link SecureRandom#getInstance(String, String) SecureRandom.getInstance}<tt>("RJGODOY","MH_TRNG")</tt>.
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 *
	 */
	public MH_SecureRandom() throws NoSuchAlgorithmException, NoSuchProviderException {
		 super(new MH_SecureRandomSpi(),null);
	 }

	/**Your mail address.<P>
	 * This property is <B>required</B> (see <A href="http://www.random.org/clients/">http://www.random.org/clients/</A>) and should be a valid email address.
	 * However, you may provide a fake email address because of privacy issues (for this purpose, <tt>@example.org</tt> domain addresses are strongly encouraged).<P>
	 * This address will be ONLY used for contacting you if your client instance cause problems.
	 * Note that providing a fake email address will not disallow access, but if any problem occurs it will be impossible to contact you, and your IP may be banned in consequence.
	 * <P><TABLE>
	 * <TR><TD><B>Property<TD> <tt>org.random.rjgodoy.trng.user</tt>.
	 * <TR><TD><B>Scope<TD>global
   * </TABLE>*/
	public final static String  USER          = "org.random.rjgodoy.trng.user";

	/**How long will the client wait for results from the server (in minutes).<P>
	 * This only applies to submitted request, it has nothing to do about waiting after the quota is exhausted.
   Use a long timeout value for your requests. Unless you have used up your quota, the RANDOM.ORG server actually tries to satisfy all requests, so if you use a short timeout value, your request will be abandoned halfways and the numbers discarded.
   This increases load on the server and probably will decrement your quota (because the server had consumed the randomness source, even though it could not deliver the result). Allow at least a couple of minutes for the server to complete your request.
   * <P><TABLE>
   * <TR><TD><B>Property<TD> <tt>org.random.rjgodoy.trng.timeout</tt>
   * <TR><TD><B>Default<TD>  2 minutes<BR>
   * <TR><TD><B>Minimum<TD>  1 minute<BR>
   * <TR><TD><B>Maximum<TD>  596.5232352 minutes (or 0 for "infinite")
   * <TR><TD><B>Scope<TD>global
   * </TABLE>
   */
	public final static String  TIMEOUT       = "org.random.rjgodoy.trng.timeout";


	/** Local file with the public certificate of www.random.org.
	 * If this property is specified AND ssl is used, then SSL will verify the server certificate against this file.<P>
	 * <P><TABLE>
	 * <TR><TD><B>Property<TD> <tt>org.random.rjgodoy.trng.certfile</tt>
	 * <TR><TD><B>Default<TD> <code>null</code> (i.e. do not check).
	 * <TR><TD><B>Scope<TD>global
	 * </TABLE>
   */
	public final static String  CERTFILE      = "org.random.rjgodoy.trng.certfile";


 /**SSL Provider parameter for instantiating {@link SSLContext#getInstance(String) SSLContext}.<P>
  * <P><TABLE>
	* <TR><TD><B>Property<TD> <tt>org.random.rjgodoy.trng.ssl_provider</tt>
	* <TR><TD><B>Default value<TD> <code>null</code> (i.e. any provider)
	* <TR><TD><B>Scope<TD>global
	* </TABLE>*/
	public final static String  SSL_PROVIDER  = "org.random.rjgodoy.trng.ssl_provider";


	/**SSL Protocol parameter for instantiating {@link SSLContext#getInstance(String) SSLContext}.
	 * If this property is <code>null</code> then SSL will be disabled (a plain HTTP connection will be used).
	 * See Appendix A in the
	 * <A href="http://java.sun.com/javase/6/docs/technotes/guides/security/StandardNames.html#SSLContext">
	 *  Java Secure Socket Extension Reference Guide
	 * </A>  for information about standard protocol names. Some allowed values are SSL, TLS, TLSv1.1, etc.<P>
	 * <P><TABLE>
	 * <TR><TD><B>Property<TD> <tt>org.random.rjgodoy.trng.ssl_protocol</tt>
   * <TR><TD><B>Default value <TD> <code>null</code> (i.e. SSL disabled)
   * <TR><TD><B>Scope<TD>global
   * </TABLE>*/
	public final static String  SSL_PROTOCOL  = "org.random.rjgodoy.trng.ssl_protocol";


	/** How many HTTP redirects will be followed.<P>
	 * <P><TABLE>
		* <TR><TD><B>Property<TD> <TT>org.random.rjgodoy.trng.max_redirects</TT>
    * <TR><TD><B>Default value <TD>  99
    * <TR><TD><B>Minimum value <TD>  0
    * <TR><TD><B>Maximum value <TD>  2147483647
    * <TR><TD><B>Scope<TD>global
    *</TABLE>*/
	public final static String  MAX_REDIRECTS = "org.random.rjgodoy.trng.max_redirects";


	/** The {@link FallbackPolicy} that will be used.<P>
	 * <P><TABLE>
		* <TR><TD><B>Property<TD> <TT>org.random.rjgodoy.trng.fallback</TT>
    * <TR><TD><B>Default value <TD> TRNG
    * <TR><TD><B>Allowed values<TD> TRNG, PRNG
    * <TR><TD><B>Scope<TD>instance
    *</TABLE>*/
	public final static String INSTANCE_FALLBACK = "org.random.rjgodoy.trng.fallback";

	/** The {@link GeneratorMode} that will be used.<P>
	 * <P><TABLE>
		* <TR><TD><B>Property<TD> <TT>org.random.rjgodoy.trng.mode</TT>
    * <TR><TD><B>Default value <TD>  TRNG
    * <TR><TD><B>Allowed values<TD>  TRNG, TRNG_XOR_PRNG
    * <TR><TD><B>Scope<TD>instance
    *</TABLE>*/
	public final static String INSTANCE_MODE = "org.random.rjgodoy.trng.mode";

	/** The provider of the auxiliar PRNG used.<P>
	 *  This property is ignored when fallback is {@link FallbackPolicy#TRNG TRNG} and the generator mode is {@link GeneratorMode#TRNG}.<P>
	 *  The RJGODOY provider must not be specified as it would result in an endless loop.
	 * <P><TABLE>
	 * <TR><TD><B>Property<TD> <TT>org.random.rjgodoy.trng.prng_provider</TT>
   * <TR><TD><B>Default value <TD>  null (i.e. any provider)
   * <TR><TD><B>Scope<TD>instance
   *</TABLE>*/
	public final static String INSTANCE_PRNG_PROVIDER = "org.random.rjgodoy.trng.prng_provider";

	/** The algorithm of the auxiliar PRNG used.<P>
	 *  This property is ignored when fallback is {@link FallbackPolicy#TRNG TRNG} and the generator mode is {@link GeneratorMode#TRNG}.<P>
	 *  See Appendix A in the
	 * <A href="http://java.sun.com/javase/6/docs/technotes/guides/security/StandardNames.html#SSLContext">
	 *  Java Secure Socket Extension Reference Guide
	 * </A>  for information about standard protocol names. As of Java 1.6 the only standard value is SHA1PRNG; other algorithms may be available in some platforms or from third-party libraries.<P>
	 *  The algorithm must no be provided by {@link RjgodoyProvider}.
	 * <P><TABLE>
	 * <TR><TD><B>Property<TD> <TT>org.random.rjgodoy.trng.prng_algorithm</TT>
   * <TR><TD><B>Default value <TD>  null (i.e. default algorithm)
   * <TR><TD><B>Scope<TD>instance
   *</TABLE>*/
	public final static String INSTANCE_PRNG_ALGORITHM = "org.random.rjgodoy.trng.prng_algorithm";



	/** Enables a thread running in the background for collectiong some bits from the remote source whenever the quota is 1.000.000.<P>
	 * The amount of bits which will be collected may be modified through {@link MH_PoolDaemon#setBlockSize(long)}
	 * <P><TABLE>
		* <TR><TD><B>Property<TD> <TT>org.random.rjgodoy.trng.pool_daemon</TT>
    * <TR><TD><B>Default value <TD>  false
    * <TR><TD><B>Allowed values<TD>  false, true
    * <TR><TD><B>Scope<TD>shared
    *</TABLE>*/
	public final static String POOL_DAEMON = "org.random.rjgodoy.trng.pool_daemon";
}

package org.random.rjgodoy.trng;
import java.security.Provider;

import org.apache.commons.logging.LogFactory;

//Properties

/**<tt>java.security.Provider</tt> implementating client for TRNG www.random.org from Mads Haahr.<P>
 * This provider defines the <tt>SecureRandom</tt> service <tt>MH_TRNG</tt>.
 *
 * @author Javier Godoy
 */
public final class RjgodoyProvider extends Provider {

	/**Initializes an instance of <tt>RjgodoyProvider</tt>*/
	public RjgodoyProvider() {
		super("RJGODOY",0.0,"RJGodoy Provider implementing client for TRNG www.random.org from Mads Haahr");
		LogFactory.getLog("RjgodoyProvider").info("Initializing "+this+" ("+this.getClass()+")");
		String serviceName = "SecureRandom.MH_TRNG";
		LogFactory.getLog("RjgodoyProvider").info("Initializing service "+serviceName);
		String className   = RjgodoyProvider.class.getPackage().getName()+"."+MH_SecureRandomSpi.class.getSimpleName();
		setProperty(serviceName, className);
	}
}

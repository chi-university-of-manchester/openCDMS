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
package org.psygrid.datasetdesigner.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.random.rjgodoy.trng.MH_SecureRandom;
import org.random.rjgodoy.trng.RjgodoyProvider;

import java.security.SecureRandom;
import java.security.Security;


/**
 * @author pwhelan
 *
 */
public class RandomGenerator {
	
	private static final Log LOG = LogFactory.getLog(RandomGenerator.class);

    long state;
    int nuflen = 256, buflen = 0;
    byte[] buffer;
    int bufptr = -1;

    private static SecureRandom secureRandom = null;
    
	//singleton instance
	private static RandomGenerator INSTANCE = null;
    
    //  Constructors

    /** Creates a new random sequence generator.  */
    private RandomGenerator() {

		//Sets the user information (for the HTTP User-Agent header)
		System.setProperty(MH_SecureRandom.USER, "support@opencdms.org");

		//How many minutes will we wait befor aborting the connection?
		System.setProperty(MH_SecureRandom.TIMEOUT,"2");

		//Use TLS for connecting to the server.
		//System.setProperty(MH_SecureRandom.SSL_PROTOCOL,"TLSv1");
		//System.setProperty(MH_SecureRandom.SSL_PROVIDER,"SunJSSE");

		//Uncomment this to verify the server certificate
		//System.setProperty(MH_SecureRandom.CERTFILE,"www_random_org.cer");

		//Configure how many HTTP redirects will be followed
		//Normally, there shouldn't be any redirect.
		System.setProperty(MH_SecureRandom.MAX_REDIRECTS,"2");

		//register the provider
		Security.addProvider(new RjgodoyProvider());

		try {
			secureRandom = SecureRandom.getInstance("MH_TRNG");
		} catch (Exception ex) {
			LOG.error("Error initiating the randomization service ", ex);
		}
		
    }
    
    /**
	 * Get the HelpHelper singleton
	 * @return the HelpHelper singleton
	 */
	public static RandomGenerator getInstance() {
		if ( null == INSTANCE ){
			INSTANCE = new RandomGenerator();
		}

		
		return INSTANCE;
	}

    public Long nextLong() {
    	if (secureRandom != null) {
    		return secureRandom.nextLong();
    	} else {
    		//if secure Random has not been successfully established, try again here
			try {
				secureRandom = SecureRandom.getInstance("MH_TRNG");
				return secureRandom.nextLong();
			} catch (Exception ex) {
				LOG.error("Error initiating the randomization service ", ex);
			}
    	}
    		
    	return null;
    }
}
    
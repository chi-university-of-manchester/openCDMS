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


package org.psygrid.web.helpers;

import java.util.Date;

import org.opensaml.SAMLAssertion;

/**
 * Helper functions for SAML Assertions.
 * 
 * @author Rob Harper
 *
 */
public class SamlHelper {

	public static String getSaAsString(SAMLAssertion sa){
		if ( null == sa ){
			return null;
		}
		return sa.toString();
	}
	
	/**
	 * Calculate the time difference in milliseconds betwene the clocks
	 * on the client and server.
	 * <p>
	 * This can subsequently be used to correct the current time when
	 * verifying SAML Assertions on the client.
	 * 
	 * @param nowClient The date on the client when the SA was generated.
	 * @param sa The SAML Assertion.
	 * @return The time difference in milliseconds. A positive value means
	 * that the server clock is running faster than the client clock.
	 */
	public static long calculateTimeDifference(Date nowClient, SAMLAssertion sa){
		//Assume "not before" date of the SA is the same as the date
		//it was generated on the server
		Date nowServer = sa.getNotBefore();
		if ( nowServer.before(nowClient) ){
			//server clock is slow compared to the client - the client
			//will think that the SA has expired before it actually has,
			//but we can live with this so return a 0 time difference
			return 0L;
		}
		return nowServer.getTime() - nowClient.getTime();
	}
}

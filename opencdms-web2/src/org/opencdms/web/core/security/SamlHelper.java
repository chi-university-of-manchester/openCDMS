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


package org.opencdms.web.core.security;

import java.net.ConnectException;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencdms.web.core.security.ldap.PsygridLdapUserDetailsImpl;
import org.opensaml.SAMLAssertion;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.PGSecurityInvalidSAMLException;
import org.psygrid.security.PGSecuritySAMLVerificationException;
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage;
import org.psygrid.security.attributeauthority.types.PostProcessLoginResponseType;
import org.psygrid.security.utils.SAMLUtilities;

/**
 * Helper functions for SAML Assertions.
 * 
 * @author Rob Harper
 *
 */
public class SamlHelper {

	private static final Log LOG = LogFactory.getLog(SamlHelper.class);
	
	private static final AAQueryClient aaqc = initAaqc();
	
	public static String getSaml(PsygridLdapUserDetailsImpl user){
		if ( null == user.getSaml() ){
			return null;
		}
		
		LOG.info("Checking SA for "+user.getPgDn());
		try{
			SAMLUtilities.verifySAMLAssertion(user.getSaml(), user.getServerTimeDifference());
			return user.getSaml().toString();
		}
		catch(Exception ex){
			//the SAML Assertion is not valid so retrieve a new one
			LOG.info("Invalid SAML Assertion: "+ex.getMessage());
			try{
				LOG.info("Retrieving SAML assertion for dn "+user.getPgDn());
				user.setSaml(aaqc.getSAMLAssertion(user.getPgDn()));
				return user.getSaml().toString();
			}
			catch(NotAuthorisedFaultMessage nafm){
				LOG.error("Exception when retrieving SAML assertion", nafm);
			}
			catch(PGSecurityException pgse){
				LOG.error("Exception when retrieving SAML assertion", pgse);
			}
			catch(PGSecuritySAMLVerificationException pgsve){
				LOG.error("Exception when retrieving SAML assertion", pgsve);
			}
			catch(PGSecurityInvalidSAMLException pgise){
				LOG.error("Exception when retrieving SAML assertion", pgise);
			}
			catch(ConnectException ce){
				LOG.error("Exception when retrieving SAML assertion", ce);
			}
			catch(NullPointerException npe){
				LOG.error("Exception when retrieving SAML assertion", npe);
			}
			
		}
		
		return null;
		
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
	
	private static AAQueryClient initAaqc(){
		AAQueryClient aaqc = null;
		try{
			aaqc = new AAQueryClient("aaclient.properties");
		}
		catch(Exception ex){
			LOG.error("Unable to initialize aaqq", ex);
		}
		return aaqc;
	}
	
	public static boolean forcePasswordChange(String user) throws Exception {
		PostProcessLoginResponseType pplrt = aaqc.postProcessLogin(user);
		return pplrt.isForcePasswordChange();
	}
}

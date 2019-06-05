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


package org.psygrid.web.security;

import java.net.ConnectException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.PGSecurityInvalidSAMLException;
import org.psygrid.security.PGSecuritySAMLVerificationException;
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage;
import org.psygrid.security.utils.SAMLUtilities;
import org.psygrid.web.details.PsygridUserDetails;

/**
 * SamlRefresher implementation that uses the attribute authority
 * to get a new SAML Assertion if the current one has expired, or 
 * is about to expire.
 * 
 * @author Rob Harper
 *
 */
public class PsygridSamlRefresher implements SamlRefresher {

	private static final Log sLog = LogFactory.getLog(PsygridSamlRefresher.class);
	
	private AAQueryClient aaqc;
	
	public void refresh(PsygridUserDetails pgud) {
		sLog.info("Checking SA for "+pgud.getPgDn());
		try{
			SAMLUtilities.verifySAMLAssertion(pgud.getSaml(), pgud.getServerTimeDifference());
		}
		catch(Exception ex){
			//the SAML Assertion is not valid so retrieve a new one
			sLog.info("Invalid SAML Assertion: "+ex.getMessage());
			try{
				sLog.info("Retrieving SAML assertion for dn "+pgud.getPgDn());
				pgud.setSaml(aaqc.getSAMLAssertion(pgud.getPgDn()));
			}
			catch(NotAuthorisedFaultMessage nafm){
				sLog.error("Exception when retrieving SAML assertion", nafm);
			}
			catch(PGSecurityException pgse){
				sLog.error("Exception when retrieving SAML assertion", pgse);
			}
			catch(PGSecuritySAMLVerificationException pgsve){
				sLog.error("Exception when retrieving SAML assertion", pgsve);
			}
			catch(PGSecurityInvalidSAMLException pgise){
				sLog.error("Exception when retrieving SAML assertion", pgise);
			}
			catch(ConnectException ce){
				sLog.error("Exception when retrieving SAML assertion", ce);
			}
			catch(NullPointerException npe){
				sLog.error("Exception when retrieving SAML assertion", npe);
			}
			
		}
	}

	public AAQueryClient getAaqc() {
		return aaqc;
	}

	public void setAaqc(AAQueryClient aaqc) {
		this.aaqc = aaqc;
	}

}

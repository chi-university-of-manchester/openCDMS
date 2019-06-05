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

package org.opencdms.web.core.security.ldap;

import java.net.ConnectException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.acegisecurity.AuthenticationException;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.LockedException;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.providers.ldap.LdapAuthenticationProvider;
import org.acegisecurity.providers.ldap.LdapAuthenticator;
import org.acegisecurity.providers.ldap.LdapAuthoritiesPopulator;
import org.acegisecurity.ui.WebAuthenticationDetails;
import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.ldap.LdapUserDetails;
import org.acegisecurity.userdetails.ldap.LdapUserDetailsImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencdms.web.core.security.SamlHelper;
import org.opencdms.web.core.security.UnableToCompleteAuthentication;
import org.opensaml.SAMLAssertion;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.PGSecurityInvalidSAMLException;
import org.psygrid.security.PGSecuritySAMLVerificationException;
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectType;

/**
 * PsyGrid specific implementation of LdapAuthenticationProvider.
 * <p>
 * Retrieves SAMl assertion for the user from the Attribute Authority.
 * 
 * @author Rob Harper
 *
 */
public class PsygridLdapAuthenticationProvider extends
		LdapAuthenticationProvider {
	
	private static final Log log = LogFactory.getLog(PsygridLdapAuthenticationProvider.class);
	
	private AAQueryClient aaqc;
	
	public PsygridLdapAuthenticationProvider(LdapAuthenticator authenticator, LdapAuthoritiesPopulator authoritiesPopulator) {
		super(authenticator, authoritiesPopulator);
	}

	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		super.additionalAuthenticationChecks(userDetails, authentication);
		//if we get to this point assume that authentication (in that valid username and password
		//have been provided) has passed.
		try{
			WebAuthenticationDetails wad = (WebAuthenticationDetails)authentication.getDetails();
			if ( aaqc.recordLoginAttempt(authentication.getName(), true, new Date(), wad.getRemoteAddress(), null) ){
				//Account is locked
				log.info("Account is locked/dormant!!");
				
				if(aaqc.isAccountDormant(authentication.getName())){
					throw new LockedException("Account dormant");
				}
				throw new LockedException("Account locked");
			}
		}
		catch(LockedException ex){
			throw ex;
		}
		catch(Exception ex){
			log.error(ex);
			//Cannot connect to the AA to find if the account is locked
			//As being unable to connect to the AA will also mess other
			//things up like finding the user's roles we throw an exception
			//that can be handled by the acegi framework to say that
			//authentication has failed and display a suitable page.
			throw new UnableToCompleteAuthentication("Unable to connect to the AA to complete Authentication", ex);
		}
	}

	@Override
	protected UserDetails createUserDetails(LdapUserDetails ldapUser, String username, String password) {
		//This is the code from the overridden version of createUserDetails MINUS the call
		//to the Authorities Populator - we need to make this call once we have the SAML, projects
		//and groups.
        LdapUserDetailsImpl.Essence user = new LdapUserDetailsImpl.Essence(ldapUser);
        user.setUsername(username);
        user.setPassword(password);
		LdapUserDetails ud = user.createUserDetails();

		String dn = ud.getDn();
		//add spaces after commas!
		dn = dn.replace(",", ", ");
		//capitalize CN, OU, etc.
		dn = dn.replace("cn=", "CN=");
		dn = dn.replace("ou=", "OU=");
		dn = dn.replace("o=", "O=");
		dn = dn.replace("c=", "C=");
		SAMLAssertion saml = null;
		List<ProjectType> projects = null;
		Map<ProjectType, List<GroupType>> groups = new HashMap<ProjectType, List<GroupType>>();
		Date now = new Date();
		long timeDiff = 0;
		try{
			log.info("Retrieving SAML assertion for dn "+dn);
			saml = aaqc.getSAMLAssertion(dn);
			projects = aaqc.getUsersProjects(dn);
			for ( ProjectType p: projects ){
				groups.put(p, aaqc.getUsersGroupsInProject(dn, p));
			}
			timeDiff = SamlHelper.calculateTimeDifference(now, saml);
		}
		catch(NotAuthorisedFaultMessage ex){
			log.error("Exception when retrieving SAML assertion", ex);
		}
		catch(PGSecurityException ex){
			log.error("Exception when retrieving SAML assertion", ex);
		}
		catch(PGSecuritySAMLVerificationException ex){
			log.error("Exception when retrieving SAML assertion", ex);
		}
		catch(PGSecurityInvalidSAMLException ex){
			log.error("Exception when retrieving SAML assertion", ex);
		}
		catch(ConnectException ex){
			log.error("Exception when retrieving SAML assertion", ex);
		}
		catch(NullPointerException ex){
			log.error("Exception when retrieving SAML assertion", ex);
		}
		PsygridLdapUserDetailsImpl pgud = new PsygridLdapUserDetailsImpl(ud, saml, dn, projects, groups, timeDiff);
		
        GrantedAuthority[] extraAuthorities = getAuthoritiesPopulator().getGrantedAuthorities(pgud);
        pgud.addAuthorities(extraAuthorities);
        pgud.setGlobalAuthorities(extraAuthorities);
		
		return pgud;
	}

	public AAQueryClient getAaqc() {
		return aaqc;
	}

	public void setAaqc(AAQueryClient aaqc) {
		this.aaqc = aaqc;
	}

}

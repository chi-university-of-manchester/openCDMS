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

package org.opencdms.web.core.application;

import org.acegisecurity.Authentication;
import org.acegisecurity.AuthenticationException;
import org.acegisecurity.AuthenticationManager;
import org.acegisecurity.BadCredentialsException;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.ui.WebAuthenticationDetails;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Request;
import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.apache.wicket.protocol.http.WebRequestCycle;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.opencdms.web.core.security.ldap.PsygridLdapUserDetailsImpl;

/**
 * @author Rob Harper
 *
 */
public class OpenCdmsWebSession extends AuthenticatedWebSession {

	private static final long serialVersionUID = 1L;

	private static final Log LOG = LogFactory.getLog(OpenCdmsWebSession.class);
	
	private boolean forcePasswordChange = false;
	
	public OpenCdmsWebSession(Request request) {
		super(request);
	}

	
	/**
	 * @return Current openCDMS web session
	 */
	public static OpenCdmsWebSession get(){
		return (OpenCdmsWebSession)AuthenticatedWebSession.get();
	}

	public boolean isForcePasswordChange() {
		return forcePasswordChange;
	}

	public void setForcePasswordChange(boolean forcePasswordChange) {
		this.forcePasswordChange = forcePasswordChange;
	}

	@Override
	public boolean authenticate(String username, String password) {
        String u = username == null ? "" : username;
        String p = password == null ? "" : password;

        // Create an Acegi authentication request.
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(u, p);
        ServletWebRequest swr = (ServletWebRequest)WebRequestCycle.get().getRequest();
        authRequest.setDetails(new WebAuthenticationDetails(swr.getHttpServletRequest()));
        
        // Attempt authentication.
        try {
            AuthenticationManager authenticationManager =
                ((OpenCdmsWeb) getApplication()).getAuthenticationManager();
            Authentication authResult = authenticationManager.authenticate(authRequest);
            setAuthentication(authResult);

            LOG.info("Login by user '" + username + "'.");
            return true;

        } catch (BadCredentialsException e) {
            LOG.info("Failed login by user '" + username + "'.");
            setAuthentication(null);
            return false;

        } catch (AuthenticationException e) {
            LOG.error("Could not authenticate a user", e);
            setAuthentication(null);
            throw e;

        } catch (RuntimeException e) {
            LOG.error("Unexpected exception while authenticating a user", e);
            setAuthentication(null);
            throw e;
        }
	}

	@Override
	public Roles getRoles() {
        if (isSignedIn()) {
            Roles roles = new Roles();
            // Retrieve the granted authorities from the current authentication. These correspond one on
            // one with user roles.
            GrantedAuthority[] authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
            for (int i = 0; i < authorities.length; i++) {
                GrantedAuthority authority = authorities[i];
                roles.add(authority.getAuthority());
            }
            return roles;
        }
        return null;
	}

	@Override
	public void signOut() {
		PsygridLdapUserDetailsImpl user = getUser();
        if (user != null) {
            LOG.info("Logout by user '" + user.getUsername() + "'.");
        }
        setAuthentication(null);
        invalidate();
	}

    /**
     * @return the currently logged in user, or null when no user is logged in
     */
    public PsygridLdapUserDetailsImpl getUser() {
    	PsygridLdapUserDetailsImpl user = null;
        if (isSignedIn()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            user = (PsygridLdapUserDetailsImpl) authentication.getPrincipal();
        }
        return user;
    }

    /**
     * Sets the acegi authentication.
     * @param authentication the authentication or null to clear
     */
    private void setAuthentication(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }


}

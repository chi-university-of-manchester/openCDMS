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

import java.util.Date;

import org.acegisecurity.event.authentication.AuthenticationFailureBadCredentialsEvent;
import org.acegisecurity.ui.WebAuthenticationDetails;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

/**
 * Application listener to listen for authentication events
 * 
 * @author Rob Harper
 *
 */
public class PsygridAuthenticationListener implements ApplicationListener {

	private static final Log LOG = LogFactory.getLog(PsygridAuthenticationListener.class);
	
	private AAQueryClient aaqc;
	
	public PsygridAuthenticationListener(AAQueryClient aaqc){
		this.aaqc = aaqc;
	}
	
	public void onApplicationEvent(ApplicationEvent event) {
		if ( event instanceof AuthenticationFailureBadCredentialsEvent ){
			//record failed login attempts
			try{
				AuthenticationFailureBadCredentialsEvent afbce = (AuthenticationFailureBadCredentialsEvent)event;
				WebAuthenticationDetails wad = (WebAuthenticationDetails)afbce.getAuthentication().getDetails();
				aaqc.recordLoginAttempt(afbce.getAuthentication().getName(), false, new Date(), wad.getRemoteAddress(), null);
			}
			catch (Exception ex){
				LOG.error("Cannot connect to AAQC to record failed login attempt", ex);
			}
		}
	}

}

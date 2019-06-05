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


package org.psygrid.web.acegi;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.userdetails.UserDetails;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.psygrid.security.attributeauthority.types.PostProcessLoginResponseType;

/**
 * Filter to see if the default password is still being used for the
 * current user, and if so force then to change it.
 * 
 * @author Rob Harper
 *
 */
public class ChangeDefaultPasswordFilter implements Filter {

	private static final Log LOG = LogFactory.getLog(ChangeDefaultPasswordFilter.class);
	
	private String changePasswordUrl;
	
	private AAQueryClient aaqc;
	
	public ChangeDefaultPasswordFilter(AAQueryClient aaqc){
		this.aaqc = aaqc;
	}
	
	public String getChangePasswordUrl() {
		return changePasswordUrl;
	}

	public void setChangePasswordUrl(String changePasswordUrl) {
		this.changePasswordUrl = changePasswordUrl;
	}

	/**
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {

	}

	/**
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
			throws IOException, ServletException {
		
        if (!(request instanceof HttpServletRequest)) {
        	throw new ServletException("Can only process HttpServletRequest");
        }

        if (!(response instanceof HttpServletResponse)) {
        	throw new ServletException("Can only process HttpServletResponse");
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        //don't attempt to redirect to the change password page if that's already
        //where we are going - otherwise we will get in a bit of a loop!
        if ( !httpRequest.getRequestURI().endsWith(changePasswordUrl)){
	        if ( null != SecurityContextHolder.getContext().getAuthentication() ){
				UserDetails user = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
				try{
					PostProcessLoginResponseType pplrt = aaqc.postProcessLogin(user.getUsername());
					if ( pplrt.isForcePasswordChange() ){
						sendRedirect(httpRequest, httpResponse, changePasswordUrl);
						return;
					}
				}
				catch(Exception ex){
					//Unable to connect to AA to see if a password change is required
					//Do nothing apart from the log the exception, because (a) if the
					//AA is not contactable authentication should have failed anyway,
					//so we won't actually get to this point, and (b) even if they do 
					//get here they can just change the password next time, when hopefully
					//the AA is contactable again.
					LOG.error("Error when trying to connect to AA to see if password change required", ex);
				}
	        }
        }
        chain.doFilter(request, response);

	}

	/**
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig arg0) throws ServletException {

	}

    protected void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url)
    throws IOException {
	    if (!url.startsWith("http://") && !url.startsWith("https://")) {
	        url = request.getContextPath() + url;
	    }
	
	    response.sendRedirect(response.encodeRedirectURL(url));
	}

}

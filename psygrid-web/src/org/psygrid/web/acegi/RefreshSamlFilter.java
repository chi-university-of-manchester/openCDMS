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

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;
import org.psygrid.web.details.PsygridUserDetails;
import org.psygrid.web.security.SamlRefresher;

/**
 * @author Rob Harper
 *
 */
public class RefreshSamlFilter implements Filter {

	private SamlRefresher samlRefresher;
	
	public SamlRefresher getSamlRefresher() {
		return samlRefresher;
	}

	public void setSamlRefresher(SamlRefresher samlRefresher) {
		this.samlRefresher = samlRefresher;
	}

	public void init(FilterConfig arg0) throws ServletException {
		//nothing to do
	}

	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if ( null != auth ){
			//get the users details from the session
			PsygridUserDetails pgud = (PsygridUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			samlRefresher.refresh(pgud);
		}
		//call the next filter in the chain
		chain.doFilter(req, resp);
	}

	public void destroy() {
		//nothing to do
	}

}

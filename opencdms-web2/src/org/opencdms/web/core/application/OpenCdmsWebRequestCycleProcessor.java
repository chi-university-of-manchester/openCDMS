/*
Copyright (c) 2006-2009, The University of Manchester, UK.

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

import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.AccessStackPageMap;
import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.IPageMap;
import org.apache.wicket.IRequestTarget;
import org.apache.wicket.Page;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Session;
import org.apache.wicket.AccessStackPageMap.Access;
import org.apache.wicket.protocol.http.IgnoreAjaxRequestException;
import org.apache.wicket.protocol.http.PageExpiredException;
import org.apache.wicket.protocol.http.WebRequestCycleProcessor;
import org.apache.wicket.protocol.http.servlet.AbortWithWebErrorCodeException;
import org.apache.wicket.request.IRequestCodingStrategy;
import org.apache.wicket.request.RequestParameters;
import org.apache.wicket.request.target.basic.EmptyAjaxRequestTarget;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Rob Harper
 *
 */
public class OpenCdmsWebRequestCycleProcessor extends WebRequestCycleProcessor {

	private static final Logger log = LoggerFactory.getLogger(OpenCdmsWebRequestCycleProcessor.class);

	/**
	 * This is an identical copy of the method in the superclass except 
	 * if the request cannot be processed then a PageExpiredException is
	 * always thrown, whether the request is a normal one or an ajax one
	 * (in the superclass the latter case results in an empty ajax response)
	 */
	@Override
	public IRequestTarget resolve(RequestCycle requestCycle,
			RequestParameters requestParameters) {
		// TODO Auto-generated method stub
		IRequestCodingStrategy requestCodingStrategy = requestCycle.getProcessor()
		.getRequestCodingStrategy();

		final String path = requestParameters.getPath();
		IRequestTarget target = null;
	
		// See whether this request points to a bookmarkable page
		if (requestParameters.getBookmarkablePageClass() != null)
		{
			target = resolveBookmarkablePage(requestCycle, requestParameters);
		}
		// See whether this request points to a rendered page
		else if (requestParameters.getComponentPath() != null)
		{
			// marks whether or not we will be processing this request
			boolean processRequest = true;
			synchronized (requestCycle.getSession())
			{
				// we need to check if this request has been flagged as
				// process-only-if-path-is-active and if so make sure this
				// condition is met
				if (requestParameters.isOnlyProcessIfPathActive())
				{
					// this request has indeed been flagged as
					// process-only-if-path-is-active
	
					Session session = Session.get();
					IPageMap pageMap = session.pageMapForName(requestParameters.getPageMapName(),
						false);
					if (pageMap == null)
					{
						// requested pagemap no longer exists - ignore this
						// request
						processRequest = false;
					}
					else if (pageMap instanceof AccessStackPageMap)
					{
						AccessStackPageMap accessStackPageMap = (AccessStackPageMap)pageMap;
						if (accessStackPageMap.getAccessStack().size() > 0)
						{
							final Access access = (Access)accessStackPageMap.getAccessStack()
								.peek();
	
							final int pageId = Integer.parseInt(Strings.firstPathComponent(
								requestParameters.getComponentPath(), Component.PATH_SEPARATOR));
	
							if (pageId != access.getId())
							{
								// the page is no longer the active page
								// - ignore this request
								processRequest = false;
							}
							else
							{
								final int version = requestParameters.getVersionNumber();
								if (version != Page.LATEST_VERSION &&
									version != access.getVersion())
								{
									// version is no longer the active version -
									// ignore this request
									processRequest = false;
								}
							}
						}
					}
					else
					{
						// TODO also this should work..
					}
				}
			}
			if (processRequest)
			{
				try
				{
					target = resolveRenderedPage(requestCycle, requestParameters);
				}
				catch (IgnoreAjaxRequestException e)
				{
					target = EmptyAjaxRequestTarget.getInstance();
				}
			}
			else
			{
				throw new PageExpiredException("Request cannot be processed");
			}
		}
		// See whether this request points to a shared resource
		else if (requestParameters.getResourceKey() != null)
		{
			target = resolveSharedResource(requestCycle, requestParameters);
		}
		// See whether this request points to the home page
		else if (Strings.isEmpty(path) || ("/".equals(path)))
		{
			target = resolveHomePageTarget(requestCycle, requestParameters);
		}
	
		// NOTE we are doing the mount check as the last item, so that it will
		// only be executed when everything else fails. This enables URLs like
		// /foo/bar/?wicket:bookmarkablePage=my.Page to be resolved, where
		// is either a valid mount or a non-valid mount. I (Eelco) am not
		// absolutely sure this is a great way to go, but it seems to have been
		// established as the default way of doing things. If we ever want to
		// tighten the algorithm up, it should be combined by going back to
		// unmounted paths so that requests with Wicket parameters like
		// 'bookmarkablePage' are always created and resolved in the same
		// fashion. There is a test for this in UrlMountingTest.
		if (target == null)
		{
			// still null? check for a mount
			target = requestCodingStrategy.targetForRequest(requestParameters);
	
			if (target == null && requestParameters.getComponentPath() != null)
			{
				// If the target is still null and there was a component path
				// then the Page could not be located in the session
				throw new PageExpiredException(
					"Cannot find the rendered page in session [pagemap=" +
						requestParameters.getPageMapName() + ",componentPath=" +
						requestParameters.getComponentPath() + ",versionNumber=" +
						requestParameters.getVersionNumber() + "]");
			}
		}
		else
		{
			// a target was found, but not by looking up a mount. check whether
			// this is allowed
			if (Application.get().getSecuritySettings().getEnforceMounts() &&
				requestCodingStrategy.pathForTarget(target) != null)
			{
				String msg = "Direct access not allowed for mounted targets";
				// the target was mounted, but we got here via another path
				// : deny the request
				log.error(msg + " [request=" + requestCycle.getRequest() + ",target=" + target +
					",session=" + Session.get() + "]");
				throw new AbortWithWebErrorCodeException(HttpServletResponse.SC_FORBIDDEN, msg);
			}
		}
	
		// (WICKET-1356) in case no target was found, return null here. RequestCycle will deal with
		// it
		// possible letting wicket filter to pass the request down the filter chain
		/*
		 * if (target == null) { // if we get here, we have no recognized Wicket target, and thus //
		 * regard this as a external (non-wicket) resource request on // this server return
		 * resolveExternalResource(requestCycle); }
		 */
	
		return target;
	}

}

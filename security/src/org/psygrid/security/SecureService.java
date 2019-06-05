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


//Created on Oct 17, 2005 by John Ainsworth

package org.psygrid.security;

import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.rpc.ServiceException;
import javax.xml.rpc.server.ServiceLifecycle;
import javax.xml.rpc.server.ServletEndpointContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.security.WSSecurityEngineResult;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.apache.ws.security.handler.WSHandlerResult;
import org.opensaml.SAMLAssertion;
import org.opensaml.SAMLException;

/**
 * @author jda
 * 
 */
public abstract class SecureService implements ServiceLifecycle {


	/** logger */
	private static Log sLog = LogFactory.getLog(SecureService.class);

	/** the service endpoint context provided by the J2EE container */
	protected ServletEndpointContext oContext;

	protected SAMLAssertion samlToken = null;

	protected ServletContext xContext = null;

	/**
	 * 
	 */
	public SecureService() {
		super();
	}

	/**
	 * @return
	 */
	protected String getCallersIdentity() {
		String xDN = oContext.getUserPrincipal().toString();
		sLog.debug("Authenticated User: " + xDN);
		return xDN;
	}

	/**
	 * @return
	 */
	protected Principal getCallerPrincipals() {
		return oContext.getUserPrincipal();
	}

	/**
	 * JAX-RPC runtime system ends the lifecycle of a service endpoint instance
	 * by invoking the destroy method. The service endpoint releases its
	 * resourcesin the implementation of the destroy method.
	 */
	public void destroy() {
		// cleanup
	}

	/**
	 * Used for initialization of a service endpoint. After a service endpoint
	 * instance (an instance of a service endpoint class) is instantiated, the
	 * JAX-RPC runtime system invokes the init method. The service endpoint
	 * class uses the init method to initialize its configuration and setup
	 * access to any external resources. The context parameter in the init
	 * method enables the endpoint instance to access the endpoint context
	 * provided by the underlying JAX-RPC runtime system. The init method
	 * implementation should typecast the context parameter to an appropriate
	 * Java type. For service endpoints deployed on a servlet container based
	 * JAX-RPC runtime system, the context parameter is of the Java type
	 * javax.xml.rpc.server.ServletEndpointContext. The ServletEndpointContext
	 * provides an endpoint context maintained by the underlying servlet
	 * container based JAX-RPC runtime system
	 * 
	 * @param pContext
	 *            Endpoint context for a JAX-RPC service endpoint
	 * 
	 * @throws ServiceException
	 *             If any error in initialization of the service endpoint; or if
	 *             any illegal context has been provided in the init method
	 */
	public void init(Object o) throws ServiceException {
		sLog.debug("initialising secure service");
		if (!(o instanceof ServletEndpointContext)) {
			throw new ServiceException(
					"The <name> service implementation is expected to be hosted in a Servlet environment");
		}
		final ServletEndpointContext fContext = (ServletEndpointContext) o;

		// wrap the servlet endpoint context so that the getUserPrincipal()
		// method can be overriden
		oContext = new ServletEndpointContext() {
			public javax.xml.rpc.handler.MessageContext getMessageContext() {
				return fContext.getMessageContext();
			}

			public Principal getUserPrincipal() {
				Principal xPrincipal = null;

				// check if principal exists. If not, attempt to retrieve
				// principal from message context
//				if (xPrincipal == null) {
//					xPrincipal = fContext.getUserPrincipal();
//					if (xPrincipal != null) {
//						sLog.debug("principal obtained from MessageContext - "
//								+ xPrincipal.getName());
//					}
//				}
				
				// check if principal exists. If not, attempt to get it from the
				// Axis system (possibley using HTTPS)
				if (xPrincipal == null) {
					sLog.debug("entering request for message context");
					// Axis stores the request object in the message contxt
					ServletRequest xRequest = (ServletRequest) getMessageContext()
					.getProperty("transport.http.servletRequest");	
					if (xRequest != null) {
						if (xRequest
								.getAttribute("javax.servlet.request.X509Certificate") instanceof X509Certificate[]) {		
							X509Certificate[] xCerts = (X509Certificate[]) xRequest
							.getAttribute("javax.servlet.request.X509Certificate");
							if (xCerts != null && xCerts.length > 0) {
								xPrincipal = (xCerts[0]).getSubjectDN();
							}
							if (xPrincipal != null) {
								sLog
								.debug("principal obtained from Axis transport - "
										+ xPrincipal.getName());
							}
						} 
					} 
				}
				
				// can be null
				return xPrincipal;
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see javax.xml.rpc.server.ServletEndpointContext#getHttpSession()
			 */
			public HttpSession getHttpSession() {
				return fContext.getHttpSession();
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see javax.xml.rpc.server.ServletEndpointContext#getServletContext()
			 */
			public ServletContext getServletContext() {
				return fContext.getServletContext();
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see javax.xml.rpc.server.ServletEndpointContext#isUserInRole(java.lang.String)
			 */
			public boolean isUserInRole(String s) {
				return fContext.isUserInRole(s);
			}

		};
		xContext = getServletEndpointContext().getServletContext();
	}

	/**
	 * get the associated ServletEndpointContext
	 * 
	 * @return ServletEndpointContext the context object
	 */
	protected ServletEndpointContext getServletEndpointContext() {
		if (oContext == null) {
			throw new IllegalStateException(
					"service has not been initialised by the servlet system yet.");
		}
		return oContext;
	}
	/**
	 * get the associated ServletContext
	 * 
	 * @return ServletContext the context object
	 */
	protected ServletContext getServletContext() {
		if (xContext == null) {
			throw new IllegalStateException(
					"service has not been initialised by the servlet system yet.");
		}
		return xContext;
	}
	/**
	 * 
	 * @return boolean
	 */
	protected boolean authoriseUser() {

		Vector results = null;
		// get the result Vector from the property

		if ((results = (Vector) oContext.getMessageContext().getProperty(
				WSHandlerConstants.RECV_RESULTS)) == null) {
			sLog.debug("No security results!!");
		}
		for (int i = 0; i < results.size(); i++) {

			WSHandlerResult hResult = (WSHandlerResult) results.get(i);

			Vector hResults = hResult.getResults();

			for (int j = 0; j < hResults.size(); j++) {

				WSSecurityEngineResult eResult = (WSSecurityEngineResult) hResults
						.get(j);
				if (eResult.getAssertion() != null) {
					samlToken = eResult.getAssertion();
					sLog.debug(">>>>>>>"+samlToken.toString());
					
					try{
					samlToken.checkValidity();
						sLog.debug("SAML token is valid");
						
					Iterator it = samlToken.getX509Certificates();
					if(it.hasNext()){
						sLog.debug(((X509Certificate)it.next()).getSubjectDN().getName());
					}
					samlToken.verify();
						sLog.debug("SAML token is verified");
					}catch (SAMLException se){
						sLog.debug(se.getMessage());
					}
					return true;
				}
			}
		}
		return false;
	}

	protected X509Certificate[] getUserCertificates() {
		ServletRequest xRequest = (ServletRequest) oContext.getMessageContext()
				.getProperty("transport.http.servletRequest");
		if (xRequest != null) {
			if (xRequest.getAttribute("javax.servlet.request.X509Certificate") instanceof X509Certificate[]) {
				X509Certificate[] xCerts = (X509Certificate[]) xRequest
						.getAttribute("javax.servlet.request.X509Certificate");
				if (xCerts != null && xCerts.length > 0) {
					return xCerts;
				}
			}
		}
		return null;
	}
}

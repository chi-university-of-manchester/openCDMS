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

package org.psygrid.security.accesscontrol;

import java.rmi.RemoteException;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.rpc.handler.MessageContext;
import javax.xml.rpc.server.ServletEndpointContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opensaml.SAMLAssertion;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.PGSecurityInvalidSAMLException;
import org.psygrid.security.PGSecuritySAMLVerificationException;
import org.psygrid.security.policyauthority.client.PAQueryClient;
import org.psygrid.security.policyauthority.service.InputFaultMessage;
import org.psygrid.security.policyauthority.service.NotAuthorisedFaultMessage;
import org.psygrid.security.policyauthority.service.ProcessingFaultMessage;
import org.psygrid.security.utils.SAMLUtilities;
import org.psygrid.www.xml.security.core.types.ActionTargetType;
import org.psygrid.www.xml.security.core.types.ActionType;
import org.psygrid.www.xml.security.core.types.AllowedType;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectActionType;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.TargetType;


/**
 * @author jda
 *
 */
public class AccessEnforcementFunction implements IAccessEnforcementFunction {
	
	/** logger */
	private static Log sLog = LogFactory.getLog(AccessEnforcementFunction.class);

	/** the service endpoint context provided by the J2EE container */
	protected ServletEndpointContext oContext;
	
	PAQueryClient paqc = null;
	
	// A cache for authorisation results.
	// This is a very basic implementation of a cache 
	// the whole cache times out every sixty seconds.
	Map<String,Boolean> authCache = Collections.synchronizedMap(new HashMap<String,Boolean>());

	// Timeout for the cache in milliseconds
	private static long CACHE_TIMEOUT_MS=60000; // One minute
	
	// The last cache timeout
	long last_cache_clear = 0;

	public AccessEnforcementFunction(){}

	public AccessEnforcementFunction(ServletEndpointContext ctx){
		try{
			initialise(ctx);
		} catch (PGSecurityException pgse){
			sLog.fatal(pgse.getMessage());
		}
	}

	/**
	 * @return
	 */
	public String getCallersIdentity() {
		String xDN = null;
		if (oContext!=null && oContext.getUserPrincipal() != null) {
			xDN = oContext.getUserPrincipal().toString();
			sLog.debug("Authenticated User: " + xDN);
		}
		return xDN;
	}

	/**
	 * @return
	 */
	protected Principal getCallerPrincipals() {
		return oContext.getUserPrincipal();
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

	/* (non-Javadoc)
	 * @see org.psygrid.security.accesscontrol.IAccessEnforcementFunction#authoriseUser(java.lang.String, java.lang.String, org.psygrid.www.xml.security.core.types.ActionType, java.lang.String)
	 */ 
	public boolean authoriseUserOld(String stringAssertion, 
			AEFGroup group, 
			AEFAction action, 
			AEFProject project) 
	throws PGSecurityException, PGSecurityInvalidSAMLException, PGSecuritySAMLVerificationException {

		return _authoriseUser(stringAssertion, 
					new GroupType(group.getName(), group.getIdCode(), null),
					new ActionType(action.getName(), action.getIdCode()),
					new  ProjectType(project.getName(), project.getIdCode(), null, null, false)); 
	}


	/* (non-Javadoc)
	 * @see org.psygrid.security.accesscontrol.IAccessEnforcementFunction#authoriseUser(java.lang.String, java.lang.String, org.psygrid.www.xml.security.core.types.ActionType, java.lang.String)
	 */ 
	public boolean authoriseUser(String stringAssertion, 
			AEFGroup group, 
			AEFAction action, 
			AEFProject project) 
	throws PGSecurityException, PGSecurityInvalidSAMLException, PGSecuritySAMLVerificationException {

		// Clear the cache after CACHE_TIMEOUT_MS milliseconds
		long now=System.currentTimeMillis();
		if(now-last_cache_clear>CACHE_TIMEOUT_MS){
			sLog.debug("Authorization Cache timeout");
			authCache.clear();
			last_cache_clear=now;
		}
		
		String key = stringAssertion+"::"+group.getName()+"::"+group.getIdCode()
		  +"::"+action.getName()+"::"+action.getIdCode()+"::"+project.getName()
		  +"::"+project.getIdCode();
		
		Boolean result = authCache.get(key);
		if(result==null){
			sLog.debug("Authorization Cache miss");
			result = _authoriseUser(stringAssertion, 
					new GroupType(group.getName(), group.getIdCode(), null),
					new ActionType(action.getName(), action.getIdCode()),
					new  ProjectType(project.getName(), project.getIdCode(), null, null, false));
			authCache.put(key, result);
		}
		else {
			sLog.debug("Authorization Cache hit");
		}
		return result;
	}
		
	public boolean _authoriseUser(String stringAssertion,
			GroupType group, ActionType action, ProjectType project)
			throws PGSecurityException, PGSecurityInvalidSAMLException,
			PGSecuritySAMLVerificationException{
		
		SAMLAssertion sa = null;
		boolean result = false;
 
		try {
			result = paqc.getPort().makePolicyDecision(project, new TargetType(group.getName(), group.getIdCode()), action, 
					stringAssertion);
		} catch (InputFaultMessage e) {
			sLog.info(e.getFaultDiagnostic());
			throw new PGSecurityException(e.getFaultDiagnostic());
		} catch (ProcessingFaultMessage e) {
			sLog.info(e.getFaultDiagnostic());
			throw new PGSecurityException(e.getFaultDiagnostic());
		} catch (NotAuthorisedFaultMessage e) {
			sLog.info(e.getFaultDiagnostic());
			throw new PGSecurityException(e.getFaultDiagnostic());
		} catch (RemoteException e) {
			sLog.info(e.getMessage());
			throw new PGSecurityException(e.getMessage());
		}
		return result;
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
	
	public void initialise(ServletEndpointContext ctx) throws PGSecurityException{
		final ServletEndpointContext fContext = ctx;

		// wrap the servlet endpoint context so that the getUserPrincipal()
		// method can be overriden
		oContext = new ServletEndpointContext() {
			public javax.xml.rpc.handler.MessageContext getMessageContext() {
				return fContext.getMessageContext();
			}

			public Principal getUserPrincipal() {
				Principal xPrincipal = null;

				// check if principal exists. If not, attempt to get it from the
				// Axis system (possibly using HTTPS)
				if (xPrincipal == null) {
					sLog.debug("entering request for message context");
					// Axis stores the request object in the message context
					// But there may not be one if we are NOT calling a service via an Axis call
					// e.g. we are calling it from a scheduled job.
					MessageContext msgContext = getMessageContext();
					if(msgContext!=null) {
						ServletRequest xRequest = (ServletRequest) msgContext.getProperty("transport.http.servletRequest");
						if (xRequest != null) {
							if (xRequest
									.getAttribute("javax.servlet.request.X509Certificate") instanceof X509Certificate[]) {
								X509Certificate[] xCerts = (X509Certificate[]) xRequest
										.getAttribute("javax.servlet.request.X509Certificate");
								if (xCerts != null && xCerts.length > 0) {
									xPrincipal = (xCerts[0]).getSubjectDN();
								}
								if (xPrincipal != null) {
									sLog	.debug("principal obtained from Axis transport - "
													+ xPrincipal.getName());
								}
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
		String propertiesFile = oContext.getServletContext().getInitParameter("policyAuthorityProperties");	
		try{
			paqc = new PAQueryClient(propertiesFile);
		} catch (PGSecurityException pgse) {
			sLog.fatal("failed to initiliase PA Query Client");
			throw pgse;
		}
	}

	public String getUserFromSAML(String stringAssertion)
			throws PGSecurityException, PGSecurityInvalidSAMLException,
			PGSecuritySAMLVerificationException {
		return SAMLUtilities.getUserFromSAML(stringAssertion);
	}

	public String getUserFromUnverifiedSAML(String stringAssertion)
			throws PGSecurityException {
		return SAMLUtilities.getUserFromUnverifiedSAML(stringAssertion);
	}

	public String getIssuerFromSAML(String stringAssertion)
			throws PGSecurityException, PGSecurityInvalidSAMLException,
			PGSecuritySAMLVerificationException {
		return SAMLUtilities.getIssuerFromSAML(stringAssertion);
	}

	public String getIssuerFromUnverifiedSAML(String stringAssertion)
			throws PGSecurityException {
		return SAMLUtilities.getIssuerFromUnverifiedSAML(stringAssertion);
	}
	
	public  Map<ProjectType, List<Map<ActionType, List<TargetType>>>> 
	checkAuthorisation(Map<ProjectType, List<ActionType>> request, String sa)
	throws PGSecurityException {
		
		AllowedType[] result = null;

		ProjectActionType[] pata = new ProjectActionType[request.size()];
		Set<Entry<ProjectType, List<ActionType>>> ws = request.entrySet();
		int i=0;
		for(Entry<ProjectType, List<ActionType>> e : ws){			
			pata[i] = new ProjectActionType();
			pata[i].setProject(e.getKey());
			List<ActionType> l = e.setValue(new ArrayList<ActionType>());
			pata[i].setAction(l.toArray(new ActionType[l.size()]));
			i++;
		}
		
		try {
			result = paqc.getPort().testPrivileges(pata, sa);
		} catch (InputFaultMessage e) {
			sLog.info(e.getFaultDiagnostic());
			throw new PGSecurityException(e.getFaultDiagnostic());
		} catch (ProcessingFaultMessage e) {
			sLog.info(e.getFaultDiagnostic());
			throw new PGSecurityException(e.getFaultDiagnostic());
		} catch (NotAuthorisedFaultMessage e) {
			sLog.info(e.getFaultDiagnostic());
			throw new PGSecurityException(e.getFaultDiagnostic());
		} catch (RemoteException e) {
			sLog.info(e.getMessage());
			throw new PGSecurityException(e.getMessage());
		}
		
		// process result
		Map<ProjectType, List<Map<ActionType, List<TargetType>>>> resultMap = new HashMap<ProjectType, List<Map<ActionType, List<TargetType>>>>();
		for(int j=0;j<result.length;j++){
			AllowedType at = result[j];
			List<Map<ActionType, List<TargetType>>> latttt = new ArrayList<Map<ActionType, List<TargetType>>>();
			for(int k=0;k<at.getActionTarget().length;k++){
				ActionTargetType att = at.getActionTarget()[k];
				List<TargetType> ltt = new ArrayList<TargetType>();
				for(int l=0;l<att.getTargets().length;l++){
					ltt.add(att.getTargets(l));
				}
				Map<ActionType, List<TargetType>> attmap = new HashMap<ActionType, List<TargetType>>();
				attmap.put(att.getAction(), ltt);
				latttt.add(attmap);
			}
			resultMap.put(result[j].getProject(), latttt);
		}
		return resultMap;
	}
}

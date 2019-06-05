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

import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.rpc.server.ServletEndpointContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.security.WSSecurityEngineResult;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.apache.ws.security.handler.WSHandlerResult;
import org.opensaml.SAMLAssertion;
import org.opensaml.SAMLAttribute;
import org.opensaml.SAMLAttributeStatement;
import org.opensaml.SAMLException;
import org.psygrid.security.PGSecurityConstants;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.PGSecurityInvalidSAMLException;
import org.psygrid.security.PGSecuritySAMLVerificationException;
import org.psygrid.security.policyauthority.client.PAQueryClient;
import org.psygrid.www.xml.security.core.types.ActionType;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.PrivilegeType;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.RoleType;
import org.psygrid.www.xml.security.core.types.TargetType;

/**
 * @author jda
 *
 */
public class AccessEnforcementFunctionStub implements IAccessEnforcementFunction {
	
	/** logger */
	private static Log sLog = LogFactory.getLog(AccessEnforcementFunctionStub.class);

	/** the service endpoint context provided by the J2EE container */
	protected ServletEndpointContext oContext;
	
	PAQueryClient paqc = null;

	public AccessEnforcementFunctionStub(){}
	
	public AccessEnforcementFunctionStub(ServletEndpointContext ctx){
		try{
		initialise(ctx);
		} catch (PGSecurityException pgse) {
			sLog.info(pgse.getMessage());
		}
	}
		
	/**
	 * @return
	 */
    public String getCallersIdentity() {
		String xDN = null;
		if (oContext.getUserPrincipal() != null) {
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


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psygrid.security.accesscontrol.IAccessEnforcementFunction#authoriseUser(java.lang.String,
	 *      java.lang.String,
	 *      org.psygrid.www.xml.security.core.types.ActionType,
	 *      java.lang.String)
	 */
	public boolean authoriseUser(String stringAssertion, AEFGroup group,
			AEFAction action, AEFProject project) throws PGSecurityException, PGSecurityInvalidSAMLException, PGSecuritySAMLVerificationException {	
		boolean result = true;
		return result;
	}
	
//	public boolean authoriseUser(String stringAssertion, GroupType group,
//			ActionType action, ProjectType project) throws PGSecurityException, PGSecurityInvalidSAMLException, PGSecuritySAMLVerificationException {	
//		boolean result = true;
//		return result;
//	}
	
	private PrivilegeType[] getUsersPrivilegesInProjectFromST(SAMLAssertion sa, String project){
		Iterator<SAMLAttributeStatement> it = sa.getStatements();
		boolean groupFound = false;
		SAMLAttributeStatement st = null;
		SAMLAttribute attribute = null;
		List<PrivilegeType> ptl = new ArrayList<PrivilegeType>();
		while (it.hasNext()&&!groupFound) {
			st = it.next();
			Iterator<SAMLAttribute> it2 = st.getAttributes();
			while (it2.hasNext()) {
				attribute = it2.next();
				if (attribute.getName().equals(
						PGSecurityConstants.SAML_ATTRIBUTE_MEMBERSHIP)) {
					Iterator<String> it3 = attribute.getValues();
					while (it3.hasNext()) {
						String g = it3.next();
						if(g.equals(project)){
							groupFound=true;
						}
					}		
					if(groupFound){
						while(it2.hasNext()){
							attribute = it2.next();
							if (attribute.getName().equals(
									PGSecurityConstants.SAML_ATTRIBUTE_ROLE)) {
								it3 = attribute.getValues();
								while (it3.hasNext()) {
									String r = it3.next();
									ptl.add(new PrivilegeType(new RoleType(r, null), null));
								}
							}
							if (attribute.getName().equals(
									PGSecurityConstants.SAML_ATTRIBUTE_GROUP)) {
								it3 = attribute.getValues();
								while (it3.hasNext()) {
									String r = it3.next(); //group name
									String s = it3.next(); //group code
									ptl.add(new PrivilegeType(null, new GroupType(r, s, null)));

								}
							}
						}
					}
				}
			}
		}
		return ptl.toArray(new PrivilegeType[ptl.size()]);
	}

	private SAMLAssertion getSAMLTokenFromWSSecurity(){
		Vector results = null;
		// get the result Vector from the property
		SAMLAssertion samlToken = null;
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
					return samlToken;
				}
			}
		}
		return null;
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

				// check if principal exists. If not, attempt to retrieve
				// principal from message context
				if (xPrincipal == null) {
					xPrincipal = fContext.getUserPrincipal();
					if (xPrincipal != null) {
						sLog.debug("principal obtained from MessageContext - "
								+ xPrincipal.getName());
					}
				}

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
		String propertiesFile = oContext.getServletContext().getInitParameter("policyAuthorityProperties");	
		try{
			paqc = new PAQueryClient(propertiesFile);
		} catch (PGSecurityException pgse) {
			sLog.fatal("failed to initiliase PA Query Client");
		}
	}

    public String getUserFromSAML(String stringAssertion) throws PGSecurityException, PGSecurityException,
	PGSecurityInvalidSAMLException, 
	PGSecuritySAMLVerificationException {
        return "NoUser";
    }
    
	public String getUserFromUnverifiedSAML(String stringAssertion)
			throws PGSecurityException {
		return "NoUser";
	}

	public String getIssuerFromSAML(String stringAssertion)
			throws PGSecurityException, PGSecurityInvalidSAMLException,
			PGSecuritySAMLVerificationException {
		return "NoIssuer";
	}

	public String getIssuerFromUnverifiedSAML(String stringAssertion)
			throws PGSecurityException {
		return "NoIssuer";
	}
	public Map<ProjectType, List<Map<ActionType, List<TargetType>>>> checkAuthorisation(
			Map<ProjectType, List<ActionType>> request, String sa)
			throws PGSecurityException {
		return null;
	}
}

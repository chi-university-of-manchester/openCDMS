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


//Created on Oct 11, 2005 by John Ainsworth
package org.psygrid.security.policyauthority;

import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.xml.rpc.server.ServiceLifecycle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opensaml.SAMLAssertion;
import org.opensaml.SAMLException;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.PGSecurityInvalidSAMLException;
import org.psygrid.security.PGSecuritySAMLVerificationException;
import org.psygrid.security.policyauthority.dao.PolicyDAO;
import org.psygrid.security.policyauthority.model.hibernate.Action;
import org.psygrid.security.policyauthority.model.hibernate.Authority;
import org.psygrid.security.policyauthority.model.hibernate.CompositeRule;
import org.psygrid.security.policyauthority.model.hibernate.Group;
import org.psygrid.security.policyauthority.model.hibernate.Policy;
import org.psygrid.security.policyauthority.model.hibernate.Privilege;
import org.psygrid.security.policyauthority.model.hibernate.Role;
import org.psygrid.security.policyauthority.model.hibernate.Statement;
import org.psygrid.security.policyauthority.model.hibernate.Target;
import org.psygrid.security.policyauthority.service.InputFaultMessage;
import org.psygrid.security.policyauthority.service.NotAuthorisedFaultMessage;
import org.psygrid.security.policyauthority.service.PolicyAuthorityQueryPortType;
import org.psygrid.security.policyauthority.service.ProcessingFaultMessage;
import org.psygrid.security.utils.SAMLUtilities;
import org.psygrid.www.xml.security.core.types.ActionTargetType;
import org.psygrid.www.xml.security.core.types.ActionType;
import org.psygrid.www.xml.security.core.types.AllowedType;
import org.psygrid.www.xml.security.core.types.CompositeRuleType;
import org.psygrid.www.xml.security.core.types.PrivilegeType;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.TargetType;

public class PolicyAuthorityQueryPortTypeImpl extends PolicyAuthority
		implements PolicyAuthorityQueryPortType, ServiceLifecycle {

	/** logger */
	static Log sLog = LogFactory
			.getLog(PolicyAuthorityQueryPortTypeImpl.class);

    public PolicyAuthorityQueryPortTypeImpl() {
		super();
	}
	
	public boolean makePolicyDecision(ProjectType project, 
			TargetType target, 
			ActionType action, 
			String saml)
	throws NotAuthorisedFaultMessage, ProcessingFaultMessage, InputFaultMessage 
	{
	    final String METHOD_NAME="makePolicyDecision";
		sLog.debug("makePolicyDecision("+project.getName()+", "+target.getName()+", "+action.getName()+", "+saml+")");
	
		if(SAMLUtilities.isSystemLevelAssertion(saml)){
			return true;
		}
		
 		try {
 			String uid = SAMLUtilities.getUserFromUnverifiedSAML(saml);
 			String cid = getCallersIdentity();
 			logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, uid, cid);
			if (!authorisedForQueryRequests(getCallerPrincipals())) {
	             logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, uid, cid);	        
				throw new NotAuthorisedFaultMessage("Query Permission Denied");
			}
		} catch (PGSecurityException e) {
			throw new ProcessingFaultMessage(e.getMessage());
		}
		PrivilegeType[] privilege = null;
		Principal authorityFromSAML = null;
		try{
			SAMLAssertion sa = verifySAMLAssertion(saml);
			privilege = SAMLUtilities.getUsersPrivilegesInProjectFromST(sa, project);
			X509Certificate ix509 = (X509Certificate)sa.getX509Certificates().next();
			authorityFromSAML = ix509.getSubjectX500Principal();
		} catch (PGSecuritySAMLVerificationException ve){
			throw new NotAuthorisedFaultMessage(ve.getMessage());
		} catch (PGSecurityInvalidSAMLException ise){
			throw new NotAuthorisedFaultMessage(ise.getMessage());
		} catch (PGSecurityException se){
			throw new NotAuthorisedFaultMessage(se.getMessage());
		} catch (SAMLException se){
			throw new NotAuthorisedFaultMessage(se.getMessage());
		}

		PolicyDAO pdao = getPolicyDAO();
			
		if(!authorityFromSAML.getName().equals(getDefaultAuthority().getName())){
			List<Authority> authoritiesList = pdao.getAuthorities(project.getName(), project.getIdCode()); 
			boolean delegated = false;
			for(Authority authority : authoritiesList){
				if(authorityFromSAML.getName().equals(authority.getName())){
					delegated = true;
					break;
				}
			}
			if(!delegated){
				sLog.info("Attempt to access "+project.getName()+" with SA issued by "+authorityFromSAML.getName()+" has been denied.");
				throw new NotAuthorisedFaultMessage("SAML Assertion issuer unknown");				
			}
		}

		List<Statement> sl = pdao.getMatchingStatementsFromPolicy(new Policy(project.getName(), project.getIdCode()),Target.fromTargetType(target), Action.fromActionType(action));

		if (sl.size() != 0) {
			List<Privilege> lra = new ArrayList<Privilege>();
			for (int i = 0; i < privilege.length; i++) {
				if(privilege[i].getRole()!=null){
					sLog.debug(privilege[i].getRole().getName());
					lra.add(Role.fromRoleType(privilege[i].getRole()));
				}else if(privilege[i].getGroup()!=null){
					sLog.debug(privilege[i].getGroup().getName());
					lra.add(Group.fromGroupType(privilege[i].getGroup()));				
				}
			}	
			for (Statement stemp:sl){
				if(stemp.getRule().isPermitted(lra)){
					if (sLog.isDebugEnabled()) {
						sLog.debug("Matched: "
								+ stemp.getAction().getActionName() + " "
								+ stemp.getTarget().getTargetName() + " "
								+ stemp.getRule().getId());
						String pstring = "";
						for (Privilege ptemp : lra) {
							pstring += ptemp.getName() + " ";
						}
						sLog.debug("With: " + pstring);
					}
					return true;
				}
			}
			sLog.debug("isallowed = false");
			return false;
		}
		sLog.debug("isallowed = nopolicy");
		throw new InputFaultMessage("Policy does not exist");
	}
    public org.psygrid.www.xml.security.core.types.AllowedType[] testPrivileges(
			org.psygrid.www.xml.security.core.types.ProjectActionType[] request,
			java.lang.String assertion)
			throws java.rmi.RemoteException,
			org.psygrid.security.policyauthority.service.NotAuthorisedFaultMessage,
			org.psygrid.security.policyauthority.service.ProcessingFaultMessage,
			org.psygrid.security.policyauthority.service.InputFaultMessage {

		final String METHOD_NAME = "testPrivileges";

		//preamble
		try {
			String uid = SAMLUtilities.getUserFromUnverifiedSAML(assertion);
			String cid = getCallersIdentity();
			logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, uid, cid);
			if (!authorisedForQueryRequests(getCallerPrincipals())) {
				logHelper
						.logAccessDenied(COMPONENT_NAME, METHOD_NAME, uid, cid);
				throw new NotAuthorisedFaultMessage("Query Permission Denied");
			}
		} catch (PGSecurityException e) {
			throw new ProcessingFaultMessage(e.getMessage());
		}
		
		SAMLAssertion sa = null;
		Principal authorityFromSAML = null;
		List<ActionTargetType> latt = new ArrayList<ActionTargetType>();
		PrivilegeType[] privilege = null;
		try {
			sa = verifySAMLAssertion(assertion);
			X509Certificate ix509 = (X509Certificate)sa.getX509Certificates().next();
			authorityFromSAML = ix509.getSubjectX500Principal();
		} catch (PGSecuritySAMLVerificationException ve){
			throw new NotAuthorisedFaultMessage(ve.getMessage());
		} catch (PGSecurityInvalidSAMLException ise){
			throw new NotAuthorisedFaultMessage(ise.getMessage());
		} catch (PGSecurityException se){
			throw new NotAuthorisedFaultMessage(se.getMessage());
		} catch (SAMLException se){
			throw new NotAuthorisedFaultMessage(se.getMessage());
		}

		PolicyDAO pdao = getPolicyDAO();

		//begin
		List<AllowedType> lat = new ArrayList<AllowedType>();
		for (int j = 0; j < request.length; j++) {
				
			privilege = SAMLUtilities.getUsersPrivilegesInProjectFromST(sa,
					request[j].getProject());
			
			if(!authorityFromSAML.getName().equals(getDefaultAuthority().getName())){
				List<Authority> authoritiesList = pdao.getAuthorities(request[j].getProject().getName(), request[j].getProject().getIdCode()); 
				boolean delegated = false;
				for(Authority authority : authoritiesList){
					if(authorityFromSAML.getName().equals(authority.getName())){
						delegated = true;
						break;
					}
				}
				if(!delegated){
					sLog.info("Attempt to access "+request[j].getProject().getName()+" with SA issued by "+authorityFromSAML.getName()+" has been denied.");
					continue;				
				}
			}
			for (int k = 0; k < request[j].getAction().length; k++) {
				List<Statement> sl = pdao.getMatchingStatementsFromPolicy(
					new Policy(request[j].getProject().getName(), 
							request[j].getProject().getIdCode()), 
					Action.fromActionType(request[j].getAction(k)));
				if (sl.size() != 0) {
					List<TargetType> ltt = new ArrayList<TargetType>();
					List<Privilege> lra = new ArrayList<Privilege>();
					for (int i = 0; i < privilege.length; i++) {
						if (privilege[i].getRole() != null) {
							sLog.debug(privilege[i].getRole().getName());
							lra.add(Role.fromRoleType(privilege[i].getRole()));
						} else if (privilege[i].getGroup() != null) {
							sLog.debug(privilege[i].getGroup().getName());
							lra.add(Group.fromGroupType(privilege[i].getGroup()));
						}
					}
					for (int l = 0; l < sl.size(); l++) {
						if (sl.get(l).getRule().isPermitted(lra)) {
							ltt.add(sl.get(l).getTarget().toTargetType());
						}
					}
					if(ltt.size()>0){
						ActionTargetType att = new ActionTargetType(request[j].getAction(k), ltt.toArray(new TargetType[ltt.size()]));
						latt.add(att);
					}
				}
			}
			if(latt.size()>0){
				AllowedType at = new AllowedType(request[j].getProject(), latt.toArray(new ActionTargetType[latt.size()]));
				lat.add(at);
			}
		}
		if(lat.size()>0){
			return lat.toArray(new AllowedType[lat.size()]);
		}
		return null;
	}	
    
    /*
    public org.psygrid.www.xml.security.core.types.CompositeRuleType[] getMatchingRules(
			org.psygrid.www.xml.security.core.types.ProjectActionTargetType request,
			java.lang.String assertion)
			throws java.rmi.RemoteException,
			org.psygrid.security.policyauthority.service.NotAuthorisedFaultMessage,
			org.psygrid.security.policyauthority.service.ProcessingFaultMessage,
			org.psygrid.security.policyauthority.service.InputFaultMessage {
		
    	final String METHOD_NAME = "getMatchingRules";

		//preamble
    	try {
    		String uid = SAMLUtilities.getUserFromUnverifiedSAML(assertion);
    		String cid = getCallersIdentity();
    		logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, uid, cid);
    		if (!authorisedForQueryRequests(getCallerPrincipals())) {
    			logHelper
    			.logAccessDenied(COMPONENT_NAME, METHOD_NAME, uid, cid);
    			throw new NotAuthorisedFaultMessage("Query Permission Denied");
    		}
    	} catch (PGSecurityException e) {
    		throw new ProcessingFaultMessage(e.getMessage());
    	}
		//begin
		List<CompositeRuleType> lr = new ArrayList<CompositeRuleType>();

		PolicyDAO pdao = getPolicyDAO();

		List<Statement> sl = pdao.getMatchingStatementsFromPolicy(
				new Policy(request.getProject().getName(), 
						request.getProject().getIdCode()), 
						Target.fromTargetType(request.getTarget()),
						Action.fromActionType(request.getAction()));
		if (sl.size() != 0) {
			for (int l = 0; l < sl.size(); l++) {
				lr.add(((CompositeRule)sl.get(l).getRule()).toExternalType());
			}
		}
		if(lr.size()>0){
			return lr.toArray(new CompositeRuleType[lr.size()]);
		}
		return null;
    
    }
    */
}


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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.rpc.ServiceException;
import javax.xml.rpc.server.ServiceLifecycle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opensaml.SAMLAssertion;
import org.psygrid.security.PGSecurityConstants;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.PGSecurityInvalidSAMLException;
import org.psygrid.security.PGSecuritySAMLVerificationException;
import org.psygrid.security.RBACAction;
import org.psygrid.security.policyauthority.dao.DAOException;
import org.psygrid.security.policyauthority.dao.ObjectOutOfDateException;
import org.psygrid.security.policyauthority.dao.PolicyDAO;
import org.psygrid.security.policyauthority.model.hibernate.Action;
import org.psygrid.security.policyauthority.model.hibernate.Group;
import org.psygrid.security.policyauthority.model.hibernate.Policy;
import org.psygrid.security.policyauthority.model.hibernate.Privilege;
import org.psygrid.security.policyauthority.model.hibernate.Role;
import org.psygrid.security.policyauthority.model.hibernate.Statement;
import org.psygrid.security.policyauthority.model.hibernate.Target;
import org.psygrid.security.policyauthority.service.InputFaultMessage;
import org.psygrid.security.policyauthority.service.NotAuthorisedFaultMessage;
import org.psygrid.security.policyauthority.service.PolicyAuthorityManagementPortType;
import org.psygrid.security.policyauthority.service.ProcessingFaultMessage;
import org.psygrid.security.utils.SAMLUtilities;
import org.psygrid.security.utils.XMLUtilities;
import org.psygrid.www.xml.security.core.types.ActionType;
import org.psygrid.www.xml.security.core.types.PolicyType;
import org.psygrid.www.xml.security.core.types.PrivilegeType;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.TargetType;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.w3c.dom.Document;

/**
 * @author jda
 * 
 */

public class PolicyAuthorityManagementPortTypeImpl extends PolicyAuthority
		implements PolicyAuthorityManagementPortType, ServiceLifecycle {

    static Object DB_LOCK = new Object();
    
	/** Logger */
	private static Log log = LogFactory
			.getLog(PolicyAuthorityManagementPortTypeImpl.class);
	
	private PolicyAuthorityManagementPortType managementService;

	public PolicyAuthorityManagementPortTypeImpl() {
		super();
	}

    /* (non-Javadoc)
     * @see org.psygrid.security.policyauthority.service.PolicyAuthorityManagementPortType#getVersion()
     */
    public java.lang.String getVersion() throws java.rmi.RemoteException
    {
		return getServletContext().getInitParameter("version");
    }
    
	public void init(Object pContext) throws ServiceException {
		super.init(pContext);
		ApplicationContext context = WebApplicationContextUtils
		.getWebApplicationContext(getServletContext());
		managementService = (PolicyAuthorityManagementPortType) context.getBean("policyAuthorityManagementService");	
	}

    
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psygrid.security.policyauthority.service.PolicyAuthorityConfigurationPortType#updateConfiguration(java.lang.String)
	 */
	public boolean updateConfiguration(String config) throws RemoteException,
			InputFaultMessage, ProcessingFaultMessage,
			NotAuthorisedFaultMessage {
		String sxml = null;
		boolean rc = false;
		Document doc = null;
		
		final String METHOD_NAME = "updateConfiguration";
		String cid = getCallersIdentity();
		logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);
		
		if (!_makePolicyDecision(new ProjectType(PGSecurityConstants.SYSTEM_PROJECT,
				PGSecurityConstants.SYSTEM_PROJECT_ID, null, null, true),
				new TargetType(), new ActionType(
				RBACAction.ACTION_PA_UPDATE_CONFIGURATION.toString(),
				RBACAction.ACTION_PA_UPDATE_CONFIGURATION.idAsString()))) {
			try {
				if (!authorisedForAdministrationRequests(getCallerPrincipals())) {
					logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid,
							cid);
					throw new NotAuthorisedFaultMessage(
							"Administration Permission Denied");
				}
			} catch (PGSecurityException pge) {
				throw new ProcessingFaultMessage(pge.getMessage());
			}
		}				
		if (config != null) {
			if ((doc = new XMLUtilities().toDocument(config, true,
					getXMLSchemaLocation() + File.separator
					+ getConfigurationSchemaFile())) != null) {
				synchronized (getConfigurationDOM()) {
					setConfigurationDOM(doc);
					if ((sxml = XMLUtilities
							.domToString(getConfigurationDOM())) == null) {
						throw new ProcessingFaultMessage(
						"internal error 007");
					}
					try {
						FileWriter configFileWriter = new FileWriter(
								getConfigurationFile());
						configFileWriter.write(sxml);
						configFileWriter.flush();
					} catch (IOException ioe) {
						log.fatal("io error " + ioe.getMessage());
						throw new ProcessingFaultMessage(
						"internal error 008");
					}
				}
				rc = true;
			} else {
				log.fatal("invalid XML " + config);
				throw new InputFaultMessage("Non-conformist XML "
						+ config);
			}
		} else {
			// refresh the configuration
			configure(getConfigurationFile());
			try{
				initialiseAACertsKeyStore();
			} catch (ServiceException se){
				throw new ProcessingFaultMessage("internal error 009 "+se.getMessage());
			}
		}
		return rc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psygrid.security.policyauthority.service.PolicyAuthorityConfigurationPortType#retrieveConfiguration(boolean)
	 */
	public String retrieveConfiguration(boolean refresh)
			throws RemoteException, ProcessingFaultMessage,
			NotAuthorisedFaultMessage {
		String s = null;
		
		final String METHOD_NAME = "retrieveConfiguration";
		String cid = getCallersIdentity();
		logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);

		if (!_makePolicyDecision(new ProjectType(PGSecurityConstants.SYSTEM_PROJECT,
				PGSecurityConstants.SYSTEM_PROJECT_ID, null, null, true),
				new TargetType(), new ActionType(
				RBACAction.ACTION_PA_RETRIEVE_CONFIGURATION.toString(),
				RBACAction.ACTION_PA_RETRIEVE_CONFIGURATION.idAsString()))) {
			try {
				if (!authorisedForAdministrationRequests(getCallerPrincipals())) {
					logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid,
							cid);
					throw new NotAuthorisedFaultMessage(
							"Administration Permission Denied");
				}
			} catch (PGSecurityException pge) {
				throw new ProcessingFaultMessage(pge.getMessage());
			}
		}

		if (refresh)
			configure(getConfigurationFile());
		s = XMLUtilities.domToString(getConfigurationDOM());

		return s;
	}

	public boolean addPolicy(
			org.psygrid.www.xml.security.core.types.PolicyType[] input)
			throws java.rmi.RemoteException,
			org.psygrid.security.policyauthority.service.NotAuthorisedFaultMessage,
			org.psygrid.security.policyauthority.service.ProcessingFaultMessage,
			org.psygrid.security.policyauthority.service.InputFaultMessage {
		
		final String METHOD_NAME = "addPolicy";
		String cid = getCallersIdentity();
		logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);
		
		if (!_makePolicyDecision(new ProjectType(
				PGSecurityConstants.SYSTEM_PROJECT,
				PGSecurityConstants.SYSTEM_PROJECT_ID, null, null, true), new TargetType(),
				new ActionType(RBACAction.ACTION_PA_ADD_POLICY.toString(),
						RBACAction.ACTION_PA_ADD_POLICY.idAsString()))) {
			try {
				if (!authorisedForAdministrationRequests(getCallerPrincipals())) {
					logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid,
							cid);
					throw new NotAuthorisedFaultMessage(
							"Administration Permission Denied");
				}
				
			} catch (PGSecurityException pge) {
				throw new ProcessingFaultMessage(pge.getMessage());
			}
		}

		
		return managementService.addPolicy(input);
		
	}
	
	public boolean updatePolicy(
			org.psygrid.www.xml.security.core.types.PolicyType[] input)
			throws java.rmi.RemoteException,
			org.psygrid.security.policyauthority.service.NotAuthorisedFaultMessage,
			org.psygrid.security.policyauthority.service.ProcessingFaultMessage,
			org.psygrid.security.policyauthority.service.InputFaultMessage {
		
		final String METHOD_NAME = "updatePolicy";
		String cid = getCallersIdentity();
		logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);
		
		for(int i=0;i<input.length;i++){
			if (!_makePolicyDecision(new ProjectType(
					input[i].getName(),
					input[i].getIdCode(), null, null, false), new TargetType(),
					new ActionType(RBACAction.ACTION_PA_MODIFY_POLICY.toString(),
							RBACAction.ACTION_PA_MODIFY_POLICY.idAsString()))) {
				try {
					if (!authorisedForAdministrationRequests(getCallerPrincipals())) {
						logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid,
								cid);
						throw new NotAuthorisedFaultMessage(
						"Administration Permission Denied");
					}	
				} catch (PGSecurityException pge) {
					throw new ProcessingFaultMessage(pge.getMessage());
				}
			}
		}

		PolicyDAO policyDAO = getPolicyDAO();
		for (int i = 0; i < input.length; i++) {			
			Policy newPolicy = factory.createPolicy(input[i]);
			try {
				Policy persistedPolicy = policyDAO.getPolicyByName(newPolicy.getPolicyName());
				if (persistedPolicy == null) {
					addPolicy(new PolicyType[]{input[i]});
				} else {
					synchronized (DB_LOCK) {
						persistedPolicy.updateDescription(input[i].getDescription());
						policyDAO.updatePolicy(persistedPolicy);
						persistedPolicy = policyDAO.getPolicyByName(newPolicy.getPolicyName());					
						for(Statement s : newPolicy.getStatements()){
							s.attach(persistedPolicy);
						}
						policyDAO.updatePolicy(persistedPolicy);						
					}
				}
			} catch (DAOException doa) {
				doa.printStackTrace();
				throw new ProcessingFaultMessage(doa.getMessage());
			} catch (ObjectOutOfDateException oood) {
				throw new ProcessingFaultMessage(oood.getMessage());
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		return true;
	}

	public boolean deletePolicy(
			PolicyType[] deletePolicyRequest)
			throws java.rmi.RemoteException,
			org.psygrid.security.policyauthority.service.NotAuthorisedFaultMessage,
			org.psygrid.security.policyauthority.service.ProcessingFaultMessage,
			org.psygrid.security.policyauthority.service.InputFaultMessage {
		log.debug("deletePolicy(" + deletePolicyRequest + ")");
		
		final String METHOD_NAME = "deletePolicy";
		String cid = getCallersIdentity();
		logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);
		
		for(int i=0;i<deletePolicyRequest.length;i++){
			if (!_makePolicyDecision(new ProjectType(
					deletePolicyRequest[i].getName(),
					deletePolicyRequest[i].getIdCode(), null, null, true), new TargetType(),
					new ActionType(RBACAction.ACTION_PA_DELETE_POLICY.toString(),
							RBACAction.ACTION_PA_DELETE_POLICY.idAsString()))) {
				try {
					if (!authorisedForAdministrationRequests(getCallerPrincipals())) {
						logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid,
								cid);
						throw new NotAuthorisedFaultMessage(
						"Administration Permission Denied");
					}
					
				} catch (PGSecurityException pge) {
					throw new ProcessingFaultMessage(pge.getMessage());
				}
			}
		}
		PolicyDAO policyDAO = getPolicyDAO();

		for (int i = 0; i < deletePolicyRequest.length; i++) {
			try {
				Policy p = policyDAO.getPolicyByName(deletePolicyRequest[i].getName());
				if (p != null) {
					synchronized (DB_LOCK) {
					policyDAO.removePolicy(p.getId());
					}
				} else {
					log.error("Policy does not exist: "+deletePolicyRequest[i].getName());
				}
			} catch (DAOException doa) {
				throw new ProcessingFaultMessage(doa.getMessage());
			} catch (ObjectOutOfDateException oood) {
				throw new ProcessingFaultMessage(oood.getMessage());
			}
		}
		return true;
	}

	public boolean addStatementToPolicy(
			org.psygrid.www.xml.security.core.types.PolicyType[] input)
			throws java.rmi.RemoteException,
			org.psygrid.security.policyauthority.service.NotAuthorisedFaultMessage,
			org.psygrid.security.policyauthority.service.ProcessingFaultMessage,
			org.psygrid.security.policyauthority.service.InputFaultMessage {
		
		final String METHOD_NAME = "addStatementToPolicy";
		String cid = getCallersIdentity();
		logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);

		for(int i=0;i<input.length;i++){
			if (!_makePolicyDecision(new ProjectType(
					input[i].getName(),
					input[i].getIdCode(), null, null, false), new TargetType(),
					new ActionType(RBACAction.ACTION_PA_MODIFY_POLICY.toString(),
							RBACAction.ACTION_PA_MODIFY_POLICY.idAsString()))) {
				try {
					if (!authorisedForAdministrationRequests(getCallerPrincipals())) {
						logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid,
								cid);
						throw new NotAuthorisedFaultMessage(
						"Administration Permission Denied");
					}
					
				} catch (PGSecurityException pge) {
					throw new ProcessingFaultMessage(pge.getMessage());
				}
			}
		}

		PolicyDAO policyDAO = getPolicyDAO();
		for (int i = 0; i < input.length; i++) {
			Policy p = Policy.fromPolicyType(input[i]);
			try {
				synchronized (DB_LOCK) {

					Policy persistedPolicy = policyDAO.getPolicyByName(input[i].getName());
					for(Statement s : p.getStatements()){
						s.attach(persistedPolicy);
					}
					policyDAO.updatePolicy(persistedPolicy);
				}
			} catch (DAOException doa) {
				throw new ProcessingFaultMessage(doa.getMessage());
			} catch (ObjectOutOfDateException oood) {
				throw new ProcessingFaultMessage(oood.getMessage());
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		return true;
	}

	public boolean deleteStatementFromPolicy(
			org.psygrid.www.xml.security.core.types.PolicyType[] input)
			throws java.rmi.RemoteException,
			org.psygrid.security.policyauthority.service.NotAuthorisedFaultMessage,
			org.psygrid.security.policyauthority.service.ProcessingFaultMessage,
			org.psygrid.security.policyauthority.service.InputFaultMessage {

		log.debug("deleteStatementFromPolicy(" + input + ")");
		
		final String METHOD_NAME = "deleteStatementFromPolicy";
		String cid = getCallersIdentity();
		logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);
		
		for(int i=0;i<input.length;i++){
			if (!_makePolicyDecision(new ProjectType(
					input[i].getName(),
					input[i].getIdCode(), null, null, false), new TargetType(),
					new ActionType(RBACAction.ACTION_PA_MODIFY_POLICY.toString(),
							RBACAction.ACTION_PA_MODIFY_POLICY.idAsString()))) {
				try {
					if (!authorisedForAdministrationRequests(getCallerPrincipals())) {
						logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid,
								cid);
						throw new NotAuthorisedFaultMessage(
						"Administration Permission Denied");
					}
					
				} catch (PGSecurityException pge) {
					throw new ProcessingFaultMessage(pge.getMessage());
				}
			}
		}

		PolicyDAO policyDAO = getPolicyDAO();

		for (int i = 0; i < input.length; i++) {
			try {
				synchronized (DB_LOCK) {
				policyDAO.deleteStatementFromPolicy(input[i]);
				}
			} catch (DAOException doa) {
				throw new ProcessingFaultMessage(doa.getMessage());
			} catch (ObjectOutOfDateException oood) {
				throw new ProcessingFaultMessage(oood.getMessage());
			}
		}
		return true;
	}

	public org.psygrid.www.xml.security.core.types.PolicyType getPolicyByProject(
			PolicyType input)
			throws java.rmi.RemoteException,
			org.psygrid.security.policyauthority.service.NotAuthorisedFaultMessage,
			org.psygrid.security.policyauthority.service.ProcessingFaultMessage,
			org.psygrid.security.policyauthority.service.InputFaultMessage {
		PolicyType pt = null;

		log.debug("getPolicyByProject(" + ")");
		try {
			final String METHOD_NAME = "getPolicyByProject";
			String cid = getCallersIdentity();
			logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);

			if (!_makePolicyDecision(new ProjectType(input.getName(), input
					.getIdCode(), null, null, false), new TargetType(), new ActionType(
					RBACAction.ACTION_PA_GET_POLICY.toString(),
					RBACAction.ACTION_PA_GET_POLICY.idAsString()))) {

				if (!authorisedForAdministrationRequests(getCallerPrincipals())) {
					logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid,
							cid);
					throw new NotAuthorisedFaultMessage(
							"Administration Permission Denied");
				}
			}
		} catch (PGSecurityException pge) {
			throw new ProcessingFaultMessage(pge.getMessage());
		}
		

		PolicyDAO policyDAO = getPolicyDAO();

		try {
			Policy lp = policyDAO.getPolicyByName(input.getName());
			if (lp != null) {
				pt = lp.toPolicyType();
			} else {
				log.error("Policy does not exist. (Policy follows)");
				Policy.fromPolicyType(input).print();
			}
		} catch (DAOException doa) {
			throw new ProcessingFaultMessage(doa.getMessage());
		}
		return pt;
	}

	public PolicyType[] getAllPolicies()
			throws java.rmi.RemoteException,
			org.psygrid.security.policyauthority.service.NotAuthorisedFaultMessage,
			org.psygrid.security.policyauthority.service.ProcessingFaultMessage,
			org.psygrid.security.policyauthority.service.InputFaultMessage {
		log.debug("getAllPolicies(" + ")");
		PolicyType[] pta = null;


		try {
			final String METHOD_NAME = "getAllPolicies";
			String cid = getCallersIdentity();
			logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);
			if (!_makePolicyDecision(new ProjectType(
					PGSecurityConstants.SYSTEM_PROJECT,
					PGSecurityConstants.SYSTEM_PROJECT_ID, null, null, true), new TargetType(),
					new ActionType(RBACAction.ACTION_PA_GET_POLICY.toString(),
							RBACAction.ACTION_PA_GET_POLICY.idAsString()))) {

				if (!authorisedForAdministrationRequests(getCallerPrincipals())) {
		             logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid, cid);	
					throw new NotAuthorisedFaultMessage(
							"Administration Permission Denied");
				}
			}
		} catch (PGSecurityException pge) {
			throw new ProcessingFaultMessage(pge.getMessage());
		}
		
		try {
			PolicyDAO policyDAO = getPolicyDAO();

			List<Policy> lp = policyDAO.getPolicies();
			if (lp != null) {
				pta = new PolicyType[lp.size()];
				for (int i = 0; i < lp.size(); i++) {
					pta[i] = lp.get(i).toPolicyType();
				}
			}
		} catch (DAOException doa) {
			throw new ProcessingFaultMessage(doa.getMessage());
		} catch (Exception e){
			e.printStackTrace();
		}
		return pta;
	}
	String _getAttributeForUserInProject(String user, ProjectType project){
		try {
			return aaqc.getPort().getAttributesForUserInProject(user, project);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		return null;
	}
	public boolean _makePolicyDecision(ProjectType project, 
			TargetType target, 
			ActionType action)
	throws ProcessingFaultMessage, InputFaultMessage 
	{
		PrivilegeType[] privilege = null;
		String saml = this._getAttributeForUserInProject(this.getCallersIdentity(),project);
		if(saml==null){
			return false;
		}
		try{
			SAMLAssertion sa = verifySAMLAssertion(saml);
			privilege = SAMLUtilities.getUsersPrivilegesInProjectFromST(sa, project);
		} catch (PGSecuritySAMLVerificationException ve){
			sLog.error(ve.getMessage());
			return false;
		} catch (PGSecurityInvalidSAMLException ise){
			sLog.error(ise.getMessage());
			return false;
		} catch (PGSecurityException se){
			sLog.error(se.getMessage());
			return false;
		}
		
		PolicyDAO pdao = getPolicyDAO();
		List<Statement> sl = pdao.getMatchingStatementsFromPolicy(
				new Policy(project.getName(), project.getIdCode()),
				Target.fromTargetType(target), 
				Action.fromActionType(action));
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
					sLog.debug("isallowed = true");
					return true;
				}
			}
			sLog.debug("isallowed = false");
			return false;
		}
		sLog.debug("no matching statements");
		return false;
	}
}

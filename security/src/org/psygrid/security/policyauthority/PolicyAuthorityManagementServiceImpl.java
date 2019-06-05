package org.psygrid.security.policyauthority;

import java.rmi.RemoteException;
import java.util.List;

import javax.xml.rpc.server.ServletEndpointContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.logging.AuditLogger;
import org.psygrid.security.PGSecurityConstants;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.RBACAction;
import org.psygrid.security.policyauthority.dao.DAOException;
import org.psygrid.security.policyauthority.dao.ObjectOutOfDateException;
import org.psygrid.security.policyauthority.dao.PolicyDAO;
import org.psygrid.security.policyauthority.model.Factory;
import org.psygrid.security.policyauthority.model.hibernate.Policy;
import org.psygrid.security.policyauthority.model.hibernate.Statement;
import org.psygrid.security.policyauthority.service.InputFaultMessage;
import org.psygrid.security.policyauthority.service.NotAuthorisedFaultMessage;
import org.psygrid.security.policyauthority.service.PolicyAuthorityManagementPortType;
import org.psygrid.security.policyauthority.service.ProcessingFaultMessage;
import org.psygrid.www.xml.security.core.types.ActionType;
import org.psygrid.www.xml.security.core.types.PolicyType;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.TargetType;

public class PolicyAuthorityManagementServiceImpl implements
		PolicyAuthorityManagementPortType {


		protected PolicyDAO policyDao = null;
		
		protected Factory factory = null;
		
		/** Logger */
		private static Log log = LogFactory
				.getLog(PolicyAuthorityManagementServiceImpl.class);
		
		protected static final String COMPONENT_NAME = "PolicyAuthority";
		
	    /**
		 * Audit logger
		 */
		protected static AuditLogger logHelper = new AuditLogger(PolicyAuthorityManagementServiceImpl.class);
		
		
		public PolicyDAO getPolicyDao() {
			return policyDao;
		}

		public void setPolicyDao(PolicyDAO policyDao) {
			this.policyDao = policyDao;
		}
		
		public void setFactory(Factory factory) {
			this.factory = factory;
		}

	
	public boolean addPolicy(PolicyType[] input) throws RemoteException,
			ProcessingFaultMessage, NotAuthorisedFaultMessage,
			InputFaultMessage {
		
		log.debug("addPolicy(" + ")");
		
		for (int i = 0; i < input.length; i++) {			
			Policy policy = factory.createPolicy(input[i]);
			try {
	
				if (policyDao.policyExists(input[i].getName(), input[i].getIdCode())) {
					//delete it first and then re-add
					Policy p = policyDao.getPolicyByName(input[i].getName());
					policyDao.removePolicy(p.getId());
				} 
		
				List<Statement> tempsl = policy.getStatements();
				policy.setStatements(null);
				policyDao.addPolicy(policy);
				Policy persistedPolicy = policyDao.getPolicyByName(policy.getPolicyName());
				for(Statement s : tempsl){
					s.attach(persistedPolicy);
					}
				policyDao.updatePolicy(persistedPolicy);						

			} catch (DAOException doa) {
				doa.printStackTrace();
				throw new ProcessingFaultMessage(doa.getMessage());
			} catch (ObjectOutOfDateException oood) {
				throw new ProcessingFaultMessage(oood.getMessage());
			} catch (Exception e){
				e.printStackTrace();
				throw new ProcessingFaultMessage(e.getMessage());
			}
		}
		return true;

	}

	
	public boolean addStatementToPolicy(PolicyType[] policy)
			throws RemoteException, ProcessingFaultMessage,
			NotAuthorisedFaultMessage, InputFaultMessage {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean deletePolicy(PolicyType[] deletePolicyRequest)
			throws RemoteException, ProcessingFaultMessage,
			NotAuthorisedFaultMessage, InputFaultMessage {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean deleteStatementFromPolicy(PolicyType[] policy)
			throws RemoteException, ProcessingFaultMessage,
			NotAuthorisedFaultMessage, InputFaultMessage {
		// TODO Auto-generated method stub
		return false;
	}

	
	public PolicyType[] getAllPolicies() throws RemoteException,
			ProcessingFaultMessage, NotAuthorisedFaultMessage,
			InputFaultMessage {
		// TODO Auto-generated method stub
		return null;
	}

	
	public PolicyType getPolicyByProject(PolicyType policy)
			throws RemoteException, ProcessingFaultMessage,
			NotAuthorisedFaultMessage, InputFaultMessage {
		// TODO Auto-generated method stub
		return null;
	}

	
	public String getVersion() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	
	public String retrieveConfiguration(boolean refresh)
			throws RemoteException, ProcessingFaultMessage,
			NotAuthorisedFaultMessage {
		// TODO Auto-generated method stub
		return null;
	}

	
	public boolean updateConfiguration(String configuration)
			throws RemoteException, ProcessingFaultMessage,
			NotAuthorisedFaultMessage, InputFaultMessage {
		// TODO Auto-generated method stub
		return false;
	}

}

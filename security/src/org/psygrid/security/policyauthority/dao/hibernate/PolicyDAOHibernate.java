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


//Created on Oct 27, 2005 by John Ainsworth

package org.psygrid.security.policyauthority.dao.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.psygrid.security.RBACAction;
import org.psygrid.security.RBACTarget;
import org.psygrid.security.policyauthority.dao.DAOException;
import org.psygrid.security.policyauthority.dao.ObjectOutOfDateException;
import org.psygrid.security.policyauthority.dao.PolicyDAO;
import org.psygrid.security.policyauthority.model.IPolicy;
import org.psygrid.security.policyauthority.model.hibernate.Action;
import org.psygrid.security.policyauthority.model.hibernate.Argument;
import org.psygrid.security.policyauthority.model.hibernate.Authority;
import org.psygrid.security.policyauthority.model.hibernate.Group;
import org.psygrid.security.policyauthority.model.hibernate.Policy;
import org.psygrid.security.policyauthority.model.hibernate.Privilege;
import org.psygrid.security.policyauthority.model.hibernate.Statement;
import org.psygrid.security.policyauthority.model.hibernate.Target;
import org.psygrid.security.utils.TargetAssessor;
import org.psygrid.www.xml.security.core.types.PolicyType;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * @author John Ainsworth (stolen, cloned and adapted from the Rob Harper
 *         original)
 * 
 */

public class PolicyDAOHibernate extends HibernateDaoSupport implements
		PolicyDAO {

	/**
	 * Retrieve a list of the known policies from the database.
	 * 
	 * @return List of policies.
	 */
	@SuppressWarnings("unchecked")
	public List<Policy> getPolicies() {
		return getHibernateTemplate().find("from org.psygrid.security.policyauthority.model.hibernate.Policy");
	}

	/**
	 * Retrieve a policies from the database with the specified name
	 * 
	 * @param name
	 *            The name of the policy
	 * @return List of policies.
	 */
	@SuppressWarnings("unchecked")
	public Policy getPolicyByName(String name) throws DAOException {
		Policy found = null;
		List<Policy> pl = getHibernateTemplate().find(
				"from Policy x where x.policyName = ?", name);
		if (!pl.isEmpty()) {
			for (int i = 1; i < pl.size(); i++) {
				// delete the other policies
				// should not be any!
				removePolicy(pl.get(i).getId());
			}
			found = pl.get(0);
		}
		return found;
	}
	
	/**
	 * Retrieve a policy's authorities from the database with the specified name
	 * 
	 * @param name
	 *            The name of the policy
	 * @return List of authorities.
	 */
	@SuppressWarnings("unchecked")
	public List<Authority> getAuthorities(String name, String id) {
		List<Authority> la = getHibernateTemplate().find(
				"select authorities " +
				"from Policy as policy " +
				"join policy.authorities as authorities " +
				"where (policy.policyName = ? or policy.idCode =?) ", new Object[]{name, id});
		return la;
	}

	/**
	 * Retrieve a policies from the database with the specified name or IdCode
	 * 
	 * @param name
	 *            A policy type object
	 * @return List of policies.
	 */
	public Policy getPolicy(PolicyType pt) throws DAOException {
		Policy p = null;
		if (pt.getName() != null) {
			p = getPolicyByName(pt.getName());
		}
		if ((p == null) && (pt.getIdCode() != null)) {
			p = getPolicyByIdCode(pt.getIdCode());
		}
		return p;
	}

	/**
	 * Retrieve a policies from the database with the specified idcode
	 * 
	 * @param name
	 *            The idcode of the policy
	 * @return List of policies.
	 */
	@SuppressWarnings("unchecked")
	public Policy getPolicyByIdCode(String idcode) throws DAOException {
		Policy found = null;
		List<Policy> pl = getHibernateTemplate().find(
				"from Policy x where x.idCode = ?", idcode);
		if (!pl.isEmpty()) {
			for (int i = 1; i < pl.size(); i++) {
				// delete the other policies
				// should not be any!
				removePolicy(pl.get(i).getId());
			}
			found = pl.get(0);
		}
		return found;
	}

	/**
	 * Retrieve a single policy from the database.
	 * 
	 * @param policyId
	 *            Unique identifier of the Policy to retrieve.
	 * @return The Policy with the unique identifier in the argument.
	 * @throws DAOException
	 *             if no Policy exists with the unique identifier specified in
	 *             the argument.
	 */
	public IPolicy getPolicy(final Long policyId) throws DAOException {
		HibernateCallback callback = new HibernateCallback() {
			public Object doInHibernate(Session session) {
				Policy policy = (Policy) session.createCriteria(Policy.class)
						.add(Restrictions.idEq(policyId)).uniqueResult();
				return policy;
			}
		};

		IPolicy policy = (IPolicy) getHibernateTemplate().execute(callback);
		if (null == policy) {
			throw new DAOException(
					"No Policy exists in the repository for id = " + policyId);
		}
		return policy;
	}

	/**
	 * Add a single policy to the repository.
	 * 
	 * @param policy
	 *            The Policy to add.
	 * @throws DAOException
	 *             if the policy exists.
	 */
	public void addPolicy(IPolicy policy) throws DAOException,
			ObjectOutOfDateException {

		Policy p = (Policy) policy;
		try {
			getHibernateTemplate().saveOrUpdate(p);
		} catch (HibernateOptimisticLockingFailureException ex) {
			throw new ObjectOutOfDateException(
					"Cannot save policy - the object is out-of-date", ex);
		}
	}

	/**
	 * Remove a single Policy from the data repository.
	 * 
	 * @param policyId
	 *            Unique identifier of the Policy to remove.
	 * @throws DAOException
	 *             if no Policy exists with the unique identifier specified in
	 *             the argument
	 */
	public void removePolicy(Long policyId) throws DAOException {
		HibernateTemplate ht = getHibernateTemplate();
		Policy policy = (Policy) ht.get(Policy.class, policyId);
		if (null == policy) {
			throw new DAOException("No policy exists in the database for id = "
					+ policyId);
		}
		ht.delete(policy);
	}

	/**
	 * Update a policy's record.
	 * 
	 * @param policy.
	 *            The policy to update
	 * @throws DAOException
	 *             if no policy exists with the policy.policyName;
	 */
	public void updatePolicy(IPolicy p) throws DAOException,
			ObjectOutOfDateException {

		Policy policy = (Policy) p;
//		if (null != policy.getId()) {
//			// for existing policies, check that it has not been deleted
//			Policy storedPolicy = (Policy) getHibernateTemplate().get(
//					Policy.class, policy.getId());
//			if (null == storedPolicy) {
//				// policy has been deleted by another session
//				throw new ObjectOutOfDateException(
//						"Cannot save policy - the object is out-of-date");
//			}
//		}
		try {
			getHibernateTemplate().saveOrUpdate(policy);
		} catch (HibernateOptimisticLockingFailureException ex) {
			throw new ObjectOutOfDateException(
					"Cannot save policy - the object is out-of-date", ex);
		}
	}
//	public void addStatementToPolicy(PolicyType p)throws DAOException,
//	ObjectOutOfDateException{	
//		final PolicyType pt = p;
//		HibernateCallback callback = new HibernateCallback() {
//			public Object doInHibernate(Session session) {
//
//				Policy policy = null;
//				if (pt.getName() != null) {
//					policy = (Policy) session.createQuery(
//							"from Policy x where x.policyName = ?").setString(
//							0, pt.getName()).uniqueResult();
//				}
//				if (pt.getIdCode() != null && policy == null) {
//					policy = (Policy) session.createQuery(
//							"from Policy x where x.idCode = ?").setString(0,
//							pt.getIdCode()).uniqueResult();
//				}
//				if (policy != null) {
//					for (int i = 0; i < pt.getStatement().length; i++) {
//						Statement st = Statement.fromStatementType(pt
//								.getStatement()[i]);
//						boolean rc = false;
//						if (policy.isSupported(st.getAction(), st.getTarget())) {
//							rc = policy.removeStatement(st);
//							session.evict(st);
//						}
//						rc = policy.getStatements().add(st);
//					}
//					session.saveOrUpdate(policy);
//				}
//				return policy;
//			}
//		};
//
//		try {
//			Object result = (Object) getHibernateTemplate().execute(callback);
//			if(result==null){
//				throw new DAOException("Policy does nor exist: "+p.getName()+" "+p.getIdCode());
//			}
//			if (result instanceof DAOException) {
//				((DAOException)result).printStackTrace();
//				throw (DAOException) result;
//			}
//		} catch (HibernateOptimisticLockingFailureException ex) {
//			throw new ObjectOutOfDateException(
//					"Could not save record - the object is out-of-date", ex);
//		} catch (DataAccessException ex) {
//			throw new DAOException("Could not save record", ex);
//		}		
//	}
	
	public void deleteStatementFromPolicy(PolicyType p)throws DAOException,
	ObjectOutOfDateException{
		final PolicyType pt = p;
		HibernateCallback callback = new HibernateCallback() {
			public Object doInHibernate(Session session) {

				Policy policy = null;
				if (pt.getName() != null) {
					policy = (Policy) session.createQuery(
							"from Policy x where x.policyName = ?").setString(
							0, pt.getName()).uniqueResult();
				}
				if (pt.getIdCode() != null && policy == null) {
					policy = (Policy) session.createQuery(
							"from Policy x where x.idCode = ?").setString(0,
							pt.getIdCode()).uniqueResult();
				}
				if (policy != null) {
					for (int i = 0; i < pt.getStatement().length; i++) {
						Statement st = Statement.fromStatementType(pt
								.getStatement()[i]);
						boolean rc = false;
						if (policy.isSupported(st.getAction(), st.getTarget())) {
							rc = policy.removeStatement(st);
							session.evict(st);
						}
					}
					session.saveOrUpdate(policy);
				}
				return policy;
			}
		};

		try {
			Object result = (Object) getHibernateTemplate().execute(callback);
			if(result==null){
				throw new DAOException("Policy does nor exist: "+p.getName()+" "+p.getIdCode());
			}
			if (result instanceof DAOException) {
				throw (DAOException) result;
			}
		} catch (HibernateOptimisticLockingFailureException ex) {
			throw new ObjectOutOfDateException(
					"Could not save record - the object is out-of-date", ex);
		} catch (DataAccessException ex) {
			throw new DAOException("Could not save record", ex);
		}		
	}
	
	
	protected List<Statement> selectMatchingStatementsFromPolicy(Policy policy, Target target, Action action, boolean retrieveGroupSpecificStatements){
		//Get the old statements.
		
		String groupSpecificStatement = "select statement " +
						"from Policy as policy " +
						"join policy.statements as statement " +
						"where (policy.policyName = ? or policy.idCode = ?) " +
						"and (statement.target.targetName = ? or statement.target.idCode = ? " +
						"or statement.target.targetName = ? or statement.target.idCode = ?" +
						"or statement.target.targetName=  ? or statement.target.idCode = ?)" +
						"and (statement.action.actionName = ? or statement.action.actionName= ?)";
		
		Object[] groupSpecificArray = new Object[] {policy.getPolicyName(), policy.getIdCode(), 
				target.getTargetName(),target.getIdCode(), 
				RBACTarget.ANY.toString(), RBACTarget.ANY.idAsString(), 
				RBACTarget.GROUP_INCLUSION.toString(), RBACTarget.GROUP_INCLUSION.idAsString(),
				action.getActionName(), RBACAction.ANY.toString()};
		
		String nonGroupSpecificStatement = "select statement " +
						"from Policy as policy " +
						"join policy.statements as statement " +
						"where (policy.policyName = ? or policy.idCode = ?) " +
						"and (statement.target.targetName = ? or statement.target.idCode = ? " +
						"or statement.target.targetName = ? or statement.target.idCode = ?)" +
						"and (statement.action.actionName = ? or statement.action.actionName= ?)";
		
		Object[] nonGroupSpecificArray = new Object[] {policy.getPolicyName(), policy.getIdCode(), 
				target.getTargetName(),target.getIdCode(), 
				RBACTarget.ANY.toString(), RBACTarget.ANY.idAsString(), 
				action.getActionName(), RBACAction.ANY.toString()};
		
		List<Statement> sl = getHibernateTemplate().find(
				retrieveGroupSpecificStatements ? groupSpecificStatement : nonGroupSpecificStatement, retrieveGroupSpecificStatements ? groupSpecificArray : nonGroupSpecificArray);
		
		return sl;
	}
	
	
	private void modifyGroupSpecificRules(List<Statement> statements, Target target){
		
		List<Statement> groupSpecificStatements = new ArrayList<Statement>();
		for(Statement s : statements){
			if(s.getTarget().getTargetName().equals(RBACTarget.GROUP_INCLUSION.toString())){
				Group g = new Group(target.getTargetName(), target.getIdCode());
				Argument a = new Argument(g , true);
				s.getRule().setAdditionalArgument(a);
			}
		}
		
	}
	
	/* (non-Javadoc)
	 * @see org.psygrid.security.policyauthority.dao.PolicyDAO#getMatchingStatementsFromPolicy(org.psygrid.security.policyauthority.model.hibernate.Policy, org.psygrid.security.policyauthority.model.hibernate.Target, org.psygrid.security.policyauthority.model.hibernate.Action)
	 */
	public List<Statement> getMatchingStatementsFromPolicy(Policy policy, Target target, Action action){
		Statement found = null;
		
		boolean targetIsCentre = TargetAssessor.targetIsCentre(target);
		List<Statement> statements = null;
		
		statements = selectMatchingStatementsFromPolicy(policy, target, action, targetIsCentre);
		
		if(targetIsCentre){
			modifyGroupSpecificRules(statements, target);
		}

		return statements;
		
	}
	
	/* (non-Javadoc)
	 * @see org.psygrid.security.policyauthority.dao.PolicyDAO#getMatchingStatementsFromPolicy(org.psygrid.security.policyauthority.model.hibernate.Policy, org.psygrid.security.policyauthority.model.hibernate.Action)
	 */
	public List<Statement> getMatchingStatementsFromPolicy(Policy policy, Action action){
		Statement found = null;
		
		List<Statement> sl = getHibernateTemplate().find(
				"select statement " +
				"from Policy as policy " +
				"join policy.statements as statement " +
				"where (policy.policyName = ? or policy.idCode = ?) " +
				"and (statement.action.actionName = ? or statement.action.actionName= ?)", 
				new Object[]{policy.getPolicyName(), policy.getIdCode(), 
					action.getActionName(), RBACAction.ANY.toString()});
		return sl;
		
	}
	
	public boolean policyExists(String name, String idCode){
		boolean found = false;
		
		List<String> sl = getHibernateTemplate().find(
				"select policy.policyName " +
				"from Policy as policy " +
				"where (policy.policyName = ? or policy.idCode = ?) ", 
				new Object[]{name, idCode});
		if(sl.size()>0){
			found = true;
		}
		return found;
		
	}

}

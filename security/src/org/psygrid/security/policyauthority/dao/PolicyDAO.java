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



package org.psygrid.security.policyauthority.dao;

import java.util.List;

import org.psygrid.security.policyauthority.model.IPolicy;
import org.psygrid.security.policyauthority.model.hibernate.Action;
import org.psygrid.security.policyauthority.model.hibernate.Authority;
import org.psygrid.security.policyauthority.model.hibernate.Policy;
import org.psygrid.security.policyauthority.model.hibernate.Statement;
import org.psygrid.security.policyauthority.model.hibernate.Target;
import org.psygrid.www.xml.security.core.types.PolicyType;

/**
 * @author John Ainsworth
 *
 */
public interface PolicyDAO {

	
    /**
     * Retrieve a list of the known Policies from the database.
     * 
     * @return List of policys.
     */
    public List<Policy> getPolicies()
           throws DAOException;

    /**
     * Retrieve a policy with the name specified from the database.
     * 
     * @param name The name of the policy
     * @return Policy
     */
    public Policy getPolicyByName(String name)
    		throws DAOException;
 
    
	public Policy getPolicy(PolicyType pt) throws DAOException;
		

    /**
     * Retrieve a policy with the id code specified from the database.
     * 
     * @param name The idCode of the policy
     * @return Policy
     */
    public Policy getPolicyByIdCode(String idCode)
    		throws DAOException;
    
    /**
     * Retrieve a single policy from the database.
     * 
     * @param policyId Unique identifier of the Policy to retrieve.
     * @return The Policy with the unique identifier in the argument.
     * @throws DAOException if no Policy exists with the unique 
     * identifier specified in the argument.
     */
    public IPolicy getPolicy(Long policyId) 
        throws DAOException;
   
    
    /**
     * Add a single policy to the repository.
     * 
     * @param policy The Policy to add.
     * @throws DAOException if the policy exists.
     */
    public void addPolicy(IPolicy policy)
        throws DAOException, ObjectOutOfDateException;
    
    /**
     * Remove a single Policy from the data repository.
     * 
     * @param policyId Unique identifier of the Policy to remove.
     * @throws DAOException if no Policy exists with the unique 
     * identifier specified in the argument
     */
    public void removePolicy(Long policyId) 
        throws DAOException, ObjectOutOfDateException;
    
    /**
     * Update a policy's record.
     * 
     * @param policy. The policy to update
     * @throws DAOException if no policy exists with the policy.policyName;
     */
    public void updatePolicy(IPolicy policy) 
        throws DAOException, ObjectOutOfDateException;
        
    /**
	 * Delete statements from a policy.
	 * 
	 * @param policy.
	 *            The policy to update
	 * @throws DAOException
	 *             if no policy exists with the policy.policyName;
	 */
	public void deleteStatementFromPolicy(PolicyType policy)
			throws DAOException, ObjectOutOfDateException;
	
	/**
	 * Query the database to find all the statements inb the policy which match
	 * the target and actions
	 * @param policy
	 * @param target
	 * @param action
	 * @return A List of the statements found
	 */
	public List<Statement> getMatchingStatementsFromPolicy(Policy policy, Target target, Action action);
	
	/**
	 * Query the database to find all the statements inb the policy which match
	 * the actions
	 * @param policy
	 * @param action
	 * @return A List of the statements found
	 */
	public List<Statement> getMatchingStatementsFromPolicy(Policy policy, Action action);
    /**
     * Retrieve a list of authrotieis with the policy specified from the database.
     * 
     * @param name The name of the policy
     * @param id The ID of the policy
     * @return List of Authority
     */
    public List<Authority> getAuthorities(String name, String id);   

    /**
     * Check a policy with the name specified exists in the database.
     * 
     * @param name The name of the policy
     * @param idCode The idCode of the policy
     * @return boolean
     */
	public boolean policyExists(String name, String idCode);
}

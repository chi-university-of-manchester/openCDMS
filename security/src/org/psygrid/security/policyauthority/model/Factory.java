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

package org.psygrid.security.policyauthority.model;

import java.util.ArrayList;

import org.psygrid.security.policyauthority.model.hibernate.Action;
import org.psygrid.security.policyauthority.model.hibernate.CompositeRule;
import org.psygrid.security.policyauthority.model.hibernate.Policy;
import org.psygrid.security.policyauthority.model.hibernate.Statement;
import org.psygrid.security.policyauthority.model.hibernate.Target;
import org.psygrid.www.xml.security.core.types.CompositeRuleType;
import org.psygrid.www.xml.security.core.types.PolicyType;

/**
 * Factory interface used to manage the creation of data repository
 * model objects.
 * 
 * @author Rob Harper, clone and adapted by John Ainsworth
 *
 */
public interface Factory {
    
    /**
     * Create a new role with the given name.
     * 
     * @param name The name of the new role.
     * @return The new role.
     */
    public IRole createRole(String name);
 
    /**
     * Create a new action with the given name.
     * 
     * @param name The name of the new action.
     * @return The new action.
     */
    public IAction createAction(String name);
    
    /**
     * Create a new target with the given name.
     * 
     * @param name The name of the new target.
     * @return The new target.
     */
    public ITarget createTarget(String name, String idcode);
 
    /**
     * Create a new rule from the type
     * 
     * @param rule The new rule specification.
     * @return The new rule.
     */
    public IRule createRule(CompositeRuleType rule);
    
    /**
     * Create a new action with the given name and list of roles
     * 
     * @param t The target of the statement.
     * @param a The action of the statement.
     * @param r The rule of the statement.
     * @return The new action.
     */
    public IStatement createStatement(Target t, Action a, CompositeRule r);
 
    /**
     * Create a new policy with the given name.
     * 
     * @param name The name of the new policy.
     * @return The new policy.
     */   
    public IPolicy createPolicy(String name);
 
    /**
     * Create a new policy from the supplied object.
     * 
     * @param p The complete specification of the policy
     * @return The new policy.
     */   
    public Policy createPolicy(PolicyType p);
    
    /**
     * Create a new policy with the given name and list of actions
     * 
     * @param name The name of the new policy.
     * @param id The id of the policy
     * @param actions The list of actions for this policy
     * @return The new policy.
     */
    public Policy createPolicy(String name, String id, ArrayList<Statement> actions);
}


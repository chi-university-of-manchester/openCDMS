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

package org.psygrid.security.policyauthority.model.hibernate;

import java.util.ArrayList;

import org.psygrid.security.policyauthority.model.Factory;
import org.psygrid.security.policyauthority.model.IAction;
import org.psygrid.security.policyauthority.model.IPolicy;
import org.psygrid.security.policyauthority.model.IRole;
import org.psygrid.security.policyauthority.model.IRule;
import org.psygrid.security.policyauthority.model.IStatement;
import org.psygrid.security.policyauthority.model.ITarget;
import org.psygrid.www.xml.security.core.types.CompositeRuleType;
import org.psygrid.www.xml.security.core.types.PolicyType;

public class HibernateFactory implements Factory {

    public IRole createRole(String name) {
        return new Role(name);
    }
    public ITarget createTarget(String name, String idcode) {
        return new Target(name, idcode);
    }
    public IAction createAction(String name) {
        return new Action(name);
    }
    public IRule createRule(CompositeRuleType rule) {
        return CompositeRule.fromExternalType(rule);
    }
    public IStatement createStatement(Target target, Action action, CompositeRule rule) {
        return new Statement(target, action, rule);
    }
    
    public IPolicy createPolicy(String name) {
        return new Policy(name);
    }
    
    public Policy createPolicy(String name, String id, ArrayList<Statement> groups) {
        return new Policy(name, id, groups);
    }
   
    public Policy createPolicy(PolicyType pt){
    		return Policy.fromPolicyType(pt);
    }
}

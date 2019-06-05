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


//Created on Nov 29, 2005 by John Ainsworth



package org.psygrid.security.policyauthority.model.hibernate;

import java.util.ArrayList;
import java.util.List;


/**
 * @author jda
 * @hibernate.joined-subclass table="t_rules"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class Rule extends Persistent {
	/**
	 * Children of the composite rule
	 */
	protected List<Rule> children = new ArrayList<Rule>();
	
	/**
	 * Default no-arg constructor, as required by the Hibernate framework
	 * for all persistable classes.
	 * 
	 */
	protected Rule(){};
	
	private Argument additionalArgument = null;
	
	public  void print(){
		for(int i=0;i<children.size();i++){
			children.get(i).print();
		}
	}
	
	protected boolean specifiesARole(){return true;}
	
	protected boolean specifiesAndRelationshipBetweenRoles(){return true;}
	
	public boolean canBeTransformed(){return false;}
	
	public  boolean isPermitted(List<Privilege> privilegeList){return false;}

	public  Rule getChild(int i){return children.get(i);}

	public  Object toExternalType(){return null;}
	
	/**
     * Get the children
     * 
     * @return A list containing the children.
     * @hibernate.list cascade="all-delete-orphan" lazy="false"
     * @hibernate.one-to-many class="org.psygrid.security.policyauthority.model.hibernate.Rule" 
     * @hibernate.key column="c_parent_id" not-null="false"
     * @hibernate.list-index column="c_index"
     */
	public List<Rule> getChildren() {
		return this.children;
	}

	/**
	 * @param children The children to set.
	 */
	public void setChildren(List<Rule> arguments) {
		this.children = arguments;
	}
	
	/**
	 * Attach the rule to the persisted policy supplied
	 * 
	 * @param p The persisted policy to attach to
	 * @return False if any point of attachment fails 
	 * 
	 */
	public boolean attach(Policy p){	
		return false;
	}

	public void setAdditionalArgument(Argument additionalArgument) {
		this.additionalArgument = additionalArgument;
	}

	public Argument getAdditionalArgument() {
		return additionalArgument;
	}
}

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



package org.psygrid.security.policyauthority.model.hibernate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.security.policyauthority.model.IRule;
import org.psygrid.www.xml.security.core.types.ArgumentType;
import org.psygrid.www.xml.security.core.types.CompositeRuleType;
import org.psygrid.www.xml.security.core.types.OperatorType;
import org.w3c.dom.Element;

/**
 * @author jda
 *
 * @hibernate.joined-subclass table="t_compositerules"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class CompositeRule extends Rule implements IRule {
	private static Log sLog = LogFactory.getLog(CompositeRule.class);
		 
	private String operator;

	
	/**
	 * Default no-arg constructor, as required by the Hibernate framework
	 * for all persistable classes.
	 * 
	 */
	protected CompositeRule(){};
	
	
    /**
     * Constructor that accepts the operator and a list
     * of children
     *  
     * @param op The operator type.
     * @param args A list of conditions
     */
    public CompositeRule(String op, ArrayList<Rule> args){
		this.setOperator(op);
		this.setChildren(args); 
    }   

    public Element toDOM(){
    		return null; 
    }
    
    public static CompositeRule fromExternalType(CompositeRuleType r) {
		CompositeRule rule = new CompositeRule();
		ArrayList<Rule> argList = new ArrayList<Rule>();
		if (r != null) {
				if (r.getChildren() != null) {
					for (int i = 0; i < r.getChildren().length; i++) {
						argList.add(CompositeRule.fromExternalType(r.getChildren()[i]));
					}
				}	
				if(r.getPrivilege()!=null){
					for(int i=0;i<r.getPrivilege().length;i++){
						argList.add(Argument.fromExternalType(r.getPrivilege()[i]));
					}				
				}
				rule.setChildren(argList);
				rule.setOperator(r.getOperator().getValue());	
		}
		return rule;
	}
    
	public CompositeRuleType toExternalType() {
		OperatorType ot = OperatorType.fromValue(this.getOperator());
		ArrayList<ArgumentType> ata = new ArrayList<ArgumentType>();
		ArrayList<CompositeRuleType> crta = new ArrayList<CompositeRuleType>();
		for (int i = 0; i < getChildren().size(); i++) {
			if(getChildren().get(i) instanceof Argument){
				ata.add(((Argument)getChildren().get(i)).toExternalType());
			}
			if(getChildren().get(i) instanceof CompositeRule){
				crta.add(((CompositeRule)getChildren().get(i)).toExternalType());
			}
		}
		return new CompositeRuleType(ot, crta.toArray(new CompositeRuleType[crta.size()]), ata.toArray(new ArgumentType[ata.size()]));
	}
	
    public void print(){
		sLog.info("CompositeRule: "+toString()+"\n\tID: "+this.getId()+"\n\tVersion: "+getVersion()+"\n\tType: "+this.getOperator());
		for(int i=0;i<children.size();i++){
			children.get(i).print();
		}
	}
	
    
	protected boolean specifiesARole(){
		
		boolean specifiesARole = false;
		
		for(Rule r : children){
			
			if(r.specifiesARole()){
				specifiesARole = true;
				break;
			}
			
		}
		
		return specifiesARole;
	}
	
	protected boolean specifiesAndRelationshipBetweenRoles(){
		
		for(Rule r : children){
			if(r.specifiesAndRelationshipBetweenRoles()){
				return true;
			}
		}
		
		if(getOperator().equals("Or")){
			return false;
		}
		
		//If the logic get this far it means that as yet, none of the children, themselves specify and and rlt'nship b/t roles. Must now check wrt the
		//and operator within this object
		
		int roleSpecificationCount = 0;
		
		for(Rule r : children){
			if(r.specifiesARole() == true)
				roleSpecificationCount++;
		}
		
		if(roleSpecificationCount > 1){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean canBeTransformed(){
			
		if(this.specifiesAndRelationshipBetweenRoles())
			return false;
		else
			return true;
	}
    
	public boolean isPermitted(List<Privilege> lp){
		boolean rc = true;
		if(getOperator().equals("And")){
			//is each of the children in the lr?
			Iterator<Rule> ia = getChildren().iterator();
			while(ia.hasNext()&&rc){
				Rule r=ia.next();
				rc = r.isPermitted(lp);
			}
		} else if (getOperator().equals("Or")) {
			rc = false;
			Iterator<Rule> ia = getChildren().iterator();
			while (ia.hasNext() && !rc) {
				Rule r = ia.next();
				rc = r.isPermitted(lp);
			}				
		}	
		
		if(rc && this.getAdditionalArgument() != null){
			rc = getAdditionalArgument().isPermitted(lp);
		}
		
		return rc;
	}
	
	public Rule getChild(int i){
		return this.getChildren().get(i);
	}
	
	/**
	 * Get the operator
	 * 
	 * @return The operator.
	 * @hibernate.property column = "c_operator" lazy="false"
	 */
	public String getOperator() {
		return this.operator;
	}
	
	/**
	 * @param operator The operator to set.
	 */
	public void setOperator(String operator) {
		this.operator = operator;
	}
	
	/**
	 * Attach the rule to the persisted policy supplied
	 * 
	 * @param p
	 *            The persisted policy to attach to
	 * @return False if any point of attachment fails
	 * 
	 */
	public boolean attach(Policy p){
		boolean found = true;
		Iterator<Rule> ia = getChildren().iterator();
		while(ia.hasNext()&&found){
			Rule r=ia.next();
			found = r.attach(p);
		}
		return found;
	}
}

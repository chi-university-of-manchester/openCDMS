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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.security.policyauthority.model.IOperator;
import org.psygrid.www.xml.security.core.types.OperatorType;
import org.w3c.dom.Element;

/**
 * @author jda
 * @hibernate.joined-subclass table="t_operators"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class Operator extends Persistent implements IOperator {
	private static Log sLog = LogFactory.getLog(Operator.class);

	/**
	 * Operator identity
	 */
	private String operatorName;

	/**
	 * Default no-arg constructor, as required by the Hibernate framework for
	 * all persistable classes.
	 * 
	 */
	protected Operator() {
	};

	/**
	 * Constructor that accepts the name of the operator
	 * 
	 * @param operatorName
	 *            The name of the operator.
	 */
	public Operator(String operatorName) {
		this.setOperatorName(operatorName);
	}

	/**
	 * Get the operatorName
	 * 
	 * @return The operatorName.
	 * @hibernate.property column = "c_operator_name" lazy="false"
	 */
	public String getOperatorName() {
		return operatorName;
	}

	/**
	 * Set the operatorName
	 * 
	 * @param operatorName
	 *            The operator name.
	 */
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public Element toDOM() {
		return null;
	}

	public void print(){
		sLog.info("Operator: "+toString()+"\n\tID: "+this.getId()+"\n\tVersion: "+getVersion()+"\n\tType: "+this.getOperatorName());
	}
	
	public static Operator fromOperatorType(OperatorType ot){
		return new Operator(ot.getValue());
	}
}

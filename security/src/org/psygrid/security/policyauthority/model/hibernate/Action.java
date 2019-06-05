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
import org.psygrid.security.policyauthority.model.IAction;
import org.psygrid.www.xml.security.core.types.ActionType;
import org.w3c.dom.Element;

/**
 * @author jda
 * @hibernate.joined-subclass table="t_actions"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class Action extends Persistent implements IAction {
	private static Log sLog = LogFactory.getLog(Action.class);


	/**
	 * Action identity
	 */
	private String actionName;
	
	/**
	 * Action identity
	 */
	private String idCode;

	/**
	 * Default no-arg constructor, as required by the Hibernate framework for
	 * all persistable classes.
	 * 
	 */
	protected Action() {}

	/**
	 * Constructor that accepts the name of the action
	 * 
	 * @param actionName
	 *            The name of the action.
	 */
	public Action(String actionName) {
		this.setActionName(actionName);
	}

	/**
	 * Constructor that accepts the name of the action
	 * 
	 * @param actionName
	 *            The name of the action.
	 * @param idCode
	 *            The id of the action.
	 */
	public Action(String actionName, String idCode) {
		this.setActionName(actionName);
		this.setIdCode(idCode);
	}
	
	/**
	 * Get the actionName
	 * 
	 * @return The actionName.
	 * @hibernate.property column = "c_action_name" lazy="false"
	 */
	public String getActionName() {
		return actionName;
	}

	/**
	 * Set the actionName
	 * 
	 * @param actionName
	 *            The action name.
	 */
	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public Element toDOM() {
		return null;
	}

	public static Action fromActionType(ActionType rt) {
		Action r = new Action();
		if (rt != null) {
			r.setActionName(rt.getName());
			r.setIdCode(rt.getIdCode());
		}
		return r;
	}
	
	public ActionType toActionType() {
		ActionType r = new ActionType(this.getActionName(), this.getIdCode());
		return r;
	}
	
	public void print(){
		sLog.info("Action: "+toString()+"\n\tID: "+this.getId()+"\n\tVersion: "+getVersion()+"\n\tName: "+this.getActionName());
	}

	/**
	 * @return Returns the idCode.
	 * @hibernate.property column = "c_action_id" lazy="false"
	 */
	public String getIdCode() {
		return this.idCode;
	}

	/**
	 * @param idCode The idCode to set.
	 */
	public void setIdCode(String actionId) {
		this.idCode = actionId;
	}
	
	public boolean isEqual(Action a) {
		boolean result = false;
		if ((a.getActionName() != null) && (getActionName() != null)) {
			if ((getActionName().equals(a.getActionName()) 
					&& (!a.getActionName().equals("")))) {
				result = true;
			}
		}
		if ((a.getIdCode() != null) && (getIdCode()!=null)) {
			if (getIdCode().equals(a.getIdCode())
					&& (!a.getIdCode().equals(""))) {
				result = true;
			}
		}
		return result;
	}
}


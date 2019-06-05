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
package org.psygrid.data.model.dto;

public class AdminInfo {
	
	public final static String APPROVE = "approve"; 
	
	private String description = null;
	private String who = null;
	private boolean elementActive = false;
	private String registrar = null;
	private String terminologicalReference = null;
	private String actionTaken;

	public AdminInfo(){
		
	}
	
	public AdminInfo(String action, String description, boolean elementActive, String registrar, String terminologicalReference){
		this.description = description;
		this.elementActive = elementActive;
		this.registrar = registrar;
		this.terminologicalReference = terminologicalReference;
		this.actionTaken = action.toString();
	}

	public String getDescription() {
		return description;
	}

	public boolean getElementActive() {
		return elementActive;
	}
	
	public void setElementActive(boolean elementActive) {
		this.elementActive = elementActive;
	}


	public String getRegistrar() {
		return registrar;
	}


	public String getTerminologicalReference() {
		return terminologicalReference;
	}

	public String getWho() {
		return who;
	}

	public void setWho(String who) {
		this.who = who;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setRegistrar(String registrar) {
		this.registrar = registrar;
	}

	public void setTerminologicalReference(String terminologicalReference) {
		this.terminologicalReference = terminologicalReference;
	}

	public String getActionTaken() {
		return actionTaken;
	}

	public void setActionTaken(String actionTaken) {
		this.actionTaken = actionTaken;
	}
	
	public org.psygrid.data.model.hibernate.AdminInfo toHibernate() {
		org.psygrid.data.model.hibernate.AdminInfo hAI = new org.psygrid.data.model.hibernate.AdminInfo();
		
		hAI.setActionTaken(actionTaken);
		hAI.setDescription(description);
		hAI.setElementActive(elementActive);
		hAI.setRegistrar(registrar);
		hAI.setTerminologicalReference(terminologicalReference);
		hAI.setWho(who);
		
		return hAI;
	}
}

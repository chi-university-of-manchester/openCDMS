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

import java.util.Date;
import java.util.Map;

/**
 * @author Rob Harper
 *
 */
public class ChangeHistoryDTO extends PersistentDTO {

	/**
	 * The DN of the user making the change.
	 */
	protected String user;
	
	/**
	 * The date when the change is made.
	 */
	protected Date when;
	
	protected Date whenSystem;
	
	/**
	 * The action taken (e.d. add, edit)
	 */
	protected String action;
	
	protected Long parentId;
	
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Date getWhen() {
		return when;
	}

	public void setWhen(Date when) {
		this.when = when;
	}

	public Date getWhenSystem() {
		return whenSystem;
	}

	public void setWhenSystem(Date whenSystem) {
		this.whenSystem = whenSystem;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	@Override
	public org.psygrid.data.model.hibernate.ChangeHistory toHibernate(
			Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs) {
        //check for an already existing instance of a hibernate object for this 
        //change history in the map of references
        org.psygrid.data.model.hibernate.ChangeHistory hCH = null;
        if ( hRefs.containsKey(this)){
        	hCH = (org.psygrid.data.model.hibernate.ChangeHistory)hRefs.get(this);
        }
        if ( null == hCH ){
            //an instance of the change history has not already
            //been created, so create it, and add it to the 
            //map of references
        	hCH = new org.psygrid.data.model.hibernate.ChangeHistory();
            hRefs.put(this, hCH);
            toHibernate(hCH, hRefs);
        }

        return hCH;
	}

    public void toHibernate(org.psygrid.data.model.hibernate.ChangeHistory hCH, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hCH, hRefs);
        hCH.setWhen(this.when);
        hCH.setWhenSystem(this.whenSystem);
        hCH.setUser(this.user);
        hCH.setAction(this.action);
        hCH.setParentId(this.parentId);
    }
}

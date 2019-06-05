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

import java.util.HashMap;
import java.util.Map;

public class LSIDAuthorityDTO extends PersistentDTO {
	
	private String authorityID;
	
	/* Used only for wsdl marshalling
	 */
	public LSIDAuthorityDTO(){
		
	}
	
	public LSIDAuthorityDTO(String authorityID){
		this.authorityID = authorityID;
	}
	
	public org.psygrid.data.model.hibernate.LSIDAuthority toHibernate(){
		Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> dtoRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
		return (org.psygrid.data.model.hibernate.LSIDAuthority)toHibernate(dtoRefs);
	}
	
	@Override
	public org.psygrid.data.model.hibernate.LSIDAuthority toHibernate(
			Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs) {
		
		org.psygrid.data.model.hibernate.LSIDAuthority hibAuthority = null;
		if(hRefs.containsKey(this)){
			hibAuthority = (org.psygrid.data.model.hibernate.LSIDAuthority)hRefs.get(this);
		}
		
		if(hibAuthority == null){
			hibAuthority = new org.psygrid.data.model.hibernate.LSIDAuthority();
			hRefs.put(this, hibAuthority);
			toHibernate(hibAuthority, hRefs);
		}
		
		return hibAuthority;
		
	}
	
	public void toHibernate(org.psygrid.data.model.hibernate.LSIDAuthority hLA, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
		super.toHibernate(hLA, hRefs);
		hLA.setAuthorityID(this.authorityID);
	}
	
	public String getAuthorityID() {
		return authorityID;
	}

	public void setAuthorityID(String authorityID) {
		this.authorityID = authorityID;
	}

}

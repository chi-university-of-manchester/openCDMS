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

import java.util.Map;

public class LSIDNameSpaceDTO extends PersistentDTO {

	private String nameSpace;
	
	/* Used only for wsdl marshalling
	 */
	public LSIDNameSpaceDTO(){
		
	}
	
	public LSIDNameSpaceDTO(String nameSpace){
		this.nameSpace = nameSpace;
	}

	public String getNameSpace() {
		return nameSpace;
	}

	public void setNameSpace(String nameSpace) {
		this.nameSpace = nameSpace;
	}
	
	@Override
	public org.psygrid.data.model.hibernate.LSIDNameSpace toHibernate(
			Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs) {
		org.psygrid.data.model.hibernate.LSIDNameSpace hibNS = null;
		if(hRefs.containsKey(this)){
			hibNS = (org.psygrid.data.model.hibernate.LSIDNameSpace)hRefs.get(this);
		}
		
		if(hibNS == null){
			hibNS = new org.psygrid.data.model.hibernate.LSIDNameSpace();
			hRefs.put(this, hibNS);
			toHibernate(hibNS, hRefs);
		}
		
		return hibNS;
	}
	
	public void toHibernate(org.psygrid.data.model.hibernate.LSIDNameSpace hNS, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
		super.toHibernate(hNS, hRefs);
		hNS.setNameSpace(this.nameSpace);
	}
}

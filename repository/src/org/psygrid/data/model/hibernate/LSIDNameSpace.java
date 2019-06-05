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
package org.psygrid.data.model.hibernate;

import java.util.Map;


/**
 * 
 * @author williamvance
 * 
 * @hibernate.joined-subclass table="t_lsid_namespace" batch-size="100"
 * @hibernate.joined-subclass-key column="c_ns_id"
 */
public class LSIDNameSpace extends Persistent {

	
	private String nameSpace;
	
	/*
	 * Required for wsdl marshalling. Otherwise not used
	 */
	public LSIDNameSpace(){
		
	}
	
	public LSIDNameSpace(String nameSpace){
		this.nameSpace = nameSpace;
	}
	
	@Override
	public org.psygrid.data.model.dto.LSIDNameSpaceDTO toDTO(
			Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs,
			RetrieveDepth depth) {
        org.psygrid.data.model.dto.LSIDNameSpaceDTO dto = null;
        if(dtoRefs.containsKey(this)){
        	dto = (org.psygrid.data.model.dto.LSIDNameSpaceDTO)dtoRefs.get(this);
        }
        if(null == dto){
        	dto = new org.psygrid.data.model.dto.LSIDNameSpaceDTO(this.nameSpace);
        	dtoRefs.put(this, dto);
        	super.toDTO(dto, dtoRefs, depth);
        }
		
		return dto;
	}

	/**
	 * 
	 * @hibernate.property column="c_namespace"
	 */
	public String getNameSpace() {
		return nameSpace;
	}

	public void setNameSpace(String nameSpace) {
		this.nameSpace = nameSpace;
	}

}

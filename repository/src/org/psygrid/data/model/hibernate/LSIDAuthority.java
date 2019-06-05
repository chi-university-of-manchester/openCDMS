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

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author williamvance
 * 
 * @hibernate.joined-subclass table="t_lsid_authority" batch-size="100"
 * @hibernate.joined-subclass-key column="c_auth_id"
 */
public class LSIDAuthority extends Persistent {

	private String authorityID;
	
	/*
	 * required for wsdl marshalling. Otherwise not used.
	 */
	public LSIDAuthority(){
		
	}
	
	public LSIDAuthority(String authorityID){
		this.authorityID = authorityID;
	}
	
	public org.psygrid.data.model.dto.LSIDAuthorityDTO toDTO(){
	    Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
	    return (org.psygrid.data.model.dto.LSIDAuthorityDTO)toDTO(dtoRefs, RetrieveDepth.DS_COMPLETE);
	}
	
	@Override
	public org.psygrid.data.model.dto.LSIDAuthorityDTO toDTO(
			Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs,
			RetrieveDepth depth) {
			
        org.psygrid.data.model.dto.LSIDAuthorityDTO dto = null;
        if(dtoRefs.containsKey(this)){
        	dto = (org.psygrid.data.model.dto.LSIDAuthorityDTO)dtoRefs.get(this);
        }
        if(null == dto){
        	dto = new org.psygrid.data.model.dto.LSIDAuthorityDTO(this.authorityID);
        	dtoRefs.put(this, dto);
        	super.toDTO(dto, dtoRefs, depth);
        }
		
		return dto;
	}

	/**
	 * 
	 * @hibernate.property column="c_authority_id"
	 */
	public String getAuthorityID() {
		return authorityID;
	}

	public void setAuthorityID(String authorityID) {
		this.authorityID = authorityID;
	}

}

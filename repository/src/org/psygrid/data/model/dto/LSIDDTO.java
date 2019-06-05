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

public class LSIDDTO extends PersistentDTO {
	
	private LSIDAuthorityDTO lsidAuthority;
	private LSIDNameSpaceDTO lsidNameSpace;
	private String objectId;
	private String revisionId;
	private static final String urnToken = "URN";
	private static final String lsidToken = "LSID";
	private static final String idSeparator = ":";


	public LSIDDTO(){
		
	}
	
	public LSIDDTO(LSIDAuthorityDTO authorityObj, LSIDNameSpaceDTO nsObj, String objectId, String revisionId){
		this.lsidAuthority = authorityObj;
		this.lsidNameSpace = nsObj;
		this.objectId = objectId;
		this.revisionId = revisionId;
	}

	public String getAuthorityId(){
		return this.lsidAuthority.getAuthorityID();
	}
	
	
	public String getNamespaceId(){
		return this.lsidNameSpace.getNameSpace();
	}
	
	public String getRevisionId(){
		return revisionId;
	}
	
	public void setRevisionId(String revisionId){
		this.revisionId = revisionId;
	}

	public String getObjectId(){
		return objectId;
	}
	
	public void setObjectId(String objectId){
		this.objectId = objectId;
	}
	
	public String toString(){
		StringBuffer stringBuilder = new StringBuffer();
		stringBuilder.append(urnToken).append(idSeparator);
		stringBuilder.append(lsidToken).append(idSeparator);
		stringBuilder.append(this.lsidAuthority.getAuthorityID()).append(idSeparator);
		stringBuilder.append(this.lsidNameSpace.getNameSpace()).append(idSeparator);
		stringBuilder.append(objectId);
		
		if(revisionId != null){
			stringBuilder.append(idSeparator);
			stringBuilder.append(revisionId);
		}

		return stringBuilder.toString();
	}
	
	public org.psygrid.data.model.hibernate.LSID toHibernate(
			Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs) {
		org.psygrid.data.model.hibernate.LSID lsid = null;
		if ( hRefs.containsKey(this) ){
			lsid = (org.psygrid.data.model.hibernate.LSID)hRefs.get(this);
		}
		else{
			lsid = new org.psygrid.data.model.hibernate.LSID();
			hRefs.put(this, lsid);
			toHibernate(lsid, hRefs);
		}
		return lsid;
	}
	
	public void toHibernate(org.psygrid.data.model.hibernate.LSID hLsid, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
		super.toHibernate(hLsid, hRefs);
		hLsid.setLsidAuthority(this.lsidAuthority.toHibernate(hRefs));
		hLsid.setLsidNameSpace(this.lsidNameSpace.toHibernate(hRefs));
		hLsid.setObjectId(this.objectId);
		hLsid.setRevisionId(this.revisionId);
	}

	public LSIDAuthorityDTO getLsidAuthority() {
		return lsidAuthority;
	}

	public void setLsidAuthority(LSIDAuthorityDTO lsidAuthority) {
		this.lsidAuthority = lsidAuthority;
	}

	public LSIDNameSpaceDTO getLsidNameSpace() {
		return lsidNameSpace;
	}

	public void setLsidNameSpace(LSIDNameSpaceDTO lsidNameSpace) {
		this.lsidNameSpace = lsidNameSpace;
	}
	
}

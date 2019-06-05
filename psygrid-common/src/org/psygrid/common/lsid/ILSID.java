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

package org.psygrid.common.lsid;

import org.psygrid.common.IPersistent;

/**
 * Interface to be used by a class that represents a LSID.
 * 
 * Life Science Identifiers (LSIDs) are persistent, location-independent, 
 * resource identifiers for uniquely naming biologically significant 
 * resources. 
 *
 * For example, LSIDs can be used to represent a dataset, a document, an 
 * entry or an instance of any one of them.
 *  
 * The LSID produced will be of the format:
 * 		urn:lsid:authority:namespace:identifier:revision;
 *  
 * See http://www-128.ibm.com/developerworks/opensource/library/os-lsidbp/
 * for further information. 
 *  
 * @author Lucy Bridges
 */
public interface ILSID extends IPersistent {

	public static final String urnToken = "URN";
	public static final String lsidToken = "LSID";
	
	/**
	 * The token used to separate each element of the LSID
	 */
	public static final String idSeparator = ":";
	
	/**
	 * Separator used in the object identifier portion to distinquish
	 * the LSID's project code and unique id. 
	 */
	public final static String separator = "_";
	
	/**
	 * Get the namespace identification assigned to this LSID
	 * 
	 * @return namespace
	 */
	public String getNamespace();
	
	/**
	 * Set the namespace identification assigned to this LSID
	 * 
	 * @param namespace
	 */
	public void setNamespace(String namespace);
	
	/**
	 * Get the issuing authority of this LSID
	 *  
	 * @return authority
	 */
	public String getAuthority();
	
	/**
	 * Set the issuing authority of this LSID
	 * 
	 * @param authority
	 */
	public void setAuthority(String authority);
	
	/**
	 * Get the object identification for this LSID. This will
	 * always be unique for a namespace.
	 * 
	 * The identifier is normally the projectCode+separator+uniqueId
	 * e.g 'OLK_123'.
	 * 
	 * @return identifier
	 */
	public String getIdentifier();
	
	/**
	 * Get the project code for the project this LSID is part of.
	 * 
	 * @return projectCode
	 */
	public String getProjectCode();
	
	/**
	 * Set the project code of the project that this LSID is part
	 * of.
	 * 
	 * @param projectCode
	 */
	public void setProjectCode(String projectCode);
	
	/**
	 * Get the unique identifier, which forms part of the unique
	 * object identification (along with the project code) for 
	 * the LSID.
	 * 
	 * The unique id will always be unique for a given project
	 * code and namespace.
	 * 
	 * @return uniqueId
	 */
	public String getUniqueId();
	
	/**
	 * Set the unique identifier, which forms part of the unique
	 * object identification (along with the project code) for
	 * the LSID.
	 * 
	 * The unique id will always be unique for a given project 
	 * code and namespace.
	 * 
	 * @param uniqueId
	 */
	public void setUniqueId(String uniqueId);
	
	/**
	 * Get the revision, if any, of this LSID
	 * 
	 * @return revision
	 */
	public String getRevision();
	
	/**
	 * Set the revision, if any, of this LSID
	 * 
	 * @param revision
	 */
	public void setRevision(String revision);
	
	/**
	 * Method should return the LSID as a string, in a URN style format.
	 * 
	 * e.g urn:lsid:psygrid.org:org.psygrid.storage:OLK_124335
	 * 
	 * @return lsid 
	 */
	public String toString();
	
	/**
	 * Method should compare the LSID with another lsid, using the 
	 * URN string representation where applicable and ignoring the case.
	 * 
	 * @param obj
	 * @return boolean
	 */
	public boolean equals(Object obj);
	
}

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


//Created on January 18, 2007 by John Ainsworth

package org.psygrid.data.model.dto;

import java.util.Map;

/**
 * @author John Ainsworth
 *
 */
public class SiteDTO extends PersistentDTO {	
	
	/**
	 *  site Name
	 */
	private String siteName;
		
	/**
	 *  site ID
	 */
	private String siteId;
		
	/**
	 *  geographic code e.g postcode or zip address
	 */
	private String geographicCode;
		
	
	/**
	 *  The list of the consultants based at this site.
	 * 
	 * Consultants are assigned to each record that is
	 * created, as required by the UKCRN reports.
	 */
	private String[] consultants = new String[0];
	
	/**
     * Get the siteName
     * 
     * @return The siteName.
     */
    public String getSiteName(){
    		return this.siteName;
    }

    /**
     * Set the siteName
     * 
     * @param siteName The site name.
     */
    public void setSiteName(String externalName){
    		this.siteName = externalName;
    }
	
	/**
	 * @return Returns the siteId.
	 */
	public String getSiteId() {
		return this.siteId;
	}

	/**
	 * @param id The siteId to set.
	 */
	public void setSiteId(String id) {
		this.siteId = id;
	}

	/**
	 * @return Returns the geographicCode.
	 */
	public String getGeographicCode() {
		return this.geographicCode;
	}

	/**
	 * @param geographicCode The geographicCode to set.
	 */
	public void setGeographicCode(String geographicCode) {
		this.geographicCode = geographicCode;
	}
	
    
    public String[] getConsultants() {
		return consultants;
	}

	public void setConsultants(String[] consultants) {
		this.consultants = consultants;
	}

	public org.psygrid.data.model.hibernate.Site toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        org.psygrid.data.model.hibernate.Site hS = new org.psygrid.data.model.hibernate.Site();
        toHibernate(hS, hRefs);
        return hS;
    }
    
    public void toHibernate(org.psygrid.data.model.hibernate.Site hS, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hS, hRefs);
        hS.setSiteName(this.siteName);
        hS.setSiteId(this.siteId);
        hS.setGeographicCode(this.geographicCode);
        
        for ( int i=0; i<this.consultants.length; i++ ){
        	if (this.consultants[i] != null) {
        		hS.addConsultant(this.consultants[i]);
        	}
        }
    }
}

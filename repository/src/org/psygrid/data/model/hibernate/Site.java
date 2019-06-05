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


package org.psygrid.data.model.hibernate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author John Ainsworth
 *
 * @hibernate.joined-subclass table="t_sites"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class Site extends Persistent {	
	
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
	 * parent group of the site
	 */
	private Group parent;
	
	/**
	 * A list of the consultants based at this site.
	 * 
	 * Consultants are assigned to each record that is 
	 * created, as required by the UKCRN reports.
	 */
	private List<String> consultants = new ArrayList<String>();
	
	/**
	 * Default no-arg constructor, as required by the Hibernate framework
	 * for all persistable classes.
	 * 
	 */
	public Site(){};
	
    /**
     * Constructor that accepts the name, id and geographic code
     *  
     * @param siteName The name of the site.
     * @param siteId The identity of the site.
     * @param geographicCode The geogrpahic code used to identify the site.
     */
    public Site(String siteName,String siteId,
    		String geoCode){
		this.setSiteName(siteName);
		this.setSiteId(siteId);
		this.setGeographicCode(geoCode);
    }
    
    /**
     * Constructor that accepts the name, id, geographic code and parent
     *  
     * @param siteName The name of the site.
     * @param siteId The identity of the site.
     * @param geographicCode The geogrpahic code used to identify the site.
     * @param group The parent group of the site
     */
    public Site(String siteName,String siteId,
    		String geoCode, Group group){
		this.setSiteName(siteName);
		this.setSiteId(siteId);
		this.setGeographicCode(geoCode);
		this.setParent(group);
    }
	
	/**
     * Get the siteName
     * 
     * @return The siteName.
     * @hibernate.property column = "c_site_name"
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
	 * Get the siteId
	 * @return Returns the siteId.
	 * @hibernate.property column = "c_site_id"
	 */
	public String getSiteId() {
		return this.siteId;
	}

	/**
	 * Set the siteId
	 * 
	 * @param id The siteId to set.
	 */
	public void setSiteId(String id) {
		this.siteId = id;
	}

	/**
	 * Get the geographicCode
	 * 
	 * @return Returns the geographicCode.
	 * @hibernate.property column = "c_geographic_code"
	 */
	public String getGeographicCode() {
		return this.geographicCode;
	}

	/**
	 * Set the geographicCode
	 * 
	 * @param geographicCode The geographicCode to set.
	 */
	public void setGeographicCode(String geographicCode) {
		this.geographicCode = geographicCode;
	}
	
    /**
     * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.Group"
     *                        column="c_group_id"
     *                        not-null="true"
     *                        insert="false"
     *                        update="false"
     */
    public Group getParent() {
        return parent;
    }

    public void setParent(Group group) {
        this.parent = group;
    }
    
    /**
     * Get a list of the consultants based at this site.
	 * 
	 * Consultants are assigned to each record that is
	 * created, as required by the UKCRN reports.
	 * 
     * @hibernate.list cascade="all" table="t_site_consultants" batch-size="100"
     * @hibernate.key column="c_site_id" not-null="true"
     * @hibernate.element column="c_consultant" type="string"
     * @hibernate.list-index column="c_index"
     * 
     * @return consultants
     */
    public List<String> getConsultants() {
		return consultants;
	}

	/**
	 * Add a consultant to this site.
	 * 
	 * Consultants are assigned to each record that is 
	 * created, as required by the UKCRN reports.
	 * 
	 * @param consultant
	 */
    public void addConsultant(String consultant) {
    	this.consultants.add(consultant);
    }
    
    /**
     * Set the list of the consultants based at this site.
	 * 
	 * Consultants are assigned to each record that is
	 * created, as required by the UKCRN reports.
	 * 
     * @param consultants
     */
	public void setConsultants(List<String> consultants) {
		this.consultants = consultants;
	}

	public org.psygrid.data.model.dto.SiteDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        //check for an already existing instance of a dto object for this 
        //transformer in the map of references
        org.psygrid.data.model.dto.SiteDTO dtoS = new org.psygrid.data.model.dto.SiteDTO();
        toDTO(dtoS, dtoRefs, depth);
        return dtoS;
    }
    
    public void toDTO(org.psygrid.data.model.dto.SiteDTO dtoS, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        super.toDTO(dtoS, dtoRefs, depth);
        dtoS.setSiteName(this.siteName);
        dtoS.setGeographicCode(this.geographicCode);
        dtoS.setSiteId(this.siteId);
         
        dtoS.setConsultants(new String[this.consultants.size()]);
        if (this.consultants != null) {
        	for (int i=0; i < this.consultants.size(); i++) {
        		if (this.consultants.get(i) != null) {
        			dtoS.getConsultants()[i] = this.consultants.get(i);
        		}
        	}
        }
    }
}

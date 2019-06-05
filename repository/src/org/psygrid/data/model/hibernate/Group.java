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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;



/**
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_groups"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class Group extends Persistent {

	/**
	 * The group code
	 */
    private String name;
    
    /**
     * A user-friendly name for this group
     */
    private String longName;
    
    private int maxSuffix;
    
    private DataSet dataSet;
    
    /**
     * Collection of sites associated with the group.
     */
    private List<Site> sites = new ArrayList<Site>();
    
    /**
     * Collection of group codes in the secondary dataset that records
     * in this group may be linked to.
     * <p>
     * Only applicable if the dataset that the group belongs to is the
     * primary dataset in a dual data entry relationship.
     */
    private List<String> theSecondaryGroups = new ArrayList<String>();
    
    /**
     * Retrieve the group code
     * 
     * @return code
     * @hibernate.property
     * @hibernate.column name="c_name" not-null="true" unique-key="unique-group-codes"
     */
    public String getName() {
        return name;
    }

    public void setName(String name) throws ModelException {
    	
    	//Verify that the name is not an integer value between 0-1000 inclusive
    	int centreCodeNumeric = -999;
		boolean centreCodeIsAnInteger = true;
		try{
			centreCodeNumeric = Integer.parseInt(name);
		}catch (NumberFormatException e){
			centreCodeIsAnInteger = false;
		}
		
		if(centreCodeIsAnInteger == true && centreCodeNumeric >= 0 && centreCodeNumeric <= 1000 && Integer.toString(centreCodeNumeric).equals(name)) {
			throw new ModelException("Centre code cannot be an integer value between 0 and 1000 inclusive");
		}
    	
        this.name = name;
    }

    /**
     * Retrieve the user-friendly, long name for this group.
     * 
     * @return name
     * @hibernate.property
     * @hibernate.column name="c_long_name" not-null="true" unique-key="unique-group-names"
     */
    public String getLongName() {
		return longName;
	}

	public void setLongName(String longName) {
		this.longName = longName;
	}

	/**
     * @hibernate.property column="c_max_suffix" 
     * 						update="false"
     */
    public int getMaxSuffix() {
        return maxSuffix;
    }

    public void setMaxSuffix(int maxIdentifier) {
        this.maxSuffix = maxIdentifier;
    }
    
    /**
     * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.DataSet"
     *                        insert="false"
     *                        update="false"
     * @hibernate.column name="c_dataset_id" not-null="true" unique-key="unique-group-codes,unique-group-names"
     */
    public DataSet getDataSet() {
        return dataSet;
    }

    public void setDataSet(DataSet dataSet) {
        this.dataSet = dataSet;
    }
    
    /**
     * @hibernate.list cascade="all-delete-orphan" batch-size="100"
     * @hibernate.one-to-many class="org.psygrid.data.model.hibernate.Site"
     * @hibernate.key column="c_group_id" not-null="true"
     * @hibernate.list-index column="c_index"
     */
    public List<Site> getSites() {
        return sites;
    }

    public void setSites(List<Site> sl) {
        this.sites = sl;
    }
    
    public int numSites() {
		return sites.size();
	}
    
    public Site getSite(int i) {
		return sites.get(i);
	}
    
    public void addSite(Site s){
    		sites.add((Site)s);
    }

    /**
     * Get the collection of group codes in the secondary dataset that records
     * in this group may be linked to.
     * <p>
     * Only applicable if the dataset that the group belongs to is the
     * primary dataset in a dual data entry relationship.
     * 
     * @return The collection of secondary group codes.
     * 
     * @hibernate.list table="t_grp_sec_grps"
     *                 cascade="all" batch-size="100"
     * @hibernate.key column="c_grp_id"
     * @hibernate.list-index column="c_index"
     * @hibernate.element type="string"
     *                    column="c_sec_grp"
     *                    not-null="true"
     */
    public List<String> getTheSecondaryGroups() {
		return theSecondaryGroups;
	}

    /**
     * Set the collection of group codes in the secondary dataset that records
     * in this group may be linked to.
     * <p>
     * Only applicable if the dataset that the group belongs to is the
     * primary dataset in a dual data entry relationship.
     * 
     * @param secondaryGroups The collection of secondary group codes.
     */
	public void setTheSecondaryGroups(List<String> theSecondaryGroups) {
		this.theSecondaryGroups = theSecondaryGroups;
	}
	
	public int numSecondaryGroups() {
		return theSecondaryGroups.size();
	}
	
	public String getSecondaryGroup(int index) throws ModelException {
		try{
			return theSecondaryGroups.get(index);
		}
		catch(IndexOutOfBoundsException ex){
			throw new ModelException("No secondary group exists for index "+index);
		}
	}

	public void addSecondaryGroup(String secondaryGroup){
		theSecondaryGroups.add(secondaryGroup);
	}
	
	public List<String> getSecondaryGroups(){
		return Collections.unmodifiableList(this.theSecondaryGroups);
	}
	
	public Group(){}
    
    public Group(String name){
        this.name = name;
    }
    
    
    public org.psygrid.data.model.dto.GroupDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        //check for an already existing instance of a dto object for this 
        //transformer in the map of references
        org.psygrid.data.model.dto.GroupDTO dtoG = new org.psygrid.data.model.dto.GroupDTO();
        toDTO(dtoG, dtoRefs, depth);
        return dtoG;
    }
    
    public void toDTO(org.psygrid.data.model.dto.GroupDTO dtoG, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        super.toDTO(dtoG, dtoRefs, depth);
        dtoG.setName(this.name);
        dtoG.setLongName(longName);
        dtoG.setMaxSuffix(this.maxSuffix);
        org.psygrid.data.model.dto.SiteDTO[] dtoSites = new org.psygrid.data.model.dto.SiteDTO[this.sites.size()];
        for (int i=0; i<this.sites.size(); i++){
            Site s = sites.get(i);
            dtoSites[i] = s.toDTO(dtoRefs, depth);
        }        
        dtoG.setSites(dtoSites);
        
        String[] dtoSecGrps = new String[this.theSecondaryGroups.size()];
        for ( int i=0, c=theSecondaryGroups.size(); i<c; i++ ){
        	dtoSecGrps[i] = theSecondaryGroups.get(i);
        }
        dtoG.setTheSecondaryGroups(dtoSecGrps);
    }
    
}

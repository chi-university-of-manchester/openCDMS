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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.psygrid.common.identifier.IdentifierHelper;
import org.psygrid.common.identifier.InvalidIdentifierException;

/**
 * Class to represent the unique identifier of a record within
 * its dataset.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_identifiers"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class Identifier extends Persistent implements Comparable<Identifier> {

    /**
     * The overall identifier string.
     * <p>
     * The overall identifier string is constructed as
     * &lt;project prefix&gt;-&lt;group prefix&gt;-&lt;suffix&gt;
     */
    private String identifier;
    
    /**
     * The project prefix of the identifier. Intended to define
     * the project (dataset) the the record is associated with.
     */
    private String projectPrefix;

    /**
     * The group prefix of the identifier. Intended to define
     * the group within the project that the record is associated with.
     * <p>
     * Typically groups will be defined for geographical divisions
     * of the project, or similar.
     */
    private String groupPrefix;

    /**
     * The number in the suffix of the identifier.
     * <p>
     * In the identifier itself the suffix will be padded with zeroes
     * so that the number of characters in the suffix is equal to that
     * specified in the dataset that generated the identifier.
     */
    private int suffix;
    
    /**
     * The user to whom the identifier was allocated.
     */
    private String user;
    
    
    /**
     * The date when the identifier was created.
     */
    private Date created;
    
    public Identifier(){}
    
    public Identifier(String user){
        this.user = user;
        this.created = new Date();
    }
    
    /**
     * Get the overall identifier string.
     *
     * @return The identifier string.
     * @hibernate.property column="c_identifier"
     *                     unique="true"
     *                     not-null="true"
     */
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    
    /**
     * Get the group prefix of the identifier. Intended to define
     * the group within the project that the record is associated with.
     * <p>
     * Typically groups will be defined for geographical divisions
     * of the project, or similar.
     *
     * @return The group prefix.
     * @hibernate.property column="c_group_prefix"
     *                     not-null="true"
     *                     index="group_prefix_index"
     */
    public String getGroupPrefix() {
        return groupPrefix;
    }

    /**
     * Set the group prefix of the identifier. Intended to define
     * the group within the project that the record is associated with.
     * <p>
     * Typically groups will be defined for geographical divisions
     * of the project, or similar.
     * 
     * @param groupPrefix The group prefix.
     */
    public void setGroupPrefix(String groupPrefix) {
        this.groupPrefix = groupPrefix;
    }

    /**
     * Get the project prefix of the identifier. Intended to define
     * the project (dataset) the the record is associated with.
     *
     * @return The project prefix.
     * @hibernate.property column="c_proj_prefix"
     *                     not-null="true"
     *                     index="project_prefix_index"
     */
    public String getProjectPrefix() {
        return projectPrefix;
    }

    /**
     * Set the project prefix of the identifier. Intended to define
     * the project (dataset) the the record is associated with.
     * 
     * @param projectPrefix The project prefix.
     */
    public void setProjectPrefix(String projectPrefix) {
        this.projectPrefix = projectPrefix;
    }

    /**
     * Get the number in the suffix of the identifier.
     *
     * @return The number in the suffix.
     * @hibernate.property column="c_suffix"
     *                     not-null="true"
     *                     index="suffix_index"
     */
    public int getSuffix() {
        return suffix;
    }

    /**
     * Set the number in the suffix of the identifier.
     * 
     * @param suffix The number in the suffix.
     */
    public void setSuffix(int suffix) {
        this.suffix = suffix;
    }

    /**
     * Get the date when the identifier was created.
     * 
     * @return The date created.
     * 
     * @hibernate.property column="c_created"
     */
    public Date getCreated() {
        return created;
    }

    /**
     * Set the date when the identifier was created.
     * 
     * @param created The date created.
     */
    public void setCreated(Date created) {
        this.created = created;
    }

    /**
     * Get the user who the identifier was allocated to.
     * 
     * @return The user.
     * 
     * @hibernate.property column="c_user"
     */
    public String getUser() {
        return user;
    }

    /**
     * Set the user who the identifier was allocated to.
     * 
     * @param user The user.
     */
    public void setUser(String user) {
        this.user = user;
    }

    public org.psygrid.data.model.dto.IdentifierDTO toDTO(){
        Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
        return toDTO(dtoRefs, RetrieveDepth.DS_COMPLETE);
    }
    
    public org.psygrid.data.model.dto.IdentifierDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        //check for an already existing instance of a dto object for this 
        //unit in the map of references
        org.psygrid.data.model.dto.IdentifierDTO dtoI = new org.psygrid.data.model.dto.IdentifierDTO();
        toDTO(dtoI, dtoRefs, depth);
        return dtoI;
    }
    
    public void toDTO(org.psygrid.data.model.dto.IdentifierDTO dtoI, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        super.toDTO(dtoI, dtoRefs, depth);
        dtoI.setIdentifier(this.identifier);
        dtoI.setSuffix(this.suffix);
        dtoI.setGroupPrefix(this.groupPrefix);
        dtoI.setProjectPrefix(this.projectPrefix);
        dtoI.setCreated(this.created);
        dtoI.setUser(this.user);
    }
    
    /**
     * Helper method to initialize all properties of the identifier in one go, including
     * constructing the overall identifier string from its components.
     * 
     * @param projectPrefix The project prefix.
     * @param groupPrefix The group prefix.
     * @param suffix The suffix.
     * @param suffixSize The number of characters in the suffix when the overall identifier
     * is constructed.
     */
    public void initialize(String projectPrefix, String groupPrefix, int suffix, int suffixSize){
        this.projectPrefix = projectPrefix;
        this.groupPrefix = groupPrefix;
        this.suffix = suffix;
        
        //form the overall identifier
        StringBuilder builder = new StringBuilder();
        builder.append(this.projectPrefix).append(IdentifierHelper.PROJ_GRP_SEPARATOR).append(this.groupPrefix).append(IdentifierHelper.GRP_SUFF_SEPARATOR);
        //pad suffix with leading zeros
        for ( int j=0; j<suffixSize-Integer.toString(suffix).length(); j++){
            builder.append("0");
        }
        builder.append(Integer.toString(suffix));
        
        this.identifier = builder.toString();
    }
    
    /**
     * Helper method to initialize all properties of the identifier
     * from the entered overall identifier, which is parsed to find
     * the individual components of the identifier.
     * 
     * @param overallIdentifier The overall identifier.
     * @throws InvalidInputException if the overall identifier cannot be correctly
     * parsed into its individual components.
     */
    public void initialize(String overallIdentifier) throws InvalidIdentifierException {

    	//check the identifier
    	IdentifierHelper.checkIdentifier(overallIdentifier);
    	
        int projGrpPos = overallIdentifier.indexOf(IdentifierHelper.PROJ_GRP_SEPARATOR);
        int grpSuffPos = overallIdentifier.lastIndexOf(IdentifierHelper.GRP_SUFF_SEPARATOR);
    	String projectPrefix = overallIdentifier.substring(0, projGrpPos);
        String groupPrefix = overallIdentifier.substring(projGrpPos+1, grpSuffPos);
        
        try{
            int suffix = Integer.parseInt(overallIdentifier.substring(grpSuffPos+1));
            this.identifier = overallIdentifier;
            this.projectPrefix = projectPrefix;
            this.groupPrefix = groupPrefix;
            this.suffix = suffix;
        }
        catch(NumberFormatException ex){
            throw new InvalidIdentifierException("The supplied identifier '"+overallIdentifier+"' does not contain a valid number in its prefix. The format should be <project code>"+IdentifierHelper.PROJ_GRP_SEPARATOR+"<group code>"+IdentifierHelper.GRP_SUFF_SEPARATOR+"<number>");
        }
    }
    
    /**
     * Compares this object with the specified object for order.
     *
     * Returns a negative integer, zero, or a positive integer
     * as this object is less than, equal to, or greater than the specified object.
     *
     * @param i
     * @return int
     */
    public int compareTo(Identifier i) {
    	/*Compares this object with the specified object for order. 
    	 * 
    	 * Returns a negative integer, zero, or a positive integer 
    	 * as this object is less than, equal to, or greater than the specified object.
    	 *
    	 */
    	if (this == i ) {
    		return 0;
    	}
    	if (this.getProjectPrefix().compareTo(i.getProjectPrefix()) == 0) {
    		if (this.getGroupPrefix().compareTo(i.getGroupPrefix()) == 0) {
    			if(this.getSuffix() == i.getSuffix()) {
    				return 0;
    			}
    			if(this.getSuffix() > i.getSuffix()) {
    				return 1;
    			}
    			else {
    				return -1;
    			}
    		}
    		else {
    			return this.getGroupPrefix().compareTo(i.getGroupPrefix());
    		}
    	}
    	else {
    		return this.getProjectPrefix().compareTo(i.getProjectPrefix());
    	}
    
    }
}

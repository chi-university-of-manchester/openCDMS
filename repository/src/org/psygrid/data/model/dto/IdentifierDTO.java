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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to represent the unique identifier of a record within
 * its dataset.
 * 
 * @author Rob Harper
 *
 */
public class IdentifierDTO extends PersistentDTO {

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
    
    public IdentifierDTO() {}
    
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public int getSuffix() {
        return suffix;
    }

    public void setSuffix(int suffix) {
        this.suffix = suffix;
    }

    public String getGroupPrefix() {
        return groupPrefix;
    }

    public void setGroupPrefix(String groupPrefix) {
        this.groupPrefix = groupPrefix;
    }

    public String getProjectPrefix() {
        return projectPrefix;
    }

    public void setProjectPrefix(String projectPrefix) {
        this.projectPrefix = projectPrefix;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public org.psygrid.data.model.hibernate.Identifier toHibernate(){
        Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
        return toHibernate(hRefs);
    }
    
    public org.psygrid.data.model.hibernate.Identifier toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        org.psygrid.data.model.hibernate.Identifier hI = new org.psygrid.data.model.hibernate.Identifier();
        toHibernate(hI, hRefs);
        return hI;
    }
    
    public void toHibernate(org.psygrid.data.model.hibernate.Identifier hI, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hI, hRefs);
        hI.setIdentifier(this.identifier);
        hI.setSuffix(this.suffix);
        hI.setGroupPrefix(this.groupPrefix);
        hI.setProjectPrefix(this.projectPrefix);
        hI.setCreated(this.created);
        hI.setUser(this.user);
    }

}

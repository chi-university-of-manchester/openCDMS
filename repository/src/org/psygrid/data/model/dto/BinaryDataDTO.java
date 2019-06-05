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


/**
 * Class to hold the actual binary data for a BinaryObject
 * class.
 * 
 * @author Rob Harper
 */
public class BinaryDataDTO extends PersistentDTO {

    /**
     * The binary data
     */
    private byte[] data;
    
    /**
     * Default no-arg constructor, as required by the Hibernate framwework
     * for all persistable classes.
     */
    public BinaryDataDTO(){};
    
    /**
     * Get the binary data.
     * 
     * @return The binary data.
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Set the binary data.
     * 
     * @param data The binary data.
     */
    public void setData(byte[] data) {
        this.data = data;
    }
    
    public org.psygrid.data.model.hibernate.BinaryData toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        org.psygrid.data.model.hibernate.BinaryData hBD = new org.psygrid.data.model.hibernate.BinaryData();
        toHibernate(hBD, hRefs);
        return hBD;
    }
    
    public void toHibernate(org.psygrid.data.model.hibernate.BinaryData hBD, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hBD, hRefs);
        hBD.setData(this.data);
    }
}

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
 * Class to represent binary objects stored in the data repository.
 * <p>
 * This class is designed to store information about the binary
 * object, rather than the binary data itself.
 * 
 * @author Rob Harper
 */
public class BinaryObjectDTO extends PersistentDTO {

    /**
     * The mime-type of the binary object
     */
    private String mimeType;
    
    /**
     * The original filename of the binary object
     */
    private String fileName;
    
    /**
     * Description of the binary object
     */
    private String description;
    
    /**
     * The actual binary data of the binary object
     */
    private BinaryDataDTO data;
    
    public BinaryObjectDTO(){};

    /**
     * Get the actual binary data of the binary object
     * 
     * @return The binary data
     */
    public BinaryDataDTO getData() {
        return data;
    }

    /**
     * Set the actual binary data of the binary object.
     * 
     * @param data The binary data.
     */
    public void setData(BinaryDataDTO data) {
        this.data = data;
    }

    /**
     * Get the description of the binary object
     * 
     * @return The description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Set the description of the binary object
     * 
     * @param description The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the original filename of the binary object
     * 
     * @return The filename
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Set the original filename of the binary object
     * 
     * @param fileName The filename
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Get the mime-type of the binary object
     * 
     * @return The mime-type
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Set the mime-type of the binary object
     * 
     * @param mimeType The mime-type
     */
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public org.psygrid.data.model.hibernate.BinaryObject toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        org.psygrid.data.model.hibernate.BinaryObject hBO = new org.psygrid.data.model.hibernate.BinaryObject();
        toHibernate(hBO, hRefs);
        return hBO;
    }
    
    public void toHibernate(org.psygrid.data.model.hibernate.BinaryObject hBO, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hBO, hRefs);
        hBO.setDescription(this.description);
        hBO.setFileName(this.fileName);
        hBO.setMimeType(this.mimeType);
        if ( null != this.data ){
            hBO.setData(this.data.toHibernate(hRefs));
        }
    }
    
}

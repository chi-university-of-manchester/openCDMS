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

import java.util.Map;

/**
 * Class to represent binary objects stored in the data repository.
 * <p>
 * This class is designed to store information about the binary
 * object, rather than the binary data itself.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_binary_objects"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class BinaryObject extends Persistent {

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
     * <p>
     * Marked as transient so that XStream will not serialize
     * this property.
     */
    private transient BinaryData data;
    
    public BinaryObject(){};
    
    public BinaryObject(byte[] data){
        this.data = new BinaryData(data);
    }

    /**
     * Get the actual binary data of the binary object
     * 
     * @return The binary data
     * 
     * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.BinaryData"
     *                        column="c_data_id"
     *                        not-null="false"
     *                        unique="true"
     *                        cascade="all"
     */
    public BinaryData getData() {
        return data;
    }

    /**
     * Set the actual binary data of the binary object.
     * 
     * @param data The binary data.
     */
    public void setData(BinaryData data) {
        this.data = data;
    }

    /**
     * Get the description of the binary object
     * 
     * @return The description
     * 
     * @hibernate.property column="c_description"
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
     * 
     * @hibernate.property column="c_file_name"
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
     * 
     * @hibernate.property column="c_mime_type"
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
    
    public org.psygrid.data.model.dto.BinaryObjectDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        org.psygrid.data.model.dto.BinaryObjectDTO dtoBO = new org.psygrid.data.model.dto.BinaryObjectDTO();
        toDTO(dtoBO, dtoRefs, depth);
        return dtoBO;
    }
    
    public void toDTO(org.psygrid.data.model.dto.BinaryObjectDTO dtoBO, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        super.toDTO(dtoBO, dtoRefs, depth);
        dtoBO.setDescription(this.description);
        dtoBO.setFileName(this.fileName);
        dtoBO.setMimeType(this.mimeType);
        if ( RetrieveDepth.DS_COMPLETE == depth ||
             RetrieveDepth.RS_COMPLETE == depth ){
            if ( null != this.data ){
                dtoBO.setData(this.data.toDTO(dtoRefs, depth));
            }
        }
    }
}

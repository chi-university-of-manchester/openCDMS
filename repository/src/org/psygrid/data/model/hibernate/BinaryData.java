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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Map;

import org.hibernate.Hibernate;

/**
 * Class to hold the actual binary data for a BinaryObject
 * class.
 * <p>
 * The BLOB handling code was obtained from
 * http://hansonchar.blogspot.com/2005/06/oracle-blob-mapped-to-byte-in.html
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_binary_data"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class BinaryData extends Persistent {

    /**
     * The binary data
     */
    private byte[] data;
    
    /**
     * Default no-arg constructor, as required by the Hibernate framwework
     * for all persistable classes.
     */
    public BinaryData(){};
    
    /**
     * Constructor that accepts the data of the binary data object.
     * 
     * @param data The data.
     */
    public BinaryData(byte[] data){
        this.data = data;
    }

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
    
    /**
     * Used internally by Hibernate to get the contents of the byte array
     * containing the binary data as a Blob.
     * 
     * @return The binary data as a Blob.
     * 
     * @hibernate.property column="c_data"
     *                     length="2000000"
     */
    protected Blob getBlobData() {
        return Hibernate.createBlob(this.data);
    }
    
    /**
     * Used internally by Hibernate to set the contents of the byte array
     * containing the binary data from a Blob.
     * 
     * @param blobData The binary data in Blob format.
     */
    protected void setBlobData(Blob blobData){
        this.data = toByteArray(blobData);
    }
    
    
    public org.psygrid.data.model.dto.BinaryDataDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        org.psygrid.data.model.dto.BinaryDataDTO dtoBD = new org.psygrid.data.model.dto.BinaryDataDTO();
        toDTO(dtoBD, dtoRefs, depth);
        return dtoBD;
    }
    
    public void toDTO(org.psygrid.data.model.dto.BinaryDataDTO dtoBD, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        super.toDTO(dtoBD, dtoRefs, depth);
        dtoBD.setData(this.data);
    }
    
    /**
     * Method to obtain the data contained by a Blob as a byte array.
     * 
     * @param fromBlob The Blob to obtain the data from.
     * @return Byte array containing the Blob's data.
     */
    private static byte[] toByteArray(Blob fromBlob) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            byte[] buf = new byte[4000];
            InputStream is = fromBlob.getBinaryStream();
            try {
                while(true) {
                    int dataSize = is.read(buf);
                    if (dataSize == -1){
                        break;
                    }
                    baos.write(buf, 0, dataSize);
                }
            } 
            finally {
                if (is != null) {
                    try {
                        is.close();
                    } 
                    catch (IOException ex) {
                        //do nothing
                    }
                }
            }
            return baos.toByteArray();
        } 
        catch (SQLException e) {
            throw new RuntimeException(e);
        } 
        catch (IOException e) {
            throw new RuntimeException(e);
        } 
        finally {
            if (baos != null) {
                try {
                    baos.close();
                } 
                catch (IOException ex) {
                    //do nothing
                }
            }
        }
    }
    
}

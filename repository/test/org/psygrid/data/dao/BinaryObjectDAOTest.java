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

package org.psygrid.data.dao;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.psygrid.data.model.hibernate.BinaryData;
import org.psygrid.data.model.hibernate.BinaryObject;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Factory;
import org.psygrid.data.repository.dao.DAOException;
import org.psygrid.data.repository.dao.RepositoryDAO;

/**
 * Unit tests for implementations of the BinaryObjectDAO
 * interface.
 * 
 * @author Rob Harper
 *
 */
public class BinaryObjectDAOTest extends DAOTest {

    private RepositoryDAO dao = null;
    
    private Factory factory = null;
    
    protected void setUp() throws Exception {
        super.setUp();
        dao = (RepositoryDAO)ctx.getBean("repositoryDAOService");
        factory = (Factory) ctx.getBean("factory");
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        dao = null;
        factory = null;
    }
    
    public void testGetBinaryData_Success(){
        try{
            //Create a dataset
            String name = "testGetBinaryData_Success - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            ds.setProjectCode(guid.toString());
            byte[] data = read2Array("test/test-info-doc.pdf");
            BinaryObject infoObject1 = factory.createBinaryObject(data);
            infoObject1.setFileName("test-info-doc.pdf");
            infoObject1.setMimeType("application/pdf");
            ds.setInfoSheet(infoObject1);
            
            //save the dataset
            Long dsId = dao.saveDataSet(ds.toDTO());
            ds = dao.getDataSet(dsId).toHibernate();
            
            //get the binary data
            BinaryData bd = dao.getBinaryData(ds.getInfoSheet().getId());
            assertNotNull("Retrieved a null binary data object", bd);
            assertNotNull("Binary data object has null data",bd.getData());
            assertEquals("Binary data object's data is the incorrect length",data.length,bd.getData().length);
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex.toString());
        }
    }
    
    public void testGetBinaryData_InvalidId(){
        try{
            //Create a dataset
            String name = "testGetBinaryData_InvalidId - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            ds.setProjectCode(guid.toString());
            byte[] data = read2Array("test/test-info-doc.pdf");
            BinaryObject infoObject1 = factory.createBinaryObject(data);
            infoObject1.setFileName("test-info-doc.pdf");
            infoObject1.setMimeType("application/pdf");
            ds.setInfoSheet(infoObject1);
            
            //save the dataset
            Long dsId = dao.saveDataSet(ds.toDTO());
            
            try{
                dao.getBinaryData(dsId);
                fail("Exception should have been thrown when trying to retrieve binary data using an invalid id");
            }
            catch(DAOException ex){
                //do nothing
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex.toString());
        }
    }
    
    /**
     * Read a file from local disk into a byte array.
     * 
     * @param fileName The path of file to read.
     * @return File as a byte array
     * @throws IOException
     * @see http://forum.java.sun.com/thread.jspa?threadID=457266&messageID=2090543
     */
    private byte[] read2Array(String fileName) throws IOException {
        InputStream is = new FileInputStream(fileName);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        for(int len=-1;(len=is.read(buf))!=-1;)
            baos.write(buf,0,len);
        baos.flush();
        is.close();
        baos.close();
        return baos.toByteArray();
    }
    
}

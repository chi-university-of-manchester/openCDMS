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

import org.psygrid.data.model.hibernate.Factory;
import org.psygrid.data.model.hibernate.StandardCode;
import org.psygrid.data.repository.dao.DAOException;
import org.psygrid.data.repository.dao.RepositoryDAO;

public class StandardCodeDAOTest extends DAOTest {

    private RepositoryDAO dao = null;
    private Factory factory = null;
    
    protected void setUp() throws Exception {
        super.setUp();
        dao = (RepositoryDAO)ctx.getBean("repositoryDAOService");
        factory = (Factory)ctx.getBean("factory");
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        dao = null;
        factory = null;
    }
    
    public void testSaveStandardCode(){
        try{
            StandardCode sc = generateUniqueCode();
            String desc = sc.getDescription();
            int code = sc.getCode();
            
            Long scId = dao.saveStandardCode(sc.toDTO());
            assertNotNull("ID of the saved standard code is null", scId);
            
            sc = dao.getStandardCode(scId).toHibernate();
            assertEquals("Standard code has the wrong description", desc, sc.getDescription());
            assertEquals("Standard code has the wrong code", code, sc.getCode());
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex.toString());
        }
    }
    
    
    public void testGetStandardCode_Success(){
        try{
            StandardCode sc = generateUniqueCode();
            String desc = sc.getDescription();
            int code = sc.getCode();
            
            Long scId = dao.saveStandardCode(sc.toDTO());
            sc = dao.getStandardCode(scId).toHibernate();
            assertEquals("Standard code has the wrong description", desc, sc.getDescription());
            assertEquals("Standard code has the wrong code", code, sc.getCode());
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex.toString());
        }
    }
    
    public void testGetStandardCode_InvalidId(){
        try{
            try{
                dao.getStandardCode(-1L);
                fail("Exception should have been thrown when trying to get a standard code using an invalid id");
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
    
    public void testGetStandardCodes(){
        try{
            org.psygrid.data.model.dto.StandardCodeDTO[] codes = dao.getStandardCodes();
            int baseSize = codes.length;
            
            StandardCode sc1 = generateUniqueCode();
            dao.saveStandardCode(sc1.toDTO());
            StandardCode sc2 = generateUniqueCode();
            dao.saveStandardCode(sc2.toDTO());
            StandardCode sc3 = generateUniqueCode();
            dao.saveStandardCode(sc3.toDTO());
            
            codes = dao.getStandardCodes();
            
            assertEquals("Length of standard codes array has not increased by three",3,codes.length-baseSize);
            
            for ( int i=0; i<codes.length; i++){
                StandardCode code = codes[i].toHibernate();
                assertNotNull("Standard code description is null", code.getDescription());
                assertTrue("Standard code code is not greater than zero", code.getCode()>0);
            }
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex.toString());
        }
    }
    
    private StandardCode generateUniqueCode() throws Exception{
        org.psygrid.data.model.dto.StandardCodeDTO[] codes = dao.getStandardCodes();
        int maxCode = 0;
        for ( int i=0; i<codes.length; i++){
            if ( codes[i].getCode() > maxCode ){
                maxCode = codes[i].getCode();
            }
        }
        maxCode++;
        return factory.createStandardCode("CODE "+Integer.toString(maxCode), maxCode);
    }
    
}

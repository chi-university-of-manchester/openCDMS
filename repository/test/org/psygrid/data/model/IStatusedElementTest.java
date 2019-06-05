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

package org.psygrid.data.model;

import org.psygrid.data.model.hibernate.ModelException;
import org.psygrid.data.model.hibernate.Status;
import org.psygrid.data.model.hibernate.StatusedElement;
import org.testng.annotations.Test;
import org.testng.Assert;
import org.testng.AssertJUnit;


public class IStatusedElementTest extends ModelTest {
        
    @Test()
	public void testNumStatus(){
            StatusedElement doc = factory.createDocument("Doc 1");
            Status status1 = factory.createStatus("Status 1", 1);
            Status status2 = factory.createStatus("Status 2", 2);
            Status status3 = factory.createStatus("Status 3", 3);
            doc.addStatus(status1);
            doc.addStatus(status2);
            doc.addStatus(status3);
            
            AssertJUnit.assertEquals("Element has the wrong number of statuses",3,doc.numStatus());
    }
    
    @Test()
	public void testAddStatus_Success(){
            StatusedElement doc = factory.createDocument("Doc 1");
            String sName = "Status 1";
            int sCode = 1;
            Status status1 = factory.createStatus(sName, sCode);
            doc.addStatus(status1);
            
            AssertJUnit.assertEquals("Element has the wrong number of statuses",1,doc.numStatus());
            AssertJUnit.assertEquals("Status at index 0 has the wrong name",sName,doc.getStatus(0).getShortName());
            AssertJUnit.assertEquals("Status at index 0 has the wrong code",sCode,doc.getStatus(0).getCode());
    }
    
    @Test()
	public void testAddStatus_Null(){
            StatusedElement doc = factory.createDocument("Doc 1");
            try{
                doc.addStatus(null);
                Assert.fail("Exception should have been thrown when trying to add a null status");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testGetStatus_Success(){
            StatusedElement doc = factory.createDocument("Doc 1");
            Status status1 = factory.createStatus("Status 1", 1);
            Status status2 = factory.createStatus("Status 2", 2);
            String sName3 = "Status 3";
            int sCode3 = 3;
            Status status3 = factory.createStatus(sName3, sCode3);
            doc.addStatus(status1);
            doc.addStatus(status2);
            doc.addStatus(status3);
            
            Status s = doc.getStatus(2);
            AssertJUnit.assertNotNull("Status at index 2 is null",s);
            AssertJUnit.assertEquals("Status at index 2 has the wrong name", sName3,s.getShortName());
            AssertJUnit.assertEquals("Status at index 2 has the wrong code", sCode3, s.getCode());
    }
    
    @Test()
	public void testGetStatus_InvalidId(){
            StatusedElement doc = factory.createDocument("Doc 1");
            Status status1 = factory.createStatus("Status 1", 1);
            Status status2 = factory.createStatus("Status 2", 2);
            Status status3 = factory.createStatus("Status 3", 3);
            doc.addStatus(status1);
            doc.addStatus(status2);
            doc.addStatus(status3);

            try{
                doc.getStatus(-1);
                Assert.fail("Exception should have been thrown when trying to get a status using an invalid id (-1)");
            }
            catch(ModelException ex){
                //do nothing
            }
            
            try{
                doc.getStatus(3);
                Assert.fail("Exception should have been thrown when trying to get a status using an invalid id (3)");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testRemoveStatus_Success(){
            StatusedElement doc = factory.createDocument("Doc 1");
            String sName1 = "Status 1";
            int sCode1 = 1;
            Status status1 = factory.createStatus(sName1, sCode1);
            Status status2 = factory.createStatus("Status 2", 2);
            String sName3 = "Status 3";
            int sCode3 = 3;
            Status status3 = factory.createStatus(sName3, sCode3);
            doc.addStatus(status1);
            doc.addStatus(status2);
            doc.addStatus(status3);

            doc.removeStatus(1);
            AssertJUnit.assertEquals("Folder has the wrong number of statuses",2,doc.numStatus());
            AssertJUnit.assertEquals("Status at index 0 has the wrong name",sName1,doc.getStatus(0).getShortName());
            AssertJUnit.assertEquals("Status at index 1 has the wrong name",sName3,doc.getStatus(1).getShortName());
    }
    
    @Test()
	public void testRemoveStatus_InvalidId(){
            StatusedElement doc = factory.createDocument("Doc 1");
            Status status1 = factory.createStatus("Status 1", 1);
            Status status2 = factory.createStatus("Status 2", 2);
            Status status3 = factory.createStatus("Status 3", 3);
            doc.addStatus(status1);
            doc.addStatus(status2);
            doc.addStatus(status3);

            try{
                doc.removeStatus(-1);
                Assert.fail("Exception should have been thrown when trying to remove a status using an invalid id (-1)");
            }
            catch(ModelException ex){
                //do nothing
            }
            
            try{
                doc.removeStatus(3);
                Assert.fail("Exception should have been thrown when trying to remove a status using an invalid id (3)");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
}

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
import org.testng.annotations.Test;
import org.testng.Assert;
import org.testng.AssertJUnit;


public class IStatusTest extends ModelTest {

    @Test()
	public void testConstructor1(){
            String name = "name";
            int code = 32;
            Status status = factory.createStatus(name, code);
            
            AssertJUnit.assertNotNull("Status object is null", status);
            AssertJUnit.assertEquals("Status has the wrong name", name, status.getShortName());
            AssertJUnit.assertEquals("Status has the wrong code", code, status.getCode());
    }
    
    @Test()
	public void testAddStatusTransition_Success(){
            Status status = factory.createStatus("name", 1);
            String name1 = "Trans 1";
            Status trans1 = factory.createStatus(name1, 2);
            String name2 = "Trans 2";
            Status trans2 = factory.createStatus(name2, 3);
            String name3 = "Trans 3";
            Status trans3 = factory.createStatus(name3, 4);
            
            status.addStatusTransition(trans1);
            status.addStatusTransition(trans2);
            AssertJUnit.assertEquals("Status has the wrong number of transitions",2,status.numStatusTransitions());
            AssertJUnit.assertEquals("Transition at index 0 has the wrong text value",name1,status.getStatusTransition(0).getShortName());
            AssertJUnit.assertEquals("Transition at index 1 has the wrong text value",name2,status.getStatusTransition(1).getShortName());
            
            status.addStatusTransition(trans3);
            AssertJUnit.assertEquals("Status has the wrong number of transitions",3,status.numStatusTransitions());
            AssertJUnit.assertEquals("Transition at index 0 has the wrong text value",name1,status.getStatusTransition(0).getShortName());
            AssertJUnit.assertEquals("Transition at index 1 has the wrong text value",name2,status.getStatusTransition(1).getShortName());
            AssertJUnit.assertEquals("Transition at index 2 has the wrong text value",name3,status.getStatusTransition(2).getShortName());
    }
    
    @Test()
	public void testAddStatusTransition_Null(){
            Status status = factory.createStatus("name", 1);
            try{
                status.addStatusTransition(null);
                Assert.fail("Exception should have been thrown when trying to add a null transition");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testNumStatusTransitions(){
            Status status = factory.createStatus("name", 1);
            
            AssertJUnit.assertEquals("Status has the wrong number of transitions",0,status.numStatusTransitions());
            
            String name1 = "Trans 1";
            Status trans1 = factory.createStatus(name1, 2);
            String name2 = "Trans 2";
            Status trans2 = factory.createStatus(name2, 3);
            String name3 = "Trans 3";
            Status trans3 = factory.createStatus(name3, 4);
            
            status.addStatusTransition(trans1);
            status.addStatusTransition(trans2);
            status.addStatusTransition(trans3);
            
            AssertJUnit.assertEquals("Status has the wrong number of transitions",3,status.numStatusTransitions());
    }
    
    @Test()
	public void testGetStatusTransition_Success(){
            Status status = factory.createStatus("name", 1);
            String name1 = "Trans 1";
            Status trans1 = factory.createStatus(name1, 2);
            String name2 = "Trans 2";
            Status trans2 = factory.createStatus(name2, 3);
            String name3 = "Trans 3";
            int code3 = 4;
            Status trans3 = factory.createStatus(name3, code3);
            
            status.addStatusTransition(trans1);
            status.addStatusTransition(trans2);
            status.addStatusTransition(trans3);
            
            Status trans = status.getStatusTransition(2);
            AssertJUnit.assertNotNull("Status transition is null", trans);
            AssertJUnit.assertEquals("Status transition has the wrong name",name3,trans.getShortName());
            AssertJUnit.assertEquals("Status transition has the wrong code", code3, trans.getCode());
    }
    
    @Test()
	public void testGetStatusTransition_InvalidId(){
            Status status = factory.createStatus("name", 1);
            String name1 = "Trans 1";
            Status trans1 = factory.createStatus(name1, 2);
            String name2 = "Trans 2";
            Status trans2 = factory.createStatus(name2, 3);
            String name3 = "Trans 3";
            int code3 = 4;
            Status trans3 = factory.createStatus(name3, code3);
            
            status.addStatusTransition(trans1);
            status.addStatusTransition(trans2);
            status.addStatusTransition(trans3);
            
            try{
                status.getStatusTransition(-1);
                Assert.fail("Exception should have been thrown when trying to get a transition using an invalid id (-1)");
            }
            catch(ModelException ex){
                //do nothing
            }
            
            try{
                status.getStatusTransition(3);
                Assert.fail("Exception should have been thrown when trying to get a transition using an invalid id (3)");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testRemoveStatusTransition_Success(){
            Status status = factory.createStatus("name", 1);
            String name1 = "Trans 1";
            Status trans1 = factory.createStatus(name1, 2);
            String name2 = "Trans 2";
            Status trans2 = factory.createStatus(name2, 3);
            String name3 = "Trans 3";
            int code3 = 4;
            Status trans3 = factory.createStatus(name3, code3);
            
            status.addStatusTransition(trans1);
            status.addStatusTransition(trans2);
            status.addStatusTransition(trans3);
            
            status.removeStatusTransition(0);
            
            AssertJUnit.assertEquals("Status has the wrong number of transitions",2,status.numStatusTransitions());
            AssertJUnit.assertEquals("Transition at index 0 has the wrong text value",name2,status.getStatusTransition(0).getShortName());
            AssertJUnit.assertEquals("Transition at index 1 has the wrong text value",name3,status.getStatusTransition(1).getShortName());
    }
    
    @Test()
	public void testRemoveStatusTransition_InvalidId(){
            Status status = factory.createStatus("name", 1);
            String name1 = "Trans 1";
            Status trans1 = factory.createStatus(name1, 2);
            String name2 = "Trans 2";
            Status trans2 = factory.createStatus(name2, 3);
            String name3 = "Trans 3";
            int code3 = 4;
            Status trans3 = factory.createStatus(name3, code3);
            
            status.addStatusTransition(trans1);
            status.addStatusTransition(trans2);
            status.addStatusTransition(trans3);
            
            try{
                status.removeStatusTransition(-1);
                Assert.fail("Exception should have been thrown when trying to remove a transition using an invalid id (-1)");
            }
            catch(ModelException ex){
                //do nothing
            }
            
            try{
                status.removeStatusTransition(3);
                Assert.fail("Exception should have been thrown when trying to remove a transition using an invalid id (3)");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
}

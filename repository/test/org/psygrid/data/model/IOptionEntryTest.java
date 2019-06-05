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

import org.psygrid.data.model.hibernate.*;
import org.testng.annotations.Test;
import org.testng.Assert;
import org.testng.AssertJUnit;


public class IOptionEntryTest extends ModelTest {

    @Test()
	public void testConstructor1(){
            String name = "Entry 1";
            OptionEntry entry = factory.createOptionEntry(name);
            AssertJUnit.assertEquals("Entry has the wrong name",name,entry.getName());
            AssertJUnit.assertEquals("Entry has the wrong status",EntryStatus.MANDATORY,entry.getEntryStatus());
            AssertJUnit.assertNull("Entry has non-null display text",entry.getDisplayText());
    }
    
    @Test()
	public void testConstructor2(){
            String name = "Entry 1";
            String displayText = "Display text";
            OptionEntry entry = factory.createOptionEntry(name, displayText);
            AssertJUnit.assertEquals("Entry has the wrong name",name,entry.getName());
            AssertJUnit.assertEquals("Entry has the wrong display text",displayText,entry.getDisplayText());
            AssertJUnit.assertEquals("Entry has the wrong status",EntryStatus.MANDATORY,entry.getEntryStatus());
    }
    
    @Test()
	public void testConstructor3(){
            String name = "Entry 1";
            EntryStatus status = EntryStatus.OPTIONAL;
            OptionEntry entry = factory.createOptionEntry(name, status);
            AssertJUnit.assertEquals("Entry has the wrong name",name,entry.getName());
            AssertJUnit.assertEquals("Entry has the wrong status",status,entry.getEntryStatus());
            AssertJUnit.assertNull("Entry has non-null display text",entry.getDisplayText());
    }
    
    @Test()
	public void testConstructor4(){
            String name = "Entry 1";
            String displayText = "Display text";
            EntryStatus status = EntryStatus.OPTIONAL;
            OptionEntry entry = factory.createOptionEntry(name, displayText, status);
            AssertJUnit.assertEquals("Entry has the wrong name",name,entry.getName());
            AssertJUnit.assertEquals("Entry has the wrong status",status,entry.getEntryStatus());
            AssertJUnit.assertEquals("Entry has the wrong display text",displayText,entry.getDisplayText());
    }
    
    @Test()
	public void testNumOptions(){
            OptionEntry entry = factory.createOptionEntry("Entry");
            String name1 = "Option 1";
            Option o1 = factory.createOption(name1, name1);
            String name2 = "Option 2";
            Option o2 = factory.createOption(name2, name2);
            String name3 = "Option 3";
            Option o3 = factory.createOption(name3, name3);
            String name4 = "Option 4";
            Option o4 = factory.createOption(name4, name4);
            entry.addOption(o1);
            entry.addOption(o2);
            entry.addOption(o3);
            entry.addOption(o4);
            
            AssertJUnit.assertEquals("Entry has the wrong number of options",4,entry.numOptions());
    }
    
    @Test()
	public void testAddOption(){
            OptionEntry entry = factory.createOptionEntry("Entry");
            String name1 = "Option 1";
            Option o1 = factory.createOption(name1, name1);
            String name2 = "Option 2";
            Option o2 = factory.createOption(name2, name2);
            String name3 = "Option 3";
            Option o3 = factory.createOption(name3, name3);
            String name4 = "Option 4";
            Option o4 = factory.createOption(name4, name4);
            
            entry.addOption(o1);
            entry.addOption(o2);
            AssertJUnit.assertEquals("Entry has the wrong number of options",2,entry.numOptions());
            AssertJUnit.assertEquals("Option at index 0 has the wrong text value",name1,entry.getOption(0).getDisplayText());
            AssertJUnit.assertEquals("Option at index 1 has the wrong text value",name2,entry.getOption(1).getDisplayText());
            
            entry.addOption(o3);
            entry.addOption(o4);
            AssertJUnit.assertEquals("Entry has the wrong number of options",4,entry.numOptions());
            AssertJUnit.assertEquals("Option at index 0 has the wrong text value",name1,entry.getOption(0).getDisplayText());
            AssertJUnit.assertEquals("Option at index 1 has the wrong text value",name2,entry.getOption(1).getDisplayText());
            AssertJUnit.assertEquals("Option at index 2 has the wrong text value",name3,entry.getOption(2).getDisplayText());
            AssertJUnit.assertEquals("Option at index 3 has the wrong text value",name4,entry.getOption(3).getDisplayText());
    }
    
    @Test()
	public void testAddOption_Null(){
            OptionEntry entry = factory.createOptionEntry("Entry");

            try{
                entry.addOption(null);
                Assert.fail("Exception should have been thrown when trying to add a null option");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Entry has the wrong number of options",0,entry.numOptions());               
            }
    }
    
    @Test()
	public void testGetOption_Success(){
            OptionEntry entry = factory.createOptionEntry("Entry");
            String name1 = "Option 1";
            Option o1 = factory.createOption(name1, name1);
            String name2 = "Option 2";
            Option o2 = factory.createOption(name2, name2);
            String name3 = "Option 3";
            Option o3 = factory.createOption(name3, name3);
            String name4 = "Option 4";
            Option o4 = factory.createOption(name4, name4);
            
            entry.addOption(o1);
            entry.addOption(o2);
            entry.addOption(o3);
            entry.addOption(o4);
            AssertJUnit.assertEquals("Option at index 0 has the wrong text value",name1,entry.getOption(0).getDisplayText());
            AssertJUnit.assertEquals("Option at index 1 has the wrong text value",name2,entry.getOption(1).getDisplayText());
            AssertJUnit.assertEquals("Option at index 2 has the wrong text value",name3,entry.getOption(2).getDisplayText());
            AssertJUnit.assertEquals("Option at index 3 has the wrong text value",name4,entry.getOption(3).getDisplayText());
    }
    
    @Test()
	public void testGetOption_InvalidIndex(){
            OptionEntry entry = factory.createOptionEntry("Entry");
            String name1 = "Option 1";
            Option o1 = factory.createOption(name1, name1);
            String name2 = "Option 2";
            Option o2 = factory.createOption(name2, name2);
            String name3 = "Option 3";
            Option o3 = factory.createOption(name3, name3);
            String name4 = "Option 4";
            Option o4 = factory.createOption(name4, name4);
            
            entry.addOption(o1);
            entry.addOption(o2);
            entry.addOption(o3);
            entry.addOption(o4);

            try{
                entry.getOption(-1);
                Assert.fail("Exception should have been thrown when trying to get an option using an invalid index (-1)");
            }
            catch(ModelException ex){
                //do nothing
            }

            try{
                entry.getOption(4);
                Assert.fail("Exception should have been thrown when trying to get an option using an invalid index (4)");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testInsertOption_Success(){
            OptionEntry entry = factory.createOptionEntry("Entry");
            String name1 = "Option 1";
            Option o1 = factory.createOption(name1, name1);
            String name2 = "Option 2";
            Option o2 = factory.createOption(name2, name2);
            String name3 = "Option 3";
            Option o3 = factory.createOption(name3, name3);
            String name4 = "Option 4";
            Option o4 = factory.createOption(name4, name4);
            
            entry.addOption(o1);
            entry.addOption(o2);
            entry.addOption(o3);
            entry.addOption(o4);
            
            String name5 = "Option 5";
            Option o5 = factory.createOption(name5, name5);
            
            entry.insertOption(o5, 0);            
            AssertJUnit.assertEquals("Entry has the wrong number of options",5,entry.numOptions());
            AssertJUnit.assertEquals("Option at index 0 has the wrong text value",name5,entry.getOption(0).getDisplayText());
            AssertJUnit.assertEquals("Option at index 1 has the wrong text value",name1,entry.getOption(1).getDisplayText());
            AssertJUnit.assertEquals("Option at index 2 has the wrong text value",name2,entry.getOption(2).getDisplayText());
            AssertJUnit.assertEquals("Option at index 3 has the wrong text value",name3,entry.getOption(3).getDisplayText());
            AssertJUnit.assertEquals("Option at index 4 has the wrong text value",name4,entry.getOption(4).getDisplayText());

            String name6 = "Option 6";
            Option o6 = factory.createOption(name6, name6);
            
            entry.insertOption(o6, 5);            
            AssertJUnit.assertEquals("Entry has the wrong number of options",6,entry.numOptions());
            AssertJUnit.assertEquals("Option at index 0 has the wrong text value",name5,entry.getOption(0).getDisplayText());
            AssertJUnit.assertEquals("Option at index 1 has the wrong text value",name1,entry.getOption(1).getDisplayText());
            AssertJUnit.assertEquals("Option at index 2 has the wrong text value",name2,entry.getOption(2).getDisplayText());
            AssertJUnit.assertEquals("Option at index 3 has the wrong text value",name3,entry.getOption(3).getDisplayText());
            AssertJUnit.assertEquals("Option at index 4 has the wrong text value",name4,entry.getOption(4).getDisplayText());
            AssertJUnit.assertEquals("Option at index 5 has the wrong text value",name6,entry.getOption(5).getDisplayText());
    }
    
    @Test()
	public void testInsertOption_Null(){
            OptionEntry entry = factory.createOptionEntry("Entry");
            String name1 = "Option 1";
            Option o1 = factory.createOption(name1, name1);
            String name2 = "Option 2";
            Option o2 = factory.createOption(name2, name2);
            String name3 = "Option 3";
            Option o3 = factory.createOption(name3, name3);
            String name4 = "Option 4";
            Option o4 = factory.createOption(name4, name4);
            
            entry.addOption(o1);
            entry.addOption(o2);
            entry.addOption(o3);
            entry.addOption(o4);
            
            try{
                entry.insertOption(null, 0);
                Assert.fail("Exception should have been thrown when trying to insert a null option");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Entry has the wrong number of options",4,entry.numOptions());
            }
    }
    
    @Test()
	public void testInsertOption_InvalidIndex(){
            OptionEntry entry = factory.createOptionEntry("Entry");
            String name1 = "Option 1";
            Option o1 = factory.createOption(name1, name1);
            String name2 = "Option 2";
            Option o2 = factory.createOption(name2, name2);
            String name3 = "Option 3";
            Option o3 = factory.createOption(name3, name3);
            String name4 = "Option 4";
            Option o4 = factory.createOption(name4, name4);
            
            entry.addOption(o1);
            entry.addOption(o2);
            entry.addOption(o3);
            entry.addOption(o4);
            
            String name5 = "Option 5";
            Option o5 = factory.createOption(name5, name5);
            
            try{
                entry.insertOption(o5, -1);
                Assert.fail("Exception should have been thrown when trying to insert an option using an invalid index (-1)");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Entry has the wrong number of options",4,entry.numOptions());
                AssertJUnit.assertEquals("Option at index 0 has the wrong text value",name1,entry.getOption(0).getDisplayText());
                AssertJUnit.assertEquals("Option at index 1 has the wrong text value",name2,entry.getOption(1).getDisplayText());
                AssertJUnit.assertEquals("Option at index 2 has the wrong text value",name3,entry.getOption(2).getDisplayText());
                AssertJUnit.assertEquals("Option at index 3 has the wrong text value",name4,entry.getOption(3).getDisplayText());
            }
            
            try{
                entry.insertOption(o5, 5);
                Assert.fail("Exception should have been thrown when trying to insert an option using an invalid index (5)");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Entry has the wrong number of options",4,entry.numOptions());
                AssertJUnit.assertEquals("Option at index 0 has the wrong text value",name1,entry.getOption(0).getDisplayText());
                AssertJUnit.assertEquals("Option at index 1 has the wrong text value",name2,entry.getOption(1).getDisplayText());
                AssertJUnit.assertEquals("Option at index 2 has the wrong text value",name3,entry.getOption(2).getDisplayText());
                AssertJUnit.assertEquals("Option at index 3 has the wrong text value",name4,entry.getOption(3).getDisplayText());
            }
    }
    
    @Test()
	public void testRemoveOption_Success(){
            OptionEntry entry = factory.createOptionEntry("Entry");
            String name1 = "Option 1";
            Option o1 = factory.createOption(name1, name1);
            String name2 = "Option 2";
            Option o2 = factory.createOption(name2, name2);
            String name3 = "Option 3";
            Option o3 = factory.createOption(name3, name3);
            String name4 = "Option 4";
            Option o4 = factory.createOption(name4, name4);
            
            entry.addOption(o1);
            entry.addOption(o2);
            entry.addOption(o3);
            entry.addOption(o4);
            
            entry.removeOption(0);
            AssertJUnit.assertEquals("Entry has the wrong number of options",3,entry.numOptions());
            AssertJUnit.assertEquals("Option at index 0 has the wrong text value",name2,entry.getOption(0).getDisplayText());
            AssertJUnit.assertEquals("Option at index 1 has the wrong text value",name3,entry.getOption(1).getDisplayText());
            AssertJUnit.assertEquals("Option at index 2 has the wrong text value",name4,entry.getOption(2).getDisplayText());

            entry.removeOption(2);
            AssertJUnit.assertEquals("Entry has the wrong number of options",2,entry.numOptions());
            AssertJUnit.assertEquals("Option at index 0 has the wrong text value",name2,entry.getOption(0).getDisplayText());
            AssertJUnit.assertEquals("Option at index 1 has the wrong text value",name3,entry.getOption(1).getDisplayText());
    }
    
    @Test()
	public void testRemoveOption_InvalidIndex(){
            OptionEntry entry = factory.createOptionEntry("Entry");
            String name1 = "Option 1";
            Option o1 = factory.createOption(name1, name1);
            String name2 = "Option 2";
            Option o2 = factory.createOption(name2, name2);
            String name3 = "Option 3";
            Option o3 = factory.createOption(name3, name3);
            String name4 = "Option 4";
            Option o4 = factory.createOption(name4, name4);
            
            entry.addOption(o1);
            entry.addOption(o2);
            entry.addOption(o3);
            entry.addOption(o4);
            
            try{
                entry.removeOption(-1);
                Assert.fail("Exception should have been thrown when trying to remove an option using an invalid index (-1)");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Entry has the wrong number of options",4,entry.numOptions());
                AssertJUnit.assertEquals("Option at index 0 has the wrong text value",name1,entry.getOption(0).getDisplayText());
                AssertJUnit.assertEquals("Option at index 1 has the wrong text value",name2,entry.getOption(1).getDisplayText());
                AssertJUnit.assertEquals("Option at index 2 has the wrong text value",name3,entry.getOption(2).getDisplayText());
                AssertJUnit.assertEquals("Option at index 3 has the wrong text value",name4,entry.getOption(3).getDisplayText());
            }
            
            try{
                entry.removeOption(4);
                Assert.fail("Exception should have been thrown when trying to remove an option using an invalid index (4)");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Entry has the wrong number of options",4,entry.numOptions());
                AssertJUnit.assertEquals("Option at index 0 has the wrong text value",name1,entry.getOption(0).getDisplayText());
                AssertJUnit.assertEquals("Option at index 1 has the wrong text value",name2,entry.getOption(1).getDisplayText());
                AssertJUnit.assertEquals("Option at index 2 has the wrong text value",name3,entry.getOption(2).getDisplayText());
                AssertJUnit.assertEquals("Option at index 3 has the wrong text value",name4,entry.getOption(3).getDisplayText());
            }
    }
    
    @Test()
	public void testMoveOption_Success(){
            OptionEntry entry = factory.createOptionEntry("Entry");
            String name1 = "Option 1";
            Option o1 = factory.createOption(name1, name1);
            String name2 = "Option 2";
            Option o2 = factory.createOption(name2, name2);
            String name3 = "Option 3";
            Option o3 = factory.createOption(name3, name3);
            String name4 = "Option 4";
            Option o4 = factory.createOption(name4, name4);
            
            entry.addOption(o1);
            entry.addOption(o2);
            entry.addOption(o3);
            entry.addOption(o4);
            
            entry.moveOption(3,0);
            AssertJUnit.assertEquals("Entry has the wrong number of options",4,entry.numOptions());
            AssertJUnit.assertEquals("Option at index 0 has the wrong text value",name4,entry.getOption(0).getDisplayText());
            AssertJUnit.assertEquals("Option at index 1 has the wrong text value",name1,entry.getOption(1).getDisplayText());
            AssertJUnit.assertEquals("Option at index 2 has the wrong text value",name2,entry.getOption(2).getDisplayText());
            AssertJUnit.assertEquals("Option at index 3 has the wrong text value",name3,entry.getOption(3).getDisplayText());

            entry.moveOption(0,3);
            AssertJUnit.assertEquals("Entry has the wrong number of options",4,entry.numOptions());
            AssertJUnit.assertEquals("Option at index 0 has the wrong text value",name1,entry.getOption(0).getDisplayText());
            AssertJUnit.assertEquals("Option at index 1 has the wrong text value",name2,entry.getOption(1).getDisplayText());
            AssertJUnit.assertEquals("Option at index 2 has the wrong text value",name3,entry.getOption(2).getDisplayText());
            AssertJUnit.assertEquals("Option at index 3 has the wrong text value",name4,entry.getOption(3).getDisplayText());
    }
    
    @Test()
	public void testMoveOption_InvalidIndex(){
            OptionEntry entry = factory.createOptionEntry("Entry");
            String name1 = "Option 1";
            Option o1 = factory.createOption(name1, name1);
            String name2 = "Option 2";
            Option o2 = factory.createOption(name2, name2);
            String name3 = "Option 3";
            Option o3 = factory.createOption(name3, name3);
            String name4 = "Option 4";
            Option o4 = factory.createOption(name4, name4);
            
            entry.addOption(o1);
            entry.addOption(o2);
            entry.addOption(o3);
            entry.addOption(o4);
            
            try{
                entry.moveOption(-1,2);
                Assert.fail("Exception should have been thrown when trying to move an option using an invalid current index (-1)");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Entry has the wrong number of options",4,entry.numOptions());
                AssertJUnit.assertEquals("Option at index 0 has the wrong text value",name1,entry.getOption(0).getDisplayText());
                AssertJUnit.assertEquals("Option at index 1 has the wrong text value",name2,entry.getOption(1).getDisplayText());
                AssertJUnit.assertEquals("Option at index 2 has the wrong text value",name3,entry.getOption(2).getDisplayText());
                AssertJUnit.assertEquals("Option at index 3 has the wrong text value",name4,entry.getOption(3).getDisplayText());
            }

            try{
                entry.moveOption(4,2);
                Assert.fail("Exception should have been thrown when trying to move an option using an invalid current index (4)");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Entry has the wrong number of options",4,entry.numOptions());
                AssertJUnit.assertEquals("Option at index 0 has the wrong text value",name1,entry.getOption(0).getDisplayText());
                AssertJUnit.assertEquals("Option at index 1 has the wrong text value",name2,entry.getOption(1).getDisplayText());
                AssertJUnit.assertEquals("Option at index 2 has the wrong text value",name3,entry.getOption(2).getDisplayText());
                AssertJUnit.assertEquals("Option at index 3 has the wrong text value",name4,entry.getOption(3).getDisplayText());
            }

            try{
                entry.moveOption(2,-1);
                Assert.fail("Exception should have been thrown when trying to move an option using an invalid new index (-1)");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Entry has the wrong number of options",4,entry.numOptions());
                AssertJUnit.assertEquals("Option at index 0 has the wrong text value",name1,entry.getOption(0).getDisplayText());
                AssertJUnit.assertEquals("Option at index 1 has the wrong text value",name2,entry.getOption(1).getDisplayText());
                AssertJUnit.assertEquals("Option at index 2 has the wrong text value",name3,entry.getOption(2).getDisplayText());
                AssertJUnit.assertEquals("Option at index 3 has the wrong text value",name4,entry.getOption(3).getDisplayText());
            }

            try{
                entry.moveOption(2,4);
                Assert.fail("Exception should have been thrown when trying to move an option using an invalid new index (4)");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Entry has the wrong number of options",4,entry.numOptions());
                AssertJUnit.assertEquals("Option at index 0 has the wrong text value",name1,entry.getOption(0).getDisplayText());
                AssertJUnit.assertEquals("Option at index 1 has the wrong text value",name2,entry.getOption(1).getDisplayText());
                AssertJUnit.assertEquals("Option at index 2 has the wrong text value",name3,entry.getOption(2).getDisplayText());
                AssertJUnit.assertEquals("Option at index 3 has the wrong text value",name4,entry.getOption(3).getDisplayText());
            }
    }
    
    @Test()
	public void testGenerateInstance_Blank(){
            Section sec = factory.createSection("SEC");
            SectionOccurrence so = factory.createSectionOccurrence("SO");
            sec.addOccurrence(so);
            String name = "Entry 1";
            OptionEntry entry = factory.createOptionEntry(name);
            entry.setSection(sec);
            String name1 = "Option 1";
            Option o1 = factory.createOption(name1, name1);
            String name2 = "Option 2";
            Option o2 = factory.createOption(name2, name2);
            String name3 = "Option 3";
            Option o3 = factory.createOption(name3, name3);
            String name4 = "Option 4";
            Option o4 = factory.createOption(name4, name4);
            entry.addOption(o1);
            entry.addOption(o2);
            entry.addOption(o3);
            entry.addOption(o4);

            BasicResponse instance = entry.generateInstance(so);
            AssertJUnit.assertNotNull("Instance is null",instance);
            AssertJUnit.assertEquals("Instance does not reference the correct element",entry,instance.getEntry());
            AssertJUnit.assertTrue("Instance is not a Response",instance instanceof Response);
            BasicResponse response = (BasicResponse)instance;
            AssertJUnit.assertNull("Response does not have null value",response.getStringValue());
            AssertJUnit.assertNull("Response does not have null option value",response.getValue());
    }
    
    @Test()
	public void testGenerateValue_Blank(){
            String name = "Entry 1";
            OptionEntry entry = factory.createOptionEntry(name);
            String name1 = "Option 1";
            Option o1 = factory.createOption(name1, name1);
            String name2 = "Option 2";
            Option o2 = factory.createOption(name2, name2);
            String name3 = "Option 3";
            Option o3 = factory.createOption(name3, name3);
            String name4 = "Option 4";
            Option o4 = factory.createOption(name4, name4);
            entry.addOption(o1);
            entry.addOption(o2);
            entry.addOption(o3);
            entry.addOption(o4);

            IValue value = entry.generateValue();
            AssertJUnit.assertTrue("Generated value is not a IOptionValue",value instanceof IOptionValue);
            IOptionValue ov = (IOptionValue)value;
            AssertJUnit.assertEquals("Generated value has the wrong value",null,ov.getValue());
    }
    
}

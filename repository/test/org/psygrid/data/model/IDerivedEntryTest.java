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


public class IDerivedEntryTest extends ModelTest {


    @Test()
	public void testConstructor1(){
            String name = "Entry 1";
            DerivedEntry entry = factory.createDerivedEntry(name);
            AssertJUnit.assertEquals("Entry has the wrong name",name,entry.getName());
            AssertJUnit.assertEquals("Entry has the wrong status",EntryStatus.MANDATORY,entry.getEntryStatus());
            AssertJUnit.assertNull("Entry has non-null display text",entry.getDisplayText());
    }
    
    @Test()
	public void testConstructor2(){
            String name = "Entry 1";
            String displayText = "Display text";
            DerivedEntry entry = factory.createDerivedEntry(name, displayText);
            AssertJUnit.assertEquals("Entry has the wrong name",name,entry.getName());
            AssertJUnit.assertEquals("Entry has the wrong display text",displayText,entry.getDisplayText());
            AssertJUnit.assertEquals("Entry has the wrong status",EntryStatus.MANDATORY,entry.getEntryStatus());
    }
    
    @Test()
	public void testConstructor3(){
            String name = "Entry 1";
            EntryStatus status = EntryStatus.OPTIONAL;
            DerivedEntry entry = factory.createDerivedEntry(name, status);
            AssertJUnit.assertEquals("Entry has the wrong name",name,entry.getName());
            AssertJUnit.assertEquals("Entry has the wrong status",status,entry.getEntryStatus());
            AssertJUnit.assertNull("Entry has non-null display text",entry.getDisplayText());
    }
    
    @Test()
	public void testConstructor4(){
            String name = "Entry 1";
            String displayText = "Display text";
            EntryStatus status = EntryStatus.OPTIONAL;
            DerivedEntry entry = factory.createDerivedEntry(name, displayText, status);
            AssertJUnit.assertEquals("Entry has the wrong name",name,entry.getName());
            AssertJUnit.assertEquals("Entry has the wrong status",status,entry.getEntryStatus());
            AssertJUnit.assertEquals("Entry has the wrong display text",displayText,entry.getDisplayText());
    }
    
    @Test()
	public void testAddVariable_Success(){
            DerivedEntry entry = factory.createDerivedEntry("entry");
            String name1 = "ne1";
            NumericEntry ne1 = factory.createNumericEntry(name1);
            String vn1 = "x_1";
            entry.addVariable(vn1, ne1);
            String name2 = "ne2";
            NumericEntry ne2 = factory.createNumericEntry(name2);
            String vn2 = "Y2";
            entry.addVariable(vn2, ne2);
            
            BasicEntry ie = entry.getVariable(vn1);
            AssertJUnit.assertNotNull("No inputable entry retrieved for name '"+vn1+"'",ie);
            AssertJUnit.assertEquals("Retrieved inputable entry for name '"+vn1+"' has the wrong name",name1,ie.getName());

            BasicEntry ie2 = entry.getVariable(vn2);
            AssertJUnit.assertNotNull("No inputable entry retrieved for name '"+vn2+"'",ie2);
            AssertJUnit.assertEquals("Retrieved inputable entry for name '"+vn2+"' has the wrong name",name2,ie2.getName());
    }
    
    @Test()
	public void testAddVariable_Null(){
            DerivedEntry entry = factory.createDerivedEntry("entry");
            
            try{
                entry.addVariable(null, factory.createNumericEntry("ne1"));
                Assert.fail("Exception should have been thrown when trying to add a variable with a null name");
            }
            catch(ModelException ex){
                //do nothing
            }

            try{
                entry.addVariable("name1", null);
                Assert.fail("Exception should have been thrown when trying to add a variable with a null inputable entry");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testAddVariable_Overwrite(){
            DerivedEntry entry = factory.createDerivedEntry("entry");
            String name1 = "ne1";
            NumericEntry ne1 = factory.createNumericEntry(name1);
            String name2 = "ne2";
            NumericEntry ne2 = factory.createNumericEntry(name2);
            String vn1 = "_x";
            entry.addVariable(vn1, ne1);
            entry.addVariable(vn1, ne2);
            
            BasicEntry ie = entry.getVariable(vn1);
            AssertJUnit.assertNotNull("No inputable entry retrieved for name '"+vn1+"'",ie);
            AssertJUnit.assertEquals("Retrieved inputable entry for name '"+vn1+"' has the wrong name",name2,ie.getName());
    }
    
    @Test()
	public void testAddVariable_IllegalChars(){
            DerivedEntry entry = factory.createDerivedEntry("entry");
            String name1 = "ne1";
            NumericEntry ne1 = factory.createNumericEntry(name1);

            try{
                entry.addVariable("a 1",ne1);
                Assert.fail("Exception should have been thrown when trying to add a variable with a space in the name");
            }
            catch(ModelException ex){
                //do nothing
            }
            
            try{
                entry.addVariable("a+1",ne1);
                Assert.fail("Exception should have been thrown when trying to add a variable with a plus in the name");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testGetVariable_Success(){
            DerivedEntry entry = factory.createDerivedEntry("entry");
            String name1 = "ne1";
            NumericEntry ne1 = factory.createNumericEntry(name1);
            String vn1 = "x";
            entry.addVariable(vn1, ne1);
            String name2 = "ne2";
            NumericEntry ne2 = factory.createNumericEntry(name2);
            String vn2 = "y";
            entry.addVariable(vn2, ne2);
            
            BasicEntry ie = entry.getVariable(vn1);
            AssertJUnit.assertNotNull("No inputable entry retrieved for name '"+vn1+"'",ie);
            AssertJUnit.assertEquals("Retrieved inputable entry for name '"+vn1+"' has the wrong name",name1,ie.getName());

            BasicEntry ie2 = entry.getVariable(vn2);
            AssertJUnit.assertNotNull("No inputable entry retrieved for name '"+vn2+"'",ie2);
            AssertJUnit.assertEquals("Retrieved inputable entry for name '"+vn2+"' has the wrong name",name2,ie2.getName());
    }
    
    @Test()
	public void testGetVariable_InvalidName(){
            DerivedEntry entry = factory.createDerivedEntry("entry");
            String name1 = "ne1";
            NumericEntry ne1 = factory.createNumericEntry(name1);
            String vn1 = "x";
            entry.addVariable(vn1, ne1);
            String name2 = "ne2";
            NumericEntry ne2 = factory.createNumericEntry(name2);
            String vn2 = "y";
            entry.addVariable(vn2, ne2);
            
            try{
                entry.getVariable("z");
                Assert.fail("Exception should have been thrown when trying to get a variable using an invalid name");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testRemoveVariable_Success(){
            DerivedEntry entry = factory.createDerivedEntry("entry");
            String name1 = "ne1";
            NumericEntry ne1 = factory.createNumericEntry(name1);
            String vn1 = "x";
            entry.addVariable(vn1, ne1);
            String name2 = "ne2";
            NumericEntry ne2 = factory.createNumericEntry(name2);
            String vn2 = "y";
            entry.addVariable(vn2, ne2);
            
            entry.removeVariable(vn1);
            try{
                entry.getVariable(vn1);
                Assert.fail("Exception should have been thrown when trying to get a variable using an invalid name");
            }
            catch(ModelException ex){
                //do nothing
            }
            
            BasicEntry ie2 = entry.getVariable(vn2);
            AssertJUnit.assertNotNull("No inputable entry retrieved for name '"+vn2+"'",ie2);
            AssertJUnit.assertEquals("Retrieved inputable entry for name '"+vn2+"' has the wrong name",name2,ie2.getName());            
    }
    
    @Test()
	public void testRemoveVariable_InvalidName(){
            DerivedEntry entry = factory.createDerivedEntry("entry");
            String name1 = "ne1";
            NumericEntry ne1 = factory.createNumericEntry(name1);
            String vn1 = "x";
            entry.addVariable(vn1, ne1);
            String name2 = "ne2";
            NumericEntry ne2 = factory.createNumericEntry(name2);
            String vn2 = "y";
            entry.addVariable(vn2, ne2);
            
            try{
                entry.removeVariable("z");
                Assert.fail("Exception should have been thrown when trying to remove a variable using an invalid name");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testGenerateInstance_Blank(){
            String name = "Entry 1";
            Section sec = factory.createSection("SEC");
            SectionOccurrence so = factory.createSectionOccurrence("SO");
            sec.addOccurrence(so);
            DerivedEntry entry = factory.createDerivedEntry(name);
            entry.setSection(sec);
            BasicResponse instance = entry.generateInstance(so);
            AssertJUnit.assertNotNull("Instance is null",instance);
            AssertJUnit.assertEquals("Instance does not reference the correct element",entry,instance.getEntry());
            AssertJUnit.assertTrue("Instance is not a Response",instance instanceof Response);
            BasicResponse response = (BasicResponse)instance;
            AssertJUnit.assertNull("Response does not have null value",response.getStringValue());
            AssertJUnit.assertNull("Response does not have null numeric value",response.getValue());
    }
    
}

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
import org.testng.AssertJUnit;



public class IBooleanEntryTest extends ModelTest {

    @Test()
	public void testConstructor1(){
            String name = "Entry 1";
            BooleanEntry entry = factory.createBooleanEntry(name);
            AssertJUnit.assertEquals("Entry has the wrong name",name,entry.getName());
            AssertJUnit.assertEquals("Entry has the wrong status",EntryStatus.MANDATORY,entry.getEntryStatus());
            AssertJUnit.assertNull("Entry has non-null display text",entry.getDisplayText());
    }
    
    @Test()
	public void testConstructor2(){
            String name = "Entry 1";
            String displayText = "Display text";
            BooleanEntry entry = factory.createBooleanEntry(name, displayText);
            AssertJUnit.assertEquals("Entry has the wrong name",name,entry.getName());
            AssertJUnit.assertEquals("Entry has the wrong display text",displayText,entry.getDisplayText());
            AssertJUnit.assertEquals("Entry has the wrong status",EntryStatus.MANDATORY,entry.getEntryStatus());
    }
    
    @Test()
	public void testConstructor3(){
            String name = "Entry 1";
            EntryStatus status = EntryStatus.OPTIONAL;
            BooleanEntry entry = factory.createBooleanEntry(name, status);
            AssertJUnit.assertEquals("Entry has the wrong name",name,entry.getName());
            AssertJUnit.assertEquals("Entry has the wrong status",status,entry.getEntryStatus());
            AssertJUnit.assertNull("Entry has non-null display text",entry.getDisplayText());
    }
    
    @Test()
	public void testConstructor4(){
            String name = "Entry 1";
            String displayText = "Display text";
            EntryStatus status = EntryStatus.OPTIONAL;
            BooleanEntry entry = factory.createBooleanEntry(name, displayText, status);
            AssertJUnit.assertEquals("Entry has the wrong name",name,entry.getName());
            AssertJUnit.assertEquals("Entry has the wrong status",status,entry.getEntryStatus());
            AssertJUnit.assertEquals("Entry has the wrong display text",displayText,entry.getDisplayText());
    }
    
    @Test()
	public void testGenerateInstance_Blank(){
            String name = "Entry 1";
            Section sec = factory.createSection("SEC");
            SectionOccurrence so = factory.createSectionOccurrence("SO");
            sec.addOccurrence(so);
            BooleanEntry entry = factory.createBooleanEntry(name);
            entry.setSection(sec);
            BasicResponse response = entry.generateInstance(so);
            AssertJUnit.assertNotNull("Response is null",response);
            AssertJUnit.assertEquals("Instance does not reference the correct response",entry,response.getEntry());
            AssertJUnit.assertNull("Response does not have null value",response.getStringValue());
            AssertJUnit.assertNull("Response does not have null boolean value",response.getValue());
    }
    
    @Test()
	public void testGenerateValue_Blank(){
            String name = "Entry 1";
            BooleanEntry entry = factory.createBooleanEntry(name);
            BooleanValue value = entry.generateValue();
            AssertJUnit.assertTrue("Generated value is not a BooleanValue",value instanceof BooleanValue);
            AssertJUnit.assertEquals("Generated value has the wrong value",false,value.getValue());
    }
    
}

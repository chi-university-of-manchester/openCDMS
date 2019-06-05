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

import org.psygrid.data.model.hibernate.EntryStatus;
import org.psygrid.data.model.hibernate.LongTextEntry;
import org.testng.annotations.Test;
import org.testng.AssertJUnit;


public class ILongTextEntryTest extends ModelTest {

    @Test()
	public void testConstructor1(){
            String name = "Entry 1";
            LongTextEntry entry = factory.createLongTextEntry(name);
            AssertJUnit.assertEquals("Entry has the wrong name",name,entry.getName());
            AssertJUnit.assertEquals("Entry has the wrong status",EntryStatus.MANDATORY,entry.getEntryStatus());
            AssertJUnit.assertNull("Entry has non-null display text",entry.getDisplayText());
    }
    
    @Test()
	public void testConstructor2(){
            String name = "Entry 1";
            String displayText = "Display text";
            LongTextEntry entry = factory.createLongTextEntry(name, displayText);
            AssertJUnit.assertEquals("Entry has the wrong name",name,entry.getName());
            AssertJUnit.assertEquals("Entry has the wrong display text",displayText,entry.getDisplayText());
            AssertJUnit.assertEquals("Entry has the wrong status",EntryStatus.MANDATORY,entry.getEntryStatus());
    }
    
    @Test()
	public void testConstructor3(){
            String name = "Entry 1";
            EntryStatus status = EntryStatus.OPTIONAL;
            LongTextEntry entry = factory.createLongTextEntry(name, status);
            AssertJUnit.assertEquals("Entry has the wrong name",name,entry.getName());
            AssertJUnit.assertEquals("Entry has the wrong status",status,entry.getEntryStatus());
            AssertJUnit.assertNull("Entry has non-null display text",entry.getDisplayText());
    }
    
    @Test()
	public void testConstructor4(){
            String name = "Entry 1";
            String displayText = "Display text";
            EntryStatus status = EntryStatus.OPTIONAL;
            LongTextEntry entry = factory.createLongTextEntry(name, displayText, status);
            AssertJUnit.assertEquals("Entry has the wrong name",name,entry.getName());
            AssertJUnit.assertEquals("Entry has the wrong status",status,entry.getEntryStatus());
            AssertJUnit.assertEquals("Entry has the wrong display text",displayText,entry.getDisplayText());
    }
    
    @Test()
	public void testGenerateValue_Blank(){
            String name = "Entry 1";
            LongTextEntry entry = factory.createLongTextEntry(name);
            IValue value = entry.generateValue();
            AssertJUnit.assertTrue("Generated value is not a ILongTextValue",value instanceof ILongTextValue);
            ILongTextValue tv = (ILongTextValue)value;
            AssertJUnit.assertEquals("Generated value has the wrong value",null,tv.getValue());        
    }
    
    
}

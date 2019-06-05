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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ITextEntryTest extends ModelTest {

    @Test()
	public void testConstructor1(){
            String name = "Entry 1";
            TextEntry entry = factory.createTextEntry(name);
            AssertJUnit.assertEquals("Entry has the wrong name",name,entry.getName());
            AssertJUnit.assertEquals("Entry has the wrong status",EntryStatus.MANDATORY,entry.getEntryStatus());
            AssertJUnit.assertNull("Entry has non-null display text",entry.getDisplayText());
    }
    
    @Test()
	public void testConstructor2(){
            String name = "Entry 1";
            String displayText = "Display text";
            TextEntry entry = factory.createTextEntry(name, displayText);
            AssertJUnit.assertEquals("Entry has the wrong name",name,entry.getName());
            AssertJUnit.assertEquals("Entry has the wrong display text",displayText,entry.getDisplayText());
            AssertJUnit.assertEquals("Entry has the wrong status",EntryStatus.MANDATORY,entry.getEntryStatus());
    }
    
    @Test()
	public void testConstructor3(){
            String name = "Entry 1";
            EntryStatus status = EntryStatus.OPTIONAL;
            TextEntry entry = factory.createTextEntry(name, status);
            AssertJUnit.assertEquals("Entry has the wrong name",name,entry.getName());
            AssertJUnit.assertEquals("Entry has the wrong status",status,entry.getEntryStatus());
            AssertJUnit.assertNull("Entry has non-null display text",entry.getDisplayText());
    }
    
    @Test()
	public void testConstructor4(){
            String name = "Entry 1";
            String displayText = "Display text";
            EntryStatus status = EntryStatus.OPTIONAL;
            TextEntry entry = factory.createTextEntry(name, displayText, status);
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
            TextEntry entry = factory.createTextEntry(name);
            entry.setSection(sec);
            BasicResponse instance = entry.generateInstance(so);
            AssertJUnit.assertNotNull("Instance is null",instance);
            AssertJUnit.assertEquals("Instance does not reference the correct element",entry,instance.getEntry());
            AssertJUnit.assertTrue("Instance is not a Response",instance instanceof Response);
            BasicResponse response = (BasicResponse)instance;
            AssertJUnit.assertNull("Response does not have null value",response.getStringValue());
            AssertJUnit.assertNull("Response does not have null text value",response.getValue());
    }
    
    @Test()
	public void testGenerateValue_Blank(){
            String name = "Entry 1";
            TextEntry entry = factory.createTextEntry(name);
            IValue value = entry.generateValue();
            AssertJUnit.assertTrue("Generated value is not a INumericValue",value instanceof ITextValue);
            ITextValue tv = (ITextValue)value;
            AssertJUnit.assertEquals("Generated value has the wrong value",null,tv.getValue());        
    }
    
    @Test()
	public void testNotification() {
            String name = "Entry 1";
            TextEntry entry = factory.createTextEntry(name);
            final String initialValue = "initial";
            final String newValue = "test";
            final boolean[] hasRun = new boolean[1];
            ITextValue value = entry.generateValue();
            value.setValue(initialValue);
            value.addPropertyChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    hasRun[0] = true;
                    AssertJUnit.assertEquals(evt.getNewValue(), newValue);
                    AssertJUnit.assertEquals(evt.getOldValue(), initialValue);
                    AssertJUnit.assertEquals(evt.getPropertyName(), "value");
                }
                
            });
            value.setValue(newValue);
            if (hasRun[0] == false) {
                Assert.fail("The listener was not executed.");
            }
    }
    
    
}

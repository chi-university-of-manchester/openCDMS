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


public class IOptionValueTest extends ModelTest {

    @Test()
	public void testReadOnly(){
            Section sec = factory.createSection("SEC");
            SectionOccurrence so = factory.createSectionOccurrence("SO");
            sec.addOccurrence(so);
            OptionEntry oe = factory.createOptionEntry("OE");
            oe.setSection(sec);
            Option o1 = factory.createOption("Option 1", "Option 1");
            Option o2 = factory.createOption("Option 2", "Option 2");
            Option o3 = factory.createOption("Option 3", "Option 3");
            oe.addOption(o1);
            oe.addOption(o2);
            oe.addOption(o3);
            BasicResponse resp = oe.generateInstance(so);
            IOptionValue ov = (IOptionValue)oe.generateValue();
            ov.setValue(o2);
            resp.setValue(ov);
            ov.publish();
            try{
                ov.setValue(o2);
                Assert.fail("Exception should have been thrown when trying to set the value of a read-only value");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testGetValueAsString(){
            OptionEntry oe = factory.createOptionEntry("LTE");
            Option o1 = factory.createOption("Foo");
            Option o2 = factory.createOption("Bar", 1);
            IOptionValue ov = oe.generateValue();
            
            AssertJUnit.assertNull("Wrong value returned by getValueAsString for null", ov.getValueAsString());
            
            ov.setValue(o1);
            AssertJUnit.assertEquals("Wrong value returned by getValueAsString", "Foo", ov.getValueAsString());
            
            ov.setValue(o2);
            AssertJUnit.assertEquals("Wrong value returned by getValueAsString", "1. Bar", ov.getValueAsString());
    }
    
}

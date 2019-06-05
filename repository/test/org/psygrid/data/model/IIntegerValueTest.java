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

import org.psygrid.data.model.hibernate.BasicResponse;
import org.psygrid.data.model.hibernate.IntegerEntry;
import org.psygrid.data.model.hibernate.ModelException;
import org.psygrid.data.model.hibernate.Section;
import org.psygrid.data.model.hibernate.SectionOccurrence;
import org.testng.annotations.Test;
import org.testng.Assert;
import org.testng.AssertJUnit;


public class IIntegerValueTest extends ModelTest {

    @Test()
	public void testReadOnly(){
            Section sec = factory.createSection("SEC");
            SectionOccurrence so = factory.createSectionOccurrence("SO");
            sec.addOccurrence(so);
            IntegerEntry ne = factory.createIntegerEntry("NE");
            ne.setSection(sec);
            BasicResponse resp = ne.generateInstance(so);
            IIntegerValue nv = (IIntegerValue)ne.generateValue();
            nv.setValue(23);
            resp.setValue(nv);
            nv.publish();
            try{
                nv.setValue(1);
                Assert.fail("Exception should have been thrown when trying to set the value of a read-only value");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testGetValueAsString(){
            IntegerEntry ne = factory.createIntegerEntry("NE");
            IIntegerValue nv = ne.generateValue();
            
            AssertJUnit.assertNull("Wrong value returned by getValueAsString for null", nv.getValueAsString());
            
            nv.setValue(new Integer(5));
            AssertJUnit.assertEquals("Wrong value returned by getValueAsString", "5", nv.getValueAsString());
    }
}

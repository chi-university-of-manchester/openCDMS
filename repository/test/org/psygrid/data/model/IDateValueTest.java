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
import org.psygrid.data.model.hibernate.DateEntry;
import org.psygrid.data.model.hibernate.ModelException;
import org.psygrid.data.model.hibernate.Section;
import org.psygrid.data.model.hibernate.SectionOccurrence;
import org.testng.annotations.Test;
import org.testng.Assert;
import org.testng.AssertJUnit;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class IDateValueTest extends ModelTest {

    @Test()
	public void testReadOnly(){
            Section sec = factory.createSection("SEC");
            SectionOccurrence so = factory.createSectionOccurrence("SO");
            sec.addOccurrence(so);
            DateEntry de = factory.createDateEntry("DE");
            de.setSection(sec);
            BasicResponse resp = de.generateInstance(so);
            IDateValue dv = (IDateValue)de.generateValue();
            dv.setValue(new Date());
            resp.setValue(dv);
            dv.publish();
            try{
                dv.setValue(new Date());
                Assert.fail("Exception should have been thrown when trying to set the value of a read-only value");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testGetValueAsString(){
            DateEntry de = factory.createDateEntry("DE");
            IDateValue dv = (IDateValue)de.generateValue();

            // Test months with 31 days - grab a date for  31 May 2006
            Calendar cal = Calendar.getInstance();
            cal.set(2006, 4, 31);
            
            Date may312006 = cal.getTime();
            
            dv.setValue(may312006);
            
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
            AssertJUnit.assertEquals("Wrong value returned by getValueAsString for date",formatter.format(may312006), dv.getValueAsString());
            
            dv.setValue(null);
            dv.setMonth(new Integer(3));
            dv.setYear(new Integer(2006));
            AssertJUnit.assertEquals("Wrong value returned by getValueAsString for month and year","April-2006", dv.getValueAsString());
            
            dv.setMonth(null);
            dv.setYear(new Integer(2006));
            AssertJUnit.assertEquals("Wrong value returned by getValueAsString for year","2006", dv.getValueAsString());
            
            dv.setYear(null);
            AssertJUnit.assertNull("Wrong value returned by getValueAsString for null", dv.getValueAsString());
    }
}

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

package org.psygrid.data.model.hibernate;

import org.testng.annotations.Test;
import org.testng.AssertJUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DateValueTest {

    @Test()
	public void testToDTO(){
            DateValue dv = new DateValue();
            Date value = new Date();
            dv.setValue(value);
            Integer month = new Integer(7);
            dv.setMonth(month);
            Integer year = new Integer(2006);
            dv.setYear(year);
            boolean deprecated = true;
            dv.setDeprecated(deprecated);
            
            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
            org.psygrid.data.model.dto.DateValueDTO dtoDV = dv.toDTO(dtoRefs, RetrieveDepth.RS_COMPLETE);

            AssertJUnit.assertNotNull("DTO date value is null", dtoDV);
            AssertJUnit.assertEquals("DTO date value does not have the correct deprecated flag",deprecated,dtoDV.isDeprecated());
            AssertJUnit.assertEquals("DTO date value does not have the correct value",value,dtoDV.getValue());
            AssertJUnit.assertEquals("DTO date value does not have the correct month",month,dtoDV.getMonth());
            AssertJUnit.assertEquals("DTO date value does not have the correct year",year,dtoDV.getYear());
    }
    
    @Test()
	public void testCopy(){
            DateValue dv = new DateValue();
            Unit u1 = new Unit("U1");
            StandardCode sc = new StandardCode("SC1", 1);
            boolean deprecated = true;
            boolean transformed = true;
            Date value = new Date();
            Integer month = new Integer(5);
            Integer year = new Integer(2006);
            dv.setDeprecated(deprecated);
            dv.setStandardCode(sc);
            dv.setTransformed(transformed);
            dv.setUnit(u1);
            dv.setValue(value);
            dv.setMonth(month);
            dv.setYear(year);
            
            DateValue copy = dv.copy();
            
            AssertJUnit.assertEquals("Copied value has the wrong deprecated", deprecated, copy.isDeprecated());
            AssertJUnit.assertEquals("Copied value has the wrong standard code", sc, copy.getStandardCode());
            AssertJUnit.assertEquals("Copied value has the wrong transformed", transformed, copy.isTransformed());
            AssertJUnit.assertEquals("Copied value has the wrong unit", u1, copy.getUnit());
            AssertJUnit.assertEquals("Copied value has the wrong value", value, copy.getValue());
            AssertJUnit.assertEquals("Copied value has the wrong month", month, copy.getMonth());
            AssertJUnit.assertEquals("Copied value has the wrong year", year, copy.getYear());
    }
    
    
}

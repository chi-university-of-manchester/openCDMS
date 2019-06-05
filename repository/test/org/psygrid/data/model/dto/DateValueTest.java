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

package org.psygrid.data.model.dto;

import org.testng.annotations.Test;
import org.testng.AssertJUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DateValueTest {

    @Test()
	public void testToHibernate(){
            DateValueDTO dv = new DateValueDTO();
            Date value = new Date();
            dv.setValue(value);
            Integer month = new Integer(7);
            dv.setMonth(month);
            Integer year = new Integer(9);
            dv.setYear(year);
            boolean deprecated = true;
            dv.setDeprecated(deprecated);
            
            Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
            org.psygrid.data.model.hibernate.DateValue hDV = dv.toHibernate(hRefs);

            AssertJUnit.assertNotNull("Hibernate date value is null", hDV);
            AssertJUnit.assertEquals("Hibernate date value does not have the correct deprecated flag",deprecated,hDV.isDeprecated());
            AssertJUnit.assertEquals("Hibernate date value does not have the correct value",value,hDV.getValue());
            AssertJUnit.assertEquals("Hibernate date value does not have the correct month",month,hDV.getMonth());
            AssertJUnit.assertEquals("Hibernate date value does not have the correct year",year,hDV.getYear());
    }
}

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
import java.util.HashMap;
import java.util.Map;

import org.psygrid.data.model.hibernate.TimeUnits;

public class ReminderTest{

    @Test()
	public void testToHibernate(){
            ReminderDTO r = new ReminderDTO();
            Integer time = new Integer(103);
            r.setTime(time);
            TimeUnits units = TimeUnits.DAYS;
            r.setTimeUnits(units.toString());
            Long oId = new Long(5);
            r.setId(oId);

            Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
            org.psygrid.data.model.hibernate.Reminder hR = r.toHibernate(hRefs);
            
            AssertJUnit.assertNotNull("Hibernate reminder is null", hR);
            AssertJUnit.assertEquals("Hibernate reminder has the wrong id",oId,hR.getId());
            AssertJUnit.assertEquals("Hibernate reminder has the wrong time",time,hR.getTime());
            AssertJUnit.assertEquals("Hibernate reminder has the wrong units",units,hR.getTimeUnits());
    }
    
}

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
import java.util.HashMap;
import java.util.Map;


public class ReminderTest {

    @Test()
	public void testToDTO(){
            Reminder r = new Reminder();
            Integer time = new Integer(103);
            r.setTime(time);
            TimeUnits units = TimeUnits.DAYS;
            r.setTimeUnits(units);
            Long oId = new Long(5);
            r.setId(oId);

            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
            org.psygrid.data.model.dto.ReminderDTO dtoR = r.toDTO(dtoRefs, RetrieveDepth.RS_COMPLETE);
            
            AssertJUnit.assertNotNull("Hibernate reminder is null", dtoR);
            AssertJUnit.assertEquals("Hibernate reminder has the wrong id",oId,dtoR.getId());
            AssertJUnit.assertEquals("Hibernate reminder has the wrong time",time,dtoR.getTime());
            AssertJUnit.assertEquals("Hibernate reminder has the wrong units",units.toString(),dtoR.getTimeUnits());
    }
    
}

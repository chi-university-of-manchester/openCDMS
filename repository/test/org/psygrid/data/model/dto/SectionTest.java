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

public class SectionTest {

    @Test()
	public void testToHibernate(){
            SectionDTO s = new SectionDTO();
            
            Long sId = new Long(2);
            s.setId(sId);
            
            String name = "Name";
            s.setName(name);
            
            s.setOccurrences(new SectionOccurrenceDTO[2]);
            SectionOccurrenceDTO so1 = new SectionOccurrenceDTO();
            Long so1Id = new Long(5);
            so1.setId(so1Id);
            s.getOccurrences()[0] = so1;
            SectionOccurrenceDTO so2 = new SectionOccurrenceDTO();
            Long so2Id = new Long(6);
            so2.setId(so2Id);
            s.getOccurrences()[1] = so2;
            
            Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
            org.psygrid.data.model.hibernate.Section hS = s.toHibernate(hRefs);
            
            AssertJUnit.assertNotNull("Hibernate section is null", hS);
            AssertJUnit.assertEquals("Hibernate section has the wrong id",sId,hS.getId());
            AssertJUnit.assertEquals("Hibernate section has the wrong name",name,hS.getName());
            AssertJUnit.assertEquals("Hibernate section has the wrong number of occurrences",s.getOccurrences().length,hS.getOccurrences().size());
            AssertJUnit.assertNotNull("Hibernate section has null occurrence at index 0", hS.getOccurrences().get(0));
            AssertJUnit.assertEquals("Hibernate section has the wrong occurrence at index 0",so1Id,hS.getOccurrences().get(0).getId());
            AssertJUnit.assertNotNull("Hibernate section has null occurrence at index 1", hS.getOccurrences().get(1));
            AssertJUnit.assertEquals("Hibernate section has the wrong occurrence at index 1",so2Id,hS.getOccurrences().get(1).getId());
    }
}

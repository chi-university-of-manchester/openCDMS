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

public class SectionTest {

    @Test()
	public void testToDTO(){

    	    Section s = new Section();
            
            Long sId = new Long(2);
            s.setId(sId);
            
            String name = "Name";
            s.setName(name);
            
            SectionOccurrence so1 = new SectionOccurrence();
            Long so1Id = new Long(5);
            so1.setId(so1Id);
            s.getOccurrences().add(so1);
            SectionOccurrence so2 = new SectionOccurrence();
            Long so2Id = new Long(6);
            so2.setId(so2Id);
            s.getOccurrences().add(so2);
            
            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
            org.psygrid.data.model.dto.SectionDTO dtoS = s.toDTO(dtoRefs, RetrieveDepth.DS_COMPLETE);
            
            AssertJUnit.assertNotNull("DTO section is null", dtoS);
            AssertJUnit.assertEquals("DTO section has the wrong id",sId,dtoS.getId());
            AssertJUnit.assertEquals("DTO section has the wrong name",name,dtoS.getName());
            AssertJUnit.assertEquals("DTO section has the wrong number of occurrences",s.getOccurrences().size(),dtoS.getOccurrences().length);
            AssertJUnit.assertNotNull("DTO section has null occurrence at index 0", dtoS.getOccurrences()[0]);
            AssertJUnit.assertEquals("DTO section has the wrong occurrence at index 0",so1Id,dtoS.getOccurrences()[0].getId());
            AssertJUnit.assertNotNull("DTO section has null occurrence at index 1", dtoS.getOccurrences()[1]);
            AssertJUnit.assertEquals("DTO section has the wrong occurrence at index 1",so2Id,dtoS.getOccurrences()[1].getId());
            
    }
}

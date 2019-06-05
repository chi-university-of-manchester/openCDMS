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

public class SecOccInstanceTest {

    @Test()
	public void testToDTO(){
            SecOccInstance soi = new SecOccInstance();
            
            Long sId = new Long(2);
            soi.setId(sId);
            
            SectionOccurrence so = new SectionOccurrence();
            Long soId = new Long(3);
            so.setId(soId);
            soi.setSectionOccurrence(so);

            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
            org.psygrid.data.model.dto.SecOccInstanceDTO dtoSOI = soi.toDTO(dtoRefs, RetrieveDepth.DS_COMPLETE);
            
            AssertJUnit.assertNotNull("Hibernate section occurrence instance is null", dtoSOI);
            AssertJUnit.assertEquals("Hibernate section occurrence instance has the wrong id",sId,dtoSOI.getId());
            AssertJUnit.assertEquals("Hibernate section occurrence instance has the wrong section occurrence",soId,dtoSOI.getSectionOccurrenceId());
            
    }

}

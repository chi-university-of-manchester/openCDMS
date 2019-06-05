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

public class PersistentTest {

    @Test()
	public void testToDTO(){
            Persistent p = new Reminder();
            Long id = new Long(2);
            p.setId(id);
            int version = 5;
            p.setVersion(version);
            
            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
            org.psygrid.data.model.dto.PersistentDTO dtoP = p.toDTO(dtoRefs, RetrieveDepth.DS_COMPLETE);
        
            AssertJUnit.assertNotNull("DTO persistent is null", dtoP);
            AssertJUnit.assertEquals("DTO persistent has the wrong id",id,dtoP.getId());
            AssertJUnit.assertEquals("DTO persistent has the wrong version",version,dtoP.getVersion());
    }
}

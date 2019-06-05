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

public class StandardCodeTest {

    @Test()
	public void testToDTO(){
            StandardCode sc = new StandardCode();
            int code = 4;
            sc.setCode(code);
            String desc = "Desc";
            sc.setDescription(desc);
            Long id = new Long(23);
            sc.setId(id);
            
            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
            org.psygrid.data.model.dto.StandardCodeDTO dtoSC = sc.toDTO(dtoRefs, RetrieveDepth.RS_COMPLETE);
            
            AssertJUnit.assertNotNull("DTO standard code is null", dtoSC);
            AssertJUnit.assertEquals("DTO standard code has the wrong code",code,dtoSC.getCode());
            AssertJUnit.assertEquals("DTO standard code has the wrong description",desc,dtoSC.getDescription());
            AssertJUnit.assertEquals("DTO standard code has the wrong id",id,dtoSC.getId());
    }
}

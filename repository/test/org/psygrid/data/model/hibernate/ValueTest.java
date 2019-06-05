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

public class ValueTest {

    @Test()
	public void testToDTO(){
            Value v = new TextValue();
            boolean deprecated = true;
            v.setDeprecated(deprecated);
            boolean transformed = true;
            v.setTransformed(transformed);
            Long id = new Long(2);
            v.setId(id);
            StandardCode sc = new StandardCode();
            Long scid = new Long(3);
            sc.setId(scid);
            v.setStandardCode(sc);
            Unit unit = new Unit();
            Long unitId = new Long(4);
            unit.setId(unitId);
            v.setUnit(unit);
            boolean hidden = true;
            v.setHidden(hidden);
            
            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
            org.psygrid.data.model.dto.ValueDTO dtoV = v.toDTO(dtoRefs, RetrieveDepth.RS_COMPLETE);
        
            AssertJUnit.assertNotNull("DTO value is null", dtoV);
            AssertJUnit.assertEquals("DTO value has the wrong deprecated",deprecated,dtoV.isDeprecated());
            AssertJUnit.assertEquals("DTO value has the wrong transformed",transformed,dtoV.isTransformed());
            AssertJUnit.assertEquals("DTO value has the wrong id",id,dtoV.getId());
            AssertJUnit.assertNotNull("DTO value has null standard code", dtoV.getStandardCode());
            AssertJUnit.assertEquals("DTO value has the wrong standard code",scid,dtoV.getStandardCode().getId());
            AssertJUnit.assertEquals("DTO value has the wrong unit",unitId,dtoV.getUnitId());
            AssertJUnit.assertEquals("DTO value has the wrong hidden",hidden,dtoV.isHidden());
    }
}

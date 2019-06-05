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

public class ValueTest {

    @Test()
	public void testToHibernate(){
            ValueDTO v = new TextValueDTO();
            boolean deprecated = true;
            v.setDeprecated(deprecated);
            boolean transformed = true;
            v.setTransformed(transformed);
            Long id = new Long(2);
            v.setId(id);
            StandardCodeDTO sc = new StandardCodeDTO();
            Long scid = new Long(3);
            sc.setId(scid);
            v.setStandardCode(sc);
            Long unitId = new Long(4);
            v.setUnitId(unitId);
            boolean hidden = true;
            v.setHidden(hidden);
            
            Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
            org.psygrid.data.model.hibernate.Value hV = v.toHibernate(hRefs);
        
            AssertJUnit.assertNotNull("Hibernate value is null", hV);
            AssertJUnit.assertEquals("Hibernate value has the wrong deprecated",deprecated,hV.isDeprecated());
            AssertJUnit.assertEquals("Hibernate value has the wrong transformed",transformed,hV.isTransformed());
            AssertJUnit.assertEquals("Hibernate value has the wrong id",id,hV.getId());
            AssertJUnit.assertNotNull("Hibernate value has null standard code", hV.getStandardCode());
            AssertJUnit.assertEquals("Hibernate value has the wrong standard code",scid,hV.getStandardCode().getId());
            AssertJUnit.assertEquals("Hibernate value has the wrong unit",unitId,hV.getUnitId());
            AssertJUnit.assertEquals("Hibernate value has the wrong hidden",hidden,hV.isHidden());
    }
}

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

public class IntegerValidationRuleTest {

    @Test()
	public void testToDTO(){
            IntegerValidationRule nvr = new IntegerValidationRule();
            String desc = "Desc";
            nvr.setDescription(desc);
            Integer lowerLimit = new Integer(2);
            nvr.setLowerLimit(lowerLimit);
            Integer upperLimit = new Integer(3);
            nvr.setUpperLimit(upperLimit);
            
            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
            org.psygrid.data.model.dto.IntegerValidationRuleDTO dtoNVR = nvr.toDTO(dtoRefs, RetrieveDepth.DS_COMPLETE);
            
            AssertJUnit.assertNotNull("DTO integer validation rule is null", dtoNVR);
            AssertJUnit.assertEquals("DTO integer validation rule has the wrong description",desc,dtoNVR.getDescription());
            AssertJUnit.assertEquals("DTO integer validation rule has the wrong lower limit",lowerLimit,dtoNVR.getLowerLimit());
            AssertJUnit.assertEquals("DTO integer validation rule has the wrong upper limit",upperLimit,dtoNVR.getUpperLimit());
    }
}

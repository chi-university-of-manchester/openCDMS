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

public class ValidationRuleTest {

    @Test()
	public void testToDTO(){
            ValidationRule vr = new DateValidationRule();
            String desc = "Desc";
            vr.setDescription(desc);
            String message = "Message";
            vr.setMessage(message);
            Long id = new Long(321);
            vr.setId(id);
            
            ValidationRule avr1 = new DateValidationRule();
            Long avr1Id = new Long(3);
            avr1.setId(avr1Id);
            vr.getAssociatedRules().add(avr1);
            ValidationRule avr2 = new DateValidationRule();
            Long avr2Id = new Long(4);
            avr2.setId(avr2Id);
            vr.getAssociatedRules().add(avr2);

            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
            org.psygrid.data.model.dto.ValidationRuleDTO dtoVR = vr.toDTO(dtoRefs, RetrieveDepth.DS_COMPLETE);
            
            AssertJUnit.assertNotNull("DTO validation rule is null", dtoVR);
            AssertJUnit.assertEquals("DTO validation rule has the wrong description",desc,dtoVR.getDescription());
            AssertJUnit.assertEquals("DTO validation rule has the wrong message",message,dtoVR.getMessage());
            AssertJUnit.assertEquals("DTO validation rule has the wrong id",id,dtoVR.getId());
            AssertJUnit.assertEquals("DTO validation rule has the wrong number of associated rules",vr.getAssociatedRules().size(),dtoVR.getAssociatedRules().length);
            AssertJUnit.assertNotNull("DTO validation rule has null associated rule at index 0", dtoVR.getAssociatedRules()[0]);
            AssertJUnit.assertEquals("DTO validation rule has the wrong associated rule at index 0",avr1Id,dtoVR.getAssociatedRules()[0].getId());
            AssertJUnit.assertNotNull("DTO validation rule has null associated rule at index 1", dtoVR.getAssociatedRules()[1]);
            AssertJUnit.assertEquals("DTO validation rule has the wrong associated rule at index 1",avr2Id,dtoVR.getAssociatedRules()[1].getId());
    }
}

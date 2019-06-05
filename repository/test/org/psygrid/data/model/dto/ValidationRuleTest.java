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

public class ValidationRuleTest {

    @Test()
	public void testToHibernate(){
            ValidationRuleDTO vr = new DateValidationRuleDTO();
            String desc = "Desc";
            vr.setDescription(desc);
            String message = "Message";
            vr.setMessage(message);
            Long id = new Long(321);
            vr.setId(id);
            
            vr.setAssociatedRules(new ValidationRuleDTO[2]);
            ValidationRuleDTO avr1 = new DateValidationRuleDTO();
            Long avr1Id = new Long(3);
            avr1.setId(avr1Id);
            vr.getAssociatedRules()[0] = avr1;
            ValidationRuleDTO avr2 = new DateValidationRuleDTO();
            Long avr2Id = new Long(4);
            avr2.setId(avr2Id);
            vr.getAssociatedRules()[1] = avr2;
            
            Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
            org.psygrid.data.model.hibernate.ValidationRule hVR = vr.toHibernate(hRefs);
            
            AssertJUnit.assertNotNull("Hibernate validation rule is null", hVR);
            AssertJUnit.assertEquals("Hibernate validation rule has the wrong description",desc,hVR.getDescription());
            AssertJUnit.assertEquals("Hibernate validation rule has the wrong message",message,hVR.getMessage());
            AssertJUnit.assertEquals("Hibernate validation rule has the wrong id",id,hVR.getId());
            AssertJUnit.assertEquals("Hibernate validation rule has the wrong number of associated rules",vr.getAssociatedRules().length,hVR.getAssociatedRules().size());
            AssertJUnit.assertNotNull("Hibernate validation rule has null associated rule at index 0", hVR.getAssociatedRules().get(0));
            AssertJUnit.assertEquals("Hibernate validation rule has the wrong associated rule at index 0",avr1Id,hVR.getAssociatedRules().get(0).getId());
            AssertJUnit.assertNotNull("Hibernate validation rule has null associated rule at index 1", hVR.getAssociatedRules().get(1));
            AssertJUnit.assertEquals("Hibernate validation rule has the wrong associated rule at index 1",avr2Id,hVR.getAssociatedRules().get(1).getId());
    }
}

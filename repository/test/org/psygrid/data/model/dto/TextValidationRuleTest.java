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

public class TextValidationRuleTest {

    @Test()
	public void testToHibernate(){
            TextValidationRuleDTO tvr = new TextValidationRuleDTO();
            String desc = "Desc";
            tvr.setDescription(desc);
            Integer lowerLimit = new Integer(2);
            tvr.setLowerLimit(lowerLimit);
            Integer upperLimit = new Integer(5);
            tvr.setUpperLimit(upperLimit);
            String pattern = "Blah";
            tvr.setPattern(pattern);
            String details = "Details";
            tvr.setPatternDetails(details);
            
            Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
            org.psygrid.data.model.hibernate.TextValidationRule hTVR = tvr.toHibernate(hRefs);
            
            AssertJUnit.assertNotNull("Hibernate text validation rule is null", hTVR);
            AssertJUnit.assertEquals("Hibernate text validation rule has the wrong description",desc,hTVR.getDescription());
            AssertJUnit.assertEquals("Hibernate text validation rule has the wrong lower limit",lowerLimit,hTVR.getLowerLimit());
            AssertJUnit.assertEquals("Hibernate text validation rule has the wrong upper limit",upperLimit,hTVR.getUpperLimit());
            AssertJUnit.assertEquals("Hibernate text validation rule has the wrong pattern",pattern,hTVR.getPattern());
            AssertJUnit.assertEquals("Hibernate text validation rule has the wrong pattern details",details,hTVR.getPatternDetails());
    }
}

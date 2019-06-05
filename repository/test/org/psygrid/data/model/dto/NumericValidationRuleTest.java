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

public class NumericValidationRuleTest {

    @Test()
	public void testToHibernate(){
            NumericValidationRuleDTO nvr = new NumericValidationRuleDTO();
            String desc = "Desc";
            nvr.setDescription(desc);
            Double lowerLimit = new Double(1.23);
            nvr.setLowerLimit(lowerLimit);
            Double upperLimit = new Double(3.21);
            nvr.setUpperLimit(upperLimit);
            
            Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
            org.psygrid.data.model.hibernate.NumericValidationRule hNVR = nvr.toHibernate(hRefs);
            
            AssertJUnit.assertNotNull("Hibernate numeric validation rule is null", hNVR);
            AssertJUnit.assertEquals("Hibernate numeric validation rule has the wrong description",desc,hNVR.getDescription());
            AssertJUnit.assertEquals("Hibernate numeric validation rule has the wrong lower limit",lowerLimit,hNVR.getLowerLimit());
            AssertJUnit.assertEquals("Hibernate numeric validation rule has the wrong upper limit",upperLimit,hNVR.getUpperLimit());
    }
}

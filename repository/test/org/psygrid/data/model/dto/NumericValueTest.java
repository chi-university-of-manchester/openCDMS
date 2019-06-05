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

public class NumericValueTest {

    @Test()
	public void testToHibernate(){
            NumericValueDTO nv = new NumericValueDTO();
            Double value = new Double(1.23);
            nv.setValue(value);
            boolean deprecated = true;
            nv.setDeprecated(deprecated);
            
            Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
            org.psygrid.data.model.hibernate.NumericValue hNV = nv.toHibernate(hRefs);

            AssertJUnit.assertNotNull("Hibernate numeric value is null", hNV);
            AssertJUnit.assertEquals("Hibernate numeric value does not have the correct deprecated flag",deprecated,hNV.isDeprecated());
            AssertJUnit.assertEquals("Hibernate numeric value does not have the correct value",value,hNV.getValue());
    }
}

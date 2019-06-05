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

public class NumericEntryTest {

    @Test()
	public void testToHibernate(){
            NumericEntryDTO ne = new NumericEntryDTO();
            ne.setUnits(new UnitDTO[1]);
            ne.getUnits()[0] = new UnitDTO();
            
            Double defVal = new Double(3.3);
            ne.setDefaultValue(defVal);
            
            Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
            org.psygrid.data.model.hibernate.NumericEntry hNE = ne.toHibernate(hRefs);
            
            AssertJUnit.assertNotNull("Hibernate numeric entry is null", hNE);
            AssertJUnit.assertEquals("Hibernate numeric entry does not have the right number of units",ne.getUnits().length,hNE.getUnits().size());
            AssertJUnit.assertEquals("Hibernate numeric entry does not have the right default value",defVal,hNE.getDefaultValue());
    }
}

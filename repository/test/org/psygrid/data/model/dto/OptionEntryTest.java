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

public class OptionEntryTest {

    @Test()
	public void testToHibernate(){
            OptionEntryDTO oe = new OptionEntryDTO();
            
            oe.setUnits(new UnitDTO[1]);
            oe.getUnits()[0] = new UnitDTO();
            
            oe.setOptions(new OptionDTO[2]);
            Integer code1 = new Integer(101);
            OptionDTO o1 = new OptionDTO();
            o1.setCode(code1);
            Integer code2 = new Integer(102);
            OptionDTO o2 = new OptionDTO();
            o2.setCode(code2);
            oe.getOptions()[0] = o1;
            oe.getOptions()[1] = o2;
            
            oe.setDefaultValue(o2);
            
            boolean display = true;
            oe.setOptionCodesDisplayed(display);
            
            Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
            org.psygrid.data.model.hibernate.OptionEntry hOE = oe.toHibernate(hRefs);
            
            AssertJUnit.assertNotNull("Hibernate option entry is null", hOE);
            AssertJUnit.assertEquals("Hibernate option entry does not have the right number of units",oe.getUnits().length,hOE.getUnits().size());
            AssertJUnit.assertEquals("Hibernate option entry does not have the right number of options",oe.getOptions().length,hOE.getOptions().size());
            AssertJUnit.assertEquals("Hibernate option entry does not have the right option at index 0",code1,hOE.getOptions().get(0).getCode());
            AssertJUnit.assertEquals("Hibernate option entry does not have the right option at index 1",code2,hOE.getOptions().get(1).getCode());
            AssertJUnit.assertNotNull("Hibernate option entry has null default value",hOE.getDefaultValue());
            AssertJUnit.assertEquals("Hibernate option entry does not have the right default value",code2,hOE.getDefaultValue().getCode());
            AssertJUnit.assertTrue("Hibernate option entry default value and option 2 are separate instances",hOE.getDefaultValue() == hOE.getOptions().get(1));
            AssertJUnit.assertEquals("Hibernate option entry does not have the right option codes displayed",display,hOE.isOptionCodesDisplayed());
    }
}

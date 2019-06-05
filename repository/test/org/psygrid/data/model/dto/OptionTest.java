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

public class OptionTest {

    @Test()
	public void testToHibernate(){
            OptionDTO o = new OptionDTO();
            
            Integer code = new Integer(43);
            o.setCode(code);
            
            Long id = new Long(2);
            o.setId(id);
            
            String text = "Text";
            o.setDisplayText(text);
            
            o.setOptionDependents(new OptionDependentDTO[2]);
            OptionDependentDTO od1 = new OptionDependentDTO();
            Long od1id = new Long(3);
            od1.setId(od1id);
            o.getOptionDependents()[0] = od1;
            OptionDependentDTO od2 = new OptionDependentDTO();
            Long od2id = new Long(2);
            od2.setId(od2id);
            o.getOptionDependents()[1] = od2;
            
            boolean textOK = true;
            o.setTextEntryAllowed(textOK);
            
            Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
            org.psygrid.data.model.hibernate.Option hO = o.toHibernate(hRefs);
            
            AssertJUnit.assertNotNull("Hibernate option is null", hO);
            AssertJUnit.assertEquals("Hibernate option has the wrong code",code,hO.getCode());
            AssertJUnit.assertEquals("Hibernate option has the wrong id",id,hO.getId());
            AssertJUnit.assertEquals("Hibernate option has the wrong text",text,hO.getDisplayText());
            AssertJUnit.assertEquals("Hibernate option has the wrong number of option dependents",o.getOptionDependents().length,hO.getOptionDependents().size());
            AssertJUnit.assertNotNull("Hibernate option has null option dependent at index 0", hO.getOptionDependents().get(0));
            AssertJUnit.assertEquals("Hibernate option has the wrong option dependent at index 0",od1id,hO.getOptionDependents().get(0).getId());
            AssertJUnit.assertNotNull("Hibernate option has null option dependent at index 1", hO.getOptionDependents().get(1));
            AssertJUnit.assertEquals("Hibernate option has the wrong option dependent at index 1",od2id,hO.getOptionDependents().get(1).getId());
            AssertJUnit.assertEquals("Hibernate option has the wrong value for the text entry allowed flag",textOK,hO.isTextEntryAllowed());
    }
}

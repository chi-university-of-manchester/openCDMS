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

public class OptionTest {

    @Test()
	public void testToDTO(){
            Option o = new Option();
            
            Integer code = new Integer(43);
            o.setCode(code);
            
            Long id = new Long(2);
            o.setId(id);
            
            String text = "Text";
            o.setDisplayText(text);
            
            OptionDependent od1 = new OptionDependent();
            Long od1id = new Long(3);
            od1.setId(od1id);
            o.getOptionDependents().add(od1);
            OptionDependent od2 = new OptionDependent();
            Long od2id = new Long(2);
            od2.setId(od2id);
            o.getOptionDependents().add(od2);
            
            boolean textOK = true;
            o.setTextEntryAllowed(textOK);
            
            
            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
            org.psygrid.data.model.dto.OptionDTO dtoO = o.toDTO(dtoRefs, RetrieveDepth.DS_COMPLETE);
            
            AssertJUnit.assertNotNull("DTO option is null", dtoO);
            AssertJUnit.assertEquals("DTO option has the wrong code",code,dtoO.getCode());
            AssertJUnit.assertEquals("DTO option has the wrong id",id,dtoO.getId());
            AssertJUnit.assertEquals("DTO option has the wrong text",text,dtoO.getDisplayText());
            AssertJUnit.assertEquals("DTO option has the wrong number of option dependents",o.getOptionDependents().size(),dtoO.getOptionDependents().length);
            AssertJUnit.assertNotNull("DTO option has null option dependent at index 0", dtoO.getOptionDependents()[0]);
            AssertJUnit.assertEquals("DTO option has the wrong option dependent at index 0",od1id,dtoO.getOptionDependents()[0].getId());
            AssertJUnit.assertNotNull("DTO option has null option dependent at index 1", dtoO.getOptionDependents()[1]);
            AssertJUnit.assertEquals("DTO option has the wrong option dependent at index 1",od2id,dtoO.getOptionDependents()[1].getId());
            AssertJUnit.assertEquals("DTO option has the wrong value for the text entry allowed flag",textOK,dtoO.isTextEntryAllowed());
    }
}

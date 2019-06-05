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

import org.psygrid.data.query.IEntryStatement;
import org.psygrid.data.query.QueryStatementValue;
import org.psygrid.data.query.hibernate.OptionStatement;
import org.testng.annotations.Test;
import org.testng.AssertJUnit;
import java.util.HashMap;
import java.util.Map;

public class OptionEntryTest {

    @Test()
	public void testToDTO(){
            OptionEntry oe = new OptionEntry();
            
            oe.getUnits().add(new Unit());
            
            Integer code1 = new Integer(101);
            Option o1 = new Option();
            o1.setCode(code1);
            Integer code2 = new Integer(102);
            Option o2 = new Option();
            o2.setCode(code2);
            oe.getOptions().add(o1);
            oe.getOptions().add(o2);
            
            oe.setDefaultValue(o2);
            
            boolean display = true;
            oe.setOptionCodesDisplayed(display);
            
            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
            org.psygrid.data.model.dto.OptionEntryDTO dtoOE = oe.toDTO(dtoRefs, RetrieveDepth.DS_COMPLETE);
            
            AssertJUnit.assertNotNull("DTO option entry is null", dtoOE);
            AssertJUnit.assertEquals("DTO option entry does not have the right number of units",oe.getUnits().size(),dtoOE.getUnits().length);
            AssertJUnit.assertEquals("DTO option entry does not have the right number of options",oe.getOptions().size(),dtoOE.getOptions().length);
            AssertJUnit.assertEquals("Hibernate option entry does not have the right option at index 0",code1,dtoOE.getOptions()[0].getCode());
            AssertJUnit.assertEquals("Hibernate option entry does not have the right option at index 1",code2,dtoOE.getOptions()[1].getCode());
            AssertJUnit.assertNotNull("DTO option entry has null default value",dtoOE.getDefaultValue());
            AssertJUnit.assertEquals("DTO option entry does not have the right default value",code2,dtoOE.getDefaultValue().getCode());
            AssertJUnit.assertTrue("DTO option entry default value and option 2 are separate instances",dtoOE.getDefaultValue() == dtoOE.getOptions()[1]);
            AssertJUnit.assertEquals("DTO option entry has wrong option codes displayed value", display, dtoOE.isOptionCodesDisplayed());
    }
    
    @Test
    public void testCreateStatement() {
    	QueryStatementValue queryStatement = new QueryStatementValue();
    	Option value = new Option("option",1);
    	queryStatement.setOptionValue(value);
    	Entry optionEntry = new OptionEntry();
    	IEntryStatement optionEntryStatement = optionEntry.createStatement(queryStatement);
    	AssertJUnit.assertTrue(optionEntryStatement instanceof OptionStatement);
    	AssertJUnit.assertEquals(value, optionEntryStatement.getTheValue());
    }
}

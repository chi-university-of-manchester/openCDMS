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
import org.psygrid.data.query.hibernate.TextStatement;
import org.testng.annotations.Test;
import org.testng.AssertJUnit;
import java.util.HashMap;
import java.util.Map;

public class TextEntryTest {

    @Test()
	public void testToDTO(){
            TextEntry te = new TextEntry();
            te.getUnits().add(new Unit());
            int size = 34;
            te.setSize(size);
            
            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
            org.psygrid.data.model.dto.TextEntryDTO dtoTE = te.toDTO(dtoRefs, RetrieveDepth.DS_COMPLETE);
            
            AssertJUnit.assertNotNull("DTO text entry is null", dtoTE);
            AssertJUnit.assertEquals("DTO text entry does not have the right number of units",te.getUnits().size(),dtoTE.getUnits().length);
            AssertJUnit.assertEquals("DTO text entry does not have the correct size",te.getSize(), dtoTE.getSize());
    }
    
    @Test
    public void testCreateStatement() {
    	QueryStatementValue queryStatement = new QueryStatementValue();
    	String value = new String("text");
    	queryStatement.setTextValue(value);
    	Entry textEntry = new TextEntry();
    	IEntryStatement textEntryStatement = textEntry.createStatement(queryStatement);
    	AssertJUnit.assertTrue(textEntryStatement instanceof TextStatement);
    	AssertJUnit.assertEquals(value, textEntryStatement.getTheValue());    	
    }
}

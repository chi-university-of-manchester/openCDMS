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

public class LongTextEntryTest {

    @Test()
	public void testToDTO(){
            LongTextEntry lte = new LongTextEntry();
            lte.getUnits().add(new Unit());
            
            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
            org.psygrid.data.model.dto.LongTextEntryDTO dtoLTE = lte.toDTO(dtoRefs, RetrieveDepth.DS_COMPLETE);
            
            AssertJUnit.assertNotNull("DTO long text entry is null", dtoLTE);
            AssertJUnit.assertEquals("DTO long text entry does not have the right number of units",lte.getUnits().size(),dtoLTE.getUnits().length);
    }
    
    @Test
    public void testCreateStatement() {
    	QueryStatementValue queryStatement = new QueryStatementValue();
    	String value = new String("text");
    	queryStatement.setTextValue(value);
    	Entry longTextEntry = new LongTextEntry();
    	IEntryStatement longTextEntryStatement = longTextEntry.createStatement(queryStatement);
    	AssertJUnit.assertTrue(longTextEntryStatement instanceof TextStatement);
    	AssertJUnit.assertEquals(value, longTextEntryStatement.getTheValue());
    }
}

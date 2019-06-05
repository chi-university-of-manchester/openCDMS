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
import org.psygrid.data.query.hibernate.IntegerStatement;
import org.testng.annotations.Test;
import org.testng.AssertJUnit;
import java.util.HashMap;
import java.util.Map;

public class IntegerEntryTest {

    @Test()
	public void testToDTO(){
            IntegerEntry ie = new IntegerEntry();
            ie.getUnits().add(new Unit());
            
            Integer defVal = new Integer(5);
            ie.setDefaultValue(defVal);
            
            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
            org.psygrid.data.model.dto.IntegerEntryDTO dtoIE = ie.toDTO(dtoRefs, RetrieveDepth.DS_COMPLETE);
            
            AssertJUnit.assertNotNull("DTO integer entry is null", dtoIE);
            AssertJUnit.assertEquals("DTO integer entry does not have the right number of units",ie.getUnits().size(),dtoIE.getUnits().length);
            AssertJUnit.assertEquals("DTO integer entry does not have the right default value",defVal,dtoIE.getDefaultValue());
    }
    
    @Test
    public void testCreateStatement() {
    	QueryStatementValue queryStatement = new QueryStatementValue();
    	Integer value = new Integer(2);
    	queryStatement.setIntegerValue(value);
    	Entry integerEntry = new IntegerEntry();
    	IEntryStatement integerEntryStatement = integerEntry.createStatement(queryStatement);
    	AssertJUnit.assertTrue(integerEntryStatement instanceof IntegerStatement);
    	AssertJUnit.assertEquals(value, integerEntryStatement.getTheValue());
    }
}

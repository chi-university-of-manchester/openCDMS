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
import org.psygrid.data.query.hibernate.DateStatement;
import org.testng.annotations.Test;
import org.testng.AssertJUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DateEntryTest {

    @Test()
	public void testToDTO(){
            DateEntry de = new DateEntry();
            de.getUnits().add(new Unit());
            String format = "dd-mmm-yyyy";
            de.setFormat(format);
            
            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
            org.psygrid.data.model.dto.DateEntryDTO dtoDE = de.toDTO(dtoRefs, RetrieveDepth.DS_COMPLETE);
            
            AssertJUnit.assertNotNull("DTO date entry is null", dtoDE);
            AssertJUnit.assertEquals("DTO date entry does not have the right number of units",de.getUnits().size(),dtoDE.getUnits().length);
            AssertJUnit.assertEquals("DTO date entry does not have the correct format",format,dtoDE.getFormat());
    }
    
    @Test()
	public void testIsNull(){
            DateValue bv = new DateValue();
            AssertJUnit.assertTrue("isNull should return true", bv.isNull());
            bv.setValue(new Date());
            AssertJUnit.assertFalse("isNull should return false for date value with a date", bv.isNull());
            bv.setValue(null);
            bv.setMonth(new Integer(5));
            AssertJUnit.assertFalse("isNull should return false for date value with a month", bv.isNull());
            bv.setMonth(null);
            bv.setYear(new Integer(2006));
            AssertJUnit.assertFalse("isNull should return false for date value with a year", bv.isNull());
    }
    
    @Test
    public void testCreateStatement() {
    	QueryStatementValue value = new QueryStatementValue();
    	Date date = new Date();
    	value.setDateValue(date);
    	Entry dateEntry = new DateEntry();
    	IEntryStatement dateEntryStatement = dateEntry.createStatement(value);
    	AssertJUnit.assertTrue(dateEntryStatement instanceof DateStatement);
    	AssertJUnit.assertEquals(dateEntryStatement.getTheValue(),date);
    }
}

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
import org.psygrid.data.query.hibernate.NumericStatement;
import org.testng.annotations.Test;
import org.testng.AssertJUnit;
import java.util.HashMap;
import java.util.Map;

public class NumericEntryTest {

    @Test()
	public void testToDTO(){
            NumericEntry ne = new NumericEntry();
            ne.getUnits().add(new Unit());
            
            Double defVal = new Double(3.3);
            ne.setDefaultValue(defVal);
            
            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
            org.psygrid.data.model.dto.NumericEntryDTO dtoNE = ne.toDTO(dtoRefs, RetrieveDepth.DS_COMPLETE);
            
            AssertJUnit.assertNotNull("DTO numeric entry is null", dtoNE);
            AssertJUnit.assertEquals("DTO numeric entry does not have the right number of units",ne.getUnits().size(),dtoNE.getUnits().length);
            AssertJUnit.assertEquals("DTO numeric entry does not have the right default value",defVal,dtoNE.getDefaultValue());
    }
    
    @Test
    public void testCreateStatement() {
    	QueryStatementValue queryStatement = new QueryStatementValue();
    	Double value = new Double(2.3);
    	queryStatement.setDoubleValue(value);
    	Entry numericEntry = new NumericEntry();
    	IEntryStatement numericEntryStatement = numericEntry.createStatement(queryStatement);
    	AssertJUnit.assertTrue(numericEntryStatement instanceof NumericStatement);
    	AssertJUnit.assertEquals(value, numericEntryStatement.getTheValue());
    }
}

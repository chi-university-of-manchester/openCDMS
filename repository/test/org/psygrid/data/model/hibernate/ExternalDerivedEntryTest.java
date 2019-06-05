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

import org.psygrid.data.query.IEntryStatement;
import org.psygrid.data.query.QueryStatementValue;
import org.psygrid.data.query.hibernate.NumericStatement;

public class ExternalDerivedEntryTest {
	
	@Test()
	@SuppressWarnings("unchecked")
	public void testToDTO(){
			//TODO finish writing test case
            ExternalDerivedEntry ede = new ExternalDerivedEntry();
            
            //TODO finish this - create an Opcrit transformer
            Transformer transformer = new Transformer();
            ede.setExternalTransformer(transformer);
            
            String keyX = "x";
            BasicEntry varX = new NumericEntry();
            Long varXId = new Long(8);
            varX.setId(varXId);
            String keyY = "y";
            BasicEntry varY = new NumericEntry();
            Long varYId = new Long(9);
            varY.setId(varYId);
            
            ede.getVariables().put(keyX,varX);
            ede.getVariables().put(keyY,varY);
            
            //ede.getUnits().add(new Unit());
            
            //TODO what type of entry?
            //CompositeEntry ce = new CompositeEntry();
           // Long ceId = new Long(10);
           //ce.setId(ceId);
            //ede.setComposite(ce);
            
            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
            org.psygrid.data.model.dto.ExternalDerivedEntryDTO dtoDE = ede.toDTO(dtoRefs, RetrieveDepth.DS_COMPLETE);
            
            AssertJUnit.assertNotNull("DTO external derived entry is null", dtoDE);
           // assertEquals("DTO external derived entry does not have the right number of units",ede.getUnits().size(),dtoDE.getUnits().length);
            AssertJUnit.assertEquals("DTO external derived entry does not have the correct number of variable keys",ede.getVariables().size(), dtoDE.getVariableKeys().length);
            AssertJUnit.assertEquals("DTO external derived entry does not have the correct number of variables",ede.getVariables().size(), dtoDE.getVariables().length);
            int position = 0;
            for ( int i=0; i<dtoDE.getVariableKeys().length; i++ ){
                if ( dtoDE.getVariableKeys()[i].equals(keyY) ){
                    position = i;
                    break;
                }
            }
            AssertJUnit.assertEquals("DTO external derived entry does not have the the correct variable at position "+position, varYId, dtoDE.getVariables()[position].getId());
            //assertEquals("DTO derived entry does not have the correct composite",ceId,dtoDE.getComposite().getId());
            AssertJUnit.assertNotNull("DTO external derived entry does not exist", dtoDE.getExternalTransformer());
    }
	
    @Test
    public void testCreateStatement() {
    	QueryStatementValue queryStatement = new QueryStatementValue();
    	Double value = new Double(2.3);
    	queryStatement.setDoubleValue(value);
    	Entry externalDerivedEntry = new ExternalDerivedEntry();
    	IEntryStatement externalDerivedEntryStatement = externalDerivedEntry.createStatement(queryStatement);
    	AssertJUnit.assertTrue(externalDerivedEntryStatement instanceof NumericStatement);
    	AssertJUnit.assertEquals(value, externalDerivedEntryStatement.getTheValue());
    }    
}

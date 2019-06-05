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

public class DerivedEntryTest {

    @Test()
	@SuppressWarnings("unchecked")
    public void testToDTO(){
            DerivedEntry de = new DerivedEntry();
            
            de.getUnits().add(new Unit());
            
            String formula = "x+y";
            de.setFormula(formula);
            
            String keyX = "x";
            BasicEntry varX = new NumericEntry();
            Long varXId = new Long(8);
            varX.setId(varXId);
            String keyY = "y";
            BasicEntry varY = new NumericEntry();
            Long varYId = new Long(9);
            varY.setId(varYId);
            de.getVariables().put(keyX,varX);
            de.getVariables().put(keyY,varY);
            
            NumericValue nv1 = new NumericValue();
            Long nv1id = Long.valueOf(20);
            nv1.setId(nv1id);
            nv1.setValue(new Double(30));
            de.getVariableDefaults().put(keyX, nv1);
            NumericValue nv2 = new NumericValue();
            Long nv2id = Long.valueOf(21);
            nv2.setId(nv2id);
            nv2.setValue(new Double(31));
            de.getVariableDefaults().put(keyX, nv1);
            
            String aggOp = "+";
            de.setAggregateOperator(aggOp);
            
            CompositeEntry ce = new CompositeEntry();
            Long ceId = new Long(10);
            ce.setId(ceId);
            de.setComposite(ce);
            
            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
            org.psygrid.data.model.dto.DerivedEntryDTO dtoDE = de.toDTO(dtoRefs, RetrieveDepth.DS_COMPLETE);
            
            AssertJUnit.assertNotNull("DTO derived entry is null", dtoDE);
            AssertJUnit.assertEquals("DTO derived entry does not have the right number of units",de.getUnits().size(),dtoDE.getUnits().length);
            AssertJUnit.assertEquals("DTO derived entry does not have the correct formula",formula,dtoDE.getFormula());
            AssertJUnit.assertEquals("DTO derived entry does not have the correct number of variable keys",de.getVariables().size(),dtoDE.getVariableKeys().length);
            AssertJUnit.assertEquals("DTO derived entry does not have the correct number of variables",de.getVariables().size(),dtoDE.getVariables().length);
            int position = 0;
            for ( int i=0; i<dtoDE.getVariableKeys().length; i++ ){
                if ( dtoDE.getVariableKeys()[i].equals(varY) ){
                    position = i;
                    break;
                }
            }
            AssertJUnit.assertEquals("DTO derived entry does not have the the correct variable at position "+position,varYId,dtoDE.getVariables()[position].getId());

            AssertJUnit.assertEquals("DTO derived entry does not have the correct number of variable default keys",de.getVariableDefaults().size(),dtoDE.getVariableDefaultKeys().length);
            AssertJUnit.assertEquals("DTO derived entry does not have the correct number of variables default values",de.getVariableDefaults().size(),dtoDE.getVariableDefaultValues().length);
            for ( int i=0, c=dtoDE.getVariableDefaultKeys().length; i<c; i++){
            	String key = dtoDE.getVariableDefaultKeys()[i];
            	org.psygrid.data.model.dto.NumericValueDTO nv = dtoDE.getVariableDefaultValues()[i];
            	AssertJUnit.assertEquals("DTO variable default value id is wrong for key "+key,de.getVariableDefaults().get(key).getId(), nv.getId());
            	AssertJUnit.assertEquals("DTO variable default value value is wrong for key "+key,de.getVariableDefaults().get(key).getValue(), nv.getValue());
            }
            
            AssertJUnit.assertEquals("DTO derived entry does not have the correct aggregate operator",aggOp,dtoDE.getAggregateOperator());
            AssertJUnit.assertEquals("DTO derived entry does not have the correct composite",ceId,dtoDE.getComposite().getId());
    }
    
    @Test
    public void testCreateStatement() {
    	QueryStatementValue queryStatement = new QueryStatementValue();
    	Double value = new Double(2.3);
    	queryStatement.setDoubleValue(value);
    	Entry derivedEntry = new DerivedEntry();
    	IEntryStatement derivedEntryStatement = derivedEntry.createStatement(queryStatement);
    	AssertJUnit.assertTrue(derivedEntryStatement instanceof NumericStatement);
    	AssertJUnit.assertEquals(value, derivedEntryStatement.getTheValue());
    }
}

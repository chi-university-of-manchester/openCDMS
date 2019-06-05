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


public class BasicEntryTest {

    @Test()
	public void testToDTO(){
            BasicEntry e = new TextEntry();
            EntryStatus status = EntryStatus.DISABLED;
            e.setEntryStatus(status);
            
            Unit unit1 = new Unit();
            Long unit1id = new Long(2);
            unit1.setId(unit1id);
            e.getUnits().add(unit1);
            Unit unit2 = new Unit();
            Long unit2id = new Long(3);
            unit2.setId(unit2id);
            e.getUnits().add(unit2);

            ValidationRule vr1 = new NumericValidationRule();
            Long vr1id = new Long(4);
            vr1.setId(vr1id);
            e.getValidationRules().add(vr1);
            ValidationRule vr2 = new NumericValidationRule();
            Long vr2id = new Long(5);
            vr2.setId(vr2id);
            e.getValidationRules().add(vr2);
            
            Long id = new Long(6);
            e.setId(id);
            
            Transformer t1 = new Transformer();
            Long t1id = new Long(7);
            t1.setId(t1id);
            e.getTransformers().add(t1);
            Transformer t2 = new Transformer();
            Long t2id = new Long(8);
            t2.setId(t2id);
            e.getTransformers().add(t2);
             
            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
            org.psygrid.data.model.dto.BasicEntryDTO dtoE = e.toDTO(dtoRefs, RetrieveDepth.DS_COMPLETE);
        
            AssertJUnit.assertNotNull("DTO entry is null", dtoE);
            AssertJUnit.assertEquals("DTO entry has the wrong status",status.toString(),dtoE.getEntryStatus());
            AssertJUnit.assertEquals("DTO entry has the wrong number of units",e.getUnits().size(),dtoE.getUnits().length);
            AssertJUnit.assertNotNull("DTO entry has null unit at index 0", dtoE.getUnits()[0]);
            AssertJUnit.assertEquals("DTO entry has the wrong unit at index 0",unit1id,dtoE.getUnits()[0].getId());
            AssertJUnit.assertNotNull("DTO entry has null unit at index 1", dtoE.getUnits()[1]);
            AssertJUnit.assertEquals("DTO entry has the wrong unit at index 1",unit2id,dtoE.getUnits()[1].getId());
            AssertJUnit.assertEquals("DTO entry has the wrong number of validation rules",e.getValidationRules().size(),dtoE.getValidationRules().length);
            AssertJUnit.assertNotNull("DTO entry has null validation rule at index 0", dtoE.getValidationRules()[0]);
            AssertJUnit.assertEquals("DTO entry has the wrong validation rule at index 0",vr1id,dtoE.getValidationRules()[0].getId());
            AssertJUnit.assertNotNull("DTO entry has null validation rule at index 1", dtoE.getValidationRules()[1]);
            AssertJUnit.assertEquals("DTO entry has the wrong validation rule at index 1",vr2id,dtoE.getValidationRules()[1].getId());
            AssertJUnit.assertEquals("DTO entry has the wrong id",id,dtoE.getId());
            AssertJUnit.assertEquals("DTO entry has the wrong number of transformers",e.getTransformers().size(),dtoE.getTransformers().length);
            AssertJUnit.assertNotNull("DTO entry has null transformer at index 0", dtoE.getTransformers()[0]);
            AssertJUnit.assertEquals("DTO entry has the wrong transformer at index 0",t1id,dtoE.getTransformers()[0].getId());
            AssertJUnit.assertNotNull("DTO entry has null transformer at index 1", dtoE.getTransformers()[1]);
            AssertJUnit.assertEquals("DTO entry has the wrong transformer at index 1",t2id,dtoE.getTransformers()[1].getId());
            
    }
}

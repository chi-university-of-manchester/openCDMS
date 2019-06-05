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

package org.psygrid.data.model.dto;

import org.testng.annotations.Test;
import org.testng.AssertJUnit;
import java.util.HashMap;
import java.util.Map;

import org.psygrid.data.model.hibernate.EntryStatus;

public class BasicEntryTest {

    @Test()
	public void testToHibernate(){
            BasicEntryDTO e = new TextEntryDTO();
            EntryStatus status = EntryStatus.DISABLED;
            e.setEntryStatus(status.toString());
            
            e.setUnits(new UnitDTO[2]);
            UnitDTO unit1 = new UnitDTO();
            Long unit1id = new Long(2);
            unit1.setId(unit1id);
            e.getUnits()[0] = unit1;
            UnitDTO unit2 = new UnitDTO();
            Long unit2id = new Long(3);
            unit2.setId(unit2id);
            e.getUnits()[1] = unit2;

            e.setValidationRules(new ValidationRuleDTO[2]);
            ValidationRuleDTO vr1 = new NumericValidationRuleDTO();
            Long vr1id = new Long(4);
            vr1.setId(vr1id);
            e.getValidationRules()[0] = vr1;
            ValidationRuleDTO vr2 = new NumericValidationRuleDTO();
            Long vr2id = new Long(5);
            vr2.setId(vr2id);
            e.getValidationRules()[1] = vr2;
            Long id = new Long(6);
            e.setId(id);
            
            e.setTransformers(new TransformerDTO[2]);
            TransformerDTO t1 = new TransformerDTO();
            Long t1id = new Long(7);
            t1.setId(t1id);
            e.getTransformers()[0] = t1;
            TransformerDTO t2 = new TransformerDTO();
            Long t2id = new Long(8);
            t2.setId(t2id);
            e.getTransformers()[1] = t2;
             
            Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
            org.psygrid.data.model.hibernate.BasicEntry hE = e.toHibernate(hRefs);
        
            AssertJUnit.assertNotNull("Hibernate entry is null", hE);
            AssertJUnit.assertEquals("Hibernate entry has the wrong status",status,hE.getEntryStatus());
            AssertJUnit.assertEquals("Hibernate entry has the wrong number of units",e.getUnits().length,hE.getUnits().size());
            AssertJUnit.assertNotNull("Hibernate entry has null unit at index 0", hE.getUnits().get(0));
            AssertJUnit.assertEquals("Hibernate entry has the wrong unit at index 0",unit1id,hE.getUnits().get(0).getId());
            AssertJUnit.assertNotNull("Hibernate entry has null unit at index 1", hE.getUnits().get(1));
            AssertJUnit.assertEquals("Hibernate entry has the wrong unit at index 1",unit2id,hE.getUnits().get(1).getId());
            AssertJUnit.assertEquals("Hibernate entry has the wrong number of validation rules",e.getValidationRules().length,hE.getValidationRules().size());
            AssertJUnit.assertNotNull("Hibernate entry has null validation rule at index 0", hE.getValidationRules().get(0));
            AssertJUnit.assertEquals("Hibernate entry has the wrong validation rule at index 0",vr1id,hE.getValidationRules().get(0).getId());
            AssertJUnit.assertNotNull("Hibernate entry has null validation rule at index 1", hE.getValidationRules().get(1));
            AssertJUnit.assertEquals("Hibernate entry has the wrong validation rule at index 1",vr2id,hE.getValidationRules().get(1).getId());
            AssertJUnit.assertEquals("Hibernate entry has the wrong id",id,hE.getId());
            AssertJUnit.assertEquals("Hibernate entry has the wrong number of transformers",e.getTransformers().length,hE.getTransformers().size());
            AssertJUnit.assertNotNull("Hibernate entry has null transformer at index 0", hE.getTransformers().get(0));
            AssertJUnit.assertEquals("Hibernate entry has the wrong transformer at index 0",t1id,hE.getTransformers().get(0).getId());
            AssertJUnit.assertNotNull("Hibernate entry has null transformer at index 1", hE.getTransformers().get(1));
            AssertJUnit.assertEquals("Hibernate entry has the wrong transformer at index 1",t2id,hE.getTransformers().get(1).getId());
    }
}

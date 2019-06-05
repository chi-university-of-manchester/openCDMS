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

public class DerivedEntryTest {

    @Test()
	@SuppressWarnings("unchecked")
    public void testToHibernate(){
            DerivedEntryDTO de = new DerivedEntryDTO();
            
            de.setUnits(new UnitDTO[1]);
            de.getUnits()[0] = new UnitDTO();
            
            String formula = "x+y";
            de.setFormula(formula);
            
            String keyX = "x";
            BasicEntryDTO varX = new NumericEntryDTO();
            Long varXId = new Long(8);
            varX.setId(varXId);
            String keyY = "y";
            BasicEntryDTO varY = new NumericEntryDTO();
            Long varYId = new Long(9);
            varY.setId(varYId);
            de.setVariables(new BasicEntryDTO[2]);
            de.setVariableKeys(new String[2]);
            de.getVariables()[0] = varX;
            de.getVariableKeys()[0] = keyX;
            de.getVariables()[1] = varY;
            de.getVariableKeys()[1] = keyY;
            
            NumericValueDTO nv1 = new NumericValueDTO();
            Long nv1id = Long.valueOf(20);
            nv1.setId(nv1id);
            nv1.setValue(new Double(30));
            NumericValueDTO nv2 = new NumericValueDTO();
            Long nv2id = Long.valueOf(21);
            nv2.setId(nv2id);
            nv2.setValue(new Double(31));
            de.setVariableDefaultKeys(new String[2]);
            de.setVariableDefaultValues(new NumericValueDTO[2]);
            de.getVariableDefaultKeys()[0] = keyX;
            de.getVariableDefaultValues()[0] = nv1;
            de.getVariableDefaultKeys()[1] = keyY;
            de.getVariableDefaultValues()[1] = nv2;
            
            String aggOp = "+";
            de.setAggregateOperator(aggOp);
            
            CompositeEntryDTO ce = new CompositeEntryDTO();
            Long ceId = new Long(10);
            ce.setId(ceId);
            de.setComposite(ce);
            
            Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
            org.psygrid.data.model.hibernate.DerivedEntry hDE = de.toHibernate(hRefs);
            
            AssertJUnit.assertNotNull("Hibernate derived entry is null", hDE);
            AssertJUnit.assertEquals("Hibernate derived entry does not have the right number of units",de.getUnits().length,hDE.getUnits().size());
            AssertJUnit.assertEquals("Hibernate derived entry does not have the correct formula",formula,hDE.getFormula());
            AssertJUnit.assertEquals("Hibernate derived entry does not have the correct number of variables",de.getVariables().length,hDE.getVariables().size());
            AssertJUnit.assertEquals("Hibernate derived entry does not have the the correct variable for key "+keyX,varXId,((org.psygrid.data.model.hibernate.BasicEntry)hDE.getVariables().get(keyX)).getId());
            AssertJUnit.assertEquals("Hibernate derived entry does not have the the correct variable for key "+keyY,varYId,((org.psygrid.data.model.hibernate.BasicEntry)hDE.getVariables().get(keyY)).getId());

            AssertJUnit.assertEquals("Hibernate derived entry does not have the correct number of variable defaults",de.getVariableDefaultKeys().length,hDE.getVariableDefaults().size());
            AssertJUnit.assertEquals("Hibernate derived entry does not have the the correct variable default for key "+keyX,nv1.getId(),((org.psygrid.data.model.hibernate.NumericValue)hDE.getVariableDefaults().get(keyX)).getId());
            AssertJUnit.assertEquals("Hibernate derived entry does not have the the correct variable default for key "+keyY,nv2.getId(),((org.psygrid.data.model.hibernate.NumericValue)hDE.getVariableDefaults().get(keyY)).getId());
            
            AssertJUnit.assertEquals("Hibernate derived entry does not have the the correct aggregate operator",aggOp,de.getAggregateOperator());
            AssertJUnit.assertEquals("Hibernate derived entry does not have the the correct composite",ceId,de.getComposite().getId());
    }
}

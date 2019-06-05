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

public class NumericValueTest {

    @Test()
	public void testToDTO(){
            NumericValue nv = new NumericValue();
            Double value = new Double(1.23);
            nv.setValue(value);
            boolean deprecated = true;
            nv.setDeprecated(deprecated);
            
            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
            org.psygrid.data.model.dto.NumericValueDTO dtoNV = nv.toDTO(dtoRefs, RetrieveDepth.RS_COMPLETE);

            AssertJUnit.assertNotNull("DTO numeric value is null", dtoNV);
            AssertJUnit.assertEquals("DTO numeric value does not have the correct deprecated flag",deprecated,dtoNV.isDeprecated());
            AssertJUnit.assertEquals("DTO numeric value does not have the correct value",value,dtoNV.getValue());
    }

    @Test()
	public void testCopy(){
            NumericValue v = new NumericValue();
            Unit u1 = new Unit("U1");
            StandardCode sc = new StandardCode("SC1", 1);
            boolean deprecated = true;
            boolean transformed = true;
            Double value = new Double(3.4);
            v.setDeprecated(deprecated);
            v.setStandardCode(sc);
            v.setTransformed(transformed);
            v.setUnit(u1);
            v.setValue(value);
            
            NumericValue copy = v.copy();
            
            AssertJUnit.assertEquals("Copied value has the wrong deprecated", deprecated, copy.isDeprecated());
            AssertJUnit.assertEquals("Copied value has the wrong standard code", sc, copy.getStandardCode());
            AssertJUnit.assertEquals("Copied value has the wrong transformed", transformed, copy.isTransformed());
            AssertJUnit.assertEquals("Copied value has the wrong unit", u1, copy.getUnit());
            AssertJUnit.assertEquals("Copied value has the wrong value", value, copy.getValue());
    }
    
    @Test()
	public void testIsNull(){
            NumericValue bv = new NumericValue();
            AssertJUnit.assertTrue("isNull should return true", bv.isNull());
            bv.setValue(new Double(3.3));
            AssertJUnit.assertFalse("isNull should return false", bv.isNull());
    }

}

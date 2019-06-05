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

public class BooleanValueTest {

    @Test()
	public void testToDTO(){
            BooleanValue bv = new BooleanValue();
            boolean value = true;
            bv.setValue(value);
            boolean deprecated = true;
            bv.setDeprecated(deprecated);
            
            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
            org.psygrid.data.model.dto.BooleanValueDTO dtoBV = bv.toDTO(dtoRefs, RetrieveDepth.RS_COMPLETE);

            AssertJUnit.assertNotNull("DTO boolean value is null", dtoBV);
            AssertJUnit.assertEquals("DTO boolean value does not have the correct deprecated flag",deprecated,dtoBV.isDeprecated());
            AssertJUnit.assertEquals("DTO boolean value does not have the correct value",value,dtoBV.getValue());
    }
    
    @Test()
	public void testCopy(){
            BooleanValue bv = new BooleanValue();
            Unit u1 = new Unit("U1");
            StandardCode sc = new StandardCode("SC1", 1);
            boolean deprecated = true;
            boolean transformed = true;
            boolean value = true;
            bv.setDeprecated(deprecated);
            bv.setStandardCode(sc);
            bv.setTransformed(transformed);
            bv.setUnit(u1);
            bv.setValue(value);
            
            BooleanValue copy = bv.copy();
            
            AssertJUnit.assertEquals("Copied value has the wrong deprecated", deprecated, copy.isDeprecated());
            AssertJUnit.assertEquals("Copied value has the wrong standard code", sc, copy.getStandardCode());
            AssertJUnit.assertEquals("Copied value has the wrong transformed", transformed, copy.isTransformed());
            AssertJUnit.assertEquals("Copied value has the wrong unit", u1, copy.getUnit());
            AssertJUnit.assertEquals("Copied value has the wrong value", value, copy.getValue());
    }
    
    @Test()
	public void testIsNull(){
            BooleanValue bv = new BooleanValue();
            bv.setValue(false);
            AssertJUnit.assertFalse("isNull should return false for boolean value false", bv.isNull());
            bv.setValue(true);
            AssertJUnit.assertFalse("isNull should return false for boolean value true", bv.isNull());
    }
}

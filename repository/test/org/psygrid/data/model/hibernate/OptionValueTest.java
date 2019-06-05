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

public class OptionValueTest {

    @Test()
	public void testToDTO(){
            OptionValue ov = new OptionValue();
            
            Option value = new Option();
            Long optionId = new Long(123);
            value.setId(optionId);
            ov.setValue(value);
            
            String textValue = "Blah";
            ov.setTextValue(textValue);
            
            boolean deprecated = true;
            ov.setDeprecated(deprecated);
            
            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
            org.psygrid.data.model.dto.OptionValueDTO dtoOV = ov.toDTO(dtoRefs, RetrieveDepth.RS_COMPLETE);

            AssertJUnit.assertNotNull("DTO boolean value is null", dtoOV);
            AssertJUnit.assertEquals("DTO boolean value does not have the correct deprecated flag",deprecated,dtoOV.isDeprecated());
            AssertJUnit.assertEquals("DTO boolean value does not have the correct value",value.getId(),dtoOV.getValueId());
            AssertJUnit.assertEquals("DTO boolean value does not have the correct text value",textValue,dtoOV.getTextValue());
    }

    @Test()
	public void testCopy(){
            OptionValue v = new OptionValue();
            Unit u1 = new Unit("U1");
            StandardCode sc = new StandardCode("SC1", 1);
            boolean deprecated = true;
            boolean transformed = true;
            Option value = new Option("O1");
            String textValue = "Foo Bar";
            v.setDeprecated(deprecated);
            v.setStandardCode(sc);
            v.setTransformed(transformed);
            v.setUnit(u1);
            v.setValue(value);
            v.setTextValue(textValue);
            
            OptionValue copy = v.copy();
            
            AssertJUnit.assertEquals("Copied value has the wrong deprecated", deprecated, copy.isDeprecated());
            AssertJUnit.assertEquals("Copied value has the wrong standard code", sc, copy.getStandardCode());
            AssertJUnit.assertEquals("Copied value has the wrong transformed", transformed, copy.isTransformed());
            AssertJUnit.assertEquals("Copied value has the wrong unit", u1, copy.getUnit());
            AssertJUnit.assertEquals("Copied value has the wrong value", value, copy.getValue());
            AssertJUnit.assertEquals("Copied value has the wrong text value", textValue, copy.getTextValue());
    }
    
    @Test()
	public void testIsNull(){
            OptionValue bv = new OptionValue();
            AssertJUnit.assertTrue("isNull should return true", bv.isNull());
            bv.setValue(new Option("foo"));
            AssertJUnit.assertFalse("isNull should return false", bv.isNull());
    }

}

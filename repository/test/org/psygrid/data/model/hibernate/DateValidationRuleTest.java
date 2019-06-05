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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class DateValidationRuleTest {

    @Test()
	public void testToDTO(){
            DateValidationRule dvr = new DateValidationRule();
            String desc = "Desc";
            dvr.setDescription(desc);
            Date lowerLimit = new Date(0);
            dvr.setAbsLowerLimit(lowerLimit);
            Date upperLimit = new Date();
            dvr.setAbsUpperLimit(upperLimit);
            Integer relLower = new Integer(4);
            dvr.setRelLowerLimit(relLower);
            TimeUnits relLowerUnits = TimeUnits.MONTHS;
            dvr.setRelLowerLimitUnits(relLowerUnits);
            Integer relUpper = new Integer(7);
            dvr.setRelUpperLimit(relUpper);
            TimeUnits relUpperUnits = TimeUnits.DAYS;
            dvr.setRelUpperLimitUnits(relUpperUnits);
            
            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
            org.psygrid.data.model.dto.DateValidationRuleDTO dtoDVR = dvr.toDTO(dtoRefs, RetrieveDepth.DS_COMPLETE);
            
            AssertJUnit.assertNotNull("DTO date validation rule is null", dtoDVR);
            AssertJUnit.assertEquals("DTO date validation rule has the wrong description",desc,dtoDVR.getDescription());
            AssertJUnit.assertEquals("DTO date validation rule has the wrong lower limit",lowerLimit,dtoDVR.getAbsLowerLimit());
            AssertJUnit.assertEquals("DTO date validation rule has the wrong upper limit",upperLimit,dtoDVR.getAbsUpperLimit());
            AssertJUnit.assertEquals("DTO date validation rule has the wrong relative lower limit",relLower,dtoDVR.getRelLowerLimit());
            AssertJUnit.assertEquals("DTO date validation rule has the wrong relative lower limit units",relLowerUnits.toString(),dtoDVR.getRelLowerLimitUnits());
            AssertJUnit.assertEquals("DTO date validation rule has the wrong relative upper limit",relUpper,dtoDVR.getRelUpperLimit());
            AssertJUnit.assertEquals("DTO date validation rule has the wrong relative upper limit units",relUpperUnits.toString(),dtoDVR.getRelUpperLimitUnits());
    }
}

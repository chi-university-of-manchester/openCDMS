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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.psygrid.data.model.hibernate.TimeUnits;

public class DateValidationRuleTest {

    @Test()
	public void testToHibernate(){
            DateValidationRuleDTO dvr = new DateValidationRuleDTO();
            String desc = "Desc";
            dvr.setDescription(desc);
            Date lowerLimit = new Date(0);
            dvr.setAbsLowerLimit(lowerLimit);
            Date upperLimit = new Date();
            dvr.setAbsUpperLimit(upperLimit);
            Integer relLower = new Integer(4);
            dvr.setRelLowerLimit(relLower);
            TimeUnits relLowerUnits = TimeUnits.MONTHS;
            dvr.setRelLowerLimitUnits(relLowerUnits.toString());
            Integer relUpper = new Integer(7);
            dvr.setRelUpperLimit(relUpper);
            TimeUnits relUpperUnits = TimeUnits.DAYS;
            dvr.setRelUpperLimitUnits(relUpperUnits.toString());
            
            Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
            org.psygrid.data.model.hibernate.DateValidationRule hDVR = dvr.toHibernate(hRefs);
            
            AssertJUnit.assertNotNull("Hibernate date validation rule is null", hDVR);
            AssertJUnit.assertEquals("Hibernate date validation rule has the wrong description",desc,hDVR.getDescription());
            AssertJUnit.assertEquals("Hibernate date validation rule has the wrong absolute lower limit",lowerLimit,hDVR.getAbsLowerLimit());
            AssertJUnit.assertEquals("Hibernate date validation rule has the wrong absolute upper limit",upperLimit,hDVR.getAbsUpperLimit());
            AssertJUnit.assertEquals("Hibernate date validation rule has the wrong relative lower limit",relLower,hDVR.getRelLowerLimit());
            AssertJUnit.assertEquals("Hibernate date validation rule has the wrong relative lower limit units",relLowerUnits,hDVR.getRelLowerLimitUnits());
            AssertJUnit.assertEquals("Hibernate date validation rule has the wrong relative upper limit",relUpper,hDVR.getRelUpperLimit());
            AssertJUnit.assertEquals("Hibernate date validation rule has the wrong relative upper limit units",relUpperUnits,hDVR.getRelUpperLimitUnits());
    }
}

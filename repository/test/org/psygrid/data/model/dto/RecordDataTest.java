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

/**
 * @author Rob Harper
 *
 */
public class RecordDataTest {

    @Test()
	public void testToHibernate(){
            RecordDataDTO rd = new RecordDataDTO();
            Date start = new Date();
            rd.setScheduleStartDate(start);
            Date entry = new Date(1234567);
            rd.setStudyEntryDate(entry);
            String comment = "Comment";
            rd.setNotes(comment);
            Long rdId = new Long(3);
            rd.setId(rdId);

            Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
            org.psygrid.data.model.hibernate.RecordData hRD = rd.toHibernate(hRefs);
        
            AssertJUnit.assertNotNull("Hibernate record data is null", hRD);
            AssertJUnit.assertEquals("Hibernate record data has the wrong schedule start date",start,hRD.getScheduleStartDate());
            AssertJUnit.assertEquals("Hibernate record data has the wrong study entry date",entry,hRD.getStudyEntryDate());
            AssertJUnit.assertEquals("Hibernate record data has the wrong notes",comment,hRD.getNotes());
            AssertJUnit.assertEquals("Hibernate record data has the wrong id",rdId,hRD.getId());
    }
}

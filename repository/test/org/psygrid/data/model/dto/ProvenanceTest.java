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

public class ProvenanceTest {

    @Test()
	public void testToHibernate(){
            ProvenanceDTO p = new ProvenanceDTO();
            String action = "Action";
            p.setAction(action);
            ValueDTO current = new TextValueDTO();
            Long currentId = new Long(2);
            current.setId(currentId);
            p.setTheCurrentValue(current);
            Long id = new Long(3);
            p.setId(id);
            ValueDTO prev = new TextValueDTO();
            Long prevId = new Long(4);
            prev.setId(prevId);
            p.setThePrevValue(prev);
            Date timestamp = new Date();
            p.setTimestamp(timestamp);
            String user = "User";
            p.setUser(user);
            String comment = "Comment";
            p.setComment(comment);
            
            Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
            org.psygrid.data.model.hibernate.Provenance hP = p.toHibernate(hRefs);
            
            AssertJUnit.assertNotNull("Hibernate provenance is null", hP);
            AssertJUnit.assertEquals("Hibernate provenance has the wrong action",action,hP.getAction());
            AssertJUnit.assertNotNull("Hibernate provenance has null current value", hP.getTheCurrentValue());
            AssertJUnit.assertEquals("Hibernate provenance has the wrong current value",currentId,hP.getTheCurrentValue().getId());
            AssertJUnit.assertEquals("Hibernate provenance has the wrong id",id,hP.getId());
            AssertJUnit.assertNotNull("Hibernate provenance has null previous value", hP.getThePrevValue());
            AssertJUnit.assertEquals("Hibernate provenance has the wrong previous value",prevId,hP.getThePrevValue().getId());
            AssertJUnit.assertEquals("Hibernate provenance has the wrong timestamp",timestamp,hP.getTimestamp());
            AssertJUnit.assertEquals("Hibernate provenance has the wrong user",user,hP.getUser());
            AssertJUnit.assertEquals("Hibernate provenance has the wrong comment",comment,hP.getComment());
    }
}

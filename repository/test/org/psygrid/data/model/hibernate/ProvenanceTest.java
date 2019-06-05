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

public class ProvenanceTest {

    @Test()
	public void testToDTO(){
            Provenance p = new Provenance();
            String action = "Action";
            p.setAction(action);
            Value current = new TextValue();
            Long currentId = new Long(2);
            current.setId(currentId);
            p.setTheCurrentValue(current);
            Long id = new Long(3);
            p.setId(id);
            Value prev = new TextValue();
            Long prevId = new Long(4);
            prev.setId(prevId);
            p.setThePrevValue(prev);
            Date timestamp = new Date();
            p.setTimestamp(timestamp);
            String user = "User";
            p.setUser(user);
            String comment = "Comment";
            p.setComment(comment);
            
            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
            org.psygrid.data.model.dto.ProvenanceDTO dtoP = p.toDTO(dtoRefs, RetrieveDepth.RS_COMPLETE);
            
            AssertJUnit.assertNotNull("DTO provenance is null", dtoP);
            AssertJUnit.assertEquals("DTO provenance has the wrong action",action,dtoP.getAction());
            AssertJUnit.assertNotNull("DTO provenance has null current value", dtoP.getTheCurrentValue());
            AssertJUnit.assertEquals("DTO provenance has the wrong current value",currentId,dtoP.getTheCurrentValue().getId());
            AssertJUnit.assertEquals("DTO provenance has the wrong id",id,dtoP.getId());
            AssertJUnit.assertNotNull("DTO provenance has null previous value", dtoP.getThePrevValue());
            AssertJUnit.assertEquals("DTO provenance has the wrong previous value",prevId,dtoP.getThePrevValue().getId());
            AssertJUnit.assertEquals("DTO provenance has the wrong timestamp",timestamp,dtoP.getTimestamp());
            AssertJUnit.assertEquals("DTO provenance has the wrong user",user,dtoP.getUser());
            AssertJUnit.assertEquals("DTO provenance has the wrong comment",comment,dtoP.getComment());
    }
}

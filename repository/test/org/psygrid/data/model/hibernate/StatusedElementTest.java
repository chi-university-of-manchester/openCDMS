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

public class StatusedElementTest {

    @Test()
	public void testToDTO(){
            StatusedElement e = new Document();
            
            DataSet ds = new DataSet();
            Long dsId = new Long(3);
            ds.setId(dsId);
            e.setMyDataSet(ds);
            
            Long id = new Long(4);
            e.setId(id);
            
            Status status1 = new Status();
            Long status1id = new Long(8);
            status1.setId(status1id);
            e.getStatuses().add(status1);
            Status status2 = new Status();
            Long status2id = new Long(9);
            status2.setId(status2id);
            e.getStatuses().add(status2);

            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
            org.psygrid.data.model.dto.StatusedElementDTO dtoE = e.toDTO(dtoRefs, RetrieveDepth.DS_COMPLETE);
            
            AssertJUnit.assertNotNull("DTO element is null", dtoE);
            AssertJUnit.assertNotNull("DTO element has null dataset", dtoE.getMyDataSet());
            AssertJUnit.assertEquals("DTO element has the wrong dataset",dsId,dtoE.getMyDataSet().getId());
            AssertJUnit.assertEquals("DTO element has the wrong id",id,dtoE.getId());
            AssertJUnit.assertEquals("DTO element has the wrong number of statuses",e.getStatuses().size(),dtoE.getStatuses().length);
            AssertJUnit.assertNotNull("DTO element has null status at index 0", dtoE.getStatuses()[0]);
            AssertJUnit.assertEquals("DTO element has the wrong status at index 0",status1id,dtoE.getStatuses()[0].getId());
            AssertJUnit.assertNotNull("DTO element has null status at index 1", dtoE.getStatuses()[1]);
            AssertJUnit.assertEquals("DTO element has the wrong status at index 1",status2id,dtoE.getStatuses()[1].getId());
    }
}

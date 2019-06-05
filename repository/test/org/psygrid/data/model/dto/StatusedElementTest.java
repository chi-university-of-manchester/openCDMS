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

public class StatusedElementTest {

    @Test()
	public void testToHibernate(){
            StatusedElementDTO e = new DocumentDTO();
            
            DataSetDTO ds = new DataSetDTO();
            Long dsId = new Long(3);
            ds.setId(dsId);
            e.setMyDataSet(ds);
            
            Long id = new Long(4);
            e.setId(id);
            
            e.setStatuses(new StatusDTO[2]);
            StatusDTO status1 = new StatusDTO();
            Long status1id = new Long(8);
            status1.setId(status1id);
            e.getStatuses()[0] = status1;
            StatusDTO status2 = new StatusDTO();
            Long status2id = new Long(9);
            status2.setId(status2id);
            e.getStatuses()[1] = status2;
            
            Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
            org.psygrid.data.model.hibernate.StatusedElement hE = e.toHibernate(hRefs);
            
            AssertJUnit.assertNotNull("Hibernate element is null", hE);
            AssertJUnit.assertNotNull("Hibernate element has null dataset", hE.getDataSet());
            AssertJUnit.assertEquals("Hibernate element has the wrong dataset",dsId,hE.getDataSet().getId());
            AssertJUnit.assertEquals("Hibernate element has the wrong id",id,hE.getId());
            AssertJUnit.assertEquals("Hibernate element has the wrong number of statuses",e.getStatuses().length,hE.getStatuses().size());
            AssertJUnit.assertNotNull("Hibernate element has null status at index 0", hE.getStatuses().get(0));
            AssertJUnit.assertEquals("Hibernate element has the wrong status at index 0",status1id,hE.getStatuses().get(0).getId());
            AssertJUnit.assertNotNull("Hibernate element has null status at index 1", hE.getStatuses().get(1));
            AssertJUnit.assertEquals("Hibernate element has the wrong status at index 1",status2id,hE.getStatuses().get(1).getId());
    }
}

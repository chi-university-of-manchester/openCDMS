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

public class BinaryDataTest {

    @Test()
	public void testToHibernate(){
            BinaryDataDTO bd = new BinaryDataDTO();
            byte[] data = new byte[10];
            data[0] = 1;
            bd.setData(data);
            Long id = new Long(2);
            bd.setId(id);
            int version = 3;
            bd.setVersion(version);
            
            Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
            org.psygrid.data.model.hibernate.BinaryData hBD = bd.toHibernate(hRefs);
            
            AssertJUnit.assertNotNull("Hibernate binary data is null", hBD);
            AssertJUnit.assertEquals("Hibernate binary data has the wrong length data",data.length,hBD.getData().length);
            AssertJUnit.assertEquals("Hibernate binary data has the wrong data",data[0],hBD.getData()[0]);
            AssertJUnit.assertEquals("Hibernate binary data has the wrong id",id,hBD.getId());
            AssertJUnit.assertEquals("Hibernate binary data has the wrong version",version,hBD.getVersion());
    }
}

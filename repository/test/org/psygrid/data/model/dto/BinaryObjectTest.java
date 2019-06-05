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

public class BinaryObjectTest {

    @Test()
	public void testToHibernate(){
            BinaryObjectDTO bo = new BinaryObjectDTO();
            bo.setData(new BinaryDataDTO());
            String desc = "Foo";
            bo.setDescription(desc);
            String file = "Bar";
            bo.setFileName(file);
            Long id = new Long(123);
            bo.setId(id);
            String mime = "application/pdf";
            bo.setMimeType(mime);
            int version = 4;
            bo.setVersion(version);
            
            Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
            org.psygrid.data.model.hibernate.BinaryObject hBO = bo.toHibernate(hRefs);
            
            AssertJUnit.assertNotNull("Hibernate binary object is null", hBO);
            AssertJUnit.assertEquals("Hibernate binary object has the wrong id",id,hBO.getId());
            AssertJUnit.assertEquals("Hibernate binary object has the wrong version",version,hBO.getVersion());
            AssertJUnit.assertEquals("Hibernate binary object has the wrong description",desc,hBO.getDescription());
            AssertJUnit.assertEquals("Hibernate binary object has the wrong filename",file,hBO.getFileName());
            AssertJUnit.assertEquals("Hibernate binary object has the wrong mime-type",mime,hBO.getMimeType());
            AssertJUnit.assertNotNull("Hibernate binary object has null binary data", hBO.getData());
    }
}

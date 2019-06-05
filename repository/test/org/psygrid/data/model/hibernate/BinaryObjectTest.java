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

public class BinaryObjectTest {

    @Test()
	public void testToDTO(){
            BinaryObject bo = new BinaryObject();
            bo.setData(new BinaryData());
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
            
            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
            org.psygrid.data.model.dto.BinaryObjectDTO dtoBO = bo.toDTO(dtoRefs, RetrieveDepth.DS_COMPLETE);
            
            AssertJUnit.assertNotNull("DTO binary object is null", dtoBO);
            AssertJUnit.assertEquals("DTO binary object has the wrong id",id,dtoBO.getId());
            AssertJUnit.assertEquals("DTO binary object has the wrong version",version,dtoBO.getVersion());
            AssertJUnit.assertEquals("DTO binary object has the wrong description",desc,dtoBO.getDescription());
            AssertJUnit.assertEquals("DTO binary object has the wrong filename",file,dtoBO.getFileName());
            AssertJUnit.assertEquals("DTO binary object has the wrong mime-type",mime,dtoBO.getMimeType());
            //currently the binary data is never retrieved, so no need for this test
            //assertNotNull("DTO binary object has null binary data", dtoBO.getData());
    }
}

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

import org.psygrid.data.export.security.ExportSecurityValues;

public class ElementTest {

    @Test()
	public void testToDTO(){
            Element e = new Document();
            
            DataSet ds = new DataSet();
            Long dsId = new Long(3);
            ds.setId(dsId);
            e.setMyDataSet(ds);
            
            Long id = new Long(4);
            e.setId(id);
            
            String name = "Name";
            e.setName(name);
            
            String exportValue = ExportSecurityValues.EXPORT_LEVEL_10.toString();
            e.setEnumExportSecurity(exportValue);
                        
            String iguid = "abcdefghijklmonpqrstuvwxyz1234567890";
            e.setIGUId(iguid);
            
            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
            org.psygrid.data.model.dto.ElementDTO dtoE = e.toDTO(dtoRefs, RetrieveDepth.DS_COMPLETE);
            
            AssertJUnit.assertNotNull("DTO element is null", dtoE);
            AssertJUnit.assertNotNull("DTO element has null dataset", dtoE.getMyDataSet());
            AssertJUnit.assertEquals("DTO element has the wrong dataset",dsId,dtoE.getMyDataSet().getId());
            AssertJUnit.assertEquals("DTO element has the wrong id",id,dtoE.getId());
            AssertJUnit.assertEquals("DTO element has the wrong name",name,dtoE.getName());
            AssertJUnit.assertEquals("Hibernate element has the wrong IGUID", iguid, dtoE.getIGUId());
            AssertJUnit.assertEquals("DTO element has the wrong security tag", e.getEnumExportSecurity(), dtoE.getExportSecurityValue());
    }
}

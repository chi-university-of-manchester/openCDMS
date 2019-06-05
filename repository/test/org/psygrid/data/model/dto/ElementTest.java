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

import org.psygrid.data.export.security.ExportSecurityValues;

public class ElementTest {

    @Test()
	public void testToHibernate(){
            ElementDTO e = new DocumentDTO();
            
            DataSetDTO ds = new DataSetDTO();
            Long dsId = new Long(3);
            ds.setId(dsId);
            e.setMyDataSet(ds);
            
            String name = "Name";
            e.setName(name);
            
            Long id = new Long(4);
            e.setId(id);
            
            e.setExportSecurityValue(ExportSecurityValues.EXPORT_LEVEL_13.toString());
            
            String iguid = "abcdefghijklmonpqrstuvwxyz1234567890";
            e.setIGUId(iguid);
            Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
            org.psygrid.data.model.hibernate.Element hE = e.toHibernate(hRefs);
            
            AssertJUnit.assertNotNull("Hibernate element is null", hE);
            AssertJUnit.assertNotNull("Hibernate element has null dataset", hE.getDataSet());
            AssertJUnit.assertEquals("Hibernate element has the wrong dataset",dsId,hE.getDataSet().getId());
            AssertJUnit.assertEquals("Hibernate element has the wrong id",id,hE.getId());
            AssertJUnit.assertEquals("Hibernate element has the wrong name",name,hE.getName());
            AssertJUnit.assertEquals("Hibernate element has the wtong IGUID", iguid, hE.getIGUId());
            AssertJUnit.assertEquals("Hibernate element has the wrong security tag", ExportSecurityValues.EXPORT_LEVEL_13.toString(), hE.getEnumExportSecurity());
    }
}

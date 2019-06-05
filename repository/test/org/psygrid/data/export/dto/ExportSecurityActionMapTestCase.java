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
package org.psygrid.data.export.dto;


import org.testng.annotations.Test;
import org.testng.AssertJUnit;
import org.psygrid.data.export.dto.ExportSecurityActionMap;
import org.psygrid.data.export.security.DataExportActions;
import org.psygrid.data.export.security.ExportSecurityValues;

public class ExportSecurityActionMapTestCase {
	
	@Test()
	public void testToHibernate(){
			Long id = Long.valueOf("1000000");
			int version = 9;
			ExportSecurityActionMap map = new ExportSecurityActionMap();
			map.setExportAction(DataExportActions.ACTION_EXPORT_RESTRICTED.toString());
			map.setSecurityTag(ExportSecurityValues.EXPORT_LEVEL_13.toString());
			map.setId(id);
			map.setVersion(version);
			
			org.psygrid.data.export.hibernate.ExportSecurityActionMap hESAM = map.toHibernate();
			
			AssertJUnit.assertEquals("The ExportSecurityActionMap's hibernate id doesn't match the dto", map.getId(), hESAM.getId());
			AssertJUnit.assertEquals("The ExportSecurityActionMap's hibernate version doesn't match the dto", map.getVersion(), hESAM.getVersion());
			AssertJUnit.assertEquals("The ExportSecurityActionMap's export action doesn't match the dto", map.getExportAction(), hESAM.getExportAction());
			AssertJUnit.assertEquals("The ExportSecurityActionMap's security tag doesn't match the dto", map.getSecurityTag(), hESAM.getSecurityTag());
	}

}

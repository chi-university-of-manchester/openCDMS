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
package org.psygrid.data.export.hibernate;

import org.testng.annotations.Test;
import org.testng.AssertJUnit;
import org.psygrid.data.export.security.DataExportActions;
import org.psygrid.data.export.security.ExportSecurityValues;

public class ExportSecurityActionMapTestCase {
	
	@Test()
	public void testToDTO(){
			ExportSecurityActionMap actionMap = new ExportSecurityActionMap();
			actionMap.setId(Long.valueOf("30"));
			actionMap.setVersion(10);
			actionMap.setExportAction(DataExportActions.ACTION_EXPORT_TRANSFORMED.toString());
			actionMap.setSecurityTag(ExportSecurityValues.EXPORT_LEVEL_12.toString());
			
			org.psygrid.data.export.dto.ExportSecurityActionMap actionMapDTO = actionMap.toDTO();
			
			AssertJUnit.assertEquals("The DTO Id is incorrect", actionMap.getId(), actionMapDTO.getId());
			AssertJUnit.assertEquals("The DTO version is incorrect", actionMap.getVersion(), actionMapDTO.getVersion());
			AssertJUnit.assertEquals("The DTO export action is incorrect", actionMap.getExportAction(), actionMapDTO.getExportAction());
			AssertJUnit.assertEquals("The DTO security tag is incorrect", actionMap.getSecurityTag(), actionMapDTO.getSecurityTag());
	}
}

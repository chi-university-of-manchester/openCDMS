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
import java.util.Date;

import org.psygrid.data.export.security.DataExportActions;
import org.psygrid.data.export.security.ExportSecurityValues;

/**
 * @author Rob Harper
 *
 */
public class ExportRequestTest {

	@Test()
	public void testToHibernate(){
			ExportRequest req = new ExportRequest();
			Date completedDate = new Date();
			req.setCompletedDate(completedDate);
			String[] groups = new String[2];
			groups[0] = "Group 1";
			groups[1] = "Group 2";
			req.setGroups(groups);
			Long id = new Long(2);
			req.setId(id);
			String path = "Path";
			req.setPath(path);
			String sha1Path = "SHA1 Path";
			req.setSha1Path(sha1Path);
			String md5Path = "MD5 Path";
			req.setMd5Path(md5Path);
			String projectCode = "Project";
			req.setProjectCode(projectCode);
			Date requestDate = new Date();
			req.setRequestDate(requestDate);
			String requestor = "Requestor";
			req.setRequestor(requestor);
			String status = "Status";
			req.setStatus(status);
			int version = 3;
			req.setVersion(version);
			
			ExportSecurityActionMap[] map = new ExportSecurityActionMap[2];
			Long testVal = Long.valueOf("1010101001");
			map[0] = new ExportSecurityActionMap();
			map[1] = new ExportSecurityActionMap();
			
			map[0].setId(testVal);
			map[0].setVersion(9);
			map[0].setSecurityTag(ExportSecurityValues.EXPORT_LEVEL_0.toString());
			map[0].setExportAction(DataExportActions.ACTION_EXPORT_RESTRICTED.toString());
			
			testVal +=1;
			map[1].setId(testVal);
			map[1].setVersion(10);
			map[1].setSecurityTag(ExportSecurityValues.EXPORT_LEVEL_14.toString());
			map[1].setExportAction(DataExportActions.ACTION_EXPORT_UNRESTRICTED.toString());
			
			req.setExportSecurityActionMaps(map);
			
			org.psygrid.data.export.hibernate.ExportRequest hReq 
				= req.toHibernate();
			
			AssertJUnit.assertNotNull("Hibernate export request is null", hReq);
			AssertJUnit.assertEquals("Hibernate export request completed date is wrong", completedDate, hReq.getCompletedDate());
			AssertJUnit.assertEquals("Hibernate export wrong number of groups", groups.length, hReq.getGroups().size());
			AssertJUnit.assertEquals("Hibernate export wrong group at pos 0", groups[0], hReq.getGroups().get(0));
			AssertJUnit.assertEquals("Hibernate export wrong group at pos 1", groups[1], hReq.getGroups().get(1));
			AssertJUnit.assertEquals("Hibernate export request id is wrong", id, hReq.getId());
			AssertJUnit.assertEquals("Hibernate export request path is wrong", path, hReq.getPath());
			AssertJUnit.assertEquals("Hibernate export request SHA1 path is wrong", sha1Path, hReq.getSha1Path());
			AssertJUnit.assertEquals("Hibernate export request MD5 path is wrong", md5Path, hReq.getMd5Path());
			AssertJUnit.assertEquals("Hibernate export request projectCode is wrong", projectCode, hReq.getProjectCode());
			AssertJUnit.assertEquals("Hibernate export request request date is wrong", requestDate, hReq.getRequestDate());
			AssertJUnit.assertEquals("Hibernate export request requestor is wrong", requestor, hReq.getRequestor());
			AssertJUnit.assertEquals("Hibernate export request status is wrong", status, hReq.getStatus());
			AssertJUnit.assertEquals("Hibernate export request version is wrong", version, hReq.getVersion());
			AssertJUnit.assertEquals("Hibernate export action map count is wrong", map.length, hReq.getActionsMap().size());
	}
	
}

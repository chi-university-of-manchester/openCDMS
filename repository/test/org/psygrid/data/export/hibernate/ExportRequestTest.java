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
import java.util.ArrayList;
import java.util.Date;

import java.util.List;

/**
 * @author Rob Harper
 *
 */
public class ExportRequestTest {

	@Test()
	public void testToDTO(){
			ExportRequest req = new ExportRequest();
			Date completedDate = new Date();
			req.setCompletedDate(completedDate);
			String group1 = "Group 1";
			String group2 = "Group 2";
			req.getGroups().add(group1);
			req.getGroups().add(group2);
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
			
			List<ExportSecurityActionMap> actionMap = new ArrayList<ExportSecurityActionMap>();
			actionMap.add(new ExportSecurityActionMap());
			actionMap.add(new ExportSecurityActionMap());
			
			org.psygrid.data.export.dto.ExportRequest dtoReq 
				= req.toDTO();
			
			AssertJUnit.assertNotNull("DTO export request is null", dtoReq);
			AssertJUnit.assertEquals("DTO export request completed date is wrong", completedDate, dtoReq.getCompletedDate());
			AssertJUnit.assertEquals("DTO export wrong number of groups", 2, dtoReq.getGroups().length);
			AssertJUnit.assertEquals("DTO export wrong group at pos 0", group1, dtoReq.getGroups()[0]);
			AssertJUnit.assertEquals("DTO export wrong group at pos 1", group2, dtoReq.getGroups()[1]);
			AssertJUnit.assertEquals("DTO export request id is wrong", id, dtoReq.getId());
			AssertJUnit.assertEquals("DTO export request path is wrong", path, dtoReq.getPath());
			AssertJUnit.assertEquals("DTO export request SHA-1 path is wrong", sha1Path, dtoReq.getSha1Path());
			AssertJUnit.assertEquals("DTO export request MD5 path is wrong", md5Path, dtoReq.getMd5Path());
			AssertJUnit.assertEquals("DTO export request projectCode is wrong", projectCode, dtoReq.getProjectCode());
			AssertJUnit.assertEquals("DTO export request request date is wrong", requestDate, dtoReq.getRequestDate());
			AssertJUnit.assertEquals("DTO export request requestor is wrong", requestor, dtoReq.getRequestor());
			AssertJUnit.assertEquals("DTO export request status is wrong", status, dtoReq.getStatus());
			AssertJUnit.assertEquals("DTO export request version is wrong", version, dtoReq.getVersion());
			AssertJUnit.assertEquals("DTO export request actionmap count is wrong", req.getActionsMap().size(), dtoReq.getExportSecurityActionMaps().length);
	}
}

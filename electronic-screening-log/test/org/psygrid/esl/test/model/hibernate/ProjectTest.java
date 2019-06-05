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

package org.psygrid.esl.test.model.hibernate;

import org.psygrid.esl.model.hibernate.*;
import org.psygrid.esl.model.IPersistent;

import junit.framework.TestCase;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Lucy Bridges
 *
 */
public class ProjectTest extends TestCase {

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link org.psygrid.esl.model.hibernate.Project}.
	 * Tests creation of new projects.
	 */
	public void testNewProject() {
		fail("This test needs to be fixed as Subjects are not now sent as part of a Group DTO");

		try {
			Project proj = new Project();
			String code = "Code";
            proj.setProjectCode(code);
            proj.setVersion(1);
            proj.setProjectName("Test 1");
            
            proj.setId(new Long("1"));
            
            Group grp1 = new Group();
            grp1.setGroupCode("123");
            grp1.setGroupName("Test 1");
            
            proj.setGroup(grp1);
            
            Subject s1 = new Subject();
            s1.setLastName("Test 1");
            
            grp1.setSubject(s1);
            
            Map<IPersistent, org.psygrid.esl.model.dto.Persistent> dtoRefs = new HashMap<IPersistent, org.psygrid.esl.model.dto.Persistent>();
            org.psygrid.esl.model.dto.Project dtoDS = proj.toDTO(dtoRefs);
            
            assertNotNull("DTO project is null", dtoDS);
            assertNotNull("Project has no ID", proj.getId());
            assertNotNull("Project has no Groups", proj.getGroups());
            
		}
		catch(Exception ex){
            ex.printStackTrace();
            fail("Exception:"+ex.toString());
        }
	}

}

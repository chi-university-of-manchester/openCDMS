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
package org.psygrid.collection.entry.persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.psygrid.collection.entry.AbstractEntryTestCase;
import org.psygrid.www.xml.security.core.types.ProjectType;

@SuppressWarnings("nls")
public class PersistenceDataTest extends AbstractEntryTestCase {

    
    private PersistenceData data;

    @Override
    protected void setUp() {
        data = new PersistenceData();
    }
    
    public void testUpdateProjects() {
        List<ProjectType> projects = new ArrayList<ProjectType>();
        projects.add(new ProjectType("ProjectA", "A", null, null, false));
        projects.add(new ProjectType("ProjectB", "B", null, null, false));
        projects.add(new ProjectType("ProjectC", "C", null, null, false));
        projects.add(new ProjectType("ProjectD", "D", null, null, false));
        data.updateProjects(projects);
        assertEquals(projects.size(), data.getProjects().size());
        for (DatedProjectType project : data.getProjects()) {
            assertEquals(data.getVeryOldDate(), project.getLastModified());
        }
        
        // Change lastModified
        String projectId = "B";
        Date newDate = new Date();
        data.getProject(projectId).setLastModified(newDate);
        data.updateProjects(projects);
        assertEquals(projects.size(), data.getProjects().size());
        for (int i = 0, c = data.getProjects().size(); i < c; ++i) {
            DatedProjectType project = data.getProjects().get(i);
            if (project.getIdCode().equals(projectId)) {
                assertEquals(newDate, project.getLastModified());
                continue;
            }
            assertEquals(data.getVeryOldDate(), project.getLastModified());
        }
        
        // Add new project
        projects.add(new ProjectType("ProjectE", "E", null, null, false));
        data.updateProjects(projects);
        
        assertEquals(projects.size(), data.getProjects().size());
        for (int i = 0, c = data.getProjects().size(); i < c; ++i) {
            DatedProjectType project = data.getProjects().get(i);
            if (project.getIdCode().equals(projectId)) {
                assertEquals(newDate, project.getLastModified());
                continue;
            }
            assertEquals(data.getVeryOldDate(), project.getLastModified());
        }
        
        // Remove two projects
        projects.remove(4);
        projects.remove(0);
        data.updateProjects(projects);
        assertEquals(projects.size(), data.getProjects().size());
        for (int i = 0, c = data.getProjects().size(); i < c; ++i) {
            DatedProjectType project = data.getProjects().get(i);
            if (project.getIdCode().equals(projectId)) {
                assertEquals(newDate, project.getLastModified());
                continue;
            }
            assertEquals(data.getVeryOldDate(), project.getLastModified());
        }
    }
}

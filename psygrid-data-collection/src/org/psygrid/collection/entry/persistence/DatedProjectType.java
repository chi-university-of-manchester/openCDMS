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

import java.util.Date;

import org.psygrid.www.xml.security.core.types.ProjectType;

public class DatedProjectType {
    private ProjectType project;
    private Date lastModified;
    
    /**
     * Default constructor required by XStream when using pure java mode.
     */
    public DatedProjectType() {
        // Empty constructor
    }

    public DatedProjectType(ProjectType project, Date lastModified) {
        super();
        this.project = project;
        this.lastModified = lastModified;
    }
    public final Date getLastModified() {
        return lastModified;
    }
    public final ProjectType getProject() {
        return project;
    }
    public final String getName() {
        return project.getName();
    }
    
    public final String getIdCode() {
        return project.getIdCode();
    }
    
    public final String getAliasName() {
        return project.getAliasName();
    }
    
    public final String getAliasId() {
        return project.getAliasId();
    }
    
    public final void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }
    
    public final boolean isVirtual() {
        return project.isVirtual();
    }
}

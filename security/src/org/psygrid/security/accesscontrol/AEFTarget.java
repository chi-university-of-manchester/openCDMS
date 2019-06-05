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


// Created on Dec 8, 2006 by John Ainsworth


package org.psygrid.security.accesscontrol;

import org.psygrid.www.xml.security.core.types.TargetType;

public class AEFTarget  implements java.io.Serializable {
    private java.lang.String name;

    private java.lang.String idCode;

    public AEFTarget() {
    }

    public AEFTarget(
           java.lang.String name,
           java.lang.String idCode) {
           this.name = name;
           this.idCode = idCode;
    }

    public AEFTarget(TargetType tt) {
		name = tt.getName();
		idCode = tt.getIdCode();
	}

    /**
     * Gets the name value for this TargetType.
     * 
     * @return name
     */
    public java.lang.String getName() {
        return name;
    }


    /**
     * Sets the name value for this TargetType.
     * 
     * @param name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }


    /**
     * Gets the idCode value for this TargetType.
     * 
     * @return idCode
     */
    public java.lang.String getIdCode() {
        return idCode;
    }


    /**
     * Sets the idCode value for this TargetType.
     * 
     * @param idCode
     */
    public void setIdCode(java.lang.String idCode) {
        this.idCode = idCode;
    }

}

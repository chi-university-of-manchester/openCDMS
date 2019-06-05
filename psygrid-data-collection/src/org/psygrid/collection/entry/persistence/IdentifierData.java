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

import org.psygrid.data.model.hibernate.Identifier;

public class IdentifierData implements Comparable<IdentifierData> {
    private Identifier identifier;
    private boolean used;
    private long dataSetId;
    
    public IdentifierData(Identifier identifier, long dataSetId) {
        this.identifier = identifier;
        this.dataSetId = dataSetId;
    }
    
    public final Identifier getIdentifier() {
        return identifier;
    }
    
    public final boolean isUsed() {
        return used;
    }
    
    public final void setUsed(boolean b) {
        this.used = b;
    }

    public final long getDataSetId() {
        return dataSetId;
    }
    
    public final String getGroupPrefix() {
        return identifier.getGroupPrefix();
    }
    
    public final String getProjectPrefix() {
        return identifier.getProjectPrefix();
    }
    
    public int compareTo(IdentifierData identifierData) {
    	if (this == identifierData) {
    		return 0;
    	}
    	return this.getIdentifier().compareTo(identifierData.getIdentifier());
    }
}

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.psygrid.data.model.hibernate.Identifier;
import org.psygrid.www.xml.security.core.types.GroupType;

public class IdentifiersList {
    private List<IdentifierData> identifiers;
    
    public IdentifiersList() {
        identifiers = new ArrayList<IdentifierData>();
    }
    
    public void sort() {
    	java.util.Collections.sort(identifiers);
    }
    
    /**
     * Finds an unused identifier and returns it.
     * @return an unused identifier.
     */
    public IdentifierData getUnused(long dataSetId, String group) {
        for (IdentifierData identifier : identifiers) {
            if (isUnused(identifier, dataSetId, group)) {
                return identifier;
            }
        }
        return null;
    }
    
    public void setLastUsed(Identifier identifier, long dataSetId) {
        int index = indexOf(identifier.getIdentifier());
        IdentifierData idData;
        if (index >= 0) {
            idData = identifiers.remove(index);
        }
        else {
            idData = new IdentifierData(identifier, dataSetId);
            idData.setUsed(true);
        }
        identifiers.add(0, idData);
    }
    
    private boolean isUnused(IdentifierData identifier, long dataSetId,
            String groupPrefix) {
        return (!identifier.isUsed()) && (identifier.getDataSetId() == dataSetId) && 
                identifier.getGroupPrefix().equals(groupPrefix);
    }
    
    public IdentifierData get(Identifier identifier) {
        return get(identifier.getIdentifier());
    }
    
    public int indexOf(String identifier) {
        int index = 0;
        for (IdentifierData idData : identifiers) {
            if (idData.getIdentifier().getIdentifier().equals(identifier)) {
                return index;
            }
            ++index;
        }
        return -1;
    }
    
    public IdentifierData get(String identifier) {
        int index = indexOf(identifier);
        if (index < 0) {
            return null;
        }
        return identifiers.get(index);
    }
    
    public int getNumUnused(long dataSetId, String group) {
        int numUnused = 0;
        for (IdentifierData identifier : identifiers) {
            if (isUnused(identifier, dataSetId, group)) {
                ++numUnused;
            }
        }
        return numUnused;
    }
    
    public void add(IdentifierData identifier) {
        identifiers.add(identifier);
    }
    
    public List<String> getGroups(String projectPrefix) {
        List<String> groups = new ArrayList<String>();
        for (IdentifierData identifier : identifiers) {
            if (projectPrefix.equals(identifier.getProjectPrefix())
                    && (!groups.contains(identifier.getGroupPrefix()))) {
                
                groups.add(identifier.getGroupPrefix());
            }
        }
        return groups;
    }

    public int size() {
        return identifiers.size();
    }
    
    /**
     * Remove any identifiers that are for a group no longer
     * possessed by the user.
     * 
     * @param project The project
     * @param groups The list of the users groups
     */
    public void synchronizeWithGroups(DatedProjectType project, List<GroupType> groups){
    	String projectCode = project.getIdCode();
    	Set<String> groupCodes = new HashSet<String>();
    	for ( GroupType g: groups ){
    		groupCodes.add(g.getIdCode());
    	}
    	List<IdentifierData> toDelete = new ArrayList<IdentifierData>();
    	for ( IdentifierData identifier: identifiers ){
    		if ( identifier.getProjectPrefix().equals(projectCode) ){
    			if ( !groupCodes.contains(identifier.getGroupPrefix()) ){
    				toDelete.add(identifier);
    			}
    		}
    	}
    	
    	for ( IdentifierData identifier: toDelete ){
    		identifiers.remove(identifier);
    	}
    }
    
}

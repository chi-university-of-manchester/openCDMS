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


package org.psygrid.data.reporting.definition.dto;

import java.util.List;
import java.util.Map;

import org.psygrid.data.model.dto.GroupDTO;
import org.psygrid.data.model.dto.PersistentDTO;
import org.psygrid.data.model.hibernate.Group;

/**
 * @author Rob Harper
 *
 */
public class CollectionDateChart extends ManagementChart {

	private GroupDTO[] groups = new GroupDTO[0];
	
	private String[] collDateEntryKeys = new String[0];
    
	private Integer[] collDateEntryValues = new Integer[0];
    
    public GroupDTO[] getGroups() {
        return groups;
    }

    public void setGroups(GroupDTO[] groups) {
        this.groups = groups;
    }

	public String[] getCollDateEntryKeys() {
		return collDateEntryKeys;
	}

	public void setCollDateEntryKeys(String[] collDateEntryKeys) {
		this.collDateEntryKeys = collDateEntryKeys;
	}

	public Integer[] getCollDateEntryValues() {
		return collDateEntryValues;
	}

	public void setCollDateEntryValues(Integer[] collDateEntryValues) {
		this.collDateEntryValues = collDateEntryValues;
	}

	@Override
    public org.psygrid.data.reporting.definition.hibernate.CollectionDateChart toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs) {
        //check for an already existing instance of a hibernate object for this 
        //collection date chart in the map of references
        org.psygrid.data.reporting.definition.hibernate.CollectionDateChart hCDC = null;
        if ( hRefs.containsKey(this)){
            hCDC = (org.psygrid.data.reporting.definition.hibernate.CollectionDateChart)hRefs.get(this);
        }
        else{
            //an instance of the chart has not already
            //been created, so create it, and add it to the
            //map of references
            hCDC = new org.psygrid.data.reporting.definition.hibernate.CollectionDateChart();
            hRefs.put(this, hCDC);
            toHibernate(hCDC, hRefs);
        }
        
        return hCDC;
    }

    public void toHibernate(org.psygrid.data.reporting.definition.hibernate.CollectionDateChart hCDC, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs) {
        super.toHibernate(hCDC, hRefs);
 
        List<Group> hGroups = hCDC.getGroups();
        for (GroupDTO g: groups){
            if ( null != g ){
                hGroups.add(g.toHibernate(hRefs));
            }
        }
        
        Map<String, Integer> hCollDateEntries = hCDC.getCollectionDateEntries();
        for ( int i=0; i<this.collDateEntryKeys.length; i++){
        	String key = this.collDateEntryKeys[i];
        	Integer value = this.collDateEntryValues[i];
            if ( null != key && null != value ){
                hCollDateEntries.put(key, value);
            }
        }
        
    }

}

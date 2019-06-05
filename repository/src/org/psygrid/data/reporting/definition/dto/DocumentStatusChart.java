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


public class DocumentStatusChart extends ManagementChart {

	private GroupDTO[] groups = new GroupDTO[0];
    
    public GroupDTO[] getGroups() {
        return groups;
    }

    public void setGroups(GroupDTO[] groups) {
        this.groups = groups;
    }

	@Override
    public org.psygrid.data.reporting.definition.hibernate.DocumentStatusChart toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs) {
        //check for an already existing instance of a hibernate object for this 
        //project summary chart in the map of references
        org.psygrid.data.reporting.definition.hibernate.DocumentStatusChart hPSC = null;
        if ( hRefs.containsKey(this)){
            hPSC = (org.psygrid.data.reporting.definition.hibernate.DocumentStatusChart)hRefs.get(this);
        }
        else{
            //an instance of the chart has not already
            //been created, so create it, and add it to the
            //map of references
            hPSC = new org.psygrid.data.reporting.definition.hibernate.DocumentStatusChart();
            hRefs.put(this, hPSC);
            toHibernate(hPSC, hRefs);
        }
        
        return hPSC;
    }

    public void toHibernate(org.psygrid.data.reporting.definition.hibernate.DocumentStatusChart hC, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs) {
        super.toHibernate(hC, hRefs);
 
        List<Group> hGroups = hC.getGroups();
        for (GroupDTO g: groups){
            if ( null != g ){
                hGroups.add(g.toHibernate(hRefs));
            }
        }
    }

}

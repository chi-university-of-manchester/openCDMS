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

package org.psygrid.data.model.dto;

import java.util.List;
import java.util.Map;

public class GroupDTO extends PersistentDTO {

    private String name;
    
    private String longName;
    
    private int maxSuffix;

    /**
     * Array of sites associated with the group.
     */
    private SiteDTO[] sites = new SiteDTO[0];
    
    private String[] theSecondaryGroups = new String[0];
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLongName() {
		return longName;
	}

	public void setLongName(String longName) {
		this.longName = longName;
	}
    
    public int getMaxSuffix() {
        return maxSuffix;
    }

    public void setMaxSuffix(int maxIdentifier) {
        this.maxSuffix = maxIdentifier;
    }
    
    public SiteDTO[] getSites() {
        return sites;
    }

    public void setSites(SiteDTO[] sl) {
        this.sites = sl;
    }

    public String[] getTheSecondaryGroups() {
		return theSecondaryGroups;
	}

	public void setTheSecondaryGroups(String[] theSecondaryGroups) {
		this.theSecondaryGroups = theSecondaryGroups;
	}

	public org.psygrid.data.model.hibernate.Group toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        org.psygrid.data.model.hibernate.Group hG = new org.psygrid.data.model.hibernate.Group();
        toHibernate(hG, hRefs);
        return hG;
    }
    
    public void toHibernate(org.psygrid.data.model.hibernate.Group hG, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hG, hRefs);
        hG.setName(this.name);
        hG.setLongName(longName);
        hG.setMaxSuffix(this.maxSuffix);
        
        List<org.psygrid.data.model.hibernate.Site> hSites = 
            hG.getSites();
        for (int i=0; i<this.sites.length; i++){
            SiteDTO s = sites[i];
            if ( null != s ){
                hSites.add(s.toHibernate(hRefs));
            }
        }
        
        List<String> hSecGrps = hG.getTheSecondaryGroups();
        for ( int i=0, c=theSecondaryGroups.length; i<c; i++ ){
        	hSecGrps.add(theSecondaryGroups[i]);
        }
    }
    
}

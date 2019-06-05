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

package org.psygrid.edie.patches.v1_1_6;

import java.util.ArrayList;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.data.model.hibernate.Site;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch4 extends AbstractPatch {

    public boolean isolated(){
   	 return true;
   }

    public String getName() {
        return "Add sites to the groups";
    }


    public void applyPatch(DataSet ds, String saml) throws Exception {
        int numGroups = ds.numGroups();

        for (int i=0; i<numGroups; i++){
        		Group g = (Group)ds.getGroup(i);
        		if(g.getName().equals("001001")){
        			ArrayList<Site> sl = new ArrayList<Site>();
        			sl.add(new Site("Faculty of Medical and Human Sciences", "N0000673", "M13 9PL", g));
        			g.setSites(sl);
        		}
        		if(g.getName().equals("002001")){
        			ArrayList<Site> sl = new ArrayList<Site>();
        			sl.add(new Site("School of Health Sciences", "N0000674", "B15 2TT", g));
        			g.setSites(sl);
        		}
        		if(g.getName().equals("003001")){
        			ArrayList<Site> sl = new ArrayList<Site>();
        			sl.add(new Site("School of Clinical Medicine", "N0000675", "CB2 2QQ", g));
        			g.setSites(sl);
        		}
        		if(g.getName().equals("004001")){
        			ArrayList<Site> sl = new ArrayList<Site>();
        			sl.add(new Site("University of East Anglia", "N0000676", "NR4 7TJ", g));
        			g.setSites(sl);
        		}
        		if(g.getName().equals("005001")){
        			ArrayList<Site> sl = new ArrayList<Site>();
        			sl.add(new Site("Gartnavel Royal Hospital", "N0000677", "G12 0XH", g));
        			g.setSites(sl);
        		}
        }
    }
}

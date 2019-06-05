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

package org.psygrid.neden.patches.v1_1_6;

import java.util.ArrayList;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.data.model.hibernate.Site;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch6 extends AbstractPatch {

    public String getName() {
        return "Add sites to the groups";
    }

//    	Heart of Birminigham - West EIS
//    	001001
//    	Heart of Birminigham - East EIS
//    	002001
//    	East PCT Brimingham
//    	003001
//    	Lancashire 001-400
//    	004001
//    	Lancashire 401-800
//    	004002
//    	Norfolk
//    	005001
//    	Cambridge CAMEO
//    	006001
//    	Cornwall 001-500
//    	007001
//    	Cornwall 501-1000
//    	007002
//    	Brimingham South
//    	008001


    public void applyPatch(DataSet ds, String saml) throws Exception {

        int numGroups = ds.numGroups();

        for (int i=0; i<numGroups; i++){
        		Group g = (Group)ds.getGroup(i);
        		if(g.getName().equals("001001")){
        			ArrayList<Site> sl = new ArrayList<Site>();
        			sl.add(new Site("Birmingham EIS", "N0000678", "B6 4NF", g));
        			g.setSites(sl);
        		}
        		if(g.getName().equals("002001")){
        			ArrayList<Site> sl = new ArrayList<Site>();
        			sl.add(new Site("Birmingham EIS", "N0000678", "B6 4NF", g));
        			g.setSites(sl);
        		}
        		if(g.getName().equals("003001")){
        			ArrayList<Site> sl = new ArrayList<Site>();
        			sl.add(new Site("Birmingham EIS", "N0000678", "B6 4NF", g));
        			g.setSites(sl);
        		}
        		if(g.getName().equals("004001")){
        			ArrayList<Site> sl = new ArrayList<Site>();
        			sl.add(new Site("Preston EIS", "N0000679", "PR1 7LY", g));
        			g.setSites(sl);
        		}
        		if(g.getName().equals("004002")){
        			ArrayList<Site> sl = new ArrayList<Site>();
        			sl.add(new Site("Blackburn EIS", "N0000680", "BB6 7DD", g));
        			g.setSites(sl);
        		}
        		if(g.getName().equals("005001")){
        			ArrayList<Site> sl = new ArrayList<Site>();
        			sl.add(new Site("East Anglia Norfolk EIS", "N0000682", "NR1 3RE", g));
        			g.setSites(sl);
        		}
        		if(g.getName().equals("006001")){
        			ArrayList<Site> sl = new ArrayList<Site>();
        			sl.add(new Site("CAMEO Cambridge", "N0000683", "CB1 5EE", g));
        			g.setSites(sl);
        		}
        		if(g.getName().equals("007001")){
        			ArrayList<Site> sl = new ArrayList<Site>();
        			sl.add(new Site("Truro EIS", "N0000684", "TR15 2SP", g));
        			g.setSites(sl);
        		}
        		if(g.getName().equals("007002")){
        			ArrayList<Site> sl = new ArrayList<Site>();
        			sl.add(new Site("Plymouth EIS", "N0000685", "PL31 2QT", g));
        			g.setSites(sl);
        		}
        		if(g.getName().equals("008001")){
        			ArrayList<Site> sl = new ArrayList<Site>();
        			sl.add(new Site("Birmingham EIS", "N0000678", "B6 4NF", g));
        			g.setSites(sl);
        		}
        }
    }

}

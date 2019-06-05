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

package org.psygrid.outlook.patches.v1_1_12;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.data.model.hibernate.Site;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch42 extends AbstractPatch {


    public String getName() {
    	return "Add group 006003";
    }

    public void applyPatch(DataSet ds, String saml) throws Exception {
    	org.psygrid.data.model.hibernate.Factory dsfactory =
    		new org.psygrid.data.model.hibernate.HibernateFactory();

    	//Add Group
    	Group grp = (Group)dsfactory.createGroup("006003");
        grp.addSite(new Site("Blackpool and Morecambe EIS", "N0000681", "BL1 0AA", grp));
        grp.addSite(new Site("Preston EIS", "N0000679", "PR1 7LY", grp));
        grp.addSite(new Site("Blackburn EIS", "N0000680", "BB6 7DD", grp));

    	ds.addGroup(grp);

    }

}

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

package org.psygrid.outlook.patches.v0_9_7;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch9 extends AbstractPatch {

    public String getName() {
        return "Add units for Drug Check  sleeping tablets/sedatives question 'How much do you usually have at each time of the day'";
    }

    public void applyPatch(DataSet ds, String saml) throws Exception {

        Factory factory = new HibernateFactory();
        Unit uTabs = factory.createUnit("tabs");
        ds.addUnit(uTabs);

        Document doc = ds.getDocument(8);
        if ( !"Drug Check".equals(doc.getName())){
            throw new RuntimeException("This is not the Drug Check form - it is "+doc.getName());
        }

        BasicEntry e1 = (BasicEntry)doc.getEntry(5);
        if ( !"Amount per week".equals(e1.getName())){
            throw new RuntimeException("This is not the 'How much do you normally have...' question - it is "+doc.getDisplayText());
        }

        //Find the mg unit
        //Although the units are held by the dataset in a list I'm
        //not sure we can guarantee the order of this, due to the way
        //that the units are created in the Units class (they are first
        //added to a Map, which is then iterated through to add them to
        //the dataset's list)
        Unit uMg = null;
        for ( int i=0; i<ds.numUnits(); i++ ){
            Unit u = ds.getUnit(i);
            if ( "mg".equals(u.getAbbreviation()) ){
                uMg = u;
                break;
            }
        }
        if ( null == uMg ){
            throw new RuntimeException("Cannot find the 'mg' unit in the dataset");
        }

        e1.addUnit(uMg);
        e1.addUnit(uTabs);

    }

}

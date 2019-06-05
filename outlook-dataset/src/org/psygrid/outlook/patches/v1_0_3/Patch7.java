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

package org.psygrid.outlook.patches.v1_0_3;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch7 extends AbstractPatch {

    public String getName() {
        return "Change text for 'WHO screening schedule' 'Satisifes criteria' question";
    }

    public void applyPatch(DataSet ds, String saml) throws Exception {

        Document who = ds.getDocument(3);
        if ( !"WHO Screening Schedule".equals(who.getName()) ){
            throw new RuntimeException("This is not the WHO Screening Schedule document, it is "+who.getName());
        }

        Entry e = who.getEntry(19);
        if ( !"Satisfies criteria".equals(e.getName()) ){
            throw new RuntimeException("This is not the Satisifies criteria document, it is "+e.getName());
        }

        e.setDisplayText("Patient satisfies entry criteria - 0 = NO, 1 = YES");
    }

}

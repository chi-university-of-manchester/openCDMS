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

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.data.model.hibernate.Section;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch8 extends AbstractPatch {

    public String getName() {
        return "Set the correct section for the 'Additional Info' textbox in Pathways to Care";
    }

    public void applyPatch(DataSet ds, String saml) throws Exception {

        Document ptc = ds.getDocument(9);
        if ( !"PathwaysToCare".equals(ptc.getName())){
            throw new RuntimeException("This is not the Pathways To Care form - it is "+ptc.getName());
        }

        Section sec = ptc.getSection(0);
        if ( !"General".equals(sec.getDisplayText())){
            throw new RuntimeException("This is not the General section - it is "+ptc.getDisplayText());
        }

        Entry e1 = ptc.getEntry(2);
        if ( !"Additional Info".equals(e1.getName())){
            throw new RuntimeException("This is not the Additional Info entry - it is "+e1.getName());
        }
        e1.setSection(sec);

    }

}

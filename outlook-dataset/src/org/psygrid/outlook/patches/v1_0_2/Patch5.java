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

package org.psygrid.outlook.patches.v1_0_2;

import org.psygrid.data.model.hibernate.BasicEntry;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.DerivedEntry;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch5 extends AbstractPatch {

    public String getName() {
        return "Correct the PANSS general score calculation";
    }

    public void applyPatch(DataSet ds, String saml) throws Exception {
        Document panss = ds.getDocument(6);
        if ( !"PANSS".equals(panss.getName()) ){
            throw new RuntimeException("This is not the PANSS document, it is "+panss.getName());
        }
        DerivedEntry generalScore = (DerivedEntry)panss.getEntry(34);
        if ( !"General subtotal".equals(generalScore.getName()) ){
            throw new RuntimeException("This is not the General subtotal entry, it is "+generalScore.getName());
        }
        generalScore.setFormula("sc+a+gf+t+mp+de+mr+u+utc+di+pa+lj+dv+pic+p+asa");
        generalScore.removeVariable("d");
        BasicEntry depression = (BasicEntry)panss.getEntry(23);
        if ( !"Depression".equals(depression.getName()) ){
            throw new RuntimeException("This is not the Depression entry, it is "+depression.getName());
        }
        BasicEntry disorientation = (BasicEntry)panss.getEntry(27);
        if ( !"Disorientation".equals(disorientation.getName()) ){
            throw new RuntimeException("This is not the Disorientation entry, it is "+disorientation.getName());
        }
        generalScore.addVariable("de", depression);
        generalScore.addVariable("di", disorientation);
    }

}

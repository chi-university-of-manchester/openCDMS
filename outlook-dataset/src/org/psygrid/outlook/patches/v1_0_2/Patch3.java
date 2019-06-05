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

import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch3 extends AbstractPatch {

    public String getName() {
        return "Add 'Incoherent; communication impossible' option to Young Mania: Language - Thought Disorder";
    }

    public void applyPatch(DataSet ds, String saml) throws Exception {
        Factory factory = new HibernateFactory();
        Document doc = ds.getDocument(7);
        //check this is Young Mania
        if ( !"Young Mania".equals(doc.getName()) ){
            throw new RuntimeException("This is not the Young Mania document, it is "+doc.getName());
        }
        OptionEntry e = (OptionEntry)doc.getEntry(7);
        //check this is the Language - Thought Disorder entry
        if ( !"Language - Thought Disorder".equals(e.getDisplayText())){
            throw new RuntimeException("This is not the Language - Thought Disorder entry, it is "+e.getDisplayText());
        }
        Option o = factory.createOption("Incoherent; communication impossible", 4);
        e.addOption(o);
    }

}

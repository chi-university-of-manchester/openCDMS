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

package org.psygrid.outlook.patches.v0_9_10;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch12 extends AbstractPatch {

    public String getName() {
        return "Add questions b2 and b3 to Pathways to care, Pathway section";
    }

    public void applyPatch(DataSet ds, String saml) throws Exception {

        Factory factory = new HibernateFactory();

        //get the Pathways to care doc
        Document pathways = ds.getDocument(9);
        if ( !"PathwaysToCare".equals(pathways.getName())) {
            throw new RuntimeException("This is not the Pathways To Care document - it is "+pathways.getName());
        }

        Section sec = pathways.getSection(1);
        if ( !"Pathway".equals(sec.getDisplayText())){
            throw new RuntimeException("This is not the Pathway section - it is "+sec.getDisplayText());
        }

        Entry e = pathways.getEntry(5);
        if ( !"Who suggested care".equals(e.getName())){
            throw new RuntimeException("This is not the Who suggested care entry - it is "+e.getName());
        }

        OptionEntry wasAttended = factory.createOptionEntry("Was the appointment attended", "Was the appointment attended?");
        pathways.insertEntry(wasAttended, 5);
        wasAttended.setSection(sec);
        wasAttended.setLabel("b2");
        wasAttended.addOption(factory.createOption("No", 0));
        wasAttended.addOption(factory.createOption("Yes", 1));

        OptionEntry clientInvOption = factory.createOptionEntry("Client involved ",
                "Was the client involved in this pathway?");
        pathways.insertEntry(clientInvOption, 6);
        clientInvOption.setSection(sec);
        clientInvOption.setLabel("b3");
        clientInvOption.addOption(factory.createOption("No", 0));
        clientInvOption.addOption(factory.createOption("Yes", 1));

    }

}

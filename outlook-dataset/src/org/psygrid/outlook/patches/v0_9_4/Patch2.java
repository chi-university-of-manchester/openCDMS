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

package org.psygrid.outlook.patches.v0_9_4;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.patches.AbstractPatch;

/**
 * Patch to add an "Other" row to the Hobbies list in the Time
 * Use Interview form, and a text box below "If 'Other' was
 * selected in the previous table, please specify" .
 *
 * @author Rob Harper
 *
 */
public class Patch2 extends AbstractPatch {

    public String getName() {
        return "Add 'Other' row to Hobbies list of Time Use Interview";
    }

    public void applyPatch(DataSet ds, String saml) throws Exception {
        Document doc = ds.getDocument(20);
        //check this the correct document
        if ( !"Time Use Interview".equals(doc.getName()) ){
            throw new RuntimeException("This is not the Time Use Interview - it is "+doc.getName());
        }
        Section hobbiesSec = doc.getSection(4);
        //check this is the correct section
        if ( !"Hobbies section".equals(hobbiesSec.getName())){
            throw new RuntimeException("This is not the Hobbies section - it is "+hobbiesSec.getName());
        }
        CompositeEntry hobbiesList = (CompositeEntry)doc.getEntry(121);
        //check this is the correct entry
        if ( !"Hobbies list".equals(hobbiesList.getName())){
            throw new RuntimeException("This is not the Hobbies list entry - it is "+hobbiesList.getName());
        }

        hobbiesList.addRowLabel("Other");

        Factory factory = new HibernateFactory();

        TextEntry hobbyOther = factory.createTextEntry(
                "Hobby Other",
                "If 'Other' was selected in the previous table, please specify",
                EntryStatus.OPTIONAL);
        doc.insertEntry(hobbyOther, 122);
        hobbyOther.setSection(hobbiesSec);

    }

}

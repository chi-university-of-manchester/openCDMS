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

package org.psygrid.outlook.patches.v0_9_9;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch10 extends AbstractPatch {

    public String getName() {
        return "Add Yes/No question to Accomodation Details section of CSSRI - if Yes is selected, rest of questions are enabled";
    }

    public void applyPatch(DataSet ds, String saml) throws Exception {

        Factory factory = new HibernateFactory();

        Document doc = ds.getDocument(19);
        if ( !"CSSRI".equals(doc.getName())){
            throw new RuntimeException("This is not the CSSRI form - it is "+doc.getName());
        }

        Section hospitalSec = doc.getSection(5);
        if ( !"Hospital or Community Accomodation Details section".equals(hospitalSec.getName())){
            throw new RuntimeException("This is not the Hospital or Community Accomodation Details section - it is "+hospitalSec.getName());
        }

        //set the occurrence to MANDATORY (was DISABLED)
        SectionOccurrence hospitalSecOcc = hospitalSec.getOccurrence(0);
        hospitalSecOcc.setEntryStatus(EntryStatus.MANDATORY);

        //remove option dependent on the section:
        //1. find the "What kind of accomodation" question
        OptionEntry kindOfAccom = (OptionEntry)doc.getEntry(1);
        if ( !"Kind of accomodation".equals(kindOfAccom.getName())){
            throw new RuntimeException("This is not the Kind of accomodation entry - it is "+kindOfAccom.getName());
        }

        //2. for each of the community/hospital options, remove the option dependent
        for ( int i=3; i<10; i++ ){
            Option o = kindOfAccom.getOption(i);
            o.removeOptionDependent(0);
        }

        //add new Yes/No question
        OptionEntry enterDetails = factory.createOptionEntry("Enter details",
                "Do you want to enter hospital or community accomodation details?");
        doc.insertEntry(enterDetails, 45);
        enterDetails.setSection(hospitalSec);
        Option enterDetailsYes = factory.createOption("Yes");
        enterDetails.addOption(enterDetailsYes);
        enterDetails.addOption(factory.createOption("No"));

        //make other entries in this section disabled by default,
        //with an option dependent to enable them if Yes is selected
        //in the above question

        for ( int i=46; i<53; i++ ){
            Entry e = doc.getEntry(i);
            e.setEntryStatus(EntryStatus.DISABLED);
            OptionDependent optDep = factory.createOptionDependent();
            optDep.setEntryStatus(EntryStatus.MANDATORY);
            enterDetailsYes.addOptionDependent(optDep);
            optDep.setDependentEntry(e);
        }

    }

}

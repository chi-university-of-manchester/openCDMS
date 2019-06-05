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


package org.psygrid.command;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.AssessmentForm;

/**
 * @author Rob Harper
 *
 */
public class RiskRating extends AssessmentForm {

    public static Document createDocument(Factory factory) {

        Document doc = factory.createDocument(
                "Risk Rating",
                "Risk Rating (screening tool)");

        createDocumentStatuses(factory, doc);

        Section mainSec = factory.createSection("Main Section", "Main");
        doc.addSection(mainSec);
        SectionOccurrence mainSecOcc = factory.createSectionOccurrence("Main Sec Occ");
        mainSec.addOccurrence(mainSecOcc);

        OptionEntry q1 = factory.createOptionEntry("Experiencing voices",
                "Has client been experiencing voices commanding him/her to act? (for at least 6 months prior to the study)");
        doc.addEntry(q1);
        q1.setSection(mainSec);
        q1.setLabel("1");
        createOptions(factory, q1, new String[]{"Yes", "No"});
        Option q1Yes = q1.getOption(0);

        LongTextEntry q1a = factory.createLongTextEntry("Examples", "Provide examples");
        q1a.setEntryStatus(EntryStatus.DISABLED);
        q1a.setSection(mainSec);
        doc.addEntry(q1a);
        createOptionDependent(factory, q1Yes, q1a);

        OptionEntry q2 = factory.createOptionEntry("History of harmful",
                "History of harmful compliance/risk on acting on voices with potential harm to self or other?");
		doc.addEntry(q2);
		q2.setSection(mainSec);
		q2.setLabel("2");
		createOptions(factory, q2, new String[]{"Yes", "No"});
		Option q2Yes = q2.getOption(0);

        LongTextEntry q2a = factory.createLongTextEntry("Examples", "Provide examples");
        q2a.setEntryStatus(EntryStatus.DISABLED);
        q2a.setSection(mainSec);
        doc.addEntry(q2a);
        createOptionDependent(factory, q2Yes, q2a);

        OptionEntry q3 = factory.createOptionEntry("Level of risk", "Level of risk");
		doc.addEntry(q3);
		q3.setSection(mainSec);
		q3.setLabel("3");
		createOptions(factory, q3, new String[]{"Level 1: Extreme", "Level 2: Severe", "Level 3: Moderate"});

        return doc;
    }
}

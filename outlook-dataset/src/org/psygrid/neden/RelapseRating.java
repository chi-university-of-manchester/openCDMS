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

package org.psygrid.neden;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.AssessmentForm;

public class RelapseRating extends AssessmentForm {

    public static Document createDocument(Factory factory) {

        Document doc = factory.createDocument(
                "Relapse Rating",
                "Relapse Rating Data Entry Form");

        createDocumentStatuses(factory, doc);

        doc.setLongRunning(true);

        Section mainSec = factory.createSection("Main", "Main");
        doc.addSection(mainSec);
        mainSec.setDisplayText("Episode");
        SectionOccurrence mainOcc = factory.createSectionOccurrence("Main section occurrence");
        mainSec.addOccurrence(mainOcc);
        mainOcc.setMultipleAllowed(true);

        OptionEntry type = factory.createOptionEntry("Type of relapse", "Type of relapse");
        doc.addEntry(type);
        type.setSection(mainSec);
        type.setLabel("1");
        type.addOption(factory.createOption("Type 1", 1));
        type.addOption(factory.createOption("Type 2", 2));

        DateEntry start = factory.createDateEntry("Start date", "Start date of relapse");
        doc.addEntry(start);
        start.setSection(mainSec);
        start.setLabel("2");

        DateEntry end = factory.createDateEntry("End date", "End date of relapse");
        doc.addEntry(end);
        end.setSection(mainSec);
        end.setLabel("3");

        OptionEntry admitted = factory.createOptionEntry("Client admitted", "Was the client admitted");
        doc.addEntry(admitted);
        admitted.setSection(mainSec);
        admitted.setLabel("4a");
        admitted.addOption(factory.createOption("No", 0));
        Option admittedYes = factory.createOption("Yes", 1);
        admitted.addOption(admittedYes);

        NarrativeEntry ifAdmitted = factory.createNarrativeEntry("If admitted narrative", "If client was admitted:");
        doc.addEntry(ifAdmitted);
        ifAdmitted.setSection(mainSec);

        TextEntry whereAdmitted = factory.createTextEntry(
                "Where admitted",
                "To which facility/ hospital was client admitted?",
                EntryStatus.DISABLED);
        doc.addEntry(whereAdmitted);
        whereAdmitted.setSection(mainSec);
        whereAdmitted.setLabel("4b");
        createOptionDependent(factory, admittedYes, whereAdmitted);

        DateEntry whenAdmitted = factory.createDateEntry(
                "When admitted",
                "When was the client admitted",
                EntryStatus.DISABLED);
        doc.addEntry(whenAdmitted);
        whenAdmitted.setSection(mainSec);
        whenAdmitted.setLabel("4c");
        createOptionDependent(factory, admittedYes, whenAdmitted);

        OptionEntry committed = factory.createOptionEntry("Committed", "Was the client committed under the Mental Health Act");
        doc.addEntry(committed);
        committed.setSection(mainSec);
        committed.setLabel("5");
        committed.addOption(factory.createOption("No", 0));
        committed.addOption(factory.createOption("Yes", 1));

        return doc;
    }
}

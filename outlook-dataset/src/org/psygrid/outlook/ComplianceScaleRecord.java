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
package org.psygrid.outlook;

import org.psygrid.data.model.hibernate.*;

public class ComplianceScaleRecord extends AssessmentForm{

    public static Document createDocument(Factory factory) {

        Document doc = factory.createDocument("Seven-Point Compliance Scale " +
                "Record", "Seven-Point Compliance Scale Record");

        createDocumentStatuses(factory, doc);

        Section mainSec = factory.createSection("Main Section", "Main");
        doc.addSection(mainSec);
        mainSec.setDisplayText("Main");
        SectionOccurrence mainOcc = factory.createSectionOccurrence("Main section occurrence");
        mainSec.addOccurrence(mainOcc);

        OptionEntry complianceOption = factory.createOptionEntry(
                "Client compliance",
                "Record level of client compliance");
        doc.addEntry(complianceOption);
        complianceOption.setSection(mainSec);
        complianceOption.addOption(factory.createOption("Complete refusal", 1));
        complianceOption.addOption(factory.createOption("Partial refusal " +
                "(e.g. refused depot) or accepts only minimal dose", 2));
        complianceOption.addOption(factory.createOption("Accepts only because " +
                "compulsory, or very reluctant/requires persuasion, or " +
                "questions need often (e.g. once every two days)", 3));
        complianceOption.addOption(factory.createOption("Occasional reluctance " +
                "(e.g. questions need once a week)", 4));
        complianceOption.addOption(factory.createOption("Passive acceptance", 5));
        complianceOption.addOption(factory.createOption("Moderate participation, " +
                "some knowledge and interest in medication and no prompting  " +
                "required", 6));
        complianceOption.addOption(factory.createOption("Active participation, " +
                "readily accepts, and shows some responsibility for regimen", 7));

        OptionEntry sourceOption = factory.createOptionEntry("Source of " +
                "information", "Person/source of information");
        doc.addEntry(sourceOption);
        sourceOption.setSection(mainSec);
        sourceOption.addOption(factory.createOption("Clinical case notes", 1));
        sourceOption.addOption(factory.createOption("Member of clinical " +
                "team/Keyworker", 2));
        sourceOption.addOption(factory.createOption("Relative/partner/friend",
                3));
        Option otherOption = factory.createOption("Other (please specify)", 4);
        sourceOption.addOption(otherOption);
        otherOption.setTextEntryAllowed(true);

        OptionEntry lowestComplOption = factory.createOptionEntry(
                "Compliance calculated",
                "Was the lowest compliance rating calculated?");
        doc.addEntry(lowestComplOption);
        lowestComplOption.setSection(mainSec);
        lowestComplOption.addOption(factory.createOption("No", 0));
        lowestComplOption.addOption(factory.createOption("Yes", 1));

        return doc;
    }

}

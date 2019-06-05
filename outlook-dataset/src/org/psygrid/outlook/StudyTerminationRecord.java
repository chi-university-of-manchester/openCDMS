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

public class StudyTerminationRecord extends AssessmentForm {

    public static Document createDocument(Factory factory) {

        Document doc = factory.createDocument("Study Termination Record",
                "Study Termination Record");

        createDocumentStatuses(factory, doc);

        Section mainSec = factory.createSection("Main Section");
        doc.addSection(mainSec);
        mainSec.setDisplayText("Main");

        SectionOccurrence mainSecOcc = factory.createSectionOccurrence("Main " +
                "Section Occurrence");
        mainSec.addOccurrence(mainSecOcc);

        NarrativeEntry complete = factory.createNarrativeEntry("Completed " +
                "Assessments", "Did the patient complete the following " +
                "assessments?");
        doc.addEntry(complete);
        complete.setSection(mainSec);

        OptionEntry baseline = factory.createOptionEntry("Baseline", "Baseline");
        doc.addEntry(baseline);
        baseline.setSection(mainSec);
        Option baselineNo = factory.createOption("No", "No", 0);
        baseline.addOption(baselineNo);
        Option baselineYes = factory.createOption("Yes", "Yes", 1);
        baseline.addOption(baselineYes);

        OptionEntry core = factory.createOptionEntry("Section A", "Core " +
                "assessments (Section A)");
        doc.addEntry(core);
        core.setSection(mainSec);
        Option coreNo = factory.createOption("No", "No", 0);
        core.addOption(coreNo);
        Option coreYes = factory.createOption("Yes", "Yes", 1);
        core.addOption(coreYes);

        OptionEntry sectionB = factory.createOptionEntry("Section B",
                "Section B");
        doc.addEntry(sectionB);
        sectionB.setSection(mainSec);
        Option sectionBNo = factory.createOption("No", "No", 0);
        sectionB.addOption(sectionBNo);
        Option sectionBYes = factory.createOption("Yes", "Yes", 1);
        sectionB.addOption(sectionBYes);

        OptionEntry sectionC = factory.createOptionEntry("Section C",
                "Section C");
        doc.addEntry(sectionC);
        sectionC.setSection(mainSec);
        Option sectionCNo = factory.createOption("No", "No", 0);
        sectionC.addOption(sectionCNo);
        Option sectionCYes = factory.createOption("Yes", "Yes", 1);
        sectionC.addOption(sectionCYes);

        OptionEntry sixMonths = factory.createOptionEntry("6 Months",
                "6 Months");
        doc.addEntry(sixMonths);
        sixMonths.setSection(mainSec);
        Option sixMonthsNo = factory.createOption("No", "No", 0);
        sixMonths.addOption(sixMonthsNo);
        Option sixMonthsYes = factory.createOption("Yes", "Yes", 1);
        sixMonths.addOption(sixMonthsYes);

        OptionEntry twelveMonths = factory.createOptionEntry("12 months",
                "12 months");
        doc.addEntry(twelveMonths);
        twelveMonths.setSection(mainSec);
        Option twelveMonthsNo = factory.createOption("No", "No", 0);
        twelveMonths.addOption(twelveMonthsNo);
        Option twelveMonthsYes = factory.createOption("Yes", "Yes", 1);
        twelveMonths.addOption(twelveMonthsYes);

        OptionEntry primaryReason = factory.createOptionEntry(
                "Primary reason",
                "If not, list the primary reason(s)");
        doc.addEntry(primaryReason);
        primaryReason.setSection(mainSec);
        primaryReason.addOption(factory.createOption("ineffective", "ineffective", 1));
        primaryReason.addOption(factory.createOption("intolerable", "intolerable", 2));
        Option failedReturn = factory.createOption("patient failed to return",
                "patient failed to return (specify reason)", 3);
        failedReturn.setTextEntryAllowed(true);
        primaryReason.addOption(failedReturn);
        Option withdrew = factory.createOption("patient withdrew consent", "patient withdrew " +
                "consent (specify reason)", 4);
        withdrew.setTextEntryAllowed(true);
        primaryReason.addOption(withdrew);
        primaryReason.addOption(factory.createOption("non-compliance", "non-compliance", 5));
        Option death = factory.createOption("death", "death (specify primary cause)", 6);
        death.setTextEntryAllowed(true);
        primaryReason.addOption(death);
        Option other = factory.createOption("other", "other (specify reason)", 7);
        other.setTextEntryAllowed(true);
        primaryReason.addOption(other);

        LongTextEntry otherInfo = factory.createLongTextEntry("Other Info", "Any other information");
        doc.addEntry(otherInfo);
        otherInfo.setSection(mainSec);

        return doc;
    }
}

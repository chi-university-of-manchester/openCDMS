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

import org.psygrid.common.ValidationRulesWrapper;
import org.psygrid.data.model.hibernate.*;

public class InsightScale extends AssessmentForm {

    public static Document createDocument(Factory factory)    {

        Document doc = factory.createDocument("Insight Scale Scoring", "Insight Scale");

        createDocumentStatuses(factory, doc);

        Section mainSec = factory.createSection("Main Section", "Main");
        doc.addSection(mainSec);
        mainSec.setDisplayText("Main");
        SectionOccurrence mainSecOcc = factory.createSectionOccurrence(
                "Main Section Occurrence");
        mainSec.addOccurrence(mainSecOcc);

        String agree = "Agree";
        String disagree = "Disagree";
        String unsure = "Unsure";

        OptionEntry symptomsOption = factory.createOptionEntry("Symptoms were " +
                "made by my mind optione entry", "Some of the symptoms were " +
                "made by my mind");
        doc.addEntry(symptomsOption);
        symptomsOption.setSection(mainSec);
        symptomsOption.setLabel("1");
        symptomsOption.addOption(factory.createOption(agree, 2));
        symptomsOption.addOption(factory.createOption(disagree, 0));
        symptomsOption.addOption(factory.createOption(unsure, 1));

        OptionEntry mentallyWellOption = factory.createOptionEntry("Mentally well " +
                "option", "I am mentally well");
        doc.addEntry(mentallyWellOption);
        mentallyWellOption.setSection(mainSec);
        mentallyWellOption.setLabel("2");
        mentallyWellOption.addOption(factory.createOption(agree, 0));
        mentallyWellOption.addOption(factory.createOption(disagree, 2));
        mentallyWellOption.addOption(factory.createOption(unsure, 1));

        OptionEntry needMedOption = factory.createOptionEntry("Do not " +
                "need medication option", "I do not need medication");
        doc.addEntry(needMedOption);
        needMedOption.setSection(mainSec);
        needMedOption.setLabel("3");
        needMedOption.addOption(factory.createOption(agree, 0));
        needMedOption.addOption(factory.createOption(disagree, 2));
        needMedOption.addOption(factory.createOption(unsure, 1));

        OptionEntry stayInHospOption = factory.createOptionEntry("Stay in " +
                "hospital necessary option", "My stay in hospital was necessary");
        doc.addEntry(stayInHospOption);
        stayInHospOption.setSection(mainSec);
        stayInHospOption.setLabel("4");
        stayInHospOption.addOption(factory.createOption(agree, 2));
        stayInHospOption.addOption(factory.createOption(disagree, 0));
        stayInHospOption.addOption(factory.createOption(unsure, 1));

        OptionEntry prescribeMedOption = factory.createOptionEntry("Prescribing " +
                "medication option", "The doctor is right in prescribing " +
                "medication for me");
        doc.addEntry(prescribeMedOption);
        prescribeMedOption.setSection(mainSec);
        prescribeMedOption.setLabel("5");
        prescribeMedOption.addOption(factory.createOption(agree, 2));
        prescribeMedOption.addOption(factory.createOption(disagree, 0));
        prescribeMedOption.addOption(factory.createOption(unsure, 1));

        OptionEntry seenByDoctorOption = factory.createOptionEntry("Do not need " +
                "to be seen by doctor option", "I do not need to be seen by a " +
                "doctor or psychiatrist");
        doc.addEntry(seenByDoctorOption);
        seenByDoctorOption.setSection(mainSec);
        seenByDoctorOption.setLabel("6");
        seenByDoctorOption.addOption(factory.createOption(agree, 0));
        seenByDoctorOption.addOption(factory.createOption(disagree, 2));
        seenByDoctorOption.addOption(factory.createOption(unsure, 1));

        OptionEntry mentalIllnessOption = factory.createOptionEntry("Nervous " +
                "or mental illness option", "If someone said I had a nervous or " +
                "mental illness then they would be right");
        doc.addEntry(mentalIllnessOption);
        mentalIllnessOption.setSection(mainSec);
        mentalIllnessOption.setLabel("7");
        mentalIllnessOption.addOption(factory.createOption(agree, 2));
        mentalIllnessOption.addOption(factory.createOption(disagree, 0));
        mentalIllnessOption.addOption(factory.createOption(unsure, 1));

        OptionEntry unusualThingsOption = factory.createOptionEntry("Not due to illness",
                "None of the unusual things I experienced are due to an illness");
        doc.addEntry(unusualThingsOption);
        unusualThingsOption.setSection(mainSec);
        unusualThingsOption.setLabel("8");
        unusualThingsOption.addOption(factory.createOption(agree, 0));
        unusualThingsOption.addOption(factory.createOption(disagree, 2));
        unusualThingsOption.addOption(factory.createOption(unsure, 1));

        NarrativeEntry subscalesNarrative = factory.createNarrativeEntry(
                "Subscales", "Subscales - 3 or 4 = Good insight, 1 or 2 = poor " +
                "insight. Possible total = 4.");
        doc.addEntry(subscalesNarrative);
        subscalesNarrative.setSection(mainSec);

        DerivedEntry awareSympDerived = factory.createDerivedEntry(
                "Awareness of symptoms derived entry", "Awareness of symptoms");
        doc.addEntry(awareSympDerived);
        awareSympDerived.setSection(mainSec);
        awareSympDerived.setDescription("Item 1 + Item 8");
        awareSympDerived.addVariable("a", symptomsOption);
        awareSympDerived.addVariable("b", unusualThingsOption);
        awareSympDerived.setFormula("a + b");

        DerivedEntry awareIllnDerived = factory.createDerivedEntry(
                "Awareness of illness derived entry", "Awareness of illness");
        doc.addEntry(awareIllnDerived);
        awareIllnDerived.setSection(mainSec);
        awareIllnDerived.setDescription("Item 2 + Item 7");
        awareIllnDerived.addVariable("a", mentallyWellOption);
        awareIllnDerived.addVariable("b", mentalIllnessOption);
        awareIllnDerived.setFormula("a + b");

        DerivedEntry needForTreatDerived = factory.createDerivedEntry(
                "Need for treatment derived entry", "Need for treatment");
        doc.addEntry(needForTreatDerived);
        needForTreatDerived.setSection(mainSec);
        needForTreatDerived.setDescription(
                "(Item 3 + Item 4 + Item 5 + Item 6) / 2");
        needForTreatDerived.addVariable("a", needMedOption);
        needForTreatDerived.addVariable("b", stayInHospOption);
        needForTreatDerived.addVariable("c", prescribeMedOption);
        needForTreatDerived.addVariable("d", seenByDoctorOption);
        needForTreatDerived.setFormula("(a + b + c + d)/ 2");

        NarrativeEntry totalScoreExplanation = factory.createNarrativeEntry(""
                + "Total Score Explanation");
        doc.addEntry(totalScoreExplanation);
        totalScoreExplanation.setSection(mainSec);
        totalScoreExplanation
                .setDisplayText("Maximum Score = 12 - Full insight, "
                        + "Minimum Score = 0 - No insight, "
                        + "(9 and above = good insight).");

        DerivedEntry totalScore = factory.createDerivedEntry(
                "Total", "Total");
        doc.addEntry(totalScore);
        totalScore.setSection(mainSec);
        totalScore.addVariable("a", awareSympDerived);
        totalScore.addVariable("b", awareIllnDerived);
        totalScore.addVariable("c", needForTreatDerived);
        totalScore.setFormula("a + b + c");

        ValidationRule notInFuture = ValidationRulesWrapper.instance().getRule("Not in future");
        ValidationRule after2000 = ValidationRulesWrapper.instance().getRule("After 2000");
        DateEntry dateOfAssessment = factory.createDateEntry("Date of assesssment",
                "Date of assessment");
        doc.addEntry(dateOfAssessment);
        dateOfAssessment.setSection(mainSec);
        dateOfAssessment.addValidationRule(after2000);
        dateOfAssessment.addValidationRule(notInFuture);

        return doc;
    }
}

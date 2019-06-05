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

public class DrugSideEffects extends AssessmentForm {

    public static Document createDocument(Factory factory) {

        Document doc = factory.createDocument("Side Effect Record ", "Side Effect Record");

        createDocumentStatuses(factory, doc);

        ValidationRule epseValidationRule = ValidationRulesWrapper.instance().getRule("ZeroToFour");

        //EPSE section
        Section epseSec = factory.createSection("EPSE section");
        doc.addSection(epseSec);
        epseSec.setDisplayText("EPSE");
        SectionOccurrence epseSecOcc = factory
                .createSectionOccurrence("EPSE section occurrence");
        epseSec.addOccurrence(epseSecOcc);

        String description = "Please enter 0,1,2,3 or 4 as defined in the rating manual.";
        NarrativeEntry epseNarrative = factory.createNarrativeEntry("EPSE narrative",
                "Simpson-Angus Scale for Extrapyramidal Side-Effects (EPSE)");
        doc.addEntry(epseNarrative);
        epseNarrative.setSection(epseSec);
        epseNarrative.setLabel("1");
        epseNarrative.setStyle(NarrativeStyle.HEADER);

        NarrativeEntry epseInstructions = factory.createNarrativeEntry("Instructions",
                "Instructions: Complete examination procedure before making the " +
                "ratings. Insert codes 0,1,2,3 or 4 as defined in the rating " +
                "manual (In study tools).");
        doc.addEntry(epseInstructions);
        epseInstructions.setSection(epseSec);

        NumericEntry gait = factory.createNumericEntry("Gait", "Gait");
        doc.addEntry(gait);
        gait.setSection(epseSec);
        gait.setLabel("1");
        gait.addValidationRule(epseValidationRule);
        gait.setDescription(description);

        NumericEntry armDrop = factory.createNumericEntry("Arm dropping", "Arm dropping");
        doc.addEntry(armDrop);
        armDrop.setSection(epseSec);
        armDrop.setLabel("2");
        armDrop.addValidationRule(epseValidationRule);
        armDrop.setDescription(description);

        NumericEntry shoulder = factory.createNumericEntry("Shoulder shaking", "Shoulder shaking");
        doc.addEntry(shoulder);
        shoulder.setSection(epseSec);
        shoulder.setLabel("3");
        shoulder.addValidationRule(epseValidationRule);
        shoulder.setDescription(description);

        NumericEntry elbow = factory.createNumericEntry("Elbow rigidity", "Elbow rigidity");
        doc.addEntry(elbow);
        elbow.setSection(epseSec);
        elbow.setLabel("4");
        elbow.addValidationRule(epseValidationRule);
        elbow.setDescription(description);

        NumericEntry wrist = factory.createNumericEntry("Fixation of position or wrist rigidity", "Fixation of position or wrist rigidity");
        doc.addEntry(wrist);
        wrist.setSection(epseSec);
        wrist.setLabel("5");
        wrist.addValidationRule(epseValidationRule);
        wrist.setDescription(description);

        NumericEntry leg = factory.createNumericEntry("Leg pendulousness", "Leg pendulousness");
        doc.addEntry(leg);
        leg.setSection(epseSec);
        leg.setLabel("6");
        leg.addValidationRule(epseValidationRule);
        leg.setDescription(description);

        NumericEntry head = factory.createNumericEntry("Head dropping", "Head dropping");
        doc.addEntry(head);
        head.setSection(epseSec);
        head.setLabel("7");
        head.addValidationRule(epseValidationRule);
        head.setDescription(description);

        NumericEntry galbellar = factory.createNumericEntry("Galbellar tap", "Galbellar tap");
        doc.addEntry(galbellar);
        galbellar.setSection(epseSec);
        galbellar.setLabel("8");
        galbellar.addValidationRule(epseValidationRule);
        galbellar.setDescription(description);

        NumericEntry tremor = factory.createNumericEntry("Tremor", "Tremor");
        doc.addEntry(tremor);
        tremor.setSection(epseSec);
        tremor.setLabel("9");
        tremor.addValidationRule(epseValidationRule);
        tremor.setDescription(description);

        NumericEntry salivation = factory.createNumericEntry("Salivation", "Salivation");
        doc.addEntry(salivation);
        salivation.setSection(epseSec);
        salivation.setLabel("10");
        salivation.addValidationRule(epseValidationRule);
        salivation.setDescription(description);

        DerivedEntry total = factory.createDerivedEntry("Total", "Total");
        doc.addEntry(total);
        total.setSection(epseSec);
        total.setDescription("Sum of items 1 to 10");
        total.addVariable("a", gait);
        total.addVariable("b", armDrop);
        total.addVariable("c", shoulder);
        total.addVariable("d", elbow);
        total.addVariable("e", wrist);
        total.addVariable("f", leg);
        total.addVariable("g", head);
        total.addVariable("h", galbellar);
        total.addVariable("i", tremor);
        total.addVariable("j", salivation);
        total.setFormula("a + b + c + d + e + f + g + h + i + j");


        //BARS section
        Section barsSec = factory.createSection("BARS section");
        doc.addSection(barsSec);
        barsSec.setDisplayText("BARS");
        SectionOccurrence barsSecOcc = factory
                .createSectionOccurrence("BARS section occurrence");
        barsSec.addOccurrence(barsSecOcc);

        NarrativeEntry barsNarrative = factory.createNarrativeEntry("BARS narrative",
                "Barnes Akathisia Rating Scale (BARS)");
        doc.addEntry(barsNarrative);
        barsNarrative.setStyle(NarrativeStyle.HEADER);
        barsNarrative.setSection(barsSec);
        barsNarrative.setLabel("2");

        NarrativeEntry barsInstructions = factory.createNarrativeEntry("BARS instructions",
                "Instructions: Complete examination procedure before making " +
                "ratings. For questions 1-3, insert codes 0,1,2 or 3 as defined " +
                "in rating manual. For question 4, insert code 0,1,2,3,4 or 5 " +
                "as defined in rating manual.");
        doc.addEntry(barsInstructions);
        barsInstructions.setSection(barsSec);

        String description2 = "Please insert 0,1,2 or 3 as defined in the rating manual.";
        ValidationRule zeroToThree = ValidationRulesWrapper.instance().getRule("ZeroToThree");

        NumericEntry objective = factory.createNumericEntry("Objective", "Objective");
        doc.addEntry(objective);
        objective.setSection(barsSec);
        objective.setLabel("1");
        objective.addValidationRule(zeroToThree);
        objective.setDescription(description2);

        NumericEntry subjective1 = factory.createNumericEntry("Subjective - " +
                "awareness of restlessness", "Subjective - awareness of " +
                "restlessness");
        doc.addEntry(subjective1);
        subjective1.setSection(barsSec);
        subjective1.setLabel("2");
        subjective1.addValidationRule(zeroToThree);
        subjective1.setDescription(description2);

        NumericEntry subjective2 = factory.createNumericEntry(
                "Subjective - distress related to restlessness",
                "Subjective - distress related to restlessness");
        doc.addEntry(subjective2);
        subjective2.setSection(barsSec);
        subjective2.setLabel("3");
        subjective2.addValidationRule(zeroToThree);
        subjective2.setDescription(description2);

        String globalDescription = "Please insert 0,1,2,3,4 or 5 as defined in the rating manual.";
        ValidationRule globalRule = ValidationRulesWrapper.instance().getRule("ZeroToFive");
        NumericEntry global = factory.createNumericEntry(
                "Global Clinical Assessment", "Global Clinical Assessment");
        doc.addEntry(global);
        global.setSection(barsSec);
        global.setLabel("4");
        global.addValidationRule(globalRule);
        global.setDescription(globalDescription);

        return doc;
    }
}

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

public class Calgary extends AssessmentForm {

    public static Document createDocument(Factory factory){

        ValidationRule calgaryRule = ValidationRulesWrapper.instance().getRule("Calgary validation rule");

        Document calgary = factory.createDocument("Calgary", "Calgary Depression Scale Record");

        createDocumentStatuses(factory, calgary);

        Section main = factory.createSection("Main", "Main");
        calgary.addSection(main);
        main.setDisplayText("Main");
        SectionOccurrence mainOcc = factory.createSectionOccurrence("Main section occurrence");
        main.addOccurrence(mainOcc);

        NarrativeEntry intro = factory.createNarrativeEntry("Introduction");
        calgary.addEntry(intro);
        intro.setSection(main);
        intro.setDisplayText("Instructions: enter appropriate code for each item - refer to rating manual in study "+
                             "tools section of document depository. 0 = absent, 1= mild, 2 = moderate, 3 = severe");

        String helpText = "Enter one of the following 4 numbers: 0 = absent, 1 = mild, 2 = moderate, 3 = severe.";
        NumericEntry depression = factory.createNumericEntry("Depression", "Depression");
        calgary.addEntry(depression);
        depression.setSection(main);
        depression.addValidationRule(calgaryRule);
        depression.setDescription(helpText);
        depression.setLabel("1");

        NumericEntry hopelessness = factory.createNumericEntry("Hopelessness", "Hopelessness");
        calgary.addEntry(hopelessness);
        hopelessness.setSection(main);
        hopelessness.addValidationRule(calgaryRule);
        hopelessness.setDescription(helpText);
        hopelessness.setLabel("2");

        NumericEntry selfDepreciation = factory.createNumericEntry("Self depreciation", "Self depreciation");
        calgary.addEntry(selfDepreciation);
        selfDepreciation.setSection(main);
        selfDepreciation.addValidationRule(calgaryRule);
        selfDepreciation.setDescription(helpText);
        selfDepreciation.setLabel("3");

        NumericEntry guiltIdeas = factory.createNumericEntry("Guilt ideas of reference", "Guilt ideas of reference");
        calgary.addEntry(guiltIdeas);
        guiltIdeas.setSection(main);
        guiltIdeas.addValidationRule(calgaryRule);
        guiltIdeas.setDescription(helpText);
        guiltIdeas.setLabel("4");

        NumericEntry pathologicalGuilt = factory.createNumericEntry("Pathological guilt", "Pathological guilt");
        calgary.addEntry(pathologicalGuilt);
        pathologicalGuilt.setSection(main);
        pathologicalGuilt.addValidationRule(calgaryRule);
        pathologicalGuilt.setDescription(helpText);
        pathologicalGuilt.setLabel("5");

        NumericEntry morningDepression = factory.createNumericEntry("Morning depression", "Morning depression");
        calgary.addEntry(morningDepression);
        morningDepression.setSection(main);
        morningDepression.addValidationRule(calgaryRule);
        morningDepression.setDescription(helpText);
        morningDepression.setLabel("6");

        NumericEntry earlyAwakening = factory.createNumericEntry("Early awakening", "Early awakening");
        calgary.addEntry(earlyAwakening);
        earlyAwakening.setSection(main);
        earlyAwakening.addValidationRule(calgaryRule);
        earlyAwakening.setDescription(helpText);
        earlyAwakening.setLabel("7");

        NumericEntry suicide = factory.createNumericEntry("Suicide", "Suicide");
        calgary.addEntry(suicide);
        suicide.setSection(main);
        suicide.addValidationRule(calgaryRule);
        suicide.setDescription(helpText);
        suicide.setLabel("8");

        NumericEntry observedDepression = factory.createNumericEntry("Observed depression", "Observed depression");
        calgary.addEntry(observedDepression);
        observedDepression.setSection(main);
        observedDepression.addValidationRule(calgaryRule);
        observedDepression.setDescription(helpText);
        observedDepression.setLabel("9");

        DerivedEntry totalScore = factory.createDerivedEntry("Total score", "Total score");
        calgary.addEntry(totalScore);
        totalScore.setSection(main);
        totalScore.setDescription("Sum of items 1 to 9");
        totalScore.setFormula("d+h+sd+gi+pg+md+ea+s+od");
        totalScore.addVariable("d", depression);
        totalScore.addVariable("h", hopelessness);
        totalScore.addVariable("sd", selfDepreciation);
        totalScore.addVariable("gi", guiltIdeas);
        totalScore.addVariable("pg", pathologicalGuilt);
        totalScore.addVariable("md", morningDepression);
        totalScore.addVariable("ea", earlyAwakening);
        totalScore.addVariable("s", suicide);
        totalScore.addVariable("od", observedDepression);

        ValidationRule notInFuture = ValidationRulesWrapper.instance().getRule("Not in future");
        ValidationRule after2000 = ValidationRulesWrapper.instance().getRule("After 2000");
        DateEntry dateOfAssessment = factory.createDateEntry("Date of assesssment",
                "Date of assessment");
        calgary.addEntry(dateOfAssessment);
        dateOfAssessment.setSection(main);
        dateOfAssessment.addValidationRule(after2000);
        dateOfAssessment.addValidationRule(notInFuture);

        return calgary;
    }
}

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

public class GlobalAssessmentFunctioning extends AssessmentForm {

    public static Document createDocument(Factory factory) {

        Document doc = factory.createDocument("GAF Data Entry Sheet",
                "Global Assessment of Functioning Scale (GAF) Record");

        createDocumentStatuses(factory, doc);

        Section mainSec = factory.createSection("Main section");
        doc.addSection(mainSec);
        mainSec.setDisplayText("Main");
        SectionOccurrence mainSecOcc = factory.createSectionOccurrence(
                "Main Section Occurrence");
        mainSec.addOccurrence(mainSecOcc);

        NarrativeEntry instructions = factory.createNarrativeEntry("Instructions",
                "Instructions: Enter a value between 0 and 100: See ratings in " +
                        "study tools section for details of how to rate each scale.");
        doc.addEntry(instructions);
        instructions.setSection(mainSec);

        String description = "Enter a value between 0 and 100.";
        ValidationRule rule = ValidationRulesWrapper.instance().getRule("ZeroToHundred");

        NumericEntry totalNumeric = factory.createNumericEntry("Total Numeric Entry",
                "Total");
        doc.addEntry(totalNumeric);
        totalNumeric.setSection(mainSec);
        totalNumeric.setLabel("1");
        totalNumeric.setDescription(description);
        totalNumeric.addValidationRule(rule);

        NumericEntry symptomsNumeric = factory.createNumericEntry("Symptoms",
                "Symptoms");
        doc.addEntry(symptomsNumeric);
        symptomsNumeric.setSection(mainSec);
        symptomsNumeric.setLabel("2");
        symptomsNumeric.setDescription(description);
        symptomsNumeric.addValidationRule(rule);

        NumericEntry disabilityNumeric = factory.createNumericEntry("Disability " +
                "Numeric Entry", "Disability");
        doc.addEntry(disabilityNumeric);
        disabilityNumeric.setSection(mainSec);
        disabilityNumeric.setLabel("3");
        disabilityNumeric.setDescription(description);
        disabilityNumeric.addValidationRule(rule);

        ValidationRule notInFuture = ValidationRulesWrapper.instance().getRule("Not in future");
        ValidationRule after2000 = ValidationRulesWrapper.instance().getRule("After 2000");
        DateEntry dateOfAssessment = factory.createDateEntry("Date of assesssment",
                "Date of assessment");
        doc.addEntry(dateOfAssessment);
        dateOfAssessment.setSection(mainSec);
        dateOfAssessment.addValidationRule(after2000);
        dateOfAssessment.addValidationRule(notInFuture);

        return doc;

    }}

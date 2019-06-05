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

package org.psygrid.matisse;

import org.psygrid.common.ValidationRulesWrapper;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.AssessmentForm;

public class GAF extends AssessmentForm {

    public static Document createDocument(Factory factory) {

        Document doc = factory.createDocument("Global Assessment of Functioning (GAF) Scale",
                "Global Assessment of Functioning (GAF) Scale");

        createDocumentStatuses(factory, doc);

        Section mainSec = factory.createSection("Main section");
        doc.addSection(mainSec);
        mainSec.setDisplayText("Main");
        SectionOccurrence mainSecOcc = factory.createSectionOccurrence(
                "Main Section Occurrence");
        mainSec.addOccurrence(mainSecOcc);

        NarrativeEntry instructions = factory.createNarrativeEntry("Instructions",
                "Instructions: Enter a value between 1 and 100.");
        doc.addEntry(instructions);
        instructions.setSection(mainSec);


        String description = "Enter a value between 1 and 100.";
        ValidationRule rule = ValidationRulesWrapper.instance().getRule("OneToHundred");

        NumericEntry totalNumeric = factory.createNumericEntry("GAF Score",
                "GAF Score");
        doc.addEntry(totalNumeric);
        totalNumeric.setSection(mainSec);
        totalNumeric.setDescription(description);
        totalNumeric.addValidationRule(rule);

        return doc;

    }
}

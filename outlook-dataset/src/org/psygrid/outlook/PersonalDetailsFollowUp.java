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

import org.psygrid.common.UnitWrapper;
import org.psygrid.common.ValidationRulesWrapper;
import org.psygrid.data.model.hibernate.*;

public class PersonalDetailsFollowUp extends AssessmentForm {

    public static Document createDocument(Factory factory) {

    	ValidationRule positive = ValidationRulesWrapper.instance().getRule("Positive");
        ValidationRule after1900 = ValidationRulesWrapper.instance().getRule("After 1900");

        Document doc = factory.createDocument("Personal Details Follow Up", "Personal Details Form - Follow Up");

        createDocumentStatuses(factory, doc);

        Section mainSec = factory.createSection("Main");
        doc.addSection(mainSec);
        mainSec.setDisplayText("Main");
        SectionOccurrence mainSecOcc = factory.createSectionOccurrence("Main Occurrence");
        mainSec.addOccurrence(mainSecOcc);

        OptionEntry statusFirstAdmission = factory.createOptionEntry("Status on First Admission", "Status on current/first admission");
        doc.addEntry(statusFirstAdmission);
        statusFirstAdmission.setSection(mainSec);
        {
            Option voluntary = factory.createOption("Voluntary", 1);
            Option compulsory = factory.createOption("Compulsory", 2);
            Option voluntarySectioned = factory.createOption("Voluntary then sectioned", 3);
            statusFirstAdmission.addOption(voluntary);
            statusFirstAdmission.addOption(compulsory);
            statusFirstAdmission.addOption(voluntarySectioned);
        }

        NumericEntry lengthFirstAdmission = factory.createNumericEntry("Length First Admission", "Length of first admission");
        doc.addEntry(lengthFirstAdmission);
        lengthFirstAdmission.setSection(mainSec);
        lengthFirstAdmission.addUnit(UnitWrapper.instance().getUnit("days"));
        lengthFirstAdmission.addValidationRule(positive);

        OptionEntry natureLastAdmission = factory.createOptionEntry("Nature Last Admission", "Nature of last admission");
        doc.addEntry(natureLastAdmission);
        natureLastAdmission.setSection(mainSec);
        natureLastAdmission.setDescription("If only one admission, enter as 970");
        {
            Option voluntary = factory.createOption("Voluntary", 1);
            Option compulsory = factory.createOption("Compulsory", 2);
            Option voluntarySectioned = factory.createOption("Voluntary then sectioned", 3);
            natureLastAdmission.addOption(voluntary);
            natureLastAdmission.addOption(compulsory);
            natureLastAdmission.addOption(voluntarySectioned);
        }

        DateEntry dateLastAdmission = factory.createDateEntry("Date Last Admission", "Date of last admission");
        doc.addEntry(dateLastAdmission);
        dateLastAdmission.setSection(mainSec);
        dateLastAdmission.addValidationRule(after1900);

        NumericEntry lengthLastAdmission = factory.createNumericEntry("Length Last Admission", "Length of last admission");
        doc.addEntry(lengthLastAdmission);
        lengthLastAdmission.setSection(mainSec);
        lengthLastAdmission.addUnit(UnitWrapper.instance().getUnit("days"));
        lengthLastAdmission.addValidationRule(positive);

        return doc;
    }
}

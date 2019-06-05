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


package org.psygrid.drn.address;

import org.psygrid.common.ValidationRulesWrapper;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.AssessmentForm;

/**
 * @author Rob Harper
 *
 */
public class DiabetesFamilyHistory extends AssessmentForm {

    public static Document createDocument(Factory factory) {

        Document familyHistory = factory.createDocument("Family History",
                "Family history of diabetes");

        createDocumentStatuses(factory, familyHistory);

        // main section
        Section mainSection = factory.createSection("Main section");
        familyHistory.addSection(mainSection);
        mainSection.setDisplayText("Main");
        SectionOccurrence mainSectionOcc = factory.createSectionOccurrence("Main Section Occurrence");
        mainSection.addOccurrence(mainSectionOcc);

        OptionEntry motherDiabetic = factory.createOptionEntry("Mother diabetic", "Mother diabetic");
        familyHistory.addEntry(motherDiabetic);
        motherDiabetic.setSection(mainSection);
        createOptions(factory, motherDiabetic, new String[]{"Yes", "No"}, new int[]{1,0});

        OptionEntry motherGestational = factory.createOptionEntry("Mother gestational", "Mother had gestational diabetes");
        familyHistory.addEntry(motherGestational);
        motherGestational.setSection(mainSection);
        createOptions(factory, motherGestational, new String[]{"Yes", "No"}, new int[]{1,0});

        OptionEntry fatherDiabetic = factory.createOptionEntry("Father diabetic", "Father diabetic");
        familyHistory.addEntry(fatherDiabetic);
        fatherDiabetic.setSection(mainSection);
        createOptions(factory, fatherDiabetic, new String[]{"Yes", "No"}, new int[]{1,0});

        OptionEntry diabeticSiblings = factory.createOptionEntry("Diabetic siblings", "Diabetic siblings");
        familyHistory.addEntry(diabeticSiblings);
        diabeticSiblings.setSection(mainSection);
        createOptions(factory, diabeticSiblings, new String[]{"Yes", "No"}, new int[]{1,0});
        Option diabeticSiblingsYes = diabeticSiblings.getOption(0);

        IntegerEntry numberSiblings = factory.createIntegerEntry("Number siblings", "Number of siblings (including half siblings)");
        familyHistory.addEntry(numberSiblings);
        numberSiblings.setSection(mainSection);
        numberSiblings.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive"));

        IntegerEntry numberSiblingsDiabetes = factory.createIntegerEntry("Number siblings diabetes", "Number of siblings with diabetes (if known)", EntryStatus.DISABLED);
        familyHistory.addEntry(numberSiblingsDiabetes);
        numberSiblingsDiabetes.setSection(mainSection);
        numberSiblingsDiabetes.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive"));
        createOptionDependent(factory, diabeticSiblingsYes, numberSiblingsDiabetes);

        LongTextEntry comments = factory.createLongTextEntry("Comments", "Comments on family history", EntryStatus.OPTIONAL);
        familyHistory.addEntry(comments);
        comments.setSection(mainSection);

        return familyHistory;
    }
}

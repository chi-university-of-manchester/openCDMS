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
public class Termination extends AssessmentForm {

    public static Document createDocument(Factory factory) {

        Document termination = factory.createDocument("Termination",
                "Termination");

        createDocumentStatuses(factory, termination);

        // main section
        Section mainSection = factory.createSection("Main section");
        termination.addSection(mainSection);
        mainSection.setDisplayText("Main");
        SectionOccurrence mainSectionOcc = factory.createSectionOccurrence("Main Section Occurrence");
        mainSection.addOccurrence(mainSectionOcc);

        DateEntry dateOfBirth = factory.createDateEntry("Date of birth", "Date of birth");
        termination.addEntry(dateOfBirth);
        dateOfBirth.setSection(mainSection);
        dateOfBirth.addValidationRule(ValidationRulesWrapper.instance().getRule("Not in future"));
        dateOfBirth.addValidationRule(ValidationRulesWrapper.instance().getRule("After 1900"));
        dateOfBirth.setDisablePartialDate(true);
        dateOfBirth.setDisableStandardCodes(true);

        DateEntry dateOfTermination = factory.createDateEntry("Date of termination", "Date of termination");
        termination.addEntry(dateOfTermination);
        dateOfTermination.setSection(mainSection);
        dateOfTermination.addValidationRule(ValidationRulesWrapper.instance().getRule("Not in future"));
        dateOfTermination.addValidationRule(ValidationRulesWrapper.instance().getRule("2000 or after"));

        OptionEntry reason = factory.createOptionEntry("Reason for termination", "Reason for termination");
        termination.addEntry(reason);
        reason.setSection(mainSection);
        createOptions(factory, reason, new String[]{"Withdrawn consent", "Deceased", "Moved out of area", "Lost to follow up"},
        		new int[]{1,2,3,4});

        LongTextEntry comments = factory.createLongTextEntry("Comments", "Comments on reason for termination", EntryStatus.OPTIONAL);
        termination.addEntry(comments);
        comments.setSection(mainSection);

        OptionEntry remove = factory.createOptionEntry("Requested anonymous data be removed", "Requested anonymous data be removed");
        termination.addEntry(remove);
        remove.setSection(mainSection);
        createOptions(factory, remove, new String[]{"Yes", "No"}, new int[]{1,0});

        return termination;
    }
}

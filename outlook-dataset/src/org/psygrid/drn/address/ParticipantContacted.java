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
public class ParticipantContacted extends AssessmentForm {

    public static Document createDocument(Factory factory) {

        Document participantContacted = factory.createDocument("Participant Contacted",
                "Participant Contacted");

        createDocumentStatuses(factory, participantContacted);

        // contact section
        Section contactSection = factory.createSection("Contact section");
        participantContacted.addSection(contactSection);
        contactSection.setDisplayText("Contact");
        SectionOccurrence contactSectionOcc = factory.createSectionOccurrence("Contact Section Occurrence");
        contactSection.addOccurrence(contactSectionOcc);
        contactSectionOcc.setMultipleAllowed(true);

        DateEntry dob = factory.createDateEntry("Date of birth", "Date of birth");
        participantContacted.addEntry(dob);
        dob.setSection(contactSection);
        dob.addValidationRule(ValidationRulesWrapper.instance().getRule("After 1900"));
        dob.addValidationRule(ValidationRulesWrapper.instance().getRule("Not in future"));
        dob.setDisableStandardCodes(true);
        dob.setDisablePartialDate(true);

        TextEntry study1Name = factory.createTextEntry(
                "Study participant contacted about",
                "Study participant contacted about");
        participantContacted.addEntry(study1Name);
        study1Name.setSection(contactSection);

        DateEntry firstContact1 = factory.createDateEntry("Date first contact", "Date of first contact about this study");
        participantContacted.addEntry(firstContact1);
        firstContact1.setSection(contactSection);
        firstContact1.addValidationRule(ValidationRulesWrapper.instance().getRule("Not in future"));
        firstContact1.addValidationRule(ValidationRulesWrapper.instance().getRule("2000 or after"));

        OptionEntry response1 = factory.createOptionEntry("Response", "Response");
        participantContacted.addEntry(response1);
        response1.setSection(contactSection);
        createOptions(factory, response1, new String[]{"Positive", "Negative", "No response"}, new int[]{1,0,2}	);

        OptionEntry recruited1 = factory.createOptionEntry("Recruited into study", "Recruited into study");
        participantContacted.addEntry(recruited1);
        recruited1.setSection(contactSection);
        createOptions(factory, recruited1, new String[]{"Pending", "Yes", "No"}, new int[]{2,1,0});

        return participantContacted;
    }
}

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
public class GpDetails extends AssessmentForm {

    public static Document createDocument(Factory factory) {

        Document gpDetails = factory.createDocument("GP Details",
                "GP Details");

        createDocumentStatuses(factory, gpDetails);

        // main section
        Section mainSection = factory.createSection("Main section");
        gpDetails.addSection(mainSection);
        mainSection.setDisplayText("Main");
        SectionOccurrence mainSectionOcc = factory.createSectionOccurrence("Main Section Occurrence");
        mainSection.addOccurrence(mainSectionOcc);

        DateEntry dob = factory.createDateEntry("Date of birth", "Date of birth");
        gpDetails.addEntry(dob);
        dob.setSection(mainSection);
        dob.addValidationRule(ValidationRulesWrapper.instance().getRule("After 1900"));
        dob.addValidationRule(ValidationRulesWrapper.instance().getRule("Not in future"));
        dob.setDisableStandardCodes(true);
        dob.setDisablePartialDate(true);

        DateEntry addressEntered = factory.createDateEntry("Date address entered", "Date address entered");
        gpDetails.addEntry(addressEntered);
        addressEntered.setSection(mainSection);
        addressEntered.addValidationRule(ValidationRulesWrapper.instance().getRule("Not in future"));
        addressEntered.addValidationRule(ValidationRulesWrapper.instance().getRule("2000 or after"));

        TextEntry gpName = factory.createTextEntry("GP name", "GP name");
        gpDetails.addEntry(gpName);
        gpName.setSection(mainSection);

        TextEntry gpAddress1 = factory.createTextEntry("GP address 1", "GP address 1");
        gpDetails.addEntry(gpAddress1);
        gpAddress1.setSection(mainSection);

        TextEntry gpAddress2 = factory.createTextEntry("GP address 2", "GP address 2", EntryStatus.OPTIONAL);
        gpDetails.addEntry(gpAddress2);
        gpAddress2.setSection(mainSection);

        TextEntry gpAddress3 = factory.createTextEntry("GP address 3", "GP address 3", EntryStatus.OPTIONAL);
        gpDetails.addEntry(gpAddress3);
        gpAddress3.setSection(mainSection);

        TextEntry gpTown = factory.createTextEntry("GP town/city", "GP town/city");
        gpDetails.addEntry(gpTown);
        gpTown.setSection(mainSection);

        TextEntry gpCounty = factory.createTextEntry("GP county", "GP county", EntryStatus.OPTIONAL);
        gpDetails.addEntry(gpCounty);
        gpCounty.setSection(mainSection);

        TextEntry gpPostcode = factory.createTextEntry("GP postcode", "GP postcode");
        gpDetails.addEntry(gpPostcode);
        gpPostcode.setSection(mainSection);

        TextEntry gpTel = factory.createTextEntry("GP telephone number", "GP telephone number", EntryStatus.OPTIONAL);
        gpDetails.addEntry(gpTel);
        gpTel.setSection(mainSection);

        TextEntry gpFax = factory.createTextEntry("GP fax number", "GP fax number", EntryStatus.OPTIONAL);
        gpDetails.addEntry(gpFax);
        gpFax.setSection(mainSection);

        TextEntry practiceNurse = factory.createTextEntry("Practice nurse name", "Practice nurse name", EntryStatus.OPTIONAL);
        gpDetails.addEntry(practiceNurse);
        practiceNurse.setSection(mainSection);

        return gpDetails;
    }
}

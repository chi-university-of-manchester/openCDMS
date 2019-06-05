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
public class SiteTransfer extends AssessmentForm {

    public static Document createDocument(Factory factory) {

        Document siteTransfer = factory.createDocument("Site Transfer",
                "Site Transfer");

        createDocumentStatuses(factory, siteTransfer);

        // Transfer section
        Section transferSection = factory.createSection("Transfer section");
        siteTransfer.addSection(transferSection);
        transferSection.setDisplayText("Transfer");
        SectionOccurrence transferSectionOcc = factory.createSectionOccurrence("Transfer Section Occurrence");
        transferSection.addOccurrence(transferSectionOcc);
        transferSectionOcc.setMultipleAllowed(true);

        DateEntry dateOfBirth = factory.createDateEntry("Date of birth", "Date of birth");
        siteTransfer.addEntry(dateOfBirth);
        dateOfBirth.setSection(transferSection);
        dateOfBirth.addValidationRule(ValidationRulesWrapper.instance().getRule("Not in future"));
        dateOfBirth.addValidationRule(ValidationRulesWrapper.instance().getRule("After 1900"));
        dateOfBirth.setDisablePartialDate(true);
        dateOfBirth.setDisableStandardCodes(true);

        IntegerEntry previousCentreNumber = factory.createIntegerEntry("Previous group number", "Previous group number");
        siteTransfer.addEntry(previousCentreNumber);
        previousCentreNumber.setSection(transferSection);

        TextEntry previousSiteName = factory.createTextEntry("Previous site name", "Previous site name");
        siteTransfer.addEntry(previousSiteName);
        previousSiteName.setSection(transferSection);

        IntegerEntry newCentreNumber = factory.createIntegerEntry("New group number", "New group number");
        siteTransfer.addEntry(newCentreNumber);
        newCentreNumber.setSection(transferSection);

        TextEntry newSiteName = factory.createTextEntry("New site name", "New site name");
        siteTransfer.addEntry(newSiteName);
        newSiteName.setSection(transferSection);

        DateEntry dateOfTransfer = factory.createDateEntry("Date of transfer", "Date of transfer");
        siteTransfer.addEntry(dateOfTransfer);
        dateOfTransfer.setSection(transferSection);
        dateOfTransfer.addValidationRule(ValidationRulesWrapper.instance().getRule("Not in future"));
        dateOfTransfer.addValidationRule(ValidationRulesWrapper.instance().getRule("2000 or after"));

        return siteTransfer;

    }
}

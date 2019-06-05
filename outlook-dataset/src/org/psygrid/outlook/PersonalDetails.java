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

public class PersonalDetails extends AssessmentForm {

    public static Document createDocument(Factory factory) {

        ValidationRule after1900 = ValidationRulesWrapper.instance().getRule("After 1900");

        Document doc = factory.createDocument("Interview and consent " +
                "information form", "Interview and consent information form");

        createDocumentStatuses(factory, doc);

        Section mainSec = factory.createSection("Main Section", "Main");
        doc.addSection(mainSec);
        mainSec.setDisplayText("Main");
        SectionOccurrence mainSecOcc = factory.createSectionOccurrence("Main Section Occ");
        mainSec.addOccurrence(mainSecOcc);

        DateEntry dateApproached = factory.createDateEntry("Date team " +
                "approached client", "Date team approached for client contact");
        doc.addEntry(dateApproached);
        dateApproached.setSection(mainSec);
        dateApproached.addValidationRule(after1900);

        DateEntry dateArranged = factory.createDateEntry("Date arranged for " +
                "contact", "Date arranged for contact");
        doc.addEntry(dateArranged);
        dateArranged.setSection(mainSec);
        dateArranged.addValidationRule(after1900);

        DateEntry dateInfoSheet = factory.createDateEntry("Date information " +
                "sheet given", "Date information sheet given");
        doc.addEntry(dateInfoSheet);
        dateInfoSheet.setSection(mainSec);
        dateInfoSheet.addValidationRule(after1900);

        OptionEntry consentOption = factory.createOptionEntry("Informed " +
                "consent option", "Did patient give informed consent?");
        doc.addEntry(consentOption);
        consentOption.setSection(mainSec);
        Option consentNoOption = factory.createOption("No", 0);
        consentOption.addOption(consentNoOption);
        Option consentYesOption = factory.createOption("Yes", 1);
        consentOption.addOption(consentYesOption);

        OptionDependent consentNoOptDep = factory.createOptionDependent();
        consentNoOptDep.setEntryStatus(EntryStatus.MANDATORY);
        consentNoOption.addOptionDependent(consentNoOptDep);

        OptionDependent consentYesOptDep = factory.createOptionDependent();
        consentYesOptDep.setEntryStatus(EntryStatus.MANDATORY);
        consentYesOption.addOptionDependent(consentYesOptDep);

        DateEntry dateConsent = factory.createDateEntry("Date  of informed " +
                "consent", "Date  of informed consent", EntryStatus.DISABLED);
        doc.addEntry(dateConsent);
        dateConsent.setSection(mainSec);
        dateConsent.addValidationRule(after1900);
        consentYesOptDep.setDependentEntry(dateConsent);

        NarrativeEntry consentNarrative2 = factory.createNarrativeEntry("Consent not given instruction",
                "If patient does not give consent, indicate on the form and explain why below " +
                        "(also record this on the screening log).");
        doc.addEntry(consentNarrative2);
        consentNarrative2.setSection(mainSec);

        OptionEntry reasonsOption = factory.createOptionEntry("Reasons",
                "Reasons", EntryStatus.DISABLED);
        doc.addEntry(reasonsOption);
        reasonsOption.setSection(mainSec);
        reasonsOption.addOption(factory.createOption("Clinically unable to consent", 0));
        reasonsOption.addOption(factory.createOption("Un-contactable", 1));
        reasonsOption.addOption(factory.createOption("English not spoken", 2));
        reasonsOption.addOption(factory.createOption("Team advised to wait", 3));
        reasonsOption.addOption(factory.createOption("Undecided", 4));
        reasonsOption.addOption(factory.createOption("Declined consent", 5));
        Option otherOption = factory.createOption("Other reason (please specify in box below)", 6);
        reasonsOption.addOption(otherOption);
        consentNoOptDep.setDependentEntry(reasonsOption);

        LongTextEntry otherReasonText =
            factory.createLongTextEntry("Other reason text entry");
        doc.addEntry(otherReasonText);
        otherReasonText.setSection(mainSec);
        otherReasonText.setEntryStatus(EntryStatus.DISABLED);

        OptionDependent otherOptDep = factory.createOptionDependent();
        otherOptDep.setEntryStatus(EntryStatus.MANDATORY);
        otherOptDep.setDependentEntry(otherReasonText);
        otherOption.addOptionDependent(otherOptDep);

        return doc;
    }
}

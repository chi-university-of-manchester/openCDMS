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
public class DiabetesComplications extends AssessmentForm {

    public static Document createDocument(Factory factory) {

        Document complications = factory.createDocument("Diabetes complications",
                "Diabetes complications");

        createDocumentStatuses(factory, complications);

        // retinopathy section
        Section retinopathySection = factory.createSection("Retinopathy section", "Retinopathy");
        complications.addSection(retinopathySection);
        SectionOccurrence retinopathySectionOcc = factory.createSectionOccurrence("Retinopathy Section Occurrence");
        retinopathySection.addOccurrence(retinopathySectionOcc);

        OptionEntry retinopathyStatus = factory.createOptionEntry("Retinopathy status", "Retinopathy status");
        complications.addEntry(retinopathyStatus);
        retinopathyStatus.setSection(retinopathySection);
        createOptions(factory, retinopathyStatus,
        		new String[]{"None", "Retinopathy (non-proliferative, no maculopathy)", "Maculopathy", "Proliferative retinopathy", "Proliferative retinopathy and maculopathy"},
        		new int[]{0,1,2,3,4});

        DateEntry dateOfRetinopathy = factory.createDateEntry("Date of retinopathy", "Date of retinopathy assessment (if performed)");
        complications.addEntry(dateOfRetinopathy);
        dateOfRetinopathy.setSection(retinopathySection);
        dateOfRetinopathy.addValidationRule(ValidationRulesWrapper.instance().getRule("Not in future"));
        dateOfRetinopathy.addValidationRule(ValidationRulesWrapper.instance().getRule("2000 or after"));

        LongTextEntry retinopathyComments = factory.createLongTextEntry("Retinopathy status comments", "Retinopathy status comments", EntryStatus.OPTIONAL);
        complications.addEntry(retinopathyComments);
        retinopathyComments.setSection(retinopathySection);

        Section nephropathySection = factory.createSection("Nephropathy section", "Nephropathy");
        complications.addSection(nephropathySection);
        SectionOccurrence nephropathySecOcc = factory.createSectionOccurrence("Nephropathy section occ");
        nephropathySection.addOccurrence(nephropathySecOcc);

        Biochemistry.addAlbuminuriaProteinuriaEntries(factory, complications, nephropathySection);

        LongTextEntry nephropathyComments = factory.createLongTextEntry(
                "Nephropathy status comments", "Nephropathy status comments", EntryStatus.OPTIONAL);
        complications.addEntry(nephropathyComments);
        nephropathyComments.setSection(nephropathySection);


        Section neuropathySection = factory.createSection("Neuropathy section", "Neuropathy");
        complications.addSection(neuropathySection);
        SectionOccurrence neuropathySecOcc = factory.createSectionOccurrence("Neuropathy section occ");
        neuropathySection.addOccurrence(neuropathySecOcc);

        OptionEntry neuropathyStatus = factory.createOptionEntry("Neuropathy status", "Neuropathy status");
        complications.addEntry(neuropathyStatus);
        neuropathyStatus.setSection(neuropathySection);
        createOptions(factory, neuropathyStatus, new String[]{
        		"None",
        		"Abnormal neurologic examination"}, new int[]{0,1});

        DateEntry dateNeuropathy = factory.createDateEntry("Date of neuropathy assessment", "Date of neuropathy assessment (if performed)");
        complications.addEntry(dateNeuropathy);
        dateNeuropathy.setSection(neuropathySection);
        dateNeuropathy.addValidationRule(ValidationRulesWrapper.instance().getRule("Not in future"));
        dateNeuropathy.addValidationRule(ValidationRulesWrapper.instance().getRule("2000 or after"));

        LongTextEntry neuropathyComments = factory.createLongTextEntry(
                "Neuropathy status comments", "Neuropathy status comments", EntryStatus.OPTIONAL);
        complications.addEntry(neuropathyComments);
        neuropathyComments.setSection(neuropathySection);

        return complications;
    }
}

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
package org.psygrid.edie;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.AssessmentForm;

public class SCIDPsychDisorders extends AssessmentForm {

	public static Document createDocument(Factory factory) {

		Document scid = factory.createDocument("SCID",
                "SCID");

		createDocumentStatuses(factory, scid);

		// main section
		Section mainSection = factory.createSection("Main section occurrence");
		scid.addSection(mainSection);
		mainSection.setDisplayText("Main");
		SectionOccurrence mainSectionOcc = factory
				.createSectionOccurrence("Main Section Occurrence");
		mainSection.addOccurrence(mainSectionOcc);

		CompositeEntry diagComp = factory.createComposite("SCID composite", "Select the Scid diagnosis/diagnoses from the list");
		scid.addEntry(diagComp);
		diagComp.setSection(mainSection);
		diagComp.setLabel("1");


		OptionEntry d = factory.createOptionEntry("SCID Diagnosis", "SCID Diagnosis");
		d.setSection(mainSection);
		diagComp.addEntry(d);
		d.setOptionCodesDisplayed(true);
		d.setDescription("Select the SCID diagnosis from the list.");
        d.addOption(factory.createOption("None", 0));
        d.addOption(factory.createOption("Schizophrenia", 1));
        d.addOption(factory.createOption("Schizophreniform disorder", 2));
        d.addOption(factory.createOption("Schizoaffective disorder", 3));
        d.addOption(factory.createOption("Delusional disorder", 4));
        d.addOption(factory.createOption("Brief psychotic disorder", 5));
        Option d6 = factory.createOption("Psychotic disorder due to a general medical condition, please specify", 6);
        d.addOption(d6);
        d6.setTextEntryAllowed(true);
        d.addOption(factory.createOption("Substance-induced psychotic disorder", 7));
        d.addOption(factory.createOption("Psychotic disorder not otherwise specified", 8));
		return scid;
	}
}

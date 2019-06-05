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

public class SCID extends AssessmentForm {

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
		d.addOption(factory.createOption("Bipolar I disorder", 1));
		d.addOption(factory.createOption("Bipolar II disorder", 2));
		d.addOption(factory.createOption("Other bipolar disorder", 3));
		d.addOption(factory.createOption("Major depressive disorder", 4));
		d.addOption(factory.createOption("Dysthymic disorder", 5));
		d.addOption(factory.createOption("Depressive disorder not otherwise specified", 6));
		Option o7 = factory.createOption("Mood disorder due to a general medical condition", 7);
		d.addOption(o7);
		o7.setTextEntryAllowed(true);
		d.addOption(factory.createOption("Substance-induced mood disorder", 8));
		d.addOption(factory.createOption("Panic disorder with agrophobia", 9));
		d.addOption(factory.createOption("Panic disorder without agrophobia", 36));
		d.addOption(factory.createOption("Agoraphobia without history of panic disorder", 10));
		d.addOption(factory.createOption("Social phobia", 11));
		d.addOption(factory.createOption("Specific phobia", 12));
		d.addOption(factory.createOption("Obsessive compulsive", 13));
		d.addOption(factory.createOption("Posttraumatic stress", 14));
		d.addOption(factory.createOption("Generalized anxiety", 15));
		Option o16 = factory.createOption("Anxiety disorder due to a general medical condition", 16);
		d.addOption(o16);
		o16.setTextEntryAllowed(true);
		d.addOption(factory.createOption("Substance-induced anxiety disorder", 17));
		d.addOption(factory.createOption("Anxiety disorder not otherwise specified", 18));
		d.addOption(factory.createOption("Alcohol abuse", 19));
		d.addOption(factory.createOption("Alcohol dependence", 20));
		d.addOption(factory.createOption("Sedative-Hypnotic-Anxiolytic abuse", 21));
		d.addOption(factory.createOption("Sedative-Hypnotic-Anxiolytic dependence", 22));
		d.addOption(factory.createOption("Cannabis abuse", 23));
		d.addOption(factory.createOption("Cannabis dependence", 24));
		d.addOption(factory.createOption("Stimulants abuse", 25));
		d.addOption(factory.createOption("Stimulants dependence", 26));
		d.addOption(factory.createOption("Opiod abuse", 27));
		d.addOption(factory.createOption("Opiod dependence", 28));
		d.addOption(factory.createOption("Cocaine abuse", 29));
		d.addOption(factory.createOption("Cocaine dependence", 30));
		d.addOption(factory.createOption("Hal./PCP abuse", 31));
		d.addOption(factory.createOption("Hal./PCP dependence", 32));
		d.addOption(factory.createOption("Poly Drug dependence", 33));
		Option o34 = factory.createOption("Other substance abuse", 34);
		d.addOption(o34);
		o34.setTextEntryAllowed(true);
		Option o35 = factory.createOption("Other substance dependence", 35);
		d.addOption(o35);
		o35.setTextEntryAllowed(true);
		//36 embedded above
		d.addOption(factory.createOption("Somatization Disorder", 37));
		d.addOption(factory.createOption("Pain Disorder", 38));
		d.addOption(factory.createOption("Undifferentiated Somatoform Disorder", 39));
		d.addOption(factory.createOption("Hypochondiasis", 40));
		d.addOption(factory.createOption("Body Dysmorphic", 41));
		d.addOption(factory.createOption("Anorexia Nervosa", 42));
		d.addOption(factory.createOption("Bulimia Nervosa", 43));
		d.addOption(factory.createOption("Binge Eating Disorder", 44));
		d.addOption(factory.createOption("Adjustment Disorder", 45));
		Option o46 = factory.createOption("Other DSM-IV Axis I Disorder", 46);
		d.addOption(o46);
		o46.setTextEntryAllowed(true);
		d.addOption(factory.createOption("None", 0));
		return scid;
	}
}

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

import org.psygrid.common.UnitWrapper;
import org.psygrid.common.ValidationRulesWrapper;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.AssessmentForm;

public class Treatment extends AssessmentForm {

	public static Document createDocument(Factory factory) {

		Unit mg = UnitWrapper.instance().getUnit("mg");

		ValidationRule positiveNumber = ValidationRulesWrapper.instance().getRule(
				"Positive");

		Document transition = factory.createDocument("Treatment",
                "Treatment");

		createDocumentStatuses(factory, transition);

		// main section
		Section mainSection = factory.createSection("Main section occurrence");
		transition.addSection(mainSection);
		mainSection.setDisplayText("Main");
		SectionOccurrence mainSectionOcc = factory
				.createSectionOccurrence("Main Section Occurrence");
		mainSection.addOccurrence(mainSectionOcc);

		IntegerEntry hospitalisation = factory.createIntegerEntry("Hospital Days", "Number of days in hospital since last assessment");
		transition.addEntry(hospitalisation);
		hospitalisation.setSection(mainSection);
		hospitalisation.addValidationRule(positiveNumber);


		CompositeEntry hospitalisationDetails = factory.createComposite("Hospitalisation Details", "Hospitalisation Details");
		transition.addEntry(hospitalisationDetails);
		hospitalisationDetails.setSection(mainSection);

		TextEntry hospitalisationService = factory.createTextEntry("Hospitalisation Service", "Service");
		hospitalisationDetails.addEntry(hospitalisationService);
		hospitalisationService.setSection(mainSection);

		TextEntry hospitalisationReason = factory.createTextEntry("Hospitalisation Reason", "Reason");
		hospitalisationDetails.addEntry(hospitalisationReason);
		hospitalisationReason.setSection(mainSection);


		CompositeEntry currentMedication = factory.createComposite("Medication Prescribed", "Medications(s) prescribed since last assessment");
		transition.addEntry(currentMedication);
        currentMedication.setSection(mainSection);

        TextEntry medicationC = factory.createTextEntry("Medication", "Medication");
        currentMedication.addEntry(medicationC);
        medicationC.setSection(mainSection);

        NumericEntry doseC = factory.createNumericEntry("Dose", "Dose");
        currentMedication.addEntry(doseC);
        doseC.setSection(mainSection);
        doseC.addUnit(mg);
        doseC.addValidationRule(positiveNumber);

        OptionEntry freqOfUseC = factory.createOptionEntry("Freq", "Freq");
        currentMedication.addEntry(freqOfUseC);
        freqOfUseC.setSection(mainSection);
        freqOfUseC.addOption(factory.createOption("As required",0));
        freqOfUseC.addOption(factory.createOption("Daily",1));
        freqOfUseC.addOption(factory.createOption("Weekly",2));
        freqOfUseC.addOption(factory.createOption("Monthly",3));

		return transition;
	}
}

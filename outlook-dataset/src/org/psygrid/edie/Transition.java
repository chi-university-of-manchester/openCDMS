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

public class Transition extends AssessmentForm {

	public static Document createDocument(Factory factory) {

		Unit mg = UnitWrapper.instance().getUnit("mg");

		ValidationRule notInFuture = ValidationRulesWrapper.instance().getRule(
				"Not in future");
		ValidationRule after2000 = ValidationRulesWrapper.instance().getRule(
				"After 2000");
		ValidationRule positiveNumber = ValidationRulesWrapper.instance().getRule(
				"Positive");

		Document transition = factory.createDocument("Transition",
                "Transition");

		createDocumentStatuses(factory, transition);

		// main section
		Section mainSection = factory.createSection("Main section occurrence");
		transition.addSection(mainSection);
		mainSection.setDisplayText("Main");
		SectionOccurrence mainSectionOcc = factory
				.createSectionOccurrence("Main Section Occurrence");
		mainSection.addOccurrence(mainSectionOcc);

		DateEntry date = factory.createDateEntry("Date of Transition", "Date of Transition");
		transition.addEntry(date);
		date.setSection(mainSection);
		date.addValidationRule(notInFuture);
		date.addValidationRule(after2000);

		OptionEntry typeof = factory.createOptionEntry("Type of Transition", "What type of transition?");
		Option caarms = factory.createOption("CAARMS Transition", 1);
		typeof.addOption(caarms);
		Option medication = factory.createOption("Medication", 2);
		typeof.addOption(medication);
		Option scid = factory.createOption("SCID", 3);
		typeof.addOption(scid);
		transition.addEntry(typeof);
		typeof.setSection(mainSection);

        CompositeEntry currentMedication = factory.createComposite("Current Medication", "Current Medication");
        transition.addEntry(currentMedication);
        currentMedication.setSection(mainSection);
        currentMedication.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, medication, currentMedication);

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

        OptionEntry scidDiagnosis = factory.createOptionEntry("SCID Diagnosis", "What diagnosis?");
        transition.addEntry(scidDiagnosis);
        scidDiagnosis.setSection(mainSection);
        scidDiagnosis.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, scid, scidDiagnosis);
        scidDiagnosis.setOptionCodesDisplayed(true);
        scidDiagnosis.addOption(factory.createOption("Schizophrenia", 1));
        scidDiagnosis.addOption(factory.createOption("Schizophreniform disorder", 2));
        scidDiagnosis.addOption(factory.createOption("Schizoaffective disorder", 3));
        scidDiagnosis.addOption(factory.createOption("Delusional disorder", 4));
        scidDiagnosis.addOption(factory.createOption("Brief psychotic disorder", 5));
        scidDiagnosis.addOption(factory.createOption("Psychotic disorder due to a general medical condition", 6));
        scidDiagnosis.addOption(factory.createOption("Substance-induced psychotic disorder", 7));
        scidDiagnosis.addOption(factory.createOption("Psychotic disorder not otherwise specified", 8));

        OptionEntry hospitalisation = factory.createOptionEntry("Hospitalisation",
                "Has transition necessitated inpatient hospitalisation?");
        hospitalisation.setSection(mainSection);
        transition.addEntry(hospitalisation);
        hospitalisation.setOptionCodesDisplayed(true);
        hospitalisation.addOption(factory.createOption("No", 0));
        hospitalisation.addOption(factory.createOption("Yes", 1));
        return transition;
	}
}

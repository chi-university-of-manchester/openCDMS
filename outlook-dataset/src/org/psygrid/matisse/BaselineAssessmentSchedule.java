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


package org.psygrid.matisse;
/*

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
02110-1301, USA.
*/
import org.psygrid.common.ValidationRulesWrapper;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.AssessmentForm;

public class BaselineAssessmentSchedule extends AssessmentForm {


	public static Document createDocument(Factory factory){

        ValidationRule notInFuture = ValidationRulesWrapper.instance().getRule("Not in future");
        ValidationRule after1900 = ValidationRulesWrapper.instance().getRule("After 1900");

        Document doc = factory.createDocument("Baseline Assessment Schedule",
                "Baseline Assessment Schedule");
        createDocumentStatuses(factory, doc);

        Section mainSec = factory.createSection("Main", "Main");
        doc.addSection(mainSec);
        mainSec.setDisplayText("Main");
        SectionOccurrence sectionOcc = factory.createSectionOccurrence("Main");
        mainSec.addOccurrence(sectionOcc);

        TextEntry patientIDNumber = factory.createTextEntry("Patient ID number", "Patient identification number:");
        doc.addEntry(patientIDNumber);
        patientIDNumber.setSection(mainSec);

        DateEntry dob = factory.createDateEntry("DOB", "Date of Birth");
        doc.addEntry(dob);
        dob.setSection(mainSec);
        dob.addValidationRule(notInFuture);
        dob.addValidationRule(after1900);

		IntegerEntry age = factory.createIntegerEntry("Age", "Age:");
		doc.addEntry(age);
		age.setSection(mainSec);

        OptionEntry gender = factory.createOptionEntry("Gender "
                , "Gender");
        doc.addEntry(gender);
        gender.setSection(mainSec);
        gender.addOption(factory.createOption("Male", 1));
        gender.addOption(factory.createOption("Female", 2));

        OptionEntry ethnicity = factory.createOptionEntry("Ethnicity"
                , "Ethnicity");
        doc.addEntry(ethnicity);
        ethnicity.setSection(mainSec);
        ethnicity.setOptionCodesDisplayed(true);
        ethnicity.addOption(factory.createOption("White British", 1));
        ethnicity.addOption(factory.createOption("White: Irish", 2));
        ethnicity.addOption(factory.createOption("White: Other", 3));
        ethnicity.addOption(factory.createOption("Asian/ Asian British", 4));
        ethnicity.addOption(factory.createOption("Black/ Black British: Caribbean", 5));
        ethnicity.addOption(factory.createOption("Black/ Black British: African", 6));
        ethnicity.addOption(factory.createOption("Black/ Black British: Other", 7));
        ethnicity.addOption(factory.createOption("Chinese/ Far-East", 8));
        ethnicity.addOption(factory.createOption("Arab/ Middle East", 9));

        OptionEntry maritalStatus = factory.createOptionEntry("Marital status"
                , "Marital Status");
        doc.addEntry(maritalStatus);
        maritalStatus.setSection(mainSec);
        maritalStatus.addOption(factory.createOption("Married/ living as married", 1));
        maritalStatus.addOption(factory.createOption("Divorced/ separated", 2));
        maritalStatus.addOption(factory.createOption("Widowed", 3));
        maritalStatus.addOption(factory.createOption("Single", 4));


        OptionEntry highestEdAchievement = factory.createOptionEntry("Highest Educational Achievement"
                , "Highest Educational Achievement");
        doc.addEntry(highestEdAchievement);
        highestEdAchievement.setSection(mainSec);
        highestEdAchievement.addOption(factory.createOption("Degree", 1));
        highestEdAchievement.addOption(factory.createOption("A-levels", 2));
        highestEdAchievement.addOption(factory.createOption("GCSE", 3));
        highestEdAchievement.addOption(factory.createOption("NVQ/ vocational training", 4));
        highestEdAchievement.addOption(factory.createOption("Nil", 5));

        OptionEntry primaryDiagnosis = factory.createOptionEntry("Primary Diagnosis", "Primary Diagnosis");
        doc.addEntry(primaryDiagnosis);
        primaryDiagnosis.setSection(mainSec);
        primaryDiagnosis.addOption(factory.createOption("Schizophrenia", 1));
        primaryDiagnosis.addOption(factory.createOption("Schizoaffective disorder", 2));
        primaryDiagnosis.addOption(factory.createOption("Delusional disorders", 3));
        primaryDiagnosis.addOption(factory.createOption("Brief reactive psychosis", 4));
        primaryDiagnosis.addOption(factory.createOption("Bipolar affective disorder", 5));
        primaryDiagnosis.addOption(factory.createOption("Substance misuse", 6));
        primaryDiagnosis.addOption(factory.createOption("Depression", 7));
        primaryDiagnosis.addOption(factory.createOption("Anxiety", 8));
        primaryDiagnosis.addOption(factory.createOption("PTSD", 9));
        primaryDiagnosis.addOption(factory.createOption("Personality disorder", 10));
        Option otherOption = factory.createOption("Other (please state)", 11);
        otherOption.setTextEntryAllowed(true);
        primaryDiagnosis.addOption(otherOption);

        DateEntry firstPresentation = factory.createDateEntry("First presentation");
        doc.addEntry(firstPresentation);
        firstPresentation.setSection(mainSec);
        firstPresentation.setDisplayText("Date of first presentation to services with schizophrenia");
        firstPresentation.addValidationRule(after1900);
        firstPresentation.addValidationRule(notInFuture);


	    CompositeEntry psychosocialInterventions = factory.createComposite("Psychosocial Interventions",
                "Previous receipt of structured psychosocial interventions including:");
		doc.addEntry(psychosocialInterventions);
		psychosocialInterventions.setSection(mainSec);
		psychosocialInterventions.addRowLabel("Family Therapy");
		psychosocialInterventions.addRowLabel("Cognitive behaviour therapy");
		psychosocialInterventions.addRowLabel("Relapse Prevention");
		psychosocialInterventions.addRowLabel("Art therapy");
		psychosocialInterventions.addRowLabel("Music therapy");
		psychosocialInterventions.addRowLabel("Dance/ movement therapy");
		psychosocialInterventions.addRowLabel("Other arts therapies");
		psychosocialInterventions.addRowLabel("Other therapy (please state below)");

		TextEntry interventionType = factory.createTextEntry(
                "Type", "Type");
		psychosocialInterventions.addEntry(interventionType);
		interventionType.setSection(mainSec);

		OptionEntry response = factory.createOptionEntry(
                "Response", "Response");
		psychosocialInterventions.addEntry(response);
		response.addOption(factory.createOption("No", 0));
		response.addOption(factory.createOption("Yes (ever)", 1));
		response.addOption(factory.createOption("Yes - last 12 months", 2));
		response.setSection(mainSec);

		NumericEntry numSessions = factory.createNumericEntry("Number of sessions",
                "Number of sessions within last 12 months");
		psychosocialInterventions.addEntry(numSessions);
		numSessions.setSection(mainSec);

		OptionEntry sessionType = factory.createOptionEntry("Type of session", "Type of session");
		psychosocialInterventions.addEntry(sessionType);
		sessionType.addOption(factory.createOption("Individual", 1));
		sessionType.addOption(factory.createOption("Group", 2));

        TextEntry other = factory.createTextEntry(
                "Other",
                "If you chose 'other' in the previous question, please specify.",
                EntryStatus.OPTIONAL);
        doc.addEntry(other);
        other.setSection(mainSec);

        return doc;
	}

}



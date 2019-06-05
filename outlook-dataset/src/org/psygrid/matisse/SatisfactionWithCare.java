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

import org.psygrid.common.ValidationRulesWrapper;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.AssessmentForm;

public class SatisfactionWithCare extends AssessmentForm {

	public static Document createDocument(Factory factory){

		Document doc = factory.createDocument("Satisfaction with care",
                "Satisfaction with care");

		ValidationRule oneToSeven = ValidationRulesWrapper.instance().getRule("OneToSeven");

        Section mainSec = factory.createSection("Main", "Main");
        doc.addSection(mainSec);
        mainSec.setDisplayText("Main");
        SectionOccurrence mainOcc = factory.createSectionOccurrence("Main section occurrence");
        mainSec.addOccurrence(mainOcc);

        NarrativeEntry intro = factory.createNarrativeEntry("Introduction");
        intro.setDisplayText("Please can you tell us whether or not you have been satisfied with the services" +
                             " you have been provided over the last six months by answering the questions below." +
                             " We are interested in your honest opinion, whether they are positive or negative.");
        doc.addEntry(intro);
        intro.setSection(mainSec);
		createDocumentStatuses(factory, doc);

        OptionEntry serviceQuality = factory.createOptionEntry("Quality of service", "How would you rate the quality of service you received?");
        doc.addEntry(serviceQuality);
        serviceQuality.setOptionCodesDisplayed(true);
        serviceQuality.setSection(mainSec);
        serviceQuality.setLabel("1");
        serviceQuality.addOption(factory.createOption("Excellent", 4));
        serviceQuality.addOption(factory.createOption("Good", 3));
        serviceQuality.addOption(factory.createOption("Fair", 2));
        serviceQuality.addOption(factory.createOption("Poor", 1));

        OptionEntry wantedService = factory.createOptionEntry("Wanted service", "Did you get the kind of service you wanted?");
        doc.addEntry(wantedService);
        wantedService.setOptionCodesDisplayed(true);
        wantedService.setSection(mainSec);
        wantedService.setLabel("2");
        wantedService.addOption(factory.createOption("No, definitely not", 4));
        wantedService.addOption(factory.createOption("No, not really", 3));
        wantedService.addOption(factory.createOption("Yes, generally", 2));
        wantedService.addOption(factory.createOption("Yes, definitely", 1));

        OptionEntry programMetNeeds = factory.createOptionEntry("Program Met Needs", "To what extent has our program met your needs?");
        doc.addEntry(programMetNeeds);
        programMetNeeds.setOptionCodesDisplayed(true);
        programMetNeeds.setSection(mainSec);
        programMetNeeds.setLabel("3");
        programMetNeeds.addOption(factory.createOption("Almost all of my needs have been met", 4));
        programMetNeeds.addOption(factory.createOption("Most of my needs have been met", 3));
        programMetNeeds.addOption(factory.createOption("Only a few of my needs have been met", 2));
        programMetNeeds.addOption(factory.createOption("None of my needs have been met", 1));

        OptionEntry recommendProgram = factory.createOptionEntry("Recommend Program", "If a friend were in need of" +
                " similar help, would you recommend our program to him or her?");
        doc.addEntry(recommendProgram);
        recommendProgram.setOptionCodesDisplayed(true);
        recommendProgram.setSection(mainSec);
        recommendProgram.setLabel("4");
        recommendProgram.addOption(factory.createOption("No, definitely not", 4));
        recommendProgram.addOption(factory.createOption("No, not really", 3));
        recommendProgram.addOption(factory.createOption("Yes, generally", 2));
        recommendProgram.addOption(factory.createOption("Yes, definitely", 1));

        OptionEntry satisfiedAmount = factory.createOptionEntry("Satisfied Amount", "How satisfied" +
                " are you with the amount of help you received?");
		doc.addEntry(satisfiedAmount);
		satisfiedAmount.setOptionCodesDisplayed(true);
		satisfiedAmount.setSection(mainSec);
		satisfiedAmount.setLabel("5");
		satisfiedAmount.addOption(factory.createOption("Quite dissatisfied", 4));
		satisfiedAmount.addOption(factory.createOption("Mildly dissatisfied", 3));
		satisfiedAmount.addOption(factory.createOption("Mostly satisfied", 2));
		satisfiedAmount.addOption(factory.createOption("Very satisfied", 1));

        OptionEntry servicesHelped = factory.createOptionEntry("Services Helped", "Have the services you received" +
                " helped you to deal more effectively with your problems?");
		doc.addEntry(servicesHelped);
		servicesHelped.setOptionCodesDisplayed(true);
		servicesHelped.setSection(mainSec);
		servicesHelped.setLabel("6");
		servicesHelped.addOption(factory.createOption("Yes, they helped a great deal", 4));
		servicesHelped.addOption(factory.createOption("Yes, they helped somewhat", 3));
		servicesHelped.addOption(factory.createOption("No, they didn't really help", 2));
		servicesHelped.addOption(factory.createOption("No, they seemed to make things worse", 1));

        OptionEntry servicesSatisfied = factory.createOptionEntry("Services Satisfied", "In an overall, general sense," +
                " how satisfied are you with the service you received?");
		doc.addEntry(servicesSatisfied);
		servicesSatisfied.setOptionCodesDisplayed(true);
		servicesSatisfied.setSection(mainSec);
		servicesSatisfied.setLabel("7");
		servicesSatisfied.addOption(factory.createOption("Very satisfied", 4));
		servicesSatisfied.addOption(factory.createOption("Mostly satisfied", 3));
		servicesSatisfied.addOption(factory.createOption("Mildy satisfied", 2));
		servicesSatisfied.addOption(factory.createOption("Quite dissatisfied", 1));

        OptionEntry useServicesAgain = factory.createOptionEntry("Use Services Again", "If you were to seek help again," +
                " would you come back to use these services");
		doc.addEntry(useServicesAgain);
		useServicesAgain.setOptionCodesDisplayed(true);
		useServicesAgain.setSection(mainSec);
		useServicesAgain.setLabel("8");
		useServicesAgain.addOption(factory.createOption("No, definitely not", 4));
		useServicesAgain.addOption(factory.createOption("No, not really", 3));
		useServicesAgain.addOption(factory.createOption("Yes, generally", 2));
		useServicesAgain.addOption(factory.createOption("Yes, definitely", 1));

        OptionEntry preferenceInStudy = factory.createOptionEntry("Study Preference",
                "As we have discussed, we will be deciding which of the three types of treatment" +
                        " people will get by random.  So, at the stage we have no idea whether you will be" +
                        " offered treatment as usual, treatment as usual plus a place in activity group or treatment" +
                        " as usual plus a place in an art therapy group.  However, we would like to find out whether" +
                        " people do have a preference for which arm of the study they end up in (even though we" +
                        " are not able to use this information when allocating epople to one of these three interventions)." +
                        " Please could you tell us what your preference would be for the study? ");
		doc.addEntry(preferenceInStudy);
		preferenceInStudy.setOptionCodesDisplayed(true);
		preferenceInStudy.setSection(mainSec);
		preferenceInStudy.setLabel("9");


		Option preferTreatment1 = factory.createOption("I would prefer to receive treatment as usual", 1);
		Option preferTreatment2 = factory.createOption("I would prefer to receive treatment as usual " +
                " plus a place in an activity group", 2);
		Option preferTreatment3 = factory.createOption("I would prefer to receive treatment as usual plus a place " +
                "in an art therapy group", 3);
		Option preferTreatment4 = factory.createOption("I do not have any preference", 4);

		preferenceInStudy.addOption(preferTreatment1);
		preferenceInStudy.addOption(preferTreatment2);
		preferenceInStudy.addOption(preferTreatment3);
		preferenceInStudy.addOption(preferTreatment4);

        String text = "If you have a preference, how strong is this preference? (1=Not strong at all, 7=Very strong preference)";
        NumericEntry preferenceScore = factory.createNumericEntry("Preference",
                text);
        doc.addEntry(preferenceScore);
        preferenceScore.setSection(mainSec);
        preferenceScore.setDescription(text);
        preferenceScore.addValidationRule(oneToSeven);
        preferenceScore.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, preferTreatment1, preferenceScore);
        createOptionDependent(factory, preferTreatment2, preferenceScore);
        createOptionDependent(factory, preferTreatment3, preferenceScore);


		return doc;
	}
}
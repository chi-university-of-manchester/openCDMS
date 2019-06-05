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

package org.psygrid.neden;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.AssessmentForm;

public class ServiceEngagementScale extends AssessmentForm {

	public static Document createDocument(Factory factory) {

		Document ses = factory.createDocument("Service Engagement Scale",
                "Service Engagment Scale");

		createDocumentStatuses(factory, ses);

		Section mainSec = factory.createSection("Main", "Main");
		ses.addSection(mainSec);
		mainSec.setDisplayText("Main");
		SectionOccurrence mainOcc = factory
				.createSectionOccurrence("Main section occurrence");
		mainSec.addOccurrence(mainOcc);

		NarrativeEntry instructions1 = factory
				.createNarrativeEntry(
                        "Instructions 1",
                        "People differ in the way that they engage with services. Please indicate how well each of the following statements describes the way that your \"client\" makes use of the available services.");
		ses.addEntry(instructions1);
		instructions1.setSection(mainSec);

		NarrativeEntry availability = factory.createNarrativeEntry(
                "Availability", "Availability");
		ses.addEntry(availability);
		availability.setSection(mainSec);
		availability.setLabel("1");
		{
			OptionEntry appointmentDifficulty = factory.createOptionEntry(
                    "Appointment Difficulty",
                    "The client makes it difficult to arrange appointments");
			ses.addEntry(appointmentDifficulty);
			appointmentDifficulty.setSection(mainSec);
			Option rarely = factory.createOption("Not at all or Rarely", 1);
			appointmentDifficulty.addOption(rarely);
			Option sometimes = factory.createOption("Sometimes", 2);
			appointmentDifficulty.addOption(sometimes);
			Option often = factory.createOption("Often", 3);
			appointmentDifficulty.addOption(often);
			Option usually = factory.createOption("Most of the time", 4);
			appointmentDifficulty.addOption(usually);
		}
		{
			OptionEntry availableAfterArrangment = factory.createOptionEntry(
                    "Available After Arrangement",
                    "When a visit is arranged, the client is available");
			ses.addEntry(availableAfterArrangment);
			availableAfterArrangment.setSection(mainSec);
			Option rarely = factory.createOption("Not at all or Rarely", 1);
			availableAfterArrangment.addOption(rarely);
			Option sometimes = factory.createOption("Sometimes", 2);
			availableAfterArrangment.addOption(sometimes);
			Option often = factory.createOption("Often", 3);
			availableAfterArrangment.addOption(often);
			Option usually = factory.createOption("Most of the time", 4);
			availableAfterArrangment.addOption(usually);
		}
		{
			OptionEntry appointmentAvoidance = factory.createOptionEntry(
                    "Appointment Avoidance",
                    "The client avoids making appointments");
			ses.addEntry(appointmentAvoidance);
			appointmentAvoidance.setSection(mainSec);
			Option rarely = factory.createOption("Not at all or Rarely", 1);
			appointmentAvoidance.addOption(rarely);
			Option sometimes = factory.createOption("Sometimes", 2);
			appointmentAvoidance.addOption(sometimes);
			Option often = factory.createOption("Often", 3);
			appointmentAvoidance.addOption(often);
			Option usually = factory.createOption("Most of the time", 4);
			appointmentAvoidance.addOption(usually);
		}

		NarrativeEntry collaboration = factory.createNarrativeEntry(
                "Collaboration", "Collaboration");
		ses.addEntry(collaboration);
		collaboration.setSection(mainSec);
		collaboration.setLabel("2");
		{
			OptionEntry adviceResistance = factory.createOptionEntry(
                    "Appointment Difficulty",
                    "If you offer advice, does the client resist it?");
			ses.addEntry(adviceResistance);
			adviceResistance.setSection(mainSec);
			Option rarely = factory.createOption("Not at all or Rarely", 1);
			adviceResistance.addOption(rarely);
			Option sometimes = factory.createOption("Sometimes", 2);
			adviceResistance.addOption(sometimes);
			Option often = factory.createOption("Often", 3);
			adviceResistance.addOption(often);
			Option usually = factory.createOption("Most of the time", 4);
			adviceResistance.addOption(usually);
		}
		{
			OptionEntry planning = factory.createOptionEntry(
                    "Planning",
                    "The client takes an active part in the setting of goals or treatment plans");
			ses.addEntry(planning);
			planning.setSection(mainSec);
			Option rarely = factory.createOption("Not at all or Rarely", 1);
			planning.addOption(rarely);
			Option sometimes = factory.createOption("Sometimes", 2);
			planning.addOption(sometimes);
			Option often = factory.createOption("Often", 3);
			planning.addOption(often);
			Option usually = factory.createOption("Most of the time", 4);
			planning.addOption(usually);
		}
		{
			OptionEntry participation = factory.createOptionEntry(
                    "Participation",
                    "The client actively participates in managing his/her illness");
			ses.addEntry(participation);
			participation.setSection(mainSec);
			Option rarely = factory.createOption("Not at all or Rarely", 1);
			participation.addOption(rarely);
			Option sometimes = factory.createOption("Sometimes", 2);
			participation.addOption(sometimes);
			Option often = factory.createOption("Often", 3);
			participation.addOption(often);
			Option usually = factory.createOption("Most of the time", 4);
			participation.addOption(usually);
		}

		NarrativeEntry helpSeeking = factory.createNarrativeEntry(
                "Help Seeking", "Help Seeking");
		ses.addEntry(helpSeeking);
		helpSeeking.setSection(mainSec);
		helpSeeking.setLabel("3");
		{
			OptionEntry assistanceNeeded = factory.createOptionEntry(
                    "Assitance Needed",
                    "The client seeks help when assistance is needed");
			ses.addEntry(assistanceNeeded);
			assistanceNeeded.setSection(mainSec);
			Option rarely = factory.createOption("Not at all or Rarely", 1);
			assistanceNeeded.addOption(rarely);
			Option sometimes = factory.createOption("Sometimes", 2);
			assistanceNeeded.addOption(sometimes);
			Option often = factory.createOption("Often", 3);
			assistanceNeeded.addOption(often);
			Option usually = factory.createOption("Most of the time", 4);
			assistanceNeeded.addOption(usually);
		}
		{
			OptionEntry askingForHelp = factory.createOptionEntry(
                    "Asking for Help",
                    "The client finds it difficult to ask for help");
			ses.addEntry(askingForHelp);
			askingForHelp.setSection(mainSec);
			Option rarely = factory.createOption("Not at all or Rarely", 1);
			askingForHelp.addOption(rarely);
			Option sometimes = factory.createOption("Sometimes", 2);
			askingForHelp.addOption(sometimes);
			Option often = factory.createOption("Often", 3);
			askingForHelp.addOption(often);
			Option usually = factory.createOption("Most of the time", 4);
			askingForHelp.addOption(usually);
		}
		{
			OptionEntry crisisPrevention = factory.createOptionEntry(
                    "Crisis Prevention",
                    "The client seeks help to prevent a crisis");
			ses.addEntry(crisisPrevention);
			crisisPrevention.setSection(mainSec);
			Option rarely = factory.createOption("Not at all or Rarely", 1);
			crisisPrevention.addOption(rarely);
			Option sometimes = factory.createOption("Sometimes", 2);
			crisisPrevention.addOption(sometimes);
			Option often = factory.createOption("Often", 3);
			crisisPrevention.addOption(often);
			Option usually = factory.createOption("Most of the time", 4);
			crisisPrevention.addOption(usually);
		}
		{
			OptionEntry activelySeeking = factory.createOptionEntry(
                    "Actively Seeking Help",
                    "The client does not actively seek help");
			ses.addEntry(activelySeeking);
			activelySeeking.setSection(mainSec);
			Option rarely = factory.createOption("Not at all or Rarely", 1);
			activelySeeking.addOption(rarely);
			Option sometimes = factory.createOption("Sometimes", 2);
			activelySeeking.addOption(sometimes);
			Option often = factory.createOption("Often", 3);
			activelySeeking.addOption(often);
			Option usually = factory.createOption("Most of the time", 4);
			activelySeeking.addOption(usually);
		}

		NarrativeEntry treatmentAdherence = factory.createNarrativeEntry(
                "Treatment Adherence", "Treatment Adherence");
		ses.addEntry(treatmentAdherence);
		treatmentAdherence.setSection(mainSec);
		treatmentAdherence.setLabel("4");
		{
			OptionEntry medicationAgreement = factory.createOptionEntry(
                    "Medication Agreement",
                    "The client agrees to take the prescribed medication");
			ses.addEntry(medicationAgreement);
			medicationAgreement.setSection(mainSec);
			Option rarely = factory.createOption("Not at all or Rarely", 1);
			medicationAgreement.addOption(rarely);
			Option sometimes = factory.createOption("Sometimes", 2);
			medicationAgreement.addOption(sometimes);
			Option often = factory.createOption("Often", 3);
			medicationAgreement.addOption(often);
			Option usually = factory.createOption("Most of the time", 4);
			medicationAgreement.addOption(usually);
		}
		{
			OptionEntry medicationUnderstanding = factory.createOptionEntry(
                    "Medication Understanding",
                    "The client is clear about what medications he/she is taking and why");
			ses.addEntry(medicationUnderstanding);
			medicationUnderstanding.setSection(mainSec);
			Option rarely = factory.createOption("Not at all or Rarely", 1);
			medicationUnderstanding.addOption(rarely);
			Option sometimes = factory.createOption("Sometimes", 2);
			medicationUnderstanding.addOption(sometimes);
			Option often = factory.createOption("Often", 3);
			medicationUnderstanding.addOption(often);
			Option usually = factory.createOption("Most of the time", 4);
			medicationUnderstanding.addOption(usually);
		}
		{
			OptionEntry cooperation = factory.createOptionEntry(
                    "Treatment Cooperation",
                    "The client refuses to co-operate with treatment");
			ses.addEntry(cooperation);
			cooperation.setSection(mainSec);
			Option rarely = factory.createOption("Not at all or Rarely", 1);
			cooperation.addOption(rarely);
			Option sometimes = factory.createOption("Sometimes", 2);
			cooperation.addOption(sometimes);
			Option often = factory.createOption("Often", 3);
			cooperation.addOption(often);
			Option usually = factory.createOption("Most of the time", 4);
			cooperation.addOption(usually);
		}
		{
			OptionEntry adherence = factory.createOptionEntry(
                    "Medication Adherence",
                    "The client has difficulty in adhering to the prescribed medication");
			ses.addEntry(adherence);
			adherence.setSection(mainSec);
			Option rarely = factory.createOption("Not at all or Rarely", 1);
			adherence.addOption(rarely);
			Option sometimes = factory.createOption("Sometimes", 2);
			adherence.addOption(sometimes);
			Option often = factory.createOption("Often", 3);
			adherence.addOption(often);
			Option usually = factory.createOption("Most of the time", 4);
			adherence.addOption(usually);
		}

		return ses;
	}
}

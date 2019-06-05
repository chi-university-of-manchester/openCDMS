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

import java.util.ArrayList;
import java.util.List;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.AssessmentForm;

public class PADS10 extends AssessmentForm {

	public static Document createDocument(Factory factory) {

		Document pads10 = factory.createDocument("PADS-10",
                "PADS-10 Questionnaire");

		createDocumentStatuses(factory, pads10);

		// main section
		Section mainSection = factory.createSection("Main section occurrence");
		pads10.addSection(mainSection);
		mainSection.setDisplayText("Main");
		SectionOccurrence mainSectionOcc = factory
				.createSectionOccurrence("Main Section Occurrence");
		mainSection.addOccurrence(mainSectionOcc);

		NarrativeEntry pads10Instructions = factory
				.createNarrativeEntry("pads10instructions");
		pads10.addEntry(pads10Instructions);
		pads10Instructions.setSection(mainSection);
		pads10Instructions
				.setDisplayText("Please read each of the following statements " +
						"carefully and indicate the extent to which they are true " +
						"or false by selecting a response.");

		List<String> questions = new ArrayList<String>();
		questions.add("There are times when I worry that others might be plotting against me.");
		questions.add("I often find it hard to think of anything other than the negative ideas " +
				"others have about me.");
		questions.add("My friends often tell me to relax and stop worrying about being deceived or harmed");
		questions.add("Every time I meet someone for the first time, I'm afraid they've already heard bad things about me.");
		questions.add("I'm often suspicious of other people's intentions towards me.");
		questions.add("Sometimes, I just know that people are talking critically about me.");
		questions.add("There are people who think of me as a bad person.");
		questions.add("People will almost certainly lie to me.");
		questions.add("I believe that some people want to hurt me deliberately.");
		questions.add("You should only trust yourself.");

		List<String> sub_questions = new ArrayList<String>();
		sub_questions.add("Do you feel like you deserve others to plot against you?");
		sub_questions.add("Do you feel like you deserve people to have negative ideas about you?");
		sub_questions.add("Do you feel like you deserve being deceived or harmed?");
		sub_questions.add("Do you feel like you deserve to have people hearing bad things about you?");
		sub_questions.add("Do you feel like you deserve people having bad intentions towards you?");
		sub_questions.add("Do you feel like you deserve people to talk critically about you?");
		sub_questions.add("Do you feel like you deserve people to think of you as bad person?");
		sub_questions.add("Do you feel like you deserve people to lie to you?");
		sub_questions.add("Do you feel like you deserve people to hurt you deliberately?");
		sub_questions.add("Do you feel like you deserve to have no one you can trust?");

		int count = 1;
		for (String text : questions) {
			OptionEntry question = factory.createOptionEntry("Question "
                    + count, text);
			pads10.addEntry(question);
			question.setSection(mainSection);
			question.setLabel(count + ".");
			buildFiveOptions1(factory, question);
			OptionEntry sub_question = factory.createOptionEntry("Question "
                    + count + ".1", sub_questions.get(count - 1));
			sub_question.setEntryStatus(EntryStatus.DISABLED);
			pads10.addEntry(sub_question);
			sub_question.setSection(mainSection);
			sub_question.setLabel(count + ".1");
			buildFiveOptions2(factory, sub_question);
	        createOptionDependent(factory, question.getOption(2), sub_question);
	        createOptionDependent(factory, question.getOption(3), sub_question);
	        createOptionDependent(factory, question.getOption(4), sub_question);
			count++;
		}
		return pads10;
	}

	static void buildFiveOptions1(Factory factory, OptionEntry q) {
		Option op0 = factory.createOption("Certainly false", 0);
		q.addOption(op0);
		Option op1 = factory.createOption("Possibly false", 1);
		q.addOption(op1);
		Option op2 = factory.createOption("Unsure", 2);
		q.addOption(op2);
		Option op3 = factory.createOption("Possibly true", 3);
		q.addOption(op3);
		Option op4 = factory.createOption("Certainly true", 4);
		q.addOption(op4);
	}

	static void buildFiveOptions2(Factory factory, OptionEntry q) {
		Option op0 = factory.createOption("Not at all", 0);
		q.addOption(op0);
		Option op1 = factory.createOption("Possibly not", 1);
		q.addOption(op1);
		Option op2 = factory.createOption("Unsure", 2);
		q.addOption(op2);
		Option op3 = factory.createOption("Possibly", 3);
		q.addOption(op3);
		Option op4 = factory.createOption("Very much", 4);
		q.addOption(op4);
	}

}

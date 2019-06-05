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

import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.AssessmentForm;

public class ProcessFactors extends AssessmentForm {


	public static Document createDocument(Factory factory){

        Document doc = factory.createDocument("Process Factors",
                "Process Factors");
        createDocumentStatuses(factory, doc);

        Section mainSec = factory.createSection("Main", "Main");
        doc.addSection(mainSec);
        mainSec.setDisplayText("Main");
        SectionOccurrence mainOcc = factory.createSectionOccurrence("Main section occurrence");
        mainSec.addOccurrence(mainOcc);


        OptionEntry generalFeeling = factory.createOptionEntry("General feeling",
                "Thinking about your living circumstances, relationships" +
                        " and social and work life, which statment best describes" +
                        " your feelings about your way of living generally?");
		doc.addEntry(generalFeeling);
		generalFeeling.setSection(mainSec);
		generalFeeling.setLabel("1");
		generalFeeling.setOptionCodesDisplayed(true);
		Option generalFeeling1 = factory.createOption("I haven't given my way of living any thought at all", 0);
		Option generalFeeling2 = factory.createOption("I think about improving my way of living from time to time" +
                " and then put the matter out of mind", 1);
		Option generalFeeling3 = factory.createOption("I keep meaning to do something about my way of living but"
                + " don't actually get around to it", 2);
		Option generalFeeling4 = factory.createOption("From time to time I try to do something to improve"
                + " my way of living but at other times go back to doing the same"
                + " old things", 3);
		Option generalFeeling5 = factory.createOption("I have been consciously trying to improve my way of living"
                + " for the last 6 weeks", 4);
		Option generalFeeling6 = factory.createOption("I have been consciously trying to improve my way of living for longer"
                + " than the last 6 weeks. ", 5);

		generalFeeling.addOption(generalFeeling1);
		generalFeeling.addOption(generalFeeling2);
		generalFeeling.addOption(generalFeeling3);
		generalFeeling.addOption(generalFeeling4);
		generalFeeling.addOption(generalFeeling5);
		generalFeeling.addOption(generalFeeling6);

		OptionEntry talkingFeelings = factory.createOptionEntry("Talking Feeling",
                " How do you feel talking about your thoughts and feelings"
                        + " including the troubling ones?");
		doc.addEntry(talkingFeelings);
		talkingFeelings.setLabel("2");
		talkingFeelings.setSection(mainSec);
		talkingFeelings.setOptionCodesDisplayed(true);

		Option talkingFeelings1 = factory.createOption("I am very comfortable describing what I think and feel", 1);
		Option talkingFeelings2 = factory.createOption("I feel a bit umcomfortable describing what I think and feel", 2);
		Option talkingFeelings3 = factory.createOption("I find it quite difficult to describe what I think and feel", 3);
		Option talkingFeelings4 = factory.createOption("I find it very difficult to describe what I think and feel", 4);
		Option talkingFeelings5 = factory.createOption("I am unable to describe what I think and feel", 5);

		talkingFeelings.addOption(talkingFeelings1);
		talkingFeelings.addOption(talkingFeelings2);
		talkingFeelings.addOption(talkingFeelings3);
		talkingFeelings.addOption(talkingFeelings4);
		talkingFeelings.addOption(talkingFeelings5);

		OptionEntry outsideFamily = factory.createOptionEntry("Outside Family",
                "How do you feel about being with other people outside your"
                        + " family and friends?");
		doc.addEntry(outsideFamily);
		outsideFamily.setLabel("3");
		outsideFamily.setSection(mainSec);
		outsideFamily.setOptionCodesDisplayed(true);

		Option outsideFamily1 = factory.createOption("I am very comfortable being around other people", 1);
		Option outsideFamily2 = factory.createOption("I feel a bit uncomfortable being around other people", 2);
		Option outsideFamily3 = factory.createOption("I find it quite difficult to be with other people", 3);
		Option outsideFamily4 = factory.createOption("I find it very difficult to be with other people", 4);
		Option outsideFamily5 = factory.createOption("I am unable to be around other people", 5);

		outsideFamily.addOption(outsideFamily1);
		outsideFamily.addOption(outsideFamily2);
		outsideFamily.addOption(outsideFamily3);
		outsideFamily.addOption(outsideFamily4);
		outsideFamily.addOption(outsideFamily5);

		OptionEntry creativity = factory.createOptionEntry("Creativity",
                "Some people enjoy being creative, experimenting, making and"
                        + " designing things, and using their imagination more than"
                        + " others.  Which of these statements best describes you?");
		doc.addEntry(creativity);
		creativity.setLabel("4");
		creativity.setSection(mainSec);
		creativity.setOptionCodesDisplayed(true);

		Option creativity1 = factory.createOption("I like being creative and make opportunities to do creative"
                + " things in every day life", 1);
		Option creativity2 = factory.createOption("I like being creative and will do something creative if"
                + " the opportunity arises", 2);
		Option creativity3 = factory.createOption("I might participate in creative activites but could take"
                + " or leave it", 3);
		Option creativity4 = factory.createOption("I would rather not do creative things and prefer to spend"
                + " my time in other ways", 4);
		Option creativity5 = factory.createOption("I avoid doing creative thigns", 5);

		creativity.addOption(creativity1);
		creativity.addOption(creativity2);
		creativity.addOption(creativity3);
		creativity.addOption(creativity4);
		creativity.addOption(creativity5);

		return doc;
	}


}
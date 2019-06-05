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

public class BAPS extends AssessmentForm {

    public static Document createDocument(Factory factory) {

        Document baps = factory.createDocument("BAPS",
                "BAPS Questionnaire");

        createDocumentStatuses(factory, baps);

        // main section
        Section mainSection = factory.createSection("Main section occurrence");
        baps.addSection(mainSection);
        mainSection.setDisplayText("Main");
        SectionOccurrence mainSectionOcc = factory.createSectionOccurrence("Main Section Occurrence");
        mainSection.addOccurrence(mainSectionOcc);

        NarrativeEntry bapsInstructions = factory.createNarrativeEntry("BAPSinstructions");
        baps.addEntry(bapsInstructions);
        bapsInstructions.setSection(mainSection);
        bapsInstructions.setDisplayText("The experience of feeling paranoid is a common one. " +
        		"It is partciularly common when under stress. Listed below are a number of attitudes " +
        		"and thoughts that people have expressed about paranoia. " +
        		"There are no right or wrong answers. Please give a response about how you feel generally." +
        		"\n\nPlease read each statement and then circle the number that " +
        		"corresponds to how much you believe this. " +
        		"Please give a response to all statements");

        List<String> questions = new ArrayList<String>();
        questions.add("My paranoia gets out of control");
        questions.add("I get upset when I feel paranoid");
        questions.add("It is important to be paranoid");
        questions.add("If I were not paranoid others would take advantage of me");
        questions.add("It is safer to be paranoid");
        questions.add("Everybody feels paranoid at sometime or other");
        questions.add("My paranoia prevents me from doing things I enjoy");
        questions.add("Most people get paranoid sometimes");
        questions.add("My paranoid thoughts worry me");
        questions.add("Paranoia is normal");
        questions.add("My paranoia keeps me on my toes");
        questions.add("Being paranoid keeps me sharp");
        questions.add("Everybody is paranoid on some level");
        questions.add("My paranoia gets exaggerated");
        questions.add("My paranoia protects me");
        questions.add("Paranoia is something everybody has to some extent");
        questions.add("Being paranoid is just human nature");
        questions.add("My paranoia distresses me");

        int count = 1;
        for (String text : questions) {
			OptionEntry question = factory
					.createOptionEntry("Question " + count, text);
			baps.addEntry(question);
			question.setSection(mainSection);
			question.setLabel(count+".");
			buildFourOptions(factory, question);
			count++;
		}
        return baps;
    }
    static void buildFourOptions(Factory factory, OptionEntry q){
        Option op0 = factory.createOption("Not at all", 1);
        q.addOption(op0);
        Option op1 = factory.createOption("Somewhat", 2);
        q.addOption(op1);
        Option op2 = factory.createOption("Moderately so", 3);
        q.addOption(op2);
        Option op3 =  factory.createOption("Very much so", 4);
        q.addOption(op3);
    }

}

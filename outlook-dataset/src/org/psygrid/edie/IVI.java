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

public class IVI extends AssessmentForm {

    public static Document createDocument(Factory factory) {

        Document ivi = factory.createDocument("IVI",
                "IVI Questionnaire");

        createDocumentStatuses(factory, ivi);

        // main section
        Section mainSection = factory.createSection("Main section occurrence");
        ivi.addSection(mainSection);
        mainSection.setDisplayText("Main");
        SectionOccurrence mainSectionOcc = factory.createSectionOccurrence("Main Section Occurrence");
        mainSection.addOccurrence(mainSectionOcc);

        NarrativeEntry iviInstructions = factory.createNarrativeEntry("IVIinstructions");
        ivi.addEntry(iviInstructions);
        iviInstructions.setSection(mainSection);
        iviInstructions.setDisplayText("The experience of hearing sounds and voices " +
        		"when there is nothing to explain it is a common one. " +
        		"It is particularly common when under stress, falling asleep or waking up. " +
        		"Listed below are a number of attitudes and thoguhts that people have expressed " +
        		"about hearing unexpected sounds or voices. " +
        		"There are no right or wrong answers. " +
        		"Please give a response about how you generally feel." +
        		"\n\nPlease read each statement and then circle the number that " +
        		"corresponds to how much you believe this. " +
        		"Please give a response to all statements");

        List<String> questions = new ArrayList<String>();
        questions.add("They are a sign that I am being punished.");
        questions.add("They help me keep control.");
        questions.add("They would make me harm someone.");
        questions.add("They mean I have done something bad.");
        questions.add("They mean I am close to God.");
        questions.add("They mean I will do bad things.");
        questions.add("They allow me to help others.");
        questions.add("They mean that I have been chosen.");
        questions.add("They make me important.");
        questions.add("They will make me go crazy.");
        questions.add("They mean I will lose control of my behaviour.");
        questions.add("They will take over my mind.");
        questions.add("They have come from the spiritual world.");
        questions.add("They are a sign that I am evil.");
        questions.add("They will harm me physically.");
        questions.add("They mean I am possessed.");
        questions.add("They have to be obeyed.");
        questions.add("They make me special.");
        questions.add("They help me cope.");
        questions.add("They keep me company.");
        questions.add("I would not cope without them.");
        questions.add("They mean I will harm myself.");
        questions.add("They control the way I think.");
        questions.add("They protect me.");
        questions.add("If I do not obey them, something bad will happen.");
        questions.add("They mean I am a bad person.");

        int count = 1;
        for (String text : questions) {
			OptionEntry question = factory
					.createOptionEntry("Question " + count, text);
			ivi.addEntry(question);
			question.setSection(mainSection);
			question.setLabel(count+".");
			buildFourOptions(factory, question);
			count++;
		}
        return ivi;
    }
    static void buildFourOptions(Factory factory, OptionEntry q){
        Option op0 = factory.createOption("Not at all", 1);
        q.addOption(op0);
        Option op1 = factory.createOption("Some what", 2);
        q.addOption(op1);
        Option op2 = factory.createOption("Moderately so", 3);
        q.addOption(op2);
        Option op3 =  factory.createOption("Very much", 4);
        q.addOption(op3);
    }

}

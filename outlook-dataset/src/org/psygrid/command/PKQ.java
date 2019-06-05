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


package org.psygrid.command;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.AssessmentForm;

/**
 * @author Rob Harper
 *
 */
public class PKQ extends AssessmentForm {

    public static Document createDocument(Factory factory){

    	Document doc = factory.createDocument("PKQ", "Personal Knowledge Questionnaire");
    	createDocumentStatuses(factory, doc);

        Section mainSec = factory.createSection("Main Section", "Main");
        doc.addSection(mainSec);
        SectionOccurrence mainSecOcc = factory.createSectionOccurrence("Main Section Occ");
        mainSec.addOccurrence(mainSecOcc);

        NarrativeEntry q0 = factory.createNarrativeEntry("Narrative", "My voice has talked about...");
        doc.addEntry(q0);
        q0.setSection(mainSec);

        OptionEntry q1 = factory.createOptionEntry("What's going through my mind", "What's going through my mind");
        doc.addEntry(q1);
        q1.setSection(mainSec);
        q1.setLabel("1");
        createOptions(factory, q1, new String[]{"Yes", "No"}, new int[]{1,0});

        OptionEntry q2 = factory.createOptionEntry("My weaknesses", "My weaknesses");
        doc.addEntry(q2);
        q2.setSection(mainSec);
        q2.setLabel("2");
        createOptions(factory, q2, new String[]{"Yes", "No"}, new int[]{1,0});

        OptionEntry q3 = factory.createOptionEntry("How I'm feeling", "How I'm feeling");
        doc.addEntry(q3);
        q3.setSection(mainSec);
        q3.setLabel("3");
        createOptions(factory, q3, new String[]{"Yes", "No"}, new int[]{1,0});

        OptionEntry q4 = factory.createOptionEntry("Bad things that will happen to me in the future", "Bad things that will happen to me in the future");
        doc.addEntry(q4);
        q4.setSection(mainSec);
        q4.setLabel("4");
        createOptions(factory, q4, new String[]{"Yes", "No"}, new int[]{1,0});

        OptionEntry q5 = factory.createOptionEntry("Good things happened in past", "Good things that have happened in my past");
        doc.addEntry(q5);
        q5.setSection(mainSec);
        q5.setLabel("5");
        createOptions(factory, q5, new String[]{"Yes", "No"}, new int[]{1,0});

        OptionEntry q6 = factory.createOptionEntry("Things that I fear", "Things that I fear most or wouldn't want to happen");
        doc.addEntry(q6);
        q6.setSection(mainSec);
        q6.setLabel("6");
        createOptions(factory, q6, new String[]{"Yes", "No"}, new int[]{1,0});

        OptionEntry q7 = factory.createOptionEntry("Bad things in past", "Bad things I have done in my past");
        doc.addEntry(q7);
        q7.setSection(mainSec);
        q7.setLabel("7");
        createOptions(factory, q7, new String[]{"Yes", "No"}, new int[]{1,0});

        OptionEntry q8 = factory.createOptionEntry("What I am doing", "What I am doing");
        doc.addEntry(q8);
        q8.setSection(mainSec);
        q8.setLabel("8");
        createOptions(factory, q8, new String[]{"Yes", "No"}, new int[]{1,0});

        OptionEntry q9 = factory.createOptionEntry("Bad things that will happen in my future", "Bad things that will happen in my future");
        doc.addEntry(q9);
        q9.setSection(mainSec);
        q9.setLabel("9");
        createOptions(factory, q9, new String[]{"Yes", "No"}, new int[]{1,0});

        OptionEntry q10 = factory.createOptionEntry("Good things done in past", "Good things I have done in the past");
        doc.addEntry(q10);
        q10.setSection(mainSec);
        q10.setLabel("10");
        createOptions(factory, q10, new String[]{"Yes", "No"}, new int[]{1,0});

        OptionEntry q11 = factory.createOptionEntry("Bad things in past", "Bad things that have happened in my past");
        doc.addEntry(q11);
        q11.setSection(mainSec);
        q11.setLabel("11");
        createOptions(factory, q11, new String[]{"Yes", "No"}, new int[]{1,0});

        OptionEntry q12 = factory.createOptionEntry("Things I am thinking", "Things I am thinking");
        doc.addEntry(q12);
        q12.setSection(mainSec);
        q12.setLabel("12");
        createOptions(factory, q12, new String[]{"Yes", "No"}, new int[]{1,0});

        OptionEntry q13 = factory.createOptionEntry("What I'm about to do", "What I'm about to do");
        doc.addEntry(q13);
        q13.setSection(mainSec);
        q13.setLabel("13");
        createOptions(factory, q13, new String[]{"Yes", "No"}, new int[]{1,0});

        OptionEntry q14 = factory.createOptionEntry("What the future holds", "What the future holds");
        doc.addEntry(q14);
        q14.setSection(mainSec);
        q14.setLabel("14");
        createOptions(factory, q14, new String[]{"Yes", "No"}, new int[]{1,0});

        OptionEntry q15 = factory.createOptionEntry("What will wind me up", "What will wind me up or make me angry");
        doc.addEntry(q15);
        q15.setSection(mainSec);
        q15.setLabel("15");
        createOptions(factory, q15, new String[]{"Yes", "No"}, new int[]{1,0});

        return doc;
    }
}

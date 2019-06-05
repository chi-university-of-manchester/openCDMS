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
public class VPD extends AssessmentForm {

    public static Document createDocument(Factory factory){

    	Document doc = factory.createDocument("VPD", "Voice Power Differential Scale");
    	createDocumentStatuses(factory, doc);

        Section mainSec = factory.createSection("Main Section", "Main");
        doc.addSection(mainSec);
        SectionOccurrence mainSecOcc = factory.createSectionOccurrence("Main Section Occ");
        mainSec.addOccurrence(mainSecOcc);

        NarrativeEntry q0 = factory.createNarrativeEntry("Narrative", "Please select the option which best describes how you feel in relation to your voice");
        doc.addEntry(q0);
        q0.setSection(mainSec);

        TextEntry q0a = factory.createTextEntry("Name of voice(s)", "Name or description of voice(s)");
        doc.addEntry(q0a);
        q0a.setSection(mainSec);

        OptionEntry q1 = factory.createOptionEntry("Power", "Power");
        doc.addEntry(q1);
        q1.setSection(mainSec);
        createOptions(factory, q1,
        		new String[]{"I am much more powerful than my voice",
        					 "I am more powerful than my voice",
        					 "We have about the same amount of power as each other",
        					 "My voice is more powerful than me",
        					 "My voice is much more powerful than me"},
        		new int[]{1,2,3,4,5});

        OptionEntry q2 = factory.createOptionEntry("Strength", "Strength");
        doc.addEntry(q2);
        q2.setSection(mainSec);
        createOptions(factory, q2,
        		new String[]{"I am much stronger than my voice",
        					 "I am stronger than my voice",
        					 "We are as strong as each other",
        					 "My voice is stronger than me",
        					 "My voice is much stronger than me"},
        		new int[]{1,2,3,4,5});

        OptionEntry q3 = factory.createOptionEntry("Confidence", "Confidence");
        doc.addEntry(q3);
        q3.setSection(mainSec);
        createOptions(factory, q3,
        		new String[]{"I am much more confident than my voice",
        					 "I am more confident than my voice",
        					 "We are as confident  as each other",
        					 "My voice is more confident than me",
        					 "My voice is much more confident than me"},
        		new int[]{1,2,3,4,5});

        OptionEntry q4 = factory.createOptionEntry("Respect", "Respect");
        doc.addEntry(q4);
        q4.setSection(mainSec);
        createOptions(factory, q4,
        		new String[]{"I respect my voice much more than it respects me",
        					 "I respect my voice more than it respects me",
        					 "We respect each other about the same",
        					 "My voice respects me more than I respect it",
        					 "My voice respects me much more than I respect it"},
        		new int[]{5,4,3,2,1});

        OptionEntry q5 = factory.createOptionEntry("Harm", "Harm");
        doc.addEntry(q5);
        q5.setSection(mainSec);
        createOptions(factory, q5,
        		new String[]{"I am much more able to harm my voice than it is able to harm me",
        					 "I am more able to harm my voice than it is able to harm me",
        					 "We are equally able to harm each other",
        					 "My voice is more able to harm me than I am able to harm it",
        					 "My voice is much more able to harm me than I am able to harm it"},
        		new int[]{1,2,3,4,5});

        OptionEntry q6 = factory.createOptionEntry("Superiority", "Superiority");
        doc.addEntry(q6);
        q6.setSection(mainSec);
        createOptions(factory, q6,
        		new String[]{"I am greatly  superior to my voice",
        					 "I am superior to my voice",
        					 "We are equal to each other",
        					 "My voice is superior to me",
        					 "My voice is greatly superior to met"},
        		new int[]{1,2,3,4,5});

        OptionEntry q7 = factory.createOptionEntry("Knowledge", "Knowledge");
        doc.addEntry(q7);
        q7.setSection(mainSec);
        createOptions(factory, q7,
        		new String[]{"I am much more knowledgeable than my voice",
        					 "I am more knowledgeable than my voice",
        					 "We have about the same amount of knowledge as each other",
        					 "My voice is more knowledgeable than me",
        					 "My voice is much more knowledgeable than me"},
        		new int[]{1,2,3,4,5});

        return doc;
    }

}

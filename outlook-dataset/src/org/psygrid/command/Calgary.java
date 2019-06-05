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

import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.Factory;
import org.psygrid.data.model.hibernate.OptionEntry;
import org.psygrid.data.model.hibernate.Section;
import org.psygrid.data.model.hibernate.SectionOccurrence;
import org.psygrid.outlook.AssessmentForm;

/**
 * @author Rob Harper
 *
 */
public class Calgary extends AssessmentForm {

    public static Document createDocument(Factory factory) {

        Document doc = factory.createDocument(
                "Calgary",
                "Calgary Depression Scale");

        createDocumentStatuses(factory, doc);

        Section mainSec = factory.createSection("Main Section", "Main");
        doc.addSection(mainSec);
        SectionOccurrence mainSecOcc = factory.createSectionOccurrence("Main Sec Occ");
        mainSec.addOccurrence(mainSecOcc);

        OptionEntry q1 = factory.createOptionEntry("Depression", "Depression");
        doc.addEntry(q1);
        q1.setSection(mainSec);
        q1.setLabel("1");
        q1.setDescription("How would you describe your mood over the last two weeks? " +
        		"Do you keep reasonably cheerful or have you been very depressed or low spirited recently? " +
        		"In the last two weeks how often have you (own words) every day? all day?");
        createOptions(factory, q1,
        		new String[]{"Absent", "Mild", "Moderate", "Severe"},
        		new int[]{0,1,2,3});

        OptionEntry q2 = factory.createOptionEntry("Hopelessness", "Hopelessness");
        doc.addEntry(q2);
        q2.setSection(mainSec);
        q2.setLabel("2");
        q2.setDescription("How do you see the future for yourself? " +
        		"Can you see any future or has life seemed quite hopeless? " +
        		"Have you given up or does there still seem some reason for trying?");
        createOptions(factory, q2,
        		new String[]{"Absent", "Mild", "Moderate", "Severe"},
        		new int[]{0,1,2,3});

        OptionEntry q3 = factory.createOptionEntry("Self-depreciation", "Self-depreciation");
        doc.addEntry(q3);
        q3.setSection(mainSec);
        q3.setLabel("3");
        q3.setDescription("What is your opinion of yourself compared to other people? " +
        		"Do you feel better or not as good or about the same as most? " +
        		"Do you feel inferior or even worthless?");
        createOptions(factory, q3,
        		new String[]{"Absent", "Mild", "Moderate", "Severe"},
        		new int[]{0,1,2,3});

        OptionEntry q4 = factory.createOptionEntry("Guilty ideas of reference", "Guilty ideas of reference");
        doc.addEntry(q4);
        q4.setSection(mainSec);
        q4.setLabel("4");
        q4.setDescription("Do you have the feeling that you are being blamed for something or even wrongly accused? " +
        		"What about ? (Do not include justifiable blame or accusations; exclude delusions of guilt).");
        createOptions(factory, q4,
        		new String[]{"Absent", "Mild", "Moderate", "Severe"},
        		new int[]{0,1,2,3});

        OptionEntry q5 = factory.createOptionEntry("Pathlogical Guilt", "Pathlogical Guilt");
        doc.addEntry(q5);
        q5.setSection(mainSec);
        q5.setLabel("5");
        q5.setDescription("Do you tend to blame yourself for little things you may have done in the past? " +
        		"Do you think you deserve to be so concerned about this?");
        createOptions(factory, q5,
        		new String[]{"Absent", "Mild", "Moderate", "Severe"},
        		new int[]{0,1,2,3});

        OptionEntry q6 = factory.createOptionEntry("Morning depression", "Morning depression");
        doc.addEntry(q6);
        q6.setSection(mainSec);
        q6.setLabel("6");
        q6.setDescription("When you have felt depressed over the last two weeks, have you noticed the " +
        		"depression being worse at any particular time of day?");
        createOptions(factory, q6,
        		new String[]{"Absent", "Mild", "Moderate", "Severe"},
        		new int[]{0,1,2,3});

        OptionEntry q7 = factory.createOptionEntry("Early wakening", "Early wakening");
        doc.addEntry(q7);
        q7.setSection(mainSec);
        q7.setLabel("7");
        q7.setDescription("Do you wake earlier in the morning than is normal for you? How many times a week does this happen?");
        createOptions(factory, q7,
        		new String[]{"Absent", "Mild", "Moderate", "Severe"},
        		new int[]{0,1,2,3});

        OptionEntry q8 = factory.createOptionEntry("Suicide", "Suicide");
        doc.addEntry(q8);
        q8.setSection(mainSec);
        q8.setLabel("8");
        q8.setDescription("Have you felt that life wasn’t worth living? Did you ever " +
        		"feel like ending it all? What did you think you might do? Did you actually try?");
        createOptions(factory, q8,
        		new String[]{"Absent", "Mild", "Moderate", "Severe"},
        		new int[]{0,1,2,3});

        OptionEntry q9 = factory.createOptionEntry("Observed depression", "Observed depression");
        doc.addEntry(q9);
        q9.setSection(mainSec);
        q9.setLabel("9");
        q9.setDescription("Based on interviewer’s observations during the entire interview. " +
        		"The question 'do you feel like crying?' used at an appropriate point in the interview, " +
        		"may elicit information useful to this observation.");
        createOptions(factory, q9,
        		new String[]{"Absent", "Mild", "Moderate", "Severe"},
        		new int[]{0,1,2,3});

        return doc;
    }
}

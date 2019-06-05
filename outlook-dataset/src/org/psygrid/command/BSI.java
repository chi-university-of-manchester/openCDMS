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

import org.psygrid.common.ValidationRulesWrapper;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.AssessmentForm;

/**
 * @author Rob Harper
 *
 */
public class BSI extends AssessmentForm {

    public static Document createDocument(Factory factory){

    	Document doc = factory.createDocument("BSI", "Beck Scale for Suicide Ideation");
    	createDocumentStatuses(factory, doc);

        Section mainSec = factory.createSection("Main Section", "Main");
        doc.addSection(mainSec);
        SectionOccurrence mainSecOcc = factory.createSectionOccurrence("Main Section Occ");
        mainSec.addOccurrence(mainSecOcc);

        NarrativeEntry q0 = factory.createNarrativeEntry("Directions",
        		"Directions: Please carefully read each group of statements below. " +
        		"Circle the one statement in each group that best describes how you have been " +
        		"feeling for the past week, including today. Be sure to read all of the " +
        		"statements in each group before making a choice.");
        doc.addEntry(q0);
        q0.setSection(mainSec);

        OptionEntry q1 = factory.createOptionEntry("Wish to live", "Wish to live");
        doc.addEntry(q1);
        q1.setSection(mainSec);
        q1.setLabel("1");
        createOptions(factory, q1,
        		new String[]{"I have a moderate wish to live",
        					 "I have a weak wish to live",
        					 "I have no wish to live"},
        		new int[]{0,1,2});

        OptionEntry q2 = factory.createOptionEntry("Wish to die", "Wish to die");
        doc.addEntry(q2);
        q2.setSection(mainSec);
        q2.setLabel("2");
        createOptions(factory, q2,
        		new String[]{"I have no wish to die",
        					 "I have a weak wish to die",
        					 "I have a moderate wish to strong wish to die"},
        		new int[]{0,1,2});

        OptionEntry q3 = factory.createOptionEntry("Reasons for living", "Reasons for living");
        doc.addEntry(q3);
        q3.setSection(mainSec);
        q3.setLabel("3");
        createOptions(factory, q3,
        		new String[]{"My reasons for living outweigh my reasons for dying",
        					 "My reasons for living or dying are about equal",
        					 "My reasons for dying outweigh my reasons for living"},
        		new int[]{0,1,2});

        OptionEntry q4 = factory.createOptionEntry("Desire to kill myself", "Desire to kill myself");
        doc.addEntry(q4);
        q4.setSection(mainSec);
        q4.setLabel("4");
        createOptions(factory, q4,
        		new String[]{"I have no desire to kill myself",
        					 "I have a weak desire to kill myself",
        					 "I have a moderate to strong desire to kill myself"},
        		new int[]{0,1,2});

        OptionEntry q5 = factory.createOptionEntry("Save my life or not", "Save my life or not");
        doc.addEntry(q5);
        q5.setSection(mainSec);
        q5.setLabel("5");
        createOptions(factory, q5,
        		new String[]{"I would try to save my life if I found myself in a life-threatening situation",
        					 "I would take a chance on life or death if I found myself in a life-threatening situation",
        					 "I would not take the steps necessary to avoid death if I found myself in a life-threatening situation."},
        		new int[]{0,1,2});

        OptionEntry zeroFor4And5 = factory.createOptionEntry("0 for 4 and 5?", "Did you select option 0 for both questions 4 and 5 above?");
        doc.addEntry(zeroFor4And5);
        zeroFor4And5.setSection(mainSec);
        createOptions(factory, zeroFor4And5, new String[]{"Yes", "No"});
        Option zeroFor4And5No = zeroFor4And5.getOption(1);

        OptionEntry q6 = factory.createOptionEntry("Thinking about killing myself", "Thinking about killing myself", EntryStatus.DISABLED);
        doc.addEntry(q6);
        q6.setSection(mainSec);
        q6.setLabel("6");
        createOptions(factory, q6,
        		new String[]{"I have brief periods of thinking about killing myself which pass quickly",
        					 "I have periods of thinking about killing myself which last for moderate amounts of time",
        					 "I have long periods of thinking about killing myself.."},
        		new int[]{0,1,2});
        createOptionDependent(factory, zeroFor4And5No, q6);

        OptionEntry q7 = factory.createOptionEntry("Frequency", "Frequency of thinking about killing myself", EntryStatus.DISABLED);
        doc.addEntry(q7);
        q7.setSection(mainSec);
        q7.setLabel("7");
        createOptions(factory, q7,
        		new String[]{"I rarely or only occasionally think about killing myself",
        					 "I have frequent thoughts about killing myself",
        					 "I continuously think about killing myself"},
        		new int[]{0,1,2});
        createOptionDependent(factory, zeroFor4And5No, q7);

        OptionEntry q8 = factory.createOptionEntry("Accepting the idea of killing myself", "Accepting the idea of killing myself", EntryStatus.DISABLED);
        doc.addEntry(q8);
        q8.setSection(mainSec);
        q8.setLabel("8");
        createOptions(factory, q8,
        		new String[]{"I do not accept the idea of killing myself",
        					 "I neither accept nor reject the idea of killing myself",
        					 "I accept the idea of killing myself"},
        		new int[]{0,1,2});
        createOptionDependent(factory, zeroFor4And5No, q8);

        OptionEntry q9 = factory.createOptionEntry("Keeping myself from committing suicide", "Keeping myself from committing suicide", EntryStatus.DISABLED);
        doc.addEntry(q9);
        q9.setSection(mainSec);
        q9.setLabel("9");
        createOptions(factory, q9,
        		new String[]{"I can keep myself from committing suicide",
        					 "I am unsure that I can keep myself from committing suicide",
        					 "I cannot keep myself from committing suicide"},
        		new int[]{0,1,2});
        createOptionDependent(factory, zeroFor4And5No, q9);

        OptionEntry q10 = factory.createOptionEntry("Family, friends, religion, possible injury", "Family, friends, religion, possible injury", EntryStatus.DISABLED);
        doc.addEntry(q10);
        q10.setSection(mainSec);
        q10.setLabel("10");
        createOptions(factory, q10,
        		new String[]{"I would not kill myself because of my family, friends, religion, possible injury from an unsuccessful attempt etc.",
        					 "I am somewhat concerned about killing myself because of my family, friends religion, " +
        					 	"possible injury from an unsuccessful attempt etc.",
        					 "I am not or only a little concerned about killing myself because of my family, friends, religion, " +
        					 	"possible injury from an unsuccessful attempt"},
        		new int[]{0,1,2});
        createOptionDependent(factory, zeroFor4And5No, q10);

        OptionEntry q11 = factory.createOptionEntry("Reasons for wanting to commit suicide", "Reasons for wanting to commit suicide", EntryStatus.DISABLED);
        doc.addEntry(q11);
        q11.setSection(mainSec);
        q11.setLabel("11");
        createOptions(factory, q11,
        		new String[]{"My reasons for wanting to commit suicide are primarily aimed at influencing other people, " +
        						"such as getting even with people, making people happier making people pay attention to me etc.",
        					 "My reasons for wanting to commit suicide are not only aimed at influencing other people, " +
        					 	"such as getting even with people, but also represent a way of solving my problems",
        					 "My reasons for wanting to commit suicide are primarily based upon escaping from my problems."},
        		new int[]{0,1,2});
        createOptionDependent(factory, zeroFor4And5No, q11);

        OptionEntry q12 = factory.createOptionEntry("Plan about how to kill myself", "Plan about how to kill myself", EntryStatus.DISABLED);
        doc.addEntry(q12);
        q12.setSection(mainSec);
        q12.setLabel("12");
        createOptions(factory, q12,
        		new String[]{"I have no specific plan about how to kill myself",
        					 "I have considered ways of killing myself, but have not worked out the details",
        					 "I have a specific plan for killing myself"},
        		new int[]{0,1,2});
        createOptionDependent(factory, zeroFor4And5No, q12);

        OptionEntry q13 = factory.createOptionEntry("Method and opportunity to kill myself", "Method and opportunity to kill myself", EntryStatus.DISABLED);
        doc.addEntry(q13);
        q13.setSection(mainSec);
        q13.setLabel("13");
        createOptions(factory, q13,
        		new String[]{"I do not have access to a method or an opportunity to kill myself",
        					 "The method that I would use for committing suicide takes time, " +
        					 	"and I really do not have a good opportunity to use this method.",
        					 "I have access or anticipate having access to the method that I would " +
        					 	"choose for killing myself and also have or shall have the opportunity to use it."},
        		new int[]{0,1,2});
        createOptionDependent(factory, zeroFor4And5No, q13);

        OptionEntry q14 = factory.createOptionEntry("Courage and ability to commit suicide", "Courage and ability to commit suicide", EntryStatus.DISABLED);
        doc.addEntry(q14);
        q14.setSection(mainSec);
        q14.setLabel("14");
        createOptions(factory, q14,
        		new String[]{"I do not have the courage or the ability to commit suicide",
        					 "I am unsure that I have the courage or the ability to commit suicide",
        					 "I have the courage and the ability to commit suicide"},
        		new int[]{0,1,2});
        createOptionDependent(factory, zeroFor4And5No, q14);

        OptionEntry q15 = factory.createOptionEntry("Certainty of making a suicide attempt", "Certainty of making a suicide attempt", EntryStatus.DISABLED);
        doc.addEntry(q15);
        q15.setSection(mainSec);
        q15.setLabel("15");
        createOptions(factory, q15,
        		new String[]{"I do not expect to make a suicide attempt",
        					 "I am unsure that I shall make a suicide attempt",
        					 "I am sure that I shall make a suicide attempt"},
        		new int[]{0,1,2});
        createOptionDependent(factory, zeroFor4And5No, q15);

        OptionEntry q16 = factory.createOptionEntry("Preparations for committing suicide", "Preparations for committing suicide", EntryStatus.DISABLED);
        doc.addEntry(q16);
        q16.setSection(mainSec);
        q16.setLabel("16");
        createOptions(factory, q16,
        		new String[]{"I have made no preparations for committing suicide",
        					 "I have made some preparations for committing suicide",
        					 "I have almost finished or completed my preparations for committing suicide."},
        		new int[]{0,1,2});
        createOptionDependent(factory, zeroFor4And5No, q16);

        OptionEntry q17 = factory.createOptionEntry("Suicide note", "Suicide note", EntryStatus.DISABLED);
        doc.addEntry(q17);
        q17.setSection(mainSec);
        q17.setLabel("17");
        createOptions(factory, q17,
        		new String[]{"I have not written a suicide note",
        					 "I have thought about writing a suicide note or have started to write one but have not completed it",
        					 "I have completed a suicide note"},
        		new int[]{0,1,2});
        createOptionDependent(factory, zeroFor4And5No, q17);

        OptionEntry q18 = factory.createOptionEntry("Arrangements for after I've commited suicide", "Arrangements for after I've commited suicide", EntryStatus.DISABLED);
        doc.addEntry(q18);
        q18.setSection(mainSec);
        q18.setLabel("18");
        createOptions(factory, q18,
        		new String[]{"I have made no arrangements for what will happen after I commit suicide",
        					 "I have thought about making some arrangements for what will happen after I have committed suicide",
        					 "I have made definite arrangements for what will happen after I have committed suicide"},
        		new int[]{0,1,2});
        createOptionDependent(factory, zeroFor4And5No, q18);

        OptionEntry q19 = factory.createOptionEntry("Hiding desire to kill myself", "Hiding desire to kill myself", EntryStatus.DISABLED);
        doc.addEntry(q19);
        q19.setSection(mainSec);
        q19.setLabel("19");
        createOptions(factory, q19,
        		new String[]{"I have not hidden my desire to kill myself from people",
        					 "I have held back from telling people about wanting to kill myself",
        					 "I have attempted to hide, conceal, or lie about wanting to commit suicide"},
        		new int[]{0,1,2});
        createOptionDependent(factory, zeroFor4And5No, q19);

        OptionEntry q20 = factory.createOptionEntry("Attempted suicide", "Attempted suicide");
        doc.addEntry(q20);
        q20.setSection(mainSec);
        q20.setLabel("20");
        createOptions(factory, q20,
        		new String[]{"I have never attempted suicide",
        					 "I have attempted suicide once",
        					 "I have attempted suicide two or more times"},
        		new int[]{0,1,2});
        Option q20Once = q20.getOption(1);
        Option q20Multiple = q20.getOption(2);

        //Create dep
        OptionEntry q21 = factory.createOptionEntry("Wish to die", "Wish to die");
        doc.addEntry(q21);
        q21.setSection(mainSec);
        q21.setLabel("21");
        createOptions(factory, q21,
        		new String[]{"My wish to die during the last suicide attempt was low",
        					 "My wish to die during the last suicide attempt was moderate",
        					 "My wish to die during the last suicide attempt was high"},
        		new int[]{0,1,2});
        q21.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, q20Once, q21);
        createOptionDependent(factory, q20Multiple, q21);

        DerivedEntry total = factory.createDerivedEntry("Total", "Total score for suicidal ideation");
        doc.addEntry(total);
        total.setSection(mainSec);
        total.addValidationRule(ValidationRulesWrapper.instance().getRule("Suicidal ideation total validation rule"));
        total.setFormula("a+b+c+d+e+f+g+h+i+j+k+l+m+n+o+p+q+r+s");
        total.addVariable("a", q1);
        total.addVariable("b", q2);
        total.addVariable("c", q3);
        total.addVariable("d", q4);
        total.addVariable("e", q5);
        total.addVariable("f", q6);
        total.addVariable("g", q7);
        total.addVariable("h", q8);
        total.addVariable("i", q9);
        total.addVariable("j", q10);
        total.addVariable("k", q11);
        total.addVariable("l", q12);
        total.addVariable("m", q13);
        total.addVariable("n", q14);
        total.addVariable("o", q15);
        total.addVariable("p", q16);
        total.addVariable("q", q17);
        total.addVariable("r", q18);
        total.addVariable("s", q19);

        return doc;
    }
}

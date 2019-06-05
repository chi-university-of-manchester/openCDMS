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
public class BHS extends AssessmentForm {

    public static Document createDocument(Factory factory){

    	Document doc = factory.createDocument("BHS", "Beck Hopelessness Scale");
    	createDocumentStatuses(factory, doc);

        Section mainSec = factory.createSection("Main Section", "Main");
        doc.addSection(mainSec);
        SectionOccurrence mainSecOcc = factory.createSectionOccurrence("Main Section Occ");
        mainSec.addOccurrence(mainSecOcc);

        NarrativeEntry q0 = factory.createNarrativeEntry("Narrative",
        		"This questionnaire consists of a list of 20 statements (sentences).  " +
        		"Please read the statements carefully one by one.  " +
        		"If the statement describes your attitude for the past week including today, " +
        		"select 'true'. If the statement is false for you, select 'false'.  " +
        		"Please be sure to read each statement.");
        doc.addEntry(q0);
        q0.setSection(mainSec);

        OptionEntry q1 = factory.createOptionEntry("Look forward to the future", "I look forward to the future with hope and enthusiasm");
        doc.addEntry(q1);
        q1.setSection(mainSec);
        q1.setLabel("1");
        q1.setOptionCodesDisplayed(false);
        createOptions(factory, q1, new String[]{"True", "False"}, new int[]{0,1});

        OptionEntry q2 = factory.createOptionEntry("Might as well give up", "I might as well give up because there's nothing I can do about making things better for myself");
        doc.addEntry(q2);
        q2.setSection(mainSec);
        q2.setLabel("2");
        q2.setOptionCodesDisplayed(false);
        createOptions(factory, q2, new String[]{"True", "False"}, new int[]{1,0});

        OptionEntry q3 = factory.createOptionEntry("Things are going badly, can't stay that way forever", "When things are going badly, I am helped by knowing that they can't stay that way forever");
        doc.addEntry(q3);
        q3.setSection(mainSec);
        q3.setLabel("3");
        q3.setOptionCodesDisplayed(false);
        createOptions(factory, q3, new String[]{"True", "False"}, new int[]{0,1});

        OptionEntry q4 = factory.createOptionEntry("Can't imagine what my life would be like in 10 years", "I can't imagine what my life would be like in 10 years");
        doc.addEntry(q4);
        q4.setSection(mainSec);
        q4.setLabel("4");
        q4.setOptionCodesDisplayed(false);
        createOptions(factory, q4, new String[]{"True", "False"}, new int[]{1,0});

        OptionEntry q5 = factory.createOptionEntry("Have enough time to accomplish", "I have enough time to accomplish the things I most want to do");
        doc.addEntry(q5);
        q5.setSection(mainSec);
        q5.setLabel("5");
        q5.setOptionCodesDisplayed(false);
        createOptions(factory, q5, new String[]{"True", "False"}, new int[]{0,1});

        OptionEntry q6 = factory.createOptionEntry("In future expect to succeed", "In the future you expect to succeed in what concerns me most");
        doc.addEntry(q6);
        q6.setSection(mainSec);
        q6.setLabel("6");
        q6.setOptionCodesDisplayed(false);
        createOptions(factory, q6, new String[]{"True", "False"}, new int[]{0,1});

        OptionEntry q7 = factory.createOptionEntry("My future seems dark", "My future seems dark");
        doc.addEntry(q7);
        q7.setSection(mainSec);
        q7.setLabel("7");
        q7.setOptionCodesDisplayed(false);
        createOptions(factory, q7, new String[]{"True", "False"}, new int[]{1,0});

        OptionEntry q8 = factory.createOptionEntry("Happen to be particularly lucky", "I happen to be particularly lucky and I expect to get more of the good things in life than the average person");
        doc.addEntry(q8);
        q8.setSection(mainSec);
        q8.setLabel("8");
        q8.setOptionCodesDisplayed(false);
        createOptions(factory, q8, new String[]{"True", "False"}, new int[]{0,1});

        OptionEntry q9 = factory.createOptionEntry("Just don't get the breaks", "I just don't get the breaks, and there's no reason to believe that I will in the future");
        doc.addEntry(q9);
        q9.setSection(mainSec);
        q9.setLabel("9");
        q9.setOptionCodesDisplayed(false);
        createOptions(factory, q9, new String[]{"True", "False"}, new int[]{0,1});

        OptionEntry q10 = factory.createOptionEntry("Past experiences have prepared me well", "My past experiences have prepared me well for my future");
        doc.addEntry(q10);
        q10.setSection(mainSec);
        q10.setLabel("10");
        q10.setOptionCodesDisplayed(false);
        createOptions(factory, q10, new String[]{"True", "False"}, new int[]{0,1});

        OptionEntry q11 = factory.createOptionEntry("See ahead is unpleasantness", "All I can see ahead is unpleasantness rather than pleasantness");
        doc.addEntry(q11);
        q11.setSection(mainSec);
        q11.setLabel("11");
        q11.setOptionCodesDisplayed(false);
        createOptions(factory, q11, new String[]{"True", "False"}, new int[]{1,0});

        OptionEntry q12 = factory.createOptionEntry("Don't expect to get what I want", "I don't expect to get what I really want");
        doc.addEntry(q12);
        q12.setSection(mainSec);
        q12.setLabel("12");
        q12.setOptionCodesDisplayed(false);
        createOptions(factory, q12, new String[]{"True", "False"}, new int[]{1,0});

        OptionEntry q13 = factory.createOptionEntry("Look ahead to future, expect to be happier", "When I look ahead to the future, I expect to be happier than I am now");
        doc.addEntry(q13);
        q13.setSection(mainSec);
        q13.setLabel("13");
        q13.setOptionCodesDisplayed(false);
        createOptions(factory, q13, new String[]{"True", "False"}, new int[]{0,1});

        OptionEntry q14 = factory.createOptionEntry("Things just won't work out", "Things just won't work out the way I want them to");
        doc.addEntry(q14);
        q14.setSection(mainSec);
        q14.setLabel("14");
        q14.setOptionCodesDisplayed(false);
        createOptions(factory, q14, new String[]{"True", "False"}, new int[]{1,0});

        OptionEntry q15 = factory.createOptionEntry("Great faith in future", "I have great faith in the future");
        doc.addEntry(q15);
        q15.setSection(mainSec);
        q15.setLabel("15");
        q15.setOptionCodesDisplayed(false);
        createOptions(factory, q15, new String[]{"True", "False"}, new int[]{0,1});

        OptionEntry q16 = factory.createOptionEntry("Never get what I want", "I never get what I want, so it's foolish to want anything");
        doc.addEntry(q16);
        q16.setSection(mainSec);
        q16.setLabel("16");
        q16.setOptionCodesDisplayed(false);
        createOptions(factory, q16, new String[]{"True", "False"}, new int[]{1,0});

        OptionEntry q17 = factory.createOptionEntry("Unlikely I will get any real satisfaction", "It is very unlikely that I will get any real satisfaction in the future");
        doc.addEntry(q17);
        q17.setSection(mainSec);
        q17.setLabel("17");
        q17.setOptionCodesDisplayed(false);
        createOptions(factory, q17, new String[]{"True", "False"}, new int[]{1,0});

        OptionEntry q18 = factory.createOptionEntry("Future seems vague", "The future seems vague and uncertain to me");
        doc.addEntry(q18);
        q18.setSection(mainSec);
        q18.setLabel("18");
        q18.setOptionCodesDisplayed(false);
        createOptions(factory, q18, new String[]{"True", "False"}, new int[]{1,0});

        OptionEntry q19 = factory.createOptionEntry("Can look forward to more good times", "I can look forward to more good times than bad times");
        doc.addEntry(q19);
        q19.setSection(mainSec);
        q19.setLabel("19");
        q19.setOptionCodesDisplayed(false);
        createOptions(factory, q19, new String[]{"True", "False"}, new int[]{0,1});

        OptionEntry q20 = factory.createOptionEntry("No use in really trying", "There's no use in really trying to get something I want because I probably won't get it");
        doc.addEntry(q20);
        q20.setSection(mainSec);
        q20.setLabel("20");
        q20.setOptionCodesDisplayed(false);
        createOptions(factory, q20, new String[]{"True", "False"}, new int[]{1,0});

        DerivedEntry total = factory.createDerivedEntry("Total", "Hopelessness total");
        doc.addEntry(total);
        total.setSection(mainSec);
        total.setFormula("a+b+c+d+e+f+g+h+i+j+k+l+m+n+o+p+q+r+s+t");
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
        total.addVariable("t", q20);
        total.setDescription("0-3: Normal range; 4-8: Mild hopelessness; 9-14: Moderate hopelessness; > 14: Severe hopelessness");

        NarrativeEntry classification = factory.createNarrativeEntry("Hopelessness total classification");
        classification.setDisplayText("0-3: Normal range\n" +
        		"4-8: Mild hopelessness\n" +
        		"9-14: Moderate hopelessness\n" +
        		"> 14: Severe hopelessness");
        classification.setSection(mainSec);
        doc.addEntry(classification);

        return doc;
    }
}

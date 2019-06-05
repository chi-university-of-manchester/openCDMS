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

public class BAVQR extends AssessmentForm {

    public static Document createDocument(Factory factory){

    	Document doc = factory.createDocument("BAVQ-R", "Beliefs About Voices Questionnaire (BAVQ - R)");
    	createDocumentStatuses(factory, doc);

        Section genSec = factory.createSection("Introduction Section", "Introduction");
        doc.addSection(genSec);
        SectionOccurrence genSecOcc = factory.createSectionOccurrence("Introduction Section Occ");
        genSec.addOccurrence(genSecOcc);

        NarrativeEntry q0 = factory.createNarrativeEntry("Narrative 1",
        		"There are many people who hear voices. It would help us to find out how you are " +
        		"feeling about your voices by completing this questionnaire. " +
        		"Please read each statement and tick the box that best describes the way you have " +
        		"been feeling in the past week.");
        doc.addEntry(q0);
        q0.setSection(genSec);

        NarrativeEntry q0a = factory.createNarrativeEntry("Narrative 2",
        		"If you hear more than one voice then please complete the form for the voice that is dominant.");
        doc.addEntry(q0a);
        q0a.setSection(genSec);

        NarrativeEntry q0b = factory.createNarrativeEntry("Narrative 3",
				"Thank you for your help.");
        doc.addEntry(q0b);
        q0b.setSection(genSec);

        TextEntry name = factory.createTextEntry("Name", "Name");
        doc.addEntry(name);
        name.setSection(genSec);

        IntegerEntry age = factory.createIntegerEntry("Age", "Age");
        doc.addEntry(age);
        age.setSection(genSec);
        age.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive Integer"));

        Section beliefsSec = factory.createSection("Beliefs About Voices Section", "Beliefs About Voices");
        doc.addSection(beliefsSec);
        SectionOccurrence beliefsSecOcc = factory.createSectionOccurrence("Beliefs About Voices Section Occ");
        beliefsSec.addOccurrence(beliefsSecOcc);

        OptionEntry q1 = factory.createOptionEntry("Voice punishing me",
                "My voice is punishing me for something that I have done");
        doc.addEntry(q1);
        q1.setSection(beliefsSec);
        q1.setLabel("1");
        q1.setOptionCodesDisplayed(false);
        createOptions(factory, q1,
        		new String[]{"Disagree", "Unsure", "Slightly agree", "Strongly agree"},
        		new int[]{0,1,2,3});

        OptionEntry q2 = factory.createOptionEntry("Voice wants to help me",
                "My voice wants to help me");
		doc.addEntry(q2);
		q2.setSection(beliefsSec);
		q2.setLabel("2");
		q2.setOptionCodesDisplayed(false);
		createOptions(factory, q2,
				new String[]{"Disagree", "Unsure", "Slightly agree", "Strongly agree"},
				new int[]{0,1,2,3});

		OptionEntry q3 = factory.createOptionEntry("Voice is very powerful",
                "My voice is very powerful");
		doc.addEntry(q3);
		q3.setSection(beliefsSec);
		q3.setLabel("3");
		q3.setOptionCodesDisplayed(false);
		createOptions(factory, q3,
		new String[]{"Disagree", "Unsure", "Slightly agree", "Strongly agree"},
		new int[]{0,1,2,3});

		OptionEntry q4 = factory.createOptionEntry("Voice is persecuting me",
                "My voice is persecuting me for no good reason");
		doc.addEntry(q4);
		q4.setSection(beliefsSec);
		q4.setLabel("4");
		q4.setOptionCodesDisplayed(false);
		createOptions(factory, q4,
		new String[]{"Disagree", "Unsure", "Slightly agree", "Strongly agree"},
		new int[]{0,1,2,3});

		OptionEntry q5 = factory.createOptionEntry("Voice wants to protect me",
                "My voice wants to protect me");
		doc.addEntry(q5);
		q5.setSection(beliefsSec);
		q5.setLabel("5");
		q5.setOptionCodesDisplayed(false);
		createOptions(factory, q5,
		new String[]{"Disagree", "Unsure", "Slightly agree", "Strongly agree"},
		new int[]{0,1,2,3});

		OptionEntry q6 = factory.createOptionEntry("Voice knows everything about me",
                "My voice seems to know everything about me");
		doc.addEntry(q6);
		q6.setSection(beliefsSec);
		q6.setLabel("6");
		q6.setOptionCodesDisplayed(false);
		createOptions(factory, q6,
		new String[]{"Disagree", "Unsure", "Slightly agree", "Strongly agree"},
		new int[]{0,1,2,3});

		OptionEntry q7 = factory.createOptionEntry("Voice is evil",
                "My voice is evil");
		doc.addEntry(q7);
		q7.setSection(beliefsSec);
		q7.setLabel("7");
		q7.setOptionCodesDisplayed(false);
		createOptions(factory, q7,
		new String[]{"Disagree", "Unsure", "Slightly agree", "Strongly agree"},
		new int[]{0,1,2,3});

		OptionEntry q8 = factory.createOptionEntry("Voice helping me keep sane",
                "My voice is helping me to keep sane");
		doc.addEntry(q8);
		q8.setSection(beliefsSec);
		q8.setLabel("8");
		q8.setOptionCodesDisplayed(false);
		createOptions(factory, q8,
		new String[]{"Disagree", "Unsure", "Slightly agree", "Strongly agree"},
		new int[]{0,1,2,3});

		OptionEntry q9 = factory.createOptionEntry("Voice makes me do things",
                "My voice makes me do things that I really don’t want to do");
		doc.addEntry(q9);
		q9.setSection(beliefsSec);
		q9.setLabel("9");
		q9.setOptionCodesDisplayed(false);
		createOptions(factory, q9,
		new String[]{"Disagree", "Unsure", "Slightly agree", "Strongly agree"},
		new int[]{0,1,2,3});

		OptionEntry q10 = factory.createOptionEntry("Voice wants to harm me",
                "My voice wants to harm me");
		doc.addEntry(q10);
		q10.setSection(beliefsSec);
		q10.setLabel("10");
		q10.setOptionCodesDisplayed(false);
		createOptions(factory, q10,
		new String[]{"Disagree", "Unsure", "Slightly agree", "Strongly agree"},
		new int[]{0,1,2,3});

		OptionEntry q11 = factory.createOptionEntry("Voice helping me develop special powers",
                "My voice is helping me to develop my special powers or abilities");
		doc.addEntry(q11);
		q11.setSection(beliefsSec);
		q11.setLabel("11");
		q11.setOptionCodesDisplayed(false);
		createOptions(factory, q11,
		new String[]{"Disagree", "Unsure", "Slightly agree", "Strongly agree"},
		new int[]{0,1,2,3});

		OptionEntry q12 = factory.createOptionEntry("Cannot control voices",
                "I cannot control my voices");
		doc.addEntry(q12);
		q12.setSection(beliefsSec);
		q12.setLabel("12");
		q12.setOptionCodesDisplayed(false);
		createOptions(factory, q12,
		new String[]{"Disagree", "Unsure", "Slightly agree", "Strongly agree"},
		new int[]{0,1,2,3});

		OptionEntry q13 = factory.createOptionEntry("Voice wants me to do bad things",
                "My voice wants me to do bad things");
		doc.addEntry(q13);
		q13.setSection(beliefsSec);
		q13.setLabel("13");
		q13.setOptionCodesDisplayed(false);
		createOptions(factory, q13,
		new String[]{"Disagree", "Unsure", "Slightly agree", "Strongly agree"},
		new int[]{0,1,2,3});

		OptionEntry q14 = factory.createOptionEntry("Voice helping me achieve goal",
                "My voice is helping me to achieve my goal in life");
		doc.addEntry(q14);
		q14.setSection(beliefsSec);
		q14.setLabel("14");
		q14.setOptionCodesDisplayed(false);
		createOptions(factory, q14,
		new String[]{"Disagree", "Unsure", "Slightly agree", "Strongly agree"},
		new int[]{0,1,2,3});

		OptionEntry q15 = factory.createOptionEntry("Voice will harm",
                "My voice will harm or kill me if I disobey or resist it");
		doc.addEntry(q15);
		q15.setSection(beliefsSec);
		q15.setLabel("15");
		q15.setOptionCodesDisplayed(false);
		createOptions(factory, q15,
		new String[]{"Disagree", "Unsure", "Slightly agree", "Strongly agree"},
		new int[]{0,1,2,3});

		OptionEntry q16 = factory.createOptionEntry("Voice trying to corrupt",
                "My voice is trying to corrupt or destroy me");
		doc.addEntry(q16);
		q16.setSection(beliefsSec);
		q16.setLabel("16");
		q16.setOptionCodesDisplayed(false);
		createOptions(factory, q16,
		new String[]{"Disagree", "Unsure", "Slightly agree", "Strongly agree"},
		new int[]{0,1,2,3});

		OptionEntry q17 = factory.createOptionEntry("Grateful for my voice",
                "I am grateful for my voice");
		doc.addEntry(q17);
		q17.setSection(beliefsSec);
		q17.setLabel("17");
		q17.setOptionCodesDisplayed(false);
		createOptions(factory, q17,
		new String[]{"Disagree", "Unsure", "Slightly agree", "Strongly agree"},
		new int[]{0,1,2,3});

		OptionEntry q18 = factory.createOptionEntry("Voice rules my life",
                "My voice rules my life");
		doc.addEntry(q18);
		q18.setSection(beliefsSec);
		q18.setLabel("18");
		q18.setOptionCodesDisplayed(false);
		createOptions(factory, q18,
		new String[]{"Disagree", "Unsure", "Slightly agree", "Strongly agree"},
		new int[]{0,1,2,3});

        Section emotionalSec = factory.createSection("Emotional Reactions Section", "Emotional Reactions");
        doc.addSection(emotionalSec);
        SectionOccurrence emotionalSecOcc = factory.createSectionOccurrence("Emotional Reactions Section Occ");
        emotionalSec.addOccurrence(emotionalSecOcc);

        OptionEntry q19 = factory.createOptionEntry("My voice reassures me",
                "My voice reassures me");
        doc.addEntry(q19);
        q19.setSection(emotionalSec);
        q19.setLabel("19");
        q19.setOptionCodesDisplayed(false);
        createOptions(factory, q19,
        		new String[]{"Disagree", "Unsure", "Slightly agree", "Strongly agree"},
        		new int[]{0,1,2,3});

        OptionEntry q20 = factory.createOptionEntry("My voice frightens me",
                "My voice frightens me");
		doc.addEntry(q20);
		q20.setSection(emotionalSec);
		q20.setLabel("19");
		q20.setOptionCodesDisplayed(false);
		createOptions(factory, q20,
				new String[]{"Disagree", "Unsure", "Slightly agree", "Strongly agree"},
				new int[]{0,1,2,3});

		OptionEntry q21 = factory.createOptionEntry("My voice makes me happy",
                "My voice makes me happy");
		doc.addEntry(q21);
		q21.setSection(emotionalSec);
		q21.setLabel("19");
		q21.setOptionCodesDisplayed(false);
		createOptions(factory, q21,
				new String[]{"Disagree", "Unsure", "Slightly agree", "Strongly agree"},
				new int[]{0,1,2,3});

		OptionEntry q22 = factory.createOptionEntry("My voice makes me feel down",
                "My voice makes me feel down");
		doc.addEntry(q22);
		q22.setSection(emotionalSec);
		q22.setLabel("22");
		q22.setOptionCodesDisplayed(false);
		createOptions(factory, q22,
				new String[]{"Disagree", "Unsure", "Slightly agree", "Strongly agree"},
				new int[]{0,1,2,3});

		OptionEntry q23 = factory.createOptionEntry("My voice makes me feel angry",
                "My voice makes me feel angry");
		doc.addEntry(q23);
		q23.setSection(emotionalSec);
		q23.setLabel("23");
		q23.setOptionCodesDisplayed(false);
		createOptions(factory, q23,
				new String[]{"Disagree", "Unsure", "Slightly agree", "Strongly agree"},
				new int[]{0,1,2,3});

		OptionEntry q24 = factory.createOptionEntry("My voice makes me feel calm",
                "My voice makes me feel calm");
		doc.addEntry(q24);
		q24.setSection(emotionalSec);
		q24.setLabel("24");
		q24.setOptionCodesDisplayed(false);
		createOptions(factory, q24,
				new String[]{"Disagree", "Unsure", "Slightly agree", "Strongly agree"},
				new int[]{0,1,2,3});

		OptionEntry q25 = factory.createOptionEntry("My voice makes me feel anxious",
                "My voice makes me feel anxious");
		doc.addEntry(q25);
		q25.setSection(emotionalSec);
		q25.setLabel("25");
		q25.setOptionCodesDisplayed(false);
		createOptions(factory, q25,
				new String[]{"Disagree", "Unsure", "Slightly agree", "Strongly agree"},
				new int[]{0,1,2,3});

		OptionEntry q26 = factory.createOptionEntry("My voice makes me feel confident",
                "My voice makes me feel confident");
		doc.addEntry(q26);
		q26.setSection(emotionalSec);
		q26.setLabel("26");
		q26.setOptionCodesDisplayed(false);
		createOptions(factory, q26,
				new String[]{"Disagree", "Unsure", "Slightly agree", "Strongly agree"},
				new int[]{0,1,2,3});

        Section behaviouralSec = factory.createSection("Behavioural Reactions Section", "Behavioural Reactions");
        doc.addSection(behaviouralSec);
        SectionOccurrence behaviouralSecOcc = factory.createSectionOccurrence("Behavioural Reactions Section Occ");
        behaviouralSec.addOccurrence(behaviouralSecOcc);

        NarrativeEntry behNar = factory.createNarrativeEntry("Behavioural narrative", "When I hear my voice, usually...");
        doc.addEntry(behNar);
        behNar.setSection(behaviouralSec);

        OptionEntry q27 = factory.createOptionEntry("I tell it to leave me alone",
                "I tell it to leave me alone");
        doc.addEntry(q27);
        q27.setSection(behaviouralSec);
        q27.setLabel("27");
        q27.setOptionCodesDisplayed(false);
        createOptions(factory, q27,
        		new String[]{"Disagree", "Unsure", "Slightly agree", "Strongly agree"},
        		new int[]{0,1,2,3});

        OptionEntry q28 = factory.createOptionEntry("I try and take my mind off it",
                "I try and take my mind off it");
		doc.addEntry(q28);
		q28.setSection(behaviouralSec);
		q28.setLabel("28");
		q28.setOptionCodesDisplayed(false);
		createOptions(factory, q28,
				new String[]{"Disagree", "Unsure", "Slightly agree", "Strongly agree"},
				new int[]{0,1,2,3});

		OptionEntry q29 = factory.createOptionEntry("I try and stop it",
                "I try and stop it");
		doc.addEntry(q29);
		q29.setSection(behaviouralSec);
		q29.setLabel("29");
		q29.setOptionCodesDisplayed(false);
		createOptions(factory, q29,
				new String[]{"Disagree", "Unsure", "Slightly agree", "Strongly agree"},
				new int[]{0,1,2,3});

		OptionEntry q30 = factory.createOptionEntry("I do things to prevent it talking",
                "I do things to prevent it talking");
		doc.addEntry(q30);
		q30.setSection(behaviouralSec);
		q30.setLabel("30");
		q30.setOptionCodesDisplayed(false);
		createOptions(factory, q30,
				new String[]{"Disagree", "Unsure", "Slightly agree", "Strongly agree"},
				new int[]{0,1,2,3});

		OptionEntry q31 = factory.createOptionEntry("Reluctant to obey it",
                "I am reluctant to obey it");
		doc.addEntry(q31);
		q31.setSection(behaviouralSec);
		q31.setLabel("31");
		q31.setOptionCodesDisplayed(false);
		createOptions(factory, q31,
				new String[]{"Disagree", "Unsure", "Slightly agree", "Strongly agree"},
				new int[]{0,1,2,3});

		OptionEntry q32 = factory.createOptionEntry("Listen to it because I want to",
                "I listen to it because I want to");
		doc.addEntry(q32);
		q32.setSection(behaviouralSec);
		q32.setLabel("32");
		q32.setOptionCodesDisplayed(false);
		createOptions(factory, q32,
				new String[]{"Disagree", "Unsure", "Slightly agree", "Strongly agree"},
				new int[]{0,1,2,3});

		OptionEntry q33 = factory.createOptionEntry("Willingly follow what voice tells me",
                "I willingly follow what my voice tells me to do");
		doc.addEntry(q33);
		q33.setSection(behaviouralSec);
		q33.setLabel("33");
		q33.setOptionCodesDisplayed(false);
		createOptions(factory, q33,
				new String[]{"Disagree", "Unsure", "Slightly agree", "Strongly agree"},
				new int[]{0,1,2,3});

		OptionEntry q34 = factory.createOptionEntry("Start to get in contact with voice",
                "I have done things to start to get in contact with my voice");
		doc.addEntry(q34);
		q34.setSection(behaviouralSec);
		q34.setLabel("34");
		q34.setOptionCodesDisplayed(false);
		createOptions(factory, q34,
				new String[]{"Disagree", "Unsure", "Slightly agree", "Strongly agree"},
				new int[]{0,1,2,3});

		OptionEntry q35 = factory.createOptionEntry("Seek advice of voice",
                "I seek the advice of my voice");
		doc.addEntry(q35);
		q35.setSection(behaviouralSec);
		q35.setLabel("35");
		q35.setOptionCodesDisplayed(false);
		createOptions(factory, q35,
				new String[]{"Disagree", "Unsure", "Slightly agree", "Strongly agree"},
				new int[]{0,1,2,3});

        Section totalsSec = factory.createSection("Totals Section", "Totals");
        doc.addSection(totalsSec);
        SectionOccurrence totalsSecOcc = factory.createSectionOccurrence("Totals Section Occ");
        totalsSec.addOccurrence(totalsSecOcc);

        DerivedEntry malevolence = factory.createDerivedEntry("Malevolence total", "Malevolence total");
        doc.addEntry(malevolence);
        malevolence.setSection(totalsSec);
        malevolence.setDescription("=Q1+Q4+Q7+Q10+Q13+Q16");
        malevolence.setFormula("a+b+c+d+e+f");
        malevolence.addVariable("a", q1);
        malevolence.addVariable("b", q4);
        malevolence.addVariable("c", q7);
        malevolence.addVariable("d", q10);
        malevolence.addVariable("e", q13);
        malevolence.addVariable("f", q16);

        DerivedEntry benevolence = factory.createDerivedEntry("Benevolence total", "Benevolence total");
        doc.addEntry(benevolence);
        benevolence.setSection(totalsSec);
        benevolence.setDescription("=Q2+Q5+Q8+Q11+Q14+Q17");
        benevolence.setFormula("a+b+c+d+e+f");
        benevolence.addVariable("a", q2);
        benevolence.addVariable("b", q5);
        benevolence.addVariable("c", q8);
        benevolence.addVariable("d", q11);
        benevolence.addVariable("e", q14);
        benevolence.addVariable("f", q17);

        DerivedEntry omnipotence = factory.createDerivedEntry("Omnipotence total", "Omnipotence total");
        doc.addEntry(omnipotence);
        omnipotence.setSection(totalsSec);
        omnipotence.setDescription("=Q3+Q6+Q9+Q12+Q15+Q18");
        omnipotence.setFormula("a+b+c+d+e+f");
        omnipotence.addVariable("a", q3);
        omnipotence.addVariable("b", q6);
        omnipotence.addVariable("c", q9);
        omnipotence.addVariable("d", q12);
        omnipotence.addVariable("e", q15);
        omnipotence.addVariable("f", q18);

        DerivedEntry resistance = factory.createDerivedEntry("Resistance total", "Resistance total");
        doc.addEntry(resistance);
        resistance.setSection(totalsSec);
        resistance.setDescription("=Q20+Q22+Q23+Q25+Q27+Q28+Q29+Q30+Q31");
        resistance.setFormula("a+b+c+d+e+f+g+h+i");
        resistance.addVariable("a", q20);
        resistance.addVariable("b", q22);
        resistance.addVariable("c", q23);
        resistance.addVariable("d", q25);
        resistance.addVariable("e", q27);
        resistance.addVariable("f", q28);
        resistance.addVariable("g", q29);
        resistance.addVariable("h", q30);
        resistance.addVariable("i", q31);

        DerivedEntry resistanceEmotional = factory.createDerivedEntry("Resistance total (emotional)", "...of which emotional");
        doc.addEntry(resistanceEmotional);
        resistanceEmotional.setSection(totalsSec);
        resistanceEmotional.setDescription("=Q20+Q22+Q23+Q25");
        resistanceEmotional.setFormula("a+b+c+d");
        resistanceEmotional.addVariable("a", q20);
        resistanceEmotional.addVariable("b", q22);
        resistanceEmotional.addVariable("c", q23);
        resistanceEmotional.addVariable("d", q25);

        DerivedEntry resistanceBehavioural = factory.createDerivedEntry("Resistance total (behavioural)", "...of which behavioural");
        doc.addEntry(resistanceBehavioural);
        resistanceBehavioural.setSection(totalsSec);
        resistanceBehavioural.setDescription("=Q27+Q28+Q29+Q30+Q31");
        resistanceBehavioural.setFormula("e+f+g+h+i");
        resistanceBehavioural.addVariable("e", q27);
        resistanceBehavioural.addVariable("f", q28);
        resistanceBehavioural.addVariable("g", q29);
        resistanceBehavioural.addVariable("h", q30);
        resistanceBehavioural.addVariable("i", q31);

        DerivedEntry engagement = factory.createDerivedEntry("Engagement total", "Engagement total");
        doc.addEntry(engagement);
        engagement.setSection(totalsSec);
        engagement.setDescription("=Q19+Q21+Q24+Q26+Q32+Q33+Q34+Q35");
        engagement.setFormula("a+b+c+d+e+f+g+h");
        engagement.addVariable("a", q19);
        engagement.addVariable("b", q21);
        engagement.addVariable("c", q24);
        engagement.addVariable("d", q26);
        engagement.addVariable("e", q32);
        engagement.addVariable("f", q33);
        engagement.addVariable("g", q34);
        engagement.addVariable("h", q35);

        DerivedEntry engagementEmotional = factory.createDerivedEntry("Engagement total (emotional)", "...of which emotional");
        doc.addEntry(engagementEmotional);
        engagementEmotional.setSection(totalsSec);
        engagementEmotional.setDescription("=Q19+Q21+Q24+Q26");
        engagementEmotional.setFormula("a+b+c+d");
        engagementEmotional.addVariable("a", q19);
        engagementEmotional.addVariable("b", q21);
        engagementEmotional.addVariable("c", q24);
        engagementEmotional.addVariable("d", q26);

        DerivedEntry engagementBehavioural = factory.createDerivedEntry("Engagement total (behavioural)", "...of which behavioural");
        doc.addEntry(engagementBehavioural);
        engagementBehavioural.setSection(totalsSec);
        engagementBehavioural.setDescription("=Q32+Q33+Q34+Q35");
        engagementBehavioural.setFormula("e+f+g+h");
        engagementBehavioural.addVariable("e", q32);
        engagementBehavioural.addVariable("f", q33);
        engagementBehavioural.addVariable("g", q34);
        engagementBehavioural.addVariable("h", q35);

        return doc;
    }
}
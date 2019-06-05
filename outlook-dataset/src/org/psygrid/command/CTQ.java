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

public class CTQ extends AssessmentForm {

    public static Document createDocument(Factory factory){

    	Document doc = factory.createDocument("CTQ", "Childhood Trauma Questionnaire");
    	createDocumentStatuses(factory, doc);

        Section mainSec = factory.createSection("Main Section", "Main");
        doc.addSection(mainSec);
        SectionOccurrence mainSecOcc = factory.createSectionOccurrence("Main Section Occ");
        mainSec.addOccurrence(mainSecOcc);

        IntegerEntry age = factory.createIntegerEntry("Age", "Age");
        doc.addEntry(age);
        age.setSection(mainSec);
        age.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive Integer"));

        OptionEntry sex = factory.createOptionEntry("Sex", "Sex");
        doc.addEntry(sex);
        sex.setSection(mainSec);
        createOptions(factory, sex, new String[]{"Male", "Female"}, new int[]{1,2});

        NarrativeEntry growingUp = factory.createNarrativeEntry("Growing Up", "When I was growing up...");
        doc.addEntry(growingUp);
        growingUp.setSection(mainSec);

        OptionEntry q1 = factory.createOptionEntry("Not enough to eat", "I didn't have enough to eat.");
        doc.addEntry(q1);
        q1.setSection(mainSec);
        q1.setLabel("1");
        q1.setOptionCodesDisplayed(false);
        createOptions(factory, q1,
        		new String[]{"Never true", "Rarely true", "Sometimes true", "Often true", "Very often true"},
        		new int[]{1,2,3,4,5});

        OptionEntry q2 = factory.createOptionEntry("Knew someone to take care", "I knew that there was someone to take care of me and protect me.");
        doc.addEntry(q2);
        q2.setSection(mainSec);
        q2.setLabel("2");
        q2.setOptionCodesDisplayed(false);
        createOptions(factory, q2,
        		new String[]{"Never true", "Rarely true", "Sometimes true", "Often true", "Very often true"},
        		new int[]{5,4,3,2,1});

        OptionEntry q3 = factory.createOptionEntry("Called things like stupid", "People in my family called things like 'stupid', 'lazy' or 'ugly'.");
        doc.addEntry(q3);
        q3.setSection(mainSec);
        q3.setLabel("3");
        q3.setOptionCodesDisplayed(false);
        createOptions(factory, q3,
        		new String[]{"Never true", "Rarely true", "Sometimes true", "Often true", "Very often true"},
        		new int[]{1,2,3,4,5});

        OptionEntry q4 = factory.createOptionEntry("Parents too drunk to take care", "My parents were too drunk or high to take care of the family.");
        doc.addEntry(q4);
        q4.setSection(mainSec);
        q4.setLabel("4");
        q4.setOptionCodesDisplayed(false);
        createOptions(factory, q4,
        		new String[]{"Never true", "Rarely true", "Sometimes true", "Often true", "Very often true"},
        		new int[]{1,2,3,4,5});

        OptionEntry q5 = factory.createOptionEntry("Someone helped me feel important", "There was someone in my family who helped me feel that I was important or special.");
        doc.addEntry(q5);
        q5.setSection(mainSec);
        q5.setLabel("5");
        q5.setOptionCodesDisplayed(false);
        createOptions(factory, q5,
        		new String[]{"Never true", "Rarely true", "Sometimes true", "Often true", "Very often true"},
        		new int[]{5,4,3,2,1});

        OptionEntry q6 = factory.createOptionEntry("Dirty clothes", "I had to wear dirty clothes.");
        doc.addEntry(q6);
        q6.setSection(mainSec);
        q6.setLabel("6");
        q6.setOptionCodesDisplayed(false);
        createOptions(factory, q6,
        		new String[]{"Never true", "Rarely true", "Sometimes true", "Often true", "Very often true"},
        		new int[]{1,2,3,4,5});

        OptionEntry q7 = factory.createOptionEntry("Felt loved", "I felt loved.");
        doc.addEntry(q7);
        q7.setSection(mainSec);
        q7.setLabel("7");
        q7.setOptionCodesDisplayed(false);
        createOptions(factory, q7,
        		new String[]{"Never true", "Rarely true", "Sometimes true", "Often true", "Very often true"},
        		new int[]{5,4,3,2,1});

        OptionEntry q8 = factory.createOptionEntry("Thought parent wished me not born", "I thought that my parent wished I had never been born.");
        doc.addEntry(q8);
        q8.setSection(mainSec);
        q8.setLabel("8");
        q8.setOptionCodesDisplayed(false);
        createOptions(factory, q8,
        		new String[]{"Never true", "Rarely true", "Sometimes true", "Often true", "Very often true"},
        		new int[]{1,2,3,4,5});

        OptionEntry q9 = factory.createOptionEntry("Hit so hard see doctor", "I got hit so hard by someone in my family that I had to see a doctor or go to hospital.");
        doc.addEntry(q9);
        q9.setSection(mainSec);
        q9.setLabel("9");
        q9.setOptionCodesDisplayed(false);
        createOptions(factory, q9,
        		new String[]{"Never true", "Rarely true", "Sometimes true", "Often true", "Very often true"},
        		new int[]{1,2,3,4,5});

        OptionEntry q10 = factory.createOptionEntry("Nothing wanted to change", "There was nothing I wanted to change about my family.");
        doc.addEntry(q10);
        q10.setSection(mainSec);
        q10.setLabel("10");
        q10.setOptionCodesDisplayed(false);
        createOptions(factory, q10,
        		new String[]{"Never true", "Rarely true", "Sometimes true", "Often true", "Very often true"},
        		new int[]{1,2,3,4,5});

        OptionEntry q11 = factory.createOptionEntry("Hit me so hard left marks", "People in my family hit me so hard that it left me with bruises or marks.");
        doc.addEntry(q11);
        q11.setSection(mainSec);
        q11.setLabel("11");
        q11.setOptionCodesDisplayed(false);
        createOptions(factory, q11,
        		new String[]{"Never true", "Rarely true", "Sometimes true", "Often true", "Very often true"},
        		new int[]{1,2,3,4,5});

        OptionEntry q12 = factory.createOptionEntry("Punished with belt", "I was punished with a belt, a board, a cord or some other hard objects.");
        doc.addEntry(q12);
        q12.setSection(mainSec);
        q12.setLabel("12");
        q12.setOptionCodesDisplayed(false);
        createOptions(factory, q12,
        		new String[]{"Never true", "Rarely true", "Sometimes true", "Often true", "Very often true"},
        		new int[]{1,2,3,4,5});

        OptionEntry q13 = factory.createOptionEntry("Family looked out each other", "People in my family looked out for each other.");
        doc.addEntry(q13);
        q13.setSection(mainSec);
        q13.setLabel("13");
        q13.setOptionCodesDisplayed(false);
        createOptions(factory, q13,
        		new String[]{"Never true", "Rarely true", "Sometimes true", "Often true", "Very often true"},
        		new int[]{5,4,3,2,1});

        OptionEntry q14 = factory.createOptionEntry("Family said hurtful things", "People in my family said hurtful or insulting things to me.");
        doc.addEntry(q14);
        q14.setSection(mainSec);
        q14.setLabel("14");
        q14.setOptionCodesDisplayed(false);
        createOptions(factory, q14,
        		new String[]{"Never true", "Rarely true", "Sometimes true", "Often true", "Very often true"},
        		new int[]{1,2,3,4,5});

        OptionEntry q15 = factory.createOptionEntry("Believe physically abused", "I believe that I was physically abused.");
        doc.addEntry(q15);
        q15.setSection(mainSec);
        q15.setLabel("15");
        q15.setOptionCodesDisplayed(false);
        createOptions(factory, q15,
        		new String[]{"Never true", "Rarely true", "Sometimes true", "Often true", "Very often true"},
        		new int[]{1,2,3,4,5});

        OptionEntry q16 = factory.createOptionEntry("Perfect childhood", "I had the perfect childhood.");
        doc.addEntry(q16);
        q16.setSection(mainSec);
        q16.setLabel("16");
        q16.setOptionCodesDisplayed(false);
        createOptions(factory, q16,
        		new String[]{"Never true", "Rarely true", "Sometimes true", "Often true", "Very often true"},
        		new int[]{1,2,3,4,5});

        OptionEntry q17 = factory.createOptionEntry("Hit so bad was noticed", "I got hit or beaten so badly that it was noticed by someone like a teacher, neighbour, or doctor.");
        doc.addEntry(q17);
        q17.setSection(mainSec);
        q17.setLabel("17");
        q17.setOptionCodesDisplayed(false);
        createOptions(factory, q17,
        		new String[]{"Never true", "Rarely true", "Sometimes true", "Often true", "Very often true"},
        		new int[]{1,2,3,4,5});

        OptionEntry q18 = factory.createOptionEntry("Felt someone hated me", "I felt that someone in my family hated me.");
        doc.addEntry(q18);
        q18.setSection(mainSec);
        q18.setLabel("18");
        q18.setOptionCodesDisplayed(false);
        createOptions(factory, q18,
        		new String[]{"Never true", "Rarely true", "Sometimes true", "Often true", "Very often true"},
        		new int[]{1,2,3,4,5});

        OptionEntry q19 = factory.createOptionEntry("Family felt close", "People in my family felt close to each other.");
        doc.addEntry(q19);
        q19.setSection(mainSec);
        q19.setLabel("19");
        q19.setOptionCodesDisplayed(false);
        createOptions(factory, q19,
        		new String[]{"Never true", "Rarely true", "Sometimes true", "Often true", "Very often true"},
        		new int[]{5,4,3,2,1});

        OptionEntry q20 = factory.createOptionEntry("Tried touch me sexual way", "Someone tried to touch me in a sexual way or tried to make me touch them.");
        doc.addEntry(q20);
        q20.setSection(mainSec);
        q20.setLabel("20");
        q20.setOptionCodesDisplayed(false);
        createOptions(factory, q20,
        		new String[]{"Never true", "Rarely true", "Sometimes true", "Often true", "Very often true"},
        		new int[]{1,2,3,4,5});

        OptionEntry q21 = factory.createOptionEntry("Threatened to hurt unless did something sexual", "Someone threatened to hurt me or tell lies about me unless I did something sexual with them.");
        doc.addEntry(q21);
        q21.setSection(mainSec);
        q21.setLabel("21");
        q21.setOptionCodesDisplayed(false);
        createOptions(factory, q21,
        		new String[]{"Never true", "Rarely true", "Sometimes true", "Often true", "Very often true"},
        		new int[]{1,2,3,4,5});

        OptionEntry q22 = factory.createOptionEntry("Best family in world", "I had the best family in the world.");
        doc.addEntry(q22);
        q22.setSection(mainSec);
        q22.setLabel("22");
        q22.setOptionCodesDisplayed(false);
        createOptions(factory, q22,
        		new String[]{"Never true", "Rarely true", "Sometimes true", "Often true", "Very often true"},
        		new int[]{1,2,3,4,5});

        OptionEntry q23 = factory.createOptionEntry("Tried make me do sexual things", "Someone tried to make me do sexual things or watch sexual things.");
        doc.addEntry(q23);
        q23.setSection(mainSec);
        q23.setLabel("23");
        q23.setOptionCodesDisplayed(false);
        createOptions(factory, q23,
        		new String[]{"Never true", "Rarely true", "Sometimes true", "Often true", "Very often true"},
        		new int[]{1,2,3,4,5});

        OptionEntry q24 = factory.createOptionEntry("Someone molested me", "Someone molested me.");
        doc.addEntry(q24);
        q24.setSection(mainSec);
        q24.setLabel("24");
        q24.setOptionCodesDisplayed(false);
        createOptions(factory, q24,
        		new String[]{"Never true", "Rarely true", "Sometimes true", "Often true", "Very often true"},
        		new int[]{1,2,3,4,5});

        OptionEntry q25 = factory.createOptionEntry("Believe was emotionally abused", "I believe that I was emotionally abused.");
        doc.addEntry(q25);
        q25.setSection(mainSec);
        q25.setLabel("25");
        q25.setOptionCodesDisplayed(false);
        createOptions(factory, q25,
        		new String[]{"Never true", "Rarely true", "Sometimes true", "Often true", "Very often true"},
        		new int[]{1,2,3,4,5});

        OptionEntry q26 = factory.createOptionEntry("Someone to take me to doctor", "There was someone to take me to the doctor if I needed it.");
        doc.addEntry(q26);
        q26.setSection(mainSec);
        q26.setLabel("26");
        q26.setOptionCodesDisplayed(false);
        createOptions(factory, q26,
        		new String[]{"Never true", "Rarely true", "Sometimes true", "Often true", "Very often true"},
        		new int[]{5,4,3,2,1});

        OptionEntry q27 = factory.createOptionEntry("Believe was sexually abused", "I believe that I was sexually abused.");
        doc.addEntry(q27);
        q27.setSection(mainSec);
        q27.setLabel("27");
        q27.setOptionCodesDisplayed(false);
        createOptions(factory, q27,
        		new String[]{"Never true", "Rarely true", "Sometimes true", "Often true", "Very often true"},
        		new int[]{1,2,3,4,5});

        OptionEntry q28 = factory.createOptionEntry("Family source of strength", "My family was a source of strength and support.");
        doc.addEntry(q28);
        q28.setSection(mainSec);
        q28.setLabel("28");
        q28.setOptionCodesDisplayed(false);
        createOptions(factory, q28,
        		new String[]{"Never true", "Rarely true", "Sometimes true", "Often true", "Very often true"},
        		new int[]{5,4,3,2,1});

        Section scalesSec = factory.createSection("Scales Section", "Scales");
        doc.addSection(scalesSec);
        SectionOccurrence scalesSecOcc = factory.createSectionOccurrence("Scales Section Occ");
        scalesSec.addOccurrence(scalesSecOcc);

        NarrativeEntry eaNar = factory.createNarrativeEntry("EAN", "Emotional Abuse");
        doc.addEntry(eaNar);
        eaNar.setSection(scalesSec);
        eaNar.setStyle(NarrativeStyle.HEADER);

        DerivedEntry emotionalTotal = factory.createDerivedEntry("Emotional Abuse Total",
                "Emotional Abuse Scale Total");
        doc.addEntry(emotionalTotal);
        emotionalTotal.setSection(scalesSec);
        emotionalTotal.setDescription("=q3+q8+q14+q18+q25");
        emotionalTotal.setFormula("q3+q8+q14+q18+q25");
        emotionalTotal.addVariable("q3", q3);
        emotionalTotal.addVariable("q8", q8);
        emotionalTotal.addVariable("q14", q14);
        emotionalTotal.addVariable("q18", q18);
        emotionalTotal.addVariable("q25", q25);

        DerivedEntry emotionalClass = factory.createDerivedEntry("Emotional Abuse Classification",
                "Emotional Abuse Classification");
        doc.addEntry(emotionalClass);
        emotionalClass.setSection(scalesSec);
		emotionalClass.setDescription("1 = None (or Minimal); 2 = Low (to Moderate); 3 = Moderate (to Severe); 4 = Severe (to Extreme)");
        emotionalClass.setFormula("if(e>=5&&e<=8,1,if(e>=9&&e<=12,2,if(e>=13&&e<=15,3,4)))");
        emotionalClass.addVariable("e", emotionalTotal);

        DerivedEntry emotionalPercentile = factory.createDerivedEntry("Emotional Abuse Percentile",
                "Emotional Abuse Percentile");
		doc.addEntry(emotionalPercentile);
		emotionalPercentile.setSection(scalesSec);
		emotionalPercentile.setDescription("For example, if answer is 10 this implies being in the 10th percentile (10-20); if 20 implies being in the 20th percentile (20-30) etc.");
		emotionalPercentile.setFormula(
				"if(s==1," +
				"if(e<7,20,if(e<8,30,if(e<9,40,if(e<11,50,if(e<15,60,if(e<17,70,if(e<19,80,if(e<21,90,if(e<24,95,99)))))))))," +
				"if(e<8,10,if(e<10,20,if(e<12,30,if(e<13,40,if(e<15,50,if(e<17,60,if(e<19,70,if(e<23,80,if(e<24,90,if(e<25,95,99)))))))))) )");
		emotionalPercentile.addVariable("e", emotionalTotal);
		emotionalPercentile.addVariable("s", sex);



        NarrativeEntry paNar = factory.createNarrativeEntry("PAN", "Physical Abuse");
        doc.addEntry(paNar);
        paNar.setSection(scalesSec);
        paNar.setStyle(NarrativeStyle.HEADER);

        DerivedEntry physicalTotal = factory.createDerivedEntry("Physical Abuse Total",
                "Physical Abuse Scale Total");
        doc.addEntry(physicalTotal);
        physicalTotal.setSection(scalesSec);
        physicalTotal.setDescription("=q9+q11+q12+q15+q17");
        physicalTotal.setFormula("q9+q11+q12+q15+q17");
        physicalTotal.addVariable("q9", q9);
        physicalTotal.addVariable("q11", q11);
        physicalTotal.addVariable("q12", q12);
        physicalTotal.addVariable("q15", q15);
        physicalTotal.addVariable("q17", q17);

        DerivedEntry physicalClass = factory.createDerivedEntry("Physical Abuse Classification",
                "Physical Abuse Classification");
		doc.addEntry(physicalClass);
		physicalClass.setSection(scalesSec);
		physicalClass.setDescription("1 = None (or Minimal); 2 = Low (to Moderate); 3 = Moderate (to Severe); 4 = Severe (to Extreme)");
		physicalClass.setFormula("if(p>=5&&p<=7,1,if(p>=8&&p<=9,2,if(p>=10&&p<=12,3,4)))");
		physicalClass.addVariable("p", physicalTotal);

        DerivedEntry physicalPercentile = factory.createDerivedEntry("Physical Abuse Percentile",
                "Physical Abuse Percentile");
		doc.addEntry(physicalPercentile);
		physicalPercentile.setSection(scalesSec);
		physicalPercentile.setDescription("For example, if answer is 10 this implies being in the 10th percentile (10-20); if 20 implies being in the 20th percentile (20-30) etc.");
		physicalPercentile.setFormula(
				"if(s==1," +
				"if(p<6,40,if(p<7,50,if(p<9,60,if(p<11,70,if(p<14,80,if(p<16,90,if(p<22,95,99)))))))," +
				"if(p<6,30,if(p<7,40,if(p<8,50,if(p<11,60,if(p<14,70,if(p<19,80,if(p<20,90,if(p<24,95,99)))))))) )");
		physicalPercentile.addVariable("p", physicalTotal);
		physicalPercentile.addVariable("s", sex);



        NarrativeEntry saNar = factory.createNarrativeEntry("SAN", "Sexual Abuse");
        doc.addEntry(saNar);
        saNar.setSection(scalesSec);
        saNar.setStyle(NarrativeStyle.HEADER);

		DerivedEntry sexualTotal = factory.createDerivedEntry("Sexual Abuse Total",
                "Sexual Abuse Scale Total");
		doc.addEntry(sexualTotal);
		sexualTotal.setSection(scalesSec);
		sexualTotal.setDescription("=q20+q21+q23+q24+q27");
		sexualTotal.setFormula("q20+q21+q23+q24+q27");
		sexualTotal.addVariable("q20", q20);
		sexualTotal.addVariable("q21", q21);
		sexualTotal.addVariable("q23", q23);
		sexualTotal.addVariable("q24", q24);
		sexualTotal.addVariable("q27", q27);

        DerivedEntry sexualClass = factory.createDerivedEntry("Sexual Abuse Classification",
                "Sexual Abuse Classification");
		doc.addEntry(sexualClass);
		sexualClass.setSection(scalesSec);
		sexualClass.setDescription("1 = None (or Minimal); 2 = Low (to Moderate); 3 = Moderate (to Severe); 4 = Severe (to Extreme)");
		sexualClass.setFormula("if(s>=5&&s<=5,1,if(s>=6&&s<=7,2,if(s>=8&&s<=12,3,4)))");
		sexualClass.addVariable("s", sexualTotal);

        DerivedEntry sexualPercentile = factory.createDerivedEntry("Sexual Abuse Percentile",
                "Sexual Abuse Percentile");
		doc.addEntry(sexualPercentile);
		sexualPercentile.setSection(scalesSec);
		sexualPercentile.setDescription("For example, if answer is 10 this implies being in the 10th percentile (10-20); if 20 implies being in the 20th percentile (20-30) etc.");
		sexualPercentile.setFormula(
				"if(s==1," +
				"if(t<6,70,if(t<10,80,if(t<19,90,if(t<24,95,99))))," +
				"if(t<7,50,if(t<10,60,if(t<15,70,if(t<22,80,if(t<24,90,if(t<25,95,99)))))) )");
		sexualPercentile.addVariable("t", sexualTotal);
		sexualPercentile.addVariable("s", sex);



        NarrativeEntry enNar = factory.createNarrativeEntry("ENN", "Emotional Neglect");
        doc.addEntry(enNar);
        enNar.setSection(scalesSec);
        enNar.setStyle(NarrativeStyle.HEADER);

        DerivedEntry emotionalTotal2 = factory.createDerivedEntry("Emotional Neglect Total",
                "Emotional Neglect Scale Total");
		doc.addEntry(emotionalTotal2);
		emotionalTotal2.setSection(scalesSec);
		emotionalTotal2.setDescription("=q5+q7+q13+q19+q28");
		emotionalTotal2.setFormula("q5+q7+q13+q19+q28");
		emotionalTotal2.addVariable("q5", q5);
		emotionalTotal2.addVariable("q7", q7);
		emotionalTotal2.addVariable("q13", q13);
		emotionalTotal2.addVariable("q19", q19);
		emotionalTotal2.addVariable("q28", q28);

        DerivedEntry emotionalClass2 = factory.createDerivedEntry("Emotional Neglect Classification",
                "Emotional Neglect Classification");
		doc.addEntry(emotionalClass2);
		emotionalClass2.setSection(scalesSec);
		emotionalClass2.setDescription("1 = None (or Minimal); 2 = Low (to Moderate); 3 = Moderate (to Severe); 4 = Severe (to Extreme)");
		emotionalClass2.setFormula("if(e>=5&&e<=9,1,if(e>=10&&e<=14,2,if(e>=15&&e<=17,3,4)))");
		emotionalClass2.addVariable("e", emotionalTotal2);

        DerivedEntry emotionalPercentile2 = factory.createDerivedEntry("Emotional Neglect Percentile",
                "Emotional Neglect Percentile");
		doc.addEntry(emotionalPercentile2);
		emotionalPercentile2.setSection(scalesSec);
		emotionalPercentile2.setDescription("For example, if answer is 10 this implies being in the 10th percentile (10-20); if 20 implies being in the 20th percentile (20-30) etc.");
		emotionalPercentile2.setFormula(
				"if(s==1," +
				"if(t<7,10,if(t<8,20,if(t<9,30,if(t<12,40,if(t<14,50,if(t<16,60,if(t<18,70,if(t<21,80,if(t<22,90,if(t<24,95,99))))))))))," +
				"if(t<9,10,if(t<10,20,if(t<12,30,if(t<14,40,if(t<16,50,if(t<17,60,if(t<19,70,if(t<21,80,if(t<22,90,if(t<24,95,99)))))))))) )");
		emotionalPercentile2.addVariable("t", emotionalTotal2);
		emotionalPercentile2.addVariable("s", sex);



        NarrativeEntry pnNar = factory.createNarrativeEntry("PNN", "Physical Neglect");
        doc.addEntry(pnNar);
        pnNar.setSection(scalesSec);
        pnNar.setStyle(NarrativeStyle.HEADER);

        DerivedEntry physicalTotal2 = factory.createDerivedEntry("Physical Neglect Total",
                "Physical Neglect Scale Total");
		doc.addEntry(physicalTotal2);
		physicalTotal2.setSection(scalesSec);
		physicalTotal2.setDescription("=q1+q2+q4+q6+q26");
		physicalTotal2.setFormula("q1+q2+q4+q6+q26");
		physicalTotal2.addVariable("q1", q1);
		physicalTotal2.addVariable("q2", q2);
		physicalTotal2.addVariable("q4", q4);
		physicalTotal2.addVariable("q6", q6);
		physicalTotal2.addVariable("q26", q26);

        DerivedEntry physicalClass2 = factory.createDerivedEntry("Physical Neglect Classification",
                "Physical Neglect Classification");
		doc.addEntry(physicalClass2);
		physicalClass2.setSection(scalesSec);
		physicalClass2.setDescription("1 = None (or Minimal); 2 = Low (to Moderate); 3 = Moderate (to Severe); 4 = Severe (to Extreme)");
		physicalClass2.setFormula("if(p>=5&&p<=7,1,if(p>=8&&p<=9,2,if(p>=10&&p<=12,3,4)))");
		physicalClass2.addVariable("p", physicalTotal2);

        DerivedEntry physicalPercentile2 = factory.createDerivedEntry("Physical Neglect Percentile",
                "Physical Neglect Percentile");
		doc.addEntry(physicalPercentile2);
		physicalPercentile2.setSection(scalesSec);
		physicalPercentile2.setDescription("For example, if answer is 10 this implies being in the 10th percentile (10-20); if 20 implies being in the 20th percentile (20-30) etc.");
		physicalPercentile2.setFormula(
				"if(s==1," +
				"if(t<6,30,if(t<7,50,if(t<9,60,if(t<10,70,if(t<13,80,if(t<15,90,if(t<18,95,99)))))))," +
				"if(t<6,20,if(t<7,40,if(t<8,50,if(t<10,60,if(t<12,70,if(t<14,80,if(t<17,90,if(t<22,95,99)))))))) )");
		physicalPercentile2.addVariable("t", physicalTotal2);
		physicalPercentile2.addVariable("s", sex);


        NarrativeEntry mdNar = factory.createNarrativeEntry("MDN", "Minimalisation/Denial");
        doc.addEntry(mdNar);
        mdNar.setSection(scalesSec);
        mdNar.setStyle(NarrativeStyle.HEADER);

		DerivedEntry minimalisationTotal = factory.createDerivedEntry("Minimalisation/Denial",
                "Minimalisation/Denial Scale Total Score");
		doc.addEntry(minimalisationTotal);
		minimalisationTotal.setSection(scalesSec);
		minimalisationTotal.setFormula("if(q10>4,1,0)+if(q16>4,1,0)+if(q22>4,1,0)");
		minimalisationTotal.addVariable("q10", q10);
		minimalisationTotal.addVariable("q16", q16);
		minimalisationTotal.addVariable("q22", q22);

        return doc;
    }
}

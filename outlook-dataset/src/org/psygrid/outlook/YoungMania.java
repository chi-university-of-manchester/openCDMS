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

package org.psygrid.outlook;

import org.psygrid.common.ValidationRulesWrapper;
import org.psygrid.data.model.hibernate.*;

public class YoungMania extends AssessmentForm {

    public static Document createDocument(Factory factory){

        Document youngMania = factory.createDocument("Young Mania", "The Young Mania Scale");

        createDocumentStatuses(factory, youngMania);

        Section mainSec = factory.createSection("Main section", "Main");
        youngMania.addSection(mainSec);
        mainSec.setDisplayText("Main");
        SectionOccurrence mainSecOcc = factory.createSectionOccurrence("Main Section Occ");
        mainSec.addOccurrence(mainSecOcc);

        NarrativeEntry directions = factory.createNarrativeEntry("Directions");
        youngMania.addEntry(directions);
        directions.setSection(mainSec);
        directions.setDisplayText("Directions: Please read each question below and select the answer which most closely describes the client.");

        OptionEntry elevatedMood = factory.createOptionEntry("Elevated Mood", "Elevated Mood");
        youngMania.addEntry(elevatedMood);
        elevatedMood.setSection(mainSec);
        Option em0 = factory.createOption("Absent", 0);
        Option em1 = factory.createOption("Mildly or possibly increased on questioning", 1);
        Option em2 = factory.createOption("Definite subjective elevation; optimistic, self-confident; cheerful; appropriate to content", 2);
        Option em3 = factory.createOption("Elevated, inappropriate to content; humorous", 3);
        Option em4 = factory.createOption("Euphoric; inappropriate laughter; singing", 4);
        elevatedMood.addOption(em0);
        elevatedMood.addOption(em1);
        elevatedMood.addOption(em2);
        elevatedMood.addOption(em3);
        elevatedMood.addOption(em4);

        OptionEntry increasedMotorActivity = factory.createOptionEntry("Motor Activity", "Increased Motor Activity - Energy");
        youngMania.addEntry(increasedMotorActivity);
        increasedMotorActivity.setSection(mainSec);
        Option ima0 = factory.createOption("Absent", 0);
        Option ima1 = factory.createOption("Subjectively increased", 1);
        Option ima2 = factory.createOption("Animated; gestures increased", 2);
        Option ima3 = factory.createOption("Excessive energy; hyperactive at times; restless (can be calmed)", 3);
        Option ima4 = factory.createOption("Motor excitement; continuous hyperactivity (cannot be calmed)", 4);
        increasedMotorActivity.addOption(ima0);
        increasedMotorActivity.addOption(ima1);
        increasedMotorActivity.addOption(ima2);
        increasedMotorActivity.addOption(ima3);
        increasedMotorActivity.addOption(ima4);

        OptionEntry sexualInterest = factory.createOptionEntry("Sexual Interest", "Sexual Interest");
        youngMania.addEntry(sexualInterest);
        sexualInterest.setSection(mainSec);
        Option si0 = factory.createOption("Normal; not increased", 0);
        Option si1 = factory.createOption("Mildly or possibly increased", 1);
        Option si2 = factory.createOption("Definite subjective increase on questioning", 2);
        Option si3 = factory.createOption("Spontaneous sexual content; elaborates on sexual matters; hypersexual by self report", 3);
        Option si4 = factory.createOption("Overt sexual acts (towards patients, staff or interviewer)", 4);
        sexualInterest.addOption(si0);
        sexualInterest.addOption(si1);
        sexualInterest.addOption(si2);
        sexualInterest.addOption(si3);
        sexualInterest.addOption(si4);

        OptionEntry sleep = factory.createOptionEntry("Sleep", "Sleep");
        youngMania.addEntry(sleep);
        sleep.setSection(mainSec);
        Option s0 = factory.createOption("Reports no decrease in sleep", 0);
        Option s1 = factory.createOption("Sleeping less than normal amount by up to one hour", 1);
        Option s2 = factory.createOption("Sleeping less than normal by more than one hour", 2);
        Option s3 = factory.createOption("Reports decreased need for sleep", 3);
        Option s4 = factory.createOption("Denies needing sleep", 4);
        sleep.addOption(s0);
        sleep.addOption(s1);
        sleep.addOption(s2);
        sleep.addOption(s3);
        sleep.addOption(s4);

        OptionEntry irritability = factory.createOptionEntry("Irritability", "Irritability");
        youngMania.addEntry(irritability);
        irritability.setSection(mainSec);
        Option i0 = factory.createOption("Absent", 0);
        Option i1 = factory.createOption("Subjectively increased", 2);
        Option i2 = factory.createOption("Irritable at times during the interview; recent episodes of anger or annoyance on ward/in community", 4);
        Option i3 = factory.createOption("Frequently irritable during interview; short curt throughout", 6);
        Option i4 = factory.createOption("Hostile, uncooperative; interview impossible", 8);
        irritability.addOption(i0);
        irritability.addOption(i1);
        irritability.addOption(i2);
        irritability.addOption(i3);
        irritability.addOption(i4);

        OptionEntry speech = factory.createOptionEntry("Speech", "Speech (Rate and Amount)");
        youngMania.addEntry(speech);
        speech.setSection(mainSec);
        Option sra0 = factory.createOption("No increase", 0);
        Option sra1 = factory.createOption("Feels talkative", 2);
        Option sra2 = factory.createOption("Increased rate or amount at times; verbose at times", 4);
        Option sra3 = factory.createOption("Push; consistently increased rate and amount, difficult to interpret", 6);
        Option sra4 = factory.createOption("Pressured; uninterruptible; continuous speech", 8);
        speech.addOption(sra0);
        speech.addOption(sra1);
        speech.addOption(sra2);
        speech.addOption(sra3);
        speech.addOption(sra4);

        OptionEntry language = factory.createOptionEntry("Language", "Language - Thought Disorder");
        youngMania.addEntry(language);
        language.setSection(mainSec);
        Option l0 = factory.createOption("Absent", 0);
        Option l1 = factory.createOption("Circumstantial; mild distractibility; quick thoughts", 1);
        Option l2 = factory.createOption("Distractible; loses goal of thought; changes topics frequently; racing thoughts", 2);
        Option l3 = factory.createOption("Flight of ideas; tangentially; difficult to follow; rhyming echolalia", 3);
        Option l4 = factory.createOption("Incoherent; communication impossible", 4);
        language.addOption(l0);
        language.addOption(l1);
        language.addOption(l2);
        language.addOption(l3);
        language.addOption(l4);

        OptionEntry content = factory.createOptionEntry("Content", "Content");
        youngMania.addEntry(content);
        content.setSection(mainSec);
        Option c0 = factory.createOption("Normal", 0);
        Option c1 = factory.createOption("Questionable plans, new interests", 2);
        Option c2 = factory.createOption("Special project(s); hyper religious", 4);
        Option c3 = factory.createOption("Grandiose or paranoid ideas; ideas of reference", 6);
        Option c4 = factory.createOption("Delusions; hallucinations", 8);
        content.addOption(c0);
        content.addOption(c1);
        content.addOption(c2);
        content.addOption(c3);
        content.addOption(c4);

        OptionEntry behaviour = factory.createOptionEntry("Behaviour", "Disruptive Aggressive Behaviour");
        youngMania.addEntry(behaviour);
        behaviour.setSection(mainSec);
        Option b0 = factory.createOption("Absent; co-operative", 0);
        Option b1 = factory.createOption("Sarcastic; loud at times; guarded", 2);
        Option b2 = factory.createOption("Demanding; threats on ward/at home", 4);
        Option b3 = factory.createOption("Threatens interviewer; shouting; interview difficult", 6);
        Option b4 = factory.createOption("Assaultive; destructive; interview impossible", 8);
        behaviour.addOption(b0);
        behaviour.addOption(b1);
        behaviour.addOption(b2);
        behaviour.addOption(b3);
        behaviour.addOption(b4);

        OptionEntry appearance = factory.createOptionEntry("Appearance", "Appearance");
        youngMania.addEntry(appearance);
        appearance.setSection(mainSec);
        Option a0 = factory.createOption("Appropriate dress and grooming", 0);
        Option a1 = factory.createOption("Minimally unkempt", 1);
        Option a2 = factory.createOption("Poorly groomed; moderately dishevelled; overdressed", 2);
        Option a3 = factory.createOption("Dishevelled; partly clothed; garish make-up", 3);
        Option a4 = factory.createOption("Completely unkempt; decorated; bizarre garb", 4);
        appearance.addOption(a0);
        appearance.addOption(a1);
        appearance.addOption(a2);
        appearance.addOption(a3);
        appearance.addOption(a4);

        OptionEntry insight = factory.createOptionEntry("Insight", "Insight");
        youngMania.addEntry(insight);
        insight.setSection(mainSec);
        Option in0 = factory.createOption("Present; admits illness; agrees with need for treatment", 0);
        Option in1 = factory.createOption("Possibly ill", 1);
        Option in2 = factory.createOption("Admits behaviour change, but denies illness", 2);
        Option in3 = factory.createOption("Admits possible change in behaviour, but denies illness", 3);
        Option in4 = factory.createOption("Denies any behaviour change", 4);
        insight.addOption(in0);
        insight.addOption(in1);
        insight.addOption(in2);
        insight.addOption(in3);
        insight.addOption(in4);

        DerivedEntry totalScore = factory.createDerivedEntry("Total", "Total Score");
        youngMania.addEntry(totalScore);
        totalScore.setSection(mainSec);
        totalScore.setFormula("em+ima+si+s+i+sra+l+c+b+a+in");
        totalScore.addVariable("em", elevatedMood);
        totalScore.addVariable("ima", increasedMotorActivity);
        totalScore.addVariable("si", sexualInterest);
        totalScore.addVariable("s", sleep);
        totalScore.addVariable("i", irritability);
        totalScore.addVariable("sra", speech);
        totalScore.addVariable("l", language);
        totalScore.addVariable("c", content);
        totalScore.addVariable("b", behaviour);
        totalScore.addVariable("a", appearance);
        totalScore.addVariable("in", insight);

        ValidationRule notInFuture = ValidationRulesWrapper.instance().getRule("Not in future");
        ValidationRule after2000 = ValidationRulesWrapper.instance().getRule("After 2000");
        DateEntry dateOfAssessment = factory.createDateEntry("Date of assesssment",
                "Date of assessment");
        youngMania.addEntry(dateOfAssessment);
        dateOfAssessment.setSection(mainSec);
        dateOfAssessment.addValidationRule(after2000);
        dateOfAssessment.addValidationRule(notInFuture);

        return youngMania;
    }
}
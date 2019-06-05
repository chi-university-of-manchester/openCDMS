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

package org.psygrid.matisse;

import org.psygrid.common.ValidationRulesWrapper;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.AssessmentForm;

public class BaselineQuestionnaire extends AssessmentForm {

    public static Document createDocument(Factory factory) {

        Document doc = factory.createDocument("Baseline Questionnaire",
                "Baseline Questionnaire - staff");

        createDocumentStatuses(factory, doc);
        addDuringLastTwelveMonths(doc, factory, 1);
        addAccommodation(doc, factory, 2);
        addAlcoholAndDrugs(doc, factory, 3);
        addPatientsEngagements(doc, factory, 4);
        addSelfHarm(doc, factory, 5);

        return doc;
    }

    public static void addDuringLastTwelveMonths(Document doc, Factory factory, int sectionNumber){

        Section sectionA = factory.createSection("During the last 12 months",
                "Last 12 months");

        NarrativeEntry instructions = factory.createNarrativeEntry("Instructions",
        	"During the last 12 months has the patient:");
        doc.addEntry(instructions);
        instructions.setSection(sectionA);

        doc.addSection(sectionA);
        SectionOccurrence sectionAOcc = factory.createSectionOccurrence("During the last 12 months occurrence");
        sectionA.addOccurrence(sectionAOcc);

        OptionEntry employment = factory.createOptionEntry("Employment", "Undertaken ANY full or part-time paid employment");
        doc.addEntry(employment);
        employment.setSection(sectionA);
        employment.setLabel("1");
        employment.addOption(factory.createOption("no", 0));
        employment.addOption(factory.createOption("part-time", 1));
        employment.addOption(factory.createOption("full-time", 2));

        OptionEntry study = factory.createOptionEntry("Study", "Undertaken ANY full or part-time paid study");
        doc.addEntry(study);
        study.setLabel("2");
        study.setSection(sectionA);
        study.addOption(factory.createOption("no", 0));
        study.addOption(factory.createOption("part-time", 1));
        study.addOption(factory.createOption("full-time", 2));

        OptionEntry voluntaryWork = factory.createOptionEntry("Voluntary work", "Undertaken ANY full or part-time voluntary work");
        doc.addEntry(voluntaryWork);
        voluntaryWork.setLabel("3");
        voluntaryWork.setSection(sectionA);
        voluntaryWork.addOption(factory.createOption("no", 0));
        voluntaryWork.addOption(factory.createOption("part-time", 1));
        voluntaryWork.addOption(factory.createOption("full-time", 2));

        OptionEntry attendedDayCentre = factory.createOptionEntry("Attended day-centre", "Attended a day-centre or day hospital");
        doc.addEntry(attendedDayCentre);
        attendedDayCentre.setLabel("4");
        attendedDayCentre.setSection(sectionA);
        attendedDayCentre.addOption(factory.createOption("no", 0));
        attendedDayCentre.addOption(factory.createOption("erratic attendance", 1));
        attendedDayCentre.addOption(factory.createOption("regular attendance", 2));

        OptionEntry sports = factory.createOptionEntry("Sports", "Undertaken sports/physical activity");
        doc.addEntry(sports);
        sports.setSection(sectionA);
        sports.setLabel("5");
        sports.addOption(factory.createOption("no", 0));
        sports.addOption(factory.createOption("occasionally", 1));
        sports.addOption(factory.createOption("regularly", 2));

        OptionEntry hobby = factory.createOptionEntry("Hobby", "Pursued a hobby or interest");
        doc.addEntry(hobby);
        hobby.setLabel("6");
        hobby.setSection(sectionA);
        hobby.addOption(factory.createOption("no", 0));
        hobby.addOption(factory.createOption("occasionally", 1));
        hobby.addOption(factory.createOption("regularly", 2));
    }

    public static void addAccommodation(Document doc, Factory factory, int sectionNumber){

        Section sectionB = factory.createSection("Accommodation",
                "Accommodation");
        doc.addSection(sectionB);
        SectionOccurrence sectionBOcc = factory.createSectionOccurrence("Regarding Accommodation occurrence");
        sectionB.addOccurrence(sectionBOcc);
        OptionEntry accommodation = factory.createOptionEntry("Accommodation", "Regarding accommodation does the patient currently live:");
        doc.addEntry(accommodation);
        accommodation.setLabel("1");
        accommodation.setSection(sectionB);
        accommodation.addOption(factory.createOption("Independent: alone/children", 1));
        accommodation.addOption(factory.createOption("Independent: with partner", 2));
        accommodation.addOption(factory.createOption("Independent: with family", 3));
        accommodation.addOption(factory.createOption("Independent: with friends", 4));
        accommodation.addOption(factory.createOption("Supported accommodation: access to support", 5));
        accommodation.addOption(factory.createOption("Supported accommodation: part-time support", 6));
        accommodation.addOption(factory.createOption("Supported accommodation: full-time support", 7));
        accommodation.addOption(factory.createOption("Temporary accommodation", 8));
        accommodation.addOption(factory.createOption("Homeless", 9));
    }

    public static void addAlcoholAndDrugs(Document doc, Factory factory, int sectionNumber){

		ValidationRule oneToFive = ValidationRulesWrapper.instance().getRule("OneToFive");

    	Section sectionC = factory.createSection("Alchol and Drugs",
                "Alcohol and Drugs");
        doc.addSection(sectionC);


        SectionOccurrence sectionCOcc = factory.createSectionOccurrence("Alcohol and Drugs");
        sectionC.addOccurrence(sectionCOcc);


        NarrativeEntry instructions = factory.createNarrativeEntry("Alcohol and Drugs",
        		"Please give your rating for your client's use of both alcohol and drugs. \n" +
        		"This gives a rating of subject's alcohol and drug use over last 6 months (or " +
        		"6 months prior to institutionalisation if in institution). The ratings are as follows: \n" +
        		"1=abstinent \n" +
        		"2=use without impairment (no social/psychological or physical problems due to use) \n" +
        		"3=abuse (persistent or recurrent social/psychological/physical problems secondary to use" +
        		" lasting at least 1 month) \n" +
        		"4=dependence (large amounts of time spent in alcohol or drug seeking or use/ longer periods " +
        		"than intended involved in alcohol seeking or use/continued use despite advice to stop " +
        		"/tolerance/ withdrawal symptoms/ intoxication or withdrawal interferes with other activities/ " +
        		"alcohol or drugs used to avoid withdrawal symptoms) \n" +
        		"5=dependence with institutionalisation (problems secondary to alcohol or drugs so severe they make " +
        		"non-institutional living difficult).");
        doc.addEntry(instructions);
        instructions.setSection(sectionC);

        NumericEntry alcoholScore = factory.createNumericEntry("Alcohol rating",
                										"Alcohol rating");
        alcoholScore.setLabel("1");
        doc.addEntry(alcoholScore);
        alcoholScore.setSection(sectionC);
        alcoholScore.setDescription("Enter a value 1-5. 1=abstinent, 2=use without impairment, 3=abuse, 4=dependence, 5=dependence with" +
        		" institutionalisation");
        alcoholScore.addValidationRule(oneToFive);

        NumericEntry drugScore = factory.createNumericEntry("Drug rating",
                										"Drug rating");
        drugScore.setLabel("2");
        doc.addEntry(drugScore);
        drugScore.setDescription("Enter a value 1-5. 1=abstinent, 2=use without impairment, 3=abuse, 4=dependence, 5=dependence with" +
		" institutionalisation");
        drugScore.setSection(sectionC);
        drugScore.addValidationRule(oneToFive);
    }

    public static void addPatientsEngagements(Document doc, Factory factory, int sectionNumber){

    	Section sectionD = factory.createSection("Patients' engagement",
                "Patients' engagement and acceptance of treatment");
        doc.addSection(sectionD);
        SectionOccurrence sectionDOcc = factory.createSectionOccurrence("Patients' engagement");
        sectionD.addOccurrence(sectionDOcc);

        OptionEntry feelingsTowardsWorker = factory.createOptionEntry("Feeling towards worker",
                "This rating concerns: How the client feels about you as a worker");
        doc.addEntry(feelingsTowardsWorker);
        feelingsTowardsWorker.setSection(sectionD);
        feelingsTowardsWorker.setLabel("1");
        feelingsTowardsWorker.addOption(factory.createOption("The client is well disposed towards me and looks forward to my visits", 4));
        feelingsTowardsWorker.addOption(factory.createOption("The client is mildly positive towards me", 3));
        feelingsTowardsWorker.addOption(factory.createOption("The client is neutral in attitude towards me", 2));
        feelingsTowardsWorker.addOption(factory.createOption("The client is suspicious of my intent or mildly hostile", 1));
        feelingsTowardsWorker.addOption(factory.createOption("The client is overtly hostile and antagonistic towards me", 0));

        OptionEntry clientEngaged = factory.createOptionEntry("Degree client engaged",
                "This rating concerns: The degree to which the client can be engaged");
		doc.addEntry(clientEngaged);
		clientEngaged.setSection(sectionD);
		clientEngaged.setLabel("2");
		clientEngaged.addOption(factory.createOption("The client goes to great lengths to avoid contact", 0));
		clientEngaged.addOption(factory.createOption("The client generally avoids contact and only occasionally agrees to be seen", 1));
		clientEngaged.addOption(factory.createOption("The client does not seek contact but usually agrees to be seen", 2));
		clientEngaged.addOption(factory.createOption("The client is easy to contact and reliable over appointments", 3));
		clientEngaged.addOption(factory.createOption("The client frequently initiates contact", 4));

        OptionEntry attitudeToHelp = factory.createOptionEntry("Attitude to Help",
                "This rating concerns: The client's attitude to help");
		doc.addEntry(attitudeToHelp);
		attitudeToHelp.setSection(sectionD);
		attitudeToHelp.setLabel("3");
		attitudeToHelp.addOption(factory.createOption("The client is keen on being helped and is an active participant in making plans", 3));
		attitudeToHelp.addOption(factory.createOption("The client is prepared to accept help but there are difficulties in agreeing a common plan", 2));
		attitudeToHelp.addOption(factory.createOption("The client claims not to need help but is prepared after some persuasion to accept some degree of intervention", 1));
		attitudeToHelp.addOption(factory.createOption("The client insists no help is needed and actively resists all attempts at intervention", 0));

        OptionEntry engagesWithOthers = factory.createOptionEntry("Engages with Others",
                "This rating concerns: The way the client engages with others");
		doc.addEntry(engagesWithOthers);
		engagesWithOthers.setSection(sectionD);
		engagesWithOthers.setLabel("4");
		engagesWithOthers.addOption(factory.createOption("The client is actively hostile towards others", 0));
		engagesWithOthers.addOption(factory.createOption("The client actively avoids most contact with others", 1));
		engagesWithOthers.addOption(factory.createOption("The client passively avoids others, company may be tolerated silently", 2));
		engagesWithOthers.addOption(factory.createOption("Variable engagement - unpredictably withdrawn or friendly", 3));
		engagesWithOthers.addOption(factory.createOption("Appropriate social engagement with spontaneous conversation", 4));

        DerivedEntry score = factory.createDerivedEntry(
                "Total Engagment Score", "Total Engagment Score");
        doc.addEntry(score);
        score.setSection(sectionD);
        score.addVariable("a", feelingsTowardsWorker);
        score.addVariable("b", clientEngaged);
        score.addVariable("c", attitudeToHelp);
        score.addVariable("d", engagesWithOthers);
        score.setFormula("a+b+c+d");

    }

    public static void addSelfHarm(Document doc, Factory factory, int sectionNumber){

    	Section sectionE = factory.createSection("Harm to self/others",
                "Harm to self/others");
        doc.addSection(sectionE);
        SectionOccurrence sectionEOcc = factory.createSectionOccurrence("Harm to self occurrence");
        sectionE.addOccurrence(sectionEOcc);

        OptionEntry deliberateHarm = factory.createOptionEntry("Deliberate Harm",
                "Has he or she deliberately harmed him/herself in the past 12 months?");
        doc.addEntry(deliberateHarm);
        deliberateHarm.setSection(sectionE);
        deliberateHarm.setLabel("i");
        deliberateHarm.addOption(factory.createOption("No, has not deliberately self-harmed in past 12 months", 0));

        Option yesOnceOption = factory.createOption("Yes, once", 1);
        Option yesTwiceOption = factory.createOption("Yes, 2-3 times", 2);
        Option yesManyOption = factory.createOption("4 or more times", 3);

        deliberateHarm.addOption(yesOnceOption);
        deliberateHarm.addOption(yesTwiceOption);
        deliberateHarm.addOption(yesManyOption);

        OptionEntry consequences = factory.createOptionEntry("Consequences",
                "If yes, describe the consequences of the most serious act.");
		doc.addEntry(consequences);
		consequences.setSection(sectionE);
		consequences.setLabel("ii");
		consequences.addOption(factory.createOption("No, never self-harmed", 0));
		consequences.addOption(factory.createOption("Yes, physical effects did not require admission to general hospital", 1));
		consequences.addOption(factory.createOption("Yes, required admission at least overnight to general hospital,"
								+ " but no lasting effects and no admission to intensive care unit", 2));
		consequences.addOption(factory.createOption("Self harm is likely to have caused lasting physical damage (other than,"
				+ " superficial scarring) and/or required admission to intensive care unit", 3));
		consequences.setEntryStatus(EntryStatus.DISABLED);
		createOptionDependent(factory, yesOnceOption, consequences);
		createOptionDependent(factory, yesTwiceOption, consequences);
		createOptionDependent(factory, yesManyOption, consequences);

		CompositeEntry selfHarmForms = factory.createComposite("Self Harm");
		selfHarmForms.setDisplayText("What form(s) did the self-harm take? (all which apply)");
		doc.addEntry(selfHarmForms);
		selfHarmForms.setSection(sectionE);
		selfHarmForms.setEntryStatus(EntryStatus.DISABLED);

        OptionEntry selfHarm = factory.createOptionEntry("Forms",
                "Add all self harm forms which apply");
		selfHarmForms.addEntry(selfHarm);
		selfHarmForms.setLabel("iii");
		selfHarm.addOption(factory.createOption("overdose or other form of self-poisoning ", 1));
		selfHarm.addOption(factory.createOption("superficial self-cutting", 2));
		selfHarm.addOption(factory.createOption("cutting deep enough to involve major blood vessels or organs", 3));
		selfHarm.addOption(factory.createOption("suicide attempt using violent method e.g. shooting self" +
				", self-immolation, jumping from high place or in front of moving vehicle," +
				" self-asphyxiation by hanging or carbon monoxide", 4));
		Option otherElse = factory.createOption("other, please describe", 5);
		otherElse.setTextEntryAllowed(true);
		selfHarm.addOption(otherElse);
		createOptionDependent(factory, yesOnceOption, selfHarmForms);
		createOptionDependent(factory, yesTwiceOption, selfHarmForms);
		createOptionDependent(factory, yesManyOption, selfHarmForms);

		OptionEntry assaulted = factory.createOptionEntry("Assault", "Has the subject ever" +
                " assaulted someone, committed a sexual offence, repeatedly threatened to inflict serious harm" +
                " on someone or threatened someone with a weapon?");
		doc.addEntry(assaulted);
		assaulted.setSection(sectionE);
		assaulted.setLabel("iv");
		assaulted.addOption(factory.createOption("no", 0));

		Option assaultYesOnceOption = factory.createOption("yes, once", 1);
		Option assaultYesTwiceOption = factory.createOption("yes, 2 or 3 times", 2);
		Option assaultYesManyOption = factory.createOption("yes, 4 or more times", 3);

		assaulted.addOption(assaultYesOnceOption);
		assaulted.addOption(assaultYesTwiceOption);
		assaulted.addOption(assaultYesManyOption);

		OptionEntry severity = factory.createOptionEntry("Assault severity",
                " If so, which of the following best describes the severity of the most serious act?");
		doc.addEntry(severity);
		severity.setSection(sectionE);
		severity.setLabel("v");
		severity.addOption(factory.createOption("nil", 0));
		severity.addOption(factory.createOption("no assault, but minor verbal aggression", 1));
		severity.addOption(factory.createOption("no assault, but has made repeated threats to inflict" +
				" significant harm on someone or has threatened someone with a weapon", 2));
		severity.addOption(factory.createOption("assault not resulting in any need for victim" +
				" to have hospital in-patient treatment", 3));
		severity.addOption(factory.createOption("assault resulting in victim needing hospital" +
				" in-patient treatment, but not in lasting disability assault resulting in lasting" +
				" disability homicide", 4));
		severity.addOption(factory.createOption("assault resulting in lasting disability", 5));
		severity.addOption(factory.createOption("homicide", 6));
		severity.setEntryStatus(EntryStatus.DISABLED);
		createOptionDependent(factory, assaultYesOnceOption, severity);
		createOptionDependent(factory, assaultYesTwiceOption, severity);
		createOptionDependent(factory, assaultYesManyOption, severity);

    }



}


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

/**
 * @author Rob Harper
 *
 */
public class FollowUpQuestionnaire extends AssessmentForm {

	public static Document createDocument(Factory factory){


        Document doc = factory.createDocument("Follow Up Questionnaire (Staff)",
                "Follow Up Questionnaire (Staff)");
        createDocumentStatuses(factory, doc);

        Section main = factory.createSection("Main", "Main");
        doc.addSection(main);
        SectionOccurrence mainOcc = factory.createSectionOccurrence("Main occurrence");
        main.addOccurrence(mainOcc);

        NarrativeEntry blockA = factory.createNarrativeEntry("A", "A. During the last 12 Months has the patient:");
        doc.addEntry(blockA);
        blockA.setSection(main);
        blockA.setStyle(NarrativeStyle.HEADER);

        OptionEntry employment = factory.createOptionEntry("Full or part-time employment", "Undertaken ANY full or part-time employment");
        doc.addEntry(employment);
        employment.setSection(main);
        createOptions(factory, employment, new String[]{"No", "Part-time", "Full-time"}, new int[]{0, 1, 2});

        OptionEntry study = factory.createOptionEntry("Full or part-time study", "Undertaken ANY full or part-time study");
        doc.addEntry(study);
        study.setSection(main);
        createOptions(factory, study, new String[]{"No", "Part-time", "Full-time"}, new int[]{0, 1, 2});

        OptionEntry voluntary = factory.createOptionEntry("Full or part-time voluntary work", "Undertaken ANY full or part-time voluntary work");
        doc.addEntry(voluntary);
        voluntary.setSection(main);
        createOptions(factory, voluntary, new String[]{"No", "Part time", "Full time"}, new int[]{0, 1, 2});

        OptionEntry dayCentre = factory.createOptionEntry("Day centre or hospital", "Attended a day centre or day hospital");
        doc.addEntry(dayCentre);
        dayCentre.setSection(main);
        createOptions(factory, dayCentre, new String[]{"No", "Erratic attendance", "Regular attendance"}, new int[]{0, 1, 2});

        OptionEntry sports = factory.createOptionEntry("Sports/physical activity", "Undertaken sports/physical activity");
        doc.addEntry(sports);
        sports.setSection(main);
        createOptions(factory, sports, new String[]{"No", "Occasionally", "Regularly"}, new int[]{0, 1, 2});

        OptionEntry hobby = factory.createOptionEntry("Hobby or interest", "Pursued a hobby or interest");
        doc.addEntry(hobby);
        hobby.setSection(main);
        createOptions(factory, hobby, new String[]{"No", "Occasionally", "Regularly"}, new int[]{0, 1, 2});

        NarrativeEntry blockB = factory.createNarrativeEntry("B", "B. Regarding accommodation does the patient currently live:");
        doc.addEntry(blockB);
        blockB.setSection(main);
        blockB.setStyle(NarrativeStyle.HEADER);

        OptionEntry currentAccom = factory.createOptionEntry("Accomodation", "Does the patient currently live");
        doc.addEntry(currentAccom);
        currentAccom.setSection(main);
        createOptions(factory, currentAccom, new String[]{"Independent: alone/ children",
        												  "Independent: with partner",
        												  "Independent: with family",
        												  "Independent: with friends",
        												  "Supported accommodation: access to support",
        												  "Supported accommodation: part-time support",
        												  "Supported accommodation: full-time support",
        												  "Temporary accommodation",
        												  "Homeless"},
        									new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9});

        NarrativeEntry blockC = factory.createNarrativeEntry("C", "C. Please give your rating below for your client's use of both alcohol and drugs:");
        doc.addEntry(blockC);
        blockC.setSection(main);
        blockC.setStyle(NarrativeStyle.HEADER);

        OptionEntry alcohol = factory.createOptionEntry("Alcohol rating", "Alcohol rating");
        doc.addEntry(alcohol);
        alcohol.setSection(main);
        createOptions(factory, alcohol, new String[]{"Abstinent",
        											 "Use without impairment",
        											 "Abuse",
        											 "Dependence",
        											 "Dependence with institutionalisation"},
        								new int[] {1, 2, 3, 4, 5},
        								new String[]{null,
        											 "no social/psychological or physical problems due to use",
        											 "persistent or recurrent social/psychological/physical problems secondary to use lasting at least 1 month",
        											 "large amounts of time spent in alcohol or drug seeking or use/ longer periods than intended involved in alcohol seeking or use/continued use despite advice to stop/ tolerance/ withdrawal symptoms/ intoxication or withdrawal interferes with other activities/ alcohol or drugs used to avoid withdrawal symptoms",
        											 "problems secondary to alcohol or drugs so severe they make non-institutional living difficult"});

        OptionEntry drugs = factory.createOptionEntry("Drug rating", "Drug rating");
        doc.addEntry(drugs);
        drugs.setSection(main);
        createOptions(factory, drugs, new String[]{"Abstinent",
        											 "Use without impairment",
        											 "Abuse",
        											 "Dependence",
        											 "Dependence with institutionalisation"},
        								new int[] {1, 2, 3, 4, 5},
        								new String[]{null,
        											 "no social/psychological or physical problems due to use",
        											 "persistent or recurrent social/psychological/physical problems secondary to use lasting at least 1 month",
        											 "large amounts of time spent in alcohol or drug seeking or use/ longer periods than intended involved in alcohol seeking or use/continued use despite advice to stop/ tolerance/ withdrawal symptoms/ intoxication or withdrawal interferes with other activities/ alcohol or drugs used to avoid withdrawal symptoms",
        											 "problems secondary to alcohol or drugs so severe they make non-institutional living difficult"});

        NarrativeEntry blockD = factory.createNarrativeEntry("D", "D. Patients' engagement and acceptance of treatment");
        doc.addEntry(blockD);
        blockD.setSection(main);
        blockD.setStyle(NarrativeStyle.HEADER);

        OptionEntry worker = factory.createOptionEntry("How the client feels about you as a worker", "This rating concerns: How the client feels about you as a worker");
        doc.addEntry(worker);
        worker.setSection(main);
        worker.setLabel("1");
        createOptions(factory, worker, new String[]{"The client is well disposed towards me and looks forward to my visits",
        											"The client is mildly positive towards me",
        											"The client is neutral in attitude towards me",
        											"The client is suspicious of my intent or mildly hostile",
        											"The client is overtly hostile and antagonistic towards me"},
        							   new int[]{4, 3, 2, 1, 0});

        OptionEntry degreeEngaged = factory.createOptionEntry("The degree to which the client can be engaged", "This rating concerns: The degree to which the client can be engaged");
        doc.addEntry(degreeEngaged);
        degreeEngaged.setSection(main);
        degreeEngaged.setLabel("2");
        createOptions(factory, degreeEngaged, new String[]{"The client goes to great lengths to avoid contact",
        												   "The client generally avoids contact and only occasionally agrees to be seen",
        												   "The client does not seek contact but usually agrees to be seen",
        												   "The client is easy to contact and reliable over appointments",
        												   "The client frequently initiates contact"},
        									  new int[]{0, 1, 2, 3, 4});

        OptionEntry attitudeToHelp = factory.createOptionEntry("The client's attitude to help", "This rating concerns: The client's attitude to help");
        doc.addEntry(attitudeToHelp);
        attitudeToHelp.setSection(main);
        attitudeToHelp.setLabel("3");
        createOptions(factory, attitudeToHelp, new String[]{"The client is keen on being helped and is an active participant in making plans",
        													"The client is prepared to accept help but there are difficulties in agreeing a common plan",
        													"The client claims not to need help but is prepared after some persuasion to accept some degree of intervention",
        													"The client insists no help is needed and actively resists all attempts at intervention"},
        									   new int[]{3, 2, 1, 0});

        OptionEntry engagesOthers = factory.createOptionEntry("The way the client engages with others", "This rating concerns: The way the client engages with others");
        doc.addEntry(engagesOthers);
        engagesOthers.setSection(main);
        engagesOthers.setLabel("4");
        createOptions(factory, engagesOthers, new String[]{"The client is actively hostile towards others",
        												   "The client actively avoids most contact with others",
        												   "The client passively avoids others, company may be tolerated silently",
        												   "Variable engagement - unpredictably withdrawn or friendly",
        												   "Appropriate social engagement with spontaneous conversation"},
        									   new int[]{0, 1, 2, 3, 4});

        DerivedEntry totalEngagement = factory.createDerivedEntry("Total engagement", "Total engagement");
        doc.addEntry(totalEngagement);
        totalEngagement.setSection(main);
        totalEngagement.setFormula("w+d+a+e");
        totalEngagement.addVariable("w", worker);
        totalEngagement.addVariable("d", degreeEngaged);
        totalEngagement.addVariable("a", attitudeToHelp);
        totalEngagement.addVariable("e", engagesOthers);

        NarrativeEntry blockE = factory.createNarrativeEntry("E", "E. Harm to self/others");
        doc.addEntry(blockE);
        blockE.setSection(main);
        blockE.setStyle(NarrativeStyle.HEADER);

        OptionEntry harmedSelf = factory.createOptionEntry("Harmed self", "Has he or she deliberately harmed him/herself in the past 12 months?");
        doc.addEntry(harmedSelf);
        harmedSelf.setSection(main);
        harmedSelf.setLabel("i");
        createOptions(factory, harmedSelf, new String[]{"No, has not deliberately self-harmed in past 12 months",
        												"Yes, once",
        												"Yes, 2-3 times",
        												"4 or more times"},
        								   new int[]{0, 1, 2, 3});

        OptionEntry harmedConsequences = factory.createOptionEntry("Harmed consequences", "If yes, describe the consequences of the most serious act.");
        doc.addEntry(harmedConsequences);
        harmedConsequences.setSection(main);
        harmedConsequences.setLabel("ii");
        createOptions(factory, harmedConsequences, new String[]{"No, never self-harmed",
        														"Yes, physical effects did not require admission to general hospital",
        														"Yes, required admission at least overnight to general hospital, but no lasting effects and no admission to intensive care unit",
        														"Self harm is likely to have caused lasting physical damage (other than superficial scarring) and/or required admission to intensive care unit"},
        										   new int[]{0, 1, 2, 3});

        CompositeEntry harmFormComp = factory.createComposite("Form of harm composite", "What form(s) did the self-harm take? (all which apply)");
        doc.addEntry(harmFormComp);
        harmFormComp.setSection(main);
        harmFormComp.setLabel("iii");

        OptionEntry harmForm = factory.createOptionEntry("Form of harm", "Form of harm");
        harmFormComp.addEntry(harmForm);
        harmForm.setSection(main);
        createOptions(factory, harmForm, new String[]{"Overdose or other form of self-poisoning",
        											  "Superficial self-cutting",
        											  "Cutting deep enough to involve major blood vessels or organs",
        											  "Suicide attempt using violent method",
        											  "Other, please describe"},
        								 new int[]{1, 2, 3, 4, 5},
        								 new String[]{null,
        											  null,
        											  null,
        											  " e.g. shooting self, self-immolation, jumping from high place or in front of moving vehicle, self-asphyxiation by hanging or carbon monoxide",
        											  null});

        OptionEntry assaulted = factory.createOptionEntry("Assaulted", "Has the subject ever assaulted someone, committed a sexual offence, repeatedly threatened to inflict serious harm on someone or threatened someone with a weapon?");
        doc.addEntry(assaulted);
        assaulted.setSection(main);
        assaulted.setLabel("iv");
        createOptions(factory, assaulted, new String[]{"No",
        											   "Yes, once",
        											   "Yes, 2 or 3 times",
        											   "Yes, 4 or more times"},
        								  new int[]{0, 1, 2, 3});

        OptionEntry assaultSeverity = factory.createOptionEntry("Severity of the most serious act", "If so, which of the following best describes the severity of the most serious act?");
        doc.addEntry(assaultSeverity);
        assaultSeverity.setSection(main);
        assaultSeverity.setLabel("v");
        createOptions(factory, assaultSeverity, new String[]{"Nil",
        													 "No assault, but minor verbal aggression",
        													 "No assault, but has made repeated threats to inflict significant harm on someone or has threatened someone with a weapon",
        													 "Assault not resulting in any need for victim to have hospital in-patient treatment",
        													 "Assault resulting in victim needing hospital in-patient treatment, but not in lasting disability",
        													 "Assault resulting in lasting disability",
        													 "Homicide"},
        										new int[]{0, 1, 2, 3, 4, 5, 6});

        NarrativeEntry blockF = factory.createNarrativeEntry("F", "F. Patient Preference");
        doc.addEntry(blockF);
        blockF.setSection(main);
        blockF.setStyle(NarrativeStyle.HEADER);

        OptionEntry preference = factory.createOptionEntry("Treatment arm preference", "Please could you tell if this participant had a preference for one of the three treatment arms of the study?");
        doc.addEntry(preference);
        preference.setSection(main);
        createOptions(factory, preference, new String[]{"They would prefer to receive treatment as usual",
        												"They would prefer to receive treatment as usual plus a place in an activity group",
        												"They would prefer to receive treatment as usual plus a place in an art therapy group",
        												"They do not have any preference"},
        								   new int[]{1, 2, 3, 4});

        NumericEntry preferenceStrength = factory.createNumericEntry("Strength of preference", "If they had a preference, how strong is this?");
        doc.addEntry(preferenceStrength);
        preferenceStrength.setSection(main);
        preferenceStrength.setDescription("Enter a number between 1 and 7, where 1 represents 'Not strong at all' and 7 represents 'Very strong preference'");
        preferenceStrength.addValidationRule(ValidationRulesWrapper.instance().getRule("OneToSeven"));

        NarrativeEntry blockG = factory.createNarrativeEntry("G", "G. Global Assessment of Functioning (GAF) Scale");
        doc.addEntry(blockG);
        blockG.setSection(main);
        blockG.setStyle(NarrativeStyle.HEADER);

        NumericEntry gaf = factory.createNumericEntry("GAF", "Please rate the patients overall functioning in the last 4 weeks using the scale below");
        doc.addEntry(gaf);
        gaf.setSection(main);
        gaf.setDescription("Enter a value between 1 and 100");
        gaf.addValidationRule(ValidationRulesWrapper.instance().getRule("OneToHundred"));

        return doc;
	}
}

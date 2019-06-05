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

import java.util.ArrayList;
import java.util.List;

import org.psygrid.common.TransformersWrapper;
import org.psygrid.data.model.hibernate.*;

public class Opcrit extends AssessmentForm {

	public static Document createDocument(Factory factory) {

		Document doc = factory.createDocument("Opcrit Data Entry Sheet",
                "Opcrit Data Entry Record");

		createDocumentStatuses(factory, doc);




		/*
		 * Opcrit for Windows (v4),
		 * Item Guidelines & Ratings.
		 * &copy; 1992,1993,1997,2004 P.McGuffin, Anne Farmer
		 */

		Section detailsSec = factory.createSection("Details & History");
		doc.addSection(detailsSec);
		detailsSec.setDisplayText("Details & History");
		SectionOccurrence detailsSecOcc = factory.createSectionOccurrence(
                "Details & History Section Occurrence");
		detailsSec.addOccurrence(detailsSecOcc);

		NarrativeEntry instructions = factory.createNarrativeEntry("Instructions",
                "Instructions: Select an answer for each question");
		doc.addEntry(instructions);
		instructions.setSection(detailsSec);

		OptionEntry q1 = factory.createOptionEntry("1", "Source of Rating");
		q1.addOption(factory.createOption("Hospital case notes (charts)", 1));
		q1.addOption(factory.createOption("Structured interview with subject", 2));
		q1.addOption(factory.createOption("Prepared abstract", 3));
		q1.addOption(factory.createOption("Interview with informant", 4));
		q1.addOption(factory.createOption("Combined sources including structured interview", 5));
		q1.addOption(factory.createOption("Combined sources not including structured interview", 6));
		doc.addEntry(q1);
		q1.setSection(detailsSec);
		q1.setLabel("1");

		OptionEntry q2 = factory.createOptionEntry("2", "Time Frame");
		q2.addOption(factory.createOption("Present or most recent episode", 1));
		q2.addOption(factory.createOption("Worst ever episode", 2));
		q2.addOption(factory.createOption("Lifetime ever occurrence of symptoms and signs", 3));
		q2.addOption(factory.createOption("Other specified episode or time period", 4));
		doc.addEntry(q2);
		q2.setSection(detailsSec);
		q2.setLabel("2");

		OptionEntry q3 = factory.createOptionEntry("3", "Gender");
		q3.addOption(factory.createOption("Male", 0));
		q3.addOption(factory.createOption("Female", 1));
		doc.addEntry(q3);
		q3.setSection(detailsSec);
		q3.setLabel("3");

		TextEntry q4 = factory.createTextEntry("4", "Age of onset");
		q4.setDescription("This should be given to the nearest year and is defined as the earliest age at which medical " +
				"advice was sought for psychiatric reasons or at which symptoms began to cause subjective distress or " +
		"impair functioning (enter age in years, eg 35).");
		doc.addEntry(q4);
		q4.setSection(detailsSec);
		q4.setLabel("4");

		OptionEntry q5 = factory.createOptionEntry("5", "Mode of onset");
		q5.setDescription("Rate up if in any doubt");
		q5.addOption(factory.createOption("Abrupt onset definable to within hours or days", 1));
		q5.addOption(factory.createOption("Acute onset definable to within 1 week", 2));
		q5.addOption(factory.createOption("Moderately acute onset definable within 1 month", 3));
		q5.addOption(factory.createOption("Gradual onset over period up to 6 months", 4));
		q5.addOption(factory.createOption("Insidious onset over period greater than 6 months", 5));
		doc.addEntry(q5);
		q5.setSection(detailsSec);
		q5.setLabel("5");

		OptionEntry q6 = factory.createOptionEntry("6", "Single (subject never married /lived as married)");
		q6.addOption(factory.createOption("Married", 0));
		q6.addOption(factory.createOption("Single", 1));
		doc.addEntry(q6);
		q6.setSection(detailsSec);
		q6.setLabel("6");

		OptionEntry q7 = factory.createOptionEntry("7", "Unemployed at onset");
		q7.setDescription("The subject was not employed at onset as defined above. Women working full time in the home score as if " +
		"employed. Students attending classes on full time course, score as if employed.");
		q7.addOption(factory.createOption("Employed", 0));
		q7.addOption(factory.createOption("Unemployed", 1));
		doc.addEntry(q7);
		q7.setSection(detailsSec);
		q7.setLabel("7");

		TextEntry q8 = factory.createTextEntry("8", "Duration of illness in weeks(max=99)");
		q8.setDescription("Total duration of illness includes prodromal and residual disabilities as well as the active phase " +
				"of illness. In psychotic disorder prodromal/residual phase symptoms count as any 2 of the following before " +
				"or after an active episode: Social isolation/marked impairment in role/markedly peculiar behaviour/marked " +
				"impairment in personal hygiene/blunted, flat or inappropriate affect/digressive, vague, over-elaborate speech/odd " +
		"or bizarre ideation/unusual perceptual experiences.");
		doc.addEntry(q8);
		q8.setSection(detailsSec);
		q8.setLabel("8");

		OptionEntry q9 = factory.createOptionEntry("9", "Poor work adjustment");
		q9.setDescription("Refers to work history before onset of illness. It should be scored if the patient was unable to keep " +
				"any job for more than 6 months, had a history of frequent changes of job or was only able to sustain a job well " +
				"below that expected by his educational level or training at time of first psychiatric contact. Also score " +
				"positively for a persistently very poor standard of housework (housewives) and badly failing to keep up with " +
		"studies (students).");
		q9.addOption(factory.createOption("No", 0));
		q9.addOption(factory.createOption("Yes", 1));
		doc.addEntry(q9);
		q9.setSection(detailsSec);
		q9.setLabel("9");

		OptionEntry q10 = factory.createOptionEntry("10", "Poor premorbid social adjustment");
		q10.setDescription("Patient found difficulty entering or maintaining normal social relationships, showed persistent social " +
		"isolation, withdrawal or maintained solitary interests prior to onset of psychotic symptoms.");
		q10.addOption(factory.createOption("No", 0));
		q10.addOption(factory.createOption("Yes", 1));
		doc.addEntry(q10);
		q10.setSection(detailsSec);
		q10.setLabel("10");

		OptionEntry q11 = factory.createOptionEntry("11", "Premorbid personality disorder");
		q11.setDescription("Evidence of inadequate/schizoid/schizotypal/paranoid/cyclothymic/psychopathic/sociopathic " +
		"personality disorder present since adolescence and prior to the onset of psychotic symptoms.");
		q11.addOption(factory.createOption("No", 0));
		q11.addOption(factory.createOption("Yes", 1));
		doc.addEntry(q11);
		q11.setSection(detailsSec);
		q11.setLabel("11");

		OptionEntry q12 = factory.createOptionEntry("12", "Alcohol/drug abuse within one year of onset of psychotic symptoms");
		q12.setDescription("Alcohol abuse where quantity is excessive (rater judgement) where alcohol related " +
				"complications occur, during the year prior to first psychiatric contact (rated strictly as " +
				"exclusion criteria for some definitions of schizophrenia). Drug abuse where non-prescribed drugs " +
				"are repeatedly taken or prescribed drugs are used in excessive quantities and without medical " +
		"supervision in year prior to first psychiatric contact.");
		q12.addOption(factory.createOption("No", 0));
		q12.addOption(factory.createOption("Yes", 1));
		doc.addEntry(q12);
		q12.setSection(detailsSec);
		q12.setLabel("12");

		OptionEntry q13 = factory.createOptionEntry("13", "Family history of schizophrenia");
		q13.setDescription("Definite history of schizophrenia in first or second degree relative.");
		q13.addOption(factory.createOption("No", 0));
		q13.addOption(factory.createOption("Yes", 1));
		doc.addEntry(q13);
		q13.setSection(detailsSec);
		q13.setLabel("13");

		OptionEntry q14 = factory.createOptionEntry("14", "Family history of other psychiatric disorder");
		q14.setDescription("First or second degree relative has another psychiatric disorder severe enough to " +
		"warrant psychiatric referral.");
		q14.addOption(factory.createOption("No", 0));
		q14.addOption(factory.createOption("Yes", 1));
		doc.addEntry(q14);
		q14.setSection(detailsSec);
		q14.setLabel("14");

		OptionEntry q15 = factory.createOptionEntry("15", "Coarse brain disease prior to onset");
		q15.setDescription("There is evidence from physical examination and/or special investigations of " +
				"physical illness that could explain all or most mental symptoms. This may include an " +
				"overt brain lesion (or lesions),marked metabolic disturbance, or drug induced state known " +
				"to cause psychotic disturbance, confusion or alteration of conscious level.      " +
				"Non specific abnormalities (eg enlarged lateral ventricles on brain scan) should not be " +
		"included.");
		q15.addOption(factory.createOption("No", 0));
		q15.addOption(factory.createOption("Yes", 1));
		doc.addEntry(q15);
		q15.setSection(detailsSec);
		q15.setLabel("15");

		OptionEntry q16 = factory.createOptionEntry("16", "Definite psychosocial stressor prior to onset");
		q16.setDescription("A severely or moderately severely threatening event has occurred prior to onset of " +
				"disorder that is unlikely to have resulted from the subjects own behaviour.(ie the event can " +
		"be seen as independent or uncontrollable).");
		q16.addOption(factory.createOption("No", 0));
		q16.addOption(factory.createOption("Yes", 1));
		doc.addEntry(q16);
		q16.setSection(detailsSec);
		q16.setLabel("16");


		Section appearanceSec = factory.createSection("Appearance & Behaviour");
		doc.addSection(appearanceSec);
		appearanceSec.setDisplayText("Appearance & Behaviour");
		SectionOccurrence appearanceSecOcc = factory.createSectionOccurrence(
                "Appearance & Behaviour Section Occurrence");
		appearanceSec.addOccurrence(appearanceSecOcc);

		OptionEntry q17 = factory.createOptionEntry("17", "Bizarre behaviour");
		q17.setDescription("Behaviour that is strange and incomprehensible to others. Includes behaviour which " +
		"could be interpreted as response to auditory hallucinations or thought interference.");
		q17.addOption(factory.createOption("No", 0));
		q17.addOption(factory.createOption("Yes", 1));
		doc.addEntry(q17);
		q17.setSection(appearanceSec);
		q17.setLabel("17");

		OptionEntry q18 = factory.createOptionEntry("18", "Catatonia");
		q18.setDescription("Patient exhibits persistent mannerisms, stereotypies, posturing, catalepsy, stupor, " +
				"command automatism or excitement which is not explicable by affective change. Score '0' if absent, " +
				"score '1' if present for less than one month or if duration is unknown. Score '2' if present for " +
		"at least a significant portion of time during a 1 month period or more.");
		q18.addOption(factory.createOption("No", 0));
		q18.addOption(factory.createOption("Present for less than 1 month or unspecified", 1));
		q18.addOption(factory.createOption("Present for significant %age of a 1 month period", 2));
		doc.addEntry(q18);
		q18.setSection(appearanceSec);
		q18.setLabel("18");

		OptionEntry q19 = factory.createOptionEntry("19", "Excessive activity");
		q19.setDescription("Patient is markedly over-active. This includes motor, social and sexual activity. " +
				"Score '1' for hyper-activity lasting at least 4 days, score '2' for a duration of at least " +
		"one week and '3' for a duration of at least two weeks.");
		q19.addOption(factory.createOption("No", 0));
		q19.addOption(factory.createOption("At least 4 days", 1));
		q19.addOption(factory.createOption("At least 1 week", 2));
		q19.addOption(factory.createOption("At least 2 weeks", 3));
		doc.addEntry(q19);
		q19.setSection(appearanceSec);
		q19.setLabel("19");

		OptionEntry q20 = factory.createOptionEntry("20", "Reckless activity");
		q20.setDescription("Patient is excessively involved in activities with high potential for painful " +
				"consequences which is not recognised, e.g. excessive spending, sexual indiscretions, " +
				"reckless driving, etc. Score '1' for a duration of at least 4 days, score '2' for a " +
		"duration of at least one week and score '3' for a duration of at least two weeks.");
		q20.addOption(factory.createOption("No", 0));
		q20.addOption(factory.createOption("At least 4 days", 1));
		q20.addOption(factory.createOption("At least 1 week", 2));
		q20.addOption(factory.createOption("At least 2 weeks", 3));
		doc.addEntry(q20);
		q20.setSection(appearanceSec);
		q20.setLabel("20");

		OptionEntry q21 = factory.createOptionEntry("21", "Distractibility");
		q21.setDescription("Patient experiences difficulties concentrating on what is going on around " +
				"because attention is too easily drawn to irrelevant or extraneous factors. Score '1' for a " +
				"duration of at least 4 days, score '2' for a duration of at least one week, score '3' for " +
		"a duration of at least two weeks.");
		q21.addOption(factory.createOption("No", 0));
		q21.addOption(factory.createOption("At least 4 days", 1));
		q21.addOption(factory.createOption("At least 1 week", 2));
		q21.addOption(factory.createOption("At least 2 weeks", 3));
		doc.addEntry(q21);
		q21.setSection(appearanceSec);
		q21.setLabel("21");

		OptionEntry q22 = factory.createOptionEntry("22", "Reduced need for sleep");
		q22.setDescription("Patient sleeps less but there is no complaint of insomnia. Extra waking time " +
				"is usually taken up with excessive activities. Score '1' for a duration at least 4 days, " +
				"score '2' for a duration of at least one week and score '3' for a duration of at least " +
		"two weeks.");
		q22.addOption(factory.createOption("No", 0));
		q22.addOption(factory.createOption("At least 4 days", 1));
		q22.addOption(factory.createOption("At least 1 week", 2));
		q22.addOption(factory.createOption("At least 2 weeks", 3));
		doc.addEntry(q22);
		q22.setSection(appearanceSec);
		q22.setLabel("22");

		OptionEntry q23 = factory.createOptionEntry("23", "Agitated activity");
		q23.setDescription("Patient shows excessive repetitive activity, such as fidgety restlessness, " +
				"wringing of hands, pacing up and down, all usually accompanied by expression of mental " +
				"anguish. Score '1' if present for at least one week, '2' if present for two weeks and '3' " +
		"if present for at least one month.");
		q23.addOption(factory.createOption("No", 0));
		q23.addOption(factory.createOption("At least 1 week", 1));
		q23.addOption(factory.createOption("At least 2 weeks", 2));
		q23.addOption(factory.createOption("At least 1 month", 3));
		doc.addEntry(q23);
		q23.setSection(appearanceSec);
		q23.setLabel("23");

		OptionEntry q24 = factory.createOptionEntry("24", "Slowed activity");
		q24.setDescription("Patient complains that he feels slowed up and unable to move. Others may " +
				"report subjective feeling of retardation or retardation may be noted by examining clinician. " +
				"Score '1' if present for at least one week, '2' if present for at least two weeks and '3' if " +
		"present for at least one month.");
		q24.addOption(factory.createOption("No", 0));
		q24.addOption(factory.createOption("At least 1 week", 1));
		q24.addOption(factory.createOption("At least 2 weeks", 2));
		q24.addOption(factory.createOption("At least 1 month", 3));
		doc.addEntry(q24);
		q24.setSection(appearanceSec);
		q24.setLabel("24");

		OptionEntry q25 = factory.createOptionEntry("25", "Loss of energy/tiredness");
		q25.setDescription("Subjective complaint of being excessively tired with no energy. Score '1' for at " +
		"least one week's duration, '2' for two weeks and '3' for one month.");
		q25.addOption(factory.createOption("No", 0));
		q25.addOption(factory.createOption("At least 1 week", 1));
		q25.addOption(factory.createOption("At least 2 weeks", 2));
		q25.addOption(factory.createOption("At least 1 month", 3));
		doc.addEntry(q25);
		q25.setSection(appearanceSec);
		q25.setLabel("25");

		Section speechSec = factory.createSection("Speech & Form of Thought");
		doc.addSection(speechSec);
		speechSec.setDisplayText("Speech & Form of Thought");
		SectionOccurrence speechSecOcc = factory.createSectionOccurrence(
                "Speech & Form of Thought Section Occurrence");
		speechSec.addOccurrence(speechSecOcc);

		OptionEntry q26 = factory.createOptionEntry("26", "Speech difficult to understand");
		q26.setDescription("Speech which makes communication difficult because of lack of logical or " +
		"understandable organisation. Does not include dysarthria or speech impediment.");
		q26.addOption(factory.createOption("No", 0));
		q26.addOption(factory.createOption("Yes", 1));
		doc.addEntry(q26);
		q26.setSection(speechSec);
		q26.setLabel("26");

		OptionEntry q27 = factory.createOptionEntry("27", "Incoherent");
		q27.setDescription("Normal grammatical sentence construction has broken down. Includes 'word salad' " +
				"and should only be rated conservatively for extreme forms of formal thought disorder. Score " +
				"'0' if absent, score '1' if present for less than one month or duration is unspecified " +
		"score, '2' if present for at least a significant proportion of time during a 1 month period.");
		q27.addOption(factory.createOption("No", 0));
		q27.addOption(factory.createOption("Present for less than 1 month or unspecified", 1));
		q27.addOption(factory.createOption("Present for significant %age of a 1 month period", 2));
		doc.addEntry(q27);
		q27.setSection(speechSec);
		q27.setLabel("27");

		OptionEntry q28 = factory.createOptionEntry("28", "Positive formal thought disorder");
		q28.setDescription("The patient has fluent speech but tends to communicate poorly due to neologisms, " +
				"bizarre use of words, derailments, loosening of associations. Score '0' if absent, score '1' " +
				"if present for less than one month or duration is unspecified, score '2' if present for at " +
		"least a significant proportion of time during a 1 month period.");
		q28.addOption(factory.createOption("No", 0));
		q28.addOption(factory.createOption("Present for less than 1 month or unspecified", 1));
		q28.addOption(factory.createOption("Present for significant %age of a 1 month period", 2));
		doc.addEntry(q28);
		q28.setSection(speechSec);
		q28.setLabel("28");

		OptionEntry q29 = factory.createOptionEntry("29", "Negative formal thought disorder");
		q29.setDescription("Includes paucity of thought, frequent thought blocking, poverty of speech or " +
				"poverty of content of speech. Score '0' if absent, score '1' if present for less than one " +
				"month or duration is unspecified, score '2' if present for at least a significant proportion " +
		"of the time during a 1 month period.");
		q29.addOption(factory.createOption("No", 0));
		q29.addOption(factory.createOption("Present for less than 1 month or unspecified", 1));
		q29.addOption(factory.createOption("Present for significant %age of a 1 month period", 2));
		doc.addEntry(q29);
		q29.setSection(speechSec);
		q29.setLabel("29");

		OptionEntry q30 = factory.createOptionEntry("30", "Pressured speech");
		q30.setDescription("Patient much more talkative than usual or feels under pressure to continue " +
				"talking. Include manic type of formal thought disorder with clang associations, punning and " +
				"rhyming etc. Score '0' if absent, score '1' for a duration of at least 4 days, score '2' for " +
		"duration of at least one week and '3' for a duration of at least two weeks.");
		q30.addOption(factory.createOption("No", 0));
		q30.addOption(factory.createOption("At least 4 days", 1));
		q30.addOption(factory.createOption("At least 1 week", 2));
		q30.addOption(factory.createOption("At least 2 weeks", 3));
		doc.addEntry(q30);
		q30.setSection(speechSec);
		q30.setLabel("30");

		OptionEntry q31 = factory.createOptionEntry("31", "Thoughts racing");
		q31.setDescription("Patient experiences thoughts racing through his head or others observe flights of " +
				"ideas and find difficulty in following what patient is saying. or in interrupting because of " +
				"the rapidity and quantity of speech. Score '0' if absent, score '1' for a duration of at " +
				"least 4 days, score '2' for a duration of at least one week and '3' for a duration of at " +
		"least two weeks.");
		q31.addOption(factory.createOption("No", 0));
		q31.addOption(factory.createOption("At least 4 days", 1));
		q31.addOption(factory.createOption("At least 1 week", 2));
		q31.addOption(factory.createOption("At least 2 weeks", 3));
		doc.addEntry(q31);
		q31.setSection(speechSec);
		q31.setLabel("31");


		Section featuresSec = factory.createSection("Affect and Associated Features");
		doc.addSection(featuresSec);
		featuresSec.setDisplayText("Affect and Associated Features");
		SectionOccurrence featuresSecOcc = factory.createSectionOccurrence(
                "Affect and Associated Features Section Occurrence");
		featuresSec.addOccurrence(featuresSecOcc);

		OptionEntry q32 = factory.createOptionEntry("32", "Restricted affect");
		q32.setDescription("Patient's emotional responses are restricted in range and at interview there is " +
				"an impression of bland indifference or 'lack of contact'. Score '0' if absent, score '1' " +
				"if present for less than one month or duration is unspecified score '2' if present for at " +
		"least a significant portion of time in a 1 month period.");
		q32.addOption(factory.createOption("No", 0));
		q32.addOption(factory.createOption("Present for less than 1 month or unspecified", 1));
		q32.addOption(factory.createOption("Present for significant %age of a 1 month period", 2));
		doc.addEntry(q32);
		q32.setSection(featuresSec);
		q32.setLabel("32");

		OptionEntry q33 = factory.createOptionEntry("33", "Blunted affect");
		q33.setDescription("Where the patient's emotional responses are persistently flat and show a complete " +
				"failure to 'resonate' to external change. (NB. Differences between restricted and blunted " +
				"affect should be regarded as one of degree, with 'blunted' only being rated in extreme cases). " +
				"Score '0' if absent, score , score '1' if present for less than one month or duration is " +
		"unspecified, '2' if present for at least a significant portion of time in a 1 month period.");
		q33.addOption(factory.createOption("No", 0));
		q33.addOption(factory.createOption("Present for less than 1 month or unspecified", 1));
		q33.addOption(factory.createOption("Present for significant %age of a 1 month period", 2));
		doc.addEntry(q33);
		q33.setSection(featuresSec);
		q33.setLabel("33");

		OptionEntry q34 = factory.createOptionEntry("34", "Inappropriate affect");
		q34.setDescription("Patient's emotional responses are inappropriate to the circumstance, e.g. " +
				"laughter when discussing painful or sad occurrences, fatuous giggling without apparent reason. " +
				"Score '0' if absent, score '1' if present for less than one month or duration is unspecified, " +
		"score '2', if present for at least a significant portion of time in a 1 month period.");
		q34.addOption(factory.createOption("No", 0));
		q34.addOption(factory.createOption("Present for less than 1 month or unspecified", 1));
		q34.addOption(factory.createOption("Present for significant %age of a 1 month period", 2));
		doc.addEntry(q34);
		q34.setSection(featuresSec);
		q34.setLabel("34");

		OptionEntry q35 = factory.createOptionEntry("35", "Elevated mood");
		q35.setDescription("Patient's predominant mood is one of elation. Score '0' if absent, score '1' for a " +
				"duration of at least 4 days, score '2' for a duration of at least one week and '3' for at " +
				"least two weeks. If elation lasted less than one week but patient was hospitalised for " +
		"affective disorder score '3'.");
		q35.addOption(factory.createOption("No", 0));
		q35.addOption(factory.createOption("At least 4 days", 1));
		q35.addOption(factory.createOption("At least 1 week", 2));
		q35.addOption(factory.createOption("At least 2 weeks", 3));
		doc.addEntry(q35);
		q35.setSection(featuresSec);
		q35.setLabel("35");

		OptionEntry q36 = factory.createOptionEntry("36", "Irritable mood");
		q36.setDescription("Patient's mood is predominantly irritable. Score '0' if absent, score '1' for a " +
				"duration of at least 4 days, score '2' for a duration of at least one week and score '3' for " +
				"a duration of at least two weeks. If irritability lasted less than one week but the patient " +
		"was hospitalised for affective disorder score '3'.");
		q36.addOption(factory.createOption("No", 0));
		q36.addOption(factory.createOption("At least 4 days", 1));
		q36.addOption(factory.createOption("At least 1 week", 2));
		q36.addOption(factory.createOption("At least 2 weeks", 3));
		doc.addEntry(q36);
		q36.setSection(featuresSec);
		q36.setLabel("36");

		OptionEntry q37 = factory.createOptionEntry("37", "Dysphoria");
		q37.setDescription("Persistently low or depressed mood, irritable and sad mood or pervasive loss of " +
				"interest. Score '0' of absent, score '1' if present for at least one week, score '2' if " +
		"present for two weeks and '3' if present for one month.");
		q37.addOption(factory.createOption("No", 0));
		q37.addOption(factory.createOption("At least 1 week", 1));
		q37.addOption(factory.createOption("At least 2 weeks", 2));
		q37.addOption(factory.createOption("At least 1 month", 3));
		doc.addEntry(q37);
		q37.setSection(featuresSec);
		q37.setLabel("37");

		OptionEntry q38 = factory.createOptionEntry("38", "Diurnal variation (mood worse mornings)");
		q38.setDescription("Dysphoria/low mood and/or associated depressive symptoms are at their worst " +
		"soon after awakening with some improvement (even if only slight) as the day goes on.");
		q38.addOption(factory.createOption("No", 0));
		q38.addOption(factory.createOption("Yes", 1));
		doc.addEntry(q38);
		q38.setSection(featuresSec);
		q38.setLabel("38");

		OptionEntry q39 = factory.createOptionEntry("39", "Loss of pleasure");
		q39.setDescription("Pervasive inability to enjoy any activity. Include marked loss of interest or " +
				"loss of libido. Score '0' if absent, score '1' if present for at least one week, score '2' " +
		"for at least two weeks and '3' for at least one month.");
		q39.addOption(factory.createOption("No", 0));
		q39.addOption(factory.createOption("At least 1 week", 1));
		q39.addOption(factory.createOption("At least 2 weeks", 2));
		q39.addOption(factory.createOption("At least 1 month", 3));
		doc.addEntry(q39);
		q39.setSection(featuresSec);
		q39.setLabel("39");

		OptionEntry q40 = factory.createOptionEntry("40", "Altered libido");
		q40.setDescription("Definite and persistent change in sexual drive or interest as compared with " +
				"before onset of disorder. Score '1' for a loss of libido for at least 1 week and score '2' " +
		"for an increase in libido for at least one week.");
		q40.addOption(factory.createOption("No", 0));
		q40.addOption(factory.createOption("Loss of libido for at least 1 week", 1));
		q40.addOption(factory.createOption("Increase in libido for at least 1 week", 2));
		doc.addEntry(q40);
		q40.setSection(featuresSec);
		q40.setLabel("40");

		OptionEntry q41 = factory.createOptionEntry("41", "Poor concentration");
		q41.setDescription("Subjective complaint of being unable to think clearly, make decisions etc. " +
				"Score '0' if absent, score '1' for duration of at least one week, '2' for at least two weeks " +
		"and '3' for at least one month.");
		q41.addOption(factory.createOption("No", 0));
		q41.addOption(factory.createOption("At least 1 week", 1));
		q41.addOption(factory.createOption("At least 2 weeks", 2));
		q41.addOption(factory.createOption("At least 1 month", 3));
		doc.addEntry(q41);
		q41.setSection(featuresSec);
		q41.setLabel("41");

		OptionEntry q42 = factory.createOptionEntry("42", "Excessive self reproach");
		q42.setDescription("Extreme feelings of guilt and unworthiness. May be of delusional intensity ('worse " +
				"person in the whole world'). Score '0' if absent, score '1' for duration of at least one week, " +
		"'2' for at least two weeks and '3' for at least one month.");
		q42.addOption(factory.createOption("No", 0));
		q42.addOption(factory.createOption("At least 1 week", 1));
		q42.addOption(factory.createOption("At least 2 weeks", 2));
		q42.addOption(factory.createOption("At least 1 month", 3));
		doc.addEntry(q42);
		q42.setSection(featuresSec);
		q42.setLabel("42");

		OptionEntry q43 = factory.createOptionEntry("43", "Suicidal ideation");
		q43.setDescription("Preoccupation with thoughts of death (not necessarily own). Thinking of suicide, " +
				"wishing to be dead, attempts to kill self. Score '0' if absent, score '1' for duration of at " +
		"least one week, '2' for at least two weeks duration and '3' for at least one month.");
		q43.addOption(factory.createOption("No", 0));
		q43.addOption(factory.createOption("At least 1 week or suicide attempt", 1));
		q43.addOption(factory.createOption("At least 2 weeks", 2));
		q43.addOption(factory.createOption("At least 1 month", 3));
		doc.addEntry(q43);
		q43.setSection(featuresSec);
		q43.setLabel("43");

		OptionEntry q44 = factory.createOptionEntry("44", "Initial insomnia");
		q44.setDescription("Patient complains that unable to get off to sleep and lies awake for at least " +
				"one hour. Score '0' if absent, score '1' for duration of at least one week, '2' for duration " +
		"of at least two weeks and '3' for duration of at least one month.");
		q44.addOption(factory.createOption("No", 0));
		q44.addOption(factory.createOption("At least 1 week", 1));
		q44.addOption(factory.createOption("At least 2 weeks", 2));
		q44.addOption(factory.createOption("At least 1 month", 3));
		doc.addEntry(q44);
		q44.setSection(featuresSec);
		q44.setLabel("44");

		OptionEntry q45 = factory.createOptionEntry("45", "Middle insomnia (broken sleep)");
		q45.setDescription("Most nights sleep disturbed; subject awakes in the middle of sleep and experiences " +
				"difficulty in getting back to sleep." +
				"" +
		"NB. if you only have information on 'insomnia', score items 44 and 45.");
		q45.addOption(factory.createOption("No", 0));
		q45.addOption(factory.createOption("Yes", 1));
		doc.addEntry(q45);
		q45.setSection(featuresSec);
		q45.setLabel("45");

		OptionEntry q46 = factory.createOptionEntry("46", "Early morning waking");
		q46.setDescription("Patient complains that persistently wakes up at least one hour earlier than usual " +
				"waking time. Score '0' if absent,    score '1' for duration of at least one week, score '2' " +
		"for duration of at least two weeks, and '3' for duration of at least one month.");
		q46.addOption(factory.createOption("No", 0));
		q46.addOption(factory.createOption("At least 1 week", 1));
		q46.addOption(factory.createOption("At least 2 weeks", 2));
		q46.addOption(factory.createOption("At least 1 month", 3));
		doc.addEntry(q46);
		q46.setSection(featuresSec);
		q46.setLabel("46");

		OptionEntry q47 = factory.createOptionEntry("47", "Excessive sleep");
		q47.setDescription("Patient complains that sleeping too much. Score '0' if absent, score '1' if present " +
				"for at least one week, score '2' if present for at least two weeks and '3' if present for at " +
		"least one month.");
		q47.addOption(factory.createOption("No", 0));
		q47.addOption(factory.createOption("At least 1 week", 1));
		q47.addOption(factory.createOption("At least 2 weeks", 2));
		q47.addOption(factory.createOption("At least 1 month", 3));
		doc.addEntry(q47);
		q47.setSection(featuresSec);
		q47.setLabel("47");

		OptionEntry q48 = factory.createOptionEntry("48", "Poor appetite");
		q48.setDescription("Subjective complaint that patient has poor appetite. Not necessarily observed to be " +
				"eating less. Score '0' if absent, score '1' if present for at least one week, score '2' if " +
		"present for at least two weeks and '3' for at least one month.");
		q48.addOption(factory.createOption("No", 0));
		q48.addOption(factory.createOption("At least 1 week", 1));
		q48.addOption(factory.createOption("At least 2 weeks", 2));
		q48.addOption(factory.createOption("At least 1 month", 3));
		doc.addEntry(q48);
		q48.setSection(featuresSec);
		q48.setLabel("48");

		OptionEntry q49 = factory.createOptionEntry("49", "Weight loss");
		q49.setDescription("Score '1' for a loss of 1 lb per week over several weeks. Score '2' for a loss of " +
				"at least 2 lb's a week over several weeks. Score '3' for a loss of at least 10 lb a over one " +
		"year. Do not score those who have reduced weight as a result of deliberate dieting.");
		q49.addOption(factory.createOption("No", 0));
		q49.addOption(factory.createOption("1lb+ / week over several weeks", 1));
		q49.addOption(factory.createOption("2lb+ / week over several weeks", 2));
		q49.addOption(factory.createOption("10lb+ over one year", 3));
		doc.addEntry(q49);
		q49.setSection(featuresSec);
		q49.setLabel("49");

		OptionEntry q50 = factory.createOptionEntry("50", "Increased appetite");
		q50.setDescription("Patient reports increased appetite and/or 'comfort eating'. Score '0' if absent, " +
				"score '1' for duration of at least one week, score '2' for duration of at least two weeks " +
		"and score '3' for at least one month.");
		q50.addOption(factory.createOption("No", 0));
		q50.addOption(factory.createOption("At least 1 week", 1));
		q50.addOption(factory.createOption("At least 2 weeks", 2));
		q50.addOption(factory.createOption("At least 1 month", 3));
		doc.addEntry(q50);
		q50.setSection(featuresSec);
		q50.setLabel("50");

		OptionEntry q51 = factory.createOptionEntry("51", "Weight gain");
		q51.setDescription("Score '1' for a gain of 1 lb a week over several weeks. Score '2' for a gain " +
				"of at least 2 lb's a week over several weeks. Score '3' for a gain of at least 10lb's over " +
		"one year.");
		q51.addOption(factory.createOption("No", 0));
		q51.addOption(factory.createOption("1lb+ / week over several weeks", 1));
		q51.addOption(factory.createOption("2lb+ / week over several weeks", 2));
		q51.addOption(factory.createOption("10lb+ over one year", 3));
		doc.addEntry(q51);
		q51.setSection(featuresSec);
		q51.setLabel("51");

		OptionEntry q52 = factory.createOptionEntry("52", "Relationship between psychotic and affective symptoms");
		q52.addOption(factory.createOption("No co-occurrence.", 0));
		q52.addOption(factory.createOption("Psychotic symptoms dominate the " +
				"clinical picture although occasional affective disturbance " +
				"may also occur.", 1));
		q52.addOption(factory.createOption("Psychotic and affective symptoms " +
				"are balanced but delusions or hallucinations have occurred " +
				"for at least 2 weeks without prominent mood symptoms.", 2));
		q52.addOption(factory.createOption("Affective symptoms predominate " +
				"although psychotic symptoms may also occur.", 3));
		doc.addEntry(q52);
		q52.setSection(featuresSec);
		q52.setLabel("52");

		OptionEntry q53 = factory.createOptionEntry("53", "Increased sociability");
		q53.setDescription("Score '0' if absent, score '1' for over-familiarity lasting at least 4 days, score " +
				"'2' for loss of social inhibition resulting in behaviour which is inappropriate to the " +
				"circumstances and out of character lasting at least one week, score '3' when this " +
		"inappropriate behaviour lasts for at least 2 weeks.");
		q53.addOption(factory.createOption("No", 0));
		q53.addOption(factory.createOption("Over familiarity (duration 4+ days)", 1));
		q53.addOption(factory.createOption("Loss of social inhibition with inappropriate behaviour (>= 1 week)", 2));
		q53.addOption(factory.createOption("Inappropriate behaviour lasts at least 2 weeks", 3));
		doc.addEntry(q53);
		q53.setSection(featuresSec);
		q53.setLabel("53");


		Section beliefsSec = factory.createSection("Abnormal Beliefs and Ideas");
		doc.addSection(beliefsSec);
		beliefsSec.setDisplayText("Abnormal Beliefs and Ideas");
		SectionOccurrence beliefsSecOcc = factory.createSectionOccurrence(
                "Abnormal Beliefs and Ideas Section Occurrence");
		beliefsSec.addOccurrence(beliefsSecOcc);

		NarrativeEntry abinstructions = factory.createNarrativeEntry("Abnormal Beliefs and Ideas Instructions",
                "NB. When scoring delusions please score each separate delusion under one " +
                        "and only one category describing the specific type of the delusion i.e. as either; persecutory, " +
                        "grandiose, influence/reference, bizarre, passivity, primary del perception, other primary del, " +
                        "thought withdrawal, thought broadcast, thought insertion, guilt, poverty or nihilistic.");
		doc.addEntry(abinstructions);
		abinstructions.setSection(beliefsSec);

		OptionEntry q54 = factory.createOptionEntry("54", "Persecutory delusions");
		q54.setDescription("Includes all delusions with persecutory ideation. Score '0' if absent, and '1' for " +
				"less than one months duration or duration is not known score, '2' if present for a significant " +
		"portion of time for at least a one month period.");
		q54.addOption(factory.createOption("No", 0));
		q54.addOption(factory.createOption("Present for less than 1 month or unspecified", 1));
		q54.addOption(factory.createOption("Present for significant %age of a 1 month period", 2));
		doc.addEntry(q54);
		q54.setSection(beliefsSec);
		q54.setLabel("54");

		OptionEntry q55 = factory.createOptionEntry("55", "Well organised delusions");
		q55.setDescription("Illness is characterised by a series of well organised or well systematised delusions. " +
				"Score '0' if absent, score '1' if present for less than one month or duration is unspecified. " +
				"Score '2' if present for at least a significant portion of time in a 1 month period. " +
				"\n " +
		"NB. This item (55) should be scored in addition to scoring the type of delusion/s described.");
		q55.addOption(factory.createOption("No", 0));
		q55.addOption(factory.createOption("Present for less than 1 month or unspecified", 1));
		q55.addOption(factory.createOption("Present for significant %age of a 1 month period", 2));
		doc.addEntry(q55);
		q55.setSection(beliefsSec);
		q55.setLabel("55");

		OptionEntry q56 = factory.createOptionEntry("56", "Increased self esteem");
		q56.setDescription("Patient believes that he is an exceptional person with special powers, plans, " +
				"talents or abilities. Rate positively here if overvalued idea but if delusional in quality also " +
				"score item 57 (grandiose delusions). Score '0' if absent, score '1' for a duration of at least " +
		"4 days, score '1' for duration of at least one week and '2' for a duration of at least two weeks.");
		q56.addOption(factory.createOption("No", 0));
		q56.addOption(factory.createOption("At least 4 days", 1));
		q56.addOption(factory.createOption("At least 1 week", 2));
		q56.addOption(factory.createOption("At least 2 weeks", 3));
		doc.addEntry(q56);
		q56.setSection(beliefsSec);
		q56.setLabel("56");

		OptionEntry q57 = factory.createOptionEntry("57", "Grandiose delusions");
		q57.setDescription("Patient has grossly exaggerated and unshakeable sense of own importance, has " +
				"exceptional powers or abilities or falsely believes that he is rich or famous, aristocratic or " +
				"divine Score '0' if absent, score '1' for at least 4 days, score '2' for at least one week, " +
		"score '3' for at least 2 weeks.");
		q57.addOption(factory.createOption("No", 0));
		q57.addOption(factory.createOption("At least 4 days", 1));
		q57.addOption(factory.createOption("At least 1 week", 2));
		q57.addOption(factory.createOption("At least 2 weeks", 3));
		doc.addEntry(q57);
		q57.setLabel("57");
		q57.setSection(beliefsSec);

		OptionEntry q58 = factory.createOptionEntry("58", "Delusions of influence");
		q58.setDescription("Events, objects or other people in patient's immediate surroundings have a special " +
				"significance, often of a persecutory nature. Include ideas of reference from the TV or radio, " +
				"or newspapers, where patient believes that these are providing instructions or prescribing " +
				"certain behaviour. Score '0' if absent, score '1' if present for less than one month or " +
				"duration is unspecified. Score '2' if present for at least a significant portion of time in a 1 " +
		"month period.");
		q58.addOption(factory.createOption("No", 0));
		q58.addOption(factory.createOption("Present for less than 1 month or unspecified", 1));
		q58.addOption(factory.createOption("Present for significant %age of a 1 month period", 2));
		doc.addEntry(q58);
		q58.setSection(beliefsSec);
		q58.setLabel("58");

		OptionEntry q59 = factory.createOptionEntry("59", "Bizarre delusions");
		q59.setDescription("Strange, absurd or fantastic delusions whose content may have a mystical, magical " +
				"or 'science fiction' quality. Score '0' if absent, score '1' if present less than a month or " +
				"duration is unspecified, score '2' if present for at least a significant portion of time in a " +
		"1 month period.");
		q59.addOption(factory.createOption("No", 0));
		q59.addOption(factory.createOption("Present for less than 1 month or unspecified", 1));
		q59.addOption(factory.createOption("Present for significant %age of a 1 month period", 2));
		doc.addEntry(q59);
		q59.setSection(beliefsSec);
		q59.setLabel("59");

		OptionEntry q60 = factory.createOptionEntry("60", "Widespread delusions");
		q60.setDescription("Delusions which intrude into most aspects of the patient's life and/or preoccupy " +
				"the patient for most of his time. Score '0' if absent, score '1' if present for less than a " +
				"month or duration is unspecified, score '2' if present for at least a significant portion of " +
				"time in a 1 month period. " +
				"\n " +
		"NB. This item (60) should be scored in addition to scoring the type of delusion/s described.");
		q60.addOption(factory.createOption("No", 0));
		q60.addOption(factory.createOption("Present for less than 1 month or unspecified", 1));
		q60.addOption(factory.createOption("Present for significant %age of a 1 month period", 2));
		doc.addEntry(q60);
		q60.setSection(beliefsSec);
		q60.setLabel("60");

		OptionEntry q61 = factory.createOptionEntry("61", "Delusions of passivity");
		q61.setDescription("Include all 'made' sensations, emotions or actions. That is, the patient believes " +
				"that their emotional feelings, their impulses, acts or somatic sensations are controlled or " +
				"imposed by an external agency. Score '0' if absent, score '1' if present for less than one " +
				"month or duration is unspecified, score '2' if present for at least a significant portion of " +
		"time in a 1 month period.");
		q61.addOption(factory.createOption("No", 0));
		q61.addOption(factory.createOption("Present for less than 1 month or unspecified", 1));
		q61.addOption(factory.createOption("Present for significant %age of a 1 month period", 2));
		doc.addEntry(q61);
		q61.setSection(beliefsSec);
		q61.setLabel("61");

		OptionEntry q62 = factory.createOptionEntry("62", "Primary delusional perception");
		q62.setDescription("The patient has an ordinary perception which triggers a firmly held false belief. " +
				"The belief arises out of the perception in a non understandable fashion. Score '0' if absent, " +
				"score '1' if present for less than one month or duration is unspecified score '2' if the " +
		"belief remains for a significant portion of time in a 1 month period.");
		q62.addOption(factory.createOption("No", 0));
		q62.addOption(factory.createOption("Present for less than 1 month or unspecified", 1));
		q62.addOption(factory.createOption("Present for significant %age of a 1 month period", 2));
		doc.addEntry(q62);
		q62.setSection(beliefsSec);
		q62.setLabel("62");

		OptionEntry q63 = factory.createOptionEntry("63", "Other primary delusions");
		q63.setDescription("Includes delusional mood and delusional ideas. In delusional mood the environment " +
				"appears changed in a threatening way but the significance of the change cannot be understood " +
				"by the patient who is usually anxious puzzled and bewildered. Delusional beliefs, typically " +
				"of a self referential or persecutor type may then arise. A delusional idea appears abruptly " +
				"in the patient's mind fully developed and unheralded by any related thoughts. Score '0' if " +
				"absent and score '1' if present for less than one month or duration is not specified, score " +
		"'2' if present for a significant portion of time in a one month period treated.");
		q63.addOption(factory.createOption("No", 0));
		q63.addOption(factory.createOption("Present for less than 1 month or unspecified", 1));
		q63.addOption(factory.createOption("Present for significant %age of a 1 month period", 2));
		doc.addEntry(q63);
		q63.setSection(beliefsSec);
		q63.setLabel("63");

		OptionEntry q64 = factory.createOptionEntry("64", "Delusions & hallucinations last for one week");
		q64.setDescription("Any type of delusion accompanied by hallucinations of any type lasting at " +
				"least one week. Score '0' if absent, score '1' if present for less than one month or " +
				"duration is unspecified, score '2' if present for at least a significant portion of time " +
				"in a 1 month period." +
				" \n " +
		"NB. This item (64) should be scored in addition to scoring the type of delusion/s described.");
		q64.addOption(factory.createOption("No", 0));
		q64.addOption(factory.createOption("Present for less than 1 month or unspecified", 1));
		q64.addOption(factory.createOption("Present for significant %age of a 1 month period", 2));
		doc.addEntry(q64);
		q64.setSection(beliefsSec);
		q64.setLabel("64");

		OptionEntry q65 = factory.createOptionEntry("65", "Persecutory/jealous delusions & hallucinations");
		q65.setDescription("This is self explanatory. But note that abnormal beliefs are of delusional " +
				"intensity and quality and are accompanied by true hallucinations. Score '0' if absent " +
				"score '1' if present for less than one month or duration is unspecified, score '2' if " +
				"present for at least a significant portion of time in a 1 month period." +
				" \n " +
		"NB. This item (65) should be scored in addition to scoring the type of delusion/s described.");
		q65.addOption(factory.createOption("No", 0));
		q65.addOption(factory.createOption("Present for less than 1 month or unspecified", 1));
		q65.addOption(factory.createOption("Present for significant %age of a 1 month period", 2));
		doc.addEntry(q65);
		q65.setSection(beliefsSec);
		q65.setLabel("65");

		OptionEntry q66 = factory.createOptionEntry("66", "Thought insertion");
		q66.setDescription("Patient recognises that thoughts are being put into his head which are not his " +
				"own and which have been inserted by some external agency. Score '0' if absent, score '1' " +
				"if present for less than one month or duration is unspecified, score '2' if present for " +
		"at least a significant portion of time in a 1 month period.");
		q66.addOption(factory.createOption("No", 0));
		q66.addOption(factory.createOption("Present for less than 1 month or unspecified", 1));
		q66.addOption(factory.createOption("Present for significant %age of a 1 month period", 2));
		doc.addEntry(q66);
		q66.setSection(beliefsSec);
		q66.setLabel("66");

		OptionEntry q67 = factory.createOptionEntry("67", "Thought withdrawal");
		q67.setDescription("Patient experiences thoughts ceasing in his head which may be interpreted as " +
				"thoughts being removed (or 'stolen') by some external agency. Score '0' if absent score '1' " +
				"if present for less than one month or duration is unspecified, score '2' if present for at " +
		"least a significant portion of time in a 1 month period.");
		q67.addOption(factory.createOption("No", 0));
		q67.addOption(factory.createOption("Present for less than 1 month or unspecified", 1));
		q67.addOption(factory.createOption("Present for significant %age of a 1 month period", 2));
		doc.addEntry(q67);
		q67.setSection(beliefsSec);
		q67.setLabel("67");

		OptionEntry q68 = factory.createOptionEntry("68", "Thought broadcast");
		q68.setDescription("Patient experiences thoughts diffusing out of his head so that they may be " +
				"shared by others or even heard by others. Score '0' if absent, score '1' if present for " +
				"less than one month or duration is unspecified, score '2' if present for at least a " +
		"significant portion of time in a 1 month period.");
		q68.addOption(factory.createOption("No", 0));
		q68.addOption(factory.createOption("Present for less than 1 month or unspecified", 1));
		q68.addOption(factory.createOption("Present for significant %age of a 1 month period", 2));
		doc.addEntry(q68);
		q68.setSection(beliefsSec);
		q68.setLabel("68");

		OptionEntry q69 = factory.createOptionEntry("69", "Delusions of guilt");
		q69.setDescription("Firm belief held by subject that they have committed some sin, crime or " +
				"have caused harm to others despite absence of any evidence to support this. Score '0' if " +
				"absent, score '1' for duration of at least one week, score '2' for duration of at least " +
		"two weeks, and '3' for duration of at least one month.");
		q69.addOption(factory.createOption("No", 0));
		q69.addOption(factory.createOption("At least 1 week", 1));
		q69.addOption(factory.createOption("At least 2 weeks", 2));
		q69.addOption(factory.createOption("At least 1 month", 3));
		doc.addEntry(q69);
		q69.setSection(beliefsSec);
		q69.setLabel("69");

		OptionEntry q70 = factory.createOptionEntry("70", "Delusions of poverty");
		q70.setDescription("Firm belief held by subject that they have lost all or much of their money " +
				"or property and have become impoverished despite absence of any evidence to support this. " +
				"Score '0' if absent, score '1' for duration of at least one week, score '2' for duration " +
		"of at least two weeks, and '3' for duration of at least one month.");
		q70.addOption(factory.createOption("No", 0));
		q70.addOption(factory.createOption("At least 1 week", 1));
		q70.addOption(factory.createOption("At least 2 weeks", 2));
		q70.addOption(factory.createOption("At least 1 month", 3));
		doc.addEntry(q70);
		q70.setSection(beliefsSec);
		q70.setLabel("70");

		OptionEntry q71 = factory.createOptionEntry("71", "Nihilistic delusions");
		q71.setDescription("Firmly held belief that some part of patient's body has disappeared or is " +
				"rotting away or is affected by some devastating or malignant disorder despite a lack of " +
				"any objective supporting evidence. Score '0' if absent, score '1' for duration of at least " +
				"one week, score '2' for duration of at least two weeks, and '3' for duration of at least " +
		"one month.");
		q71.addOption(factory.createOption("No", 0));
		q71.addOption(factory.createOption("At least 1 week", 1));
		q71.addOption(factory.createOption("At least 2 weeks", 2));
		q71.addOption(factory.createOption("At least 1 month", 3));
		doc.addEntry(q71);
		q71.setSection(beliefsSec);
		q71.setLabel("71");


		Section abSec = factory.createSection("Abnormal Perceptions");
		doc.addSection(abSec);
		abSec.setDisplayText("Abnormal Perceptions");
		SectionOccurrence abSecOcc = factory.createSectionOccurrence(
                "Abnormal Perceptions Section Occurrence");
		abSec.addOccurrence(abSecOcc);

		OptionEntry q72 = factory.createOptionEntry("72", "Thought echo");
		q72.setDescription("Score '1' if patient experiences thoughts repeated or echoed in his or her head " +
				"or by a voice outside the head Score '0' if absent, score '1' if present for less than one " +
				"month or duration is unspecified, score '2' if present for at least a significant portion " +
		"of time in a 1 month period.");
		q72.addOption(factory.createOption("No", 0));
		q72.addOption(factory.createOption("Present for less than 1 month or unspecified", 1));
		q72.addOption(factory.createOption("Present for significant %age of a 1 month period", 2));
		doc.addEntry(q72);
		q72.setSection(abSec);
		q72.setLabel("72");

		OptionEntry q73 = factory.createOptionEntry("73", "Third person auditory hallucinations");
		q73.setDescription("Two or more voices discussing the patient in the third person. Score if either " +
				"'true' or 'pseudo' hallucinations, i.e. differentiation of the source of the voices is " +
				"unimportant. Score '0' if absent score '1' if present for less than one month or duration " +
				"is unspecified, score '2' if present for at least a significant portion of time in a 1 month " +
		"period.");
		q73.addOption(factory.createOption("No", 0));
		q73.addOption(factory.createOption("Present for less than 1 month or unspecified", 1));
		q73.addOption(factory.createOption("Present for significant %age of a 1 month period", 2));
		doc.addEntry(q73);
		q73.setSection(abSec);
		q73.setLabel("73");

		OptionEntry q74 = factory.createOptionEntry("74", "Running commentary voices");
		q74.setDescription("Patient hears voice(s) describing his actions, sensations or emotions as they " +
				"occur. Score '0' if absent, score '1' if present for less than one month or duration is " +
		"unspecifiedscore '2' if present for at least a significant portion of time in a 1 month period.");
		q74.addOption(factory.createOption("No", 0));
		q74.addOption(factory.createOption("Present for less than 1 month or unspecified", 1));
		q74.addOption(factory.createOption("Present for significant %age of a 1 month period", 2));
		doc.addEntry(q74);
		q74.setSection(abSec);
		q74.setLabel("74");

		OptionEntry q75 = factory.createOptionEntry("75", "Abusive/accusatory/persecutory voices");
		q75.setDescription("Voices talking to the patient in an accusatory, abusive or persecutory manner. " +
				"Score '0' if absent, score '1' if present for less than one month or duration is unspecified " +
		"score '2' if present for at least a significant portion of time in a 1 month period.");
		q75.addOption(factory.createOption("No", 0));
		q75.addOption(factory.createOption("Present for less than 1 month or unspecified", 1));
		q75.addOption(factory.createOption("Present for significant %age of a 1 month period", 2));
		doc.addEntry(q75);
		q75.setSection(abSec);
		q75.setLabel("75");

		OptionEntry q76 = factory.createOptionEntry("76", "Other (non affective) auditory hallucinations");
		q76.setDescription("Any other kind of auditory hallucination. Includes pleasant or neutral voices and " +
				"non verbal hallucinations. Score '0' if absent. Score '1' if present for less than one month " +
				"or duration is unspecified, score '2' if present for at least a significant portion of time " +
		"in a 1 month period.");
		q76.addOption(factory.createOption("No", 0));
		q76.addOption(factory.createOption("Present for less than 1 month or unspecified", 1));
		q76.addOption(factory.createOption("Present for significant %age of a 1 month period", 2));
		doc.addEntry(q76);
		q76.setSection(abSec);
		q76.setLabel("76");

		OptionEntry q77 = factory.createOptionEntry("77", "Non-affective hallucination in any modality");
		q77.setDescription("Hallucinations in which the content has no apparent relationship to elation or " +
				"depression. Score '0' if absent score '1' if present throughout the day for several days or " +
				"intermittently for at least one week, or duration is unspecified, score '2' if present for " +
		"at least a significant portion of time in a 1 month period.");
		q77.addOption(factory.createOption("No", 0));
		q77.addOption(factory.createOption("Present for less than 1 month or unspecified", 1));
		q77.addOption(factory.createOption("Present for several days or unspecified", 2));
		doc.addEntry(q77);
		q77.setSection(abSec);
		q77.setLabel("77");


		Section substanceSec = factory.createSection("Substance Abuse or Dependence");
		doc.addSection(substanceSec);
		substanceSec.setDisplayText("Substance Abuse or Dependence");
		SectionOccurrence substanceSecOcc = factory.createSectionOccurrence(
                "Substance Abuse or Dependence Section Occurrence");
		substanceSec.addOccurrence(substanceSecOcc);

		OptionEntry q78 = factory.createOptionEntry("78", "Life time diagnosis of alcohol abuse/dependence");
		q78.setDescription("Continued use despite knowledge of having a persistent or recurrent social," +
				"occupational, psychological or physical problem that is caused or exacerbated by alcohol; " +
				"or recurrent use in situations in which it is physically hazardous; or symptoms definitely " +
				"indicative of dependence. One of the above must have occurred persistently for at least one " +
		"month, or repeatedly over a longer period.");
		q78.addOption(factory.createOption("No", 0));
		q78.addOption(factory.createOption("Yes", 1));
		doc.addEntry(q78);
		q78.setSection(substanceSec);
		q78.setLabel("78");

		OptionEntry q79 = factory.createOptionEntry("79", "Life time diagnosis of cannabis abuse/dependence");
		q79.setDescription("Continued use despite knowledge of having a persistent or recurrent social," +
				"occupational, psychological or physical problem that is caused or exacerbated by cannabis; " +
				"or recurrent use in situations in which it is physically hazardous; or symptoms definitely " +
				"indicative of dependence. One of the above must have occurred persistently for at least one " +
		"month, or repeatedly over a longer period.");
		q79.addOption(factory.createOption("No", 0));
		q79.addOption(factory.createOption("Yes", 1));
		doc.addEntry(q79);
		q79.setSection(substanceSec);
		q79.setLabel("79");

		OptionEntry q80 = factory.createOptionEntry("80", "Life time diagnosis of other abuse/dependence");
		q80.setDescription("Continued use despite knowledge of having a persistent or recurrent social," +
				"occupational, psychological or physical problem that is caused or exacerbated by substance " +
				"use; or recurrent use in situations in which it is physically hazardous; or symptoms " +
				"definitely indicative of dependence. One of the above must have occurred persistently for at " +
		"least one month, or repeatedly over a longer period.");
		q80.addOption(factory.createOption("No", 0));
		q80.addOption(factory.createOption("Yes", 1));
		doc.addEntry(q80);
		q80.setSection(substanceSec);
		q80.setLabel("80");

		OptionEntry q81 = factory.createOptionEntry("81", "Alcohol abuse/dependence with psychopathology");
		q81.setDescription("Abuse or dependence as defined under item 78 accompanied by any of the preceding " +
		"items describing psychopathology.");
		q81.addOption(factory.createOption("No", 0));
		q81.addOption(factory.createOption("Yes", 1));
		doc.addEntry(q81);
		q81.setSection(substanceSec);
		q81.setLabel("81");

		OptionEntry q82 = factory.createOptionEntry("82", "Cannabis abuse/dependence with psychopathology");
		q82.setDescription("Abuse or dependence as defined under item 79 accompanied by any of the preceding " +
		"items describing psychopathology.");
		q82.addOption(factory.createOption("No", 0));
		q82.addOption(factory.createOption("Yes", 1));
		doc.addEntry(q82);
		q82.setSection(substanceSec);
		q82.setLabel("82");

		OptionEntry q83 = factory.createOptionEntry("83", "Other abuse/dependence with psychopathology");
		q83.setDescription("Abuse or dependence as defined under item 80 accompanied by any of the preceding " +
		"items describing psychopathology.");
		q83.addOption(factory.createOption("No", 0));
		q83.addOption(factory.createOption("Yes", 1));
		doc.addEntry(q83);
		q83.setSection(substanceSec);
		q83.setLabel("83");


		Section genSec = factory.createSection("General Appraisal");
		doc.addSection(genSec);
		genSec.setDisplayText("General Appraisal");
		SectionOccurrence genSecOcc = factory.createSectionOccurrence(
                "General Appraisal Section Occurrence");
		genSec.addOccurrence(genSecOcc);

		OptionEntry q84 = factory.createOptionEntry("84", "Information not credible");
		q84.setDescription("Patient gives misleading answers to questions or provides a jumbled, incoherent " +
		"or inconsistent account.");
		q84.addOption(factory.createOption("No", 0));
		q84.addOption(factory.createOption("Yes", 1));
		doc.addEntry(q84);
		q84.setSection(genSec);
		q84.setLabel("84");

		OptionEntry q85 = factory.createOptionEntry("85", "Lack of insight");
		q85.setDescription("Patient is unable to recognise that his experiences are abnormal or that they are " +
				"the product of anomalous mental process, or recognises that his experiences are abnormal but " +
		"gives a delusional explanation.");
		q85.addOption(factory.createOption("No", 0));
		q85.addOption(factory.createOption("Yes", 1));
		doc.addEntry(q85);
		q85.setSection(genSec);
		q85.setLabel("85");

		OptionEntry q86 = factory.createOptionEntry("86", "Rapport difficult");
		q86.setDescription("Interviewer finds difficulty in establishing contact with patient who appears remote " +
				"or cut off. Does not include patients who are difficult to interview because of hostility or " +
		"irritability.");
		q86.addOption(factory.createOption("No", 0));
		q86.addOption(factory.createOption("Yes", 1));
		doc.addEntry(q86);
		q86.setSection(genSec);
		q86.setLabel("86");

		OptionEntry q87 = factory.createOptionEntry("87", "Impairment/incapacity during disorder");
		q87.addOption(factory.createOption("No impairment", 0));
		q87.addOption(factory.createOption("Subjective impairment at work, school, or in " +
				"social functioning", 1));
		q87.addOption(factory.createOption("Impairment in major life role with definite " +
				"reduction in productivity and/or criticism has been received", 2));
		q87.addOption(factory.createOption("No function at all in major life role for " +
				"more than 2 days or in patient treatment has been required or active " +
				"psychotic symptoms such as delusions or hallucinations have occurred", 3));
		doc.addEntry(q87);
		q87.setSection(genSec);
		q87.setLabel("87");

		OptionEntry q88 = factory.createOptionEntry("88", "Deterioration from premorbid level of functioning");
		q88.setDescription("For a significant portion of the time since onset there has been deterioration in work " +
				"interpersonal relations or self-care or, if the onset was in childhood or adolescence there has " +
				"been failure to achieve the expected interpersonal academic occupational level. Score 1 if " +
		"deterioration present, 0 if absent.");
		q88.addOption(factory.createOption("No", 0));
		q88.addOption(factory.createOption("Yes", 1));
		doc.addEntry(q88);
		q88.setSection(genSec);
		q88.setLabel("88");

		OptionEntry q89 = factory.createOptionEntry("89", "Psychotic symptoms respond to neuroleptics");
		q89.setDescription("Rate globally over total period. Score positively if illness appears to respond to " +
		"any type of neuroleptics, (depot or oral) or if relapse occurs when medication is stopped.");
		q89.addOption(factory.createOption("False", 0));
		q89.addOption(factory.createOption("True", 1));
		doc.addEntry(q89);
		q89.setSection(genSec);
		q89.setLabel("89");

		OptionEntry q90 = factory.createOptionEntry("90", "Course of disorder");
		q90.setDescription("Score this item in hierarchical fashion, eg if patient's course in past rated '2',but " +
		"for the time-period now being considered it rates '4', then the correct rating is '4'.");
		q90.addOption(factory.createOption("Single episode with good recovery", 1));
		q90.addOption(factory.createOption("Multiple episodes with good recovery between", 2));
		q90.addOption(factory.createOption("Multiple episodes with partial recovery between", 3));
		q90.addOption(factory.createOption("Continuous chronic illness", 4));
		q90.addOption(factory.createOption("Continuous chronic illness with deterioration", 5));
		doc.addEntry(q90);
		q90.setSection(genSec);
		q90.setLabel("90");


		//Add the external derived entry to fetch the Opcrit calculation
		ExternalDerivedEntry exDE = factory.createExternalDerivedEntry("opcrit", "Opcrit's diagnosis");
		exDE.setExternalTransformer(TransformersWrapper.instance().getTransformer("opcrit"));
		doc.addEntry(exDE);
		exDE.setSection(genSec);
		exDE.setLabel("91");

		int counter = 0;
		for (int i = 0; i < doc.numEntries(); i++) {
			Entry entry = doc.getEntry(i);
			if (entry instanceof OptionEntry || entry instanceof TextEntry) {
				exDE.addVariable(Integer.toString(counter), (BasicEntry)entry);
				counter++;
			}
		}

		//Allow std codes to be present
		exDE.setTransformWithStdCodes(true);

		/*the items that do not count towards any ratings:
		Q1.Source of rating
		Q2. Time frame
		Q12. Alcohol / drug abuse within 1 year of onset of psychotic symptoms
		Q14. Family history of other psychiatric disorder
		Q15. Coarse brain disease prior to onset
		Q16. Definite psychosocial stressor prior to onset
		Q78. Lifetime diagnosis of alcohol abuse/dependence
		Q79. Lifetime diagnosis of cannabis abuse/dependence
		Q80. Lifetime diagnosis of other abuse/dependence
		Q81. Alcohol abuse/dependence with psychopathology
		Q82. Cannabis abuse/dependence with psychopathology
		Q83. Other abuse/dependence with psychopathology
		Q90. Course of disorder
		 */
		List<Integer> ignoreQuestions = new ArrayList<Integer>();
		//Add the questions (-1 as entries start at 0)
		ignoreQuestions.add(0);
		ignoreQuestions.add(1);
		ignoreQuestions.add(11);
		ignoreQuestions.add(13);
		ignoreQuestions.add(14);
		ignoreQuestions.add(15);
		ignoreQuestions.add(77);
		ignoreQuestions.add(78);
		ignoreQuestions.add(79);
		ignoreQuestions.add(80);
		ignoreQuestions.add(81);
		ignoreQuestions.add(82);
		ignoreQuestions.add(89);

		//List of variable names that are required and cannot have std codes entered.
		for (int i = 0; i < 90; i++) {
			if (!ignoreQuestions.contains(i)) {
				exDE.addTransformRequiredVariable(Integer.toString(i));
			}
		}

		return doc;

	}

}

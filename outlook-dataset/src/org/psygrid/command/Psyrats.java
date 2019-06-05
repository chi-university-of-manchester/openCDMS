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
public class Psyrats extends AssessmentForm {

    public static Document createDocument(Factory factory){

    	Document doc = factory.createDocument("Psyrats", "Psyrats");
    	createDocumentStatuses(factory, doc);

        Section mainSec = factory.createSection("Main Section", "Main");
        doc.addSection(mainSec);
        SectionOccurrence mainSecOcc = factory.createSectionOccurrence("Main Section Occ");
        mainSec.addOccurrence(mainSecOcc);

        OptionEntry frequency = factory.createOptionEntry("Frequency",
                "Frequency. How often do you experience voices?  E.g. every day, all day long etc.");
        doc.addEntry(frequency);
        frequency.setSection(mainSec);
        frequency.setLabel("1");
        createOptions(factory, frequency,
        		new String[]{"Voices not present or less than once a week, (specify frequency present)",
        					 "Voices occur for at least once a week",
        					 "Voices occur at least once a day",
        					 "Voices occur at least once an hour",
        					 "Voices occur continuously or almost continually i.e. stop only for a few seconds or minutes."},
        		new int[]{0,1,2,3,4});
        frequency.getOption(0).setTextEntryAllowed(true);

        OptionEntry duration = factory.createOptionEntry("Duration",
                "Duration. When you hear your voices, how long do they last e.g. few seconds, minutes, hours, all day long?");
		doc.addEntry(duration);
		duration.setSection(mainSec);
		duration.setLabel("2");
		createOptions(factory, duration,
				new String[]{"Voices not present",
							 "Voices last for a few seconds, fleeting voices",
							 "Voices last for several minutes",
							 "Voices last for at least one hour",
							 "Voices last for hours at a time."},
				new int[]{0,1,2,3,4});

		OptionEntry location = factory.createOptionEntry("Location",
                "Location. When you hear your voices, where do they sound like they are coming from? " +
                        "Inside your head and/or outside your head? If voices sound like they are outside your head, " +
                        "whereabouts do they sound like they are coming from?");
		doc.addEntry(location);
		location.setSection(mainSec);
		location.setLabel("3");
		createOptions(factory, location,
				new String[]{"No voices present",
							 "Voices originate inside head only",
							 "Voices outside the head, but close to ears or head. Voices inside the head may also be present",
							 "Voices originate inside or close to ears AND outside head away from ears",
							 "Voices originate from outside space, away from head only"},
				new int[]{0,1,2,3,4});

		OptionEntry loudness = factory.createOptionEntry("Loudness",
                "Loudness. How loud are your voices? Are they louder than your voice, about the same " +
                        "loudness, quieter, or just a whisper?");
		doc.addEntry(loudness);
		loudness.setSection(mainSec);
		loudness.setLabel("4");
		createOptions(factory, loudness,
				new String[]{"Voices are not present",
							"Quieter than own voice, whispers",
							"About the same loudness as own voice",
							"Louder than own voice",
							"Extremely loud, shouting"},
				new int[]{0,1,2,3,4});

		OptionEntry beliefs = factory.createOptionEntry("Beliefs Re-origin of Voices",
                "Beliefs Re-origin of Voices. What do you think has caused your voices? " +
                        "Are the voices caused by factors related to yourself or solely due to other people or factors? " +
                        "If patient expresses an external origin: How much do you believe that your voices are caused " +
                        "by _____________ (add patients attribution) on a scale from 0-100 with 100 being that " +
                        "you are totally convinced, have no doubts and 0 being that it is completely untrue?");
		doc.addEntry(beliefs);
		beliefs.setSection(mainSec);
		beliefs.setLabel("5");
		createOptions(factory, beliefs,
				new String[]{"Voices not present",
							 "Believes voices to be solely internally generated and related to self",
							 "Holds less than 50% conviction that voices originate from external causes",
							 "Holds 50% or more conviction (but less than 100%) that voices originate from external cause",
							 "Believes voices are solely due to external causes (100% conviction)"},
				new int[]{0,1,2,3,4});

		OptionEntry amountNegative = factory.createOptionEntry(
                "Amount of Negative Content of Voices",
                "Amount of Negative Content of Voices. Do you voices say unpleasant or negative things? " +
                        "Can you give me some examples of what the voices say? (record these e.g’s). " +
                        "How much of the time do the voices say these type of unpleasant or negative items?");
		doc.addEntry(amountNegative);
		amountNegative.setSection(mainSec);
		amountNegative.setLabel("6");
		createOptions(factory, amountNegative,
				new String[]{"No unpleasant content",
							 "Occasional unpleasant content",
							 "Minority of voice content is unpleasant or negative (less than 50%)",
							 "Majority of voice content is unpleasant or negative (more than 50%)",
							 "All of voice content is unpleasant or negative"},
				new int[]{0,1,2,3,4});

		OptionEntry degreeNegative = factory.createOptionEntry("Degree of Negative Content",
                "Degree of Negative Content (Rate using criteria on scale, asking patient for more detail if necessary)");
		doc.addEntry(degreeNegative);
		degreeNegative.setSection(mainSec);
		degreeNegative.setLabel("7");
		createOptions(factory, degreeNegative,
				new String[]{"Not unpleasant or negative",
							 "Some degree of negative content, but not personal comments relating to self or family e.g. " +
							 	"swear words or comments not directed to self, e.g. 'the milkman is ugly'",
							 "Personal verbal abuse, comments on behaviour e.g. 'shouldn’t do that, or say that'",
							 "Personal verbal abuse relating to self-concept e.g. 'you’re lazy, ugly, mad, perverted'",
							 "Personal threats to self e.g. threats to harm to self or family, extreme instructions or commands to harm self or others and personal verbal abuse as in (3)"},
				new int[]{0,1,2,3,4});

		OptionEntry amountDistress = factory.createOptionEntry("Amount of Distress",
                "Amount of Distress. Are your voices distressing? How much of the time?");
		doc.addEntry(amountDistress);
		amountDistress.setSection(mainSec);
		amountDistress.setLabel("8");
		createOptions(factory, amountDistress,
				new String[]{"Voices not distressing at all",
							 "Voices occasionally distressing, majority not distressing",
							 "Equal amounts of distressing and non-distressing voices",
							 "Majority of voices distressing, minority not distressing",
							 "Voices always distressing"},
				new int[]{0,1,2,3,4});

		OptionEntry intensityDistress = factory.createOptionEntry("Intensity of Distress",
                "Intensity of Distress. When voices are distressing, how distressing are they? " +
                        "Do they cause you minimal, moderate, severe distress? " +
                        "Are they the most distressing they have ever been?");
		doc.addEntry(intensityDistress);
		intensityDistress.setSection(mainSec);
		intensityDistress.setLabel("9");
		createOptions(factory, intensityDistress,
				new String[]{"Voices not distressing at all",
							 "Voices slightly distressing",
							 "Voices are distressing to a moderate degree",
							 "Voices are distressing, although subject could feel worse",
							 "Voices are extremely distressing, feel the worst he/she could possibly feel"},
				new int[]{0,1,2,3,4});

		OptionEntry disruption = factory.createOptionEntry("Disruption to the Life Caused by Voices",
                "Disruption to the Life Caused by Voices. How much disruption do the voices cause to your life? " +
                        "Do the voices stop you from working or other daytime activity? " +
                        "Do they interfere with your relationships with friends and/or family? " +
                        "Do they prevent you from looking after yourself, e.g. bathing, changing clothes etc?");
		doc.addEntry(disruption);
		disruption.setSection(mainSec);
		disruption.setLabel("10");
		createOptions(factory, disruption,
				new String[]{"No disruption to life",
							 "Voices cause minimal amount of disruption to life",
							 "Voices cause moderate amount of disruption to life",
							 "Voices cause severe disruption to life so that hospitalisation is usually necessary.",
							 "Voices cause complete disruption of daily life requiring hospitalisation."},
				new int[]{0,1,2,3,4},
				new String[]{"Able to maintain independent living with no problems in daily living skills. " +
							 "Able to maintain social and family relationships (if present)",
							 "Interferes with concentration although " +
							 "able to maintain daytime activity and social and family relationships and be bale to maintain independent " +
							 "living without support",
							 "Causing some Disturbance to daytime activity " +
							 "and/or family or social activities.  The patient is not in hospital although may live in supported " +
							 "accommodation or receive additional help with daily living skills",
							 "The patient is able to maintain some daily activities, self-care and relationships whilst in hospital. " +
							 "The patient may also be in supported accommodation but experiencing sever disruption of life in terms " +
							 "of activities, daily living skills and or relationships",
							 "The patient is unable to maintain " +
							 "any daily activities and social relationships. Self-care is also severely disrupted"});


		OptionEntry controllability = factory.createOptionEntry("Controllability of Voices",
                "Controllability of Voices. " +
                        "Do you think that you have any control over when your voices happen? " +
                        "Can you dismiss or bring on your voices?");
		doc.addEntry(controllability);
		controllability.setSection(mainSec);
		controllability.setLabel("11");
		createOptions(factory, controllability,
				new String[]{"Subject believes they can have control over their and can always bring on or dismiss them at will",
							 "Subject believes they can have some control over the voices on the majority of occasions",
							 "Subject believes they can have some control over their voices approximately half of the time",
							 "Subject believes they can have some control over their but only occasionally. The majority of time the subject experiences voices which are uncontrollable",
							 "Subject has no control over when the voices occur and cannot dismiss or bring them on at all"},
				new int[]{0,1,2,3,4});


		 //TODO total score
        DerivedEntry total = factory.createDerivedEntry("Total", "Total score");
        doc.addEntry(total);
        total.setSection(mainSec);
        total.setFormula("a+b+c+d+e+f+g+h+i+j+k");
        total.addVariable("a", frequency);
        total.addVariable("b", duration);
        total.addVariable("c", location);
        total.addVariable("d", loudness);
        total.addVariable("e", beliefs);
        total.addVariable("f", amountNegative);
        total.addVariable("g", degreeNegative);
        total.addVariable("h", amountDistress);
        total.addVariable("i", intensityDistress);
        total.addVariable("j", disruption);
        total.addVariable("k", controllability);

        return doc;
    }
}

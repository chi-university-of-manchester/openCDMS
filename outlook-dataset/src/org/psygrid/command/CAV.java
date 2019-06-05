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

import org.psygrid.common.UnitWrapper;
import org.psygrid.common.ValidationRulesWrapper;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.AssessmentForm;

/**
 * @author Rob Harper
 *
 */
public class CAV extends AssessmentForm {

	public static Document createDocument(Factory factory){

		Document doc = factory.createDocument("CAV", "Cognitive Assessment of Voices");
		createDocumentStatuses(factory, doc);

		Section voiceSec = factory.createSection("Voice Section", "Voice");
		doc.addSection(voiceSec);
		SectionOccurrence voiceSecOcc = factory.createSectionOccurrence("Voice Section Occ");
		voiceSec.addOccurrence(voiceSecOcc);

		IntegerEntry qA1 = factory.createIntegerEntry("Number of voices", "How many voices do you hear?");
		doc.addEntry(qA1);
		qA1.setSection(voiceSec);
		qA1.setLabel("1");
		qA1.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive"));

		OptionEntry qA2 = factory.createOptionEntry("Where voice from", "Does the voice come through the ears or from inside your head?");
		doc.addEntry(qA2);
		qA2.setSection(voiceSec);
		qA2.setLabel("2");
		createOptions(factory, qA2, new String[]{"Through ears", "Inside ears", "Both"}, new int[]{0, 1, 2});

		OptionEntry qA3 = factory.createOptionEntry("Man or woman?", "Is the voice a man or a woman, or are you unsure?");
		doc.addEntry(qA3);
		qA3.setSection(voiceSec);
		qA3.setLabel("3");
		createOptions(factory, qA3, new String[]{"Male", "Female", "Both", "Unsure"}, new int[]{0,1,2,3});

		TextEntry qA4 = factory.createTextEntry("One voice dominant?", "Is one of the voices more dominant that the others?");
		doc.addEntry(qA4);
		qA4.setSection(voiceSec);
		qA4.setLabel("4");


		Section contentSec = factory.createSection("Content Section", "Content");
		doc.addSection(contentSec);
		SectionOccurrence contentSecOcc = factory.createSectionOccurrence("Content Section Occ");
		contentSec.addOccurrence(contentSecOcc);

		OptionEntry qB1 = factory.createOptionEntry("To you or about you?", "Does the voice talk to you or about you?");
		doc.addEntry(qB1);
		qB1.setSection(contentSec);
		qB1.setLabel("1");
		createOptions(factory, qB1, new String[]{"To me", "About me", "Both", "Unsure"}, new int[]{0,1,2,3});

		OptionEntry qB2 = factory.createOptionEntry("Used your name?", "Has the voice used your name?");
		doc.addEntry(qB2);
		qB2.setSection(contentSec);
		qB2.setLabel("2");
		createOptions(factory, qB2, new String[]{"No", "Yes", "Unsure"}, new int[]{0,1,2});

		NarrativeEntry qB3 = factory.createNarrativeEntry("Things voice says",
				"Can you tell me what kinds of things the voice says? (record 2 or 3 recent examples). " +
		"Explore if the voice ever says the following (recorded examples)");
		doc.addEntry(qB3);
		qB3.setSection(contentSec);
		qB3.setLabel("3");

		OptionEntry qB3a = factory.createOptionEntry("Commands", "Commands: Does the voice ever tell you to do something?");
		doc.addEntry(qB3a);
		qB3a.setSection(contentSec);
		qB3a.setLabel("3a");
		createOptions(factory, qB3a, new String[]{"No", "Yes", "Unable to say", "Unsure"}, new int[]{0,1,2,3});

		OptionEntry qB3b = factory.createOptionEntry("Advice", "Advice: Does the voice ever give you advice or suggestions?");
		doc.addEntry(qB3b);
		qB3b.setSection(contentSec);
		qB3b.setLabel("3b");
		createOptions(factory, qB3b, new String[]{"No", "Yes"}, new int[]{0,1});

		OptionEntry qB3c = factory.createOptionEntry("Commentary", "Commentary: Does the voice ever comment on what you are doing or thinking?");
		doc.addEntry(qB3c);
		qB3c.setSection(contentSec);
		qB3c.setLabel("3c");
		createOptions(factory, qB3c, new String[]{"No", "Yes"}, new int[]{0,1});

		OptionEntry qB3d = factory.createOptionEntry("Criticism and Abuse", "Criticism and Abuse: Does the voice ever say unpleasant things about you or someone else?");
		doc.addEntry(qB3d);
		qB3d.setSection(contentSec);
		qB3d.setLabel("3d");
		createOptions(factory, qB3d, new String[]{"No", "Yes"}, new int[]{0,1});

		OptionEntry qB3e = factory.createOptionEntry("Hostility", "Hostility: Does the voice ever threaten to harm you or someone else?");
		doc.addEntry(qB3e);
		qB3e.setSection(contentSec);
		qB3e.setLabel("3e");
		createOptions(factory, qB3e, new String[]{"Harm me", "Harm others"}, new int[]{1,2});

		OptionEntry qB4 = factory.createOptionEntry("Identify the target voice", "Then IDENTIFY THE TARGET VOICE: Is there a voice that is more distressing/disturbing");
		Option qB4No = factory.createOption("No", 0);
		Option qB4Yes = factory.createOption("Yes", 1);
		qB4Yes.setTextEntryAllowed(true);
		qB4.addOption(qB4No);
		qB4.addOption(qB4Yes);
		doc.addEntry(qB4);
		qB4.setSection(contentSec);
		qB4.setLabel("4");

		LongTextEntry qB4b = factory.createLongTextEntry("Target voice content", "Target voice content");
		doc.addEntry(qB4b);
		qB4b.setSection(contentSec);

		NarrativeEntry qB5 = factory.createNarrativeEntry("Focus instruction", "Subsequent questions will focus on the target command voice");
		doc.addEntry(qB5);
		qB5.setSection(contentSec);


		Section antecedentsSec = factory.createSection("Antecedents Section", "Antecedents (Cues)");
		doc.addSection(antecedentsSec);
		SectionOccurrence antecedentsSecOcc = factory.createSectionOccurrence("Antecedents Section Occ");
		antecedentsSec.addOccurrence(antecedentsSecOcc);

		TextEntry qC1 = factory.createTextEntry("Times voice more active",
				"We have found that most people's voices are more active at certain times: " +
				"perhaps last thing at night, or when they are shopping or in pubs, or when they are feeling nervous. " +
		"Are there certain times or occasions when your voice is more active?");
		doc.addEntry(qC1);
		qC1.setSection(antecedentsSec);
		qC1.setLabel("1");

		TextEntry qC2 = factory.createTextEntry("Times don't hear voice",
		"Are there times when you don't hear the voice? Perhaps when you have company and are talking to someone?");
		doc.addEntry(qC2);
		qC2.setSection(antecedentsSec);
		qC2.setLabel("2");

		TextEntry qC3 = factory.createTextEntry("What doing when voice",
		"What are you doing when the voice says ____ [command]?");
		doc.addEntry(qC3);
		qC3.setSection(antecedentsSec);
		qC3.setLabel("3");

		OptionEntry qC4 = factory.createOptionEntry("Always do what says", "Do you always do what the voice says in this situation(s)?");
		doc.addEntry(qC4);
		qC4.setSection(antecedentsSec);
		qC4.setLabel("4");
		createOptions(factory, qC4, new String[]{"No", "Yes", "Sometimes"}, new int[]{0,1,2});

		TextEntry qC5 = factory.createTextEntry("Something makes you believe",
		"Is there something about being in this particular situation that makes you believe that you have to do as the voice(s) say(s)?");
		doc.addEntry(qC5);
		qC5.setSection(antecedentsSec);
		qC5.setLabel("5");

		TextEntry qC6 = factory.createTextEntry("Who with",
		"Who are you with/who is around you?");
		doc.addEntry(qC6);
		qC6.setSection(antecedentsSec);
		qC6.setLabel("6");

		TextEntry qC7 = factory.createTextEntry("How feel",
		"How do you feel in this situation(s)?");
		doc.addEntry(qC7);
		qC7.setSection(antecedentsSec);
		qC7.setLabel("7");

		TextEntry qC8 = factory.createTextEntry("Feel like this any other time",
		"Do you feel like this at any other time?");
		doc.addEntry(qC8);
		qC8.setSection(antecedentsSec);
		qC8.setLabel("8");


		Section affectSec = factory.createSection("Affect Section", "Affect");
		doc.addSection(affectSec);
		SectionOccurrence affectSecOcc = factory.createSectionOccurrence("Affect Section Occ");
		affectSec.addOccurrence(affectSecOcc);

		TextEntry qD1 = factory.createTextEntry("How feel",
		"How do you feel when the voice speaks? (scared, tormented, reassured, amused, indifferent, etc)");
		doc.addEntry(qD1);
		qD1.setSection(affectSec);
		qD1.setLabel("1");

		OptionEntry qD2 = factory.createOptionEntry("Times hear and do not feel this way",
                "Are there times when you hear the voice and do not feel this way? (record feelings)");
		doc.addEntry(qD2);
		qD2.setSection(affectSec);
		qD2.setLabel("2");
		createOptions(factory, qD2, new String[]{"No", "Yes", "Sometimes", "Don't know"}, new int[]{0,1,2,3});


		Section behaviourSec = factory.createSection("Behaviour Section", "Behaviour");
		doc.addSection(behaviourSec);
		SectionOccurrence behaviourSecOcc = factory.createSectionOccurrence("Behaviour Section Occ");
		behaviourSec.addOccurrence(behaviourSecOcc);

		NarrativeEntry qE1 = factory.createNarrativeEntry("When the voice talks what do you usually do?",
		"When the voice talks what do you usually do? Do you (use prompts: always, usually, sometimes never)");
		doc.addEntry(qE1);
		qE1.setSection(behaviourSec);
		qE1.setLabel("1");

		OptionEntry qE1a = factory.createOptionEntry("Listen because you feel you have to?", "Listen because you feel you have to?");
		doc.addEntry(qE1a);
		qE1a.setSection(behaviourSec);
		qE1a.setLabel("1a");
		createOptions(factory, qE1a, new String[]{"No", "Yes"}, new int[]{0,1});

		OptionEntry qE1b = factory.createOptionEntry("Listen because you want to?", "Listen because you want to?");
		doc.addEntry(qE1b);
		qE1b.setSection(behaviourSec);
		qE1b.setLabel("1b");
		createOptions(factory, qE1b, new String[]{"No", "Yes"}, new int[]{0,1});

		OptionEntry qE1c = factory.createOptionEntry("Shout and swear at the voice?", "Shout and swear at the voice?");
		doc.addEntry(qE1c);
		qE1c.setSection(behaviourSec);
		qE1c.setLabel("1c");
		createOptions(factory, qE1c, new String[]{"No", "Yes"}, new int[]{0,1});

		OptionEntry qE1d = factory.createOptionEntry("Do what the voice says willingly?", "Do what the voice says willingly?");
		doc.addEntry(qE1d);
		qE1d.setSection(behaviourSec);
		qE1d.setLabel("1d");
		createOptions(factory, qE1d, new String[]{"No", "Yes"}, new int[]{0,1});

		OptionEntry qE1e = factory.createOptionEntry("Talk to the voice?", "Talk to the voice?");
		doc.addEntry(qE1e);
		qE1e.setSection(behaviourSec);
		qE1e.setLabel("1e");
		createOptions(factory, qE1e, new String[]{"No", "Yes"}, new int[]{0,1});

		OptionEntry qE1f = factory.createOptionEntry("Ignore the voice?", "Ignore the voice?");
		doc.addEntry(qE1f);
		qE1f.setSection(behaviourSec);
		qE1f.setLabel("1f");
		createOptions(factory, qE1f, new String[]{"No", "Yes"}, new int[]{0,1});

		OptionEntry qE1g = factory.createOptionEntry("Try and stop talking to it?", "Try and stop talking to it?");
		doc.addEntry(qE1g);
		qE1g.setSection(behaviourSec);
		qE1g.setLabel("1g");
		createOptions(factory, qE1g, new String[]{"No", "Yes"}, new int[]{0,1});

		TextEntry qE2 = factory.createTextEntry("Anything makes the voice go away?",
		"Is there anything you have found to do that makes the voice go away or seem less intense (e.g. talking, reading, drugs).");
		doc.addEntry(qE2);
		qE2.setSection(behaviourSec);
		qE2.setLabel("2");


		Section identitySec = factory.createSection("Identity Section", "Identity");
		doc.addSection(identitySec);
		SectionOccurrence identitySecOcc = factory.createSectionOccurrence("Identity Section Occ");
		identitySec.addOccurrence(identitySecOcc);

		NarrativeEntry qF0 = factory.createNarrativeEntry("Identity narrative", "Beliefs about IDENTITY of the voice:");
		doc.addEntry(qF0);
		qF0.setSection(identitySec);

		TextEntry qF1 = factory.createTextEntry("Whose voice?", "Do you have an idea whose voice you hear?");
		doc.addEntry(qF1);
		qF1.setSection(identitySec);
		qF1.setLabel("1");

		NumericEntry qF2 = factory.createNumericEntry("How sure?", "How sure are you that the voice is (given name)?");
		doc.addEntry(qF2);
		qF2.setSection(identitySec);
		qF2.setLabel("2");
		qF2.addUnit(UnitWrapper.instance().getUnit("%"));
		qF2.addValidationRule(ValidationRulesWrapper.instance().getRule("0 to 100"));

		TextEntry qF3 = factory.createTextEntry("What makes you think the voice is?", "What makes you think the voice is ____?");
		doc.addEntry(qF3);
		qF3.setSection(identitySec);
		qF3.setLabel("3");


		Section meaningSec = factory.createSection("Meaning Section", "Meaning");
		doc.addSection(meaningSec);
		SectionOccurrence meaningSecOcc = factory.createSectionOccurrence("Meaning Section Occ");
		meaningSec.addOccurrence(meaningSecOcc);

		NarrativeEntry qG0 = factory.createNarrativeEntry("Meaning narrative",
				"We say something like 'Most people I have spoken to have found that they really needed to try " +
				"and make sense of hearing voices, some thought the voice might be punishing them or getting at " +
		"them in some way, others that it might be trying to help them'.");
		doc.addEntry(qG0);
		qG0.setSection(meaningSec);

		TextEntry qG1 = factory.createTextEntry("Why this voice", "Have you any idea why it is that you hear this particular voice?");
		doc.addEntry(qG1);
		qG1.setSection(meaningSec);
		qG1.setLabel("1");

		OptionEntry qG2 = factory.createOptionEntry("Voice trying to harm you?",
                "Do you think the voice is trying to harm you in some way (e.g. punishment for bad deed, undeserved persecution)");
		doc.addEntry(qG2);
		qG2.setSection(meaningSec);
		qG2.setLabel("2");
		createOptions(factory, qG2, new String[]{"No", "Yes", "Don't know"}, new int[]{0,1,2});

		NumericEntry qG3 = factory.createNumericEntry("How sure harm", "How sure are you that this is true?");
		doc.addEntry(qG3);
		qG3.setSection(meaningSec);
		qG3.setLabel("3");
		qG3.addUnit(UnitWrapper.instance().getUnit("%"));
		qG3.addValidationRule(ValidationRulesWrapper.instance().getRule("0 to 100"));

		OptionEntry qG4 = factory.createOptionEntry("Voice trying to help you?",
                "Is the voice trying to help you (e.g protecting you, developing special power)");
		doc.addEntry(qG4);
		qG4.setSection(meaningSec);
		qG4.setLabel("4");
		createOptions(factory, qG4, new String[]{"No", "Yes", "Don't know"}, new int[]{0,1,2});

		NumericEntry qG5 = factory.createNumericEntry("How sure help", "How sure are you that this is true?");
		doc.addEntry(qG5);
		qG5.setSection(meaningSec);
		qG5.setLabel("5");
		qG5.addUnit(UnitWrapper.instance().getUnit("%"));
		qG5.addValidationRule(ValidationRulesWrapper.instance().getRule("0 to 100"));

		OptionEntry qG6 = factory.createOptionEntry("Voice said this is its purpose?",
                "Has the voice said that this is its purpose?");
		doc.addEntry(qG6);
		qG6.setSection(meaningSec);
		qG6.setLabel("6");
		createOptions(factory, qG6, new String[]{"No", "Yes", "Don't know"}, new int[]{0,1,2});
		Option qG6No = qG6.getOption(0);

		LongTextEntry qG6a = factory.createLongTextEntry("Purpose explore evidence",
                "If no, explore evidence: say something like 'so you have worked this out for yourself? " +
                        "What makes you think the voice is (give meaning)?'", EntryStatus.DISABLED);
		doc.addEntry(qG6a);
		qG6a.setSection(meaningSec);
		qG6a.setLabel("6a");
		createOptionDependent(factory, qG6No, qG6a);


		Section powerSec = factory.createSection("Power Section", "Power and Control");
		doc.addSection(powerSec);
		SectionOccurrence powerSecOcc = factory.createSectionOccurrence("Power Section Occ");
		powerSec.addOccurrence(powerSecOcc);

		OptionEntry qH1 = factory.createOptionEntry("Voice powerful?",
                "Do you think that the voice might be very powerful?");
		doc.addEntry(qH1);
		qH1.setSection(powerSec);
		qH1.setLabel("1");
		createOptions(factory, qH1, new String[]{"No", "Yes", "Don't know"}, new int[]{0,1,2});

		TextEntry qH2 = factory.createTextEntry("What makes you think powerful", "What makes you think this (e.g. voice makes me do things, reads my mind)?");
		doc.addEntry(qH2);
		qH2.setSection(powerSec);
		qH2.setLabel("2");

		OptionEntry qH3 = factory.createOptionEntry("Can you control the voice?",
                "Can you control the voice?");
		doc.addEntry(qH3);
		qH3.setSection(powerSec);
		qH3.setLabel("3");
		createOptions(factory, qH3, new String[]{"No", "Yes", "Don't know"}, new int[]{0,1,2});

		NumericEntry qH3a = factory.createNumericEntry("How sure control", "How sure are you of this?");
		doc.addEntry(qH3a);
		qH3a.setSection(powerSec);
		qH3a.setLabel("3a");
		qH3a.addUnit(UnitWrapper.instance().getUnit("%"));
		qH3a.addValidationRule(ValidationRulesWrapper.instance().getRule("0 to 100"));

		OptionEntry qH4 = factory.createOptionEntry("Can you call up the voice?",
                "Can you call up the voice?");
		doc.addEntry(qH4);
		qH4.setSection(powerSec);
		qH4.setLabel("4");
		createOptions(factory, qH4, new String[]{"No", "Yes"}, new int[]{0,1});

		OptionEntry qH5 = factory.createOptionEntry("Can you stop it talking?",
                "Can you stop it talking?");
		doc.addEntry(qH5);
		qH5.setSection(powerSec);
		qH5.setLabel("5");
		createOptions(factory, qH5, new String[]{"No", "Yes"}, new int[]{0,1});

		OptionEntry qH6 = factory.createOptionEntry("Can you have a conversation with it?",
                "Can you have a conversation with it (e.g. ask questions and get answers)?");
		doc.addEntry(qH6);
		qH6.setSection(powerSec);
		qH6.setLabel("6");
		createOptions(factory, qH6, new String[]{"No", "Yes"}, new int[]{0,1});

		NumericEntry qH7 = factory.createNumericEntry("How powerful", "How powerful do you think the voice is?");
		doc.addEntry(qH7);
		qH7.setSection(powerSec);
		qH7.setLabel("7");
		qH7.addUnit(UnitWrapper.instance().getUnit("%"));
		qH7.addValidationRule(ValidationRulesWrapper.instance().getRule("0 to 100"));

		NarrativeEntry qH8 = factory.createNarrativeEntry("Power narrative",
		"Other people have said that their voices are powerful because of the following reasons, do any of these apply to you?");
		doc.addEntry(qH8);
		qH8.setSection(powerSec);
		qH8.setLabel("8");

		OptionEntry qH8a = factory.createOptionEntry("Voice makes the person do things?",
                "Voice makes the person do things?");
		doc.addEntry(qH8a);
		qH8a.setSection(powerSec);
		qH8a.setLabel("8a");
		createOptions(factory, qH8a, new String[]{"No", "Yes"}, new int[]{0,1});

		OptionEntry qH8b = factory.createOptionEntry("Voice reads the mind of the person",
                "Voice reads the mind of the person");
		doc.addEntry(qH8b);
		qH8b.setSection(powerSec);
		qH8b.setLabel("8b");
		createOptions(factory, qH8b, new String[]{"No", "Yes"}, new int[]{0,1});

		OptionEntry qH8c = factory.createOptionEntry("Unusual experiences",
                "Unusual experiences, such as seeing a vision of the voice speaking to them");
		doc.addEntry(qH8c);
		qH8c.setSection(powerSec);
		qH8c.setLabel("8c");
		createOptions(factory, qH8c, new String[]{"No", "Yes"}, new int[]{0,1});

		OptionEntry qH8d = factory.createOptionEntry("The voice led them to harm",
                "The voice led them to harm");
		doc.addEntry(qH8d);
		qH8d.setSection(powerSec);
		qH8d.setLabel("8d");
		createOptions(factory, qH8d, new String[]{"No", "Yes"}, new int[]{0,1});

		OptionEntry qH8e = factory.createOptionEntry("The frequency of the voice",
                "The frequency of the voice");
		doc.addEntry(qH8e);
		qH8e.setSection(powerSec);
		qH8e.setLabel("8e");
		createOptions(factory, qH8e, new String[]{"No", "Yes"}, new int[]{0,1});

		OptionEntry qH8f = factory.createOptionEntry("Unable to control when voice speaks/stops",
                "The person is unable to control when the voice speaks and stops");
		doc.addEntry(qH8f);
		qH8f.setSection(powerSec);
		qH8f.setLabel("8f");
		createOptions(factory, qH8f, new String[]{"No", "Yes"}, new int[]{0,1});

		OptionEntry qH8g = factory.createOptionEntry("The voice knows all about them",
                "The voice knows all about them, about their past");
		doc.addEntry(qH8g);
		qH8g.setSection(powerSec);
		qH8g.setLabel("8g");
		createOptions(factory, qH8g, new String[]{"No", "Yes"}, new int[]{0,1});

		OptionEntry qH8h = factory.createOptionEntry("The voice makes predictions",
                "The voice makes predictions about the future");
		doc.addEntry(qH8h);
		qH8h.setSection(powerSec);
		qH8h.setLabel("8h");
		createOptions(factory, qH8h, new String[]{"No", "Yes"}, new int[]{0,1});

		OptionEntry qH8i = factory.createOptionEntry("Comments on things person thinking",
                "The voice comments on things the person is thinking");
		doc.addEntry(qH8i);
		qH8i.setSection(powerSec);
		qH8i.setLabel("8i");
		createOptions(factory, qH8i, new String[]{"No", "Yes"}, new int[]{0,1});

		Section freqSec = factory.createSection("Frequency Section", "Frequency");
		doc.addSection(freqSec);
		SectionOccurrence freqSecOcc = factory.createSectionOccurrence("Frequency Section Occ");
		freqSec.addOccurrence(freqSecOcc);

		OptionEntry qI1 = factory.createOptionEntry("How often",
                "How often does this voice tell you what to do?");
		doc.addEntry(qI1);
		qI1.setSection(freqSec);
		qI1.setLabel("1");
		createOptions(factory, qI1, new String[]{"Daily", "Weekly", "Monthly", "All the time"}, new int[]{1,2,3,4});

		TextEntry qI2 = factory.createTextEntry("How many times do you",
		"How many times do you do what the voice(s) tells you to do?");
		doc.addEntry(qI2);
		qI2.setSection(freqSec);
		qI2.setLabel("2");

		TextEntry qI3 = factory.createTextEntry("How many times do you bit/part",
		"How often do you only do a bit or part of what the voice(s) has asked/told you to do?");
		doc.addEntry(qI3);
		qI3.setSection(freqSec);
		qI3.setLabel("3");

		Section complSec = factory.createSection("Compliance Section", "Compliance");
		doc.addSection(complSec);
		SectionOccurrence complSecOcc = factory.createSectionOccurrence("Compliance Section Occ");
		complSec.addOccurrence(complSecOcc);

		OptionEntry qJ1 = factory.createOptionEntry("Believe you have to do",
                "Do you believe you have to do as the voice(s) say(s)?");
		doc.addEntry(qJ1);
		qJ1.setSection(complSec);
		qJ1.setLabel("1");
		createOptions(factory, qJ1, new String[]{"No", "Yes", "Sometimes"}, new int[]{0,1,2});

		NumericEntry qJ2 = factory.createNumericEntry("How likely do now?", "How likely are you to do as the voice says now?");
		doc.addEntry(qJ2);
		qJ2.setSection(complSec);
		qJ2.setLabel("2");
		qJ2.setDescription("Also ask this question to a close relative or staff member who knows the client well");
		qJ2.addUnit(UnitWrapper.instance().getUnit("%"));
		qJ2.addValidationRule(ValidationRulesWrapper.instance().getRule("0 to 100"));

		NumericEntry qJ2a = factory.createNumericEntry("How likely do now (relative)?", "How likely are you to do as the voice says now? (ask relative/staff member)");
		doc.addEntry(qJ2a);
		qJ2a.setSection(complSec);
		qJ2a.setLabel("2a");
		qJ2a.addUnit(UnitWrapper.instance().getUnit("%"));
		qJ2a.addValidationRule(ValidationRulesWrapper.instance().getRule("0 to 100"));

		NumericEntry qJ3 = factory.createNumericEntry("How likely do future?", "How likely are you to do as the voice says in the future?");
		doc.addEntry(qJ3);
		qJ3.setSection(complSec);
		qJ3.setLabel("3");
		qJ3.setDescription("Also ask this question to a close relative or staff member who knows the client well");
		qJ3.addUnit(UnitWrapper.instance().getUnit("%"));
		qJ3.addValidationRule(ValidationRulesWrapper.instance().getRule("0 to 100"));

		NumericEntry qJ3a = factory.createNumericEntry("How likely do future (relative)?", "How likely are you to do as the voice says in the future? (ask relative/staff member)");
		doc.addEntry(qJ3a);
		qJ3a.setSection(complSec);
		qJ3a.setLabel("3a");
		qJ3a.addUnit(UnitWrapper.instance().getUnit("%"));
		qJ3a.addValidationRule(ValidationRulesWrapper.instance().getRule("0 to 100"));

		NarrativeEntry qJ4 = factory.createNarrativeEntry("Compliance Q4 narrative",
		"Why do you do what the voice(s) tell you OR Do you have a reason for doing what the voice(s) say?");
		doc.addEntry(qJ4);
		qJ4.setSection(complSec);
		qJ4.setLabel("4");

		OptionEntry qJ4a = factory.createOptionEntry("Voices go away",
                "The voices go away or leave me alone");
		doc.addEntry(qJ4a);
		qJ4a.setSection(complSec);
		qJ4a.setLabel("4a");
		createOptions(factory, qJ4a, new String[]{"No", "Yes"}, new int[]{0,1});

		OptionEntry qJ4b = factory.createOptionEntry("Going to do it anyway",
                "I was going to do it anyway");
		doc.addEntry(qJ4b);
		qJ4b.setSection(complSec);
		qJ4b.setLabel("4b");
		createOptions(factory, qJ4b, new String[]{"No", "Yes"}, new int[]{0,1});

		OptionEntry qJ4c = factory.createOptionEntry("Afraid",
                "I'm afraid or scared of the voice");
		doc.addEntry(qJ4c);
		qJ4c.setSection(complSec);
		qJ4c.setLabel("4c");
		createOptions(factory, qJ4c, new String[]{"No", "Yes"}, new int[]{0,1});

		OptionEntry qJ4d = factory.createOptionEntry("Anxious",
                "I'm anxious");
		doc.addEntry(qJ4d);
		qJ4d.setSection(complSec);
		qJ4d.setLabel("4d");
		createOptions(factory, qJ4d, new String[]{"No", "Yes"}, new int[]{0,1});

		OptionEntry qJ4e = factory.createOptionEntry("Feel pain",
                "I feel pain in my body if I don't comply");
		doc.addEntry(qJ4e);
		qJ4e.setSection(complSec);
		qJ4e.setLabel("4e");
		createOptions(factory, qJ4e, new String[]{"No", "Yes"}, new int[]{0,1});

		OptionEntry qJ4f = factory.createOptionEntry("don't then something might happen",
                "If I don't do as they say then something might happen (good or bad)");
		doc.addEntry(qJ4f);
		qJ4f.setSection(complSec);
		qJ4f.setLabel("4f");
		createOptions(factory, qJ4f, new String[]{"No", "Yes"}, new int[]{0,1});

		OptionEntry qJ4g = factory.createOptionEntry("harmed/punished if I don't",
                "I'll be harmed/punished if I don't comply");
		doc.addEntry(qJ4g);
		qJ4g.setSection(complSec);
		qJ4g.setLabel("4g");
		createOptions(factory, qJ4g, new String[]{"No", "Yes"}, new int[]{0,1});

		LongTextEntry qJ4h = factory.createLongTextEntry("Other reasons", "Other reasons");
		doc.addEntry(qJ4h);
		qJ4h.setSection(complSec);
		qJ4h.setLabel("4h");

		NarrativeEntry qJ5 = factory.createNarrativeEntry("Compliance Q5 narrative",
		"How do you feel about doing what the voice tells you? Do you ever feel ____ (use prompts) if you do as the voice tells you?");
		doc.addEntry(qJ5);
		qJ5.setSection(complSec);
		qJ5.setLabel("5");

		OptionEntry qJ5a = factory.createOptionEntry("Not in control",
                "Not in control");
		doc.addEntry(qJ5a);
		qJ5a.setSection(complSec);
		qJ5a.setLabel("5a");
		createOptions(factory, qJ5a, new String[]{"No", "Yes"}, new int[]{0,1});

		OptionEntry qJ5b = factory.createOptionEntry("Worried/scared",
                "Worried/scared");
		doc.addEntry(qJ5b);
		qJ5b.setSection(complSec);
		qJ5b.setLabel("5b");
		createOptions(factory, qJ5b, new String[]{"No", "Yes"}, new int[]{0,1});

		OptionEntry qJ5c = factory.createOptionEntry("Better/happier",
                "Better/happier");
		doc.addEntry(qJ5c);
		qJ5c.setSection(complSec);
		qJ5c.setLabel("5c");
		createOptions(factory, qJ5c, new String[]{"No", "Yes"}, new int[]{0,1});

		OptionEntry qJ5d = factory.createOptionEntry("More in control",
                "More in control");
		doc.addEntry(qJ5d);
		qJ5d.setSection(complSec);
		qJ5d.setLabel("5d");
		createOptions(factory, qJ5d, new String[]{"No", "Yes"}, new int[]{0,1});

		OptionEntry qJ5e = factory.createOptionEntry("Its in my interest to comply because",
                "Its in my interest to comply because ____");
		doc.addEntry(qJ5e);
		qJ5e.setSection(complSec);
		qJ5e.setLabel("5e");
		createOptions(factory, qJ5e, new String[]{"No", "Yes"}, new int[]{0,1});
		Option qJ5eYes = qJ5e.getOption(1);

		LongTextEntry qJ5eBecause = factory.createLongTextEntry("because..", "because..");
		doc.addEntry(qJ5eBecause);
		qJ5eBecause.setSection(complSec);
		qJ5eBecause.setEntryStatus(EntryStatus.DISABLED);
		//dependent on Yes to question above
		createOptionDependent(factory, qJ5eYes, qJ5eBecause);

		LongTextEntry qJ5f = factory.createLongTextEntry("Other feelings", "Other feelings");
		doc.addEntry(qJ5f);
		qJ5f.setSection(complSec);
		qJ5f.setLabel("5f");

		TextEntry qJ6 = factory.createTextEntry("How do you feel",
		"How do you feel and what happens when you do exactly what the voice says/ask you to do?");
		doc.addEntry(qJ6);
		qJ6.setSection(complSec);
		qJ6.setLabel("6");

		OptionEntry qJ7a = factory.createOptionEntry("Wanted to anyway?",
                "Even if the voice hadn't told you to ____ would you have wanted to anyway?");
		doc.addEntry(qJ7a);
		qJ7a.setSection(complSec);
		qJ7a.setLabel("7a");
		createOptions(factory, qJ7a, new String[]{"No", "Yes", "Don't know"}, new int[]{0,1,2});

		TextEntry qJ7b = factory.createTextEntry("Wanted to anyway reasons",
		"Why?");
		doc.addEntry(qJ7b);
		qJ7b.setSection(complSec);
		qJ7b.setLabel("7b");

		OptionEntry qJ8a = factory.createOptionEntry("Always do?",
                "Do you always do what the voice(s) ask/tells you to do no matter what it is?");
		doc.addEntry(qJ8a);
		qJ8a.setSection(complSec);
		qJ8a.setLabel("8a");
		createOptions(factory, qJ8a, new String[]{"No", "Yes"}, new int[]{0,1});

		TextEntry qJ8b = factory.createTextEntry("Always do reasons",
		"Why?");
		doc.addEntry(qJ8b);
		qJ8b.setSection(complSec);
		qJ8b.setLabel("8b");

		TextEntry qJ9 = factory.createTextEntry("What get out of complying",
		"What do you get out of complying? (is it in your personal interest to comply? Relief? Instrumental i.e. a means to an end? Personal?)");
		doc.addEntry(qJ9);
		qJ9.setSection(complSec);
		qJ9.setLabel("9");

		OptionEntry qJ10 = factory.createOptionEntry("Frequency increase/decrease?",
                "Does the frequency of the command increase/decrease if you comply?");
		doc.addEntry(qJ10);
		qJ10.setSection(complSec);
		qJ10.setLabel("10");
		createOptions(factory, qJ10, new String[]{"Increases", "Decreases", "Stays the same"}, new int[]{0,1,2});

		Section resSec = factory.createSection("Resistance Section", "Resistance");
		doc.addSection(resSec);
		SectionOccurrence resSecOcc = factory.createSectionOccurrence("Resistance Section Occ");
		resSec.addOccurrence(resSecOcc);

		NarrativeEntry qK1 = factory.createNarrativeEntry("Resistance Q1 narrative",
		"Why is it that you do not do as the voice(s) ask/tell you to do? (use prompts)");
		doc.addEntry(qK1);
		qK1.setSection(resSec);
		qK1.setLabel("1");

		OptionEntry qK1a = factory.createOptionEntry("Know it's wrong",
                "I know/believe that it is wrong to do what they are asking?");
		doc.addEntry(qK1a);
		qK1a.setSection(resSec);
		qK1a.setLabel("1a");
		createOptions(factory, qK1a, new String[]{"No", "Yes"}, new int[]{0,1});

		OptionEntry qK1b = factory.createOptionEntry("Ignore goes away",
                "If I ignore the voice it goes away");
		doc.addEntry(qK1b);
		qK1b.setSection(resSec);
		qK1b.setLabel("1b");
		createOptions(factory, qK1b, new String[]{"No", "Yes"}, new int[]{0,1});

		OptionEntry qK1c = factory.createOptionEntry("Feel in control",
                "I feel more in control");
		doc.addEntry(qK1c);
		qK1c.setSection(resSec);
		qK1c.setLabel("1c");
		createOptions(factory, qK1c, new String[]{"No", "Yes"}, new int[]{0,1});

		OptionEntry qK1d = factory.createOptionEntry("Feel better",
                "I feel better/happier");
		doc.addEntry(qK1d);
		qK1d.setSection(resSec);
		qK1d.setLabel("1d");
		createOptions(factory, qK1d, new String[]{"No", "Yes"}, new int[]{0,1});

		OptionEntry qK1e = factory.createOptionEntry("Feel worried",
                "I feel worried/scared");
		doc.addEntry(qK1e);
		qK1e.setSection(resSec);
		qK1e.setLabel("1e");
		createOptions(factory, qK1e, new String[]{"No", "Yes"}, new int[]{0,1});

		LongTextEntry qK1f = factory.createLongTextEntry("Other reasons", "Other reasons");
		doc.addEntry(qK1f);
		qK1f.setSection(resSec);
		qK1f.setLabel("1f");

		NarrativeEntry qK2 = factory.createNarrativeEntry("Resistance Q2 narrative",
		"How do you feel when you do not do/resist what the voice(s) tells/asks? (use prompts)");
		doc.addEntry(qK2);
		qK2.setSection(resSec);
		qK2.setLabel("2");

		OptionEntry qK2a = factory.createOptionEntry("Worried something might happen",
                "I feel worried in case something might happen (what?)");
		doc.addEntry(qK2a);
		qK2a.setSection(resSec);
		qK2a.setLabel("2a");
		createOptions(factory, qK2a, new String[]{"No", "Yes"}, new int[]{0,1});

		OptionEntry qK2b = factory.createOptionEntry("Feel happier",
                "I feel happier/more in control for resisting?");
		doc.addEntry(qK2b);
		qK2b.setSection(resSec);
		qK2b.setLabel("2b");
		createOptions(factory, qK2b, new String[]{"No", "Yes"}, new int[]{0,1});

		OptionEntry qK2c = factory.createOptionEntry("Worried might act",
                "I feel worried/scared that I might act on what the voice(s) says");
		doc.addEntry(qK2c);
		qK2c.setSection(resSec);
		qK2c.setLabel("2c");
		createOptions(factory, qK2c, new String[]{"No", "Yes"}, new int[]{0,1});

		LongTextEntry qK2d = factory.createLongTextEntry("Other feelings", "Other feelings");
		doc.addEntry(qK2d);
		qK2d.setSection(resSec);
		qK2d.setLabel("2d");

		OptionEntry qK3 = factory.createOptionEntry("Frequency change if resist",
                "Does the frequency of the command increase/decrease if you resist?");
		doc.addEntry(qK3);
		qK3.setSection(resSec);
		qK3.setLabel("3");
		createOptions(factory, qK3, new String[]{"Increases", "Decreases", "Stays the same"}, new int[]{1,2,3});

		Section sitSec = factory.createSection("Situations Section", "Situations");
		doc.addSection(sitSec);
		SectionOccurrence sitSecOcc = factory.createSectionOccurrence("Situations Section Occ");
		sitSec.addOccurrence(sitSecOcc);

		NarrativeEntry qL1 = factory.createNarrativeEntry("Situations Q1 narrative",
		"Compliance");
		doc.addEntry(qL1);
		qL1.setSection(sitSec);
		qL1.setLabel("1");

		TextEntry qL1a = factory.createTextEntry("Compliance - which situations",
		"In which situations do you do as the voice says/asks? (Where? What is happening? What time of day?");
		doc.addEntry(qL1a);
		qL1a.setSection(sitSec);
		qL1a.setLabel("1a");

		OptionEntry qL1b = factory.createOptionEntry("Compliance - always do",
                "Do you always do what the voice says in this situation(s)?");
		doc.addEntry(qL1b);
		qL1b.setSection(sitSec);
		qL1b.setLabel("1b");
		createOptions(factory, qL1b, new String[]{"No", "Yes", "Sometimes"}, new int[]{0,1,2});

		OptionEntry qL1c = factory.createOptionEntry("Compliance - something believe have to",
                "Is there something about being in this particular situation that makes you believe that you have to do as the voice(s) says?");
		doc.addEntry(qL1c);
		qL1c.setSection(sitSec);
		qL1c.setLabel("1c");
		createOptions(factory, qL1c, new String[]{"No", "Yes", "Don't know"}, new int[]{0,1,2});

		TextEntry qL1d = factory.createTextEntry("Compliance - who with",
		"Who are you with/who is around you?");
		doc.addEntry(qL1d);
		qL1d.setSection(sitSec);
		qL1d.setLabel("1d");

		TextEntry qL1e = factory.createTextEntry("Compliance - how feel",
		"How do you feel in this situation(s)?");
		doc.addEntry(qL1e);
		qL1e.setSection(sitSec);
		qL1e.setLabel("1e");

		OptionEntry qL1f = factory.createOptionEntry("Compliance - feel other time",
                "Do you feel like this at any other time?");
		doc.addEntry(qL1f);
		qL1f.setSection(sitSec);
		qL1f.setLabel("1f");
		createOptions(factory, qL1f, new String[]{"No", "Yes", "Sometimes", "Don't know"}, new int[]{0,1,2,3});

		NarrativeEntry qL2 = factory.createNarrativeEntry("Situations Q2 narrative",
		"Resistance");
		doc.addEntry(qL2);
		qL2.setSection(sitSec);
		qL2.setLabel("2");

		TextEntry qL2a = factory.createTextEntry("Resistance - which situations",
		"In which situations do you do as the voice says/asks? (Where? What is happening? What time of day?");
		doc.addEntry(qL2a);
		qL2a.setSection(sitSec);
		qL2a.setLabel("2a");

		OptionEntry qL2b = factory.createOptionEntry("Resistance - always do",
                "Do you always do what the voice says in this situation(s)?");
		doc.addEntry(qL2b);
		qL2b.setSection(sitSec);
		qL2b.setLabel("2b");
		createOptions(factory, qL2b, new String[]{"No", "Yes", "Sometimes"}, new int[]{0,1,2});

		OptionEntry qL2c = factory.createOptionEntry("Resistance - something believe have to",
                "Is there something about being in this particular situation that makes you believe that you have to do as the voice(s) says?");
		doc.addEntry(qL2c);
		qL2c.setSection(sitSec);
		qL2c.setLabel("2c");
		createOptions(factory, qL2c, new String[]{"No", "Yes", "Don't know"}, new int[]{0,1,2});

		TextEntry qL2d = factory.createTextEntry("Resistance - who with",
		"Who are you with/who is around you?");
		doc.addEntry(qL2d);
		qL2d.setSection(sitSec);
		qL2d.setLabel("2d");

		TextEntry qL2e = factory.createTextEntry("Resistance - how feel",
		"How do you feel in this situation(s)?");
		doc.addEntry(qL2e);
		qL2e.setSection(sitSec);
		qL2e.setLabel("2e");

		OptionEntry qL2f = factory.createOptionEntry("Resistance - feel other time",
                "Do you feel like this at any other time?");
		doc.addEntry(qL2f);
		qL2f.setSection(sitSec);
		qL2f.setLabel("2f");
		createOptions(factory, qL2f, new String[]{"No", "Yes", "Sometimes", "Don't know"}, new int[]{0,1,2,3});

		Section ebccrSec = factory.createSection("E/BCCR Section", "Emotional and Behavioural Consequences of Compliance / Resistance");
		doc.addSection(ebccrSec);
		SectionOccurrence ebccrSecOcc = factory.createSectionOccurrence("E/BCCR Section Occ");
		ebccrSec.addOccurrence(ebccrSecOcc);

		NarrativeEntry qM0 = factory.createNarrativeEntry("E/BCCR narrative",
		"[feeling prompts: worried/distressed/good/useless/guilty/anxious/panic/happy/no different]");
		doc.addEntry(qM0);
		qM0.setSection(ebccrSec);

		NarrativeEntry qM1 = factory.createNarrativeEntry("E/BCCR Q1 narrative",
		"Non-Compliance");
		doc.addEntry(qM1);
		qM1.setSection(ebccrSec);
		qM1.setLabel("1");

		NarrativeEntry qM1_2 = factory.createNarrativeEntry("E/BCCR Q1_2 narrative",
		"When the voice says ____ [command] do you ever?");
		doc.addEntry(qM1_2);
		qM1_2.setSection(ebccrSec);

		OptionEntry qM1a = factory.createOptionEntry("Not do",
                "Not do as the voice says");
		doc.addEntry(qM1a);
		qM1a.setSection(ebccrSec);
		qM1a.setLabel("1a");
		createOptions(factory, qM1a, new String[]{"No", "Yes"}, new int[]{0,1});

		LongTextEntry qM1a_2 = factory.createLongTextEntry("Not do - what",
                "What happens? How do you feel?");
		doc.addEntry(qM1a_2);
		qM1a_2.setSection(ebccrSec);

		OptionEntry qM1b = factory.createOptionEntry("Do the opposite",
                "Do the opposite of what the voice says?");
		doc.addEntry(qM1b);
		qM1b.setSection(ebccrSec);
		qM1b.setLabel("1b");
		createOptions(factory, qM1b, new String[]{"No", "Yes"}, new int[]{0,1});

		LongTextEntry qM1b_2 = factory.createLongTextEntry("Do the opposite - what",
                "What happens? How do you feel?");
		doc.addEntry(qM1b_2);
		qM1b_2.setSection(ebccrSec);

		OptionEntry qM1c = factory.createOptionEntry("Prevented",
                "Are you prevented from doing as the voice says (e.g. someone stops you/delusional belief)");
		doc.addEntry(qM1c);
		qM1c.setSection(ebccrSec);
		qM1c.setLabel("1c");
		createOptions(factory, qM1c, new String[]{"No", "Yes"}, new int[]{0,1});

		LongTextEntry qM1c_2 = factory.createLongTextEntry("Prevented - what",
                "What happens? How do you feel?");
		doc.addEntry(qM1c_2);
		qM1c_2.setSection(ebccrSec);

		NarrativeEntry qM2 = factory.createNarrativeEntry("E/BCCR Q2 narrative",
		"Partial Compliance");
		doc.addEntry(qM2);
		qM2.setSection(ebccrSec);
		qM2.setLabel("2");

		OptionEntry qM2a = factory.createOptionEntry("Partly do",
                "When the voice says ____ [command] do you ever partly do as the voice says?");
		doc.addEntry(qM2a);
		qM2a.setSection(ebccrSec);
		qM2a.setLabel("2a");
		createOptions(factory, qM2a, new String[]{"No", "Yes"}, new int[]{0,1});

		LongTextEntry qM2a_2 = factory.createLongTextEntry("Partly do - what",
                "What happens? How do you feel?");
		doc.addEntry(qM2a_2);
		qM2a_2.setSection(ebccrSec);

		OptionEntry qM2b = factory.createOptionEntry("Do it late",
                "For example, do you think “I'll do it late�? [covert appeasement]");
		doc.addEntry(qM2b);
		qM2b.setSection(ebccrSec);
		qM2b.setLabel("2b");
		createOptions(factory, qM2b, new String[]{"No", "Yes"}, new int[]{0,1});

		LongTextEntry qM2b_2 = factory.createLongTextEntry("Do it late - what",
                "What happens? How do you feel?");
		doc.addEntry(qM2b_2);
		qM2b_2.setSection(ebccrSec);

		OptionEntry qM2c = factory.createOptionEntry("Plan",
                "Plan how to fulfil the command [covert acting]");
		doc.addEntry(qM2c);
		qM2c.setSection(ebccrSec);
		qM2c.setLabel("2c");
		createOptions(factory, qM2c, new String[]{"No", "Yes"}, new int[]{0,1});

		LongTextEntry qM2c_2 = factory.createLongTextEntry("Plan - what",
                "What happens? How do you feel?");
		doc.addEntry(qM2c_2);
		qM2c_2.setSection(ebccrSec);

		OptionEntry qM2d = factory.createOptionEntry("Do something not told",
                "Do something to satisfy the voice which is not what you were told/asked to do? [overt appeasement]");
		doc.addEntry(qM2d);
		qM2d.setSection(ebccrSec);
		qM2d.setLabel("2d");
		createOptions(factory, qM2d, new String[]{"No", "Yes"}, new int[]{0,1});

		LongTextEntry qM2d_2 = factory.createLongTextEntry("Do something not told - what",
                "What happens? How do you feel?");
		doc.addEntry(qM2d_2);
		qM2d_2.setSection(ebccrSec);

		OptionEntry qM2e = factory.createOptionEntry("Do bit",
                "Do a bit or part of what the voice asks/tells you to do? [overt partial acting]");
		doc.addEntry(qM2e);
		qM2e.setSection(ebccrSec);
		qM2e.setLabel("2e");
		createOptions(factory, qM2e, new String[]{"No", "Yes"}, new int[]{0,1});

		LongTextEntry qM2e_2 = factory.createLongTextEntry("Do bit - what",
                "What happens? How do you feel?");
		doc.addEntry(qM2e_2);
		qM2e_2.setSection(ebccrSec);

		OptionEntry qM2f = factory.createOptionEntry("Fantasise",
                "Carry out what the voice says in your imagination only (fantasise) [covert full acting]");
		doc.addEntry(qM2f);
		qM2f.setSection(ebccrSec);
		qM2f.setLabel("2f");
		createOptions(factory, qM2f, new String[]{"No", "Yes"}, new int[]{0,1});

		LongTextEntry qM2f_2 = factory.createLongTextEntry("Fantasise - what",
                "What happens? How do you feel?");
		doc.addEntry(qM2f_2);
		qM2f_2.setSection(ebccrSec);

		NarrativeEntry qM3 = factory.createNarrativeEntry("E/BCCR Q3 narrative",
		"Full Compliance");
		doc.addEntry(qM3);
		qM3.setSection(ebccrSec);
		qM3.setLabel("3");

		OptionEntry qM3a = factory.createOptionEntry("Exactly do",
                "When the voice says ____[command] do you ever do exactly as the voice says/asks you to do? [overt full acting]");
		doc.addEntry(qM3a);
		qM3a.setSection(ebccrSec);
		qM3a.setLabel("3a");
		createOptions(factory, qM3a, new String[]{"No", "Yes"}, new int[]{0,1});

		LongTextEntry qM3a_2 = factory.createLongTextEntry("Partly do - what",
                "What happens? How do you feel?");
		doc.addEntry(qM3a_2);
		qM3a_2.setSection(ebccrSec);


		return doc;
	}

}

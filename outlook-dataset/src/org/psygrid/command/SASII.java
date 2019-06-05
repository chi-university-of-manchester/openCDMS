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
public class SASII extends AssessmentForm {

    public static Document createDocument(Factory factory){

    	Document doc = factory.createDocument("SASII", "Linehan Suicide Attempt - Self-Injury Interview (SASII) -Standard (Short) Version");
    	createDocumentStatuses(factory, doc);

        Section mainSec = factory.createSection("Main Section", "Main");
        doc.addSection(mainSec);
        SectionOccurrence mainSecOcc = factory.createSectionOccurrence("Main Section Occ");
        mainSec.addOccurrence(mainSecOcc);

        OptionEntry qS1 = factory.createOptionEntry("S1",
                "At any time in the last year [your life, since last assessment, etc.] have you " +
                        "deliberately harmed or injured yourself or attempted suicide?");
        doc.addEntry(qS1);
        qS1.setSection(mainSec);
        qS1.setLabel("S1");
        createOptions(factory, qS1, new String[]{"No", "Yes"}, new int[]{0,1});

        IntegerEntry qS2 = factory.createIntegerEntry("S2",
                "How many times have you deliberately harmed or injured yourself or attempted " +
                        "suicide in the last year [your life, since last assessment, etc.]?");
        doc.addEntry(qS2);
        qS2.setSection(mainSec);
        qS2.setLabel("S2");
        qS2.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive Integer"));

        OptionEntry qS3 = factory.createOptionEntry("S3", "INTERVIEWER: HOW RELIABLE IS THIS NUMBER?");
        doc.addEntry(qS3);
        qS3.setSection(mainSec);
        qS3.setLabel("S3");
        createOptions(factory, qS3,
        		new String[]{"Unreliable","Somewhat reliable", "Reliable"},
        		new int[]{0,1,2});

        IntegerEntry qS4 = factory.createIntegerEntry("S4",
                "HOW MANY EPISODES WERE COUNTED AS THRESHOLD 'SUICIDE ATTEMPT/INTENTIONAL " +
                        "SELF-INJURY'? (Answer at end of interview)");
        doc.addEntry(qS4);
        qS4.setSection(mainSec);
        qS4.setLabel("S4");
        qS4.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive Integer"));

        NarrativeEntry dateLine = factory.createNarrativeEntry("Dateline",
        		"Use this horizontal dateline to note suicide attempts or intentional self-injury episodes, in chronological order. " +
        		"Start in the lower right corner, on the first line, and move from right to left. Make a short vertical mark for each " +
        		"suicide attempt/intentional self-harm. Next to the mark, write the date of the episode, the method and if the subject " +
        		"received medical treatment as a result. Circle any events that the subjects describe as suicide attempts. Any further " +
        		"details should be written in the body of the interview.");
        doc.addEntry(dateLine);
        dateLine.setSection(mainSec);

        IntegerEntry q1 = factory.createIntegerEntry("SASII SEQUENCE NUMBER",
                "SASII SEQUENCE NUMBER (Count most recent SASII as '1') (If no SASII, code 0 and stop interview)");
        doc.addEntry(q1);
        q1.setSection(mainSec);
        q1.setLabel("01");
        q1.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive Integer"));

        OptionEntry q2 = factory.createOptionEntry("BASIS FOR SEQUENCE NUMBER", "BASIS FOR SEQUENCE NUMBER");
        doc.addEntry(q2);
        q2.setSection(mainSec);
        q2.setLabel("02");
        createOptions(factory, q2,
        		new String[]{"All episodes", "All medically treated episodes", "Most serious episode", "First episode",
        					 "Most recent episode", "most serious last year", "Other"},
        		new int[]{1,2,3,4,5,6,7});
        q2.getOption(6).setTextEntryAllowed(true);

        OptionEntry q3 = factory.createOptionEntry("Single event or cluster",
                "Think back to the most recent time (time before that) when you harmed yourself. " +
                        "Was this a single event or a series or cluster of events? INTERVIEWER: RATE " +
                        "EPISODE AS A SINGLE EVENT OR CLUSTER OF EVENTS.");
        doc.addEntry(q3);
        q3.setSection(mainSec);
        q3.setLabel("03");
        createOptions(factory, q3,
        		new String[]{"Single event", "Cluster of events"},
        		new int[]{0,1},
        		new String[]{"An act clearly remembered and/or distinguishable from another act by any detail.",
        					 "A repetitive or habitual series of low lethality acts in which all circumstances were "+
        					 "identical, or a series of acts so poorly recalled by subject such that acts cannot be "+
        					 "differentiated from each other in any way other than count."});
        Option q3Cluster = q3.getOption(1);

        LongTextEntry q3a = factory.createLongTextEntry("Cluster basis", "DESCRIBE BASIS FOR LABELING AS A CLUSTER");
        doc.addEntry(q3a);
        q3a.setSection(mainSec);
        createOptionDependent(factory, q3Cluster, q3a);

        OptionEntry q4 = factory.createOptionEntry(
                "Initiation of action",
                "Was the initiation of your action to (method)/(self-injury/suicide attempt/overdose) deliberate, accidental, or somewhere in between?");
        doc.addEntry(q4);
        q4.setSection(mainSec);
        q4.setLabel("04");
        q4.setDescription("INTERVIEWER: IF INITIATION OF ACT ITSELF WAS AN ACCIDENT, I.E. CODE=1, BEHAVIOR IS NOT A SASII.");
        createOptions(factory, q4,
        		new String[]{"Accidental", "Semi-deliberate", "Deliberate"},
        		new int[]{1,2,3});

        IntegerEntry q5 = factory.createIntegerEntry("Number of events in cluster",
                "Exact/estimated number of suicide attempts or self-harm events in this cluster");
		doc.addEntry(q5);
		q5.setSection(mainSec);
		q5.setLabel("05");
		q5.setDescription("IF SINGLE EVENT, ENTER 1");
		q5.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive Integer"));

		DateEntry q5a = factory.createDateEntry("First date of cluster", "First date of cluster");
		doc.addEntry(q5a);
		q5a.setSection(mainSec);
		q5a.setLabel("5a");
		q5a.setDescription("IF SINGLE EVENT ENTER DATE OF EVENT");

		DateEntry q5b = factory.createDateEntry("Last date of cluster", "Last date of cluster");
		doc.addEntry(q5b);
		q5b.setSection(mainSec);
		q5b.setLabel("5b");
		q5b.setDescription("IF SINGLE EVENT ENTER DATE OF EVENT");

        OptionEntry q6 = factory.createOptionEntry(
                "How accurate is this date",
                "How accurate is this date");
        doc.addEntry(q6);
        q6.setSection(mainSec);
        q6.setLabel("06");
        createOptions(factory, q4,
        		new String[]{"Exact", "Within two weeks", "Within one month", "Anytime in last year"},
        		new int[]{1,2,3,4});

        Section methodSec = factory.createSection("Method Section", "Method and Lethality of Method");
        doc.addSection(methodSec);
        SectionOccurrence methodSecOcc = factory.createSectionOccurrence("Method Section Occ");
        methodSec.addOccurrence(methodSecOcc);

        LongTextEntry q7 = factory.createLongTextEntry("Method used to injure",
                "Before we try to understand what led up to and followed your self-injury/attempted suicide/overdose, " +
                        "I want to first understand exactly what you did. Tell me again/describe exactly what method(s) you " +
                        "used to injure yourself?");
        doc.addEntry(q7);
        q7.setSection(methodSec);

        NarrativeEntry q7_0 = factory.createNarrativeEntry("Q7 Narrative",
        		"INTERVIEWER: CODE PRIMARY METHOD FOR #07.");
        doc.addEntry(q7_0);
        q7_0.setSection(methodSec);

        OptionEntry q7_1 = factory.createOptionEntry(
                "Alcohol",
                "Alcohol (used with direct intent to self-harm)");
        doc.addEntry(q7_1);
        q7_1.setSection(methodSec);
        q7_1.setLabel("7.1");
        createOptions(factory, q7_1,
        		new String[]{"Not used", "Used"},
        		new int[]{0,1});
        Option q7_1Used = q7_1.getOption(1);

        OptionEntry q7_1a = factory.createOptionEntry("What were you drinking?", "What were you drinking?");
        doc.addEntry(q7_1a);
        q7_1a.setSection(methodSec);
        q7_1a.setLabel("7.1a");
        createOptions(factory, q7_1a,
        		new String[]{"BEER", "WINE", "LIQUOR", "COMBINATION OF 1 & 2", "COMBINATION OF 1 & 3", "COMBINATION OF 2 & 3", "COMBINATION OF 1, 2, & 3", "OTHER"},
        		new int[]{1,2,3,4,5,6,7,8});
        q7_1a.getOption(7).setTextEntryAllowed(true);
        createOptionDependent(factory, q7_1Used, q7_1a);

        NumericEntry q7_1b = factory.createNumericEntry("How much did you drink?", "How much did you drink?");
        doc.addEntry(q7_1b);
        q7_1b.setSection(methodSec);
        q7_1b.setLabel("7.1b");
        q7_1b.addUnit(UnitWrapper.instance().getUnit("units"));
        q7_1b.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive"));
        createOptionDependent(factory, q7_1Used, q7_1b);

        OptionEntry q7_2 = factory.createOptionEntry(
                "Drugs/Medications",
                "Drugs/Medications (used with direct intent to self-harm)");
        doc.addEntry(q7_2);
        q7_2.setSection(methodSec);
        q7_2.setLabel("7.2");
        createOptions(factory, q7_2,
        		new String[]{"Not used", "Used"},
        		new int[]{0,1});
        Option q7_2Used = q7_2.getOption(1);

        IntegerEntry q7_2a = factory.createIntegerEntry("Number different drugs", "How many different drugs or medications did you take?");
        doc.addEntry(q7_2a);
        q7_2a.setSection(methodSec);
        q7_2a.setLabel("7.2a");
        q7_2a.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive Integer"));
		createOptionDependent(factory, q7_2Used, q7_2a);

		TextEntry q7_2b = factory.createTextEntry("What drug #1", "What drugs or medication did you take?");
        doc.addEntry(q7_2b);
        q7_2b.setSection(methodSec);
        q7_2b.setLabel("7.2b");
		createOptionDependent(factory, q7_2Used, q7_2b);

		TextEntry q7_2c = factory.createTextEntry("DRUG CODE #1", "DRUG CODE");
        doc.addEntry(q7_2c);
        q7_2c.setSection(methodSec);
        q7_2c.setLabel("7.2c");
		createOptionDependent(factory, q7_2Used, q7_2c);

		NumericEntry q7_2d = factory.createNumericEntry("How much #1", "How much did you take?");
        doc.addEntry(q7_2d);
        q7_2d.setSection(methodSec);
        q7_2d.setLabel("7.2d");
        q7_2d.addUnit(UnitWrapper.instance().getUnit("tablets"));
        q7_2d.addUnit(UnitWrapper.instance().getUnit("mg"));
		createOptionDependent(factory, q7_2Used, q7_2d);

		TextEntry q7_2g = factory.createTextEntry("What drug #2", "What other drugs or medication did you take?");
        doc.addEntry(q7_2g);
        q7_2g.setSection(methodSec);
        q7_2g.setLabel("7.2g");
		createOptionDependent(factory, q7_2Used, q7_2g);

		TextEntry q7_2h = factory.createTextEntry("DRUG CODE #2", "DRUG CODE");
        doc.addEntry(q7_2h);
        q7_2h.setSection(methodSec);
        q7_2h.setLabel("7.2h");
		createOptionDependent(factory, q7_2Used, q7_2h);

		NumericEntry q7_2i = factory.createNumericEntry("How much #2", "How much did you take?");
        doc.addEntry(q7_2i);
        q7_2i.setSection(methodSec);
        q7_2i.setLabel("7.2i");
        q7_2i.addUnit(UnitWrapper.instance().getUnit("tablets"));
        q7_2i.addUnit(UnitWrapper.instance().getUnit("mg"));
		createOptionDependent(factory, q7_2Used, q7_2i);

		TextEntry q7_2l = factory.createTextEntry("What drug #1", "What other drugs or medication did you take?");
        doc.addEntry(q7_2l);
        q7_2l.setSection(methodSec);
        q7_2l.setLabel("7.2l");
		createOptionDependent(factory, q7_2Used, q7_2l);

		TextEntry q7_2m = factory.createTextEntry("DRUG CODE #1", "DRUG CODE");
        doc.addEntry(q7_2m);
        q7_2m.setSection(methodSec);
        q7_2m.setLabel("7.2m");
		createOptionDependent(factory, q7_2Used, q7_2m);

		NumericEntry q7_2n = factory.createNumericEntry("How much #3", "How much did you take?");
        doc.addEntry(q7_2n);
        q7_2n.setSection(methodSec);
        q7_2n.setLabel("7.2n");
        q7_2n.addUnit(UnitWrapper.instance().getUnit("tablets"));
        q7_2n.addUnit(UnitWrapper.instance().getUnit("mg"));
		createOptionDependent(factory, q7_2Used, q7_2n);

		LongTextEntry q7_2q = factory.createLongTextEntry("Other drugs", "List any other drugs that you took");
        doc.addEntry(q7_2q);
        q7_2q.setSection(methodSec);
        q7_2q.setLabel("7.2q");
		createOptionDependent(factory, q7_2Used, q7_2q);

        OptionEntry q7_3 = factory.createOptionEntry(
                "Poison/caustic substance",
                "Poison/caustic substance");
        doc.addEntry(q7_3);
        q7_3.setSection(methodSec);
        q7_3.setLabel("7.3");
        createOptions(factory, q7_3,
        		new String[]{"Not used", "Used"},
        		new int[]{0,1});
        Option q7_3Used = q7_3.getOption(1);

        OptionEntry q7_3a = factory.createOptionEntry(
                "Poison - What substance",
                "What substance did you take?");
        doc.addEntry(q7_3a);
        q7_3a.setSection(methodSec);
        q7_3a.setLabel("7.3a");
        createOptions(factory, q7_3a,
        		new String[]{"LYSOL", "RAT POISON", "AMMONIA", "POLISH REMOVER", "OTHER"},
        		new int[]{1,2,3,4,5});
        q7_3a.getOption(4).setTextEntryAllowed(true);
        createOptionDependent(factory, q7_3Used, q7_3a);

        //TODO should this be numeric?
		TextEntry q7_3b = factory.createTextEntry("Poison - how much", "How much did you take?");
        doc.addEntry(q7_3b);
        q7_3b.setSection(methodSec);
        q7_3b.setLabel("7.3b");
		createOptionDependent(factory, q7_3Used, q7_3b);

        OptionEntry q7_4 = factory.createOptionEntry(
                "Burning",
                "Burning");
        doc.addEntry(q7_4);
        q7_4.setSection(methodSec);
        q7_4.setLabel("7.4");
        createOptions(factory, q7_4,
        		new String[]{"Not used", "Used"},
        		new int[]{0,1});
        Option q7_4Used = q7_4.getOption(1);

        OptionEntry q7_4a = factory.createOptionEntry(
                "Burning - What used",
                "What did you use?");
        doc.addEntry(q7_4a);
        q7_4a.setSection(methodSec);
        q7_4a.setLabel("7.4a");
        createOptions(factory, q7_4a,
        		new String[]{"CIGARETTE", "LIGHTER/MATCH", "OVEN/STOVE","CURLING IRON/FLAT IRON",
        					 "CLOTHES IRON", "HOT METAL", "HEATED KNIFE", "CANDLE", "CHARCOAL", "GREASE",
        					 "BOILING WATER", "LIGHT BULB", "INCENSE STICK", "OTHER"},
        		new int[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14});
        q7_4a.getOption(13).setTextEntryAllowed(true);
        createOptionDependent(factory, q7_4Used, q7_4a);

        OptionEntry q7_4b = factory.createOptionEntry(
                "Burning - Where",
                "Where did you burn yourself?");
        doc.addEntry(q7_4b);
        q7_4b.setSection(methodSec);
        q7_4b.setLabel("7.4b");
        createOptions(factory, q7_4b,
        		new String[]{"WRISTS/ARMS", "TORSO", "LEGS", "OTHER/MIXED", "RECTUM", "VAGINA"},
        		new int[]{1,2,3,4,5,6});
        q7_4b.getOption(3).setTextEntryAllowed(true);
        createOptionDependent(factory, q7_4Used, q7_4b);

        OptionEntry q7_4c = factory.createOptionEntry(
                "Burning - VERIFICATION BY SCARS?",
                "VERIFICATION BY SCARS?");
        doc.addEntry(q7_4c);
        q7_4c.setSection(methodSec);
        q7_4c.setLabel("7.4c");
        createOptions(factory, q7_4c,
        		new String[]{"No", "Yes"},
        		new int[]{0,1});
        createOptionDependent(factory, q7_4Used, q7_4c);

        OptionEntry q7_5 = factory.createOptionEntry(
                "Scratch/cut",
                "Scratch/cut");
        doc.addEntry(q7_5);
        q7_5.setSection(methodSec);
        q7_5.setLabel("7.5");
        createOptions(factory, q7_5,
        		new String[]{"Not used", "Used"},
        		new int[]{0,1});
        Option q7_5Used = q7_5.getOption(1);

        OptionEntry q7_5a = factory.createOptionEntry(
                "Scratch/cut - What used",
                "What did you use?");
        doc.addEntry(q7_5a);
        q7_5a.setSection(methodSec);
        q7_5a.setLabel("7.5a");
        createOptions(factory, q7_5a,
        		new String[]{"RAZOR", "KITCHEN KNIFE", "EXACTO KNIFE/BOX CUTTER/CARPET KNIFE/UTILITY KNIFE",
        					 "POCKET KNIFE/SWISS ARMY KNIFE", "SCISSORS/WIRE CUTTER", "FINGERNAILS", "GLASS/LIGHT BULB/POTTERY",
        					 "CAN LID/POP CAN", "EATING UTENSILS", "TWEEZERS", "PLASTIC", "NAILS", "SAFETY PIN/PUSH PIN/TACK", "OTHER"},
        		new int[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14});
        q7_5a.getOption(13).setTextEntryAllowed(true);
        createOptionDependent(factory, q7_5Used, q7_5a);

        OptionEntry q7_5b = factory.createOptionEntry(
                "Scratch/cut - Where",
                "Where did you scratch/cut?");
        doc.addEntry(q7_5b);
        q7_5b.setSection(methodSec);
        q7_5b.setLabel("7.5b");
        createOptions(factory, q7_5b,
        		new String[]{"WRISTS/ARMS", "THROAT", "TORSO", "LEGS", "OTHER/MIXED"},
        		new int[]{1,2,3,4,5});
        q7_5b.getOption(4).setTextEntryAllowed(true);
        createOptionDependent(factory, q7_5Used, q7_5b);

        IntegerEntry q7_5c = factory.createIntegerEntry(
                "Scratch/cut - no. stitches",
                "How many stitches did you have?");
        doc.addEntry(q7_5c);
        q7_5c.setSection(methodSec);
        q7_5c.setLabel("7.5c");
        q7_5c.setDescription("If none, code 0");
        q7_5c.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive Integer"));
		createOptionDependent(factory, q7_5Used, q7_5c);

        OptionEntry q7_5d = factory.createOptionEntry(
                "Scratch/cut - SEVERITY",
                "SEVERITY?");
        doc.addEntry(q7_5d);
        q7_5d.setSection(methodSec);
        q7_5d.setLabel("7.5d");
        createOptions(factory, q7_5d,
        		new String[]{"SCRATCH", "CUTS, NO TENDON, ARTERY, NERVE DAMAGE", "TENDON, ARTERY, NERVE DAMAGE"},
        		new int[]{1,2,3});
        createOptionDependent(factory, q7_5Used, q7_5d);

        OptionEntry q7_5e = factory.createOptionEntry(
                "Scratch/cut - VERIFICATION BY SCARS?",
                "VERIFICATION BY SCARS?");
        doc.addEntry(q7_5e);
        q7_5e.setSection(methodSec);
        q7_5e.setLabel("7.5e");
        createOptions(factory, q7_5e,
        		new String[]{"No", "Yes"},
        		new int[]{0,1});
        createOptionDependent(factory, q7_5Used, q7_5e);

        OptionEntry q7_6 = factory.createOptionEntry(
                "Stabbing, puncture",
                "Stabbing, puncture");
        doc.addEntry(q7_6);
        q7_6.setSection(methodSec);
        q7_6.setLabel("7.6");
        createOptions(factory, q7_6,
        		new String[]{"Not used", "Used"},
        		new int[]{0,1});
        Option q7_6Used = q7_6.getOption(1);

        OptionEntry q7_6a = factory.createOptionEntry(
                "Stabbing, puncture - What used",
                "What did you use?");
        doc.addEntry(q7_6a);
        q7_6a.setSection(methodSec);
        q7_6a.setLabel("7.6a");
        createOptions(factory, q7_6a,
        		new String[]{"NEEDLE", "KITCHEN KNIFE", "POCKET KNIFE", "UTILITY KNIFE", "PEN/PENCIL", "NAILS",
        					 "SCISSORS", "GLASS", "KEYS", "PINS", "OTHER"},
        		new int[]{1,2,3,4,5,6,7,8,9,10,11});
        q7_6a.getOption(10).setTextEntryAllowed(true);
        createOptionDependent(factory, q7_6Used, q7_6a);

        OptionEntry q7_6b = factory.createOptionEntry(
                "Stabbing, puncture - Where",
                "Where did you stab/puncture?");
        doc.addEntry(q7_6b);
        q7_6b.setSection(methodSec);
        q7_6b.setLabel("7.6b");
        createOptions(factory, q7_6b,
        		new String[]{"WRISTS/ARMS", "TORSO", "LEGS", "OTHER/MIXED"},
        		new int[]{1,2,3,4});
        q7_6b.getOption(3).setTextEntryAllowed(true);
        createOptionDependent(factory, q7_6Used, q7_6b);

        IntegerEntry q7_6c = factory.createIntegerEntry(
                "Stabbing, puncture - no. stitches",
                "How many stitches did you have?");
        doc.addEntry(q7_6c);
        q7_6c.setSection(methodSec);
        q7_6c.setLabel("7.6c");
        q7_6c.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive Integer"));
		createOptionDependent(factory, q7_6Used, q7_6c);

        OptionEntry q7_6d = factory.createOptionEntry(
                "Stabbing, puncture - VERIFICATION BY SCARS?",
                "VERIFICATION BY SCARS?");
        doc.addEntry(q7_6d);
        q7_6d.setSection(methodSec);
        q7_6d.setLabel("7.6d");
        createOptions(factory, q7_6d,
        		new String[]{"No", "Yes"},
        		new int[]{0,1});
        createOptionDependent(factory, q7_6Used, q7_6d);

        OptionEntry q7_7 = factory.createOptionEntry(
                "Gun",
                "Gun");
        doc.addEntry(q7_7);
        q7_7.setSection(methodSec);
        q7_7.setLabel("7.7");
        createOptions(factory, q7_7,
        		new String[]{"Not used", "Used"},
        		new int[]{0,1});
        Option q7_7Used = q7_7.getOption(1);

        OptionEntry q7_7a = factory.createOptionEntry(
                "Gun - What kind",
                "What kind of gun did you use?");
        doc.addEntry(q7_7a);
        q7_7a.setSection(methodSec);
        q7_7a.setLabel("7.7a");
        createOptions(factory, q7_7a,
        		new String[]{"BB GUN", "HAND GUN", "RIFFLE", "AUTOMATIC", "DART GUN", "OTHER"},
        		new int[]{1,2,3,4,5,6});
        createOptionDependent(factory, q7_7Used, q7_7a);

        OptionEntry q7_7b = factory.createOptionEntry(
                "Gun - Where",
                "Where did you shoot?");
        doc.addEntry(q7_7b);
        q7_7b.setSection(methodSec);
        q7_7b.setLabel("7.7b");
        createOptions(factory, q7_7b,
        		new String[]{"HEAD", "CHEST", "LOWER TORSO", "LIMBS", "OTHER/MIXED"},
        		new int[]{1,2,3,4,5});
        q7_7b.getOption(4).setTextEntryAllowed(true);
        createOptionDependent(factory, q7_7Used, q7_7b);

        OptionEntry q7_7c = factory.createOptionEntry(
                "Gun - VERIFICATION BY SCARS?",
                "VERIFICATION BY SCARS?");
        doc.addEntry(q7_7c);
        q7_7c.setSection(methodSec);
        q7_7c.setLabel("7.7c");
        createOptions(factory, q7_7c,
        		new String[]{"No", "Yes"},
        		new int[]{0,1});
        createOptionDependent(factory, q7_7Used, q7_7c);

        OptionEntry q7_8 = factory.createOptionEntry(
                "Hanging",
                "Hanging");
        doc.addEntry(q7_8);
        q7_8.setSection(methodSec);
        q7_8.setLabel("7.8");
        createOptions(factory, q7_8,
        		new String[]{"Not used", "Used"},
        		new int[]{0,1});
        Option q7_8Used = q7_8.getOption(1);

        OptionEntry q7_8a = factory.createOptionEntry(
                "Hanging - What used",
                "What did you use?");
        doc.addEntry(q7_8a);
        q7_8a.setSection(methodSec);
        q7_8a.setLabel("7.8a");
        createOptions(factory, q7_8a,
        		new String[]{"STRING", "ROPE", "SHEET", "OTHER", "BELT/STRAP", "TOWEL"},
        		new int[]{1,2,3,4,5,6});
        q7_8a.getOption(3).setTextEntryAllowed(true);
        createOptionDependent(factory, q7_8Used, q7_8a);

        OptionEntry q7_9 = factory.createOptionEntry(
                "Strangling",
                "Strangling");
        doc.addEntry(q7_9);
        q7_9.setSection(methodSec);
        q7_9.setLabel("7.9");
        createOptions(factory, q7_9,
        		new String[]{"Not used", "Used"},
        		new int[]{0,1});
        Option q7_9Used = q7_9.getOption(1);

        OptionEntry q7_9a = factory.createOptionEntry(
                "Strangling - What used",
                "What did you use?");
        doc.addEntry(q7_9a);
        q7_9a.setSection(methodSec);
        q7_9a.setLabel("7.9a");
        createOptions(factory, q7_9a,
        		new String[]{"STRING", "ROPE", "SHEET", "OTHER", "BELT/STRAP", "TOWEL", "HANDS"},
        		new int[]{1,2,3,4,5,6,7});
        q7_9a.getOption(3).setTextEntryAllowed(true);
        createOptionDependent(factory, q7_9Used, q7_9a);

        OptionEntry q7_10 = factory.createOptionEntry(
                "Asphyxiation",
                "Asphyxiation");
        doc.addEntry(q7_10);
        q7_10.setSection(methodSec);
        q7_10.setLabel("7.10");
        createOptions(factory, q7_10,
        		new String[]{"Not used", "Used"},
        		new int[]{0,1});
        Option q7_10Used = q7_10.getOption(1);

        OptionEntry q7_10a = factory.createOptionEntry(
                "Asphyxiation - What used",
                "What did you use?");
        doc.addEntry(q7_10a);
        q7_10a.setSection(methodSec);
        q7_10a.setLabel("7.10a");
        createOptions(factory, q7_10a,
        		new String[]{"CARBON MONOXIDE", "PLASTIC BAG", "OTHER", "PILLOW"},
        		new int[]{1,2,3,4});
        q7_10a.getOption(2).setTextEntryAllowed(true);
        createOptionDependent(factory, q7_10Used, q7_10a);

        OptionEntry q7_11 = factory.createOptionEntry(
                "Jumping",
                "Jumping");
        doc.addEntry(q7_11);
        q7_11.setSection(methodSec);
        q7_11.setLabel("7.11");
        createOptions(factory, q7_11,
        		new String[]{"Not used", "Used"},
        		new int[]{0,1});
        Option q7_11Used = q7_11.getOption(1);

        OptionEntry q7_11a = factory.createOptionEntry(
                "Jumping - What land on",
                "On what did you land?");
        doc.addEntry(q7_11a);
        q7_11a.setSection(methodSec);
        q7_11a.setLabel("7.11a");
        createOptions(factory, q7_11a,
        		new String[]{"SOLID GROUND", "WATER", "OTHER", "DIDN'T FALL BUT WOULD HAVE BEEN LAND", "DIDN'T FALL BUT WOULD have LANDED IN WATER"},
        		new int[]{1,2,3,4,5});
        q7_11a.getOption(2).setTextEntryAllowed(true);
        createOptionDependent(factory, q7_11Used, q7_11a);

		NumericEntry q7_11b = factory.createNumericEntry(
                "Jumping - how high", "From how high did you jump?");
        doc.addEntry(q7_11b);
        q7_11b.setSection(methodSec);
        q7_11b.setLabel("7.11b");
        q7_11b.addUnit(UnitWrapper.instance().getUnit("ft"));
        q7_11b.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive"));
		createOptionDependent(factory, q7_11Used, q7_11b);

        OptionEntry q7_12 = factory.createOptionEntry(
                "Drowning",
                "Drowning");
        doc.addEntry(q7_12);
        q7_12.setSection(methodSec);
        q7_12.setLabel("7.12");
        createOptions(factory, q7_12,
        		new String[]{"Not used", "Used"},
        		new int[]{0,1});
        Option q7_12Used = q7_12.getOption(1);

		NumericEntry q7_12a = factory.createNumericEntry(
                "Drowning - how far from shore", "How far from shore or safety did you swim ?");
        doc.addEntry(q7_12a);
        q7_12a.setSection(methodSec);
        q7_12a.setLabel("7.12a");
        q7_12a.addUnit(UnitWrapper.instance().getUnit("ft"));
        q7_12a.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive"));
		createOptionDependent(factory, q7_12Used, q7_12a);

        OptionEntry q7_12b = factory.createOptionEntry(
                "Drowning - water warm/cold",
                "Was the water warm or cold?");
        doc.addEntry(q7_12b);
        q7_12b.setSection(methodSec);
        q7_12b.setLabel("7.12b");
        createOptions(factory, q7_12b,
        		new String[]{"WARM", "COLD"},
        		new int[]{1,2});
        createOptionDependent(factory, q7_12Used, q7_12b);

        OptionEntry q7_12c = factory.createOptionEntry(
                "Drowning - Can you swim?",
                "Can you swim?");
        doc.addEntry(q7_12c);
        q7_12c.setSection(methodSec);
        q7_12c.setLabel("7.12c");
        createOptions(factory, q7_12c,
        		new String[]{"NO", "YES"},
        		new int[]{0,1});
        createOptionDependent(factory, q7_12Used, q7_12c);

        OptionEntry q7_13 = factory.createOptionEntry(
                "Hitting body",
                "Hitting body");
        doc.addEntry(q7_13);
        q7_13.setSection(methodSec);
        q7_13.setLabel("7.13");
        createOptions(factory, q7_13,
        		new String[]{"Not used", "Used"},
        		new int[]{0,1});
        Option q7_13Used = q7_13.getOption(1);

        OptionEntry q7_13a = factory.createOptionEntry(
                "Hitting body - object hit",
                "What object did you hit?");
        doc.addEntry(q7_13a);
        q7_13a.setSection(methodSec);
        q7_13a.setLabel("7.13a");
        createOptions(factory, q7_13a,
        		new String[]{"WALL", "FLOOR", "WALL AND FLOOR", "OTHER", "FISTS", "SINK", "APPLIANCES", "HAMMER", "FURNITURE", "WHIP"},
        		new int[]{1,2,3,4,5,6,7,8,9,10});
        q7_13a.getOption(3).setTextEntryAllowed(true);
        createOptionDependent(factory, q7_13Used, q7_13a);

        IntegerEntry q7_13b = factory.createIntegerEntry(
                "Hitting body - times hit",
                "How many times did you hit yourself?");
        doc.addEntry(q7_13b);
        q7_13b.setSection(methodSec);
        q7_13b.setLabel("7.13b");
        q7_13b.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive Integer"));
        createOptionDependent(factory, q7_13Used, q7_13b);

        OptionEntry q7_13c = factory.createOptionEntry(
                "Hitting body - object hit",
                "What object did you hit?");
        doc.addEntry(q7_13c);
        q7_13c.setSection(methodSec);
        q7_13c.setLabel("7.13c");
        createOptions(factory, q7_13c,
        		new String[]{"HEAD AGAINST OBJECT", "FISTS AGAINST OBJECTS", "FISTS AGAINST HEAD", "OTHER"},
        		new int[]{1,2,3,4});
        q7_13c.getOption(3).setTextEntryAllowed(true);
        createOptionDependent(factory, q7_13Used, q7_13c);

        OptionEntry q7_13d = factory.createOptionEntry(
                "Hitting body - VERIFICATION",
                "VERIFICATION BY BRUISE/SWELLING?");
        doc.addEntry(q7_13d);
        q7_13d.setSection(methodSec);
        q7_13d.setLabel("7.13d");
        createOptions(factory, q7_13d,
        		new String[]{"No", "Yes"},
        		new int[]{0,1});
        createOptionDependent(factory, q7_13Used, q7_13d);

        OptionEntry q7_14 = factory.createOptionEntry(
                "Stopped required medications",
                "Stopped required medical treatments or medications (with direct intent to self-harm)");
        doc.addEntry(q7_14);
        q7_14.setSection(methodSec);
        q7_14.setLabel("7.14");
        createOptions(factory, q7_14,
        		new String[]{"Not used", "Used"},
        		new int[]{0,1});
        Option q7_14Used = q7_14.getOption(1);

        OptionEntry q7_14a = factory.createOptionEntry(
                "Stopped required medications - what stopped",
                "What did you stop doing?");
        doc.addEntry(q7_14a);
        q7_14a.setSection(methodSec);
        q7_14a.setLabel("7.14a");
        createOptions(factory, q7_14a,
        		new String[]{"STOPPED NEEDED MEDICAL TREATMENTS", "STOPPED MEDICATIONS", "OTHER"},
        		new int[]{1,2,3});
        q7_14a.getOption(2).setTextEntryAllowed(true);
        createOptionDependent(factory, q7_14Used, q7_14a);

		NumericEntry q7_14b = factory.createNumericEntry(
                "Stopped required medications - how long",
                "For how long was the treatment/medication stopped?");
        doc.addEntry(q7_14b);
        q7_14b.setSection(methodSec);
        q7_14b.setLabel("7.14b");
        q7_14b.addUnit(UnitWrapper.instance().getUnit("hrs"));
        q7_14b.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive"));
		createOptionDependent(factory, q7_14Used, q7_14b);

		TextEntry q7_14c = factory.createTextEntry(
                "Stopped required medications - what treatment for",
                "What was the treatment for?");
        doc.addEntry(q7_14c);
        q7_14c.setSection(methodSec);
        q7_14c.setLabel("7.14c");
		createOptionDependent(factory, q7_14Used, q7_14c);

		TextEntry q7_14d = factory.createTextEntry(
                "Stopped required medications - consequences",
                "What were expected consequences of stopping treatment");
        doc.addEntry(q7_14d);
        q7_14d.setSection(methodSec);
        q7_14d.setLabel("7.14d");
		createOptionDependent(factory, q7_14Used, q7_14d);

        OptionEntry q7_15 = factory.createOptionEntry(
                "Transportation related injury",
                "Transportation related injury (e.g., drove car off a cliff)");
        doc.addEntry(q7_15);
        q7_15.setSection(methodSec);
        q7_15.setLabel("7.15");
        createOptions(factory, q7_15,
        		new String[]{"Not used", "Used"},
        		new int[]{0,1});
        Option q7_15Used = q7_15.getOption(1);

		LongTextEntry q7_15a = factory.createLongTextEntry(
                "Transportation related injury - describe",
                "Describe");
        doc.addEntry(q7_15a);
        q7_15a.setSection(methodSec);
        q7_15a.setLabel("7.15a");
		createOptionDependent(factory, q7_15Used, q7_15a);

        OptionEntry q7_16 = factory.createOptionEntry(
                "Stepped into traffic",
                "Stepped into traffic");
        doc.addEntry(q7_16);
        q7_16.setSection(methodSec);
        q7_16.setLabel("7.16");
        createOptions(factory, q7_16,
        		new String[]{"Not used", "Used"},
        		new int[]{0,1});
        Option q7_16Used = q7_16.getOption(1);

		LongTextEntry q7_16a = factory.createLongTextEntry(
                "Stepped into traffic - describe",
                "Describe");
        doc.addEntry(q7_16a);
        q7_16a.setSection(methodSec);
        q7_16a.setLabel("7.16a");
		createOptionDependent(factory, q7_16Used, q7_16a);

        OptionEntry q7_17 = factory.createOptionEntry(
                "Other",
                "Other");
        doc.addEntry(q7_17);
        q7_17.setSection(methodSec);
        q7_17.setLabel("7.17");
        createOptions(factory, q7_17,
        		new String[]{"Not used", "Used"},
        		new int[]{0,1});
        Option q7_17Used = q7_17.getOption(1);

		LongTextEntry q7_17a = factory.createLongTextEntry(
                "Other - describe",
                "Describe");
        doc.addEntry(q7_17a);
        q7_17a.setSection(methodSec);
        q7_17a.setLabel("7.17a");
		createOptionDependent(factory, q7_17Used, q7_17a);

        OptionEntry q8 = factory.createOptionEntry(
                "RATE MEDICAL RISK OF DEATH",
                "INTERVIEWER: RATE MEDICAL RISK OF DEATH BASED ON METHOD AND ON OTHER SUBSTANCES PRESENT AT TIME");
        doc.addEntry(q8);
        q8.setSection(methodSec);
        q8.setLabel("08");
        createOptions(factory, q8,
        		new String[]{"Very low", "Low", "Moderate", "High", "Very high", "Severe"},
        		new int[]{1,2,3,4,5,6},
        		new String[]{"Less than/equal to 5 pills (unless medication potentially lethal in low doses); scratching; "+
        					 "reopening partially healed wounds; head banging, swallowing small, non-sharp objects; going "+
        					 "underdressed into cold for brief time, lying down at night in the middle of a non-busy road but getting up "+
        					 "when a car doesn't come or swimming out to middle of lake and returning upon getting tired. Minor "+
        					 "heroin overdose 1.5 times usual dependent dose.",
        					 "Superficial cut on surface or limbs; 6-10 pills (or fewer if medication potentially lethal in low doses); "+
        					 "cigarette burn(s), jumping feet first from very low place (less than 10 feet). Heroin overdose 1.5 times "+
        					 "usual dependent dose combined with other drugs and/or alcohol.",
        					 "Overdose on 11-50 pills or two or more types of pills or 6-10 pills potentially lethal in low doses " +
        					 "and combined with alcohol; deep cuts anywhere but neck, swallowing ≤ 12 oz shampoo or astringent, ≤ 2 " +
        					 "oz. lighter fluid, or ≤ 4 tbsp. cleaning compounds; igniting flammable substance on limb. Moderate heroin "+
        					 "overdose 2 - < 3 times usual dependent dose.",
        					 "Overdose with over 50 pills or 11-30 pills potentially lethal in low doses or combined with large "+
        					 "amount of alcohol, stabbing to body; pulling trigger of a loaded gun aimed at a limb (arm or leg), "+
        					 "swallowing > 2 oz lighter fluid, > 12 oz shampoo or astringent or > 4 tbsp. cleaning compounds, igniting "+
        					 "flammable substance on multiple limbs and torso, walking into heavy traffic.",
        					 "Overdose with over 30 pills lethal in small doses or combined with large amount of alcohol; " +
        					 "poison (unless small amount not potentially lethal); attempted drowning; suffocation; deep cuts to the "+
        					 "throat or limbs; jumping from low place (less that 20 feet), igniting flammable substance all over body, "+
        					 "electrocution, throwing self in front of or from car going less than 30 miles/hr, strangulation. Serious "+
        					 "heroin overdose 3 or more times usual dependent dose.",
        					 "Pulling trigger of loaded gun aimed at vital area (such as torso or head); Russian roulette, jumping "+
        					 "from a high place (more than 20 feet); hanging (feet above the ground); asphyxiation (such as carbon "+
        					 "monoxide suffocation); jumping in front of auto going faster than 30 miles/hr or off overpass in rush hour "+
        					 "traffic, attempted drowning after ingesting alcohol or other drugs, swallowing nail polish remover, "+
        					 "turpentine or similar substances. Serious heroin overdose 3 or more times usual dependent dose combined "+
        					 "with other drugs and/or alcohol."
        					 });

        Section intentSec = factory.createSection("Intent Section", "Intent");
        doc.addSection(intentSec);
        SectionOccurrence intentSecOcc = factory.createSectionOccurrence("Intent Section Occ");
        intentSec.addOccurrence(intentSecOcc);

        LongTextEntry q9 = factory.createLongTextEntry(
                "Final outcome intended/expected",
                "At the time of your self-injury/suicide attempt/overdose, what final " +
                        "outcome did you most intend and expect? (RECORD ANSWER VERBATIM.)");
        doc.addEntry(q9);
        q9.setSection(intentSec);
        q9.setLabel("09");

        OptionEntry q9a = factory.createOptionEntry(
                "RATE CONSCIOUS INTENT TO CAUSE SELF-INJURY",
                "INTERVIEWER: RATE SUBJECT'S CONSCIOUS INTENT TO CAUSE SELF-INJURY, I.E., DEGREE " +
                        "THAT BEHAVIOR WAS INITIATED AND PERFORMED IN ORDER TO CAUSE SELF-INJURY OR IN " +
                        "ORDER TO RISK SELF-INJURY.");
        doc.addEntry(q9a);
        q9a.setSection(intentSec);
        createOptions(factory, q9a,
        		new String[]{"No bodily or physiological harm intended or expected",
				 "Ambivalent intent to cause bodily injury or physiological harm to self and took a chance",
				 "Clear expectations of some bodily injury, physiological harm to self"},
				new int[]{0,1,2},
				new String[]{"e.g., expected to fly from window ledge; habitual " +
				 "substance abuser expected to get high as usual; bulimic expected to purge as usual",
				 "e.g., Russian roulette, habitual substance abuser took more than normal amount",
				 "e.g., expected to sleep for a whole weekend, expected skin to be broken, bulimic expected to disrupt electrolyte balance), or death"});

        OptionEntry q10 = factory.createOptionEntry(
                "Thinking about suicide",
                "Just before or at the time of this self-injury/overdose, were you thinking about suicide or wishing you were dead?");
        doc.addEntry(q10);
        q10.setSection(intentSec);
        createOptions(factory, q10,
        		new String[]{"Not at all",
        					 "I was wishing I was dead, but the thought of suicide did not go thru my mind",
        					 "The thought of suicide passed thru my mind",
        					 "I briefly considered it, but not seriously",
        					 "I was thinking about it and was somewhat serious",
        					 "I was very serious about dying but was also somewhat ambivalent",
        					 "I was extremely serious, intended to die and was not ambivalent at all"},
        		new int[]{0,1,2,3,4,5,6});

        NarrativeEntry q11 = factory.createNarrativeEntry(
        		"Q11 Narrative",
        		"Would you say that you injured yourself/attempted suicide/overdosed for any of the reasons on " +
        		"this list and, if so, which ones? Please Give Card A to client");
        doc.addEntry(q11);
        q11.setSection(intentSec);
        q11.setLabel("11");

        OptionEntry q11_1 = factory.createOptionEntry(
                "To stop bad feelings",
                "To stop bad feelings");
        doc.addEntry(q11_1);
        q11_1.setSection(intentSec);
        q11_1.setLabel("11.1");
        createOptions(factory, q11_1,
        		new String[]{"Not mentioned", "Mentioned"},
        		new int[]{0,1});

        OptionEntry q11_2 = factory.createOptionEntry(
                "To communicate desperation",
                "To communicate to or let others know how desperate you were");
        doc.addEntry(q11_2);
        q11_2.setSection(intentSec);
        q11_2.setLabel("11.2");
        createOptions(factory, q11_2,
        		new String[]{"Not mentioned", "Mentioned"},
        		new int[]{0,1});

        OptionEntry q11_3 = factory.createOptionEntry(
                "To get help",
                "To get help");
        doc.addEntry(q11_3);
        q11_3.setSection(intentSec);
        q11_3.setLabel("11.3");
        createOptions(factory, q11_3,
        		new String[]{"Not mentioned", "Mentioned"},
        		new int[]{0,1});

        OptionEntry q11_4 = factory.createOptionEntry(
                "To gain admission into a hospital",
                "To gain admission into a hospital or treatment program");
        doc.addEntry(q11_4);
        q11_4.setSection(intentSec);
        q11_4.setLabel("11.4");
        createOptions(factory, q11_4,
        		new String[]{"Not mentioned", "Mentioned"},
        		new int[]{0,1});

        OptionEntry q11_5 = factory.createOptionEntry(
                "To die",
                "To die");
        doc.addEntry(q11_5);
        q11_5.setSection(intentSec);
        q11_5.setLabel("11.5");
        createOptions(factory, q11_5,
        		new String[]{"Not mentioned", "Mentioned"},
        		new int[]{0,1});

        OptionEntry q11_6 = factory.createOptionEntry(
                "To feel something, even if it was pain",
                "To feel something, even if it was pain");
        doc.addEntry(q11_6);
        q11_6.setSection(intentSec);
        q11_6.setLabel("11.6");
        createOptions(factory, q11_6,
        		new String[]{"Not mentioned", "Mentioned"},
        		new int[]{0,1});

        OptionEntry q11_7 = factory.createOptionEntry(
                "To punish yourself",
                "To punish yourself");
        doc.addEntry(q11_7);
        q11_7.setSection(intentSec);
        q11_7.setLabel("11.7");
        createOptions(factory, q11_7,
        		new String[]{"Not mentioned", "Mentioned"},
        		new int[]{0,1});

        OptionEntry q11_8 = factory.createOptionEntry(
                "To get a vacation from having to try so hard",
                "To get a vacation from having to try so hard");
        doc.addEntry(q11_8);
        q11_8.setSection(intentSec);
        q11_8.setLabel("11.8");
        createOptions(factory, q11_8,
        		new String[]{"Not mentioned", "Mentioned"},
        		new int[]{0,1});

        OptionEntry q11_9 = factory.createOptionEntry(
                "To get out of doing something",
                "To get out of doing something");
        doc.addEntry(q11_9);
        q11_9.setSection(intentSec);
        q11_9.setLabel("11.9");
        createOptions(factory, q11_9,
        		new String[]{"Not mentioned", "Mentioned"},
        		new int[]{0,1});

        OptionEntry q11_10 = factory.createOptionEntry(
                "To shock or impress others",
                "To shock or impress others");
        doc.addEntry(q11_10);
        q11_10.setSection(intentSec);
        q11_10.setLabel("11.10");
        createOptions(factory, q11_10,
        		new String[]{"Not mentioned", "Mentioned"},
        		new int[]{0,1});

        OptionEntry q11_11 = factory.createOptionEntry(
                "To prove to yourself that things really were bad",
                "To prove to yourself that things really were bad");
        doc.addEntry(q11_11);
        q11_11.setSection(intentSec);
        q11_11.setLabel("11.11");
        createOptions(factory, q11_11,
        		new String[]{"Not mentioned", "Mentioned"},
        		new int[]{0,1});

        OptionEntry q11_12 = factory.createOptionEntry(
                "To give you something, anything to do",
                "To give you something, anything to do");
        doc.addEntry(q11_12);
        q11_12.setSection(intentSec);
        q11_12.setLabel("11.12");
        createOptions(factory, q11_12,
        		new String[]{"Not mentioned", "Mentioned"},
        		new int[]{0,1});

        OptionEntry q11_13 = factory.createOptionEntry(
                "To get other people to act differently or change",
                "To get other people to act differently or change");
        doc.addEntry(q11_13);
        q11_13.setSection(intentSec);
        q11_13.setLabel("11.13");
        createOptions(factory, q11_13,
        		new String[]{"Not mentioned", "Mentioned"},
        		new int[]{0,1});

        OptionEntry q11_14 = factory.createOptionEntry(
                "To get back at or hurt someone",
                "To get back at or hurt someone");
        doc.addEntry(q11_14);
        q11_14.setSection(intentSec);
        q11_14.setLabel("11.14");
        createOptions(factory, q11_14,
        		new String[]{"Not mentioned", "Mentioned"},
        		new int[]{0,1});

        OptionEntry q11_15 = factory.createOptionEntry(
                "To make others better off",
                "To make others better off");
        doc.addEntry(q11_15);
        q11_15.setSection(intentSec);
        q11_15.setLabel("11.15");
        createOptions(factory, q11_15,
        		new String[]{"Not mentioned", "Mentioned"},
        		new int[]{0,1});

        OptionEntry q11_16 = factory.createOptionEntry(
                "To get away or escape",
                "To get away or escape");
        doc.addEntry(q11_16);
        q11_16.setSection(intentSec);
        q11_16.setLabel("11.16");
        createOptions(factory, q11_16,
        		new String[]{"Not mentioned", "Mentioned"},
        		new int[]{0,1});
        Option q11_16Mentioned = q11_16.getOption(1);

        NarrativeEntry q11_16n = factory.createNarrativeEntry(
        		"Q11.16 Narrative",
        		"To get away or escape from what? (check all that apply)");
        doc.addEntry(q11_16n);
        q11_16n.setSection(intentSec);
        createOptionDependent(factory, q11_16Mentioned, q11_16n);

        BooleanEntry q11_16a = factory.createBooleanEntry(
                "Escape - thoughts and memories",
                "your thoughts and memories");
        doc.addEntry(q11_16a);
        q11_16a.setSection(intentSec);
        createOptionDependent(factory, q11_16Mentioned, q11_16a);

        BooleanEntry q11_16b = factory.createBooleanEntry(
                "Escape - feelings",
                "your feelings");
        doc.addEntry(q11_16b);
        q11_16b.setSection(intentSec);
        createOptionDependent(factory, q11_16Mentioned, q11_16b);

        BooleanEntry q11_16c = factory.createBooleanEntry(
                "Escape - other people",
                "other people");
        doc.addEntry(q11_16c);
        q11_16c.setSection(intentSec);
        createOptionDependent(factory, q11_16Mentioned, q11_16c);

        BooleanEntry q11_16d = factory.createBooleanEntry(
                "Escape - yourself",
                "yourself");
        doc.addEntry(q11_16d);
        q11_16d.setSection(intentSec);
        createOptionDependent(factory, q11_16Mentioned, q11_16d);

        OptionEntry q11_17 = factory.createOptionEntry(
                "To stop feeling numb or dead",
                "To stop feeling numb or dead");
        doc.addEntry(q11_17);
        q11_17.setSection(intentSec);
        q11_17.setLabel("11.17");
        createOptions(factory, q11_17,
        		new String[]{"Not mentioned", "Mentioned"},
        		new int[]{0,1});

        OptionEntry q11_18 = factory.createOptionEntry(
                "To prevent being hurt in a worse way",
                "To prevent being hurt in a worse way");
        doc.addEntry(q11_18);
        q11_18.setSection(intentSec);
        q11_18.setLabel("11.18");
        createOptions(factory, q11_18,
        		new String[]{"Not mentioned", "Mentioned"},
        		new int[]{0,1});

        OptionEntry q11_19 = factory.createOptionEntry(
                "To stop feeling angry or frustrated or enraged",
                "To stop feeling angry or frustrated or enraged");
        doc.addEntry(q11_19);
        q11_19.setSection(intentSec);
        q11_19.setLabel("11.19");
        createOptions(factory, q11_19,
        		new String[]{"Not mentioned", "Mentioned"},
        		new int[]{0,1});

        OptionEntry q11_20 = factory.createOptionEntry(
                "To demonstrate to others how wrong they are/were",
                "To demonstrate to others how wrong they are/were");
        doc.addEntry(q11_20);
        q11_20.setSection(intentSec);
        q11_20.setLabel("11.20");
        createOptions(factory, q11_20,
        		new String[]{"Not mentioned", "Mentioned"},
        		new int[]{0,1});

        OptionEntry q11_21 = factory.createOptionEntry(
                "To relieve anxiety or terror",
                "To relieve anxiety or terror");
        doc.addEntry(q11_21);
        q11_21.setSection(intentSec);
        q11_21.setLabel("11.21");
        createOptions(factory, q11_21,
        		new String[]{"Not mentioned", "Mentioned"},
        		new int[]{0,1});

        OptionEntry q11_22 = factory.createOptionEntry(
                "To distract yourself from other problems",
                "To distract yourself from other problems");
        doc.addEntry(q11_22);
        q11_22.setSection(intentSec);
        q11_22.setLabel("11.22");
        createOptions(factory, q11_22,
        		new String[]{"Not mentioned", "Mentioned"},
        		new int[]{0,1});

        OptionEntry q11_23 = factory.createOptionEntry(
                "To relieve feelings of aloneness, emptiness or isolation",
                "To relieve feelings of aloneness, emptiness or isolation");
        doc.addEntry(q11_23);
        q11_23.setSection(intentSec);
        q11_1.setLabel("11.1");
        createOptions(factory, q11_1,
        		new String[]{"Not mentioned", "Mentioned"},
        		new int[]{0,1});

        OptionEntry q11_24 = factory.createOptionEntry(
                "To stop feeling self-hatred, shame",
                "To stop feeling self-hatred, shame");
        doc.addEntry(q11_24);
        q11_24.setSection(intentSec);
        q11_24.setLabel("11.24");
        createOptions(factory, q11_24,
        		new String[]{"Not mentioned", "Mentioned"},
        		new int[]{0,1});

        OptionEntry q11_25 = factory.createOptionEntry(
                "To express anger or frustration",
                "To express anger or frustration");
        doc.addEntry(q11_25);
        q11_25.setSection(intentSec);
        q11_25.setLabel("11.25");
        createOptions(factory, q11_25,
        		new String[]{"Not mentioned", "Mentioned"},
        		new int[]{0,1});

        OptionEntry q11_26 = factory.createOptionEntry(
                "To obtain relief from a terrible state of mind",
                "To obtain relief from a terrible state of mind");
        doc.addEntry(q11_26);
        q11_26.setSection(intentSec);
        q11_26.setLabel("11.26");
        createOptions(factory, q11_26,
        		new String[]{"Not mentioned", "Mentioned"},
        		new int[]{0,1});

        OptionEntry q11_27 = factory.createOptionEntry(
                "To make others understand how desperate you are",
                "To make others understand how desperate you are");
        doc.addEntry(q11_27);
        q11_27.setSection(intentSec);
        q11_27.setLabel("11.27");
        createOptions(factory, q11_27,
        		new String[]{"Not mentioned", "Mentioned"},
        		new int[]{0,1});

        OptionEntry q11_28 = factory.createOptionEntry(
                "To stop feeling sad",
                "To stop feeling sad");
        doc.addEntry(q11_28);
        q11_28.setSection(intentSec);
        q11_28.setLabel("11.28");
        createOptions(factory, q11_28,
        		new String[]{"Not mentioned", "Mentioned"},
        		new int[]{0,1});

        OptionEntry q11_29 = factory.createOptionEntry(
                "Other",
                "Other");
        doc.addEntry(q11_29);
        q11_29.setSection(intentSec);
        q11_29.setLabel("11.29");
        createOptions(factory, q11_29,
        		new String[]{"Not mentioned", "Mentioned"},
        		new int[]{0,1});

        OptionEntry q12 = factory.createOptionEntry(
                "At time - Consider episode a suicide attempt",
                "At the time it occurred, did you consider the episode a suicide attempt, even if you did not really intend to die?");
        doc.addEntry(q12);
        q12.setSection(intentSec);
        q12.setLabel("12");
        createOptions(factory, q12,
        		new String[]{"No", "Yes"},
        		new int[]{0,1});

        OptionEntry q13 = factory.createOptionEntry(
                "Now - Consider episode a suicide attempt",
                "Do you now consider that episode a suicide attempt?");
        doc.addEntry(q13);
        q13.setSection(intentSec);
        q13.setLabel("13");
        createOptions(factory, q13,
        		new String[]{"No", "Yes"},
        		new int[]{0,1});

        NarrativeEntry q13a_n = factory.createNarrativeEntry(
        		"Q13a narrative",
        		"If Q. 12 & 13 ARE CODED DIFFERENTLY, ASK THE FOLLOWING AND RECORD ANSWER VERBATIM.");
        doc.addEntry(q13a_n);
        q13a_n.setSection(intentSec);

        LongTextEntry q13a = factory.createLongTextEntry(
                "What accounts for this change?",
                "What accounts for this change?");
        doc.addEntry(q13a);
        q13a.setSection(intentSec);
        q13a.setLabel("13a");

        OptionEntry q14 = factory.createOptionEntry(
                "SUBJECT'S CONSCIOUS EXPECTATION OF FATAL OUTCOME",
                "INTERVIEWER: RATE SUBJECT'S CONSCIOUS EXPECTATION OF FATAL OUTCOME.");
        doc.addEntry(q14);
        q14.setSection(intentSec);
        q14.setLabel("14");
        createOptions(factory, q14,
        		new String[]{"No expectation","Uncertain of outcome", "Clear expectations of fatal outcome"},
        		new int[]{0,1,2});

        Section communicationSec = factory.createSection("Communication Section", "Communication of Suicide Intent");
        doc.addSection(communicationSec);
        SectionOccurrence communicationSecOcc = factory.createSectionOccurrence("Communication Section Occ");
        communicationSec.addOccurrence(communicationSecOcc);

        OptionEntry q15 = factory.createOptionEntry(
                "Tell anyone thinking of suicide",
                "At the time or near the time of this episode, did you tell anyone, directly or indirectly, that you were thinking of " +
                        "suicide or that you wished you were dead? (ASSESS IF SUBJECT COMMUNICATED SUICIDE IDEATION)");
        doc.addEntry(q15);
        q15.setSection(communicationSec);
        q15.setLabel("15");
        createOptions(factory, q15,
        		new String[]{"No", "Indirect communication", "Direct communication"},
        		new int[]{0,1,2});
        Option q15Indirect = q15.getOption(1);
        Option q15Direct = q15.getOption(2);

        LongTextEntry q15a = factory.createLongTextEntry(
                "Tell anyone thinking of suicide - describe",
                "Describe");
        doc.addEntry(q15a);
        q15a.setSection(communicationSec);
        q15a.setLabel("15a");
        createOptionDependent(factory, q15Direct, q15a);
        createOptionDependent(factory, q15Indirect, q15a);

        OptionEntry q16 = factory.createOptionEntry(
                "Threaten suicide to anyone",
                "At the time or near the time of this episode, did you threaten suicide to anyone or do anything that could be or " +
                        "was interpreted by someone else as a threat to harm or kill yourself? (ASSESS IF SUBJECT THREATENED)");
        doc.addEntry(q16);
        q16.setSection(communicationSec);
        q16.setLabel("16");
        createOptions(factory, q16,
        		new String[]{"No", "Indirect threat", "Direct threat"},
        		new int[]{0,1,2});
        Option q16Indirect = q16.getOption(1);
        Option q16Direct = q16.getOption(2);

        LongTextEntry q16a = factory.createLongTextEntry(
                "Threaten suicide to anyone - describe",
                "Describe");
        doc.addEntry(q16a);
        q16a.setSection(communicationSec);
        q16a.setLabel("16a");
        createOptionDependent(factory, q16Direct, q16a);
        createOptionDependent(factory, q16Indirect, q16a);

        Section impulsivitySec = factory.createSection("Impulsivity Section", "Impulsivity and Probability of Intervention");
        doc.addSection(impulsivitySec);
        SectionOccurrence impulsivitySecOcc = factory.createSectionOccurrence("Impulsivity Section Occ");
        impulsivitySec.addOccurrence(impulsivitySecOcc);

        OptionEntry q17 = factory.createOptionEntry(
                "Plan or impulsive - rate",
                "Did you plan your self-injury/suicide attempt/overdose, or was it an impulsive act? INTERVIEWER: RATE IMPULSIVITY OF ACT.");
        doc.addEntry(q17);
        q17.setSection(impulsivitySec);
        q17.setLabel("17");
        createOptions(factory, q17,
        		new String[]{"Commitment to act, followed by very careful or elaborate plan carried out over a period of time.",
        					 "Actively planned and/or got implements. Had impulse, resisted for _____ days, then acted.",
        					 "Actively planned and/or got implements. Had impulse, resisted for less than 24 hours.",
        					 "No active planning. Had impulse, resisted for _____ days, then acted.",
        					 "No active planning. Had impulse, resisted for less than 24 hours, then acted.",
        					 "No active planning. Occurred impulsively, with no forethought and without very strong emotion.",
        					 "No active planning. Occurred impulsively, with no forethought and with very strong emotion."},
        		new int[]{1,2,3,4,5,6,7});

        LongTextEntry q17a = factory.createLongTextEntry(
                "Plan or impulsive - verbatim",
                "RECORD ANSWER VERBATIM");
        doc.addEntry(q17a);
        q17a.setSection(impulsivitySec);
        q17a.setLabel("17");

        OptionEntry q18 = factory.createOptionEntry(
                "Write suicide note",
                "At the time or near the time of this episode, did you write a suicide note?");
        doc.addEntry(q18);
        q18.setSection(impulsivitySec);
        q18.setLabel("18");
        createOptions(factory, q18,
        		new String[]{"No", "Yes"},
        		new int[]{0,1});

        OptionEntry q19 = factory.createOptionEntry(
                "Arrange so difficult to be stopped",
                "Did you arrange your self-injury/suicide attempt/overdose in such a way that it " +
                        "would be difficult for anyone to find, stop, or save you?");
        doc.addEntry(q19);
        q19.setSection(impulsivitySec);
        q19.setLabel("19");
        createOptions(factory, q19,
        		new String[]{"No","Somewhat","Yes"},
        		new int[]{0,1,2});

        LongTextEntry q19a = factory.createLongTextEntry(
                "Arrange so difficult to be stopped - verbatim",
                "Describe the circumstances: (RECORD ANSWER VERBATIM.)");
        doc.addEntry(q19a);
        q19a.setSection(impulsivitySec);
        q19a.setLabel("19a");

        OptionEntry q20 = factory.createOptionEntry(
                "RATE PROBABILITY OF INTERVENTION",
                "INTERVIEWER: RATE PROBABILITY OF INTERVENTION BASED ON ALL INFORMATION");
        doc.addEntry(q20);
        q20.setSection(impulsivitySec);
        q20.setLabel("20");
        createOptions(factory, q20,
        		new String[]{"Chance of intervention remote",
        					 "Improbable intervention",
        					 "Ambiguous chance of intervention",
        					 "Probable intervention",
        					 "Certain intervention"},
        		new int[]{1,2,3,4,5},
				new String[]{"Act committed by person in a solitary or isolated place " +
						 "without access to telephone (i.e., a wooded area, cemetery, etc.)",
						 "Act committed by person alone, with intervention by a passerby possible although "+
						 "not expected (i.e., in a motel room, an office late at night, at home alone with no one expected)",
						 "Act committed by person alone, with no certainty of immediate "+
						 "assistance. However, a reasonable chance for intervention existed (i.e., the victim is aware of the "+
						 "impending arrival of others)",
						 "Act committed with another person in the immediate vicinity but not visibly present " +
						 "(such as in the same dwelling/building). Or made phone call but did not directly communicate intention.",
						 "Act committed in the presence of another person/made phone call immediately before "+
						 "or after in order to advise of act or to say good-bye."});

        Section treatmentSec = factory.createSection("Treatment Section", "Level of Medical Treatment");
        doc.addSection(treatmentSec);
        SectionOccurrence treatmentSecOcc = factory.createSectionOccurrence("Treatment Section Occ");
        treatmentSec.addOccurrence(treatmentSecOcc);

        NarrativeEntry q21 = factory.createNarrativeEntry("Q21",
        		"Following your self-injury/suicide attempt/overdose were you taken to any of these places or did you turn to " +
        		"any of these places or people for help? (Give Card B)");
        doc.addEntry(q21);
        q21.setSection(treatmentSec);
        q21.setLabel("21");

        OptionEntry q21_1 = factory.createOptionEntry(
                "Physician/nurse", "Physician/nurse (Visit)");
        doc.addEntry(q21_1);
        q21_1.setSection(treatmentSec);
        q21_1.setLabel("21.1");
        createOptions(factory, q21_1,
        		new String[]{"Not contacted", "Contacted"},
        		new int[]{0,1});

        OptionEntry q21_2 = factory.createOptionEntry(
                "Crisis outreach", "Crisis outreach/after hours team/mental health professional (In person visit)");
        doc.addEntry(q21_2);
        q21_2.setSection(treatmentSec);
        q21_2.setLabel("21.2");
        createOptions(factory, q21_2,
        		new String[]{"Not contacted", "Contacted"},
        		new int[]{0,1});

        OptionEntry q21_3 = factory.createOptionEntry(
                "Police/wellness check", "Police/wellness check (At home or other residence)");
        doc.addEntry(q21_3);
        q21_3.setSection(treatmentSec);
        q21_3.setLabel("21.3");
        createOptions(factory, q21_3,
        		new String[]{"Not contacted", "Contacted"},
        		new int[]{0,1});

        OptionEntry q21_4 = factory.createOptionEntry(
                "Paramedics/ambulance/aid car", "Paramedics/ambulance/aid car (At home or other residence)");
        doc.addEntry(q21_4);
        q21_4.setSection(treatmentSec);
        q21_4.setLabel("21.4");
        createOptions(factory, q21_4,
        		new String[]{"Not contacted", "Contacted"},
        		new int[]{0,1});

        OptionEntry q21_5 = factory.createOptionEntry(
                "Hospital emergency room", "Hospital emergency room");
        doc.addEntry(q21_5);
        q21_5.setSection(treatmentSec);
        q21_5.setLabel("21.5");
        createOptions(factory, q21_5,
        		new String[]{"Not contacted", "Contacted"},
        		new int[]{0,1});
        Option q21_5Contacted = q21_5.getOption(1);

        OptionEntry q21_5b = factory.createOptionEntry(
                "Hospital emergency room - treated", "Treated?");
        doc.addEntry(q21_5b);
        q21_5b.setSection(treatmentSec);
        q21_5b.setLabel("21.5b");
        createOptions(factory, q21_5b,
        		new String[]{"Not medically treated", "Treated"},
        		new int[]{0,1});
        createOptionDependent(factory, q21_5Contacted, q21_5b);

        OptionEntry q21_6 = factory.createOptionEntry(
                "Inpatient, psychiatric unit", "Inpatient, psychiatric unit");
        doc.addEntry(q21_6);
        q21_6.setSection(treatmentSec);
        q21_6.setLabel("21.6");
        createOptions(factory, q21_6,
        		new String[]{"Not contacted", "Contacted"},
        		new int[]{0,1});
        Option q21_6Contacted = q21_6.getOption(1);

        NumericEntry q21_6b = factory.createNumericEntry(
                "Inpatient, psychiatric unit - no. days", "Number of days");
        doc.addEntry(q21_6b);
        q21_6b.setSection(treatmentSec);
        q21_6b.setLabel("21.6b");
        q21_6b.setDefaultValue(new Double(0));
        createOptionDependent(factory, q21_6Contacted, q21_6b);

        OptionEntry q21_6c = factory.createOptionEntry(
                "Inpatient, psychiatric unit - Voluntary", "Voluntary");
        doc.addEntry(q21_6c);
        q21_6c.setSection(treatmentSec);
        q21_6c.setLabel("21.6c");
        createOptions(factory, q21_6c,
        		new String[]{"Yes",
        					 "voluntary but threatened with legal commitment if not agreed to",
        					 "legally detained on a 24-48 hr. hold",
        					 "72+ hold"},
        		new int[]{1,2,3,4});
        createOptionDependent(factory, q21_6Contacted, q21_6c);

        OptionEntry q21_7 = factory.createOptionEntry(
                "Hospital medical floor", "Hospital medical floor");
        doc.addEntry(q21_7);
        q21_7.setSection(treatmentSec);
        q21_7.setLabel("21.7");
        createOptions(factory, q21_7,
        		new String[]{"Not contacted", "Contacted"},
        		new int[]{0,1});
        Option q21_7Contacted = q21_7.getOption(1);

        NumericEntry q21_7b = factory.createNumericEntry(
                "Hospital medical floor - no. days", "Number of days");
        doc.addEntry(q21_7b);
        q21_7b.setSection(treatmentSec);
        q21_7b.setLabel("21.7b");
        q21_7b.setDefaultValue(new Double(0));
        createOptionDependent(factory, q21_7Contacted, q21_7b);

        OptionEntry q21_8 = factory.createOptionEntry(
                "Intensive care", "Intensive care");
        doc.addEntry(q21_8);
        q21_8.setSection(treatmentSec);
        q21_8.setLabel("21.8");
        createOptions(factory, q21_8,
        		new String[]{"Not contacted", "Contacted"},
        		new int[]{0,1});
        Option q21_8Contacted = q21_8.getOption(1);

        NumericEntry q21_8b = factory.createNumericEntry(
                "Intensive care - no. days", "Number of days");
        doc.addEntry(q21_8b);
        q21_8b.setSection(treatmentSec);
        q21_8b.setLabel("21.8b");
        q21_8b.setDefaultValue(new Double(0));
        createOptionDependent(factory, q21_8Contacted, q21_8b);

        NarrativeEntry q22 = factory.createNarrativeEntry("Q22", "What was your physical condition afterward?");
        doc.addEntry(q22);
        q22.setSection(treatmentSec);
        q22.setLabel("22");

        LongTextEntry q22a = factory.createLongTextEntry("Physical condition - verbatim", "RECORD VERBATIM ANSWER.");
        doc.addEntry(q22a);
        q22a.setSection(treatmentSec);
        q22a.setLabel("22a");

        LongTextEntry q22b = factory.createLongTextEntry("Physical condition - medical records", "RECORD INFORMATION FROM MEDICAL RECORDS.");
        doc.addEntry(q22b);
        q22b.setSection(treatmentSec);
        q22b.setLabel("22b");

        OptionEntry q22c = factory.createOptionEntry(
                "RATE PHYSICAL CONDITION", "INTERVIEWER: RATE PHYSICAL CONDITION FOLLOWING EPISODE");
        doc.addEntry(q22c);
        q22c.setSection(treatmentSec);
        q22c.setLabel("22c");
        createOptions(factory, q22c,
        		new String[]{"No effect",
        					 "Very mild effect",
        					 "Mild effect",
        					 "Moderate effect",
        					 "Severe effect",
        					 "Very severe effect",
        					 "Extremely severe effect",
        					 "Lethal effect"},
        		new int[]{0,1,2,3,4,5,6,7},
        		new String[]{"No effect",
				 "Death impossible. (e.g., went to sleep at regular time, woke up ok; " +
				 "slightly queasy or nauseous, but no vomiting; rash type abrasion, bruise; chilled; " +
				 "small non-sharp objects in digestive tract)",
				 "Death is highly improbable; could only occur due to secondary complications or very unusual " +
				 "circumstance. (e.g., nauseous; slept significantly more than normal, woke up ok; 1st degree burn; " +
				 "superficial lacerations without tendon, nerve or vessel damage and not requiring sutures; minimal blood " +
				 "loss; larger non-sharp objects in digestive tract)",
				 "Death is improbable; could only occur due to secondary effects; medical aid is warranted, " +
				 "but not required for survival (e.g., vomiting; slept significantly more than normal, woke up still " +
				 "drowsy; 2nd degree burn; non-septic infection; shallow lacerations on limbs or torso with slight " +
				 "tendon damage requiring sutures; broken digits or limbs; slight to moderate hypothermia or frost " +
				 "bite; slight concussion with no disorientation)",
				 "Death is improbable if first aid or medical attention is administered (e.g., " +
				 "respiratory failure, elevated blood pressure, convulsions or seizures; 3rd degree burn covering 20% " +
				 "or less of body surface; septicemia; deep lacerations on face, limbs or torso with tendon damage or " +
				 "severing and possible nerve, vessel or artery damage; cuts on neck which may require sutures but no major " +
				 "nerves or vessels severed; blood loss less than 100 cc.; bullet in or deep piercing of limbs; severe head " +
				 "injury with decreased orientation; moderate tissue damage; sharp objects in digestive tract; vertebral " +
				 "fracture without cord injury)",
				 "Death is somewhat probable unless first aid or medical attention is administered " +
				 "(e.g., caustic substance; hypertensive crisis; stroke; 3rd degree burn covering 40% of body surface; severe, " +
				 "deep lacerations on face, limbs or torso with severing of major arteries; blood loss more than 200 cc; loss " +
				 "of eye, ear or digits; bullet or deep piercing in lower torso; severe tissue loss; vertebral fracture with " +
				 "cord injury; mild hypoxia; comatose but still responding to pain)",
				 "Death is highly probable without out immediate and vigorous medical attention, " +
				 "and may occur even with vigorous first aid or medical attention (e.g., 3rd degree burn covering 50% or " +
				 "more of body surface; loss of limb; deep lacerations on neck with major artery damage, i.e., cutting " +
				 "jugular vein; irreparable damage and/or systemic organ failure; gun shot or bullet in chest or head; " +
				 "closed airways, severe hypoxia and/or respiratory arrest; severe hypothermia; cardiac arrest; comatose " +
				 "and not responding to pain)",
				 "Death occurred."});

        OptionEntry q23 = factory.createOptionEntry(
                "Highest level of care",
                "INTERVIEWER: USE ALL APPROPRIATE INFORMATION REGARDING TREATMENT THAT HAS " +
                        "BEEN GATHERED THROUGHOUT INTERVIEW TO CODE HIGHEST APPLICABLE NUMBER FROM LIST BELOW");
        doc.addEntry(q23);
        q23.setSection(treatmentSec);
        q23.setLabel("23");
        createOptions(factory, q23,
        		new String[]{"No medical treatment sought/required",
        					 "Went to emergency room or physician, had no medical treatment or assessment " +
        					 "and went home (e.g., talked to social worker or resident and left)",
        					 "Went directly to an in-patient psychiatric unit",
        					 "Medically treated while on in-patient psychiatric unit, without going to emergency room",
        					 "Went to emergency room or physician, was medically treated and went home",
        					 "Went to emergency room, was treated and admitted to psychiatry unit",
        					 "While on psychiatric unit, went to emergency room for medical treatment and then returned to psychiatric unit",
        					 "Admitted to medical unit, whether or not via emergency room, for observation (hours to overnight)",
        					 "Admitted to medical unit, whether or not via emergency room, for required treatment",
        					 "Admitted to intensive care unit, whether or not via emergency room or medical floor",
        					 "Mortuary"},
        		new int[]{0,1,2,3,4,5,6,7,8,9,10});

        OptionEntry q24 = factory.createOptionEntry(
                "RATE SUBJECT'S INTENT TO DIE",
                "INTERVIEWER: RATE SUBJECT'S INTENT TO DIE, I.E., THE SERIOUSNESS OR INTENSITY OF THE " +
                        "WISH TO TERMINATE HIS OR HER OWN LIFE. RATINGS SHOULD REFLECT YOUR BEST ESTIMATE BASED ON ALL INFORMATION.");
        doc.addEntry(q24);
        q24.setSection(treatmentSec);
        q24.setLabel("24");
        createOptions(factory, q24,
        		new String[]{"Obviously no intent",
        					 "Only minimal intent",
        					 "Definite intent but very ambivalent",
        					 "Serious intent",
        					 "Extreme intent (careful planning and every expectation of death)"},
        		new int[]{1,2,3,4,5});

        OptionEntry q25 = factory.createOptionEntry(
                "CATEGORIZE BEHAVIOR",
                "INTERVIEWER: BASED ON DEFINITION OF SASII ON APPENDIX, CATEGORIZE BEHAVIOR. " +
                        "CODING SHOULD REFLECT YOUR BEST JUDGMENT BASED ON ALL INFORMATION.");
        doc.addEntry(q25);
        q25.setSection(treatmentSec);
        q25.setLabel("25");
        createOptions(factory, q25,
        		new String[]{"Accidental self-harm, without undue risk taking and without unreasonable expectation of safety",
        					 "Accidental self-harm, with undue risk taking or with unreasonable expectation of safety",
        					 "Victim-precipitated self-harm, without intent to be harmed by others but with undue risk " +
        					 "taking or with unreasonable expectation of safety",
        					 "'Victim-precipitated' self-harm with intent to be harmed by other",
        					 "Intentional self-injury, but not a suicide attempt",
        					 "Ambivalent suicide attempt",
        					 "Suicide attempt with no ambivalence",
        					 "Suicide attempt that is a 'failed suicide', with continued life purely accidental and a near miracle",
        					 "OTHER, including absence of a behavior, which results in harm or illness (e.g., " +
        					 	"stopped taking important medicines such as insulin)"},
        		new int[]{1,2,3,4,5,6,7,8,9});

        Section experimentalSec = factory.createSection("Experimental Section", "Supplemental and experimental questions");
        doc.addSection(experimentalSec);
        SectionOccurrence experimentalSecOcc = factory.createSectionOccurrence("Experimental Section Occ");
        experimentalSec.addOccurrence(experimentalSecOcc);

        LongTextEntry q26 = factory.createLongTextEntry("Trigger, main precipitating event",
                "If you had to pick one thing that you think most triggered your " +
                        "self-injury/suicide attempt, what would you say it was? (PROBE FOR MAIN PRECIPITATING EVENT)");
        doc.addEntry(q26);
        q26.setSection(experimentalSec);
        q26.setLabel("26");

        OptionEntry q26a = factory.createOptionEntry("Trigger on day of attempt",
                "Did that happen on the day you injured yourself/attempted suicide?");
        doc.addEntry(q26a);
        q26a.setSection(experimentalSec);
        q26a.setLabel("26a");
        createOptions(factory, q26a, new String[]{"No", "Yes"}, new int[]{0,1});
        Option q26aNo = q26a.getOption(0);

        OptionEntry q26b = factory.createOptionEntry("Trigger right before felt urge",
                "IF NO: did that happen right before you felt the urge to injure yourself or attempt suicide?");
		doc.addEntry(q26b);
		q26b.setSection(experimentalSec);
		q26b.setLabel("26b");
		createOptions(factory, q26b, new String[]{"No", "Yes"}, new int[]{0,1});
		Option q26bNo = q26b.getOption(0);
		createOptionDependent(factory, q26aNo, q26b);

        LongTextEntry q26c = factory.createLongTextEntry("Trigger, what was different",
                "IF NO TO BOTH: In thinking about the trigger, ask yourself what was it about that particular " +
                        "day and that particular time that was different. What was the 'straw that broke the camel's back' " +
                        "that triggered your action or your final decision to act? What was different about the day you " +
                        "harmed yourself from a day or a week before or after? Why did you injure yourself on that " +
                        "particular day, as opposed to the day before or the week before? What specific events, thoughts, " +
                        "or feelings were most important?");
        doc.addEntry(q26c);
        q26c.setSection(experimentalSec);
        q26c.setLabel("26c");
        createOptionDependent(factory, q26bNo, q26c);

        NarrativeEntry q27 = factory.createNarrativeEntry("Q27",
        		"Did any of the events or experiences on this list happen to you in the 24 hours before your self injury/suicide "+
        		"attempt? Give Card D ASSESSOR CHECK ALL ITEMS LISTED BY CLIENT.");
        doc.addEntry(q27);
        q27.setSection(experimentalSec);
        q27.setLabel("27");

        NarrativeEntry q27a = factory.createNarrativeEntry("Q27a",
        		"Thinks that happened in the environment");
		doc.addEntry(q27a);
		q27a.setSection(experimentalSec);
		q27a.setStyle(NarrativeStyle.HEADER);

		OptionEntry q27_1 = factory.createOptionEntry("Argument or conflict with another person",
                "You had an argument or conflict with another person");
		doc.addEntry(q27_1);
		q27_1.setSection(experimentalSec);
		q27_1.setLabel("27.1");
		createOptions(factory, q27_1, new String[]{"Not mentioned", "Mentioned"}, new int[]{0,1});

		OptionEntry q27_2 = factory.createOptionEntry("Tried to spend time with someone but couldn't",
                "You tried to spend time with someone but couldn't");
		doc.addEntry(q27_2);
		q27_2.setSection(experimentalSec);
		q27_2.setLabel("27.2");
		createOptions(factory, q27_2, new String[]{"Not mentioned", "Mentioned"}, new int[]{0,1});

		OptionEntry q27_3 = factory.createOptionEntry("Someone was disappointed with you",
                "Someone was disappointed with you");
		doc.addEntry(q27_3);
		q27_3.setSection(experimentalSec);
		q27_3.setLabel("27.3");
		createOptions(factory, q27_3, new String[]{"Not mentioned", "Mentioned"}, new int[]{0,1});

		OptionEntry q27_4 = factory.createOptionEntry("Someone was angry with you",
                "Someone was angry with you, criticized you, or put you down");
		doc.addEntry(q27_4);
		q27_4.setSection(experimentalSec);
		q27_4.setLabel("27.4");
		createOptions(factory, q27_4, new String[]{"Not mentioned", "Mentioned"}, new int[]{0,1});

		OptionEntry q27_5 = factory.createOptionEntry("Someone let you down",
                "Someone let you down or broke a promise");
		doc.addEntry(q27_5);
		q27_5.setSection(experimentalSec);
		q27_5.setLabel("27.5");
		createOptions(factory, q27_5, new String[]{"Not mentioned", "Mentioned"}, new int[]{0,1});

		OptionEntry q27_6 = factory.createOptionEntry("Someone rejected you", "Someone rejected you");
		doc.addEntry(q27_6);
		q27_6.setSection(experimentalSec);
		q27_6.setLabel("27.6");
		createOptions(factory, q27_6, new String[]{"Not mentioned", "Mentioned"}, new int[]{0,1});

		OptionEntry q27_7 = factory.createOptionEntry("Lost someone important",
                "You lost someone important (even if temporary loss)");
		doc.addEntry(q27_7);
		q27_7.setSection(experimentalSec);
		q27_7.setLabel("27.7");
		createOptions(factory, q27_7, new String[]{"Not mentioned", "Mentioned"}, new int[]{0,1});

		OptionEntry q27_8 = factory.createOptionEntry("Therapist went out of town",
                "Therapist went out of town or took a break from having sessions");
		doc.addEntry(q27_8);
		q27_8.setSection(experimentalSec);
		q27_8.setLabel("27.8");
		createOptions(factory, q27_8, new String[]{"Not mentioned", "Mentioned"}, new int[]{0,1});

		OptionEntry q27_9 = factory.createOptionEntry("Were isolated or alone",
                "You were isolated or alone more than you wanted to be");
		doc.addEntry(q27_9);
		q27_9.setSection(experimentalSec);
		q27_9.setLabel("27.9");
		createOptions(factory, q27_9, new String[]{"Not mentioned", "Mentioned"}, new int[]{0,1});

		OptionEntry q27_10 = factory.createOptionEntry("Had financial problems", "You had financial problems");
		doc.addEntry(q27_10);
		q27_10.setSection(experimentalSec);
		q27_10.setLabel("27.10");
		createOptions(factory, q27_10, new String[]{"Not mentioned", "Mentioned"}, new int[]{0,1});

		OptionEntry q27_11 = factory.createOptionEntry("Lost a job", "You lost a job");
		doc.addEntry(q27_11);
		q27_11.setSection(experimentalSec);
		q27_11.setLabel("27.11");
		createOptions(factory, q27_11, new String[]{"Not mentioned", "Mentioned"}, new int[]{0,1});

		OptionEntry q27_12 = factory.createOptionEntry("Had health problems", "You had health problems or physical discomfort");
		doc.addEntry(q27_12);
		q27_12.setSection(experimentalSec);
		q27_12.setLabel("27.12");
		createOptions(factory, q27_12, new String[]{"Not mentioned", "Mentioned"}, new int[]{0,1});

		OptionEntry q27_13 = factory.createOptionEntry("Had a new demand", "You had a new demand");
		doc.addEntry(q27_13);
		q27_13.setSection(experimentalSec);
		q27_13.setLabel("27.13");
		createOptions(factory, q27_13, new String[]{"Not mentioned", "Mentioned"}, new int[]{0,1});
		q27_13.getOption(1).setTextEntryAllowed(true);

		OptionEntry q27_14 = factory.createOptionEntry("Tried to get something you wanted but couldn't",
                "You tried to get (or continue) something you wanted but couldn't");
		doc.addEntry(q27_14);
		q27_14.setSection(experimentalSec);
		q27_14.setLabel("27.14");
		createOptions(factory, q27_14, new String[]{"Not mentioned", "Mentioned"}, new int[]{0,1});

		OptionEntry q27_15 = factory.createOptionEntry("Heard of someone else attempting suicide",
                "You heard of someone else attempting suicide or harming themselves");
		doc.addEntry(q27_15);
		q27_15.setSection(experimentalSec);
		q27_15.setLabel("27.15");
		createOptions(factory, q27_15, new String[]{"Not mentioned", "Mentioned"}, new int[]{0,1});

		OptionEntry q27_16 = factory.createOptionEntry("Saw things could use to harm with",
                "You saw things that you could use to harm yourself or attempt suicide with");
		doc.addEntry(q27_16);
		q27_16.setSection(experimentalSec);
		q27_16.setLabel("27.16");
		createOptions(factory, q27_16, new String[]{"Not mentioned", "Mentioned"}, new int[]{0,1});

		OptionEntry q27_17 = factory.createOptionEntry("Talked to someone about sexual abuse",
                "You talked to someone about sexual abuse or rape");
		doc.addEntry(q27_17);
		q27_17.setSection(experimentalSec);
		q27_17.setLabel("27.17");
		createOptions(factory, q27_17, new String[]{"Not mentioned", "Mentioned"}, new int[]{0,1});

		OptionEntry q27_18 = factory.createOptionEntry("Talked with therapist about sexual abuse",
                "You talked with your therapist about sexual abuse or rape");
		doc.addEntry(q27_18);
		q27_18.setSection(experimentalSec);
		q27_18.setLabel("27.18");
		createOptions(factory, q27_18, new String[]{"Not mentioned", "Mentioned"}, new int[]{0,1});

		OptionEntry q27_19 = factory.createOptionEntry("Therapy session before",
                "You had a therapy session before your self-injury/suicide attempt (on the same day)");
		doc.addEntry(q27_19);
		q27_19.setSection(experimentalSec);
		q27_19.setLabel("27.19");
		createOptions(factory, q27_19, new String[]{"Not mentioned", "Mentioned"}, new int[]{0,1});

		OptionEntry q27_20 = factory.createOptionEntry("Therapy session later in day",
                "You had a therapy session scheduled for later in the day (after self-injury/suicide attempt)");
		doc.addEntry(q27_20);
		q27_20.setSection(experimentalSec);
		q27_20.setLabel("27.20");
		createOptions(factory, q27_20, new String[]{"Not mentioned", "Mentioned"}, new int[]{0,1});

		OptionEntry q27_21 = factory.createOptionEntry("Other negative events",
                "Other important negative events happened which could have triggered your suicide attempt/self-injury");
		doc.addEntry(q27_21);
		q27_21.setSection(experimentalSec);
		q27_21.setLabel("27.21");
		createOptions(factory, q27_21, new String[]{"Not mentioned", "Mentioned"}, new int[]{0,1});
        q27_21.getOption(1).setTextEntryAllowed(true);

        NarrativeEntry q27b = factory.createNarrativeEntry("Q27b",
				"Client's feelings");
		doc.addEntry(q27b);
		q27b.setSection(experimentalSec);
		q27b.setStyle(NarrativeStyle.HEADER);

		OptionEntry q27_22 = factory.createOptionEntry("Upset, miserable or distressed",
                "Upset, miserable or distressed");
		doc.addEntry(q27_22);
		q27_22.setSection(experimentalSec);
		q27_22.setLabel("27.22");
		createOptions(factory, q27_22, new String[]{"Not mentioned", "Mentioned"}, new int[]{0,1});

		OptionEntry q27_23 = factory.createOptionEntry("Out of control", "Out of control");
		doc.addEntry(q27_23);
		q27_23.setSection(experimentalSec);
		q27_23.setLabel("27.23");
		createOptions(factory, q27_23, new String[]{"Not mentioned", "Mentioned"}, new int[]{0,1});

		OptionEntry q27_24 = factory.createOptionEntry("Anxious, afraid, or panicked", "Anxious, afraid, or panicked");
		doc.addEntry(q27_24);
		q27_24.setSection(experimentalSec);
		q27_24.setLabel("27.24");
		createOptions(factory, q27_24, new String[]{"Not mentioned", "Mentioned"}, new int[]{0,1});

		OptionEntry q27_25 = factory.createOptionEntry("Overwhelmed", "Overwhelmed");
		doc.addEntry(q27_25);
		q27_25.setSection(experimentalSec);
		q27_25.setLabel("27.25");
		createOptions(factory, q27_25, new String[]{"Not mentioned", "Mentioned"}, new int[]{0,1});

		OptionEntry q27_26 = factory.createOptionEntry("Angry, frustrated or enraged unspecified",
                "Angry, frustrated or enraged unspecified");
		doc.addEntry(q27_26);
		q27_26.setSection(experimentalSec);
		q27_26.setLabel("27.26");
		createOptions(factory, q27_26, new String[]{"Not mentioned", "Mentioned"}, new int[]{0,1});

		OptionEntry q27_27 = factory.createOptionEntry("Angry, frustrated or enraged at someone else",
                "Angry, frustrated or enraged at someone else");
		doc.addEntry(q27_27);
		q27_27.setSection(experimentalSec);
		q27_27.setLabel("27.27");
		createOptions(factory, q27_27, new String[]{"Not mentioned", "Mentioned"}, new int[]{0,1});

		OptionEntry q27_28 = factory.createOptionEntry("Angry frustrated or enraged at yourself",
                "Angry frustrated or enraged at yourself");
		doc.addEntry(q27_28);
		q27_28.setSection(experimentalSec);
		q27_28.setLabel("27.28");
		createOptions(factory, q27_28, new String[]{"Not mentioned", "Mentioned"}, new int[]{0,1});

		OptionEntry q27_29 = factory.createOptionEntry("Self-hatred or shame, or thought you were 'bad'",
                "Self-hatred or shame, or thought you were 'bad'");
		doc.addEntry(q27_29);
		q27_29.setSection(experimentalSec);
		q27_29.setLabel("27.29");
		createOptions(factory, q27_29, new String[]{"Not mentioned", "Mentioned"}, new int[]{0,1});

		OptionEntry q27_30 = factory.createOptionEntry("Like you deserved to be punished or hurt", "Like you deserved to be punished or hurt");
		doc.addEntry(q27_30);
		q27_30.setSection(experimentalSec);
		q27_30.setLabel("27.30");
		createOptions(factory, q27_30, new String[]{"Not mentioned", "Mentioned"}, new int[]{0,1});

		OptionEntry q27_31 = factory.createOptionEntry("Like a failure or inferior", "Like a failure or inferior");
		doc.addEntry(q27_31);
		q27_31.setSection(experimentalSec);
		q27_31.setLabel("27.31");
		createOptions(factory, q27_31, new String[]{"Not mentioned", "Mentioned"}, new int[]{0,1});

		OptionEntry q27_32 = factory.createOptionEntry("Like a burden to others", "Like a burden to others");
		doc.addEntry(q27_32);
		q27_32.setSection(experimentalSec);
		q27_32.setLabel("27.32");
		createOptions(factory, q27_32, new String[]{"Not mentioned", "Mentioned"}, new int[]{0,1});

		OptionEntry q27_33 = factory.createOptionEntry("Felt bad about yourself", "Felt bad about yourself");
		doc.addEntry(q27_33);
		q27_33.setSection(experimentalSec);
		q27_33.setLabel("27.33");
		createOptions(factory, q27_33, new String[]{"Not mentioned", "Mentioned"}, new int[]{0,1});

		OptionEntry q27_34 = factory.createOptionEntry("Guilty", "Guilty");
		doc.addEntry(q27_34);
		q27_34.setSection(experimentalSec);
		q27_34.setLabel("27.34");
		createOptions(factory, q27_34, new String[]{"Not mentioned", "Mentioned"}, new int[]{0,1});

		OptionEntry q27_35 = factory.createOptionEntry("Sad or disappointed", "Sad or disappointed");
		doc.addEntry(q27_35);
		q27_35.setSection(experimentalSec);
		q27_35.setLabel("27.35");
		createOptions(factory, q27_35, new String[]{"Not mentioned", "Mentioned"}, new int[]{0,1});

		OptionEntry q27_36 = factory.createOptionEntry("Depressed", "Depressed");
		doc.addEntry(q27_36);
		q27_36.setSection(experimentalSec);
		q27_36.setLabel("27.36");
		createOptions(factory, q27_36, new String[]{"Not mentioned", "Mentioned"}, new int[]{0,1});

		OptionEntry q27_37 = factory.createOptionEntry("Tired or exhausted", "Tired or exhausted");
		doc.addEntry(q27_37);
		q27_37.setSection(experimentalSec);
		q27_37.setLabel("27.37");
		createOptions(factory, q27_37, new String[]{"Not mentioned", "Mentioned"}, new int[]{0,1});

		OptionEntry q27_38 = factory.createOptionEntry("Lonely, isolated, or abandoned", "Lonely, isolated, or abandoned");
		doc.addEntry(q27_38);
		q27_38.setSection(experimentalSec);
		q27_38.setLabel("27.38");
		createOptions(factory, q27_38, new String[]{"Not mentioned", "Mentioned"}, new int[]{0,1});

		OptionEntry q27_39 = factory.createOptionEntry("Trapped or helpless", "Trapped or helpless");
		doc.addEntry(q27_39);
		q27_39.setSection(experimentalSec);
		q27_39.setLabel("27.39");
		createOptions(factory, q27_39, new String[]{"Not mentioned", "Mentioned"}, new int[]{0,1});

		OptionEntry q27_40 = factory.createOptionEntry("Discouraged or hopeless", "Discouraged or hopeless");
		doc.addEntry(q27_40);
		q27_40.setSection(experimentalSec);
		q27_40.setLabel("27.40");
		createOptions(factory, q27_40, new String[]{"Not mentioned", "Mentioned"}, new int[]{0,1});

		OptionEntry q27_41 = factory.createOptionEntry("Confused", "Confused");
		doc.addEntry(q27_41);
		q27_41.setSection(experimentalSec);
		q27_41.setLabel("27.41");
		createOptions(factory, q27_41, new String[]{"Not mentioned", "Mentioned"}, new int[]{0,1});

		OptionEntry q27_42 = factory.createOptionEntry("Emotionally empty or numb", "Emotionally empty or numb");
		doc.addEntry(q27_42);
		q27_42.setSection(experimentalSec);
		q27_42.setLabel("27.42");
		createOptions(factory, q27_42, new String[]{"Not mentioned", "Mentioned"}, new int[]{0,1});

        NarrativeEntry q27c = factory.createNarrativeEntry("Q27c",
				"Client's thoughts");
		doc.addEntry(q27c);
		q27c.setSection(experimentalSec);
		q27c.setStyle(NarrativeStyle.HEADER);

		OptionEntry q27_43 = factory.createOptionEntry("About sexual abuse or rape", "About sexual abuse or rape");
		doc.addEntry(q27_43);
		q27_43.setSection(experimentalSec);
		q27_43.setLabel("27.43");
		createOptions(factory, q27_43, new String[]{"Not mentioned", "Mentioned"}, new int[]{0,1});

		OptionEntry q27_44 = factory.createOptionEntry("About physical abuse or assault", "About physical abuse or assault");
		doc.addEntry(q27_44);
		q27_44.setSection(experimentalSec);
		q27_44.setLabel("27.44");
		createOptions(factory, q27_44, new String[]{"Not mentioned", "Mentioned"}, new int[]{0,1});

		OptionEntry q27_45 = factory.createOptionEntry("Had flashbacks or nightmares", "Had flashbacks or nightmares");
		doc.addEntry(q27_45);
		q27_45.setSection(experimentalSec);
		q27_45.setLabel("27.45");
		createOptions(factory, q27_45, new String[]{"Not mentioned", "Mentioned"}, new int[]{0,1});

		NarrativeEntry q28 = factory.createNarrativeEntry("Q28",
				"During the 24 hours before your self-injury/suicide attempt/overdose, did you:");
		doc.addEntry(q28);
		q28.setSection(experimentalSec);
		q28.setLabel("28");

        OptionEntry q28_1 = factory.createOptionEntry("24 hours before - Drink alcohol",
                "Drink alcohol?");
		doc.addEntry(q28_1);
		q28_1.setSection(experimentalSec);
		q28_1.setLabel("28.1");
		createOptions(factory, q28_1, new String[]{"No", "Yes"}, new int[]{0,1});
		Option q28_1Yes = q28_1.getOption(1);

		NumericEntry q28_1b = factory.createNumericEntry("24 hours before - Drink - how much", "How much did you drink?");
		doc.addEntry(q28_1b);
		q28_1b.setSection(experimentalSec);
		q28_1b.setLabel("28.1b");
		q28_1b.addUnit(UnitWrapper.instance().getUnit("units"));
		q28_1b.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive"));
		createOptionDependent(factory, q28_1Yes, q28_1b);

		NumericEntry q28_1c = factory.createNumericEntry("24 hours before - Drink - hours", "How many hours were you drinking?");
		doc.addEntry(q28_1c);
		q28_1c.setSection(experimentalSec);
		q28_1c.setLabel("28.1c");
		q28_1c.addUnit(UnitWrapper.instance().getUnit("hrs"));
		q28_1c.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive"));
		createOptionDependent(factory, q28_1Yes, q28_1c);

		NumericEntry q28_1d = factory.createNumericEntry("24 hours before - Drink - hours before SI",
                "How long before your self-injury did you stop drinking?");
		doc.addEntry(q28_1d);
		q28_1d.setSection(experimentalSec);
		q28_1d.setLabel("28.1d");
		q28_1d.setDescription("CODE = '0' IF DRANK IMMEDIATELY PRIOR TO INJURY");
		q28_1d.addUnit(UnitWrapper.instance().getUnit("hrs"));
		q28_1d.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive"));
		createOptionDependent(factory, q28_1Yes, q28_1d);

        OptionEntry q28_2 = factory.createOptionEntry("24 hours before - take drugs",
                "Take illegal drugs or more than the prescribed amount of medications?");
		doc.addEntry(q28_2);
		q28_2.setSection(experimentalSec);
		q28_2.setLabel("28.2");
		createOptions(factory, q28_2, new String[]{"No", "Yes"}, new int[]{0,1});
		Option q28_2Yes = q28_2.getOption(1);

		IntegerEntry q28_2a = factory.createIntegerEntry("24 hours before - drugs - number",
                "How many different drugs did you use?");
		doc.addEntry(q28_2a);
		q28_2a.setSection(experimentalSec);
		q28_2a.setLabel("28.2a");
		q28_2a.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive Integer"));
		createOptionDependent(factory, q28_2Yes, q28_2a);

		TextEntry q28_2b = factory.createTextEntry("24 hours before - drugs - what #1",
                "What did you use?");
		doc.addEntry(q28_2b);
		q28_2b.setSection(experimentalSec);
		q28_2b.setLabel("28.2b");
		createOptionDependent(factory, q28_2Yes, q28_2b);

		TextEntry q28_2c = factory.createTextEntry("24 hours before - drugs - how much #1",
                "How much did you use?");
		doc.addEntry(q28_2c);
		q28_2c.setSection(experimentalSec);
		q28_2c.setLabel("28.2c");
		createOptionDependent(factory, q28_2Yes, q28_2c);

		NumericEntry q28_2d = factory.createNumericEntry("24 hours before - drugs - hours before SI #1",
                "How long before your self-injury did you take the drugs/medications?");
		doc.addEntry(q28_2d);
		q28_2d.setSection(experimentalSec);
		q28_2d.setLabel("28.2d");
		q28_2d.setDescription("CODE = '0' IF USED IMMEDIATELY PRIOR TO INJURY");
		q28_2d.addUnit(UnitWrapper.instance().getUnit("hrs"));
		q28_2d.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive"));
		createOptionDependent(factory, q28_2Yes, q28_2d);

		TextEntry q28_2e = factory.createTextEntry("24 hours before - drugs - what #2",
                "What did you use?");
		doc.addEntry(q28_2e);
		q28_2e.setSection(experimentalSec);
		q28_2e.setLabel("28.2e");
		createOptionDependent(factory, q28_2Yes, q28_2e);

		TextEntry q28_2f = factory.createTextEntry("24 hours before - drugs - how much #2",
                "How much did you use?");
		doc.addEntry(q28_2f);
		q28_2f.setSection(experimentalSec);
		q28_2f.setLabel("28.2f");
		createOptionDependent(factory, q28_2Yes, q28_2f);

		NumericEntry q28_2g = factory.createNumericEntry("24 hours before - drugs - hours before SI #2",
                "How long before your self-injury did you take the drugs/medications?");
		doc.addEntry(q28_2g);
		q28_2g.setSection(experimentalSec);
		q28_2g.setLabel("28.2g");
		q28_2g.setDescription("CODE = '0' IF USED IMMEDIATELY PRIOR TO INJURY");
		q28_2g.addUnit(UnitWrapper.instance().getUnit("hrs"));
		q28_2g.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive"));
		createOptionDependent(factory, q28_2Yes, q28_2g);

		TextEntry q28_2h = factory.createTextEntry("24 hours before - drugs - what #3",
                "What did you use?");
		doc.addEntry(q28_2h);
		q28_2h.setSection(experimentalSec);
		q28_2h.setLabel("28.2h");
		createOptionDependent(factory, q28_2Yes, q28_2h);

		TextEntry q28_2i = factory.createTextEntry("24 hours before - drugs - how much #3",
                "How much did you use?");
		doc.addEntry(q28_2i);
		q28_2i.setSection(experimentalSec);
		q28_2i.setLabel("28.2i");
		createOptionDependent(factory, q28_2Yes, q28_2i);

		NumericEntry q28_2j = factory.createNumericEntry("24 hours before - drugs - hours before SI #3",
                "How long before your self-injury did you take the drugs/medications?");
		doc.addEntry(q28_2j);
		q28_2j.setSection(experimentalSec);
		q28_2j.setLabel("28.2j");
		q28_2j.setDescription("CODE = '0' IF USED IMMEDIATELY PRIOR TO INJURY");
		q28_2j.addUnit(UnitWrapper.instance().getUnit("hrs"));
		q28_2j.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive"));
		createOptionDependent(factory, q28_2Yes, q28_2j);

		LongTextEntry q28_2k = factory.createLongTextEntry("24 hours before - drugs - other",
                "List any additional ones used");
		doc.addEntry(q28_2k);
		q28_2k.setSection(experimentalSec);
		q28_2k.setLabel("28.2k");
		createOptionDependent(factory, q28_2Yes, q28_2k);

        OptionEntry q28_3 = factory.createOptionEntry("24 hours before - sleep worse",
                "Sleep worse than you usually do?");
		doc.addEntry(q28_3);
		q28_3.setSection(experimentalSec);
		q28_3.setLabel("28.3");
		createOptions(factory, q28_3, new String[]{"No", "Yes"}, new int[]{0,1});

        OptionEntry q28_4 = factory.createOptionEntry("24 hours before - ask for help",
                "Ask someone for help?");
		doc.addEntry(q28_4);
		q28_4.setSection(experimentalSec);
		q28_4.setLabel("28.4");
		createOptions(factory, q28_4, new String[]{"No", "Yes"}, new int[]{0,1});
		Option q28_4Yes = q28_4.getOption(1);

        OptionEntry q28_4b = factory.createOptionEntry("24 hours before - get help",
                "Ask someone for help?");
		doc.addEntry(q28_4b);
		q28_4b.setSection(experimentalSec);
		q28_4b.setLabel("28.4b");
		createOptions(factory, q28_4b, new String[]{"No", "Yes"}, new int[]{0,1});
		createOptionDependent(factory, q28_4Yes, q28_4b);

        OptionEntry q28_5 = factory.createOptionEntry("24 hours before - binge eating",
                "Eat a lot more food that you usually do (i.e., binge eating)?");
		doc.addEntry(q28_5);
		q28_5.setSection(experimentalSec);
		q28_5.setLabel("28.5");
		createOptions(factory, q28_5, new String[]{"No", "Yes"}, new int[]{0,1});

        OptionEntry q28_6 = factory.createOptionEntry("24 hours before - illegal behavior",
                "Engage in illegal behavior (other than using drugs)?");
		doc.addEntry(q28_6);
		q28_6.setSection(experimentalSec);
		q28_6.setLabel("28.6");
		createOptions(factory, q28_6, new String[]{"No", "Yes"}, new int[]{0,1});

        OptionEntry q29 = factory.createOptionEntry("Feeling disconnected prior to attempt",
                "Were you feeling disconnected from your feelings or as if you were unreal " +
                        "during or prior to your self-injury/suicide attempt/overdose?");
		doc.addEntry(q29);
		q29.setSection(experimentalSec);
		q29.setLabel("29");
		createOptions(factory, q29, new String[]{"No", "Yes"}, new int[]{0,1});

        OptionEntry q30 = factory.createOptionEntry("Disconnected begin after decided to attempt",
                "Did this state of being disconnected or unreal begin after you decided to self-injury/suicide attempt/overdose?");
		doc.addEntry(q30);
		q30.setSection(experimentalSec);
		q30.setLabel("30");
		createOptions(factory, q30, new String[]{"No, not began before", "Maybe", "Yes, began after", "No dissociation"}, new int[]{0,1,2,-8});

        OptionEntry q31 = factory.createOptionEntry("Hearing voices prior to attempt",
                "Were you hearing voices that were telling you to harm yourself during or " +
                        "prior to your self-injury/suicide attempt/overdose?");
		doc.addEntry(q31);
		q31.setSection(experimentalSec);
		q31.setLabel("31");
		createOptions(factory, q31, new String[]{"No", "Yes"}, new int[]{0,1});

        OptionEntry q32 = factory.createOptionEntry("Feel physical pain during attempt",
                "Did you feel physical pain during your self-injury/suicide attempt/overdose?");
		doc.addEntry(q32);
		q32.setSection(experimentalSec);
		q32.setLabel("32");
		createOptions(factory, q32, new String[]{"No", "Yes"}, new int[]{0,1});
		Option q32Yes = q32.getOption(1);

		NumericEntry q32a = factory.createNumericEntry("How much pain during attempt",
                "IF YES: How much pain did you feel on a scale of 1 to 5 with 1=little " +
                        "pain but mostly none and 5=extreme pain. (Score 0=none)");
		doc.addEntry(q32a);
		q32a.setSection(experimentalSec);
		q32a.setLabel("32a");
		q32a.addValidationRule(ValidationRulesWrapper.instance().getRule("ZeroToFive"));
		createOptionDependent(factory, q32Yes, q32a);

		NarrativeEntry q33 = factory.createNarrativeEntry("Q33",
				"Following your self-injury/suicide attempt/overdose were you taken to any of " +
				"these places or did you turn to any of these places or people for help? (Give Card " +
				"C and code in the order that Subject contacted each)");
		doc.addEntry(q33);
		q33.setSection(experimentalSec);
		q33.setLabel("33");

		OptionEntry q33_1 = factory.createOptionEntry("Relative", "Relative");
		doc.addEntry(q33_1);
		q33_1.setSection(experimentalSec);
		q33_1.setLabel("33.1");
		createOptions(factory, q33_1,
				new String[]{"Not contacted", "Contacted first", "Contacted second", "Contacted third",
							 "Contacted fourth", "Contacted fifth", "Contacted sixth", "Contacted seventh",
							 "Contacted eighth", "Contacted ninth", "Contacted tenth"},
				new int[]{0,1,2,3,4,5,6,7,8,9,10});

		OptionEntry q33_2 = factory.createOptionEntry("Friend", "Friend");
		doc.addEntry(q33_2);
		q33_2.setSection(experimentalSec);
		q33_2.setLabel("33.2");
		createOptions(factory, q33_2,
				new String[]{"Not contacted", "Contacted first", "Contacted second", "Contacted third",
							 "Contacted fourth", "Contacted fifth", "Contacted sixth", "Contacted seventh",
							 "Contacted eighth", "Contacted ninth", "Contacted tenth"},
				new int[]{0,1,2,3,4,5,6,7,8,9,10});

		OptionEntry q33_3 = factory.createOptionEntry("Supervisor/teacher", "Supervisor/teacher");
		doc.addEntry(q33_3);
		q33_3.setSection(experimentalSec);
		q33_3.setLabel("33.3");
		createOptions(factory, q33_3,
				new String[]{"Not contacted", "Contacted first", "Contacted second", "Contacted third",
							 "Contacted fourth", "Contacted fifth", "Contacted sixth", "Contacted seventh",
							 "Contacted eighth", "Contacted ninth", "Contacted tenth"},
				new int[]{0,1,2,3,4,5,6,7,8,9,10});

		OptionEntry q33_4 = factory.createOptionEntry("Co-worker/other student", "Co-worker/other student");
		doc.addEntry(q33_4);
		q33_4.setSection(experimentalSec);
		q33_4.setLabel("33.4");
		createOptions(factory, q33_4,
				new String[]{"Not contacted", "Contacted first", "Contacted second", "Contacted third",
							 "Contacted fourth", "Contacted fifth", "Contacted sixth", "Contacted seventh",
							 "Contacted eighth", "Contacted ninth", "Contacted tenth"},
				new int[]{0,1,2,3,4,5,6,7,8,9,10});

		OptionEntry q33_5 = factory.createOptionEntry("Stranger, neighbor", "Stranger, neighbor");
		doc.addEntry(q33_5);
		q33_5.setSection(experimentalSec);
		q33_5.setLabel("33.5");
		createOptions(factory, q33_5,
				new String[]{"Not contacted", "Contacted first", "Contacted second", "Contacted third",
							 "Contacted fourth", "Contacted fifth", "Contacted sixth", "Contacted seventh",
							 "Contacted eighth", "Contacted ninth", "Contacted tenth"},
				new int[]{0,1,2,3,4,5,6,7,8,9,10});

		OptionEntry q33_6 = factory.createOptionEntry("Crisis service/after hours team. (By phone)", "Crisis service/after hours team. (By phone)");
		doc.addEntry(q33_6);
		q33_6.setSection(experimentalSec);
		q33_6.setLabel("33.6");
		createOptions(factory, q33_6,
				new String[]{"Not contacted", "Contacted first", "Contacted second", "Contacted third",
							 "Contacted fourth", "Contacted fifth", "Contacted sixth", "Contacted seventh",
							 "Contacted eighth", "Contacted ninth", "Contacted tenth"},
				new int[]{0,1,2,3,4,5,6,7,8,9,10});

		OptionEntry q33_7 = factory.createOptionEntry("Psychotherapist (By phone)", "Psychotherapist (By phone)");
		doc.addEntry(q33_7);
		q33_7.setSection(experimentalSec);
		q33_7.setLabel("33.7");
		createOptions(factory, q33_7,
				new String[]{"Not contacted", "Contacted first", "Contacted second", "Contacted third",
							 "Contacted fourth", "Contacted fifth", "Contacted sixth", "Contacted seventh",
							 "Contacted eighth", "Contacted ninth", "Contacted tenth"},
				new int[]{0,1,2,3,4,5,6,7,8,9,10});

		OptionEntry q33_8 = factory.createOptionEntry("Physician/nurse (By phone)", "Physician/nurse (By phone)");
		doc.addEntry(q33_8);
		q33_8.setSection(experimentalSec);
		q33_8.setLabel("33.8");
		createOptions(factory, q33_8,
				new String[]{"Not contacted", "Contacted first", "Contacted second", "Contacted third",
							 "Contacted fourth", "Contacted fifth", "Contacted sixth", "Contacted seventh",
							 "Contacted eighth", "Contacted ninth", "Contacted tenth"},
				new int[]{0,1,2,3,4,5,6,7,8,9,10});

		OptionEntry q33_9 = factory.createOptionEntry("Psychotherapist (Extra visit)", "Psychotherapist (Extra visit)");
		doc.addEntry(q33_9);
		q33_9.setSection(experimentalSec);
		q33_9.setLabel("33.9");
		createOptions(factory, q33_9,
				new String[]{"Not contacted", "Contacted first", "Contacted second", "Contacted third",
							 "Contacted fourth", "Contacted fifth", "Contacted sixth", "Contacted seventh",
							 "Contacted eighth", "Contacted ninth", "Contacted tenth"},
				new int[]{0,1,2,3,4,5,6,7,8,9,10});

		OptionEntry q33_10 = factory.createOptionEntry("Other", "Other");
		doc.addEntry(q33_10);
		q33_10.setSection(experimentalSec);
		q33_10.setLabel("33.10");
		createOptions(factory, q33_10,
				new String[]{"Not contacted", "Contacted first", "Contacted second", "Contacted third",
							 "Contacted fourth", "Contacted fifth", "Contacted sixth", "Contacted seventh",
							 "Contacted eighth", "Contacted ninth", "Contacted tenth"},
				new int[]{0,1,2,3,4,5,6,7,8,9,10});
		Option q33_10_1 = q33_10.getOption(1);
		Option q33_10_2 = q33_10.getOption(2);
		Option q33_10_3 = q33_10.getOption(3);
		Option q33_10_4 = q33_10.getOption(4);
		Option q33_10_5 = q33_10.getOption(5);
		Option q33_10_6 = q33_10.getOption(6);
		Option q33_10_7 = q33_10.getOption(7);
		Option q33_10_8 = q33_10.getOption(8);
		Option q33_10_9 = q33_10.getOption(9);
		Option q33_10_10 = q33_10.getOption(10);

		TextEntry q33_10a = factory.createTextEntry("Other - specify", "Other - specify");
		doc.addEntry(q33_10a);
		q33_10a.setSection(experimentalSec);
		q33_10a.setLabel("33.10a");
		createOptionDependent(factory, q33_10_1, q33_10a);
		createOptionDependent(factory, q33_10_2, q33_10a);
		createOptionDependent(factory, q33_10_3, q33_10a);
		createOptionDependent(factory, q33_10_4, q33_10a);
		createOptionDependent(factory, q33_10_5, q33_10a);
		createOptionDependent(factory, q33_10_6, q33_10a);
		createOptionDependent(factory, q33_10_7, q33_10a);
		createOptionDependent(factory, q33_10_8, q33_10a);
		createOptionDependent(factory, q33_10_9, q33_10a);
		createOptionDependent(factory, q33_10_10, q33_10a);

		NarrativeEntry q34a = factory.createNarrativeEntry("Q34",
				"How helpful were each of the people/agencies with whom you had contact? Please " +
				"rate on a scale of 1 to 5 with 1 = they made things worse to 5 = they made things much better.");
		doc.addEntry(q34a);
		q34a.setSection(experimentalSec);
		q34a.setLabel("34a");

		NumericEntry q34a_1 = factory.createNumericEntry("Relative - how helpful", "Relative");
		doc.addEntry(q34a_1);
		q34a_1.setSection(experimentalSec);
		q34a_1.setLabel("34a.1");
		q34a_1.addValidationRule(ValidationRulesWrapper.instance().getRule("OneToFiveAndMinusEight"));

		NumericEntry q34a_2 = factory.createNumericEntry("Friend - how helpful", "Friend");
		doc.addEntry(q34a_2);
		q34a_2.setSection(experimentalSec);
		q34a_2.setLabel("34a.2");
		q34a_2.addValidationRule(ValidationRulesWrapper.instance().getRule("OneToFiveAndMinusEight"));

		NumericEntry q34a_3 = factory.createNumericEntry("Supervisor/teacher - how helpful", "Supervisor/teacher");
		doc.addEntry(q34a_3);
		q34a_3.setSection(experimentalSec);
		q34a_3.setLabel("34a.3");
		q34a_3.addValidationRule(ValidationRulesWrapper.instance().getRule("OneToFiveAndMinusEight"));

		NumericEntry q34a_4 = factory.createNumericEntry("Co-worker/other student - how helpful", "Co-worker/other student");
		doc.addEntry(q34a_4);
		q34a_4.setSection(experimentalSec);
		q34a_4.setLabel("34a.4");
		q34a_4.addValidationRule(ValidationRulesWrapper.instance().getRule("OneToFiveAndMinusEight"));

		NumericEntry q34a_5 = factory.createNumericEntry("Stranger, neighbor - how helpful", "Stranger, neighbor");
		doc.addEntry(q34a_5);
		q34a_5.setSection(experimentalSec);
		q34a_5.setLabel("34a.5");
		q34a_5.addValidationRule(ValidationRulesWrapper.instance().getRule("OneToFiveAndMinusEight"));

		NumericEntry q34a_6 = factory.createNumericEntry("Crisis service/after hours team. (By phone) - how helpful",
                "Crisis service/after hours team. (By phone)");
		doc.addEntry(q34a_6);
		q34a_6.setSection(experimentalSec);
		q34a_6.setLabel("34a.6");
		q34a_6.addValidationRule(ValidationRulesWrapper.instance().getRule("OneToFiveAndMinusEight"));

		NumericEntry q34a_7 = factory.createNumericEntry("Psychotherapist (By phone) - how helpful", "Psychotherapist (By phone)");
		doc.addEntry(q34a_7);
		q34a_7.setSection(experimentalSec);
		q34a_7.setLabel("34a.7");
		q34a_7.addValidationRule(ValidationRulesWrapper.instance().getRule("OneToFiveAndMinusEight"));

		NumericEntry q34a_8 = factory.createNumericEntry("Physician/nurse (By phone) - how helpful", "Physician/nurse (By phone)");
		doc.addEntry(q34a_8);
		q34a_8.setSection(experimentalSec);
		q34a_8.setLabel("34a.8");
		q34a_8.addValidationRule(ValidationRulesWrapper.instance().getRule("OneToFiveAndMinusEight"));

		NumericEntry q34a_9 = factory.createNumericEntry("Psychotherapist (Extra visit) - how helpful", "Psychotherapist (Extra visit)");
		doc.addEntry(q34a_9);
		q34a_9.setSection(experimentalSec);
		q34a_9.setLabel("34a.9");
		q34a_9.addValidationRule(ValidationRulesWrapper.instance().getRule("OneToFiveAndMinusEight"));

		NumericEntry q34a_10 = factory.createNumericEntry("Other - how helpful", "Other");
		doc.addEntry(q34a_10);
		q34a_10.setSection(experimentalSec);
		q34a_10.setLabel("34a.10");
		q34a_10.addValidationRule(ValidationRulesWrapper.instance().getRule("OneToFiveAndMinusEight"));

		NarrativeEntry q34b = factory.createNarrativeEntry("Q34",
				"For those items answered with a number in question #21, ask the above question. " +
				"Code = '-8' if person/agency was coded '0' in #21");
		doc.addEntry(q34b);
		q34b.setSection(experimentalSec);
		q34b.setLabel("34b");

		NumericEntry q34b_1 = factory.createNumericEntry("Physician/nurse (Visit) - how helpful", "Physician/nurse (Visit)");
		doc.addEntry(q34b_1);
		q34b_1.setSection(experimentalSec);
		q34b_1.setLabel("34b.1");
		q34b_1.addValidationRule(ValidationRulesWrapper.instance().getRule("OneToFiveAndMinusEight"));

		NumericEntry q34b_2 = factory.createNumericEntry("Crisis outreach - how helpful", "Crisis outreach/after hours team/mental health professional (In person visit)");
		doc.addEntry(q34b_2);
		q34b_2.setSection(experimentalSec);
		q34b_2.setLabel("34b.2");
		q34b_2.addValidationRule(ValidationRulesWrapper.instance().getRule("OneToFiveAndMinusEight"));

		NumericEntry q34b_3 = factory.createNumericEntry("Police/wellness check - how helpful", "Police/wellness check (At home or other residence)");
		doc.addEntry(q34b_3);
		q34b_3.setSection(experimentalSec);
		q34b_3.setLabel("34b.3");
		q34b_3.addValidationRule(ValidationRulesWrapper.instance().getRule("OneToFiveAndMinusEight"));

		NumericEntry q34b_4 = factory.createNumericEntry("Paramedics/ambulance/aid car - how helpful", "Paramedics/ambulance/aid car (At home or other residence)");
		doc.addEntry(q34b_4);
		q34b_4.setSection(experimentalSec);
		q34b_4.setLabel("34b.4");
		q34b_4.addValidationRule(ValidationRulesWrapper.instance().getRule("OneToFiveAndMinusEight"));

		NumericEntry q34b_5 = factory.createNumericEntry("Hospital emergency room - how helpful", "Hospital emergency room");
		doc.addEntry(q34b_5);
		q34b_5.setSection(experimentalSec);
		q34b_5.setLabel("34b.5");
		q34b_5.addValidationRule(ValidationRulesWrapper.instance().getRule("OneToFiveAndMinusEight"));

		NumericEntry q34b_6 = factory.createNumericEntry("Inpatient, psychiatric unit - how helpful", "Inpatient, psychiatric unit");
		doc.addEntry(q34b_6);
		q34b_6.setSection(experimentalSec);
		q34b_6.setLabel("34b.6");
		q34b_6.addValidationRule(ValidationRulesWrapper.instance().getRule("OneToFiveAndMinusEight"));

		NumericEntry q34b_7 = factory.createNumericEntry("Hospital medical floor - how helpful", "Hospital medical floor");
		doc.addEntry(q34b_7);
		q34b_7.setSection(experimentalSec);
		q34b_7.setLabel("34b.7");
		q34b_7.addValidationRule(ValidationRulesWrapper.instance().getRule("OneToFiveAndMinusEight"));

		NumericEntry q34b_8 = factory.createNumericEntry("Intensive care - how helpful", "Intensive care");
		doc.addEntry(q34b_8);
		q34b_8.setSection(experimentalSec);
		q34b_8.setLabel("34b.8");
		q34b_8.addValidationRule(ValidationRulesWrapper.instance().getRule("OneToFiveAndMinusEight"));

		OptionEntry q35 = factory.createOptionEntry("Consequences on job",
                "Did your self-injury/suicide attempt/overdose have any of the following consequences on your job?");
		doc.addEntry(q35);
		q35.setSection(experimentalSec);
		q35.setLabel("35");
		createOptions(factory, q35, new String[]{"Strongly improved my job performance by causing me to work more, be more focused, etc.",
												 "Slightly improved my job performance",
												 "No effect or overall neutral effect",
												 "Impaired my job performance",
												 "Reprimanded/demoted",
												 "Lost job",
												 "Subject unemployed"},
									new int[]{1,2,3,4,5,6,-8});

		NumericEntry q36 = factory.createNumericEntry("Work days missed",
                "How many work days did you miss because of your self-injury? (CODE = '-8' IF SUBJECT UNEMPLOYED)");
		doc.addEntry(q36);
		q36.setSection(experimentalSec);
		q36.setLabel("36");
		q36.addValidationRule(ValidationRulesWrapper.instance().getRule("PositiveOrMinusEight"));

		OptionEntry q37 = factory.createOptionEntry("Consequences on school work",
                "Did your self-injury/suicide attempt/overdose have any of the following consequences on " +
                        "your school work? (CODE = '-8' IF SUBJECT NOT ENROLLED)");
		doc.addEntry(q37);
		q37.setSection(experimentalSec);
		q37.setLabel("37");
		createOptions(factory, q37, new String[]{"Strongly improved my school performance by causing me to study more, be more focused, etc.",
												 "Slightly improved my school performance",
												 "No effect or overall neutral effect",
												 "Impaired my school performance",
												 "Dropped a class(es) / Failed a class(es)",
												 "Expelled",
												 "Subject not enrolled"},
									new int[]{1,2,3,4,5,6,-8});

		NumericEntry q38 = factory.createNumericEntry("School days missed",
                "How many days did you miss because of your self-injury? (CODE = '-8' IF SUBJECT NOT ENROLLED)");
		doc.addEntry(q38);
		q38.setSection(experimentalSec);
		q38.setLabel("38");
		q38.addValidationRule(ValidationRulesWrapper.instance().getRule("PositiveOrMinusEight"));

		OptionEntry q39 = factory.createOptionEntry("Consequences on housing situation",
                "Did your self-injury/suicide attempt/overdose have any of the following consequences on your housing situation?");
		doc.addEntry(q39);
		q39.setSection(experimentalSec);
		q39.setLabel("39");
		createOptions(factory, q39, new String[]{"Strongly improved living situation by making roommates/family with whom you live more understanding, reducing housework, etc.",
												 "Slightly improved living situation",
												 "No effect or overall neutral effect",
												 "Housemates/neighbors upset / Restrictions placed on me",
												 "Neighbors called the authorities to complain / Threatened with an eviction",
												 "Evicted"},
									new int[]{1,2,3,4,6,7});

		OptionEntry q40 = factory.createOptionEntry("Consequences on financial situation",
                "Did your self-injury/suicide attempt/overdose have any of the following consequences on your financial situation?");
		doc.addEntry(q40);
		q40.setSection(experimentalSec);
		q40.setLabel("40");
		createOptions(factory, q40, new String[]{"Significantly improved my financial situation by causing others to give me money, reduce my debt, etc.",
												 "Slightly improved my financial situation",
												 "No effect or overall neutral effect",
												 "Costs paid for by insurance or other third party or paid less than $100 out of pocket",
												 "Paid costs out of pocket of more than $100",
												 "Bankrupt"},
									new int[]{1,2,3,4,5,6});

		OptionEntry q41 = factory.createOptionEntry("Consequences on relationships",
                "Did your self-injury/suicide attempt/overdose have any of the following consequences " +
                        "on your relationships with people that you care about?");
		doc.addEntry(q41);
		q41.setSection(experimentalSec);
		q41.setLabel("41");
		createOptions(factory, q41, new String[]{"Much closer, much more contact",
												 "Somewhat closer or somewhat more contact",
												 "No effect or overall neutral effect",
												 "Somewhat more distant or strained or somewhat less contact",
												 "More distant or strained or less contact",
												 "Relationship(s) ended"},
									new int[]{1,2,3,4,5,6});

		NarrativeEntry q42 = factory.createNarrativeEntry("Q42",
				"Did any of the events or experiences on this list happen immediately following your selfharming/"+
				"suicidal incident? Give Card E. If so please give a rating for each question on the following "+
				"1-5 scale: 1 = 'Not true at all/ did not happen at all,' to 5 = 'Very true/ happened a lot'.");
		doc.addEntry(q42);
		q42.setSection(experimentalSec);
		q42.setLabel("42");

		NumericEntry q42_1 = factory.createNumericEntry("", "Bad feelings stopped");
		doc.addEntry(q42_1);
		q42_1.setSection(experimentalSec);
		q42_1.setLabel("42.1");
		q42_1.addValidationRule(ValidationRulesWrapper.instance().getRule("OneToFive"));

		NumericEntry q42_2 = factory.createNumericEntry("", "Others understood how desperate you are/were");
		doc.addEntry(q42_2);
		q42_2.setSection(experimentalSec);
		q42_2.setLabel("42.2");
		q42_2.addValidationRule(ValidationRulesWrapper.instance().getRule("OneToFive"));

		NumericEntry q42_3 = factory.createNumericEntry("You got help", "You got help");
		doc.addEntry(q42_3);
		q42_3.setSection(experimentalSec);
		q42_3.setLabel("42.3");
		q42_3.addValidationRule(ValidationRulesWrapper.instance().getRule("OneToFive"));

		NumericEntry q42_4 = factory.createNumericEntry("You gained admission into a hospital or treatment program",
                "You gained admission into a hospital or treatment program");
		doc.addEntry(q42_4);
		q42_4.setSection(experimentalSec);
		q42_4.setLabel("42.4");
		q42_4.addValidationRule(ValidationRulesWrapper.instance().getRule("OneToFive"));

		NumericEntry q42_5 = factory.createNumericEntry("You felt something, even if it was pain", "You felt something, even if it was pain");
		doc.addEntry(q42_5);
		q42_5.setSection(experimentalSec);
		q42_5.setLabel("42.5");
		q42_5.addValidationRule(ValidationRulesWrapper.instance().getRule("OneToFive"));

		NumericEntry q42_6 = factory.createNumericEntry("You felt punished or succeeded in punishing yourself", "You felt punished or succeeded in punishing yourself");
		doc.addEntry(q42_6);
		q42_6.setSection(experimentalSec);
		q42_6.setLabel("42.6");
		q42_6.addValidationRule(ValidationRulesWrapper.instance().getRule("OneToFive"));

		NumericEntry q42_7 = factory.createNumericEntry("You got a vacation from having to try so hard", "You got a vacation from having to try so hard");
		doc.addEntry(q42_7);
		q42_7.setSection(experimentalSec);
		q42_7.setLabel("42.7");
		q42_7.addValidationRule(ValidationRulesWrapper.instance().getRule("OneToFive"));

		NumericEntry q42_8 = factory.createNumericEntry("You got out of doing something", "You got out of doing something");
		doc.addEntry(q42_8);
		q42_8.setSection(experimentalSec);
		q42_8.setLabel("42.8");
		q42_8.addValidationRule(ValidationRulesWrapper.instance().getRule("OneToFive"));

		NumericEntry q42_9 = factory.createNumericEntry("You shocked or impressed others", "You shocked or impressed others");
		doc.addEntry(q42_9);
		q42_9.setSection(experimentalSec);
		q42_9.setLabel("42.9");
		q42_9.addValidationRule(ValidationRulesWrapper.instance().getRule("OneToFive"));

		NumericEntry q42_10 = factory.createNumericEntry("You proved to yourself that things really were bad", "You proved to yourself that things really were bad");
		doc.addEntry(q42_10);
		q42_10.setSection(experimentalSec);
		q42_10.setLabel("42.10");
		q42_10.addValidationRule(ValidationRulesWrapper.instance().getRule("OneToFive"));

		NumericEntry q42_11 = factory.createNumericEntry("It gave you something, anything to do", "It gave you something, anything to do");
		doc.addEntry(q42_11);
		q42_11.setSection(experimentalSec);
		q42_11.setLabel("42.11");
		q42_11.addValidationRule(ValidationRulesWrapper.instance().getRule("OneToFive"));

		NumericEntry q42_12 = factory.createNumericEntry("Other people treated you better", "Other people treated you better");
		doc.addEntry(q42_12);
		q42_12.setSection(experimentalSec);
		q42_12.setLabel("42.12");
		q42_12.addValidationRule(ValidationRulesWrapper.instance().getRule("OneToFive"));

		NumericEntry q42_13 = factory.createNumericEntry("You got back at or hurt someone", "You got back at or hurt someone");
		doc.addEntry(q42_13);
		q42_13.setSection(experimentalSec);
		q42_13.setLabel("42.13");
		q42_13.addValidationRule(ValidationRulesWrapper.instance().getRule("OneToFive"));

		NumericEntry q42_14 = factory.createNumericEntry("Other people were better off than before you harmed yourself", "Other people were better off than before you harmed yourself");
		doc.addEntry(q42_14);
		q42_14.setSection(experimentalSec);
		q42_14.setLabel("42.14");
		q42_14.addValidationRule(ValidationRulesWrapper.instance().getRule("OneToFive"));

		NumericEntry q42_15 = factory.createNumericEntry("You got away or escaped", "You got away or escaped");
		doc.addEntry(q42_15);
		q42_15.setSection(experimentalSec);
		q42_15.setLabel("42.15");
		q42_15.addValidationRule(ValidationRulesWrapper.instance().getRule("OneToFive"));

		NumericEntry q42_16 = factory.createNumericEntry("You stopped feeling numb or dead", "You stopped feeling numb or dead");
		doc.addEntry(q42_16);
		q42_16.setSection(experimentalSec);
		q42_16.setLabel("42.16");
		q42_16.addValidationRule(ValidationRulesWrapper.instance().getRule("OneToFive"));

		NumericEntry q42_17 = factory.createNumericEntry("You prevented yourself from being hurt in a worse way", "You prevented yourself from being hurt in a worse way");
		doc.addEntry(q42_17);
		q42_17.setSection(experimentalSec);
		q42_17.setLabel("42.17");
		q42_17.addValidationRule(ValidationRulesWrapper.instance().getRule("OneToFive"));

		NumericEntry q42_18 = factory.createNumericEntry("Feelings of anger, frustration, or rage stopped", "Feelings of anger, frustration, or rage stopped");
		doc.addEntry(q42_18);
		q42_18.setSection(experimentalSec);
		q42_18.setLabel("42.18");
		q42_18.addValidationRule(ValidationRulesWrapper.instance().getRule("OneToFive"));

		NumericEntry q42_19 = factory.createNumericEntry("Others realized how wrong they are/were", "Others realized how wrong they are/were");
		doc.addEntry(q42_19);
		q42_19.setSection(experimentalSec);
		q42_19.setLabel("42.19");
		q42_19.addValidationRule(ValidationRulesWrapper.instance().getRule("OneToFive"));

		NumericEntry q42_20 = factory.createNumericEntry("Feelings of anxiety or terror stopped", "Feelings of anxiety or terror stopped");
		doc.addEntry(q42_20);
		q42_20.setSection(experimentalSec);
		q42_20.setLabel("42.20");
		q42_20.addValidationRule(ValidationRulesWrapper.instance().getRule("OneToFive"));

		NumericEntry q42_21 = factory.createNumericEntry("You were distracted from other problems", "You were distracted from other problems");
		doc.addEntry(q42_21);
		q42_21.setSection(experimentalSec);
		q42_21.setLabel("42.21");
		q42_21.addValidationRule(ValidationRulesWrapper.instance().getRule("OneToFive"));

		NumericEntry q42_22 = factory.createNumericEntry("Feelings of aloneness, emptiness, or isolation stopped", "Feelings of aloneness, emptiness, or isolation stopped");
		doc.addEntry(q42_22);
		q42_22.setSection(experimentalSec);
		q42_22.setLabel("42.22");
		q42_22.addValidationRule(ValidationRulesWrapper.instance().getRule("OneToFive"));

		NumericEntry q42_23 = factory.createNumericEntry("Feelings of self-hatred/shame stopped", "Feelings of self-hatred/shame stopped");
		doc.addEntry(q42_23);
		q42_23.setSection(experimentalSec);
		q42_23.setLabel("42.23");
		q42_23.addValidationRule(ValidationRulesWrapper.instance().getRule("OneToFive"));

		NumericEntry q42_24 = factory.createNumericEntry("Your (self-injury/suicide attempt/overdose) expressed your anger or frustration", "Your (self-injury/suicide attempt/overdose) expressed your anger or frustration");
		doc.addEntry(q42_24);
		q42_24.setSection(experimentalSec);
		q42_24.setLabel("42.24");
		q42_24.addValidationRule(ValidationRulesWrapper.instance().getRule("OneToFive"));

		NumericEntry q42_25 = factory.createNumericEntry("You experienced relief from a terrible state of mind", "You experienced relief from a terrible state of mind");
		doc.addEntry(q42_25);
		q42_25.setSection(experimentalSec);
		q42_25.setLabel("42.25");
		q42_25.addValidationRule(ValidationRulesWrapper.instance().getRule("OneToFive"));

		NumericEntry q42_26 = factory.createNumericEntry("Feelings of sadness stopped", "Feelings of sadness stopped");
		doc.addEntry(q42_26);
		q42_26.setSection(experimentalSec);
		q42_26.setLabel("42.26");
		q42_26.addValidationRule(ValidationRulesWrapper.instance().getRule("OneToFive"));

		NumericEntry q42_27 = factory.createNumericEntry("You stopped feeling empty inside, as if you were unreal, or disconnected from your feelings", "You stopped feeling empty inside, as if you were unreal, or disconnected from your feelings");
		doc.addEntry(q42_27);
		q42_27.setSection(experimentalSec);
		q42_27.setLabel("42.27");
		q42_27.addValidationRule(ValidationRulesWrapper.instance().getRule("OneToFive"));

		NumericEntry q42_28 = factory.createNumericEntry("Feelings of depression stopped", "Feelings of depression stopped");
		doc.addEntry(q42_28);
		q42_28.setSection(experimentalSec);
		q42_28.setLabel("42.28");
		q42_28.addValidationRule(ValidationRulesWrapper.instance().getRule("OneToFive"));

		NumericEntry q42_29 = factory.createNumericEntry("You felt worse about yourself or felt more self-hatred/shame", "You felt worse about yourself or felt more self-hatred/shame");
		doc.addEntry(q42_29);
		q42_29.setSection(experimentalSec);
		q42_29.setLabel("42.29");
		q42_29.addValidationRule(ValidationRulesWrapper.instance().getRule("OneToFive"));

		NumericEntry q42_30 = factory.createNumericEntry("Other", "Other");
		doc.addEntry(q42_30);
		q42_30.setSection(experimentalSec);
		q42_30.setLabel("42.30");
		q42_30.addValidationRule(ValidationRulesWrapper.instance().getRule("OneToFive"));

		TextEntry q42_30a = factory.createTextEntry("Other - specify", "Other - specify");
		doc.addEntry(q42_30a);
		q42_30a.setSection(experimentalSec);
		q42_30a.setLabel("42.30a");

        return doc;
    }
}

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

import org.psygrid.data.model.hibernate.*;

public class AdverseOutcomesCarer extends AssessmentForm {
    public static Document createDocument(Factory factory) {

        Document doc = factory.createDocument("Adverse Outcomes Screening " +
                "Questionnaire (Carer)", "Adverse Outcomes Screening Questionnaire (Carer)");

        createDocumentStatuses(factory, doc);

        //Self Harm Section
        Section selfHarmSec = factory.createSection("Self-Harm Section");
        doc.addSection(selfHarmSec);
        selfHarmSec.setDisplayText("Self-Harm");
        SectionOccurrence selfHarmSecOcc = factory.createSectionOccurrence(
                "Self-Harm Section Occurrence");
        selfHarmSec.addOccurrence(selfHarmSecOcc);

        OptionEntry thought = factory.createOptionEntry("Thought of harming " +
                "self", "In the last 6 months, has your relative thought of harming " +
                "themselves?");
        doc.addEntry(thought);
        thought.setSection(selfHarmSec);
        thought.setLabel("1");
        Option thoughtNo = factory.createOption("No", "No (skip to Q2)", 0);
        thought.addOption(thoughtNo);
        Option thoughtYes = factory.createOption("Yes", "Yes", 1);
        thought.addOption(thoughtYes);

        OptionEntry thoughtFreq = factory.createOptionEntry("Thought of harming " +
                "self - how often", "If so, how often? (code most frequent " +
                "category)", EntryStatus.DISABLED);
        doc.addEntry(thoughtFreq);
        thoughtFreq.setSection(selfHarmSec);
        thoughtFreq.setLabel("1.a");
        thoughtFreq.addOption(factory.createOption("At least once a day", "At least once a day", 1));
        thoughtFreq.addOption(factory.createOption("At least once a week", "At least once a week", 2));
        thoughtFreq.addOption(factory.createOption("At least once a month", "At least once a month", 3));
        thoughtFreq.addOption(factory.createOption("At least once in the last six months", "At least once in the last six months", 4));
        Option thoughtFreqYes = factory.createOption("Other (please specify)", "Other (please specify)", 5);
        thoughtFreqYes.setTextEntryAllowed(true);
        thoughtFreq.addOption(thoughtFreqYes);
        createOptionDependent(factory, thoughtYes, thoughtFreq);

        OptionEntry harm = factory.createOptionEntry("Harmed self", "In the " +
                "last 6 months, have they harmed themselves?");
        doc.addEntry(harm);
        harm.setSection(selfHarmSec);
        harm.setLabel("2");
        Option harmNo = factory.createOption("No", "No (skip to Q3)", 0);
        harm.addOption(harmNo);
        Option harmYes = factory.createOption("Yes", "Yes", 1);
        harm.addOption(harmYes);

        OptionEntry harmFreq = factory.createOptionEntry("Thought of harming " +
                "self - how often", "If so, how many times?", EntryStatus.DISABLED);
        doc.addEntry(harmFreq);
        harmFreq.setSection(selfHarmSec);
        harmFreq.setLabel("2.a");
        harmFreq.addOption(factory.createOption("Once", "Once", 1));
        harmFreq.addOption(factory.createOption("2 to 5 times", "2 to 5 times", 2));
        harmFreq.addOption(factory.createOption("> 5 times", "> 5 times", 3));
        createOptionDependent(factory, harmYes, harmFreq);

        CompositeEntry howHarmed = factory.createComposite("How harmed self " +
                "composite", "How did they harm themselves? (record all relevant categories)");
        doc.addEntry(howHarmed);
        howHarmed.setSection(selfHarmSec);
        howHarmed.setEntryStatus(EntryStatus.DISABLED);
        howHarmed.setLabel("2.b");
        createOptionDependent(factory, harmYes, howHarmed);

        OptionEntry howHarmedOption = factory.createOptionEntry("How harmed self "
                + "option");
        howHarmedOption.setEntryStatus(EntryStatus.DISABLED);
        howHarmed.addEntry(howHarmedOption);
        howHarmedOption.setSection(selfHarmSec);
        howHarmedOption.addOption(factory.createOption("Knife/razor", "Knife/razor",
                1));
        howHarmedOption.addOption(factory.createOption("Pills/drugs/alcohol",
                "Pills/drugs/alcohol", 2));
        howHarmedOption.addOption(factory.createOption("Hanging", "Hanging", 3));
        howHarmedOption.addOption(factory.createOption("Suffocation", "Suffocation ",
                4));
        howHarmedOption.addOption(factory.createOption("Jump from high place",
                "Jump from high place", 5));
        howHarmedOption.addOption(factory.createOption(
                "Throw self in front of vehicle / in vehicle",
                "Throw self in front of vehicle / in vehicle", 6));
        howHarmedOption
                .addOption(factory.createOption("Starvation", "Starvation", 7));
        howHarmedOption.addOption(factory.createOption("Fire/burning",
                "Fire/burning", 8));
        howHarmedOption.addOption(factory.createOption("Drowning", "Drowning", 9));
        howHarmedOption.addOption(factory.createOption("Gun", "Gun", 10));
        Option otherOption = factory.createOption("Other (please specify)",
                "Other (please specify)", 11);
        otherOption.setTextEntryAllowed(true);
        howHarmedOption.addOption(otherOption);

        OptionEntry hospital = factory.createOptionEntry("Go to hospital",
                "If they harmed themselves, did they go to general hospital?",
                EntryStatus.DISABLED);
        doc.addEntry(hospital);
        hospital.setSection(selfHarmSec);
        hospital.setLabel("2.c");
        Option hospitalNo = factory.createOption("No", "No", 0);
        hospital.addOption(hospitalNo);
        Option hospitalYes = factory.createOption("Yes", "Yes", 1);
        hospital.addOption(hospitalYes);
        createOptionDependent(factory, harmYes, hospital);

        //Violence - Screening section
        Section violenceSec = factory.createSection("Violence Section");
        doc.addSection(violenceSec);
        violenceSec.setDisplayText("Violence - Screening");
        SectionOccurrence violenceSecOcc = factory.createSectionOccurrence(
                "Violence Section Occurrence");
        violenceSec.addOccurrence(violenceSecOcc);
        violenceSecOcc.setDisplayText("Violence - Screening");

        OptionEntry thoughtOthers = factory.createOptionEntry("Thought of harming " +
                "others", "In the last 6 months, has your relative thought of physically " +
                "harming others?");
        doc.addEntry(thoughtOthers);
        thoughtOthers.setSection(violenceSec);
        thoughtOthers.setLabel("3");
        Option thoughtOthersNo = factory.createOption("No", "No (skip to Q4)", 0);
        thoughtOthers.addOption(thoughtOthersNo);
        Option thoughtOthersYes = factory.createOption("Yes", "Yes", 1);
        thoughtOthers.addOption(thoughtOthersYes);

        OptionEntry thoughtOthersFreq = factory.createOptionEntry("Thought of harming " +
                "others - how often", "If so, how often? (code most frequent " +
                "category)", EntryStatus.DISABLED);
        doc.addEntry(thoughtOthersFreq);
        thoughtOthersFreq.setSection(violenceSec);
        thoughtOthersFreq.setLabel("3.a");
        thoughtOthersFreq.addOption(factory.createOption("At least once a day", "At least once a day", 1));
        thoughtOthersFreq.addOption(factory.createOption("At least once a week", "At least once a week", 2));
        thoughtOthersFreq.addOption(factory.createOption("At least once a month", "At least once a month", 3));
        thoughtOthersFreq.addOption(factory.createOption("At least once in the last six months", "At least once in the last six months", 4));
        Option thoughtOthersFreqYes = factory.createOption("Other (please specify)", "Other (please specify)", 5);
        thoughtOthersFreqYes.setTextEntryAllowed(true);
        thoughtOthersFreq.addOption(thoughtOthersFreqYes);
        createOptionDependent(factory, thoughtOthersYes, thoughtOthersFreq);

        CompositeEntry thoughtWho = factory.createComposite("Thought of " +
                "harming others - who", "If so, who? (record all relevant " +
                "categories)");
        doc.addEntry(thoughtWho);
        thoughtWho.setSection(violenceSec);
        thoughtWho.setLabel("3.b");
        thoughtWho.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, thoughtOthersYes, thoughtWho);

        OptionEntry thoughtWhoOption = factory.createOptionEntry("Thought of " +
                "harming others - who option");
        thoughtWho.addEntry(thoughtWhoOption);
        thoughtWho.setSection(violenceSec);
        thoughtWhoOption.addOption(factory.createOption("Family", "Family", 1));
        thoughtWhoOption.addOption(factory.createOption("Friend", "Friend", 2));
        thoughtWhoOption.addOption(factory.createOption("Health/care staff",
                "Health/care staff", 3));
        thoughtWhoOption.addOption(factory.createOption("Police", "Police", 4));
        thoughtWhoOption.addOption(factory.createOption("Stranger",
                "Stranger", 5));
        Option thoughtWhoOther = factory.createOption("Other (please specify)",
                "Other (please specify)", 6);
        thoughtWhoOther.setTextEntryAllowed(true);
        thoughtWhoOption.addOption(thoughtWhoOther);

        OptionEntry threatened = factory.createOptionEntry("Threatened with " +
                "violence", "In the last 6 months have they threatened others " +
                "with violence?");
        doc.addEntry(threatened);
        threatened.setSection(violenceSec);
        threatened.setLabel("4");
        Option threatenedNo = factory.createOption("No", "No (skip to Q5)", 0);
        threatened.addOption(threatenedNo);
        Option threatenedYes = factory.createOption("Yes", "Yes", 1);
        threatened.addOption(threatenedYes);

        OptionEntry threatenedFreq = factory.createOptionEntry("Threatened with " +
                "violence - frequency", "If so, how often?",
                EntryStatus.DISABLED);
        doc.addEntry(threatenedFreq);
        threatenedFreq.setSection(violenceSec);
        threatenedFreq.setLabel("4.a");
        threatenedFreq.addOption(factory.createOption("Once", "Once", 1));
        threatenedFreq.addOption(factory.createOption("2 to 5 times", "2 to 5 times", 2));
        threatenedFreq.addOption(factory.createOption("> 5 times", "> 5 times", 3));
        createOptionDependent(factory, threatenedYes, threatenedFreq);

        CompositeEntry threatenedWho = factory.createComposite("Threatened " +
                "with violence - who", "If so, who? (record all relevant " +
                "categories)");
        doc.addEntry(threatenedWho);
        threatenedWho.setLabel("4.b");
        threatenedWho.setEntryStatus(EntryStatus.DISABLED);
        threatenedWho.setSection(violenceSec);
        createOptionDependent(factory, threatenedYes, threatenedWho);

        OptionEntry threatenedWhoOption = factory.createOptionEntry("Threatened " +
                "with violence - who option");
        threatenedWho.addEntry(threatenedWhoOption);
        threatenedWhoOption.setSection(violenceSec);
        threatenedWhoOption.addOption(factory.createOption("Family", "Family", 1));
        threatenedWhoOption.addOption(factory.createOption("Friend", "Friend", 2));
        threatenedWhoOption.addOption(factory.createOption("Health/care staff",
                "Health/care staff", 3));
        threatenedWhoOption.addOption(factory.createOption("Police", "Police", 4));
        threatenedWhoOption.addOption(factory.createOption("Stranger",
                "Stranger", 5));
        Option threatenedWhoOther = factory.createOption("Other (please specify)",
                "Other (please specify)", 6);
        threatenedWhoOther.setTextEntryAllowed(true);
        threatenedWhoOption.addOption(threatenedWhoOther);

        OptionEntry threatenedFreqWeapon = factory.createOptionEntry("Threatend with " +
                "weapon - frequency", "If so, how often with a weapon?",
                EntryStatus.DISABLED);
        doc.addEntry(threatenedFreqWeapon);
        threatenedFreqWeapon.setSection(violenceSec);
        threatenedFreqWeapon.setLabel("4.c");
        threatenedFreqWeapon.addOption(factory.createOption("Once", "Once", 1));
        threatenedFreqWeapon.addOption(factory.createOption("2 to 5 times", "2 to 5 times", 2));
        threatenedFreqWeapon.addOption(factory.createOption("> 5 times", "> 5 times", 3));
        createOptionDependent(factory, threatenedYes, threatenedFreqWeapon);

        CompositeEntry threatenedWeapon = factory.createComposite("Threatened " +
                "with violence - type of weapon", "What type of weapon? (record " +
                "all relevant categories)");
        doc.addEntry(threatenedWeapon);
        threatenedWeapon.setLabel("4.d");
        threatenedWeapon.setEntryStatus(EntryStatus.DISABLED);
        threatenedWeapon.setSection(violenceSec);
        createOptionDependent(factory, threatenedYes, threatenedWeapon);

        OptionEntry threatenedWeaponOption = factory.createOptionEntry("Threatened " +
                "with violence - type of weapon option");
        threatenedWeapon.addEntry(threatenedWeaponOption);
        threatenedWeaponOption.setSection(violenceSec);
        threatenedWeaponOption.addOption(factory.createOption("knife or sharp " +
                "instrument", "knife or sharp instrument", 1));
        threatenedWeaponOption.addOption(factory.createOption("blunt instrument",
                "blunt instrument (eg baseball bat, brick)", 2));
        threatenedWeaponOption.addOption(factory.createOption("gun", "gun", 3));
        Option threatenedWeaponOther = factory.createOption("other (please specify)",
                "other (please specify)", 4);
        threatenedWeaponOther.setTextEntryAllowed(true);
        threatenedWeaponOption.addOption(threatenedWeaponOther);

        OptionEntry harmedOthers = factory.createOptionEntry("Harmed others",
                "In the last 6 months, have they physically harmed others?");
        doc.addEntry(harmedOthers);
        harmedOthers.setSection(violenceSec);
        harmedOthers.setLabel("5");
        Option harmedOthersNo = factory.createOption("No", "No (skip to Q6)", 0);
        harmedOthers.addOption(harmedOthersNo);
        Option harmedOthersYes = factory.createOption("Yes", "Yes", 1);
        harmedOthers.addOption(harmedOthersYes);

        OptionEntry harmedOthersFreq = factory.createOptionEntry("Harmed others " +
                "- frequency", "If so, how often?",
                EntryStatus.DISABLED);
        doc.addEntry(harmedOthersFreq);
        harmedOthersFreq.setSection(violenceSec);
        harmedOthersFreq.setLabel("5.a");
        harmedOthersFreq.addOption(factory.createOption("Once", "Once", 1));
        harmedOthersFreq.addOption(factory.createOption("2 to 5 times", "2 to 5 times", 2));
        harmedOthersFreq.addOption(factory.createOption("> 5 times", "> 5 times", 3));
        createOptionDependent(factory, harmedOthersYes, harmedOthersFreq);

        CompositeEntry harmedOthersWho = factory.createComposite("Harmed others " +
                "who", "If so, who? (record all relevant " +
                "categories)");
        harmedOthersWho.setEntryStatus(EntryStatus.DISABLED);
        doc.addEntry(harmedOthersWho);
        harmedOthersWho.setLabel("5.b");
        harmedOthersWho.setSection(violenceSec);
        createOptionDependent(factory, harmedOthersYes, harmedOthersWho);

        OptionEntry harmedOthersWhoOption = factory.createOptionEntry("Harmed " +
                "others - who option");
        harmedOthersWho.addEntry(harmedOthersWhoOption);
        harmedOthersWhoOption.setSection(violenceSec);
        harmedOthersWhoOption.addOption(factory.createOption("Family", "Family", 1));
        harmedOthersWhoOption.addOption(factory.createOption("Friend", "Friend", 2));
        harmedOthersWhoOption.addOption(factory.createOption("Health/care staff",
                "Health/care staff", 3));
        harmedOthersWhoOption.addOption(factory.createOption("Police", "Police", 4));
        harmedOthersWhoOption.addOption(factory.createOption("Stranger",
                "Stranger", 5));
        Option harmedOthersWhoOther = factory.createOption("Other (please specify)",
                "Other (please specify)", 6);
        harmedOthersWhoOther.setTextEntryAllowed(true);
        harmedOthersWhoOption.addOption(harmedOthersWhoOther);

        OptionEntry pushed = factory.createOptionEntry("Pushed anyone",
                "Have they pushed or grabbed anyone?");
        doc.addEntry(pushed);
        pushed.setSection(violenceSec);
        pushed.setLabel("6");
        pushed.addOption(factory.createOption("No", "No", 0));
        pushed.addOption(factory.createOption("Yes", "Yes", 1));

        OptionEntry slapped = factory.createOptionEntry("Slapped anyone",
                "Have they slapped, scratched or bitten anyone?");
        doc.addEntry(slapped);
        slapped.setSection(violenceSec);
        slapped.setLabel("7");
        slapped.addOption(factory.createOption("No", "No", 0));
        slapped.addOption(factory.createOption("Yes", "Yes", 1));

        OptionEntry choked = factory.createOptionEntry("Choked anyone",
                "Have they choked or strangled anyone?");
        doc.addEntry(choked);
        choked.setSection(violenceSec);
        choked.setLabel("8");
        choked.addOption(factory.createOption("No", "No", 0));
        choked.addOption(factory.createOption("Yes", "Yes", 1));

        OptionEntry kicked = factory.createOptionEntry("Kicked anyone",
                "Have they kicked anyone?");
        doc.addEntry(kicked);
        kicked.setSection(violenceSec);
        kicked.setLabel("9");
        kicked.addOption(factory.createOption("No", "No", 0));
        kicked.addOption(factory.createOption("Yes", "Yes", 1));

        OptionEntry beaten = factory.createOptionEntry("Beaten anyone",
                "Have they hit anyone with a fist or beaten up anyone?");
        doc.addEntry(beaten);
        beaten.setSection(violenceSec);
        beaten.setLabel("10");
        beaten.addOption(factory.createOption("No", "No", 0));
        beaten.addOption(factory.createOption("Yes", "Yes", 1));

        OptionEntry blunt = factory.createOptionEntry("Hit anyone with blunt " +
                "instrument", "Have they hit anyone with a blunt instrument or " +
                "object?");
        doc.addEntry(blunt);
        blunt.setSection(violenceSec);
        blunt.setLabel("11");
        blunt.addOption(factory.createOption("No", "No", 0));
        blunt.addOption(factory.createOption("Yes", "Yes", 1));

        OptionEntry knife = factory.createOptionEntry("Used a knife",
                "Have they used a knife or any sharp instrument?");
        doc.addEntry(knife);
        knife.setSection(violenceSec);
        knife.setLabel("12");
        knife.addOption(factory.createOption("No", "No", 0));
        knife.addOption(factory.createOption("Yes", "Yes", 1));

        OptionEntry sex = factory.createOptionEntry("Force anyone to have sex",
                "Have they tried to physically force anyone to have sex against " +
                        "their will?");
        doc.addEntry(sex);
        sex.setSection(violenceSec);
        sex.setLabel("13");
        sex.addOption(factory.createOption("No", "No", 0));
        sex.addOption(factory.createOption("Yes", "Yes", 1));

        OptionEntry otherViolent = factory.createOptionEntry("Anything else " +
                "might be violent",
                "Have they done anything else that might be considered violent? " +
                        "(include only violence to persons)");
        doc.addEntry(otherViolent);
        otherViolent.setSection(violenceSec);
        otherViolent.setLabel("14");
        otherViolent.addOption(factory.createOption("No", "No", 0));
        Option otherViolentYes = factory.createOption("Yes", "Yes", 1);
        otherViolent.addOption(otherViolentYes);

        OptionEntry otherViolentDetail = factory.createOptionEntry("Anything else " +
                "might be violent - What", "If so, what?", EntryStatus.DISABLED);
        doc.addEntry(otherViolentDetail);
        otherViolentDetail.setSection(violenceSec);
        otherViolentDetail.setLabel("14a");
        otherViolentDetail.addOption(factory.createOption("Hit with car",
                "Hit with car", 1));
        otherViolentDetail.addOption(factory.createOption("Arson/fire/burns",
                "Arson/fire/burns", 2));
        otherViolentDetail.addOption(factory.createOption("Threw liquid at " +
                "someone", "Threw liquid at someone", 3));
        otherViolentDetail.addOption(factory.createOption("Maced someone (or " +
                "other spray/gas)", "Maced someone (or other spray/gas)", 4));
        otherViolentDetail.addOption(factory.createOption("Fired gun",
                "Fired gun", 5));
        Option otherViolentDetailSpecify = factory.createOption("Other " +
                "(please specify)", "Other (please specify)", 6);
        otherViolentDetailSpecify.setTextEntryAllowed(true);
        otherViolentDetail.addOption(otherViolentDetailSpecify);
        createOptionDependent(factory, otherViolentYes, otherViolentDetail);

        OptionEntry otherViolentVictim = factory.createOptionEntry("Anything else " +
                "might be violent - injury to victim", "Injury to victim " +
                "(record most serious)", EntryStatus.DISABLED);
        doc.addEntry(otherViolentVictim);
        otherViolentVictim.setSection(violenceSec);
        otherViolentVictim.setLabel("14b");
        otherViolentVictim.addOption(factory.createOption("No injury",
                "No injury", 0));
        otherViolentVictim.addOption(factory.createOption("Bruises, cuts",
                "Bruises, cuts", 1));
        otherViolentVictim.addOption(factory.createOption("Broken bones/teeth",
                "Broken bones/teeth", 2));
        otherViolentVictim.addOption(factory.createOption("Unconscious, " +
                "internal injuries", "Unconscious, internal injuries", 3));
        otherViolentVictim.addOption(factory.createOption("Stab/gunshot",
                "Stab/gunshot", 4));
        otherViolentVictim.addOption(factory.createOption("Death", "Death", 5));
        otherViolentVictim.addOption(factory.createOption("Other", "Other", 7));
        createOptionDependent(factory, otherViolentYes, otherViolentVictim);

        OptionEntry otherViolentHospital = factory.createOptionEntry("Anything " +
                "else might be violent - victim went to hospital",
                "If the victim(s) was injured, did any go to general hospital?",
                EntryStatus.DISABLED);
        doc.addEntry(otherViolentHospital);
        otherViolentHospital.setSection(violenceSec);
        otherViolentHospital.setLabel("14c");
        otherViolentHospital.addOption(factory.createOption("No", "No", 0));
        otherViolentHospital.addOption(factory.createOption("Yes", "Yes", 1));
        createOptionDependent(factory, otherViolentYes, otherViolentHospital);

        return doc;
    }
}

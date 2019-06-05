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
package org.psygrid.edie;

import org.psygrid.common.ValidationRulesWrapper;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.AssessmentForm;

public class CAARMSWithGAF extends AssessmentForm {

    public static Document createDocument(Factory factory) {

        ValidationRule levelOfDistressRule = ValidationRulesWrapper.instance().getRule("Level of distress validation rule");
        ValidationRule zeroToHundred = ValidationRulesWrapper.instance().getRule(
                "ZeroToHundred");
        Document caarms = factory.createDocument("CAARMS with GAF",
                "CAARMS with GAF");

        createDocumentStatuses(factory, caarms);

        // main section
        Section mainSection = factory.createSection("Main section occurrence");
        caarms.addSection(mainSection);
        mainSection.setDisplayText("Main");
        SectionOccurrence mainSectionOcc = factory.createSectionOccurrence("Main Section Occurrence");
        mainSection.addOccurrence(mainSectionOcc);

        NarrativeEntry caarmsInstructions = factory.createNarrativeEntry("CAARMS instructions");
        caarms.addEntry(caarmsInstructions);
        caarmsInstructions.setSection(mainSection);
        caarmsInstructions.setDisplayText("");

        DateEntry programmeEntry = factory.createDateEntry("Date", "Date of assessment");
        caarms.addEntry(programmeEntry);
        programmeEntry.setSection(mainSection);

		TextEntry nameEntry = factory.createTextEntry("Name", "Name of assessor");
		caarms.addEntry(nameEntry);
		nameEntry.setSection(mainSection);

        // Unusual Thought Content Section
        Section unusualThoughtSection = factory.createSection("Unusual Thought Section");
        caarms.addSection(unusualThoughtSection);
        unusualThoughtSection.setDisplayText("Unusual Thought Content");
        SectionOccurrence unusualThoughtSectionOcc = factory.createSectionOccurrence("Unusual Thought Section Occurrence");
        unusualThoughtSection.addOccurrence(unusualThoughtSectionOcc);

        // Unusual Thought Global Rating Scale
        OptionEntry utcgrs = factory.createOptionEntry("Unusual Thought Content Global Rating Scale",
                "Unusual Thought Content Global Rating Scale");
        utcgrs.setOptionCodesDisplayed(true);
        caarms.addEntry(utcgrs);
        utcgrs.setSection(unusualThoughtSection);
        Option utcgrsop0 = factory.createOption("Never, absent", 0);
        utcgrs.addOption(utcgrsop0);
        Option utcgrsop1 = factory.createOption("Questionable", 1);
        utcgrs.addOption(utcgrsop1);
        Option utcgrsop2 = factory.createOption("Mild", 2);
        utcgrs.addOption(utcgrsop2);
        Option utcgrsop3 =  factory.createOption("Moderate", 3);
        utcgrs.addOption(utcgrsop3);
        Option utcgrsop4 = factory.createOption("Moderately severe", 4);
        utcgrs.addOption(utcgrsop4);
        Option utcgrsop5 = factory.createOption("Severe", 5);
        utcgrs.addOption(utcgrsop5);
        Option utcgrsop6 = factory.createOption("Psychotic and Severe", 6);
        utcgrs.addOption(utcgrsop6);
        utcgrs.setDefaultValue(utcgrsop0);

        DateEntry utcOnsetDate = factory.createDateEntry("Unusual Thought Content Onset Date",
                "Onset Date");
        caarms.addEntry(utcOnsetDate);
        utcOnsetDate.setSection(unusualThoughtSection);
        utcOnsetDate.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, utcgrsop1, utcOnsetDate);
        createOptionDependent(factory, utcgrsop2, utcOnsetDate);
        createOptionDependent(factory, utcgrsop3, utcOnsetDate);
        createOptionDependent(factory, utcgrsop4, utcOnsetDate);
        createOptionDependent(factory, utcgrsop5, utcOnsetDate);
        createOptionDependent(factory, utcgrsop6, utcOnsetDate);

        DateEntry utcOffsetDate = factory.createDateEntry("Unusual Thought Content Offset Date",
                "Offset Date");
        caarms.addEntry(utcOffsetDate);
        utcOffsetDate.setSection(unusualThoughtSection);
        utcOffsetDate.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, utcgrsop1, utcOffsetDate);
        createOptionDependent(factory, utcgrsop2, utcOffsetDate);
        createOptionDependent(factory, utcgrsop3, utcOffsetDate);
        createOptionDependent(factory, utcgrsop4, utcOffsetDate);
        createOptionDependent(factory, utcgrsop5, utcOffsetDate);
        createOptionDependent(factory, utcgrsop6, utcOffsetDate);

        // Unusual Thought Frequency and Duration
        OptionEntry utcfd = factory.createOptionEntry("Unusual Thought Content Frequency and Duration",
                "Frequency and Duration");
        utcfd.setOptionCodesDisplayed(true);
        caarms.addEntry(utcfd);
        utcfd.setSection(unusualThoughtSection);
        Option utcfdop0 = factory.createOption("Absent", 0);
        utcfd.addOption(utcfdop0);
        buildFrequencyOptions(factory, utcfd);
        utcfd.setEntryStatus(EntryStatus.DISABLED);
        utcfd.setDefaultValue(utcfdop0);
        createOptionDependent(factory, utcgrsop1, utcfd);
        createOptionDependent(factory, utcgrsop2, utcfd);
        createOptionDependent(factory, utcgrsop3, utcfd);
        createOptionDependent(factory, utcgrsop4, utcfd);
        createOptionDependent(factory, utcgrsop5, utcfd);
        createOptionDependent(factory, utcgrsop6, utcfd);

        OptionEntry utcFourTimes = factory.createOptionEntry("UTC Four Times",
                "Has this happened at least four times in total?");
        caarms.addEntry(utcFourTimes);
        utcFourTimes.setSection(unusualThoughtSection);
        Option utcFourTimesOpNo = factory.createOption("No", 0);
        utcFourTimes.addOption(utcFourTimesOpNo);
        utcFourTimes.addOption(factory.createOption("Yes", 1));
        utcFourTimes.setEntryStatus(EntryStatus.DISABLED);
        utcFourTimes.setDefaultValue(utcFourTimesOpNo);
        createOptionDependent(factory, utcgrsop1, utcFourTimes);
        createOptionDependent(factory, utcgrsop2, utcFourTimes);
        createOptionDependent(factory, utcgrsop3, utcFourTimes);
        createOptionDependent(factory, utcgrsop4, utcFourTimes);
        createOptionDependent(factory, utcgrsop5, utcFourTimes);
        createOptionDependent(factory, utcgrsop6, utcFourTimes);

        // Unusual Thought Pattern of Symptoms
        OptionEntry utcpos = factory.createOptionEntry("Unusual Thought Content Pattern of Symptoms",
                "Pattern of Symptoms");
        utcpos.setOptionCodesDisplayed(true);
        caarms.addEntry(utcpos);
        utcpos.setSection(unusualThoughtSection);
        Option utcposop1 = factory.createOption("No relation to substance use noted", 0);
        utcpos.addOption(utcposop1);
        buildSymptomsOptions(factory, utcpos);
        utcpos.setEntryStatus(EntryStatus.DISABLED);
        utcpos.setDefaultValue(utcposop1);
        createOptionDependent(factory, utcgrsop1, utcpos);
        createOptionDependent(factory, utcgrsop2, utcpos);
        createOptionDependent(factory, utcgrsop3, utcpos);
        createOptionDependent(factory, utcgrsop4, utcpos);
        createOptionDependent(factory, utcgrsop5, utcpos);
        createOptionDependent(factory, utcgrsop6, utcpos);

        NumericEntry utcLevelOfDistress = factory.createNumericEntry("Unusual Thought Content Level of Distress",
                "Level of Distress (In Relation to Symptoms)");
        caarms.addEntry(utcLevelOfDistress);
        utcLevelOfDistress.setSection(unusualThoughtSection);
        utcLevelOfDistress.addValidationRule(levelOfDistressRule);
        utcLevelOfDistress.setEntryStatus(EntryStatus.DISABLED);
        utcLevelOfDistress.setDefaultValue(0.0);
        createOptionDependent(factory, utcgrsop1, utcLevelOfDistress);
        createOptionDependent(factory, utcgrsop2, utcLevelOfDistress);
        createOptionDependent(factory, utcgrsop3, utcLevelOfDistress);
        createOptionDependent(factory, utcgrsop4, utcLevelOfDistress);
        createOptionDependent(factory, utcgrsop5, utcLevelOfDistress);
        createOptionDependent(factory, utcgrsop6, utcLevelOfDistress);

        // Non-Bizarre Ideas Section
        Section nonBizarreIdeasSection = factory.createSection("Non-Bizarre Ideas Section");
        caarms.addSection(nonBizarreIdeasSection);
        nonBizarreIdeasSection.setDisplayText("Non-Bizarre Ideas");
        SectionOccurrence nonBizarreIdeasSectionOcc = factory.createSectionOccurrence("Non-Bizarre Ideas Section Occurrence");
        nonBizarreIdeasSection.addOccurrence(nonBizarreIdeasSectionOcc);

        // Non-Bizarre Ideas Global Rating Scale
        OptionEntry nbigrs = factory.createOptionEntry("Non-Bizarre Ideas Global Rating Scale",
                "Non-Bizarre Ideas Global Rating Scale");
        nbigrs.setOptionCodesDisplayed(true);
        caarms.addEntry(nbigrs);
        nbigrs.setSection(nonBizarreIdeasSection);
        Option nbigrsop0 = factory.createOption("Never, absent", 0);
        nbigrs.addOption(nbigrsop0);
        Option nbigrsop1 = factory.createOption("Questionable", 1);
        nbigrs.addOption(nbigrsop1);
        Option nbigrsop2 = factory.createOption("Mild", 2);
        nbigrs.addOption(nbigrsop2);
        Option nbigrsop3 =  factory.createOption("Moderate", 3);
        nbigrs.addOption(nbigrsop3);
        Option nbigrsop4 = factory.createOption("Moderately severe", 4);
        nbigrs.addOption(nbigrsop4);
        Option nbigrsop5 = factory.createOption("Severe", 5);
        nbigrs.addOption(nbigrsop5);
        Option nbigrsop6 = factory.createOption("Psychotic and Severe", 6);
        nbigrs.addOption(nbigrsop6);
        nbigrs.setDefaultValue(nbigrsop0);

        DateEntry nbiOnsetDate = factory.createDateEntry("Non-Bizarre Ideas Onset Date",
                "Onset Date");
        caarms.addEntry(nbiOnsetDate);
        nbiOnsetDate.setSection(nonBizarreIdeasSection);
        nbiOnsetDate.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, nbigrsop1, nbiOnsetDate);
        createOptionDependent(factory, nbigrsop2, nbiOnsetDate);
        createOptionDependent(factory, nbigrsop3, nbiOnsetDate);
        createOptionDependent(factory, nbigrsop4, nbiOnsetDate);
        createOptionDependent(factory, nbigrsop5, nbiOnsetDate);
        createOptionDependent(factory, nbigrsop6, nbiOnsetDate);

        DateEntry nbiOffsetDate = factory.createDateEntry("Non-Bizarre Ideas Offset Date",
                "Offset Date");
        caarms.addEntry(nbiOffsetDate);
        nbiOffsetDate.setSection(nonBizarreIdeasSection);
        nbiOffsetDate.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, nbigrsop1, nbiOffsetDate);
        createOptionDependent(factory, nbigrsop2, nbiOffsetDate);
        createOptionDependent(factory, nbigrsop3, nbiOffsetDate);
        createOptionDependent(factory, nbigrsop4, nbiOffsetDate);
        createOptionDependent(factory, nbigrsop5, nbiOffsetDate);
        createOptionDependent(factory, nbigrsop6, nbiOffsetDate);

//      Non-Bizarre Ideas Frequency and Duration
        OptionEntry nbifd = factory.createOptionEntry("Non-Bizarre Ideas Frequency and Duration",
                "Frequency and Duration");
        nbifd.setOptionCodesDisplayed(true);
        caarms.addEntry(nbifd);
        nbifd.setSection(nonBizarreIdeasSection);
        Option nbifdop0 = factory.createOption("Absent", 0);
        nbifd.addOption(nbifdop0);
        buildFrequencyOptions(factory, nbifd);
        nbifd.setEntryStatus(EntryStatus.DISABLED);
        nbifd.setDefaultValue(nbifdop0);
        createOptionDependent(factory, nbigrsop1, nbifd);
        createOptionDependent(factory, nbigrsop2, nbifd);
        createOptionDependent(factory, nbigrsop3, nbifd);
        createOptionDependent(factory, nbigrsop4, nbifd);
        createOptionDependent(factory, nbigrsop5, nbifd);
        createOptionDependent(factory, nbigrsop6, nbifd);

        OptionEntry nbiFourTimes = factory.createOptionEntry("NBI Four Times",
                "Has this happened at least four times in total?");
        caarms.addEntry(nbiFourTimes);
        nbiFourTimes.setSection(nonBizarreIdeasSection);
        Option nbiFourTimesOpNo = factory.createOption("No", 0);
        nbiFourTimes.addOption(nbiFourTimesOpNo);
        nbiFourTimes.addOption(factory.createOption("Yes", 1));
        nbiFourTimes.setEntryStatus(EntryStatus.DISABLED);
        nbiFourTimes.setDefaultValue(nbiFourTimesOpNo);
        createOptionDependent(factory, nbigrsop1, nbiFourTimes);
        createOptionDependent(factory, nbigrsop2, nbiFourTimes);
        createOptionDependent(factory, nbigrsop3, nbiFourTimes);
        createOptionDependent(factory, nbigrsop4, nbiFourTimes);
        createOptionDependent(factory, nbigrsop5, nbiFourTimes);
        createOptionDependent(factory, nbigrsop6, nbiFourTimes);

//      Non-Bizarre Ideas Pattern of Symptoms
        OptionEntry nbipos = factory.createOptionEntry("Non-Bizarre Ideas Pattern of Symptoms",
                "Pattern of Symptoms");
        nbipos.setOptionCodesDisplayed(true);
        caarms.addEntry(nbipos);
        nbipos.setSection(nonBizarreIdeasSection);
        Option nbiposop1 = factory.createOption("No relation to substance use noted", 0);
        nbipos.addOption(nbiposop1);
        buildSymptomsOptions(factory, nbipos);
        nbipos.setEntryStatus(EntryStatus.DISABLED);
        nbipos.setDefaultValue(nbiposop1);
        createOptionDependent(factory, nbigrsop1, nbipos);
        createOptionDependent(factory, nbigrsop2, nbipos);
        createOptionDependent(factory, nbigrsop3, nbipos);
        createOptionDependent(factory, nbigrsop4, nbipos);
        createOptionDependent(factory, nbigrsop5, nbipos);
        createOptionDependent(factory, nbigrsop6, nbipos);

        NumericEntry nbiLevelOfDistress = factory.createNumericEntry("Non-Bizarre Ideas Level of Distress",
                "Level of Distress (In Relation to Symptoms)");
        caarms.addEntry(nbiLevelOfDistress);
        nbiLevelOfDistress.setSection(nonBizarreIdeasSection);
        nbiLevelOfDistress.addValidationRule(levelOfDistressRule);
        nbiLevelOfDistress.setEntryStatus(EntryStatus.DISABLED);
        nbiLevelOfDistress.setDefaultValue(0.0);
        createOptionDependent(factory, nbigrsop1, nbiLevelOfDistress);
        createOptionDependent(factory, nbigrsop2, nbiLevelOfDistress);
        createOptionDependent(factory, nbigrsop3, nbiLevelOfDistress);
        createOptionDependent(factory, nbigrsop4, nbiLevelOfDistress);
        createOptionDependent(factory, nbigrsop5, nbiLevelOfDistress);
        createOptionDependent(factory, nbigrsop6, nbiLevelOfDistress);

        // Perceptual Abnormalities Section
        Section perceptualAbnormalitiesSection = factory.createSection("Perceptual Abnormalities Section");
        caarms.addSection(perceptualAbnormalitiesSection);
        perceptualAbnormalitiesSection.setDisplayText("Perceptual Abnormalities");
        SectionOccurrence perceptualAbnormalitiesSectionOcc = factory.createSectionOccurrence("Perceptual Abnormalities Section Occurrence");
        perceptualAbnormalitiesSection.addOccurrence(perceptualAbnormalitiesSectionOcc);

        // Perceptual Abnormalities Global Rating Scale
        OptionEntry perabgrs = factory.createOptionEntry("Perceptual Abnormalities Global Rating Scale",
                "Perceptual Abnormalities Global Rating Scale");
        perabgrs.setOptionCodesDisplayed(true);
        caarms.addEntry(perabgrs);
        perabgrs.setSection(perceptualAbnormalitiesSection);
        Option perabgrsop0 = factory.createOption("Never, absent", 0);
        perabgrs.addOption(perabgrsop0);
        Option perabgrsop1 = factory.createOption("Questionable", 1);
        perabgrs.addOption(perabgrsop1);
        Option perabgrsop2 = factory.createOption("Mild", 2);
        perabgrs.addOption(perabgrsop2);
        Option perabgrsop3 =  factory.createOption("Moderate", 3);
        perabgrs.addOption(perabgrsop3);
        Option perabgrsop4 = factory.createOption("Moderately severe", 4);
        perabgrs.addOption(perabgrsop4);
        Option perabgrsop5 = factory.createOption("Psychotic but not severe", 5);
        perabgrs.addOption(perabgrsop5);
        Option perabgrsop6 = factory.createOption("Psychotic and severe", 6);
        perabgrs.addOption(perabgrsop6);
        perabgrs.setDefaultValue(perabgrsop0);

        DateEntry perabOnsetDate = factory.createDateEntry("Perceptual Abnormalities Onset Date",
                "Onset Date");
        caarms.addEntry(perabOnsetDate);
        perabOnsetDate.setSection(perceptualAbnormalitiesSection);
        perabOnsetDate.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, perabgrsop1, perabOnsetDate);
        createOptionDependent(factory, perabgrsop2, perabOnsetDate);
        createOptionDependent(factory, perabgrsop3, perabOnsetDate);
        createOptionDependent(factory, perabgrsop4, perabOnsetDate);
        createOptionDependent(factory, perabgrsop5, perabOnsetDate);
        createOptionDependent(factory, perabgrsop6, perabOnsetDate);

        DateEntry perabOffsetDate = factory.createDateEntry("Perceptual Abnormalities Offset Date",
                "Offset Date");
        caarms.addEntry(perabOffsetDate);
        perabOffsetDate.setSection(perceptualAbnormalitiesSection);
        perabOffsetDate.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, perabgrsop1, perabOffsetDate);
        createOptionDependent(factory, perabgrsop2, perabOffsetDate);
        createOptionDependent(factory, perabgrsop3, perabOffsetDate);
        createOptionDependent(factory, perabgrsop4, perabOffsetDate);
        createOptionDependent(factory, perabgrsop5, perabOffsetDate);
        createOptionDependent(factory, perabgrsop6, perabOffsetDate);

//      Perceptual Abnormalities Frequency and Duration
        OptionEntry perabfd = factory.createOptionEntry("Perceptual Abnormalities Frequency and Duration",
                "Frequency and Duration");
        perabfd.setOptionCodesDisplayed(true);
        caarms.addEntry(perabfd);
        perabfd.setSection(perceptualAbnormalitiesSection);
        Option perabfdop0 = factory.createOption("Absent", 0);
        perabfd.addOption(perabfdop0);
        buildFrequencyOptions(factory, perabfd);
        perabfd.setEntryStatus(EntryStatus.DISABLED);
        perabfd.setDefaultValue(perabfdop0);
        createOptionDependent(factory, perabgrsop1, perabfd);
        createOptionDependent(factory, perabgrsop2, perabfd);
        createOptionDependent(factory, perabgrsop3, perabfd);
        createOptionDependent(factory, perabgrsop4, perabfd);
        createOptionDependent(factory, perabgrsop5, perabfd);
        createOptionDependent(factory, perabgrsop6, perabfd);

        OptionEntry perabFourTimes = factory.createOptionEntry("perab Four Times",
                "Has this happened at least four times in total?");
        caarms.addEntry(perabFourTimes);
        perabFourTimes.setSection(perceptualAbnormalitiesSection);
        Option perabFourTimesOpNo = factory.createOption("No", 0);
        perabFourTimes.addOption(perabFourTimesOpNo);
        perabFourTimes.addOption(factory.createOption("Yes", 1));
        perabFourTimes.setEntryStatus(EntryStatus.DISABLED);
        perabFourTimes.setDefaultValue(perabFourTimesOpNo);
        createOptionDependent(factory, perabgrsop1, perabFourTimes);
        createOptionDependent(factory, perabgrsop2, perabFourTimes);
        createOptionDependent(factory, perabgrsop3, perabFourTimes);
        createOptionDependent(factory, perabgrsop4, perabFourTimes);
        createOptionDependent(factory, perabgrsop5, perabFourTimes);
        createOptionDependent(factory, perabgrsop6, perabFourTimes);

//      Perceptual Abnormalities Pattern of Symptoms
        OptionEntry perabpos = factory.createOptionEntry("Perceptual Abnormalities Pattern of Symptoms",
                "Pattern of Symptoms");
        perabpos.setOptionCodesDisplayed(true);
        caarms.addEntry(perabpos);
        perabpos.setSection(perceptualAbnormalitiesSection);
        Option perabposop1 = factory.createOption("No relation to substance use noted", 0);
        perabpos.addOption(perabposop1);
        buildSymptomsOptions(factory, perabpos);
        perabpos.setEntryStatus(EntryStatus.DISABLED);
        perabpos.setDefaultValue(perabposop1);
        createOptionDependent(factory, perabgrsop1, perabpos);
        createOptionDependent(factory, perabgrsop2, perabpos);
        createOptionDependent(factory, perabgrsop3, perabpos);
        createOptionDependent(factory, perabgrsop4, perabpos);
        createOptionDependent(factory, perabgrsop5, perabpos);
        createOptionDependent(factory, perabgrsop6, perabpos);

        NumericEntry perabLevelOfDistress = factory.createNumericEntry("Perceptual Abnormalities Level of Distress",
                "Level of Distress (In Relation to Symptoms)");
        caarms.addEntry(perabLevelOfDistress);
        perabLevelOfDistress.setSection(perceptualAbnormalitiesSection);
        perabLevelOfDistress.addValidationRule(levelOfDistressRule);
        perabLevelOfDistress.setEntryStatus(EntryStatus.DISABLED);
        perabLevelOfDistress.setDefaultValue(0.0);
        createOptionDependent(factory, perabgrsop1, perabLevelOfDistress);
        createOptionDependent(factory, perabgrsop2, perabLevelOfDistress);
        createOptionDependent(factory, perabgrsop3, perabLevelOfDistress);
        createOptionDependent(factory, perabgrsop4, perabLevelOfDistress);
        createOptionDependent(factory, perabgrsop5, perabLevelOfDistress);
        createOptionDependent(factory, perabgrsop6, perabLevelOfDistress);

        // Disorganised Speech Section
        Section disorganisedSpeechSection = factory.createSection("Disorganised Speech Section");
        caarms.addSection(disorganisedSpeechSection);
        disorganisedSpeechSection.setDisplayText("Disorganised Speech");
        SectionOccurrence disorganisedSpeechSectionOcc = factory.createSectionOccurrence("Disorganised Speech Section Occurrence");
        disorganisedSpeechSection.addOccurrence(disorganisedSpeechSectionOcc);

        // Disorganised Speech Global Rating Scale
        OptionEntry disspgrs = factory.createOptionEntry("Disorganised Speech Global Rating Scale",
                "Disorganised Speech Global Rating Scale");
        disspgrs.setOptionCodesDisplayed(true);
        caarms.addEntry(disspgrs);
        disspgrs.setSection(disorganisedSpeechSection);
        Option disspgrsop0 = factory.createOption("Never, absent", 0);
        disspgrs.addOption(disspgrsop0);
        Option disspgrsop1 = factory.createOption("Questionable", 1);
        disspgrs.addOption(disspgrsop1);
        Option disspgrsop2 = factory.createOption("Mild", 2);
        disspgrs.addOption(disspgrsop2);
        Option disspgrsop3 =  factory.createOption("Moderate", 3);
        disspgrs.addOption(disspgrsop3);
        Option disspgrsop4 = factory.createOption("Moderately severe", 4);
        disspgrs.addOption(disspgrsop4);
        Option disspgrsop5 = factory.createOption("Severe", 5);
        disspgrs.addOption(disspgrsop5);
        Option disspgrsop6 = factory.createOption("Psychotic", 6);
        disspgrs.addOption(disspgrsop6);
        disspgrs.setDefaultValue(disspgrsop0);

        DateEntry disspOnsetDate = factory.createDateEntry("Disorganised Speech Onset Date",
                "Onset Date");
        caarms.addEntry(disspOnsetDate);
        disspOnsetDate.setSection(disorganisedSpeechSection);
        disspOnsetDate.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, disspgrsop1, disspOnsetDate);
        createOptionDependent(factory, disspgrsop2, disspOnsetDate);
        createOptionDependent(factory, disspgrsop3, disspOnsetDate);
        createOptionDependent(factory, disspgrsop4, disspOnsetDate);
        createOptionDependent(factory, disspgrsop5, disspOnsetDate);
        createOptionDependent(factory, disspgrsop6, disspOnsetDate);

        DateEntry disspOffsetDate = factory.createDateEntry("Disorganised Speech Offset Date",
                "Offset Date");
        caarms.addEntry(disspOffsetDate);
        disspOffsetDate.setSection(disorganisedSpeechSection);
        disspOffsetDate.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, disspgrsop1, disspOffsetDate);
        createOptionDependent(factory, disspgrsop2, disspOffsetDate);
        createOptionDependent(factory, disspgrsop3, disspOffsetDate);
        createOptionDependent(factory, disspgrsop4, disspOffsetDate);
        createOptionDependent(factory, disspgrsop5, disspOffsetDate);
        createOptionDependent(factory, disspgrsop6, disspOffsetDate);

//      Disorganised Speech Frequency and Duration
        OptionEntry disspfd = factory.createOptionEntry("Disorganised Speech Frequency and Duration",
                "Frequency and Duration");
        disspfd.setOptionCodesDisplayed(true);
        caarms.addEntry(disspfd);
        disspfd.setSection(disorganisedSpeechSection);
        Option disspfdop0 = factory.createOption("Absent", 0);
        disspfd.addOption(disspfdop0);
        buildFrequencyOptions(factory, disspfd);
        disspfd.setEntryStatus(EntryStatus.DISABLED);
        disspfd.setDefaultValue(disspfdop0);
        createOptionDependent(factory, disspgrsop1, disspfd);
        createOptionDependent(factory, disspgrsop2, disspfd);
        createOptionDependent(factory, disspgrsop3, disspfd);
        createOptionDependent(factory, disspgrsop4, disspfd);
        createOptionDependent(factory, disspgrsop5, disspfd);
        createOptionDependent(factory, disspgrsop6, disspfd);

        OptionEntry disspFourTimes = factory.createOptionEntry("dissp Four Times",
                "Has this happened at least four times in total?");
        caarms.addEntry(disspFourTimes);
        disspFourTimes.setSection(disorganisedSpeechSection);
        Option disspFourTimesOpNo = factory.createOption("No", 0);
        disspFourTimes.addOption(disspFourTimesOpNo);
        disspFourTimes.addOption(factory.createOption("Yes", 1));
        disspFourTimes.setEntryStatus(EntryStatus.DISABLED);
        disspFourTimes.setDefaultValue(disspFourTimesOpNo);
        createOptionDependent(factory, disspgrsop1, disspFourTimes);
        createOptionDependent(factory, disspgrsop2, disspFourTimes);
        createOptionDependent(factory, disspgrsop3, disspFourTimes);
        createOptionDependent(factory, disspgrsop4, disspFourTimes);
        createOptionDependent(factory, disspgrsop5, disspFourTimes);
        createOptionDependent(factory, disspgrsop6, disspFourTimes);

//      Disorganised Speech Pattern of Symptoms
        OptionEntry dissppos = factory.createOptionEntry("Disorganised Speech Pattern of Symptoms",
                "Pattern of Symptoms");
        dissppos.setOptionCodesDisplayed(true);
        caarms.addEntry(dissppos);
        dissppos.setSection(disorganisedSpeechSection);
        Option disspposop1 = factory.createOption("No relation to substance use noted", 0);
        dissppos.addOption(disspposop1);
        buildSymptomsOptions(factory, dissppos);
        dissppos.setEntryStatus(EntryStatus.DISABLED);
        dissppos.setDefaultValue(disspposop1);
        createOptionDependent(factory, disspgrsop1, dissppos);
        createOptionDependent(factory, disspgrsop2, dissppos);
        createOptionDependent(factory, disspgrsop3, dissppos);
        createOptionDependent(factory, disspgrsop4, dissppos);
        createOptionDependent(factory, disspgrsop5, dissppos);
        createOptionDependent(factory, disspgrsop6, dissppos);

        NumericEntry disspLevelOfDistress = factory.createNumericEntry("Disorganised Speech Level of Distress",
                "Level of Distress (In Relation to Symptoms)");
        caarms.addEntry(disspLevelOfDistress);
        disspLevelOfDistress.setSection(disorganisedSpeechSection);
        disspLevelOfDistress.addValidationRule(levelOfDistressRule);
        disspLevelOfDistress.setEntryStatus(EntryStatus.DISABLED);
        disspLevelOfDistress.setDefaultValue(0.0);
        createOptionDependent(factory, disspgrsop1, disspLevelOfDistress);
        createOptionDependent(factory, disspgrsop2, disspLevelOfDistress);
        createOptionDependent(factory, disspgrsop3, disspLevelOfDistress);
        createOptionDependent(factory, disspgrsop4, disspLevelOfDistress);
        createOptionDependent(factory, disspgrsop5, disspLevelOfDistress);
        createOptionDependent(factory, disspgrsop6, disspLevelOfDistress);

        // Agressive/Dangerous Behaviour Section
        Section agressiveDangerousSection = factory.createSection("Agressive/Dangerous Behaviour Section");
        caarms.addSection(agressiveDangerousSection);
        agressiveDangerousSection.setDisplayText("Agressive/Dangerous Behaviour");
        SectionOccurrence agressiveDangerousSectionOcc = factory.createSectionOccurrence("Agressive/Dangerous Behaviour Section Occurrence");
        agressiveDangerousSection.addOccurrence(agressiveDangerousSectionOcc);

        // Agressive/Dangerous Behaviour Severity Rating Scale
        OptionEntry agdabegrs = factory.createOptionEntry("Agressive/Dangerous Behaviour Global Rating Scale",
                "Agressive/Dangerous Behaviour Global Rating Scale");
        agdabegrs.setOptionCodesDisplayed(true);
        caarms.addEntry(agdabegrs);
        agdabegrs.setSection(agressiveDangerousSection);
        Option agdabegrsop0 = factory.createOption("Never, absent", 0);
        agdabegrs.addOption(agdabegrsop0);
        Option agdabegrsop1 = factory.createOption("Questionable", 1);
        agdabegrs.addOption(agdabegrsop1);
        Option agdabegrsop2 = factory.createOption("Mild", 2);
        agdabegrs.addOption(agdabegrsop2);
        Option agdabegrsop3 =  factory.createOption("Moderate", 3);
        agdabegrs.addOption(agdabegrsop3);
        Option agdabegrsop4 = factory.createOption("Moderately severe", 4);
        agdabegrs.addOption(agdabegrsop4);
        Option agdabegrsop5 = factory.createOption("Severe", 5);
        agdabegrs.addOption(agdabegrsop5);
        Option agdabegrsop6 = factory.createOption("Extreme", 6);
        agdabegrs.addOption(agdabegrsop6);
        agdabegrs.setDefaultValue(agdabegrsop0);

        DateEntry agdabeOnsetDate = factory.createDateEntry("Agressive/Dangerous Behaviour Onset Date",
                "Onset Date");
        caarms.addEntry(agdabeOnsetDate);
        agdabeOnsetDate.setSection(agressiveDangerousSection);
        agdabeOnsetDate.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, agdabegrsop1, agdabeOnsetDate);
        createOptionDependent(factory, agdabegrsop2, agdabeOnsetDate);
        createOptionDependent(factory, agdabegrsop3, agdabeOnsetDate);
        createOptionDependent(factory, agdabegrsop4, agdabeOnsetDate);
        createOptionDependent(factory, agdabegrsop5, agdabeOnsetDate);
        createOptionDependent(factory, agdabegrsop6, agdabeOnsetDate);

        DateEntry agdabeOffsetDate = factory.createDateEntry("Agressive/Dangerous Behaviour Offset Date",
                "Offset Date");
        caarms.addEntry(agdabeOffsetDate);
        agdabeOffsetDate.setSection(agressiveDangerousSection);
        agdabeOffsetDate.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, agdabegrsop1, agdabeOffsetDate);
        createOptionDependent(factory, agdabegrsop2, agdabeOffsetDate);
        createOptionDependent(factory, agdabegrsop3, agdabeOffsetDate);
        createOptionDependent(factory, agdabegrsop4, agdabeOffsetDate);
        createOptionDependent(factory, agdabegrsop5, agdabeOffsetDate);
        createOptionDependent(factory, agdabegrsop6, agdabeOffsetDate);

//      Agressive/Dangerous Behaviour Frequency and Duration
        OptionEntry agdabefd = factory.createOptionEntry("Agressive/Dangerous Behaviour Frequency and Duration",
                "Frequency and Duration");
        agdabefd.setOptionCodesDisplayed(true);
        caarms.addEntry(agdabefd);
        agdabefd.setSection(agressiveDangerousSection);
        Option agdabefdop0 = factory.createOption("Absent", 0);
        agdabefd.addOption(agdabefdop0);
        buildFrequencyOptions(factory, agdabefd);
        agdabefd.setEntryStatus(EntryStatus.DISABLED);
        agdabefd.setDefaultValue(agdabefdop0);
        createOptionDependent(factory, agdabegrsop1, agdabefd);
        createOptionDependent(factory, agdabegrsop2, agdabefd);
        createOptionDependent(factory, agdabegrsop3, agdabefd);
        createOptionDependent(factory, agdabegrsop4, agdabefd);
        createOptionDependent(factory, agdabegrsop5, agdabefd);
        createOptionDependent(factory, agdabegrsop6, agdabefd);

//      Agressive/Dangerous Behaviour Pattern of Symptoms
        OptionEntry agdabepos = factory.createOptionEntry("Agressive/Dangerous Behaviour Pattern of Symptoms",
                "Pattern of Symptoms");
        agdabepos.setOptionCodesDisplayed(true);
        caarms.addEntry(agdabepos);
        agdabepos.setSection(agressiveDangerousSection);
        Option agdabeposop1 = factory.createOption("No relation to substance use noted", 0);
        agdabepos.addOption(agdabeposop1);
        buildSymptomsOptions(factory, agdabepos);
        agdabepos.setEntryStatus(EntryStatus.DISABLED);
        agdabepos.setDefaultValue(agdabeposop1);
        createOptionDependent(factory, agdabegrsop1, agdabepos);
        createOptionDependent(factory, agdabegrsop2, agdabepos);
        createOptionDependent(factory, agdabegrsop3, agdabepos);
        createOptionDependent(factory, agdabegrsop4, agdabepos);
        createOptionDependent(factory, agdabegrsop5, agdabepos);
        createOptionDependent(factory, agdabegrsop6, agdabepos);

        // Suicidality Section
        Section suicidalitySection = factory.createSection("Suicidality Section");
        caarms.addSection(suicidalitySection);
        suicidalitySection.setDisplayText("Suicidality");
        SectionOccurrence suicidalitySectionOcc = factory.createSectionOccurrence("Suicidality Section Occurrence");
        suicidalitySection.addOccurrence(suicidalitySectionOcc);

        // Suicidality Global Rating Scale
        OptionEntry suicidalitygrs = factory.createOptionEntry("Suicidality Global Rating Scale",
                "Suicidality Global Rating Scale");
        suicidalitygrs.setOptionCodesDisplayed(true);
        caarms.addEntry(suicidalitygrs);
        suicidalitygrs.setSection(suicidalitySection);
        Option suicidalitygrsop0 = factory.createOption("Never, absent", 0);
        suicidalitygrs.addOption(suicidalitygrsop0);
        Option suicidalitygrsop1 = factory.createOption("Questionable", 1);
        suicidalitygrs.addOption(suicidalitygrsop1);
        Option suicidalitygrsop2 = factory.createOption("Mild", 2);
        suicidalitygrs.addOption(suicidalitygrsop2);
        Option suicidalitygrsop3 =  factory.createOption("Moderate", 3);
        suicidalitygrs.addOption(suicidalitygrsop3);
        Option suicidalitygrsop4 = factory.createOption("Moderately severe", 4);
        suicidalitygrs.addOption(suicidalitygrsop4);
        Option suicidalitygrsop5 = factory.createOption("Severe", 5);
        suicidalitygrs.addOption(suicidalitygrsop5);
        Option suicidalitygrsop6 = factory.createOption("Extreme", 6);
        suicidalitygrs.addOption(suicidalitygrsop6);
        suicidalitygrs.setDefaultValue(suicidalitygrsop0);

        DateEntry suicidalityOnsetDate = factory.createDateEntry("Suicidality Onset Date",
                "Onset Date");
        caarms.addEntry(suicidalityOnsetDate);
        suicidalityOnsetDate.setSection(suicidalitySection);
        suicidalityOnsetDate.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, suicidalitygrsop1, suicidalityOnsetDate);
        createOptionDependent(factory, suicidalitygrsop2, suicidalityOnsetDate);
        createOptionDependent(factory, suicidalitygrsop3, suicidalityOnsetDate);
        createOptionDependent(factory, suicidalitygrsop4, suicidalityOnsetDate);
        createOptionDependent(factory, suicidalitygrsop5, suicidalityOnsetDate);
        createOptionDependent(factory, suicidalitygrsop6, suicidalityOnsetDate);

        DateEntry suicidalityOffsetDate = factory.createDateEntry("Suicidality Offset Date",
                "Offset Date");
        caarms.addEntry(suicidalityOffsetDate);
        suicidalityOffsetDate.setSection(suicidalitySection);
        suicidalityOffsetDate.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, suicidalitygrsop1, suicidalityOffsetDate);
        createOptionDependent(factory, suicidalitygrsop2, suicidalityOffsetDate);
        createOptionDependent(factory, suicidalitygrsop3, suicidalityOffsetDate);
        createOptionDependent(factory, suicidalitygrsop4, suicidalityOffsetDate);
        createOptionDependent(factory, suicidalitygrsop5, suicidalityOffsetDate);
        createOptionDependent(factory, suicidalitygrsop6, suicidalityOffsetDate);

//      Suicidality Frequency and Duration
        OptionEntry suicidalityfd = factory.createOptionEntry("Suicidality Frequency and Duration",
                "Frequency and Duration");
        suicidalityfd.setOptionCodesDisplayed(true);
        caarms.addEntry(suicidalityfd);
        suicidalityfd.setSection(suicidalitySection);
        Option suicidalityfdop0 = factory.createOption("Absent", 0);
        suicidalityfd.addOption(suicidalityfdop0);
        buildFrequencyOptions(factory, suicidalityfd);
        suicidalityfd.setEntryStatus(EntryStatus.DISABLED);
        suicidalityfd.setDefaultValue(suicidalityfdop0);
        createOptionDependent(factory, suicidalitygrsop1, suicidalityfd);
        createOptionDependent(factory, suicidalitygrsop2, suicidalityfd);
        createOptionDependent(factory, suicidalitygrsop3, suicidalityfd);
        createOptionDependent(factory, suicidalitygrsop4, suicidalityfd);
        createOptionDependent(factory, suicidalitygrsop5, suicidalityfd);
        createOptionDependent(factory, suicidalitygrsop6, suicidalityfd);

//      Suicidality Pattern of Symptoms
        OptionEntry suicidalitypos = factory.createOptionEntry("Suicidality Pattern of Symptoms",
                "Pattern of Symptoms");
        suicidalitypos.setOptionCodesDisplayed(true);
        caarms.addEntry(suicidalitypos);
        suicidalitypos.setSection(suicidalitySection);
        Option suicidalityposop1 = factory.createOption("No relation to substance use noted", 0);
        suicidalitypos.addOption(suicidalityposop1);
        buildSymptomsOptions(factory, suicidalitypos);
        suicidalitypos.setEntryStatus(EntryStatus.DISABLED);
        suicidalitypos.setDefaultValue(suicidalityposop1);
        createOptionDependent(factory, suicidalitygrsop1, suicidalitypos);
        createOptionDependent(factory, suicidalitygrsop2, suicidalitypos);
        createOptionDependent(factory, suicidalitygrsop3, suicidalitypos);
        createOptionDependent(factory, suicidalitygrsop4, suicidalitypos);
        createOptionDependent(factory, suicidalitygrsop5, suicidalitypos);
        createOptionDependent(factory, suicidalitygrsop6, suicidalitypos);

        // GAF
        Section gafSection = factory.createSection("GAF section");
        caarms.addSection(gafSection);
        gafSection.setDisplayText("GAF");
        SectionOccurrence gafSectionOcc = factory.createSectionOccurrence(
                "GAF Section Occurrence");
        gafSection.addOccurrence(gafSectionOcc);

        NarrativeEntry instructions = factory.createNarrativeEntry("Instructions",
                "Instructions: Enter a value between 0 and 100: See ratings in " +
                        "study tools section for details of how to rate each scale.");
        caarms.addEntry(instructions);
        instructions.setSection(gafSection);

        String description = "Enter a value between 0 and 100.";

        NumericEntry currentScore = factory.createNumericEntry("Current Score",
                "Current Score");
        caarms.addEntry(currentScore);
        currentScore.setSection(gafSection);
        currentScore.setLabel("1");
        currentScore.setDescription(description);
        currentScore.addValidationRule(zeroToHundred);

        NumericEntry highestInYear = factory.createNumericEntry(
                "Highest In Year", "Highest Score in Past Year");
		caarms.addEntry(highestInYear);
		highestInYear.setSection(gafSection);
		highestInYear.setLabel("2");
		highestInYear.setDescription(description);
		highestInYear.addValidationRule(zeroToHundred);

        // Inclusion Criteria Section
        Section inclusionCriteriaSection = factory.createSection("Inclusion Criteria Section");
        caarms.addSection(inclusionCriteriaSection);
        inclusionCriteriaSection.setDisplayText("Inclusion Criteria");
        SectionOccurrence inclusionCriteriaSectionOcc = factory.createSectionOccurrence("Inclusion Criteria Section Occurrence");
        inclusionCriteriaSection.addOccurrence(inclusionCriteriaSectionOcc);

        NarrativeEntry group1 = factory.createNarrativeEntry("Group 1", "Vulnerability Group");
        group1.setSection(inclusionCriteriaSection);
        caarms.addEntry(group1);
        group1.setLabel("1");

        OptionEntry g1TRF = factory.createOptionEntry("Trait risk factor",
                "Family history of psychosis in the first degree OR Schizotypal Personality Disorder " +
                        "in identified patient");
        caarms.addEntry(g1TRF);
        g1TRF.setSection(inclusionCriteriaSection);
        g1TRF.addOption(factory.createOption("No", 0));
        g1TRF.addOption(factory.createOption("Yes", 1));

        DerivedEntry g1GAF = factory.createDerivedEntry("Group 1 GAF change",
                "30% drop in GAF score from premorbid level, sustained for a month, occurred within past " +
                        "12 months OR GAF score of 50 or less for past 12 months or longer");
        caarms.addEntry(g1GAF);
        g1GAF.setSection(inclusionCriteriaSection);
        g1GAF.addVariable("a", currentScore);
        g1GAF.addVariable("b", highestInYear);
        g1GAF.setFormula("if((((b<=50)&&(a<=50))||(a<=((b*70)/100))), 1, 0)");

        DerivedEntry g1Threshold = factory.createDerivedEntry(
                "Vulnerability Group Criterion", "Criterion met for group 1 (Vulnerability Group) - 0 = NO, 1 = YES");
        caarms.addEntry(g1Threshold);
        g1Threshold.setSection(inclusionCriteriaSection);
        g1Threshold.addVariable("a", g1TRF);
        g1Threshold.addVariable("b", g1GAF);
        g1Threshold.setFormula("if((a+b==2),1,0)");

        //Group2
        NarrativeEntry group2 = factory.createNarrativeEntry("Group 2", "Attenuated Psychosis Group");
        group2.setSection(inclusionCriteriaSection);
        caarms.addEntry(group2);
        group2.setLabel("2");

        NarrativeEntry sub2a = factory.createNarrativeEntry("Sub Group 2a", "Subthreshold intensity");
        sub2a.setSection(inclusionCriteriaSection);
        sub2a.setLabel("2a");
        caarms.addEntry(sub2a);

        DerivedEntry g2a1 = factory.createDerivedEntry(
                "Group 2a1", "Global rating scale score of " +
                "3-5 on 'Unusual Thought Content', 3-5 on 'Non-Bizarre ideas', 3-4 on " +
                "'Perceptual Abnormalities' subscale OR 4-5 on 'Disorganised Speech' subscales " +
                "of the CAARMS PLUS Frequency Scale Score of greater than or equal " +
                "to 3 on 'Unusual Thought Content', 'Non-bizarre Ideas', " +
                "'Perceptual Abnormalities' OR 'Disorganised Speech' subscales " +
                "- 0 = NO, 1 = YES.");
        caarms.addEntry(g2a1);
        g2a1.setSection(inclusionCriteriaSection);
        g2a1.addVariable("a", utcgrs);
        g2a1.addVariableDefault("a", new NumericValue(0.0));
        g2a1.addVariable("b", nbigrs);
        g2a1.addVariableDefault("b", new NumericValue(0.0));
        g2a1.addVariable("c", perabgrs);
        g2a1.addVariableDefault("c", new NumericValue(0.0));
        g2a1.addVariable("d", disspgrs);
        g2a1.addVariableDefault("d", new NumericValue(0.0));
        g2a1.addVariable("e", utcfd);
        g2a1.addVariable("f", nbifd);
        g2a1.addVariable("g", perabfd);
        g2a1.addVariable("h", disspfd);
        g2a1.setFormula("if((((a>=3)&&(a<=5)&&(e>=3))||((b>=3)&&(b<=5)&&(f>=3))||((c>=3)&&(c<=4)&&(g>=3))||((d>=4)&&(d<=5)&&(h>=3))), 1, 0)");

        OptionEntry g2a1ForWeek = factory.createOptionEntry("Group 2a1 For a Week",
                "FOR AT LEAST A WEEK");
        caarms.addEntry(g2a1ForWeek);
        g2a1ForWeek.setSection(inclusionCriteriaSection);
        g2a1ForWeek.addOption(factory.createOption("No", 0));
        g2a1ForWeek.addOption(factory.createOption("Yes", 1));

        DerivedEntry g2a2 = factory.createDerivedEntry(
                "Group 2a2", "OR Global rating scale score of " +
                "3-5 on 'Unusual Thought Content', 3-5 on 'Non-Bizarre ideas', 3-4 on " +
                "'Perceptual Abnormalities' subscale OR 4-5 on 'Disorganised Speech' subscales " +
                "of the CAARMS PLUS Frequency Scale Score equal " +
                "to 2 on 'Unusual Thought Content', 'Non-bizarre Ideas', " +
                "'Perceptual Abnormalities' OR 'Disorganised Speech' subscales of the CAARMS " +
                "(experienced a minimum of four times in total)" +
                "- 0 = NO, 1 = YES.");
        caarms.addEntry(g2a2);
        g2a2.setSection(inclusionCriteriaSection);
        g2a2.addVariable("a", utcgrs);
        g2a2.addVariableDefault("a", new NumericValue(0.0));
        g2a2.addVariable("b", nbigrs);
        g2a2.addVariableDefault("b", new NumericValue(0.0));
        g2a2.addVariable("c", perabgrs);
        g2a2.addVariableDefault("c", new NumericValue(0.0));
        g2a2.addVariable("d", disspgrs);
        g2a2.addVariableDefault("d", new NumericValue(0.0));
        g2a2.addVariable("e", utcfd);
        g2a2.addVariable("f", nbifd);
        g2a2.addVariable("g", perabfd);
        g2a2.addVariable("h", disspfd);
        g2a2.addVariable("i", utcFourTimes);
        g2a2.addVariable("j", nbiFourTimes);
        g2a2.addVariable("k", perabFourTimes);
        g2a2.addVariable("l", disspFourTimes);
        g2a2.setFormula("if((((a>=3)&&(a<=5)&&(e==2)&&(i))||((b>=3)&&(b<=5)&&(f==2)&&(j))||((c>=3)&&(c<=4)&&(g==2)&&(k))||((d>=4)&&(d<=5)&&(h==2)&&(l))), 1, 0)");

        NarrativeEntry sub2b = factory.createNarrativeEntry("Sub Group 2b", "Subthreshold frequency");
        sub2b.setSection(inclusionCriteriaSection);
        sub2b.setLabel("2b");
        caarms.addEntry(sub2b);

        DerivedEntry g2b = factory.createDerivedEntry(
                "Group 2b GRS", "Global rating scale score of 6 on " +
                "'Unusual Thought Content', OR 6 on 'Non-Bizarre ideas', OR 5 or 6 on " +
                "'Perceptual Abnormalities' subscale OR 6 on 'Disorganised Speech' subscales " +
                "of the CAARMS PLUS Frequency Scale Score equal " +
                "to 3 on 'Unusual Thought Content', 'Non-bizarre Ideas', " +
                "'Perceptual Abnormalities' or 'Disorganised Speech' subscales " +
                "- 0 = NO, 1 = YES.");
        caarms.addEntry(g2b);
        g2b.setSection(inclusionCriteriaSection);
        g2b.addVariable("a", utcgrs);
        g2b.addVariableDefault("a", new NumericValue(0.0));
        g2b.addVariable("b", nbigrs);
        g2b.addVariableDefault("b", new NumericValue(0.0));
        g2b.addVariable("c", perabgrs);
        g2b.addVariableDefault("c", new NumericValue(0.0));
        g2b.addVariable("d", disspgrs);
        g2b.addVariableDefault("d", new NumericValue(0.0));
        g2b.addVariable("a1", utcfd);
        g2b.addVariable("b1", nbifd);
        g2b.addVariable("c1", perabfd);
        g2b.addVariable("d1", disspfd);
        g2b.setFormula("if((((a==6)&&(a1==3))||((b==6)&&(b1==3))||((c>=5)&&(c1==3))||((d==6)&&(d1==3))),1,0)");


        OptionEntry g2SympOcc = factory.createOptionEntry("Group 2 symptoms occurence",
                "Symptoms present in past year");
        caarms.addEntry(g2SympOcc);
        g2SympOcc.setSection(inclusionCriteriaSection);
        g2SympOcc.addOption(factory.createOption("No", 0));
        g2SympOcc.addOption(factory.createOption("Yes", 1));

        DerivedEntry g2Threshold = factory.createDerivedEntry(
                "Attenuated Psychosis Group Criterion", "Criterion met for group 2 (Attenuated Psychosis Group) - 0 = NO, 1 = YES");
        caarms.addEntry(g2Threshold);
        g2Threshold.setSection(inclusionCriteriaSection);
        g2Threshold.addVariable("a", g2a1);
        g2Threshold.addVariable("a1", g2a1ForWeek);
        g2Threshold.addVariable("b", g2a2);
        g2Threshold.addVariable("c", g2b);
        g2Threshold.addVariable("d", g2SympOcc);
        g2Threshold.setFormula("if(((((a==1)&&(a1==1))||(b==1)||(c==1))&&(d==1)),1,0)");

        NarrativeEntry group3 = factory.createNarrativeEntry("Group 3", "BLIPS Group");
        group3.setSection(inclusionCriteriaSection);
        caarms.addEntry(group3);
        group3.setLabel("3");

        DerivedEntry g3SeverityScore = factory.createDerivedEntry(
                "Group 3 GRS", "Global rating scale score of 6 on " +
                "'Unusual Thought Content', OR 6 on 'Non-Bizarre ideas', OR 5 or 6 on " +
                "'Perceptual Abnormalities' subscale OR 6 on 'Disorganised Speech' subscales " +
                "of the CAARMS PLUS Frequency Scale Score of greater than or equal " +
                "to 4 on 'Unusual Thought Content', 'Non-bizarre Ideas', " +
                "'Perceptual Abnormalities' or 'Disorganised Speech' subscales " +
                "- 0 = NO, 1 = YES.");
        caarms.addEntry(g3SeverityScore);
        g3SeverityScore.setSection(inclusionCriteriaSection);
        g3SeverityScore.addVariable("a", utcgrs);
        g3SeverityScore.addVariableDefault("a", new NumericValue(0.0));
        g3SeverityScore.addVariable("b", nbigrs);
        g3SeverityScore.addVariableDefault("b", new NumericValue(0.0));
        g3SeverityScore.addVariable("c", perabgrs);
        g3SeverityScore.addVariableDefault("c", new NumericValue(0.0));
        g3SeverityScore.addVariable("d", disspgrs);
        g3SeverityScore.addVariableDefault("d", new NumericValue(0.0));
        g3SeverityScore.addVariable("a1", utcfd);
        g3SeverityScore.addVariable("b1", nbifd);
        g3SeverityScore.addVariable("c1", perabfd);
        g3SeverityScore.addVariable("d1", disspfd);
        g3SeverityScore.setFormula("if((((a==6)&&(a1>=4))||((b==6)&&(b1>=4))||((c>=5)&&(c1>=4))||((d==6)&&(d1>=4))),1,0)");

        OptionEntry g3SympDur = factory.createOptionEntry("Group 3 symptoms duration",
                "Each episode of symptoms is present for less than one week and symptoms " +
                        "spontaneously remit on every occasion");
        caarms.addEntry(g3SympDur);
        g3SympDur.setSection(inclusionCriteriaSection);
        g3SympDur.addOption(factory.createOption("No", 0));
        g3SympDur.addOption(factory.createOption("Yes", 1));

        OptionEntry g3SympOcc = factory.createOptionEntry("Group 3 symptoms occurence",
                "Symptoms occurred during the last year ");
        caarms.addEntry(g3SympOcc);
        g3SympOcc.setSection(inclusionCriteriaSection);
        g3SympOcc.addOption(factory.createOption("No", 0));
        g3SympOcc.addOption(factory.createOption("Yes", 1));

        DerivedEntry g3Threshold = factory.createDerivedEntry(
                "BLIPS Group Criterion", "Criterion met for group 3 (BLIPS Group) - 0 = NO, 1 = YES");
        caarms.addEntry(g3Threshold);
        g3Threshold.setSection(inclusionCriteriaSection);
        g3Threshold.addVariable("a", g3SeverityScore);
        g3Threshold.addVariable("c", g3SympDur);
        g3Threshold.addVariable("d", g3SympOcc);
        g3Threshold.setFormula("if((a+c+d==3),1,0)");

        // Psychosis Threshold Section
        Section psychosisThresholdSection = factory.createSection("Psychosis Threshold / Anti-Psychotic Threshold Section");
        caarms.addSection(psychosisThresholdSection);
        psychosisThresholdSection.setDisplayText("Psychosis Threshold / Anti-Psychotic Treatment Threshold");
        SectionOccurrence psychosisThresholdSectionOcc = factory.createSectionOccurrence("Psychosis Threshold / Anti-Psychotic Threshold Section Occurrence");
        psychosisThresholdSection.addOccurrence(psychosisThresholdSectionOcc);

        DerivedEntry severityScorePsychosis = factory.createDerivedEntry(
                "Severity Score Psychosis", "Severity scales score of 6 on " +
                "'Unusual Thought Content', OR 6 on 'Non-Bizarre ideas', OR 5 or 6 on " +
                "'Perceptual Abnormalities' subscale OR 6 on 'Disorganised Speech' subscales " +
                "of the CAARMS PLUS " +
                "Frequency Scale Score of greater than or equal " +
                "to 4 on 'Unusual Thought Content', 'Non-bizarre Ideas', " +
                "'Perceptual Abnormalities' or 'Disorganised Speech' subscales " +
                "- 0 = NO, 1 = YES.");
        caarms.addEntry(severityScorePsychosis);
        severityScorePsychosis.setSection(psychosisThresholdSection);
        severityScorePsychosis.addVariable("a", utcgrs);
        severityScorePsychosis.addVariableDefault("a", new NumericValue(0.0));
        severityScorePsychosis.addVariable("b", nbigrs);
        severityScorePsychosis.addVariableDefault("b", new NumericValue(0.0));
        severityScorePsychosis.addVariable("c", perabgrs);
        severityScorePsychosis.addVariableDefault("c", new NumericValue(0.0));
        severityScorePsychosis.addVariable("d", disspgrs);
        severityScorePsychosis.addVariableDefault("d", new NumericValue(0.0));
        severityScorePsychosis.addVariable("a1", utcfd);
        severityScorePsychosis.addVariable("b1", nbifd);
        severityScorePsychosis.addVariable("c1", perabfd);
        severityScorePsychosis.addVariable("d1", disspfd);
        severityScorePsychosis.setFormula("if((((a==6)&&(a1>=4))||((b==6)&&(b1>=4))||((c>=5)&&(c1>=4))||((d==6)&&(d1>=4))),1,0)");

        OptionEntry symptomsDurationPsychosis = factory.createOptionEntry("Psychosis symptoms duration",
                "Symptoms present for longer than one week");
        caarms.addEntry(symptomsDurationPsychosis);
        symptomsDurationPsychosis.setSection(psychosisThresholdSection);
        symptomsDurationPsychosis.addOption(factory.createOption("No", 0));
        symptomsDurationPsychosis.addOption(factory.createOption("Yes", 1));

        DerivedEntry psychosisThreshold = factory.createDerivedEntry(
                "Psychosis Threshold", "Psychosis threshold criterion met - 0 = NO, 1 = YES");
        caarms.addEntry(psychosisThreshold);
        psychosisThreshold.setSection(psychosisThresholdSection);
        psychosisThreshold.addVariable("a", severityScorePsychosis);
        psychosisThreshold.addVariable("c", symptomsDurationPsychosis);
        psychosisThreshold.setFormula("if((a+c==2),1,0)");

        // 'Break Blind' Section
        Section breakBlindSection = factory.createSection("Break Blind Section");
        caarms.addSection(breakBlindSection);
        breakBlindSection.setDisplayText("Study Withdrawal ('Break Blind') Threshold");
        SectionOccurrence breakBlindSectionOcc = factory.createSectionOccurrence("Break Blind Section Occurrence");
        breakBlindSection.addOccurrence(breakBlindSectionOcc);

        DerivedEntry severityScoreWithdrawal = factory.createDerivedEntry(
                "Severity Score Withdrawal", "Severity scales score of 6 on 'Aggression/Dangerous Behaviour' or score of 5 on 'Suicidality/Self-harm subscales': " +
                "Consider Breaking blind  - 0 = NO, 1 = YES. NOTE:" +
                " This should be considered independently from the level of psychosis");
        caarms.addEntry(severityScoreWithdrawal);
        severityScoreWithdrawal.setSection(breakBlindSection);
        severityScoreWithdrawal.addVariable("a", agdabegrs);
        severityScoreWithdrawal.addVariable("b", suicidalitygrs);
        severityScoreWithdrawal.setFormula("if(((a==6)||(b==5)),1,0)");

        DerivedEntry severityScoreWithdrawal2 = factory.createDerivedEntry(
                "Severity Score Withdrawal 2", "Severity scales score of 6 on 'Suicidality/Self-harm subscales': IMMEDIATE breaking blind - 0 = NO, 1 = YES. NOTE:" +
                " This should be considered independently from the level of psychosis");
        caarms.addEntry(severityScoreWithdrawal2);
        severityScoreWithdrawal2.setSection(breakBlindSection);
        severityScoreWithdrawal2.addVariable("b", suicidalitygrs);
        severityScoreWithdrawal2.setFormula("if((b>=6),1,0)");

        DerivedEntry withdrawalThreshold = factory.createDerivedEntry(
                "Withdrawal Threshold", "Study withdrawal threshold criterion met - 0 = NO, 1 = YES");
        caarms.addEntry(withdrawalThreshold);
        withdrawalThreshold.setSection(breakBlindSection);
        withdrawalThreshold.addVariable("a", severityScoreWithdrawal);
        withdrawalThreshold.addVariable("b", severityScoreWithdrawal2);
        withdrawalThreshold.setFormula("if(((a==1)||(b==1)),1,0)");

        return caarms;
    }
    static void buildRatingOptions(Factory factory, OptionEntry q){
        Option op1 = factory.createOption("Questionable", 1);
        q.addOption(op1);
        Option op2 = factory.createOption("Mild", 2);
        q.addOption(op2);
        Option op3 =  factory.createOption("Moderate", 3);
        q.addOption(op3);
        Option op4 = factory.createOption("Moderately severe", 4);
        q.addOption(op4);
        Option op5 = factory.createOption("Severe", 5);
        q.addOption(op5);
        Option op6 = factory.createOption("Psychotic and Severe", 6);
        q.addOption(op6);
    }
    static void buildRatingOptions1(Factory factory, OptionEntry q){
        Option op1 = factory.createOption("Questionable", 1);
        q.addOption(op1);
        Option op2 = factory.createOption("Mild", 2);
        q.addOption(op2);
        Option op3 =  factory.createOption("Moderate", 3);
        q.addOption(op3);
        Option op4 = factory.createOption("Moderately severe", 4);
        q.addOption(op4);
        Option op5 = factory.createOption("Psychotic but not severe", 5);
        q.addOption(op5);
        Option op6 = factory.createOption("Psychotic and severe", 6);
        q.addOption(op6);
    }
    static void buildRatingOptions2(Factory factory, OptionEntry q){
        Option op1 = factory.createOption("Questionable", 1);
        q.addOption(op1);
        Option op2 = factory.createOption("Mild", 2);
        q.addOption(op2);
        Option op3 =  factory.createOption("Moderate", 3);
        q.addOption(op3);
        Option op4 = factory.createOption("Moderately severe", 4);
        q.addOption(op4);
        Option op5 = factory.createOption("Severe", 5);
        q.addOption(op5);
        Option op6 = factory.createOption("Extreme", 6);
        q.addOption(op6);
    }
    static void buildFrequencyOptions(Factory factory, OptionEntry q){
        Option op1 = factory.createOption("Less then once a month", 1);
        q.addOption(op1);
        Option op2 = factory.createOption("Once a month to twice a week - less than one hour per occasion", 2);
        q.addOption(op2);
        Option op3 =  factory.createOption("Once a month to twice a week - more than one hour per occaision OR 3 to 6 times a week - less then one hour per occasion", 3);
        q.addOption(op3);
        Option op4 = factory.createOption("3 to 6 times a week - more than an hour per occasion OR daily - less than an hour per occasion", 4);
        q.addOption(op4);
        Option op5 = factory.createOption("Daily - more than an hour per occasion OR several times a day", 5);
        q.addOption(op5);
        Option op6 = factory.createOption("Continuous", 6);
        q.addOption(op6);
    }
    static void buildSymptomsOptions(Factory factory, OptionEntry q){
        Option op2 = factory.createOption("Occurs in relation to substance use and at other times as well", 1);
        q.addOption(op2);
        Option op3 = factory.createOption("Noted only in relation to substance use", 2);
        q.addOption(op3);
    }
    static void buildYesNoOptions(Factory factory, OptionEntry q){
        Option op1 = factory.createOption("No", 0);
        q.addOption(op1);
        Option op2 = factory.createOption("Yes", 1);
        q.addOption(op2);
    }
}

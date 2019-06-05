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

import org.psygrid.common.UnitWrapper;
import org.psygrid.common.ValidationRulesWrapper;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.AssessmentForm;

public class DrugCheck extends AssessmentForm {
    public static Document createDocument(Factory factory) {

        Document doc = factory.createDocument("Drug Check", "Drug Check");

        createDocumentStatuses(factory, doc);

        ValidationRule oneToSeven = ValidationRulesWrapper.instance().getRule("OneToSeven");

        // General section
        Section generalSec = factory.createSection("General Section");
        doc.addSection(generalSec);
        generalSec.setDisplayText("General");
        SectionOccurrence generalSecOcc = factory
                .createSectionOccurrence("General section occurrence");
        generalSec.addOccurrence(generalSecOcc);

        NarrativeEntry lastThreeMonths = factory
                .createNarrativeEntry("In the last 3 months",
                        "During the last 3 months have you had any...");
        doc.addEntry(lastThreeMonths);
        lastThreeMonths.setSection(generalSec);

        // alcohol questions
        OptionEntry alcohol = factory.createOptionEntry("alcohol",
                "Alchoholic drinks?");
        doc.addEntry(alcohol);
        alcohol.setSection(generalSec);
        alcohol.addOption(factory.createOption("No", "No", 0));
        Option alcoholYes = factory.createOption("Yes", "Yes - Please complete the 'Alcohol Audit' section", 1);
        alcohol.addOption(alcoholYes);

        // Caffine questions
        OptionEntry caffine = factory.createOptionEntry("Caffine",
                "Tea, coffee, or cola drinks?");
        doc.addEntry(caffine);
        caffine.setSection(generalSec);
        caffine.addOption(factory.createOption("No", "No", 0));
        Option caffineYes = factory.createOption("Yes", "Yes", 1);
        caffine.addOption(caffineYes);

        OptionEntry caffineFreq = factory.createOptionEntry("Frequency",
                "How often have you had them?", EntryStatus.DISABLED);
        doc.addEntry(caffineFreq);
        caffineFreq.setSection(generalSec);
        caffineFreq.addOption(factory.createOption("None",0));
        caffineFreq.addOption(factory.createOption("Occasional user (less than weekly)",1));
        caffineFreq.addOption(factory.createOption("Regular user (1-3 times weekly)",2));
        caffineFreq.addOption(factory.createOption("Frequent user (almost everyday)",3));
        createOptionDependent(factory, caffineYes, caffineFreq);

        NumericEntry teaDailyFreq = factory.createNumericEntry("Daily frequency tea/coffee",
                "How many cups of tea or coffee do you drink per day?",
                EntryStatus.DISABLED);
        doc.addEntry(teaDailyFreq);
        teaDailyFreq.setSection(generalSec);
        teaDailyFreq.addUnit(UnitWrapper.instance().getUnit("cups per day"));
        createOptionDependent(factory, caffineYes, teaDailyFreq);

        NumericEntry colaDailyFreq = factory.createNumericEntry("Daily frequency cola",
                "How many cola drinks (e.g. coke, pepsi) do you drink per day?",
                EntryStatus.DISABLED);
        doc.addEntry(colaDailyFreq);
        colaDailyFreq.setSection(generalSec);
        colaDailyFreq.addUnit(UnitWrapper.instance().getUnit("cans per day"));
        createOptionDependent(factory, caffineYes, colaDailyFreq);

        // Nicotine questions
        OptionEntry nicotine = factory.createOptionEntry("Nicotine",
                "Cigarettes?");
        doc.addEntry(nicotine);
        nicotine.setSection(generalSec);
        nicotine.addOption(factory.createOption("No", "No", 0));
        Option nicotineYes = factory.createOption("Yes", "Yes", 1);
        nicotine.addOption(nicotineYes);

        OptionEntry nicotineFreq = factory.createOptionEntry("Frequency",
                "How often have you had them?", EntryStatus.DISABLED);
        doc.addEntry(nicotineFreq);
        nicotineFreq.setSection(generalSec);
        nicotineFreq.addOption(factory.createOption("None",0));
        nicotineFreq.addOption(factory.createOption("Occasional user (less than weekly)",1));
        nicotineFreq.addOption(factory.createOption("Regular user (1-3 times weekly)",2));
        nicotineFreq.addOption(factory.createOption("Frequent user (almost everyday)",3));
        createOptionDependent(factory, nicotineYes, nicotineFreq);

        NumericEntry nicotineDailyFreq = factory.createNumericEntry("Daily frequency cigarettes",
                "How many many cigarettes do you have per day?",
                EntryStatus.DISABLED);
        doc.addEntry(nicotineDailyFreq);
        nicotineDailyFreq.setSection(generalSec);
        nicotineDailyFreq.addUnit(UnitWrapper.instance().getUnit("cigs per day"));
        createOptionDependent(factory, nicotineYes, nicotineDailyFreq);

        TextEntry fagsBrand = factory.createTextEntry("Cigarette Brand", "Brand", EntryStatus.DISABLED);
        doc.addEntry(fagsBrand);
        fagsBrand.setSection(generalSec);
        createOptionDependent(factory, nicotineYes, fagsBrand);

        TextEntry fagsType = factory.createTextEntry("Cigarette Type", "Type", EntryStatus.DISABLED);
        doc.addEntry(fagsType);
        fagsType.setSection(generalSec);
        createOptionDependent(factory, nicotineYes, fagsType);

        // Sedatives questions
        OptionEntry sedatives = factory.createOptionEntry("Sedatives",
                "Sleeping tablets or sedatives? (like valium or normison)");
        doc.addEntry(sedatives);
        sedatives.setSection(generalSec);
        sedatives.addOption(factory.createOption("No", "No", 0));
        Option sedativesYes = factory.createOption("Yes", "Yes", 1);
        sedatives.addOption(sedativesYes);

        OptionEntry sedativesFreq = factory.createOptionEntry("Frequency",
                "How often have you had them?", EntryStatus.DISABLED);
        doc.addEntry(sedativesFreq);
        sedativesFreq.setSection(generalSec);
        sedativesFreq.addOption(factory.createOption("None",0));
        sedativesFreq.addOption(factory.createOption("Occasional user (less than weekly)",1));
        sedativesFreq.addOption(factory.createOption("Regular user (1-3 times weekly)",2));
        sedativesFreq.addOption(factory.createOption("Frequent user (almost everyday)",3));
        createOptionDependent(factory, sedativesYes, sedativesFreq);

        NumericEntry sedativesDailyFreq = factory.createNumericEntry("Daily frequency sedatives",
                "How many times a day do you take the sleeping tablets/sedatives?",
                EntryStatus.DISABLED);
        doc.addEntry(sedativesDailyFreq);
        sedativesDailyFreq.setSection(generalSec);
        createOptionDependent(factory, sedativesYes, sedativesDailyFreq);

        NumericEntry sedativesQty = factory.createNumericEntry("Amount per week",
                "How much do you usually have at each time of the day?",
                EntryStatus.DISABLED);
        doc.addEntry(sedativesQty);
        sedativesQty.setSection(generalSec);
        sedativesQty.addUnit(UnitWrapper.instance().getUnit("mg"));
        sedativesQty.addUnit(UnitWrapper.instance().getUnit("tabs"));
        createOptionDependent(factory, sedativesYes, sedativesQty);

        TextEntry sedativesName = factory.createTextEntry("Sedatives name",
                "What is the name of the sleeping tables/sedatives?",
                EntryStatus.DISABLED);
        doc.addEntry(sedativesName);
        sedativesName.setSection(generalSec);
        createOptionDependent(factory, sedativesYes, sedativesName);

        // Painkillers questions
        OptionEntry painkillers = factory.createOptionEntry("Painkillers",
                "Other painkillers");
        doc.addEntry(painkillers);
        painkillers.setSection(generalSec);
        painkillers.addOption(factory.createOption("No", "No", 0 ));
        Option painkillersYes = factory.createOption("Yes", "Yes", 1);
        painkillers.addOption(painkillersYes);

        OptionEntry painkillersFreq = factory.createOptionEntry("Frequency",
                "How often have you had them?", EntryStatus.DISABLED);
        doc.addEntry(painkillersFreq);
        painkillersFreq.setSection(generalSec);
        painkillersFreq.addOption(factory.createOption("None",0));
        painkillersFreq.addOption(factory.createOption("Occasional user (less than weekly)",1));
        painkillersFreq.addOption(factory.createOption("Regular user (1-3 times weekly)",2));
        painkillersFreq.addOption(factory.createOption("Frequent user (almost everyday)",3));
        createOptionDependent(factory, painkillersYes, painkillersFreq);

        NumericEntry painkillersDailyFreq = factory.createNumericEntry("Daily frequency painkillers",
                "How many times a day do you take the painkillers?",
                EntryStatus.DISABLED);
        doc.addEntry(painkillersDailyFreq);
        painkillersDailyFreq.setSection(generalSec);
        createOptionDependent(factory, painkillersYes, painkillersDailyFreq);

        NumericEntry painkillersQty = factory.createNumericEntry("Amount per week",
                "How much do you usually have at each time of the day?",
                EntryStatus.DISABLED);
        doc.addEntry(painkillersQty);
        painkillersQty.setSection(generalSec);
        painkillersQty.addUnit(UnitWrapper.instance().getUnit("mg"));
        painkillersQty.addUnit(UnitWrapper.instance().getUnit("tabs"));
        createOptionDependent(factory, painkillersYes, painkillersQty);

        TextEntry painkillersName = factory.createTextEntry("Painkillers name",
                "What is the name of the painkillers?",
                EntryStatus.DISABLED);
        doc.addEntry(painkillersName);
        painkillersName.setSection(generalSec);
        createOptionDependent(factory, painkillersYes, painkillersName);

        // Hash questions
        OptionEntry hash = factory.createOptionEntry("Marijuana",
                "Marijuana, cannabis, or hash?");
        doc.addEntry(hash);
        hash.setSection(generalSec);
        hash.addOption(factory.createOption("No", "No", 0 ));
        Option hashYes = factory.createOption("Yes", "Yes", 1);
        hash.addOption(hashYes);

        OptionEntry hashFreq = factory.createOptionEntry("Frequency",
                "How often have you had them?", EntryStatus.DISABLED);
        doc.addEntry(hashFreq);
        hashFreq.setSection(generalSec);
        hashFreq.addOption(factory.createOption("None",0));
        hashFreq.addOption(factory.createOption("Occasional user (less than weekly)",1));
        hashFreq.addOption(factory.createOption("Regular user (1-3 times weekly)",2));
        hashFreq.addOption(factory.createOption("Frequent user (almost everyday)",3));
        createOptionDependent(factory, hashYes, hashFreq);


        NumericEntry hashQuantity = factory.createNumericEntry("Quantity",
                "How much do you usually have?", EntryStatus.DISABLED);
        doc.addEntry(hashQuantity);
        hashQuantity.setSection(generalSec);
        hashQuantity.addUnit(UnitWrapper.instance().getUnit("cones per day"));
        hashQuantity.addUnit(UnitWrapper.instance().getUnit("joints per day"));
        hashQuantity.addUnit(UnitWrapper.instance().getUnit("spliffs per day"));
        createOptionDependent(factory, hashYes, hashQuantity);

        OptionEntry hashType = factory.createOptionEntry("Hash Type", "Type",
                EntryStatus.DISABLED);
        doc.addEntry(hashType);
        hashType.setSection(generalSec);
        hashType.addOption(factory.createOption("Leaf", "Leaf", 0));
        hashType.addOption(factory.createOption("Head", "Head", 1));
        hashType.addOption(factory.createOption("Hash", "Hash", 2));
        Option hashTypeOther = factory.createOption("Other",
                "Other (please specify)", 3);
        hashTypeOther.setTextEntryAllowed(true);
        hashType.addOption(hashTypeOther);
        createOptionDependent(factory, hashYes, hashType);

        // Drugs you sniff questions
        OptionEntry sniff = factory.createOptionEntry("Drugs you sniff",
                "Drugs you sniff, like petrol/glue?");
        doc.addEntry(sniff);
        sniff.setSection(generalSec);
        sniff.addOption(factory.createOption("No", "No", 0));
        Option sniffYes = factory.createOption("Yes", "Yes", 1);
        sniff.addOption(sniffYes);

        OptionEntry sniffFreq = factory.createOptionEntry("Frequency",
                "How often have you had them?", EntryStatus.DISABLED);
        doc.addEntry(sniffFreq);
        sniffFreq.setSection(generalSec);
        sniffFreq.addOption(factory.createOption("None",0));
        sniffFreq.addOption(factory.createOption("Occasional user (less than weekly)",1));
        sniffFreq.addOption(factory.createOption("Regular user (1-3 times weekly)",2));
        sniffFreq.addOption(factory.createOption("Frequent user (almost everyday)",3));
        createOptionDependent(factory, sniffYes, sniffFreq);


        OptionEntry sniffType = factory.createOptionEntry("Type", "Type",
                EntryStatus.DISABLED);
        doc.addEntry(sniffType);
        sniffType.setSection(generalSec);
        sniffType.addOption(factory.createOption("Petrol", "Petrol", 0));			//28
        sniffType.addOption(factory.createOption("Glue", "Glue", 1));
        sniffType.addOption(factory.createOption("Aerosol", "Aerosol", 2));
        Option sniffTypeOther = factory.createOption("Other",
                "Other (please specify)", 3);
        sniffTypeOther.setTextEntryAllowed(true);
        sniffType.addOption(sniffTypeOther);
        createOptionDependent(factory, sniffYes, sniffType);

        // LSD questions
        OptionEntry lsd = factory.createOptionEntry("Drugs like LSD",
                "Drugs like LSD?");
        doc.addEntry(lsd);
        lsd.setSection(generalSec);
        lsd.addOption(factory.createOption("No", "No", 0));
        Option lsdYes = factory.createOption("Yes", "Yes", 1);
        lsd.addOption(lsdYes);

        OptionEntry lsdFreq = factory.createOptionEntry("Frequency",
                "How often have you had them?", EntryStatus.DISABLED);
        doc.addEntry(lsdFreq);
        lsdFreq.setSection(generalSec);
        lsdFreq.addOption(factory.createOption("None",0));
        lsdFreq.addOption(factory.createOption("Occasional user (less than weekly)",1));
        lsdFreq.addOption(factory.createOption("Regular user (1-3 times weekly)",2));
        lsdFreq.addOption(factory.createOption("Frequent user (almost everyday)",3));
        createOptionDependent(factory, lsdYes, lsdFreq);

        NumericEntry lsdQuantity = factory.createNumericEntry("Quantity",
                "How much do you usually have?", EntryStatus.DISABLED);
        doc.addEntry(lsdQuantity);
        lsdQuantity.setSection(generalSec);
        lsdQuantity.addUnit(UnitWrapper.instance().getUnit("trips per week"));
        createOptionDependent(factory, lsdYes, lsdQuantity);

        // Speed questions
        OptionEntry speed = factory.createOptionEntry("Speed",
                "Speed, ecstasy, crack or cocaine?");
        doc.addEntry(speed);
        speed.setSection(generalSec);
        speed.addOption(factory.createOption("No", "No", 0));
        Option speedYes = factory.createOption("Yes", "Yes", 1);
        speed.addOption(speedYes);

        OptionEntry speedFreq = factory.createOptionEntry("Frequency",
                "How often have you had them?", EntryStatus.DISABLED);
        doc.addEntry(speedFreq);
        speedFreq.setSection(generalSec);
        speedFreq.addOption(factory.createOption("None",0));
        speedFreq.addOption(factory.createOption("Occasional user (less than weekly)",1));
        speedFreq.addOption(factory.createOption("Regular user (1-3 times weekly)",2));
        speedFreq.addOption(factory.createOption("Frequent user (almost everyday)",3));
        createOptionDependent(factory, speedYes, speedFreq);


        NumericEntry speedQuantity = factory.createNumericEntry("Quantity",
                "How much do you usually have?", EntryStatus.DISABLED);
        doc.addEntry(speedQuantity);
        speedQuantity.setSection(generalSec);
        speedQuantity.addUnit(UnitWrapper.instance().getUnit("g per week"));
        speedQuantity.addUnit(UnitWrapper.instance().getUnit("tabs per week"));
        speedQuantity.addUnit(UnitWrapper.instance().getUnit("hits per week"));
        createOptionDependent(factory, speedYes, speedQuantity);

        OptionEntry speedType = factory.createOptionEntry("Type", "Type",             //35
                EntryStatus.DISABLED);
        doc.addEntry(speedType);
        speedType.setSection(generalSec);
        speedType.addOption(factory.createOption("Pills", "Pills", 0));
        speedType.addOption(factory.createOption("Powder", "Powder", 1));
        speedType.addOption(factory.createOption("Injection", "Injection", 2));
        Option speedTypeOther = factory.createOption("Other",
                "Other (please specify)", 3);
        speedTypeOther.setTextEntryAllowed(true);
        speedType.addOption(speedTypeOther);
        createOptionDependent(factory, speedYes, speedType);

        // Heroin questions
        OptionEntry heroin = factory.createOptionEntry("Heroin",
                "Heroin, morphine or methadone?");
        doc.addEntry(heroin);
        heroin.setSection(generalSec);
        heroin.addOption(factory.createOption("No", "No", 0));
        Option heroinYes = factory.createOption("Yes", "Yes", 1);
        heroin.addOption(heroinYes);

        OptionEntry heroinFreq = factory.createOptionEntry("Frequency",
                "How often have you had them?", EntryStatus.DISABLED);
        doc.addEntry(heroinFreq);
        heroinFreq.setSection(generalSec);
        heroinFreq.addOption(factory.createOption("None",0));
        heroinFreq.addOption(factory.createOption("Occasional user (less than weekly)",1));
        heroinFreq.addOption(factory.createOption("Regular user (1-3 times weekly)",2));
        heroinFreq.addOption(factory.createOption("Frequent user (almost everyday)",3));
        createOptionDependent(factory, heroinYes, heroinFreq);

        NumericEntry heroinQuantity = factory.createNumericEntry("Quantity",
                "How much do you usually have?", EntryStatus.DISABLED);
        doc.addEntry(heroinQuantity);
        heroinQuantity.setSection(generalSec);
        heroinQuantity.addUnit(UnitWrapper.instance().getUnit("g per day"));
        heroinQuantity.addUnit(UnitWrapper.instance().getUnit("ml per day"));
        heroinQuantity.addUnit(UnitWrapper.instance().getUnit("hits per day"));
        heroinQuantity.addUnit(UnitWrapper.instance().getUnit("tabs per day"));
        createOptionDependent(factory, heroinYes, heroinQuantity);

        OptionEntry heroinType = factory.createOptionEntry("Type", "Type",         //39
                EntryStatus.DISABLED);
        doc.addEntry(heroinType);
        heroinType.setSection(generalSec);
        heroinType.addOption(factory.createOption("Pills", "Pills", 0));
        heroinType.addOption(factory.createOption("Powder", "Powder", 1));
        heroinType.addOption(factory.createOption("Liquid", "Liquid", 2));
        heroinType.addOption(factory.createOption("Injection", "Injection", 3));
        Option heroinTypeOther = factory.createOption("Other",
                "Other (please specify)", 4);
        heroinTypeOther.setTextEntryAllowed(true);
        heroinType.addOption(heroinTypeOther);
        createOptionDependent(factory, heroinYes, heroinType);


        // Other drugs questions
        OptionEntry otherDrug = factory.createOptionEntry("Other Drugs",
                "Other Drugs not listed above");
        doc.addEntry(otherDrug);
        otherDrug.setSection(generalSec);
        otherDrug.addOption(factory.createOption("No", "No", 0));
        Option otherDrugYes = factory.createOption("Yes", "Yes", 1);
        otherDrug.addOption(otherDrugYes);

        {
			// Other table
			CompositeEntry otherComp = factory.createComposite("Other drugs",
                    "Other drugs");
			doc.addEntry(otherComp);
			createOptionDependent(factory, otherDrugYes, otherComp);
			otherComp.setEntryStatus(EntryStatus.DISABLED);
			otherComp.setSection(generalSec);
			TextEntry otherType = factory.createTextEntry("Type",
                    "Type of drug", EntryStatus.DISABLED);
			otherComp.addEntry(otherType);

			OptionEntry otherFreq = factory.createOptionEntry("Frequency",
                    "How often have you had them?", EntryStatus.DISABLED);
			otherComp.addEntry(otherFreq);
			otherFreq.setSection(generalSec);
			otherFreq.addOption(factory.createOption("None", 0));
			otherFreq.addOption(factory.createOption(
					"Occasional user (less than weekly)", 1));
			otherFreq.addOption(factory.createOption(
					"Regular user (1-3 times weekly)", 2));
			otherFreq.addOption(factory.createOption(
					"Frequent user (almost everyday)", 3));

			TextEntry quantity = factory.createTextEntry("Quantity",
                    "Quantity", EntryStatus.DISABLED);
			otherComp.addEntry(quantity);
			quantity.setSection(generalSec);
		}

        // Drug that caused most problems question
        OptionEntry mostProblems = factory.createOptionEntry("Drug that caused " +
                "most problems", "You said that you have been using... " +
                "(summarize the drugs that were identified from the list above), " +
                "which of these drugs have caused you the most problems or " +
                "hassles in the last 3 months? Take into consideration the " +
                "various risk factors associated with the substances the " +
                "patient is presently using & select the most problematic drugs " +
                "based on ALL available information.");
        doc.addEntry(mostProblems);
        mostProblems.setSection(generalSec);
        mostProblems.addOption(factory.createOption("Alcohol?" ,"Alcoholic drinks?", 0));
        mostProblems.addOption(factory.createOption("Tea, coffe or cola drinks?" ,"Tea, coffee, or cola drinks?", 1));
        mostProblems.addOption(factory.createOption("Cigarettes?" ,"Cigarettes?", 2));
        mostProblems.addOption(factory.createOption("Sleeping tablets or sedatives?",
                "Sleeping tablets or sedatives? (like valium or normison)", 3));
        mostProblems.addOption(factory.createOption("Painkillers?",
        "Other Painkillers?", 4));
        mostProblems.addOption(factory.createOption("Marijuana, cannabis, or " +
                "hash?", "Marijuana, cannabis, or hash?", 5));
        mostProblems.addOption(factory.createOption("Drugs you sniff, " +
                "like petrol/glue?", "Drugs you sniff, like petrol/glue?", 6));
        mostProblems.addOption(factory.createOption("Drugs like LSD?",
                "Drugs like LSD?", 7));
        mostProblems.addOption(factory.createOption("Speed, ecstasy, crack or " +
                "cocaine?", "Speed, ecstasy, crack or cocaine?", 8));
        mostProblems.addOption(factory.createOption("Heroin, morphine or " +
                "methadone?", "Heroin, morphine or methadone?", 9));
        Option mostProblemsOther = factory.createOption("Other", "Other", 10);
        mostProblemsOther.setTextEntryAllowed(true);
        mostProblems.addOption(mostProblemsOther);


        // Alcohol Audit Section
        Section alcoholSection = factory.createSection("Alcohol List section");
        doc.addSection(alcoholSection);
        alcoholSection.setDisplayText("Alcohol Audit");
        SectionOccurrence alcoholSectionOcc = factory.createSectionOccurrence("Alcohol List section occurrence");
        alcoholSection.addOccurrence(alcoholSectionOcc);

        OptionEntry alcoholFrequency = factory.createOptionEntry(
                "How often drink",
                "How often do you have a drink containing alcohol?");
        doc.addEntry(alcoholFrequency);
        alcoholFrequency.setSection(alcoholSection);
        alcoholFrequency.setLabel("1.");
        alcoholFrequency.addOption(factory.createOption("never", "never", 0));
        alcoholFrequency.addOption(factory.createOption("monthly or less", "monthly or less", 1));
        alcoholFrequency.addOption(factory.createOption("once a week", "once a week", 2));
        alcoholFrequency.addOption(factory.createOption("2 to 4 times a week", "2 to 4 times a week", 3));
        alcoholFrequency.addOption(factory.createOption("5 or more times a week", "5 or more times a week", 4));

        OptionEntry alcoholAmount = factory.createOptionEntry(
                "How much drink",
                "How many 'standard drinks' do you have on a typical day when you are drinking?");
        doc.addEntry(alcoholAmount);
        alcoholAmount.setSection(alcoholSection);
        alcoholAmount.setLabel("2.");
        alcoholAmount.addOption(factory.createOption("one", "1", 0));
        alcoholAmount.addOption(factory.createOption("two", "2", 1));
        alcoholAmount.addOption(factory.createOption("three or four", "3 or 4", 2));
        alcoholAmount.addOption(factory.createOption("five or six", "5 or 6", 3));
        alcoholAmount.addOption(factory.createOption("seven or more", "7 or more", 4));

        OptionEntry bingeDrinking = factory.createOptionEntry(
                "How often binge",
                "How often do you have six or more drinks on one occasion?");
        doc.addEntry(bingeDrinking);
        bingeDrinking.setSection(alcoholSection);
        bingeDrinking.setLabel("3.");
        bingeDrinking.addOption(factory.createOption("never", "never", 0));
        bingeDrinking.addOption(factory.createOption("less than monthly", "less than monthly", 1));
        bingeDrinking.addOption(factory.createOption("monthly", "monthly", 2));
        bingeDrinking.addOption(factory.createOption("weekly", "weekly", 3));
        bingeDrinking.addOption(factory.createOption("daily or almost daily", "daily or almost daily", 4));

        OptionEntry keepDrinking = factory.createOptionEntry(
                "How often not stop",
                "How often during the last three months have you found that you were not able to stop drinking once you had started?");
        doc.addEntry(keepDrinking);
        keepDrinking.setSection(alcoholSection);
        keepDrinking.setLabel("4.");
        keepDrinking.addOption(factory.createOption("never", "never", 0));
        keepDrinking.addOption(factory.createOption("less than monthly", "less than monthly", 1));
        keepDrinking.addOption(factory.createOption("monthly", "monthly", 2));
        keepDrinking.addOption(factory.createOption("weekly", "weekly", 3));
        keepDrinking.addOption(factory.createOption("daily or almost daily", "daily or almost daily", 4));

        OptionEntry failedDrinking = factory.createOptionEntry(
                "How often failed",
                "How often during the last three months have you failed to do what was normally expected of you because of your drinking?");
        doc.addEntry(failedDrinking);
        failedDrinking.setSection(alcoholSection);
        failedDrinking.setLabel("5.");
        failedDrinking.addOption(factory.createOption("never", "never", 0));
        failedDrinking.addOption(factory.createOption("less than monthly", "less than monthly", 1));
        failedDrinking.addOption(factory.createOption("monthly", "monthly", 2));
        failedDrinking.addOption(factory.createOption("weekly", "weekly", 3));
        failedDrinking.addOption(factory.createOption("daily or almost daily", "daily or almost daily", 4));

        OptionEntry morningDrinking = factory.createOptionEntry(
                "How often morning drinking",
                "How often during the last three months have you needed an alcoholic drink in the morning to get yourself going after a heavy drinking session?");
        doc.addEntry(morningDrinking);
        morningDrinking.setSection(alcoholSection);
        morningDrinking.setLabel("6.");
        morningDrinking.addOption(factory.createOption("never", "never", 0));
        morningDrinking.addOption(factory.createOption("less than monthly", "less than monthly", 1));
        morningDrinking.addOption(factory.createOption("monthly", "monthly", 2));
        morningDrinking.addOption(factory.createOption("weekly", "weekly", 3));
        morningDrinking.addOption(factory.createOption("daily or almost daily", "daily or almost daily", 4));

        OptionEntry guiltDrinking = factory.createOptionEntry(
                "How often guilt",
                "How often during the last three months have you had a feeling of guilt or remorse after drinking?");
        doc.addEntry(guiltDrinking);
        guiltDrinking.setSection(alcoholSection);
        guiltDrinking.setLabel("7.");
        guiltDrinking.addOption(factory.createOption("never", "never", 0));
        guiltDrinking.addOption(factory.createOption("less than monthly", "less than monthly", 1));
        guiltDrinking.addOption(factory.createOption("monthly", "monthly", 2));
        guiltDrinking.addOption(factory.createOption("weekly", "weekly", 3));
        guiltDrinking.addOption(factory.createOption("daily or almost daily", "daily or almost daily", 4));

        OptionEntry blackoutDrinking = factory.createOptionEntry(
                "How often blackout",
                "How often during the last three months have you been unable to remember what happened the night before because you had been drinking?");
        doc.addEntry(blackoutDrinking);
        blackoutDrinking.setSection(alcoholSection);
        blackoutDrinking.setLabel("8.");
        blackoutDrinking.addOption(factory.createOption("never", "never", 0));
        blackoutDrinking.addOption(factory.createOption("less than monthly", "less than monthly", 1));
        blackoutDrinking.addOption(factory.createOption("monthly", "monthly", 2));
        blackoutDrinking.addOption(factory.createOption("weekly", "weekly", 3));
        blackoutDrinking.addOption(factory.createOption("daily or almost daily", "daily or almost daily", 4));

        OptionEntry injuredDrinking = factory.createOptionEntry(
                "Drinking Injuries",
                "Have you or someone else been injured as a result of your drinking?");
        doc.addEntry(injuredDrinking);
        injuredDrinking.setSection(alcoholSection);
        injuredDrinking.setLabel("9.");
        injuredDrinking.addOption(factory.createOption("no", "no", 0));
        injuredDrinking.addOption(factory.createOption("yes, but not in the last 3 months", "yes, but not in the last 3 months", 2));
        injuredDrinking.addOption(factory.createOption("yes, during the last 3 months", "yes, during the last 3 months", 4));

        OptionEntry concernDrinking = factory.createOptionEntry(
                "Drinking Concern",
                "Has a relative or friend, or a doctor or other health worker been concerned about your drinking or suggested you cut down?");
        doc.addEntry(concernDrinking);
        concernDrinking.setSection(alcoholSection);
        concernDrinking.setLabel("10.");
        concernDrinking.addOption(factory.createOption("no", "no", 0));
        concernDrinking.addOption(factory.createOption("yes, but not in the last 3 months", "yes, but not in the last 3 months", 2));
        concernDrinking.addOption(factory.createOption("yes, during the last 3 months", "yes, during the last 3 months", 4));

        DerivedEntry auditTotal = factory.createDerivedEntry(
                "Total Audit Score", "Total Audit Score");
        doc.addEntry(auditTotal);
        auditTotal.setSection(alcoholSection);
        auditTotal.addVariable("a", alcoholFrequency);
        auditTotal.addVariable("b", alcoholAmount);
        auditTotal.addVariable("c", bingeDrinking);
        auditTotal.addVariable("d", keepDrinking);
        auditTotal.addVariable("e", failedDrinking);
        auditTotal.addVariable("f", morningDrinking);
        auditTotal.addVariable("g", guiltDrinking);
        auditTotal.addVariable("i", blackoutDrinking);
        auditTotal.addVariable("h", injuredDrinking);
        auditTotal.addVariable("j", concernDrinking);
        auditTotal.setFormula("a + b + c + d + e + f + g + h + i + j");

        NarrativeEntry alcoholScoring = factory.createNarrativeEntry("Alcohol Scoring", "Key to audit scoring");
        doc.addEntry(alcoholScoring);
        alcoholScoring.setSection(alcoholSection);
        alcoholScoring.setDisplayText("Males: a score greater than or equal to 7 suggests a pattern of hazardous or harmful drinking. " +
        		"Females: a score greater than or equal to 6 suggests a pattern of hazardous or harmful drinking. " +
        		"A score > 13 for both sexes indictaes that the person is likely to be alochol dependent.");

//        IOptionEntry mOrF = factory.createOptionEntry("Gender", "Male or female?");
//        doc.addEntry(mOrF);
//        mOrF.setSection(alcoholSection);
//        IOption female = factory.createOption("Female", 1);
//        IOption male = factory.createOption("Male", 0);
//        mOrF.addOption(female);
//        mOrF.addOption(male);

        OptionEntry hazardousDrinking = factory.createOptionEntry("Hazardous drinking", "Hazardous/Harmful levels?");
        doc.addEntry(hazardousDrinking);
        hazardousDrinking.setSection(alcoholSection);
        Option hdYes = factory.createOption("Yes", 1);
        Option hdNo = factory.createOption("No", 0);
        hazardousDrinking.addOption(hdYes);
        hazardousDrinking.addOption(hdNo);

        // Problems list section
        Section problemSection = factory.createSection("Problem List section");
        doc.addSection(problemSection);
        problemSection.setDisplayText("Problem List");
        SectionOccurrence problemSectionOcc = factory.createSectionOccurrence("Problem List section occurrence");
        problemSection.addOccurrence(problemSectionOcc);


        OptionEntry money = factory.createOptionEntry("Cause money problems",
                "Did cannabis cause any money problems for you?");
        doc.addEntry(money);
        money.setSection(problemSection);
        money.setLabel("1");
        money.addOption(factory.createOption("No", "No", 0));
        money.addOption(factory.createOption("A bit", "A bit", 1));
        money.addOption(factory.createOption("A lot", "A lot", 2));

        OptionEntry work = factory
                .createOptionEntry(
                        "Cause work problems",
                        "Did cannabis make you have problems at work, or at school (Tafe/University/ training courses)?");
        doc.addEntry(work);
        work.setSection(problemSection);
        work.setLabel("2");
        work.addOption(factory.createOption("No", "No", 0));
        work.addOption(factory.createOption("A bit", "A bit", 1));
        work.addOption(factory.createOption("A lot", "A lot", 2));

        OptionEntry housing = factory.createOptionEntry(
                "Cause housing problems",
                "Did you have housing problems because of cannabis?");
        doc.addEntry(housing);
        housing.setSection(problemSection);
        housing.setLabel("3");
        housing.addOption(factory.createOption("No", "No", 0));
        housing.addOption(factory.createOption("A bit", "A bit", 1));
        housing.addOption(factory.createOption("A lot", "A lot", 2));

        OptionEntry home = factory
                .createOptionEntry("Cause home problems",
                        "Were there problems at home or with your family because of cannabis?");
        doc.addEntry(home);
        home.setSection(problemSection);
        home.setLabel("4");
        home.addOption(factory.createOption("No", "No", 0));
        home.addOption(factory.createOption("A bit", "A bit", 1));
        home.addOption(factory.createOption("A lot", "A lot", 2));

        OptionEntry arguments = factory.createOptionEntry(
                "Cause arguments or fights",
                "Did you have any arguments or fights because of cannabis?");
        doc.addEntry(arguments);
        arguments.setSection(problemSection);
        arguments.setLabel("5");
        arguments.addOption(factory.createOption("No", "No", 0));
        arguments.addOption(factory.createOption("A bit", "A bit", 1));
        arguments.addOption(factory.createOption("A lot", "A lot", 2));

        OptionEntry law = factory
                .createOptionEntry("Cause trouble with the law",
                        "Has cannabis caused any trouble with the law, or the police?");
        doc.addEntry(law);
        law.setSection(problemSection);
        law.setLabel("6");
        law.addOption(factory.createOption("No", "No", 0));
        law.addOption(factory.createOption("A bit", "A bit", 1));
        law.addOption(factory.createOption("A lot", "A lot", 2));

        OptionEntry health = factory.createOptionEntry(
                "Cause health problems",
                "Has cannabis caused any health problems or injuries?");
        doc.addEntry(health);
        health.setSection(problemSection);
        health.setLabel("7");
        health.addOption(factory.createOption("No", "No", 0));
        health.addOption(factory.createOption("A bit", "A bit", 1));
        health.addOption(factory.createOption("A lot", "A lot", 2));

        OptionEntry risky = factory
                .createOptionEntry("Done anything risky",
                        "Have you done anything 'risky' or 'outrageous' after  using cannabis?");
        doc.addEntry(risky);
        risky.setSection(problemSection);
        risky.setLabel("8");
        risky.addOption(factory.createOption("No", "No", 0));
        Option riskyBit = factory.createOption("A bit", "A bit", 1);
        risky.addOption(riskyBit);
        Option riskyLot = factory.createOption("A lot", "A lot", 2);
        risky.addOption(riskyLot);

        OptionDependent riskyBitOptDep = factory.createOptionDependent();
        riskyBit.addOptionDependent(riskyBitOptDep);
        riskyBitOptDep.setEntryStatus(EntryStatus.MANDATORY);
        OptionDependent riskyLotOptDep = factory.createOptionDependent();
        riskyLot.addOptionDependent(riskyLotOptDep);
        riskyLotOptDep.setEntryStatus(EntryStatus.MANDATORY);

        CompositeEntry riskyComposite = factory.createComposite("Risky details " +
                "composite",
                "Enter details of 'risky' or 'outrageous' behaviour (e.g. driving under the influence" +
                        "; unprotected sex; sharing needles)");
        doc.addEntry(riskyComposite);
        riskyComposite.setSection(problemSection);
        riskyComposite.setEntryStatus(EntryStatus.DISABLED);

        TextEntry riskyOption = factory.createTextEntry("Risky details " +
                "option");
        riskyComposite.addEntry(riskyOption);
        riskyOption.setSection(problemSection);
        riskyBitOptDep.setDependentEntry(riskyComposite);
        riskyLotOptDep.setDependentEntry(riskyComposite);

        NarrativeEntry substanceUseResultIn = factory
                .createNarrativeEntry(
                        "Substance use in the last 3 months result in",
                        "Did your use of cannabis in the last 3 months result in you...");
        doc.addEntry(substanceUseResultIn);
        substanceUseResultIn.setSection(problemSection);

        OptionEntry uninterested = factory.createOptionEntry(
                "Uninterested in usual activities",
                "Being uninterested in your usual activities?");
        doc.addEntry(uninterested);
        uninterested.setSection(problemSection);
        uninterested.setLabel("9");
        uninterested.addOption(factory.createOption("No", "No", 0));
        uninterested.addOption(factory.createOption("A bit", "A bit", 1));
        uninterested.addOption(factory.createOption("A lot", "A lot", 2));

        OptionEntry depressed = factory.createOptionEntry(
                "Feeling depressed",
                "Feeling depressed?");
        doc.addEntry(depressed);
        depressed.setSection(problemSection);
        depressed.setLabel("10");
        depressed.addOption(factory.createOption("No", "No", 0));
        depressed.addOption(factory.createOption("A bit", "A bit", 1));
        depressed.addOption(factory.createOption("A lot", "A lot", 2));

        OptionEntry suspicious = factory.createOptionEntry(
                "Being suspicious",
                "Being suspicious or distrustful of others?");
        doc.addEntry(suspicious);
        suspicious.setSection(problemSection);
        suspicious.setLabel("11");
        suspicious.addOption(factory.createOption("No", "No", 0));
        suspicious.addOption(factory.createOption("A bit", "A bit", 1));
        suspicious.addOption(factory.createOption("A lot", "A lot", 2));

        OptionEntry strangeThoughts = factory.createOptionEntry(
                "Having strange thoughts",
                "Having strange thoughts?");
        doc.addEntry(strangeThoughts);
        strangeThoughts.setSection(problemSection);
        strangeThoughts.setLabel("12");
        strangeThoughts.addOption(factory.createOption("No", "No", 0));
        strangeThoughts.addOption(factory.createOption("A bit", "A bit", 1));
        strangeThoughts.addOption(factory.createOption("A lot", "A lot", 2));

        OptionEntry missingMed = factory.createOptionEntry(
                "Missing doses of medication",
                "Missing doses of medication?");
        doc.addEntry(missingMed);
        missingMed.setSection(problemSection);
        missingMed.setLabel("13");
        missingMed.addOption(factory.createOption("No", "No", 0));
        missingMed.addOption(factory.createOption("A bit", "A bit", 1));
        missingMed.addOption(factory.createOption("A lot", "A lot", 2));


        NumericEntry cannabisCost = factory.createNumericEntry("Cannabis cost",
                "How much money (UKP) do you spend on cannabis per day?", EntryStatus.OPTIONAL);
        doc.addEntry(cannabisCost);
        cannabisCost.setSection(problemSection);
        cannabisCost.addUnit(UnitWrapper.instance().getUnit("gbp"));

        //SDS Section
        Section sdsSection = factory.createSection("SDS section");
        doc.addSection(sdsSection);
        sdsSection.setDisplayText("The Severity of Dependence Scale (SDS)");
        SectionOccurrence sdsSectionOcc = factory.createSectionOccurrence("SDS section occurrence");
        sdsSection.addOccurrence(sdsSectionOcc);

        NarrativeEntry pastThreeMonths = factory.createNarrativeEntry(
                "During the past 3 months", "During the past 3 months...");
        doc.addEntry(pastThreeMonths);
        pastThreeMonths.setSection(sdsSection);

        OptionEntry outOfControl = factory
                .createOptionEntry("Think substance use was out of control",
                        "Did you ever think your use of cannabis was out of control?");
        doc.addEntry(outOfControl);
        outOfControl.setSection(sdsSection);
        outOfControl.setLabel("1");
        outOfControl.addOption(factory.createOption("never/almost never",
                "never/almost never", 0));
        outOfControl.addOption(factory
                .createOption("sometimes", "sometimes", 1));
        outOfControl.addOption(factory.createOption("often", "often", 2));
        outOfControl.addOption(factory.createOption("always/nearly always",
                "always/nearly always", 3));

        OptionEntry anxious = factory
                .createOptionEntry(
                        "Missing a fix make you anxious",
                        "Did the prospect of not using cannabis make you anxious or worried?");
        doc.addEntry(anxious);
        anxious.setSection(sdsSection);
        anxious.setLabel("2");
        anxious.addOption(factory.createOption("never/almost never",
                "never/almost never", 0));
        anxious.addOption(factory.createOption("sometimes", "sometimes", 1));
        anxious.addOption(factory.createOption("often", "often", 2));
        anxious.addOption(factory.createOption("always/nearly always",
                "always/nearly always", 3));

        OptionEntry worry = factory.createOptionEntry(
                "Worry about use of substance",
                "Did you worry about your use of cannabis?");
        doc.addEntry(worry);
        worry.setSection(sdsSection);
        worry.setLabel("3");
        worry.addOption(factory.createOption("never/almost never",
                "never/almost never", 0));
        worry.addOption(factory.createOption("sometimes", "sometimes", 1));
        worry.addOption(factory.createOption("often", "often", 2));
        worry.addOption(factory.createOption("always/nearly always",
                "always/nearly always", 3));

        OptionEntry couldStop = factory.createOptionEntry(
                "Wish you could stop", "Did you wish you could stop?");
        doc.addEntry(couldStop);
        couldStop.setSection(sdsSection);
        couldStop.setLabel("4");
        couldStop.addOption(factory.createOption("never/almost never",
                "never/almost never", 0));
        couldStop.addOption(factory.createOption("sometimes", "sometimes", 1));
        couldStop.addOption(factory.createOption("often", "often", 2));
        couldStop.addOption(factory.createOption("always/nearly always",
                "always/nearly always", 3));

        OptionEntry difficult = factory
                .createOptionEntry("Difficult to stop",
                        "How difficult did you find it to stop, or go without cannabis?");
        doc.addEntry(difficult);
        difficult.setLabel("5");
        difficult.setSection(sdsSection);
        difficult.addOption(factory.createOption(
                "not difficult",
                "not difficult", 0));
        difficult.addOption(factory.createOption(
                "quite difficult",
                "quite difficult", 1));
        difficult.addOption(factory.createOption(
                "very difficult",
                "very difficult", 2));
        difficult.addOption(factory.createOption(
                "impossible",
                "impossible", 3));

        //Readiness to change section
        Section readinessSection = factory.createSection("Readiness to Change section");
        doc.addSection(readinessSection);
        readinessSection.setDisplayText("Readiness to Change");
        SectionOccurrence readinessSectionOcc = factory.createSectionOccurrence("Readiness to Change section occurrence");
        readinessSection.addOccurrence(readinessSectionOcc);

        OptionEntry wantToChange = factory.createOptionEntry("",
                "Do you want to change your use of cannabis right now?");
        doc.addEntry(wantToChange);
        wantToChange.setSection(readinessSection);
        wantToChange.addOption(factory.createOption("no", "no", 0));
        wantToChange.addOption(factory.createOption("probably not",
                "probably not", 1));
        wantToChange.addOption(factory.createOption("unsure", "unsure", 2));
        wantToChange.addOption(factory.createOption("possibly", "possibly", 3));
        wantToChange.addOption(factory.createOption("definitely", "definitely", 4));

        //Confidence to change section
        Section confidenceSection = factory.createSection("Confidence to Change section");
        doc.addSection(confidenceSection);
        confidenceSection.setDisplayText("Confidence to Change");
        SectionOccurrence confidenceSectionOcc = factory.createSectionOccurrence("Confidence to Change section occurrence");
        confidenceSection.addOccurrence(confidenceSectionOcc);

        OptionEntry couldChange = factory
                .createOptionEntry("",
                        "Do you think you could change your use of cannabis now if you wanted to?");
        doc.addEntry(couldChange);
        couldChange.setSection(confidenceSection);
        couldChange.addOption(factory.createOption("definitely could not",
                "definitely could not", 0));
        couldChange.addOption(factory.createOption("probably could not",
                "probably could not", 1));
        couldChange.addOption(factory.createOption("unsure", "unsure", 2));
        couldChange.addOption(factory.createOption("probably could",
                "probably could", 3));
        couldChange.addOption(factory.createOption("definitely could",
                "definitely could", 4));

        //Summary totals section
        Section totalsSection = factory.createSection("Summary Totals section");
        doc.addSection(totalsSection);
        totalsSection.setDisplayText("Summary Totals");
        SectionOccurrence totalsSectionOcc = factory.createSectionOccurrence("Summary Totals section occurrence");
        totalsSection.addOccurrence(totalsSectionOcc);

        NarrativeEntry totals = factory.createNarrativeEntry(
                "Assessment summary totals",
                "Probable substance use problem: Assessment Summary Totals");
        doc.addEntry(totals);
        totals.setSection(totalsSection);

        DerivedEntry problemTotal = factory.createDerivedEntry(
                "Problem List Total", "Problem List Total");
        doc.addEntry(problemTotal);
        problemTotal.setSection(totalsSection);
        problemTotal.addVariable("a", money);
        problemTotal.addVariable("b", work);
        problemTotal.addVariable("c", housing);
        problemTotal.addVariable("d", home);
        problemTotal.addVariable("e", arguments);
        problemTotal.addVariable("f", law);
        problemTotal.addVariable("g", health);
        problemTotal.addVariable("i", risky);
        problemTotal.addVariable("h", uninterested);
        problemTotal.addVariable("j", depressed);
        problemTotal.addVariable("k", suspicious);
        problemTotal.addVariable("l", strangeThoughts);
        problemTotal.addVariable("m", missingMed);
        problemTotal.setFormula("a + b + c + d + e + f + g + h + i + j + k + l + m");

        DerivedEntry sdsTotal = factory.createDerivedEntry("SDS Total",
                "SDS Total");
        doc.addEntry(sdsTotal);
        sdsTotal.setSection(totalsSection);
        sdsTotal.addVariable("a", outOfControl);
        sdsTotal.addVariable("b", anxious);
        sdsTotal.addVariable("c", worry);
        sdsTotal.addVariable("d", couldStop);
        sdsTotal.addVariable("e", difficult);
        sdsTotal.setFormula("a + b + c + d + e");

        DerivedEntry readinessTotal = factory.createDerivedEntry(
                "Readiness to Change", "Readiness to Change");
        doc.addEntry(readinessTotal);
        readinessTotal.setSection(totalsSection);
        readinessTotal.addVariable("a", wantToChange);
        readinessTotal.setFormula("a");

        DerivedEntry confidenceTotal = factory.createDerivedEntry(
                "Confidence to Change", "Confidence to Change");
        doc.addEntry(confidenceTotal);
        confidenceTotal.setSection(totalsSection);
        confidenceTotal.addVariable("a", couldChange);
        confidenceTotal.setFormula("a");

        return doc;
    }

}
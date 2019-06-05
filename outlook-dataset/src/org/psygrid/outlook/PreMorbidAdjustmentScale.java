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

public class PreMorbidAdjustmentScale extends AssessmentForm {

    public static Document createDocument(Factory factory) {

        Document doc = factory.createDocument("Premorbid Summary Sheet",
                "Premorbid Adjustment Scale Scoring Summary");

        createDocumentStatuses(factory, doc);

        Section clientSec = factory.createSection("Original scores (client) section");
        doc.addSection(clientSec);
        clientSec.setDisplayText("Original scores - client");
        SectionOccurrence clientSecOcc = factory
                .createSectionOccurrence("Original scores (client) section occurrence");
        clientSec.addOccurrence(clientSecOcc);

        Section familySec = factory.createSection("Original scores (family) section");
        doc.addSection(familySec);
        familySec.setDisplayText("Original scores - family");
        SectionOccurrence familySecOcc = factory
                .createSectionOccurrence("Original scores (family) section occurrence");
        familySec.addOccurrence(familySecOcc);

        Section adjustedSec = factory.createSection("Adjusted scores section");
        doc.addSection(adjustedSec);
        adjustedSec.setDisplayText("Adjusted scores");
        SectionOccurrence adjustedSecOcc = factory
                .createSectionOccurrence("Adjusted scores section occurrence");
        adjustedSec.addOccurrence(adjustedSecOcc);

        ValidationRule zeroToSixRule = ValidationRulesWrapper.instance().getRule("ZeroToSix");
        ValidationRule zeroTwoFourOrSixRule = ValidationRulesWrapper.instance().getRule("ZeroTwoFourOrSix");
        String description = "Please enter a number between 0 and 6.";
        String description2 = "Please enter a number from 0, 2, 4 and 6.";
        String description3 = "If Married (presently or formerly), enter a number between 0 and 3; "+
                              "if Never Married (Over 30), enter a number between 2 and 6;"+
                              "if Never Married (20-29), enter a number from 0, 1, 3, 5 and 6.";

        /* Original scores (client) section */
        //Childhood
        NarrativeEntry childhoodNarrative1 = factory.createNarrativeEntry("Childhood",
                "Childhood");
        childhoodNarrative1.setStyle(NarrativeStyle.HEADER);
        doc.addEntry(childhoodNarrative1);
        childhoodNarrative1.setSection(clientSec);

        NumericEntry sociabilityChildhood1 = factory.createNumericEntry("Sociability " +
                "and withdrawal (Childhood)", "Sociability and withdrawal");
        doc.addEntry(sociabilityChildhood1);
        sociabilityChildhood1.setSection(clientSec);
        sociabilityChildhood1.addValidationRule(zeroToSixRule);
        sociabilityChildhood1.setDescription(description);

        NumericEntry peerChildhood1 = factory.createNumericEntry("Peer relationships (Childhood)",
                "Peer relationships");
        doc.addEntry(peerChildhood1);
        peerChildhood1.setSection(clientSec);
        peerChildhood1.addValidationRule(zeroToSixRule);
        peerChildhood1.setDescription(description);

        NumericEntry scholasticChildhood1 = factory.createNumericEntry("Scholastic performance (Childhood)",
                "Scholastic performance");
        doc.addEntry(scholasticChildhood1);
        scholasticChildhood1.setSection(clientSec);
        scholasticChildhood1.addValidationRule(zeroToSixRule);
        scholasticChildhood1.setDescription(description);

        NumericEntry adaptionChildhood1 = factory.createNumericEntry("Adaption to school (Childhood)",
                "Adaption to school");
        doc.addEntry(adaptionChildhood1);
        adaptionChildhood1.setSection(clientSec);
        adaptionChildhood1.addValidationRule(zeroToSixRule);
        adaptionChildhood1.setDescription(description);

        //Early adolescence
        NarrativeEntry earlyAdNarrative1 = factory.createNarrativeEntry(
                "Early Adolescence", "Early Adolescence");
        earlyAdNarrative1.setStyle(NarrativeStyle.HEADER);
        doc.addEntry(earlyAdNarrative1);
        earlyAdNarrative1.setSection(clientSec);

        NumericEntry sociabilityEarlyAd1 = factory
                .createNumericEntry("Sociability and withdrawal (Early Adolescence)",
                        "Sociability and withdrawal");
        doc.addEntry(sociabilityEarlyAd1);
        sociabilityEarlyAd1.setSection(clientSec);
        sociabilityEarlyAd1.addValidationRule(zeroToSixRule);
        sociabilityEarlyAd1.setDescription(description);

        NumericEntry peerEarlyAd1 = factory.createNumericEntry(
                "Peer relationships (Early Adolescence)", "Peer relationships");
        doc.addEntry(peerEarlyAd1);
        peerEarlyAd1.setSection(clientSec);
        peerEarlyAd1.addValidationRule(zeroToSixRule);
        peerEarlyAd1.setDescription(description);

        NumericEntry scholasticEarlyAd1 = factory.createNumericEntry(
                "Scholastic performance (Early Adolescence)", "Scholastic performance");
        doc.addEntry(scholasticEarlyAd1);
        scholasticEarlyAd1.setSection(clientSec);
        scholasticEarlyAd1.addValidationRule(zeroToSixRule);
        scholasticEarlyAd1.setDescription(description);

        NumericEntry adaptionEarlyAd1 = factory.createNumericEntry(
                "Adaption to school (Early Adolescence)", "Adaption to school");
        doc.addEntry(adaptionEarlyAd1);
        adaptionEarlyAd1.setSection(clientSec);
        adaptionEarlyAd1.addValidationRule(zeroToSixRule);
        adaptionEarlyAd1.setDescription(description);

        NumericEntry socialEarlyAd1 = factory.createNumericEntry(
                "Social sexual aspects of life (Early Adolescence)", "Social sexual aspects of life");
        doc.addEntry(socialEarlyAd1);
        socialEarlyAd1.setSection(clientSec);
        socialEarlyAd1.addValidationRule(zeroToSixRule);
        socialEarlyAd1.setDescription(description);

        //Late adolescence
        NarrativeEntry lateAdNarrative1 = factory.createNarrativeEntry(
                "Late Adolescence", "Late Adolescence");
        doc.addEntry(lateAdNarrative1);
        lateAdNarrative1.setStyle(NarrativeStyle.HEADER);
        lateAdNarrative1.setSection(clientSec);

        NumericEntry sociabilityLateAd1 = factory
                .createNumericEntry("Sociability and withdrawal (Late Adolescence)",
                        "Sociability and withdrawal");
        doc.addEntry(sociabilityLateAd1);
        sociabilityLateAd1.setSection(clientSec);
        sociabilityLateAd1.addValidationRule(zeroToSixRule);
        sociabilityLateAd1.setDescription(description);

        NumericEntry peerLateAd1 = factory.createNumericEntry(
                "Peer relationships (Late Adolescence)", "Peer relationships");
        doc.addEntry(peerLateAd1);
        peerLateAd1.setSection(clientSec);
        peerLateAd1.addValidationRule(zeroToSixRule);
        peerLateAd1.setDescription(description);

        NumericEntry scholasticLateAd1 = factory.createNumericEntry(
                "Scholastic performance (Late Adolescence)", "Scholastic performance");
        doc.addEntry(scholasticLateAd1);
        scholasticLateAd1.setSection(clientSec);
        scholasticLateAd1.addValidationRule(zeroToSixRule);
        scholasticLateAd1.setDescription(description);

        NumericEntry adaptionLateAd1 = factory.createNumericEntry(
                "Adaption to school (Late Adolescence)", "Adaption to school");
        doc.addEntry(adaptionLateAd1);
        adaptionLateAd1.setSection(clientSec);
        adaptionLateAd1.addValidationRule(zeroToSixRule);
        adaptionLateAd1.setDescription(description);

        NumericEntry socialLateAd1 = factory.createNumericEntry(
                "Social sexual aspects of life (Late Adolescence)", "Social sexual aspects of life");
        doc.addEntry(socialLateAd1);
        socialLateAd1.setSection(clientSec);
        socialLateAd1.addValidationRule(zeroToSixRule);
        socialLateAd1.setDescription(description);

        //Adulthood
        NarrativeEntry adulthoodNarrative1 = factory.createNarrativeEntry(
                "Adulthood", "Adulthood");
        doc.addEntry(adulthoodNarrative1);
        adulthoodNarrative1.setStyle(NarrativeStyle.HEADER);
        adulthoodNarrative1.setSection(clientSec);

        NumericEntry sociabilityAdulthood1 = factory
                .createNumericEntry("Sociability and withdrawal (Adulthood)",
                        "Sociability and withdrawal");
        doc.addEntry(sociabilityAdulthood1);
        sociabilityAdulthood1.setSection(clientSec);
        sociabilityAdulthood1.addValidationRule(zeroToSixRule);
        sociabilityAdulthood1.setDescription(description);

        NumericEntry peerAdulthood1 = factory.createNumericEntry(
                "Peer relationships (Adulthood)", "Peer relationships");
        doc.addEntry(peerAdulthood1);
        peerAdulthood1.setSection(clientSec);
        peerAdulthood1.addValidationRule(zeroToSixRule);
        peerAdulthood1.setDescription(description);

        NumericEntry socialAdulthood1 = factory.createNumericEntry(
                "Social sexual aspects of life (Adulthood)", "Social sexual aspects of life");
        doc.addEntry(socialAdulthood1);
        socialAdulthood1.setSection(clientSec);
        socialAdulthood1.addValidationRule(zeroToSixRule);
        socialAdulthood1.setDescription(description3);

        //General
        NarrativeEntry general1 = factory.createNarrativeEntry("General",
                "General");
        doc.addEntry(general1);
        general1.setStyle(NarrativeStyle.HEADER);
        general1.setSection(clientSec);

        NumericEntry education1 = factory.createNumericEntry(
                "Education", "Education");
        doc.addEntry(education1);
        education1.setSection(clientSec);
        education1.addValidationRule(zeroToSixRule);
        education1.setDescription(description);
        education1.setLabel("1");

        NumericEntry employed1 = factory.createNumericEntry(
                "Employed/At School", "Employed/At School");
        doc.addEntry(employed1);
        employed1.setSection(clientSec);
        employed1.addValidationRule(zeroToSixRule);
        employed1.setDescription(description);
        employed1.setLabel("2");

        NumericEntry change1 = factory.createNumericEntry(
                "Change in Work/School Performance", "Change in Work/School Performance");
        doc.addEntry(change1);
        change1.setSection(clientSec);
        change1.addValidationRule(zeroToSixRule);
        change1.setDescription(description);
        change1.setLabel("3");

        NumericEntry jobChange1 = factory.createNumericEntry(
                "Job Change/Interrupted School Attendance",
                "Job Change/Interrupted School Attendance");
        doc.addEntry(jobChange1);
        jobChange1.setSection(clientSec);
        jobChange1.addValidationRule(zeroToSixRule);
        jobChange1.setDescription(description);
        jobChange1.setLabel("4");

        NumericEntry independence1 = factory.createNumericEntry(
                "Establishment of Independence", "Establishment of Independence");
        doc.addEntry(independence1);
        independence1.setSection(clientSec);
        independence1.addValidationRule(zeroTwoFourOrSixRule);
        independence1.setDescription(description2);
        independence1.setLabel("5");

        NumericEntry functioning1 = factory.createNumericEntry(
                "Highest Level of Functioning Achieved in Patient's Life",
                "Highest Level of Functioning Achieved in Patient's Life");
        doc.addEntry(functioning1);
        functioning1.setSection(clientSec);
        functioning1.addValidationRule(zeroTwoFourOrSixRule);
        functioning1.setDescription(description2);
        functioning1.setLabel("6");

        NumericEntry adjustment1 = factory.createNumericEntry(
                "Social Personal Adjustment", "Social Personal Adjustment");
        doc.addEntry(adjustment1);
        adjustment1.setSection(clientSec);
        adjustment1.addValidationRule(zeroToSixRule);
        adjustment1.setDescription(description);
        adjustment1.setLabel("7");

        NumericEntry interest1 = factory.createNumericEntry(
                "Degree of Interest in Life", "Degree of Interest in Life");
        doc.addEntry(interest1);
        interest1.setSection(clientSec);
        interest1.addValidationRule(zeroTwoFourOrSixRule);
        interest1.setDescription(description2);
        interest1.setLabel("8");

        NumericEntry energy1 = factory.createNumericEntry(
                "Energy Level", "Energy Level");
        doc.addEntry(energy1);
        energy1.setSection(clientSec);
        energy1.addValidationRule(zeroTwoFourOrSixRule);
        energy1.setDescription(description2);
        energy1.setLabel("9");

        /* Original scores (family) section */
        //Childhood
        NarrativeEntry childhoodNarrative2 = factory.createNarrativeEntry("Childhood",
                "Childhood");
        doc.addEntry(childhoodNarrative2);
        childhoodNarrative2.setStyle(NarrativeStyle.HEADER);
        childhoodNarrative2.setSection(familySec);

        NumericEntry sociabilityChildhood2 = factory.createNumericEntry("Sociability " +
                "and withdrawal (Childhood)", "Sociability and withdrawal");
        doc.addEntry(sociabilityChildhood2);
        sociabilityChildhood2.setSection(familySec);
        sociabilityChildhood2.addValidationRule(zeroToSixRule);
        sociabilityChildhood2.setDescription(description);

        NumericEntry peerChildhood2 = factory.createNumericEntry("Peer relationships (Childhood)",
                "Peer relationships");
        doc.addEntry(peerChildhood2);
        peerChildhood2.setSection(familySec);
        peerChildhood2.addValidationRule(zeroToSixRule);
        peerChildhood2.setDescription(description);

        NumericEntry scholasticChildhood2 = factory.createNumericEntry("Scholastic performance (Childhood)",
                "Scholastic performance");
        doc.addEntry(scholasticChildhood2);
        scholasticChildhood2.setSection(familySec);
        scholasticChildhood2.addValidationRule(zeroToSixRule);
        scholasticChildhood2.setDescription(description);

        NumericEntry adaptionChildhood2 = factory.createNumericEntry("Adaption to school (Childhood)",
                "Adaption to school");
        doc.addEntry(adaptionChildhood2);
        adaptionChildhood2.setSection(familySec);
        adaptionChildhood2.addValidationRule(zeroToSixRule);
        adaptionChildhood2.setDescription(description);

        //Early adolescence
        NarrativeEntry earlyAdNarrative2 = factory.createNarrativeEntry(
                "Early Adolescence", "Early Adolescence");
        doc.addEntry(earlyAdNarrative2);
        earlyAdNarrative2.setStyle(NarrativeStyle.HEADER);
        earlyAdNarrative2.setSection(familySec);

        NumericEntry sociabilityEarlyAd2 = factory
                .createNumericEntry("Sociability and withdrawal (Early Adolescence)",
                        "Sociability and withdrawal");
        doc.addEntry(sociabilityEarlyAd2);
        sociabilityEarlyAd2.setSection(familySec);
        sociabilityEarlyAd2.addValidationRule(zeroToSixRule);
        sociabilityEarlyAd2.setDescription(description);

        NumericEntry peerEarlyAd2 = factory.createNumericEntry(
                "Peer relationships (Early Adolescence)", "Peer relationships");
        doc.addEntry(peerEarlyAd2);
        peerEarlyAd2.setSection(familySec);
        peerEarlyAd2.addValidationRule(zeroToSixRule);
        peerEarlyAd2.setDescription(description);

        NumericEntry scholasticEarlyAd2 = factory.createNumericEntry(
                "Scholastic performance (Early Adolescence)", "Scholastic performance");
        doc.addEntry(scholasticEarlyAd2);
        scholasticEarlyAd2.setSection(familySec);
        scholasticEarlyAd2.addValidationRule(zeroToSixRule);
        scholasticEarlyAd2.setDescription(description);

        NumericEntry adaptionEarlyAd2 = factory.createNumericEntry(
                "Adaption to school (Early Adolescence)", "Adaption to school");
        doc.addEntry(adaptionEarlyAd2);
        adaptionEarlyAd2.setSection(familySec);
        adaptionEarlyAd2.addValidationRule(zeroToSixRule);
        adaptionEarlyAd2.setDescription(description);

        NumericEntry socialEarlyAd2 = factory.createNumericEntry(
                "Social sexual aspects of life (Early Adolescence)", "Social sexual aspects of life");
        doc.addEntry(socialEarlyAd2);
        socialEarlyAd2.setSection(familySec);
        socialEarlyAd2.addValidationRule(zeroToSixRule);
        socialEarlyAd2.setDescription(description);

        //Late adolescence
        NarrativeEntry lateAdNarrative2 = factory.createNarrativeEntry(
                "Late Adolescence", "Late Adolescence");
        doc.addEntry(lateAdNarrative2);
        lateAdNarrative2.setStyle(NarrativeStyle.HEADER);
        lateAdNarrative2.setSection(familySec);

        NumericEntry sociabilityLateAd2 = factory
                .createNumericEntry("Sociability and withdrawal (Late Adolescence)",
                        "Sociability and withdrawal");
        doc.addEntry(sociabilityLateAd2);
        sociabilityLateAd2.setSection(familySec);
        sociabilityLateAd2.addValidationRule(zeroToSixRule);
        sociabilityLateAd2.setDescription(description);

        NumericEntry peerLateAd2 = factory.createNumericEntry(
                "Peer relationships (Late Adolescence)", "Peer relationships");
        doc.addEntry(peerLateAd2);
        peerLateAd2.setSection(familySec);
        peerLateAd2.addValidationRule(zeroToSixRule);
        peerLateAd2.setDescription(description);

        NumericEntry scholasticLateAd2 = factory.createNumericEntry(
                "Scholastic performance (Late Adolescence)", "Scholastic performance");
        doc.addEntry(scholasticLateAd2);
        scholasticLateAd2.setSection(familySec);
        scholasticLateAd2.addValidationRule(zeroToSixRule);
        scholasticLateAd2.setDescription(description);

        NumericEntry adaptionLateAd2 = factory.createNumericEntry(
                "Adaption to school (Late Adolescence)", "Adaption to school");
        doc.addEntry(adaptionLateAd2);
        adaptionLateAd2.setSection(familySec);
        adaptionLateAd2.addValidationRule(zeroToSixRule);
        adaptionLateAd2.setDescription(description);

        NumericEntry socialLateAd2 = factory.createNumericEntry(
                "Social sexual aspects of life (Late Adolescence)", "Social sexual aspects of life");
        doc.addEntry(socialLateAd2);
        socialLateAd2.setSection(familySec);
        socialLateAd2.addValidationRule(zeroToSixRule);
        socialLateAd2.setDescription(description);

        //Adulthood
        NarrativeEntry adulthoodNarrative2 = factory.createNarrativeEntry(
                "Adulthood", "Adulthood");
        doc.addEntry(adulthoodNarrative2);
        adulthoodNarrative2.setStyle(NarrativeStyle.HEADER);
        adulthoodNarrative2.setSection(familySec);

        NumericEntry sociabilityAdulthood2 = factory
                .createNumericEntry("Sociability and withdrawal (Adulthood)",
                        "Sociability and withdrawal");
        doc.addEntry(sociabilityAdulthood2);
        sociabilityAdulthood2.setSection(familySec);
        sociabilityAdulthood2.addValidationRule(zeroToSixRule);
        sociabilityAdulthood2.setDescription(description);

        NumericEntry peerAdulthood2 = factory.createNumericEntry(
                "Peer relationships (Adulthood)", "Peer relationships");
        doc.addEntry(peerAdulthood2);
        peerAdulthood2.setSection(familySec);
        peerAdulthood2.addValidationRule(zeroToSixRule);
        peerAdulthood2.setDescription(description);

        NumericEntry socialAdulthood2 = factory.createNumericEntry(
                "Social sexual aspects of life (Adulthood)", "Social sexual aspects of life");
        doc.addEntry(socialAdulthood2);
        socialAdulthood2.setSection(familySec);
        socialAdulthood2.addValidationRule(zeroToSixRule);
        socialAdulthood2.setDescription(description3);

        //General
        NarrativeEntry general2 = factory.createNarrativeEntry("General",
                "General");
        doc.addEntry(general2);
        general2.setStyle(NarrativeStyle.HEADER);
        general2.setSection(familySec);

        NumericEntry education2 = factory.createNumericEntry(
                "Education", "Education");
        doc.addEntry(education2);
        education2.setSection(familySec);
        education2.addValidationRule(zeroToSixRule);
        education2.setDescription(description);
        education2.setLabel("1");

        NumericEntry employed2 = factory.createNumericEntry(
                "Employed/At School", "Employed/At School");
        doc.addEntry(employed2);
        employed2.setSection(familySec);
        employed2.addValidationRule(zeroToSixRule);
        employed2.setDescription(description);
        employed2.setLabel("2");

        NumericEntry change2 = factory.createNumericEntry(
                "Change in Work/School Performance", "Change in Work/School Performance");
        doc.addEntry(change2);
        change2.setSection(familySec);
        change2.addValidationRule(zeroToSixRule);
        change2.setDescription(description);
        change2.setLabel("3");

        NumericEntry jobChange2 = factory.createNumericEntry(
                "Job Change/Interrupted School Attendance",
                "Job Change/Interrupted School Attendance");
        doc.addEntry(jobChange2);
        jobChange2.setSection(familySec);
        jobChange2.addValidationRule(zeroToSixRule);
        jobChange2.setDescription(description);
        jobChange2.setLabel("4");

        NumericEntry independence2 = factory.createNumericEntry(
                "Establishment of Independence", "Establishment of Independence");
        doc.addEntry(independence2);
        independence2.setSection(familySec);
        independence2.addValidationRule(zeroTwoFourOrSixRule);
        independence2.setDescription(description2);
        independence2.setLabel("5");

        NumericEntry functioning2 = factory.createNumericEntry(
                "Highest Level of Functioning Achieved in Patient's Life",
                "Highest Level of Functioning Achieved in Patient's Life");
        doc.addEntry(functioning2);
        functioning2.setSection(familySec);
        functioning2.addValidationRule(zeroTwoFourOrSixRule);
        functioning2.setDescription(description2);
        functioning2.setLabel("6");

        NumericEntry adjustment2 = factory.createNumericEntry(
                "Social Personal Adjustment", "Social Personal Adjustment");
        doc.addEntry(adjustment2);
        adjustment2.setSection(familySec);
        adjustment2.addValidationRule(zeroToSixRule);
        adjustment2.setDescription(description);
        adjustment2.setLabel("7");

        NumericEntry interest2 = factory.createNumericEntry(
                "Degree of Interest in Life", "Degree of Interest in Life");
        doc.addEntry(interest2);
        interest2.setSection(familySec);
        interest2.addValidationRule(zeroTwoFourOrSixRule);
        interest2.setDescription(description2);
        interest2.setLabel("8");

        NumericEntry energy2 = factory.createNumericEntry(
                "Energy Level", "Energy Level");
        doc.addEntry(energy2);
        energy2.setSection(familySec);
        energy2.addValidationRule(zeroTwoFourOrSixRule);
        energy2.setDescription(description2);
        energy2.setLabel("9");

        /* Adjusted scores section */
        //Childhood
        final String formula = "(a+b)/2";

        NarrativeEntry childhoodNarrative3 = factory.createNarrativeEntry("Childhood",
                "Childhood");
        doc.addEntry(childhoodNarrative3);
        childhoodNarrative3.setSection(adjustedSec);
        childhoodNarrative3.setStyle(NarrativeStyle.HEADER);

        DerivedEntry sociabilityChildhood3 = factory.createDerivedEntry("Sociability " +
                "and withdrawal (Childhood)", "Sociability and withdrawal");
        doc.addEntry(sociabilityChildhood3);
        sociabilityChildhood3.setSection(adjustedSec);
        sociabilityChildhood3.addVariable("a", sociabilityChildhood1);
        sociabilityChildhood3.addVariable("b", sociabilityChildhood2);
        sociabilityChildhood3.setFormula(formula);

        DerivedEntry peerChildhood3 = factory.createDerivedEntry("Peer relationships (Childhood)",
                "Peer relationships");
        doc.addEntry(peerChildhood3);
        peerChildhood3.setSection(adjustedSec);
        peerChildhood3.addVariable("a", peerChildhood1);
        peerChildhood3.addVariable("b", peerChildhood2);
        peerChildhood3.setFormula(formula);

        DerivedEntry scholasticChildhood3 = factory.createDerivedEntry("Scholastic performance (Childhood)",
                "Scholastic performance");
        doc.addEntry(scholasticChildhood3);
        scholasticChildhood3.setSection(adjustedSec);
        scholasticChildhood3.addVariable("a", scholasticChildhood1);
        scholasticChildhood3.addVariable("b", scholasticChildhood2);
        scholasticChildhood3.setFormula(formula);

        DerivedEntry adaptionChildhood3 = factory.createDerivedEntry("Adaption to school (Childhood)",
                "Adaption to school");
        doc.addEntry(adaptionChildhood3);
        adaptionChildhood3.setSection(adjustedSec);
        adaptionChildhood3.addVariable("a", adaptionChildhood1);
        adaptionChildhood3.addVariable("b", adaptionChildhood2);
        adaptionChildhood3.setFormula(formula);

        //Early adolescence
        NarrativeEntry earlyAdNarrative3 = factory.createNarrativeEntry(
                "Early Adolescence", "Early Adolescence");
        doc.addEntry(earlyAdNarrative3);
        earlyAdNarrative3.setSection(adjustedSec);
        earlyAdNarrative3.setStyle(NarrativeStyle.HEADER);

        DerivedEntry sociabilityEarlyAd3 = factory
                .createDerivedEntry("Sociability and withdrawal (Early Adolescence)",
                        "Sociability and withdrawal");
        doc.addEntry(sociabilityEarlyAd3);
        sociabilityEarlyAd3.setSection(adjustedSec);
        sociabilityEarlyAd3.addVariable("a", sociabilityEarlyAd1);
        sociabilityEarlyAd3.addVariable("b", sociabilityEarlyAd2);
        sociabilityEarlyAd3.setFormula(formula);

        DerivedEntry peerEarlyAd3 = factory.createDerivedEntry(
                "Peer relationships (Early Adolescence)", "Peer relationships");
        doc.addEntry(peerEarlyAd3);
        peerEarlyAd3.setSection(adjustedSec);
        peerEarlyAd3.addVariable("a", peerEarlyAd1);
        peerEarlyAd3.addVariable("b", peerEarlyAd2);
        peerEarlyAd3.setFormula(formula);

        DerivedEntry scholasticEarlyAd3 = factory.createDerivedEntry(
                "Scholastic performance (Early Adolescence)", "Scholastic performance");
        doc.addEntry(scholasticEarlyAd3);
        scholasticEarlyAd3.setSection(adjustedSec);
        scholasticEarlyAd3.addVariable("a", scholasticEarlyAd1);
        scholasticEarlyAd3.addVariable("b", scholasticEarlyAd2);
        scholasticEarlyAd3.setFormula(formula);

        DerivedEntry adaptionEarlyAd3 = factory.createDerivedEntry(
                "Adaption to school (Early Adolescence)", "Adaption to school");
        doc.addEntry(adaptionEarlyAd3);
        adaptionEarlyAd3.setSection(adjustedSec);
        adaptionEarlyAd3.addVariable("a", adaptionEarlyAd1);
        adaptionEarlyAd3.addVariable("b", adaptionEarlyAd2);
        adaptionEarlyAd3.setFormula(formula);

        DerivedEntry socialEarlyAd3 = factory.createDerivedEntry(
                "Social sexual aspects of life (Early Adolescence)", "Social sexual aspects of life");
        doc.addEntry(socialEarlyAd3);
        socialEarlyAd3.setSection(adjustedSec);
        socialEarlyAd3.addVariable("a", socialEarlyAd1);
        socialEarlyAd3.addVariable("b", socialEarlyAd2);
        socialEarlyAd3.setFormula(formula);

        //Late adolescence
        NarrativeEntry lateAdNarrative3 = factory.createNarrativeEntry(
                "Late Adolescence", "Late Adolescence");
        doc.addEntry(lateAdNarrative3);
        lateAdNarrative3.setSection(adjustedSec);
        lateAdNarrative3.setStyle(NarrativeStyle.HEADER);

        DerivedEntry sociabilityLateAd3 = factory
                .createDerivedEntry("Sociability and withdrawal (Late Adolescence)",
                        "Sociability and withdrawal");
        doc.addEntry(sociabilityLateAd3);
        sociabilityLateAd3.setSection(adjustedSec);
        sociabilityLateAd3.addVariable("a", sociabilityLateAd1);
        sociabilityLateAd3.addVariable("b", sociabilityLateAd2);
        sociabilityLateAd3.setFormula(formula);

        DerivedEntry peerLateAd3 = factory.createDerivedEntry(
                "Peer relationships (Late Adolescence)", "Peer relationships");
        doc.addEntry(peerLateAd3);
        peerLateAd3.setSection(adjustedSec);
        peerLateAd3.addVariable("a", peerLateAd1);
        peerLateAd3.addVariable("b", peerLateAd2);
        peerLateAd3.setFormula(formula);

        DerivedEntry scholasticLateAd3 = factory.createDerivedEntry(
                "Scholastic performance (Late Adolescence)", "Scholastic performance");
        doc.addEntry(scholasticLateAd3);
        scholasticLateAd3.setSection(adjustedSec);
        scholasticLateAd3.addVariable("a", scholasticLateAd1);
        scholasticLateAd3.addVariable("b", scholasticLateAd2);
        scholasticLateAd3.setFormula(formula);

        DerivedEntry adaptionLateAd3 = factory.createDerivedEntry(
                "Adaption to school (Late Adolescence)", "Adaption to school");
        doc.addEntry(adaptionLateAd3);
        adaptionLateAd3.setSection(adjustedSec);
        adaptionLateAd3.addVariable("a", adaptionLateAd1);
        adaptionLateAd3.addVariable("b", adaptionLateAd2);
        adaptionLateAd3.setFormula(formula);

        DerivedEntry socialLateAd3 = factory.createDerivedEntry(
                "Social sexual aspects of life (Late Adolescence)", "Social sexual aspects of life");
        doc.addEntry(socialLateAd3);
        socialLateAd3.setSection(adjustedSec);
        socialLateAd3.addVariable("a", socialLateAd1);
        socialLateAd3.addVariable("b", socialLateAd2);
        socialLateAd3.setFormula(formula);

        //Adulthood
        NarrativeEntry adulthoodNarrative3 = factory.createNarrativeEntry(
                "Adulthood", "Adulthood");
        doc.addEntry(adulthoodNarrative3);
        adulthoodNarrative3.setSection(adjustedSec);
        adulthoodNarrative3.setStyle(NarrativeStyle.HEADER);

        DerivedEntry sociabilityAdulthood3 = factory
                .createDerivedEntry("Sociability and withdrawal (Adulthood)",
                        "Sociability and withdrawal");
        doc.addEntry(sociabilityAdulthood3);
        sociabilityAdulthood3.setSection(adjustedSec);
        sociabilityAdulthood3.addVariable("a", sociabilityAdulthood1);
        sociabilityAdulthood3.addVariable("b", sociabilityAdulthood2);
        sociabilityAdulthood3.setFormula(formula);

        DerivedEntry peerAdulthood3 = factory.createDerivedEntry(
                "Peer relationships (Adulthood)", "Peer relationships");
        doc.addEntry(peerAdulthood3);
        peerAdulthood3.setSection(adjustedSec);
        peerAdulthood3.addVariable("a", peerAdulthood1);
        peerAdulthood3.addVariable("b", peerAdulthood2);
        peerAdulthood3.setFormula(formula);

        DerivedEntry socialAdulthood3 = factory.createDerivedEntry(
                "Social sexual aspects of life (Adulthood)", "Social sexual aspects of life");
        doc.addEntry(socialAdulthood3);
        socialAdulthood3.setSection(adjustedSec);
        socialAdulthood3.addVariable("a", socialAdulthood1);
        socialAdulthood3.addVariable("b", socialAdulthood2);
        socialAdulthood3.setFormula(formula);

        //General
        NarrativeEntry general3 = factory.createNarrativeEntry("General",
                "General");
        doc.addEntry(general3);
        general3.setSection(adjustedSec);
        general3.setStyle(NarrativeStyle.HEADER);

        DerivedEntry education3 = factory.createDerivedEntry(
                "Education", "Education");
        doc.addEntry(education3);
        education3.setSection(adjustedSec);
        education3.setLabel("1");
        education3.addVariable("a", education1);
        education3.addVariable("b", education2);
        education3.setFormula(formula);

        DerivedEntry employed3 = factory.createDerivedEntry(
                "Employed/At School", "Employed/At School");
        doc.addEntry(employed3);
        employed3.setSection(adjustedSec);
        employed3.setLabel("2");
        employed3.addVariable("a", employed1);
        employed3.addVariable("b", employed2);
        employed3.setFormula(formula);

        DerivedEntry change3 = factory.createDerivedEntry(
                "Change in Work/School Performance", "Change in Work/School Performance");
        doc.addEntry(change3);
        change3.setSection(adjustedSec);
        change3.setLabel("3");
        change3.addVariable("a", change1);
        change3.addVariable("b", change2);
        change3.setFormula(formula);

        DerivedEntry jobChange3 = factory.createDerivedEntry(
                "Job Change/Interrupted School Attendance",
                "Job Change/Interrupted School Attendance");
        doc.addEntry(jobChange3);
        jobChange3.setSection(adjustedSec);
        jobChange3.setLabel("4");
        jobChange3.addVariable("a", jobChange1);
        jobChange3.addVariable("b", jobChange2);
        jobChange3.setFormula(formula);

        DerivedEntry independence3 = factory.createDerivedEntry(
                "Establishment of Independence", "Establishment of Independence");
        doc.addEntry(independence3);
        independence3.setSection(adjustedSec);
        independence3.setLabel("5");
        independence3.addVariable("a", independence1);
        independence3.addVariable("b", independence2);
        independence3.setFormula(formula);

        DerivedEntry functioning3 = factory.createDerivedEntry(
                "Highest Level of Functioning Achieved in Patient's Life",
                "Highest Level of Functioning Achieved in Patient's Life");
        doc.addEntry(functioning3);
        functioning3.setSection(adjustedSec);
        functioning3.setLabel("6");
        functioning3.addVariable("a", functioning1);
        functioning3.addVariable("b", functioning2);
        functioning3.setFormula(formula);

        DerivedEntry adjustment3 = factory.createDerivedEntry(
                "Social Personal Adjustment", "Social Personal Adjustment");
        doc.addEntry(adjustment3);
        adjustment3.setSection(adjustedSec);
        adjustment3.setLabel("7");
        adjustment3.addVariable("a", adjustment1);
        adjustment3.addVariable("b", adjustment2);
        adjustment3.setFormula(formula);

        DerivedEntry interest3 = factory.createDerivedEntry(
                "Degree of Interest in Life", "Degree of Interest in Life");
        doc.addEntry(interest3);
        interest3.setSection(adjustedSec);
        interest3.setLabel("8");
        interest3.addVariable("a", interest1);
        interest3.addVariable("b", interest2);
        interest3.setFormula(formula);

        DerivedEntry energy3 = factory.createDerivedEntry(
                "Energy Level", "Energy Level");
        doc.addEntry(energy3);
        energy3.setSection(adjustedSec);
        energy3.setLabel("9");
        energy3.addVariable("a", energy1);
        energy3.addVariable("b", energy2);
        energy3.setFormula(formula);

        LongTextEntry additionalInfo = factory.createLongTextEntry("Additional " +
                "Information", "Additional Information");
        doc.addEntry(additionalInfo);
        additionalInfo.setSection(adjustedSec);

        return doc;
    }
}

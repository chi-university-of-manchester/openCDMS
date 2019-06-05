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

package org.psygrid.neden;

import org.psygrid.common.ValidationRulesWrapper;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.AssessmentForm;

public class Premorbid_Adjusted extends AssessmentForm {

    public static Document createDocument(Factory factory) {

        Document doc = factory.createDocument("Premorbid Summary Sheet",
                "Premorbid Adjustment Scale Scoring Summary");

        createDocumentStatuses(factory, doc);

        Section adjustedSec = factory.createSection("Adjusted scores section");
        doc.addSection(adjustedSec);
        adjustedSec.setDisplayText("Adjusted scores");
        SectionOccurrence adjustedSecOcc = factory
                .createSectionOccurrence("Adjusted scores section occurrence");
        adjustedSec.addOccurrence(adjustedSecOcc);

        NarrativeEntry childhoodNarrative3 = factory.createNarrativeEntry("Childhood",
        "Childhood");
        doc.addEntry(childhoodNarrative3);
        childhoodNarrative3.setSection(adjustedSec);
        childhoodNarrative3.setStyle(NarrativeStyle.HEADER);

        ValidationRule rule = ValidationRulesWrapper.instance().getRule("ZeroToSix");
        String description = "Please enter a number between 0 and 6.";

        NumericEntry sociabilityChildhood3 = factory.createNumericEntry("Sociability " +
                "and withdrawal", "Sociability and withdrawal");
        doc.addEntry(sociabilityChildhood3);
        sociabilityChildhood3.setSection(adjustedSec);
        sociabilityChildhood3.addValidationRule(rule);
        sociabilityChildhood3.setDescription(description);

        NumericEntry peerChildhood3 = factory.createNumericEntry("Peer relationships",
                "Peer relationships");
        doc.addEntry(peerChildhood3);
        peerChildhood3.setSection(adjustedSec);
        peerChildhood3.addValidationRule(rule);
        peerChildhood3.setDescription(description);

        NumericEntry scholasticChildhood3 = factory.createNumericEntry("Scholastic performance",
                "Scholastic performance");
        doc.addEntry(scholasticChildhood3);
        scholasticChildhood3.setSection(adjustedSec);
        scholasticChildhood3.addValidationRule(rule);
        scholasticChildhood3.setDescription(description);

        NumericEntry adaptionChildhood3 = factory.createNumericEntry("Adaption to school",
                "Adaption to school");
        doc.addEntry(adaptionChildhood3);
        adaptionChildhood3.setSection(adjustedSec);
        adaptionChildhood3.addValidationRule(rule);
        adaptionChildhood3.setDescription(description);

        //Early adolescence
        NarrativeEntry earlyAdNarrative3 = factory.createNarrativeEntry(
                "Early Adolescence", "Early Adolescence");
        doc.addEntry(earlyAdNarrative3);
        earlyAdNarrative3.setSection(adjustedSec);
        earlyAdNarrative3.setStyle(NarrativeStyle.HEADER);

        NumericEntry sociabilityEarlyAd3 = factory
                .createNumericEntry("Sociability and withdrawal",
                        "Sociability and withdrawal");
        doc.addEntry(sociabilityEarlyAd3);
        sociabilityEarlyAd3.setSection(adjustedSec);
        sociabilityEarlyAd3.addValidationRule(rule);
        sociabilityEarlyAd3.setDescription(description);

        NumericEntry peerEarlyAd3 = factory.createNumericEntry(
                "Peer relationships", "Peer relationships");
        doc.addEntry(peerEarlyAd3);
        peerEarlyAd3.setSection(adjustedSec);
        peerEarlyAd3.addValidationRule(rule);
        peerEarlyAd3.setDescription(description);

        NumericEntry scholasticEarlyAd3 = factory.createNumericEntry(
                "Scholastic performance", "Scholastic performance");
        doc.addEntry(scholasticEarlyAd3);
        scholasticEarlyAd3.setSection(adjustedSec);
        scholasticEarlyAd3.addValidationRule(rule);
        scholasticEarlyAd3.setDescription(description);

        NumericEntry adaptionEarlyAd3 = factory.createNumericEntry(
                "Adaption to school", "Adaption to school");
        doc.addEntry(adaptionEarlyAd3);
        adaptionEarlyAd3.setSection(adjustedSec);
        adaptionEarlyAd3.addValidationRule(rule);
        adaptionEarlyAd3.setDescription(description);

        NumericEntry socialEarlyAd3 = factory.createNumericEntry(
                "Social sexual aspects of life", "Social sexual aspects of life");
        doc.addEntry(socialEarlyAd3);
        socialEarlyAd3.setSection(adjustedSec);
        socialEarlyAd3.addValidationRule(rule);
        socialEarlyAd3.setDescription(description);

        //Late adolescence
        NarrativeEntry lateAdNarrative3 = factory.createNarrativeEntry(
                "Late Adolescence", "Late Adolescence");
        doc.addEntry(lateAdNarrative3);
        lateAdNarrative3.setSection(adjustedSec);
        lateAdNarrative3.setStyle(NarrativeStyle.HEADER);

        NumericEntry sociabilityLateAd3 = factory
                .createNumericEntry("Sociability and withdrawal",
                        "Sociability and withdrawal");
        doc.addEntry(sociabilityLateAd3);
        sociabilityLateAd3.setSection(adjustedSec);
        sociabilityLateAd3.addValidationRule(rule);
        sociabilityLateAd3.setDescription(description);

        NumericEntry peerLateAd3 = factory.createNumericEntry(
                "Peer relationships", "Peer relationships");
        doc.addEntry(peerLateAd3);
        peerLateAd3.setSection(adjustedSec);
        peerLateAd3.addValidationRule(rule);
        peerLateAd3.setDescription(description);

        NumericEntry scholasticLateAd3 = factory.createNumericEntry(
                "Scholastic performance", "Scholastic performance");
        doc.addEntry(scholasticLateAd3);
        scholasticLateAd3.setSection(adjustedSec);
        scholasticLateAd3.addValidationRule(rule);
        scholasticLateAd3.setDescription(description);

        NumericEntry adaptionLateAd3 = factory.createNumericEntry(
                "Adaption to school", "Adaption to school");
        doc.addEntry(adaptionLateAd3);
        adaptionLateAd3.setSection(adjustedSec);
        adaptionLateAd3.addValidationRule(rule);
        adaptionLateAd3.setDescription(description);

        NumericEntry socialLateAd3 = factory.createNumericEntry(
                "Social sexual aspects of life", "Social sexual aspects of life");
        doc.addEntry(socialLateAd3);
        socialLateAd3.setSection(adjustedSec);
        socialLateAd3.addValidationRule(rule);
        socialLateAd3.setDescription(description);

        //Adulthood
        NarrativeEntry adulthoodNarrative3 = factory.createNarrativeEntry(
                "Adulthood", "Adulthood");
        doc.addEntry(adulthoodNarrative3);
        adulthoodNarrative3.setSection(adjustedSec);
        adulthoodNarrative3.setStyle(NarrativeStyle.HEADER);

        NumericEntry sociabilityAdulthood3 = factory
                .createNumericEntry("Sociability and withdrawal",
                        "Sociability and withdrawal");
        doc.addEntry(sociabilityAdulthood3);
        sociabilityAdulthood3.setSection(adjustedSec);
        sociabilityAdulthood3.addValidationRule(rule);
        sociabilityAdulthood3.setDescription(description);

        NumericEntry peerAdulthood3 = factory.createNumericEntry(
                "Peer relationships", "Peer relationships");
        doc.addEntry(peerAdulthood3);
        peerAdulthood3.setSection(adjustedSec);
        peerAdulthood3.addValidationRule(rule);
        peerAdulthood3.setDescription(description);

        NumericEntry socialAdulthood3 = factory.createNumericEntry(
                "Social sexual aspects of life", "Social sexual aspects of life");
        doc.addEntry(socialAdulthood3);
        socialAdulthood3.setSection(adjustedSec);
        socialAdulthood3.addValidationRule(rule);
        socialAdulthood3.setDescription(description);

        //General
        NarrativeEntry general3 = factory.createNarrativeEntry("General",
                "General");
        doc.addEntry(general3);
        general3.setSection(adjustedSec);
        general3.setStyle(NarrativeStyle.HEADER);

        NumericEntry education3 = factory.createNumericEntry(
                "Education", "Education");
        doc.addEntry(education3);
        education3.setSection(adjustedSec);
        education3.addValidationRule(rule);
        education3.setDescription(description);
        education3.setLabel("1");

        NumericEntry employed3 = factory.createNumericEntry(
                "Employed/At School", "Employed/At School");
        doc.addEntry(employed3);
        employed3.setSection(adjustedSec);
        employed3.addValidationRule(rule);
        employed3.setDescription(description);
        employed3.setLabel("2");

        NumericEntry change3 = factory.createNumericEntry(
                "Change in Work/School Performance", "Change in Work/School Performance");
        doc.addEntry(change3);
        change3.setSection(adjustedSec);
        change3.addValidationRule(rule);
        change3.setDescription(description);
        change3.setLabel("3");

        NumericEntry jobChange3 = factory.createNumericEntry(
                "Job Change/Interrupted School Attendance",
                "Job Change/Interrupted School Attendance");
        doc.addEntry(jobChange3);
        jobChange3.setSection(adjustedSec);
        jobChange3.addValidationRule(rule);
        jobChange3.setDescription(description);
        jobChange3.setLabel("4");

        NumericEntry independence3 = factory.createNumericEntry(
                "Establishment of Independence", "Establishment of Independence");
        doc.addEntry(independence3);
        independence3.setSection(adjustedSec);
        independence3.addValidationRule(rule);
        independence3.setDescription(description);
        independence3.setLabel("5");

        NumericEntry functioning3 = factory.createNumericEntry(
                "Highest Level of Functioning Achieved in Patient's Life",
                "Highest Level of Functioning Achieved in Patient's Life");
        doc.addEntry(functioning3);
        functioning3.setSection(adjustedSec);
        functioning3.addValidationRule(rule);
        functioning3.setDescription(description);
        functioning3.setLabel("6");

        NumericEntry adjustment3 = factory.createNumericEntry(
                "Social Personal Adjustment", "Social Personal Adjustment");
        doc.addEntry(adjustment3);
        adjustment3.setSection(adjustedSec);
        adjustment3.addValidationRule(rule);
        adjustment3.setDescription(description);
        adjustment3.setLabel("7");

        NumericEntry interest3 = factory.createNumericEntry(
                "Degree of Interest in Life", "Degree of Interest in Life");
        doc.addEntry(interest3);
        interest3.setSection(adjustedSec);
        interest3.addValidationRule(rule);
        interest3.setDescription(description);
        interest3.setLabel("8");

        NumericEntry energy3 = factory.createNumericEntry(
                "Energy Level", "Energy Level");
        doc.addEntry(energy3);
        energy3.setSection(adjustedSec);
        energy3.addValidationRule(rule);
        energy3.setDescription(description);
        energy3.setLabel("9");

        LongTextEntry additionalInfo = factory.createLongTextEntry("Additional " +
                "Information", "Additional Information");
        doc.addEntry(additionalInfo);
        additionalInfo.setSection(adjustedSec);

        return doc;

    }
}
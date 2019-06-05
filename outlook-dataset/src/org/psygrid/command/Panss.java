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

import org.psygrid.common.ValidationRulesWrapper;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.AssessmentForm;

public class Panss extends AssessmentForm {

    public static Document createDocument(Factory factory){

        ValidationRule panssRule = ValidationRulesWrapper.instance().getRule("PANSS validation rule");

        Document panss = factory.createDocument("PANSS",
                "Positive and Negative Syndrome Scale for Schizophrenia (PANSS)");

        createDocumentStatuses(factory, panss);

        //instructions section
        Section header = factory.createSection("Header");
        panss.addSection(header);
        header.setDisplayText("Instructions");
        SectionOccurrence headerOcc = factory.createSectionOccurrence("Main");
        header.addOccurrence(headerOcc);

        NarrativeEntry intro = factory.createNarrativeEntry("Introduction");
        intro.setDisplayText("Instructions: refer to the rating manual (in study tools) for item definitions, descriptions, " +
                             "anchoring points and scoring procedure. Also, refer to Sci-PANSS for help with interview instruction " +
                             "(in study tools). Select from one of the codes below to complete the boxes: 1=absent, 2=minimal, "+
                             "3=mild, 4=moderate, 5=moderate severe, 6=severe, 7=extreme.");
        panss.addEntry(intro);
        intro.setSection(header);

        //positive scale section
        Section positiveScale = factory.createSection("Positive Scale", "Positive Scale");
        panss.addSection(positiveScale);
        positiveScale.setDisplayText("Positive Scale");
        SectionOccurrence positiveScaleOcc = factory.createSectionOccurrence("Positive Scale Occ");
        positiveScale.addOccurrence(positiveScaleOcc);

        NumericEntry delusions = factory.createNumericEntry("Delusions", "Delusions");
        panss.addEntry(delusions);
        delusions.setSection(positiveScale);
        delusions.setLabel("P1");
        delusions.addValidationRule(panssRule);

        NumericEntry conceptualDisorganization = factory.createNumericEntry("Conceptual disorganization", "Conceptual disorganization");
        panss.addEntry(conceptualDisorganization);
        conceptualDisorganization.setSection(positiveScale);
        conceptualDisorganization.setLabel("P2");
        conceptualDisorganization.addValidationRule(panssRule);

        NumericEntry hallucinatoryBehaviour = factory.createNumericEntry("Hallucinatory behaviour", "Hallucinatory behaviour");
        panss.addEntry(hallucinatoryBehaviour);
        hallucinatoryBehaviour.setSection(positiveScale);
        hallucinatoryBehaviour.setLabel("P3");
        hallucinatoryBehaviour.addValidationRule(panssRule);

        NumericEntry excitement = factory.createNumericEntry("Excitement", "Excitement");
        panss.addEntry(excitement);
        excitement.setSection(positiveScale);
        excitement.setLabel("P4");
        excitement.addValidationRule(panssRule);

        NumericEntry grandiosity = factory.createNumericEntry("Grandiosity", "Grandiosity");
        panss.addEntry(grandiosity);
        grandiosity.setSection(positiveScale);
        grandiosity.setLabel("P5");
        grandiosity.addValidationRule(panssRule);

        NumericEntry suspiciousnessPersecution = factory.createNumericEntry("Suspiciousness / persecution", "Suspiciousness / persecution");
        panss.addEntry(suspiciousnessPersecution);
        suspiciousnessPersecution.setSection(positiveScale);
        suspiciousnessPersecution.setLabel("P6");
        suspiciousnessPersecution.addValidationRule(panssRule);

        NumericEntry hostility = factory.createNumericEntry("Hostility", "Hostility");
        panss.addEntry(hostility);
        hostility.setSection(positiveScale);
        hostility.setLabel("P7");
        hostility.addValidationRule(panssRule);

        DerivedEntry subtotalPositive = factory.createDerivedEntry("Positive subtotal", "Subtotal (positive syndrome)");
        panss.addEntry(subtotalPositive);
        subtotalPositive.setSection(positiveScale);
        subtotalPositive.setDescription("Sum of P1 through P7");
        subtotalPositive.setFormula("d+cd+hb+e+g+sp+h");
        subtotalPositive.addVariable("d", delusions);
        subtotalPositive.addVariable("cd", conceptualDisorganization);
        subtotalPositive.addVariable("hb", hallucinatoryBehaviour);
        subtotalPositive.addVariable("e", excitement);
        subtotalPositive.addVariable("g", grandiosity);
        subtotalPositive.addVariable("sp", suspiciousnessPersecution);
        subtotalPositive.addVariable("h", hostility);


        //negative scale section
        Section negativeScale = factory.createSection("Negative Scale", "Negative Scale");
        panss.addSection(negativeScale);
        negativeScale.setDisplayText("Negative Scale");
        SectionOccurrence negativeScaleOcc = factory.createSectionOccurrence("Negative Scale Occurrence");
        negativeScale.addOccurrence(negativeScaleOcc);

        NumericEntry bluntedAffect = factory.createNumericEntry("Blunted affect", "Blunted affect");
        panss.addEntry(bluntedAffect);
        bluntedAffect.setSection(negativeScale);
        bluntedAffect.setLabel("N1");
        bluntedAffect.addValidationRule(panssRule);

        NumericEntry emotionalWithdrawl = factory.createNumericEntry("Emotional withdrawal", "Emotional withdrawal");
        panss.addEntry(emotionalWithdrawl);
        emotionalWithdrawl.setSection(negativeScale);
        emotionalWithdrawl.setLabel("N2");
        emotionalWithdrawl.addValidationRule(panssRule);

        NumericEntry poorRapport = factory.createNumericEntry("Poor rapport", "Poor rapport");
        panss.addEntry(poorRapport);
        poorRapport.setSection(negativeScale);
        poorRapport.setLabel("N3");
        poorRapport.addValidationRule(panssRule);

        NumericEntry socialWithdrawl = factory.createNumericEntry("Social withdrawal", "Passive/apathetic social withdrawal");
        panss.addEntry(socialWithdrawl);
        socialWithdrawl.setSection(negativeScale);
        socialWithdrawl.setLabel("N4");
        socialWithdrawl.addValidationRule(panssRule);

        NumericEntry abstractThinking = factory.createNumericEntry("Abstract thinking", "Difficulty in abstract thinking");
        panss.addEntry(abstractThinking);
        abstractThinking.setSection(negativeScale);
        abstractThinking.setLabel("N5");
        abstractThinking.addValidationRule(panssRule);

        NumericEntry lackOfSpontaneity = factory.createNumericEntry("Lack of spontaneity", "Lack of spontaneity and flow of conversation");
        panss.addEntry(lackOfSpontaneity);
        lackOfSpontaneity.setSection(negativeScale);
        lackOfSpontaneity.setLabel("N6");
        lackOfSpontaneity.addValidationRule(panssRule);

        NumericEntry stereotypedThinking = factory.createNumericEntry("Stereotyped thinking", "Stereotyped thinking");
        panss.addEntry(stereotypedThinking);
        stereotypedThinking.setSection(negativeScale);
        stereotypedThinking.setLabel("N7");
        stereotypedThinking.addValidationRule(panssRule);

        DerivedEntry subtotalNegative = factory.createDerivedEntry("Negative subtotal", "Subtotal (negative syndrome)");
        panss.addEntry(subtotalNegative);
        subtotalNegative.setSection(negativeScale);
        subtotalNegative.setDescription("sum of N1 through N7");
        subtotalNegative.setFormula("ba+ew+pr+sw+at+ls+st");
        subtotalNegative.addVariable("ba", bluntedAffect);
        subtotalNegative.addVariable("ew", emotionalWithdrawl);
        subtotalNegative.addVariable("pr", poorRapport);
        subtotalNegative.addVariable("sw", socialWithdrawl);
        subtotalNegative.addVariable("at", abstractThinking);
        subtotalNegative.addVariable("ls", lackOfSpontaneity);
        subtotalNegative.addVariable("st", stereotypedThinking);

        DerivedEntry compositeIndex = factory.createDerivedEntry("Composite Index", "Composite Index");
        panss.addEntry(compositeIndex);
        compositeIndex.setSection(negativeScale);
        compositeIndex.setDescription("positive syndrome minus negative syndrome");
        compositeIndex.setFormula("ps-ns");
        compositeIndex.addVariable("ps", subtotalPositive);
        compositeIndex.addVariable("ns", subtotalNegative);


        //general scale section
        Section generalScale = factory.createSection("General scale", "General Psychopathology Scale");
        panss.addSection(generalScale);
        generalScale.setDisplayText("General Scale");
        SectionOccurrence generalScaleOcc = factory.createSectionOccurrence("General scale occurrence");
        generalScale.addOccurrence(generalScaleOcc);

        NumericEntry somaticConcern = factory.createNumericEntry("Somatic concern", "Somatic concern");
        panss.addEntry(somaticConcern);
        somaticConcern.setSection(generalScale);
        somaticConcern.setLabel("G1");
        somaticConcern.addValidationRule(panssRule);

        NumericEntry anxiety = factory.createNumericEntry("Anxiety", "Anxiety");
        panss.addEntry(anxiety);
        anxiety.setSection(generalScale);
        anxiety.setLabel("G2");
        anxiety.addValidationRule(panssRule);

        NumericEntry guiltFeelings = factory.createNumericEntry("Guilt feelings", "Guilt feelings");
        panss.addEntry(guiltFeelings);
        guiltFeelings.setSection(generalScale);
        guiltFeelings.setLabel("G3");
        guiltFeelings.addValidationRule(panssRule);

        NumericEntry tension = factory.createNumericEntry("Tension", "Tension");
        panss.addEntry(tension);
        tension.setSection(generalScale);
        tension.setLabel("G4");
        tension.addValidationRule(panssRule);

        NumericEntry mannerisms = factory.createNumericEntry("Mannerisms", "Mannerisms and posturing");
        panss.addEntry(mannerisms);
        mannerisms.setSection(generalScale);
        mannerisms.setLabel("G5");
        mannerisms.addValidationRule(panssRule);

        NumericEntry depression = factory.createNumericEntry("Depression", "Depression");
        panss.addEntry(depression);
        depression.setSection(generalScale);
        depression.setLabel("G6");
        depression.addValidationRule(panssRule);

        NumericEntry motorRetardation = factory.createNumericEntry("Motor retardation", "Motor retardation");
        panss.addEntry(motorRetardation);
        motorRetardation.setSection(generalScale);
        motorRetardation.setLabel("G7");
        motorRetardation.addValidationRule(panssRule);

        NumericEntry uncooperativeness = factory.createNumericEntry("Uncooperativeness", "Uncooperativeness");
        panss.addEntry(uncooperativeness);
        uncooperativeness.setSection(generalScale);
        uncooperativeness.setLabel("G8");
        uncooperativeness.addValidationRule(panssRule);

        NumericEntry unusualThought = factory.createNumericEntry("Unusual thought content", "Unusual thought content");
        panss.addEntry(unusualThought);
        unusualThought.setSection(generalScale);
        unusualThought.setLabel("G9");
        unusualThought.addValidationRule(panssRule);

        NumericEntry disorientation = factory.createNumericEntry("Disorientation", "Disorientation");
        panss.addEntry(disorientation);
        disorientation.setSection(generalScale);
        disorientation.setLabel("G10");
        disorientation.addValidationRule(panssRule);

        NumericEntry poorAttention = factory.createNumericEntry("Poor attention", "Poor attention");
        panss.addEntry(poorAttention);
        poorAttention.setSection(generalScale);
        poorAttention.setLabel("G11");
        poorAttention.addValidationRule(panssRule);

        NumericEntry lackOfJudgement = factory.createNumericEntry("Lack of judgement", "Lack of judgement and insight");
        panss.addEntry(lackOfJudgement);
        lackOfJudgement.setSection(generalScale);
        lackOfJudgement.setLabel("G12");
        lackOfJudgement.addValidationRule(panssRule);

        NumericEntry disturbanceOfVolition = factory.createNumericEntry("Disturbance of volition", "Disturbance of volition");
        panss.addEntry(disturbanceOfVolition);
        disturbanceOfVolition.setSection(generalScale);
        disturbanceOfVolition.setLabel("G13");
        disturbanceOfVolition.addValidationRule(panssRule);

        NumericEntry poorImpulseControl = factory.createNumericEntry("Poor impulse control", "Poor impulse control");
        panss.addEntry(poorImpulseControl);
        poorImpulseControl.setSection(generalScale);
        poorImpulseControl.setLabel("G14");
        poorImpulseControl.addValidationRule(panssRule);

        NumericEntry preoccupation = factory.createNumericEntry("Preoccupation", "Preoccupation");
        panss.addEntry(preoccupation);
        preoccupation.setSection(generalScale);
        preoccupation.setLabel("G15");
        preoccupation.addValidationRule(panssRule);

        NumericEntry socialAvoidance = factory.createNumericEntry("Active social avoidance", "Active social avoidance");
        panss.addEntry(socialAvoidance);
        socialAvoidance.setSection(generalScale);
        socialAvoidance.setLabel("G16");
        socialAvoidance.addValidationRule(panssRule);

        DerivedEntry subtotalGeneral = factory.createDerivedEntry("General subtotal", "Subtotal (general psychopathology)");
        panss.addEntry(subtotalGeneral);
        subtotalGeneral.setSection(generalScale);
        subtotalGeneral.setDescription("sum of G1 through G16");
        subtotalGeneral.setFormula("sc+a+gf+t+mp+de+mr+u+utc+di+pa+lj+dv+pic+p+asa");
        subtotalGeneral.addVariable("sc", somaticConcern);
        subtotalGeneral.addVariable("a", anxiety);
        subtotalGeneral.addVariable("gf", guiltFeelings);
        subtotalGeneral.addVariable("t", tension);
        subtotalGeneral.addVariable("mp", mannerisms);
        subtotalGeneral.addVariable("de", depression);
        subtotalGeneral.addVariable("mr", motorRetardation);
        subtotalGeneral.addVariable("u", uncooperativeness);
        subtotalGeneral.addVariable("utc", unusualThought);
        subtotalGeneral.addVariable("di", disorientation);
        subtotalGeneral.addVariable("pa", poorAttention);
        subtotalGeneral.addVariable("lj", lackOfJudgement);
        subtotalGeneral.addVariable("dv", disturbanceOfVolition);
        subtotalGeneral.addVariable("pic", poorImpulseControl);
        subtotalGeneral.addVariable("p", preoccupation);
        subtotalGeneral.addVariable("asa", socialAvoidance);

        DerivedEntry total = factory.createDerivedEntry("Total score", "Total PANSS score");
        panss.addEntry(total);
        total.setSection(generalScale);
        total.setDescription("this is the sum of the 3 subtotals");
        total.setFormula("p+n+g");
        total.addVariable("p", subtotalPositive);
        total.addVariable("n", subtotalNegative);
        total.addVariable("g", subtotalGeneral);

        return panss;
    }

}

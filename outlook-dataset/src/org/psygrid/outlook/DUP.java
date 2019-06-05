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

public class DUP extends AssessmentForm {

    public static Document createDocument(Factory factory){

        ValidationRule after1900 = ValidationRulesWrapper.instance().getRule("After 1900");

        Document dup = factory.createDocument("DUP", "Duration of Untreated Psychosis");

        createDocumentStatuses(factory, dup);

        Section mainSec = factory.createSection("Main", "Main");
        dup.addSection(mainSec);
        mainSec.setDisplayText("Main");
        SectionOccurrence mainOcc = factory.createSectionOccurrence("Main section occurrence");
        mainSec.addOccurrence(mainOcc);

        DateEntry onsetNonSpecific = factory.createDateEntry("Onset Non Specific", "Onset of non-specific symptoms");
        dup.addEntry(onsetNonSpecific);
        onsetNonSpecific.setSection(mainSec);
        onsetNonSpecific.setLabel("A");
        onsetNonSpecific.addValidationRule(after1900);

        DateEntry onsetPsychosis = factory.createDateEntry("Onset Psychosis", "Onset of Psychosis");
        dup.addEntry(onsetPsychosis);
        onsetPsychosis.setSection(mainSec);
        onsetPsychosis.setLabel("B");
        onsetPsychosis.addValidationRule(after1900);

        DateEntry firstDecision = factory.createDateEntry("1st Decision Care", "1st Decision to seek care");
        dup.addEntry(firstDecision);
        firstDecision.setSection(mainSec);
        firstDecision.setLabel("C");
        firstDecision.addValidationRule(after1900);

        DateEntry firstReferal = factory.createDateEntry("1st Referral Received", "1st Referral received by mental health services");
        dup.addEntry(firstReferal);
        firstReferal.setSection(mainSec);
        firstReferal.setLabel("D");
        firstReferal.addValidationRule(after1900);

        DateEntry onsetCriteria = factory.createDateEntry("OCT", "Onset of criteria treatment (OCT)");
        dup.addEntry(onsetCriteria);
        onsetCriteria.setSection(mainSec);
        onsetCriteria.setLabel("E");
        onsetCriteria.addValidationRule(after1900);

        DateEntry onsetSubthreshold = factory.createDateEntry("Onset subthreshold", "Onset of subthreshold treatment");
        dup.addEntry(onsetSubthreshold);
        onsetSubthreshold.setSection(mainSec);
        onsetSubthreshold.addValidationRule(after1900);

        DateEntry referral = factory.createDateEntry("Referral date", "Referral date to EIS");
        dup.addEntry(referral);
        referral.setSection(mainSec);
        referral.setLabel("F");
        referral.addValidationRule(after1900);

        DateEntry accepted = factory.createDateEntry("Accepted date", "Taken on by EIS");
        dup.addEntry(accepted);
        accepted.setSection(mainSec);
        accepted.setLabel("G");
        accepted.addValidationRule(after1900);

        DerivedEntry durationProdromal = factory.createDerivedEntry("Prodromal duration", "Duration of prodromal period");
        dup.addEntry(durationProdromal);
        durationProdromal.setSection(mainSec);
        durationProdromal.setLabel("1");
        durationProdromal.setDescription("B-A");
        durationProdromal.setFormula("b-a");
        durationProdromal.addVariable("a", onsetNonSpecific);
        durationProdromal.addVariable("b", onsetPsychosis);

        DerivedEntry delayHelpSeeking = factory.createDerivedEntry("Delay help seeking", "Delay in help seeking");
        dup.addEntry(delayHelpSeeking);
        delayHelpSeeking.setSection(mainSec);
        delayHelpSeeking.setLabel("2");
        delayHelpSeeking.setDescription("C-B");
        delayHelpSeeking.setFormula("c-b");
        delayHelpSeeking.addVariable("b", onsetPsychosis);
        delayHelpSeeking.addVariable("c", firstDecision);

        DerivedEntry delayHelpSeekingPathway = factory.createDerivedEntry("Delay health seeking pathway", "Delay in help-seeking pathway");
        dup.addEntry(delayHelpSeekingPathway);
        delayHelpSeekingPathway.setSection(mainSec);
        delayHelpSeekingPathway.setLabel("3");
        delayHelpSeekingPathway.setDescription("D-C");
        delayHelpSeekingPathway.setFormula("d-c");
        delayHelpSeekingPathway.addVariable("c", firstDecision);
        delayHelpSeekingPathway.addVariable("d", firstReferal);

        DerivedEntry delayMHS = factory.createDerivedEntry("Delay within MHS", "Delay within mental health services");
        dup.addEntry(delayMHS);
        delayMHS.setSection(mainSec);
        delayMHS.setLabel("4");
        delayMHS.setDescription("E-D");
        delayMHS.setFormula("e-d");
        delayMHS.addVariable("d", firstReferal);
        delayMHS.addVariable("e", onsetCriteria);

        DerivedEntry delayEIS = factory.createDerivedEntry("Delay EIS", "Delay in reaching EIS (client's involvement with other agencies prior to referral to EIS)");
        dup.addEntry(delayEIS);
        delayEIS.setSection(mainSec);
        delayEIS.setLabel("5");
        delayEIS.setDescription("F-C");
        delayEIS.setFormula("f-c");
        delayEIS.addVariable("c", firstDecision);
        delayEIS.addVariable("f", referral);

        //13
        DerivedEntry durationUntreatedPsychosis = factory.createDerivedEntry("Duration Untreated Psychosis", "Duration of untreated psychosis");
        dup.addEntry(durationUntreatedPsychosis);
        durationUntreatedPsychosis.setSection(mainSec);
        durationUntreatedPsychosis.setLabel("6");
        durationUntreatedPsychosis.setDescription("E-B");
        durationUntreatedPsychosis.setFormula("e-b");
        durationUntreatedPsychosis.addVariable("b", onsetPsychosis);
        durationUntreatedPsychosis.addVariable("e", onsetCriteria);

        DerivedEntry durationUntreatedIllness = factory.createDerivedEntry("Duration untreated illness", "Duration of untreated illness");
        dup.addEntry(durationUntreatedIllness);
        durationUntreatedIllness.setSection(mainSec);
        durationUntreatedIllness.setLabel("7");
        durationUntreatedIllness.setDescription("E-A");
        durationUntreatedIllness.setFormula("e-a");
        durationUntreatedIllness.addVariable("a", onsetNonSpecific);
        durationUntreatedIllness.addVariable("e", onsetCriteria);

        OptionEntry helpSought = factory.createOptionEntry("Help sought", "Was help sought in the prodromal phase?");
        dup.addEntry(helpSought);
        helpSought.setSection(mainSec);
        helpSought.setLabel("8");
        Option helpNo = factory.createOption("No", 0);
        Option helpYes = factory.createOption("Yes", 1);
        helpSought.addOption(helpNo);
        helpSought.addOption(helpYes);

        return dup;
    }
}

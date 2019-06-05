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

public class WHOScreeningSchedule extends AssessmentForm {

    public static Document createDocument(Factory factory) {

        Document doc = factory.createDocument(
                "WHO Screening Schedule",
                "Screening Schedule For Psychosis");

        createDocumentStatuses(factory, doc);

        //Main Section
        Section mainSec = factory.createSection("Main section");
        doc.addSection(mainSec);
        mainSec.setDisplayText("Main");
        SectionOccurrence mainSecOcc = factory.createSectionOccurrence(
                "Main section occurrence");
        mainSec.addOccurrence(mainSecOcc);

        NarrativeEntry ageAreaNarra = factory.createNarrativeEntry(
                "Patients Age and Area Narrative",
                "Patient's Age and Location");
        doc.addEntry(ageAreaNarra);
        ageAreaNarra.setSection(mainSec);
        ageAreaNarra.setLabel("A");

        OptionEntry patientsAge = factory.createOptionEntry(
                "Patient's age",
                "Is this patient's age below 14 or above 65?");
        doc.addEntry(patientsAge);
        patientsAge.setSection(mainSec);
        patientsAge.setLabel("1");
        patientsAge.setOptionCodesDisplayed(false);
        patientsAge.addOption(factory.createOption("No", 0));
        patientsAge.addOption(factory.createOption("Yes", 1));

        OptionEntry patientsArea = factory.createOptionEntry(
                "Patient's area",
                "Does this patient live outside the study catchment area?");
        doc.addEntry(patientsArea);
        patientsArea.setSection(mainSec);
        patientsArea.setLabel("2");
        patientsArea.setOptionCodesDisplayed(false);
        patientsArea.addOption(factory.createOption("No", 0));
        patientsArea.addOption(factory.createOption("Yes", 1));

        NarrativeEntry problemsNarra = factory.createNarrativeEntry(
                "Problems Narrative",
                "Is there evidence that this patient has any of the following problems?");
        doc.addEntry(problemsNarra);
        problemsNarra.setSection(mainSec);
        problemsNarra.setLabel("B");

        OptionEntry clinicallyManifested = factory.createOptionEntry(
                "Clinically manifested disorder",
                "Clinically manifest organic cerebral disorder (e.g. infections, parasitic, toxic, cerebrovascular, epilepsy, brain injury, etc.)");
        doc.addEntry(clinicallyManifested);
        clinicallyManifested.setSection(mainSec);
        clinicallyManifested.setLabel("1");
        clinicallyManifested.setOptionCodesDisplayed(false);
        clinicallyManifested.addOption(factory.createOption("No", 0));
        clinicallyManifested.addOption(factory.createOption("Yes", 1));

        OptionEntry mentalRetardation = factory.createOptionEntry(
                "Mental retardation",
                "Severe or moderate mental retardation (i.e. IQ less than 50 or clinically manifest as such)");
        doc.addEntry(mentalRetardation);
        mentalRetardation.setSection(mainSec);
        mentalRetardation.setLabel("2");
        mentalRetardation.setOptionCodesDisplayed(false);
        mentalRetardation.addOption(factory.createOption("No", 0));
        mentalRetardation.addOption(factory.createOption("Yes", 1));

        NarrativeEntry presentedNarra = factory.createNarrativeEntry(
                "Presented Narrative",
                "Has the patient ever presented any of the following?");
        doc.addEntry(presentedNarra);
        presentedNarra.setSection(mainSec);
        presentedNarra.setLabel("C");

        OptionEntry hallucinations = factory.createOptionEntry(
                "Hallucinations",
                "Hallucinations or pseudo-hallucinations in any modality");
        doc.addEntry(hallucinations);
        hallucinations.setSection(mainSec);
        hallucinations.setLabel("1");
        hallucinations.setOptionCodesDisplayed(false);
        hallucinations.addOption(factory.createOption("No", 0));
        hallucinations.addOption(factory.createOption("Yes", 1));
        hallucinations.addOption(factory.createOption("Unclear", 2));

        OptionEntry delusions = factory.createOptionEntry(
                "Delusions",
                "Delusions");
        doc.addEntry(delusions);
        delusions.setSection(mainSec);
        delusions.setLabel("2");
        delusions.setOptionCodesDisplayed(false);
        delusions.addOption(factory.createOption("No", 0));
        delusions.addOption(factory.createOption("Yes", 1));
        delusions.addOption(factory.createOption("Unclear", 2));

        OptionEntry speechDisorder = factory.createOptionEntry(
                "Thought and Speech Disorder",
                "Marked thought and speech disorder (e.g. incoherence, irrelevance, " +
                        "thought blocking, nelogisms, incomprehensibility of speech) other " +
                        "than simple retardation or acceleration");
        doc.addEntry(speechDisorder);
        speechDisorder.setSection(mainSec);
        speechDisorder.setLabel("3");
        speechDisorder.setOptionCodesDisplayed(false);
        speechDisorder.addOption(factory.createOption("No", 0));
        speechDisorder.addOption(factory.createOption("Yes", 1));
        speechDisorder.addOption(factory.createOption("Unclear", 2));

        OptionEntry psychomotorDisorder = factory.createOptionEntry(
                "Psychomotor disorder",
                "Marked psychomotor disorder (e.g., negativism, mutism or stupor, " +
                        "catatonic excitement, constrained attitudes or unnatural postures " +
                        "maintained for long periods) other than simple retardation or acceleration");
        doc.addEntry(psychomotorDisorder);
        psychomotorDisorder.setSection(mainSec);
        psychomotorDisorder.setLabel("4");
        psychomotorDisorder.setOptionCodesDisplayed(false);
        psychomotorDisorder.addOption(factory.createOption("No", 0));
        psychomotorDisorder.addOption(factory.createOption("Yes", 1));
        psychomotorDisorder.addOption(factory.createOption("Unclear", 2));

        OptionEntry inappropriateBehaviour = factory.createOptionEntry(
                "Inappropriate Behaviour",
                "Emergence or marked exacerbation of bizarre and grossly inappropriate " +
                        "behaviour (e.g. talking or giggling to self, acts incomprehensible to " +
                        "others, loss of social constraints, etc.)");
        doc.addEntry(inappropriateBehaviour);
        inappropriateBehaviour.setSection(mainSec);
        inappropriateBehaviour.setLabel("5");
        inappropriateBehaviour.setOptionCodesDisplayed(false);
        inappropriateBehaviour.addOption(factory.createOption("No", 0));
        inappropriateBehaviour.addOption(factory.createOption("Yes", 1));
        inappropriateBehaviour.addOption(factory.createOption("Unclear", 2));

        NarrativeEntry changeNarra = factory.createNarrativeEntry(
                "Change of personality Narrative",
                "A definite change of personality and behaviour manifested in any of the following");
        doc.addEntry(changeNarra);
        changeNarra.setSection(mainSec);
        changeNarra.setLabel("D");

        OptionEntry lossInterests = factory.createOptionEntry(
                "Loss of interests",
                "Marked reduction or loss of interests, initiative and drive, leading " +
                        "to serious deterioration of the performance of usual activities and tasks");
        doc.addEntry(lossInterests);
        lossInterests.setSection(mainSec);
        lossInterests.setLabel("1");
        lossInterests.setOptionCodesDisplayed(false);
        lossInterests.addOption(factory.createOption("No", 0));
        lossInterests.addOption(factory.createOption("Yes", 1));
        lossInterests.addOption(factory.createOption("Unclear", 2));

        OptionEntry socialWithdrawl = factory.createOptionEntry(
                "Social withdrawl",
                "Emergence of marked exacerbation of social withdrawal (active " +
                        "avoidance of communication with other people)");
        doc.addEntry(socialWithdrawl);
        socialWithdrawl.setSection(mainSec);
        socialWithdrawl.setLabel("2");
        socialWithdrawl.setOptionCodesDisplayed(false);
        socialWithdrawl.addOption(factory.createOption("No", 0));
        socialWithdrawl.addOption(factory.createOption("Yes", 1));
        socialWithdrawl.addOption(factory.createOption("Unclear", 2));

        OptionEntry aggression = factory.createOptionEntry(
                "Aggression",
                "Severe excitement, purposeless destructiveness or aggression");
        doc.addEntry(aggression);
        aggression.setSection(mainSec);
        aggression.setLabel("3");
        aggression.setOptionCodesDisplayed(false);
        aggression.addOption(factory.createOption("No", 0));
        aggression.addOption(factory.createOption("Yes", 1));
        aggression.addOption(factory.createOption("Unclear", 2));

        OptionEntry anxiety = factory.createOptionEntry(
                "Anxiety",
                "Episodic or persistent states of overwhelming fear or severe anxiety");
        doc.addEntry(anxiety);
        anxiety.setSection(mainSec);
        anxiety.setLabel("4");
        anxiety.setOptionCodesDisplayed(false);
        anxiety.addOption(factory.createOption("No", 0));
        anxiety.addOption(factory.createOption("Yes", 1));
        anxiety.addOption(factory.createOption("Unclear", 2));

        OptionEntry selfNeglect = factory.createOptionEntry(
                "Self neglect",
                "Gross and persistent self-neglect");
        doc.addEntry(selfNeglect);
        selfNeglect.setSection(mainSec);
        selfNeglect.setLabel("5");
        selfNeglect.setOptionCodesDisplayed(false);
        selfNeglect.addOption(factory.createOption("No", 0));
        selfNeglect.addOption(factory.createOption("Yes", 1));
        selfNeglect.addOption(factory.createOption("Unclear", 2));

        OptionEntry contact = factory.createOptionEntry(
                "Contact with psychiatric services",
                "Has the patient ever made contact with the psychiatric services for " +
                        "symptoms C or D which could be considered to represent a previous " +
                        "episode?  Do not exclude patients with contact for symptoms outside " +
                        "the criteria of C or D (e.g. affective illness).");
        doc.addEntry(contact);
        contact.setSection(mainSec);
        contact.setLabel("E");
        contact.setOptionCodesDisplayed(false);
        contact.addOption(factory.createOption("No", 0));
        contact.addOption(factory.createOption("Yes", 1));

        DerivedEntry satisfiesCriteria = factory.createDerivedEntry(
                "Satisfies criteria",
                "Patient satisfies entry criteria - 0 = NO, 1 = YES");
        doc.addEntry(satisfiesCriteria);
        satisfiesCriteria.setSection(mainSec);
        satisfiesCriteria.setDescription(
                "Conditions for inclusion in the study: All replies to questions in "+
                "Sections A, B and E must be 'NO' and there should be at least one "+
                "'YES' in Section C or two in Section D.");
        satisfiesCriteria.setFormula(
                "if(((a1+a2+b1+b2+e)==0)&&(((if(c1==1,1,0)+if(c2==1,1,0)+if(c3==1,1,0)+if(c4==1,1,0)+if(c5==1,1,0))>=1)||((if(d1==1,1,0)+if(d2==1,1,0)+if(d3==1,1,0)+if(d4==1,1,0)+if(d5==1,1,0))>=2)), 1, 0)");
        satisfiesCriteria.addVariable("a1",patientsAge);
        satisfiesCriteria.addVariable("a2",patientsArea);
        satisfiesCriteria.addVariable("b1",clinicallyManifested);
        satisfiesCriteria.addVariable("b2",mentalRetardation);
        satisfiesCriteria.addVariable("c1",hallucinations);
        satisfiesCriteria.addVariable("c2",delusions);
        satisfiesCriteria.addVariable("c3",speechDisorder);
        satisfiesCriteria.addVariable("c4",psychomotorDisorder);
        satisfiesCriteria.addVariable("c5",inappropriateBehaviour);
        satisfiesCriteria.addVariable("d1",lossInterests);
        satisfiesCriteria.addVariable("d2",socialWithdrawl);
        satisfiesCriteria.addVariable("d3",aggression);
        satisfiesCriteria.addVariable("d4",anxiety);
        satisfiesCriteria.addVariable("d5",selfNeglect);
        satisfiesCriteria.addVariable("e",contact);

        return doc;
    }
}

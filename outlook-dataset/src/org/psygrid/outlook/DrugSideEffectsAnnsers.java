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

import java.util.ArrayList;
import java.util.List;

import org.psygrid.common.UnitWrapper;
import org.psygrid.common.ValidationRulesWrapper;
import org.psygrid.data.model.hibernate.*;

public class DrugSideEffectsAnnsers extends AssessmentForm {

    public static Document createDocument(Factory factory) {

        List<OptionEntry> severityQuestions = new ArrayList<OptionEntry>();

        ValidationRule zeroToThirty = ValidationRulesWrapper.instance().getRule("ZeroToThirty");

        Document annsers = factory.createDocument("ANNSERS", "Antipsychotic Non-Neurological Side-Effects Rating Scale (ANNSERS)");

        createDocumentStatuses(factory, annsers);

        //---------------------------------------------------------------------------
        // Instructions section
        //---------------------------------------------------------------------------
        Section instrSec = factory.createSection("Instructions");
        annsers.addSection(instrSec);
        instrSec.setDisplayText("Instructions");
        SectionOccurrence instrSecOcc = factory.createSectionOccurrence("Default");
        instrSec.addOccurrence(instrSecOcc);

        NarrativeEntry instructions  = factory.createNarrativeEntry("Instructions", "For each side effect that is identified as having been present in the past month, please tick the appropriate boxes. Extrapyramidal side effects (parkinsonism, akathisia, dystonia, tardive dyskinesia) are to be rated on separate, specific rating scales.");
        annsers.addEntry(instructions);
        instructions.setSection(instrSec);

        //---------------------------------------------------------------------------
        // General section
        //---------------------------------------------------------------------------
        Section generalSec = factory.createSection("General", "General");
        annsers.addSection(generalSec);
        generalSec.setDisplayText("General");
        SectionOccurrence generalSecOcc = factory.createSectionOccurrence("Default");
        generalSec.addOccurrence(generalSecOcc);

        //Headache
        severityQuestions.add(addAnnsersQuestion(annsers, generalSec, "Headache", "1", null, factory));

        //Lethargy/Lassitude
        severityQuestions.add(addAnnsersQuestion(annsers, generalSec, "Lethargy/Lassitude", "2", null, factory));

        //---------------------------------------------------------------------------
        // Sleep disturbance section
        //---------------------------------------------------------------------------
        Section sleepDistSec = factory.createSection("Sleep disturbance", "Sleep disturbance");
        annsers.addSection(sleepDistSec);
        sleepDistSec.setDisplayText("Sleep disturbance");
        SectionOccurrence sleepDistSecOcc = factory.createSectionOccurrence("Default");
        sleepDistSec.addOccurrence(sleepDistSecOcc);

        //Night sleep pattern
        severityQuestions.add(addAnnsersQuestion(annsers, sleepDistSec, "Night sleep pattern", "3", null, factory));

        //Daytime sleepiness/difficulty waking
        severityQuestions.add(addAnnsersQuestion(annsers, sleepDistSec, "Daytime sleepiness/difficulty waking", "4", null, factory));

        //---------------------------------------------------------------------------
        // Subjective experience section
        //---------------------------------------------------------------------------
        Section subjExpSec = factory.createSection("Subjective experience", "Subjective experience");
        annsers.addSection(subjExpSec);
        subjExpSec.setDisplayText("Subjective experience");
        SectionOccurrence subjExpSecOcc = factory.createSectionOccurrence("Default");
        subjExpSec.addOccurrence(subjExpSecOcc);

        //Loss of energy/drive
        severityQuestions.add(addAnnsersQuestion(annsers, subjExpSec, "Loss of energy/drive", "5", null, factory));

        //Problems with memory
        severityQuestions.add(addAnnsersQuestion(annsers, subjExpSec, "Problems with memory", "6", null, factory));

        //MMSE score
        NumericEntry mmseScore = factory.createNumericEntry(
                "MMSE score",
                "Record MMSE score if known",
                EntryStatus.OPTIONAL);
        annsers.addEntry(mmseScore);
        mmseScore.setSection(subjExpSec);
        mmseScore.addValidationRule(zeroToThirty);

        //Problems with concentration
        severityQuestions.add(addAnnsersQuestion(annsers, subjExpSec, "Problems with concentration", "7", null, factory));

        //Dysphoria
        severityQuestions.add(addAnnsersQuestion(annsers, subjExpSec, "Dysphoria", "8", null, factory));

        //Emotional numbing
        severityQuestions.add(addAnnsersQuestion(annsers, subjExpSec, "Emotional numbing", "9", null, factory));

        //---------------------------------------------------------------------------
        // Cardiovascular problems section
        //---------------------------------------------------------------------------
        Section cardioProbsSec = factory.createSection("Cardiovascular problems", "Cardiovascular problems");
        annsers.addSection(cardioProbsSec);
        cardioProbsSec.setDisplayText("Cardiovascular problems");
        SectionOccurrence cardioProbsSecOcc = factory.createSectionOccurrence("Default");
        cardioProbsSec.addOccurrence(cardioProbsSecOcc);

        //Tachycardia
        severityQuestions.add(addAnnsersQuestion(annsers, cardioProbsSec, "Tachycardia", "10", null, factory));

        //Postural hypotension
        severityQuestions.add(addAnnsersQuestion(annsers, cardioProbsSec, "Postural hypotension", "11", null, factory));

        //Hypertension
        severityQuestions.add(addAnnsersQuestion(annsers, cardioProbsSec, "Hypertension", "12", null, factory));

        //ECG abnormality
        severityQuestions.add(addAnnsersQuestion(annsers, cardioProbsSec, "ECG abnormality", "13", null, factory));

        //QTc prolongation
        NumericEntry qtcpScore = factory.createNumericEntry(
                "QTc prolongation",
                "Record QTc prolongation if known",
                EntryStatus.OPTIONAL);
        annsers.addEntry(qtcpScore);
        qtcpScore.setSection(cardioProbsSec);
        qtcpScore.addUnit(UnitWrapper.instance().getUnit("sec"));

        //Peripheral oedema
        severityQuestions.add(addAnnsersQuestion(annsers, cardioProbsSec, "Peripheral oedema", "14", null, factory));

        //Breathlessness
        severityQuestions.add(addAnnsersQuestion(annsers, cardioProbsSec, "Breathlessness", "15", null, factory));

        //---------------------------------------------------------------------------
        // Gastrointestinal problems section
        //---------------------------------------------------------------------------
        Section gastroProbsSec = factory.createSection("Gastrointestinal problems", "Gastrointestinal problems");
        annsers.addSection(gastroProbsSec);
        gastroProbsSec.setDisplayText("Gastrointestinal problems");
        SectionOccurrence gastroProbsSecOcc = factory.createSectionOccurrence("Default");
        gastroProbsSec.addOccurrence(gastroProbsSecOcc);

        //Nausea/vomiting
        severityQuestions.add(addAnnsersQuestion(annsers, gastroProbsSec, "Nausea/vomiting", "16", null, factory));

        //Constipation
        severityQuestions.add(addAnnsersQuestion(annsers, gastroProbsSec, "Constipation", "17", null, factory));

        //Diarrhoea
        severityQuestions.add(addAnnsersQuestion(annsers, gastroProbsSec, "Diarrhoea", "18", null, factory));

        //---------------------------------------------------------------------------
        // Endocrine/metabolic problems section
        //---------------------------------------------------------------------------
        Section endoProbsSec = factory.createSection("Endocrine/metabolic problems", "Endocrine/metabolic problems");
        annsers.addSection(endoProbsSec);
        endoProbsSec.setDisplayText("Endocrine/metabolic problems");
        SectionOccurrence endoProbsSecOcc = factory.createSectionOccurrence("Default");
        endoProbsSec.addOccurrence(endoProbsSecOcc);

        //Weight gain
        severityQuestions.add(addAnnsersQuestion(annsers, endoProbsSec, "Weight gain", "19", null, factory));

        //Weight
        NumericEntry weight = factory.createNumericEntry(
                "Weight", "Weight", EntryStatus.OPTIONAL);
        annsers.addEntry(weight);
        weight.setSection(endoProbsSec);
        weight.addUnit(UnitWrapper.instance().getUnit("kg"));

        //Gynaecomastia
        severityQuestions.add(addAnnsersQuestion(annsers, endoProbsSec, "Gynaecomastia", "20", null, factory));

        //Galactorrhoea
        severityQuestions.add(addAnnsersQuestion(annsers, endoProbsSec, "Galactorrhoea", "21", null, factory));

        //Prolactin elevation
        severityQuestions.add(addAnnsersQuestion(annsers, endoProbsSec, "Prolactin elevation", "22", null, factory));

        //Onset/worsening of diabetes
        severityQuestions.add(addAnnsersQuestion(annsers, endoProbsSec, "Onset/worsening of diabetes", "23", null, factory));

        //Dyslipidaemia
        severityQuestions.add(addAnnsersQuestion(annsers, endoProbsSec, "Dyslipidaemia", "24", null, factory));

        //Cholesterol etc
        LongTextEntry cholesterol = factory.createLongTextEntry(
                "Cholesterol etc",
                "Record values for total cholesterol, low density " +
                        "lipoproteins, high density lipoproteins, triglycerides, " +
                        "etc. if known:",
                EntryStatus.OPTIONAL);
        annsers.addEntry(cholesterol);
        cholesterol.setSection(endoProbsSec);


        //---------------------------------------------------------------------------
        // Autonomic problems section
        //---------------------------------------------------------------------------
        Section autonomProbsSec = factory.createSection("Autonomic problems", "Autonomic problems");
        annsers.addSection(autonomProbsSec);
        autonomProbsSec.setDisplayText("Autonomic problems");
        SectionOccurrence autonomProbsSecOcc = factory.createSectionOccurrence("Default");
        autonomProbsSec.addOccurrence(autonomProbsSecOcc);

        //Blurred vision
        severityQuestions.add(addAnnsersQuestion(annsers, autonomProbsSec, "Blurred vision", "25", null, factory));

        //Dry mouth
        severityQuestions.add(addAnnsersQuestion(annsers, autonomProbsSec, "Dry mouth", "26", null, factory));

        //Hypersalivation
        severityQuestions.add(addAnnsersQuestion(annsers, autonomProbsSec, "Hypersalivation", "27", null, factory));

        //Sweating
        severityQuestions.add(addAnnsersQuestion(annsers, autonomProbsSec, "Sweating", "28", null, factory));

        //Fever
        severityQuestions.add(addAnnsersQuestion(annsers, autonomProbsSec, "Fever", "29", null, factory));

        //---------------------------------------------------------------------------
        // Genitourinary problems section
        //---------------------------------------------------------------------------
        Section genitoProbsSec = factory.createSection("Genitourinary problems", "Genitourinary problems");
        annsers.addSection(genitoProbsSec);
        genitoProbsSec.setDisplayText("Genitourinary problems");
        SectionOccurrence genitoProbsSecOcc = factory.createSectionOccurrence("Default");
        genitoProbsSec.addOccurrence(genitoProbsSecOcc);

        //Nocturnal enuresis
        severityQuestions.add(addAnnsersQuestion(annsers, genitoProbsSec, "Nocturnal enuresis", "30", null, factory));

        //Difficulty passing urine
        severityQuestions.add(addAnnsersQuestion(annsers, genitoProbsSec, "Difficulty passing urine", "31", null, factory));

        //---------------------------------------------------------------------------
        // Sexual side effects section
        //---------------------------------------------------------------------------
        Section sexualEffectsSec = factory.createSection("Sexual side effects", "Sexual side effects");
        annsers.addSection(sexualEffectsSec);
        sexualEffectsSec.setDisplayText("Sexual side effects");
        SectionOccurrence sexualEffectsSecOcc = factory.createSectionOccurrence("Default");
        sexualEffectsSec.addOccurrence(sexualEffectsSecOcc);

        NarrativeEntry sexEffInstr = factory.createNarrativeEntry(
                "Instructions",
                "Questions marked 'F' are to be asked of female patients only; " +
                        "Questions marked 'M' are to be asked of male patients only.");
        annsers.addEntry(sexEffInstr);
        sexEffInstr.setSection(sexualEffectsSec);

        String femaleOnly = "To be asked of female patients only";
        String maleOnly = "To be asked of male patients only";

        //Loss of libido
        severityQuestions.add(addAnnsersQuestion(annsers, sexualEffectsSec, "Loss of libido", "32", null, factory));

        //Problems of sexual arousal
        OptionEntry posa = addAnnsersQuestion(annsers, sexualEffectsSec, "Problems of sexual arousal", "33 F", femaleOnly, factory);
        posa.addOption(factory.createOption("Male patient - not applicable",-1));
        severityQuestions.add(posa);

        //Erectile difficulties
        OptionEntry ed = addAnnsersQuestion(annsers, sexualEffectsSec, "Erectile difficulties", "33 M", maleOnly, factory);
        ed.addOption(factory.createOption("Female patient - not applicable",-1));
        severityQuestions.add(ed);

        //Orgasmic difficulties
        OptionEntry od = addAnnsersQuestion(annsers, sexualEffectsSec, "Orgasmic difficulties", "34 F", femaleOnly, factory);
        od.addOption(factory.createOption("Male patient - not applicable",-1));
        severityQuestions.add(od);

        //Delayed ejaculation
        OptionEntry de = addAnnsersQuestion(annsers, sexualEffectsSec, "Delayed ejaculation", "34 M", maleOnly, factory);
        de.addOption(factory.createOption("Female patient - not applicable",-1));
        severityQuestions.add(de);

        //Change in menstruation
        OptionEntry cim = addAnnsersQuestion(annsers, sexualEffectsSec, "Change in menstruation", "35 F", femaleOnly, factory);
        cim.addOption(factory.createOption("Male patient - not applicable",-1));
        severityQuestions.add(cim);

        //Reduction in ejaculation volume/intensity
        OptionEntry riev = addAnnsersQuestion(annsers, sexualEffectsSec, "Reduction in ejaculation volume/intensity", "35 M", maleOnly, factory);
        riev.addOption(factory.createOption("Female patient - not applicable",-1));
        severityQuestions.add(riev);

        //---------------------------------------------------------------------------
        // CNS problems section
        //---------------------------------------------------------------------------
        Section cnsProbsSec = factory.createSection("CNS problems", "CNS problems");
        annsers.addSection(cnsProbsSec);
        cnsProbsSec.setDisplayText("CNS problems");
        SectionOccurrence cnsProbsSecOcc = factory.createSectionOccurrence("Default");
        cnsProbsSec.addOccurrence(cnsProbsSecOcc);

        //Confusion
        severityQuestions.add(addAnnsersQuestion(annsers, cnsProbsSec, "Confusion", "36", null, factory));

        //Fits
        OptionEntry fits = addAnnsersQuestion(annsers, cnsProbsSec, "Fits", "37", null, factory);
        severityQuestions.add(fits);

        //Fits - please specify...
        TextEntry fitsSpecify = factory.createTextEntry(
                "Fits - Specify",
                "Please specify and give details",
                EntryStatus.DISABLED);
        annsers.addEntry(fitsSpecify);
        fitsSpecify.setSection(cnsProbsSec);
        createOptionDependent(factory, fits.getOption(1), fitsSpecify);
        createOptionDependent(factory, fits.getOption(2), fitsSpecify);
        createOptionDependent(factory, fits.getOption(3), fitsSpecify);

        //Neuroleptic malignant syndrome
        severityQuestions.add(addAnnsersQuestion(annsers, cnsProbsSec, "Neuroleptic malignant syndrome", "38", null, factory));

        //---------------------------------------------------------------------------
        // Miscellaneous side effects section
        //---------------------------------------------------------------------------
        Section miscSec = factory.createSection("Miscellaneous side effects", "Miscellaneous side effects");
        annsers.addSection(miscSec);
        miscSec.setDisplayText("Miscellaneous side effects");
        SectionOccurrence miscSecOcc = factory.createSectionOccurrence("Default");
        miscSec.addOccurrence(miscSecOcc);

        //Hepatic dysfunction
        severityQuestions.add(addAnnsersQuestion(annsers, miscSec, "Hepatic dysfunction", "39", null, factory));

        //Skin rash
        severityQuestions.add(addAnnsersQuestion(annsers, miscSec, "Skin rash", "40", null, factory));

        //Blood dyscrasia
        OptionEntry bloodDyscrasia = addAnnsersQuestion(annsers, miscSec, "Blood dyscrasia", "41", null, factory);
        severityQuestions.add(bloodDyscrasia);

        //Blood dyscrasia - please specify...
        LongTextEntry bloodDyscSpecify = factory.createLongTextEntry(
                "Blood dyscrasia - Specify",
                "Please specify and give details",
                EntryStatus.DISABLED);
        annsers.addEntry(bloodDyscSpecify);
        bloodDyscSpecify.setSection(miscSec);
        createOptionDependent(factory, bloodDyscrasia.getOption(1), bloodDyscSpecify);
        createOptionDependent(factory, bloodDyscrasia.getOption(2), bloodDyscSpecify);
        createOptionDependent(factory, bloodDyscrasia.getOption(3), bloodDyscSpecify);

        //Facial oedema
        severityQuestions.add(addAnnsersQuestion(annsers, miscSec, "Facial oedema", "42", null, factory));

        //Other side-effect
        OptionEntry otherSideEffect = addAnnsersQuestion(annsers, miscSec, "Other side-effect", "43", null, factory);
        severityQuestions.add(otherSideEffect);

        //Other side-effect - please specify...
        LongTextEntry otherSideEffectSpecify = factory.createLongTextEntry(
                "Other side-effect - Specify",
                "Please specify and give details",
                EntryStatus.DISABLED);
        annsers.addEntry(otherSideEffectSpecify);
        otherSideEffectSpecify.setSection(miscSec);
        createOptionDependent(factory, otherSideEffect.getOption(1), otherSideEffectSpecify);
        createOptionDependent(factory, otherSideEffect.getOption(2), otherSideEffectSpecify);
        createOptionDependent(factory, otherSideEffect.getOption(3), otherSideEffectSpecify);

        //---------------------------------------------------------------------------
        // Optional additional question section
        //---------------------------------------------------------------------------
        Section optQuestSec = factory.createSection("Optional additional question", "Optional additional question");
        annsers.addSection(optQuestSec);
        optQuestSec.setDisplayText("Optional additional question");
        SectionOccurrence optQuestSecOcc = factory.createSectionOccurrence("Default");
        optQuestSec.addOccurrence(optQuestSecOcc);

        //Alteration in pharmacotherapy secondary to adverse effects
        OptionEntry altPharma = addAnnsersQuestion(annsers, optQuestSec,
                "Alteration in pharmacotherapy secondary to adverse effects",
                "44", null, factory);
        severityQuestions.add(altPharma);

        //Alteration in pharmacotherapy - please specify...
        LongTextEntry altPharmaSpecify = factory.createLongTextEntry(
                "Alteration in pharmacotherapy - Specify",
                "Please specify and give details",
                EntryStatus.DISABLED);
        annsers.addEntry(altPharmaSpecify);
        altPharmaSpecify.setSection(optQuestSec);
        createOptionDependent(factory, altPharma.getOption(1), altPharmaSpecify);
        createOptionDependent(factory, altPharma.getOption(2), altPharmaSpecify);
        createOptionDependent(factory, altPharma.getOption(3), altPharmaSpecify);

        //---------------------------------------------------------------------------
        // ANNSERS scoring section
        //---------------------------------------------------------------------------
        Section scoringSec = factory.createSection("ANNSERS scoring", "ANNSERS scoring");
        annsers.addSection(scoringSec);
        scoringSec.setDisplayText("ANNSERS scoring");
        SectionOccurrence scoringSecOcc = factory.createSectionOccurrence("Default");
        scoringSec.addOccurrence(scoringSecOcc);

        //Total number of side effects rated as present
        DerivedEntry totalPresent = factory.createDerivedEntry("Total side effects present", "Total number of side effects rated as present");
        annsers.addEntry(totalPresent);
        totalPresent.setSection(scoringSec);
        //Add the variables and create the formula. The variables consist of
        //all of the "severity" option entries, which will be named a0, a1,
        //a2, etc. The formula will be of the form
        //  if(a0,1,0)+if(a1,1,0)+if(a2,1,0)+...
        //The if(x,y,z) JEP function returns y if x>0, z if x<=0
        {
            int counter = 0;
            StringBuilder expression = new StringBuilder();
            for ( OptionEntry oe: severityQuestions ){
                String varName = "a"+counter;
                totalPresent.addVariable(varName, oe);
                if (counter>0){
                    expression.append("+");
                }
                expression.append("if(").append(varName).append(",1,0)");
                counter++;
            }
            totalPresent.setFormula(expression.toString());
        }

        //Mild side-effects sub-total
        DerivedEntry mildPresent = factory.createDerivedEntry("Number of mild side effects", "Number of mild side effects");
        annsers.addEntry(mildPresent);
        mildPresent.setSection(scoringSec);
        //Add the variables and create the formula. The variables consist of
        //all of the "severity" option entries, which will be named a0, a1,
        //a2, etc. The formula will be of the form
        //  if(a0==1,1,0)+if(a1==1,1,0)+if(a2==1,1,0)+...
        //The if(x,y,z) JEP function returns y if x>0, z if x<=0
        {
            int counter = 0;
            StringBuilder expression = new StringBuilder();
            for ( OptionEntry oe: severityQuestions ){
                String varName = "a"+counter;
                mildPresent.addVariable(varName, oe);
                if (counter>0){
                    expression.append("+");
                }
                expression.append("if(").append(varName).append("==1,1,0)");
                counter++;
            }
            mildPresent.setFormula(expression.toString());
        }

        //Moderate side-effects sub-total
        DerivedEntry moderatePresent = factory.createDerivedEntry("Number of moderate side effects", "Number of moderate side effects");
        annsers.addEntry(moderatePresent);
        moderatePresent.setSection(scoringSec);
        //Add the variables and create the formula. The variables consist of
        //all of the "severity" option entries, which will be named a0, a1,
        //a2, etc. The formula will be of the form
        //  if(a0==2,1,0)+if(a1==2,1,0)+if(a2==2,1,0)+...
        //The if(x,y,z) JEP function returns y if x>0, z if x<=0
        {
            int counter = 0;
            StringBuilder expression = new StringBuilder();
            for ( OptionEntry oe: severityQuestions ){
                String varName = "a"+counter;
                moderatePresent.addVariable(varName, oe);
                if (counter>0){
                    expression.append("+");
                }
                expression.append("if(").append(varName).append("==2,1,0)");
                counter++;
            }
            moderatePresent.setFormula(expression.toString());
        }

        //Mild side-effects sub-total
        DerivedEntry severePresent = factory.createDerivedEntry("Number of severe side effects", "Number of severe side effects");
        annsers.addEntry(severePresent);
        severePresent.setSection(scoringSec);
        //Add the variables and create the formula. The variables consist of
        //all of the "severity" option entries, which will be named a0, a1,
        //a2, etc. The formula will be of the form
        //  if(a0==3,1,0)+if(a1==3,1,0)+if(a2==3,1,0)+...
        //The if(x,y,z) JEP function returns y if x>0, z if x<=0
        {
            int counter = 0;
            StringBuilder expression = new StringBuilder();
            for ( OptionEntry oe: severityQuestions ){
                String varName = "a"+counter;
                severePresent.addVariable(varName, oe);
                if (counter>0){
                    expression.append("+");
                }
                expression.append("if(").append(varName).append("==3,1,0)");
                counter++;
            }
            severePresent.setFormula(expression.toString());
        }

        //Total score for scale
        DerivedEntry totalScore = factory.createDerivedEntry("Total score", "Total score for scale");
        annsers.addEntry(totalScore);
        totalScore.setSection(scoringSec);
        //Add the variables and create the formula. The variables consist of
        //all of the "severity" option entries, which will be named a0, a1,
        //a2, etc. The formula will be of the form
        //  a0+a1+a2+...
        {
            int counter = 0;
            StringBuilder expression = new StringBuilder();
            for ( OptionEntry oe: severityQuestions ){
                String varName = "a"+counter;
                totalScore.addVariable(varName, oe);
                if (counter>0){
                    expression.append("+");
                }
                expression.append("if(").append(varName).append(",").append(varName).append(",0)");
                counter++;
            }
            totalScore.setFormula(expression.toString());
        }

        return annsers;
    }

    /**
     * Create all of the objects necessary for a single question/"row" in
     * the ANNSERS assessment.
     *
     * @param doc The document containing the question.
     * @param sec The section of the document containing the question.
     * @param name The name of the question e.g. Headache.
     * @param label The label of the question e.g. 1
     * @param description The description of the question.
     * @param factory The object factory.
     * @return The option entry for the severity question, so that
     * it may be referenced by derived entries.
     */
    private static OptionEntry addAnnsersQuestion(Document doc, Section sec, String name, String label, String description, Factory factory){
        NarrativeEntry headache = factory.createNarrativeEntry(name, name);
        headache.setLabel(label);
        headache.setDescription(description);
        doc.addEntry(headache);
        headache.setSection(sec);

        BooleanEntry questionPatient = factory.createBooleanEntry(name + " - ReportPatient", "ReportPatient");
        doc.addEntry(questionPatient);
        questionPatient.setSection(sec);

        BooleanEntry questionCasenotes = factory.createBooleanEntry(name + " - Casenotes", "Casenotes, Investigation or Examination");
        doc.addEntry(questionCasenotes);
        questionCasenotes.setSection(sec);

        OptionEntry questionSeverity = factory.createOptionEntry(name + " - Severity", "Severity");
        doc.addEntry(questionSeverity);
        questionSeverity.setSection(sec);
        Option absent = factory.createOption("Absent", 0);
        Option mild = factory.createOption("Mild", 1);
        Option moderate = factory.createOption("Moderate", 2);
        Option severe = factory.createOption("Severe", 3);
        questionSeverity.addOption(absent);
        questionSeverity.addOption(mild);
        questionSeverity.addOption(moderate);
        questionSeverity.addOption(severe);
        questionSeverity.setDefaultValue(absent);

        return questionSeverity;
    }

}

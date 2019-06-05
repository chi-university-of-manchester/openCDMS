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


package org.psygrid.drn.address;

import org.psygrid.common.TransformersWrapper;
import org.psygrid.common.UnitWrapper;
import org.psygrid.common.ValidationRulesWrapper;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.AssessmentForm;

/**
 * @author Rob Harper
 *
 */
public class Biochemistry extends AssessmentForm {

    public static Document createDocument(Factory factory) {

        Document biochemistry = factory.createDocument("Biochemistry and Urinalysis",
                "Biochemistry and Urinalysis");

        createDocumentStatuses(factory, biochemistry);

        // antibody section
        Section antibodySection = factory.createSection("Antibody Status section");
        biochemistry.addSection(antibodySection);
        antibodySection.setDisplayText("Antibody Status");
        SectionOccurrence antibodySectionOcc = factory.createSectionOccurrence("Antibody Status Section Occurrence");
        antibodySection.addOccurrence(antibodySectionOcc);

        OptionEntry antibodiesTested = factory.createOptionEntry("Antibodies tested", "Antibodies tested");
        biochemistry.addEntry(antibodiesTested);
        antibodiesTested.setSection(antibodySection);
        createOptions(factory, antibodiesTested, new String[]{"Yes", "No"}, new int[]{1,0});
        Option abTestedYes = antibodiesTested.getOption(0);

        DateEntry dateAbTest = factory.createDateEntry("Date antibody test", "Date of antibody test (if tested)", EntryStatus.DISABLED);
        biochemistry.addEntry(dateAbTest);
        dateAbTest.setSection(antibodySection);
        dateAbTest.addValidationRule(ValidationRulesWrapper.instance().getRule("1990 or after"));
        dateAbTest.addValidationRule(ValidationRulesWrapper.instance().getRule("Not in future"));
        createOptionDependent(factory, abTestedYes, dateAbTest);

        OptionEntry gadTest = factory.createOptionEntry("GAD Test", "Glutamic Acid Decarboxylase (GAD)", EntryStatus.DISABLED);
        biochemistry.addEntry(gadTest);
        gadTest.setSection(antibodySection);
        createOptions(factory, gadTest, new String[]{"Yes", "No"}, new int[]{1,0});
        createOptionDependent(factory, abTestedYes, gadTest);

        OptionEntry icaTest = factory.createOptionEntry("ICA Test", "Islet Cell (ICA)", EntryStatus.DISABLED);
        biochemistry.addEntry(icaTest);
        icaTest.setSection(antibodySection);
        createOptions(factory, icaTest, new String[]{"Yes", "No"}, new int[]{1,0});
        createOptionDependent(factory, abTestedYes, icaTest);

        OptionEntry iaaTest = factory.createOptionEntry("IAA Test", "Insulin (IAA)", EntryStatus.DISABLED);
        biochemistry.addEntry(iaaTest);
        iaaTest.setSection(antibodySection);
        createOptions(factory, iaaTest, new String[]{"Yes", "No"}, new int[]{1,0});
        createOptionDependent(factory, abTestedYes, iaaTest);

        OptionEntry icaiaTest = factory.createOptionEntry("ICA512 / IA-2 Test", "ICA512 / IA-2", EntryStatus.DISABLED);
        biochemistry.addEntry(icaiaTest);
        icaiaTest.setSection(antibodySection);
        createOptions(factory, icaiaTest, new String[]{"Yes", "No"}, new int[]{1,0});
        createOptionDependent(factory, abTestedYes, icaiaTest);

        OptionEntry otherAntiTest = factory.createOptionEntry("Other Antibody Test", "Other antibody (please specify)", EntryStatus.DISABLED);
        biochemistry.addEntry(otherAntiTest);
        otherAntiTest.setSection(antibodySection);
        createOptions(factory, otherAntiTest, new String[]{"Yes", "No"}, new int[]{1,0});
        createOptionDependent(factory, abTestedYes, otherAntiTest);
        Option otherAntiTestYes = otherAntiTest.getOption(0);

        TextEntry otherAntiTestSpecifics = factory.createTextEntry("Other Antibody Test - specifics", "Other Antibody Test - specifics", EntryStatus.DISABLED);
        biochemistry.addEntry(otherAntiTestSpecifics);
        otherAntiTestSpecifics.setSection(antibodySection);
        createOptionDependent(factory, otherAntiTestYes, otherAntiTestSpecifics);

        LongTextEntry abComments = factory.createLongTextEntry("Comments", "Comments on antibody status", EntryStatus.DISABLED);
        biochemistry.addEntry(abComments);
        abComments.setSection(antibodySection);
        createOptionDependent(factory, abTestedYes, abComments, EntryStatus.OPTIONAL);


        //Create Electrolytes and metabolites, Lipids, HbA1c, Liver function
        //Glucose and Thyroid function sections (these are shared with the
        //follow up form)
        createSharedSections(biochemistry, factory);

        Section albuminuriaProteinuria = factory.createSection("Albuminuria / proteinuria", "Albuminuria / Proteinuria");
        biochemistry.addSection(albuminuriaProteinuria);
        SectionOccurrence albuminuriaProteinuriaSecOcc = factory.createSectionOccurrence("Albuminuria/Proteinuria Sec Occ");
        albuminuriaProteinuria.addOccurrence(albuminuriaProteinuriaSecOcc);

        addAlbuminuriaProteinuriaEntries(factory, biochemistry, albuminuriaProteinuria);

        return biochemistry;
    }

    public static void addAlbuminuriaProteinuriaEntries(Factory factory, Document doc, Section sec){

    	OptionEntry uaorpTested = factory.createOptionEntry("Urinary albumin or protein tested", "Urinary albumin or protein tested");
    	doc.addEntry(uaorpTested);
    	uaorpTested.setSection(sec);
    	createOptions(factory, uaorpTested, new String[]{"Yes", "No"}, new int[]{1, 0});
    	Option uaorpTestedYes = uaorpTested.getOption(0);

    	OptionEntry gender = factory.createOptionEntry("Gender", "Gender", EntryStatus.DISABLED);
    	doc.addEntry(gender);
    	gender.setSection(sec);
    	createOptions(factory, gender, new String[]{"Male", "Female"}, new int[]{1, 2});
    	createOptionDependent(factory, uaorpTestedYes, gender);

    	OptionEntry dtForMicroA = factory.createOptionEntry("Dipstick test for microalbuminuria", "Dipstick test for microalbuminuria", EntryStatus.DISABLED);
    	doc.addEntry(dtForMicroA);
    	dtForMicroA.setSection(sec);
    	createOptions(factory, dtForMicroA, new String[]{"Yes", "No"}, new int[]{1, 0});
    	Option dtForMicroAYes = dtForMicroA.getOption(0);
    	createOptionDependent(factory, uaorpTestedYes, dtForMicroA);

    	DateEntry dateOfDtForMATest = factory.createDateEntry("Date of test (Dipstick test for microalbuminuria)", "Date of dipstick test for microalbuminuria", EntryStatus.DISABLED);
    	doc.addEntry(dateOfDtForMATest);
    	dateOfDtForMATest.setSection(sec);
    	dateOfDtForMATest.addValidationRule(ValidationRulesWrapper.instance().getRule("2000 or after"));
    	dateOfDtForMATest.addValidationRule(ValidationRulesWrapper.instance().getRule("Not in future"));
    	createOptionDependent(factory, dtForMicroAYes, dateOfDtForMATest);

    	OptionEntry resultOfDtForMATest = factory.createOptionEntry("Result of dipstick test for microalbuminuria", "Result of dipstick test for microalbuminuria", EntryStatus.DISABLED);
    	doc.addEntry(resultOfDtForMATest);
    	resultOfDtForMATest.setSection(sec);
    	createOptions(factory, resultOfDtForMATest, new String[]{"Negative", "Positive"}, new int[]{1,2});
    	createOptionDependent(factory, dtForMicroAYes, resultOfDtForMATest);

    	OptionEntry dtForProtU = factory.createOptionEntry("Dipstick test for proteinuria", "Dipstick test for proteinuria", EntryStatus.DISABLED);
    	doc.addEntry(dtForProtU);
    	dtForProtU.setSection(sec);
    	createOptions(factory, dtForProtU, new String[]{"Yes", "No"}, new int[]{1, 0});
    	Option dtForProtUYes = dtForProtU.getOption(0);
    	createOptionDependent(factory, uaorpTestedYes, dtForProtU);

    	DateEntry dateOfDtForPUTest = factory.createDateEntry("Date of test (Dipstick test for proteinuria)", "Date of dipstick test for proteinuria", EntryStatus.DISABLED);
    	doc.addEntry(dateOfDtForPUTest);
    	dateOfDtForPUTest.setSection(sec);
    	dateOfDtForPUTest.addValidationRule(ValidationRulesWrapper.instance().getRule("2000 or after"));
    	dateOfDtForPUTest.addValidationRule(ValidationRulesWrapper.instance().getRule("Not in future"));
    	createOptionDependent(factory, dtForProtUYes, dateOfDtForPUTest);

    	OptionEntry resultOfDtForPUTest = factory.createOptionEntry("Result of dipstick test for proteinuria", "Result of dipstick test for proteinuria", EntryStatus.DISABLED);
    	doc.addEntry(resultOfDtForPUTest);
    	resultOfDtForPUTest.setSection(sec);
    	createOptions(factory, resultOfDtForPUTest, new String[]{"Negative", "Positive"}, new int[]{1,2}, new String[]{null, "1+ or more"});
    	createOptionDependent(factory, dtForProtUYes, resultOfDtForPUTest);

    	NumericEntry resultOfDtForPUTestConc = factory.createNumericEntry("Result of dipstick test for proteinuria (concentration)", "Result of dipstick test for proteinuria (concentration)", EntryStatus.DISABLED);
    	doc.addEntry(resultOfDtForPUTestConc);
    	resultOfDtForPUTestConc.setSection(sec);
    	resultOfDtForPUTestConc.addUnit(UnitWrapper.instance().getUnit("mg/L"));
    	resultOfDtForPUTestConc.addValidationRule(ValidationRulesWrapper.instance().getRule("Proteinuria Concentration"));
    	createOptionDependent(factory, dtForProtUYes, resultOfDtForPUTestConc);

    	OptionEntry acRatioTested = factory.createOptionEntry("ACR tested", "Albumin:creatinine ratio tested", EntryStatus.DISABLED);
    	doc.addEntry(acRatioTested);
    	acRatioTested.setSection(sec);
    	createOptions(factory, acRatioTested, new String[]{"Yes", "No"}, new int[]{1, 0});
    	Option acRatioTestedYes = acRatioTested.getOption(0);
    	createOptionDependent(factory, uaorpTestedYes, acRatioTested);

    	DateEntry dateOfInitialAcTest = factory.createDateEntry("Date of initial test (ACR)", "Date of initial ACR test", EntryStatus.DISABLED);
    	doc.addEntry(dateOfInitialAcTest);
    	dateOfInitialAcTest.setSection(sec);
    	dateOfInitialAcTest.addValidationRule(ValidationRulesWrapper.instance().getRule("2000 or after"));
    	dateOfInitialAcTest.addValidationRule(ValidationRulesWrapper.instance().getRule("Not in future"));
    	createOptionDependent(factory, acRatioTestedYes, dateOfInitialAcTest);

    	NumericEntry acRatioInitial = factory.createNumericEntry("ACR (initial)", "Albumin:creatinine ratio (initial result)", EntryStatus.DISABLED);
    	doc.addEntry(acRatioInitial);
    	acRatioInitial.setSection(sec);
    	acRatioInitial.addValidationRule(ValidationRulesWrapper.instance().getRule("0.01 to 500"));
    	createOptionDependent(factory, acRatioTestedYes, acRatioInitial);

    	DateEntry dateOfFirstFUAcTest = factory.createDateEntry("Date of first follow-up test (ACR)", "Date of first follow-up ACR test", EntryStatus.DISABLED);
    	doc.addEntry(dateOfFirstFUAcTest);
    	dateOfFirstFUAcTest.setSection(sec);
    	dateOfFirstFUAcTest.addValidationRule(ValidationRulesWrapper.instance().getRule("2000 or after"));
    	dateOfFirstFUAcTest.addValidationRule(ValidationRulesWrapper.instance().getRule("Not in future"));
    	createOptionDependent(factory, acRatioTestedYes, dateOfFirstFUAcTest);

    	NumericEntry acRatioFirstFU = factory.createNumericEntry("ACR (first follow-up)", "Albumin:creatinine ratio (first follow-up result)", EntryStatus.DISABLED);
    	doc.addEntry(acRatioFirstFU);
    	acRatioFirstFU.setSection(sec);
    	acRatioFirstFU.addValidationRule(ValidationRulesWrapper.instance().getRule("0.01 to 500"));
    	createOptionDependent(factory, acRatioTestedYes, acRatioFirstFU);

    	DateEntry dateOfSecondFUAcTest = factory.createDateEntry("Date of second follow-up test (ACR)", "Date of second follow-up ACR test", EntryStatus.DISABLED);
    	doc.addEntry(dateOfSecondFUAcTest);
    	dateOfSecondFUAcTest.setSection(sec);
    	dateOfSecondFUAcTest.addValidationRule(ValidationRulesWrapper.instance().getRule("2000 or after"));
    	dateOfSecondFUAcTest.addValidationRule(ValidationRulesWrapper.instance().getRule("Not in future"));
    	createOptionDependent(factory, acRatioTestedYes, dateOfSecondFUAcTest);

    	NumericEntry acRatioSecondFU = factory.createNumericEntry("ACR (second follow-up)", "Albumin:creatinine ratio (second follow-up result)", EntryStatus.DISABLED);
    	doc.addEntry(acRatioSecondFU);
    	acRatioSecondFU.setSection(sec);
    	acRatioSecondFU.addValidationRule(ValidationRulesWrapper.instance().getRule("0.01 to 500"));
    	createOptionDependent(factory, acRatioTestedYes, acRatioSecondFU);

    	OptionEntry urineInfection = factory.createOptionEntry("Urinary Tract Infection", "Urinary Tract Infection", EntryStatus.DISABLED);
    	doc.addEntry(urineInfection);
    	urineInfection.setSection(sec);
    	createOptions(factory, urineInfection, new String[]{"Yes", "No"}, new int[]{1,0});
    	createOptionDependent(factory, acRatioTestedYes, urineInfection);

    	OptionEntry pcRatioTested = factory.createOptionEntry("PCR tested", "Protein:creatinine ratio tested", EntryStatus.DISABLED);
    	doc.addEntry(pcRatioTested);
    	pcRatioTested.setSection(sec);
    	createOptions(factory, pcRatioTested, new String[]{"Yes", "No"}, new int[]{1, 0});
    	Option pcRatioTestedYes = pcRatioTested.getOption(0);
    	createOptionDependent(factory, uaorpTestedYes, pcRatioTested);

    	DateEntry dateOfInitialPcTest = factory.createDateEntry("Date of initial test (PCR)", "Date of initial PCR test", EntryStatus.DISABLED);
    	doc.addEntry(dateOfInitialPcTest);
    	dateOfInitialPcTest.setSection(sec);
    	dateOfInitialPcTest.addValidationRule(ValidationRulesWrapper.instance().getRule("2000 or after"));
    	dateOfInitialPcTest.addValidationRule(ValidationRulesWrapper.instance().getRule("Not in future"));
    	createOptionDependent(factory, pcRatioTestedYes, dateOfInitialPcTest);

    	NumericEntry pcRatioInitial = factory.createNumericEntry("PCR (initial)", "Protein:creatinine ratio (initial result)", EntryStatus.DISABLED);
    	doc.addEntry(pcRatioInitial);
    	pcRatioInitial.setSection(sec);
    	pcRatioInitial.addValidationRule(ValidationRulesWrapper.instance().getRule("0.01 to 500"));
    	createOptionDependent(factory, pcRatioTestedYes, pcRatioInitial);

    	DateEntry dateOfFirstFUPcTest = factory.createDateEntry("Date of first follow-up test (PCR)", "Date of first follow-up PCR test", EntryStatus.DISABLED);
    	doc.addEntry(dateOfFirstFUPcTest);
    	dateOfFirstFUPcTest.setSection(sec);
    	dateOfFirstFUPcTest.addValidationRule(ValidationRulesWrapper.instance().getRule("2000 or after"));
    	dateOfFirstFUPcTest.addValidationRule(ValidationRulesWrapper.instance().getRule("Not in future"));
    	createOptionDependent(factory, pcRatioTestedYes, dateOfFirstFUPcTest);

    	NumericEntry pcRatioFirstFU = factory.createNumericEntry("PCR (first follow-up)", "Protein:creatinine ratio (first follow-up result)", EntryStatus.DISABLED);
    	doc.addEntry(pcRatioFirstFU);
    	pcRatioFirstFU.setSection(sec);
    	pcRatioFirstFU.addValidationRule(ValidationRulesWrapper.instance().getRule("0.01 to 500"));
    	createOptionDependent(factory, pcRatioTestedYes, pcRatioFirstFU);

    	DateEntry dateOfSecondFUPcTest = factory.createDateEntry("Date of second follow-up test (PCR)", "Date of second follow-up PCR test", EntryStatus.DISABLED);
    	doc.addEntry(dateOfSecondFUPcTest);
    	dateOfSecondFUPcTest.setSection(sec);
    	dateOfSecondFUPcTest.addValidationRule(ValidationRulesWrapper.instance().getRule("2000 or after"));
    	dateOfSecondFUPcTest.addValidationRule(ValidationRulesWrapper.instance().getRule("Not in future"));
    	createOptionDependent(factory, pcRatioTestedYes, dateOfSecondFUPcTest);

    	NumericEntry pcRatioSecondFU = factory.createNumericEntry("PCR (second follow-up)", "Protein:creatinine ratio (second follow-up result)", EntryStatus.DISABLED);
    	doc.addEntry(pcRatioSecondFU);
    	pcRatioSecondFU.setSection(sec);
    	pcRatioSecondFU.addValidationRule(ValidationRulesWrapper.instance().getRule("0.01 to 500"));
    	createOptionDependent(factory, pcRatioTestedYes, pcRatioSecondFU);

    	OptionEntry uapt24hr = factory.createOptionEntry("Urinary albumin/protein tested in 24 hour/overnight/timed collection", "Urinary albumin/protein tested in 24 hour/overnight/timed collection", EntryStatus.DISABLED);
    	doc.addEntry(uapt24hr);
    	uapt24hr.setSection(sec);
    	createOptions(factory, uapt24hr, new String[]{"Yes", "No"}, new int[]{1,0});
    	Option uapt24hrYes = uapt24hr.getOption(0);
    	createOptionDependent(factory, uaorpTestedYes, uapt24hr);

    	DateEntry dateOfUapt24hrTest = factory.createDateEntry("Date of urinary albumin/protein tested in 24 hour/overnight/timed collection)",
                "Date of urinary albumin/protein tested in 24 hour/overnight/timed collection", EntryStatus.DISABLED);
    	doc.addEntry(dateOfUapt24hrTest);
    	dateOfUapt24hrTest.setSection(sec);
    	dateOfUapt24hrTest.addValidationRule(ValidationRulesWrapper.instance().getRule("2000 or after"));
    	dateOfUapt24hrTest.addValidationRule(ValidationRulesWrapper.instance().getRule("Not in future"));
    	createOptionDependent(factory, uapt24hrYes, dateOfUapt24hrTest);

        OptionEntry timePeriodUrineColl = factory.createOptionEntry("Time period of urine collection", "Time period of urine collection", EntryStatus.DISABLED);
        doc.addEntry(timePeriodUrineColl);
        timePeriodUrineColl.setSection(sec);
        createOptions(factory,timePeriodUrineColl, new String[]{"24 Hours", "Overnight", "Other"}, new int[]{1,2,3});
        Option tpucOther = timePeriodUrineColl.getOption(2);
    	createOptionDependent(factory, uapt24hrYes, timePeriodUrineColl);

    	IntegerEntry timePeriodUrineCollMin = factory.createIntegerEntry("Time period of urine collection in minutes", "Time period of urine collection in minutes", EntryStatus.DISABLED);
    	doc.addEntry(timePeriodUrineCollMin);
    	timePeriodUrineCollMin.setSection(sec);
    	timePeriodUrineCollMin.addValidationRule(ValidationRulesWrapper.instance().getRule("1 to 3000"));
    	timePeriodUrineCollMin.addUnit(UnitWrapper.instance().getUnit("min"));
    	createOptionDependent(factory, tpucOther, timePeriodUrineCollMin);

    	NumericEntry resultUaTestMass = factory.createNumericEntry("Result of urinary albumin test (mass)", "Result of urinary albumin test (mass)", EntryStatus.DISABLED);
    	doc.addEntry(resultUaTestMass);
    	resultUaTestMass.setSection(sec);
    	resultUaTestMass.addValidationRule(ValidationRulesWrapper.instance().getRule("1 to 1000"));
    	resultUaTestMass.addUnit(UnitWrapper.instance().getUnit("mg"));
    	createOptionDependent(factory, uapt24hrYes, resultUaTestMass);

    	NumericEntry resultUaTestConc = factory.createNumericEntry("Result of urinary albumin test (concentration)", "Result of urinary albumin test (concentration)", EntryStatus.DISABLED);
    	doc.addEntry(resultUaTestConc);
    	resultUaTestConc.setSection(sec);
    	resultUaTestConc.addValidationRule(ValidationRulesWrapper.instance().getRule("0.01 to 100"));
    	resultUaTestConc.addUnit(UnitWrapper.instance().getUnit("mg/dL"));
    	createOptionDependent(factory, uapt24hrYes, resultUaTestConc);

    	NumericEntry resultUpTestMass = factory.createNumericEntry("Result of urinary protein test (mass)", "Result of urinary protein test (mass)", EntryStatus.DISABLED);
    	doc.addEntry(resultUpTestMass);
    	resultUpTestMass.setSection(sec);
    	resultUpTestMass.addValidationRule(ValidationRulesWrapper.instance().getRule("1 to 1000"));
    	resultUpTestMass.addUnit(UnitWrapper.instance().getUnit("mg"));
    	createOptionDependent(factory, uapt24hrYes, resultUpTestMass);

    	NumericEntry resultUpTestConc = factory.createNumericEntry("Result of urinary protein test (concentration)", "Result of urinary protein test (concentration)", EntryStatus.DISABLED);
    	doc.addEntry(resultUpTestConc);
    	resultUpTestConc.setSection(sec);
    	resultUpTestConc.addValidationRule(ValidationRulesWrapper.instance().getRule("0.01 to 100"));
    	resultUpTestConc.addUnit(UnitWrapper.instance().getUnit("mg/dL"));
    	createOptionDependent(factory, uapt24hrYes, resultUpTestConc);

    	OptionEntry stuapc = factory.createOptionEntry("Spot test of urinary albumin/protein concentration (one-off sample)", "Spot test of urinary albumin/protein concentration (one-off sample)", EntryStatus.DISABLED);
    	doc.addEntry(stuapc);
    	stuapc.setSection(sec);
    	createOptions(factory, stuapc, new String[]{"Yes", "No"}, new int[]{1,0});
    	Option stuapcYes = stuapc.getOption(0);
    	createOptionDependent(factory, uaorpTestedYes, stuapc);

    	DateEntry dateOfStuapc = factory.createDateEntry("Date of spot test of urinary albumin/protein concentration (one-off sample))",
                "Date of spot test of urinary albumin/protein concentration (one-off sample))", EntryStatus.DISABLED);
    	doc.addEntry(dateOfStuapc);
    	dateOfStuapc.setSection(sec);
    	dateOfStuapc.addValidationRule(ValidationRulesWrapper.instance().getRule("2000 or after"));
    	dateOfStuapc.addValidationRule(ValidationRulesWrapper.instance().getRule("Not in future"));
    	createOptionDependent(factory, stuapcYes, dateOfStuapc);

    	NumericEntry resultStuac = factory.createNumericEntry("Result of spot test of urinary albumin concentration", "Result of spot test of urinary albumin concentration", EntryStatus.DISABLED);
    	doc.addEntry(resultStuac);
    	resultStuac.setSection(sec);
    	resultStuac.addValidationRule(ValidationRulesWrapper.instance().getRule("0.01 to 100"));
    	resultStuac.addUnit(UnitWrapper.instance().getUnit("mg/dL"));
    	createOptionDependent(factory, stuapcYes, resultStuac);

    	NumericEntry resultStupc = factory.createNumericEntry("Result of spot test of urinary protein concentration", "Result of spot test of urinary protein concentration", EntryStatus.DISABLED);
    	doc.addEntry(resultStupc);
    	resultStupc.setSection(sec);
    	resultStupc.addValidationRule(ValidationRulesWrapper.instance().getRule("0.01 to 100"));
    	resultStupc.addUnit(UnitWrapper.instance().getUnit("mg/dL"));
    	createOptionDependent(factory, stuapcYes, resultStupc);

		//Add the external derived entry to fetch the Opcrit calculation
		ExternalDerivedEntry exDE = factory.createExternalDerivedEntry("Nephropathy status", "Nephropathy status");
		doc.addEntry(exDE);
		exDE.setSection(sec);
		exDE.setExternalTransformer(TransformersWrapper.instance().getTransformer("drnNephropathy"));
		exDE.setTransformWithStdCodes(true);

		exDE.addVariable("0", uaorpTested);
		exDE.addVariable("1", gender);
		exDE.addVariable("2", dtForMicroA);
		exDE.addVariable("3", resultOfDtForMATest);
		exDE.addVariable("4", dtForProtU);
		exDE.addVariable("5", resultOfDtForPUTest);
		exDE.addVariable("6", acRatioTested);
		exDE.addVariable("7", acRatioInitial);
		exDE.addVariable("8", acRatioFirstFU);
		exDE.addVariable("9", acRatioSecondFU);
		exDE.addVariable("10", urineInfection);
		exDE.addVariable("11", pcRatioTested);
		exDE.addVariable("12", pcRatioInitial);
		exDE.addVariable("13", pcRatioFirstFU);
		exDE.addVariable("14", pcRatioSecondFU);
		exDE.addVariable("15", uapt24hr);
		exDE.addVariable("16", timePeriodUrineColl);
		exDE.addVariable("17", timePeriodUrineCollMin);
		exDE.addVariable("18", resultUaTestMass);
		exDE.addVariable("19", resultUaTestConc);
		exDE.addVariable("20", resultUpTestMass);
		exDE.addVariable("21", resultUpTestConc);

    }

    /**
     * Create the Electrolytes and metabolites, Lipids, HbA1c, Liver function,
     * Glucose and Thyroid function sections for the Biochemistry and Biochemistry
     * Follow Up forms.
     *
     * @param biochemistry The document to add the sections to.
     * @param factory Factory.
     * @param rules Validation rules.
     */
    public static void createSharedSections(Document biochemistry, Factory factory){

        //Electrolytes and metabolites section
        Section emSection = factory.createSection("Electrolytes and metabolites section", "Electrolytes and metabolites");
        biochemistry.addSection(emSection);
        SectionOccurrence emSectionOcc = factory.createSectionOccurrence("Electrolytes and metabolites section occurrence");
        emSection.addOccurrence(emSectionOcc);

        OptionEntry emTested = factory.createOptionEntry("Electrolytes and metabolites tested", "Electrolytes and metabolites tested");
        biochemistry.addEntry(emTested);
        emTested.setSection(emSection);
        createOptions(factory, emTested, new String[]{"Yes", "No"}, new int[]{1,0});
        Option emTestedYes = emTested.getOption(0);

        DateEntry dateEmTest = factory.createDateEntry("Date of electrolytes and metabolites test", "Date of electrolytes and metabolites test", EntryStatus.DISABLED);
        biochemistry.addEntry(dateEmTest);
        dateEmTest.setSection(emSection);
        dateEmTest.addValidationRule(ValidationRulesWrapper.instance().getRule("2000 or after"));
        dateEmTest.addValidationRule(ValidationRulesWrapper.instance().getRule("Not in future"));
        createOptionDependent(factory, emTestedYes, dateEmTest);

        NumericEntry creatinine = factory.createNumericEntry("Creatinine", "Creatinine", EntryStatus.DISABLED);
        biochemistry.addEntry(creatinine);
        creatinine.setSection(emSection);
        creatinine.addValidationRule(ValidationRulesWrapper.instance().getRule("1 to 1000"));
        creatinine.addUnit(UnitWrapper.instance().getUnit("umol/L"));
        createOptionDependent(factory, emTestedYes, creatinine);

        NumericEntry creatinine2 = factory.createNumericEntry("Creatinine #2", "Creatinine", EntryStatus.DISABLED);
        biochemistry.addEntry(creatinine2);
        creatinine2.setSection(emSection);
        creatinine2.addValidationRule(ValidationRulesWrapper.instance().getRule("0.01 to 10"));
        creatinine2.addUnit(UnitWrapper.instance().getUnit("mg/dL"));
        createOptionDependent(factory, emTestedYes, creatinine2);

        NumericEntry potassium = factory.createNumericEntry("Potassium", "Potassium", EntryStatus.DISABLED);
        biochemistry.addEntry(potassium);
        potassium.setSection(emSection);
        potassium.addValidationRule(ValidationRulesWrapper.instance().getRule("0.1 to 100"));
        potassium.addUnit(UnitWrapper.instance().getUnit("mmol/L"));
        createOptionDependent(factory, emTestedYes, potassium);

        NumericEntry sodium = factory.createNumericEntry("Sodium", "Sodium", EntryStatus.DISABLED);
        biochemistry.addEntry(sodium);
        sodium.setSection(emSection);
        sodium.addValidationRule(ValidationRulesWrapper.instance().getRule("10 to 500"));
        sodium.addUnit(UnitWrapper.instance().getUnit("mmol/L"));
        createOptionDependent(factory, emTestedYes, sodium);

        NumericEntry urea = factory.createNumericEntry("Urea", "Urea", EntryStatus.DISABLED);
        biochemistry.addEntry(urea);
        urea.setSection(emSection);
        urea.addValidationRule(ValidationRulesWrapper.instance().getRule("0.01 to 500"));
        urea.addUnit(UnitWrapper.instance().getUnit("mmol/L"));
        createOptionDependent(factory, emTestedYes, urea);

        NumericEntry urea2 = factory.createNumericEntry("Urea #2", "Urea", EntryStatus.DISABLED);
        biochemistry.addEntry(urea2);
        urea2.setSection(emSection);
        urea2.addValidationRule(ValidationRulesWrapper.instance().getRule("0.1 to 100"));
        urea2.addUnit(UnitWrapper.instance().getUnit("mg/dL"));
        createOptionDependent(factory, emTestedYes, urea2);

        //Lipids section
        Section lipidsSection = factory.createSection("Lipids section", "Lipids");
        biochemistry.addSection(lipidsSection);
        SectionOccurrence lipidsSectionOcc = factory.createSectionOccurrence("Lipids section occurrence");
        lipidsSection.addOccurrence(lipidsSectionOcc);

        OptionEntry lipidsTested = factory.createOptionEntry("Lipids tested", "Lipids tested");
        biochemistry.addEntry(lipidsTested);
        lipidsTested.setSection(lipidsSection);
        createOptions(factory, lipidsTested, new String[]{"Yes", "No"}, new int[]{1,0});
        Option lipidsTestedYes = lipidsTested.getOption(0);

        DateEntry dateLipidsTest = factory.createDateEntry("Date of lipids test", "Date of lipids test", EntryStatus.DISABLED);
        biochemistry.addEntry(dateLipidsTest);
        dateLipidsTest.setSection(lipidsSection);
        dateLipidsTest.addValidationRule(ValidationRulesWrapper.instance().getRule("2000 or after"));
        dateLipidsTest.addValidationRule(ValidationRulesWrapper.instance().getRule("Not in future"));
        createOptionDependent(factory, lipidsTestedYes, dateLipidsTest);

        OptionEntry fastingSampleLipids = factory.createOptionEntry("Fasting sample lipids", "Fasting sample for lipids test", EntryStatus.DISABLED);
        biochemistry.addEntry(fastingSampleLipids);
        fastingSampleLipids.setSection(lipidsSection);
        createOptions(factory, fastingSampleLipids, new String[]{"Yes", "No"}, new int[]{1,0}); //Use std code data for "Information not available"
        createOptionDependent(factory, lipidsTestedYes, fastingSampleLipids);

        NumericEntry cholesterol = factory.createNumericEntry("Cholesterol", "Cholesterol", EntryStatus.DISABLED);
        biochemistry.addEntry(cholesterol);
        cholesterol.setSection(lipidsSection);
        cholesterol.addValidationRule(ValidationRulesWrapper.instance().getRule("0.1 to 50"));
        cholesterol.addUnit(UnitWrapper.instance().getUnit("mmol/L"));
        createOptionDependent(factory, lipidsTestedYes, cholesterol);

        NumericEntry triglycerides = factory.createNumericEntry("Triglycerides", "Triglycerides", EntryStatus.DISABLED);
        biochemistry.addEntry(triglycerides);
        triglycerides.setSection(lipidsSection);
        triglycerides.addValidationRule(ValidationRulesWrapper.instance().getRule("0.01 to 50"));
        triglycerides.addUnit(UnitWrapper.instance().getUnit("mmol/L"));
        createOptionDependent(factory, lipidsTestedYes, triglycerides);

        NumericEntry hdl = factory.createNumericEntry("HDL", "High density lipoprotein (HDL)", EntryStatus.DISABLED);
        biochemistry.addEntry(hdl);
        hdl.setSection(lipidsSection);
        hdl.addValidationRule(ValidationRulesWrapper.instance().getRule("0.01 to 50"));
        hdl.addUnit(UnitWrapper.instance().getUnit("mmol/L"));
        createOptionDependent(factory, lipidsTestedYes, hdl);

        DerivedEntry cholOverHdl = factory.createDerivedEntry("Cholesterol/HDL", "Cholesterol/HDL", EntryStatus.DISABLED);
        biochemistry.addEntry(cholOverHdl);
        cholOverHdl.setSection(lipidsSection);
        cholOverHdl.setFormula("c/h");
        cholOverHdl.addVariable("c", cholesterol);
        cholOverHdl.addVariable("h", hdl);
        createOptionDependent(factory, lipidsTestedYes, cholOverHdl);

        NumericEntry ldl = factory.createNumericEntry("LDL", "Low density lipoprotein (LDL)", EntryStatus.DISABLED);
        biochemistry.addEntry(ldl);
        ldl.setSection(lipidsSection);
        ldl.addValidationRule(ValidationRulesWrapper.instance().getRule("0.1 to 50"));
        ldl.addUnit(UnitWrapper.instance().getUnit("mmol/L"));
        createOptionDependent(factory, lipidsTestedYes, ldl);


        //HbA1C section
        Section hbA1cSection = factory.createSection("HbA1C section", "HbA1C");
        biochemistry.addSection(hbA1cSection);
        SectionOccurrence hbA1cSectionOcc = factory.createSectionOccurrence("HbA1C section occurrence");
        hbA1cSection.addOccurrence(hbA1cSectionOcc);

        OptionEntry hbA1cTested = factory.createOptionEntry("HbA1C tested", "HbA1C tested");
        biochemistry.addEntry(hbA1cTested);
        hbA1cTested.setSection(hbA1cSection);
        createOptions(factory, hbA1cTested, new String[]{"Yes", "No"}, new int[]{1,0});
        Option hbA1cTestedYes = hbA1cTested.getOption(0);

        DateEntry dateHbA1cTest = factory.createDateEntry("Date of HbA1C test", "Date of HbA1C test", EntryStatus.DISABLED);
        biochemistry.addEntry(dateHbA1cTest);
        dateHbA1cTest.setSection(hbA1cSection);
        dateHbA1cTest.addValidationRule(ValidationRulesWrapper.instance().getRule("2000 or after"));
        dateHbA1cTest.addValidationRule(ValidationRulesWrapper.instance().getRule("Not in future"));
        createOptionDependent(factory, hbA1cTestedYes, dateHbA1cTest);

        NumericEntry hbA1c = factory.createNumericEntry("HbA1c", "HbA1c", EntryStatus.DISABLED);
        biochemistry.addEntry(hbA1c);
        hbA1c.setSection(hbA1cSection);
        hbA1c.addValidationRule(ValidationRulesWrapper.instance().getRule("0.1 to 50"));
        hbA1c.addUnit(UnitWrapper.instance().getUnit("%"));
        createOptionDependent(factory, hbA1cTestedYes, hbA1c);


        //Liver Function section
        Section liverFunctionSection = factory.createSection("Liver function section", "Liver function");
        biochemistry.addSection(liverFunctionSection);
        SectionOccurrence liverFunctionSectionOcc = factory.createSectionOccurrence("Liver function section occurrence");
        liverFunctionSection.addOccurrence(liverFunctionSectionOcc);

        OptionEntry liverFunctionTested = factory.createOptionEntry("Liver function tested", "Liver function tested");
        biochemistry.addEntry(liverFunctionTested);
        liverFunctionTested.setSection(liverFunctionSection);
        createOptions(factory, liverFunctionTested, new String[]{"Yes", "No"}, new int[]{1,0});
        Option liverFunctionTestedYes = liverFunctionTested.getOption(0);

        DateEntry dateLiverFunctionTest = factory.createDateEntry("Date of liver function test", "Date of liver function test", EntryStatus.DISABLED);
        biochemistry.addEntry(dateLiverFunctionTest);
        dateLiverFunctionTest.setSection(liverFunctionSection);
        dateLiverFunctionTest.addValidationRule(ValidationRulesWrapper.instance().getRule("2000 or after"));
        dateLiverFunctionTest.addValidationRule(ValidationRulesWrapper.instance().getRule("Not in future"));
        createOptionDependent(factory, liverFunctionTestedYes, dateLiverFunctionTest);

        NumericEntry alt = factory.createNumericEntry("ALT", "Alanine transaminase (ALT)", EntryStatus.DISABLED);
        biochemistry.addEntry(alt);
        alt.setSection(liverFunctionSection);
        alt.addValidationRule(ValidationRulesWrapper.instance().getRule("0.1 to 500"));
        alt.addUnit(UnitWrapper.instance().getUnit("U/L"));
        createOptionDependent(factory, liverFunctionTestedYes, alt);

        NumericEntry albumin = factory.createNumericEntry("Albumin", "Albumin", EntryStatus.DISABLED);
        biochemistry.addEntry(albumin);
        albumin.setSection(liverFunctionSection);
        albumin.addValidationRule(ValidationRulesWrapper.instance().getRule("1 to 500"));
        albumin.addUnit(UnitWrapper.instance().getUnit("g/L"));
        createOptionDependent(factory, liverFunctionTestedYes, albumin);

        NumericEntry alp = factory.createNumericEntry("ALP", "Alkaline phosphatase (ALP)", EntryStatus.DISABLED);
        biochemistry.addEntry(alp);
        alp.setSection(liverFunctionSection);
        alp.addValidationRule(ValidationRulesWrapper.instance().getRule("0.1 to 5000"));
        alp.addUnit(UnitWrapper.instance().getUnit("U/L"));
        createOptionDependent(factory, liverFunctionTestedYes, alp);

        NumericEntry bilirubin = factory.createNumericEntry("Bilirubin", "Bilirubin", EntryStatus.DISABLED);
        biochemistry.addEntry(bilirubin);
        bilirubin.setSection(liverFunctionSection);
        bilirubin.addValidationRule(ValidationRulesWrapper.instance().getRule("0.1 to 100"));
        bilirubin.addUnit(UnitWrapper.instance().getUnit("umol/L"));
        createOptionDependent(factory, liverFunctionTestedYes, bilirubin);

        NumericEntry totalProtein = factory.createNumericEntry("Total protein", "Total protein", EntryStatus.DISABLED);
        biochemistry.addEntry(totalProtein);
        totalProtein.setSection(liverFunctionSection);
        totalProtein.addValidationRule(ValidationRulesWrapper.instance().getRule("1 to 500"));
        totalProtein.addUnit(UnitWrapper.instance().getUnit("g/L"));
        createOptionDependent(factory, liverFunctionTestedYes, totalProtein);


        //Glucose section
        Section glucoseSection = factory.createSection("Glucose section", "Glucose");
        biochemistry.addSection(glucoseSection);
        SectionOccurrence glucoseSectionOcc = factory.createSectionOccurrence("Glucose section occurrence");
        glucoseSection.addOccurrence(glucoseSectionOcc);

        OptionEntry glucoseTested = factory.createOptionEntry("Glucose tested", "Glucose tested");
        biochemistry.addEntry(glucoseTested);
        glucoseTested.setSection(glucoseSection);
        createOptions(factory, glucoseTested, new String[]{"Yes", "No"});
        Option glucoseTestedYes = glucoseTested.getOption(0);

        DateEntry dateGlucoseTest = factory.createDateEntry("Date of glucose test", "Date of glucose test", EntryStatus.DISABLED);
        biochemistry.addEntry(dateGlucoseTest);
        dateGlucoseTest.setSection(glucoseSection);
        dateGlucoseTest.addValidationRule(ValidationRulesWrapper.instance().getRule("2000 or after"));
        dateGlucoseTest.addValidationRule(ValidationRulesWrapper.instance().getRule("Not in future"));
        createOptionDependent(factory, glucoseTestedYes, dateGlucoseTest);

        OptionEntry fastingSampleGlucose = factory.createOptionEntry("Fasting sample glucose", "Fasting sample for glucose test", EntryStatus.DISABLED);
        biochemistry.addEntry(fastingSampleGlucose);
        fastingSampleGlucose.setSection(glucoseSection);
        createOptions(factory, fastingSampleGlucose, new String[]{"Yes", "No"});
        createOptionDependent(factory, glucoseTestedYes, fastingSampleGlucose);

        NumericEntry glucose = factory.createNumericEntry("Glucose", "Glucose", EntryStatus.DISABLED);
        biochemistry.addEntry(glucose);
        glucose.setSection(glucoseSection);
        glucose.addValidationRule(ValidationRulesWrapper.instance().getRule("0.1 to 60"));
        glucose.addUnit(UnitWrapper.instance().getUnit("mmol/L"));
        createOptionDependent(factory, glucoseTestedYes, glucose);

        NumericEntry glucose2 = factory.createNumericEntry("Glucose #2", "Glucose", EntryStatus.DISABLED);
        biochemistry.addEntry(glucose2);
        glucose2.setSection(glucoseSection);
        glucose2.addValidationRule(ValidationRulesWrapper.instance().getRule("1 to 1000"));
        glucose2.addUnit(UnitWrapper.instance().getUnit("mg/dL"));
        createOptionDependent(factory, glucoseTestedYes, glucose2);


        //Glucose section
        Section thyroidFunctionSection = factory.createSection("Thyroid function section", "Thyroid function");
        biochemistry.addSection(thyroidFunctionSection);
        SectionOccurrence thyroidFunctionSectionOcc = factory.createSectionOccurrence("Thyroid function section occurrence");
        thyroidFunctionSection.addOccurrence(thyroidFunctionSectionOcc);

        OptionEntry thyroidFunctionTested = factory.createOptionEntry("Thyroid function tested", "Thyroid function tested");
        biochemistry.addEntry(thyroidFunctionTested);
        thyroidFunctionTested.setSection(thyroidFunctionSection);
        createOptions(factory, thyroidFunctionTested, new String[]{"Yes", "No"});
        Option thyroidFunctionTestedYes = thyroidFunctionTested.getOption(0);

        DateEntry dateThyroidFunctionTest = factory.createDateEntry("Date of thyroid function test", "Date of thyroid function test", EntryStatus.DISABLED);
        biochemistry.addEntry(dateThyroidFunctionTest);
        dateThyroidFunctionTest.setSection(thyroidFunctionSection);
        dateThyroidFunctionTest.addValidationRule(ValidationRulesWrapper.instance().getRule("2000 or after"));
        dateThyroidFunctionTest.addValidationRule(ValidationRulesWrapper.instance().getRule("Not in future"));
        createOptionDependent(factory, thyroidFunctionTestedYes, dateThyroidFunctionTest);

        NumericEntry tsh = factory.createNumericEntry("TSH", "Thyroid-stimulating hormone (TSH)", EntryStatus.DISABLED);
        biochemistry.addEntry(tsh);
        tsh.setSection(thyroidFunctionSection);
        tsh.addValidationRule(ValidationRulesWrapper.instance().getRule("0.01 to 100"));
        tsh.addUnit(UnitWrapper.instance().getUnit("mlU/L"));
        createOptionDependent(factory, thyroidFunctionTestedYes, tsh);

        NumericEntry ft4 = factory.createNumericEntry("FT4", "Free thyroxine (FT4)", EntryStatus.DISABLED);
        biochemistry.addEntry(ft4);
        ft4.setSection(thyroidFunctionSection);
        ft4.addValidationRule(ValidationRulesWrapper.instance().getRule("0.1 to 500"));
        ft4.addUnit(UnitWrapper.instance().getUnit("pmol/L"));
        createOptionDependent(factory, thyroidFunctionTestedYes, ft4);

        NumericEntry ft3 = factory.createNumericEntry("FT3", "Free tri-iodothyronine (FT3)", EntryStatus.DISABLED);
        biochemistry.addEntry(ft3);
        ft3.setSection(thyroidFunctionSection);
        ft3.addValidationRule(ValidationRulesWrapper.instance().getRule("0.1 to 100"));
        ft3.addUnit(UnitWrapper.instance().getUnit("pmol/L"));
        createOptionDependent(factory, thyroidFunctionTestedYes, ft3);

    }

}

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

import org.psygrid.common.UnitWrapper;
import org.psygrid.common.ValidationRulesWrapper;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.AssessmentForm;

/**
 * @author Rob Harper
 *
 */
public class MedicationAndClinicalDetails extends AssessmentForm {

    public static Document createDocument(Factory factory) {

        Document macd = factory.createDocument("Medication and Clinical Measurements",
                "Medication and Clinical Measurements");

        createDocumentStatuses(factory, macd);

        // main section
        Section generalSection = factory.createSection("General section");
        macd.addSection(generalSection);
        generalSection.setDisplayText("General");
        SectionOccurrence mainSectionOcc = factory.createSectionOccurrence("General Section Occurrence");
        generalSection.addOccurrence(mainSectionOcc);

        DateEntry dateOfFollowUp = factory.createDateEntry("Date of follow up", "Date of follow up");
        macd.addEntry(dateOfFollowUp);
        dateOfFollowUp.setSection(generalSection);
        dateOfFollowUp.addValidationRule(ValidationRulesWrapper.instance().getRule("Not in future"));
        dateOfFollowUp.addValidationRule(ValidationRulesWrapper.instance().getRule("2000 or after"));

        DateEntry dob = factory.createDateEntry("Date of birth", "Date of birth");
        macd.addEntry(dob);
        dob.setSection(generalSection);
        dob.addValidationRule(ValidationRulesWrapper.instance().getRule("After 1900"));
        dob.addValidationRule(ValidationRulesWrapper.instance().getRule("Not in future"));
        dob.setDisableStandardCodes(true);
        dob.setDisablePartialDate(true);

        //Diabetes classification section
        Section dcSection = factory.createSection("Diabetes classification section");
        macd.addSection(dcSection);
        dcSection.setDisplayText("Diabetes classification");
        SectionOccurrence dcSectionOcc = factory.createSectionOccurrence("Diabetes classification Section Occurrence");
        dcSection.addOccurrence(dcSectionOcc);

        OptionEntry changeInDiabetes = factory.createOptionEntry("Change in diabetes classification", "Change in diabetes classification");
        macd.addEntry(changeInDiabetes);
        changeInDiabetes.setSection(dcSection);
        createOptions(factory, changeInDiabetes, new String[]{"Yes", "No"}, new int[]{1,0});
        Option changeYes = changeInDiabetes.getOption(0);

        OptionEntry diabetesType = factory.createOptionEntry("Type of diabetes",
                "Type of diabetes (if untested, as determined by diagnosing clinician)",
                EntryStatus.DISABLED);
        macd.addEntry(diabetesType);
		diabetesType.setSection(dcSection);
		createOptions(factory, diabetesType, new String[]{
				"Type 1",
				"Type 2",
				"Maturity onset diabetes of the young (MODY)",
				"Latent autoimmune diabetes in adults (LADA)",
				"Other (please specify)"},
				new int[]{1,2,3,4,5});
		Option diabetesTypeOther = diabetesType.getOption(4);
		createOptionDependent(factory, changeYes, diabetesType);

		TextEntry diabetesTypeOtherSpecifics = factory.createTextEntry("Type of diabetes - other - specifics", "Type of diabetes - other - specifics", EntryStatus.DISABLED);
		macd.addEntry(diabetesTypeOtherSpecifics);
		diabetesTypeOtherSpecifics.setSection(dcSection);
		createOptionDependent(factory, diabetesTypeOther, diabetesTypeOtherSpecifics);

		LongTextEntry diabetesComments = factory.createLongTextEntry("Comments", "Comments on type of diabetes", EntryStatus.DISABLED);
		macd.addEntry(diabetesComments);
		diabetesComments.setSection(dcSection);
		createOptionDependent(factory, changeYes, diabetesComments, EntryStatus.OPTIONAL);

		//Medication section
		Section medSection = factory.createSection("Medication section");
        macd.addSection(medSection);
        medSection.setDisplayText("Medication");
        SectionOccurrence medSectionOcc = factory.createSectionOccurrence("Medication Section Occurrence");
        medSection.addOccurrence(medSectionOcc);

        OptionEntry changeInMedication = factory.createOptionEntry("Change in diabetes medication", "Change in diabetes medication");
        macd.addEntry(changeInMedication);
        changeInMedication.setSection(medSection);
        createOptions(factory, changeInMedication, new String[]{"Yes", "No"}, new int[]{1,0});
        Option changeMedYes = changeInMedication.getOption(0);

        OptionEntry insulin = factory.createOptionEntry("Insulin", "Insulin", EntryStatus.DISABLED);
        macd.addEntry(insulin);
        insulin.setSection(medSection);
        createOptions(factory, insulin, new String[]{"Yes", "No"}, new int[]{1,0});
        Option insulinYes = insulin.getOption(0);
        createOptionDependent(factory, changeMedYes, insulin);

        String[] insulinTypeNames = new String[]{"Subcutaneous injection", "Continuous subcutaneous insulin infusion", "Inhaled insulin"};
        for ( int i=0, c=insulinTypeNames.length; i<c; i++ ){
            OptionEntry oe = factory.createOptionEntry(insulinTypeNames[i], insulinTypeNames[i], EntryStatus.DISABLED);
            macd.addEntry(oe);
            oe.setSection(medSection);
            createOptions(factory, oe, new String[]{"Yes", "No"});
            createOptionDependent(factory, insulinYes, oe);
        }


        OptionEntry ohas = factory.createOptionEntry("OHAs", "Oral hypoglycaemic agents (OHAs)", EntryStatus.DISABLED);
        macd.addEntry(ohas);
        ohas.setSection(medSection);
        createOptions(factory, ohas, new String[]{"Yes", "No"}, new int[]{1,0});
        Option ohasYes = ohas.getOption(0);
        createOptionDependent(factory, changeMedYes, ohas);

        String[] ohasTypeNames = new String[]{"Sulphonylureas", "Metformin", "Other treatment (please specify)"};
        for ( int i=0, c=ohasTypeNames.length; i<c; i++ ){
            OptionEntry oe = factory.createOptionEntry(ohasTypeNames[i], ohasTypeNames[i], EntryStatus.DISABLED);
            macd.addEntry(oe);
            oe.setSection(medSection);
            createOptions(factory, oe, new String[]{"Yes", "No"});
            createOptionDependent(factory, ohasYes, oe);
            if ( ohasTypeNames[i].equals("Other treatment (please specify)") ){
            	TextEntry te = factory.createTextEntry("Other treatment - specifics", "Other treatment - specifics", EntryStatus.DISABLED);
            	macd.addEntry(te);
                te.setSection(medSection);
                createOptionDependent(factory, oe.getOption(0), te);
            }
        }

        LongTextEntry diabTreatComments = factory.createLongTextEntry("Comments on diabetes treatment",
                "Comments on diabetes treatment",
                EntryStatus.OPTIONAL);
        macd.addEntry(diabTreatComments);
        diabTreatComments.setSection(medSection);

        OptionEntry otherMed = factory.createOptionEntry("Other medication", "Other medication", EntryStatus.DISABLED);
        macd.addEntry(otherMed);
        otherMed.setSection(medSection);
        createOptions(factory, otherMed, new String[]{"Yes", "No"}, new int[]{1,0});
        Option otherMedYes = otherMed.getOption(0);
        createOptionDependent(factory, changeMedYes, otherMed);

        LongTextEntry otherMedComments = factory.createLongTextEntry("Comments on other medication",
                "Comments on other medication", EntryStatus.DISABLED);
        macd.addEntry(otherMedComments);
        otherMedComments.setSection(medSection);
        createOptionDependent(factory, otherMedYes, otherMedComments, EntryStatus.OPTIONAL);


		//Clinical measurements section
		Section clinicalSection = factory.createSection("Clinical measurements section");
        macd.addSection(clinicalSection);
        clinicalSection.setDisplayText("Clinical measurements");
        SectionOccurrence clinicalSectionOcc = factory.createSectionOccurrence("Clinical measurements Section Occurrence");
        clinicalSection.addOccurrence(clinicalSectionOcc);

        OptionEntry clinMeasMade = factory.createOptionEntry("Clinical measurements made", "Clinical measurements made");
        macd.addEntry(clinMeasMade);
        clinMeasMade.setSection(clinicalSection);
        createOptions(factory, clinMeasMade, new String[]{"Yes", "No"}, new int[]{1,0});
        Option cmmYes = clinMeasMade.getOption(0);

        OptionEntry bloodPressureMeasured = factory.createOptionEntry("Blood pressure measured", "Blood pressure measured", EntryStatus.DISABLED);
        macd.addEntry(bloodPressureMeasured);
        bloodPressureMeasured.setSection(clinicalSection);
        createOptions(factory, bloodPressureMeasured, new String[]{"Yes", "No"}, new int[]{1,0});
        Option bpmYes = bloodPressureMeasured.getOption(0);
        createOptionDependent(factory, cmmYes, bloodPressureMeasured);

        NumericEntry systolic = factory.createNumericEntry("Systolic", "Systolic blood pressure", EntryStatus.DISABLED);
        macd.addEntry(systolic);
        systolic.setSection(clinicalSection);
        systolic.addValidationRule(ValidationRulesWrapper.instance().getRule("Systolic BP"));
        systolic.addUnit(UnitWrapper.instance().getUnit("mm/Hg"));
        createOptionDependent(factory, bpmYes, systolic);

        NumericEntry diastolic = factory.createNumericEntry("Diastolic", "Diastolic blood pressure", EntryStatus.DISABLED);
        macd.addEntry(diastolic);
        diastolic.setSection(clinicalSection);
        diastolic.addValidationRule(ValidationRulesWrapper.instance().getRule("Diastolic BP"));
        diastolic.addUnit(UnitWrapper.instance().getUnit("mm/Hg"));
        createOptionDependent(factory, bpmYes, diastolic);

        OptionEntry weightMeasured = factory.createOptionEntry("Weight measured", "Weight measured", EntryStatus.DISABLED);
        macd.addEntry(weightMeasured);
        weightMeasured.setSection(clinicalSection);
        createOptions(factory, weightMeasured, new String[]{"Yes", "No"}, new int[]{1,0});
        Option weightYes = weightMeasured.getOption(0);
        createOptionDependent(factory, cmmYes, weightMeasured);

        NumericEntry weight = factory.createNumericEntry("Weight", "Weight", EntryStatus.DISABLED);
        macd.addEntry(weight);
        weight.setSection(clinicalSection);
        weight.addUnit(UnitWrapper.instance().getUnit("kg"));
        weight.addValidationRule(ValidationRulesWrapper.instance().getRule("Weight kg"));
        createOptionDependent(factory, weightYes, weight);

        OptionEntry heightMeasured = factory.createOptionEntry("Height measured", "Height measured", EntryStatus.DISABLED);
        macd.addEntry(heightMeasured);
        heightMeasured.setSection(clinicalSection);
        createOptions(factory, heightMeasured, new String[]{"Yes", "No"}, new int[]{1,0});
        Option heightYes = heightMeasured.getOption(0);
        createOptionDependent(factory, cmmYes, heightMeasured);

        NumericEntry height = factory.createNumericEntry("Height", "Height", EntryStatus.DISABLED);
        macd.addEntry(height);
        height.setSection(clinicalSection);
        height.addUnit(UnitWrapper.instance().getUnit("m"));
        height.addValidationRule(ValidationRulesWrapper.instance().getRule("Height m"));
        createOptionDependent(factory, heightYes, height);

        DerivedEntry bmi = factory.createDerivedEntry("BMI", "Body Mass Index", EntryStatus.DISABLED);
        macd.addEntry(bmi);
        bmi.setSection(clinicalSection);
        bmi.setDescription("= weight / height^2");
        bmi.setFormula("w/(h*h)");
        bmi.addVariable("w", weight);
        bmi.addVariable("h", height);
        createOptionDependent(factory, cmmYes, bmi);

        OptionEntry waistMeasured = factory.createOptionEntry("Waist measured", "Waist circumference measured");
        macd.addEntry(waistMeasured);
        waistMeasured.setSection(clinicalSection);
        createOptions(factory, waistMeasured, new String[]{"Yes", "No"}, new int[]{1,0});
        Option waistYes = waistMeasured.getOption(0);
        createOptionDependent(factory, cmmYes, waistMeasured);

        NumericEntry waist = factory.createNumericEntry("Waist circumference", "Waist circumference", EntryStatus.DISABLED);
        macd.addEntry(waist);
        waist.setSection(clinicalSection);
        waist.addUnit(UnitWrapper.instance().getUnit("cm"));
        waist.addValidationRule(ValidationRulesWrapper.instance().getRule("Circumference"));
        createOptionDependent(factory, waistYes, waist);

        OptionEntry hipMeasured = factory.createOptionEntry("Hip measured", "Hip circumference measured");
        macd.addEntry(hipMeasured);
        hipMeasured.setSection(clinicalSection);
        createOptions(factory, hipMeasured, new String[]{"Yes", "No"}, new int[]{1,0});
        Option hipYes = hipMeasured.getOption(0);
        createOptionDependent(factory, cmmYes, hipMeasured);

        NumericEntry hip = factory.createNumericEntry("Hip circumference", "Hip circumference", EntryStatus.DISABLED);
        macd.addEntry(hip);
        hip.setSection(clinicalSection);
        hip.addUnit(UnitWrapper.instance().getUnit("cm"));
        hip.addValidationRule(ValidationRulesWrapper.instance().getRule("Circumference"));
        createOptionDependent(factory, hipYes, hip);

        DerivedEntry waistHipRatio = factory.createDerivedEntry("Waist to hip ratio", "Waist to hip ratio");
        macd.addEntry(waistHipRatio);
        waistHipRatio.setSection(clinicalSection);
        waistHipRatio.setDescription("= waist circumference / hip circumference");
        waistHipRatio.setFormula("w/h");
        waistHipRatio.addVariable("w", waist);
        waistHipRatio.addVariable("h", hip);

        return macd;
    }
}

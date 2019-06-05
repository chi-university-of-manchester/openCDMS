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
public class ClinicalMeasurements extends AssessmentForm {

    public static Document createDocument(Factory factory) {

        Document clinicalMeasurements = factory.createDocument("Clinical Measurements",
                "Clinical Measurements");

        createDocumentStatuses(factory, clinicalMeasurements);

        // main section
        Section mainSection = factory.createSection("Main section");
        clinicalMeasurements.addSection(mainSection);
        mainSection.setDisplayText("Main");
        SectionOccurrence mainSectionOcc = factory.createSectionOccurrence("Main Section Occurrence");
        mainSection.addOccurrence(mainSectionOcc);

        OptionEntry bloodPressureMeasured = factory.createOptionEntry("Blood pressure measured", "Blood pressure measured");
        clinicalMeasurements.addEntry(bloodPressureMeasured);
        bloodPressureMeasured.setSection(mainSection);
        createOptions(factory, bloodPressureMeasured, new String[]{"Yes", "No"}, new int[]{1,0});
        Option bpmYes = bloodPressureMeasured.getOption(0);

        NumericEntry systolic = factory.createNumericEntry("Systolic", "Systolic blood pressure", EntryStatus.DISABLED);
        clinicalMeasurements.addEntry(systolic);
        systolic.setSection(mainSection);
        systolic.addValidationRule(ValidationRulesWrapper.instance().getRule("Systolic BP"));
        systolic.addUnit(UnitWrapper.instance().getUnit("mm/Hg"));
        createOptionDependent(factory, bpmYes, systolic);

        NumericEntry diastolic = factory.createNumericEntry("Diastolic", "Diastolic blood pressure", EntryStatus.DISABLED);
        clinicalMeasurements.addEntry(diastolic);
        diastolic.setSection(mainSection);
        diastolic.addValidationRule(ValidationRulesWrapper.instance().getRule("Diastolic BP"));
        diastolic.addUnit(UnitWrapper.instance().getUnit("mm/Hg"));
        createOptionDependent(factory, bpmYes, diastolic);

        OptionEntry weightMeasured = factory.createOptionEntry("Weight measured", "Weight measured");
        clinicalMeasurements.addEntry(weightMeasured);
        weightMeasured.setSection(mainSection);
        createOptions(factory, weightMeasured, new String[]{"Yes", "No"}, new int[]{1,0});
        Option weightYes = weightMeasured.getOption(0);

        NumericEntry weight = factory.createNumericEntry("Weight", "Weight", EntryStatus.DISABLED);
        clinicalMeasurements.addEntry(weight);
        weight.setSection(mainSection);
        weight.addUnit(UnitWrapper.instance().getUnit("kg"));
        weight.addValidationRule(ValidationRulesWrapper.instance().getRule("Weight kg"));
        createOptionDependent(factory, weightYes, weight);

        OptionEntry heightMeasured = factory.createOptionEntry("Height measured", "Height measured");
        clinicalMeasurements.addEntry(heightMeasured);
        heightMeasured.setSection(mainSection);
        createOptions(factory, heightMeasured, new String[]{"Yes", "No"}, new int[]{1,0});
        Option heightYes = heightMeasured.getOption(0);

        NumericEntry height = factory.createNumericEntry("Height", "Height", EntryStatus.DISABLED);
        clinicalMeasurements.addEntry(height);
        height.setSection(mainSection);
        height.addUnit(UnitWrapper.instance().getUnit("m"));
        height.addValidationRule(ValidationRulesWrapper.instance().getRule("Height m"));
        createOptionDependent(factory, heightYes, height);

        DerivedEntry bmi = factory.createDerivedEntry("BMI", "Body Mass Index");
        clinicalMeasurements.addEntry(bmi);
        bmi.setSection(mainSection);
        bmi.setDescription("= weight / height^2");
        bmi.setFormula("w/(h*h)");
        bmi.addVariable("w", weight);
        bmi.addVariable("h", height);

        OptionEntry waistMeasured = factory.createOptionEntry("Waist measured", "Waist circumference measured");
        clinicalMeasurements.addEntry(waistMeasured);
        waistMeasured.setSection(mainSection);
        createOptions(factory, waistMeasured, new String[]{"Yes", "No"}, new int[]{1,0});
        Option waistYes = waistMeasured.getOption(0);

        NumericEntry waist = factory.createNumericEntry("Waist circumference", "Waist circumference", EntryStatus.DISABLED);
        clinicalMeasurements.addEntry(waist);
        waist.setSection(mainSection);
        waist.addUnit(UnitWrapper.instance().getUnit("cm"));
        waist.addValidationRule(ValidationRulesWrapper.instance().getRule("Circumference"));
        createOptionDependent(factory, waistYes, waist);

        OptionEntry hipMeasured = factory.createOptionEntry("Hip measured", "Hip circumference measured");
        clinicalMeasurements.addEntry(hipMeasured);
        hipMeasured.setSection(mainSection);
        createOptions(factory, hipMeasured, new String[]{"Yes", "No"}, new int[]{1,0});
        Option hipYes = hipMeasured.getOption(0);

        NumericEntry hip = factory.createNumericEntry("Hip circumference", "Hip circumference", EntryStatus.DISABLED);
        clinicalMeasurements.addEntry(hip);
        hip.setSection(mainSection);
        hip.addUnit(UnitWrapper.instance().getUnit("cm"));
        hip.addValidationRule(ValidationRulesWrapper.instance().getRule("Circumference"));
        createOptionDependent(factory, hipYes, hip);

        DerivedEntry waistHipRatio = factory.createDerivedEntry("Waist to hip ratio", "Waist to hip ratio");
        clinicalMeasurements.addEntry(waistHipRatio);
        waistHipRatio.setSection(mainSection);
        waistHipRatio.setDescription("= waist circumference / hip circumference");
        waistHipRatio.setFormula("w/h");
        waistHipRatio.addVariable("w", waist);
        waistHipRatio.addVariable("h", hip);

        return clinicalMeasurements;
    }
}

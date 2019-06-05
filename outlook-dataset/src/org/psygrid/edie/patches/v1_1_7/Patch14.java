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


package org.psygrid.edie.patches.v1_1_7;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.AssessmentForm;
import org.psygrid.outlook.patches.AbstractPatch;

/**
 * @author Rob Harper
 *
 */
public class Patch14 extends AbstractPatch {

	/**
	 * @see org.psygrid.outlook.patches.AbstractPatch#applyPatch(org.psygrid.data.model.hibernate.DataSet, java.lang.String)
	 */
	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		Factory factory = new HibernateFactory();

		DocumentGroup grp = ds.getDocumentGroup(0);
		if ( !"Baseline -1 Group".equals(grp.getName())){
			throw new RuntimeException("This is not the Baseline -1 Group doc group, it is "+grp.getName());
		}

		ValidationRule positiveNumber = ds.getValidationRule(15);
		if ( !"Positive".equals(positiveNumber.getDescription())){
			throw new RuntimeException("This is not the Positive validation rule, it is "+positiveNumber.getDescription());
		}

		Unit mg = ds.getUnit(27);
		if ( !"mg".equals(mg.getAbbreviation())){
			throw new RuntimeException("This is not the mg unit, it is "+mg.getAbbreviation());
		}

		Document doc = factory.createDocument("Current Treatment", "Current Treatment");

		AssessmentForm.createDocumentStatuses(factory, doc);

		// main section
		Section mainSection = factory.createSection("Main section");
		doc.addSection(mainSection);
		mainSection.setDisplayText("Main");
		SectionOccurrence mainSectionOcc = factory
				.createSectionOccurrence("Main Section Occurrence");
		mainSection.addOccurrence(mainSectionOcc);

		CompositeEntry currentMedication = factory.createComposite("Current medication", "What medications is the participant currently taking?");
		doc.addEntry(currentMedication);
        currentMedication.setSection(mainSection);

        TextEntry medicationC = factory.createTextEntry("Medication", "Medication");
        currentMedication.addEntry(medicationC);
        medicationC.setSection(mainSection);

        NumericEntry doseC = factory.createNumericEntry("Dose", "Dose");
        currentMedication.addEntry(doseC);
        doseC.setSection(mainSection);
        doseC.addUnit(mg);
        doseC.addValidationRule(positiveNumber);

        OptionEntry freqOfUseC = factory.createOptionEntry("Freq", "Freq");
        currentMedication.addEntry(freqOfUseC);
        freqOfUseC.setSection(mainSection);
        freqOfUseC.addOption(factory.createOption("As required",0));
        freqOfUseC.addOption(factory.createOption("Daily",1));
        freqOfUseC.addOption(factory.createOption("Weekly",2));
        freqOfUseC.addOption(factory.createOption("Monthly",3));

		ds.addDocument(doc);

		DocumentOccurrence docOcc = factory.createDocumentOccurrence("Baseline -1");
		docOcc.setDisplayText("Baseline -1");
		docOcc.setDocumentGroup(grp);

		doc.addOccurrence(docOcc);

		doc.addConsentFormGroup(ds.getAllConsentFormGroup(0));

	}

	/**
	 * @see org.psygrid.outlook.patches.AbstractPatch#getName()
	 */
	@Override
	public String getName() {
		return "Add Current Treatment document";
	}

}

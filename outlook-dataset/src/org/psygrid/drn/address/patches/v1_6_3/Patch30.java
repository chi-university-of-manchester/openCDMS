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

package org.psygrid.drn.address.patches.v1_6_3;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.patches.AbstractPatch;

/**
 * Patch to update the validation rule for blood glucose. See Bug #1200
 *
 * @author Lucy Bridges
 *
 */
public class Patch30 extends AbstractPatch {

	/* (non-Javadoc)
	 * @see org.psygrid.outlook.patches.AbstractPatch#applyPatch(org.psygrid.data.model.IDataSet, java.lang.String)
	 */
	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		Factory factory = new HibernateFactory();
		NumericValidationRule pointOneToSixty = factory.createNumericValidationRule();
        pointOneToSixty.setDescription("0.1 to 60");
        pointOneToSixty.setLowerLimit(new Double(0.1));
        pointOneToSixty.setUpperLimit(new Double(60));
        pointOneToSixty.setUpperGte(true);

		ds.addValidationRule(pointOneToSixty);

		for (int i = 0; i < ds.numDocuments(); i++) {
			Document doc = ds.getDocument(i);
			if (doc.getName().equals("Biochemistry and Urinalysis")) {
				System.out.println("Found the Biochemistry and Urinalysis Document");
				for (int e = 0; e < doc.numEntries(); e++) {
					Entry entry = doc.getEntry(e);

					if (entry.getName().equals("Glucose")) {
						System.out.println("Found the Glucose Entry");
						NumericEntry glucose = (NumericEntry) entry;
						for (int v=0; v<glucose.numValidationRules(); v++) {
							if ("0.1 to 50".equals(glucose.getValidationRule(v).getDescription())) {
								glucose.removeValidationRule(v);
								break;
							}
						}
						glucose.addValidationRule(pointOneToSixty);
						break;
					}
				}
			}
		}

	}

	/* (non-Javadoc)
	 * @see org.psygrid.outlook.patches.AbstractPatch#getName()
	 */
	@Override
	public String getName() {
		return "Update Validation Rule for Blood Glucose Levels";
	}

}

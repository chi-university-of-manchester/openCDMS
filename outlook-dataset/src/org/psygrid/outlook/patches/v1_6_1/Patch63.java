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
package org.psygrid.outlook.patches.v1_6_1;

import java.util.ArrayList;
import java.util.List;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.ExternalDerivedEntry;
import org.psygrid.outlook.patches.AbstractPatch;

/**
 * @author lucy
 *
 */
public class Patch63 extends AbstractPatch {

	public String getName() {
		return "Alter the Opcrit form to allow some optional fields";
	}

	public void applyPatch(DataSet ds, String saml) throws Exception {

		for (int i=0; i < ds.numDocuments(); i++) {
			Document doc = ds.getDocument(i);
			if (doc.getName().equals("Opcrit Data Entry Sheet")) {
				//Found opcrit
				System.out.println("Found the Opcrit Document.");
				for (int j = 0; j < doc.numEntries(); j++) {

					if (doc.getEntry(j) instanceof ExternalDerivedEntry) {
						System.out.println("Found the external derived entry.");
						ExternalDerivedEntry exDE = (ExternalDerivedEntry)doc.getEntry(j);
						System.out.println("Updating..");
						//Allow std codes to be present
						exDE.setTransformWithStdCodes(true);

						/*the items that do not count towards any ratings:
						Q1.Source of rating
						Q2. Time frame
						Q12. Alcohol / drug abuse within 1 year of onset of psychotic symptoms
						Q14. Family history of other psychiatric disorder
						Q15. Coarse brain disease prior to onset
						Q16. Definite psychosocial stressor prior to onset
						Q78. Lifetime diagnosis of alcohol abuse/dependence
						Q79. Lifetime diagnosis of cannabis abuse/dependence
						Q80. Lifetime diagnosis of other abuse/dependence
						Q81. Alcohol abuse/dependence with psychopathology
						Q82. Cannabis abuse/dependence with psychopathology
						Q83. Other abuse/dependence with psychopathology
						Q90. Course of disorder
						 */
						List<Integer> ignoreQuestions = new ArrayList<Integer>();
						//Add the questions (-1 as entries start at 0)
						ignoreQuestions.add(0);
						ignoreQuestions.add(1);
						ignoreQuestions.add(11);
						ignoreQuestions.add(13);
						ignoreQuestions.add(14);
						ignoreQuestions.add(15);
						ignoreQuestions.add(77);
						ignoreQuestions.add(78);
						ignoreQuestions.add(79);
						ignoreQuestions.add(80);
						ignoreQuestions.add(81);
						ignoreQuestions.add(82);
						ignoreQuestions.add(89);

						//List of variable names that are required and cannot have std codes entered.
						for (int e = 0; e < 90; e++) {
							if (!ignoreQuestions.contains(e)) {
								exDE.addTransformRequiredVariable(Integer.toString(e));
							}
						}
					}
				}
			}
		}
	}

}

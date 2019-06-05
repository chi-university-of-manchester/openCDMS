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

package org.psygrid.edie.test.patches.v1_6_2;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch18 extends AbstractPatch {

	@Override
	public String getName() {
		return "Update the CAARMS with GAF document for Bug #697";
	}

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {

		for (int i = 0 ; i< ds.numDocuments(); i++) {
			Document doc = ds.getDocument(i);

			if ("CAARMS with GAF".equals(doc.getName())) {
				System.out.println("Found the CAARMS with GAF document");

				for (int j = 0; j < doc.numEntries(); j++) {
					Entry entry = doc.getEntry(j);

					if (entry instanceof DerivedEntry) {
						DerivedEntry de = (DerivedEntry)entry;
						if ("Group 2a1".equals(entry.getName())) {
							System.out.println("Updating Group 2a1");
							de.addVariableDefault("a", new NumericValue(0.0));
							de.addVariableDefault("b", new NumericValue(0.0));
							de.addVariableDefault("c", new NumericValue(0.0));
							de.addVariableDefault("d", new NumericValue(0.0));
							continue;
						}
						if ("Group 2a2".equals(entry.getName())) {
							System.out.println("Updating Group 2a2");
							de.addVariableDefault("a", new NumericValue(0.0));
							de.addVariableDefault("b", new NumericValue(0.0));
							de.addVariableDefault("c", new NumericValue(0.0));
							de.addVariableDefault("d", new NumericValue(0.0));
							continue;
						}
						if ("Group 2b GRS".equals(entry.getName())) {
							System.out.println("Updating Group 2b GRS");
							de.addVariableDefault("a", new NumericValue(0.0));
							de.addVariableDefault("b", new NumericValue(0.0));
							de.addVariableDefault("c", new NumericValue(0.0));
							de.addVariableDefault("d", new NumericValue(0.0));
							continue;
						}
						if ("Group 3 GRS".equals(entry.getName())) {
							System.out.println("Updating Group 3 GRS");
							de.addVariableDefault("a", new NumericValue(0.0));
							de.addVariableDefault("b", new NumericValue(0.0));
							de.addVariableDefault("c", new NumericValue(0.0));
							de.addVariableDefault("d", new NumericValue(0.0));
							continue;
						}
						if ("Severity Score Psychosis".equals(entry.getName())) {
							System.out.println("Updating Severity Score Psychosis");
							de.addVariableDefault("a", new NumericValue(0.0));
							de.addVariableDefault("b", new NumericValue(0.0));
							de.addVariableDefault("c", new NumericValue(0.0));
							de.addVariableDefault("d", new NumericValue(0.0));
							continue;
						}
					}
				}
				System.out.println("Finished. Five entries should have been updated.");
			}

		}
	}
}

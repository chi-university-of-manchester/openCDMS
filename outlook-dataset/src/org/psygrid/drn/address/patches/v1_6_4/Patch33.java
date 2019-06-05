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

package org.psygrid.drn.address.patches.v1_6_4;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.data.model.hibernate.Option;
import org.psygrid.data.model.hibernate.OptionEntry;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch33 extends AbstractPatch {

	public String getName() {
		return "Add missing codes to Address dataset";
	}

	public void applyPatch(DataSet ds, String saml) throws Exception {
		for (Document doc: ((DataSet)ds).getDocuments()) {
			if (doc.getDisplayText().equals("Biochemistry and Urinalysis")) {
				updateBiochemistryAndUrinalysis(doc);
			}
			else if (doc.getDisplayText().equals("Medication and Clinical Measurements")) {
				updateMedicationAndClinicalMeasurements(doc);
			}
			else if (doc.getDisplayText().equals("Biochemistry")) {
				updateBiochemistry(doc);
			}
		}
	}

	private void updateBiochemistryAndUrinalysis(Document doc) {
		int counter = 0;
		for (Entry entry: doc.getEntries()) {
			if ("Glucose tested".equals(entry.getName())) {
				System.out.println("Found Glucose tested");
				counter++;
				OptionEntry glucose = (OptionEntry)entry;
				for (Option option: glucose.getOptions()) {
					if (option.getName().equals("No")) {
						option.setCode(new Integer(0));
					}
					else if (option.getName().equals("Yes")) {
						option.setCode(new Integer(1));
					}
				}
			} else if ("Fasting sample glucose".equals(entry.getName())) {
				System.out.println("Found Fasting sample glucose");
				counter++;
				OptionEntry glucose = (OptionEntry)entry;
				for (Option option: glucose.getOptions()) {
					if (option.getName().equals("No")) {
						option.setCode(new Integer(0));
					}
					else if (option.getName().equals("Yes")) {
						option.setCode(new Integer(1));
					}
				}
			} else if ("Thyroid function tested".equals(entry.getName())) {
				System.out.println("Found Thyroid function tested");
				counter++;
				OptionEntry glucose = (OptionEntry)entry;
				for (Option option: glucose.getOptions()) {
					if (option.getName().equals("No")) {
						option.setCode(new Integer(0));
					}
					else if (option.getName().equals("Yes")) {
						option.setCode(new Integer(1));
					}
				}
			}
		}
		System.out.println("Edited "+counter+" entries out of 3");
	}

	private void updateMedicationAndClinicalMeasurements(Document doc) {
		int counter = 0;
		for (Entry entry: doc.getEntries()) {
			if ("Subcutaneous injection".equals(entry.getName())) {
				System.out.println("Subcutaneous injection");
				counter++;
				OptionEntry glucose = (OptionEntry)entry;
				for (Option option: glucose.getOptions()) {
					if (option.getName().equals("No")) {
						option.setCode(new Integer(0));
					}
					else if (option.getName().equals("Yes")) {
						option.setCode(new Integer(1));
					}
				}
			} else if ("Continuous subcutaneous insulin infusion".equals(entry.getName())) {
				System.out.println("Continuous subcutaneous insulin infusion");
				counter++;
				OptionEntry glucose = (OptionEntry)entry;
				for (Option option: glucose.getOptions()) {
					if (option.getName().equals("No")) {
						option.setCode(new Integer(0));
					}
					else if (option.getName().equals("Yes")) {
						option.setCode(new Integer(1));
					}
				}
			} else if ("Inhaled insulin".equals(entry.getName())) {
				System.out.println("Inhaled insulin");
				counter++;
				OptionEntry glucose = (OptionEntry)entry;
				for (Option option: glucose.getOptions()) {
					if (option.getName().equals("No")) {
						option.setCode(new Integer(0));
					}
					else if (option.getName().equals("Yes")) {
						option.setCode(new Integer(1));
					}
				}
			} else if ("Sulphonylureas".equals(entry.getName())) {
				System.out.println("Sulphonylureas");
				counter++;
				OptionEntry glucose = (OptionEntry)entry;
				for (Option option: glucose.getOptions()) {
					if (option.getName().equals("No")) {
						option.setCode(new Integer(0));
					}
					else if (option.getName().equals("Yes")) {
						option.setCode(new Integer(1));
					}
				}
			} else if ("Metformin".equals(entry.getName())) {
				System.out.println("Metformin");
				counter++;
				OptionEntry glucose = (OptionEntry)entry;
				for (Option option: glucose.getOptions()) {
					if (option.getName().equals("No")) {
						option.setCode(new Integer(0));
					}
					else if (option.getName().equals("Yes")) {
						option.setCode(new Integer(1));
					}
				}
			} else if ("Other treatment (please specify)".equals(entry.getName())) {
				System.out.println("Other treatment (please specify)");
				counter++;
				OptionEntry glucose = (OptionEntry)entry;
				for (Option option: glucose.getOptions()) {
					if (option.getName().equals("No")) {
						option.setCode(new Integer(0));
					}
					else if (option.getName().equals("Yes")) {
						option.setCode(new Integer(1));
					}
				}
			}
		}
		System.out.println("Edited "+counter+" entries out of 6");
	}

	private void updateBiochemistry(Document doc) {
		int counter = 0;
		for (Entry entry: doc.getEntries()) {
			if ("Glucose tested".equals(entry.getName())) {
				System.out.println("Found Glucose tested");
				counter++;
				OptionEntry glucose = (OptionEntry)entry;
				for (Option option: glucose.getOptions()) {
					if (option.getName().equals("No")) {
						option.setCode(new Integer(0));
					}
					else if (option.getName().equals("Yes")) {
						option.setCode(new Integer(1));
					}
				}
			} else if ("Fasting sample glucose".equals(entry.getName())) {
				System.out.println("Found Fasting sample glucose");
				counter++;
				OptionEntry glucose = (OptionEntry)entry;
				for (Option option: glucose.getOptions()) {
					if (option.getName().equals("No")) {
						option.setCode(new Integer(0));
					}
					else if (option.getName().equals("Yes")) {
						option.setCode(new Integer(1));
					}
				}
			} else if ("Thyroid function tested".equals(entry.getName())) {
				System.out.println("Found Thyroid function tested");
				counter++;
				OptionEntry glucose = (OptionEntry)entry;
				for (Option option: glucose.getOptions()) {
					if (option.getName().equals("No")) {
						option.setCode(new Integer(0));
					}
					else if (option.getName().equals("Yes")) {
						option.setCode(new Integer(1));
					}
				}
			}
		}
		System.out.println("Edited "+counter+" entries out of 3");
	}
}

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

package org.psygrid.edie.test.patches.v1_6_4;

import org.psygrid.data.model.hibernate.CompositeEntry;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.data.model.hibernate.Option;
import org.psygrid.data.model.hibernate.OptionEntry;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch20 extends AbstractPatch {

	public String getName() {
		return "Add missing option codes to EDIE2Test documents";
	}

	public void applyPatch(DataSet ds, String saml) throws Exception {
		for (Document doc: ((DataSet)ds).getDocuments()) {
			if (doc.getDisplayText().equals("Drug Check")) {
				updateDrugCheck(doc);
			}
			else if (doc.getDisplayText().equals("Non-Psychiatric Hospital Record Record")) {
				updateNPHR(doc);
			}
			else if (doc.getDisplayText().equals("Psychiatric Hospital Record Record")) {
				updatePHR(doc);
			}
			else if (doc.getDisplayText().equals("Economic Patient Questionnaire v3")) {
				updateEPQv3(doc);
			}
		}
	}


	private void updateDrugCheck(Document doc) {
		int counter = 0;
		System.out.println("34 is "+doc.getEntry(34).getName()+": "+doc.getEntry(34).getDisplayText());
		System.out.println("38 is "+doc.getEntry(38).getName()+": "+doc.getEntry(38).getDisplayText());

		System.out.println("Type - 34");counter++;
		OptionEntry oe34 = (OptionEntry)doc.getEntry(34);
		for (Option option: oe34.getOptions()) {
			if (option.getName().equals("Pills")) {
				option.setCode(new Integer(0));
			}
			else if (option.getName().equals("Powder")) {
				option.setCode(new Integer(1));
			}
			else if (option.getName().equals("Injection")) {
				option.setCode(new Integer(2));
			}
			else if (option.getName().equals("Other")) {
				option.setCode(new Integer(3));
			}
		}
		System.out.println("Type - 38");counter++;
		OptionEntry oe38 = (OptionEntry)doc.getEntry(38);
		for (Option option: oe38.getOptions()) {
			if (option.getName().equals("Pills")) {
				option.setCode(new Integer(0));
			}
			else if (option.getName().equals("Powder")) {
				option.setCode(new Integer(1));
			}
			else if (option.getName().equals("Liquid")) {
				option.setCode(new Integer(2));
			}
			else if (option.getName().equals("Injection")) {
				option.setCode(new Integer(3));
			}
			else if (option.getName().equals("Other")) {
				option.setCode(new Integer(4));
			}
		}
		System.out.println("Type - 27");counter++;
		OptionEntry oe27 = (OptionEntry)doc.getEntry(27);
		for (Option option: oe27.getOptions()) {
			if (option.getName().equals("Petrol")) {
				option.setCode(new Integer(0));
			}
			else if (option.getName().equals("Glue")) {
				option.setCode(new Integer(1));
			}
			else if (option.getName().equals("Aerosol")) {
				option.setCode(new Integer(2));
			}
			else if (option.getName().equals("Other")) {
				option.setCode(new Integer(3));
			}
		}
		for (Entry entry: doc.getEntries()) {
			if ("Alchoholic drinks?".equals(entry.getDisplayText())) {
				System.out.println("Found Alchoholic drinks?");
				counter++;
				OptionEntry alcohol = (OptionEntry)entry;
				for (Option option: alcohol.getOptions()) {
					if (option.getName().equals("No")) {
						option.setCode(new Integer(0));
					}
					else if (option.getName().equals("Yes")) {
						option.setCode(new Integer(1));
					}
				}
			}
			else if ("Tea, coffee, or cola drinks?".equals(entry.getDisplayText())) {
				System.out.println("Tea, coffee, or cola drinks?");counter++;
				OptionEntry alcohol = (OptionEntry)entry;
				for (Option option: alcohol.getOptions()) {
					if (option.getName().equals("No")) {
						option.setCode(new Integer(0));
					}
					else if (option.getName().equals("Yes")) {
						option.setCode(new Integer(1));
					}
				}
			}
			else if ("Cigarettes?".equals(entry.getDisplayText())) {
				System.out.println("Cigarettes?");counter++;
				OptionEntry alcohol = (OptionEntry)entry;
				for (Option option: alcohol.getOptions()) {
					if (option.getName().equals("No")) {
						option.setCode(new Integer(0));
					}
					else if (option.getName().equals("Yes")) {
						option.setCode(new Integer(1));
					}
				}
			}
			else if ("Sleeping tablets or sedatives? (like valium or normison)".equals(entry.getDisplayText())) {
				System.out.println("Sleeping tablets or sedatives? (like valium or normison)");counter++;
				OptionEntry alcohol = (OptionEntry)entry;
				for (Option option: alcohol.getOptions()) {
					if (option.getName().equals("No")) {
						option.setCode(new Integer(0));
					}
					else if (option.getName().equals("Yes")) {
						option.setCode(new Integer(1));
					}
				}
			}
			else if ("Other painkillers".equals(entry.getDisplayText())) {
				System.out.println("Other painkillers");counter++;
				OptionEntry alcohol = (OptionEntry)entry;
				for (Option option: alcohol.getOptions()) {
					if (option.getName().equals("No")) {
						option.setCode(new Integer(0));
					}
					else if (option.getName().equals("Yes")) {
						option.setCode(new Integer(1));
					}
				}
			}
			else if ("Marijuana, cannabis, or hash?".equals(entry.getDisplayText())) {
				System.out.println("Marijuana, cannabis, or hash?");counter++;
				OptionEntry alcohol = (OptionEntry)entry;
				for (Option option: alcohol.getOptions()) {
					if (option.getName().equals("No")) {
						option.setCode(new Integer(0));
					}
					else if (option.getName().equals("Yes")) {
						option.setCode(new Integer(1));
					}
				}
			}
			else if ("Hash Type".equals(entry.getName())) {
				System.out.println("Hash Type");counter++;
				OptionEntry alcohol = (OptionEntry)entry;
				for (Option option: alcohol.getOptions()) {
					if (option.getName().equals("Leaf")) {
						option.setCode(new Integer(0));
					}
					else if (option.getName().equals("Head")) {
						option.setCode(new Integer(1));
					}
					else if (option.getName().equals("Hash")) {
						option.setCode(new Integer(2));
					}
					else if (option.getName().equals("Other")) {
						option.setCode(new Integer(3));
					}
				}
			}
			else if ("Drugs you sniff, like petrol/glue?".equals(entry.getDisplayText())) {
				System.out.println("Drugs you sniff, like petrol/glue?");counter++;
				OptionEntry alcohol = (OptionEntry)entry;
				for (Option option: alcohol.getOptions()) {
					if (option.getName().equals("No")) {
						option.setCode(new Integer(0));
					}
					else if (option.getName().equals("Yes")) {
						option.setCode(new Integer(1));
					}
				}
			}
			else if ("Drugs like LSD?".equals(entry.getDisplayText())) {
				System.out.println("Drugs like LSD?");counter++;
				OptionEntry alcohol = (OptionEntry)entry;
				for (Option option: alcohol.getOptions()) {
					if (option.getName().equals("No")) {
						option.setCode(new Integer(0));
					}
					else if (option.getName().equals("Yes")) {
						option.setCode(new Integer(1));
					}
				}
			}
			else if ("Speed, ecstasy, crack or cocaine?".equals(entry.getDisplayText())) {
				System.out.println("Speed, ecstasy, crack or cocaine?");counter++;
				OptionEntry alcohol = (OptionEntry)entry;
				for (Option option: alcohol.getOptions()) {
					if (option.getName().equals("No")) {
						option.setCode(new Integer(0));
					}
					else if (option.getName().equals("Yes")) {
						option.setCode(new Integer(1));
					}
				}
			}
			else if ("Heroin, morphine or methadone?".equals(entry.getDisplayText())) {
				System.out.println("Heroin, morphine or methadone?");counter++;
				OptionEntry alcohol = (OptionEntry)entry;
				for (Option option: alcohol.getOptions()) {
					if (option.getName().equals("No")) {
						option.setCode(new Integer(0));
					}
					else if (option.getName().equals("Yes")) {
						option.setCode(new Integer(1));
					}
				}
			}
			else if ("Other Drugs not listed above".equals(entry.getDisplayText())) {
				System.out.println("Other Drugs not listed above");counter++;
				OptionEntry alcohol = (OptionEntry)entry;
				for (Option option: alcohol.getOptions()) {
					if (option.getName().equals("No")) {
						option.setCode(new Integer(0));
					}
					else if (option.getName().equals("Yes")) {
						option.setCode(new Integer(1));
					}
				}
			}
			else if ("Drug that caused most problems".equals(entry.getName())) {
				System.out.println("Drug that caused most problems");counter++;
				OptionEntry alcohol = (OptionEntry)entry;
				for (Option option: alcohol.getOptions()) {
					if (option.getName().equals("Alcohol?")) {
						option.setCode(new Integer(0));
					}
					else if (option.getName().equals("Tea, coffe or cola drinks?")) {
						option.setCode(new Integer(1));
					}
					else if (option.getName().equals("Cigarettes?")) {
						option.setCode(new Integer(2));
					}
					else if (option.getName().equals("Sleeping tablets or sedatives?")) {
						option.setCode(new Integer(3));
					}
					else if (option.getName().equals("Painkillers?")) {
						option.setCode(new Integer(4));
					}
					else if (option.getName().equals("Marijuana, cannabis, or hash?")) {
						option.setCode(new Integer(5));
					}
					else if (option.getName().equals("Drugs you sniff, like petrol/glue?")) {
						option.setCode(new Integer(6));
					}
					else if (option.getName().equals("Drugs like LSD?")) {
						option.setCode(new Integer(7));
					}
					else if (option.getName().equals("Speed, ecstasy, crack or cocaine?")) {
						option.setCode(new Integer(8));
					}
					else if (option.getName().equals("Heroin, morphine or methadone?")) {
						option.setCode(new Integer(9));
					}
					else if (option.getName().equals("Other")) {
						option.setCode(new Integer(10));
					}
				}
			}
		}
		System.out.println("Edited "+counter+" entries out of 16");
	}

	private void updateNPHR(Document doc) {
		int counter = 0;
		for (Entry entry: doc.getEntries()) {
			if ("Was the patient admitted as an inpatient to a non psychiatric hospital during the period under observation?".equals(entry.getDisplayText())) {
				counter++;
				OptionEntry oe = (OptionEntry)entry;
				for (Option option: oe.getOptions()) {
					if (option.getName().equals("No")) {
						option.setCode(new Integer(2));
					}
					else if (option.getName().equals("Yes")) {
						option.setCode(new Integer(1));
					}
				}
			}
			else if ("Has the patient had any screens or evaluations to test for other health problems (e.g. cardiac or gastrointestinal problems)?".equals(entry.getDisplayText())) {
				counter++;
				OptionEntry oe = (OptionEntry)entry;
				for (Option option: oe.getOptions()) {
					if (option.getName().equals("No")) {
						option.setCode(new Integer(2));
					}
					else if (option.getName().equals("Yes")) {
						option.setCode(new Integer(1));
					}
				}
			}
			else if ("Did the patient attend any A&E departments during the period under observation?".equals(entry.getDisplayText())) {
				counter++;
				OptionEntry oe = (OptionEntry)entry;
				for (Option option: oe.getOptions()) {
					if (option.getName().equals("No")) {
						option.setCode(new Integer(2));
					}
					else if (option.getName().equals("Yes")) {
						option.setCode(new Integer(1));
					}
				}
			}
			else if ("Was the patient admitted as an inpatient".equals(entry.getDisplayText())) {
				counter++;
				OptionEntry oe = (OptionEntry)entry;
				for (Option option: oe.getOptions()) {
					if (option.getName().equals("No")) {
						option.setCode(new Integer(2));
					}
					else if (option.getName().equals("Yes")) {
						option.setCode(new Integer(1));
					}
				}
			}
			else if ("Did the patient have any outpatient consultations or domiciliary visits during the period under observation?".equals(entry.getDisplayText())) {
				counter++;
				OptionEntry oe = (OptionEntry)entry;
				for (Option option: oe.getOptions()) {
					if (option.getName().equals("No")) {
						option.setCode(new Integer(2));
					}
					else if (option.getName().equals("Yes")) {
						option.setCode(new Integer(1));
					}
				}
			}
		}
		System.out.println("Edited "+counter+" entries out of 5");
	}

	private void updatePHR(Document doc) {
		for (Entry entry: doc.getEntries()) {
			if ("NHS Outpatient Attendance".equals(entry.getName())) {
				System.out.println("Edited NHS Outpatient Attendance");
				OptionEntry oe = (OptionEntry)entry;
				for (Option option: oe.getOptions()) {
					if (option.getName().equals("No")) {
						option.setCode(new Integer(2));
					}
					else if (option.getName().equals("Yes")) {
						option.setCode(new Integer(1));
					}
				}
			}
			else if ("Other Outpatient Attendance".equals(entry.getName())) {
				System.out.println("Edited Other Outpatient Attendance");
				OptionEntry oe = (OptionEntry)entry;
				for (Option option: oe.getOptions()) {
					if (option.getName().equals("No")) {
						option.setCode(new Integer(2));
					}
					else if (option.getName().equals("Yes")) {
						option.setCode(new Integer(1));
					}
				}
			}
		}
	}

	private void updateEPQv3(Document doc) {
		for (Entry entry: doc.getEntries()) {

			if (entry instanceof CompositeEntry) {
				for (Entry e: ((CompositeEntry)entry).getEntries()) {
					if ("Inpatient service option".equals(e.getName())) {
						System.out.println("Edited Inpatient service option");
						OptionEntry oe = (OptionEntry)e;
						for (Option option: oe.getOptions()) {
							if (option.getName().equals("Acute psychiatric ward")) {
								option.setCode(new Integer(1));
							}
							else if (option.getName().equals("Psychiatric rehabilitation ward")) {
								option.setCode(new Integer(2));
							}
							else if (option.getName().equals("Long-stay psychiatric ward")) {
								option.setCode(new Integer(3));
							}
							else if (option.getName().equals("Emergency / crisis centre")) {
								option.setCode(new Integer(4));
							}
							else if (option.getName().equals("General medical ward")) {
								option.setCode(new Integer(5));
							}
							else if (option.getName().equals("Drug treatment ward")) {
								option.setCode(new Integer(6));
							}
							else if (option.getName().equals("Alcohol tretament ward")) {
								option.setCode(new Integer(7));
							}
							else if (option.getName().equals("Other")) {
								option.setCode(new Integer(8));
							}
						}
					}
					else if ("Outpatient service option".equals(e.getName())) {
						System.out.println("Edited "+e.getName());
						OptionEntry oe = (OptionEntry)e;
						for (Option option: oe.getOptions()) {
							if (option.getName().equals("Psychiatric")) {
								option.setCode(new Integer(1));
							}
							else if (option.getName().equals("Hospital alcohol service")) {
								option.setCode(new Integer(2));
							}
							else if (option.getName().equals("Hospital substance use service")) {
								option.setCode(new Integer(3));
							}
							else if (option.getName().equals("Non-psychiatric")) {
								option.setCode(new Integer(4));
							}
							else if (option.getName().equals("Accident and Emergency")) {
								option.setCode(new Integer(5));
							}
							else if (option.getName().equals("Day hospital")) {
								option.setCode(new Integer(6));
							}
							else if (option.getName().equals("Other")) {
								option.setCode(new Integer(7));
							}
						}
					}
					else if ("Community Day Services Option".equals(e.getName())) {
						System.out.println("Edited "+e.getName());
						OptionEntry oe = (OptionEntry)e;
						for (Option option: oe.getOptions()) {
							if (option.getName().equals("GP, surgery visit ")) {
								option.setCode(new Integer(1));
							}
							else if (option.getName().equals("GP, home visit ")) {
								option.setCode(new Integer(2));
							}
							else if (option.getName().equals("Psychiatrist ")) {
								option.setCode(new Integer(3));
							}
							else if (option.getName().equals("Psychologist")) {
								option.setCode(new Integer(4));
							}
							else if (option.getName().equals("Alcohol treatment or rehab")) {
								option.setCode(new Integer(5));
							}
							else if (option.getName().equals("Drug treatment or rehab")) {
								option.setCode(new Integer(6));
							}
							else if (option.getName().equals("District Nurse")) {
								option.setCode(new Integer(7));
							}
							else if (option.getName().equals("Community psychiatric nurse")) {
								option.setCode(new Integer(8));
							}
							else if (option.getName().equals("Social worker")) {
								option.setCode(new Integer(9));
							}
							else if (option.getName().equals("Occupational therapist")) {
								option.setCode(new Integer(10));
							}
							else if (option.getName().equals("Voluntary counsellor")) {
								option.setCode(new Integer(11));
							}
							else if (option.getName().equals("Home help / care worker")) {
								option.setCode(new Integer(12));
							}
							else if (option.getName().equals("Other")) {
								option.setCode(new Integer(13));
							}
						}
					}
					else if ("Category of offence".equals(e.getDisplayText())) {
						System.out.println("Edited "+e.getDisplayText());
						OptionEntry oe = (OptionEntry)e;
						for (Option option: oe.getOptions()) {
							if (option.getName().equals("Homocide/Manslaughter")) {
								option.setCode(new Integer(1));
							}
							else if (option.getName().equals("Serious assault leading to major injury")) {
								option.setCode(new Integer(2));
							}
							else if (option.getName().equals("Less serious assault leading to minor or no injury")) {
								option.setCode(new Integer(3));
							}
							else if (option.getName().equals("Sexual offences")) {
								option.setCode(new Integer(11));
							}
							else if (option.getName().equals("Robbery")) {
								option.setCode(new Integer(4));
							}
							else if (option.getName().equals("Burlgary")) {
								option.setCode(new Integer(5));
							}
							else if (option.getName().equals("Theft and handling stolen goods")) {
								option.setCode(new Integer(6));
							}
							else if (option.getName().equals("Fraud and forgery")) {
								option.setCode(new Integer(7));
							}
							else if (option.getName().equals("Criminal damage")) {
								option.setCode(new Integer(8));
							}
							else if (option.getName().equals("Drug offences")) {
								option.setCode(new Integer(9));
							}
							else if (option.getName().equals("Other")) {
								option.setCode(new Integer(10));
							}
						}
					}
				}
			}
			if ("QC7".equals(entry.getName())) {
				System.out.println("Edited "+entry.getName());
				OptionEntry oe = (OptionEntry)entry;
				for (Option option: oe.getOptions()) {
					if (option.getDisplayText().equals("No (Go to C.11)")) {
						option.setCode(new Integer(2));
					}
					else if (option.getDisplayText().equals("Yes (ask C.8)")) {
						option.setCode(new Integer(1));
					}
				}
			}
			else if ("QC17".equals(entry.getName())) {
				System.out.println("Edited "+entry.getName());
				OptionEntry oe = (OptionEntry)entry;
				for (Option option: oe.getOptions()) {
					if (option.getName().equals("No")) {
						option.setCode(new Integer(2));
					}
					else if (option.getName().equals("Yes")) {
						option.setCode(new Integer(1));
					}
				}
			}
			else if ("Thinking of the last 3 months, have you been looking for any kind of education/course?".equals(entry.getDisplayText())) {
				System.out.println("Edited "+entry.getDisplayText());
				OptionEntry oe = (OptionEntry)entry;
				for (Option option: oe.getOptions()) {
					if (option.getName().equals("No")) {
						option.setCode(new Integer(2));
					}
					else if (option.getName().equals("Yes")) {
						option.setCode(new Integer(1));
					}
				}
			}
		}
	}
}

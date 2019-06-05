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

package org.psygrid.edie.control.patches.v1_6_4;

import org.psygrid.data.model.hibernate.CompositeEntry;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.data.model.hibernate.Option;
import org.psygrid.data.model.hibernate.OptionEntry;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch10 extends AbstractPatch {

	public String getName() {
		return "Add missing option codes to EDIE2Control documents";
	}

	public void applyPatch(DataSet ds, String saml) throws Exception {
		for (Document doc: ((DataSet)ds).getDocuments()) {
			if (doc.getDisplayText().equals("Economic Patient Questionnaire v3")) {
				updateEPQv3(doc);
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

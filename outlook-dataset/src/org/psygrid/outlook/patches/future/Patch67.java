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

package org.psygrid.outlook.patches.future;

import org.psygrid.data.model.hibernate.CompositeEntry;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.data.model.hibernate.Option;
import org.psygrid.data.model.hibernate.OptionEntry;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch67 extends AbstractPatch {

	public String getName() {
		return "Add missing codes to National Eden dataset";
	}

	public void applyPatch(DataSet ds, String saml) throws Exception {
		for (Document doc: ((DataSet)ds).getDocuments()) {
			if (doc.getDisplayText().equals("Baseline Audit Form (Information obtained from case notes)")) {
				updateBaselineAuditForm(doc);
			}
			else if (doc.getDisplayText().equals("Interview and consent information form")) {
				updateInterviewForm(doc);
			}
			else if (doc.getDisplayText().equals("Personal Details Form")) {
				updatePersonalDetailsForm(doc);
			}
			else if (doc.getDisplayText().equals("Drug Check")) {
				updateDrugCheck(doc);
			}
			else if (doc.getDisplayText().equals("Pathways to Care - Collated")) {
				updatePathwaysToCare(doc);
			}
			else if (doc.getDisplayText().equals("Health Questionnaire - EQ-5D Record")) {
				updateEQ5D(doc);
			}
			else if (doc.getDisplayText().equals("Client Sociodemographic and Service Receipt Inventory (CSSRI - EU)")) {
				updateCSSRI(doc);
			}
			else if (doc.getDisplayText().equals("Time Use Interview Score Sheet")) {
				updateTimeUse(doc);
			}
			else if (doc.getDisplayText().equals("Client Sociodemographic and Service Receipt Inventory (CSSRI - EU) - Follow Up")) {
				updateCSSRIFollowUp(doc);
			}
		}
	}

	private void updateBaselineAuditForm(Document doc) {
		int counter = 0;
		for (Entry entry: doc.getEntries()) {
			if (entry instanceof CompositeEntry) {
				CompositeEntry ce = (CompositeEntry)entry;
				for (Entry e: ce.getEntries()) {
					if ("Drug".equals(e.getDisplayText())) {
						System.out.println("Drug");
						counter++;
						OptionEntry glucose = (OptionEntry)e;
						for (Option option: glucose.getOptions()) {
							if (option.getDisplayText().equals("Cannabis")) {
								option.setCode(new Integer(0));
							}
							else if (option.getDisplayText().equals("Amphetamines")) {
								option.setCode(new Integer(1));
							}
							else if (option.getDisplayText().equals("Ecstasy (MDMA)")) {
								option.setCode(new Integer(2));
							}
							else if (option.getDisplayText().equals("LSD")) {
								option.setCode(new Integer(3));
							}
							else if (option.getDisplayText().equals("Hallucinogenic Mushrooms")) {
								option.setCode(new Integer(4));
							}
							else if (option.getDisplayText().equals("Cocaine/Crack")) {
								option.setCode(new Integer(5));
							}
							else if (option.getDisplayText().equals("Heroin/Opiates")) {
								option.setCode(new Integer(6));
							}
							else if (option.getDisplayText().equals("Amyl/Butyl Nitrates")) {
								option.setCode(new Integer(7));
							}
							else if (option.getDisplayText().equals("Solvents")) {
								option.setCode(new Integer(8));
							}
							else if (option.getDisplayText().equals("Khat")) {
								option.setCode(new Integer(9));
							}
							else if (option.getDisplayText().equals("Ketamine")) {
								option.setCode(new Integer(10));
							}
							else if (option.getDisplayText().equals("GHB")) {
								option.setCode(new Integer(11));
							}
							else if (option.getDisplayText().equals("Barbituates")) {
								option.setCode(new Integer(12));
							}
							else if (option.getDisplayText().equals("Over Counter Medication")) {
								option.setCode(new Integer(13));
							}
							else if (option.getDisplayText().equals("Benzodiazepines")) {
								option.setCode(new Integer(14));
							}
							else if (option.getDisplayText().equals("Other Drugs Specify")) {
								option.setCode(new Integer(15));
							}
						}
					}
				}
			}
		}
		System.out.println("Edited "+counter+" entries out of 1");
	}

	private void updateInterviewForm(Document doc) {
		int counter = 0;
		for (Entry entry: doc.getEntries()) {
			if ("Reasons".equals(entry.getName())) {
				System.out.println("Reasons");
				counter++;
				OptionEntry glucose = (OptionEntry)entry;
				for (Option option: glucose.getOptions()) {
					if (option.getDisplayText().equals("Clinically unable to consent")) {
						option.setCode(new Integer(0));
					}
					else if (option.getDisplayText().equals("Un-contactable")) {
						option.setCode(new Integer(1));
					}
					else if (option.getDisplayText().equals("English not spoken")) {
						option.setCode(new Integer(2));
					}
					else if (option.getDisplayText().equals("Team advised to wait")) {
						option.setCode(new Integer(3));
					}
					else if (option.getDisplayText().equals("Undecided")) {
						option.setCode(new Integer(4));
					}
					else if (option.getDisplayText().equals("Declined consent")) {
						option.setCode(new Integer(5));
					}
					else if (option.getDisplayText().equals("Other reason (please specify in box below)")) {
						option.setCode(new Integer(6));
					}
				}
			}
		}
		System.out.println("Edited "+counter+" entries out of 1");
	}

	private void updatePersonalDetailsForm(Document doc) {
		int counter = 0;
		for (Entry entry: doc.getEntries()) {
			if ("Has the patient had previous antipsychotic medication?".equals(entry.getDisplayText())) {
				System.out.println("Has the patient had previous antipsychotic medication?");
				counter++;
				OptionEntry glucose = (OptionEntry)entry;
				for (Option option: glucose.getOptions()) {
					if (option.getDisplayText().equals("No")) {
						option.setCode(new Integer(0));
					}
					else if (option.getDisplayText().equals("Yes")) {
						option.setCode(new Integer(1));
					}
				}
			}
			else if ("Is the patient currently receiving medication?".equals(entry.getDisplayText())) {
				System.out.println("Is the patient currently receiving medication?");
				counter++;
				OptionEntry glucose = (OptionEntry)entry;
				for (Option option: glucose.getOptions()) {
					if (option.getDisplayText().equals("No")) {
						option.setCode(new Integer(0));
					}
					else if (option.getDisplayText().equals("Yes")) {
						option.setCode(new Integer(1));
					}
				}
			}
		}
		System.out.println("Edited "+counter+" entries out of 2");
	}

	private void updateDrugCheck(Document doc) {
		int counter = 0;
		for (Entry entry: doc.getEntries()) {
			if ("Had sedatives".equals(entry.getName())) {
				System.out.println("Had sedatives");
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
			} else if ("Had marijuana".equals(entry.getName())) {
				System.out.println("Had marijuana");
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
			} else if ("Had drugs you sniff".equals(entry.getName())) {
				System.out.println("Had drugs you sniff");
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
			} else if ("Drugs you sniff type".equals(entry.getName())) {
				System.out.println("Drugs you sniff type");
				counter++;
				OptionEntry glucose = (OptionEntry)entry;
				for (Option option: glucose.getOptions()) {
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
			} else if ("Had LSD".equals(entry.getName())) {
				System.out.println("Had LSD");
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
			} else if ("Had speed".equals(entry.getName())) {
				System.out.println("Had speed");
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
			}else if ("Speed type".equals(entry.getName())) {
				System.out.println("Speed type");
				counter++;
				OptionEntry glucose = (OptionEntry)entry;
				for (Option option: glucose.getOptions()) {
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
			}else if ("Had heroin".equals(entry.getName())) {
				System.out.println("Had heroin");
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
			}else if ("Heroin type".equals(entry.getName())) {
				System.out.println("Heroin type");
				counter++;
				OptionEntry glucose = (OptionEntry)entry;
				for (Option option: glucose.getOptions()) {
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
			}else if ("Other Drugs".equals(entry.getName())) {
				System.out.println("Other Drugs");
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
			}else if ("Drug that caused most problems".equals(entry.getName())) {
				System.out.println("Drug that caused most problems");
				counter++;
				OptionEntry glucose = (OptionEntry)entry;
				for (Option option: glucose.getOptions()) {
					if (option.getName().equals("Sleeping tablets or sedatives?")) {
						option.setCode(new Integer(0));
					}
					else if (option.getName().equals("Marijuana, cannabis, or hash?")) {
						option.setCode(new Integer(1));
					}
					else if (option.getName().equals("Drugs you sniff, like petrol/glue?")) {
						option.setCode(new Integer(2));
					}
					else if (option.getName().equals("Drugs like LSD?")) {
						option.setCode(new Integer(3));
					}
					else if (option.getName().equals("Speed, ecstasy, crack or cocaine?")) {
						option.setCode(new Integer(4));
					}
					else if (option.getName().equals("Heroin, morphine or methadone?")) {
						option.setCode(new Integer(5));
					}
					else if (option.getName().equals("Other")) {
						option.setCode(new Integer(6));
					}
				}
			}
		}
		System.out.println("Edited "+counter+" entries out of 11");
	}

	private void updatePathwaysToCare(Document doc) {
		int counter = 0;
		for (Entry entry: doc.getEntries()) {
			if ("Does the client have any history of previous care by any mental health service?".equals(entry.getDisplayText())) {
				System.out.println("Does the client have any history of previous care by any mental health service?");
				counter++;
				OptionEntry glucose = (OptionEntry)entry;
				for (Option option: glucose.getOptions()) {
					if (option.getDisplayText().equals("No")) {
						option.setCode(new Integer(0));
					}
					else if (option.getDisplayText().equals("Yes")) {
						option.setCode(new Integer(1));
					}
				}
			}
		}
		System.out.println("Edited "+counter+" entries out of 1");
	}

	private void updateCSSRI(Document doc) {
		//Subset of Outlook
		int counter = 0;
		for (Entry entry: doc.getEntries()) {
			if ("Type of income".equals(entry.getName())) {
				System.out.println("Type of income");
				counter++;
				OptionEntry glucose = (OptionEntry)entry;
				for (Option option: glucose.getOptions()) {
					if (option.getName().equals("Gross income")) {
						option.setCode(new Integer(1));
					}
					else if (option.getName().equals("Net income")) {
						option.setCode(new Integer(2));
					}
				}
			}
			else if (entry instanceof CompositeEntry) {
				CompositeEntry ce = (CompositeEntry)entry;
				for (Entry e: ce.getEntries()) {
					if ("Inpatient service option".equals(e.getName())) {
						System.out.println("Inpatient service option");
						counter++;
						OptionEntry glucose = (OptionEntry)e;
						for (Option option: glucose.getOptions()) {
							if (option.getName().equals("Acute psychiatric ward")) {
								option.setCode(new Integer(0));
							}
							else if (option.getName().equals("Psychiatric rehabilitation ward")) {
								option.setCode(new Integer(1));
							}
							else if (option.getName().equals("Long-stay ward")) {
								option.setCode(new Integer(2));
							}
							else if (option.getName().equals("Emergency / crisis centre")) {
								option.setCode(new Integer(3));
							}
							else if (option.getName().equals("General medical ward")) {
								option.setCode(new Integer(4));
							}
							else if (option.getName().equals("Other")) {
								option.setCode(new Integer(5));
							}
						}
					} else if ("Outpatient service option".equals(e.getName())) {
						System.out.println("Outpatient service option");
						counter++;
						OptionEntry glucose = (OptionEntry)e;
						for (Option option: glucose.getOptions()) {
							if (option.getName().equals("Accident and Emergency department")) {
								option.setCode(new Integer(0));
							}
							else if (option.getName().equals("Psychiatric outpatient visit")) {
								option.setCode(new Integer(1));
							}
							else if (option.getName().equals("Other hospital outpatient visit")) {
								option.setCode(new Integer(2));
							}
							else if (option.getName().equals("Day hospital")) {
								option.setCode(new Integer(3));
							}
							else if (option.getName().equals("Other")) {
								option.setCode(new Integer(4));
							}
						}
					} else if ("Community Day Services Option".equals(e.getName())) {
						System.out.println("Community Day Services Option");
						counter++;
						OptionEntry glucose = (OptionEntry)e;
						for (Option option: glucose.getOptions()) {
							if (option.getName().equals("Community mental health centre")) {
								option.setCode(new Integer(0));
							}
							else if (option.getName().equals("Day care centre")) {
								option.setCode(new Integer(1));
							}
							else if (option.getName().equals("Group therapy")) {
								option.setCode(new Integer(2));
							}
							else if (option.getName().equals("Sheltered workshop")) {
								option.setCode(new Integer(3));
							}
							else if (option.getName().equals("Specialist education")) {
								option.setCode(new Integer(4));
							}
							else if (option.getName().equals("Other")) {
								option.setCode(new Integer(5));
							}
						}
					} else if ("Community Care Contacts Service option".equals(e.getName())) {
						System.out.println("Community Care Contacts Service option");
						counter++;
						OptionEntry glucose = (OptionEntry)e;
						for (Option option: glucose.getOptions()) {
							if (option.getName().equals("Psychiatrist")) {
								option.setCode(new Integer(0));
							}
							else if (option.getName().equals("Psychologist")) {
								option.setCode(new Integer(1));
							}
							else if (option.getName().equals("GP / Primary care physician")) {
								option.setCode(new Integer(2));
							}
							else if (option.getName().equals("Counsellor")) {
								option.setCode(new Integer(3));
							}
							else if (option.getName().equals("District nurse")) {
								option.setCode(new Integer(4));
							}
							else if (option.getName().equals("Community psychiatric nurse")) {
								option.setCode(new Integer(5));
							}
							else if (option.getName().equals("Social worker")) {
								option.setCode(new Integer(6));
							}
							else if (option.getName().equals("Occupational therapist")) {
								option.setCode(new Integer(7));
							}
							else if (option.getName().equals("Home help / care worker")) {
								option.setCode(new Integer(8));
							}
							else if (option.getName().equals("Other")) {
								option.setCode(new Integer(9));
							}
						}
					} else if ("Type of help".equals(e.getDisplayText())) {
						System.out.println("Type of help");
						counter++;
						OptionEntry glucose = (OptionEntry)e;
						for (Option option: glucose.getOptions()) {
							if (option.getName().equals("Child Care")) {
								option.setCode(new Integer(0));
							}
							else if (option.getName().equals("Personal care")) {
								option.setCode(new Integer(1));
							}
							else if (option.getName().equals("Help in/ around the house")) {
								option.setCode(new Integer(2));
							}
							else if (option.getName().equals("Help outside the home")) {
								option.setCode(new Integer(3));
							}
							else if (option.getName().equals("Other")) {
								option.setCode(new Integer(4));
							}
						}
					} else if ("Who contributes option".equals(e.getName())) {
						System.out.println("Who contributes option");
						counter++;
						OptionEntry glucose = (OptionEntry)e;
						for (Option option: glucose.getOptions()) {
							if (option.getName().equals("National government")) {
								option.setCode(new Integer(0));
							}
							else if (option.getName().equals("Local government")) {
								option.setCode(new Integer(1));
							}
							else if (option.getName().equals("Voluntary organisation/charity")) {
								option.setCode(new Integer(2));
							}
							else if (option.getName().equals("Private organisation/company")) {
								option.setCode(new Integer(3));
							}
							else if (option.getName().equals("Private individual")) {
								option.setCode(new Integer(4));
							}
						}
					}
				}
			} else if ("Enter details".equals(entry.getName())) {
				System.out.println("Enter details");
				counter++;
				OptionEntry glucose = (OptionEntry)entry;
				for (Option option: glucose.getOptions()) {
					if (option.getDisplayText().equals("No")) {
						option.setCode(new Integer(0));
					}
					else if (option.getDisplayText().equals("Yes")) {
						option.setCode(new Integer(1));
					}
				}
			}
		}
		System.out.println("Edited "+counter+" entries out of 8");
	}

	private void updateCSSRIFollowUp(Document doc) {
		//From Outlook
		int counter = 0;
		for (Entry entry: doc.getEntries()) {
			if ("Type of income".equals(entry.getName())) {
				System.out.println("Type of income");
				counter++;
				OptionEntry glucose = (OptionEntry)entry;
				for (Option option: glucose.getOptions()) {
					if (option.getName().equals("Gross income")) {
						option.setCode(new Integer(1));
					}
					else if (option.getName().equals("Net income")) {
						option.setCode(new Integer(2));
					}
				}
			} else if (entry instanceof CompositeEntry) {
				CompositeEntry ce = (CompositeEntry)entry;
				for (Entry e: ce.getEntries()) {
					if ("Unemployed reason options".equals(e.getName())) {
						System.out.println("Unemployed reason options");
						counter++;
						OptionEntry glucose = (OptionEntry)e;
						for (Option option: glucose.getOptions()) {
							if (option.getDisplayText().equals("Mental Illness")) {
								option.setCode(new Integer(0));
							}
							else if (option.getDisplayText().equals("Physical Illness or Disability")) {
								option.setCode(new Integer(1));
							}
							else if (option.getDisplayText().equals("General Employment Situation")) {
								option.setCode(new Integer(2));
							}
							else if (option.getDisplayText().equals("Redundancy")) {
								option.setCode(new Integer(3));
							}
							else if (option.getDisplayText().equals("Other (please specify)")) {
								option.setCode(new Integer(4));
							}
						}
					} else if ("Inpatient service option".equals(e.getName())) {
						System.out.println("Inpatient service option");
						counter++;
						OptionEntry glucose = (OptionEntry)e;
						for (Option option: glucose.getOptions()) {
							if (option.getName().equals("Acute psychiatric ward")) {
								option.setCode(new Integer(0));
							}
							else if (option.getName().equals("Psychiatric rehabilitation ward")) {
								option.setCode(new Integer(1));
							}
							else if (option.getName().equals("Long-stay ward")) {
								option.setCode(new Integer(2));
							}
							else if (option.getName().equals("Emergency / crisis centre")) {
								option.setCode(new Integer(3));
							}
							else if (option.getName().equals("General medical ward")) {
								option.setCode(new Integer(4));
							}
							else if (option.getName().equals("Other")) {
								option.setCode(new Integer(5));
							}
						}
					} else if ("Outpatient service option".equals(e.getName())) {
						System.out.println("Outpatient service option");
						counter++;
						OptionEntry glucose = (OptionEntry)e;
						for (Option option: glucose.getOptions()) {
							if (option.getName().equals("Accident and Emergency department")) {
								option.setCode(new Integer(0));
							}
							else if (option.getName().equals("Psychiatric outpatient visit")) {
								option.setCode(new Integer(1));
							}
							else if (option.getName().equals("Other hospital outpatient visit")) {
								option.setCode(new Integer(2));
							}
							else if (option.getName().equals("Day hospital")) {
								option.setCode(new Integer(3));
							}
							else if (option.getName().equals("Other")) {
								option.setCode(new Integer(4));
							}
						}
					} else if ("Community Day Services Option".equals(e.getName())) {
						System.out.println("Community Day Services Option");
						counter++;
						OptionEntry glucose = (OptionEntry)e;
						for (Option option: glucose.getOptions()) {
							if (option.getName().equals("Community mental health centre")) {
								option.setCode(new Integer(0));
							}
							else if (option.getName().equals("Day care centre")) {
								option.setCode(new Integer(1));
							}
							else if (option.getName().equals("Group therapy")) {
								option.setCode(new Integer(2));
							}
							else if (option.getName().equals("Sheltered workshop")) {
								option.setCode(new Integer(3));
							}
							else if (option.getName().equals("Specialist education")) {
								option.setCode(new Integer(4));
							}
							else if (option.getName().equals("Other")) {
								option.setCode(new Integer(5));
							}
						}
					} else if ("Community Care Contacts Service option".equals(e.getName())) {
						System.out.println("Community Care Contacts Service option");
						counter++;
						OptionEntry glucose = (OptionEntry)e;
						for (Option option: glucose.getOptions()) {
							if (option.getName().equals("Psychiatrist")) {
								option.setCode(new Integer(0));
							}
							else if (option.getName().equals("Psychologist")) {
								option.setCode(new Integer(1));
							}
							else if (option.getName().equals("GP / Primary care physician")) {
								option.setCode(new Integer(2));
							}
							else if (option.getName().equals("Counsellor")) {
								option.setCode(new Integer(3));
							}
							else if (option.getName().equals("District nurse")) {
								option.setCode(new Integer(4));
							}
							else if (option.getName().equals("Community psychiatric nurse")) {
								option.setCode(new Integer(5));
							}
							else if (option.getName().equals("Social worker")) {
								option.setCode(new Integer(6));
							}
							else if (option.getName().equals("Occupational therapist")) {
								option.setCode(new Integer(7));
							}
							else if (option.getName().equals("Home help / care worker")) {
								option.setCode(new Integer(8));
							}
							else if (option.getName().equals("Other")) {
								option.setCode(new Integer(9));
							}
						}
					} else if ("Type of help".equals(e.getDisplayText())) {
						System.out.println("Type of help");
						counter++;
						OptionEntry glucose = (OptionEntry)e;
						for (Option option: glucose.getOptions()) {
							if (option.getName().equals("Child Care")) {
								option.setCode(new Integer(0));
							}
							else if (option.getName().equals("Personal care")) {
								option.setCode(new Integer(1));
							}
							else if (option.getName().equals("Help in/ around the house")) {
								option.setCode(new Integer(2));
							}
							else if (option.getName().equals("Help outside the home")) {
								option.setCode(new Integer(3));
							}
							else if (option.getName().equals("Other")) {
								option.setCode(new Integer(4));
							}
						}
					} else if ("Who contributes option".equals(e.getName())) {
						System.out.println("Who contributes option");
						counter++;
						OptionEntry glucose = (OptionEntry)e;
						for (Option option: glucose.getOptions()) {
							if (option.getName().equals("National government")) {
								option.setCode(new Integer(0));
							}
							else if (option.getName().equals("Local government")) {
								option.setCode(new Integer(1));
							}
							else if (option.getName().equals("Voluntary organisation/charity")) {
								option.setCode(new Integer(2));
							}
							else if (option.getName().equals("Private organisation/company")) {
								option.setCode(new Integer(3));
							}
							else if (option.getName().equals("Private individual")) {
								option.setCode(new Integer(4));
							}
						}
					}
				}
			} else if ("Enter details".equals(entry.getName())) {
				System.out.println("Enter details");
				counter++;
				OptionEntry glucose = (OptionEntry)entry;
				for (Option option: glucose.getOptions()) {
					if (option.getDisplayText().equals("No")) {
						option.setCode(new Integer(0));
					}
					else if (option.getDisplayText().equals("Yes")) {
						option.setCode(new Integer(1));
					}
				}
			}
		}
		System.out.println("Edited "+counter+" entries out of 9");
	}

	private void updateTimeUse(Document doc) {
		int counter = 0;
		for (Entry entry: doc.getEntries()) {
			if ("Paid Work".equals(entry.getName())) {
				System.out.println("Paid Work");
				counter++;
				OptionEntry glucose = (OptionEntry)entry;
				for (Option option: glucose.getOptions()) {
					if (option.getDisplayText().equals("Present")) {
						option.setCode(new Integer(1));
					}
					else if (option.getDisplayText().equals("Absent")) {
						option.setCode(new Integer(2));
					}
				}
			} else if ("Active Search Work".equals(entry.getName())) {
				System.out.println("Active Search Work");
				counter++;
				OptionEntry glucose = (OptionEntry)entry;
				for (Option option: glucose.getOptions()) {
					if (option.getDisplayText().equals("Present")) {
						option.setCode(new Integer(1));
					}
					else if (option.getDisplayText().equals("Absent")) {
						option.setCode(new Integer(2));
					}
				}
			} else if ("Paid Work Ever".equals(entry.getName())) {
				System.out.println("Paid Work Ever");
				counter++;
				OptionEntry glucose = (OptionEntry)entry;
				for (Option option: glucose.getOptions()) {
					if (option.getDisplayText().equals("Present")) {
						option.setCode(new Integer(1));
					}
					else if (option.getDisplayText().equals("Absent")) {
						option.setCode(new Integer(2));
					}
				}
			} else if ("Current education".equals(entry.getName())) {
				System.out.println("Current education");
				counter++;
				OptionEntry glucose = (OptionEntry)entry;
				for (Option option: glucose.getOptions()) {
					if (option.getDisplayText().equals("Present")) {
						option.setCode(new Integer(1));
					}
					else if (option.getDisplayText().equals("Absent")) {
						option.setCode(new Integer(2));
					}
				}
			} else if ("Active Search Edu".equals(entry.getName())) {
				System.out.println("Active Search Edu");
				counter++;
				OptionEntry glucose = (OptionEntry)entry;
				for (Option option: glucose.getOptions()) {
					if (option.getDisplayText().equals("Present")) {
						option.setCode(new Integer(1));
					}
					else if (option.getDisplayText().equals("Absent")) {
						option.setCode(new Integer(2));
					}
				}
			} else if ("Voluntary Work".equals(entry.getName())) {
				System.out.println("Voluntary Work");
				counter++;
				OptionEntry glucose = (OptionEntry)entry;
				for (Option option: glucose.getOptions()) {
					if (option.getDisplayText().equals("Present")) {
						option.setCode(new Integer(1));
					}
					else if (option.getDisplayText().equals("Absent")) {
						option.setCode(new Integer(2));
					}
				}
			} else if ("Leisure".equals(entry.getName())) {
				System.out.println("Leisure");
				counter++;
				OptionEntry glucose = (OptionEntry)entry;
				for (Option option: glucose.getOptions()) {
					if (option.getDisplayText().equals("Present")) {
						option.setCode(new Integer(1));
					}
					else if (option.getDisplayText().equals("Absent")) {
						option.setCode(new Integer(2));
					}
				}
			} else if ("Sport".equals(entry.getName())) {
				System.out.println("Sport");
				counter++;
				OptionEntry glucose = (OptionEntry)entry;
				for (Option option: glucose.getOptions()) {
					if (option.getDisplayText().equals("Present")) {
						option.setCode(new Integer(1));
					}
					else if (option.getDisplayText().equals("Absent")) {
						option.setCode(new Integer(2));
					}
				}
			} else if ("Hobbies".equals(entry.getName())) {
				System.out.println("Hobbies");
				counter++;
				OptionEntry glucose = (OptionEntry)entry;
				for (Option option: glucose.getOptions()) {
					if (option.getDisplayText().equals("Present")) {
						option.setCode(new Integer(1));
					}
					else if (option.getDisplayText().equals("Absent")) {
						option.setCode(new Integer(2));
					}
				}
			} else if ("Childcare".equals(entry.getName())) {
				System.out.println("Childcare");
				counter++;
				OptionEntry glucose = (OptionEntry)entry;
				for (Option option: glucose.getOptions()) {
					if (option.getDisplayText().equals("Applicable")) {
						option.setCode(new Integer(1));
					}
					else if (option.getDisplayText().equals("Non-applicable")) {
						option.setCode(new Integer(2));
					}
				}
			}
		}
		System.out.println("Edited "+counter+" entries out of 10");
	}

	private void updateEQ5D(Document doc) {
		int counter = 0;
		for (Entry entry: doc.getEntries()) {
			if ("...in you yourself?".equals(entry.getDisplayText())) {
				System.out.println("...in you yourself?");
				counter++;
				OptionEntry glucose = (OptionEntry)entry;
				for (Option option: glucose.getOptions()) {
					if (option.getDisplayText().equals("Yes")) {
						option.setCode(new Integer(1));
					}
					else if (option.getDisplayText().equals("No")) {
						option.setCode(new Integer(0));
					}
				}
			}
			else if ("...in your family?".equals(entry.getDisplayText())) {
				System.out.println("...in your family?");
				counter++;
				OptionEntry glucose = (OptionEntry)entry;
				for (Option option: glucose.getOptions()) {
					if (option.getDisplayText().equals("Yes")) {
						option.setCode(new Integer(1));
					}
					else if (option.getDisplayText().equals("No")) {
						option.setCode(new Integer(0));
					}
				}
			}
			else if ("...in caring for others?".equals(entry.getDisplayText())) {
				System.out.println("...in caring for others?");
				counter++;
				OptionEntry glucose = (OptionEntry)entry;
				for (Option option: glucose.getOptions()) {
					if (option.getDisplayText().equals("Yes")) {
						option.setCode(new Integer(1));
					}
					else if (option.getDisplayText().equals("No")) {
						option.setCode(new Integer(0));
					}
				}
			}
			else if ("Sex".equals(entry.getName())) {
				System.out.println("Sex");
				counter++;
				OptionEntry glucose = (OptionEntry)entry;
				for (Option option: glucose.getOptions()) {
					if (option.getDisplayText().equals("Male")) {
						option.setCode(new Integer(0));
					}
					else if (option.getDisplayText().equals("Female")) {
						option.setCode(new Integer(1));
					}
				}
			}
			else if ("Smoking".equals(entry.getName())) {
				System.out.println("Smoking");
				counter++;
				OptionEntry glucose = (OptionEntry)entry;
				for (Option option: glucose.getOptions()) {
					if (option.getDisplayText().equals("A current smoker")) {
						option.setCode(new Integer(0));
					}
					else if (option.getDisplayText().equals("An ex-smoker")) {
						option.setCode(new Integer(1));
					}
					else if (option.getDisplayText().equals("A never smoker")) {
						option.setCode(new Integer(2));
					}
				}
			}
			else if ("Do you now, or did you ever, work in health or social services?".equals(entry.getDisplayText())) {
				System.out.println("Do you now, or did you ever, work in health or social services?");
				counter++;
				OptionEntry glucose = (OptionEntry)entry;
				for (Option option: glucose.getOptions()) {
					if (option.getDisplayText().equals("Yes")) {
						option.setCode(new Integer(1));
					}
					else if (option.getDisplayText().equals("No")) {
						option.setCode(new Integer(0));
					}
				}
			}
			else if ("Which of the following best describes your main activity?".equals(entry.getDisplayText())) {
				System.out.println("Which of the following best describes your main activity?");
				counter++;
				OptionEntry glucose = (OptionEntry)entry;
				for (Option option: glucose.getOptions()) {
					if (option.getDisplayText().equals("Employed/Self-employed")) {
						option.setCode(new Integer(0));
					}
					else if (option.getDisplayText().equals("Retired")) {
						option.setCode(new Integer(1));
					}
					else if (option.getDisplayText().equals("Housework")) {
						option.setCode(new Integer(2));
					}
					else if (option.getDisplayText().equals("Student")) {
						option.setCode(new Integer(3));
					}
					else if (option.getDisplayText().equals("Seeking work")) {
						option.setCode(new Integer(4));
					}
					else if (option.getDisplayText().equals("Other (please specify)")) {
						option.setCode(new Integer(5));
					}
				}
			}
			else if ("Did your education continue after the minimum school leaving age?".equals(entry.getDisplayText())) {
				System.out.println("Did your education continue after the minimum school leaving age?");
				counter++;
				OptionEntry glucose = (OptionEntry)entry;
				for (Option option: glucose.getOptions()) {
					if (option.getDisplayText().equals("Yes")) {
						option.setCode(new Integer(1));
					}
					else if (option.getDisplayText().equals("No")) {
						option.setCode(new Integer(0));
					}
				}
			}
			else if ("Do you have a Degree or equivalent professional qualification?".equals(entry.getDisplayText())) {
				System.out.println("Do you have a Degree or equivalent professional qualification?");
				counter++;
				OptionEntry glucose = (OptionEntry)entry;
				for (Option option: glucose.getOptions()) {
					if (option.getDisplayText().equals("Yes")) {
						option.setCode(new Integer(1));
					}
					else if (option.getDisplayText().equals("No")) {
						option.setCode(new Integer(0));
					}
				}
			}
		}
		System.out.println("Edited "+counter+" entries out of 9");
	}
}

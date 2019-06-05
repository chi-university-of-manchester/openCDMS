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


//Created on Nov 10, 2005 by John Ainsworth

package org.psygrid.security;

import java.util.ArrayList;
import java.util.List;

import org.psygrid.security.RBACAction;
import org.psygrid.security.RBACRole;
import org.psygrid.security.RBACTarget;
import org.psygrid.security.attributeauthority.model.hibernate.Group;
import org.psygrid.www.xml.security.core.types.ActionType;
import org.psygrid.www.xml.security.core.types.ArgumentType;
import org.psygrid.www.xml.security.core.types.CompositeRuleType;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.OperatorType;
import org.psygrid.www.xml.security.core.types.PrivilegeType;
import org.psygrid.www.xml.security.core.types.RoleType;
import org.psygrid.www.xml.security.core.types.StatementType;

/**
 * @author jda
 *
 */
public class DefaultPolicy {
	public static List<StatementType> buildStatements(GroupType[] groups){

		List<StatementType> lst = new ArrayList<StatementType>();

		///Begin AA/PA
		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
					new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_AA_GET_USERS_AND_PRIVILEGES_IN_PROJECT.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
					new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_AA_ADD_USER.toActionType(), rule);

			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
					new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_AA_LDAP_QUERY.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
					new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_AA_RESET_PASSWORD.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
					new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_AA_GET_USER.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
					new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_AA_MODIFY_USER.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
					new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_AA_MODIFY_PROJECT.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
					new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_AA_DELETE_PROJECT.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
					new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_PA_MODIFY_POLICY.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
					new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_PA_GET_POLICY.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
					new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_PA_DELETE_POLICY.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		///End AA/PA


		///Begin ESL
		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
					new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_ESL_SAVE_PROJECT.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}	
		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
					new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_ESL_RETRIEVE_ALL_PROJECTS.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}	
		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
					new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
					new ArgumentType(RBACRole.TreatmentAdministrator.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_ESL_RETRIEVE_PROJECT.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}	
		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
					new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
					new ArgumentType(RBACRole.TreatmentAdministrator.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_ESL_RETRIEVE_PROJECT_BY_CODE.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}	
		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
					new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_ESL_EMERGENCY_BREAK_IN.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}	
		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
					new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_ESL_LOOKUP_RANDOMIZER_STATISTICS.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
					new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_ESL_LOOKUP_STRATIFIED_RANDOMIZER_STATISTICS.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.And,
								null,
								new ArgumentType[] {
										new ArgumentType(new PrivilegeType(
												null, groups[g]), true),
												new ArgumentType(
														RBACRole.ClinicalResearchOfficer.toPrivilegeType(),
														true) }) },
														new ArgumentType[] { new ArgumentType(
																RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.ACTION_ESL_SAVE_SUBJECT.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.And,
								null,
								new ArgumentType[] {
										new ArgumentType(new PrivilegeType(
												null, groups[g]), true),
												new ArgumentType(
														RBACRole.ClinicalResearchOfficer.toPrivilegeType(),
														true) }) },
														new ArgumentType[] { new ArgumentType(
																RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.ACTION_ESL_RANDOMISE_SUBJECT.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}


		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.And,
								new CompositeRuleType[] { new CompositeRuleType(
										OperatorType.Or,
										null,
										new ArgumentType[] {
												new ArgumentType(
														RBACRole.TreatmentAdministrator.toPrivilegeType(), true),
														new ArgumentType(
																RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true) }), },
																new ArgumentType[] { new ArgumentType(
																		new PrivilegeType(null, groups[g]),
																		true) }), },
																		new ArgumentType[] { new ArgumentType(
																				RBACRole.ClinicalResearchManager.toPrivilegeType(),
																				true) });
				StatementType st = new StatementType(Group.fromGroupType(
						groups[g]).toTargetType(),
						RBACAction.ACTION_ESL_RETRIEVE_SUBJECT.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}


		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
					new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
					new ArgumentType(RBACRole.TreatmentAdministrator.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true)
			});
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_ESL_FIND_SUBJECT_BY_EXAMPLE.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.And,
								new CompositeRuleType[] { new CompositeRuleType(
										OperatorType.Or,
										null,
										new ArgumentType[] {
												new ArgumentType(
														RBACRole.TreatmentAdministrator.toPrivilegeType(), true),
														new ArgumentType(
																RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true) }), },
																new ArgumentType[] { new ArgumentType(
																		new PrivilegeType(null, groups[g]),
																		true) }), },
																		new ArgumentType[] { new ArgumentType(
																				RBACRole.ClinicalResearchManager.toPrivilegeType(),
																				true) });
				StatementType st = new StatementType(Group.fromGroupType(
						groups[g]).toTargetType(),
						RBACAction.ACTION_ESL_RETRIEVE_SUBJECT_BY_STUDY_NUMBER.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}


		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
					new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
					new ArgumentType(RBACRole.TreatmentAdministrator.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true)
			});
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_ESL_LOOKUP_STUDY_NUMBER.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.And,
								null,
								new ArgumentType[] {
										new ArgumentType(new PrivilegeType(
												null, groups[g]), true),
												new ArgumentType(
														RBACRole.TreatmentAdministrator.toPrivilegeType(),
														true) }) },
														new ArgumentType[] { new ArgumentType(
																RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.ACTION_ESL_LOOKUP_RANDOMISATION_RESULT.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
					new ArgumentType(RBACRole.ANY.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_ESL_EXISTS.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		///End ESL

		///Begin Randomizer

		try {
			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.Or,
					null,
					new ArgumentType[] { new ArgumentType(
							RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
							new ArgumentType(
									RBACRole.TreatmentAdministrator.toPrivilegeType(), true)});
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_RS_GET_ALLOCATION.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
					new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_RS_CHECK_INTEGRITY.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
					new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true)});
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_RS_SAVE_RANDOMIZER.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.Or,
					null,
					new ArgumentType[] { new ArgumentType(
							RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
							new ArgumentType(
									RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true)});
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_RS_ALLOCATE.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
					new ArgumentType(RBACRole.ANY.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_RS_GET_RANDOMIZER_STATISTICS.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
					new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_RS_GET_ALLOCATIONS.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		
		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
					new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true),
			});
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_RS_DELETE_RANDOMIZATION.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println("Build statements " + e.getMessage());
		}

		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
					new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true),
			});
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_RS_GET_RANDOMIZER.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println("Build statements " + e.getMessage());
		}
		
		///End Randomiser

		///Begin Web IF
		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.And,
								null,
								new ArgumentType[] {
										new ArgumentType(new PrivilegeType(
												null, groups[g]), true),
												new ArgumentType(
														RBACRole.TreatmentAdministrator.toPrivilegeType(),
														true) }) },
														new ArgumentType[] { new ArgumentType(
																RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.ACTION_ESLW_LOOKUP_RANDOMIZATION_RESULT.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.And,
								new CompositeRuleType[] { new CompositeRuleType(
										OperatorType.Or,
										null,
										new ArgumentType[] {
												new ArgumentType(
														RBACRole.TreatmentAdministrator.toPrivilegeType(), true),
														new ArgumentType(
																RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true) }), },
																new ArgumentType[] { new ArgumentType(
																		new PrivilegeType(null, groups[g]),
																		true) }), }, 
																		new ArgumentType[] {
								new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true)});
				StatementType st = new StatementType(Group.fromGroupType(
						groups[g]).toTargetType(), RBACAction.ACTION_ESLW_LOOKUP_STUDY_NUMBER.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}


		///End Web

		///Begin repository

		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.And,
								new CompositeRuleType[] { new CompositeRuleType(
										OperatorType.Or,
										null,
											new ArgumentType[] {
												new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(),true) ,
												new ArgumentType(RBACRole.TreatmentAdministrator.toPrivilegeType(),true) }), },
											new ArgumentType[] {
												new ArgumentType(new PrivilegeType(null, groups[g]), true) }), },				
											new ArgumentType[] { 
												new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.ACTION_DR_CHANGE_RECORD_STATUS.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
					new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_DR_MARK_RESPONSE_AS_VALID.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
					new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_DR_MARK_RESPONSE_AS_INVALID.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.And,
					null,
					new ArgumentType[] { new ArgumentType(
							RBACRole.ANY.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_DR_GET_DATASET_COMPLETE.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		try {
			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.And,
					null,
					new ArgumentType[] { new ArgumentType(
							RBACRole.ANY.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_DR_GET_DATASET_SUMMARY.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
					new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_DR_SAVE_DATASET.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
					new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_DR_SAVE_DATASET.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
					new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true)});
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_DR_ADD_IDENTIFIER.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
					new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true)});
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_DR_SAVE_RECORD_AS_USER.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
					new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_DR_PUBLISH_DATASET.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
					new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true)});
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_DR_PATCH_DATASET.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
					new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_DR_SAVE_REPORT.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.And,
								new CompositeRuleType[] { new CompositeRuleType(
										OperatorType.Or,
										null,
										new ArgumentType[] {
												new ArgumentType(
														RBACRole.PrincipalInvestigator.toPrivilegeType(), true),
														new ArgumentType(
																RBACRole.TreatmentAdministrator.toPrivilegeType(), true),
																new ArgumentType(
																		RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true) }), },
																		new ArgumentType[] { new ArgumentType(
																				new PrivilegeType(null, groups[g]),
																				true) }), }, 
																				new ArgumentType[] {
								new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
								new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
								new ArgumentType(
										RBACRole.ProjectManager.toPrivilegeType(), true),
										new ArgumentType(RBACRole.DataAnalyst.toPrivilegeType(), true)  });
				StatementType st = new StatementType(Group.fromGroupType(
						groups[g]).toTargetType(), RBACAction.ACTION_DR_GET_BINARY_DATA.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		try {
			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.And,
					null,
					new ArgumentType[] { new ArgumentType(
							RBACRole.ANY.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_DR_GET_STANDARD_CODES.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
					new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true)});
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_DCC_AUTO_GENERATE_IDENTIFIERS.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.Or,
					null,
					new ArgumentType[] { new ArgumentType(
							RBACRole.ClinicalResearchManager.toPrivilegeType(),
							true) });
			StatementType st = new StatementType(
					RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_DR_GET_RECORDS_BY_STATUS.toActionType(),
					rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
					new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true)});
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_DR_GET_RECORDS.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.And,
								new CompositeRuleType[] { new CompositeRuleType(
										OperatorType.Or,
										null,
											new ArgumentType[] {
												new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(),true) ,
												new ArgumentType(RBACRole.TreatmentAdministrator.toPrivilegeType(),true) }), },
											new ArgumentType[] {
												new ArgumentType(new PrivilegeType(null, groups[g]), true) }), },				
											new ArgumentType[] { 
												new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.getRecordsDocumentsByStatus_Incomplete.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		try {
			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.Or,
					null,
					new ArgumentType[] { new ArgumentType(
							RBACRole.ClinicalResearchManager.toPrivilegeType(),
							true) });
			StatementType st = new StatementType(
					RBACTarget.ANY.toTargetType(),
					RBACAction.getRecordsDocumentsByStatus_Pending.toActionType(),
					rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.Or,
					null,
					new ArgumentType[] { new ArgumentType(
							RBACRole.ClinicalResearchManager.toPrivilegeType(),
							true) });
			StatementType st = new StatementType(
					RBACTarget.ANY.toTargetType(),
					RBACAction.getRecordsDocumentsByStatus_Approved.toActionType(),
					rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.And,
								new CompositeRuleType[] { new CompositeRuleType(
										OperatorType.Or,
										null,
											new ArgumentType[] {
												new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(),true) ,
												new ArgumentType(RBACRole.TreatmentAdministrator.toPrivilegeType(),true) }), },
											new ArgumentType[] {
												new ArgumentType(new PrivilegeType(null, groups[g]), true) }), },				
											new ArgumentType[] { 
												new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.getRecordsDocumentsByStatus_Rejected.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.And,
								new CompositeRuleType[] { new CompositeRuleType(
										OperatorType.Or,
										null,
										new ArgumentType[] {
												new ArgumentType(
														RBACRole.PrincipalInvestigator.toPrivilegeType(), true),
														new ArgumentType(RBACRole.TreatmentAdministrator.toPrivilegeType(), true),
														new ArgumentType(
																RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true) }), },
																new ArgumentType[] { new ArgumentType(
																		new PrivilegeType(null, groups[g]),
																		true) }), }, 
																		new ArgumentType[] {
								new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
								new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
								new ArgumentType(
										RBACRole.ProjectManager.toPrivilegeType(), true),
										new ArgumentType(RBACRole.DataAnalyst.toPrivilegeType(), true)  });
				StatementType st = new StatementType(Group.fromGroupType(
						groups[g]).toTargetType(), RBACAction.ACTION_DR_GET_RECORD_COMPLETE.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}	
		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.And,
								new CompositeRuleType[] { new CompositeRuleType(
										OperatorType.Or,
										null,
										new ArgumentType[] {
												new ArgumentType(
														RBACRole.PrincipalInvestigator.toPrivilegeType(), true),
														new ArgumentType(RBACRole.TreatmentAdministrator.toPrivilegeType(), true),
														new ArgumentType(
																RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true) }), },
																new ArgumentType[] { new ArgumentType(
																		new PrivilegeType(null, groups[g]),
																		true) }), }, 
																		new ArgumentType[] {
								new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
								new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
								new ArgumentType(
										RBACRole.ProjectManager.toPrivilegeType(), true),
										new ArgumentType(RBACRole.DataAnalyst.toPrivilegeType(), true)  });
				StatementType st = new StatementType(Group.fromGroupType(
						groups[g]).toTargetType(), 
						RBACAction.ACTION_DR_GET_RECORD_SUMMARY.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}	
		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.And,
								new CompositeRuleType[] { new CompositeRuleType(
										OperatorType.Or,
										null,
										new ArgumentType[] {
												new ArgumentType(
														RBACRole.PrincipalInvestigator.toPrivilegeType(), true),
														new ArgumentType(
																RBACRole.TreatmentAdministrator.toPrivilegeType(), true),
																new ArgumentType(
																		RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true) }), },
																		new ArgumentType[] { new ArgumentType(
																				new PrivilegeType(null, groups[g]),
																				true) }), }, 
																				new ArgumentType[] {
								new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
								new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
								new ArgumentType(
										RBACRole.ProjectManager.toPrivilegeType(), true),
										new ArgumentType(RBACRole.DataAnalyst.toPrivilegeType(), true) });
				StatementType st = new StatementType(Group.fromGroupType(
						groups[g]).toTargetType(), RBACAction.ACTION_DR_GET_RECORD_SINGLE_DOCUMENT.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.And,
								new CompositeRuleType[] { new CompositeRuleType(
										OperatorType.Or,
										null,
										new ArgumentType[] {
												new ArgumentType(
														RBACRole.PrincipalInvestigator.toPrivilegeType(), true),
														new ArgumentType(
																RBACRole.TreatmentAdministrator.toPrivilegeType(), true),
																new ArgumentType(
																		RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true) }), },
																		new ArgumentType[] { new ArgumentType(
																				new PrivilegeType(null, groups[g]),
																				true) }), }, 
																				new ArgumentType[] {
								new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
								new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
								new ArgumentType(
										RBACRole.ProjectManager.toPrivilegeType(), true),	
										new ArgumentType(
												RBACRole.DataAnalyst.toPrivilegeType(),
												true) });
				StatementType st = new StatementType(Group.fromGroupType(
						groups[g]).toTargetType(), 
						RBACAction.ACTION_DR_GET_RECORDS_WITH_CONSENT_BY_GROUPS.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.And,
								new CompositeRuleType[] { new CompositeRuleType(
										OperatorType.Or,
										null,
										new ArgumentType[] {
												new ArgumentType(
														RBACRole.PrincipalInvestigator.toPrivilegeType(), true),
														new ArgumentType(
																RBACRole.TreatmentAdministrator.toPrivilegeType(), true),
																new ArgumentType(
																		RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true) }), },
																		new ArgumentType[] { new ArgumentType(
																				new PrivilegeType(null, groups[g]),
																				true) }), }, 
																				new ArgumentType[] {
								new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
								new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
								new ArgumentType(
										RBACRole.ProjectManager.toPrivilegeType(), true),	
										new ArgumentType(
												RBACRole.DataAnalyst.toPrivilegeType(),
												true) });
				StatementType st = new StatementType(Group.fromGroupType(
						groups[g]).toTargetType(), RBACAction.ACTION_DR_GET_RECORDS_BY_GROUPS.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.And,
								new CompositeRuleType[] { new CompositeRuleType(
										OperatorType.Or,
										null,
											new ArgumentType[] {
												new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(),true) ,
												new ArgumentType(RBACRole.TreatmentAdministrator.toPrivilegeType(),true) }), },
											new ArgumentType[] {
												new ArgumentType(new PrivilegeType(null, groups[g]), true) }), },				
											new ArgumentType[] { 
												new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.getRecordsByGroupsAndDocStatus_Incomplete.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		try {
			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.Or,
					null,
					new ArgumentType[] { new ArgumentType(
							RBACRole.ClinicalResearchManager.toPrivilegeType(),
							true) });
			StatementType st = new StatementType(
					RBACTarget.ANY.toTargetType(),
					RBACAction.getRecordsByGroupsAndDocStatus_Pending.toActionType(),
					rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.Or,
					null,
					new ArgumentType[] { new ArgumentType(
							RBACRole.ClinicalResearchManager.toPrivilegeType(),
							true) });
			StatementType st = new StatementType(
					RBACTarget.ANY.toTargetType(),
					RBACAction.getRecordsByGroupsAndDocStatus_Approved.toActionType(),
					rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.And,
								new CompositeRuleType[] { new CompositeRuleType(
										OperatorType.Or,
										null,
											new ArgumentType[] {
												new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(),true) ,
												new ArgumentType(RBACRole.TreatmentAdministrator.toPrivilegeType(),true) }), },
											new ArgumentType[] {
												new ArgumentType(new PrivilegeType(null, groups[g]), true) }), },				
											new ArgumentType[] { 
												new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.getRecordsByGroupsAndDocStatus_Rejected.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}


		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.And,
								null,
								new ArgumentType[] {
										new ArgumentType(new PrivilegeType(
												null, groups[g]), true),
												new ArgumentType(
														RBACRole.ClinicalResearchOfficer.toPrivilegeType(),
														true) }) },
														new ArgumentType[] { new ArgumentType(
																RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.ACTION_DR_GENERATE_IDENTIFIERS.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.And,
								null,
								new ArgumentType[] {
										new ArgumentType(new PrivilegeType(null, groups[g]), true),
										new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true) }), 
						
						new CompositeRuleType(
								OperatorType.And,
								null,
								new ArgumentType[] {
										new ArgumentType(new PrivilegeType(null, groups[g]), true),
										new ArgumentType(RBACRole.TreatmentAdministrator.toPrivilegeType(), true) })  },
								new ArgumentType[] { new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
													 new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true)});
				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.ACTION_DR_SAVE_RECORD.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.And,
								null,
								new ArgumentType[] {
										new ArgumentType(new PrivilegeType(
												null, groups[g]), true),
												new ArgumentType(
														RBACRole.ClinicalResearchOfficer.toPrivilegeType(),
														true) }) },
														new ArgumentType[] { new ArgumentType(
																RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.ACTION_DR_WITHDRAW_CONSENT.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.And,
								null,
								new ArgumentType[] {
										new ArgumentType(new PrivilegeType(
												null, groups[g]), true),
												new ArgumentType(
														RBACRole.ClinicalResearchOfficer.toPrivilegeType(),
														true) }) },
														new ArgumentType[] { new ArgumentType(
																RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.ACTION_DR_ADD_CONSENT.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}


		/*
		 * 
		 * Begin document
		 * 
		 */

		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.And,
								new CompositeRuleType[] { new CompositeRuleType(
										OperatorType.Or,
										null,
											new ArgumentType[] {
												new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(),true) ,
												new ArgumentType(RBACRole.TreatmentAdministrator.toPrivilegeType(),true) }), },
											new ArgumentType[] {
												new ArgumentType(new PrivilegeType(null, groups[g]), true) }), },				
											new ArgumentType[] { 
												new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.changeStatus_D_Incomplete_Pending.toActionType(),
						rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			
			
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.And,
								new CompositeRuleType[] { new CompositeRuleType(
										OperatorType.Or,
										null,
											new ArgumentType[] {
												new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(),true) ,
												new ArgumentType(RBACRole.TreatmentAdministrator.toPrivilegeType(),true) }), },
											new ArgumentType[] {
												new ArgumentType(new PrivilegeType(null, groups[g]), true) }), },				
											new ArgumentType[] { 
												new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.changeStatus_D_Pending_Incomplete.toActionType(),
						rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.And,
								new CompositeRuleType[] { new CompositeRuleType(
										OperatorType.Or,
										null,
											new ArgumentType[] {
												new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(),true) ,
												new ArgumentType(RBACRole.TreatmentAdministrator.toPrivilegeType(),true) }), },
											new ArgumentType[] {
												new ArgumentType(new PrivilegeType(null, groups[g]), true) }), },				
											new ArgumentType[] { 
												new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.changeStatus_D_Rejected_Pending.toActionType(),
						rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}


		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
					new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.changeStatus_D_Pending_Approved.toActionType(), 
					rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}			
		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
					new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.changeStatus_D_Pending_Rejected.toActionType(),
					rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}			
		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
					new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.changeStatus_D_Approved_Pending.toActionType(),
					rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}


		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.And,
								new CompositeRuleType[] { new CompositeRuleType(
										OperatorType.Or,
										null,
											new ArgumentType[] {
												new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(),true) ,
												new ArgumentType(RBACRole.TreatmentAdministrator.toPrivilegeType(),true) }), },
											new ArgumentType[] {
												new ArgumentType(new PrivilegeType(null, groups[g]), true) }), },				
											new ArgumentType[] { 
												new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.changeDocumentStatus_Incomplete_Pending.toActionType(),
						rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.And,
								new CompositeRuleType[] { new CompositeRuleType(
										OperatorType.Or,
										null,
											new ArgumentType[] {
												new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(),true) ,
												new ArgumentType(RBACRole.TreatmentAdministrator.toPrivilegeType(),true) }), },
											new ArgumentType[] {
												new ArgumentType(new PrivilegeType(null, groups[g]), true) }), },				
											new ArgumentType[] { 
												new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.changeDocumentStatus_Pending_Incomplete.toActionType(),
						rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.And,
								new CompositeRuleType[] { new CompositeRuleType(
										OperatorType.Or,
										null,
											new ArgumentType[] {
												new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(),true) ,
												new ArgumentType(RBACRole.TreatmentAdministrator.toPrivilegeType(),true) }), },
											new ArgumentType[] {
												new ArgumentType(new PrivilegeType(null, groups[g]), true) }), },				
											new ArgumentType[] { 
												new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.changeDocumentStatus_Rejected_Pending.toActionType(),
						rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}


		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
					new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.changeDocumentStatus_Pending_Approved.toActionType(), 
					rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}           
		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
					new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.changeDocumentStatus_Pending_Rejected.toActionType(),
					rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}           
		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
					new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.changeDocumentStatus_Approved_Pending.toActionType(),
					rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.And,
								new CompositeRuleType[] { new CompositeRuleType(
										OperatorType.Or,
										null,
										new ArgumentType[] {
												new ArgumentType(
														RBACRole.PrincipalInvestigator.toPrivilegeType(), true),
														new ArgumentType(
																RBACRole.TreatmentAdministrator.toPrivilegeType(), true),
																new ArgumentType(
																		RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true) }), },
																		new ArgumentType[] { new ArgumentType(
																				new PrivilegeType(null, groups[g]),
																				true) }), }, 
																				new ArgumentType[] {
								new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
								new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
								new ArgumentType(
										RBACRole.ProjectManager.toPrivilegeType(), true),
										new ArgumentType(RBACRole.DataAnalyst.toPrivilegeType(), true)  });
				StatementType st = new StatementType(Group.fromGroupType(
						groups[g]).toTargetType(), RBACAction.ACTION_DR_GENERATE_REPORT.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		try {
			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.And,
					null,
					new ArgumentType[] { new ArgumentType(
							RBACRole.ANY.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_DR_GET_REPORTS_BY_DATASET.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		//Only permit project manager 
		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
					new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_DR_GET_ALL_REPORTS_BY_DATASET.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		//Only permit project manager 
		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
					new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_DR_DELETE_REPORT.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}


		//trend report
		try {
			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.Or,
					null,
					new ArgumentType[] {								
							new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
							new ArgumentType(RBACRole.PrincipalInvestigator.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ProjectAdministrator.toPrivilegeType(), true),	
							new ArgumentType(RBACRole.DataAnalyst.toPrivilegeType(), true), 
							new ArgumentType(RBACRole.RecruitmentManager.toPrivilegeType(), true)
					});

			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_DR_GENERATE_TREND_REPORT.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		//dynamic management report
		try {

			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.Or,
					null,
					new ArgumentType[] {								
							new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
							new ArgumentType(RBACRole.PrincipalInvestigator.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ProjectAdministrator.toPrivilegeType(), true),	
							new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true),	
							new ArgumentType(RBACRole.DataAnalyst.toPrivilegeType(), true), 
							new ArgumentType(RBACRole.RecruitmentManager.toPrivilegeType(), true)
					});

			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_DR_GENERATE_DYNAMIC_REPORT.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		//dynamic trends report
		try {

			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.Or,
					null,
					new ArgumentType[] {								
							new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
							new ArgumentType(RBACRole.PrincipalInvestigator.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ProjectAdministrator.toPrivilegeType(), true),	
							new ArgumentType(RBACRole.DataAnalyst.toPrivilegeType(), true), 
							new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true), 
							new ArgumentType(RBACRole.RecruitmentManager.toPrivilegeType(), true)
					});

			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_DR_GENERATE_DYNAMIC_TRENDS_REPORT.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		//dynamic record report
		try {

			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.Or,
					null,
					new ArgumentType[] {								
							new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
							new ArgumentType(RBACRole.PrincipalInvestigator.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ProjectAdministrator.toPrivilegeType(), true),	
							new ArgumentType(RBACRole.DataAnalyst.toPrivilegeType(), true), 
							new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true), 
							new ArgumentType(RBACRole.RecruitmentManager.toPrivilegeType(), true)
					});

			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_DR_GENERATE_DYNAMIC_RECORD_REPORT.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		//management report
		try {

			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.Or,
					null,
					new ArgumentType[] {								
							new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ProjectAdministrator.toPrivilegeType(), true),	
							new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true),
							new ArgumentType(RBACRole.PrincipalInvestigator.toPrivilegeType(), true)
					});

			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_DR_GENERATE_MANAGEMENT_REPORT.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}


		/*
		 * Rules for individual reports. Used to specify who is emailed the reports.
		 * 
		 * Currently tends to mirror the roles that receive the reports, but could be made more specific
		 * to individual reports in future.
		 */

		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.And,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.Or,
								null,
								new ArgumentType[] {
										new ArgumentType(RBACRole.NamedInvestigator.toPrivilegeType(), true),
										new ArgumentType(RBACRole.PrincipalInvestigator.toPrivilegeType(), true)
								}) },
								new ArgumentType[] {
									new ArgumentType(new PrivilegeType(null, groups[g]), true),
									
								});

				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.ACTION_DR_INVESTIGATOR_REPORT.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		try {
			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.Or,
					null,
					new ArgumentType[] {
							new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
							new ArgumentType(RBACRole.RecruitmentManager.toPrivilegeType(), true),
					}) ;

			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_DR_UKCRN_REPORT.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.And,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.Or,
								null,
								new ArgumentType[] {
										new ArgumentType(RBACRole.RecruitmentManager.toPrivilegeType(), true)
								}) },
							new ArgumentType[] {
								new ArgumentType(new PrivilegeType(null, groups[g]), true)
							});

				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.ACTION_DR_RECRUITMENT_REPORT.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		
		try {
			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.And,
					null,
					new ArgumentType[] {
							new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
					});

			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_DR_CHIEF_INVESTIGATOR_REPORT.toActionType(), rule);
			
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.And,
						null,
						new ArgumentType[] {									
								new ArgumentType(new PrivilegeType(null, groups[g]), true),
								new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true)
						});

				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.ACTION_DR_CRM_REPORT.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		
		//General policy for viewing management reports through psygrid-web
		try {
			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.Or,
					null,
					new ArgumentType[] {								
							new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
							new ArgumentType(RBACRole.PrincipalInvestigator.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ProjectAdministrator.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true),
							new ArgumentType(RBACRole.DataAnalyst.toPrivilegeType(), true), 
							new ArgumentType(RBACRole.RecruitmentManager.toPrivilegeType(), true)
					});

			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_DR_VIEW_MGMT_REPORT.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		//Specific policy for viewing Document and Record status reports through psygrid-web
		try {
			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.Or,
					null,
					new ArgumentType[] {
							new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
							new ArgumentType(RBACRole.PrincipalInvestigator.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ProjectAdministrator.toPrivilegeType(), true),	
							new ArgumentType(RBACRole.DataAnalyst.toPrivilegeType(), true), 
							new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true),
							new ArgumentType(RBACRole.RecruitmentManager.toPrivilegeType(), true)
					});
						
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_DR_STATUS_REPORT.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		
		/* ***************************************************** */

		//get reports of type
		try {

			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.Or,
					null,
					new ArgumentType[] {								
							new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
							new ArgumentType(RBACRole.PrincipalInvestigator.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ProjectAdministrator.toPrivilegeType(), true),	
							new ArgumentType(RBACRole.DataAnalyst.toPrivilegeType(), true), 
							new ArgumentType(RBACRole.RecruitmentManager.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true)
					});

			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_DR_GET_REPORTS_OF_TYPE.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		//get report
		try {
			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.Or,
					null,
					new ArgumentType[] {								
							new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
							new ArgumentType(RBACRole.PrincipalInvestigator.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ProjectAdministrator.toPrivilegeType(), true),	
							new ArgumentType(RBACRole.DataAnalyst.toPrivilegeType(), true), 
							new ArgumentType(RBACRole.RecruitmentManager.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true)
					});

			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_DR_GET_REPORT.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		//get groups for codes (reporting function)
		try {

			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.Or,
					null,
					new ArgumentType[] {								
							new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
							new ArgumentType(RBACRole.PrincipalInvestigator.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ProjectAdministrator.toPrivilegeType(), true),	
							new ArgumentType(RBACRole.DataAnalyst.toPrivilegeType(), true), 
							new ArgumentType(RBACRole.RecruitmentManager.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true)
					});

			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_DR_GET_GROUPS_FOR_CODES.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		//generic public randomisation details for a project
		try {
			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.And,
					null,
					new ArgumentType[] { new ArgumentType(
							RBACRole.ANY.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_ESL_IS_PROJECT_RANDOMISED.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println("Build statements " + e.getMessage());
		}

		// Only permit project manager 
		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
					new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_ESL_DELETE_PROJECT.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.And,
					null,
					new ArgumentType[] { new ArgumentType(
							RBACRole.ANY.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_ESL_RETRIEVE_SUBJECT_RANDOMISATION_EVENTS.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			CompositeRuleType rule = new CompositeRuleType(				
					OperatorType.And,
					null,
					new ArgumentType[] { new ArgumentType(
							RBACRole.ANY.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_RS_GET_SUBJECT_RANDOMIZATION_EVENTS.toActionType(), rule);
			lst.add(st);                      
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		// Export
		try {
			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.Or,
					null,
					new ArgumentType[] {
							new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
							new ArgumentType(RBACRole.DataAnalyst.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true),
							new ArgumentType(RBACRole.PrincipalInvestigator.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true)  });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_DR_DOWNLOAD_EXPORT.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.Or,
					null,
					new ArgumentType[] {
							new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
							new ArgumentType(RBACRole.DataAnalyst.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true),
							new ArgumentType(RBACRole.PrincipalInvestigator.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true)});
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_DR_GET_MY_EXPORT_REQUESTS.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { 
							new CompositeRuleType(
								OperatorType.And,
								new CompositeRuleType[] { 
									new CompositeRuleType(
										OperatorType.Or,
										null,
										new ArgumentType[] {
												new ArgumentType(RBACRole.DataAnalyst.toPrivilegeType(), true),
												new ArgumentType(RBACRole.PrincipalInvestigator.toPrivilegeType(), true),
												new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true)}) },
									new ArgumentType[] { 
										new ArgumentType(new PrivilegeType(null, groups[g]), true) }) }, 
						new ArgumentType[] {
								new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
								new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
								new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true) } );
				StatementType st = new StatementType(Group.fromGroupType(
						groups[g]).toTargetType(), RBACAction.ACTION_DR_REQUEST_EXPORT.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		
		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { 
							new CompositeRuleType(
								OperatorType.And,
								new CompositeRuleType[] { 
									new CompositeRuleType(
										OperatorType.Or,
										null,
										new ArgumentType[] {
												new ArgumentType(RBACRole.DataAnalyst.toPrivilegeType(), true),
												new ArgumentType(RBACRole.PrincipalInvestigator.toPrivilegeType(), true)}) },
									new ArgumentType[] { 
										new ArgumentType(new PrivilegeType(null, groups[g]), true) }) }, 
						new ArgumentType[] {
								new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
								new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
								new ArgumentType(RBACRole.QueryData.toPrivilegeType(), true) } );
				StatementType st = new StatementType(Group.fromGroupType(
						groups[g]).toTargetType(), RBACAction.ACTION_DR_EXECUTE_QUERY.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		try {
			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.Or,
					null, 
					new ArgumentType[] {
							new ArgumentType(RBACRole.DataAnalyst.toPrivilegeType(), true),
							new ArgumentType(RBACRole.PrincipalInvestigator.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
							new ArgumentType(RBACRole.QueryData.toPrivilegeType(), true) } );
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(), RBACAction.ACTION_DR_SAVE_QUERY.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.Or,
					null, 
					new ArgumentType[] {
							new ArgumentType(RBACRole.DataAnalyst.toPrivilegeType(), true),
							new ArgumentType(RBACRole.PrincipalInvestigator.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
							new ArgumentType(RBACRole.QueryData.toPrivilegeType(), true) } );
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(), RBACAction.ACTION_DR_GET_MY_QUERIES.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}		

		
		try {
			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.Or,
					null, 
					new ArgumentType[] {
							new ArgumentType(RBACRole.DataAnalyst.toPrivilegeType(), true),
							new ArgumentType(RBACRole.PrincipalInvestigator.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
							new ArgumentType(RBACRole.QueryData.toPrivilegeType(), true) } );
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(), RBACAction.ACTION_DR_GET_QUERY.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}		

	
		
		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { 
							new CompositeRuleType(
								OperatorType.And,
								new CompositeRuleType[] { 
									new CompositeRuleType(
										OperatorType.Or,
										null,
										new ArgumentType[] {
												new ArgumentType(RBACRole.DataAnalyst.toPrivilegeType(), true),
												new ArgumentType(RBACRole.PrincipalInvestigator.toPrivilegeType(), true),
												new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true)}), },
								new ArgumentType[] { 
										new ArgumentType(new PrivilegeType(null, groups[g]), true) }) }, 
						new ArgumentType[] {
								new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
								new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
								new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true) } );
				StatementType st = new StatementType(Group.fromGroupType(
						groups[g]).toTargetType(), new ActionType(
								RBACAction.ACTION_DR_CANCEL_EXPORT.toString(),
								RBACAction.ACTION_DR_CANCEL_EXPORT.idAsString()), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		// Import
		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
					new ArgumentType(RBACRole.DataImporter.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_DR_IMPORT_DATA.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.And,
						null,
						new ArgumentType[] {									
								new ArgumentType(new PrivilegeType(null, groups[g]), true),
								new ArgumentType(RBACRole.DataImporter.toPrivilegeType(), true)
						});

				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.ACTION_DR_IMPORT_DATA.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		
		//get identifiers 
		try {

			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.Or,
					null,
					new ArgumentType[] {						
							new ArgumentType(new PrivilegeType(new RoleType("ClinicalResearchOfficer",null), null), true),
							new ArgumentType(new PrivilegeType(new RoleType("ClinicalResearchManager",null), null), true),
							new ArgumentType(new PrivilegeType(new RoleType("ChiefInvestigator",null), null), true),
							new ArgumentType(new PrivilegeType(new RoleType("PrincipalInvestigator",null), null), true),
							new ArgumentType(new PrivilegeType(new RoleType("ProjectAdministrator",null), null), true),	
							new ArgumentType(new PrivilegeType(new RoleType("DataAnalyst",null),null), true), 
							new ArgumentType(new PrivilegeType(new RoleType("RecruitmentManager",null),	null), true)
					});

			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),							
					RBACAction.ACTION_DR_GET_IDENTIFIERS.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}


		//The following 3 rules are req'd by the receiving treatment chart
		try {
			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.Or,
					null,
					new ArgumentType[] {								
							new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ProjectAdministrator.toPrivilegeType(), true),	
							new ArgumentType(RBACRole.PrincipalInvestigator.toPrivilegeType(), true)
					});

			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_ESL_RETRIEVE_NHS_NUMBERS.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		try {
			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.Or,
					null,
					new ArgumentType[] {								
							new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ProjectAdministrator.toPrivilegeType(), true),	
							new ArgumentType(RBACRole.PrincipalInvestigator.toPrivilegeType(), true)
					});

			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_ESL_LOOKUP_RANDOMISATION_RESULT_FOR_DATE.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		try {
			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.Or,
					null,
					new ArgumentType[] {								
							new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ProjectAdministrator.toPrivilegeType(), true),	
							new ArgumentType(RBACRole.PrincipalInvestigator.toPrivilegeType(), true)
					});

			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_RS_LOOKUP_RANDOMIZATION_RESULT_FOR_DATE.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.Or,
					null,
					new ArgumentType[] { 
							new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
					});
			StatementType st = new StatementType(
					RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_DR_TRANSFORM.toActionType(),
					rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		//get groups for dataset (used by psygrid-web)
		try {
			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.And,
					null,
					new ArgumentType[] { new ArgumentType(
							RBACRole.ANY.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_DR_GET_GROUPS.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.And,
								new CompositeRuleType[] { new CompositeRuleType(
										OperatorType.Or,
										null,
										new ArgumentType[] {
												new ArgumentType(
														RBACRole.PrincipalInvestigator.toPrivilegeType(), true),
														new ArgumentType(
																RBACRole.TreatmentAdministrator.toPrivilegeType(), true),
																new ArgumentType(
																		RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true) }), },
																		new ArgumentType[] { new ArgumentType(
																				new PrivilegeType(null, groups[g]),
																				true) }), }, 
																				new ArgumentType[] {
								new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
								new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
								new ArgumentType(
										RBACRole.ProjectManager.toPrivilegeType(), true),	
										new ArgumentType(
												RBACRole.DataAnalyst.toPrivilegeType(),
												true) });
				StatementType st = new StatementType(Group.fromGroupType(
						groups[g]).toTargetType(), 
						RBACAction.ACTION_DR_GET_DELETED_RECORDS_BY_GROUPS.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		
		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.And,
								null,
								new ArgumentType[] {
										new ArgumentType(new PrivilegeType(
												null, groups[g]), true),
												new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(),true) }) },
								new ArgumentType[] { new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.ACTION_DR_UPDATE_RECORD_METADATA.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		
		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.And,
								null,
								new ArgumentType[] {
										new ArgumentType(new PrivilegeType(
												null, groups[g]), true),
												new ArgumentType(
														RBACRole.ClinicalResearchOfficer.toPrivilegeType(),
														true) }) },
														new ArgumentType[] { new ArgumentType(
																RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.ACTION_DR_SYNC_DOC_STAT_WITH_PRIMARY.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.And,
								null,
								new ArgumentType[] {
										new ArgumentType(new PrivilegeType(
												null, groups[g]), true),
												new ArgumentType(
														RBACRole.ClinicalResearchOfficer.toPrivilegeType(),
														true) }) },
														new ArgumentType[] { new ArgumentType(
																RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.ACTION_DR_UPDATE_PRIMARY_IDENTIFIER.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { 
								new CompositeRuleType(
								OperatorType.And,
								null,
								new ArgumentType[] {
										new ArgumentType(new PrivilegeType(null, groups[g]), true),
										new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true) }), 
						
								new CompositeRuleType(
								OperatorType.And,
								null,
								new ArgumentType[] {
										new ArgumentType(new PrivilegeType(null, groups[g]), true),
										new ArgumentType(RBACRole.DataAnalyst.toPrivilegeType(), true) })  
								},				
						new ArgumentType[] { new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.ACTION_DR_UPDATE_SECONDARY_IDENTIFIER.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.And,
								new CompositeRuleType[] { new CompositeRuleType(
										OperatorType.Or,
										null,
										new ArgumentType[] {
												new ArgumentType(
														RBACRole.PrincipalInvestigator.toPrivilegeType(), true),
														new ArgumentType(
																RBACRole.TreatmentAdministrator.toPrivilegeType(), true),
																new ArgumentType(
																		RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true) }), },
																		new ArgumentType[] { new ArgumentType(
																				new PrivilegeType(null, groups[g]),
																				true) }), }, 
																				new ArgumentType[] {
								new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
								new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
								new ArgumentType(
										RBACRole.ProjectManager.toPrivilegeType(), true),	
										new ArgumentType(
												RBACRole.DataAnalyst.toPrivilegeType(),
												true) });
				StatementType st = new StatementType(Group.fromGroupType(
						groups[g]).toTargetType(), 
						RBACAction.ACTION_DR_GET_LINKABLE_RECORDS.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}


		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.And,
								new CompositeRuleType[] { new CompositeRuleType(
										OperatorType.Or,
										null,
										new ArgumentType[] {
												new ArgumentType(
														RBACRole.PrincipalInvestigator.toPrivilegeType(), true),
														new ArgumentType(
																RBACRole.TreatmentAdministrator.toPrivilegeType(), true),
																new ArgumentType(
																		RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true) }), },
																		new ArgumentType[] { new ArgumentType(
																				new PrivilegeType(null, groups[g]),
																				true) }), }, 
																				new ArgumentType[] {
								new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
								new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
								new ArgumentType(
										RBACRole.ProjectManager.toPrivilegeType(), true),	
										new ArgumentType(
												RBACRole.DataAnalyst.toPrivilegeType(),
												true) });
				StatementType st = new StatementType(Group.fromGroupType(
						groups[g]).toTargetType(), 
						RBACAction.ACTION_DR_GET_LINKED_RECORDS.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}


		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.And,
								new CompositeRuleType[] { new CompositeRuleType(
										OperatorType.Or,
										null,
										new ArgumentType[] {
												new ArgumentType(
														RBACRole.PrincipalInvestigator.toPrivilegeType(), true),
														new ArgumentType(
																RBACRole.TreatmentAdministrator.toPrivilegeType(), true),
																new ArgumentType(
																		RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true) }), },
																		new ArgumentType[] { new ArgumentType(
																				new PrivilegeType(null, groups[g]),
																				true) }), }, 
																				new ArgumentType[] {
								new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
								new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
								new ArgumentType(
										RBACRole.ProjectManager.toPrivilegeType(), true),	
										new ArgumentType(
												RBACRole.DataAnalyst.toPrivilegeType(),
												true) });
				StatementType st = new StatementType(Group.fromGroupType(
						groups[g]).toTargetType(), 
						RBACAction.ACTION_DR_GET_CONSENT_AND_STATUS_INFO_FOR_GROUPS.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		/* Fuction required by psygrid-web for some reports */
		try {
			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.Or,
					null,
					new ArgumentType[] {								
							new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
							new ArgumentType(
									RBACRole.PrincipalInvestigator.toPrivilegeType(), true),
									new ArgumentType(RBACRole.ProjectAdministrator.toPrivilegeType(), true),	
									new ArgumentType(RBACRole.DataAnalyst.toPrivilegeType(), true), 
									new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true),
									new ArgumentType(RBACRole.RecruitmentManager.toPrivilegeType(), true)
					});

			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_DR_GET_DOCUMENTS.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		try {
			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.Or,
					null,
					new ArgumentType[] {								
							new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
							new ArgumentType(
									RBACRole.PrincipalInvestigator.toPrivilegeType(), true),
									new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true),	
									new ArgumentType(RBACRole.ProjectAdministrator.toPrivilegeType(), true),	
									new ArgumentType(RBACRole.DataAnalyst.toPrivilegeType(), true), 
									new ArgumentType(RBACRole.RecruitmentManager.toPrivilegeType(), true)
					});

			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_DR_GET_ENTRIES.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.And,
								null,
								new ArgumentType[] {
										new ArgumentType(new PrivilegeType(null, groups[g]), true),
										new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(),true) }) },
								new ArgumentType[] { 
										new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.ACTION_ESL_LOCK_SUBJECT.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.And,
								new CompositeRuleType[] { new CompositeRuleType(
										OperatorType.Or,
										null,
										new ArgumentType[] {
												new ArgumentType(
														RBACRole.TreatmentAdministrator.toPrivilegeType(), true),
														new ArgumentType(
																RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true) }), },
																new ArgumentType[] { new ArgumentType(
																		new PrivilegeType(null, groups[g]),
																		true) }), },
																		new ArgumentType[] { new ArgumentType(
																				RBACRole.ClinicalResearchManager.toPrivilegeType(),
																				true) });
				StatementType st = new StatementType(Group.fromGroupType(
						groups[g]).toTargetType(),
						RBACAction.ACTION_ESL_GET_PROPERTY.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		/* Permissions for accessing documents from the repository */
		try {
			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.Or,
					null,
					new ArgumentType[] {
								new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true) ,
								new ArgumentType(RBACRole.DataAnalyst.toPrivilegeType(), true) ,
								new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true),
								new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_DR_DOC_STANDARD.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		for (int g = 0; g < groups.length; g++) {	//restrict document instances by groups
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.And,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.Or,
								null,
								new ArgumentType[] {
										new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true),
										new ArgumentType(RBACRole.DataAnalyst.toPrivilegeType(), true) ,
										new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true),
										new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(),true) }) },
										new ArgumentType[] { new ArgumentType(new PrivilegeType(null, groups[g]), true)});
				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.ACTION_DR_DOC_STANDARD_INST.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		/* Only allow the treatment administrator (who is not blinded) to view a document */
		try {
			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.Or,
					null,
					new ArgumentType[] {
							new ArgumentType(RBACRole.DataAnalyst.toPrivilegeType(), true) ,
							new ArgumentType(RBACRole.TreatmentAdministrator.toPrivilegeType(),	true) }
													);
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_DR_DOC_BLIND.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		for (int g = 0; g < groups.length; g++) {	//restrict document instances by group
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.And,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.Or,
								null,
								new ArgumentType[] {
										new ArgumentType(RBACRole.DataAnalyst.toPrivilegeType(), true) ,
										new ArgumentType(RBACRole.TreatmentAdministrator.toPrivilegeType(), true) }) },
										new ArgumentType[] { new ArgumentType(new PrivilegeType(null, groups[g]), true)});
				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.ACTION_DR_DOC_BLIND_INST.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		
		/* Allow both the CRO and treatment administrator to view a document or entry */
		try {
			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.Or,
					null,
					new ArgumentType[] {
							new ArgumentType(RBACRole.TreatmentAdministrator.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true),
							new ArgumentType(RBACRole.DataAnalyst.toPrivilegeType(), true) ,
							new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(),true) }
													);
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_DR_VIEW_TREATMENT.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		for (int g = 0; g < groups.length; g++) {	//restrict document instances by group
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.And,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.Or,
								null,
								new ArgumentType[] {
										new ArgumentType(RBACRole.TreatmentAdministrator.toPrivilegeType(), true),
										new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true),
										new ArgumentType(RBACRole.DataAnalyst.toPrivilegeType(), true) ,
										new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true),
										new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(),true) }) },
								new ArgumentType[] { new ArgumentType(new PrivilegeType(null, groups[g]), true)});
				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.ACTION_DR_VIEW_TREATMENT_INST.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		
		/* Allow both the CRO and treatment administrator to edit a document or entry */
		try {
			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.Or,
					null,
					new ArgumentType[] {
							new ArgumentType(RBACRole.TreatmentAdministrator.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(),true),
							new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true) }
													);
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_DR_EDIT_TREATMENT.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		for (int g = 0; g < groups.length; g++) {	//restrict document instances by group
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.And,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.Or,
								null,
								new ArgumentType[] {
										new ArgumentType(RBACRole.TreatmentAdministrator.toPrivilegeType(), true),
										new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(),true),
										new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true) }) },
								new ArgumentType[] { new ArgumentType(new PrivilegeType(null, groups[g]), true)});
				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.ACTION_DR_EDIT_TREATMENT_INST.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		
		/* Permissions for being able to edit documents from the repository */
		//Any users who can access a document but not edit it will only be able to view the document 
		//and will not be permitted to save it. 	
		try {
			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.Or,
					null,
					new ArgumentType[] {
							new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(),true) } );
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_DR_EDIT_DOC.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		for (int g = 0; g < groups.length; g++) {		//restrict document instances by group
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.And,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.Or,
								null,
								new ArgumentType[] {
										new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true),
										new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(),true) }) },
										new ArgumentType[] { new ArgumentType(new PrivilegeType(null, groups[g]), true)});
				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.ACTION_DR_EDIT_DOC_INST.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		/* Permissions for accessing documents which contain participant identifying data from the repository */
		try {
			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.Or,
					null,
					new ArgumentType[] {
								new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true) ,
								new ArgumentType(RBACRole.ViewIdentity.toPrivilegeType(), true) ,
								new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true),
								new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_DR_DOC_VIEW_IDENTITY.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		for (int g = 0; g < groups.length; g++) {	//restrict document instances by groups
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.And,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.Or,
								null,
								new ArgumentType[] {
										new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true),
										new ArgumentType(RBACRole.ViewIdentity.toPrivilegeType(), true) ,
										new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true),
										new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(),true) }) },
										new ArgumentType[] { new ArgumentType(new PrivilegeType(null, groups[g]), true)});
				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.ACTION_DR_DOC_VIEW_IDENTITY_INST.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		
		
		/* Permissions for being able to edit particular entries (set in an Entry's editableAction) */
		//Any users who is not able to edit the entry will still be able to view it (assuming they have access
		//according to the accessAction of the element)
		try {
			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.Or,
					null,
					new ArgumentType[] {
							new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(),true) } );
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_DR_EDIT_ENTRY.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		for (int g = 0; g < groups.length; g++) {	//restrict access to responses by group
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.And,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.Or,
								null,
								new ArgumentType[] {
										new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true),
										new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(),true) }) },
								new ArgumentType[] { new ArgumentType(new PrivilegeType(null, groups[g]), true)});
				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.ACTION_DR_EDIT_ENTRY_RESPONSE.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		
		
		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
					new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
			StatementType st = new StatementType(
					RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_DR_AUDIT_BY_PROJECT.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
					new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
			StatementType st = new StatementType(
					RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_DR_AUDIT_BY_RECORD.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
					new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
			StatementType st = new StatementType(
					RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_DR_GET_PROVENANCE_FOR_CHANGE.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.And,
								new CompositeRuleType[] { new CompositeRuleType(
										OperatorType.Or,
										null,
										new ArgumentType[] {
												new ArgumentType(
														RBACRole.PrincipalInvestigator.toPrivilegeType(), true),
														new ArgumentType(
																RBACRole.TreatmentAdministrator.toPrivilegeType(), true),
																new ArgumentType(
																		RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true) }), },
																		new ArgumentType[] { new ArgumentType(
																				new PrivilegeType(null, groups[g]),
																				true) }), }, 
																				new ArgumentType[] {
								new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
								new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
								new ArgumentType(
										RBACRole.ProjectManager.toPrivilegeType(), true),
										new ArgumentType(RBACRole.DataAnalyst.toPrivilegeType(), true) });
				StatementType st = new StatementType(Group.fromGroupType(
						groups[g]).toTargetType(), RBACAction.ACTION_DR_GET_STATUS_ID_FOR_DOCUMENT.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		
		//EXPORT POLICIES
		try {
			CompositeRuleType rule = new CompositeRuleType(
							OperatorType.Or,
							null,
							new ArgumentType[] {
										new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
										new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true),
										new ArgumentType(RBACRole.DataAnalyst.toPrivilegeType(), true),
										new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
										new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true),
										new ArgumentType(RBACRole.PrincipalInvestigator.toPrivilegeType(), true)});

				StatementType st = new StatementType(RBACTarget.EXPORT_LEVEL_0.toTargetType(),
							RBACAction.ACTION_EXPORT_UNRESTRICTED.toActionType(), rule);
				lst.add(st);
		} catch (Exception e) {
				System.out.println(e.getMessage());
		}
		
		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
				null, new ArgumentType[] {
					new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true),
					new ArgumentType(RBACRole.DataAnalyst.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true),
					new ArgumentType(RBACRole.PrincipalInvestigator.toPrivilegeType(), true)});
				StatementType st = new StatementType(RBACTarget.EXPORT_LEVEL_1.toTargetType(),
							RBACAction.ACTION_EXPORT_UNRESTRICTED.toActionType(), rule);
				lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} 

		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
				null, new ArgumentType[] {
					new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true),
					new ArgumentType(RBACRole.DataAnalyst.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true),
					new ArgumentType(RBACRole.PrincipalInvestigator.toPrivilegeType(), true)});
				StatementType st = new StatementType(RBACTarget.EXPORT_LEVEL_2.toTargetType(),
							RBACAction.ACTION_EXPORT_UNRESTRICTED.toActionType(), rule);
				lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} 

		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
				null, new ArgumentType[] {
					new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true),
					new ArgumentType(RBACRole.DataAnalyst.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true),
					new ArgumentType(RBACRole.PrincipalInvestigator.toPrivilegeType(), true)});
				StatementType st = new StatementType(RBACTarget.EXPORT_LEVEL_3.toTargetType(),
							RBACAction.ACTION_EXPORT_UNRESTRICTED.toActionType(), rule);
				lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} 

		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
				null, new ArgumentType[] {
					new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true),
					new ArgumentType(RBACRole.DataAnalyst.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true),
					new ArgumentType(RBACRole.PrincipalInvestigator.toPrivilegeType(), true)});
				StatementType st = new StatementType(RBACTarget.EXPORT_LEVEL_4.toTargetType(),
							RBACAction.ACTION_EXPORT_UNRESTRICTED.toActionType(), rule);
				lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} 

		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
				null, new ArgumentType[] {
					new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true),
					new ArgumentType(RBACRole.DataAnalyst.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true),
					new ArgumentType(RBACRole.PrincipalInvestigator.toPrivilegeType(), true)});
				StatementType st = new StatementType(RBACTarget.EXPORT_LEVEL_5.toTargetType(),
							RBACAction.ACTION_EXPORT_UNRESTRICTED.toActionType(), rule);
				lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} 

		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
				null, new ArgumentType[] {
					new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true),
					new ArgumentType(RBACRole.DataAnalyst.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true),
					new ArgumentType(RBACRole.PrincipalInvestigator.toPrivilegeType(), true)});
				StatementType st = new StatementType(RBACTarget.EXPORT_LEVEL_6.toTargetType(),
							RBACAction.ACTION_EXPORT_TRANSFORMED.toActionType(), rule);
				lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} 

		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
				null, new ArgumentType[] {
					new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true),
					new ArgumentType(RBACRole.DataAnalyst.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true),
					new ArgumentType(RBACRole.PrincipalInvestigator.toPrivilegeType(), true)});
				StatementType st = new StatementType(RBACTarget.EXPORT_LEVEL_7.toTargetType(),
							RBACAction.ACTION_EXPORT_TRANSFORMED.toActionType(), rule);
				lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} 

		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
				null, new ArgumentType[] {
					new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true),
					new ArgumentType(RBACRole.DataAnalyst.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true),
					new ArgumentType(RBACRole.PrincipalInvestigator.toPrivilegeType(), true)});
				StatementType st = new StatementType(RBACTarget.EXPORT_LEVEL_8.toTargetType(),
							RBACAction.ACTION_EXPORT_TRANSFORMED.toActionType(), rule);
				lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} 

		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
				null, new ArgumentType[] {
					new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true),
					new ArgumentType(RBACRole.DataAnalyst.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true),
					new ArgumentType(RBACRole.PrincipalInvestigator.toPrivilegeType(), true)});
				StatementType st = new StatementType(RBACTarget.EXPORT_LEVEL_9.toTargetType(),
							RBACAction.ACTION_EXPORT_TRANSFORMED.toActionType(), rule);
				lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} 

		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
				null, new ArgumentType[] {
					new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true),
					new ArgumentType(RBACRole.DataAnalyst.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true),
					new ArgumentType(RBACRole.PrincipalInvestigator.toPrivilegeType(), true)});
				StatementType st = new StatementType(RBACTarget.EXPORT_LEVEL_10.toTargetType(),
							RBACAction.ACTION_EXPORT_TRANSFORMED.toActionType(), rule);
				lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} 

		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
				null, new ArgumentType[] {
					new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true),
					new ArgumentType(RBACRole.DataAnalyst.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true),
					new ArgumentType(RBACRole.PrincipalInvestigator.toPrivilegeType(), true)});
				StatementType st = new StatementType(RBACTarget.EXPORT_LEVEL_11.toTargetType(),
							RBACAction.ACTION_EXPORT_RESTRICTED.toActionType(), rule);
				lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} 

		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
				null, new ArgumentType[] {
					new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true),
					new ArgumentType(RBACRole.DataAnalyst.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true),
					new ArgumentType(RBACRole.PrincipalInvestigator.toPrivilegeType(), true)});
				StatementType st = new StatementType(RBACTarget.EXPORT_LEVEL_12.toTargetType(),
							RBACAction.ACTION_EXPORT_RESTRICTED.toActionType(), rule);
				lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} 

		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
				null, new ArgumentType[] {
					new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true),
					new ArgumentType(RBACRole.DataAnalyst.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true),
					new ArgumentType(RBACRole.PrincipalInvestigator.toPrivilegeType(), true)});
				StatementType st = new StatementType(RBACTarget.EXPORT_LEVEL_13.toTargetType(),
							RBACAction.ACTION_EXPORT_RESTRICTED.toActionType(), rule);
				lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} 

		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
				null, new ArgumentType[] {
					new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true),
					new ArgumentType(RBACRole.DataAnalyst.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true),
					new ArgumentType(RBACRole.PrincipalInvestigator.toPrivilegeType(), true)});
				StatementType st = new StatementType(RBACTarget.EXPORT_LEVEL_14.toTargetType(),	
						           RBACAction.ACTION_EXPORT_RESTRICTED.toActionType(), rule);
				lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} 
		
		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
				null, new ArgumentType[] {
					new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true),
					new ArgumentType(RBACRole.DataAnalyst.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
					new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true),
					new ArgumentType(RBACRole.PrincipalInvestigator.toPrivilegeType(), true)});
				StatementType st = new StatementType(RBACTarget.EXPORT_LEVEL_15.toTargetType(), 
									RBACAction.ACTION_EXPORT_RESTRICTED.toActionType(), rule);
				lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} 
		
	
		// PM can remove a dataset (but only SA's can remove a published dataset)
		try {
			CompositeRuleType removeDataSetRule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
					new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_DR_REMOVE_DATASET.toActionType(), removeDataSetRule);
			lst.add(st);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
				
		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.And,
								new CompositeRuleType[] { new CompositeRuleType(
										OperatorType.Or,
										null,
											new ArgumentType[] {
												new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(),true) ,
												new ArgumentType(RBACRole.TreatmentAdministrator.toPrivilegeType(),true) }), },
											new ArgumentType[] {
												new ArgumentType(new PrivilegeType(null, groups[g]), true) }), },				
											new ArgumentType[] { 
												new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.changeStatus_D_Incomplete_Complete.toActionType(),
						rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}

			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.And,
								new CompositeRuleType[] { new CompositeRuleType(
										OperatorType.Or,
										null,
											new ArgumentType[] {
												new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(),true) ,
												new ArgumentType(RBACRole.TreatmentAdministrator.toPrivilegeType(),true) }), },
											new ArgumentType[] {
												new ArgumentType(new PrivilegeType(null, groups[g]), true) }), },				
											new ArgumentType[] { 
												new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.changeStatus_D_Complete_Incomplete.toActionType(),
						rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}

			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.And,
								new CompositeRuleType[] { new CompositeRuleType(
										OperatorType.Or,
										null,
											new ArgumentType[] {
												new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(),true) ,
												new ArgumentType(RBACRole.TreatmentAdministrator.toPrivilegeType(),true) }), },
											new ArgumentType[] {
												new ArgumentType(new PrivilegeType(null, groups[g]), true) }), },				
											new ArgumentType[] { 
												new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.changeStatus_D_Complete_Pending.toActionType(),
						rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}

			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.And,
								new CompositeRuleType[] { new CompositeRuleType(
										OperatorType.Or,
										null,
											new ArgumentType[] {
												new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(),true) ,
												new ArgumentType(RBACRole.TreatmentAdministrator.toPrivilegeType(),true) }), },
											new ArgumentType[] {
												new ArgumentType(new PrivilegeType(null, groups[g]), true) }), },				
											new ArgumentType[] { 
												new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.changeStatus_D_Complete_Controlled.toActionType(),
						rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.And,
								new CompositeRuleType[] { new CompositeRuleType(
										OperatorType.Or,
										null,
											new ArgumentType[] {
												new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(),true) ,
												new ArgumentType(RBACRole.TreatmentAdministrator.toPrivilegeType(),true) }), },
											new ArgumentType[] {
												new ArgumentType(new PrivilegeType(null, groups[g]), true) }), },				
											new ArgumentType[] { 
												new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.changeDocumentStatus_Incomplete_Complete.toActionType(),
						rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}

			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.And,
								new CompositeRuleType[] { new CompositeRuleType(
										OperatorType.Or,
										null,
											new ArgumentType[] {
												new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(),true) ,
												new ArgumentType(RBACRole.TreatmentAdministrator.toPrivilegeType(),true) }), },
											new ArgumentType[] {
												new ArgumentType(new PrivilegeType(null, groups[g]), true) }), },				
											new ArgumentType[] { 
												new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.changeDocumentStatus_Complete_Incomplete.toActionType(),
						rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}

			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.And,
								new CompositeRuleType[] { new CompositeRuleType(
										OperatorType.Or,
										null,
											new ArgumentType[] {
												new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(),true) ,
												new ArgumentType(RBACRole.TreatmentAdministrator.toPrivilegeType(),true) }), },
											new ArgumentType[] {
												new ArgumentType(new PrivilegeType(null, groups[g]), true) }), },				
											new ArgumentType[] { 
												new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.changeDocumentStatus_Complete_Pending.toActionType(),
						rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}

			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.And,
								new CompositeRuleType[] { new CompositeRuleType(
										OperatorType.Or,
										null,
											new ArgumentType[] {
												new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(),true) ,
												new ArgumentType(RBACRole.TreatmentAdministrator.toPrivilegeType(),true) }), },
											new ArgumentType[] {
												new ArgumentType(new PrivilegeType(null, groups[g]), true) }), },				
											new ArgumentType[] { 
												new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.changeDocumentStatus_Complete_Controlled.toActionType(),
						rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}

		}

		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.And,
								null,
								new ArgumentType[] {
										new ArgumentType(new PrivilegeType(null, groups[g]), true),
										new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(),true) }) },
								new ArgumentType[] { 
										new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.ACTION_ESL_UNLOCK_SUBJECT.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.And,
								null,
								new ArgumentType[] {
										new ArgumentType(new PrivilegeType(
												null, groups[g]), true),
												new ArgumentType(
														RBACRole.ClinicalResearchOfficer.toPrivilegeType(),
														true) }) },
														new ArgumentType[] { new ArgumentType(
																RBACRole.ClinicalResearchManager.toPrivilegeType(), true) });
				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.ACTION_DR_CAN_RECORD_BE_RANDOMIZED.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { 
							new CompositeRuleType(
								OperatorType.And,
								new CompositeRuleType[] { 
									new CompositeRuleType(
										OperatorType.Or,
										null,
										new ArgumentType[] {
												new ArgumentType(RBACRole.DataAnalyst.toPrivilegeType(), true),
												new ArgumentType(RBACRole.PrincipalInvestigator.toPrivilegeType(), true),
												new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true)}) },
									new ArgumentType[] { 
										new ArgumentType(new PrivilegeType(null, groups[g]), true) }) }, 
						new ArgumentType[] {
								new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
								new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
								new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true) } );
				StatementType st = new StatementType(Group.fromGroupType(
						groups[g]).toTargetType(), RBACAction.ACTION_DR_SAVE_QUERY.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { 
							new CompositeRuleType(
								OperatorType.And,
								new CompositeRuleType[] { 
									new CompositeRuleType(
										OperatorType.Or,
										null,
										new ArgumentType[] {
												new ArgumentType(RBACRole.DataAnalyst.toPrivilegeType(), true),
												new ArgumentType(RBACRole.PrincipalInvestigator.toPrivilegeType(), true),
												new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true)}) },
									new ArgumentType[] { 
										new ArgumentType(new PrivilegeType(null, groups[g]), true) }) }, 
						new ArgumentType[] {
								new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
								new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
								new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true) } );
				StatementType st = new StatementType(Group.fromGroupType(
						groups[g]).toTargetType(), RBACAction.ACTION_DR_GET_MY_QUERIES.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { 
							new CompositeRuleType(
								OperatorType.And,
								new CompositeRuleType[] { 
									new CompositeRuleType(
										OperatorType.Or,
										null,
										new ArgumentType[] {
												new ArgumentType(RBACRole.DataAnalyst.toPrivilegeType(), true),
												new ArgumentType(RBACRole.PrincipalInvestigator.toPrivilegeType(), true),
												new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true)}) },
									new ArgumentType[] { 
										new ArgumentType(new PrivilegeType(null, groups[g]), true) }) }, 
						new ArgumentType[] {
								new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
								new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
								new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true) } );
				StatementType st = new StatementType(Group.fromGroupType(
						groups[g]).toTargetType(), RBACAction.ACTION_DR_EXECUTE_QUERY.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.Or,
						new CompositeRuleType[] { 
							new CompositeRuleType(
								OperatorType.And,
								new CompositeRuleType[] { 
									new CompositeRuleType(
										OperatorType.Or,
										null,
										new ArgumentType[] {
												new ArgumentType(RBACRole.DataAnalyst.toPrivilegeType(), true),
												new ArgumentType(RBACRole.PrincipalInvestigator.toPrivilegeType(), true),
												new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true)}) },
									new ArgumentType[] { 
										new ArgumentType(new PrivilegeType(null, groups[g]), true) }) }, 
						new ArgumentType[] {
								new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
								new ArgumentType(RBACRole.ChiefInvestigator.toPrivilegeType(), true),
								new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true) } );
				StatementType st = new StatementType(Group.fromGroupType(
						groups[g]).toTargetType(), RBACAction.ACTION_DR_GET_QUERY.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		/****************** Start Sample Tracking **********************/
		try {
			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.Or,
					null,
					new ArgumentType[] {
							new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true),
							new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true)});
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_DR_VIEW_SAMPLES_CONFIG.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.And,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.Or,
								null,
								new ArgumentType[] {
										new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true),
										new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(),true) }) },
								new ArgumentType[] { new ArgumentType(new PrivilegeType(null, groups[g]), true)});
				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.ACTION_DR_VIEW_SAMPLES.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		try {
			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.Or,
					null,
					new ArgumentType[] {new ArgumentType(RBACRole.ProjectManager.toPrivilegeType(), true)});
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_DR_EDIT_SAMPLES_CONFIG.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.And,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.Or,
								null,
								new ArgumentType[] {
										new ArgumentType(RBACRole.ClinicalResearchOfficer.toPrivilegeType(), true),
										new ArgumentType(RBACRole.ClinicalResearchManager.toPrivilegeType(),true) }) },
								new ArgumentType[] { new ArgumentType(new PrivilegeType(null, groups[g]), true)});
				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.ACTION_DR_EDIT_SAMPLES.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		/********************* End Sample Tracking **********************/
		
		// /End repository
		
		//grant sys-admin all rights
		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] {
							new ArgumentType(RBACRole.SystemAdministrator.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ANY.toActionType(), rule);
			lst.add(st);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return lst;

	}
}
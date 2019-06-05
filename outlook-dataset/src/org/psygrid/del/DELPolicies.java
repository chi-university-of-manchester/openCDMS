/*
Copyright (c) 2008, The University of Manchester, UK.

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
02110-1301, USA.
 */

package org.psygrid.del;

import java.util.ArrayList;
import java.util.List;

import org.psygrid.security.RBACAction;
import org.psygrid.security.RBACRole;
import org.psygrid.security.RBACTarget;
import org.psygrid.security.attributeauthority.model.hibernate.Group;
import org.psygrid.www.xml.security.core.types.ArgumentType;
import org.psygrid.www.xml.security.core.types.CompositeRuleType;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.OperatorType;
import org.psygrid.www.xml.security.core.types.PrivilegeType;
import org.psygrid.www.xml.security.core.types.StatementType;

/**
 * @author Lucy Bridges
 *
 */
public class DELPolicies {

	public static List<StatementType> buildStatements(GroupType[] groups){

		List<StatementType> lst = new ArrayList<StatementType>();


		/* Data Element Library */


		/* Save a new element */
		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.And,
						null,
						new ArgumentType[] {
								new ArgumentType(RBACRole.DELAuthor.toPrivilegeType(), true),
								new ArgumentType(new PrivilegeType(null, groups[g]), true)});
				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.ACTION_DEL_SAVE_NEW_ELEMENT.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		/* Get element as repository template */
		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.And,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.Or,
								null,
								new ArgumentType[] {
										new ArgumentType(RBACRole.DELViewer.toPrivilegeType(), true),
										new ArgumentType(RBACRole.DELAuthor.toPrivilegeType(), true),
										new ArgumentType(RBACRole.DELCurator.toPrivilegeType(), true)})},
										new ArgumentType[] { 
								new ArgumentType(new PrivilegeType(null, groups[g]), true) });
				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.ACTION_DEL_GET_ELEMENT_AS_REPOSITORY_TEMPLATE.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		/* Import a data element */
		try {
			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.Or,
					null,
					new ArgumentType[] {
							new ArgumentType(RBACRole.SystemAdministrator.toPrivilegeType(), true) } );
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_DEL_IMPORT_DATA_ELEMENT.toActionType(), rule);
			lst.add(st);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		/*Revise a data element */
		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.And,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.Or,
								null,
								new ArgumentType[] {
										new ArgumentType(RBACRole.DELAuthor.toPrivilegeType(), true) })},
										new ArgumentType[] { 
								new ArgumentType(new PrivilegeType(null, groups[g]), true) });

				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.ACTION_DEL_REVISE_ELEMENT.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		/* Get the metadata for an element */
		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.And,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.Or,
								null,
								new ArgumentType[] {
										new ArgumentType(RBACRole.DELViewer.toPrivilegeType(), true),
										new ArgumentType(RBACRole.DELAuthor.toPrivilegeType(), true),
										new ArgumentType(RBACRole.DELCurator.toPrivilegeType(), true)})},
										new ArgumentType[] { 
								new ArgumentType(new PrivilegeType(null, groups[g]), true) });

				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.ACTION_DEL_GET_METADATA.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		/* Search for an element */
		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.And,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.Or,
								null,
								new ArgumentType[] {
										new ArgumentType(RBACRole.DELViewer.toPrivilegeType(), true),
										new ArgumentType(RBACRole.DELAuthor.toPrivilegeType(), true),
										new ArgumentType(RBACRole.DELCurator.toPrivilegeType(), true)})},
										new ArgumentType[] { 
								new ArgumentType(new PrivilegeType(null, groups[g]), true) });
				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.ACTION_DEL_SOPHISTICATED_SEARCH_BY_TYPE_AND_NAME.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		/* Get document summary information */
		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.And,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.Or,
								null,
								new ArgumentType[] {
										new ArgumentType(RBACRole.DELViewer.toPrivilegeType(), true),
										new ArgumentType(RBACRole.DELAuthor.toPrivilegeType(), true),
										new ArgumentType(RBACRole.DELCurator.toPrivilegeType(), true)})},
										new ArgumentType[] { 
								new ArgumentType(new PrivilegeType(null, groups[g]), true) });
				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.ACTION_DEL_GET_DOCUMENTS_SUMMARY_INFO.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		/* Get list of allowed LSID Authorities */
		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.And,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.Or,
								null,
								new ArgumentType[] {
										new ArgumentType(RBACRole.DELViewer.toPrivilegeType(), true),
										new ArgumentType(RBACRole.DELAuthor.toPrivilegeType(), true),
										new ArgumentType(RBACRole.DELCurator.toPrivilegeType(), true)})},
										new ArgumentType[] { 
								new ArgumentType(new PrivilegeType(null, groups[g]), true) });
				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.ACTION_DEL_GET_LSID_AUTHORITY_LIST.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		
		/* Approve a pending element */
		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.And,
						null,
						new ArgumentType[] {
								new ArgumentType(RBACRole.DELCurator.toPrivilegeType(), true),
								new ArgumentType(new PrivilegeType(null, groups[g]), true)});
				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.ACTION_DEL_APPROVE_ELEMENT.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		
		/* Retrieve a list of LSIDs that are newer than locally held version */
		for (int g = 0; g < groups.length; g++) {
			try {
				CompositeRuleType rule = new CompositeRuleType(
						OperatorType.And,
						new CompositeRuleType[] { new CompositeRuleType(
								OperatorType.Or,
								null,
								new ArgumentType[] {
										new ArgumentType(RBACRole.DELViewer.toPrivilegeType(), true),
										new ArgumentType(RBACRole.DELAuthor.toPrivilegeType(), true),
										new ArgumentType(RBACRole.DELCurator.toPrivilegeType(), true)})},
										new ArgumentType[] { 
								new ArgumentType(new PrivilegeType(null, groups[g]), true) });
				StatementType st = new StatementType(Group.fromGroupType(groups[g]).toTargetType(),
						RBACAction.ACTION_DEL_REPORT_ELEMENT_STATUS_CHANGES.toActionType(), rule);
				lst.add(st);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		
		
		
		
		
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
		
		return lst;
	}
}

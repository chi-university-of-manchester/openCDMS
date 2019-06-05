package org.psygrid.tools;

import java.util.List;

import org.psygrid.security.DefaultPolicy;
import org.psygrid.security.RBACAction;
import org.psygrid.security.RBACRole;
import org.psygrid.security.RBACTarget;
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.psygrid.security.attributeauthority.service.AttributeAuthorityPortTypeManagementSOAPBindingStub;
import org.psygrid.security.attributeauthority.service.AttributeAuthorityPortTypeQuerySOAPBindingStub;
import org.psygrid.security.policyauthority.client.PAManagementClient;
import org.psygrid.security.policyauthority.service.PolicyAuthorityPortTypeManagementSOAPBindingStub;
import org.psygrid.www.xml.security.core.types.ArgumentType;
import org.psygrid.www.xml.security.core.types.CompositeRuleType;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.OperatorType;
import org.psygrid.www.xml.security.core.types.PolicyDescriptionType;
import org.psygrid.www.xml.security.core.types.PolicyType;
import org.psygrid.www.xml.security.core.types.PrivilegeType;
import org.psygrid.www.xml.security.core.types.ProjectDescriptionType;
import org.psygrid.www.xml.security.core.types.StatementType;
import org.psygrid.www.xml.security.core.types.TargetType;

public class RefreshPolicies {

	public static void main(String[] args) {
		try {
			boolean refreshSpecifiedProjectOnly = false;
			String specifiedProject = null;
			
			if(args[0] != null){
				refreshSpecifiedProjectOnly = true;
				specifiedProject = args[0];
			}
			
			AAQueryClient qc = new AAQueryClient("test.properties");
			ProjectDescriptionType[] allProjects = null;
			((AttributeAuthorityPortTypeQuerySOAPBindingStub)qc.getStub()).setTimeout(3600000);
			
			allProjects = qc.getPort().getProjects();

			System.out.println("There are "+allProjects.length+" to update");
			
			for (int x = 0; x < allProjects.length; x++) {
				if(allProjects[x].getProject().getName().equals("SYSTEM") ||
						allProjects[x].getProject().getName().equals("DataElementLibrary") ||
						allProjects[x].getProject().getName().equals("DELTest")){
					System.out.println("Skipping "+allProjects[x].getProject().getName());
					continue;
				}
				
				if(refreshSpecifiedProjectOnly = true && !allProjects[x].getProject().getIdCode().equals(specifiedProject)){
					continue;
				}
				
				PAManagementClient mc = new PAManagementClient("test.properties");
				((PolicyAuthorityPortTypeManagementSOAPBindingStub)mc.getStub()).setTimeout(3600000);
				System.out.println("Processing "+allProjects[x].getProject().getName());
//				PolicyType pt = mc.getPort().getPolicyByProject(
//						new PolicyType(allProjects[x].getProject().getName(),
//								allProjects[x].getProject().getIdCode(), null,
//								null));

				GroupType[] groups = allProjects[x].getGroup();
				System.out.println("Retrieved "+groups.length+" groups for "+allProjects[x].getProject().getName());
				PolicyDescriptionType pdt = new PolicyDescriptionType();
				pdt.setActions(RBACAction.allActions());

				TargetType[] tta = new TargetType[groups.length
						+ RBACTarget.allAsTargets().length];
				for (int i = 0; i < groups.length; i++) {
					tta[i] = new TargetType(groups[i].getName(), groups[i]
							.getIdCode());
				}
				int j = 0;
				for (int i = groups.length; i < groups.length
						+ RBACTarget.allAsTargets().length; i++, j++) {
					tta[i] = RBACTarget.allAsTargets()[j];
				}
				pdt.setTargets(tta);

				PrivilegeType[] prta = new PrivilegeType[groups.length
						+ RBACRole.allAsPrivileges().length];
				for (int i = 0; i < groups.length; i++) {
					prta[i] = new PrivilegeType(null, groups[i]);
				}
				int k = 0;
				for (int i = groups.length; i < groups.length
						+ RBACRole.allAsPrivileges().length; i++, k++) {
					prta[i] = RBACRole.allAsPrivileges()[k];
				}
				pdt.setPrivileges(prta);

				try {
					CompositeRuleType rule = new CompositeRuleType(
							OperatorType.Or, null,
							new ArgumentType[] { new ArgumentType(
									RBACRole.SystemAdministrator
											.toPrivilegeType(), true) });
					StatementType st = new StatementType(RBACTarget.ANY
							.toTargetType(), RBACAction.ANY.toActionType(),
							rule);
					StatementType[] sta = new StatementType[] { st };
					PolicyType newpt = new PolicyType(allProjects[x].getProject().getName(),
							allProjects[x].getProject().getIdCode(), 
							pdt, 
							sta);
					PolicyType[] pta = new PolicyType[] { newpt };
					mc.getPort().deletePolicy(pta);
					System.out.println("Deleted "+allProjects[x].getProject().getName());
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}

				try {
					CompositeRuleType rule = new CompositeRuleType(
							OperatorType.Or, null,
							new ArgumentType[] { new ArgumentType(
									RBACRole.SystemAdministrator
											.toPrivilegeType(), true) });
					StatementType st = new StatementType(RBACTarget.ANY
							.toTargetType(), RBACAction.ANY.toActionType(),
							rule);
					StatementType[] sta = new StatementType[] { st };
					PolicyType newpt = new PolicyType(allProjects[x].getProject().getName(),
							allProjects[x].getProject().getIdCode(), 
							pdt, 
							sta);
					PolicyType[] pta = new PolicyType[] { newpt };
					// Must always use addPolicy first time
					mc.getPort().addPolicy(pta);
					System.out.println("Added "+allProjects[x].getProject().getName());
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}

				List<StatementType> lst = DefaultPolicy.buildStatements(groups);

				try {
					System.out.println("Statements = " + lst.size());
					StatementType[] sta = new StatementType[lst.size()];
					int i = 0;
					for (StatementType st : lst) {
						sta[i] = st;
						i++;
					}
					PolicyType newpt = new PolicyType(allProjects[x].getProject().getName(),
							allProjects[x].getProject().getIdCode(), 
							pdt, 
							sta);
					PolicyType[] pta = new PolicyType[] { newpt };
					mc.getPort().addStatementToPolicy(pta);
					System.out.println("Added default statements for "+allProjects[x].getProject().getName());
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}

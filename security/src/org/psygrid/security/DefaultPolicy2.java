package org.psygrid.security;

import java.util.ArrayList;
import java.util.List;

import org.psygrid.security.statementmigration.SimpleStatementBuilder;
import org.psygrid.security.statementmigration.StatementTransformationException;
import org.psygrid.security.statementmigration.StatementTransformer;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.StatementType;

public class DefaultPolicy2 {
	
	
	private final static GroupType tokenGroup = new GroupType("TOKEN", "001001", "project");
	GroupType[] groups = new GroupType[]{tokenGroup};

	List<StatementType> statementDTOs = DefaultPolicy.buildStatements(groups);

	public static List<StatementType> buildStatements(){
		List<StatementType> originalDefaultStatements = DefaultPolicy.buildStatements(new GroupType[]{tokenGroup});
		List<StatementType> equivalentSimpleStatements = new ArrayList<StatementType>();
		
		for(StatementType original : originalDefaultStatements){
			System.out.println(original.getAction().getName());
		}
		
		for(StatementType original : originalDefaultStatements){
			try {
				List<StatementType> transformedStatements = StatementTransformer.transformStatement(original);
				equivalentSimpleStatements.addAll(transformedStatements);
			} catch (StatementTransformationException e) {
				System.out.println(e.getMessage() + " for Action: " + original.getAction().getName());
			}
		}
		
		
		//Medication distribution statements
		List<StatementType> additionalStatements = new ArrayList<StatementType>();
		additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_MD_ADD_PHARMACY_TO_MEDS_PROJECT, RBACRole.ProjectManager));
		additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_MD_ALLOCATE_MEDS, RBACRole.ClinicalResearchOfficer));
		additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_MD_MEDS_PHARMACY_WORKFLOW, RBACRole.Pharmacist));
		additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_MD_MEDS_WORKFLOW_CORRECTION, RBACRole.ProjectManager));
		additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_MD_VIEW_MEDS_PACKAGE, RBACRole.ProjectManager));
		additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_MD_REQUEST_MEDS_EXPORT_FOR_PROJECT, RBACRole.ProjectManager));
		additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_MD_VIEW_PROJECT_PACKAGES, RBACRole.ProjectManager));
		additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_MD_VIEW_MEDS_PACKAGE, RBACRole.Pharmacist));
		additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_MD_VIEW_MEDS_PACKAGE, RBACRole.ClinicalResearchOfficer));
		
		// Delete subject from PR
		additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_ESL_DELETE_SUBJECT, RBACRole.ClinicalResearchOfficer));
		additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_ESL_DELETE_SUBJECT, RBACRole.ClinicalResearchManager));
		
		// Export PR data
		additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_ESL_EXPORT, RBACRole.CanExportPRData));
	
		equivalentSimpleStatements.addAll(additionalStatements);
		
		return equivalentSimpleStatements;
	}
	
	public static List<StatementType>buildStatement(StatementType oldStatement) throws StatementTransformationException{

		List<StatementType> transformedStatements = StatementTransformer.transformStatement(oldStatement);
		
		
		return transformedStatements;

	}
	

}

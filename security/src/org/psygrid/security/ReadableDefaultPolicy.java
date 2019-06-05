package org.psygrid.security;

import java.util.ArrayList;
import java.util.List;

import org.psygrid.security.statementmigration.SimpleStatementBuilder;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.StatementType;

public class ReadableDefaultPolicy {
	public static List<StatementType> buildStatements(GroupType[] groups){
		
	List<StatementType> additionalStatements = new ArrayList<StatementType>();
	////////////////////////////////////////////////////////
	//Statements for DELCurator
	
	//Non-group statements for DELCurator
	
	
	//Group statements for DELCurator
	
	////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////
	//Statements for NamedInvestigator
	
	//Non-group statements for NamedInvestigator
	
	
	//Group statements for NamedInvestigator
	
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_INVESTIGATOR_REPORT, RBACRole.NamedInvestigator));
	////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////
	//Statements for RecruitmentManager
	
	//Non-group statements for RecruitmentManager
	
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GENERATE_TREND_REPORT, RBACRole.RecruitmentManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GENERATE_DYNAMIC_REPORT, RBACRole.RecruitmentManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GENERATE_DYNAMIC_TRENDS_REPORT, RBACRole.RecruitmentManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GENERATE_DYNAMIC_RECORD_REPORT, RBACRole.RecruitmentManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_UKCRN_REPORT, RBACRole.RecruitmentManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_VIEW_MGMT_REPORT, RBACRole.RecruitmentManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_STATUS_REPORT, RBACRole.RecruitmentManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_REPORTS_OF_TYPE, RBACRole.RecruitmentManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_REPORT, RBACRole.RecruitmentManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_GROUPS_FOR_CODES, RBACRole.RecruitmentManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_IDENTIFIERS, RBACRole.RecruitmentManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_DOCUMENTS, RBACRole.RecruitmentManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_ENTRIES, RBACRole.RecruitmentManager));
	
	//Group statements for RecruitmentManager
	
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_RECRUITMENT_REPORT, RBACRole.RecruitmentManager));
	////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////
	//Statements for StudyPatcher
	
	//Non-group statements for StudyPatcher
	
	
	//Group statements for StudyPatcher
	
	////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////
	//Statements for QueryData
	
	//Non-group statements for QueryData
	
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_EXECUTE_QUERY, RBACRole.QueryData));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_SAVE_QUERY, RBACRole.QueryData));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_MY_QUERIES, RBACRole.QueryData));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_QUERY, RBACRole.QueryData));
	
	//Group statements for QueryData
	
	////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////
	//Statements for TreatmentAdministrator
	
	//Non-group statements for TreatmentAdministrator
	
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_ESL_RETRIEVE_PROJECT, RBACRole.TreatmentAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_ESL_RETRIEVE_PROJECT_BY_CODE, RBACRole.TreatmentAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_ESL_FIND_SUBJECT_BY_EXAMPLE, RBACRole.TreatmentAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_ESL_LOOKUP_STUDY_NUMBER, RBACRole.TreatmentAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_RS_GET_ALLOCATION, RBACRole.TreatmentAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_DOC_BLIND, RBACRole.TreatmentAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_VIEW_TREATMENT, RBACRole.TreatmentAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_EDIT_TREATMENT, RBACRole.TreatmentAdministrator));
	
	//Group statements for TreatmentAdministrator
	
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_ESL_RETRIEVE_SUBJECT, RBACRole.TreatmentAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_ESL_RETRIEVE_SUBJECT_BY_STUDY_NUMBER, RBACRole.TreatmentAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_ESL_LOOKUP_RANDOMISATION_RESULT, RBACRole.TreatmentAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_ESLW_LOOKUP_RANDOMIZATION_RESULT, RBACRole.TreatmentAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_ESLW_LOOKUP_STUDY_NUMBER, RBACRole.TreatmentAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_CHANGE_RECORD_STATUS, RBACRole.TreatmentAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_GET_BINARY_DATA, RBACRole.TreatmentAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.getRecordsDocumentsByStatus_Incomplete, RBACRole.TreatmentAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.getRecordsDocumentsByStatus_Rejected, RBACRole.TreatmentAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_GET_RECORD_COMPLETE, RBACRole.TreatmentAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_GET_RECORD_SUMMARY, RBACRole.TreatmentAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_GET_RECORD_SINGLE_DOCUMENT, RBACRole.TreatmentAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_GET_RECORDS_WITH_CONSENT_BY_GROUPS, RBACRole.TreatmentAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_GET_RECORDS_BY_GROUPS, RBACRole.TreatmentAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.getRecordsByGroupsAndDocStatus_Incomplete, RBACRole.TreatmentAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.getRecordsByGroupsAndDocStatus_Rejected, RBACRole.TreatmentAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_SAVE_RECORD, RBACRole.TreatmentAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.changeStatus_D_Incomplete_Pending, RBACRole.TreatmentAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.changeStatus_D_Pending_Incomplete, RBACRole.TreatmentAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.changeStatus_D_Rejected_Pending, RBACRole.TreatmentAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.changeDocumentStatus_Incomplete_Pending, RBACRole.TreatmentAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.changeDocumentStatus_Pending_Incomplete, RBACRole.TreatmentAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.changeDocumentStatus_Rejected_Pending, RBACRole.TreatmentAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_GENERATE_REPORT, RBACRole.TreatmentAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_GET_DELETED_RECORDS_BY_GROUPS, RBACRole.TreatmentAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_GET_LINKABLE_RECORDS, RBACRole.TreatmentAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_GET_LINKED_RECORDS, RBACRole.TreatmentAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_GET_CONSENT_AND_STATUS_INFO_FOR_GROUPS, RBACRole.TreatmentAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_ESL_GET_PROPERTY, RBACRole.TreatmentAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_DOC_BLIND_INST, RBACRole.TreatmentAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_VIEW_TREATMENT_INST, RBACRole.TreatmentAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_EDIT_TREATMENT_INST, RBACRole.TreatmentAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_GET_STATUS_ID_FOR_DOCUMENT, RBACRole.TreatmentAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.changeStatus_D_Incomplete_Complete, RBACRole.TreatmentAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.changeStatus_D_Complete_Incomplete, RBACRole.TreatmentAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.changeStatus_D_Complete_Pending, RBACRole.TreatmentAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.changeStatus_D_Complete_Controlled, RBACRole.TreatmentAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.changeDocumentStatus_Incomplete_Complete, RBACRole.TreatmentAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.changeDocumentStatus_Complete_Incomplete, RBACRole.TreatmentAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.changeDocumentStatus_Complete_Pending, RBACRole.TreatmentAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.changeDocumentStatus_Complete_Controlled, RBACRole.TreatmentAdministrator));
	////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////
	//Statements for Pharmacist
	
	//Non-group statements for Pharmacist
	
	
	//Group statements for Pharmacist
	
	////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////
	//Statements for ProjectManager
	
	//Non-group statements for ProjectManager
	
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_AA_GET_USERS_AND_PRIVILEGES_IN_PROJECT, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_AA_ADD_USER, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_AA_LDAP_QUERY, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_AA_RESET_PASSWORD, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_AA_GET_USER, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_AA_MODIFY_USER, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_AA_MODIFY_PROJECT, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_AA_DELETE_PROJECT, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_PA_MODIFY_POLICY, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_PA_GET_POLICY, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_PA_DELETE_POLICY, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_ESL_SAVE_PROJECT, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_ESL_RETRIEVE_ALL_PROJECTS, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_ESL_RETRIEVE_PROJECT, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_ESL_RETRIEVE_PROJECT_BY_CODE, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_RS_SAVE_RANDOMIZER, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_RS_DELETE_RANDOMIZATION, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_RS_GET_RANDOMIZER, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_SAVE_DATASET, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_SAVE_DATASET, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_ADD_IDENTIFIER, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_SAVE_RECORD_AS_USER, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_PUBLISH_DATASET, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_PATCH_DATASET, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_SAVE_REPORT, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_BINARY_DATA, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_RECORD_COMPLETE, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_RECORD_SUMMARY, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_RECORD_SINGLE_DOCUMENT, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_RECORDS_WITH_CONSENT_BY_GROUPS, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_RECORDS_BY_GROUPS, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_SAVE_RECORD, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GENERATE_REPORT, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_ALL_REPORTS_BY_DATASET, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_DELETE_REPORT, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GENERATE_DYNAMIC_REPORT, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GENERATE_DYNAMIC_TRENDS_REPORT, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GENERATE_DYNAMIC_RECORD_REPORT, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GENERATE_MANAGEMENT_REPORT, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_VIEW_MGMT_REPORT, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_STATUS_REPORT, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_REPORTS_OF_TYPE, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_REPORT, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_GROUPS_FOR_CODES, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_ESL_DELETE_PROJECT, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_DOWNLOAD_EXPORT, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_MY_EXPORT_REQUESTS, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_REQUEST_EXPORT, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_CANCEL_EXPORT, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_ESL_EXPORT, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_DELETED_RECORDS_BY_GROUPS, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_LINKABLE_RECORDS, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_LINKED_RECORDS, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_CONSENT_AND_STATUS_INFO_FOR_GROUPS, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_DOCUMENTS, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_ENTRIES, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_DOC_STANDARD, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_VIEW_TREATMENT, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_DOC_VIEW_IDENTITY, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_STATUS_ID_FOR_DOCUMENT, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_0, RBACAction.ACTION_EXPORT_UNRESTRICTED, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_1, RBACAction.ACTION_EXPORT_UNRESTRICTED, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_2, RBACAction.ACTION_EXPORT_UNRESTRICTED, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_3, RBACAction.ACTION_EXPORT_UNRESTRICTED, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_4, RBACAction.ACTION_EXPORT_UNRESTRICTED, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_5, RBACAction.ACTION_EXPORT_UNRESTRICTED, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_6, RBACAction.ACTION_EXPORT_TRANSFORMED, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_7, RBACAction.ACTION_EXPORT_TRANSFORMED, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_8, RBACAction.ACTION_EXPORT_TRANSFORMED, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_9, RBACAction.ACTION_EXPORT_TRANSFORMED, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_10, RBACAction.ACTION_EXPORT_TRANSFORMED, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_11, RBACAction.ACTION_EXPORT_RESTRICTED, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_12, RBACAction.ACTION_EXPORT_RESTRICTED, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_13, RBACAction.ACTION_EXPORT_RESTRICTED, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_14, RBACAction.ACTION_EXPORT_RESTRICTED, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_15, RBACAction.ACTION_EXPORT_RESTRICTED, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_REMOVE_DATASET, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_SAVE_QUERY, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_MY_QUERIES, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_EXECUTE_QUERY, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_QUERY, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_VIEW_SAMPLES_CONFIG, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_EDIT_SAMPLES_CONFIG, RBACRole.ProjectManager));
	
	//Group statements for ProjectManager
	
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_DOC_STANDARD_INST, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_VIEW_TREATMENT_INST, RBACRole.ProjectManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_DOC_VIEW_IDENTITY_INST, RBACRole.ProjectManager));
	////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////
	//Statements for PrincipalInvestigator
	
	//Non-group statements for PrincipalInvestigator
	
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GENERATE_TREND_REPORT, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GENERATE_DYNAMIC_REPORT, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GENERATE_DYNAMIC_TRENDS_REPORT, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GENERATE_DYNAMIC_RECORD_REPORT, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GENERATE_MANAGEMENT_REPORT, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_VIEW_MGMT_REPORT, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_STATUS_REPORT, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_REPORTS_OF_TYPE, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_REPORT, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_GROUPS_FOR_CODES, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_DOWNLOAD_EXPORT, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_MY_EXPORT_REQUESTS, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_SAVE_QUERY, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_MY_QUERIES, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_QUERY, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_IDENTIFIERS, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_ESL_RETRIEVE_NHS_NUMBERS, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_ESL_LOOKUP_RANDOMISATION_RESULT_FOR_DATE, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_RS_LOOKUP_RANDOMIZATION_RESULT_FOR_DATE, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_DOCUMENTS, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_ENTRIES, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_0, RBACAction.ACTION_EXPORT_UNRESTRICTED, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_1, RBACAction.ACTION_EXPORT_UNRESTRICTED, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_2, RBACAction.ACTION_EXPORT_UNRESTRICTED, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_3, RBACAction.ACTION_EXPORT_UNRESTRICTED, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_4, RBACAction.ACTION_EXPORT_UNRESTRICTED, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_5, RBACAction.ACTION_EXPORT_UNRESTRICTED, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_6, RBACAction.ACTION_EXPORT_TRANSFORMED, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_7, RBACAction.ACTION_EXPORT_TRANSFORMED, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_8, RBACAction.ACTION_EXPORT_TRANSFORMED, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_9, RBACAction.ACTION_EXPORT_TRANSFORMED, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_10, RBACAction.ACTION_EXPORT_TRANSFORMED, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_11, RBACAction.ACTION_EXPORT_RESTRICTED, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_12, RBACAction.ACTION_EXPORT_RESTRICTED, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_13, RBACAction.ACTION_EXPORT_RESTRICTED, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_14, RBACAction.ACTION_EXPORT_RESTRICTED, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_15, RBACAction.ACTION_EXPORT_RESTRICTED, RBACRole.PrincipalInvestigator));
	
	//Group statements for PrincipalInvestigator
	
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_GET_BINARY_DATA, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_GET_RECORD_COMPLETE, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_GET_RECORD_SUMMARY, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_GET_RECORD_SINGLE_DOCUMENT, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_GET_RECORDS_WITH_CONSENT_BY_GROUPS, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_GET_RECORDS_BY_GROUPS, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_GENERATE_REPORT, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_INVESTIGATOR_REPORT, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_REQUEST_EXPORT, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_EXECUTE_QUERY, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_CANCEL_EXPORT, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_ESL_EXPORT, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_GET_DELETED_RECORDS_BY_GROUPS, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_GET_LINKABLE_RECORDS, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_GET_LINKED_RECORDS, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_GET_CONSENT_AND_STATUS_INFO_FOR_GROUPS, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_GET_STATUS_ID_FOR_DOCUMENT, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_SAVE_QUERY, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_GET_MY_QUERIES, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_EXECUTE_QUERY, RBACRole.PrincipalInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_GET_QUERY, RBACRole.PrincipalInvestigator));
	////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////
	//Statements for ClinicalResearchManager
	
	//Non-group statements for ClinicalResearchManager
	
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_ESL_SAVE_PROJECT, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_ESL_RETRIEVE_ALL_PROJECTS, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_ESL_RETRIEVE_PROJECT, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_ESL_RETRIEVE_PROJECT_BY_CODE, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_ESL_EMERGENCY_BREAK_IN, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_ESL_LOOKUP_RANDOMIZER_STATISTICS, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_ESL_LOOKUP_STRATIFIED_RANDOMIZER_STATISTICS, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_ESL_SAVE_SUBJECT, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_ESL_RANDOMISE_SUBJECT, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_ESL_RETRIEVE_SUBJECT, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_ESL_FIND_SUBJECT_BY_EXAMPLE, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_ESL_RETRIEVE_SUBJECT_BY_STUDY_NUMBER, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_ESL_LOOKUP_STUDY_NUMBER, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_ESL_LOOKUP_RANDOMISATION_RESULT, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_RS_GET_ALLOCATION, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_RS_CHECK_INTEGRITY, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_RS_SAVE_RANDOMIZER, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_RS_ALLOCATE, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_RS_GET_ALLOCATIONS, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_ESLW_LOOKUP_RANDOMIZATION_RESULT, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_ESLW_LOOKUP_STUDY_NUMBER, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_CHANGE_RECORD_STATUS, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_MARK_RESPONSE_AS_VALID, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_MARK_RESPONSE_AS_INVALID, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_SAVE_DATASET, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_SAVE_DATASET, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_PUBLISH_DATASET, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_SAVE_REPORT, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_BINARY_DATA, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_RECORDS_BY_STATUS, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_RECORDS, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.getRecordsDocumentsByStatus_Incomplete, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.getRecordsDocumentsByStatus_Pending, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.getRecordsDocumentsByStatus_Approved, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.getRecordsDocumentsByStatus_Rejected, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_RECORD_COMPLETE, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_RECORD_SUMMARY, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_RECORD_SINGLE_DOCUMENT, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_RECORDS_WITH_CONSENT_BY_GROUPS, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_RECORDS_BY_GROUPS, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.getRecordsByGroupsAndDocStatus_Incomplete, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.getRecordsByGroupsAndDocStatus_Pending, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.getRecordsByGroupsAndDocStatus_Approved, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.getRecordsByGroupsAndDocStatus_Rejected, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GENERATE_IDENTIFIERS, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_SAVE_RECORD, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_WITHDRAW_CONSENT, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_ADD_CONSENT, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.changeStatus_D_Incomplete_Pending, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.changeStatus_D_Pending_Incomplete, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.changeStatus_D_Rejected_Pending, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.changeStatus_D_Pending_Approved, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.changeStatus_D_Pending_Rejected, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.changeStatus_D_Approved_Pending, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.changeDocumentStatus_Incomplete_Pending, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.changeDocumentStatus_Pending_Incomplete, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.changeDocumentStatus_Rejected_Pending, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.changeDocumentStatus_Pending_Approved, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.changeDocumentStatus_Pending_Rejected, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.changeDocumentStatus_Approved_Pending, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GENERATE_REPORT, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GENERATE_TREND_REPORT, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GENERATE_DYNAMIC_REPORT, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GENERATE_DYNAMIC_TRENDS_REPORT, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GENERATE_DYNAMIC_RECORD_REPORT, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GENERATE_MANAGEMENT_REPORT, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_VIEW_MGMT_REPORT, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_STATUS_REPORT, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_REPORTS_OF_TYPE, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_REPORT, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_GROUPS_FOR_CODES, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_DOWNLOAD_EXPORT, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_MY_EXPORT_REQUESTS, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_REQUEST_EXPORT, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_EXECUTE_QUERY, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_SAVE_QUERY, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_MY_QUERIES, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_QUERY, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_CANCEL_EXPORT, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_IDENTIFIERS, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_ESL_RETRIEVE_NHS_NUMBERS, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_ESL_LOOKUP_RANDOMISATION_RESULT_FOR_DATE, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_RS_LOOKUP_RANDOMIZATION_RESULT_FOR_DATE, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_TRANSFORM, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_ESL_EXPORT, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_DELETED_RECORDS_BY_GROUPS, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_UPDATE_RECORD_METADATA, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_SYNC_DOC_STAT_WITH_PRIMARY, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_UPDATE_PRIMARY_IDENTIFIER, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_UPDATE_SECONDARY_IDENTIFIER, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_LINKABLE_RECORDS, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_LINKED_RECORDS, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_CONSENT_AND_STATUS_INFO_FOR_GROUPS, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_DOCUMENTS, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_ENTRIES, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_ESL_LOCK_SUBJECT, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_ESL_GET_PROPERTY, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_DOC_STANDARD, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_VIEW_TREATMENT, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_EDIT_TREATMENT, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_EDIT_DOC, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_DOC_VIEW_IDENTITY, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_EDIT_ENTRY, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_AUDIT_BY_PROJECT, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_AUDIT_BY_RECORD, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_PROVENANCE_FOR_CHANGE, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_STATUS_ID_FOR_DOCUMENT, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_0, RBACAction.ACTION_EXPORT_UNRESTRICTED, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_1, RBACAction.ACTION_EXPORT_UNRESTRICTED, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_2, RBACAction.ACTION_EXPORT_UNRESTRICTED, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_3, RBACAction.ACTION_EXPORT_UNRESTRICTED, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_4, RBACAction.ACTION_EXPORT_UNRESTRICTED, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_5, RBACAction.ACTION_EXPORT_UNRESTRICTED, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_6, RBACAction.ACTION_EXPORT_TRANSFORMED, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_7, RBACAction.ACTION_EXPORT_TRANSFORMED, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_8, RBACAction.ACTION_EXPORT_TRANSFORMED, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_9, RBACAction.ACTION_EXPORT_TRANSFORMED, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_10, RBACAction.ACTION_EXPORT_TRANSFORMED, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_11, RBACAction.ACTION_EXPORT_RESTRICTED, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_12, RBACAction.ACTION_EXPORT_RESTRICTED, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_13, RBACAction.ACTION_EXPORT_RESTRICTED, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_14, RBACAction.ACTION_EXPORT_RESTRICTED, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_15, RBACAction.ACTION_EXPORT_RESTRICTED, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.changeStatus_D_Incomplete_Complete, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.changeStatus_D_Complete_Incomplete, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.changeStatus_D_Complete_Pending, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.changeStatus_D_Complete_Controlled, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.changeDocumentStatus_Incomplete_Complete, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.changeDocumentStatus_Complete_Incomplete, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.changeDocumentStatus_Complete_Pending, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.changeDocumentStatus_Complete_Controlled, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_ESL_UNLOCK_SUBJECT, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_CAN_RECORD_BE_RANDOMIZED, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_SAVE_QUERY, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_MY_QUERIES, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_EXECUTE_QUERY, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_QUERY, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_VIEW_SAMPLES_CONFIG, RBACRole.ClinicalResearchManager));
	
	//Group statements for ClinicalResearchManager
	
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_CRM_REPORT, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_DOC_STANDARD_INST, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_VIEW_TREATMENT_INST, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_EDIT_TREATMENT_INST, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_EDIT_DOC_INST, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_DOC_VIEW_IDENTITY_INST, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_EDIT_ENTRY_RESPONSE, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_VIEW_SAMPLES, RBACRole.ClinicalResearchManager));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_EDIT_SAMPLES, RBACRole.ClinicalResearchManager));
	////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////
	//Statements for ProjectAdministrator
	
	//Non-group statements for ProjectAdministrator
	
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GENERATE_TREND_REPORT, RBACRole.ProjectAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GENERATE_DYNAMIC_REPORT, RBACRole.ProjectAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GENERATE_DYNAMIC_TRENDS_REPORT, RBACRole.ProjectAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GENERATE_DYNAMIC_RECORD_REPORT, RBACRole.ProjectAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GENERATE_MANAGEMENT_REPORT, RBACRole.ProjectAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_VIEW_MGMT_REPORT, RBACRole.ProjectAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_STATUS_REPORT, RBACRole.ProjectAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_REPORTS_OF_TYPE, RBACRole.ProjectAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_REPORT, RBACRole.ProjectAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_GROUPS_FOR_CODES, RBACRole.ProjectAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_IDENTIFIERS, RBACRole.ProjectAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_ESL_RETRIEVE_NHS_NUMBERS, RBACRole.ProjectAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_ESL_LOOKUP_RANDOMISATION_RESULT_FOR_DATE, RBACRole.ProjectAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_RS_LOOKUP_RANDOMIZATION_RESULT_FOR_DATE, RBACRole.ProjectAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_DOCUMENTS, RBACRole.ProjectAdministrator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_ENTRIES, RBACRole.ProjectAdministrator));
	
	//Group statements for ProjectAdministrator
	
	////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////
	//Statements for DataImporter
	
	//Non-group statements for DataImporter
	
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_IMPORT_DATA, RBACRole.DataImporter));
	
	//Group statements for DataImporter
	
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_IMPORT_DATA, RBACRole.DataImporter));
	////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////
	//Statements for ClinicalResearchOfficer
	
	//Non-group statements for ClinicalResearchOfficer
	
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_ESL_RETRIEVE_PROJECT, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_ESL_RETRIEVE_PROJECT_BY_CODE, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_ESL_FIND_SUBJECT_BY_EXAMPLE, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_ESL_LOOKUP_STUDY_NUMBER, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_RS_ALLOCATE, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DCC_AUTO_GENERATE_IDENTIFIERS, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GENERATE_DYNAMIC_REPORT, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GENERATE_DYNAMIC_RECORD_REPORT, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_STATUS_REPORT, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_REPORTS_OF_TYPE, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_REPORT, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_GROUPS_FOR_CODES, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_DOWNLOAD_EXPORT, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_MY_EXPORT_REQUESTS, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_IDENTIFIERS, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_TRANSFORM, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_DOC_STANDARD, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_VIEW_TREATMENT, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_EDIT_TREATMENT, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_EDIT_DOC, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_DOC_VIEW_IDENTITY, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_EDIT_ENTRY, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_0, RBACAction.ACTION_EXPORT_UNRESTRICTED, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_1, RBACAction.ACTION_EXPORT_UNRESTRICTED, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_2, RBACAction.ACTION_EXPORT_UNRESTRICTED, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_3, RBACAction.ACTION_EXPORT_UNRESTRICTED, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_4, RBACAction.ACTION_EXPORT_UNRESTRICTED, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_5, RBACAction.ACTION_EXPORT_UNRESTRICTED, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_6, RBACAction.ACTION_EXPORT_TRANSFORMED, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_7, RBACAction.ACTION_EXPORT_TRANSFORMED, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_8, RBACAction.ACTION_EXPORT_TRANSFORMED, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_9, RBACAction.ACTION_EXPORT_TRANSFORMED, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_10, RBACAction.ACTION_EXPORT_TRANSFORMED, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_11, RBACAction.ACTION_EXPORT_RESTRICTED, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_12, RBACAction.ACTION_EXPORT_RESTRICTED, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_13, RBACAction.ACTION_EXPORT_RESTRICTED, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_14, RBACAction.ACTION_EXPORT_RESTRICTED, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_15, RBACAction.ACTION_EXPORT_RESTRICTED, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_VIEW_SAMPLES_CONFIG, RBACRole.ClinicalResearchOfficer));
	
	//Group statements for ClinicalResearchOfficer
	
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_ESL_SAVE_SUBJECT, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_ESL_RANDOMISE_SUBJECT, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_ESL_RETRIEVE_SUBJECT, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_ESL_RETRIEVE_SUBJECT_BY_STUDY_NUMBER, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_ESLW_LOOKUP_STUDY_NUMBER, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_CHANGE_RECORD_STATUS, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_GET_BINARY_DATA, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.getRecordsDocumentsByStatus_Incomplete, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.getRecordsDocumentsByStatus_Rejected, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_GET_RECORD_COMPLETE, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_GET_RECORD_SUMMARY, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_GET_RECORD_SINGLE_DOCUMENT, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_GET_RECORDS_WITH_CONSENT_BY_GROUPS, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_GET_RECORDS_BY_GROUPS, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.getRecordsByGroupsAndDocStatus_Incomplete, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.getRecordsByGroupsAndDocStatus_Rejected, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_GENERATE_IDENTIFIERS, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_SAVE_RECORD, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_WITHDRAW_CONSENT, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_ADD_CONSENT, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.changeStatus_D_Incomplete_Pending, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.changeStatus_D_Pending_Incomplete, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.changeStatus_D_Rejected_Pending, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.changeDocumentStatus_Incomplete_Pending, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.changeDocumentStatus_Pending_Incomplete, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.changeDocumentStatus_Rejected_Pending, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_GENERATE_REPORT, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_REQUEST_EXPORT, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_CANCEL_EXPORT, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_ESL_EXPORT, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_GET_DELETED_RECORDS_BY_GROUPS, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_UPDATE_RECORD_METADATA, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_SYNC_DOC_STAT_WITH_PRIMARY, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_UPDATE_PRIMARY_IDENTIFIER, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_UPDATE_SECONDARY_IDENTIFIER, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_GET_LINKABLE_RECORDS, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_GET_LINKED_RECORDS, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_GET_CONSENT_AND_STATUS_INFO_FOR_GROUPS, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_ESL_LOCK_SUBJECT, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_ESL_GET_PROPERTY, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_DOC_STANDARD_INST, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_VIEW_TREATMENT_INST, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_EDIT_TREATMENT_INST, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_EDIT_DOC_INST, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_DOC_VIEW_IDENTITY_INST, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_EDIT_ENTRY_RESPONSE, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_GET_STATUS_ID_FOR_DOCUMENT, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.changeStatus_D_Incomplete_Complete, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.changeStatus_D_Complete_Incomplete, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.changeStatus_D_Complete_Pending, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.changeStatus_D_Complete_Controlled, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.changeDocumentStatus_Incomplete_Complete, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.changeDocumentStatus_Complete_Incomplete, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.changeDocumentStatus_Complete_Pending, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.changeDocumentStatus_Complete_Controlled, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_ESL_UNLOCK_SUBJECT, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_CAN_RECORD_BE_RANDOMIZED, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_SAVE_QUERY, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_GET_MY_QUERIES, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_EXECUTE_QUERY, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_GET_QUERY, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_VIEW_SAMPLES, RBACRole.ClinicalResearchOfficer));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_EDIT_SAMPLES, RBACRole.ClinicalResearchOfficer));
	////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////
	//Statements for DataAnalyst
	
	//Non-group statements for DataAnalyst
	
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_BINARY_DATA, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_RECORD_COMPLETE, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_RECORD_SUMMARY, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_RECORD_SINGLE_DOCUMENT, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_RECORDS_WITH_CONSENT_BY_GROUPS, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_RECORDS_BY_GROUPS, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GENERATE_REPORT, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GENERATE_TREND_REPORT, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GENERATE_DYNAMIC_REPORT, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GENERATE_DYNAMIC_TRENDS_REPORT, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GENERATE_DYNAMIC_RECORD_REPORT, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_VIEW_MGMT_REPORT, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_STATUS_REPORT, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_REPORTS_OF_TYPE, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_REPORT, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_GROUPS_FOR_CODES, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_DOWNLOAD_EXPORT, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_MY_EXPORT_REQUESTS, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_SAVE_QUERY, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_MY_QUERIES, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_QUERY, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_IDENTIFIERS, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_DELETED_RECORDS_BY_GROUPS, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_LINKABLE_RECORDS, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_LINKED_RECORDS, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_CONSENT_AND_STATUS_INFO_FOR_GROUPS, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_DOCUMENTS, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_ENTRIES, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_DOC_STANDARD, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_DOC_BLIND, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_VIEW_TREATMENT, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_STATUS_ID_FOR_DOCUMENT, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_0, RBACAction.ACTION_EXPORT_UNRESTRICTED, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_1, RBACAction.ACTION_EXPORT_UNRESTRICTED, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_2, RBACAction.ACTION_EXPORT_UNRESTRICTED, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_3, RBACAction.ACTION_EXPORT_UNRESTRICTED, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_4, RBACAction.ACTION_EXPORT_UNRESTRICTED, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_5, RBACAction.ACTION_EXPORT_UNRESTRICTED, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_6, RBACAction.ACTION_EXPORT_TRANSFORMED, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_7, RBACAction.ACTION_EXPORT_TRANSFORMED, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_8, RBACAction.ACTION_EXPORT_TRANSFORMED, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_9, RBACAction.ACTION_EXPORT_TRANSFORMED, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_10, RBACAction.ACTION_EXPORT_TRANSFORMED, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_11, RBACAction.ACTION_EXPORT_RESTRICTED, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_12, RBACAction.ACTION_EXPORT_RESTRICTED, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_13, RBACAction.ACTION_EXPORT_RESTRICTED, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_14, RBACAction.ACTION_EXPORT_RESTRICTED, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_15, RBACAction.ACTION_EXPORT_RESTRICTED, RBACRole.DataAnalyst));
	
	//Group statements for DataAnalyst
	
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_REQUEST_EXPORT, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_EXECUTE_QUERY, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_CANCEL_EXPORT, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_ESL_EXPORT, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_UPDATE_SECONDARY_IDENTIFIER, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_DOC_STANDARD_INST, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_DOC_BLIND_INST, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_VIEW_TREATMENT_INST, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_SAVE_QUERY, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_GET_MY_QUERIES, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_EXECUTE_QUERY, RBACRole.DataAnalyst));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_GET_QUERY, RBACRole.DataAnalyst));
	////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////
	//Statements for DELViewer
	
	//Non-group statements for DELViewer
	
	
	//Group statements for DELViewer
	
	////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////
	//Statements for ChiefInvestigator
	
	//Non-group statements for ChiefInvestigator
	
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_BINARY_DATA, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_RECORD_COMPLETE, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_RECORD_SUMMARY, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_RECORD_SINGLE_DOCUMENT, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_RECORDS_WITH_CONSENT_BY_GROUPS, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_RECORDS_BY_GROUPS, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GENERATE_REPORT, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GENERATE_TREND_REPORT, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GENERATE_DYNAMIC_REPORT, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GENERATE_DYNAMIC_TRENDS_REPORT, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GENERATE_DYNAMIC_RECORD_REPORT, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GENERATE_MANAGEMENT_REPORT, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_UKCRN_REPORT, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_CHIEF_INVESTIGATOR_REPORT, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_VIEW_MGMT_REPORT, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_STATUS_REPORT, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_REPORTS_OF_TYPE, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_REPORT, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_GROUPS_FOR_CODES, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_DOWNLOAD_EXPORT, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_MY_EXPORT_REQUESTS, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_REQUEST_EXPORT, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_EXECUTE_QUERY, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_SAVE_QUERY, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_MY_QUERIES, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_QUERY, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_CANCEL_EXPORT, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_IDENTIFIERS, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_ESL_RETRIEVE_NHS_NUMBERS, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_ESL_LOOKUP_RANDOMISATION_RESULT_FOR_DATE, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_RS_LOOKUP_RANDOMIZATION_RESULT_FOR_DATE, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_TRANSFORM, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_ESL_EXPORT, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_DELETED_RECORDS_BY_GROUPS, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_LINKABLE_RECORDS, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_LINKED_RECORDS, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_CONSENT_AND_STATUS_INFO_FOR_GROUPS, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_DOCUMENTS, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_ENTRIES, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_STATUS_ID_FOR_DOCUMENT, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_0, RBACAction.ACTION_EXPORT_UNRESTRICTED, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_1, RBACAction.ACTION_EXPORT_UNRESTRICTED, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_2, RBACAction.ACTION_EXPORT_UNRESTRICTED, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_3, RBACAction.ACTION_EXPORT_UNRESTRICTED, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_4, RBACAction.ACTION_EXPORT_UNRESTRICTED, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_5, RBACAction.ACTION_EXPORT_UNRESTRICTED, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_6, RBACAction.ACTION_EXPORT_TRANSFORMED, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_7, RBACAction.ACTION_EXPORT_TRANSFORMED, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_8, RBACAction.ACTION_EXPORT_TRANSFORMED, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_9, RBACAction.ACTION_EXPORT_TRANSFORMED, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_10, RBACAction.ACTION_EXPORT_TRANSFORMED, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_11, RBACAction.ACTION_EXPORT_RESTRICTED, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_12, RBACAction.ACTION_EXPORT_RESTRICTED, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_13, RBACAction.ACTION_EXPORT_RESTRICTED, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_14, RBACAction.ACTION_EXPORT_RESTRICTED, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.EXPORT_LEVEL_15, RBACAction.ACTION_EXPORT_RESTRICTED, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_SAVE_QUERY, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_MY_QUERIES, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_EXECUTE_QUERY, RBACRole.ChiefInvestigator));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_QUERY, RBACRole.ChiefInvestigator));
	
	//Group statements for ChiefInvestigator
	
	////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////
	//Statements for SystemAdministrator
	
	//Non-group statements for SystemAdministrator
	
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ANY, RBACRole.SystemAdministrator));
	
	//Group statements for SystemAdministrator
	
	////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////
	//Statements for DELAuthor
	
	//Non-group statements for DELAuthor
	
	
	//Group statements for DELAuthor
	
	////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////
	//Statements for ANY
	
	//Non-group statements for ANY
	
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_ESL_EXISTS, RBACRole.ANY));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_RS_GET_RANDOMIZER_STATISTICS, RBACRole.ANY));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_DATASET_COMPLETE, RBACRole.ANY));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_DATASET_SUMMARY, RBACRole.ANY));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_STANDARD_CODES, RBACRole.ANY));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_REPORTS_BY_DATASET, RBACRole.ANY));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_ESL_IS_PROJECT_RANDOMISED, RBACRole.ANY));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_ESL_RETRIEVE_SUBJECT_RANDOMISATION_EVENTS, RBACRole.ANY));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_RS_GET_SUBJECT_RANDOMIZATION_EVENTS, RBACRole.ANY));
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_GET_GROUPS, RBACRole.ANY));
	
	//Group statements for ANY
	
	////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////
	//Statements for ViewIdentity
	
	//Non-group statements for ViewIdentity
	
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.ANY, RBACAction.ACTION_DR_DOC_VIEW_IDENTITY, RBACRole.ViewIdentity));
	
	//Group statements for ViewIdentity
	
	additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction.ACTION_DR_DOC_VIEW_IDENTITY_INST, RBACRole.ViewIdentity));
	////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////
	//Statements for ScientificResearchOfficer
	
	//Non-group statements for ScientificResearchOfficer
	
	
	//Group statements for ScientificResearchOfficer
	
	////////////////////////////////////////////////


	return additionalStatements;

	}

}

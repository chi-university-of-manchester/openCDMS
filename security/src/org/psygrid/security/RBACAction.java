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


// Created on Oct 10, 2006 by John Ainsworth

package org.psygrid.security;

import org.psygrid.security.accesscontrol.AEFAction;
import org.psygrid.www.xml.security.core.types.ActionType;

/**
 * @author jda
 *
 */
public enum RBACAction {
	
	ANY(0, null),
	
	ACTION_AA_ADD_USER(1, null),
    ACTION_AA_MODIFY_USER(2, null),
    ACTION_AA_MODIFY_PROJECT(3, null),
    ACTION_AA_DELETE_USER(4, null),
    ACTION_AA_UPDATE_CONFIGURATION(5, null),
    ACTION_AA_RETRIEVE_CONFIGURATION(6, null),
    ACTION_AA_DELETE_PROJECT(7, null),
    ACTION_AA_ADD_PROJECT(8, null),
    
    ACTION_PA_MODIFY_POLICY(9, null),
    ACTION_PA_UPDATE_CONFIGURATION(10, null),
    ACTION_PA_RETRIEVE_CONFIGURATION(11, null), 
    ACTION_PA_ADD_POLICY(12, null),
    ACTION_PA_GET_POLICY(13, null),
    ACTION_PA_DELETE_POLICY(14, null),

    ACTION_DR_GENERATE_REPORT(15, null),
    ACTION_DR_GET_REPORTS_BY_DATASET(16, null),
    ACTION_DR_SAVE_REPORT(17, null),
    ACTION_DR_ADD_CONSENT(18, null),
    ACTION_DR_ADD_IDENTIFIER(19, null),
    ACTION_DR_EXPORT_DATASET_AS_CSV(20, null),
    ACTION_DR_GENERATE_IDENTIFIERS(21, null),
    ACTION_DR_GET_BINARY_DATA(22, null),
    ACTION_DR_GET_DATASET_COMPLETE(23, null),
    ACTION_DR_GET_DATASET_SUMMARY(24, null),
    ACTION_DR_GET_IDENTIFIERS(25, null),
    ACTION_DR_GET_MODIFIED_DATASETS(26, null),
    ACTION_DR_GET_RECORD_COMPLETE(27, null),
    ACTION_DR_GET_RECORDS(28, null),
    ACTION_DR_GET_RECORDS_BY_GROUPS(29, null),
    ACTION_DR_GET_RECORD_SINGLE_DOCUMENT(30, null),
    ACTION_DR_MARK_RESPONSE_AS_INVALID(31, null),
    ACTION_DR_MARK_RESPONSE_AS_VALID(32, null),   
    ACTION_DR_PATCH_DATASET(33, null),
    ACTION_DR_PUBLISH_DATASET(34, null),

	changeStatus_D_Incomplete_Pending(35, null),
	changeStatus_D_Pending_Incomplete(36, null),
	changeStatus_D_Pending_Approved(37, null),
	changeStatus_D_Pending_Rejected(38, null),
	changeStatus_D_Approved_Pending(39, null),
	changeStatus_D_Rejected_Pending(40, null),
	
    changeDocumentStatus_Incomplete_Pending(41, null),
    changeDocumentStatus_Pending_Incomplete(42, null),
    changeDocumentStatus_Pending_Approved(43, null),
    changeDocumentStatus_Pending_Rejected(44, null),
    changeDocumentStatus_Approved_Pending(45, null),
    changeDocumentStatus_Rejected_Pending(46, null),
    
    ACTION_DCC_AUTO_GENERATE_IDENTIFIERS(47, null),

    ACTION_AA_GET_USERS_AND_PRIVILEGES_IN_PROJECT(48, null),
    ACTION_AA_GET_USERS(49, null),

    ACTION_DR_GET_RECORDS_BY_STATUS(50, null),

    
    /*
     * 
     * 51-53 missing
     * 
     */
    
    getRecordsByGroupsAndDocStatus_Incomplete(54, null),
    getRecordsByGroupsAndDocStatus_Pending(55, null),
    getRecordsByGroupsAndDocStatus_Approved(56, null),
    getRecordsByGroupsAndDocStatus_Rejected(57, null),
   
    getRecordsDocumentsByStatus_Incomplete(58, null),
    getRecordsDocumentsByStatus_Pending(59, null),
    getRecordsDocumentsByStatus_Approved(60, null),
    getRecordsDocumentsByStatus_Rejected(61, null),

    ACTION_DR_REMOVE_PUBLISHED_DATASET(62, null),
    ACTION_DR_SAVE_DATASET(63, null),
    ACTION_DR_SAVE_RECORD(64, null),
    ACTION_DR_SAVE_RECORD_AS_USER(65, null),
    ACTION_DR_WITHDRAW_CONSENT(66, null),
    ACTION_DR_REMOVE_DATASET(67, null),
    
	ACTION_ESL_SAVE_PROJECT (68, null),
	ACTION_ESL_RETRIEVE_PROJECT (69, null),
	ACTION_ESL_RETRIEVE_PROJECT_BY_CODE (70, null),
	ACTION_ESL_RETRIEVE_ALL_PROJECTS (71, null),
	ACTION_ESL_SAVE_GROUP (72, null),
	ACTION_ESL_SAVE_SUBJECT (73, null),
	ACTION_ESL_RANDOMISE_SUBJECT(74, null),
	ACTION_ESL_RETRIEVE_SUBJECT (75, null),
	ACTION_ESL_RETRIEVE_SUBJECT_BY_STUDY_NUMBER (76, null),
	ACTION_ESL_LOOKUP_STUDY_NUMBER (77, null),
	ACTION_ESL_LOOKUP_RANDOMISATION_RESULT (78, null),
	ACTION_ESL_EXISTS (79, null),
	ACTION_ESL_EMERGENCY_BREAK_IN (80, null),
	ACTION_ESL_SETUP_RANDOMISATION (81, null),
	ACTION_ESL_GET_VERSION (82, null),
	
	ACTION_RS_SAVE_RANDOMIZER (83, null),
	ACTION_RS_ALLOCATE (84, null),
	ACTION_RS_CHECK_INTEGRITY (85, null),
	ACTION_RS_GET_ALLOCATION (86, null),
	ACTION_RS_GET_ALLOCATIONS (87, null),
	ACTION_RS_GET_RANDOMIZER_STATISTICS (88, null),
	ACTION_RS_GET_VERSION (89, null),
	
	ACTION_ESLW_LOOKUP_RANDOMIZATION_RESULT (90, null),
	ACTION_ESLW_LOOKUP_STUDY_NUMBER (91, null),
	
	ACTION_DR_GET_RECORDS_WITH_CONSENT_BY_GROUPS (92, null),
	ACTION_ESL_FIND_SUBJECT_BY_EXAMPLE(93, null),
	ACTION_DR_GET_RECORD_SUMMARY (94, null),
	
	/*
	 * 
	 * 95 - 102 EDIE state changes 
	 * 
	 */
	
	ACTION_DR_GET_ALL_REPORTS_BY_DATASET(103, null),
	ACTION_DR_DELETE_REPORT(104, null),
	
	ACTION_ESL_LOOKUP_RANDOMIZER_STATISTICS(105, null),
	ACTION_ESL_LOOKUP_STRATIFIED_RANDOMIZER_STATISTICS(106, null),
	
	ACTION_ESL_IS_PROJECT_RANDOMISED(107, null),
	ACTION_ESL_RETRIEVE_SUBJECT_RANDOMISATION_EVENTS(108, null),
	ACTION_RS_GET_SUBJECT_RANDOMIZATION_EVENTS(109, null),
	
	ACTION_DR_GENERATE_TREND_REPORT(110, null),         //CPM, PIs, RMs, DAs
	ACTION_DR_GENERATE_MANAGEMENT_REPORT(111, null),	//CPM, PIs 
	ACTION_DR_GENERATE_DYNAMIC_REPORT(112, null),		//CPM, PIs, RecruitmentManagers (no groups), DAs
	
	ACTION_DR_REQUEST_EXPORT(113, null), //CPM, PI, CI, DA
	ACTION_DR_GET_MY_EXPORT_REQUESTS(114, null), //CPM, PI, CI, DA
	ACTION_DR_DOWNLOAD_EXPORT(115, null), //CPM, PI, CI, DA
	
	ACTION_DR_GENERATE_DYNAMIC_RECORD_REPORT(116, null), //CPM, PIs, RMs, DAs (no groups) 
	ACTION_DR_GET_REPORTS_OF_TYPE(117, null),  //CPM, PIs, RMs, DAs (no groups)
	ACTION_DR_GET_GROUPS_FOR_CODES(118, null), //CPM, PIs, RMs, DAs (no groups) ?
	ACTION_DR_GET_REPORT(119, null),  //CPM, PIs, RMs, DAs (no groups)
	ACTION_DR_CHANGE_RECORD_STATUS(120, null),
	
	ACTION_AA_GET_USER(121, null),
	
	ACTION_DR_GET_STANDARD_CODES(122, null),
	ACTION_DR_CANCEL_EXPORT(123, null),
	
	ACTION_AA_LDAP_QUERY(124, null),
	
	ACTION_ESL_RETRIEVE_NHS_NUMBERS(125, null),						//req'd for receiving treatment chart
	ACTION_ESL_LOOKUP_RANDOMISATION_RESULT_FOR_DATE(126, null),		//req'd for receiving treatment chart
	ACTION_RS_LOOKUP_RANDOMIZATION_RESULT_FOR_DATE(127, null),		//req'd for receiving treatment chart
	ACTION_AA_RESET_PASSWORD(128, null),
	
	ACTION_DR_TRANSFORM(129, null),
	
	//EXPORT SECURITY ACTIONS
	ACTION_EXPORT_RESTRICTED(130, null), //Defn: before exporting, data must be assigned standard code indicating that it is restricted (raw data not exported)
	ACTION_EXPORT_TRANSFORMED(131, null), //Defn: before exporting, data must be transformed first
	ACTION_EXPORT_UNRESTRICTED(132, null), //Defn: export data as-is in the database.
	//END EXPORT SECURITY ACTIONS
	
	ACTION_DR_DELETE_RECORD(133, null),
	ACTION_ESL_EXPORT(134, null),
	
	ACTION_DR_GET_GROUPS(135, null),
	
	ACTION_DR_GET_DELETED_RECORDS_BY_GROUPS(136, null),
	ACTION_DR_UPDATE_RECORD_METADATA(137, null),
	
	ACTION_DR_SYNC_DOC_STAT_WITH_PRIMARY(138, null),
	ACTION_DR_UPDATE_PRIMARY_IDENTIFIER(139, null),
	ACTION_DR_UPDATE_SECONDARY_IDENTIFIER(140, null),
	ACTION_DR_GET_LINKABLE_RECORDS(141, null),
	
	/* Actions for specific reports */
	ACTION_DR_INVESTIGATOR_REPORT(142, null),	//used to specify that PI and NIs are to be emailed particular report	
	ACTION_DR_CHIEF_INVESTIGATOR_REPORT(143, null),	
	ACTION_DR_CRM_REPORT(144, null),	
	ACTION_DR_RECRUITMENT_REPORT(145, null),	
	ACTION_DR_UKCRN_REPORT(146, null),
	
	ACTION_DR_GET_CONSENT_AND_STATUS_INFO_FOR_GROUPS(147, null),
	ACTION_DR_GET_DOCUMENTS(148, null),			//Required for generating the StdCodeStatusChart via psygrid-web
	ACTION_DR_GET_ENTRIES(149, null),			//Required for generating the BasicStatsChart via psygrid-web
	
	ACTION_ESL_LOCK_SUBJECT(150, null),
	ACTION_ESL_GET_PROPERTY(151, null),
	ACTION_ESL_DELETE_PROJECT(152, null),

	/* Actions for specifying access to documents */
	ACTION_DR_DOC_STANDARD(153, null),			//Normal data entry done by a CRO
	ACTION_DR_DOC_STANDARD_INST(154, null),		//Normal data entry done by a CRO (document instances)
	ACTION_DR_DOC_BLIND(155, null),				//Documents to be viewable by unblinded individuals only (e.g TAs)
	ACTION_DR_DOC_BLIND_INST(156, null),		//Documents to be viewable by unblinded individuals only (e.g TAs) (document instances)
	
	ACTION_DR_STATUS_REPORT(157, null),			//For viewing document and record status reports
	ACTION_DR_VIEW_MGMT_REPORT(158, null),		//For viewing management reports through psygrid-web
	ACTION_DR_GENERATE_DYNAMIC_TRENDS_REPORT(159, null),	//For viewing trends reports through psygrid-web
	
	ACTION_RS_DELETE_RANDOMIZATION(160, null), 		//action to delete a randomizer
	ACTION_RS_GET_RANDOMIZER(161, null), 			//action to get randomizer
	ACTION_DR_GET_LINKED_RECORDS(162, null),
	
	/* Actions for specifying access to documents and entries */
	ACTION_DR_EDIT_DOC(163, null),				    //Specifies who can edit a document
	ACTION_DR_EDIT_DOC_INST(164, null),				//Specifies who can edit a document instance (inc groups)
	ACTION_DR_EDIT_ENTRY(165, null),				//Restricts who can edit a particular entry
	ACTION_DR_EDIT_ENTRY_RESPONSE(166, null),		//Restricts who can edit a particular response to an entry
	ACTION_DR_VIEW_ENTRY(167, null),				//Restricts who can see a particular entry
	ACTION_DR_VIEW_ENTRY_RESPONSE(168, null),		//Restricts who can see a particular response to an entry

	
	/* Data Element Library */	
	ACTION_DEL_SAVE_NEW_ELEMENT(169, null),
	ACTION_DEL_GET_ELEMENT_AS_REPOSITORY_TEMPLATE(170, null),
	ACTION_DEL_IMPORT_DATA_ELEMENT(171, null),
	ACTION_DEL_REVISE_ELEMENT(172, null),
	ACTION_DEL_GET_METADATA(173, null),
	ACTION_DEL_SOPHISTICATED_SEARCH_BY_TYPE_AND_NAME(174, null),
	ACTION_DEL_GET_DOCUMENTS_SUMMARY_INFO(175, null),
	ACTION_DEL_GET_LSID_AUTHORITY_LIST(176, null),
	ACTION_DEL_APPROVE_ELEMENT(177, null),
	ACTION_DEL_REPORT_ELEMENT_STATUS_CHANGES(178, null),
	
	ACTION_DR_AUDIT_BY_PROJECT(179, null),
	ACTION_DR_AUDIT_BY_RECORD(180, null),
	ACTION_DR_GET_PROVENANCE_FOR_CHANGE(181, null),
    ACTION_DR_REQUEST_IMMEDIATE_EXPORT(182, null),
    
    ACTION_DR_GET_STATUS_ID_FOR_DOCUMENT(183, null),
    
	changeStatus_D_Incomplete_Complete(184, null),
	changeStatus_D_Complete_Incomplete(185, null),
	changeStatus_D_Complete_Pending(186, null),
	changeStatus_D_Complete_Controlled(187, null),
	
    changeDocumentStatus_Incomplete_Complete(188, null),
    changeDocumentStatus_Complete_Incomplete(189, null),
    changeDocumentStatus_Complete_Pending(190, null),
    changeDocumentStatus_Complete_Controlled(191, null),
	
    ACTION_DR_VIEW_TREATMENT(192, null),
	ACTION_DR_VIEW_TREATMENT_INST(193, null),
	ACTION_DR_EDIT_TREATMENT(194, null),
	ACTION_DR_EDIT_TREATMENT_INST(195, null),
	
	ACTION_ESL_UNLOCK_SUBJECT(196, null),
	ACTION_DR_CAN_RECORD_BE_RANDOMIZED(197, null),
	ACTION_DR_GENERATE_MGMT_REPORT_BY_ID(198, null),
		
	ACTION_DR_SAVE_QUERY(199, null),
	ACTION_DR_GET_MY_QUERIES(200, null),
	ACTION_DR_EXECUTE_QUERY(201, null),
	ACTION_DR_GET_QUERY(202, null),
	ACTION_DR_IMPORT_DATA(203, null),

	// Actions to view documents that contain participant identifying information
	ACTION_DR_DOC_VIEW_IDENTITY(204,null),
	ACTION_DR_DOC_VIEW_IDENTITY_INST(205,null),
	
	ACTION_DR_VIEW_SAMPLES(206, null),
	ACTION_DR_EDIT_SAMPLES(207, null),
	ACTION_DR_VIEW_SAMPLES_CONFIG(208, null),
	ACTION_DR_EDIT_SAMPLES_CONFIG(209, null),
	
	//Actions required for studies that utilise the medication distribution service
	ACTION_MD_ALLOCATE_MEDS(210, null),
	ACTION_MD_MEDS_PHARMACY_WORKFLOW(211, null), //Distribute, VET, Return
	ACTION_MD_MEDS_WORKFLOW_CORRECTION(212, null),
	ACTION_MD_VIEW_MEDS_PACKAGE(213, null), //request to view individual package or multiple packages from the same pharmacy.
	ACTION_MD_CREATE_MEDS_PROJECT(214, null),
	ACTION_MD_IMPORT_MED_PACKAGE(215, null),
	
	ACTION_ESL_DELETE_SUBJECT(216, null),
		
	ACTION_MD_VIEW_PROJECT_PACKAGES(217, null),
	ACTION_MD_SAVE_MEDS_PROJECT(218, null), //Need to be system administrator for this.
	ACTION_MD_ADD_PHARMACY_TO_MEDS_PROJECT(219, null),
	ACTION_MD_REQUEST_MEDS_EXPORT_FOR_PROJECT(220, null);
	
	
	private final int id;
	
	private final String alias;

	RBACAction(int id, String alias) {
		this.id = id;
		this.alias = alias;
	}

	public int id() {
		return id;
	}
	
	public String alias(){
		return alias;
	}
	
	public String idAsString(){
		return new Integer(id).toString();
	}

	public ActionType toActionType(){
		return new ActionType(toString(), idAsString());
	}
	
	public AEFAction toAEFAction(){
		return new AEFAction(toString(), idAsString());
	}
	
	public static ActionType[] allActions(){
		RBACAction[] rbacaa = RBACAction.values();
		ActionType[] lat = new ActionType[rbacaa.length];
		for(int i=0; i <rbacaa.length; i++){
			lat[i]=new ActionType(rbacaa[i].toString(),rbacaa[i].idAsString());
		}
		return lat;
	}
	public static ActionType[] noActions(){
		return new ActionType[]{};
	}
}

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
package org.psygrid.data.export;

import java.io.IOException;
import java.net.ConnectException;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.data.export.dto.ExportRequest;
import org.psygrid.data.export.hibernate.ExportSecurityActionMap;
import org.psygrid.data.export.hibernate.ExternalQuery;
import org.psygrid.data.export.security.DataExportActions;
import org.psygrid.data.export.security.ExportSecurityValues;
import org.psygrid.data.model.dto.DataSetDTO;
import org.psygrid.data.model.hibernate.RetrieveDepth;
import org.psygrid.data.repository.RepositoryNoSuchDatasetFault;
import org.psygrid.data.repository.RepositoryServiceFault;
import org.psygrid.data.repository.dao.DAOException;
import org.psygrid.data.repository.dao.NoDatasetException;
import org.psygrid.data.utils.security.NotAuthorisedFault;
import org.psygrid.data.utils.service.AbstractServiceImpl;
import org.psygrid.logging.AuditLogger;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.PGSecurityInvalidSAMLException;
import org.psygrid.security.PGSecuritySAMLVerificationException;
import org.psygrid.security.RBACAction;
import org.psygrid.security.RBACTarget;
import org.psygrid.security.accesscontrol.AEFAction;
import org.psygrid.security.accesscontrol.AEFGroup;
import org.psygrid.security.accesscontrol.AEFProject;

/**
 * Implementation of export service.
 * 
 * @author Terry Child
 *
 */
public class ExportServiceImpl extends AbstractServiceImpl implements ExportServiceInternal {

	/**
	 * Name of the component, used for audit logging
	 */
	private static final String COMPONENT_NAME = "ExportService";

	/**
	 * General purpose logger
	 */
	private static Log sLog = LogFactory.getLog(ExportServiceImpl.class);

	/**
	 * Audit logger
	 */
	private static AuditLogger logHelper = new AuditLogger(ExportServiceImpl.class);

	private ExportDAO exportDAO;
	
	protected String getComponentName() {
		return COMPONENT_NAME;
	}
	
	public ExportDAO getExportDAO() {
		return exportDAO;
	}

	public void setExportDAO(ExportDAO exportDAO) {
		this.exportDAO = exportDAO;
	}

	public void requestExport(ExportRequest exportRequest, String saml) throws RemoteException, RepositoryServiceFault, NotAuthorisedFault, RepositoryNoSuchDatasetFault {

		final String METHOD_NAME = "requestExport";
		
		try{

			org.psygrid.data.export.hibernate.ExportRequest export = exportRequest.toHibernate();
			
			String[] groups = exportRequest.getGroups();
			String projectCode = exportRequest.getProjectCode();

			checkPermissionsByGroups(saml, "requestExport", RBACAction.ACTION_DR_REQUEST_EXPORT, projectCode, groups);
			
			List<ExportSecurityActionMap> actionsMap = new ArrayList<ExportSecurityActionMap>();

			DataSetDTO dataSet = repositoryDAO.getSummaryForProjectCode(projectCode, RetrieveDepth.REP_SAVE);
			if(dataSet.getExportSecurityActive()){
				retrieveExportSecurityActions(actionsMap, projectCode, saml);
			}

			String userName = findUserName(saml);
			export.setRequestor(userName);
			export.setActionsMap(actionsMap);
			
			exportDAO.requestExport(export, dataSet.getExportSecurityActive());
		}
		catch(PGSecurityInvalidSAMLException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw new NotAuthorisedFault("The supplied SAML assertion has expired", ex);
		}
		catch(PGSecuritySAMLVerificationException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw new NotAuthorisedFault("The supplied SAML assertion does not come from a trusted issuer", ex);
		}
		catch(PGSecurityException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw new NotAuthorisedFault("An error occurred during authorisation", ex);
		}
		catch(NoDatasetException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw new RepositoryNoSuchDatasetFault(ex);
		}
	}
	
	
	public void requestImmediateExport(ExportRequest exportRequest, String saml)
			throws RemoteException, RepositoryServiceFault, NotAuthorisedFault,
			RepositoryNoSuchDatasetFault {

		final String METHOD_NAME = RBACAction.ACTION_DR_REQUEST_IMMEDIATE_EXPORT
				.toString();

		org.psygrid.data.export.hibernate.ExportRequest export = exportRequest
				.toHibernate();

		String[] groups = exportRequest.getGroups();
		String projectCode = exportRequest.getProjectCode();

		try {
			String userName = findUserName(saml);
			String callerIdentity = accessControl.getCallersIdentity();
			logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, userName,
					callerIdentity);

			List<String> authorisedGroups = new ArrayList<String>();
			for (int i = 0; i < groups.length; i++) {
				if (!accessControl.authoriseUser(saml, new AEFGroup(null,
						groups[i], null),
						RBACAction.ACTION_DR_REQUEST_IMMEDIATE_EXPORT
								.toAEFAction(), new AEFProject(null,
								projectCode, false))) {
					logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME,
							userName, callerIdentity);
				} else {
					authorisedGroups.add(groups[i]);
				}
			}
			if (0 == authorisedGroups.size()) {
				throw new NotAuthorisedFault("User '" + userName
						+ "' is not authorised to perform the action '"
						+ METHOD_NAME + "' for project '" + projectCode + "'");
			}

			List<ExportSecurityActionMap> actionsMap = new ArrayList<ExportSecurityActionMap>();

			DataSetDTO dataSet = repositoryDAO.getSummaryForProjectCode(
					projectCode, RetrieveDepth.REP_SAVE);

			if (dataSet.getExportSecurityActive()) {
				retrieveExportSecurityActions(actionsMap, projectCode, saml);
			}
			export.setRequestor(userName);
			export.setActionsMap(actionsMap);

			exportDAO.requestExport(export,dataSet.getExportSecurityActive());
		} catch (PGSecurityInvalidSAMLException ex) {
			sLog.error(METHOD_NAME + ": " + ex.getClass().getSimpleName(), ex);
			throw new NotAuthorisedFault(
					"The supplied SAML assertion has expired", ex);
		} catch (PGSecuritySAMLVerificationException ex) {
			sLog.error(METHOD_NAME + ": " + ex.getClass().getSimpleName(), ex);
			throw new NotAuthorisedFault(
					"The supplied SAML assertion does not come from a trusted issuer",
					ex);
		} catch (PGSecurityException ex) {
			sLog.error(METHOD_NAME + ": " + ex.getClass().getSimpleName(), ex);
			throw new NotAuthorisedFault(
					"An error occurred during authorisation", ex);
		} catch (NoDatasetException ex) {
			sLog.error(METHOD_NAME + ": " + ex.getClass().getSimpleName(), ex);
			throw new RepositoryNoSuchDatasetFault(ex);
		}

	}	
		
	public org.psygrid.data.export.dto.ExportRequest[] getMyExportRequests(String[] projects, String saml) throws RemoteException, RepositoryServiceFault, NotAuthorisedFault {

		final String METHOD_NAME = "getMyExportRequests";

		try{
			String userName = findUserName(saml);
			String callerIdentity = accessControl.getCallersIdentity();
			logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);

			List<String> authorisedProjects = new ArrayList<String>();
			for ( int i=0; i<projects.length; i++ ){
				if ( !accessControl.authoriseUser(saml, new AEFGroup(), 
						RBACAction.ACTION_DR_GET_MY_EXPORT_REQUESTS.toAEFAction(), 
						new AEFProject(null, projects[i], false) ) ){
					logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);
				}
				else{
					authorisedProjects.add(projects[i]);
				}
			}
			if ( 0 == authorisedProjects.size() ){
				throw new NotAuthorisedFault(
						"User '"+userName+"' is not authorised to perform the action '"+METHOD_NAME+"'");            
			}

			return exportDAO.getRequestsForUser(authorisedProjects, userName);
		}
		catch(PGSecurityInvalidSAMLException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw new NotAuthorisedFault("The supplied SAML assertion has expired", ex);
		}
		catch(PGSecuritySAMLVerificationException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw new NotAuthorisedFault("The supplied SAML assertion does not come from a trusted issuer", ex);
		}
		catch(PGSecurityException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw new NotAuthorisedFault("An error occurred during authorisation", ex);
		}
	}
	
	
	public byte[] downloadExport(long exportRequestId, String saml) throws RemoteException, RepositoryServiceFault, NotAuthorisedFault, NoSuchExportFault {

		try{
			//find the project of the export request
			String project = exportDAO.getProjectForExportRequest(exportRequestId);

			checkPermissionsByProject(saml,"downloadExport",RBACAction.ACTION_DR_DOWNLOAD_EXPORT,project);		

			String userName = findUserName(saml);
			return exportDAO.getCompletedExport(userName, exportRequestId);
		}
		catch(NoSuchExportException ex){
			sLog.error(ex.getMessage(),ex);
			throw new NoSuchExportFault(ex.getMessage(), ex);
		}
	}

	public byte[] downloadExportHash(long exportRequestId, String format, String saml) throws RemoteException, RepositoryServiceFault, NotAuthorisedFault, NoSuchExportFault {

		try{
			//find the project of the export request
			String project = exportDAO.getProjectForExportRequest(exportRequestId);
			
			checkPermissionsByProject(saml,"downloadExportHash",RBACAction.ACTION_DR_DOWNLOAD_EXPORT,project);		
			String userName = findUserName(saml);
			return exportDAO.getCompletedExportHash(userName, format, exportRequestId);
		}
		catch(NoSuchExportException ex){
			sLog.error(ex.getMessage(),ex);
			throw new NoSuchExportFault(ex.getMessage(), ex);
		}
	}
	
	
	public void cancelExport(long exportRequestId, String saml) 
	throws RemoteException, RepositoryServiceFault, NotAuthorisedFault, UnableToCancelExportFault {

		try{
			//find the project of the export request
			String project = exportDAO.getProjectForExportRequest(exportRequestId);

			checkPermissionsByProject(saml,"cancelExport",RBACAction.ACTION_DR_CANCEL_EXPORT,project);		

			String userName = findUserName(saml);
			exportDAO.deleteExportRequest(userName, exportRequestId);
		}
		catch(UnableToCancelExportException ex){
			sLog.error(ex.getMessage(),ex);
			throw new UnableToCancelExportFault(ex.getMessage(), ex);
		}
	}
	
	
	/**
	 * 
	 * @param actionsMap - the tag-to-security_action mapping (populated by this method)
	 * @param projectCode - the project code, identifying the dataset, for which the policy queries will be made
	 * @param saml - the assertion string uniquely identifying the user for which policy queries will be made
	 * @throws IllegalArgumentException
	 * @throws PGSecurityException
	 * @throws PGSecurityInvalidSAMLException
	 * @throws PGSecuritySAMLVerificationException
	 */
	private void retrieveExportSecurityActions(List<ExportSecurityActionMap> actionsMap, String projectCode, String saml) throws IllegalArgumentException, PGSecurityException, PGSecurityInvalidSAMLException, PGSecuritySAMLVerificationException {

		List<ExportSecurityValues> values = java.util.Arrays.asList(ExportSecurityValues.values());

		for(ExportSecurityValues tag:values){

			//Convert the security tag to its equivalent PGSecurityConstant.
			RBACTarget rbacTag = RBACTarget.valueOf(tag.toString());

			//Now setup the inner loop - the possible actions - from most permissive to least permissive...
			//The higher the number of the action, the MORE permissive it is...

			int actionSize = java.util.Arrays.asList(DataExportActions.values()).size();
			ListIterator<DataExportActions> exportActionsIterator = java.util.Arrays.asList(DataExportActions.values()).listIterator(actionSize);
			while (exportActionsIterator.hasPrevious()){
				DataExportActions action = exportActionsIterator.previous();

				//Convert the action to its RBACAction equivalent...
				RBACAction rbacAction = RBACAction.valueOf(action.toString());


				if(accessControl.authoriseUser(saml, 
						new AEFGroup(rbacTag.toString(), Integer.toString(rbacTag.ordinal()), null), //The group will become the target when the logic queries the policy authority...
						new AEFAction(rbacAction.toString(), Integer.toString(rbacAction.ordinal())),
						new AEFProject(null, projectCode, false) ) ) {

					actionsMap.add(new ExportSecurityActionMap(tag, action));
					break;
				}
				else {
					//There's nothing to do...
				}
			}

		}

	}

	// Methods used for scheduled exports
	
	public ExportRequest getNextPendingRequest(boolean immediate) throws RemoteException, ConnectException {
		return exportDAO.getNextPendingRequest(immediate);
	}

	public void updateRequestStatus(Long requestId, String newStatus) throws RemoteException, ConnectException {
		exportDAO.updateRequestStatus(requestId, newStatus);
	}

	public void generateHashes(String filePath, String md5Path, String shaPath) throws IOException, NoSuchAlgorithmException {
		exportDAO.generateHashes(filePath, md5Path, shaPath);
	}

	public void updateRequestSetComplete(Long requestId, String path,String md5Path, String shaPath) throws DAOException {
		exportDAO.updateRequestSetComplete(requestId, path, md5Path, shaPath);
	}

	public ExternalQuery[] getExternalQueries(String projectCode) {
		return exportDAO.getExternalQueries(projectCode);
	}


}


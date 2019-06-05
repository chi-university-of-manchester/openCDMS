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
package org.psygrid.data;

import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.rpc.ServiceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.psygrid.data.dao.DataElementDAO;
import org.psygrid.data.dao.ElementAuthorityNotRecognizedException;
import org.psygrid.data.dao.ElementRevisionException;
import org.psygrid.data.dao.ElementStatusChangeException;
import org.psygrid.data.dao.NoSuchElementException;
import org.psygrid.data.dao.UnknownNativeRelationship;
import org.psygrid.data.model.FailedTestException;
import org.psygrid.data.model.RepositoryModelException;
import org.psygrid.data.model.dto.AdminInfo;
import org.psygrid.data.model.dto.DELQueryObject;
import org.psygrid.data.model.dto.DataElementContainerDTO;
import org.psygrid.data.model.dto.DocumentDTO;
import org.psygrid.data.model.dto.ElementMetaDataDTO;
import org.psygrid.data.model.dto.ElementStatusContainer;
import org.psygrid.data.model.dto.LSIDAuthorityDTO;
import org.psygrid.data.model.hibernate.DataElementAction;
import org.psygrid.data.model.hibernate.DataElementStatus;
import org.psygrid.data.model.hibernate.LSID;
import org.psygrid.data.model.hibernate.LSIDException;
import org.psygrid.data.repository.dao.RelationshipReconstitutionException;
import org.psygrid.data.utils.security.NotAuthorisedFault;
import org.psygrid.logging.AuditLogger;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.PGSecurityInvalidSAMLException;
import org.psygrid.security.PGSecuritySAMLVerificationException;
import org.psygrid.security.RBACAction;
import org.psygrid.security.accesscontrol.AEFGroup;
import org.psygrid.security.accesscontrol.AEFProject;
import org.psygrid.services.SecureSoapBindingImpl;
import org.springframework.context.ApplicationContext;

public class DataElementSoapBindingImpl extends SecureSoapBindingImpl implements
DataElement {

	/**
	 * Name of the component, used for audit logging
	 */
	private static final String COMPONENT_NAME = "DEL";

	private static boolean useSecurity = true;

	private DataElementDAO dataElementDao = null;

	/**
	 * General purpose logger
	 */
	private static Log sLog = LogFactory.getLog(DataElementSoapBindingImpl.class);

	/**
	 * Audit logger
	 */
	private static AuditLogger logHelper = new AuditLogger(DataElementSoapBindingImpl.class);

	private final AEFGroup GROUP = new AEFGroup(null, "default", null);

	@Override
	protected void onInit() throws ServiceException {
		super.onInit();
		ApplicationContext ctx = getWebApplicationContext();
		dataElementDao = (DataElementDAO)ctx.getBean("myDataElementDAO");
	}


	public String saveNewElement(DataElementContainerDTO element, AdminInfo info, final String authority, String saml) 
	throws RemoteException, DELFailedTestException,
	NotAuthorisedFault, DELServiceFault, ElementAuthorityNotRecognizedException {
		final String METHOD_NAME = RBACAction.ACTION_DEL_SAVE_NEW_ELEMENT.toString();


		try{
			if(useSecurity){
				String userName = findUserName(saml);
				String callerIdentity = accessControl.getCallersIdentity();			
				info.setWho(callerIdentity);

				if ( !accessControl.authoriseUser(saml, GROUP, RBACAction.ACTION_DEL_SAVE_NEW_ELEMENT.toAEFAction(),  new AEFProject(null, authority, false) ) ){
					logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);
					throw new NotAuthorisedFault("User '"+userName+"' is not authorised to perform the action '"+METHOD_NAME+"' for project "+authority);
				}

				if(sLog.isInfoEnabled()){
					sLog.info("Element being saved. Element name is " + element.toHibernate().getElementName() + ". Element description is " + element.toHibernate().getElementDescription() + ". User is " +
							callerIdentity + ". Authority is " + authority + ".");
				}
			}

			org.psygrid.data.model.dto.LSIDDTO lsid = dataElementDao.saveDataElement(element, info.toHibernate(), authority, false);
			return lsid.toString();
		} catch (HibernateException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(), e);
			throw new DELServiceFault(e.getMessage(), e);
		} catch (UnknownNativeRelationship e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(), e);
			throw new DELServiceFault(e.getMessage(), e);
		} catch (PGSecurityException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new NotAuthorisedFault("An error occurred during authorisation", e);
		} catch (PGSecurityInvalidSAMLException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new NotAuthorisedFault("The supplied SAML assertion has expired", e);
		} catch (PGSecuritySAMLVerificationException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new NotAuthorisedFault("The supplied SAML assertion does not come from a trusted issuer", e);
		} catch (UnsupportedEncodingException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new DELServiceFault(e.getMessage(), e);
		} catch (LSIDException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new DELServiceFault(e.getMessage(), e);
		} catch (FailedTestException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new DELFailedTestException(e.getMessage(), e);
		} catch (ElementAuthorityNotRecognizedException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw e;
		} catch (Exception e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new DELServiceFault(e.getMessage(), e);
		} 
	}

	public DataElementContainerDTO getElementAsRepositoryTemplate(String lsid, String saml) throws RemoteException, NotAuthorisedFault, DELServiceFault {
		//Retrieve all constituents, and zero all DB references.

		final String METHOD_NAME = RBACAction.ACTION_DEL_GET_ELEMENT_AS_REPOSITORY_TEMPLATE.toString();

		DataElementContainerDTO elem = null;
		try{
			LSID lsidObj = LSID.valueOf(lsid);
			String authority = lsidObj.getAuthorityId();

			List<String> authorities = this.getAllAuthoritiesInDatabase();
			UserPrivilegeMatrix upm = new UserPrivilegeMatrix(saml);

			if(useSecurity){
				String userName = findUserName(saml);
				String callerIdentity = accessControl.getCallersIdentity();			

				if ( !accessControl.authoriseUser(saml, GROUP, RBACAction.ACTION_DEL_GET_ELEMENT_AS_REPOSITORY_TEMPLATE.toAEFAction(), new AEFProject(null, authority, false)) ){
					logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);
					throw new NotAuthorisedFault("User '"+userName+"' is not authorised to perform the action '"+METHOD_NAME+"' for project "+authority);
				}

				for(String authorityStr: authorities){
					boolean isAuthor = accessControl.authoriseUser(saml, GROUP, RBACAction.ACTION_DEL_SAVE_NEW_ELEMENT.toAEFAction(), new AEFProject(null, authorityStr, false));
					upm.specifyIsAuthorForAuthority(authorityStr, isAuthor);			
				}
			}else{
				for(String authorityStr: authorities){
					//Assume they have authorship in each authority.
					upm.specifyIsAuthorForAuthority(authorityStr, true);
				}
			}

			if(sLog.isInfoEnabled()){
				sLog.info("Element being retrieved as repository template. Element lsid is " + lsid + ".");
			}

			elem = dataElementDao.getElementAndConstituents(lsid, true, true, upm);			

		}catch (HibernateException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(), ex);
			throw new DELServiceFault(ex);
		} catch (RelationshipReconstitutionException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(), e);
			throw new DELServiceFault(e);
		} catch (NoSuchElementException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(), e);
			throw new DELServiceFault(e);
		} catch (LSIDException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new DELServiceFault(e.getMessage(), e);
		} catch (PGSecurityException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new NotAuthorisedFault("An error occurred during authorisation", e);
		} catch (PGSecurityInvalidSAMLException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new NotAuthorisedFault("The supplied SAML assertion has expired", e);
		} catch (PGSecuritySAMLVerificationException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new NotAuthorisedFault("The supplied SAML assertion does not come from a trusted issuer", e);
		}  catch (Exception e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new DELServiceFault(e.getMessage(), e);
		} 

		return elem;
	}

	public String[] getElementTypes() throws RemoteException, NotAuthorisedFault {
		//Returns all the possible element types in the data element library schema.
		//Does not necessarily mean that there are instances of these types in the database.
		String [] elementTypes = {"DataSet", "Document", "Entry", "BasicEntry", "BooleanEntry", "DateEntry",
				"DerivedEntry", "ExternalDerivedEntry", "IntegerEntry", "LongTextEntry", "NumericEntry",
				"OptionEntry", "TextEntry", "CompositeEntry", "NarrativeEntry", "DateValidationRule", "IntegerValidationRule",
				"NumericValidationRule", "TextValidationRule"};
		return elementTypes;
	}

	public void addAuthority(final String authority, String saml) throws RemoteException, NotAuthorisedFault, DELServiceFault{
		final String METHOD_NAME = "addAuthority";

		try {

			if(useSecurity){
				String userName = findUserName(saml);
				String callerIdentity = accessControl.getCallersIdentity();

				if ( !accessControl.authoriseUser(saml, GROUP, RBACAction.ACTION_DEL_IMPORT_DATA_ELEMENT.toAEFAction(), new AEFProject(null, authority, false)) ){
					logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);
					throw new NotAuthorisedFault("User '"+userName+"' is not authorised to perform the action '"+METHOD_NAME+"' for project "+authority);
				}
			}

			dataElementDao.insertLSIDAuthority(authority);

		} catch (PGSecurityException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new NotAuthorisedFault(e.getMessage(), e);
		} catch (PGSecurityInvalidSAMLException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new  NotAuthorisedFault(e.getMessage(), e);
		} catch (PGSecuritySAMLVerificationException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new  NotAuthorisedFault(e.getMessage(), e);
		} catch (HibernateException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new DELServiceFault(e.getMessage(), e);
		} catch (LSIDException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new DELServiceFault(e.getMessage(), e);
		} catch (Exception e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new DELServiceFault(e.getMessage(), e);
		} 

	}

	public String importDataElement(DataElementContainerDTO element, AdminInfo info, final String authority, String saml) throws RemoteException, DELServiceFault, NotAuthorisedFault, ElementAuthorityNotRecognizedException {
		final String METHOD_NAME = RBACAction.ACTION_DEL_IMPORT_DATA_ELEMENT.toString();

		boolean allowImportSecurity = true;

		try{
			if(useSecurity && !allowImportSecurity){
				String userName = findUserName(saml);
				String callerIdentity = accessControl.getCallersIdentity();	
				info.setWho(callerIdentity);

				if ( !accessControl.authoriseUser(saml, GROUP, RBACAction.ACTION_DEL_IMPORT_DATA_ELEMENT.toAEFAction(), new AEFProject(null, authority, false)) ){
					logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);
					throw new NotAuthorisedFault("User '"+userName+"' is not authorised to perform the action '"+METHOD_NAME+"' for project "+authority);
				}

				if(sLog.isInfoEnabled()){
					sLog.info("Element being imported. Element name is " + element.toHibernate().getElementName() + ". Element description is " + element.toHibernate().getElementDescription() + ". User is " +
							callerIdentity + ".");
				}
			}

			info.setActionTaken(DataElementAction.IMPORT.toString());
			org.psygrid.data.model.dto.LSIDDTO lsid = dataElementDao.saveDataElement(element, info.toHibernate(), authority, true);

			Runtime r = Runtime.getRuntime();
			long availMem = r.freeMemory();
			r.gc();

			return lsid.toString();
		} catch (HibernateException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new DELServiceFault(e.getMessage(), e);
		} catch (UnknownNativeRelationship e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new DELServiceFault(e.getMessage(), e);
		} catch (PGSecurityException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new NotAuthorisedFault("An error occurred during authorisation", e);
		} catch (PGSecurityInvalidSAMLException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new NotAuthorisedFault("The supplied SAML assertion has expired", e);
		} catch (PGSecuritySAMLVerificationException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new NotAuthorisedFault("The supplied SAML assertion does not come from a trusted issuer", e);
		} catch (UnsupportedEncodingException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new DELServiceFault(e.getMessage(), e);
		} catch (LSIDException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new DELServiceFault(e.getMessage(), e);
		} catch (FailedTestException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new DELServiceFault(e.getMessage(), e);
		} catch (ElementAuthorityNotRecognizedException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw e;
		} catch (Exception e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new DELServiceFault(e.getMessage(), e);
		} 
	}


	public String reviseElement(DataElementContainerDTO elem, AdminInfo adminInfo, final String authority, String saml) throws RemoteException, NotAuthorisedFault, 
	DELServiceFault, ElementRevisionException, DELFailedTestException, ElementAuthorityNotRecognizedException, RepositoryModelException {
		String elementString;
		final String METHOD_NAME = RBACAction.ACTION_DEL_REVISE_ELEMENT.toString();

		try {

			if(useSecurity){
				String userName = findUserName(saml);
				String callerIdentity = accessControl.getCallersIdentity();	
				adminInfo.setWho(callerIdentity);

				//the authority, though being passed in, can be obtained from the element's
				//lsid. This is what should happen, because a revised element will never change
				//authorities in the process.

				if ( !accessControl.authoriseUser(saml, GROUP, RBACAction.ACTION_DEL_REVISE_ELEMENT.toAEFAction(), new AEFProject(null, authority, false)) ){
					logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);
					throw new NotAuthorisedFault("User '"+userName+"' is not authorised to perform the action '"+METHOD_NAME+"' for project "+authority);
				}
				if(sLog.isInfoEnabled()){
					sLog.info("Element being revised. Element lsid is " + elem.toHibernate().getElementLSID() + ". Element description is " + elem.toHibernate().getElementDescription() + ". User is " +
							callerIdentity + ". Element name is " + elem.toHibernate().getElementName() + ".");
				}
			}


			elementString = dataElementDao.reviseElement(elem, adminInfo.toHibernate(), saml);
			return elementString;
		} catch (UnknownNativeRelationship e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new DELServiceFault("Unrecognised element relationship!", e);
		} catch (PGSecurityException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new NotAuthorisedFault("An error occurred during authorisation", e);
		} catch (PGSecurityInvalidSAMLException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new NotAuthorisedFault("The supplied SAML assertion has expired", e);
		} catch (PGSecuritySAMLVerificationException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new NotAuthorisedFault("The supplied SAML assertion does not come from a trusted issuer", e);
		} catch (LSIDException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new DELServiceFault(e.getMessage(), e);
		} catch (UnsupportedEncodingException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new DELServiceFault(e.getMessage(), e);
		} catch (FailedTestException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new DELFailedTestException(e.getMessage(), e);
		} catch (ElementAuthorityNotRecognizedException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw e;
		} catch (RepositoryModelException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw e;
		} catch (Exception e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new DELServiceFault("General Exception", e);
		}
	}


	public ElementMetaDataDTO getMetaData(String lsid, String saml) throws RemoteException, NotAuthorisedFault, DELServiceFault {
		final String METHOD_NAME = RBACAction.ACTION_DEL_GET_METADATA.toString();
		ElementMetaDataDTO metaData = null;
		try{
			LSID lsidObj = LSID.valueOf(lsid);
			String authority = lsidObj.getAuthorityId();

			boolean retrieveFullHistory = true;

			if(useSecurity){
				List<String> authorities = getAllAuthoritiesInDatabase();
				String userName = findUserName(saml);
				String callerIdentity = accessControl.getCallersIdentity();	

				if ( authority == null ||
						!accessControl.authoriseUser(saml, GROUP, RBACAction.ACTION_DEL_GET_METADATA.toAEFAction(), new AEFProject(null, authority, false)) ){
					logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);
					throw new NotAuthorisedFault("User '"+userName+"' is not authorised to perform the action '"+METHOD_NAME+"' for project "+authority+" with the authority "+authority);
				}

				for(String authorityStr: authorities){
					boolean isAuthor = accessControl.authoriseUser(saml, GROUP, RBACAction.ACTION_DEL_SAVE_NEW_ELEMENT.toAEFAction(), new AEFProject(null, authorityStr, false));
					boolean isCurator = accessControl.authoriseUser(saml, GROUP, RBACAction.ACTION_DEL_APPROVE_ELEMENT.toAEFAction(), new AEFProject(null, authorityStr, false));

					if(isAuthor || isCurator){
						retrieveFullHistory = true;
					}else{
						//is neither the curator or the author, so must be a viewer
						//(implied from the fact that we've already passed access control).
						retrieveFullHistory = false;
					}

				}
			}

			if(sLog.isInfoEnabled()){
				sLog.info("Retrieving metadata for element lsid: "+lsid);
			}

			//TODO:DEL need to determine whether or not to get the full revision history!
			//Right now, we are by default, but later we'll do this according to the role of the
			//user within the authority of the lsid for which they're requesting metadata.

			//If the user is a an author or a curator, allow them the full history.
			//If only a viewer, only allow them to see approved states.

			metaData = dataElementDao.getMetaData(lsid, retrieveFullHistory);
			return metaData;
		}catch(HibernateException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw new DELServiceFault(ex);
		} catch (RelationshipReconstitutionException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new DELServiceFault(e);
		} catch (NoSuchElementException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new DELServiceFault(e);
		} catch (LSIDException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new DELServiceFault(e.getMessage(), e);
		} catch (PGSecurityException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new NotAuthorisedFault("An error occurred during authorisation", e);
		} catch (PGSecurityInvalidSAMLException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new NotAuthorisedFault("The supplied SAML assertion has expired", e);
		} catch (PGSecuritySAMLVerificationException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new NotAuthorisedFault("The supplied SAML assertion does not come from a trusted issuer", e);
		} catch (Exception e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new DELServiceFault(e.getMessage(), e);
		} 
	}

	public DELQueryObject sophisticatedSearchByTypeAndName(DELQueryObject manager, String saml) 
	throws RemoteException, NotAuthorisedFault, DELServiceFault {
		final String METHOD_NAME = RBACAction.ACTION_DEL_SOPHISTICATED_SEARCH_BY_TYPE_AND_NAME.toString();

		DELQueryObject returnObject = null;
		try {
			List<String> authorities = manager.getAuthorityFilterLSIDs();
			if (authorities == null) {
				authorities = new ArrayList<String>();
			}
			if (authorities.size() == 0) {
				//No authorities listed so retrieving the ones the user has access to
				LSIDAuthorityDTO[] auths = this.getLSIDAuthorityList(saml);
				for (LSIDAuthorityDTO a: auths) {
					authorities.add(a.getAuthorityID());
				}
				manager.setAuthorityFilterLSIDs(authorities);
			}

			if(useSecurity){
				String userName = findUserName(saml);
				String callerIdentity = accessControl.getCallersIdentity();	

				for (String authority: authorities) {
					if ( !accessControl.authoriseUser(saml, GROUP, RBACAction.ACTION_DEL_SOPHISTICATED_SEARCH_BY_TYPE_AND_NAME.toAEFAction(), new AEFProject(null, authority, false) ) ){
						logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);
						throw new NotAuthorisedFault("User '"+userName+"' is not authorised to perform the action '"+METHOD_NAME+"' for project "+authority);
					}
				}
			}
			if(sLog.isInfoEnabled()){
				sLog.info("About to execute a query. Query criteria is " + manager.getSearchCriteria() + 
						". Query type is " + manager.getSearchType() + ". Element type is " + manager.getElementType() + ".");
			}


			returnObject =  dataElementDao.sophisticatedGetElementByTypeAndName(manager);
			return returnObject;
		} catch (HibernateException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new DELServiceFault(e);
		} catch (NoSuchElementException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new DELServiceFault(e);
		} catch (RelationshipReconstitutionException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new DELServiceFault(e);
		} catch (LSIDException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new DELServiceFault(e.getMessage(), e);
		} catch (PGSecurityException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new NotAuthorisedFault("An error occurred during authorisation", e);
		} catch (PGSecurityInvalidSAMLException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new NotAuthorisedFault("The supplied SAML assertion has expired", e);
		} catch (PGSecuritySAMLVerificationException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new NotAuthorisedFault("The supplied SAML assertion does not come from a trusted issuer", e);
		} catch (Exception e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new DELServiceFault(e.getMessage(), e);
		} 
	}


	public DocumentDTO[] getDocumentsSummaryInfo(final String authority, final String saml) 
	throws RemoteException, NotAuthorisedFault, DELServiceFault {
		final String METHOD_NAME = RBACAction.ACTION_DEL_GET_DOCUMENTS_SUMMARY_INFO.toString();

		try {

			if(useSecurity){
				String userName = findUserName(saml);
				String callerIdentity = accessControl.getCallersIdentity();	

				if ( !accessControl.authoriseUser(saml, GROUP, RBACAction.ACTION_DEL_GET_DOCUMENTS_SUMMARY_INFO.toAEFAction(), new AEFProject(null, authority, false) ) ){
					logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);
					throw new NotAuthorisedFault("User '"+userName+"' is not authorised to perform the action '"+METHOD_NAME+"' for project "+authority);
				}
			}

			if(sLog.isInfoEnabled()){
				sLog.info("Getting documents Summary Info.");
			}
			return dataElementDao.getDocumentsSummaryInfo(authority);

		} catch (HibernateException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new DELServiceFault(e);
		} catch (PGSecurityException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new NotAuthorisedFault("An error occurred during authorisation", e);
		} catch (PGSecurityInvalidSAMLException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new NotAuthorisedFault("The supplied SAML assertion has expired", e);
		} catch (PGSecuritySAMLVerificationException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new NotAuthorisedFault("The supplied SAML assertion does not come from a trusted issuer", e);
		} catch (Exception e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new DELServiceFault(e.getMessage(), e);
		} 
	}

	private List<String> getAllAuthoritiesInDatabase(){
		List<String> authorities = new ArrayList<String>();
		org.psygrid.data.model.hibernate.LSIDAuthority[] authorityArray = dataElementDao.getLSIDAuthorityList();
		for(int i = 0; i < authorityArray.length; i++){
			authorities.add(authorityArray[i].getAuthorityID());
		}
		return authorities;
	}

	public LSIDAuthorityDTO[] getLSIDAuthorityList(String saml) throws NotAuthorisedFault {
		final String METHOD_NAME = RBACAction.ACTION_DEL_GET_LSID_AUTHORITY_LIST.toString();

		if(useSecurity){
			org.psygrid.data.model.hibernate.LSIDAuthority[] authorityArray = dataElementDao.getLSIDAuthorityList();

			LSIDAuthorityDTO[] dtoList = new LSIDAuthorityDTO[authorityArray.length];
			for(int i = 0; i < dtoList.length; i++) {
				dtoList[i] = authorityArray[i].toDTO();
			}

			List<LSIDAuthorityDTO> allowed = new ArrayList<LSIDAuthorityDTO>(authorityArray.length);
			try {
				for (LSIDAuthorityDTO authority: dtoList) {
					try {
						if ( accessControl.authoriseUser(saml, GROUP, RBACAction.ACTION_DEL_GET_LSID_AUTHORITY_LIST.toAEFAction(), new AEFProject(null, authority.getAuthorityID(), false) ) ){
							allowed.add(authority);
						}
					} catch (PGSecurityException e) {
						continue;
					}

				}

				if(sLog.isInfoEnabled()){
					sLog.info("Getting allowed LSIDAuthorities");
				}

				LSIDAuthorityDTO[] dtoAllowed = allowed.toArray(new LSIDAuthorityDTO[allowed.size()]);
				return dtoAllowed;

			} catch (PGSecurityInvalidSAMLException e) {
				sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
				throw new NotAuthorisedFault("The supplied SAML assertion has expired", e);
			} catch (PGSecuritySAMLVerificationException e) {
				sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
				throw new NotAuthorisedFault("The supplied SAML assertion does not come from a trusted issuer", e);
			}

		}else{
			LSIDAuthorityDTO[] dtoAllowed = new LSIDAuthorityDTO[1];
			LSIDAuthorityDTO allowedAuthority = new LSIDAuthorityDTO("org.psygrid");
			dtoAllowed[0] = allowedAuthority;
			return dtoAllowed;
		}

	}

	/**
	 * This requires that the lsid of a currently pending element is provided, and that the element's submission context
	 * is 'root'. Otherwise, approval cannot be allowed.
	 * 
	 * If the pending element has subordinates that are also pending, then these will be set to
	 * 'approved' as well.
	 * @throws ElementStatusChangeException 
	 * 
	 */
	public void modifyElementStatus(String status, String lsid, AdminInfo info, String saml) throws RemoteException, NotAuthorisedFault, DELServiceFault, ElementStatusChangeException {

		final String METHOD_NAME = RBACAction.ACTION_DEL_APPROVE_ELEMENT.toString();

		//Extract the lsid authority from the lsid.
		LSID lsidObj = null;


		try {

			lsidObj = LSID.valueOf(lsid);

			if(useSecurity){
				String authority = lsidObj.getAuthorityId();
				String userName = findUserName(saml);
				String callerIdentity = accessControl.getCallersIdentity();	
				info.setWho(callerIdentity);

				if ( !accessControl.authoriseUser(saml, GROUP, RBACAction.ACTION_DEL_APPROVE_ELEMENT.toAEFAction(), new AEFProject(null, authority, false)) ){
					logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);
					throw new NotAuthorisedFault("User '"+userName+"' is not authorised to perform the action '"+METHOD_NAME+"' for project "+ authority);
				}
				if(sLog.isInfoEnabled()){
					sLog.info("Element being approved. Element lsid is " + lsid + ".");
				}
			}


			dataElementDao.modifyElementStatus(DataElementStatus.valueOf(status), lsid, info.toHibernate());

		} catch (PGSecurityException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new NotAuthorisedFault("An error occurred during authorisation", e);
		} catch (PGSecurityInvalidSAMLException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new NotAuthorisedFault("The supplied SAML assertion has expired", e);
		} catch (PGSecuritySAMLVerificationException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new NotAuthorisedFault("The supplied SAML assertion does not come from a trusted issuer", e);
		} catch (LSIDException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new DELServiceFault(e.getMessage(), e);
		} catch (ElementStatusChangeException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw e;
		} catch (Exception e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new DELServiceFault(e.getMessage(), e);
		} 
	}


	public ElementStatusContainer [] reportElementStatusChanges(ElementStatusContainer [] elementsInQuestion, boolean reportNonHeadRevisionElements, String saml) throws RemoteException, NotAuthorisedFault, DELServiceFault {

		final String METHOD_NAME = RBACAction.ACTION_DEL_REPORT_ELEMENT_STATUS_CHANGES.toString();
		try {
			if(useSecurity){
				Set<String> authorities = new HashSet<String>();
				for (ElementStatusContainer container: elementsInQuestion) {
					String lsid = container.getHeadRevisionLSID();
					try {	
						LSID lsidObj = LSID.valueOf(lsid);
						String authority = lsidObj.getAuthorityId();
						authorities.add(authority);
					} catch (LSIDException e) {
						sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
						throw new DELServiceFault(e.getMessage(), e);
					}
				}

				String userName = findUserName(saml);
				String callerIdentity = accessControl.getCallersIdentity();	

				for (String authority: authorities) {
					if ( !accessControl.authoriseUser(saml, GROUP, RBACAction.ACTION_DEL_REPORT_ELEMENT_STATUS_CHANGES.toAEFAction(), new AEFProject(null, authority, false)) ){
						logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);
						throw new NotAuthorisedFault("User '"+userName+"' is not authorised to perform the action '"+METHOD_NAME+"' for project "+ authority);
					}
				}
			}

			try {	
				return dataElementDao.reportElementStatusChanges(elementsInQuestion, reportNonHeadRevisionElements);
			} catch (LSIDException e) {
				sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
				throw new DELServiceFault(e.getMessage(), e);
			}
		} catch (PGSecurityException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new NotAuthorisedFault("An error occurred during authorisation", e);
		} catch (PGSecurityInvalidSAMLException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new NotAuthorisedFault("The supplied SAML assertion has expired", e);
		} catch (PGSecuritySAMLVerificationException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new NotAuthorisedFault("The supplied SAML assertion does not come from a trusted issuer", e);
		} catch (Exception e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new DELServiceFault(e.getMessage(), e);
		} 
	}

	public String[][] getLatestElementVersion(String[] lsids, String saml) throws RemoteException, NotAuthorisedFault, DELServiceFault {

		final String METHOD_NAME = "getLatestElementVersion";

		try {

			//Retrieve all of the authorities and put them in a unique list.
			Collection<String> uniqueAuthorityCollection = new HashSet<String>();
			int numLSIDs = lsids.length;
			for(int i = 0; i < numLSIDs; i++){
				LSID lsidObj = LSID.valueOf(lsids[i]);
				String authority = lsidObj.getAuthorityId();
				uniqueAuthorityCollection.add(authority);
			}

			UserPrivilegeMatrix upm = new UserPrivilegeMatrix(saml);

			if(useSecurity){

				String userName = findUserName(saml);
				String callerIdentity = accessControl.getCallersIdentity();	



				for(String authority : uniqueAuthorityCollection){
					if ( !accessControl.authoriseUser(saml, GROUP, RBACAction.ACTION_DEL_GET_ELEMENT_AS_REPOSITORY_TEMPLATE.toAEFAction(), new AEFProject(null, authority, false)) ){
						logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);
						throw new NotAuthorisedFault("User '"+userName+"' is not authorised to perform the action '"+METHOD_NAME+"' for project "+authority+" with the authority "+authority);
					}

					boolean isAuthor = accessControl.authoriseUser(saml, GROUP, RBACAction.ACTION_DEL_SAVE_NEW_ELEMENT.toAEFAction(), new AEFProject(null, authority, false));
					boolean isCurator = accessControl.authoriseUser(saml, GROUP, RBACAction.ACTION_DEL_APPROVE_ELEMENT.toAEFAction(), new AEFProject(null, authority, false));	
					boolean isAuthorOrIsCurator = false;

					if(isAuthor){
						isAuthorOrIsCurator = true;
						upm.specifyIsAuthorForAuthority(authority, true);
					}

					if(isCurator){
						isAuthorOrIsCurator = true;
						upm.specifyIsCuratorForAuthority(authority, true);
					}

					if(!isAuthorOrIsCurator){
						upm.specifyIsViewerForAuthority(authority, true);
					}

				}

			}


			//return a two-dimensional array.
			String[][] returnArray = new String[lsids.length][2];

			for(int i = 0; i < lsids.length; i++){

				LSID lsidObj = LSID.valueOf(lsids[i]);
				String authority = lsidObj.getAuthorityId();

				boolean omitPending;

				if(upm.getIsAuthorForAuthority(authority) || upm.getIsCuratorForAuthority(authority)){
					omitPending = false;
				}else if(upm.getIsViewerForAuthority(authority)){
					omitPending = true;
				}else{
					//upm not set - can happen when not using security.
					omitPending = false;
				}

				String latestVersion = dataElementDao.getCurrentRevisionLevel(lsids[i], omitPending);

				returnArray[i][0] = lsids[i];
				returnArray[i][1] = latestVersion;

			}


			return returnArray;

		} catch (HibernateException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new DELServiceFault(e.getMessage(), e);
		} catch (LSIDException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new DELServiceFault(e.getMessage(), e);
		} catch (PGSecurityException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new NotAuthorisedFault(e.getMessage(), e);
		} catch (PGSecurityInvalidSAMLException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new NotAuthorisedFault(e.getMessage(), e);
		} catch (PGSecuritySAMLVerificationException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new NotAuthorisedFault(e.getMessage(), e);
		} catch (Exception e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(),e);
			throw new DELServiceFault(e.getMessage(), e);
		} 
	}


}

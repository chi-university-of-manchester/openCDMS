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
package org.psygrid.data.utils.security;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.psygrid.data.model.dto.CompositeEntryDTO;
import org.psygrid.data.model.dto.DocumentDTO;
import org.psygrid.data.model.dto.EntryDTO;
import org.psygrid.data.model.hibernate.EditAction;
import org.psygrid.data.model.hibernate.Record;
import org.psygrid.data.repository.RepositoryServiceFault;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.PGSecurityInvalidSAMLException;
import org.psygrid.security.PGSecuritySAMLVerificationException;
import org.psygrid.security.RBACAction;
import org.psygrid.security.accesscontrol.AEFGroup;
import org.psygrid.security.accesscontrol.AEFProject;
import org.psygrid.security.accesscontrol.IAccessEnforcementFunction;

/**
 * Helper class to evaluate the security permissions for documents 
 * and document instances.
 * 
 * Used to calculate the documents and document instances to be
 * returned. Also used to calculate whether entries are viewable
 * and editable.
 * 
 * @author Lucy Bridges
 *
 */
public class DocumentSecurityHelper {

	/**
	 * String used to replace text responses where viewing
	 * is denied. 
	 */
	private static final String HIDDEN_VALUE = "*****";

	/**
	 * Access enforcement function
	 */
	protected IAccessEnforcementFunction accessController = null;

	// Make this thread local as it is set and used later in web service methods.
	private ThreadLocal<String> saml = new ThreadLocal<String>();

	/**
	 * Empty constructor needed for Spring dependency injection.
	 */
	public DocumentSecurityHelper() {
	}	
	
	/**
	 * @param accessController
	 */
	public DocumentSecurityHelper(IAccessEnforcementFunction accessController) {
		super();
		this.accessController = accessController;
	}

	public IAccessEnforcementFunction getAccessController() {
		return accessController;
	}

	public void setAccessController(IAccessEnforcementFunction accessController) {
		this.accessController = accessController;
	}

	public String getSaml() {
		return saml.get();
	}

	public void setSaml(String saml) {
		this.saml.set(saml);
	}

	/**
	 * Examine the documents provided and return the ones allowed by the user
	 *  
	 * @param documents
	 * @param projectCode
	 * @param user
	 * @return allowedDocuments
	 * @throws PGSecurityInvalidSAMLException
	 * @throws PGSecurityException
	 * @throws ConnectException
	 * @throws PGSecuritySAMLVerificationException
	 */
	public DocumentDTO[] getAllowedDocuments(DocumentDTO[] documents, String projectCode, String user) {
		try {
			if (documents == null) {
				return null;
			}
			List<DocumentDTO> allowed = new ArrayList<DocumentDTO>();
			for (DocumentDTO document: documents) {
				String action = document.getAction();
				document.setEditingPermitted(true);		//User can edit document by default
				if (action == null) {
					document = checkEntries(document, projectCode, user);
					allowed.add(document);
					
					String edit = document.getEditableAction();
					if (edit != null) {
						RBACAction editRbac = RBACAction.valueOf(edit);
						if (!accessController.authoriseUser(getSaml(), new AEFGroup(), editRbac.toAEFAction(), new AEFProject(null, projectCode, false)) ){
							document.setEditingPermitted(false);		//User is not permitted to edit document
						}
					}
					continue;
				}
				RBACAction rbac = RBACAction.valueOf(action);
				if (accessController.authoriseUser(getSaml(), new AEFGroup(), rbac.toAEFAction(), new AEFProject(null, projectCode, false)) ){
					document = checkEntries(document, projectCode, user);
					allowed.add(document);		//User can access document
	
					String edit = document.getEditableAction();
					if (edit != null) {
						RBACAction editRbac = RBACAction.valueOf(edit);
						if (!accessController.authoriseUser(getSaml(), new AEFGroup(), editRbac.toAEFAction(), new AEFProject(null, projectCode, false)) ){
							document.setEditingPermitted(false);		//User is not permitted to edit document
						}
					}
				}
			}
			DocumentDTO[] allowedDocs = new DocumentDTO[allowed.size()];
			for (int i=0; i<allowed.size(); i++) {
				allowedDocs[i] = allowed.get(i);
			}
			return allowedDocs;
		}
		catch(PGSecurityInvalidSAMLException ex){
			throw new NotAuthorisedFault("The supplied SAML assertion has expired", ex);
		}
		catch(PGSecuritySAMLVerificationException ex){
			throw new NotAuthorisedFault("The supplied SAML assertion does not come from a trusted issuer", ex);
		}
		catch(PGSecurityException ex){
			throw new NotAuthorisedFault("An error occurred during authorisation", ex);
		}
	}

	/**
	 * Check that the user can access/edit each entry and set the editing action.
	 * 
	 * @param document
	 * @param projectCode
	 * @param user
	 * @return document
	 * @throws PGSecurityInvalidSAMLException
	 * @throws PGSecurityException
	 * @throws ConnectException
	 * @throws PGSecuritySAMLVerificationException
	 */
	public DocumentDTO checkEntries(DocumentDTO document, String projectCode, String user) {
		if (document.getEntries() == null) {
			return document;
		}
		
		Map<String,Boolean> accessible = new HashMap<String,Boolean>();
		Map<String,Boolean> editable = new HashMap<String,Boolean>();

		//iterate through document  and retrieve unique entry actions
		for (EntryDTO entry: document.getEntries()) {
			accessible.put(entry.getAccessAction(), null);
			editable.put(entry.getEditableAction(), null);
		}

		
		try {
			//check security and record result for each entry
			for (String action: accessible.keySet()) {
				if (action != null) {
					RBACAction rbac = RBACAction.valueOf(action);
						accessible.put(action, accessController.authoriseUser(getSaml(), new AEFGroup(), rbac.toAEFAction(), new AEFProject(null, projectCode, false)));
				}
			}
			for (String action: editable.keySet()) {
				if (action != null) {
					RBACAction rbac = RBACAction.valueOf(action);
					editable.put(action, accessController.authoriseUser(getSaml(), new AEFGroup(), rbac.toAEFAction(), new AEFProject(null, projectCode, false)));
				}
			}
		}
		catch(PGSecurityInvalidSAMLException ex){
			throw new NotAuthorisedFault("The supplied SAML assertion has expired", ex);
		}
		catch(PGSecuritySAMLVerificationException ex){
			throw new NotAuthorisedFault("The supplied SAML assertion does not come from a trusted issuer", ex);
		}
		catch(PGSecurityException ex){
			throw new NotAuthorisedFault("An error occurred during authorisation", ex);
		}

		//iterate through doc again and set the non-persisted editingPermitted action as appropriate
		for (EntryDTO entry: document.getEntries()) {
			if (entry instanceof CompositeEntryDTO) {
				CompositeEntryDTO comp = (CompositeEntryDTO)entry;
				for (EntryDTO e: comp.getEntries()) {
					String accessAction = e.getAccessAction();
					String editAction   = e.getEditableAction();

					//Set writable by default
					e.setEditingPermitted(EditAction.READWRITE.toString());

					//If entry is not accessible, set as not viewable
					if (accessAction != null && !accessible.get(accessAction)) {
						e.setEditingPermitted(EditAction.DENY.toString());
					}
					//If entry is not editable set as read only
					else if (editAction != null && !editable.get(editAction)) {
						e.setEditingPermitted(EditAction.READONLY.toString());
					}
				}
			}

			String accessAction = entry.getAccessAction();
			String editAction   = entry.getEditableAction();

			//Set writable by default
			entry.setEditingPermitted(EditAction.READWRITE.toString());

			//If entry is not accessible, set as not viewable
			if (accessAction != null && !accessible.get(accessAction)) {
				entry.setEditingPermitted(EditAction.DENY.toString());
			}
			//If entry is not editable set as read only
			else if (editAction != null && !editable.get(editAction)) {
				entry.setEditingPermitted(EditAction.READONLY.toString());
			}
		}

		return document;
	}
	
	/**
	 * Examine the document instances provided and return only the ones allowed by the user.
	 * 
	 * @param documentInstances
	 * @param projectCode
	 * @param user
	 * @return allowedDocumentInstances
	 * @throws PGSecurityInvalidSAMLException
	 * @throws PGSecurityException
	 * @throws ConnectException
	 * @throws PGSecuritySAMLVerificationException
	 */
	public Set<org.psygrid.data.model.hibernate.DocumentInstance> getAllowedDocumentInstances(Set<org.psygrid.data.model.hibernate.DocumentInstance> documentInstances, String projectCode, String groupCode, String user) 
	throws DocumentSecurityFault {
		return this.getAllowedDocumentInstances(documentInstances, projectCode, groupCode, user, true);
	}
	
	/**
	 * Examine the document instances provided and return only the ones allowed by the user.
	 * 
	 * @param documentInstances
	 * @param projectCode
	 * @param groupCode
	 * @param user
	 * @param hideValues
	 * @return allowedDocumentInstances
	 * @throws PGSecurityInvalidSAMLException
	 * @throws PGSecurityException
	 * @throws ConnectException
	 * @throws PGSecuritySAMLVerificationException
	 */
	public Set<org.psygrid.data.model.hibernate.DocumentInstance> getAllowedDocumentInstances(Set<org.psygrid.data.model.hibernate.DocumentInstance> documentInstances, String projectCode, String groupCode, String user, boolean hideValues) 
	throws DocumentSecurityFault {
		org.psygrid.data.model.hibernate.DocumentInstance[] insts = new org.psygrid.data.model.hibernate.DocumentInstance[documentInstances.size()];		
		int i = 0;
		for (org.psygrid.data.model.hibernate.DocumentInstance docInst: documentInstances) {
			if (docInst != null) {
				insts[i] = docInst;
			}
			i++;
		}
		org.psygrid.data.model.hibernate.DocumentInstance[] returnedInsts = this.getAllowedDocumentInstances(insts, projectCode, groupCode, user, hideValues);

		Set<org.psygrid.data.model.hibernate.DocumentInstance> newInsts = new HashSet<org.psygrid.data.model.hibernate.DocumentInstance>(returnedInsts.length);
		for (int j=0; j < returnedInsts.length; j++) {
			if (returnedInsts[j] != null) {
				newInsts.add(returnedInsts[j]);
			}
		}
		return newInsts;
	}
	
	/**
	 * Examine the document instances provided and return only the ones allowed by the user.
	 * 
	 * @param documentInstances
	 * @param projectCode
	 * @param user
	 * @param hideValues obscure unviewable values
	 * @return allowedDocumentInstances
	 * @throws PGSecurityInvalidSAMLException
	 * @throws PGSecurityException
	 * @throws ConnectException
	 * @throws PGSecuritySAMLVerificationException
	 */
	private org.psygrid.data.model.hibernate.DocumentInstance[] getAllowedDocumentInstances(org.psygrid.data.model.hibernate.DocumentInstance[] documentInstances, String projectCode, String groupCode, String user, boolean hideValues) 
	throws DocumentSecurityFault {
		try {
			if (documentInstances == null) {
				return null;
			}
			List<org.psygrid.data.model.hibernate.DocumentInstance> allowed = new ArrayList<org.psygrid.data.model.hibernate.DocumentInstance>();
			for (org.psygrid.data.model.hibernate.DocumentInstance documentInst: documentInstances) {
				String action = documentInst.getAction();
				documentInst.setEditingPermitted(true);		//User can edit document by default
				if (action == null) {
					checkResponses(documentInst, projectCode, groupCode, user, hideValues);
					allowed.add(documentInst);
					String edit = documentInst.getEditableAction();
					if (edit != null) {
						RBACAction editRbac = RBACAction.valueOf(edit);
						if (!accessController.authoriseUser(getSaml(), new AEFGroup(null, groupCode, null), editRbac.toAEFAction(), new AEFProject(null, projectCode, false)) ){
							documentInst.setEditingPermitted(false);		//User is not permitted to edit document
						}
					}
					continue;
				}
				RBACAction rbac = RBACAction.valueOf(action);
				if (accessController.authoriseUser(getSaml(), new AEFGroup(null, groupCode, null), rbac.toAEFAction(), new AEFProject(null, projectCode, false)) ){
					checkResponses(documentInst, projectCode, groupCode, user, hideValues);
					allowed.add(documentInst);		//User can access document
					String edit = documentInst.getEditableAction();
					if (edit != null) {
						RBACAction editRbac = RBACAction.valueOf(edit);
						if (!accessController.authoriseUser(getSaml(), new AEFGroup(null, groupCode, null), editRbac.toAEFAction(), new AEFProject(null, projectCode, false)) ){
							documentInst.setEditingPermitted(false);		//User is not permitted to edit document
						}
					}
				}
			}
			org.psygrid.data.model.hibernate.DocumentInstance[] allowedDocs = new org.psygrid.data.model.hibernate.DocumentInstance[allowed.size()];
			for (int i=0; i<allowed.size(); i++) {
				allowedDocs[i] = allowed.get(i);
			}
			return allowedDocs;
		}
		catch(PGSecurityInvalidSAMLException ex){
			throw new NotAuthorisedFault("The supplied SAML assertion has expired", ex);
		}
		catch(PGSecuritySAMLVerificationException ex){
			throw new NotAuthorisedFault("The supplied SAML assertion does not come from a trusted issuer", ex);
		}
		catch(PGSecurityException ex){
			throw new NotAuthorisedFault("An error occurred during authorisation", ex);
		}
	}

	/**
	 * Check that the user can access each response and set whether it can be edited.
	 * 
	 * @param documentInstance
	 * @param projectCode
	 * @param user
	 * @param saml
	 * @param hideValues obscure unviewable values
	 * @return documentInstance
	 * @throws PGSecurityInvalidSAMLException
	 * @throws PGSecurityException
	 * @throws ConnectException
	 * @throws PGSecuritySAMLVerificationException
	 */
	private org.psygrid.data.model.hibernate.DocumentInstance checkResponses(org.psygrid.data.model.hibernate.DocumentInstance documentInstance, String projectCode, String groupCode, String user, boolean hideValues) throws PGSecurityInvalidSAMLException,
	PGSecurityException, PGSecuritySAMLVerificationException, DocumentSecurityFault {
		if (documentInstance.getResponses() == null) {
			return documentInstance;
		}
		Map<String,Boolean> accessible = new HashMap<String,Boolean>();
		Map<String,Boolean> editable = new HashMap<String,Boolean>();

		//iterate through document instance and retrieve unique response actions
		for (org.psygrid.data.model.hibernate.Response response: documentInstance.getResponses()) {
			accessible.put(response.getAccessAction(), null);
			editable.put(response.getEditableAction(), null);
		}

		//check security and record result for each response
		for (String action: accessible.keySet()) {
			if (action != null) {
				RBACAction rbac = RBACAction.valueOf(action);
				accessible.put(action, accessController.authoriseUser(getSaml(), new AEFGroup(null, groupCode, null), rbac.toAEFAction(), new AEFProject(null, projectCode, false)));
			}
		}
		for (String action: editable.keySet()) {
			if (action != null) {
				RBACAction rbac = RBACAction.valueOf(action);
				editable.put(action, accessController.authoriseUser(getSaml(), new AEFGroup(null, groupCode, null), rbac.toAEFAction(), new AEFProject(null, projectCode, false)));
			}
		}
		
		//iterate through doc inst again and set the non-persisted editingPermitted action as appropriate
		for (org.psygrid.data.model.hibernate.Response response: documentInstance.getResponses()) {
			String accessAction = response.getAccessAction();
			String editAction   = response.getEditableAction();
			//Set writable by default
			response.setEditingPermitted(EditAction.READWRITE);
			
			//If response is not accessible, set as not viewable
			if (accessAction != null && !accessible.get(accessAction)) {
				response.setEditingPermitted(EditAction.DENY);
				if (hideValues) {
					//Remove the value and replace with stars
					if (response instanceof org.psygrid.data.model.hibernate.BasicResponse) {
						obscureTheValue((org.psygrid.data.model.hibernate.BasicResponse)response);
					}
					else if (response instanceof org.psygrid.data.model.hibernate.CompositeResponse) {
						for (org.psygrid.data.model.hibernate.CompositeRow row: ((org.psygrid.data.model.hibernate.CompositeResponse)response).getCompositeRows()) {
							for (org.psygrid.data.model.hibernate.BasicResponse br: row.getBasicResponses()) {
								obscureTheValue(br);
							}
						}
					}
					else {
						throw new DocumentSecurityFault("Response was not recognised: "+response);
					}
				}
			}
			//If response is not editable set as read only
			else if (editAction != null && !editable.get(editAction)) {
				response.setEditingPermitted(EditAction.READONLY);
			}

		}

		return documentInstance;
	}

	private void obscureTheValue(org.psygrid.data.model.hibernate.BasicResponse br) {
		//Remove the value and set a default (CoCoA doesn't like null values).
		//Set the value as hidden so that CoCoA knows to display non-text 
		//values nicely too.
		if (br.getTheValue() instanceof org.psygrid.data.model.hibernate.OptionValue) {
			org.psygrid.data.model.hibernate.OptionValue v = new org.psygrid.data.model.hibernate.OptionValue();
			v.setTextValue(HIDDEN_VALUE);
			v.setHidden(true);
			br.setTheValue(v);
		}
		else if (br.getTheValue() instanceof org.psygrid.data.model.hibernate.IntegerValue) {
			org.psygrid.data.model.hibernate.IntegerValue v = new org.psygrid.data.model.hibernate.IntegerValue();
			v.setValue(new Integer(0));
			v.setHidden(true);
			br.setTheValue(v);
		}
		else if (br.getTheValue() instanceof org.psygrid.data.model.hibernate.NumericValue) {
			org.psygrid.data.model.hibernate.NumericValue v = new org.psygrid.data.model.hibernate.NumericValue();
			v.setValue(new Double(0));
			v.setHidden(true);
			br.setTheValue(v);
		}
		else if (br.getTheValue() instanceof org.psygrid.data.model.hibernate.LongTextValue) {
			org.psygrid.data.model.hibernate.LongTextValue v = new org.psygrid.data.model.hibernate.LongTextValue();
			v.setValue(HIDDEN_VALUE);
			v.setHidden(true);
			br.setTheValue(v);
		}
		else if (br.getTheValue() instanceof org.psygrid.data.model.hibernate.DateValue) {
			org.psygrid.data.model.hibernate.DateValue v = new org.psygrid.data.model.hibernate.DateValue();
			v.setValue(new Date());
			v.setHidden(true);
			br.setTheValue(v);
		}
		else if (br.getTheValue() instanceof org.psygrid.data.model.hibernate.BooleanValue) {
			org.psygrid.data.model.hibernate.BooleanValue v = new org.psygrid.data.model.hibernate.BooleanValue();
			v.setValue(false);
			v.setHidden(true);
			br.setTheValue(v);
		}
		else {
			org.psygrid.data.model.hibernate.TextValue defaultValue = new org.psygrid.data.model.hibernate.TextValue();
			defaultValue.setValue(HIDDEN_VALUE);
			defaultValue.setHidden(true);
			br.setTheValue(defaultValue);
		}
	}
	
	/**
	 * Used by recordDAO to check document instances against the originals in the database. Performs the
	 * same function as getAllowedDocumentInstances, but does not obscure the values in unviewable responses.
	 *  
	 * @param docInst
	 * @param user
	 * @param saml
	 * @return
	 * @throws PGSecurityInvalidSAMLException
	 * @throws PGSecurityException
	 * @throws ConnectException
	 * @throws PGSecuritySAMLVerificationException
	 * @throws RepositoryServiceFault
	 */
	public org.psygrid.data.model.hibernate.DocumentInstance authoriseDocumentInstance(org.psygrid.data.model.hibernate.DocumentInstance docInst, String user) 
	throws DocumentSecurityFault {
		
		if (docInst == null) {
			return null;
		}
		Record record = docInst.getRecord();

		String groupCode   = record.getIdentifier().getGroupPrefix();
		String projectCode = record.getIdentifier().getProjectPrefix();

		org.psygrid.data.model.hibernate.DocumentInstance[] documentInstances = new org.psygrid.data.model.hibernate.DocumentInstance[1];
		documentInstances[0] = docInst;

		//Don't obscure the values held by the responses
		org.psygrid.data.model.hibernate.DocumentInstance[] returned = getAllowedDocumentInstances(documentInstances, projectCode, groupCode, user, false);
		if (returned == null || returned.length == 0) {
			throw new DocumentSecurityFault("User does not have access to the document");	
		}
		if (returned[0] == null) {
			return null;
		}
		return returned[0];
	}
}

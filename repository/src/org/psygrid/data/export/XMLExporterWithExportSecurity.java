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

import java.net.ConnectException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.data.export.hibernate.ExportSecurityActionMap;
import org.psygrid.data.export.security.DataExportActions;
import org.psygrid.data.export.security.ExportSecurityValues;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.repository.dao.DAOException;
import org.psygrid.data.repository.dao.RepositoryDAO;
import org.psygrid.data.repository.transformer.InputTransformer;
import org.psygrid.data.repository.transformer.TransformerClient;
import org.psygrid.data.repository.transformer.TransformerException;
import org.psygrid.data.utils.esl.EslException;
import org.psygrid.data.utils.esl.IRemoteClient;
import org.psygrid.data.utils.esl.RemoteClient;
import org.psygrid.data.utils.security.NotAuthorisedFault;
import org.psygrid.data.utils.wrappers.AAQCWrapper;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.PGSecurityInvalidSAMLException;
import org.psygrid.security.PGSecuritySAMLVerificationException;
import org.psygrid.security.RBACAction;
import org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectType;

/**
 * Implementation of {@link XMLExporter} that implements export 
 * security by using transformers or applying standard codes to
 * restricted entries.
 * 
 * @author Rob Harper
 *
 */
public class XMLExporterWithExportSecurity extends XMLExporter {

	private static final Log LOG = LogFactory.getLog(XMLExporterWithExportSecurity.class);

	/**
	 * Input transformer to be used for transforming items according to applied security tags and user privilege.
	 * If security is to be applied the transformer is required only if the data to be exported requires
	 * transformations. It is required for no other purpose.
	 * Wired in by the application context.
	 */
	private InputTransformer inputTransformer = null; 

	/**
	 * DAO bean that handles all communication with the database.
	 * If security is to be applied, the dao is required only if the the data to be exported requires transformations.
	 * It is required for no other purpose.
	 * Wired in by the application context.
	 */
	private RepositoryDAO repositoryDAO = null;
	
	/**
	 * Wired in by the application context.
	 */
	private AAQCWrapper aaqc = null;

	/**
	 * The export-restricted standard code.
	 */
	private static final StandardCode exportRestrictedCode = new StandardCode("Data is export-restricted", 950);

	{
		exportRestrictedCode.setUsedForDerivedEntry(false);
		exportRestrictedCode.setId(-1l); //This is just a surrogate Id and does not correlate to anything in the database...
	}	
	
	
	/**
	 * Wired in application context.
	 */
	public void setInputTransformer(InputTransformer inputTransformer) {
		this.inputTransformer = inputTransformer;
	}

	/**
	 * Wired in application context.
	 */
	public void setRepositoryDAO(RepositoryDAO repositoryDAO) {
		this.repositoryDAO = repositoryDAO;
	}
	
	/**
	 * Wired in application context.
	 */
	public void setAaqc(AAQCWrapper aaqc) {
		this.aaqc = aaqc;
	}

	@Override
	protected boolean checkAuthorisation(String projectCode, String groupCode, String action, String requestor) {
		try {
			if (!requestor.equals("NoUser") && action != null) {
				String saml = aaqc.getSAMLAssertion(requestor);
				RBACAction rbac = RBACAction.valueOf(action);
				aaqc.authoriseUser(requestor, rbac.toAEFAction(), new ProjectType(null, projectCode, null, null, false), 
						new GroupType(null, groupCode, null), saml);
			}
		}
		catch (PGSecurityException ex) {
			LOG.error("Problem occurred when trying to retrieve saml in generateMgmtReportsForDataSet", ex);
			return false;
		}
		catch (PGSecurityInvalidSAMLException ex) {
			LOG.error("Problem occurred when trying to retrieve saml in generateMgmtReportsForDataSet", ex);
			return false;
		}
		catch (ConnectException ex) {
			LOG.error("Problem occurred when trying to retrieve saml in generateMgmtReportsForDataSet", ex);
			return false;
		}
		catch (PGSecuritySAMLVerificationException ex) {
			LOG.error("Problem occurred when trying to retrieve saml in generateMgmtReportsForDataSet", ex);
			return false;
		}
		catch (NotAuthorisedFault ex) {
			LOG.error("Problem occurred when trying to retrieve saml in generateMgmtReportsForDataSet", ex);
			return false;
		}
		catch (NotAuthorisedFaultMessage ex) {
			LOG.error("Problem occurred when trying to retrieve saml in generateMgmtReportsForDataSet", ex);
			return false;
		}

		return true;
	}

	@Override
	protected boolean checkAuthForResponse(Response response, String projectCode, String groupCode, String requestor){
		if (response == null){
			return true;
		}
		return checkAuthorisation(projectCode, groupCode, response.getAccessAction(),requestor);
	}

	@Override
	protected void setRequiredAction(DataSet ds, Entry entry, Document document,List<ExportSecurityActionMap> actionMap) {

		if( !ds.getExportSecurityActive() ){
			return;
		}

		ExportSecurityValues tag = null; 

		boolean outputTransformerExists = true;
		if(entry instanceof BasicEntry){ //Determine whether this entry actually has an output transformer...
			BasicEntry basicEntry = (BasicEntry)entry;
			List<Transformer> transformerList = basicEntry.getOutputTransformers();
			outputTransformerExists = transformerList.iterator().hasNext();
		}
		else if(entry instanceof CompositeEntry){
			CompositeEntry compositeEntry = (CompositeEntry) entry;
			List<BasicEntry> entries = compositeEntry.getEntries();
			if(compositeEntry.getExportSecurity() != null){
				//Apply the composite entry's security tag to any consituents that don't already have a security tag specified.
				for(BasicEntry e: entries){
					if(e.getExportSecurity() == null){
						e.setExportSecurity(compositeEntry.getExportSecurity());      					
					}
				}
			}
			for(BasicEntry e: entries){
				setRequiredAction(ds,e,document,actionMap);
			}
			return;
		}
		else if(entry instanceof NarrativeEntry){
			//NOTE: It would be nice if instead of checking for NarrativeEntry, we could check to see if the entry
			//is of a type that never has a response.
			return;
		}

		///////////////////////////////////////////////////////////////////////////////////////////////////////
		//Below this line, the entry is guaranteed to be a BasicEntry.

		BasicEntry basicEntry = (BasicEntry)entry;
		tag = entry.getExportSecurity();

		//Fix for Bug #757
		//Check to see if the document has been tagged. If so, then its tag trumps any tags that have been
		//applied to its constituent entries. However, if a null document tag does NOT trump tags already
		//applied to its constituent entries.
		ExportSecurityValues documentTag = document.getExportSecurity();
		if(documentTag != null){
			tag = documentTag;
		}

		if(tag == null){
			basicEntry.setExportAction(DataExportActions.ACTION_EXPORT_UNRESTRICTED);
		}
		else{
			boolean actionFound = false;
			if(actionMap != null){
				for(ExportSecurityActionMap m: actionMap){
					if(m.getSecurityTag().equals(tag.toString())){
						basicEntry.setExportAction(DataExportActions.valueOf(m.getExportAction()));
						actionFound = true;
						break;
					}
				}
			}
			if(!actionFound){
				//NOTE: This clause should NEVER be called because there should be a mapping defined
				//for EACH and EVERY tag. This must be guaranteed by the dataset designer.
				basicEntry.setExportAction(DataExportActions.ACTION_EXPORT_RESTRICTED);
			}
		}



		//Adjust the export action. If the required action is EXPORT_TRANSFORMED but there is no transformer,
		//then set the required action to EXPORT_RESTRICTED (more secure)
		if(basicEntry.getExportAction() == DataExportActions.ACTION_EXPORT_TRANSFORMED && !outputTransformerExists){
			basicEntry.setExportAction(DataExportActions.ACTION_EXPORT_RESTRICTED);
		}

		//the required action is ACTION_EXPORT_TRANSFORMED but this object hasn't been fully initialised,
		//then it's not possible to do the transformation. In that case, apply the more stringent security action,
		//ACTION_EXPORT_RESTRICTED
		if(basicEntry.getExportAction() == DataExportActions.ACTION_EXPORT_TRANSFORMED && (inputTransformer == null || repositoryDAO == null) ){
			basicEntry.setExportAction(DataExportActions.ACTION_EXPORT_RESTRICTED);
		}
	}

	@Override
	protected void takeRequiredSecurityAction(DataSet ds, Response resp) throws DAOException, RemoteException, TransformerException {

		if( !ds.getExportSecurityActive() || resp == null){
			return;
		}

		if(resp instanceof BasicResponse){
			BasicEntry basicEntry = (BasicEntry)resp.getEntry();
			DataExportActions requiredAction = basicEntry.getExportAction();

			if(requiredAction == DataExportActions.ACTION_EXPORT_TRANSFORMED){ 
				Map<Long, TransformerClient> transformerClients = null;
				transformerClients = repositoryDAO.getTransformerClients(ds.getId()); 
				inputTransformer.transformResponseForExport((Response)resp, transformerClients); 
			}
			if(requiredAction == DataExportActions.ACTION_EXPORT_RESTRICTED){ 
				BasicResponse basicResp = (BasicResponse) resp;
				basicResp.getValue().setStandardCode(exportRestrictedCode);       		
			}
		}else if(resp instanceof CompositeResponse){
			CompositeResponse compositeResp = (CompositeResponse) resp;
			CompositeEntry entry = (CompositeEntry)compositeResp.getEntry();

			for ( int i=0; i<compositeResp.numCompositeRows(); i++ ){
				CompositeRow cRow = compositeResp.getCompositeRow(i);
				for ( int j=0; j<entry.numEntries(); j++ ){
					BasicEntry be = entry.getEntry(j);
					BasicResponse bResp = cRow.getResponse(be);
					takeRequiredSecurityAction(ds,bResp);
				}
			}    
		}
	}

	/**
	 * Get the date of the last randomisation for the record.
	 *  
	 * @param record
	 * @return
	 */
	@Override
	protected String getRandomisationDate(org.psygrid.data.model.hibernate.DataSet ds,Record record, String requestor) {
		String saml = null;
		try {
			saml = aaqc.getSAMLAssertion(requestor);
		}
		catch (PGSecurityException ex) {
			LOG.error("Problem occurred when trying to retrieve saml in generateRandomisationDate", ex);
			return null;
		}
		catch (ConnectException ex) {
			LOG.error("Problem occurred when trying to retrieve saml in generateRandomisationDate", ex);
			return null;
		}
		catch (PGSecuritySAMLVerificationException ex) {
			LOG.error("Problem occurred when trying to retrieve saml in generateRandomisationDate", ex);
			return null;
		}
		catch (PGSecurityInvalidSAMLException ex) {
			LOG.error("Problem occurred when trying to retrieve saml in generateRandomisationDate", ex);
			return null;
		}
		catch (NotAuthorisedFaultMessage ex) {
			LOG.error("Problem occurred when trying to retrieve saml in generateRandomisationDate", ex);
			return null;
		}

		try {
			IRemoteClient client = new RemoteClient();

			Date[] randomisations = client.getSubjectRandomisationEvents(record.getDataSet().getProjectCode(), record.getIdentifier().getIdentifier(), saml);
			if (randomisations != null && randomisations.length > 0) {
				Date date = randomisations[randomisations.length-1];
				SimpleDateFormat ddMmmYyyyFormatter = new SimpleDateFormat("dd-MMM-yyyy");
				return ddMmmYyyyFormatter.format(date);
			}
		}
		catch (EslException e) {
			LOG.error("Problem occurred when calling 'getSubjectRandomisationEvents' ", e);
			return null;
		}

		return NOT_RANDOMISED;
	}

}

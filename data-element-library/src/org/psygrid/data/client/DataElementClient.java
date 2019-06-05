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
package org.psygrid.data.client;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.rpc.ServiceException;

import org.apache.axis.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.common.AbstractClient;
import org.psygrid.data.DELFailedTestException;
import org.psygrid.data.DELServiceFault;
import org.psygrid.data.DataElement;
import org.psygrid.data.DataElementServiceLocator;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.repository.RepositorySoapBindingStub;
import org.psygrid.data.dao.ElementAuthorityNotRecognizedException;
import org.psygrid.data.dao.ElementRevisionException;
import org.psygrid.data.dao.ElementStatusChangeException;
import org.psygrid.data.model.IAdminInfo;
import org.psygrid.data.model.IDELQueryObject;
import org.psygrid.data.model.RepositoryModelException;
import org.psygrid.data.model.dto.DELQueryObject;
import org.psygrid.data.model.dto.ElementMetaDataDTO;
import org.psygrid.data.model.dto.PersistentDTO;
import org.psygrid.data.repository.RepositoryServiceFault;
import org.psygrid.data.utils.security.NotAuthorisedFault;


public class DataElementClient extends AbstractClient{

	private final static Log LOG = LogFactory.getLog(DataElementClient.class);

	/**
	 * Default no-arg constructor
	 */
	public DataElementClient(){}

	public DataElementClient(URL url){
		super(url);
	}

	/**
	 * Constructor that accepts a value for the url where the web
	 * service is located and the timeout for the web service.
	 * 
	 * @param url
	 * @param timeout
	 */
	public DataElementClient(URL url, int timeout){
		super(url, timeout);
	}

	/**
	 * Constructor that accepts a timeout for the web service.
	 * 
	 * @param timeout
	 */
	public DataElementClient(int timeout){
		super(timeout);
	}

	private DataElement getService() throws ServiceException{
		DataElementServiceLocator locator = new DataElementServiceLocator();
		DataElement service = null;
		if ( null == this.url ){
			service = locator.getdataElement();
		}
		else{
			service = locator.getdataElement(url);
		}
		if ( this.timeout >= 0 ){
			RepositorySoapBindingStub stub  = (RepositorySoapBindingStub)service;
			stub.setTimeout(this.timeout);
		}
		return service;
	}

	public List<Document> getDocumentSummaryList(final String authority, final String saml) throws ServiceException, RemoteException, 
	NotAuthorisedFault, DELServiceFault {

		org.psygrid.data.model.dto.DocumentDTO[] docs = null;

		DataElement service;
		try {
			service = getService();
			docs = service.getDocumentsSummaryInfo(authority, saml);
			List<Document> docSummaryList = new ArrayList<Document>();

			for(int i = 0; i < docs.length; i++){
				docSummaryList.add((Document)docs[i].toHibernate());
			}
			return docSummaryList;
		} catch (ServiceException e) {
			LOG.fatal(e.getMessage(), e);
			throw e;
		} catch (RemoteException e) {
			LOG.fatal(e.getMessage(), e);
			throw e;
		}
	}

	/**
	 * importDataElement(...) is required for the initial population of the data 
	 * element library. Typically this will be a data set, as those 
	 * prepared in the outlook-dataset project.
	 * The element, and all of its constituents will be imported, and each will be assigned an LSID.
	 * The admin details will be applied to ALL elements.
	 * 
	 * @param element - the element to be imported
	 * @param info - admin details including the organisation to whom the importer belongs, and a description of the
	 * 				reason for the import.
	 * @param saml - identifies the user
	 * @return - a string version of the lsid of the successfully-imported root element.
	 * @throws RemoteException
	 * @throws RepositoryServiceFault
	 * @throws NotAuthorisedFault
	 * @throws DELServiceFault 
	 * @throws ServiceException 
	 * @throws ElementAuthorityNotRecognizedException 
	 */
	public String importDataElement(DataElementContainer element, IAdminInfo info, final String authority, String saml) throws RemoteException, 
	NotAuthorisedFault, SocketTimeoutException, ConnectException, DELServiceFault, ServiceException, ElementAuthorityNotRecognizedException
	{

		try{
			DataElement service = getService();
			org.psygrid.data.model.dto.DataElementContainerDTO elemContainerDTO = element.toDTO();
			String newLSID = service.importDataElement(elemContainerDTO, info.toDTO(), authority, saml);

			LSID newId = null;
			try {
				newId = LSID.valueOf(newLSID);
			} catch (LSIDException e) {
				//This will not happen!
			}

			setContainerLSID(newId, element);

			return newLSID;

		}
		catch(AxisFault fault){ 
			//This is generated by the stub. The stub receives all exceptions as AxisFault objects, which embed
			//the more specific fault. It then converts those it can (such as to a DELServiceFault),
			//And issues as a RemoteException anything that cannot be specifically identified.
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(ServiceException ex){ //Comes from calling getService() - likely to be an incorrect url
			LOG.fatal(ex.getMessage(), ex);
			throw ex;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw ex;
		}
	}

	/**
	 * getElementTypes(...) is used in order to get a list of the types of elements that are available in the library.
	 * The returned array will typically comprise the Class objects for dataset, document, entry.
	 * 
	 * @return - an array of element class types available in the library
	 * @throws NotAuthorisedFault
	 * @throws SocketTimeoutException 
	 * @throws ConnectException 
	 * @throws ServiceException 
	 * @throws RemoteException 
	 */
	public String [] getElementTypes() throws NotAuthorisedFault, ConnectException, SocketTimeoutException, ServiceException, RemoteException{
		String [] elementTypes = null;
		try{
			DataElement service = getService();
			elementTypes = service.getElementTypes();
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(ServiceException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw ex;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw ex;
		}

		return elementTypes;
	}


	/**
	 * To be used by an element library author in order to approve or reject an element.
	 * 
	 * @param element to change the status of
	 * @param action what action to take (e.g. approve, reject)
	 * @param saml
	 * @throws NotAuthorisedFault
	 * @throws IllegalArgumentException
	 * @throws DELServiceFault
	 * @throws ConnectException
	 * @throws SocketTimeoutException
	 * @throws ServiceException
	 * @throws RemoteException
	 * @throws ElementStatusChangeException
	 */
	public void modifyElementStatus(DataElementContainer element, String actionTaken, String comment, String saml) throws
	NotAuthorisedFault, IllegalArgumentException, DELServiceFault, ConnectException, 
	SocketTimeoutException, ServiceException, RemoteException, ElementStatusChangeException{

		DataElementStatus newStatus = null;

		if (actionTaken.equalsIgnoreCase(org.psygrid.data.model.dto.AdminInfo.APPROVE)) {
			newStatus = DataElementStatus.APPROVED;
		}
		String registrar = element.getElementLSIDObject().getAuthorityId();
		org.psygrid.data.model.dto.AdminInfo info = new org.psygrid.data.model.dto.AdminInfo(actionTaken, comment, true, registrar, null);

		DataElement service = getService();
		service.modifyElementStatus(newStatus.toString(), element.getElementLSID(), info, saml);
	}

	/**
	 * Given a list of ElementStatusContainer object, the server will check each element's current
	 * status against the containers passed in by the client. The method will return updated 
	 * information for those elements whose statuses have changed.
	 * 
	 * The return list will contain those elements
	 * for which the client passed in out-of-sync status info.
	 * 
	 * @param elementsInQuestion - The elements to be checked.
	 * @param reportNonHeadRevisionElements - if true, include these in the returned list, regardless of 
	 * 			comparison outcome.
	 * @return - a list of up-to-date element information
	 * @throws ServiceException 
	 * @throws DELServiceFault 
	 * @throws NotAuthorisedFault 
	 * @throws RemoteException 
	 */
	public List<ElementStatusContainer> reportElementStatusChanges(List<ElementStatusContainer> elementsInQuestion, boolean reportNonHeadRevisionElements, String saml) throws ServiceException, RemoteException, NotAuthorisedFault, DELServiceFault{

		List<org.psygrid.data.model.dto.ElementStatusContainer> dtoList = new ArrayList<org.psygrid.data.model.dto.ElementStatusContainer>();
		for(ElementStatusContainer elem: elementsInQuestion){
			dtoList.add(elem.toDTO());
		}

		org.psygrid.data.model.dto.ElementStatusContainer [] dtoContainerArray = new org.psygrid.data.model.dto.ElementStatusContainer[dtoList.size()];
		dtoContainerArray = dtoList.toArray(dtoContainerArray);

		DataElement service = getService();


		org.psygrid.data.model.dto.ElementStatusContainer [] returnArray = service.reportElementStatusChanges(dtoContainerArray, reportNonHeadRevisionElements, saml);

		List<org.psygrid.data.model.dto.ElementStatusContainer> returnList = Arrays.asList(returnArray);
		List<ElementStatusContainer> returnListHib = new ArrayList<ElementStatusContainer>();
		for(org.psygrid.data.model.dto.ElementStatusContainer cont: returnList){
			returnListHib.add(cont.toHibernate());
		}

		return returnListHib;
	}

	public  List<List<String>> getLatestRevision(List<String> lsids, String saml) throws ServiceException, RemoteException, NotAuthorisedFault, DELServiceFault{

		String [] lsidArray = new String[lsids.size()];
		lsidArray = lsids.toArray(lsidArray);

		DataElement service;
		String[][] returnArray = null;
		try {
			service = getService();
			returnArray = service.getLatestElementVersion(lsidArray, saml);
		} catch (ServiceException e) {
			LOG.fatal(e.getMessage(), e);
			throw e;
		} catch (RemoteException e) {
			LOG.fatal(e.getMessage(), e);
			throw e;
		} catch (NotAuthorisedFault e) {
			LOG.fatal(e.getMessage(), e);
			throw e;
		} catch (DELServiceFault e) {
			LOG.fatal(e.getMessage(), e);
			throw e;
		}

		//initalize the double list...
		List<List<String>> returnList = new ArrayList<List<String>>();
		for(int i = 0; i < returnArray.length; i++){
			List lsidList = new ArrayList<String>();
			lsidList.add(returnArray[i][0]);
			lsidList.add(returnArray[i][1]);
			returnList.add(lsidList);
		}

		return returnList;
	}

	public LSIDAuthority[] getLSIDAuthorities(String saml) throws ConnectException, SocketTimeoutException, 
	ServiceException, RemoteException, DELServiceFault, NotAuthorisedFault {

		org.psygrid.data.model.dto.LSIDAuthorityDTO[] authorities = null;

		DataElement service = getService();
		try {
			authorities = service.getLSIDAuthorityList(saml);
		} catch (DELServiceFault e) {
			LOG.fatal(e.getMessage(), e);
			throw e;
		}

		LSIDAuthority[] authoritiesHib = new LSIDAuthority[authorities.length];

		for(int i = 0; i < authorities.length; i++) {
			authoritiesHib[i] = authorities[i].toHibernate();
		}

		return authoritiesHib;
	}


	public org.psygrid.data.model.IDELQueryObject sophisticatedSearchByTypeAndName(IDELQueryObject queryObject, String saml) throws DELServiceFault, ConnectException, SocketTimeoutException, NotAuthorisedFault, ServiceException, RemoteException{
		DELQueryObject queryManager = null;
		DELQueryObject dtoObject = ((org.psygrid.data.model.hibernate.DELQueryObject)queryObject).toDTO();

		try {
			DataElement service = getService();
			queryManager = service.sophisticatedSearchByTypeAndName(dtoObject, saml);	
		}catch (AxisFault fault){ 
			handleAxisFault(fault, LOG);
			return null;
		}catch (ServiceException e) {
			LOG.fatal(e.getMessage(), e);
			throw e;
		} catch (RemoteException e) {
			LOG.fatal(e.getMessage(), e);
			throw e;
		} 


		return (IDELQueryObject)queryManager.toHibernate();
	}

	/**
	 * reviseElement(...) revises the element. Note that it is possible for this to fail if the latest revision number
	 * of the element on the server has been incremented  (i.e. it has been revised) since the element was retrieved
	 * by the client.
	 * 
	 * @param elem - the element to be revised
	 * @param adminInfo - an object containing administrative details about the update, including a description of the
	 * 		change made. 
	 * @param saml - identifies the user
	 * @throws NotAuthorisedFault
	 * @throws RepositoryServiceFault
	 * @throws IllegalArgumentException - throws this if elem has not been retrieved through getElementForRevision(...).
	 * 			Will also get thrown if the RevisionAdminInfo object is not fully specified.
	 * @throws DELServiceFault 
	 * @throws SocketTimeoutException 
	 * @throws ConnectException 
	 * @throws ServiceException 
	 * @throws RemoteException 
	 * @throws ElementRevisionException 
	 * @throws ElementAuthorityNotRecognizedException 
	 * @throws RepositoryModelException 
	 */
	public String reviseElement(DataElementContainer elem, IAdminInfo adminInfo, final String authority, String saml)
	throws NotAuthorisedFault, IllegalArgumentException, DELServiceFault, ConnectException, 
	SocketTimeoutException, ServiceException, RemoteException, ElementRevisionException, DELFailedTestException, ElementAuthorityNotRecognizedException, RepositoryModelException
	{

		if(elem == null)
			throw new IllegalArgumentException("IElement object cannot be null!");
		else if(adminInfo == null)
			throw new IllegalArgumentException("IAdminInfo object cannot be null!");

		String revisedLSID = null;
		try{
			DataElement service = getService();

			//By calling setPrepareElementForNewRevision, the element's hibernate id and revision no are zeroed
			//when toDTO is called.
			elem.setPrepareElementForNewRevision(true);
			revisedLSID = service.reviseElement(elem.toDTO(), adminInfo.toDTO(), authority, saml);

			org.psygrid.data.model.hibernate.LSID lsid = null;
			try {
				lsid = LSID.valueOf(revisedLSID);
			} catch (LSIDException e) {
				//This won't happen - the lsid will always be valid when coming from the server.
			}

			setContainerLSID(lsid, elem);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(ServiceException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw ex;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw ex;
		}    

		return revisedLSID;
	}

	/**
	 * getMetaData(...) retrieves the meta-data for a given element. If no revision is specified in the lsid, then the
	 * meta-data will be returned for the latest revision in the library.
	 * 
	 * @param lsid - identifies the element for which meta-data will be retrieved.
	 * @param saml - identifies the user.
	 * @return the element meta-data
	 * @throws NotAuthorisedFault
	 * @throws RepositoryServiceFault
	 * @throws IllegalArgumentException - this will be thrown if the lsid is either invalid or not found in the library.
	 * @throws DELServiceFault 
	 * @throws ServiceException 
	 * @throws RemoteException 
	 */

	public ElementMetaData getMetaData(String lsid, String saml)
	throws NotAuthorisedFault, IllegalArgumentException, DELServiceFault, 
	IllegalArgumentException, ServiceException, RemoteException
	{
		//Validate the lsid string
		try {
			LSID.valueOf(lsid);
		} catch (LSIDException e1) {
			throw new IllegalArgumentException("LSID invalid", e1);
		}

		org.psygrid.data.model.hibernate.ElementMetaData hibMD = null;
		try {
			DataElement service = getService();
			ElementMetaDataDTO metaData = service.getMetaData(lsid, saml);
			Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
			hibMD = (org.psygrid.data.model.hibernate.ElementMetaData)metaData.toHibernate(hRefs);
		} catch (ServiceException e) {
			LOG.fatal(e.getMessage(), e);
			throw e;
		} catch (RemoteException e) {
			LOG.fatal(e.getMessage(), e);
			throw e;
		}
		return hibMD;
	}



	/**
	 * getElementAsRepositoryTemplate(...) returns an element object that is ready to be either incorporated into
	 * the building of a new database, or to be saved as-is into the repository.
	 * 
	 * @param lsid - identifies the element.
	 * @param saml - identifies the user.
	 * @throws NotAuthorisedFault
	 * @throws RepositoryServiceFault		LSID newId = null;
		try {
			newId = LSID.valueOf(newLSID);
		} catch (LSIDException e) {
			//This will not happen!
		}
		elem.setLSID(newId);
	 * @throws IllegalArgumentException - will be thrown if the lsid is either invalid or not found in the library.
	 * @throws DELServiceFault 
	 * @throws ServiceException 
	 * @throws RemoteException 
	 */
	public DataElementContainer getCompleteElement(String lsid, String saml, boolean forDSImport)
	throws NotAuthorisedFault, RepositoryServiceFault, IllegalArgumentException, DELServiceFault, ServiceException, RemoteException
	{
		//Validate the lsid string
		try {
			LSID.valueOf(lsid);
		} catch (LSIDException e1) {
			throw new IllegalArgumentException("LSID invalid", e1);
		}

		org.psygrid.data.model.dto.DataElementContainerDTO container = null;    	
		DataElement service;
		try {
			service = getService();
			container = service.getElementAsRepositoryTemplate(lsid, saml);
		} catch (ServiceException e) {
			LOG.fatal(e.getMessage(), e);
			throw e;
		} catch (RemoteException e) {
			LOG.fatal(e.getMessage(), e);
			throw e;
		}

		return container.toHibernate();
	}

	private void setContainerLSID(LSID id, DataElementContainer element){
		if(element.getElement() instanceof ValidationRule){
			((ValidationRule)element.getElement()).setLSID(id);
		}else if(element.getElement() instanceof org.psygrid.data.model.hibernate.Element){
			((org.psygrid.data.model.hibernate.Element)element.getElement()).setLSID(id);
		}
	}

	public void saveAuthorityToDatabase(String authority, String saml) throws RemoteException, DELServiceFault, NotAuthorisedFault, ServiceException{
		try {
			DataElement service = getService();
			service.addAuthority(authority, saml);
		}catch (RemoteException e){
			LOG.fatal(e.getMessage(), e);
			throw e;
		}catch (DELServiceFault e){
			LOG.fatal(e.getMessage(), e);
			throw e;
		}catch (NotAuthorisedFault e){
			LOG.fatal(e.getMessage(), e);
			throw e;
		} catch (ServiceException e) {
			LOG.fatal(e.getMessage(), e);
			throw e;
		}
	}

	public String saveNewElement(DataElementContainer element, IAdminInfo info, String authority, String saml) 
	throws DELServiceFault, RemoteException, NotAuthorisedFault, ServiceException, DELFailedTestException, ElementAuthorityNotRecognizedException {

		if(element == null)
			throw new IllegalArgumentException("IElement object cannot be null!");
		else if(info == null)
			throw new IllegalArgumentException("IAdminInfo object cannot be null!");

		String newLSID = null;

		try {
			DataElement service = getService();
			newLSID = service.saveNewElement(element.toDTO(), info.toDTO(), authority, saml);
		} catch (ServiceException e) {
			LOG.fatal(e.getMessage(), e);
			throw e;
		} catch (RemoteException e) {
			LOG.fatal(e.getMessage(), e);
			throw e;
		} catch (NotAuthorisedFault e) {
			LOG.fatal(e.getMessage(), e);
			throw e;
		}

		LSID newId = null;
		try {
			newId = LSID.valueOf(newLSID);
		} catch (LSIDException e) {
			//This will not happen!
		}

		setContainerLSID(newId, element);

		return newLSID;
	}

}

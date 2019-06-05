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

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.psygrid.data.dao.ElementAuthorityNotRecognizedException;
import org.psygrid.data.dao.ElementRevisionException;
import org.psygrid.data.dao.ElementStatusChangeException;
import org.psygrid.data.dao.NoSuchElementException;
import org.psygrid.data.model.RepositoryModelException;
import org.psygrid.data.model.dto.DELQueryObject;
import org.psygrid.data.model.dto.DataElementContainerDTO;
import org.psygrid.data.model.dto.DocumentDTO;
import org.psygrid.data.model.dto.ElementMetaDataDTO;
import org.psygrid.data.model.dto.ElementStatusContainer;
import org.psygrid.data.model.dto.LSIDAuthorityDTO;
import org.psygrid.data.model.dto.AdminInfo;
import org.psygrid.data.repository.RepositoryServiceFault;

public interface DataElement extends Remote{
	
	/**
	 * Retrieves the array of LSID authorities currently registered within the database.
	 * @return
	 */
	public LSIDAuthorityDTO[] getLSIDAuthorityList(String saml) throws RemoteException, DELServiceFault;
	
	/**
	 * 
	 * @param authority
	 * @param saml
	 * @return
	 * @throws RemoteException
	 * @throws NotAuthorisedFault
	 * @throws DELServiceFault
	 */
	public DocumentDTO[] getDocumentsSummaryInfo(final String authority, final String saml) 
	throws RemoteException, DELServiceFault ;
	
	/**
	 * This is similar in function to the above searchByTypeAndName. Whereas the above method cannot
	 * handle queries with results that go over a threshold, this method can, by making iterative
	 * queries to the server to return more and more results.
	 * 
	 * @param manager
	 * @param saml
	 * @return
	 */
	
	public DELQueryObject sophisticatedSearchByTypeAndName(DELQueryObject manager, String saml)
		throws RemoteException, DELServiceFault;
		
	
	public void addAuthority(final String authority, String saml) throws RemoteException, DELServiceFault;
	
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
	 * @throws ElementAuthorityNotRecognizedException 
	 */
    public String importDataElement(DataElementContainerDTO element, AdminInfo info, final String authority, String saml)
    	throws RemoteException, DELServiceFault, ElementAuthorityNotRecognizedException;

    /**
     * getElementTypes(...) is used in order to get a list of the types of elements that are available in the library.
     * The returned array will typically comprise the Class objects for dataset, document, entry.
     * 
     * @return - an array of element class types available in the library
     * @throws NotAuthorisedFault
     */
    public String [] getElementTypes() throws RemoteException;
    
    /**
     * reviseElement(...) revises the element. Note that it is possible for this to fail if the latest revision number
     * of the elemnt on the server has been incremented  (i.e. it has been revised) since the element was retrieved
     * by the client.
     * 
     * @param elem - the element to be revised
     * @param adminInfo - an object containing administrative details about the update, including a description of the
     * 					change, 
     * @param saml - identifies the user
     * @throws NotAuthorisedFault
     * @throws RepositoryServiceFault
     * @throws DELServiceFault 
     * @throws ElementRevisionException 
     * @throws ElementAuthorityNotRecognizedException 
     * @throws RepositoryModelException 
     */
    public String reviseElement(DataElementContainerDTO elem, AdminInfo adminInfo, final String authority, String saml) throws RemoteException,
    	DELServiceFault, ElementRevisionException, DELFailedTestException, ElementAuthorityNotRecognizedException, RepositoryModelException;
    
    /**
     * getMetaData(...) retrieves the meta-data for a given element. If no revision is specified in the lsid, then the
     * meta-data will be returned for the latest revision in the library.
     * 
     * @param lsid - identifies the element for which meta-data will be retrieved.
     * @param saml - identifies the user.
     * @return the element meta-data
     * @throws NotAuthorisedFault
     * @throws RepositoryServiceFault
     * @throws DELServiceFault 
     * @throws NoSuchElementException 
     */
     public ElementMetaDataDTO getMetaData(String lsid, String saml) throws RemoteException,
     	DELServiceFault;
     
     
     /**
      * getElementAsRepositoryTemplate(...) returns an element object that is ready to be either incorporated into
      * the building of a new database, or to be saved as-is into the repository.
      * 
      * @param lsid - identifies the element.
      * @param saml - identifies the user.
      * @throws NotAuthorisedFault
      * @throws RepositoryServiceFault
     * @throws DELServiceFault 
     * @throws NoSuchElementException 
      */
     public DataElementContainerDTO getElementAsRepositoryTemplate(String lsid, String saml) throws RemoteException,
     	DELServiceFault;
    
    
    /**
     * Saves a new element into the element library. 
     * It is allowed that the element can comprise sub-elements that already exist within the
     * library.
     * 
     * @param element - the element to save.
     * @param info - the admin info providing provenance data about the save.
     * @param authority - the authority to which this element is to belong.
     * @param saml
     * @return
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     * @throws DELServiceFault 
     * @throws ElementAuthorityNotRecognizedException 
     */
    public String saveNewElement(DataElementContainerDTO element, AdminInfo info, String authority, String saml)
    	throws RemoteException, DELServiceFault, DELFailedTestException, ElementAuthorityNotRecognizedException;
    
    
    /**
     * 
     * @param status
     * @param lsid
     * @param info
     * @param saml
     * @throws RemoteException
     * @throws NotAuthorisedFault
     * @throws DELServiceFault
     * @throws ElementStatusChangeException 
     */
    public void modifyElementStatus(String status, String lsid, AdminInfo info, String saml)
    	throws RemoteException, DELServiceFault, ElementStatusChangeException;
    
    
    /**
     * Given a list of ElementStatusContainer object, the server will check the elements current
     * status the containers passed in by the client. The return list will contain those elements
     * for which the client passed in out-of-sync status info.
     * The list will be empty if there are no element status out-of-sync.
     * @param elementsInQuestion
     * @return
     */
    public ElementStatusContainer [] reportElementStatusChanges(ElementStatusContainer [] elementsInQuestion, boolean reportNonHeadRevisionElements, String saml)
    	throws RemoteException, DELServiceFault;
    
    
    /**
     * Retrieves the latest appropriate version of the element.
     * For a viewer, this will be the latest approved element version.
     * For an author or curator, this will be the element head revision (i.e. pending or approved).
     * 
     * @param lsid - lsid of the element for which the latest version is required.
     * @param saml
     * @return - lsid of the latest appropriate version
     * @throws RemoteException
     * @throws NotAuthorisedFault
     * @throws DELServiceFault
     */
    public String[][] getLatestElementVersion(String[] lsids, String saml) 
    	throws RemoteException, DELServiceFault;
    	
    
}

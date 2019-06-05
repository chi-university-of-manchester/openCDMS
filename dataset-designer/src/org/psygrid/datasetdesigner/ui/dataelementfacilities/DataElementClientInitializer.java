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
package org.psygrid.datasetdesigner.ui.dataelementfacilities;


import java.beans.PropertyChangeSupport;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.rpc.ServiceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.collection.entry.remote.RemoteServiceFault;
import org.psygrid.collection.entry.security.EntrySAMLException;
import org.psygrid.collection.entry.security.SecurityHelper;
import org.psygrid.collection.entry.security.SecurityManager;
import org.psygrid.data.DELServiceFault;
import org.psygrid.data.client.DataElementClient;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.ElementStatusContainer;
import org.psygrid.data.model.hibernate.LSIDAuthority;
import org.psygrid.data.utils.security.NotAuthorisedFault;
import org.psygrid.datasetdesigner.model.DocTreeModel;
import org.psygrid.datasetdesigner.ui.MainFrame;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

public class DataElementClientInitializer { 

	private static final Log LOG = LogFactory.getLog(DataElementClientInitializer.class);

	private String saml;

	private DataElementClient client = null;
	Boolean delConnectionIsInitialised = false;
	boolean initialiseAttemptComplete;

	String [] elementTypes = null;
	String [] authorities = null;
	List<Document> documentSummaryArray = null;

	protected final PropertyChangeSupport propertyChangeSupport;

	private final SecurityManager securityManager = SecurityManager.getInstance();

	private Map<Role,List<String>> roles = null; 

	private MainFrame frame;

	public DataElementClientInitializer(MainFrame frame){
		this.frame = frame;	
		propertyChangeSupport=new PropertyChangeSupport(this);
		init();
	}

	/**
	 * Initialize the connection to the Data Element Library.
	 *
	 */
	public void init() {
		initialiseAttemptComplete = false;
		client = new DataElementClient();
		
		//Initialise the DELSecurity object, which is used by other classes (inc MainTree and MainMenu) to 
		//find out what permissions different roles have.
		DELSecurity.getInstance().init(this);
		retrieveInitialData();
		checkForUpdates();
	}
	
	
	private void retrieveInitialData(){
		try {
			saml = SecurityHelper.getAAQueryClient().getSAMLAssertion().toString();
			getElementTypes();
			getAuthorities();
			if (authorities != null) {
				for (String authority: authorities) {
					getDocumentsSummary(authority);
				}
			}
			initialiseAttemptComplete = true;
			delConnectionIsInitialised = true;
			getAuthoritiesForRoles();
		} catch (Exception e) {
			try {
				String[] authorities = (String[])PersistenceManager.getInstance().load(PropertiesHelper.getDELAuthoritiesLocation());
				Map<Role,List<String>> roles = (Map<Role,List<String>>)PersistenceManager.getInstance().load(PropertiesHelper.getDELAuthRolesLocation());
				String[] elementTypes = (String[])PersistenceManager.getInstance().load(PropertiesHelper.getDELTypesLocation());
				this.authorities = authorities;
				this.roles = roles;
				this.elementTypes = elementTypes;
			}
			catch (FileNotFoundException ex) {
				LOG.error("Unable to retrieve initial data from local file", ex);
			}
			catch (IOException ioe) {
				LOG.error("Unable to retrieve initial data from local file", ioe);	
			}
			initialiseAttemptComplete = true;
			delConnectionIsInitialised = false;
			LOG.error("Unable to retrieve initial data from the AAQueryClient", e);
		}
	}

	public DataElementClient getClient() {
		return client;
	}

	public boolean isDelConnectionIsInitialised() {
		return delConnectionIsInitialised;
	}

	private void getAuthorities() throws Exception{
		try {
			updateSAML();
			LSIDAuthority[] lsidAuthorities = client.getLSIDAuthorities(saml);

			//Just to play it safe, weed out any duplicates.
			List<LSIDAuthority> authorityListNoDuplicates = new ArrayList<LSIDAuthority>();

			for(int i = 0; i < lsidAuthorities.length; i++){
				boolean duplicateFound = false;
				for(LSIDAuthority auth:authorityListNoDuplicates){

					if(auth.getAuthorityID().equals(lsidAuthorities[i].getAuthorityID())){
						duplicateFound = true;
						break;
					}
				}

				if(!duplicateFound){
					authorityListNoDuplicates.add(lsidAuthorities[i]);
				}
			}

			authorities = new String[authorityListNoDuplicates.size()];

			for(int i = 0; i < authorityListNoDuplicates.size(); i++){
				authorities[i] = authorityListNoDuplicates.get(i).getAuthorityID();
			}	
		} catch (Exception e){
			LOG.error("Error retrieving del authorities", e);
			throw e;
		}
	}

	private void getDocumentsSummary(String authority) throws Exception{
		try {
			updateSAML();
			documentSummaryArray = client.getDocumentSummaryList(authority, saml);
		} catch (RemoteException e) {
			LOG.error("Error retrieving documents summary - " + e.getMessage());
			throw e;
		} catch (ServiceException e) {
			LOG.error("Error retrieving documents summary - " + e.getMessage());
			throw e;
		}
	}

	private void getElementTypes() throws Exception {
		try{
			elementTypes = client.getElementTypes();
		} catch (Exception e) {
			LOG.error("Error retrieving element types - " + e.getMessage());
			throw e;
		} 
	}

	public String[] getTypes() {
		return elementTypes;
	}

	public String[] getLSIDAuthorities(){
		return authorities;
	}

	public List<Document> getDocSummaryList(){
		return documentSummaryArray;
	}

	public String getSaml() {
		try {
			updateSAML();
		}
		catch (Exception e) {
			LOG.error("Problem occurred updating SAML.", e);
		}
		return saml;
	}

	public void setSaml(String saml) {
		this.saml = saml;
	}

	public Map<Role,List<String>> getAuthoritiesForRoles() throws Exception{
		if (!delConnectionIsInitialised && roles != null) {
			return roles;
		}
		else if (!delConnectionIsInitialised) {
			LOG.info("DEL connection has not been initialised. Unable to get authorities for roles.");
			return null;
		}
		if (roles != null) {
			return roles;
		}

		roles = new HashMap<Role,List<String>>();

		String[] authorities = this.getLSIDAuthorities();
		List<String> auths = new ArrayList<String>();
		if (authorities != null) {
			for (String auth: authorities) {
				auths.add(auth);
			}
		}

		roles.put(Role.Viewer, auths);
		try {
			roles.put(Role.Author, SecurityManager.getInstance().getAuthoritiesAsDELAuthor());
			roles.put(Role.Curator, SecurityManager.getInstance().getAuthoritiesAsDELCurator());
		}
		catch (NotAuthorisedFault ex) {
			LOG.error("Error retrieving authorities for roles - " + ex.getMessage());
			throw ex;
		}
		catch (ConnectException ex) {
			LOG.error("Error retrieving authorities for roles - " + ex.getMessage());
			throw ex;
		}
		catch (IOException ex) {
			LOG.error("Error retrieving authorities for roles - " + ex.getMessage());
			throw ex;
		}
		catch (EntrySAMLException ex) {
			LOG.error("Error retrieving authorities for roles - " + ex.getMessage());
			throw ex;
		}
		catch (RemoteServiceFault ex) {
			LOG.error("Error retrieving authorities for roles - " + ex.getMessage());
			throw ex;
		}
		return roles;
	}

	public boolean isCurator() throws Exception {
		Map<Role,List<String>> roles = getAuthoritiesForRoles();
		return (roles != null && roles.get(Role.Curator) != null && roles.get(Role.Curator).size() > 0);
	}

	public boolean isCurator(String authority) throws Exception {
		Map<Role,List<String>> roles = getAuthoritiesForRoles();
		return (roles != null && roles.get(Role.Curator) != null && roles.get(Role.Curator).contains(authority));
	}

	public boolean isAuthor() throws Exception {
		Map<Role,List<String>> roles = getAuthoritiesForRoles();
		return (roles != null && roles.get(Role.Author) != null && roles.get(Role.Author).size() > 0);
	}

	public boolean isAuthor(String authority) throws Exception {
		Map<Role,List<String>> roles = getAuthoritiesForRoles();
		return (roles != null && roles.get(Role.Author) != null && roles.get(Role.Author).contains(authority));
	}

	public boolean isViewer() throws Exception {
		Map<Role,List<String>> roles = getAuthoritiesForRoles();
		return (roles != null && roles.get(Role.Viewer) != null && roles.get(Role.Viewer).size() > 0);
	}

	public boolean isViewer(String authority) throws Exception {
		Map<Role,List<String>> roles = getAuthoritiesForRoles();
		return (roles != null && roles.get(Role.Viewer) != null && roles.get(Role.Viewer).contains(authority));
	}

	private void updateSAML() throws ConnectException, RemoteServiceFault, IOException, EntrySAMLException, NotAuthorisedFault {
		synchronized(securityManager) {
			Date keyValidity = securityManager.getKeyValidity();
			//check to see that there is a key to refresh
			if (keyValidity != null) {
				Date now = new Date();
				long thirtySeconds = 30 * 1000;
				now.setTime(now.getTime() + thirtySeconds);
				if (keyValidity.getTime() < now.getTime()) {
					securityManager.refreshKey();
				}
			}
		}

		saml = securityManager.getSAMLAssertion();
	}

	public boolean checkForUpdates() {

		//Get a list of LSIDs which could be updated. Ignore read only elements.
		List<ElementStatusContainer> current = DocTreeModel.getInstance().getCheckedOutLSIDs(true, false);
		//Get all top level elements, to include those read only elements that were excluded previously
		
		current.addAll(DocTreeModel.getInstance().getCheckedOutLSIDs(false, true));
		for (int i = current.size()-1; i >= 0; i--) {
			if (!current.get(i).getIsHeadRevision()) {
				//Remove elements that are not the head revision, because these are already known to be out of date
				current.remove(i);
			}
		}
		if (current.size() == 0) {
			return false;
		}

		//Remove duplicate elements (caused by calling getCheckedOutLSIDs twice)
		List<ElementStatusContainer> uniqueCurrent = new ArrayList<ElementStatusContainer>();
		nextContainer: for (ElementStatusContainer container: current) {
			for (ElementStatusContainer unique: uniqueCurrent) {
				if (unique.getLsid().equals(container.getLsid())) {
					continue nextContainer;
				}
			}
			uniqueCurrent.add(container);
		}
		List<ElementStatusContainer> changed = null;
		try {
			//Retrieve a list of elements whose status has changed or if they are no longer the head revision.
			changed = client.reportElementStatusChanges(uniqueCurrent, true, saml);
		}
		catch (Exception e) {
			LOG.error("Error updating elements from the library", e);
		}

		if (changed == null) {
			return false;
		}
		if (changed.size() == 0) {
			//No changes to any elements so do nothing
			return false;	
		}
		for (int i = 0; i < changed.size(); i++) {
			ElementStatusContainer element = changed.get(i);
		}
		new UpdateElementsDialog(this.frame.getDocPane(), changed);
		return true;
	}

	/**
	 * Retrieves the latest appropriate version of the element.
	 * 
	 * For a viewer, this will be the latest approved element version.
	 * For an author or curator, this will be the element head revision (i.e. pending or approved).
	 * 
	 * @param lsid - lsid of the element for which the latest version is required.
	 * @return newlsid
	 */
	public String getLatestLSID(String lsid) {
		try {
			List<String> inputList = new ArrayList<String>();
			inputList.add(lsid);
			List<List<String>> returnList =  client.getLatestRevision(inputList, saml);
			return returnList.get(0).get(1);
		}
		catch (ServiceException e){
			LOG.error("Unable to get latest LSID", e);
		}
		catch (DELServiceFault e){
			LOG.error("Unable to get latest LSID", e);
		}
		catch (NotAuthorisedFault e){
			LOG.error("Unable to get latest LSID", e);
		}
		catch (RemoteException e){
			LOG.error("Unable to get latest LSID", e);
		}
		return "";
	}
	
	public enum Role {

		/**
		 * A viewer is someone who has permission to view
		 * elements in the data element library.
		 */
		Viewer, 

		/**
		 * An author can view, edit and create elements in
		 * the data element library.
		 */
		Author,

		/**
		 * A curator can view elements and approve any pending
		 * elements.
		 */
		Curator
	};
}

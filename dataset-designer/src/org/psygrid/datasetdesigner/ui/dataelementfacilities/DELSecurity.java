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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Check the permission of the current user to perform various
 * actions on the data element library 
 * 
 * @author Lucy Bridges
 *
 */
public class DELSecurity {

	private static DELSecurity instance;

	private DataElementClientInitializer client;

	private static final Log LOG = LogFactory.getLog(DELSecurity.class);

	private DELSecurity() {
		//Private to enforce singleton
	}

	public static DELSecurity getInstance() {
		if (instance != null) {
			return instance;
		}
		instance = new DELSecurity();
		return instance;
	}

	public void init(DataElementClientInitializer client) {
		this.client = client;
	}
	
	/**
	 * Returns whether the user is allowed to view pending
	 * elements.
	 * 
	 * Will return true if the user is an author or curator
	 * but not if they are a viewer.
	 * 
	 * @return boolean
	 */
	public boolean canViewPending() {
		try {
			if (client.isAuthor() || client.isCurator()) {
				return true;
			}
		} catch (Exception e) {
			LOG.error("Problem retrieving permissions from the DataElementClientInitializer", e);
		}
		return false;
	}

	/**
	 * Returns whether the user is allowed to view pending
	 * elements for the given authority.
	 * 
	 * Will return true if the user is an author or curator
	 * but not if they are a viewer.
	 * 
	 * @parm authority
	 * @return boolean
	 */
	public boolean canViewPending(String authority) {
		if (authority == null) {
			return canViewPending();
		}
		try {
			if (client.isAuthor(authority) || client.isCurator(authority)) {
				return true;
			}
		} catch (Exception e) {
			LOG.error("Problem retrieving permissions from the DataElementClientInitializer", e);
		}
		return false;
	}
	
	/**
	 * Returns whether the current user is allowed to submit
	 * new or edited elements to the data element library.
	 * 
	 * @return boolean
	 */
	public boolean canSubmitElements() {
		try {
			if (client.isAuthor()) {
				return true;
			}
		} catch (Exception e) {
			LOG.error("Problem retrieving permissions from the DataElementClientInitializer", e);
		}
		return false;
	}
	
	/**
	 * Returns whether the current user is allowed to submit
	 * new or edited elements to the data element library for
	 * the specified authority.
	 * 
	 * @parm authority
	 * @return boolean
	 */
	public boolean canSubmitElements(String authority) {
		if (authority == null) {
			return canSubmitElements();
		}
		try {
			if (client.isAuthor(authority)) {
				return true;
			}
		} catch (Exception e) {
			LOG.error("Problem retrieving permissions from the DataElementClientInitializer", e);
		}
		return false;
	}
	
	/**
	 * Returns whether the current user is allowed to search 
	 * the data element library.
	 * 
	 * All library users should be allowed to do this.
	 * 
	 * @return boolean
	 */
	public boolean canSearchLibrary() {
		try {
			if (client.isAuthor() || client.isViewer() || client.isCurator()) {
				return true;
			}
		} catch (Exception e) {
			LOG.error("Problem retrieving permissions from the DataElementClientInitializer", e);
		}
		return false;
	}
	
	/**
	 * Returns whether the current user is allowed to search 
	 * the data element library for the specified authority.
	 * 
	 * All library users with access to the authority should 
	 * be allowed to do this.
	 * 
	 * @return boolean
	 */
	public boolean canSearchLibrary(String authority) {
		if (authority == null) {
			return canSearchLibrary();
		}
		try {
			if (client.isAuthor(authority) || client.isViewer(authority) || client.isCurator(authority)) {
				return true;
			}
		} catch (Exception e) {
			LOG.error("Problem retrieving permissions from the DataElementClientInitializer", e);
		}
		return false;
	}
	
	/**
	 * Returns whether the current user is allowed to edit
	 * elements from the library.
	 * 
	 * Only authors can edit elements. 
	 * 
	 * @return boolean
	 */
	public boolean canEditElements() {
		try {
			if (client.isAuthor()) {
				return true;
			}
		} catch (Exception e) {
			LOG.error("Problem retrieving permissions from the DataElementClientInitializer", e);
		}
		return false;
	}
	
	/**
	 * Returns whether the current user is allowed to edit
	 * elements from the library for the given authority.
	 * 
	 * Only authors can edit elements. 
	 * 
	 * @param authority
	 * @return boolean
	 */
	public boolean canEditElements(String authority) {
		if (authority == null) {
			return canEditElements();
		}
		try {
			if (client.isAuthor(authority)) {
				return true;
			}
		} catch (Exception e) {
			LOG.error("Problem retrieving permissions from the DataElementClientInitializer", e);
		}
		return false;
	}
	
	/**
	 * Returns whether the current user is allowed to approve
	 * elements from the library.
	 * 
	 * Only curators can approve elements
	 * 
	 * @return boolean
	 */
	public boolean canApproveElements() {
		try {
			if (client.isCurator()) {
				return true;
			}
		} catch (Exception e) {
			LOG.error("Problem retrieving permissions from the DataElementClientInitializer", e);
		}
		return false;
	}
	
	/**
	 * Returns whether the current user is allowed to approve
	 * elements from the library for the given authority.
	 * 
	 * Only curators can approve elements
	 * 
	 * @param authority
	 * @return boolean
	 */
	public boolean canApproveElements(String authority) {
		if (authority == null) {
			return canApproveElements();
		}
		try {
			if (client.isCurator(authority)) {
				return true;
			}
		} catch (Exception e) {
			LOG.error("Problem retrieving permissions from the DataElementClientInitializer", e);
		}
		return false;
	}
}

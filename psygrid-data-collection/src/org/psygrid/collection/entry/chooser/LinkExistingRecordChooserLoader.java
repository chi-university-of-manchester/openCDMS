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

package org.psygrid.collection.entry.chooser;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.ExceptionsHelper;
import org.psygrid.collection.entry.event.RecordSelectedListener;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.collection.entry.persistence.SecondaryIdentifierMap;
import org.psygrid.collection.entry.remote.RemoteManager;
import org.psygrid.collection.entry.remote.RemoteServiceFault;
import org.psygrid.collection.entry.security.EntrySAMLException;
import org.psygrid.common.identifier.IdentifierHelper;
import org.psygrid.common.identifier.InvalidIdentifierException;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Identifier;
import org.psygrid.data.model.hibernate.Record;
import org.psygrid.data.utils.security.NotAuthorisedFault;

/**
 * Loader to generate a list of identifiers of records for the supplied 
 * dataset that are suitable for linking to a primary record i.e. are members
 * of one of the defined secondary centres and are not already linked.
 * 
 * @author Rob Harper
 *
 */
public class LinkExistingRecordChooserLoader extends RecordChooserLoader {

	/**
	 * List of allowed centres i.e. the defined secondary centres for the
	 * centre that the primary record we are linking to is a part of
	 */
	private final List<String> secGroups;
	
	/**
	 * Dataset of the secondary record.
	 */
	private final DataSet secDs;
	
	private final RecordSelectedListener listener;
	
	public LinkExistingRecordChooserLoader(Application application, List<String> secGroups, DataSet secDs, RecordSelectedListener listener) {
		super(application);
		this.secGroups = secGroups;
		this.secDs = secDs;
		this.listener = listener;
	}

	@Override
	protected List<String> doInBackground() throws ConnectException,
			SocketTimeoutException, IOException, NotAuthorisedFault,
			RemoteServiceFault, EntrySAMLException, InvalidIdentifierException {
		return getPossibleIdentifiers();
	}

	@Override
	protected String getChooserTitle() {
		return "Select the record to link to.";
	}

	@Override
	protected RecordSelectedListener getRecordSelectedListener() {
		return listener;
	}

    protected void showNoRecordsMessage(){
        String title = Messages.getString("LinkExistingRecordChooserLoader.noRecordsTitle");
        String message = Messages.getString("LinkExistingRecordChooserLoader.noRecordsMessage");
        JOptionPane.showMessageDialog(application, message, title, 
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Get the list of identifiers that may be chosen for the secondary record.
     * 
     * @return List of identifiers
     */
	private List<String> getPossibleIdentifiers() {
		List<String> identifiers = new ArrayList<String>();
		if ( RemoteManager.getInstance().isTestDataset() ){
			//Running in test/preview mode
			//Return a single possible identifier, the identifier of the
			//current record (if there is one)
			Record r = application.getRecord();
			if ( null != r ){
				identifiers.add(r.getIdentifier().getIdentifier());
			}				
		}
		else{
			//Running in normal mode
			PersistenceManager pManager = PersistenceManager.getInstance();
			synchronized (pManager) {
	
				// get all used identifiers
				Set<String> usedIdentifiers = pManager.getConsentMap().getIdentifiers();
	
				// narrow these down to just the identifiers relating to the current
				// dataset
				String project = secDs.getProjectCode();
				Set<String> idsForProject = new HashSet<String>();
				for (String id : usedIdentifiers) {
					if (id.startsWith(project)) {
						idsForProject.add(id);
					}
				}
	
				idsForProject = filterIdentifiers(idsForProject);				
				identifiers = sortIdentifiers(idsForProject);
			}
		}
		
		return identifiers;
	}

	/**
	 * Only want to include identifiers for (a) records which are not already secondaries
	 * in dual data entry relationships, and (b) are for one of the defined secondary groups
	 * 
	 * @param identifiers All possible identifiers
	 * @return Filtered identifiers
	 */
	protected Set<String> filterIdentifiers(Set<String> identifiers) {
		PersistenceManager pManager = PersistenceManager.getInstance();
		SecondaryIdentifierMap sim = null;
		synchronized (pManager) {
			sim = pManager.getSecondaryIdentifierMap();
		}

		Set<String> filteredIdentifiers = new HashSet<String>();
		for ( String i: identifiers ){
			try{
				if ( !sim.isIdentifierSecondary(i) && secGroups.contains(IdentifierHelper.getGroupCodeFromIdentifier(i)) ){
					filteredIdentifiers.add(i);
				}
			}
			catch(InvalidIdentifierException ex){
				//this should never happen - if it does, something very wrong has happened!
				ExceptionsHelper.handleFatalException(ex);
			}
		}
		return filteredIdentifiers;
	}

	/**
	 * Sort a list of identifiers in a natural way.
	 * 
	 * @param identifiers Identifiers to sort.
	 * @return Sorted identifiers
	 */
	protected List<String> sortIdentifiers(Set<String> identifiers) {
		List<String> ids = new ArrayList<String>();
		
		List<Identifier> realIds = new ArrayList<Identifier>();
		for (String id: identifiers) {
			Identifier i =new Identifier();
			try {
				i.initialize(id);
				realIds.add(i);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		java.util.Collections.sort(realIds);
		for (Identifier id: realIds) {
			ids.add(id.getIdentifier());
		}
		return ids;
	}

}

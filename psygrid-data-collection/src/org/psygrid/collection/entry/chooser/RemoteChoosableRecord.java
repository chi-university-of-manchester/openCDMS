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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.psygrid.collection.entry.DocumentStatus;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.collection.entry.persistence.RecordsList.Item;
import org.psygrid.collection.entry.remote.RemoteManager;
import org.psygrid.collection.entry.remote.RemoteServiceFault;
import org.psygrid.collection.entry.security.DecryptionException;
import org.psygrid.collection.entry.security.EntrySAMLException;
import org.psygrid.common.identifier.IdentifierHelper;
import org.psygrid.common.identifier.InvalidIdentifierException;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.utils.security.NotAuthorisedFault;

/**
 * A Choosable to represent a Record where only its identifier
 * is known at the time of construction.
 * <p>
 * Full details of the Record are retrieved from the central data
 * repository only when it is selected in a Chooser. This is for 
 * performance reasons.
 * 
 * @author Rob Harper
 *
 */
public class RemoteChoosableRecord extends AbstractChoosableRecord<Choosable> {

	/**
	 * The identifier of the record
	 */
	protected final String identifier;
	
	protected final String sysIdentifier;

	/**
	 * If non-<code>null</code> then only document instances in
	 * this status are included in the children of the record.
	 * If <code>null</code> then all document instances are included
	 * in the children.
	 */
	protected final DocumentStatus documentStatus;

	/**
	 * If true, do not include document instances that are intended
	 * to be completed by data propagation from a primary record in
	 * the children.
	 */
	protected final boolean hideDdeSecondaries;

	/**
	 * The complete Record, only assigned if the Choosable is selected
	 * and hence its children are required.
	 */
	protected Record completeRecord;

	/*
	public RemoteChoosableRecord(String identifier, ChoosableList parent){
		super(parent);
		this.identifier = identifier;
		this.documentStatus = null;
		this.hideDdeSecondaries = false;
	}
	*/

	
	public RemoteChoosableRecord(String identifier, String sysIdentifier, DocumentStatus docStatus, boolean hideDdeSecondaries, AbstractChoosableWithChildren<? extends Choosable> parent) {
		super(parent);
		this.identifier = identifier;
		this.sysIdentifier = sysIdentifier;
		this.documentStatus = docStatus;
		this.hideDdeSecondaries = hideDdeSecondaries;
	}
	

	/*
	public RemoteChoosableRecord(String identifier, DocumentStatus docStatus, boolean hideDdeSecondaries, ChoosableList parent) {
		super(parent);
		this.identifier = identifier;
		this.documentStatus = docStatus;
		this.hideDdeSecondaries = hideDdeSecondaries;
	}
	*/

	/*
	public RemoteChoosableRecord(String identifier, boolean hideDdeSecondaries) {
		super(null);
		this.identifier = identifier;
		this.documentStatus = null;
		this.hideDdeSecondaries = hideDdeSecondaries;
	}

	/*
	public RemoteChoosableRecord(String identifier) {
		super(null);
		this.identifier = identifier;
		this.documentStatus = null;
		this.hideDdeSecondaries = false;
	}
	*/

	public String getDisplayText() {
		return identifier;
	}

	public List<Choosable> getChildren() throws ChoosableException {
		children = new ArrayList<Choosable>();
		if (completeRecord == null) {
			try {
				completeRecord = loadCompleteRecord();
			} catch (Exception e) {
				// In this case, it doesn't matter what we catch, we have to
				// rethrow
				// as ChoosableException
				throw new ChoosableException(
						"Problem retrieving record's children", e);
			}
		}
		DataSet dataSet = completeRecord.getDataSet();

		for (int i = 0, c = dataSet.numDocumentGroups(); i < c; ++i) {
			DocumentGroup docGroup = dataSet.getDocumentGroup(i);

			ChoosableDocInstanceGroup docInstanceGroup = 
				new ChoosableDocInstanceGroup(docGroup, this, false);

			boolean groupHasDocInstance = addDocumentInstances(dataSet, docInstanceGroup, null);
			if (groupHasDocInstance) {
				children.add(docInstanceGroup);
			}
		}
		return children;
	}

	public List<Choosable> getAllChildren() throws ChoosableException {
		children = new ArrayList<Choosable>();
		if (completeRecord == null) {
			try {
				completeRecord = loadCompleteRecord2();
			} catch (Exception e) {
				// In this case, it doesn't matter what we catch, we have to
				// rethrow
				// as ChoosableException
				throw new ChoosableException(
						"Problem retrieving record's children", e);
			}
		}
		DataSet dataSet = completeRecord.getDataSet();

		for (int i = 0, c = dataSet.numDocumentGroups(); i < c; ++i) {
			DocumentGroup docGroup = dataSet.getDocumentGroup(i);

			ChoosableDocInstanceGroup docInstanceGroup = 
				new ChoosableDocInstanceGroup(docGroup, this, false);

			boolean groupHasDocInstance = addDocumentInstances(dataSet, docInstanceGroup, null);
			if (groupHasDocInstance) {
				children.add(docInstanceGroup);
			}
		}
		return children;
	}


	private Record loadCompleteRecord() throws FileNotFoundException,
	IOException, DecryptionException, NotAuthorisedFault,
	RemoteServiceFault, EntrySAMLException, InvalidIdentifierException {
		PersistenceManager pManager = PersistenceManager.getInstance();
		if (documentStatus == null) {
			Record record = null;
			for (Item item: pManager.getRecordsList().getItems()) {
				if (item.getIdentifier().getIdentifier().equals(sysIdentifier)) {
					//This will return the first locally held documents found..?
					return pManager.loadRecord(item);
				}
			}

			if (record == null) {
				return RemoteManager.getInstance().getCompleteRecord(sysIdentifier);	
			}
		}
		synchronized (pManager) {
			Record record = null;
			for (Item item: pManager.getRecordsList().getItems()) {
				if (item.getIdentifier().getIdentifier().equals(sysIdentifier)) {
					record = pManager.loadRecord(item);
				}
			}
			if (record == null) {
				//must only exist remotely
				DataSet dataSet = pManager.getData().getCompleteDataSet(
                        IdentifierHelper.getProjectCodeFromIdentifier(sysIdentifier));
				record = RemoteManager.getInstance().getDocumentsByStatus(
						dataSet, sysIdentifier, documentStatus);
			}
			return record;
		}
	}

	/**
	 * Load ALL documents for a record, whether local or remote, complete or incomplete.
	 * 
	 * @return record with documents
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws DecryptionException
	 * @throws NotAuthorisedFault
	 * @throws RemoteServiceFault
	 * @throws EntrySAMLException
	 * @throws InvalidIdentifierException
	 */
	private Record loadCompleteRecord2() throws FileNotFoundException,
	IOException, DecryptionException, NotAuthorisedFault,
	RemoteServiceFault, EntrySAMLException, InvalidIdentifierException {
		PersistenceManager pManager = PersistenceManager.getInstance();

		Record record = null;

		if (documentStatus == null) {
			/*
			 * Get locally held documents (both complete and incomplete)
			 */
			Record comrecord = null;
			try {
				comrecord = pManager.loadRecord(sysIdentifier, true);
			}
			catch (FileNotFoundException e) {
				//Safe to ignore
			}
			Record increcord = null;
			try {
				increcord = (Record)pManager.loadRecord(sysIdentifier, false);
			}
			catch (FileNotFoundException e) {
				//Safe to ignore
			}
			if (comrecord != null) {
				if (increcord != null) {
					for (DocumentInstance inst: increcord.getDocInstances()) {
						comrecord.addDocumentInstance(inst);
					}
				}
				record = comrecord;
			}
			else if (increcord != null) {
				record = increcord;
			}
			
			/*
			 * Get the remote record
			 */
			if (record == null) {
				record = RemoteManager.getInstance().getCompleteRecord(sysIdentifier);	
			}
			else {
				Record remoterecord = null;
				try {
				remoterecord = (Record)RemoteManager.getInstance().getCompleteRecord(sysIdentifier);
				}
				catch (RemoteServiceFault e) {
					//No record held remotely, so ignore
				}
				if (remoterecord != null) {
					for (DocumentInstance inst: remoterecord.getDocInstances()) {
						if (!((Record)record).getDocInstances().contains(inst)) {
							record.addDocumentInstance(inst);
						}
					}
				}
			}
			return record;
		}
		else {
			synchronized (pManager) {
				DataSet dataSet = pManager.getData().getCompleteDataSet(
                        IdentifierHelper.getProjectCodeFromIdentifier(sysIdentifier));

				switch (documentStatus) {
				case LOCALLY_INCOMPLETE:
					try {
						record = (Record)pManager.loadRecord(sysIdentifier, false);
					}
					catch (FileNotFoundException e) {
						//Safe to ignore
					}
					return record;
				case READY_TO_SUBMIT:
					try {
						record = (Record)pManager.loadRecord(sysIdentifier, true);
					}
					catch (FileNotFoundException e) {
						//Safe to ignore
					}
					return record;
				default:
					//is a remote document status
					record = RemoteManager.getInstance().getDocumentsByStatus(
							dataSet, sysIdentifier, documentStatus);
				return record;
				}
			}
		}
	}

	protected boolean addDocumentInstances(DataSet dataSet,
			ChoosableDocInstanceGroup docInstanceGroup, DocumentStatus status) {
		boolean hasDocInstance = false;
		for (int i = 0, c = dataSet.numDocuments(); i < c; ++i) {
			Document doc = dataSet.getDocument(i);

			for (int j = 0, d = doc.numOccurrences(); j < d; ++j) {
				DocumentOccurrence occurrence = doc.getOccurrence(j);
				DocumentGroup docGroup = occurrence.getDocumentGroup();
				if (docGroup != null
						&& docGroup.getName().equals(docInstanceGroup.getDocumentGroup().getName())) {

					DocumentInstance docInstance = completeRecord
					.getDocumentInstance(occurrence);
					if (docInstance != null) {
						if (status == null || docInstance.getStatus().getLongName().equals(status.toStatusLongName())) {
							boolean show = true;
							if ( hideDdeSecondaries ){
								//don't display document instances completed via dual data entry
								if ( null != doc.getPrimaryDocIndex() && null != occurrence.getPrimaryOccIndex() && null != completeRecord.getPrimaryIdentifier() ){
									//instance was created via dual data entry data propagation from a primary record -
									//do not show it in the chooser
									show = false;
								}
							}
							if ( show ){
								hasDocInstance = true;
								new ChoosableDocInstance(docInstance, docInstanceGroup);
							}
						}
					}
				}
			}
		}
		return hasDocInstance;
	}

	@Override
	public Record getRecord() {
		return completeRecord;
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}


	@Override
	public String getSysIdentifier() {
		return sysIdentifier;
	}

}

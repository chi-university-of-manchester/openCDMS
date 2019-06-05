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

package org.psygrid.collection.entry.util;

import java.io.IOException;

import org.psygrid.collection.entry.persistence.DataSetSummary;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.common.identifier.IdentifierHelper;
import org.psygrid.common.identifier.InvalidIdentifierException;
import org.psygrid.data.model.hibernate.DataSet;import org.psygrid.data.model.hibernate.Record;

/**
 * Class containing convenience methods for constructing Record objects.
 * 
 * @author Rob Harper
 *
 */
public class RecordHelper {

	/**
	 * Construct a new record object with the given identifier, and consent
	 * extracted from the consent map.
	 * 
	 * @param identifier The identifier
	 * @return The new record.
	 * @throws InvalidIdentifierException
	 * @throws IOException
	 */
	public static Record constructRecord(final String identifier) throws InvalidIdentifierException, IOException {
        PersistenceManager pManager = PersistenceManager.getInstance();
        return constructRecord(identifier, pManager);
	}
	
	/**
	 * Construct a new record object with the given identifier, and consent
	 * extracted from the consent map.
	 * 
	 * @param identifier The identifier
	 * @param pManager The {@link PersistenceManager}
	 * @return The new record.
	 * @throws InvalidIdentifierException
	 * @throws IOException
	 */
	public static Record constructRecord(final String identifier, final PersistenceManager pManager)
	throws InvalidIdentifierException, IOException {
		Record record = null;
		synchronized(pManager){
        	DataSetSummary dss = pManager.getData().getDataSetSummary(IdentifierHelper.getProjectCodeFromIdentifier(identifier));
        	DataSet ds = pManager.loadDataSet(dss);
            record = ds.generateInstance();
        	configure(record, identifier, pManager);
        }
		return record;
	}
	
	public static Record constructRecord(final Record record) throws InvalidIdentifierException {
        PersistenceManager pManager = PersistenceManager.getInstance();
		return constructRecord(record, pManager);
	}
	
	public static Record constructRecord(final Record record, final PersistenceManager pManager) throws InvalidIdentifierException {
		Record newRecord = null;
		synchronized(pManager){
        	DataSet ds = record.getDataSet();
        	newRecord = ds.generateInstance();
        	configure(newRecord, record.getIdentifier().getIdentifier(), pManager);
        }
		return newRecord;
	}

	public static Record constructRecord(final String identifier, final DataSet dataSet) throws InvalidIdentifierException {
		PersistenceManager pManager = PersistenceManager.getInstance();
		Record newRecord = null;
		synchronized(pManager){
        	newRecord = dataSet.generateInstance();
        	configure(newRecord, identifier, pManager);
        }
		return newRecord;
	}

	private static void configure(final Record record, final String identifier, final PersistenceManager pManager) throws InvalidIdentifierException {
        record.generateIdentifier(identifier);
        pManager.getConsentMap().addConsentFromMapToRecord(record);
        record.setSecondaryIdentifier(pManager.getSecondaryIdentifierMap().get(identifier));
        record.setPrimaryIdentifier(pManager.getSecondaryIdentifierMap().getPrimary(identifier));
	}
}

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

package org.psygrid.collection.entry.replication;

import java.util.List;

import org.psygrid.data.model.hibernate.DocumentInstance;
import org.psygrid.data.model.hibernate.Record;

/**
 * Class to contain the result of linking two records and
 * performing data replication between the two.
 * 
 * @author Rob Harper
 *
 */
public class DdeCopyResult{
	
	/**
	 * List of the names of documents that could not be copied from the
	 * primary record to the secondary record, due to insufficient consent 
	 * in the secondary.
	 */
	final private List<String> docsNotCopied;
	
	/**
	 * List of document instances in the secondary record whose status 
	 * must be reset back to "Pending Approval"
	 */
	final private List<DocumentInstance> docsToResetStatus;
	
	/**
	 * List of document instances in the primary record whose status
	 * must be set to "Pending Approval" (after reverse copy from the
	 * secondary record).
	 */
	final private List<DocumentInstance> docsToSetToPending;
	
	/**
	 * The primary local record after data replication.
	 */
	final private Record primaryLocalRecord;
	
	/**
	 * The primary local record (incomplete document instances) after
	 * data replication.
	 */
	final private Record primaryLocalIncompRecord;
	
	/**
	 * If true, then the primary remote record must be committed to
	 * the data repository.
	 */
	final private boolean savePrimaryRemoteRecord;
	
	/**
	 * If true, then the secondary remote record must be committed to the
	 * data repository.
	 */
	final private boolean saveSecondaryRemoteRecord;
	
	/**
	 * If True then there is a primary record that needs to be persisted locally.
	 */
	final private boolean savePrimaryLocalRecord;
	
	/**
	 * If True then there is a primary record with incomplete document instances
	 * that needs to be persisted locally.
	 */
	final private boolean savePrimaryLocalIncompRecord;
	
	/**
	 * If True then there is a secondary record with incomplete document
	 * instances that needs to be persisted locally.
	 */
	final private boolean saveSecondaryLocalIncompRecord;
	
	public DdeCopyResult(
			List<String> docsNotCopied, List<DocumentInstance> docsToResetStatus, List<DocumentInstance> docsToSetToPending,
			boolean savePrimaryRemoteRecord, boolean saveSecondaryRemoteRecord,
			boolean savePrimaryLocalRecord, boolean savePrimaryLocalIncompRecord,
			boolean saveSecondaryLocalIncompRecord, 
			Record primaryLocalRecord, Record primaryLocalIncompRecord){
		this.docsNotCopied = docsNotCopied;
		this.docsToResetStatus = docsToResetStatus;
		this.docsToSetToPending = docsToSetToPending;
		this.primaryLocalRecord = primaryLocalRecord;
		this.primaryLocalIncompRecord = primaryLocalIncompRecord;
		this.savePrimaryRemoteRecord = savePrimaryRemoteRecord;
		this.saveSecondaryRemoteRecord = saveSecondaryRemoteRecord;
		this.savePrimaryLocalRecord = savePrimaryLocalRecord;
		this.savePrimaryLocalIncompRecord = savePrimaryLocalIncompRecord;
		this.saveSecondaryLocalIncompRecord = saveSecondaryLocalIncompRecord;
	}

	public List<String> getDocsNotCopied() {
		return docsNotCopied;
	}

	public List<DocumentInstance> getDocsToResetStatus() {
		return docsToResetStatus;
	}

	public List<DocumentInstance> getDocsToSetToPending() {
		return docsToSetToPending;
	}

	public boolean isSavePrimaryRemoteRecord() {
		return savePrimaryRemoteRecord;
	}

	public boolean isSaveSecondaryRemoteRecord() {
		return saveSecondaryRemoteRecord;
	}

	public Record getPrimaryLocalRecord() {
		return primaryLocalRecord;
	}

	public boolean isSavePrimaryLocalRecord() {
		return savePrimaryLocalRecord;
	}

	public Record getPrimaryLocalIncompRecord() {
		return primaryLocalIncompRecord;
	}

	public boolean isSavePrimaryLocalIncompRecord() {
		return savePrimaryLocalIncompRecord;
	}

	public boolean isSaveSecondaryLocalIncompRecord() {
		return saveSecondaryLocalIncompRecord;
	}
	
}

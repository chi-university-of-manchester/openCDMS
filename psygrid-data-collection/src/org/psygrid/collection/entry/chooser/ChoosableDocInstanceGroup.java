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

import java.util.List;

import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.data.model.hibernate.DocumentGroup;
import org.psygrid.data.model.hibernate.Status;

/**
 * Used to list document instances having a particular status, e.g
 * pending or incomplete. 
 * 
 * Document groups with no document instances are not shown and no 
 * new documents can be created from this view.
 *
 */
public class ChoosableDocInstanceGroup extends AbstractChoosableDocGroup<ChoosableDocInstance> {

	public ChoosableDocInstanceGroup(DocumentGroup documentGroup,
			RemoteChoosableRecord parent) {
		super(documentGroup, parent);	
		parent.addChild(this);
	}

	public ChoosableDocInstanceGroup(DocumentGroup documentGroup,
			RemoteChoosableRecord parent, boolean autoAddChild) {
		super(documentGroup, parent);
		if ( autoAddChild ){
			parent.addChild(this);
		}
	}

	public boolean isLocked() {

		if (this.getParent().isLocked()) {
			return true;
		}

		String recordIdentifier = this.getParent().getDisplayText();

		Status status = PersistenceManager.getInstance().getRecordStatusMap().getStatusForRecord(recordIdentifier);

		if ((getDocumentGroup().getPrerequisiteGroups() == null || getDocumentGroup().getPrerequisiteGroups().size() == 0)
				&& (getDocumentGroup().getAllowedRecordStatus() == null || getDocumentGroup().getAllowedRecordStatus().size() == 0)) {
			return false; //No dependancies, can be viewed whatever
		}

		if (getDocumentGroup().getAllowedRecordStatus().contains(status)) {	

			if (getDocumentGroup().getPrerequisiteGroups() == null || getDocumentGroup().getPrerequisiteGroups().size() == 0) {
				return false;
			}

			//check prerequisite document groups status
			// if any are locked then this docGroup is locked
			List<DocumentGroup> prerequisites = getDocumentGroup().getPrerequisiteGroups();
			for (DocumentGroup docGroup: prerequisites) {
				if ( checkPrerequisiteDocGroup(docGroup, status) ) {
					return true;
				}
			}
			return false;	//all prerequisites are allowed
		}

		return true;
	}

	/**
	 * Check whether a DocumentGroup should be locked (if not, check its DocumentGroups) 
	 * 
	 * Checks prerequisites against current record status.
	 * 
	 * Returns true if this DocumentGroup should be LOCKED.
	 * 
	 * @param prerequisite
	 * @return boolean
	 */
	private boolean checkPrerequisiteDocGroup(DocumentGroup prerequisite, Status status) {
		//check that the current record is allowed to access this DocGroup
		if (prerequisite.getAllowedRecordStatus().contains(status)) {

			//now check whether the prerequisite groups are accessible and have been completed.

			if (prerequisite.getPrerequisiteGroups() == null || prerequisite.getPrerequisiteGroups().size() == 0) {
				return false;
			}

			for (DocumentGroup group: prerequisite.getPrerequisiteGroups()) {
				if (checkPrerequisiteDocGroup(group, status)) {
					return true;
				}
			}
			return false;	//all prerequisites are allowed
		}
		return true;	//record doesn't have allowed status
	}	
}

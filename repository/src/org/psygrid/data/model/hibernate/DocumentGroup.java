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

package org.psygrid.data.model.hibernate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class to represent a logical grouping of documents
 * within a dataset.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_doc_groups"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class DocumentGroup extends Component {

    /**
     * The label for the group.
     * <p>
     * The label is intended to be used for displaying the number 
     * of the group within its dataset.
     */
    protected String label;
    
    /**
     * Record statuses that are permitted to fill in documents 
     * within this DocumentGroup
     */
    protected List<Status> allowedRecordStatus = new ArrayList<Status>();
    
    /**
     * DocumentGroups that are prerequisites of this one and so
     * should have been completed before starting documents in this
     * group
     */
    protected List<DocumentGroup> prerequisiteGroups = new ArrayList<DocumentGroup>();
    
    /**
     * The State a Record should be updated to once the Documents in 
     * this DocumentGroup have been completed
     */
    protected Status updateStatus;
    
    /**
     * Default no-arg constructor.
     */
    public DocumentGroup() {}
    
    /**
     * Constructor that accepts the name of the group.
     * 
     * @param name The name of the group.
     */
    public DocumentGroup(String name){
        super(name);
    }

    /**
     * Get the label for the group.
     * <p>
     * The label is intended to be used for displaying the number 
     * of the group within its dataset.
     * 
     * @return The label for the group.
     * @hibernate.property column="c_label"
     */
    public String getLabel() {
        return label;
    }

    /**
     * Set the label for the group.
     * <p>
     * The label is intended to be used for displaying the number 
     * of the group within its dataset.
     * 
     * @param label The label for the group.
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * 
     * Get record statuses that are permitted to fill in documents 
     * within this DocumentGroup
     * 
     * @return allowRecordStatuses
     * 
     * @hibernate.list cascade="none"
     *                 table="t_allowed_record_statuses" batch-size="100"
     * @hibernate.key column="c_document_group_id"
     * @hibernate.many-to-many class="org.psygrid.data.model.hibernate.Status"
     *                         column="c_status_id"
     * @hibernate.list-index column="c_index"
     */
    public List<Status> getAllowedRecordStatus() {
		return allowedRecordStatus;
	}

    /**
     * Add a Record Status that is permitted to fill in Documents
     * within this DocumentGroup
     * 
     * @param recordStatus
     */
    public void addAllowedRecordStatus(Status recordStatus) {
    	allowedRecordStatus.add(recordStatus);
    }
    
    /**
     * Set the Record Statuses permitted to access Documents within
     * this DocumentGroup
     * 
     * @param recordStatuses
     */
	public void setAllowedRecordStatus(List<Status> allowedRecordStatus) {
		this.allowedRecordStatus = allowedRecordStatus;
	}

	/**
	 * 
     * Get the DocumentGroups that are prerequisites of this one and so
     * should have been completed before starting documents in this
     * group
     * 
	 * @return prerequisiteGroups
	 * 
	 * TODO update hibernate column names
	 * @hibernate.list cascade="none"
     *                 table="t_prerequisite_groups" batch-size="100"
     * @hibernate.key column="c_document_group_id"
     * @hibernate.many-to-many class="org.psygrid.data.model.hibernate.DocumentGroup"
     *                         column="c_prerequisite_id"
     * @hibernate.list-index column="c_index"
	 */
	public List<DocumentGroup> getPrerequisiteGroups() {
		return prerequisiteGroups;
	}

    /**
     * Specify a DocumentGroup that is a prerequisite of this
     * DocumentGroup and so should have been completed before starting
     * Documents within this one.
     * 
     * @param documentGroup
     */
	public void addPrerequisiteGroup(DocumentGroup  documentGroup) {
		prerequisiteGroups.add(documentGroup);
	}
	
	public void setPrerequisiteGroups(List<DocumentGroup> prerequisiteGroups) {
		this.prerequisiteGroups = prerequisiteGroups;
	}

	/**
	 * 
     * The State a Record should be updated to once the Documents in 
     * this DocumentGroup have been completed
     * 
	 * @return updateStatus
	 *
     * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.Status"
     *                         column="c_update_status_id"
	 *						   cascade="none"
	 *						   update="true"
	 *						   not-null="false"
	 */
	public Status getUpdateStatus() {
		return updateStatus;
	}

    /**
     * Specify the Status to be applied to a Record upon completion of 
     * this DocumentGroup
     * 
     * @param updateStatus
     */
	public void setUpdateStatus(Status updateStatus) {
		this.updateStatus = updateStatus;
	}

	public org.psygrid.data.model.dto.DocumentGroupDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        //check for an already existing instance of a dto object for this 
        //document group in the map of references
        org.psygrid.data.model.dto.DocumentGroupDTO dtoDG = null;
        if ( dtoRefs.containsKey(this)){
            dtoDG = (org.psygrid.data.model.dto.DocumentGroupDTO)dtoRefs.get(this);
        }
        if ( null == dtoDG ){
            //an instance of the document group has not already
            //been created, so create it, and add it to the
            //map of references
            dtoDG = new org.psygrid.data.model.dto.DocumentGroupDTO();
            dtoRefs.put(this, dtoDG);
            toDTO(dtoDG, dtoRefs, depth);
        }
        
        if (null != updateStatus) {
        	dtoDG.setUpdateStatus(((Status)updateStatus).toDTO(dtoRefs, depth));
        }
        
        if (null != getAllowedRecordStatus()) {
        	org.psygrid.data.model.dto.StatusDTO[] recordStatuses = new org.psygrid.data.model.dto.StatusDTO[getAllowedRecordStatus().size()];
        	for (int i = 0; i < getAllowedRecordStatus().size(); i++) {
        		recordStatuses[i] = ((Status)getAllowedRecordStatus().get(i)).toDTO(dtoRefs, depth);
        	}
        	dtoDG.setAllowedRecordStatus(recordStatuses);
        }
        
        if (null != getPrerequisiteGroups()) {
        	org.psygrid.data.model.dto.DocumentGroupDTO[] groups = new org.psygrid.data.model.dto.DocumentGroupDTO[getPrerequisiteGroups().size()];
        	for (int i = 0; i < getPrerequisiteGroups().size(); i++) {
        		groups[i] = ((DocumentGroup)getPrerequisiteGroups().get(i)).toDTO(dtoRefs, depth);
        	}
        	dtoDG.setPrerequisiteGroups(groups);
        }
        
        
        return dtoDG;
    }
    
    public void toDTO(org.psygrid.data.model.dto.DocumentGroupDTO dtoDG, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        super.toDTO(dtoDG, dtoRefs, depth);
        dtoDG.setLabel(this.label);
    }
}

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

package org.psygrid.data.model.dto;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class DocumentInstanceDTO extends StatusedInstanceDTO {

	protected Long occurrenceId;

	protected ResponseDTO[] responses = new ResponseDTO[0];

	protected SecOccInstanceDTO[] secOccInstances = new SecOccInstanceDTO[0];

	/**
	 * The toString representation of the RBACAction used to control access to this
	 * document 
	 */
	protected String action;

	/**
	 * Stores whether the document instance has been used to trigger 
	 * randomisation. Will be false if randomisation has been turned down 
	 * for this document instance.
	 * 
	 * Will be null if randomisation is not used or has not yet been applied
	 * for this document instance.
	 */
	protected Boolean isRandomised = null;

	   /**
     * The toString representation of the RBACAction, which if present, will enable
     * the relevant users to edit the document instance. 
     * 
     * If null, it is assumed that the document instance is read only for any user 
     * who can access it.
     */
    protected String editableAction;
    
    /**
     * A non persisted reference to whether this document instance can be edited
     * or is to be viewed read-only.
     * 
     * Set after the EditableAction has been checked, and then used by CoCoA
     */
    protected boolean editingPermitted;
	
	public DocumentInstanceDTO() {
		super();
	}

	public Long getOccurrenceId() {
		return occurrenceId;
	}

	public void setOccurrenceId(Long occurrenceId) {
		this.occurrenceId = occurrenceId;
	}

	public ResponseDTO[] getResponses() {
		return responses;
	}

	public void setResponses(ResponseDTO[] responses) {
		this.responses = responses;
	}

	public SecOccInstanceDTO[] getSecOccInstances() {
		return secOccInstances;
	}

	public void setSecOccInstances(SecOccInstanceDTO[] secOccInstances) {
		this.secOccInstances = secOccInstances;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Boolean getIsRandomised() {
		return isRandomised;
	}

	public void setIsRandomised(Boolean isRandomised) {
		this.isRandomised = isRandomised;
	}

	/**
	 * Get the toString representation of the RBACAction, which if present, 
	 * will enable the relevant users to edit the document instance. 
     * 
     * If null, it is assumed that the document instance is editable by any 
     * user who can access it, this is for backwards compatibility purposes.
	 * 
	 * @return editableAction
	 */
	public String getEditableAction() {
		return editableAction;
	}

	/**
	 * Set the editableAction, using the toString representation of the
	 * relevant RBACAction. If present this will indicate the users able
	 * to edit the document instance.
	 * 
	 * If null, it is assumed that the document instance is editable by any 
     * user who can access it, this is for backwards compatibility purposes.
	 * 
	 * @param editableAction
	 */
	public void setEditableAction(String editableAction) {
		this.editableAction = editableAction;
	}

	/**
	 * Temporary variable, not persisted by hibernate, to indicate
	 * whether this document instance can be edited, based on the
	 * editableAction RBACAction.
	 * 
	 * @return isEditingPermitted
	 */
	public boolean isEditingPermitted() {
		return editingPermitted;
	}
	
	/**
	 * Temporary variable, not persisted by hibernate, to indicate
	 * whether this document instance can be edited, based on the
	 * editableAction RBACAction.
	 * 
	 * @param editingPermitted
	 */
	public void setEditingPermitted(boolean editingPermitted) {
		this.editingPermitted = editingPermitted;
	}

	
	public org.psygrid.data.model.hibernate.DocumentInstance toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
		org.psygrid.data.model.hibernate.DocumentInstance hDI = null;
		if ( hRefs.containsKey(this) ){
			hDI = (org.psygrid.data.model.hibernate.DocumentInstance)hRefs.get(this);
		}
		else{
			hDI = new org.psygrid.data.model.hibernate.DocumentInstance();
			hRefs.put(this, hDI);
			toHibernate(hDI, hRefs);
		}
		return hDI;
	}

	public void toHibernate(org.psygrid.data.model.hibernate.DocumentInstance hDI, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
		super.toHibernate(hDI, hRefs);

		if ( null != this.occurrenceId ){
			hDI.setOccurrenceId(this.occurrenceId);
		}

		hDI.setAction(action);
		hDI.setIsRandomised(isRandomised);
		hDI.setEditableAction(editableAction);
		hDI.setEditingPermitted(editingPermitted);
		
		Set<org.psygrid.data.model.hibernate.Response> hResponses = hDI.getResponses();
		for ( int i=0; i<this.responses.length; i++ ){
			ResponseDTO resp = this.responses[i];
			if ( null != resp ){
				hResponses.add(resp.toHibernate(hRefs));
			}
		}

		List<org.psygrid.data.model.hibernate.SecOccInstance> hSOIs = hDI.getSecOccInstances();
		for ( int i=0; i<this.secOccInstances.length; i++ ){
			SecOccInstanceDTO soi = this.secOccInstances[i];
			if ( null != soi ){
				hSOIs.add(soi.toHibernate(hRefs));
			}
		}
	}

}

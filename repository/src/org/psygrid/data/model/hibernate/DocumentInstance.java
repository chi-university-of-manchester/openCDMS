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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.psygrid.data.model.IValue;
import org.psygrid.security.RBACAction;

/**
 * Class to represent an instance of a Document of a DataSet
 * in a Record.
 * 
 * @author Rob Harper
 * 
 * @hibernate.joined-subclass table="t_doc_insts"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class DocumentInstance extends StatusedInstance {

    /**
     * The occurrence of the referenced element that this instance
     * applies to.
     * <p>
     * If the element has no defined occurrences, this should be 
     * <code>null</code>. Otherwise, this should reference one of
     * the occurrences associated with the element.
     */
    protected DocumentOccurrence occurrence;
    
    protected Long occurrenceId;
    
    protected Set<Response> responses = new HashSet<Response>();
    
    protected List<SecOccInstance> secOccInstances = new ArrayList<SecOccInstance>();
    
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
     * The toString representation of the RBACAction used to control access to this
     * document 
     */
    protected String action;
    
    /**
     * The toString representation of the RBACAction, which if present, will enable
     * the relevant users to edit the document instance. 
     * 
     * If null, it is assumed that the document instance is editable for any user 
     * who can access it, this is for backwards compatibility purposes.
     */
    protected String editableAction;
    
    /**
     * A non persisted reference to whether this document instance can be edited
     * or is to be viewed read-only.
     * 
     * Set after the EditableAction has been checked, and then used by CoCoA
     */
    protected boolean editingPermitted;
    
    @Deprecated
    protected transient boolean isEditable;
    
    public DocumentInstance() {
        super();
    }

    /**
	 * Get the occurrence of the referenced element that this instance
	 * applies to.
	 * <p>
	 * If the element has no defined occurrences, this should be 
	 * <code>null</code>. Otherwise, this should reference one of
	 * the occurrences associated with the element.
	 * 
	 * @return The occurrence.
     * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.DocumentOccurrence"
     *                        column="c_occurrence_id"
     *                        not-null="false"
     *                        cascade="none"
     */
    public DocumentOccurrence getOccurrence() {
        return occurrence;
    }

    public void setOccurrence(DocumentOccurrence occurrence) {
        this.occurrence = (DocumentOccurrence)occurrence;
    }

    public Long getOccurrenceId() {
        return occurrenceId;
    }

    public void setOccurrenceId(Long occurrenceId) {
        this.occurrenceId = occurrenceId;
    }

    /**
     * 
     * @return
     * 
     * @hibernate.set cascade="all" inverse="true" batch-size="100"
     * @hibernate.one-to-many class="org.psygrid.data.model.hibernate.Response"
     * @hibernate.key column="c_doc_inst_id" not-null="false"
     */
    public Set<Response> getResponses() {
        return responses;
    }

    public void setResponses(Set<Response> responses) {
        this.responses = responses;
    }

	/**
	 * Add a single child response to this DocumentInstance's
	 * collection of responses.
	 * 
	 * @param child The response to add as a child.
	 * @throws ModelException if the response cannot be added.
	 */
    public void addResponse(Response response) throws ModelException {
        Response r = (Response)response;
        checkNewInstance(r);
        addInstanceServer(r);
        propertyChangeSupport.firePropertyChange(null, null, null);
    }

	/**
	 * Retrieve all responses associated with a specific
	 * entry from the document instance's collection of responses.
	 * 
	 * @param entry The entry that the responses to retrieve
	 * are associated with.
	 * @return List of responses that are associated with the 
	 * given entry.
	 */
    public List<Response> getResponses(Entry entry) {
        List<Response> children = new ArrayList<Response>();
        for (Response r:responses){
            if ( r.getEntry().equals(entry)){
                children.add(r);
            }
        }
        return children;
    }

	/**
	 * Retrieve the single response associated with a specific entry
	 * and a specific section occurrence from the document instance's 
	 * collection of responses.
	 * 
	 * @param entry The entry that the response to retrieve
	 * are associated with.
	 * @param occurrence The section occurrence that the response to
	 * retrieve is associated with.
	 * @return The response, or <code>null</code> if no response found 
	 * for the given entry and section occurrence.
	 * @throws ModelException if the response
	 */
    public Response getResponse(Entry entry, SectionOccurrence occurrence) {
        Response resp = null;
        for (Response r:responses){
            if ( r.getEntry().equals(entry) && null != r.getSectionOccurrence() && r.getSectionOccurrence().equals(occurrence)){
                resp = r;
                //TODO: break;
            }
        }
        return resp;
    }

	/**
	 * Retrieve the single response associated with a specific entry
	 * and a specific section occurrence instance from the document 
	 * instance's collection of responses.
	 * 
	 * @param entry The entry that the response to retrieve
	 * are associated with.
	 * @param occurrence The section occurrence instance that the 
	 * response to retrieve is associated with.
	 * @return The response, or <code>null</code> if no response found 
	 * for the given entry and section occurrence instance.
	 */
    public Response getResponse(Entry entry, SecOccInstance secOccInst) {
        Response resp = null;
        for (Response r:responses){
            
            if ( r.getEntry().equals(entry) && null != r.getSecOccInstance() && r.getSecOccInstance().equals(secOccInst)){
                resp = r;
                //TODO: break;
            }
        }
        return resp;
    }

    /**
     * @hibernate.list cascade="all"
     * @hibernate.one-to-many class="org.psygrid.data.model.hibernate.SecOccInstance"
     * @hibernate.key column="c_doc_inst_id" not-null="false"
     * @hibernate.list-index column="c_index"
     */
    public List<SecOccInstance> getSecOccInstances() {
        return secOccInstances;
    }

    public void setSecOccInstances(List<SecOccInstance> secOccInstances) {
        this.secOccInstances = secOccInstances;
    }

	/**
	 * Add a section occurrence instance to this document instance.
	 * 
	 * @param soi The section occurrence instance to add.
	 * @throws ModelException if the section occurrence instance being 
	 * added is <code>null</code>.
	 */
    public void addSecOccInstance(SecOccInstance soi) throws ModelException {
        if ( null == soi ){
            throw new ModelException("Cannot add a null section occurrence instance");
        }
        this.secOccInstances.add((SecOccInstance)soi);
    }

	/**
	 * Add a section occurrence instance to this document instance, at the
	 * specified index in the document instances list of section 
	 * occurrence instances.
	 * 
	 * @param soi The section occurrence instance to add.
	 * @param index The index to insert the section occurrence instance at.
	 * @throws ModelException if the section occurrence instance being 
	 * added is <code>null</code>; if the specified index is not valid.
	 */
    public void addSecOccInstance(SecOccInstance soi, int index) throws ModelException {
        if ( null == soi ){
            throw new ModelException("Cannot add a null section occurrence instance");
        }
        try{
        	this.secOccInstances.add(index, (SecOccInstance)soi);
        }
        catch (IndexOutOfBoundsException ex){
        	throw new ModelException("The specified index is not valid", ex);
        }
    }

	/**
	 * Add a section occurrence instance to this document instance, at the
	 * specified index in the document instances list of section 
	 * occurrence instances.
	 * 
	 * @param soi The section occurrence instance to add.
	 * @param index The index to insert the section occurrence instance at.
	 * @param reason The reason the section was added
	 * @throws ModelException if the section occurrence instance being 
	 * added is <code>null</code>; if the specified index is not valid.
	 */
    public void addSecOccInstance(SecOccInstance soi, int index, String reason) throws ModelException {
        if ( null == soi ){
            throw new ModelException("Cannot add a null section occurrence instance");
        }
        try{
        	this.secOccInstances.add(index, (SecOccInstance)soi);
    		Provenance p = new Provenance(null, (SecOccInstance)soi);
    		p.setComment(reason);
    		this.provItems.add(p);
        }
        catch (IndexOutOfBoundsException ex){
        	throw new ModelException("The specified index is not valid", ex);
        }
    }

	/**
	 * Retrieve a single section occurrence instance for the document
	 * instance, by its index.
	 * 
	 * @param index The index of the section occurrence instance to retrieve.
	 * @return The section occurrence instance.
	 * @throws ModelException if no section occurrence instance exists for
	 * the given index.
	 */
    public SecOccInstance getSecOccInstance(int index) throws ModelException {
     	int counter = -1;
    	for ( SecOccInstance soi: secOccInstances ){
    		if ( !soi.isDeleted() ){
    			counter++;
    			if ( counter == index ){
    				return soi;
    			}
    		}
    	}
        throw new ModelException("No section occurrence instance exists for the given index");
    }

	/**
	 * Retrieve a list of section occurrence instances that all relate to
	 * the given section occurrence.
	 * 
	 * @param so The section occurrence to get instances for.
	 * @return The list of section occurrence instances.
	 */
    public List<SecOccInstance> getSecOccInstances(SectionOccurrence so) {
        List<SecOccInstance> sois = new ArrayList<SecOccInstance>();
        for (SecOccInstance soi: this.secOccInstances){
            if ( soi.getSectionOccurrence().equals(so) && !soi.isDeleted() ){
                sois.add(soi);
            }
        }
        return sois;
    }

	/**
	 * Retrieve the number of section occurrence instances for this
	 * document instance.
	 * 
	 * @return The number of section occurrence instances.
	 */
    public int numSecOccInstances() {
    	int count = 0;
    	for ( SecOccInstance soi: secOccInstances ){
    		if ( !soi.isDeleted() ){
    			count++;
    		}
    	}
        return count;
    }

    public void insertSecOccInstance(SecOccInstance soi, int index) throws ModelException {
    	
    }
    
	/**
	 * Remove the given section occurrence instance. All
	 * responses relating to the section occurrence instance are
	 * removed too.
	 * 
	 * @param index The index of the section occurrence instance to remove.
	 * @param reason The reason why the section occurrence instance is being removed.
	 * 
	 * @throws ModelException if no section occurrence instance does not exist.
	 */
    public void removeSecOccInstance(SecOccInstance secOccInst, String reason) throws ModelException {
    	int index = secOccInstances.indexOf(secOccInst);
    	if ( index < 0 ){
    		throw new ModelException("The section occurrence instance does not exist");
    	}
        SecOccInstance soi = secOccInstances.get(index);
        soi.setDeleted(true);
		Provenance p = new Provenance(soi, null);
		p.setComment(reason);
		this.provItems.add(p);
		//also, all the responses related to this sec occ inst need to be
		//deleted too!
		Iterator<Response> it = responses.iterator();
		while ( it.hasNext() ){
			Response r = it.next();
			if ( soi.equals(r.getSecOccInstance()) ){
				r.setDeleted(true);
			}
		}
    }
    
    public void attach(DocumentOccurrence docO){
        
        super.attach(docO.getDocument());
        
        this.occurrence = docO;
        this.occurrenceId = null;
        
        for(Response r:this.responses){
            boolean attached = false;
            Long entryId = null;
            if ( null != r.getEntryId() ){
                entryId = r.getEntryId();
            }
            else{
                //preserve backwards compatability with records detached
                //prior to the introduction of Response.entryId
                entryId = r.getEntry().getId();
            }
            for (Entry e:docO.getDocument().getEntries()){
                if ( entryId.equals(e.getId()) ){
                    r.attach(e);
                    attached = true;
                    break;
                }
            }
            if ( !attached ){
                throw new ModelException("Failed to attach response id="+r.getId()+" - no entry exists with id="+entryId);
            }
        }
        
        for(SecOccInstance soi:this.secOccInstances){
            boolean attached = false;
            Long secOccId = null;
            if ( null != soi.getSectionOccurrenceId() ){
                secOccId = soi.getSectionOccurrenceId();
            }
            else{
                //preserve backwards compatability with records detached
                //prior to the introduction of SecOccInstance.sectionOccurrenceId
                secOccId = soi.getSectionOccurrence().getId();
            }
            for (Section s:docO.getDocument().getSections()){
                for (SectionOccurrence so: s.getOccurrences()){
                    if ( secOccId.equals(so.getId()) ){
                        soi.attach(so);
                        attached = true;
                        break;
                    }
                }
            }
            if ( !attached ){
                throw new ModelException("Failed to attach section occurrence instance id="+soi.getId()+
                        " - no section occurrence exists with id="+secOccId);
            }
        }
        
   }
    
    public void detach(){
        super.detach();
        
        if(this.occurrence != null){
	        this.occurrenceId = this.occurrence.getId();
	        this.occurrence = null;
        }
        
        for(Response r:this.responses){
            r.detach();
        }
        
        for(SecOccInstance soi:this.secOccInstances){
            soi.detach();
        }
       
    }
    
	/**
	 * Detach the document instance from the record and the dataset.
	 * <p>
	 * This is the safest way to handle a document instance which is 
	 * required to be moved between Records.
	 * 
	 */
    public void detachFromRecord(){
    	getRecord().detachDocumentInstance(this);
    	detach();
    	this.setRecord(null);
    }
    
    /**
     * Method that does most of the work for adding a child to the
     * element instance, except for performing validation checks. It is
     * assumed that these check will have been performed when the child
     * was added by the client.
     * 
     * @param e The element instance to add as a child
     */
    public void addInstanceServer(Response r) {
        r.setRecord(this.record);
        //in case the children of this child have been added prior to
        //this point we must traverse the graph of children to ensure that
        //the references to the record are correct
        r.addChildTasks(this.record);
        responses.add(r);
        r.setDocInstance(this);
    }
    
    private void checkNewInstance(Response child) throws ModelException{
        
        //check that this is a valid child to add
        //i.e. the element that the child references must be
        //a child element of the element that this instance
        //references.
        boolean validChild = false;
        for (Entry e:this.getOccurrence().getDocument().getEntries()){
            if (child.getEntry().equals(e)){
            	//Set the role based action to control whether this response can be viewed.
            	child.setAccessAction(e.getResponseAccessAction());
            	//Set the role based action to control whether this response can be edited.
            	child.setEditableAction(e.getResponseEditableAction());
                validChild = true;
                break;
            }
        }
        if ( !validChild ){
            throw new ModelException("Cannot add response - it is not a valid child of the parent");
        }
        
        //check that the child is not a duplicate
        boolean uniqueChild = true;
        for (Response r:this.responses){
            if (r.equals(child)){
                uniqueChild = false;
                break;
            }
        }
        if ( !uniqueChild ){
            throw new ModelException("Cannot add response - it is already a child of the parent");
        }
        
        //check that there isn't already a response for the same entry in the same sec occ
        boolean duplicate = false;
        for (Response r:this.responses){
        	if ( r.getEntry().equals(child.getEntry())){
        		if ( null != r.getSecOccInstance() && r.getSecOccInstance().equals(child.getSecOccInstance())){
        			duplicate = true;
        			break;
        		}
        		if ( null != r.getSectionOccurrence() && r.getSectionOccurrence().equals(child.getSectionOccurrence())){
        			duplicate = true;
        			break;
        		}
        	}
        }
        if ( duplicate ){
            throw new ModelException("Cannot add response - there is already a response for the same entry");
        }
        
    }
    
    @Override
    protected void addChildTasks(Record r) {
        for (Response resp: this.responses){
            resp.setRecord(r);
            resp.addChildTasks(r);
        }
    }

    @Override
    protected StatusedElement findElement() {
        return this.occurrence.getDocument();
    }
    
	/**
	 * Remove the single response associated with the specific entry
	 * and the specific section occurrence from the document instance's 
	 * collection of responses.
	 * 
	 * @param entry The entry that the response to remove
	 * is associated with.
	 * @param occurrence The section occurrence that the response to
	 * remove is associated with.
	 * @throws ModelException if the response
	 */
    public void removeResponse(Entry entry, SectionOccurrence occurrence) throws ModelException {
        Response resp = null;
        for (Response r:responses){
            if ( r.getEntry().equals(entry) && null != r.getSectionOccurrence() && r.getSectionOccurrence().equals(occurrence)){
                resp = r;
            }
        }
        if ( null != resp ){
	        responses.remove(resp);
	        if ( null != resp.getId() ){
	        	resp.detach();
	        	this.record.addDeletedObject(resp);
	        }
        }
    }

	/**
	 * Get the RBACAction used to control access to this document instance, as a
	 * string.
	 * 
	 * @return action
	 * @hibernate.property column="c_rbac_action"
	 */
	public String getAction() {
		return action;
	}
	
	public void setAction(String action) {
		this.action = action;
	}
	
	/**
	 * Set the RBACAction to control access to this document instance.
	 * 
	 * @param action
	 */
	public void setAction(RBACAction action) {
		if (action == null) {
			this.action = null;
		}
		else {
			this.action = action.toString();
		}
	}
    
	/**
	 * Get whether the document instance has been used to trigger 
	 * randomisation. Will be false if the option to randomised has been 
	 * turned down for this document instance.
	 * 
	 * Will be null if randomisation is not used or the option has not yet 
	 * been applied for this document instance.
	 * 
	 * @return isRandomised
	 * @hibernate.property column="c_is_randomised"
	 */
    public Boolean getIsRandomised() {
		return isRandomised;
	}

	/**
	 * Set whether the document instance has been used to trigger 
	 * randomisation. Will be false if the option to randomised has been 
	 * turned down for this document instance.
	 * 
	 * Will be null if randomisation is not used or the option has not yet 
	 * been applied for this document instance.
	 * @param isRandomised
	 */
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
	 * @hibernate.property column="c_can_edit_action"
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
	 * Set the RBACAction used for this editableAction. If present this will 
	 * indicate the users able to edit the document instance.
	 * 
	 * If null, it is assumed that the document instance is editable by any 
     * user who can access it, this is for backwards compatibility purposes.
	 * 
	 * @param editableAction
	 */
	public void setEditableAction(RBACAction editableAction) {
		if (editableAction == null) {
			this.editableAction = null;
		}
		else {
			this.editableAction = editableAction.toString();
		}
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
	
	public org.psygrid.data.model.dto.DocumentInstanceDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        org.psygrid.data.model.dto.DocumentInstanceDTO dtoDI = null;
        if ( dtoRefs.containsKey(this) ){
            dtoDI = (org.psygrid.data.model.dto.DocumentInstanceDTO)dtoRefs.get(this);
        }
        else{
            dtoDI = new org.psygrid.data.model.dto.DocumentInstanceDTO();
            dtoRefs.put(this, dtoDI);
            toDTO(dtoDI, dtoRefs, depth);
        }
        return dtoDI;
    }
    
    public void toDTO(org.psygrid.data.model.dto.DocumentInstanceDTO dtoDI, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        super.toDTO(dtoDI, dtoRefs, depth);
        
        if ( null != this.occurrence ){
            dtoDI.setOccurrenceId(this.occurrence.getId());
        }
        else {
        	dtoDI.setOccurrenceId(this.occurrenceId);
        }
        
        dtoDI.setAction(action);
        dtoDI.setIsRandomised(isRandomised);
        dtoDI.setEditableAction(editableAction);
        dtoDI.setEditingPermitted(editingPermitted);
        
        if ( RetrieveDepth.RS_SUMMARY != depth && RetrieveDepth.RS_DOC_ONLY != depth ){
            org.psygrid.data.model.dto.ResponseDTO[] dtoResponses = 
                new org.psygrid.data.model.dto.ResponseDTO[this.responses.size()];
            int i=0;
            for ( Response resp: responses ){
            	dtoResponses[i] = resp.toDTO(dtoRefs, depth);
            	i++;
            }
            dtoDI.setResponses(dtoResponses);
            
            org.psygrid.data.model.dto.SecOccInstanceDTO[] dtoSOIs = 
                new org.psygrid.data.model.dto.SecOccInstanceDTO[this.secOccInstances.size()];
            int j=0;
            for ( SecOccInstance soi: secOccInstances ){
                dtoSOIs[j] = soi.toDTO(dtoRefs, depth);
                j++;
            }
            dtoDI.setSecOccInstances(dtoSOIs);            

        }
    }

	/**
	 * Copy the data from the document instance into another document
	 * instance for the purposes of dual data entry.
	 * <p>
	 * Whilst the two document instances reference different document
	 * occurrences and documents it is assumed that the structure of these
	 * is identical i.e. the documents contain the same number of entries and
	 * at each index in the list of entries is found the same entry type.
	 * 
	 * @param secInst The document instance to copy the data to.
	 */
	public void ddeCopy(DocumentInstance ddeInst) {
		DocumentOccurrence primOcc = this.occurrence;
		Document primDoc = primOcc.getDocument();
		DocumentOccurrence secOcc = ddeInst.getOccurrence();
		Document secDoc = secOcc.getDocument();
		
		for ( int i=0, c=primDoc.numEntries(); i<c; i++ ){
			Entry primEntry = primDoc.getEntry(i);
			Entry secEntry = null;
			try{
				secEntry = secDoc.getEntry(i);
			}
			catch(IndexOutOfBoundsException ex){
				throw new RuntimeException("Fatal error during ddeCopy - mismatch between number of entries in primary and secondary. " +
						"No secondary entry for index = "+i+
						" [Primary document="+primOcc.getCombinedDisplayText()+"; Secondary document="+secOcc.getCombinedDisplayText()+"]", ex);
			}
			
			//check that the entries are the same type
			if ( !primEntry.getClass().equals(secEntry.getClass())){
				throw new RuntimeException("Fatal error during ddeCopy - entries are not the same type. " +
						"Primary entry is a "+primEntry.getClass().getSimpleName()+ " ("+primEntry.getDisplayText()+"); " +
						"Secondary entry is a "+secEntry.getClass().getSimpleName()+ " ("+secEntry.getDisplayText()+")"+
						" [Primary document="+primOcc.getCombinedDisplayText()+"; Secondary document="+secOcc.getCombinedDisplayText()+"]");
			}
			
			//skip narrative entries
			if ( primEntry instanceof NarrativeEntry){
				continue;
			}
			
			Section primSec = primEntry.getSection();
			Section secSec = secEntry.getSection();
			for ( int j=0, d=primSec.numOccurrences(); j<d; j++ ){
				SectionOccurrence primSecOcc = primSec.getOccurrence(j);
				SectionOccurrence secSecOcc = secSec.getOccurrence(j);
				
				if ( primSecOcc.isMultipleAllowed() ){
					//multiple runtime section occurrences are allowed
					for ( int k=0, e=this.numSecOccInstances(); k<e; k++ ){
						SecOccInstance primSecOccInst = this.getSecOccInstance(k);
						
						//see if the section occurrence instance for the secondary has already been
						//created - if not, create it
						SecOccInstance secSecOccInst = null;
						try{
							secSecOccInst = ddeInst.getSecOccInstance(k);
						}
						catch(ModelException ex){
							//no section occurrence available for this index, so create
							//a new one
							secSecOccInst = secSecOcc.generateInstance();
							ddeInst.addSecOccInstance(secSecOccInst);
						}
						
						Response primResp = this.getResponse(primEntry, primSecOccInst);
						if ( primResp instanceof BasicResponse){
							BasicResponse primBasResp = (BasicResponse)primResp;
							BasicResponse secBasResp = (BasicResponse)ddeInst.getResponse(secEntry, secSecOccInst);
							if ( null == secBasResp ){
								//no pre-existing response in secondary doc inst so generate one
								secBasResp = (BasicResponse)secEntry.generateInstance(secSecOccInst);
								ddeInst.addResponse(secBasResp);
							}
							IValue secValue = primBasResp.getValue().ddeCopy((BasicEntry)primEntry, (BasicEntry)secEntry);
							secBasResp.setValue(secValue);
						}
						else if ( primResp instanceof CompositeResponse){
							CompositeEntry primCompEntry = (CompositeEntry)primEntry;
							CompositeEntry secCompEntry = (CompositeEntry)secEntry;
							CompositeResponse primCompResp = (CompositeResponse)primResp;
							CompositeResponse secCompResp = (CompositeResponse)ddeInst.getResponse(secEntry, secSecOccInst);
							if ( null == secCompResp ){
								secCompResp = (CompositeResponse)secEntry.generateInstance(secSecOccInst);
								ddeInst.addResponse(secCompResp);
							}
							for ( int l=0, f=primCompResp.numCompositeRows(); l<f; l++ ){
								CompositeRow primRow = primCompResp.getCompositeRow(l);
								CompositeRow secRow = null;
								try{
									secRow = secCompResp.getCompositeRow(l);
								}
								catch(ModelException ex){
									secRow = secCompResp.createCompositeRow();
								}
								for ( int m=0, g=primCompEntry.numEntries(); m<g; m++ ){
									BasicEntry priCompBasEntry = primCompEntry.getEntry(m);
									BasicEntry secCompBasEntry = secCompEntry.getEntry(m);
									BasicResponse primBasResp = primRow.getResponse(priCompBasEntry);
									BasicResponse secBasResp = secRow.getResponse(secCompBasEntry);
									if ( null == secBasResp ){
										secBasResp = (BasicResponse)secCompBasEntry.generateInstance(secSecOccInst);
										secRow.addResponse(secBasResp);
									}
									IValue secValue = primBasResp.getValue().ddeCopy(priCompBasEntry, secCompBasEntry);
									secBasResp.setValue(secValue);
								}
							}
						}

					}
				}
				else{
					Response primResp = this.getResponse(primEntry, primSecOcc);
					if ( primResp instanceof BasicResponse){
						BasicResponse primBasResp = (BasicResponse)primResp;
						BasicResponse secBasResp = (BasicResponse)ddeInst.getResponse(secEntry, secSecOcc);
						if ( null == secBasResp ){
							try{
								secBasResp = (BasicResponse)secEntry.generateInstance(secSecOcc);
							}
							catch(ModelException ex){
								throw new RuntimeException("ModelException when trying to generate secondary section occurrence. [Primary document="
										+primOcc.getCombinedDisplayText()+"; Secondary document="+secOcc.getCombinedDisplayText()+
										"; Primary section="+primSecOcc.getCombinedDisplayText()+"; Secondary section="+secSecOcc.getCombinedDisplayText(),
										ex);
							}
							try{
								ddeInst.addResponse(secBasResp);
							}
							catch(NullPointerException npe){
								throw new RuntimeException("NPE when copying data for entry '"+secEntry.getDisplayText()+"'"+
										" [Primary document="+primOcc.getCombinedDisplayText()+"; Secondary document="+secOcc.getCombinedDisplayText()+"]", 
										npe);
							}
						}
						IValue secValue = primBasResp.getValue().ddeCopy((BasicEntry)primEntry, (BasicEntry)secEntry);
						secBasResp.setValue(secValue);
					}
					else if ( primResp instanceof CompositeResponse){
						CompositeEntry primCompEntry = (CompositeEntry)primEntry;
						CompositeEntry secCompEntry = (CompositeEntry)secEntry;
						CompositeResponse primCompResp = (CompositeResponse)primResp;
						CompositeResponse secCompResp = (CompositeResponse)ddeInst.getResponse(secEntry, secSecOcc);
						if ( null == secCompResp ){
							secCompResp = (CompositeResponse)secEntry.generateInstance(secSecOcc);
							ddeInst.addResponse(secCompResp);
						}
						for ( int k=0, e=primCompResp.numCompositeRows(); k<e; k++ ){
							CompositeRow primRow = primCompResp.getCompositeRow(k);
							CompositeRow secRow = null;
							try{
								secRow = secCompResp.getCompositeRow(k);
							}
							catch(ModelException ex){
								secRow = secCompResp.createCompositeRow();
							}
							for ( int l=0, f=primCompEntry.numEntries(); l<f; l++ ){
								BasicEntry priCompBasEntry = primCompEntry.getEntry(l);
								BasicEntry secCompBasEntry = secCompEntry.getEntry(l);
								BasicResponse primBasResp = primRow.getResponse(priCompBasEntry);
								BasicResponse secBasResp = secRow.getResponse(secCompBasEntry);
								if ( null == secBasResp ){
									secBasResp = (BasicResponse)secCompBasEntry.generateInstance(secSecOcc);
									secRow.addResponse(secBasResp);
								}								
								IValue secValue = primBasResp.getValue().ddeCopy(priCompBasEntry, secCompBasEntry);
								secBasResp.setValue(secValue);
							}
						}
					}
				}
			}
		}
	}
    
	/**
	 * Get the index of the SecOccInstance in the DocumentInstance's list of
	 * SecOccInstances.
	 * 
	 * @param secOccInst The SecOccInstance whose index is to be found.
	 * @return The index.
	 * @throws ModelException if the SecOccInstance isn't found.
	 */
	public int getIndexOfSecOccInstance(SecOccInstance secOccInst) throws ModelException {
		int index = secOccInstances.indexOf(secOccInst);
		if ( index < 0 ){
			throw new ModelException("SecOccInstance not found");
		}
		return index;
	}
	
	/**
	 * Store the current state of the responses in the
	 * document instance so that at the end of an editing
	 * session we may see what has changed.
	 *
	 */
	public void recordCurrentState(){
		for ( Response r: responses ){
			r.recordCurrentState();
		}
	}

	/**
	 * Examine the document instance to see what (if anything)
	 * was changed during an editing session.
	 * <p>
	 * For any changes found appropriate provenance is captured.
	 *
	 * @param change The ChangeHistory object that represents the changes
	 * made during this editing session.
	 */
	public void checkForChanges(ChangeHistory change) {
		for ( Response r: responses ){
			r.checkForChanges(change);
		}
	}
}

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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 * Class to represent a single row of a composite response.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_comp_rows"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class CompositeRow extends ElementInstance {

    /**
     * The collection of basic responses that make up the 
     * cells/columns of the row.
     */
    private Set<BasicResponse> basicResponses = new HashSet<BasicResponse>();

    /**
     * The composite response with which this row is associated with.
     */
    private CompositeResponse compositeResponse = null;
    
    /**
     * Get the collection of basic responses that make up the 
     * cells/columns of the row.
     * 
     * @return The collection of child BasicResponse objects
     * 
     * @hibernate.set cascade="all"
     * @hibernate.one-to-many class="org.psygrid.data.model.hibernate.BasicResponse"
     * @hibernate.key column="c_comp_resp_id" 
     *                not-null="false"
     */
    public Set<BasicResponse> getBasicResponses() {
        return basicResponses;
    }

    /**
     * Set the collection of basic responses that make up the 
     * cells/columns of the row.
     * 
     * @param basicResponses The collection of child BasicResponse objects
     */
    public void setBasicResponses(Set<BasicResponse> basicResponses) {
        this.basicResponses = basicResponses;
    }
    
    /**
     * Get the composite response which this composite row is
     * associated with.
     * 
     * @return The composite response.
     * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.CompositeResponse"
     *                        column="c_comp_resp_id"
     *                        not-null="false"
     *                        insert="false"
     *                        update="false"
     */
    public CompositeResponse getCompositeResponse() {
        return compositeResponse;
    }

    /**
     * Set the composite response with which this row is associated with.
     * 
     * @param compositeResponse The composite response.
     */
    public void setCompositeResponse(CompositeResponse compositeResponse) {
        this.compositeResponse = compositeResponse;
    }

    /**
     * Add a basic response to the composite row.
     * <p>
     * The basic response being added must reference one of the child
     * basic entries of the composite entry referenced by the
     * composite response that this composite row is associated with.
     * 
     * @param response The basic response to add.
     * @throws ModelException if it is not possible to add the basic
     * response.
     */
    public void addResponse(BasicResponse response) throws ModelException {
        BasicResponse br = (BasicResponse)response;
        checkNewInstance(br);
        addInstanceServer(br);
        propertyChangeSupport.firePropertyChange(null, null, null);
    }

    /**
     * Get the basic response associated with this composite row
     * that references the given entry. If no such a response exists,
     * then <code>null</code> is returned.
     * 
     * @param entry The entry to get the response for.
     * @return The basic response.
     */
    public BasicResponse getResponse(Entry entry) {
        BasicResponse basicResp = null;
        for (BasicResponse r:basicResponses){
            if ( r.getEntry().equals(entry) ){
                basicResp = r;
            }
        }
        return basicResp;
    }

    private void checkNewInstance(BasicResponse child) throws ModelException{
        //check that this is a valid child to add
        //i.e. the element that the child references must be
        //a child element of the element that this instance
        //references.
        boolean validChild = false;
        CompositeEntry ce = (CompositeEntry)compositeResponse.getEntry();
        for (BasicEntry be:ce.getEntries()){
            if (child.getEntry().equals(be)){
                validChild = true;
                break;
            }
        }
        if ( !validChild ){
            throw new ModelException("Cannot add response - it is not a valid child of the parent");
        }
        
        //check that the child is not a duplicate
        boolean uniqueChild = true;
        for (BasicResponse r:this.basicResponses){
            if (r.equals(child)){
                uniqueChild = false;
                break;
            }
        }
        if ( !uniqueChild ){
            throw new ModelException("Cannot add response - it is already a child of the parent");
        }
        
    }

    @Override
    protected void addChildTasks(Record r) {
        for (BasicResponse resp: this.basicResponses){
            resp.setRecord(r);
            resp.addChildTasks(r);
        }
    }
    
    /**
     * Method that does most of the work for adding a child to the
     * element instance, except for performing validation checks. It is
     * assumed that these check will have been performed when the child
     * was added by the client.
     * 
     * @param e The element instance to add as a child
     */
    public void addInstanceServer(BasicResponse resp) {
        resp.setRecord(this.record);
        //in case the children of this child have been added prior to
        //this point we must traverse the graph of children to ensure that
        //the references to the record are correct
        resp.addChildTasks(this.record);
        basicResponses.add(resp);
    }
    
    public org.psygrid.data.model.dto.CompositeRowDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        org.psygrid.data.model.dto.CompositeRowDTO dtoR = null;
        if ( dtoRefs.containsKey(this) ){
            dtoR = (org.psygrid.data.model.dto.CompositeRowDTO)dtoRefs.get(this);
        }
        else{
            dtoR = new org.psygrid.data.model.dto.CompositeRowDTO();
            dtoRefs.put(this, dtoR);
            toDTO(dtoR, dtoRefs, depth);
        }
        return dtoR;
    }
    
    public void toDTO(org.psygrid.data.model.dto.CompositeRowDTO dtoR, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        super.toDTO(dtoR, dtoRefs, depth);

        if ( RetrieveDepth.RS_SUMMARY != depth){
            org.psygrid.data.model.dto.BasicResponseDTO[] dtoResponses = 
                new org.psygrid.data.model.dto.BasicResponseDTO[this.basicResponses.size()];
            Iterator it = this.basicResponses.iterator();
            int counter = 0;
            while ( it.hasNext() ){
                BasicResponse resp = (BasicResponse)it.next();
                dtoResponses[counter] = resp.toDTO(dtoRefs, depth);
                counter++;
            }
            dtoR.setBasicResponses(dtoResponses);
            
            if ( null != this.compositeResponse ){
                dtoR.setCompositeResponse(this.compositeResponse.toDTO(dtoRefs, depth));
            }
        }
    }

    public void attach(Entry ent) {
        
        for (BasicResponse br: basicResponses){
            boolean attached = false;
            Long entryId = null;
            if ( null != br.getEntryId() ){
                entryId = br.getEntryId();
            }
            else{
                //preserve backwards compatability with records detached
                //prior to the introduction of BasicResponse.entryId
                entryId = br.getEntry().getId();
            }
            for (BasicEntry be:((CompositeEntry)ent).getEntries()){
                if ( entryId.equals(be.getId()) ){
                    br.attach(be);
                    br.setEntryId(null);
                    attached = true;
                    break;
                }
            }
            if ( !attached ){
                throw new ModelException("Failed to attach basic response id="+br.getId()+" - no entry exists with id="+entryId);
            }
        }
        
    }

    public void detach() {
        for (BasicResponse br: basicResponses){
            br.detach();
        }
    }

    @Override
    protected Element findElement() {
        return this.compositeResponse.findElement();
    }

	public void recordCurrentState() {
		for ( BasicResponse r: basicResponses ){
			r.recordCurrentState();
		}
	}

	public void checkForChanges(ChangeHistory change) {
		for ( BasicResponse r: basicResponses ){
			r.checkForChanges(change);
		}
	}

}

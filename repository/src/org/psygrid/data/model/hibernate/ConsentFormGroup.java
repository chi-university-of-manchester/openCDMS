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
 * Class to represent a group of consent forms.
 * <p>
 * The consent form group is used to allow all consent forms
 * in the dataset (that can be associated with an element at
 * any level of the hierarchy under a dataset object) to be
 * accessed from the dataset level.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_cons_form_groups"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class ConsentFormGroup extends Persistent {

    /**
     * Textual description of the consent form group.
     */
    private String description;
    
    /**
     * Collection of primary consent forms.
     * <p>
     * If multiple consent forms are associated with an element then 
     * they are intended to have an OR relation i.e. for an instance
     * of the element to be created in a record one of the
     * consent forms in the set must have been completed in the
     * positive.
     */
    private List<PrimaryConsentForm> consentForms = new ArrayList<PrimaryConsentForm>();
    
    /**
     * The dataset that the consent form group belongs to.
     */
    private DataSet dataSet;
    
    /**
     * If True, then this consent form group guards access to the ESL.
     * <p>
     * Consent must be present for the group in order to add the client
     * to the ESL, and if consent is withdrawn then the clients details
     * must be made inaccessible via normal ESL operation.
     */
    private boolean eslTrigger;
    
    /**
     * Get the textual description of the consent form group.
     * 
     * @return The textual description.
     * @hibernate.property column="c_description"
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Set the textual description of the consent form group.
     * 
     * @param description The textual description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the collection of primary consent forms.
     * 
     * @return The collection of primary consent forms.
     * 
     * @hibernate.list cascade="all" batch-size="100"
     * @hibernate.one-to-many class="org.psygrid.data.model.hibernate.PrimaryConsentForm"
     * @hibernate.key column="c_group_id"
     *                not-null="true"
     * @hibernate.list-index column="c_index"
     */
    public List<PrimaryConsentForm> getConsentForms() {
        return consentForms;
    }

    /**
     * Set the collection of primary consent forms.
     * 
     * @param consentForms The collection of primary consent forms.
     */
    public void setConsentForms(List<PrimaryConsentForm> consentForms) {
        this.consentForms = consentForms;
    }

    /**
     * Return the number of consent forms belonging to the group.
     * 
     * @return The number of consent forms
     */
    public int numConsentForms(){
        return this.consentForms.size();
    }
    
    /**
     * Add a single consent form to the group's collection
     * of consent forms.
     * 
     * @param form The consent form to add,
     * @throws ModelException if the consent form in the argument 
     * is <code>null</code>.
     */
    public void addConsentForm(PrimaryConsentForm form) throws ModelException{
        if ( null == form ){
            throw new ModelException("Cannot add a null consent form");
        }
        PrimaryConsentForm pcf = (PrimaryConsentForm)form;
        pcf.setGroup(this);
        consentForms.add(pcf);
    }
    
    /**
     * Retrieve a single consent form at the specified index
     * in the group's collection of consent forms.
     * 
     * @param index The index to retrieve the consent form from.
     * @return IConsentForm The consent form at the specified index
     * @throws ModelException if no consent form is found at the
     * specified index.
     */
    public PrimaryConsentForm getConsentForm(int index) throws ModelException{
        try{
            return consentForms.get(index);
        }
        catch(IndexOutOfBoundsException ex){
            throw new ModelException("No consent form found at index "+index);
        }
    }

    /**
     * Insert a new consent form into the group's collection of
     * consent forms.
     * 
     * @param form The new consent form to insert.
     * @param index The index to insert the consent form at.
     * @throws ModelException if the index is not valid, or if
     * the consent form in the argument is <code>null</code>.
     */
    public void insertConsentForm(PrimaryConsentForm form, int index) throws ModelException {
        if ( null == form ){
            throw new ModelException("Cannot insert a null consent form");
        }
        try{
            PrimaryConsentForm pcf = (PrimaryConsentForm)form;
            pcf.setGroup(this);
            consentForms.add(index, pcf);
        }
        catch(IndexOutOfBoundsException ex){
            throw new ModelException("Cannot insert consent form at index "+index+" - invalid index",ex);
        }
    }

    /**
     * Move a consent from from its current index in the group's
     * collection of consent forms to a new index.
     * 
     * @param currentIndex The index of the consent form to move.
     * @param newIndex The index to move the consent form to.
     * @throws ModelException if no consent form exists for the given
     * current index, or if the new index is not valid.
     */
    public void moveConsentForm(int currentIndex, int newIndex) throws ModelException {
        PrimaryConsentForm c = null;
        try{
            c = consentForms.remove(currentIndex);
        }
        catch(IndexOutOfBoundsException ex){
            throw new ModelException("No consent form found for index "+currentIndex, ex);
        }
        try{
            consentForms.add(newIndex, c);
        }
        catch(IndexOutOfBoundsException ex){
            //roll back - re-insert consent form to its old position
            consentForms.add(currentIndex, c);
            throw new ModelException("Cannot move consent form to index "+newIndex+" - invalid index", ex);
        }
    }

    /**
     * Remove a single consent form from the group's collection of
     * consent forms.
     * 
     * @param index The index of the consent form to remove.
     * @throws ModelException if no consent form exists for the given 
     * index.
     */
    public void removeConsentForm(int index) throws ModelException {
        try{
            PrimaryConsentForm pcf = consentForms.remove(index);
            if ( null != pcf.getId() ){
                //try to get a reference to the dataset
                try{
                    //consent form object being removed has already been
                    //persisted so add it to the list of objects to delete
                    this.getDataSet().getDeletedObjects().add(pcf);
                }
                catch(NullPointerException ex){
                    //do nothing - if the consent form group is not linked to a
                    //dataset then any consent forms that we remove
                    //can just be de-referenced
                }
            }
        }
        catch(IndexOutOfBoundsException ex){
            throw new ModelException("No consent form found for index "+index, ex);
        }
    }
    
    /**
     * Get the dataset that the consent form group is associated with.
     * 
     * @return The dataset.
     * 
     * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.DataSet"
     *                        column="c_dataset_id"
     *                        not-null="true"
     *                        insert="false"
     *                        update="false"
     */
    public DataSet getDataSet() {
        return this.dataSet;
    }

    /**
     * Set the element that the consent form group is associated with.
     * 
     * @param element The element.
     */
    public void setDataSet(DataSet dataSet) {
        this.dataSet = dataSet;
    }
    
    /**
     * Get the value of the ESL trigger flag.
     * <p>
     * If True, then this consent form group guards access to the ESL.
     * <p>
     * Consent must be present for the group in order to add the client
     * to the ESL, and if consent is withdrawn then the clients details
     * must be made inaccessible via normal ESL operation.
     * 
     * @return The value of the ESL trigger flag.
     * @hibernate.property column="c_esl_trigger"
     */
    public boolean isEslTrigger() {
		return eslTrigger;
	}

    /**
     * Set the value of the ESL trigger flag.
     * <p>
     * If True, then this consent form group guards access to the ESL.
     * <p>
     * Consent must be present for the group in order to add the client
     * to the ESL, and if consent is withdrawn then the clients details
     * must be made inaccessible via normal ESL operation.
     *
     * @param eslTrigger The value of the ESL trigger flag.
     */
	public void setEslTrigger(boolean eslTrigger) {
		this.eslTrigger = eslTrigger;
	}

	public org.psygrid.data.model.dto.ConsentFormGroupDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        //check for an already existing instance of a dto object for this 
        //consent form group in the map of references
        org.psygrid.data.model.dto.ConsentFormGroupDTO dtoCFG = null;
        if ( dtoRefs.containsKey(this)){
            dtoCFG = (org.psygrid.data.model.dto.ConsentFormGroupDTO)dtoRefs.get(this);
        }
        if ( null == dtoCFG ){
            //an instance of the consent form group has not already
            //been created, so create it, and add it to the map of 
            //references
            dtoCFG = new org.psygrid.data.model.dto.ConsentFormGroupDTO();
            dtoRefs.put(this, dtoCFG);
            toDTO(dtoCFG, dtoRefs, depth);
        }

        return dtoCFG;
    }
    
    public void toDTO(org.psygrid.data.model.dto.ConsentFormGroupDTO dtoCFG, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        super.toDTO(dtoCFG, dtoRefs, depth);
        dtoCFG.setDescription(this.description);
        dtoCFG.setEslTrigger(this.eslTrigger);
        org.psygrid.data.model.dto.PrimaryConsentFormDTO[] dtoCFs = 
            new org.psygrid.data.model.dto.PrimaryConsentFormDTO[this.consentForms.size()];
        for (int i=0; i<this.consentForms.size(); i++){
            PrimaryConsentForm cf = consentForms.get(i);
            dtoCFs[i] = cf.toDTO(dtoRefs, depth);
        }
        dtoCFG.setConsentForms(dtoCFs);
        if ( null != this.dataSet ){
            dtoCFG.setDataSet(this.dataSet.toDTO(dtoRefs, depth));
        }
    }
    
}

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
 * Class to represent a primary consent form.
 * <p>
 * A primary consent form is one which can have child
 * associated consent forms.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_prim_consent_forms"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class PrimaryConsentForm extends ConsentForm {

    /**
     * List of consent forms that are associated with this consent
     * form. These consent forms have a boolean AND relationship with
     * their parent.
     */
    private List<AssociatedConsentForm> associatedConsentForms = new ArrayList<AssociatedConsentForm>();
    
    /**
     * The consent form group that the consent form is a part of.
     */
    private ConsentFormGroup group;
    
    /**
     * Get the set of consent forms that are associated with this consent
     * form.
     * 
     * @return The set of associated consent forms
     * 
     * @hibernate.list cascade="all" batch-size="100"
     * @hibernate.one-to-many class="org.psygrid.data.model.hibernate.AssociatedConsentForm"
     * @hibernate.key column="c_cf_id"
     *                not-null="true"
     * @hibernate.list-index column="c_index"
     */
    public List<AssociatedConsentForm> getAssociatedConsentForms() {
        return associatedConsentForms;
    }

    /**
     * Set the set of consent forms that are associated with this consent
     * form.
     * 
     * @param associatedConsentForms The set of associated consent forms
     */
    protected void setAssociatedConsentForms(List<AssociatedConsentForm> associatedConsentForms) {
        this.associatedConsentForms = associatedConsentForms;
    }
    
    /**
     * Get the number of associated consent forms attached
     * to the consent form.
     * 
     * @return The number of associated consent forms.
     */
    public int numAssociatedConsentForms(){
        return associatedConsentForms.size();
    }
    
    /**
     * Add a single associated consent form to the consent form's 
     * collection of associated consent forms.
     * 
     * @param consentForm The associated consent form
     * @throws ModelException if the associated consent form in the 
     * argument is <code>null</code>.
     */
    public void addAssociatedConsentForm(AssociatedConsentForm consentForm)
            throws ModelException{
        if ( null == consentForm ){
            throw new ModelException("Cannot add a null associated consent form");
        }
        AssociatedConsentForm acf = (AssociatedConsentForm)consentForm;
        acf.setPrimaryConsentForm(this);
        associatedConsentForms.add(acf);
    }
    
    /**
     * Retrieve a single associated consent form at the specified
     * index in this consent form's collection of associated consent
     * forms.
     * 
     * @param position The index to retrieve the associated consent 
     * form for.
     * @return The associated consent form at the index specified
     * in the arguments.
     * @throws ModelException if no associated consent forms exists at
     * the specified index.
     */
    public AssociatedConsentForm getAssociatedConsentForm(int index)
            throws ModelException{
        try{
            return associatedConsentForms.get(index);
        }
        catch(IndexOutOfBoundsException ex){
            throw new ModelException("No associated consent form found for index "+index);
        }
    }
    
    /**
     * Insert a new associated consent form at the specified index
     * in this consent form's collection of associated consent
     * forms.
     * 
     * @param consentForm The new associated consent form to insert.
     * @param index The index at which to insert the new associated
     * consent form.
     * @throws ModelException if the specified index is not valid, or 
     * if the associated consent form in the argument is <code>null</code>.
     */
    public void insertAssociatedConsentForm(AssociatedConsentForm consentForm, int index) throws ModelException {
        if ( null == consentForm ){
            throw new ModelException("Cannot add a null associated consent form");
        }
        AssociatedConsentForm acf = (AssociatedConsentForm)consentForm;
        acf.setPrimaryConsentForm(this);
        try{
            associatedConsentForms.add(index, acf);
        }
        catch (IndexOutOfBoundsException ex){
            throw new ModelException("Cannot insert associated consent form at index "+index+" - invalid index", ex);
        }
    }

    /**
     * Move an existing associated consent form to a new index in the
     * consent form's collection of associated consent forms.
     * 
     * @param currentIndex The current index of the associated consent
     * form to move.
     * @param newIndex The index to move the associated consent form to.
     * @throws ModelException if no consent form exists for the given
     * current index, or is the new index is not valid.
     */
    public void moveAssociatedConsentForm(int currentIndex, int newIndex) throws ModelException {
        AssociatedConsentForm c = null;
        try{
            c = associatedConsentForms.remove(currentIndex);
        }
        catch(IndexOutOfBoundsException ex){
            throw new ModelException("No associated consent form found for index "+currentIndex, ex);
        }
        try{
            associatedConsentForms.add(newIndex, c);
        }
        catch(IndexOutOfBoundsException ex){
            //roll back - re-insert consent form to its old position
            associatedConsentForms.add(currentIndex, c);
            throw new ModelException("Cannot move associated consent form to index "+newIndex+" - invalid index", ex);
        }
    }

    /**
     * Remove a single associated consent form from the consent form's
     * collection of associated consent forms.
     * 
     * @param index The index of the associated consent form to remove.
     * @throws ModelException if no associated consent form exists at
     * the specified index.
     */
    public void removeAssociatedConsentForm(int index) throws ModelException {
        try{
            AssociatedConsentForm acf = associatedConsentForms.remove(index);
            if ( null != acf.getId() ){
                //try to get a reference to the dataset
                try{
                    //associated consent form object being removed has already been
                    //persisted so add it to the list of objects to delete
                    this.group.getDataSet().getDeletedObjects().add(acf);
                }
                catch(NullPointerException ex){
                    //do nothing - if the consent form is not linked to a
                    //dataset then any associated consent forms that we remove
                    //can just be de-referenced
                }
            }
        }
        catch (IndexOutOfBoundsException ex){
            throw new ModelException("No associated consent form found for index "+index, ex);
        }
    }

    /**
     * Get the consent form group that the consent form is a part of.
     * 
     * @return The consent form group.
     * 
     * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.ConsentFormGroup"
     *                        column="c_group_id"
     *                        not-null="true"
     *                        insert="false"
     *                        update="false"
     */
    public ConsentFormGroup getGroup() {
        return group;
    }

    public void setGroup(ConsentFormGroup group) {
        this.group = group;
    }

    protected DataSet findDataset() {
        DataSet ds = null;
        try{
            ds = this.group.getDataSet();
        }
        catch(NullPointerException ex){
            //do nothing - the method will then just return null
        }
        return ds;
    }

    @Override
    public PrimaryConsentForm getBasicCopy() {
        PrimaryConsentForm pcf = new PrimaryConsentForm();
        pcf.setId(this.getId());
        return pcf;
    }

    public org.psygrid.data.model.dto.PrimaryConsentFormDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        //check for an already existing instance of a dto object for this 
        //primary consent form in the map of references
        org.psygrid.data.model.dto.PrimaryConsentFormDTO dtoPCF = null;
        if ( dtoRefs.containsKey(this)){
            dtoPCF = (org.psygrid.data.model.dto.PrimaryConsentFormDTO)dtoRefs.get(this);
        }
        if ( null == dtoPCF ){
            //an instance of the primary consent form has not already
            //been created, so create it, and add it to the 
            //map of references
            dtoPCF = new org.psygrid.data.model.dto.PrimaryConsentFormDTO();
            dtoRefs.put(this, dtoPCF);
            toDTO(dtoPCF, dtoRefs, depth);
        }
        
        return dtoPCF;
    }
    
    public void toDTO(org.psygrid.data.model.dto.PrimaryConsentFormDTO dtoPCF, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        super.toDTO(dtoPCF, dtoRefs, depth);
        if ( RetrieveDepth.RS_COMPLETE != depth &&
                RetrieveDepth.RS_NO_BINARY != depth &&
                RetrieveDepth.RS_SUMMARY != depth ){
            org.psygrid.data.model.dto.AssociatedConsentFormDTO[] dtoACFs = 
                new org.psygrid.data.model.dto.AssociatedConsentFormDTO[this.associatedConsentForms.size()];
            for (int i=0; i<this.associatedConsentForms.size(); i++){
                AssociatedConsentForm cf = associatedConsentForms.get(i);
                dtoACFs[i] = cf.toDTO(dtoRefs, depth);
            }
            dtoPCF.setAssociatedConsentForms(dtoACFs);
            if ( null != this.group ){
                dtoPCF.setGroup(this.group.toDTO(dtoRefs, depth));
            }
        }
    }
}

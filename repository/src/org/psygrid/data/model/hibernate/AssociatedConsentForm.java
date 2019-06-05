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

import java.util.Map;

/**
 * Class to represent an associated consent form.
 * <p>
 * An associated consent form is one that forms an "AND"
 * relationship with a standard consent form. For instance,
 * consider a standard consent form with two associated 
 * consent forms. Consent for the standard consent form is
 * only valid if consent has also been obtained for both
 * of the associated consent forms.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_assoc_con_forms"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class AssociatedConsentForm extends ConsentForm {

    /**
     * The primary consent form that the associated consent form
     * is associated with.
     */
    private PrimaryConsentForm primaryConsentForm;
    
    /**
     * Get the primary consent form that the associated consent form
     * is associated with.
     * 
     * @return The primary consent form.
     * 
     * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.PrimaryConsentForm"
     *                        column="c_cf_id"
     *                        not-null="true"
     *                        insert="false"
     *                        update="false"
     */
    public PrimaryConsentForm getPrimaryConsentForm() {
        return primaryConsentForm;
    }

    /**
     * Set the primary consent form that the associated consent form
     * is associated with.
     * 
     * @param primaryConsentForm The primary consent form.
     */
    public void setPrimaryConsentForm(PrimaryConsentForm primaryConsentForm) {
        this.primaryConsentForm = primaryConsentForm;
    }

    @Override
    public AssociatedConsentForm getBasicCopy() {
        AssociatedConsentForm acf = new AssociatedConsentForm();
        acf.setId(this.getId());
        return acf;
    }

    protected DataSet findDataset() {
        DataSet ds = null;
        try{
            ds = this.primaryConsentForm.getGroup().getDataSet();
        }
        catch(NullPointerException ex){
            //do nothing - the method will then just return null
        }
        return ds;
    }

    public org.psygrid.data.model.dto.AssociatedConsentFormDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        org.psygrid.data.model.dto.AssociatedConsentFormDTO dtoACF = new org.psygrid.data.model.dto.AssociatedConsentFormDTO();
        toDTO(dtoACF, dtoRefs, depth);
        return dtoACF;
    }
    
    public void toDTO(org.psygrid.data.model.dto.AssociatedConsentFormDTO dtoACF, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        super.toDTO(dtoACF, dtoRefs, depth);
        if ( depth != RetrieveDepth.RS_COMPLETE &&
                depth != RetrieveDepth.RS_NO_BINARY &&
                depth != RetrieveDepth.RS_SUMMARY ){
            if ( null != this.primaryConsentForm ){
                dtoACF.setPrimaryConsentForm(this.primaryConsentForm.toDTO(dtoRefs, depth));
            }
        }
    }
}

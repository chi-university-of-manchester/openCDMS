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
 * Class to represent a completed consent form that is part of a Record.
 * 
 * @author Rob Harper
 * 
 * @hibernate.joined-subclass table="t_consents"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class Consent extends Provenanceable {

    private boolean readOnly;
    
    /**
     * Boolean to represent simply whether Consent has been
     * given or not. If True, consent has been given; if False,
     * consent has not been given.
     */
    private boolean consentGiven;
    
    /**
     * The location of the hard-copy of the signed consent form.
     */
    private String location;
    
    /**
     * Electronic copy of the signed consent form.
     */
    private BinaryObject consentDoc;
    
    /**
     * The consent form that this consent relates to
     */
    private ConsentForm consentForm;
    
    private Long consentFormId;
    
    /**
     * Default constructor. Property consentGiven set to False
     * by default.
     */
    public Consent(){
        this.consentGiven = false;
    }

    /**
     * Constructor that takes the ConsentForm that this is associated
     * with as an argument. The property consentGiven set to False
     * by default i.e. consent has not been given.
     * 
     * @param cf The ConsentForm that this Consent object is associated
     * with
     */
    public Consent(ConsentForm cf){
        this.consentGiven = false;
        this.consentForm = cf;
    }
    
    /**
     * Get the electronic copy of the signed consent form.
     * 
     * @return The electronic copy of the signed consent form.
     * 
     * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.BinaryObject"
     *                        column="c_e_doc"
     *                        not-null="false"
     */
    public BinaryObject getConsentDoc() {
        return consentDoc;
    }

    /**
     * Set the electronic copy of the signed consent form.
     * 
     * @param consentDoc The electronic copy of the signed 
     * consent form.
     */
    public void setConsentDoc(BinaryObject consentDoc) {
        if ( readOnly ){
            throw new ModelException("Cannot set consent doc - object is read only");
        }
        this.consentDoc = consentDoc;
    }

    /**
     * Get the boolean that represents whether consent has been 
     * given or not.
     * 
     * @return Boolean, True if consent has been given, False
     * otherwise.
     * 
     * @hibernate.property column="c_consent_given"
     */
    public boolean isConsentGiven() {
        return consentGiven;
    }

    /**
     * Set the boolean that represents whether consent has been 
     * given or not.
     * 
     * @param consentGiven Boolean, True if consent has been given, 
     * False otherwise.
     */
    public void setConsentGiven(boolean consentGiven) {
        if ( readOnly ){
            throw new ModelException("Cannot set consent given - object is read only");
        }
        this.consentGiven = consentGiven;
    }

    /**
     * Get the location of the hard-copy of the signed consent form.
     * 
     * @return The location of the hard-copy.
     * 
     * @hibernate.property column="c_location"
     */
    public String getLocation() {
        return location;
    }

    /**
     * Set the location of the hard-copy of the signed consent form.
     * 
     * @param location The location of the hard-copy.
     */
    public void setLocation(String location) {
        if ( readOnly ){
            throw new ModelException("Cannot set location - object is read only");
        }
        this.location = location;
    }

    /**
     * Get the consent form that this consent relates to.
     * 
     * @return The consent form
     * 
     * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.ConsentForm"
     *                        column="c_cons_form_id"
     *                        not-null="false"
     */
    public ConsentForm getConsentForm() {
        return consentForm;
    }

    /**
     * Set the consent form that this consent relates to.
     * 
     * @param consentForm The consent form.
     */
    public void setConsentForm(ConsentForm consentForm) {
        if ( readOnly ){
            throw new ModelException("Cannot set consent form - object is read only");
        }
        this.consentForm = consentForm;
    }

    public Long getConsentFormId() {
        return consentFormId;
    }

    public void setConsentFormId(Long consentFormId) {
        this.consentFormId = consentFormId;
    }

    /**
     * Get a basic copy of the consent, containing just the 
     * details of the consent and a reference to a basic 
     * copy of the consent form it is associated with.
     * 
     * @return The basic copy of the consent.
     */
    public Consent getBasicCopy() {
        Consent c = new Consent();
        c.setConsentGiven(this.consentGiven);
        c.setLocation(this.location);
        c.setId(this.getId());
        c.setConsentForm(this.consentForm.getBasicCopy());
        return c;
    }
        
    public org.psygrid.data.model.dto.ConsentDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        org.psygrid.data.model.dto.ConsentDTO dtoC = null;
        if ( dtoRefs.containsKey(this) ){
            dtoC = (org.psygrid.data.model.dto.ConsentDTO)dtoRefs.get(this);
        }
        else{
            dtoC = new org.psygrid.data.model.dto.ConsentDTO();
            dtoRefs.put(this, dtoC);
            toDTO(dtoC, dtoRefs, depth);
        }
        return dtoC;
    }
    
    public void toDTO(org.psygrid.data.model.dto.ConsentDTO dtoC, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        super.toDTO(dtoC, dtoRefs, depth);
        dtoC.setConsentGiven(this.consentGiven);
        dtoC.setLocation(this.location);
        if ( null != this.consentDoc ){
            dtoC.setConsentDoc(this.consentDoc.toDTO(dtoRefs, depth));
        }
        if ( null != this.consentForm ){
            dtoC.setConsentFormId(this.consentForm.getId());
        }
        else {
        	 dtoC.setConsentFormId(this.consentFormId);
        }
    }

    /**
     * Attach a detached consent to its dataset objects.
     * 
     * @param cf The consent form to attach the consent to.
     */
    public void attach(ConsentForm cf) {
        this.consentForm = cf;
        this.consentFormId = null;
    }

    public void detach(){
        if ( null != this.consentForm ){
            this.consentFormId = this.consentForm.getId();
            this.consentForm = null;
        }
    }

    public void lock() {
        readOnly = true;
    }

    public void unlock() {
        readOnly = false;
    }
}

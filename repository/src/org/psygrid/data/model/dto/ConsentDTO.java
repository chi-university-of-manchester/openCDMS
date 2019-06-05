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

import java.util.Map;


/**
 * Class to represent a completed consent form that is part of a Record.
 * 
 * @author Rob Harper
 */
public class ConsentDTO extends ProvenanceableDTO {

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
    private BinaryObjectDTO consentDoc;
    
    private Long consentFormId;
    
    /**
     * Default constructor. Property consentGiven set to False
     * by default.
     */
    public ConsentDTO(){
        this.consentGiven = false;
    }

    /**
     * Get the electronic copy of the signed consent form.
     * 
     * @return The electronic copy of the signed consent form.
     */
    public BinaryObjectDTO getConsentDoc() {
        return consentDoc;
    }

    /**
     * Set the electronic copy of the signed consent form.
     * 
     * @param consentDoc The electronic copy of the signed 
     * consent form.
     */
    public void setConsentDoc(BinaryObjectDTO consentDoc) {
        this.consentDoc = consentDoc;
    }
    
    /**
     * Get the boolean that represents whether consent has been 
     * given or not.
     * 
     * @return Boolean, True if consent has been given, False
     * otherwise.
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
        this.consentGiven = consentGiven;
    }

    /**
     * Get the location of the hard-copy of the signed consent form.
     * 
     * @return The location of the hard-copy.
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
        this.location = location;
    }

    public Long getConsentFormId() {
        return consentFormId;
    }

    public void setConsentFormId(Long consentFormId) {
        this.consentFormId = consentFormId;
    }

    public org.psygrid.data.model.hibernate.Consent toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        org.psygrid.data.model.hibernate.Consent hC = null;
        if ( hRefs.containsKey(this) ){
            hC = (org.psygrid.data.model.hibernate.Consent)hRefs.get(this);
        }
        else{
            hC = new org.psygrid.data.model.hibernate.Consent();
            hRefs.put(this, hC);
            toHibernate(hC, hRefs);
        }
        return hC;
    }
    
    public void toHibernate(org.psygrid.data.model.hibernate.Consent hC, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hC, hRefs);
        hC.setConsentGiven(this.consentGiven);
        hC.setLocation(this.location);
        if ( null != this.consentDoc ){
            hC.setConsentDoc(this.consentDoc.toHibernate(hRefs));
        }
        if ( null != this.consentFormId ){
            hC.setConsentFormId(this.consentFormId);
        }
    }
        
}

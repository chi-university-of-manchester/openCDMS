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


/**
 * Class to represent a primary consent form.
 * <p>
 * A primary consent form is one which can have child
 * associated consent forms.
 * 
 * @author Rob Harper
 *
 */
public class PrimaryConsentFormDTO extends ConsentFormDTO {

    /**
     * Collection of consent forms that are associated with this consent
     * form. These consent forms have a boolean AND relationship with
     * their parent.
     */
    private AssociatedConsentFormDTO[] associatedConsentForms = new AssociatedConsentFormDTO[0];
    
    private ConsentFormGroupDTO group;
    
    public PrimaryConsentFormDTO(){};
    
    /**
     * Get the collection of consent forms that are associated with this 
     * consent form.
     * 
     * @return The collection of associated consent forms.
     * 
     */
    public AssociatedConsentFormDTO[] getAssociatedConsentForms() {
        return associatedConsentForms;
    }

    /**
     * Set the collection of consent forms that are associated with this 
     * consent form.
     * 
     * @param associatedConsentForms The collection of associated consent forms.
     */
    public void setAssociatedConsentForms(AssociatedConsentFormDTO[] associatedConsentForms) {
        this.associatedConsentForms = associatedConsentForms;
    }
    
    public ConsentFormGroupDTO getGroup() {
        return group;
    }

    public void setGroup(ConsentFormGroupDTO group) {
        this.group = group;
    }

    public org.psygrid.data.model.hibernate.PrimaryConsentForm toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        //check for an already existing instance of a hibernate object for this 
        //primary consent form in the map of references
        org.psygrid.data.model.hibernate.PrimaryConsentForm hPCF = null;
        if ( hRefs.containsKey(this)){
            hPCF = (org.psygrid.data.model.hibernate.PrimaryConsentForm)hRefs.get(this);
        }
        if ( null == hPCF ){
            //an instance of the primary consent form has not already
            //been created, so create it, and add it to the
            //map of references
            hPCF = new org.psygrid.data.model.hibernate.PrimaryConsentForm();
            hRefs.put(this, hPCF);
            toHibernate(hPCF, hRefs);
        }
        
        return hPCF;
    }
    
    public void toHibernate(org.psygrid.data.model.hibernate.PrimaryConsentForm hPCF, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hPCF, hRefs);
        List<org.psygrid.data.model.hibernate.AssociatedConsentForm> hACFs = hPCF.getAssociatedConsentForms();
        for ( int i=0; i<this.associatedConsentForms.length; i++){
            AssociatedConsentFormDTO acf = this.associatedConsentForms[i];
            if ( null != acf ){
                hACFs.add(acf.toHibernate(hRefs));
            }
        }
        if ( null != this.group ){
            hPCF.setGroup(this.group.toHibernate(hRefs));
        }
    }

}

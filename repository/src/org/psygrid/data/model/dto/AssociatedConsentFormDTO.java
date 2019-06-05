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
 */
public class AssociatedConsentFormDTO extends ConsentFormDTO {

    private PrimaryConsentFormDTO primaryConsentForm;
    
    public AssociatedConsentFormDTO(){};

    public PrimaryConsentFormDTO getPrimaryConsentForm() {
        return primaryConsentForm;
    }

    public void setPrimaryConsentForm(PrimaryConsentFormDTO primaryConsentForm) {
        this.primaryConsentForm = primaryConsentForm;
    }

    public org.psygrid.data.model.hibernate.AssociatedConsentForm toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        org.psygrid.data.model.hibernate.AssociatedConsentForm hACF = new org.psygrid.data.model.hibernate.AssociatedConsentForm();
        toHibernate(hACF, hRefs);
        return hACF;
    }
    
    public void toHibernate(org.psygrid.data.model.hibernate.AssociatedConsentForm hACF, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hACF, hRefs);
        if ( null != this.primaryConsentForm ){
            hACF.setPrimaryConsentForm(this.primaryConsentForm.toHibernate(hRefs));
        }
    }
    
}

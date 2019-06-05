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
 * Class to represent a group of consent forms.
 * <p>
 * The consent form group is used to allow all consent forms
 * in the dataset (that can be associated with an element at
 * any level of the hierarchy under a dataset object) to be
 * accessed from the dataset level.
 * 
 * @author Rob Harper
 */
public class ConsentFormGroupDTO extends PersistentDTO {

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
    private PrimaryConsentFormDTO[] consentForms = new PrimaryConsentFormDTO[0];
    
    /**
     * The dataset that the consent form group belongs to.
     */
    private DataSetDTO dataSet;
    
    private boolean eslTrigger;
    
    public ConsentFormGroupDTO(){};
    
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the collection of primary consent forms.
     * 
     * @return The collection of primary consent forms.
     */
    public PrimaryConsentFormDTO[] getConsentForms() {
        return consentForms;
    }

    /**
     * Set the collection of primary consent forms.
     * 
     * @param consentForms The collection of primary consent forms.
     */
    public void setConsentForms(PrimaryConsentFormDTO[] consentForms) {
        this.consentForms = consentForms;
    }
    
    public DataSetDTO getDataSet() {
        return dataSet;
    }

    public void setDataSet(DataSetDTO dataSet) {
        this.dataSet = dataSet;
    }

    public boolean isEslTrigger() {
		return eslTrigger;
	}

	public void setEslTrigger(boolean eslTrigger) {
		this.eslTrigger = eslTrigger;
	}

	public org.psygrid.data.model.hibernate.ConsentFormGroup toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        //check for an already existing instance of a hibernate object for this 
        //consent form group in the map of references
        org.psygrid.data.model.hibernate.ConsentFormGroup hCFG = null;
        if ( hRefs.containsKey(this)){
            hCFG = (org.psygrid.data.model.hibernate.ConsentFormGroup)hRefs.get(this);
        }
        if ( null == hCFG ){
            //an instance of the consent form group has not already
            //been created, so create it, and add it to the map of 
            //references
            hCFG = new org.psygrid.data.model.hibernate.ConsentFormGroup();
            hRefs.put(this, hCFG);
            toHibernate(hCFG, hRefs);
        }

        return hCFG;
    }
    
    public void toHibernate(org.psygrid.data.model.hibernate.ConsentFormGroup hCFG, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hCFG, hRefs);
        hCFG.setDescription(this.description);
        hCFG.setEslTrigger(this.eslTrigger);
        List<org.psygrid.data.model.hibernate.PrimaryConsentForm> dtoCFs = 
            hCFG.getConsentForms();
        for (int i=0; i<this.consentForms.length; i++){
            PrimaryConsentFormDTO cf = consentForms[i];
            if ( null != cf ){
                dtoCFs.add(cf.toHibernate(hRefs));
            }
        }
        if ( null != this.dataSet ){
            hCFG.setDataSet(this.dataSet.toHibernate(hRefs));
        }
    }
    
}

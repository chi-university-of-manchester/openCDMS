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
 * Class to represent a consent form.
 * 
 * @author Rob Harper
 */
public abstract class ConsentFormDTO extends PersistentDTO {

    /**
     * Reference number for the consent form.
     * <p>
     * Could be used to link to paper copies of the consent form.
     */
    private String referenceNumber;
    
    /**
     * The question that will be displayed when asking a user
     * if they have consent
     */
    private String question;
    
    /**
     * Binary object that is a printable representation of the
     * consent form in a standard electronic document format.
     */
    private BinaryObjectDTO elecDoc;
    
    public ConsentFormDTO(){};
    
    /**
     * Get the binary object that is a printable representation of the
     * consent form in a standard electronic document format.
     * 
     * @return The electronic document
     */
    public BinaryObjectDTO getElecDoc() {
        return elecDoc;
    }

    /**
     * Set the binary object that is a printable representation of the
     * consent form in a standard electronic document format.
     * 
     * @param document The electronic document
     */
    public void setElecDoc(BinaryObjectDTO elecDoc) {
        this.elecDoc = elecDoc;
    }

    /**
     * Get the question that will be displayed when asking a user
     * if they have consent.
     * 
     * @return The question text
     */
    public String getQuestion() {
        return question;
    }

    /**
     * Set the question that will be displayed when asking a user
     * if they have consent.
     * 
     * @param question The question text
     */
    public void setQuestion(String question) {
        this.question = question;
    }

    /**
     * Get the reference number for the consent form.
     * 
     * @return The reference number
     */
    public String getReferenceNumber() {
        return referenceNumber;
    }

    /**
     * Set the reference number for the consent form.
     * 
     * @param referenceNumber The reference number
     */
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public abstract org.psygrid.data.model.hibernate.ConsentForm toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs);
    
    public void toHibernate(org.psygrid.data.model.hibernate.ConsentForm dtoCF, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(dtoCF, hRefs);
        dtoCF.setQuestion(this.question);
        dtoCF.setReferenceNumber(this.referenceNumber);
        if ( null != this.elecDoc ){
            dtoCF.setElecDoc(this.elecDoc.toHibernate(hRefs));
        }
    }
    
}

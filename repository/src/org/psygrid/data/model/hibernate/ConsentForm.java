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
 * Class to represent a consent form.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_consent_forms"
 * @hibernate.joined-subclass-key column="c_id"
 */
public abstract class ConsentForm extends Persistent {

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
    private BinaryObject elecDoc;
    
    /**
     * Standard JavaBean getter for the consent form electronic document.
     * 
     * @return The electronic document.
     * 
     * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.BinaryObject"
     *                        column="c_e_doc"
     *                        not-null="false"
     *                        cascade="all"
     */
    public BinaryObject getElecDoc() {
        return elecDoc;
    }

    /**
     * Standard JavaBean setter for the consent form electronic document.
     * 
     * @param elecDoc The electronic document.
     */
    public void setElecDoc(BinaryObject elecDoc) {
        this.elecDoc = elecDoc;
    }

    /**
     * Get the binary object that is a printable representation of the
     * consent form in a standard electronic document format.
     * 
     * @return The electronic document
     */
    public BinaryObject getElectronicDocument() {
        return elecDoc;
    }

    /**
     * Set the binary object that is a printable representation of the
     * consent form in a standard electronic document format.
     * 
     * @param document The electronic document
     */
    public void setElectronicDocument(BinaryObject electronicDocument) {
        BinaryObject oldDoc = this.elecDoc;
        BinaryObject newDoc = (BinaryObject)electronicDocument;
        if ( null != oldDoc && null != newDoc ){
            if ( !newDoc.equals(oldDoc) ){
                this.elecDoc = newDoc;
                if ( null != oldDoc.getId() ){
                    //binary object being replaced has already been
                    //persisted so add it to the list of objects to delete
                    findDataset().getDeletedObjects().add(oldDoc);
                }
            }
        }
        else if ( null != oldDoc && null == newDoc ){
            this.elecDoc = newDoc;
            if ( null != oldDoc.getId() ){
                //binary object being replaced has already been
                //persisted so add it to the list of objects to delete
                findDataset().getDeletedObjects().add(oldDoc);
            }
        }
        else if ( null == oldDoc && null != newDoc ){
            this.elecDoc = newDoc;
        }
    }

    /**
     * Get the question that will be displayed when asking a user
     * if they have consent.
     * 
     * @return The question text
     * 
     * @hibernate.property column="c_question" type="string" length="1024"
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
     * 
     * @hibernate.property column="c_ref_no"
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

    /**
     * Generate default consent reply(s) for this consent form and
     * its associated consent forms (if any).
     * <p>
     * The generated consent replies will default to consent not
     * given.
     * 
     * @return The generated consent replies.
     */
    public Consent generateConsent(){
        return new Consent(this);
    }

    protected abstract DataSet findDataset();

    /**
     * Get a basic copy of the consent form, containing only the
     * unique identifier of the consent form.
     * 
     * @return basic copy of the consent form.
     */
    public abstract ConsentForm getBasicCopy();
    
    public abstract org.psygrid.data.model.dto.ConsentFormDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth);
    
    public void toDTO(org.psygrid.data.model.dto.ConsentFormDTO dtoCF, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        super.toDTO(dtoCF, dtoRefs, depth);
        if ( depth != RetrieveDepth.RS_COMPLETE &&
             depth != RetrieveDepth.RS_NO_BINARY &&
             depth != RetrieveDepth.RS_SUMMARY ){
            dtoCF.setQuestion(this.question);
            dtoCF.setReferenceNumber(this.referenceNumber);
            if ( null != this.elecDoc ){
                dtoCF.setElecDoc(this.elecDoc.toDTO(dtoRefs, depth));
            }
        }
    }
}

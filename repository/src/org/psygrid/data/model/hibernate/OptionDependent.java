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
 * Class to represent the dependency of an entry or an occurrence
 * of a section on the selection of a specific option.
 * <p>
 * An OptionDependent object defines how the status of an entry is, or the
 * entries contained in a section occurrence are, affected when an option in 
 * an option entry is selected. This will facilitate assessment structures 
 * such as "If Yes go to question 3, if no go to question 6".
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_option_deps"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class OptionDependent extends Persistent {

    /**
     * The dependent entry.
     */
    private Entry myDependentEntry;
    
    /**
     * The dependent section occurrence.
     */
    private SectionOccurrence myDependentSecOcc;
    
    /**
     * The modified status for the dependent entry.
     */
    private EntryStatus entryStatus;
    
    /**
     * Get the dependent entry.
     * 
     * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.Entry"
     *                        column="c_entry_id"
     *                        unique="false"
     *                        not-null="false"
     *                        cascade="none"
     */
    public Entry getMyDependentEntry() {
        return myDependentEntry;
    }

    /**
     * Set the dependent entry.
     * 
     * @param dependentEntry The dependent entry.
     */
    public void setMyDependentEntry(Entry dependentEntry){
        this.myDependentEntry = dependentEntry;
    }
    
    /**
     * Get the dependent entry.
     * 
     * @return The dependent entry.
     */
    public Entry getDependentEntry(){
        return this.myDependentEntry;
    }
    
    /**
     * Set the dependent entry.
     * <p>
     * Note that if the option dependent already references a section occurrence
     * then the action of setting the dependent entry will cause this to be set
     * to <code>null</code>. An option dependent may only reference a dependent
     * entry or a dependent section occurrence, not both.
     * 
     * @param dependentEntry The dependent entry.
      */
    public void setDependentEntry(Entry dependentEntry) {
        this.myDependentSecOcc = null;
        this.myDependentEntry = (Entry)dependentEntry;
    }

    /**
     * Get the dependent section occurrence.
     * 
     * @return The dependent section occurrence.
     * 
     * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.SectionOccurrence"
     *                        column="c_sec_occ_id"
     *                        unique="false"
     *                        not-null="false"
     *                        cascade="none"
     */
    public SectionOccurrence getMyDependentSecOcc() {
        return myDependentSecOcc;
    }

    /**
     * Set the dependent section occurrence.
     * 
     * @param dependentSecOcc The dependent section occurrence.
     */
    public void setMyDependentSecOcc(SectionOccurrence dependentSecOcc) {
        this.myDependentSecOcc = dependentSecOcc;
    }

    /**
     * Get the dependent section occurrence.
     * 
     * @return The dependent section occurrence.
     */
    public SectionOccurrence getDependentSecOcc() {
        return this.myDependentSecOcc;
    }

    /**
     * Set the dependent section occurrence.
     * <p>
     * Note that if the option dependent already references an entry then the 
     * action of setting the dependent section occurrence will cause this to be set
     * to <code>null</code>. An option dependent may only reference a dependent
     * entry or a dependent section occurrence, not both.
     * 
     * @param dependentSecOcc The dependent section occurrence.
     */
    public void setDependentSecOcc(SectionOccurrence dependentSecOcc) {
        this.myDependentEntry = null;
        this.myDependentSecOcc = (SectionOccurrence)dependentSecOcc;
    }

    /**
     * Get the modified entry status for the dependent entry.
     * 
     * @return The entry status.
     */
    public EntryStatus getEntryStatus() {
        return this.entryStatus;
    }

    /**
     * Set the modified entry status for the dependent entry.
     * 
     * @param entryStatus The entry status.
     */
    public void setEntryStatus(EntryStatus entryStatus) {
        this.entryStatus = entryStatus;
    }

    /**
     * Get the string value of the enumerated entry status.
     * <p>
     * Only used by Hibernate to persist the string value of the enumerated
     * entry status.
     * 
     * @return The string value of the enumerated entry status
     * 
     * @hibernate.property column="c_entry_status"
     */
    protected String getEnumEntryStatus() {
        if ( null == entryStatus ){
            return null;
        }
        else{
            return entryStatus.toString();
        }
    }

    /**
     * Set the string value of the enumerated entry status.
     * <p>
     * Only used by Hibernate to un-persist the string value of the enumerated
     * entry status.
     * 
     * @param enumEntryStatus The string value of the enumerated entry status.
     */
    protected void setEnumEntryStatus(String enumEntryStatus) {
        if ( null == enumEntryStatus ){
            entryStatus = null;
        }
        else{
            entryStatus = EntryStatus.valueOf(enumEntryStatus);
        }
    }

    public org.psygrid.data.model.dto.OptionDependentDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        org.psygrid.data.model.dto.OptionDependentDTO dtoOD = new org.psygrid.data.model.dto.OptionDependentDTO();
        toDTO(dtoOD, dtoRefs, depth);
        return dtoOD;
    }

    public void toDTO(org.psygrid.data.model.dto.OptionDependentDTO dtoOD, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        super.toDTO(dtoOD, dtoRefs, depth);
        if ( null != this.entryStatus ){
            dtoOD.setEntryStatus(this.entryStatus.toString());
        }
        if ( null != this.myDependentEntry ){
            dtoOD.setMyDependentEntry(this.myDependentEntry.toDTO(dtoRefs, depth));
        }
        if ( null != this.myDependentSecOcc ){
            dtoOD.setMyDependentSecOcc(this.myDependentSecOcc.toDTO(dtoRefs, depth));
        }
    }
}
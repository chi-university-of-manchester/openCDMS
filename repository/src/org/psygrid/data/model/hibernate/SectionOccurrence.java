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
 * Class to represent an occurrence of a section.
 * <p>
 * Section occurrences are used when the same set of data
 * needs to be entered multiple times in the same document
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_sec_occs"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class SectionOccurrence extends Component {

    /**
     * The label for the occurrence.
     * <p>
     * The label is intended to be used for displaying the occurrence number 
     * of the section within its document.
     */
    protected String label;
    
    /**
     * The section that the section occurrence is associated with.
     */
    protected Section section;
    
    /**
     * Flag to mark whether entries in the section are optional or not.
     */
    protected EntryStatus entryStatus;
    
    /**
     * Flag to mark whether multiple instances of this occurrence
     * may be created at runtime.
     */
    protected boolean multipleAllowed;
    
    public SectionOccurrence(){}
    
    public SectionOccurrence(String name){
        super(name);
    }
    
    /**
     * Get the label for the occurrence.
     * <p>
     * The label is intended to be used for displaying the occurrence number 
     * of the section within its document.
     * 
     * @return The label.
     * @hibernate.property column="c_label"
     */
    public String getLabel() {
        return label;
    }

    /**
     * Set the label for the occurrence.
     * <p>
     * The label is intended to be used for displaying the occurrence number 
     * of the section within its document.
     * 
     * @param label The label.
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Get the section that the section occurrence is associated with.
     * 
     * @return The section.
     * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.Section"
     *                        column="c_section_id"
     *                        not-null="true"
     *                        insert="false"
     *                        update="false"
     */
    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    /**
     * Get the status of the section occurrence.
     * <p>
     * The status indicated whether entries contained by the section are 
     * mandatory, disabled, etc.
     * 
     * @return The status of the section occurrence.
     */
    public EntryStatus getEntryStatus() {
        return entryStatus;
    }

    /**
     * Set the status of the section occurrence.
     * <p>
     * The status indicated whether entries contained by the section are 
     * mandatory, disabled, etc.
     * 
     * @param entryStatus The status of the section occurrence.
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
        if ( null == this.entryStatus ){
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
    
    /**
     * Get the value of the flag that marks whether multiple instances of 
     * this occurrence may be created at runtime.
     * 
     * @return Multiple instance flag.
     * @hibernate.property column="c_multiple_allowed"
     */
    public boolean isMultipleAllowed() {
        return multipleAllowed;
    }

    /**
     * Set the value of the flag that marks whether multiple instances of 
     * this occurrence may be created at runtime.
     * 
     * @param allowMultiple Multiple instance flag.
     */
    public void setMultipleAllowed(boolean allowMultiple) {
        this.multipleAllowed = allowMultiple;
    }

    /**
     * Generate a new section occurrence instance associated with this
     * section occurrence
     * 
     * @return The new section occurrence instance.
     * @throws ModelException if the section occurence does not permit the
     * creation of multiple runtime instances.
     */
    public SecOccInstance generateInstance() throws ModelException {
        if ( !this.multipleAllowed ){
            throw new ModelException("This section occurrence does not "+
                    "permit the creation of multiple runtime instances.");
        }
        SecOccInstance soi = new SecOccInstance();
        soi.setSectionOccurrence(this);
        return soi;
    }
    
    /**
     * Get a string that is the combination of the section's
     * display text and the occurrence's display text, in the form
     * <section display text> - <sec occurrence display text>.
     *  
     * @return Combined section and occurrence display text.
     */
    public String getCombinedDisplayText() {
        StringBuilder builder = new StringBuilder();
        if ( null != this.section && null != this.section.getDisplayText() ){
            builder.append(this.section.getDisplayText());
            if ( null != this.displayText ){
                builder.append(" - ");
            }
        }
        if ( null != this.displayText ){
            builder.append(this.displayText);
        }
        return builder.toString();
    }

    /**
     * Get a string that is the combination of the section's
     * name and the occurrence's name, in the form
     * &lt;section name&gt; - &lt;sec occurrence name&gt;.
     *  
     * @return Combined section and occurrence name.
     */
    public String getCombinedName() {
        StringBuilder builder = new StringBuilder();
        if ( null == this.section || null == this.section.getName() ){
            builder.append("Unknown");
        }
        else{
            builder.append(this.section.getName());
        }
        builder.append(" (");
        if ( null == this.name ){
            builder.append("Unknown");
        }
        else{
            builder.append(this.name);
        }
        builder.append(")");
        return builder.toString();
    }

    @Override
    public org.psygrid.data.model.dto.SectionOccurrenceDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
        org.psygrid.data.model.dto.SectionOccurrenceDTO dtoSO = null;
        if ( dtoRefs.containsKey(this) ){
            dtoSO = (org.psygrid.data.model.dto.SectionOccurrenceDTO)dtoRefs.get(this);
        }
        else{
            dtoSO = new org.psygrid.data.model.dto.SectionOccurrenceDTO();
            dtoRefs.put(this, dtoSO);
            toDTO(dtoSO, dtoRefs, depth);
        }
        return dtoSO;
    }
    
    public void toDTO(org.psygrid.data.model.dto.SectionOccurrenceDTO dtoSO, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        super.toDTO(dtoSO, dtoRefs, depth);
        if ( depth != RetrieveDepth.REP_SAVE ){
            dtoSO.setLabel(this.label);
            dtoSO.setMultipleAllowed(this.multipleAllowed);
            if ( null != this.section ){
                dtoSO.setSection(this.section.toDTO(dtoRefs, depth));
            }
            if ( null != this.entryStatus ){
                dtoSO.setEntryStatus(this.entryStatus.toString());
            }
        }
    }
    
}

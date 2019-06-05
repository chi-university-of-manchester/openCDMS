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
 * Class to represent a runtime instance of a section occurrence.
 * <p>
 * Facilitates section occurrences for which multiple instance may
 * be created at runtime (i.e. at design time it is not known how
 * many section occurrences are required).
 * 
 * @author Rob Harper
 * 
 * @hibernate.joined-subclass table="t_sec_occ_insts"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class SecOccInstance extends Provenanceable {

    protected SectionOccurrence sectionOccurrence;
    
    protected Long sectionOccurrenceId;
    
    protected boolean deleted;
    
    /**
     * Get the section occurrence that this section occurrence instance is
     * a runtime instance of.
     * 
     * @return The section occurrence.
     * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.SectionOccurrence"
     *                        column="c_sec_occ_id"
     *                        not-null="false"
     *                        cascade="none"
     */
    public SectionOccurrence getSectionOccurrence() {
        return sectionOccurrence;
    }

    /**
     * Get the section occurrence that this section occurrence instance is
     * a runtime instance of.
     * 
     * @param sectionOccurrence The section occurrence.
     */
    public void setSectionOccurrence(SectionOccurrence sectionOccurrence) {
        this.sectionOccurrence = sectionOccurrence;
    }

    public Long getSectionOccurrenceId() {
        return sectionOccurrenceId;
    }

    public void setSectionOccurrenceId(Long sectionOccurrenceId) {
        this.sectionOccurrenceId = sectionOccurrenceId;
    }

    /**
     * @hibernate.property column="c_deleted"
     */
    public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public void attach(SectionOccurrence so){
        this.sectionOccurrence = so;
        this.sectionOccurrenceId = null;
    }
    
    public org.psygrid.data.model.dto.SecOccInstanceDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        org.psygrid.data.model.dto.SecOccInstanceDTO dtoSOI = null;
        if ( dtoRefs.containsKey(this) ){
            dtoSOI = (org.psygrid.data.model.dto.SecOccInstanceDTO)dtoRefs.get(this);
        }
        else{
            dtoSOI = new org.psygrid.data.model.dto.SecOccInstanceDTO();
            dtoRefs.put(this, dtoSOI);
            toDTO(dtoSOI, dtoRefs, depth);
        }
        return dtoSOI;        
    }

    public void toDTO(org.psygrid.data.model.dto.SecOccInstanceDTO dtoSOI, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        super.toDTO(dtoSOI, dtoRefs, depth);
        dtoSOI.setDeleted(deleted);
        if ( RetrieveDepth.RS_SUMMARY != depth){
            if ( null != this.sectionOccurrence){
                dtoSOI.setSectionOccurrenceId(this.sectionOccurrence.getId());
            }
            else {
            	dtoSOI.setSectionOccurrenceId(this.sectionOccurrenceId);
            }
        }
    }
    
    public void detach() throws ModelException {
        if ( null != this.sectionOccurrence ){
            this.sectionOccurrenceId = this.sectionOccurrence.getId();
            this.sectionOccurrence = null;
        }
    }
}

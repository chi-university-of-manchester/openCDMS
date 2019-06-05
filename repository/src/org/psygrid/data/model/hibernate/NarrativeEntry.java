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

import java.util.List;
import java.util.Map;

import org.psygrid.data.model.dto.ElementDTO;
import org.psygrid.data.query.IEntryStatement;
import org.psygrid.data.query.QueryOperation;
import org.psygrid.data.query.QueryStatementValue;

/**
 * Class to act as a placeholder for narrative text within a
 * document.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_narrative_entrys"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class NarrativeEntry extends Entry {

    /**
     * The rendering style for the narrative entry.
     */
    private NarrativeStyle style;
    
    public NarrativeEntry() {
    }

    public NarrativeEntry(String name, EntryStatus entryStatus) {
        super(name, entryStatus);
        style = NarrativeStyle.NORMAL;
    }

    public NarrativeEntry(String name, String displayText, EntryStatus entryStatus) {
        super(name, displayText, entryStatus);
        style = NarrativeStyle.NORMAL;
    }

    public NarrativeEntry(String name, String displayText) {
        super(name, displayText);
        style = NarrativeStyle.NORMAL;
    }

    public NarrativeEntry(String name) {
        super(name);
        style = NarrativeStyle.NORMAL;
    }

    /**
     * Get the rendering style for the narrative entry.
     * 
     * @return The rendering style.
     */
    public NarrativeStyle getStyle() {
        return style;
    }

    /**
     * Set the rendering style for the narrative entry.
     * 
     * @param style The rendering style.
     */
    public void setStyle(NarrativeStyle style) {
        this.style = style;
    }

    /**
     * Get the string value of the enumerated style.
     * <p>
     * Only used by Hibernate to persist the string value of the enumerated
     * style.
     * 
     * @return The string value of the enumerated style.
     * 
     * @hibernate.property column="c_style"
     */
    protected String getEnumStyle() {
        if ( null == style ){
            return null;
        }
        else{
            return style.toString();
        }
    }

    /**
     * Set the string value of the enumerated style.
     * <p>
     * Only used by Hibernate to un-persist the string value of the enumerated
     * style.
     * 
     * @param enumEntryStatus The string value of the enumerated style.
     */
    protected void setEnumStyle(String enumStyle) {
        if ( null == enumStyle ){
            style = null;
        }
        else{
            style = NarrativeStyle.valueOf(enumStyle);
        }
    }
    
    @Override
    public org.psygrid.data.model.dto.NarrativeEntryDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
        //check for an already existing instance of a dto object for this 
        //narrative entry in the map of references
        org.psygrid.data.model.dto.NarrativeEntryDTO dtoNE = null;
        if ( dtoRefs.containsKey(this)){
            dtoNE = (org.psygrid.data.model.dto.NarrativeEntryDTO)dtoRefs.get(this);
        }
        else{
            //an instance of the date entry has not already
            //been created, so create it, and add it to the map of 
            //references
            dtoNE = new org.psygrid.data.model.dto.NarrativeEntryDTO();
            dtoRefs.put(this, dtoNE);
            toDTO(dtoNE, dtoRefs, depth);
        }
        return dtoNE;
    }

    public void toDTO(org.psygrid.data.model.dto.NarrativeEntryDTO dtoNE, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        super.toDTO(dtoNE, dtoRefs, depth);
        if ( depth != RetrieveDepth.REP_SAVE ){
            if ( null != this.style ){
                dtoNE.setStyle(style.toString());
            }
        }
    }
    
    @Override
    protected void addChildTasks(DataSet ds) {
        //do nothing
    }

    public Response generateInstance(SectionOccurrence occurrence) throws ModelException {
        //it is not possible to create an instance of a narrative entry
        return null;
    }

    public Response generateInstance(SecOccInstance secOccInst) throws ModelException {
        //it is not possible to create an instance of a narrative entry
        return null;
    }

	public void applyStandardCode(DocumentInstance docInst, SectionOccurrence secOcc, SecOccInstance secOccInst, StandardCode stdCode) {
		//Do nothing - a narrative entry can never have a response
	}

	@Override
	public ElementDTO instantiateDTO() {
		return new org.psygrid.data.model.dto.NarrativeEntryDTO();
	}

	public List<QueryOperation> getQueryOperations() {
		// Should never be called
		return null;
	}

	@Override
	public boolean isQueryable() {
		return false;
	}

	public IEntryStatement createStatement(QueryStatementValue queryStatementValue) {
		// TODO: Currently, there is no NarrativeStatement that can be created.
		return null;
	}

}

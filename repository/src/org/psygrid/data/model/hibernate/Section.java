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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Class to represent a section of a document.
 * <p>
 * A section is used to group entries logically for display within
 * a document.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_sections"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class Section extends Component {

	/**
	 * The collection of occurrences for the section.
	 * <p>
	 * Each section must have at least one occurrence.
	 */
	private List<SectionOccurrence> occurrences = new ArrayList<SectionOccurrence>();

	public Section(){}

	public Section(String name){
		super(name);
	}

	public Section(String name, String displayText){
		super(name, displayText);
	}

	/**
	 * Get the collection of occurrences for the section.
	 * <p>
	 * Each section must have at least one occurrence.
	 * 
	 * @return The collection of occurrences.
	 * 
	 * @hibernate.list cascade="all" batch-size="100"
	 * @hibernate.one-to-many class="org.psygrid.data.model.hibernate.SectionOccurrence"
	 * @hibernate.key column="c_section_id"
	 *                not-null="true"
	 * @hibernate.list-index column="c_index"
	 */
	public List<SectionOccurrence> getOccurrences() {
		return occurrences;
	}

	/**
	 * Set the collection of occurrences for the section.
	 * <p>
	 * Each section must have at least one occurrence.
	 * 
	 * @param occurrences The collection of occurrences.
	 */
	public void setOccurrences(List<SectionOccurrence> occurrences) {
		this.occurrences = occurrences;
	}

    /**
     * Get the number of section occurrences associated with the
     * section.
     * 
     * @return The number of section occurrences.
     */
	public int numOccurrences() {
		return occurrences.size();
	}

    /**
     * Add a section occurrence to the sections collection of
     * occurrences.
     * 
     * @param occ The section occurrence to add.
     * @throws ModelException if the section occurrence being 
     * added is <code>null</code>.
     */
	public void addOccurrence(SectionOccurrence occ) throws ModelException {
		if ( null == occ ){
			throw new ModelException("Cannot add a null section occurrence");
		}
		SectionOccurrence so = (SectionOccurrence)occ;
		so.setSection(this);
		occurrences.add(so);
	}

    /**
     * Get a single section occurrence from the section's collection of
     * occurrences, at the given index.
     * 
     * @param index The index to get the occurrence from.
     * @return The section occurrence at the given index.
     * @throws ModelException if no section occurrence exists for the given
     * index.
     */
	public SectionOccurrence getOccurrence(int index) throws ModelException {
		try{
			return occurrences.get(index);
		}
		catch (IndexOutOfBoundsException ex){
			throw new ModelException("No section occurrence found for index "+index, ex);
		}
	}

	@Override
	public org.psygrid.data.model.dto.SectionDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
		//check for an already existing instance of a dto object for this 
		//section in the set of references
		org.psygrid.data.model.dto.SectionDTO dtoS = null;
		if ( dtoRefs.containsKey(this)){
			dtoS = (org.psygrid.data.model.dto.SectionDTO)dtoRefs.get(this);
		}
		else{
			//an instance of the section has not already
			//been created, so create it and add it to the map of references
			dtoS = new org.psygrid.data.model.dto.SectionDTO();
			dtoRefs.put(this, dtoS);
			toDTO(dtoS, dtoRefs, depth);
		}

		return dtoS;
	}

	public void toDTO(org.psygrid.data.model.dto.SectionDTO dtoS, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
		super.toDTO(dtoS, dtoRefs, depth);

		org.psygrid.data.model.dto.SectionOccurrenceDTO[] dtoOccs = new org.psygrid.data.model.dto.SectionOccurrenceDTO[this.occurrences.size()];
		for (int i=0; i<this.occurrences.size(); i++){
			SectionOccurrence so = occurrences.get(i);
			dtoOccs[i] = so.toDTO(dtoRefs, depth);
		}        
		dtoS.setOccurrences(dtoOccs);

	}

    /**
     * Get the index of a SectionOccurrence in the Section's list of SectionOccurrences
     * 
     * @param secOcc The SectionOccurrence whose index is to be found.
     * @return The index.
     * @throws ModelException If the SectionOccurrence is not found in the Section.
     */
	public int getIndexOfSectionOccurrence(SectionOccurrence secOcc) throws ModelException {
		int index = occurrences.indexOf(secOcc);
		if ( index < 0 ){
			throw new ModelException("SectionOccurrence not found");
		}
		return index;
	}

}

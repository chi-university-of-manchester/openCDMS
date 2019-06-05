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

public class SectionDTO extends ComponentDTO {

	/**
	 * The collection of occurrences for the section.
	 * <p>
	 * Each section must have at least one occurrence.
	 */
	private SectionOccurrenceDTO[] occurrences = new SectionOccurrenceDTO[0];

	public SectionOccurrenceDTO[] getOccurrences() {
		return occurrences;
	}

	public void setOccurrences(SectionOccurrenceDTO[] occurrences) {
		this.occurrences = occurrences;
	}

	@Override
	public org.psygrid.data.model.hibernate.Section toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs) {
		//check for an already existing instance of a hibernate object for this 
		//section in the set of references
		org.psygrid.data.model.hibernate.Section hS = null;
		if ( hRefs.containsKey(this)){
			hS = (org.psygrid.data.model.hibernate.Section)hRefs.get(this);
		}
		else{
			//an instance of the section has not already
			//been created, so create it and add it to the map of references
			hS = new org.psygrid.data.model.hibernate.Section();
			hRefs.put(this, hS);
			toHibernate(hS, hRefs);
		}

		return hS;

	}

	public void toHibernate(org.psygrid.data.model.hibernate.Section hS, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
		super.toHibernate(hS, hRefs);

		List<org.psygrid.data.model.hibernate.SectionOccurrence> hOccs = 
			hS.getOccurrences();
		for (int i=0; i<this.occurrences.length; i++){
			SectionOccurrenceDTO so = occurrences[i];
			if ( null != so ){
				hOccs.add(so.toHibernate(hRefs));
			}
		}    
	}

}
